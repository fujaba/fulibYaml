package org.fulib.yaml;

import java.util.Objects;

public class StrUtil
{
   public static String cap(String oldTxt)
   {
      final StringBuilder builder = new StringBuilder(oldTxt);
      builder.setCharAt(0, Character.toUpperCase(builder.charAt(0)));
      return builder.toString();
   }

   /**
    * Checks if the two strings are equal, handling nulls correctly.
    *
    * @param word1
    *    the first string
    * @param word2
    *    the second string
    *
    * @return true if the two strings are equal or both null, false otherwise
    *
    * @deprecated since 1.2; use {@link Objects#equals(Object, Object)} instead
    */
   @Deprecated
   public static boolean stringEquals(String word1, String word2)
   {
      return Objects.equals(word1, word2);
   }

   public static String downFirstChar(String oldTxt)
   {
      final StringBuilder builder = new StringBuilder(oldTxt);
      builder.setCharAt(0, Character.toLowerCase(builder.charAt(0)));
      return builder.toString();
   }
}
