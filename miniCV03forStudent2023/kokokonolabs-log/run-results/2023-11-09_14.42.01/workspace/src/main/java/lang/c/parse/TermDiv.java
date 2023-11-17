package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class TermDiv extends CParseRule {
	// termDiv ::= DIV factor
	CToken op;
	CParseRule left, right;

	public TermDiv(CParseContext pcx, CParseRule left) {
		this.left = left;
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_DIV;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx);
		System.out.printf("%s, %s\n", op.getTokenString(), op.getText());
		// /の次の字句を読む
		ct.getNextToken(pcx);

		CToken tk = ct.getNextToken(pcx);
		if (Term.isFirst(tk)) {
			right = new Term(pcx);
			right.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "/の後ろはtermです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		// 割り算の型計算規則
		final int s[][] = {
				// T_err       T_int         T_pint
				{ CType.T_err, CType.T_err,  CType.T_err }, // T_err
				{ CType.T_err, CType.T_int,  CType.T_err }, // T_int
				{ CType.T_err, CType.T_err, CType.T_err },  // T_pint
		};
		if (left != null && right != null) {
			left.semanticCheck(pcx);
			right.semanticCheck(pcx);
			int lt = left.getCType().getType(); // -の左辺の型
			int rt = right.getCType().getType(); // -の右辺の型
			int nt = s[lt][rt]; // 規則による型計算
			if (nt == CType.T_err) {
				pcx.fatalError(op.toExplainString() + "左辺の型[" + left.getCType().toString() + "]と右辺の型["
						+ right.getCType().toString() + "]は割れません");
			}
			this.setCType(CType.getCType(nt));
			this.setConstant(left.isConstant() && right.isConstant()); // /の左右両方が定数のときだけ定数
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; termDiv starts");
		if (right != null) {
			right.codeGen(pcx);
            o.println("\tJSR\tDIV\t; DIVサブルーチンを呼ぶ");
            o.println("\tSUB\t#2, R6\t; スタックから計算した値を消す");
            o.println("\tMOV\tR0, (R6)+\t; 結果をスタックにPushする");
		}
		o.println(";;; termDiv completes");
	}
}
