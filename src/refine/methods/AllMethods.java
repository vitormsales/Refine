package refine.methods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

import refine.ast.DeepDependencyVisitor;
import refine.basic.AllEntitiesMapping;
import refine.dependencies.AccessFieldDependency;
import refine.dependencies.AccessMethodDependency;
import refine.dependencies.AnnotateMethodDependency;
import refine.dependencies.CreateMethodDependency;
import refine.dependencies.DeclareLocalVariableDependency;
import refine.dependencies.DeclareParameterDependency;
import refine.dependencies.DeclareReturnDependency;
import refine.dependencies.Dependency;
import refine.dependencies.SimpleNameDependency;
import refine.dependencies.ThrowDependency;
import refine.utils.MoveMethod;
import refine.utils.PrintOutput;
import refine.utils.RefineSignatures;

public class AllMethods {

	private List<Method> allMethodsList;
	private int numberOfExcluded;
	private Map<Integer, IMethod> iMethodMapping;
	private Set<Integer> moveIspossible;

	public AllMethods(List<DeepDependencyVisitor> allDeepDependency)
			throws JavaModelException {
		// TODO Auto-generated method stub

		if (AllEntitiesMapping.getInstance().isCreated()) {

			this.numberOfExcluded = 0;
			this.allMethodsList = new ArrayList<Method>();
			this.iMethodMapping = new HashMap<Integer, IMethod>();
			this.moveIspossible = new HashSet<Integer>();

			createMethodsDependeciesList(allDeepDependency);

		} else {

			throw new ExceptionInInitializerError(
					"AllDependeciesMapping not created yet.");
		}
	}

	private void createMethodsDependeciesList(
			List<DeepDependencyVisitor> allDeepDependency)
			throws JavaModelException {

		for (DeepDependencyVisitor deep : allDeepDependency) {

			// for (IMethod m : deep.getUnit().findPrimaryType().getMethods()) {
			// System.out.println("element " + m.getElementName() + " "
			// + m.getDeclaringType().getFullyQualifiedName());
			// }
			// System.out.println();

			for (Dependency dep : deep.getDependencies()) {

				if (dep instanceof AccessMethodDependency) {

					AccessMethodDependency acDependency = (AccessMethodDependency) dep;

					// String method = dep.getClassNameA() + "."
					// + ((AccessMethodDependency) dep).getMethodNameA()
					// + "()";
					String sourceClass = dep.getClassNameA();
					String dependecyClass = dep.getClassNameB();

					String methodA = RefineSignatures.getMethodSignature(
							dep.getClassNameA(), acDependency.getImethodA());
					String methodB = RefineSignatures.getMethodSignature(
							dep.getClassNameB(), acDependency.getImethodB());

					// String methodB = dep.getClassNameB() + "."
					// + ((AccessMethodDependency) dep).getMethodNameB()
					// + "()";

					List<String> dependeciesList = new ArrayList<String>();
					dependeciesList.add(dependecyClass);
					dependeciesList.add(methodB);

					putIMethodInMapping(methodA, acDependency.getImethodA());
					putIMethodInMapping(methodB, acDependency.getImethodB());

					createMethod(methodA, sourceClass, dependeciesList,
							acDependency.getImethodA());

				}

				if (dep instanceof AccessFieldDependency) {

					AccessFieldDependency acFieldDependency = (AccessFieldDependency) dep;

					String methodA = RefineSignatures.getMethodSignature(
							dep.getClassNameA(),
							acFieldDependency.getImethodA());

					String field = RefineSignatures.getFieldSignature(
							dep.getClassNameB(),
							acFieldDependency.getiVariableBinding());

					// String method = dep.getClassNameA() + "."
					// + ((AccessFieldDependency) dep).getMethodName()
					// + "()";

					String sourceClass = dep.getClassNameA();
					String dependecyClass = dep.getClassNameB();
					// String field = dep.getClassNameB() + "."
					// + ((AccessFieldDependency) dep).getFieldName();

					List<String> dependeciesList = new ArrayList<String>();
					dependeciesList.add(dependecyClass);
					dependeciesList.add(field);

					putIMethodInMapping(methodA,
							acFieldDependency.getImethodA());

					createMethod(methodA, sourceClass, dependeciesList,
							acFieldDependency.getImethodA());

				}

				if (dep instanceof SimpleNameDependency) {

					SimpleNameDependency snDependency = (SimpleNameDependency) dep;

					// String method = dep.getClassNameA() + "."
					// + ((SimpleNameDependency) dep).getMethodName()
					// + "()";

					String sourceClass = dep.getClassNameA();
					String dependecyClass = dep.getClassNameB();
					// String field = dep.getClassNameB() + "."
					// + ((SimpleNameDependency) dep).getVariableName();

					String methodA = RefineSignatures.getMethodSignature(
							dep.getClassNameA(), snDependency.getImethodA());

					String field = RefineSignatures.getFieldSignature(
							dep.getClassNameB(),
							snDependency.getiVariableBinding());

					List<String> dependeciesList = new ArrayList<String>();
					dependeciesList.add(dependecyClass);
					dependeciesList.add(field);

					putIMethodInMapping(methodA, snDependency.getImethodA());

					createMethod(methodA, sourceClass, dependeciesList,
							snDependency.getImethodA());

				}

				if (dep instanceof AnnotateMethodDependency) {

					AnnotateMethodDependency anDependency = (AnnotateMethodDependency) dep;

					String sourceClass = dep.getClassNameA();
					String dependecyClass = dep.getClassNameB();
					// String method = dep.getClassNameA() + "."
					// + ((AnnotateMethodDependency) dep).getMethodName()
					// + "()";

					String methodA = RefineSignatures.getMethodSignature(
							dep.getClassNameA(), anDependency.getImethodA());

					List<String> dependeciesList = new ArrayList<String>();
					dependeciesList.add(dependecyClass);

					putIMethodInMapping(methodA, anDependency.getImethodA());

					createMethod(methodA, sourceClass, dependeciesList,
							anDependency.getImethodA());

				}

				if (dep instanceof CreateMethodDependency) {

					CreateMethodDependency cmDependency = (CreateMethodDependency) dep;

					String sourceClass = dep.getClassNameA();
					String dependecyClass = dep.getClassNameB();

					String methodA = RefineSignatures.getMethodSignature(
							dep.getClassNameA(), cmDependency.getImethodA());

					// String method = dep.getClassNameA() + "."
					// + ((CreateMethodDependency) dep).getMethodNameA()
					// + "()";

					List<String> dependeciesList = new ArrayList<String>();
					dependeciesList.add(dependecyClass);

					putIMethodInMapping(methodA, cmDependency.getImethodA());

					createMethod(methodA, sourceClass, dependeciesList,
							cmDependency.getImethodA());

				}

				if (dep instanceof DeclareParameterDependency) {

					DeclareParameterDependency dpDependency = (DeclareParameterDependency) dep;

					// String method = dep.getClassNameA()
					// + "."
					// + ((DeclareParameterDependency) dep)
					// .getMethodName() + "()";

					String methodA = RefineSignatures.getMethodSignature(
							dep.getClassNameA(), dpDependency.getImethodA());

					String sourceClass = dep.getClassNameA();
					String dependecyClass = dep.getClassNameB();
					// String field = dep.getClassNameB() + "."
					// + ((DeclareParameterDependency) dep).getFieldName();

					List<String> dependeciesList = new ArrayList<String>();
					dependeciesList.add(dependecyClass);
					// dependeciesList.add(field);

					putIMethodInMapping(methodA, dpDependency.getImethodA());

					createMethod(methodA, sourceClass, dependeciesList,
							dpDependency.getImethodA());

				}

				if (dep instanceof DeclareReturnDependency) {

					DeclareReturnDependency drDependency = (DeclareReturnDependency) dep;

					String methodA = RefineSignatures.getMethodSignature(
							dep.getClassNameA(), drDependency.getImethodA());

					String sourceClass = dep.getClassNameA();
					String dependecyClass = dep.getClassNameB();
					// String method = dep.getClassNameA() + "."
					// + ((DeclareReturnDependency) dep).getMethodName()
					// + "()";

					List<String> dependeciesList = new ArrayList<String>();
					dependeciesList.add(dependecyClass);

					putIMethodInMapping(methodA, drDependency.getImethodA());

					createMethod(methodA, sourceClass, dependeciesList,
							drDependency.getImethodA());

				}

				if (dep instanceof DeclareLocalVariableDependency) {

					DeclareLocalVariableDependency dlvDependency = (DeclareLocalVariableDependency) dep;

					String methodA = RefineSignatures.getMethodSignature(
							dep.getClassNameA(), dlvDependency.getImethodA());

					// String method = dep.getClassNameA()
					// + "."
					// + ((DeclareLocalVariableDependency) dep)
					// .getMethodName() + "()";
					String sourceClass = dep.getClassNameA();
					String dependecyClass = dep.getClassNameB();
					// String field = dep.getClassNameB()
					// + "."
					// + ((DeclareLocalVariableDependency) dep)
					// .getFieldName();

					List<String> dependeciesList = new ArrayList<String>();
					dependeciesList.add(dependecyClass);
					// dependeciesList.add(field);

					putIMethodInMapping(methodA, dlvDependency.getImethodA());

					createMethod(methodA, sourceClass, dependeciesList,
							dlvDependency.getImethodA());

				}

				if (dep instanceof ThrowDependency) {

					ThrowDependency throwDependency = (ThrowDependency) dep;

					String methodA = RefineSignatures.getMethodSignature(
							dep.getClassNameA(), throwDependency.getImethodA());

					String sourceClass = dep.getClassNameA();
					String dependecyClass = dep.getClassNameB();
					// String method = dep.getClassNameA() + "."
					// + ((ThrowDependency) dep).getMethodName() + "()";

					List<String> dependeciesList = new ArrayList<String>();
					dependeciesList.add(dependecyClass);

					putIMethodInMapping(methodA, throwDependency.getImethodA());

					createMethod(methodA, sourceClass, dependeciesList,
							throwDependency.getImethodA());

				}

			}

		}

		// // #............ Imprime dependencias

		Iterator<Method> it = allMethodsList.iterator();
		while (it.hasNext()) {
			Method m = it.next();

			PrintOutput.write(
					AllEntitiesMapping.getInstance().getByID(
							m.getNameID()) + "\n", "conjuntos");

			for (Integer chaves : m.getMethodsDependencies()) {
				PrintOutput
						.write(AllEntitiesMapping.getInstance().getByID(chaves)
								+ "\n", "conjuntos");
			}

			PrintOutput.write("\n \n", "conjuntos");
		}
		// //#............

	}

	private void createMethod(String methodName, String sourceClass,
			List<String> dependeciesList, IMethod iMethod) {
		// TODO Auto-generated method stub

		int methodId = AllEntitiesMapping.getInstance().getByName(methodName);

		int sourceClassID = AllEntitiesMapping.getInstance().getByName(
				sourceClass);

		Method method2 = new Method(methodId, sourceClassID);

		if (allMethodsList.contains(method2)) {

			method2 = allMethodsList.get(allMethodsList.indexOf(method2));
			method2.setNewMethodsDependencies(dependeciesList);

		} else {

			List<String> moveReachable = MoveMethod
					.getpossibleRefactoring(getIMethod(method2));
			if (moveReachable.size() > 0) {
				moveIspossible.add(methodId);
			}

			allMethodsList.add(method2);
			method2.setNewMethodsDependencies(dependeciesList);

		}

	}

	private void putIMethodInMapping(String methodName, IMethod iMethod) {
		// TODO Auto-generated method stub

		int methodId = AllEntitiesMapping.getInstance().getByName(methodName);
		iMethodMapping.put(methodId, iMethod);

	}

	public IMethod getIMethod(Method method) {
		// TODO Auto-generated method stub
		return iMethodMapping.get(method.getNameID());
	}

	public List<Method> getAllMethodsList() {
		return allMethodsList;
	}

	public Method getMethodByID(int methodID) {
		for (Method method : allMethodsList) {
			if (methodID == method.getNameID())
				return method;
		}
		return null;

	}

	public int getNumberOfExcluded() {
		return numberOfExcluded;
	}

	private void setNumberOfExcluded(int numberOfExcluded) {
		this.numberOfExcluded = numberOfExcluded;
	}

	public Set<Integer> getMoveIspossible() {
		return moveIspossible;
	}

	public void excludeDependeciesLessThan(int Numdepedencies) {

		List<Method> smallMethod = new ArrayList<Method>();

		if (Numdepedencies > getNumberOfExcluded()) {

			setNumberOfExcluded(Numdepedencies);

			for (Method method : allMethodsList) {
				if (method.getMethodsDependencies().size() < Numdepedencies) {
					smallMethod.add(method);
				}
			}

			allMethodsList.removeAll(smallMethod);
		}
	}

	public void excludeConstructors() {

		Iterator<Entry<Integer, IMethod>> it = iMethodMapping.entrySet()
				.iterator();

		while (it.hasNext()) {
			Entry<Integer, IMethod> entry = it.next();
			IMethod method = entry.getValue();
			try {
				if (method != null && method.isConstructor()) {
					Method methodTemp = getMethodByID(entry.getKey());
					int index = allMethodsList.indexOf(methodTemp);
					allMethodsList.remove(index);
				}
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
