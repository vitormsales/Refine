package refine.dependencies;

public final class ImplementDirectDependency extends ImplementDependency {

	public ImplementDirectDependency(String classNameA, String classNameB) {
		super(classNameA, classNameB);
	}
	
	@Override
	public String toString() {
		return "'" + 
				this.classNameA + "' directly implements '" + this.classNameB + "'";
	}

	
}
