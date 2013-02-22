package refine.dependencies;

import refine.enums.DependencyType;
import dclsuite.util.DCLUtil;

public class DeclareDependency extends HandleDependency {

	public DeclareDependency(String classNameA, String classNameB) {
		super(classNameA, classNameB);
	}
	
	@Override
	public DependencyType getDependencyType() {
		return DependencyType.DECLARE;
	}
	
	@Override
	public String toShortString() {
		return "The declaration of " + DCLUtil.getSimpleClassName(this.classNameB) + " is disallowed for this location w.r.t. the architecture";
	}
}