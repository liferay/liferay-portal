/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypeAccessPolicy;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.PortletModeFactory;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletMode;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Adolfo Pérez
 */
public class DefaultLayoutTypeAccessPolicyImpl
	implements LayoutTypeAccessPolicy {

	public static LayoutTypeAccessPolicy create() {
		return _layoutTypeAccessPolicy;
	}

	@Override
	public void checkAccessAllowedToPortlet(
			HttpServletRequest httpServletRequest, Layout layout,
			Portlet portlet)
		throws PortalException {

		String checkAccessAllowedToPortletCacheKey = StringBundler.concat(
			"LIFERAY_SHARED_",
			DefaultLayoutTypeAccessPolicyImpl.class.getName(), "#",
			layout.getPlid(), "#", portlet.getPortletId());

		Boolean allowed = (Boolean)httpServletRequest.getAttribute(
			checkAccessAllowedToPortletCacheKey);

		if (allowed != null) {
			if (allowed) {
				return;
			}

			throw new PrincipalException.MustHavePermission(
				PortalUtil.getUserId(httpServletRequest),
				StringBundler.concat(
					portlet.getDisplayName(), StringPool.SPACE,
					portlet.getPortletId()),
				0, ActionKeys.ACCESS);
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String layoutFriendlyURL = layout.getFriendlyURL();

		if (layout.isSystem() &&
			layoutFriendlyURL.equals(
				PropsUtil.get(PropsKeys.CONTROL_PANEL_LAYOUT_FRIENDLY_URL)) &&
			PortletPermissionUtil.hasControlPanelAccessPermission(
				PermissionThreadLocal.getPermissionChecker(),
				themeDisplay.getScopeGroupId(), portlet)) {

			httpServletRequest.setAttribute(
				checkAccessAllowedToPortletCacheKey, Boolean.TRUE);

			return;
		}

		if (isAccessAllowedToLayoutPortlet(
				httpServletRequest, layout, portlet)) {

			PortalUtil.addPortletDefaultResource(httpServletRequest, portlet);

			if (hasAccessPermission(httpServletRequest, layout, portlet)) {
				httpServletRequest.setAttribute(
					checkAccessAllowedToPortletCacheKey, Boolean.TRUE);

				return;
			}
		}

		httpServletRequest.setAttribute(
			checkAccessAllowedToPortletCacheKey, Boolean.FALSE);

		throw new PrincipalException.MustHavePermission(
			PortalUtil.getUserId(httpServletRequest),
			StringBundler.concat(
				portlet.getDisplayName(), StringPool.SPACE,
				portlet.getPortletId()),
			0, ActionKeys.ACCESS);
	}

	@Override
	public boolean isAddLayoutAllowed(
			PermissionChecker permissionChecker, Layout layout)
		throws PortalException {

		return LayoutPermissionUtil.contains(
			permissionChecker, layout, ActionKeys.ADD_LAYOUT);
	}

	@Override
	public boolean isCustomizeLayoutAllowed(
			PermissionChecker permissionChecker, Layout layout)
		throws PortalException {

		return LayoutPermissionUtil.contains(
			permissionChecker, layout, ActionKeys.CUSTOMIZE);
	}

	@Override
	public boolean isDeleteLayoutAllowed(
			PermissionChecker permissionChecker, Layout layout)
		throws PortalException {

		return LayoutPermissionUtil.contains(
			permissionChecker, layout, ActionKeys.DELETE);
	}

	@Override
	public boolean isUpdateLayoutAllowed(
			PermissionChecker permissionChecker, Layout layout)
		throws PortalException {

		return LayoutPermissionUtil.contains(
			permissionChecker, layout, ActionKeys.UPDATE);
	}

	@Override
	public boolean isViewLayoutAllowed(
			PermissionChecker permissionChecker, Layout layout)
		throws PortalException {

		return LayoutPermissionUtil.contains(
			permissionChecker, layout, ActionKeys.VIEW);
	}

	protected boolean hasAccessPermission(
			HttpServletRequest httpServletRequest, Layout layout,
			Portlet portlet)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletMode portletMode = PortletMode.VIEW;

		String portletId = portlet.getPortletId();
		String ppid = httpServletRequest.getParameter("p_p_id");
		String ppmode = httpServletRequest.getParameter("p_p_mode");

		if (portletId.equals(ppid) && (ppmode != null)) {
			portletMode = PortletModeFactory.getPortletMode(ppmode);
		}

		return PortletPermissionUtil.hasAccessPermission(
			PermissionThreadLocal.getPermissionChecker(),
			themeDisplay.getScopeGroupId(), layout, portlet, portletMode);
	}

	protected boolean isAccessAllowedToLayoutPortlet(
		HttpServletRequest httpServletRequest, Layout layout, Portlet portlet) {

		if (isAccessGrantedByRuntimePortlet(httpServletRequest) ||
			isAccessGrantedByPortletOnPage(layout, portlet) ||
			isAccessGrantedByPortletAuthenticationToken(
				httpServletRequest, layout, portlet)) {

			return true;
		}

		return false;
	}

	protected boolean isAccessGrantedByPortletAuthenticationToken(
		HttpServletRequest httpServletRequest, Layout layout, Portlet portlet) {

		if (!portlet.isAddDefaultResource()) {
			return false;
		}

		if (!_PORTLET_ADD_DEFAULT_RESOURCE_CHECK_ENABLED ||
			AuthTokenUtil.isValidPortletInvocationToken(
				httpServletRequest, layout, portlet)) {

			return true;
		}

		return false;
	}

	protected boolean isAccessGrantedByPortletOnPage(
		Layout layout, Portlet portlet) {

		String portletId = portlet.getPortletId();

		if (layout.isTypePanel() && isPanelSelectedPortlet(layout, portletId)) {
			return true;
		}

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		if ((layoutTypePortlet != null) &&
			layoutTypePortlet.hasPortletId(portletId)) {

			return true;
		}

		return false;
	}

	protected boolean isAccessGrantedByRuntimePortlet(
		HttpServletRequest httpServletRequest) {

		Boolean renderPortletResource =
			(Boolean)httpServletRequest.getAttribute(
				WebKeys.RENDER_PORTLET_RESOURCE);

		if (renderPortletResource != null) {
			return renderPortletResource;
		}

		return false;
	}

	protected boolean isPanelSelectedPortlet(Layout layout, String portletId) {
		String panelSelectedPortlets = layout.getTypeSettingsProperty(
			"panelSelectedPortlets");

		if (Validator.isNotNull(panelSelectedPortlets)) {
			String[] panelSelectedPortletsArray = StringUtil.split(
				panelSelectedPortlets);

			return ArrayUtil.contains(panelSelectedPortletsArray, portletId);
		}

		return false;
	}

	private static final boolean _PORTLET_ADD_DEFAULT_RESOURCE_CHECK_ENABLED =
		GetterUtil.getBoolean(
			PropsUtil.get(
				PropsKeys.PORTLET_ADD_DEFAULT_RESOURCE_CHECK_ENABLED));

	private static final LayoutTypeAccessPolicy _layoutTypeAccessPolicy =
		new DefaultLayoutTypeAccessPolicyImpl();

}