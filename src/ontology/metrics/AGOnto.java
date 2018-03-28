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
import java.util.Map;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Andrej Tibaut
 *
 */
public class AGOnto {
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private static String NS_XML = "http://www.w3.org/XML/1998/namespace";
	private static String NS_OWL = "http://www.w3.org/2002/07/owl#";
	private static String NS_OWLX = "http://www.w3.org/2003/05/owl-xml";
	private static String NS_XSD = "http://www.w3.org/2001/XMLSchema#";
	private static String NS_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	private static String NS_RDFS = "http://www.w3.org/2000/01/rdf-schema#";

	public AGOnto(OntModel ontologyModel) {
		logger.info("*********************************************");
		logger.info("AGOnto - Aggregability");

		String baseURI = ontologyModel.getNsPrefixURI("");
		logger.debug("Base namespace: " + baseURI);

		long rie = getNumOfInternaAndExternalResources(ontologyModel);
		double re = 0;

		Map<String, String> nss = ontologyModel.getNsPrefixMap();
		for (Map.Entry<String, String> entry : nss.entrySet()) {
			logger.debug("Namespace: " + entry.getKey() + "+" + NS_OWL + entry.getValue());
			if (!isDefaultResource(entry.getValue()) && !entry.getValue().startsWith(baseURI)) {
				long n = getNumOfNSResources(ontologyModel, entry.getValue());
				logger.debug("n = " + n);
//				logger.debug("ne% = " + n / rie);
				re = re + (double) n / (double) rie;
				logger.debug("N = " + re);
			}

		}

		logger.info("Number of all internal (base) and external resources in axioms: " + rie);
		logger.info("Number of all external resources in axioms: " + re);

		double agonto = (double) rie;
		logger.info("AGOnto: " + agonto);
		logger.info("*********************************************");
	}

	/**
	 * Finds all external resources not counting default (must-have) ontology
	 * namespaces
	 * 
	 * @param iOntologyModel
	 *            ontology model
	 * @return number of resources
	 * @author Andrej Tibaut
	 */
	public long getNumOfInternaAndExternalResources(final OntModel iOntologyModel) {
		long n = 0;

		StmtIterator iter = iOntologyModel.listStatements((Resource) null, (Property) null, (RDFNode) null);
		while (iter.hasNext()) {
			Statement s = iter.nextStatement();
			logger.debug("eStatement: " + s);
			if (s.getSubject().isURIResource()) {
				if (!isDefaultResource(s.getSubject().getNameSpace())) {
					logger.debug(">>Sub: " + s.getSubject().getNameSpace());
					n++;
				}
			}
			if (s.getPredicate().isURIResource() && !isDefaultResource(s.getPredicate().getNameSpace())) {
				logger.debug(">>Pre: " + s.getPredicate().getNameSpace());
				n++;
			}
			if (s.getObject() instanceof Resource) {
				if (s.getObject().isURIResource()) {
					if (!isDefaultResource(s.getObject().asResource().getNameSpace())) {
						logger.debug(">>Obj: " + s.getObject().asResource().getNameSpace());
						n++;
					}
				}
			}

		}

		return n;

	}

	/**
	 * Finds all external resources not counting default (must-have) ontology
	 * namespaces
	 * 
	 * @param iOntologyModel
	 *            ontology model
	 * @param iNS
	 *            a namespace
	 * @return number of resources
	 * @author Andrej Tibaut
	 */
	public long getNumOfNSResources(final OntModel iOntologyModel, String iNS) {
		long n = 0;

		StmtIterator iter = iOntologyModel.listStatements((Resource) null, (Property) null, (RDFNode) null);
		while (iter.hasNext()) {
			Statement s = iter.nextStatement();
			logger.debug("eStatement: " + s);
			if (s.getSubject().isURIResource()) {
				if (s.getSubject().getNameSpace().startsWith(iNS)) {
					logger.debug(">>Sub: " + s.getSubject().getNameSpace());
					n++;
				}
			}
			if (s.getPredicate().isURIResource() && s.getPredicate().getNameSpace().startsWith(iNS)) {
				logger.debug(">>Pre: " + s.getPredicate().getNameSpace());
				n++;
			}
			if (s.getObject() instanceof Resource) {
				if (s.getObject().isURIResource()) {
					if (s.getObject().asResource().getNameSpace().startsWith(iNS)) {
						logger.debug(">>Obj: " + s.getObject().asResource().getNameSpace());
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
	private boolean isDefaultResource(String iResourceURI) {

		return (iResourceURI.startsWith(NS_XML) || iResourceURI.startsWith(NS_OWLX) || iResourceURI.startsWith(NS_OWL)
				|| iResourceURI.startsWith(NS_XSD) || iResourceURI.startsWith(NS_RDF)
				|| iResourceURI.startsWith(NS_RDFS));

	}

}