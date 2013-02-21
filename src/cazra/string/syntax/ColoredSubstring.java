package cazra.string.syntax;

import java.awt.Color;

/** 
 * A single-colored substring of a SyntaxFormattedLine. 
 */
public class ColoredSubstring {
  
  /** The text of this substring. */
  public String str;
  
  /** The color of this substring. */
  public Color color;
  
  /** 
   * A single-colored substring of a SyntaxFormattedLine. 
   * @param str     The uncolored substring.
   * @param color   The color for this substring.
   */
  public ColoredSubstring(String str, Color color) {
    this.str = str;
    this.color = color;
  }
  
  public String toString() {
    return str;
  }
} 