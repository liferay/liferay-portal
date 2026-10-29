/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.seo.web.internal.configuration.admin.display.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLTrashLocalService;
import com.liferay.layout.seo.model.LayoutSEOSite;
import com.liferay.layout.seo.service.LayoutSEOSiteLocalService;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Dictionary;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class OpenGraphSiteSettingsConfigurationScreenWrapperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() {
		Bundle bundle = FrameworkUtil.getBundle(
			OpenGraphSiteSettingsConfigurationScreenWrapperTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, ConfigurationScreen.class, null,
			(serviceReference, emitter) -> {
				ConfigurationScreen configurationScreen =
					bundleContext.getService(serviceReference);

				emitter.emit(configurationScreen.getKey());
			});
	}

	@After
	public void tearDown() {
		_serviceTrackerMap.close();
	}

	@Test
	public void testExportImportProperties() throws Exception {
		Group sourceGroup = GroupTestUtil.addGroup();

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext(sourceGroup.getGroupId()));

		Group depotGroup = depotEntry.getGroup();

		FileEntry fileEntry = _addFileEntry(
			RandomTestUtil.randomString(), depotGroup.getGroupId());

		Map<Locale, String> openGraphImageAltMap =
			RandomTestUtil.randomLocaleStringMap();

		_updateLayoutSEOSite(
			sourceGroup.getGroupId(), false, openGraphImageAltMap,
			fileEntry.getFileEntryId());

		ConfigurationScreen configurationScreen = _serviceTrackerMap.getService(
			"site-configuration-open-graph");

		Dictionary<String, Object> properties =
			configurationScreen.exportProperties(sourceGroup.getGroupId());

		Assert.assertEquals(Boolean.FALSE, properties.get("openGraphEnabled"));
		Assert.assertEquals(
			fileEntry.getExternalReferenceCode(),
			properties.get("openGraphImageFileEntryERC"));
		Assert.assertEquals(
			depotGroup.getExternalReferenceCode(),
			properties.get("openGraphImageFileEntryScopeERC"));

		Group targetGroup = GroupTestUtil.addGroup();

		configurationScreen.importProperties(
			properties, targetGroup.getGroupId());

		LayoutSEOSite layoutSEOSite =
			_layoutSEOSiteLocalService.fetchLayoutSEOSiteByGroupId(
				targetGroup.getGroupId());

		Assert.assertFalse(layoutSEOSite.isOpenGraphEnabled());
		Assert.assertEquals(
			openGraphImageAltMap, layoutSEOSite.getOpenGraphImageAltMap());
		Assert.assertEquals(
			fileEntry.getFileEntryId(),
			layoutSEOSite.getOpenGraphImageFileEntryId());

		layoutSEOSite = _layoutSEOSiteLocalService.fetchLayoutSEOSiteByGroupId(
			sourceGroup.getGroupId());

		Assert.assertFalse(layoutSEOSite.isOpenGraphEnabled());
		Assert.assertEquals(
			openGraphImageAltMap, layoutSEOSite.getOpenGraphImageAltMap());
		Assert.assertEquals(
			fileEntry.getFileEntryId(),
			layoutSEOSite.getOpenGraphImageFileEntryId());
	}

	@Test
	public void testExportImportPropertiesWithFileEntryInSameGroup()
		throws Exception {

		Group sourceGroup = GroupTestUtil.addGroup();

		String externalReferenceCode = RandomTestUtil.randomString();

		FileEntry sourceFileEntry = _addFileEntry(
			externalReferenceCode, sourceGroup.getGroupId());

		_updateLayoutSEOSite(
			sourceGroup.getGroupId(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomLocaleStringMap(),
			sourceFileEntry.getFileEntryId());

		ConfigurationScreen configurationScreen = _serviceTrackerMap.getService(
			"site-configuration-open-graph");

		Dictionary<String, Object> properties =
			configurationScreen.exportProperties(sourceGroup.getGroupId());

		Assert.assertEquals(
			externalReferenceCode,
			properties.get("openGraphImageFileEntryERC"));
		Assert.assertNull(properties.get("openGraphImageFileEntryScopeERC"));

		Group targetGroup = GroupTestUtil.addGroup();

		FileEntry targetFileEntry = _addFileEntry(
			externalReferenceCode, targetGroup.getGroupId());

		configurationScreen.importProperties(
			properties, targetGroup.getGroupId());

		LayoutSEOSite layoutSEOSite =
			_layoutSEOSiteLocalService.fetchLayoutSEOSiteByGroupId(
				targetGroup.getGroupId());

		Assert.assertEquals(
			targetFileEntry.getFileEntryId(),
			layoutSEOSite.getOpenGraphImageFileEntryId());
	}

	@Test
	public void testExportProperties() throws Exception {
		Group group = GroupTestUtil.addGroup();

		ConfigurationScreen configurationScreen = _serviceTrackerMap.getService(
			"site-configuration-open-graph");

		Dictionary<String, Object> properties =
			configurationScreen.exportProperties(group.getGroupId());

		Assert.assertTrue(properties.isEmpty());
	}

	@Test
	public void testExportPropertiesWithMissingFileEntry() throws Exception {
		Group group = GroupTestUtil.addGroup();

		FileEntry fileEntry = _addFileEntry(
			RandomTestUtil.randomString(), group.getGroupId());

		_updateLayoutSEOSite(
			group.getGroupId(), false, RandomTestUtil.randomLocaleStringMap(),
			fileEntry.getFileEntryId());

		_dlTrashLocalService.moveFileEntryToTrash(
			TestPropsValues.getUserId(), group.getGroupId(),
			fileEntry.getFileEntryId());

		ConfigurationScreen configurationScreen = _serviceTrackerMap.getService(
			"site-configuration-open-graph");

		Dictionary<String, Object> properties =
			configurationScreen.exportProperties(group.getGroupId());

		Assert.assertEquals(properties.toString(), 1, properties.size());
		Assert.assertEquals(Boolean.FALSE, properties.get("openGraphEnabled"));

		_dlAppLocalService.deleteFileEntry(fileEntry.getFileEntryId());

		properties = configurationScreen.exportProperties(group.getGroupId());

		Assert.assertEquals(properties.toString(), 1, properties.size());
		Assert.assertEquals(Boolean.FALSE, properties.get("openGraphEnabled"));
	}

	@Test
	public void testImportProperties() throws Exception {
		Group group = GroupTestUtil.addGroup();

		FileEntry fileEntry = _addFileEntry(
			RandomTestUtil.randomString(), group.getGroupId());

		Map<Locale, String> openGraphImageAltMap =
			RandomTestUtil.randomLocaleStringMap();

		_updateLayoutSEOSite(
			group.getGroupId(), true, openGraphImageAltMap,
			fileEntry.getFileEntryId());

		ConfigurationScreen configurationScreen = _serviceTrackerMap.getService(
			"site-configuration-open-graph");

		configurationScreen.importProperties(
			HashMapDictionaryBuilder.<String, Object>put(
				"openGraphEnabled", false
			).build(),
			group.getGroupId());

		LayoutSEOSite layoutSEOSite =
			_layoutSEOSiteLocalService.fetchLayoutSEOSiteByGroupId(
				group.getGroupId());

		Assert.assertFalse(layoutSEOSite.isOpenGraphEnabled());
		Assert.assertEquals(
			openGraphImageAltMap, layoutSEOSite.getOpenGraphImageAltMap());
		Assert.assertEquals(
			fileEntry.getFileEntryId(),
			layoutSEOSite.getOpenGraphImageFileEntryId());
	}

	@Test
	public void testImportPropertiesWithMissingFileEntry() throws Exception {
		Group group = GroupTestUtil.addGroup();

		FileEntry fileEntry = _addFileEntry(
			RandomTestUtil.randomString(), group.getGroupId());

		Map<Locale, String> openGraphImageAltMap =
			RandomTestUtil.randomLocaleStringMap();

		_updateLayoutSEOSite(
			group.getGroupId(), true, openGraphImageAltMap,
			fileEntry.getFileEntryId());

		ConfigurationScreen configurationScreen = _serviceTrackerMap.getService(
			"site-configuration-open-graph");

		String externalReferenceCode = RandomTestUtil.randomString();

		try {
			configurationScreen.importProperties(
				HashMapDictionaryBuilder.<String, Object>put(
					"openGraphEnabled", false
				).put(
					"openGraphImageFileEntryERC", externalReferenceCode
				).build(),
				group.getGroupId());

			Assert.fail();
		}
		catch (NoSuchFileEntryException noSuchFileEntryException) {
			Assert.assertEquals(
				StringBundler.concat(
					"No file entry exists with external reference code ",
					externalReferenceCode,
					" in the site or asset library with external reference ",
					"code ", group.getExternalReferenceCode()),
				noSuchFileEntryException.getMessage());
		}

		String scopeExternalReferenceCode = RandomTestUtil.randomString();

		try {
			configurationScreen.importProperties(
				HashMapDictionaryBuilder.<String, Object>put(
					"openGraphEnabled", false
				).put(
					"openGraphImageFileEntryERC", externalReferenceCode
				).put(
					"openGraphImageFileEntryScopeERC",
					scopeExternalReferenceCode
				).build(),
				group.getGroupId());

			Assert.fail();
		}
		catch (NoSuchFileEntryException noSuchFileEntryException) {
			Assert.assertEquals(
				StringBundler.concat(
					"No file entry exists with external reference code ",
					externalReferenceCode,
					" in the site or asset library with external reference ",
					"code ", scopeExternalReferenceCode),
				noSuchFileEntryException.getMessage());
		}

		FileEntry trashedFileEntry = _addFileEntry(
			RandomTestUtil.randomString(), group.getGroupId());

		_dlTrashLocalService.moveFileEntryToTrash(
			TestPropsValues.getUserId(), group.getGroupId(),
			trashedFileEntry.getFileEntryId());

		try {
			configurationScreen.importProperties(
				HashMapDictionaryBuilder.<String, Object>put(
					"openGraphEnabled", false
				).put(
					"openGraphImageFileEntryERC",
					trashedFileEntry.getExternalReferenceCode()
				).build(),
				group.getGroupId());

			Assert.fail();
		}
		catch (NoSuchFileEntryException noSuchFileEntryException) {
			Assert.assertEquals(
				StringBundler.concat(
					"No file entry exists with external reference code ",
					trashedFileEntry.getExternalReferenceCode(),
					" in the site or asset library with external reference ",
					"code ", group.getExternalReferenceCode()),
				noSuchFileEntryException.getMessage());
		}

		LayoutSEOSite layoutSEOSite =
			_layoutSEOSiteLocalService.fetchLayoutSEOSiteByGroupId(
				group.getGroupId());

		Assert.assertTrue(layoutSEOSite.isOpenGraphEnabled());
		Assert.assertEquals(
			openGraphImageAltMap, layoutSEOSite.getOpenGraphImageAltMap());
		Assert.assertEquals(
			fileEntry.getFileEntryId(),
			layoutSEOSite.getOpenGraphImageFileEntryId());
	}

	private FileEntry _addFileEntry(String externalReferenceCode, long groupId)
		throws Exception {

		return _dlAppLocalService.addFileEntry(
			externalReferenceCode, TestPropsValues.getUserId(), groupId,
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".png", ContentTypes.IMAGE_PNG,
			RandomTestUtil.randomBytes(), null, null, null,
			ServiceContextTestUtil.getServiceContext(groupId));
	}

	private void _updateLayoutSEOSite(
			long groupId, boolean openGraphEnabled,
			Map<Locale, String> openGraphImageAltMap,
			long openGraphImageFileEntryId)
		throws Exception {

		_layoutSEOSiteLocalService.updateLayoutSEOSite(
			TestPropsValues.getUserId(), groupId, openGraphEnabled,
			openGraphImageAltMap, openGraphImageFileEntryId,
			ServiceContextTestUtil.getServiceContext(groupId));
	}

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLTrashLocalService _dlTrashLocalService;

	@Inject
	private LayoutSEOSiteLocalService _layoutSEOSiteLocalService;

	private ServiceTrackerMap<String, ConfigurationScreen> _serviceTrackerMap;

}