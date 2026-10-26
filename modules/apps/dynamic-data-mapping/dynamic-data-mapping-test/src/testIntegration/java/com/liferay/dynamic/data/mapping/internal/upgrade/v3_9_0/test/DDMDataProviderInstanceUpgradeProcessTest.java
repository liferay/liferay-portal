/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.v3_9_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance;
import com.liferay.dynamic.data.mapping.service.DDMDataProviderInstanceLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Chaitanya Sammetla
 */
@RunWith(Arquillian.class)
public class DDMDataProviderInstanceUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testUpgrade() throws Exception {
		_testUpgrade("$..label;$..value", "$..label;$..value");
		_testUpgrade("$..nameCurrentValue", "nameCurrentValue;List");
		_testUpgrade("name", "name;Text");
	}

	private void _testUpgrade(
			String expectedOutputParameterPath, String outputParameterPath)
		throws Exception {

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			DDMFormTestUtil.createDDMForm());

		DDMFormFieldValue ddmFormFieldValue =
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"outputParameters", StringPool.BLANK);

		ddmFormFieldValue.addNestedDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"outputParameterPath", outputParameterPath));

		ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);

		DDMDataProviderInstance ddmDataProviderInstance =
			_ddmDataProviderInstanceLocalService.addDataProviderInstance(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomLocaleStringMap(), null, ddmFormValues,
				"rest",
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_ddmDataProviderInstances.add(ddmDataProviderInstance);

		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.dynamic.data.mapping.internal.upgrade.v3_9_0." +
				"DDMDataProviderInstanceUpgradeProcess");

		upgradeProcess.upgrade();

		EntityCacheUtil.clearCache();

		ddmDataProviderInstance =
			_ddmDataProviderInstanceLocalService.getDataProviderInstance(
				ddmDataProviderInstance.getDataProviderInstanceId());

		Assert.assertEquals(
			expectedOutputParameterPath,
			JSONUtil.getValue(
				JSONFactoryUtil.createJSONObject(
					ddmDataProviderInstance.getDefinition()),
				"JSONArray/fieldValues", "Object/0",
				"JSONArray/nestedFieldValues", "Object/0", "Object/value"));
	}

	@Inject
	private DDMDataProviderInstanceLocalService
		_ddmDataProviderInstanceLocalService;

	@DeleteAfterTestRun
	private final List<DDMDataProviderInstance> _ddmDataProviderInstances =
		new ArrayList<>();

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "(&(component.name=com.liferay.dynamic.data.mapping.internal.upgrade.registry.DDMServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}