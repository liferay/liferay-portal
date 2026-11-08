/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.configuration.admin.display.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Dictionary;
import java.util.List;

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
		Group sourceGroup = GroupTestUtil.addGroup();

		String[] sourceAssetTagNames = {
			RandomTestUtil.randomString(), RandomTestUtil.randomString()
		};

		_groupLocalService.updateAsset(
			TestPropsValues.getUserId(), sourceGroup, null,
			sourceAssetTagNames);

		ConfigurationScreen configurationScreen = _serviceTrackerMap.getService(
			"site-configuration-categorization");

		Dictionary<String, Object> properties =
			configurationScreen.exportProperties(sourceGroup.getGroupId());

		Assert.assertEquals(properties.toString(), 1, properties.size());
		Assert.assertEquals(
			SetUtil.fromArray(sourceAssetTagNames),
			SetUtil.fromCollection(
				(List<String>)properties.get("assetTagNames")));

		Group targetGroup = GroupTestUtil.addGroup();

		AssetCategory assetCategory = _addAssetCategory(targetGroup);

		_groupLocalService.updateAsset(
			TestPropsValues.getUserId(), targetGroup,
			new long[] {assetCategory.getCategoryId()},
			new String[] {RandomTestUtil.randomString()});

		configurationScreen.importProperties(
			properties, targetGroup.getGroupId());

		Assert.assertArrayEquals(
			new long[] {assetCategory.getCategoryId()},
			_assetCategoryLocalService.getCategoryIds(
				Group.class.getName(), targetGroup.getGroupId()));
		Assert.assertEquals(
			SetUtil.fromArray(sourceAssetTagNames),
			SetUtil.fromArray(
				_assetTagLocalService.getTagNames(
					Group.class.getName(), targetGroup.getGroupId())));
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

		String[] assetTagNames = {RandomTestUtil.randomString()};

		_groupLocalService.updateAsset(
			TestPropsValues.getUserId(), group, null, assetTagNames);

		ConfigurationScreen configurationScreen = _serviceTrackerMap.getService(
			"site-configuration-categorization");

		configurationScreen.importProperties(
			new HashMapDictionary<>(), group.getGroupId());

		Assert.assertArrayEquals(
			assetTagNames,
			_assetTagLocalService.getTagNames(
				Group.class.getName(), group.getGroupId()));
	}

	private AssetCategory _addAssetCategory(Group group) throws Exception {
		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), group.getGroupId(),
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		return _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), group.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));
	}

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetTagLocalService _assetTagLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	private ServiceTrackerMap<String, ConfigurationScreen> _serviceTrackerMap;

}