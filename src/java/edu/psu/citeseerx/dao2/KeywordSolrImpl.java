package edu.psu.citeseerx.dao2;

import edu.psu.citeseerx.domain.Keyword;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.springframework.dao.DataAccessException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Doug on 1/27/16.
 */
public class KeywordSolrImpl extends KeywordDAOImpl {

    private String solrUrl;
    private SolrClient solr;

    public void setSolrUrl(String solrUrl) {
        this.solrUrl = solrUrl + "keywords";
    }

    @Override
    public List<Keyword> getKeywords(String doi, boolean getSource)
            throws DataAccessException {

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
        List<Keyword> keywords = new ArrayList<Keyword>();

        for (SolrDocument doc : resp.getResults()) {
            Keyword keyword = new Keyword();
            keyword.setDatum(Keyword.DOI_KEY, doc.getFieldValue("id").toString());
            keyword.setDatum(Keyword.KEYWORD_KEY, doc.getFieldValue("keyword").toString());
            keywords.add(keyword);
        }
        return keywords;
    }
}
