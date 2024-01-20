package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.ArrayList;

import lang.*;
import lang.c.*;

public class Call extends CParseRule {
	// call            ::= LPAR RPAR
	CToken num;
	ArrayList<CParseRule> expressions;
	FunctionInfo functionInfo;

	public Call(CParseContext pcx, FunctionInfo functionInfo) {
		expressions = new ArrayList<CParseRule>();
		this.functionInfo = functionInfo;
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_LPAR;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx); // (を読み飛ばす

		if (Expression.isFirst(tk)) {
			expressions.add(new Expression(pcx));
			expressions.get(expressions.size()-1).parse(pcx);
			tk = ct.getCurrentToken(pcx);
			while (tk.getType() == CToken.TK_COMMA) {
				tk = ct.getNextToken(pcx); // ,を読み飛ばす
				if (!Expression.isFirst(tk)) {
					pcx.recoverableError(tk.toExplainString() + ",の後ろには引数が必要です");
					continue;
				}
				expressions.add(new Expression(pcx));
				expressions.get(expressions.size()-1).parse(pcx);
				tk = ct.getCurrentToken(pcx);
			}
		}

		if (tk.getType() != CToken.TK_RPAR) {
			pcx.recoverableError(tk.toDetailExplainString() + "Call: ()が閉じていません");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		int argSize = expressions.size();
		if (argSize != functionInfo.getParamSize()) {
			String msg = String.format("Call: 関数%s: 引数の数が一致しません<定義:%d, 使用数:%d>",
				functionInfo.getName(), functionInfo.getParamSize(), argSize);
			pcx.warning(msg);
		}

		for (int i = 0; i < argSize; i++) {
			expressions.get(i).semanticCheck(pcx);
			CType argType = expressions.get(i).getCType();
			ArrayList<ParameterInfo> paramList = functionInfo.getParamInfoList();
			CType paramType = paramList.get(i).getType();
			String name = paramList.get(i).getName();

			if (argType.getType() != paramType.getType()) {
				String msg = String.format("Call: 関数%s: 第%d引数%sの型が一致しません<定義:%s, 使用:%s>",
					functionInfo.getName(), i+1, name, paramType.toString(), argType.toString());
				pcx.warning(msg);
			}
		}

	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; Call starts");
		int argSize = expressions.size();
		for (int i = argSize-1; i >= 0; i--) {
			expressions.get(i).codeGen(pcx);
		}
		o.println(";;; Call completes");
	}
}
