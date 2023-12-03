package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Variable extends CParseRule {
    //variable ::= ident [array]
	CParseRule ident;
	CParseRule array;

	public Variable(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return Ident.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		ident = new Ident(pcx);
		ident.parse(pcx);
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx);
		if (tk.getType() == CToken.TK_LBRA) {
			array = new Array(pcx);
			array.parse(pcx);
			ct.getNextToken(pcx); // ]を読み飛ばす
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (ident != null) {
			ident.semanticCheck(pcx);
			boolean isIntArray = ident.getCType().isCType(CType.T_int_array);
			boolean isPintArray = ident.getCType().isCType(CType.T_pint_array);
			if (array != null) { // ident arrayの場合
				if (!isIntArray && !isPintArray) {
					pcx.warning("identの型が配列型ではありません");
				}
				array.semanticCheck(pcx);

				if (isIntArray) {
					setCType(CType.getCType(CType.T_int));
				} else if (isPintArray) {
					setCType(CType.getCType(CType.T_pint));
				}
			} else { // identのみの場合
				if (isIntArray || isPintArray) {
					pcx.warning("配列のインデックスが指定されていません");
				}
				setCType(ident.getCType());
			}
			setConstant(ident.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; variable starts");
		if (ident != null) {
			ident.codeGen(pcx);
		}
		if (array != null) {
			array.codeGen(pcx);
		}
		o.println(";;; variable completes");
	}
}
