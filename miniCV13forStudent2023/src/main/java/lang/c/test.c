int proto_correct(int, int*);
int proto_error(int, int*);

func int test(int a, int b) {
  int c;
  c = a + b;
  return c;
}

func int main() {
  int a, b;
  //a = test();
  //a = test(a, b);
  //a = test(a, &1);
  //a = b();
  call test(a, b);
  call test(1, 2);
  call test(a, &b);
  return 0;
}

func int proto_correct(int a, int *b) {
  return 0;
}

func int proto_error(int a, int b) {
  return 0;
}

func int test(int a, int b) {
  return 0;
}

func int test(int* a, int b) {
  return 0;
}