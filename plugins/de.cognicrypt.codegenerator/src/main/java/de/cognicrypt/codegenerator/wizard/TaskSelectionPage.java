/**
 * Copyright 2015-2017 Technische Universitaet Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author Ram Kamath
 *
 */
package de.cognicrypt.codegenerator.wizard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
//import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.tasks.TaskJSONReader;
import de.cognicrypt.codegenerator.utilities.Labels;
import de.cognicrypt.codegenerator.utilities.Utils;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class TaskSelectionPage extends WizardPage {

	private Composite container;
	private ComboViewer taskComboSelection;
	private Button guidedModeCheckBox;
	private Label selectProjectLabel;
	private Label selectTaskLabel;
	private Label taskDescription;
	private Text descriptionText;
	private IProject selectedProject = null;

	public TaskSelectionPage() {
		super(Labels.SELECT_TASK);
		setTitle(Labels.TASK_LIST);
		setDescription(Labels.DESCRIPTION_TASK_SELECTION_PAGE);
		setPageComplete(false);
	}

	@Override
	public void createControl(final Composite parent) {

		this.container = new Composite(parent, SWT.NONE);
		this.container.setBounds(10, 10, 200, 300);
		
		/** To display the Help view after clicking the help icon
		 * @param help_id_1 
		 *        This id refers to HelpContexts_1.xml
		 */
		PlatformUI.getWorkbench().getHelpSystem().setHelp(container, "de.cognicrypt.codegenerator.help_id_1");
		container.setLayout(new GridLayout(2, false));
		
		this.selectProjectLabel = new Label(this.container, SWT.NONE);
		GridData gd_selectProjectLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_selectProjectLabel.heightHint = 28;
		gd_selectProjectLabel.widthHint = 143;
		selectProjectLabel.setLayoutData(gd_selectProjectLabel);
		this.selectProjectLabel.setText(Constants.SELECT_JAVA_PROJECT);

		ComboViewer projectComboSelection = new ComboViewer(this.container, SWT.DROP_DOWN | SWT.READ_ONLY);
		Combo projectCombo = projectComboSelection.getCombo();
		GridData gd_combo = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_combo.widthHint = 197;
		projectCombo.setLayoutData(gd_combo);
		projectCombo.setToolTipText(Constants.PROJECTLIST_TOOLTIP);
		projectCombo.setEnabled(true);
		projectComboSelection.setContentProvider(ArrayContentProvider.getInstance());

		Map<String, IProject> javaProjects = new HashMap<String, IProject>();
		for (IProject project : Utils.createListOfJavaProjectsInCurrentWorkspace()) {
			javaProjects.put(project.getName(), project);
		}

		if (javaProjects.isEmpty()) {
			String[] errorMessage = { Constants.ERROR_MESSAGE_NO_PROJECT };
			projectComboSelection.setInput(errorMessage);
			projectComboSelection.setSelection(new StructuredSelection(projectComboSelection.getElementAt(0)));
		} else {
			projectComboSelection.setInput(javaProjects.keySet().toArray());
			projectComboSelection.addSelectionChangedListener(event -> {
				final IStructuredSelection selected = (IStructuredSelection) event.getSelection();
				this.selectedProject = javaProjects.get((String) selected.getFirstElement());
				projectComboSelection.refresh();

			});

			IProject currentProject = Utils.getCurrentProject();
			if (currentProject == null) {
				projectComboSelection.setSelection(new StructuredSelection(projectComboSelection.getElementAt(0)));
			} else {
				projectComboSelection.setSelection(new StructuredSelection(currentProject.getName()));
			}
		}

		this.selectTaskLabel = new Label(this.container, SWT.NONE);
		GridData gd_selectTaskLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_selectTaskLabel.heightHint = 28;
		gd_selectTaskLabel.widthHint = 139;
		selectTaskLabel.setLayoutData(gd_selectTaskLabel);
		this.selectTaskLabel.setText(Constants.SELECT_TASK);

		this.taskComboSelection = new ComboViewer(this.container, SWT.DROP_DOWN | SWT.READ_ONLY);
		Combo taskCombo = taskComboSelection.getCombo();
		GridData gd_taskCombo = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_taskCombo.widthHint = 223;
		taskCombo.setLayoutData(gd_taskCombo);
		taskCombo.setToolTipText(Constants.TASKLIST_TOOLTIP);
		taskCombo.setEnabled(true);
		this.taskComboSelection.setContentProvider(ArrayContentProvider.getInstance());

		final List<Task> tasks = TaskJSONReader.getTasks();

		this.taskComboSelection.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(final Object task) {
				if (task instanceof Task) {
					final Task current = (Task) task;
					return current.getDescription();
					
				}
				return super.getText(task);			
			}
		});

		this.taskComboSelection.setInput(tasks);
		//Label for task description
		this.taskDescription = new Label(this.container, SWT.NONE);
		GridData gd_taskDescription = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		gd_taskDescription.widthHint = 139;
		taskDescription.setLayoutData(gd_taskDescription);
		this.taskDescription.setText(Constants.TASK_DESCRIPTION);
		
		// Adding description text for the cryptographic task that has been selected from the combo box
		this.descriptionText = new Text(container, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		GridData gd_descriptionText = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_descriptionText.widthHint = 297;
		gd_descriptionText.heightHint = 96;
		descriptionText.setLayoutData(gd_descriptionText);
		this.descriptionText.setToolTipText(Constants.DESCRIPTION_BOX_TOOLTIP);
		this.descriptionText.setEditable(false);
		
		this.taskComboSelection.addSelectionChangedListener(event -> {
			final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			final Task selectedTask = (Task) selection.getFirstElement();
			TaskSelectionPage.this.taskComboSelection.refresh();
			setPageComplete(selectedTask != null && this.selectedProject != null);
			// To display the description text
			this.descriptionText.setText(selectedTask.getTaskDescription())	;
		});

		this.taskComboSelection.setSelection(new StructuredSelection(tasks.get(0)));
		setControl(this.container);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
			
		//Check box for going to guided mode
		this.guidedModeCheckBox = new Button(container, SWT.CHECK);
		guidedModeCheckBox.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		guidedModeCheckBox.setToolTipText(Constants.GUIDEDMODE_TOOLTIP);
		this.guidedModeCheckBox.setEnabled(true);
		this.guidedModeCheckBox.addSelectionListener(new SelectionAdapter() {
		    @Override
			public void widgetSelected(SelectionEvent e) {
				
		    }
		});
		this.guidedModeCheckBox.setText(Constants.GUIDED_MODE);
		this.guidedModeCheckBox.setSelection(true);
		final ControlDecoration deco = new ControlDecoration(guidedModeCheckBox, SWT.TOP | SWT.LEFT );
        Image image = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION)
		.getImage();
		
		deco.setDescriptionText("If you do not use the guided mode, then you have to \nconfigure the algorithm by yourself");
		deco.setImage(image);
		deco.setShowOnlyOnFocus(false);
		
	}

	public IProject getSelectedProject() {
		return this.selectedProject;
	}

	public Task getSelectedTask() {
		return (Task) ((IStructuredSelection) this.taskComboSelection.getSelection()).getFirstElement();
	}

	/**
	 * Helper method to UI , this flag decides the second page of the wizard
	 *
	 * @return
	 */
	public boolean isGuidedMode() {
			return this.guidedModeCheckBox.getSelection();
	}

	@Override
	public void setVisible( boolean visible ) {
	  super.setVisible( visible );
	  if( visible ) {
	    container.setFocus();
	  }
	}
}
