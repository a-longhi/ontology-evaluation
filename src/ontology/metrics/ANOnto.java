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
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ANOnto - Annotation Richness. Mean number of annotation properties
 * (rdfs:comment, rdfs:label) per concept (owl:Class). Formula: ANOnto=∑|ApCi| /
 * ∑|Cj|; where ApCi is the i-th annotation and Cj is the j-th concept in the
 * ontology. Reference: http://miuras.inf.um.es/oquarewiki/
 * 
 * @author Andrej Tibaut
 */
public class ANOnto {
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public ANOnto(OntModel ontologyModel) {
		logger.info("*********************************************");
		logger.info("ANOnto - Annotation Richness");

		// find all concepts in the ontology
		final List<OntClass> allConcepts = findAllConcepts(ontologyModel);
		int nc = allConcepts.size();

		// Find all annotations for the list of concepts (classes) from the ontology
		final List<RDFNode> classAnnotations = findAllAnnotations(allConcepts);
		int nca = classAnnotations.size();

		double ANOnto = (double) nca / nc;

		logger.info("Number of all concepts: " + nc);
		logger.info("Number of annotations of all concepts: " + nca);
		logger.info("ANOnto: " + ANOnto);
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
	 * Collects all annotation properties as defined in OWL (owl:versionInfo,
	 * rdfs:label rdfs:comment rdfs:seeAlso rdfs:isDefinedBy). Reference:
	 * https://www.w3.org/TR/owl-ref/ (see owl:AnnotationProperty)
	 * 
	 * @param iConcepts
	 * @return list of all OWL annotations
	 * @author Andrej Tibaut
	 */
	public static List<RDFNode> findAllAnnotations(final List<OntClass> iConcepts) {
		final List<RDFNode> results = new ArrayList<RDFNode>();

		ListIterator<OntClass> liConcepts = iConcepts.listIterator();


		while (liConcepts.hasNext()) {
			OntClass ontClass = (OntClass) liConcepts.next();
//			ontClass.list
			if (ontClass.getURI() != null) {

				Resource c = ontClass.getOntModel().createResource(ontClass.getNameSpace() + ontClass.getLocalName());
				StmtIterator iter = ontClass.getOntModel().listStatements(c, RDFS.comment, (RDFNode) null);
				while (iter.hasNext()) {
					Statement stmt = iter.nextStatement();
					RDFNode resource = (RDFNode) stmt.getObject().asLiteral();
					results.add(resource);
				}

				c = ontClass.getOntModel().createResource(ontClass.getNameSpace() + ontClass.getLocalName());
				iter = ontClass.getOntModel().listStatements(c, RDFS.label, (RDFNode) null);
				while (iter.hasNext()) {
					Statement stmt = iter.nextStatement();
					RDFNode resource = (RDFNode) stmt.getObject().asLiteral();
					results.add(resource);
				}

				c = ontClass.getOntModel().createResource(ontClass.getNameSpace() + ontClass.getLocalName());
				iter = ontClass.getOntModel().listStatements(c, RDFS.seeAlso, (RDFNode) null);
				while (iter.hasNext()) {
					Statement stmt = iter.nextStatement();
					RDFNode resource = (RDFNode) stmt.getObject().asLiteral();
					results.add(resource);
				}

				c = ontClass.getOntModel().createResource(ontClass.getNameSpace() + ontClass.getLocalName());
				iter = ontClass.getOntModel().listStatements(c, RDFS.isDefinedBy, (RDFNode) null);
				while (iter.hasNext()) {
					Statement stmt = iter.nextStatement();
					RDFNode resource = (RDFNode) stmt.getObject().asLiteral();
					results.add(resource);
				}

				c = ontClass.getOntModel().createResource(ontClass.getNameSpace() + ontClass.getLocalName());
				iter = ontClass.getOntModel().listStatements(c, OWL.versionInfo, (RDFNode) null);
				while (iter.hasNext()) {
					Statement stmt = iter.nextStatement();
					RDFNode resource = (RDFNode) stmt.getObject().asLiteral();
					results.add(resource);
				}

			}
		}
		return results;
	}

}