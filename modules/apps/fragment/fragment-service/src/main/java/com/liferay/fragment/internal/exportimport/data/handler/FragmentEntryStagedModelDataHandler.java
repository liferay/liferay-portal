/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.exportimport.data.handler;

import com.liferay.exportimport.content.processor.ExportImportContentProcessor;
import com.liferay.exportimport.data.handler.base.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.staged.model.repository.StagedModelRepository;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.xml.Element;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(service = StagedModelDataHandler.class)
public class FragmentEntryStagedModelDataHandler
	extends BaseStagedModelDataHandler<FragmentEntry> {

	public static final String[] CLASS_NAMES = {FragmentEntry.class.getName()};

	@Override
	public void deleteStagedModel(FragmentEntry fragmentEntry)
		throws PortalException {

		_stagedModelRepository.deleteStagedModel(fragmentEntry);
	}

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException {

		_stagedModelRepository.deleteStagedModel(
			uuid, groupId, className, extraData);
	}

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	public String getDisplayName(FragmentEntry fragmentEntry) {
		return fragmentEntry.getName();
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext, FragmentEntry fragmentEntry)
		throws Exception {

		if (fragmentEntry.isMarketplace() &&
			!ExportImportThreadLocal.isStagingInProcess()) {

			return;
		}

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.fetchFragmentCollection(
				fragmentEntry.getFragmentCollectionId());

		if (fragmentCollection == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to export fragment entry with key " +
						fragmentEntry.getFragmentEntryKey());
			}

			return;
		}

		StagedModelDataHandlerUtil.exportReferenceStagedModel(
			portletDataContext, fragmentEntry, fragmentCollection,
			PortletDataContext.REFERENCE_TYPE_PARENT);

		if (fragmentEntry.getPreviewFileEntryId() > 0) {
			try {
				FileEntry fileEntry =
					PortletFileRepositoryUtil.getPortletFileEntry(
						fragmentEntry.getPreviewFileEntryId());

				StagedModelDataHandlerUtil.exportReferenceStagedModel(
					portletDataContext, fragmentEntry, fileEntry,
					PortletDataContext.REFERENCE_TYPE_WEAK);
			}
			catch (PortalException portalException) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to export file entry " +
							fragmentEntry.getPreviewFileEntryId(),
						portalException);
				}
			}
		}

		String html =
			_dlReferencesExportImportContentProcessor.
				replaceExportContentReferences(
					portletDataContext, fragmentEntry, fragmentEntry.getHtml(),
					true, false);

		fragmentEntry.setHtml(html);

		Element entryElement = portletDataContext.getExportDataElement(
			fragmentEntry);

		portletDataContext.addClassedModel(
			entryElement, ExportImportPathUtil.getModelPath(fragmentEntry),
			fragmentEntry);
	}

	@Override
	protected void doImportMissingReference(
			PortletDataContext portletDataContext, String uuid, long groupId,
			long fragmentEntryId)
		throws Exception {

		FragmentEntry existingFragmentEntry = fetchMissingReference(
			uuid, groupId);

		if (existingFragmentEntry == null) {
			return;
		}

		Map<Long, Long> fragmentEntryIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				FragmentEntry.class);

		fragmentEntryIds.put(
			fragmentEntryId, existingFragmentEntry.getFragmentEntryId());
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext, FragmentEntry fragmentEntry)
		throws Exception {

		Map<Long, Long> fragmentCollectionIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				FragmentCollection.class);

		long fragmentCollectionId = MapUtil.getLong(
			fragmentCollectionIds, fragmentEntry.getFragmentCollectionId(),
			fragmentEntry.getFragmentCollectionId());

		FragmentEntry importedFragmentEntry =
			(FragmentEntry)fragmentEntry.clone();

		importedFragmentEntry.setGroupId(portletDataContext.getScopeGroupId());
		importedFragmentEntry.setFragmentCollectionId(fragmentCollectionId);
		importedFragmentEntry.setHtml(
			_dlReferencesExportImportContentProcessor.
				replaceImportContentReferences(
					portletDataContext, fragmentEntry,
					fragmentEntry.getHtml()));

		FragmentEntry existingFragmentEntry =
			_stagedModelRepository.fetchStagedModelByUuidAndGroupId(
				fragmentEntry.getUuid(), portletDataContext.getScopeGroupId());

		if ((existingFragmentEntry == null) ||
			!portletDataContext.isDataStrategyMirror()) {

			importedFragmentEntry = _stagedModelRepository.addStagedModel(
				portletDataContext, importedFragmentEntry);
		}
		else {
			importedFragmentEntry.setMvccVersion(
				existingFragmentEntry.getMvccVersion());
			importedFragmentEntry.setFragmentEntryId(
				existingFragmentEntry.getFragmentEntryId());

			importedFragmentEntry = _stagedModelRepository.updateStagedModel(
				portletDataContext, importedFragmentEntry);
		}

		if ((fragmentEntry.getPreviewFileEntryId() == 0) &&
			(importedFragmentEntry.getPreviewFileEntryId() > 0)) {

			PortletFileRepositoryUtil.deletePortletFileEntry(
				importedFragmentEntry.getPreviewFileEntryId());

			importedFragmentEntry =
				_fragmentEntryLocalService.updateFragmentEntry(
					importedFragmentEntry.getFragmentEntryId(), 0);
		}
		else if (fragmentEntry.getPreviewFileEntryId() > 0) {
			Map<Long, Long> fileEntryIds =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					FileEntry.class);

			long previewFileEntryId = MapUtil.getLong(
				fileEntryIds, fragmentEntry.getPreviewFileEntryId(), 0);

			importedFragmentEntry =
				_fragmentEntryLocalService.updateFragmentEntry(
					importedFragmentEntry.getFragmentEntryId(),
					previewFileEntryId);
		}

		portletDataContext.importClassedModel(
			fragmentEntry, importedFragmentEntry);
	}

	@Override
	protected StagedModelRepository<FragmentEntry> getStagedModelRepository() {
		return _stagedModelRepository;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FragmentEntryStagedModelDataHandler.class);

	@Reference(target = "(content.processor.type=DLReferences)")
	private ExportImportContentProcessor<String>
		_dlReferencesExportImportContentProcessor;

	@Reference
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Reference
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.fragment.model.FragmentEntry)",
		unbind = "-"
	)
	private StagedModelRepository<FragmentEntry> _stagedModelRepository;

}