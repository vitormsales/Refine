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
import refine.utils.MoveMethod;
import refine.utils.PrintOutput;

public class CalculateMediaClassApproach {

	class ClassAtributes { // classe interna usada somente dentro do metodo
		int classID;
		double similarityIndice;

		public ClassAtributes(int classID) {
			super();
			this.classID = classID;
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

	private Map<Pair<Method, Classe>, Parameters> allParameters;
	private AllMethods allMethods;
	private List<Classe> allClassList;

	final int indexCORRETA = 0;
	final int indexSUGESTAO = 1;
	final int indexERRADO = 3;

	// ###### variaveis para excrita
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

		PrintOutput.write("\n " + strategy + "\nMetodos com menos que "
				+ allMethods.getNumberOfExcluded()
				+ " dependencias excluidos\n", blindAdress);
		PrintOutput.write("\n " + strategy + "\nMetodos com menos que "
				+ allMethods.getNumberOfExcluded()
				+ " dependencias excluidos\n", sugestionAdress);
		PrintOutput.write("\n " + strategy + "\nMetodos com menos que "
				+ allMethods.getNumberOfExcluded()
				+ " dependencias excluidos\n", indicationAdress);
		Pair<Method, Classe> p1 = new Pair<Method, Classe>();
		CoefficientsResolution resolution = new CoefficientsResolution();

		List<ClassAtributes> allClassSimilarity;

		for (int i = 0; i < allMethods.getAllMethodsList().size(); i++) {

			// #########begin conta somente aqueles que algum move é possivel
			int source = allMethods.getAllMethodsList().get(i).getNameID();
			if (!allMethods.getMoveIspossible().contains(source)) {
				continue;
			}
			// ########end;

			allClassSimilarity = new ArrayList<ClassAtributes>();

			Method sourceMethod = allMethods.getAllMethodsList().get(i);

			System.out.println("Calculando o metodo " + sourceMethod);

			for (int j = 0; j < allClassList.size(); j++) {

				Classe targetClass = allClassList.get(j);

				ClassAtributes atributes = new ClassAtributes(
						targetClass.getNameID());

				if (allClassSimilarity.contains(atributes)) {
					int index = allClassSimilarity.indexOf(atributes);
					atributes = allClassSimilarity.get(index);

				} else {

					allClassSimilarity.add(atributes);

				}

				p1.setFirst(sourceMethod);
				p1.setSecond(targetClass);

				atributes.similarityIndice += resolution.calculate(
						allParameters.get(p1), strategy);
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

			blindAnalisysBinary(allClassSimilarity, sourceMethod, contador);
			// blindAnalisys(allClass, sourceMethod, contador);

			writeTraceIndications(sourceMethod, allClassSimilarity);

		}

		PrintOutput.write(
				" Numero de sugestoes " + contador[indexSUGESTAO] + " \n",
				sugestionAdress);

		writeStatisticsBlind(contador);
		writeExcelFormat(contador, strategy);

		if (!needCalculateAll) {

			PrintOutput.finish(blindAdress);

			PrintOutput.finish(sugestionAdress);

			PrintOutput.finish(indicationAdress);
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

		PrintOutput.write("Similaridade para método " + method + "\n",
				indicationAdress);

		PrintOutput.write("Ranking classe original " + classe + " "
				+ (allClassSimilarity.indexOf(classOriginal) + 1) + "º \n",
				indicationAdress);

		for (ClassAtributes classAtributes : allClassSimilarity) {
			PrintOutput.write(
					AllEntitiesMapping.getInstance().getByID(
							classAtributes.classID)
							+ " ", indicationAdress);

			PrintOutput.write(classAtributes.similarityIndice + "\n",
					indicationAdress);
		}
		PrintOutput.write("\n", indicationAdress);

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

	private void blindAnalisysBinary(List<ClassAtributes> allClassSimilarity,
			Method sourceMethod, int[] contador) {
		// TODO Auto-generated method stub

		ClassAtributes classOriginal = new ClassAtributes(
				sourceMethod.getSourceClassID());

		final int POSICAOMAXIMA = 3;
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
			contador[indexERRADO]++;
		else if (indexOf > 0)
			contador[indexCORRETA]++;

		if (checkPossibleSugestion(sourceMethod, allClassSimilarity,
				POSICAOMAXIMA)) {
			contador[indexSUGESTAO]++;
		}

	}

	private void writeExcelFormat(int[] contador, CoefficientStrategy strategy) {
		// TODO Auto-generated method stub

		float total = 0;

		total += contador[indexCORRETA] + contador[indexERRADO];

		String excell = "Excell" + activeProjectName;

		PrintOutput.write("\n" + strategy + "\t ", excell);
		PrintOutput.write(contador[indexCORRETA] + "\t ", excell);
		PrintOutput.write(contador[indexSUGESTAO] + "\t ", excell);
		PrintOutput.write(contador[indexERRADO] + "\t ", excell);
		PrintOutput.write((int) total + "\t ", excell);
	}

	private void writeStatisticsBlind(int[] contador) {
		float total = 0;

		total += contador[indexCORRETA] + contador[indexERRADO];

		PrintOutput.write("Correto " + contador[indexCORRETA] + " " + 100
				* contador[indexCORRETA] / total + "%\n", blindAdress);
		PrintOutput.write("Sugestões " + contador[indexSUGESTAO] + " " + 100
				* contador[indexSUGESTAO] / contador[indexERRADO] + "%\n",
				blindAdress);
		PrintOutput.write("Erros " + contador[indexERRADO] + " " + 100
				* contador[indexERRADO] / total + "%\n", blindAdress);
		PrintOutput.write("Total " + (int) total + " " + 100 * total / total
				+ "%\n", blindAdress);

	}

	public void calculateForAllStrategies() {

		needCalculateAll = true;

		for (CoefficientStrategy strategy : CoefficientsResolution
				.AllCoefficientStrategy()) {
			calculate(strategy);
		}

		PrintOutput.finish(indicationAdress);

		PrintOutput.finish(blindAdress);

		PrintOutput.finish(sugestionAdress);
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
						PrintOutput.write(" Mover " + sourceMethod
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
			System.out.println(treatyName);
			return AllEntitiesMapping.getInstance().getByName(treatyName);
		} else {
			System.out.println(possibleCandidates + " É classe Interna");
		}
		return -1;
	}
}
