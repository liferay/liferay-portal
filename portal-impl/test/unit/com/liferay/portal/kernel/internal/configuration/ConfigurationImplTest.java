/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.internal.configuration;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.test.rule.NewEnv;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class ConfigurationImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws ClassNotFoundException {
		Class.forName(Initializer.class.getName());
	}

	@NewEnv(type = NewEnv.Type.JVM)
	@NewEnv.Environment(
		variables = {
			"LIFERAY_INCLUDE_MINUS_AND_MINUS_OVERRIDE=a.properties,b.properties",
			"LIFERAY_LIFERAY_PERIOD_HOME=/liferay",
			"LIFERAY_SETUP_PERIOD_WIZARD_PERIOD_ENABLED=false",
			"LIFERAY_INDEX_PERIOD_ON_PERIOD_STARTUP=false",
			"LIFERAY_SETUP_PERIOD_DATABASE_PERIOD_DRIVER_UPPERCASEC_LASS" +
				"_UPPERCASEN_AME_OPENBRACKET_DB2_CLOSEBRACKET_=" +
					"com.ibm.db2.jcc.DB2Driver",
			"LIFERAY_SETUP_PERIOD_DATABASE_PERIOD_JAR_PERIOD_NAME" +
				"_OPENBRACKET_COM_PERIOD_MYSQL_PERIOD_CJ_PERIOD_JDBC_PERIOD_" +
					"_UPPERCASED_RIVER_CLOSEBRACKET_=mysql.jar",
			"LIFERAY_LAYOUT_PERIOD_STATIC_PERIOD_PORTLETS_PERIOD_START" +
				"_PERIOD_COLUMN_DASH_1_OPENBRACKET_USER_CLOSEBRACKET_" +
					"_OPENBRACKET__SLASH_HOME_CLOSEBRACKET_=6"
		}
	)
	@Test
	public void testEnvironmentVariableOverrideMisc() throws IOException {

		// Examples from LPS-72541

		TestResourceClassLoader testResourceClassLoader =
			new TestResourceClassLoader();

		testResourceClassLoader.addPropertiesResource("testName", "");

		ConfigurationImpl configurationImpl = new ConfigurationImpl(
			testResourceClassLoader, "testName");

		Assert.assertEquals("/liferay", configurationImpl.get("liferay.home"));
		Assert.assertEquals(
			"false", configurationImpl.get("setup.wizard.enabled"));
		Assert.assertEquals("false", configurationImpl.get("index.on.startup"));
		Assert.assertEquals(
			"com.ibm.db2.jcc.DB2Driver",
			configurationImpl.get(
				"setup.database.driverClassName", new Filter("db2")));
		Assert.assertEquals(
			"mysql.jar",
			configurationImpl.get(
				"setup.database.jar.name",
				new Filter("com.mysql.cj.jdbc.Driver")));
		Assert.assertEquals(
			"6",
			configurationImpl.get(
				"layout.static.portlets.start.column-1",
				new Filter("user", "/home")));

		// LPS-151913

		Assert.assertArrayEquals(
			new String[0], configurationImpl.getArray("include-and-override"));

		configurationImpl = new ConfigurationImpl(
			testResourceClassLoader, "portal");

		String[] includeAndOverrides = configurationImpl.getArray(
			"include-and-override");

		Assert.assertEquals(
			"a.properties",
			includeAndOverrides[includeAndOverrides.length - 2]);
		Assert.assertEquals(
			"b.properties",
			includeAndOverrides[includeAndOverrides.length - 1]);
	}

	@NewEnv(type = NewEnv.Type.JVM)
	@NewEnv.Environment(variables = "LIFERAY_NAMESPACE_PERIOD_KEY2=valuex")
	@Test
	public void testEnvironmentVariableOverrideProperties() throws IOException {
		TestResourceClassLoader testResourceClassLoader =
			new TestResourceClassLoader();

		testResourceClassLoader.addPropertiesResource(
			"testName", "namespace.key1=value1\nnamespace.key2=value2");

		ConfigurationImpl configurationImpl = new ConfigurationImpl(
			testResourceClassLoader, "testName");

		Properties properties = configurationImpl.getProperties(
			"namespace.", false);

		Assert.assertEquals(2, properties.size());
		Assert.assertEquals("value1", properties.get("namespace.key1"));
		Assert.assertEquals("valuex", properties.get("namespace.key2"));

		properties = configurationImpl.getProperties("namespace.", true);

		Assert.assertEquals(2, properties.size());
		Assert.assertEquals("value1", properties.get("key1"));
		Assert.assertEquals("valuex", properties.get("key2"));
	}

	@NewEnv(type = NewEnv.Type.JVM)
	@NewEnv.Environment(append = false, variables = {})
	@Test
	public void testLoadEmptyProperties() throws Exception {
		TestResourceClassLoader testResourceClassLoader =
			new TestResourceClassLoader();

		testResourceClassLoader.addPropertiesResource(
			ConfigurationImplTest.class.getName(), StringPool.BLANK);

		ConfigurationImpl configurationImpl = new ConfigurationImpl(
			testResourceClassLoader, ConfigurationImplTest.class.getName());

		Properties properties = configurationImpl.getProperties();

		Assert.assertTrue(properties.isEmpty());
	}

	@Test
	public void testMultiValueProperty() throws IOException {
		TestResourceClassLoader testResourceClassLoader =
			new TestResourceClassLoader();

		testResourceClassLoader.addPropertiesResource(
			ConfigurationImplTest.class.getName(),
			"key1=value1,value2\nkey2=value3\nkey2=value4");

		ConfigurationImpl configurationImpl = new ConfigurationImpl(
			testResourceClassLoader, ConfigurationImplTest.class.getName());

		Assert.assertEquals("value1,value2", configurationImpl.get("key1"));
		Assert.assertEquals("value3,value4", configurationImpl.get("key2"));
		Assert.assertArrayEquals(
			new String[] {"value1", "value2"},
			configurationImpl.getArray("key1"));
		Assert.assertArrayEquals(
			new String[] {"value3", "value4"},
			configurationImpl.getArray("key2"));
	}

	@Test
	public void testMultiValuePropertyVariableInterpolation()
		throws IOException {

		TestResourceClassLoader testResourceClassLoader =
			new TestResourceClassLoader();

		testResourceClassLoader.addPropertiesResource(
			ConfigurationImplTest.class.getName(),
			"key1=value1\nkey2=${key1},value2");

		ConfigurationImpl configurationImpl = new ConfigurationImpl(
			testResourceClassLoader, ConfigurationImplTest.class.getName());

		Assert.assertEquals("value1", configurationImpl.get("key1"));

		// This is a limitation of Commons Configuration where it does not
		// resolve variables for multivalue properties with variable
		// interpolation when you get the value as a single line although
		// variables resolve when you get the values as an array

		Assert.assertEquals("${key1},value2", configurationImpl.get("key2"));
		Assert.assertArrayEquals(
			new String[] {"value1", "value2"},
			configurationImpl.getArray("key2"));
	}

	@Test
	public void testPropertyVariableInterpolation() throws IOException {
		TestResourceClassLoader testResourceClassLoader =
			new TestResourceClassLoader();

		testResourceClassLoader.addPropertiesResource(
			ConfigurationImplTest.class.getName(),
			"key1=value1\nkey2=${key1}value2");

		ConfigurationImpl configurationImpl = new ConfigurationImpl(
			testResourceClassLoader, ConfigurationImplTest.class.getName());

		Assert.assertEquals("value1", configurationImpl.get("key1"));
		Assert.assertEquals("value1value2", configurationImpl.get("key2"));
	}

	@Test
	public void testSet() throws Exception {
		TestResourceClassLoader testResourceClassLoader =
			new TestResourceClassLoader();

		testResourceClassLoader.addPropertiesResource(
			ConfigurationImplTest.class.getName(), StringPool.BLANK);

		ConfigurationImpl configurationImpl = new ConfigurationImpl(
			testResourceClassLoader, ConfigurationImplTest.class.getName());

		configurationImpl.set("key", "value1");

		Assert.assertArrayEquals(
			new String[] {"value1"}, configurationImpl.getArray("key"));

		configurationImpl.set("key", "value2");

		Assert.assertArrayEquals(
			new String[] {"value2"}, configurationImpl.getArray("key"));

		configurationImpl.set("key", "value3,value4");

		Assert.assertArrayEquals(
			new String[] {"value3", "value4"},
			configurationImpl.getArray("key"));
	}

	@Test
	public void testSetDoesNotOverrideFilter() throws Exception {
		TestResourceClassLoader testResourceClassLoader =
			new TestResourceClassLoader();

		testResourceClassLoader.addPropertiesResource(
			ConfigurationImplTest.class.getName(),
			"key=value1\nkey[filter]=value2");

		ConfigurationImpl configurationImpl = new ConfigurationImpl(
			testResourceClassLoader, ConfigurationImplTest.class.getName());

		Assert.assertArrayEquals(
			new String[] {"value1"}, configurationImpl.getArray("key"));

		Assert.assertArrayEquals(
			new String[] {"value2"},
			configurationImpl.getArray("key", new Filter("filter")));

		configurationImpl.set("key", "value3,value4");

		Assert.assertArrayEquals(
			new String[] {"value2"},
			configurationImpl.getArray("key", new Filter("filter")));
	}

	@Test
	public void testSetWithFilter() throws Exception {
		TestResourceClassLoader testResourceClassLoader =
			new TestResourceClassLoader();

		testResourceClassLoader.addPropertiesResource(
			ConfigurationImplTest.class.getName(), StringPool.BLANK);

		ConfigurationImpl configurationImpl = new ConfigurationImpl(
			testResourceClassLoader, ConfigurationImplTest.class.getName());

		configurationImpl.set("key", "value1");

		Assert.assertArrayEquals(
			new String[] {"value1"},
			configurationImpl.getArray("key", new Filter("filter")));

		configurationImpl.set("key", "value2,value3");

		Assert.assertArrayEquals(
			new String[] {"value2", "value3"},
			configurationImpl.getArray("key", new Filter("filter")));
	}

	@Test
	public void testSystemPropertyOverrideProperties() throws IOException {
		TestResourceClassLoader testResourceClassLoader =
			new TestResourceClassLoader();

		testResourceClassLoader.addPropertiesResource(
			ConfigurationImplTest.class.getName(),
			"namespace.key1=value1\nnamespace.key2=value2");

		ConfigurationImpl configurationImpl = new ConfigurationImpl(
			testResourceClassLoader, ConfigurationImplTest.class.getName());

		Properties properties = configurationImpl.getProperties(
			"namespace.", false);

		Assert.assertEquals(2, properties.size());
		Assert.assertEquals("value1", properties.get("namespace.key1"));
		Assert.assertEquals("value2", properties.get("namespace.key2"));

		properties = configurationImpl.getProperties("namespace.", true);

		Assert.assertEquals(2, properties.size());
		Assert.assertEquals("value1", properties.get("key1"));
		Assert.assertEquals("value2", properties.get("key2"));

		configurationImpl.clearCache();

		System.setProperty(
			ConfigurationImplTest.class.getName() + ":namespace.key2",
			"valuex");

		try {
			properties = configurationImpl.getProperties("namespace.", false);

			Assert.assertEquals(2, properties.size());
			Assert.assertEquals("value1", properties.get("namespace.key1"));
			Assert.assertEquals("valuex", properties.get("namespace.key2"));

			properties = configurationImpl.getProperties("namespace.", true);

			Assert.assertEquals(2, properties.size());
			Assert.assertEquals("value1", properties.get("key1"));
			Assert.assertEquals("valuex", properties.get("key2"));

			configurationImpl.clearCache();
		}
		finally {
			System.clearProperty(
				ConfigurationImplTest.class.getName() + ":namespace.key2");

			Assert.assertEquals(
				"value1", configurationImpl.get("namespace.key1"));
			Assert.assertEquals(
				"value2", configurationImpl.get("namespace.key2"));
		}
	}

	@Test
	public void testSystemPropertyOverrideSingleValue() throws IOException {
		TestResourceClassLoader testResourceClassLoader =
			new TestResourceClassLoader();

		testResourceClassLoader.addPropertiesResource(
			ConfigurationImplTest.class.getName(), "key1=value1\nkey2=value2");

		ConfigurationImpl configurationImpl = new ConfigurationImpl(
			testResourceClassLoader, ConfigurationImplTest.class.getName());

		Assert.assertEquals("value1", configurationImpl.get("key1"));
		Assert.assertEquals("value2", configurationImpl.get("key2"));

		configurationImpl.clearCache();

		System.setProperty(
			ConfigurationImplTest.class.getName() + ":key2", "valuex");

		try {
			Assert.assertEquals("value1", configurationImpl.get("key1"));
			Assert.assertEquals("valuex", configurationImpl.get("key2"));

			configurationImpl.clearCache();
		}
		finally {
			System.clearProperty(
				ConfigurationImplTest.class.getName() + ":key2");

			Assert.assertEquals("value1", configurationImpl.get("key1"));
			Assert.assertEquals("value2", configurationImpl.get("key2"));
		}
	}

	private static final Map<URL, byte[]> _testURLResources = new HashMap<>();

	private static class Initializer {

		static {
			URL.setURLStreamHandlerFactory(
				new URLStreamHandlerFactory() {

					@Override
					public URLStreamHandler createURLStreamHandler(
						String protocol) {

						if (!protocol.equals("test")) {
							return null;
						}

						return new URLStreamHandler() {

							@Override
							protected URLConnection openConnection(URL url) {
								return new URLConnection(url) {

									@Override
									public void connect() {
									}

									@Override
									public InputStream getInputStream()
										throws IOException {

										byte[] data = _testURLResources.get(
											url);

										if (data == null) {
											throw new IOException(
												"Unable to open " + url);
										}

										return new UnsyncByteArrayInputStream(
											data);
									}

								};
							}

						};
					}

				});
		}

	}

	private static class TestResourceClassLoader extends ClassLoader {

		public void addPropertiesResource(String name, String content)
			throws IOException {

			URL url = new URL("test://" + String.valueOf(UUID.randomUUID()));

			_testURLResources.put(url, content.getBytes(StringPool.UTF8));

			_resources.put(name.concat(".properties"), url);
		}

		@Override
		public URL getResource(String name) {
			URL url = _resources.get(name);

			if (url == null) {
				url = super.getResource(name);
			}

			return url;
		}

		private TestResourceClassLoader() {
			super(ConfigurationImplTest.class.getClassLoader());
		}

		private final Map<String, URL> _resources = new HashMap<>();

	}

}