/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.search.spi.model.index.contributor;

import com.liferay.document.library.kernel.exception.NoSuchFileVersionException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.search.batch.BatchIndexingActionable;
import com.liferay.portal.search.batch.DynamicQueryBatchIndexingActionableFactory;
import com.liferay.portal.search.spi.model.index.contributor.ModelIndexerWriterContributor;
import com.liferay.portal.search.spi.model.index.contributor.helper.IndexerWriterMode;
import com.liferay.portal.search.spi.model.index.contributor.helper.ModelIndexerWriterDocumentHelper;

/**
 * @author Michael C. Han
 */
public class DLFileEntryModelIndexerWriterContributor
	implements ModelIndexerWriterContributor<DLFileEntry> {

	public DLFileEntryModelIndexerWriterContributor(
		DLFileEntryLocalService dlFileEntryLocalService,
		DynamicQueryBatchIndexingActionableFactory
			dynamicQueryBatchIndexingActionableFactory) {

		_dlFileEntryLocalService = dlFileEntryLocalService;
		_dynamicQueryBatchIndexingActionableFactory =
			dynamicQueryBatchIndexingActionableFactory;
	}

	@Override
	public void customize(
		BatchIndexingActionable batchIndexingActionable,
		ModelIndexerWriterDocumentHelper modelIndexerWriterDocumentHelper) {

		batchIndexingActionable.setPerformActionMethod(
			(DLFileEntry dlFileEntry) -> batchIndexingActionable.addDocuments(
				modelIndexerWriterDocumentHelper.getDocument(dlFileEntry)));
	}

	@Override
	public BatchIndexingActionable getBatchIndexingActionable() {
		return _dynamicQueryBatchIndexingActionableFactory.
			getBatchIndexingActionable(
				_dlFileEntryLocalService.getIndexableActionableDynamicQuery());
	}

	@Override
	public long getCompanyId(DLFileEntry dlFileEntry) {
		return dlFileEntry.getCompanyId();
	}

	@Override
	public IndexerWriterMode getIndexerWriterMode(DLFileEntry dlFileEntry) {
		DLFileVersion dlFileVersion = null;

		try {
			dlFileVersion = dlFileEntry.getFileVersion();
		}
		catch (NoSuchFileVersionException noSuchFileVersionException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to get file version for file entry " +
						dlFileEntry.getFileEntryId(),
					noSuchFileVersionException);
			}

			return IndexerWriterMode.SKIP;
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}

		if (!dlFileEntry.isInTrash() && !dlFileVersion.isApproved() &&
			!dlFileVersion.isExpired()) {

			return IndexerWriterMode.SKIP;
		}

		return IndexerWriterMode.UPDATE;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLFileEntryModelIndexerWriterContributor.class);

	private final DLFileEntryLocalService _dlFileEntryLocalService;
	private final DynamicQueryBatchIndexingActionableFactory
		_dynamicQueryBatchIndexingActionableFactory;

}