package org.semanticweb.elk.explanations;
/*
 * #%L
 * Explanation Workbench
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

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.inference.ProtegeOWLReasonerInfo;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.IllegalConfigurationException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
/*
 * Copyright (C) 2010, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 06-Apr-2010
 */
public class ProtegeOWLReasonerFactoryWrapper implements OWLReasonerFactory {

    private ProtegeOWLReasonerInfo info;

    private OWLReasonerFactory reasonerFactory;

    public ProtegeOWLReasonerFactoryWrapper(ProtegeOWLReasonerInfo info) {
        this.info = info;
        this.reasonerFactory = info.getReasonerFactory();
    }

    public ProtegeOWLReasonerFactoryWrapper(OWLEditorKit editorKit) {
        this(editorKit.getOWLModelManager().getOWLReasonerManager().getCurrentReasonerFactory());
    }

    public String getReasonerName() {
        return reasonerFactory.getReasonerName();
    }

    public OWLReasoner createNonBufferingReasoner(OWLOntology ontology) {
        return reasonerFactory.createReasoner(ontology);
    }

    public OWLReasoner createReasoner(OWLOntology ontology) {
        return reasonerFactory.createReasoner(ontology);
    }

    public OWLReasoner createNonBufferingReasoner(OWLOntology ontology, OWLReasonerConfiguration owlReasonerConfiguration) throws IllegalConfigurationException {
        return reasonerFactory.createReasoner(ontology);
    }

    public OWLReasoner createReasoner(OWLOntology ontology, OWLReasonerConfiguration owlReasonerConfiguration) throws IllegalConfigurationException {
        return reasonerFactory.createReasoner(ontology);
    }
}