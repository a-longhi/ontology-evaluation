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
import org.apache.jena.util.iterator.ExtendedIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INROnto (Relationships per concept): Mean number of subconcepts
 * (rdfs:subClassOf) per concept. Formula: INROnto=∑∑| SubCij| /∑|Ci|; where
 * SubCij is the j-th subconcept of the concept Ci in the ontology. Reference:
 * http://miuras.inf.um.es/oquarewiki/
 * 
 * @author Andrej Tibaut
 * @version 10.12.2017 1.0
 */
public class INROnto {
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public INROnto(OntModel ontologyModel) {
		logger.info("*********************************************");
		logger.info("INROnto - Relationships per concept");

		// find all concepts in the ontology
		final List<OntClass> allConcepts = findAllConcepts(ontologyModel);
		int nc = allConcepts.size();

		// get number of all subconcepts in the ontology
		int nsc = getNumberOfSubconcepts(allConcepts);

		double inronto = (double) nsc / nc;

		logger.info("Number of all concepts: " + nc);
		logger.info("Number of all subconcepts: " + nsc);
		logger.info("INROnto: " + inronto);
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

	public int getNumberOfSubconcepts(final List<OntClass> iConcepts) {
		int nsc = 0; // number of subconcepts

		for (OntClass aConcept : iConcepts) {
			ExtendedIterator<OntClass> iter = aConcept.listSubClasses(true);
			if (iter.hasNext())
				nsc += iter.toList().size();
		}

		return nsc;
	}

}