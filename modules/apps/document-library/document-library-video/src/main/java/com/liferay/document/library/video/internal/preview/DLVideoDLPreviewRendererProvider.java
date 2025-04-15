/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.video.internal.preview;

import com.liferay.document.library.constants.DLFileVersionPreviewConstants;
import com.liferay.document.library.kernel.model.DLProcessorConstants;
import com.liferay.document.library.kernel.processor.DLProcessor;
import com.liferay.document.library.kernel.processor.DLProcessorHelperUtil;
import com.liferay.document.library.kernel.processor.VideoProcessor;
import com.liferay.document.library.preview.DLPreviewRenderer;
import com.liferay.document.library.preview.DLPreviewRendererProvider;
import com.liferay.document.library.preview.exception.DLFileEntryPreviewGenerationException;
import com.liferay.document.library.preview.exception.DLPreviewGenerationInProcessException;
import com.liferay.document.library.preview.exception.DLPreviewSizeException;
import com.liferay.document.library.service.DLFileVersionPreviewLocalService;
import com.liferay.document.library.video.renderer.DLVideoRenderer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.util.ContentTypes;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = DLPreviewRendererProvider.class)
public class DLVideoDLPreviewRendererProvider
	implements DLPreviewRendererProvider {

	@Override
	public Set<String> getMimeTypes() {
		Set<String> mimeTypes = new HashSet<>();

		mimeTypes.add(
			ContentTypes.APPLICATION_VND_LIFERAY_VIDEO_EXTERNAL_SHORTCUT_HTML);

		VideoProcessor videoProcessor = (VideoProcessor)_dlProcessor;

		mimeTypes.addAll(videoProcessor.getVideoMimeTypes());

		return mimeTypes;
	}

	@Override
	public DLPreviewRenderer getPreviewDLPreviewRenderer(
		FileVersion fileVersion) {

		VideoProcessor videoProcessor = (VideoProcessor)_dlProcessor;

		if ((fileVersion != null) && !videoProcessor.hasVideo(fileVersion) &&
			!Objects.equals(
				fileVersion.getMimeType(),
				ContentTypes.
					APPLICATION_VND_LIFERAY_VIDEO_EXTERNAL_SHORTCUT_HTML) &&
			!videoProcessor.isVideoSupported(fileVersion.getMimeType())) {

			return null;
		}

		return (request, response) -> {
			_checkForPreviewGenerationExceptions(fileVersion);

			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher("/preview.jsp");

			request.setAttribute(FileVersion.class.getName(), fileVersion);
			request.setAttribute(
				DLVideoRenderer.class.getName(), _dlVideoRenderer);

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

		if (Objects.equals(
				fileVersion.getMimeType(),
				ContentTypes.
					APPLICATION_VND_LIFERAY_VIDEO_EXTERNAL_SHORTCUT_HTML)) {

			return;
		}

		if (_dlFileVersionPreviewLocalService.hasDLFileVersionPreview(
				fileVersion.getFileEntryId(), fileVersion.getFileVersionId(),
				DLFileVersionPreviewConstants.STATUS_FAILURE)) {

			throw new DLFileEntryPreviewGenerationException();
		}

		VideoProcessor videoProcessor = (VideoProcessor)_dlProcessor;

		if (!videoProcessor.hasVideo(fileVersion)) {
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

	@Reference(target = "(type=" + DLProcessorConstants.VIDEO_PROCESSOR + ")")
	private DLProcessor _dlProcessor;

	@Reference
	private DLVideoRenderer _dlVideoRenderer;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.document.library.video)"
	)
	private ServletContext _servletContext;

}