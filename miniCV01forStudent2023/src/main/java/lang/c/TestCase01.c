// Testcase of "cv01". Add each Testcase to Test files.

// CTokenizerNumberTest_cv01.java --------------------------------------------
-100    // 実装してください

// CTokenizerExpressionSubTest.java --------------------------------------------
13 + 7 - 2  --> readSimpleTokenSub()

// CTokenizeCommentTest.java --------------------------------------------
"100 // 数字"               --> inlineCommentWithNumber()
"+ - // plus minus 記号"    --> inlineCommnetWithFactor()
"// LINE_COMMENT\n" +
" 13 + 7 + 2 "              --> lineComment()
"/***/123/*/12/*/34/*/56/*/78    // 123 34 78 が出てくるはず"
                        └─> blockCommentWithFactor()
"/***         // 閉じていないコメントはEOFが出るはず"
                        └─> blockCommentError()
"/* COMMENT_START AND COMMENT_LINE1\n" +
"   COMMENT_LINE2\n" +
"   COMMENT_LINE3\n" +
"   COMMENT_LINE4 AND COMMENT_END */\n"
" 13 + 7 + 2 "              --> blockComment()

// IsFirstTest_cv01.java ---------------------------------------------------
3-1 --> testExpressionSub()

// ParseExpressionTest.java ------------------------------------------------
// すべて不当
1+  --> parseErrorNumPlusNone()
-3  --> parseErrorNoneSubNum()
3-  --> parseErrorNumMinusNone()
+   --> parseErrorOnlyPlus()

// SemanticCheckProgramTest.java -------------------------------------------
13 - 2  --> ExpressionSubNoError()

// CodeGenExpressionTest.java -------------------------------------------
// 以下のテストケースを実装してください
7-2
13-7+3
13+7-3
13+7+3
13+/*comment*/7+3
// +4 や -5 がちゃんと認識＋コードが出ていることを確認する
1+2-3+4-5