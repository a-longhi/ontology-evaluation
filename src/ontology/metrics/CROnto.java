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

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CROnto (Concept Richness): Mean number of individuals per class Formula:
 * CROnto=∑| ICi| / ∑|Ci |; where ICi, is the set of individuals of the Ci.
 * 
 * @author Andrej Tibaut
 *
 */
public class CROnto {
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public CROnto(OntModel ontologyModel) {
		logger.info("*********************************************");
		logger.info("CROnto - Class Richness");

		double inrOnto = getCROnto(ontologyModel);
		logger.info("CROnto: " + inrOnto);
		logger.info("*********************************************");

	}

	public static double getCROnto(final OntModel iOntologyModel) {
		double inrOnto = 0;
		double nc = 0; // number of concepts
		double nin = 0; // number of direct indivuduals

		final ExtendedIterator<OntClass> allConcepts = iOntologyModel.listClasses();
		while (allConcepts.hasNext()) {
			nc++;
			OntClass aConcept = (OntClass) allConcepts.next();
			if (aConcept.getURI() != null) {
				ExtendedIterator<? extends OntResource> iter = aConcept.listInstances(true);
				if (iter.hasNext()) {
					nin += iter.toList().size();
				}
			}
		}
		logger.info("Number of all concepts: " + nc);
		logger.info("Number of all direct individuals: " + nin);

		inrOnto = nin / nc;

		return inrOnto;
	}

}