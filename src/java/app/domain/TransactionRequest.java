package app.domain;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

@Entity
public class TransactionRequest implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private double amount;
    private double rate;
    @OneToOne(fetch = FetchType.EAGER)
    private Client client;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "transactionRequest")
    private List<TransactionOffer> transactionOffers;
    @Transient
    //used to denote which requests were already sent offers to by the specific Exchanger;
    //this does not go into the database and is relative to the Exchanger currently in question
    //in short, the service/DAO method which is meant to REMOVE requests that the Exchanger in question has already sent an offer to, will instead keep them, but will also mark them as already offered, so they can be "grayed out" on the front end
    private boolean alreadyOffered;

    public TransactionRequest() {
    }

    public String JSONify() {
        StringBuilder sb = new StringBuilder("{'id':");
        sb.append(id);
        sb.append(",'amount':");
        sb.append(amount);
        sb.append(",'rate':");
        sb.append(rate);
        sb.append(",'client':");
        sb.append(client.JSONify());
        sb.append(",'alreadyOffered':");
        sb.append(alreadyOffered);
        sb.append("}");
        return sb.toString();
    }

    public String JSONifyAbbreviated() {
        StringBuilder sb = new StringBuilder("{'id':");
        sb.append(id);
        sb.append(",'amount':");
        sb.append(amount);
        sb.append(",'rate':");
        sb.append(rate);
        sb.append(",'alreadyOffered':");
        sb.append(alreadyOffered);
        sb.append("}");
        return sb.toString();
    }

    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public double getRate() {
        return rate;
    }

    /**
     * @param rate the rate to set
     */
    public void setRate(double rate) {
        this.rate = rate;
    }

    public List<TransactionOffer> getTransactionOffers() {
        return transactionOffers;
    }

    public void setTransactionOffers(List<TransactionOffer> transactionOffers) {
        this.transactionOffers = transactionOffers;
    }

    /**
     * @return the alreadyOffered
     */
    public boolean isAlreadyOffered() {
        return alreadyOffered;
    }

    /**
     * @param alreadyOffered the alreadyOffered to set
     */
    public void setAlreadyOffered(boolean alreadyOffered) {
        this.alreadyOffered = alreadyOffered;
    }
}
