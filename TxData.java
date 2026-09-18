import java.security.PrivateKey;
import java.security.PublicKey;

public class TxData {
    
    public String signatureId; // hash of the tx
    public PublicKey fromUser;
    public PublicKey toUser;
    public float transferValue;
    public long createdTime;
    
    public String rsaSignature; // digital signature

    // Note: fromUser can be null if it's the System mining reward
    public TxData(PublicKey from, PublicKey to, float val) {
        this.fromUser = from;
        this.toUser = to;
        this.transferValue = val;
        this.createdTime = System.currentTimeMillis();
        this.signatureId = calculateHash();
    }

    // Hash of the raw tx data (without signature)
    private String calculateHash() {
        String fromStr = (fromUser == null) ? "SYSTEM" : HashGenerator.getStringFromKey(fromUser);
        String combined = fromStr + HashGenerator.getStringFromKey(toUser) + transferValue + createdTime;
        return HashGenerator.buildHash(combined);
    }

    // signs the data so nobody else can fake it
    public void generateSignature(PrivateKey privateKey) {
        String data = (fromUser == null ? "SYSTEM" : HashGenerator.getStringFromKey(fromUser)) + 
                      HashGenerator.getStringFromKey(toUser) + Float.toString(transferValue);
        rsaSignature = HashGenerator.applyRSASig(privateKey, data);
    }
    
    // verifier for the ledger to check before accepting
    public boolean verifySignature() {
        if (fromUser == null) return true; // System reward transactions don't need signature
        
        String data = HashGenerator.getStringFromKey(fromUser) + HashGenerator.getStringFromKey(toUser) + Float.toString(transferValue);
        return HashGenerator.verifyRSASig(fromUser, data, rsaSignature);
    }
    
    // just for printing
    public void display() {
        String sender = (fromUser == null) ? "System" : "Wallet[" + HashGenerator.getStringFromKey(fromUser).substring(0,6) + "...]";
        String rec = "Wallet[" + HashGenerator.getStringFromKey(toUser).substring(0,6) + "...]";
        System.out.println("  [Tx: " + signatureId.substring(0,8) + "] " + sender + " pays " + transferValue + " to " + rec);
    }
}
