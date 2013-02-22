package refine.classes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import refine.basic.Pair;
import refine.basic.Parameters;
import refine.methods.AllDependenciesMethods;
import refine.methods.Method;

public class StatisticsMethod2MethodsOfClass {
	// parametros p,q,r,s para calculo de similaridade
	private Map<Pair<Method, Classe>, Parameters> allParameters;

	public StatisticsMethod2MethodsOfClass(List<Method> allMethodsList,
			List<Classe> allClassList) {
		// TODO Auto-generated constructor stub
		allParameters = new HashMap<Pair<Method, Classe>, Parameters>();

		createParameters(allMethodsList, allClassList);
	}

	private void createParameters(List<Method> allMethodsList,
			List<Classe> allClassList) {
		// TODO Auto-generated method stub
		float total = allMethodsList.size();
		System.out.println("Total de metodos " + (int) total);

		for (int i = 0; i < allMethodsList.size(); i++) {
			System.out.printf("%2.2" + "f %c \n", (100) * (i / total), '%');
			for (int j = 0; j < allClassList.size(); j++) {
				Pair<Method, Classe> pair = new Pair<Method, Classe>(
						allMethodsList.get(i), allClassList.get(j));

				Parameters p = findParameters(allMethodsList.get(i),
						allClassList.get(j));
				allParameters.put(pair, p);

			}
		}

		// ############imprimir
		// Iterator<Pair<Method, Classe>> it =
		// allParameters.keySet().iterator();
		// while (it.hasNext()) {
		// Pair<Method, Classe> key = it.next();
		// System.out.println(key);
		// System.out.println(allParameters.get(key));
		// System.out.println();
		//
		// }

		// for (Integer i : AllDependenciesMethods.getInstance()
		// .getAllDependenciesMethodID()) {
		// System.out.println(i + " "
		// + AllEntitiesMapping.getInstance().getByID(i));
		//
		// }
		// System.out.println(AllDependenciesMethods.getInstance()
		// .getAllDependenciesMethodID().size());
		// ############################fim

	}

	private Parameters findParameters(Method method1, Classe classA) {
		// TODO Auto-generated method stub

		Parameters parameters = new Parameters();

		int p = inBoth(method1.getMethodsDependencies(),
				classA.getClassDependencies(method1));
		parameters.setP(p);
		int q = inFirstOnly(method1.getMethodsDependencies(),
				classA.getClassDependencies(method1));
		parameters.setQ(q);
		int r = inSecondOnly(method1.getMethodsDependencies(),
				classA.getClassDependencies(method1));
		parameters.setR(r);
		int s = inNone(method1.getMethodsDependencies(),
				classA.getClassDependencies(method1));
		parameters.setS(s);

		return parameters;
	}

	private int inBoth(Set<Integer> ConjuntoA, Set<Integer> ConjuntoB) {
		// TODO Auto-generated method stub
		int tamA = ConjuntoA.size();
		int tamB = ConjuntoB.size();
		Set<Integer> union = new HashSet<Integer>();

		union.addAll(ConjuntoA);
		union.addAll(ConjuntoB);

		int tamEnd = union.size();

		return tamA + tamB - tamEnd;
	}

	private int inFirstOnly(Set<Integer> ConjuntoA, Set<Integer> ConjuntoB) {
		// TODO Auto-generated method stub

		Set<Integer> union = new HashSet<Integer>(ConjuntoB);

		int tamBegin = union.size();
		union.addAll(ConjuntoA);
		int tamEnd = union.size();

		return tamEnd - tamBegin;
	}

	private int inSecondOnly(Set<Integer> ConjuntoA, Set<Integer> ConjuntoB) {
		// TODO Auto-generated method stub

		Set<Integer> union = new HashSet<Integer>(ConjuntoA);

		int tamBegin = union.size();
		union.addAll(ConjuntoB);
		int tamEnd = union.size();

		return tamEnd - tamBegin;
	}

	private int inNone(Set<Integer> ConjuntoA, Set<Integer> ConjuntoB) {
		// TODO Auto-generated method stub
		int cont = 0;
		Set<Integer> union = new HashSet<Integer>();
		Set<Integer> allDepenciesSet = AllDependenciesMethods.getInstance()
				.getAllDependenciesMethodID();

		union.addAll(ConjuntoA);
		union.addAll(ConjuntoB);

		for (Integer ID : allDepenciesSet) {
			if (!union.contains(ID)) {
				cont++;
			}
		}

		return cont;
	}

	public Map<Pair<Method, Classe>, Parameters> getAllParameters() {
		return allParameters;
	}

}
