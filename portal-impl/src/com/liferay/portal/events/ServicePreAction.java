/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.events;

import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalServiceUtil;
import com.liferay.exportimport.kernel.service.ExportImportLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.image.ImageToolUtil;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.exception.LayoutPermissionException;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.frontend.spa.FrontendSPAUtil;
import com.liferay.portal.kernel.interval.IntervalActionProcessor;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.login.AuthLoginGroupSettingsUtil;
import com.liferay.portal.kernel.model.ColorScheme;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutTemplate;
import com.liferay.portal.kernel.model.LayoutTypeAccessPolicy;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.LayoutTypePortletConstants;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.impl.VirtualLayout;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ImageLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.ThemeLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.service.permission.PortalPermissionUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.servlet.PortalWebResourceConstants;
import com.liferay.portal.kernel.servlet.PortalWebResourcesUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ColorSchemeFactoryUtil;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.SessionParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.webserver.WebServerServletTokenUtil;
import com.liferay.portal.theme.ThemeDisplayFactory;
import com.liferay.portal.util.LayoutClone;
import com.liferay.portal.util.LayoutCloneFactory;
import com.liferay.portal.util.LayoutTypeAccessPolicyTracker;
import com.liferay.sites.kernel.util.SitesUtil;

import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.File;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.time.StopWatch;

/**
 * @author Brian Wing Shun Chan
 * @author Felix Ventero
 * @author Jorge Ferrer
 */
public class ServicePreAction extends Action {

	public ServicePreAction() {
		_initImportLARFiles();
	}

	@Override
	public void run(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws ActionException {

		StopWatch stopWatch = new StopWatch();

		stopWatch.start();

		try {
			servicePre(httpServletRequest, httpServletResponse, true);
		}
		catch (Exception exception) {
			throw new ActionException(exception);
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Running takes " + stopWatch.getTime() + " ms");
		}
	}

	public void servicePre(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			boolean initPermissionChecker)
		throws Exception {

		// Theme display

		ThemeDisplay themeDisplay = _initThemeDisplay(
			httpServletRequest, httpServletResponse, initPermissionChecker);

		if (themeDisplay == null) {
			return;
		}

		_trackThemeDisplay(httpServletResponse, themeDisplay);

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		// Service context

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			httpServletRequest);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		// Ajaxable render

		if (PropsValues.LAYOUT_AJAX_RENDER_ENABLE) {
			boolean portletAjaxRender = ParamUtil.getBoolean(
				httpServletRequest, "p_p_ajax", true);

			httpServletRequest.setAttribute(
				WebKeys.PORTLET_AJAX_RENDER, portletAjaxRender);
		}
	}

	protected File privateLARFile;
	protected File publicLARFile;

	protected class LayoutComposite {

		protected LayoutComposite(Layout layout, List<Layout> layouts) {
			_layout = layout;
			_layouts = layouts;
		}

		protected Layout getLayout() {
			return _layout;
		}

		protected List<Layout> getLayouts() {
			return _layouts;
		}

		private final Layout _layout;
		private final List<Layout> _layouts;

	}

	private void _addDefaultLayoutsByLAR(
			long userId, long groupId, boolean privateLayout, File larFile)
		throws Exception {

		User user = UserLocalServiceUtil.getUser(userId);

		Map<String, Serializable> importLayoutSettingsMap =
			ExportImportConfigurationSettingsMapFactoryUtil.
				buildImportLayoutSettingsMap(
					user, groupId, privateLayout, null,
					HashMapBuilder.put(
						PortletDataHandlerKeys.PERMISSIONS,
						new String[] {Boolean.TRUE.toString()}
					).put(
						PortletDataHandlerKeys.PORTLET_ARCHIVED_SETUPS_ALL,
						new String[] {Boolean.TRUE.toString()}
					).put(
						PortletDataHandlerKeys.PORTLET_CONFIGURATION,
						new String[] {Boolean.TRUE.toString()}
					).put(
						PortletDataHandlerKeys.PORTLET_CONFIGURATION_ALL,
						new String[] {Boolean.TRUE.toString()}
					).put(
						PortletDataHandlerKeys.PORTLET_DATA,
						new String[] {Boolean.TRUE.toString()}
					).put(
						PortletDataHandlerKeys.PORTLET_DATA_ALL,
						new String[] {Boolean.TRUE.toString()}
					).put(
						PortletDataHandlerKeys.PORTLET_DATA_CONTROL_DEFAULT,
						new String[] {Boolean.TRUE.toString()}
					).put(
						PortletDataHandlerKeys.PORTLET_SETUP_ALL,
						new String[] {Boolean.TRUE.toString()}
					).put(
						PortletDataHandlerKeys.PORTLET_USER_PREFERENCES_ALL,
						new String[] {Boolean.TRUE.toString()}
					).put(
						PortletDataHandlerKeys.THEME_REFERENCE,
						new String[] {Boolean.TRUE.toString()}
					).build());

		ExportImportConfiguration exportImportConfiguration =
			ExportImportConfigurationLocalServiceUtil.
				addDraftExportImportConfiguration(
					user.getUserId(),
					ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT,
					importLayoutSettingsMap);

		ExportImportLocalServiceUtil.importLayouts(
			exportImportConfiguration, larFile);
	}

	private void _addDefaultUserPrivateLayoutByProperties(
			long userId, long groupId)
		throws Exception {

		Map<Locale, String> nameMap = new HashMap<>();

		for (Locale locale : LanguageUtil.getAvailableLocales(groupId)) {
			nameMap.put(
				locale,
				LanguageUtil.get(
					locale, PropsValues.DEFAULT_USER_PRIVATE_LAYOUT_NAME));
		}

		Map<Locale, String> friendlyURLMap = Collections.singletonMap(
			LocaleUtil.getSiteDefault(),
			_getFriendlyURL(
				PropsValues.DEFAULT_USER_PRIVATE_LAYOUT_FRIENDLY_URL));

		Layout layout = LayoutLocalServiceUtil.addLayout(
			null, userId, groupId, true,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, nameMap, null, null, null,
			null, LayoutConstants.TYPE_PORTLET, StringPool.BLANK, false,
			friendlyURLMap, new ServiceContext());

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		layoutTypePortlet.setLayoutTemplateId(
			0, PropsValues.DEFAULT_USER_PRIVATE_LAYOUT_TEMPLATE_ID, false);

		LayoutTemplate layoutTemplate = layoutTypePortlet.getLayoutTemplate();

		for (String columnId : layoutTemplate.getColumns()) {
			String keyPrefix = PropsKeys.DEFAULT_USER_PRIVATE_LAYOUT_PREFIX;

			String portletIds = PropsUtil.get(keyPrefix.concat(columnId));

			layoutTypePortlet.addPortletIds(
				0, StringUtil.split(portletIds), columnId, false);
		}

		LayoutLocalServiceUtil.updateLayout(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			layout.getTypeSettings());

		boolean updateLayoutSet = false;

		LayoutSet layoutSet = layout.getLayoutSet();

		if (Validator.isNotNull(
				PropsValues.DEFAULT_USER_PRIVATE_LAYOUT_REGULAR_THEME_ID)) {

			layoutSet.setThemeId(
				PropsValues.DEFAULT_USER_PRIVATE_LAYOUT_REGULAR_THEME_ID);

			updateLayoutSet = true;
		}

		if (Validator.isNotNull(
				PropsValues.
					DEFAULT_USER_PRIVATE_LAYOUT_REGULAR_COLOR_SCHEME_ID)) {

			layoutSet.setColorSchemeId(
				PropsValues.
					DEFAULT_USER_PRIVATE_LAYOUT_REGULAR_COLOR_SCHEME_ID);

			updateLayoutSet = true;
		}

		if (updateLayoutSet) {
			LayoutSetLocalServiceUtil.updateLayoutSet(layoutSet);
		}
	}

	private void _addDefaultUserPrivateLayouts(User user) throws Exception {
		Group group = user.getGroup();

		if (privateLARFile != null) {
			_addDefaultLayoutsByLAR(
				user.getUserId(), group.getGroupId(), true, privateLARFile);
		}
		else {
			_addDefaultUserPrivateLayoutByProperties(
				user.getUserId(), group.getGroupId());
		}
	}

	private void _addDefaultUserPublicLayoutByProperties(
			long userId, long groupId)
		throws Exception {

		Map<Locale, String> nameMap = new HashMap<>();

		for (Locale locale : LanguageUtil.getAvailableLocales(groupId)) {
			nameMap.put(
				locale,
				LanguageUtil.get(
					locale, PropsValues.DEFAULT_USER_PUBLIC_LAYOUT_NAME));
		}

		Map<Locale, String> friendlyURLMap = Collections.singletonMap(
			LocaleUtil.getSiteDefault(),
			_getFriendlyURL(
				PropsValues.DEFAULT_USER_PUBLIC_LAYOUT_FRIENDLY_URL));

		Layout layout = LayoutLocalServiceUtil.addLayout(
			null, userId, groupId, false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, nameMap, null, null, null,
			null, LayoutConstants.TYPE_PORTLET, StringPool.BLANK, false,
			friendlyURLMap, new ServiceContext());

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		layoutTypePortlet.setLayoutTemplateId(
			0, PropsValues.DEFAULT_USER_PUBLIC_LAYOUT_TEMPLATE_ID, false);

		LayoutTemplate layoutTemplate = layoutTypePortlet.getLayoutTemplate();

		for (String columnId : layoutTemplate.getColumns()) {
			String keyPrefix = PropsKeys.DEFAULT_USER_PUBLIC_LAYOUT_PREFIX;

			String portletIds = PropsUtil.get(keyPrefix.concat(columnId));

			layoutTypePortlet.addPortletIds(
				0, StringUtil.split(portletIds), columnId, false);
		}

		LayoutLocalServiceUtil.updateLayout(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			layout.getTypeSettings());

		boolean updateLayoutSet = false;

		LayoutSet layoutSet = layout.getLayoutSet();

		if (Validator.isNotNull(
				PropsValues.DEFAULT_USER_PUBLIC_LAYOUT_REGULAR_THEME_ID)) {

			layoutSet.setThemeId(
				PropsValues.DEFAULT_USER_PUBLIC_LAYOUT_REGULAR_THEME_ID);

			updateLayoutSet = true;
		}

		if (Validator.isNotNull(
				PropsValues.
					DEFAULT_USER_PUBLIC_LAYOUT_REGULAR_COLOR_SCHEME_ID)) {

			layoutSet.setColorSchemeId(
				PropsValues.DEFAULT_USER_PUBLIC_LAYOUT_REGULAR_COLOR_SCHEME_ID);

			updateLayoutSet = true;
		}

		if (updateLayoutSet) {
			LayoutSetLocalServiceUtil.updateLayoutSet(layoutSet);
		}
	}

	private void _addDefaultUserPublicLayouts(User user) throws Exception {
		Group userGroup = user.getGroup();

		if (publicLARFile != null) {
			_addDefaultLayoutsByLAR(
				user.getUserId(), userGroup.getGroupId(), false, publicLARFile);
		}
		else {
			_addDefaultUserPublicLayoutByProperties(
				user.getUserId(), userGroup.getGroupId());
		}
	}

	private void _deleteDefaultUserPrivateLayouts(User user) throws Exception {
		Group group = user.getGroup();

		LayoutLocalServiceUtil.deleteLayouts(
			group.getGroupId(), true, new ServiceContext());
	}

	private void _deleteDefaultUserPublicLayouts(User user) throws Exception {
		Group userGroup = user.getGroup();

		LayoutLocalServiceUtil.deleteLayouts(
			userGroup.getGroupId(), false, new ServiceContext());
	}

	private LayoutComposite _getDefaultUserPersonalSiteLayoutComposite(
		User user) {

		Layout layout = null;

		Group group = user.getGroup();

		List<Layout> layouts = LayoutLocalServiceUtil.getLayouts(
			group.getGroupId(), true, LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);

		if (layouts.isEmpty()) {
			layouts = LayoutLocalServiceUtil.getLayouts(
				group.getGroupId(), false,
				LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);
		}

		if (!layouts.isEmpty()) {
			layout = layouts.get(0);
		}

		return new LayoutComposite(layout, layouts);
	}

	private LayoutComposite _getDefaultUserSitesLayoutComposite(final User user)
		throws Exception {

		final LinkedHashMap<String, Object> groupParams =
			LinkedHashMapBuilder.<String, Object>put(
				"usersGroups", Long.valueOf(user.getUserId())
			).build();

		int count = GroupLocalServiceUtil.searchCount(
			user.getCompanyId(), null, null, groupParams);

		IntervalActionProcessor<LayoutComposite> intervalActionProcessor =
			new IntervalActionProcessor<>(count);

		intervalActionProcessor.setPerformIntervalActionMethod(
			new IntervalActionProcessor.PerformIntervalActionMethod
				<LayoutComposite>() {

				@Override
				public LayoutComposite performIntervalAction(
					int start, int end) {

					List<Group> groups = GroupLocalServiceUtil.search(
						user.getCompanyId(), null, null, groupParams, start,
						end);

					for (Group group : groups) {
						List<Layout> layouts =
							LayoutLocalServiceUtil.getLayouts(
								group.getGroupId(), true,
								LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);

						if (layouts.isEmpty()) {
							layouts = LayoutLocalServiceUtil.getLayouts(
								group.getGroupId(), false,
								LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);
						}

						if (!layouts.isEmpty()) {
							return new LayoutComposite(layouts.get(0), layouts);
						}
					}

					intervalActionProcessor.incrementStart(groups.size());

					return null;
				}

			});

		LayoutComposite layoutComposite =
			intervalActionProcessor.performIntervalActions();

		if (layoutComposite == null) {
			return new LayoutComposite(null, new ArrayList<Layout>());
		}

		return layoutComposite;
	}

	private LayoutComposite _getDefaultViewableLayoutComposite(
			HttpServletRequest httpServletRequest, User user,
			PermissionChecker permissionChecker, boolean signedIn,
			boolean ignoreHiddenLayouts)
		throws Exception {

		LayoutComposite defaultLayoutComposite =
			_getDefaultVirtualHostLayoutComposite(httpServletRequest);

		defaultLayoutComposite = _getViewableLayoutComposite(
			httpServletRequest, user, permissionChecker,
			defaultLayoutComposite.getLayout(),
			defaultLayoutComposite.getLayouts(), ignoreHiddenLayouts);

		if (ListUtil.isNotEmpty(defaultLayoutComposite.getLayouts())) {
			return defaultLayoutComposite;
		}

		if (signedIn) {
			defaultLayoutComposite = _getDefaultUserPersonalSiteLayoutComposite(
				user);

			if (defaultLayoutComposite.getLayout() == null) {
				defaultLayoutComposite = _getDefaultUserSitesLayoutComposite(
					user);
			}

			defaultLayoutComposite = _getViewableLayoutComposite(
				httpServletRequest, user, permissionChecker,
				defaultLayoutComposite.getLayout(),
				defaultLayoutComposite.getLayouts(), ignoreHiddenLayouts);

			if (ListUtil.isNotEmpty(defaultLayoutComposite.getLayouts())) {
				return defaultLayoutComposite;
			}
		}

		defaultLayoutComposite = _getGuestSiteLayoutComposite(user);

		return _getViewableLayoutComposite(
			httpServletRequest, user, permissionChecker,
			defaultLayoutComposite.getLayout(),
			defaultLayoutComposite.getLayouts(), ignoreHiddenLayouts);
	}

	private LayoutComposite _getDefaultVirtualHostLayoutComposite(
			HttpServletRequest httpServletRequest)
		throws Exception {

		Layout layout = null;
		List<Layout> layouts = null;

		LayoutSet layoutSet = (LayoutSet)httpServletRequest.getAttribute(
			WebKeys.VIRTUAL_HOST_LAYOUT_SET);

		if (layoutSet != null) {
			layouts = LayoutLocalServiceUtil.getLayouts(
				layoutSet.getGroupId(), layoutSet.isPrivateLayout(),
				LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);

			Group group = null;

			if (!layouts.isEmpty()) {
				layout = layouts.get(0);

				group = layout.getGroup();
			}

			if ((layout != null) && layout.isPrivateLayout()) {
				layouts = LayoutLocalServiceUtil.getLayouts(
					group.getGroupId(), false,
					LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);

				if (!layouts.isEmpty()) {
					layout = layouts.get(0);
				}
				else {
					group = null;
					layout = null;
				}
			}

			if ((group != null) && group.isStagingGroup()) {
				Group liveGroup = group.getLiveGroup();

				layouts = LayoutLocalServiceUtil.getLayouts(
					liveGroup.getGroupId(), false,
					LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);

				if (!layouts.isEmpty()) {
					layout = layouts.get(0);
				}
				else {
					layout = null;
				}
			}
		}

		return new LayoutComposite(layout, layouts);
	}

	private String _getFriendlyURL(String friendlyURL) {
		friendlyURL = GetterUtil.getString(friendlyURL);

		return FriendlyURLNormalizerUtil.normalize(friendlyURL);
	}

	private LayoutComposite _getGuestSiteLayoutComposite(User user)
		throws Exception {

		Layout layout = null;

		Group guestGroup = GroupLocalServiceUtil.getGroup(
			user.getCompanyId(), GroupConstants.GUEST);

		List<Layout> layouts = LayoutLocalServiceUtil.getLayouts(
			guestGroup.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);

		if (!layouts.isEmpty()) {
			layout = layouts.get(0);
		}

		return new LayoutComposite(layout, layouts);
	}

	private String _getPortalDomain(String portalURL) {
		String portalDomain = _portalDomains.get(portalURL);

		if (portalDomain == null) {
			portalDomain = HttpComponentsUtil.getDomain(portalURL);

			_portalDomains.put(portalURL, portalDomain);
		}

		return portalDomain;
	}

	private LayoutComposite _getViewableLayoutComposite(
			HttpServletRequest httpServletRequest, User user,
			PermissionChecker permissionChecker, Layout layout,
			List<Layout> layouts, boolean ignoreHiddenLayouts)
		throws Exception {

		if (ListUtil.isEmpty(layouts)) {
			return new LayoutComposite(layout, layouts);
		}

		boolean hasViewLayoutPermission = false;

		if (_hasAccessPermission(permissionChecker, layout, false)) {
			hasViewLayoutPermission = true;
		}

		List<Layout> accessibleLayouts = new ArrayList<>();

		for (Layout curLayout : layouts) {
			if ((ignoreHiddenLayouts || !curLayout.isHidden()) &&
				_hasAccessPermission(permissionChecker, curLayout, false)) {

				if (accessibleLayouts.isEmpty() && !hasViewLayoutPermission) {
					layout = curLayout;
				}

				accessibleLayouts.add(curLayout);
			}
		}

		if (accessibleLayouts.isEmpty()) {
			layouts = null;

			if (!_isLoginRequest(httpServletRequest) &&
				!hasViewLayoutPermission) {

				if (user.isGuestUser() &&
					AuthLoginGroupSettingsUtil.isPromptEnabled(
						layout.getGroupId())) {

					throw new PrincipalException.MustBeAuthenticated(
						String.valueOf(user.getUserId()));
				}

				SessionErrors.add(
					httpServletRequest,
					LayoutPermissionException.class.getName());
			}
		}
		else {
			layouts = accessibleLayouts;
		}

		return new LayoutComposite(layout, layouts);
	}

	private boolean _hasAccessPermission(
			PermissionChecker permissionChecker, Layout layout,
			boolean checkViewableGroup)
		throws Exception {

		return LayoutPermissionUtil.contains(
			permissionChecker, layout, checkViewableGroup, ActionKeys.VIEW);
	}

	private Boolean _hasPowerUserRole(User user) throws Exception {
		return RoleLocalServiceUtil.hasUserRole(
			user.getUserId(), user.getCompanyId(), RoleConstants.POWER_USER,
			true);
	}

	private void _initImportLARFiles() {
		String privateLARFileName =
			PropsValues.DEFAULT_USER_PRIVATE_LAYOUTS_LAR;

		if (_log.isDebugEnabled()) {
			_log.debug("Reading private LAR file " + privateLARFileName);
		}

		if (Validator.isNotNull(privateLARFileName)) {
			privateLARFile = new File(privateLARFileName);

			if (!privateLARFile.exists()) {
				_log.error(
					"Private LAR file " + privateLARFile + " does not exist");

				privateLARFile = null;
			}
			else {
				if (_log.isDebugEnabled()) {
					_log.debug("Using private LAR file " + privateLARFileName);
				}
			}
		}

		String publicLARFileName = PropsValues.DEFAULT_USER_PUBLIC_LAYOUTS_LAR;

		if (_log.isDebugEnabled()) {
			_log.debug("Reading public LAR file " + publicLARFileName);
		}

		if (Validator.isNotNull(publicLARFileName)) {
			publicLARFile = new File(publicLARFileName);

			if (!publicLARFile.exists()) {
				_log.error(
					"Public LAR file " + publicLARFile + " does not exist");

				publicLARFile = null;
			}
			else {
				if (_log.isDebugEnabled()) {
					_log.debug("Using public LAR file " + publicLARFileName);
				}
			}
		}
	}

	private ThemeDisplay _initThemeDisplay(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			boolean initPermissionChecker)
		throws Exception {

		// Company

		Company company = PortalUtil.getCompany(httpServletRequest);

		// CDN host

		String cdnHost = PortalUtil.getCDNHost(httpServletRequest);

		String dynamicResourcesCDNHost = StringPool.BLANK;

		boolean cdnDynamicResourceEnabled =
			PortalUtil.isCDNDynamicResourcesEnabled(httpServletRequest);

		if (cdnDynamicResourceEnabled) {
			dynamicResourcesCDNHost = cdnHost;
		}

		// Paths

		String contextPath = PortalUtil.getPathContext();
		String friendlyURLPrivateGroupPath =
			PortalUtil.getPathFriendlyURLPrivateGroup();
		String friendlyURLPrivateUserPath =
			PortalUtil.getPathFriendlyURLPrivateUser();
		String friendlyURLPublicPath = PortalUtil.getPathFriendlyURLPublic();
		String imagePath = dynamicResourcesCDNHost.concat(
			PortalUtil.getPathImage());
		String mainPath = _PATH_MAIN;

		String i18nPath = (String)httpServletRequest.getAttribute(
			WebKeys.I18N_PATH);

		if (Validator.isNotNull(i18nPath)) {
			if (Validator.isNotNull(contextPath)) {
				String i18nContextPath = contextPath.concat(i18nPath);

				friendlyURLPrivateGroupPath = StringUtil.replaceFirst(
					friendlyURLPrivateGroupPath, contextPath, i18nContextPath);
				friendlyURLPrivateUserPath = StringUtil.replaceFirst(
					friendlyURLPrivateUserPath, contextPath, i18nContextPath);
				friendlyURLPublicPath = StringUtil.replaceFirst(
					friendlyURLPublicPath, contextPath, i18nContextPath);
				mainPath = StringUtil.replaceFirst(
					mainPath, contextPath, i18nContextPath);
			}
			else {
				friendlyURLPrivateGroupPath = i18nPath.concat(
					friendlyURLPrivateGroupPath);
				friendlyURLPrivateUserPath = i18nPath.concat(
					friendlyURLPrivateUserPath);
				friendlyURLPublicPath = i18nPath.concat(friendlyURLPublicPath);
				mainPath = i18nPath.concat(mainPath);
			}
		}

		// Company logo

		String companyLogo = imagePath + "/company_logo";

		long companyLogoId = company.getLogoId();

		if (companyLogoId > 0) {
			companyLogo = StringBundler.concat(
				companyLogo, "?img_id=", company.getLogoId(), "&t=",
				WebServerServletTokenUtil.getToken(company.getLogoId()));
		}

		int companyLogoHeight = 0;
		int companyLogoWidth = 0;

		Image companyLogoImage = null;

		if (companyLogoId > 0) {
			companyLogoImage = ImageLocalServiceUtil.getCompanyLogo(
				companyLogoId);
		}
		else {
			companyLogoImage = ImageToolUtil.getDefaultCompanyLogo();
		}

		if (companyLogoImage != null) {
			companyLogoHeight = companyLogoImage.getHeight();
			companyLogoWidth = companyLogoImage.getWidth();
		}

		// User

		User user = null;

		try {
			user = PortalUtil.initUser(httpServletRequest);
		}
		catch (NoSuchUserException noSuchUserException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(noSuchUserException);
			}

			return null;
		}

		HttpSession httpSession = httpServletRequest.getSession();

		User realUser = user;

		Long realUserId = (Long)httpSession.getAttribute(WebKeys.USER_ID);

		if (realUserId != null) {
			if (user.getUserId() != realUserId.longValue()) {
				realUser = UserLocalServiceUtil.getUserById(
					realUserId.longValue());
			}
			else if (!user.isActive()) {
				httpSession.invalidate();
			}
		}

		boolean signedIn = !user.isGuestUser();

		if (PropsValues.BROWSER_CACHE_DISABLED ||
			(PropsValues.BROWSER_CACHE_SIGNED_IN_DISABLED && signedIn)) {

			httpServletResponse.setDateHeader(HttpHeaders.EXPIRES, 0);
			httpServletResponse.setHeader(
				HttpHeaders.CACHE_CONTROL,
				HttpHeaders.CACHE_CONTROL_NO_CACHE_VALUE);
			httpServletResponse.setHeader(
				HttpHeaders.PRAGMA, HttpHeaders.PRAGMA_NO_CACHE_VALUE);
		}

		long refererPlid = ParamUtil.getLong(httpServletRequest, "refererPlid");

		if ((refererPlid != 0) &&
			(LayoutLocalServiceUtil.fetchLayout(refererPlid) == null)) {

			refererPlid = 0;
		}

		// Permission checker

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker(
				user, initPermissionChecker);

		// Cookie support

		try {

			// LEP-4069

			CookiesManagerUtil.validateSupportCookie(httpServletRequest);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			CookiesManagerUtil.addSupportCookie(
				httpServletRequest, httpServletResponse);
		}

		// Time zone

		TimeZone timeZone = user.getTimeZone();

		if (timeZone == null) {
			timeZone = company.getTimeZone();
		}

		// Layouts

		if (signedIn) {
			_updateUserLayouts(user);
		}

		Layout layout = null;

		long plid = ParamUtil.getLong(httpServletRequest, "p_l_id");

		if (plid > 0) {
			layout = LayoutLocalServiceUtil.getLayout(plid);
		}
		else {
			long groupId = ParamUtil.getLong(httpServletRequest, "groupId");
			long layoutId = ParamUtil.getLong(httpServletRequest, "layoutId");

			if ((groupId > 0) && (layoutId > 0)) {
				boolean privateLayout = ParamUtil.getBoolean(
					httpServletRequest, "privateLayout");

				layout = LayoutLocalServiceUtil.getLayout(
					groupId, privateLayout, layoutId);
			}
		}

		if (layout != null) {
			Group layoutGroup = layout.getGroup();

			if (layoutGroup.isUser() &&
				(layoutGroup.getClassPK() != user.getUserId())) {

				if ((plid > 0) &&
					!GetterUtil.getBoolean(
						PropsUtil.get(
							PropsKeys.LAYOUT_USER_ACCESS_VIA_PLID_ENABLED))) {

					long originalPlid = ParamUtil.getLong(
						PortalUtil.getOriginalServletRequest(
							httpServletRequest),
						"p_l_id");

					String method = httpServletRequest.getMethod();

					if ((originalPlid == plid) &&
						(Objects.equals(method, HttpMethods.GET) ||
						 (!Objects.equals(method, HttpMethods.GET) &&
						  !signedIn))) {

						String message =
							"User layouts cannot be accessed via p_l_id";

						if (_log.isWarnEnabled()) {
							_log.warn(message);
						}

						throw new NoSuchLayoutException(message);
					}
				}

				if ((layout.isPrivateLayout() &&
					 !PrefsPropsUtil.getBoolean(
						 PortalUtil.getCompanyId(httpServletRequest),
						 PropsKeys.LAYOUT_USER_PRIVATE_LAYOUTS_ENABLED)) ||
					(layout.isPublicLayout() &&
					 !PrefsPropsUtil.getBoolean(
						 user.getCompanyId(),
						 PropsKeys.LAYOUT_USER_PUBLIC_LAYOUTS_ENABLED))) {

					User layoutUser = UserLocalServiceUtil.getUserById(
						company.getCompanyId(), layoutGroup.getClassPK());

					_updateUserLayouts(layoutUser);

					layout = LayoutLocalServiceUtil.fetchLayout(
						layout.getPlid());
				}
			}
		}

		boolean viewableSourceGroup = true;

		if (layout != null) {
			Group sourceGroup = null;

			long sourceGroupId = ParamUtil.getLong(
				httpServletRequest, "p_v_l_s_g_id");

			if ((sourceGroupId > 0) && (sourceGroupId != layout.getGroupId())) {
				sourceGroup = GroupLocalServiceUtil.fetchGroup(sourceGroupId);
			}

			if (sourceGroup != null) {
				if (layout.isTypeControlPanel() || layout.isPublicLayout() ||
					SitesUtil.isUserGroupLayoutSetViewable(
						permissionChecker, layout.getGroup())) {

					if (layout.isTypeControlPanel() && sourceGroup.isUser() &&
						(sourceGroup.getClassPK() != user.getUserId()) &&
						!GroupPermissionUtil.contains(
							permissionChecker, sourceGroup, ActionKeys.VIEW)) {

						String message = StringBundler.concat(
							"User ", user.getUserId(),
							" is not allowed to access the private pages of ",
							"user ", sourceGroup.getClassPK());

						if (_log.isWarnEnabled()) {
							_log.warn(message);
						}

						throw new NoSuchLayoutException(message);
					}

					layout = new VirtualLayout(layout, sourceGroup);
				}
				else {
					viewableSourceGroup = false;
				}
			}
		}

		long doAsGroupId = ParamUtil.getLong(httpServletRequest, "doAsGroupId");
		String doAsUserId = ParamUtil.getString(
			httpServletRequest, "doAsUserId");
		String doAsUserLanguageId = ParamUtil.getString(
			httpServletRequest, "doAsUserLanguageId");
		Group group = null;
		List<Layout> layouts = null;
		boolean loginRequest = _isLoginRequest(httpServletRequest);

		Boolean redirectToDefaultLayout =
			(Boolean)httpServletRequest.getAttribute(
				WebKeys.REDIRECT_TO_DEFAULT_LAYOUT);

		if (redirectToDefaultLayout == null) {
			redirectToDefaultLayout = Boolean.FALSE;
		}

		long refererGroupId = ParamUtil.getLong(
			httpServletRequest, "refererGroupId");

		boolean stagingGroup = false;
		boolean viewableGroup = false;

		if (layout != null) {
			group = layout.getGroup();

			stagingGroup = group.isStagingGroup();

			if (!signedIn && PropsValues.AUTH_FORWARD_BY_REDIRECT) {
				httpServletRequest.setAttribute(
					WebKeys.REQUESTED_LAYOUT, layout);
			}

			viewableGroup = _hasAccessPermission(
				permissionChecker, layout, true);

			boolean viewableStaging = false;

			if (!group.isControlPanel() &&
				GroupPermissionUtil.contains(
					permissionChecker, group, ActionKeys.VIEW_STAGING)) {

				viewableStaging = true;
			}

			if (viewableStaging) {
				layouts = LayoutLocalServiceUtil.getLayouts(
					layout.getGroupId(), layout.isPrivateLayout(),
					LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);
			}
			else if ((!viewableGroup || !viewableSourceGroup) && stagingGroup) {
				layout = null;
			}
			else if (!loginRequest &&
					 (!viewableGroup || !viewableSourceGroup ||
					  (!redirectToDefaultLayout &&
					   !_hasAccessPermission(
						   permissionChecker, layout, false)))) {

				if (!group.isUser() && user.isGuestUser() &&
					AuthLoginGroupSettingsUtil.isPromptEnabled(
						group.getGroupId())) {

					throw new PrincipalException.MustBeAuthenticated(
						user.getUserId());
				}

				String message = StringBundler.concat(
					"User ", user.getUserId(), " is not allowed to access the ",
					layout.isPrivateLayout() ? "private" : "public",
					" pages of group ", layout.getGroupId());

				if (_log.isWarnEnabled()) {
					_log.warn(message);
				}

				throw new NoSuchLayoutException(message);
			}
			else if (loginRequest && !viewableGroup) {
				layout = null;
			}
			else if (group.isLayoutPrototype()) {
				layouts = new ArrayList<>();
			}
			else {
				layouts = LayoutLocalServiceUtil.getLayouts(
					layout.getGroupId(), layout.isPrivateLayout(),
					LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);

				if (!group.isControlPanel()) {
					doAsGroupId = 0;
				}
			}
		}

		List<Layout> unfilteredLayouts = layouts;

		LayoutComposite viewableLayoutComposite = null;

		if (layout == null) {
			boolean ignoreHiddenLayouts = false;

			if (((!viewableGroup || !viewableSourceGroup) && stagingGroup) ||
				(loginRequest && !viewableGroup)) {

				ignoreHiddenLayouts = true;
			}

			viewableLayoutComposite = _getDefaultViewableLayoutComposite(
				httpServletRequest, user, permissionChecker, signedIn,
				ignoreHiddenLayouts);

			httpServletRequest.setAttribute(
				WebKeys.LAYOUT_DEFAULT, Boolean.TRUE);
		}
		else {
			viewableLayoutComposite = _getViewableLayoutComposite(
				httpServletRequest, user, permissionChecker, layout, layouts,
				false);
		}

		String layoutSetLogo = null;

		layout = viewableLayoutComposite.getLayout();
		layouts = viewableLayoutComposite.getLayouts();

		if (layout != null) {
			if (group == null) {
				group = layout.getGroup();
			}

			if (!group.isControlPanel()) {
				_rememberVisitedGroupIds(
					httpServletRequest, group.getGroupId());
			}
		}

		LayoutTypePortlet layoutTypePortlet = null;

		layouts = _mergeAdditionalLayouts(
			httpServletRequest, user, permissionChecker, layout, layouts);

		LayoutSet layoutSet = null;

		boolean hasUpdateLayoutPermission = false;

		if (layout != null) {
			LayoutTypeAccessPolicy layoutTypeAccessPolicy =
				LayoutTypeAccessPolicyTracker.getLayoutTypeAccessPolicy(layout);

			boolean hasCustomizeLayoutPermission =
				layoutTypeAccessPolicy.isCustomizeLayoutAllowed(
					permissionChecker, layout);

			hasUpdateLayoutPermission =
				layoutTypeAccessPolicy.isUpdateLayoutAllowed(
					permissionChecker, layout);

			layoutSet = layout.getLayoutSet();

			if (company.isSiteLogo()) {
				long logoId = 0;

				if (layoutSet.isLogo()) {
					logoId = layoutSet.getLogoId();

					if (logoId == 0) {
						logoId = layoutSet.getLiveLogoId();
					}
				}
				else {
					LayoutSet siblingLayoutSet =
						LayoutSetLocalServiceUtil.getLayoutSet(
							layout.getGroupId(), !layout.isPrivateLayout());

					if (siblingLayoutSet.isLogo()) {
						logoId = siblingLayoutSet.getLogoId();

						if (logoId == 0) {
							logoId = siblingLayoutSet.getLiveLogoId();
						}
					}
				}

				if (logoId > 0) {
					layoutSetLogo = StringBundler.concat(
						imagePath, "/layout_set_logo?img_id=", logoId, "&t=",
						WebServerServletTokenUtil.getToken(logoId));

					Image layoutSetLogoImage =
						ImageLocalServiceUtil.getCompanyLogo(logoId);

					companyLogo = layoutSetLogo;
					companyLogoHeight = layoutSetLogoImage.getHeight();
					companyLogoWidth = layoutSetLogoImage.getWidth();
				}
			}

			plid = layout.getPlid();

			layoutTypePortlet = (LayoutTypePortlet)layout.getLayoutType();

			boolean customizable = layoutTypePortlet.isCustomizable();

			boolean customizedView = SessionParamUtil.getBoolean(
				httpServletRequest, "customized_view", true);

			if (!customizable || group.isLayoutPrototype() ||
				group.isLayoutSetPrototype() || group.isStagingGroup()) {

				customizedView = false;
			}

			layoutTypePortlet.setCustomizedView(customizedView);
			layoutTypePortlet.setUpdatePermission(hasUpdateLayoutPermission);

			if (signedIn && customizable && customizedView &&
				hasCustomizeLayoutPermission) {

				layoutTypePortlet.setPortalPreferences(
					PortletPreferencesFactoryUtil.getPortalPreferences(
						user.getUserId(), true));
			}

			LayoutClone layoutClone = LayoutCloneFactory.getInstance();

			if (layoutClone != null) {
				String typeSettings = layoutClone.get(httpServletRequest, plid);

				if (typeSettings != null) {
					UnicodeProperties typeSettingsUnicodeProperties =
						UnicodePropertiesBuilder.create(
							true
						).load(
							typeSettings
						).build();

					layoutTypePortlet.setStateMax(
						typeSettingsUnicodeProperties.getProperty(
							LayoutTypePortletConstants.STATE_MAX));
					layoutTypePortlet.setStateMin(
						typeSettingsUnicodeProperties.getProperty(
							LayoutTypePortletConstants.STATE_MIN));
					layoutTypePortlet.setModeAbout(
						typeSettingsUnicodeProperties.getProperty(
							LayoutTypePortletConstants.MODE_ABOUT));
					layoutTypePortlet.setModeConfig(
						typeSettingsUnicodeProperties.getProperty(
							LayoutTypePortletConstants.MODE_CONFIG));
					layoutTypePortlet.setModeEdit(
						typeSettingsUnicodeProperties.getProperty(
							LayoutTypePortletConstants.MODE_EDIT));
					layoutTypePortlet.setModeEditDefaults(
						typeSettingsUnicodeProperties.getProperty(
							LayoutTypePortletConstants.MODE_EDIT_DEFAULTS));
					layoutTypePortlet.setModeEditGuest(
						typeSettingsUnicodeProperties.getProperty(
							LayoutTypePortletConstants.MODE_EDIT_GUEST));
					layoutTypePortlet.setModeHelp(
						typeSettingsUnicodeProperties.getProperty(
							LayoutTypePortletConstants.MODE_HELP));
					layoutTypePortlet.setModePreview(
						typeSettingsUnicodeProperties.getProperty(
							LayoutTypePortletConstants.MODE_PREVIEW));
					layoutTypePortlet.setModePrint(
						typeSettingsUnicodeProperties.getProperty(
							LayoutTypePortletConstants.MODE_PRINT));
				}
			}

			httpServletRequest.setAttribute(WebKeys.LAYOUT, layout);
			httpServletRequest.setAttribute(WebKeys.LAYOUTS, layouts);
		}

		// Locale

		String i18nLanguageId = (String)httpServletRequest.getAttribute(
			WebKeys.I18N_LANGUAGE_ID);

		Locale locale = PortalUtil.getLocale(
			httpServletRequest, httpServletResponse, true);

		// Portal URL

		String portalURL = PortalUtil.getPortalURL(httpServletRequest);

		// Scope

		long scopeGroupId = PortalUtil.getScopeGroupId(httpServletRequest);

		if (group.isInheritContent()) {
			scopeGroupId = group.getParentGroupId();
		}

		if ((scopeGroupId <= 0) && (doAsGroupId > 0)) {
			scopeGroupId = doAsGroupId;
		}

		long siteGroupId = 0;

		if (layout != null) {
			if (layout.isTypeControlPanel()) {
				siteGroupId = PortalUtil.getSiteGroupId(scopeGroupId);
			}
			else {
				siteGroupId = PortalUtil.getSiteGroupId(layout.getGroupId());
			}
		}

		// Theme and color scheme

		Theme theme = null;
		ColorScheme colorScheme = null;

		if ((layout != null) &&
			(layout.isTypeControlPanel() || group.isControlPanel())) {

			String themeId = PrefsPropsUtil.getString(
				company.getCompanyId(),
				PropsKeys.CONTROL_PANEL_LAYOUT_REGULAR_THEME_ID);
			String colorSchemeId =
				ColorSchemeFactoryUtil.getDefaultRegularColorSchemeId();

			theme = ThemeLocalServiceUtil.getTheme(
				company.getCompanyId(), themeId);

			colorScheme = ThemeLocalServiceUtil.getColorScheme(
				company.getCompanyId(), theme.getThemeId(), colorSchemeId);

			httpServletRequest.setAttribute(WebKeys.COLOR_SCHEME, colorScheme);

			httpServletRequest.setAttribute(WebKeys.THEME, theme);
		}

		boolean themeCssFastLoad = PropsValues.THEME_CSS_FAST_LOAD;

		if (PropsValues.THEME_CSS_FAST_LOAD_CHECK_REQUEST_PARAMETER) {
			themeCssFastLoad = SessionParamUtil.getBoolean(
				httpServletRequest, "css_fast_load",
				PropsValues.THEME_CSS_FAST_LOAD);
		}

		boolean themeImagesFastLoad = PropsValues.THEME_IMAGES_FAST_LOAD;

		if (PropsValues.THEME_IMAGES_FAST_LOAD_CHECK_REQUEST_PARAMETER) {
			SessionParamUtil.getBoolean(
				httpServletRequest, "images_fast_load",
				PropsValues.THEME_IMAGES_FAST_LOAD);
		}

		boolean themeJsBarebone = PropsValues.JAVASCRIPT_BAREBONE_ENABLED;

		if (themeJsBarebone &&
			(signedIn || FrontendSPAUtil.isEnabled(company.getCompanyId()))) {

			themeJsBarebone = false;
		}

		boolean themeJsFastLoad = SessionParamUtil.getBoolean(
			httpServletRequest, "js_fast_load",
			PropsValues.JAVASCRIPT_FAST_LOAD);

		String lifecycle = ParamUtil.getString(
			httpServletRequest, "p_p_lifecycle", "0");

		lifecycle = ParamUtil.getString(
			httpServletRequest, "p_t_lifecycle", lifecycle);

		String async = ParamUtil.getString(httpServletRequest, "p_p_async");
		String hub = ParamUtil.getString(httpServletRequest, "p_p_hub");
		boolean isolated = ParamUtil.getBoolean(
			httpServletRequest, "p_p_isolated");

		boolean widget = false;

		Boolean widgetObj = (Boolean)httpServletRequest.getAttribute(
			WebKeys.WIDGET);

		if (widgetObj != null) {
			widget = widgetObj.booleanValue();
		}

		// Theme display

		ThemeDisplay themeDisplay = ThemeDisplayFactory.create();

		themeDisplay.setRequest(httpServletRequest);
		themeDisplay.setResponse(httpServletResponse);

		// Set attributes first that other methods (getCDNBaseURL and
		// setLookAndFeel) depend on

		boolean secure = PortalUtil.isForwardedSecure(httpServletRequest);

		themeDisplay.setCDNDynamicResourcesHost(dynamicResourcesCDNHost);
		themeDisplay.setCDNHost(cdnHost);
		themeDisplay.setPortalDomain(_getPortalDomain(portalURL));
		themeDisplay.setPortalURL(portalURL);
		themeDisplay.setRefererPlid(refererPlid);
		themeDisplay.setSecure(secure);
		themeDisplay.setServerName(
			PortalUtil.getForwardedHost(httpServletRequest));
		themeDisplay.setServerPort(
			PortalUtil.getForwardedPort(httpServletRequest));
		themeDisplay.setWidget(widget);

		themeDisplay.setAsync(async.equals("1"));
		themeDisplay.setCompany(company);
		themeDisplay.setCompanyLogo(companyLogo);
		themeDisplay.setCompanyLogoHeight(companyLogoHeight);
		themeDisplay.setCompanyLogoWidth(companyLogoWidth);
		themeDisplay.setDoAsGroupId(doAsGroupId);
		themeDisplay.setDoAsUserId(doAsUserId);
		themeDisplay.setDoAsUserLanguageId(doAsUserLanguageId);
		themeDisplay.setHubAction(hub.equals("0"));
		themeDisplay.setHubPartialAction(hub.equals("1"));
		themeDisplay.setHubResource(hub.equals("2"));
		themeDisplay.setI18nLanguageId(i18nLanguageId);
		themeDisplay.setI18nPath(i18nPath);
		themeDisplay.setIsolated(isolated);
		themeDisplay.setLanguageId(LocaleUtil.toLanguageId(locale));
		themeDisplay.setLayout(layout);
		themeDisplay.setLayoutSet(layoutSet);
		themeDisplay.setLayoutSetLogo(layoutSetLogo);
		themeDisplay.setLayoutTypePortlet(layoutTypePortlet);
		themeDisplay.setLayouts(layouts);
		themeDisplay.setLifecycle(lifecycle);
		themeDisplay.setLifecycleAction(lifecycle.equals("1"));
		themeDisplay.setLifecycleEvent(lifecycle.equals("3"));
		themeDisplay.setLifecycleRender(lifecycle.equals("0"));
		themeDisplay.setLifecycleResource(lifecycle.equals("2"));
		themeDisplay.setLocale(locale);
		themeDisplay.setLookAndFeel(theme, colorScheme);
		themeDisplay.setPathApplet(contextPath.concat("/applets"));
		themeDisplay.setPathContext(contextPath);
		themeDisplay.setPathFriendlyURLPrivateGroup(
			friendlyURLPrivateGroupPath);
		themeDisplay.setPathFriendlyURLPrivateUser(friendlyURLPrivateUserPath);
		themeDisplay.setPathFriendlyURLPublic(friendlyURLPublicPath);
		themeDisplay.setPathImage(imagePath);
		themeDisplay.setPathJavaScript(
			PortalWebResourcesUtil.getContextPath(
				PortalWebResourceConstants.RESOURCE_TYPE_JS));
		themeDisplay.setPathMain(mainPath);
		themeDisplay.setPathSound(contextPath.concat("/html/sound"));
		themeDisplay.setPermissionChecker(permissionChecker);
		themeDisplay.setPlid(plid);
		themeDisplay.setPpid(ParamUtil.getString(httpServletRequest, "p_p_id"));
		themeDisplay.setRealCompanyLogo(companyLogo);
		themeDisplay.setRealCompanyLogoHeight(companyLogoHeight);
		themeDisplay.setRealCompanyLogoWidth(companyLogoWidth);
		themeDisplay.setRealUser(realUser);
		themeDisplay.setRefererGroupId(refererGroupId);
		themeDisplay.setScopeGroupId(scopeGroupId);
		themeDisplay.setSignedIn(signedIn);
		themeDisplay.setSiteDefaultLocale(
			PortalUtil.getSiteDefaultLocale(siteGroupId));
		themeDisplay.setSiteGroupId(siteGroupId);
		themeDisplay.setStateExclusive(
			LiferayWindowState.isExclusive(httpServletRequest));
		themeDisplay.setStateMaximized(
			LiferayWindowState.isMaximized(httpServletRequest));
		themeDisplay.setStatePopUp(
			LiferayWindowState.isPopUp(httpServletRequest));
		themeDisplay.setThemeCssFastLoad(themeCssFastLoad);
		themeDisplay.setThemeImagesFastLoad(themeImagesFastLoad);
		themeDisplay.setThemeJsBarebone(themeJsBarebone);
		themeDisplay.setThemeJsFastLoad(themeJsFastLoad);
		themeDisplay.setTimeZone(timeZone);
		themeDisplay.setUnfilteredLayouts(unfilteredLayouts);
		themeDisplay.setUser(user);

		// Icons

		boolean showControlPanelIcon = false;

		if (signedIn &&
			PortalPermissionUtil.contains(
				permissionChecker, ActionKeys.VIEW_CONTROL_PANEL)) {

			showControlPanelIcon = true;
		}

		themeDisplay.setShowControlPanelIcon(showControlPanelIcon);

		themeDisplay.setShowHomeIcon(true);
		themeDisplay.setShowMyAccountIcon(signedIn);
		themeDisplay.setShowPageSettingsIcon(hasUpdateLayoutPermission);
		themeDisplay.setShowPortalIcon(true);
		themeDisplay.setShowSignInIcon(
			!signedIn &&
			!(Objects.equals(
				PropsValues.AUTH_LOGIN_PORTLET_NAME, themeDisplay.getPpid()) &&
			  Objects.equals(
				  ParamUtil.getString(httpServletRequest, "p_p_state"),
				  WindowState.MAXIMIZED.toString())));

		boolean showSignOutIcon = signedIn;

		if (themeDisplay.isImpersonated()) {
			showSignOutIcon = false;
		}

		themeDisplay.setShowSignOutIcon(showSignOutIcon);

		themeDisplay.setShowStagingIcon(false);

		boolean showSiteAdministrationIcon = false;

		if (signedIn &&
			GroupPermissionUtil.contains(
				permissionChecker, group,
				ActionKeys.VIEW_SITE_ADMINISTRATION)) {

			showSiteAdministrationIcon = true;
		}

		themeDisplay.setShowSiteAdministrationIcon(showSiteAdministrationIcon);

		// Session

		if (PropsValues.SESSION_ENABLE_URL_WITH_SESSION_ID &&
			!CookiesManagerUtil.hasSessionId(httpServletRequest)) {

			themeDisplay.setAddSessionIdToURL(true);
			themeDisplay.setSessionId(httpSession.getId());
		}

		// URLs

		String urlControlPanel = friendlyURLPrivateGroupPath.concat(
			GroupConstants.CONTROL_PANEL_FRIENDLY_URL);

		if (Validator.isNotNull(doAsUserId)) {
			urlControlPanel = HttpComponentsUtil.addParameter(
				urlControlPanel, "doAsUserId", doAsUserId);
		}

		if (refererGroupId > 0) {
			urlControlPanel = HttpComponentsUtil.addParameter(
				urlControlPanel, "refererGroupId", refererGroupId);
		}
		else if (scopeGroupId > 0) {
			Layout refererLayout = LayoutLocalServiceUtil.fetchLayout(plid);

			if (refererLayout != null) {
				Group refererLayoutGroup = refererLayout.getGroup();

				if (refererLayoutGroup.isUserGroup()) {
					urlControlPanel = HttpComponentsUtil.addParameter(
						urlControlPanel, "refererGroupId", scopeGroupId);
				}
			}
		}

		if (refererPlid > 0) {
			urlControlPanel = HttpComponentsUtil.addParameter(
				urlControlPanel, "refererPlid", refererPlid);
		}
		else if (plid > 0) {
			urlControlPanel = HttpComponentsUtil.addParameter(
				urlControlPanel, "refererPlid", plid);
		}

		if (themeDisplay.isAddSessionIdToURL()) {
			urlControlPanel = PortalUtil.getURLWithSessionId(
				urlControlPanel, httpSession.getId());
		}

		themeDisplay.setURLControlPanel(urlControlPanel);

		themeDisplay.setURLCurrent(
			PortalUtil.getCurrentURL(httpServletRequest));

		themeDisplay.setURLHome(PortalUtil.getHomeURL(httpServletRequest));

		if (layout != null) {
			if (layout.isTypePortlet() && hasUpdateLayoutPermission) {
				themeDisplay.setShowLayoutTemplatesIcon(true);

				if (!group.isUser()) {
					themeDisplay.setShowPageCustomizationIcon(true);
				}
			}

			if (hasUpdateLayoutPermission) {
				themeDisplay.setShowPageSettingsIcon(true);
			}

			if (group.hasStagingGroup()) {
				themeDisplay.setShowLayoutTemplatesIcon(false);
				themeDisplay.setURLPublishToLive(null);
			}

			if (group.isControlPanel()) {
				themeDisplay.setShowPageSettingsIcon(false);
				themeDisplay.setURLPublishToLive(null);
			}

			// LEP-4987

			if (group.isStaged() || group.isStagingGroup()) {
				Group scopeGroup = GroupLocalServiceUtil.getGroup(scopeGroupId);

				boolean hasManageStagingPermission =
					GroupPermissionUtil.contains(
						permissionChecker, scopeGroup,
						ActionKeys.MANAGE_STAGING);
				boolean hasPublishStagingPermission =
					GroupPermissionUtil.contains(
						permissionChecker, scopeGroup,
						ActionKeys.PUBLISH_STAGING);
				boolean hasViewStagingPermission = GroupPermissionUtil.contains(
					permissionChecker, scopeGroup, ActionKeys.VIEW_STAGING);

				if (hasManageStagingPermission || hasPublishStagingPermission ||
					hasUpdateLayoutPermission || hasViewStagingPermission) {

					themeDisplay.setShowStagingIcon(true);
				}

				if (hasPublishStagingPermission) {
					PortletURL publishToLiveURL = PortletURLBuilder.create(
						PortletURLFactoryUtil.create(
							httpServletRequest, PortletKeys.EXPORT_IMPORT, plid,
							PortletRequest.RENDER_PHASE)
					).setMVCRenderCommandName(
						"/export_import/publish_layouts"
					).buildPortletURL();

					if (layout.isPrivateLayout()) {
						publishToLiveURL.setParameter("tabs1", "private-pages");
					}
					else {
						publishToLiveURL.setParameter("tabs1", "public-pages");
					}

					publishToLiveURL.setParameter(
						"groupId", String.valueOf(scopeGroupId));
					publishToLiveURL.setParameter(
						"selPlid", String.valueOf(plid));
					publishToLiveURL.setPortletMode(PortletMode.VIEW);
					publishToLiveURL.setWindowState(
						LiferayWindowState.EXCLUSIVE);

					themeDisplay.setURLPublishToLive(publishToLiveURL);
				}
			}
		}

		if (!user.isActive() ||
			(PrefsPropsUtil.getBoolean(
				company.getCompanyId(), PropsKeys.TERMS_OF_USE_REQUIRED) &&
			 !user.isAgreedToTermsOfUse())) {

			themeDisplay.setShowMyAccountIcon(false);
			themeDisplay.setShowPageSettingsIcon(false);
		}

		if ((layout != null) && layout.isLayoutPrototypeLinkActive()) {
			themeDisplay.setShowPageCustomizationIcon(false);
		}

		if (group.isLayoutPrototype()) {
			themeDisplay.setShowHomeIcon(false);
			themeDisplay.setShowMyAccountIcon(false);
			themeDisplay.setShowPageCustomizationIcon(false);
			themeDisplay.setShowPageSettingsIcon(true);
			themeDisplay.setShowPortalIcon(false);
			themeDisplay.setShowSignInIcon(false);
			themeDisplay.setShowSignOutIcon(false);
			themeDisplay.setShowSiteAdministrationIcon(false);
			themeDisplay.setShowStagingIcon(false);
		}

		if (group.isLayoutSetPrototype()) {
			themeDisplay.setShowPageCustomizationIcon(false);
		}

		if (group.hasStagingGroup()) {
			themeDisplay.setShowLayoutTemplatesIcon(false);
			themeDisplay.setShowPageCustomizationIcon(false);
		}

		themeDisplay.setURLPortal(portalURL.concat(contextPath));

		String securePortalURL = PortalUtil.getPortalURL(
			httpServletRequest, secure);

		String urlSignIn = StringBundler.concat(
			securePortalURL, mainPath, _PATH_PORTAL_LOGIN);

		if (layout != null) {
			urlSignIn = HttpComponentsUtil.addParameter(
				urlSignIn, "p_l_id", layout.getPlid());
		}

		themeDisplay.setURLSignIn(urlSignIn);

		themeDisplay.setURLSignOut(mainPath.concat(_PATH_PORTAL_LOGOUT));

		return themeDisplay;
	}

	/**
	 * Returns <code>true</code> if the request URI's path starts with the
	 * portal's default login path <code>c/portal/login</code>.
	 *
	 * @param  httpServletRequest the servlet request for the page, which can be
	 *         a result of a redirect
	 * @return <code>true</code> if the request is a login request;
	 *         <code>false</code> otherwise
	 */
	private boolean _isLoginRequest(HttpServletRequest httpServletRequest) {
		if (GetterUtil.getBoolean(
				httpServletRequest.getAttribute(WebKeys.LOGIN_REQUEST))) {

			return true;
		}

		String requestURI = httpServletRequest.getRequestURI();

		String mainPath = _PATH_MAIN;

		if (_PATH_PROXY != null) {
			if (!requestURI.startsWith(_PATH_PROXY)) {
				requestURI = _PATH_PROXY.concat(requestURI);
			}

			if (!mainPath.startsWith(_PATH_PROXY)) {
				mainPath = _PATH_PROXY.concat(mainPath);
			}
		}

		if (requestURI.startsWith(mainPath) &&
			requestURI.startsWith(_PATH_PORTAL_LOGIN, mainPath.length())) {

			return true;
		}

		return false;
	}

	private List<Layout> _mergeAdditionalLayouts(
			HttpServletRequest httpServletRequest, User user,
			PermissionChecker permissionChecker, Layout layout,
			List<Layout> layouts)
		throws Exception {

		if ((layout == null) || layout.isPrivateLayout()) {
			return layouts;
		}

		long layoutGroupId = layout.getGroupId();

		Group guestGroup = GroupLocalServiceUtil.getGroup(
			user.getCompanyId(), GroupConstants.GUEST);

		if (layoutGroupId != guestGroup.getGroupId()) {
			Group layoutGroup = GroupLocalServiceUtil.getGroup(layoutGroupId);

			UnicodeProperties typeSettingsUnicodeProperties =
				layoutGroup.getTypeSettingsProperties();

			boolean mergeGuestPublicPages = GetterUtil.getBoolean(
				typeSettingsUnicodeProperties.getProperty(
					"mergeGuestPublicPages"));

			if (!mergeGuestPublicPages) {
				return layouts;
			}

			List<Layout> guestLayouts = LayoutLocalServiceUtil.getLayouts(
				guestGroup.getGroupId(), false,
				LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);

			LayoutComposite viewableLayoutComposite =
				_getViewableLayoutComposite(
					httpServletRequest, user, permissionChecker, layout,
					guestLayouts, false);

			guestLayouts = viewableLayoutComposite.getLayouts();

			if (layouts == null) {
				return guestLayouts;
			}

			layouts.addAll(0, guestLayouts);
		}
		else {
			HttpSession httpSession = httpServletRequest.getSession();

			Long previousGroupId = (Long)httpSession.getAttribute(
				WebKeys.VISITED_GROUP_ID_PREVIOUS);

			if ((previousGroupId != null) &&
				(previousGroupId.longValue() != layoutGroupId)) {

				Group previousGroup = null;

				try {
					previousGroup = GroupLocalServiceUtil.getGroup(
						previousGroupId.longValue());
				}
				catch (NoSuchGroupException noSuchGroupException) {
					if (_log.isWarnEnabled()) {
						_log.warn(noSuchGroupException);
					}

					return layouts;
				}

				UnicodeProperties typeSettingsUnicodeProperties =
					previousGroup.getTypeSettingsProperties();

				boolean mergeGuestPublicPages = GetterUtil.getBoolean(
					typeSettingsUnicodeProperties.getProperty(
						"mergeGuestPublicPages"));

				if (!mergeGuestPublicPages) {
					return layouts;
				}

				List<Layout> previousLayouts =
					LayoutLocalServiceUtil.getLayouts(
						previousGroupId.longValue(), false,
						LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);

				LayoutComposite viewableLayoutComposite =
					_getViewableLayoutComposite(
						httpServletRequest, user, permissionChecker, layout,
						previousLayouts, false);

				previousLayouts = viewableLayoutComposite.getLayouts();

				if (previousLayouts != null) {
					layouts.addAll(previousLayouts);
				}
			}
		}

		return layouts;
	}

	private void _rememberVisitedGroupIds(
		HttpServletRequest httpServletRequest, long currentGroupId) {

		String requestURI = GetterUtil.getString(
			httpServletRequest.getRequestURI());

		if (!requestURI.endsWith(_PATH_PORTAL_LAYOUT)) {
			return;
		}

		HttpSession httpSession = httpServletRequest.getSession();

		Long recentGroupId = (Long)httpSession.getAttribute(
			WebKeys.VISITED_GROUP_ID_RECENT);

		Long previousGroupId = (Long)httpSession.getAttribute(
			WebKeys.VISITED_GROUP_ID_PREVIOUS);

		if (recentGroupId == null) {
			recentGroupId = Long.valueOf(currentGroupId);

			httpSession.setAttribute(
				WebKeys.VISITED_GROUP_ID_RECENT, recentGroupId);
		}
		else if (recentGroupId.longValue() != currentGroupId) {
			previousGroupId = Long.valueOf(recentGroupId.longValue());

			recentGroupId = Long.valueOf(currentGroupId);

			httpSession.setAttribute(
				WebKeys.VISITED_GROUP_ID_RECENT, recentGroupId);

			httpSession.setAttribute(
				WebKeys.VISITED_GROUP_ID_PREVIOUS, previousGroupId);
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Current group id " + currentGroupId);
			_log.debug("Recent group id " + recentGroupId);
			_log.debug("Previous group id " + previousGroupId);
		}
	}

	private void _trackThemeDisplay(
		HttpServletResponse httpServletResponse, ThemeDisplay themeDisplay) {

		if (!_TRACK_THEME_DISPLAY) {
			return;
		}

		httpServletResponse.setHeader(
			"X-Liferay-Request-Company",
			String.valueOf(themeDisplay.getCompanyId()));

		List<String> liferayRequestGroupHeaderValues = new ArrayList<>();

		liferayRequestGroupHeaderValues.add(
			String.valueOf(themeDisplay.getScopeGroupId()));

		Group group = themeDisplay.getScopeGroup();

		liferayRequestGroupHeaderValues.add(group.getType() + "t");

		Layout layout = themeDisplay.getLayout();

		if (group.getGroupId() == themeDisplay.getCompanyGroupId()) {
			liferayRequestGroupHeaderValues.add("1x");
		}

		if (group.getParentGroupId() != 0) {
			liferayRequestGroupHeaderValues.add("2x");
		}

		if (group.isStaged()) {
			liferayRequestGroupHeaderValues.add("3x");
		}

		if (group.isControlPanel() || layout.isTypeControlPanel()) {
			liferayRequestGroupHeaderValues.add("4x");
		}

		if (group.isUser()) {
			if (layout.isPrivateLayout()) {
				liferayRequestGroupHeaderValues.add("5x");
			}
			else {
				liferayRequestGroupHeaderValues.add("10x");
			}

			if (layout instanceof VirtualLayout) {
				liferayRequestGroupHeaderValues.add("6x");
			}
		}

		if (group.isLayoutSetPrototype()) {
			liferayRequestGroupHeaderValues.add("7x");
		}

		if (group.isLayoutPrototype() || (layout.getMasterLayoutPlid() > 0)) {
			liferayRequestGroupHeaderValues.add("8x");
		}

		if (group.isOrganization()) {
			liferayRequestGroupHeaderValues.add("9x");
		}

		if (group.isSite()) {
			liferayRequestGroupHeaderValues.add("s");
		}

		httpServletResponse.setHeader(
			"X-Liferay-Request-Group",
			ListUtil.toString(
				liferayRequestGroupHeaderValues, (String)null,
				StringPool.SPACE));

		User user = themeDisplay.getUser();

		httpServletResponse.setHeader(
			"X-Liferay-Request-Guest-User", String.valueOf(user.isGuestUser()));
		httpServletResponse.setHeader(
			"X-Liferay-Request-User",
			DigesterUtil.digestHex(
				DigesterUtil.MD5, String.valueOf(user.getUserId())));
	}

	private void _updateUserLayouts(User user) throws Exception {
		if (user.isLayoutsUpdated()) {
			return;
		}

		Boolean hasPowerUserRole = null;

		// Private layouts

		boolean addDefaultUserPrivateLayouts = false;

		if (PrefsPropsUtil.getBoolean(
				user.getCompanyId(),
				PropsKeys.LAYOUT_USER_PRIVATE_LAYOUTS_ENABLED) &&
			PrefsPropsUtil.getBoolean(
				user.getCompanyId(),
				PropsKeys.LAYOUT_USER_PRIVATE_LAYOUTS_AUTO_CREATE)) {

			addDefaultUserPrivateLayouts = true;

			if (PropsValues.LAYOUT_USER_PRIVATE_LAYOUTS_POWER_USER_REQUIRED) {
				if (hasPowerUserRole == null) {
					hasPowerUserRole = _hasPowerUserRole(user);
				}

				if (!hasPowerUserRole.booleanValue()) {
					addDefaultUserPrivateLayouts = false;
				}
			}
		}

		Integer privateLayoutsCount = null;

		if (addDefaultUserPrivateLayouts) {
			privateLayoutsCount = LayoutLocalServiceUtil.getLayoutsCount(
				user.getGroupId(), true);

			if (privateLayoutsCount == 0) {
				_addDefaultUserPrivateLayouts(user);
			}
		}

		boolean deleteDefaultUserPrivateLayouts = false;

		if (!PrefsPropsUtil.getBoolean(
				user.getCompanyId(),
				PropsKeys.LAYOUT_USER_PRIVATE_LAYOUTS_ENABLED)) {

			deleteDefaultUserPrivateLayouts = true;
		}
		else if (PropsValues.LAYOUT_USER_PRIVATE_LAYOUTS_POWER_USER_REQUIRED) {
			if (hasPowerUserRole == null) {
				hasPowerUserRole = _hasPowerUserRole(user);
			}

			if (!hasPowerUserRole.booleanValue()) {
				deleteDefaultUserPrivateLayouts = true;
			}
		}

		if (deleteDefaultUserPrivateLayouts) {
			if (privateLayoutsCount == null) {
				privateLayoutsCount = LayoutLocalServiceUtil.getLayoutsCount(
					user.getGroupId(), true);
			}

			if (privateLayoutsCount > 0) {
				_deleteDefaultUserPrivateLayouts(user);
			}
		}

		// Public pages

		boolean addDefaultUserPublicLayouts = false;

		if (PrefsPropsUtil.getBoolean(
				user.getCompanyId(),
				PropsKeys.LAYOUT_USER_PUBLIC_LAYOUTS_ENABLED) &&
			PrefsPropsUtil.getBoolean(
				user.getCompanyId(),
				PropsKeys.LAYOUT_USER_PUBLIC_LAYOUTS_AUTO_CREATE)) {

			addDefaultUserPublicLayouts = true;

			if (PropsValues.LAYOUT_USER_PUBLIC_LAYOUTS_POWER_USER_REQUIRED) {
				if (hasPowerUserRole == null) {
					hasPowerUserRole = _hasPowerUserRole(user);
				}

				if (!hasPowerUserRole.booleanValue()) {
					addDefaultUserPublicLayouts = false;
				}
			}
		}

		Integer publicLayoutsCount = null;

		if (addDefaultUserPublicLayouts) {
			publicLayoutsCount = LayoutLocalServiceUtil.getLayoutsCount(
				user.getGroupId(), false);

			if (publicLayoutsCount == 0) {
				_addDefaultUserPublicLayouts(user);
			}
		}

		boolean deleteDefaultUserPublicLayouts = false;

		if (!PrefsPropsUtil.getBoolean(
				user.getCompanyId(),
				PropsKeys.LAYOUT_USER_PUBLIC_LAYOUTS_ENABLED)) {

			deleteDefaultUserPublicLayouts = true;
		}
		else if (PropsValues.LAYOUT_USER_PUBLIC_LAYOUTS_POWER_USER_REQUIRED) {
			if (hasPowerUserRole == null) {
				hasPowerUserRole = _hasPowerUserRole(user);
			}

			if (!hasPowerUserRole.booleanValue()) {
				deleteDefaultUserPublicLayouts = true;
			}
		}

		if (deleteDefaultUserPublicLayouts) {
			if (publicLayoutsCount == null) {
				publicLayoutsCount = LayoutLocalServiceUtil.getLayoutsCount(
					user.getGroupId(), false);
			}

			if (publicLayoutsCount > 0) {
				_deleteDefaultUserPublicLayouts(user);
			}
		}

		user.setLayoutsUpdated(true);
	}

	private static final String _PATH_MAIN = PortalUtil.getPathMain();

	private static final String _PATH_PORTAL_LAYOUT = "/portal/layout";

	private static final String _PATH_PORTAL_LOGIN = "/portal/login";

	private static final String _PATH_PORTAL_LOGOUT = "/portal/logout";

	private static final String _PATH_PROXY;

	private static final boolean _TRACK_THEME_DISPLAY = GetterUtil.getBoolean(
		PropsUtil.get("service.pre.action.track.theme.display"));

	private static final Log _log = LogFactoryUtil.getLog(
		ServicePreAction.class);

	private static final Map<String, String> _portalDomains =
		new ConcurrentHashMap<>();

	static {
		String pathProxy = PortalUtil.getPathProxy();

		if (Validator.isBlank(pathProxy)) {
			_PATH_PROXY = null;
		}
		else {
			_PATH_PROXY = pathProxy;
		}
	}

}