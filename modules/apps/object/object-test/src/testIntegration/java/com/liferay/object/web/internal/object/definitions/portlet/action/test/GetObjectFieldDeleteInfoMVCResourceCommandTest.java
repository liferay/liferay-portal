/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.definitions.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectValidationRuleConstants;
import com.liferay.object.constants.ObjectValidationRuleSettingConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectValidationRuleLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.validation.rule.setting.builder.ObjectValidationRuleSettingBuilder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pedro Leite
 */
@RunWith(Arquillian.class)
public class GetObjectFieldDeleteInfoMVCResourceCommandTest
	extends BaseMVCResourceCommandTestCase {

	@Test
	public void testGetObjectFieldDeleteInfo() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		ObjectField objectField1 = _addCustomObjectField(
			objectDefinition.getObjectDefinitionId());
		ObjectField objectField2 = _addCustomObjectField(
			objectDefinition.getObjectDefinitionId());
		ObjectField objectField3 = _addCustomObjectField(
			objectDefinition.getObjectDefinitionId());

		_objectValidationRuleLocalService.addObjectValidationRule(
			StringPool.BLANK, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), true,
			ObjectValidationRuleConstants.ENGINE_TYPE_COMPOSITE_KEY,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION,
			StringPool.BLANK, false,
			Arrays.asList(
				new ObjectValidationRuleSettingBuilder(
				).name(
					ObjectValidationRuleSettingConstants.
						NAME_COMPOSITE_KEY_OBJECT_FIELD_ID
				).value(
					String.valueOf(objectField1.getObjectFieldId())
				).build(),
				new ObjectValidationRuleSettingBuilder(
				).name(
					ObjectValidationRuleSettingConstants.
						NAME_COMPOSITE_KEY_OBJECT_FIELD_ID
				).value(
					String.valueOf(objectField2.getObjectFieldId())
				).build()));

		_testGetObjectFieldDeleteInfo(false, objectField1);
		_testGetObjectFieldDeleteInfo(false, objectField2);
		_testGetObjectFieldDeleteInfo(true, objectField3);
	}

	@Override
	protected MVCResourceCommand getMVCResourceCommand() {
		return _mvcResourceCommand;
	}

	private ObjectField _addCustomObjectField(long objectDefinitionId)
		throws Exception {

		return ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).labelMap(
				RandomTestUtil.randomLocaleStringMap()
			).name(
				"a" + RandomTestUtil.randomString()
			).objectDefinitionId(
				objectDefinitionId
			).userId(
				TestPropsValues.getUserId()
			).build());
	}

	private void _testGetObjectFieldDeleteInfo(
			boolean expectedDeleteObjectFieldObjectValidationRuleSetting,
			ObjectField objectField)
		throws Exception {

		JSONObject jsonObject = getJSONObject(
			"objectFieldId", String.valueOf(objectField.getObjectFieldId()));

		Assert.assertEquals(
			expectedDeleteObjectFieldObjectValidationRuleSetting,
			jsonObject.getBoolean(
				"deleteObjectFieldObjectValidationRuleSetting"));
	}

	@Inject(
		filter = "mvc.command.name=/object_definitions/get_object_field_delete_info"
	)
	private MVCResourceCommand _mvcResourceCommand;

	@Inject
	private ObjectValidationRuleLocalService _objectValidationRuleLocalService;

}