/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.osgi.commands.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.model.AssetListEntrySegmentsEntryRel;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.asset.list.service.AssetListEntrySegmentsEntryRelLocalService;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.asset.publisher.test.util.AssetPublisherTestUtil;
import com.liferay.asset.publisher.util.AssetEntryResult;
import com.liferay.asset.publisher.util.AssetPublisherHelper;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.segments.constants.SegmentsEntryConstants;

import jakarta.portlet.PortletPreferences;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author ALicia García
 */
@RunWith(Arquillian.class)
public class AssetPublisherOSGiCommandsTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypePortletLayout(_group.getGroupId());

		_portletId = LayoutTestUtil.addPortletToLayout(
			_layout, AssetPublisherPortletKeys.ASSET_PUBLISHER);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetAssetEntriesFromDynamicCollection() throws Exception {
		List<AssetEntry> assetEntries = new ArrayList<>();

		assetEntries.add(_addAssetEntry());
		assetEntries.add(_addAssetEntry());
		assetEntries.add(_addAssetEntry());

		PortletPreferences portletPreferences =
			_setDynamicSelectionStylePreference();

		Assert.assertEquals(
			"dynamic", portletPreferences.getValue("selectionStyle", null));

		AssetEntryQuery assetEntryQuery =
			_assetPublisherHelper.getAssetEntryQuery(
				portletPreferences, _group.getGroupId(), _layout, null, null);

		SearchContainer<AssetEntry> searchContainer = new SearchContainer<>();

		searchContainer.setResultsAndTotal(Collections::emptyList, 10);

		List<AssetEntryResult> actualAssetEntryResults =
			_assetPublisherHelper.getAssetEntryResults(
				searchContainer, assetEntryQuery, _layout, portletPreferences,
				StringPool.BLANK, null, null, TestPropsValues.getCompanyId(),
				_group.getGroupId(), TestPropsValues.getUserId(),
				assetEntryQuery.getClassNameIds(), null);

		AssetEntryResult assetEntryResult = actualAssetEntryResults.get(0);

		List<AssetEntry> actualAssetEntries =
			assetEntryResult.getAssetEntries();

		Assert.assertEquals(
			SetUtil.fromList(assetEntries),
			SetUtil.fromList(actualAssetEntries));

		_run("migratePortletPreferences");

		portletPreferences =
			PortletPreferencesFactoryUtil.getExistingPortletSetup(
				_layout, _portletId);

		Assert.assertEquals(
			"asset-list", portletPreferences.getValue("selectionStyle", null));

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.
				fetchAssetListEntryByExternalReferenceCode(
					portletPreferences.getValue(
						"assetListEntryExternalReferenceCode", null),
					_group.getGroupId());

		AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel =
			_assetListEntrySegmentsEntryRelLocalService.
				fetchAssetListEntrySegmentsEntryRel(
					assetListEntry.getAssetListEntryId(),
					SegmentsEntryConstants.ID_DEFAULT);

		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.load(
			assetListEntrySegmentsEntryRel.getTypeSettings()
		).build();

		Assert.assertEquals(
			"true", unicodeProperties.getProperty("anyAssetType", null));
		Assert.assertEquals(
			_CLASS_NAME_IDS,
			unicodeProperties.getProperty("classNameIds", null));

		assetEntryQuery = _assetPublisherHelper.getAssetEntryQuery(
			portletPreferences, _group.getGroupId(), _layout, null, null);

		actualAssetEntryResults = _assetPublisherHelper.getAssetEntryResults(
			searchContainer, assetEntryQuery, _layout, portletPreferences,
			StringPool.BLANK, null, null, TestPropsValues.getCompanyId(),
			_group.getGroupId(), TestPropsValues.getUserId(),
			assetEntryQuery.getClassNameIds(), null);

		assetEntryResult = actualAssetEntryResults.get(0);

		Assert.assertEquals(
			SetUtil.fromList(actualAssetEntries),
			SetUtil.fromList(assetEntryResult.getAssetEntries()));
	}

	@Test
	public void testGetAssetEntriesFromManualCollection() throws Exception {
		AssetEntry assetEntry1 = _addAssetEntry();
		AssetEntry assetEntry2 = _addAssetEntry();
		AssetEntry assetEntry3 = _addAssetEntry();
		AssetEntry assetEntry4 = _addAssetEntry();

		PortletPreferences portletPreferences =
			_setPortletManualSelectionStylePreference(
				assetEntry1, assetEntry2, assetEntry3, assetEntry4);

		Assert.assertEquals(
			"manual", portletPreferences.getValue("selectionStyle", null));

		_run("migratePortletPreferences");

		portletPreferences =
			PortletPreferencesFactoryUtil.getExistingPortletSetup(
				_layout, _portletId);

		Assert.assertEquals(
			"asset-list", portletPreferences.getValue("selectionStyle", null));

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.
				fetchAssetListEntryByExternalReferenceCode(
					portletPreferences.getValue(
						"assetListEntryExternalReferenceCode", null),
					_group.getGroupId());

		Assert.assertNotNull(assetListEntry);
	}

	@Test
	public void testMigratePortletPreferencesWithLongLayoutName()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), RandomTestUtil.randomString(80), false);

		String portletId = LayoutTestUtil.addPortletToLayout(
			layout, AssetPublisherPortletKeys.ASSET_PUBLISHER);

		PortletPreferences portletPreferences =
			LayoutTestUtil.getPortletPreferences(layout, portletId);

		portletPreferences.setValue(
			"scopeIds",
			AssetPublisherHelper.SCOPE_ID_GROUP_PREFIX + _group.getGroupId());
		portletPreferences.setValue("selectionStyle", "dynamic");

		portletPreferences.store();

		_run("migratePortletPreferences");

		portletPreferences =
			PortletPreferencesFactoryUtil.getExistingPortletSetup(
				layout, portletId);

		Assert.assertEquals(
			"asset-list", portletPreferences.getValue("selectionStyle", null));

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.
				fetchAssetListEntryByExternalReferenceCode(
					portletPreferences.getValue(
						"assetListEntryExternalReferenceCode", null),
					_group.getGroupId());

		String title = assetListEntry.getTitle();

		Assert.assertEquals(
			ModelHintsUtil.getMaxLength(
				AssetListEntry.class.getName(), "title"),
			title.length());
		Assert.assertTrue(
			title.startsWith(
				"AP " + PortletIdCodec.decodeInstanceId(portletId)));
	}

	private AssetEntry _addAssetEntry() throws Exception {
		BlogsEntry blogsEntry = _blogsEntryLocalService.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			StringPool.BLANK, StringPool.BLANK, RandomTestUtil.randomString(),
			1, 1, 1965, 0, 0, true, true, null, StringPool.BLANK, null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		return _assetEntryLocalService.getEntry(
			_group.getGroupId(), blogsEntry.getUuid());
	}

	private void _run(String name) throws Exception {
		Class<?> clazz = _assetPublisherOSGiCommands.getClass();

		Method method = clazz.getMethod(name);

		method.invoke(_assetPublisherOSGiCommands, null);
	}

	private PortletPreferences _setDynamicSelectionStylePreference()
		throws Exception {

		PortletPreferences portletPreferences =
			LayoutTestUtil.getPortletPreferences(_layout, _portletId);

		portletPreferences.setValue("classNameIds", _CLASS_NAME_IDS);
		portletPreferences.setValue(
			"scopeIds",
			AssetPublisherHelper.SCOPE_ID_GROUP_PREFIX + _group.getGroupId());
		portletPreferences.setValue("selectionStyle", "dynamic");

		portletPreferences.store();

		return portletPreferences;
	}

	private PortletPreferences _setPortletManualSelectionStylePreference(
			AssetEntry... assetEntries)
		throws Exception {

		PortletPreferences portletPreferences =
			LayoutTestUtil.getPortletPreferences(_layout, _portletId);

		portletPreferences.setValue("selectionStyle", "manual");

		String[] assetEntryXmls = portletPreferences.getValues(
			"assetEntryXml", new String[0]);

		for (AssetEntry assetEntry : assetEntries) {
			String assetEntryXml = AssetPublisherTestUtil.getAssetEntryXml(
				assetEntry);

			if (!ArrayUtil.contains(assetEntryXmls, assetEntryXml)) {
				assetEntryXmls = ArrayUtil.append(
					assetEntryXmls, assetEntryXml);
			}
		}

		portletPreferences.setValues("assetEntryXml", assetEntryXmls);

		portletPreferences.store();

		return portletPreferences;
	}

	private static final String _CLASS_NAME_IDS =
		PortalUtil.getClassNameId(Layout.class) + StringPool.COMMA +
			RandomTestUtil.nextLong();

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private AssetListEntryLocalService _assetListEntryLocalService;

	@Inject
	private AssetListEntrySegmentsEntryRelLocalService
		_assetListEntrySegmentsEntryRelLocalService;

	@Inject
	private AssetPublisherHelper _assetPublisherHelper;

	@Inject(
		filter = "osgi.command.scope=assetPublisher", type = Inject.NoType.class
	)
	private Object _assetPublisherOSGiCommands;

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;
	private String _portletId;

}