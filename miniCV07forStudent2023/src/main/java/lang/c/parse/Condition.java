package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Condition extends CParseRule {
	//condition    ::= TRUE | FALSE | expression ( conditionLT | conditionLE | conditionGT
	//                                           | conditionGE | conditionEQ | conditionNE )
	CParseRule nextParseRule;
	boolean condition;

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
			Expression expression = new Expression(pcx);
			expression.parse(pcx);

			tk = ct.getCurrentToken(pcx);
			nextParseRule = switch(tk.getType()) {
				case CToken.TK_LT -> new ConditionLT(pcx, expression, tk);
				case CToken.TK_LE -> new ConditionLE(pcx, expression, tk);
				case CToken.TK_GT -> new ConditionGT(pcx, expression, tk);
				case CToken.TK_GE -> new ConditionGE(pcx, expression, tk);
				case CToken.TK_EQ -> new ConditionEQ(pcx, expression, tk);
				case CToken.TK_NE -> new ConditionNE(pcx, expression, tk);
				default -> {
					pcx.fatalError("expressionの後ろは条件演算子です");
					yield null;
				}
			};
			nextParseRule.parse(pcx);
		} else {
			if (tk.getType() == CToken.TK_TRUE) {
				condition = true;
			} else {
				condition = false;
			}
			ct.getNextToken(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (nextParseRule != null) {
			nextParseRule.semanticCheck(pcx);
			setCType(nextParseRule.getCType());
			setConstant(nextParseRule.isConstant());
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