package lang.c.parse.statement;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.Primary;
import lang.c.parse.PrimaryBlock;

public class StatementInput extends CParseRule {
    // statementInput ::= INPUT LAPR primary RAPR SEMI
	CParseRule primary;

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
			primary = new PrimaryBlock(pcx);
			primary.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "inputの後ろはprimaryBlockです");
		}

		if (tk.getType() != CToken.TK_SEMI) {
			pcx.fatalError(tk.toExplainString() + "文末は;です");
		}
		ct.getNextToken(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (primary != null) {
			primary.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; StatementInput starts");
		if (primary != null) {
			primary.codeGen(pcx);
		}
		o.println(";;; StatementInput completes");
	}
}