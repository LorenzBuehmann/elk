/**
 * 
 */
package org.semanticweb.elk.owlapi.proofs;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.utils.TautologyChecker;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.owlapitools.proofs.OWLInference;
import org.semanticweb.owlapitools.proofs.exception.ProofGenerationException;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public abstract class BaseDerivedExpressionWrap<E extends DerivedExpression> implements OWLExpression {

	final E expression;
	
	BaseDerivedExpressionWrap(E expr) {
		expression = expr;
	}
	
	@Override
	public Iterable<OWLInference> getInferences() throws ProofGenerationException {
		try {
			return Operations.map(expression.getInferences(), new Operations.Transformation<Inference, OWLInference>() {

				@Override
				public OWLInference transform(Inference inference) {
					return ElkToOwlProofConverter.convert(inference);
				}
			});
		} catch (ElkException e) {
			throw new ProofGenerationException(e);
		}
	}

	@Override
	public boolean isTautology() {
		return expression.accept(new TautologyChecker(), null);
	}
}