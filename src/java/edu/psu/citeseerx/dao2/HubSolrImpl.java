package edu.psu.citeseerx.dao2;

import edu.psu.citeseerx.domain.Author;
import edu.psu.citeseerx.domain.Hub;
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

    private String solrUrl;
    private SolrClient solr;

    public void setSolrUrl(String solrUrl) {
        this.solrUrl = solrUrl + "urls";
    }

    @Override
    public List<String> getUrls(String doi) throws DataAccessException {
        if (solr == null) {
            solr = new HttpSolrClient(solrUrl);
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

    @Override
    public List<Hub> getHubs(String doi) throws DataAccessException {
        if (solr == null) {
            solr = new HttpSolrClient(solrUrl);
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
        List<Hub> hubs = new ArrayList<Hub>();

        for (SolrDocument doc : resp.getResults()) {
            Hub hub = new Hub();
            hub.setUrl(doc.getFieldValue("url").toString());
        }

        return hubs;
    }  //- getHubs
}
