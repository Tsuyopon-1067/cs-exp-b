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
			}

			try {
				list.parse(pcx);
			} catch (RecoverableErrorException e) {
				int lineNo = tk.getLineNo();
				while (!TermMult.isFirst(tk) || TermDiv.isFirst(tk) || tk.getType() != CToken.TK_SEMI) {
					tk = ct.getNextToken(pcx);
					if (tk.getType() != CToken.TK_SEMI || tk.getLineNo() != lineNo) {
						break;
					}
				}
				pcx.warning("TermMultまたはTermDivをスキップしました");
				continue;
			}
			factor = list;
			tk = ct.getCurrentToken(pcx); // この命令がないと次の字句を読めない
		}
		// この時点でtermの次の字句まで読んでいる
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
		o.println(";;; term starts");
		if (term != null) {
			term.codeGen(pcx);
		}
		o.println(";;; term completes");
	}
}
