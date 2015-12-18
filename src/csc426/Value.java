package csc426;


import java.util.Iterator;
import java.util.List;

import csc426.AST.Block;
import csc426.AST.Param;
import csc426.AST.Param.Var;

public abstract class Value {
	public boolean boolValue() throws ValueError {
		throw new ValueError("Boolean Value Required");
	}

	public int intValue() throws ValueError {
		throw new ValueError("Integer Value Required");
	}

	public void set(Value value) throws ValueError {
		throw new ValueError("Variable Required");
	}

	public void call(List<Value> args, InterpreterVisitor visitor, SymbolTable<Value> table) throws ValueError {
		throw new ValueError("Procedure Required");
	}
	public void match(List<Value> args, TypeChecker checker, SymbolTable<Value> table) throws ValueError {
		throw new ValueError("Procedure Required");
	}

	public static class IntValue extends Value {
		private int value;

		public IntValue(int value) {
			this.value = value;
		}

		@Override
		public int intValue() {
			return value;
		}
	}

	public static class BoolValue extends Value {
		private boolean value;

		public BoolValue(boolean value) {
			this.value = value;
		}

		@Override
		public boolean boolValue() {
			return value;
		}
	}

	public static class IntCell extends Value {
		private IntValue value;

		public IntCell() {
			this.value = null;
		}

		@Override
		public int intValue() throws ValueError {
			if (value != null) {
				return value.intValue();
			} else {
				throw new ValueError("Uninitialized variable");
			}
		}

		@Override
		public void set(Value value) throws ValueError {
			this.value = new IntValue(value.intValue());
		}
	}

	public static class BoolCell extends Value {
		private BoolValue value;

		public BoolCell() {
			this.value = null;
		}

		@Override
		public boolean boolValue() throws ValueError {
			if (value != null) {
				return value.boolValue();
			} else {
				throw new ValueError("Uninitialized variable");
			}
		}

		@Override
		public void set(Value value) throws ValueError {
			this.value = new BoolValue(value.boolValue());
		}
	}

	public static class ProcValue extends Value {
		private List<Param> params;
		private Block block;

		public ProcValue(List<Param> params, Block block) {
			this.params = params;
			this.block = block;
		}

		@Override
		public void call(List<Value> args, InterpreterVisitor visitor, SymbolTable<Value> table) throws ValueError {
			if (args.size() != params.size()) {
				throw new ValueError("Wrong number of arguments");
			}

			try {
				Iterator<Value> it = args.iterator();
				for (Param param : params) {
					Value arg = it.next();
					if (param instanceof Var) {
						table.add(param.id, arg);
					} else {
						switch (param.type) {
						case Int: {
							Value value = new IntCell();
							value.set(new IntValue(arg.intValue()));
							table.add(param.id, value);
							break;
						}
						case Bool: {
							Value value = new BoolCell();
							value.set(new BoolValue(arg.boolValue()));
							table.add(param.id, value);
							break;
						}
						}
					}
				}
			} catch (TableError e) {
				throw new ValueError(e.getMessage());
			}

			block.accept(visitor);
		}
		

		@Override
		public void match(List<Value> args, TypeChecker checker, SymbolTable<Value> table) throws ValueError {
			if (args.size() != params.size()) {
				throw new ValueError("Wrong number of arguments");
			}
			Iterator<Value> it = args.iterator();
			for (Param param : params) {
				Value arg = it.next();
				switch(param.type){
				case Bool:
					if(arg instanceof BoolValue){
					} else if(arg instanceof BoolCell) {
					} else {
						throw new ValueError("Type mismatch in proc call, " + param.id);
					}
					break;
				case Int:
					if(arg instanceof IntValue){
					} else if(arg instanceof IntCell) {
					} else {
						throw new ValueError("Type mismatch in proc call, " + param.id);
					}
					break;
				default:
					throw new ValueError("Match Broke on " + param.id);

				}
			}


			block.accept(checker);
		}
	}
}