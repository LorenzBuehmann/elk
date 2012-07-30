/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkDataProperty;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkDataRange;
import org.semanticweb.elk.owl.interfaces.ElkDataSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkDatatypeRestriction;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasValue;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyChain;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.datatypes.DatatypeEngine;
import org.semanticweb.elk.reasoner.datatypes.enums.Datatype;
import org.semanticweb.elk.reasoner.datatypes.handlers.DatatypeHandler;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;

/**
 * Visitor for Elk classes, properties, and individuals that returns the
 * corresponding indexed objects, already filtered through the
 * IndexedObjectFilter provided in the constructor.
 * 
 * @author Frantisek Simancik
 * 
 */
public class ElkObjectIndexerVisitor extends AbstractElkObjectIndexerVisitor {

	// logger for events
	private static final Logger LOGGER_ = Logger
			.getLogger(ElkObjectIndexerVisitor.class);

	protected IndexedObjectFilter objectFilter;

	/**
	 * @param objectFilter
	 *            filter that is applied to the indexed objects after
	 *            construction
	 */
	public ElkObjectIndexerVisitor(IndexedObjectFilter objectFilter) {
		this.objectFilter = objectFilter;
	}

	@Override
	public IndexedClassExpression visit(ElkClass elkClass) {
		return objectFilter.filter(new IndexedClass(elkClass));
	}

	@Override
	public IndexedClassExpression visit(ElkObjectHasValue elkObjectHasValue) {
		IndexedObjectProperty iop = (IndexedObjectProperty) elkObjectHasValue
				.getProperty().accept(this);
		return objectFilter.filter(new IndexedObjectSomeValuesFrom(iop,
				elkObjectHasValue.getFiller().accept(this)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.semanticweb.elk.owl.visitors.ElkClassExpressionVisitor#visit(org.
	 * semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf)
	 * 
	 * Binarization of conjunctions. To be able to use a map instead of a
	 * multimap in IndexedClassExpression.negConjunctionsByConjunct It is
	 * important to ensure that we never create both (A & B) and (B & A). This
	 * is achieved by ordering conjuncts so that A < B in each binary
	 * conjunction. The comparison is based on hashCodes generated by {@link
	 * HashGenerator}. Although these are not guaranteed to be unique in
	 * general, current implementation of HashGenerator seems to generate all
	 * 2^32 different hashCodes before repeating. A runtime exception is thrown
	 * in {@link IndexedClassExpression.addNegConjunctionByConjunct} when equal
	 * hashCodes would lead to corrupted index.
	 */
	@Override
	public IndexedClassExpression visit(
			ElkObjectIntersectionOf elkObjectIntersectionOf) {

		IndexedClassExpression result = null;
		for (ElkClassExpression c : elkObjectIntersectionOf
				.getClassExpressions()) {
			IndexedClassExpression ice = c.accept(this);

			if (result == null) {
				result = ice;
				continue;
			}

			IndexedClassExpression firstConjunct, secondConjunct;
			if (result.hashCode() < ice.hashCode()) {
				firstConjunct = result;
				secondConjunct = ice;
			} else {
				firstConjunct = ice;
				secondConjunct = result;
			}

			result = objectFilter.filter(new IndexedObjectIntersectionOf(
					firstConjunct, secondConjunct));
		}

		return result;
	}

	@Override
	public IndexedClassExpression visit(
			ElkObjectSomeValuesFrom elkObjectSomeValuesFrom) {
		IndexedObjectProperty iop = (IndexedObjectProperty) elkObjectSomeValuesFrom
				.getProperty().accept(this);
		return objectFilter.filter(new IndexedObjectSomeValuesFrom(iop,
				elkObjectSomeValuesFrom.getFiller().accept(this)));
	}

	@Override
	public IndexedClassExpression visit(ElkDataHasValue elkDataHasValue) {
		IndexedDataProperty idp = (IndexedDataProperty) elkDataHasValue
				.getProperty().accept(this);

		ElkLiteral filler = elkDataHasValue.getFiller();
		Datatype datatype = Datatype.getByIri(filler.getDatatype()
				.getDatatypeIRI());
		DatatypeHandler handler = DatatypeEngine.getDatatypeHandler(datatype);
		ValueSpace valueSpace = handler.getValueSpace(filler, datatype);
		return objectFilter.filter(new IndexedDatatypeExpression(idp,
				valueSpace));
	}

	@Override
	public IndexedClassExpression visit(
			ElkDataSomeValuesFrom elkDataSomeValuesFrom) {
		List<? extends ElkDataPropertyExpression> exps = elkDataSomeValuesFrom
				.getDataPropertyExpressions();
		if (exps != null && exps.size() == 1) {
			IndexedDataProperty idp = (IndexedDataProperty) exps.get(0).accept(
					this);
			ElkDataRange dataRange = elkDataSomeValuesFrom.getDataRange();
			Datatype datatype;

			if (dataRange instanceof ElkDatatype) {
				datatype = Datatype.getByIri(((ElkDatatype) dataRange)
						.getDatatypeIRI());
			} else {
				datatype = Datatype
						.getByIri(((ElkDatatypeRestriction) dataRange)
								.getDatatype().getDatatypeIRI());
			}
			DatatypeHandler handler = DatatypeEngine
					.getDatatypeHandler(datatype);
			ValueSpace valueSpace = handler.getValueSpace(dataRange, datatype);

			return objectFilter.filter(new IndexedDatatypeExpression(idp,
					valueSpace));
		} else {
			throw new IndexingException(
					ElkDataSomeValuesFrom.class.getSimpleName()
							+ "with multiple properties not supported");
		}
	}

	@Override
	public IndexedPropertyChain visit(ElkObjectProperty elkObjectProperty) {
		return objectFilter
				.filter(new IndexedObjectProperty(elkObjectProperty));
	}

	@Override
	public IndexedDataProperty visit(ElkDataProperty elkDataProperty) {
		return objectFilter.filter(new IndexedDataProperty(elkDataProperty));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.semanticweb.elk.owl.visitors.ElkSubObjectPropertyExpressionVisitor
	 * #visit(org.semanticweb.elk.owl.interfaces.ElkObjectPropertyChain)
	 * 
	 * Binarization of role chains. Order must be preserved.
	 */
	@Override
	public IndexedPropertyChain visit(
			ElkObjectPropertyChain elkObjectPropertyChain) {

		IndexedPropertyChain result = null;
		ListIterator<? extends ElkObjectPropertyExpression> iterator = elkObjectPropertyChain
				.getObjectPropertyExpressions().listIterator(
						elkObjectPropertyChain.getObjectPropertyExpressions()
								.size());

		while (iterator.hasPrevious()) {
			IndexedObjectProperty iop = (IndexedObjectProperty) iterator
					.previous().accept(this);

			if (result == null) {
				result = iop;
				continue;
			}

			result = objectFilter.filter(new IndexedBinaryPropertyChain(iop,
					result));
		}

		return result;
	}

	@Override
	public IndexedIndividual visit(ElkNamedIndividual elkNamedIndividual) {
		return (IndexedIndividual) objectFilter.filter(new IndexedIndividual(
				elkNamedIndividual));
	}
}
