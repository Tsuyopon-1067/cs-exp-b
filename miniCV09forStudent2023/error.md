# エラー仕様書

## condition

-   expression の次が不正
    -   recoverableError: expression の後ろは条件演算子です

### Condition

## statement

### StatementAssign

-   ;がないとき

    -   warning ;がありません

-   型の不一致
    -   warning: 左辺の型[" + primary.getCType().toString()"]と右辺の型[" + expression.getCType().toString() + "]が異なります"
-   代入先が定数
    -   warning: 左辺が定数です
-   Expressin のエラーが起きた
    -   文末または;までスキップしました

### StatementBlock

-   }が閉じていないとき
    -   recoverableError: }が閉じていません

### StatementIf

-   if の後で ConditionBlock.isFirst を満たしていない

    -   recoverableError: if の後ろは conditionBlock です

-   ConditionBlock の後で Statement.isFirst を満たしていない

    -   recoverableError: if ブロックの中は statement です

-   else の後で Statement.isFirst を満たしていない
    -   recoverableError: else ブロックの中は statement です

### StatementInput

-   input の後ろが primary ではない
    -   warning: input の後ろは primary です
    -   warning: primary をスキップしました
-   ;が抜けている
    -   recoverableError: 文末は;です
-   引数が定数のとき
    -   warning: 引数が定数です

### StatementOutput

-   ;が抜けている
    -   recoverableError: 文末は;です

### StatementWhile

-   while の後ろに conditionBlock がない
    -   while の後ろは conditionBlock です
-   conditionBlock の後ろに statement がない
    -   while ブロックの中は statement です
-   ;が抜けている
    -   recoverableError: while の後ろは conditionBlock です

### AbstractConditionOperator

-   比較演算子の後で ConditionBlock.isFirst を満たしていない
    -   recoverableError: 比較演算子の後ろは expression です
-   型が不正
    -   左辺の型[" + left.getCType().toString() + "]と右辺の型[" + right.getCType().toString() + "]は比較できません

### Array

-   Expression.isFirst を満たしていない
    -   recoverableError: [の後ろは expression です
-   添字が int でない
    -   warning: 配列のインデックスは int である必要があります

### BitNotFactor

-   !の右が bool でない
    -   warning: !の右は T_bool です

### BitExpressionOr

-   ||の左辺と右辺が bool でない
    -   warning: ||の左辺と右辺は T_bool である必要があります

### BitTermAnd

-   &&の左辺と右辺が bool でない
    -   warning: &&の左辺と右辺は T_bool である必要があります

### Expression

-   ExpressionAdd か ExpressionSub で recoverableError
    -   warning: ExpressionAdd または ExpressionSub をスキップしました

### ExpressionAdd

-   +の後ろが Term ではない

    -   recoverableError: +の後ろは term です

-   型が不正
    -   "左辺の型[" + left.getCType().toString() + "]と右辺の型[" + right.getCType().toString() + "]は足せません

### ExpressionSub

-   +の後ろが Term ではない

    -   +の後ろは term です

-   型が不正
    -   "左辺の型[" + left.getCType().toString() + "]と右辺の型[" + right.getCType().toString() + "]は引けません

### FactorAmp

-   &の後ろが Number か Primary でない
    -   &の後は Number か Primary です

### Ident

-   変数名の最初が型名を表していない
    -   pcx.warning("変数は i*，ip*，ia*，ipa*，c\_のどれかから始まる必要があります");

### MinusFactor

-   番地を表す値に-がついている
    -   ポインタに符号(-)はつけられません

### PrimaryMult

-   ポインタ出ない数値に対して\*がついている

    -   ポインタではない数値でアドレスを参照することはできません

### Term

-   TermMult か TermDiv で recoverableError
    -   warning: ExpressionAdd または ExpressionSub をスキップしました

### TermDiv

-   \*の後ろが Factor ではない

    -   recoverableError: \*の後ろは factor です

-   型が不正
    -   warning:左辺の型[" + left.getCType().toString() + "]と右辺の型[" + right.getCType().toString() + "]は割れません

### TermMult

-   \/の後ろが Factor ではない

    -   recoverableError: \/の後ろは factor です

-   型が不正

    -   warning:左辺の型[" + left.getCType().toString() + "]と右辺の型[" + right.getCType().toString() + "]は掛けられません

### Variable

-   配列ではない変数の後ろに添え字がある
    -   ident の型が配列型ではありません
-   配列なのに添字がない
    -   配列のインデックスが指定されていません
