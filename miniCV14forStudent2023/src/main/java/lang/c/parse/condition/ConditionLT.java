package lang.c.parse.condition;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class ConditionLT extends AbstractConditionOperator {
	//conditionLT ::= LT expression

	public ConditionLT(CParseContext pcx, CParseRule left, CToken op) {
		super(pcx, left, op);
	}

	@Override
	public int newValue(int left, int right) {
		return (left < right) ? 1 : 0;
	}

	@Override
	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; condition < (compare) starts");
		if (left != null && right != null) {
			left.codeGen(pcx);
			right.codeGen(pcx);
			int seq = pcx.getSeqId();
			o.println("\tMOV\t-(R6), R0\t; ConditionLT:  右の数を用意する2数を取り出して、比べる");
			//o.println("\tMOV\t-(R6), R1\t; ConditionLT:");
			o.println("\tMOV\t#0x0001, R2\t; ConditionLT: set true");
			o.println("\tCMP\tR0, -(R6)\t; 左の数を取得して比較する ConditionLT: R1<R0 = R1-R0<0");
			o.println("\tBRN\tLT" + seq + "\t; ConditionLT: N=1のときLT[seq]へジャンプ");
			o.println("\tCLR\tR2\t\t; ConditionLT: set false");
			o.println("LT" + seq + ":\tMOV\tR2, (R6)+\t; ConditionLT:");
		}
		o.println(";;;condition < (compare) completes");
	}
}