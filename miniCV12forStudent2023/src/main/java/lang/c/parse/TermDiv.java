package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class TermDiv extends AbstractTermMultDiv {
	// termDiv ::= DIV factor

	public TermDiv(CParseContext pcx, CParseRule left) {
		super(pcx, left);
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_DIV;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx);
		// /の次の字句を読む
		CToken tk = ct.getNextToken(pcx);
		if (Factor.isFirst(tk)) {
			right = new Factor(pcx);
			right.parse(pcx);
		} else {
			parseNextTokenError(pcx, tk);
		}
	}

	@Override
	protected void parseNextTokenError(CParseContext pcx, CToken tk) throws RecoverableErrorException {
		pcx.recoverableError(tk.toExplainString() + "/の後ろはfactorです");
	}

	@Override
	protected int[][] getOperationRule() {
		// 割り算の型計算規則
		final int s[][] = {
			// T_err       T_int         T_pint        T_int_array  T_pint_array
			{ CType.T_err, CType.T_err,  CType.T_err,  CType.T_err, CType.T_err }, // T_err
			{ CType.T_err, CType.T_int,  CType.T_err,  CType.T_err, CType.T_err }, // T_int
			{ CType.T_err, CType.T_err,  CType.T_err,  CType.T_err, CType.T_err }, // T_pint
			{ CType.T_err, CType.T_err,  CType.T_err,  CType.T_err, CType.T_err }, // T_int_array
			{ CType.T_err, CType.T_err,  CType.T_err,  CType.T_err, CType.T_err }, // T_pint_array
		};
		return s;
	}

	@Override
	protected void semanticCheckTypeError(CParseContext pcx) throws FatalErrorException {
		if (left.getCType() != null && right.getCType() != null) {
			pcx.warning(op.toExplainString() + "左辺の型[" + left.getCType().toString() + "]と右辺の型["
				+ right.getCType().toString() + "]は割れません");
		}
	}

	@Override
	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; termDiv starts");
		if (left != null && right != null) {
			o.println(";;; termDiv left starts");
			left.codeGen(pcx); // 左部分木のコード生成を頼む
			o.println(";;; termDiv left complete");
			o.println(";;; termDiv right starts");
			right.codeGen(pcx); // 右部分木のコード生成を頼む
			o.println(";;; termDiv right complete");
            o.println("\tJSR\tDIV\t; DIVサブルーチンを呼ぶ");
            o.println("\tSUB\t#2, R6\t; スタックから計算した値を消す");
            o.println("\tMOV\tR0, (R6)+\t; 結果をスタックにPushする");
		}
		o.println(";;; termDiv completes");
	}
}
