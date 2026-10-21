/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.upload.web.internal;

import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.editor.constants.EditorConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.upload.AttachmentElementHandler;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 * @author Jürgen Kappler
 */
@Component(
	property = {"format=html", "html.tag.name=img"},
	service = AttachmentElementHandler.class
)
public class HTMLImageAttachmentElementHandler
	implements AttachmentElementHandler {

	@Override
	public String replaceAttachmentElements(
			String content,
			UnsafeFunction<FileEntry, FileEntry, PortalException>
				saveTempFileUnsafeFunction)
		throws PortalException {

		int index = content.indexOf(_ATTRIBUTE_DATA_IMAGE_ID_PREFIX);

		if (index == -1) {
			return content;
		}

		StringBundler sb = new StringBundler();

		Map<Long, FileEntry> fileEntries = new HashMap<>();
		int lastIndex = 0;

		while (index != -1) {
			int startIndex = content.lastIndexOf(CharPool.LESS_THAN, index);
			int endIndex = content.indexOf(CharPool.GREATER_THAN, index) + 1;

			if (!_isImgTag(content, startIndex, index, endIndex)) {
				index = content.indexOf(
					_ATTRIBUTE_DATA_IMAGE_ID_PREFIX, index + 1);

				continue;
			}

			String imgTag = content.substring(startIndex, endIndex);

			long fileEntryId = _getFileEntryId(content, index);

			FileEntry fileEntry = fileEntries.get(fileEntryId);

			if (fileEntry == null) {
				fileEntry = saveTempFileUnsafeFunction.apply(
					_dlAppService.getFileEntry(fileEntryId));

				fileEntries.put(fileEntryId, fileEntry);
			}

			sb.append(content.substring(lastIndex, startIndex));
			sb.append(
				HTMLImageAttachmentElementReplacerUtil.replace(
					imgTag, fileEntry, _portletFileRepository));

			lastIndex = endIndex;

			index = content.indexOf(_ATTRIBUTE_DATA_IMAGE_ID_PREFIX, endIndex);
		}

		sb.append(content.substring(lastIndex));

		return sb.toString();
	}

	private long _getFileEntryId(String content, int index) {
		int beginIndex = index + _ATTRIBUTE_DATA_IMAGE_ID_PREFIX.length();

		return GetterUtil.getLong(
			content.substring(
				beginIndex, content.indexOf(CharPool.QUOTE, beginIndex)));
	}

	private boolean _isImgTag(
		String content, int startIndex, int index, int endIndex) {

		if ((endIndex == 0) ||
			(content.lastIndexOf(CharPool.GREATER_THAN, index) > startIndex) ||
			!content.startsWith("<img", startIndex) ||
			(content.charAt(startIndex + 4) > CharPool.SPACE) ||
			(content.charAt(index - 1) > CharPool.SPACE) ||
			(content.charAt(endIndex - 2) != CharPool.FORWARD_SLASH)) {

			return false;
		}

		return true;
	}

	private static final String _ATTRIBUTE_DATA_IMAGE_ID_PREFIX =
		EditorConstants.ATTRIBUTE_DATA_IMAGE_ID + "=\"";

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private PortletFileRepository _portletFileRepository;

}