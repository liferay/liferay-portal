/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.video.internal.portlet.action;

import com.liferay.document.library.constants.DLFileVersionPreviewConstants;
import com.liferay.document.library.helper.DLURLHelper;
import com.liferay.document.library.kernel.model.DLProcessorConstants;
import com.liferay.document.library.kernel.processor.DLProcessor;
import com.liferay.document.library.kernel.processor.VideoProcessor;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.preview.exception.DLFileEntryPreviewGenerationException;
import com.liferay.document.library.service.DLFileVersionPreviewLocalService;
import com.liferay.document.library.video.internal.constants.DLVideoPortletKeys;
import com.liferay.document.library.video.internal.constants.DLVideoWebKeys;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DLVideoPortletKeys.DL_VIDEO,
		"mvc.command.name=/document_library_video/embed_video"
	},
	service = MVCRenderCommand.class
)
public class EmbedVideoMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		try {
			FileVersion fileVersion = _dlAppLocalService.getFileVersion(
				ParamUtil.getLong(renderRequest, "fileVersionId"));

			if (fileVersion != null) {
				renderRequest.setAttribute(
					FileVersion.class.getName(), fileVersion);

				VideoProcessor videoProcessor = (VideoProcessor)_dlProcessor;

				if (videoProcessor.hasVideo(fileVersion)) {
					String videoPosterURL = _getVideoPosterURL(
						fileVersion,
						(ThemeDisplay)renderRequest.getAttribute(
							WebKeys.THEME_DISPLAY));

					renderRequest.setAttribute(
						DLVideoWebKeys.PREVIEW_FILE_URLS,
						_getPreviewFileURLs(
							fileVersion, videoPosterURL, renderRequest));
					renderRequest.setAttribute(
						DLVideoWebKeys.VIDEO_POSTER_URL, videoPosterURL);

					return "/embed/video.jsp";
				}
				else if (_isPreviewFailure(fileVersion)) {
					return "/embed/error.jsp";
				}

				return "/embed/generating.jsp";
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return "/embed/error.jsp";
	}

	private List<String> _getPreviewFileURLs(
			FileVersion fileVersion, String videoPosterURL,
			RenderRequest renderRequest)
		throws PortalException {

		if (PropsValues.DL_FILE_ENTRY_PREVIEW_VIDEO_CONTAINERS.length <= 0) {
			return Collections.singletonList(videoPosterURL);
		}

		List<String> previewFileURLs = TransformUtil.transformToList(
			PropsValues.DL_FILE_ENTRY_PREVIEW_VIDEO_CONTAINERS,
			dlFileEntryPreviewVideoContainer -> {
				VideoProcessor videoProcessor = (VideoProcessor)_dlProcessor;

				long previewFileSize = videoProcessor.getPreviewFileSize(
					fileVersion, dlFileEntryPreviewVideoContainer);

				if (previewFileSize <= 0) {
					return null;
				}

				int status = ParamUtil.getInteger(
					renderRequest, "status", WorkflowConstants.STATUS_ANY);

				String previewQueryString = "&videoPreview=1";

				if (status != WorkflowConstants.STATUS_ANY) {
					previewQueryString += "&status=" + status;
				}

				ThemeDisplay themeDisplay =
					(ThemeDisplay)renderRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				return _dlURLHelper.getPreviewURL(
					fileVersion.getFileEntry(), fileVersion, themeDisplay,
					previewQueryString + "&type=" +
						dlFileEntryPreviewVideoContainer);
			});

		if (previewFileURLs.isEmpty()) {
			throw new DLFileEntryPreviewGenerationException(
				"No preview available for " + fileVersion.getTitle());
		}

		return previewFileURLs;
	}

	private String _getVideoPosterURL(
			FileVersion fileVersion, ThemeDisplay themeDisplay)
		throws PortalException {

		return _dlURLHelper.getPreviewURL(
			fileVersion.getFileEntry(), fileVersion, themeDisplay,
			"&videoThumbnail=1");
	}

	private boolean _isPreviewFailure(FileVersion fileVersion) {
		VideoProcessor videoProcessor = (VideoProcessor)_dlProcessor;

		if (_dlFileVersionPreviewLocalService.hasDLFileVersionPreview(
				fileVersion.getFileEntryId(), fileVersion.getFileVersionId(),
				DLFileVersionPreviewConstants.STATUS_FAILURE) ||
			!videoProcessor.isVideoSupported(fileVersion)) {

			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EmbedVideoMVCRenderCommand.class);

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLFileVersionPreviewLocalService _dlFileVersionPreviewLocalService;

	@Reference(target = "(type=" + DLProcessorConstants.VIDEO_PROCESSOR + ")")
	private DLProcessor _dlProcessor;

	@Reference
	private DLURLHelper _dlURLHelper;

}