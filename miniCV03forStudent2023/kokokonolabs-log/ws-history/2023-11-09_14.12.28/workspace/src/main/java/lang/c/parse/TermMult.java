package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class TermMult extends CParseRule {
	// termMul ::= MUL factor
	CToken op;
	CParseRule factor, left, right;

	public TermMult(CParseContext pcx, CParseRule left) {
		this.left = left;
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MULT;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx);
		// *の次の字句を読む
		CToken tk = ct.getNextToken(pcx);
		if (Term.isFirst(tk)) {
			right = new Term(pcx);
			right.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "-の後ろはtermです");
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
		o.println(";;; termMult starts");
		if (factor != null) {
			factor.codeGen(pcx);
            o.println("\tJSR\tMUL\t; MULサブルーチンを呼ぶ");
            o.println("\tSUB\t#2, R6\t; スタックから計算した値を消す");
            o.println("\tMOV\tR0, (R6)+\t; 結果をスタックにPushする");
		}
		o.println(";;; termMult completes");
	}
}
