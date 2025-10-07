/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.change.tracking.conflict;

import com.liferay.change.tracking.conflict.CTEntryConflictHelper;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.store.DLStore;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.change.tracking.CTModel;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(service = CTEntryConflictHelper.class)
public class DLFileEntryCTEntryConflictHelper implements CTEntryConflictHelper {

	@Override
	public String getMissingRequirementTypeName(
		CTEntry ctEntry, long targetCTCollectionId) {

		DLFileEntry dlFileEntry = _dlFileEntryLocalService.fetchDLFileEntry(
			ctEntry.getModelClassPK());

		if ((dlFileEntry == null) || (dlFileEntry.getSize() == 0)) {
			return null;
		}

		try {
			DLFileVersion dlFileVersion = dlFileEntry.getFileVersion();

			boolean hasFile = _dlStore.hasFile(
				dlFileEntry.getCompanyId(), dlFileEntry.getDataRepositoryId(),
				dlFileEntry.getName(), dlFileVersion.getStoreFileName());

			if (!hasFile) {
				return "file";
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return "file";
		}

		return null;
	}

	@Override
	public Class<? extends CTModel<?>> getModelClass() {
		return DLFileEntry.class;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLFileEntryCTEntryConflictHelper.class);

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private DLStore _dlStore;

	@Reference
	private Language _language;

}