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
 * AGOnto (Aggregability): The metric relates to the aggregation size of the
 * sets of the external ontologies. It calculates the percentage of the used
 * resources for the maximal and minimal set according to the total size of all
 * external sets in the maintained ontology. The maximal percentage is then
 * divided by the size of the minimal percentage to get the multiplier. Formula:
 * AGOnto=Max(ENSi/NE, …, ENSn/NE) ∕ Min(ENSi/NE, …, ENSn/NE)
 * 
 * where ENSi is the size of the i-th external namespace (i between 1 and number
 * of external namespaces), NE is total sum of used external resources in the
 * maintained ontology.
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

		String baseNS = ontologyModel.getNsPrefixURI("");
		logger.info("Base namespace: " + baseNS);

		long ner = 0;
		long nens = 0;
		double agonto = 0;

		Map<String, Long> ens = new HashMap<String, Long>();

		Map<String, String> nss = ontologyModel.getNsPrefixMap();
		// iterate used namespaces
		for (Map.Entry<String, String> entry : nss.entrySet()) {
			// external resouces
			if (!isDefaultResource(entry.getValue()) && !isBaseResource(entry.getValue(), baseNS)) {
				logger.info("External namespace: " + entry.getKey() + "=" + entry.getValue());
				long er = getNumOfNSResources(ontologyModel, entry.getValue());
				// it counts only if there are some external resources used
				if (er > 0) {
					ner += er;
					nens++;
					ens.put(entry.getKey(), er);
					logger.info("entry: " + entry.getKey() + " contributes n: " + er);
				}
			}

		}

		logger.info("Number of all used external namespaces in axioms: " + nens);
		logger.info("Number of all used external resources in axioms: " + ner);

		if (nens > 0) {
			String mink = ens.entrySet().stream().min(Map.Entry.comparingByValue()).get().getKey();
			String maxk = ens.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();
			long minv = ens.get(mink);
			long maxv = ens.get(maxk);
			double minp = (double) minv / ner;
			double maxp = (double) maxv / ner;

			logger.info("Max external resource conceptualization: " + maxk + " contributes resources: " + maxp);
			logger.info("Min external resource conceptualization: " + mink + " contributes resources: " + minp);

			agonto = maxp / minp;

		}
		logger.info("AGOnto: " + agonto);
		logger.info("*********************************************");
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
			// logger.debug("eStatement: " + s);
			if (s.getSubject().isURIResource() && s.getSubject().getNameSpace().startsWith(iNS)) {
				// logger.debug(">>eSub: " + s.getSubject().getNameSpace());
				n++;
			}
			if (s.getPredicate().isURIResource() && s.getPredicate().getNameSpace().startsWith(iNS)) {
				// logger.debug(">>ePre: " + s.getPredicate().getNameSpace());
				n++;
			}
			if (s.getObject() instanceof Resource) {
				if (s.getObject().isURIResource()) {
					if (s.getObject().asResource().getNameSpace().startsWith(iNS)) {
						// logger.debug(">>eObj: " + s.getObject().asResource().getNameSpace());
						n++;
					}
				}
			}

		}

		return n;

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
	public boolean isBaseResource(String iResourceURI, String iBaseNS) {

		return (iResourceURI.startsWith(iBaseNS));

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