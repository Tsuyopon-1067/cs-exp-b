package lang.c.parse;

import lang.*;
import lang.c.*;

public abstract class AbstractTermMultDiv extends CParseRule {
	// termMul ::= MUL factor
	// termDiv ::= DIV factor
	CToken op;
	CParseRule left, right;

	public AbstractTermMultDiv(CParseContext pcx, CParseRule left) {
		this.left = left;
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

	protected abstract void parseNextTokenError(CParseContext pcx, CToken tk) throws RecoverableErrorException;

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		// 計算規則
		final int s[][] = getOperationRule();
		if (left != null && right != null) {
			left.semanticCheck(pcx);
			right.semanticCheck(pcx);
			int lt = 0;
			int rt = 0;
			if (left.getCType() != null) {
				lt = left.getCType().getType(); // 左辺の型
			}
			if (right.getCType() != null) {
				rt = right.getCType().getType(); // 右辺の型
			}
			int nt = s[lt][rt]; // 規則による型計算
			if (nt == CType.T_err) {
				semanticCheckTypeError(pcx);
			}
			this.setCType(CType.getCType(nt));
			this.setConstant(left.isConstant() && right.isConstant()); // /の左右両方が定数のときだけ定数
		}
	}

	abstract protected int[][] getOperationRule();

	protected abstract void semanticCheckTypeError(CParseContext pcx) throws FatalErrorException;

	public abstract void codeGen(CParseContext pcx) throws FatalErrorException;
}
