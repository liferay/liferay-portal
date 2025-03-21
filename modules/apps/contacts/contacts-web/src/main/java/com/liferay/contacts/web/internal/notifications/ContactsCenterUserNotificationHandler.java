/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.contacts.web.internal.notifications;

import com.liferay.contacts.web.internal.constants.ContactsPortletKeys;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.notifications.BaseUserNotificationHandler;
import com.liferay.portal.kernel.notifications.UserNotificationHandler;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.social.kernel.model.SocialRelationConstants;
import com.liferay.social.kernel.model.SocialRequest;
import com.liferay.social.kernel.model.SocialRequestConstants;
import com.liferay.social.kernel.service.SocialRequestLocalService;

import jakarta.portlet.WindowState;

import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jonathan Lee
 * @deprecated As of Cavanaugh (7.4.x)
 */
@Component(
	property = "jakarta.portlet.name=" + ContactsPortletKeys.CONTACTS_CENTER,
	service = UserNotificationHandler.class
)
@Deprecated
public class ContactsCenterUserNotificationHandler
	extends BaseUserNotificationHandler {

	public ContactsCenterUserNotificationHandler() {
		setActionable(true);
		setPortletId(ContactsPortletKeys.CONTACTS_CENTER);
	}

	@Override
	protected String getBody(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			userNotificationEvent.getPayload());

		long socialRequestId = jsonObject.getLong("classPK");

		SocialRequest socialRequest =
			_socialRequestLocalService.fetchSocialRequest(socialRequestId);

		if (socialRequest == null) {
			_userNotificationEventLocalService.deleteUserNotificationEvent(
				userNotificationEvent.getUserNotificationEventId());

			return null;
		}

		String title = getTitle(userNotificationEvent, serviceContext);

		if ((socialRequest.getStatus() !=
				SocialRequestConstants.STATUS_PENDING) ||
			(socialRequest.getModifiedDate() >
				userNotificationEvent.getTimestamp())) {

			return StringUtil.replace(
				_BODY, new String[] {"[$BODY$]", "[$TITLE$]"},
				new String[] {StringPool.BLANK, title});
		}

		LiferayPortletResponse liferayPortletResponse =
			serviceContext.getLiferayPortletResponse();

		return StringUtil.replace(
			getBodyTemplate(),
			new String[] {
				"[$CONFIRM$]", "[$CONFIRM_URL$]", "[$IGNORE$]",
				"[$IGNORE_URL$]", "[$TITLE$]"
			},
			new String[] {
				serviceContext.translate("confirm"),
				PortletURLBuilder.createActionURL(
					liferayPortletResponse, ContactsPortletKeys.CONTACTS_CENTER
				).setActionName(
					"updateSocialRequest"
				).setRedirect(
					serviceContext.getLayoutFullURL()
				).setParameter(
					"socialRequestId", socialRequestId
				).setParameter(
					"status", SocialRequestConstants.STATUS_CONFIRM
				).setParameter(
					"userNotificationEventId",
					userNotificationEvent.getUserNotificationEventId()
				).setWindowState(
					WindowState.NORMAL
				).buildString(),
				serviceContext.translate("ignore"),
				PortletURLBuilder.createActionURL(
					liferayPortletResponse, ContactsPortletKeys.CONTACTS_CENTER
				).setActionName(
					"updateSocialRequest"
				).setRedirect(
					serviceContext.getLayoutFullURL()
				).setParameter(
					"socialRequestId", socialRequestId
				).setParameter(
					"status", SocialRequestConstants.STATUS_IGNORE
				).setParameter(
					"userNotificationEventId",
					userNotificationEvent.getUserNotificationEventId()
				).setWindowState(
					WindowState.NORMAL
				).buildString(),
				title
			});
	}

	@Override
	protected String getLink(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws Exception {

		return StringPool.BLANK;
	}

	@Override
	protected String getTitle(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			userNotificationEvent.getPayload());

		long socialRequestId = jsonObject.getLong("classPK");

		SocialRequest socialRequest =
			_socialRequestLocalService.fetchSocialRequest(socialRequestId);

		if (socialRequest == null) {
			_userNotificationEventLocalService.deleteUserNotificationEvent(
				userNotificationEvent.getUserNotificationEventId());

			return null;
		}

		String creatorUserName = _getUserNameLink(
			socialRequest.getUserId(), serviceContext);

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			serviceContext.getLocale(),
			ContactsCenterUserNotificationHandler.class);

		if (socialRequest.getType() ==
				SocialRelationConstants.TYPE_BI_CONNECTION) {

			return ResourceBundleUtil.getString(
				resourceBundle,
				"request-social-networking-summary-add-connection",
				new Object[] {creatorUserName});
		}

		return ResourceBundleUtil.getString(
			resourceBundle, "x-sends-you-a-social-relationship-request",
			new Object[] {creatorUserName});
	}

	private String _getUserNameLink(
		long userId, ServiceContext serviceContext) {

		try {
			if (userId <= 0) {
				return StringPool.BLANK;
			}

			User user = _userLocalService.getUserById(userId);

			String userName = user.getFullName();

			String userDisplayURL = user.getDisplayURL(
				serviceContext.getThemeDisplay());

			return StringBundler.concat(
				"<a href=\"", userDisplayURL, "\">", HtmlUtil.escape(userName),
				"</a>");
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return StringPool.BLANK;
		}
	}

	private static final String _BODY =
		"<div class=\"title\">[$TITLE$]</div><div class=\"body\">[$BODY$]" +
			"</div>";

	private static final Log _log = LogFactoryUtil.getLog(
		ContactsCenterUserNotificationHandler.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private SocialRequestLocalService _socialRequestLocalService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}