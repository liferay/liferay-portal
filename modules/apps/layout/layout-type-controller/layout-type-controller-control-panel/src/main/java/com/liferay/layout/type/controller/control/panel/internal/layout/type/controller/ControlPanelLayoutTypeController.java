/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.type.controller.control.panel.internal.layout.type.controller;

import com.liferay.application.list.PanelAppRegistry;
import com.liferay.application.list.constants.ApplicationListWebKeys;
import com.liferay.layout.type.controller.BaseLayoutTypeControllerImpl;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypeController;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.servlet.PipingServletResponse;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "layout.type=" + LayoutConstants.TYPE_CONTROL_PANEL,
	service = LayoutTypeController.class
)
public class ControlPanelLayoutTypeController
	extends BaseLayoutTypeControllerImpl {

	@Override
	public String getURL() {
		return _URL;
	}

	@Override
	public boolean isCheckLayoutViewPermission() {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		return !permissionChecker.isSignedIn();
	}

	@Override
	public boolean isFirstPageable() {
		return true;
	}

	@Override
	public boolean isInstanceable() {
		return false;
	}

	@Override
	public boolean isParentable() {
		return true;
	}

	@Override
	public boolean isSitemapable() {
		return true;
	}

	@Override
	public boolean isURLFriendliable() {
		return true;
	}

	@Override
	protected void addAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			ApplicationListWebKeys.PANEL_APP_REGISTRY, _panelAppRegistry);
	}

	@Override
	protected ServletResponse createServletResponse(
		HttpServletResponse httpServletResponse,
		UnsyncStringWriter unsyncStringWriter) {

		return new PipingServletResponse(
			httpServletResponse, unsyncStringWriter);
	}

	@Override
	protected String getEditPage() {
		return _EDIT_PAGE;
	}

	@Override
	protected ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	protected String getViewPage() {
		return _VIEW_PAGE;
	}

	@Override
	protected void removeAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.removeAttribute(
			ApplicationListWebKeys.PANEL_APP_REGISTRY);
	}

	private static final String _EDIT_PAGE = "/layout/edit/control_panel.jsp";

	private static final String _URL =
		"${liferay:mainPath}/portal/layout?p_l_id=${liferay:plid}" +
			"&p_v_l_s_g_id=${liferay:pvlsgid}";

	private static final String _VIEW_PAGE = "/layout/view/control_panel.jsp";

	@Reference
	private PanelAppRegistry _panelAppRegistry;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.layout.type.controller.control.panel)"
	)
	private ServletContext _servletContext;

}