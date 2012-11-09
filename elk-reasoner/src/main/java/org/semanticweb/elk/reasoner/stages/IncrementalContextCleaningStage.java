/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
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

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.incremental.IncrementalStages;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturation;
import org.semanticweb.elk.reasoner.saturation.rules.ContextCleaningFactory;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationFactory;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class IncrementalContextCleaningStage extends AbstractReasonerStage {

	// logger for this class
	private static final Logger LOGGER_ = Logger.getLogger(IncrementalContextCleaningStage.class);

	private ClassExpressionSaturation<IndexedClassExpression> cleaning_ = null;

	public IncrementalContextCleaningStage(AbstractReasonerState reasoner) {
		super(reasoner);
	}

	@Override
	public String getName() {
		return IncrementalStages.CONTEXT_CLEANING.toString();
	}

	@Override
	public boolean done() {
		return reasoner.incrementalState.getStageStatus(IncrementalStages.CONTEXT_CLEANING);
	}

	@Override
	public List<ReasonerStage> getDependencies() {
		return Arrays.asList((ReasonerStage) new IncrementalContextInitializationStage(reasoner, new IncrementalDeSaturationStage(reasoner)));
	}

	@Override
	public void execute() throws ElkInterruptedException {
		if (cleaning_ == null) {
			initComputation();
		}
		
		progressMonitor.start(getName());
		
		try {
			for (;;) {
				cleaning_.process();
				if (!interrupted())
					break;
			}
		} finally {
			progressMonitor.finish();
		}
		
		reasoner.incrementalState.setStageStatus(IncrementalStages.CONTEXT_CLEANING, true);
	}
	
	

	@Override
	void initComputation() {
		super.initComputation();

		RuleApplicationFactory cleaningFactory = new ContextCleaningFactory(reasoner.saturationState, true);
		
		LOGGER_.trace(reasoner.saturationState.getModifiedContexts());
		
		cleaning_ = new ClassExpressionSaturation<IndexedClassExpression>(
				reasoner.getProcessExecutor(),
				workerNo,
				reasoner.getProgressMonitor(),
				cleaningFactory);
	}

	@Override
	public void printInfo() {
		if (cleaning_ != null)
			cleaning_.printStatistics();
	}
}