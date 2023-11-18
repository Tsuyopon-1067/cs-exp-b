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
		expression = new Expression(pcx);
		expression.parse(pcx);
		tk = ct.getNextToken(pcx); // ]が来るはず
		if (tk.getType() != CToken.TK_RBRA) {
            pcx.fatalError("[]が閉じていません");
		}
		ct.getNextToken(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (expression != null) {
			expression.semanticCheck(pcx);
			if (expression.getCType().getType() != CType.T_int) {
				pcx.fatalError("配列のインデックスはintである必要があります");
			}
			setCType(expression.getCType()); // expression の型をそのままコピー
			setConstant(expression.isConstant()); // expression は常に定数
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; Array starts");
		if (expression != null) {
			expression.codeGen(pcx);
		}
		o.println("\tMOV\t-(R6), R0\t; Array:");
        o.println("\tADD\t-(R6), R0\t; Array:配列が表す番地を計算");
        o.println("\tMOV\tR0, (R6)+\t; Array:積む");
		o.println(";;; Array completes");
	}
}