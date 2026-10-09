/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.model.listener;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.processor.DLProcessor;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFileVersionLocalService;
import com.liferay.document.library.kernel.store.DLStoreUtil;
import com.liferay.document.library.model.DLFileVersionPreview;
import com.liferay.document.library.service.DLFileVersionPreviewLocalService;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.repository.model.FileVersion;

import java.util.Objects;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = ModelListener.class)
public class DLFileVersionModelListener
	extends BaseModelListener<DLFileVersion> {

	@Override
	public void onAfterRemove(DLFileVersion dlFileVersion)
		throws ModelListenerException {

		try {
			DLFileEntry dlFileEntry = _dlFileEntryLocalService.fetchDLFileEntry(
				dlFileVersion.getFileEntryId());

			if (dlFileEntry != null) {
				DLStoreUtil.deleteFile(
					dlFileEntry.getCompanyId(),
					dlFileEntry.getDataRepositoryId(), dlFileEntry.getName(),
					dlFileVersion.getStoreFileName());
			}

			DLFileVersionPreview dlFileVersionPreview =
				_dlFileVersionPreviewLocalService.fetchDLFileVersionPreview(
					dlFileVersion.getFileEntryId(),
					dlFileVersion.getFileVersionId());

			if (dlFileVersionPreview != null) {
				_dlFileVersionPreviewLocalService.deleteDLFileVersionPreview(
					dlFileVersionPreview);
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
	}

	@Override
	public void onAfterUpdate(DLFileVersion originalModel, DLFileVersion model)
		throws ModelListenerException {

		if (Objects.equals(originalModel.getVersion(), model.getVersion()) &&
			!Objects.equals(
				DLFileEntryConstants.PRIVATE_WORKING_COPY_VERSION,
				originalModel.getVersion()) &&
			!Objects.equals(
				originalModel.getStoreFileName(), model.getStoreFileName())) {

			try {
				DLFileEntry fileEntry = originalModel.getFileEntry();

				DLStoreUtil.deleteFile(
					originalModel.getCompanyId(),
					originalModel.getRepositoryId(), fileEntry.getName(),
					originalModel.getStoreFileName());
				DLStoreUtil.deleteFile(
					originalModel.getCompanyId(),
					originalModel.getRepositoryId(), fileEntry.getName(),
					originalModel.getStoreFileName() + ".index");
			}
			catch (PortalException portalException) {
				_log.error(portalException);
			}
		}
	}

	@Override
	public void onBeforeRemove(DLFileVersion dlFileVersion)
		throws ModelListenerException {

		try {
			dlFileVersion = _dlFileVersionLocalService.fetchDLFileVersion(
				dlFileVersion.getFileVersionId());

			if (dlFileVersion == null) {
				return;
			}

			if (Objects.equals(
					DLFileEntryConstants.PRIVATE_WORKING_COPY_VERSION,
					dlFileVersion.getVersion())) {

				DLFileVersion latestFileVersion =
					_dlFileVersionLocalService.fetchLatestFileVersion(
						dlFileVersion.getFileEntryId(), true);

				if (latestFileVersion != null) {
					_cleanUpFileVersion(latestFileVersion.getFileVersionId());
				}
			}

			_cleanUpFileVersion(dlFileVersion.getFileVersionId());
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, DLProcessor.class, "type");
	}

	private void _cleanUpFileVersion(long fileVersionId)
		throws PortalException {

		FileVersion fileVersion = _dlAppLocalService.getFileVersion(
			fileVersionId);

		for (DLProcessor dlProcessor : _serviceTrackerMap.values()) {
			if (!dlProcessor.isSupported(fileVersion)) {
				continue;
			}

			dlProcessor.cleanUp(fileVersion);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLFileVersionModelListener.class);

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private DLFileVersionLocalService _dlFileVersionLocalService;

	@Reference
	private DLFileVersionPreviewLocalService _dlFileVersionPreviewLocalService;

	private ServiceTrackerMap<String, DLProcessor> _serviceTrackerMap;

}