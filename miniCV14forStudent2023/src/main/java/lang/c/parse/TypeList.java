package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayList;

import lang.*;
import lang.c.*;

public class TypeList extends CParseRule {
	// call            ::= LPAR RPAR
	private CToken num;
	private String identName;
	private ArrayList<TypeItem> typeItemList;

	public TypeList(CParseContext pcx, String identName) {
		typeItemList = new ArrayList<TypeItem>();
		this.identName = identName;
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_INT;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

		typeItemList.add(new TypeItem(pcx));
		typeItemList.get(typeItemList.size()-1).parse(pcx);
		tk = ct.getCurrentToken(pcx);

		while (tk.getType() == CToken.TK_COMMA) {
			tk = ct.getNextToken(pcx); // ,を読み飛ばす
			if (!TypeItem.isFirst(tk)) {
				pcx.recoverableError(tk.toExplainString() + ",の後ろには引数が必要です");
				continue;
			}
			typeItemList.add(new TypeItem(pcx));
			typeItemList.get(typeItemList.size()-1).parse(pcx);
			tk = ct.getCurrentToken(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
	}

	public ArrayList<TypeItem> getTypeItemList() {
		return typeItemList;
	}
}
