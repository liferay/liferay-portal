/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.bulk.selection;

import com.liferay.bulk.selection.BulkSelectionAction;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.site.cms.site.initializer.bulk.selection.BaseObjectBulkSelectionAction;

import java.io.Serializable;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Balázs Sáfrány-Kovalik
 */
@Component(
	property = "bulk.selection.action.key=expire.object",
	service = BulkSelectionAction.class
)
public class ExpireObjectBulkSelectionAction
	extends BaseObjectBulkSelectionAction {

	@Override
	protected void doExecute(
			User user, Map<String, Serializable> inputMap, Object object)
		throws Exception {

		if (object instanceof ObjectEntry) {
			ObjectEntry objectObjectEntry = (ObjectEntry)object;

			_objectEntryService.expireObjectEntry(
				objectObjectEntry.getObjectEntryId(), new ServiceContext());
		}
		else if (object instanceof ObjectEntryFolder) {
			ObjectEntryFolder objectEntryFolder = (ObjectEntryFolder)object;

			throw new IllegalArgumentException(
				"Object entry folders do not support expiration " +
					objectEntryFolder);
		}
		else {
			throw new IllegalArgumentException("Unsupported object " + object);
		}
	}

	@Reference
	private ObjectEntryService _objectEntryService;

}