/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.exportimport.data.handler;

import com.liferay.document.library.kernel.exception.NoSuchMetadataSetException;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.document.library.util.DLFileEntryTypeUtil;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.exportimport.data.handler.base.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelModifiedDateComparator;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.xml.Element;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Máté Thurzó
 */
@Component(service = StagedModelDataHandler.class)
public class DLFileEntryTypeStagedModelDataHandler
	extends BaseStagedModelDataHandler<DLFileEntryType> {

	public static final String[] CLASS_NAMES = {
		DLFileEntryType.class.getName()
	};

	@Override
	public void deleteStagedModel(DLFileEntryType fileEntryType)
		throws PortalException {

		_dlFileEntryTypeLocalService.deleteFileEntryType(fileEntryType);
	}

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException {

		DLFileEntryType dlFileEntryType = fetchStagedModelByUuidAndGroupId(
			uuid, groupId);

		if (dlFileEntryType != null) {
			deleteStagedModel(dlFileEntryType);
		}
	}

	@Override
	public DLFileEntryType fetchStagedModelByUuidAndGroupId(
		String uuid, long groupId) {

		return _dlFileEntryTypeLocalService.
			fetchDLFileEntryTypeByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public List<DLFileEntryType> fetchStagedModelsByUuidAndCompanyId(
		String uuid, long companyId) {

		return _dlFileEntryTypeLocalService.
			getDLFileEntryTypesByUuidAndCompanyId(
				uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				new StagedModelModifiedDateComparator<DLFileEntryType>());
	}

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	public Map<String, String> getReferenceAttributes(
		PortletDataContext portletDataContext, DLFileEntryType fileEntryType) {

		return HashMapBuilder.put(
			"file-entry-type-key", fileEntryType.getFileEntryTypeKey()
		).put(
			"preloaded", () -> String.valueOf(_isPreloaded(fileEntryType))
		).build();
	}

	@Override
	public boolean validateReference(
		PortletDataContext portletDataContext, Element referenceElement) {

		validateMissingGroupReference(portletDataContext, referenceElement);

		String uuid = referenceElement.attributeValue("uuid");

		Map<Long, Long> groupIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Group.class);

		long groupId = GetterUtil.getLong(
			referenceElement.attributeValue("group-id"));

		groupId = MapUtil.getLong(groupIds, groupId);

		boolean preloaded = GetterUtil.getBoolean(
			referenceElement.attributeValue("preloaded"));

		if (!preloaded) {
			return super.validateMissingReference(uuid, groupId);
		}

		String fileEntryTypeKey = referenceElement.attributeValue(
			"file-entry-type-key");

		DLFileEntryType existingFileEntryType =
			_fetchExistingFileEntryTypeWithParentGroups(
				uuid, groupId, fileEntryTypeKey, preloaded);

		if (existingFileEntryType == null) {
			return false;
		}

		return true;
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext,
			DLFileEntryType fileEntryType)
		throws Exception {

		Element fileEntryTypeElement = portletDataContext.getExportDataElement(
			fileEntryType);

		List<DDMStructure> ddmStructures = DLFileEntryTypeUtil.getDDMStructures(
			fileEntryType);

		for (DDMStructure ddmStructure : ddmStructures) {
			Element referenceElement =
				StagedModelDataHandlerUtil.exportReferenceStagedModel(
					portletDataContext, fileEntryType,
					_ddmStructureLocalService.getStructure(
						ddmStructure.getStructureId()),
					PortletDataContext.REFERENCE_TYPE_STRONG);

			referenceElement.addAttribute(
				"structure-id", String.valueOf(ddmStructure.getStructureId()));
		}

		if (_isPreloaded(fileEntryType)) {
			fileEntryTypeElement.addAttribute("preloaded", "true");
		}

		portletDataContext.addClassedModel(
			fileEntryTypeElement,
			ExportImportPathUtil.getModelPath(fileEntryType), fileEntryType);
	}

	@Override
	protected void doImportMissingReference(
			PortletDataContext portletDataContext, Element referenceElement)
		throws PortletDataException {

		importMissingGroupReference(portletDataContext, referenceElement);

		String uuid = referenceElement.attributeValue("uuid");

		Map<Long, Long> groupIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Group.class);

		long groupId = GetterUtil.getLong(
			referenceElement.attributeValue("group-id"));

		groupId = MapUtil.getLong(groupIds, groupId);

		String fileEntryTypeKey = referenceElement.attributeValue(
			"file-entry-type-key");
		boolean preloaded = GetterUtil.getBoolean(
			referenceElement.attributeValue("preloaded"));

		DLFileEntryType existingFileEntryType;

		if (!preloaded) {
			existingFileEntryType = fetchMissingReference(uuid, groupId);
		}
		else {
			existingFileEntryType = _fetchExistingFileEntryTypeWithParentGroups(
				uuid, groupId, fileEntryTypeKey, preloaded);
		}

		if (existingFileEntryType == null) {
			return;
		}

		Map<Long, Long> fileEntryTypeIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				DLFileEntryType.class);

		long fileEntryTypeId = GetterUtil.getLong(
			referenceElement.attributeValue("class-pk"));

		fileEntryTypeIds.put(
			fileEntryTypeId, existingFileEntryType.getFileEntryTypeId());
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext,
			DLFileEntryType fileEntryType)
		throws Exception {

		long userId = portletDataContext.getUserId(fileEntryType.getUserUuid());

		List<Element> ddmStructureReferenceElements =
			portletDataContext.getReferenceElements(
				fileEntryType, DDMStructure.class);

		long[] ddmStructureIdsArray =
			new long[ddmStructureReferenceElements.size()];

		Map<Long, Long> ddmStructureIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				DDMStructure.class);

		for (int i = 0; i < ddmStructureReferenceElements.size(); i++) {
			Element ddmStructureReferenceElement =
				ddmStructureReferenceElements.get(i);

			long ddmStructureId = GetterUtil.getLong(
				ddmStructureReferenceElement.attributeValue("class-pk"));

			ddmStructureIdsArray[i] = MapUtil.getLong(
				ddmStructureIds, ddmStructureId);
		}

		ServiceContext serviceContext = portletDataContext.createServiceContext(
			fileEntryType);

		DLFileEntryType importedDLFileEntryType = null;

		Element element = portletDataContext.getImportDataStagedModelElement(
			fileEntryType);

		boolean preloaded = GetterUtil.getBoolean(
			element.attributeValue("preloaded"));

		if (portletDataContext.isDataStrategyMirror()) {
			DLFileEntryType existingDLFileEntryType =
				_fetchExistingFileEntryType(
					fileEntryType.getUuid(),
					portletDataContext.getScopeGroupId(),
					fileEntryType.getFileEntryTypeKey(), preloaded);

			if (existingDLFileEntryType == null) {
				serviceContext.setUuid(fileEntryType.getUuid());

				_validateDDMStructures(
					fileEntryType.getFileEntryTypeKey(), ddmStructureIdsArray);

				importedDLFileEntryType =
					_dlFileEntryTypeLocalService.addFileEntryType(
						null, userId, portletDataContext.getScopeGroupId(),
						ddmStructureIdsArray[0],
						fileEntryType.getFileEntryTypeKey(),
						fileEntryType.getNameMap(),
						fileEntryType.getDescriptionMap(),
						fileEntryType.getScope(), serviceContext);

				_dlFileEntryTypeLocalService.addDDMStructureLinks(
					importedDLFileEntryType.getFileEntryTypeId(),
					SetUtil.fromArray(ddmStructureIdsArray));
			}
			else {
				_validateDDMStructures(
					existingDLFileEntryType.getFileEntryTypeKey(),
					ddmStructureIdsArray);

				_dlFileEntryTypeLocalService.updateFileEntryType(
					existingDLFileEntryType.getFileEntryTypeId(),
					fileEntryType.getNameMap(),
					fileEntryType.getDescriptionMap());

				importedDLFileEntryType =
					_dlFileEntryTypeLocalService.fetchDLFileEntryType(
						existingDLFileEntryType.getFileEntryTypeId());

				_dlFileEntryTypeLocalService.updateDDMStructureLinks(
					importedDLFileEntryType.getFileEntryTypeId(),
					SetUtil.fromArray(ddmStructureIdsArray));
			}
		}
		else {
			_validateDDMStructures(
				fileEntryType.getFileEntryTypeKey(), ddmStructureIdsArray);

			importedDLFileEntryType =
				_dlFileEntryTypeLocalService.addFileEntryType(
					null, userId, portletDataContext.getScopeGroupId(),
					ddmStructureIdsArray[0],
					fileEntryType.getFileEntryTypeKey(),
					fileEntryType.getNameMap(),
					fileEntryType.getDescriptionMap(), fileEntryType.getScope(),
					serviceContext);

			_dlFileEntryTypeLocalService.addDDMStructureLinks(
				importedDLFileEntryType.getFileEntryTypeId(),
				SetUtil.fromArray(ddmStructureIdsArray));
		}

		portletDataContext.importClassedModel(
			fileEntryType, importedDLFileEntryType);

		if (preloaded) {
			return;
		}

		String importedDLFileEntryDDMStructureKey = DLUtil.getDDMStructureKey(
			importedDLFileEntryType);

		List<DDMStructure> importedDDMStructures =
			DLFileEntryTypeUtil.getDDMStructures(importedDLFileEntryType);

		for (DDMStructure importedDDMStructure : importedDDMStructures) {
			String ddmStructureKey = importedDDMStructure.getStructureKey();

			if (!DLUtil.isAutoGeneratedDLFileEntryTypeDDMStructureKey(
					ddmStructureKey) ||
				ddmStructureKey.equals(importedDLFileEntryDDMStructureKey)) {

				continue;
			}

			DDMStructure ddmStructure =
				_ddmStructureLocalService.getDDMStructure(
					importedDDMStructure.getStructureId());

			ddmStructure.setStructureKey(importedDLFileEntryDDMStructureKey);

			_ddmStructureLocalService.updateDDMStructure(ddmStructure);
		}
	}

	private DLFileEntryType _fetchExistingFileEntryType(
		String uuid, long groupId, String fileEntryTypeKey, boolean preloaded) {

		DLFileEntryType existingDLFileEntryType = null;

		if (!preloaded) {
			existingDLFileEntryType = fetchStagedModelByUuidAndGroupId(
				uuid, groupId);
		}
		else {
			existingDLFileEntryType =
				_dlFileEntryTypeLocalService.fetchFileEntryType(
					groupId, fileEntryTypeKey);
		}

		return existingDLFileEntryType;
	}

	private DLFileEntryType _fetchExistingFileEntryTypeWithParentGroups(
		String uuid, long groupId, String fileEntryTypeKey, boolean preloaded) {

		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return _fetchExistingFileEntryType(
				uuid, groupId, fileEntryTypeKey, preloaded);
		}

		long companyId = group.getCompanyId();

		while (group != null) {
			DLFileEntryType existingDLFileEntryType =
				_fetchExistingFileEntryType(
					uuid, group.getGroupId(), fileEntryTypeKey, preloaded);

			if (existingDLFileEntryType != null) {
				return existingDLFileEntryType;
			}

			group = group.getParentGroup();
		}

		Group companyGroup = _groupLocalService.fetchCompanyGroup(companyId);

		if (companyGroup == null) {
			return null;
		}

		return _fetchExistingFileEntryType(
			uuid, companyGroup.getGroupId(), fileEntryTypeKey, preloaded);
	}

	private boolean _isPreloaded(DLFileEntryType fileEntryType) {
		if ((fileEntryType.getFileEntryTypeId() ==
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT) ||
			(fileEntryType.getScope() ==
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_SCOPE_SYSTEM)) {

			return true;
		}

		User guestUser = _userLocalService.fetchGuestUser(
			fileEntryType.getCompanyId());

		if (guestUser == null) {
			return false;
		}

		if (guestUser.getUserId() == fileEntryType.getUserId()) {
			return true;
		}

		return false;
	}

	private void _validateDDMStructures(
			String fileEntryTypeKey, long[] ddmStructureIds)
		throws Exception {

		if (ddmStructureIds.length == 0) {
			throw new NoSuchMetadataSetException(
				"DDM structure IDs is empty for file entry type " +
					fileEntryTypeKey);
		}

		for (long ddmStructureId : ddmStructureIds) {
			DDMStructure ddmStructure =
				_ddmStructureLocalService.fetchStructure(ddmStructureId);

			if (ddmStructure == null) {
				throw new NoSuchMetadataSetException(
					"{ddmStructureId=" + ddmStructureId + "}");
			}
		}
	}

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private UserLocalService _userLocalService;

}