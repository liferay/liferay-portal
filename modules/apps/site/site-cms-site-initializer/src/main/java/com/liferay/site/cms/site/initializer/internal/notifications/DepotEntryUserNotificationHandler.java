/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.notifications;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotPortletKeys;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.notifications.BaseModelUserNotificationHandler;
import com.liferay.portal.kernel.notifications.UserNotificationHandler;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.site.cms.site.initializer.internal.util.ActionUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Balázs Sáfrány-Kovalik
 */
@Component(
	property = "jakarta.portlet.name=" + DepotPortletKeys.DEPOT_ADMIN,
	service = UserNotificationHandler.class
)
public class DepotEntryUserNotificationHandler
	extends BaseModelUserNotificationHandler {

	public DepotEntryUserNotificationHandler() {
		setPortletId(DepotPortletKeys.DEPOT_ADMIN);
	}

	@Override
	protected String getBody(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			userNotificationEvent.getPayload());

		long classPK = jsonObject.getLong("classPK");

		DepotEntry depotEntry = _depotEntryLocalService.fetchDepotEntry(
			classPK);

		if ((depotEntry == null) || (depotEntry.getGroup() == null) ||
			!_groupLocalService.hasUserGroup(
				serviceContext.getUserId(), depotEntry.getGroupId())) {

			_userNotificationEventLocalService.deleteUserNotificationEvent(
				userNotificationEvent.getUserNotificationEventId());

			return null;
		}

		return StringBundler.concat(
			"<div class=\"title\">", _getTitle(depotEntry, serviceContext),
			"</div>");
	}

	@Override
	protected String getLink(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			userNotificationEvent.getPayload());

		DepotEntry depotEntry = _depotEntryLocalService.fetchDepotEntry(
			jsonObject.getLong("classPK"));

		ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

		if (depotEntry.getType() == DepotConstants.TYPE_PROJECT) {
			return StringBundler.concat(
				serviceContext.getPortalURL(),
				themeDisplay.getPathFriendlyURLPublic(),
				GroupConstants.CMS_FRIENDLY_URL, "/projects");
		}

		String spaceURL = ActionUtil.getSpaceURL(
			jsonObject.getLong("classPK"), themeDisplay);

		return serviceContext.getPortalURL() + spaceURL;
	}

	@Override
	protected String getTitle(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			userNotificationEvent.getPayload());

		long classPK = jsonObject.getLong("classPK");

		DepotEntry depotEntry = _depotEntryLocalService.fetchDepotEntry(
			classPK);

		if (depotEntry == null) {
			return null;
		}

		return _getTitle(depotEntry, serviceContext);
	}

	private String _getTitle(
			DepotEntry depotEntry, ServiceContext serviceContext)
		throws Exception {

		Group group = depotEntry.getGroup();

		if (depotEntry.getType() == DepotConstants.TYPE_DESIGN_LIBRARY) {
			return serviceContext.translate(
				"you-have-been-invited-to-collaborate-in-the-x-design-library",
				HtmlUtil.escape(group.getName(serviceContext.getLocale())));
		}

		if (depotEntry.getType() == DepotConstants.TYPE_PROJECT) {
			return serviceContext.translate(
				"you-have-been-invited-to-collaborate-in-the-x-project",
				HtmlUtil.escape(group.getName(serviceContext.getLocale())));
		}

		return serviceContext.translate(
			"you-have-been-invited-to-collaborate-in-the-x-space",
			HtmlUtil.escape(group.getName(serviceContext.getLocale())));
	}

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}