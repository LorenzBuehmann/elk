/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation;
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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.PositiveSuperClassExpression;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.ContextImpl;
import org.semanticweb.elk.reasoner.saturation.rules.ContextRules;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class SaturationState {

	// logger for this class
	private static final Logger LOGGER_ = Logger.getLogger(SaturationState.class);
	
	private final OntologyIndex ontologyIndex_;
	
	/**
	 * Cached constants
	 */
	private final IndexedClassExpression owlThing_, owlNothing_;

	/**
	 * The queue containing all activated contexts. Every activated context
	 * occurs exactly once.
	 */
	private final Queue<Context> activeContexts_ = new ConcurrentLinkedQueue<Context>();
	
	private final ContextCreationListenerChain contextCreationListeners_ = new ContextCreationListenerChain();
	
	private Queue<IndexedClassExpression> modifiedContexts_ = new ConcurrentLinkedQueue<IndexedClassExpression>();
	
	
	public SaturationState(OntologyIndex index) {
		ontologyIndex_ = index;
		owlThing_ = index.getIndexedOwlThing();
		owlNothing_ = index.getIndexedOwlNothing();
	}	
	
	public void markAsModified(Context context) {
		modifiedContexts_.add(context.getRoot());
	}
	
	public Collection<IndexedClassExpression> getModifiedContexts() {
		return modifiedContexts_ == null ? Collections.<IndexedClassExpression>emptyList() : modifiedContexts_;
	}
	
	public void clearModifiedContexts() {
		modifiedContexts_.clear();
	}
	
	public Context pollForContext() {
		return activeContexts_.poll();
	}
	
	public IndexedClassExpression getOwlThing() {
		return owlThing_;
	}
	
	public IndexedClassExpression getOwlNothing() {
		return owlNothing_;
	}
	
	public void produce(Context context, Conclusion item) {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace(context.getRoot() + ": new conclusion " + item);
		if (context.addToDo(item)) {
			// context was activated
			activeContexts_.add(context);
			//LOGGER_.trace(context.getRoot() + " was activated!");	
		}
	}
	
	public Context getCreateContext(IndexedClassExpression root) {
		if (root.getContext() == null) {
			Context context = new ContextImpl(root);
			if (root.setContext(context)) {
				initContext(context);
				contextCreationListeners_.notifyAll(context);
			}
		}
		return root.getContext();
	}	
	
	public void initContext(Context context) {
		produce(context, new PositiveSuperClassExpression(context.getRoot()));
		//apply all context initialization rules
		ContextRules initRules = ontologyIndex_.getContextInitRules();
		
		while (initRules != null) {
			initRules.apply(this, context);
			initRules = initRules.next();
		}
	}
	
	public void registerContextCreationListener(ContextCreationListener listener) {
		contextCreationListeners_.prepend(listener);
	}
	
	public void deregisterContextCreationListener(ContextCreationListener listener) {
		contextCreationListeners_.remove(listener);
	}
	
	int size() {
		return activeContexts_.size();
	}
	
	Iterator<Context> getActiveContextIterator() {
		return activeContexts_.iterator();
	}
	
	/**
	 * A tiny chain of listeners supporting register/deregister operations
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	private static class ContextCreationListenerChain {
		
		private ContextCreationListener head_;
		
		private ContextCreationListenerChain tail_;
		
		ContextCreationListenerChain() {
			this(null, null);
		}
		
		ContextCreationListenerChain(ContextCreationListener head, ContextCreationListenerChain tail) {
			head = head_;
			tail = tail_;
		}
		
		/**
		 * Notify all registered listeners that a context has been created
		 */
		void notifyAll(Context newContext) {
			ContextCreationListener head = head_;
			
			while (head != null) {
				head.notifyContextCreation(newContext);
				head = tail_ == null ? null : tail_.head_;
			}
		}
		
		void prepend(ContextCreationListener listener) {
			ContextCreationListenerChain newTail = new ContextCreationListenerChain(head_, tail_);
			
			head_ = listener;
			tail_ = newTail;
		}
		
		void remove(ContextCreationListener listener) {
			ContextCreationListenerChain curr = this;
			ContextCreationListenerChain prev = null;
			
			while (curr != null && curr.head_ != null) {
				if (curr.head_ == listener) {
					if (prev == null) {
						//removing the head
						head_ = tail_ == null ? null : tail_.head_;
						tail_ = tail_ == null ? null : tail_.tail_;
					}
					else {
						prev.tail_ = curr.tail_;
						curr.tail_ = null;
					}
					
					break;
				}
				
				prev = curr;
				curr = curr.tail_;
			}
		}
	}
}