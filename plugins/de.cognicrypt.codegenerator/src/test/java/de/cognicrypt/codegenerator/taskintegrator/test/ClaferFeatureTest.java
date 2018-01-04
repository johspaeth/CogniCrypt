package de.cognicrypt.codegenerator.taskintegrator.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.Test;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.FeatureProperty;

public class ClaferFeatureTest {

	public static final String testFileFolder = "src/test/resources/taskintegrator/";

	@Test
	public final void testPropertyAmount() {
		ArrayList<FeatureProperty> featureProperties = new ArrayList<>();
		for (int i = 0; i < 9; i++) {
			featureProperties.add(new FeatureProperty("featureProperty" + String.valueOf(i), "propertyType"));
		}
		ClaferFeature claferFeature = new ClaferFeature(Constants.FeatureType.CONCRETE, "testFeature", "");
		claferFeature.setFeatureProperties(featureProperties);

		assertEquals(claferFeature.getfeatureProperties(), featureProperties);
	}
	
	public static boolean filesEqual(String expectedFilename, String actualFilename) throws IOException {
		Path expectedFilePath = Paths.get(expectedFilename);
		Path actualFilePath = Paths.get(actualFilename);

		byte[] expectedBytes = Files.readAllBytes(expectedFilePath);
		byte[] actualBytes = Files.readAllBytes(actualFilePath);
		
		return Arrays.equals(expectedBytes, actualBytes);
	}

	@Test
	public final void testClaferFeatureToString() throws IOException {
		// file to test against
		String expectedFilename = testFileFolder + "testFile1.cfr";

		// programmatically created Clafer feature
		ClaferFeature cfrFeature = new ClaferFeature(Constants.FeatureType.ABSTRACT, "Algorithm", "");
		cfrFeature.getfeatureProperties().add(new FeatureProperty("securityLevel", "Security"));

		// generate file from ClaferFeature instance
		String actualFilename = testFileFolder + "testFile1_tmp.cfr";
		Path actualPath = Paths.get(actualFilename);

		Charset charset = Charset.forName("UTF-8");
		String s = cfrFeature.toString();
		try (BufferedWriter writer = Files.newBufferedWriter(actualPath, charset)) {
			writer.write(s, 0, s.length());
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
		}

		// compare the files
		assertTrue(filesEqual(expectedFilename, actualFilename));
	}
	
	@Test
	public final void testWriteCFRFile() {
		fail("Not yet implemented");
	}

	@Test
	public final void testImplementMissingFeatures() {
		ArrayList<ClaferFeature> featureList = new ArrayList<>();
		ClaferFeature cfrFeature = new ClaferFeature(Constants.FeatureType.ABSTRACT, "AES", "Algorithm");
		cfrFeature.implementMissingFeatures(featureList);
		
		boolean featureFound = false;
		for (ClaferFeature currentFeature : featureList) {
			if (currentFeature.getFeatureName().equals("Algorithm")) {
				featureFound = true;
				break;
			}
		}

		assertTrue(featureFound);
	}

	@Test
	public final void testNoEmptyFeatures() {
		ArrayList<ClaferFeature> featureList = new ArrayList<>();
		ClaferFeature cfrFeature = new ClaferFeature(Constants.FeatureType.ABSTRACT, "AES", "");
		cfrFeature.implementMissingFeatures(featureList);

		for (ClaferFeature currentFeature : featureList) {
			assertTrue(!currentFeature.getFeatureName().isEmpty());
		}
	}

	@Test
	public final void testSolveClaferFeature() throws IOException {
		String temporaryCfrFile = testFileFolder + "testFile2_tmp.cfr";

		/**
		 * Create Clafer feature
		 * abstract Algorithm
		 *   securityLevel -> Security
		 */
		ClaferFeature algoFeature = new ClaferFeature(Constants.FeatureType.ABSTRACT, "Algorithm", "");
		ArrayList<FeatureProperty> propertyList = new ArrayList<>();
		propertyList.add(new FeatureProperty("securityLevel", "Security"));
		algoFeature.setFeatureProperties(propertyList);

		// add feature to an empty list
		ArrayList<ClaferFeature> featureList = new ArrayList<>();
		featureList.add(algoFeature);

		// automatically create missing features (a concrete Clafer Security is supposed to be created)
		algoFeature.implementMissingFeatures(featureList);

		// serialize Clafer model to file
		StringBuilder sb = new StringBuilder();
		for (ClaferFeature cfrFeature : featureList) {
			sb.append(cfrFeature.toString());
		}
		FileWriter fileWriter = new FileWriter(temporaryCfrFile);
		fileWriter.write(sb.toString());
		fileWriter.close();

		// try compilation
		try {
			ProcessBuilder processBuilder = new ProcessBuilder("clafer", "-k", "-m", "choco", temporaryCfrFile);
			processBuilder.redirectErrorStream(true);
			Process compilerProcess = processBuilder.start();

			compilerProcess.waitFor();

			if (compilerProcess.exitValue() != 0) {
				fail("Clafer compilation error: make sure your model is correct. Aborting...");
			}

			// make sure the compilation exits with value 0
			assertEquals(0, compilerProcess.exitValue());

		} catch (Exception e) {
			fail("Abnormal Clafer compiler termination. Aborting...");
		}
	}

	@AfterClass
	public final static void deleteFiles() throws IOException {
		// gather all files to be deleted
		ArrayList<String> temporaryFiles = new ArrayList<>();
		temporaryFiles.add(testFileFolder + "testFile1_tmp.cfr");
		temporaryFiles.add(testFileFolder + "testFile2_tmp.cfr");
		temporaryFiles.add(testFileFolder + "testFile2_tmp.js");
		
		// generate the paths and delete the files
		for (String filename : temporaryFiles) {
			Path path = Paths.get(filename);
			Files.delete(path);
		}
		
	}

}
