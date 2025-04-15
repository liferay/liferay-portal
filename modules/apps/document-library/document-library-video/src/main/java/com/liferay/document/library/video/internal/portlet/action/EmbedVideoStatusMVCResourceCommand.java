/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.video.internal.portlet.action;

import com.liferay.document.library.constants.DLFileVersionPreviewConstants;
import com.liferay.document.library.kernel.model.DLProcessorConstants;
import com.liferay.document.library.kernel.processor.DLProcessor;
import com.liferay.document.library.kernel.processor.DLProcessorHelperUtil;
import com.liferay.document.library.kernel.processor.VideoProcessor;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.service.DLFileVersionPreviewLocalService;
import com.liferay.document.library.video.internal.constants.DLVideoPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DLVideoPortletKeys.DL_VIDEO,
		"mvc.command.name=/document_library_video/get_embed_video_status"
	},
	service = MVCResourceCommand.class
)
public class EmbedVideoStatusMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	public void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IOException {

		resourceResponse.setStatus(_getEmbedVideoStatus(resourceRequest));
	}

	private int _getEmbedVideoStatus(ResourceRequest resourceRequest) {
		try {
			FileVersion fileVersion = _dlAppLocalService.getFileVersion(
				ParamUtil.getLong(resourceRequest, "fileVersionId"));

			if (fileVersion != null) {
				VideoProcessor videoProcessor = (VideoProcessor)_dlProcessor;

				if (_isPreviewFailure(fileVersion)) {
					return HttpServletResponse.SC_SERVICE_UNAVAILABLE;
				}
				else if (!videoProcessor.hasVideo(fileVersion)) {
					return HttpServletResponse.SC_ACCEPTED;
				}

				return HttpServletResponse.SC_OK;
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return HttpServletResponse.SC_SERVICE_UNAVAILABLE;
	}

	private boolean _isPreviewFailure(FileVersion fileVersion) {
		if (_dlFileVersionPreviewLocalService.hasDLFileVersionPreview(
				fileVersion.getFileEntryId(), fileVersion.getFileVersionId(),
				DLFileVersionPreviewConstants.STATUS_FAILURE) ||
			!DLProcessorHelperUtil.isPreviewableSize(fileVersion)) {

			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EmbedVideoStatusMVCResourceCommand.class);

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLFileVersionPreviewLocalService _dlFileVersionPreviewLocalService;

	@Reference(target = "(type=" + DLProcessorConstants.VIDEO_PROCESSOR + ")")
	private DLProcessor _dlProcessor;

}