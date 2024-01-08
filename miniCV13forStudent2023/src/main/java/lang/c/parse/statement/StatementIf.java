package lang.c.parse.statement;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.condition.ConditionBlock;

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

		try {
			if (ConditionBlock.isFirst(tk)) {
				condition = new ConditionBlock(pcx);
				condition.parse(pcx);
			} else {
				pcx.recoverableError(tk.toExplainString() + "ifの後ろはconditionBlockです");
			}
		} catch (RecoverableErrorException e) {
			ct.skipTo(pcx, CToken.TK_RPAR); // ConditionBlockの終わりまで飛ばす
		}
		tk = ct.getNextToken(pcx);

		if (Statement.isFirst(tk)) {
			statement1 = new Statement(pcx);
			statement1.parse(pcx);
		} else {
			pcx.recoverableError(tk.toExplainString() + "ifブロックの中はstatementです");
		}
		tk = ct.getCurrentToken(pcx); // statementは次の字句まで読んでしまう

		if (tk.getType() == CToken.TK_ELSE) {
			tk = ct.getNextToken(pcx);
			if (Statement.isFirst(tk)) {
				statement2 = new Statement(pcx);
				statement2.parse(pcx);
				tk = ct.getCurrentToken(pcx); // statementは次の字句まで読んでしまう
			} else {
				pcx.recoverableError(tk.toExplainString() + "elseブロックの中はstatementです");
			}
		}
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
		int seq = pcx.getSeqId();
		String endIfLabel = "ENDIF" + seq;
		String elseLabel = "ELSE" + seq;

		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; StatementIf starts");
		if (condition != null) {
			condition.codeGen(pcx);
		}
		o.println("\tMOV\t-(R6), R0\t;StatementIF: スタックからconditionの結果を持ってくる");
		if (statement2 == null) {
			// "elseがない場合"
			o.println(String.format("\tBRZ\t%s\t\t;StatementIF: false(Zフラグが立つ)ならENDIFに飛ぶ", endIfLabel));
		} else {
			// "elseがある場合"
			o.println(String.format("\tBRZ\t%s\t\t;StatementIF: false(Zフラグが立つ)ならELSEに飛ぶ", elseLabel));
		}
		if (statement1 != null) {
			statement1.codeGen(pcx);
		}
		if (statement2 != null) {
			// "elseがある場合
			o.println(String.format("\tJMP\t%s\t\t;StatementIF: ENDIFに飛ぶ", endIfLabel));
			o.println(String.format("%s:", elseLabel));
			statement2.codeGen(pcx);
		}
		o.println(String.format("%s:", endIfLabel));
		o.println(";;; StatementIf completes");
	}
}