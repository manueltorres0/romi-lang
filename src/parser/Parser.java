package parser;

import ast.ASTNodes;
import ast.Name;
import ast.Sequence;
import java.io.StreamTokenizer;
import tokenizer.LispTokenizer;
import tokenizer.Token;

public class Parser {
  private final LispTokenizer tokenizer;

  public Parser(LispTokenizer tokenizer) {
    this.tokenizer = tokenizer;
  }


  public ASTNodes restructureTokensIntoASTNodes() {
    Token nextToken = tokenizer.peekToken();

    switch (nextToken.type) {
      case StreamTokenizer.TT_WORD:
        try {
          double num = Double.parseDouble(nextToken.text);
          return new ast.Number(num);
        } catch (NumberFormatException e) {
          return new Name(nextToken);
        }
      default:
        tokenizer.next();
        return new Parser(tokenizer).restructureRecursiveSequenceOfTokens();

    }
  }


  public ASTNodes restructureRecursiveSequenceOfTokens() {
    Sequence seq = new Sequence();
    Token nextToken = tokenizer.peekToken();

    while (!nextToken.toString().equals(")")) {

      switch (nextToken.type) {

        case StreamTokenizer.TT_WORD:
          try {
            double num = Double.parseDouble(nextToken.text);
            seq.add(new ast.Number(num));
          } catch (NumberFormatException e) {
            seq.add(new Name(nextToken));
          }
          tokenizer.next();
          break;
        default:
          tokenizer.next();
          seq.add(new Parser(tokenizer).restructureRecursiveSequenceOfTokens());
          break;
      }
      nextToken = tokenizer.peekToken();
      if (!tokenizer.hasNext()) {
        break;
      }

    }
    tokenizer.next();
    return seq;

  }


}

