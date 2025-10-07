/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.context;

import com.liferay.notification.model.NotificationTemplate;

import java.util.Map;

/**
 * @author Feliphe Marinho
 */
public class NotificationContextBuilder {

	public NotificationContext build() {
		return _notificationContext;
	}

	public NotificationContextBuilder className(String className) {
		_notificationContext.setClassName(className);

		return this;
	}

	public NotificationContextBuilder classPK(long classPK) {
		_notificationContext.setClassPK(classPK);

		return this;
	}

	public NotificationContextBuilder companyId(long companyId) {
		_notificationContext.setCompanyId(companyId);

		return this;
	}

	public NotificationContextBuilder externalReferenceCode(
		String externalReferenceCode) {

		_notificationContext.setExternalReferenceCode(externalReferenceCode);

		return this;
	}

	public NotificationContextBuilder groupId(long groupId) {
		_notificationContext.setGroupId(groupId);

		return this;
	}

	public NotificationContextBuilder notificationTemplate(
		NotificationTemplate notificationTemplate) {

		_notificationContext.setNotificationTemplate(notificationTemplate);

		return this;
	}

	public NotificationContextBuilder portletId(String portletId) {
		_notificationContext.setPortletId(portletId);

		return this;
	}

	public NotificationContextBuilder preferredLanguageId(
		String preferredLanguageId) {

		_notificationContext.setPreferredLanguageId(preferredLanguageId);

		return this;
	}

	public NotificationContextBuilder termValues(
		Map<String, Object> termValues) {

		_notificationContext.setTermValues(termValues);

		return this;
	}

	public NotificationContextBuilder usePreferredLanguageForGuests(
		boolean usePreferredLanguageForGuests) {

		_notificationContext.setUsePreferredLanguageForGuests(
			usePreferredLanguageForGuests);

		return this;
	}

	public NotificationContextBuilder userId(long userId) {
		_notificationContext.setUserId(userId);

		return this;
	}

	private final NotificationContext _notificationContext =
		new NotificationContext();

}