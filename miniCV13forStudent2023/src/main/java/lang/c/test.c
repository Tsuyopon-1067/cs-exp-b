/*
func int plus(int a, int b) {
  int c;
  c = a + b;
  return c;
}

func void main() {
  int b;
  b = plus(1, 3);
}
}*/
func void show3(int n, int* array, int* array2) {
  output array[0];
  output array[1];
  output array[2];

  output array2[2];
}

func int main() {
  int n, array[3], array2[5];
  input n;
  array[0] = 0;
  array[1] = 1;
  array[2] = 2;
  call show3(n, &array, &array2);
  return 0;
}