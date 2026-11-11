/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.model.impl;

import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;

/**
 * @author Adam Brandizzi
 */
public class CalendarNotificationTemplateImpl
	extends CalendarNotificationTemplateBaseImpl {

	@Override
	public String getNotificationTypeSettings() {
		if (_notificationTypeSettingsUnicodeProperties == null) {
			return super.getNotificationTypeSettings();
		}

		return _notificationTypeSettingsUnicodeProperties.toString();
	}

	@Override
	public UnicodeProperties getNotificationTypeSettingsProperties() {
		if (_notificationTypeSettingsUnicodeProperties == null) {
			_notificationTypeSettingsUnicodeProperties =
				UnicodePropertiesBuilder.create(
					true
				).load(
					super.getNotificationTypeSettings()
				).build();
		}

		return _notificationTypeSettingsUnicodeProperties;
	}

	@Override
	public void setNotificationTypeSettings(String notificationTypeSettings) {
		_notificationTypeSettingsUnicodeProperties = null;

		super.setNotificationTypeSettings(notificationTypeSettings);
	}

	@Override
	public void setTypeSettingsProperties(
		UnicodeProperties notificationTypeSettingsUnicodeProperties) {

		_notificationTypeSettingsUnicodeProperties =
			notificationTypeSettingsUnicodeProperties;

		super.setNotificationTypeSettings(
			_notificationTypeSettingsUnicodeProperties.toString());
	}

	private UnicodeProperties _notificationTypeSettingsUnicodeProperties;

}