package de.cognicrypt.crysl.handler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import de.cognicrypt.core.Constants;
import de.cognicrypt.crysl.reader.CrySLModelReader;
import de.cognicrypt.crysl.reader.CrySLReaderUtils;

public class CrySLBuilder extends IncrementalProjectBuilder {

	public static final String BUILDER_ID = "de.cognicrypt.crysl.handler.cryslbuilder";
	
	@Override
	protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException {
		try {
			CrySLModelReader csmr = new CrySLModelReader(getProject());
			final IProject curProject = getProject();
			List<IPath> resourcesPaths = new ArrayList<IPath>();
			List<IPath> outputPaths = new ArrayList<IPath>();
			IJavaProject projectAsJavaProject = JavaCore.create(curProject);
			
			for (final IClasspathEntry entry : projectAsJavaProject.getResolvedClasspath(true)) {
				if (entry.getContentKind() == IPackageFragmentRoot.K_SOURCE) {
					resourcesPaths.add(entry.getPath());
					IPath outputLocation = entry.getOutputLocation();
					outputPaths.add(outputLocation != null ? outputLocation : projectAsJavaProject.getOutputLocation());
				}
			}
			for (int i = 0; i < resourcesPaths.size(); i++) {
				CrySLReaderUtils.storeRulesToFile(csmr.readRules(resourcesPaths.get(i).toOSString()), ResourcesPlugin.getWorkspace().getRoot().findMember(outputPaths.get(i)).getLocation().toOSString());
			}
		}
		catch (IOException e) {
			Activator.getDefault().logError(e, "Build of CrySL rules failed.");
		}

		return null;
	}

	protected void clean(IProgressMonitor monitor) throws CoreException {
		for (final IClasspathEntry entry : JavaCore.create(getProject()).getResolvedClasspath(true)) {
			if (entry.getContentKind() == IPackageFragmentRoot.K_SOURCE) {
				Arrays.asList(new File(getProject().getLocation().toOSString() + Constants.outerFileSeparator + entry.getOutputLocation().removeFirstSegments(1).toOSString()).listFiles()).parallelStream().forEach(e -> e.delete());
			}
		}
	}

}
