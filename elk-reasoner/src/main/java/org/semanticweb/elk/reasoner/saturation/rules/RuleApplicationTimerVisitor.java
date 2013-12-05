package org.semanticweb.elk.reasoner.saturation.rules;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.semanticweb.elk.reasoner.indexing.hierarchy.DirectIndex.ContextRootInitializationRule;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointnessAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ContradictionImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLinkImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.PropagationImpl;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.util.logging.CachedTimeThread;

/**
 * A {@link CompositionRuleApplicationVisitor} wrapper for a given
 * {@link CompositionRuleApplicationVisitor} that additionally records the time spend
 * within methods in the given {@link RuleApplicationTimer}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class RuleApplicationTimerVisitor implements CompositionRuleApplicationVisitor {

	/**
	 * the visitor whose methods to be timed
	 */
	private final CompositionRuleApplicationVisitor visitor_;

	/**
	 * timer used to time the visitor
	 */
	private final RuleApplicationTimer timer_;

	/**
	 * Creates a new {@link DecompositionRuleApplicationVisitor} that executes
	 * the corresponding methods of the given
	 * {@link DecompositionRuleApplicationVisitor} and measures the time spent
	 * within the corresponding methods using the given
	 * {@link RuleApplicationTimer}.
	 * 
	 * @param visitor
	 *            the {@link DecompositionRuleApplicationVisitor} used to
	 *            execute the methods
	 * @param timer
	 *            the {@link RuleApplicationTimer} used to mesure the time spent
	 *            within the methods
	 */
	public RuleApplicationTimerVisitor(CompositionRuleApplicationVisitor visitor,
			RuleApplicationTimer timer) {
		this.timer_ = timer;
		this.visitor_ = visitor;
	}

	@Override
	public void visit(
			IndexedClass.OwlThingContextInitializationRule owlThingContextInitializationRule,
			BasicSaturationStateWriter writer, Context context) {
		timer_.timeOwlThingContextInitializationRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(owlThingContextInitializationRule, writer, context);
		timer_.timeOwlThingContextInitializationRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(
			IndexedDisjointnessAxiom.ThisCompositionRule thisCompositionRule,
			BasicSaturationStateWriter writer, Conclusion premise, Context context) {
		timer_.timeDisjointnessAxiomCompositionRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(thisCompositionRule, writer, premise, context);
		timer_.timeDisjointnessAxiomCompositionRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(
			IndexedDisjointnessAxiom.ThisContradictionRule thisContradictionRule,
			BasicSaturationStateWriter writer, Context context) {
		timer_.timeDisjointnessAxiomContradictionRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(thisContradictionRule, writer, context);
		timer_.timeDisjointnessAxiomContradictionRule += CachedTimeThread
				.getCurrentTimeMillis();

	}

	@Override
	public void visit(
			IndexedObjectComplementOf.ThisCompositionRule thisCompositionRule,
			BasicSaturationStateWriter writer, Context context) {
		timer_.timeObjectComplementOfCompositionRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(thisCompositionRule, writer, context);
		timer_.timeObjectComplementOfCompositionRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(
			IndexedObjectIntersectionOf.ThisCompositionRule thisCompositionRule,
			BasicSaturationStateWriter writer, Conclusion premise, Context context) {
		timer_.timeObjectIntersectionOfCompositionRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(thisCompositionRule, writer, premise, context);
		timer_.timeObjectIntersectionOfCompositionRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(
			IndexedSubClassOfAxiom.ThisCompositionRule thisCompositionRule,
			BasicSaturationStateWriter writer, Conclusion premise, Context context) {
		timer_.timeSubClassOfAxiomCompositionRule -= CachedTimeThread
				.getCurrentTimeMillis();

		visitor_.visit(thisCompositionRule, writer, premise, context);
		timer_.timeSubClassOfAxiomCompositionRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(
			IndexedObjectSomeValuesFrom.ThisCompositionRule thisCompositionRule,
			BasicSaturationStateWriter writer, Conclusion premise, Context context) {
		timer_.timeObjectSomeValuesFromCompositionRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(thisCompositionRule, writer, premise, context);
		timer_.timeObjectSomeValuesFromCompositionRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(
			IndexedObjectUnionOf.ThisCompositionRule thisCompositionRule,
			BasicSaturationStateWriter writer, Conclusion premise, Context context) {
		timer_.timeObjectUnionOfCompositionRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(thisCompositionRule, writer, premise, context);
		timer_.timeObjectUnionOfCompositionRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(ForwardLinkImpl.ThisBackwardLinkRule thisBackwardLinkRule,
			BasicSaturationStateWriter writer, BackwardLink backwardLink, Context context) {
		timer_.timeForwardLinkBackwardLinkRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(thisBackwardLinkRule, writer, backwardLink, context);
		timer_.timeForwardLinkBackwardLinkRule += CachedTimeThread
				.getCurrentTimeMillis();

	}

	@Override
	public void visit(PropagationImpl.ThisBackwardLinkRule thisBackwardLinkRule,
			BasicSaturationStateWriter writer, BackwardLink backwardLink, Context context) {
		timer_.timePropagationBackwardLinkRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(thisBackwardLinkRule, writer, backwardLink, context);
		timer_.timePropagationBackwardLinkRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(
			ContradictionImpl.ContradictionBackwardLinkRule bottomBackwardLinkRule,
			BasicSaturationStateWriter writer, BackwardLink backwardLink, Context context) {
		timer_.timeContradictionBottomBackwardLinkRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(bottomBackwardLinkRule, writer, backwardLink, context);
		timer_.timeContradictionBottomBackwardLinkRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(ContextRootInitializationRule rootInitRule,
			BasicSaturationStateWriter writer, Context context) {
		timer_.timeContextRootInitializationRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(rootInitRule, writer, context);
		timer_.timeContextRootInitializationRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

}
