package lang.c.parse.condition;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.AbstractConditionOperator;

public class ConditionLE extends AbstractConditionOperator {
	//conditionLE ::= LE expression

	public ConditionLE(CParseContext pcx, CParseRule left, CToken op) {
		super(pcx, left, op);
	}

	@Override
	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; condition <= (compare) starts");
		if (left != null && right != null) {
			left.codeGen(pcx);
			right.codeGen(pcx);
			int seq = pcx.getSeqId();
			o.println("\tMOV\t-(R6), R0\t; ConditionLE: 2数を取り出して、比べる");
			o.println("\tMOV\t-(R6), R1\t; ConditionLE:");
			o.println("\tMOV\t#0x0001, R2\t; ConditionLE: set true");
			o.println("\tADD\t#1, R1\t; ConditionLE: R1に1を加える");
			o.println("\tCMP\tR0, R1\t; ConditionLE: R1<=R0 = R1-R0<0");
			o.println("\tBRN\tLE" + seq + "\t; ConditionLE: N=1のときLE[seq]へジャンプ");
			o.println("\tBRZ\tLE" + seq + "\t; ConditionLE: Z=1のときLE[seq]へジャンプ");
			o.println("\tCLR\tR2\t\t; ConditionLE: set false");
			o.println("LE" + seq + ":\tMOV\tR2, (R6)+\t; ConditionLE:");
		}
		o.println(";;;condition <= (compare) completes");
	}
}