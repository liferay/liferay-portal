/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.preview.document.internal;

import com.liferay.document.library.constants.DLFileVersionPreviewConstants;
import com.liferay.document.library.kernel.processor.DLProcessorHelperUtil;
import com.liferay.document.library.kernel.processor.PDFProcessorUtil;
import com.liferay.document.library.preview.DLPreviewRenderer;
import com.liferay.document.library.preview.DLPreviewRendererProvider;
import com.liferay.document.library.preview.exception.DLFileEntryPreviewGenerationException;
import com.liferay.document.library.preview.exception.DLPreviewGenerationInProcessException;
import com.liferay.document.library.preview.exception.DLPreviewSizeException;
import com.liferay.document.library.service.DLFileVersionPreviewLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = DLPreviewRendererProvider.class)
public class DocumentPreviewRendererProvider
	implements DLPreviewRendererProvider {

	@Override
	public Set<String> getMimeTypes() {
		return _mimeTypes;
	}

	@Override
	public DLPreviewRenderer getPreviewDLPreviewRenderer(
		FileVersion fileVersion) {

		if ((fileVersion == null) || (fileVersion.getSize() == 0) ||
			(!PDFProcessorUtil.hasImages(fileVersion) &&
			 !PDFProcessorUtil.isDocumentSupported(
				 fileVersion.getMimeType()))) {

			return null;
		}

		return (request, response) -> {
			_checkForPreviewGenerationExceptions(fileVersion);

			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher("/preview/view.jsp");

			request.setAttribute(
				WebKeys.DOCUMENT_LIBRARY_FILE_VERSION, fileVersion);

			requestDispatcher.include(request, response);
		};
	}

	@Override
	public DLPreviewRenderer getThumbnailDLPreviewRenderer(
		FileVersion fileVersion) {

		return null;
	}

	private void _checkForPreviewGenerationExceptions(FileVersion fileVersion)
		throws PortalException {

		if (_dlFileVersionPreviewLocalService.hasDLFileVersionPreview(
				fileVersion.getFileEntryId(), fileVersion.getFileVersionId(),
				DLFileVersionPreviewConstants.STATUS_FAILURE)) {

			throw new DLFileEntryPreviewGenerationException();
		}

		if (!PDFProcessorUtil.hasImages(fileVersion)) {
			if (!DLProcessorHelperUtil.isPreviewableSize(fileVersion)) {
				throw new DLPreviewSizeException(
					DLProcessorHelperUtil.getPreviewableProcessorMaxSize(
						fileVersion.getGroupId()));
			}

			throw new DLPreviewGenerationInProcessException();
		}
	}

	private static final Set<String> _mimeTypes = new HashSet<>(
		Arrays.asList(
			ContentTypes.APPLICATION_MSWORD, ContentTypes.APPLICATION_PDF,
			ContentTypes.APPLICATION_TEXT,
			ContentTypes.APPLICATION_VND_MS_EXCEL,
			ContentTypes.APPLICATION_VND_MS_POWERPOINT,
			ContentTypes.APPLICATION_X_PDF, ContentTypes.TEXT_CSS,
			ContentTypes.TEXT_HTML, ContentTypes.TEXT_PLAIN,
			ContentTypes.TEXT_X_JSP, "application/javascript",
			"application/rtf", "application/vnd.oasis.opendocument.graphics",
			"application/vnd.oasis.opendocument.presentation",
			"application/vnd.oasis.opendocument.spreadsheet",
			"application/vnd.oasis.opendocument.text",
			"application/vnd.openxmlformats-officedocument.presentationml." +
				"presentation",
			"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
			"application/vnd.openxmlformats-officedocument.wordprocessingml." +
				"document",
			"application/vnd.sun.xml.calc", "application/vnd.sun.xml.writer",
			"application/wordperfect", "application/x-sh", "text/jsp",
			"text/jspf", "text/rtf", "text/x-java-source"));

	@Reference
	private DLFileVersionPreviewLocalService _dlFileVersionPreviewLocalService;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.document.library.preview.document)"
	)
	private ServletContext _servletContext;

}