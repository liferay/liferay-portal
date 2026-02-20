/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.bulk.selection;

import com.liferay.bulk.selection.BulkSelectionAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.site.cms.site.initializer.bulk.selection.BaseObjectBulkSelectionAction;

import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Danny Situ
 */
@Component(
	property = "bulk.selection.action.key=status.object",
	service = BulkSelectionAction.class
)
public class StatusObjectBulkSelectionAction
	extends BaseObjectBulkSelectionAction {

	@Override
	protected void doExecute(
			User user, Map<String, Serializable> inputMap, Object object)
		throws Exception {

		if (!(object instanceof ObjectEntry)) {
			return;
		}

		String status = (String)inputMap.get("status");

		if (Validator.isBlank(status)) {
			return;
		}

		ObjectEntry objectEntry = (ObjectEntry)object;

		Map<String, Serializable> properties = new HashMap<>();

		ObjectDefinition objectDefinition =
			objectDefinitionLocalService.getObjectDefinition(
				objectEntry.getObjectDefinitionId());

		if (StringUtil.equals(objectDefinition.getName(), "CMPTask")) {
			properties.put("state", status);
		}
		else {
			properties.put("status", status);
		}

		_objectEntryService.partialUpdateObjectEntry(
			objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(), properties,
			new ServiceContext());
	}

	@Reference
	private ObjectEntryService _objectEntryService;

}