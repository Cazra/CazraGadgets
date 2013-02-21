package cazra.string.syntax;

import cazra.tuples.Pair;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/** 
 * A structure containing syntax coloring data for a multi-lined string.
 */
public class SyntaxFormattedString {
  
  /** The entire uncolored source String. */
  public String src;
  
  /** List of color start indices (inclusive) for the string. */
  public List<Integer> starts;
  
  /** List of colors associated with each index in starts. */
  public List<Color> colors;
  
  /** List of color end indices (exclusive) for the string. */
  public List<Integer> ends;

  /** The list of colored lines making up this string. */
  public List<SyntaxFormattedLine> lines;
  
  /** The number of characters in the longest line. (assuming characters are monospaced) */
  public int width;
  
  // a bunch of private data for doing the syntax coloring work.
  private Stack<Color> colorStack;
  private Color curColor;
  private int chompedChars;
    
  private List<Integer> startsCpy;
  private List<Integer> endsCpy;
  private List<Color> colorsCpy;
  
  /** 
   * A structure containing syntax coloring data for a multi-lined string.
   * @param src       The uncolored String.
   * @param starts    List of color start indices (inclusive) for the string.
   * @param colors    List of colors associated with each index in starts.
   * @param ends      List of color end indices (exclusive) for the string.
   */
  public SyntaxFormattedString(String src, List<Integer> starts, List<Color> colors, List<Integer> ends) {
    this.src = src;
    this.starts = starts;
    this.colors = colors;
    this.ends = ends;
    
    lines = new ArrayList<SyntaxFormattedLine>();
    
    _init();
  }
  
  /** Applies the syntax coloring so that the lines list is useful. */
  protected void _init() {
    String[] rawLines = src.split("\n", -1);
    
    colorStack = new Stack<Color>();
    curColor = Color.BLACK;
    chompedChars = 0;
    
    startsCpy = new ArrayList<Integer>(starts);
    endsCpy = new ArrayList<Integer>(ends);
    colorsCpy = new ArrayList<Color>(colors);
    
    for(int i = 0; i < rawLines.length; i++) {
      int j = 0;
      String curLine = rawLines[i];
      
      if(curLine.length() > width) {
        width = curLine.length();
      }
      
      SyntaxFormattedLine resultLine = new SyntaxFormattedLine(curLine);
      lines.add(resultLine);
      
      // process the characters in the current line to form SyntaxFormattedLines.
      _checkForNewColor(j);
      
      while(j < curLine.length()) {
        // figure out the indices for the next colored substring.
        int k = curLine.length();
        
        int nextStart = -9999;
        if(!startsCpy.isEmpty()) {
          nextStart = startsCpy.get(0) - chompedChars;
        }
        
        int nextEnd = -9999;
        if(!endsCpy.isEmpty()) {
          nextEnd = endsCpy.get(0) - chompedChars;
        }
        
        if(nextStart >= j && nextStart < k) 
          k = nextStart;
        if(nextEnd >= j && nextEnd < k) 
          k = nextEnd;
          
        // use the indices we obtained to create a substring of the current 
        // string and store it with our current color.
        String substr = curLine.substring(j,k);
        ColoredSubstring coloredSubstr = new ColoredSubstring(substr, curColor);
        
        resultLine.substrings.add(new Pair<Integer, ColoredSubstring>(j, coloredSubstr));

        j = k;
        _checkForNewColor(j);
      } // endwhile
      
      chompedChars += curLine.length() + 1;
    }
  }
  
  /** 
   * Uses our lists to decide whether to change colors based on our 
   * current index in a line of the source string. 
   */
  protected void _checkForNewColor(int pos) {
    // are we at a color end?
    if(!endsCpy.isEmpty() && pos == endsCpy.get(0) - chompedChars) {
      curColor = colorStack.pop();

      // get next color end
      endsCpy.remove(0);
    } // endif
    
    // are we at a color start?
    if(!startsCpy.isEmpty() && pos == startsCpy.get(0) - chompedChars) {
      Color newColor = colorsCpy.get(0);
      colorStack.push(curColor);
      curColor = newColor;
      
      // get next color and color start
      startsCpy.remove(0);
      colorsCpy.remove(0);
    } // endif
  }
  
  /** Returns the original uncolored string. */
  public String toString() {
    return src;
  }
}