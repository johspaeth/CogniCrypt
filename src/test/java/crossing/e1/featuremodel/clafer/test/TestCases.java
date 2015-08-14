package crossing.e1.featuremodel.clafer.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.clafer.ast.AstAbstractClafer;
import org.clafer.ast.AstClafer;
import org.clafer.ast.AstConcreteClafer;

import crossing.e1.configurator.ReadConfig;
import crossing.e1.featuremodel.clafer.ClaferModel;
import crossing.e1.featuremodel.clafer.InstanceGenerator;
import crossing.e1.featuremodel.clafer.ParseClafer;
import crossing.e1.featuremodel.clafer.StringLableMapper;

/**
 * @author Ram
 *
 */

public class TestCases {

	public static void main(String[] args) {
		TestCases test = new TestCases();
		String path = new ReadConfig().getClaferPath();
		ClaferModel model = new ClaferModel(path);

		// InstanceGenerator inst = test.getInstance(model);
		// test.displayInstances(inst);
		// test.displayConstraints(model);
		// test.displaySuperClafers(model);
		test.displayTasks(model);
		test.displayProperties(model);

	}

	/**
	 * @param model
	 */
	private void displayProperties(ClaferModel model) {

		System.out.println("----- Listing Propertie -----");
		ParseClafer parser = new ParseClafer();

		System.out.println("Task Name "
				+ StringLableMapper.getTaskLables().keySet().toArray()[0]
						.toString() + "\nProperties are");
		for (AstConcreteClafer key : StringLableMapper.getPropertiesLables()
				.keySet()) {
			System.out.println(key + " => "
					+ StringLableMapper.getPropertiesLables().get(key));

		}

	}

	/**
	 * @param model
	 */
	private void displayTasks(ClaferModel model) {
		model.getTaskList(model.getModel());
		System.out.println("----- Listing Tasks -----");
		for (String inst : StringLableMapper.getTaskLables().keySet()) {

			System.out.println(inst + " => "
					+ StringLableMapper.getTaskLables().get(inst));
		}
		ParseClafer parser = new ParseClafer();
		parser.setConstraintClafers(StringLableMapper.getTaskLables().get(
				StringLableMapper.getTaskLables().keySet().toArray()[0]
						.toString()));

	}

	private void displaySuperClafers(ClaferModel model) {

		System.out.println("--Testing displaySuperClafers Method--");
		Map<String, AstConcreteClafer> taskList = new HashMap<String, AstConcreteClafer>();
		taskList = model.getTaskList(model.getModel());
		for (String key : taskList.keySet()) {
			System.out.println("VALUE : " + taskList.get(key));
		}

	}

	private void displayConstraints(ClaferModel model) {
		Map<String, AstConcreteClafer> constraints = model
				.getConstraintClafers();
		System.out.println("--Testing Constrainable properties--");
		for (String key : constraints.keySet()) {
			System.out.println("VALUE : " + constraints.get(key));
		}

	}

	Map<ArrayList<AstConcreteClafer>, ArrayList<Integer>> getMap() {
		Map<ArrayList<AstConcreteClafer>, ArrayList<Integer>> filters = new HashMap<ArrayList<AstConcreteClafer>, ArrayList<Integer>>();
		// filters.put(null, {3});
		// filters.put(null, {256});
		return filters;
	}

	void displayInstances(InstanceGenerator instance) {
		System.out.println("----Diplaying instances----");
		for (String b : instance.getInstances().keySet())
			System.out.println(instance.displayInstanceValues(instance
					.getInstances().get(b), ""));
	}

	InstanceGenerator getInstance(ClaferModel model) {
		System.out.println("-- Testing instance Generator method--");
		InstanceGenerator instance = new InstanceGenerator();
		;
		instance.generateInstances(model, getMap());
		System.out.println("There are " + instance.getNoOfInstances()
				+ " instances");
		return instance;
		// instance.displayInstances();
	}

}
