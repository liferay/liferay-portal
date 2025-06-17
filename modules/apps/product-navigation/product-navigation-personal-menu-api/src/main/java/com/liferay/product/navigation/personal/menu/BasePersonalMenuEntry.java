/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.personal.menu;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.product.navigation.personal.menu.util.PersonalApplicationURLUtil;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Provides a skeletal implementation of the {@link PersonalMenuEntry} to
 * minimize the effort required to implement this interface. To implement a user
 * personal menu entry, this class should be extended and {@link
 * #getPortletId()} should be overridden.
 *
 * @author Pei-Jung Lan
 */
public abstract class BasePersonalMenuEntry implements PersonalMenuEntry {

	@Override
	public String getLabel(Locale locale) {
		return LanguageUtil.get(
			getResourceBundle(locale),
			JavaConstants.JAKARTA_PORTLET_TITLE + StringPool.PERIOD +
				getPortletId());
	}

	/**
	 * Returns the portlet's ID associated with the user personal menu entry.
	 *
	 * @return the portlet's ID associated with the user personal menu entry
	 */
	public abstract String getPortletId();

	@Override
	public String getPortletURL(HttpServletRequest httpServletRequest)
		throws PortalException {

		if (Validator.isNull(getPortletId())) {
			return null;
		}

		return PersonalApplicationURLUtil.getPersonalApplicationURL(
			httpServletRequest, getPortletId());
	}

	@Override
	public boolean isActive(PortletRequest portletRequest, String portletId) {
		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		String layoutFriendlyURL = layout.getFriendlyURL();

		if ((!layout.isTypeControlPanel() && !layout.isSystem()) ||
			!layoutFriendlyURL.equals(
				PropsUtil.get(PropsKeys.CONTROL_PANEL_LAYOUT_FRIENDLY_URL))) {

			return false;
		}

		return portletId.equals(getPortletId());
	}

	@Override
	public boolean isShow(
			PortletRequest portletRequest, PermissionChecker permissionChecker)
		throws PortalException {

		try {
			return hasAccessPermission(
				permissionChecker,
				PortletLocalServiceUtil.getPortletById(getPortletId()));
		}
		catch (PortalException | RuntimeException exception) {
			throw exception;
		}
		catch (Exception exception) {
			throw new PortalException(exception);
		}
	}

	protected ResourceBundle getResourceBundle(Locale locale) {
		return ResourceBundleUtil.getBundle(locale, getClass());
	}

	protected boolean hasAccessPermission(
			PermissionChecker permissionChecker, Portlet portlet)
		throws Exception {

		List<String> actions = ResourceActionsUtil.getResourceActions(
			portlet.getPortletId());

		if (actions.contains(ActionKeys.ACCESS_IN_CONTROL_PANEL) &&
			PortletPermissionUtil.contains(
				permissionChecker, 0, portlet.getRootPortletId(),
				ActionKeys.ACCESS_IN_CONTROL_PANEL, true)) {

			return true;
		}

		return false;
	}

}