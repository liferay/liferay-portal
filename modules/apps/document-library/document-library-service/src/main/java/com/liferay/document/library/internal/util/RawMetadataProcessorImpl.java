/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.util;

import com.liferay.document.library.configuration.DLFileEntryRawMetadataProcessorConfigurationProvider;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLProcessorConstants;
import com.liferay.document.library.kernel.processor.DLProcessor;
import com.liferay.document.library.kernel.processor.RawMetadataProcessor;
import com.liferay.document.library.kernel.service.DLFileEntryMetadataLocalService;
import com.liferay.dynamic.data.mapping.kernel.DDMFormValues;
import com.liferay.dynamic.data.mapping.kernel.DDMStructure;
import com.liferay.dynamic.data.mapping.kernel.DDMStructureManager;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.kernel.metadata.RawMetadataProcessorUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GroupThreadLocal;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.repository.liferayrepository.model.LiferayFileEntry;

import java.io.IOException;
import java.io.InputStream;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alexander Chow
 * @author Mika Koivisto
 * @author Miguel Pastor
 */
@Component(
	property = "type=" + DLProcessorConstants.RAW_METADATA_PROCESSOR,
	service = DLProcessor.class
)
public class RawMetadataProcessorImpl
	implements DLProcessor, RawMetadataProcessor {

	@Override
	public void cleanUp(FileEntry fileEntry) {
	}

	@Override
	public void cleanUp(FileVersion fileVersion) {
	}

	@Override
	public void copy(
		FileVersion sourceFileVersion, FileVersion destinationFileVersion) {
	}

	@Override
	public void exportGeneratedFiles(
		PortletDataContext portletDataContext, FileEntry fileEntry,
		Element fileEntryElement) {
	}

	@Override
	public void generateMetadata(FileVersion fileVersion) {
		long fileEntryMetadataCount =
			_dlFileEntryMetadataLocalService.
				getFileVersionFileEntryMetadatasCount(
					fileVersion.getFileVersionId());

		if (fileEntryMetadataCount == 0) {
			trigger(fileVersion);
		}
	}

	@Override
	public String getType() {
		return DLProcessorConstants.RAW_METADATA_PROCESSOR;
	}

	@Override
	public void importGeneratedFiles(
		PortletDataContext portletDataContext, FileEntry fileEntry,
		FileEntry importedFileEntry, Element fileEntryElement) {
	}

	@Override
	public boolean isSupported(FileVersion fileVersion) {
		return _isSupported(
			fileVersion.getGroupId(), fileVersion.getMimeType());
	}

	@Override
	public boolean isSupported(String mimeType) {
		return _isSupported(GroupThreadLocal.getGroupId(), mimeType);
	}

	@Override
	public void saveMetadata(FileVersion fileVersion) throws PortalException {
		Map<String, DDMFormValues> rawMetadataMap = null;

		try (InputStream inputStream = fileVersion.getContentStream(false)) {
			if (inputStream == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"No metadata is available for file version " +
							fileVersion.getFileVersionId());
				}

				return;
			}

			rawMetadataMap = RawMetadataProcessorUtil.getRawMetadataMap(
				fileVersion.getMimeType(), inputStream);
		}
		catch (IOException ioException) {
			if (_log.isWarnEnabled()) {
				_log.warn(ioException);
			}
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					fileVersion.getCtCollectionId())) {

			List<DDMStructure> ddmStructures =
				_ddmStructureManager.getClassStructures(
					fileVersion.getCompanyId(),
					_portal.getClassNameId(RawMetadataProcessor.class));

			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setScopeGroupId(fileVersion.getGroupId());
			serviceContext.setUserId(fileVersion.getUserId());

			_dlFileEntryMetadataLocalService.updateFileEntryMetadata(
				null, fileVersion.getCompanyId(), ddmStructures,
				fileVersion.getFileEntryId(), fileVersion.getFileVersionId(),
				rawMetadataMap, serviceContext);

			FileEntry fileEntry = fileVersion.getFileEntry();

			if (fileEntry instanceof LiferayFileEntry) {
				Indexer<DLFileEntry> indexer = IndexerRegistryUtil.getIndexer(
					DLFileEntryConstants.getClassName());

				if (indexer != null) {
					LiferayFileEntry liferayFileEntry =
						(LiferayFileEntry)fileEntry;

					indexer.reindex(liferayFileEntry.getDLFileEntry());
				}
			}
		}
	}

	@Override
	public void trigger(FileVersion fileVersion) {
		trigger(fileVersion, fileVersion);
	}

	@Override
	public void trigger(
		FileVersion sourceFileVersion, FileVersion destinationFileVersion) {

		MessageBusUtil.sendMessage(
			DestinationNames.DOCUMENT_LIBRARY_RAW_METADATA_PROCESSOR,
			destinationFileVersion);
	}

	private boolean _isSupported(long groupId, String mimeType) {
		try {
			if (ArrayUtil.contains(
					_dlFileEntryRawMetadataProcessorConfigurationProvider.
						getGroupExcludedMimeTypes(groupId),
					mimeType)) {

				return false;
			}
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}

			return false;
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RawMetadataProcessorImpl.class);

	@Reference
	private DDMStructureManager _ddmStructureManager;

	@Reference
	private DLFileEntryMetadataLocalService _dlFileEntryMetadataLocalService;

	@Reference
	private DLFileEntryRawMetadataProcessorConfigurationProvider
		_dlFileEntryRawMetadataProcessorConfigurationProvider;

	@Reference
	private Portal _portal;

}