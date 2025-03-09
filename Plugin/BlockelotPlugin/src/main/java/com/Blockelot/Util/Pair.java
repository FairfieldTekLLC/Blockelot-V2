package com.blockelot.Util;
import java.util.Objects;


//A simple pair class.
public class Pair<A, B> {
    public final A fst;
    public final B snd;

    public Pair(A fst, B snd) {
        this.fst = fst;
        this.snd = snd;
    }

    public A getFst() {
      return fst;
    }

    public B getSnd() {
      return snd;
    }

    @Override
    public String toString() {
        return "(" + fst + ", " + snd + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pair<?, ?> other = (Pair<?, ?>) obj;
        return Objects.equals(fst, other.fst) && Objects.equals(snd, other.snd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fst, snd);
    }
}