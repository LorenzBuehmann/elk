package org.semanticweb.elk.reasoner.saturation.rules.subsumers;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ChainableRule;
import org.semanticweb.elk.reasoner.saturation.rules.CompositionRuleVisitor;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ModifiableLinkImpl;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * The composition rule producing {@link Subsumer} for the super class of
 * {@link IndexedSubClassOfAxiom} when processing its sub class
 * {@link IndexedClassExpression}
 * 
 * @see IndexedSubClassOfAxiom#getSuperClass()
 * @see IndexedSubClassOfAxiom#getSubClass()
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class SuperClassFromSubClassRule extends
		ModifiableLinkImpl<ChainableRule<IndexedClassExpression>> implements
		ChainableRule<IndexedClassExpression> {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(SuperClassFromSubClassRule.class);

	private static final String NAME_ = "SubClassOf Expansion";

	/**
	 * Correctness of axioms deletions requires that toldSuperClassExpressions
	 * is a List.
	 */
	private final List<IndexedClassExpression> toldSuperClassExpressions_;

	private SuperClassFromSubClassRule(
			ChainableRule<IndexedClassExpression> tail) {
		super(tail);
		this.toldSuperClassExpressions_ = new ArrayList<IndexedClassExpression>(
				1);
	}

	private SuperClassFromSubClassRule(IndexedClassExpression ice) {
		super(null);
		this.toldSuperClassExpressions_ = new ArrayList<IndexedClassExpression>(
				1);

		toldSuperClassExpressions_.add(ice);
	}

	public static void addRuleFor(IndexedSubClassOfAxiom axiom,
			ModifiableOntologyIndex index) {
		index.add(axiom.getSubClass(),
				new SuperClassFromSubClassRule(axiom.getSuperClass()));
	}

	public static void removeRuleFor(IndexedSubClassOfAxiom axiom,
			ModifiableOntologyIndex index) {
		index.remove(axiom.getSubClass(),
				new SuperClassFromSubClassRule(axiom.getSuperClass()));
	}

	// TODO: hide this method
	public Collection<IndexedClassExpression> getToldSuperclasses() {
		return toldSuperClassExpressions_;
	}

	@Override
	public String getName() {
		return NAME_;
	}

	@Override
	public void apply(IndexedClassExpression premise, Context context,
			SaturationStateWriter writer) {
		LOGGER_.trace("Applying {}: {} to {}", NAME_,
				toldSuperClassExpressions_, context);

		for (IndexedClassExpression implied : toldSuperClassExpressions_) {
			writer.produce(context, new DecomposedSubsumer(implied));
		}
	}

	@Override
	public boolean addTo(Chain<ChainableRule<IndexedClassExpression>> ruleChain) {
		SuperClassFromSubClassRule rule = ruleChain.getCreate(
				SuperClassFromSubClassRule.MATCHER_,
				SuperClassFromSubClassRule.FACTORY_);
		boolean changed = false;

		for (IndexedClassExpression ice : toldSuperClassExpressions_) {
			LOGGER_.trace("Adding {} to {}", ice, NAME_);

			changed |= rule.addToldSuperClassExpression(ice);
		}

		return changed;

	}

	@Override
	public boolean removeFrom(
			Chain<ChainableRule<IndexedClassExpression>> ruleChain) {
		SuperClassFromSubClassRule rule = ruleChain
				.find(SuperClassFromSubClassRule.MATCHER_);
		boolean changed = false;

		if (rule != null) {
			for (IndexedClassExpression ice : toldSuperClassExpressions_) {
				LOGGER_.trace("Removing {} from {}", ice, NAME_);

				changed |= rule.removeToldSuperClassExpression(ice);
			}

			if (rule.isEmpty()) {
				ruleChain.remove(SuperClassFromSubClassRule.MATCHER_);

				LOGGER_.trace("{}: removed ", NAME_);

				return true;
			}
		}

		return changed;

	}

	@Override
	public void accept(CompositionRuleVisitor visitor,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer) {
		visitor.visit(this, premise, context, writer);
	}

	protected boolean addToldSuperClassExpression(
			IndexedClassExpression superClassExpression) {
		return toldSuperClassExpressions_.add(superClassExpression);
	}

	/**
	 * @param superClassExpression
	 * @return true if successfully removed
	 */
	protected boolean removeToldSuperClassExpression(
			IndexedClassExpression superClassExpression) {
		return toldSuperClassExpressions_.remove(superClassExpression);
	}

	/**
	 * @return {@code true} if this rule never does anything
	 */
	private boolean isEmpty() {
		return toldSuperClassExpressions_.isEmpty();
	}

	@Override
	public String toString() {
		return getName() + ": " + toldSuperClassExpressions_;
	}

	private static final Matcher<ChainableRule<IndexedClassExpression>, SuperClassFromSubClassRule> MATCHER_ = new SimpleTypeBasedMatcher<ChainableRule<IndexedClassExpression>, SuperClassFromSubClassRule>(
			SuperClassFromSubClassRule.class);

	private static final ReferenceFactory<ChainableRule<IndexedClassExpression>, SuperClassFromSubClassRule> FACTORY_ = new ReferenceFactory<ChainableRule<IndexedClassExpression>, SuperClassFromSubClassRule>() {
		@Override
		public SuperClassFromSubClassRule create(
				ChainableRule<IndexedClassExpression> tail) {
			return new SuperClassFromSubClassRule(tail);
		}
	};

}