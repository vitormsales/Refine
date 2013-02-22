package refine.dependencies;

import org.eclipse.jdt.core.IMethod;

import refine.enums.DependencyType;
import dclsuite.util.DCLUtil;

public final class ThrowDependency extends Dependency {
	private final String methodNameA;
	private final IMethod ImethodA;

	public ThrowDependency(String classNameA, String classNameB,
			String methodNameA, IMethod ImethodA) {
		super(classNameA, classNameB);
		this.methodNameA = methodNameA;
		this.ImethodA = ImethodA;
	}

	public String getMethodName() {
		return this.methodNameA;
	}

	public IMethod getImethodA() {
		return ImethodA;
	}

	@Override
	public String toString() {
		return "'" + this.classNameA + "' contains the method '"
				+ this.methodNameA + "' which throws '" + this.classNameB + "'";
	}

	@Override
	public String toShortString() {
		return "The throwing of " + DCLUtil.getSimpleClassName(this.classNameB)
				+ " is disallowed for this location w.r.t. the architecture";
	}

	@Override
	public DependencyType getDependencyType() {
		return DependencyType.THROW;
	}

}