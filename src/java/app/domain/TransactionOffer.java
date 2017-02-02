package app.domain;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class TransactionOffer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private double amount;
    private double rate;
    @ManyToOne(fetch = FetchType.EAGER)
    private Exchanger exchanger;
    @ManyToOne(fetch = FetchType.EAGER)
    private Client client;
    @ManyToOne(fetch = FetchType.EAGER)
    private TransactionRequest transactionRequest;


    public TransactionOffer() {
    }

    public String JSONify() {
        StringBuilder sb = new StringBuilder("{'id':");
        sb.append(id);
        sb.append(",'amount':");
        sb.append(amount);
        sb.append(",'rate':");
        sb.append(rate);
        sb.append(",'exchanger':");
        sb.append(exchanger.JSONify());
        sb.append(",'client':");
        sb.append(client.JSONify());
        sb.append(",'transactionRequest':");
        sb.append(transactionRequest.JSONifyAbbreviated());
        sb.append("}");
        return sb.toString();
    }

    /**
     * @return the id
     */
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


    public Client getClient() {
        return client;
    }


    public void setClient(Client client) {
        this.client = client;
    }

    public TransactionRequest getTransactionRequest() {
        return transactionRequest;
    }


    public void setTransactionRequest(TransactionRequest transactionRequest) {
        this.transactionRequest = transactionRequest;
    }

   

}
