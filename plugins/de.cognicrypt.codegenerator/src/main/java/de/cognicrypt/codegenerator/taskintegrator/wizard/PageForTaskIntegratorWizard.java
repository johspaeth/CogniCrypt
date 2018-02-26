/**
 * 
 */
package de.cognicrypt.codegenerator.taskintegrator.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.clafer.instance.InstanceClafer;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.featuremodel.clafer.InstanceGenerator;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.ClaferDependency;
import de.cognicrypt.codegenerator.question.CodeDependency;
import de.cognicrypt.codegenerator.question.Page;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.question.QuestionsJSONReader;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.ModelAdvancedMode;
import de.cognicrypt.codegenerator.taskintegrator.widgets.CompositeBrowseForFile;
import de.cognicrypt.codegenerator.taskintegrator.widgets.CompositeChoiceForModeOfWizard;
import de.cognicrypt.codegenerator.taskintegrator.widgets.CompositeForXsl;
import de.cognicrypt.codegenerator.taskintegrator.widgets.CompositeToHoldGranularUIElements;
import de.cognicrypt.codegenerator.utilities.XMLParser;

/**
 * @author rajiv
 *
 */
public class PageForTaskIntegratorWizard extends WizardPage {

	private CompositeChoiceForModeOfWizard compositeChoiceForModeOfWizard = null;
	protected CompositeToHoldGranularUIElements compositeToHoldGranularUIElements = null;

	private CompositeForXsl compositeForXsl = null;

	int counter = 0;// TODO for testing only.
	protected ArrayList<ClaferFeature> cfrFeatures;

	private HashMap<String, String> tagValueTagData;


	/**
	 * Create the wizard.
	 */
	public PageForTaskIntegratorWizard(String name, String title, String description) {
		super(name);
		setTitle(title);
		setDescription(description);
		this.setPageComplete(false);
		// The String to display, and the constructed string for the XSL document.
		setTagValueTagData(new HashMap<>());
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		setControl(container);

		// make the page layout two-column
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayout(new GridLayout(2, false));

		switch (this.getName()) {
			case Constants.PAGE_NAME_FOR_MODE_OF_WIZARD:
				setCompositeChoiceForModeOfWizard(new CompositeChoiceForModeOfWizard(container, SWT.NONE, this));
				break;
			case Constants.PAGE_NAME_FOR_XSL_FILE_CREATION:

				setCompositeForXsl(new CompositeForXsl(container, SWT.NONE));
				// fill the available space on the with the big composite
				getCompositeForXsl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));

				Button btnAddXSLTag = new Button(container, SWT.PUSH);//Add button to add the xsl tag in the code
				btnAddXSLTag.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
				btnAddXSLTag.setText("Add Xsl Tag");
				Button btnReadCode = new Button(container, SWT.PUSH);//Add button to add the xsl tag in the code
				btnReadCode.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
				btnReadCode.setText("Get the code");

				btnReadCode.addSelectionListener(new SelectionAdapter() {
					/*
					 * (non-Javadoc)
					 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
					 */

					@Override
					public void widgetSelected(SelectionEvent e) {

						super.widgetSelected(e);

						FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);

						fileDialog.setFilterExtensions(new String[] {"*.xsl", "*.java", "*.txt" });
						fileDialog.setText("Choose the code file:");

						String fileDialogResult = fileDialog.open();
						if (fileDialogResult != null) {
							((CompositeForXsl) getCompositeForXsl()).updateTheTextFieldWithFileData(fileDialogResult);
						}
					}

				});

				btnAddXSLTag.addSelectionListener(new SelectionAdapter() {
					
					@Override
					public void widgetSelected(SelectionEvent e) {
						


						//Composite test = ((PageForTaskIntegratorWizard) getWizard().getPage(Constants.PAGE_NAME_FOR_CLAFER_FILE_CREATION)).getCompositeChoiceForModeOfWizard();

						// this is needed to get the name and the description of the task from the wizard.
						ModelAdvancedMode objectForDataInGuidedMode = ((PageForTaskIntegratorWizard) getWizard().getPage(Constants.PAGE_NAME_FOR_MODE_OF_WIZARD))
							.getCompositeChoiceForModeOfWizard().getObjectForDataInNonGuidedMode();
						String taskName = objectForDataInGuidedMode.getNameOfTheTask();
						String taskDescription = objectForDataInGuidedMode.getTaskDescription();

						//TODO not implemented yet. We need the location of the js file that is created from the provided clafer model. 
						String jsFilePath = ((PageForTaskIntegratorWizard) getWizard().getPage(Constants.PAGE_NAME_FOR_CLAFER_FILE_CREATION)).getJSFilePath();
						InstanceGenerator instanceGenerator = new InstanceGenerator(jsFilePath, "c0_" + taskName, taskDescription);
						
						// This will contain the xml strings that are generated for every -> operator encountered.
						List<Document> xmlStrings = new ArrayList<Document>();

						XMLParser xmlParser = new XMLParser();
						// this will remain empty for the first instance, that contains no -> operators.
						HashMap<Question, Answer> constraints = new HashMap<>();
						InstanceClafer initialInstance = instanceGenerator.generateInstances(constraints).get(0);
						// get the number of children on the instance generation where there are no constraints.
						//int numberOfChildren = initialInstance.getChildren().length;
						// Get the instance value for the blank constraint.
						//xmlStrings.add(xmlParser.displayInstanceValues(initialInstance, constraints));

						xmlStrings.add(xmlParser.displayInstanceValues(initialInstance, constraints));

						//						DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
						//						try {
						//							DocumentBuilder builder = factory.newDocumentBuilder();
						//							xmlStrings.add((Document) builder.parse(new InputSource(new StringReader(xmlParser.displayInstanceValues(initialInstance, constraints)))));
						//						} catch (ParserConfigurationException | SAXException | IOException e1) {
						//							// TODO Auto-generated catch block
						//							e1.printStackTrace();
						//						}

						// Questions needed to get the answer that has a constraint with the -> operator.
						QuestionsJSONReader reader = new QuestionsJSONReader();
						// TODO update this to read the data generated in the questions page.
						List<Page> pages = reader.getPages("/src/main/resources/TaskDesc/SymmetricEncryption.json");

						for (Page page : pages) {
							for (Question question : page.getContent()) {
								for (Answer answer : question.getAnswers()) {
									if (answer.getClaferDependencies() != null) {
										for (ClaferDependency claferDependency : answer.getClaferDependencies()) {
											if ("->".equals(claferDependency.getOperator())) {
												xmlStrings.add(getXMLForNewAlgorithmInsertion(question, answer, xmlParser, instanceGenerator, claferDependency));

											}
										} // clafer dependency loop
									} // clafer dependency check
									if (answer.getCodeDependencies() != null) {
										for (CodeDependency codeDependency : answer.getCodeDependencies()) {
											//xmlStrings.get(0).elementByID(Constants.Code).addElement(codeDependency.getOption()).addText(codeDependency.getValue() + "");
											Element root = xmlStrings.get(0).getRootElement();

											for (Iterator<Element> element = root.elementIterator(Constants.Code); element.hasNext();) {
												Element codeElement = element.next();
												codeElement.addElement(codeDependency.getOption()).addText(codeDependency.getValue() + "");
										    }
										} // code dependency loop
									} // code dependency check
								} // answer loop
							} // question loop
						} // page loop


						for (Document xmlDocument : xmlStrings) {

							processXMLDocument(xmlDocument);

							System.out.println(xmlDocument.asXML());
						}

						SortedSet<String> keys = new TreeSet<String>(getTagValueTagData().keySet());
						//						for (String key : keys) {
						//							System.out.println(key + " " + getTagValueTagData().get(key));
						//						}

						XSLTagDialog dialog;
						if (keys.size() > 0) {
							dialog = new XSLTagDialog(getShell(), keys);
						} else {
							dialog = new XSLTagDialog(getShell());
						}

						if (dialog.open() == Window.OK) {
							// To locate the position of the xsl tag to be introduce						
							Point selected = getCompositeForXsl().getXslTxtBox().getSelection();
							String xslTxtBoxContent = getCompositeForXsl().getXslTxtBox().getText();
							xslTxtBoxContent = xslTxtBoxContent.substring(0, selected.x) + dialog.getTag().toString() + xslTxtBoxContent.substring(selected.y,
								xslTxtBoxContent.length());
							getCompositeForXsl().getXslTxtBox().setText(xslTxtBoxContent);
						}

						/*for (IWizardPage page : getWizard().getPages()) {
							// get the Clafer creation page
							if (page instanceof PageForTaskIntegratorWizard) {
								PageForTaskIntegratorWizard pftiw = (PageForTaskIntegratorWizard) page;
								if (pftiw.getCompositeToHoldGranularUIElements() instanceof CompositeToHoldGranularUIElements) {
									CompositeToHoldGranularUIElements comp = (CompositeToHoldGranularUIElements) pftiw.getCompositeToHoldGranularUIElements();
									if (pftiw.getName() == Constants.PAGE_NAME_FOR_CLAFER_FILE_CREATION) {
										// get the Clafer features
										claferModel = comp.getClaferModel();

										// get all the Clafer features' properties
										for (ClaferFeature cfrFtr : claferModel) {
											String ftrName = cfrFtr.getFeatureName();
											for (FeatureProperty prop : cfrFtr.getFeatureProperties()) {
												// prepend the feature name and add the property to dropdown entries
												strFeatures.add(ftrName + "." + prop.getPropertyName());
											}
										}
									} else if (pftiw.getName() == Constants.PAGE_NAME_FOR_HIGH_LEVEL_QUESTIONS) {
										questions = comp.getListOfAllQuestions();

										for (Question question : questions) {
											// TODO compare against Constants.GUIElements.text
											if (question.getQuestionType().equals("text")) {
												strFeatures.add("[Answer to \"" + question.getQuestionText() + "\"]");
											}
											
											
										}
									}
								}
							}
						}*/

						

					}

					/**
					 * 
					 * @param xmlDocument
					 */
					private void processXMLDocument(Document xmlDocument) {
						Element root = xmlDocument.getRootElement();
						// send a slash as a parameter to keep the recursive method as generic as possible.
						processElement(root, "", Constants.SLASH, true);
					}

					/**
					 * 
					 * @param xmlElement
					 * @param existingNameToBeDisplayed
					 * @param existingDataForXSLDocument
					 * @param isRoot
					 */
					private void processElement(Element xmlElement, String existingNameToBeDisplayed, String existingDataForXSLDocument, boolean isRoot) {
						StringBuilder tagNameToBeDisplayed = new StringBuilder();
						StringBuilder tagDataForXSLDocument = new StringBuilder();

						tagNameToBeDisplayed.append(existingNameToBeDisplayed);
						tagDataForXSLDocument.append(existingDataForXSLDocument);

						if (!isRoot) {
							tagNameToBeDisplayed.append(Constants.DOT);
						}
						tagNameToBeDisplayed.append(xmlElement.getName());
						tagDataForXSLDocument.append(Constants.SLASH);
						tagDataForXSLDocument.append(xmlElement.getName());



						int builderDisplayDataSizeTillRoot = tagNameToBeDisplayed.length();
						int builderTagDataSizeTillRoot = tagDataForXSLDocument.length();


						if (xmlElement.attributeCount() == 0 && !xmlElement.elementIterator().hasNext()) {
							// adding the tag, if there are no attributes.
							getTagValueTagData().put(tagNameToBeDisplayed.toString(), tagDataForXSLDocument.toString());
						} else {
							for (Iterator<Attribute> attribute = xmlElement.attributeIterator(); attribute.hasNext();) {
								Attribute attributeData = attribute.next();
								// TODO the name of the task can be fixed here based on what is chosen before.	

								if (tagNameToBeDisplayed.length() > builderDisplayDataSizeTillRoot) {
									tagNameToBeDisplayed.delete(builderDisplayDataSizeTillRoot, tagNameToBeDisplayed.length());
								}

								if (tagDataForXSLDocument.length() > builderTagDataSizeTillRoot) {
									tagDataForXSLDocument.delete(builderTagDataSizeTillRoot, tagDataForXSLDocument.length());
								}

								tagNameToBeDisplayed.append(Constants.DOT);
								tagNameToBeDisplayed.append("@" + attributeData.getName());

								tagDataForXSLDocument.append(Constants.ATTRIBUTE_BEGIN);
								tagDataForXSLDocument.append(attributeData.getName());
								tagDataForXSLDocument.append(Constants.ATTRIBUTE_END);

								getTagValueTagData().put(tagNameToBeDisplayed.toString(), tagDataForXSLDocument.toString());
								
								// Adding the loop for the remaining elements within the attribute loop to have unique tags based on the attributes. 
								for (Iterator<Element> element = xmlElement.elementIterator(); element.hasNext();) {
									Element currentElement = element.next();
									// do not consider the imports tag. The data is not relevant.
									if (!currentElement.getName().equals("Imports")) {
										if (tagNameToBeDisplayed.length() > builderDisplayDataSizeTillRoot) {
											tagNameToBeDisplayed.delete(builderDisplayDataSizeTillRoot, tagNameToBeDisplayed.length());
										}

										if (isRoot) {
											if (tagDataForXSLDocument.length() > builderTagDataSizeTillRoot) {
												tagDataForXSLDocument.delete(builderTagDataSizeTillRoot, tagDataForXSLDocument.length());
											}
										}
										// recursive call
										processElement(currentElement, tagNameToBeDisplayed.toString(), tagDataForXSLDocument.toString(), false);
									}
								}
							}
						}

						// A similar loop outside the attribute loop to check the tags that are not nested.
						for (Iterator<Element> element = xmlElement.elementIterator(); element.hasNext();) {
							Element currentElement = element.next();
							// do not consider the imports tag. The data is not relevant.
							if (!currentElement.getName().equals("Imports")) {
								if (tagNameToBeDisplayed.length() > builderDisplayDataSizeTillRoot) {
									tagNameToBeDisplayed.delete(builderDisplayDataSizeTillRoot, tagNameToBeDisplayed.length());
								}

								if (isRoot) {
									if (tagDataForXSLDocument.length() > builderTagDataSizeTillRoot) {
										tagDataForXSLDocument.delete(builderTagDataSizeTillRoot, tagDataForXSLDocument.length());
									}
								}
								// recursive call
								processElement(currentElement, tagNameToBeDisplayed.toString(), tagDataForXSLDocument.toString(), false);
							}
						}
					}

					/**
					 * This method is created to be able to exit the nested loops as soon as the correct instance is found.
					 * 
					 * @param question
					 *        The question object from the outer loop.
					 * @param answer
					 *        The answer object from the outer loop.
					 * @param xmlParser
					 *        This object is needed to generate the xml string.
					 * @param instanceGenerator
					 *        This object is needed to generate the instances
					 * @param claferDependency
					 *        The claferDependency from the outer loop
					 * @return
					 */
					private Document getXMLForNewAlgorithmInsertion(Question question, Answer answer, XMLParser xmlParser, InstanceGenerator instanceGenerator, ClaferDependency claferDependency) {
						HashMap<Question, Answer> constraints = new HashMap<>();
						constraints.put(question, answer);
						String constraintOnType = claferDependency.getAlgorithm();
						for (InstanceClafer instance : instanceGenerator.generateInstances(constraints)) {
							for (InstanceClafer childInstance : instance.getChildren()) {
								// check if the name of the constraint on the clafer instance is the same as the one on the clafer dependency from the outer loop.
								if (childInstance.getType().getName().equals(constraintOnType)) {
									return xmlParser.displayInstanceValues(instance, constraints);
								}
							} // child instance loop
						} // instance loop
						return null;
					}
				});
				break;
			case Constants.PAGE_NAME_FOR_HIGH_LEVEL_QUESTIONS:
				setCompositeToHoldGranularUIElements(new CompositeToHoldGranularUIElements(container, this.getName()));
				// fill the available space on the with the big composite
				getCompositeToHoldGranularUIElements().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

				TaskIntegrationWizard tiWizard = null;

				if (TaskIntegrationWizard.class.isInstance(getWizard())) {
					tiWizard = (TaskIntegrationWizard) getWizard();
				} else {
					Activator.getDefault().logError("PageForTaskIntegratorWizard was instantiated by a wizard other than TaskIntegrationWizard");
				}

				PageForTaskIntegratorWizard claferPage = tiWizard.getTIPageByName(Constants.PAGE_NAME_FOR_CLAFER_FILE_CREATION);
				CompositeToHoldGranularUIElements claferPageComposite = (CompositeToHoldGranularUIElements) claferPage.getCompositeToHoldGranularUIElements();

				QuestionDialog questionDialog = new QuestionDialog(parent.getShell());
				Button qstnDialog = new Button(container, SWT.NONE);
				qstnDialog.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
				qstnDialog.setText("Add Question");

				qstnDialog.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						int response = questionDialog.open();
						if (response == Window.OK) {
							counter++;
							//Question questionDetails = getDummyQuestion(questionDialog.getQuestionText(),questionDialog.getquestionType(),questionDialog.getAnswerValue());
							Question questionDetails = questionDialog.getQuestionDetails();
							questionDetails.setId(counter);

							// Update the array list.
							compositeToHoldGranularUIElements.getListOfAllQuestions().add(questionDetails);
							compositeToHoldGranularUIElements.addQuestionUIElements(questionDetails, claferPageComposite.getClaferModel(), false);
						}
					}
				});
				break;
			case Constants.PAGE_NAME_FOR_LINK_ANSWERS:
				setCompositeToHoldGranularUIElements(new CompositeToHoldGranularUIElements(container, this.getName()));
				// fill the available space on the with the big composite
				getCompositeToHoldGranularUIElements().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
				break;
		}
	}

	public String getJSFilePath() {
		return "/home/rajiv/git/CogniCrypt/plugins/de.cognicrypt.codegenerator/src/main/resources/ClaferModel/SymmetricEncryption.js";
	}

	/**
	 * Overwriting the getNextPage method to extract the list of all questions
	 * from highLevelQuestion page and forward the data to pageForLinkAnswers at runtime
	 */
	public IWizardPage getNextPage() {
		boolean isNextPressed = "nextPressed".equalsIgnoreCase(Thread.currentThread().getStackTrace()[2].getMethodName());
		if (isNextPressed) {
			boolean validatedNextPress = this.nextPressed(this);
			if (!validatedNextPress) {
				return this;
			}
		}
		
		if (this.getName().equals(Constants.PAGE_NAME_FOR_MODE_OF_WIZARD) && !getCompositeChoiceForModeOfWizard().getObjectForDataInNonGuidedMode().isGuidedModeChosen()) {
			return null;
		}
		
		if (this.getName().equals(Constants.PAGE_NAME_FOR_CLAFER_FILE_CREATION)) {

		}
		/*
		 * This is for debugging only. To be removed for the final version.
		 * TODO Please add checks on the pages after mode selection to mark those pages as completed, or restrict the finish button.
		 */
		IWizardPage nextPage = super.getNextPage();
		if (nextPage != null) {
			((WizardPage)nextPage).setPageComplete(true);
		}
				
		return nextPage;

	}

	/**
	 * Extract data from highLevelQuestions page and forward it to pageForLinkAnswers at runtime
	 * 
	 * @param page
	 *        highLevelQuestions page is received
	 * @return true always
	 */
	protected boolean nextPressed(IWizardPage page) {
		boolean ValidateNextPress = true;
		try {
			if (page.getName().equals(Constants.PAGE_NAME_FOR_HIGH_LEVEL_QUESTIONS)) {
				PageForTaskIntegratorWizard highLevelQuestionPage = (PageForTaskIntegratorWizard) page;
				CompositeToHoldGranularUIElements highLevelQuestionPageComposite = (CompositeToHoldGranularUIElements) highLevelQuestionPage.getCompositeToHoldGranularUIElements();
				IWizardPage nextPage = super.getNextPage();
				ArrayList<Question> listOfAllQuestions = highLevelQuestionPageComposite.getListOfAllQuestions();
				if (nextPage instanceof PageForTaskIntegratorWizard) {
					PageForTaskIntegratorWizard pftiw = (PageForTaskIntegratorWizard) nextPage;
					if (pftiw.getCompositeToHoldGranularUIElements() instanceof CompositeToHoldGranularUIElements) {
						CompositeToHoldGranularUIElements comp = (CompositeToHoldGranularUIElements) pftiw.getCompositeToHoldGranularUIElements();
						if (comp.getListOfAllQuestions().size() > 0) {
							comp.deleteAllQuestion();
						}
						for (Question question : listOfAllQuestions) {
							comp.getListOfAllQuestions().add(question);
							comp.addQuestionUIElements(question, null, true);

						}

					}
				}
			}

		} catch (Exception ex) {
			System.out.println("Error validation when pressing Next: " + ex);

		}
		return ValidateNextPress;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#canFlipToNextPage()
	 */
	@Override
	public boolean canFlipToNextPage() {

		// each case needs to be handled separately. By default all cases will return false. 
		/*
		 * switch(this.getName()){ case Constants.PAGE_NAME_FOR_MODE_OF_WIZARD: if(((boolean)compositeChoiceForModeOfWizard.getData(Constants.WIDGET_DATA_IS_GUIDED_MODE_CHOSEN) ==
		 * true || (boolean)compositeChoiceForModeOfWizard.getData(Constants.WIDGET_DATA_IS_GUIDED_MODE_FORCED) == true) && !this.isPageComplete()){ return true; } case
		 * Constants.PAGE_NAME_FOR_CLAFER_FILE_CREATION: return false; case Constants.PAGE_NAME_FOR_XSL_FILE_CREATION: return false; case
		 * Constants.PAGE_NAME_FOR_HIGH_LEVEL_QUESTIONS: return false; default: return false; }
		 */
		return super.canFlipToNextPage();

	}
	/**
	 * This method will check whether all the validations on the page were successful. The page is set to incomplete if any of the validations have an ERROR.
	 */
	public void checkIfModeSelectionPageIsComplete() {		
		boolean errorOnFileWidgets = false;
		// The first child of the composite is a group. Get the children of this group to iterated over.
		for (Control control : ((Group)getCompositeChoiceForModeOfWizard().getChildren()[0]).getChildren()) {
			// Check if the child is an instance of group and is visible.
			if (control instanceof Group && control.isVisible()) {
				
				// Get the children of this group and iterate over them. These are the widgets that get the file data. This loop generalizes for all these widgets.
				for (Control subGroup : ((Group)control).getChildren()) {					
					if (subGroup instanceof CompositeBrowseForFile) {
						CompositeBrowseForFile tempVaraiable = (CompositeBrowseForFile) subGroup;
						if ((tempVaraiable).getDecFilePath().getDescriptionText().contains(Constants.ERROR)) {
							errorOnFileWidgets = true;
						}
					}
					
				}	
				
			}
		}		
		
		// Check if validation failed on the task name.
		boolean errorOnTaskName = getCompositeChoiceForModeOfWizard().getDecNameOfTheTask().getDescriptionText().contains(Constants.ERROR);
		
		// Set the page to incomplete if the validation failed on any of the text boxes.
		if (errorOnTaskName || errorOnFileWidgets) {
			setPageComplete(false);
			
		} else {
			setPageComplete(true);
		}
	}

	/**
	 * Return the composite for the first page, i.e. to choose the mode of the wizard.
	 * @return the compositeChoiceForModeOfWizard
	 */
	public CompositeChoiceForModeOfWizard getCompositeChoiceForModeOfWizard() {
		return compositeChoiceForModeOfWizard;
	}

	/**
	 * The composite is maintained as a global variable to have access to it as part of the page object.
	 * @param compositeChoiceForModeOfWizard
	 *        the compositeChoiceForModeOfWizard to set
	 */
	private void setCompositeChoiceForModeOfWizard(CompositeChoiceForModeOfWizard compositeChoiceForModeOfWizard) {
		this.compositeChoiceForModeOfWizard = compositeChoiceForModeOfWizard;
	}

	/**
	 * @return the compositeToHoldGranularUIElements
	 */
	public Composite getCompositeToHoldGranularUIElements() {
		return compositeToHoldGranularUIElements;
	}

	/**
	 * The composite is maintained as a global variable to have access to it as part of the page object.
	 * @param compositeToHoldGranularUIElements
	 *        the compositeToHoldGranularUIElements to set
	 */
	public void setCompositeToHoldGranularUIElements(CompositeToHoldGranularUIElements compositeToHoldGranularUIElements) {
		this.compositeToHoldGranularUIElements = compositeToHoldGranularUIElements;
	}

	public int getCounter() {
		return counter;
	}

	/**
	 * Return the composite for the XSL page.
	 * @return the compositeForXsl
	 */
	public CompositeForXsl getCompositeForXsl() {
		return compositeForXsl;
	}

	/**
	 * The composite is maintained as a global variable to have access to it as part of the page object.
	 * @param compositeForXsl
	 *        the compositeForXsl to set
	 */
	public void setCompositeForXsl(CompositeForXsl compositeForXsl) {
		this.compositeForXsl = compositeForXsl;

	}

	/**
	 * @return the tagValueTagData
	 */
	public HashMap<String, String> getTagValueTagData() {
		return tagValueTagData;
	}

	/**
	 * @param tagValueTagData
	 *        the tagValueTagData to set
	 */
	public void setTagValueTagData(HashMap<String, String> tagValueTagData) {
		this.tagValueTagData = tagValueTagData;
	}

}
