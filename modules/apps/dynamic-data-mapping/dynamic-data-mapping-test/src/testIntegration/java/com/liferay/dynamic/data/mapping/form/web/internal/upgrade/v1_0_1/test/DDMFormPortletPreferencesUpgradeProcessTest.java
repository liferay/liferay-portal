/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.upgrade.v1_0_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.test.util.DDMFormInstanceTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
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
 * @author Paulo Albuquerque
 */
@RunWith(Arquillian.class)
public class DDMFormPortletPreferencesUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		_portletId = LayoutTestUtil.addPortletToLayout(
			_layout, DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM);
	}

	@Test
	public void testUpgrade() throws Exception {
		PortletPreferences portletPreferences =
			LayoutTestUtil.getPortletPreferences(_layout, _portletId);

		portletPreferences.setValue(
			"formInstanceId", String.valueOf(RandomTestUtil.randomLong()));

		_runUpgrade();

		_assertPortletPreferences(Collections.emptyMap());

		DDMFormInstance ddmFormInstance =
			DDMFormInstanceTestUtil.addDDMFormInstance(
				_group, TestPropsValues.getUserId());

		portletPreferences.setValue(
			"formInstanceId",
			String.valueOf(ddmFormInstance.getFormInstanceId()));

		portletPreferences.setValue(
			"groupId", String.valueOf(_group.getGroupId()));

		_portletPreferencesLocalService.updatePreferences(
			PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, _layout.getPlid(), _portletId,
			portletPreferences);

		_assertPortletPreferences(
			HashMapBuilder.put(
				"formInstanceId",
				String.valueOf(ddmFormInstance.getFormInstanceId())
			).put(
				"groupId", String.valueOf(_group.getGroupId())
			).build());

		_runUpgrade();

		DDMStructure ddmStructure = _ddmStructureLocalService.getDDMStructure(
			ddmFormInstance.getStructureId());

		_assertPortletPreferences(
			HashMapBuilder.put(
				"ddmStructureExternalReferenceCode",
				ddmStructure.getExternalReferenceCode()
			).put(
				"formInstanceId",
				String.valueOf(ddmFormInstance.getFormInstanceId())
			).put(
				"groupExternalReferenceCode", _group.getExternalReferenceCode()
			).put(
				"groupId", String.valueOf(_group.getGroupId())
			).build());
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

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator, _CLASS_NAME);

			upgradeProcess.upgrade();

			_multiVMPool.clear();
		}
	}

	private static final String _CLASS_NAME =
		"com.liferay.dynamic.data.mapping.form.web.internal.upgrade.v1_0_1." +
			"DDMFormPortletPreferencesUpgradeProcess";

	@Inject(
		filter = "component.name=com.liferay.dynamic.data.mapping.form.web.internal.upgrade.registry.DDMFormWebUpgradeStepRegistrator"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private MultiVMPool _multiVMPool;

	private String _portletId;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

}