package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayDeque;

import lang.*;
import lang.c.*;

public class Call extends CParseRule {
	// call            ::= LPAR RPAR
	CToken num;
	ArrayDeque<CParseRule> expressions;

	public Call(CParseContext pcx) {
		expressions = new ArrayDeque<CParseRule>();
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_LPAR;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx); // (を読み飛ばす

		if (Expression.isFirst(tk)) {
			expressions.addLast(new Expression(pcx));
			expressions.getLast().parse(pcx);
			tk = ct.getCurrentToken(pcx);
			while (tk.getType() == CToken.TK_COMMA) {
				tk = ct.getNextToken(pcx); // ,を読み飛ばす
				if (!Expression.isFirst(tk)) {
					pcx.recoverableError(tk.toExplainString() + ",の後ろには引数が必要です");
					continue;
				}
				expressions.addLast(new Expression(pcx));
				expressions.getLast().parse(pcx);
				tk = ct.getCurrentToken(pcx);
			}
		}

		if (tk.getType() != CToken.TK_RPAR) {
			pcx.recoverableError(tk.toDetailExplainString() + "Call: ()が閉じていません");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
	}
}
