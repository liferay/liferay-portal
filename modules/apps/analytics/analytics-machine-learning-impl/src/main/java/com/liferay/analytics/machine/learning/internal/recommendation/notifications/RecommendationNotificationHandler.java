/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.machine.learning.internal.recommendation.notifications;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.notifications.BaseUserNotificationHandler;
import com.liferay.portal.kernel.notifications.UserNotificationHandler;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.PortletRequest;

import java.util.Locale;

import org.apache.http.client.utils.URIBuilder;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcos Martins
 */
@Component(
	property = "jakarta.portlet.name=" + PortletKeys.RECOMMENDATIONS,
	service = UserNotificationHandler.class
)
public class RecommendationNotificationHandler
	extends BaseUserNotificationHandler {

	public RecommendationNotificationHandler() {
		setPortletId(PortletKeys.RECOMMENDATIONS);
	}

	@Override
	protected String getBody(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			userNotificationEvent.getPayload());

		return StringUtil.replace(
			getBodyTemplate(), new String[] {"[$BODY$]", "[$TITLE$]"},
			new String[] {
				StringPool.BLANK,
				_getTitle(
					serviceContext.getLocale(),
					jsonObject.getInt("notificationTypeCode"))
			});
	}

	@Override
	protected String getLink(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws Exception {

		URIBuilder uriBuilder = new URIBuilder(
			PortletURLBuilder.create(
				_portal.getControlPanelPortletURL(
					serviceContext.getRequest(),
					ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
					PortletRequest.RENDER_PHASE)
			).setMVCRenderCommandName(
				"/configuration_admin/view_configuration_screen"
			).setParameter(
				"configurationScreenKey", "analytics-cloud-connection"
			).buildString());

		uriBuilder.addParameter("currentPage", "RECOMMENDATIONS");

		return uriBuilder.build(
		).toString();
	}

	@Override
	protected String getTitle(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			userNotificationEvent.getPayload());

		return _getTitle(
			serviceContext.getLocale(),
			jsonObject.getInt("notificationTypeCode"));
	}

	private String _getTitle(Locale locale, int notificationTypeCode) {
		RecommendationNotificationType recommendationNotificationType =
			RecommendationNotificationType.fromNotificationTypeCode(
				notificationTypeCode);

		if (recommendationNotificationType == null) {
			return null;
		}

		return _language.get(locale, recommendationNotificationType.getKey());
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}