/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 Commenting on lines +1 to +2
 Comment
 Leave a comment
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.navigation.web.internal.upgrade.v1_0_2.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.categories.navigation.constants.AssetCategoriesNavigationPortletKeys;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import jakarta.portlet.PortletPreferences;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class UpgradePortletPreferencesTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypePortletLayout(_group);
	}

	@Test
	public void testUpgradePortletPreferences() throws Exception {
		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		_assertUpgrade(
			HashMapBuilder.put(
				"assetVocabularyIds",
				String.valueOf(assetVocabulary.getVocabularyId())
			).put(
				"displayStyleGroupId", String.valueOf(_group.getGroupId())
			).build(),
			HashMapBuilder.put(
				"assetVocabularyExternalReferenceCodes",
				assetVocabulary.getExternalReferenceCode()
			).put(
				"displayStyleGroupExternalReferenceCode",
				_group.getExternalReferenceCode()
			).build());
	}

	@Test
	public void testUpgradePortletPreferencesWithCompanyAssetVocabulary()
		throws Exception {

		Group companyGroup = _groupLocalService.getCompanyGroup(
			_group.getCompanyId());

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			companyGroup.getGroupId());

		_assertUpgrade(
			HashMapBuilder.put(
				"assetVocabularyIds",
				String.valueOf(assetVocabulary.getVocabularyId())
			).put(
				"displayStyleGroupId", String.valueOf(companyGroup.getGroupId())
			).build(),
			HashMapBuilder.put(
				"assetVocabularyExternalReferenceCodes_" +
					companyGroup.getExternalReferenceCode(),
				assetVocabulary.getExternalReferenceCode()
			).put(
				"assetVocabularyGroupExternalReferenceCodes",
				companyGroup.getExternalReferenceCode()
			).put(
				"displayStyleGroupExternalReferenceCode",
				companyGroup.getExternalReferenceCode()
			).build());
	}

	@Test
	public void testUpgradePortletPreferencesWithSomeAssetVocabularies()
		throws Exception {

		Group companyGroup = _groupLocalService.getCompanyGroup(
			_group.getCompanyId());

		AssetVocabulary assetVocabulary1 = AssetTestUtil.addVocabulary(
			companyGroup.getGroupId());

		AssetVocabulary assetVocabulary2 = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		_assertUpgrade(
			HashMapBuilder.put(
				"assetVocabularyIds",
				StringUtil.merge(
					new Long[] {
						assetVocabulary1.getVocabularyId(),
						assetVocabulary2.getVocabularyId()
					})
			).put(
				"displayStyleGroupId", String.valueOf(companyGroup.getGroupId())
			).build(),
			HashMapBuilder.put(
				"assetVocabularyExternalReferenceCodes_" +
					companyGroup.getExternalReferenceCode(),
				assetVocabulary1.getExternalReferenceCode()
			).put(
				"assetVocabularyExternalReferenceCodes",
				assetVocabulary2.getExternalReferenceCode()
			).put(
				"assetVocabularyGroupExternalReferenceCodes",
				companyGroup.getExternalReferenceCode()
			).put(
				"displayStyleGroupExternalReferenceCode",
				companyGroup.getExternalReferenceCode()
			).build());
	}

	private void _assertPortletPreferences(
			String portletId, Map<String, String> portletPreferencesMap)
		throws Exception {

		PortletPreferences portletPreferences =
			LayoutTestUtil.getPortletPreferences(_layout, portletId);

		for (Map.Entry<String, String> entry :
				portletPreferencesMap.entrySet()) {

			Assert.assertEquals(
				entry.getValue(),
				portletPreferences.getValue(entry.getKey(), null));
		}
	}

	private void _assertUpgrade(
			Map<String, String> actualPortletPreferencesMap,
			Map<String, String> expectedPortletPreferencesMapMap)
		throws Exception {

		String portletId = LayoutTestUtil.addPortletToLayout(
			_layout,
			AssetCategoriesNavigationPortletKeys.ASSET_CATEGORIES_NAVIGATION,
			_getPreferenceMap(actualPortletPreferencesMap));

		_assertPortletPreferences(portletId, actualPortletPreferencesMap);

		_runUpgrade();

		_assertPortletPreferences(portletId, expectedPortletPreferencesMapMap);
	}

	private Map<String, String[]> _getPreferenceMap(Map<String, String> map) {
		Map<String, String[]> preferenceMap = new HashMap<>();

		for (Map.Entry<String, String> entry : map.entrySet()) {
			preferenceMap.put(entry.getKey(), new String[] {entry.getValue()});
		}

		return preferenceMap;
	}

	private void _runUpgrade() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.OFF)) {

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator, _CLASS_NAME);

			upgradeProcess.upgrade();

			_entityCache.clearCache();
			_multiVMPool.clear();
		}
	}

	private static final String _CLASS_NAME =
		"com.liferay.asset.categories.navigation.web.internal.upgrade.v1_0_2." +
			"UpgradePortletPreferences";

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private EntityCache _entityCache;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	private Layout _layout;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private Portal _portal;

	@Inject(
		filter = "(&(component.name=com.liferay.asset.categories.navigation.web.internal.upgrade.registry.AssetCategoriesNavigationWebUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}