/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.cache.configuration.PortalCacheManagerConfiguration;
import com.liferay.portal.kernel.cache.PortalCacheManager;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import java.lang.management.ManagementFactory;

import java.util.function.Consumer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Kyle Miho
 */
@RunWith(Arquillian.class)
public class PortalCacheExtenderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@After
	public void tearDown() throws Exception {
		if (_bundle.getState() != Bundle.UNINSTALLED) {
			_bundle.uninstall();
		}
	}

	@Test
	public void testFinderCacheConfigAfterUpdateEntityCacheConfig()
		throws Exception {

		_multiVmXML = _generateXMLContent(
			1,
			new String[] {_CACHE_NAME_MULTI_ENTITY, _CACHE_NAME_MULTI_FINDER},
			1001, 51, true);

		_bundle = _installBundle(_BUNDLE_SYMBOLIC_NAME, _multiVmXML, null);

		_assertCacheConfig(
			PortalCacheManagerNames.MULTI_VM, 1001,
			_CACHE_NAME_MULTI_ENTITY + "1", 51L);
		_assertCacheConfig(
			PortalCacheManagerNames.MULTI_VM, 1001,
			_CACHE_NAME_MULTI_FINDER + "1", 51L);

		_multiVMPortalCacheManager.getPortalCache(
			_CACHE_NAME_MULTI_FINDER + "1");

		Bundle overridingBundle = null;

		String multiVmXMLUpdated = _generateXMLContent(
			1, new String[] {_CACHE_NAME_MULTI_ENTITY}, 2001, 101, true);

		try {
			overridingBundle = _installBundle(
				_BUNDLE_SYMBOLIC_NAME.concat(".updated"), multiVmXMLUpdated,
				null);

			_assertCacheConfig(
				PortalCacheManagerNames.MULTI_VM, 2001,
				_CACHE_NAME_MULTI_ENTITY + "1", 101L);
			_assertCacheConfig(
				PortalCacheManagerNames.MULTI_VM, 1001,
				_CACHE_NAME_MULTI_FINDER + "1", 51L);
		}
		finally {
			if ((overridingBundle != null) &&
				(overridingBundle.getState() != Bundle.UNINSTALLED)) {

				overridingBundle.uninstall();
			}
		}
	}

	@Test
	public void testRecreateMultiVmConfig() throws Exception {
		_multiVmXML = _generateXMLContent(
			12, new String[] {_CACHE_NAME_MULTI}, 1001, 51, true);

		for (int i = 10; i <= 12; i++) {
			_multiVmXML = StringUtil.replace(
				_multiVmXML, _CACHE_NAME_MULTI + i,
				_CACHE_NAME_MULTI_FINDER + (i - 1));
		}

		_multiVmXML = StringUtil.replaceFirst(
			_multiVmXML, _CACHE_NAME_MULTI + "9",
			_CACHE_NAME_MULTI_ENTITY + "9");

		_bundle = _installBundle(_BUNDLE_SYMBOLIC_NAME, _multiVmXML, null);

		_assertCacheConfig(
			PortalCacheManagerNames.MULTI_VM, 1001,
			_CACHE_NAME_MULTI_FINDER + "9", 51L);

		Bundle overridingBundle = null;

		try {
			overridingBundle = _installBundle(
				_BUNDLE_SYMBOLIC_NAME.concat(".updated"), _multiVmXML, null);

			_assertCacheConfig(
				PortalCacheManagerNames.MULTI_VM, 1001,
				_CACHE_NAME_MULTI_FINDER + "9", 51L);
		}
		finally {
			if ((overridingBundle != null) &&
				(overridingBundle.getState() != Bundle.UNINSTALLED)) {

				overridingBundle.uninstall();
			}
		}
	}

	@Test
	public void testRequireSerializationConfig() throws Exception {
		String defaultConfigCacheName = RandomTestUtil.randomString();
		String offHeapConfigCacheName = RandomTestUtil.randomString();

		_multiVmXML = _generateXMLContent(
			sb -> {
				sb.append("<cache alias=\"");
				sb.append(defaultConfigCacheName);
				sb.append("\"><key-type>java.io.Serializable</key-type>");
				sb.append("<value-type>java.io.Serializable</value-type>");
				sb.append("<heap>1000</heap></cache>");

				sb.append("<cache alias=\"");
				sb.append(offHeapConfigCacheName);
				sb.append("\"><key-type>java.io.Serializable</key-type>");
				sb.append("<value-type>java.io.Serializable</value-type>");
				sb.append("<resources><heap>1000</heap><offheap unit=\"MB\">");
				sb.append("10</offheap></resources></cache>");
			});

		_bundle = _installBundle(_BUNDLE_SYMBOLIC_NAME, _multiVmXML, null);

		_assertRequireSerialization(defaultConfigCacheName, false);
		_assertRequireSerialization(offHeapConfigCacheName, true);
	}

	@Test
	public void testUpdateConfig() throws Exception {
		_multiVmXML = _generateXMLContent(
			1, new String[] {_CACHE_NAME_MULTI}, 1001, 51, true);
		_singleVmXML = _generateXMLContent(
			1, new String[] {_CACHE_NAME_SINGLE}, 1001, 51, false);

		_bundle = _installBundle(
			_BUNDLE_SYMBOLIC_NAME, _multiVmXML, _singleVmXML);

		_assertCacheConfig(
			PortalCacheManagerNames.MULTI_VM, 1001, _CACHE_NAME_MULTI + "1",
			51L);
		_assertCacheConfig(
			PortalCacheManagerNames.SINGLE_VM, 1001, _CACHE_NAME_SINGLE + "1",
			51L);

		Bundle overridingBundle = null;

		String multiVmXMLUpdated = _generateXMLContent(
			1, new String[] {_CACHE_NAME_MULTI}, 2001, 101, true);
		String singleVmXMLUpdated = _generateXMLContent(
			1, new String[] {_CACHE_NAME_SINGLE}, 2001, 101, false);

		try {
			overridingBundle = _installBundle(
				_BUNDLE_SYMBOLIC_NAME.concat(".updated"), multiVmXMLUpdated,
				singleVmXMLUpdated);

			_assertCacheConfig(
				PortalCacheManagerNames.MULTI_VM, 2001, _CACHE_NAME_MULTI + "1",
				101L);
			_assertCacheConfig(
				PortalCacheManagerNames.SINGLE_VM, 2001,
				_CACHE_NAME_SINGLE + "1", 101L);
		}
		finally {
			if ((overridingBundle != null) &&
				(overridingBundle.getState() != Bundle.UNINSTALLED)) {

				overridingBundle.uninstall();
			}
		}
	}

	@Test
	public void testUpdateConfigByExtFile() throws Exception {
		_multiVmXML = _generateXMLContent(
			1, new String[] {_CACHE_NAME_MULTI}, 1001, 51, true);
		_singleVmXML = _generateXMLContent(
			1, new String[] {_CACHE_NAME_SINGLE}, 1001, 51, false);

		_bundle = _installBundle(
			_BUNDLE_SYMBOLIC_NAME, _multiVmXML, _singleVmXML);

		_assertCacheConfig(
			PortalCacheManagerNames.MULTI_VM, 1001, _CACHE_NAME_MULTI + "1",
			51L);
		_assertCacheConfig(
			PortalCacheManagerNames.SINGLE_VM, 1001, _CACHE_NAME_SINGLE + "1",
			51L);

		_bundle.stop();

		File ehcacheFolder = new File(
			PropsUtil.get(PropsKeys.MODULE_FRAMEWORK_BASE_DIR) + "/ehcache/");

		try {
			_file.write(
				new File(
					ehcacheFolder, _BUNDLE_SYMBOLIC_NAME + "-multi-vm-ext.xml"),
				_generateXMLContent(
					1, new String[] {_CACHE_NAME_MULTI}, 2001, 101, true));

			_file.write(
				new File(
					ehcacheFolder,
					_BUNDLE_SYMBOLIC_NAME + "-single-vm-ext.xml"),
				_generateXMLContent(
					1, new String[] {_CACHE_NAME_SINGLE}, 2001, 101, false));

			_bundle.start();

			_assertCacheConfig(
				PortalCacheManagerNames.MULTI_VM, 2001, _CACHE_NAME_MULTI + "1",
				101L);
			_assertCacheConfig(
				PortalCacheManagerNames.SINGLE_VM, 2001,
				_CACHE_NAME_SINGLE + "1", 101L);
		}
		finally {
			_file.deltree(ehcacheFolder);
		}
	}

	private void _assertCacheConfig(
			String cacheManagerName, int maxHeapEntries, String name,
			long timeToIdleSeconds)
		throws Exception {

		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

		ObjectName objectName = new ObjectName(
			StringBundler.concat(
				"org.ehcache:type=Cache,CacheManager=", cacheManagerName,
				",name=", name));

		Assert.assertEquals(
			maxHeapEntries + " entries",
			mBeanServer.getAttribute(objectName, "HeapSize"));
		Assert.assertEquals(name, mBeanServer.getAttribute(objectName, "Name"));
		Assert.assertEquals(
			timeToIdleSeconds,
			mBeanServer.getAttribute(objectName, "TimeToIdle"));
	}

	private void _assertRequireSerialization(
		String cacheName, boolean requireSerialization) {

		PortalCacheManagerConfiguration portalCacheManagerConfiguration =
			ReflectionTestUtil.getFieldValue(
				_multiVMPortalCacheManager, "_portalCacheManagerConfiguration");

		Assert.assertEquals(
			requireSerialization,
			ReflectionTestUtil.getFieldValue(
				portalCacheManagerConfiguration.getPortalCacheConfiguration(
					cacheName),
				"_requireSerialization"));
	}

	private InputStream _createBundle(
			String bundleSymbolicName, String multiCacheConfigContent,
			String singleCacheConfigContent)
		throws Exception {

		try (UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
				new UnsyncByteArrayOutputStream()) {

			try (JarOutputStream jarOutputStream = new JarOutputStream(
					unsyncByteArrayOutputStream)) {

				_writeManifest(bundleSymbolicName, "1.0.0", jarOutputStream);

				_writeClass(jarOutputStream);

				if (multiCacheConfigContent != null) {
					_writeResource(
						jarOutputStream, multiCacheConfigContent,
						"META-INF/module-multi-vm.xml");
				}

				if (singleCacheConfigContent != null) {
					_writeResource(
						jarOutputStream, singleCacheConfigContent,
						"META-INF/module-single-vm.xml");
				}
			}

			return new UnsyncByteArrayInputStream(
				unsyncByteArrayOutputStream.unsafeGetByteArray(), 0,
				unsyncByteArrayOutputStream.size());
		}
	}

	private String _generateXMLContent(Consumer<StringBundler> consumer) {
		StringBundler sb = new StringBundler();

		sb.append("<config xmlns=\"http://www.ehcache.org/v3\" ");
		sb.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
		sb.append("xsi:schemaLocation=\"http://www.ehcache.org/v3 ");
		sb.append("https://www.ehcache.org/schema/ehcache-core-3.10.xsd\">");

		consumer.accept(sb);

		sb.append("</config>");

		return sb.toString();
	}

	private String _generateXMLContent(
		int cacheEntries, String[] cacheNames, int maxHeapEntries,
		int timeToIdleSeconds, boolean multiVM) {

		return _generateXMLContent(
			sb -> {
				for (int i = 1; i <= cacheEntries; i++) {
					for (String cacheName : cacheNames) {
						sb.append("<cache alias=\"");
						sb.append(cacheName + i);
						sb.append("\">");

						if (multiVM) {
							sb.append("<key-type>java.io.Serializable");
							sb.append("</key-type><value-type>");
							sb.append("java.io.Serializable</value-type>");
						}
						else {
							sb.append("<key-type>java.io.Serializable");
							sb.append("</key-type><value-type>");
							sb.append("java.lang.Object</value-type>");
						}

						sb.append("<expiry><tti>");
						sb.append(timeToIdleSeconds);
						sb.append("</tti></expiry><heap>");
						sb.append(maxHeapEntries);
						sb.append("</heap></cache>");
					}
				}
			});
	}

	private Bundle _installBundle(
			String bundleSymbolicName, String multiCacheConfigContent,
			String singleCacheConfigContent)
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(PortalCacheExtenderTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		Bundle newBundle = bundleContext.installBundle(
			bundleSymbolicName,
			_createBundle(
				bundleSymbolicName, multiCacheConfigContent,
				singleCacheConfigContent));

		newBundle.start();

		return newBundle;
	}

	private void _writeClass(JarOutputStream jarOutputStream)
		throws IOException {

		String className = PortalCacheExtenderTest.class.getName();

		String path = StringUtil.replace(
			className, CharPool.PERIOD, CharPool.SLASH);

		String resourcePath = path.concat(".class");

		jarOutputStream.putNextEntry(new ZipEntry(resourcePath));

		ClassLoader classLoader =
			PortalCacheExtenderTest.class.getClassLoader();

		StreamUtil.transfer(
			classLoader.getResourceAsStream(resourcePath), jarOutputStream,
			false);

		jarOutputStream.closeEntry();
	}

	private void _writeManifest(
			String bundleSymbolicName, String bundleVersion,
			JarOutputStream jarOutputStream)
		throws IOException {

		Manifest manifest = new Manifest();

		Attributes attributes = manifest.getMainAttributes();

		attributes.putValue(Constants.BUNDLE_MANIFESTVERSION, "2");
		attributes.putValue(Constants.BUNDLE_SYMBOLICNAME, bundleSymbolicName);
		attributes.putValue(Constants.BUNDLE_VERSION, bundleVersion);
		attributes.putValue("Manifest-Version", "1");

		jarOutputStream.putNextEntry(new ZipEntry(JarFile.MANIFEST_NAME));

		manifest.write(jarOutputStream);

		jarOutputStream.closeEntry();
	}

	private void _writeResource(
			JarOutputStream jarOutputStream, String content, String outputPath)
		throws IOException {

		jarOutputStream.putNextEntry(new ZipEntry(outputPath));

		StreamUtil.transfer(
			new UnsyncByteArrayInputStream(content.getBytes()), jarOutputStream,
			false);

		jarOutputStream.closeEntry();
	}

	private static final String _BUNDLE_SYMBOLIC_NAME =
		"com.liferay.portal.cache.internal.test.PortalCacheTestModule";

	private static final String _CACHE_NAME_MULTI = "test.cache.multi";

	private static final String _CACHE_NAME_MULTI_ENTITY =
		EntityCache.class.getName() + StringPool.PERIOD + _CACHE_NAME_MULTI;

	private static final String _CACHE_NAME_MULTI_FINDER =
		FinderCache.class.getName() + StringPool.PERIOD + _CACHE_NAME_MULTI;

	private static final String _CACHE_NAME_SINGLE = "test.cache.single";

	private static Bundle _bundle;
	private static String _multiVmXML;
	private static String _singleVmXML;

	@Inject
	private com.liferay.portal.kernel.util.File _file;

	@Inject(
		filter = "component.name=com.liferay.portal.cache.ehcache.internal.MultiVMEhcachePortalCacheManager"
	)
	private PortalCacheManager<? extends Serializable, ? extends Serializable>
		_multiVMPortalCacheManager;

}