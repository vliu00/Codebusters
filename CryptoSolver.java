//API : http://mabe02.github.io/lanterna/apidocs/2.1/
import com.googlecode.lanterna.terminal.Terminal.SGR;
import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.input.Key.Kind;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.Terminal.Color;
import com.googlecode.lanterna.terminal.TerminalSize;

import java.io.FileNotFoundException;

import com.googlecode.lanterna.LanternaException;
import com.googlecode.lanterna.input.CharacterPattern;
import com.googlecode.lanterna.input.InputDecoder;
import com.googlecode.lanterna.input.InputProvider;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.input.KeyMappingProfile;

import java.util.ArrayList;


public class CryptoSolver {
    public static Terminal terminal;
    public static Keyy keyy;
    public static int x = 10;
    public static int y = 10;
    public static String crypto;
    public static ArrayList<CryptoCharacter> CryptoCharacters = new ArrayList<CryptoCharacter>();
    public static ArrayList<CryptoCharacter> menuCryptChars = new ArrayList<CryptoCharacter>();
    public static String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static String lowercaseLetters = letters.toLowerCase();
    public static CryptoCharacter currentCryptoChar;
    public static String desiredChar;
    public static int cursorX;
    public static boolean isBufferOn = false;

    public static void putString(int r, int c, String s){
        terminal.moveCursor(r,c);
        for(int i = 0; i < s.length();i++){
            terminal.putCharacter(s.charAt(i));
        }
    }

    public static void putString(int r, int c,
            String s, Terminal.Color forg, Terminal.Color back ){
        terminal.moveCursor(r,c);
        terminal.applyBackgroundColor(forg);
        terminal.applyForegroundColor(Terminal.Color.BLACK);

        for(int i = 0; i < s.length();i++){
            terminal.putCharacter(s.charAt(i));
        }
        terminal.applyBackgroundColor(Terminal.Color.DEFAULT);
        terminal.applyForegroundColor(Terminal.Color.DEFAULT);
    }

    public static void putCryptoOnScreen(Xenocrypt cipher){
        keyy = cipher.key();
        crypto = cipher.crypto();
        terminal.applyBackgroundColor(Terminal.Color.MAGENTA);
        for (int i = 0; i < crypto.length(); i++){
            CryptoCharacter cryptoCharInitialize = new CryptoCharacter(x,y,i,crypto.substring(i,i+1));
            CryptoCharacters.add(cryptoCharInitialize);
            terminal.moveCursor(x,y);
            if (!letters.contains(crypto.substring(i,i+1))){
                cryptoCharInitialize.setGuessedChar(crypto.substring(i,i+1));
                terminal.applyBackgroundColor(Terminal.Color.DEFAULT);
                terminal.putCharacter(crypto.charAt(i));
                terminal.applyBackgroundColor(Terminal.Color.MAGENTA);
            } else {
                terminal.putCharacter(' ');
            }
            x++;
        }
        y++;
        x = 10;
        terminal.applyBackgroundColor(Terminal.Color.DEFAULT);
        for (int i = 0; i < crypto.length(); i++){
            terminal.moveCursor(x,y);
            terminal.putCharacter(crypto.charAt(i));
            x++;
        }
        x = 5;
        y = 17;
        //instantiating the menu cryptochars
        for (int i = 0; i < letters.length(); i++){
            menuCryptChars.add(new CryptoCharacter(x, y, i, letters.substring(i,i+1), "0"));
            terminal.moveCursor(x,y);
            terminal.putCharacter(letters.charAt(i));
            x++;
        }
    }

    //pre-condition: the character inserted must be part of the "letters" container
    //pre-condition: x and y are actually on the pink or green background color coordinates
    public static void placeLetter(int x, int y, Key key){
        String desiredChar = null;
        terminal.moveCursor(x,y);
        for (int i = 0; i < CryptoCharacters.size(); i++){
            CryptoCharacter cryptoChar = CryptoCharacters.get(i);
            if (cryptoChar.x() == x && cryptoChar.y() == y){
                desiredChar = cryptoChar.character();
            }
        }
        String replacedLetter = ""; //only to initialize
        String letterBeingDeleted = CryptoCharacters.get(0).character(); //only to initialize
        char letter = key.getCharacter();
        for (int i = 0; i < CryptoCharacters.size(); i++){
            CryptoCharacter cryptoChar = CryptoCharacters.get(i);
            if (cryptoChar.character().equals(desiredChar)){
                terminal.moveCursor(cryptoChar.x(),cryptoChar.y());
                terminal.applyBackgroundColor(Terminal.Color.GREEN);
                if (key.getKind() == Key.Kind.Backspace){
                    terminal.putCharacter(' ');
                    letterBeingDeleted = cryptoChar.guessedChar();
                    cryptoChar.setGuessedChar(" ");
                } else {
                    //checks if letter is already used
                    for (int index = 0; index < menuCryptChars.size(); index++){
                        if (CharToString(letter).equals(menuCryptChars.get(index).character().toLowerCase())){
                            if (menuCryptChars.get(index).guessedChar().equals("1")){
                                terminal.applyForegroundColor(Terminal.Color.RED);
                            }
                        }
                    }
                    //need to make sure that replaced letters also act like they have been removed
                    replacedLetter = cryptoChar.guessedChar();
                    //ok I've dealt with the replaced letters issue
                    terminal.putCharacter(letter);
                    cryptoChar.setGuessedChar(CharToString(letter));
                }
                terminal.applyBackgroundColor(Terminal.Color.DEFAULT);
                terminal.applyForegroundColor(Terminal.Color.DEFAULT);
            }
        }
        //now to indicate which letters have been used
        int menuX = 5;
        int menuY = 17;
        for (int i = 0; i < letters.length(); i++){
            if (key.getKind() == Key.Kind.Backspace){
                if (letterBeingDeleted.equals(lowercaseLetters.substring(i,i+1))){
                    if (menuCryptChars.get(i).guessedChar().equals("1")){
                        terminal.moveCursor(menuX, menuY);
                        terminal.applyForegroundColor(Terminal.Color.DEFAULT);
                        terminal.putCharacter(StringToChar(letterBeingDeleted.toUpperCase()));
                        menuCryptChars.get(i).setGuessedChar("0"); //0 indicates letter is not used
                    } else {
                        int currentGuessedChar = Integer.parseInt(menuCryptChars.get(i).guessedChar());
                        menuCryptChars.get(i).setGuessedChar(""+(currentGuessedChar-1));
                    }
                }
            } else {
                if (lowercaseLetters.charAt(i) == letter){
                    terminal.moveCursor(menuX,menuY);
                    terminal.applyForegroundColor(Terminal.Color.BLUE);
                    terminal.putCharacter(StringToChar(CharToString(letter).toUpperCase()));
                    terminal.applyForegroundColor(Terminal.Color.DEFAULT);
                    int currentGuessedChar = Integer.parseInt(menuCryptChars.get(i).guessedChar());
                    menuCryptChars.get(i).setGuessedChar(""+(currentGuessedChar+1)); //any number > 0 just indicates letter is already used
                }
                if (lowercaseLetters.substring(i,i+1).equals(replacedLetter)){
                    if (menuCryptChars.get(i).guessedChar().equals("1")){
                        terminal.moveCursor(menuX, menuY);
                        terminal.applyForegroundColor(Terminal.Color.DEFAULT);
                        terminal.putCharacter(StringToChar(replacedLetter.toUpperCase()));
                        menuCryptChars.get(i).setGuessedChar("0"); //0 indicates letter is not used
                    } else {
                        int currentGuessedChar = Integer.parseInt(menuCryptChars.get(i).guessedChar());
                        menuCryptChars.get(i).setGuessedChar(""+(currentGuessedChar-1));
                    }
                }
            }
            menuX++;
        }
        isBufferOn = true;
        moveRight();
    }

    //pre-condition: String must be in the "letters" category or its corresponding lowercase category and has to have length 1
    public static char StringToChar(String str){
        if (str.length() != 1){
            return '\0';
        }
        for (int i = 0; i < letters.length(); i++){
            if (letters.substring(i,i+1).equals(str)){
                return letters.charAt(i);
            }
            if (lowercaseLetters.substring(i,i+1).equals(str)){
                return lowercaseLetters.charAt(i);
            }
        }
        return '\0';
    }

    //pre-condition: char must be in the "letters" category or lowercase part of it
    public static String CharToString(char c){
        for (int i = 0; i < letters.length(); i++){
            if (letters.charAt(i) == c){
                return letters.substring(i,i+1);
            }
            if (lowercaseLetters.charAt(i) == c){
                return lowercaseLetters.substring(i,i+1);
            }
        }
        return " ";
    }

    public static void moveRight(){
        if (currentCryptoChar.index() == CryptoCharacters.size() - 1){ //if it's just on the verge of out of bounds
            currentCryptoChar = CryptoCharacters.get(0); //then loop back to beginning
            cursorX = 10;
        } else {
            currentCryptoChar = CryptoCharacters.get(currentCryptoChar.index()+1); //else next cryptochar
            while (!letters.contains(currentCryptoChar.character())){
                if (currentCryptoChar.index() == CryptoCharacters.size() - 1){ //if it's just on the verge of out of bounds
                    currentCryptoChar = CryptoCharacters.get(0); //then loop back to beginning
                    cursorX = 9;
                } else {
                    currentCryptoChar = CryptoCharacters.get(currentCryptoChar.index()+1); //to autoskip spaces
                    cursorX++;
                }
            }
            cursorX++;
        }
        for (int i = 0; i < CryptoCharacters.size(); i++){ //cleans the board of terminal color before replacing stuff with magenta
            CryptoCharacter cleaningCryptoChar = CryptoCharacters.get(i);
            if (letters.contains(cleaningCryptoChar.character())){
                terminal.moveCursor(cleaningCryptoChar.x(), cleaningCryptoChar.y());
                terminal.applyBackgroundColor(Terminal.Color.MAGENTA);
                terminal.putCharacter(StringToChar(cleaningCryptoChar.guessedChar()));
            }
        }
        terminal.moveCursor(currentCryptoChar.x(), currentCryptoChar.y());
        x = currentCryptoChar.x();
        y = currentCryptoChar.y();
        if (letters.contains(currentCryptoChar.character())){
            for (int i = 0; i < CryptoCharacters.size(); i++){ //finding the CryptoCharacter the mouse is hovering over
                CryptoCharacter cryptoChar = CryptoCharacters.get(i);
                if (cryptoChar.x() == x && cryptoChar.y() == y){
                    desiredChar = cryptoChar.character();
                }
            }
            for (int i = 0; i < CryptoCharacters.size(); i++){ //makes all the common CryptoChars green
                CryptoCharacter cryptoChar = CryptoCharacters.get(i);
                if (cryptoChar.character().equals(desiredChar)){
                    terminal.moveCursor(cryptoChar.x(),cryptoChar.y());
                    terminal.applyBackgroundColor(Terminal.Color.GREEN);
                    terminal.putCharacter(StringToChar(cryptoChar.guessedChar()));
                    terminal.applyBackgroundColor(Terminal.Color.DEFAULT);
                }
            }
            terminal.moveCursor(x,y);
            terminal.applyBackgroundColor(Terminal.Color.BLUE);
            terminal.putCharacter(StringToChar(currentCryptoChar.guessedChar()));
        }
    }
    
    public static void main(String[] args) {

        terminal = TerminalFacade.createTerminal();
        terminal.enterPrivateMode();

        TerminalSize size = terminal.getTerminalSize();
        terminal.setCursorVisible(false);

        boolean running = true;
        long startTime =  System.currentTimeMillis();
        Xenocrypt cipher = new Xenocrypt("just to initialize");
        try {
            if (args.length == 1){
                cipher = Generator.generate(args[0]);
                putCryptoOnScreen(cipher);
            }
            if (args.length == 2){
                int seed = Integer.parseInt(args[1]);
                cipher = Generator.generate(args[0], seed);
                putCryptoOnScreen(cipher);
            }
        } catch (FileNotFoundException e){
            System.out.println("use a valid filename!");
        }
        
        boolean toggleTimeDisplay = false;
        long endTime;
        long lastTime = System.currentTimeMillis();
        desiredChar = CryptoCharacters.get(0).character();
        for (int i = 0; i < CryptoCharacters.size(); i++){ //makes all the common CryptoChars green
            CryptoCharacter cryptoChar = CryptoCharacters.get(i);
            if (cryptoChar.character().equals(desiredChar)){
                terminal.moveCursor(cryptoChar.x(),cryptoChar.y());
                terminal.applyBackgroundColor(Terminal.Color.GREEN);
                terminal.putCharacter(' ');
                terminal.applyBackgroundColor(Terminal.Color.DEFAULT);
            }
        }
        terminal.applyBackgroundColor(Terminal.Color.BLUE);
        terminal.moveCursor(10,10);
        terminal.putCharacter(' ');
        terminal.applyBackgroundColor(Terminal.Color.DEFAULT);
        currentCryptoChar = CryptoCharacters.get(0);
        putString(5,5,"Press Tab to toggle on display of time!");
        putString(5,6,"Press Enter to check your solution!");
        cursorX = 10;
        int cursorY = 10;
        String minutes = "";
        boolean messageTiming = false;
        long startMessageTimeMillis = 0; //just to initialize
        System.out.println(cipher.solution()); //will comment out later

        while(running){
            Key key = terminal.readInput();
            
            if (toggleTimeDisplay){
                endTime = System.currentTimeMillis();
                long timer = endTime - startTime;
                timer = timer / 1000;
                if (timer > lastTime){
                    lastTime = timer;
                    minutes = String.valueOf(lastTime / 60);
                    String seconds = String.valueOf(lastTime % 60);
                    if (seconds.length() < 2){
                        seconds = "0" + seconds; //to fix the seconds notation
                    }
                    minutes+= ":"+seconds;
                    terminal.applyBackgroundColor(Terminal.Color.DEFAULT);
                    putString(15,20,minutes);
                }
            }

            if (messageTiming){
                long endMessageTimeMillis = System.currentTimeMillis();
                long diffMessageTimeMillis = endMessageTimeMillis - startMessageTimeMillis;
                if (diffMessageTimeMillis / 1000 > 3){
                    putString(10,20,"                          ");
                    messageTiming = false;
                }
            }

            if (key != null)
            {


                if (key.getKind() == Key.Kind.Tab){
                    if (!toggleTimeDisplay){
                        endTime = System.currentTimeMillis();
                        lastTime = (endTime - startTime) / 1000;
                        toggleTimeDisplay = true;
                    } else {
                        terminal.applyBackgroundColor(Terminal.Color.DEFAULT);
                        putString(15,20,"     ");
                        toggleTimeDisplay = false;
                    }
                }

                if (key.getKind() == Key.Kind.Enter){ //checks the solution
                    endTime = System.currentTimeMillis();
                    String guessedSolution = "";
                    for (int i = 0; i < CryptoCharacters.size(); i++){
                        guessedSolution+= CryptoCharacters.get(i).guessedChar();
                    }
                    if (guessedSolution.toUpperCase().equals(cipher.solution())){
                        terminal.exitPrivateMode();
                        long timer = endTime - startTime;
                        timer = timer / 1000;
                        minutes = String.valueOf(timer / 60);
                        String seconds = String.valueOf(timer % 60);
                        if (seconds.length() < 2){
                            seconds = "0" + seconds; //to fix the seconds notation
                        }
                        minutes+= ":"+seconds;
                        System.out.println("Hooray! You solved it!");
                        System.out.println("Time: "+minutes);
                        System.out.println();
                        System.out.println(cipher.solution());
                    } else {
                        messageTiming = true;
                        startMessageTimeMillis = System.currentTimeMillis();
                        terminal.applyBackgroundColor(Terminal.Color.DEFAULT);
                        terminal.applyForegroundColor(Terminal.Color.DEFAULT);
                        putString(10,20,"Sorry, that's not correct!");
                    }
                }


                if (key.getKind() == Key.Kind.ArrowRight){
                    moveRight();
                }

                if (key.getKind() == Key.Kind.ArrowLeft){
                    if (currentCryptoChar.index() == 0){ //if it's just on the verge of out of bounds
                        currentCryptoChar = CryptoCharacters.get(CryptoCharacters.size() - 1); //then loop back to end
                        cursorX+= CryptoCharacters.size() - 1;
                    } else {
                        currentCryptoChar = CryptoCharacters.get(currentCryptoChar.index()-1); //else last cryptochar
                        while (!letters.contains(currentCryptoChar.character())){
                            currentCryptoChar = CryptoCharacters.get(currentCryptoChar.index()-1); //to autoskip spaces
                            cursorX--;
                        }
                        cursorX--;
                    }
                    for (int i = 0; i < CryptoCharacters.size(); i++){ //cleans the board of terminal color before replacing stuff with blue
                        CryptoCharacter cleaningCryptoChar = CryptoCharacters.get(i);
                        if (letters.contains(cleaningCryptoChar.character())){
                            terminal.moveCursor(cleaningCryptoChar.x(), cleaningCryptoChar.y());
                            terminal.applyBackgroundColor(Terminal.Color.MAGENTA);
                            terminal.putCharacter(StringToChar(cleaningCryptoChar.guessedChar()));
                        }
                    }
                    terminal.moveCursor(currentCryptoChar.x(), currentCryptoChar.y());
                    x = currentCryptoChar.x();
                    y = currentCryptoChar.y();
                    if (letters.contains(currentCryptoChar.character())){
                        for (int i = 0; i < CryptoCharacters.size(); i++){ //finding the CryptoCharacter the mouse is hovering over
                            CryptoCharacter cryptoChar = CryptoCharacters.get(i);
                            if (cryptoChar.x() == x && cryptoChar.y() == y){
                                desiredChar = cryptoChar.character();
                            }
                        }
                        for (int i = 0; i < CryptoCharacters.size(); i++){ //makes all the common CryptoChars green
                            CryptoCharacter cryptoChar = CryptoCharacters.get(i);
                            if (cryptoChar.character().equals(desiredChar)){
                                terminal.moveCursor(cryptoChar.x(),cryptoChar.y());
                                terminal.applyBackgroundColor(Terminal.Color.GREEN);
                                terminal.putCharacter(StringToChar(cryptoChar.guessedChar()));
                                terminal.applyBackgroundColor(Terminal.Color.DEFAULT);
                            }
                        }
                        terminal.moveCursor(x,y);
                        terminal.applyBackgroundColor(Terminal.Color.BLUE);
                        terminal.putCharacter(StringToChar(currentCryptoChar.guessedChar()));
                    }
                }

                if (key.getKind() == Key.Kind.Escape){
                    terminal.exitPrivateMode();
                }

                if (key.getKind() == Key.Kind.Backspace){
                    for (int i = 0; i < CryptoCharacters.size(); i++){
                        CryptoCharacter parsingChar = CryptoCharacters.get(i);
                        if (cursorX == parsingChar.x() && cursorY == parsingChar.y()){
                            if (letters.contains(parsingChar.character())){ //if your current position isn't hovering over an empty space...
                                placeLetter(cursorX, cursorY, key);
                            }
                        }
                    }
                }

                if (lowercaseLetters.contains(CharToString(key.getCharacter()))){
                    isBufferOn = false;
                    for (int i = 0; i < CryptoCharacters.size(); i++){
                        CryptoCharacter parsingChar = CryptoCharacters.get(i);
                        if (cursorX == parsingChar.x() && cursorY == parsingChar.y()){
                            if (letters.contains(parsingChar.character())){ //if your current position isn't hovering over an empty space...
                                if (!isBufferOn){ 
                                    placeLetter(cursorX, cursorY, key);
                                }
                            }
                        }
                    }
                }



            }

        //terminal.applySGR(Terminal.SGR.RESET_ALL);


        }


    }
}