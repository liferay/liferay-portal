/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
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
import com.liferay.object.service.ObjectEntryFolderService;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.site.cms.site.initializer.bulk.selection.BaseObjectBulkSelectionAction;

import java.io.Serializable;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alicia García
 */
@Component(
	property = "bulk.selection.action.key=copy.object",
	service = BulkSelectionAction.class
)
public class CopyBulkSelectionAction extends BaseObjectBulkSelectionAction {

	@Override
	protected void doExecute(
			User user, Map<String, Serializable> inputMap, Object object)
		throws Exception {

		Long objectEntryFolderId = (Long)inputMap.get("objectEntryFolderId");

		if (Validator.isNull(objectEntryFolderId)) {
			throw new IllegalArgumentException(
				"Object entry folder ID is null");
		}

		ObjectEntryFolder targetObjectEntryFolder =
			_objectEntryFolderService.getObjectEntryFolder(objectEntryFolderId);

		if (object instanceof ObjectEntry) {
			ObjectEntry objectEntry = (ObjectEntry)object;

			ObjectDefinition objectDefinition =
				objectDefinitionLocalService.getObjectDefinition(
					objectEntry.getObjectDefinitionId());

			DefaultObjectEntryManager defaultObjectEntryManager =
				DefaultObjectEntryManagerProvider.provide(
					_objectEntryManagerRegistry.getObjectEntryManager(
						objectDefinition.getCompanyId(),
						objectDefinition.getStorageType()));

			defaultObjectEntryManager.copyObjectEntry(
				_getDTOConverterContext(
					PortalUtil.getSiteDefaultLocale(
						targetObjectEntryFolder.getGroupId()),
					objectEntry.getObjectEntryId(), user),
				objectEntry.getObjectEntryId(),
				targetObjectEntryFolder.getObjectEntryFolderId(), false);
		}
		else if (object instanceof ObjectEntryFolder) {
			ObjectEntryFolder objectEntryFolder = (ObjectEntryFolder)object;

			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setUserId(user.getUserId());

			_objectEntryFolderService.copyObjectEntryFolder(
				objectEntryFolder.getObjectEntryFolderId(),
				targetObjectEntryFolder.getObjectEntryFolderId(), false,
				serviceContext);
		}
		else {
			throw new IllegalArgumentException("Unsupported object " + object);
		}
	}

	private DefaultDTOConverterContext _getDTOConverterContext(
		Locale locale, Long objectEntryId, User user) {

		return new DefaultDTOConverterContext(
			_dtoConverterRegistry, objectEntryId, locale, null, user);
	}

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private ObjectEntryFolderService _objectEntryFolderService;

	@Reference
	private ObjectEntryManagerRegistry _objectEntryManagerRegistry;

}