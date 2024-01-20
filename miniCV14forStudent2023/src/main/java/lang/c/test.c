func void fibo(int n, int array[]) {
  int i;
  i = 0;
  while (i <= n) {
    if (i < 2) {
      array[i] = 1;
    } else {
      array[i] = array[i-1] + array[i-2];
    }
    i = i + 1;
  }
}

func int main() {
  int n, array[24]; // f23 = 28657
  input n;
  array[0] = 1;
  array[1] = 1;
  call fibo(n, &array);
  output array[n];
  return 0;
}

