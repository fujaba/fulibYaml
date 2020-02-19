package org.fulib.yaml;

import java.util.Objects;

/**
 * This class provides static utility methods for working with strings in general, as used by various implementations
 * in this library.
 */
public class StrUtil
{
   /**
    * Transforms the first character of the input string to uppercase, leaving the remaining characters as-is.
    *
    * @param string
    *    the input string
    *
    * @return the input string with the first character in uppercase
    */
   public static String cap(String string)
   {
      final StringBuilder builder = new StringBuilder(string);
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

   /**
    * Transforms the first character of the input string to lowercase, leaving the remaining characters as-is.
    *
    * @param string
    *    the input string
    *
    * @return the input string with the first character in lowercase
    */
   public static String downFirstChar(String string)
   {
      final StringBuilder builder = new StringBuilder(string);
      builder.setCharAt(0, Character.toLowerCase(builder.charAt(0)));
      return builder.toString();
   }
}
