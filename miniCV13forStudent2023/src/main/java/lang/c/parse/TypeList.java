package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayList;

import lang.*;
import lang.c.*;

public class TypeList extends CParseRule {
	// call            ::= LPAR RPAR
	CToken num;
	ArrayList<CType> cTypeList;

	public TypeList(CParseContext pcx) {
		cTypeList = new ArrayList<CType>();
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_LPAR;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

		TypeItem typeItem = new TypeItem(pcx);
		typeItem.parse(pcx);
		cTypeList.add(typeItem.getItemCType());

		while (tk.getType() == CToken.TK_COMMA) {
			tk = ct.getNextToken(pcx); // ,を読み飛ばす
			if (!TypeItem.isFirst(tk)) {
				pcx.recoverableError(tk.toExplainString() + ",の後ろには引数が必要です");
				continue;
			}
			typeItem = new TypeItem(pcx);
			typeItem.parse(pcx);
			cTypeList.add(typeItem.getItemCType());
			tk = ct.getCurrentToken(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
	}

	public ArrayList<CType> getCTypeList() {
		return cTypeList;
	}
}
