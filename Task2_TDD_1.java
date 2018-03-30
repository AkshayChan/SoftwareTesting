package src.st;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;


public class Task2_TDD_1 {
    
    private EntryMap map0;
    private EntryMap map1;
    private EntryMap mapY;
    private EntryMap map5;
    
    private String result;

    private TemplateEngine engine;
    
    private SimpleTemplateEngine simpleEngine;
    
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
        assertEquals("I won the championship in 2013", result);
        
        //Basic in years test
        mapY.update("year", "in 6 years");
        result = engine.evaluate("I will win the championship in ${year}", mapY, TemplateEngine.DEFAULT);
        assertEquals("I will win the championship in 2024", result);
        
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
        
        mapY.delete("base_year");
        
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
        
        mapY.delete("base_year");
        
        //Not Number years ago test
        mapY.update("year", "A years ago");
        result = engine.evaluate("I won the championship in ${year}", mapY, TemplateEngine.DEFAULT);
        assertEquals("I won the championship in A years ago", result);
        
        //Not Number in years test
        mapY.update("year", "in A years");
        result = engine.evaluate("I will win the championship in ${year}", mapY, TemplateEngine.DEFAULT);
        assertEquals("I will win the championship in in A years", result);
    }
    
}