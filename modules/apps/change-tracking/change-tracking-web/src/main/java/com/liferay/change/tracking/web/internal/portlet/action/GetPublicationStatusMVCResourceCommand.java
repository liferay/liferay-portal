/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.portlet.action;

import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.model.CTProcess;
import com.liferay.change.tracking.service.CTProcessLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.backgroundtask.display.BackgroundTaskDisplay;
import com.liferay.portal.kernel.backgroundtask.display.BackgroundTaskDisplayFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.Serializable;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Samuel Trong Tran
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CTPortletKeys.PUBLICATIONS,
		"mvc.command.name=/change_tracking/get_publication_status"
	},
	service = MVCResourceCommand.class
)
public class GetPublicationStatusMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IOException, PortalException {

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			resourceRequest);

		long ctProcessId = ParamUtil.getLong(resourceRequest, "ctProcessId");

		CTProcess ctProcess = _ctProcessLocalService.fetchCTProcess(
			ctProcessId);

		String displayType = "danger";
		String label = _language.get(httpServletRequest, "failed");
		int percentage = -1;
		boolean published = false;

		if (ctProcess == null) {
			_writeJSON(
				resourceRequest, resourceResponse, displayType, label,
				percentage, published);

			return;
		}

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.fetchBackgroundTask(
				ctProcess.getBackgroundTaskId());

		if (backgroundTask == null) {
			_writeJSON(
				resourceRequest, resourceResponse, displayType, label,
				percentage, published);

			return;
		}

		BackgroundTaskDisplay backgroundTaskDisplay =
			_backgroundTaskDisplayFactory.getBackgroundTaskDisplay(
				backgroundTask.getBackgroundTaskId());

		percentage = backgroundTaskDisplay.getPercentage();

		if (backgroundTask.getStatus() ==
				BackgroundTaskConstants.STATUS_IN_PROGRESS) {

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put("percentage", percentage));

			return;
		}

		if ((backgroundTask.getStatus() ==
				BackgroundTaskConstants.STATUS_FAILED) &&
			StringUtil.matchesIgnoreCase(
				backgroundTask.getStatusMessage(), "duplicate entry")) {

			Map<String, Serializable> taskContextMap =
				backgroundTask.getTaskContextMap();

			label = _language.get(httpServletRequest, "conflict");

			_writeJSON(
				resourceRequest, resourceResponse, displayType, label,
				percentage, published);

			throw new PortalException(
				StringBundler.concat(
					"The selected changes cannot be published to production ",
					"because one or more of your selected changes from the ",
					"source change tracking collection ",
					taskContextMap.get("ctCollectionId"),
					" conflicts with a preexisting change in the destination ",
					"change tracking collection"));
		}

		if (backgroundTask.getStatus() ==
				BackgroundTaskConstants.STATUS_SUCCESSFUL) {

			displayType = "success";
			label = _language.get(httpServletRequest, "published");
			published = true;
		}

		_writeJSON(
			resourceRequest, resourceResponse, displayType, label, percentage,
			published);
	}

	private void _writeJSON(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse,
			String displayType, String label, int percentage, boolean published)
		throws IOException {

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			JSONUtil.put(
				"displayType", displayType
			).put(
				"label", label
			).put(
				"percentage",
				() -> {
					if (percentage < 0) {
						return null;
					}

					return percentage;
				}
			).put(
				"published", published
			));
	}

	@Reference
	private BackgroundTaskDisplayFactory _backgroundTaskDisplayFactory;

	@Reference
	private BackgroundTaskLocalService _backgroundTaskLocalService;

	@Reference
	private CTProcessLocalService _ctProcessLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}