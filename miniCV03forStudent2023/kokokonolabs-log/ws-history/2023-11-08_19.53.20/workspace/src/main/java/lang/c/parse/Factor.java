package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Factor extends CParseRule {
	// factor ::= PlusFactor.isFirst(tk) MinusFactor.isFirst(tk) UnsignedFactor.isFirst(tk);
	CParseRule number;

	public Factor(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return PlusFactor.isFirst(tk) || MinusFactor.isFirst(tk) || UnsignedFactor.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		System.out.println(tk.getText());
		System.out.println("sjlksfdjlsfjlsdfs");
		if (Number.isFirst(tk)) {
			number = new Number(pcx);
			number.parse(pcx);
		} else if (FactorAmp.isFirst(tk)) {
			number = new FactorAmp(pcx);
			number.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "オーバーフローする数です");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (number != null) {
			number.semanticCheck(pcx);
			setCType(number.getCType()); // number の型をそのままコピー
			setConstant(number.isConstant()); // number は常に定数
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; factor starts");
		if (number != null) {
			number.codeGen(pcx);
		}
		o.println(";;; factor completes");
	}
}