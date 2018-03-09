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
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DITOnto (Depth of subsumption hierarchy): Length (number of links between
 * concepts) of the longest path from Thing (owl:Thing) to a leaf concept.
 * Formula: DITOnto=Max(PathLength(CThing,LeafCi)), where PathLength is the
 * function that calculates length between the i-th leaf concept LeafCi and the
 * CThing (owl:Thing). The maximum path length (Max) is then selected as result.
 * Inspired by:
 * https://stackoverflow.com/questions/40626898/getting-all-possible-paths-in-a-tree-structure
 * 
 * 
 * @author Andrej Tibaut
 * @version 10.12.2017 1.0
 */
public class DITOnto {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	public DITOnto(OntModel ontologyModel) {
		System.out.println("DITOnto - Depth of subsumption hierarchy");

		int ditOnto = findLongestPathFromThing2Leaf(ontologyModel);

		System.out.println("DITOnto: " + ditOnto);
		System.out.println("*********************************************");

	}

	/**
	 * The method finds all concepts in the ontology.
	 * 
	 * @param iOntologyModel
	 * @return List of concepts in the ontology
	 * @author Andrej Tibaut
	 */
	public static int findLongestPathFromThing2Leaf(final OntModel iOntModel) {
		List<List<OntClass>> allPathsThing2Leaf = new ArrayList<>();

		ExtendedIterator<OntClass> rootConcepts = iOntModel.listHierarchyRootClasses();
		while (rootConcepts.hasNext()) {
			OntClass aConcept = (OntClass) rootConcepts.next();
			OntClass theThing = aConcept.getSuperClass();

			// in case the root concept doesn't have rdfs:subClassOf
			// rdf:resource="http://www.w3.org/2002/07/owl#Thing" (ontology pitfall)
			String name = ((theThing == null) || (theThing.getLocalName() == null) ? "N/A" : theThing.getLocalName());
			allPathsThing2Leaf = searchDepthFirstAllThing2LeafPaths(aConcept, name);
		}
		// System.out.println("All paths Thing to leaf: " + allPathsThing2Leaf);
		List<OntClass> maxLengthList = allPathsThing2Leaf.stream().max(Comparator.comparingInt(List::size)).get();
		// System.out.println("Maximal length path Thing to leaf: " + maxLengthList);

		return maxLengthList.size();
	}

	public static List<List<OntClass>> searchDepthFirstAllThing2LeafPaths(OntClass iConcept, String s) {
		List<List<OntClass>> retLists = new ArrayList<>();

		if (iConcept.listSubClasses(true).toList().size() == 0) {
			// System.out.println(s + " --> " + iConcept.getLocalName());
			List<OntClass> leafList = new LinkedList<>();
			leafList.add(iConcept);
			retLists.add(leafList);
		} else {
			ExtendedIterator<OntClass> isubConcepts = iConcept.listSubClasses(true);
			while (isubConcepts.hasNext()) {
				OntClass subConcept = isubConcepts.next();
				List<List<OntClass>> nodeLists = searchDepthFirstAllThing2LeafPaths(subConcept,
						s + " --> " + iConcept.getLocalName());
				for (List<OntClass> nodeList : nodeLists) {
					nodeList.add(0, iConcept);
					retLists.add(nodeList);
				}
			}

		}
		return retLists;
	}

}