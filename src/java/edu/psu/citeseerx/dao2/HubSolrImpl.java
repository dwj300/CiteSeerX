package edu.psu.citeseerx.dao2;

import edu.psu.citeseerx.domain.Author;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.springframework.context.ApplicationContextException;
import org.springframework.dao.DataAccessException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Doug on 1/27/16.
 */
public class HubSolrImpl extends HubDAOImpl {

    private static String SOLR_URL = "http://localhost:8983/solr/urls";
    private SolrClient solr;

    @Override
    public List<String> getUrls(String doi) throws DataAccessException {
        if (solr == null) {
            solr = new HttpSolrClient(SOLR_URL);
        }

        SolrQuery query = new SolrQuery();
        query.setQuery("paperid:\"" + doi + "\"");
        QueryResponse resp = null;
        try {
            resp = solr.query(query);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        List<String> urls = new ArrayList<String>();

        for (SolrDocument doc : resp.getResults()) {
            urls.add(doc.getFieldValue("url").toString());
        }

        return urls;
    }
}
