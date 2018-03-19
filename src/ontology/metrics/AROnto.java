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
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.Restriction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AROnto - Attribute richness. Number of property restrictions
 * (owl:Restrictions (owl:someValuesFrom, owl:allValuesFrom, owl:hasValue,
 * owl:minCardinality, owl:maxCardinality)) nested inside of rdfs:subClassOf per
 * concept in the ontology. Formula: AROnto=∑|RestCi| / ∑|Cj|; where RestCi is
 * the i-th restriction and Cj is the j-th concept in the ontology. Reference:
 * http://miuras.inf.um.es/oquarewiki/
 * 
 * @author Andrej Tibaut
 */
public class AROnto {
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public AROnto(OntModel ontologyModel) {
		logger.info("*********************************************");
		logger.info("AROnto - Attribute richness");

		// find all concepts in the ontology
		final List<OntClass> allConcepts = findAllConcepts(ontologyModel);
		int nc = allConcepts.size();

		// Find all restrictions for the list of concepts (classes) from the ontology
		int ncr = countAllRestrictionsForTheConcepts(allConcepts);

		double AROnto = (double) ncr / nc;

		logger.info("Number of all concepts: " + nc);
		logger.info("Number of restrictions for the concepts: " + ncr);
		logger.info("AROnto: " + AROnto);
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
	 * The method collects all owl:Restriction (owl:someValuesFrom,
	 * owl:allValuesFrom, owl:hasValue, owl:minCardinality, owl:maxCardinality) for
	 * the given list of classes. Some ideas are from
	 * https://stackoverflow.com/questions/7779927/get-owl-restrictions-on-classes-using-jena
	 * 
	 * @param iConcepts
	 *            list of unique
	 * @return Number of all restrictions for the given list of classes
	 * @author Andrej Tibaut
	 */
	public static int countAllRestrictionsForTheConcepts(final List<OntClass> iConcepts) {
		int ncr = 0;
		ListIterator<OntClass> liConcepts = iConcepts.listIterator();
		while (liConcepts.hasNext()) {
			OntClass ontConcept = (OntClass) liConcepts.next();
			// restrictions are enclosed inside rdfs:subClassOf so we are searching for
			// owl:Restriction inside them
			for (Iterator<OntClass> supConcepts = ontConcept.listSuperClasses(); supConcepts.hasNext();) {
				OntClass supConcept = supConcepts.next();
				if (supConcept.isRestriction()) {
					Restriction restriction = supConcept.asRestriction();
					if (restriction.isSomeValuesFromRestriction() || restriction.isAllValuesFromRestriction()
							|| restriction.isHasValueRestriction() || restriction.isMinCardinalityRestriction()
							|| restriction.isMaxCardinalityRestriction()) {
						ncr++;
					}
				}
			}
		}
		return ncr;
	}

}