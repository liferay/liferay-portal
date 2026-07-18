/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.documentlibrary.service.impl;

import com.liferay.document.library.kernel.exception.DuplicateDLFileEntryTypeExternalReferenceCodeException;
import com.liferay.document.library.kernel.exception.DuplicateFileEntryTypeException;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryTypeException;
import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.document.library.kernel.exception.NoSuchMetadataSetException;
import com.liferay.document.library.kernel.exception.RequiredFileEntryTypeException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppHelperLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileVersionLocalService;
import com.liferay.document.library.kernel.service.persistence.DLFileEntryPersistence;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.dynamic.data.mapping.kernel.DDMStructure;
import com.liferay.dynamic.data.mapping.kernel.DDMStructureLink;
import com.liferay.dynamic.data.mapping.kernel.DDMStructureLinkManagerUtil;
import com.liferay.dynamic.data.mapping.kernel.DDMStructureManagerUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.service.permission.ModelPermissionsFactory;
import com.liferay.portal.kernel.service.persistence.ResourceActionPersistence;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.SortedArrayList;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.repository.liferayrepository.model.LiferayFileEntry;
import com.liferay.portal.repository.liferayrepository.model.LiferayFileVersion;
import com.liferay.portlet.documentlibrary.service.base.DLFileEntryTypeLocalServiceBaseImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Provides the local service for accessing, adding, cascading, deleting, and
 * updating file and folder file entry types.
 *
 * @author Alexander Chow
 * @author Sergio González
 */
public class DLFileEntryTypeLocalServiceImpl
	extends DLFileEntryTypeLocalServiceBaseImpl {

	@Override
	public void addDDMStructureLinks(
		long fileEntryTypeId, Set<Long> ddmStructureIds) {

		long classNameId = _classNameLocalService.getClassNameId(
			DLFileEntryType.class);

		for (long ddmStructureId : ddmStructureIds) {
			DDMStructureLinkManagerUtil.addStructureLink(
				classNameId, fileEntryTypeId, ddmStructureId);
		}
	}

	@Override
	public DLFileEntryType addFileEntryType(
			String externalReferenceCode, long userId, long groupId,
			long dataDefinitionId, String fileEntryTypeKey,
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap,
			int scope, ServiceContext serviceContext)
		throws PortalException {

		User user = _userPersistence.findByPrimaryKey(userId);

		if (Validator.isNull(fileEntryTypeKey)) {
			fileEntryTypeKey = String.valueOf(counterLocalService.increment());
		}
		else {
			fileEntryTypeKey = StringUtil.toUpperCase(fileEntryTypeKey.trim());
		}

		String fileEntryTypeUuid = GetterUtil.getString(
			serviceContext.getAttribute("fileEntryTypeUuid"));

		if (Validator.isNull(fileEntryTypeUuid)) {
			fileEntryTypeUuid = serviceContext.getUuid();

			if (Validator.isNull(fileEntryTypeUuid)) {
				fileEntryTypeUuid = PortalUUIDUtil.generate();
			}
		}

		_validateExternalReferenceCode(externalReferenceCode, groupId);

		_validateFileEntryTypeKey(groupId, fileEntryTypeKey);

		_validateDDMStructures(fileEntryTypeKey, new long[] {dataDefinitionId});

		DLFileEntryType dlFileEntryType = dlFileEntryTypePersistence.create(
			counterLocalService.increment());

		dlFileEntryType.setUuid(fileEntryTypeUuid);
		dlFileEntryType.setExternalReferenceCode(externalReferenceCode);
		dlFileEntryType.setGroupId(groupId);
		dlFileEntryType.setCompanyId(user.getCompanyId());
		dlFileEntryType.setUserId(user.getUserId());
		dlFileEntryType.setUserName(user.getFullName());
		dlFileEntryType.setDataDefinitionId(dataDefinitionId);
		dlFileEntryType.setFileEntryTypeKey(fileEntryTypeKey);
		dlFileEntryType.setNameMap(nameMap);
		dlFileEntryType.setDescriptionMap(descriptionMap);
		dlFileEntryType.setScope(scope);

		dlFileEntryType = dlFileEntryTypePersistence.update(dlFileEntryType);

		if (serviceContext.isAddGroupPermissions() ||
			serviceContext.isAddGuestPermissions()) {

			addFileEntryTypeResources(
				dlFileEntryType, serviceContext.isAddGroupPermissions(),
				serviceContext.isAddGuestPermissions());
		}
		else {
			addFileEntryTypeResources(
				dlFileEntryType, serviceContext.getModelPermissions());
		}

		return dlFileEntryType;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #addFileEntryType(String, long, long, long, String, Map, Map, int,
	 *             ServiceContext)}
	 */
	@Deprecated
	@Override
	public DLFileEntryType addFileEntryType(
			String externalReferenceCode, long userId, long groupId,
			long dataDefinitionId, String fileEntryTypeKey,
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap,
			ServiceContext serviceContext)
		throws PortalException {

		return addFileEntryType(
			externalReferenceCode, userId, groupId, dataDefinitionId,
			fileEntryTypeKey, nameMap, descriptionMap,
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_SCOPE_DEFAULT,
			serviceContext);
	}

	@Override
	public void cascadeFileEntryTypes(long userId, DLFolder dlFolder)
		throws PortalException {

		long[] groupIds = PortalUtil.getCurrentAndAncestorSiteGroupIds(
			dlFolder.getGroupId());

		List<DLFileEntryType> dlFileEntryTypes = getFolderFileEntryTypes(
			groupIds, dlFolder.getFolderId(), true);

		long defaultFileEntryTypeId = getDefaultFileEntryTypeId(
			dlFolder.getFolderId());

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(dlFolder.getCompanyId());
		serviceContext.setScopeGroupId(dlFolder.getGroupId());
		serviceContext.setUserId(userId);

		cascadeFileEntryTypes(
			userId, dlFolder.getGroupId(), dlFolder.getFolderId(),
			defaultFileEntryTypeId, getFileEntryTypeIds(dlFileEntryTypes),
			serviceContext);
	}

	@Override
	public DLFileEntryType createBasicDocumentDLFileEntryType() {
		DLFileEntryType dlFileEntryType = dlFileEntryTypePersistence.create(
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT);

		dlFileEntryType.setCompanyId(CompanyConstants.SYSTEM);
		dlFileEntryType.setFileEntryTypeKey(
			StringUtil.toUpperCase(
				DLFileEntryTypeConstants.NAME_BASIC_DOCUMENT));
		dlFileEntryType.setName(
			DLFileEntryTypeConstants.NAME_BASIC_DOCUMENT,
			LocaleUtil.getDefault());

		return dlFileEntryTypePersistence.update(dlFileEntryType);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public DLFileEntryType deleteFileEntryType(DLFileEntryType dlFileEntryType)
		throws PortalException {

		int count = _dlFileEntryPersistence.countByFileEntryTypeId(
			dlFileEntryType.getFileEntryTypeId());

		if (count > 0) {
			throw new RequiredFileEntryTypeException(
				"There are file entries of file entry type " +
					dlFileEntryType.getFileEntryTypeId());
		}

		DDMStructureLinkManagerUtil.deleteStructureLinks(
			_classNameLocalService.getClassNameId(DLFileEntryType.class),
			dlFileEntryType.getFileEntryTypeId());

		DDMStructure ddmStructure = DDMStructureManagerUtil.fetchStructure(
			dlFileEntryType.getDataDefinitionId());

		if (ddmStructure == null) {
			ddmStructure = DDMStructureManagerUtil.fetchStructure(
				dlFileEntryType.getGroupId(),
				_classNameLocalService.getClassNameId(
					DLFileEntryMetadata.class),
				DLUtil.getDDMStructureKey(dlFileEntryType));
		}

		if (ddmStructure == null) {
			ddmStructure = DDMStructureManagerUtil.fetchStructure(
				dlFileEntryType.getGroupId(),
				_classNameLocalService.getClassNameId(
					DLFileEntryMetadata.class),
				DLUtil.getDeprecatedDDMStructureKey(dlFileEntryType));
		}

		if (ddmStructure != null) {
			DDMStructureManagerUtil.deleteStructure(
				ddmStructure.getStructureId());
		}

		_resourceLocalService.deleteResource(
			dlFileEntryType.getCompanyId(), DLFileEntryType.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			dlFileEntryType.getFileEntryTypeId());

		return dlFileEntryTypePersistence.remove(dlFileEntryType);
	}

	@Override
	public void deleteFileEntryType(long fileEntryTypeId)
		throws PortalException {

		DLFileEntryType dlFileEntryType =
			dlFileEntryTypePersistence.findByPrimaryKey(fileEntryTypeId);

		dlFileEntryTypeLocalService.deleteFileEntryType(dlFileEntryType);
	}

	@Override
	public void deleteFileEntryTypeByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		dlFileEntryTypeLocalService.deleteFileEntryType(
			getDLFileEntryTypeByExternalReferenceCode(
				externalReferenceCode, groupId));
	}

	@Override
	public void deleteFileEntryTypes(long groupId) throws PortalException {
		List<DLFileEntryType> dlFileEntryTypes =
			dlFileEntryTypePersistence.findByGroupId(groupId);

		for (DLFileEntryType dlFileEntryType : dlFileEntryTypes) {
			dlFileEntryTypeLocalService.deleteFileEntryType(dlFileEntryType);
		}
	}

	@Override
	public DLFileEntryType fetchDataDefinitionFileEntryType(
		long groupId, long dataDefinitionId) {

		return dlFileEntryTypePersistence.fetchByG_DDI(
			groupId, dataDefinitionId);
	}

	@Override
	public DLFileEntryType fetchFileEntryType(long fileEntryTypeId) {
		return dlFileEntryTypePersistence.fetchByPrimaryKey(fileEntryTypeId);
	}

	@Override
	public DLFileEntryType fetchFileEntryType(
		long groupId, String fileEntryTypeKey) {

		fileEntryTypeKey = StringUtil.toUpperCase(
			StringUtil.trim(fileEntryTypeKey));

		return dlFileEntryTypePersistence.fetchByG_F(groupId, fileEntryTypeKey);
	}

	@Override
	public DLFileEntryType getBasicDocumentDLFileEntryType()
		throws NoSuchFileEntryTypeException {

		DLFileEntryType dlFileEntryType =
			dlFileEntryTypePersistence.fetchByPrimaryKey(
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT);

		if (dlFileEntryType != null) {
			return dlFileEntryType;
		}

		return dlFileEntryTypeLocalService.createBasicDocumentDLFileEntryType();
	}

	@Override
	public long getDefaultFileEntryTypeId(long folderId)
		throws PortalException {

		folderId = _getFileEntryTypesPrimaryFolderId(folderId);

		if (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			DLFolder dlFolder = dlFolderPersistence.findByPrimaryKey(folderId);

			return dlFolder.getDefaultFileEntryTypeId();
		}

		return DLFileEntryTypeConstants.COMPANY_ID_BASIC_DOCUMENT;
	}

	@Override
	public DLFileEntryType getFileEntryType(long fileEntryTypeId)
		throws PortalException {

		return dlFileEntryTypePersistence.findByPrimaryKey(fileEntryTypeId);
	}

	@Override
	public DLFileEntryType getFileEntryType(
			long groupId, String fileEntryTypeKey)
		throws PortalException {

		fileEntryTypeKey = StringUtil.toUpperCase(
			StringUtil.trim(fileEntryTypeKey));

		return dlFileEntryTypePersistence.findByG_F(groupId, fileEntryTypeKey);
	}

	@Override
	public List<DLFileEntryType> getFileEntryTypes(long[] groupIds) {
		return dlFileEntryTypePersistence.findByGroupId(groupIds);
	}

	@Override
	public List<DLFileEntryType> getFileEntryTypesByCompanyId(long companyId) {
		return dlFileEntryTypePersistence.findByCompanyId(companyId);
	}

	@Override
	public List<DLFileEntryType> getFolderFileEntryTypes(
			long[] groupIds, long folderId, boolean inherited)
		throws PortalException {

		if (!inherited &&
			(folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID)) {

			return dlFolderPersistence.getDLFileEntryTypes(folderId);
		}

		folderId = _getFileEntryTypesPrimaryFolderId(folderId);

		if (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return dlFolderPersistence.getDLFileEntryTypes(folderId);
		}

		List<DLFileEntryType> dlFileEntryTypes = new ArrayList<>(
			getFileEntryTypes(groupIds));

		DLFileEntryType dlFileEntryType =
			dlFileEntryTypeLocalService.getBasicDocumentDLFileEntryType();

		dlFileEntryTypes.add(0, dlFileEntryType);

		return dlFileEntryTypes;
	}

	@Override
	public List<DLFileEntryType> search(
		long companyId, long[] groupIds, String keywords,
		boolean includeBasicFileEntryType, int start, int end,
		OrderByComparator<DLFileEntryType> orderByComparator) {

		return dlFileEntryTypeFinder.findByKeywords(
			companyId, groupIds, keywords, includeBasicFileEntryType, start,
			end, orderByComparator);
	}

	@Override
	public int searchCount(
		long companyId, long[] groupIds, String keywords,
		boolean includeBasicFileEntryType) {

		return dlFileEntryTypeFinder.countByKeywords(
			companyId, groupIds, keywords, includeBasicFileEntryType);
	}

	@Override
	public void unsetFolderFileEntryTypes(long folderId) {
		List<DLFileEntryType> dlFileEntryTypes =
			dlFolderPersistence.getDLFileEntryTypes(folderId);

		for (DLFileEntryType dlFileEntryType : dlFileEntryTypes) {
			dlFolderPersistence.removeDLFileEntryType(
				folderId, dlFileEntryType);
		}
	}

	@Override
	public void updateDDMStructureLinks(
			long fileEntryTypeId, Set<Long> ddmStructureIds)
		throws PortalException {

		Set<Long> existingDDMStructureLinkStructureIds =
			getExistingDDMStructureLinkStructureIds(fileEntryTypeId);

		deleteDDMStructureLinks(
			fileEntryTypeId,
			getStaleDDMStructureLinkStructureIds(
				ddmStructureIds, existingDDMStructureLinkStructureIds));

		addDDMStructureLinks(
			fileEntryTypeId,
			getMissingDDMStructureLinkStructureIds(
				ddmStructureIds, existingDDMStructureLinkStructureIds));
	}

	@Override
	public DLFileEntry updateFileEntryFileEntryType(
			DLFileEntry dlFileEntry, ServiceContext serviceContext)
		throws PortalException {

		long groupId = serviceContext.getScopeGroupId();
		long folderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;

		DLFolder dlFolder = dlFolderPersistence.fetchByPrimaryKey(
			dlFileEntry.getFolderId());

		if (dlFolder != null) {
			groupId = dlFolder.getGroupId();
			folderId = dlFolder.getFolderId();
		}

		List<DLFileEntryType> dlFileEntryTypes = getFolderFileEntryTypes(
			PortalUtil.getCurrentAndAncestorSiteGroupIds(groupId), folderId,
			true);

		List<Long> fileEntryTypeIds = getFileEntryTypeIds(dlFileEntryTypes);

		if (fileEntryTypeIds.contains(dlFileEntry.getFileEntryTypeId())) {
			return dlFileEntry;
		}

		DLFileVersion dlFileVersion =
			_dlFileVersionLocalService.getLatestFileVersion(
				dlFileEntry.getFileEntryId(), true);

		if (dlFileVersion.isPending()) {
			_workflowInstanceLinkLocalService.deleteWorkflowInstanceLink(
				dlFileVersion.getCompanyId(), dlFileEntry.getGroupId(),
				DLFileEntry.class.getName(), dlFileVersion.getFileVersionId());
		}

		return _dlFileEntryLocalService.updateFileEntry(
			serviceContext.getUserId(), dlFileEntry.getFileEntryId(), null,
			null, null, null, null, null,
			DLVersionNumberIncrease.fromMajorVersion(false),
			getDefaultFileEntryTypeId(folderId), null, null, null, 0, null,
			null, null, serviceContext);
	}

	@Override
	public DLFileEntryType updateFileEntryType(
			long fileEntryTypeId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap)
		throws PortalException {

		DLFileEntryType dlFileEntryType =
			dlFileEntryTypePersistence.findByPrimaryKey(fileEntryTypeId);

		dlFileEntryType.setNameMap(nameMap);
		dlFileEntryType.setDescriptionMap(descriptionMap);

		return dlFileEntryTypePersistence.update(dlFileEntryType);
	}

	@Override
	public void updateFolderFileEntryTypes(
		DLFolder dlFolder, List<Long> fileEntryTypeIds,
		long defaultFileEntryTypeId, ServiceContext serviceContext) {

		List<Long> originalFileEntryTypeIds = getFileEntryTypeIds(
			dlFolderPersistence.getDLFileEntryTypes(dlFolder.getFolderId()));

		if (fileEntryTypeIds.equals(originalFileEntryTypeIds)) {
			return;
		}

		for (Long fileEntryTypeId : fileEntryTypeIds) {
			if (!originalFileEntryTypeIds.contains(fileEntryTypeId)) {
				if (fileEntryTypeId ==
						DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_ALL) {

					continue;
				}

				DLFileEntryType dlFileEntryType =
					DLFileEntryTypeLocalServiceUtil.fetchDLFileEntryType(
						fileEntryTypeId);

				if (dlFileEntryType == null) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Document library file entry type " +
								fileEntryTypeId + " does not exist");
					}

					continue;
				}

				dlFolderPersistence.addDLFileEntryType(
					dlFolder.getFolderId(), fileEntryTypeId);
			}
		}

		for (Long originalFileEntryTypeId : originalFileEntryTypeIds) {
			if (!fileEntryTypeIds.contains(originalFileEntryTypeId)) {
				dlFolderPersistence.removeDLFileEntryType(
					dlFolder.getFolderId(), originalFileEntryTypeId);

				_workflowDefinitionLinkLocalService.
					deleteWorkflowDefinitionLink(
						dlFolder.getCompanyId(), dlFolder.getGroupId(),
						DLFolder.class.getName(), dlFolder.getFolderId(),
						originalFileEntryTypeId);
			}
		}
	}

	protected void addFileEntryTypeResources(
			DLFileEntryType dlFileEntryType, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		_resourceLocalService.addResources(
			dlFileEntryType.getCompanyId(), dlFileEntryType.getGroupId(),
			dlFileEntryType.getUserId(), DLFileEntryType.class.getName(),
			dlFileEntryType.getFileEntryTypeId(), false, addGroupPermissions,
			addGuestPermissions);

		_resourceLocalService.addResources(
			dlFileEntryType.getCompanyId(), dlFileEntryType.getGroupId(),
			dlFileEntryType.getUserId(),
			_DL_FILE_ENTRY_METADATA_DDM_STRUCTURE_CLASS_NAME,
			dlFileEntryType.getDataDefinitionId(), false, addGroupPermissions,
			addGuestPermissions);
	}

	protected void addFileEntryTypeResources(
			DLFileEntryType dlFileEntryType, ModelPermissions modelPermissions)
		throws PortalException {

		_resourceLocalService.addModelResources(
			dlFileEntryType.getCompanyId(), dlFileEntryType.getGroupId(),
			dlFileEntryType.getUserId(), DLFileEntryType.class.getName(),
			dlFileEntryType.getFileEntryTypeId(), modelPermissions);

		ModelPermissions dlFileEntryMetadataDDMStructureModelPermissions = null;

		if (modelPermissions != null) {
			dlFileEntryMetadataDDMStructureModelPermissions =
				ModelPermissionsFactory.create(
					_DL_FILE_ENTRY_METADATA_DDM_STRUCTURE_CLASS_NAME);

			List<ResourceAction> dlFileEntryMetadataResourceActions =
				_resourceActionPersistence.findByName(
					_DL_FILE_ENTRY_METADATA_DDM_STRUCTURE_CLASS_NAME);

			Set<String> dlFileEntryMetadataActionIds = new HashSet<>();

			for (ResourceAction resourceAction :
					dlFileEntryMetadataResourceActions) {

				dlFileEntryMetadataActionIds.add(resourceAction.getActionId());
			}

			for (String roleName : modelPermissions.getRoleNames()) {
				Set<String> dlFileEntryMetadataDDMStructureActionIds =
					new HashSet<>();

				for (String actionId :
						modelPermissions.getActionIds(roleName)) {

					if (dlFileEntryMetadataActionIds.contains(actionId)) {
						dlFileEntryMetadataDDMStructureActionIds.add(actionId);
					}
				}

				dlFileEntryMetadataDDMStructureModelPermissions.
					addRolePermissions(
						roleName,
						dlFileEntryMetadataDDMStructureActionIds.toArray(
							new String[0]));
			}
		}

		_resourceLocalService.addModelResources(
			dlFileEntryType.getCompanyId(), dlFileEntryType.getGroupId(),
			dlFileEntryType.getUserId(),
			_DL_FILE_ENTRY_METADATA_DDM_STRUCTURE_CLASS_NAME,
			dlFileEntryType.getDataDefinitionId(),
			dlFileEntryMetadataDDMStructureModelPermissions);
	}

	protected void cascadeFileEntryTypes(
			long userId, long groupId, long folderId,
			long defaultFileEntryTypeId, List<Long> fileEntryTypeIds,
			ServiceContext serviceContext)
		throws PortalException {

		List<DLFileEntry> dlFileEntries = _dlFileEntryPersistence.findByG_F(
			groupId, folderId);

		for (DLFileEntry dlFileEntry : dlFileEntries) {
			if (fileEntryTypeIds.contains(dlFileEntry.getFileEntryTypeId())) {
				continue;
			}

			DLFileVersion dlFileVersion =
				_dlFileVersionLocalService.getLatestFileVersion(
					dlFileEntry.getFileEntryId(), true);

			if (dlFileVersion.isPending()) {
				_workflowInstanceLinkLocalService.deleteWorkflowInstanceLink(
					dlFileVersion.getCompanyId(), groupId,
					DLFileEntry.class.getName(),
					dlFileVersion.getFileVersionId());
			}

			_dlFileEntryLocalService.updateFileEntryType(
				userId, dlFileEntry.getFileEntryId(), defaultFileEntryTypeId,
				serviceContext);

			_dlAppHelperLocalService.updateAsset(
				userId, new LiferayFileEntry(dlFileEntry),
				new LiferayFileVersion(dlFileVersion), serviceContext);
		}

		List<DLFolder> subfolders = dlFolderPersistence.findByG_M_P_H(
			groupId, false, folderId, false);

		for (DLFolder subfolder : subfolders) {
			if (subfolder.getRestrictionType() ==
					DLFolderConstants.RESTRICTION_TYPE_INHERIT) {

				continue;
			}

			cascadeFileEntryTypes(
				userId, groupId, subfolder.getFolderId(),
				defaultFileEntryTypeId, fileEntryTypeIds, serviceContext);
		}
	}

	protected void deleteDDMStructureLinks(
			long fileEntryTypeId, Set<Long> ddmStructureIds)
		throws PortalException {

		long classNameId = _classNameLocalService.getClassNameId(
			DLFileEntryType.class);

		for (long ddmStructureId : ddmStructureIds) {
			DDMStructureLinkManagerUtil.deleteStructureLink(
				classNameId, fileEntryTypeId, ddmStructureId);
		}
	}

	protected Set<Long> getExistingDDMStructureLinkStructureIds(
		long fileEntryTypeId) {

		Set<Long> existingDDMStructureLinkStructureIds = new HashSet<>();

		List<DDMStructureLink> structureLinks =
			DDMStructureLinkManagerUtil.getStructureLinks(
				_classNameLocalService.getClassNameId(DLFileEntryType.class),
				fileEntryTypeId);

		for (DDMStructureLink structureLink : structureLinks) {
			existingDDMStructureLinkStructureIds.add(
				structureLink.getStructureId());
		}

		return existingDDMStructureLinkStructureIds;
	}

	protected List<Long> getFileEntryTypeIds(
		List<DLFileEntryType> dlFileEntryTypes) {

		List<Long> fileEntryTypeIds = new SortedArrayList<>();

		for (DLFileEntryType dlFileEntryType : dlFileEntryTypes) {
			fileEntryTypeIds.add(dlFileEntryType.getFileEntryTypeId());
		}

		return fileEntryTypeIds;
	}

	protected Set<Long> getMissingDDMStructureLinkStructureIds(
		Set<Long> ddmStructureIds, Set<Long> existingDDMStructureIds) {

		Set<Long> missingDDMStructureLinkStructureIds = new HashSet<>(
			ddmStructureIds);

		missingDDMStructureLinkStructureIds.removeAll(existingDDMStructureIds);

		return missingDDMStructureLinkStructureIds;
	}

	protected Set<Long> getStaleDDMStructureLinkStructureIds(
		Set<Long> ddmStructureIds, Set<Long> existingDDMStructureIds) {

		Set<Long> staleDDMStructureLinkStructureIds = new HashSet<>(
			existingDDMStructureIds);

		staleDDMStructureLinkStructureIds.removeAll(ddmStructureIds);

		return staleDDMStructureLinkStructureIds;
	}

	private long _getFileEntryTypesPrimaryFolderId(long folderId)
		throws NoSuchFolderException {

		while (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			DLFolder dlFolder = dlFolderPersistence.findByPrimaryKey(folderId);

			if (dlFolder.getRestrictionType() ==
					DLFolderConstants.
						RESTRICTION_TYPE_FILE_ENTRY_TYPES_AND_WORKFLOW) {

				break;
			}

			folderId = dlFolder.getParentFolderId();
		}

		return folderId;
	}

	private void _validateDDMStructures(
			String fileEntryTypeKey, long[] ddmStructureIds)
		throws NoSuchMetadataSetException {

		if (ddmStructureIds.length == 0) {
			throw new NoSuchMetadataSetException(
				"DDM structure IDs is empty for file entry type " +
					fileEntryTypeKey);
		}

		for (long ddmStructureId : ddmStructureIds) {
			DDMStructure ddmStructure = DDMStructureManagerUtil.fetchStructure(
				ddmStructureId);

			if (ddmStructure == null) {
				throw new NoSuchMetadataSetException(
					"{ddmStructureId=" + ddmStructureId + "}");
			}
		}
	}

	private void _validateExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		if (Validator.isNull(externalReferenceCode)) {
			return;
		}

		DLFileEntryType dlFileEntryType =
			dlFileEntryTypePersistence.fetchByERC_G(
				externalReferenceCode, groupId);

		if (dlFileEntryType != null) {
			throw new DuplicateDLFileEntryTypeExternalReferenceCodeException(
				StringBundler.concat(
					"Duplicate file entry type external reference code ",
					externalReferenceCode, " in group ", groupId));
		}
	}

	private void _validateFileEntryTypeKey(
			long groupId, String fileEntryTypeKey)
		throws DuplicateFileEntryTypeException {

		DLFileEntryType dlFileEntryType = dlFileEntryTypePersistence.fetchByG_F(
			groupId, fileEntryTypeKey);

		if (dlFileEntryType != null) {
			throw new DuplicateFileEntryTypeException(
				"A file entry type already exists for key " + fileEntryTypeKey);
		}
	}

	private static final String
		_DL_FILE_ENTRY_METADATA_DDM_STRUCTURE_CLASS_NAME =
			"com.liferay.document.library.kernel.model.DLFileEntryMetadata-" +
				"com.liferay.dynamic.data.mapping.model.DDMStructure";

	private static final Log _log = LogFactoryUtil.getLog(
		DLFileEntryTypeLocalServiceImpl.class);

	@BeanReference(type = ClassNameLocalService.class)
	private ClassNameLocalService _classNameLocalService;

	@BeanReference(type = DLAppHelperLocalService.class)
	private DLAppHelperLocalService _dlAppHelperLocalService;

	@BeanReference(type = DLFileEntryLocalService.class)
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@BeanReference(type = DLFileEntryPersistence.class)
	private DLFileEntryPersistence _dlFileEntryPersistence;

	@BeanReference(type = DLFileVersionLocalService.class)
	private DLFileVersionLocalService _dlFileVersionLocalService;

	@BeanReference(type = ResourceActionPersistence.class)
	private ResourceActionPersistence _resourceActionPersistence;

	@BeanReference(type = ResourceLocalService.class)
	private ResourceLocalService _resourceLocalService;

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

	@BeanReference(type = WorkflowDefinitionLinkLocalService.class)
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@BeanReference(type = WorkflowInstanceLinkLocalService.class)
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

}