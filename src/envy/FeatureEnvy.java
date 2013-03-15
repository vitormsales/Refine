package envy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import refine.basic.AllEntitiesMapping;
import refine.methods.AllMethods;
import refine.methods.Method;
import refine.utils.MoveMethod;
import refine.utils.PrintOutput;
import refine.utils.SorMapByValues;

public class FeatureEnvy {

	private static FeatureEnvy instance;

	// Id do método de feature envy e a lista de candidatos
	private Map<Integer, List<Integer>> featuryEnvyMethodMap;

	// Depedencia e a frequencia de ocorrencia
	private Map<Integer, Integer> frequency;
	private boolean hasEnvyClass;

	private final String address = "FeatureEnvyMoves";

	private FeatureEnvy() {
		// TODO Auto-generated constructor stub
		this.featuryEnvyMethodMap = new HashMap<Integer, List<Integer>>();
		this.frequency = new HashMap<Integer, Integer>();
	}

	public static FeatureEnvy getInstance() {
		if (instance == null) {
			instance = new FeatureEnvy();
		}
		return instance;
	}

	private void detectFeatureEnvy(List<Method> allMethodsList) {

		for (Method method : allMethodsList) {

			// System.out.println(method);
			int myOriginalclassID = method.getSourceClassID();

			frequency.clear();
			hasEnvyClass = false;

			for (Integer ID : method.getMethodsAcessDependenciesID()) {
				Integer key = getDependencyClassID(ID);
				if (key != null) {
					if (!frequency.containsKey(key)) {
						frequency.put(key, 1);
					} else {
						int value = frequency.get(key);
						frequency.put(key, ++value);
					}
				}
			}

			// Iterator<Entry<Integer, Integer>> it = frequency.entrySet()
			// .iterator();
			//
			// // classe acessada e frequencia acessada
			// while (it.hasNext()) {
			// Entry<Integer, Integer> entry = it.next();
			// System.out.println(AllEntitiesMapping.getInstance().getByID(
			// entry.getKey())
			// + " " + entry.getValue());
			//
			// }
			// System.out.println();

			List<Integer> CandidateList = getEnviedClass(myOriginalclassID);

			if (hasEnvyClass) {
				// System.out.println("FEature Envy para o metodo " + method);
				featuryEnvyMethodMap.put(method.getNameID(), CandidateList);
				// for (Integer id : CandidateList) {
				// System.out.println(AllEntitiesMapping.getInstance()
				// .getByID(id));
				// }
			}
			// System.out.println();
		}

	}

	private List<Integer> getEnviedClass(int myOriginalclassID) {
		int MyValor = 0;

		if (frequency.containsKey(myOriginalclassID)) {
			MyValor = frequency.get(myOriginalclassID);
		}

		frequency = SorMapByValues.sortByValues(frequency);

		List<Integer> CandidateList = new ArrayList<Integer>();

		Iterator<Entry<Integer, Integer>> it = frequency.entrySet().iterator();

		while (it.hasNext()) {

			Entry<Integer, Integer> entry = it.next();
			//
			// System.out.println(AllEntitiesMapping.getInstance().getByID(
			// entry.getKey())
			// + " " + entry.getValue());

			if (MyValor <= entry.getValue()
					&& myOriginalclassID != entry.getKey()) {
				if (InternalClass.getInstance().isInternal(entry.getKey())) {
					CandidateList.add(entry.getKey());
					hasEnvyClass = true;
				}
			}

		}
		return CandidateList;
	}

	private Integer getDependencyClassID(Integer iD) {
		// TODO Auto-generated method stub
		String dependency = AllEntitiesMapping.getInstance().getByID(iD);
		String parts[];

		if (dependency.contains("::")) {
			parts = dependency.split("::", 2);
			// System.out.println(dependency + " Método " + parts[0]);
			return AllEntitiesMapping.getInstance().getByName(parts[0]);
		} else {
			String part = getCorrectDependecy(dependency);
			return AllEntitiesMapping.getInstance().getByName(part);

		}

	}

	private String getCorrectDependecy(String dependency) {
		// TODO Auto-generated method stub
		String parts[];
		parts = dependency.split(":", 2);

		parts[1] = treatyClassNameID(parts[1]);
		// System.out.println(dependency + " parte1 " + parts[1]);

		int key = AllEntitiesMapping.getInstance().getByName(parts[1]);

		if (InternalClass.getInstance().isInternal(key)) {
			return null;
		}

		parts = dependency.split(";;", 2);
		parts[0] = treatyClassNameID(parts[0]);
		// System.out.println(dependency + " parte0 " + parts[0]);

		return parts[0];
	}

	public void sugestFeatureEnvyMoves(AllMethods allMethods) {

		detectFeatureEnvy(allMethods.getAllMethodsList());

		Iterator<Entry<Integer, List<Integer>>> it = featuryEnvyMethodMap
				.entrySet().iterator();
		int cont = 0;
		while (it.hasNext()) {
			Entry<Integer, List<Integer>> entry = it.next();

			Method sourceMethod = allMethods.getMethodByID(entry.getKey());

			// #### tira metodos pequenos
			if (sourceMethod.getMethodsDependencies().size() < 4) {
				continue;
			}
			// #### end

			List<String> possibilities = MoveMethod
					.getpossibleRefactoring(allMethods.getIMethod(sourceMethod));

			List<Integer> possibilitiesID = new ArrayList<Integer>();

			for (String possibility : possibilities) {
				possibilitiesID.add(AllEntitiesMapping.getInstance().getByName(
						possibility));
			}

			List<Integer> candidates = entry.getValue();

			for (Integer candidateID : candidates) {
				if (possibilitiesID.contains(candidateID)) {
					// System.out.println(sourceMethod
					// + " para "
					// + AllEntitiesMapping.getInstance().getByID(
					// candidateID));
					cont++;
					PrintOutput.write(
							" Mover "
									+ sourceMethod
									+ " para classe "
									+ AllEntitiesMapping.getInstance().getByID(
											candidateID) + "\n", address);
					break;
				}

			}

		}
		PrintOutput.write(cont + " feature envy sugestões\n", address);
		PrintOutput.finish(address);
	}

	private String treatyClassNameID(String possibleCandidates) {
		// TODO Auto-generated method stub

		int indexBegin = possibleCandidates.indexOf('<');

		if (indexBegin > 0) {
			return possibleCandidates.substring(0, indexBegin);

		}

		indexBegin = possibleCandidates.indexOf('[');
		if (indexBegin > 0) {
			return possibleCandidates.substring(0, indexBegin);

		}

		return possibleCandidates;
	}

	public Map<Integer, List<Integer>> getFeaturyEnvyMethodMap() {
		return featuryEnvyMethodMap;
	}

}
