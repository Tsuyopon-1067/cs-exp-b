package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class TermDiv extends CParseRule {
	// termDiv ::= DIV factor
	CParseRule factor;

	public TermDiv(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_DIV;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		// /の次の字句を読む
		ct.getNextToken(pcx);

		factor = new Factor(pcx);
		factor.parse(pcx);
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
		o.println(";;; termDiv starts");
		if (factor != null) {
			factor.codeGen(pcx);
            o.println("\tJSR\tDIV\t; DIVサブルーチンを呼ぶ");
            o.println("\tSUB\t#2, R6\t; スタックから計算した値を消す");
            o.println("\tMOV\tR0, (R6)+\t; 結果をスタックにPushする");
		}
		o.println(";;; termDiv completes");
	}
}
