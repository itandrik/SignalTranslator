package com.itcherry.translators;

import javax.swing.tree.DefaultTreeModel;
import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		LexicalAnalyser lexicalAnalyser = new LexicalAnalyser();
		lexicalAnalyser.analyse();

		SyntaxAnalyser syntaxAnalyser = new SyntaxAnalyser(lexicalAnalyser);
		DefaultTreeModel tree = syntaxAnalyser.analyse();

		CodeGenerator codeGenerator = new CodeGenerator(syntaxAnalyser.getResult());
		codeGenerator.generate();

		GUI frame = lexicalAnalyser.getFrame();
		frame.initTable(lexicalAnalyser.keywords,
				lexicalAnalyser.identificators,
				lexicalAnalyser.constants,
				lexicalAnalyser.getResult(),
				lexicalAnalyser.getSignalText(),
				tree,
				codeGenerator.getAsmTextString());
		frame.drawFrame();
	}

}
