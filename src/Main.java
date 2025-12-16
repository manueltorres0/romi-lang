import ast.ASTNodes;
import cesk.CESK;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import parser.Parser;
import programs.Program;
import tokenizer.LispTokenizer;
import systemComponents.System;


public class Main {
  public static void main(String[] args) {

    Reader reader = new InputStreamReader(java.lang.System.in);
    LispTokenizer tokenizer = new LispTokenizer(reader);

    System system = parseTokensIntoSystemIfWellFormed(tokenizer);
    printProgramOutput(system);
  }


  private static System parseTokensIntoSystemIfWellFormed(LispTokenizer tokenizer) {
      ASTNodes nodes = new Parser(tokenizer).restructureTokensIntoASTNodes();
      return nodes.convertToSystemOrErrorNode();
  }


  private static void printProgramOutput(System system) {
    if (system.containsError()) {
      java.lang.System.out.print("\"parser error\"");
    } else if (system.containsDuplicateModuleName()) {
      java.lang.System.out.print("\"duplicate module name\"");
    } else if (system.containsClassWithDuplicateMethodFieldOrParamNames()){
      java.lang.System.out.print("\"duplicate method, field, or parameter name\"");
    } else if (system.containsBadImport()) {
      java.lang.System.out.print("\"bad import\"");
    } else if (system.containsUndefinedVariables()) {
      java.lang.System.out.print("\"undeclared variable error\"");
    } else if (system.containsTypeError()) {
      java.lang.System.out.print("\"type error\"");
    } else {
      system.synthesize();
      system.typeIt();
      Program validatedWellFormedAndStrippedAst = system.linkModules();
      CESK.runCSK(validatedWellFormedAndStrippedAst);
    }
  }

}