import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.ArrayList;
import java.io.FileInputStream;

//use interfaces if expanding this program to patristocrats

public class Generator{
    /*public static void main(String[]args){
        try{
            Cipher c1 = generate("xeno1.dat",1);
            System.out.println(c1); System.out.println();
            System.out.println(c1.solution());
        } catch (FileNotFoundException e){
            System.out.println("Use a correct filename!");
        }
    }*/

    public static Xenocrypt generate(String filename) throws FileNotFoundException{
        File file = new File(filename);
        Scanner s = new Scanner(new FileInputStream(file));
        ArrayList<String> lines = new ArrayList<>();
        while (s.hasNextLine()){
            lines.add(s.nextLine());
        }
        Random r = new Random();
        String line = lines.get(Math.abs(r.nextInt(lines.size())));
        String lineInsert = line.toUpperCase();
        Xenocrypt cip = new Xenocrypt(lineInsert); //chooses a random line, will diversify to patristos later?
        s.close();
        return cip;
    }

    public static Xenocrypt generate(String filename, int seed) throws FileNotFoundException{
        File file = new File(filename);
        Scanner s = new Scanner(new FileInputStream(file));
        ArrayList<String> lines = new ArrayList<>();
        while (s.hasNextLine()){
            lines.add(s.nextLine());
        }
        Random r = new Random(seed);
        String line = lines.get(Math.abs(r.nextInt(lines.size())));
        String lineInsert = line.toUpperCase();
        Xenocrypt cip = new Xenocrypt(lineInsert, seed); //chooses a random line, will diversify to patristos later?
        s.close();
        return cip;
    }
}