/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notifications.web.internal.servlet.taglib.util;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.notifications.UserNotificationFeedEntry;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.subscription.model.Subscription;
import com.liferay.subscription.service.SubscriptionLocalServiceUtil;

import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class UserNotificationEventActionDropdownItem {

	public UserNotificationEventActionDropdownItem(
		HttpServletRequest httpServletRequest, RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderResponse = renderResponse;
	}

	public List<DropdownItem> getActionDropdownItems(
			UserNotificationEvent userNotificationEvent,
			UserNotificationFeedEntry userNotificationFeedEntry)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			userNotificationEvent.getPayload());

		long subscriptionId = jsonObject.getLong("subscriptionId");

		if (subscriptionId > 0) {
			Subscription subscription =
				SubscriptionLocalServiceUtil.fetchSubscription(subscriptionId);

			if (subscription == null) {
				subscriptionId = 0;
			}
		}

		long finalSubscriptionId = subscriptionId;

		return DropdownItemListBuilder.add(
			() ->
				!userNotificationEvent.isActionRequired() &&
				!userNotificationEvent.isArchived(),
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.createActionURL(
						_renderResponse
					).setActionName(
						"markNotificationAsRead"
					).setRedirect(
						themeDisplay.getURLCurrent()
					).setParameter(
						"userNotificationEventId",
						userNotificationEvent.getUserNotificationEventId()
					).buildString());
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "mark-as-read"));
			}
		).add(
			() ->
				!userNotificationEvent.isActionRequired() &&
				userNotificationEvent.isArchived(),
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.createActionURL(
						_renderResponse
					).setActionName(
						"markNotificationAsUnread"
					).setRedirect(
						themeDisplay.getURLCurrent()
					).setParameter(
						"userNotificationEventId",
						userNotificationEvent.getUserNotificationEventId()
					).buildString());
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "mark-as-unread"));
			}
		).add(
			() -> finalSubscriptionId > 0,
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.createActionURL(
						_renderResponse
					).setActionName(
						"unsubscribe"
					).setRedirect(
						themeDisplay.getURLCurrent()
					).setParameter(
						"subscriptionId", finalSubscriptionId
					).setParameter(
						"userNotificationEventId",
						userNotificationEvent.getUserNotificationEventId()
					).buildString());
				dropdownItem.setLabel(
					LanguageUtil.get(
						_httpServletRequest,
						"stop-receiving-notifications-from-this-asset"));
			}
		).add(
			() -> !userNotificationFeedEntry.isActionable(),
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.createActionURL(
						_renderResponse
					).setActionName(
						"deleteUserNotificationEvent"
					).setRedirect(
						themeDisplay.getURLCurrent()
					).setParameter(
						"userNotificationEventId",
						userNotificationEvent.getUserNotificationEventId()
					).buildString());
				dropdownItem.setLabel(
					LanguageUtil.get(
						_httpServletRequest, "delete[notification-action]"));
			}
		).build();
	}

	private final HttpServletRequest _httpServletRequest;
	private final RenderResponse _renderResponse;

}