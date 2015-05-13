/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.stups.hhu.rodinaxiompos;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IAxiom;
import org.eventb.core.IContextRoot;
import org.eventb.core.ISCAxiom;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

public class ContradictionAttributeProcessor extends SCProcessorModule {
	public static final IModuleType<ContradictionAttributeProcessor> MODULE_TYPE = SCCore
			.getModuleType(Activator.PLUGIN_ID
					+ ".contradictionAttributeProcessor"); //$NON-NLS-1$

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		assert (element instanceof IRodinFile);
		assert (target instanceof ISCContextRoot);

		// get all variables and copy over the attributes IRodinFile
		IRodinFile contextFile = (IRodinFile) element;
		IContextRoot contextRoot = (IContextRoot) contextFile.getRoot();

		ISCContextRoot scContextRoot = (ISCContextRoot) target;

		IAxiom[] axioms = contextRoot.getAxioms();
		ISCAxiom[] scaxioms = scContextRoot.getSCAxioms();

		if (axioms.length == 0 || scaxioms.length == 0)
			return;

		for (IAxiom axiom : axioms) {
			String identifier = axiom
					.getAttributeValue(EventBAttributes.LABEL_ATTRIBUTE);

			ISCAxiom scAxiom = scContextRoot.getSCAxiom(identifier);

			// might have been filtered out by previous modules
			if (scAxiom.exists()) { // original might not contain the attribute
				if (axiom.hasAttribute(ContradictionAttribute.ATTRIBUTE)) {
					boolean attribute = axiom
							.getAttributeValue(ContradictionAttribute.ATTRIBUTE);

					scAxiom.setAttributeValue(ContradictionAttribute.ATTRIBUTE,
							attribute, monitor);
				}
			}
		}

	}

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

}
