/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.definitions.portlet.action;

import com.liferay.object.admin.rest.dto.v1_0.ObjectFolder;
import com.liferay.object.admin.rest.dto.v1_0.ObjectFolderItem;
import com.liferay.object.admin.rest.resource.v1_0.ObjectFolderResource;
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
 * @author Guilherme Sá
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ObjectPortletKeys.OBJECT_DEFINITIONS,
		"mvc.command.name=/object_definitions/export_object_folder"
	},
	service = MVCResourceCommand.class
)
public class ExportObjectFolderMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ObjectFolderResource.Builder builder =
			_objectFolderResourceFactory.create();

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		ObjectFolderResource objectFolderResource = builder.user(
			themeDisplay.getUser()
		).build();

		long objectFolderId = ParamUtil.getLong(
			resourceRequest, "objectFolderId");

		ObjectFolder objectFolder = objectFolderResource.getObjectFolder(
			objectFolderId);

		for (ObjectFolderItem objectFolderItem :
				objectFolder.getObjectFolderItems()) {

			ExportImportObjectDefinitionUtil.prepareObjectDefinitionForExport(
				_jsonFactory, objectFolderItem.getObjectDefinition());
		}

		JSONObject objectFolderJSONObject = _jsonFactory.createJSONObject(
			objectFolder.toString());

		if (!FeatureFlagManagerUtil.isEnabled("LPS-135430")) {
			objectFolderJSONObject.remove("storageType");
		}

		JSONObjectSanitizerUtil.sanitize(
			objectFolderJSONObject,
			new String[] {
				"dateCreated", "dateModified", "id", "listTypeDefinitionId",
				"objectDefinitionId", "objectDefinitionId1",
				"objectDefinitionId2", "objectFieldId", "objectRelationshipId",
				"titleObjectFieldId"
			});

		String objectFolderJSON = objectFolderJSONObject.toString();

		PortletResponseUtil.sendFile(
			resourceRequest, resourceResponse,
			StringBundler.concat(
				"Object_Folder_", objectFolder.getName(), StringPool.UNDERLINE,
				objectFolderId, StringPool.UNDERLINE, Time.getTimestamp(),
				".json"),
			objectFolderJSON.getBytes(), ContentTypes.APPLICATION_JSON);
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ObjectFolderResource.Factory _objectFolderResourceFactory;

}