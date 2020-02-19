package org.fulib.yaml;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;

/**
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

   public Reflector getReflector(Object obj)
   {
      return this.reflectorMap.getReflector(obj);
   }

   public String getId(Object object)
   {
      return this.idObjMap.get(object);
   }

   public Object getObject(String objId)
   {
      return this.objIdMap.get(objId);
   }

   public String putObject(Object object)
   {
      String key = this.idObjMap.get(object);

      if (key == null)
      {
         key = this.addToObjIdMap(object);
      }
      return key;
   }

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
