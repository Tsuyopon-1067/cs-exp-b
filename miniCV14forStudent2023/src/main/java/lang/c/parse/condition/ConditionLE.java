package lang.c.parse.condition;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

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
			o.println("\tMOV\t-(R6), R0\t; ConditionLE: 右の数を用意する 2数を取り出して、比べる");
			//o.println("\tMOV\t-(R6), R1\t; ConditionLE:");
			o.println("\tMOV\t#0x0001, R2\t; ConditionLE: set true");
			o.println("\tCMP\tR0, -(R6)\t; 左の数を取得して比較する ConditionLE: R1<=R0 = R1<(R0+1) = R1-(R0+1)<0");
			o.println("\tBRN\tLE" + seq + "\t; ConditionLE: N=1のときLE[seq]へジャンプ");
			o.println("\tBRZ\tLE" + seq + "\t; ConditionLE: Z=1のときLE[seq]へジャンプ");
			o.println("\tCLR\tR2\t\t; ConditionLE: set false");
			o.println("LE" + seq + ":\tMOV\tR2, (R6)+\t; ConditionLE:");
		}
		o.println(";;;condition <= (compare) completes");
	}
}