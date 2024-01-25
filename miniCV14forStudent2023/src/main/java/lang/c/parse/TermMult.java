package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class TermMult extends AbstractTermMultDiv {
	// termMul ::= MUL factor

	public TermMult(CParseContext pcx, CParseRule left) {
		super(pcx, left);
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MULT;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx);
		// *の次の字句を読む
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
		pcx.recoverableError(tk.toExplainString() + "*の後ろはfactorです");
	}

	@Override
	protected int[][] getOperationRule() {
		// 掛け算の型計算規則
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
					+ right.getCType().toString() + "]は掛けられません");
		}
	}

	@Override
	protected int getNewValue(int leftValue, int rightValue) {
		return leftValue * rightValue;
	}

	@Override
	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; termMult starts");
		if (left != null && right != null) {
			left.codeGen(pcx); // 左部分木のコード生成を頼む
			right.codeGen(pcx); // 右部分木のコード生成を頼む
			if (right.isConstant() && this.isShiftedBinValue(right.getValue())) {
				o.println("\tMOV\t-(R6), R0\t; TermMult: <*" + right.getValue() + ">");
				for (int i = right.getValue(); i > 1; i /= 2) {
					o.println("\tASL\tR0, R0\t; TermMult: シフト演算して2倍する （FオペランドはD:なら何でも良い）");
				}
			} else {
				o.println("\tJSR\tMUL\t; TermMult: MULサブルーチンを呼ぶ");
				o.println("\tSUB\t#2, R6\t; TermMult: スタックから計算した値を消す");
			}
			o.println("\tMOV\tR0, (R6)+\t; TermMult; 結果をスタックにPushする");
		}
		o.println(";;; termMult completes");
	}
}
