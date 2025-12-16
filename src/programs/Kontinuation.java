package programs;

import Control.*;
import Maps.*;
import block.Block;
import cesk.CESK;

import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Stack;
import Class.IClass;
import Closure.*;
import types.IType;

/**
 * Represents the kontinuation of a CESK machine, where the top-most Closure of the stack is the next
 * instruction to be executed by the CESK machine.
 */
public class Kontinuation {
  Stack<ClosureOrReturnType> instructions;

  public Kontinuation(Block block, Environment env) {
    instructions = new Stack<ClosureOrReturnType>();
    Closure programAST = new Closure(env, block);
    instructions.push(programAST);
  }

  public void pop() {
    instructions.pop();
  }

  public void push(ClosureOrReturnType closure) {
    instructions.push(closure);
  }

  public IControl findExpression(CESK cesk) {
    return instructions.peek().findNextExpression(this, cesk);
  }

  public IControl evaluateStatementOrDeclaration(Environment env, Store store, IControl control, List<IClass> classes) {
    ClosureOrReturnType nextInstruction = instructions.peek();
    if (nextInstruction.isReturnType()) {
      return conformReturnType(control, nextInstruction);
    }
    return nextInstruction.evaluateStatementOrDeclaration(env, store, control, classes);
  }

  private IControl conformReturnType(IControl control, ClosureOrReturnType nextInstruction) {
    Optional<ProxyOrValue> conformedReturn =
        Utils.Utils.conforms(control.getValue(), (IType) nextInstruction);
    if (conformedReturn.isEmpty()) {
      return new ErrorControl();
    } else {
      this.pop();
      return new ValueControl(conformedReturn.get());
    }
  }

  public boolean isEmpty() {
    return instructions.isEmpty();
  }

  public Environment topMostEnv() {
    ListIterator<ClosureOrReturnType> iterator = instructions.listIterator(instructions.size());
    while (iterator.hasPrevious()) {
      ClosureOrReturnType element = iterator.previous();
      if (!element.isReturnType()) {
        return element.getEnv();
      }
    }
    throw new IllegalStateException("No closure was found on the stack");
  }


  public int size() {
    return instructions.size();
  }

  public boolean doesNotContainClosures() {
    return !Utils.
        Utils.hasElementMatching(ins -> !ins.isReturnType(), instructions);
  }
}
