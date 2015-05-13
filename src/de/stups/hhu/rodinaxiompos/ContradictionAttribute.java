/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.stups.hhu.rodinaxiompos;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IAxiom;
import org.eventb.internal.ui.eventbeditor.manipulation.AbstractBooleanManipulation;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

@SuppressWarnings("restriction")
public class ContradictionAttribute extends AbstractBooleanManipulation {
	private static final String MAY_CONTAIN_CONTRADICTION = "may contain contradiction";
	private static final String MAY_NOT_CONTAIN_CONTRADICTION = "may not contain contradiction";
	public static IAttributeType.Boolean ATTRIBUTE = RodinCore
			.getBooleanAttrType(Activator.PLUGIN_ID + ".contradictionAttribute");

	public ContradictionAttribute() {
		super(MAY_NOT_CONTAIN_CONTRADICTION, MAY_CONTAIN_CONTRADICTION);
	}

	private IInternalElement asInternalElement(IRodinElement element) {
		if (element instanceof IAxiom) {
			return (IAxiom) element;
		}
		return null;
	}

	@Override
	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		try {
			return asInternalElement(element).getAttributeValue(ATTRIBUTE) ? MAY_NOT_CONTAIN_CONTRADICTION
					: MAY_CONTAIN_CONTRADICTION;
		} catch (RodinDBException ex) {
			// happens if the attribute is not set on this element
			// just return a default instead of throwing a RodinDBException
		}
		return MAY_CONTAIN_CONTRADICTION;
	}

	@Override
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asInternalElement(element).hasAttribute(ATTRIBUTE);
	}

	@Override
	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asInternalElement(element).removeAttribute(ATTRIBUTE, monitor);
	}

	@Override
	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asInternalElement(element).setAttributeValue(ATTRIBUTE, false, monitor);

	}

	@Override
	public void setValue(IRodinElement element, String value,
			IProgressMonitor monitor) throws RodinDBException {
		final boolean isSetToNot = value.equals(MAY_NOT_CONTAIN_CONTRADICTION);
		asInternalElement(element).setAttributeValue(ATTRIBUTE, isSetToNot,
				monitor);
	}
}
