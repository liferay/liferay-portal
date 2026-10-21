/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.upload.web.internal;

import com.liferay.adaptive.media.image.html.constants.AMImageHTMLConstants;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.editor.constants.EditorConstants;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;

/**
 * @author Alejandro Tardín
 * @author Jürgen Kappler
 */
public class HTMLImageAttachmentElementReplacerUtil {

	public static String replace(
		String imgTag, FileEntry fileEntry,
		PortletFileRepository portletFileRepository) {

		int dataImageIdIndex = imgTag.indexOf(_DATA_IMAGE_ID_PREFIX);

		int dataImageIdEndIndex = imgTag.indexOf(
			CharPool.QUOTE, dataImageIdIndex + _DATA_IMAGE_ID_PREFIX.length());

		int endIndex = imgTag.length() - 2;

		if (imgTag.charAt(endIndex - 1) == CharPool.SPACE) {
			endIndex--;
		}

		String fileEntryURL = portletFileRepository.getPortletFileEntryURL(
			null, fileEntry, StringPool.BLANK);

		StringBundler sb = new StringBundler(8);

		sb.append(imgTag.substring(0, dataImageIdIndex - 1));

		int srcIndex = imgTag.indexOf(_SRC_PREFIX, dataImageIdEndIndex);

		if (srcIndex == -1) {
			sb.append(imgTag.substring(dataImageIdEndIndex + 1, endIndex));
			sb.append(_SRC_PREFIX);
			sb.append(fileEntryURL);
			sb.append(CharPool.QUOTE);
		}
		else {
			srcIndex += _SRC_PREFIX.length();

			sb.append(imgTag.substring(dataImageIdEndIndex + 1, srcIndex));
			sb.append(fileEntryURL);
			sb.append(
				imgTag.substring(
					imgTag.indexOf(CharPool.QUOTE, srcIndex), endIndex));
		}

		sb.append(_DATA_FILE_ENTRY_ID_PREFIX);
		sb.append(fileEntry.getFileEntryId());
		sb.append("\" />");

		return sb.toString();
	}

	private static final String _DATA_FILE_ENTRY_ID_PREFIX =
		StringBundler.concat(
			StringPool.SPACE, AMImageHTMLConstants.ATTRIBUTE_NAME_FILE_ENTRY_ID,
			"=\"");

	private static final String _DATA_IMAGE_ID_PREFIX =
		EditorConstants.ATTRIBUTE_DATA_IMAGE_ID + "=\"";

	private static final String _SRC_PREFIX = " src=\"";

}