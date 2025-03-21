/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
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
import com.liferay.portal.kernel.json.JSONArray;
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
import com.liferay.portal.vulcan.pagination.Page;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.Collection;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Guilherme Sá
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ObjectPortletKeys.OBJECT_DEFINITIONS,
		"mvc.command.name=/object_definitions/export_bound_object_definitions"
	},
	service = MVCResourceCommand.class
)
public class ExportBoundObjectDefinitionsMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-34594")) {
			throw new UnsupportedOperationException();
		}

		ObjectDefinitionResource.Builder builder =
			_objectDefinitionResourceFactory.create();

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		ObjectDefinitionResource objectDefinitionResource = builder.user(
			themeDisplay.getUser()
		).build();

		long objectDefinitionId = ParamUtil.getLong(
			resourceRequest, "objectDefinitionId");

		ObjectDefinition rootObjectDefinition =
			objectDefinitionResource.getObjectDefinition(objectDefinitionId);

		Page<ObjectDefinition> page =
			objectDefinitionResource.getObjectDefinitionsPage(
				null, null,
				objectDefinitionResource.toFilter(
					StringBundler.concat(
						"rootObjectDefinitionExternalReferenceCode eq '",
						rootObjectDefinition.getExternalReferenceCode(), "'")),
				null, null);

		Collection<ObjectDefinition> objectDefinitions = page.getItems();

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		for (ObjectDefinition objectDefinition : objectDefinitions) {
			ExportImportObjectDefinitionUtil.prepareObjectDefinitionForExport(
				_jsonFactory, objectDefinition);

			JSONObject jsonObject = _jsonFactory.createJSONObject(
				objectDefinition.toString());

			if (!FeatureFlagManagerUtil.isEnabled("LPS-135430")) {
				jsonObject.remove("storageType");
			}

			JSONObjectSanitizerUtil.sanitize(
				jsonObject,
				new String[] {
					"dateCreated", "dateModified", "id", "listTypeDefinitionId",
					"objectDefinitionId", "objectDefinitionId1",
					"objectDefinitionId2", "objectFieldId",
					"objectRelationshipId", "titleObjectFieldId"
				});

			jsonArray.put(jsonObject);
		}

		String json = jsonArray.toString();

		PortletResponseUtil.sendFile(
			resourceRequest, resourceResponse,
			StringBundler.concat(
				"Bound_Object_Definitions_", objectDefinitionId,
				StringPool.UNDERLINE, Time.getTimestamp(), ".json"),
			json.getBytes(), ContentTypes.APPLICATION_JSON);
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ObjectDefinitionResource.Factory _objectDefinitionResourceFactory;

}