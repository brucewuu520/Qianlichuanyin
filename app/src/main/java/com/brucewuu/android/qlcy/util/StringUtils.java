/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.brucewuu.android.qlcy.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Miscellaneous {@link String} utility methods.
 * <p/>
 * <p/>
 * Mainly for internal use within the framework; consider <a
 * href="http://jakarta.apache.org/commons/lang/">Jakarta's Commons Lang</a> for
 * a more comprehensive suite of String utilities.
 * <p/>
 * <p/>
 * This class delivers some simple functionality that should really be provided
 * by the core Java <code>String</code> and {@link StringBuilder} classes, such
 * as the ability to {@link #} all occurrences of a given substring in a
 * target string. It also provides easy-to-use methods to convert between
 * delimited strings, such as CSV strings, and collections and arrays.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Keith Donald
 * @author Rob Harrop
 * @author Rick Evans
 * @author Arjen Poutsma
 * @since 1.0
 */
public abstract class StringUtils {

    private static final Pattern phone = Pattern.compile("^[0-9]\\d{10}$");
    private static final Pattern nick = Pattern.compile("^[\\u4E00-\\u9FA5A-Za-z0-9_]+$");
    private static final Pattern password = Pattern.compile("^\\w+$");

    /**
     * 判断是不是一个合法的手机号码
     */
    public static boolean isPhone(CharSequence phoneNum) {
        if (isEmpty(phoneNum))
            return false;
        return phone.matcher(phoneNum).matches();
    }

    /**
     * 校验昵称是否合格
     *
     * @param nickName
     * @return
     */
    public static boolean isNick(CharSequence nickName) {
        if (isEmpty(nickName))
            return false;
        return nick.matcher(nickName).matches();
    }

    /**
     * 校验密码是否合格（只能由数字或字母组成）
     */
    public static boolean isPass(CharSequence pass) {
        if (isEmpty(pass))
            return false;
        return password.matcher(pass).matches();
    }

    /**
     * 检验是否符合基本json数据格式
     */
    public static boolean isJson(String str) {
        if (isEmpty(str))
            return false;
        return str.matches("^\\{.*");
    }

    /**
     * 检验是否符合json数组格式
     */
    public static boolean isJsonArray(String str) {
        if (isEmpty(str))
            return false;
        return str.matches("^\\[\\{.*");
    }

    public static boolean isColor(String str) {
        if (isEmpty(str))
            return false;

        return str.length() == 7 && str.startsWith("#");
    }

    public static boolean isUrl(String str) {
        if (isEmpty(str))
            return false;

        if (str.startsWith("http"))
            return true;

        if (str.startsWith("https"))
            return true;

        if (str.startsWith("file:///android_asset"))
            return true;

        return false;
    }

    /**
     * 将String转换成Json 获取天气时用
     */
    public static String toJson(String str) {
        if (isEmpty(str))
            return null;

        return "{\"data\":" + str.substring(str.lastIndexOf("(") + 1, str.lastIndexOf(")")) + "}";
    }

    /**
     * 字符串转整数
     *
     * @param str
     * @param defValue
     * @return
     */
    public static int toInt(String str, int defValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        return defValue;
    }

    /**
     * String转long
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static long toLong(String obj) {
        try {
            return Long.parseLong(obj);
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * String转double
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static double toDouble(String obj) {
        try {
            return Double.parseDouble(obj);
        } catch (Exception e) {
        }
        return 0D;
    }

    /**
     * 字符串转布尔
     *
     * @param b
     * @return 转换异常返回 false
     */
    public static boolean toBool(String b) {
        try {
            return Boolean.parseBoolean(b);
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 判断一个字符串是不是数字
     */
    public static boolean isNumber(CharSequence str) {
        try {
            Integer.parseInt(str.toString());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * byte[]数组转换为16进制的字符串。
     *
     * @param data 要转换的字节数组。
     * @return 转换后的结果。
     */
    public static final String byteArrayToHexString(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte b : data) {
            int v = b & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase(Locale.getDefault());
    }

    /**
     * 16进制表示的字符串转换为字节数组。
     *
     * @param s 16进制表示的字符串
     * @return byte[] 字节数组
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] d = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个进制字节
            d[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
                    .digit(s.charAt(i + 1), 16));
        }
        return d;
    }

    private static boolean isEmpty(CharSequence text) {
        return text == null || text.length() == 0;
    }

    public static boolean isEmpty(Object[] array) {
        return (array == null || array.length == 0);
    }

    /**
     * <p>Checks if a CharSequence is whitespace, empty ("") or null.</p>
     * <p/>
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is null, empty or whitespace
     * @since 3.0 Changed signature from isBlank(String) to isBlank(CharSequence)
     */
    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }
//
//    /**
//     * Check whether the given CharSequence has actual text. More specifically,
//     * returns <code>true</code> if the string not <code>null</code>, its length
//     * is greater than 0, and it contains at least one non-whitespace character.
//     * <p/>
//     * <p/>
//     * <pre>
//     * StringUtils.hasText(null) = false
//     * StringUtils.hasText("") = false
//     * StringUtils.hasText(" ") = false
//     * StringUtils.hasText("12345") = true
//     * StringUtils.hasText(" 12345 ") = true
//     * </pre>
//     *
//     * @param str the CharSequence to check (may be <code>null</code>)
//     * @return <code>true</code> if the CharSequence is not <code>null</code>,
//     * its length is greater than 0, and it does not contain whitespace
//     * only
//     * @see Character#isWhitespace
//     */
//    public static boolean hasText(CharSequence str) {
//        if (!hasLength(str)) {
//            return false;
//        }
//        int strLen = str.length();
//        for (int i = 0; i < strLen; i++) {
//            if (!Character.isWhitespace(str.charAt(i))) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    /**
//     * Check whether the given String has actual text. More specifically,
//     * returns <code>true</code> if the string not <code>null</code>, its length
//     * is greater than 0, and it contains at least one non-whitespace character.
//     *
//     * @param str the String to check (may be <code>null</code>)
//     * @return <code>true</code> if the String is not <code>null</code>, its
//     * length is greater than 0, and it does not contain whitespace only
//     * @see #hasText(CharSequence)
//     */
//    public static boolean hasText(String str) {
//        return hasText((CharSequence) str);
//    }
//
//    /**
//     * Check whether the given CharSequence contains any whitespace characters.
//     *
//     * @param str the CharSequence to check (may be <code>null</code>)
//     * @return <code>true</code> if the CharSequence is not empty and contains
//     * at least 1 whitespace character
//     * @see Character#isWhitespace
//     */
//    public static boolean containsWhitespace(CharSequence str) {
//        if (!hasLength(str)) {
//            return false;
//        }
//        int strLen = str.length();
//        for (int i = 0; i < strLen; i++) {
//            if (Character.isWhitespace(str.charAt(i))) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    /**
//     * Check whether the given String contains any whitespace characters.
//     *
//     * @param str the String to check (may be <code>null</code>)
//     * @return <code>true</code> if the String is not empty and contains at
//     * least 1 whitespace character
//     * @see #containsWhitespace(CharSequence)
//     */
//    public static boolean containsWhitespace(String str) {
//        return containsWhitespace((CharSequence) str);
//    }
//
//    // Delete
//    //-----------------------------------------------------------------------
//
//    /**
//     * <p>Deletes all whitespaces from a String as defined by
//     * {@link Character#isWhitespace(char)}.</p>
//     * <p/>
//     * <pre>
//     * StringUtils.deleteWhitespace(null)         = null
//     * StringUtils.deleteWhitespace("")           = ""
//     * StringUtils.deleteWhitespace("abc")        = "abc"
//     * StringUtils.deleteWhitespace("   ab  c  ") = "abc"
//     * </pre>
//     *
//     * @param str the String to delete whitespace from, may be null
//     * @return the String without whitespaces, {@code null} if null String input
//     */
//    public static String deleteWhitespace(final String str) {
//        if (isEmpty(str)) {
//            return str;
//        }
//        final int sz = str.length();
//        final char[] chs = new char[sz];
//        int count = 0;
//        for (int i = 0; i < sz; i++) {
//            if (!Character.isWhitespace(str.charAt(i))) {
//                chs[count++] = str.charAt(i);
//            }
//        }
//        if (count == sz) {
//            return str;
//        }
//        return new String(chs, 0, count);
//    }
//
//
//    /**
//     * Trim leading and trailing whitespace from the given String.
//     *
//     * @param str the String to check
//     * @return the trimmed String
//     * @see Character#isWhitespace
//     */
//    public static String trimWhitespace(String str) {
//        if (!hasLength(str)) {
//            return str;
//        }
//        StringBuilder sb = new StringBuilder(str);
//        while (sb.length() > 0 && Character.isWhitespace(sb.charAt(0))) {
//            sb.deleteCharAt(0);
//        }
//        while (sb.length() > 0
//                && Character.isWhitespace(sb.charAt(sb.length() - 1))) {
//            sb.deleteCharAt(sb.length() - 1);
//        }
//        return sb.toString();
//    }
//
//    public static String trim(String text) {
//        return text == null ? null : text.trim();
//    }
//
//    /**
//     * Trim <i>all</i> whitespace from the given String: leading, trailing, and
//     * inbetween characters.
//     *
//     * @param str the String to check
//     * @return the trimmed String
//     * @see Character#isWhitespace
//     */
//    public static String trimAllWhitespace(String str) {
//        if (!hasLength(str)) {
//            return str;
//        }
//        StringBuilder sb = new StringBuilder(str);
//        int index = 0;
//        while (sb.length() > index) {
//            if (Character.isWhitespace(sb.charAt(index))) {
//                sb.deleteCharAt(index);
//            } else {
//                index++;
//            }
//        }
//        return sb.toString();
//    }
//
//    /**
//     * 此方法会去除字符串首尾的空白字符，然后把中间的多个连续的换行符替换为一个换行符号
//     *
//     * @param text
//     * @return
//     */
//    public static String reduceLineBreaks(String text) {
//        if (isEmpty(text)) {
//            return text;
//        }
//        String textTrimed = trimWhitespace(text);
//        return textTrimed.replaceAll("(\\r\\n|\\r|\\n)+", "\n");
//    }
//
//    public static String replaceLineBreaks(String text) {
//        if (isEmpty(text)) {
//            return text;
//        }
//        return text.replaceAll("\\r\\n", "  ").replaceAll("\\n", " ");
//    }
//
//    /**
//     * Trim leading whitespace from the given String.
//     *
//     * @param str the String to check
//     * @return the trimmed String
//     * @see Character#isWhitespace
//     */
//    public static String trimLeadingWhitespace(String str) {
//        if (!hasLength(str)) {
//            return str;
//        }
//        StringBuilder sb = new StringBuilder(str);
//        while (sb.length() > 0 && Character.isWhitespace(sb.charAt(0))) {
//            sb.deleteCharAt(0);
//        }
//        return sb.toString();
//    }
//
//    /**
//     * Trim trailing whitespace from the given String.
//     *
//     * @param str the String to check
//     * @return the trimmed String
//     * @see Character#isWhitespace
//     */
//    public static String trimTrailingWhitespace(String str) {
//        if (!hasLength(str)) {
//            return str;
//        }
//        StringBuilder sb = new StringBuilder(str);
//        while (sb.length() > 0
//                && Character.isWhitespace(sb.charAt(sb.length() - 1))) {
//            sb.deleteCharAt(sb.length() - 1);
//        }
//        return sb.toString();
//    }
//
//    /**
//     * Trim all occurences of the supplied leading character from the given
//     * String.
//     *
//     * @param str              the String to check
//     * @param leadingCharacter the leading character to be trimmed
//     * @return the trimmed String
//     */
//    public static String trimLeadingCharacter(String str, char leadingCharacter) {
//        if (!hasLength(str)) {
//            return str;
//        }
//        StringBuilder sb = new StringBuilder(str);
//        while (sb.length() > 0 && sb.charAt(0) == leadingCharacter) {
//            sb.deleteCharAt(0);
//        }
//        return sb.toString();
//    }
//
//    /**
//     * Trim all occurences of the supplied trailing character from the given
//     * String.
//     *
//     * @param str               the String to check
//     * @param trailingCharacter the trailing character to be trimmed
//     * @return the trimmed String
//     */
//    public static String trimTrailingCharacter(String str,
//                                               char trailingCharacter) {
//        if (!hasLength(str)) {
//            return str;
//        }
//        StringBuilder sb = new StringBuilder(str);
//        while (sb.length() > 0
//                && sb.charAt(sb.length() - 1) == trailingCharacter) {
//            sb.deleteCharAt(sb.length() - 1);
//        }
//        return sb.toString();
//    }
//
//    /**
//     * Test if the given String starts with the specified prefix, ignoring
//     * upper/lower case.
//     *
//     * @param str    the String to check
//     * @param prefix the prefix to look for
//     * @see String#startsWith
//     */
//    public static boolean startsWithIgnoreCase(String str, String prefix) {
//        if (str == null || prefix == null) {
//            return false;
//        }
//        if (str.startsWith(prefix)) {
//            return true;
//        }
//        if (str.length() < prefix.length()) {
//            return false;
//        }
//        String lcStr = str.substring(0, prefix.length()).toLowerCase();
//        String lcPrefix = prefix.toLowerCase();
//        return lcStr.equals(lcPrefix);
//    }
//
//    /**
//     * Test if the given String ends with the specified suffix, ignoring
//     * upper/lower case.
//     *
//     * @param str    the String to check
//     * @param suffix the suffix to look for
//     * @see String#endsWith
//     */
//    public static boolean endsWithIgnoreCase(String str, String suffix) {
//        if (str == null || suffix == null) {
//            return false;
//        }
//        if (str.endsWith(suffix)) {
//            return true;
//        }
//        if (str.length() < suffix.length()) {
//            return false;
//        }
//
//        String lcStr = str.substring(str.length() - suffix.length())
//                .toLowerCase();
//        String lcSuffix = suffix.toLowerCase();
//        return lcStr.equals(lcSuffix);
//    }
//
//    /**
//     * Test whether the given string matches the given substring at the given
//     * index.
//     *
//     * @param str       the original string (or StringBuilder)
//     * @param index     the index in the original string to start matching against
//     * @param substring the substring to match at the given index
//     */
//    public static boolean substringMatch(CharSequence str, int index,
//                                         CharSequence substring) {
//        for (int j = 0; j < substring.length(); j++) {
//            int i = index + j;
//            if (i >= str.length() || str.charAt(i) != substring.charAt(j)) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    /**
//     * Count the occurrences of the substring in string s.
//     *
//     * @param str string to search in. Return 0 if this is null.
//     * @param sub string to search for. Return 0 if this is null.
//     */
//    public static int countOccurrencesOf(String str, String sub) {
//        if (str == null || sub == null || str.length() == 0
//                || sub.length() == 0) {
//            return 0;
//        }
//        int count = 0;
//        int pos = 0;
//        int idx;
//        while ((idx = str.indexOf(sub, pos)) != -1) {
//            ++count;
//            pos = idx + sub.length();
//        }
//        return count;
//    }
//
//    /**
//     * Replace all occurences of a substring within a string with another
//     * string.
//     *
//     * @param inString   String to examine
//     * @param oldPattern String to replace
//     * @param newPattern String to insert
//     * @return a String with the replacements
//     */
//    public static String replace(String inString, String oldPattern,
//                                 String newPattern) {
//        if (!hasLength(inString) || !hasLength(oldPattern)
//                || newPattern == null) {
//            return inString;
//        }
//        StringBuilder sb = new StringBuilder();
//        int pos = 0; // our position in the old string
//        int index = inString.indexOf(oldPattern);
//        // the index of an occurrence we've found, or -1
//        int patLen = oldPattern.length();
//        while (index >= 0) {
//            sb.append(inString.substring(pos, index));
//            sb.append(newPattern);
//            pos = index + patLen;
//            index = inString.indexOf(oldPattern, pos);
//        }
//        sb.append(inString.substring(pos));
//        // remember to append any characters to the right of a match
//        return sb.toString();
//    }
//
//    /**
//     * Delete all occurrences of the given substring.
//     *
//     * @param inString the original String
//     * @param pattern  the pattern to delete all occurrences of
//     * @return the resulting String
//     */
//    public static String delete(String inString, String pattern) {
//        return replace(inString, pattern, "");
//    }
//
//    /**
//     * Delete any character in a given String.
//     *
//     * @param inString      the original String
//     * @param charsToDelete a set of characters to delete. E.g. "az\n" will delete 'a's,
//     *                      'z's and new lines.
//     * @return the resulting String
//     */
//    public static String deleteAny(String inString, String charsToDelete) {
//        if (!hasLength(inString) || !hasLength(charsToDelete)) {
//            return inString;
//        }
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < inString.length(); i++) {
//            char c = inString.charAt(i);
//            if (charsToDelete.indexOf(c) == -1) {
//                sb.append(c);
//            }
//        }
//        return sb.toString();
//    }
//
//    // ---------------------------------------------------------------------
//    // Convenience methods for working with formatted Strings
//    // ---------------------------------------------------------------------
//
//    /**
//     * Quote the given String with single quotes.
//     *
//     * @param str the input String (e.g. "myString")
//     * @return the quoted String (e.g. "'myString'"), or
//     * <code>null<code> if the input was <code>null</code>
//     */
//    public static String quote(String str) {
//        return (str != null ? "'" + str + "'" : null);
//    }
//
//    /**
//     * Turn the given Object into a String with single quotes if it is a String;
//     * keeping the Object as-is else.
//     *
//     * @param obj the input Object (e.g. "myString")
//     * @return the quoted String (e.g. "'myString'"), or the input object as-is
//     * if not a String
//     */
//    public static Object quoteIfString(Object obj) {
//        return (obj instanceof String ? quote((String) obj) : obj);
//    }
//
//    /**
//     * Unqualify a string qualified by a '.' dot character. For example,
//     * "this.name.is.qualified", returns "qualified".
//     *
//     * @param qualifiedName the qualified name
//     */
//    public static String unqualify(String qualifiedName) {
//        return unqualify(qualifiedName, '.');
//    }
//
//    /**
//     * Unqualify a string qualified by a separator character. For example,
//     * "this:name:is:qualified" returns "qualified" if using a ':' separator.
//     *
//     * @param qualifiedName the qualified name
//     * @param separator     the separator
//     */
//    public static String unqualify(String qualifiedName, char separator) {
//        return qualifiedName
//                .substring(qualifiedName.lastIndexOf(separator) + 1);
//    }
//
//    /**
//     * Capitalize a <code>String</code>, changing the first letter to upper case
//     * as per {@link Character#toUpperCase(char)}. No other letters are changed.
//     *
//     * @param str the String to capitalize, may be <code>null</code>
//     * @return the capitalized String, <code>null</code> if null
//     */
//    public static String capitalize(String str) {
//        return changeFirstCharacterCase(str, true);
//    }
//
//    /**
//     * Uncapitalize a <code>String</code>, changing the first letter to lower
//     * case as per {@link Character#toLowerCase(char)}. No other letters are
//     * changed.
//     *
//     * @param str the String to uncapitalize, may be <code>null</code>
//     * @return the uncapitalized String, <code>null</code> if null
//     */
//    public static String uncapitalize(String str) {
//        return changeFirstCharacterCase(str, false);
//    }
//
//    private static String changeFirstCharacterCase(String str,
//                                                   boolean capitalize) {
//        if (str == null || str.length() == 0) {
//            return str;
//        }
//        StringBuilder sb = new StringBuilder(str.length());
//        if (capitalize) {
//            sb.append(Character.toUpperCase(str.charAt(0)));
//        } else {
//            sb.append(Character.toLowerCase(str.charAt(0)));
//        }
//        sb.append(str.substring(1));
//        return sb.toString();
//    }
//
//    /**
//     * Parse the given <code>localeString</code> value into a {@link java.util.Locale}.
//     * <p/>
//     * This is the inverse operation of {@link java.util.Locale#toString Locale's
//     * readString}.
//     *
//     * @param localeString the locale string, following <code>Locale's</code>
//     *                     <code>readString()</code> format ("en", "en_UK", etc); also
//     *                     accepts spaces as separators, as an alternative to underscores
//     * @return a corresponding <code>Locale</code> instance
//     */
//    public static Locale parseLocaleString(String localeString) {
//        String[] parts = tokenizeToStringArray(localeString, "_ ", false, false);
//        String language = (parts.length > 0 ? parts[0] : "");
//        String country = (parts.length > 1 ? parts[1] : "");
//        validateLocalePart(language);
//        validateLocalePart(country);
//        String variant = "";
//        if (parts.length >= 2) {
//            // There is definitely a variant, and it is everything after the
//            // country
//            // code sans the separator between the country code and the variant.
//            int endIndexOfCountryCode = localeString.indexOf(country)
//                    + country.length();
//            // Strip off any leading '_' and whitespace, what's left is the
//            // variant.
//            variant = trimLeadingWhitespace(localeString
//                    .substring(endIndexOfCountryCode));
//            if (variant.startsWith("_")) {
//                variant = trimLeadingCharacter(variant, '_');
//            }
//        }
//        return (language.length() > 0 ? new Locale(language, country, variant)
//                : null);
//    }
//
//    private static void validateLocalePart(String localePart) {
//        for (int i = 0; i < localePart.length(); i++) {
//            char ch = localePart.charAt(i);
//            if (ch != '_' && ch != ' ' && !Character.isLetterOrDigit(ch)) {
//                throw new IllegalArgumentException("Locale part \""
//                        + localePart + "\" contains invalid characters");
//            }
//        }
//    }
//
//    /**
//     * Determine the RFC 3066 compliant language tag, as used for the HTTP
//     * "Accept-Language" header.
//     *
//     * @param locale the Locale to transform to a language tag
//     * @return the RFC 3066 compliant language tag as String
//     */
//    public static String toLanguageTag(Locale locale) {
//        return locale.getLanguage()
//                + (hasText(locale.getCountry()) ? "-" + locale.getCountry()
//                : "");
//    }
//
//    // ---------------------------------------------------------------------
//    // Convenience methods for working with String arrays
//    // ---------------------------------------------------------------------
//
//    /**
//     * Append the given String to the given String array, returning a new array
//     * consisting of the input array contents plus the given String.
//     *
//     * @param array the array to append to (can be <code>null</code>)
//     * @param str   the String to append
//     * @return the new array (never <code>null</code>)
//     */
//    public static String[] addStringToArray(String[] array, String str) {
//        if (isEmpty(array)) {
//            return new String[]{str};
//        }
//        String[] newArr = new String[array.length + 1];
//        System.arraycopy(array, 0, newArr, 0, array.length);
//        newArr[array.length] = str;
//        return newArr;
//    }
//
//    /**
//     * Concatenate the given String arrays into one, with overlapping array
//     * elements included twice.
//     * <p/>
//     * The order of elements in the original arrays is preserved.
//     *
//     * @param array1 the first array (can be <code>null</code>)
//     * @param array2 the second array (can be <code>null</code>)
//     * @return the new array (<code>null</code> if both given arrays were
//     * <code>null</code>)
//     */
//    public static String[] concatenateStringArrays(String[] array1,
//                                                   String[] array2) {
//        if (isEmpty(array1)) {
//            return array2;
//        }
//        if (isEmpty(array2)) {
//            return array1;
//        }
//        String[] newArr = new String[array1.length + array2.length];
//        System.arraycopy(array1, 0, newArr, 0, array1.length);
//        System.arraycopy(array2, 0, newArr, array1.length, array2.length);
//        return newArr;
//    }
//
//    /**
//     * Merge the given String arrays into one, with overlapping array elements
//     * only included once.
//     * <p/>
//     * The order of elements in the original arrays is preserved (with the
//     * exception of overlapping elements, which are only included on their first
//     * occurrence).
//     *
//     * @param array1 the first array (can be <code>null</code>)
//     * @param array2 the second array (can be <code>null</code>)
//     * @return the new array (<code>null</code> if both given arrays were
//     * <code>null</code>)
//     */
//    public static String[] mergeStringArrays(String[] array1, String[] array2) {
//        if (isEmpty(array1)) {
//            return array2;
//        }
//        if (isEmpty(array2)) {
//            return array1;
//        }
//        List<String> result = new ArrayList<String>();
//        result.addAll(Arrays.asList(array1));
//        for (String str : array2) {
//            if (!result.contains(str)) {
//                result.add(str);
//            }
//        }
//        return toStringArray(result);
//    }
//
//    /**
//     * Turn given source String array into sorted array.
//     *
//     * @param array the source array
//     * @return the sorted array (never <code>null</code>)
//     */
//    public static String[] sortStringArray(String[] array) {
//        if (isEmpty(array)) {
//            return new String[0];
//        }
//        Arrays.sort(array);
//        return array;
//    }
//
//    /**
//     * Copy the given Collection into a String array. The Collection must
//     * contain String elements only.
//     *
//     * @param collection the Collection to copy
//     * @return the String array (<code>null</code> if the passed-in Collection
//     * was <code>null</code>)
//     */
//    public static String[] toStringArray(Collection<String> collection) {
//        if (collection == null) {
//            return null;
//        }
//        return collection.toArray(new String[collection.size()]);
//    }
//
//    /**
//     * Copy the given Enumeration into a String array. The Enumeration must
//     * contain String elements only.
//     *
//     * @param enumeration the Enumeration to copy
//     * @return the String array (<code>null</code> if the passed-in Enumeration
//     * was <code>null</code>)
//     */
//    public static String[] toStringArray(Enumeration<String> enumeration) {
//        if (enumeration == null) {
//            return null;
//        }
//        List<String> list = Collections.list(enumeration);
//        return list.toArray(new String[list.size()]);
//    }
//
//    /**
//     * Trim the elements of the given String array, calling
//     * <code>String.trim()</code> on each of them.
//     *
//     * @param array the original String array
//     * @return the resulting array (of the same size) with trimmed elements
//     */
//    public static String[] trimArrayElements(String[] array) {
//        if (isEmpty(array)) {
//            return new String[0];
//        }
//        String[] result = new String[array.length];
//        for (int i = 0; i < array.length; i++) {
//            String element = array[i];
//            result[i] = (element != null ? element.trim() : null);
//        }
//        return result;
//    }
//
//    /**
//     * Remove duplicate Strings from the given array. Also sorts the array, as
//     * it uses a TreeSet.
//     *
//     * @param array the String array
//     * @return an array without duplicates, in natural sort order
//     */
//    public static String[] removeDuplicateStrings(String[] array) {
//        if (isEmpty(array)) {
//            return array;
//        }
//        Set<String> set = new TreeSet<String>();
//        Collections.addAll(set, array);
//        return toStringArray(set);
//    }
//
//    /**
//     * Split a String at the first occurrence of the delimiter. Does not include
//     * the delimiter in the result.
//     *
//     * @param toSplit   the string to split
//     * @param delimiter to split the string up with
//     * @return a two element array with index 0 being before the delimiter, and
//     * index 1 being after the delimiter (neither element includes the
//     * delimiter); or <code>null</code> if the delimiter wasn't found in
//     * the given input String
//     */
//    public static String[] split(String toSplit, String delimiter) {
//        if (!hasLength(toSplit) || !hasLength(delimiter)) {
//            return null;
//        }
//        int offset = toSplit.indexOf(delimiter);
//        if (offset < 0) {
//            return null;
//        }
//        String beforeDelimiter = toSplit.substring(0, offset);
//        String afterDelimiter = toSplit.substring(offset + delimiter.length());
//        return new String[]{beforeDelimiter, afterDelimiter};
//    }
//
//    /**
//     * Take an array Strings and split each element based on the given
//     * delimiter. A <code>Properties</code> instance is then generated, with the
//     * left of the delimiter providing the key, and the right of the delimiter
//     * providing the value.
//     * <p/>
//     * Will trim both the key and value before adding them to the
//     * <code>Properties</code> instance.
//     *
//     * @param array     the array to process
//     * @param delimiter to split each element using (typically the equals symbol)
//     * @return a <code>Properties</code> instance representing the array
//     * contents, or <code>null</code> if the array to process was null
//     * or empty
//     */
//    public static Properties splitArrayElementsIntoProperties(String[] array,
//                                                              String delimiter) {
//        return splitArrayElementsIntoProperties(array, delimiter, null);
//    }
//
//    /**
//     * Take an array Strings and split each element based on the given
//     * delimiter. A <code>Properties</code> instance is then generated, with the
//     * left of the delimiter providing the key, and the right of the delimiter
//     * providing the value.
//     * <p/>
//     * Will trim both the key and value before adding them to the
//     * <code>Properties</code> instance.
//     *
//     * @param array         the array to process
//     * @param delimiter     to split each element using (typically the equals symbol)
//     * @param charsToDelete one or more characters to remove from each element prior to
//     *                      attempting the split operation (typically the quotation mark
//     *                      symbol), or <code>null</code> if no removal should occur
//     * @return a <code>Properties</code> instance representing the array
//     * contents, or <code>null</code> if the array to process was
//     * <code>null</code> or empty
//     */
//    public static Properties splitArrayElementsIntoProperties(String[] array,
//                                                              String delimiter, String charsToDelete) {
//
//        if (isEmpty(array)) {
//            return null;
//        }
//        Properties result = new Properties();
//        for (String element : array) {
//            if (charsToDelete != null) {
//                element = deleteAny(element, charsToDelete);
//            }
//            String[] splittedElement = split(element, delimiter);
//            if (splittedElement == null) {
//                continue;
//            }
//            result.setProperty(splittedElement[0].trim(),
//                    splittedElement[1].trim());
//        }
//        return result;
//    }
//
//    /**
//     * Tokenize the given String into a String array via a StringTokenizer.
//     * Trims tokens and omits empty tokens.
//     * <p/>
//     * The given delimiters string is supposed to consist of any number of
//     * delimiter characters. Each of those characters can be used to separate
//     * tokens. A delimiter is always a single character; for multi-character
//     * delimiters, consider using <code>delimitedListToStringArray</code>
//     *
//     * @param str        the String to tokenize
//     * @param delimiters the delimiter characters, assembled as String (each of those
//     *                   characters is individually considered as delimiter).
//     * @return an array of the tokens
//     * @see java.util.StringTokenizer
//     * @see String#trim()
//     * @see #delimitedListToStringArray
//     */
//    public static String[] tokenizeToStringArray(String str, String delimiters) {
//        return tokenizeToStringArray(str, delimiters, true, true);
//    }
//
//    /**
//     * Tokenize the given String into a String array via a StringTokenizer.
//     * <p/>
//     * The given delimiters string is supposed to consist of any number of
//     * delimiter characters. Each of those characters can be used to separate
//     * tokens. A delimiter is always a single character; for multi-character
//     * delimiters, consider using <code>delimitedListToStringArray</code>
//     *
//     * @param str               the String to tokenize
//     * @param delimiters        the delimiter characters, assembled as String (each of those
//     *                          characters is individually considered as delimiter)
//     * @param trimTokens        trim the tokens via String's <code>trim</code>
//     * @param ignoreEmptyTokens omit empty tokens from the result array (only applies to
//     *                          tokens that are empty after trimming; StringTokenizer will not
//     *                          consider subsequent delimiters as token in the first place).
//     * @return an array of the tokens (<code>null</code> if the input String was
//     * <code>null</code>)
//     * @see java.util.StringTokenizer
//     * @see String#trim()
//     * @see #delimitedListToStringArray
//     */
//    public static String[] tokenizeToStringArray(String str, String delimiters,
//                                                 boolean trimTokens, boolean ignoreEmptyTokens) {
//
//        if (str == null) {
//            return null;
//        }
//        StringTokenizer st = new StringTokenizer(str, delimiters);
//        List<String> tokens = new ArrayList<String>();
//        while (st.hasMoreTokens()) {
//            String token = st.nextToken();
//            if (trimTokens) {
//                token = token.trim();
//            }
//            if (!ignoreEmptyTokens || token.length() > 0) {
//                tokens.add(token);
//            }
//        }
//        return toStringArray(tokens);
//    }
//
//    /**
//     * Take a String which is a delimited list and convert it to a String array.
//     * <p/>
//     * A single delimiter can consists of more than one character: It will still
//     * be considered as single delimiter string, rather than as bunch of
//     * potential delimiter characters - in contrast to
//     * <code>tokenizeToStringArray</code>.
//     *
//     * @param str       the input String
//     * @param delimiter the delimiter between elements (this is a single delimiter,
//     *                  rather than a bunch individual delimiter characters)
//     * @return an array of the tokens in the list
//     * @see #tokenizeToStringArray
//     */
//    public static String[] delimitedListToStringArray(String str,
//                                                      String delimiter) {
//        return delimitedListToStringArray(str, delimiter, null);
//    }
//
//    /**
//     * Take a String which is a delimited list and convert it to a String array.
//     * <p/>
//     * A single delimiter can consists of more than one character: It will still
//     * be considered as single delimiter string, rather than as bunch of
//     * potential delimiter characters - in contrast to
//     * <code>tokenizeToStringArray</code>.
//     *
//     * @param str           the input String
//     * @param delimiter     the delimiter between elements (this is a single delimiter,
//     *                      rather than a bunch individual delimiter characters)
//     * @param charsToDelete a set of characters to delete. Useful for deleting unwanted
//     *                      line breaks: e.g. "\r\n\f" will delete all new lines and line
//     *                      feeds in a String.
//     * @return an array of the tokens in the list
//     * @see #tokenizeToStringArray
//     */
//    public static String[] delimitedListToStringArray(String str,
//                                                      String delimiter, String charsToDelete) {
//        if (str == null) {
//            return new String[0];
//        }
//        if (delimiter == null) {
//            return new String[]{str};
//        }
//        List<String> result = new ArrayList<String>();
//        if ("".equals(delimiter)) {
//            for (int i = 0; i < str.length(); i++) {
//                result.add(deleteAny(str.substring(i, i + 1), charsToDelete));
//            }
//        } else {
//            int pos = 0;
//            int delPos;
//            while ((delPos = str.indexOf(delimiter, pos)) != -1) {
//                result.add(deleteAny(str.substring(pos, delPos), charsToDelete));
//                pos = delPos + delimiter.length();
//            }
//            if (str.length() > 0 && pos <= str.length()) {
//                // Add rest of String, but not in case of empty input.
//                result.add(deleteAny(str.substring(pos), charsToDelete));
//            }
//        }
//        return toStringArray(result);
//    }
//
//    /**
//     * Convert a CSV list into an array of Strings.
//     *
//     * @param str the input String
//     * @return an array of Strings, or the empty array in case of empty input
//     */
//    public static String[] commaDelimitedListToStringArray(String str) {
//        return delimitedListToStringArray(str, ",");
//    }
//
//    /**
//     * Convenience method to convert a CSV string list to a set. Note that this
//     * will suppress duplicates.
//     *
//     * @param str the input String
//     * @return a Set of String entries in the list
//     */
//    public static Set<String> commaDelimitedListToSet(String str) {
//        Set<String> set = new TreeSet<String>();
//        String[] tokens = commaDelimitedListToStringArray(str);
//        Collections.addAll(set, tokens);
//        return set;
//    }
//
//    /**
//     * Convenience method to return a Collection as a delimited (e.g. CSV)
//     * String. E.g. useful for <code>readString()</code> implementations.
//     *
//     * @param coll   the Collection to display
//     * @param delim  the delimiter to use (probably a ",")
//     * @param prefix the String to start each element with
//     * @param suffix the String to end each element with
//     * @return the delimited String
//     */
//    public static String toString(Collection<?> coll,
//                                  String delim, String prefix, String suffix) {
//        if (coll == null || coll.isEmpty()) {
//            return "";
//        }
//        StringBuilder sb = new StringBuilder();
//        Iterator<?> it = coll.iterator();
//        while (it.hasNext()) {
//            sb.append(prefix).append(it.next()).append(suffix);
//            if (it.hasNext()) {
//                sb.append(delim);
//            }
//        }
//        return sb.toString();
//    }
//
//    public static <K, V> String toString(Map<K, V> map,
//                                         String delim, String prefix, String suffix) {
//        if (map == null || map.isEmpty()) {
//            return "";
//        }
//        Set<Map.Entry<K, V>> items = map.entrySet();
//        StringBuilder sb = new StringBuilder();
//        Iterator<Map.Entry<K, V>> it = items.iterator();
//        while (it.hasNext()) {
//            Map.Entry<K, V> entry = it.next();
//            sb.append(prefix).append(entry.getKey()).append("=").append(entry.getValue()).append(suffix);
//            if (it.hasNext()) {
//                sb.append(delim);
//            }
//        }
//        return sb.toString();
//    }
//
//    /**
//     * Convenience method to return a Collection as a delimited (e.g. CSV)
//     * String. E.g. useful for <code>readString()</code> implementations.
//     *
//     * @param coll  the Collection to display
//     * @param delim the delimiter to use (probably a ",")
//     * @return the delimited String
//     */
//    public static String toString(Collection<?> coll,
//                                  String delim) {
//        return toString(coll, delim, "", "");
//    }
//
//    public static <K, V> String toString(Map<K, V> map,
//                                         String delim) {
//        return toString(map, delim, "", "");
//    }
//
//    /**
//     * Convenience method to return a Collection as a CSV String. E.g. useful
//     * for <code>readString()</code> implementations.
//     *
//     * @param coll the Collection to display
//     * @return the delimited String
//     */
//    public static String toString(Collection<?> coll) {
//        return toString(coll, ",");
//    }
//
//    public static <K, V> String toString(Map<K, V> map) {
//        return toString(map, ",");
//    }
//
//    /**
//     * Convenience method to return a String array as a delimited (e.g. CSV)
//     * String. E.g. useful for <code>readString()</code> implementations.
//     *
//     * @param arr   the array to display
//     * @param delim the delimiter to use (probably a ",")
//     * @return the delimited String
//     */
//    public static String toString(Object[] arr, String delim) {
//        if (isEmpty(arr)) {
//            return "";
//        }
//        if (arr.length == 1) {
//            return String.valueOf(arr[0]);
//        }
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < arr.length; i++) {
//            if (i > 0) {
//                sb.append(delim);
//            }
//            sb.append(arr[i]);
//        }
//        return sb.toString();
//    }
//
//    /**
//     * Convenience method to return a String array as a CSV String. E.g. useful
//     * for <code>readString()</code> implementations.
//     *
//     * @param arr the array to display
//     * @return the delimited String
//     */
//    public static String toString(Object[] arr) {
//        return toString(arr, ",");
//    }
//
//    /**
//     * 比较两个字符串是否相等，任何一个为null则返回false
//     *
//     * @param text1
//     * @param text2
//     * @return
//     */
//    public static boolean nullSafeEquals(String text1, String text2) {
//        if (text1 == null || text2 == null) {
//            return false;
//        } else {
//            return text1.equals(text2);
//        }
//    }
//
//    public static String toLowerCase(String text) {
//        return text == null ? null : text.toLowerCase(Locale.US);
//    }
//
//    public static boolean nullSafeEqualsIgnoreCase(String text1, String text2) {
//        if (text1 == null) {
//            return text2 == null;
//        } else {
//            return text1.equalsIgnoreCase(text2);
//        }
//    }
//
//    public static String safeSubString(String text, int end) {
//        return safeSubString(text, 0, end);
//    }
//
//    // 截取子串函数（不抛出异常）
//    public static String safeSubString(String text, int start, int end) {
//        if (text == null || text.length() == 0) {
//            return text;
//        }
//        if (start < 0 || end < 0 || end <= start) {
////            throw new IllegalArgumentException(String.format("illegal argument: start: %1$s, end: %2$s", start, end));
//            return text;
//        }
//        int len = end - start;
//        if (text.length() <= len) {
//            return text;
//        }
//        return text.substring(start, end);
//    }
//
//    public static String getHumanReadableByteCount(long bytes) {
//        int unit = 1024;
//        if (bytes < unit) return bytes + " B";
//        int exp = (int) (Math.log(bytes) / Math.log(unit));
//        String pre = "KMGTPE".charAt(exp - 1) + "";
//        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
//    }
//
//    public static boolean isEmpty(Object[] array) {
//        return (array == null || array.length == 0);
//    }
//
//    /**
//     * Converts any string into a string that is safe to use as a file name.
//     * The result will only include ascii characters and numbers, and the "-","_", and "." characters.
//     */
//    public static String toSafeFileName(String name) {
//        int size = name.length();
//        StringBuilder builder = new StringBuilder(size * 2);
//        for (int i = 0; i < size; i++) {
//            char c = name.charAt(i);
//            boolean valid = c >= 'a' && c <= 'z';
//            valid = valid || (c >= 'A' && c <= 'Z');
//            valid = valid || (c >= '0' && c <= '9');
//            valid = valid || (c == '_') || (c == '-') || (c == '.');
//
//            if (valid) {
//                builder.append(c);
//            } else {
//                // Encode the character using hex notation
//                builder.append('x');
//                builder.append(Integer.toHexString(i));
//            }
//        }
//        return builder.toString();
//    }

    // Overlay
    //-----------------------------------------------------------------------

    /**
     * <p>Overlays part of a String with another String.</p>
     * <p/>
     * <p>A {@code null} string input returns {@code null}.
     * A negative index is treated as zero.
     * An index greater than the string length is treated as the string length.
     * The start index is always the smaller of the two indices.</p>
     * <p/>
     * <pre>
     * StringUtils.overlay(null, *, *, *)            = null
     * StringUtils.overlay("", "abc", 0, 0)          = "abc"
     * StringUtils.overlay("abcdef", null, 2, 4)     = "abef"
     * StringUtils.overlay("abcdef", "", 2, 4)       = "abef"
     * StringUtils.overlay("abcdef", "", 4, 2)       = "abef"
     * StringUtils.overlay("abcdef", "zzzz", 2, 4)   = "abzzzzef"
     * StringUtils.overlay("abcdef", "zzzz", 4, 2)   = "abzzzzef"
     * StringUtils.overlay("abcdef", "zzzz", -1, 4)  = "zzzzef"
     * StringUtils.overlay("abcdef", "zzzz", 2, 8)   = "abzzzz"
     * StringUtils.overlay("abcdef", "zzzz", -2, -3) = "zzzzabcdef"
     * StringUtils.overlay("abcdef", "zzzz", 8, 10)  = "abcdefzzzz"
     * </pre>
     *
     * @param str     the String to do overlaying in, may be null
     * @param overlay the String to overlay, may be null
     * @param start   the position to start overlaying at
     * @param end     the position to stop overlaying before
     * @return overlayed String, {@code null} if null String input
     * @since 2.0
     */
    public static String overlay(final String str, String overlay, int start, int end) {
        if (str == null) {
            return null;
        }
        if (overlay == null) {
            overlay = "";
        }
        final int len = str.length();
        if (start < 0) {
            start = 0;
        }
        if (start > len) {
            start = len;
        }
        if (end < 0) {
            end = 0;
        }
        if (end > len) {
            end = len;
        }
        if (start > end) {
            final int temp = start;
            start = end;
            end = temp;
        }
        return new StringBuilder(len + start - end + overlay.length() + 1)
                .append(str.substring(0, start))
                .append(overlay)
                .append(str.substring(end))
                .toString();
    }

    /**
     * 返回两个字符串中间的内容
     *
     * @param all
     * @param start
     * @param end
     * @return
     */
    public static String getMiddleString(String all, String start, String end) {
        int beginIdx = all.indexOf(start) + start.length();
        int endIdx = all.indexOf(end);

        return all.substring(beginIdx, endIdx);
    }

    /**
     * Convenience method to return a Collection as a delimited (e.g. CSV)
     * String. E.g. useful for <code>readString()</code> implementations.
     *
     * @param coll   the Collection to display
     * @param delim  the delimiter to use (probably a ",")
     * @param prefix the String to start each element with
     * @param suffix the String to end each element with
     * @return the delimited String
     */
    public static String toString(Collection<?> coll,
                                  String delim, String prefix, String suffix) {
        if (coll == null || coll.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Iterator<?> it = coll.iterator();
        while (it.hasNext()) {
            sb.append(prefix).append(it.next()).append(suffix);
            if (it.hasNext()) {
                sb.append(delim);
            }
        }
        return sb.toString();
    }

    public static <K, V> String toString(Map<K, V> map,
                                         String delim, String prefix, String suffix) {
        if (map == null || map.isEmpty()) {
            return "";
        }
        Set<Map.Entry<K, V>> items = map.entrySet();
        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<K, V>> it = items.iterator();
        List<String> strings = new ArrayList<String>();
        while (it.hasNext()) {
            Map.Entry<K, V> entry = it.next();
            strings.add(entry.getKey() + "=" + entry.getValue());
        }

        Collections.sort(strings);
        ListIterator<String> it2 = strings.listIterator();
        while (it2.hasNext()) {
            sb.append(prefix).append(it2.next()).append(suffix);
            if (it2.hasNext()) {
                sb.append(delim);
            }
        }
        return sb.toString();
    }

    /**
     * Convenience method to return a Collection as a delimited (e.g. CSV)
     * String. E.g. useful for <code>readString()</code> implementations.
     *
     * @param coll  the Collection to display
     * @param delim the delimiter to use (probably a ",")
     * @return the delimited String
     */
    public static String toString(Collection<?> coll,
                                  String delim) {
        return toString(coll, delim, "", "");
    }

    public static <K, V> String toString(Map<K, V> map,
                                         String delim) {
        return toString(map, delim, "", "");
    }

    /**
     * Convenience method to return a Collection as a CSV String. E.g. useful
     * for <code>readString()</code> implementations.
     *
     * @param coll the Collection to display
     * @return the delimited String
     */
    public static String toString(Collection<?> coll) {
        return toString(coll, ",");
    }

    public static <K, V> String toString(Map<K, V> map) {
        return toString(map, ",");
    }

    /**
     * Convenience method to return a String array as a delimited (e.g. CSV)
     * String. E.g. useful for <code>readString()</code> implementations.
     *
     * @param arr   the array to display
     * @param delim the delimiter to use (probably a ",")
     * @return the delimited String
     */
    public static String toString(Object[] arr, String delim) {
        if (isEmpty(arr)) {
            return "";
        }
        if (arr.length == 1) {
            return String.valueOf(arr[0]);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) {
                sb.append(delim);
            }
            sb.append(arr[i]);
        }
        return sb.toString();
    }

    /**
     * Convenience method to return a String array as a CSV String. E.g. useful
     * for <code>readString()</code> implementations.
     *
     * @param arr the array to display
     * @return the delimited String
     */
    public static String toString(Object[] arr) {
        return toString(arr, ",");
    }

}
