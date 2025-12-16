# Romi-Lang

A gradually-typed language interpreter with proxy-based type checking. Enforces type safety at boundaries between typed and untyped modules using runtime contracts and first-order shape validation.

## Overview
Romi-Lang is an implementation of a mixed-typed programming language that allows typed and untyped code to interoperate safely.

### Key Features

- **Gradual Typing**: Seamlessly mix typed and untyped modules in the same program
- **Proxy-Based Type Enforcement**: Typed class instantiation wraps objects in proxies that enforce type contracts
- **Runtime Type Checking**: Validates types at module boundaries through first-order shape checks
- **Sound Semantics**: Prevents type violations through comprehensive runtime validation
- **CESK Machine**: Control-Environment-Store-Kontinuation abstract machine implementation

## How It Works

### Type System

The language supports two module types:

- **Untyped Modules** (`module`): Traditional dynamic typing with no static guarantees
- **Typed Modules** (`tmodule`): Static type annotations with runtime enforcement

### Proxy Mechanism

When typed code instantiates a class, the runtime wraps the object in a **proxy** that combines:
- The underlying object
- Its type specification (Shape)

Proxies enforce type contracts at three critical points:

1. **Method Calls**: Arguments are checked against parameter types before entering methods; return values are validated after methods return
2. **Field Access**: Retrieved values are validated against field types
3. **Field Mutation**: Stored values must conform to field types

### First-Order Checking

When an untyped object crosses into typed code, the runtime performs a **first-order check**:

- Field names must match exactly (same set)
- Field values must conform to their declared types
- Method names must match exactly
- Method parameter counts must match signatures

If the check passes, the object is wrapped in a proxy. If not, a runtime error occurs.

## Language Syntax

### Program Structure
```scheme
(
  (module name (class ClassName (fields...) (methods...)))
  (tmodule name (class ClassName (fields...) (methods...)) shape)
  (import moduleName shape)
  statements...
  expression
)
```

### Types

- `Number`: Double-precision floating point
- `Shape`: Object type with field and method signatures
```scheme
  (Shape (field1 Type1) (field2 Type2) ... 
         ((method1 (ParamType...) ReturnType) ...))
```

### Classes
```scheme
(class ClassName ((field1 field2 ...))
  (method methodName ((param1 param2 ...))
    statements...
    expression))
```

### Expressions

- Literals: `42.0`
- Variables: `x`
- Arithmetic: `(x + y)`, `(x / y)`
- Comparison: `(x == y)`
- Object creation: `(new ClassName (arg1 arg2 ...))`
- Field access: `(obj --> field)`
- Method call: `(obj --> method (arg1 arg2 ...))`
- Type check: `(obj isa ClassName)`

### Statements

- Variable declaration: `(def varName expression)`
- Assignment: `(varName = expression)`
- Field mutation: `(obj --> field = expression)`
- Conditional: `(if0 test thenStmt elseStmt)`
- While loop: `(while0 test bodyStmt)`
- Block: `(block defs... stmts...)` 

## Installation

### Prerequisites

- Java 17 or higher
- Make

### Building
```bash
# Clone the repository
git clone https://github.com/yourusername/janus-lang.git
cd janus-lang

# Build the project
make
```

## Quick Start

Pipe an S-expression program into the executable:
```bash
echo '(
  (tmodule math
    (class Calculator ((Number value))
      (method add ((Number n))
        (def current (this --> value))
        (current + n)))
    (((value Number)) ((add (Number) Number))))
  (import math)
  (def ten 10.0)
  (def calc (new Calculator (ten)))
  (def five 5.0)
  (calc --> add (five))
)' | ./xromi
```

Output: `15.0`

Or run from a file:
```bash
./xtr < program.janus
```

### Output Types

The interpreter produces one of the following:

- **Number**: `42.0` - successful execution
- **Errors**:
    - `parser error` - syntax error
    - `duplicate module name` - naming conflict
    - `duplicate method, field, or parameter name` - naming conflict
    - `bad import` - invalid import
    - `undeclared variable error` - undefined reference
    - `type error` - static type checking failure
    - `run-time error` - runtime type violation