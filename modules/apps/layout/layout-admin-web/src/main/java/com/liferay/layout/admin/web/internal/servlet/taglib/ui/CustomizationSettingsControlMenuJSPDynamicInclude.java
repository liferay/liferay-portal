/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.servlet.taglib.ui;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.servlet.taglib.BaseJSPDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Chema Balsas
 */
@Component(service = DynamicInclude.class)
public class CustomizationSettingsControlMenuJSPDynamicInclude
	extends BaseJSPDynamicInclude {

	public static final String CUSTOMIZATION_SETTINGS_LAYOUT_UPDATE_PERMISSION =
		"CUSTOMIZATION_SETTINGS_LAYOUT_UPDATE_PERMISSION";

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	public boolean hasUpdateLayoutPermission(ThemeDisplay themeDisplay)
		throws PortalException {

		return LayoutPermissionUtil.contains(
			themeDisplay.getPermissionChecker(), themeDisplay.getLayout(),
			ActionKeys.UPDATE);
	}

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		try {
			if (!isShow(httpServletRequest)) {
				return;
			}

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			httpServletRequest.setAttribute(
				CUSTOMIZATION_SETTINGS_LAYOUT_UPDATE_PERMISSION,
				hasUpdateLayoutPermission(themeDisplay));
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		super.include(httpServletRequest, httpServletResponse, key);
	}

	public boolean isShow(HttpServletRequest httpServletRequest)
		throws PortalException {

		Boolean show = (Boolean)httpServletRequest.getAttribute(_SHOW);

		if (show != null) {
			return show;
		}

		show = _isShow(httpServletRequest);

		httpServletRequest.setAttribute(_SHOW, show);

		return show;
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(
			"com.liferay.product.navigation.taglib#/page.jsp#post");
	}

	@Override
	protected String getJspPath() {
		return "/dynamic_include/customization_settings.jsp";
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	private boolean _isCustomizableLayout(ThemeDisplay themeDisplay)
		throws PortalException {

		Layout layout = themeDisplay.getLayout();

		Group group = layout.getGroup();

		if (group.isLayoutPrototype() || group.isLayoutSetPrototype() ||
			group.isStagingGroup() || group.isUserGroup()) {

			return false;
		}

		LayoutTypePortlet layoutTypePortlet =
			themeDisplay.getLayoutTypePortlet();

		if (!layout.isTypePortlet() || (layoutTypePortlet == null)) {
			return false;
		}

		if (layout.isCustomizable() &&
			hasUpdateLayoutPermission(themeDisplay)) {

			return true;
		}

		if (!layoutTypePortlet.isCustomizable() ||
			!LayoutPermissionUtil.containsWithoutViewableGroup(
				themeDisplay.getPermissionChecker(), layout, false,
				ActionKeys.CUSTOMIZE)) {

			return false;
		}

		return true;
	}

	private boolean _isShow(HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		if (layout.isTypeControlPanel() || layout.isTypeContent() ||
			!_isCustomizableLayout(themeDisplay)) {

			return false;
		}

		return true;
	}

	private static final String _SHOW =
		CustomizationSettingsControlMenuJSPDynamicInclude.class + "#_SHOW";

	private static final Log _log = LogFactoryUtil.getLog(
		CustomizationSettingsControlMenuJSPDynamicInclude.class);

	@Reference(target = "(osgi.web.symbolicname=com.liferay.layout.admin.web)")
	private ServletContext _servletContext;

}