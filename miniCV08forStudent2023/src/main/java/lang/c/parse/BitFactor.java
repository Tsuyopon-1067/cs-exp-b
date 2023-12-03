package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.condition.Condition;

public class BitFactor extends CParseRule {
	// bitFactor       ::= condition | LT bitExpression GT | bitNotFactor // 不等号で囲む (LPAR expression RPAR と区別したい)
	CParseRule nexParseRule;

	public BitFactor(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return Condition.isFirst(tk) || tk.getType() == CToken.TK_LT || BitNotFactor.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if (Condition.isFirst(tk)) {
			nexParseRule = new Condition(pcx);
			nexParseRule.parse(pcx);
		} else if (tk.getType() == CToken.TK_LT) {
			tk = ct.getNextToken(pcx); // '<'の次の字句を読む
			if (!BitExpression.isFirst(tk)) {
				pcx.fatalError(tk.toExplainString() + "<の後ろはbitExpressionです");
			}
			nexParseRule = new BitExpression(pcx);
			nexParseRule.parse(pcx);
			tk = ct.getCurrentToken(pcx);
			if (tk.getType() != CToken.TK_GT) {
				pcx.fatalError(tk.toExplainString() + ">が閉じていません");
			}
			ct.getNextToken(pcx); // '>'の次の字句を読む
		} else if (BitNotFactor.isFirst(tk)) {
			nexParseRule = new BitNotFactor(pcx);
			nexParseRule.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (nexParseRule != null) {
			nexParseRule.semanticCheck(pcx);
			System.out.println(nexParseRule.getClass().getSimpleName());
			setCType(nexParseRule.getCType()); // 型をそのままコピー
			setConstant(nexParseRule.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; bitFactor starts");
		if (nexParseRule != null) {
			nexParseRule.codeGen(pcx);
		}
		o.println(";;; bitFactor completes");
	}
}