/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.personal.menu.util;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.impl.VirtualLayout;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;
import com.liferay.product.navigation.personal.menu.configuration.PersonalMenuConfiguration;
import com.liferay.product.navigation.personal.menu.configuration.PersonalMenuConfigurationRegistry;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Samuel Trong Tran
 */
public class PersonalApplicationURLUtil {

	public static Layout getOrAddEmbeddedPersonalApplicationLayout(
			User user, Group group, boolean privateLayout)
		throws PortalException {

		try {
			return LayoutLocalServiceUtil.getFriendlyURLLayout(
				group.getGroupId(), privateLayout,
				PropsValues.CONTROL_PANEL_LAYOUT_FRIENDLY_URL);
		}
		catch (NoSuchLayoutException noSuchLayoutException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(noSuchLayoutException);
			}

			return _addEmbeddedPersonalApplicationLayout(
				user.getUserId(), group, privateLayout);
		}
	}

	public static String getPersonalApplicationURL(
			HttpServletRequest httpServletRequest, String portletId)
		throws PortalException {

		User user = PortalUtil.getUser(httpServletRequest);

		Group group = user.getGroup();

		boolean controlPanelLayout = false;
		boolean privateLayout = true;

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PersonalMenuConfiguration personalMenuConfiguration =
			_getPersonalMenuConfigurationTracker().
				getCompanyPersonalMenuConfiguration(
					themeDisplay.getCompanyId());

		String personalApplicationsLookAndFeel =
			personalMenuConfiguration.personalApplicationsLookAndFeel();

		if (personalApplicationsLookAndFeel.equals("current-site")) {
			group = GroupLocalServiceUtil.getGroup(
				PortalUtil.getScopeGroupId(httpServletRequest));

			if (group.isStagingGroup()) {
				group = group.getLiveGroup();
			}

			Layout currentLayout = themeDisplay.getLayout();

			if (currentLayout.isTypeControlPanel()) {
				controlPanelLayout = true;
			}

			if (currentLayout.isPublicLayout()) {
				privateLayout = false;
			}

			user = UserLocalServiceUtil.getGuestUser(
				themeDisplay.getCompanyId());
		}

		Layout layout = getOrAddEmbeddedPersonalApplicationLayout(
			user, group, privateLayout);

		if ((controlPanelLayout && !group.isControlPanel()) ||
			!LayoutPermissionUtil.contains(
				themeDisplay.getPermissionChecker(), layout, true,
				ActionKeys.VIEW)) {

			Group controlPanelGroup = themeDisplay.getControlPanelGroup();

			layout = new VirtualLayout(
				LayoutLocalServiceUtil.getFriendlyURLLayout(
					controlPanelGroup.getGroupId(), privateLayout,
					PropsValues.CONTROL_PANEL_LAYOUT_FRIENDLY_URL),
				themeDisplay.getScopeGroup());
		}

		LiferayPortletURL liferayPortletURL = PortletURLFactoryUtil.create(
			httpServletRequest, portletId, layout, PortletRequest.RENDER_PHASE);

		String backURL = ParamUtil.getString(httpServletRequest, "currentURL");

		liferayPortletURL.setParameter("backURL", backURL);

		return liferayPortletURL.toString();
	}

	private static Layout _addEmbeddedPersonalApplicationLayout(
			long userId, Group group, boolean privateLayout)
		throws PortalException {

		String friendlyURL = FriendlyURLNormalizerUtil.normalize(
			PropsValues.CONTROL_PANEL_LAYOUT_FRIENDLY_URL);

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAttribute(
			"layout.instanceable.allowed", Boolean.TRUE);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					group.getCtCollectionId())) {

			Layout layout = LayoutLocalServiceUtil.addLayout(
				null, userId, group.getGroupId(), privateLayout,
				LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
				PropsValues.CONTROL_PANEL_LAYOUT_NAME, StringPool.BLANK,
				StringPool.BLANK, LayoutConstants.TYPE_PORTLET, true, true,
				friendlyURL, serviceContext);

			LayoutTypePortlet layoutTypePortlet =
				(LayoutTypePortlet)layout.getLayoutType();

			layoutTypePortlet.setLayoutTemplateId(
				userId, "1_column_dynamic", false);

			return LayoutLocalServiceUtil.updateLayout(
				layout.getGroupId(), layout.isPrivateLayout(),
				layout.getLayoutId(), layout.getTypeSettings());
		}
	}

	private static PersonalMenuConfigurationRegistry
		_getPersonalMenuConfigurationTracker() {

		return _serviceTracker.getService();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PersonalApplicationURLUtil.class);

	private static final ServiceTracker
		<PersonalMenuConfigurationRegistry, PersonalMenuConfigurationRegistry>
			_serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			PersonalMenuConfigurationRegistry.class);

		ServiceTracker
			<PersonalMenuConfigurationRegistry,
			 PersonalMenuConfigurationRegistry> serviceTracker =
				new ServiceTracker<>(
					bundle.getBundleContext(),
					PersonalMenuConfigurationRegistry.class, null);

		serviceTracker.open();

		_serviceTracker = serviceTracker;
	}

}