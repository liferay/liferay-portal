/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.portlet.action;

import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.scheduler.PublishScheduler;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Samuel Trong Tran
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CTPortletKeys.PUBLICATIONS,
		"mvc.command.name=/change_tracking/reschedule_publication",
		"mvc.command.name=/change_tracking/schedule_publication"
	},
	service = MVCActionCommand.class
)
public class SchedulePublicationMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long publishTime = ParamUtil.getLong(actionRequest, "publishTime");

		try {
			Date publishDate = new Date(publishTime);

			Date currentDate = new Date(System.currentTimeMillis());

			if (!publishDate.after(currentDate)) {
				JSONPortletResponseUtil.writeJSON(
					actionRequest, actionResponse,
					JSONUtil.put(
						"validationError",
						_language.get(
							_portal.getHttpServletRequest(actionRequest),
							"the-publish-time-must-be-in-the-future")));

				return;
			}

			long ctCollectionId = ParamUtil.getLong(
				actionRequest, "ctCollectionId");

			String actionName = ParamUtil.getString(
				actionRequest, ActionRequest.ACTION_NAME);

			if (actionName.equals("/change_tracking/reschedule_publication")) {
				_publishScheduler.unschedulePublish(ctCollectionId);
			}

			_publishScheduler.schedulePublish(
				ctCollectionId, themeDisplay.getUserId(), publishDate);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse,
				JSONUtil.put(
					"error",
					_language.get(
						_portal.getHttpServletRequest(actionRequest),
						"an-unexpected-error-occurred")));

			return;
		}

		JSONPortletResponseUtil.writeJSON(
			actionRequest, actionResponse,
			JSONUtil.put(
				"redirect",
				PortletURLBuilder.createRenderURL(
					_portal.getLiferayPortletResponse(actionResponse)
				).setMVCRenderCommandName(
					"/change_tracking/view_scheduled"
				).buildString()));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SchedulePublicationMVCActionCommand.class);

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private PublishScheduler _publishScheduler;

}