/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.form.dto.v1_0.util;

import com.liferay.document.library.helper.DLURLHelper;
import com.liferay.headless.form.dto.v1_0.FormDocument;
import com.liferay.portal.kernel.repository.model.FileEntry;

/**
 * @author Victor Oliveira
 */
public class FormDocumentUtil {

	public static FormDocument toFormDocument(
			DLURLHelper dlurlHelper, FileEntry fileEntry)
		throws Exception {

		return new FormDocument() {
			{
				setContentUrl(
					() -> dlurlHelper.getPreviewURL(
						fileEntry, fileEntry.getFileVersion(), null, ""));
				setEncodingFormat(fileEntry::getMimeType);
				setFileExtension(fileEntry::getExtension);
				setId(fileEntry::getFileEntryId);
				setSiteId(fileEntry::getGroupId);
				setSizeInBytes(fileEntry::getSize);
				setTitle(fileEntry::getTitle);
			}
		};
	}

}