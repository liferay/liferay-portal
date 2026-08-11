/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.util;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Alberto Sousa
 */
public class ObjectEntryPortletURLUtil {

	public static PortletURL getRelatedObjectEntryPortletURL(
		Group group, HttpServletRequest httpServletRequest, String lifecycle,
		ObjectDefinition objectDefinition, String portletId) {

		if (Validator.isNull(portletId)) {
			return PortalUtil.getControlPanelPortletURL(
				httpServletRequest, group, objectDefinition.getPortletId(), 0,
				0, lifecycle);
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		if (layout.isTypeControlPanel()) {
			return PortalUtil.getControlPanelPortletURL(
				httpServletRequest, group, portletId, 0, 0, lifecycle);
		}

		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(httpServletRequest);

		return requestBackedPortletURLFactory.createPortletURL(
			portletId, lifecycle);
	}

}