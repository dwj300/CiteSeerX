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

    private String solrUrl;

    private String NGRAM_SOLR_URL;
    private SolrClient solr;
    private SolrClient ngramSolr;

    public void setSolrUrl(String solrUrl) {
        this.solrUrl = solrUrl + "papers";
        this.NGRAM_SOLR_URL = solrUrl + "ngrams";
    }

    @Override
    public Document getDocument(String doi, boolean getSource)
            throws DataAccessException {
        // todo: use a bean
        // todo: handle getsource
        if (solr == null) {
            solr = new HttpSolrClient(solrUrl);
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
            System.out.println("no results found for doi:" + doi);
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

        Object id = solrDoc.getFieldValue("id");
        Object version = solrDoc.getFieldValue("version");
        Object cluster = solrDoc.getFieldValue("cluster");
        Object ncites = solrDoc.getFieldValue("ncites");
        Object selfCites = solrDoc.getFieldValue("selfCites");
        Object versionName = solrDoc.getFieldValue("versionName");
        Object state = solrDoc.getFieldValue("public");
        Object versionTime = solrDoc.getFieldValue("versionTime");

        if (id != null) {
            doc.setDatum(Document.DOI_KEY, id.toString());    
        }
        if (version != null) {
            doc.setVersion((int)version);    
        }
        if (cluster != null) {
            doc.setClusterID((long)cluster);
        }
        if (ncites != null) {
            doc.setNcites((int)ncites);
        }
        if (selfCites != null) {
            doc.setSelfCites((int)selfCites);
        }
        if (versionName != null) {
            doc.setVersionName(versionName.toString());
        }
        if (state != null) {
            doc.setState((int)state);
        }
        if (versionTime != null) {
            doc.setVersionTime((Date)versionTime);
        }
        
/*
            if (rs.getBoolean("public")) {
                doc.setState(DocumentProperties.IS_PUBLIC);
            }
	    else{
                doc.setState(DocumentProperties.LOGICAL_DELETE);
            }
*/
        

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
