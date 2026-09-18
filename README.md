# My Java Blockchain Project

For my evaluated project on VITyarthi, I decided to build a simple blockchain using core Java. I didn't want to just copy some library, so I wrote the hashing and block logic myself to really understand how it all connects.

## How to run this on your machine

It's pretty straightforward to run since I didn't use Maven or any external stuff. Just pure Java.
Make sure you have Java installed (I used JDK 8 but newer ones are fine).

1. Open your terminal or powershell.
2. Go to the folder where you downloaded these files:
   `cd path/to/VITYARTHI_Blockchain_Project`
3. Compile all the java files at once:
   `javac *.java`
4. Run the main class which is LedgerCore:
   `java LedgerCore`

## What does it do?
When you run it, the code will start creating blocks (Genesis block, Block 1, etc). For each block, it actually does the mining process (Proof of Work) by looping until it finds a hash that starts with enough zeros.

After making a few blocks and transactions, the program runs a check to make sure the chain is valid. 
Then I added a part where the code acts like a hacker and forcefully changes the amount of money sent in block 1. When it runs the check again, the system catches the error because the Merkle Root changed, which breaks the block hash and the link to the next block. It prints out an alert and rejects the hack.

I think this proves how hard it is to mess with decentralized data once it's on a blockchain.
