package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayDeque;

import lang.*;
import lang.c.*;

public class ArgList extends CParseRule {
	// arglist         ::= argItem { COMMA argItem }
	private ArrayDeque<CParseRule> argItems;

	public ArgList(CParseContext pcx) {
		argItems = new ArrayDeque<CParseRule>();
	}

	public static boolean isFirst(CToken tk) {
		return ArgItem.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		argItems.addLast(new ArgItem(pcx));
		argItems.getLast().parse(pcx);
		tk = ct.getCurrentToken(pcx);
		while (tk.getType() == CToken.TK_COMMA) {
			tk = ct.getNextToken(pcx);
			if (!ArgItem.isFirst(tk)) {
				pcx.recoverableError(tk.toExplainString() + ",の後ろには引数が必要です");
			}
			argItems.addLast(new ArgItem(pcx));
			argItems.getLast().parse(pcx);
			tk = ct.getCurrentToken(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
	}

	public ArrayDeque<CParseRule> getArgItems() {
		return argItems;
	}
}
