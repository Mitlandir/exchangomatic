package app.dao;

import app.domain.Client;
import app.domain.TransactionOffer;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionOfferDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private HttpServletRequest pageContext;

    @Autowired
    private TransactionRequestDao transactionRequestDao;

    public boolean transactionOfferAlreadySent(TransactionOffer offer) {
        //checks if this exchanger has already sent an offer to the same client for the same transaction request
        Session ses = sessionFactory.openSession();
        Transaction tx = ses.beginTransaction();
        List list = ses.createCriteria(TransactionOffer.class).add(Restrictions.eq("client", offer.getClient())).add(Restrictions.eq("exchanger", offer.getExchanger())).add(Restrictions.eq("transactionRequest", offer.getTransactionRequest())).list();
        tx.commit();
        ses.close();

        if (list.isEmpty()) {//if the list is empty, it means the database has no records of offers sent by this particular exchanger to this particular client for this particular transaction request
            return false;
        }

        return true;
    }

    public void saveTransactionOffer(TransactionOffer offer) {
        if (transactionOfferAlreadySent(offer)) {
            StringBuilder sb = new StringBuilder("Error! Exchanger ");
            sb.append(offer.getExchanger().getName());
            sb.append(" has already sent an offer to Client ");
            sb.append(offer.getClient().getEmail());
            sb.append(" for transaction request ");
            sb.append(offer.getTransactionRequest().getId());          
            System.out.println(sb.toString());
            return;
        }

        Session ses = sessionFactory.openSession();
        Transaction tx = ses.beginTransaction();
        ses.save(offer);
        tx.commit();
        ses.close();
    }

    public void deleteTransactionOffer(TransactionOffer offer) {
        Session ses = sessionFactory.openSession();
        Transaction tx = ses.beginTransaction();
        ses.delete(offer);
        tx.commit();
        ses.close();
    }

    public void deleteTransactionOffersByClient(Client client) {
        Session ses = sessionFactory.openSession();
        Transaction tx = ses.beginTransaction();
        List<TransactionOffer> list = ses.createCriteria(TransactionOffer.class).add(Restrictions.eq("client", client)).list();
        for (TransactionOffer offer : list) {
            ses.delete(offer);
        }
        tx.commit();
        ses.close();
    }

    public boolean transactionOfferMismatch(Client client, int id) {
        Session ses = sessionFactory.openSession();
        Transaction tx = ses.beginTransaction();
        List<TransactionOffer> list = ses.createCriteria(TransactionOffer.class).add(Restrictions.eq("client", client)).add(Restrictions.eq("id", id)).list();
        tx.commit();
        ses.close();

        if (list.isEmpty()) {
            return true;
        }
        
        if ((!list.get(0).getClient().getEmail().equals(client.getEmail())) || (!list.get(0).getClient().getPassword().equals(client.getPassword()))) {
            //making sure the client making the query is indeed the client whose transaction offer is being fetched
            return true;
        }

        if (!list.isEmpty()) {
            return false;
        }
        
        return true;
    }

    public String fetchTransactionOffer(Client client, int id) {
        if (transactionOfferMismatch(client, id)) {
            StringBuilder sb = new StringBuilder("Error! Transaction Offer ");
            sb.append(id);
            sb.append(" does not belong to client ");
            sb.append(client.getEmail());
            System.out.println(sb.toString());
            return sb.toString();
        }
        Session ses = sessionFactory.openSession();
        Transaction tx = ses.beginTransaction();
        List<TransactionOffer> list = ses.createCriteria(TransactionOffer.class).add(Restrictions.eq("id", id)).list();
        tx.commit();
        ses.close();

        return list.get(0).JSONify();
    }

    public String fetchTransactionOffersByClient(Client client) {
        Session ses = sessionFactory.openSession();
        Transaction tx = ses.beginTransaction();
        List<TransactionOffer> transactionOffers = ses.createCriteria(TransactionOffer.class).add(Restrictions.eq("client", client)).list();
        tx.commit();
        ses.close();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for (int i = 0; i < transactionOffers.size(); i++) { //avoiding for-each to ensure I can choose NOT to append a comma after the last JSON object (it would actually work fine even with the comma, but better safe than sorry!)
            stringBuilder.append(transactionOffers.get(i).JSONify());
            if (i != (transactionOffers.size() - 1)) {
                stringBuilder.append(",");
            }
        }
        stringBuilder.append("]");

        return String.valueOf(stringBuilder);
    }

}
