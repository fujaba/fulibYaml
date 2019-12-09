package org.fulib.yaml;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.BiPredicate;
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

   private final ArrayList<Consumer<? super Map<String, String>>> eventListeners = new ArrayList<>();

   private final LinkedHashMap<String, Long> keyNumMap = new LinkedHashMap<>();

   private final TreeMap<Long, Map<String, String>> numEventMap = new TreeMap<>();

   private long lastEventTime;

   private long oldEventTimeStamp = 0;

   public DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

   // =============== Properties ===============

   public long getLastEventTime()
   {
      return this.lastEventTime;
   }

   // =============== Methods ===============

   public void addEventListener(Consumer<? super Map<String, String>> listener)
   {
      this.eventListeners.add(listener);
   }

   /**
    * Gets all events after or at the specified timestamp.
    *
    * @param since
    *    the timestamp
    *
    * @return all events after or at the specified timestamp
    *
    * @deprecated since 1.2; use {@link #getEvents(long)} instead
    */
   @Deprecated
   public SortedMap<Long, LinkedHashMap<String, String>> pull(long since)
   {
      return this.pull(since, (Function<Map.Entry<Long, LinkedHashMap<String, String>>, Boolean>) null);
   }

   /**
    * Gets all events after or at the specified timestamp that have any one of the relevant event types.
    *
    * @param since
    *    the timestamp
    * @param relevantEventTypes
    *    the relevant event types
    *
    * @return all events after or at the specified timestamp that have any one of the relevant event types
    *
    * @deprecated since 1.2; use {@link #getEvents(long, String...)} instead
    */
   @Deprecated
   public SortedMap<Long, LinkedHashMap<String, String>> pull(long since, String... relevantEventTypes)
   {
      final Set<String> eventTypes = new HashSet<>(Arrays.asList(relevantEventTypes));
      return this.pull(since, e -> eventTypes.contains(e.getValue().get(EVENT_KEY)));
   }

   /**
    * Gets all events after or at the specified timestamp that fulfill the given predicate.
    *
    * @param since
    *    the timestamp
    * @param filterOp
    *    the predicate on timestamp and event
    *
    * @return all events after or at the specified timestamp that fulfill the given predicate
    *
    * @deprecated since 1.2; use {@link #getEvents(long, BiPredicate)} instead
    */
   @Deprecated
   public SortedMap<Long, LinkedHashMap<String, String>> pull(long since,
      Function<Map.Entry<Long, LinkedHashMap<String, String>>, Boolean> filterOp)
   {
      final SortedMap<Long, Map<String, String>> tailMap = this.numEventMap.tailMap(since);
      final TreeMap<Long, LinkedHashMap<String, String>> resultMap = new TreeMap<>();

      for (Map.Entry<Long, Map<String, String>> entry : tailMap.entrySet())
      {
         final LinkedHashMap<String, String> linkedEvent = makeLinked(entry.getValue());

         if (filterOp == null || filterOp.apply(new AbstractMap.SimpleEntry<>(entry.getKey(), linkedEvent)))
         {
            resultMap.put(entry.getKey(), linkedEvent);
         }
      }

      return resultMap;
   }

   /**
    * Gets all events after or at the specified timestamp.
    *
    * @param since
    *    the timestamp
    *
    * @return all events after or at the specified timestamp
    *
    * @since 1.2
    */
   public SortedMap<Long, Map<String, String>> getEvents(long since)
   {
      return Collections.unmodifiableSortedMap(this.numEventMap.tailMap(since));
   }

   /**
    * Gets all events after or at the specified timestamp that have any one of the relevant event types.
    *
    * @param since
    *    the timestamp
    * @param relevantEventTypes
    *    the relevant event types
    *
    * @return all events after or at the specified timestamp that have any one of the relevant event types
    *
    * @since 1.2
    */
   public SortedMap<Long, Map<String, String>> getEvents(long since, String... relevantEventTypes)
   {
      final Set<String> eventTypes = new HashSet<>(Arrays.asList(relevantEventTypes));
      return this.getEvents(since, (k, v) -> eventTypes.contains(v.get(EVENT_KEY)));
   }

   /**
    * Gets all events after or at the specified timestamp that fulfill the given predicate.
    *
    * @param since
    *    the timestamp
    * @param filterOp
    *    the predicate on timestamp and event
    *
    * @return all events after or at the specified timestamp that fulfill the given predicate
    *
    * @since 1.2
    */
   public SortedMap<Long, Map<String, String>> getEvents(long since,
      BiPredicate<? super Long, ? super Map<String, String>> filterOp)
   {
      final SortedMap<Long, Map<String, String>> events = this.numEventMap.tailMap(since);
      if (filterOp == null)
      {
         return events;
      }

      final SortedMap<Long, Map<String, String>> result = new TreeMap<>();

      for (final Map.Entry<Long, Map<String, String>> entry : events.entrySet())
      {
         final Long key = entry.getKey();
         final Map<String, String> value = entry.getValue();
         if (filterOp.test(key, value))
         {
            result.put(key, value);
         }
      }

      return result;
   }

   /**
    * Gets the newest event with the given key, or null if not found.
    *
    * @param eventKey
    *    the event key
    *
    * @return the newest event with the given key
    *
    * @deprecated since 1.2; use {@link #getNewestEvent(String)} instead
    */
   @Deprecated
   public LinkedHashMap<String, String> getEvent(String eventKey)
   {
      return makeLinked(this.getNewestEvent(eventKey));
   }

   /**
    * Gets the newest event with the given key, or null if not found.
    *
    * @param eventKey
    *    the event key
    *
    * @return the newest event with the given key
    *
    * @since 1.2
    */
   public Map<String, String> getNewestEvent(String eventKey)
   {
      final Long timeStamp = this.keyNumMap.get(eventKey);
      return timeStamp != null ? this.numEventMap.get(timeStamp) : null;
   }

   /**
    * Checks whether the given event was already overwritten, i.e. whether a newer event with the same key exists.
    *
    * @param event
    *    the event
    *
    * @return true if the event was overwritten, false otherwise.
    *
    * @deprecated since 1.2; use {@link #isOverwritten(Map)} instead
    */
   @Deprecated
   public boolean isOverwritten(LinkedHashMap<String, String> event)
   {
      return this.isOverwritten((Map<String, String>) event);
   }

   /**
    * Checks whether the given event was already overwritten, i.e. whether a newer event with the same key exists.
    *
    * @param event
    *    the event
    *
    * @return true if the event was overwritten, false otherwise.
    *
    * @since 1.2
    */
   public boolean isOverwritten(Map<String, String> event)
   {
      String eventKey = event.get(EVENT_KEY);
      String eventTimeTxt = event.get(EVENT_TIMESTAMP);

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

      for (Consumer<? super Map<String, String>> listener : this.eventListeners)
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
      return YamlGenerator.serialize(this.numEventMap.values());
   }

   public void encodeYaml(Writer writer) throws IOException
   {
      YamlGenerator.serialize(this.numEventMap.values(), writer);
   }

   // =============== Static Methods ===============

   @Deprecated // only used by legacy methods
   private static LinkedHashMap<String, String> makeLinked(Map<String, String> event)
   {
      return event instanceof LinkedHashMap ? (LinkedHashMap<String, String>) event : new LinkedHashMap<>(event);
   }

   /**
    * Encodes the events as a list of YAML objects.
    *
    * @param events
    *    the events
    *
    * @return the encoded YAML object list
    *
    * @deprecated since 1.2; use {@link YamlGenerator#serialize(Iterable) YamlGenerator.encodeYaml}{@code (events.values())} instead
    */
   @Deprecated
   public static String encodeYaml(SortedMap<Long, ? extends Map<String, String>> events)
   {
      return YamlGenerator.serialize(events.values());
   }

   /**
    * Encodes the events as a list of YAML objects.
    *
    * @param events
    *    the events
    *
    * @return the encoded YAML object list
    *
    * @deprecated since 1.2; use {@link YamlGenerator#serialize(Iterable)} instead
    */
   @Deprecated
   public static String encodeYaml(List<? extends Map<String, String>> events)
   {
      return YamlGenerator.serialize(events);
   }

   /**
    * Encodes the event as a YAML object.
    *
    * @param event
    *    the event
    *
    * @return the encoded YAML object
    *
    * @deprecated since 1.2; use {@link YamlGenerator#serialize(Map)} instead
    */
   @Deprecated
   public static String encodeYaml(LinkedHashMap<String, String> event)
   {
      return YamlGenerator.serialize(event);
   }
}
