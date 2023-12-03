package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Array extends CParseRule {
	//array ::= LBRA expression RBRA
	CParseRule expression;

	public Array(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_LBRA;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx); // [を読み飛ばす

		try {
			if (Expression.isFirst(tk)) {
				expression = new Expression(pcx);
				expression.parse(pcx);
			} else {
				pcx.recoverableError(tk.toExplainString() + "[の後ろはexpressionです");
			}
		} catch (RecoverableErrorException e) {
			if (tk.getType() != CToken.TK_RBRA) {
				ct.getNextToken(pcx);
			}
		}

		// Expressionは次の字句まで読んでしまう
		tk = ct.getCurrentToken(pcx); // ]が来るはず
		if (tk.getType() != CToken.TK_RBRA) {
            pcx.fatalError("[]が閉じていません");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (expression != null) {
			expression.semanticCheck(pcx);
			if (expression.getCType().getType() != CType.T_int) {
				pcx.warning("配列のインデックスはintである必要があります");
			}
			setCType(expression.getCType()); // expression の型をそのままコピー
			setConstant(false);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; Array starts");
		if (expression != null) {
			expression.codeGen(pcx);
		}
		o.println("\tMOV\t-(R6), R0\t; Array:インデックスを取得");
        o.println("\tADD\t-(R6), R0\t; Array:R0に基準アドレスを足して配列が表す番地を計算");
        o.println("\tMOV\tR0, (R6)+\t; Array:積む");
		o.println(";;; Array completes");
	}
}