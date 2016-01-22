package edu.psu.citeseerx.dao2;

import edu.psu.citeseerx.domain.Author;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.dao.DataAccessException;
import org.apache.solr.common.SolrDocument;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by Doug on 1/22/16.
 */
public class AuthorSolrImpl extends AuthorDAOImpl {

    private static String SOLR_URL = "http://localhost:8983/solr/authors";
    private SolrClient solr;

    public List<Author> getDocAuthors(String doi, boolean getSource)
            throws DataAccessException {
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
        List<Author> authors = new ArrayList<Author>();

        for (SolrDocument doc : resp.getResults()) {
            Author auth = new Author();
            auth.setDatum(Author.DOI_KEY, doc.getFieldValue("id").toString());
            auth.setClusterID((long) doc.getFieldValue("cluster"));
            auth.setDatum(Author.NAME_KEY, doc.getFieldValue("name").toString());
            auth.setDatum(Author.AFFIL_KEY, doc.getFieldValue("affil").toString());
            auth.setDatum(Author.ADDR_KEY, doc.getFieldValue("address").toString());
            auth.setDatum(Author.EMAIL_KEY, doc.getFieldValue("email").toString());
            auth.setDatum(Author.ORD_KEY, doc.getFieldValue("ord").toString());
            authors.add(auth);
        }

        return authors;
    }
}