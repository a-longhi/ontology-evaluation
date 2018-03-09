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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NACOnto (Number of Ancestor Concepts): Arithmetic mean number of direct
 * ancestor concepts per leaf concept. Formula: NACOnto=∑AncLeafCi / ∑LeafCj
 * where AncLeafCi is the i-th direct ancestor of a leaf and LeafCj is j-th leaf
 * concept.
 * 
 * 
 * @author Andrej Tibaut
 * @version 10.12.2017 1.0
 */
public class NACOnto {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	public NACOnto(OntModel ontologyModel) {
		System.out.println("NACOnto - Number of Ancestor Concepts");

		// find all concepts in the ontology
		final List<OntClass> leafConcepts = findLeafConcepts(ontologyModel);

		// get number of direct ancestors for leafs
		int nalc = getNumberOfAllAncestors(leafConcepts);

		double naconto = (double) nalc / leafConcepts.size();

		System.out.println("Number of all leaf concepts: " + leafConcepts.size());
		System.out.println("Number of all direct ancestors of leafs: " + nalc);
		System.out.println("NACOnto: " + naconto);
		System.out.println("*********************************************");

	}

	/**
	 * The method finds all concepts in the ontology.
	 * 
	 * @param iOntologyModel
	 * @return List of concepts in the ontology
	 * @author Andrej Tibaut
	 */
	public static List<OntClass> findLeafConcepts(final OntModel model) {
		final List<OntClass> results = new ArrayList<OntClass>();

		final ExtendedIterator<OntClass> concepts = model.listClasses();
		while (concepts.hasNext()) {
			OntClass aConcept = (OntClass) concepts.next();
			if (aConcept.listSubClasses(true).toList().size() == 0) {
				// System.out.println("LEAF: " + aConcept.getLocalName());
				results.add(aConcept);
			}
		}

		return results;
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
		int na = 0;

		ListIterator<OntClass> liConcepts = iConcepts.listIterator();
		while (liConcepts.hasNext()) {
			OntClass ontClass = (OntClass) liConcepts.next();
			// get only direct parent concepts (=true)
			na += ontClass.listSuperClasses(true).toList().size();
		}
		return na;
	}

}