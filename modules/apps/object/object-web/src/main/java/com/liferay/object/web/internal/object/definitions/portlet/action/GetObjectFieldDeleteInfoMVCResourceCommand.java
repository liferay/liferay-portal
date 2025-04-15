/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.definitions.portlet.action;

import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.object.constants.ObjectValidationRuleSettingConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectValidationRuleSettingLocalService;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ObjectPortletKeys.OBJECT_DEFINITIONS,
		"mvc.command.name=/object_definitions/get_object_field_delete_info"
	},
	service = MVCResourceCommand.class
)
public class GetObjectFieldDeleteInfoMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ObjectField objectField = _objectFieldLocalService.fetchObjectField(
			ParamUtil.getLong(resourceRequest, "objectFieldId"));

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectField.getObjectDefinitionId());

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			JSONUtil.put(
				"deleteLastPublishedObjectDefinitionObjectField",
				_deleteLastPublishedObjectDefinitionObjectField(
					objectDefinition, objectField)
			).put(
				"deleteObjectFieldObjectValidationRuleSetting",
				_deleteObjectFieldObjectValidationRuleSetting(objectField)
			).put(
				"showObjectFieldDeletionConfirmationModal",
				() -> {
					if (objectDefinition.isApproved() &&
						_deleteLastPublishedObjectDefinitionObjectField(
							objectDefinition, objectField) &&
						_deleteObjectFieldObjectValidationRuleSetting(
							objectField)) {

						return true;
					}

					return false;
				}
			));
	}

	private boolean _deleteLastPublishedObjectDefinitionObjectField(
		ObjectDefinition objectDefinition, ObjectField objectField) {

		if (!objectDefinition.isApproved() || objectDefinition.isSystem()) {
			return true;
		}

		int customObjectFieldsCount =
			_objectFieldLocalService.getObjectFieldsCount(
				objectField.getObjectDefinitionId(), false);

		if (customObjectFieldsCount <= 1) {
			return false;
		}

		return true;
	}

	private boolean _deleteObjectFieldObjectValidationRuleSetting(
		ObjectField objectField) {

		int count =
			_objectValidationRuleSettingLocalService.
				getObjectValidationRuleSettingsCount(
					ObjectValidationRuleSettingConstants.
						NAME_COMPOSITE_KEY_OBJECT_FIELD_ID,
					String.valueOf(objectField.getObjectFieldId()));

		if (count > 0) {
			return false;
		}

		return true;
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectValidationRuleSettingLocalService
		_objectValidationRuleSettingLocalService;

}