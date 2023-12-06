package lang.c;

import lang.FatalErrorException;
import lang.IOContext;
import lang.RecoverableErrorException;
import lang.c.parse.Program;

public class MiniCompilerImpl {
    void compile(IOContext ioContext) {
        CTokenizer tknz = new CTokenizer(new CTokenRule());
		CParseContext pcx = new CParseContext(ioContext, tknz);
		try {
			CTokenizer ct = pcx.getTokenizer();
			CToken tk = ct.getNextToken(pcx);
			if (Program.isFirst(tk)) {
				CParseRule parseTree = new Program(pcx);
				try {
					parseTree.parse(pcx);									// 構文解析
					//if (pcx.hasNoError()) parseTree.semanticCheck(pcx);		// 意味解析
					parseTree.semanticCheck(pcx);		// 意味解析
				} catch (RecoverableErrorException e) {
				}
				if (pcx.hasNoError()) parseTree.codeGen(pcx);			// コード生成
				pcx.errorReport();
			} else {
				pcx.fatalError(tk.toExplainString() + "プログラムの先頭にゴミがあります");
			}
		} catch (FatalErrorException e) {
			e.printStackTrace();
		}
    }
}
