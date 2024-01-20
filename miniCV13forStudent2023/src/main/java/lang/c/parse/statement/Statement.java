package lang.c.parse.statement;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.FunctionInfo;

public class Statement extends CParseRule {
    // statement       ::= statementAssign | statementInput | statementOutput | statementIf | statementWhile | statementBlock | statementCall | statementReturn
    CParseRule nextCParseRule;
	FunctionInfo functionInfo;

	public Statement(CParseContext pcx, FunctionInfo functionInfo) {
		this.functionInfo = functionInfo;
	}

	public static boolean isFirst(CToken tk) {
        return StatementAssign.isFirst(tk)
			|| StatementInput.isFirst(tk)
			|| StatementOutput.isFirst(tk)
			|| StatementIf.isFirst(tk)
			|| StatementWhile.isFirst(tk)
			|| StatementBlock.isFirst(tk)
			|| StatementCall.isFirst(tk)
			|| StatementReturn.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
		ct.getCurrentToken(pcx);
		if (StatementAssign.isFirst(ct.getCurrentToken(pcx))) {
			nextCParseRule = new StatementAssign(pcx);
		} else if (StatementInput.isFirst(ct.getCurrentToken(pcx))) {
			nextCParseRule = new StatementInput(pcx);
		} else if (StatementOutput.isFirst(ct.getCurrentToken(pcx))) {
			nextCParseRule = new StatementOutput(pcx);
		} else if (StatementIf.isFirst(ct.getCurrentToken(pcx))) {
			nextCParseRule = new StatementIf(pcx, functionInfo);
		} else if (StatementWhile.isFirst(ct.getCurrentToken(pcx))) {
			nextCParseRule = new StatementWhile(pcx, functionInfo);
		} else if (StatementBlock.isFirst(ct.getCurrentToken(pcx))) {
			nextCParseRule = new StatementBlock(pcx, functionInfo);
		} else if (StatementCall.isFirst(ct.getCurrentToken(pcx))) {
			nextCParseRule = new StatementCall(pcx);
		} else if (StatementReturn.isFirst(ct.getCurrentToken(pcx))) {
			nextCParseRule = new StatementReturn(pcx, functionInfo);
		} else {
			pcx.fatalError(ct.getCurrentToken(pcx).toExplainString() + "statementの文がありません");
		}
		try {
			nextCParseRule.parse(pcx);
		} catch (FatalErrorException e) {
			CToken tk = ct.getCurrentToken(pcx);
			int lineNo = tk.getLineNo();
			while (tk.getType() != CToken.TK_SEMI
			&& tk.getType() != CToken.TK_RCUR
			&& !Statement.isFirst(tk)
			&& tk.getType() != CToken.TK_EOF) {
				tk = ct.getNextToken(pcx);
				if (tk.getLineNo() != lineNo) {
					break; // 改行したら抜ける
				}
			}
			if (tk.getType() == CToken.TK_SEMI) {
				ct.getNextToken(pcx);
			}
			pcx.warning("Statement: statementのエラーをスキップしました");
			return;
		}
		// 各statementが次の字句を読んでしまうので次の字句は読まない
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (nextCParseRule != null) {
			nextCParseRule.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; Statement starts");
		if (nextCParseRule != null) {
			nextCParseRule.codeGen(pcx);
		}
		o.println(";;; Statement completes");
	}
}