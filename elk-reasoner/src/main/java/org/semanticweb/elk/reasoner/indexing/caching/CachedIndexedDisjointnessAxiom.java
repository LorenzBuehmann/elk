package org.semanticweb.elk.reasoner.indexing.caching;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedDisjointnessAxiom;

public interface CachedIndexedDisjointnessAxiom extends
		ModifiableIndexedDisjointnessAxiom,
		CachedIndexedAxiom<CachedIndexedDisjointnessAxiom> {

	static class Helper extends CachedIndexedObject.Helper {

		public static int structuralHashCode(
				Set<? extends ModifiableIndexedClassExpression> inconsistentMembers,
				Set<? extends ModifiableIndexedClassExpression> disjointMembers) {
			return combinedHashCode(CachedIndexedDisjointnessAxiom.class,
					combinedHashCode(inconsistentMembers),
					combinedHashCode(disjointMembers));
		}

		public static CachedIndexedDisjointnessAxiom structuralEquals(
				CachedIndexedDisjointnessAxiom first, Object second) {
			if (first == second) {
				return first;
			}
			if (second instanceof CachedIndexedDisjointnessAxiom) {
				CachedIndexedDisjointnessAxiom secondEntry = (CachedIndexedDisjointnessAxiom) second;
				if (first.getDisjointMembers().equals(
						secondEntry.getDisjointMembers())
						&& first.getInconsistentMembers().equals(
								secondEntry.getInconsistentMembers()))
					return secondEntry;
			}
			// else
			return null;
		}

	}

}