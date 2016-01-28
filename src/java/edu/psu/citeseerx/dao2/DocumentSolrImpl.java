package edu.psu.citeseerx.dao2;

import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.dao.DataAccessException;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;

import java.util.ArrayList;
import java.util.Date;
import edu.psu.citeseerx.domain.*;
import java.text.DateFormat;
import java.util.List;

/**
 * Created by Doug on 1/20/16.
 */
public class DocumentSolrImpl extends DocumentDAOImpl {
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.DocumentDAO#getDocument(java.lang.String, boolean)
     */

    private static String SOLR_URL = "http://localhost:8983/solr/papers";

    private static String NGRAM_SOLR_URL = "http://localhost:8983/solr/ngrams";
    private SolrClient solr;
    private SolrClient ngramSolr;

    @Override
    public Document getDocument(String doi, boolean getSource)
            throws DataAccessException {
        // todo: use a bean
        // todo: handle getsource
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

        String[] keys = {Document.TITLE_KEY, Document.ABSTRACT_KEY, Document.YEAR_KEY, Document.VENUE_KEY,
                         Document.VEN_TYPE_KEY, Document.PAGES_KEY, Document.VOL_KEY, Document.PUBLISHER_KEY,
                         Document.PUBADDR_KEY, Document.TECH_KEY };

        for (String key : keys) {
            Object obj = solrDoc.getFieldValue(key);
            if (obj != null) {
                doc.setDatum(key, obj.toString());
            }
        }

        doc.setDatum(Document.DOI_KEY, solrDoc.getFieldValue("id").toString());
        doc.setVersion((int)solrDoc.getFieldValue("version"));
        doc.setClusterID((long)solrDoc.getFieldValue("cluster"));

        doc.setNcites((int)solrDoc.getFieldValue("ncites"));
        doc.setSelfCites((int)solrDoc.getFieldValue("selfCites"));
        doc.setVersionName(solrDoc.getFieldValue("versionName").toString());
        doc.setState((int)solrDoc.getFieldValue("public"));
/*
            if (rs.getBoolean("public")) {
                doc.setState(DocumentProperties.IS_PUBLIC);
            }
	    else{
                doc.setState(DocumentProperties.LOGICAL_DELETE);
            }
*/
        doc.setVersionTime((Date)solrDoc.getFieldValue("versionTime"));

        DocumentFileInfo finfo = new DocumentFileInfo();
        finfo.setDatum(DocumentFileInfo.CRAWL_DATE_KEY, DateFormat.getDateInstance().format((Date)solrDoc.getFieldValue("crawlDate")));
        finfo.setDatum(DocumentFileInfo.REP_ID_KEY, solrDoc.getFieldValue("repositoryID").toString());
        finfo.setDatum(DocumentFileInfo.CONV_TRACE_KEY, solrDoc.getFieldValue("conversionTrace").toString());
        doc.setFileInfo(finfo);

        return doc;

    }  //- getDocument

    @Override
    public List<String> getKeyphrase(String doi)
            throws DataAccessException {
        if (ngramSolr == null) {
            ngramSolr = new HttpSolrClient(NGRAM_SOLR_URL);
        }

        // TOOD: deal with sort
        SolrQuery query = new SolrQuery();
        query.setQuery("paper_id:\"" + doi + "\"");

        QueryResponse resp = null;
        try {
            resp = ngramSolr.query(query);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        List<String> ngrams = new ArrayList<String>();

        for (SolrDocument doc : resp.getResults()) {
            ngrams.add(doc.getFieldValue("ngram").toString());
        }
        return ngrams;

    } //- getKeyphrase
}
