package lang.c.parse.statement;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Statement extends CParseRule {
    // statement ::= statementAssign | statementInput | statementOutput | statementIf | statementWhile | statementBlock
    CParseRule statmentAssign;

	public Statement(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
        return StatementAssign.isFirst(tk)
			|| StatementInput.isFirst(tk)
			|| StatementOutput.isFirst(tk)
			|| StatementIf.isFirst(tk)
			|| StatementWhile.isFirst(tk)
			|| StatementBlock.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
		ct.getCurrentToken(pcx);
		statmentAssign = new StatementAssign(pcx);
		statmentAssign.parse(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (statmentAssign != null) {
			statmentAssign.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; Statement starts");
		statmentAssign.codeGen(pcx);
		o.println(";;; Statement completes");
	}
}