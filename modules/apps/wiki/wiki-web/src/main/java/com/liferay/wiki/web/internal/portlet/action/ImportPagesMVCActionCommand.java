/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.portlet.action;

import com.liferay.petra.io.StreamUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ProgressTracker;
import com.liferay.portal.kernel.util.ProgressTrackerThreadLocal;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.exception.NoSuchNodeException;
import com.liferay.wiki.service.WikiNodeService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletException;

import java.io.IOException;
import java.io.InputStream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 */
@Component(
	property = {
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI_ADMIN,
		"mvc.command.name=/wiki/import_pages"
	},
	service = MVCActionCommand.class
)
public class ImportPagesMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			_importPages(actionRequest);
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchNodeException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());
			}
			else if (exception instanceof PortalException) {
				SessionErrors.add(actionRequest, exception.getClass());
			}
			else {
				throw new PortletException(exception);
			}
		}
	}

	private void _importPages(ActionRequest actionRequest) throws Exception {
		UploadPortletRequest uploadPortletRequest =
			_portal.getUploadPortletRequest(actionRequest);

		String importProgressId = ParamUtil.getString(
			uploadPortletRequest, "importProgressId");

		ProgressTracker progressTracker = new ProgressTracker(importProgressId);

		ProgressTrackerThreadLocal.setProgressTracker(progressTracker);

		progressTracker.start(actionRequest);

		long nodeId = ParamUtil.getLong(uploadPortletRequest, "nodeId");

		InputStream[] inputStreams = new InputStream[_MAX_FILE_COUNT];

		try {
			for (int i = 0; i < _MAX_FILE_COUNT; i++) {
				inputStreams[i] = uploadPortletRequest.getFileAsStream(
					"file" + i);
			}

			_wikiNodeService.importPages(
				nodeId, inputStreams, actionRequest.getParameterMap());
		}
		finally {
			try {
				StreamUtil.cleanUp(inputStreams);
			}
			catch (IOException ioException) {
				if (_log.isWarnEnabled()) {
					_log.warn(ioException);
				}
			}
		}

		progressTracker.finish(actionRequest);
	}

	private static final int _MAX_FILE_COUNT = 3;

	private static final Log _log = LogFactoryUtil.getLog(
		ImportPagesMVCActionCommand.class);

	@Reference
	private Portal _portal;

	@Reference
	private WikiNodeService _wikiNodeService;

}