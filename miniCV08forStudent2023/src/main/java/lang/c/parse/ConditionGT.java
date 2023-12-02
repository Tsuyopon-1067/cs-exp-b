package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class ConditionGT extends AbstractConditionOperator {
	//conditionGT ::= GT expression

	public ConditionGT(CParseContext pcx, CParseRule left, CToken op) {
		super(pcx, left, op);
	}

	@Override
	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; condition > (compare) starts");
		if (left != null && right != null) {
			left.codeGen(pcx);
			right.codeGen(pcx);
			int seq = pcx.getSeqId();
			o.println("\tMOV\t-(R6), R0\t; ConditionGT: 2数を取り出して、比べる");
			o.println("\tMOV\t-(R6), R1\t; ConditionGT:");
			o.println("\tMOV\t#0x0001, R2\t; ConditionGT: set true");
			o.println("\tCMP\tR1, R0\t; ConditionGT: R1>R0 = R0-R1<0");
			o.println("\tBRN\tGT" + seq + "\t; ConditionGT: N=1のときGT[seq]へジャンプ");
			o.println("\tCLR\tR2\t\t; ConditionGT: set false");
			o.println("GT" + seq + ":\tMOV\tR2, (R6)+\t; ConditionGT:");
		}
		o.println(";;;condition > (compare) completes");
	}
}