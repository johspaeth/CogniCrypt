package de.cognicrypt.codegenerator.taskintegrator.widgets;

import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;


public class CompositeToHoldGranularUIElements extends ScrolledComposite {
	private String targetPageName;
	private int lowestWidgetYAxisValue = Constants.PADDING_BETWEEN_GRANULAR_UI_ELEMENTS;
	private ClaferModel claferModel;
	private CompositeClaferFeedback compositeClaferFeedback;
	
	private ArrayList<Question> listOfAllQuestions;
	int counter;
	
	/**
	 * Create the composite.  
	 * @param parent
	 */
	public CompositeToHoldGranularUIElements(Composite parent, String pageName) {
		super(parent, SWT.BORDER | SWT.V_SCROLL);
		
		claferModel = new ClaferModel();
		
		listOfAllQuestions = new ArrayList<Question>();
		
		
		setTargetPageName(pageName);
		
		setExpandHorizontal(true);		
		setExpandVertical(true);
		setLayout(new GridLayout(2, false));
		
		// All the granular UI elements will be added to this composite for the ScrolledComposite to work.
		Composite contentComposite = new Composite(this, SWT.NONE);
		contentComposite.setLayout(new GridLayout(1, false));
		setContent(contentComposite);
		setMinSize(contentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
	public void addGranularClaferUIElements(ClaferFeature claferFeature){
		new CompositeClaferFeature((Composite) this.getContent(), claferFeature);
		setMinSize(((Composite) getContent()).computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	public void deleteClaferFeature(ClaferFeature featureToBeDeleted) {
		claferModel.remove(featureToBeDeleted);
		updateClaferContainer();
	}

	public void updateClaferContainer() {
		Composite compositeContentOfThisScrolledComposite = (Composite)this.getContent();
		
		// first dispose all the granular UI elements (which includes the deleted one).
		for(Control uiRepresentationOfClaferFeatures : compositeContentOfThisScrolledComposite.getChildren()){
			uiRepresentationOfClaferFeatures.dispose();
		}
		
		// update the size values.
		setLowestWidgetYAxisValue(0);
		setMinHeight(getLowestWidgetYAxisValue());
		
		// add all the clafer features excluding the deleted one.
		for(ClaferFeature featureUnderConsideration : claferModel){
			addGranularClaferUIElements(featureUnderConsideration);
		}

		((Composite) this.getContent()).layout();
	}
	
	public void setCompositeClaferFeedback(CompositeClaferFeedback compositeClaferFeedback) {
		this.compositeClaferFeedback = compositeClaferFeedback;
	}

	/**
	 * updates a given feature with a new version
	 *
	 * @param originalClaferFeature
	 *        the feature to be updated
	 * @param modifiedClaferFeature
	 *        the updated version of the feature
	 */
	public void modifyClaferFeature(ClaferFeature originalClaferFeature, ClaferFeature modifiedClaferFeature ){
		for (ClaferFeature featureUnderConsideration : claferModel) {
			if(featureUnderConsideration.equals(originalClaferFeature)){
				featureUnderConsideration = modifiedClaferFeature;
				break;
			}
		}
		
		updateClaferContainer();

		compositeClaferFeedback.setFeedback("modified Clafer feature");
	}
	
	public void addQuestionUIElements(Question question, ClaferModel claferModel, boolean linkAnswerPage) {

		// Update the array list.
		//listOfAllClaferFeatures.add(claferFeature);
		setClaferModel(claferModel);
		CompositeGranularUIForHighLevelQuestions granularQuestion = new CompositeGranularUIForHighLevelQuestions((Composite) this.getContent(), // the content composite of ScrolledComposite.
			SWT.NONE, question, linkAnswerPage);

		//heightOfTheGranularComposite variable is used to determine the height of the granular Composite
		int heightOfTheGranularComposite = 0;
		if (linkAnswerPage) {
			heightOfTheGranularComposite = granularQuestion.getSize().y - 55;
		} else if (!linkAnswerPage) {
			heightOfTheGranularComposite = granularQuestion.getSize().y;
		}
		granularQuestion.setBounds(Constants.PADDING_BETWEEN_GRANULAR_UI_ELEMENTS, getLowestWidgetYAxisValue(), Constants.WIDTH_FOR_GRANULAR_CLAFER_UI_ELEMENT,
			//Constants.HEIGHT_FOR_GRANULAR_CLAFER_UI_ELEMENT
			heightOfTheGranularComposite);

		//granularQuestion.setSize(SWT.DEFAULT, granularQuestion.getSize().y);
		setLowestWidgetYAxisValue(getLowestWidgetYAxisValue() + heightOfTheGranularComposite);
		setMinHeight(getLowestWidgetYAxisValue());

	}

	/**
	 * 
	 * @param question
	 *        the question that is to be move up in the list
	 */
	public void moveUpTheQuestion(Question question) {
		int questionToBeMoveUp = 0;
		int questionToBeMoveDown = 0;
		for(Question qstn:listOfAllQuestions){
		if(qstn.getId()==question.getId()){
				questionToBeMoveUp = qstn.getId();
		}else if(qstn.getId()==question.getId()-1){
				questionToBeMoveDown = qstn.getId();
		}
		}
		Collections.swap(listOfAllQuestions, questionToBeMoveUp, questionToBeMoveDown);
		updateQuestionsID();
		updateQuestionContainer();
	}
	
	/**
	 * 
	 * @param question
	 *        the question that is to be move down in the list
	 */
	public void moveDownTheQuestion(Question question) {
		int questionToBeMoveUp = 0;
		int questionToBeMoveDown = 0;
		for (Question qstn : listOfAllQuestions) {
			if (qstn.getId() == question.getId()) {
				questionToBeMoveUp = qstn.getId();
			} else if (qstn.getId() == question.getId() + 1) {
				questionToBeMoveDown = qstn.getId();
			}
		}
		Collections.swap(listOfAllQuestions, questionToBeMoveUp, questionToBeMoveDown);
		updateQuestionsID();
		updateQuestionContainer();
	}

	/**
	 * 
	 * @param questionToBeDeleted
	 *        the question to be deleted
	 */
	public void deleteQuestion(Question questionToBeDeleted){		
		
		listOfAllQuestions.remove(questionToBeDeleted);
		updateQuestionsID();		
		updateQuestionContainer();
		
	}
	
	/**
	 * updates the question Id everytime
	 * when a question is deleted
	 */
	
	private void updateQuestionsID() {
		int qID=0;
		for(Question qstn: listOfAllQuestions){
			qstn.setId(qID++);
		}
		
	}

	/**	 
	 * executes when next button of "highLevelQuestion" is pressed
	 * deletes listOfAllQuestions of "pageForLinkAnswers"
	 * to refresh the question list of "pagForLinkAnswers"
	 */
	public void deleteAllQuestion(){
		listOfAllQuestions.clear();
		updateQuestionContainer();
	}
	
	public void updateQuestionContainer() {
		Composite compositeContentOfThisScrolledComposite = (Composite)this.getContent();
		
		// first dispose all the granular UI elements (which includes the deleted one).
		for(Control uiRepresentationOfQuestions : compositeContentOfThisScrolledComposite.getChildren()){
			uiRepresentationOfQuestions.dispose();
		}
		
		// update the size values.
		setLowestWidgetYAxisValue(0);
		setMinHeight(getLowestWidgetYAxisValue());
		
		// add all the clafer features excluding the deleted one.
		for(Question questionUnderConsideration : listOfAllQuestions){
			addQuestionUIElements(questionUnderConsideration, claferModel, false);
		}
	}
	
	public void modifyQuestion(Question originalQuestion, Question modifiedQuestion ){
		for(Question questionUnderConsideration:listOfAllQuestions){
			if(questionUnderConsideration.equals(originalQuestion)){
				questionUnderConsideration = modifiedQuestion;
				break;
			}
		}
		updateClaferContainer();
	}
	
	public void modifyHighLevelQuestion(Question originalQuestion, Question modifiedQuestion ){
		for(Question questionUnderConsideration:listOfAllQuestions){
			if(questionUnderConsideration.equals(originalQuestion)){
				questionUnderConsideration.setQuestionText(modifiedQuestion.getQuestionText());
				questionUnderConsideration.setElement(modifiedQuestion.getElement());
				questionUnderConsideration.getAnswers().clear();
				questionUnderConsideration.setAnswers(modifiedQuestion.getAnswers());
				break;
			}
		}
		//deleteQuestion(originalQuestion);
		updateQuestionContainer();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * @return the targetPageName
	 */
	public String getTargetPageName() {
		return targetPageName;
	}

	/**
	 * @param targetPageName the targetPageName to set
	 */
	private void setTargetPageName(String targetPageName) {
		this.targetPageName = targetPageName;
	}

	/**
	 * @return the lowestWidgetYAxisValue
	 */
	public int getLowestWidgetYAxisValue() {
		return lowestWidgetYAxisValue;
	}

	/**
	 * @param lowestWidgetYAxisValue the lowestWidgetYAxisValue to set
	 */
	public void setLowestWidgetYAxisValue(int lowestWidgetYAxisValue) {
		this.lowestWidgetYAxisValue = lowestWidgetYAxisValue + Constants.PADDING_BETWEEN_GRANULAR_UI_ELEMENTS;
	}
	
	public ClaferModel getClaferModel() {
		return claferModel;
	}

	public void setClaferModel(ClaferModel claferModel) {
		this.claferModel = claferModel;
	}

	/**
	 * @return the listOfAllQuestions
	 */
	public ArrayList<Question> getListOfAllQuestions() {
		return listOfAllQuestions;
	}

	/**
	 * @param listOfAllQuestions the listOfAllQuestions to set
	 */
	public void setListOfAllQuestions(ArrayList<Question> listOfAllQuestions) {
		this.listOfAllQuestions = listOfAllQuestions;
	}

}