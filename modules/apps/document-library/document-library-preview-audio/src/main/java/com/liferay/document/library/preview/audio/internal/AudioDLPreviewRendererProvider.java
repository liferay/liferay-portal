/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.preview.audio.internal;

import com.liferay.document.library.constants.DLFileVersionPreviewConstants;
import com.liferay.document.library.kernel.model.DLProcessorConstants;
import com.liferay.document.library.kernel.processor.AudioProcessor;
import com.liferay.document.library.kernel.processor.DLProcessor;
import com.liferay.document.library.kernel.processor.DLProcessorHelperUtil;
import com.liferay.document.library.preview.DLPreviewRenderer;
import com.liferay.document.library.preview.DLPreviewRendererProvider;
import com.liferay.document.library.preview.audio.internal.constants.DLPreviewAudioWebKeys;
import com.liferay.document.library.preview.exception.DLFileEntryPreviewGenerationException;
import com.liferay.document.library.preview.exception.DLPreviewGenerationInProcessException;
import com.liferay.document.library.preview.exception.DLPreviewSizeException;
import com.liferay.document.library.service.DLFileVersionPreviewLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = DLPreviewRendererProvider.class)
public class AudioDLPreviewRendererProvider
	implements DLPreviewRendererProvider {

	@Override
	public Set<String> getMimeTypes() {
		AudioProcessor audioProcessor = (AudioProcessor)_dlProcessor;

		return audioProcessor.getAudioMimeTypes();
	}

	@Override
	public DLPreviewRenderer getPreviewDLPreviewRenderer(
		FileVersion fileVersion) {

		AudioProcessor audioProcessor = (AudioProcessor)_dlProcessor;

		if (!audioProcessor.hasAudio(fileVersion) &&
			!audioProcessor.isAudioSupported(fileVersion.getMimeType())) {

			return null;
		}

		return (request, response) -> {
			_checkForPreviewGenerationExceptions(fileVersion);

			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher("/preview/view.jsp");

			request.setAttribute(
				DLPreviewAudioWebKeys.PREVIEW_FILE_URLS,
				_getPreviewFileURLs(fileVersion, request));
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

		AudioProcessor audioProcessor = (AudioProcessor)_dlProcessor;

		if (!audioProcessor.hasAudio(fileVersion)) {
			if (!DLProcessorHelperUtil.isPreviewableSize(fileVersion)) {
				throw new DLPreviewSizeException(
					DLProcessorHelperUtil.getPreviewableProcessorMaxSize(
						fileVersion.getGroupId()));
			}

			throw new DLPreviewGenerationInProcessException();
		}
	}

	private List<String> _getPreviewFileURLs(
			FileVersion fileVersion, HttpServletRequest httpServletRequest)
		throws PortalException {

		List<String> previewFileURLs = new ArrayList<>();

		int status = ParamUtil.getInteger(
			httpServletRequest, "status", WorkflowConstants.STATUS_ANY);

		String previewQueryString = "&audioPreview=1";

		if (status != WorkflowConstants.STATUS_ANY) {
			previewQueryString += "&status=" + status;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			AudioProcessor audioProcessor = (AudioProcessor)_dlProcessor;

			for (String dlFileEntryPreviewAudioContainer :
					PropsValues.DL_FILE_ENTRY_PREVIEW_AUDIO_CONTAINERS) {

				long previewFileSize = audioProcessor.getPreviewFileSize(
					fileVersion, dlFileEntryPreviewAudioContainer);

				if (previewFileSize > 0) {
					previewFileURLs.add(
						_dlURLHelper.getPreviewURL(
							fileVersion.getFileEntry(), fileVersion,
							themeDisplay,
							previewQueryString + "&type=" +
								dlFileEntryPreviewAudioContainer));
				}
			}
		}
		catch (Exception exception) {
			throw new PortalException(exception);
		}

		if (previewFileURLs.isEmpty()) {
			throw new PortalException(
				"No preview available for " + fileVersion.getTitle());
		}

		return previewFileURLs;
	}

	@Reference
	private DLFileVersionPreviewLocalService _dlFileVersionPreviewLocalService;

	@Reference(target = "(type=" + DLProcessorConstants.AUDIO_PROCESSOR + ")")
	private DLProcessor _dlProcessor;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.document.library.preview.audio)"
	)
	private ServletContext _servletContext;

}