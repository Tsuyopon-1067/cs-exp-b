package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.statement.Statement;

public class Term extends CParseRule {
	// term ::= factor {termMult | termDiv}
	CParseRule term;
	CToken termToken;

	public Term(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return Factor.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		termToken = tk;

		CParseRule factor = null, list = null; // factor.leftになるのでこのクラス内で保持する必要はない
		factor = new Factor(pcx);
		factor.parse(pcx);
		tk = ct.getCurrentToken(pcx);
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
				while (tk.getType() != CToken.TK_SEMI && !Statement.isFirst(tk) && tk.getType() != CToken.TK_EOF) {
					tk = ct.getNextToken(pcx);
					if (tk.getLineNo() != lineNo) {
						break; // 改行したら抜ける
					}
				}
				pcx.warning("TermMultまたはTermDivをスキップしました");
				return;
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
			this.setValue(term.getValue());

			if (term.isConstant()) {
				if (term instanceof Factor) {
					Factor newFactor = new Factor(pcx);
					int newValue = ((Factor)term).getValue();
					newFactor.setValue(newValue);
					newFactor.setConstant(true);
					newFactor.setCType(this.getCType());
					term = newFactor;
				} else if (term instanceof AbstractTermMultDiv) {
					AbstractTermMultDiv newTerm = (AbstractTermMultDiv)term;
					term = newTerm.getCalculatedConstValue(pcx);
				}
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; term starts");

		if (term != null) {
			term.codeGen(pcx);
		} else if (this.isConstant()) {
			Number.numberCodeGen(pcx, this.getValue());
		}
		o.println(";;; term completes");
	}
}
