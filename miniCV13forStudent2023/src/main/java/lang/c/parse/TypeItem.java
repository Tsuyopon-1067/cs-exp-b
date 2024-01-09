package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayDeque;

import lang.*;
import lang.c.*;

public class TypeItem extends CParseRule {
	// typeItem        ::= INT[MULT][LBRARBRA]
	boolean isPint = false;
	boolean isArray = false;

	public TypeItem(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_INT;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx); // intを読み飛ばす
		if (tk.getType() == CToken.TK_MULT) {
			isPint = true;
			tk = ct.getNextToken(pcx);
		}

		if (tk.getType() == CToken.TK_LBRA) {
			isArray = true;
			tk = ct.getNextToken(pcx); // [を読み飛ばす
			if (tk.getType() == CToken.TK_RBRA) {
				pcx.warning(tk.toDetailExplainString() + "[]が閉じていません");
			}
			tk = ct.getNextToken(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
	}

	public CType getItemCType() {
		if (isPint) {
			if (isArray) {
				return CType.getCType(CType.T_pint_array);
			}
			return CType.getCType(CType.T_pint);
		} else {
			if (isArray) {
				return CType.getCType(CType.T_int_array);
			}
			return CType.getCType(CType.T_int);
		}
	}
}
