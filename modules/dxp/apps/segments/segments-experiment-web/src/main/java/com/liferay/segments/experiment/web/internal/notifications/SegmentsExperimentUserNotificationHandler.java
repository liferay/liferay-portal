/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.experiment.web.internal.notifications;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.notifications.BaseUserNotificationHandler;
import com.liferay.portal.kernel.notifications.UserNotificationHandler;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.constants.SegmentsExperimentConstants;
import com.liferay.segments.constants.SegmentsPortletKeys;
import com.liferay.segments.model.SegmentsExperiment;
import com.liferay.segments.service.SegmentsExperimentLocalService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = "jakarta.portlet.name=" + SegmentsPortletKeys.SEGMENTS_EXPERIMENT,
	service = UserNotificationHandler.class
)
public class SegmentsExperimentUserNotificationHandler
	extends BaseUserNotificationHandler {

	public SegmentsExperimentUserNotificationHandler() {
		setPortletId(SegmentsPortletKeys.SEGMENTS_EXPERIMENT);
	}

	@Override
	protected String getBody(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws Exception {

		SegmentsExperiment segmentsExperiment = _getSegmentsExperiment(
			userNotificationEvent);

		if (segmentsExperiment == null) {
			_userNotificationEventLocalService.deleteUserNotificationEvent(
				userNotificationEvent.getUserNotificationEventId());

			return null;
		}

		String title = _getTitle(segmentsExperiment, serviceContext);

		if (title == null) {
			return null;
		}

		return StringUtil.replace(
			getBodyTemplate(), new String[] {"[$BODY$]", "[$TITLE$]"},
			new String[] {
				HtmlUtil.escape(
					StringUtil.shorten(
						HtmlUtil.escape(segmentsExperiment.getName()))),
				title
			});
	}

	@Override
	protected String getLink(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			userNotificationEvent.getPayload());

		long referrerClassNameId = jsonObject.getLong("referrerClassNameId");

		if (referrerClassNameId != _portal.getClassNameId(
				Layout.class.getName())) {

			return StringPool.BLANK;
		}

		long referrerClassPK = jsonObject.getLong("referrerClassPK");

		Layout layout = _layoutLocalService.fetchLayout(referrerClassPK);

		if (layout == null) {
			return StringPool.BLANK;
		}

		String segmentsExperimentKey = jsonObject.getString(
			"segmentsExperimentKey");

		return _getLayoutURL(layout, segmentsExperimentKey, serviceContext);
	}

	@Override
	protected String getTitle(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws Exception {

		SegmentsExperiment segmentsExperiment = _getSegmentsExperiment(
			userNotificationEvent);

		if (segmentsExperiment == null) {
			_userNotificationEventLocalService.deleteUserNotificationEvent(
				userNotificationEvent.getUserNotificationEventId());

			return null;
		}

		return _getTitle(segmentsExperiment, serviceContext);
	}

	private String _getLayoutURL(
		Layout layout, String segmentsExperimentKey,
		ServiceContext serviceContext) {

		HttpServletRequest httpServletRequest = serviceContext.getRequest();

		if (httpServletRequest == null) {
			return StringPool.BLANK;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			return HttpComponentsUtil.addParameter(
				_portal.getLayoutURL(layout, themeDisplay, false),
				"segmentsExperimentKey", segmentsExperimentKey);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return StringPool.BLANK;
		}
	}

	private SegmentsExperiment _getSegmentsExperiment(
			UserNotificationEvent userNotificationEvent)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			userNotificationEvent.getPayload());

		return _segmentsExperimentLocalService.fetchSegmentsExperiment(
			jsonObject.getLong("classPK"));
	}

	private String _getTitle(
			SegmentsExperiment segmentsExperiment,
			ServiceContext serviceContext)
		throws Exception {

		SegmentsExperimentConstants.Status status =
			SegmentsExperimentConstants.Status.parse(
				segmentsExperiment.getStatus());

		if (status == null) {
			return null;
		}

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", serviceContext.getLocale(), getClass());

		return ResourceBundleUtil.getString(
			resourceBundle, "ab-test-has-changed-status-to-x",
			status.getLabel());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SegmentsExperimentUserNotificationHandler.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsExperimentLocalService _segmentsExperimentLocalService;

	@Reference
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}