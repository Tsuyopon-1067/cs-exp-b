package lang.c;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import lang.*;

public class CTokenizer extends Tokenizer<CToken, CParseContext> {
	@SuppressWarnings("unused")
	private CTokenRule rule;
	private int lineNo, colNo;
	private char backCh;
	private boolean backChExist = false;

	public CTokenizer(CTokenRule rule) {
		this.rule = rule;
		lineNo = 1;
		colNo = 1;
	}

	private InputStream in;
	private PrintStream err;

	private char readChar() {
		char ch;
		if (backChExist) {
			ch = backCh;
			backChExist = false;
		} else {
			try {
				ch = (char) in.read();
			} catch (IOException e) {
				e.printStackTrace(err);
				ch = (char) -1;
			}
		}
		++colNo;
		if (ch == '\n') {
			colNo = 1;
			++lineNo;
		}
		// System.out.print("'"+ch+"'("+(int)ch+")");
		return ch;
	}

	private void backChar(char c) {
		backCh = c;
		backChExist = true;
		--colNo;
		if (c == '\n') {
			--lineNo;
		}
	}

	// 現在読み込まれているトークンを返す
	private CToken currentTk = null;

	public CToken getCurrentToken(CParseContext pctx) {
		return currentTk;
	}

	// 次のトークンを読んで返す
	public CToken getNextToken(CParseContext pctx) {
		in = pctx.getIOContext().getInStream();
		err = pctx.getIOContext().getErrStream();
		currentTk = readToken();
		return currentTk;
	}

	private CToken readToken() {
		CToken tk = null;
		char ch;
		int startCol = colNo;
		StringBuffer text = new StringBuffer();

		int state = CTokenizerStateConst.ST_INIT;
		boolean accept = false;
		while (!accept) {
			switch (state) {
				case CTokenizerStateConst.ST_INIT: // 初期状態
					ch = readChar();
					if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
					} else if (ch == (char) -1) { // EOF
						startCol = colNo - 1;
						state = CTokenizerStateConst.ST_EOF;
					} else if (ch == '0') {
						startCol = colNo - 1;
						text.append(ch);
						state = CTokenizerStateConst.ST_ZERO_HEX_OCT;
					} else if (ch >= '1' && ch <= '9') {
						startCol = colNo - 1;
						text.append(ch);
						state = CTokenizerStateConst.ST_DEC;
					} else if (ch == '+') {
						startCol = colNo - 1;
						text.append(ch);
						state = CTokenizerStateConst.ST_PLUS;
					} else if (ch == '-') {
						startCol = colNo - 1;
						text.append(ch);
						state = CTokenizerStateConst.ST_MINUS;
					} else if (ch == '*') {
						startCol = colNo - 1;
						text.append(ch);
						state = CTokenizerStateConst.ST_MUL;
					} else if (ch == '/') {
						startCol = colNo - 1;
						text.append(ch);
						state = CTokenizerStateConst.ST_SLASH;
					} else if (ch == '&') {
						startCol = colNo - 1;
						text.append(ch);
						state = CTokenizerStateConst.ST_AMP;
					} else if (ch == '(') {
						startCol = colNo - 1;
						text.append(ch);
						state = CTokenizerStateConst.ST_LPAR;
					} else if (ch == ')') {
						startCol = colNo - 1;
						text.append(ch);
						state = CTokenizerStateConst.ST_RPAR;
					} else if (ch == '[') {
						startCol = colNo - 1;
						text.append(ch);
						state = CTokenizerStateConst.ST_LBRA;
					} else if (ch == ']') {
						startCol = colNo - 1;
						text.append(ch);
						state = CTokenizerStateConst.ST_RBRA;
					} else if (('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z') || ch == '_') {
						startCol = colNo - 1;
						text.append(ch);
						state = CTokenizerStateConst.ST_IDENT;
					} else if (ch == '=') {
						startCol = colNo - 1;
						text.append(ch);
						state = CTokenizerStateConst.ST_EQ;
					} else if (ch == ';') {
						startCol = colNo - 1;
						text.append(ch);
						state = CTokenizerStateConst.ST_SEMI;
					} else if (ch == '<') {
						startCol = colNo - 1;
						text.append(ch);
						state = CTokenizerStateConst.ST_LT;
					} else if (ch == '>') {
						startCol = colNo - 1;
						text.append(ch);
						state = CTokenizerStateConst.ST_GT;
					} else if (ch == '!') {
						startCol = colNo - 1;
						text.append(ch);
						state = CTokenizerStateConst.ST_NE;
					} else if (ch == '{') {
						startCol = colNo - 1;
						text.append(ch);
						state = CTokenizerStateConst.ST_LCUR;
					} else if (ch == '}') {
						startCol = colNo - 1;
						text.append(ch);
						state = CTokenizerStateConst.ST_RCUR;
					} else { // ヘンな文字を読んだ
						startCol = colNo - 1;
						text.append(ch);
						state = CTokenizerStateConst.ST_ILL;
					}
					break;
				case CTokenizerStateConst.ST_EOF: // EOFを読んだ
					tk = new CToken(CToken.TK_EOF, lineNo, startCol, "end_of_file");
					accept = true;
					break;
				case CTokenizerStateConst.ST_ILL: // ヘンな文字を読んだ
					tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
					accept = true;
					break;
				case CTokenizerStateConst.ST_DEC: // 数（10進数）の開始
					ch = readChar();
					if (Character.isDigit(ch)) {
						text.append(ch);
					} else {
						// 数の終わり
						backChar(ch); // 数を表さない文字は戻す（読まなかったことにする）
						tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
						if (tk.getIntValue() > 32768) {
							tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
						}
						accept = true;
					}
					break;
				case CTokenizerStateConst.ST_ZERO_HEX_OCT: // 8進数か16進数の頭の0を読んだ
					ch = readChar();
					if (Character.isDigit(ch)) {
						text.append(ch);
						state = CTokenizerStateConst.ST_OCT;
					} else if (ch == 'x') {
						text.append(ch);
						state = CTokenizerStateConst.ST_HEX_INIT;
					} else {
						// このときは0として受理
						backChar(ch); // 数を表さない文字は戻す（読まなかったことにする）
						tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
						accept = true;
					}
					break;
				case CTokenizerStateConst.ST_OCT: // 数（8進数）の開始
					ch = readChar();
					if ('0' <= ch && ch <= '7') {
						text.append(ch);
					} else {
						// 数の終わり
						backChar(ch); // 数を表さない文字は戻す（読まなかったことにする）
						tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
						if (tk.getIntValue() > 65535) {
							tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
						}
						accept = true;
					}
					break;
				case CTokenizerStateConst.ST_HEX_INIT: // 数（16進数）の開始
					ch = readChar();
					if (Character.isDigit(ch) || ('a' <= ch && ch <= 'f') || 'A' <= ch && ch <= 'F') {
						state = CTokenizerStateConst.ST_HEX;
						text.append(ch);
					} else {
						state = CTokenizerStateConst.ST_ILL;
					}
					break;
				case CTokenizerStateConst.ST_HEX: // 数（16進数）
					ch = readChar();
					if (Character.isDigit(ch) || ('a' <= ch && ch <= 'f') || 'A' <= ch && ch <= 'F') {
						text.append(ch);
					} else {
						// 数の終わり
						backChar(ch); // 数を表さない文字は戻す（読まなかったことにする）
						tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
						if (tk.getIntValue() > 65535) {
							tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
						}
						accept = true;
					}
					break;
				case CTokenizerStateConst.ST_PLUS: // +を読んだ
					tk = new CToken(CToken.TK_PLUS, lineNo, startCol, "+");
					accept = true;
					break;
				case CTokenizerStateConst.ST_MINUS: // -を読んだ
					tk = new CToken(CToken.TK_MINUS, lineNo, startCol, "-");
					accept = true;
					break;
				case CTokenizerStateConst.ST_MUL: // *を読んだ
					tk = new CToken(CToken.TK_MULT, lineNo, startCol, "*");
					accept = true;
					break;
				case CTokenizerStateConst.ST_SLASH: // /を読んだ
					ch = readChar();
					text.append(ch);
					if (ch == '/') {
						state = CTokenizerStateConst.ST_SLASH_COMMENT;
					} else if (ch == '*') {
						state = CTokenizerStateConst.ST_SLASH_ASTAR;
					} else {
						backChar(ch); // 読んだ文字を戻す（読まなかったことにする）
						tk = new CToken(CToken.TK_DIV, lineNo, startCol, "/");
						accept = true;
					}
					//tk = new CToken(CToken.TK_MINUS, lineNo, startCol, "-");
					//accept = true;
					break;
				case CTokenizerStateConst.ST_SLASH_COMMENT: // //を読んでからコメントの途中
					ch = readChar();
					if (ch == '\n') { // 改行でコメントおわり
						text = new StringBuffer();
						state = CTokenizerStateConst.ST_INIT;
					} else if (ch == (char)-1) {
						accept = true;
						tk = new CToken(CToken.TK_EOF, lineNo, startCol, "end_of_file");
					}
					// 他のコメント文字は無視
					break;
				case CTokenizerStateConst.ST_SLASH_ASTAR: /* コメントの途中 */
					ch = readChar();
					if (ch == '*') { // 終わる体制に入るよん
						state = CTokenizerStateConst.ST_SLASH_ASTAR_ASTER;
					} else if (ch == (char)-1) {
						tk = new CToken(CToken.TK_EOF, lineNo, startCol, "end_of_file");
						accept = true;
						break;
					}
					// 他のコメント文字
					break;
				case CTokenizerStateConst.ST_SLASH_ASTAR_ASTER: /* コメントのおわりがけ */
					ch = readChar();
					if (ch == '/') { // /でコメントおわり
						text = new StringBuffer();
						state = CTokenizerStateConst.ST_INIT;
					} else if (ch == '*') {
						state = CTokenizerStateConst.ST_SLASH_ASTAR_ASTER;
					} else if (ch == (char)-1) {
						tk = new CToken(CToken.TK_EOF, lineNo, startCol, "end_of_file");
						accept = true;
						break;
					} else {
						state = CTokenizerStateConst.ST_SLASH_ASTAR;
					}
					break;
				case CTokenizerStateConst.ST_AMP: // &を読んだ
					tk = new CToken(CToken.TK_AMP, lineNo, startCol, "&");
					accept = true;
					break;
				case CTokenizerStateConst.ST_LPAR:
					tk = new CToken(CToken.TK_LPAR, lineNo, startCol, "(");
					accept = true;
					break;
				case CTokenizerStateConst.ST_RPAR:
					tk = new CToken(CToken.TK_RPAR, lineNo, startCol, ")");
					accept = true;
					break;
				case CTokenizerStateConst.ST_LBRA:
					tk = new CToken(CToken.TK_LBRA, lineNo, startCol, "[");
					accept = true;
					break;
				case CTokenizerStateConst.ST_RBRA:
					tk = new CToken(CToken.TK_RBRA, lineNo, startCol, "]");
					accept = true;
					break;
				case CTokenizerStateConst.ST_IDENT: // 識別子を読んだ
					startCol = colNo - 1;
					while (true) {
						ch = readChar();
						if (('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z') || ('0' <= ch && ch <= '9') || ch == '_') {
							text.append(ch);
						} else {
							backChar(ch); // 読んだ文字を戻す（読まなかったことにする）
							break;
						}
					}
					// ここから予約後 or Identの判定をする
					Integer i = (Integer) rule.get(text.toString());
					if (i != null) { // 予約後
						tk = new CToken(i.intValue(), lineNo, startCol, text.toString());
					} else { // 変数名
						tk = new CToken(CToken.TK_IDENT, lineNo, startCol, text.toString());
					}
					accept = true;
					break;
				case CTokenizerStateConst.ST_SEMI:
					tk = new CToken(CToken.TK_SEMI, lineNo, startCol, ";");
					accept = true;
					break;
				case CTokenizerStateConst.ST_LT:
					ch = readChar();
					if (ch == '=') {
						text.append(ch);
						tk = new CToken(CToken.TK_LE, lineNo, startCol, text.toString());
					} else {
						tk = new CToken(CToken.TK_LT, lineNo, startCol, text.toString());
						backChar(ch); // 読んだ文字を戻す（読まなかったことにする）
					}
					accept = true;
					break;
				case CTokenizerStateConst.ST_GT:
					ch = readChar();
					if (ch == '=') {
						text.append(ch);
						tk = new CToken(CToken.TK_GE, lineNo, startCol, text.toString());
					} else {
						tk = new CToken(CToken.TK_GT, lineNo, startCol, text.toString());
						backChar(ch); // 読んだ文字を戻す（読まなかったことにする）
					}
					accept = true;
					break;
				case CTokenizerStateConst.ST_EQ:
					ch = readChar();
					if (ch == '=') {
						text.append(ch);
						tk = new CToken(CToken.TK_EQ, lineNo, startCol, text.toString());
					} else {
						backChar(ch);
						tk = new CToken(CToken.TK_ASSIGN, lineNo, startCol, text.toString());
					}
					accept = true;
					break;
				case CTokenizerStateConst.ST_NE:
					ch = readChar();
					if (ch == '=') {
						text.append(ch);
						tk = new CToken(CToken.TK_NE, lineNo, startCol, text.toString());
					} else {
						backChar(ch);
						tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
					}
					accept = true;
					break;
				case CTokenizerStateConst.ST_LCUR:
					tk = new CToken(CToken.TK_LCUR, lineNo, startCol, "{");
					accept = true;
					break;
				case CTokenizerStateConst.ST_RCUR:
					tk = new CToken(CToken.TK_RCUR, lineNo, startCol, "}");
					accept = true;
					break;
			}
		}
		return tk;
	}
}
