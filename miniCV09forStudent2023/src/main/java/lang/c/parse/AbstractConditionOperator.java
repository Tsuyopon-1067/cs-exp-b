package lang.c.parse;

import lang.*;
import lang.c.*;

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
		if (!Expression.isFirst(tk)) {
			pcx.fatalError(op.toExplainString() + "比較演算子の後ろはexpressionです");
		}
		right = new Expression(pcx);
		right.parse(pcx);
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
		}
		setCType(CType.getCType(CType.T_bool));
		setConstant(true);
	}

	public abstract void codeGen(CParseContext pcx) throws FatalErrorException;
}