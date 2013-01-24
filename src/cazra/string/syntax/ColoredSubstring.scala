package cazra.string.syntax

import java.awt.Color

/** 
 * A single-colored substring of a SyntaxFormattedLine. 
 * @param str     The uncolored substring.
 * @param color   The color for this substring.
 */
class ColoredSubstring(val str : String, val color : Color) {
  override def toString : String = str
} 
