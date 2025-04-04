package lang.c;

import lang.*;

public class CParseContext extends ParseContext {
	public CParseContext(IOContext ioCtx,  CTokenizer tknz) {
		super(ioCtx, tknz);
		symbolTable = new CSymbolTable();
	}

	@Override
	public CTokenizer getTokenizer()		{ return (CTokenizer) super.getTokenizer(); }

	private int seqNo = 0;
	public int getSeqId() { return ++seqNo; }

	private CSymbolTable symbolTable;
	public CSymbolTable getSymbolTable()	{ return symbolTable; }
}
