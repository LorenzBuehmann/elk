package org.semanticweb.elk.reasoner.stages.debug;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.reasoner.taxonomy.BasicTaxonomyValidator;
import org.semanticweb.elk.reasoner.taxonomy.InvalidTaxonomyException;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyAcyclicityAndReductionValidator;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyLinkConsistencyVisitor;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyNodeDisjointnessVisitor;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyNodeIndexConsistencyVisitor;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyValidator;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;

/**
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ValidateTaxonomyStage extends BasePostProcessingStage {

	private static final Logger LOGGER_ = Logger
			.getLogger(ContextSaturationFlagCheckingStage.class);	

	private final Taxonomy<ElkClass> taxonomy_;
	
	public ValidateTaxonomyStage(Taxonomy<ElkClass> t) {
		taxonomy_ = t;
	}
	
	@Override
	public String getName() {
		return "Checking validity of class taxonomy";
	}

	@Override
	public void execute() throws ElkException {
	
		try {
			if (taxonomy_ != null) {
									
				TaxonomyValidator<ElkClass> validator = new BasicTaxonomyValidator<ElkClass>()
						.add(new TaxonomyNodeDisjointnessVisitor<ElkClass>())
						.add(new TaxonomyLinkConsistencyVisitor<ElkClass>())
						.add(new TaxonomyNodeIndexConsistencyVisitor<ElkClass>());

				validator.validate(taxonomy_);

				new TaxonomyAcyclicityAndReductionValidator<ElkClass>()
						.validate(taxonomy_);
			}
		} catch (InvalidTaxonomyException e) {
			LOGGER_.error("Invalid taxonomy", e);
			
			throw e;
		}
	}
}