/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.upgrade.v3_10_2.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.test.util.BaseCTUpgradeProcessTestCase;
import com.liferay.dynamic.data.mapping.model.DDMContent;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion;
import com.liferay.dynamic.data.mapping.service.DDMContentLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordVersionLocalService;
import com.liferay.dynamic.data.mapping.test.util.DDMFormInstanceRecordTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormInstanceTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author David Truong
 */
@RunWith(Arquillian.class)
public class DDMContentCTUpgradeProcessTest
	extends BaseCTUpgradeProcessTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_ddmForm = DDMFormTestUtil.createDDMForm();

		DDMFormField fieldSetDDMFormField = DDMFormTestUtil.createDDMFormField(
			"fieldset", "fieldset", "fieldset", "", false, false, false);

		fieldSetDDMFormField.addNestedDDMFormField(
			DDMFormTestUtil.createTextDDMFormField(
				"field1", false, false, false));
		fieldSetDDMFormField.addNestedDDMFormField(
			DDMFormTestUtil.createTextDDMFormField(
				"field2", false, false, false));
		fieldSetDDMFormField.setProperty(
			"rows",
			JSONUtil.putAll(
				JSONUtil.put(
					"columns",
					JSONUtil.put(
						JSONUtil.put(
							"fields", JSONUtil.put("field1")
						).put(
							"size", 12
						))),
				JSONUtil.put(
					"columns",
					JSONUtil.put(
						JSONUtil.put(
							"fields", JSONUtil.put("field2")
						).put(
							"size", 12
						)))));

		_ddmForm.addDDMFormField(fieldSetDDMFormField);

		_ddmFormInstance = DDMFormInstanceTestUtil.addDDMFormInstance(
			_ddmForm, _group, TestPropsValues.getUserId());

		_ddmFormInstanceRecord =
			DDMFormInstanceRecordTestUtil.addDDMFormInstanceRecord(
				_ddmFormInstance,
				DDMFormValuesTestUtil.createDDMFormValues(_ddmForm), _group,
				TestPropsValues.getUserId());
	}

	@Override
	protected CTModel<?> addCTModel() throws Exception {
		JSONObject jsonObject = JSONUtil.put(
			"name", RandomTestUtil.randomString());

		JSONArray jsonArray = JSONUtil.putAll(
			JSONUtil.put(
				"fieldReference", RandomTestUtil.randomString()
			).put(
				"instanceId", RandomTestUtil.randomString()
			).put(
				"name", RandomTestUtil.randomString()
			));

		jsonObject.put("fieldValues", jsonArray);

		DDMContent ddmContent = _ddmContentLocalService.createDDMContent(
			RandomTestUtil.nextLong());

		ddmContent.setGroupId(_group.getGroupId());
		ddmContent.setCompanyId(_group.getCompanyId());
		ddmContent.setUserId(TestPropsValues.getUserId());
		ddmContent.setName(RandomTestUtil.randomString());
		ddmContent.setDescription(RandomTestUtil.randomString());
		ddmContent.setData(jsonObject.toString());

		ddmContent = _ddmContentLocalService.addDDMContent(ddmContent);

		DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion =
			_ddmFormInstanceRecord.getFormInstanceRecordVersion();

		ddmFormInstanceRecordVersion.setStorageId(ddmContent.getContentId());

		_ddmFormInstanceRecordVersionLocalService.
			updateDDMFormInstanceRecordVersion(ddmFormInstanceRecordVersion);

		return ddmContent;
	}

	@Override
	protected CTService<?> getCTService() {
		return _ddmContentLocalService;
	}

	@Override
	protected void runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();
	}

	@Override
	protected CTModel<?> updateCTModel(CTModel<?> ctModel) throws Exception {
		DDMContent ddmContent = (DDMContent)ctModel;

		ddmContent.setData(
			JSONUtil.put(
				"fieldValues",
				JSONUtil.putAll(
					JSONUtil.put(
						"fieldReference", RandomTestUtil.randomString()
					).put(
						"instanceId", RandomTestUtil.randomString()
					).put(
						"name", RandomTestUtil.randomString()
					))
			).put(
				"name", RandomTestUtil.randomString()
			).toString());

		return _ddmContentLocalService.updateDDMContent(ddmContent);
	}

	private static final String _CLASS_NAME =
		"com.liferay.dynamic.data.mapping.internal.upgrade.v3_10_2." +
			"DDMContentUpgradeProcess";

	@Inject
	private DDMContentLocalService _ddmContentLocalService;

	private DDMForm _ddmForm;

	@DeleteAfterTestRun
	private DDMFormInstance _ddmFormInstance;

	@DeleteAfterTestRun
	private DDMFormInstanceRecord _ddmFormInstanceRecord;

	@Inject
	private DDMFormInstanceRecordVersionLocalService
		_ddmFormInstanceRecordVersionLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "(&(component.name=com.liferay.dynamic.data.mapping.internal.upgrade.registry.DDMServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}