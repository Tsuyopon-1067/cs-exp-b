package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.condition.Condition;

public class BitNotFactor extends CParseRule {
	// bitNotFactor    ::= NOT bitFactor
	CParseRule nextParseRule;
	CToken op;
	boolean isCondiriton;

	public BitNotFactor(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_NOT;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx);
		CToken tk = ct.getNextToken(pcx); // NOTの次の字句を読む
		if (BitFactor.isFirst(tk)) {
			nextParseRule = new BitFactor(pcx);
			nextParseRule.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "!の後ろはbitFactorです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (nextParseRule != null) {
			nextParseRule.semanticCheck(pcx);
			if (nextParseRule.getCType().getType() != CType.T_bool) {
				pcx.warning(op.toExplainString() + "!の右はT_boolです");
			}
			setCType(nextParseRule.getCType()); // 型をそのままコピー
			setConstant(nextParseRule.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; bitNotFactor starts");
		if (nextParseRule != null) {
			nextParseRule.codeGen(pcx);
		}
		o.println("\tMOV\t-(R6), R0\t; 値を取り出す");
		o.println("\tXOR\t#0x0001, R0\t; NOTを計算する");
		o.println("\tMOV\tR0, (R6)+\t; 結果をスタックに積む");
		o.println(";;; bitNotFactor completes");
	}
}