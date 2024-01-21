package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Number extends CParseRule {
	// number ::= NUM
	CToken num;

	public Number(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_NUM;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		num = tk;
		tk = ct.getNextToken(pcx);

		int numValue = Integer.parseInt(num.getText());
		this.setValue(numValue);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		this.setCType(CType.getCType(CType.T_int));
		this.setConstant(true);
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; number starts");
		if (num != null) {
			Number.numberCodeGen(pcx, this.getValue());
		}
		o.println(";;; number completes");
	}

	public static void numberCodeGen(CParseContext pcx, int value) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		String valueStr = Integer.toString(value);
		o.println("\tMOV\t#" + valueStr + ", R0\t; Number: 即値をR0に用意する");
		o.println("\tMOV\tR0, (R6)+\t; Number: 即値を積む");
	}
}
