import java.util.List;
import java.util.ArrayList;
import java.util.Date;

public class BlockChain {
	public String hash;
	public String previousHash; 
	private String data; 				//our data will be a simple message.
	private long timeStamp; 			//as number of milliseconds since 1/1/1970.

	public static void main(String[] args) {
		List<BlockChain> blockChain = new ArrayList<BlockChain>();
		
		blockChain.add(new BlockChain("Example Chain 1", ""));
		blockChain.add(new BlockChain("Example Chain 2", blockChain.get(0).hash));
		blockChain.add(new BlockChain("Example Chain 3", blockChain.get(1).hash));
		blockChain.add(new BlockChain("Example Chain 4", blockChain.get(2).hash));
		blockChain.add(new BlockChain("Example Chain 5", blockChain.get(2).hash));

		String hash = blockChain.get(0).hash;
		for(int i=1; i < blockChain.size(); i++) {
			System.out.println(blockChain.get(i).data);
			if(hash != blockChain.get(i).previousHash) {
				System.out.println("\tinValid hash");
			} else if(blockChain.get(i).hash.equals(blockChain.get(i).calculateHash())) {
				System.out.println("\tValid");
			} else {
				System.out.println("\tinValid");
			}
			hash = blockChain.get(i).hash;
		}
	}

	public BlockChain(String data, String previousHash) {
		this.data = data;
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		this.hash = calculateHash(); 				//Making sure we do this after we set the other values.
	}
	
	public String calculateHash() {
		String calculatedhash = Encrypt.applySha256(previousHash + Long.toString(timeStamp) + data);
		return calculatedhash;
	}
	
}