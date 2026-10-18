/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.upload.web.internal;

import com.liferay.adaptive.media.image.html.constants.AMImageHTMLConstants;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.editor.constants.EditorConstants;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * @author Alejandro Tardín
 * @author Jürgen Kappler
 */
public class HTMLImageAttachmentElementReplacer {

	public HTMLImageAttachmentElementReplacer(
		PortletFileRepository portletFileRepository) {

		_portletFileRepository = portletFileRepository;
	}

	public String replace(String originalImgHtmlElement, FileEntry fileEntry) {
		Element element = _toElement(originalImgHtmlElement);

		String fileEntryURL = _portletFileRepository.getPortletFileEntryURL(
			null, fileEntry, StringPool.BLANK);

		element.attr("src", fileEntryURL);

		element.removeAttr(EditorConstants.ATTRIBUTE_DATA_IMAGE_ID);

		element.attr(
			AMImageHTMLConstants.ATTRIBUTE_NAME_FILE_ENTRY_ID,
			String.valueOf(fileEntry.getFileEntryId()));

		return element.toString();
	}

	private Element _toElement(String originalImgTag) {
		Document document = Jsoup.parseBodyFragment(originalImgTag);

		Document.OutputSettings outputSettings = new Document.OutputSettings();

		outputSettings.prettyPrint(false);
		outputSettings.syntax(Document.OutputSettings.Syntax.xml);

		document.outputSettings(outputSettings);

		Element bodyElement = document.body();

		return bodyElement.child(0);
	}

	private final PortletFileRepository _portletFileRepository;

}