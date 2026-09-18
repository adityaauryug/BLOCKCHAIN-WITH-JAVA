# vityarthi project - programming in java
## advanced blockchain implementation

**name:** aditya singh
**reg no:** 25BAI11211
**faculty:** dr. chandan kumar behra

---

### 1. what is this project about? (abstract)
so for this project, i decided to build a completely functional blockchain ledger from scratch using just core java. i didn't want to use any pre-built crypto libraries or frameworks like maven because that defeats the purpose of learning how the backend actually works. 

the main goal here was to simulate how real cryptocurrencies handle data. so it has proof-of-work (pow) mining, rsa cryptography for digital signatures, actual wallets that manage your keys, and a mempool for pending transactions. it also checks your balance dynamically so you literally cant double spend.

### 2. how the system is built
the architecture is broken down into a few main java classes so it's not just one massive confusing file:

- **LedgerCore.java:** this is the brain of the network. it manages the linkedlist of blocks and holds the mempool (pending transactions). it also runs the massive validation loop to check if the chain is healthy.
- **Wallet.java:** early on i just used strings for users, but real blockchains use crypto. so i made this class generate a real RSA-1024 public/private keypair using java.security. it calculates your balance by scanning every block in the chain plus whatever is sitting in the mempool.
- **TxData.java:** handles the actual money transfers. when u send money, it takes the hash of the transfer details and signs it with your private key. if the signature is fake, the network just throws it out.
- **NodeBlock.java:** the block itself. it stores a list of transactions, a timestamp, and a nonce. the nonce is what gets looped during the mining process to find a hash with enough leading zeros.
- **HashGenerator.java:** all the messy math is hidden here. it does the sha-256 hashing, base64 conversions for the rsa keys, and builds the merkle root (pairing up transaction hashes until only one is left).

### 3. the mining and mempool mechanics
transactions dont just magically go into a block. when a wallet sends funds, it gets verified and pushed to a pending list (mempool). 

a miner then has to call a function to bundle those pending transactions and do the proof-of-work mining. the mining loop just brute-forces hashes until it finds one that meets the target difficulty. once its mined, it gets added to the chain and the miner gets a 50 coin reward from the system.

### 4. testing the security (hack simulation)
the whole point of a blockchain is that u cant change the data. to prove my code actually enforces this, i wrote a live hack simulation in the main method.

after the chain is built and running normally, i put in a line of code that forcefully reaches back into the first block and changes a student's payment from 300 to 9999. 

when the ledger runs its validation check again, it recalculates the merkle root for that block. because the transaction data was changed, the new merkle root is completely different from what was originally saved. this mismatch breaks the hash of that block, which then breaks the link to every block after it. the program instantly catches the hack and spits out an alert rejecting the whole chain.

### 5. thoughts & conclusion
getting the rsa signatures and the mempool to sync up correctly was probably the hardest part of this. but it was totally worth it. it perfectly demonstrates how decentralized systems stay secure. by using basic cryptography and standard java data structures, it proves u can build a ledger where nobody can secretly alter records.

### 6. references
i used a lot of standard documentation to get the cryptography right without external libraries:

- **java security api guide:** (used this for the rsa keys and signature instances)
  https://docs.oracle.com/javase/8/docs/technotes/guides/security/crypto/CryptoSpec.html
- **sha-256 specs:** (just to understand how the byte arrays are manipulated)
  https://csrc.nist.gov/publications/detail/fips/180/4/final
- **bitcoin whitepaper:** (satoshi's original paper, mostly used this to understand how mempools and merkle trees link together)
  https://bitcoin.org/bitcoin.pdf
