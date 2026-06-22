/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.definitions.portlet.action;

import com.liferay.object.admin.rest.dto.v1_0.ObjectDefinition;
import com.liferay.object.admin.rest.dto.v1_0.util.ObjectDefinitionUtil;
import com.liferay.object.admin.rest.resource.v1_0.ObjectDefinitionResource;
import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.tree.Node;
import com.liferay.object.tree.ObjectDefinitionTreeFactory;
import com.liferay.object.tree.Tree;
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

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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

		JSONArray jsonArray = _jsonFactory.createJSONArray();
		Set<Long> objectDefinitionIds = new HashSet<>();

		long objectDefinitionId = ParamUtil.getLong(
			resourceRequest, "objectDefinitionId");
		ObjectDefinitionResource objectDefinitionResource =
			_getObjectDefinitionResource(resourceRequest);

		Iterator<Node> iterator = _getTreeIterator(
			objectDefinitionId, objectDefinitionResource);

		while (iterator.hasNext()) {
			Node node = iterator.next();

			if (!objectDefinitionIds.add(node.getPrimaryKey())) {
				continue;
			}

			ObjectDefinition objectDefinition =
				objectDefinitionResource.getObjectDefinition(
					node.getPrimaryKey());

			ObjectDefinitionUtil.prepareObjectDefinitionForExport(
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

	private ObjectDefinitionResource _getObjectDefinitionResource(
		ResourceRequest resourceRequest) {

		ObjectDefinitionResource.Builder builder =
			_objectDefinitionResourceFactory.create();

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return builder.user(
			themeDisplay.getUser()
		).build();
	}

	private Iterator<Node> _getTreeIterator(
			long objectDefinitionId,
			ObjectDefinitionResource objectDefinitionResource)
		throws Exception {

		ObjectDefinitionTreeFactory objectDefinitionTreeFactory =
			new ObjectDefinitionTreeFactory(
				_objectDefinitionLocalService, _objectRelationshipLocalService);

		ObjectDefinition rootObjectDefinition =
			objectDefinitionResource.getObjectDefinition(objectDefinitionId);

		Tree tree = objectDefinitionTreeFactory.create(
			rootObjectDefinition.getId());

		return tree.iterator();
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectDefinitionResource.Factory _objectDefinitionResourceFactory;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

}