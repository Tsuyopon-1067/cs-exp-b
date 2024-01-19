package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class ArgItem extends CParseRule {
	// argItem         ::= INT [ MULT ] IDENT [ LBRA RBRA ]
	CToken num;
	boolean isPint = false;
	boolean isArray = false;
	CParseRule ident;


	public ArgItem(CParseContext pcx) {
		setCType(CType.getCType(CType.T_int));
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_INT;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx); // intを読み飛ばす
		if (tk.getType() == CToken.TK_MULT) {
			tk = ct.getNextToken(pcx);
			isPint = true;
			setCType(CType.getCType(CType.T_pint));
		}
		if (tk.getType() == CToken.TK_IDENT) {
			// todo identの処理
			tk = ct.getNextToken(pcx); // identの次を読む
		} else {
			pcx.recoverableError(tk.toExplainString() + "変数名が必要です");
		}

		if (tk.getType() == CToken.TK_LBRA) {
			isArray = true;
			tk = ct.getNextToken(pcx); // [を読み飛ばす
			if (tk.getType() == CToken.TK_RBRA) {
				pcx.warning(tk.toDetailExplainString() + "[]が閉じていません");
			}
			tk = ct.getNextToken(pcx);

			if (this.getCType().getType() == CType.T_int) {
				setCType(CType.getCType(CType.T_int_array));
			} else if (this.getCType().getType() == CType.T_pint) {
				setCType(CType.getCType(CType.T_pint_array));
			}
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
	}
}
