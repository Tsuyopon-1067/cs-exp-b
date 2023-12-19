package lang.c.parse.statement;

import java.io.PrintStream;
import java.util.ArrayList;

import lang.*;
import lang.c.*;

public class StatementBlock extends CParseRule {
    // statementBlock ::= LCUR { statement } RCUR
	ArrayList<CParseRule> statmentList = new ArrayList<CParseRule>();

	public StatementBlock(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_LCUR;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx);

		while (Statement.isFirst(tk)) {
			Statement statement = new Statement(pcx);
			statmentList.add(statement);
			try {
				statement.parse(pcx);
			} catch (Exception e) {
				pcx.warning("StatementBlock: statementのエラーをスキップしました");
			}
			ct = pcx.getTokenizer();
			tk = ct.getCurrentToken(pcx);
		}

		try {
			if (tk.getType() != CToken.TK_RCUR) {
				pcx.recoverableError(tk.toExplainString() + "}が閉じていません");
			}
			ct.getNextToken(pcx); // ifは次の字句を読んでしまうのでそれに合わせる
		} catch (RecoverableErrorException e) {
		}
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
		o.println(";;; StatementBlock starts");
		if (statmentList != null) {
			for (CParseRule statment : statmentList) {
				statment.codeGen(pcx);
			}
		}
		o.println(";;; StatementBlock completes");
	}
}