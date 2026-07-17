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

		Document document = _parseDocument(content);
		String elementSelector =
			"[" + AMImageHTMLConstants.ATTRIBUTE_NAME_FILE_ENTRY_ID + "]";

		for (Element element : document.select(elementSelector)) {
			try {
				long fileEntryId = GetterUtil.getLong(
					element.attr(
						AMImageHTMLConstants.ATTRIBUTE_NAME_FILE_ENTRY_ID));

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
		}
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

			if (Objects.equals(element.tagName(), "picture")) {
				Elements imgElements = element.getElementsByTag("img");

				Element imgElement = imgElements.first();

				imgElement.removeAttr(_ATTRIBUTE_NAME_EXPORT_IMPORT_PATH);
				imgElement.attr(
					AMImageHTMLConstants.ATTRIBUTE_NAME_FILE_ENTRY_ID,
					String.valueOf(fileEntryId));

				Element picture = _parseNode(
					_amImageHTMLTagFactory.create(
						imgElement.toString(), fileEntry));

				element.html(picture.html());
			}
		}

		Element bodyElement = document.body();

		return bodyElement.html();
	}

	private String _replace(
		String content, AMReferenceExporter amReferenceExporter) {

		Document document = _parseDocument(content);
		String elementSelector =
			"[" + AMImageHTMLConstants.ATTRIBUTE_NAME_FILE_ENTRY_ID + "]";

		for (Element element : document.select(elementSelector)) {
			try {
				long fileEntryId = GetterUtil.getLong(
					element.attr(
						AMImageHTMLConstants.ATTRIBUTE_NAME_FILE_ENTRY_ID));

				if (fileEntryId == 0) {
					continue;
				}

				FileEntry fileEntry = _dlAppLocalService.getFileEntry(
					fileEntryId);

				amReferenceExporter.exportReference(fileEntry);

				element.removeAttr(
					AMImageHTMLConstants.ATTRIBUTE_NAME_FILE_ENTRY_ID);
				element.attr(
					_ATTRIBUTE_NAME_EXPORT_IMPORT_PATH,
					ExportImportPathUtil.getModelPath(fileEntry));
			}
			catch (PortalException portalException) {
				if (_log.isWarnEnabled()) {
					_log.warn(portalException);
				}
			}
		}

		Element bodyElement = document.body();

		return bodyElement.html();
	}

	private static final String _ATTRIBUTE_NAME_EXPORT_IMPORT_PATH =
		"export-import-path";

	private static final Log _log = LogFactoryUtil.getLog(
		AMImageHTMLExportImportContentProcessor.class);

	@Reference
	private AMEmbeddedReferenceSetFactory _amEmbeddedReferenceSetFactory;

	@Reference
	private AMImageHTMLTagFactory _amImageHTMLTagFactory;

	@Reference
	private DLAppLocalService _dlAppLocalService;

}