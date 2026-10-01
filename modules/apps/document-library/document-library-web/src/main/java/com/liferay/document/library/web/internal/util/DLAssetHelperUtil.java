/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.util;

import com.liferay.document.library.helper.DLAssetHelper;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;

/**
 * @author Alejandro Tardín
 */
public class DLAssetHelperUtil {

	public static long getAssetClassPK(
		FileEntry fileEntry, FileVersion fileVersion) {

		DLAssetHelper dlAssetHelper = _dlAssetHelperSnapshot.get();

		return dlAssetHelper.getAssetClassPK(fileEntry, fileVersion);
	}

	public static DLAssetHelper getDLAssetHelper() {
		return _dlAssetHelperSnapshot.get();
	}

	private static final Snapshot<DLAssetHelper> _dlAssetHelperSnapshot =
		new Snapshot<>(DLAssetHelperUtil.class, DLAssetHelper.class);

}