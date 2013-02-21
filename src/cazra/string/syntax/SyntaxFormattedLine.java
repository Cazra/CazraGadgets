package cazra.string.syntax;

import cazra.tuples.Pair;

import java.awt.Color;
import java.util.ArrayList;

/** 
 * A single line in a SyntaxFormattedString. 
 * @param line  The uncolored single-line string.
 */
class SyntaxFormattedLine {
  
  /** The text of this line, minus the newline character. */
  public String line;
  
  /** The list of index, colored substring tuples making up this line. */
  public ArrayList<Pair<Integer, ColoredSubstring>> substrings;
  
  /** 
   * A single line in a SyntaxFormattedString. 
   * @param line  The uncolored single-line string.
   */
  public SyntaxFormattedLine(String line) {
    this.line = line;
    substrings = new ArrayList<Pair<Integer, ColoredSubstring>>();
  }
  
  
  public String toString() {
    return line;
  }
} 
