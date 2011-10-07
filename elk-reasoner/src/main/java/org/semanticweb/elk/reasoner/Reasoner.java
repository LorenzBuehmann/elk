/*
 * #%L
 * elk-reasoner
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
package org.semanticweb.elk.reasoner;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.ElkAxiomProcessor;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParser;
import org.semanticweb.elk.owl.parsing.javacc.ParseException;
import org.semanticweb.elk.reasoner.classification.ClassTaxonomy;
import org.semanticweb.elk.reasoner.classification.ClassTaxonomyComputation;
import org.semanticweb.elk.reasoner.classification.ClassTaxonomyPrinter;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.OntologyIndexImpl;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturation;
import org.semanticweb.elk.reasoner.saturation.ObjectPropertySaturation;
import org.semanticweb.elk.util.logging.Statistics;

public class Reasoner {
	// executor used to run the jobs
	protected final ExecutorService executor;
	// number of workers for concurrent jobs
	protected final int workerNo;

	protected final OntologyIndex ontologyIndex;

	protected ClassTaxonomy classTaxonomy;

	// logger for events
	protected final static Logger LOGGER_ = Logger.getLogger(Reasoner.class);

	public Reasoner(ExecutorService executor, int workerNo) {
		this.executor = executor;
		this.workerNo = workerNo;
		this.ontologyIndex = new OntologyIndexImpl();
	}

	public OntologyIndex getOntologyIndex() {
		return ontologyIndex;
	}

	public Reasoner() {
		this(Executors.newCachedThreadPool(), 2 * Runtime.getRuntime()
				.availableProcessors());
	}

	public void loadOntologyFromStream(InputStream stream,
			ElkAxiomProcessor elkAxiomProcessor) throws ParseException,
			IOException {
		Statistics.logOperationStart("Loading", LOGGER_);

		Owl2FunctionalStyleParser parser = new Owl2FunctionalStyleParser(stream);
		parser.ontologyDocument(elkAxiomProcessor);
		stream.close();
		Statistics.logOperationFinish("Loading", LOGGER_);
		Statistics.logMemoryUsage(LOGGER_);
	}

	public void loadOntologyFromStream(InputStream stream)
			throws ParseException, IOException {
		loadOntologyFromStream(stream, ontologyIndex.getAxiomInserter());
	}

	public void loadOntologyFromFile(File file,
			ElkAxiomProcessor elkAxiomProcessor) throws ParseException,
			IOException {
		if (LOGGER_.isInfoEnabled()) {
			LOGGER_.info("Loading ontology from " + file);
		}
		loadOntologyFromStream(new FileInputStream(file), elkAxiomProcessor);
	}

	public void loadOntologyFromFile(String fileName,
			ElkAxiomProcessor elkAxiomProcessor) throws ParseException,
			IOException {
		loadOntologyFromFile(new File(fileName), elkAxiomProcessor);
	}

	public void loadOntologyFromFile(File file) throws ParseException,
			IOException {
		loadOntologyFromFile(file, ontologyIndex.getAxiomInserter());
	}

	public void loadOntologyFromFile(String fileName) throws ParseException,
			IOException {
		loadOntologyFromFile(new File(fileName));
	}

	public void loadOntologyFromString(String text,
			ElkAxiomProcessor elkAxiomProcessor) throws ParseException,
			IOException {
		if (LOGGER_.isInfoEnabled()) {
			LOGGER_.info("Loading ontology from string");
		}
		loadOntologyFromStream(new ByteArrayInputStream(text.getBytes()),
				elkAxiomProcessor);
	}

	public void loadOntologyFromString(String text) throws ParseException,
			IOException {
		loadOntologyFromString(text, ontologyIndex.getAxiomInserter());
	}

	public void addAxiom(ElkAxiom axiom) {
		ontologyIndex.getAxiomInserter().process(axiom);
	}

	public void removeAxiom(ElkAxiom axiom) {
		ontologyIndex.getAxiomDeleter().process(axiom);
	}

	public void classify(ProgressMonitor progressMonitor) {
		// number of indexed classes
		final int maxIndexedClassCount = ontologyIndex.getIndexedClassCount();
		// variable used in progress monitors
		int progress;

		// Saturation stage
		ObjectPropertySaturation objectPropertySaturation = new ObjectPropertySaturation(
				executor, workerNo, ontologyIndex);

		ClassExpressionSaturation classExpressionSaturation = new ClassExpressionSaturation(
				executor, workerNo, ontologyIndex);

		if (LOGGER_.isInfoEnabled())
			LOGGER_.info("Saturation using " + workerNo + " workers");
		Statistics.logOperationStart("Saturation", LOGGER_);
		progressMonitor.start("Saturation");

		try {
			objectPropertySaturation.compute();
		} catch (InterruptedException e1) {
		}
		

		progress = 0;
		classExpressionSaturation.start();
		for (IndexedClass ic : ontologyIndex.getIndexedClasses()) {
			try {
				classExpressionSaturation.submit(ic);
			} catch (InterruptedException e) {
			}
			progressMonitor.report(++progress, maxIndexedClassCount);
		}
		try {
			classExpressionSaturation.waitCompletion();
		} catch (InterruptedException e) {
		}

		progressMonitor.finish();
		Statistics.logOperationFinish("Saturation", LOGGER_);
		Statistics.logMemoryUsage(LOGGER_);

		// Transitive reduction stage
		if (LOGGER_.isInfoEnabled())
			LOGGER_.info("Transitive reduction using " + workerNo + " workers");
		Statistics.logOperationStart("Transitive reduction", LOGGER_);
		progressMonitor.start("Transitive reduction");

		ClassTaxonomyComputation classification = new ClassTaxonomyComputation(
				executor, workerNo);
		progress = 0;
		classification.start();

		for (IndexedClass ic : ontologyIndex.getIndexedClasses()) {
			try {
				classification.submit(ic);
			} catch (InterruptedException e) {
			}
			progressMonitor.report(++progress, maxIndexedClassCount);
		}

		try {
			classTaxonomy = classification.computeTaxonomy();
		} catch (InterruptedException e) {
		}

		progressMonitor.finish();
		Statistics.logOperationFinish("Transitive reduction", LOGGER_);
		Statistics.logMemoryUsage(LOGGER_);
	}

	public void classify() {
		classify(new DummyProgressMonitor());
	}

	public ClassTaxonomy getTaxonomy() {
		return classTaxonomy;
	}

	public void writeTaxonomyToFile(File file) throws IOException {
		if (LOGGER_.isInfoEnabled()) {
			LOGGER_.info("Writing taxonomy to " + file);
		}
		Statistics.logOperationStart("Writing taxonomy", LOGGER_);
		ClassTaxonomyPrinter.dumpClassTaxomomyToFile(this.getTaxonomy(),
				file.getPath(), true);
		Statistics.logOperationFinish("Writing taxonomy", LOGGER_);
	}

	public void shutdown() {
		executor.shutdownNow();
	}
}
