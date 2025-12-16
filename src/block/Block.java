package block;

import Closure.Closure;
import Control.*;
import Maps.Environment;
import Maps.Location;
import Maps.ProxyOrValue;
import Maps.Store;
import Utils.ModuleClassBinding;
import Utils.Utils;
import ast.ASTNodes;
import cesk.CESK;
import declarations.IDeclaration;
import error.ErrorNode;
import error.TypeError;
import expressions.IExpression;
import expressions.Variable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import Class.*;
import programs.Kontinuation;
import programs.IStatementOrNestedBlock;
import types.IType;
import types.Shape;

/**
 * Represents a block of a program which can be at the top level, or nested.
 */
public class Block implements IBlock {
  public final List<IDeclaration> declarations;
  public final List<IStatementOrNestedBlock> stmts;
  public Optional<IExpression> expression;

  public Block(List<ASTNodes> declarationsAndStmts, AtomicBoolean valid) {
    declarationsAndStmts.removeFirst();
    this.declarations = parseDefs(declarationsAndStmts, valid);
    this.stmts = parseStatements(declarationsAndStmts, valid);
    this.expression = Optional.empty();

  }

  public Block(List<ASTNodes> declarationsAndStmts, ASTNodes expression, AtomicBoolean valid) {
    this.declarations = parseDefs(declarationsAndStmts, valid);
    this.stmts = parseStatements(declarationsAndStmts, valid);
    this.expression = Optional.of(expression.convertToExpressionOrError(valid));

  }

  private static List<IDeclaration> parseDefs(List<ASTNodes> defList, AtomicBoolean validGrammar) {
    List<IDeclaration> processedDefs = new ArrayList<>();
    if  (!defList.isEmpty()) {
      ASTNodes firstDef = defList.getFirst();
      while (!firstDef.isStatement()) {
        if (firstDef.isDeclaration()) {
          processedDefs.add(firstDef.convertToDeclarationOrError(validGrammar));
        } else if (firstDef.isClass()) {
          validGrammar.set(false);
          processedDefs.add(new ErrorNode(firstDef.toString(), "A class" +
              " should not go after a def"));
        } else {
          validGrammar.set(false);
          processedDefs.add(new ErrorNode(firstDef.toString(), "A def does" +
              " not follow this pattern"));
        }
        defList.removeFirst();
        if (defList.isEmpty()) {
          break;
        } else {
          firstDef = defList.getFirst();
        }
      }
    }
    return processedDefs;
  }

  private static List<IStatementOrNestedBlock> parseStatements(List<ASTNodes> stmtList,
                                                               AtomicBoolean validGrammar) {
    List<IStatementOrNestedBlock> processedStatements = new ArrayList<>();
    for (ASTNodes node : stmtList) {
      if (node.isStatement()) {
        processedStatements.add(node.convertToStmtOrError(validGrammar));
      } else if (node.isClass()) {
        validGrammar.set(false);
        processedStatements.add(new ErrorNode(node.toString(), "A class" +
            " should not go after a statement"));
      } else if (node.isDeclaration()) {
        validGrammar.set(false);
        processedStatements.add(new ErrorNode(node.toString(), "A def" +
            " should not go after a statement"));
      } else {
        validGrammar.set(false);
        processedStatements.add(new ErrorNode(node.toString(), "A stmt does" +
            " not follow this pattern"));
      }
    }
    return processedStatements;
  }


  public Block(List<IDeclaration> declarations, List<? extends IStatementOrNestedBlock> stmts, Optional<IExpression> expression) {
    List<IDeclaration> declarationsCopy = new ArrayList<>();
    Utils.voidMap(declarations, decl -> declarationsCopy.add(decl.copyDecl()));
    this.declarations = declarationsCopy;
    this.stmts = new ArrayList<>();
    Utils.voidMap(stmts,
        statementOrBlock -> this.stmts.add(statementOrBlock.copy()));
    if (expression.isPresent()) {
      this.expression = Optional.of(expression.get().copyExpr());
    } else {
      this.expression = Optional.empty();
    }

  }


  @Override
  public boolean containsUndefinedVariables(Set<Variable> definedVariables,
                                            Set<Variable> definedClassNames) {

    boolean undefined = false;

    HashSet<Variable> definedVariablesInBlock =  new HashSet<>(definedVariables);
    undefined = Utils.hasElementMatching(
        declaration -> declaration.containsUndefinedVariables(definedVariablesInBlock, definedClassNames),
        this.declarations);

    undefined |= Utils.hasElementMatching(
        statement -> statement.containsUndefinedVariables(definedVariablesInBlock, definedClassNames),
        this.stmts);

    if (expression.isPresent()) {
      undefined |= expression.get().containsUndefinedVariables(definedVariablesInBlock, definedClassNames);
    }
    return undefined;
  }

  @Override
  public Block copy() {
    return new Block(declarations, stmts,  expression);
  }

  @Override
  public IControl findNextExpression(Kontinuation k, CESK cesk) {
    Store s = cesk.store;
    if (!this.declarations.isEmpty()) {
      IDeclaration firstDef = this.declarations.getFirst();
      return firstDef.findRHS();
    } else if (this.stmts.isEmpty() && this.expression.isEmpty()) {
      k.pop();
      cesk.env = k.topMostEnv();
      return new Search();
    } else if (this.stmts.isEmpty() && this.expression.isPresent()) {
      return this.properTailCall(k, cesk, s);
    } else if (this.stmts.getFirst().isNestedBlock()) {
      return this.enterNestedBlock(k, cesk);
    } else {
      IStatementOrNestedBlock firstStmt = this.stmts.getFirst();
      return firstStmt.findNextExpression(k, cesk);
    }
  }

  private IControl enterNestedBlock(Kontinuation k, CESK cesk) {
    IStatementOrNestedBlock nestedBlock = this.stmts.removeFirst();
    Environment envForBlock = new Environment(cesk.env);
    Closure nestedBlockClosure = new Closure(envForBlock, nestedBlock.getNestedBlockOrThrow());
    k.push(nestedBlockClosure);
    cesk.env = envForBlock;
    return new Search();
  }

  private IControl properTailCall(Kontinuation k, CESK cesk, Store s) {
    IExpression expression = this.expression.get();
    List<Variable> rVariables = expression.getVariables();
    if (rVariables.isEmpty()) {
      k.pop();
      extendEnvironmentOutsideMethodCall(cesk, List.of(), List.of());
      return new ExpressionControl(expression);
    }

    List<ProxyOrValue> variableValues = findVariableValues(rVariables, k, s);
    IExpression renamedExpression = expression.renameVariables(cesk.env.keySet());
    List<Variable> renamedVariables = renamedExpression.getVariables();
    k.pop();
    extendEnvironmentOutsideMethodCall(cesk, renamedVariables, variableValues);

    return new ExpressionControl(renamedExpression);
  }



  private void extendEnvironmentOutsideMethodCall(CESK cesk, List<Variable> renamedVariables,
                                                  List<ProxyOrValue> variableValues) {
    if (cesk.kontinuation.doesNotContainClosures()) {
      cesk.env = cesk.env.extendEnv(renamedVariables, variableValues, cesk.store);
    } else {
      cesk.env = cesk.kontinuation.topMostEnv().extendEnv(renamedVariables, variableValues, cesk.store);
    }
  }


  private List<ProxyOrValue> findVariableValues(List<Variable> variables, Kontinuation k, Store s) {
    Environment env = k.topMostEnv();
    List<ProxyOrValue> variableValues = new ArrayList<>();
    for (Variable variable : variables) {
      Location objLoc = env.get(variable);
      ProxyOrValue obj = s.get(objLoc);
      variableValues.add(obj);
    }
    return variableValues;
  }



  @Override
  public IControl evaluateStatement(Environment env, Store store, IControl control,
                                    Block program, List<IClass> classes) {
    throw new IllegalStateException("A block cannot be evaluated as a whole");
  }




  public IControl evaluateStatementOrDeclaration(Environment env, Store store, IControl control, List<IClass> classes) {
    if (!this.declarations.isEmpty()) {
      IDeclaration firstDef = this.declarations.removeFirst();
      return firstDef.evaluateDeclaration(env, store, control);
    } else {
      IStatementOrNestedBlock firstStmt = this.stmts.removeFirst();
      return firstStmt.evaluateStatement(env, store, control, this, classes);
    }
  }


  @Override
  public boolean isNestedBlock() {
    return this.expression.isEmpty();
  }

  @Override
  public Block getNestedBlockOrThrow() {
    return this;
  }


  public void addNextToDo(IStatementOrNestedBlock next) {
    this.stmts.addFirst(next);
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Block block) {
      return this.declarations.equals(block.declarations)
          && this.stmts.equals(block.stmts)
          && this.expression.equals(block.expression);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(declarations, stmts, expression);
  }


  @Override
  public void renameClassesToQualifiedNames(ModuleClassBinding binding) {
    renameClassesInBodyDeclarations(binding);
    renameClassesInBodyStatements(binding);
    if (expression.isPresent()) {
      expression.get().renameClassesToQualifiedNames(binding);
    }
  }

  @Override
  public boolean containsTypeError(Map<Variable, Shape> sClasses, Map<Variable, IType> tVar, IType returnType) {
    Map<Variable, IType> tVarCopy = new HashMap<>(tVar);
    boolean hasTypeError = false;
    hasTypeError |= declarationsContainsTypeErrors(sClasses, tVarCopy);
    hasTypeError |= statementsContainsTypeErrors(sClasses, tVarCopy);
    hasTypeError |= expressionContainsTypeErrors(sClasses, tVarCopy);
    if (!hasTypeError && this.expression.isPresent()) {
      hasTypeError |= this.invalidReturnType(returnType, tVarCopy, sClasses);
    }
    return hasTypeError;
  }

  private boolean invalidReturnType(IType returnType, Map<Variable, IType> tVar, Map<Variable, Shape> sClasses) {
    if (!expression.get().returnTypeEquals(returnType, tVar, sClasses)) {
      this.expression = Optional.of(new TypeError(this.expression.get().toString(),
          "Block return type does not match."));
      return true;
    }
    return false;
  }

  private boolean declarationsContainsTypeErrors(Map<Variable, Shape> sClasses, Map<Variable, IType> tVar) {
    return Utils.anyMatchFullScan(def -> def.containsTypeError(sClasses, tVar),
        this.declarations);
  }

  private boolean statementsContainsTypeErrors(Map<Variable, Shape> sClasses, Map<Variable, IType> tVar) {
    return Utils.anyMatchFullScan(stmt -> stmt.containsTypeError(sClasses, tVar, null),
        this.stmts);
  }

  private boolean expressionContainsTypeErrors(Map<Variable, Shape> sClasses, Map<Variable, IType> tVar) {
    if (expression.isPresent()) {
      return expression.get().containsTypeError(sClasses, tVar);
    } else {
      return false;
    }
  }


  private void renameClassesInBodyDeclarations(ModuleClassBinding binding) {
    Utils.voidMap(declarations, (IDeclaration decl) -> {decl.renameClassesToQualifiedNames(binding);});
  }
  private void renameClassesInBodyStatements(ModuleClassBinding binding) {
    Utils.voidMap(stmts, (IStatementOrNestedBlock stmt) -> {stmt.renameClassesToQualifiedNames(binding);});
  }

}
