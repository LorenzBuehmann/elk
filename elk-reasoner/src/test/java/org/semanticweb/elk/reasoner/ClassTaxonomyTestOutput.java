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
package org.semanticweb.elk.reasoner;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyPrinter;
import org.semanticweb.elk.reasoner.taxonomy.hashing.TaxonomyHasher;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.testing.TestOutput;

public class ClassTaxonomyTestOutput<T extends Taxonomy<ElkClass>> implements
		TestOutput {

	private final T taxonomy_;

	ClassTaxonomyTestOutput(T taxonomy) {
		this.taxonomy_ = taxonomy;
	}

	public T getTaxonomy() {
		return this.taxonomy_;
	}

	int getHashCode() {
		return TaxonomyHasher.hash(taxonomy_);
	}

	@Override
	public String toString() {

		Writer writer = new StringWriter();
		try {
			TaxonomyPrinter.dumpClassTaxomomy(taxonomy_, writer, false);
		} catch (IOException e) {
			// TODO: what to return?
			return e.toString();
		}
		return writer.toString();

		// return String.valueOf(getHashCode());
	}

}