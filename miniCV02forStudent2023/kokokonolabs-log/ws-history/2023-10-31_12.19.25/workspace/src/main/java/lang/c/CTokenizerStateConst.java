package lang.c;

public class CTokenizerStateConst {
	public static final int ST_INIT         = 0;  // 初期状態
	public static final int ST_EOF          = 1;  // EOF
	public static final int ST_ILL          = 2;  // 変なやつ読んだ
	public static final int ST_ZERO_HEX_OCT	= 3;  // 8進数か16進数の頭の0を読んだ
	public static final int ST_PLUS	        = 4;  // +演算子
	public static final int ST_MINUS        = 5;  // -演算子
	public static final int ST_MUL          = 6;  // *演算子
	public static final int ST_DIV          = 7;  // /演算子
	public static final int ST_AMP          = 11; // &演算子
	public static final int ST_DEC          = 12; // 10進数読んだ
}
