package org.fulib.yaml;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * The IdMap maintains an one-to-one mapping from objects to IDs.
 * IDs are either automatically generated based on {@code id} or {@code name} properties or class names (see {@link
 * #putObject(Object)}, or explicitly definied (with {@link #putObject(Object)}.
 * Auto-generated IDs are suffixed with a running number if necessary.
 *
 * @since 1.2
 */
public class IdMap
{
   // =============== Fields ===============

   protected final ReflectorMap reflectorMap;

   protected String userId;
   protected int maxUsedIdNum = 0;

   protected final LinkedHashMap<String, Object> objIdMap = new LinkedHashMap<>();
   protected final LinkedHashMap<Object, String> idObjMap = new LinkedHashMap<>();

   // =============== Constructors ===============

   public IdMap(ReflectorMap reflectorMap)
   {
      this.reflectorMap = reflectorMap;
   }

   // =============== Properties ===============

   public String getUserId()
   {
      return this.userId;
   }

   public void setUserId(String userId)
   {
      this.userId = userId;
   }

   // =============== Methods ===============

   /**
    * @param object
    *    the object
    *
    * @return a reflector corresponding to the given object's class,
    * as given by {@link ReflectorMap#getReflector(Object)}
    */
   public Reflector getReflector(Object object)
   {
      return this.reflectorMap.getReflector(object);
   }

   /**
    * @param object
    *    the object
    *
    * @return the id of the given {@code object}, or {@code null} if not found
    */
   public String getId(Object object)
   {
      return this.idObjMap.get(object);
   }

   /**
    * @param id
    *    the id
    *
    * @return the object with the given id, or {@code null} if not found
    */
   public Object getObject(String id)
   {
      return this.objIdMap.get(id);
   }

   /**
    * Adds the object with an automatically generated ID.
    * It is generated as follows:
    *
    * <ol>
    *    <li>If the object has a {@code getId()} method, we use that as the base ID
    *    by replacing any non-word characters (regex {@code \W}) with {@code _}.</li>
    *    <li>If the object has no {@code getId()} method but a {@code getName()} method,
    *    the base ID becomes the result of that with the same replacement applied</li>
    *    <li>Otherwise, the base ID is the first character of the {@linkplain Class#getSimpleName() simple name}
    *    of the object's class</li>
    * </ol>
    * <p>
    * Then,
    *
    * <ol>
    *    <li>the base ID's first character is converted to lowercase</li>
    *    <li>if an object is already present under the base ID, a running number is appended</li>
    *    <li>if a {@linkplain #getUserId() user ID} is specified and the object is not the first to be added,
    *    the user ID followed by a period are prepended to the base ID</li>
    * </ol>
    *
    * @param object
    *    the object to add
    *
    * @return the auto-generated ID
    */
   public String putObject(Object object)
   {
      String key = this.idObjMap.get(object);

      if (key == null)
      {
         key = this.addToObjIdMap(object);
      }
      return key;
   }

   /**
    * Adds the {@code object} with the given {@code id}.
    * If there is already an object with that id, the old object is removed.
    *
    * @param id
    *    the id
    * @param object
    *    the object
    */
   public void putObject(String id, Object object)
   {
      String oldKey = this.idObjMap.get(object);
      if (oldKey != null)
      {
         this.objIdMap.remove(oldKey);
         this.idObjMap.remove(object);
      }

      this.objIdMap.put(id, object);
      this.idObjMap.put(object, id);
   }

   /**
    * Discovers all objects reachable from the {@code root} and within the packages managed by the {@link ReflectorMap}
    * that was given in the constructor, and adds them via {@link #putObject(Object)}.
    *
    * @param root
    *    the root object
    *
    * @see ReflectorMap#discoverObjects(Object)
    */
   public void discoverObjects(Object root)
   {
      this.reflectorMap.discoverObjects(root).forEach(this::putObject);
   }

   /**
    * Discovers all objects reachable from the {@code roots} and within the packages managed by the {@link ReflectorMap}
    * that was given in the constructor, and adds them via {@link #putObject(Object)}.
    *
    * @param roots
    *    the root objects
    *
    * @see ReflectorMap#discoverObjects(Object...)
    */
   public void discoverObjects(Object... roots)
   {
      this.reflectorMap.discoverObjects(roots).forEach(this::putObject);
   }

   /**
    * Discovers all objects reachable from the {@code roots} and within the packages managed by the {@link ReflectorMap}
    * that was given in the constructor, and adds them via {@link #putObject(Object)}.
    *
    * @param roots
    *    the root objects
    *
    * @see ReflectorMap#discoverObjects(Collection)
    */
   public void discoverObjects(Collection<?> roots)
   {
      this.reflectorMap.discoverObjects(roots).forEach(this::putObject);
   }

   // --------------- Helpers ---------------

   private String addToObjIdMap(Object obj)
   {
      final String key = this.generateUniqueKey(obj);
      this.objIdMap.put(key, obj);
      this.idObjMap.put(obj, key);
      return key;
   }

   private String generateUniqueKey(Object obj)
   {
      if (obj instanceof YamlObject)
      {
         YamlObject yamlObj = (YamlObject) obj;
         return yamlObj.getId();
      }

      String key = getIntrinsicKey(obj);
      key = StrUtil.downFirstChar(key);
      key = this.makeUnique(key);
      key = this.addUserId(key);
      return key;
   }

   private static String getIntrinsicKey(Object obj)
   {
      final Class<?> clazz = obj.getClass();
      final String id = getKeyFromProperty(obj, clazz, "getId");
      if (id != null)
      {
         return id;
      }

      final String name = getKeyFromProperty(obj, clazz, "getName");
      if (name != null)
      {
         return name;
      }

      return obj.getClass().getSimpleName().substring(0, 1);
   }

   private String makeUnique(String key)
   {
      if (this.objIdMap.get(key) != null)
      {
         // key is already in use
         this.maxUsedIdNum++;
         key += this.maxUsedIdNum;
      }
      return key;
   }

   private String addUserId(String key)
   {
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
}
