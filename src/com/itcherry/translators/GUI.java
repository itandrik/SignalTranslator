package com.itcherry.translators;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class GUI extends JFrame {
    private JPanel panel1;

    private JScrollPane identifiersScroll;
    private JScrollPane SignalProgramScroll;
    private JTextArea syntaxText;
    private JTextArea signalText;
    private JTextArea asmText;
    private JScrollPane treeScroll;
    private JScrollPane keywordsScroll;
    private JScrollPane syntaxResultScroll;
    private JScrollPane separatorsScroll;
    private JScrollPane constantsScroll;
    private JScrollPane asmScroll;

    private JTree tree;
    private JTable identificators;
    private JTable constants;
    private JTable keywords;
    private JTable separators;
    private JPanel borderPane1;
    private JPanel borderPane2;
    private JPanel borderPane3;
    private JPanel borderPane4;
    private JPanel borderPane5;
    private JPanel borderPane6;
    private JPanel borderPane7;

    private JPanel borderPane8;

    private StringBuffer signalProgram;
    private String[][] keywordsFields;
    private String[][] identificatorsFields;
    private String[][] separatorsFields;
    private String[][] constantsFields;
    private String[] columnNames = {
            "Значення",
            "Код",
    };
    private ArrayList<Lexeme> result;
    private DefaultTreeModel treeModel;
    private String asmTextString;

    GUI() {
        super("ІПЗ1.ОПТ");
        setContentPane(panel1);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(1500, 900);
        this.setLocationRelativeTo(null);
    }

    public void initTable(HashMap<String, Integer> keywords, HashMap<String, Integer> identificators,
                          HashMap<String, Integer> constants, ArrayList<Lexeme> result,
                          StringBuffer signalProgram, DefaultTreeModel treeModel, StringBuffer asmText) {
        this.asmTextString = asmText.toString();
        this.result = result;
        this.signalProgram = signalProgram;
        signalProgram.deleteCharAt(signalProgram.length()-1);
        this.treeModel = treeModel;
        if (treeModel instanceof Error) {
            this.setVisible(false);
            JOptionPane.showMessageDialog(this, ((Error) treeModel).getErrorMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        keywordsFields = new String[keywords.size()][2];
        int i = 0;
        for (Map.Entry<String, Integer> pair : keywords.entrySet()) {
            keywordsFields[i][0] = pair.getKey();
            keywordsFields[i][1] = pair.getValue().toString();
            i++;
        }
        for (Map.Entry<String, Integer> entry : identificators.entrySet()) {
            entry.setValue(entry.getValue() >> 2);
        }
        identificatorsFields = new String[identificators.size()][2];
        i = 0;
        for (Map.Entry<String, Integer> pair : identificators.entrySet()) {
            identificatorsFields[i][0] = pair.getKey();
            identificatorsFields[i][1] = pair.getValue().toString();
            i++;
        }
        separatorsFields = new String[][]{
                {"=", "61"}, {":", "58"}, {";", "59"},
                {")", "41"}, {"+", "43"}, {"-", "45"},
                {"/", "47"}, {"\\", "92"}, {".", "46"},
                {"(", "40"}
        };
        constantsFields = new String[constants.size()][2];
        i = 0;
        if (constantsFields.length != 0)
            for (Map.Entry<String, Integer> pair : constants.entrySet()) {
                constantsFields[i][0] = pair.getKey();
                constantsFields[i][1] = pair.getValue().toString();
                i++;
            }
    }
    public File getOpenFilePath(){
        JFileChooser fileopen = new JFileChooser();
        fileopen.setCurrentDirectory(new File("C:\\Users\\Dron\\Desktop\\Study\\6_semester\\Транслятори\\lab1\\src\\res"));
        int ret = fileopen.showDialog(null, "Виберіть файл для трансляції");
        if (ret == JFileChooser.APPROVE_OPTION) {
            fileopen.approveSelection();
            return fileopen.getSelectedFile();
        }else return null;
    }

    public void drawFrame() {
        setVisible(true);
        identificators.setModel(new DefaultTableModel(identificatorsFields, columnNames));

        Color green = new Color(21, 149, 21);
        TitledBorder mtb1 = BorderFactory.createTitledBorder("Таблиця констант");
        mtb1.setTitleColor(green);
        borderPane1.setBorder(mtb1);
        TitledBorder mtb2 = BorderFactory.createTitledBorder("Таблиця роздільників");
        mtb2.setTitleColor(green);
        borderPane2.setBorder(mtb2);
        TitledBorder mtb3 = BorderFactory.createTitledBorder("Код програми SIGNAL");
        mtb3.setTitleColor(green);
        borderPane3.setBorder(mtb3);
        TitledBorder mtb4 = BorderFactory.createTitledBorder("Результат роботи лексичного аналізатора");
        mtb4.setTitleColor(green);
        borderPane4.setBorder(mtb4);
        TitledBorder mtb5 = BorderFactory.createTitledBorder("Таблиця ключових слів");
        mtb5.setTitleColor(green);
        borderPane5.setBorder(mtb5);
        TitledBorder mtb6 = BorderFactory.createTitledBorder("Дерево розбору");
        mtb6.setTitleColor(green);
        borderPane6.setBorder(mtb6);
        TitledBorder mtb7 = BorderFactory.createTitledBorder("Таблиця ідентифікаторів");
        mtb7.setTitleColor(green);
        borderPane7.setBorder(mtb7);

        borderPane8 = new JPanel(new BorderLayout());
        TitledBorder mtb8 = BorderFactory.createTitledBorder("Згенерований асемблерний код");
        mtb8.setTitleColor(green);
        borderPane8.setBorder(mtb8);
        asmText = new JTextArea();
        asmScroll = new JScrollPane(asmText);
        borderPane8.add(asmScroll);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.gridheight = 4;
        gbc.gridwidth = 5;
        gbc.ipadx = 200;

        gbc.fill = GridBagConstraints.BOTH;
        this.getContentPane().add(borderPane8,gbc);

        asmText.setText(asmTextString);

        constants.setModel(new DefaultTableModel(constantsFields, columnNames));

        keywords.setModel(new DefaultTableModel(keywordsFields, columnNames));

        separators.setModel(new DefaultTableModel(separatorsFields, columnNames));
        StringBuilder resultString = new StringBuilder();
        for(Lexeme i : result){
            resultString.append(i.getLexCode());
            resultString.append(" | ");
        }
        syntaxText.setText(resultString.toString());
        syntaxText.setLineWrap(true);
        syntaxText.setWrapStyleWord(true);
        syntaxText.setFont(new Font("Times New Roman", Font.PLAIN, 20));

        signalText.setText(signalProgram.toString());
        signalText.setWrapStyleWord(true);

        tree.setModel(treeModel);
    }

}
