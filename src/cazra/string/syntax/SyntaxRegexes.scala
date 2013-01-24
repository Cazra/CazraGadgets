package cazra.string.syntax

import scala.collection.mutable.ListBuffer
import scala.util.matching._

import ornl.elision.gui._

/** Provides regexes and some other useful data for performing syntax coloring. */
trait SyntaxRegexes {
    /** List of this syntax's regexes in the order of their priority. */
    val rList : List[Regex]
    
    /** A mapping of regexes to web colors ("#" followed by the color's hex value) */
    val colorMap : Map[Regex, String]
    
    /** 
     * A mapping of regexes to their recursive subgroup index (if any). 
     * If the regex doesn't have a recursive subgroup, map it to -1. 
     * Currently, this only supports one recursive subgroup per regex.
     */
    val recursableMap : Map[Regex, Int]
}


