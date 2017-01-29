package app.services;

import app.domain.Client;
import app.domain.Exchanger;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
public class AccountAuthenticationService {

    @Autowired
    private SessionFactory sessionFactory;

    public boolean authenticateClient(Client client) {
        Session ses = sessionFactory.openSession();
        Transaction tx = ses.beginTransaction();
        List<Client> list = ses.createCriteria(Client.class).add(Restrictions.eq("id", client.getId())).add(Restrictions.eq("email", client.getEmail())).add(Restrictions.eq("password", client.getPassword())).list();
        tx.commit();
        ses.close();
        if (list.isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean authenticateExchanger(Exchanger exchanger) {
        Session ses = sessionFactory.openSession();
        Transaction tx = ses.beginTransaction();
        List<Client> list = ses.createCriteria(Exchanger.class).add(Restrictions.eq("id", exchanger.getId())).add(Restrictions.eq("name", exchanger.getName())).add(Restrictions.eq("password", exchanger.getPassword())).list();
        tx.commit();
        ses.close();
        if (list.isEmpty()) {
            return false;
        }
        return true;
    }

}
