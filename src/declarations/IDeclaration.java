package declarations;

import Maps.Environment;
import Maps.Store;
import Utils.ModuleClassBinding;
import expressions.Variable;

import java.util.Map;
import java.util.Set;
import Control.IControl;
import types.IType;
import types.Shape;

public interface IDeclaration {


  boolean containsUndefinedVariables(Set<Variable> definedVariables,
                                     Set<Variable> definedClassNames);

  IControl findRHS();

  IControl evaluateDeclaration(Environment env, Store store, IControl control);

  void renameClassesToQualifiedNames(ModuleClassBinding moduleToClassBinder);
  boolean containsTypeError(Map<Variable, Shape> sClasses, Map<Variable, IType> tVar);

  IDeclaration copyDecl();
}
