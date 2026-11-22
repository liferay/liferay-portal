/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.test.util.BundleContextTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.language.LanguageResources;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author Kevin Lee
 */
@RunWith(Arquillian.class)
public class LanguageResourcesExtenderTest {

	@Test
	public void testRegistration() throws Exception {
		String value = RandomTestUtil.randomString();

		Bundle bundle = _installResourceBundle(
			"test.bundle",
			HashMapBuilder.put(
				StringPool.BLANK, "language-key-1=" + value
			).put(
				String.valueOf(LocaleUtil.ENGLISH), "about=" + value
			).build(),
			null, 1, null, false, null);

		Assert.assertEquals(
			"About", LanguageResources.getMessage(LocaleUtil.ENGLISH, "about"));
		Assert.assertEquals(
			"Enabled",
			LanguageResources.getMessage(LocaleUtil.ENGLISH, "enabled"));
		Assert.assertNull(
			LanguageResources.getMessage(LocaleUtil.ENGLISH, "language-key-1"));

		try {
			bundle.start();

			Assert.assertEquals(
				value,
				LanguageResources.getMessage(LocaleUtil.ENGLISH, "about"));
			Assert.assertEquals(
				"Enabled",
				LanguageResources.getMessage(LocaleUtil.ENGLISH, "enabled"));
			Assert.assertEquals(
				value,
				LanguageResources.getMessage(
					LocaleUtil.ENGLISH, "language-key-1"));
		}
		finally {
			bundle.uninstall();
		}
	}

	@Test
	public void testRegistrationAggregate() throws Exception {
		String value1 = RandomTestUtil.randomString();
		String value2 = RandomTestUtil.randomString();
		String value3 = RandomTestUtil.randomString();

		Bundle bundle1 = _installResourceBundle(
			"test.bundle1",
			HashMapBuilder.put(
				StringPool.BLANK,
				StringBundler.concat(
					"language-key-1=", value1, "\nshared-language-key=", value1)
			).put(
				String.valueOf(LocaleUtil.ENGLISH), "about=" + value1
			).build(),
			"test-bundle1", 1, false, true, null);
		Bundle bundle2 = _installResourceBundle(
			"test.bundle2",
			HashMapBuilder.put(
				StringPool.BLANK,
				StringBundler.concat(
					"language-key-2=", value2, "\nshared-language-key=", value2)
			).put(
				String.valueOf(LocaleUtil.ENGLISH), "about=" + value2
			).build(),
			"test-bundle2", 1, true, true, null);
		Bundle bundle3 = _installResourceBundle(
			"test.bundle3",
			Collections.singletonMap(
				StringPool.BLANK, "language-key-3=" + value3),
			"test-bundle3", 1, true, true,
			"liferay.language.resources;filter:=\"(bundle.symbolic.name=" +
				"test.bundle2)\",liferay.language.resources;filter:=\"(" +
					"bundle.symbolic.name=test.bundle1)\"",
			_getProvideCapabilityAggregate(
				"test.bundle3", "test-bundle3", 2, true,
				new String[] {"test.bundle2", "test.bundle1", "test.bundle3"}));

		Assert.assertNull(
			ResourceBundleLoaderUtil.
				getResourceBundleLoaderByBundleSymbolicName("test.bundle1"));
		Assert.assertNull(
			ResourceBundleLoaderUtil.
				getResourceBundleLoaderByBundleSymbolicName("test.bundle2"));
		Assert.assertNull(
			ResourceBundleLoaderUtil.
				getResourceBundleLoaderByBundleSymbolicName("test.bundle3"));

		try {
			bundle1.start();
			bundle2.start();
			bundle3.start();

			ResourceBundleLoader resourceBundleLoader1 =
				ResourceBundleLoaderUtil.
					getResourceBundleLoaderByBundleSymbolicName("test.bundle1");

			ResourceBundle resourceBundle1 =
				resourceBundleLoader1.loadResourceBundle(LocaleUtil.ENGLISH);

			Assert.assertEquals(value1, resourceBundle1.getString("about"));
			Assert.assertEquals(
				value1, resourceBundle1.getString("language-key-1"));
			Assert.assertFalse(resourceBundle1.containsKey("language-key-2"));
			Assert.assertFalse(resourceBundle1.containsKey("language-key-3"));
			Assert.assertEquals(
				value1, resourceBundle1.getString("shared-language-key"));

			ResourceBundleLoader resourceBundleLoader2 =
				ResourceBundleLoaderUtil.
					getResourceBundleLoaderByBundleSymbolicName("test.bundle2");

			ResourceBundle resourceBundle2 =
				resourceBundleLoader2.loadResourceBundle(LocaleUtil.ENGLISH);

			Assert.assertEquals(value2, resourceBundle2.getString("about"));
			Assert.assertFalse(resourceBundle2.containsKey("language-key-1"));
			Assert.assertEquals(
				value2, resourceBundle2.getString("language-key-2"));
			Assert.assertFalse(resourceBundle2.containsKey("language-key-3"));
			Assert.assertEquals(
				value2, resourceBundle2.getString("shared-language-key"));

			ResourceBundleLoader resourceBundleLoader3 =
				ResourceBundleLoaderUtil.
					getResourceBundleLoaderByBundleSymbolicName("test.bundle3");

			ResourceBundle resourceBundle3 =
				resourceBundleLoader3.loadResourceBundle(LocaleUtil.ENGLISH);

			Assert.assertEquals(value2, resourceBundle3.getString("about"));
			Assert.assertEquals(
				value1, resourceBundle3.getString("language-key-1"));
			Assert.assertEquals(
				value2, resourceBundle3.getString("language-key-2"));
			Assert.assertEquals(
				value3, resourceBundle3.getString("language-key-3"));
			Assert.assertEquals(
				value2, resourceBundle3.getString("shared-language-key"));
		}
		finally {
			bundle1.uninstall();
			bundle2.uninstall();
			bundle3.uninstall();
		}
	}

	@Test
	public void testRegistrationBothHeaders() throws Exception {
		String bundleSymbolicName = "test.bundle";
		String servletContextName = "test-bundle";

		String value = RandomTestUtil.randomString();

		Bundle bundle = _installResourceBundle(
			"test.bundle",
			HashMapBuilder.put(
				StringPool.BLANK, "language-key-1=" + value
			).put(
				String.valueOf(LocaleUtil.ENGLISH), "about=" + value
			).build(),
			null, 1, null, false, null,
			_getProvideCapabilityLegacy(
				bundleSymbolicName, servletContextName, 1, false, true));

		Assert.assertEquals(
			"About", LanguageResources.getMessage(LocaleUtil.ENGLISH, "about"));
		Assert.assertNull(
			LanguageResources.getMessage(LocaleUtil.ENGLISH, "language-key-1"));

		Assert.assertNull(
			ResourceBundleLoaderUtil.
				getResourceBundleLoaderByBundleSymbolicName(
					bundleSymbolicName));
		Assert.assertNull(
			ResourceBundleLoaderUtil.
				getResourceBundleLoaderByServletContextName(
					servletContextName));

		try {
			bundle.start();

			Assert.assertEquals(
				value,
				LanguageResources.getMessage(LocaleUtil.ENGLISH, "about"));
			Assert.assertEquals(
				value,
				LanguageResources.getMessage(
					LocaleUtil.ENGLISH, "language-key-1"));

			Assert.assertNull(
				ResourceBundleLoaderUtil.
					getResourceBundleLoaderByBundleSymbolicName(
						bundleSymbolicName));
			Assert.assertNull(
				ResourceBundleLoaderUtil.
					getResourceBundleLoaderByServletContextName(
						servletContextName));
		}
		finally {
			bundle.uninstall();
		}
	}

	@Test
	public void testRegistrationBothHeadersModuleOnly() throws Exception {
		String bundleSymbolicName = "test.bundle";
		String servletContextName = "test-bundle";

		String value = RandomTestUtil.randomString();

		Bundle bundle = _installResourceBundle(
			bundleSymbolicName,
			HashMapBuilder.put(
				StringPool.BLANK, "language-key-1=" + value
			).put(
				String.valueOf(LocaleUtil.ENGLISH), "about=" + value
			).build(),
			servletContextName, 1, false, true, null,
			_getProvideCapabilityLegacy(
				bundleSymbolicName, servletContextName, 2, true, true));

		try {
			bundle.start();

			BundleContext bundleContext =
				BundleContextTestUtil.getBundleContext(
					LanguageResourcesExtenderTest.class);

			List<ServiceReference<ResourceBundleLoader>> serviceReferences =
				new ArrayList<>(
					bundleContext.getServiceReferences(
						ResourceBundleLoader.class,
						StringBundler.concat(
							"(&(bundle.symbolic.name=", bundleSymbolicName,
							")(servlet.context.name=", servletContextName,
							"))")));

			Assert.assertEquals(
				serviceReferences.toString(), 1, serviceReferences.size());

			ServiceReference<ResourceBundleLoader> serviceReference =
				serviceReferences.get(0);

			Assert.assertEquals(
				"false",
				serviceReference.getProperty("exclude.portal.resources"));
			Assert.assertEquals(
				1, serviceReference.getProperty("service.ranking"));
		}
		finally {
			bundle.uninstall();
		}
	}

	@Test
	public void testRegistrationExcludePortalResources() throws Exception {
		String value = RandomTestUtil.randomString();

		Bundle bundle = _installResourceBundle(
			"test.bundle",
			HashMapBuilder.put(
				StringPool.BLANK, "language-key-1=" + value
			).put(
				String.valueOf(LocaleUtil.ENGLISH), "about=" + value
			).build(),
			"test-bundle", 1, true, true, null);

		Assert.assertNull(
			ResourceBundleLoaderUtil.
				getResourceBundleLoaderByBundleSymbolicName("test.bundle"));

		try {
			bundle.start();

			ResourceBundleLoader resourceBundleLoader =
				ResourceBundleLoaderUtil.
					getResourceBundleLoaderByBundleSymbolicName("test.bundle");

			Assert.assertNotNull(resourceBundleLoader);

			ResourceBundle resourceBundle =
				resourceBundleLoader.loadResourceBundle(LocaleUtil.ENGLISH);

			Assert.assertNotNull(resourceBundle);

			Assert.assertEquals(value, resourceBundle.getString("about"));
			Assert.assertFalse(resourceBundle.containsKey("enabled"));
			Assert.assertEquals(
				value, resourceBundle.getString("language-key-1"));
		}
		finally {
			bundle.uninstall();
		}
	}

	@Test
	public void testRegistrationLegacy() throws Exception {
		String bundleSymbolicName = "test.bundle";
		String servletContextName = "test-bundle";

		String value = RandomTestUtil.randomString();

		Bundle bundle = _installResourceBundle(
			bundleSymbolicName,
			HashMapBuilder.put(
				String.valueOf(LocaleUtil.ENGLISH), "about=" + value
			).put(
				StringPool.BLANK, "language-key-1=" + value
			).build(),
			servletContextName, 1, false, true, null);

		Assert.assertNull(
			ResourceBundleLoaderUtil.
				getResourceBundleLoaderByBundleSymbolicName(
					bundleSymbolicName));
		Assert.assertNull(
			ResourceBundleLoaderUtil.
				getResourceBundleLoaderByServletContextName(
					servletContextName));

		try {
			bundle.start();

			Assert.assertNotNull(
				ResourceBundleLoaderUtil.
					getResourceBundleLoaderByBundleSymbolicName(
						bundleSymbolicName));
			Assert.assertNotNull(
				ResourceBundleLoaderUtil.
					getResourceBundleLoaderByServletContextName(
						servletContextName));
		}
		finally {
			bundle.uninstall();
		}
	}

	@Test
	public void testRegistrationModuleOnly() throws Exception {
		String bundleSymbolicName = "test.bundle";
		String servletContextName = "test-bundle";

		String value = RandomTestUtil.randomString();

		Bundle bundle = _installResourceBundle(
			bundleSymbolicName,
			HashMapBuilder.put(
				StringPool.BLANK, "language-key-1=" + value
			).put(
				String.valueOf(LocaleUtil.ENGLISH), "about=" + value
			).build(),
			servletContextName, 1, false, true, null);

		Assert.assertEquals(
			"About", LanguageResources.getMessage(LocaleUtil.ENGLISH, "about"));
		Assert.assertNull(
			LanguageResources.getMessage(LocaleUtil.ENGLISH, "language-key-1"));

		Assert.assertNull(
			ResourceBundleLoaderUtil.
				getResourceBundleLoaderByBundleSymbolicName(
					bundleSymbolicName));
		Assert.assertNull(
			ResourceBundleLoaderUtil.
				getResourceBundleLoaderByServletContextName(
					servletContextName));

		try {
			bundle.start();

			Assert.assertEquals(
				"About",
				LanguageResources.getMessage(LocaleUtil.ENGLISH, "about"));
			Assert.assertNull(
				LanguageResources.getMessage(
					LocaleUtil.ENGLISH, "language-key-1"));

			ResourceBundleLoader resourceBundleLoader1 =
				ResourceBundleLoaderUtil.
					getResourceBundleLoaderByBundleSymbolicName(
						bundleSymbolicName);
			ResourceBundleLoader resourceBundleLoader2 =
				ResourceBundleLoaderUtil.
					getResourceBundleLoaderByServletContextName(
						servletContextName);

			Assert.assertNotNull(resourceBundleLoader1);
			Assert.assertNotNull(resourceBundleLoader2);

			ResourceBundle resourceBundle1 =
				resourceBundleLoader1.loadResourceBundle(LocaleUtil.ENGLISH);
			ResourceBundle resourceBundle2 =
				resourceBundleLoader2.loadResourceBundle(LocaleUtil.ENGLISH);

			Assert.assertNotNull(resourceBundle1);
			Assert.assertNotNull(resourceBundle2);

			Assert.assertEquals(value, resourceBundle1.getString("about"));
			Assert.assertEquals(
				"Enabled", resourceBundle1.getString("enabled"));
			Assert.assertEquals(
				value, resourceBundle1.getString("language-key-1"));
			Assert.assertEquals(value, resourceBundle2.getString("about"));
			Assert.assertEquals(
				"Enabled", resourceBundle2.getString("enabled"));
			Assert.assertEquals(
				value, resourceBundle2.getString("language-key-1"));
		}
		finally {
			bundle.uninstall();
		}
	}

	@Test
	public void testRegistrationModuleOnlyMultiple() throws Exception {
		String value1 = RandomTestUtil.randomString();
		String value2 = RandomTestUtil.randomString();

		Bundle bundle1 = _installResourceBundle(
			"test.bundle1",
			HashMapBuilder.put(
				StringPool.BLANK,
				StringBundler.concat(
					"language-key-1=", value1, "\nshared-language-key=", value1)
			).put(
				String.valueOf(LocaleUtil.ENGLISH), "about=" + value1
			).build(),
			"test-bundle1", 1, false, true, null);
		Bundle bundle2 = _installResourceBundle(
			"test.bundle2",
			HashMapBuilder.put(
				StringPool.BLANK,
				StringBundler.concat(
					"language-key-2=", value2, "\nshared-language-key=", value2)
			).put(
				String.valueOf(LocaleUtil.ENGLISH), "about=" + value2
			).build(),
			"test-bundle2", 1, false, true, null);

		Assert.assertNull(
			ResourceBundleLoaderUtil.
				getResourceBundleLoaderByBundleSymbolicName("test.bundle1"));
		Assert.assertNull(
			ResourceBundleLoaderUtil.
				getResourceBundleLoaderByBundleSymbolicName("test.bundle2"));

		try {
			bundle1.start();
			bundle2.start();

			ResourceBundleLoader resourceBundleLoader1 =
				ResourceBundleLoaderUtil.
					getResourceBundleLoaderByBundleSymbolicName("test.bundle1");
			ResourceBundleLoader resourceBundleLoader2 =
				ResourceBundleLoaderUtil.
					getResourceBundleLoaderByBundleSymbolicName("test.bundle2");

			Assert.assertNotNull(resourceBundleLoader1);
			Assert.assertNotNull(resourceBundleLoader2);

			ResourceBundle resourceBundle1 =
				resourceBundleLoader1.loadResourceBundle(LocaleUtil.ENGLISH);
			ResourceBundle resourceBundle2 =
				resourceBundleLoader2.loadResourceBundle(LocaleUtil.ENGLISH);

			Assert.assertNotNull(resourceBundle1);
			Assert.assertNotNull(resourceBundle2);

			Assert.assertEquals(value1, resourceBundle1.getString("about"));
			Assert.assertEquals(
				value1, resourceBundle1.getString("language-key-1"));
			Assert.assertFalse(resourceBundle1.containsKey("language-key-2"));
			Assert.assertEquals(
				value1, resourceBundle1.getString("shared-language-key"));
			Assert.assertEquals(value2, resourceBundle2.getString("about"));
			Assert.assertFalse(resourceBundle2.containsKey("language-key-1"));
			Assert.assertEquals(
				value2, resourceBundle2.getString("language-key-2"));
			Assert.assertEquals(
				value2, resourceBundle2.getString("shared-language-key"));
		}
		finally {
			bundle1.uninstall();
			bundle2.uninstall();
		}
	}

	@Test
	public void testRegistrationServiceRanking() throws Exception {
		String value1 = RandomTestUtil.randomString();
		String value2 = RandomTestUtil.randomString();

		Bundle bundle1 = _installResourceBundle(
			"test.bundle1",
			HashMapBuilder.put(
				StringPool.BLANK,
				StringBundler.concat(
					"language-key-1=", value1, "\nshared-language-key=", value1)
			).put(
				String.valueOf(LocaleUtil.ENGLISH), "about=" + value1
			).build(),
			null, 1, null, false, null);
		Bundle bundle2 = _installResourceBundle(
			"test.bundle2",
			HashMapBuilder.put(
				StringPool.BLANK,
				StringBundler.concat(
					"language-key-2=", value2, "\nshared-language-key=", value2)
			).put(
				String.valueOf(LocaleUtil.ENGLISH), "about=" + value2
			).build(),
			null, 2, null, false, null);

		Assert.assertEquals(
			"About", LanguageResources.getMessage(LocaleUtil.ENGLISH, "about"));
		Assert.assertNull(
			LanguageResources.getMessage(LocaleUtil.ENGLISH, "language-key-1"));
		Assert.assertNull(
			LanguageResources.getMessage(LocaleUtil.ENGLISH, "language-key-2"));
		Assert.assertNull(
			LanguageResources.getMessage(
				LocaleUtil.ENGLISH, "shared-language-key"));

		try {
			bundle1.start();
			bundle2.start();

			Assert.assertEquals(
				value2,
				LanguageResources.getMessage(LocaleUtil.ENGLISH, "about"));
			Assert.assertEquals(
				value1,
				LanguageResources.getMessage(
					LocaleUtil.ENGLISH, "language-key-1"));
			Assert.assertEquals(
				value2,
				LanguageResources.getMessage(
					LocaleUtil.ENGLISH, "language-key-2"));
			Assert.assertEquals(
				value2,
				LanguageResources.getMessage(
					LocaleUtil.ENGLISH, "shared-language-key"));
		}
		finally {
			bundle1.uninstall();
			bundle2.uninstall();
		}
	}

	private String _getProvideCapability(
		String bundleSymbolicName, String servletContextName,
		Integer serviceRanking, Boolean excludePortalResources,
		Boolean moduleOnly) {

		StringBundler sb = new StringBundler(13);

		sb.append("liferay.language.resources;bundle.symbolic.name=\"");
		sb.append(bundleSymbolicName);
		sb.append("\"");

		if (excludePortalResources != null) {
			sb.append(";exclude.portal.resources=");
			sb.append(excludePortalResources);
		}

		if (moduleOnly != null) {
			sb.append(";module.only=");
			sb.append(moduleOnly);
		}

		sb.append(";resource.bundle.base.name=\"content.Language\"");

		if (serviceRanking != null) {
			sb.append(";service.ranking=");
			sb.append(serviceRanking);
		}

		if (servletContextName != null) {
			sb.append(";servlet.context.name=\"");
			sb.append(servletContextName);
			sb.append("\"");
		}

		return sb.toString();
	}

	private String _getProvideCapabilityAggregate(
		String bundleSymbolicName, String servletContextName,
		int serviceRanking, boolean excludePortalResources,
		String[] aggregateResourceBundles) {

		StringBundler sb = new StringBundler(11);

		sb.append(
			_getProvideCapability(
				bundleSymbolicName, servletContextName, serviceRanking,
				excludePortalResources, true));

		if (aggregateResourceBundles.length > 0) {
			sb.append(";resource.bundle.aggregate=\"");

			for (int i = 0; i < aggregateResourceBundles.length; i++) {
				if (i > 0) {
					sb.append(",");
				}

				sb.append("(bundle.symbolic.name=");
				sb.append(aggregateResourceBundles[i]);
				sb.append(")");
			}

			sb.append("\"");
		}

		return sb.toString();
	}

	private String _getProvideCapabilityLegacy(
		String bundleSymbolicName, String servletContextName,
		Integer serviceRanking, Boolean excludePortalResources,
		Boolean moduleOnly) {

		return StringUtil.replace(
			_getProvideCapability(
				bundleSymbolicName, servletContextName, serviceRanking,
				excludePortalResources, moduleOnly),
			"liferay.language.resources", "liferay.resource.bundle");
	}

	private Bundle _installResourceBundle(
			String bundleSymbolicName, Map<String, String> languageProperties,
			String servletContextName, Integer serviceRanking,
			Boolean excludePortalResources, Boolean moduleOnly,
			String requireCapabilities, String... extraProvideCapabilities)
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(
			LanguageResourcesExtenderTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		try (UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
				new UnsyncByteArrayOutputStream()) {

			try (JarOutputStream jarOutputStream = new JarOutputStream(
					unsyncByteArrayOutputStream)) {

				_writeManifest(
					jarOutputStream, bundleSymbolicName,
					ArrayUtil.append(
						extraProvideCapabilities,
						_getProvideCapability(
							bundleSymbolicName, servletContextName,
							serviceRanking, excludePortalResources,
							moduleOnly)),
					requireCapabilities);

				for (Map.Entry<String, String> entry :
						languageProperties.entrySet()) {

					String fileName = "Language.properties";

					if (Validator.isNotNull(entry.getKey())) {
						fileName = "Language_" + entry.getKey() + ".properties";
					}

					jarOutputStream.putNextEntry(
						new ZipEntry("content/" + fileName));

					String fileContent = entry.getValue();

					StreamUtil.transfer(
						new UnsyncByteArrayInputStream(
							fileContent.getBytes(StandardCharsets.UTF_8)),
						jarOutputStream, false);

					jarOutputStream.closeEntry();
				}
			}

			return bundleContext.installBundle(
				bundleSymbolicName,
				new UnsyncByteArrayInputStream(
					unsyncByteArrayOutputStream.unsafeGetByteArray(), 0,
					unsyncByteArrayOutputStream.size()));
		}
	}

	private void _writeManifest(
			JarOutputStream jarOutputStream, String bundleSymbolicName,
			String[] provideCapabilities, String requireCapabilities)
		throws Exception {

		Manifest manifest = new Manifest();

		Attributes attributes = manifest.getMainAttributes();

		attributes.putValue(Constants.BUNDLE_MANIFESTVERSION, "2");
		attributes.putValue(Constants.BUNDLE_SYMBOLICNAME, bundleSymbolicName);
		attributes.putValue(Constants.BUNDLE_VERSION, "1.0.0");
		attributes.putValue("Manifest-Version", "2");

		if (provideCapabilities != null) {
			attributes.putValue(
				Constants.PROVIDE_CAPABILITY,
				StringUtil.merge(provideCapabilities, StringPool.COMMA));
		}

		if (requireCapabilities != null) {
			attributes.putValue(
				Constants.REQUIRE_CAPABILITY, requireCapabilities);
		}

		jarOutputStream.putNextEntry(new ZipEntry(JarFile.MANIFEST_NAME));

		manifest.write(jarOutputStream);

		jarOutputStream.closeEntry();
	}

}