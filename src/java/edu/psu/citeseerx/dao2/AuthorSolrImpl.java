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

    private String solrUrl;
    private SolrClient solr;

    public void setSolrUrl(String solrUrl) {
        this.solrUrl = solrUrl + "authors";
    }

    @Override
    public List<Author> getDocAuthors(String doi, boolean getSource)
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
        List<Author> authors = new ArrayList<Author>();

        for (SolrDocument doc : resp.getResults()) {
            Author auth = new Author();
            Object id = doc.getFieldValue("id");
            Object cluster = doc.getFieldValue("cluster");
            Object name = doc.getFieldValue("name");
            Object affil = doc.getFieldValue("affil");
            Object addr = doc.getFieldValue("address");
            Object email = doc.getFieldValue("email");
            Object ord = doc.getFieldValue("ord");

            if (id != null) {
                auth.setDatum(Author.DOI_KEY, id.toString());
            }
            if (cluster != null) {
                auth.setClusterID((long)cluster);
            }
            if (name != null) {
                auth.setDatum(Author.NAME_KEY, name.toString());
            }

            if (affil != null) {
                auth.setDatum(Author.AFFIL_KEY, affil.toString());    
            }
            if (addr != null) {
                auth.setDatum(Author.ADDR_KEY, addr.toString());
            }
            if (email != null) {
                auth.setDatum(Author.EMAIL_KEY, email.toString());    
            }
            if (ord != null) {
                auth.setDatum(Author.ORD_KEY, ord.toString());    
            }
            
            authors.add(auth);
        }

        return authors;
    }
}
