package st;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

public class Task2_TDD_2 {

	private EntryMap map0;
    private EntryMap map1;
    private EntryMap mapY;
    private EntryMap map5;
    
    private String result;

    private TemplateEngine engine;
    
    private SimpleTemplateEngine simpleEngine;
    
    Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);

    
    @Before
    public void setUp() throws Exception {
        
        result = null;
        
        // 0 entries
        map0 = new EntryMap();
        
        // one entry
        map1 = new EntryMap();
        map1.store("one", "WORD1");
        
        mapY = new EntryMap();
        mapY.store("year", "5 years ago");
            
        // many entries
        map5 = new EntryMap();
        map5.store("one", "WORD1");
        map5.store("tWo", "WORD2");
        map5.store("three", "WORD3");
        map5.store("fou r", "WORD4");
        map5.store("abc", "abcabc");
        
        engine = new TemplateEngine();
        simpleEngine = new SimpleTemplateEngine();
    }
    
    @Test
    public void testYearBasic() {
        
        //Basic years ago test
        result = engine.evaluate("I won the championship in ${year}", mapY, TemplateEngine.DEFAULT);
        assertEquals("I won the championship in " + (currentYear - 5), result);
        
        //Basic in years test
        mapY.update("year", "in 6 years");
        result = engine.evaluate("I will win the championship in ${year}", mapY, TemplateEngine.DEFAULT);
        assertEquals("I will win the championship in " + (currentYear + 6), result);
        
        //Testing garbage string value 
        mapY.update("year", "Not in a lifetime");
        result = engine.evaluate("Will he win the championship? ${year}", mapY, TemplateEngine.DEFAULT);
        assertEquals("Will he win the championship? Not in a lifetime", result);
            
    }
    
    @Test
    public void testBase() {
        
        //Base years ago test
        mapY.store("base_year", "1990");
        mapY.update("year", "3 years ago");
        result = engine.evaluate("I was born in ${year} ${base_year}", mapY, TemplateEngine.ACCURATE_SEARCH);
        assertEquals("I was born in 1987 1990", result);
        
        //Base in years test
        mapY.update("year", "in 6 years");
        result = engine.evaluate("I was born in ${year}", mapY, TemplateEngine.ACCURATE_SEARCH);
        assertEquals("I was born in 1996", result);
        
        //Testing garbage base year values
        mapY.update("year", "20 years ago");
        mapY.update("base_year", "hello there!");
        result = engine.evaluate("I was born in ${year}", mapY, TemplateEngine.DEFAULT);
        assertEquals("I was born in 1998", result);

        //Testing negative base year values
        mapY.update("base_year", "-2009");
        result = engine.evaluate("I was born in ${year}", mapY, TemplateEngine.DEFAULT);
        assertEquals("I was born in 1998", result);
    }
    
    @Test
    public void testNegative() {
                
        //Negative years ago test
        mapY.update("year", "-5 years ago");
        result = engine.evaluate("I won the championship in ${year}", mapY, TemplateEngine.DEFAULT);
        assertEquals("I won the championship in -5 years ago", result);
        
        //Negative in years test
        mapY.update("year", "in -6 years");
        result = engine.evaluate("I will win the championship in ${year}", mapY, TemplateEngine.DEFAULT);
        assertEquals("I will win the championship in in -6 years", result);
       
    }
    
    @Test
    public void testNotNumber() {
                
        //Not Number years ago test
        mapY.update("year", "A years ago");
        result = engine.evaluate("I won the championship in ${year}", mapY, TemplateEngine.DEFAULT);
        assertEquals("I won the championship in A years ago", result);
        
        //Not Number in years test
        mapY.update("year", "in A years");
        result = engine.evaluate("I will win the championship in ${year}", mapY, TemplateEngine.DEFAULT);
        assertEquals("I will win the championship in in A years", result);
    }
	
    @Test
    public void testYearModes() {
    	
    	mapY.delete("year");
        result = engine.evaluate("I won the championship in ${year}", mapY, TemplateEngine.DELETE_UNMATCHED);
        assertEquals("I won the championship in ", result);
        
        mapY.store("YeAr", "in 6 years");
        result = engine.evaluate("I won the championship in ${yeaR}", mapY, TemplateEngine.CASE_SENSITIVE );
        assertEquals("I won the championship in ${yeaR}", result);
        
        result = engine.evaluate("I won the championship in ${yeaR}", mapY, TemplateEngine.CASE_SENSITIVE | 
        		TemplateEngine.DELETE_UNMATCHED);
        assertEquals("I won the championship in ", result);
        
        result = engine.evaluate("I won the championship in ${ye  ar}", mapY, TemplateEngine.BLUR_SEARCH);
        assertEquals("I won the championship in " + (currentYear + 6), result);

        mapY.store("bas e_Ye ar","1980");
        result = engine.evaluate("I won the championship in ${yeaR}", mapY, TemplateEngine.BLUR_SEARCH);
        assertEquals("I won the championship in 1986", result);
        
        mapY.store("year", "in 6 years");
        result = engine.evaluate("I won the championship in ${year}", mapY, TemplateEngine.BLUR_SEARCH |
        		TemplateEngine.CASE_SENSITIVE);
        assertEquals("I won the championship in " + (currentYear + 6), result);
    }
    
    
    @Test
    public void testMultiple() {
    	int correctYear = currentYear - 5;
    	result = engine.evaluate("We won in ${year}. ${year} was lucky for us.", mapY, TemplateEngine.DEFAULT);
        assertEquals("We won in " + correctYear + ". " + correctYear + " was lucky for us." , result);
    }
    
    @Test
    public void testInside() {
    	int correctYear = currentYear - 5;
    	mapY.store("check", "r");
    	result = engine.evaluate("${yea   ${check}}", mapY, TemplateEngine.BLUR_SEARCH);
        assertEquals(String.valueOf(correctYear) , result);
    }
    
    @Test
    public void testYearInside() {
    	mapY.store("base_year", "2015");
    	mapY.store("2010","some year");
    	result = engine.evaluate("${${year}}", mapY, TemplateEngine.BLUR_SEARCH |
        		TemplateEngine.CASE_SENSITIVE);
        assertEquals("some year", result);
    }
    
    @Test 
    public void testShouldNotLoop() {
    	int correctYear = currentYear - 5;
    	mapY.store(String.valueOf(correctYear),String.valueOf(correctYear));
    	result = engine.evaluate(("${year} ${" + correctYear + "}"), mapY, TemplateEngine.BLUR_SEARCH);
        assertEquals(String.valueOf(correctYear) + " " +  String.valueOf(correctYear), result);
    }
    
    /////////////////////////////////////////////////////////////////////////////
    ///         OLD TESTS 								////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    
    
 // 
 	// Combining No. of entries in entryMap with templates in template string.
 	// 
@Test 	
public void testTemplateNullOrEmpty() {
 	// template string is null or empty with EntryMap of variable size
	result = engine.evaluate(null, map0, TemplateEngine.DEFAULT);
	assertNull(result);
	result = engine.evaluate("", map0, TemplateEngine.DEFAULT);
	assertEquals("", result);
	result = engine.evaluate(null, map1, TemplateEngine.DEFAULT);
	assertNull(result);
	result = engine.evaluate("", map1, TemplateEngine.DEFAULT);
	assertEquals("", result);
	result = engine.evaluate(null, map5, TemplateEngine.DEFAULT);
	assertNull(result);
	result = engine.evaluate("", map5, TemplateEngine.DEFAULT);
	assertEquals("", result);
	result = engine.evaluate(null, null, TemplateEngine.DEFAULT);
	assertNull(result);
	result = engine.evaluate("", null, TemplateEngine.DEFAULT);
	assertEquals("", result);
}

@Test
public void testTemplateOne() {
	// template string has one template
	result = engine.evaluate("${one}", map0, TemplateEngine.DEFAULT);
	assertEquals("${one}", result);
	result = engine.evaluate("${one}", map1, TemplateEngine.DEFAULT);
	assertEquals("WORD1", result);
	result = engine.evaluate("${one}", map5, TemplateEngine.DEFAULT);
	assertEquals("WORD1", result);
	result = engine.evaluate("${one}", null, TemplateEngine.DEFAULT);
	assertEquals("${one}", result);
}

@Test
public void testTemplateMany() {	
	// template string has many templates
	result = engine.evaluate("${one} ${three}", map0, TemplateEngine.DEFAULT);
	assertEquals("${one} ${three}", result);
	result = engine.evaluate("${one} ${three}", map1, TemplateEngine.DEFAULT);
	assertEquals("WORD1 ${three}", result);
	result = engine.evaluate("${one} ${three}", map5, TemplateEngine.DEFAULT);
	assertEquals("WORD1 WORD3", result);
	result = engine.evaluate("${one} ${three}", null, TemplateEngine.DEFAULT);
	assertEquals("${one} ${three}", result);
}

@Test
public void testEntryMapRaiseException() {
	try {
		map0.store(null, "WORD");
		fail("Allowed template key to be null");
	} catch (RuntimeException e) {
		
	} catch (Throwable t) {
		fail("Threw some other error instead of runtime exception.");
	}

	try {
		map0.store("", "WORD");
		fail("Allowed template key to be empty");
	} catch (RuntimeException e) {
		
	} catch (Throwable t) {
		fail("Threw some other error instead of runtime exception.");
	}
	
	try {
		map0.store("key", null);
		fail("Allowed replace value to be null");
	} catch (RuntimeException e) {

	} catch (Throwable t) {
		fail("Threw some other error instead of runtime exception.");
	}
	
	try {
		map1.delete(null);;
		fail("Allowed deleting null key");
	} catch (RuntimeException e) {
		
	} catch (Throwable t) {
		fail("Threw some other error instead of runtime exception.");
	}
	
	try {
		map1.delete("");;
		fail("Allowed deleting empty key");
	} catch (RuntimeException e) {
		
	} catch (Throwable t) {
		fail("Threw some other error instead of runtime exception.");
	}
	
	try {
		map5.update(null, "someWord");;
		fail("Allowed updating null key");
	} catch (RuntimeException e) {
		
	} catch (Throwable t) {
		fail("Threw some other error instead of runtime exception.");
	}
	
	try {
		map5.update("", "someWord");;
		fail("Allowed deleting empty key");
	} catch (RuntimeException e) {
		
	} catch (Throwable t) {
		fail("Threw some other error instead of runtime exception.");
	}
}

@Test
public void testEntryExist() {

	// entries that already exist cannot be stored again
	map0.store("one", "CORRECT");
	map0.store("one", "INCORRECT");
	result = engine.evaluate("${one}", map0, TemplateEngine.DEFAULT);
	assertEquals("CORRECT", result);
}

@Test
public void testEntryDelete() {
	
	map0.store("one", "CORRECT");

	
	// only existing value pair can be deleted
	map0.delete("notPresent"); // should not raise an error
	result = engine.evaluate("${one}", map0, TemplateEngine.DEFAULT);
	assertEquals("CORRECT", result);
	// now deleting existing
	map0.delete("one");
	result = engine.evaluate("${one}", map0, TemplateEngine.DEFAULT);
	assertEquals("${one}", result);
}

@Test
public void testEntryUpdate() {
	
	// only existing value pair can be updated
	map1.update("one", "NEWWORD");
	result = engine.evaluate("${one}", map1, TemplateEngine.DEFAULT);
	assertEquals("NEWWORD", result);
	map1.update("one","WORD1");
	map1.update("notHere", "NEW WORD");
	result = engine.evaluate("${one} ${notHere}", map1, TemplateEngine.DEFAULT);
	assertEquals("WORD1 ${notHere}", result);
	map1.update("one","");
	result = engine.evaluate("${one} ${notHere}", map1, TemplateEngine.DEFAULT);
	assertEquals(" ${notHere}", result);
}
	
@Test
public void testTemplateMatchingMode_Default() {
	
	// 0, null, unspecified and default use Default Mode
	// Default mode = KEEP_UNMATCHED, CASE_INSENSITIVE and ACCURATE_SEARCH
	result = engine.evaluate( "${noMatch} ${TWO} ${fou r} ${four}",map5 , TemplateEngine.DEFAULT);
	assertEquals("${noMatch} WORD2 WORD4 ${four}", result);
	
	result = engine.evaluate( "${noMatch} ${TWO} ${fou r} ${four}",map5 , null);
	assertEquals("${noMatch} WORD2 WORD4 ${four}", result); 
	
	result = engine.evaluate( "${noMatch} ${TWO} ${fou r} ${four}",map5 , 0);
	assertEquals("${noMatch} WORD2 WORD4 ${four}", result);
	
	result = engine.evaluate( "${noMatch} ${TWO} ${fou r} ${four}",map5 , 46);
	assertEquals("${noMatch} WORD2 WORD4 ${four}", result);
}

@Test
public void testTemplateMatchingMode_One() {
	
	// Check by choosing one mode. Others should be default
	result = engine.evaluate( "${noMatch} ${TWO} ${fou r} ${four}",map5 , TemplateEngine.DELETE_UNMATCHED);
	assertEquals(" WORD2 WORD4 ", result);
	
	result = engine.evaluate( "${noMatch} ${TWO} ${fou r} ${four}",map5 , TemplateEngine.CASE_SENSITIVE);
	assertEquals("${noMatch} ${TWO} WORD4 ${four}", result);
	
	result = engine.evaluate( "${noMatch} ${TWO} ${fou r} ${four}",map5 , TemplateEngine.BLUR_SEARCH);
	assertEquals("${noMatch} WORD2 WORD4 WORD4", result);
}

@Test
public void testTemplateMatchingMode_Multiple() {
	
	// Multiple modes using '|' 
	result = engine.evaluate( "${noMatch} ${TWO} ${fou r} ${four}",map5 , TemplateEngine.DELETE_UNMATCHED | 
			TemplateEngine.BLUR_SEARCH);
	assertEquals(" WORD2 WORD4 WORD4", result);
	
	result = engine.evaluate( "${noMatch} ${TWO} ${fou r} ${four}",map5 , TemplateEngine.DELETE_UNMATCHED | 
			TemplateEngine.BLUR_SEARCH | TemplateEngine.CASE_SENSITIVE);
	assertEquals("  WORD4 WORD4", result);
	
	result = engine.evaluate( "${noMatch} ${TWO} ${fou r} ${four}",map5 , TemplateEngine.CASE_SENSITIVE | 
			TemplateEngine.DELETE_UNMATCHED);
	assertEquals("  WORD4 ", result);
}

@Test
public void testTemplateMatchingMode_Contradictory() {
	
	// Contradictory modes
	result = engine.evaluate( "${noMatch} ${TWO} ${fou r} ${four}",map5 , TemplateEngine.KEEP_UNMATCHED 
			| TemplateEngine.DELETE_UNMATCHED);
	assertEquals(" WORD2 WORD4 ", result);
	
	result = engine.evaluate( "${noMatch} ${TWO} ${fou r} ${four}",map5 , TemplateEngine.ACCURATE_SEARCH 
			| TemplateEngine.BLUR_SEARCH);
	assertEquals("${noMatch} WORD2 WORD4 WORD4", result);
	
	result = engine.evaluate( "${noMatch} ${TWO} ${fou r} ${four}",map5 , TemplateEngine.CASE_SENSITIVE 
			| TemplateEngine.CASE_INSENSITIVE);
	assertEquals("${noMatch} ${TWO} WORD4 ${four}", result);
	
	result = engine.evaluate( "${noMatch} ${TWO} ${fou r} ${four}",map5 , TemplateEngine.CASE_SENSITIVE 
			| TemplateEngine.CASE_INSENSITIVE | TemplateEngine.BLUR_SEARCH);
	assertEquals("${noMatch} ${TWO} WORD4 WORD4", result);
}

@Test
public void testGarbageMode() {
	// Correct modes with garbage value
	result = engine.evaluate( "${noMatch} ${TWO} ${fou r} ${four}",map5 , TemplateEngine.CASE_SENSITIVE 
			| TemplateEngine.CASE_INSENSITIVE | TemplateEngine.BLUR_SEARCH | 46);
	assertEquals("${noMatch} WORD2 WORD4 ${four}", result);
}

@Test
public void testTemplatePrecedenceRecursion() {
	
	// BLUR works?
	result = engine.evaluate( "${f o u r} ${ T  W  O} ${fo u r} ${four}",map5 , TemplateEngine.BLUR_SEARCH);
	assertEquals("WORD4 WORD2 WORD4 WORD4", result);
	
	// template precedence and  ${}
	map5.store("this WORD1", "REPLACED");
	result = engine.evaluate( "${noMatch} ${TWO} ${fou r} ${this ${one}}",map5 , TemplateEngine.DEFAULT);
	assertEquals("${noMatch} WORD2 WORD4 REPLACED", result);
	
	result = engine.evaluate( "${} ${this ${one}}",map5 , TemplateEngine.DEFAULT);
	assertEquals("${} REPLACED", result);

	map5.store("blank", "");
	result = engine.evaluate( "abc}${two}${this ${one}${blank}}${one}uvw${xyz",map5 , TemplateEngine.DEFAULT);
	assertEquals("abc}WORD2REPLACEDWORD1uvw${xyz", result);
	
	// no recursion , here : WORD3 --> three and three --> WORD3
	map5.store("WORD3", "three");
	result = engine.evaluate( "${three} ${WORD3}",map5 , TemplateEngine.DEFAULT);
	assertEquals("WORD3 three", result);
}

@Test
public void testInception() {
	map0.store("one", "${one}");
	map0.store("${one}", "one");
	String result = engine.evaluate("${one}", map0, TemplateEngine.DEFAULT);
	assertEquals("${one}", result);
}

/////////////////////////////////////////////////////
/////// Tests for SimpleTemplateEngine //////////////
/////////////////////////////////////////////////////

@Test
public void testSimple() {
	String template = "david XXX XdavidX YYY Y Z X";
    String pattern = "david";
    String value = "Tom";
    Integer matchingMode = SimpleTemplateEngine.DEFAULT_MATCH;
    String result = simpleEngine.evaluate(template, pattern, value, matchingMode);
    assertEquals("Tom XXX XTomX YYY Y Z X", result);
    
}

@Test
public void testSpecialValue() {
	String template = "david XXX XdavidX YYY Y Z X";
    String pattern = "david#2";
    String value = "Tom";
    Integer matchingMode = SimpleTemplateEngine.DEFAULT_MATCH;
    String result = simpleEngine.evaluate(template, pattern, value, matchingMode);
    assertEquals("david XXX XTomX YYY Y Z X", result);
}

@Test
public void testNumberofPatters() {
	
	// no patterns
	String template = "david XXX XdavidX YYY Y Z X";
    String pattern = "Row";
    String value = "Tom";
    Integer matchingMode = SimpleTemplateEngine.DEFAULT_MATCH;
    String result = simpleEngine.evaluate(template, pattern, value, matchingMode);
    assertEquals("david XXX XdavidX YYY Y Z X", result);
    
    // one pattern
    template = "row XXX XdavidX YYY Y Z X";
    pattern = "david";
    value = "Tom";
    matchingMode = SimpleTemplateEngine.DEFAULT_MATCH;
    result = simpleEngine.evaluate(template, pattern, value, matchingMode);
    assertEquals("row XXX XTomX YYY Y Z X", result);
    
    // many pattern
    template = "david XXX XdavidX YYY Y Z david X";
    pattern = "david";
    value = "Tom";
    matchingMode = SimpleTemplateEngine.DEFAULT_MATCH;
    result = simpleEngine.evaluate(template, pattern, value, matchingMode);
    assertEquals("Tom XXX XTomX YYY Y Z Tom X", result);
}

@Test
public void testNullandEmptyVlaues() {
	
	// template null or empty
	String template = null;
    String pattern = "Row";
    String value = "Tom";
    Integer matchingMode = SimpleTemplateEngine.DEFAULT_MATCH;
    String result = simpleEngine.evaluate(template, pattern, value, matchingMode);
    assertNull(result);
    
    template = "";
    pattern = "david";
    value = "Tom";
    matchingMode = SimpleTemplateEngine.DEFAULT_MATCH;
    result = simpleEngine.evaluate(template, pattern, value, matchingMode);
    assertEquals("", result);
    
    // format pattern null or empty
    template = "row XXX XdavidX YYY Y Z X";
    pattern = null;
    value = "Tom";
    matchingMode = SimpleTemplateEngine.DEFAULT_MATCH;
    result = simpleEngine.evaluate(template, pattern, value, matchingMode);
    assertEquals("row XXX XdavidX YYY Y Z X", result);
    
    template = "row XXX XdavidX YYY Y Z X";
    pattern = "";
    value = "Tom";
    matchingMode = SimpleTemplateEngine.DEFAULT_MATCH;
    result = simpleEngine.evaluate(template, pattern, value, matchingMode);
    assertEquals("row XXX XdavidX YYY Y Z X", result);
    
    // Value string null or empty
    template = "david XXX XdavidX YYY Y Z david X";
    pattern = "david"; 
    value = null;
    matchingMode = SimpleTemplateEngine.DEFAULT_MATCH;
    result = simpleEngine.evaluate(template, pattern, value, matchingMode);
    assertEquals("david XXX XdavidX YYY Y Z david X", result);
    
    template = "david XXX XdavidX YYY Y Z david X";
    pattern = "david";
    value = "";
    matchingMode = SimpleTemplateEngine.DEFAULT_MATCH;
    result = simpleEngine.evaluate(template, pattern, value, matchingMode);
    assertEquals("david XXX XdavidX YYY Y Z david X", result);
}

@Test
public void testNoMatchFound() {
	String template = "david XXX XDavidX YYY Y Z david X";
    String pattern = "Row";
    String value = "Tom";
    Integer matchingMode = SimpleTemplateEngine.DEFAULT_MATCH;
    String result = simpleEngine.evaluate(template, pattern, value, matchingMode);
    assertEquals("david XXX XDavidX YYY Y Z david X", result);
}

public void testMatchingModes() {
	
	String template = "david XXX XDavidX YYY Y Z david X";
    String pattern = "David";
    String value = "Tom";
    Integer matchingMode = SimpleTemplateEngine.CASE_SENSITIVE;
    String result = simpleEngine.evaluate(template, pattern, value, matchingMode);
    assertEquals("david XXX XTomX YYY Y Z david X", result);
    
    template = "david XXX XDavidX YYY Y Z david X";
    pattern = "David";
    value = "Tom";
    matchingMode = SimpleTemplateEngine.WHOLE_WORLD_SEARCH;
    result = simpleEngine.evaluate(template, pattern, value, matchingMode);
    assertEquals("Tom XXX XDavidX YYY Y Z Tom X", result);
    
    template = "david XXX XDavidX YYY Y Z David X";
    pattern = "David";
    value = "Tom";
    matchingMode = SimpleTemplateEngine.WHOLE_WORLD_SEARCH | SimpleTemplateEngine.CASE_SENSITIVE;
    result = simpleEngine.evaluate(template, pattern, value, matchingMode);
    assertEquals("david XXX XDavidX YYY Y Z Tom X", result);
}

@Test
public void testSimpleTemplateEngineSpecs() {
	
	// special character #
	String template = "david# XXX X#DavidX YYY Y Z david X";
    String pattern = "David##";
    String value = "Tom";
    Integer matchingMode = SimpleTemplateEngine.DEFAULT_MATCH;
    String result = simpleEngine.evaluate(template, pattern, value, matchingMode);
    assertEquals("Tom XXX X#DavidX YYY Y Z david X", result);
    
    template = "david## XXX X#David##X YYY Y Z david X";
    pattern = "David#####2";
    value = "Tom";
    matchingMode = SimpleTemplateEngine.DEFAULT_MATCH;
    result = simpleEngine.evaluate(template, pattern, value, matchingMode);
    assertEquals("david## XXX X#TomX YYY Y Z david X", result);
    
    template = "davi## XXX X#Davi##X YYY Y Z david X";
    pattern = "Davi#####2ani";
    value = "To";
    matchingMode = SimpleTemplateEngine.DEFAULT_MATCH;
    result = simpleEngine.evaluate(template, pattern, value, matchingMode);
    assertEquals("davi## XXX X#ToX YYY Y Z david X", result);
    
    template = "localVARIABLE int localId = local";
    pattern = "local";
    value = "global";
    matchingMode = SimpleTemplateEngine.DEFAULT_MATCH;
    result = simpleEngine.evaluate(template, pattern, value, matchingMode);
    assertEquals("globalVARIABLE int globalId = global", result);
    
    template = "localVARIABLE int localId = local";
    pattern = "local";
    value = "global";
    matchingMode = SimpleTemplateEngine.WHOLE_WORLD_SEARCH;
    result = simpleEngine.evaluate(template, pattern, value, matchingMode);
    assertEquals("localVARIABLE int localId = global", result);
    
    template = "localVARIABLE int localId = local";
    pattern = "local";
    value = "global";
    matchingMode = SimpleTemplateEngine.DEFAULT_MATCH;
    result = simpleEngine.evaluate(template, pattern, value, matchingMode);
    assertNotEquals("localVARIABLE int localId = global", result);
    
}

@Test
public void testNoRecursion() {
	String template = "david XXX X#DavidX YYY Y Z david X";
    String pattern = "david";
    String value = "david";
    Integer matchingMode = SimpleTemplateEngine.DEFAULT_MATCH;
    String result = simpleEngine.evaluate(template, pattern, value, matchingMode);
    assertEquals("david XXX X#davidX YYY Y Z david X", result);
    
    template = "david XXX X#DavidX YYY Y Z david abc X";
    pattern = "abc";
    value = "abcabc";
    matchingMode = SimpleTemplateEngine.DEFAULT_MATCH;
    result = simpleEngine.evaluate(template, pattern, value, matchingMode);
    assertEquals("david XXX X#DavidX YYY Y Z david abcabc X", result);
    
}

@Test
public void testSpecalChars() {
	String template = ".!.david9a##";
    String pattern = "david9a";
    String value = "tom3";
    Integer matchingMode = SimpleTemplateEngine.WHOLE_WORLD_SEARCH;
    String result = simpleEngine.evaluate(template, pattern, value, matchingMode);
    assertEquals(".!.tom3##", result);
    
    
}

///////////////////////////////////////////////////////////
/////// Tests added after Coverage analysis //////////////
//////////////////////////////////////////////////////////

// Can't test EntryMap.Entry as the class is not public.

// Adding check for invalid matching mode for SimleTemplateEngine
// null, mode < 0 , mode > 3 should go to default.
@Test
public void testInvalidModeForSimpleEngine() {
	String template = "david XXX X#DavidX YYY Y Z david X";
    String pattern = "david";
    String value = "tom";
    Integer matchingMode = null;
    String result = simpleEngine.evaluate(template, pattern, value, matchingMode);
    assertEquals("tom XXX X#tomX YYY Y Z tom X", result);
    
    template = "david XXX X#DavidX YYY Y Z david X";
    pattern = "david";
    value = "tom";
    matchingMode = -3;
    result = simpleEngine.evaluate(template, pattern, value, matchingMode);
    assertEquals("tom XXX X#tomX YYY Y Z tom X", result);
    
    template = "david XXX X#DavidX YYY Y Z david X";
    pattern = "david";
    value = "tom";
    matchingMode = 9;
    result = simpleEngine.evaluate(template, pattern, value, matchingMode);
    assertEquals("tom XXX X#tomX YYY Y Z tom X", result);
}

// case sensitive
@Test
public void testSensitive() {
	String template = "david XXX X#DavidX YYY Y Z david X";
    String pattern = "david";
    String value = "tom";
    Integer matchingMode = SimpleTemplateEngine.CASE_SENSITIVE;
    String result = simpleEngine.evaluate(template, pattern, value, matchingMode);
    assertEquals("tom XXX X#DavidX YYY Y Z tom X", result);
}

// matching mode < 0 for TemplateEngine
@Test
public void testNegativeMode() {
	String result = engine.evaluate("${one} ${three}", map1, -5);
	assertEquals("WORD1 ${three}", result);
}

// $ not following 
@Test
public void testDollar() {
	map0.store("one", "CORRECT");
	String result = engine.evaluate("$ ${one} {} {one} ${ $} ${} ${ }", map0, TemplateEngine.DEFAULT);
	assertEquals("$ CORRECT {} {one} ${ $} ${} ${ }", result);
}

// digit SimpleEngineTest
@Test
public void testDigit() {
	String template = "22david9a22 4 david9a ani";
    String pattern = "david9a";
    String value = "tom3";
    Integer matchingMode = SimpleTemplateEngine.WHOLE_WORLD_SEARCH;
    String result = simpleEngine.evaluate(template, pattern, value, matchingMode);
    assertEquals("22david9a22 4 tom3 ani", result);
}

@Test
public void testDigit2() {
	String template = "22david9a22 4 david9a ani";
    String pattern = "david9a";
    String value = "tom3";
    Integer matchingMode = SimpleTemplateEngine.DEFAULT_MATCH;
    String result = simpleEngine.evaluate(template, pattern, value, matchingMode);
    assertEquals("22tom322 4 tom3 ani", result);
}

@Test
public void testHash() {
	String template = "Hello#";
    String pattern = "#";
    String value = "tom3";
    Integer matchingMode = SimpleTemplateEngine.DEFAULT_MATCH;
    String result = simpleEngine.evaluate(template, pattern, value, matchingMode);
    assertEquals("Hello#", result);
}


@Test
public void testHash2() {
	String template = "Wow";
    String pattern = "Hello#";
    String value = "tom3";
    Integer matchingMode = SimpleTemplateEngine.DEFAULT_MATCH;
    String result = simpleEngine.evaluate(template, pattern, value, matchingMode);
    assertEquals("Wow", result);
}


@Test
public void testEntries() {
	ArrayList e = map5.getEntries();
	assertTrue((e.get(0).equals(e.get(0))));
	assertTrue(!(e.get(0).equals(e.get(1))));
	assertTrue(!(e.get(0).equals(null)));
	assertTrue(!(e.get(0).equals(" ")));
}

@Test
public void testHash3() {
	String template = "Wow";
    String pattern = "#a";
    String value = "tom3";
    Integer matchingMode = SimpleTemplateEngine.DEFAULT_MATCH;
    String result = simpleEngine.evaluate(template, pattern, value, matchingMode);
    assertEquals("Wow", result);
}

@Test
public void testHash4() {
	String template = "Wow";
    String pattern = "#99aa";
    String value = "tom3";
    Integer matchingMode = SimpleTemplateEngine.DEFAULT_MATCH;
    String result = simpleEngine.evaluate(template, pattern, value, matchingMode);
    assertEquals("Wow", result);
}

@Test
public void testLength1() {
	String template = "aAnirudh";
    String pattern = "Anirudh";
    String value = "wow";
    Integer matchingMode = SimpleTemplateEngine.WHOLE_WORLD_SEARCH;
    String result = simpleEngine.evaluate(template, pattern, value, matchingMode);
    assertEquals("aAnirudh", result);
}
@Test
public void testLength2() {
	String template = "Anirudh";
    String pattern = "Anirudh";
    String value = "wow";
    Integer matchingMode = SimpleTemplateEngine.WHOLE_WORLD_SEARCH;
    String result = simpleEngine.evaluate(template, pattern, value, matchingMode);
    assertEquals("wow", result);
}


@Test
public void testReflection() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
   

   Class<?> innerClass = engine.getClass().getDeclaredClasses()[1];
   
   Constructor<?> constructor = innerClass.getDeclaredConstructors()[0];
   
   constructor.setAccessible(true);
   
   java.lang.reflect.Method[] methods = innerClass.getMethods();
   
   Object templateObject1 = constructor.newInstance(engine, 0, 2, "ani");
   Object templateObject2 = constructor.newInstance(engine, 0, 2, "ani");
   Object templateObject3 = constructor.newInstance(engine, 3, 5, "bni");
   Object templateObject4 = constructor.newInstance(engine, null, 5, "bni");
   Object templateObject5 = constructor.newInstance(engine, 3, null, "bni");
   Object templateObject6 = constructor.newInstance(engine, null, 5, null);
   Object templateObject7 = constructor.newInstance(engine, null, 5, null);
   Object templateObject8 = constructor.newInstance(engine, 3, null, "bni");
   
   Object templateObject9 = constructor.newInstance(engine, 3, 5, null);
   
   
   
   for (java.lang.reflect.Method i: methods ) {
	   if (i.getName() == "equals") {
		    // covering various branches
		   
		   assertTrue(templateObject1.equals(templateObject2));
		   assertTrue(!templateObject1.equals(null));
		   assertTrue(!templateObject1.equals(templateObject3));
		   assertTrue(templateObject1.equals(templateObject1));
		   assertTrue(!templateObject1.equals(" "));
		   assertTrue(!templateObject4.equals(templateObject3));
		   assertTrue(!templateObject5.equals(templateObject3));
		   assertTrue(!templateObject6.equals(templateObject3));
		   assertTrue(templateObject6.equals(templateObject7));
		   assertTrue(templateObject5.equals(templateObject8));
		   
		   assertTrue(!templateObject3.equals(templateObject5));
		   assertTrue(!templateObject9.equals(templateObject3));
	   }
   }
   
  
   
}

}
