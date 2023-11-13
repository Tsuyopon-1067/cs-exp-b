// 16進数
0xffff 0x0 0xfffff  // ビット幅が大きすぎてエラーのはず
    0xffgf  // gのところでエラーのはず ＝＝＞ 0xff を16進， g と f を ILL
            // としてだす もしくは 0xffgf
            // 全体をILLとしても良いがその場合はテストコードを修正すること
    0x  // エラーのはず

    // 8進数
    0472 0 0177777 0277777  // ビット幅が大きすぎてエラーのはず
    01786  // 8のところで8進数ではない＝＝＞ 86 を10進数として出す
           // もしくは01786
           // 全体をILLとしてもよいがその場合はテストコードを修正すること

    // 10進数
    32767  // 16ビット符号付で最大の整数
    32768  // 16ビット符号付では表現できないが -32768
           // を後で解釈できるためにスルーする
    32769  // これはOverflowとする
    123a4  // aのところでエラーのはず 123 を10進数としてだし，aをILL, 4
           // を10進として出す 123a4
           // 全体をILLとしてもよいがその場合はテストコードを修正すること

    // CTokenizeAddressTest.java -----------------------------------------------
    // & が字句として認識されるか？　 2021/10/25追加
    & 100 & &0472 & 0xffe0