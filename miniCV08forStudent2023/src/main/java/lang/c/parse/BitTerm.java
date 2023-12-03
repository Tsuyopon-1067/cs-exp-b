package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class BitTerm extends CParseRule {
	// bitTerm        ::= bitFactor { AND bitFactor }
	CParseRule term;

	public BitTerm(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return BitFactor.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CParseRule factor = null, list = null;
		factor = new BitFactor(pcx);
		factor.parse(pcx);

		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

		while (BitTermAnd.isFirst(tk)) {
			list = new BitTermAnd(pcx, factor);
			if (list != null) {
				list.parse(pcx);
				factor = list;
				tk = ct.getCurrentToken(pcx); // この命令がないと次の字句を読めない
			}
		}
		// この時点でtermの次の字句まで読んでいる
		if (tk.getType() == CToken.TK_AMP) {
			pcx.fatalError(tk.toExplainString() + "演算子は&ではなく&&です");
		}
		term = factor;
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (term != null) {
			term.semanticCheck(pcx);
			this.setCType(term.getCType()); // term の型をそのままコピー
			this.setConstant(term.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; bitTerm starts");
		if (term != null) {
			term.codeGen(pcx);
		}
		o.println(";;; bitTerm completes");
	}
}
