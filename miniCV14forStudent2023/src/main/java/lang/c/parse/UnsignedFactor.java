package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class UnsignedFactor extends CParseRule {
    // unsignedFactor ::= factorAmp | number | LPAR expression RPAR | addressToValue

    private CParseRule factor;
	String sss = "unsignedFactor";

	public UnsignedFactor(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
        return FactorAmp.isFirst(tk)
                || Number.isFirst(tk)
                || tk.getType() == CToken.TK_LPAR
				|| AddressToValue.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		switch (tk.getType()) {
			case CToken.TK_LPAR:
			sss = "(unsignedFactor)";
				tk = ct.getNextToken(pcx);// (は読み飛ばす
				if (Expression.isFirst(tk)) {
					factor = new Expression(pcx);
					factor.parse(pcx);
					ct = pcx.getTokenizer();
					tk = ct.getCurrentToken(pcx); // Expressionは次の字句まで読んでしまう
					if (tk.getType() != CToken.TK_RPAR) {
						// 閉じカッコじゃない
						pcx.fatalError(tk.toExplainString() + "括弧が閉じられていません");
					}
					ct = pcx.getTokenizer();
					tk =  ct.getNextToken(pcx);
				} else {
					pcx.fatalError(tk.toExplainString() + "左括弧の後ろはExpressionです");
				}
				break;
			case CToken.TK_AMP:
				factor = new FactorAmp(pcx);
				factor.parse(pcx);
				break;
			case CToken.TK_NUM:
				factor = new Number(pcx);
				factor.parse(pcx);
				break;
			default:
				factor = new AddressToValue(pcx);
				factor.parse(pcx);
				break;
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (factor != null) {
			factor.semanticCheck(pcx);
			setCType(factor.getCType()); // factor の型をそのままコピー
			setConstant(factor.isConstant());

			if (factor.isConstant()) {
				this.setValue(factor.getValue());
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; unsignedFactor starts");
		if (factor != null) {
			factor.codeGen(pcx);
		}
		o.println(";;; unsignedFactor completes");
	}
}