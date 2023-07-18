import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private int lineNumber = 0;
    private Command commandType;
    ArrayList<String> inputFileList;
    private String cleanLine;
    private String compMnemonic;
    private String destMnemonic;
    private String jumpMnemonic;
    private String rawLine;
    private String symbol;
    private int numLines;

    public Parser(String fileName){
        FileInputStream inputStream = null;
        if (fileName.matches(".+\\.asm")){
            try {
                inputStream = new FileInputStream(fileName);
            } catch(FileNotFoundException e){
                e.printStackTrace();
            }
        }
        else{
            System.out.println("File is not a .asm file.");
            System.exit(-1);
        }
        Scanner inputFile = new Scanner(inputStream);
        inputFileList = new ArrayList<>();
        String line = "";
        while (inputFile.hasNextLine()){
            line = inputFile.nextLine();
            if (!line.matches("\\s*|\\s*//.*"))
                inputFileList.add(line);
        }
        numLines = inputFileList.size();
        try {inputStream.close();} catch (IOException e) {e.printStackTrace();}
        inputFile.close();
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public Command getCommandType(){
        return commandType;
    }

    public String getRawLine(){
        return rawLine;
    }

    public String getCleanLine(){
        return cleanLine;
    }

    public String getCompMnemonic() {
        return compMnemonic;
    }

    public String getDestMnemonic() {
        return destMnemonic;
    }

    public String getJumpMnemonic() {
        return jumpMnemonic;
    }

    public String getSymbol() {
        return symbol;
    }

    public boolean hasMoreCommands(){
        if (lineNumber < inputFileList.size()){
            return true;
        }
        return false;
    }

        public void advance() throws Exception {
            if (hasMoreCommands()){
                rawLine = inputFileList.get(lineNumber);
                cleanLine = cleanRawLine(rawLine);
                parse();
                if (commandType == Command.L_COMMAND){
                    inputFileList.remove(lineNumber);
                }
                else{
                    lineNumber++;
                }

            }
            else{
                System.out.println("There are no more commands in the .asm file.");
            }
        }

    public void reset(){
        lineNumber = 0;
    }

    public String toString(){
        return "lineNumber: " + lineNumber +
                "\ncommandType: " + commandType +
                "\nsymbol: " + symbol +
                "\ncompMnemonic: " + compMnemonic +
                "\ndestMnemonic: " + destMnemonic +
                "\njumpMnemonic: " + jumpMnemonic +
                "\nrawLine: " + rawLine +
                "\ncleanLine: " + cleanLine;
    }

    private void parse() throws Exception {
        parseCommandType();
        switch (commandType){
            case L_COMMAND:
            case A_COMMAND:
                parseSymbol();
                destMnemonic = compMnemonic = jumpMnemonic = "NULL";
                break;
            case C_COMMAND:
                parseComp();
                parseDest();
                parseJump();
                symbol = "NULL";
                break;
            case NO_COMMAND:
                break;
        }
    }

    private void parseCommandType() {
        if (cleanLine.startsWith("@"))
            commandType = Command.A_COMMAND;
        else if (cleanLine.contains("=") || cleanLine.contains(";"))
            commandType = Command.C_COMMAND;
        else if (cleanLine.matches("\\(\\w+\\)"))
            commandType = Command.L_COMMAND;
        else
            commandType = Command.NO_COMMAND;
    }

    private void parseSymbol() {
        if (cleanLine.startsWith("@"))
            symbol = cleanLine.replaceAll("@", "");
        else if (cleanLine.startsWith("("))
            symbol = cleanLine.replaceAll("[\\(\\)]", "");

    }

    private String parseMnemonic(String group) throws Exception {
        String result;
        String pattern0 =
                "(?<dest>MD?|D|A(M|D|MD)?)=?" +
                "(?<comp>0|1|-1|D|A|!D|!A||-D|-A|D+1|A+1|D-1|A-1|D+A|D-A|A-D|D&A|D\\|A|M|!M|-M|M+1|M-1|D+M|D-M|M-D|D&M|D\\|M);?" +
                "(?<jump>J(GT|EQ|GE|LT|NE|LE|MP))?";
        String pattern = "(?<dest>[AMD]{0,3})\\s*=?\\s*(?<comp>[AMD+-01&|]{1,3})\\s*;?\\s*(?<jump>[JLEMPGTQ]{0,3})";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(cleanLine);
        matcher.find();
        try {
            result = matcher.group(group);

        }catch (IllegalStateException e) {
            throw new Exception("Invalid " + group.toUpperCase() + " in line:\n" + rawLine);
        }
        return result.isEmpty() ? "NULL" : result;
    }

    private void parseJump() throws Exception {
        jumpMnemonic = parseMnemonic("jump");
    }

    private void parseDest() throws Exception {
        destMnemonic = parseMnemonic("dest");
    }

    private void parseComp() throws Exception {
        compMnemonic = parseMnemonic("comp");
    }

    private String cleanRawLine(String rawLine) {
        return rawLine.replaceAll("\\s|//.*", "");
    }

}
