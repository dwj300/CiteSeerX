package edu.psu.citeseerx.dao2;

import edu.psu.citeseerx.domain.Document;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.dao.DataAccessException;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;


/**
 * Created by Doug on 1/20/16.
 */
public class DocumentSolrImpl extends DocumentDAOImpl {
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.DocumentDAO#getDocument(java.lang.String, boolean)
     */

    private static String SOLR_URL = "http://localhost:8983/solr/papers";
    private SolrClient solr;


    public Document getDocument(String doi, boolean getSource)
            throws DataAccessException {
        // todo: use a bean
        if (solr == null) {
            solr = new HttpSolrClient(SOLR_URL);
        }

        SolrQuery query = new SolrQuery();
        query.setQuery("id:\"" + doi + "\"");

        QueryResponse resp = null;
        try {
            resp = solr.query(query);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if (resp.getResults().getNumFound() == 0) {
            return null;
        }
        SolrDocument solrDoc = resp.getResults().get(0);

        Document doc = new Document();

        for (String key : Document.getKeys()) {
            Object obj = solrDoc.getFieldValue(key);
            if (obj != null) {
                doc.setSource(key, obj.toString());
            }
        }

        return doc;

    }  //- getDocument
}
