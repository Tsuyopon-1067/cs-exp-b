package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.RecoverableErrorException;
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
	protected void parseNextTokenError(CParseContext pcx, CToken tk) throws RecoverableErrorException {
		pcx.recoverableError(tk.toExplainString() + "+の後ろはtermです");
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
		if (left.getCType() != null && right.getCType() != null) {
			pcx.warning(op.toExplainString() + "左辺の型[" + left.getCType().toString() + "]と右辺の型["
					+ right.getCType().toString() + "]は足せません");
		}
	}

	@Override
	protected int getNewValue(int leftValue, int rightValue) {
		return leftValue + rightValue;
	}



	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; ExpressionAdd starts");
		if (left != null && right != null) {
			right.codeGen(pcx); // 右部分木のコード生成を頼む
			left.codeGen(pcx); // 左部分木のコード生成を頼む
			o.println("\tMOV\t-(R6), R0\t; ExpressionAdd: 左部分木の値を取り出す<" + op.toExplainString() + ">");
			//o.println("\tMOV\t-(R6), R1\t; ExpressionAdd:");
			o.println("\tADD\t-(R6), R0\t; ExpressionAdd:右部分技の値を取り出して足す");
			o.println("\tMOV\tR0, (R6)+\t; ExpressionAdd:結果をスタックに保存する");
		}
		o.println(";;; ExpressionAdd completes");
	}
}