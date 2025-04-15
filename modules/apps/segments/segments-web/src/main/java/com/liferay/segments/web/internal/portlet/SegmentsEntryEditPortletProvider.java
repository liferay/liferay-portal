/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.web.internal.portlet;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.BasePortletProvider;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.constants.SegmentsActionKeys;
import com.liferay.segments.constants.SegmentsPortletKeys;
import com.liferay.segments.web.internal.security.permission.resource.SegmentsResourcePermission;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Eduardo García
 */
@Component(
	property = "model.class.name=com.liferay.segments.model.SegmentsEntry",
	service = PortletProvider.class
)
public class SegmentsEntryEditPortletProvider extends BasePortletProvider {

	@Override
	public String getPortletName() {
		return SegmentsPortletKeys.SEGMENTS;
	}

	@Override
	public PortletURL getPortletURL(HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!SegmentsResourcePermission.contains(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroupId(),
				SegmentsActionKeys.MANAGE_SEGMENTS_ENTRIES)) {

			return null;
		}

		return PortletURLBuilder.create(
			super.getPortletURL(httpServletRequest)
		).setMVCRenderCommandName(
			"/segments/edit_segments_entry"
		).buildPortletURL();
	}

	@Override
	public Action[] getSupportedActions() {
		return _supportedActions;
	}

	private final Action[] _supportedActions = {Action.EDIT};

}