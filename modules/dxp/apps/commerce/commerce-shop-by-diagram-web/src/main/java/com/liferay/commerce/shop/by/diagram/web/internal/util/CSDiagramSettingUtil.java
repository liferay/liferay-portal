/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.web.internal.util;

import com.liferay.commerce.product.exception.NoSuchCPAttachmentFileEntryException;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramSetting;
import com.liferay.document.library.helper.DLURLHelper;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;

/**
 * @author Andrea Sbarra
 * @author Crescenzo Rega
 */
public class CSDiagramSettingUtil {

	public static FileVersion getFileVersion(CSDiagramSetting csDiagramSetting)
		throws Exception {

		FileEntry fileEntry = _fetchFileEntry(csDiagramSetting);

		if (fileEntry != null) {
			return fileEntry.getFileVersion();
		}

		return null;
	}

	public static String getImageURL(
			CSDiagramSetting csDiagramSetting, DLURLHelper dlURLHelper)
		throws Exception {

		FileEntry fileEntry = _fetchFileEntry(csDiagramSetting);

		if (fileEntry != null) {
			return dlURLHelper.getDownloadURL(
				fileEntry, fileEntry.getFileVersion(), null, StringPool.BLANK);
		}

		return StringPool.BLANK;
	}

	private static FileEntry _fetchFileEntry(CSDiagramSetting csDiagramSetting)
		throws Exception {

		if (csDiagramSetting == null) {
			return null;
		}

		try {
			CPAttachmentFileEntry cpAttachmentFileEntry =
				csDiagramSetting.getCPAttachmentFileEntry();

			return cpAttachmentFileEntry.fetchFileEntry();
		}
		catch (NoSuchCPAttachmentFileEntryException
					noSuchCPAttachmentFileEntryException) {

			if (_log.isInfoEnabled()) {
				_log.info(noSuchCPAttachmentFileEntryException);
			}

			return null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CSDiagramSettingUtil.class);

}