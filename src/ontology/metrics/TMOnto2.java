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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TMOnto2 (Tangledness): Mean number of direct ancestors of concepts with more
 * than 1 direct ancestor (multiple parentage). Formula:
 * TMOnto2=∑|SupCi(Cj)|/∑|Ck|; where SupCi is the i-th direct parent concept of
 * the j-th concept Cj with more than one direct parent and Ck is the k-th
 * concept with more than one direct parent in the ontology.
 * 
 * @author Andrej Tibaut
 * @version 10.12.2017 1.0
 */
public class TMOnto2 {
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public TMOnto2(OntModel ontologyModel) {
		logger.info("*********************************************");

		logger.info("TMOnto2 - Tangledness2");

		// Find all subClass concepts (classes) in the graph
		int ncm1p = 0;
		int npc = 0;
		final HashMap<OntClass, Integer> cm1p = findConceptsWithMoreThan1Parent(ontologyModel);
		ncm1p = cm1p.size();
		for (Iterator<Integer> itr = cm1p.values().iterator(); itr.hasNext();) {
			npc = npc + itr.next();
		}
		double tmonto2 = (double) npc / ncm1p;

		logger.info("Number of concepts with more than 1 parent: " + ncm1p);
		logger.info("Number of direct parents belonging to concepts with more than 1 parent: " + npc);
		logger.info("TMOnto2: " + tmonto2);
		logger.info("*********************************************");

	}

	/**
	 * The method finds all concepts with more than one direct parent in the
	 * ontology
	 * 
	 * @param iOntologyModel
	 * @return Hashmap of all concepts with more than one parent in the ontology
	 *         including corresponding number of parents
	 * @author Andrej Tibaut
	 */
	public static HashMap<OntClass, Integer> findConceptsWithMoreThan1Parent(final OntModel model) {
		int i = 0;
		final HashMap<OntClass, Integer> results = new HashMap<OntClass, Integer>();
		final ExtendedIterator<OntClass> concepts = model.listClasses();

		while (concepts.hasNext()) {
			OntClass ontClass = (OntClass) concepts.next();
			if (ontClass.getURI() != null) {
				// get only direct parent concepts (=true)
				final List<OntClass> parents = ontClass.listSuperClasses(true).toList();

				if (parents.size() > 1) {
					i++;
					for (OntClass p : parents) {
						Integer count = results.get(ontClass);
						if (count == null) {
							results.put(ontClass, 1);
							logger.debug(
									i + " Class " + ontClass.getLocalName() + " has superClass " + p.getLocalName());
						} else {
							results.put(ontClass, count + 1);
							logger.debug(
									i + " Class " + ontClass.getLocalName() + " has superClass " + p.getLocalName());
						}
					}
				}
			}
		}
		return results;
	}

}