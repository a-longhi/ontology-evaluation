/**
 * Copyright 2018 Andrej Tibaut
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.  
 */
package ontology.metrics;

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RROnto (Relationship Richness): Number of subconcepts (rdfs:subClassOf)
 * divided by the sum of subconcepts (rdfs:subClassOf) plus object and data
 * properties (owl:ObjectProperty, owl:DatatypeProperty) of the concepts.
 * Formula: RROnto=∑|SubCi| ∕ ∑(|SubCi| + ∑|ProCjk|); where SubCi is the i-th
 * subconcept and ProCjk is the j-th property of the k-th concept in the
 * ontology.
 * 
 * @author Andrej Tibaut
 * @version 10.12.2017 1.0
 */
public class RROnto {
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public RROnto(OntModel ontologyModel) {
		logger.info("*********************************************");
		logger.info("RROnto - Relationship Richness");

		// find all concepts in the ontology
		final List<OntClass> allConcepts = findAllConcepts(ontologyModel);
		int nc = allConcepts.size();

		// get number of all subconcepts in the ontology
		int nsc = getNumberOfSubconcepts(allConcepts);

		// get number of usage for object properties in the ontology
		int nop = getNumberOfObjectProperties(ontologyModel);

		// get number of usage for data properties in the ontology
		int ndp = getNumberOfDataProperties(ontologyModel);

		double rronto = (double) nsc / (double) (nsc + nop + ndp);

		logger.info("Number of concepts: " + nc);
		logger.info("Number of direct subconcepts: " + nsc);
		logger.info("Number of object properties: " + nop);
		logger.info("Number of data properties: " + ndp);
		logger.info("RROnto: " + rronto);
		logger.info("*********************************************");

	}

	/**
	 * The method finds all concepts in the ontology.
	 * 
	 * @param iOntologyModel
	 * @return List of concepts in the ontology
	 * @author Andrej Tibaut
	 */
	public static List<OntClass> findAllConcepts(final OntModel iOntologyModel) {

		return iOntologyModel.listNamedClasses().toList();
	}

	/**
	 * The method finds all direct subconcepts for the given list of concepts
	 * 
	 * @param iConcepts
	 *            list of all ontology concepts
	 * @return List of subconcepts
	 * @author Andrej Tibaut
	 */
	public int getNumberOfSubconcepts(final List<OntClass> iConcepts) {
		int nsc = 0; // number of subconcepts

		for (OntClass aConcept : iConcepts) {
			ExtendedIterator<OntClass> iter = aConcept.listSubClasses(true);
			if (iter.hasNext())
				nsc += iter.toList().size();
		}

		return nsc;
	}

	/**
	 * The method finds all object properties
	 * 
	 * @param iOntologyModel
	 *            ontology model
	 * @return List of subconcepts
	 * @author Andrej Tibaut
	 */
	public int getNumberOfObjectProperties(final OntModel iOntologyModel) {

		return iOntologyModel.listObjectProperties().toList().size();
	}

	/**
	 * The method finds all data properties
	 * 
	 * @param iOntologyModel
	 *            ontology model
	 * @return List of subconcepts
	 * @author Andrej Tibaut
	 */
	public int getNumberOfDataProperties(final OntModel iOntologyModel) {

		return iOntologyModel.listDatatypeProperties().toList().size();
	}

	@Deprecated
	public static int getNumSubclasses(final Resource klass) {
		return klass.getModel().listSubjectsWithProperty(RDFS.subClassOf, klass).toList().size();
	}

}