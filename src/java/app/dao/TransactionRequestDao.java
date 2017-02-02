package app.dao;

import app.domain.Client;
import app.domain.Exchanger;
import app.domain.TransactionOffer;
import app.domain.TransactionRequest;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
public class TransactionRequestDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private HttpServletRequest pageContext;

    public boolean authenticateTransactionRequest(TransactionRequest request) {
        Session ses = sessionFactory.openSession();
        Transaction tx = ses.beginTransaction();
        List list = ses.createCriteria(TransactionRequest.class).add(Restrictions.eq("id", request.getId())).add(Restrictions.eq("client", request.getClient())).list();
        tx.commit();
        ses.close();
        if (list.isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean transactionRequestInProgress(Client client) {
        Session ses = sessionFactory.openSession();
        Transaction tx = ses.beginTransaction();
        List list = ses.createCriteria(TransactionRequest.class).add(Restrictions.eq("client", client)).list();
        tx.commit();
        ses.close();
        if (list.isEmpty()) {
            return false;
        }
        System.out.println("A transaction request is already in progress for this account!");
        return true;
    }

    public void saveTransactionRequest(TransactionRequest req) {
        if (transactionRequestInProgress(req.getClient())) {
            return;
        }
        Session ses = sessionFactory.openSession();
        Transaction tx = ses.beginTransaction();
        ses.save(req);
        tx.commit();
        ses.close();
        //and some sort of return value for status message
    }

    public boolean transactionRequestBelongsToClient(Client client, TransactionRequest request) {
        Session ses = sessionFactory.openSession();
        Transaction tx = ses.beginTransaction();
        List<TransactionRequest> list = ses.createCriteria(TransactionRequest.class).add(Restrictions.eq("client", client)).list();
        tx.commit();
        ses.close();
        if (list.isEmpty()) {
            return false;
        }
        return true;
    }

    public String deleteTransactionRequest(TransactionRequest request) {
        //security check (whether this request belongs to the correct client) is being done in the controller
        Session ses = sessionFactory.openSession();
        Transaction tx = ses.beginTransaction();
        ses.delete(request);
        tx.commit();
        ses.close();
        return "Success";
    }

    public String fetchTransactionRequest(Exchanger exchanger, int id) { //SECURITY NOTE: will probably need to either hash the ID, or change the method so it requests so kind of authorization and/or an entire object
        Session ses = sessionFactory.openSession();
        Transaction tx = ses.beginTransaction();
        List<TransactionRequest> transactionRequests = ses.createCriteria(TransactionRequest.class).add(Restrictions.eq("id", id)).list();
        tx.commit();
        ses.close();
        
        if(transactionRequests.isEmpty()){
            StringBuilder sb = new StringBuilder("Error! Transaction request ");
            sb.append(id);
            sb.append(" does not belong to exchanger ");
            sb.append(exchanger.getName());
            
            System.out.println(sb.toString());
            return sb.toString();
        }

        //change to StringBuilder for optimization purposes
        return transactionRequests.get(0).JSONify();
    }

    public String fetchTransactionRequests(Exchanger exchanger) {
        Session ses = sessionFactory.openSession();
        Transaction tx = ses.beginTransaction();
        //making a workaround because I am too darn lazy to figure out complex criteria...
        List<TransactionOffer> offers = ses.createCriteria(TransactionOffer.class).add(Restrictions.eq("exchanger", exchanger)).list();
        List<TransactionRequest> requests = ses.createCriteria(TransactionRequest.class).list();
        List<TransactionRequest> requestsToRemove = new ArrayList<TransactionRequest>();
        tx.commit();
        ses.close();
        //using lists as a workaround since I am too lazy to try and figure out how to work with complex criteria (joining PLUS restrictions on the joined table); should probably resort to it eventually
        for (TransactionOffer offer : offers) {
                for (TransactionRequest request : requests) {
                    if (offer.getTransactionRequest().getId() == request.getId()) {
                        request.setAlreadyOffered(true);
                    }
                }
        }
        
        //requests.removeAll(requestsToRemove); -- DEPRECATED; not purging "already offered" requests anymore; marking them as "alreadyOffered" instead

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for (int i = 0; i < requests.size(); i++) { //avoiding for-each to ensure I can choose NOT to append a comma after the last JSON object (it would actually work fine even with the comma, but better safe than sorry!)
            stringBuilder.append(requests.get(i).JSONify());
            if (i != (requests.size() - 1)) {
                stringBuilder.append(",");
            }
        }
        stringBuilder.append("]");

        return String.valueOf(stringBuilder);
    }

}
