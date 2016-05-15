package com.itcherry.translators;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Chernysh Andrii, NTUU "KPI", FAM, KV-31
 *         date : 03-Mar-2016
 *         Class analyzer is first part of translator language signal.
 *         This class is responsible for lexical analyze some code.
 *         Result of analyzing we put into ArrayList<Integer> result.
 *         Method getInfo starts graphical interface to show all tables and result
 */
class LexicalAnalyser extends StandartTables {
    private ArrayList<Lexeme> result;
    private GUI frame;
    private int row;
    private int col;
    private FileInputStream fileInputStream;
    private boolean eof;
    private char ch;
    private StringBuffer buf;
    private int lexCode;
    private boolean supressOutput;
    private StringBuffer signalProgram;
    private File file;

    /**
     * Method responsible for initializing some file from storage
     */
    private void initFile() {
        row = 1;
        col = 0;
        signalProgram = new StringBuffer();
        frame = new GUI();
        file = frame.getOpenFilePath();
        //File file = new File("src/res/" + fileName);
        try {
            fileInputStream = new FileInputStream(file);
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
    }

    /**
     * @return 1 symbol from file
     */
    private char getChar() {
        char current = (char) 0;
        try {
            current = (char) fileInputStream.read();
            signalProgram.append(current);
            col++;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return current;
    }

    /**
     * Closing the opened file
     */
    private void closeFile() {
        try {
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Identifying keyword or identificator
     */
    private void processLetter() {
        while (!eof
                && (attributes[(int) ch] == StandartTables.DIGIT || attributes[(int) ch] == StandartTables.LETTER)) {
            buf.append(ch);
            ch = getChar();
        }
        if (keywords.containsKey(buf.toString())) {
            lexCode = keywords.get(buf.toString());
        } else if (identificators.containsKey(buf.toString())) {
            lexCode = getIdentificator(buf.toString());
        } else {
            putIdentificator(buf.toString());
            lexCode = getIdentificator(buf.toString());
        }
    }

    /**
     * Identifying constants
     */
    private void processDigit() {
        while (!eof && (attributes[(int) ch] == StandartTables.DIGIT)) {
            buf.append(ch);
            ch = getChar();
        }
        if (constants.containsKey(buf.toString())) {
            lexCode = constants.get(buf.toString());
        } else {
            putConst(buf.toString());
            lexCode = constants.get(buf.toString());
        }
    }

    /**
     * Identifying whitespaces
     */
    private void processWhitespaces() {
        while (!eof && (attributes[(int) ch] == StandartTables.WHITESPACE)) {
            if (ch == '\n') {
                row++;
                col = 0;
            }
            ch = getChar();
        }
        supressOutput = true;
    }

    /**
     * @throws IOException Handle with commentaries
     */
    private void processComment() throws IOException {
        eof = fileInputStream.available() <= 0;
        if (eof)
            lexCode = (int) '(';
        else {
            ch = getChar();
            if (ch == '*') {
                if (eof) {
                    lexCode = -99; // Error : *) expected but end of file found
                } else {
                    ch = getChar();
                    do {
                        while (fileInputStream.available() > 0 && ch != '*') {
                            ch = getChar();
                        }
                        if (fileInputStream.available() <= 0) {
                            lexCode = -99; // Error : *) expected but end of
                            // file found
                            return;
                        } else
                            ch = getChar();
                    } while (ch != ')');
                }
                supressOutput = true;
            } else
                lexCode = (int) '(';
        }
        ch = getChar();


    }

    /**
     * Identifying delimiters
     */
    private void processDelimiter() {
        lexCode = (int) ch;
        ch = getChar();
    }

    /**
     * Identifying forbidden symbols
     */
    private void processForbidden() {
        lexCode = -100; // Error : forbidden symbol in line
        ch = getChar();
    }

    /**
     * @throws IOException Analyzing some file and get result array
     */
    void analyse() throws IOException {
        initFile();
        result = new ArrayList<>();
        eof = fileInputStream.available() <= 0;
        if (eof) {
            System.out.println("Empty file!!");
            return;
        }

        buf = new StringBuffer();
        lexCode = 0;
        ch = getChar();
        do {
            supressOutput = false;
            switch (attributes[(int) ch]) {
                case StandartTables.DIGIT:
                    processDigit();
                    break;
                case StandartTables.COMMENT_START:
                    processComment();
                    break;
                case StandartTables.FORBIDDEN:
                    processForbidden();
                    break;
                case StandartTables.LETTER:
                    processLetter();
                    break;
                case StandartTables.SINGLE_DELIMITER:
                    processDelimiter();
                    break;
                case StandartTables.WHITESPACE:
                    processWhitespaces();
                    break;
                default:
                    break;
            }
            buf.delete(0, buf.length());
            if (!supressOutput)
                result.add(new Lexeme(lexCode, row, col));
            eof = fileInputStream.available() <= 0;
        } while (((int) ch != 65535));
        closeFile();
    }

    /**
     *
     * @return result array of lexical analyser
     */
    public ArrayList<Lexeme> getResult(){
        return  result;
    }

    /**
     *
     * @return graphical user interface
     */
    public GUI getFrame(){
        return frame;
    }

    /**
     *
     * @return signal text from file
     */
    public StringBuffer getSignalText(){
        return signalProgram;
    }

}
