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
 * <h3>Storyboard Yaml</h3>
 * <h4><a>Step 1: Read graph from yaml text:</a></h4>
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
 * <h4><a>Step 2: Call YamlIdMap.decode:</a></h4>
 * <pre><code>
 *       YamlIdMap yamlIdMap = new YamlIdMap(&quot;org.sdmlib.test.examples.studyrightWithAssignments.model&quot;);
 *
 *       University studyRight = (University) yamlIdMap.decode(yaml);
 * </code></pre>
 * <h4><a>Step 3: Decoded object structure:</a></h4>
 * <img src="doc-files/YamlStep5.png" alt="YamlStep5.png" width='869'>
 * <p>Check: root object exists "Study " Right"And"Fast now"</p>
 * <h4><a>Step 4: Generate Yaml from model:</a></h4>
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
 * <h4><a>Step 5: decoded again:</a></h4>
 * <img src="doc-files/YamlStep11.png" alt="YamlStep11.png" width='876'>
 * <h4><a>Step 6: now read from excel file</a></h4>
 * <pre><code>
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
   // =============== Constants ===============

   private static final String REMOVE = "remove";
   private static final String REMOVE_YOU = "removeYou";

   // =============== Fields ===============

   private String yaml;
   private String userId;
   private boolean decodingPropertyChange;

   private LinkedHashMap<String, Object> objIdMap = new LinkedHashMap<>();
   private LinkedHashMap<Object, String> idObjMap = new LinkedHashMap<>();

   private int maxUsedIdNum = 0;

   private Yamler yamler = new Yamler();

   private HashMap<String, String> attrTimeStamps = new HashMap<>();

   ReflectorMap reflectorMap;

   private String yamlChangeText;

   // =============== Constructors ===============

   /**
    * @param packageName
    *    the names of the package in which model classes reside
    *
    * @since 1.2
    */
   public YamlIdMap(String packageName)
   {
      this.reflectorMap = new ReflectorMap(packageName);
   }

   /**
    * @param packageNames
    *    the names of the packages in which model classes reside
    */
   public YamlIdMap(String... packageNames)
   {
      this.reflectorMap = new ReflectorMap(packageNames);
   }

   /**
    * @param packageNames
    *    the names of the packages in which model classes reside
    *
    * @since 1.2
    */
   public YamlIdMap(Collection<String> packageNames)
   {
      this.reflectorMap = new ReflectorMap(packageNames);
   }

   // =============== Properties ===============

   /**
    * @deprecated since 1.2; use {@link #getObject(String)} instead
    */
   @Deprecated
   public LinkedHashMap<String, Object> getObjIdMap()
   {
      return this.objIdMap;
   }

   /**
    * @deprecated since 1.2; use {@link #getId(Object)} instead
    */
   @Deprecated
   public LinkedHashMap<Object, String> getIdObjMap()
   {
      return this.idObjMap;
   }

   /**
    * @deprecated since 1.2; use {@link #getAttributeTimeStamp(String)} instead
    */
   @Deprecated
   public HashMap<String, String> getAttrTimeStamps()
   {
      return this.attrTimeStamps;
   }

   public YamlIdMap withUserId(String userId)
   {
      this.userId = userId;
      return this;
   }

   public boolean isDecodingPropertyChange()
   {
      return this.decodingPropertyChange;
   }

   public void setDecodingPropertyChange(boolean decodingPropertyChange)
   {
      this.decodingPropertyChange = decodingPropertyChange;
   }

   // =============== Methods ===============

   // --------------- CSV ---------------

   public Object decodeCSV(String fileName)
   {
      try
      {
         byte[] bytes = Files.readAllBytes(Paths.get(fileName));

         String csvText = new String(bytes);

         String yamlText = this.convertCsv2Yaml(csvText);

         // System.out.println(yamlText);

         return this.decode(yamlText);
      }
      catch (IOException e)
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

   // --------------- Decoding ---------------

   public Object decode(String yaml, Object root)
   {
      this.getOrCreateKey(root);
      Object decodedRoot = this.decode(yaml);

      if (decodedRoot != root)
      {
         throw new RuntimeException("Object passed as root does not match the first object in the yaml string.\n"
                                    + "Ensure that the type of the passed root and the first object in the yaml string match. \n"
                                    + "Ensure that the key of the passed root and the key of the first object in tha yaml string match. \n"
                                    + "You get the key of the passed root object via 'String key = getOrCreateKey(root);'\n");
      }

      return root;
   }

   public Object decode(String yaml)
   {
      this.decodingPropertyChange = false;
      this.yamlChangeText = null;

      this.yaml = yaml;
      Object root;

      this.yamler = new Yamler(yaml);

      if (!"-".equals(this.yamler.getCurrentToken()))
      {
         return this.yamler.decode(yaml);
      }

      root = this.parseObjectIds();

      this.yamler = new Yamler(yaml);

      this.parseObjectAttrs();

      // reset property change decoding
      this.setDecodingPropertyChange(false);

      this.yamlChangeText = null;

      return root;
   }

   // --------------- Parsing ---------------

   private void parseObjectAttrs()
   {
      while (this.yamler.getCurrentToken() != null)
      {
         if (!"-".equals(this.yamler.getCurrentToken()))
         {
            this.yamler.printError("'-' expected");
            this.yamler.nextToken();
            continue;
         }

         String key = this.yamler.nextToken();

         if (key.endsWith(":"))
         {
            // usual
            this.parseUsualObjectAttrs();
         }
         else
         {
            this.parseObjectTableAttrs();
         }
      }
   }

   private void parseObjectTableAttrs()
   {
      // skip column names
      String className = this.yamler.getCurrentToken();

      Reflector creator = this.reflectorMap.getReflector(className);
      this.yamler.nextToken();

      ArrayList<String> colNameList = new ArrayList<>();

      while (this.yamler.getCurrentToken() != null && this.yamler.getLookAheadToken() != null
             && this.yamler.getLookAheadToken().endsWith(":"))
      {
         String colName = this.yamler.stripColon(this.yamler.getCurrentToken());
         colNameList.add(colName);
         this.yamler.nextToken();
      }

      while (this.yamler.getCurrentToken() != null && !"-".equals(this.yamler.getCurrentToken()))
      {
         String objectId = this.yamler.stripColon(this.yamler.getCurrentToken());
         this.yamler.nextToken();

         Object obj = this.objIdMap.get(objectId);

         // column values
         int colNum = 0;
         while (this.yamler.getCurrentToken() != null && !this.yamler.getCurrentToken().endsWith(":") && !"-".equals(
            this.yamler.getCurrentToken()))
         {
            String attrName = colNameList.get(colNum);

            if (this.yamler.getCurrentToken().startsWith("["))
            {
               String value = this.yamler.getCurrentToken().substring(1);
               if ("".equals(value.trim()))
               {
                  value = this.yamler.nextToken();
               }
               this.setValue(creator, obj, attrName, value);

               while (this.yamler.getCurrentToken() != null && !this.yamler.getCurrentToken().endsWith("]"))
               {
                  this.yamler.nextToken();
                  value = this.yamler.getCurrentToken();
                  if (this.yamler.getCurrentToken().endsWith("]"))
                  {
                     value = this.yamler.getCurrentToken().substring(0, this.yamler.getCurrentToken().length() - 1);
                  }
                  if (!"".equals(value.trim()))
                  {
                     this.setValue(creator, obj, attrName, value);
                  }
               }
            }
            else
            {
               this.setValue(creator, obj, attrName, this.yamler.getCurrentToken());
            }
            colNum++;
            this.yamler.nextToken();
         }
      }
   }

   private void parseUsualObjectAttrs()
   {
      String objectId = this.yamler.stripColon(this.yamler.getCurrentToken());
      String className = this.yamler.nextToken();
      this.yamler.nextToken();

      if (className.endsWith(".remove"))
      {
         this.objIdMap.remove(objectId);

         // skip time stamp, if necessary
         while (this.yamler.getCurrentToken() != null && !"-".equals(this.yamler.getCurrentToken()))
         {
            this.yamler.nextToken();
         }
         return;
      }

      if (".Map".equals(className))
      {
         YamlObject yamlObj = (YamlObject) this.objIdMap.get(objectId);
         Map<String, Object> map = yamlObj.getProperties();

         while (this.yamler.getCurrentToken() != null && !"-".equals(this.yamler.getCurrentToken()))
         {
            String attrName = this.yamler.stripColon(this.yamler.getCurrentToken());
            this.yamler.nextToken();

            if (map == null)
            {
               // no object created by parseObjectIds. Object has been removed.
               // ignore attr changes
               while (this.yamler.getCurrentToken() != null && !this.yamler.getCurrentToken().endsWith(":")
                      && !"-".equals(this.yamler.getCurrentToken()))
               {
                  this.yamler.nextToken();
               }
               continue;
            }

            // many values
            ArrayList<Object> previousValue = null;
            while (this.yamler.getCurrentToken() != null && !this.yamler.getCurrentToken().endsWith(":") && !"-".equals(
               this.yamler.getCurrentToken()))
            {
               String attrValue = this.yamler.getCurrentToken();

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

               this.yamler.nextToken();
            }
         }
      }
      else
      {
         Reflector reflector = this.reflectorMap.getReflector(className);

         Object obj = this.objIdMap.get(objectId);

         // read attributes
         while (this.yamler.getCurrentToken() != null && !"-".equals(this.yamler.getCurrentToken()))
         {
            String attrName = this.yamler.stripColon(this.yamler.getCurrentToken());
            this.yamler.nextToken();

            if (obj == null)
            {
               // no object created by parseObjectIds. Object has been removed.
               // ignore attr changes
               while (this.yamler.getCurrentToken() != null && !this.yamler.getCurrentToken().endsWith(":")
                      && !"-".equals(this.yamler.getCurrentToken()))
               {
                  this.yamler.nextToken();
               }
               continue;
            }

            // many values
            while (this.yamler.getCurrentToken() != null && !this.yamler.getCurrentToken().endsWith(":") && !"-".equals(
               this.yamler.getCurrentToken()))
            {
               String attrValue = this.yamler.getCurrentToken();

               if (this.yamler.getLookAheadToken() != null && this.yamler.getLookAheadToken().endsWith(".time:"))
               {
                  String propWithTime = this.yamler.nextToken();
                  String newTimeStamp = this.yamler.nextToken();
                  String oldTimeStamp = this.attrTimeStamps.get(objectId + "." + attrName);

                  if (oldTimeStamp == null || oldTimeStamp.compareTo(newTimeStamp) <= 0)
                  {
                     this.setDecodingPropertyChange(true);

                     if (this.yamlChangeText == null)
                     {
                        this.yamlChangeText = this.yaml;
                     }

                     this.setValue(reflector, obj, attrName, attrValue);
                     this.attrTimeStamps.put(objectId + "." + attrName, newTimeStamp);
                  }
               }
               else
               {
                  this.setValue(reflector, obj, attrName, attrValue);
               }

               this.yamler.nextToken();
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
            Object targetObj = this.objIdMap.get(attrValue);
            if (targetObj != null)
            {
               reflector.setValue(obj, attrName, targetObj, type);
            }
         }
      }
      catch (Exception e)
      {
         // maybe a node
         Object targetObj = this.objIdMap.get(attrValue);
         if (targetObj != null)
         {
            reflector.setValue(obj, attrName, targetObj, type);
         }
      }
   }

   private Object parseObjectIds()
   {
      Object root = null;
      while (this.yamler.getCurrentToken() != null)
      {
         if (!"-".equals(this.yamler.getCurrentToken()))
         {
            this.yamler.printError("'-' expected");
            this.yamler.nextToken();
            continue;
         }

         String key = this.yamler.nextToken();

         if (key.endsWith(":"))
         {
            // usual
            Object now = this.parseUsualObjectId();
            if (root == null)
            {
               root = now;
            }
         }
         else
         {
            Object now = this.parseObjectTableIds();
            if (root == null)
            {
               root = now;
            }
         }
      }

      return root;
   }

   private Object parseUsualObjectId()
   {
      String objectId = this.yamler.stripColon(this.yamler.getCurrentToken());
      int pos = objectId.lastIndexOf('.');
      String numPart = objectId.substring(pos + 2);
      int objectNum;

      try
      {
         objectNum = Integer.parseInt(numPart);
      }
      catch (NumberFormatException e)
      {
         objectNum = this.objIdMap.size() + 1;
      }

      if (objectNum > this.maxUsedIdNum)
      {
         this.maxUsedIdNum = objectNum;
      }

      String className = this.yamler.nextToken();

      Object obj = this.objIdMap.get(objectId);

      String userId = null;

      // skip attributes
      while (this.yamler.getCurrentToken() != null && !"-".equals(this.yamler.getCurrentToken()))
      {
         String token = this.yamler.nextToken();
         if (token != null && token.endsWith(".time:"))
         {
            token = this.yamler.nextToken();

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
            foreignChange = !userId.equals(ownerId);
         }
      }

      if (obj == null && !className.endsWith(".remove") && !foreignChange)
      {
         if (".Map".equals(className))
         {
            obj = new YamlObject(objectId);
         }
         else
         {
            Reflector reflector = this.reflectorMap.getReflector(className);
            obj = reflector.newInstance();
         }

         this.objIdMap.put(objectId, obj);
         this.idObjMap.put(obj, objectId);
      }

      return obj;
   }

   private Object parseObjectTableIds()
   {
      Object root = null;

      // skip column names
      String className = this.yamler.getCurrentToken();

      Reflector reflector = this.reflectorMap.getReflector(className);

      while (!"".equals(this.yamler.getCurrentToken()) && this.yamler.getLookAheadToken().endsWith(":"))
      {
         this.yamler.nextToken();
      }

      while (!"".equals(this.yamler.getCurrentToken()) && !"-".equals(this.yamler.getCurrentToken()))
      {
         String objectId = this.yamler.stripColon(this.yamler.getCurrentToken());
         this.yamler.nextToken();

         Object obj = reflector.newInstance();

         this.objIdMap.put(objectId, obj);
         this.idObjMap.put(obj, objectId);

         if (root == null)
         {
            root = obj;
         }

         // skip column values
         while (!"".equals(this.yamler.getCurrentToken()) && !this.yamler.getCurrentToken().endsWith(":")
                && !"-".equals(this.yamler.getCurrentToken()))
         {
            this.yamler.nextToken();
         }
      }

      return root;
   }

   // --------------- Object Access ---------------

   public Reflector getReflector(Object obj)
   {
      return this.reflectorMap.getReflector(obj);
   }

   /**
    * @since 1.2
    */
   public String getId(Object object)
   {
      return this.idObjMap.get(object);
   }

   public Object getObject(String objId)
   {
      return this.objIdMap.get(objId);
   }

   public YamlIdMap putNameObject(String name, Object object)
   {

      String oldKey = this.idObjMap.get(object);
      if (oldKey != null)
      {
         this.objIdMap.remove(oldKey);
         this.idObjMap.remove(object);
      }

      this.collectObjects(object);

      this.objIdMap.put(name, object);
      this.idObjMap.put(object, name);

      return this;
   }

   public String getOrCreateKey(Object obj)
   {
      String key = this.idObjMap.get(obj);

      if (key == null)
      {
         key = this.addToObjIdMap(obj);
      }
      return key;
   }

   private String addToObjIdMap(Object obj)
   {
      final String key = this.generateUniqueKey(obj);
      this.objIdMap.put(key, obj);
      this.idObjMap.put(obj, key);
      return key;
   }

   private String generateUniqueKey(Object obj)
   {
      String className = obj.getClass().getSimpleName();

      if (obj instanceof YamlObject)
      {
         YamlObject yamlObj = (YamlObject) obj;
         return yamlObj.getId();
      }

      String key;
      Class<?> clazz = obj.getClass();
      final String id = getKeyFromProperty(obj, clazz, "getId");
      if (id != null)
      {
         key = id;
      }
      else
      {
         final String name = getKeyFromProperty(obj, clazz, "getName");
         if (name != null)
         {
            key = name;
         }
         else
         {
            key = className.substring(0, 1);
         }
      }

      key = StrUtil.downFirstChar(key);

      if (this.objIdMap.get(key) != null)
      {
         // key is already in use
         this.maxUsedIdNum++;
         key += this.maxUsedIdNum;
      }

      if (this.maxUsedIdNum > 1 && this.userId != null)
      {
         // all but the first get a userId prefix
         key = this.userId + "." + key;
      }
      return key;
   }

   private static String getKeyFromProperty(Object obj, Class<?> clazz, String getterName)
   {
      try
      {
         Method getter = clazz.getMethod(getterName);
         Object result = getter.invoke(obj);
         if (result != null)
         {
            return result.toString().replaceAll("\\W+", "_");
         }
      }
      catch (Exception ignored)
      {
         // go with old key
      }
      return null;
   }

   public LinkedHashSet<Object> collectObjects(Object... rootObjList)
   {
      final LinkedHashSet<Object> collectedObjects = new LinkedHashSet<>();
      this.reflectorMap.discoverObjects(rootObjList, collectedObjects);
      for (final Object collectedObject : collectedObjects)
      {
         this.addToObjIdMap(collectedObject);
      }
      return collectedObjects;
   }

   // --------------- Encoding ---------------

   public String encode(Object... rootObjList)
   {
      Objects.requireNonNull(rootObjList);

      StringBuilder buf = new StringBuilder();

      this.collectObjects(rootObjList);

      for (Entry<String, Object> entry : this.objIdMap.entrySet())
      {
         String key = entry.getKey();
         Object obj = entry.getValue();
         String className = obj.getClass().getSimpleName();

         buf.append("- ").append(key).append(": \t").append(className).append("\n");

         // attrs
         Reflector creator = this.getReflector(obj);

         for (String prop : creator.getOwnProperties())
         {
            Object value = creator.getValue(obj, prop);

            if (value == null)
            {
               continue;
            }

            if (value instanceof Collection)
            {
               if (((Collection<?>) value).isEmpty())
               {
                  continue;
               }

               buf.append("  ").append(prop).append(": \t");
               for (Object valueObj : (Collection<?>) value)
               {
                  String valueKey = this.idObjMap.get(valueObj);
                  buf.append(valueKey).append(" \t");
               }
               buf.append("\n");
            }
            else if (value instanceof Map)
            {
            }
            else
            {
               String valueKey = this.idObjMap.get(value);

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
               if (this.userId != null)
               {
                  String timeKey = key + "." + prop;
                  String timeStamp = this.attrTimeStamps.get(timeKey);

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
         String valueKey = this.getOrCreateKey(value);
         buf.append("- ").append(valueKey).append(": \t").append(className).append(".remove\n");

         if (this.userId != null)
         {
            String now = "" + LocalDateTime.now() + "." + this.userId;
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

      this.encodeAttrValue(buf, obj, propertyName, value);
   }

   /**
    * @deprecated since 1.2; for internal use only
    */
   @Deprecated
   public void encodeAttrValue(StringBuilder buf, Object obj, String propertyName, Object value)
   {
      // already known?
      String key = this.getOrCreateKey(obj);
      String className = obj.getClass().getSimpleName();
      buf.append("- ").append(key).append(": \t").append(className).append("\n");
      Class<?> valueClass = value.getClass();

      if (valueClass.getName().startsWith("java.lang.") || valueClass == String.class)
      {
         buf.append("  ")
            .append(propertyName)
            .append(": \t")
            .append(YamlGenerator.encapsulate(value.toString()))
            .append("\n");
         if (this.userId != null)
         {
            String now = "" + LocalDateTime.now() + "." + this.userId;
            buf.append("  ").append(propertyName).append(".time: \t").append(now).append("\n");
            this.attrTimeStamps.put(key + "." + propertyName, now);
         }
      }
      else
      {
         // value is an object
         String valueKey = this.getOrCreateKey(value);

         buf.append("  ").append(propertyName).append(": \t").append(valueKey).append("\n");
         if (this.userId != null)
         {
            // add timestamp only for to-one assocs
            Reflector reflector = this.reflectorMap.getReflector(obj);
            String fieldName = propertyName;

            if (propertyName.endsWith(".remove"))
            {
               fieldName = propertyName.substring(0, propertyName.lastIndexOf('.'));
            }

            Object fieldValue = reflector.getValue(obj, fieldName);

            String now = LocalDateTime.now() + "." + this.userId;
            if (fieldValue instanceof Collection)
            {
               buf.append("  ")
                  .append(propertyName)
                  .append('.')
                  .append(valueKey)
                  .append(".time: \t")
                  .append(now)
                  .append("\n");
               this.attrTimeStamps.put(key + "." + propertyName + "." + valueKey, now);
            }
            else
            {
               buf.append("  ").append(propertyName).append(".time: \t").append(now).append("\n");
               this.attrTimeStamps.put(key + "." + propertyName, now);
            }
         }

         if (!propertyName.endsWith(".remove"))
         {
            buf.append("- ").append(valueKey).append(": \t").append(valueClass.getSimpleName()).append("\n");
         }
      }
   }

   // --------------- Yaml Change ---------------

   public String getYamlChange()
   {
      String result = this.yamlChangeText;
      this.yamlChangeText = "";
      return result;
   }

   // --------------- Time Stamps ---------------

   /**
    * @deprecated since 1.2; unused
    */
   @Deprecated
   public String getLastTimeStamps()
   {
      LinkedHashMap<String, String> user2TimeStampMap = this.getLastTimeStampMap();

      StringBuilder buf = new StringBuilder();
      for (Entry<String, String> e : user2TimeStampMap.entrySet())
      {
         buf.append(e.getValue()).append(" ");
      }

      return buf.toString();
   }

   /**
    * @since 1.2
    */
   public String getAttributeTimeStamp(String attribute)
   {
      return this.attrTimeStamps.get(attribute);
   }

   /**
    * @deprecated since 1.2; unused
    */
   @Deprecated
   public LinkedHashMap<String, String> getLastTimeStampMap()
   {
      LinkedHashMap<String, String> user2TimeStampMap = new LinkedHashMap<>();

      for (Entry<String, String> e : this.attrTimeStamps.entrySet())
      {
         String timeStamp = e.getValue();
         int pos = timeStamp.lastIndexOf('.');
         String userName = timeStamp.substring(pos + 1);
         String oldTimeStamp = user2TimeStampMap.get(userName);

         if (oldTimeStamp == null || oldTimeStamp.compareTo(timeStamp) < 0)
         {
            user2TimeStampMap.put(userName, timeStamp);
         }
      }
      return user2TimeStampMap;
   }

   /**
    * @deprecated since 1.2; unused
    */
   @Deprecated
   public LinkedHashMap<String, String> getLastTimeStampMap(String lastTimeStamps)
   {
      LinkedHashMap<String, String> user2TimeStampMap = new LinkedHashMap<>();

      String[] split = lastTimeStamps.split("\\s+");

      for (String s : split)
      {
         int pos = s.lastIndexOf('.');
         String user = s.substring(pos + 1);
         user2TimeStampMap.put(user, s);
      }

      return user2TimeStampMap;
   }
}
