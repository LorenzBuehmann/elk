/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.rules;

import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Bottom;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointnessAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.IndexChange;
import org.semanticweb.elk.reasoner.saturation.conclusions.NegativeSuperClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.PositiveSuperClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.SuperClassExpression;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * Creates an engine which applies rules backwards, e.g., removes conclusions from the context instead of adding them
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class RuleDeapplicationFactory extends RuleApplicationFactory {

	public RuleDeapplicationFactory(final SaturationState saturationState) {
		super(saturationState);
	}
	
	public RuleDeapplicationFactory(final SaturationState saturationState, boolean trackModifiedContexts) {
		super(saturationState, trackModifiedContexts);
	}	

	@Override
	public Engine getEngine() {
		return new DeletionEngine(new DeleteConclusionVisitor());
	}

	/**
	 * 
	 */
	public class DeletionEngine extends Engine {
		
		protected final ConclusionVisitor<Boolean> containsVisitor;
		
		protected DeletionEngine(ConclusionVisitor<Boolean> postVisitor) {
			super(postVisitor);
			containsVisitor = new ContainsConclusionVisitor();
		}
		
		protected DeletionEngine(ConclusionVisitor<Boolean> preVisitor, ConclusionVisitor<Boolean> postVisitor) {
			super(postVisitor);
			containsVisitor = preVisitor;
		}
		
		@Override
		protected boolean preApply(Conclusion conclusion, Context context) {
			return conclusion.accept(containsVisitor, context);
		}

		@Override
		protected void process(Conclusion conclusion, Context context) {
			conclusion.deapply(saturationState_, context);
		}
		
		@Override
		protected void postApply(Conclusion conclusion, Context context) {
			conclusion.accept(conclusionVisitor, context);
		}		
	}
	
	/**
	 * Used to check whether conclusions are contained in the context
	 */
	protected class ContainsConclusionVisitor implements ConclusionVisitor<Boolean> {

		protected Boolean visitSuperclass(SuperClassExpression sce, Context context) {
			return context.containsSuperClassExpression(sce.getExpression());
		}		
		
		@Override
		public Boolean visit(NegativeSuperClassExpression negSCE, Context context) {
			return visitSuperclass(negSCE, context);
		}

		@Override
		public Boolean visit(PositiveSuperClassExpression posSCE, Context context) {
			return visitSuperclass(posSCE, context);
		}

		@Override
		public Boolean visit(BackwardLink link, Context context) {
			return context.containsBackwardLink(link);
		}

		@Override
		public Boolean visit(ForwardLink link, Context context) {
			return link.containsBackwardLinkRule(context);
		}
		
		@Override
		public Boolean visit(IndexChange indexChange, Context context) {
			return true;
		}

		@Override
		public Boolean visit(Bottom bot, Context context) {
			return context.isInconsistent();
		}

		@Override
		public Boolean visit(Propagation propagation, Context context) {
			return propagation.containsBackwardLinkRule(context);
		}

		@Override
		public Boolean visit(DisjointnessAxiom axiom, Context context) {
			return context.containsDisjointnessAxiom(axiom.getAxiom()) > 0;
		}		
	}	
	
	/**
	 * Used to remove different kinds of conclusions from the context
	 */
	protected class DeleteConclusionVisitor extends BaseConclusionVisitor {

		public Boolean visitSuperclass(SuperClassExpression sce, Context context) {
			if (context.removeSuperClassExpression(sce.getExpression())) {
				
				if (LOGGER_.isTraceEnabled()) {
					LOGGER_.trace(context.getRoot() + ": removing superclass " + sce);
				}
				
				markAsModified(context);
				
				return true;
			}
			
			return false;
		}		
		
		@Override
		public Boolean visit(NegativeSuperClassExpression negSCE,
				Context context) {
			return visitSuperclass(negSCE, context);
		}

		@Override
		public Boolean visit(PositiveSuperClassExpression posSCE,
				Context context) {
			return visitSuperclass(posSCE, context);
		}

		@Override
		public Boolean visit(BackwardLink link, Context context) {
			if (context.removeBackwardLink(link)) {
				if (LOGGER_.isTraceEnabled()) {
					LOGGER_.trace(context.getRoot() + ": removing backward link " + link);
				}
				
				return true;
			}
			
			return false;
		}

		@Override
		public Boolean visit(ForwardLink link, Context context) {
			//statistics_.forwLinkInfNo++;
			if (link.removeFromContextBackwardLinkRule(context)) {
				
				return true;
			}
			
			return false;
			//statistics_.forwLinkNo++;
		}
		
		@Override
		public Boolean visit(IndexChange indexChange, Context context) {
			// need not remove this element, just apply all its rules
			return true;
		}	
		
		@Override
		public Boolean visit(Bottom bot, Context context) {
			return context.isInconsistent();
		}

		@Override
		public Boolean visit(Propagation propagation, Context context) {
			if (propagation.removeFromContextBackwardLinkRule(context)) {
				
				return true;
			}
			
			return false;
		}

		@Override
		public Boolean visit(DisjointnessAxiom axiom, Context context) {
			
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace("Removing disjointness axiom from " + context.getRoot());
			}
			
			context.removeDisjointnessAxiom(axiom.getAxiom());
			 
			return true;
		}		
	}	
}