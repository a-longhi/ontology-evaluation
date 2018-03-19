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
 * NOCOnto (Number of Children Concepts): Number of the direct subconcepts
 * divided by the number of concepts minus the number of leaf concepts. Formula:
 * NOCOnto=∑Ci∑SubCj / (∑Ci - ∑LeafCk) where Ci is the i-th concept and SubCj is
 * its j-th direct subclass and LeafCk is k-th leaf concept.
 * 
 * 
 * @author Andrej Tibaut
 * @version 10.12.2017 1.0
 */
public class NOCOnto {
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public NOCOnto(OntModel ontologyModel) {
		logger.info("*********************************************");

		logger.info("NOCOnto (Number of Children Concepts");

		// find all concepts in the ontology
		final List<OntClass> allConcepts = findAllConcepts(ontologyModel);
		int nc = allConcepts.size();

		// get number of all direct subconcepts in the ontology
		int nsc = getNumberOfSubconcepts(allConcepts);

		// get number of all leaf concepts in the ontology
		int nlc = getNumberOfLeafConcepts(allConcepts);

		double noconto = (double) nsc / (allConcepts.size() - nlc);

		logger.info("Number of all concepts: " + nc);
		logger.info("Number of all direct subconcepts: " + nsc);
		logger.info("Number of all leaf concepts: " + nlc);
		logger.info("NOCOnto: " + noconto);
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
	 * The method finds all direct properties for the given list of concepts
	 * 
	 * @param iConcepts
	 *            list of all ontology concepts
	 * @return List of subconcepts
	 * @author Andrej Tibaut
	 */
	public int getNumberOfLeafConcepts(final List<OntClass> iConcepts) {
		int nlc = 0; // number of leaf concepts

		for (OntClass aConcept : iConcepts) {

			if (aConcept.listSubClasses(true).toList().size() == 0)
				nlc++;
		}

		return nlc;
	}

	public static int getNumSubclasses(final Resource klass) {
		return klass.getModel().listSubjectsWithProperty(RDFS.subClassOf, klass).toList().size();
	}

}