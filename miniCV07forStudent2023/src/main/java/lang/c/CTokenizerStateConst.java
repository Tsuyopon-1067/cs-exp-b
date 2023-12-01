package lang.c;

public class CTokenizerStateConst {
	public static final int ST_INIT              = 0;  // 初期状態
	public static final int ST_EOF               = 1;  // EOF
	public static final int ST_ILL               = 2;  // 変なやつ読んだ
	public static final int ST_ZERO_HEX_OCT	     = 3;  // 8進数か16進数の頭の0を読んだ
	public static final int ST_OCT	             = 13; // 8進数
	public static final int ST_HEX_INIT	         = 14; // 16進数の開始
	public static final int ST_HEX	             = 15; // 16進数
	public static final int ST_DEC               = 12; // 10進数読んだ
	public static final int ST_PLUS	             = 4;  // +演算子
	public static final int ST_MINUS             = 5;  // -演算子
	public static final int ST_MUL               = 6;  // *演算子
	//public static final int ST_DIV             = 7;  // /演算子
	public static final int ST_SLASH             = 7;  // /単体
	public static final int ST_SLASH_COMMENT     = 8;  // //を読んだ
	public static final int ST_SLASH_ASTAR       = 9;  // /*を読んだ
	public static final int ST_SLASH_ASTAR_ASTER = 10;  // /* --- *を読んだ
	public static final int ST_AMP               = 11; // &演算子
	public static final int ST_LPAR              = 16; // (
	public static final int ST_RPAR              = 17; // )
	public static final int ST_LBRA              = 18; // [
	public static final int ST_RBRA              = 19; // ]
	public static final int ST_IDENT             = 20; // ident
	public static final int ST_SEMI              = 22; // ;
	public static final int ST_LT                = 23; // <
	public static final int ST_GT                = 24; // >
	public static final int ST_EQ                = 25; // =
	public static final int ST_NE                = 26; // !
	public static final int ST_LBRACE            = 27; // {
	public static final int ST_RBRACE            = 28; // }
}
