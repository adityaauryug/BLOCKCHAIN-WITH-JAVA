# Blockchain Project Report

Building this project was a really good way for me to figure out how cryptocurrencies actually store data securely. Instead of just reading about it, I wanted to code the basic parts myself in Java.

## How the Code is Set Up
I split my code into a few different files so it wasn't just one giant mess:

- **NodeBlock.java:** This is the actual block. It holds a list of transactions, a timestamp, and its own hash. It also has the mining loop where it guesses numbers (the nonce) until it gets a hash starting with zeros.
- **TxData.java:** This just represents a transaction (who is paying who and how much). It generates a unique signature for every transaction by hashing the details together.
- **HashGenerator.java:** I put the SHA-256 logic in here so I could reuse it easily. It also has the code to build a Merkle Root. Basically it takes all the transaction hashes and hashes them together in pairs until there is only one hash left.
- **LedgerCore.java:** This is the main file that runs everything. It puts the blocks into a LinkedList and runs the validation checks.

## Security Features (The Hack Simulation)
The coolest part of the project is testing the security. The system uses a Proof-of-Work target (I set it to 4 zeros) to make mining take some actual time, which prevents spam.

To prove that the chain is immutable, I wrote a test in the `LedgerCore.java` file. After the chain is built normally, the code goes back into the first block and changes a transaction amount from 300 to 9999. 

When the validation loop runs again, it notices the data changed because it recalculates the Merkle Root. Since the new Merkle Root doesn't match the old one, it flags an error. Even if someone tried to recalculate that block's hash, it would break the link to the next block, so the whole chain falls apart and the hack is caught.

## Thoughts on the Project
This relates perfectly to what we are learning about decentralized systems. It shows that by just using basic cryptography and a linked list structure, we can create a database where nobody can cheat or secretly change records. It was a bit tricky to get the Merkle tree pairing right, but seeing the hack get rejected by the validation loop was pretty satisfying.
