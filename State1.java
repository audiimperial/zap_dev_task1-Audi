import java.util.*;

public class State1 extends State {

    @Override
    public ResultState process(String input, String command) {
        String[] commands = CommandList.getCommands();
        String[] keys = CommandList.getKeys();
        String[] entries = input.split(" ");
        String[] modifiedEntries = Arrays.copyOfRange(entries, 1, entries.length);
        ResultState resultState = new ResultState();
        
        String list = "List of Request Commands: ";
        for (String str : commands) {
            list = list + "\n" + str;
        }

        resultState.setNextMessage(list);
        resultState.setCommand("");
        resultState.setNextState(0);
	    
	    if (Arrays.asList(keys).contains(entries[0].toUpperCase())) {
        	for (int i = 0; i < keys.length; i++) {
    			if(entries[0].toUpperCase().equals(keys[i])) {
    				entries[0] = commands[i];
    			}
    		}
        	int m = CommandList.validateKeywords(entries[0], modifiedEntries);
        	if (modifiedEntries.length == m) {
                Main.setData(modifiedEntries);
                resultState.setNextState(3);
                resultState.setCommand(input);
                resultState.setNextMessage("Confirming request to " + input + ".");
            } else if (modifiedEntries.length < m){
                resultState.setNextState(2);
                resultState.setCommand(entries[0]);
                resultState.setNextMessage("You lack a field kindly re-enter the requested information.");
            } else {
                resultState.setNextState(2);
                resultState.setCommand(entries[0]);
                resultState.setNextMessage("Invalid input. Please try again.");
            }
        }
        else if (Arrays.asList(commands).contains(input.toLowerCase())) {
            resultState.setNextState(1);
            resultState.setCommand(input.toLowerCase());
            resultState.setNextMessage("I would like to confirm if you wish to " + input.toLowerCase() + ".");
        }

        return resultState;
    }


}
