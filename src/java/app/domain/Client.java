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

@Entity
public class Client implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String email;
    private String password;
    @OneToOne(fetch = FetchType.EAGER, mappedBy = "client")
    private TransactionRequest transactionRequest = new TransactionRequest();
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "client")
    private List<TransactionOffer> transactionOffers;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "client")
    private List<CompletedTransaction> completedTransactions;

    
    public Client() {
    }

    public String JSONify(){//have to be careful with this one; can't concat transactionRequest's JSONify method to this as well, else it will enter an endless loop of concatenation
        //this is all a work-around until we get Jackson up and running
        StringBuilder sb = new StringBuilder("{'id':");
        sb.append(id);
        sb.append(",'email':'");
        sb.append(email);
        sb.append("','password':'");
        sb.append(password);
        sb.append("','transactionRequest':");
        sb.append(transactionRequest.JSONifyAbbreviated());
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
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the transactionRequest
     */
    public TransactionRequest getTransactionRequest() {
        return transactionRequest;
    }

    /**
     * @param transactionRequest the transactionRequest to set
     */
    public void setTransactionRequest(TransactionRequest transactionRequest) {
        this.transactionRequest = transactionRequest;
    }


    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public List<TransactionOffer> getTransactionOffers() {
        return transactionOffers;
    }

    public void setTransactionOffers(List<TransactionOffer> transactionOffers) {
        this.transactionOffers = transactionOffers;
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
