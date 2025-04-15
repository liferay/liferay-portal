/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.product.navigation.control.menu;

import com.liferay.frontend.taglib.clay.servlet.taglib.IconTag;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.SessionClicks;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;
import com.liferay.product.navigation.control.menu.BaseProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.constants.ProductNavigationControlMenuCategoryKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;

import java.io.IOException;
import java.io.Writer;

import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Julio Camarero
 */
@Component(
	property = {
		"product.navigation.control.menu.category.key=" + ProductNavigationControlMenuCategoryKeys.USER,
		"product.navigation.control.menu.entry.order:Integer=110"
	},
	service = ProductNavigationControlMenuEntry.class
)
public class ToggleControlsProductNavigationControlMenuEntry
	extends BaseProductNavigationControlMenuEntry {

	@Override
	public String getLabel(Locale locale) {
		return null;
	}

	@Override
	public String getURL(HttpServletRequest httpServletRequest) {
		return null;
	}

	@Override
	public boolean includeIcon(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		String toggleControls = GetterUtil.getString(
			SessionClicks.get(
				httpServletRequest,
				"com.liferay.frontend.js.web_toggleControls", "visible"));

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			_portal.getLocale(httpServletRequest), getClass());

		IconTag iconTag = new IconTag();

		iconTag.setCssClass("icon-monospaced");

		if (Objects.equals(toggleControls, "visible")) {
			iconTag.setSymbol("view");
		}
		else {
			iconTag.setSymbol("hidden");
		}

		try {
			Writer writer = httpServletResponse.getWriter();

			writer.write(
				StringUtil.replace(
					_ICON_TMPL_CONTENT, "${", "}",
					HashMapBuilder.put(
						"iconTag",
						iconTag.doTagAsString(
							httpServletRequest, httpServletResponse)
					).put(
						"title",
						_language.get(resourceBundle, "toggle-controls")
					).build()));
		}
		catch (JspException jspException) {
			throw new IOException(jspException);
		}

		return true;
	}

	@Override
	public boolean isShow(HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		if (layout.isTypeAssetDisplay() || layout.isTypeContent() ||
			layout.isTypeControlPanel()) {

			return false;
		}

		Group group = layout.getGroup();

		if (group.hasStagingGroup() && !group.isStagingGroup() &&
			PropsValues.STAGING_LIVE_GROUP_LOCKING_ENABLED) {

			return false;
		}

		if (!(hasUpdateLayoutPermission(themeDisplay) ||
			  _hasCustomizePermission(themeDisplay) ||
			  _hasPortletConfigurationPermission(themeDisplay))) {

			return false;
		}

		return super.isShow(httpServletRequest);
	}

	protected boolean hasUpdateLayoutPermission(ThemeDisplay themeDisplay)
		throws PortalException {

		return LayoutPermissionUtil.contains(
			themeDisplay.getPermissionChecker(), themeDisplay.getLayout(),
			ActionKeys.UPDATE);
	}

	private boolean _hasCustomizePermission(ThemeDisplay themeDisplay)
		throws PortalException {

		Layout layout = themeDisplay.getLayout();
		LayoutTypePortlet layoutTypePortlet =
			themeDisplay.getLayoutTypePortlet();

		if (!layout.isTypePortlet() || (layoutTypePortlet == null) ||
			!layoutTypePortlet.isCustomizable() ||
			!layoutTypePortlet.isCustomizedView()) {

			return false;
		}

		return LayoutPermissionUtil.contains(
			themeDisplay.getPermissionChecker(), layout, ActionKeys.CUSTOMIZE);
	}

	private boolean _hasPortletConfigurationPermission(
			ThemeDisplay themeDisplay)
		throws PortalException {

		return PortletPermissionUtil.hasConfigurationPermission(
			themeDisplay.getPermissionChecker(), themeDisplay.getSiteGroupId(),
			themeDisplay.getLayout(), ActionKeys.CONFIGURATION);
	}

	private static final String _ICON_TMPL_CONTENT = StringUtil.read(
		ToggleControlsProductNavigationControlMenuEntry.class, "icon.tmpl");

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}