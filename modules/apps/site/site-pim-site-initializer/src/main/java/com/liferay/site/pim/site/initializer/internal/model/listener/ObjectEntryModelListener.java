/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.model.listener;

import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.site.pim.site.initializer.internal.constants.PIMObjectFolderConstants;
import com.liferay.site.pim.site.initializer.internal.util.PIMObjectEntryFolderUtil;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(service = ModelListener.class)
public class ObjectEntryModelListener extends BaseModelListener<ObjectEntry> {

	@Override
	public void onBeforeCreate(ObjectEntry objectEntry)
		throws ModelListenerException {

		try {
			if (!_isPIMProductObjectEntry(objectEntry) ||
				!_shouldMoveToProductsObjectEntryFolder(objectEntry)) {

				return;
			}

			ObjectEntryFolder objectEntryFolder =
				PIMObjectEntryFolderUtil.getOrAddProductsObjectEntryFolder(
					_objectEntryFolderLocalService,
					_groupLocalService.getGroup(objectEntry.getGroupId()));

			objectEntry.setObjectEntryFolderId(
				objectEntryFolder.getObjectEntryFolderId());
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	private boolean _isPIMProductObjectEntry(ObjectEntry objectEntry) {
		if (!FeatureFlagManagerUtil.isEnabled(
				objectEntry.getCompanyId(), "LPD-96666")) {

			return false;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectEntry.getObjectDefinitionId());

		if (objectDefinition == null) {
			return false;
		}

		ObjectFolder objectFolder =
			_objectFolderLocalService.fetchObjectFolderByExternalReferenceCode(
				PIMObjectFolderConstants.EXTERNAL_REFERENCE_CODE_PRODUCT_TYPES,
				objectEntry.getCompanyId());

		if ((objectFolder != null) &&
			(objectDefinition.getObjectFolderId() ==
				objectFolder.getObjectFolderId())) {

			return true;
		}

		return false;
	}

	private boolean _shouldMoveToProductsObjectEntryFolder(
		ObjectEntry objectEntry) {

		long objectEntryFolderId = objectEntry.getObjectEntryFolderId();

		if (objectEntryFolderId ==
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT) {

			return true;
		}

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.fetchObjectEntryFolder(
				objectEntryFolderId);

		if ((objectEntryFolder != null) &&
			Objects.equals(
				objectEntryFolder.getExternalReferenceCode(),
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS)) {

			return true;
		}

		return false;
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Reference
	private ObjectFolderLocalService _objectFolderLocalService;

}