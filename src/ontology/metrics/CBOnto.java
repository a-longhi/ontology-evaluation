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
import java.util.ListIterator;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CBOnto (Coupling between Objects): Number of direct ancestors of all concepts
 * divided by the number of concepts not counting subconcepts of owl:Thing.
 * Formula: CBOOnto=∑Ci∑AncCj / (∑Ci - ∑CTk) where Ci is the i-th concept and
 * AncCj is its j-th direct ancestor and CTk is k-th concept with owl:Thing as
 * direct parent.
 * 
 * @author Andrej Tibaut
 * 
 */
public class CBOnto {
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public CBOnto(OntModel ontologyModel) {
		logger.info("*********************************************");
		logger.info("CBOnto - Coupling between Objects");

		// find all concepts in the ontology
		final List<OntClass> allConcepts = findAllConcepts(ontologyModel);

		// Find all subClass concepts (classes) in the graph
		int npar = getNumberOfAllAncestors(allConcepts);

		// Find all subClass concepts (classes) in the graph
		final List<OntClass> cwpt = findConpcetsWithOwlThingAsDirectAncestor(ontologyModel);
		for (OntClass ontClass : cwpt) {
			OntClass thing = ontClass.getSuperClass();
			String name = ((thing == null) || (thing.getLocalName() == null) ? "N/A" : thing.getLocalName());
			logger.info(" Class " + ontClass.getLocalName() + " has superClass " + name);
		}

		double cboonto = (double) npar / (allConcepts.size() - cwpt.size());

		logger.info("Number of all concepts: " + allConcepts.size());
		logger.info("Number of all ancestor concepts (with some ancestor): " + npar);
		logger.info("Number of concepts with owl:Thing as direct ancestor: " + cwpt.size());
		logger.info("CBOOnto: " + cboonto);
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
	 * The method finds all concepts with more than one parent in the ontology
	 * 
	 * @param iOntModel
	 * @return Hashmap of all concepts with more than one parent in the ontology
	 *         including corresponding number of parents
	 * @author Andrej Tibaut
	 */
	public static List<OntClass> findConpcetsWithOwlThingAsDirectAncestor(final OntModel iOntModel) {

		// list of concepts that have owl:Thing as their direct super-concept
		return iOntModel.listHierarchyRootClasses().toList();

	}

	/**
	 * The method finds all concepts with more than one parent in the ontology
	 * 
	 * @param iOntologyModel
	 * @return Hashmap of all concepts with more than one parent in the ontology
	 *         including corresponding number of parents
	 * @author Andrej Tibaut
	 */
	public int getNumberOfAllAncestors(final List<OntClass> iConcepts) {
		int np = 0;

		ListIterator<OntClass> liConcepts = iConcepts.listIterator();

		while (liConcepts.hasNext()) {
			OntClass ontClass = (OntClass) liConcepts.next();
			// get only direct parent concepts (=true)
			np += ontClass.listSuperClasses(true).toList().size();
		}
		return np;
	}

}