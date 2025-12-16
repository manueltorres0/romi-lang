package ast;

import error.ErrorNode;
import expressions.IExpression;
import expressions.Variable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import tokenizer.Token;
import types.IType;
import types.NumberType;

public class Name extends AbstractNodes {
  private final String name;

  static private final List<String> KEYWORDS = new ArrayList<>(List.of("if0", "while0",
      "block", "def", "=", "==", "/", "+", "class", "method", "isa", "new", "-->", "import", "tmodule", "timport", "module"));

  public Name(Token token) {
    this.name = token.text;
  }

  public Name(String name) {
    this.name = name;
  }

  public Name() {
    this.name = null;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public IExpression convertToExpressionOrError(AtomicBoolean valid) {
    if (KEYWORDS.contains(this.name)) {
      valid.set(false);
      return new ErrorNode(this.toString(), "An Keyword is not a valid expression");
    }

    return new Variable(this);

  }

  public Variable convertToVariableOrError(AtomicBoolean valid) {
    if (KEYWORDS.contains(this.name)) {
      valid.set(false);
      return new ErrorNode(this.toString(), "A keyword is not a expressions.Variable");
    } else {
      return new Variable(this);
    }
  }

  public Variable convertToModuleNameOrError(AtomicBoolean valid) {
    if (this.name.equals("Body")) {
      valid.set(false);
      return new ErrorNode(this.toString(), "Body cannot be a module name");
    }
    return this.convertToVariableOrError(valid);
  }

  @Override
  public IType convertToTypeOrError(AtomicBoolean valid) {
    if (this.name.equals("Number")) {
      return new NumberType();
    } else {
      valid.set(false);
      return new ErrorNode(this.toString(), "Not a valid Type ");
    }
  }


}
