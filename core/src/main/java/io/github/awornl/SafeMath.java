package io.github.awornl;

public class SafeMath {
    public static long add(long a, long b) {
        long r = a + b;
        if (((a ^ r) & (b ^ r)) < 0) return Long.MAX_VALUE;
        return r;
    }

    public static long multiply(long a, long b) {
        if (a == 0 || b == 0) return 0;
        long r = a * b;
        if (r / a != b) return Long.MAX_VALUE;
        return r;
    }
}
