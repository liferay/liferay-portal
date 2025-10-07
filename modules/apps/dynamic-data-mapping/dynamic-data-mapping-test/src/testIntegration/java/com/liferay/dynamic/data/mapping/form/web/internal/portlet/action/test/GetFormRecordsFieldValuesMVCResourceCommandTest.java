/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormInstanceRecordTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormInstanceTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.ByteArrayOutputStream;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class GetFormRecordsFieldValuesMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		ReflectionTestUtil.setFieldValue(
			_mvcResourceCommand, "_ddmFormInstanceLocalService",
			_ddmFormInstanceLocalService);
		ReflectionTestUtil.setFieldValue(
			_mvcResourceCommand, "_ddmFormInstanceRecordLocalService",
			_ddmFormInstanceRecordLocalService);
		ReflectionTestUtil.setFieldValue(
			_mvcResourceCommand, "_portal", _portal);
	}

	@Test
	public void testServeResource() throws Exception {
		DDMFormInstance ddmFormInstance =
			DDMFormInstanceTestUtil.addDDMFormInstance(
				DDMFormTestUtil.createDDMForm(_FIELD_NAME), _group,
				TestPropsValues.getUserId());

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmFormInstance.getDDMForm(), SetUtil.fromArray(LocaleUtil.US),
			LocaleUtil.US);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createLocalizedDDMFormFieldValue(
				_FIELD_NAME, _FIELD_VALUE));

		DDMFormInstanceRecordTestUtil.addDDMFormInstanceRecord(
			ddmFormInstance, ddmFormValues, _group,
			TestPropsValues.getUserId());

		JSONArray jsonArray = _getJSONArray(
			ddmFormInstance.getFormInstanceId());

		Assert.assertEquals(_FIELD_VALUE, jsonArray.get(0));

		DDMStructure ddmStructure = ddmFormInstance.getStructure();

		DDMForm ddmForm = ddmStructure.getDDMForm();

		Map<String, DDMFormField> ddmFormFieldsMap =
			ddmForm.getDDMFormFieldsMap(false);

		DDMFormField ddmFormField = ddmFormFieldsMap.get(_FIELD_NAME);

		ddmFormField.setFieldReference(RandomTestUtil.randomString());

		_ddmFormInstanceLocalService.updateFormInstance(
			TestPropsValues.getUserId(), ddmFormInstance.getFormInstanceId(),
			ddmFormInstance.getNameMap(), ddmFormInstance.getDescriptionMap(),
			ddmForm, ddmStructure.getDDMFormLayout(),
			ddmFormInstance.getSettingsDDMFormValues(),
			ServiceContextTestUtil.getServiceContext());

		jsonArray = _getJSONArray(ddmFormInstance.getFormInstanceId());

		Assert.assertEquals(_FIELD_VALUE, jsonArray.get(0));
	}

	private JSONArray _getJSONArray(long formInstanceId) throws Exception {
		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		mockLiferayResourceRequest.addParameter("fieldName", _FIELD_NAME);
		mockLiferayResourceRequest.addParameter(
			"formInstanceId", String.valueOf(formInstanceId));

		MockLiferayResourceResponse mockLiferayResourceResponse =
			new MockLiferayResourceResponse();

		_mvcResourceCommand.serveResource(
			mockLiferayResourceRequest, mockLiferayResourceResponse);

		ByteArrayOutputStream byteArrayOutputStream =
			(ByteArrayOutputStream)
				mockLiferayResourceResponse.getPortletOutputStream();

		return JSONFactoryUtil.createJSONArray(
			byteArrayOutputStream.toString());
	}

	private static final String _FIELD_NAME = RandomTestUtil.randomString();

	private static final String _FIELD_VALUE = RandomTestUtil.randomString();

	@Inject
	private DDMFormInstanceLocalService _ddmFormInstanceLocalService;

	@Inject
	private DDMFormInstanceRecordLocalService
		_ddmFormInstanceRecordLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "mvc.command.name=/dynamic_data_mapping_form/get_form_records_field_values"
	)
	private MVCResourceCommand _mvcResourceCommand;

	@Inject
	private Portal _portal;

}