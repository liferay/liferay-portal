/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.data.engine.rest.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.dto.v2_0.DataDefinitionField;
import com.liferay.data.engine.rest.resource.v2_0.DataDefinitionResource;
import com.liferay.data.engine.rest.test.util.DataDefinitionTestUtil;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.Inject;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class UpdateDataDefinitionMVCActionCommandTest
	extends BaseDataDefinitionMVCActionCommandTestCase {

	public MVCActionCommand getMVCActionCommand() {
		return _mvcActionCommand;
	}

	@Test
	public void testProcessAction() throws Exception {
		String dataDefinitionJSON = _read(
			"data_definition_with_nested_field.json");

		DataDefinition dataDefinition =
			DataDefinitionTestUtil.addDataDefinition(
				"journal", dataDefinitionResourceFactory, group.getGroupId(),
				dataDefinitionJSON, TestPropsValues.getUser());

		DDMStructure ddmStructure = _ddmStructureLocalService.fetchDDMStructure(
			dataDefinition.getId());

		DDMForm ddmForm = ddmStructure.getDDMForm();

		Map<String, DDMFormField> ddmFormFieldsMap =
			ddmForm.getDDMFormFieldsMap(true);

		DDMFormField ddmFormField = ddmFormFieldsMap.get("Text");

		String predefinedValue = RandomTestUtil.randomString();

		ddmFormField.setPredefinedValue(
			new LocalizedValue() {
				{
					addString(LocaleUtil.US, predefinedValue);
				}
			});

		_ddmStructureLocalService.updateStructure(
			TestPropsValues.getUserId(), ddmStructure.getStructureId(), ddmForm,
			ddmStructure.getDDMFormLayout(),
			ServiceContextTestUtil.getServiceContext());

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			createMockLiferayPortletActionRequest(
				null, null, dataDefinition.getId());

		mockLiferayPortletActionRequest.addParameter(
			"dataDefinition", dataDefinitionJSON);
		mockLiferayPortletActionRequest.addParameter(
			"dataLayout",
			String.valueOf(dataDefinition.getDefaultDataLayout()));
		mockLiferayPortletActionRequest.addParameter(
			"structureKey", dataDefinition.getDataDefinitionKey());

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		DataDefinitionResource.Builder builder =
			dataDefinitionResourceFactory.create();

		DataDefinitionResource dataDefinitionResource = builder.user(
			TestPropsValues.getUser()
		).build();

		dataDefinition = dataDefinitionResource.getDataDefinition(
			dataDefinition.getId());

		DataDefinitionField[] dataDefinitionFields =
			dataDefinition.getDataDefinitionFields();

		Assert.assertEquals(
			dataDefinitionFields.toString(), 1, dataDefinitionFields.length);

		DataDefinitionField dataDefinitionField = dataDefinitionFields[0];

		DataDefinitionField[] nestedDataDefinitionFields =
			dataDefinitionField.getNestedDataDefinitionFields();

		Assert.assertEquals(
			nestedDataDefinitionFields.toString(), 1,
			nestedDataDefinitionFields.length);

		DataDefinitionField nestedDataDefinitionField =
			nestedDataDefinitionFields[0];

		Assert.assertEquals(
			predefinedValue,
			MapUtil.getString(
				nestedDataDefinitionField.getDefaultValue(), "en_US"));
	}

	private String _read(String fileName) throws Exception {
		return new String(
			FileUtil.getBytes(getClass(), "dependencies/" + fileName));
	}

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@Inject(filter = "mvc.command.name=/journal/update_data_definition")
	private MVCActionCommand _mvcActionCommand;

}