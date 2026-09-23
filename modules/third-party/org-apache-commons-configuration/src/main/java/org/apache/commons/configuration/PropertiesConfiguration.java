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

import java.io.File;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

/**
 * This is the "classic" Properties loader which loads the values from
 * a single or multiple files (which can be chained with "include =".
 * All given path references are either absolute or relative to the
 * file name supplied in the constructor.
 * <p>
 * In this class, empty PropertyConfigurations can be built, properties
 * added and later saved. include statements are (obviously) not supported
 * if you don't construct a PropertyConfiguration from a file.
 *
 * <p>The properties file syntax is explained here, basically it follows
 * the syntax of the stream parsed by {@link java.util.Properties#load} and
 * adds several useful extensions:
 *
 * <ul>
 *  <li>
 *   Each property has the syntax <code>key &lt;separator> value</code>. The
 *   separators accepted are {@code '='}, {@code ':'} and any white
 *   space character. Examples:
 * <pre>
 *  key1 = value1
 *  key2 : value2
 *  key3   value3</pre>
 *  </li>
 *  <li>
 *   The <i>key</i> may use any character, separators must be escaped:
 * <pre>
 *  key\:foo = bar</pre>
 *  </li>
 *  <li>
 *   <i>value</i> may be separated on different lines if a backslash
 *   is placed at the end of the line that continues below.
 *  </li>
 *  <li>
 *   <i>value</i> can contain <em>value delimiters</em> and will then be interpreted
 *   as a list of tokens. Default value delimiter is the comma ','. So the
 *   following property definition
 * <pre>
 *  key = This property, has multiple, values
 * </pre>
 *   will result in a property with three values. You can change the value
 *   delimiter using the {@link AbstractConfiguration#setListDelimiter(char)}
 *   method. Setting the delimiter to 0 will disable value splitting completely.
 *  </li>
 *  <li>
 *   Commas in each token are escaped placing a backslash right before
 *   the comma.
 *  </li>
 *  <li>
 *   If a <i>key</i> is used more than once, the values are appended
 *   like if they were on the same line separated with commas. <em>Note</em>:
 *   When the configuration file is written back to disk the associated
 *   {@link PropertiesConfigurationLayout} object (see below) will
 *   try to preserve as much of the original format as possible, i.e. properties
 *   with multiple values defined on a single line will also be written back on
 *   a single line, and multiple occurrences of a single key will be written on
 *   multiple lines. If the {@code addProperty()} method was called
 *   multiple times for adding multiple values to a property, these properties
 *   will per default be written on multiple lines in the output file, too.
 *   Some options of the {@code PropertiesConfigurationLayout} class have
 *   influence on that behavior.
 *  </li>
 *  <li>
 *   Blank lines and lines starting with character '#' or '!' are skipped.
 *  </li>
 *  <li>
 *   If a property is named "include" (or whatever is defined by
 *   setInclude() and getInclude() and the value of that property is
 *   the full path to a file on disk, that file will be included into
 *   the configuration. You can also pull in files relative to the parent
 *   configuration file. So if you have something like the following:
 *
 *   include = additional.properties
 *
 *   Then "additional.properties" is expected to be in the same
 *   directory as the parent configuration file.
 *
 *   The properties in the included file are added to the parent configuration,
 *   they do not replace existing properties with the same key.
 *
 *  </li>
 * </ul>
 *
 * <p>Here is an example of a valid extended properties file:
 *
 * <p><pre>
 *      # lines starting with # are comments
 *
 *      # This is the simplest property
 *      key = value
 *
 *      # A long property may be separated on multiple lines
 *      longvalue = aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa \
 *                  aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
 *
 *      # This is a property with many tokens
 *      tokens_on_a_line = first token, second token
 *
 *      # This sequence generates exactly the same result
 *      tokens_on_multiple_lines = first token
 *      tokens_on_multiple_lines = second token
 *
 *      # commas may be escaped in tokens
 *      commas.escaped = Hi\, what'up?
 *
 *      # properties can reference other properties
 *      base.prop = /base
 *      first.prop = ${base.prop}/first
 *      second.prop = ${first.prop}/second
 * </pre>
 *
 * <p>A {@code PropertiesConfiguration} object is associated with an
 * instance of the {@link PropertiesConfigurationLayout} class,
 * which is responsible for storing the layout of the parsed properties file
 * (i.e. empty lines, comments, and such things). The {@code getLayout()}
 * method can be used to obtain this layout object. With {@code setLayout()}
 * a new layout object can be set. This should be done before a properties file
 * was loaded.
 * <p><em>Note:</em>Configuration objects of this type can be read concurrently
 * by multiple threads. However if one of these threads modifies the object,
 * synchronization has to be performed manually.
 *
 * @see java.util.Properties#load
 *
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:daveb@miceda-data">Dave Bryson</a>
 * @author <a href="mailto:geirm@optonline.net">Geir Magnusson Jr.</a>
 * @author <a href="mailto:leon@opticode.co.za">Leon Messerschmidt</a>
 * @author <a href="mailto:kjohnson@transparent.com">Kent Johnson</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:ipriha@surfeu.fi">Ilkka Priha</a>
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @author <a href="mailto:ebourg@apache.org">Emmanuel Bourg</a>
 * @version $Id: PropertiesConfiguration.java 1534445 2013-10-22 01:19:43Z henning $
 */
public class PropertiesConfiguration extends AbstractFileConfiguration
{
    /** Constant for the supported comment characters.*/
    static final String COMMENT_CHARS = "#!";

    /** Constant for the default properties separator.*/
    static final String DEFAULT_SEPARATOR = " = ";

    /**
     * Constant for the default {@code IOFactory}. This instance is used
     * when no specific factory was set.
     */
    private static final IOFactory DEFAULT_IO_FACTORY = new DefaultIOFactory();

    /**
     * This is the name of the property that can point to other
     * properties file for including other properties files.
     */
    private static String include = "include";

    /** The list of possible key/value separators */
    private static final char[] SEPARATORS = new char[] {'=', ':'};

    /** The white space characters used as key/value separators. */
    private static final char[] WHITE_SPACE = new char[]{' ', '\t', '\f'};

    /**
     * The default encoding (ISO-8859-1 as specified by
     * http://java.sun.com/j2se/1.5.0/docs/api/java/util/Properties.html)
     */
    private static final String DEFAULT_ENCODING = "ISO-8859-1";

    /** Constant for the platform specific line separator.*/
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /** Constant for the escaping character.*/
    private static final String ESCAPE = "\\";

    /** Constant for the escaped escaping character.*/
    private static final String DOUBLE_ESC = ESCAPE + ESCAPE;

    /** Constant for the radix of hex numbers.*/
    private static final int HEX_RADIX = 16;

    /** Constant for the length of a unicode literal.*/
    private static final int UNICODE_LEN = 4;

    /** Stores the layout object.*/
    private PropertiesConfigurationLayout layout;

    /** The IOFactory for creating readers and writers.*/
    private volatile IOFactory ioFactory;

    /** Allow file inclusion or not */
    private boolean includesAllowed = true;

    /**
     * Creates an empty PropertyConfiguration object which can be
     * used to synthesize a new Properties file by adding values and
     * then saving().
     */
    public PropertiesConfiguration()
    {
        layout = createLayout();
    }

    /**
     * Creates and loads the extended properties from the specified file.
     * The specified file can contain "include = " properties which then
     * are loaded and merged into the properties.
     *
     * @param fileName The name of the properties file to load.
     * @throws ConfigurationException Error while loading the properties file
     */
    public PropertiesConfiguration(String fileName) throws ConfigurationException
    {
        super(fileName);
    }

    /**
     * Creates and loads the extended properties from the specified file.
     * The specified file can contain "include = " properties which then
     * are loaded and merged into the properties. If the file does not exist,
     * an empty configuration will be created. Later the {@code save()}
     * method can be called to save the properties to the specified file.
     *
     * @param file The properties file to load.
     * @throws ConfigurationException Error while loading the properties file
     */
    public PropertiesConfiguration(File file) throws ConfigurationException
    {
        super(file);

        // If the file does not exist, no layout object was created. We have to
        // do this manually in this case.
        getLayout();
    }

    /**
     * Creates and loads the extended properties from the specified URL.
     * The specified file can contain "include = " properties which then
     * are loaded and merged into the properties.
     *
     * @param url The location of the properties file to load.
     * @throws ConfigurationException Error while loading the properties file
     */
    public PropertiesConfiguration(URL url) throws ConfigurationException
    {
        super(url);
    }

    /**
     * Gets the property value for including other properties files.
     * By default it is "include".
     *
     * @return A String.
     */
    public static String getInclude()
    {
        return PropertiesConfiguration.include;
    }

    /**
     * Sets the property value for including other properties files.
     * By default it is "include".
     *
     * @param inc A String.
     */
    public static void setInclude(String inc)
    {
        PropertiesConfiguration.include = inc;
    }

    /**
     * Controls whether additional files can be loaded by the include = <xxx>
     * statement or not. This is <b>true</b> per default.
     *
     * @param includesAllowed True if Includes are allowed.
     */
    public void setIncludesAllowed(boolean includesAllowed)
    {
        this.includesAllowed = includesAllowed;
    }

    /**
     * Reports the status of file inclusion.
     *
     * @return True if include files are loaded.
     *
     * @see PropertiesConfiguration#isIncludesAllowed()
     *
     * @deprecated Only exists to not break backwards compatibility.
     * Use {@link PropertiesConfiguration#isIncludesAllowed()} instead.
     */
    @Deprecated
    public boolean getIncludesAllowed()
    {
        return isIncludesAllowed();
    }

    /**
     * Reports the status of file inclusion.
     *
     * @return True if include files are loaded.
     */
    public boolean isIncludesAllowed()
    {
        return this.includesAllowed;
    }

    /**
     * Return the comment header.
     *
     * @return the comment header
     * @since 1.1
     */
    public String getHeader()
    {
        return getLayout().getHeaderComment();
    }

    /**
     * Set the comment header.
     *
     * @param header the header to use
     * @since 1.1
     */
    public void setHeader(String header)
    {
        getLayout().setHeaderComment(header);
    }

    /**
     * Returns the footer comment. This is a comment at the very end of the
     * file.
     *
     * @return the footer comment
     * @since 1.10
     */
    public String getFooter()
    {
        return getLayout().getFooterComment();
    }

    /**
     * Sets the footer comment. If set, this comment is written after all
     * properties at the end of the file.
     *
     * @param footer the footer comment
     * @since 1.10
     */
    public void setFooter(String footer)
    {
        getLayout().setFooterComment(footer);
    }

    /**
     * Returns the encoding to be used when loading or storing configuration
     * data. This implementation ensures that the default encoding will be used
     * if none has been set explicitly.
     *
     * @return the encoding
     */
    @Override
    public String getEncoding()
    {
        String enc = super.getEncoding();
        return (enc != null) ? enc : DEFAULT_ENCODING;
    }

    /**
     * Returns the associated layout object.
     *
     * @return the associated layout object
     * @since 1.3
     */
    public synchronized PropertiesConfigurationLayout getLayout()
    {
        if (layout == null)
        {
            layout = createLayout();
        }
        return layout;
    }

    /**
     * Sets the associated layout object.
     *
     * @param layout the new layout object; can be <b>null</b>, then a new
     * layout object will be created
     * @since 1.3
     */
    public synchronized void setLayout(PropertiesConfigurationLayout layout)
    {
        // only one layout must exist
        if (this.layout != null)
        {
            removeConfigurationListener(this.layout);
        }

        if (layout == null)
        {
            this.layout = createLayout();
        }
        else
        {
            this.layout = layout;
        }
    }

    /**
     * Creates the associated layout object. This method is invoked when the
     * layout object is accessed and has not been created yet. Derived classes
     * can override this method to hook in a different layout implementation.
     *
     * @return the layout object to use
     * @since 1.3
     */
    protected PropertiesConfigurationLayout createLayout()
    {
        return new PropertiesConfigurationLayout(this);
    }

    /**
     * Returns the {@code IOFactory} to be used for creating readers and
     * writers when loading or saving this configuration.
     *
     * @return the {@code IOFactory}
     * @since 1.7
     */
    public IOFactory getIOFactory()
    {
        return (ioFactory != null) ? ioFactory : DEFAULT_IO_FACTORY;
    }

    /**
     * Sets the {@code IOFactory} to be used for creating readers and
     * writers when loading or saving this configuration. Using this method a
     * client can customize the reader and writer classes used by the load and
     * save operations. Note that this method must be called before invoking
     * one of the {@code load()} and {@code save()} methods.
     * Especially, if you want to use a custom {@code IOFactory} for
     * changing the {@code PropertiesReader}, you cannot load the
     * configuration data in the constructor.
     *
     * @param ioFactory the new {@code IOFactory} (must not be <b>null</b>)
     * @throws IllegalArgumentException if the {@code IOFactory} is
     *         <b>null</b>
     * @since 1.7
     */
    public void setIOFactory(IOFactory ioFactory)
    {
        if (ioFactory == null)
        {
            throw new IllegalArgumentException("IOFactory must not be null!");
        }

        this.ioFactory = ioFactory;
    }

    /**
     * Load the properties from the given reader.
     * Note that the {@code clear()} method is not called, so
     * the properties contained in the loaded file will be added to the
     * actual set of properties.
     *
     * @param in An InputStream.
     *
     * @throws ConfigurationException if an error occurs
     */
    public synchronized void load(Reader in) throws ConfigurationException
    {
        boolean oldAutoSave = isAutoSave();
        setAutoSave(false);

        try
        {
            getLayout().load(in);
        }
        finally
        {
            setAutoSave(oldAutoSave);
        }
    }

    /**
     * Save the configuration to the specified stream.
     *
     * @param writer the output stream used to save the configuration
     * @throws ConfigurationException if an error occurs
     */
    public void save(Writer writer) throws ConfigurationException
    {
        enterNoReload();
        try
        {
            getLayout().save(writer);
        }
        finally
        {
            exitNoReload();
        }
    }

    /**
     * Extend the setBasePath method to turn includes
     * on and off based on the existence of a base path.
     *
     * @param basePath The new basePath to set.
     */
    @Override
    public void setBasePath(String basePath)
    {
        super.setBasePath(basePath);
        setIncludesAllowed(StringUtils.isNotEmpty(basePath));
    }

    /**
     * Creates a copy of this object.
     *
     * @return the copy
     */
    @Override
    public Object clone()
    {
        PropertiesConfiguration copy = (PropertiesConfiguration) super.clone();
        if (layout != null)
        {
            copy.setLayout(new PropertiesConfigurationLayout(copy, layout));
        }
        return copy;
    }

    /**
     * This method is invoked by the associated
     * {@link PropertiesConfigurationLayout} object for each
     * property definition detected in the parsed properties file. Its task is
     * to check whether this is a special property definition (e.g. the
     * {@code include} property). If not, the property must be added to
     * this configuration. The return value indicates whether the property
     * should be treated as a normal property. If it is <b>false</b>, the
     * layout object will ignore this property.
     *
     * @param key the property key
     * @param value the property value
     * @return a flag whether this is a normal property
     * @throws ConfigurationException if an error occurs
     * @since 1.3
     */
    boolean propertyLoaded(String key, String value)
            throws ConfigurationException
    {
        boolean result;

        if (StringUtils.isNotEmpty(getInclude())
                && key.equalsIgnoreCase(getInclude()))
        {
            if (isIncludesAllowed())
            {
                String[] files;
                if (!isDelimiterParsingDisabled())
                {
                    files = StringUtils.split(value, getListDelimiter());
                }
                else
                {
                    files = new String[]{value};
                }
                for (String f : files)
                {
                    loadIncludeFile(interpolate(f.trim()));
                }
            }
            result = false;
        }

        else
        {
            addProperty(key, value);
            result = true;
        }

        return result;
    }

    /**
     * Tests whether a line is a comment, i.e. whether it starts with a comment
     * character.
     *
     * @param line the line
     * @return a flag if this is a comment line
     * @since 1.3
     */
    static boolean isCommentLine(String line)
    {
        String s = line.trim();
        // blanc lines are also treated as comment lines
        return s.length() < 1 || COMMENT_CHARS.indexOf(s.charAt(0)) >= 0;
    }

    /**
     * Returns the number of trailing backslashes. This is sometimes needed for
     * the correct handling of escape characters.
     *
     * @param line the string to investigate
     * @return the number of trailing backslashes
     */
    private static int countTrailingBS(String line)
    {
        int bsCount = 0;
        for (int idx = line.length() - 1; idx >= 0 && line.charAt(idx) == '\\'; idx--)
        {
            bsCount++;
        }

        return bsCount;
    }

    /**
     * This class is used to read properties lines. These lines do
     * not terminate with new-line chars but rather when there is no
     * backslash sign a the end of the line.  This is used to
     * concatenate multiple lines for readability.
     */
    public static class PropertiesReader extends LineNumberReader
    {
        /** The regular expression to parse the key and the value of a property. */
        private static final Pattern PROPERTY_PATTERN = Pattern
                .compile("(([\\S&&[^\\\\" + new String(SEPARATORS)
                        + "]]|\\\\.)*)(\\s*(\\s+|[" + new String(SEPARATORS)
                        + "])\\s*)(.*)");

        /** Constant for the index of the group for the key. */
        private static final int IDX_KEY = 1;

        /** Constant for the index of the group for the value. */
        private static final int IDX_VALUE = 5;

        /** Constant for the index of the group for the separator. */
        private static final int IDX_SEPARATOR = 3;

        /** Stores the comment lines for the currently processed property.*/
        private List<String> commentLines;

        /** Stores the name of the last read property.*/
        private String propertyName;

        /** Stores the value of the last read property.*/
        private String propertyValue;

        /** Stores the property separator of the last read property.*/
        private String propertySeparator = DEFAULT_SEPARATOR;

        /** Stores the list delimiter character.*/
        private char delimiter;

        /**
         * Constructor.
         *
         * @param reader A Reader.
         */
        public PropertiesReader(Reader reader)
        {
            this(reader, AbstractConfiguration.getDefaultListDelimiter());
        }

        /**
         * Creates a new instance of {@code PropertiesReader} and sets
         * the underlying reader and the list delimiter.
         *
         * @param reader the reader
         * @param listDelimiter the list delimiter character
         * @since 1.3
         */
        public PropertiesReader(Reader reader, char listDelimiter)
        {
            super(reader);
            commentLines = new ArrayList<String>();
            delimiter = listDelimiter;
        }

        /**
         * Reads a property line. Returns null if Stream is
         * at EOF. Concatenates lines ending with "\".
         * Skips lines beginning with "#" or "!" and empty lines.
         * The return value is a property definition (<code>&lt;name&gt;</code>
         * = <code>&lt;value&gt;</code>)
         *
         * @return A string containing a property value or null
         *
         * @throws IOException in case of an I/O error
         */
        public String readProperty() throws IOException
        {
            commentLines.clear();
            StringBuilder buffer = new StringBuilder();

            while (true)
            {
                String line = readLine();
                if (line == null)
                {
                    // EOF
                    return null;
                }

                if (isCommentLine(line))
                {
                    commentLines.add(line);
                    continue;
                }

                line = line.trim();

                if (checkCombineLines(line))
                {
                    line = line.substring(0, line.length() - 1);
                    buffer.append(line);
                }
                else
                {
                    buffer.append(line);
                    break;
                }
            }
            return buffer.toString();
        }

        /**
         * Parses the next property from the input stream and stores the found
         * name and value in internal fields. These fields can be obtained using
         * the provided getter methods. The return value indicates whether EOF
         * was reached (<b>false</b>) or whether further properties are
         * available (<b>true</b>).
         *
         * @return a flag if further properties are available
         * @throws IOException if an error occurs
         * @since 1.3
         */
        public boolean nextProperty() throws IOException
        {
            String line = readProperty();

            if (line == null)
            {
                return false; // EOF
            }

            // parse the line
            parseProperty(line);
            return true;
        }

        /**
         * Returns the comment lines that have been read for the last property.
         *
         * @return the comment lines for the last property returned by
         * {@code readProperty()}
         * @since 1.3
         */
        public List<String> getCommentLines()
        {
            return commentLines;
        }

        /**
         * Returns the name of the last read property. This method can be called
         * after {@link #nextProperty()} was invoked and its
         * return value was <b>true</b>.
         *
         * @return the name of the last read property
         * @since 1.3
         */
        public String getPropertyName()
        {
            return propertyName;
        }

        /**
         * Returns the value of the last read property. This method can be
         * called after {@link #nextProperty()} was invoked and
         * its return value was <b>true</b>.
         *
         * @return the value of the last read property
         * @since 1.3
         */
        public String getPropertyValue()
        {
            return propertyValue;
        }

        /**
         * Returns the separator that was used for the last read property. The
         * separator can be stored so that it can later be restored when saving
         * the configuration.
         *
         * @return the separator for the last read property
         * @since 1.7
         */
        public String getPropertySeparator()
        {
            return propertySeparator;
        }

        /**
         * Parses a line read from the properties file. This method is called
         * for each non-comment line read from the source file. Its task is to
         * split the passed in line into the property key and its value. The
         * results of the parse operation can be stored by calling the
         * {@code initPropertyXXX()} methods.
         *
         * @param line the line read from the properties file
         * @since 1.7
         */
        protected void parseProperty(String line)
        {
            String[] property = doParseProperty(line);
            initPropertyName(property[0]);
            initPropertyValue(property[1]);
            initPropertySeparator(property[2]);
        }

        /**
         * Sets the name of the current property. This method can be called by
         * {@code parseProperty()} for storing the results of the parse
         * operation. It also ensures that the property key is correctly
         * escaped.
         *
         * @param name the name of the current property
         * @since 1.7
         */
        protected void initPropertyName(String name)
        {
            propertyName = StringEscapeUtils.unescapeJava(name);
        }

        /**
         * Sets the value of the current property. This method can be called by
         * {@code parseProperty()} for storing the results of the parse
         * operation. It also ensures that the property value is correctly
         * escaped.
         *
         * @param value the value of the current property
         * @since 1.7
         */
        protected void initPropertyValue(String value)
        {
            propertyValue = unescapeJava(value, delimiter);
        }

        /**
         * Sets the separator of the current property. This method can be called
         * by {@code parseProperty()}. It allows the associated layout
         * object to keep track of the property separators. When saving the
         * configuration the separators can be restored.
         *
         * @param value the separator used for the current property
         * @since 1.7
         */
        protected void initPropertySeparator(String value)
        {
            propertySeparator = value;
        }

        /**
         * Checks if the passed in line should be combined with the following.
         * This is true, if the line ends with an odd number of backslashes.
         *
         * @param line the line
         * @return a flag if the lines should be combined
         */
        private static boolean checkCombineLines(String line)
        {
            return countTrailingBS(line) % 2 != 0;
        }

        /**
         * Parse a property line and return the key, the value, and the separator in an array.
         *
         * @param line the line to parse
         * @return an array with the property's key, value, and separator
         */
        private static String[] doParseProperty(String line)
        {
			int index = line.indexOf('=');

			if (index == -1) {
				return _emptyResult;
			}

			return new String[] {line.substring(0, index).trim(), line.substring(index + 1).trim(), "="};
        }

		private static final String[] _emptyResult = {"", "", ""};
    } // class PropertiesReader

    /**
     * This class is used to write properties lines. The most important method
     * is {@code writeProperty(String, Object, boolean)}, which is called
     * during a save operation for each property found in the configuration.
     */
    public static class PropertiesWriter extends FilterWriter
    {
        /** Constant for the initial size when creating a string buffer. */
        private static final int BUF_SIZE = 8;

        /** The delimiter for multi-valued properties.*/
        private char delimiter;

        /** The separator to be used for the current property. */
        private String currentSeparator;

        /** The global separator. If set, it overrides the current separator.*/
        private String globalSeparator;

        /** The line separator.*/
        private String lineSeparator;

        /**
         * Constructor.
         *
         * @param writer a Writer object providing the underlying stream
         * @param delimiter the delimiter character for multi-valued properties
         */
        public PropertiesWriter(Writer writer, char delimiter)
        {
            super(writer);
            this.delimiter = delimiter;
        }

        /**
         * Returns the current property separator.
         *
         * @return the current property separator
         * @since 1.7
         */
        public String getCurrentSeparator()
        {
            return currentSeparator;
        }

        /**
         * Sets the current property separator. This separator is used when
         * writing the next property.
         *
         * @param currentSeparator the current property separator
         * @since 1.7
         */
        public void setCurrentSeparator(String currentSeparator)
        {
            this.currentSeparator = currentSeparator;
        }

        /**
         * Returns the global property separator.
         *
         * @return the global property separator
         * @since 1.7
         */
        public String getGlobalSeparator()
        {
            return globalSeparator;
        }

        /**
         * Sets the global property separator. This separator corresponds to the
         * {@code globalSeparator} property of
         * {@link PropertiesConfigurationLayout}. It defines the separator to be
         * used for all properties. If it is undefined, the current separator is
         * used.
         *
         * @param globalSeparator the global property separator
         * @since 1.7
         */
        public void setGlobalSeparator(String globalSeparator)
        {
            this.globalSeparator = globalSeparator;
        }

        /**
         * Returns the line separator.
         *
         * @return the line separator
         * @since 1.7
         */
        public String getLineSeparator()
        {
            return (lineSeparator != null) ? lineSeparator : LINE_SEPARATOR;
        }

        /**
         * Sets the line separator. Each line written by this writer is
         * terminated with this separator. If not set, the platform-specific
         * line separator is used.
         *
         * @param lineSeparator the line separator to be used
         * @since 1.7
         */
        public void setLineSeparator(String lineSeparator)
        {
            this.lineSeparator = lineSeparator;
        }

        /**
         * Write a property.
         *
         * @param key the key of the property
         * @param value the value of the property
         *
         * @throws IOException if an I/O error occurs
         */
        public void writeProperty(String key, Object value) throws IOException
        {
            writeProperty(key, value, false);
        }

        /**
         * Write a property.
         *
         * @param key The key of the property
         * @param values The array of values of the property
         *
         * @throws IOException if an I/O error occurs
         */
        public void writeProperty(String key, List<?> values) throws IOException
        {
            for (int i = 0; i < values.size(); i++)
            {
                writeProperty(key, values.get(i));
            }
        }

        /**
         * Writes the given property and its value. If the value happens to be a
         * list, the {@code forceSingleLine} flag is evaluated. If it is
         * set, all values are written on a single line using the list delimiter
         * as separator.
         *
         * @param key the property key
         * @param value the property value
         * @param forceSingleLine the &quot;force single line&quot; flag
         * @throws IOException if an error occurs
         * @since 1.3
         */
        public void writeProperty(String key, Object value,
                boolean forceSingleLine) throws IOException
        {
            String v;

            if (value instanceof List)
            {
                List<?> values = (List<?>) value;
                if (forceSingleLine)
                {
                    v = makeSingleLineValue(values);
                }
                else
                {
                    writeProperty(key, values);
                    return;
                }
            }
            else
            {
                v = escapeValue(value, false);
            }

            write(escapeKey(key));
            write(fetchSeparator(key, value));
            write(v);

            writeln(null);
        }

        /**
         * Write a comment.
         *
         * @param comment the comment to write
         * @throws IOException if an I/O error occurs
         */
        public void writeComment(String comment) throws IOException
        {
            writeln("# " + comment);
        }

        /**
         * Escape the separators in the key.
         *
         * @param key the key
         * @return the escaped key
         * @since 1.2
         */
        private String escapeKey(String key)
        {
            StringBuilder newkey = new StringBuilder();

            for (int i = 0; i < key.length(); i++)
            {
                char c = key.charAt(i);

                if (ArrayUtils.contains(SEPARATORS, c) || ArrayUtils.contains(WHITE_SPACE, c))
                {
                    // escape the separator
                    newkey.append('\\');
                    newkey.append(c);
                }
                else
                {
                    newkey.append(c);
                }
            }

            return newkey.toString();
        }

        /**
         * Escapes the given property value. Delimiter characters in the value
         * will be escaped.
         *
         * @param value the property value
         * @param inList a flag whether the value is part of a list
         * @return the escaped property value
         * @since 1.3
         */
        private String escapeValue(Object value, boolean inList)
        {
            String escapedValue = handleBackslashs(value, inList);
            if (delimiter != 0)
            {
                escapedValue = StringUtils.replace(escapedValue, String.valueOf(delimiter), ESCAPE + delimiter);
            }
            return escapedValue;
        }

        /**
         * Performs the escaping of backslashes in the specified properties
         * value. Because a double backslash is used to escape the escape
         * character of a list delimiter, double backslashes also have to be
         * escaped if the property is part of a (single line) list. Then, in all
         * cases each backslash has to be doubled in order to produce a valid
         * properties file.
         *
         * @param value the value to be escaped
         * @param inList a flag whether the value is part of a list
         * @return the value with escaped backslashes as string
         */
        private String handleBackslashs(Object value, boolean inList)
        {
            String strValue = String.valueOf(value);

            if (inList && strValue.indexOf(DOUBLE_ESC) >= 0)
            {
                char esc = ESCAPE.charAt(0);
                StringBuilder buf = new StringBuilder(strValue.length() + BUF_SIZE);
                for (int i = 0; i < strValue.length(); i++)
                {
                    if (strValue.charAt(i) == esc && i < strValue.length() - 1
                            && strValue.charAt(i + 1) == esc)
                    {
                        buf.append(DOUBLE_ESC).append(DOUBLE_ESC);
                        i++;
                    }
                    else
                    {
                        buf.append(strValue.charAt(i));
                    }
                }

                strValue = buf.toString();
            }

            return StringEscapeUtils.escapeJava(strValue);
        }

        /**
         * Transforms a list of values into a single line value.
         *
         * @param values the list with the values
         * @return a string with the single line value (can be <b>null</b>)
         * @since 1.3
         */
        private String makeSingleLineValue(List<?> values)
        {
            if (!values.isEmpty())
            {
                Iterator<?> it = values.iterator();
                String lastValue = escapeValue(it.next(), true);
                StringBuilder buf = new StringBuilder(lastValue);
                while (it.hasNext())
                {
                    // if the last value ended with an escape character, it has
                    // to be escaped itself; otherwise the list delimiter will
                    // be escaped
                    if (lastValue.endsWith(ESCAPE) && (countTrailingBS(lastValue) / 2) % 2 != 0)
                    {
                        buf.append(ESCAPE).append(ESCAPE);
                    }
                    buf.append(delimiter);
                    lastValue = escapeValue(it.next(), true);
                    buf.append(lastValue);
                }
                return buf.toString();
            }
            else
            {
                return null;
            }
        }

        /**
         * Helper method for writing a line with the platform specific line
         * ending.
         *
         * @param s the content of the line (may be <b>null</b>)
         * @throws IOException if an error occurs
         * @since 1.3
         */
        public void writeln(String s) throws IOException
        {
            if (s != null)
            {
                write(s);
            }
            write(getLineSeparator());
        }

        /**
         * Returns the separator to be used for the given property. This method
         * is called by {@code writeProperty()}. The string returned here
         * is used as separator between the property key and its value. Per
         * default the method checks whether a global separator is set. If this
         * is the case, it is returned. Otherwise the separator returned by
         * {@code getCurrentSeparator()} is used, which was set by the
         * associated layout object. Derived classes may implement a different
         * strategy for defining the separator.
         *
         * @param key the property key
         * @param value the value
         * @return the separator to be used
         * @since 1.7
         */
        protected String fetchSeparator(String key, Object value)
        {
            return (getGlobalSeparator() != null) ? getGlobalSeparator()
                    : getCurrentSeparator();
        }
    } // class PropertiesWriter

    /**
     * <p>
     * Definition of an interface that allows customization of read and write
     * operations.
     * </p>
     * <p>
     * For reading and writing properties files the inner classes
     * {@code PropertiesReader} and {@code PropertiesWriter} are used.
     * This interface defines factory methods for creating both a
     * {@code PropertiesReader} and a {@code PropertiesWriter}. An
     * object implementing this interface can be passed to the
     * {@code setIOFactory()} method of
     * {@code PropertiesConfiguration}. Every time the configuration is
     * read or written the {@code IOFactory} is asked to create the
     * appropriate reader or writer object. This provides an opportunity to
     * inject custom reader or writer implementations.
     * </p>
     *
     * @since 1.7
     */
    public interface IOFactory
    {
        /**
         * Creates a {@code PropertiesReader} for reading a properties
         * file. This method is called whenever the
         * {@code PropertiesConfiguration} is loaded. The reader returned
         * by this method is then used for parsing the properties file.
         *
         * @param in the underlying reader (of the properties file)
         * @param delimiter the delimiter character for list parsing
         * @return the {@code PropertiesReader} for loading the
         *         configuration
         */
        PropertiesReader createPropertiesReader(Reader in, char delimiter);

        /**
         * Creates a {@code PropertiesWriter} for writing a properties
         * file. This method is called before the
         * {@code PropertiesConfiguration} is saved. The writer returned by
         * this method is then used for writing the properties file.
         *
         * @param out the underlying writer (to the properties file)
         * @param delimiter the delimiter character for list parsing
         * @return the {@code PropertiesWriter} for saving the
         *         configuration
         */
        PropertiesWriter createPropertiesWriter(Writer out, char delimiter);
    }

    /**
     * <p>
     * A default implementation of the {@code IOFactory} interface.
     * </p>
     * <p>
     * This class implements the {@code createXXXX()} methods defined by
     * the {@code IOFactory} interface in a way that the default objects
     * (i.e. {@code PropertiesReader} and {@code PropertiesWriter} are
     * returned. Customizing either the reader or the writer (or both) can be
     * done by extending this class and overriding the corresponding
     * {@code createXXXX()} method.
     * </p>
     *
     * @since 1.7
     */
    public static class DefaultIOFactory implements IOFactory
    {
        public PropertiesReader createPropertiesReader(Reader in, char delimiter)
        {
            return new PropertiesReader(in, delimiter);
        }

        public PropertiesWriter createPropertiesWriter(Writer out,
                char delimiter)
        {
            return new PropertiesWriter(out, delimiter);
        }
    }

    /**
     * <p>Unescapes any Java literals found in the {@code String} to a
     * {@code Writer}.</p> This is a slightly modified version of the
     * StringEscapeUtils.unescapeJava() function in commons-lang that doesn't
     * drop escaped separators (i.e '\,').
     *
     * @param str  the {@code String} to unescape, may be null
     * @param delimiter the delimiter for multi-valued properties
     * @return the processed string
     * @throws IllegalArgumentException if the Writer is {@code null}
     */
    protected static String unescapeJava(String str, char delimiter)
    {
        if (str == null)
        {
            return null;
        }
        int sz = str.length();
        StringBuilder out = new StringBuilder(sz);
        StringBuilder unicode = new StringBuilder(UNICODE_LEN);
        boolean hadSlash = false;
        boolean inUnicode = false;
        for (int i = 0; i < sz; i++)
        {
            char ch = str.charAt(i);
            if (inUnicode)
            {
                // if in unicode, then we're reading unicode
                // values in somehow
                unicode.append(ch);
                if (unicode.length() == UNICODE_LEN)
                {
                    // unicode now contains the four hex digits
                    // which represents our unicode character
                    try
                    {
                        int value = Integer.parseInt(unicode.toString(), HEX_RADIX);
                        out.append((char) value);
                        unicode.setLength(0);
                        inUnicode = false;
                        hadSlash = false;
                    }
                    catch (NumberFormatException nfe)
                    {
                        throw new ConfigurationRuntimeException("Unable to parse unicode value: " + unicode, nfe);
                    }
                }
                continue;
            }

            if (hadSlash)
            {
                // handle an escaped value
                hadSlash = false;

                if (ch == '\\')
                {
                    out.append('\\');
                }
                else if (ch == '\'')
                {
                    out.append('\'');
                }
                else if (ch == '\"')
                {
                    out.append('"');
                }
                else if (ch == 'r')
                {
                    out.append('\r');
                }
                else if (ch == 'f')
                {
                    out.append('\f');
                }
                else if (ch == 't')
                {
                    out.append('\t');
                }
                else if (ch == 'n')
                {
                    out.append('\n');
                }
                else if (ch == 'b')
                {
                    out.append('\b');
                }
                else if (ch == delimiter)
                {
                    out.append('\\');
                    out.append(delimiter);
                }
                else if (ch == 'u')
                {
                    // uh-oh, we're in unicode country....
                    inUnicode = true;
                }
                else
                {
                    out.append(ch);
                }

                continue;
            }
            else if (ch == '\\')
            {
                hadSlash = true;
                continue;
            }
            out.append(ch);
        }

        if (hadSlash)
        {
            // then we're in the weird case of a \ at the end of the
            // string, let's output it anyway.
            out.append('\\');
        }

        return out.toString();
    }

    /**
     * Helper method for loading an included properties file. This method is
     * called by {@code load()} when an {@code include} property
     * is encountered. It tries to resolve relative file names based on the
     * current base path. If this fails, a resolution based on the location of
     * this properties file is tried.
     *
     * @param fileName the name of the file to load
     * @throws ConfigurationException if loading fails
     */
    private void loadIncludeFile(String fileName) throws ConfigurationException
    {
        throw new ConfigurationException(
            "Property file includes are not allowed: " + fileName);
    }
}
/* @generated */