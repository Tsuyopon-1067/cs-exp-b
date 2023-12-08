package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Function extends CParseRule {
	// function        ::= FUNC ( INT [ MULT ] | VOID ) IDENT LPAR RPAR declblock
	CParseRule ident, declBlock;

	public Function(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_FUNC;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx); // funcを読み飛ばす
System.err.println("fun " + tk.toDetailExplainString());

		if (tk.getType() == CToken.TK_INT) {
			tk = ct.getNextToken(pcx);
			if (tk.getType() == CToken.TK_MULT) {
				tk = ct.getNextToken(pcx);
			}
		} else if (tk.getType() == CToken.TK_VOID) {
			tk = ct.getNextToken(pcx);
		} else {
			pcx.recoverableError(tk.toExplainString() + "funcの後ろはintかvoidです");
		}

		if (tk.getType() == CToken.TK_IDENT) {
			ident = new Ident(pcx);
			tk = ct.getNextToken(pcx);
		} else {
			pcx.recoverableError(tk.toExplainString() + "関数名が必要です");
		}

		if (Call.isFirst(tk)) {
			Call call = new Call(pcx);
			call.parse(pcx);
			tk = ct.getNextToken(pcx);
		} else {
			pcx.recoverableError(tk.toExplainString() + "関数名の後ろは()です");
		}

		if (DeclBlock.isFirst(tk)) {
			declBlock = new DeclBlock(pcx);
			declBlock.parse(pcx);
		} else {
			pcx.recoverableError(tk.toExplainString() + "()の後ろはDeclBlockです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
	}
}
