#include <stddef.h>
#include <stdio.h>

int main() {
    int a = 3;
    int b = 4;
    int *pa = &a;
    int *pb = &b;

    int *t1 = pa + a;
    int *t2 = pa - a;

    // err ptrdiff_t t3 = pa + pb;
    ptrdiff_t t4 = pa - pb;

    int *t5 = a + pa;
    // err int *t6 = a - pa;

    int t7 = a + b;
    int t8 = a - b;

    printf("ok\n");
}