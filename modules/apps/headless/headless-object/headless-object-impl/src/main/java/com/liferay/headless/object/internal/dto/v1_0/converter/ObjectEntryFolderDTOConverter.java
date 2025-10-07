/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.object.internal.dto.v1_0.converter;

import com.liferay.headless.delivery.dto.v1_0.util.CreatorUtil;
import com.liferay.headless.object.dto.v1_0.ObjectEntryFolder;
import com.liferay.headless.object.dto.v1_0.ParentObjectEntryFolderBrief;
import com.liferay.headless.object.dto.v1_0.Status;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.language.LanguageResources;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.trash.model.TrashEntry;
import com.liferay.trash.service.TrashEntryLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alicia García
 */
@Component(
	property = "dto.class.name=com.liferay.object.model.ObjectEntryFolder",
	service = DTOConverter.class
)
public class ObjectEntryFolderDTOConverter
	implements DTOConverter
		<com.liferay.object.model.ObjectEntryFolder, ObjectEntryFolder> {

	@Override
	public String getContentType() {
		return ObjectEntryFolder.class.getSimpleName();
	}

	@Override
	public ObjectEntryFolder toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		com.liferay.object.model.ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.getObjectEntryFolder(
				(Long)dtoConverterContext.getId());

		com.liferay.object.model.ObjectEntryFolder parentObjectEntryFolder =
			_getParentObjectEntryFolder(objectEntryFolder);

		TrashEntry trashEntry = null;

		if (objectEntryFolder.getStatus() ==
				WorkflowConstants.STATUS_IN_TRASH) {

			trashEntry = _trashEntryLocalService.fetchEntry(
				com.liferay.object.model.ObjectEntryFolder.class.getName(),
				objectEntryFolder.getObjectEntryFolderId());
		}

		TrashEntry finalTrashEntry = trashEntry;

		return new ObjectEntryFolder() {
			{
				setActions(dtoConverterContext::getActions);
				setCreator(
					() -> CreatorUtil.toCreator(
						dtoConverterContext, _portal,
						_userLocalService.fetchUser(
							objectEntryFolder.getUserId())));
				setDateCreated(objectEntryFolder::getCreateDate);
				setDateModified(objectEntryFolder::getModifiedDate);
				setDescription(objectEntryFolder::getDescription);
				setExternalReferenceCode(
					objectEntryFolder::getExternalReferenceCode);
				setId(objectEntryFolder::getObjectEntryFolderId);
				setLabel(
					() -> objectEntryFolder.getLabel(
						dtoConverterContext.getLocale()));
				setLabel_i18n(
					() -> LocalizedMapUtil.getLanguageIdMap(
						objectEntryFolder.getLabelMap()));
				setNumberOfObjectEntries(
					() -> NestedFieldsSupplier.supply(
						"numberOfObjectEntries",
						nestedField ->
							_objectEntryLocalService.
								getObjectEntryFolderObjectEntriesCount(
									objectEntryFolder.getGroupId(),
									objectEntryFolder.
										getObjectEntryFolderId())));
				setNumberOfObjectEntryFolders(
					() -> NestedFieldsSupplier.supply(
						"numberOfObjectEntryFolders",
						nestedField ->
							_objectEntryFolderLocalService.
								getObjectEntryFoldersCount(
									objectEntryFolder.getGroupId(),
									objectEntryFolder.getCompanyId(),
									objectEntryFolder.
										getObjectEntryFolderId())));
				setParentObjectEntryFolderBrief(
					() -> NestedFieldsSupplier.supply(
						"parentObjectEntryFolderBrief",
						nestedField -> _getParentObjectEntryFolderBrief(
							dtoConverterContext, parentObjectEntryFolder)));
				setParentObjectEntryFolderExternalReferenceCode(
					() -> {
						if (parentObjectEntryFolder != null) {
							return parentObjectEntryFolder.
								getExternalReferenceCode();
						}

						return null;
					});
				setParentObjectEntryFolderId(
					() -> {
						if (parentObjectEntryFolder != null) {
							return parentObjectEntryFolder.
								getObjectEntryFolderId();
						}

						return null;
					});
				setRemovedBy(
					() -> {
						if (finalTrashEntry != null) {
							return CreatorUtil.toCreator(
								dtoConverterContext, _portal,
								_userLocalService.fetchUser(
									finalTrashEntry.getUserId()));
						}

						return null;
					});
				setRemovedDate(
					() -> {
						if (finalTrashEntry != null) {
							return finalTrashEntry.getCreateDate();
						}

						return null;
					});
				setScopeId(objectEntryFolder::getGroupId);
				setScopeKey(
					() -> {
						Group group = _groupLocalService.fetchGroup(
							objectEntryFolder.getGroupId());

						if (group == null) {
							return String.valueOf(
								objectEntryFolder.getGroupId());
						}

						return group.getGroupKey();
					});
				setStatus(
					() -> new Status() {
						{
							setCode(objectEntryFolder::getStatus);
							setLabel(
								() -> WorkflowConstants.getStatusLabel(
									objectEntryFolder.getStatus()));
							setLabel_i18n(
								() -> _language.get(
									LanguageResources.getResourceBundle(
										dtoConverterContext.getLocale()),
									WorkflowConstants.getStatusLabel(
										objectEntryFolder.getStatus())));
						}
					});
				setTitle(objectEntryFolder::getName);
			}
		};
	}

	private com.liferay.object.model.ObjectEntryFolder
			_getParentObjectEntryFolder(
				com.liferay.object.model.ObjectEntryFolder objectEntryFolder)
		throws Exception {

		if (objectEntryFolder.getParentObjectEntryFolderId() > 0L) {
			return _objectEntryFolderLocalService.getObjectEntryFolder(
				objectEntryFolder.getParentObjectEntryFolderId());
		}

		return null;
	}

	private ParentObjectEntryFolderBrief _getParentObjectEntryFolderBrief(
		DTOConverterContext dtoConverterContext,
		com.liferay.object.model.ObjectEntryFolder parentObjectEntryFolder) {

		if (parentObjectEntryFolder == null) {
			return null;
		}

		return new ParentObjectEntryFolderBrief() {
			{
				setExternalReferenceCode(
					parentObjectEntryFolder::getExternalReferenceCode);
				setId(parentObjectEntryFolder::getObjectEntryFolderId);
				setLabel(
					() -> parentObjectEntryFolder.getLabel(
						dtoConverterContext.getLocale()));
				setLabel_i18n(
					() -> LocalizedMapUtil.getLanguageIdMap(
						parentObjectEntryFolder.getLabelMap()));
				setTitle(parentObjectEntryFolder::getName);
			}
		};
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private TrashEntryLocalService _trashEntryLocalService;

	@Reference
	private UserLocalService _userLocalService;

}