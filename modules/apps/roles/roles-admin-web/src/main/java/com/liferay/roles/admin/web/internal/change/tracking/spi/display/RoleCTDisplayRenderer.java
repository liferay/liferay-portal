/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.admin.web.internal.change.tracking.spi.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.UserPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.roles.admin.constants.RolesAdminPortletKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Noor Najjar
 */
@Component(service = CTDisplayRenderer.class)
public class RoleCTDisplayRenderer extends BaseCTDisplayRenderer<Role> {

	@Override
	public String getEditURL(HttpServletRequest httpServletRequest, Role role) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!UserPermissionUtil.contains(
				themeDisplay.getPermissionChecker(), themeDisplay.getUserId(),
				ActionKeys.UPDATE)) {

			return null;
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, RolesAdminPortletKeys.ROLES_ADMIN,
				PortletRequest.RENDER_PHASE)
		).setMVCPath(
			"/edit_role.jsp"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setParameter(
			"roleId", role.getRoleId()
		).buildString();
	}

	@Override
	public Class<Role> getModelClass() {
		return Role.class;
	}

	@Override
	public String getTitle(Locale locale, Role role) {
		return role.getTitle(locale);
	}

	@Override
	protected void buildDisplay(DisplayBuilder<Role> displayBuilder) {
		Role role = displayBuilder.getModel();

		displayBuilder.display(
			"title", role.getTitle(displayBuilder.getLocale())
		).display(
			"name", role.getName()
		).display(
			"type", role.getTypeLabel()
		).display(
			"description", role.getDescription(displayBuilder.getLocale())
		);
	}

	@Reference
	private Portal _portal;

}