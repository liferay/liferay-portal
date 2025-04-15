/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.notifications;

import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.notifications.BaseUserNotificationHandler;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brooke Dalton
 */
@Component(service = ScheduledPublicationUserNotificationHandler.class)
public class ScheduledPublicationUserNotificationHandler
	extends BaseUserNotificationHandler {

	public ScheduledPublicationUserNotificationHandler() {
		setPortletId(CTPortletKeys.PUBLICATIONS);
	}

	@Override
	protected String getBody(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			userNotificationEvent.getPayload());

		long ctCollectionId = jsonObject.getLong("ctCollectionId");

		CTCollection ctCollection = _ctCollectionLocalService.fetchCTCollection(
			ctCollectionId);

		if (ctCollection == null) {
			_userNotificationEventLocalService.deleteUserNotificationEvent(
				userNotificationEvent.getUserNotificationEventId());

			return null;
		}

		boolean showConflicts = jsonObject.getBoolean("showConflicts");

		if (showConflicts) {
			boolean scheduled = jsonObject.getBoolean("scheduled");

			String title = null;

			if (scheduled) {
				title = "x-scheduled-publication-has-conflicts-with-production";
			}
			else {
				title = "x-scheduled-publication-failed";
			}

			return StringUtil.replace(
				getBodyTemplate(), new String[] {"[$BODY$]", "[$TITLE$]"},
				new String[] {
					_language.get(
						serviceContext.getLocale(),
						"click-on-this-notification-to-see-the-list-of-" +
							"conflicts-that-need-to-be-manually-resolved"),
					_language.format(
						serviceContext.getLocale(), title,
						new Object[] {jsonObject.getString("ctCollectionName")},
						false)
				});
		}

		if (_hasAdministratorRole(userNotificationEvent)) {
			return StringUtil.replace(
				getBodyTemplate(), new String[] {"[$BODY$]", "[$TITLE$]"},
				new String[] {
					_language.get(
						serviceContext.getLocale(),
						"click-on-this-notification-to-see-the-stack-trace"),
					_language.format(
						serviceContext.getLocale(),
						"x-scheduled-publication-failed-with-an-unexpected-" +
							"system-error",
						new Object[] {jsonObject.getString("ctCollectionName")},
						false)
				});
		}

		return StringUtil.replace(
			getBodyTemplate(), new String[] {"[$BODY$]", "[$TITLE$]"},
			new String[] {
				_language.get(
					serviceContext.getLocale(),
					"an-unexpected-error-occurred-while-publishing-the-" +
						"scheduled-publication"),
				_language.format(
					serviceContext.getLocale(),
					"x-scheduled-publication-failed",
					new Object[] {jsonObject.getString("ctCollectionName")},
					false)
			});
	}

	@Override
	protected String getLink(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			userNotificationEvent.getPayload());

		boolean showConflicts = jsonObject.getBoolean("showConflicts");

		if (showConflicts) {
			return PortletURLBuilder.create(
				_portal.getControlPanelPortletURL(
					serviceContext.getRequest(), serviceContext.getScopeGroup(),
					CTPortletKeys.PUBLICATIONS, 0, 0,
					PortletRequest.RENDER_PHASE)
			).setMVCRenderCommandName(
				"/change_tracking/view_conflicts"
			).setParameter(
				"ctCollectionId",
				_jsonFactory.createJSONObject(
					userNotificationEvent.getPayload()
				).getLong(
					"ctCollectionId"
				)
			).buildString();
		}

		if (!_hasAdministratorRole(userNotificationEvent)) {
			return null;
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				serviceContext.getRequest(), serviceContext.getScopeGroup(),
				CTPortletKeys.PUBLICATIONS, 0, 0, PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/change_tracking/view_stack_trace"
		).setParameter(
			"backgroundTaskId", jsonObject.getLong("backgroundTaskId")
		).setParameter(
			"ctCollectionName", jsonObject.getString("ctCollectionName")
		).buildString();
	}

	private boolean _hasAdministratorRole(
			UserNotificationEvent userNotificationEvent)
		throws Exception {

		Role role = _roleLocalService.getRole(
			userNotificationEvent.getCompanyId(), RoleConstants.ADMINISTRATOR);

		return _userLocalService.hasRoleUser(
			role.getRoleId(), userNotificationEvent.getUserId());
	}

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}