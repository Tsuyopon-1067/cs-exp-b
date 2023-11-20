package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class FactorAmp extends CParseRule {
    // factorAmp      ::= AMP ( number | primary )
    CToken op;
    CParseRule numberPrime;

	public FactorAmp(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_AMP;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        op = ct.getCurrentToken(pcx);
		// &の次の字句を読む
		CToken tk = ct.getNextToken(pcx);
		if (Number.isFirst(tk)) {
			numberPrime = new Number(pcx);
		else if (Ident.isFirst(tk)) {
			numberPrime = new Primary(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "&の後はNumberかPrimaryです");
		}
		numberPrime.parse(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (numberPrime != null) {
			numberPrime.semanticCheck(pcx);
			this.setCType(CType.getCType(CType.T_pint));
			setConstant(numberPrime.isConstant()); // numberPrime は常に定数
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; factorAMP starts");
		if (numberPrime != null) {
			numberPrime.codeGen(pcx);
		}
		o.println(";;; factorAMP completes");
	}
}