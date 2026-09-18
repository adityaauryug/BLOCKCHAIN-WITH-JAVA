import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

public class Wallet {
    
    public PrivateKey privateKey;
    public PublicKey publicKey;
    public String walletName;

    public Wallet(String name) {
        this.walletName = name;
        generateKeyPair();
    }
    
    public void generateKeyPair() {
        try {
            // Using RSA for standard java support without extra libraries
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            keyGen.initialize(1024, random); 
            KeyPair keyPair = keyGen.generateKeyPair();
            
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
            
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    // Scans the entire chain + mempool to calculate actual balance
    public float getBalance() {
        float total = 0;
        
        // Check confirmed blocks
        for (NodeBlock block : LedgerCore.mainChain) {
            for (TxData tx : block.recordList) {
                if (tx.fromUser != null && tx.fromUser.equals(publicKey)) {
                    total -= tx.transferValue;
                }
                if (tx.toUser != null && tx.toUser.equals(publicKey)) {
                    total += tx.transferValue;
                }
            }
        }
        
        // Deduct pending transactions from mempool so they can't double spend
        for (TxData tx : LedgerCore.pendingTransactions) {
            if (tx.fromUser != null && tx.fromUser.equals(publicKey)) {
                total -= tx.transferValue;
            }
        }
        
        return total;
    }
    
    // Creates a new transaction from this wallet
    public TxData sendFunds(PublicKey recipient, float value) {
        if(getBalance() < value) {
            System.out.println("--> Alert: " + walletName + " has insufficient funds to send " + value);
            return null;
        }
        
        TxData newTx = new TxData(publicKey, recipient, value);
        newTx.generateSignature(privateKey);
        
        return newTx;
    }
}
