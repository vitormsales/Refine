package refine.classes;

import java.util.HashSet;
import java.util.Set;

import refine.basic.AllEntitiesMapping;
import refine.methods.Method;

public class Classe {

	private int NameID;
	private Set<Method> methodsID;

	public Classe(int NameID) {
		// TODO Auto-generated constructor stub
		this.NameID = NameID;
		methodsID = new HashSet<Method>();
	}

	public int getNameID() {
		return NameID;
	}

	public Set<Method> getMethodsID() {
		return methodsID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 17;
		result = prime * NameID + NameID * result;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub

		if (obj instanceof Classe) {

			Classe other = (Classe) obj;

			if (this.NameID != other.NameID) {
				return false;
			}
			return true;
		}

		return false;

	}

	public void addMethod(Method method) {
		// TODO Auto-generated method stub
		methodsID.add(method);

	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String Nameclass = "\n"
				+ AllEntitiesMapping.getInstance().getByID(NameID) + "\n";

		for (Method method : methodsID) {
			Nameclass += "\n"
					+ AllEntitiesMapping.getInstance().getByID(
							method.getNameID());
		}

		return Nameclass;
	}

	// Devolve a uniao de dependencia de todos os methods da classe excluido o
	// metodo fonte
	public Set<Integer> getClassDependencies(Method methodSource) {
		// TODO Auto-generated method stub
		Set<Integer> dependeciesID = new HashSet<Integer>();

		for (Method methods : methodsID) {

			if (methodSource!= methods) {
				for (Integer ID : methods.getMethodsDependencies()) {
					dependeciesID.add(ID);
				}
			}
		}

		return dependeciesID;
	}

	public int getNumberOfParticipantsMethods(Method methodSource) {
		
		if (methodsID.contains(methodSource.getNameID())) {
			return methodsID.size() - 1;
		}

		return methodsID.size();
	}
}
