import java.util.LinkedList;

public class LedgerCore {
    
    // using linked list instead of arraylist just to be different
    public static LinkedList<NodeBlock> mainChain = new LinkedList<>();
    public static int miningTarget = 4; // changed from 'difficulty'

    public static void main(String[] args) {
        System.out.println("Starting my blockchain project...");
        
        // 1. Genesis block
        System.out.println("\n-- Creating Block 0 (Genesis) --");
        NodeBlock gen = new NodeBlock(0, "0");
        gen.insertRecord(new TxData("System", "StudentA", 1000f));
        gen.startMining(miningTarget);
        mainChain.add(gen);
        
        // 2. Second block
        System.out.println("\n-- Creating Block 1 --");
        NodeBlock b1 = new NodeBlock(1, gen.currentHash);
        b1.insertRecord(new TxData("StudentA", "StudentB", 300f));
        b1.insertRecord(new TxData("StudentA", "Canteen", 50f));
        b1.startMining(miningTarget);
        mainChain.add(b1);
        
        // 3. Third block
        System.out.println("\n-- Creating Block 2 --");
        NodeBlock b2 = new NodeBlock(2, b1.currentHash);
        b2.insertRecord(new TxData("StudentB", "Library", 25f));
        b2.startMining(miningTarget);
        mainChain.add(b2);
        
        // validation check
        System.out.println("\nChecking if everything is correct...");
        boolean isGood = validateIntegrity();
        System.out.println("Is chain valid right now? " + isGood);
        
        // hacking simulation
        System.out.println("\nWait, simulating a hack where we change StudentA's payment...");
        
        // going back and changing the amount in the first block
        mainChain.get(1).recordList.get(0).transferValue = 9999f;
        
        System.out.println("\nRunning the checker again...");
        boolean isGoodAfterHack = validateIntegrity();
        
        if(!isGoodAfterHack) {
            System.out.println("Awesome, the system caught the hack and threw it out.");
        } else {
            System.out.println("Something went wrong, it didn't catch the hack.");
        }
    }

    public static boolean validateIntegrity() {
        // target string of zeros
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<miningTarget; i++) {
            sb.append('0');
        }
        String target = sb.toString();
        
        // checking the chain
        for(int i = 1; i < mainChain.size(); i++) {
            NodeBlock curr = mainChain.get(i);
            NodeBlock prev = mainChain.get(i-1);
            
            // first check if any data was altered
            String calcRoot = HashGenerator.computeRootHash(curr.recordList);
            if(!curr.rootTreeHash.equals(calcRoot)) {
                System.out.println("--> Alert: data changed in block " + i);
                return false;
            }
            
            // check if hash is still valid
            if(!curr.currentHash.equals(curr.generateBlockHash())) {
                System.out.println("--> Alert: block hash is wrong for block " + i);
                return false;
            }
            
            // check if it links to the previous one
            if(!prev.currentHash.equals(curr.prevBlockId)) {
                System.out.println("--> Alert: chain link broken at block " + i);
                return false;
            }
            
            // check if proof of work was actually done
            if(!curr.currentHash.substring(0, miningTarget).equals(target)) {
                System.out.println("--> Alert: mining wasn't done for block " + i);
                return false;
            }
        }
        
        return true; // everything passed
    }
}
