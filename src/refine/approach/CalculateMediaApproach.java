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

	final int indexCORRETA = 0;
	final int indexSUGESTAO = 1;
	final int indexERRADO = 3;

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

			normalize(allClassSimilarity);

			writeTraceIndications(sourceMethod, allClassSimilarity);

			blindAnalisysBinary(allClassSimilarity, sourceMethod, contador);
			// blindAnalisys(allClassSimilarity, sourceMethod, contador);

		}

		pOutput.write(
				" Numero de sugestoes " + contador[indexSUGESTAO] + " \n",
				sugestionAdress);

		writeStatisticsBlind(contador);
		writeExcelFormat(contador, strategy);

		if (!needCalculateAll) {

			pOutput.finish(blindAdress);

			pOutput.finish(sugestionAdress);

			pOutput.finish(indicationAdress);
		}

	}

	private void normalize(List<ClassAtributes> allClassSimilarity) {
		// TODO Auto-generated method stub

		double bigger = allClassSimilarity.get(0).similarityIndice;
		double minor = allClassSimilarity.get(0).similarityIndice;

		for (int i = 0; i < allClassSimilarity.size(); i++) {
			if (allClassSimilarity.get(i).similarityIndice > bigger) {
				bigger = allClassSimilarity.get(i).similarityIndice;
			} else if (allClassSimilarity.get(i).similarityIndice < minor) {
				minor = allClassSimilarity.get(i).similarityIndice;
			}
		}

		bigger -= minor;

		for (int i = 0; i < allClassSimilarity.size(); i++) {
			allClassSimilarity.get(i).similarityIndice -= minor;
			allClassSimilarity.get(i).similarityIndice /= bigger;
		}

	}

	private boolean checkPossibleSugestion(Method sourceMethod,
			List<ClassAtributes> allClassSimilarity, int posMax) {
		// TODO Auto-generated method stub

		ClassAtributes classOriginal = new ClassAtributes(
				sourceMethod.getSourceClassID());

		int MyPosition = allClassSimilarity.indexOf(classOriginal);

		if (MyPosition > posMax) {
			int[] CandidateClassID = new int[posMax];

			for (int i = 0; i < posMax; i++) {
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
						classPossibleID = treatyClassNameID(possibleCandidates);
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

	private int treatyClassNameID(String possibleCandidates) {
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

	private void writeExcelFormat(int[] contador, CoefficientStrategy strategy) {
		// TODO Auto-generated method stub

		float total = 0;

		total += contador[indexCORRETA] + contador[indexERRADO];

		String excell = "Excell" + activeProjectName;

		pOutput.write("\n" + strategy + "\t ", excell);
		pOutput.write(contador[indexCORRETA] + "\t ", excell);
		pOutput.write(contador[indexSUGESTAO] + "\t ", excell);
		pOutput.write(contador[indexERRADO] + "\t ", excell);
		pOutput.write((int) total + "\t ", excell);
	}

	private void writeStatisticsBlind(int[] contador) {
		float total = 0;

		total += contador[indexCORRETA] + contador[indexERRADO];

		pOutput.write("Correto " + contador[indexCORRETA] + " " + 100
				* contador[indexCORRETA] / total + "%\n", blindAdress);
		pOutput.write("Sugestões " + contador[indexSUGESTAO] + " " + 100
				* contador[indexSUGESTAO] / contador[indexERRADO] + "%\n",
				blindAdress);
		pOutput.write("Erros " + contador[indexERRADO] + " " + 100
				* contador[indexERRADO] / total + "%\n", blindAdress);
		pOutput.write("Total " + (int) total + " " + 100 * total / total
				+ "%\n", blindAdress);

	}

	private void blindAnalisysBinary(List<ClassAtributes> allClassSimilarity,
			Method sourceMethod, int[] contador) {
		// TODO Auto-generated method stub

		ClassAtributes classOriginal = new ClassAtributes(
				sourceMethod.getSourceClassID());

		final int POSICAOMAXIMA = 3;
		final double PORCENTAGEM = 0.5;
		final double MAXIMAPORCENTAGEM = 0.15;

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

			if (index > allClassSimilarity.size()) {
				index++;
				// System.out.println(classOriginalIndex);
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
			contador[indexCORRETA]++;
		} else {
			contador[indexERRADO]++;
		}
		// System.out.println();
		if (checkPossibleSugestion(sourceMethod, allClassSimilarity, index)) {
			contador[indexSUGESTAO]++;
		}

	}

	private void blindAnalisys(List<ClassAtributes> allClassSimilarity,
			Method sourceMethod, int[] contador) {
		// TODO Auto-generated method stub

		ClassAtributes classOriginal = new ClassAtributes(
				sourceMethod.getSourceClassID());

		int indexOf = allClassSimilarity.indexOf(classOriginal);

		final int ONLYONEMETHODINDEX = 4;
		// considera as três primeirsas posições no ranking
		final int POSICAOMAXIMA = 2;

		if (indexOf > POSICAOMAXIMA)
			contador[POSICAOMAXIMA + 1]++;
		else if (indexOf < 0)
			contador[ONLYONEMETHODINDEX]++;
		else
			contador[indexOf]++;

		if (checkPossibleSugestion(sourceMethod, allClassSimilarity,
				POSICAOMAXIMA)) {
			contador[indexSUGESTAO]++;
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
