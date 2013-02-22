package refine.classes;

import java.util.ArrayList;
import java.util.List;

import refine.basic.AllEntitiesMapping;
import refine.methods.AllMethods;
import refine.methods.Method;

public class AllDependenciesClasses {

	private List<Classe> allClassList;

	public AllDependenciesClasses(AllMethods allMethods) {
		// TODO Auto-generated method stub

		if (AllEntitiesMapping.getInstance().isCreated()) {

			allClassList = new ArrayList<Classe>();
			createClassDependeciesList(allMethods.getAllMethodsList());

		} else {

			throw new ExceptionInInitializerError(
					"AllDependeciesMapping not created yet.");
		}
	}

	private void createClassDependeciesList(List<Method> list) {

		for (Method method : list) {
			int sourceClassMethodID = method.getSourceClassID();
			Classe classA = new Classe(sourceClassMethodID);

			if (allClassList.contains(classA)) {

				int index = allClassList.indexOf(classA);
				classA = allClassList.get(index);

			} else {

				allClassList.add(classA);
			}

			classA.addMethod(method);
		}

	}

	public List<Classe> getAllClassList() {
		return allClassList;
	}

}
