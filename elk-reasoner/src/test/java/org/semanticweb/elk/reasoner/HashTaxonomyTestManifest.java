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
/**
 * 
 */
package org.semanticweb.elk.reasoner;

import java.net.URL;

import org.semanticweb.elk.testing.HashTestOutput;
import org.semanticweb.elk.testing.TestResultComparisonException;

/**
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 * @param <AO>
 */
public class HashTaxonomyTestManifest<AO extends ClassTaxonomyTestOutput<?>>
		extends ReasoningTestManifest<HashTestOutput, AO> {

	public HashTaxonomyTestManifest(URL input, HashTestOutput expOutput) {
		super(input, expOutput);
	}

	@Override
	public void compare(AO actualOutput) throws TestResultComparisonException {
		if (getExpectedOutput().getHash() != (actualOutput.getHashCode())) {
			throw new TestResultComparisonException(
					"Expected taxonomy hashcode not equal to the actual hashcode",
					getExpectedOutput(), actualOutput);
		}
	}
}
