/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.definitions.portlet.action;

import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.web.internal.display.context.helper.ObjectRequestHelper;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Richard Jeremias
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ObjectPortletKeys.OBJECT_DEFINITIONS,
		"mvc.command.name=/object_definitions/get_view_object_relationships_url"
	},
	service = MVCResourceCommand.class
)
public class GetViewObjectRelationshipsURLMVCResourceCommand
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

		_objectRequestHelper = new ObjectRequestHelper(
			_portal.getHttpServletRequest(resourceRequest));

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			JSONUtil.put(
				"url",
				PortletURLBuilder.create(
					PortletURLUtil.clone(
						PortletURLUtil.getCurrent(
							_objectRequestHelper.getLiferayPortletRequest(),
							_objectRequestHelper.getLiferayPortletResponse()),
						_objectRequestHelper.getLiferayPortletResponse())
				).setParameter(
					"objectDefinitionId",
					objectDefinition.getObjectDefinitionId()
				).buildString()));
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	private ObjectRequestHelper _objectRequestHelper;

	@Reference
	private Portal _portal;

}