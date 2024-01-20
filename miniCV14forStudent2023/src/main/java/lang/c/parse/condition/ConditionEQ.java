package lang.c.parse.condition;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.AbstractConditionOperator;

public class ConditionEQ extends AbstractConditionOperator {
	//conditionEQ ::= EQ expression

	public ConditionEQ(CParseContext pcx, CParseRule left, CToken op) {
		super(pcx, left, op);
	}

	@Override
	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; condition == (compare) starts");
		if (left != null && right != null) {
			left.codeGen(pcx);
			right.codeGen(pcx);
			int seq = pcx.getSeqId();
			o.println("\tMOV\t-(R6), R0\t; ConditionEQ: 右の数を用意する");
			//o.println("\tMOV\t-(R6), R1\t; ConditionEQ:");
			o.println("\tMOV\t#0x0001, R2\t; ConditionEQ: set true");
			o.println("\tCMP\t-(R6), R0\t; ConditionEQ: 左の数を取得して比較する R1==R0 = R0-R1=0");
			o.println("\tBRZ\tEQ" + seq + "\t; ConditionEQ: Z=1のときEQ[seq]へジャンプ"); // 2行下の命令へジャンプ
			o.println("\tCLR\tR2\t\t; ConditionEQ: set false");
			o.println("EQ" + seq + ":\tMOV\tR2, (R6)+\t; ConditionEQ:");
		}
		o.println(";;;condition == (compare) completes");
	}
}