package refine.methods;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import refine.basic.AllEntitiesMapping;
import refine.utils.JavaTypes;

public class Method {

	private int sourceClassID;
	private int NameID;
	private Set<Integer> methodsDependenciesID;
	
	//Atributo para feature envy
	private Set<Integer> methodsAcessDependenciesID;

	public Method(int methodId, int sourceClassID) {
		this.NameID = methodId;
		this.sourceClassID = sourceClassID;
		this.methodsDependenciesID = new HashSet<Integer>();
		this.methodsAcessDependenciesID = new HashSet<Integer>();
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

	public Set<Integer> getMethodsAcessDependenciesID() {
		return methodsAcessDependenciesID;
	}

	public void setNewMethodsDependencies(List<String> dependeciesList) {

		for (String dependecy : dependeciesList) {
			if (JavaTypes.mustRemoveTypes(dependecy))
				return;
		}

		int depedencyID;

		// Cria conjunto para deteccao de feature Envy
			for (String dependency : dependeciesList) {
				if (JavaTypes.ismethodOrAtribute(dependency)) {
					depedencyID = AllEntitiesMapping.getInstance().getByName(
							dependency);
					methodsAcessDependenciesID.add(depedencyID);
				}
			}

		for (String name : dependeciesList) {
			if (!JavaTypes.ismethodOrAtribute(name)) {
				depedencyID = AllEntitiesMapping.getInstance().getByName(name);
				methodsDependenciesID.add(depedencyID);
				AllDependenciesMethods.getInstance()
						.getAllDependenciesMethodID().add(depedencyID);
			}

		}

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
