/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.change.tracking.spi.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.change.tracking.spi.display.context.DisplayContext;
import com.liferay.document.library.constants.DLFileVersionPreviewConstants;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLProcessorConstants;
import com.liferay.document.library.kernel.processor.AudioProcessor;
import com.liferay.document.library.kernel.processor.DLProcessor;
import com.liferay.document.library.kernel.processor.DLProcessorHelperUtil;
import com.liferay.document.library.kernel.processor.ImageProcessor;
import com.liferay.document.library.kernel.processor.PDFProcessor;
import com.liferay.document.library.kernel.processor.VideoProcessor;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileVersionLocalService;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.document.library.preview.DLPreviewRendererProvider;
import com.liferay.document.library.service.DLFileVersionPreviewLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.InputStream;

import java.util.Locale;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Samuel Trong Tran
 */
@Component(service = CTDisplayRenderer.class)
public class DLFileVersionCTDisplayRenderer
	extends BaseCTDisplayRenderer<DLFileVersion> {

	@Override
	public DLFileVersion fetchLatestVersionedModel(
		DLFileVersion dlFileVersion) {

		return _dlFileVersionLocalService.fetchLatestFileVersion(
			dlFileVersion.getFileEntryId(), true);
	}

	@Override
	public InputStream getDownloadInputStream(
			DLFileVersion dlFileVersion, String key)
		throws PortalException {

		return getDownloadInputStream(
			_store, (AudioProcessor)_audioDLProcessor, _dlAppLocalService,
			dlFileVersion, (ImageProcessor)_imageDLProcessor, key,
			(PDFProcessor)_pdfDLProcessor, (VideoProcessor)_videoDLProcessor);
	}

	@Override
	public Class<DLFileVersion> getModelClass() {
		return DLFileVersion.class;
	}

	@Override
	public String getTitle(Locale locale, DLFileVersion dlFileVersion) {
		return dlFileVersion.getTitle();
	}

	@Override
	public String getVersionName(DLFileVersion dlFileVersion) {
		return dlFileVersion.getVersion();
	}

	@Override
	public boolean isHideable(DLFileVersion dlFileVersion) {
		return true;
	}

	@Override
	public String renderPreview(DisplayContext<DLFileVersion> displayContext)
		throws Exception {

		DLFileVersion dlFileVersion = displayContext.getModel();

		FileVersion fileVersion = _dlAppLocalService.getFileVersion(
			dlFileVersion.getFileVersionId());

		String fileName = fileVersion.getFileName();
		String mimeType = fileVersion.getMimeType();

		AudioProcessor audioProcessor = (AudioProcessor)_audioDLProcessor;

		if (audioProcessor.isSupported(mimeType)) {
			if (!audioProcessor.hasAudio(fileVersion) ||
				_dlFileVersionPreviewLocalService.hasDLFileVersionPreview(
					fileVersion.getFileEntryId(),
					fileVersion.getFileVersionId(),
					DLFileVersionPreviewConstants.STATUS_FAILURE)) {

				return null;
			}

			return StringBundler.concat(
				"<audio controls controlsList=\"nodownload\" style=\"",
				"max-width: ", PropsValues.DL_FILE_ENTRY_PREVIEW_VIDEO_WIDTH,
				"px;\"><source src=\"",
				displayContext.getDownloadURL(
					_AUDIO_PREVIEW + ",mp3",
					audioProcessor.getPreviewFileSize(fileVersion, "mp3"),
					FileUtil.stripExtension(fileName) + ".mp3"),
				"\" type=\"audio/mp3\"/><source src=\"",
				displayContext.getDownloadURL(
					_AUDIO_PREVIEW + ",ogg",
					audioProcessor.getPreviewFileSize(fileVersion, "ogg"),
					FileUtil.stripExtension(fileName) + ".ogg"),
				"\" type=\"audio/ogg\"/></audio>");
		}

		Set<String> documentMimeTypes =
			_dlPreviewRendererProvider.getMimeTypes();

		if (documentMimeTypes.contains(mimeType)) {
			PDFProcessor pdfProcessor = (PDFProcessor)_pdfDLProcessor;

			if (!pdfProcessor.isDocumentSupported(fileVersion) ||
				_dlFileVersionPreviewLocalService.hasDLFileVersionPreview(
					fileVersion.getFileEntryId(),
					fileVersion.getFileVersionId(),
					DLFileVersionPreviewConstants.STATUS_FAILURE)) {

				return null;
			}
			else if (!pdfProcessor.hasImages(fileVersion)) {
				if (!DLProcessorHelperUtil.isPreviewableSize(fileVersion)) {
					return null;
				}

				return StringBundler.concat(
					"<div class=\"alert alert-primary\" role=\"alert\">",
					_language.get(
						displayContext.getLocale(),
						"generating-preview-will-take-a-few-minutes"),
					"</div>");
			}

			fileName = StringBundler.concat(
				FileUtil.stripExtension(fileName), StringPool.PERIOD,
				PDFProcessor.PREVIEW_TYPE);

			return StringBundler.concat(
				"<img src=\"",
				displayContext.getDownloadURL(
					_PDF_PREVIEW,
					pdfProcessor.getPreviewFileSize(fileVersion, 1), fileName),
				"\" style=\"margin: auto; max-height:624px; max-width:100%;",
				"\">");
		}

		ImageProcessor imageProcessor = (ImageProcessor)_imageDLProcessor;

		if (imageProcessor.isSupported(mimeType)) {
			if (!DLProcessorHelperUtil.isPreviewableSize(fileVersion) ||
				!imageProcessor.hasImages(fileVersion) ||
				_dlFileVersionPreviewLocalService.hasDLFileVersionPreview(
					fileVersion.getFileEntryId(),
					fileVersion.getFileVersionId(),
					DLFileVersionPreviewConstants.STATUS_FAILURE)) {

				return null;
			}

			fileName = StringBundler.concat(
				FileUtil.stripExtension(fileName), StringPool.PERIOD,
				imageProcessor.getPreviewType(fileVersion));

			return StringBundler.concat(
				"<img src=\"",
				displayContext.getDownloadURL(
					_IMAGE_PREVIEW,
					imageProcessor.getPreviewFileSize(fileVersion), fileName),
				"\" style=\"margin: auto; max-height:624px; max-width:100%;",
				"\">");
		}

		VideoProcessor videoProcessor = (VideoProcessor)_videoDLProcessor;

		Set<String> videoMimeTypes = videoProcessor.getVideoMimeTypes();

		if ((!videoMimeTypes.contains(mimeType) &&
			 !mimeType.equals(
				 ContentTypes.
					 APPLICATION_VND_LIFERAY_VIDEO_EXTERNAL_SHORTCUT_HTML)) ||
			!videoProcessor.hasVideo(fileVersion)) {

			return null;
		}

		return StringBundler.concat(
			"<video controls controlsList=\"nodownload\" style=\"",
			"background-color: #000; display: block; margin: auto; ",
			"max-height:624px; max-width:",
			PropsValues.DL_FILE_ENTRY_PREVIEW_VIDEO_WIDTH,
			"px;\"><source src=\"",
			displayContext.getDownloadURL(
				_VIDEO_PREVIEW + ",mp4",
				audioProcessor.getPreviewFileSize(fileVersion, "mp4"),
				FileUtil.stripExtension(fileName) + ".mp4"),
			"\" type=\"video/mp4\"/><source src=\"",
			displayContext.getDownloadURL(
				_VIDEO_PREVIEW + ",ogv",
				audioProcessor.getPreviewFileSize(fileVersion, "ogv"),
				FileUtil.stripExtension(fileName) + ".ogv"),
			"\" type=\"video/ogv\"/></audio>");
	}

	protected static InputStream getDownloadInputStream(
			Store store, AudioProcessor audioProcessor,
			DLAppLocalService dlAppLocalService, DLFileVersion dlFileVersion,
			ImageProcessor imageProcessor, String key,
			PDFProcessor pdfProcessor, VideoProcessor videoProcessor)
		throws PortalException {

		String[] parts = StringUtil.split(key, StringPool.COMMA);

		try {
			FileVersion fileVersion = dlAppLocalService.getFileVersion(
				dlFileVersion.getFileVersionId());

			if (_AUDIO_PREVIEW.equals(parts[0]) ||
				_VIDEO_PREVIEW.equals(parts[0])) {

				if (parts.length < 2) {
					return null;
				}

				String type = parts[1];

				if (_AUDIO_PREVIEW.equals(parts[0])) {
					return audioProcessor.getPreviewAsStream(fileVersion, type);
				}

				return videoProcessor.getPreviewAsStream(fileVersion, type);
			}
			else if (_IMAGE_PREVIEW.equals(parts[0])) {
				return imageProcessor.getPreviewAsStream(fileVersion);
			}
			else if (_PDF_PREVIEW.equals(parts[0])) {
				return pdfProcessor.getPreviewAsStream(fileVersion, 1);
			}
		}
		catch (Exception exception) {
			throw new PortalException(exception);
		}

		DLFileEntry dlFileEntry = dlFileVersion.getFileEntry();

		return store.getFileAsStream(
			dlFileVersion.getCompanyId(), dlFileEntry.getDataRepositoryId(),
			dlFileEntry.getName(), parts[0]);
	}

	@Override
	protected void buildDisplay(DisplayBuilder<DLFileVersion> displayBuilder) {
		DLFileVersion dlFileVersion = displayBuilder.getModel();

		displayBuilder.display(
			"title", dlFileVersion.getTitle()
		).display(
			"description", dlFileVersion.getDescription()
		).display(
			"file-name", dlFileVersion.getFileName()
		).display(
			"extension", dlFileVersion.getExtension()
		).display(
			"mime-type", dlFileVersion.getMimeType()
		).display(
			"version", dlFileVersion.getVersion()
		).display(
			"size", dlFileVersion.getSize()
		).display(
			"download",
			getDownloadLink(
				displayBuilder.getDisplayContext(), dlFileVersion.getVersion(),
				dlFileVersion.getSize(), dlFileVersion.getFileName()),
			false
		);
	}

	private static final String _AUDIO_PREVIEW = "AUDIO_PREVIEW";

	private static final String _IMAGE_PREVIEW = "IMAGE_PREVIEW";

	private static final String _PDF_PREVIEW = "PDF_PREVIEW";

	private static final String _VIDEO_PREVIEW = "VIDEO_PREVIEW";

	@Reference(target = "(type=" + DLProcessorConstants.AUDIO_PROCESSOR + ")")
	private DLProcessor _audioDLProcessor;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLFileVersionLocalService _dlFileVersionLocalService;

	@Reference
	private DLFileVersionPreviewLocalService _dlFileVersionPreviewLocalService;

	@Reference(
		target = "(component.name=com.liferay.document.library.preview.document.internal.DocumentPreviewRendererProvider)"
	)
	private DLPreviewRendererProvider _dlPreviewRendererProvider;

	@Reference(target = "(type=" + DLProcessorConstants.IMAGE_PROCESSOR + ")")
	private DLProcessor _imageDLProcessor;

	@Reference
	private Language _language;

	@Reference(target = "(type=" + DLProcessorConstants.PDF_PROCESSOR + ")")
	private DLProcessor _pdfDLProcessor;

	@Reference(target = "(default=true)")
	private Store _store;

	@Reference(target = "(type=" + DLProcessorConstants.VIDEO_PROCESSOR + ")")
	private DLProcessor _videoDLProcessor;

}