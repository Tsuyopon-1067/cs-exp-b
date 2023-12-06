package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import lang.c.parse.statement.Statement;

import lang.*;
import lang.c.*;

public class DeclBlock extends CParseRule {
    // declblock ::= LCUR { declaration } { statement } RCUR
	ArrayDeque<CParseRule> declareList = new ArrayDeque()<CParseRule>();
	ArrayDeque<CParseRule> statmentList = new ArrayDeque<CParseRule>();

	public DeclBlock(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_LCUR;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		pcx.getSymbolTable().setupLocalSymbolTable();
        CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx); // {を読み飛ばす

		while (Declaration.isFirst(tk)) {
			declareList.add(new Declaration(pcx));
			declareList.getFirst().parse(pcx);
			ct = pcx.getTokenizer();
			tk = ct.getCurrentToken(pcx);
		}

		while (Statement.isFirst(tk)) {
			statmentList.add(new Statement(pcx));
			statmentList.getFirst().parse(pcx);
			ct = pcx.getTokenizer();
			tk = ct.getCurrentToken(pcx);
		}

		try {
			if (tk.getType() != CToken.TK_RCUR) {
				pcx.recoverableError(tk.toExplainString() + "declblockの後ろは}です");
			}
			ct.getNextToken(pcx); // ifは次の字句を読んでしまうのでそれに合わせる
		} catch (RecoverableErrorException e) {
		}
		pcx.getSymbolTable().deleteLocalSymbolTable();
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (statmentList != null) {
			for (CParseRule statment : statmentList) {
				statment.semanticCheck(pcx);
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; DeclBlock starts");
		if (statmentList != null) {
			for (CParseRule statment : statmentList) {
				statment.codeGen(pcx);
			}
		}
		o.println(";;; DeclBlock completes");
	}
}