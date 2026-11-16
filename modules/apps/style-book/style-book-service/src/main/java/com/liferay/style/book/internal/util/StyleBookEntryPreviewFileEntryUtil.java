/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.internal.util;

import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.style.book.constants.StyleBookPortletKeys;

/**
 * @author Thiago Buarque
 */
public class StyleBookEntryPreviewFileEntryUtil {

	public static FileEntry fetchPreviewFileEntry(
			long groupId, long previewFileEntryId)
		throws PortalException {

		if (previewFileEntryId <= 0) {
			return null;
		}

		FileEntry fileEntry = DLAppLocalServiceUtil.fetchFileEntry(
			previewFileEntryId);

		if (fileEntry == null) {
			return null;
		}

		Repository repository =
			PortletFileRepositoryUtil.fetchPortletRepository(
				groupId, StyleBookPortletKeys.STYLE_BOOK);

		if ((repository == null) ||
			(repository.getRepositoryId() != fileEntry.getRepositoryId())) {

			return null;
		}

		return fileEntry;
	}

}