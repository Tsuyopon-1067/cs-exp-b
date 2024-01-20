package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayDeque;

import lang.*;
import lang.c.*;

public class TypeItem extends CParseRule {
	// typeItem        ::= INT[MULT][LBRARBRA]

	public TypeItem(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_INT;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx); // intを読み飛ばす

		this.setCType(CType.getCType(CType.T_int));
		if (tk.getType() == CToken.TK_MULT) {
			this.setCType(CType.getCType(CType.T_pint));
			tk = ct.getNextToken(pcx);
		}

		if (tk.getType() == CToken.TK_LBRA) {
			if (this.getCType().getType() == CType.T_int) {
				this.setCType(CType.getCType(CType.T_int_array));
			} else {
				this.setCType(CType.getCType(CType.T_pint_array));
			}

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
}
