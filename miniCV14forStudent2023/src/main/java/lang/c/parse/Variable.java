package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Variable extends CParseRule {
    // variable        ::= ident [ array | call ]
	CParseRule ident;
	CParseRule array;
	CParseRule call;
	CToken identToken;
	private boolean isExistCall = false;

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
		CToken tk = ct.getCurrentToken(pcx);
		identToken = tk;

		tk = ct.getNextToken(pcx);
		if (tk.getType() == CToken.TK_LBRA) {
			array = new Array(pcx);
			array.parse(pcx);
			ct.getNextToken(pcx); // ]を読み飛ばす
		} else if (Call.isFirst(tk)) {
			isExistCall = true;
			if (((Ident)ident).getEntry() == null) {
				pcx.recoverableError("Variable: " + identToken.toDetailExplainString() + "は宣言されていません");
			}

			if (((Ident)ident).getEntry().getFunctionInfo() == null) {
				pcx.warning("Variable: " + identToken.toDetailExplainString() + "は関数ではありません");
			}
			call = new Call(pcx, ((Ident)ident).getEntry().getFunctionInfo());
			call.parse(pcx);
			ct.getNextToken(pcx); // )を読み飛ばす
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (ident != null) {
			ident.semanticCheck(pcx);
			this.setCType(ident.getCType());
			boolean isIntArray = ident.getCType().isCType(CType.T_int_array);
			boolean isPintArray = ident.getCType().isCType(CType.T_pint_array);
			boolean isPint = ident.getCType().isCType(CType.T_pint);
			if (array != null) { // ident arrayの場合
				array.semanticCheck(pcx);
				if (isIntArray) {
					setCType(CType.getCType(CType.T_int));
				} else if (isPintArray) {
					setCType(CType.getCType(CType.T_pint));
				} else if (isPint) {
					setCType(CType.getCType(CType.T_int));
				} else {
					String msg = String.format("Variable: identの型は%sです %s", ident.getCType().toString(), identToken.toExplainString());
					pcx.warning(msg);
					setCType(CType.getCType(CType.T_err));
				}
			} else if (ident != null) { // identのみの場合
				if (isPintArray) {
					pcx.warning("Variable: " + identToken.toDetailExplainString() + "配列のインデックスが指定されていません");
				} else {
					setCType(ident.getCType());
				}
			}
			setConstant(ident.isConstant());
			if (this.isConstant()) {
				this.setValue(ident.getValue());
			}

			if (call != null) {
				if (((Ident)ident).getEntry().isFunction()) {
					call.semanticCheck(pcx);
				} else {
					pcx.warning("Variable: " + identToken.toDetailExplainString() + "関数ではありません");
				}
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; variable starts");
		if (isExistCall) {
			// callがついているということはidentが関数
			if (call != null) {
				call.codeGen(pcx); // 先に引数を積む
			}
			if (ident != null) {
				ident.codeGen(pcx); // 関数の場合はJSRが呼び出されるだけ
			}
			CSymbolTableEntry entry = ((Ident)ident).getEntry();
			if (entry != null) {
				int paramSize = 0;
				if (entry.isFunction() && entry.getFunctionInfo() != null) {
					paramSize = entry.getFunctionInfo().getParamSize();
				}
				o.println("\tSUB\t#" + paramSize + ", R6\t; Variable: 引数を降ろす");
				o.println("\tMOV\t#" + entry.getAddress() + ", R1\t; Variable: 関数戻り値取得のためにフレームポインタと変数アドレスの変位を取得<" + identToken.toExplainString() + ">");
			}
			o.println("\tADD\tR4, R1\t; Variable: 変数アドレスを計算する<" + identToken.toExplainString() + ">");
			o.println("\tMOV\tR1, (R6)+\t; Variable: 変数アドレスを積む<" + identToken.toExplainString() + ">");
			o.println("\tMOV\tR0, (R1)\t; Variable: 戻り値を変数に代入する");
		} else {
			if (ident != null) {
				ident.codeGen(pcx);
			}
			if (array != null) {
				array.codeGen(pcx);
			}
		}
		o.println(";;; variable completes");
	}
}
