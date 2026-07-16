/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.exportimport.kernel.lar.BasePortletDataHandler;
import com.liferay.exportimport.kernel.lar.DataLevel;
import com.liferay.exportimport.kernel.lar.ExportImportHelperUtil;
import com.liferay.exportimport.kernel.lar.ManifestSummary;
import com.liferay.exportimport.kernel.lar.MissingReference;
import com.liferay.exportimport.kernel.lar.MissingReferences;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactoryUtil;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.test.util.TestUserIdStrategy;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.repository.capabilities.ThumbnailCapability;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipReaderFactory;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactory;
import com.liferay.portal.model.impl.PortletImpl;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.GenericPortlet;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Zsolt Berentey
 * @author Péter Borkuti
 * @author Balázs Sáfrány-Kovalik
 */
@RunWith(Arquillian.class)
public class ExportImportHelperUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_liveGroup = GroupTestUtil.addGroup();
		_stagingGroup = GroupTestUtil.addGroup();
	}

	@Test
	public void testGetDataSiteAndInstanceLevelPortlets() throws Exception {
		List<Portlet> portlets =
			ExportImportHelperUtil.getDataSiteAndInstanceLevelPortlets(
				TestPropsValues.getCompanyId());

		for (Portlet portlet : portlets) {
			PortletDataHandler portletDataHandler =
				portlet.getPortletDataHandlerInstance();

			DataLevel portletDataLevel = portletDataHandler.getDataLevel();

			Assert.assertTrue(!portletDataLevel.equals(DataLevel.PORTAL));
		}
	}

	@Test
	public void testGetDataSiteAndInstanceLevelPortletsRank() throws Exception {
		List<Portlet> portlets =
			ExportImportHelperUtil.getDataSiteAndInstanceLevelPortlets(
				TestPropsValues.getCompanyId());

		Integer previousRank = null;

		for (Portlet portlet : portlets) {
			PortletDataHandler portletDataHandler =
				portlet.getPortletDataHandlerInstance();

			int actualRank = portletDataHandler.getRank();

			if (previousRank != null) {
				Assert.assertTrue(
					"Portlets should be in ascending order by rank",
					previousRank <= actualRank);
			}

			previousRank = actualRank;
		}
	}

	@Test
	@TestInfo("LPD-74703")
	public void testGetDataSiteLevelPortlet() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			ExportImportHelperUtilTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		long companyId1 = RandomTestUtil.randomLong();
		long companyId2 = RandomTestUtil.randomLong();

		String className = RandomTestUtil.randomString();

		BasePortletDataHandler portletDataHandler1 = new TestPortletDataHandler(
			new String[] {className}, true, DataLevel.SITE);
		BasePortletDataHandler portletDataHandler2 = new TestPortletDataHandler(
			new String[] {className}, false, DataLevel.SITE);

		String portletId1 = RandomTestUtil.randomString();
		String portletId2 = RandomTestUtil.randomString();

		try (SafeCloseable safeCloseable1 = _registerWithSafeCloseable(
				bundleContext, List.of(companyId1), portletDataHandler1,
				portletId1);
			SafeCloseable safeCloseable2 = _registerWithSafeCloseable(
				bundleContext, List.of(companyId1),
				new TestPortletDataHandler(
					new String[] {RandomTestUtil.randomString()}, true,
					DataLevel.SITE),
				RandomTestUtil.randomString());
			SafeCloseable safeCloseable3 = _registerWithSafeCloseable(
				bundleContext, List.of(companyId2, RandomTestUtil.randomLong()),
				portletDataHandler2, portletId2);
			SafeCloseable safeCloseable4 = _registerWithSafeCloseable(
				bundleContext, List.of(companyId2, RandomTestUtil.randomLong()),
				new TestPortletDataHandler(
					new String[] {className}, true, DataLevel.SITE),
				RandomTestUtil.randomString());
			SafeCloseable safeCloseable5 = _registerWithSafeCloseable(
				bundleContext, List.of(companyId2, RandomTestUtil.randomLong()),
				new TestPortletDataHandler(
					new String[] {className}, true, DataLevel.SITE),
				RandomTestUtil.randomString());
			SafeCloseable safeCloseable6 = _registerWithSafeCloseable(
				bundleContext, List.of(companyId2, RandomTestUtil.randomLong()),
				new TestPortletDataHandler(null, false, DataLevel.SITE),
				RandomTestUtil.randomString());
			SafeCloseable safeCloseable7 = _registerWithSafeCloseable(
				bundleContext, List.of(companyId2),
				new TestPortletDataHandler(
					new String[] {className}, false,
					DataLevel.PORTLET_INSTANCE),
				RandomTestUtil.randomString());
			SafeCloseable safeCloseable8 = _registerWithSafeCloseable(
				bundleContext, List.of(companyId2),
				new TestPortletDataHandler(
					new String[] {RandomTestUtil.randomString()}, false,
					DataLevel.SITE),
				RandomTestUtil.randomString());
			SafeCloseable safeCloseable9 = _registerWithSafeCloseable(
				bundleContext,
				List.of(companyId1, RandomTestUtil.randomLong(), companyId2),
				null, RandomTestUtil.randomString())) {

			Assert.assertNotNull(
				_getDataSiteLevelPortlet(
					className, companyId1, false,
					portlet ->
						(portlet != null) &&
						Objects.equals(
							portletId1, portlet.getRootPortletId()) &&
						Objects.equals(
							portletDataHandler1,
							portlet.getPortletDataHandlerInstance())));
			Assert.assertNotNull(
				_getDataSiteLevelPortlet(
					className, companyId2, true,
					portlet ->
						(portlet != null) &&
						Objects.equals(
							portletId2, portlet.getRootPortletId()) &&
						Objects.equals(
							portletDataHandler2,
							portlet.getPortletDataHandlerInstance())));
			Assert.assertNull(
				_getDataSiteLevelPortlet(
					RandomTestUtil.randomString(), companyId1, false,
					Objects::isNull));
			Assert.assertNull(
				_getDataSiteLevelPortlet(
					RandomTestUtil.randomString(), companyId2, true,
					Objects::isNull));
		}
	}

	@Test
	public void testGetDataSiteLevelPortlets() throws Exception {
		List<Portlet> portlets =
			ExportImportHelperUtil.getDataSiteLevelPortlets(
				TestPropsValues.getCompanyId());

		for (Portlet portlet : portlets) {
			PortletDataHandler portletDataHandler =
				portlet.getPortletDataHandlerInstance();

			DataLevel portletDataLevel = portletDataHandler.getDataLevel();

			Assert.assertTrue(
				!(portletDataLevel.equals(DataLevel.PORTAL) ||
				  portletDataLevel.equals(DataLevel.PORTLET_INSTANCE)));
		}
	}

	@Test
	public void testGetDataSiteLevelPortletsRank() throws Exception {
		List<Portlet> portlets =
			ExportImportHelperUtil.getDataSiteLevelPortlets(
				TestPropsValues.getCompanyId());

		Integer previousRank = null;

		for (Portlet portlet : portlets) {
			PortletDataHandler portletDataHandler =
				portlet.getPortletDataHandlerInstance();

			int actualRank = portletDataHandler.getRank();

			if (previousRank != null) {
				Assert.assertTrue(previousRank <= actualRank);
			}

			previousRank = actualRank;
		}
	}

	@Test
	public void testGetExportPortletControlsMapAllConfiguration()
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();
		String portletId = "test_portlet";

		ExportImportTestParameterMapBuilder builder =
			new ExportImportTestParameterMapBuilder();

		Map<String, String[]> parameterMap =
			builder.withPortletArchivedSetupAll(
				true
			).withPortletConfiguration(
				false
			).withPortletConfigurationAll(
				true
			).withPortletData(
				true
			).withPortletSetupAll(
				true
			).withPortletUserPreferencesAll(
				true
			).build();

		Map<String, Boolean> actualPortletControlsMap =
			ExportImportHelperUtil.getExportPortletControlsMap(
				companyId, portletId, parameterMap);

		_assertPortletControlsMap(
			actualPortletControlsMap, true, true, false, true, true);
	}

	@Test
	public void testGetExportPortletControlsMapNoConfiguration()
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();
		String portletId = "test_portlet";

		ExportImportTestParameterMapBuilder builder =
			new ExportImportTestParameterMapBuilder();

		Map<String, String[]> parameterMap = builder.withPortletConfiguration(
			false
		).withPortletConfigurationAll(
			false
		).withPortletData(
			false
		).build();

		Map<String, Boolean> actualPortletControlsMap =
			ExportImportHelperUtil.getExportPortletControlsMap(
				companyId, portletId, parameterMap);

		_assertPortletControlsMap(
			actualPortletControlsMap, false, false, false, false, false);
	}

	@Test
	public void testGetExportPortletControlsMapRootConfiguration()
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();
		String portletId = JournalPortletKeys.JOURNAL;

		ExportImportTestParameterMapBuilder builder =
			new ExportImportTestParameterMapBuilder();

		Map<String, String[]> parameterMap =
			builder.withPortletArchivedSetupAll(
				true
			).withPortletConfiguration(
				false
			).withPortletConfigurationAll(
				false
			).withPortletData(
				false
			).withPortletSetupAll(
				true
			).withPortletUserPreferencesAll(
				true
			).build();

		Map<String, Boolean> actualPortletControlsMap =
			ExportImportHelperUtil.getExportPortletControlsMap(
				companyId, portletId, parameterMap);

		_assertPortletControlsMap(
			actualPortletControlsMap, false, false, false, false, false);
	}

	@Test
	public void testGetExportPortletControlsMapRootConfigurationWithPortletConfiguration()
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();

		String portletId = JournalPortletKeys.JOURNAL;

		String rootPortletId = PortletIdCodec.decodePortletName(portletId);

		ExportImportTestParameterMapBuilder builder =
			new ExportImportTestParameterMapBuilder();

		Map<String, String[]> parameterMap =
			builder.withPortletArchivedSetupAll(
				true
			).withPortletConfiguration(
				true
			).withPortletConfigurationAll(
				false
			).withPortletData(
				false
			).withPortletSetupAll(
				true
			).withPortletUserPreferencesAll(
				true
			).build();

		parameterMap.put(
			PortletDataHandlerKeys.PORTLET_ARCHIVED_SETUPS +
				StringPool.UNDERLINE + rootPortletId,
			new String[] {"true"});

		Map<String, Boolean> actualPortletControlsMap =
			ExportImportHelperUtil.getExportPortletControlsMap(
				companyId, portletId, parameterMap);

		_assertPortletControlsMap(
			actualPortletControlsMap, false, false, false, false, false);
	}

	@Test
	public void testGetExportPortletControlsMapRootConfigurationWithPortletConfiguration2()
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();

		String portletId = JournalPortletKeys.JOURNAL;

		String rootPortletId = PortletIdCodec.decodePortletName(portletId);

		ExportImportTestParameterMapBuilder builder =
			new ExportImportTestParameterMapBuilder();

		Map<String, String[]> parameterMap =
			builder.withPortletArchivedSetupAll(
				true
			).withPortletConfiguration(
				true
			).withPortletConfigurationAll(
				false
			).withPortletData(
				false
			).withPortletSetupAll(
				true
			).withPortletUserPreferencesAll(
				true
			).build();

		parameterMap.put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION +
				StringPool.UNDERLINE + rootPortletId,
			new String[] {"true"});
		parameterMap.put(
			PortletDataHandlerKeys.PORTLET_ARCHIVED_SETUPS +
				StringPool.UNDERLINE + rootPortletId,
			new String[] {"true"});

		Map<String, Boolean> actualPortletControlsMap =
			ExportImportHelperUtil.getExportPortletControlsMap(
				companyId, portletId, parameterMap);

		_assertPortletControlsMap(
			actualPortletControlsMap, true, true, false, false, false);
	}

	@Test
	public void testGetImportPortletControlsMapAllConfiguration()
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();
		String portletId = "test_portlet";

		ExportImportTestParameterMapBuilder builder =
			new ExportImportTestParameterMapBuilder();

		Map<String, String[]> parameterMap =
			builder.withPortletArchivedSetupAll(
				true
			).withPortletConfiguration(
				false
			).withPortletConfigurationAll(
				true
			).withPortletData(
				true
			).withPortletSetupAll(
				true
			).withPortletUserPreferencesAll(
				true
			).build();

		Element portletDataElement = null;
		ManifestSummary manifestSummary = null;

		Map<String, Boolean> actualPortletControlsMap =
			ExportImportHelperUtil.getImportPortletControlsMap(
				companyId, portletId, parameterMap, portletDataElement,
				manifestSummary);

		_assertPortletControlsMap(
			actualPortletControlsMap, true, true, false, true, true);
	}

	@Test
	public void testGetImportPortletControlsMapAllConfigurationWithSummary()
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();
		String portletId = "test_portlet";

		ExportImportTestParameterMapBuilder builder =
			new ExportImportTestParameterMapBuilder();

		Map<String, String[]> parameterMap =
			builder.withPortletArchivedSetupAll(
				true
			).withPortletConfiguration(
				false
			).withPortletConfigurationAll(
				true
			).withPortletData(
				true
			).withPortletSetupAll(
				true
			).withPortletUserPreferencesAll(
				true
			).build();

		Element portletDataElement = null;

		Map<String, Boolean> actualPortletControlsMap =
			ExportImportHelperUtil.getImportPortletControlsMap(
				companyId, portletId, parameterMap, portletDataElement,
				new ManifestSummary());

		_assertPortletControlsMap(
			actualPortletControlsMap, false, false, false, false, false);
	}

	@Test
	public void testGetImportPortletControlsMapAllConfigurationWithSummary2()
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();
		String portletId = "test_portlet";

		ExportImportTestParameterMapBuilder builder =
			new ExportImportTestParameterMapBuilder();

		Map<String, String[]> parameterMap =
			builder.withPortletArchivedSetupAll(
				true
			).withPortletConfiguration(
				false
			).withPortletConfigurationAll(
				true
			).withPortletData(
				true
			).withPortletSetupAll(
				true
			).withPortletUserPreferencesAll(
				true
			).build();

		Element portletDataElement = null;
		ManifestSummary manifestSummary = new ManifestSummary();

		Portlet testPortlet = new PortletImpl();

		testPortlet.setPortletId(portletId);

		manifestSummary.addDataPortlet(testPortlet, null);

		Map<String, Boolean> actualPortletControlsMap =
			ExportImportHelperUtil.getImportPortletControlsMap(
				companyId, portletId, parameterMap, portletDataElement,
				manifestSummary);

		_assertPortletControlsMap(
			actualPortletControlsMap, false, false, false, false, false);
	}

	@Test
	public void testGetImportPortletControlsMapAllConfigurationWithSummary3()
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();
		String portletId = JournalPortletKeys.JOURNAL;

		ExportImportTestParameterMapBuilder builder =
			new ExportImportTestParameterMapBuilder();

		Map<String, String[]> parameterMap =
			builder.withPortletArchivedSetupAll(
				true
			).withPortletConfiguration(
				false
			).withPortletConfigurationAll(
				true
			).withPortletData(
				true
			).withPortletSetupAll(
				true
			).withPortletUserPreferencesAll(
				true
			).build();

		Element portletDataElement = null;
		ManifestSummary manifestSummary = new ManifestSummary();

		Portlet testPortlet = new PortletImpl();

		testPortlet.setPortletId(portletId);

		manifestSummary.addDataPortlet(testPortlet, new String[0]);

		Map<String, Boolean> actualPortletControlsMap =
			ExportImportHelperUtil.getImportPortletControlsMap(
				companyId, portletId, parameterMap, portletDataElement,
				manifestSummary);

		_assertPortletControlsMap(
			actualPortletControlsMap, true, true, false, true, true);
	}

	@Test
	public void testGetImportPortletControlsMapNoConfiguration()
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();
		String portletId = "test_portlet";

		ExportImportTestParameterMapBuilder builder =
			new ExportImportTestParameterMapBuilder();

		Map<String, String[]> parameterMap = builder.withPortletConfiguration(
			false
		).withPortletConfigurationAll(
			false
		).withPortletData(
			false
		).build();

		Element portletDataElement = null;
		ManifestSummary manifestSummary = null;

		Map<String, Boolean> actualPortletControlsMap =
			ExportImportHelperUtil.getImportPortletControlsMap(
				companyId, portletId, parameterMap, portletDataElement,
				manifestSummary);

		_assertPortletControlsMap(
			actualPortletControlsMap, false, false, false, false, false);
	}

	@Test
	public void testGetImportPortletControlsMapRootConfiguration()
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();
		String portletId = JournalPortletKeys.JOURNAL;

		ExportImportTestParameterMapBuilder builder =
			new ExportImportTestParameterMapBuilder();

		Map<String, String[]> parameterMap =
			builder.withPortletArchivedSetupAll(
				true
			).withPortletConfiguration(
				false
			).withPortletConfigurationAll(
				false
			).withPortletData(
				false
			).withPortletSetupAll(
				true
			).withPortletUserPreferencesAll(
				true
			).build();

		Element portletDataElement = null;
		ManifestSummary manifestSummary = null;

		Map<String, Boolean> actualPortletControlsMap =
			ExportImportHelperUtil.getImportPortletControlsMap(
				companyId, portletId, parameterMap, portletDataElement,
				manifestSummary);

		_assertPortletControlsMap(
			actualPortletControlsMap, false, false, false, false, false);
	}

	@Test
	public void testGetImportPortletControlsMapRootConfigurationWithManifest()
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();
		String portletId = JournalPortletKeys.JOURNAL;

		ExportImportTestParameterMapBuilder builder =
			new ExportImportTestParameterMapBuilder();

		Map<String, String[]> parameterMap =
			builder.withPortletArchivedSetupAll(
				true
			).withPortletConfiguration(
				false
			).withPortletConfigurationAll(
				false
			).withPortletData(
				false
			).withPortletSetupAll(
				true
			).withPortletUserPreferencesAll(
				true
			).build();

		Element portletDataElement = null;

		Map<String, Boolean> actualPortletControlsMap =
			ExportImportHelperUtil.getImportPortletControlsMap(
				companyId, portletId, parameterMap, portletDataElement,
				new ManifestSummary());

		_assertPortletControlsMap(
			actualPortletControlsMap, false, false, false, false, false);
	}

	@Test
	public void testGetImportPortletControlsMapRootConfigurationWithManifest2()
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();
		String portletId = JournalPortletKeys.JOURNAL;

		ExportImportTestParameterMapBuilder builder =
			new ExportImportTestParameterMapBuilder();

		Map<String, String[]> parameterMap =
			builder.withPortletArchivedSetupAll(
				true
			).withPortletConfiguration(
				false
			).withPortletConfigurationAll(
				false
			).withPortletData(
				false
			).withPortletSetupAll(
				true
			).withPortletUserPreferencesAll(
				true
			).build();

		Element portletDataElement = null;
		ManifestSummary manifestSummary = new ManifestSummary();

		Portlet testPortlet = new PortletImpl();

		testPortlet.setPortletId(portletId);

		manifestSummary.addDataPortlet(testPortlet, null);

		Map<String, Boolean> actualPortletControlsMap =
			ExportImportHelperUtil.getImportPortletControlsMap(
				companyId, portletId, parameterMap, portletDataElement,
				manifestSummary);

		_assertPortletControlsMap(
			actualPortletControlsMap, false, false, false, false, false);
	}

	@Test
	public void testGetImportPortletControlsMapRootConfigurationWithManifest3()
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();
		String portletId = JournalPortletKeys.JOURNAL;

		ExportImportTestParameterMapBuilder builder =
			new ExportImportTestParameterMapBuilder();

		Map<String, String[]> parameterMap =
			builder.withPortletArchivedSetupAll(
				true
			).withPortletConfiguration(
				false
			).withPortletConfigurationAll(
				false
			).withPortletData(
				false
			).withPortletSetupAll(
				true
			).withPortletUserPreferencesAll(
				true
			).build();

		Element portletDataElement = null;
		ManifestSummary manifestSummary = new ManifestSummary();

		Portlet testPortlet = new PortletImpl();

		testPortlet.setPortletId(portletId);

		manifestSummary.addDataPortlet(testPortlet, new String[0]);

		Map<String, Boolean> actualPortletControlsMap =
			ExportImportHelperUtil.getImportPortletControlsMap(
				companyId, portletId, parameterMap, portletDataElement,
				manifestSummary);

		_assertPortletControlsMap(
			actualPortletControlsMap, false, false, false, false, false);
	}

	@Test
	public void testGetImportPortletControlsMapRootConfigurationWithPortletConfiguration()
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();

		String portletId = JournalPortletKeys.JOURNAL;

		String rootPortletId = PortletIdCodec.decodePortletName(portletId);

		ExportImportTestParameterMapBuilder builder =
			new ExportImportTestParameterMapBuilder();

		Map<String, String[]> parameterMap =
			builder.withPortletArchivedSetupAll(
				true
			).withPortletConfiguration(
				true
			).withPortletConfigurationAll(
				false
			).withPortletData(
				false
			).withPortletSetupAll(
				true
			).withPortletUserPreferencesAll(
				true
			).build();

		Element portletDataElement = null;
		ManifestSummary manifestSummary = null;

		parameterMap.put(
			PortletDataHandlerKeys.PORTLET_ARCHIVED_SETUPS +
				StringPool.UNDERLINE + rootPortletId,
			new String[] {"true"});

		Map<String, Boolean> actualPortletControlsMap =
			ExportImportHelperUtil.getImportPortletControlsMap(
				companyId, portletId, parameterMap, portletDataElement,
				manifestSummary);

		_assertPortletControlsMap(
			actualPortletControlsMap, false, false, false, false, false);
	}

	@Test
	public void testGetImportPortletControlsMapRootConfigurationWithPortletConfiguration2()
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();

		String portletId = JournalPortletKeys.JOURNAL;

		String rootPortletId = PortletIdCodec.decodePortletName(portletId);

		ExportImportTestParameterMapBuilder builder =
			new ExportImportTestParameterMapBuilder();

		Map<String, String[]> parameterMap =
			builder.withPortletArchivedSetupAll(
				true
			).withPortletConfiguration(
				true
			).withPortletConfigurationAll(
				false
			).withPortletData(
				false
			).withPortletSetupAll(
				true
			).withPortletUserPreferencesAll(
				true
			).build();

		parameterMap.put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION +
				StringPool.UNDERLINE + rootPortletId,
			new String[] {"true"});
		parameterMap.put(
			PortletDataHandlerKeys.PORTLET_ARCHIVED_SETUPS +
				StringPool.UNDERLINE + rootPortletId,
			new String[] {"true"});

		Element portletDataElement = null;
		ManifestSummary manifestSummary = null;

		Map<String, Boolean> actualPortletControlsMap =
			ExportImportHelperUtil.getImportPortletControlsMap(
				companyId, portletId, parameterMap, portletDataElement,
				manifestSummary);

		_assertPortletControlsMap(
			actualPortletControlsMap, true, true, false, false, false);
	}

	@Test
	public void testGetSelectedLayoutsJSONSelectAllLayouts() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(_stagingGroup);

		Layout childLayout = LayoutTestUtil.addTypePortletLayout(
			_stagingGroup, layout.getPlid());

		JSONArray selectedLayoutsJSONArray = JSONFactoryUtil.createJSONArray(
			ExportImportHelperUtil.getSelectedLayoutsJSON(
				_stagingGroup.getGroupId(), false,
				StringUtil.merge(
					new long[] {
						layout.getLayoutId(), childLayout.getLayoutId()
					})));

		Assert.assertEquals(1, selectedLayoutsJSONArray.length());

		JSONObject layoutJSONObject = selectedLayoutsJSONArray.getJSONObject(0);

		Assert.assertTrue(layoutJSONObject.getBoolean("includeChildren"));
		Assert.assertEquals(layout.getPlid(), layoutJSONObject.getLong("plid"));
	}

	@Test
	public void testGetSelectedLayoutsJSONSelectChildLayout() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(_stagingGroup);

		Layout childLayout = LayoutTestUtil.addTypePortletLayout(
			_stagingGroup, layout.getPlid());

		JSONArray selectedLayoutsJSONArray = JSONFactoryUtil.createJSONArray(
			ExportImportHelperUtil.getSelectedLayoutsJSON(
				_stagingGroup.getGroupId(), false,
				StringUtil.merge(new long[] {childLayout.getLayoutId()})));

		Assert.assertEquals(1, selectedLayoutsJSONArray.length());

		JSONObject layoutJSONObject = selectedLayoutsJSONArray.getJSONObject(0);

		Assert.assertTrue(layoutJSONObject.getBoolean("includeChildren"));
		Assert.assertEquals(
			childLayout.getPlid(), layoutJSONObject.getLong("plid"));
	}

	@Test
	public void testGetSelectedLayoutsJSONSelectNoLayouts() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(_stagingGroup);

		LayoutTestUtil.addTypePortletLayout(_stagingGroup, layout.getPlid());

		JSONArray selectedLayoutsJSONArray = JSONFactoryUtil.createJSONArray(
			ExportImportHelperUtil.getSelectedLayoutsJSON(
				_stagingGroup.getGroupId(), false,
				StringUtil.merge(new long[0])));

		Assert.assertEquals(0, selectedLayoutsJSONArray.length());
	}

	@Test
	public void testGetSelectedLayoutsJSONSelectParentLayout()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypePortletLayout(_stagingGroup);

		LayoutTestUtil.addTypePortletLayout(
			_stagingGroup.getGroupId(), "Child Layout", layout.getPlid());

		JSONArray selectedLayoutsJSONArray = JSONFactoryUtil.createJSONArray(
			ExportImportHelperUtil.getSelectedLayoutsJSON(
				_stagingGroup.getGroupId(), false,
				StringUtil.merge(new long[] {layout.getLayoutId()})));

		Assert.assertEquals(1, selectedLayoutsJSONArray.length());

		JSONObject layoutJSONObject = selectedLayoutsJSONArray.getJSONObject(0);

		Assert.assertFalse(layoutJSONObject.getBoolean("includeChildren"));
		Assert.assertEquals(layout.getPlid(), layoutJSONObject.getLong("plid"));
	}

	@Test
	public void testValidateMissingReferences() throws Exception {
		_testValidateMissingReferences();
		_testValidateMissingReferencesWithUnknownClassName();
	}

	protected String getContent(String fileName) throws Exception {
		Class<?> clazz = getClass();

		InputStream inputStream = clazz.getResourceAsStream(
			"dependencies/" + fileName);

		Scanner scanner = new Scanner(inputStream);

		scanner.useDelimiter("\\Z");

		return scanner.next();
	}

	protected FileEntry getFileEntry() throws PortalException {
		FileEntry fileEntry = DLAppLocalServiceUtil.addFileEntry(
			null, TestPropsValues.getUserId(), _stagingGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".txt", ContentTypes.TEXT_PLAIN,
			TestDataConstants.TEST_BYTE_ARRAY, null, null, null,
			ServiceContextTestUtil.getServiceContext(
				_stagingGroup.getGroupId(), TestPropsValues.getUserId()));

		ThumbnailCapability thumbnailCapability =
			fileEntry.getRepositoryCapability(ThumbnailCapability.class);

		return thumbnailCapability.setLargeImageId(
			fileEntry, fileEntry.getFileEntryId());
	}

	protected String replaceParameters(String content, FileEntry fileEntry) {
		return StringUtil.replace(
			content,
			new String[] {"[$GROUP_ID$]", "[$LIVE_GROUP_ID$]", "[$UUID$]"},
			new String[] {
				String.valueOf(fileEntry.getGroupId()),
				String.valueOf(fileEntry.getGroupId()), fileEntry.getUuid()
			});
	}

	private void _assertPortletControlsMap(
		Map<String, Boolean> actualPortletControlsMap,
		boolean portletArchivedSetups, boolean portletConfiguration,
		boolean portletData, boolean portletSetup,
		boolean portletUserPreferences) {

		boolean actualPortletArchivedSetups = MapUtil.getBoolean(
			actualPortletControlsMap,
			PortletDataHandlerKeys.PORTLET_ARCHIVED_SETUPS);
		boolean actualPortletConfiguration = MapUtil.getBoolean(
			actualPortletControlsMap,
			PortletDataHandlerKeys.PORTLET_CONFIGURATION);
		boolean actualPortletData = MapUtil.getBoolean(
			actualPortletControlsMap, PortletDataHandlerKeys.PORTLET_DATA);
		boolean actualPortletSetup = MapUtil.getBoolean(
			actualPortletControlsMap, PortletDataHandlerKeys.PORTLET_SETUP);
		boolean actualPortletUserPreferences = MapUtil.getBoolean(
			actualPortletControlsMap,
			PortletDataHandlerKeys.PORTLET_USER_PREFERENCES);

		Assert.assertEquals(portletArchivedSetups, actualPortletArchivedSetups);
		Assert.assertEquals(portletConfiguration, actualPortletConfiguration);
		Assert.assertEquals(portletData, actualPortletData);
		Assert.assertEquals(portletSetup, actualPortletSetup);
		Assert.assertEquals(
			portletUserPreferences, actualPortletUserPreferences);
	}

	private Portlet _getDataSiteLevelPortlet(
			String className, long companyId, boolean excludeDataAlwaysStaged,
			UnsafeFunction<Portlet, Boolean, Exception> unsafeFunction)
		throws Exception {

		long startTime = System.currentTimeMillis();

		while ((System.currentTimeMillis() - startTime) < 5000) {
			Portlet portlet = ExportImportHelperUtil.getDataSiteLevelPortlet(
				className, companyId, excludeDataAlwaysStaged);

			if (unsafeFunction.apply(portlet)) {
				return portlet;
			}

			Thread.sleep(50);
		}

		throw new AssertionError("No portlet found for the given criteria");
	}

	private SafeCloseable _registerWithSafeCloseable(
		BundleContext bundleContext, List<Long> companyIds,
		PortletDataHandler portletDataHandler, String portletId) {

		List<ServiceRegistration<?>> serviceRegistrations = new ArrayList<>();

		serviceRegistrations.add(
			bundleContext.registerService(
				jakarta.portlet.Portlet.class,
				new GenericPortlet() {
				},
				HashMapDictionaryBuilder.<String, Object>put(
					"jakarta.portlet.name", portletId
				).build()));

		if (portletDataHandler != null) {
			serviceRegistrations.add(
				bundleContext.registerService(
					PortletDataHandler.class, portletDataHandler,
					HashMapDictionaryBuilder.<String, Object>put(
						"companyId",
						() -> TransformUtil.transform(
							companyIds, String::valueOf)
					).put(
						"jakarta.portlet.name", portletId
					).build()));
		}

		return () -> {
			for (ServiceRegistration<?> serviceRegistration :
					serviceRegistrations) {

				serviceRegistration.unregister();
			}
		};
	}

	private void _testValidateMissingReferences() throws Exception {
		String xml = replaceParameters(
			getContent("missing_references.txt"), getFileEntry());

		ZipWriter zipWriter = _zipWriterFactory.getZipWriter();

		zipWriter.addEntry("/manifest.xml", xml);

		try (ZipReader zipReader = _zipReaderFactory.getZipReader(
				zipWriter.getFile())) {

			PortletDataContext portletDataContextImport =
				PortletDataContextFactoryUtil.createImportPortletDataContext(
					_liveGroup.getCompanyId(), _liveGroup.getGroupId(),
					new HashMap<String, String[]>(), new TestUserIdStrategy(),
					zipReader);

			MissingReferences missingReferences =
				ExportImportHelperUtil.validateMissingReferences(
					portletDataContextImport);

			Map<String, MissingReference> dependencyMissingReferences =
				missingReferences.getDependencyMissingReferences();

			Map<String, MissingReference> weakMissingReferences =
				missingReferences.getWeakMissingReferences();

			Assert.assertEquals(
				dependencyMissingReferences.toString(), 2,
				dependencyMissingReferences.size());
			Assert.assertEquals(
				weakMissingReferences.toString(), 1,
				weakMissingReferences.size());
		}

		FileUtil.delete(zipWriter.getFile());
	}

	@TestInfo("LPS-161743")
	private void _testValidateMissingReferencesWithUnknownClassName()
		throws Exception {

		ZipWriter zipWriter = _zipWriterFactory.getZipWriter();

		zipWriter.addEntry(
			"/manifest.xml",
			getContent("unknown_class_name_missing_references.txt"));

		try (ZipReader zipReader = _zipReaderFactory.getZipReader(
				zipWriter.getFile())) {

			PortletDataContext portletDataContextImport =
				PortletDataContextFactoryUtil.createImportPortletDataContext(
					_liveGroup.getCompanyId(), _liveGroup.getGroupId(),
					new HashMap<String, String[]>(), new TestUserIdStrategy(),
					zipReader);

			MissingReferences missingReferences =
				ExportImportHelperUtil.validateMissingReferences(
					portletDataContextImport);

			Map<String, MissingReference> dependencyMissingReferences =
				missingReferences.getDependencyMissingReferences();

			Assert.assertEquals(
				dependencyMissingReferences.toString(), 1,
				dependencyMissingReferences.size());
			Assert.assertTrue(
				dependencyMissingReferences.toString(),
				dependencyMissingReferences.containsKey(
					"my-custom-display-name"));
		}

		FileUtil.delete(zipWriter.getFile());
	}

	@DeleteAfterTestRun
	private Group _liveGroup;

	@DeleteAfterTestRun
	private Group _stagingGroup;

	@Inject
	private ZipReaderFactory _zipReaderFactory;

	@Inject
	private ZipWriterFactory _zipWriterFactory;

	private static class TestPortletDataHandler extends BasePortletDataHandler {

		@Override
		public String[] getClassNames() {
			return _classNames;
		}

		private TestPortletDataHandler(
			String[] classNames, boolean dataAlwaysStaged,
			DataLevel dataLevel) {

			_classNames = classNames;

			setDataAlwaysStaged(dataAlwaysStaged);
			setDataLevel(dataLevel);
		}

		private final String[] _classNames;

	}

	private class ExportImportTestParameterMapBuilder {

		public Map<String, String[]> build() {
			return _parameterMap;
		}

		public ExportImportTestParameterMapBuilder withPortletArchivedSetupAll(
			boolean portletArchivedSetupAll) {

			_parameterMap.put(
				PortletDataHandlerKeys.PORTLET_ARCHIVED_SETUPS_ALL,
				new String[] {Boolean.toString(portletArchivedSetupAll)});

			return this;
		}

		public ExportImportTestParameterMapBuilder withPortletConfiguration(
			boolean portletConfiguration) {

			_parameterMap.put(
				PortletDataHandlerKeys.PORTLET_CONFIGURATION,
				new String[] {Boolean.toString(portletConfiguration)});

			return this;
		}

		public ExportImportTestParameterMapBuilder withPortletConfigurationAll(
			boolean portletConfigurationAll) {

			_parameterMap.put(
				PortletDataHandlerKeys.PORTLET_CONFIGURATION_ALL,
				new String[] {Boolean.toString(portletConfigurationAll)});

			return this;
		}

		public ExportImportTestParameterMapBuilder withPortletData(
			boolean portletData) {

			_parameterMap.put(
				PortletDataHandlerKeys.PORTLET_DATA,
				new String[] {Boolean.toString(portletData)});

			return this;
		}

		public ExportImportTestParameterMapBuilder withPortletSetupAll(
			boolean portletSetupAll) {

			_parameterMap.put(
				PortletDataHandlerKeys.PORTLET_SETUP_ALL,
				new String[] {Boolean.toString(portletSetupAll)});

			return this;
		}

		public ExportImportTestParameterMapBuilder
			withPortletUserPreferencesAll(boolean portletUserPreferencesAll) {

			_parameterMap.put(
				PortletDataHandlerKeys.PORTLET_USER_PREFERENCES_ALL,
				new String[] {Boolean.toString(portletUserPreferencesAll)});

			return this;
		}

		private final Map<String, String[]> _parameterMap = new HashMap<>();

	}

}