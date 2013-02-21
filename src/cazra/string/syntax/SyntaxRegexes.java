package cazra.string.syntax;

import java.util.List;
import java.util.Map;
import java.util.regex.*;

/** Provides regexes and some other useful data for performing syntax coloring. */
public abstract class SyntaxRegexes {
    /** List of this syntax's regexes in the order of their priority. */
    public List<Pattern> rList;
    
    /** A mapping of regexes to web colors ("#" followed by the color's hex value) */
    public Map<Pattern, String> colorMap;
    
    /** 
     * A mapping of regexes to their recursive subgroup index (if any). 
     * If the regex doesn't have a recursive subgroup, map it to -1. 
     * Currently, this only supports one recursive subgroup per regex.
     */
    public Map<Pattern, Integer> recursableMap;
}


