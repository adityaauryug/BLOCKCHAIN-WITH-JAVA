import java.util.ArrayList;
import java.util.List;

public class NodeBlock {
    
    public int id;
    public String currentHash;
    public String prevBlockId;
    public String rootTreeHash;
    public List<TxData> recordList = new ArrayList<>();
    
    private long creationTime;
    private int attemptCounter;

    public NodeBlock(int blockId, String lastHash) {
        this.id = blockId;
        this.prevBlockId = lastHash;
        this.creationTime = System.currentTimeMillis();
        this.currentHash = generateBlockHash();
    }

    public String generateBlockHash() {
        // glue everything together and hash it
        String raw = prevBlockId + creationTime + attemptCounter + rootTreeHash;
        return HashGenerator.buildHash(raw);
    }

    // the actual mining part
    public void startMining(int targetDiff) {
        System.out.println("Mining block " + id + "...");
        
        rootTreeHash = HashGenerator.computeRootHash(recordList);
        
        // create a string with 'targetDiff' number of zeros
        StringBuilder targetStr = new StringBuilder();
        for(int i=0; i<targetDiff; i++) {
            targetStr.append('0');
        }
        String target = targetStr.toString();
        
        long tStart = System.currentTimeMillis();
        
        // keep trying new attemptCounters until the hash starts with the target zeros
        while(!currentHash.substring(0, targetDiff).equals(target)) {
            attemptCounter++;
            currentHash = generateBlockHash();
        }
        
        long tEnd = System.currentTimeMillis();
        
        System.out.println("Found hash in " + (tEnd - tStart) + " ms!");
        System.out.println("Block Hash: " + currentHash);
    }

    public void insertRecord(TxData tx) {
        if(tx != null) {
            recordList.add(tx);
            tx.display();
        }
    }
}
