/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.server.admin.web.internal.production.readiness;

import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.ServerDetector;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.FastDateFormatFactoryImpl;

import java.io.File;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;

import java.nio.file.Files;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Lily Chi
 */
public class ProductionReadinessCheckUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		FastDateFormatFactoryUtil fastDateFormatFactoryUtil =
			new FastDateFormatFactoryUtil();

		fastDateFormatFactoryUtil.setFastDateFormatFactory(
			new FastDateFormatFactoryImpl());
	}

	@Before
	public void setUp() throws Exception {
		_tempDirectory = FileUtil.createTempFolder();
	}

	@After
	public void tearDown() throws Exception {
		if (_tempDirectory != null) {
			FileUtil.deltree(_tempDirectory);
		}
	}

	@Test
	public void testCheckCounterIncrementFail() {
		try (MockedStatic<PropsUtil> propsUtilMockedStatic = Mockito.mockStatic(
				PropsUtil.class)) {

			propsUtilMockedStatic.when(
				() -> PropsUtil.get(PropsKeys.COUNTER_INCREMENT)
			).thenReturn(
				"1000"
			);

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class,
					"_checkCounterIncrement", new Class<?>[0]);

			Assert.assertEquals(
				"1000", productionReadinessResult.getCurrentValue());
			Assert.assertEquals(
				"counter-increment", productionReadinessResult.getKey());
			Assert.assertFalse(productionReadinessResult.isPass());
		}
	}

	@Test
	public void testCheckCounterIncrementPass() {
		try (MockedStatic<PropsUtil> propsUtilMockedStatic = Mockito.mockStatic(
				PropsUtil.class)) {

			propsUtilMockedStatic.when(
				() -> PropsUtil.get(PropsKeys.COUNTER_INCREMENT)
			).thenReturn(
				"2000"
			);

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class,
					"_checkCounterIncrement", new Class<?>[0]);

			Assert.assertEquals(
				"counter-increment", productionReadinessResult.getKey());
			Assert.assertTrue(productionReadinessResult.isPass());
		}
	}

	@Test
	public void testCheckDLImagePreviewDPIFail() throws Exception {
		try (AutoCloseable closeable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "DL_FILE_ENTRY_PREVIEW_DOCUMENT_DPI",
					100)) {

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class,
					"_checkDLImagePreviewDPI", new Class<?>[0]);

			Assert.assertEquals(
				"100", productionReadinessResult.getCurrentValue());
			Assert.assertEquals(
				"dl-image-preview-dpi", productionReadinessResult.getKey());
			Assert.assertFalse(productionReadinessResult.isPass());
		}
	}

	@Test
	public void testCheckDLImagePreviewDPIPass() throws Exception {
		try (AutoCloseable closeable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "DL_FILE_ENTRY_PREVIEW_DOCUMENT_DPI",
					75)) {

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class,
					"_checkDLImagePreviewDPI", new Class<?>[0]);

			Assert.assertEquals(
				"dl-image-preview-dpi", productionReadinessResult.getKey());
			Assert.assertTrue(productionReadinessResult.isPass());
		}
	}

	@Test
	public void testCheckDLPreviewForkingFail() throws Exception {
		try (AutoCloseable closeable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class,
					"DL_FILE_ENTRY_PREVIEW_FORK_PROCESS_ENABLED", false)) {

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class,
					"_checkDLPreviewForking", new Class<?>[0]);

			Assert.assertEquals(
				"dl-preview-forking", productionReadinessResult.getKey());
			Assert.assertFalse(productionReadinessResult.isPass());
		}
	}

	@Test
	public void testCheckDLPreviewForkingPass() throws Exception {
		try (AutoCloseable closeable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class,
					"DL_FILE_ENTRY_PREVIEW_FORK_PROCESS_ENABLED", true)) {

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class,
					"_checkDLPreviewForking", new Class<?>[0]);

			Assert.assertEquals(
				"dl-preview-forking", productionReadinessResult.getKey());
			Assert.assertTrue(productionReadinessResult.isPass());
		}
	}

	@Test
	public void testCheckExplicitGCDisabledFail() {
		try (MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class)) {

			_mockRuntimeInputArguments(
				managementFactoryMockedStatic, Collections.emptyList());

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class,
					"_checkExplicitGCDisabled", new Class<?>[0]);

			Assert.assertEquals(
				"explicit-gc-disabled", productionReadinessResult.getKey());
			Assert.assertFalse(productionReadinessResult.isPass());
			Assert.assertEquals(
				"-XX:+DisableExplicitGC",
				productionReadinessResult.getRecommendedValue());
		}
	}

	@Test
	public void testCheckExplicitGCDisabledPass() {
		try (MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class)) {

			_mockRuntimeInputArguments(
				managementFactoryMockedStatic,
				Collections.singletonList("-XX:+DisableExplicitGC"));

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class,
					"_checkExplicitGCDisabled", new Class<?>[0]);

			Assert.assertEquals(
				"explicit-gc-disabled", productionReadinessResult.getKey());
			Assert.assertTrue(productionReadinessResult.isPass());
		}
	}

	@Test
	public void testCheckFileStoreImplementationFail() throws Exception {
		try (AutoCloseable closeable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "DL_STORE_IMPL",
					"com.liferay.portal.store.db.DBStore")) {

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class,
					"_checkFileStoreImplementation", new Class<?>[0]);

			Assert.assertEquals(
				"file-store-implementation",
				productionReadinessResult.getKey());
			Assert.assertFalse(productionReadinessResult.isPass());
		}
	}

	@Test
	public void testCheckFileStoreImplementationPass() throws Exception {
		String[] validImpls = {
			"com.liferay.portal.store.azure.AzureStore",
			"com.liferay.portal.store.file.system.AdvancedFileSystemStore",
			"com.liferay.portal.store.gcs.GCSStore",
			"com.liferay.portal.store.s3.IBMS3Store",
			"com.liferay.portal.store.s3.S3Store"
		};

		for (String impl : validImpls) {
			try (AutoCloseable closeable =
					ReflectionTestUtil.setFieldValueWithAutoCloseable(
						PropsValues.class, "DL_STORE_IMPL", impl)) {

				ProductionReadinessResult productionReadinessResult =
					ReflectionTestUtil.invoke(
						ProductionReadinessCheckUtil.class,
						"_checkFileStoreImplementation", new Class<?>[0]);

				Assert.assertEquals(
					impl, productionReadinessResult.getCurrentValue());
				Assert.assertTrue(productionReadinessResult.isPass());
			}
		}
	}

	@Test
	public void testCheckGarbageCollectorTypeFail() {
		try (MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class)) {

			GarbageCollectorMXBean garbageCollectorMXBean = Mockito.mock(
				GarbageCollectorMXBean.class);

			Mockito.when(
				garbageCollectorMXBean.getName()
			).thenReturn(
				RandomTestUtil.randomString()
			);

			managementFactoryMockedStatic.when(
				ManagementFactory::getGarbageCollectorMXBeans
			).thenReturn(
				Collections.singletonList(garbageCollectorMXBean)
			);

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class,
					"_checkGarbageCollectorType", new Class<?>[0]);

			Assert.assertEquals(
				"garbage-collector-type", productionReadinessResult.getKey());
			Assert.assertFalse(productionReadinessResult.isPass());
		}
	}

	@Test
	public void testCheckGarbageCollectorTypePass() {
		try (MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class)) {

			GarbageCollectorMXBean garbageCollectorMXBean = Mockito.mock(
				GarbageCollectorMXBean.class);

			Mockito.when(
				garbageCollectorMXBean.getName()
			).thenReturn(
				"G1 Young Generation"
			);

			managementFactoryMockedStatic.when(
				ManagementFactory::getGarbageCollectorMXBeans
			).thenReturn(
				Collections.singletonList(garbageCollectorMXBean)
			);

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class,
					"_checkGarbageCollectorType", new Class<?>[0]);

			Assert.assertEquals(
				"garbage-collector-type", productionReadinessResult.getKey());
			Assert.assertTrue(productionReadinessResult.isPass());
		}
	}

	@Test
	public void testCheckHeapAllocationConsistencyFail() {
		try (MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class)) {

			_mockHeapMemoryUsage(
				managementFactoryMockedStatic,
				_memoryUsage(2L * 1024 * 1024 * 1024, 4L * 1024 * 1024 * 1024));

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class,
					"_checkHeapAllocationConsistency", new Class<?>[0]);

			Assert.assertEquals(
				"heap-allocation-consistency",
				productionReadinessResult.getKey());
			Assert.assertFalse(productionReadinessResult.isPass());
		}
	}

	@Test
	public void testCheckHeapAllocationConsistencyPass() {
		try (MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class)) {

			_mockHeapMemoryUsage(
				managementFactoryMockedStatic,
				_memoryUsage(4L * 1024 * 1024 * 1024, 4L * 1024 * 1024 * 1024));

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class,
					"_checkHeapAllocationConsistency", new Class<?>[0]);

			Assert.assertEquals(
				"heap-allocation-consistency",
				productionReadinessResult.getKey());
			Assert.assertTrue(productionReadinessResult.isPass());
		}
	}

	@Test
	public void testCheckHeapSizeUpperLimitFail() {
		try (MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class)) {

			_mockHeapMemoryUsage(
				managementFactoryMockedStatic,
				_memoryUsage(
					64L * 1024 * 1024 * 1024, 64L * 1024 * 1024 * 1024));

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class,
					"_checkHeapSizeUpperLimit", new Class<?>[0]);

			Assert.assertEquals(
				"heap-size-upper-limit", productionReadinessResult.getKey());
			Assert.assertFalse(productionReadinessResult.isPass());
		}
	}

	@Test
	public void testCheckHeapSizeUpperLimitPass() {
		try (MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class)) {

			_mockHeapMemoryUsage(
				managementFactoryMockedStatic,
				_memoryUsage(
					16L * 1024 * 1024 * 1024, 16L * 1024 * 1024 * 1024));

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class,
					"_checkHeapSizeUpperLimit", new Class<?>[0]);

			Assert.assertEquals(
				"heap-size-upper-limit", productionReadinessResult.getKey());
			Assert.assertTrue(productionReadinessResult.isPass());
		}
	}

	@Test
	public void testCheckHugePagesConfigurationConfiguredPass() {
		try (MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class);
			MockedStatic<FileUtil> fileUtilMockedStatic = Mockito.mockStatic(
				FileUtil.class)) {

			_mockHeapMemoryUsage(
				managementFactoryMockedStatic,
				_memoryUsage(8L * 1024 * 1024 * 1024, 8L * 1024 * 1024 * 1024));

			_mockRuntimeInputArguments(
				managementFactoryMockedStatic,
				Arrays.asList(
					"-XX:+UseLargePages", "-XX:LargePageSizeInBytes=2048k"));

			fileUtilMockedStatic.when(
				() -> FileUtil.read(ArgumentMatchers.any(File.class))
			).thenReturn(
				"Hugepagesize:    2048 kB\n"
			);

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class,
					"_checkHugePagesConfiguration", new Class<?>[0]);

			Assert.assertEquals(
				"huge-pages-configuration", productionReadinessResult.getKey());
			Assert.assertTrue(productionReadinessResult.isPass());
		}
	}

	@Test
	public void testCheckHugePagesConfigurationMissingSizeFail() {
		try (MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class)) {

			_mockHeapMemoryUsage(
				managementFactoryMockedStatic,
				_memoryUsage(8L * 1024 * 1024 * 1024, 8L * 1024 * 1024 * 1024));

			_mockRuntimeInputArguments(
				managementFactoryMockedStatic,
				Collections.singletonList("-XX:+UseLargePages"));

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class,
					"_checkHugePagesConfiguration", new Class<?>[0]);

			Assert.assertEquals(
				"huge-pages-configuration", productionReadinessResult.getKey());
			Assert.assertFalse(productionReadinessResult.isPass());
		}
	}

	@Test
	public void testCheckHugePagesConfigurationNoLargePagesFail() {
		try (MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class)) {

			_mockHeapMemoryUsage(
				managementFactoryMockedStatic,
				_memoryUsage(8L * 1024 * 1024 * 1024, 8L * 1024 * 1024 * 1024));

			_mockRuntimeInputArguments(
				managementFactoryMockedStatic, Collections.emptyList());

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class,
					"_checkHugePagesConfiguration", new Class<?>[0]);

			Assert.assertEquals(
				"huge-pages-configuration", productionReadinessResult.getKey());
			Assert.assertFalse(productionReadinessResult.isPass());
			Assert.assertEquals(
				"-XX:+UseLargePages",
				productionReadinessResult.getRecommendedValue());
		}
	}

	@Test
	public void testCheckHugePagesConfigurationSmallHeapPass() {
		try (MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class)) {

			_mockHeapMemoryUsage(
				managementFactoryMockedStatic,
				_memoryUsage(2L * 1024 * 1024 * 1024, 2L * 1024 * 1024 * 1024));

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class,
					"_checkHugePagesConfiguration", new Class<?>[0]);

			Assert.assertEquals(
				"huge-pages-configuration", productionReadinessResult.getKey());
			Assert.assertTrue(productionReadinessResult.isPass());
		}
	}

	@Test
	public void testCheckJMXConfigurationDisabledFail() {
		try (MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class)) {

			_mockRuntimeInputArguments(
				managementFactoryMockedStatic,
				Collections.singletonList(
					"-Dcom.sun.management.jmxremote.port=9000"));

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class,
					"_checkJMXConfigurationDisabled", new Class<?>[0]);

			String currentValue = productionReadinessResult.getCurrentValue();

			Assert.assertTrue(
				currentValue.contains(
					"-Dcom.sun.management.jmxremote.port=9000"));

			Assert.assertEquals(
				"jmx-configuration-disabled",
				productionReadinessResult.getKey());
			Assert.assertFalse(productionReadinessResult.isPass());
		}
	}

	@Test
	public void testCheckJMXConfigurationDisabledPass() {
		try (MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class)) {

			_mockRuntimeInputArguments(
				managementFactoryMockedStatic, Collections.emptyList());

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class,
					"_checkJMXConfigurationDisabled", new Class<?>[0]);

			Assert.assertEquals(
				"jmx-configuration-disabled",
				productionReadinessResult.getKey());
			Assert.assertTrue(productionReadinessResult.isPass());
		}
	}

	@Test
	public void testCheckJSPEngineSettingsFail() throws Exception {
		File catalinaDirectory = new File(_tempDirectory, "catalina");

		File confDirectory = new File(catalinaDirectory, "conf");

		confDirectory.mkdirs();

		File webXml = new File(confDirectory, "web.xml");

		Files.writeString(webXml.toPath(), "<web-app/>");

		String originalCatalinaBase = System.getProperty("catalina.base");
		String originalCatalinaHome = System.getProperty("catalina.home");

		System.setProperty(
			"catalina.base", catalinaDirectory.getAbsolutePath());

		try (MockedStatic<ServerDetector> serverDetectorMockedStatic =
				Mockito.mockStatic(ServerDetector.class);
			MockedStatic<FileUtil> fileUtilMockedStatic = Mockito.mockStatic(
				FileUtil.class);
			MockedStatic<SAXReaderUtil> saxReaderUtilMockedStatic =
				Mockito.mockStatic(SAXReaderUtil.class)) {

			serverDetectorMockedStatic.when(
				ServerDetector::isTomcat
			).thenReturn(
				true
			);

			fileUtilMockedStatic.when(
				() -> FileUtil.read(ArgumentMatchers.any(File.class))
			).thenReturn(
				"<web-app/>"
			);

			Document document = _mockJspWebXmlDocument("true", "false");

			saxReaderUtilMockedStatic.when(
				() -> SAXReaderUtil.read(ArgumentMatchers.anyString())
			).thenReturn(
				document
			);

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class,
					"_checkJSPEngineSettings", new Class<?>[0]);

			Assert.assertEquals(
				"jsp-engine-settings", productionReadinessResult.getKey());
			Assert.assertFalse(productionReadinessResult.isPass());
		}
		finally {
			_restoreSystemProperty("catalina.base", originalCatalinaBase);
			_restoreSystemProperty("catalina.home", originalCatalinaHome);
		}
	}

	@Test
	public void testCheckJSPEngineSettingsNoCatalinaBase() {
		String originalCatalinaBase = System.getProperty("catalina.base");
		String originalCatalinaHome = System.getProperty("catalina.home");

		System.clearProperty("catalina.base");
		System.clearProperty("catalina.home");

		try (MockedStatic<ServerDetector> serverDetectorMockedStatic =
				Mockito.mockStatic(ServerDetector.class)) {

			serverDetectorMockedStatic.when(
				ServerDetector::isTomcat
			).thenReturn(
				true
			);

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class,
					"_checkJSPEngineSettings", new Class<?>[0]);

			Assert.assertNull(productionReadinessResult);
		}
		finally {
			_restoreSystemProperty("catalina.base", originalCatalinaBase);
			_restoreSystemProperty("catalina.home", originalCatalinaHome);
		}
	}

	@Test
	public void testCheckJSPEngineSettingsNotTomcat() {
		try (MockedStatic<ServerDetector> serverDetectorMockedStatic =
				Mockito.mockStatic(ServerDetector.class)) {

			serverDetectorMockedStatic.when(
				ServerDetector::isTomcat
			).thenReturn(
				false
			);

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class,
					"_checkJSPEngineSettings", new Class<?>[0]);

			Assert.assertNull(productionReadinessResult);
		}
	}

	@Test
	public void testCheckJSPEngineSettingsPass() throws Exception {
		File catalinaDirectory = new File(_tempDirectory, "catalina");

		File confDirectory = new File(catalinaDirectory, "conf");

		confDirectory.mkdirs();

		File webXml = new File(confDirectory, "web.xml");

		Files.writeString(webXml.toPath(), "<web-app/>");

		String originalCatalinaBase = System.getProperty("catalina.base");
		String originalCatalinaHome = System.getProperty("catalina.home");

		System.setProperty(
			"catalina.base", catalinaDirectory.getAbsolutePath());

		try (MockedStatic<ServerDetector> serverDetectorMockedStatic =
				Mockito.mockStatic(ServerDetector.class);
			MockedStatic<FileUtil> fileUtilMockedStatic = Mockito.mockStatic(
				FileUtil.class);
			MockedStatic<SAXReaderUtil> saxReaderUtilMockedStatic =
				Mockito.mockStatic(SAXReaderUtil.class)) {

			serverDetectorMockedStatic.when(
				ServerDetector::isTomcat
			).thenReturn(
				true
			);

			fileUtilMockedStatic.when(
				() -> FileUtil.read(ArgumentMatchers.any(File.class))
			).thenReturn(
				"<web-app/>"
			);

			Document document = _mockJspWebXmlDocument("false", "false");

			saxReaderUtilMockedStatic.when(
				() -> SAXReaderUtil.read(ArgumentMatchers.anyString())
			).thenReturn(
				document
			);

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class,
					"_checkJSPEngineSettings", new Class<?>[0]);

			Assert.assertEquals(
				"jsp-engine-settings", productionReadinessResult.getKey());
			Assert.assertTrue(productionReadinessResult.isPass());
		}
		finally {
			_restoreSystemProperty("catalina.base", originalCatalinaBase);
			_restoreSystemProperty("catalina.home", originalCatalinaHome);
		}
	}

	@Test
	public void testCheckJSPReloadingFail() {
		try (MockedStatic<PropsUtil> propsUtilMockedStatic = Mockito.mockStatic(
				PropsUtil.class)) {

			propsUtilMockedStatic.when(
				() -> PropsUtil.get(PropsKeys.DIRECT_SERVLET_CONTEXT_RELOAD)
			).thenReturn(
				"true"
			);

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class, "_checkJSPReloading",
					new Class<?>[0]);

			Assert.assertEquals(
				"jsp-reloading", productionReadinessResult.getKey());
			Assert.assertFalse(productionReadinessResult.isPass());
		}
	}

	@Test
	public void testCheckJSPReloadingPass() {
		try (MockedStatic<PropsUtil> propsUtilMockedStatic = Mockito.mockStatic(
				PropsUtil.class)) {

			propsUtilMockedStatic.when(
				() -> PropsUtil.get(PropsKeys.DIRECT_SERVLET_CONTEXT_RELOAD)
			).thenReturn(
				"false"
			);

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class, "_checkJSPReloading",
					new Class<?>[0]);

			Assert.assertEquals(
				"jsp-reloading", productionReadinessResult.getKey());
			Assert.assertTrue(productionReadinessResult.isPass());
		}
	}

	@Test
	public void testCheckLocalesBetaFail() throws Exception {
		try (AutoCloseable closeable1 =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "LOCALES_BETA", new String[] {"fr_FR"});
			AutoCloseable closeable2 =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "LOCALES_ENABLED",
					new String[] {"en_US", "fr_FR"})) {

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class, "_checkLocalesBeta",
					new Class<?>[0]);

			Assert.assertEquals(
				"fr_FR", productionReadinessResult.getCurrentValue());
			Assert.assertEquals(
				"locales-beta", productionReadinessResult.getKey());
			Assert.assertFalse(productionReadinessResult.isPass());
		}
	}

	@Test
	public void testCheckLocalesBetaPass() throws Exception {
		try (AutoCloseable closeable1 =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "LOCALES_BETA", new String[] {"fr_FR"});
			AutoCloseable closeable2 =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "LOCALES_ENABLED",
					new String[] {"en_US"})) {

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class, "_checkLocalesBeta",
					new Class<?>[0]);

			Assert.assertEquals(
				"locales-beta", productionReadinessResult.getKey());
			Assert.assertTrue(productionReadinessResult.isPass());
		}
	}

	@Test
	public void testCheckLocalesUnusedFail() throws Exception {
		try (AutoCloseable closeable1 =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "LOCALES",
					new String[] {"en_US", "de_DE"});
			AutoCloseable closeable2 =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "LOCALES_ENABLED",
					new String[] {"en_US"})) {

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class, "_checkLocalesUnused",
					new Class<?>[0]);

			Assert.assertEquals(
				"de_DE", productionReadinessResult.getCurrentValue());
			Assert.assertEquals(
				"locales-unused", productionReadinessResult.getKey());
			Assert.assertFalse(productionReadinessResult.isPass());
		}
	}

	@Test
	public void testCheckLocalesUnusedPass() throws Exception {
		try (AutoCloseable closeable1 =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "LOCALES", new String[] {"en_US"});
			AutoCloseable closeable2 =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "LOCALES_ENABLED",
					new String[] {"en_US"})) {

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class, "_checkLocalesUnused",
					new Class<?>[0]);

			Assert.assertEquals(
				"locales-unused", productionReadinessResult.getKey());
			Assert.assertTrue(productionReadinessResult.isPass());
		}
	}

	@Test
	public void testCheckPasswordEncryption() {
		_testCheckPasswordEncryption("BCRYPT", true);
		_testCheckPasswordEncryption("MD5", false);
		_testCheckPasswordEncryption("PBKDF2WithHmacSHA1/160/1300000", true);
		_testCheckPasswordEncryption("PBKDF2WithHmacSHA1/160/1000", false);
	}

	@Test
	public void testCheckPoolVsThreadSizeFail() throws Exception {
		try (MockedStatic<PropsUtil> propsUtilMockedStatic = Mockito.mockStatic(
				PropsUtil.class);
			MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class)) {

			propsUtilMockedStatic.when(
				() -> PropsUtil.get("jdbc.default.maximumPoolSize")
			).thenReturn(
				"10"
			);

			_mockMBeanServerMaxThreads(managementFactoryMockedStatic, 200);

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class,
					"_checkPoolVsThreadSize", new Class<?>[0]);

			Assert.assertEquals(
				"pool-vs-thread-size", productionReadinessResult.getKey());
			Assert.assertFalse(productionReadinessResult.isPass());
		}
	}

	@Test
	public void testCheckPoolVsThreadSizeNull() throws Exception {
		try (MockedStatic<PropsUtil> propsUtilMockedStatic = Mockito.mockStatic(
				PropsUtil.class);
			MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class)) {

			propsUtilMockedStatic.when(
				() -> PropsUtil.get("jdbc.default.maximumPoolSize")
			).thenReturn(
				"0"
			);

			_mockMBeanServerMaxThreads(managementFactoryMockedStatic, 200);

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class,
					"_checkPoolVsThreadSize", new Class<?>[0]);

			Assert.assertNull(productionReadinessResult);
		}
	}

	@Test
	public void testCheckPoolVsThreadSizePass() throws Exception {
		try (MockedStatic<PropsUtil> propsUtilMockedStatic = Mockito.mockStatic(
				PropsUtil.class);
			MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class)) {

			propsUtilMockedStatic.when(
				() -> PropsUtil.get("jdbc.default.maximumPoolSize")
			).thenReturn(
				"200"
			);

			_mockMBeanServerMaxThreads(managementFactoryMockedStatic, 200);

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class,
					"_checkPoolVsThreadSize", new Class<?>[0]);

			Assert.assertEquals(
				"pool-vs-thread-size", productionReadinessResult.getKey());
			Assert.assertTrue(productionReadinessResult.isPass());
		}
	}

	@Test
	public void testCheckPortalDeveloperPropertiesFail() {
		try (MockedStatic<PropsUtil> propsUtilMockedStatic = Mockito.mockStatic(
				PropsUtil.class)) {

			propsUtilMockedStatic.when(
				() -> PropsUtil.getArray("include-and-override")
			).thenReturn(
				new String[] {"portal-developer.properties"}
			);

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class,
					"_checkPortalDeveloperProperties", new Class<?>[0]);

			Assert.assertEquals(
				"portal-developer-properties",
				productionReadinessResult.getKey());
			Assert.assertFalse(productionReadinessResult.isPass());
		}
	}

	@Test
	public void testCheckPortalDeveloperPropertiesPass() {
		try (MockedStatic<PropsUtil> propsUtilMockedStatic = Mockito.mockStatic(
				PropsUtil.class)) {

			propsUtilMockedStatic.when(
				() -> PropsUtil.getArray("include-and-override")
			).thenReturn(
				new String[0]
			);

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class,
					"_checkPortalDeveloperProperties", new Class<?>[0]);

			Assert.assertEquals(
				"portal-developer-properties",
				productionReadinessResult.getKey());
			Assert.assertTrue(productionReadinessResult.isPass());
		}
	}

	@Test
	public void testCheckPreventDiagnosticOverheadFail() {
		try (MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class)) {

			_mockRuntimeInputArguments(
				managementFactoryMockedStatic,
				Collections.singletonList("-XX:+UnlockDiagnosticVMOptions"));

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class,
					"_checkPreventDiagnosticOverhead", new Class<?>[0]);

			Assert.assertEquals(
				"prevent-diagnostic-overhead",
				productionReadinessResult.getKey());
			Assert.assertFalse(productionReadinessResult.isPass());
		}
	}

	@Test
	public void testCheckPreventDiagnosticOverheadPass() {
		try (MockedStatic<ManagementFactory> managementFactoryMockedStatic =
				Mockito.mockStatic(ManagementFactory.class)) {

			_mockRuntimeInputArguments(
				managementFactoryMockedStatic, Collections.emptyList());

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class,
					"_checkPreventDiagnosticOverhead", new Class<?>[0]);

			Assert.assertEquals(
				"prevent-diagnostic-overhead",
				productionReadinessResult.getKey());
			Assert.assertTrue(productionReadinessResult.isPass());
		}
	}

	@Test
	public void testCheckSidecarDetectionSidecarDetectedFail()
		throws Exception {

		SearchEngineInformation searchEngineInformation = Mockito.mock(
			SearchEngineInformation.class);

		Mockito.when(
			searchEngineInformation.getVendorString()
		).thenReturn(
			"Elasticsearch (Sidecar)"
		);

		Snapshot<SearchEngineInformation> snapshot = Mockito.mock(
			Snapshot.class);

		Mockito.when(
			snapshot.get()
		).thenReturn(
			searchEngineInformation
		);

		try (AutoCloseable closeable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					ProductionReadinessCheckUtil.class,
					"_searchEngineInformationSnapshot", snapshot)) {

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class,
					"_checkSidecarDetection", new Class<?>[0]);

			Assert.assertEquals(
				"sidecar-detection", productionReadinessResult.getKey());
			Assert.assertFalse(productionReadinessResult.isPass());
		}
	}

	@Test
	public void testCheckSidecarDetectionSidecarNotDetectedPass()
		throws Exception {

		SearchEngineInformation searchEngineInformation = Mockito.mock(
			SearchEngineInformation.class);

		Mockito.when(
			searchEngineInformation.getVendorString()
		).thenReturn(
			"Elasticsearch"
		);

		Snapshot<SearchEngineInformation> snapshot = Mockito.mock(
			Snapshot.class);

		Mockito.when(
			snapshot.get()
		).thenReturn(
			searchEngineInformation
		);

		try (AutoCloseable closeable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					ProductionReadinessCheckUtil.class,
					"_searchEngineInformationSnapshot", snapshot)) {

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class,
					"_checkSidecarDetection", new Class<?>[0]);

			Assert.assertEquals(
				"sidecar-detection", productionReadinessResult.getKey());
			Assert.assertTrue(productionReadinessResult.isPass());
		}
	}

	@Test
	public void testIsStrongerAlgorithm() {
		_testIsStrongerAlgorithm("BCRYPT", true);
		_testIsStrongerAlgorithm("BCRYPT/12", true);
		_testIsStrongerAlgorithm("MD5", false);
		_testIsStrongerAlgorithm("PBKDF2WithHmacSHA1/160/1300000", true);
		_testIsStrongerAlgorithm("PBKDF2WithHmacSHA1/160/1000", false);
		_testIsStrongerAlgorithm("SCRYPT", true);
		_testIsStrongerAlgorithm(null, false);
	}

	@Test
	public void testParseSize() {
		_testParseSize(1073741824L, "1g");
		_testParseSize(2097152L, "2048k");
		_testParseSize(2097152L, "2m");
		_testParseSize(-1L, null);
		_testParseSize(100L, "100");
	}

	private MemoryUsage _memoryUsage(long init, long max) {
		return new MemoryUsage(init, 0, max, max);
	}

	private void _mockHeapMemoryUsage(
		MockedStatic<ManagementFactory> managementFactoryMockedStatic,
		MemoryUsage memoryUsage) {

		MemoryMXBean memoryMXBean = Mockito.mock(MemoryMXBean.class);

		Mockito.when(
			memoryMXBean.getHeapMemoryUsage()
		).thenReturn(
			memoryUsage
		);

		managementFactoryMockedStatic.when(
			ManagementFactory::getMemoryMXBean
		).thenReturn(
			memoryMXBean
		);
	}

	private Document _mockJspWebXmlDocument(
		String developmentValue, String mappedFileValue) {

		Document document = Mockito.mock(Document.class);
		Element rootElement = Mockito.mock(Element.class);
		Element servletElement = Mockito.mock(Element.class);
		Element developmentParam = Mockito.mock(Element.class);
		Element mappedFileParam = Mockito.mock(Element.class);

		Mockito.when(
			developmentParam.elementText("param-name")
		).thenReturn(
			"development"
		);

		Mockito.when(
			developmentParam.elementText("param-value")
		).thenReturn(
			developmentValue
		);

		Mockito.when(
			mappedFileParam.elementText("param-name")
		).thenReturn(
			"mappedFile"
		);

		Mockito.when(
			mappedFileParam.elementText("param-value")
		).thenReturn(
			mappedFileValue
		);

		Mockito.when(
			servletElement.elementText("servlet-name")
		).thenReturn(
			"jsp"
		);

		Mockito.when(
			servletElement.elements("init-param")
		).thenReturn(
			Arrays.asList(developmentParam, mappedFileParam)
		);

		Mockito.when(
			rootElement.elements("servlet")
		).thenReturn(
			Collections.singletonList(servletElement)
		);

		Mockito.when(
			document.getRootElement()
		).thenReturn(
			rootElement
		);

		return document;
	}

	private void _mockMBeanServerMaxThreads(
			MockedStatic<ManagementFactory> managementFactoryMockedStatic,
			int maxThreads)
		throws Exception {

		MBeanServer mBeanServer = Mockito.mock(MBeanServer.class);

		ObjectName objectName = new ObjectName(
			"Catalina:type=ThreadPool,name=" + RandomTestUtil.randomString());

		Mockito.when(
			mBeanServer.queryNames(
				ArgumentMatchers.any(ObjectName.class),
				ArgumentMatchers.isNull())
		).thenReturn(
			Collections.singleton(objectName)
		);

		Mockito.when(
			mBeanServer.getAttribute(
				ArgumentMatchers.any(ObjectName.class),
				ArgumentMatchers.eq("maxThreads"))
		).thenReturn(
			maxThreads
		);

		managementFactoryMockedStatic.when(
			ManagementFactory::getPlatformMBeanServer
		).thenReturn(
			mBeanServer
		);
	}

	private void _mockRuntimeInputArguments(
		MockedStatic<ManagementFactory> managementFactoryMockedStatic,
		List<String> inputArguments) {

		RuntimeMXBean runtimeMXBean = Mockito.mock(RuntimeMXBean.class);

		Mockito.when(
			runtimeMXBean.getInputArguments()
		).thenReturn(
			inputArguments
		);

		managementFactoryMockedStatic.when(
			ManagementFactory::getRuntimeMXBean
		).thenReturn(
			runtimeMXBean
		);
	}

	private void _restoreSystemProperty(String key, String previousValue) {
		if (previousValue == null) {
			System.clearProperty(key);
		}
		else {
			System.setProperty(key, previousValue);
		}
	}

	private void _testCheckPasswordEncryption(
		String algorithm, boolean expected) {

		try (MockedStatic<PropsUtil> propsUtilMockedStatic = Mockito.mockStatic(
				PropsUtil.class)) {

			propsUtilMockedStatic.when(
				() -> PropsUtil.get(PropsKeys.PASSWORDS_ENCRYPTION_ALGORITHM)
			).thenReturn(
				algorithm
			);

			ProductionReadinessResult productionReadinessResult =
				ReflectionTestUtil.invoke(
					ProductionReadinessCheckUtil.class,
					"_checkPasswordEncryption", new Class<?>[0]);

			Assert.assertEquals(
				"password-encryption", productionReadinessResult.getKey());
			Assert.assertEquals(expected, productionReadinessResult.isPass());
		}
	}

	private void _testIsStrongerAlgorithm(String algorithm, boolean expected) {
		Assert.assertEquals(
			expected,
			ReflectionTestUtil.invoke(
				ProductionReadinessCheckUtil.class, "_isStrongerAlgorithm",
				new Class<?>[] {String.class}, algorithm));
	}

	private void _testParseSize(long expected, String sizeString) {
		Assert.assertEquals(
			expected,
			(long)ReflectionTestUtil.invoke(
				ProductionReadinessCheckUtil.class, "_parseSize",
				new Class<?>[] {String.class}, sizeString));
	}

	private File _tempDirectory;

}