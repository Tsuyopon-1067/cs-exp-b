package lang.c.parse.condition;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.Expression;

public class Condition extends CParseRule {
	//condition    ::= TRUE | FALSE | expression ( conditionLT | conditionLE | conditionGT
	//                                           | conditionGE | conditionEQ | conditionNE ) | bitExpression
	CParseRule nextParseRule;
	boolean condition;
	boolean isExpression;

	public Condition(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_TRUE
			|| tk.getType() == CToken.TK_FALSE
			|| Expression.isFirst(tk);
	}


	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if (Expression.isFirst(tk)) {
			isExpression = true;
			Expression expression = new Expression(pcx);
			expression.parse(pcx);

			tk = ct.getCurrentToken(pcx); // expressionは次の字句まで読んでしまうのでcurrentは]
			nextParseRule = switch(tk.getType()) {
				case CToken.TK_LT -> new ConditionLT(pcx, expression, tk);
				case CToken.TK_LE -> new ConditionLE(pcx, expression, tk);
				case CToken.TK_GT -> new ConditionGT(pcx, expression, tk);
				case CToken.TK_GE -> new ConditionGE(pcx, expression, tk);
				case CToken.TK_EQ -> new ConditionEQ(pcx, expression, tk);
				case CToken.TK_NE -> new ConditionNE(pcx, expression, tk);
				default -> {
					pcx.recoverableError("expressionの後ろは条件演算子です");
					yield null;
				}
			};
			nextParseRule.parse(pcx);
		} else {
			isExpression = false;
			if (tk.getType() == CToken.TK_TRUE) {
				condition = true;
			} else {
				condition = false;
			}
			ct.getNextToken(pcx); // 条件演算子は次の字句まで読んでしまうのでそれに合わせる
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (nextParseRule != null && isExpression) {
			nextParseRule.semanticCheck(pcx);
			setCType(CType.getCType(CType.T_bool));
			setConstant(nextParseRule.isConstant());
		} else if (nextParseRule == null && !isExpression) {
			setCType(CType.getCType(CType.T_bool));
			setConstant(true);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; Condition starts");
		if (nextParseRule != null) {
			nextParseRule.codeGen(pcx);
		} else {
			if (condition) { // nextParseRuleがnullのときはtrueかfalse直書き
				o.println("\tMOV\t#0x0001, (R6)+\t; Condition: true");
			} else {
				o.println("\tMOV\t#0x0000, (R6)+\t; Condition: false");
			}
		}
		o.println(";;; Condition completes");
	}
}