package lang.c.parse.statement;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.Primary;

public class StatementInput extends CParseRule {
    // statementInput ::= INPUT primary SEMI
	CParseRule primary;
	CToken primaryToken;

	public StatementInput(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_INPUT;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx);

		if (Primary.isFirst(tk)) {
			primaryToken = tk;
			primary = new Primary(pcx);
			primary.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "inputの後ろはprimaryです");
		}

		tk = ct.getCurrentToken(pcx);
		if (tk.getType() != CToken.TK_SEMI) {
			pcx.fatalError(tk.toExplainString() + "文末は;です");
		}
		ct.getNextToken(pcx); // ifは次の字句を読んでしまうのでそれに合わせる
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (primary != null) {
			primary.semanticCheck(pcx);
		}
		if (primary.isConstant()) {
			pcx.warning(primaryToken.toExplainString() + "左辺が定数です");
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; StatementInput starts");
		if (primary != null) {
			primary.codeGen(pcx);
			o.println("\tMOV\t-(R6), R0\t; StatementInput: 変数のアドレスを取り出す");
			o.println("\tMOV\t#0xFFE0, R1\t; StatementInput: IOアドレスをR1に確保");
			o.println("\tMOV\t(R1), (R0)\t; StatementInput: 変数に値を代入する");
		}
		o.println(";;; StatementInput completes");
	}
}