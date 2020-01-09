package org.fulib.yaml;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;

/**
 * <p>Storyboard <a href='.././src/test/java/org/sdmlib/test/doc/TestJavaDocStories.java'>GenJavaDocStory</a></p>
 * <p>Yamler reads simple key value pairs in YAML syntax.</p>
 * <p>Example:</p>
 * <pre>            String yaml = &quot;&quot; +
 *               &quot;msgType: newPlayer\n&quot; +
 *               &quot;login: albert\n&quot; +
 *               &quot;colors: blue red \n&quot;;
 *
 *       Yamler yamler = new Yamler();
 *       LinkedHashMap&lt;String, String&gt; map = yamler.decode(yaml);
 * </pre>
 * <pre>{msgType=newPlayer, login=albert, colors=blue red}</pre>
 */
public class Yamler
{
   // =============== Constants ===============

   private static final int LEADING_CONTEXT_CHARS  = 10;
   private static final int TRAILING_CONTEXT_CHARS = 20;

   // =============== Fields ===============

   private String          yaml;
   private StringTokenizer tokenizer;
   private String          lookAheadToken;
   private String          currentToken;
   private int             currentPos;
   private int             lookAheadPos;

   // =============== Constructors ===============

   public Yamler()
   {
      // empty
   }

   public Yamler(String yaml)
   {
      this.yaml = yaml;

      this.tokenizer = new StringTokenizer(yaml);
      this.lookAheadToken = null;
      this.nextToken();
      this.nextToken();
   }

   // =============== Properties ===============

   public String getCurrentToken()
   {
      return this.currentToken;
   }

   public int getCurrentPos()
   {
      return this.currentPos;
   }

   public String getLookAheadToken()
   {
      return this.lookAheadToken;
   }

   public int getLookAheadPos()
   {
      return this.lookAheadPos;
   }

   // =============== Methods ===============

   /**
    * Storyboard
    *
    * <p>Storyboard <a href='https://github.com/fujaba/SDMLib/blob/master/src/test/java/org/sdmlib/test/examples/groupaccount/GroupAccountTests.java'>PlainYaml</a></p>
    * <p>Start: plain yaml to be decoded to map</p>
    * <pre>joining: abu
    * lastChanges: 2018-03-17T14:48:00.000.abu 2018-03-17T14:38:00.000.bob 2018-03-17T14:18:00.000.xia</pre>
    * <pre>{joining=abu, lastChanges=2018-03-17T14:48:00.000.abu 2018-03-17T14:38:00.000.bob 2018-03-17T14:18:00.000.xia}</pre>
    * <p>Check: value for joining abu actual abu</p>
    * <p><a name = 'step_1'>Step 1: Alternatively, use special object type map</a></p>
    * <pre>- m: .Map
    *   joining: abu
    *   lastChanges: 2018-03-17T14:48:00.000.abu 2018-03-17T14:38:00.000.bob 2018-03-17T14:18:00.000.xia</pre>
    * <pre>{joining=abu, lastChanges=2018-03-17T14:48:00.000.abu 2018-03-17T14:38:00.000.bob 2018-03-17T14:18:00.000.xia}</pre>
    * <p>Check: value for joining abu actual abu</p>
    *
    * @param yaml
    *    yaml text
    *
    * @return map with key value pairs
    *
    * @see <a href='file://YamlFileMap.java'>YamlFileMap.java</a>
    */
   public LinkedHashMap<String, String> decode(String yaml)
   {
      this.yaml = yaml;
      this.tokenizer = new StringTokenizer(yaml);
      this.lookAheadToken = null;
      this.nextToken();
      this.nextToken();

      LinkedHashMap<String, String> result = new LinkedHashMap<>();

      while (this.currentToken != null && this.currentToken.endsWith(":"))
      {
         String attrName = this.stripColon(this.currentToken);

         this.nextToken();

         String value = "";
         int valueStart = this.currentPos;

         // many values
         while (this.currentToken != null && !this.currentToken.endsWith(":"))
         {
            value = yaml.substring(valueStart, this.currentPos + this.currentToken.length());

            this.nextToken();
         }

         result.put(attrName, value);
      }

      return result;
   }

   public ArrayList<LinkedHashMap<String, String>> decodeList(String yaml)
   {
      this.yaml = yaml;
      this.tokenizer = new StringTokenizer(yaml);
      this.lookAheadToken = null;
      this.nextToken();
      this.nextToken();

      ArrayList<LinkedHashMap<String, String>> result = new ArrayList<>();

      while ("-".equals(this.currentToken))
      {
         LinkedHashMap<String, String> map = new LinkedHashMap<>();
         result.add(map);
         this.nextToken();
         while (this.currentToken != null && this.currentToken.endsWith(":"))
         {
            String key = this.stripColon(this.currentToken);
            this.nextToken();
            String value = this.currentToken;
            this.nextToken();
            map.put(key, value);
         }
      }

      return result;
   }

   public String nextToken()
   {
      this.currentToken = this.lookAheadToken;
      this.currentPos = this.lookAheadPos;

      if (this.tokenizer.hasMoreTokens())
      {

         this.lookAheadToken = this.tokenizer.nextToken();
         int currentLength = 0;
         if (this.currentToken != null)
         {
            currentLength = this.currentToken.length();
         }
         this.lookAheadPos = this.yaml.indexOf(this.lookAheadToken, this.lookAheadPos + currentLength);
         // lookAheadPos = scanner.match().start();
      }
      else
      {
         this.lookAheadToken = null;
      }

      if (this.lookAheadToken != null && this.lookAheadToken.startsWith("\""))
      {
         // get up to end of string
         int stringStartPos = this.lookAheadPos + 1;
         String subToken = this.lookAheadToken;
         //MatchResult match = scanner.match();
         int subTokenEnd = this.lookAheadPos + subToken.length();
         while (subTokenEnd < stringStartPos + 1
                || (!subToken.endsWith("\"") || subToken.endsWith("\\\"")) && this.tokenizer.hasMoreTokens())
         {
            subToken = this.tokenizer.nextToken();
            subTokenEnd = this.yaml.indexOf(subToken, subTokenEnd) + subToken.length();
         }

         this.lookAheadToken = this.yaml.substring(stringStartPos, subTokenEnd - 1);

         this.lookAheadToken = this.deEncapsulate(this.lookAheadToken);
      }

      return this.currentToken;
   }

   public String stripColon(String key)
   {
      String id = key;

      if (key.endsWith(":"))
      {
         id = key.substring(0, key.length() - 1);
      }
      else
      {
         this.printError("key does not end with ':' " + key);
      }

      return id;
   }

   /**
    * Encapsulates a YAML value by enclosing it in quotes ("), if necessary.
    *
    * @param value
    *    the YAML value to encapsulate
    *
    * @return the encapsulated YAML value
    *
    * @deprecated since 1.2; use {@link YamlGenerator#encapsulate(String)} instead
    */
   public static String encapsulate(String value)
   {
      return YamlGenerator.encapsulate(value);
   }

   String deEncapsulate(String value)
   {
      value = value.replaceAll("\\\\\"", "\"");
      return value;
   }

   void printError(String msg)
   {
      int startPos = this.currentPos - LEADING_CONTEXT_CHARS;
      if (startPos < 0)
      {
         startPos = 0;
      }

      int endPos = this.currentPos + TRAILING_CONTEXT_CHARS;
      if (endPos >= this.yaml.length())
      {
         endPos = this.yaml.length();
      }

      final String info = this.yaml.substring(startPos, this.currentPos) + "<--" + msg + "-->" + this.yaml
         .substring(this.currentPos, endPos);
      System.err.println(info);
   }
}
