package de.stups.hhu.rodinaxiompos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSource;
import org.eventb.core.ISCAxiom;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.IPOGHint;
import org.eventb.core.pog.IPOGPredicate;
import org.eventb.core.pog.IPOGSource;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.POGProcessorModule;
import org.eventb.core.pog.state.IContextHypothesisManager;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

public class NoContradictionModule extends POGProcessorModule {

	private static final IModuleType<NoContradictionModule> MODULE_TYPE = POGCore
			.getModuleType(Activator.PLUGIN_ID + ".noContradictionModule");

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

		final IContextHypothesisManager contextHypothesisManager = (IContextHypothesisManager) repository
				.getState(IContextHypothesisManager.STATE_TYPE);

		IPOPredicateSet hyps = contextHypothesisManager.getRootHypothesis();

		if (mustGeneratePO(root)) {
			final IPOGSource[] sources = createSources(root);
			final Predicate noContradictionPredicate = createPredicate(root, ff);
			createPO(
					target,
					"NCA",
					POGProcessorModule.makeNature("No Contradiction in Axioms"),
					hyps, Collections.<IPOGPredicate> emptyList(),
					makePredicate(noContradictionPredicate, element), sources,
					new IPOGHint[0],
					contextHypothesisManager.contextIsAccurate(), monitor);
		}
	}

	private Predicate createPredicate(ISCContextRoot root, FormulaFactory ff)
			throws CoreException {
		ITypeEnvironmentBuilder te = ff.makeTypeEnvironment();

		List<Predicate> selected = new ArrayList<Predicate>();
		for (ISCAxiom iscAxiom : root.getSCAxioms()) {
			if (iscAxiom.hasAttribute(ContradictionAttribute.ATTRIBUTE)) {
				if (iscAxiom
						.getAttributeValue(ContradictionAttribute.ATTRIBUTE)) {
					selected.add(iscAxiom.getPredicate(te));
				}
			}
		}

		Predicate conjunctionOfAxioms;

		if (selected.size() == 1) {
			conjunctionOfAxioms = selected.get(0);
		} else {
			conjunctionOfAxioms = ff.makeAssociativePredicate(Predicate.LAND,
					selected, null);
		}

		List<BoundIdentDecl> theBoundOnes = new ArrayList<BoundIdentDecl>();

		conjunctionOfAxioms = conjunctionOfAxioms
				.bindAllFreeIdents(theBoundOnes);
		Predicate full = ff.makeQuantifiedPredicate(Predicate.EXISTS,
				theBoundOnes, conjunctionOfAxioms, null);
		return full;

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
