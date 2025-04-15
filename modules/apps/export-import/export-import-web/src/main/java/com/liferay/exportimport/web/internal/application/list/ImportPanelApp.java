/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.exportimport.constants.ExportImportPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Daniel Kocsis
 */
@Component(
	property = {
		"panel.app.order:Integer=400",
		"panel.category.key=" + PanelCategoryKeys.SITE_ADMINISTRATION_PUBLISHING
	},
	service = PanelApp.class
)
public class ImportPanelApp extends BasePanelApp {

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return ExportImportPortletKeys.IMPORT;
	}

	@Override
	public PortletURL getPortletURL(HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Group group = themeDisplay.getSiteGroup();

		PortletURL portletURL = super.getPortletURL(httpServletRequest);

		if ((!group.hasPublicLayouts() && group.hasPrivateLayouts()) ||
			group.isLayoutSetPrototype()) {

			portletURL.setParameter("privateLayout", Boolean.TRUE.toString());
		}
		else {
			portletURL.setParameter("privateLayout", Boolean.FALSE.toString());
		}

		return portletURL;
	}

	@Reference(
		target = "(jakarta.portlet.name=" + ExportImportPortletKeys.IMPORT + ")"
	)
	private Portlet _portlet;

}