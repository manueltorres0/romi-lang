package programs;

import Maps.Environment;
import Maps.Store;
import Utils.ModuleClassBinding;
import block.Block;
import cesk.CESK;
import expressions.Variable;
import Control.IControl;
import Class.*;
import types.IType;
import types.Shape;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IStatementOrNestedBlock {


  boolean containsUndefinedVariables(Set<Variable> definedVariables,
                                     Set<Variable> definedClassNames);

  boolean isNestedBlock();

  Block getNestedBlockOrThrow();

  IControl findNextExpression(Kontinuation k, CESK cesk);

  IControl evaluateStatement(Environment env, Store store, IControl control, Block program, List<IClass> classes);

  IStatementOrNestedBlock copy();

  void renameClassesToQualifiedNames(ModuleClassBinding moduleToClassBinder);

  boolean containsTypeError(Map<Variable, Shape> sClasses, Map<Variable, IType> tVar, IType returnType);


}
