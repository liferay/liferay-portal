/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.definitions.portlet.action;

import com.liferay.object.admin.rest.dto.v1_0.ObjectDefinition;
import com.liferay.object.admin.rest.resource.v1_0.ObjectDefinitionResource;
import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.object.web.internal.object.definitions.portlet.action.util.ExportImportObjectDefinitionUtil;
import com.liferay.object.web.internal.util.JSONObjectSanitizerUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
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
 * @author Marco Leo
 * @author Gabriel Albuquerque
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ObjectPortletKeys.OBJECT_DEFINITIONS,
		"mvc.command.name=/object_definitions/export_object_definition"
	},
	service = MVCResourceCommand.class
)
public class ExportObjectDefinitionMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ObjectDefinitionResource.Builder builder =
			_objectDefinitionResourceFactory.create();

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		ObjectDefinitionResource objectDefinitionResource = builder.user(
			themeDisplay.getUser()
		).build();

		long objectDefinitionId = ParamUtil.getLong(
			resourceRequest, "objectDefinitionId");

		ObjectDefinition objectDefinition =
			objectDefinitionResource.getObjectDefinition(objectDefinitionId);

		ExportImportObjectDefinitionUtil.prepareObjectDefinitionForExport(
			_jsonFactory, objectDefinition);

		JSONObject objectDefinitionJSONObject = _jsonFactory.createJSONObject(
			objectDefinition.toString());

		if (!FeatureFlagManagerUtil.isEnabled("LPS-135430")) {
			objectDefinitionJSONObject.remove("storageType");
		}

		JSONObjectSanitizerUtil.sanitize(
			objectDefinitionJSONObject,
			new String[] {
				"dateCreated", "dateModified", "id", "listTypeDefinitionId",
				"objectDefinitionId", "objectDefinitionId1",
				"objectDefinitionId2", "objectFieldId", "objectRelationshipId",
				"titleObjectFieldId"
			});

		String objectDefinitionJSON = objectDefinitionJSONObject.toString();

		PortletResponseUtil.sendFile(
			resourceRequest, resourceResponse,
			StringBundler.concat(
				"Object_Definition_", objectDefinition.getName(),
				StringPool.UNDERLINE, objectDefinitionId, StringPool.UNDERLINE,
				Time.getTimestamp(), ".json"),
			objectDefinitionJSON.getBytes(), ContentTypes.APPLICATION_JSON);
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ObjectDefinitionResource.Factory _objectDefinitionResourceFactory;

}