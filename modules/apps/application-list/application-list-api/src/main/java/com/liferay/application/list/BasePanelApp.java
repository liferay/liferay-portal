/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.application.list;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.portlet.ControlPanelEntry;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletCategoryKeys;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Provides a skeletal implementation of the {@link PanelApp} to minimize the
 * effort required to implement this interface.
 *
 * <p>
 * To implement an application, this class should be extended and {@link
 * #include(HttpServletRequest, HttpServletResponse)} should be overridden. The
 * <code>include</code> override method should return <code>true</code> when the
 * application view successfully renders and <code>false</code> otherwise.
 * </p>
 *
 * @author Adolfo Pérez
 * @see    PanelApp
 */
public abstract class BasePanelApp implements PanelApp {

	@Override
	public String getIcon() {
		return StringPool.BLANK;
	}

	@Override
	public String getKey() {
		Class<?> clazz = getClass();

		return clazz.getName();
	}

	@Override
	public String getLabel(Locale locale) {
		try {
			ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
				locale, getClass());

			return LanguageUtil.get(
				resourceBundle,
				JavaConstants.JAKARTA_PORTLET_TITLE + StringPool.PERIOD +
					getPortletId());
		}
		catch (MissingResourceException missingResourceException) {
			if (_log.isDebugEnabled()) {
				_log.debug(missingResourceException);
			}
		}

		return LanguageUtil.get(
			locale,
			JavaConstants.JAKARTA_PORTLET_TITLE + StringPool.PERIOD +
				getPortletId());
	}

	@Override
	public int getNotificationsCount(User user) {
		if (_userNotificationEventLocalService == null) {
			return 0;
		}

		Portlet portlet = getPortlet();

		return _userNotificationEventLocalService.
			getUserNotificationEventsCount(
				user.getUserId(), portlet.getPortletId(),
				UserNotificationDeliveryConstants.TYPE_WEBSITE, true, false);
	}

	@Override
	public PortletURL getPortletURL(HttpServletRequest httpServletRequest)
		throws PortalException {

		PortletURL portletURL = PortalUtil.getControlPanelPortletURL(
			httpServletRequest, getGroup(httpServletRequest), getPortletId(), 0,
			0, PortletRequest.RENDER_PHASE);

		Group group = groupProvider.getGroup(httpServletRequest);

		if (group == null) {
			return portletURL;
		}

		portletURL.setParameter(
			"p_v_l_s_g_id", String.valueOf(group.getGroupId()));

		return portletURL;
	}

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		return false;
	}

	@Override
	public boolean isShow(PermissionChecker permissionChecker, Group group)
		throws PortalException {

		Portlet portlet = _portletLocalService.getPortletById(
			group.getCompanyId(), getPortletId());

		if (!portlet.isActive()) {
			return false;
		}

		try {
			ControlPanelEntry controlPanelEntry = getControlPanelEntry();

			if (controlPanelEntry == null) {
				return true;
			}

			return controlPanelEntry.hasAccessPermission(
				permissionChecker, group, getPortlet());
		}
		catch (PortalException | RuntimeException exception) {
			throw exception;
		}
		catch (Exception exception) {
			throw new PortalException(exception);
		}
	}

	@Override
	public void setGroupProvider(GroupProvider groupProvider) {
		this.groupProvider = groupProvider;
	}

	public void setPortletLocalService(
		PortletLocalService portletLocalService) {

		_portletLocalService = portletLocalService;
	}

	protected ControlPanelEntry getControlPanelEntry() {
		Portlet portlet = getPortlet();

		if (portlet == null) {
			return null;
		}

		return portlet.getControlPanelEntryInstance();
	}

	protected Group getGroup(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Group group = themeDisplay.getScopeGroup();

		if (!group.isControlPanel()) {
			return null;
		}

		Portlet portlet = getPortlet();

		String controlPanelEntryCategory =
			portlet.getControlPanelEntryCategory();

		if (Validator.isNull(controlPanelEntryCategory) ||
			!controlPanelEntryCategory.startsWith(
				PortletCategoryKeys.SITE_ADMINISTRATION) ||
			(groupProvider == null)) {

			return null;
		}

		return groupProvider.getGroup(httpServletRequest);
	}

	protected void setUserNotificationEventLocalService(
		UserNotificationEventLocalService userNotificationEventLocalService) {

		_userNotificationEventLocalService = userNotificationEventLocalService;
	}

	protected GroupProvider groupProvider;

	private static final Log _log = LogFactoryUtil.getLog(BasePanelApp.class);

	private PortletLocalService _portletLocalService;
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}