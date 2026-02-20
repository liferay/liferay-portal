/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.bulk.selection;

import com.liferay.bulk.selection.BulkSelectionAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManagerProvider;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.site.cms.site.initializer.bulk.selection.BaseObjectBulkSelectionAction;
import com.liferay.trash.TrashHelper;

import java.io.Serializable;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = "bulk.selection.action.key=delete.object",
	service = BulkSelectionAction.class
)
public class DeleteObjectBulkSelectionAction
	extends BaseObjectBulkSelectionAction {

	@Override
	protected void doExecute(
			User user, Map<String, Serializable> inputMap, Object object)
		throws Exception {

		if (object instanceof ObjectEntry) {
			ObjectEntry objectObjectEntry = (ObjectEntry)object;

			ObjectDefinition objectDefinition =
				objectDefinitionLocalService.getObjectDefinition(
					objectObjectEntry.getObjectDefinitionId());

			DefaultObjectEntryManager defaultObjectEntryManager =
				DefaultObjectEntryManagerProvider.provide(
					_objectEntryManagerRegistry.getObjectEntryManager(
						objectDefinition.getCompanyId(),
						objectDefinition.getStorageType()));

			defaultObjectEntryManager.deleteObjectEntry(
				objectDefinition, objectObjectEntry.getObjectEntryId());
		}
		else {
			ObjectEntryFolder objectEntryFolder = (ObjectEntryFolder)object;

			_deleteObjectEntryFolder(user.getUserId(), objectEntryFolder);
		}
	}

	private void _deleteObjectEntryFolder(
			long userId, ObjectEntryFolder objectEntryFolder)
		throws Exception {

		if (FeatureFlagManagerUtil.isEnabled(
				objectEntryFolder.getCompanyId(), "LPD-17564") &&
			objectEntryFolder.isTrashable(_trashHelper)) {

			_objectEntryFolderLocalService.moveObjectEntryFolderToTrash(
				userId, objectEntryFolder, new ServiceContext());
		}
		else {
			_objectEntryFolderLocalService.deleteObjectEntryFolder(
				objectEntryFolder.getObjectEntryFolderId());
		}
	}

	@Reference
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Reference
	private ObjectEntryManagerRegistry _objectEntryManagerRegistry;

	@Reference
	private TrashHelper _trashHelper;

}