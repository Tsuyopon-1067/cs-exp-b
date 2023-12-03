package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CType;

class ExpressionAdd extends AbstractExpressionAddSub {
	// expressionAdd ::= '+' term

	public ExpressionAdd(CParseContext pcx, CParseRule left) {
		super(pcx, left);
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_PLUS;
	}

	@Override
	protected void parseNextTokenError(CParseContext pcx, CToken tk) throws FatalErrorException {
		pcx.fatalError(tk.toExplainString() + "+の後ろはtermです");
	}

	@Override
	protected int[][] getOperationRule() {
		// 足し算の型計算規則
		final int s[][] = {
				// T_err       T_int         T_pint        T_int_array  T_pint_array
				{ CType.T_err, CType.T_err,  CType.T_err , CType.T_err, CType.T_err }, // T_err
				{ CType.T_err, CType.T_int,  CType.T_pint, CType.T_err, CType.T_err }, // T_int
				{ CType.T_err, CType.T_pint, CType.T_err,  CType.T_err, CType.T_err }, // T_pint
				{ CType.T_err, CType.T_err,  CType.T_err,  CType.T_err, CType.T_err }, // T_int_array
				{ CType.T_err, CType.T_err,  CType.T_err,  CType.T_err, CType.T_err }, // T_pint_array
		};
		return s;
	}

	@Override
	protected void semanticCheckTypeError(CParseContext pcx) throws FatalErrorException {
		pcx.warning(op.toExplainString() + "左辺の型[" + left.getCType().toString() + "]と右辺の型["
				+ right.getCType().toString() + "]は足せません");
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		if (left != null && right != null) {
			left.codeGen(pcx); // 左部分木のコード生成を頼む
			right.codeGen(pcx); // 右部分木のコード生成を頼む
			o.println("\tMOV\t-(R6), R0\t; ExpressionAdd: ２数を取り出して、足し、積む<" + op.toExplainString() + ">");
			o.println("\tMOV\t-(R6), R1\t; ExpressionAdd:");
			o.println("\tADD\tR1, R0\t; ExpressionAdd:");
			o.println("\tMOV\tR0, (R6)+\t; ExpressionAdd:");
		}
	}
}