/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.configuration.admin.display.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.exception.NoSuchCategoryException;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Dictionary;
import java.util.List;
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
 * @author Jonathan McCann
 */
@RunWith(Arquillian.class)
public class CategorizationSiteSettingsConfigurationScreenWrapperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() {
		Bundle bundle = FrameworkUtil.getBundle(
			CategorizationSiteSettingsConfigurationScreenWrapperTest.class);

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
		Group companyGroup = _groupLocalService.getCompanyGroup(
			TestPropsValues.getCompanyId());

		AssetVocabulary companyAssetVocabulary = AssetTestUtil.addVocabulary(
			companyGroup.getGroupId());

		AssetCategory companyAssetCategory = AssetTestUtil.addCategory(
			companyGroup.getGroupId(),
			companyAssetVocabulary.getVocabularyId());

		Group sourceGroup = GroupTestUtil.addGroup();

		AssetVocabulary sourceAssetVocabulary = AssetTestUtil.addVocabulary(
			sourceGroup.getGroupId());

		AssetCategory sourceAssetCategory = AssetTestUtil.addCategory(
			sourceGroup.getGroupId(), sourceAssetVocabulary.getVocabularyId());

		String[] sourceAssetTagNames = {
			RandomTestUtil.randomString(), RandomTestUtil.randomString()
		};

		_groupLocalService.updateAsset(
			TestPropsValues.getUserId(), sourceGroup,
			new long[] {
				companyAssetCategory.getCategoryId(),
				sourceAssetCategory.getCategoryId()
			},
			sourceAssetTagNames);

		ConfigurationScreen configurationScreen = _serviceTrackerMap.getService(
			"site-configuration-categorization");

		Dictionary<String, Object> properties =
			configurationScreen.exportProperties(sourceGroup.getGroupId());

		Assert.assertEquals(properties.toString(), 2, properties.size());
		Assert.assertEquals(
			SetUtil.fromArray(
				HashMapBuilder.<String, Object>put(
					"externalReferenceCode",
					companyAssetCategory.getExternalReferenceCode()
				).put(
					"scopeExternalReferenceCode",
					companyGroup.getExternalReferenceCode()
				).build(),
				HashMapBuilder.<String, Object>put(
					"externalReferenceCode",
					sourceAssetCategory.getExternalReferenceCode()
				).build()),
			SetUtil.fromCollection(
				(List<Map<String, Object>>)properties.get("assetCategories")));
		Assert.assertEquals(
			SetUtil.fromArray(sourceAssetTagNames),
			SetUtil.fromCollection(
				(List<String>)properties.get("assetTagNames")));

		Group targetGroup = GroupTestUtil.addGroup();

		AssetVocabulary targetAssetVocabulary = AssetTestUtil.addVocabulary(
			targetGroup.getGroupId());

		AssetCategory targetAssetCategory = AssetTestUtil.addCategory(
			sourceAssetCategory.getExternalReferenceCode(),
			targetGroup.getGroupId(), targetAssetVocabulary.getVocabularyId());

		configurationScreen.importProperties(
			properties, targetGroup.getGroupId());

		_assertAssetCategoryIds(
			targetGroup, companyAssetCategory.getCategoryId(),
			targetAssetCategory.getCategoryId());
		_assertAssetTagNames(targetGroup, sourceAssetTagNames);
	}

	@Test
	public void testExportProperties() throws Exception {
		Group group = GroupTestUtil.addGroup();

		ConfigurationScreen configurationScreen = _serviceTrackerMap.getService(
			"site-configuration-categorization");

		Dictionary<String, Object> properties =
			configurationScreen.exportProperties(group.getGroupId());

		Assert.assertTrue(properties.isEmpty());
	}

	@Test
	public void testImportProperties() throws Exception {
		Group group = GroupTestUtil.addGroup();

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			group.getGroupId());

		AssetCategory assetCategory = AssetTestUtil.addCategory(
			group.getGroupId(), assetVocabulary.getVocabularyId());

		long[] assetCategoryIds = {assetCategory.getCategoryId()};

		String[] assetTagNames = {RandomTestUtil.randomString()};

		_groupLocalService.updateAsset(
			TestPropsValues.getUserId(), group, assetCategoryIds,
			assetTagNames);

		ConfigurationScreen configurationScreen = _serviceTrackerMap.getService(
			"site-configuration-categorization");

		configurationScreen.importProperties(
			new HashMapDictionary<>(), group.getGroupId());

		_assertAssetCategoryIds(group, assetCategoryIds);
		_assertAssetTagNames(group, assetTagNames);

		assetTagNames = new String[] {RandomTestUtil.randomString()};

		configurationScreen.importProperties(
			HashMapDictionaryBuilder.<String, Object>put(
				"assetTagNames", ListUtil.fromArray(assetTagNames)
			).build(),
			group.getGroupId());

		_assertAssetCategoryIds(group, assetCategoryIds);
		_assertAssetTagNames(group, assetTagNames);

		assetCategory = AssetTestUtil.addCategory(
			group.getGroupId(), assetVocabulary.getVocabularyId());

		assetCategoryIds = new long[] {assetCategory.getCategoryId()};

		configurationScreen.importProperties(
			HashMapDictionaryBuilder.<String, Object>put(
				"assetCategories",
				ListUtil.fromArray(
					HashMapBuilder.<String, Object>put(
						"externalReferenceCode",
						assetCategory.getExternalReferenceCode()
					).build())
			).build(),
			group.getGroupId());

		_assertAssetCategoryIds(group, assetCategoryIds);
		_assertAssetTagNames(group, assetTagNames);

		try {
			configurationScreen.importProperties(
				HashMapDictionaryBuilder.<String, Object>put(
					"assetCategories",
					ListUtil.fromArray(
						HashMapBuilder.<String, Object>put(
							"externalReferenceCode",
							RandomTestUtil.randomString()
						).build())
				).put(
					"assetTagNames",
					ListUtil.fromArray(RandomTestUtil.randomString())
				).build(),
				group.getGroupId());

			Assert.fail();
		}
		catch (NoSuchCategoryException noSuchCategoryException) {
			_assertAssetCategoryIds(group, assetCategoryIds);
			_assertAssetTagNames(group, assetTagNames);
		}
	}

	private void _assertAssetCategoryIds(
		Group group, long... assetCategoryIds) {

		Assert.assertEquals(
			SetUtil.fromArray(assetCategoryIds),
			SetUtil.fromArray(
				_assetCategoryLocalService.getCategoryIds(
					Group.class.getName(), group.getGroupId())));
	}

	private void _assertAssetTagNames(Group group, String... assetTagNames) {
		Assert.assertEquals(
			SetUtil.fromArray(assetTagNames),
			SetUtil.fromArray(
				_assetTagLocalService.getTagNames(
					Group.class.getName(), group.getGroupId())));
	}

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetTagLocalService _assetTagLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	private ServiceTrackerMap<String, ConfigurationScreen> _serviceTrackerMap;

}