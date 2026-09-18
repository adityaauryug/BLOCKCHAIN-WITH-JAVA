import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LedgerCore {
    
    public static LinkedList<NodeBlock> mainChain = new LinkedList<>();
    public static List<TxData> pendingTransactions = new ArrayList<>(); // Mempool
    public static int miningTarget = 4;
    public static float miningReward = 50f;

    public static void main(String[] args) {
        System.out.println("Starting advanced blockchain project with Wallets and Crypto...");
        
        Wallet systemWallet = new Wallet("System"); // Just to hold genesis funds
        Wallet studentA = new Wallet("StudentA");
        Wallet studentB = new Wallet("StudentB");
        Wallet canteen = new Wallet("Canteen");

        System.out.println("\nWallets generated! Balances:");
        System.out.println("StudentA: " + studentA.getBalance());
        
        // 1. Genesis block (Giving StudentA initial funds from System)
        System.out.println("\n-- Creating Block 0 (Genesis) --");
        NodeBlock gen = new NodeBlock(0, "0");
        TxData genesisTx = new TxData(null, studentA.publicKey, 1000f); 
        gen.insertRecord(genesisTx);
        gen.startMining(miningTarget);
        mainChain.add(gen);
        
        System.out.println("\nStudentA Balance after Genesis: " + studentA.getBalance());
        
        // 2. Making some transactions (sending to mempool)
        System.out.println("\nStudentA tries to send 300 to StudentB");
        processTransaction(studentA.sendFunds(studentB.publicKey, 300f));
        
        System.out.println("StudentA tries to send 50 to Canteen");
        processTransaction(studentA.sendFunds(canteen.publicKey, 50f));
        
        System.out.println("StudentA tries to send 9999 to StudentB (should fail)");
        processTransaction(studentA.sendFunds(studentB.publicKey, 9999f)); // this will fail at wallet level
        
        // 3. Mining the mempool
        System.out.println("\n-- Mining Pending Transactions into Block 1 --");
        minePendingTransactions(studentB); // StudentB mines it to get reward
        
        System.out.println("\nFinal Balances:");
        System.out.println("StudentA: " + studentA.getBalance()); // 1000 - 300 - 50 = 650
        System.out.println("StudentB: " + studentB.getBalance()); // 300 + 50 (reward) = 350
        System.out.println("Canteen: " + canteen.getBalance());   // 50
        
        // validation check
        System.out.println("\nChecking if everything is correct...");
        boolean isGood = validateIntegrity();
        System.out.println("Is chain valid right now? " + isGood);
    }
    
    // Adds to mempool if signature is valid
    public static void processTransaction(TxData tx) {
        if (tx == null) return;
        if (!tx.verifySignature()) {
            System.out.println("--> Alert: Transaction signature failed. Discarding.");
            return;
        }
        pendingTransactions.add(tx);
        System.out.println("Transaction added to Mempool!");
    }
    
    // Mines everything in the mempool into a new block
    public static void minePendingTransactions(Wallet minerWallet) {
        // Add the mining reward as a system transaction
        TxData rewardTx = new TxData(null, minerWallet.publicKey, miningReward);
        pendingTransactions.add(rewardTx);
        
        NodeBlock newBlock = new NodeBlock(mainChain.size(), mainChain.getLast().currentHash);
        
        // move from mempool to block
        for(TxData tx : pendingTransactions) {
            newBlock.insertRecord(tx);
        }
        
        newBlock.startMining(miningTarget);
        mainChain.add(newBlock);
        
        // clear mempool
        pendingTransactions.clear();
    }

    public static boolean validateIntegrity() {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<miningTarget; i++) {
            sb.append('0');
        }
        String target = sb.toString();
        
        for(int i = 1; i < mainChain.size(); i++) {
            NodeBlock curr = mainChain.get(i);
            NodeBlock prev = mainChain.get(i-1);
            
            // Verify all transaction signatures inside the block
            for (TxData tx : curr.recordList) {
                if (!tx.verifySignature()) {
                    System.out.println("--> Alert: Signature on Tx(" + tx.signatureId + ") is INVALID in block " + i);
                    return false;
                }
            }
            
            String calcRoot = HashGenerator.computeRootHash(curr.recordList);
            if(!curr.rootTreeHash.equals(calcRoot)) {
                System.out.println("--> Alert: data changed in block " + i);
                return false;
            }
            
            if(!curr.currentHash.equals(curr.generateBlockHash())) {
                System.out.println("--> Alert: block hash is wrong for block " + i);
                return false;
            }
            
            if(!prev.currentHash.equals(curr.prevBlockId)) {
                System.out.println("--> Alert: chain link broken at block " + i);
                return false;
            }
            
            if(!curr.currentHash.substring(0, miningTarget).equals(target)) {
                System.out.println("--> Alert: mining wasn't done for block " + i);
                return false;
            }
        }
        
        return true; 
    }
}
