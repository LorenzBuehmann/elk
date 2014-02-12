/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.MutableInteger;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;

/**
 * Visits only the first N inferences provided by the underlying
 * {@link TraceStore.Reader}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class FirstNInferencesReader implements TraceStore.Reader {

	private final TraceStore.Reader reader_;
	
	private final int numberOfInferencesToVisit_;
	
	public FirstNInferencesReader(TraceStore.Reader r, int n) {
		reader_ = r;
		numberOfInferencesToVisit_ = n;
	}
	
	@Override
	public void accept(IndexedClassExpression root, Conclusion conclusion, final TracedConclusionVisitor<?, ?> visitor) {
		final MutableInteger counter = new MutableInteger(0);
		
		reader_.accept(root, conclusion, new BaseTracedConclusionVisitor<Void, Void>() {

			@Override
			protected Void defaultTracedVisit(TracedConclusion inference,	Void ignored) {
				if (counter.get() < numberOfInferencesToVisit_) {
					counter.increment();
					inference.acceptTraced(visitor, null);
				}
				
				return null;
			}
			
		});
	}

}