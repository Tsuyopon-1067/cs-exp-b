package lang.c;


import lang.SimpleToken;
import java.util.HashMap;

public class CToken extends SimpleToken {
	public static final int TK_PLUS		= 2;	// +
	// add chapter1
	public static final int TK_MINUS    = 3;    // -
	// add chapter2
	public static final int TK_AMP		= 4;	// adress &
	// add chapter3
	public static final int TK_MULT     = 5;    // *
	public static final int TK_DIV      = 6;    // ÷
	public static final int TK_LPAR     = 7;	// (
	public static final int TK_RPAR     = 8;    // )
	// add chapter4
	public static final int TK_LBRA     = 9;    // [
	public static final int TK_RBRA     = 10;   // ]
	public static final int TK_IDENT    = 11;   // ident
	// add chapter5
	public static final int TK_ASSIGN 	= 12;	// =
	public static final int TK_SEMI    	= 13;	// ;
	// add chapter6
	public static final int TK_TRUE    	= 14;	// true (ident 経由で識別する)
	public static final int TK_FALSE   	= 15;	// false (ident 経由で識別する)
	public static final int TK_LT     	= 16;	// <
	public static final int TK_GT     	= 17;	// >
	public static final int TK_LE     	= 18;	// <=
	public static final int TK_GE    	= 19;	// >=
	public static final int TK_EQ    	= 20;	// ==
	public static final int TK_NE    	= 21;	// !=
	// add chapter7
	public static final int TK_IF       = 22;	// if (ident 経由で識別する)
	public static final int TK_ELSE     = 23;	// else (ident 経由で識別する)
	public static final int TK_WHILE    = 24;	// while (ident 経由で識別する)
	public static final int TK_INPUT    = 25;	// input (ident 経由で識別する)
	public static final int TK_OUTPUT   = 26;	// output (ident 経由で識別する)

	public static final int TK_LCUR		= 27;	// {
	public static final int TK_RCUR		= 28;	// }
	// add chapter8
	public static final int TK_NOT      = 29;	// !
	public static final int TK_AND      = 30;	// &&
	public static final int TK_OR       = 31;	// ||
	// add chapter10
	public static final int TK_INT      = 32;	// int
	public static final int TK_CONST    = 33;	// const
	public static final int TK_COMMA    = 34;	// ,

	// add chapter 12
	public static final int TK_FUNC    = 35;	// function




	public static final String TRUE_NUM = "0x0001";
	public static final String FALSE_NUM = "0x0000";

	public CToken(int type, int lineNo, int colNo, String s) {
		super(type, lineNo, colNo, s);
	}

	private static final HashMap<Integer, String> CTOKENS = new HashMap<Integer, String>(){
			{
				put(TK_IDENT,"TK_IDENT");
				put(TK_NUM,"TK_NUM");
				put(TK_EOF,"TK_EOF");
				put(TK_ILL,"TK_ILL");
				put(TK_PLUS,"TK_PLUS");

				put(TK_MINUS,"TK_MINUS");

				put(TK_AMP,"TK_AMP");

				put(TK_MULT,"TK_MULT");
				put(TK_DIV,"TK_DIV");
				put(TK_LPAR,"TK_LPAR");
				put(TK_RPAR,"TK_RPAR");

				put(TK_LBRA,"TK_LBRA");
				put(TK_RBRA,"TK_RBRA");

				put(TK_ASSIGN,"TK_ASSIGN");
				put(TK_SEMI,"TK_SEMI");

				put(TK_TRUE,"TK_TRUE");
				put(TK_FALSE,"TK_FALSE");
				put(TK_LT,"TK_LT");
				put(TK_GT,"TK_GT");
				put(TK_LE,"TK_LE");
				put(TK_GE,"TK_GE");
				put(TK_EQ,"TK_EQ");
				put(TK_NE,"TK_NE");

				put(TK_IF,"TK_IF");
				put(TK_ELSE,"TK_ELSE");
				put(TK_WHILE,"TK_WHILE");
				put(TK_INPUT,"TK_INPUT");
				put(TK_OUTPUT,"TK_OUTPUT");

				put(TK_LCUR,"TK_LCUR");
				put(TK_RCUR,"TK_RCUR");

				put(TK_NOT,"TK_NOT");
				put(TK_OR,"TK_OR");
				put(TK_AND,"TK_AND");

				put(TK_INT,"TK_INT");
				put(TK_CONST,"TK_CONST");
				put(TK_COMMA,"TK_COMMA");

				put(TK_FUNC,"TK_FUNC");
			}
		};

		public String toDetailExplainString() {
			String str;
			if (this.getType() == TK_NUM) {
				str = super.toExplainString() + " type=" + getTokenString() + " [" + this.getType() + "] valule=" + this.getIntValue();
			} else {
				str = super.toExplainString() + " type=" + getTokenString() + " [" + this.getType() + "]";
			}
			return str;
		}

		public String getTokenString() {
			return CToken.CTOKENS.get(this.getType());
		}
}
