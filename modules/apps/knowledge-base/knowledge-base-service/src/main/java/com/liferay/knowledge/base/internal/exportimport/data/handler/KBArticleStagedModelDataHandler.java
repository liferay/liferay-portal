/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.internal.exportimport.data.handler;

import com.liferay.document.library.kernel.exception.DuplicateFileEntryException;
import com.liferay.document.library.kernel.exception.NoSuchFileException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.exportimport.content.processor.ExportImportContentProcessor;
import com.liferay.exportimport.kernel.lar.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelModifiedDateComparator;
import com.liferay.knowledge.base.constants.KBArticleConstants;
import com.liferay.knowledge.base.constants.KBFolderConstants;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.service.KBArticleLocalService;
import com.liferay.knowledge.base.service.KBFolderLocalService;
import com.liferay.knowledge.base.util.AdminHelper;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashHandlerRegistryUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portlet.documentlibrary.lar.FileEntryUtil;

import java.io.InputStream;
import java.io.Serializable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Daniel Kocsis
 */
@Component(service = StagedModelDataHandler.class)
public class KBArticleStagedModelDataHandler
	extends BaseStagedModelDataHandler<KBArticle> {

	public static final String[] CLASS_NAMES = {KBArticle.class.getName()};

	@Override
	public void deleteStagedModel(KBArticle kbArticle) throws PortalException {
		_kbArticleLocalService.deleteKBArticle(kbArticle);
	}

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException {

		KBArticle kbArticle = fetchStagedModelByUuidAndGroupId(uuid, groupId);

		if (kbArticle != null) {
			deleteStagedModel(kbArticle);
		}
	}

	@Override
	public KBArticle fetchStagedModelByUuidAndGroupId(
		String uuid, long groupId) {

		return _kbArticleLocalService.fetchKBArticleByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public List<KBArticle> fetchStagedModelsByUuidAndCompanyId(
		String uuid, long companyId) {

		return _kbArticleLocalService.getKBArticlesByUuidAndCompanyId(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			new StagedModelModifiedDateComparator<>());
	}

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	public String getDisplayName(KBArticle kbArticle) {
		return kbArticle.getTitle();
	}

	@Override
	public int[] getExportableStatuses() {
		return new int[] {
			WorkflowConstants.STATUS_APPROVED,
			WorkflowConstants.STATUS_SCHEDULED
		};
	}

	@Override
	protected boolean countStagedModel(
		PortletDataContext portletDataContext, KBArticle kbArticle) {

		return !portletDataContext.isModelCounted(
			KBArticle.class.getName(),
			(Serializable)kbArticle.getResourcePrimKey());
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext, KBArticle kbArticle)
		throws Exception {

		if (kbArticle.getParentResourcePrimKey() !=
				KBFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

			long kbArticleClassNameId = _classNameLocalService.getClassNameId(
				KBArticleConstants.getClassName());

			if (kbArticle.getParentResourceClassNameId() ==
					kbArticleClassNameId) {

				KBArticle parentKBArticle =
					_kbArticleLocalService.getLatestKBArticle(
						kbArticle.getParentResourcePrimKey(),
						getExportableStatuses());

				StagedModelDataHandlerUtil.exportReferenceStagedModel(
					portletDataContext, kbArticle, parentKBArticle,
					PortletDataContext.REFERENCE_TYPE_PARENT);
			}
			else {
				KBFolder parentKBFolder = _kbFolderLocalService.getKBFolder(
					kbArticle.getParentResourcePrimKey());

				StagedModelDataHandlerUtil.exportReferenceStagedModel(
					portletDataContext, kbArticle, parentKBFolder,
					PortletDataContext.REFERENCE_TYPE_PARENT);
			}
		}

		_exportKBArticleAttachments(portletDataContext, kbArticle);

		String content =
			_kbArticleExportImportContentProcessor.
				replaceExportContentReferences(
					portletDataContext, kbArticle, kbArticle.getContent(), true,
					true);

		kbArticle.setContent(content);

		Element kbArticleElement = portletDataContext.getExportDataElement(
			kbArticle);

		portletDataContext.addClassedModel(
			kbArticleElement, ExportImportPathUtil.getModelPath(kbArticle),
			kbArticle);
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext, KBArticle kbArticle)
		throws Exception {

		StagedModelDataHandlerUtil.importReferenceStagedModels(
			portletDataContext, kbArticle, KBArticle.class);

		StagedModelDataHandlerUtil.importReferenceStagedModels(
			portletDataContext, kbArticle, KBFolder.class);

		Map<Long, Long> kbArticleResourcePrimKeys =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				KBArticle.class);

		long parentResourceClassNameId = _classNameLocalService.getClassNameId(
			KBFolderConstants.getClassName());
		long parentResourcePrimKey = KBFolderConstants.DEFAULT_PARENT_FOLDER_ID;

		long kbArticleClassNameId = _classNameLocalService.getClassNameId(
			KBArticleConstants.getClassName());

		if (kbArticleClassNameId == kbArticle.getParentResourceClassNameId()) {
			parentResourceClassNameId = _classNameLocalService.getClassNameId(
				KBArticleConstants.getClassName());
			parentResourcePrimKey = MapUtil.getLong(
				kbArticleResourcePrimKeys, kbArticle.getParentResourcePrimKey(),
				KBFolderConstants.DEFAULT_PARENT_FOLDER_ID);
		}
		else if (kbArticle.getParentResourcePrimKey() !=
					KBFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

			Map<Long, Long> kbFolderResourcePrimKeys =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					KBFolder.class);

			parentResourcePrimKey = MapUtil.getLong(
				kbFolderResourcePrimKeys, kbArticle.getParentResourcePrimKey(),
				KBFolderConstants.DEFAULT_PARENT_FOLDER_ID);
		}

		String[] sections = _adminHelper.unescapeSections(
			kbArticle.getSections());

		String content =
			_kbArticleExportImportContentProcessor.
				replaceImportContentReferences(
					portletDataContext, kbArticle, kbArticle.getContent());

		kbArticle.setContent(content);

		long resourcePrimaryKey = MapUtil.getLong(
			kbArticleResourcePrimKeys, kbArticle.getResourcePrimKey(),
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		ServiceContext serviceContext = portletDataContext.createServiceContext(
			kbArticle);

		long userId = portletDataContext.getUserId(kbArticle.getUserUuid());

		KBArticle importedKBArticle = null;

		if (portletDataContext.isDataStrategyMirror()) {
			KBArticle existingKBArticle = _findExistingKBArticle(
				portletDataContext, resourcePrimaryKey, kbArticle,
				serviceContext);

			if (existingKBArticle == null) {
				importedKBArticle = _addKBArticle(
					userId, parentResourceClassNameId, parentResourcePrimKey,
					kbArticle, sections, serviceContext);
			}
			else {
				importedKBArticle = _updateKBArticle(
					userId, existingKBArticle.getResourcePrimKey(),
					parentResourceClassNameId, parentResourcePrimKey, kbArticle,
					sections, serviceContext);
			}
		}
		else {
			if (resourcePrimaryKey == kbArticle.getResourcePrimKey()) {
				importedKBArticle = _addKBArticle(
					userId, parentResourceClassNameId, parentResourcePrimKey,
					kbArticle, sections, serviceContext);
			}
			else {
				importedKBArticle = _updateKBArticle(
					userId, resourcePrimaryKey, parentResourceClassNameId,
					parentResourcePrimKey, kbArticle, sections, serviceContext);
			}
		}

		_importKBArticleAttachments(
			portletDataContext, kbArticle, importedKBArticle);

		portletDataContext.importClassedModel(kbArticle, importedKBArticle);

		if (!kbArticle.isMain()) {
			kbArticleResourcePrimKeys.put(
				kbArticle.getResourcePrimKey(),
				importedKBArticle.getResourcePrimKey());
		}
	}

	@Override
	protected void doRestoreStagedModel(
			PortletDataContext portletDataContext, KBArticle kbArticle)
		throws Exception {

		KBArticle existingKBArticle = fetchStagedModelByUuidAndGroupId(
			kbArticle.getUuid(), portletDataContext.getScopeGroupId());

		if ((existingKBArticle == null) || !existingKBArticle.isInTrash()) {
			return;
		}

		TrashHandler trashHandler = TrashHandlerRegistryUtil.getTrashHandler(
			KBArticle.class.getName());

		if (trashHandler.isRestorable(existingKBArticle.getResourcePrimKey())) {
			trashHandler.restoreTrashEntry(
				portletDataContext.getUserId(kbArticle.getUserUuid()),
				existingKBArticle.getResourcePrimKey());
		}
	}

	@Override
	protected void importReferenceStagedModels(
			PortletDataContext portletDataContext, KBArticle stagedModel)
		throws PortletDataException {

		super.importReferenceStagedModels(portletDataContext, stagedModel);

		Element stagedModelElement =
			portletDataContext.getImportDataStagedModelElement(stagedModel);

		long kbFolderClassNameId = _portal.getClassNameId(
			KBFolderConstants.getClassName());

		stagedModel.setParentResourceClassNameId(kbFolderClassNameId);

		Element referencesElement = stagedModelElement.element("references");

		if (referencesElement == null) {
			return;
		}

		long kbArticleClassNameId = _portal.getClassNameId(
			KBArticleConstants.getClassName());

		List<Element> referenceElements = referencesElement.elements();

		for (Element referenceElement : referenceElements) {
			String referenceType = referenceElement.attributeValue("type");

			if (referenceType.equals(
					PortletDataContext.REFERENCE_TYPE_PARENT)) {

				String className = referenceElement.attributeValue(
					"class-name");

				if (className.equals(KBArticle.class.getName())) {
					stagedModel.setParentResourceClassNameId(
						kbArticleClassNameId);
				}

				break;
			}
		}
	}

	private KBArticle _addKBArticle(
			long userId, long parentResourceClassNameId,
			long parentResourcePrimKey, KBArticle kbArticle, String[] sections,
			ServiceContext serviceContext)
		throws Exception {

		KBArticle importedKBArticle = _kbArticleLocalService.addKBArticle(
			kbArticle.getExternalReferenceCode(), userId,
			parentResourceClassNameId, parentResourcePrimKey,
			kbArticle.getTitle(), kbArticle.getUrlTitle(),
			kbArticle.getContent(), kbArticle.getDescription(), sections,
			kbArticle.getSourceURL(), kbArticle.getDisplayDate(),
			kbArticle.getExpirationDate(), kbArticle.getReviewDate(), null,
			serviceContext);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			_kbArticleLocalService.updatePriority(
				importedKBArticle.getResourcePrimKey(),
				kbArticle.getPriority());
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		return importedKBArticle;
	}

	private void _exportKBArticleAttachments(
			PortletDataContext portletDataContext, KBArticle kbArticle)
		throws Exception {

		for (FileEntry fileEntry : kbArticle.getAttachmentsFileEntries()) {
			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, kbArticle, fileEntry,
				PortletDataContext.REFERENCE_TYPE_WEAK);
		}
	}

	private KBArticle _findExistingKBArticle(
		PortletDataContext portletDataContext, long resourcePrimaryKey,
		KBArticle kbArticle, ServiceContext serviceContext) {

		KBArticle existingKBArticle = _kbArticleLocalService.fetchKBArticle(
			resourcePrimaryKey, portletDataContext.getScopeGroupId(),
			kbArticle.getVersion());

		if (existingKBArticle != null) {
			return existingKBArticle;
		}

		existingKBArticle = fetchStagedModelByUuidAndGroupId(
			kbArticle.getUuid(), portletDataContext.getScopeGroupId());

		if (existingKBArticle != null) {
			return existingKBArticle;
		}

		serviceContext.setUuid(kbArticle.getUuid());

		existingKBArticle = _kbArticleLocalService.fetchLatestKBArticle(
			resourcePrimaryKey, portletDataContext.getScopeGroupId());

		if (existingKBArticle != null) {
			return existingKBArticle;
		}

		Map<Long, Long> kbFolderIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				KBFolder.class);

		long kbFolderId = MapUtil.getLong(
			kbFolderIds, kbArticle.getKbFolderId(), kbArticle.getKbFolderId());

		return _kbArticleLocalService.fetchLatestKBArticleByUrlTitle(
			portletDataContext.getScopeGroupId(), kbFolderId,
			kbArticle.getUrlTitle(), WorkflowConstants.STATUS_ANY);
	}

	private InputStream _getKBArticalAttachmentInputStream(
			String binPath, PortletDataContext portletDataContext,
			FileEntry fileEntry)
		throws Exception {

		if (Validator.isNull(binPath) &&
			portletDataContext.isPerformDirectBinaryImport()) {

			try {
				return FileEntryUtil.getContentStream(fileEntry);
			}
			catch (NoSuchFileException noSuchFileException) {

				// LPS-52675

				if (_log.isDebugEnabled()) {
					_log.debug(noSuchFileException);
				}

				return null;
			}
		}

		return portletDataContext.getZipEntryAsInputStream(binPath);
	}

	private void _importKBArticleAttachments(
			PortletDataContext portletDataContext, KBArticle kbArticle,
			KBArticle importedKBArticle)
		throws Exception {

		List<Element> dlFileEntryElements =
			portletDataContext.getReferenceDataElements(
				kbArticle, DLFileEntry.class);

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(portletDataContext.getCompanyId());
		serviceContext.setScopeGroupId(portletDataContext.getScopeGroupId());

		Map<String, FileEntry> attachments = new HashMap<>();

		for (FileEntry attachment :
				importedKBArticle.getAttachmentsFileEntries()) {

			attachments.put(attachment.getUuid(), attachment);
		}

		for (Element dlFileEntryElement : dlFileEntryElements) {
			String path = dlFileEntryElement.attributeValue("path");

			FileEntry fileEntry =
				(FileEntry)portletDataContext.getZipEntryAsObject(path);

			if (attachments.get(fileEntry.getUuid()) != null) {
				attachments.remove(fileEntry.getUuid());

				continue;
			}

			DLFileEntry importedFileEntry =
				_dlFileEntryLocalService.getFileEntryByUuidAndGroupId(
					fileEntry.getUuid(), portletDataContext.getScopeGroupId());

			if (importedFileEntry != null) {
				if (importedFileEntry.getFolderId() !=
						importedKBArticle.getAttachmentsFolderId()) {

					importedFileEntry.setClassName(KBArticle.class.getName());
					importedFileEntry.setClassPK(
						importedKBArticle.getClassPK());
					importedFileEntry.setFolderId(
						importedKBArticle.getAttachmentsFolderId());

					_dlFileEntryLocalService.updateDLFileEntry(
						importedFileEntry);
				}

				continue;
			}

			String binPath = dlFileEntryElement.attributeValue("bin-path");

			try (InputStream inputStream = _getKBArticalAttachmentInputStream(
					binPath, portletDataContext, fileEntry)) {

				if (inputStream == null) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to import attachment for file entry " +
								fileEntry.getFileEntryId());
					}

					continue;
				}

				_portletFileRepository.addPortletFileEntry(
					null, portletDataContext.getScopeGroupId(),
					portletDataContext.getUserId(
						importedKBArticle.getUserUuid()),
					KBArticle.class.getName(), importedKBArticle.getClassPK(),
					KBPortletKeys.KNOWLEDGE_BASE_ADMIN,
					importedKBArticle.getAttachmentsFolderId(), inputStream,
					fileEntry.getFileName(), fileEntry.getMimeType(), true);
			}
			catch (DuplicateFileEntryException duplicateFileEntryException) {

				// LPS-52675

				if (_log.isDebugEnabled()) {
					_log.debug(duplicateFileEntryException);
				}
			}
		}

		for (FileEntry unreferencedAttachment : attachments.values()) {
			_portletFileRepository.deletePortletFileEntry(
				unreferencedAttachment.getFileEntryId());
		}
	}

	private KBArticle _updateKBArticle(
			long userId, long resourcePrimKey, long parentResourceClassNameId,
			long parentResourcePrimKey, KBArticle kbArticle, String[] sections,
			ServiceContext serviceContext)
		throws Exception {

		_kbArticleLocalService.updateKBArticle(
			userId, resourcePrimKey, kbArticle.getTitle(),
			kbArticle.getContent(), kbArticle.getDescription(), sections,
			kbArticle.getSourceURL(), kbArticle.getDisplayDate(),
			kbArticle.getExpirationDate(), kbArticle.getReviewDate(), null,
			null, serviceContext);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			_kbArticleLocalService.moveKBArticle(
				userId, resourcePrimKey, parentResourceClassNameId,
				parentResourcePrimKey, kbArticle.getPriority());
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		return _kbArticleLocalService.getLatestKBArticle(
			resourcePrimKey, kbArticle.getStatus());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		KBArticleStagedModelDataHandler.class);

	@Reference
	private AdminHelper _adminHelper;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.knowledge.base.model.KBArticle)"
	)
	private ExportImportContentProcessor<String>
		_kbArticleExportImportContentProcessor;

	@Reference
	private KBArticleLocalService _kbArticleLocalService;

	@Reference
	private KBFolderLocalService _kbFolderLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletFileRepository _portletFileRepository;

}