package app.services;

import app.domain.Client;
import app.domain.Exchanger;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class LoginService {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private HttpServletRequest pageContext;


    public List<Client> lookupClient(Client client) {
        //if both the email and the password are correct, it returns a list containing the client; if the password is incorrect, it returns an empty list; if there is no match whatsoever, it returns null
        Session ses = sessionFactory.openSession(); //repeating session and transaction creation/closure to make sure the command is carried out; may need to be pruned out
        Transaction tx = ses.beginTransaction();
        List<Client> clients = ses.createCriteria(Client.class).add(Restrictions.eq("email", client.getEmail())).add(Restrictions.eq("password", client.getPassword())).list();
        tx.commit();
        ses.close();
        if (!clients.isEmpty()) {
            return clients;
        } else {
            ses = sessionFactory.openSession();
            tx = ses.beginTransaction();
            clients = ses.createCriteria(Client.class).add(Restrictions.eq("email", client.getEmail())).list();
            tx.commit();
            ses.close();
            if (!clients.isEmpty()) {
                clients = new ArrayList<Client>();
                return clients;
            }
            return null;
        }

    }

    public List<Exchanger> lookupExchanger(Exchanger exchanger) {
        //if both the name and the password are correct, it returns a list containing the client; if the password is incorrect, it returns an empty list; if there is no match whatsoever, it returns null
        Session ses = sessionFactory.openSession();//repeating session and transaction creation/closure to make sure the command is carried out; may need to be pruned out
        Transaction tx = ses.beginTransaction();
        List<Exchanger> exchangers = ses.createCriteria(Exchanger.class).add(Restrictions.eq("name", exchanger.getName())).add(Restrictions.eq("password", exchanger.getPassword())).list();
        tx.commit();
        ses.close();
        if (!exchangers.isEmpty()) {
            return exchangers;
        } else {
            ses = sessionFactory.openSession();
            tx = ses.beginTransaction();
            exchangers = ses.createCriteria(Exchanger.class).add(Restrictions.eq("name", exchanger.getName())).list();
            tx.commit();
            ses.close();
            if (!exchangers.isEmpty()) {
                exchangers = new ArrayList<Exchanger>();
                return exchangers;
            }
            return null;
        }
    }
}
