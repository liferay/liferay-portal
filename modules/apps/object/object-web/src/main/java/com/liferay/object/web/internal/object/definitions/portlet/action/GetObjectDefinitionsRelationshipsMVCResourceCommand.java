/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.definitions.portlet.action;

import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ObjectPortletKeys.OBJECT_DEFINITIONS,
		"mvc.command.name=/object_definitions/get_object_definitions_relationships"
	},
	service = MVCResourceCommand.class
)
public class GetObjectDefinitionsRelationshipsMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		JSONArray objectDefinitionsJSONArray = _jsonFactory.createJSONArray();

		List<ObjectRelationship> objectRelationships =
			_objectRelationshipLocalService.getObjectRelationships(
				ParamUtil.getLong(resourceRequest, "objectDefinitionId"));

		for (ObjectDefinition objectDefinition :
				_objectDefinitionLocalService.getObjectDefinitions(
					_portal.getCompanyId(resourceRequest), true,
					WorkflowConstants.STATUS_APPROVED)) {

			objectDefinitionsJSONArray.put(
				JSONUtil.put(
					"externalReferenceCode",
					objectDefinition.getExternalReferenceCode()
				).put(
					"id", objectDefinition.getObjectDefinitionId()
				).put(
					"label",
					objectDefinition.getLabel(
						_portal.getLocale(resourceRequest))
				).put(
					"related",
					() -> {
						if (ListUtil.exists(
								objectRelationships,
								objectRelationship -> Objects.equals(
									objectRelationship.getObjectDefinitionId2(),
									objectDefinition.
										getObjectDefinitionId()))) {

							return true;
						}

						return null;
					}
				).put(
					"system", objectDefinition.isSystem()
				));
		}

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, objectDefinitionsJSONArray);
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private Portal _portal;

}