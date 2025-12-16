package tokenizer;

import java.io.StreamTokenizer;

public class Token
{
  public static final int SYMBOL = StreamTokenizer.TT_WORD;
  public int type;
  public String text;
  public int line;
  public double numericVal;

  public Token(StreamTokenizer tzr)
  {
    this.type = tzr.ttype;
    this.text = tzr.sval;
    this.line = tzr.lineno();

    if (this.type == StreamTokenizer.TT_NUMBER) {
      this.numericVal = tzr.nval;
    }
  }

  public String toString()
  {
    switch(this.type)
    {
      case SYMBOL:
      case '"':
        return this.text;
      default:
        return String.valueOf((char)this.type);
    }
  }

  public double getNumericValue(){
    return this.numericVal;
  }
}
