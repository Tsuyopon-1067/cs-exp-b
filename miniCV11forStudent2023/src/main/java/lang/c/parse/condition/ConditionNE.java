package lang.c.parse.condition;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.AbstractConditionOperator;

public class ConditionNE extends AbstractConditionOperator {
	//conditionNE ::= NE expression

	public ConditionNE(CParseContext pcx, CParseRule left, CToken op) {
		super(pcx, left, op);
	}

	@Override
	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; condition != (compare) starts");
		if (left != null && right != null) {
			left.codeGen(pcx);
			right.codeGen(pcx);
			int seq = pcx.getSeqId();
			o.println("\tMOV\t-(R6), R0\t; ConditionNE: 2数を取り出して、比べる");
			o.println("\tMOV\t-(R6), R1\t; ConditionNE:");
            o.println("\tCLR\tR2\t\t; ConditionNE: set false");
			o.println("\tCMP\tR1, R0\t; ConditionNE: R1==R0 = R0-R1=0");
			o.println("\tBRZ\tNE" + seq + "\t; ConditionNE: Z=1のときNE[seq]へジャンプ"); // 2行下の命令へジャンプ
			o.println("\tMOV\t#0x0001, R2\t; ConditionNE: set true");
			o.println("NE" + seq + ":\tMOV\tR2, (R6)+\t; ConditionNE:");
		}
		o.println(";;;condition != (compare) completes");
	}
}