package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class TermMult extends CParseRule {
	// termMul ::= MUL factor
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
		// *の次の字句を読む
		ct.getNextToken(pcx);

		right = new Factor(pcx);
		right.parse(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		// 掛け算の型計算規則
		final int s[][] = {
				// T_err       T_int         T_pint
				{ CType.T_err, CType.T_err,  CType.T_err }, // T_err
				{ CType.T_err, CType.T_int,  CType.T_err }, // T_int
				{ CType.T_err, CType.T_pint, CType.T_int },  // T_pint
		};
		if (left != null && right != null) {
			right.semanticCheck(pcx);
			int lt = left.getCType().getType(); // *の左辺の型
			int rt = right.getCType().getType(); // *の右辺の型
			int nt = s[lt][rt]; // 規則による型計算
			this.setCType(right.getCType()); // right の型をそのままコピー
			this.setConstant(right.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; termMult starts");
		if (right != null) {
			right.codeGen(pcx);
            o.println("\tJSR\tMUL\t; MULサブルーチンを呼ぶ");
            o.println("\tSUB\t#2, R6\t; スタックから計算した値を消す");
            o.println("\tMOV\tR0, (R6)+\t; 結果をスタックにPushする");
		}
		o.println(";;; termMult completes");
	}
}
