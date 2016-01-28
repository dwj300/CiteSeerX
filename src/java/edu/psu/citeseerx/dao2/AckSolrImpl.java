package edu.psu.citeseerx.dao2;

import edu.psu.citeseerx.domain.Acknowledgment;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.dao.DataAccessException;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Doug on 1/27/16.
 */
public class AckSolrImpl extends AckDAOImpl {

    private static String SOLR_URL = "http://localhost:8983/solr/acknowledgments";
    private SolrClient solr;

    @Override
    public List<Acknowledgment> getAcknowledgments(String doi, boolean withContexts,
                                                   boolean withSource) throws DataAccessException {
        if (solr == null) {
            solr = new HttpSolrClient(SOLR_URL);
        }

        // Todo: deal with context / source

        SolrQuery query = new SolrQuery();
        query.setQuery("paperid:\"" + doi + "\"");
        QueryResponse resp = null;
        try {
            resp = solr.query(query);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        List<Acknowledgment> acknowledgments = new ArrayList<Acknowledgment>();

        for (SolrDocument doc : resp.getResults()) {
            Acknowledgment ack = new Acknowledgment();
            ack.setDatum(Acknowledgment.DOI_KEY, doc.getFieldValue("id").toString());
            ack.setClusterID((Long)doc.getFieldValue("cluster"));
            ack.setDatum(Acknowledgment.NAME_KEY, doc.getFieldValue("name").toString());
            ack.setDatum(Acknowledgment.ENT_TYPE_KEY, doc.getFieldValue("entType").toString());
            ack.setDatum(Acknowledgment.ACK_TYPE_KEY, doc.getFieldValue("ackType").toString());
            acknowledgments.add(ack);
        }
        return acknowledgments;
    }
}
