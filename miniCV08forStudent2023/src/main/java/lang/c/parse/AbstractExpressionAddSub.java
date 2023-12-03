package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

abstract class AbstractExpressionAddSub extends CParseRule {
	// expressionAdd ::= '+' term
	// expressionSub ::= '-' term
	CToken op;
	CParseRule left, right;

	public AbstractExpressionAddSub(CParseContext pcx, CParseRule left) {
		this.left = left;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx);
		// +-の次の字句を読む
		CToken tk = ct.getNextToken(pcx);
		if (Term.isFirst(tk)) {
			right = new Term(pcx);
			right.parse(pcx);
		} else {
			parseNextTokenError(pcx, tk); // 演算子の後ろはtermであるというエラーを出す
		}
	}

	protected void parseNextTokenError(CParseContext pcx, CToken tk) throws FatalErrorException { }

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		// 計算規則
		final int s[][] = getOperationRule();
		if (left != null && right != null) {
			left.semanticCheck(pcx);
			right.semanticCheck(pcx);
			int lt = left.getCType().getType(); // 左辺の型
			int rt = right.getCType().getType(); // 右辺の型
			int nt = s[lt][rt]; // 規則による型計算
			if (nt == CType.T_err) {
				semanticCheckTypeError(pcx);
			}
			this.setCType(CType.getCType(nt));
			this.setConstant(left.isConstant() && right.isConstant()); // 演算子の左右両方が定数のときだけ定数
		}
	}

	protected int[][] getOperationRule() {
		return null;
	}
	protected void semanticCheckTypeError(CParseContext pcx) throws FatalErrorException { }

	public abstract void codeGen(CParseContext pcx) throws FatalErrorException;
}