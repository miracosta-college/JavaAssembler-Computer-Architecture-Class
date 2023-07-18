import java.security.AllPermission;
import java.util.HashMap;

public class SymbolTable {
    //CONSTANTS
    private static final String ALL_VALID_CHARS = "_.$:A-Za-z0-9";   //  Regular expression containing all alphanumeric characters
    private static final String INITIAL_VALID_CHARS = "_.$:A-Za-z";   //  Regular expression containing all valid first characters

    //FIELDS
    private HashMap<String, String> symbolTable;

    //CONSTRUCTORS
    SymbolTable(){
        symbolTable  = new HashMap<>();
        symbolTable.put("SP", "0");
        symbolTable.put("LCL", "1");
        symbolTable.put("ARG", "2");
        symbolTable.put("THIS", "3");
        symbolTable.put("THAT", "4");
        symbolTable.put("R0", "0");
        symbolTable.put("R1", "1");
        symbolTable.put("R2", "2");
        symbolTable.put("R3", "3");
        symbolTable.put("R4", "4");
        symbolTable.put("R5", "5");
        symbolTable.put("R6", "6");
        symbolTable.put("R7", "7");
        symbolTable.put("R8", "8");
        symbolTable.put("R9", "9");
        symbolTable.put("R10", "10");
        symbolTable.put("R11", "11");
        symbolTable.put("R12", "12");
        symbolTable.put("R13", "13");
        symbolTable.put("R14", "14");
        symbolTable.put("R15", "15");
        symbolTable.put("SCREEN", "16384");
        symbolTable.put("KBD", "24576");
    }

    //METHODS
    public boolean addEntry(String symbol, int address){
        validName(symbol);
        if (symbolTable.put(symbol, String.valueOf(address)) != null){
            return true;
        }
        else {
            return false;
        }
    }

    public boolean contains(String symbol){
        return symbolTable.containsKey(symbol);
    }

    public String getAddress(String symbol){
        return symbolTable.get(symbol);
    }

    public static boolean validName(String symbol){
        return symbol.matches("[" + INITIAL_VALID_CHARS + "]" + "[" + ALL_VALID_CHARS + "]*");
    }
}
