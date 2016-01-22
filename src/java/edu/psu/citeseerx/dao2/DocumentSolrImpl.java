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

        Object title = solrDoc.getFieldValue("title");
        Object abstractText = solrDoc.getFieldValue("abstract");
        Object year = solrDoc.getFieldValue("year");
        Object venue = solrDoc.getFieldValue("venue");
        Object venueType = solrDoc.getFieldValue("venueType");
        Object pages = solrDoc.getFieldValue("pages");
        Object volume = solrDoc.getFieldValue("volume");
        Object number = solrDoc.getFieldValue("number");
        Object publisher = solrDoc.getFieldValue("publisher");
        Object pubAddress = solrDoc.getFieldValue("pubAddress");
        Object tech = solrDoc.getFieldValue("tech");
        Object cites = solrDoc.getFieldValue("citations");

        if (title != null) { doc.setSource(Document.TITLE_KEY, title.toString()); }
        if (abstractText != null) { doc.setSource(Document.ABSTRACT_KEY, abstractText.toString()); }
        if (year != null) { doc.setSource(Document.YEAR_KEY, year.toString()); }
        if (venue != null) { doc.setSource(Document.VENUE_KEY, venue.toString()); }
        if (venueType != null) { doc.setSource(Document.VEN_TYPE_KEY, venueType.toString()); }
        if (pages != null) { doc.setSource(Document.PAGES_KEY, pages.toString()); }
        if (volume != null) { doc.setSource(Document.VOL_KEY, volume.toString()); }
        if (number != null) { doc.setSource(Document.NUM_KEY, number.toString()); }
        if (publisher != null) { doc.setSource(Document.PUBLISHER_KEY, publisher.toString()); }
        if (pubAddress != null) { doc.setSource(Document.PUBADDR_KEY, pubAddress.toString()); }
        if (tech != null) { doc.setSource(Document.TECH_KEY, tech.toString()); }
        if (cites != null) { doc.setSource(Document.CITES_KEY, cites.toString()); }

        return doc;

    }  //- getDocument
}
