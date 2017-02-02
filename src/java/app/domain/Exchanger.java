package app.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Exchanger implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String password;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "exchanger")
    private List<TransactionOffer> transactionOffers;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "exchanger")
    private List<CompletedTransaction> completedTransactions;

    public Exchanger() {
    }

    public String JSONify() {
        StringBuilder sb = new StringBuilder("{'id':");
        sb.append(id);
        sb.append(",'name':'");
        sb.append(name);
        sb.append("','password':'");
        sb.append(password);
        sb.append("'}");
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
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    public List<TransactionOffer> getTransactionOffers() {
        return transactionOffers;
    }

    public void setTransactionOffers(List<TransactionOffer> transactionOffers) {
        this.transactionOffers = transactionOffers;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the completedTransactions
     */
    public List<CompletedTransaction> getCompletedTransactions() {
        return completedTransactions;
    }

    /**
     * @param completedTransactions the completedTransactions to set
     */
    public void setCompletedTransactions(List<CompletedTransaction> completedTransactions) {
        this.completedTransactions = completedTransactions;
    }

}
