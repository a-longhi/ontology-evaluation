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

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CPOnto (Composability): usage of resources (concepts, properties) from
 * external namespaces divided by the usage of all resources (native and
 * external). The metric describes the composure of the ontology on the scale
 * from a monolithic self-sufficient ontology to a highly composed and
 * interconnected ontology. Formula: CPOnto=(∑CEi + ∑ProEj) ∕ (∑Ck + ∑ProCl +
 * ∑CEi + ∑ProEj), where CEi is the i-th external concept and ProEj is the j-th
 * external property, Ck is the k-th concept and ProcCl is the l-th usage of
 * property in the ontology.
 * 
 * 
 * @author Andrej Tibaut
 * 
 */
public class CPOnto {
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private static String NS_XML = "http://www.w3.org/XML/1998/namespace";
	private static String NS_OWL = "http://www.w3.org/2002/07/owl#";
	private static String NS_OWLX = "http://www.w3.org/2003/05/owl-xml";
	private static String NS_XSD = "http://www.w3.org/2001/XMLSchema#";
	private static String NS_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	private static String NS_RDFS = "http://www.w3.org/2000/01/rdf-schema#";

	public CPOnto(OntModel ontologyModel) {
		logger.info("*********************************************");
		logger.info("PROnto - Properties Richness");

		String baseURI = ontologyModel.getNsPrefixURI("");
		logger.debug("Base namespace: " + baseURI);

		long ri = getInternalResources(ontologyModel, baseURI);
		long re = getExternalResources(ontologyModel, baseURI);

		logger.info("Number of all internal (base) resources in axioms: " + ri);
		logger.info("Number of all external resources in axioms: " + re);

		double cponto = (double) re / ri;
		logger.info("CPOnto: " + cponto);
		logger.info("*********************************************");
	}

	/**
	 * Finds all internal resources
	 * 
	 * @param iOntologyModel
	 *            ontology model
	 * @param iBaseURI
	 *            base URI
	 * @return
	 * @author Andrej Tibaut
	 */
	public long getInternalResources(final OntModel iOntologyModel, String iBaseURI) {
		long n = 0;

		StmtIterator iter = iOntologyModel.listStatements((Resource) null, (Property) null, (RDFNode) null);
		while (iter.hasNext()) {
			Statement s = iter.nextStatement();
			logger.debug("iStatement: " + s);
			if (s.getSubject().isURIResource()) {
				if (isInternalResource(s.getSubject().getNameSpace(), iBaseURI)) {
					logger.debug(">>iSub: " + s.getSubject().getNameSpace());
					n++;
				}
			}
			if (s.getPredicate().isURIResource() && isInternalResource(s.getPredicate().getNameSpace(), iBaseURI)) {
				logger.debug(">>iPre: " + s.getPredicate().getNameSpace());
				n++;
			}
			if (s.getObject() instanceof Resource) {
				if (s.getObject().isURIResource()) {
					if (isInternalResource(s.getObject().asResource().getNameSpace(), iBaseURI)) {
						logger.debug(">>iObj: " + s.getObject().asResource().getNameSpace());
						n++;
					}
				}
			}

		}

		return n;

	}

	/**
	 * Finds all external resources
	 * 
	 * @param iOntologyModel
	 *            ontology model
	 * @param iBaseURI
	 *            base URI
	 * @return
	 * @author Andrej Tibaut
	 */
	public long getExternalResources(final OntModel iOntologyModel, String iBaseURI) {
		long n = 0;

		StmtIterator iter = iOntologyModel.listStatements((Resource) null, (Property) null, (RDFNode) null);
		while (iter.hasNext()) {
			Statement s = iter.nextStatement();
			logger.debug("eStatement: " + s);
			if (s.getSubject().isURIResource()) {
				if (isExternalResource(s.getSubject().getNameSpace(), iBaseURI)) {
					logger.debug(">>eSub: " + s.getSubject().getNameSpace());
					n++;
				}
			}
			if (s.getPredicate().isURIResource() && isExternalResource(s.getPredicate().getNameSpace(), iBaseURI)) {
				logger.debug(">>ePre: " + s.getPredicate().getNameSpace());
				n++;
			}
			if (s.getObject() instanceof Resource) {
				if (s.getObject().isURIResource()) {
					if (isExternalResource(s.getObject().asResource().getNameSpace(), iBaseURI)) {
						logger.debug(">>eObj: " + s.getObject().asResource().getNameSpace());
						n++;
					}
				}
			}

		}

		return n;

	}

	/**
	 * The method checks if the resource is external
	 * 
	 * @param iResourceURI
	 *            resource URI
	 * @param iBaseNS
	 *            base URI
	 * @return true or false
	 * @author Andrej Tibaut
	 */
	public boolean isExternalResource(String iResourceURI, String iBaseNS) {

		return (!iResourceURI.startsWith(NS_XML) && !iResourceURI.startsWith(NS_OWLX)
				&& !iResourceURI.startsWith(NS_OWL) && !iResourceURI.startsWith(NS_XSD)
				&& !iResourceURI.startsWith(NS_RDF) && !iResourceURI.startsWith(NS_RDFS)
				&& !iResourceURI.startsWith(iBaseNS));

	}

	/**
	 * The method checks if the resource is internal
	 * 
	 * @param iResourceURI
	 *            resource URI
	 * @param iBaseNS
	 *            base URI
	 * @return true or false
	 * @author Andrej Tibaut
	 */
	public boolean isInternalResource(String iResourceURI, String iBaseNS) {

		return (iResourceURI.startsWith(iBaseNS));

	}

}