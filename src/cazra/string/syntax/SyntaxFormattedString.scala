package cazra.string.syntax

import java.awt.Color
import scala.collection.mutable.ListBuffer

/** 
 * A structure containing syntax coloring data for a multi-lined string.
 * @param src       The uncolored String.
 * @param starts    List of color start indices (inclusive) for the string.
 * @param colors    List of colors associated with each index in starts.
 * @param ends      List of color end indices (exclusive) for the string.
 */
class SyntaxFormattedString(val src : String, val starts : ListBuffer[Int], val colors : ListBuffer[Color], val ends : ListBuffer[Int]) {
  
  val rawLines = src.split("\n", -1)
  
  /** The list of colored lines making up this string. */
  val lines = new ListBuffer[SyntaxFormattedLine]
  
  /** The number of characters in the longest line. (assuming characters are monospaced) */
  var _width = 0
  def width : Int = _width
  
  
  def _init : Unit = {
    var colorStack = new collection.mutable.Stack[Color]
    var curColor = Color.BLACK
    var chompedChars = 0
    
    val startsCpy = starts.clone
    val endsCpy = ends.clone
    val colorsCpy = colors.clone
    
    /** Changes the current color if our current index in the text requires it. */
    def _checkForNewColor(pos : Int) : Unit = {
      // are we at a color end?
      if(!endsCpy.isEmpty && pos == endsCpy(0) - chompedChars) {
        curColor = colorStack.pop

        // get next color end
        val thisEnd = endsCpy(0)
        endsCpy.remove(0)
      } // endif
      
      // are we at a color start?
      if(!startsCpy.isEmpty && pos == startsCpy(0) - chompedChars) {
        val newColor = colorsCpy(0)
        colorStack = colorStack.push(curColor)
        curColor = newColor
        
        // get next color start
        val thisStart = startsCpy(0)
        startsCpy.remove(0)
        
        // get next color
        colorsCpy.remove(0)
      } // endif
    }
    
    
    for(i <- 0 until rawLines.size) {
      var j : Int = 0
      val curLine = rawLines(i)
      
      if(curLine.size > _width)
        _width = curLine.size
      
      val resultLine = new SyntaxFormattedLine(curLine)
      lines += resultLine
      
      // process the characters in the current line to form SyntaxFormattedLines.
      _checkForNewColor(j)    
      while(j < curLine.size) {       
        // figure out the indices for the next colored substring.
        var k = curLine.size
        val nextStart = if(startsCpy.isEmpty) 
                          -9999 
                        else 
                          startsCpy(0) - chompedChars
        val nextEnd = if(endsCpy.isEmpty) 
                        -9999 
                      else 
                        endsCpy(0) - chompedChars
        if(nextStart >= j && nextStart < k) 
          k = nextStart
        if(nextEnd >= j && nextEnd < k) 
          k = nextEnd
        
        // use the indices we obtained to create a substring of the current 
        // string and store it with our current color.
        val substr = curLine.substring(j,k)
        val coloredSubstr = new ColoredSubstring(substr, curColor)
        
        resultLine.substrings += ((j, coloredSubstr))

        j = k
        _checkForNewColor(j)
      } // endwhile
      
      chompedChars += curLine.size+1
    }
    
  }
  
  /** Returns the original uncolored string. */
  override def toString : String = src
  
  _init
}