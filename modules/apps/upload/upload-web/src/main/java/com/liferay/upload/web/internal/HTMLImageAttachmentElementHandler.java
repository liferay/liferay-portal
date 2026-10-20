/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.upload.web.internal;

import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.editor.constants.EditorConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.upload.AttachmentElementHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
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

		StringBuffer sb = new StringBuffer(content.length());

		Map<Long, FileEntry> fileEntries = new HashMap<>();

		Matcher matcher = _tempAttachmentPattern.matcher(content);

		while (matcher.find()) {
			long fileEntryId = GetterUtil.getLong(matcher.group(1));

			FileEntry fileEntry = fileEntries.get(fileEntryId);

			if (fileEntry == null) {
				fileEntry = saveTempFileUnsafeFunction.apply(
					_dlAppService.getFileEntry(fileEntryId));

				fileEntries.put(fileEntryId, fileEntry);
			}

			String imgTag = matcher.group(0);

			matcher.appendReplacement(
				sb,
				Matcher.quoteReplacement(
					_htmlImageAttachmentElementReplacer.replace(
						imgTag, fileEntry)));
		}

		matcher.appendTail(sb);

		return sb.toString();
	}

	@Activate
	protected void activate() {
		_htmlImageAttachmentElementReplacer =
			new HTMLImageAttachmentElementReplacer(_portletFileRepository);
	}

	private static final String _ATTRIBUTE_LIST_REGEXP =
		"(?:\\s*?\\w+\\s*?=\\s*?\"[^\"]*\")*?\\s*?";

	private static final Pattern _tempAttachmentPattern;

	static {
		_tempAttachmentPattern = Pattern.compile(
			StringBundler.concat(
				"<\\s*?img", _ATTRIBUTE_LIST_REGEXP,
				EditorConstants.ATTRIBUTE_DATA_IMAGE_ID, "\\s*?=\\s*?\"",
				"([^\"]*)\"", _ATTRIBUTE_LIST_REGEXP, "/>"));
	}

	@Reference
	private DLAppService _dlAppService;

	private HTMLImageAttachmentElementReplacer
		_htmlImageAttachmentElementReplacer;

	@Reference
	private PortletFileRepository _portletFileRepository;

}