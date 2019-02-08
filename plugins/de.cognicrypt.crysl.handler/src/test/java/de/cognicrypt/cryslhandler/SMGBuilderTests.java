package de.cognicrypt.cryslhandler;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import crypto.rules.CryptSLMethod;
import crypto.rules.CryptSLRule;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import crypto.rules.TransitionEdge;
import de.cognicrypt.crysl.reader.CrySLModelReader;

public class SMGBuilderTests {

	private static CrySLModelReader csmr = null;

	@BeforeClass
	public static void setUp() throws MalformedURLException {
		csmr = new CrySLModelReader();
	}

	private CryptSLRule readRuleFromFuleName(String ruleName) {
		return csmr.readRule(new File("src/test/resources/" + ruleName + ".cryptsl"));
	}

	@Test
	public void basicTest() {
		StateMachineGraph expectedUsagePattern = new StateMachineGraph();
		StateNode minusOne = new StateNode("-1", true);
		StateNode zero = new StateNode("0", false);
		StateNode one = new StateNode("1", false, true);

		expectedUsagePattern.addNode(minusOne);
		expectedUsagePattern.addNode(zero);
		expectedUsagePattern.addNode(one);

		CryptSLMethod aap = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod abp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aap}), minusOne, zero));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), zero, one));

		Assert.assertEquals(expectedUsagePattern.getAllTransitions(), readRuleFromFuleName("Testrule1").getUsagePattern().getAllTransitions());
	}

	@Test
	public void issueCryptoAnalysis119() {
		// see https://github.com/CROSSINGTUD/CryptoAnalysis/issues/119

		StateMachineGraph expectedUsagePattern = new StateMachineGraph();
		StateNode minusOne = new StateNode("-1", true);
		StateNode zero = new StateNode("0", false, true);
		StateNode one = new StateNode("1", false, true);
		StateNode two = new StateNode("2", false, true);

		expectedUsagePattern.addNode(minusOne);
		expectedUsagePattern.addNode(zero);
		expectedUsagePattern.addNode(one);
		expectedUsagePattern.addNode(two);

		CryptSLMethod aap = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod abp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod acp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aap}), minusOne, zero));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), zero, one));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), one, one));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), one, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), two, two));

		Assert.assertEquals(expectedUsagePattern.getAllTransitions(), readRuleFromFuleName("Testrule2").getUsagePattern().getAllTransitions());
	}

	@Test
	public void mockCipherRule() {
		StateMachineGraph expectedUsagePattern = new StateMachineGraph();
		StateNode minusOne = new StateNode("-1", true);
		StateNode zero = new StateNode("0", false);
		StateNode one = new StateNode("1", false);
		StateNode two = new StateNode("2", false, true);
		StateNode three = new StateNode("3", false, true);
		StateNode four = new StateNode("4", false);

		expectedUsagePattern.addNode(minusOne);
		expectedUsagePattern.addNode(zero);
		expectedUsagePattern.addNode(one);
		expectedUsagePattern.addNode(two);
		expectedUsagePattern.addNode(three);
		expectedUsagePattern.addNode(four);

		CryptSLMethod aap = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod abp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod acp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod adp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.d", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod aep = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.e", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod afp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.f", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aap}), minusOne, zero));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), zero, one));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), one, one));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), one, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), two, two));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {adp}), one, three));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aep}), one, four));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aep}), four, four));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {adp, afp}), four, three));

		Assert.assertEquals(expectedUsagePattern.getAllTransitions(), readRuleFromFuleName("Testrule3").getUsagePattern().getAllTransitions());
	}

	@Test
	public void mockSecureRandomRule() {
		StateMachineGraph expectedUsagePattern = new StateMachineGraph();
		StateNode minusOne = new StateNode("-1", true);
		StateNode zero = new StateNode("0", false, true);
		StateNode one = new StateNode("1", false, true);
		StateNode two = new StateNode("2", false, true);

		expectedUsagePattern.addNode(minusOne);
		expectedUsagePattern.addNode(zero);
		expectedUsagePattern.addNode(one);
		expectedUsagePattern.addNode(two);

		CryptSLMethod aap = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod abp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod acp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aap}), minusOne, zero));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), zero, one));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), one, two));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), zero, two));

		StateMachineGraph actualUsagePattern = readRuleFromFuleName("Testrule4").getUsagePattern();
		Assert.assertEquals(expectedUsagePattern.getAllTransitions(), actualUsagePattern.getAllTransitions());
	}

	@Test
	public void mockSignatureRule() {
		StateMachineGraph expectedUsagePattern = new StateMachineGraph();
		StateNode minusOne = new StateNode("-1", true);
		StateNode zero = new StateNode("0", false);
		StateNode one = new StateNode("1", false);
		StateNode two = new StateNode("2", false);
		StateNode three = new StateNode("3", false, true);
		StateNode four = new StateNode("4", false);
		StateNode five = new StateNode("5", false);
		StateNode six = new StateNode("6", false, true);

		expectedUsagePattern.addNode(minusOne);
		expectedUsagePattern.addNode(zero);
		expectedUsagePattern.addNode(one);
		expectedUsagePattern.addNode(two);
		expectedUsagePattern.addNode(three);
		expectedUsagePattern.addNode(four);
		expectedUsagePattern.addNode(five);
		expectedUsagePattern.addNode(six);

		CryptSLMethod aap = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod abp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod acp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod adp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.d", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod aep = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.e", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod afp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.f", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aap}), minusOne, zero));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), zero, one));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), one, one));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), one, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), two, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {adp}), two, three));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {adp}), three, three));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), three, two));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aep}), zero, four));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aep}), four, four));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), four, five));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), five, five));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {afp}), five, six));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {afp}), six, six));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {afp}), four, six));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), six, five));

		Assert.assertEquals(expectedUsagePattern.getAllTransitions(), readRuleFromFuleName("Testrule5").getUsagePattern().getAllTransitions());
	}

	@Test
	public void mockCipherInputStreamRule() {
		StateMachineGraph expectedUsagePattern = new StateMachineGraph();
		StateNode minusOne = new StateNode("-1", true);
		StateNode zero = new StateNode("0", false);
		StateNode one = new StateNode("1", false);
		StateNode two = new StateNode("2", false, true);

		expectedUsagePattern.addNode(minusOne);
		expectedUsagePattern.addNode(zero);
		expectedUsagePattern.addNode(one);
		expectedUsagePattern.addNode(two);

		CryptSLMethod aap = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod abp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod acp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aap}), minusOne, zero));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), zero, one));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), one, one));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), one, two));

		Assert.assertEquals(expectedUsagePattern.getAllTransitions(), readRuleFromFuleName("Testrule6").getUsagePattern().getAllTransitions());
	}

	@Test
	public void mockKeyPairRule() {
		StateMachineGraph expectedUsagePattern = new StateMachineGraph();
		StateNode minusOne = new StateNode("-1", true, true);
		StateNode zero = new StateNode("0", false, true);
		StateNode one = new StateNode("1", false, true);
		StateNode two = new StateNode("2", false, true);

		expectedUsagePattern.addNode(minusOne);
		expectedUsagePattern.addNode(zero);
		expectedUsagePattern.addNode(one);
		expectedUsagePattern.addNode(two);

		CryptSLMethod aap = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod abp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod acp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aap}), minusOne, zero));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), zero, one));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), one, one));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), one, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), two, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), zero, two));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), two, one));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), minusOne, one));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), minusOne, two));

		Assert.assertEquals(expectedUsagePattern.getAllTransitions(), readRuleFromFuleName("Testrule7").getUsagePattern().getAllTransitions());
	}

	@Test
	public void mockAeadPrimitiveRule() {
		StateMachineGraph expectedUsagePattern = new StateMachineGraph();
		StateNode minusOne = new StateNode("-1", true, true);
		StateNode zero = new StateNode("0", false, true);

		expectedUsagePattern.addNode(minusOne);
		expectedUsagePattern.addNode(zero);

		CryptSLMethod aap = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(),
				new HashMap.SimpleEntry<String, String>("_", "void"));

		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aap}), minusOne, zero));
		expectedUsagePattern.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aap}), zero, zero));

		Assert.assertEquals(expectedUsagePattern.getAllTransitions(), readRuleFromFuleName("Testrule8").getUsagePattern().getAllTransitions());
	}
	
	@Test
	public void rsaDigestSignerRule() {
		File ruleFile = new File("src/test/resources/Testrule9.cryptsl");
		System.out.println(ruleFile.exists());
		CryptSLRule r = csmr.readRule(ruleFile);
		
		StateMachineGraph s = new StateMachineGraph();
		StateNode minusOne = new StateNode("-1", true);
		StateNode zero = new StateNode("0", false);
		StateNode one = new StateNode("1", false);
		StateNode two = new StateNode("2", false);
		StateNode three = new StateNode("3", false, true);
		StateNode four = new StateNode("4", false);
		StateNode five = new StateNode("5", false);
		StateNode six = new StateNode("6", false);
		StateNode seven = new StateNode("7", false, true);
		
		s.addNode(minusOne);
		s.addNode(zero);
		s.addNode(one);
		s.addNode(two);
		s.addNode(three);
		s.addNode(four);
		s.addNode(five);
		s.addNode(six);
		s.addNode(seven);
		
		CryptSLMethod aap = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.a", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(), new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod acp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.c", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(), new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod adp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.d", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(), new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod aep = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.e", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(), new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod abp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.b", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(), new HashMap.SimpleEntry<String, String>("_", "void"));
		CryptSLMethod afp = new CryptSLMethod("de.cognicrypt.cryslhandler.TestA.f", new ArrayList<Entry<String, String>>(), new ArrayList<Boolean>(), new HashMap.SimpleEntry<String, String>("_", "void"));
		
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aap}), minusOne, zero));
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), zero, one));
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {adp}), one, two));
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {aep}), two, three));

		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {abp}), three, four));
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {acp}), four, five));
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {adp}), five, six));
		s.addEdge(new TransitionEdge(Arrays.asList(new CryptSLMethod[] {afp}), six, seven));
		
		System.out.println(s);
		StateMachineGraph actualUsagePattern = r.getUsagePattern();
		Assert.assertEquals(s.getAllTransitions(), actualUsagePattern.getAllTransitions());
	}

}