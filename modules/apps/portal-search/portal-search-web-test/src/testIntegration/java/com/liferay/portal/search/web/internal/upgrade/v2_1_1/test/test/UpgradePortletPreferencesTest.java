/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.upgrade.v2_1_1.test.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.search.web.constants.SearchBarPortletKeys;
import com.liferay.portal.search.web.constants.SearchResultsPortletKeys;
import com.liferay.portal.search.web.internal.category.facet.constants.CategoryFacetPortletKeys;
import com.liferay.portal.search.web.internal.custom.facet.constants.CustomFacetPortletKeys;
import com.liferay.portal.search.web.internal.custom.filter.constants.CustomFilterPortletKeys;
import com.liferay.portal.search.web.internal.folder.facet.constants.FolderFacetPortletKeys;
import com.liferay.portal.search.web.internal.modified.facet.constants.ModifiedFacetPortletKeys;
import com.liferay.portal.search.web.internal.site.facet.constants.SiteFacetPortletKeys;
import com.liferay.portal.search.web.internal.sort.constants.SortPortletKeys;
import com.liferay.portal.search.web.internal.tag.facet.constants.TagFacetPortletKeys;
import com.liferay.portal.search.web.internal.type.facet.constants.TypeFacetPortletKeys;
import com.liferay.portal.search.web.internal.user.facet.constants.UserFacetPortletKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import jakarta.portlet.PortletPreferences;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Joshua Cords
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

		_portletIds = new String[_PORTLET_KEYS.length];

		for (int i = 0; i < _PORTLET_KEYS.length; i++) {
			_portletIds[i] = LayoutTestUtil.addPortletToLayout(
				_layout, _PORTLET_KEYS[i]);
		}
	}

	@Test
	public void testUpgradeWithDisplayStyleGroupIdDifferentGroup()
		throws Exception {

		Group guestGroup = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.GUEST);

		_testUpgrade(
			HashMapBuilder.put(
				"displayStyleGroupExternalReferenceCode",
				guestGroup.getExternalReferenceCode()
			).put(
				"displayStyleGroupId", String.valueOf(guestGroup.getGroupId())
			).build(),
			HashMapBuilder.put(
				"displayStyleGroupId", String.valueOf(guestGroup.getGroupId())
			).build());
	}

	@Test
	public void testUpgradeWithDisplayStyleGroupIdGlobalGroup()
		throws Exception {

		Group companyGroup = _groupLocalService.getCompanyGroup(
			TestPropsValues.getCompanyId());

		_testUpgrade(
			HashMapBuilder.put(
				"displayStyleGroupExternalReferenceCode",
				companyGroup.getExternalReferenceCode()
			).put(
				"displayStyleGroupId", String.valueOf(companyGroup.getGroupId())
			).build(),
			HashMapBuilder.put(
				"displayStyleGroupId", String.valueOf(companyGroup.getGroupId())
			).build());
	}

	@Test
	public void testUpgradeWithDisplayStyleGroupIdMissingGroup()
		throws Exception {

		_testUpgrade(
			HashMapBuilder.put(
				"displayStyleGroupId",
				String.valueOf(RandomTestUtil.randomLong())
			).build());
	}

	@Test
	public void testUpgradeWithDisplayStyleGroupIdSameGroup() throws Exception {
		_testUpgrade(
			HashMapBuilder.put(
				"displayStyleGroupId", String.valueOf(_group.getGroupId())
			).build());
	}

	@Test
	public void testUpgradeWithDisplayStyleGroupKeyDifferentGroup()
		throws Exception {

		Group guestGroup = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.GUEST);

		_testUpgrade(
			HashMapBuilder.put(
				"displayStyleGroupExternalReferenceCode",
				guestGroup.getExternalReferenceCode()
			).put(
				"displayStyleGroupKey", guestGroup.getGroupKey()
			).build(),
			HashMapBuilder.put(
				"displayStyleGroupKey", guestGroup.getGroupKey()
			).build());
	}

	@Test
	public void testUpgradeWithDisplayStyleGroupKeyGlobalGroup()
		throws Exception {

		Group companyGroup = _groupLocalService.getCompanyGroup(
			TestPropsValues.getCompanyId());

		_testUpgrade(
			HashMapBuilder.put(
				"displayStyleGroupExternalReferenceCode",
				companyGroup.getExternalReferenceCode()
			).put(
				"displayStyleGroupKey", companyGroup.getGroupKey()
			).build(),
			HashMapBuilder.put(
				"displayStyleGroupKey", companyGroup.getGroupKey()
			).build());
	}

	@Test
	public void testUpgradeWithDisplayStyleGroupKeyMissingGroup()
		throws Exception {

		_testUpgrade(
			HashMapBuilder.put(
				"displayStyleGroupKey", RandomTestUtil.randomString()
			).build());
	}

	@Test
	public void testUpgradeWithDisplayStyleGroupKeySameGroup()
		throws Exception {

		_testUpgrade(
			HashMapBuilder.put(
				"displayStyleGroupKey", _group.getGroupKey()
			).build());
	}

	@Test
	public void testUpgradeWithNullDisplayStyleGroup() throws Exception {
		_testUpgrade(Collections.emptyMap());
	}

	private void _assertPortletPreferences(
		Map<String, String> expectedMap,
		PortletPreferences portletPreferences) {

		Map<String, String[]> map = portletPreferences.getMap();

		Assert.assertEquals(
			MapUtil.toString(map), expectedMap.size(), map.size());

		for (Map.Entry<String, String> entry : expectedMap.entrySet()) {
			Assert.assertTrue(entry.getKey(), map.containsKey(entry.getKey()));

			Assert.assertArrayEquals(
				new String[] {entry.getValue()}, map.get(entry.getKey()));
		}
	}

	private void _assertPortletPreferences(
			Map<String, String> expectedMap, String portletId)
		throws Exception {

		_assertPortletPreferences(
			expectedMap,
			LayoutTestUtil.getPortletPreferences(_layout, portletId));
	}

	private UpgradeProcess _getUpgradeProcess() {
		UpgradeProcess[] upgradeProcesses = UpgradeTestUtil.getUpgradeSteps(
			_upgradeStepRegistrator, new Version(2, 1, 1));

		return upgradeProcesses[0];
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = _getUpgradeProcess();

		upgradeProcess.upgrade();

		_entityCache.clearCache();
		_multiVMPool.clear();
	}

	private void _testUpgrade(Map<String, String> map) throws Exception {
		_testUpgrade(map, map);
	}

	private void _testUpgrade(
			Map<String, String> expectedMap, Map<String, String> map)
		throws Exception {

		for (String portletId : _portletIds) {
			_updateLayoutPortletPreference(portletId, map);
		}

		_runUpgrade();

		for (String portletId : _portletIds) {
			_assertPortletPreferences(expectedMap, portletId);
		}
	}

	private void _updateLayoutPortletPreference(
			String portletId, Map<String, String> portletPreferencesMap)
		throws Exception {

		LayoutTestUtil.updateLayoutPortletPreferences(
			_layout, portletId, portletPreferencesMap);

		_assertPortletPreferences(portletPreferencesMap, portletId);
	}

	private static final String[] _PORTLET_KEYS = {
		CategoryFacetPortletKeys.CATEGORY_FACET + "_INSTANCE_%",
		CustomFacetPortletKeys.CUSTOM_FACET + "_INSTANCE_%",
		CustomFilterPortletKeys.CUSTOM_FILTER + "_INSTANCE_%",
		FolderFacetPortletKeys.FOLDER_FACET + "_INSTANCE_%",
		ModifiedFacetPortletKeys.MODIFIED_FACET + "_INSTANCE_%",
		SearchBarPortletKeys.SEARCH_BAR + "_INSTANCE_%",
		SearchResultsPortletKeys.SEARCH_RESULTS + "_INSTANCE_%",
		SiteFacetPortletKeys.SITE_FACET + "_INSTANCE_%",
		SortPortletKeys.SORT + "_INSTANCE_%",
		TagFacetPortletKeys.TAG_FACET + "_INSTANCE_%",
		TypeFacetPortletKeys.TYPE_FACET + "_INSTANCE_%",
		UserFacetPortletKeys.USER_FACET + "_INSTANCE_%"
	};

	@Inject(
		filter = "component.name=com.liferay.portal.search.web.internal.upgrade.registry.SearchWebUpgradeStepRegistrator"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private EntityCache _entityCache;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	private Layout _layout;

	@Inject
	private MultiVMPool _multiVMPool;

	private String[] _portletIds;

}