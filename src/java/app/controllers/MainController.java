package app.controllers;

import app.dao.LoginDao;
import app.dao.TransactionRequestDao;
import app.domain.Client;
import app.domain.CompletedTransaction;
import app.domain.Exchanger;
import app.domain.TransactionOffer;
import app.domain.TransactionRequest;
import app.dao.AccountAuthenticationDao;
import app.dao.CompletedTransactionDao;
import app.dao.TransactionOfferDao;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {

    //TODO: move login-oriented functionalities into LoginDao (or something along those lines)
    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private HttpServletRequest pageContext;

    @Autowired
    private TransactionRequestDao transactionRequestDao;

    @Autowired
    private LoginDao loginDao;

    @Autowired
    private TransactionOfferDao transactionOfferDao;

    @Autowired
    private CompletedTransactionDao completedTransactionDao;

    @Autowired
    private AccountAuthenticationDao accountAuthenticationDao;

    @RequestMapping(value = "/client")
    public String getClientDashboard() {
        return "clientdashboard";
    }

    @RequestMapping(value = "/exchanger")
    public String getExchangerDashboard() {
        return "exchangerdashboard";
    }

    @RequestMapping(value = "/clientlogin")
    public String getClientLogin() {
        pageContext.getSession(true).setAttribute("loggedAccount", null);
        pageContext.getSession(true).setAttribute("outcomeMessage", "You are not logged in.");
        return "clientlogin";
    }

    @RequestMapping(value = "/exchangerlogin")
    public String getExchangertLogin() {
        pageContext.getSession(true).setAttribute("loggedAccount", null);
        pageContext.getSession(true).setAttribute("outcomeMessage", "You are not logged in.");
        return "exchangerlogin";
    }

    @RequestMapping(value = "/clientlogin", method = RequestMethod.POST)
    public String postClientLogin(Client client, HttpServletResponse response) throws IOException {
        List<Client> list = loginDao.lookupClient(client);
        if (list == null) {
            Session ses = sessionFactory.openSession();
            Transaction tx = ses.beginTransaction();
            ses.save(client);
            tx.commit();
            ses.close();
            pageContext.getSession(true).setAttribute("loggedAccount", client);
            pageContext.getSession(true).setAttribute("outcomeMessage", "Logged in as " + client.getEmail());
            return "clientdashboard";
        }

        if (list.isEmpty()) {
            pageContext.getSession(true).setAttribute("outcomeMessage", "The password you have entered is incorrect.");
            return "clientlogin";
        }
        pageContext.getSession(true).setAttribute("loggedAccount", list.get(0));
        pageContext.getSession(true).setAttribute("outcomeMessage", "Logged in as " + list.get(0).getEmail());
        return "clientdashboard";

    }

    @RequestMapping(value = "/exchangerlogin", method = RequestMethod.POST)
    public String postExchangerLogin(Exchanger exchanger) {
        List<Exchanger> list = loginDao.lookupExchanger(exchanger);
        if (list == null) {
            Session ses = sessionFactory.openSession();
            Transaction tx = ses.beginTransaction();
            ses.save(exchanger);
            tx.commit();
            ses.close();
            pageContext.getSession(true).setAttribute("loggedAccount", exchanger);
            pageContext.getSession(true).setAttribute("outcomeMessage", "Logged in as " + exchanger.getName());
            return "exchangerdashboard";
        }

        if (list.isEmpty()) {
            pageContext.getSession(true).setAttribute("outcomeMessage", "The password you have entered is incorrect.");
            return "exchangerlogin";
        }
        pageContext.getSession(true).setAttribute("loggedAccount", list.get(0));
        pageContext.getSession(true).setAttribute("outcomeMessage", "Logged in as " + list.get(0).getName());
        return "exchangerdashboard";

    }

    @RequestMapping(value = "/saverequest", method = RequestMethod.POST)
    @ResponseBody
    public String saveTransactionRequest(@RequestBody TransactionRequest req) {
        if (!accountAuthenticationDao.authenticateClient(req.getClient())) {
            return "Error: Client authentication failed.";
        }
        //will need to hash passwords, ID's and usernames
        transactionRequestDao.saveTransactionRequest(req);
        return "Success";
        //change void to String and have it return a status msg
    }

    @RequestMapping(value = "/fetchrequest/{id}")//NOTE: hash the ID, OR request for an entire object to be sent instead
    @ResponseBody
    public String fetchTransactionRequest(@PathVariable int id, @RequestBody Exchanger exchanger) {
        if (!accountAuthenticationDao.authenticateExchanger(exchanger)) {
            return "Error: Exchanger authentication failed.";
        }
        return transactionRequestDao.fetchTransactionRequest(exchanger, id);
    }

    @RequestMapping(value = "/fetchrequests", method = RequestMethod.POST)
    @ResponseBody
    //will fetch all requests, however, the ones that this Exchanger has already sent an offer to will be marked as alreadyOffered (boolean property)
    public String fetchTransactionRequests(@RequestBody Exchanger exchanger) {
        if (!accountAuthenticationDao.authenticateExchanger(exchanger)) {
            return "Error: Exchanger authentication failed.";
        }
        return transactionRequestDao.fetchTransactionRequests(exchanger);
    }

    @RequestMapping(value = "/fetchoffer/{id}", method = RequestMethod.POST)
    @ResponseBody
    public String fetchTransactionOffer(@PathVariable int id, @RequestBody Client client) {
        if (!accountAuthenticationDao.authenticateClient(client)) {
            return "Error: Client authentication failed.";
        }
        return transactionOfferDao.fetchTransactionOffer(client, id);
    }

    @RequestMapping(value = "/fetchoffers", method = RequestMethod.POST)
    @ResponseBody
    public String fetchTransactionOffers(@RequestBody Client client) {
        if (!accountAuthenticationDao.authenticateClient(client)) {
            return "Error: Client authentication failed.";
        }
        return transactionOfferDao.fetchTransactionOffersByClient(client);
    }

    @RequestMapping(value = "/removetransactionrequest/{id}")
    @ResponseBody
    public void deleteTransactionRequest(@PathVariable TransactionRequest request) {
        //IMPORTANT: this end-point exists for demonstration purposes only; below method(s) will be called internally in the production version
        transactionRequestDao.deleteTransactionRequest(request);
    }

    @RequestMapping(value = "/savetransactionoffer", method = RequestMethod.POST)
    @ResponseBody
    public String saveTransactionOffer(@RequestBody TransactionOffer offer) {
        if (!accountAuthenticationDao.authenticateClient(offer.getClient())) {
            return "Error: Client authentication failed.";
        }

        if (!accountAuthenticationDao.authenticateExchanger(offer.getExchanger())) {
            return "Error: Exchanger authentication failed.";
        }

        if (!transactionRequestDao.authenticateTransactionRequest(offer.getTransactionRequest())) {
            return "Error: transaction request no longer exists.";
        }
        transactionOfferDao.saveTransactionOffer(offer);
        return "Success";
    }

    @RequestMapping(value = "/deletetransactionoffer", method = RequestMethod.POST)
    @ResponseBody
    public void deleteTransactionOffer(@RequestBody TransactionOffer offer) {
        //will probably never be accessed by end-users; here just for testing
        transactionOfferDao.deleteTransactionOffer(offer);
    }

    @RequestMapping(value = "/savecompletedtransaction", method = RequestMethod.POST)
    @ResponseBody
    public String saveCompletedTransaction(@RequestBody CompletedTransaction trans) {
        if (!accountAuthenticationDao.authenticateClient(trans.getClient())) {
            System.out.println("Error: Client authentication failed.");
            return "Error: Client authentication failed.";
        }
        if (!transactionRequestDao.transactionRequestBelongsToClient(trans.getClient(), trans.getClient().getTransactionRequest())) {
            System.out.println("Error: transaction request does not belong to this client");
            return "Error: transaction request does not belong to this client";
        }
        completedTransactionDao.saveCompletedTransaction(trans); //WARNING: it might be a bad idea to first save the transaction and then delete its prerequisites, however, this was the only way. Right now, a completedTransaction is saved based on the offer, so if the offer gets deleted first, the transaction cannot be saved
        transactionOfferDao.deleteTransactionOffersByClient(trans.getClient());
        transactionRequestDao.deleteTransactionRequest(trans.getClient().getTransactionRequest());
        return "Success";
    }

    @RequestMapping(value = "/test")
    @ResponseBody
    public void getTest() {
        /*  List orders = session.createCriteria(Order.class)
         .setFetchMode(“products”,FetchMode.JOIN
         ).list();*/
        Exchanger exchanger = new Exchanger();
        exchanger.setId(1);
        exchanger.setName("panter");
        exchanger.setPassword("patka");
        Session ses = sessionFactory.openSession();
        Transaction tx = ses.beginTransaction();
        List<TransactionRequest> list = ses.createCriteria(TransactionRequest.class).setFetchMode("transactionRequests", FetchMode.JOIN).add(Restrictions.ne("exchanger", exchanger)).list();
        tx.commit();
        ses.close();
        
        System.out.println(list.get(0).JSONify());
    }

    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public String postTest(@RequestBody Client client) {
        System.out.println("Controller reached! Client: " + client.getId() + " email " + client.getEmail() + " pw " + client.getPassword());
        return "test";
    }

}
