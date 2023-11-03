package lang.c;

import lang.SimpleToken;

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



	public static final String TRUE_NUM = "0x0001";
	public static final String FALSE_NUM = "0x0000";

	public CToken(int type, int lineNo, int colNo, String s) {
		super(type, lineNo, colNo, s);
	}
}
