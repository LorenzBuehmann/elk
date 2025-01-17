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

import java.net.URL;

import org.semanticweb.elk.testing.TestResultComparisonException;

public class TaxonomyDiffManifest<EO extends ClassTaxonomyTestOutput<?>, AO extends ClassTaxonomyTestOutput<?>>
		extends ReasoningTestManifest<EO, AO> {

	public TaxonomyDiffManifest(URL input, EO expOutput) {
		super(input, expOutput);
	}

	@Override
	public void compare(AO actualOutput) throws TestResultComparisonException {

		// FIXME Implement taxonomy comparison and diff
		int expectedHash = getExpectedOutput().getHashCode();
		int actualHash = actualOutput.getHashCode();
		if (expectedHash != actualHash) {
			// FIXME: where do I see the expected and actual values (if I do not
			// print them myself as below)?
			throw new TestResultComparisonException("\n"
					+ "EXPECTED TAXONOMY: (hash=" + expectedHash + ")\n"
					+ getExpectedOutput() + "ACTUAL TAXONOMY: (hash="
					+ actualHash + ")\n" + actualOutput, getExpectedOutput(),
					actualOutput);
		}
	}
}