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
import refine.methods.AllMethods;
import refine.methods.Method;
import refine.utils.MoveMethod;
import refine.utils.PrintOutput;

public class CalculateMediaApproach {

	private int tolerance = 0;
	private final int ACCEPTABLECANDIDATESNUMBER = 3;

	private class ClassAtributes { // classe interna usada somente dentro da
									// classe
		int classID;
		int numberOfMethods;
		double similarityIndice;

		public ClassAtributes(int classID) {
			super();
			this.classID = classID;
			this.numberOfMethods = 0;
			this.similarityIndice = 0;
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

	private Map<Pair<Method, Method>, Parameters> allParameters;
	private AllMethods allMethods;

	// ###### variaveis para excrita
	private PrintOutput pOutput;
	private String activeProjectName;

	private String blindAdress;
	private String sugestionAdress;
	private String indicationAdress;

	private boolean needCalculateAll;

	public CalculateMediaApproach(
			Map<Pair<Method, Method>, Parameters> allParameters,
			AllMethods allMethods, String activeProjectName) {
		// TODO Auto-generated constructor stub
		this.allParameters = allParameters;
		this.allMethods = allMethods;

		// ###### variaveis para escrita
		this.pOutput = new PrintOutput();
		this.activeProjectName = activeProjectName;
		this.blindAdress = activeProjectName + "SaidaBlind";
		this.sugestionAdress = activeProjectName + "SaidaSugestao";

		needCalculateAll = false;

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

		Pair<Method, Method> pair = new Pair<Method, Method>();
		CoefficientsResolution resolution = new CoefficientsResolution();

		List<ClassAtributes> allClassSimilarity = new ArrayList<ClassAtributes>();

		for (int i = 0; i < allMethods.getAllMethodsList().size(); i++) {

			// #########begin conta somente aqueles que algum move é possivel
			int source = allMethods.getAllMethodsList().get(i).getNameID();
			if (!allMethods.getMoveIspossible().contains(source)) {
				continue;
			}
			// ########end;

			allClassSimilarity.clear();

			Method sourceMethod = allMethods.getAllMethodsList().get(i);

			System.out.println("Calculando o metodo " + sourceMethod);

			for (int j = 0; j < allMethods.getAllMethodsList().size(); j++) {

				if (i != j) {

					Method targetMethod = allMethods.getAllMethodsList().get(j);
					int tagertClassID = targetMethod.getSourceClassID();

					ClassAtributes atributes = new ClassAtributes(tagertClassID);

					if (allClassSimilarity.contains(atributes)) {
						int index = allClassSimilarity.indexOf(atributes);
						atributes = allClassSimilarity.get(index);

					} else {

						allClassSimilarity.add(atributes);

					}

					atributes.numberOfMethods++;
					pair.setFirst(sourceMethod);
					pair.setSecond(targetMethod);

					atributes.similarityIndice += resolution.calculate(
							allParameters.get(pair), strategy);
				}

			}
			for (ClassAtributes classAtributes : allClassSimilarity) {
				classAtributes.similarityIndice /= classAtributes.numberOfMethods;
			}

			Collections.sort(allClassSimilarity, new Comparator<Object>() {
				public int compare(Object o1, Object o2) {
					ClassAtributes c1 = (ClassAtributes) o1;
					ClassAtributes c2 = (ClassAtributes) o2;
					return Double.compare(c2.similarityIndice,
							c1.similarityIndice);
				}
			});

			ClassAtributes classOriginal = new ClassAtributes(
					sourceMethod.getSourceClassID());

			blindAnalisysBinary(allClassSimilarity, classOriginal, contador);
			// blindAnalisys(allClassSimilarity.indexOf(classOriginal),
			// contador);

			writeTraceIndications(sourceMethod, allClassSimilarity);

			if (checkPossibleSugestion(sourceMethod, allClassSimilarity)) {
				numberSuggestions++;
			}
		}

		writeStatisticsBlind(contador);
		writeExcelFormat(contador, strategy);

		pOutput.write(" Numero de sugestoes " + numberSuggestions + " \n",
				sugestionAdress);

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

			pOutput.write(classAtributes.similarityIndice + "\n",
					indicationAdress);
		}
		pOutput.write("\n", indicationAdress);

	}

	private boolean checkPossibleSugestion(Method sourceMethod,
			List<ClassAtributes> allClassSimilarity) {
		// TODO Auto-generated method stub

		ClassAtributes classOriginal = new ClassAtributes(
				sourceMethod.getSourceClassID());

		int MyPosition = allClassSimilarity.indexOf(classOriginal);

		// Para não mover para a propria classe
		if (tolerance < ACCEPTABLECANDIDATESNUMBER) {
			tolerance = ACCEPTABLECANDIDATESNUMBER;
		}

		if (MyPosition >= tolerance) {
			int[] CandidateClassID = new int[ACCEPTABLECANDIDATESNUMBER];

			for (int i = 0; i < ACCEPTABLECANDIDATESNUMBER; i++) {
				CandidateClassID[i] = allClassSimilarity.get(i).classID;
			}

			return moveIsPossible(sourceMethod, CandidateClassID);

		}

		return false;

	}

	private boolean moveIsPossible(Method sourceMethod, int[] CandidateClassID) {

		List<String> possiblesCandidateList = MoveMethod
				.getpossibleRefactoring(allMethods.getIMethod(sourceMethod));

		if (possiblesCandidateList.size() > 0) {

			for (int idCandidates : CandidateClassID) {

				for (String possibleCandidates : possiblesCandidateList) {

					// System.out.println();
					// System.out.println(possibleCandidates);
					// System.out.println();

					Integer valor = AllEntitiesMapping.getInstance().getByName(
							possibleCandidates);

					int classPossibleID;

					if (valor != null) {
						classPossibleID = valor;
					} else {
						classPossibleID = treatyNameID(possibleCandidates);
					}

					if (idCandidates == classPossibleID) {
						pOutput.write(" Mover " + sourceMethod
								+ " para classe " + possibleCandidates + "\n",
								sugestionAdress);

						return true;
					}
				}
			}

		}
		return false;
	}

	private int treatyNameID(String possibleCandidates) {
		// TODO Auto-generated method stub

		int indexBegin = possibleCandidates.indexOf('<');
		if (indexBegin > 0) {
			String treatyName = possibleCandidates.substring(0, indexBegin);
			return AllEntitiesMapping.getInstance().getByName(treatyName);
		} else {
			System.out.println(possibleCandidates + " É classe Interna");
		}
		return -1;
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
		double firstOfAll = allClassSimilarity.get(index).similarityIndice;
		double first = 1;
		double next = 1;

		if (classOriginalIndex < 0) {
			System.out.println("Nao aplicavel");
			return;
		}
		while (((first - next) / first) < PORCENTAGEM || index <= POSICAOMAXIMA) {

			ClassAtributes classAtributesFirst = allClassSimilarity.get(index);
			first = classAtributesFirst.similarityIndice;
			// System.out.println("First " + first);
			index++;

			if (index >= allClassSimilarity.size()) {
				index++;
				System.out.println(classOriginalIndex);
				break;
			}

			ClassAtributes classAtributesSecond = allClassSimilarity.get(index);
			next = classAtributesSecond.similarityIndice;
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

	private void blindAnalisysBinaryStrategy2(
			List<ClassAtributes> allClassSimilarity,
			ClassAtributes classOriginal, int[] contador) {
		// TODO Auto-generated method stub

		final int POSICAOMAXIMA = 3;
		final int POSICAOCORRETA = 0;

		final int MINIMO = 0;
		final int MEDIO = 2;
		int maximo = 2;
		int index = MEDIO + 1;

		int classOriginalIndex = allClassSimilarity.indexOf(classOriginal);

		ClassAtributes classAtributesFirst = allClassSimilarity.get(MINIMO);
		ClassAtributes classAtributesSecond = allClassSimilarity.get(MEDIO);
		double diff = Math.abs(classAtributesFirst.similarityIndice)
				- Math.abs(classAtributesSecond.similarityIndice);

		System.out.println("A diferença é " + diff);

		double nextIndice = allClassSimilarity.get(index).similarityIndice;
		while (Math.abs(nextIndice) + diff > classAtributesSecond.similarityIndice) {
			maximo++;
			nextIndice = allClassSimilarity.get(index).similarityIndice;
		}

		System.out.println("A classOriginalIndex é " + classOriginalIndex);
		System.out.println("A posição maximo é " + maximo);
		System.out.println();
		if (classOriginalIndex > maximo)
			contador[POSICAOMAXIMA]++;
		else
			contador[POSICAOCORRETA]++;

	}

	private void blindAnalisys(int indexOf, int[] contador) {
		// TODO Auto-generated method stub
		final int ONLYONEMETHODINDEX = 4;
		// considera as três primeirsas posições no ranking
		final int POSICAOMAXIMA = 3;

		if (indexOf >= POSICAOMAXIMA)
			contador[POSICAOMAXIMA]++;
		else if (indexOf < 0)
			contador[ONLYONEMETHODINDEX]++;
		else
			contador[indexOf]++;

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
