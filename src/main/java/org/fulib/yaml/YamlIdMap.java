package org.fulib.yaml;

import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * <h3>Storyboard Yaml</h3>
 * <h4><a name = 'step_1'>Step 1: Read graph from yaml text:</a></h4>
 * <pre>- studyRight: University
 *   name:       &quot;\&quot;Study \&quot; Right\&quot;And\&quot;Fast now\&quot;&quot;
 *   students:   karli
 *   rooms:      mathRoom artsRoom sportsRoom examRoom softwareEngineering
 *
 * - karli: Student
 *   id:    4242
 *   name:  karli
 *
 *
 * - Assignment   content:                      points:
 *   matrixMult:  &quot;Matrix Multiplication&quot;     5
 *   series:      &quot;Series&quot;                    6
 *   a3:          Integrals                     8
 *
 * - Room                  topic:  credits: doors:                 students: assignments:
 *   mathRoom:             math    17       null                   karli     [matrixMult series a3]
 *   artsRoom:             arts    16       mathRoom               null      null
 *   sportsRoom:           sports  25       [mathRoom artsRoom]
 *   examRoom:             exam     0       [sportsRoom artsRoom]
 *   softwareEngineering:  &quot;Software Engineering&quot; 42 [artsRoom examRoom]
 * </pre>
 * <h4><a name = 'step_2'>Step 2: Call YamlIdMap.decode:</a></h4>
 * <pre><code class="java" data-lang="java">
 *       YamlIdMap yamlIdMap = new YamlIdMap(&quot;org.sdmlib.test.examples.studyrightWithAssignments.model&quot;);
 *
 *       University studyRight = (University) yamlIdMap.decode(yaml);
 * </code></pre>
 * <h4><a name = 'step_3'>Step 3: Decoded object structure:</a></h4>
 * <img src="doc-files/YamlStep5.png" alt="YamlStep5.png" width='869'>
 * <p>Check: root object exists "Study " Right"And"Fast now"</p>
 * <h4><a name = 'step_4'>Step 4: Generate Yaml from model:</a></h4>
 * <pre>- u1: 	University
 *   name: 	&quot;\&quot;Study \&quot; Right\&quot;And\&quot;Fast now\&quot;&quot;
 *   students: 	s2
 *   rooms: 	r3 	r4 	r5 	r6 	r7
 *
 * - s2: 	Student
 *   assignmentPoints: 	0
 *   credits: 	0
 *   id: 	4242
 *   motivation: 	0
 *   name: 	karli
 *   in: 	r3
 *   university: 	u1
 *
 * - r3: 	Room
 *   credits: 	17
 *   topic: 	math
 *   doors: 	r4 	r5
 *   students: 	s2
 *   assignments: 	a8 	a9 	a10
 *   university: 	u1
 *
 * - r4: 	Room
 *   credits: 	16
 *   topic: 	arts
 *   doors: 	r3 	r5 	r6 	r7
 *   university: 	u1
 *
 * - r5: 	Room
 *   credits: 	25
 *   topic: 	sports
 *   doors: 	r3 	r4 	r6
 *   university: 	u1
 *
 * - r6: 	Room
 *   credits: 	0
 *   topic: 	exam
 *   doors: 	r5 	r4 	r7
 *   university: 	u1
 *
 * - r7: 	Room
 *   credits: 	42
 *   topic: 	&quot;Software Engineering&quot;
 *   doors: 	r4 	r6
 *   university: 	u1
 *
 * - a8: 	Assignment
 *   content: 	&quot;Matrix Multiplication&quot;
 *   points: 	5
 *   room: 	r3
 *
 * - a9: 	Assignment
 *   content: 	Series
 *   points: 	6
 *   room: 	r3
 *
 * - a10: 	Assignment
 *   content: 	Integrals
 *   points: 	8
 *   room: 	r3
 *
 * </pre>
 * <p>Check: yaml starts with - u... true</p>
 * <h4><a name = 'step_5'>Step 5: decoded again:</a></h4>
 * <img src="doc-files/YamlStep11.png" alt="YamlStep11.png" width='876'>
 * <h4><a name = 'step_6'>Step 6: now read from excel file</a></h4>
 * <pre><code class="java" data-lang="java">
 *       byte[] readAllBytes = Files.readAllBytes(Paths.get(&quot;doc&#x2F;StudyRightStartSituation.txt&quot;));
 *       String excelText = new String(readAllBytes);
 *
 *       YamlIdMap excelIdMap = new YamlIdMap(&quot;org.sdmlib.test.examples.studyrightWithAssignments.model&quot;);
 *
 *       studyRight = (University) excelIdMap.decode(excelText);
 * </code></pre>
 * <p>doc/StudyRightStartSituation.txt</p>
 * <pre>-	studyRight:	University
 * 	name: 	&quot;&quot;&quot;Study Right&quot;&quot;&quot;
 * 	students:	karli
 * 	rooms: 	mathRoom	artsRoom	sportsRoom	examRoom	softwareEngineering
 *
 * -	karli:	Student
 * 	id:	4242
 * 	name:	karli
 *
 * -	Assignment	content:	points:
 * 	matrixMult:	&quot;&quot;&quot;Matrix Multiplication&quot;&quot;&quot;	5
 * 	series:	Series	6
 * 	a3:	Integrals	8
 *
 * -	Room	topic:	credits:	doors:	students:	assignments:
 * 	mathRoom:	math	17	null	karli	[matricMult series a3]
 * 	artsRoom:	arts	25	mathRoom
 * 	sportsRoom:	sports	25	[mathRoom artsRoom]
 * 	examRoom:	exam	0	[sportsRoom artsRoom]
 * 	softwareEngineering:	&quot;&quot;&quot;Software Engineering&quot;&quot;&quot;	42	[artsRoom examRoom]
 * </pre>
 * <p>result:</p>
 * <img src="doc-files/YamlStep17.png" alt="YamlStep17.png" width='795'>
 */
public class YamlIdMap
{
   private static final String REMOVE = "remove";
   private static final String REMOVE_YOU = "removeYou";
   private ArrayList<String> packageNames;
   private String yaml;
   private String userId = null;
   private boolean decodingPropertyChange;
   private LinkedHashMap<String, Object> objIdMap = new LinkedHashMap<>();
   private LinkedHashMap<Object, String> idObjMap = new LinkedHashMap<>();
   private int maxUsedIdNum = 0;
   private Yamler yamler = new Yamler();
   private HashMap<String, String> attrTimeStamps = new HashMap<>();

   public LinkedHashMap<String, Object> getObjIdMap()
   {
      return objIdMap;
   }

   public LinkedHashMap<Object, String> getIdObjMap()
   {
      return idObjMap;
   }

   public HashMap<String, String> getAttrTimeStamps()
   {
      return attrTimeStamps;
   }



   /**
    *
    * <h3>Storyboard Yaml</h3>
    * <h4><a name = 'step_1'>Step 1: Read graph from yaml text:</a></h4>
    * <pre>- studyRight: University
    *   name:       &quot;\&quot;Study \&quot; Right\&quot;And\&quot;Fast now\&quot;&quot;
    *   students:   karli
    *   rooms:      mathRoom artsRoom sportsRoom examRoom softwareEngineering
    *
    * - karli: Student
    *   id:    4242
    *   name:  karli
    *
    *
    * - Assignment   content:                      points:
    *   matrixMult:  &quot;Matrix Multiplication&quot;     5
    *   series:      &quot;Series&quot;                    6
    *   a3:          Integrals                     8
    *
    * - Room                  topic:  credits: doors:                 students: assignments:
    *   mathRoom:             math    17       null                   karli     [matrixMult series a3]
    *   artsRoom:             arts    16       mathRoom               null      null
    *   sportsRoom:           sports  25       [mathRoom artsRoom]
    *   examRoom:             exam     0       [sportsRoom artsRoom]
    *   softwareEngineering:  &quot;Software Engineering&quot; 42 [artsRoom examRoom]
    * </pre>
    * <h4><a name = 'step_2'>Step 2: Call YamlIdMap.decode:</a></h4>
    * <pre><code class="java" data-lang="java">
    *       YamlIdMap yamlIdMap = new YamlIdMap(&quot;org.sdmlib.test.examples.studyrightWithAssignments.model&quot;);
    *
    *       University studyRight = (University) yamlIdMap.decode(yaml);
    * </code></pre>
    * <h4><a name = 'step_3'>Step 3: Decoded object structure:</a></h4>
    * <img src="doc-files/YamlStep5.png" alt="YamlStep5.png" width='869'>
    * <p>Check: root object exists "Study " Right"And"Fast now"</p>
    * <h4><a name = 'step_4'>Step 4: Generate Yaml from model:</a></h4>
    * <pre>- u1: 	University
    *   name: 	&quot;\&quot;Study \&quot; Right\&quot;And\&quot;Fast now\&quot;&quot;
    *   students: 	s2
    *   rooms: 	r3 	r4 	r5 	r6 	r7
    *
    * - s2: 	Student
    *   assignmentPoints: 	0
    *   credits: 	0
    *   id: 	4242
    *   motivation: 	0
    *   name: 	karli
    *   in: 	r3
    *   university: 	u1
    *
    * - r3: 	Room
    *   credits: 	17
    *   topic: 	math
    *   doors: 	r4 	r5
    *   students: 	s2
    *   assignments: 	a8 	a9 	a10
    *   university: 	u1
    *
    * - r4: 	Room
    *   credits: 	16
    *   topic: 	arts
    *   doors: 	r3 	r5 	r6 	r7
    *   university: 	u1
    *
    * - r5: 	Room
    *   credits: 	25
    *   topic: 	sports
    *   doors: 	r3 	r4 	r6
    *   university: 	u1
    *
    * - r6: 	Room
    *   credits: 	0
    *   topic: 	exam
    *   doors: 	r5 	r4 	r7
    *   university: 	u1
    *
    * - r7: 	Room
    *   credits: 	42
    *   topic: 	&quot;Software Engineering&quot;
    *   doors: 	r4 	r6
    *   university: 	u1
    *
    * - a8: 	Assignment
    *   content: 	&quot;Matrix Multiplication&quot;
    *   points: 	5
    *   room: 	r3
    *
    * - a9: 	Assignment
    *   content: 	Series
    *   points: 	6
    *   room: 	r3
    *
    * - a10: 	Assignment
    *   content: 	Integrals
    *   points: 	8
    *   room: 	r3
    *
    * </pre>
    * <p>Check: yaml starts with - u... true</p>
    * <h4><a name = 'step_5'>Step 5: decoded again:</a></h4>
    * <img src="doc-files/YamlStep11.png" alt="YamlStep11.png" width='876'>
    * <h4><a name = 'step_6'>Step 6: now read from excel file</a></h4>
    * <pre><code class="java" data-lang="java">
    *       byte[] readAllBytes = Files.readAllBytes(Paths.get(&quot;doc&#x2F;StudyRightStartSituation.txt&quot;));
    *       String excelText = new String(readAllBytes);
    *
    *       YamlIdMap excelIdMap = new YamlIdMap(&quot;org.sdmlib.test.examples.studyrightWithAssignments.model&quot;);
    *
    *       studyRight = (University) excelIdMap.decode(excelText);
    * </code></pre>
    * <p>doc/StudyRightStartSituation.txt</p>
    * <pre>-	studyRight:	University
    * 	name: 	&quot;&quot;&quot;Study Right&quot;&quot;&quot;
    * 	students:	karli
    * 	rooms: 	mathRoom	artsRoom	sportsRoom	examRoom	softwareEngineering
    *
    * -	karli:	Student
    * 	id:	4242
    * 	name:	karli
    *
    * -	Assignment	content:	points:
    * 	matrixMult:	&quot;&quot;&quot;Matrix Multiplication&quot;&quot;&quot;	5
    * 	series:	Series	6
    * 	a3:	Integrals	8
    *
    * -	Room	topic:	credits:	doors:	students:	assignments:
    * 	mathRoom:	math	17	null	karli	[matricMult series a3]
    * 	artsRoom:	arts	25	mathRoom
    * 	sportsRoom:	sports	25	[mathRoom artsRoom]
    * 	examRoom:	exam	0	[sportsRoom artsRoom]
    * 	softwareEngineering:	&quot;&quot;&quot;Software Engineering&quot;&quot;&quot;	42	[artsRoom examRoom]
    * </pre>
    * <p>result:</p>
    * <img src="doc-files/YamlStep17.png" alt="YamlStep17.png" width='795'>
    */
   private YamlIdMap()
   {
      // always pass package to constructor
   }



   /**
    *
    * <h3>Storyboard Yaml</h3>
    * <h4><a name = 'step_1'>Step 1: Read graph from yaml text:</a></h4>
    * <pre>- studyRight: University
    *   name:       &quot;\&quot;Study \&quot; Right\&quot;And\&quot;Fast now\&quot;&quot;
    *   students:   karli
    *   rooms:      mathRoom artsRoom sportsRoom examRoom softwareEngineering
    *
    * - karli: Student
    *   id:    4242
    *   name:  karli
    *
    *
    * - Assignment   content:                      points:
    *   matrixMult:  &quot;Matrix Multiplication&quot;     5
    *   series:      &quot;Series&quot;                    6
    *   a3:          Integrals                     8
    *
    * - Room                  topic:  credits: doors:                 students: assignments:
    *   mathRoom:             math    17       null                   karli     [matrixMult series a3]
    *   artsRoom:             arts    16       mathRoom               null      null
    *   sportsRoom:           sports  25       [mathRoom artsRoom]
    *   examRoom:             exam     0       [sportsRoom artsRoom]
    *   softwareEngineering:  &quot;Software Engineering&quot; 42 [artsRoom examRoom]
    * </pre>
    * <h4><a name = 'step_2'>Step 2: Call YamlIdMap.decode:</a></h4>
    * <pre><code class="java" data-lang="java">
    *       YamlIdMap yamlIdMap = new YamlIdMap(&quot;org.sdmlib.test.examples.studyrightWithAssignments.model&quot;);
    *
    *       University studyRight = (University) yamlIdMap.decode(yaml);
    * </code></pre>
    * <h4><a name = 'step_3'>Step 3: Decoded object structure:</a></h4>
    * <img src="doc-files/YamlStep5.png" alt="YamlStep5.png" width='869'>
    * <p>Check: root object exists "Study " Right"And"Fast now"</p>
    * <h4><a name = 'step_4'>Step 4: Generate Yaml from model:</a></h4>
    * <pre>- u1: 	University
    *   name: 	&quot;\&quot;Study \&quot; Right\&quot;And\&quot;Fast now\&quot;&quot;
    *   students: 	s2
    *   rooms: 	r3 	r4 	r5 	r6 	r7
    *
    * - s2: 	Student
    *   assignmentPoints: 	0
    *   credits: 	0
    *   id: 	4242
    *   motivation: 	0
    *   name: 	karli
    *   in: 	r3
    *   university: 	u1
    *
    * - r3: 	Room
    *   credits: 	17
    *   topic: 	math
    *   doors: 	r4 	r5
    *   students: 	s2
    *   assignments: 	a8 	a9 	a10
    *   university: 	u1
    *
    * - r4: 	Room
    *   credits: 	16
    *   topic: 	arts
    *   doors: 	r3 	r5 	r6 	r7
    *   university: 	u1
    *
    * - r5: 	Room
    *   credits: 	25
    *   topic: 	sports
    *   doors: 	r3 	r4 	r6
    *   university: 	u1
    *
    * - r6: 	Room
    *   credits: 	0
    *   topic: 	exam
    *   doors: 	r5 	r4 	r7
    *   university: 	u1
    *
    * - r7: 	Room
    *   credits: 	42
    *   topic: 	&quot;Software Engineering&quot;
    *   doors: 	r4 	r6
    *   university: 	u1
    *
    * - a8: 	Assignment
    *   content: 	&quot;Matrix Multiplication&quot;
    *   points: 	5
    *   room: 	r3
    *
    * - a9: 	Assignment
    *   content: 	Series
    *   points: 	6
    *   room: 	r3
    *
    * - a10: 	Assignment
    *   content: 	Integrals
    *   points: 	8
    *   room: 	r3
    *
    * </pre>
    * <p>Check: yaml starts with - u... true</p>
    * <h4><a name = 'step_5'>Step 5: decoded again:</a></h4>
    * <img src="doc-files/YamlStep11.png" alt="YamlStep11.png" width='876'>
    * <h4><a name = 'step_6'>Step 6: now read from excel file</a></h4>
    * <pre><code class="java" data-lang="java">
    *       byte[] readAllBytes = Files.readAllBytes(Paths.get(&quot;doc&#x2F;StudyRightStartSituation.txt&quot;));
    *       String excelText = new String(readAllBytes);
    *
    *       YamlIdMap excelIdMap = new YamlIdMap(&quot;org.sdmlib.test.examples.studyrightWithAssignments.model&quot;);
    *
    *       studyRight = (University) excelIdMap.decode(excelText);
    * </code></pre>
    * <p>doc/StudyRightStartSituation.txt</p>
    * <pre>-	studyRight:	University
    * 	name: 	&quot;&quot;&quot;Study Right&quot;&quot;&quot;
    * 	students:	karli
    * 	rooms: 	mathRoom	artsRoom	sportsRoom	examRoom	softwareEngineering
    *
    * -	karli:	Student
    * 	id:	4242
    * 	name:	karli
    *
    * -	Assignment	content:	points:
    * 	matrixMult:	&quot;&quot;&quot;Matrix Multiplication&quot;&quot;&quot;	5
    * 	series:	Series	6
    * 	a3:	Integrals	8
    *
    * -	Room	topic:	credits:	doors:	students:	assignments:
    * 	mathRoom:	math	17	null	karli	[matricMult series a3]
    * 	artsRoom:	arts	25	mathRoom
    * 	sportsRoom:	sports	25	[mathRoom artsRoom]
    * 	examRoom:	exam	0	[sportsRoom artsRoom]
    * 	softwareEngineering:	&quot;&quot;&quot;Software Engineering&quot;&quot;&quot;	42	[artsRoom examRoom]
    * </pre>
    * <p>result:</p>
    * <img src="doc-files/YamlStep17.png" alt="YamlStep17.png" width='795'>
    */
   public YamlIdMap(String... packageNames)
   {
      Objects.requireNonNull(packageNames);
      List<String> list = Arrays.asList(packageNames);
      this.packageNames = new ArrayList<String>(list);
      reflectorMap = new ReflectorMap(list);
   }



   public Object decodeCSV(String fileName)
   {
      try
      {
         byte[] bytes = Files.readAllBytes(Paths.get(fileName));

         String csvText = new String(bytes);

         String yamlText = convertCsv2Yaml(csvText);

         // System.out.println(yamlText);

         return this.decode(yamlText);

      } catch (IOException e)
      {
         Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
      }

      return null;
   }

   private String convertCsv2Yaml(String csvText)
   {
      String[] split = csvText.split(";");

      for (int i = 0; i < split.length; i++)
      {
         String token = split[i];


         if (token.startsWith("\"") && token.endsWith("\""))

         {
            // already done
            continue;
         }

         if (token.startsWith("\"") && !token.endsWith("\""))
         {
            // there is a semicolon within "   ;   " , recombine it
            int j = i;
            String nextToken;
            while (true)
            {
               j++;
               nextToken = split[j];
               split[j] = "";
               token = token + ";" + nextToken;
               if (nextToken.endsWith("\""))
               {
                  split[i] = token;
                  i = j;
                  break;
               }
            }
            continue;
         }

         if (token.trim().length() == 0)
         {
            continue;
         }

         Pattern pattern = Pattern.compile("\\s");
         Matcher matcher = pattern.matcher(token.trim());
         boolean found = matcher.find();

         if (found)
         {
            token = YamlGenerator.encapsulate(token);
            split[i] = token;
         }
      }

      StringBuilder buf = new StringBuilder();

      for (String str : split)
      {
         buf.append(str).append(" ");
      }

      return buf.toString();
   }



   public Object decode(String yaml, Object root)
   {
      getOrCreateKey(root);
      Object decodedRoot = decode(yaml);

      if (decodedRoot != root)
      {
         throw new RuntimeException("Object passed as root does not match the first object in the yaml string.\n" +
               "Ensure that the type of the passed root and the first object in the yaml string match. \n" +
               "Ensure that the key of the passed root and the key of the first object in tha yaml string match. \n" +
               "You get the key of the passed root object via 'String key = getOrCreateKey(root);'\n");
      }

      return root;
   }


   public Object decode(String yaml)
   {
      decodingPropertyChange = false;
      yamlChangeText = null;

      this.yaml = yaml;
      Object root = null;

      yamler = new Yamler(yaml);

      if ( ! yamler.getCurrentToken().equals("-"))
      {
         return yamler.decode(yaml);
      }

      root = parseObjectIds();

      yamler = new Yamler(yaml);

      parseObjectAttrs();

      // reset property change decoding
      this.setDecodingPropertyChange(false);

      yamlChangeText = null;

      return root;
   }


   private void parseObjectAttrs()
   {
      while ( yamler.getCurrentToken() != null)
      {
         if ( ! "-".equals(yamler.getCurrentToken()) )
         {
            yamler.printError("'-' expected");
            yamler.nextToken();
            continue;
         }

         String key = yamler.nextToken();

         if ( key.endsWith(":"))
         {
            // usual
            parseUsualObjectAttrs();
         }
         else
         {
            parseObjectTableAttrs();
         }
      }

   }

   private void parseObjectTableAttrs()
   {
      // skip column names
      String className = yamler.getCurrentToken();

      Reflector creator = reflectorMap.getReflector(className);
      yamler.nextToken();

      ArrayList<String> colNameList = new ArrayList<String>();

      while ( yamler.getCurrentToken() != null && yamler.getLookAheadToken() != null && yamler.getLookAheadToken().endsWith(":"))
      {
         String colName = yamler.stripColon(yamler.getCurrentToken());
         colNameList.add(colName);
         yamler.nextToken();
      }

      while ( yamler.getCurrentToken() != null  && ! "-".equals(yamler.getCurrentToken()))
      {
         String objectId = yamler.stripColon(yamler.getCurrentToken());
         yamler.nextToken();

         Object obj = objIdMap.get(objectId);

         // column values
         int colNum = 0;
         while ( yamler.getCurrentToken() != null && ! yamler.getCurrentToken().endsWith(":") && ! "-".equals(yamler.getCurrentToken()))
         {
            String attrName = colNameList.get(colNum);

            if (yamler.getCurrentToken().startsWith("["))
            {
               String value = yamler.getCurrentToken().substring(1);
               if (value.trim().equals(""))
               {
                  value = yamler.nextToken();
               }
               setValue(creator, obj, attrName, value);

               while (yamler.getCurrentToken() != null && ! yamler.getCurrentToken().endsWith("]") )
               {
                  yamler.nextToken();
                  value = yamler.getCurrentToken();
                  if (yamler.getCurrentToken().endsWith("]"))
                  {
                     value = yamler.getCurrentToken().substring(0, yamler.getCurrentToken().length()-1);
                  }
                  if ( ! value.trim().equals(""))
                  {
                     setValue(creator, obj, attrName, value);
                  }
               }
            }
            else
            {
               setValue(creator, obj, attrName, yamler.getCurrentToken());
            }
            colNum++;
            yamler.nextToken();
         }
      }
   }

   private void parseUsualObjectAttrs()
   {
      String objectId = yamler.stripColon(yamler.getCurrentToken());
      String className = yamler.nextToken();
      yamler.nextToken();

      if (className.endsWith(".remove"))
      {
         objIdMap.remove(objectId);

         // skip time stamp, if necessary
         while ( yamler.getCurrentToken() != null
               && ! yamler.getCurrentToken().equals("-"))
         {
            yamler.nextToken();
         }
         return;
      }

      if (className.equals(".Map"))
      {
         YamlObject yamlObj = (YamlObject) objIdMap.get(objectId);
         Map<String, Object> map = yamlObj.getProperties();

         while ( yamler.getCurrentToken() != null && !yamler.getCurrentToken().equals("-"))
         {
            String attrName = yamler.stripColon(yamler.getCurrentToken());
            yamler.nextToken();

            if (map == null)
            {
               // no object created by parseObjectIds. Object has been removed.
               // ignore attr changes
               while ( yamler.getCurrentToken() != null
                     && !yamler.getCurrentToken().endsWith(":")
                     && !yamler.getCurrentToken().equals("-"))
               {
                  yamler.nextToken();
               }
               continue;
            }

            // many values
            ArrayList<Object> previousValue = null;
            while ( yamler.getCurrentToken() != null
                  && !yamler.getCurrentToken().endsWith(":")
                  && !yamler.getCurrentToken().equals("-"))
            {
               String attrValue = yamler.getCurrentToken();

               Object target = this.objIdMap.get(attrValue);

               if (target != null)
               {
                  if (previousValue != null)
                  {
                     previousValue.add(target);
                     map.put(attrName, previousValue);
                  }
                  else
                  {
                     map.put(attrName, target);
                     previousValue = new ArrayList<>();
                     previousValue.add(target);
                  }
               }
               else
               {
                  if (previousValue != null)
                  {
                     previousValue.add(attrValue);
                     map.put(attrName, previousValue);
                  }
                  else
                  {
                     map.put(attrName, attrValue);
                     previousValue = new ArrayList<>();
                     previousValue.add(attrValue);
                  }
               }

               yamler.nextToken();
            }
         }
      }
      else
      {
         Reflector reflector = reflectorMap.getReflector(className);

         Object obj = objIdMap.get(objectId);

         // read attributes
         while (yamler.getCurrentToken() != null && !yamler.getCurrentToken().equals("-"))
         {
            String attrName = yamler.stripColon(yamler.getCurrentToken());
            yamler.nextToken();

            if (obj == null)
            {
               // no object created by parseObjectIds. Object has been removed.
               // ignore attr changes
               while (yamler.getCurrentToken() != null
                     && !yamler.getCurrentToken().endsWith(":")
                     && !yamler.getCurrentToken().equals("-"))
               {
                  yamler.nextToken();
               }
               continue;
            }

            // many values
            while (yamler.getCurrentToken() != null
                  && !yamler.getCurrentToken().endsWith(":")
                  && !yamler.getCurrentToken().equals("-"))
            {
               String attrValue = yamler.getCurrentToken();

               if (yamler.getLookAheadToken() != null && yamler.getLookAheadToken().endsWith(".time:"))
               {
                  String propWithTime = yamler.nextToken();
                  String newTimeStamp = yamler.nextToken();
                  String oldTimeStamp = attrTimeStamps.get(objectId + "." + attrName);

                  if (oldTimeStamp == null || oldTimeStamp.compareTo(newTimeStamp) <= 0)
                  {
                     this.setDecodingPropertyChange(true);

                     if (yamlChangeText == null)
                     {
                        yamlChangeText = yaml;
                     }

                     setValue(reflector, obj, attrName, attrValue);
                     attrTimeStamps.put(objectId + "." + attrName, newTimeStamp);
                  }
               }
               else
               {
                  setValue(reflector, obj, attrName, attrValue);
               }

               yamler.nextToken();
            }
         }
      }
   }

   private void setValue(Reflector reflector, Object obj, String attrName, String attrValue)
   {
      String type = "new";

      if (attrName.endsWith(".remove"))
      {
         attrName = attrName.substring(0, attrName.length() - ".remove".length());

         if (reflector.getValue(obj, attrName) instanceof Collection)
         {
            type = REMOVE;
         }
         else
         {
            attrValue = null;
         }
      }

      try
      {
         Object setResult = reflector.setValue(obj, attrName, attrValue, type);

         if (setResult == null)
         {
            Object targetObj = objIdMap.get(attrValue);
            if (targetObj != null)
            {
               reflector.setValue(obj, attrName, targetObj, type);
            }
         }
      }
      catch (Exception e)
      {
         // maybe a node
         Object targetObj = objIdMap.get(attrValue);
         if (targetObj != null)
         {
            reflector.setValue(obj, attrName, targetObj, type);
         }
      }
   }

   private Object parseObjectIds()
   {
      Object root = null;
      while ( yamler.getCurrentToken() != null)
      {
         if ( ! "-".equals(yamler.getCurrentToken()) )
         {
            yamler.printError("'-' expected");
            yamler.nextToken();
            continue;
         }

         String key = yamler.nextToken();

         if ( key.endsWith(":"))
         {
            // usual
            Object now = parseUsualObjectId();
            if (root == null) root = now;
            continue;
         }
         else
         {
            Object now = parseObjectTableIds();
            if (root == null) root = now;
            continue;
         }
      }

      return root;
   }


   private Object parseUsualObjectId()
   {
      String objectId = yamler.stripColon(yamler.getCurrentToken());
      int pos = objectId.lastIndexOf('.');
      String numPart = objectId.substring(pos + 2);
      int objectNum = 0;

      try
      {
         objectNum = Integer.parseInt(numPart);
      }
      catch (NumberFormatException e)
      {
         objectNum = objIdMap.size() + 1;
      }

      if (objectNum > maxUsedIdNum)
      {
         maxUsedIdNum = objectNum;
      }

      String className = yamler.nextToken();

      Object obj = objIdMap.get(objectId);

      String userId = null;

      // skip attributes
      while ( yamler.getCurrentToken() != null && ! yamler.getCurrentToken().equals("-"))
      {
         String token = yamler.nextToken();
         if (token != null && token.endsWith(".time:"))
         {
            token = yamler.nextToken();

            userId = token.substring(token.lastIndexOf('.') + 1);
         }
      }

      boolean foreignChange = false;

      if (userId != null)
      {
         int dotIndex = objectId.indexOf('.');

         if (dotIndex > 0)
         {
            String ownerId = objectId.substring(0, dotIndex);
            foreignChange = ! userId.equals(ownerId);
         }
      }

      if (obj == null && ! className.endsWith(".remove") && ! foreignChange)
      {
         if (className.equals(".Map"))
         {
            obj = new YamlObject();
            ((YamlObject) obj).put(".id", objectId);
         }
         else
         {
            Reflector reflector = reflectorMap.getReflector(className);
            obj = reflector.newInstance();
         }

         objIdMap.put(objectId, obj);
         idObjMap.put(obj, objectId);
      }

      return obj;
   }

   private Object parseObjectTableIds()
   {
      Object root = null;

      // skip column names
      String className = yamler.getCurrentToken();

      Reflector reflector = reflectorMap.getReflector(className);

      while ( ! "".equals(yamler.getCurrentToken()) && yamler.getLookAheadToken().endsWith(":"))
      {
         yamler.nextToken();
      }

      while ( ! "".equals(yamler.getCurrentToken()) && ! "-".equals(yamler.getCurrentToken()))
      {
         String objectId = yamler.stripColon(yamler.getCurrentToken());
         yamler.nextToken();

         Object obj = reflector.newInstance();

         objIdMap.put(objectId, obj);
         idObjMap.put(obj, objectId);

         if (root == null) root = obj;

         // skip column values
         while (! "".equals(yamler.getCurrentToken()) && ! yamler.getCurrentToken().endsWith(":") && ! "-".equals(yamler.getCurrentToken()))
         {
            yamler.nextToken();
         }
      }

      return root;
   }


   private Object parseObjList(String key, String second)
   {
      return null;
   }


   ReflectorMap reflectorMap;

   public Reflector getReflector(Object obj)
   {
      return reflectorMap.getReflector(obj);
   }



   public Object getObject(String objId)
   {
      return objIdMap.get(objId);
   }

   public String encode(Object... rootObjList)
   {
      Objects.requireNonNull(rootObjList);

      StringBuilder buf = new StringBuilder();

      collectObjects(rootObjList);

      for ( Entry<String, Object> entry : objIdMap.entrySet())
      {
         String key = entry.getKey();
         Object obj = entry.getValue();
         String className = obj.getClass().getSimpleName();


         buf.append("- ").append(key).append(": \t").append(className).append("\n");

         // attrs
         Reflector creator = getReflector(obj);

         for (String prop : creator.getOwnProperties())
         {
            Object value = creator.getValue(obj, prop);

            if (value == null)
            {
               continue;
            }

            if (value instanceof Collection) {
               if (((Collection) value).isEmpty())
               {
                  continue;
               }

               buf.append("  ").append(prop).append(": \t");
               for (Object valueObj : (Collection) value)
               {
                  String valueKey = idObjMap.get(valueObj);
                  buf.append(valueKey).append(" \t");
               }
               buf.append("\n");
            } else if (value instanceof Map){
               continue;
            } else {
               String valueKey = idObjMap.get(value);

               if (valueKey != null)
               {
                  buf.append("  ").append(prop).append(": \t").append(valueKey).append("\n");
               }
               else
               {
                  if (value instanceof String)
                  {
                     value = YamlGenerator.encapsulate((String) value);
                  }
                  buf.append("  ").append(prop).append(": \t").append(value).append("\n");
               }

               // add time stamp?
               if (userId != null)
               {
                  String timeKey =  key + "." + prop;
                  String timeStamp = attrTimeStamps.get(timeKey);

                  if (timeStamp != null)
                  {
                     buf.append("  ").append(prop).append(".time: \t").append(timeStamp).append("\n");
                  }
               }
            }
         }
         buf.append("\n");
      }

      return buf.toString();
   }

   public YamlIdMap putNameObject(String name, Object object)
   {

      String oldKey = idObjMap.get(object);
      if (oldKey != null)
      {
         objIdMap.remove(oldKey);
         idObjMap.remove(object);
      }

      collectObjects(object);

      objIdMap.put(name, object);
      idObjMap.put(object, name);

      return this;
   }

   public LinkedHashSet<Object> collectObjects(Object... rootObjList) {
      LinkedList<Object> simpleList = new LinkedList<>();
      LinkedHashSet<Object> collectedObjects = new LinkedHashSet<>();

      for (Object obj : rootObjList) {
         simpleList.add(obj);
      }


      // collect objects
      while ( ! simpleList.isEmpty()) {
         Object obj = simpleList.get(0);
         simpleList.remove(0);
         collectedObjects.add(obj);

         // already known?
         String key = idObjMap.get(obj);

         if (key == null) {
            // add to map
            key = addToObjIdMap(obj);

            // find neighbors
            Reflector reflector = getReflector(obj);

            for (String prop : reflector.getOwnProperties()) {
               Object value = reflector.getValue(obj, prop);

               if (value == null) {
                  continue;
               }

               Class valueClass = value.getClass();

               if (value instanceof Collection) {
                  for (Object valueObj : (Collection) value) {
                     valueClass = valueObj.getClass();

                     if (valueClass.getName().startsWith("java.lang")) break;

                     simpleList.add(valueObj);
                  }
               } else if (  valueClass.getName().startsWith("java.util.")) {
                  continue; // not (yet) supported
               } else if (  valueClass.getName().startsWith("java.lang.")) {
                  continue;
               } else {
                  simpleList.add(value);
               }
            }
         }

      } // collect objects
      return collectedObjects;
   }

   private void encodePropertyChange(StringBuilder buf, Object obj)
   {
      PropertyChangeEvent event = (PropertyChangeEvent) obj;
      obj = event.getSource();
      String propertyName = event.getPropertyName();
      Object value = event.getNewValue();
      String className = obj.getClass().getSimpleName();

      if (propertyName.equals(REMOVE_YOU))
      {
         // send - o42: C1.remove
         //        remove.time: 2018-03-11T22:11:02.123+01:00
         value = event.getOldValue();
         String valueKey = getOrCreateKey(value);
         buf.append("- ").append(valueKey).append(": \t").append(className).append(".remove\n");

         if (userId != null)
         {
            String now = "" + LocalDateTime.now() + "." + userId;
            buf.append("  ").append(className).append(".remove.time: \t").append(now).append("\n");
         }

         // remove it from our id map
         this.objIdMap.remove(valueKey);

         return;
      }

      if (value == null)
      {
         value = event.getOldValue();
         propertyName = propertyName + ".remove";

         if (value == null)
         {
            // no old nor new value, do nothing
            return;
         }
      }

      encodeAttrValue(buf, obj, propertyName, value);
   }

   public void encodeAttrValue(StringBuilder buf, Object obj, String propertyName, Object value)
   {
      // already known?
      String key = getOrCreateKey(obj);
      String className = obj.getClass().getSimpleName();
      buf.append("- ").append(key).append(": \t").append(className).append("\n");
      Class valueClass = value.getClass();

      if (  valueClass.getName().startsWith("java.lang.") || valueClass == String.class)
      {
         buf.append("  ").append(propertyName).append(": \t").append(YamlGenerator.encapsulate(value.toString()))
            .append("\n");
         if (userId != null)
         {
            String now = "" + LocalDateTime.now() + "." + userId;
            buf.append("  ").append(propertyName).append(".time: \t").append(now).append("\n");
            attrTimeStamps.put(key + "." + propertyName, now);
         }
      }
      else
      {
         // value is an object
         String valueKey = getOrCreateKey(value);

         buf.append("  ").append(propertyName).append(": \t").append(valueKey).append("\n");
         if (userId != null)
         {
            // add timestamp only for to-one assocs
            Reflector reflector = reflectorMap.getReflector(obj);
            String fieldName = propertyName;

            if (propertyName.endsWith(".remove"))
            {
               fieldName = propertyName.substring(0, propertyName.lastIndexOf('.'));
            }

            Object fieldValue = reflector.getValue(obj, fieldName);

            if (fieldValue == null || ! (fieldValue instanceof Collection))
            {
               String now = "" + LocalDateTime.now() + "." + userId;
               buf.append("  ").append(propertyName).append(".time: \t").append(now).append("\n");
               attrTimeStamps.put(key + "." + propertyName, now);
            }
            else if (fieldValue != null && fieldValue instanceof Collection)
            {
               String now = "" + LocalDateTime.now() + "." + userId;
               buf.append("  ").append(propertyName).append('.').append(valueKey).append(".time: \t").append(now).append("\n");
               attrTimeStamps.put(key + "." + propertyName + "." + valueKey, now);
            }
         }

         if (value != null && ! propertyName.endsWith(".remove"))
         {
            buf.append("- ").append(valueKey).append(": \t").append(valueClass.getSimpleName()).append("\n");
         }
      }
   }

   public String getOrCreateKey(Object obj)
   {
      String key = idObjMap.get(obj);

      if (key == null)
      {
         key = addToObjIdMap(obj);
      }
      return key;
   }

   private String addToObjIdMap(Object obj)
   {
      String className = obj.getClass().getSimpleName();

      String key = null;

      if (obj instanceof YamlObject)
      {
         YamlObject yamlObj = (YamlObject) obj;
         Object mapId = yamlObj.get(".id");
         key = (String) mapId;
      }

      if (key == null)
      {
         key = className.substring(0, 1).toLowerCase();
         Class<?> clazz = obj.getClass();
         try
         {
            Method getId = clazz.getMethod("getId");
            Object id = getId.invoke(obj);
            if (id != null)
            {
               key = id.toString().replaceAll("\\W+", "_");
            }
         }
         catch (Exception e)
         {
            try
            {
               Method getId = clazz.getMethod("getName");
               Object id = getId.invoke(obj);
               if (id != null)
               {
                  key = id.toString().replaceAll("\\W+", "_");
               }
            }
            catch (Exception e2)
            {
               // go with old key
            }
         }

         if (key.length() == 1)
         {
            key = key.substring(0, 1).toLowerCase();
         }
         else
         {
            key = key.substring(0, 1).toLowerCase() + key.substring(1);
         }

         maxUsedIdNum++;

         key += maxUsedIdNum;

         if (maxUsedIdNum > 1 && userId != null)
         {
            // all but the first get a userId prefix
            key = userId + "." + key;
         }

      }
      objIdMap.put(key, obj);
      idObjMap.put(obj, key);

      return key;
   }

   public YamlIdMap withUserId(String userId)
   {
      this.userId = userId;
      return this;
   }

   public boolean isDecodingPropertyChange()
   {
      return decodingPropertyChange;
   }

   public void setDecodingPropertyChange(boolean decodingPropertyChange)
   {
      this.decodingPropertyChange = decodingPropertyChange;
   }

   private String yamlChangeText = null;

   public String getYamlChange()
   {
      String result = yamlChangeText;
      yamlChangeText = "";
      return result;
   }

   public String getLastTimeStamps()
   {
      LinkedHashMap<String, String> user2TimeStampMap = getLastTimeStampMap();

      StringBuilder buf = new StringBuilder();
      for ( Entry<String, String> e  : user2TimeStampMap.entrySet())
      {
         buf.append(e.getValue()).append(" ");
      }

      return buf.toString();
   }

   public LinkedHashMap<String, String> getLastTimeStampMap(String lastTimeStamps)
   {
      LinkedHashMap<String, String> user2TimeStampMap = new LinkedHashMap<String, String>();

      String[] split = lastTimeStamps.split("\\s+");

      for (String s : split)
      {
         int pos = s.lastIndexOf('.');
         String user = s.substring(pos + 1);
         user2TimeStampMap.put(user, s);
      }

      return user2TimeStampMap;
   }

   public LinkedHashMap<String, String> getLastTimeStampMap()
   {
      LinkedHashMap<String, String> user2TimeStampMap = new LinkedHashMap<String, String>();

      for ( Entry<String, String> e  : attrTimeStamps.entrySet())
      {
         String timeStamp = e.getValue();
         int pos = timeStamp.lastIndexOf('.');
         String userName = timeStamp.substring(pos+1);
         String oldTimeStamp = user2TimeStampMap.get(userName);

         if (oldTimeStamp == null || oldTimeStamp.compareTo(timeStamp) < 0)
         {
            user2TimeStampMap.put(userName, timeStamp);
         }
      }
      return user2TimeStampMap;
   }

}
