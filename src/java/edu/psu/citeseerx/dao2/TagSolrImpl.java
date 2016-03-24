package edu.psu.citeseerx.dao2;

import edu.psu.citeseerx.domain.Keyword;
import edu.psu.citeseerx.domain.Tag;
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
public class TagSolrImpl extends TagDAOImpl {

    private static String solrUrl;
    private SolrClient solr;

    public void setSolrUrl(String solrUrl) {
        this.solrUrl = solrUrl + "tags";
    }

    public List<Tag> getTags(String doi) throws DataAccessException {
        if (solr == null) {
            solr = new HttpSolrClient(solrUrl);
        }

        SolrQuery query = new SolrQuery();
        query.setQuery("paperid:\"" + doi + "\""); // TODO: deal with sort!
        QueryResponse resp = null;
        try {
            resp = solr.query(query);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        List<Tag> tags = new ArrayList<Tag>();

        for (SolrDocument doc : resp.getResults()) {
            Tag tag = new Tag();
            tag.setTag(doc.getFieldValue("tag").toString());
            tag.setCount((Integer)doc.getFieldValue("count"));
            tags.add(tag);
        }
        return tags;
    } //- getTags
}
