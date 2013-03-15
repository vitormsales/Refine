package refine.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import refine.Activator;
import refine.approach.CalculateMediaApproach;
import refine.approach.CalculateMediaClassApproach;
import refine.ast.DeepDependencyVisitor;
import refine.basic.AllEntitiesMapping;
import refine.basic.CoefficientsResolution.CoefficientStrategy;
import refine.classes.AllDependenciesClasses;
import refine.classes.StatisticsMethod2MethodsOfClass;
import refine.methods.AllMethods;
import refine.methods.Method;
import refine.methods.StatisticsMethod2Method;
import dclsuite.util.DCLUtil;
import envy.FeatureEnvy;
import envy.InternalClass;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class RefineSimilarityHandler extends AbstractHandler {
	/**
	 * The constructor.
	 * 
	 */

	protected List<DeepDependencyVisitor> allDeepDependency;

	public RefineSimilarityHandler() {
		allDeepDependency = new ArrayList<DeepDependencyVisitor>();
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {

		try {

			IEditorPart editorPart = Activator.getDefault().getWorkbench()
					.getActiveWorkbenchWindow().getActivePage()
					.getActiveEditor();

			String activeProjectName;
			int numberOfClass=0;

			if (editorPart != null) {

				IFileEditorInput input = (IFileEditorInput) editorPart
						.getEditorInput();
				IFile file = input.getFile();
				IProject activeProject = file.getProject();
				activeProjectName = activeProject.getName();
				IProject project = ResourcesPlugin.getWorkspace().getRoot()
						.getProject(activeProjectName);
				IJavaProject javaProject = JavaCore.create(project);

				for (String className : DCLUtil.getClassNames(project)) {
					// System.out.println("IMPORTANTE");
					// System.out.println(className);
					// System.out.println();
					if (className == null) {
						continue;
					}

					InternalClass.getInstance().putNewInternalClass(className);

					IFile resource = DCLUtil.getFileFromClassName(javaProject,
							className);
					ICompilationUnit unit = ((ICompilationUnit) JavaCore
							.create((IFile) resource));
					// System.out.println(unit);
					System.out.println("AST para " + unit.getElementName());
					DeepDependencyVisitor deepDependency = new DeepDependencyVisitor(
							unit);
					this.allDeepDependency.add(deepDependency);
					numberOfClass++;
				}

				System.out.println(" inciando AllEntitiesMapping");
				AllEntitiesMapping.getInstance().createAllDependeciesMapping(
						allDeepDependency);
				System.out.println(" Terminou AllEntitiesMapping");

				System.out.println(" inciando AllMethods");
				AllMethods allMethods = new AllMethods(allDeepDependency);
				System.out.println(" Terminou AllMethods");

				FeatureEnvy.getInstance().sugestFeatureEnvyMoves(allMethods);
				// for (Method method : allMethods.getAllMethodsList()) {
				// System.out.println(method);
				// for (Integer ID : method.getMethodsAcessDependenciesID()) {
				// System.out.println(AllEntitiesMapping.getInstance().getByID(ID));
				//
				// }
				// System.out.println();
				// }

				// tornando visivel para o coletor de lixo
				allDeepDependency = null;

				// allMethods.excludeConstructors();
				// allMethods.excludeDependeciesLessThan(5);

				// ########## Method2Method begin

				System.out.println("iniciando StatisticsMethod2Method");
				StatisticsMethod2Method m2m = new StatisticsMethod2Method(
						allMethods.getAllMethodsList());
				System.out.println("Terminou StatisticsMethod2Method");

				System.out.println("iniciando CalculateMediaApproach");
				CalculateMediaApproach mediaApproach = new CalculateMediaApproach(
						m2m.getAllParameters(), allMethods, activeProjectName, numberOfClass);
				System.out.println("Terminou CalculateMediaApproach");

				m2m = null;
				allMethods = null;

				System.out.println("iniciando calculateForAllStrategies");
				mediaApproach.calculate(CoefficientStrategy.Jaccard);
				System.out.println("Terminou calculateForAllStrategies");
				// ########## Method2Method end

				// ########## Method2UnionMethod begin
				// AllDependenciesClasses allClasses = new
				// AllDependenciesClasses(
				// allMethods);
				//
				// System.out.println("StatisticsMethod2MethodsOfClass\n");
				// StatisticsMethod2MethodsOfClass m2mc = new
				// StatisticsMethod2MethodsOfClass(
				// allMethods.getAllMethodsList(),
				// allClasses.getAllClassList());
				// System.out.println("StatisticsMethod2MethodsOfClass FIM\n");
				//
				// System.out.println("CalculateMediaClassApproach\n");
				// CalculateMediaClassApproach mediaClassApproach = new
				// CalculateMediaClassApproach(
				// m2mc.getAllParameters(), allMethods,
				// allClasses.getAllClassList(), activeProjectName);
				// System.out.println("CalculateMediaClassApproach FIM\n");
				// mediaClassApproach.calculateForAllStrategies();
				// ########## Method2UnionMethod end
				System.out.println("Fim");
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}

		IWorkbenchWindow window = HandlerUtil
				.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(window.getShell(), "Refine DialogBox",
				"Operação Finalizada com Sucesso");
		return null;
	}
}
