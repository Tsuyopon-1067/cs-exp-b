package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class FactorAmp extends CParseRule {
    // factorAmp      ::= AMP ( number | primary )
    CToken op;
    CParseRule numberPrim;

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
			numberPrim = new Number(pcx);
		else if (Ident.isFirst(tk)) {
			numberPrim = new Primary(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "&の後はNumberかPrimaryです");
		}
		numberPrim.parse(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (numberPrim != null) {
			if (numberPrim instanceof Primary) {
				if (((Primary)numberPrim).isPrimaryMult()) {
					pcx.fatalError("&の後ろに*は付けられません");
				}

			}
			numberPrim.semanticCheck(pcx);

			// &の後ろがポインタならエラー
			if(numberPrim.getCType() == CType.getCType(CType.T_pint) || numberPrim.getCType() == CType.getCType(CType.T_pint_array)) {
				pcx.fatalError("ポインタに&はつけられません");
			}
			this.setCType(CType.getCType(CType.T_pint));
			setConstant(numberPrim.isConstant()); // numberPrim は常に定数
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; factorAMP starts");
		if (numberPrim != null) {
			numberPrim.codeGen(pcx);
		}
		o.println(";;; factorAMP completes");
	}
}