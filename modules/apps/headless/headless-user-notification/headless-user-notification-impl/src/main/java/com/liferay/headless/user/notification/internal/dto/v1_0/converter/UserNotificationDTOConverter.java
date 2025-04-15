/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.user.notification.internal.dto.v1_0.converter;

import com.liferay.headless.user.notification.dto.v1_0.UserNotification;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.notifications.UserNotificationFeedEntry;
import com.liferay.portal.kernel.notifications.UserNotificationHandler;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.util.Date;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Correa
 */
@Component(
	property = "dto.class.name=com.liferay.portal.kernel.model.UserNotificationEvent",
	service = DTOConverter.class
)
public class UserNotificationDTOConverter
	implements DTOConverter<UserNotificationEvent, UserNotification> {

	@Override
	public String getContentType() {
		return UserNotification.class.getSimpleName();
	}

	@Override
	public UserNotificationEvent getObject(String externalReferenceCode)
		throws Exception {

		return _userNotificationEventLocalService.getUserNotificationEvent(
			GetterUtil.getLong(externalReferenceCode));
	}

	@Override
	public UserNotification toDTO(
			DTOConverterContext dtoConverterContext,
			UserNotificationEvent userNotificationEvent)
		throws Exception {

		return new UserNotification() {
			{
				setActions(
					() -> {
						if (dtoConverterContext == null) {
							return null;
						}

						return dtoConverterContext.getActions();
					});
				setDateCreated(
					() -> new Date(userNotificationEvent.getTimestamp()));
				setId(userNotificationEvent::getUserNotificationEventId);
				setMessage(
					() -> _getNotificationMessage(
						dtoConverterContext, userNotificationEvent));
				setRead(userNotificationEvent::isArchived);
				setType(
					() -> {
						JSONObject jsonObject = _jsonFactory.createJSONObject(
							userNotificationEvent.getPayload());

						if (!jsonObject.has("notificationType")) {
							return null;
						}

						return jsonObject.getInt("notificationType");
					});
			}
		};
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, UserNotificationHandler.class,
			"jakarta.portlet.name");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private String _getNotificationMessage(
			DTOConverterContext dtoConverterContext,
			UserNotificationEvent userNotificationEvent)
		throws Exception {

		UserNotificationHandler userNotificationHandler =
			_serviceTrackerMap.getService(userNotificationEvent.getType());

		if (userNotificationHandler == null) {
			return null;
		}

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			dtoConverterContext.getHttpServletRequest());

		serviceContext.setLanguageId(
			_language.getLanguageId(dtoConverterContext.getLocale()));

		UserNotificationFeedEntry userNotificationFeedEntry =
			userNotificationHandler.interpret(
				userNotificationEvent, serviceContext);

		if (userNotificationFeedEntry == null) {
			return null;
		}

		return userNotificationFeedEntry.getTitle();
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	private ServiceTrackerMap<String, UserNotificationHandler>
		_serviceTrackerMap;

	@Reference
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}