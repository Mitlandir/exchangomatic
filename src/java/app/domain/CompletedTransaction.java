package app.domain;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Entity
public class CompletedTransaction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private double amount;
    private double rate;
    @ManyToOne(fetch = FetchType.EAGER)
    private Client client;
    @ManyToOne(fetch = FetchType.EAGER)
    private Exchanger exchanger;
    @Transient
    private TransactionOffer transactionOffer;

    public CompletedTransaction() {
    }

    public String JSONify() {
        StringBuilder sb = new StringBuilder("{'id':");
        sb.append(id);
        sb.append(",'amount':");
        sb.append(amount);
        sb.append(",'rate':");
        sb.append(rate);
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

    /**
     * @return the rate
     */
    public double getRate() {
        return rate;
    }

    /**
     * @param rate the rate to set
     */
    public void setRate(double rate) {
        this.rate = rate;
    }

    /**
     * @return the client
     */
    public Client getClient() {
        return client;
    }

    /**
     * @param client the client to set
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * @return the exchanger
     */
    public Exchanger getExchanger() {
        return exchanger;
    }

    /**
     * @param exchanger the exchanger to set
     */
    public void setExchanger(Exchanger exchanger) {
        this.exchanger = exchanger;
    }

    /**
     * @return the transactionOffer
     */
    public TransactionOffer getTransactionOffer() {
        return transactionOffer;
    }

    /**
     * @param transactionOffer the transactionOffer to set
     */
    public void setTransactionOffer(TransactionOffer transactionOffer) {
        this.transactionOffer = transactionOffer;
    }

}
