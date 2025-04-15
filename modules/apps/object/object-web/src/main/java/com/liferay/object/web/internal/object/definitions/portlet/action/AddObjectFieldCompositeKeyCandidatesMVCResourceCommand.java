/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.definitions.portlet.action;

import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Leite
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ObjectPortletKeys.OBJECT_DEFINITIONS,
		"mvc.command.name=/object_definitions/add_object_field_composite_key_candidates"
	},
	service = MVCResourceCommand.class
)
public class AddObjectFieldCompositeKeyCandidatesMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		List<String> objectFieldLabels = new ArrayList<>();

		long objectDefinitionId = ParamUtil.getLong(
			resourceRequest, "objectDefinitionId");

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectDefinitionId);

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		for (Long objectFieldId :
				ParamUtil.getLongValues(resourceRequest, "objectFieldsIds")) {

			ObjectField objectField = _objectFieldLocalService.fetchObjectField(
				objectFieldId);

			try {
				Table<?> table = _objectFieldLocalService.getTable(
					objectDefinitionId, objectField.getName());

				Column<?, ?> column = table.getColumn(
					objectField.getDBColumnName());

				long count = _objectEntryLocalService.getObjectEntriesCount(
					0, objectDefinition, column.isNotNull());

				if (count == 0) {
					continue;
				}

				objectFieldLabels.add(
					objectField.getLabel(themeDisplay.getLocale()));
			}
			catch (PortalException portalException) {
				SessionErrors.add(resourceRequest, portalException.getClass());

				_log.error(portalException);
			}
		}

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			JSONUtil.put(
				"errorLabel",
				() -> {
					if (objectFieldLabels.isEmpty()) {
						return StringPool.BLANK;
					}

					if (objectFieldLabels.size() > 1) {
						return _language.format(
							_portal.getHttpServletRequest(resourceRequest),
							"the-selected-fields-x-cannot-be-added-to-the-" +
								"unique-composite-key",
							StringUtil.merge(
								objectFieldLabels, StringPool.COMMA_AND_SPACE),
							false);
					}

					return _language.format(
						_portal.getHttpServletRequest(resourceRequest),
						"the-selected-field-x-cannot-be-added-to-the-unique-" +
							"composite-key",
						objectFieldLabels.get(0), false);
				}
			).put(
				"status",
				() -> {
					if (objectFieldLabels.isEmpty()) {
						return "success";
					}

					return "error";
				}
			));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AddObjectFieldCompositeKeyCandidatesMVCResourceCommand.class);

	@Reference
	private Language _language;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private Portal _portal;

}