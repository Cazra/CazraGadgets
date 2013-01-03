package cazra.string;

public class StringUtils {
  
  /** Counts the number of occurences of a character in a string. */
  public static int countChars(String str, char c) {
    int result = 0;
    
    while(str.length() > 0) {
      if(str.charAt(0) == c)
        result++;
      
      str = str.substring(1);
    }
    
    return result;
  }
  
  public static void main(String[] args) {
    String str = "a.b.c.d.e";
    System.out.println("# '.'s in " + str + ": " + countChars(str, '.'));
  } 
}
