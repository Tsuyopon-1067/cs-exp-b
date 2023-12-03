package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

class BitExpressionOr extends CParseRule {
	// bitExpressionOr ::= OR bitTerm
	CToken op;
	CParseRule left, right;

	public BitExpressionOr(CParseContext pcx, CParseRule left) {
		this.left = left;
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_OR;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx);
		// ||の次の字句を読む
		CToken tk = ct.getNextToken(pcx);
		if (BitTerm.isFirst(tk)) {
			right = new BitTerm(pcx);
			right.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "||の後ろはbitTermです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		// 計算規則
		if (left != null && right != null) {
			left.semanticCheck(pcx);
			right.semanticCheck(pcx);
			if ( !(left.getCType().getType() == CType.T_bool && right.getCType().getType() == CType.T_bool) ) {
				pcx.warning(op.toExplainString() + "||の左辺と右辺はT_boolである必要があります");
			}
			this.setCType(CType.getCType(CType.T_bool));
			this.setConstant(left.isConstant() && right.isConstant()); // 演算子の左右両方が定数のときだけ定数
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		if (left != null && right != null) {
			o.println(";;; bitTermAnd left starts");
			left.codeGen(pcx); // 左部分木のコード生成を頼む
			right.codeGen(pcx); // 右部分木のコード生成を頼む
			o.println("\tMOV\t-(R6), R0\t; 右の値を取り出す");
			o.println("\tOR\t-(R6), R0\t; 左の値を取り出しORを計算する");
			o.println("\tMOV\tR0, (R6)+\t; 結果をスタックに積む");
		}
	}
}