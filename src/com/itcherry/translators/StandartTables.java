package com.itcherry.translators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public abstract class StandartTables {
    protected HashMap<String, Integer> keywords;            //Table of keywords
    protected HashMap<String, Integer> identificators;        //Table of identificators
    protected HashMap<Integer, String> errors;                //Table of errors
    protected HashMap<String, Integer> constants;            //Table of constants
    protected ArrayList<KnuthItem> syntaxTable;                //Table of grammatic for Knuth parser machine

    public StandartTables() {
        keywords = new HashMap<>();
        identificators = new HashMap<>();
        errors = new HashMap<>();
        constants = new HashMap<>();
        syntaxTable = new ArrayList<>();
        fillAttribute();
        fillKeywords();
        fillErrors();
        fillSyntax();
    }

    /* Initializing hashmap of keywords */
    private void fillKeywords() {
        keywords.put("PROGRAM", 701);
        keywords.put("CONST", 702);
        keywords.put("CASE", 703);
        keywords.put("OF", 704);
        keywords.put("ENDCASE", 705);
        keywords.put("BEGIN", 706);
        keywords.put("END", 707);
    }
    /* End keywords initializing */

    /* Initializing hashmap of errors */
    private void fillErrors() {
        errors.put(-100, "Error : forbidden symbol in line");
        errors.put(-99, "Error : *) expected but end of file found");
    }
    /* End errors initializing */


    /* Initializing hashmap of identificators */
    private static final int IDENTIFICATOR_MIN = 708;
    private static final int IDENTIFICATOR_MAX = 1000;
    protected int identificatorIterator = IDENTIFICATOR_MIN;

    protected void putIdentificator(String ident) {
        if (identificatorIterator <= IDENTIFICATOR_MAX) {
            identificators.put(ident, identificatorIterator);
            identificatorIterator++;
        } else {
            System.err.println("Too much identificators");
        }

    }

    protected int getIdentificator(String ident) {
        return identificators.get(ident);
    }
    /* End identificators initializing */

    /* Initializing hashmap of constants */
    private static final int CONST_MIN = 401;
    private static final int CONST_MAX = 500;
    public int constIterator = CONST_MIN;

    protected void putConst(String constant) {
        if (constIterator <= CONST_MAX) {
            constants.put(constant, constIterator);
            constIterator++;
        } else {
            System.err.println("Too much constants");
        }

    }

    protected int getConst(String constant) {
        return identificators.get(constant);
    }
    /* End constants initializing */

    /* Initializing array of attributes */
    public static final int WHITESPACE = 1;
    public static final int SINGLE_DELIMITER = 2;
    public static final int DIGIT = 4;
    public static final int LETTER = 5;
    public static final int COMMENT_START = 6;
    public static final int FORBIDDEN = 7;

    private static final int ATTRIBUTE_MIN = 0;
    private static final int ATTRIBUTE_MAX = 255;
    private static final int ATTRIBUTES_NUMBER = 256;

    public int[] attributes;

    private void fillAttribute() {
        attributes = new int[ATTRIBUTES_NUMBER];
        Arrays.fill(attributes, FORBIDDEN); // forbidden symbols
        attributes[10] = WHITESPACE; //whitespaces
        attributes[9] = WHITESPACE;
        attributes[11] = WHITESPACE;
        attributes[13] = WHITESPACE;
        attributes[32] = WHITESPACE; // space symbol
        attributes[61] = SINGLE_DELIMITER; // '='
        attributes[58] = SINGLE_DELIMITER; // ':'
        attributes[59] = SINGLE_DELIMITER; // ';'
        attributes[41] = SINGLE_DELIMITER; // ')'
        attributes[42] = SINGLE_DELIMITER; // '*'

        attributes[43] = SINGLE_DELIMITER; // '+'
        attributes[45] = SINGLE_DELIMITER; // '-'
        attributes[47] = SINGLE_DELIMITER; // '/'
        attributes[92] = SINGLE_DELIMITER; // '\'
        attributes[46] = SINGLE_DELIMITER; // '.'

        attributes[40] = COMMENT_START; // '('
        Arrays.fill(attributes, 48, 58, DIGIT); // '0', ..., '9'
        Arrays.fill(attributes, 65, 91, LETTER); // 'A', ...,'Z'

    }

    public int getAttribute(int ch) {
        int result = FORBIDDEN;

        if (ch >= ATTRIBUTE_MIN && ch <= ATTRIBUTE_MAX) {
            result = attributes[ch];
        }

        return result;
    }
	/* End attributes initializing */

    /**
     * Making knuth table
     */
    private void fillSyntax() {
        syntaxTable.add(new KnuthItem("<signal-program>  1", "<program>", 1, -1));
        syntaxTable.add(new KnuthItem("<program>  2", keywords.get("PROGRAM").toString(), 2, -1));
        syntaxTable.add(new KnuthItem(null, "<procedure-identifier>", 53, -1));
        syntaxTable.add(new KnuthItem(null, "59", 4, -1)); // ;
        syntaxTable.add(new KnuthItem(null, "<block>", 6, -1));
        syntaxTable.add(new KnuthItem(null, "46", -2, -1));// .
        syntaxTable.add(new KnuthItem("<block>  3", "<declarations>", 10, -1));
        syntaxTable.add(new KnuthItem(null, keywords.get("BEGIN").toString(), 8, -1));
        syntaxTable.add(new KnuthItem(null, "<statements-list>", 21, -1));
        syntaxTable.add(new KnuthItem(null, keywords.get("END").toString(), -2, -1));
        syntaxTable.add(new KnuthItem("<declarations>  4", "<constant-declarations>|", 11, -1));
        syntaxTable.add(new KnuthItem("<constant-declarations>  5", keywords.get("CONST").toString(), 12, 13));
        syntaxTable.add(new KnuthItem(null, "<constant-declarations-list>|", 14, -1));
        syntaxTable.add(new KnuthItem(null, "<empty>", -2, -2));
        syntaxTable.add(new KnuthItem("<constant-declarations-list>  6", "<constant-declaration>", 17, 16));
        syntaxTable.add(new KnuthItem(null, "<constant-declarations-list>|", 14, -1));
        syntaxTable.add(new KnuthItem(null, "<empty>", -2, -2));
        syntaxTable.add(new KnuthItem("<constant-declaration>  7", "<constant-identifier>", 54, -10));
        syntaxTable.add(new KnuthItem(null, "61", 19, -1)); // =
        syntaxTable.add(new KnuthItem(null, "<constant>", 55, -1));
        syntaxTable.add(new KnuthItem(null, "59", -2, -1)); // ;
        syntaxTable.add(new KnuthItem("<statements-list>  8", "<statement>", 24, 23));
        syntaxTable.add(new KnuthItem(null, "<statements-list>|", 21, -1));
        syntaxTable.add(new KnuthItem(null, "<empty>", -2, -2));
        syntaxTable.add(new KnuthItem("<statement>  9", keywords.get("CASE").toString(), 25, -10));
        syntaxTable.add(new KnuthItem(null, "<expression>", 38, -1));
        syntaxTable.add(new KnuthItem(null, keywords.get("OF").toString(), 27, -1));
        syntaxTable.add(new KnuthItem(null, "<alternatives-list>", 30, -1));
        syntaxTable.add(new KnuthItem(null, keywords.get("ENDCASE").toString(), 29, -1));
        syntaxTable.add(new KnuthItem(null, "59", -2, -1)); // ;
        syntaxTable.add(new KnuthItem("<alternatives-list> 10", "<alternative>", 33, 32));
        syntaxTable.add(new KnuthItem(null, "<alternatives-list>|", 30, -1));
        syntaxTable.add(new KnuthItem(null, "<empty>", -2, -2));
        syntaxTable.add(new KnuthItem("<alternative> 11", "<expression>", 38, -10));
        syntaxTable.add(new KnuthItem(null, "58", 35, -1)); // :
        syntaxTable.add(new KnuthItem(null, "47", 36, -1)); //   /
        syntaxTable.add(new KnuthItem(null, "<statements-list>", 21, -1));
        syntaxTable.add(new KnuthItem(null, "92", -2, -1)); // \
        syntaxTable.add(new KnuthItem("<expression> 12", "<summand>", 50, 40));
        syntaxTable.add(new KnuthItem(null, "<summands-list>|", 43, -1));
        syntaxTable.add(new KnuthItem(null, "45", 41, -10)); // -
        syntaxTable.add(new KnuthItem(null, "<summand>", 50, -1));
        syntaxTable.add(new KnuthItem(null, "<summands-list>|", 43, -1));
        syntaxTable.add(new KnuthItem("<summands-list> 13", "<add-instruction>", 47, 46));
        syntaxTable.add(new KnuthItem(null, "<summand>", 50, -1));
        syntaxTable.add(new KnuthItem(null, "<summands-list>|", 43, -1));
        syntaxTable.add(new KnuthItem(null, "<empty>", -2, -2));
        syntaxTable.add(new KnuthItem("<add-instruction> 14", "43", -2, 48));// +
        syntaxTable.add(new KnuthItem(null, "45", -2, 49)); // -
        syntaxTable.add(new KnuthItem(null,"42",-2,-10));
        syntaxTable.add(new KnuthItem("<summand> 15", "<variable-identifier>", 52, 51));
        syntaxTable.add(new KnuthItem(null, "<unsigned-integer>|", 56, 56));
        syntaxTable.add(new KnuthItem("<variable-identifier> 16", "SI", -2, -1));
        syntaxTable.add(new KnuthItem("<procedure-identifier> 17", "SI", -2, -1));
        syntaxTable.add(new KnuthItem("<constant-identifier> 18", "SI", -2, -1));
        syntaxTable.add(new KnuthItem("<constant> 20", "SC", -2, -1));
        syntaxTable.add(new KnuthItem("<unsigned-integer> 20", "SC", -2, -1));
    }
}
