func int fibo(int n) {
  if (n == 0 || n == 1) {
    return 1;
  } else {
    return n + fibo(n-1);
  }
}

func int main() {
  int n, ans;
  input n;
  ans = fibo(n);
  output ans;
  return 0;
}