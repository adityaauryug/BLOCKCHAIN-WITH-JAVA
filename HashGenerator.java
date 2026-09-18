import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;
import java.util.List;

public class HashGenerator {
    
    // creating SHA-256 string from basic input
    public static String buildHash(String rawData) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = md.digest(rawData.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexStr = new StringBuilder();
            for (byte b : encodedhash) {
                String hex = String.format("%02x", b);
                hexStr.append(hex);
            }
            return hexStr.toString();
        } catch (Exception ex) {
            throw new RuntimeException("SHA-256 missing", ex);
        }
    }

    // computes the root hash of all transactions
    public static String computeRootHash(List<TxData> records) {
        if (records == null || records.isEmpty()) {
            return "EMPTY_ROOT";
        }
        
        String[] hashList = new String[records.size()];
        for (int i = 0; i < records.size(); i++) {
            hashList[i] = records.get(i).signatureId;
        }
        
        int length = hashList.length;
        while (length > 1) {
            int nextIndex = 0;
            for (int i = 0; i < length; i += 2) {
                if (i == length - 1) {
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
    
    // Applies RSA Signature and returns a Base64 string
    public static String applyRSASig(PrivateKey privateKey, String input) {
        try {
            Signature dsa = Signature.getInstance("SHA256withRSA");
            dsa.initSign(privateKey);
            byte[] strByte = input.getBytes();
            dsa.update(strByte);
            byte[] realSig = dsa.sign();
            return Base64.getEncoder().encodeToString(realSig);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    // Verifies an RSA Signature 
    public static boolean verifyRSASig(PublicKey publicKey, String data, String signature) {
        try {
            Signature ecdsaVerify = Signature.getInstance("SHA256withRSA");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(Base64.getDecoder().decode(signature));
        } catch (Exception e) {
            System.out.println("Signature verification failed: " + e.getMessage());
            return false;
        }
    }

    // Helper to get string representation of a key
    public static String getStringFromKey(java.security.Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
}
