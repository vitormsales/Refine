package refine.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import refine.basic.AllEntitiesMapping;
import refine.methods.Method;

public class FeatureEnvy {

	private static FeatureEnvy instance;
	private Map<Integer, List<Integer>> featuryEnvyMethodMap;
	private Map<Integer, Integer> frequency;
	private boolean hasEnvyClass;

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

	public void detectFeatureEnvy(List<Method> allMethodsList) {

		System.out.println("FEATURE ENVY");

		for (Method method : allMethodsList) {
			// System.out.println(method);
			int myOriginalclassID = method.getSourceClassID();

			frequency.clear();
			hasEnvyClass = false;
			for (Integer ID : method.getMethodsAcessDependenciesID()) {
				int key = getDependencyClassID(ID);
				if (!frequency.containsKey(key)) {
					frequency.put(key, 1);
				} else {
					int value = frequency.get(key);
					frequency.put(key, ++value);
				}
			}

			Iterator<Entry<Integer, Integer>> it = frequency.entrySet()
					.iterator();

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
//				System.out.println("FEature Envy para o metodo " + method);
				featuryEnvyMethodMap.put(myOriginalclassID, CandidateList);
//				for (Integer id : CandidateList) {
//					System.out.println(AllEntitiesMapping.getInstance()
//							.getByID(id));
//				}
			}
			// System.out.println();
		}

	}

	private List<Integer> getEnviedClass(int myOriginalclassID) {
		int MyValor = 0;

		if (frequency.containsKey(myOriginalclassID)) {
			MyValor = frequency.get(myOriginalclassID);
		}

		List<Integer> CandidateList = new ArrayList<Integer>();

		Iterator<Entry<Integer, Integer>> it = frequency.entrySet().iterator();

		while (it.hasNext()) {
			Entry<Integer, Integer> entry = it.next();
			if (MyValor <= entry.getValue() && myOriginalclassID != entry.getKey()) {
				if (InternalClass.getInstance().isInternal(entry.getKey())) {
					CandidateList.add(entry.getKey());
//					System.out.println("VALOR");
//					System.out.println(AllEntitiesMapping.getInstance()
//							.getByID(entry.getKey()) + " " + entry.getValue());
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

		int key = AllEntitiesMapping.getInstance().getByName(parts[1]);

		if (InternalClass.getInstance().isInternal(key)) {
			return parts[1];
		}

		parts = dependency.split(";;", 2);
		return parts[0];
	}
}
