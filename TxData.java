public class TxData {
    
    public String signature;
    public String fromUser;
    public String toUser;
    public float transferValue;
    public long createdTime;

    public TxData(String from, String to, float val) {
        this.fromUser = from;
        this.toUser = to;
        this.transferValue = val;
        this.createdTime = System.currentTimeMillis();
        this.signature = generateSignature();
    }

    // gets a unique signature for this specific transfer
    public String generateSignature() {
        String combined = fromUser + toUser + Float.toString(transferValue) + Long.toString(createdTime);
        return HashGenerator.buildHash(combined);
    }
    
    // just for printing
    public void display() {
        System.out.println("  [Tx: " + signature.substring(0,8) + "] " + fromUser + " pays " + transferValue + " to " + toUser);
    }
}
