/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.lock;

import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppHelperLocalService;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.lock.BaseLockListener;
import com.liferay.portal.kernel.lock.LockListener;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 */
@Component(service = LockListener.class)
public class DLFileEntryLockListener extends BaseLockListener {

	@Override
	public String getClassName() {
		return DLFileEntryConstants.getClassName();
	}

	@Override
	public void onAfterExpire(String key) {
		long fileEntryId = GetterUtil.getLong(key);

		try {
			if (!_dlFileEntryLocalService.isFileEntryCheckedOut(fileEntryId)) {
				return;
			}

			FileEntry fileEntry = _dlAppLocalService.getFileEntry(fileEntryId);

			FileVersion draftFileVersion = fileEntry.getLatestFileVersion(true);

			if (PropsValues.DL_FILE_ENTRY_LOCK_POLICY == 1) {
				_dlFileEntryLocalService.checkInFileEntry(
					draftFileVersion.getUserId(), fileEntryId,
					DLVersionNumberIncrease.fromMajorVersion(true),
					"Automatic timeout checkin", new ServiceContext());

				fileEntry = _dlAppLocalService.getFileEntry(fileEntryId);

				FileVersion fileVersion = fileEntry.getLatestFileVersion();

				_dlAppHelperLocalService.updateFileEntry(
					draftFileVersion.getUserId(), fileEntry, null, fileVersion,
					fileVersion.getFileVersionId());

				if (_log.isDebugEnabled()) {
					_log.debug("Lock expired and checked in " + fileEntryId);
				}
			}
			else {
				try (SafeCloseable safeCloseable =
						CTCollectionThreadLocal.
							setCTCollectionIdWithSafeCloseable(
								fileEntry.getCtCollectionId())) {

					_dlFileEntryLocalService.cancelCheckOut(
						draftFileVersion.getUserId(), fileEntryId);

					ServiceContext serviceContext = new ServiceContext();

					serviceContext.setWorkflowAction(
						WorkflowConstants.ACTION_PUBLISH);

					_dlAppHelperLocalService.cancelCheckOut(
						draftFileVersion.getUserId(), fileEntry, null,
						fileEntry.getFileVersion(), draftFileVersion,
						serviceContext);
				}

				if (_log.isDebugEnabled()) {
					_log.debug(
						"Lock expired and canceled check out of " +
							fileEntryId);
				}
			}
		}
		catch (Exception exception) {
			_log.error("Unable to execute onAfterExpire for " + key, exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLFileEntryLockListener.class);

	@Reference
	private DLAppHelperLocalService _dlAppHelperLocalService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

}