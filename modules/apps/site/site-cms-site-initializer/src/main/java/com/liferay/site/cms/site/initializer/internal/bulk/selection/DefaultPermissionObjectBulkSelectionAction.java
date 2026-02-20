/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.bulk.selection;

import com.liferay.bulk.selection.BulkSelectionAction;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.site.cms.site.initializer.bulk.selection.BaseObjectBulkSelectionAction;

import java.io.Serializable;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = "bulk.selection.action.key=default.permission.object",
	service = BulkSelectionAction.class
)
public class DefaultPermissionObjectBulkSelectionAction
	extends BaseObjectBulkSelectionAction {

	@Override
	protected void doExecute(
			User user, Map<String, Serializable> inputMap, Object object)
		throws PortalException {

		ObjectEntry objectObjectEntry = (ObjectEntry)object;

		Map<String, Serializable> objectObjectEntryValues =
			objectObjectEntry.getValues();

		String roleKey = (String)inputMap.get("roleKey");

		if (Validator.isBlank(roleKey)) {
			objectObjectEntryValues.put(
				"defaultPermissions",
				MapUtil.getString(inputMap, "defaultPermissions"));
		}
		else {
			JSONObject existingJSONObject = _jsonFactory.createJSONObject(
				GetterUtil.getString(
					objectObjectEntryValues.get("defaultPermissions"), "{}"));

			JSONObject newJSONObject = _jsonFactory.createJSONObject(
				GetterUtil.getString(
					MapUtil.getString(inputMap, "defaultPermissions"), "{}"));

			existingJSONObject.put(
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
				_getJSONObject(
					existingJSONObject.getJSONObject(
						ObjectEntryFolderConstants.
							EXTERNAL_REFERENCE_CODE_CONTENTS),
					newJSONObject.getJSONObject(
						ObjectEntryFolderConstants.
							EXTERNAL_REFERENCE_CODE_CONTENTS),
					roleKey)
			).put(
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES,
				_getJSONObject(
					existingJSONObject.getJSONObject(
						ObjectEntryFolderConstants.
							EXTERNAL_REFERENCE_CODE_FILES),
					newJSONObject.getJSONObject(
						ObjectEntryFolderConstants.
							EXTERNAL_REFERENCE_CODE_FILES),
					roleKey)
			).put(
				"OBJECT_ENTRY_FOLDERS",
				_getJSONObject(
					existingJSONObject.getJSONObject("OBJECT_ENTRY_FOLDERS"),
					newJSONObject.getJSONObject("OBJECT_ENTRY_FOLDERS"),
					roleKey)
			);

			objectObjectEntryValues.put(
				"defaultPermissions", existingJSONObject.toString());
		}

		partialUpdateObjectEntry(
			user.getUserId(), objectObjectEntry, objectObjectEntryValues);
	}

	private JSONObject _getJSONObject(
		JSONObject jsonObject1, JSONObject jsonObject2, String key) {

		if (jsonObject1 == null) {
			jsonObject1 = _jsonFactory.createJSONObject();
		}

		if ((jsonObject2 == null) || (jsonObject2.get(key) == null)) {
			return jsonObject1;
		}

		jsonObject1.put(key, jsonObject2.get(key));

		return jsonObject1;
	}

	@Reference
	private JSONFactory _jsonFactory;

}