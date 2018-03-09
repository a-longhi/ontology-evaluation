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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RFCOnto (Response for a concept): Number of direct properties
 * (owl:ObjectProperty, owl:DatatypeProperty) and direct parent concepts that
 * can be directly accessed from a concept divided by the number of all
 * concepts. Formula: RFCOnto=(∑Ci∑ProCj + ∑Ci∑ParCk) / ∑Ci where Ci is the i-th
 * concept and ProCj is its j-th property and ParCk is its k-th parent.
 * 
 * @author Andrej Tibaut
 * @version 10.12.2017 1.0
 */
public class RFCOnto {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	public RFCOnto(OntModel ontologyModel) {
		System.out.println("RFCOnto - Response for a concept");

		// find all concepts in the ontology
		final List<OntClass> allConcepts = findAllConcepts(ontologyModel);

		// get number of all properties in the ontology
		int npro = getNumberOfProperties(allConcepts);

		// Find all subClass concepts (classes) in the graph
		int npar = 0;
		final HashMap<OntClass, Integer> cwp = findAllParents(allConcepts);
		for (Iterator<Integer> itr = cwp.values().iterator(); itr.hasNext();) {
			npar = npar + itr.next();
		}

		double tmonto2 = (double) (npro + npar) / allConcepts.size();

		System.out.println("Number of all concepts: " + allConcepts.size());
		System.out.println("Number of all direct properties: " + npro);
		System.out.println("Number of concepts with some parent: " + cwp.size());
		System.out.println("Number of parent concepts belonging to concepts with some parent: " + npar);
		System.out.println("TMOnto2: " + tmonto2);
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

	/**
	 * The method finds all concepts with more than one parent in the ontology
	 * 
	 * @param iOntologyModel
	 * @return Hashmap of all concepts with more than one parent in the ontology
	 *         including corresponding number of parents
	 * @author Andrej Tibaut
	 */
	public static HashMap<OntClass, Integer> findAllParents(final List<OntClass> iConcepts) {
		int i = 0;
		final HashMap<OntClass, Integer> results = new HashMap<OntClass, Integer>();

		ListIterator<OntClass> liConcepts = iConcepts.listIterator();

		while (liConcepts.hasNext()) {
			OntClass ontClass = (OntClass) liConcepts.next();
			if (ontClass.getURI() != null) {
				// get only direct parent concepts (=true)
				final List<OntClass> parents = ontClass.listSuperClasses(true).toList();
				if (parents.size() > 0) {
					i++;
					for (OntClass p : parents) {
						Integer count = results.get(ontClass);
						if (count == null) {
							results.put(ontClass, 1);
							// System.out.println(
							// i + " Class " + ontClass.getLocalName() + " has superClass " +
							// p.getLocalName());
						} else {
							results.put(ontClass, count + 1);
							// System.out.println(
							// i + " Class " + ontClass.getLocalName() + " has superClass " +
							// p.getLocalName());
						}
					}
				}
			}
		}
		return results;
	}

}