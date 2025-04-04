package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class PlusFactor extends CParseRule {
	// factor ::= number | factorAmp
	CParseRule factor;

	public PlusFactor(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_PLUS;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx); // +を読み飛ばす
		if (UnsignedFactor.isFirst(tk)) {
			factor = new UnsignedFactor(pcx);
			factor.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "+の後は数値が来る必要があります");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (factor != null) {
			factor.semanticCheck(pcx);
			setCType(factor.getCType()); // factor の型をそのままコピー
			setConstant(factor.isConstant()); // factor は常に定数
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; plusFactor starts");
		if (factor != null) {
			factor.codeGen(pcx);
		}
		o.println(";;; plusFactor completes");
	}
}