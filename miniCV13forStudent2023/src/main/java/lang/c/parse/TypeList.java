package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayDeque;

import lang.*;
import lang.c.*;

public class TypeList extends CParseRule {
	// call            ::= LPAR RPAR
	CToken num;
	ArrayDeque<CParseRule> typeItems;

	public TypeList(CParseContext pcx) {
		typeItems = new ArrayDeque<CParseRule>();
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_LPAR;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

		typeItems.addLast(new TypeItem(pcx));
		typeItems.getLast().parse(pcx);

		while (tk.getType() == CToken.TK_COMMA) {
			tk = ct.getNextToken(pcx); // ,を読み飛ばす
			if (!TypeItem.isFirst(tk)) {
				pcx.recoverableError(tk.toExplainString() + ",の後ろには引数が必要です");
				continue;
			}
			typeItems.addLast(new TypeItem(pcx));
			typeItems.getLast().parse(pcx);
			tk = ct.getCurrentToken(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
	}
}
