package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Expression extends CParseRule {
	// expression ::= term { expressionAdd | expressionSub }
	CParseRule expression;

	public Expression(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return Term.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CParseRule term = null, list = null;
		term = new Term(pcx);
		term.parse(pcx);
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx); // termでは次の字句まで読んでしまう
		while (ExpressionAdd.isFirst(tk) || ExpressionSub.isFirst(tk)) {
			if (ExpressionAdd.isFirst(tk)) {
				list = new ExpressionAdd(pcx, term);
			} else {
				list = new ExpressionSub(pcx, term);
			}

			try {
				list.parse(pcx);
			} catch (RecoverableErrorException e) {
				int lineNo = tk.getLineNo();
				while (!ExpressionAdd.isFirst(tk) || ExpressionSub.isFirst(tk) || tk.getType() != CToken.TK_SEMI) {
					tk = ct.getNextToken(pcx);
					if (tk.getType() != CToken.TK_SEMI || tk.getLineNo() != lineNo) {
						break;
					}
				}
				pcx.warning("ExpressionAddまたはExpressionSubをスキップしました");
				continue;
			}
			term = list;
			tk = ct.getCurrentToken(pcx);
		}
		// この時点でexpressionはexperssionAddかexpressionSubの次の字句まで読んでいる
		expression = term;
		tk = ct.getCurrentToken(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (expression != null) {
			expression.semanticCheck(pcx);
			this.setCType(expression.getCType()); // expression の型をそのままコピー
			this.setConstant(expression.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; expression starts");
		if (expression != null) {
			expression.codeGen(pcx);
		}
		o.println(";;; expression completes");
	}
}
