package refine.basic;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.JavaModelException;

import refine.ast.DeepDependencyVisitor;
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
import refine.utils.RefineSignatures;

public class AllEntitiesMapping {

	private AbstractMap<Integer, String> allDependeciesMapByID = null;
	private AbstractMap<String, Integer> allDependeciesMapByName = null;
	private static AllEntitiesMapping instance;
	private boolean isCreated = false;

	private AllEntitiesMapping() {
		// TODO Auto-generated constructor stub
	}

	public static AllEntitiesMapping getInstance() {
		if (instance == null) {
			instance = new AllEntitiesMapping();
		}
		return instance;
	}

	public void createAllDependeciesMapping(
			List<DeepDependencyVisitor> deepDependency) {
		allDependeciesMapByID = new HashMap<Integer, String>();
		allDependeciesMapByName = new HashMap<String, Integer>();
		try {
			createAllDependeciesMap(deepDependency);
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		isCreated = true;

	}

	private void createAllDependeciesMap(
			List<DeepDependencyVisitor> allDeepDependency)
			throws JavaModelException {

		for (DeepDependencyVisitor deep : allDeepDependency) {
			for (Dependency dep : deep.getDependencies()) {

				if (dep instanceof AccessMethodDependency) {

					AccessMethodDependency acDependency = (AccessMethodDependency) dep;

					String classeA = dep.getClassNameA();
					String classeB = dep.getClassNameB();

					String methodA = RefineSignatures.getMethodSignature(
							dep.getClassNameA(), acDependency.getImethodA());

					String methodB = RefineSignatures.getMethodSignature(
							dep.getClassNameB(), acDependency.getImethodB());

					// System.out.println("AccessMethodDependency");
					// System.out.println(methodA);
					// System.out.println(classeA);
					// System.out.println(classeB);
					// System.out.println(methodB);
					// System.out.println();

					insertMapping(methodA);
					insertMapping(classeA);
					insertMapping(classeB);
					insertMapping(methodB);

				}

				if (dep instanceof AccessFieldDependency) {

					AccessFieldDependency acFieldDependency = (AccessFieldDependency) dep;

					String classeA = dep.getClassNameA();
					String classeB = dep.getClassNameB();

					String methodA = RefineSignatures.getMethodSignature(
							dep.getClassNameA(),
							acFieldDependency.getImethodA());

					String field = RefineSignatures.getFieldSignature(
							dep.getClassNameB(),
							acFieldDependency.getiVariableBinding());

					// System.out.println("AccessFieldDependency");
					// System.out.println(methodA);
					// System.out.println(classeA);
					// System.out.println(classeB);
					// System.out.println(field);
					// System.out.println();

					insertMapping(methodA);
					insertMapping(classeA);
					insertMapping(classeB);
					insertMapping(field);

				}

				if (dep instanceof SimpleNameDependency) {

					SimpleNameDependency snDependency = (SimpleNameDependency) dep;

					String classeA = dep.getClassNameA();
					String classeB = dep.getClassNameB();

					String methodA = RefineSignatures.getMethodSignature(
							dep.getClassNameA(), snDependency.getImethodA());

					String field = RefineSignatures.getFieldSignature(
							dep.getClassNameB(),
							snDependency.getiVariableBinding());

					// System.out.println("SimpleNameDependency");
					// System.out.println(methodA);
					// System.out.println(classeA);
					// System.out.println(classeB);
					// System.out.println(field);
					// System.out.println();

					insertMapping(methodA);
					insertMapping(classeA);
					insertMapping(classeB);
					insertMapping(field);

				}

				if (dep instanceof AnnotateMethodDependency) {

					AnnotateMethodDependency anDependency = (AnnotateMethodDependency) dep;

					String classeA = dep.getClassNameA();
					String classeB = dep.getClassNameB();

					String methodA = RefineSignatures.getMethodSignature(
							dep.getClassNameA(), anDependency.getImethodA());

					insertMapping(methodA);
					insertMapping(classeA);
					insertMapping(classeB);

					// System.out.println("AnnotateMethodDependency");
					// System.out.println(methodA);
					// System.out.println(classeA);
					// System.out.println(classeB);
					// System.out.println();
				}

				if (dep instanceof CreateMethodDependency) {

					CreateMethodDependency cmDependency = (CreateMethodDependency) dep;

					String classeA = dep.getClassNameA();
					String classeB = dep.getClassNameB();
					String methodA = RefineSignatures.getMethodSignature(
							dep.getClassNameA(), cmDependency.getImethodA());

					// System.out.println("CreateMethodDependency");
					// System.out.println(classeA);
					// System.out.println(methodA);
					// System.out.println(classeB);
					// System.out.println();

					insertMapping(classeA);
					insertMapping(methodA);
					insertMapping(classeB);

				}

				if (dep instanceof DeclareParameterDependency) {

					DeclareParameterDependency dpDependency = (DeclareParameterDependency) dep;

					String classeA = dep.getClassNameA();
					String classeB = dep.getClassNameB();

					String methodA = RefineSignatures.getMethodSignature(
							dep.getClassNameA(), dpDependency.getImethodA());

					// System.out.println("DeclareParameterDependency");
					// System.out.println(methodA);
					// System.out.println(classeA);
					// System.out.println(classeB);
					// System.out.println();

					insertMapping(methodA);
					insertMapping(classeA);
					insertMapping(classeB);

				}

				if (dep instanceof DeclareReturnDependency) {

					DeclareReturnDependency drDependency = (DeclareReturnDependency) dep;

					String classeA = dep.getClassNameA();
					String classeB = dep.getClassNameB();

					String methodA = RefineSignatures.getMethodSignature(
							dep.getClassNameA(), drDependency.getImethodA());

					// System.out.println("DeclareReturnDependency");
					// System.out.println(methodA);
					// System.out.println(classeA);
					// System.out.println(classeB);
					// System.out.println();

					insertMapping(methodA);
					insertMapping(classeA);
					insertMapping(classeB);

				}

				if (dep instanceof DeclareLocalVariableDependency) {

					DeclareLocalVariableDependency dlvDependency = (DeclareLocalVariableDependency) dep;

					String classeA = dep.getClassNameA();
					String classeB = dep.getClassNameB();

					String methodA = RefineSignatures.getMethodSignature(
							dep.getClassNameA(), dlvDependency.getImethodA());

					// System.out.println("DeclareLocalVariableDependency");
					// System.out.println(methodA);
					// System.out.println(classeA);
					// System.out.println(classeB);
					// System.out.println();

					insertMapping(methodA);
					insertMapping(classeA);
					insertMapping(classeB);

				}

				if (dep instanceof ThrowDependency) {

					ThrowDependency throwDependency = (ThrowDependency) dep;

					String classeA = dep.getClassNameA();
					String classeB = dep.getClassNameB();
					String methodA = RefineSignatures.getMethodSignature(
							dep.getClassNameA(), throwDependency.getImethodA());

					// System.out.println("ThrowDependency");
					// System.out.println(methodA);
					// System.out.println(classeA);
					// System.out.println(classeB);
					// System.out.println();

					insertMapping(methodA);
					insertMapping(classeA);
					insertMapping(classeB);

				}

			}

		}

		// // ############### Imprime todas as dependencias encontradas
		// Iterator<Integer> it = allDependeciesMapByID.keySet().iterator();
		// PrintOutput out = new PrintOutput();
		// String address = "Tudo";
		// while (it.hasNext()) {
		// String temp = allDependeciesMapByID.get(it.next());
		// System.out.println(temp + " " + allDependeciesMapByName.get(temp));
		// out.write(temp + " " + allDependeciesMapByName.get(temp) + "\n",
		// address);
		//
		// }
		// out.write("total de possiveis dep " + allDependeciesMapByID.size(),
		// address);
		// out.finish(address);
		// System.out.println("total de possiveis dep "
		// + allDependeciesMapByID.size());
		// System.out.println("\nallDependecies FINISH\n\n");
		// // ###############

	}

	private void insertMapping(String dependency) {
		// TODO Auto-generated method stub
		if (!allDependeciesMapByName.containsKey(dependency)) {
			allDependeciesMapByName.put(dependency, allDependeciesMapByName.size());
			allDependeciesMapByID.put(allDependeciesMapByName.get(dependency),
					dependency);
		}

	}

	public boolean isCreated() {
		return isCreated;
	}

	public String getByID(int id) {
		if (isCreated()) {
			return allDependeciesMapByID.get(id);
		}
		throw new ExceptionInInitializerError(
				"AllDependeciesMapping not created yet.");
	}

	public Integer getByName(String name) {
		if (isCreated()) {
			allDependeciesMapByName.get(allDependeciesMapByName.get(name));
			return allDependeciesMapByName.get(name);
		}
		throw new ExceptionInInitializerError(
				"AllDependeciesMapping not created yet.");
	}

}
