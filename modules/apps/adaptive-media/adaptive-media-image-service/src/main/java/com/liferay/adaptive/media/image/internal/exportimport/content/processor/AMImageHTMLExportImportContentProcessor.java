/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.internal.exportimport.content.processor;

import com.liferay.adaptive.media.image.html.AMImageHTMLTagFactory;
import com.liferay.adaptive.media.image.html.constants.AMImageHTMLConstants;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.exportimport.content.processor.ExportImportContentProcessor;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.Objects;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 * @author Alejandro Tardín
 */
@Component(
	property = "adaptive.media.format=html",
	service = ExportImportContentProcessor.class
)
public class AMImageHTMLExportImportContentProcessor
	implements ExportImportContentProcessor<String> {

	@Override
	public String replaceExportContentReferences(
		PortletDataContext portletDataContext, StagedModel stagedModel,
		String content, boolean exportReferencedContent,
		boolean escapeContent) {

		AMReferenceExporter amReferenceExporter = new AMReferenceExporter(
			portletDataContext, stagedModel, exportReferencedContent);

		return _replace(content, amReferenceExporter);
	}

	@Override
	public String replaceImportContentReferences(
			PortletDataContext portletDataContext, StagedModel stagedModel,
			String content)
		throws Exception {

		AMEmbeddedReferenceSet amEmbeddedReferenceSet =
			_amEmbeddedReferenceSetFactory.create(
				portletDataContext, stagedModel);

		return _replace(content, amEmbeddedReferenceSet);
	}

	@Override
	public void validateContentReferences(long groupId, String content)
		throws PortalException {

		int index = _getFileEntryIdAttributeIndex(content, 0);

		while (index != -1) {
			int fileEntryIdIndex =
				index + _FILE_ENTRY_ID_ATTRIBUTE_PREFIX.length();

			int fileEntryIdEndIndex = content.indexOf(
				CharPool.QUOTE, fileEntryIdIndex);

			if (fileEntryIdEndIndex == -1) {
				break;
			}

			try {
				long fileEntryId = GetterUtil.getLong(
					content.substring(fileEntryIdIndex, fileEntryIdEndIndex));

				_dlAppLocalService.getFileEntry(fileEntryId);
			}
			catch (NoSuchFileEntryException noSuchFileEntryException) {
				if (!ExportImportThreadLocal.isImportInProcess()) {
					throw noSuchFileEntryException;
				}

				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"An invalid file entry was detected during import ",
							"when validating the content below. This is not ",
							"an error. It typically means the file entry was ",
							"deleted.\n", content));
				}
			}

			index = _getFileEntryIdAttributeIndex(content, fileEntryIdEndIndex);
		}
	}

	private int _getFileEntryIdAttributeIndex(String content, int fromIndex) {
		int index = content.indexOf(_FILE_ENTRY_ID_ATTRIBUTE_PREFIX, fromIndex);

		while (index != -1) {
			if (content.lastIndexOf(CharPool.LESS_THAN, index) >
					content.lastIndexOf(CharPool.GREATER_THAN, index)) {

				return index;
			}

			index = content.indexOf(
				_FILE_ENTRY_ID_ATTRIBUTE_PREFIX,
				index + _FILE_ENTRY_ID_ATTRIBUTE_PREFIX.length());
		}

		return -1;
	}

	private Document _parseDocument(String html) {
		Document document = Jsoup.parseBodyFragment(html);

		Document.OutputSettings outputSettings = new Document.OutputSettings();

		outputSettings.prettyPrint(false);
		outputSettings.syntax(Document.OutputSettings.Syntax.xml);

		document.outputSettings(outputSettings);

		return document;
	}

	private Element _parseNode(String tag) {
		Document document = _parseDocument(tag);

		Element bodyElement = document.body();

		return bodyElement.child(0);
	}

	private String _replace(
			String content, AMEmbeddedReferenceSet amEmbeddedReferenceSet)
		throws Exception {

		Document document = _parseDocument(content);

		Elements elements = document.getElementsByAttribute(
			_ATTRIBUTE_NAME_EXPORT_IMPORT_PATH);

		for (Element element : elements) {
			String path = element.attr(_ATTRIBUTE_NAME_EXPORT_IMPORT_PATH);

			if (!amEmbeddedReferenceSet.containsReference(path)) {
				continue;
			}

			long fileEntryId = amEmbeddedReferenceSet.importReference(path);

			FileEntry fileEntry = _dlAppLocalService.fetchFileEntry(
				fileEntryId);

			if (fileEntry == null) {
				continue;
			}

			element.attr(
				AMImageHTMLConstants.ATTRIBUTE_NAME_FILE_ENTRY_ID,
				String.valueOf(fileEntryId));
			element.removeAttr(_ATTRIBUTE_NAME_EXPORT_IMPORT_PATH);

			if (!Objects.equals(element.tagName(), "picture")) {
				continue;
			}

			Elements imgElements = element.getElementsByTag("img");

			Element imgElement = imgElements.first();

			imgElement.attr(
				AMImageHTMLConstants.ATTRIBUTE_NAME_FILE_ENTRY_ID,
				String.valueOf(fileEntryId));
			imgElement.removeAttr(_ATTRIBUTE_NAME_EXPORT_IMPORT_PATH);

			Element picture = _parseNode(
				_amImageHTMLTagFactory.create(
					imgElement.toString(), fileEntry));

			element.html(picture.html());
		}

		Element bodyElement = document.body();

		return bodyElement.html();
	}

	private String _replace(
		String content, AMReferenceExporter amReferenceExporter) {

		StringBundler sb = new StringBundler();

		int beginIndex = 0;

		int index = _getFileEntryIdAttributeIndex(content, 0);

		while (index != -1) {
			int fileEntryIdIndex =
				index + _FILE_ENTRY_ID_ATTRIBUTE_PREFIX.length();

			int fileEntryIdEndIndex = content.indexOf(
				CharPool.QUOTE, fileEntryIdIndex);

			if (fileEntryIdEndIndex == -1) {
				break;
			}

			long fileEntryId = GetterUtil.getLong(
				content.substring(fileEntryIdIndex, fileEntryIdEndIndex));

			if (fileEntryId != 0) {
				try {
					FileEntry fileEntry = _dlAppLocalService.getFileEntry(
						fileEntryId);

					amReferenceExporter.exportReference(fileEntry);

					sb.append(content.substring(beginIndex, index));
					sb.append(_EXPORT_IMPORT_PATH_ATTRIBUTE_PREFIX);
					sb.append(ExportImportPathUtil.getModelPath(fileEntry));

					beginIndex = fileEntryIdEndIndex;
				}
				catch (PortalException portalException) {
					if (_log.isWarnEnabled()) {
						_log.warn(portalException);
					}
				}
			}

			index = _getFileEntryIdAttributeIndex(content, fileEntryIdEndIndex);
		}

		if (sb.index() == 0) {
			return content;
		}

		sb.append(content.substring(beginIndex));

		return sb.toString();
	}

	private static final String _ATTRIBUTE_NAME_EXPORT_IMPORT_PATH =
		"export-import-path";

	private static final String _EXPORT_IMPORT_PATH_ATTRIBUTE_PREFIX =
		_ATTRIBUTE_NAME_EXPORT_IMPORT_PATH + "=\"";

	private static final String _FILE_ENTRY_ID_ATTRIBUTE_PREFIX =
		AMImageHTMLConstants.ATTRIBUTE_NAME_FILE_ENTRY_ID + "=\"";

	private static final Log _log = LogFactoryUtil.getLog(
		AMImageHTMLExportImportContentProcessor.class);

	@Reference
	private AMEmbeddedReferenceSetFactory _amEmbeddedReferenceSetFactory;

	@Reference
	private AMImageHTMLTagFactory _amImageHTMLTagFactory;

	@Reference
	private DLAppLocalService _dlAppLocalService;

}