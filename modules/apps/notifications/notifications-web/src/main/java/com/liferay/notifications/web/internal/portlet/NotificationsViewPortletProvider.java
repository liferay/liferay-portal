/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notifications.web.internal.portlet;

import com.liferay.application.list.PanelApp;
import com.liferay.notifications.web.internal.constants.NotificationsPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.BasePortletProvider;
import com.liferay.portal.kernel.portlet.PortletProvider;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = "model.class.name=com.liferay.portal.kernel.model.UserNotificationEvent",
	service = PortletProvider.class
)
public class NotificationsViewPortletProvider extends BasePortletProvider {

	@Override
	public String getPortletName() {
		return NotificationsPortletKeys.NOTIFICATIONS;
	}

	@Override
	public PortletURL getPortletURL(HttpServletRequest httpServletRequest)
		throws PortalException {

		return _panelApp.getPortletURL(httpServletRequest);
	}

	@Override
	public PortletURL getPortletURL(
			HttpServletRequest httpServletRequest, Group group)
		throws PortalException {

		return getPortletURL(httpServletRequest);
	}

	@Override
	public Action[] getSupportedActions() {
		return _supportedActions;
	}

	@Reference(
		target = "(jakarta.portlet.name=" + NotificationsPortletKeys.NOTIFICATIONS + ")"
	)
	private PanelApp _panelApp;

	private final Action[] _supportedActions = {Action.VIEW};

}