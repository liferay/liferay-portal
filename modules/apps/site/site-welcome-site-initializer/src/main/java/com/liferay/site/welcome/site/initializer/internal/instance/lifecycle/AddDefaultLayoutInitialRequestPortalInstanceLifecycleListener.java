/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.welcome.site.initializer.internal.instance.lifecycle;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.instance.lifecycle.InitialRequestPortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.InvokerPortlet;
import com.liferay.portal.kernel.portlet.LiferayRenderRequest;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletInstanceFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.ThemeLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.DummyHttpServletResponse;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ColorSchemeFactoryUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PrefsProps;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.RenderRequestFactory;
import com.liferay.portlet.RenderResponseFactory;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.SiteInitializerRegistry;

import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class AddDefaultLayoutInitialRequestPortalInstanceLifecycleListener
	extends InitialRequestPortalInstanceLifecycleListener {

	@Activate
	@Override
	protected void activate(BundleContext bundleContext) {
		super.activate(bundleContext);
	}

	@Override
	protected void doPortalInstanceRegistered(long companyId) throws Exception {
		Group group = _groupLocalService.getGroup(
			companyId, GroupConstants.GUEST);

		String friendlyURL = _friendlyURLNormalizer.normalizeWithEncoding(
			PropsValues.DEFAULT_GUEST_PUBLIC_LAYOUT_FRIENDLY_URL);

		Layout defaultLayout = _layoutLocalService.fetchLayoutByFriendlyURL(
			group.getGroupId(), false, friendlyURL);

		if (defaultLayout != null) {
			return;
		}

		defaultLayout = _layoutLocalService.fetchFirstLayout(
			group.getGroupId(), false, LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			false);

		if (defaultLayout != null) {
			return;
		}

		String name = PrincipalThreadLocal.getName();

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		ServiceContext currentThreadServiceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (currentThreadServiceContext == null) {
			currentThreadServiceContext = new ServiceContext();
		}

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.
					setInitializingPortalInstanceWithSafeCloseable(true)) {

			User user = _getUser(companyId);

			PrincipalThreadLocal.setName(user.getUserId());

			PermissionThreadLocal.setPermissionChecker(
				_defaultPermissionCheckerFactory.create(user));

			ServiceContextThreadLocal.pushServiceContext(
				_populateServiceContext(
					_companyLocalService.getCompanyById(companyId), group,
					currentThreadServiceContext.getRequest(), permissionChecker,
					(ServiceContext)currentThreadServiceContext.clone(), user));

			UnicodeProperties typeSettingsUnicodeProperties =
				group.getTypeSettingsProperties();

			String siteInitializerKey =
				typeSettingsUnicodeProperties.getProperty("siteInitializerKey");

			if (Validator.isNull(siteInitializerKey)) {
				siteInitializerKey = _SITE_INITIALIZER_KEY_WELCOME;
			}

			if (!Objects.equals(
					siteInitializerKey, _SITE_INITIALIZER_KEY_WELCOME) &&
				!Objects.equals(
					siteInitializerKey, _SITE_INITIALIZER_KEY_BLANK)) {

				_layoutLocalService.deleteLayouts(
					group.getGroupId(), false, new ServiceContext());
			}

			SiteInitializer siteInitializer =
				_siteInitializerRegistry.getSiteInitializer(siteInitializerKey);

			siteInitializer.initialize(group.getGroupId());
		}
		finally {
			PrincipalThreadLocal.setName(name);

			PermissionThreadLocal.setPermissionChecker(permissionChecker);

			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private User _getUser(long companyId) throws PortalException {
		Role role = _roleLocalService.fetchRole(
			companyId, RoleConstants.ADMINISTRATOR);

		if (role == null) {
			return _userLocalService.getGuestUser(companyId);
		}

		List<User> adminUsers = _userLocalService.getRoleUsers(
			role.getRoleId(), 0, 1);

		if (adminUsers.isEmpty()) {
			return _userLocalService.getGuestUser(companyId);
		}

		return adminUsers.get(0);
	}

	private ServiceContext _populateServiceContext(
			Company company, Group group, HttpServletRequest httpServletRequest,
			PermissionChecker permissionChecker, ServiceContext serviceContext,
			User user)
		throws PortalException {

		serviceContext.setCompanyId(user.getCompanyId());
		serviceContext.setRequest(httpServletRequest);
		serviceContext.setScopeGroupId(group.getGroupId());
		serviceContext.setUserId(user.getUserId());

		if (httpServletRequest == null) {
			return serviceContext;
		}

		long controlPanelPlid = _portal.getControlPanelPlid(
			company.getCompanyId());

		Layout controlPanelLayout = _layoutLocalService.getLayout(
			controlPanelPlid);

		httpServletRequest.setAttribute(WebKeys.LAYOUT, controlPanelLayout);

		ThemeDisplay currentThemeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		ThemeDisplay themeDisplay = null;

		if (currentThemeDisplay != null) {
			try {
				themeDisplay = (ThemeDisplay)currentThemeDisplay.clone();
			}
			catch (CloneNotSupportedException cloneNotSupportedException) {
				_log.error(cloneNotSupportedException);
			}
		}
		else {
			themeDisplay = new ThemeDisplay();
		}

		themeDisplay.setCompany(company);
		themeDisplay.setLayout(controlPanelLayout);
		themeDisplay.setLayoutSet(controlPanelLayout.getLayoutSet());
		themeDisplay.setLayoutTypePortlet(
			(LayoutTypePortlet)controlPanelLayout.getLayoutType());
		themeDisplay.setLayouts(ListUtil.fromArray(controlPanelLayout));
		themeDisplay.setLocale(LocaleUtil.getSiteDefault());

		String themeId = _prefsProps.getString(
			company.getCompanyId(),
			PropsKeys.CONTROL_PANEL_LAYOUT_REGULAR_THEME_ID);

		themeDisplay.setLookAndFeel(
			_themeLocalService.getTheme(company.getCompanyId(), themeId),
			ColorSchemeFactoryUtil.getDefaultRegularColorScheme());

		themeDisplay.setPermissionChecker(permissionChecker);
		themeDisplay.setPlid(controlPanelPlid);
		themeDisplay.setRealUser(user);
		themeDisplay.setRequest(httpServletRequest);
		themeDisplay.setScopeGroupId(controlPanelLayout.getGroupId());
		themeDisplay.setSiteGroupId(controlPanelLayout.getGroupId());
		themeDisplay.setUser(user);

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);

		if (portletRequest != null) {
			return serviceContext;
		}

		Portlet portlet = _portletLocalService.getPortletById(
			CompanyConstants.SYSTEM, PortletKeys.PORTAL);

		try {
			InvokerPortlet invokerPortlet = PortletInstanceFactoryUtil.create(
				portlet, httpServletRequest.getServletContext());

			PortletConfig portletConfig = PortletConfigFactoryUtil.create(
				portlet, httpServletRequest.getServletContext());

			LiferayRenderRequest liferayRenderRequest =
				RenderRequestFactory.create(
					httpServletRequest, portlet, invokerPortlet,
					portletConfig.getPortletContext(), WindowState.NORMAL,
					PortletMode.VIEW,
					PortletPreferencesFactoryUtil.fromDefaultXML(
						portlet.getDefaultPreferences()),
					themeDisplay.getPlid());

			httpServletRequest.setAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST, liferayRenderRequest);
			httpServletRequest.setAttribute(
				JavaConstants.JAVAX_PORTLET_RESPONSE,
				RenderResponseFactory.create(
					new DummyHttpServletResponse(), liferayRenderRequest));
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return serviceContext;
	}

	private static final String _SITE_INITIALIZER_KEY_BLANK =
		"blank-site-initializer";

	private static final String _SITE_INITIALIZER_KEY_WELCOME =
		"com.liferay.site.initializer.welcome";

	private static final Log _log = LogFactoryUtil.getLog(
		AddDefaultLayoutInitialRequestPortalInstanceLifecycleListener.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private PermissionCheckerFactory _defaultPermissionCheckerFactory;

	@Reference
	private FriendlyURLNormalizer _friendlyURLNormalizer;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference
	private PrefsProps _prefsProps;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private SiteInitializerRegistry _siteInitializerRegistry;

	@Reference
	private ThemeLocalService _themeLocalService;

	@Reference
	private UserLocalService _userLocalService;

}