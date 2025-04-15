/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.push.notifications.web.internal.portlet;

import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.push.notifications.constants.PushNotificationsPortletKeys;
import com.liferay.push.notifications.service.PushNotificationsDeviceService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bruno Farache
 */
@Component(
	property = {
		"com.liferay.portlet.css-class-wrapper=push-notifications",
		"com.liferay.portlet.display-category=category.hidden",
		"jakarta.portlet.display-name=Push Notifications",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.info.keywords=Push Notifications",
		"jakarta.portlet.info.short-title=Push Notifications",
		"jakarta.portlet.info.title=Push Notifications",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + PushNotificationsPortletKeys.PUSH_NOTIFICATIONS,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class PushNotificationsPortlet extends MVCPortlet {

	public void deletePushNotificationsDevice(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long pushNotificationsDeviceId = ParamUtil.getLong(
			actionRequest, "pushNotificationsDeviceId");

		_pushNotificationsDeviceService.deletePushNotificationsDevice(
			pushNotificationsDeviceId);
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		super.render(renderRequest, renderResponse);
	}

	@Reference
	private PushNotificationsDeviceService _pushNotificationsDeviceService;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.push.notifications.web)(&(release.schema.version>=1.0.0)(!(release.schema.version>=2.0.0))))"
	)
	private Release _release;

}