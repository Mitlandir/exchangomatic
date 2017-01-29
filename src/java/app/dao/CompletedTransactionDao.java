package app.services;

import app.domain.Client;
import app.domain.CompletedTransaction;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CompletedTransactionService {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private HttpServletRequest pageContext;
    

    public void saveCompletedTransaction(CompletedTransaction trans) {
        Session ses = sessionFactory.openSession(); //repeating session and transaction creation/closure to make sure the command is carried out; may need to be pruned out
        Transaction tx = ses.beginTransaction();
        ses.save(trans);
        ses.delete(trans.getTransactionOffer()); //deleting the transaction offer which lead to this CompleteTransaction in order to prevent clients from making multiple CompletedTransactions based off of the same TransactionOffer
        tx.commit();
        ses.close();
    }

}
