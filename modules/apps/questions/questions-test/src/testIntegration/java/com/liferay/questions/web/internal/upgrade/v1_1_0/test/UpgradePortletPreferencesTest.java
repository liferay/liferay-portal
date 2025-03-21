/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.questions.web.internal.upgrade.v1_1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.service.MBCategoryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.questions.web.internal.constants.QuestionsPortletKeys;

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
 * @author Marco Galluzzi
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

		_portletId = LayoutTestUtil.addPortletToLayout(
			_layout, QuestionsPortletKeys.QUESTIONS);
	}

	@Test
	public void testUpgradeWithDefaultTopicRootId() throws Exception {
		_testUpgrade(
			StringPool.BLANK, MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID);
	}

	@Test
	public void testUpgradeWithInvalidTopicRootId() throws Exception {
		_testUpgrade(StringPool.BLANK, 1);
	}

	@Test
	public void testUpgradeWithoutTopicRootId() throws Exception {
		_testUpgrade(Collections.emptyMap(), Collections.emptyMap());
	}

	@Test
	public void testUpgradeWithValidTopicRootId() throws Exception {
		MBCategory mbCategory = _mbCategoryLocalService.addCategory(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			RandomTestUtil.randomString(), StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));

		_testUpgrade(
			mbCategory.getExternalReferenceCode(), mbCategory.getCategoryId());
	}

	private void _assertPortletPreferences(Map<String, String> expectedMap)
		throws Exception {

		PortletPreferences portletPreferences =
			LayoutTestUtil.getPortletPreferences(_layout, _portletId);

		Map<String, String[]> map = portletPreferences.getMap();

		Assert.assertEquals(
			MapUtil.toString(map), expectedMap.size(), map.size());

		for (Map.Entry<String, String> entry : expectedMap.entrySet()) {
			Assert.assertTrue(entry.getKey(), map.containsKey(entry.getKey()));

			Assert.assertArrayEquals(
				new String[] {entry.getValue()}, map.get(entry.getKey()));
		}
	}

	private void _runUpgrade() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.OFF)) {

			UpgradeProcess[] upgradeProcesses = UpgradeTestUtil.getUpgradeSteps(
				_upgradeStepRegistrator, new Version(1, 1, 0));

			upgradeProcesses[0].upgrade();

			_entityCache.clearCache();
			_multiVMPool.clear();
		}
	}

	private void _testUpgrade(
			Map<String, String> expectedMap, Map<String, String> map)
		throws Exception {

		LayoutTestUtil.updateLayoutPortletPreferences(_layout, _portletId, map);

		_assertPortletPreferences(map);

		_runUpgrade();

		_assertPortletPreferences(expectedMap);
	}

	private void _testUpgrade(String externalReferenceCode, long rootTopicId)
		throws Exception {

		_testUpgrade(
			HashMapBuilder.put(
				"rootTopicExternalReferenceCode", externalReferenceCode
			).put(
				"rootTopicId", String.valueOf(rootTopicId)
			).build(),
			HashMapBuilder.put(
				"rootTopicId", String.valueOf(rootTopicId)
			).build());
	}

	private static final String _CLASS_NAME =
		"com.liferay.questions.web.internal.upgrade.v1_1_0." +
			"UpgradePortletPreferences";

	@Inject
	private EntityCache _entityCache;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private MBCategoryLocalService _mbCategoryLocalService;

	@Inject
	private MultiVMPool _multiVMPool;

	private String _portletId;

	@Inject(
		filter = "(&(component.name=com.liferay.questions.web.internal.upgrade.registry.QuestionsWebUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}