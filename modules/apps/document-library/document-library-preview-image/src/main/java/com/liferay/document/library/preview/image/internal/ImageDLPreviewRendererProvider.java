/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.preview.image.internal;

import com.liferay.document.library.constants.DLFileVersionPreviewConstants;
import com.liferay.document.library.kernel.model.DLProcessorConstants;
import com.liferay.document.library.kernel.processor.DLProcessor;
import com.liferay.document.library.kernel.processor.DLProcessorHelperUtil;
import com.liferay.document.library.kernel.processor.ImageProcessor;
import com.liferay.document.library.preview.DLPreviewRenderer;
import com.liferay.document.library.preview.DLPreviewRendererProvider;
import com.liferay.document.library.preview.exception.DLFileEntryPreviewGenerationException;
import com.liferay.document.library.preview.exception.DLPreviewGenerationInProcessException;
import com.liferay.document.library.preview.exception.DLPreviewSizeException;
import com.liferay.document.library.service.DLFileVersionPreviewLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;

import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = DLPreviewRendererProvider.class)
public class ImageDLPreviewRendererProvider
	implements DLPreviewRendererProvider {

	@Override
	public Set<String> getMimeTypes() {
		ImageProcessor imageProcessor = (ImageProcessor)_dlProcessor;

		return imageProcessor.getImageMimeTypes();
	}

	@Override
	public DLPreviewRenderer getPreviewDLPreviewRenderer(
		FileVersion fileVersion) {

		ImageProcessor imageProcessor = (ImageProcessor)_dlProcessor;

		if ((fileVersion == null) || (fileVersion.getSize() == 0) ||
			(!imageProcessor.hasImages(fileVersion) &&
			 !imageProcessor.isImageSupported(fileVersion.getMimeType()))) {

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

		ImageProcessor imageProcessor = (ImageProcessor)_dlProcessor;

		if (!imageProcessor.hasImages(fileVersion)) {
			if (!DLProcessorHelperUtil.isPreviewableSize(fileVersion)) {
				throw new DLPreviewSizeException(
					DLProcessorHelperUtil.getPreviewableProcessorMaxSize(
						fileVersion.getGroupId()));
			}

			throw new DLPreviewGenerationInProcessException();
		}
	}

	@Reference
	private DLFileVersionPreviewLocalService _dlFileVersionPreviewLocalService;

	@Reference(target = "(type=" + DLProcessorConstants.IMAGE_PROCESSOR + ")")
	private DLProcessor _dlProcessor;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.document.library.preview.image)"
	)
	private ServletContext _servletContext;

}