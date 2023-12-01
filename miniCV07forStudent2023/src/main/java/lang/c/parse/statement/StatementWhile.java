package lang.c.parse.statement;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.ConditionBlock;

public class StatementWhile extends CParseRule {
    // statementWhile ::= WHILE conditionBlock statement
	CParseRule condition, statement;

	public StatementWhile(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_WHILE;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx);

		if (ConditionBlock.isFirst(tk)) {
			condition = new ConditionBlock(pcx);
			condition.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "whileの後ろはconditionBlockです");
		}
		tk = ct.getNextToken(pcx);

		if (Statement.isFirst(tk)) {
			statement = new StatementBlock(pcx);
			statement.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "whileブロックの中はstatementBlockです");
		}
		ct.getNextToken(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (condition != null) {
			condition.semanticCheck(pcx);
		}
		if (statement != null) {
			statement.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; StatementWhile starts");
		if (condition != null) {
			condition.codeGen(pcx);
		}
		o.println(";;; StatementWhile completes");
	}
}