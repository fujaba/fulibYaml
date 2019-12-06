package org.fulib.yaml;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class EventSource
{
   // =============== Constants ===============

   public static final String EVENT_KEY       = ".eventKey";
   public static final String EVENT_TIMESTAMP = ".eventTimestamp";
   public static final String EVENT_TYPE      = "eventType";

   // =============== Fields ===============

   private final Yamler yamler = new Yamler();

   private final ArrayList<Consumer<LinkedHashMap<String, String>>> eventListeners = new ArrayList<>();

   private final LinkedHashMap<String, Long> keyNumMap = new LinkedHashMap<>();

   private final TreeMap<Long, LinkedHashMap<String, String>> numEventMap = new TreeMap<>();

   private long lastEventTime;

   private long oldEventTimeStamp = 0;

   public DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

   // =============== Properties ===============

   public long getLastEventTime()
   {
      return this.lastEventTime;
   }

   // =============== Methods ===============

   public void addEventListener(Consumer<LinkedHashMap<String, String>> listener)
   {
      this.eventListeners.add(listener);
   }

   public SortedMap<Long, LinkedHashMap<String, String>> pull(long since)
   {
      return this.numEventMap.tailMap(since);
   }

   public SortedMap<Long, LinkedHashMap<String, String>> pull(long since, String... relevantEventTypes)
   {
      return this.pull(since, e -> this.filterRelevantEventTypes(e, Arrays.asList(relevantEventTypes)));
   }

   private Boolean filterRelevantEventTypes(Map.Entry<Long, LinkedHashMap<String, String>> e,
      List<String> relevantEventTypes)
   {
      LinkedHashMap<String, String> map = e.getValue();
      return relevantEventTypes.contains(map.get(EVENT_TYPE));
   }

   public SortedMap<Long, LinkedHashMap<String, String>> pull(long since,
      Function<Map.Entry<Long, LinkedHashMap<String, String>>, Boolean> filterOp)
   {
      SortedMap<Long, LinkedHashMap<String, String>> tailMap = this.numEventMap.tailMap(since);
      TreeMap<Long, LinkedHashMap<String, String>> resultMap = new TreeMap<>();
      for (Map.Entry<Long, LinkedHashMap<String, String>> entry : tailMap.entrySet())
      {
         boolean result = filterOp.apply(entry);

         if (result)
         {
            resultMap.put(entry.getKey(), entry.getValue());
         }
      }

      return resultMap;
   }

   public LinkedHashMap<String, String> getEvent(String eventKey)
   {
      Long aLong = this.keyNumMap.get(eventKey);

      if (aLong == null)
      {
         return null; //======================
      }

      return this.numEventMap.get(aLong);
   }

   public boolean isOverwritten(LinkedHashMap<String, String> map)
   {
      String eventKey = map.get(EVENT_KEY);
      String eventTimeTxt = map.get(EVENT_TIMESTAMP);

      Long storedTime = this.keyNumMap.get(eventKey);

      if (storedTime == null)
      {
         return false;
      }

      String storedTimeTxt = this.dateFormat.format(storedTime);

      return storedTimeTxt.compareTo(eventTimeTxt) >= 0;
   }

   public EventSource setOldEventTimeStamp(String oldTimeStampString)
   {
      if (oldTimeStampString == null)
      {
         return this; //========================
      }

      long oldTimeStamp = 0;
      try
      {
         oldTimeStamp = this.dateFormat.parse(oldTimeStampString).getTime();
      }
      catch (ParseException e)
      {
         e.printStackTrace();
      }

      return this.setOldEventTimeStamp(oldTimeStamp);
   }

   public EventSource setOldEventTimeStamp(long oldEventTimeStamp)
   {
      this.oldEventTimeStamp = oldEventTimeStamp;
      return this;
   }

   public EventSource append(LinkedHashMap<String, String> event)
   {
      String timestampString;

      this.setOldEventTimeStamp(event.get(EVENT_TIMESTAMP));

      if (this.oldEventTimeStamp > this.lastEventTime)
      {
         this.lastEventTime = this.oldEventTimeStamp;
      }
      else
      {
         long currentTime = System.currentTimeMillis();
         if (currentTime > this.lastEventTime)
         {
            this.lastEventTime = currentTime;
         }
         else
         {
            this.lastEventTime++;
         }
      }
      timestampString = this.dateFormat.format(this.lastEventTime);
      this.oldEventTimeStamp = 0;

      event.put(EVENT_TIMESTAMP, timestampString);

      String key = event.get(EVENT_KEY);
      if (key != null)
      {
         Long oldNum = this.keyNumMap.get(key);
         if (oldNum != null)
         {
            this.numEventMap.remove(oldNum);
         }
      }

      this.keyNumMap.put(key, this.lastEventTime);
      this.numEventMap.put(this.lastEventTime, event);

      for (Consumer<LinkedHashMap<String, String>> listener : this.eventListeners)
      {
         listener.accept(event);
      }

      return this;
   }

   public EventSource append(String buf)
   {
      if (buf == null)
      {
         return this; //===========================================
      }

      ArrayList<LinkedHashMap<String, String>> list = this.yamler.decodeList(buf);

      for (LinkedHashMap<String, String> event : list)
      {
         this.append(event);
      }

      return this;
   }

   public String encodeYaml()
   {
      return YamlGenerator.encodeYaml(this.numEventMap);
   }

   // =============== Static Methods ===============

   /**
    * Encodes the events as a list of YAML objects.
    *
    * @param events
    *    the events
    *
    * @return the encoded YAML object list
    *
    * @deprecated since 1.2; use {@link YamlGenerator#encodeYaml(SortedMap)} instead
    */
   @Deprecated
   public static String encodeYaml(SortedMap<Long, ? extends Map<String, String>> events)
   {
      return YamlGenerator.encodeYaml(events);
   }

   /**
    * Encodes the events as a list of YAML objects.
    *
    * @param events
    *    the events
    *
    * @return the encoded YAML object list
    *
    * @deprecated since 1.2; use {@link YamlGenerator#encodeYaml(Iterable)} instead
    */
   @Deprecated
   public static String encodeYaml(List<? extends Map<String, String>> events)
   {
      return YamlGenerator.encodeYaml(events);
   }

   /**
    * Encodes the event as a YAML object.
    *
    * @param event
    *    the event
    *
    * @return the encoded YAML object
    *
    * @deprecated since 1.2; use {@link YamlGenerator#encodeYaml(Map)} instead
    */
   @Deprecated
   public static String encodeYaml(LinkedHashMap<String, String> event)
   {
      return YamlGenerator.encodeYaml(event);
   }
}
