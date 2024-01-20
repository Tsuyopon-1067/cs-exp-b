package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.RecoverableErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CType;

class ExpressionSub extends AbstractExpressionAddSub {
	// expressionSub ::= '-' term

	public ExpressionSub(CParseContext pcx, CParseRule left) {
		super(pcx, left);
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MINUS;
	}

	@Override
	protected void parseNextTokenError(CParseContext pcx, CToken tk) throws RecoverableErrorException {
		pcx.recoverableError(tk.toExplainString() + "-の後ろはtermです");
	}

	@Override
	protected int[][] getOperationRule() {
		// 引き算の型計算規則
		final int s[][] = {
				// T_err       T_int         T_pint        T_int_array  T_pint_array
				{ CType.T_err, CType.T_err,  CType.T_err,  CType.T_err, CType.T_err }, // T_err
				{ CType.T_err, CType.T_int,  CType.T_err,  CType.T_err, CType.T_err }, // T_int
				{ CType.T_err, CType.T_pint, CType.T_int,  CType.T_err, CType.T_err }, // T_pint
				{ CType.T_err, CType.T_err,  CType.T_err,  CType.T_err, CType.T_err }, // T_int_array
				{ CType.T_err, CType.T_err,  CType.T_err,  CType.T_err, CType.T_err }, // T_pint_array
		};
		return s;
	}

	@Override
	protected void semanticCheckTypeError(CParseContext pcx) throws FatalErrorException {
		pcx.warning(op.toExplainString() + "左辺の型[" + left.getCType().toString() + "]と右辺の型["
				+ right.getCType().toString() + "]は引けません");
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		if (left != null && right != null) {
			right.codeGen(pcx); // 右部分木のコード生成を頼む
			left.codeGen(pcx); // 左部分木のコード生成を頼む
			o.println("\tMOV\t-(R6), R0\t; ExpressionSub: 左部分木の値を取り出す<" + op.toExplainString() + ">");
			//o.println("\tMOV\t-(R6), R1\t; ExpressionSub:");
			o.println("\tSUB\t-(R6), R0\t; ExpressionSub:右部分木の値を取り出して引く");
			o.println("\tMOV\tR0, (R6)+\t; ExpressionSub:結果をスタックに保存する");
		}
	}
}