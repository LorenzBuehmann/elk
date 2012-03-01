/*
 * #%L
 * ELK OWL Object Interfaces
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
package org.semanticweb.elk.owl.interfaces;

/**
 * /** A generic interface for object and data property assertion axioms.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <P>
 *            the type of the property of this assertion
 * @param <S>
 *            the type of the subject of this assertion
 * @param <O>
 *            the type of the object of this assertion
 */
public interface ElkPropertyAssertionAxiom<P, S, O> extends
		ElkPropertyAxiom<P>, ElkAssertionAxiom {

	/**
	 * Get the subject of this restriction.
	 * 
	 * @return the subject of this restriction
	 */
	S getSubject();

	/**
	 * Get the object of this restriction.
	 * 
	 * @return the object of this restriction
	 */
	O getObject();

}
