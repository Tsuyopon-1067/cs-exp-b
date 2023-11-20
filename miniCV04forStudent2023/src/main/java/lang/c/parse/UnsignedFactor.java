package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class UnsignedFactor extends CParseRule {
    //unsignedFactor ::= factorAmp | number | LPAR expression RPAR
    private CParseRule factor;

	public UnsignedFactor(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
        return FactorAmp.isFirst(tk)
                || Number.isFirst(tk)
                || tk.getType() == CToken.TK_LPAR;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		switch (tk.getType()) {
			case CToken.TK_LPAR:
				tk = ct.getNextToken(pcx);// (は読み飛ばす
				if (Expression.isFirst(tk)) {
					factor = new Expression(pcx);
					factor.parse(pcx);
					ct = pcx.getTokenizer();
					tk = ct.getCurrentToken(pcx);
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
			default:
				factor = new Number(pcx);
				factor.parse(pcx);
				break;
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
		o.println(";;; unsignedFactor starts");
		if (factor != null) {
			factor.codeGen(pcx);
		}
		o.println(";;; unsignedFactor completes");
	}
}