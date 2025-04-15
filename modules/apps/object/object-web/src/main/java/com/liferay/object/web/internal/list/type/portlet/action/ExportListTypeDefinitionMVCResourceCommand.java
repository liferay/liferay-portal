/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.list.type.portlet.action;

import com.liferay.headless.admin.list.type.dto.v1_0.ListTypeDefinition;
import com.liferay.headless.admin.list.type.resource.v1_0.ListTypeDefinitionResource;
import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.object.web.internal.util.JSONObjectSanitizerUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Murilo Stodolni
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ObjectPortletKeys.LIST_TYPE_DEFINITIONS,
		"mvc.command.name=/list_type_definitions/export_list_type_definition"
	},
	service = MVCResourceCommand.class
)
public class ExportListTypeDefinitionMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ListTypeDefinitionResource.Builder builder =
			_listTypeDefinitionResourceFactory.create();

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		ListTypeDefinitionResource listTypeDefinitionResource = builder.user(
			themeDisplay.getUser()
		).build();

		long listTypeDefinitionId = ParamUtil.getLong(
			resourceRequest, "listTypeDefinitionId");

		ListTypeDefinition listTypeDefinition =
			listTypeDefinitionResource.getListTypeDefinition(
				listTypeDefinitionId);

		JSONObject listTypeDefinitionJSONObject = _jsonFactory.createJSONObject(
			listTypeDefinition.toString());

		JSONObjectSanitizerUtil.sanitize(
			listTypeDefinitionJSONObject,
			new String[] {"actions", "dateCreated", "dateModified", "id"});

		String listTypeDefinitionJSON = String.valueOf(
			listTypeDefinitionJSONObject);

		PortletResponseUtil.sendFile(
			resourceRequest, resourceResponse,
			StringBundler.concat(
				"ListType_", listTypeDefinition.getName(), StringPool.UNDERLINE,
				listTypeDefinitionId, StringPool.UNDERLINE, Time.getTimestamp(),
				".json"),
			listTypeDefinitionJSON.getBytes(), ContentTypes.APPLICATION_JSON);
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ListTypeDefinitionResource.Factory
		_listTypeDefinitionResourceFactory;

}