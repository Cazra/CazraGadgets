package cazra.string.syntax;

import cazra.string.StringUtils;
import cazra.tuples.Pair;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

/** 
 * An object for applying syntax formatting to a string using a SyntaxRegexes. 
 * I ported this over to Java from a scala project I developed it for. It worked 
 * in Scala land, but I haven't had time yet to test if this also works in Java 
 * land. Please let me know if it doesn't work.
 */
public class SyntaxFormatter {
  
  /** A string representing an html space. */
  public static String htmlSpace = "&nbsp;";
  
  /** A string representing an html line break. */
  public static String htmlLineBreak = "<br/>";
  
  /** A string representing an html less than. */
  public static String htmlLT = "&lt;";
  
  /** A string representing an html greater than. */
  public static String htmlGT = "&gt;";
  
  /** Used to find <br/> tags in an HTML string. */
  public static Pattern htmlNewLineRegex = Pattern.compile("(<br/>)");
    
  /** Used to find <font> start tags in an HTML string. */
  public static Pattern htmlFontStartRegex = Pattern.compile("(<font.*?>)");
    
  /** Used to find </font> end tags in an HTML string. */
  public static Pattern htmlFontEndRegex = Pattern.compile("(</font>)");
  
  
  
  /** The regex information for the syntax used by this. */
  public SyntaxRegexes regexes;
  
  /** Whether to apply formatting for syntax coloring. */
  public boolean doesColoring;
  
  /** Whether to apply code-style indentation. */
  public boolean doesIndent;
  
  /** A string representing a tab in way that HTML can understand. */
  public String htmlTab = "&nbsp;&nbsp;&nbsp;";
  
  /** A string of spaces mimicing a tab. */
  public String spaceTab = "   ";
  
  /** The number of characters making up a tab. Default 3. */
  public int tabSize = 3;
  
  /** Whether to print the color data for a formatted string. Useful for debugging. */
  public boolean showColorData = false;
  
  
  
  
  
  /** 
   * An object for applying syntax formatting to a string. 
   * @param regexes       The regex information for the syntax used by this.
   * @param doesColoring  Whether to apply formatting for syntax coloring.
   * @param doesIndent    Whether to apply code-style indentation.
   */
  public SyntaxFormatter (SyntaxRegexes regexes, boolean doesColoring, boolean doesIndent) {
    this.regexes = regexes;
    this.doesColoring = doesColoring;
    this.doesIndent = doesIndent;
    
    if(regexes == null) {
      this.doesColoring = false;
    }

    tabSize = spaceTab.length();
  }
  
  public SyntaxFormatter(SyntaxRegexes regexes, boolean doesColoring) {
    this(regexes, doesColoring, false);
  }
  
  public SyntaxFormatter(SyntaxRegexes regexes) {
    this(regexes, true, false);
  }
  
  /** Sets the size of tabs used in the formatted String. */
  public void setTabSize(int size) {
    htmlTab = "";
    spaceTab = "";
    for(int i = 0; i < size; i++) {
      htmlTab += SyntaxFormatter.htmlSpace;
      spaceTab += " ";
    }
    tabSize = size;
  }
  
  /**
   * Consumes the parse string text to construct lists for correct start and end positions
   * for <font color=~~~~> </font> tags to be inserted into the parse string. 
   * @param text      the parse string that is having highlighting applied to it.
   * @param chompedChars  a count of how many characters in txt have already been processed for highlighting.
   * @param starts    an empty list of insertion indices for <font> tags. This will be populated by this method.
   * @param colors    an empty list of web colors coresponding to the indices in starts. This will be populated by this method.
   * @param ends      an empty list of insertion indices for </font> tags. This will be populated by this method.
   * @param depth     The recursive depth of this method call. Right now, nothing is done with it.
   */
  protected void _applyRegexes(String text, int chompedChars, List<Integer> starts, List<String> colors, List<Integer> ends, int depth) {
    // populate our lists of font tag data by having our regular expressions 
    // consume the text until no more regular expressions can be applied to it.
    while(text != "") {
      Pattern bestRegex = null;
      int bestStart = -1;
      int bestEnd = -1;
      int bestRecStart = -1;
      int bestRecEnd = -1;
      
      // iterate over our list of regexes and find the one the can be applied the earliest.
      for(Pattern regex : regexes.rList) {
        // Find the first match for this regex on our text.
        Matcher matcher = regex.matcher(text);
        matcher.find();
        
        // If this regex can be applied earliest, remember it. 
        if(bestStart == -1 || matcher.start() < bestStart) {
          bestRegex = regex;
          bestStart = matcher.start();
          bestEnd = matcher.end();
          
          // if the regex requires recursive formatting, figure out between 
          // what indices to apply it. 
          // Otherwise discard old recursive formatting data.
          int recGroup = regexes.recursableMap.get(bestRegex);
          if(recGroup != -1) {
            bestRecStart = matcher.start(recGroup);
            bestRecEnd = matcher.end(recGroup);
          }
          else {
            bestRecStart = -1;
            bestRecEnd = -1;
          }
        } // endif
      } // endfor
      
      if(bestRegex == null) {
        // No more regexes could be applied to the remaining string. 
        // We are done.
        text = "";
      }
      else {
        // We have determined the best regex that could be applied to the 
        // remaining string. 
        // Add its coloring/index information to our lists.
        String color = regexes.colorMap.get(bestRegex);
        starts.add(chompedChars + bestStart);
        colors.add(color);
        
        // Determine indices of recursive highlighting if needed.
        if(bestRecStart != -1) {
          ends.add(chompedChars + bestRecStart);
          
          _applyRegexes(text.substring(bestRecStart,bestRecEnd), chompedChars + bestRecStart, 
                        starts, colors, ends, depth+1);
          
          starts.add(chompedChars + bestRecEnd);
          colors.add(color);
        }
        
        ends.add(chompedChars + bestEnd);
        
        text = text.substring(bestEnd);
        chompedChars += bestEnd;
      } // endifelse
    } // endwhile
  }
  
  
  /** 
   * Applies linewrapping and indentation (if doesIndent is true) to a string.
   * @param text       the parse string for which we are computing where to insert 
   *                  line breaks and indentations.
   * @param maxCols   the character width of the component the parse string is 
   *                  going to be displayed in. 
   * @return          txt with linewrapping and indentation applied.
   */
  protected String _lineWrap(String text, int maxCols) {
    // consumed text data
    String result = "";
    
    // indentation data
    int indents = 0;
    int maxIndents = maxCols/tabSize - 1;
    
    // Apply line wrapping as long as the remaining text size is longer than 
    // the maximum allowed columns. 
    while(text.length() > maxCols) {
    
      // Apply word wrapping.
      Pair<Integer, Boolean> wwResult = _wordWrap(text, maxCols);
      int breakPt = wwResult._1;
      boolean isNewLine = wwResult._2;
      
      // chomp the characters before the break point. Om nom nom...
      String curLine = text.substring(0, breakPt);
      
      result += curLine;
      if(!isNewLine) {
        result += "\n";
      }
      
      text = text.substring(breakPt);
      
      // Apply code-style indention if this formatter uses it.
      if(doesIndent) {
        // Count the number of indents that will be needed for the next line.
        indents += StringUtils.countChars(curLine, '{');
        indents += StringUtils.countChars(curLine, '(');
        indents += StringUtils.countChars(curLine, '[');
        
        indents -= StringUtils.countChars(curLine, '}');
        indents -= StringUtils.countChars(curLine, ')');
        indents -= StringUtils.countChars(curLine, ']');
        
        indents = Math.max(0,indents);
        
        // If line starts with some number of block end characters }, ), or ] 
        // subtract that from our number of indentations to apply.
        int j = 0;
        while(j < maxCols) {
          if(j >= text.length()) {
            j = maxCols;
          }
          else {
            char ch = text.charAt(j);
            
            if(ch == '}' || ch == ')' || ch == ']') {
              indents -= 1;
              j += 1;
            }
            else {
              j = maxCols;
            }
          }
        } // endwhile
        
        // apply the number of indentations we counted.
        for(int i = 0; i < indents % maxIndents; i++) {
          text = spaceTab + text;
        }
      } // endif
    } // endwhile
    result += text;
    
    return result;
  }

  
  /** 
   * Finds where to insert a line break in a single line for correct word wrapping. 
   * @param txt       This is the line of text in which we are figuring out where 
   *                  to insert a line break that will satisfy word wrapping.
   * @param maxCols   The character width of the component the parse string is 
   *                  going to be displayed in. 
   * @return          A tuple containing the index in txt in which our wordwrap-friendly 
   *                  linebreak will be inserted and a boolean that is true iff
   *                  the linebreak resulted from encountering a \n character.
   */
  protected Pair<Integer, Boolean> _wordWrap(String txt, int maxCols) {
    int index = maxCols;
    boolean foundIt = false;
    
    // if a '\n' exists before maxCols in txt, then just return its index.
    int newLineIndex = txt.indexOf('\n');
    if(newLineIndex != -1 && newLineIndex < maxCols) {
      return new Pair<Integer, Boolean>(newLineIndex+1, true);
    }
    
    // search backwards from maxCols until we reach a nonalphanumeric character.
    while(!foundIt && index > 1) {
      index -= 1;
      foundIt = !_isWordChar(txt.charAt(index));
    }
    
    if(foundIt) 
      return new Pair<Integer, Boolean>(index, false);
    else 
      return new Pair<Integer, Boolean>(maxCols, false);
  }

  
  /** 
   * Checks if a character is an alphanumeric. 
   * @param c    the character we are testing.
   * @return    if c is an alphanumeric character, true. Otherwise false.
   */
  protected boolean _isWordChar(char c) {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9');
  }
  
  
  /** 
   * Applies line/word wrapping, indentation (if doesIndent is true), and
   * syntax coloring (if doesColoring is true) to a String. 
   * @param text        the source string which we are formatting.
   * @param maxCols     The character width of the component the parse string is 
   *                    going to be displayed in. 
   * @return            A SyntaxFormattedString object containing the formatted
   *                    String and its colored components.
   */
  public SyntaxFormattedString format(String text, int maxCols) {
    String result = _lineWrap(text, maxCols);
    
    List<Integer> starts = new ArrayList<Integer>();
    List<String> colors = new ArrayList<String>();
    List<Integer> ends = new ArrayList<Integer>();
    
    try {
      if(doesColoring) {
        _applyRegexes(result, 0, starts, colors, ends, 0);
        if(showColorData) {
          System.err.println("----Color Data----");
          System.err.println("starts: " + starts);
          System.err.println("colors: " + colors);
          System.err.println("ends: " + ends);
        }
      }
    }
    catch (Exception e) {
      // pokemon exception: gotta catchem' all!
      System.err.println("Error during syntax formatting :\n" + text);
    }
    
    List<Color> resultColors = new ArrayList<Color>();
    for(String strColor : colors) {
      resultColors.add(new Color(Integer.parseInt(strColor.substring(1), 16)));
    }
    
    return new SyntaxFormattedString(result, starts, resultColors, ends);
  }
  
  public SyntaxFormattedString format(String text) {
    return format(text, 80);
  }
  
  /** 
   * Applies HTML-style formatting to a parse string for use in an EditorPane. 
   * @param text        The parse string to which we are applying HTML tags 
   *                    for formatting.
   * @param maxCols     The character width of the component the parse string 
   *                    is going to be displayed in. 
   * @return            The parse string with HTML tags inserted for formatting.
   */
  public String htmlFormat(String text, int maxCols) {
    SyntaxFormattedString formattedString = format(text, maxCols);
    
    String resultString = "";
    for(int i = 0; i < formattedString.lines.size(); i++) {
      SyntaxFormattedLine line = formattedString.lines.get(i);
      
      for(Pair<Integer, ColoredSubstring> pair : line.substrings) {
        int j = pair._1;
        ColoredSubstring csubstr = pair._2;
        
        // convert special characters.
        String substr = csubstr.toString();
        substr = substr.replace("<", htmlLT);
        substr = substr.replace(">", htmlGT);
        substr = substr.replace(" ", htmlSpace);
        
        if(doesColoring) {
          resultString += "<font color=\"" + _colorToWebString(csubstr.color) + "\">" + substr + "</font>";
        }
        else {
          resultString += substr;
        }
      }
      
      if(i < formattedString.lines.size()-1)
        resultString += SyntaxFormatter.htmlLineBreak;
    }
    
    return resultString;
  }
  
  public String htmlFormat(String text) {
    return htmlFormat(text, 80);
  }

  protected String _colorToWebString(Color color) {
    return "#" + Integer.toHexString(color.getRGB()).substring(2);
  }
  
  
  /** 
   * Only replaces < > with &lt; &gt; respectively. Also replaces lines breaks with <br/> tags. 
   * @param text    The parse string we are converting to be HTML-friendly.
   * @param maxCols  The character width of the component the parse string is going to be displayed in. 
   * @return       The parse string with < > characters replaced with &lt; &gt; respectively. 
   */
  public String minHTMLFormat(String text, int maxCols) {
    boolean tempDoesIndent = doesIndent;
    doesIndent = false;
    
    boolean tempDoesColoring = doesColoring;
    doesColoring = false;
    
    String resultString = htmlFormat(text, maxCols);
    
    doesIndent = tempDoesIndent;
    doesColoring = tempDoesColoring;
    
    return resultString;
  }
  
  public String minHTMLFormat(String text) {
    return minHTMLFormat(text, 80);
  }
}




