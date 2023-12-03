package lang.c.parse.statement;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.condition.ConditionBlock;

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
			statement = new Statement(pcx);
			statement.parse(pcx); // statememtは次の字句まで読んでしまう
		} else {
			pcx.fatalError(tk.toExplainString() + "whileブロックの中はstatementです");
		}
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
		int seq = pcx.getSeqId();
		String whileStartLabel = "WHILE" + seq;
		String whileEndLabel = "WHILEEND" + seq;
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; StatementWhile starts");
		if (condition != null) {
			o.println(String.format("%s:", whileStartLabel));
			condition.codeGen(pcx);
			o.println("\tMOV\t-(R6), R0\t;StatementWhile: スタックからconditionの結果を持ってくる");
			o.println(String.format("\tBRZ\t%s\t\t;StatementWhile: false(Zフラグが立つ)ならWHILEENDに飛ぶ", whileEndLabel));
		}
		if (statement != null) {
			statement.codeGen(pcx);
		}
		o.println(String.format("\tJMP\t%s\t\t;StatementWhile: WHILEに飛ぶ", whileStartLabel));
		o.println(String.format("%s:", whileEndLabel));
		o.println(";;; StatementWhile completes");
	}
}