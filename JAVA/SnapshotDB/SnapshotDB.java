import java.util.*;

public class SnapshotDB {

	/**-
	 * Structure:
	 *   Snapshots
	 *      ^
	 *     S1 -> entryList<ent1, ent2, ent3...>
	 *      |               |
	 *      |        (key,valueList<Object>)
	 *      |             
	 *     S2 -> entryList<ent1, ent2, ent3...>
	 *     ...
	 */
	List<Entry> entryList = new LinkedList<Entry>();
	List<Snapshot> snapshots = new ArrayList<Snapshot>();
	int numBackups = 0;

	final String welcome = "Welcome to use this super simple snapshot database system.\n"
			+ "Enter HELP to get command lists.\n"
			+ "Enter BYE to exit";
	final String[] cmdStr = { "HELP", "BYE", "LIST KEYS", "LIST ENTRIES", "LIST SNAPSHOTS", "GET", "DEL",
			"PURGE", "SET", "PUSH", "APPEND", "PICK", "PLUCK", "POP", "DROP", "ROLLBACK", "CHECKOUT",
			"SNAPSHOT","LEN", "REV", "UNIQ" };
	final String instruction = "BYE   clear database and exit\n" + "HELP  display this help message\n" + "\n"
			+ "LIST KEYS       displays all keys in current state\n"
			+ "LIST ENTRIES    displays all entries in current state\n"
			+ "LIST SNAPSHOTS  displays all snapshots in the database\n" + "\n"
			+ "GET <key>    displays entry values\n" + "DEL <key>    deletes entry from current state\n"
			+ "PURGE <key>  deletes entry from current state and snapshots\n" + "\n"
			+ "SET <key> <value ...>     sets entry values\n"
			+ "PUSH <key> <value ...>    pushes each value to the front one at a time\n"
			+ "APPEND <key> <value ...>  append each value to the back one at a time\n" + "\n"
			+ "PICK <key> <index>   displays entry value at index\n"
			+ "PLUCK <key> <index>  displays and removes entry value at index\n"
			+ "POP <key>            displays and removes the front entry value\n" + "\n"
			+ "DROP <id>      deletes snapshot\n"
			+ "ROLLBACK <id>  restores to snapshot and deletes newer snapshots\n"
			+ "CHECKOUT <id>  replaces current state with a copy of snapshot\n"
			+ "SNAPSHOT       saves the current state as a snapshot\n" + "\n"
			+ "\n" + "REV <key>   reverses order of entry values\n"
			+ "UNIQ <key>  removes repeated adjacent entry values\n";

	private class Command {
		String instr;
		// Some attributes may not be given by users
		int index;
		int id;
		Object key;
		List<Object> values;
	}
	
	/*****HELPER METHODS*****/
	public Entry getEntry(Object key){
		for(Entry e : entryList)
			if(e.getKey().equals(key))
				return e;
		return null;
	}
	
	public void printValues(Entry e){
		List<Object> values = e.getValues();
		int size = values.size();
		System.out.print("[");
		for(Object o : values)
			if(values.indexOf(o) == size - 1)
				System.out.print(o);
			else
				System.out.print(o + " ");
		System.out.println("]");
	}
	
	/******FUNCTION METHODS******/
	// LIST KEYS
	public void printKeys(){
		if(entryList.isEmpty()){
			System.out.println("no keys\n");
			return;
		}
		
		for(Entry e : entryList)
			System.out.println(e.getKey());
	}
	// LIST ENTRIES
	public void printEntries(List<Entry> entlst){
		if(entlst.isEmpty()){
			System.out.println("no entries\n");
			return;
		}
		
		for(Entry e : entlst){
			System.out.print(e.getKey() + " ");
			printValues(e);
		}
		System.out.println();
	}
	// LIST SNAPSHOTS
	public void printSnapshots(){
		if(snapshots.isEmpty()){
			System.out.println("no snapshots\n");
			return;
		}
		
		for(int i = 1; i <= numBackups; ++i){
			System.out.println("Snapshot: " + i);
			System.out.println("Timestamp: " + snapshots.get(i - 1).getTimestamp());
			printEntries(snapshots.get(i - 1).getEntrylist());
			System.out.println();
		}
	}
	// GET
	public void showEntry(Command cmd){
		printValues(getEntry(cmd.key));
		System.out.println();
	}
	// DEL
	public void deleteEntry(Command cmd){
		// list.remove() doesn't throw nullpointerexception so we force it to do so.
		Entry ent = getEntry(cmd.key);
		if(ent == null) throw new NullPointerException();
		entryList.remove(ent);
	}
	// PURGE
	public void clearData(Command cmd){
		deleteEntry(cmd);
		Entry ent = getEntry(cmd.key);
		for(Snapshot snapshot: snapshots){
			for(Entry e: snapshot.getEntrylist()){
				if(e.getKey().equals(ent.getKey()))
					snapshot.getEntrylist().remove(e);
			}
		}
	}
	// SET
	public void setEntry(Command cmd){
		Entry ent = new Entry(cmd.key, cmd.values);
		entryList.add(ent);
		System.out.println("ok\n");
	}
	// PUSH
	public void addValuesFront(Command cmd){
		Entry ent = getEntry(cmd.key);
		ent.getValues().addAll(0, cmd.values);
		System.out.println("ok\n");
	}
	// APPEND
	public void appendValues(Command cmd){
		Entry ent = getEntry(cmd.key);
		ent.getValues().addAll(cmd.values);
		System.out.println("ok\n");
	}
	// PICK
	public void showThisValue(Command cmd){
		Entry ent = getEntry(cmd.key);
		System.out.println(ent.getValues().get(cmd.index) + "\n");
	}
	// PLUCK
	public void removeEntryValue(Command cmd){
		Entry ent = getEntry(cmd.key);
		System.out.println(ent.getValues().remove(cmd.index) + "\n");	
	}
	// DROP
	public void deleteSnapshot(Command cmd) throws InvalidArgumentException{
		if(cmd.id > numBackups)
			throw new InvalidArgumentException("index out of range");
		snapshots.remove(cmd.id - 1);
		--numBackups;
		System.out.println("ok\n");
	}
	// POP
	public void removeFrontValue(Command cmd){
		getEntry(cmd.key).getValues().remove(0);
		System.out.println("ok\n");
	}
	// LEN
	public void showLength(Command cmd){
		System.out.println(getEntry(cmd.key).getValues().size() + "\n");
	}
	// REV
	public void reverseValues(Command cmd){
		getEntry(cmd.key).reverseValues();
		System.out.println("ok\n");
	}
	// UNIQ
	public void removeRepeats(Command cmd){
		// Short but may be an inefficient way...
		// Convert value list to Set to eliminate duplicates, and then convert back to list. 
		Entry ent = getEntry(cmd.key);
		Set<Object> helper = new LinkedHashSet<Object>(ent.getValues());
		ent.setValues(new LinkedList<Object> (helper));	
		System.out.println("ok\n");
	}
	// SNAPSHOT
	public void save(){
		++numBackups;
		List<Entry> copy = new LinkedList<Entry>(entryList);
		Snapshot backup = new Snapshot(new Date().toString(), copy);
		snapshots.add(backup);
		System.out.println("saved as snapshot " + numBackups + "\n");
	}
	//CHECKOUT
	public void restoreData(Command cmd) throws InvalidArgumentException{
		if(cmd.id > numBackups)
			throw new InvalidArgumentException("no such snapshot id");
		
		entryList.clear();
		for(Entry e : snapshots.get(cmd.id - 1).getEntrylist()){
			entryList.add(e);
		}
		System.out.println("ok\n");
	}
	// ROLLBACK
	public void undoSnapshot(Command cmd) throws InvalidArgumentException{
		if(cmd.id != numBackups)
			snapshots.removeAll(snapshots.subList(cmd.id, numBackups - 1));
		restoreData(cmd);
	}

	/** COMMAND FETCHING AND PACAKING */
	public void execute(Command cmd) {
		try{
			switch(cmd.instr){
			case "LIST KEYS": printKeys(); break;
			case "LIST ENTRIES": printEntries(entryList); break;
			case "LIST SNAPSHOTS": printSnapshots();  break;
			case "GET": showEntry(cmd); break;
			case "DEL": deleteEntry(cmd); break;
			case "PURGE": clearData(cmd); break;
			case "SET": setEntry(cmd); break;
			case "PUSH": addValuesFront(cmd); break;
			case "APPEND": appendValues(cmd);  break;
			case "PICK": showThisValue(cmd); break;
			case "PLUCK": removeEntryValue(cmd); break;
			case "POP": removeFrontValue(cmd); break;
			case "DROP": deleteSnapshot(cmd); break;
			case "ROLLBACK": undoSnapshot(cmd); break;
			case "CHECKOUT": restoreData(cmd); break;
			case "SNAPSHOT": save(); break;
			case "LEN": showLength(cmd); break;
			case "REV": reverseValues(cmd); break;
			case "UNIQ": removeRepeats(cmd); break;
			default:
				break;
			}		
		} catch (NullPointerException e){
			System.out.println("no such key\n");
		} catch (IllegalArgumentException e){
			System.out.println("index out of range\n");
		} catch (IndexOutOfBoundsException e) {
			System.out.println("index out of range\n");
		} catch (InvalidArgumentException e){
			System.out.println(e.getMessage() + "\n");
		}
	}

	public String find_instr(String instr) {
		// Actually verifies if instr is a or part of a valid instruction.
		for (int i = 0; i < cmdStr.length; ++i) {
			if (instr.equals(cmdStr[i]))
				return instr;
		}
		// Otherwise signs it is invalid.
		return "INVALID";
	}

	public boolean canParseArgument(String[] input, Command cmd) {
		// First deal with LIST_XX cases
		String instr;
		if (input[0].equals("LIST") && input.length == 2) {
			String tmp = input[0] + " " + input[1];
			cmd.instr = find_instr(tmp);
			if (cmd.instr.equals("INVALID")){
				System.out.println("Invalid Command\n");
				return false;
			}
				
			return true;
		}

		// Then deal with others
		cmd.instr = find_instr(input[0]);
		if (cmd.instr.equals("INVALID")) {
			System.out.println("Invalid Command\n");
			return false;
		} else {
			try{
				switch (cmd.instr){
				case "GET":  case "DEL":  case "PURGE":
				case "POP":  case "LEN":  
				case "REV":  case "UNIQ": 
					// Avoid invalid arguments such as. "GET h balasdafas" or "GET"
					if(input.length != 2) {
						System.out.println("no such key\n");
						return false;
					} else {
						cmd.key = input[1];
					}	
					break;
				
				case "SET": case "PUSH": case "APPEND":
					cmd.key = input[1];
					cmd.values = new LinkedList<Object>();
					int len = input.length;
					for(int i = 0; i < len - 2; ++i)
						cmd.values.add(input[i + 2]);
					break;
				
				case "PICK": case "PLUCK":
					cmd.key = input[1];
					cmd.index = Integer.parseInt(input[2]);
					break;
				
				case "DROP": case "ROLLBACK": case "CHECKOUT":
					cmd.id = Integer.parseInt(input[1]);
					break;
				
				default:
					break;
				}
			} catch (NumberFormatException e){
				System.out.println("Please enter integer index/id \n");
				return false;
			} catch (ArrayIndexOutOfBoundsException e){
				System.out.println("Key/ID is missing\n");
				return false;
			}
		
			return true;
		}
	}

	public static void main(String[] args) {
		SnapshotDB db = new SnapshotDB();
		Scanner sc = new Scanner(System.in);
		String input;
		System.out.println(db.welcome);
		
		label: while (true) {
			System.out.print("> ");

			if ((input = sc.nextLine()).isEmpty()) {
				System.out.println("invalid input");
				continue;
			}

			Command cmd = db.new Command();
			String[] parts = input.split(" ");
			if(!db.canParseArgument(parts, cmd)) continue;
			
			
			switch (cmd.instr) {
			case "HELP":
				System.out.println(db.instruction);
				break;
			case "BYE":
				System.out.println("bye");
				break label;
			default:
				db.execute(cmd);
				break;
			}
			continue;
		}
	}
}
