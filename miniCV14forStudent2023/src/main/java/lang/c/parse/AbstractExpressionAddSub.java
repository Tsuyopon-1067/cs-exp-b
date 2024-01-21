package lang.c.parse;

import lang.FatalErrorException;
import lang.RecoverableErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

abstract class AbstractExpressionAddSub extends CParseRule {
	// expressionAdd ::= '+' term
	// expressionSub ::= '-' term
	CToken op;
	protected CParseRule left, right;

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

	protected void parseNextTokenError(CParseContext pcx, CToken tk) throws RecoverableErrorException { }

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		// 計算規則
		final int s[][] = getOperationRule();
		if (left != null && right != null) {
			left.semanticCheck(pcx);
			right.semanticCheck(pcx);
			left = calculateValue(pcx, left);
			right = calculateValue(pcx, right);

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
			this.setConstant(left.isConstant() && right.isConstant()); // 演算子の左右両方が定数のときだけ定数
		}
	}

	protected abstract int[][] getOperationRule();

	protected abstract void semanticCheckTypeError(CParseContext pcx) throws FatalErrorException;

	// MulDivとほぼ共通
	protected CParseRule calculateValue(CParseContext pcx, CParseRule rule) {
		if (rule.isConstant() && rule instanceof AbstractExpressionAddSub) {
			CParseRule newRule = ((AbstractExpressionAddSub)rule).getCalculatedConstValue(pcx);
			return newRule;
		}
		return rule;
	}

	// MulDivとほぼ共通 上の階層から呼び出される
	public CParseRule getCalculatedConstValue(CParseContext pcx) {
		if ( !(left.isConstant() && right.isConstant()) ) {
			return this;
		}
		int leftValue = left.getValue();
		int rightValue = right.getValue();
		int newValue = getNewValue(leftValue, rightValue);
		Term term = new Term(pcx);
		term.setValue(newValue);
		term.setConstant(true);
		term.setCType(this.getCType());
		return term;
	}

	protected abstract int getNewValue(int leftValue, int rightValue);

	public abstract void codeGen(CParseContext pcx) throws FatalErrorException;

}