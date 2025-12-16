package Utils;

import java.util.Objects;

import expressions.Variable;

//public record Pair(Variable moduleName, Variable className) {}
public class Pair <T,V> {
  T lhs;
  V rhs;
  public Pair (T lhs, V rhs) {
    this.lhs = lhs;
    this.rhs = rhs;
  }

  public T moduleName() {
    return lhs;
  }
  public V className() {
    return rhs;
  }

  @Override
  public int hashCode() {
    return Objects.hash(lhs, rhs);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Pair pair)) {
      return false;
    }
    return this.lhs == pair.lhs && this.rhs == pair.rhs;
  }

}