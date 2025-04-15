/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.admin.web.internal.portlet.action;

import com.liferay.frontend.data.set.admin.web.internal.constants.FDSAdminPortletKeys;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.Serializable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marko Cikos
 */
@Component(
	property = {
		"jakarta.portlet.name=" + FDSAdminPortletKeys.FDS_ADMIN,
		"mvc.command.name=/frontend_data_set_admin/save_data_set_table_sections"
	},
	service = MVCResourceCommand.class
)
public class SaveDataSetTableSectionsMVCResourceCommand
	extends BaseTransactionalMVCResourceCommand {

	@Override
	protected void doTransactionalCommand(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				themeDisplay.getCompanyId(), "DataSetTableSection");

		String dataSetId = ParamUtil.getString(resourceRequest, "dataSetId");

		String creationData = ParamUtil.getString(
			resourceRequest, "creationData");

		JSONArray creationDataJSONArray = _jsonFactory.createJSONArray(
			creationData);

		for (int i = 0; i < creationDataJSONArray.length(); i++) {
			JSONObject creationDataJSONObject =
				creationDataJSONArray.getJSONObject(i);

			ObjectEntry objectEntry = _objectEntryService.addObjectEntry(
				0, objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"fieldName",
					String.valueOf(creationDataJSONObject.get("name"))
				).put(
					"label_i18n",
					HashMapBuilder.put(
						themeDisplay.getLanguageId(),
						String.valueOf(creationDataJSONObject.get("name"))
					).build()
				).put(
					"r_dataSetToDataSetTableSections_l_dataSetId", dataSetId
				).put(
					"renderer", "default"
				).put(
					"sortable", creationDataJSONObject.getBoolean("sortable")
				).put(
					"type", String.valueOf(creationDataJSONObject.get("type"))
				).build(),
				new ServiceContext());

			JSONObject jsonObject = _jsonFactory.createJSONObject(
				objectEntry.getValues());

			jsonObject.put(
				"externalReferenceCode", objectEntry.getExternalReferenceCode()
			).put(
				"id", objectEntry.getObjectEntryId()
			);

			jsonArray.put(jsonObject);
		}

		long[] deletionIds = ParamUtil.getLongValues(
			resourceRequest, "deletionIds");

		for (long id : deletionIds) {
			_objectEntryService.deleteObjectEntry(id);
		}

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, jsonArray);
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryService _objectEntryService;

}