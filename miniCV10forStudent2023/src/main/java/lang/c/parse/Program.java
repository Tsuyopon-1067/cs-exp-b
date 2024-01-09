package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.ArrayList;

import lang.*;
import lang.c.*;
import lang.c.parse.statement.Statement;

public class Program extends CParseRule {
	// program     ::= { declaration } { statement } EOF
	ArrayDeque<CParseRule> declaration = new ArrayDeque<CParseRule>();
	ArrayDeque<CParseRule> statment = new ArrayDeque<CParseRule>();

	public Program(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return Declaration.isFirst(tk) || Statement.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

		while (Declaration.isFirst(tk)) {
			declaration.addLast(new Declaration(pcx));
			declaration.getLast().parse(pcx);
			tk = ct.getNextToken(pcx);
		}
		while (Statement.isFirst(tk)) {
			statment.addLast(new Statement(pcx));
			statment.getLast().parse(pcx);
			tk = ct.getCurrentToken(pcx); // statementが次の字句を読んでしまうので次の字句は読まない
		}
		if (tk.getType() == CToken.TK_RCUR) {
			pcx.warning(tk.toDetailExplainString() + "}が閉じていないので補いました");
		}
		if (tk.getType() != CToken.TK_EOF) {
			pcx.fatalError(tk.toExplainString() + "プログラムの最後にゴミがあります");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		for (CParseRule s : statment) {
			s.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; program starts");
		o.println("\t. = 0x100");
		o.println("\tJMP\t__START\t; ProgramNode: 最初の実行文へ");
		// ここには将来、宣言に対するコード生成が必要
		if (declaration != null) {
			for (CParseRule d : declaration) {
				d.codeGen(pcx);
			}
		}
		o.println("__START:");
		o.println("\tMOV\t#0x1000, R6\t; ProgramNode: 計算用スタック初期化");
		for (CParseRule s : statment) {
			s.codeGen(pcx);
		}
		//o.println("\tMOV\t-(R6), R0\t; ProgramNode: 計算結果確認用");
		o.println("\tHLT\t\t\t; ProgramNode:");
		o.println("\t.END\t\t\t; ProgramNode:");
		o.println(";;; program completes");
	}
}