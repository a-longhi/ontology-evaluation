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

import java.util.List;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NOMOnto (Number of properties): Mean number of properties
 * (owl:ObjectProperty, owl:DatatypeProperty) per concept. Number of properties
 * assigned to concepts and divided by number of concepts. Formula:
 * NOMOnto=∑Ci∑ProCj / ∑Ci; where Ci is the i-th concept and ProCj is its j-th
 * property.
 * 
 * @author Andrej Tibaut
 * @version 10.12.2017 1.0
 */
public class NOMOnto {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	public NOMOnto(OntModel ontologyModel) {

		// find all concepts in the ontology
		final List<OntClass> allConcepts = findAllConcepts(ontologyModel);
		int nc = allConcepts.size();

		// get number of all properties in the ontology
		int np = getNumberOfProperties(allConcepts);

		double nomonto = (double) np / nc;

		System.out.println("Number of all concepts: " + nc);
		System.out.println("Number of all direct properties: " + np);
		System.out.println("NOMOnto: " + nomonto);
		System.out.println("*********************************************");

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

	/**
	 * The method finds all direct properties for the given list of concepts
	 * 
	 * @param iConcepts
	 *            list of all ontology concepts
	 * @return List of subconcepts
	 * @author Andrej Tibaut
	 */
	public int getNumberOfProperties(final List<OntClass> iConcepts) {
		int np = 0; // number of subconcepts

		for (OntClass aConcept : iConcepts) {
			ExtendedIterator<OntProperty> iter = aConcept.listDeclaredProperties(true);
			if (iter.hasNext())
				np += iter.toList().size();
		}

		return np;
	}

}