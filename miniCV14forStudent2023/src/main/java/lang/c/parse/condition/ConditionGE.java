package lang.c.parse.condition;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class ConditionGE extends AbstractConditionOperator {
	//conditionGE ::= GE expression

	public ConditionGE(CParseContext pcx, CParseRule left, CToken op) {
		super(pcx, left, op);
	}

	@Override
	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; condition >= (compare) starts");
		if (left != null && right != null) {
			left.codeGen(pcx);
			right.codeGen(pcx);
			int seq = pcx.getSeqId();
			o.println("\tMOV\t-(R6), R0\t; ConditionGE: 右側の数を取得する");
			//o.println("\tMOV\t-(R6), R1\t; ConditionGE:");
			o.println("\tMOV\t#0x0001, R2\t; ConditionGE: set true");
			o.println("\tCMP\t-(R6), R0\t; ConditionGE: 左側の数を用意して比較する R1>=R0 =  R0-R1<=0");
			o.println("\tBRN\tGE" + seq + "\t; ConditionGE: N=1のときGE[seq]へジャンプ");
			o.println("\tBRZ\tGE" + seq + "\t; ConditionGE: Z=1のときGE[seq]へジャンプ");
			o.println("\tCLR\tR2\t\t; ConditionGE: set false");
			o.println("GE" + seq + ":\tMOV\tR2, (R6)+\t; ConditionGE:");
		}
		o.println(";;;condition >= (compare) completes");
	}
}