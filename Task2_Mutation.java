package st;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;
import st.EntryMap;
import st.TemplateEngine;
import st.SimpleTemplateEngine;


public class Task2_Mutation {

    private EntryMap map0;
    private EntryMap map1;
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
        map1.store("on e", "WORD1");
        
        // many entries
        map5 = new EntryMap();
        map5.store("one", "WORD1");
        map5.store("two", "WORD2");
        map5.store("three", "WORD3");
        map5.store("fou r", "WORD4");
        map5.store("abc", "abcabc");
        
        engine = new TemplateEngine();
        simpleEngine = new SimpleTemplateEngine();
    }
    
     //Can't accurately search spaces, so will also fail tests 3 and 4
     @Test 
     public void testMutationOne() {
         result = engine.evaluate("This is ${on e}", map1, TemplateEngine.DEFAULT);
         assertEquals("This is WORD1", result);
     }
     
     //This mutation can't delete and add the same entry back with a different value 
     @Test 
     public void testMutationTwo() {
         map5.delete("one");
         map5.store("one", "WORD1 again?");
         result = engine.evaluate("Is this ${one}", map5, TemplateEngine.DEFAULT);
         assertEquals("Is this WORD1 again?", result);
     }
     
     //Adds a space before the instantiated template string, so will also fail test 9
     @Test 
     public void testMutationThree() {
         result = engine.evaluate("${on e} is being added", map1, TemplateEngine.DEFAULT);
         assertEquals("WORD1 is being added", result);
     }
     
     //If the template is too long, this mutation test fails
     @Test
     public void testMutationFour() {
         map5.store ("This template is way too long to even work", "Is it thou?");
         result = engine.evaluate("Hey, ${This template is way too long to even work} ${two}", map5, TemplateEngine.DEFAULT);
         assertEquals("Hey, Is it thou? WORD2", result);
     }
     
     //If the template is saved again, then it won't accurately find the previous one 
     @Test
     public void testMutationFive() {
         map5.store("TwO", "word 2");
         result = engine.evaluate("This should be ${two}", map5, TemplateEngine.DEFAULT);
         assertEquals("This should be WORD2", result);
     }
     
     //Spent hours doing this, we randomly figured this out from the empty/a appearing at the end of test 10
     @Test
     public void testMutationSix() {
        map5.store("a", "Word 1");
        result = engine.evaluate("${}", map5, TemplateEngine.CASE_SENSITIVE);
        assertEquals("${}", result);
     }
     
     //Deleting a non existent entry should not throw an error
     @Test
     public void testMutationSeven() {
         map0.delete("Hello");
     }
     
     //Case sensitivity doesn't work for SimpleTemplateEngine
     @Test
     public void testMutationEight() {
         result = simpleEngine.evaluate("The variable Local is a local variable", "local", "global", SimpleTemplateEngine.CASE_SENSITIVE);
         assertEquals("The variable Local is a global variable", result);
     }
     
     //spaces don't show up
     @Test
     public void testMutationNine() {
         map0.store("Hello", "Will this work with spaces?   ");
         result = engine.evaluate("${Hello}", map0, TemplateEngine.DEFAULT);
         assertEquals("Will this work with spaces?   ", result);
     }
     
     //
     @Test 
     public void testMutationTen() {
         result = simpleEngine.evaluate("local local", "local", "global", SimpleTemplateEngine.DEFAULT_MATCH);
         assertEquals("global global", result);
     }
     
}