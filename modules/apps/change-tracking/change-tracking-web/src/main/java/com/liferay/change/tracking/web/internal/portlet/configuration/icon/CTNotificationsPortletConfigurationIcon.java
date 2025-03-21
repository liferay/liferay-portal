/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.portlet.configuration.icon;

import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.configuration.icon.BaseJSPPortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.ResourceURL;

import jakarta.servlet.ServletContext;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gislayne Vitorino
 */
@Component(
	property = "jakarta.portlet.name=" + CTPortletKeys.PUBLICATIONS,
	service = PortletConfigurationIcon.class
)
public class CTNotificationsPortletConfigurationIcon
	extends BaseJSPPortletConfigurationIcon {

	@Override
	public Map<String, Object> getContext(PortletRequest portletRequest) {
		ResourceURL saveDisplayPreferenceURL =
			(ResourceURL)PortletURLBuilder.create(
				PortletURLFactoryUtil.create(
					portletRequest, CTPortletKeys.PUBLICATIONS,
					PortletRequest.RESOURCE_PHASE)
			).buildPortletURL();

		saveDisplayPreferenceURL.setResourceID(
			"/change_tracking/save_display_preference");

		portletRequest.setAttribute(
			"saveDisplayPreferenceURL", saveDisplayPreferenceURL.toString());

		return HashMapBuilder.<String, Object>put(
			"action", getNamespace(portletRequest) + "CTNotifications"
		).put(
			"globalAction", true
		).build();
	}

	@Override
	public String getJspPath() {
		return "/publications/configuration/icon/ct_notifications.jsp";
	}

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return _language.get(getLocale(portletRequest), "notifications");
	}

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		PortalPreferences portalPreferences =
			_portletPreferencesFactory.getPortalPreferences(portletRequest);

		long hideContextChangeWarningExpiryTime = GetterUtil.getLong(
			portalPreferences.getValue(
				CTPortletKeys.PUBLICATIONS,
				"hideContextChangeWarningExpiryTime"));

		if (Objects.equals(hideContextChangeWarningExpiryTime, -1L)) {
			return true;
		}

		if (hideContextChangeWarningExpiryTime <= System.currentTimeMillis()) {
			return false;
		}

		return true;
	}

	@Override
	protected ServletContext getServletContext() {
		return _servletContext;
	}

	@Reference
	private Language _language;

	@Reference
	private PortletPreferencesFactory _portletPreferencesFactory;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.change.tracking.web)"
	)
	private ServletContext _servletContext;

}