package refine.methods;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import refine.basic.AllEntitiesMapping;
import refine.utils.JavaTypes;

public class Method {

	private int sourceClassID;
	private int NameID;
	private Set<Integer> methodsDependenciesID;

	public Method(int methodId, int sourceClassID) {
		this.NameID = methodId;
		this.sourceClassID = sourceClassID;
		this.methodsDependenciesID = new HashSet<Integer>();
	}

	public int getSourceClassID() {
		return sourceClassID;
	}

	public void setSourceClassID(int classeOrigemID) {
		sourceClassID = classeOrigemID;
	}

	public int getNameID() {
		return NameID;
	}

	public void setNameID(int nameID) {
		NameID = nameID;
	}

	public Set<Integer> getMethodsDependencies() {
		return methodsDependenciesID;
	}

	public void setNewMethodsDependencies(List<String> dependeciesList) {

		int depedencyID;
		for (String name : dependeciesList) {
			if (!mustRemoveTypes(name)) {
				depedencyID = AllEntitiesMapping.getInstance().getByName(name);
				methodsDependenciesID.add(depedencyID);
				AllDependenciesMethods.getInstance()
						.getAllDependenciesMethodID().add(depedencyID);
			}
		}

	}

	public boolean mustRemoveTypes(String dependency) {

		if (dependency.contains("::")) {
			return true;
		}

		if (dependency.contains(":")) {
			String part[] = dependency.split(":", 2);
			dependency = part[1];
			System.out.println(" dependecia de atributo " + dependency);
		}
		if (JavaTypes.isPrimitive(dependency)) {
			return true;
		}

		if (JavaTypes.isString(dependency)) {
			return true;
		}

		if (JavaTypes.isWrapper(dependency)) {
			return true;
		}

		if (JavaTypes.Object(dependency)) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 17;
		result = prime * result + NameID * sourceClassID;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Method) {
			Method other = (Method) obj;

			if (this.NameID != other.NameID
					|| this.sourceClassID != other.sourceClassID) {
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return AllEntitiesMapping.getInstance().getByID(NameID);
	}

}
