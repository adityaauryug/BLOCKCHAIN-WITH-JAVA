import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HashGenerator {
    
    // creating SHA-256 string from basic input
    // kinda messy but it works fine for this
    public static String buildHash(String rawData) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = md.digest(rawData.getBytes(StandardCharsets.UTF_8));
            
            // convert bytes to hex
            StringBuilder hexStr = new StringBuilder();
            for (byte b : encodedhash) {
                String hex = String.format("%02x", b);
                hexStr.append(hex);
            }
            return hexStr.toString();
        } catch (Exception ex) {
            // shouldn't happen unless java is broken
            throw new RuntimeException("SHA-256 missing", ex);
        }
    }

    // computes the root hash of all transactions
    public static String computeRootHash(List<TxData> records) {
        if (records == null || records.isEmpty()) {
            return "EMPTY_ROOT";
        }
        
        System.out.println("-> Hashing " + records.size() + " records together...");
        
        // extract hashes first
        String[] hashList = new String[records.size()];
        for (int i = 0; i < records.size(); i++) {
            hashList[i] = records.get(i).generateSignature();
        }
        
        // combine them until only 1 is left
        int length = hashList.length;
        while (length > 1) {
            int nextIndex = 0;
            for (int i = 0; i < length; i += 2) {
                if (i == length - 1) {
                    // odd number so just hash it with itself
                    hashList[nextIndex] = buildHash(hashList[i] + hashList[i]);
                } else {
                    hashList[nextIndex] = buildHash(hashList[i] + hashList[i + 1]);
                }
                nextIndex++;
            }
            length = nextIndex;
        }
        
        return hashList[0];
    }
}
