package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class MinusFactor extends CParseRule {
	// minusFactor ::= MINUS unsignedFactor
	CParseRule factor;

	public MinusFactor(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MINUS;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx); // -を読み飛ばす
		if (UnsignedFactor.isFirst(tk)) {
			factor = new UnsignedFactor(pcx);
			factor.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "-の後はUnsignedFactorです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (factor != null) {
			factor.semanticCheck(pcx);
			setCType(factor.getCType()); // factor の型をそのままコピー
			setConstant(factor.isConstant());
            if(factor.getCType().getType() == CType.T_pint){
                pcx.warning("ポインタに符号(-)はつけられません");
            }

			if (factor.isConstant()) {
				this.setValue(factor.getValue());
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; minusFactor starts");
		if (factor != null) {
			factor.codeGen(pcx);
		}
        o.println("\tMOV\t#0, R0\t; MinusFactor:減算するための0をレジスタにいれておく");
        o.println("\tSUB\t-(R6), R0\t; MinusFactor:負値にするために0との減算をする");
        o.println("\tMOV\tR0, (R6)+\t; MinusFactor:スタックに積み直す");
		o.println(";;; minusFactor completes");
	}
}