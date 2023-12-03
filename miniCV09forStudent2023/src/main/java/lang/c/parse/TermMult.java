package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class TermMult extends CParseRule {
	// termMul ::= MUL factor
	CToken op;
	CParseRule left, right;

	public TermMult(CParseContext pcx, CParseRule left) {
		this.left = left;
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
			pcx.fatalError(tk.toExplainString() + "*の後ろはfactorです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		// 掛け算の型計算規則
		final int s[][] = {
				// T_err       T_int         T_pint        T_int_array  T_pint_array
				{ CType.T_err, CType.T_err,  CType.T_err,  CType.T_err, CType.T_err }, // T_err
				{ CType.T_err, CType.T_int,  CType.T_err,  CType.T_err, CType.T_err }, // T_int
				{ CType.T_err, CType.T_err,  CType.T_err,  CType.T_err, CType.T_err }, // T_pint
				{ CType.T_err, CType.T_err,  CType.T_err,  CType.T_err, CType.T_err }, // T_int_array
				{ CType.T_err, CType.T_err,  CType.T_err,  CType.T_err, CType.T_err }, // T_pint_array
		};
		if (left != null && right != null) {
			left.semanticCheck(pcx);
			right.semanticCheck(pcx);
			int lt = left.getCType().getType(); // -の左辺の型
			int rt = right.getCType().getType(); // -の右辺の型
			int nt = s[lt][rt]; // 規則による型計算
			if (nt == CType.T_err) {
				pcx.warning(op.toExplainString() + "左辺の型[" + left.getCType().toString() + "]と右辺の型["
						+ right.getCType().toString() + "]は掛けられません");
			}
			this.setCType(CType.getCType(nt));
			this.setConstant(left.isConstant() && right.isConstant()); // *の左右両方が定数のときだけ定数
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; termMult starts");
		if (left != null && right != null) {
			o.println(";;; termMult left starts");
			left.codeGen(pcx); // 左部分木のコード生成を頼む
			o.println(";;; termMult left complete");
			o.println(";;; termMult right starts");
			right.codeGen(pcx); // 右部分木のコード生成を頼む
			o.println(";;; termMult right complete");
			o.println("\tJSR\tMUL\t; MULサブルーチンを呼ぶ");
			o.println("\tSUB\t#2, R6\t; スタックから計算した値を消す");
			o.println("\tMOV\tR0, (R6)+\t; 結果をスタックにPushする");
		}
		o.println(";;; termMult completes");
	}
}
