package de.stups.hhu.rodinaxiompos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSource;
import org.eventb.core.ISCAxiom;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.IPOGHint;
import org.eventb.core.pog.IPOGPredicate;
import org.eventb.core.pog.IPOGSource;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.POGProcessorModule;
import org.eventb.core.pog.state.IMachineHypothesisManager;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

public class NoContradictionPOGenerator extends POGProcessorModule {

	private static final IModuleType<NoContradictionPOGenerator> MODULE_TYPE = POGCore
			.getModuleType(Activator.PLUGIN_ID + ".noContradictionPOGModule");

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	public void process(IRodinElement element, IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		final FormulaFactory ff = repository.getFormulaFactory();

		final IRodinFile contextFile = (IRodinFile) element;
		final ISCContextRoot root = (ISCContextRoot) contextFile.getRoot();

		final IPORoot target = repository.getTarget();

		final IMachineHypothesisManager machineHypothesisManager = (IMachineHypothesisManager) repository
				.getState(IMachineHypothesisManager.STATE_TYPE);

		// if the finitness of bound is not trivial
		// we generate the PO
		if (mustGeneratePO(root)) {
			final IPOGSource[] sources = createSources(root);
			final Predicate noContradictionPredicate = createPredicate(root, ff);
			createPO(
					target,
					"NCA",
					POGProcessorModule.makeNature("No Contradiction in Axioms"),
					machineHypothesisManager.getFullHypothesis(),
					Collections.<IPOGPredicate> emptyList(),
					makePredicate(noContradictionPredicate, element), sources,
					new IPOGHint[0],
					machineHypothesisManager.machineIsAccurate(), monitor);
		}
	}

	private Predicate createPredicate(ISCContextRoot root, FormulaFactory ff)
			throws CoreException {
		List<ISCAxiom> selected = new ArrayList<ISCAxiom>();
		for (ISCAxiom iscAxiom : root.getSCAxioms()) {
			if (iscAxiom.hasAttribute(ContradictionAttribute.ATTRIBUTE)) {
				if (iscAxiom
						.getAttributeValue(ContradictionAttribute.ATTRIBUTE)) {
					selected.add(iscAxiom);
				}
			}
		}

		Predicate conjunctionOfAxioms = selected.get(0).getPredicate(
				ff.makeTypeEnvironment());
		for (int i = 1; i < selected.size(); i++) {
			Predicate next = selected.get(i).getPredicate(
					ff.makeTypeEnvironment());
			conjunctionOfAxioms = ff.makeBinaryPredicate(Predicate.LAND,
					conjunctionOfAxioms, next, null);
		}

		Predicate implies = ff.makeBinaryPredicate(Predicate.LIMP,
				conjunctionOfAxioms,
				ff.makeLiteralPredicate(Predicate.FALSE, null), null);
		return ff.makeUnaryPredicate(Predicate.NOT, implies, null);
	}

	private IPOGSource[] createSources(ISCContextRoot root) {
		List<IPOGSource> sources = new ArrayList<IPOGSource>();
		try {
			for (ISCAxiom iscAxiom : root.getSCAxioms()) {
				if (iscAxiom.hasAttribute(ContradictionAttribute.ATTRIBUTE)) {
					if (iscAxiom
							.getAttributeValue(ContradictionAttribute.ATTRIBUTE)) {
						sources.add(makeSource(IPOSource.DEFAULT_ROLE, iscAxiom));
					}
				}
			}
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IPOGSource[] arr = new IPOGSource[sources.size()];
		arr = sources.toArray(arr);
		return arr;
	}

	private boolean mustGeneratePO(ISCContextRoot root) {
		try {
			for (ISCAxiom iscAxiom : root.getSCAxioms()) {
				if (iscAxiom.hasAttribute(ContradictionAttribute.ATTRIBUTE)) {
					if (iscAxiom
							.getAttributeValue(ContradictionAttribute.ATTRIBUTE)) {
						return true;
					}
				}
			}
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
