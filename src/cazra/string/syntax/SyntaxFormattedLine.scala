package cazra.string.syntax

import java.awt.Color
import scala.collection.mutable.ListBuffer

/** 
 * A single line in a SyntaxFormattedString. 
 * @param line  The uncolored single-line string.
 */
class SyntaxFormattedLine(val line : String) {
  
  /** The list of index, colored substring tuples making up this line. */
  val substrings = new ListBuffer[(Int, ColoredSubstring)]
  
  override def toString : String = line
} 
