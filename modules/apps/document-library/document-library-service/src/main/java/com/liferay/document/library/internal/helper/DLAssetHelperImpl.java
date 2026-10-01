/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.helper;

import com.liferay.document.library.helper.DLAssetHelper;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.util.Validator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alejandro Tardín
 */
@Component(service = DLAssetHelper.class)
public class DLAssetHelperImpl implements DLAssetHelper {

	@Override
	public long getAssetClassPK(FileEntry fileEntry, FileVersion fileVersion) {
		if (fileEntry == null) {
			return 0;
		}

		if (fileVersion == null) {
			return fileEntry.getFileEntryId();
		}

		String version = fileVersion.getVersion();

		if (fileVersion.isApproved() || fileVersion.isExpired() ||
			Validator.isNull(version) ||
			version.equals(DLFileEntryConstants.VERSION_DEFAULT) ||
			fileEntry.isInTrash()) {

			return fileEntry.getFileEntryId();
		}

		return fileVersion.getFileVersionId();
	}

}