/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.configuration;

import java.awt.Color;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

/**
 * A utility class to convert the configuration properties into any type.
 *
 * @author Emmanuel Bourg
 * @version $Id: PropertyConverter.java 1534376 2013-10-21 21:14:18Z henning $
 * @since 1.1
 */
public final class PropertyConverter
{
    /** Constant for the list delimiter as char.*/
    static final char LIST_ESC_CHAR = '\\';

    /** Constant for the list delimiter escaping character as string.*/
    static final String LIST_ESCAPE = String.valueOf(LIST_ESC_CHAR);

    /** Constant for the prefix of hex numbers.*/
    private static final String HEX_PREFIX = "0x";

    /** Constant for the radix of hex numbers.*/
    private static final int HEX_RADIX = 16;

    /** Constant for the prefix of binary numbers.*/
    private static final String BIN_PREFIX = "0b";

    /** Constant for the radix of binary numbers.*/
    private static final int BIN_RADIX = 2;

    /** Constant for the argument classes of the Number constructor that takes a String. */
    private static final Class<?>[] CONSTR_ARGS = {String.class};

    /** The fully qualified name of {@link javax.mail.internet.InternetAddress} */
    private static final String INTERNET_ADDRESS_CLASSNAME = "javax.mail.internet.InternetAddress";

    /**
     * Private constructor prevents instances from being created.
     */
    private PropertyConverter()
    {
        // to prevent instantiation...
    }

    /**
     * Converts the specified value to the target class. If the class is a
     * primitive type (Integer.TYPE, Boolean.TYPE, etc) the value returned
     * will use the wrapper type (Integer.class, Boolean.class, etc).
     *
     * @param cls   the target class of the converted value
     * @param value the value to convert
     * @param params optional parameters used for the conversion
     * @return the converted value
     * @throws ConversionException if the value is not compatible with the requested type
     *
     * @since 1.5
     */
    static Object to(Class<?> cls, Object value, Object[] params) throws ConversionException
    {
        if (cls.isInstance(value))
        {
            return value; // no conversion needed
        }

        if (Boolean.class.equals(cls) || Boolean.TYPE.equals(cls))
        {
            return toBoolean(value);
        }
        else if (Character.class.equals(cls) || Character.TYPE.equals(cls))
        {
            return toCharacter(value);
        }
        else if (Number.class.isAssignableFrom(cls) || cls.isPrimitive())
        {
            if (Integer.class.equals(cls) || Integer.TYPE.equals(cls))
            {
                return toInteger(value);
            }
            else if (Long.class.equals(cls) || Long.TYPE.equals(cls))
            {
                return toLong(value);
            }
            else if (Byte.class.equals(cls) || Byte.TYPE.equals(cls))
            {
                return toByte(value);
            }
            else if (Short.class.equals(cls) || Short.TYPE.equals(cls))
            {
                return toShort(value);
            }
            else if (Float.class.equals(cls) || Float.TYPE.equals(cls))
            {
                return toFloat(value);
            }
            else if (Double.class.equals(cls) || Double.TYPE.equals(cls))
            {
                return toDouble(value);
            }
            else if (BigInteger.class.equals(cls))
            {
                return toBigInteger(value);
            }
            else if (BigDecimal.class.equals(cls))
            {
                return toBigDecimal(value);
            }
        }
        else if (Date.class.equals(cls))
        {
            return toDate(value, (String) params[0]);
        }
        else if (Calendar.class.equals(cls))
        {
            return toCalendar(value, (String) params[0]);
        }
        else if (URL.class.equals(cls))
        {
            return toURL(value);
        }
        else if (Locale.class.equals(cls))
        {
            return toLocale(value);
        }
        else if (isEnum(cls))
        {
            return convertToEnum(cls, value);
        }
        else if (Color.class.equals(cls))
        {
            return toColor(value);
        }
        else if (cls.getName().equals(INTERNET_ADDRESS_CLASSNAME))
        {
            return toInternetAddress(value);
        }
        else if (InetAddress.class.isAssignableFrom(cls))
        {
            return toInetAddress(value);
        }

        throw new ConversionException("The value '" + value + "' (" + value.getClass() + ")"
                + " can't be converted to a " + cls.getName() + " object");
    }

    /**
     * Convert the specified object into a Boolean. Internally the
     * {@code org.apache.commons.lang.BooleanUtils} class from the
     * <a href="http://commons.apache.org/lang/">Commons Lang</a>
     * project is used to perform this conversion. This class accepts some more
     * tokens for the boolean value of <b>true</b>, e.g. {@code yes} and
     * {@code on}. Please refer to the documentation of this class for more
     * details.
     *
     * @param value the value to convert
     * @return the converted value
     * @throws ConversionException thrown if the value cannot be converted to a boolean
     */
    public static Boolean toBoolean(Object value) throws ConversionException
    {
        if (value instanceof Boolean)
        {
            return (Boolean) value;
        }
        else if (value instanceof String)
        {
            Boolean b = BooleanUtils.toBooleanObject((String) value);
            if (b == null)
            {
                throw new ConversionException("The value " + value + " can't be converted to a Boolean object");
            }
            return b;
        }
        else
        {
            throw new ConversionException("The value " + value + " can't be converted to a Boolean object");
        }
    }

    /**
     * Converts the specified value object to a {@code Character}. This method
     * converts the passed in object to a string. If the string has exactly one
     * character, this character is returned as result. Otherwise, conversion
     * fails.
     *
     * @param value the value to be converted
     * @return the resulting {@code Character} object
     * @throws ConversionException if the conversion is not possible
     */
    public static Character toCharacter(Object value) throws ConversionException
    {
        String strValue = String.valueOf(value);
        if (strValue.length() == 1)
        {
            return Character.valueOf(strValue.charAt(0));
        }
        else
        {
            throw new ConversionException(
                    String.format(
                            "The value '%s' cannot be converted to a Character object!",
                            strValue));
        }
    }

    /**
     * Convert the specified object into a Byte.
     *
     * @param value the value to convert
     * @return the converted value
     * @throws ConversionException thrown if the value cannot be converted to a byte
     */
    public static Byte toByte(Object value) throws ConversionException
    {
        Number n = toNumber(value, Byte.class);
        if (n instanceof Byte)
        {
            return (Byte) n;
        }
        else
        {
            return new Byte(n.byteValue());
        }
    }

    /**
     * Convert the specified object into a Short.
     *
     * @param value the value to convert
     * @return the converted value
     * @throws ConversionException thrown if the value cannot be converted to a short
     */
    public static Short toShort(Object value) throws ConversionException
    {
        Number n = toNumber(value, Short.class);
        if (n instanceof Short)
        {
            return (Short) n;
        }
        else
        {
            return new Short(n.shortValue());
        }
    }

    /**
     * Convert the specified object into an Integer.
     *
     * @param value the value to convert
     * @return the converted value
     * @throws ConversionException thrown if the value cannot be converted to an integer
     */
    public static Integer toInteger(Object value) throws ConversionException
    {
        Number n = toNumber(value, Integer.class);
        if (n instanceof Integer)
        {
            return (Integer) n;
        }
        else
        {
            return new Integer(n.intValue());
        }
    }

    /**
     * Convert the specified object into a Long.
     *
     * @param value the value to convert
     * @return the converted value
     * @throws ConversionException thrown if the value cannot be converted to a Long
     */
    public static Long toLong(Object value) throws ConversionException
    {
        Number n = toNumber(value, Long.class);
        if (n instanceof Long)
        {
            return (Long) n;
        }
        else
        {
            return new Long(n.longValue());
        }
    }

    /**
     * Convert the specified object into a Float.
     *
     * @param value the value to convert
     * @return the converted value
     * @throws ConversionException thrown if the value cannot be converted to a Float
     */
    public static Float toFloat(Object value) throws ConversionException
    {
        Number n = toNumber(value, Float.class);
        if (n instanceof Float)
        {
            return (Float) n;
        }
        else
        {
            return new Float(n.floatValue());
        }
    }

    /**
     * Convert the specified object into a Double.
     *
     * @param value the value to convert
     * @return the converted value
     * @throws ConversionException thrown if the value cannot be converted to a Double
     */
    public static Double toDouble(Object value) throws ConversionException
    {
        Number n = toNumber(value, Double.class);
        if (n instanceof Double)
        {
            return (Double) n;
        }
        else
        {
            return new Double(n.doubleValue());
        }
    }

    /**
     * Convert the specified object into a BigInteger.
     *
     * @param value the value to convert
     * @return the converted value
     * @throws ConversionException thrown if the value cannot be converted to a BigInteger
     */
    public static BigInteger toBigInteger(Object value) throws ConversionException
    {
        Number n = toNumber(value, BigInteger.class);
        if (n instanceof BigInteger)
        {
            return (BigInteger) n;
        }
        else
        {
            return BigInteger.valueOf(n.longValue());
        }
    }

    /**
     * Convert the specified object into a BigDecimal.
     *
     * @param value the value to convert
     * @return the converted value
     * @throws ConversionException thrown if the value cannot be converted to a BigDecimal
     */
    public static BigDecimal toBigDecimal(Object value) throws ConversionException
    {
        Number n = toNumber(value, BigDecimal.class);
        if (n instanceof BigDecimal)
        {
            return (BigDecimal) n;
        }
        else
        {
            return new BigDecimal(n.doubleValue());
        }
    }

    /**
     * Tries to convert the specified object into a number object. This method
     * is used by the conversion methods for number types. Note that the return
     * value is not in always of the specified target class, but only if a new
     * object has to be created.
     *
     * @param value the value to be converted (must not be <b>null</b>)
     * @param targetClass the target class of the conversion (must be derived
     * from {@code java.lang.Number})
     * @return the converted number
     * @throws ConversionException if the object cannot be converted
     */
    static Number toNumber(Object value, Class<?> targetClass) throws ConversionException
    {
        if (value instanceof Number)
        {
            return (Number) value;
        }
        else
        {
            String str = value.toString();
            if (str.startsWith(HEX_PREFIX))
            {
                try
                {
                    return new BigInteger(str.substring(HEX_PREFIX.length()), HEX_RADIX);
                }
                catch (NumberFormatException nex)
                {
                    throw new ConversionException("Could not convert " + str
                            + " to " + targetClass.getName()
                            + "! Invalid hex number.", nex);
                }
            }

            if (str.startsWith(BIN_PREFIX))
            {
                try
                {
                    return new BigInteger(str.substring(BIN_PREFIX.length()), BIN_RADIX);
                }
                catch (NumberFormatException nex)
                {
                    throw new ConversionException("Could not convert " + str
                            + " to " + targetClass.getName()
                            + "! Invalid binary number.", nex);
                }
            }

            try
            {
                Constructor<?> constr = targetClass.getConstructor(CONSTR_ARGS);
                return (Number) constr.newInstance(new Object[]{str});
            }
            catch (InvocationTargetException itex)
            {
                throw new ConversionException("Could not convert " + str
                        + " to " + targetClass.getName(), itex
                        .getTargetException());
            }
            catch (Exception ex)
            {
                // Treat all possible exceptions the same way
                throw new ConversionException(
                        "Conversion error when trying to convert " + str
                                + " to " + targetClass.getName(), ex);
            }
        }
    }

    /**
     * Convert the specified object into an URL.
     *
     * @param value the value to convert
     * @return the converted value
     * @throws ConversionException thrown if the value cannot be converted to an URL
     */
    public static URL toURL(Object value) throws ConversionException
    {
        if (value instanceof URL)
        {
            return (URL) value;
        }
        else if (value instanceof String)
        {
            try
            {
                return new URL((String) value);
            }
            catch (MalformedURLException e)
            {
                throw new ConversionException("The value " + value + " can't be converted to an URL", e);
            }
        }
        else
        {
            throw new ConversionException("The value " + value + " can't be converted to an URL");
        }
    }

    /**
     * Convert the specified object into a Locale.
     *
     * @param value the value to convert
     * @return the converted value
     * @throws ConversionException thrown if the value cannot be converted to a Locale
     */
    public static Locale toLocale(Object value) throws ConversionException
    {
        if (value instanceof Locale)
        {
            return (Locale) value;
        }
        else if (value instanceof String)
        {
            List<String> elements = split((String) value, '_');
            int size = elements.size();

            if (size >= 1 && ((elements.get(0)).length() == 2 || (elements.get(0)).length() == 0))
            {
                String language = elements.get(0);
                String country = (size >= 2) ? elements.get(1) : "";
                String variant = (size >= 3) ? elements.get(2) : "";

                return new Locale(language, country, variant);
            }
            else
            {
                throw new ConversionException("The value " + value + " can't be converted to a Locale");
            }
        }
        else
        {
            throw new ConversionException("The value " + value + " can't be converted to a Locale");
        }
    }

    /**
     * Split a string on the specified delimiter. To be removed when
     * commons-lang has a better replacement available (Tokenizer?).
     *
     * todo: replace with a commons-lang equivalent
     *
     * @param s          the string to split
     * @param delimiter  the delimiter
     * @param trim       a flag whether the single elements should be trimmed
     * @return a list with the single tokens
     */
    public static List<String> split(String s, char delimiter, boolean trim)
    {
        if (s == null)
        {
            return new ArrayList<String>();
        }

        List<String> list = new ArrayList<String>();

        StringBuilder token = new StringBuilder();
        int begin = 0;
        boolean inEscape = false;

        while (begin < s.length())
        {
            char c = s.charAt(begin);
            if (inEscape)
            {
                // last character was the escape marker
                // can current character be escaped?
                if (c != delimiter && c != LIST_ESC_CHAR)
                {
                    // no, also add escape character
                    token.append(LIST_ESC_CHAR);
                }
                token.append(c);
                inEscape = false;
            }

            else
            {
                if (c == delimiter)
                {
                    // found a list delimiter -> add token and resetDefaultFileSystem buffer
                    String t = token.toString();
                    if (trim)
                    {
                        t = t.trim();
                    }
                    list.add(t);
                    token = new StringBuilder();
                }
                else if (c == LIST_ESC_CHAR)
                {
                    // eventually escape next character
                    inEscape = true;
                }
                else
                {
                    token.append(c);
                }
            }

            begin++;
        }

        // Trailing delimiter?
        if (inEscape)
        {
            token.append(LIST_ESC_CHAR);
        }
        // Add last token
        String t = token.toString();
        if (trim)
        {
            t = t.trim();
        }
        list.add(t);

        return list;
    }

    /**
     * Split a string on the specified delimiter always trimming the elements.
     * This is a shortcut for {@code split(s, delimiter, true)}.
     *
     * @param s          the string to split
     * @param delimiter  the delimiter
     * @return a list with the single tokens
     */
    public static List<String> split(String s, char delimiter)
    {
        return split(s, delimiter, true);
    }

    /**
     * Escapes the delimiters that might be contained in the given string. This
     * method works like {@link #escapeListDelimiter(String, char)}. In addition,
     * a single backslash will also be escaped.
     *
     * @param s the string with the value
     * @param delimiter the list delimiter to use
     * @return the correctly escaped string
     */
    public static String escapeDelimiters(String s, char delimiter)
    {
        String s1 = StringUtils.replace(s, LIST_ESCAPE, LIST_ESCAPE + LIST_ESCAPE);
        return escapeListDelimiter(s1, delimiter);
    }

    /**
     * Escapes the list delimiter if it is contained in the given string. This
     * method ensures that list delimiter characters that are part of a
     * property's value are correctly escaped when a configuration is saved to a
     * file. Otherwise when loaded again the property will be treated as a list
     * property.
     *
     * @param s the string with the value
     * @param delimiter the list delimiter to use
     * @return the escaped string
     * @since 1.7
     */
    public static String escapeListDelimiter(String s, char delimiter)
    {
        return StringUtils.replace(s, String.valueOf(delimiter), LIST_ESCAPE
                + delimiter);
    }

    /**
     * Convert the specified object into a Color. If the value is a String,
     * the format allowed is (#)?[0-9A-F]{6}([0-9A-F]{2})?. Examples:
     * <ul>
     *   <li>FF0000 (red)</li>
     *   <li>0000FFA0 (semi transparent blue)</li>
     *   <li>#CCCCCC (gray)</li>
     *   <li>#00FF00A0 (semi transparent green)</li>
     * </ul>
     *
     * @param value the value to convert
     * @return the converted value
     * @throws ConversionException thrown if the value cannot be converted to a Color
     */
    public static Color toColor(Object value) throws ConversionException
    {
        if (value instanceof Color)
        {
            return (Color) value;
        }
        else if (value instanceof String && !StringUtils.isBlank((String) value))
        {
            String color = ((String) value).trim();

            int[] components = new int[3];

            // check the size of the string
            int minlength = components.length * 2;
            if (color.length() < minlength)
            {
                throw new ConversionException("The value " + value + " can't be converted to a Color");
            }

            // remove the leading #
            if (color.startsWith("#"))
            {
                color = color.substring(1);
            }

            try
            {
                // parse the components
                for (int i = 0; i < components.length; i++)
                {
                    components[i] = Integer.parseInt(color.substring(2 * i, 2 * i + 2), HEX_RADIX);
                }

                // parse the transparency
                int alpha;
                if (color.length() >= minlength + 2)
                {
                    alpha = Integer.parseInt(color.substring(minlength, minlength + 2), HEX_RADIX);
                }
                else
                {
                    alpha = Color.black.getAlpha();
                }

                return new Color(components[0], components[1], components[2], alpha);
            }
            catch (Exception e)
            {
                throw new ConversionException("The value " + value + " can't be converted to a Color", e);
            }
        }
        else
        {
            throw new ConversionException("The value " + value + " can't be converted to a Color");
        }
    }

    /**
     * Convert the specified value into an internet address.
     *
     * @param value the value to convert
     * @return the converted value
     * @throws ConversionException thrown if the value cannot be converted to a InetAddress
     *
     * @since 1.5
     */
    static InetAddress toInetAddress(Object value) throws ConversionException
    {
        if (value instanceof InetAddress)
        {
            return (InetAddress) value;
        }
        else if (value instanceof String)
        {
            try
            {
                return InetAddress.getByName((String) value);
            }
            catch (UnknownHostException e)
            {
                throw new ConversionException("The value " + value + " can't be converted to a InetAddress", e);
            }
        }
        else
        {
            throw new ConversionException("The value " + value + " can't be converted to a InetAddress");
        }
    }

    /**
     * Convert the specified value into an email address.
     *
     * @param value the value to convert
     * @return the converted value
     * @throws ConversionException thrown if the value cannot be converted to an email address
     *
     * @since 1.5
     */
    static Object toInternetAddress(Object value) throws ConversionException
    {
        if (value.getClass().getName().equals(INTERNET_ADDRESS_CLASSNAME))
        {
            return value;
        }
        else if (value instanceof String)
        {
            try
            {
                Constructor<?> ctor = Class.forName(INTERNET_ADDRESS_CLASSNAME)
                        .getConstructor(new Class[] {String.class});
                return ctor.newInstance(new Object[] {value});
            }
            catch (Exception e)
            {
                throw new ConversionException("The value " + value + " can't be converted to a InternetAddress", e);
            }
        }
        else
        {
            throw new ConversionException("The value " + value + " can't be converted to a InternetAddress");
        }
    }

    /**
     * Calls Class.isEnum() on Java 5, returns false on older JRE.
     */
    static boolean isEnum(Class<?> cls)
    {
        return cls.isEnum();
    }

    /**
     * Convert the specified value into a Java 5 enum.
     *
     * @param value the value to convert
     * @param cls   the type of the enumeration
     * @return the converted value
     * @throws ConversionException thrown if the value cannot be converted to an enumeration
     *
     * @since 1.5
     */
    static <E extends Enum<E>> E toEnum(Object value, Class<E> cls) throws ConversionException
    {
        if (value.getClass().equals(cls))
        {
            return cls.cast(value);
        }
        else if (value instanceof String)
        {
            try
            {
                return Enum.valueOf(cls, (String) value);
            }
            catch (Exception e)
            {
                throw new ConversionException("The value " + value + " can't be converted to a " + cls.getName());
            }
        }
        else if (value instanceof Number)
        {
            try
            {
                E[] enumConstants = cls.getEnumConstants();
                return enumConstants[((Number) value).intValue()];
            }
            catch (Exception e)
            {
                throw new ConversionException("The value " + value + " can't be converted to a " + cls.getName());
            }
        }
        else
        {
            throw new ConversionException("The value " + value + " can't be converted to a " + cls.getName());
        }
    }

    /**
     * Convert the specified object into a Date.
     *
     * @param value  the value to convert
     * @param format the DateFormat pattern to parse String values
     * @return the converted value
     * @throws ConversionException thrown if the value cannot be converted to a Calendar
     */
    public static Date toDate(Object value, String format) throws ConversionException
    {
        if (value instanceof Date)
        {
            return (Date) value;
        }
        else if (value instanceof Calendar)
        {
            return ((Calendar) value).getTime();
        }
        else if (value instanceof String)
        {
            try
            {
                return new SimpleDateFormat(format).parse((String) value);
            }
            catch (ParseException e)
            {
                throw new ConversionException("The value " + value + " can't be converted to a Date", e);
            }
        }
        else
        {
            throw new ConversionException("The value " + value + " can't be converted to a Date");
        }
    }

    /**
     * Convert the specified object into a Calendar.
     *
     * @param value  the value to convert
     * @param format the DateFormat pattern to parse String values
     * @return the converted value
     * @throws ConversionException thrown if the value cannot be converted to a Calendar
     */
    public static Calendar toCalendar(Object value, String format) throws ConversionException
    {
        if (value instanceof Calendar)
        {
            return (Calendar) value;
        }
        else if (value instanceof Date)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime((Date) value);
            return calendar;
        }
        else if (value instanceof String)
        {
            try
            {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new SimpleDateFormat(format).parse((String) value));
                return calendar;
            }
            catch (ParseException e)
            {
                throw new ConversionException("The value " + value + " can't be converted to a Calendar", e);
            }
        }
        else
        {
            throw new ConversionException("The value " + value + " can't be converted to a Calendar");
        }
    }

    /**
     * Returns an iterator over the simple values of a composite value. This
     * implementation calls {@link #flatten(Object, char)} and
     * returns an iterator over the returned collection.
     *
     * @param value the value to "split"
     * @param delimiter the delimiter for String values
     * @return an iterator for accessing the single values
     */
    public static Iterator<?> toIterator(Object value, char delimiter)
    {
        return flatten(value, delimiter).iterator();
    }

    /**
     * Returns a collection with all values contained in the specified object.
     * This method is used for instance by the {@code addProperty()}
     * implementation of the default configurations to gather all values of the
     * property to add. Depending on the type of the passed in object the
     * following things happen:
     * <ul>
     * <li>Strings are checked for delimiter characters and split if necessary.</li>
     * <li>For objects implementing the {@code Iterable} interface, the
     * corresponding {@code Iterator} is obtained, and contained elements
     * are added to the resulting collection.</li>
     * <li>Arrays are treated as {@code Iterable} objects.</li>
     * <li>All other types are directly inserted.</li>
     * <li>Recursive combinations are supported, e.g. a collection containing
     * an array that contains strings: The resulting collection will only
     * contain primitive objects (hence the name &quot;flatten&quot;).</li>
     * </ul>
     *
     * @param value the value to be processed
     * @param delimiter the delimiter for String values
     * @return a &quot;flat&quot; collection containing all primitive values of
     *         the passed in object
     */
    private static Collection<?> flatten(Object value, char delimiter)
    {
        if (value instanceof String)
        {
            String s = (String) value;
            if (s.indexOf(delimiter) > 0)
            {
                return split(s, delimiter);
            }
        }

        Collection<Object> result = new LinkedList<Object>();
        if (value instanceof Iterable)
        {
            flattenIterator(result, ((Iterable<?>) value).iterator(), delimiter);
        }
        else if (value instanceof Iterator)
        {
            flattenIterator(result, (Iterator<?>) value, delimiter);
        }
        else if (value != null)
        {
            if (value.getClass().isArray())
            {
                for (int len = Array.getLength(value), idx = 0; idx < len; idx++)
                {
                    result.addAll(flatten(Array.get(value, idx), delimiter));
                }
            }
            else
            {
                result.add(value);
            }
        }

        return result;
    }

    /**
     * Flattens the given iterator. For each element in the iteration
     * {@code flatten()} will be called recursively.
     *
     * @param target the target collection
     * @param it the iterator to process
     * @param delimiter the delimiter for String values
     */
    private static void flattenIterator(Collection<Object> target, Iterator<?> it, char delimiter)
    {
        while (it.hasNext())
        {
            target.addAll(flatten(it.next(), delimiter));
        }
    }

    /**
     * Performs interpolation of the specified value. This method checks if the
     * given value contains variables of the form <code>${...}</code>. If
     * this is the case, all occurrences will be substituted by their current
     * values.
     *
     * @param value the value to be interpolated
     * @param config the current configuration object
     * @return the interpolated value
     */
    public static Object interpolate(Object value, AbstractConfiguration config)
    {
        if (value instanceof String)
        {
            return config.getSubstitutor().replace((String) value);
        }
        else
        {
            return value;
        }
    }

    /**
     * Helper method for converting a value to a constant of an enumeration
     * class.
     *
     * @param enumClass the enumeration class
     * @param value the value to be converted
     * @return the converted value
     */
    @SuppressWarnings("unchecked")
    // conversion is safe because we know that the class is an Enum class
    private static Object convertToEnum(Class<?> enumClass, Object value)
    {
        return toEnum(value, enumClass.asSubclass(Enum.class));
    }
}
