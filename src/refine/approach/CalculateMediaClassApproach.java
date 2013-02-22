package refine.approach;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import refine.basic.AllEntitiesMapping;
import refine.basic.CoefficientsResolution;
import refine.basic.CoefficientsResolution.CoefficientStrategy;
import refine.basic.Pair;
import refine.basic.Parameters;
import refine.classes.Classe;
import refine.methods.AllMethods;
import refine.methods.Method;
import refine.utils.PrintOutput;

public class CalculateMediaClassApproach {

	class ClassAtributes { // classe interna usada somente dentro do metodo
		int classID;
		double indice;

		public ClassAtributes(int classID) {
			super();
			this.classID = classID;
			this.indice = 0;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ClassAtributes) {
				// TODO Auto-generated method stub
				ClassAtributes other = (ClassAtributes) obj;
				if (this.classID != other.classID) {
					return false;
				}
				return true;
			}
			return false;
		}

	}

	private Map<Pair<Method, Classe>, Parameters> allParameters;
	private AllMethods allMethods;
	private List<Classe> allClassList;

	// ###### variaveis para excrita
	private PrintOutput pOutput;
	private String activeProjectName;

	private String blindAdress;
	private String sugestionAdress;
	private String indicationAdress;

	private boolean needCalculateAll;

	public CalculateMediaClassApproach(
			Map<Pair<Method, Classe>, Parameters> allParameters,
			AllMethods allMethods, List<Classe> allClassList,
			String activeProjectName) {
		// TODO Auto-generated constructor stub
		this.allParameters = allParameters;
		this.allMethods = allMethods;
		this.allClassList = allClassList;

		// ###### variaveis para escrita
		this.pOutput = new PrintOutput();
		this.activeProjectName = activeProjectName;
		this.blindAdress = activeProjectName + "SaidaBlind";
		this.sugestionAdress = activeProjectName + "SaidaSugestao";

		this.needCalculateAll = false;

	}

	public void calculate(CoefficientStrategy strategy) {

		// ##########Escreve a estrategia usada
		System.out.println();
		System.out.println(strategy);
		System.out.println();
		// ####### end

		indicationAdress = activeProjectName + " " + strategy + " indication";

		int contador[] = { 0, 0, 0, 0, 0 };
		int numberSuggestions = 0;

		pOutput.write("\n " + strategy + "\nMetodos com menos que "
				+ allMethods.getNumberOfExcluded()
				+ " dependencias excluidos\n", blindAdress);
		pOutput.write("\n " + strategy + "\nMetodos com menos que "
				+ allMethods.getNumberOfExcluded()
				+ " dependencias excluidos\n", sugestionAdress);
		pOutput.write("\n " + strategy + "\nMetodos com menos que "
				+ allMethods.getNumberOfExcluded()
				+ " dependencias excluidos\n", indicationAdress);
		Pair<Method, Classe> p1 = new Pair<Method, Classe>();
		CoefficientsResolution resolution = new CoefficientsResolution();

		List<ClassAtributes> allClass;

		for (int i = 0; i < allMethods.getAllMethodsList().size(); i++) {

			// #########begin conta somente aqueles que algum move é possivel
			int source = allMethods.getAllMethodsList().get(i).getNameID();
			if (!allMethods.getMoveIspossible().contains(source)) {
				continue;
			}
			// ########end;

			allClass = new ArrayList<ClassAtributes>();

			Method sourceMethod = allMethods.getAllMethodsList().get(i);

			System.out.println("Calculando o metodo " + sourceMethod);

			for (int j = 0; j < allClassList.size(); j++) {

				Classe targetClass = allClassList.get(j);

				ClassAtributes atributes = new ClassAtributes(
						targetClass.getNameID());

				if (allClass.contains(atributes)) {
					int index = allClass.indexOf(atributes);
					atributes = allClass.get(index);

				} else {

					allClass.add(atributes);

				}

				p1.setFirst(sourceMethod);
				p1.setSecond(targetClass);

				atributes.indice += resolution.calculate(allParameters.get(p1),
						strategy);
			}

			Collections.sort(allClass, new Comparator<Object>() {
				public int compare(Object o1, Object o2) {
					ClassAtributes c1 = (ClassAtributes) o1;
					ClassAtributes c2 = (ClassAtributes) o2;
					return Double.compare(c2.indice, c1.indice);
				}
			});

			ClassAtributes classOriginal = new ClassAtributes(
					sourceMethod.getSourceClassID());

			blindAnalisysBinary(allClass, classOriginal, contador);
			//blindAnalisys(allClass.indexOf(classOriginal), contador);

			writeTraceIndications(sourceMethod, allClass);

			pOutput.write(" Numero de sugestoes " + numberSuggestions + " \n",
					sugestionAdress);

		}

		writeStatisticsBlind(contador);
		writeExcelFormat(contador, strategy);

		if (!needCalculateAll) {

			pOutput.finish(blindAdress);

			pOutput.finish(sugestionAdress);

			pOutput.finish(indicationAdress);
		}

	}

	private void writeTraceIndications(Method sourceMethod,
			List<ClassAtributes> allClassSimilarity) {
		// TODO Auto-generated method stub
		String method = AllEntitiesMapping.getInstance().getByID(
				sourceMethod.getNameID());

		String classe = AllEntitiesMapping.getInstance().getByID(
				sourceMethod.getSourceClassID());

		ClassAtributes classOriginal = new ClassAtributes(
				sourceMethod.getSourceClassID());

		pOutput.write("Similaridade para método " + method + "\n",
				indicationAdress);

		pOutput.write("Ranking classe original " + classe + " "
				+ (allClassSimilarity.indexOf(classOriginal) + 1) + "º \n",
				indicationAdress);

		for (ClassAtributes classAtributes : allClassSimilarity) {
			pOutput.write(
					AllEntitiesMapping.getInstance().getByID(
							classAtributes.classID)
							+ " ", indicationAdress);

			pOutput.write(classAtributes.indice + "\n", indicationAdress);
		}
		pOutput.write("\n", indicationAdress);

	}

	private void writeExcelFormat(int[] contador, CoefficientStrategy strategy) {
		// TODO Auto-generated method stub

		float total = 0;

		for (int num : contador) {
			total += num;
		}

		String excell = "Excell" + activeProjectName;

		pOutput.write("\n" + strategy + "\t ", excell);
		pOutput.write(contador[0] + "\t ", excell);
		pOutput.write(contador[1] + "\t ", excell);
		pOutput.write(contador[2] + "\t ", excell);
		pOutput.write(contador[3] + "\t ", excell);
		pOutput.write((int) total + "\t ", excell);
	}
	
	private void blindAnalisysBinary(List<ClassAtributes> allClassSimilarity,
			ClassAtributes classOriginal, int[] contador) {
		// TODO Auto-generated method stub

		final int POSICAOMAXIMA = 3;
		final int POSICAOCORRETA = 0;
		final double PORCENTAGEM = 0.03;
		final double MAXIMAPORCENTAGEM = 0.10;

		int classOriginalIndex = allClassSimilarity.indexOf(classOriginal);
		int index = 0;
		double firstOfAll = allClassSimilarity.get(index).indice;
		double first = 1;
		double next = 1;

		if (classOriginalIndex < 0) {
			System.out.println("Nao aplicavel");
			return;
		}
		while (((first - next) / first) < PORCENTAGEM || index <= POSICAOMAXIMA) {

			ClassAtributes classAtributesFirst = allClassSimilarity.get(index);
			first = classAtributesFirst.indice;
			// System.out.println("First " + first);
			index++;

			if (index >= allClassSimilarity.size()) {
				index++;
				System.out.println(classOriginalIndex);
				break;
			}

			ClassAtributes classAtributesSecond = allClassSimilarity.get(index);
			next = classAtributesSecond.indice;
			// System.out.println("next " + next);

			if (classOriginalIndex < index) {
				// System.out.println("Paraou no break pos " + index);
				break;
			}

			if ((firstOfAll - next) / firstOfAll > MAXIMAPORCENTAGEM
					&& index >= POSICAOMAXIMA) {
				// System.out.println("Paraou no MAXIMAPORCENTAGEM pos " +
				// index);
				break;
			}

		}
		// System.out.println("classOriginalIndex " + classOriginalIndex);
		// System.out.println("index " + index);
		if (classOriginalIndex < index) {
			contador[POSICAOCORRETA]++;
		} else {
			contador[POSICAOMAXIMA]++;
		}
		// System.out.println();

	}
	
	private void blindAnalisys(int indexOf, int[] contador) {
		// TODO Auto-generated method stub

		// considera as três primeirsas posições no ranking
		final int POSICAOMAXIMA = 3;
		final int ONLYONEMETHOD = 4;

		if (indexOf > POSICAOMAXIMA)
			contador[POSICAOMAXIMA]++;
		else if (indexOf < 0)
			contador[ONLYONEMETHOD]++;
		else
			contador[indexOf]++;

	}

	private void writeStatisticsBlind(int[] contador) {
		float total = 0;

		for (int num : contador) {
			total += num;
		}

		pOutput.write("1º " + contador[0] + " " + 100 * contador[0] / total
				+ "%\n", blindAdress);
		pOutput.write("2º " + contador[1] + " " + 100 * contador[1] / total
				+ "%\n", blindAdress);
		pOutput.write("3º " + contador[2] + " " + 100 * contador[2] / total
				+ "%\n", blindAdress);
		pOutput.write("Erros " + contador[3] + " " + 100 * contador[3] / total
				+ "%\n", blindAdress);
		pOutput.write("Classes com apenas 1 método " + contador[4] + " " + 100
				* contador[4] / total + "%\n", blindAdress);
		pOutput.write("Total " + (int) total + " " + 100 * total / total
				+ "%\n", blindAdress);

	}

	public void calculateForAllStrategies() {

		needCalculateAll = true;

		for (CoefficientStrategy strategy : CoefficientsResolution
				.AllCoefficientStrategy()) {
			calculate(strategy);
		}

		pOutput.finish(indicationAdress);

		pOutput.finish(blindAdress);

		pOutput.finish(sugestionAdress);
	}
}
