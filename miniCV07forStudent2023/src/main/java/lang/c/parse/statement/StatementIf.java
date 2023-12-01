package lang.c.parse.statement;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.ConditionBlock;

public class StatementIf extends CParseRule {
    // statementIf ::= IF conditionBlock statement [ ELSE statement ]
	CParseRule condition, statement1, statement2;

	public StatementIf(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_IF;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx);

		if (ConditionBlock.isFirst(tk)) {
			condition = new ConditionBlock(pcx);
			condition.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "ifの後ろはconditionBlockです");
		}
		ct.getNextToken(pcx);

		if (Statement.isFirst(tk)) {
			statement1 = new StatementBlock(pcx);
			statement1.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "ifブロックの中はstatementBlockです");
		}
		ct.getNextToken(pcx);

		if (tk.getType() == CToken.TK_ELSE) {
			ct.getNextToken(pcx);
			if (Statement.isFirst(tk)) {
				statement2 = new StatementBlock(pcx);
				statement2.parse(pcx);
			} else {
				pcx.fatalError(tk.toExplainString() + "elseブロックの中はstatementBlockです");
			}
			ct.getNextToken(pcx);
		}

		if (tk.getType() == CToken.TK_ELSE) {
			pcx.fatalError(tk.toExplainString() + "elseは連続して使えません");
		} else if (tk.getType() != CToken.TK_ENDIF) {
			pcx.fatalError(tk.toExplainString() + "ifStatementの終わりはendifです");
		}
		ct.getNextToken(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (condition != null) {
			condition.semanticCheck(pcx);
		}
		if (statement1 != null) {
			statement1.semanticCheck(pcx);
		}
		if (statement2 != null) {
			statement2.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; StatementIf starts");
		if (condition != null) {
			condition.codeGen(pcx);
		}
		o.println(";;; StatementIf completes");
	}
}