package refine.dependencies;

import refine.enums.DependencyType;
import dclsuite.util.DCLUtil;

public class CreateDependency extends Dependency {
	public CreateDependency(String classNameA, String classNameB) {
		super(classNameA, classNameB);
	}

	@Override
	public DependencyType getDependencyType() {
		return DependencyType.CREATE;
	}
	
	@Override
	public String toShortString() {
		return "The creation of " + DCLUtil.getSimpleClassName(this.classNameB) + " is disallowed for this location w.r.t. the architecture";
	}
}