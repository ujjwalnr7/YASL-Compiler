package csc426;


import java.util.List;

import csc426.AST.Param;

public abstract class OffsetInformation {
	public int intValue() throws OffsetInformationError {
		throw new OffsetInformationError("The Information object is not an Integer");
	}
	
	public int levelValue() throws OffsetInformationError{
		throw new OffsetInformationError("The Information object is not a Var or Ref Type");
	}
	
	public int offsetValue() throws OffsetInformationError{
		throw new OffsetInformationError("The Information object is not a Var or Ref Type");
	}
	
	public String label() throws OffsetInformationError{
		throw new OffsetInformationError("The Information object is not a Proc Type");
	}
	
	public List<Param> params() throws OffsetInformationError{
		throw new OffsetInformationError("The Information object is not a Proc Type");
	}
	
	public static class ConstInfo extends OffsetInformation{
		private int n;
		public ConstInfo(int n){
			this.n = n;
		}
		@Override
		public int intValue(){
			return n;
		}
	}
	
	public static class VarInfo extends OffsetInformation{
		private int level, offset;
		public VarInfo(int level, int offset){
			this.level = level;
			this.offset = offset;
		}
		@Override
		public int levelValue(){
			return level;
		}
		@Override
		public int offsetValue(){
			return offset;
		}
	}
	
	public static class RefInfo extends OffsetInformation{
		private int level, offset;
		public RefInfo(int level, int offset){
			this.level = level;
			this.offset = offset;
		}
		@Override
		public int levelValue(){
			return level;
		}
		@Override
		public int offsetValue(){
			return offset;
		}
	}
	
	public static class ProcInfo extends OffsetInformation{
		private String label;
		private List<Param> params;
		public ProcInfo(String label, List<Param> params){
			this.label = label;
			this.params = params;
		}
		@Override
		public String label(){
			return label;
		}
		@Override
		public List<Param> params(){
			return params;
		}
	}
}