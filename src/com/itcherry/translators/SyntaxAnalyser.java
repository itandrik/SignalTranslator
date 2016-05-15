package com.itcherry.translators;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.Map;
import java.util.Stack;

/**
 * Class, which describes syntax analysis
 * We make tree in this class
 */
public class SyntaxAnalyser{

    private LexicalAnalyser lexicalAnalyser;
    private DefaultMutableTreeNode result;
    private DefaultTreeModel resultTreeModel;

    public SyntaxAnalyser(LexicalAnalyser lexicalAnalyser) {
        this.lexicalAnalyser = lexicalAnalyser;
    }

    /**
     * Scanning hashmap. Find key by value. Its needed by tree
     *
     * @param value - value for hashmap, where we will find key(String for tree)
     * @return String with key
     */
    private String getKeyToValue(int value) {
        if (value > 39 && value < 100) {
            return String.valueOf((char) value);
        } else if (value > 400 && value <= 500) {
            for (Map.Entry<String, Integer> entry : lexicalAnalyser.constants.entrySet()) {
                if (entry.getValue().equals(value)) return entry.getKey();
            }
        } else if (value > 700 && value <= 707) {
            for (Map.Entry<String, Integer> entry : lexicalAnalyser.keywords.entrySet()) {
                if (entry.getValue().equals(value)) return entry.getKey();
            }
        } else if (value > 707 && value <= 10000) {
            for (Map.Entry<String, Integer> entry : lexicalAnalyser.identificators.entrySet()) {
                if (entry.getValue().equals(value)) return entry.getKey();
            }
        }
        return "Error";
    }

    /**
     * Find node, which has no such name, as previous node have
     *
     * @param node : its a start node, from which we will go up
     * @return highest node with different parent's name
     */
    private DefaultMutableTreeNode findNode(DefaultMutableTreeNode node) {
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
        while (parentNode.getUserObject().equals(node.getUserObject())) {
            parentNode = (DefaultMutableTreeNode) parentNode.getParent();
            node = (DefaultMutableTreeNode) node.getParent();
        }
        return node;
    }

    private String trimNodeName(Object nodeName) {
        String result = (String) nodeName;
        result = result.substring(0, result.lastIndexOf('>') + 1);
        return result;
    }

    /**
     * Knuth parser(modified by Marchenko O.I)
     * It get identifiers, keywords, constants and errors tables. Also it get lexical analyser result
     * It parse input code and verify it. Also it builds a tree.
     *
     * @return model of tree, which GUI will build
     */
    public DefaultTreeModel analyse() {
        boolean flag = true; // AT or AF
        boolean identifiersFlag = false; // Before BEGIN and after
        Stack<Integer> stack = new Stack<>();   //Stack, which contains position of iterator in the Knuth table
        Stack<DefaultMutableTreeNode> nodeStack = new Stack<>();    //Stack, which contains useful tree nodes
        int i = 0;                              //iterator of Knuth table
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(); //Node of the tree
        for (Lexeme lex :lexicalAnalyser.getResult()) {             //Main cycle, goes for lexeme vector
            try {
                if (lex.getLexCode() < 0)       //Lexical errors
                    return new Error(lexicalAnalyser.errors.get(lex.getLexCode()), lex.getRow(), lex.getCol(), treeNode.getRoot());
                if (lex.getLexCode() == 706) identifiersFlag = true;        //After BEGIN
                do {
                    while (lexicalAnalyser.syntaxTable.get(i).getOperationCode().startsWith("<")) { //Non-Terminals
                        if (!lexicalAnalyser.syntaxTable.get(i).getOperationCode().equals("<empty>")) {
                            if (!lexicalAnalyser.syntaxTable.get(i).getOperationCode().endsWith("|") && flag)
                                stack.push(i);
                            if (flag) {
                                if (lexicalAnalyser.syntaxTable.get(i).getOperationAddress() != null) { // Make new node
                                    treeNode.add(new DefaultMutableTreeNode(
                                            lexicalAnalyser.syntaxTable.get(i).getOperationAddress()));
                                    treeNode = (DefaultMutableTreeNode) treeNode.getLastChild();
                                    nodeStack.push(treeNode);
                                }
                                i = lexicalAnalyser.syntaxTable.get(i).getAt();             //Goes to AT
                            } else {
                                i = lexicalAnalyser.syntaxTable.get(i).getAf();             //Goes to AF
                            }
                            if (i == -10) {
                                treeNode = (DefaultMutableTreeNode) treeNode.getParent();
                                i = stack.pop();
                            }
                        } else {                                                    //Empty grammar
                            i = stack.pop() + 1;
                            treeNode.removeAllChildren();
                            do {
                                treeNode = nodeStack.pop();                         //Return to upper node
                            }
                            while (!trimNodeName(treeNode.getUserObject()).equals(lexicalAnalyser.syntaxTable.get(i - 1).getOperationCode()));
                            treeNode = findNode(treeNode);
                            treeNode = (DefaultMutableTreeNode) treeNode.getParent();
                        }
                    }
                    if (lexicalAnalyser.syntaxTable.get(i).getOperationAddress() != null) {         //Non-Terminals. Its for tree
                        treeNode.add(new DefaultMutableTreeNode(
                                lexicalAnalyser.syntaxTable.get(i).getOperationAddress()));
                        treeNode = (DefaultMutableTreeNode) treeNode.getLastChild();
                    }
                    if (lexicalAnalyser.syntaxTable.get(i).getOperationCode().equals("SI")) {       //Search identifiers (51 - 53 in the Knuth table)
                        if (!identifiersFlag)
                            if (!lexicalAnalyser.identificators.containsValue(lex.getLexCode()) &&
                                    !lexicalAnalyser.identificators.containsValue(lex.getLexCode() << 2))
                                return new Error("Identifier expected", lex.getRow(), lex.getCol(), treeNode.getRoot());
                            else if (lexicalAnalyser.identificators.replace(getKeyToValue(lex.getLexCode()), lex.getLexCode() << 2) == null) {
                                return new Error("Override variable " + getKeyToValue(lex.getLexCode() << 2),
                                        lex.getRow(), lex.getCol(), treeNode.getRoot()); // Checking for variable rewriting
                            }
                        i = stack.pop() + 1;
                        if (lexicalAnalyser.identificators.containsValue(lex.getLexCode() << 2)) {      //If it is identifier
                            flag = true;
                            treeNode.add(new DefaultMutableTreeNode(getKeyToValue(lex.getLexCode() << 2)));
                            if (lexicalAnalyser.syntaxTable.get(i).getOperationCode().endsWith("|")) {
                                i = stack.pop() + 1; // for 49-50
                                treeNode = (DefaultMutableTreeNode) treeNode.getParent();
                            }
                            treeNode = (DefaultMutableTreeNode) treeNode.getParent();
                        } else {                                                        //If not identifier
                            if (lexicalAnalyser.identificators.containsValue(lex.getLexCode()))
                                return new Error("Identifier not defined", lex.getRow(), lex.getCol(), treeNode.getRoot());
                            if (!lexicalAnalyser.syntaxTable.get(i).getOperationCode().startsWith("<")) i -= 1;
                            DefaultMutableTreeNode temp = treeNode;
                            treeNode = (DefaultMutableTreeNode) treeNode.getParent();
                            temp.removeFromParent();
                            flag = false;
                        }
                    } else if (lexicalAnalyser.syntaxTable.get(i).getOperationCode().equals("SC")) {     //Search constants (54 - 55 in the Knuth table)
                        i = stack.pop() + 1;
                        if (lexicalAnalyser.constants.containsValue(lex.getLexCode())) {
                            flag = true;
                            treeNode.add(new DefaultMutableTreeNode(getKeyToValue(lex.getLexCode())));
                            treeNode = (DefaultMutableTreeNode) treeNode.getParent();
                            if (lexicalAnalyser.syntaxTable.get(i).getOperationCode().endsWith("|"))
                                treeNode = (DefaultMutableTreeNode) treeNode.getParent();
                        } else {
                            if (lexicalAnalyser.syntaxTable.get(i).getOperationCode().endsWith("|")) i -= 1;// for 49-50
                            treeNode = (DefaultMutableTreeNode) treeNode.getParent();
                            DefaultMutableTreeNode temp = treeNode;
                            treeNode = (DefaultMutableTreeNode) treeNode.getParent();
                            temp.removeFromParent();
                            flag = false;
                        }
                    } else if (lex.getLexCode() == Integer.parseInt(lexicalAnalyser.syntaxTable.get(i).getOperationCode())) { //Terminals processing
                        treeNode.add(new DefaultMutableTreeNode(getKeyToValue(lex.getLexCode())));
                        if (lexicalAnalyser.syntaxTable.get(i).getAt() == -2) {
                            i = stack.pop() + 1;
                            treeNode = (DefaultMutableTreeNode) treeNode.getParent();
                        } else i = lexicalAnalyser.syntaxTable.get(i).getAt();
                        flag = true;
                    } else if (lexicalAnalyser.syntaxTable.get(i).getAf() == -10) {                                         //Return to upper rules
                        flag = false;
                        if (i == 40) {
                            treeNode = (DefaultMutableTreeNode) treeNode.getParent();
                            i = stack.pop();
                        }
                        treeNode = (DefaultMutableTreeNode) treeNode.getParent();
                        i = stack.pop();
                    } else if (lexicalAnalyser.syntaxTable.get(i).getAf() == -1) {                  //Error processing
                        return new Error(getKeyToValue(Integer.parseInt(lexicalAnalyser.syntaxTable.get(i).getOperationCode())) + " expected!!!",
                                lex.getRow(), lex.getCol(), treeNode.getRoot());
                    } else {                                                        //Go to AF
                        i = lexicalAnalyser.syntaxTable.get(i).getAf();
                        flag = false;
                    }
                } while (!flag);
            } catch (NullPointerException e) {
                System.out.println("Check your code!!! NULL Pointer exception in treeNode");
                e.printStackTrace();
                return null;
            }
        }
        treeNode = (DefaultMutableTreeNode) treeNode.getRoot();
        result = treeNode.getNextNode();
        return new DefaultTreeModel(treeNode.getNextNode());
    }

    public DefaultMutableTreeNode getResult() {
        return result;
    }

}
