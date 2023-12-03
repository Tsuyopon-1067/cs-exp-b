package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class BitExpression extends CParseRule {
	// bitExpression  ::= bitTerm { OR bitTerm }
	CParseRule bitTerm;

	public BitExpression(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return BitTerm.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CParseRule term = null, list = null;
		term = new BitTerm(pcx);
		term.parse(pcx);
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx); // termでは次の字句まで読んでしまう
		while (BitExpressionOr.isFirst(tk)) {
			list = new BitExpressionOr(pcx, term);
			list.parse(pcx);
			term = list;
			tk = ct.getCurrentToken(pcx);
		}
		// この時点でexpressionはexperssionOrの次の字句まで読んでいる
		bitTerm = term;
		if (tk.getType() == CToken.TK_ILL) { // | を読んだ時を想定
			pcx.fatalError(tk.toExplainString() + "不正な字句です");
		}
		tk = ct.getCurrentToken(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (bitTerm != null) {
			bitTerm.semanticCheck(pcx);
			this.setCType(bitTerm.getCType()); // bitTerm の型をそのままコピー
			this.setConstant(bitTerm.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; bitExpression starts");
		if (bitTerm != null) {
			bitTerm.codeGen(pcx);
		}
		o.println(";;; bitExpression completes");
	}
}
