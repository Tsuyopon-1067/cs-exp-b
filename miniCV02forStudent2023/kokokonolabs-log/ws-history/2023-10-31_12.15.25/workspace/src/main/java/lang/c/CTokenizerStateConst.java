package lang.c;

import lang.SimpleToken;

public class CTokenizerStateConst {
	public static final int ST_INIT		= 0;	// 初期状態
	public static final int ST_EOF		= 1;	// EOF
	public static final int ST_ILL		= 2;	//
	public static final int ST_ZERO_HEX_OCT		= 3;	//
	public static final int ST_PLUS		= 4;	//
	public static final int ST_MINUS		= 5;	//
	public static final int ST_MUL		= 6;	//
	public static final int ST_DIV		= 7;	//
	public static final int ST_AMP		= 11;	//
	public static final int ST_DEC		= 12;	//
}
