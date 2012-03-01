package org.semanticweb.elk.owl.managers;
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


import org.semanticweb.elk.owl.interfaces.ElkObject;

/**
 * Implementation of the ElkObjectManager interface that does not keep track of
 * existing objects and rather allows for multiple instances of structurally
 * equivalent ElkObjects. This reduces the management overhead.
 * 
 * @author Markus Kroetzsch
 */
public class DummyElkObjectManager implements ElkObjectManager {

	public ElkObject getCanonicalElkObject(ElkObject object) {
		return object;
	}

}
