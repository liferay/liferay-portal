/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.internal.importer.util;

import com.liferay.document.library.helper.DLURLHelper;
import com.liferay.knowledge.base.exception.KBArticleImportException;
import com.liferay.knowledge.base.markdown.converter.MarkdownConverter;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.zip.ZipReader;

import java.io.IOException;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Sergio González
 */
public class KBArticleMarkdownConverter {

	public KBArticleMarkdownConverter(
			String markdown, String fileEntryName,
			MarkdownConverter markdownConverter, Map<String, String> metadata,
			DLURLHelper dlURLHelper)
		throws KBArticleImportException {

		_dlURLHelper = dlURLHelper;

		String html = null;

		try {
			html = markdownConverter.convert(markdown);
		}
		catch (IOException ioException) {
			throw new KBArticleImportException(
				"Unable to convert Markdown to HTML: " +
					ioException.getLocalizedMessage(),
				ioException);
		}

		String heading = _getHeading(html);

		if (Validator.isNull(heading)) {
			throw new KBArticleImportException(
				"Unable to extract title heading from file: " + fileEntryName);
		}

		_urlTitle = getUrlTitle(html);

		if (Validator.isNull(_urlTitle)) {
			throw new KBArticleImportException(
				"Missing title heading ID in file: " + fileEntryName);
		}

		String title = HtmlUtil.unescape(heading);

		int x = title.indexOf("[](id=");

		if (x != -1) {
			title = title.substring(0, x);
		}

		_title = title;

		html = _stripIds(html);

		_html = _stripHeading(html);

		String baseSourceURL = metadata.get(_METADATA_BASE_SOURCE_URL);

		_sourceURL = _buildSourceURL(baseSourceURL, fileEntryName);
	}

	public String getSourceURL() {
		return _sourceURL;
	}

	public String getTitle() {
		return _title;
	}

	public String getUrlTitle() {
		return _urlTitle;
	}

	public String processAttachmentsReferences(
			long userId, KBArticle kbArticle, ZipReader zipReader,
			Map<String, FileEntry> fileEntriesMap)
		throws PortalException {

		Set<Integer> indexes = new TreeSet<>();

		int index = 0;

		while ((index = _html.indexOf("<img", index)) > -1) {
			indexes.add(index);

			index += 4;
		}

		if (indexes.isEmpty()) {
			return _html;
		}

		StringBundler sb = new StringBundler();

		int previousIndex = 0;

		for (int curIndex : indexes) {
			if (curIndex < 0) {
				break;
			}

			if (curIndex > previousIndex) {

				// Append text from previous position up to image tag

				String text = _html.substring(previousIndex, curIndex);

				sb.append(text);
			}

			int pos = _html.indexOf(">", curIndex);

			if (pos < 0) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Expected close tag for image " +
							_html.substring(curIndex));
				}

				sb.append(_html.substring(curIndex));

				previousIndex = curIndex;

				break;
			}

			String text = _html.substring(curIndex, pos);

			String imageFileName = KBArticleImporterUtil.extractImageFileName(
				text);

			FileEntry imageFileEntry = KBArticleImporterUtil.addImageFileEntry(
				imageFileName, userId, kbArticle, zipReader, fileEntriesMap);

			if (imageFileEntry == null) {
				if (_log.isWarnEnabled()) {
					_log.warn("Unable to find image source " + text);
				}

				sb.append("<img alt=\"missing image\" src=\"\" ");
			}
			else {
				String imageSrc = StringPool.BLANK;

				try {
					imageSrc = _dlURLHelper.getPreviewURL(
						imageFileEntry, imageFileEntry.getFileVersion(), null,
						StringPool.BLANK);
				}
				catch (PortalException portalException) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to obtain image URL from file entry " +
								imageFileEntry.getFileEntryId(),
							portalException);
					}
				}

				sb.append("<img alt=\"");
				sb.append(HtmlUtil.escapeAttribute(imageFileEntry.getTitle()));
				sb.append("\" src=\"");
				sb.append(imageSrc);
				sb.append("\" ");
			}

			previousIndex = pos;
		}

		if (previousIndex < _html.length()) {
			sb.append(_html.substring(previousIndex));
		}

		return sb.toString();
	}

	protected String getUrlTitle(String heading) {
		int x = heading.indexOf("<h1 id=");

		if (x == -1) {
			return null;
		}

		x += 7;

		char quote = heading.charAt(x);

		String urlTitle = null;

		int y = heading.indexOf(quote, x + 1);

		if (y > (x + 1)) {
			urlTitle = heading.substring(x + 1, y);

			urlTitle = StringUtil.replace(
				urlTitle, CharPool.SPACE, CharPool.DASH);

			urlTitle = StringUtil.toLowerCase(urlTitle);
		}

		if (urlTitle == null) {
			return null;
		}

		if (!urlTitle.startsWith(StringPool.SLASH)) {
			urlTitle = StringPool.SLASH + urlTitle;
		}

		int urlTitleMaxLength = ModelHintsUtil.getMaxLength(
			KBArticle.class.getName(), "urlTitle");

		while (urlTitle.length() > urlTitleMaxLength) {
			int pos = urlTitle.lastIndexOf(StringPool.DASH);

			if (pos == -1) {
				urlTitle = urlTitle.substring(0, urlTitleMaxLength);
			}
			else {
				urlTitle = urlTitle.substring(0, pos);
			}
		}

		return urlTitle;
	}

	private String _buildSourceURL(String baseSourceURL, String fileEntryName) {
		if (!Validator.isUrl(baseSourceURL)) {
			return null;
		}

		int pos = baseSourceURL.length() - 1;

		while (pos >= 0) {
			char c = baseSourceURL.charAt(pos);

			if (c != CharPool.SLASH) {
				break;
			}

			pos--;
		}

		StringBundler sb = new StringBundler(3);

		sb.append(baseSourceURL.substring(0, pos + 1));

		if (!fileEntryName.startsWith(StringPool.SLASH)) {
			sb.append(StringPool.SLASH);
		}

		sb.append(FileUtil.replaceSeparator(fileEntryName));

		return sb.toString();
	}

	private String _getHeading(String html) {
		int x = html.indexOf("<h1");

		int y = html.indexOf("</h1>", x);

		x = html.indexOf(CharPool.GREATER_THAN, x + 3);

		if ((x == -1) || (y == -1) || (x > y)) {
			return null;
		}

		return html.substring(x + 1, y);
	}

	private String _stripHeading(String html) {
		int index = html.indexOf("</h1>");

		if (index == -1) {
			return html;
		}

		return html.substring(index + 5);
	}

	private String _stripIds(String content) {
		int index = content.indexOf("[](id=");

		if (index == -1) {
			return content;
		}

		StringBundler sb = new StringBundler();

		do {
			int x = content.indexOf(StringPool.EQUAL, index);

			int y = content.indexOf(StringPool.CLOSE_PARENTHESIS, x);

			if (y != -1) {
				int z = content.indexOf("</h", y);

				if (z != (y + 1)) {
					sb.append(content.substring(0, y + 1));
				}
				else {
					sb.append(
						StringUtil.trimTrailing(content.substring(0, index)));
				}

				content = content.substring(y + 1);
			}
			else {
				if (_log.isWarnEnabled()) {
					String msg = content.substring(index);

					// Get the invalid id text from the content

					int spaceIndex = content.indexOf(StringPool.SPACE);

					if (spaceIndex != -1) {
						msg = content.substring(index, spaceIndex);
					}

					_log.warn(
						"Missing ')' for web content containing header id " +
							msg);
				}

				// Since no close parenthesis remains in the content, stop
				// stripping out IDs and simply include all of the remaining
				// content

				break;
			}
		}
		while ((index = content.indexOf("[](id=")) != -1);

		sb.append(content);

		return sb.toString();
	}

	private static final String _METADATA_BASE_SOURCE_URL = "base.source.url";

	private static final Log _log = LogFactoryUtil.getLog(
		KBArticleMarkdownConverter.class);

	private final DLURLHelper _dlURLHelper;
	private final String _html;
	private final String _sourceURL;
	private final String _title;
	private final String _urlTitle;

}