package lang.c.parse.statement;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.ArrayList;

import lang.c.parse.*;

import lang.*;
import lang.c.*;

public class StatementCall extends CParseRule {
    // statementCall   ::= CALL ident LPAR RPAR SEMI
	private CParseRule call, ident;
	private String functionName;
	private CToken identToken;
	private ArrayList<CParseRule> expressions;
	private CSymbolTableEntry entry;

	public StatementCall(CParseContext pcx) {
		expressions = new ArrayList<CParseRule>();
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_CALL;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx); // callを読み飛ばす

		if (Ident.isFirst(tk)) {
			identToken = tk;
			ident = new Ident(pcx);
			ident.parse(pcx);
			functionName = tk.getText();
		} else {
			pcx.recoverableError(tk.toExplainString() + "callの後ろはidentです");
		}

		tk = ct.getNextToken(pcx);
		if (tk.getType() != CToken.TK_LPAR) {
			pcx.recoverableError("identの後ろは(です");
		}
		tk = ct.getNextToken(pcx); // (を読み飛ばす
		if (Expression.isFirst(tk)) {
			expressions.add(new Expression(pcx));
			expressions.get(expressions.size()-1).parse(pcx);
			tk = ct.getCurrentToken(pcx);
			while (tk.getType() == CToken.TK_COMMA) {
				tk = ct.getNextToken(pcx); // ,を読み飛ばす
				if (!Expression.isFirst(tk)) {
					pcx.recoverableError(tk.toExplainString() + ",の後ろには引数が必要です");
				}
			expressions.add(new Expression(pcx));
			expressions.get(expressions.size()-1).parse(pcx);
				tk = ct.getCurrentToken(pcx);
			}
		}
		if (tk.getType() != CToken.TK_RPAR) {
			pcx.recoverableError("StatementCall: ()が閉じていません");
		}

		tk = ct.getNextToken(pcx);
		if (tk.getType() != CToken.TK_SEMI) {
			pcx.recoverableError("StatementCall : " + tk.toExplainString() + ";がありません");
		} else {
			tk = ct.getNextToken(pcx); // ifは次の字句を読んでしまうのでそれに合わせる
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		entry = pcx.getSymbolTable().searchGlobal(functionName);
		if (entry == null) {
			pcx.warning("StatementCall: 関数" + functionName + "は宣言されていません" + identToken.toDetailExplainString());
		} else if (!entry.isFunction()) {
			pcx.warning("StatementCall: 関数" + functionName + "は変数です" + identToken.toDetailExplainString());
		}
		ident.semanticCheck(pcx);
		if (entry == null) {
			return;
		}

		int argSize = expressions.size();
		FunctionInfo functionInfo = entry.getFunctionInfo();
		if (argSize != functionInfo.getParamSize()) {
			String msg = String.format("StatementCall: 関数%s: 引数の数が一致しません<定義:%d, 使用数:%d>",
				identToken.toExplainString(), functionInfo.getParamSize(), argSize);
			pcx.warning(msg);
		}

		for (int i = 0; i < argSize; i++) {
			expressions.get(i).semanticCheck(pcx);
			CType argType = expressions.get(i).getCType();
			ArrayList<ParameterInfo> paramList = functionInfo.getParamInfoList();
			CType paramType = paramList.get(i).getType();
			String name = paramList.get(i).getName();

			if (argType.getType() != paramType.getType()) {
				String msg = String.format("StatementCall: 関数%s: 第%d引数%sの型が一致しません<定義:%s, 使用:%s>",
					identToken.toExplainString(), i+1, name, paramType.toString(), argType.toString());
				pcx.warning(msg);
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; StatementCall starts");
		int paramSize = 0;
		if (entry.isFunction()) {
			paramSize = entry.getFunctionInfo().getParamSize();
		}
		for (CParseRule expression : expressions) {
			expression.codeGen(pcx);
		}
		o.println("\tJSR\t#" + functionName + "\t; StatementCall: 関数へジャンプ");
		o.println("\tSUB\tR6, #" + paramSize + "\t; StatementCall: 引数を降ろす");
		o.println(";;; StatementCall completes");
	}
}