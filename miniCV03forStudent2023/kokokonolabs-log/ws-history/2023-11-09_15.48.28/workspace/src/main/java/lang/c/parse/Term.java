package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Term extends CParseRule {
	// term ::= factor {termMult | termDiv}
	CParseRule factor, mulDiv;

	public Term(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
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
					mulDiv = new TermMult(pcx, factor);
					break;
				case CToken.TK_DIV:
					mulDiv = new TermDiv(pcx, factor);
					break;
				default:
					pcx.fatalError(tk.toExplainString() + "*または/が必要です");
			}
			ct = pcx.getTokenizer();
			tk = ct.getCurrentToken(pcx);
			mulDiv.parse(pcx);
			tk = ct.getCurrentToken(pcx); // この命令がないと次の字句を読めない
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (factor != null) {
			mulDiv.semanticCheck(pcx);
			this.setCType(mulDiv.getCType()); // factor の型をそのままコピー
			this.setConstant(mulDiv.isConstant());
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
