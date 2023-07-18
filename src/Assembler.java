import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class Assembler {
    public static void main(String[] args) throws Exception {
        SymbolTable symbolTable = new SymbolTable();
        String assemblyFileName = "stuff.asm";
        String binaryFileName = "stuff.hack";
        Parser parser = new Parser(assemblyFileName);
        try{
            firstPass(parser, symbolTable);
            secondPass(parser, symbolTable, binaryFileName);

        }catch (Exception e){
            handleError(e.getMessage());
        }
    }

    private static void firstPass(Parser parser, SymbolTable symbolTable) throws Exception {
        while (parser.hasMoreCommands()){
            parser.advance();
            if (parser.getCommandType() == Command.L_COMMAND){
                String symbol = parser.getSymbol();
                if (SymbolTable.validName(symbol)) {
                    if (!symbolTable.contains(symbol)){
                        symbolTable.addEntry(symbol, parser.getLineNumber());
                    }
                } else{
                    throw new Exception("invalid symbol!!!!");
                }
            }
        }

    }

    private static void secondPass(Parser parser, SymbolTable symbolTable, String binaryFileName) throws Exception {
        PrintWriter outputStream = null;
        CInstructionMapper cInstructionMapper = new CInstructionMapper();
        int nextAvailableAddress = 16;
        try {outputStream = new PrintWriter(new FileOutputStream(binaryFileName));} catch (FileNotFoundException e) {e.printStackTrace();}
        parser.reset();
        while(parser.hasMoreCommands()){
            parser.advance();
            switch (parser.getCommandType()){
                case A_COMMAND:
                    String symbol = parser.getSymbol();
                    if (symbol.matches("\\d+"))
                        outputStream.println(decimalToBinary(Integer.parseInt(parser.getSymbol())));
                    else{
                        if (symbolTable.contains(symbol))
                            outputStream.println(decimalToBinary(Integer.parseInt(symbolTable.getAddress(symbol))));
                        else {
                            if (!SymbolTable.validName(symbol))
                                handleError("Invalid symbol in this line:\n" + parser.getRawLine());
                            outputStream.println(decimalToBinary(nextAvailableAddress));
                            symbolTable.addEntry(symbol, nextAvailableAddress);
                            nextAvailableAddress++;
                        }
                    }
                    break;
                case C_COMMAND:
                    String cCommand = "111";
                    cCommand += cInstructionMapper.comp(parser.getCompMnemonic());
                    cCommand += cInstructionMapper.dest(parser.getDestMnemonic());
                    cCommand += cInstructionMapper.jump(parser.getJumpMnemonic());
                    outputStream.println(cCommand);
                    break;
                case L_COMMAND:
                    break;
                case NO_COMMAND:
                    handleError("Invalid Syntax in line:\n" + parser.getRawLine());
                    break;
            }
        }
        outputStream.close();
    }

    private static String decimalToBinary(int number){
        String binaryString = Integer.toBinaryString(number);
        int zerosToAdd = 16 - binaryString.length();
        for (int i = 0; i < zerosToAdd ; i++) {
            binaryString = "0" + binaryString;
        }
        return binaryString;
    }

    private static void handleError(String message){
        System.out.println("ERROR");
        System.out.println(message);
        System.exit(-1);
    }
}
