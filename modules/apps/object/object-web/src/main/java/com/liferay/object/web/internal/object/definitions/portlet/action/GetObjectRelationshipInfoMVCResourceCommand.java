/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.definitions.portlet.action;

import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.relationship.util.ObjectRelationshipUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
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
 * @author Murilo Stodolni
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ObjectPortletKeys.OBJECT_DEFINITIONS,
		"mvc.command.name=/object_definitions/get_object_relationship_info"
	},
	service = MVCResourceCommand.class
)
public class GetObjectRelationshipInfoMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				ParamUtil.getLong(resourceRequest, "objectDefinitionId"));

		if (objectDefinition == null) {
			return;
		}

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			JSONUtil.put(
				"objectRelationshipTypes",
				ObjectRelationshipUtil.getObjectRelationshipTypes(
					objectDefinition, _systemObjectDefinitionManagerRegistry)
			).put(
				"parameterRequired",
				ObjectRelationshipUtil.isParameterRequired(
					objectDefinition, _systemObjectDefinitionManagerRegistry)
			).put(
				"restContextPath",
				ObjectRelationshipUtil.getRESTContextPath(
					objectDefinition, _systemObjectDefinitionManagerRegistry)
			));
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

}