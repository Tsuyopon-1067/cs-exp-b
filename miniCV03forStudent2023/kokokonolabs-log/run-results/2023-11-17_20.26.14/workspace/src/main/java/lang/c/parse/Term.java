package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Term extends CParseRule {
	// term ::= factor {termMult | termDiv}
	CParseRule term;

	public Term(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return Factor.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CParseRule factor = null, list = null;
		factor = new Factor(pcx);
		factor.parse(pcx);
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		while (TermMult.isFirst(tk) || TermDiv.isFirst(tk)) {
			if (TermMult.isFirst(tk)) {
				list = new TermMult(pcx, factor);
			} else if (TermDiv.isFirst(tk)) {
				list = new TermDiv(pcx, factor);
			} else {
				pcx.fatalError(tk.toExplainString() + "*または/が必要です");
			}
			if (list != null) {
				list.parse(pcx);
				factor = list;
				tk = ct.getCurrentToken(pcx); // この命令がないと次の字句を読めない
			}
		}
		term = factor;
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		//if (factor != null) {
		//	factor.semanticCheck(pcx);
		//	this.setCType(factor.getCType()); // factor の型をそのままコピー
		//	this.setConstant(factor.isConstant());
		//}
		if (term != null) {
			term.semanticCheck(pcx);
			this.setCType(term.getCType()); // factor の型をそのままコピー
			this.setConstant(term.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; term starts");
		//if (factor != null) {
		//	factor.codeGen(pcx);
		//}
		if (term != null) {
			term.codeGen(pcx);
		}
		o.println(";;; term completes");
	}
}
