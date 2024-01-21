package lang.c.parse.condition;

import lang.*;
import lang.c.*;
import lang.c.parse.BitExpression;
import lang.c.parse.BitTerm;
import lang.c.parse.Expression;

public abstract class AbstractConditionOperator extends CParseRule {
	protected CParseRule left, right; // right = expression
	protected CToken op;
	protected int seq;

	public AbstractConditionOperator(CParseContext pcx, CParseRule left, CToken op) {
		this.left = left;
		this.op = op;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx); // 比較演算子を読み飛ばす
		try {
			if (Expression.isFirst(tk)) {
				right = new Expression(pcx);
				right.parse(pcx);
			} else {
				pcx.recoverableError(op.toExplainString() + "比較演算子の後ろはexpressionです");
			}
		} catch (RecoverableErrorException e) {
			ct.getNextToken(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (left != null && right != null) {
			left.semanticCheck(pcx);
			right.semanticCheck(pcx);
			CType leftType = left.getCType();
			CType rightType = right.getCType();
			if (leftType.getType() != rightType.getType()) {
				pcx.warning(op.toExplainString() + "左辺の型[" + left.getCType().toString() + "]と右辺の型["
						+ right.getCType().toString() + "]は比較できません");
			}

			setConstant(left.isConstant() && right.isConstant());
		}
		setCType(CType.getCType(CType.T_bool));
	}

	// AddSubとほぼ共通 上の階層から呼び出される
	public CParseRule getCalculatedConstValue(CParseContext pcx) {
		System.out.println("getCalculatedConstValue");
		if (!(left.isConstant() && right.isConstant())) {
			return this;
		}
		int leftValue = left.getValue();
		int rightValue = right.getValue();
		int newValue = newValue(leftValue, rightValue);
		BitTerm newTerm = new BitTerm(pcx);
		newTerm.setValue(newValue);
		newTerm.setConstant(true);
		newTerm.setCType(CType.getCType(CType.T_bool));
		return newTerm;
	}
	public abstract int newValue(int left, int right);


	public abstract void codeGen(CParseContext pcx) throws FatalErrorException;
}