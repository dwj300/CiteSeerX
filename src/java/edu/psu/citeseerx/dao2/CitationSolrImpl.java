package edu.psu.citeseerx.dao2;

import edu.psu.citeseerx.domain.Citation;
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
public class CitationSolrImpl extends CitationDAOImpl {

    private static String SOLR_URL = "http://localhost:8983/solr/citations";
    private SolrClient solr;

    @Override
    public List<Citation> getCitations(String doi, boolean withContexts)
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
        List<Citation> citations = new ArrayList<Citation>();

        for (SolrDocument doc : resp.getResults()) {
            Citation citation = new Citation();

            citation.setDatum(Citation.DOI_KEY, doc.getFieldValue("id").toString());
            citation.setClusterID((Long)doc.getFieldValue("cluster"));

            String[] authors = doc.getFieldValue("authors").toString().split(",");
            for (int i = 0; i < authors.length; i++) {
                citation.addAuthorName(authors[i]);
            }

            String[] keys = {Citation.TITLE_KEY, Citation.VENUE_KEY, Citation.VEN_TYPE_KEY,
                    Citation.YEAR_KEY, Citation.PAGES_KEY, Citation.EDITORS_KEY, Citation.PUBLISHER_KEY,
                    Citation.PUB_ADDR_KEY, Citation.VOL_KEY, Citation.NUMBER_KEY, Citation.TECH_KEY,
                    Citation.RAW_KEY};

            for(String key : keys) {
                Object obj = doc.getFieldValue(key);
                if (obj != null) {
                    citation.setDatum(key, obj.toString());
                }
            }
            /*
            citation.setDatum(Citation.TITLE_KEY, doc.getFieldValue("title").toString());
            citation.setDatum(Citation.VENUE_KEY, doc.getFieldValue("venue").toString());
            citation.setDatum(Citation.VEN_TYPE_KEY, doc.getFieldValue("venueType").toString());
            citation.setDatum(Citation.YEAR_KEY, doc.getFieldValue("year").toString());
            citation.setDatum(Citation.PAGES_KEY, doc.getFieldValue("pages").toString());
            citation.setDatum(Citation.EDITORS_KEY, doc.getFieldValue("editors").toString());
            citation.setDatum(Citation.PUBLISHER_KEY, doc.getFieldValue("publisher").toString());
            citation.setDatum(Citation.PUB_ADDR_KEY, doc.getFieldValue("pubAddress").toString());
            citation.setDatum(Citation.VOL_KEY, doc.getFieldValue("volume").toString());
            citation.setDatum(Citation.NUMBER_KEY, doc.getFieldValue("number").toString());
            citation.setDatum(Citation.TECH_KEY, doc.getFieldValue("tech").toString());
            citation.setDatum(Citation.RAW_KEY, doc.getFieldValue("raw").toString());*/

            citation.setDatum(Citation.PAPERID_KEY, doc.getFieldValue("paperid").toString());
            citation.setSelf(((Integer)doc.getFieldValue("self")) == 1);
            citations.add(citation);
        }

        if (withContexts) {
            /*
            for (Object o : citations) {
                Citation c = (Citation)o;
                Long id = new Long(Long.parseLong(c.getDatum(Citation.DOI_KEY)));
                List<String> contexts = getContexts.run(id);
                for (Object oc : contexts) {
                    c.addContext((String)oc);
                }
            }*/
        }
        return citations;

    }  //- getCitations
}
