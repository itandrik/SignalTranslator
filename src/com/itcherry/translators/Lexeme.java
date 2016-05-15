package com.itcherry.translators;

/**
 * Class, which describes 1 lexeme.
 * It was wrote for saving row and column.
 * They will use in errors
 */
public class Lexeme {
    int lexCode;
    int row;
    int col;

    /**
     *
     * @param lexCode - code of lexeme
     * @param row  - row of lexeme
     * @param col  - column of lexeme
     */
    public Lexeme(int lexCode, int row, int col) {
        this.lexCode = lexCode;
        this.row = row;
        this.col = col;
    }

    public int getLexCode() {
        return lexCode;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

}
