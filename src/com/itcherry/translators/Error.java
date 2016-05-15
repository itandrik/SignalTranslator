package com.itcherry.translators;


import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

/**
 * Class, which describes error
 * We use it, when our syntax analyser get first problem
 */
public class Error extends DefaultTreeModel{
    private String errorMessage;
    private int row;
    private int col;

    public String getErrorMessage() {
        return errorMessage;
    }

    public Error(String errorMessage, int row, int col, TreeNode root) {
        super(root);
        this.errorMessage = errorMessage;
        this.errorMessage += ("\n Row : " + row + "\n Column : " + col);
        this.row = row;
        this.col = col;
    }
}
