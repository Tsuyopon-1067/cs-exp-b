package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Term extends CParseRule {
	// term ::= factor {termMult | termDiv}
	CParseRule factor;

	public Term(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		if (Factor.isFirst(tk)) {
			System.out.println("true3\\true3");
		}
)
		return Factor.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		factor = new Factor(pcx);
		factor.parse(pcx);
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		while (TermMult.isFirst(tk) || TermDiv.isFirst(tk)) {
			switch(tk.getType()) {
				case CToken.TK_MULT:
					factor = new TermMult(pcx);
				case CToken.TK_DIV:
					factor = new TermDiv(pcx);
				default:
					pcx.fatalError(tk.toExplainString() + "*または/が必要です");
			}
			ct = pcx.getTokenizer();
			tk = ct.getCurrentToken(pcx);
			factor.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (factor != null) {
			factor.semanticCheck(pcx);
			this.setCType(factor.getCType()); // factor の型をそのままコピー
			this.setConstant(factor.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; term starts");
		if (factor != null) {
			factor.codeGen(pcx);
		}
		o.println(";;; term completes");
	}
}
