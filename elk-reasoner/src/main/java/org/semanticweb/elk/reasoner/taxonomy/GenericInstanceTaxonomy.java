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
package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Set;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTypeNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.InstanceNode;

/**
 * A {@link GenericTaxonomy} that also stores a (possibly empty set) of
 * {@link InstanceNode}s. The types of each {@link InstanceNode} should be also
 * stored in this {@link GenericInstanceTaxonomy}. The sets of members of the
 * {@link InstanceNode}s of this {@link GenericTaxonomy} should be disjoint.
 * 
 * @author Markus Kroetzsch
 * @author "Yevgeny Kazakov"
 * 
 * @param <K>
 *            the type of the keys for the node members
 * @param <M>
 *            the type of node members
 * @param <KI>
 *            the type of the keys for the node instances
 * @param <I>
 *            the type of instances
 * @param <TN>
 *            the type of type nodes in this {@link GenericInstanceTaxonomy}
 * @param <IN>
 *            the type of instance nodes of this {@link GenericInstanceTaxonomy}
 */
public interface GenericInstanceTaxonomy<K, M, KI, I, 
			TN extends GenericTypeNode<K, M, KI, I, TN, IN>, 
			IN extends GenericInstanceNode<K, M, KI, I, TN, IN>>
		extends 
		GenericTaxonomy<K, M, TN>, 
		InstanceTaxonomy<K, M, KI, I> {
	
	@Override
	public Set<? extends IN> getInstanceNodes();

	@Override
	public IN getInstanceNode(KI key);

}