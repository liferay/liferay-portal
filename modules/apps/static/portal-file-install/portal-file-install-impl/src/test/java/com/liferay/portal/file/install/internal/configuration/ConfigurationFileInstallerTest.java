/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.file.install.internal.configuration;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.File;

import java.nio.file.Files;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Shuyang Zhou
 */
public class ConfigurationFileInstallerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testTransformURL() throws Exception {
		ConfigurationFileInstaller configurationFileInstaller =
			new ConfigurationFileInstaller(
				_configurationAdmin, StringPool.UTF8);

		String factoryPid = RandomTestUtil.randomString();

		String pid1 = factoryPid + "~" + RandomTestUtil.randomString();

		String value1 = _transformURL(configurationFileInstaller, pid1);

		String pid2 = factoryPid + "~*";

		String value2 = _transformURL(configurationFileInstaller, pid2);

		Assert.assertEquals(value1, _getValue(pid1));
		Assert.assertEquals(value2, _getValue(pid2));

		String pid3 = factoryPid + "~(*)\\";

		String value3 = _transformURL(configurationFileInstaller, pid3);

		Assert.assertEquals(value3, _getValue(pid3));
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private Object _getValue(String pid) {
		Configuration configuration = _configurations.get(pid);

		Dictionary<String, Object> properties = configuration.getProperties();

		return properties.get("key");
	}

	private String _transformURL(
			ConfigurationFileInstaller configurationFileInstaller, String pid)
		throws Exception {

		File file = temporaryFolder.newFile(pid + ".config");

		String value = RandomTestUtil.randomString();

		Files.writeString(file.toPath(), "key=\"" + value + "\"");

		configurationFileInstaller.transformURL(file);

		return value;
	}

	private final ConfigurationAdmin _configurationAdmin =
		new ConfigurationAdmin() {

			@Override
			public Configuration createFactoryConfiguration(String factoryPid) {
				throw new UnsupportedOperationException();
			}

			@Override
			public Configuration createFactoryConfiguration(
				String factoryPid, String location) {

				throw new UnsupportedOperationException();
			}

			@Override
			public Configuration getConfiguration(String pid) {
				throw new UnsupportedOperationException();
			}

			@Override
			public Configuration getConfiguration(String pid, String location) {
				throw new UnsupportedOperationException();
			}

			@Override
			public Configuration getFactoryConfiguration(
				String factoryPid, String name) {

				throw new UnsupportedOperationException();
			}

			@Override
			public Configuration getFactoryConfiguration(
				String factoryPid, String name, String location) {

				return _configurations.computeIfAbsent(
					factoryPid + "~" + name, pid -> new TestConfiguration());
			}

			@Override
			public Configuration[] listConfigurations(String filterString)
				throws InvalidSyntaxException {

				Filter filter = FrameworkUtil.createFilter(filterString);

				List<Configuration> configurations = new ArrayList<>();

				for (Configuration configuration : _configurations.values()) {
					if (!filter.match(configuration.getProperties())) {
						continue;
					}

					configurations.add(configuration);
				}

				if (configurations.isEmpty()) {
					return null;
				}

				return configurations.toArray(new Configuration[0]);
			}

		};

	private final Map<String, Configuration> _configurations = new HashMap<>();

	private static class TestConfiguration implements Configuration {

		@Override
		public void addAttributes(
			ConfigurationAttribute... configurationAttributes) {

			throw new UnsupportedOperationException();
		}

		@Override
		public void delete() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Set<ConfigurationAttribute> getAttributes() {
			return Collections.emptySet();
		}

		@Override
		public String getBundleLocation() {
			throw new UnsupportedOperationException();
		}

		@Override
		public long getChangeCount() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getFactoryPid() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getPid() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Dictionary<String, Object> getProcessedProperties(
			ServiceReference<?> serviceReference) {

			throw new UnsupportedOperationException();
		}

		@Override
		public Dictionary<String, Object> getProperties() {
			return _properties;
		}

		@Override
		public void removeAttributes(
			ConfigurationAttribute... configurationAttributes) {

			throw new UnsupportedOperationException();
		}

		@Override
		public void setBundleLocation(String location) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void update() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void update(Dictionary<String, ?> properties) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean updateIfDifferent(Dictionary<String, ?> properties) {
			_properties = HashMapDictionaryBuilder.<String, Object>putAll(
				properties
			).build();

			return true;
		}

		private Dictionary<String, Object> _properties;

	}

}