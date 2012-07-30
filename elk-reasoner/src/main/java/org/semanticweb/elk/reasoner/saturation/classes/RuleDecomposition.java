/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
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
package org.semanticweb.elk.reasoner.saturation.classes;

import java.util.List;

import org.semanticweb.elk.reasoner.datatypes.DatatypeEngine;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDatatypeExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.saturation.rulesystem.RuleApplicationFactory;

/**
 * TODO: documentation
 * 
 * @author Frantisek Simancik
 * 
 * @param <C>
 *            the type of contexts that can be used with this inference rule
 */
public class RuleDecomposition<C extends ContextElClassSaturation> implements
		InferenceRulePosSCE<C> {

	private class ClassExpressionDecomposer implements
			IndexedClassExpressionVisitor<Void> {

		private final C context;
		private final RuleApplicationFactory.Engine engine;

		public ClassExpressionDecomposer(C context,
				RuleApplicationFactory.Engine engine) {
			this.context = context;
			this.engine = engine;
		}

		@Override
		public Void visit(IndexedClass ice) {
			return null;
		}

		@Override
		public Void visit(IndexedObjectIntersectionOf ice) {
			engine.enqueue(context,
					new PositiveSuperClassExpression<C>(ice.getFirstConjunct()));
			engine.enqueue(
					context,
					new PositiveSuperClassExpression<C>(ice.getSecondConjunct()));
			return null;
		}

		@Override
		public Void visit(IndexedObjectSomeValuesFrom ice) {
			engine.enqueue(engine.getCreateContext(ice.getFiller()),
					new BackwardLink<C>(ice.getRelation(), context));
			return null;
		}

		@Override
		public Void visit(IndexedDatatypeExpression element) {
			//TODO: obtain matching negative existentials by retrieving all value spaces for the
			// data property of this element and finding those value spaces among them that subsume
			// the value space of this element
			return null;
//			List<IndexedDatatypeExpression> satisfyingNegExistentials = DatatypeEngine
//					.getSatisfyingNegExistentials(element.getProperty(),
//							element);
//			if (satisfyingNegExistentials == null) {
//				engine.enqueue(context, new PositiveSuperClassExpression<C>(
//						engine.getOwlNothing()));
//			} else {
//				for (IndexedDatatypeExpression negDatatypeExpr : satisfyingNegExistentials) {
//					if (element != negDatatypeExpr) {
//						engine.enqueue(context,
//								new PositiveSuperClassExpression<C>(
//										negDatatypeExpr));
//					}
//				}
//			}
//			return null;
		}

		@Override
		public Void visit(IndexedIndividual element) {
			return null;
		}

	};

	@Override
	public void applySCE(PositiveSuperClassExpression<C> argument, C context,
			RuleApplicationFactory.Engine engine) {
		ClassExpressionDecomposer decomposer = new ClassExpressionDecomposer(
				context, engine);
		argument.getExpression().accept(decomposer);
	}

}
