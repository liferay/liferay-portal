/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.display.template.test.util;

import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.PortletConstants;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.version.Version;
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

/**
 * @author Lourdes Fernández Besada
 */
public abstract class BaseUpgradePortletPreferencesTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		group = GroupTestUtil.addGroup();

		layout = LayoutTestUtil.addTypePortletLayout(group);

		portletId = LayoutTestUtil.addPortletToLayout(layout, getPortletId());
	}

	@Test
	public void testUpgradeWithDisplayStyleGroupIdDifferentGroup()
		throws Exception {

		Group guestGroup = groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.GUEST);

		testUpgrade(
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

		Group companyGroup = groupLocalService.getCompanyGroup(
			TestPropsValues.getCompanyId());

		testUpgrade(
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

		testUpgrade(
			HashMapBuilder.put(
				"displayStyleGroupId",
				String.valueOf(RandomTestUtil.randomLong())
			).build());
	}

	@Test
	public void testUpgradeWithDisplayStyleGroupIdSameGroup() throws Exception {
		testUpgrade(
			HashMapBuilder.put(
				"displayStyleGroupId", String.valueOf(group.getGroupId())
			).build());
	}

	@Test
	public void testUpgradeWithDisplayStyleGroupKeyDifferentGroup()
		throws Exception {

		Group guestGroup = groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.GUEST);

		testUpgrade(
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

		Group companyGroup = groupLocalService.getCompanyGroup(
			TestPropsValues.getCompanyId());

		testUpgrade(
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

		testUpgrade(
			HashMapBuilder.put(
				"displayStyleGroupKey", RandomTestUtil.randomString()
			).build());
	}

	@Test
	public void testUpgradeWithDisplayStyleGroupKeySameGroup()
		throws Exception {

		testUpgrade(
			HashMapBuilder.put(
				"displayStyleGroupKey", group.getGroupKey()
			).build());
	}

	@Test
	public void testUpgradeWithNullDisplayStyleGroup() throws Exception {
		testUpgrade(Collections.emptyMap());
	}

	protected void assertPortletPreferences(Map<String, String> expectedMap)
		throws Exception {

		_assertPortletPreferences(
			expectedMap,
			LayoutTestUtil.getPortletPreferences(layout, portletId));
	}

	protected void assertPortletPreferences(
		Map<String, String> expectedMap, long ownerId, int ownerType,
		long plid) {

		PortletPreferences portletPreferences =
			_portletPreferencesLocalService.getPreferences(
				group.getCompanyId(), ownerId, ownerType, plid, portletId);

		_assertPortletPreferences(expectedMap, portletPreferences);
	}

	protected abstract String getPortletId();

	protected UpgradeProcess getUpgradeProcess() {
		UpgradeProcess[] upgradeProcesses = UpgradeTestUtil.getUpgradeSteps(
			getUpgradeStepRegistrator(), getVersion());

		return upgradeProcesses[0];
	}

	protected abstract UpgradeStepRegistrator getUpgradeStepRegistrator();

	protected abstract Version getVersion();

	protected void runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = getUpgradeProcess();

		upgradeProcess.upgrade();

		entityCache.clearCache();
		multiVMPool.clear();
	}

	protected void testUpgrade(Map<String, String> map) throws Exception {
		testUpgrade(map, map);
	}

	protected void testUpgrade(
			Map<String, String> expectedMap, Map<String, String> map)
		throws Exception {

		updateLayoutPortletPreference(map);

		runUpgrade();

		assertPortletPreferences(expectedMap);
	}

	protected void testUpgrade(
			Map<String, String> expectedMap, Map<String, String> map,
			int ownerType, long plid)
		throws Exception {

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.getLayoutPortletSetup(
				group.getCompanyId(), group.getGroupId(), ownerType, plid,
				portletId, PortletConstants.DEFAULT_PREFERENCES);

		for (Map.Entry<String, String> entry : map.entrySet()) {
			portletPreferences.setValue(entry.getKey(), entry.getValue());
		}

		portletPreferences.store();

		assertPortletPreferences(map, group.getGroupId(), ownerType, plid);

		runUpgrade();

		assertPortletPreferences(
			expectedMap, group.getGroupId(), ownerType, plid);
	}

	protected void updateLayoutPortletPreference(
			Map<String, String> portletPreferencesMap)
		throws Exception {

		LayoutTestUtil.updateLayoutPortletPreferences(
			layout, portletId, portletPreferencesMap);

		assertPortletPreferences(portletPreferencesMap);
	}

	@Inject
	protected EntityCache entityCache;

	@DeleteAfterTestRun
	protected Group group;

	@Inject
	protected GroupLocalService groupLocalService;

	protected Layout layout;

	@Inject
	protected MultiVMPool multiVMPool;

	protected String portletId;

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

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

}