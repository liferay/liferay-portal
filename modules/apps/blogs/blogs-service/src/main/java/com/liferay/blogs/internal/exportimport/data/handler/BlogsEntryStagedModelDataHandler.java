/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.internal.exportimport.data.handler;

import com.liferay.asset.display.page.model.AssetDisplayPageEntry;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.exportimport.content.processor.ExportImportContentProcessor;
import com.liferay.exportimport.data.handler.base.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelModifiedDateComparator;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ImageLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashHandlerRegistryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.Element;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Zsolt Berentey
 * @author Roberto Díaz
 */
@Component(service = StagedModelDataHandler.class)
public class BlogsEntryStagedModelDataHandler
	extends BaseStagedModelDataHandler<BlogsEntry> {

	public static final String[] CLASS_NAMES = {BlogsEntry.class.getName()};

	@Override
	public void deleteStagedModel(BlogsEntry entry) throws PortalException {
		_blogsEntryLocalService.deleteEntry(entry);
	}

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException {

		BlogsEntry entry = fetchStagedModelByUuidAndGroupId(uuid, groupId);

		if (entry != null) {
			deleteStagedModel(entry);
		}
	}

	@Override
	public BlogsEntry fetchStagedModelByExternalReferenceCodeAndGroupId(
		String externalReferenceCode, long groupId) {

		return _blogsEntryLocalService.fetchBlogsEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	@Override
	public BlogsEntry fetchStagedModelByUuidAndGroupId(
		String uuid, long groupId) {

		return _blogsEntryLocalService.fetchBlogsEntryByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public List<BlogsEntry> fetchStagedModelsByUuidAndCompanyId(
		String uuid, long companyId) {

		return _blogsEntryLocalService.getBlogsEntriesByUuidAndCompanyId(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			new StagedModelModifiedDateComparator<>());
	}

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	public String getDisplayName(BlogsEntry entry) {
		return entry.getTitle();
	}

	@Override
	public int[] getExportableStatuses() {
		return new int[] {
			WorkflowConstants.STATUS_APPROVED,
			WorkflowConstants.STATUS_SCHEDULED
		};
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext, BlogsEntry entry)
		throws Exception {

		Element entryElement = portletDataContext.getExportDataElement(entry);

		if (entry.isSmallImage()) {
			if (entry.getSmallImageFileEntryId() > 0) {
				FileEntry fileEntry =
					_portletFileRepository.getPortletFileEntry(
						entry.getSmallImageFileEntryId());

				StagedModelDataHandlerUtil.exportReferenceStagedModel(
					portletDataContext, entry, fileEntry,
					PortletDataContext.REFERENCE_TYPE_WEAK);
			}
			else if (entry.getSmallImageId() > 0) {
				Image smallImage = _imageLocalService.fetchImage(
					entry.getSmallImageId());

				if ((smallImage != null) && (smallImage.getTextObj() != null)) {
					String smallImagePath = ExportImportPathUtil.getModelPath(
						entry,
						smallImage.getImageId() + StringPool.PERIOD +
							smallImage.getType());

					entryElement.addAttribute(
						"small-image-path", smallImagePath);

					entry.setSmallImageType(smallImage.getType());

					portletDataContext.addZipEntry(
						smallImagePath, smallImage.getTextObj());
				}
				else {
					if (_log.isWarnEnabled()) {
						_log.warn(
							StringBundler.concat(
								"Unable to export small image ",
								entry.getSmallImageId(), " to blogs entry ",
								entry.getEntryId()));
					}

					entry.setSmallImage(false);
					entry.setSmallImageId(0);
				}
			}
		}
		else if (Validator.isNotNull(entry.getSmallImageURL())) {
			entry.setSmallImage(true);
		}

		if (entry.getCoverImageFileEntryId() != 0) {
			FileEntry fileEntry = _portletFileRepository.getPortletFileEntry(
				entry.getCoverImageFileEntryId());

			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, entry, fileEntry,
				PortletDataContext.REFERENCE_TYPE_WEAK);
		}

		_exportFriendlyURLEntries(portletDataContext, entry);

		String content =
			_exportImportContentProcessor.replaceExportContentReferences(
				portletDataContext, entry, entry.getContent(),
				portletDataContext.getBooleanParameter(
					"blogs", "referenced-content"),
				true);

		entry.setContent(content);

		_exportAssetDisplayPage(portletDataContext, entry);

		portletDataContext.addClassedModel(
			entryElement, ExportImportPathUtil.getModelPath(entry), entry);
	}

	@Override
	protected void doImportMissingReference(
		PortletDataContext portletDataContext, String uuid, long groupId,
		long entryId) {

		BlogsEntry existingEntry = fetchMissingReference(uuid, groupId);

		if (existingEntry == null) {
			return;
		}

		Map<Long, Long> entryIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				BlogsEntry.class);

		entryIds.put(entryId, existingEntry.getEntryId());
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext, BlogsEntry entry)
		throws Exception {

		long userId = portletDataContext.getUserId(entry.getUserUuid());

		String content =
			_exportImportContentProcessor.replaceImportContentReferences(
				portletDataContext, entry, entry.getContent());

		entry.setContent(content);

		String[] trackbacks = StringUtil.split(entry.getTrackbacks());

		ServiceContext serviceContext = portletDataContext.createServiceContext(
			entry);

		BlogsEntry existingEntry = fetchExistingStagedModel(
			entry, portletDataContext.getScopeGroupId());

		long existingCoverImageFileEntryId = 0;
		long existingSmallImageFileEntryId = 0;

		if (existingEntry != null) {
			existingCoverImageFileEntryId =
				existingEntry.getCoverImageFileEntryId();
			existingSmallImageFileEntryId =
				existingEntry.getSmallImageFileEntryId();
		}

		if (portletDataContext.isDataStrategyMirror() &&
			(existingEntry == null)) {

			serviceContext.setUuid(entry.getUuid());
		}

		BlogsEntry importedEntry = null;

		if ((existingEntry == null) ||
			!portletDataContext.isDataStrategyMirror()) {

			String urlTitle = entry.getUrlTitle();

			existingEntry = _blogsEntryLocalService.fetchEntry(
				portletDataContext.getScopeGroupId(), entry.getUrlTitle());

			if (existingEntry != null) {
				urlTitle = null;
			}

			importedEntry = _blogsEntryLocalService.addEntry(
				entry.getExternalReferenceCode(), userId, entry.getTitle(),
				entry.getSubtitle(), urlTitle, entry.getDescription(),
				entry.getContent(), entry.getDisplayDate(),
				entry.isAllowPingbacks(), entry.isAllowTrackbacks(), trackbacks,
				entry.getCoverImageCaption(), null, null, serviceContext);
		}
		else {
			importedEntry = _blogsEntryLocalService.updateEntry(
				userId, existingEntry.getEntryId(), entry.getTitle(),
				entry.getSubtitle(), entry.getUrlTitle(),
				entry.getDescription(), entry.getContent(),
				entry.getDisplayDate(), entry.isAllowPingbacks(),
				entry.isAllowTrackbacks(), trackbacks,
				entry.getCoverImageCaption(), null, null, serviceContext);

			importedEntry.setUuid(entry.getUuid());

			importedEntry = _blogsEntryLocalService.updateBlogsEntry(
				importedEntry);
		}

		serviceContext.setModifiedDate(importedEntry.getModifiedDate());

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {

			// Cover image

			Map<Long, Long> fileEntryIds =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					FileEntry.class);

			importedEntry.setCoverImageFileEntryId(
				MapUtil.getLong(
					fileEntryIds, entry.getCoverImageFileEntryId(), 0));

			importedEntry = _blogsEntryLocalService.updateBlogsEntry(
				importedEntry);

			if ((existingCoverImageFileEntryId != 0) &&
				(entry.getCoverImageFileEntryId() == 0)) {

				_portletFileRepository.deletePortletFileEntry(
					existingCoverImageFileEntryId);
			}

			// Small image

			long smallImageFileEntryId = 0;

			if (entry.isSmallImage()) {
				smallImageFileEntryId = MapUtil.getLong(
					fileEntryIds, entry.getSmallImageFileEntryId(), 0);
			}

			importedEntry.setSmallImageFileEntryId(smallImageFileEntryId);
			importedEntry.setSmallImageURL(entry.getSmallImageURL());

			if ((smallImageFileEntryId == 0) &&
				Validator.isNull(importedEntry.getSmallImageURL())) {

				importedEntry.setSmallImage(false);
			}
			else {
				importedEntry.setSmallImage(true);
			}

			importedEntry = _blogsEntryLocalService.updateBlogsEntry(
				importedEntry);

			if ((existingSmallImageFileEntryId != 0) &&
				(entry.getSmallImageFileEntryId() == 0)) {

				_portletFileRepository.deletePortletFileEntry(
					existingSmallImageFileEntryId);
			}

			Map<Long, Long> newPrimaryKeysMap =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					BlogsEntry.class);

			newPrimaryKeysMap.put(
				entry.getEntryId(), importedEntry.getEntryId());

			_importAssetDisplayPage(portletDataContext, entry, importedEntry);
			_importFriendlyURLEntries(portletDataContext, entry, importedEntry);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		portletDataContext.importClassedModel(entry, importedEntry);
	}

	@Override
	protected void doRestoreStagedModel(
			PortletDataContext portletDataContext, BlogsEntry entry)
		throws Exception {

		BlogsEntry existingEntry = fetchStagedModelByUuidAndGroupId(
			entry.getUuid(), portletDataContext.getScopeGroupId());

		if ((existingEntry == null) || !existingEntry.isInTrash()) {
			return;
		}

		TrashHandler trashHandler = TrashHandlerRegistryUtil.getTrashHandler(
			BlogsEntry.class.getName());

		if (trashHandler.isRestorable(existingEntry.getEntryId())) {
			trashHandler.restoreTrashEntry(
				portletDataContext.getUserId(entry.getUserUuid()),
				existingEntry.getEntryId());
		}
	}

	@Override
	protected String[] getSkipImportReferenceStagedModelNames() {
		return new String[] {
			AssetDisplayPageEntry.class.getName(),
			FriendlyURLEntry.class.getName()
		};
	}

	private void _exportAssetDisplayPage(
			PortletDataContext portletDataContext, BlogsEntry entry)
		throws Exception {

		AssetDisplayPageEntry assetDisplayPageEntry =
			_assetDisplayPageEntryLocalService.fetchAssetDisplayPageEntry(
				entry.getGroupId(), _portal.getClassNameId(BlogsEntry.class),
				entry.getEntryId());

		if (assetDisplayPageEntry != null) {
			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, entry, assetDisplayPageEntry,
				PortletDataContext.REFERENCE_TYPE_DEPENDENCY);
		}
	}

	private void _exportFriendlyURLEntries(
			PortletDataContext portletDataContext, BlogsEntry blogsEntry)
		throws Exception {

		List<FriendlyURLEntry> friendlyURLEntries =
			_friendlyURLEntryLocalService.getFriendlyURLEntries(
				blogsEntry.getGroupId(),
				_portal.getClassNameId(BlogsEntry.class),
				blogsEntry.getEntryId());

		for (FriendlyURLEntry friendlyURLEntry : friendlyURLEntries) {
			StagedModelDataHandlerUtil.exportStagedModel(
				portletDataContext, friendlyURLEntry);

			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, blogsEntry, friendlyURLEntry,
				PortletDataContext.REFERENCE_TYPE_DEPENDENCY);
		}
	}

	private void _importAssetDisplayPage(
			PortletDataContext portletDataContext, BlogsEntry entry,
			BlogsEntry importedEntry)
		throws PortalException {

		List<Element> assetDisplayPageEntryElements =
			portletDataContext.getReferenceDataElements(
				entry, AssetDisplayPageEntry.class);

		Map<Long, Long> articleNewPrimaryKeys =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				BlogsEntry.class);

		articleNewPrimaryKeys.put(
			entry.getEntryId(), importedEntry.getEntryId());

		if (ListUtil.isEmpty(assetDisplayPageEntryElements)) {
			AssetDisplayPageEntry existingAssetDisplayPageEntry =
				_assetDisplayPageEntryLocalService.fetchAssetDisplayPageEntry(
					importedEntry.getGroupId(),
					_portal.getClassNameId(BlogsEntry.class.getName()),
					importedEntry.getEntryId());

			if (existingAssetDisplayPageEntry != null) {
				_assetDisplayPageEntryLocalService.deleteAssetDisplayPageEntry(
					existingAssetDisplayPageEntry);
			}

			return;
		}

		for (Element assetDisplayPageEntryElement :
				assetDisplayPageEntryElements) {

			String path = assetDisplayPageEntryElement.attributeValue("path");

			AssetDisplayPageEntry assetDisplayPageEntry =
				(AssetDisplayPageEntry)portletDataContext.getZipEntryAsObject(
					path);

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, assetDisplayPageEntryElement);

			Map<Long, Long> assetDisplayPageEntries =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					AssetDisplayPageEntry.class);

			long assetDisplayPageEntryId = MapUtil.getLong(
				assetDisplayPageEntries,
				assetDisplayPageEntry.getAssetDisplayPageEntryId(),
				assetDisplayPageEntry.getAssetDisplayPageEntryId());

			AssetDisplayPageEntry existingAssetDisplayPageEntry =
				_assetDisplayPageEntryLocalService.fetchAssetDisplayPageEntry(
					assetDisplayPageEntryId);

			if (existingAssetDisplayPageEntry == null) {
				continue;
			}

			existingAssetDisplayPageEntry.setClassPK(
				importedEntry.getEntryId());

			_assetDisplayPageEntryLocalService.updateAssetDisplayPageEntry(
				existingAssetDisplayPageEntry);
		}
	}

	private void _importFriendlyURLEntries(
			PortletDataContext portletDataContext, BlogsEntry entry,
			BlogsEntry importedEntry)
		throws PortalException {

		List<Element> friendlyURLEntryElements =
			portletDataContext.getReferenceDataElements(
				entry, FriendlyURLEntry.class);

		Map<Long, Long> articleNewPrimaryKeys =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				BlogsEntry.class);

		articleNewPrimaryKeys.put(
			entry.getEntryId(), importedEntry.getEntryId());

		for (Element friendlyURLEntryElement : friendlyURLEntryElements) {
			String path = friendlyURLEntryElement.attributeValue("path");

			FriendlyURLEntry friendlyURLEntry =
				(FriendlyURLEntry)portletDataContext.getZipEntryAsObject(path);

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, friendlyURLEntryElement);

			Map<Long, Long> friendlyURLEntries =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					FriendlyURLEntry.class);

			long friendlyURLEntryId = MapUtil.getLong(
				friendlyURLEntries, friendlyURLEntry.getFriendlyURLEntryId(),
				friendlyURLEntry.getFriendlyURLEntryId());

			FriendlyURLEntry existingFriendlyURLEntry =
				_friendlyURLEntryLocalService.fetchFriendlyURLEntry(
					friendlyURLEntryId);

			if (existingFriendlyURLEntry == null) {
				continue;
			}

			existingFriendlyURLEntry.setClassPK(importedEntry.getEntryId());

			_friendlyURLEntryLocalService.updateFriendlyURLEntry(
				existingFriendlyURLEntry);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BlogsEntryStagedModelDataHandler.class);

	@Reference
	private AssetDisplayPageEntryLocalService
		_assetDisplayPageEntryLocalService;

	@Reference
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(model.class.name=com.liferay.blogs.model.BlogsEntry)"
	)
	private volatile ExportImportContentProcessor<String>
		_exportImportContentProcessor;

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference
	private ImageLocalService _imageLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletFileRepository _portletFileRepository;

}