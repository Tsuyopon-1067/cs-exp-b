void hoge();

func int test() {
  call no_exist_function();
  return 1;
}

func int main() {
  int a, b;
  a = test();
  a = b();
  return 0;
}

func void hoge() {
  int a;
  //a = hoge();
  a = main();
  call main();
  call a();
}

func void return_type_error() {
  return 0;
}

func int no_return() {
}