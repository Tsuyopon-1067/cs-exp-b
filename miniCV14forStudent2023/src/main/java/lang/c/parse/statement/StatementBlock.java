package lang.c.parse.statement;

import java.io.PrintStream;
import java.util.ArrayList;

import lang.*;
import lang.c.*;
import lang.c.parse.FunctionInfo;

public class StatementBlock extends CParseRule {
    // statementBlock ::= LCUR { statement } RCUR
	ArrayList<CParseRule> statmentList = new ArrayList<CParseRule>();
	private FunctionInfo functionInfo;

	public StatementBlock(CParseContext pcx, FunctionInfo functionInfo) {
		this.functionInfo = functionInfo;
	}

	public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_LCUR;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx);

		while (Statement.isFirst(tk)) {
			Statement statement = new Statement(pcx, functionInfo);
			statmentList.add(statement);
			try {
				statement.parse(pcx);
			} catch (Exception e) {
				pcx.warning("StatementBlock: statementのエラーをスキップしました");
				ct.skipTo(pcx, CToken.TK_RCUR);
				return;
			}
			ct = pcx.getTokenizer();
			tk = ct.getCurrentToken(pcx);
		}

		if (tk.getType() != CToken.TK_RCUR) {
			pcx.warning("StatementBlock: " + tk.toExplainString() + "}が閉じていません");
			while (tk.getType() != CToken.TK_RCUR && tk.getType() != CToken.TK_EOF) {
				tk = ct.getNextToken(pcx);
			}
			if (tk.getType() == CToken.TK_RCUR) {
				tk = ct.getNextToken(pcx);
			}
			return;
		}
		ct.getNextToken(pcx); // ifは次の字句を読んでしまうのでそれに合わせる
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