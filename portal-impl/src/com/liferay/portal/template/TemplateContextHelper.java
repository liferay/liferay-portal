/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.template;

import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.portal.kernel.audit.AuditMessageFactoryUtil;
import com.liferay.portal.kernel.audit.AuditRouterUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.image.ImageToolUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.language.UnicodeLanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.portlet.PortletModeFactory_IW;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletRequestModel;
import com.liferay.portal.kernel.portlet.PortletRequestModelFactory;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.WindowStateFactory_IW;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.OrganizationService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.service.permission.AccountPermissionUtil;
import com.liferay.portal.kernel.service.permission.CommonPermissionUtil;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.service.permission.OrganizationPermissionUtil;
import com.liferay.portal.kernel.service.permission.PasswordPolicyPermissionUtil;
import com.liferay.portal.kernel.service.permission.PortalPermissionUtil;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.service.permission.RolePermissionUtil;
import com.liferay.portal.kernel.service.permission.UserGroupPermissionUtil;
import com.liferay.portal.kernel.service.permission.UserPermissionUtil;
import com.liferay.portal.kernel.servlet.BrowserSnifferUtil;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.template.TemplateHandlerRegistryUtil;
import com.liferay.portal.kernel.template.TemplateVariableGroup;
import com.liferay.portal.kernel.theme.NavItem;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil_IW;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.ClassLoaderUtil;
import com.liferay.portal.kernel.util.DateUtil_IW;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.GetterUtil_IW;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.InetAddressUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListMergeable;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil_IW;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.SessionClicks_IW;
import com.liferay.portal.kernel.util.StaticFieldGetter;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil_IW;
import com.liferay.portal.kernel.util.TimeZoneUtil_IW;
import com.liferay.portal.kernel.util.UnicodeFormatter_IW;
import com.liferay.portal.kernel.util.Validator_IW;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.webserver.WebServerServletTokenUtil;
import com.liferay.portal.kernel.xml.SAXReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.InetAddress;
import java.net.URL;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.portlet.ActionRequest;
import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts.tiles.ComponentContext;
import org.apache.struts.tiles.taglib.ComponentConstants;

/**
 * @author Tina Tian
 * @author Jorge Ferrer
 * @author Raymond Augé
 */
public class TemplateContextHelper {

	public static Map<String, TemplateVariableGroup> getTemplateVariableGroups(
			long classNameId, long classPK, String language, Locale locale)
		throws Exception {

		TemplateHandler templateHandler =
			TemplateHandlerRegistryUtil.getTemplateHandler(classNameId);

		if (templateHandler == null) {
			return Collections.emptyMap();
		}

		Map<String, TemplateVariableGroup> templateVariableGroups =
			templateHandler.getTemplateVariableGroups(
				classPK, language, locale);

		String[] restrictedVariables = templateHandler.getRestrictedVariables(
			language);

		TemplateVariableGroup portalServicesTemplateVariableGroup =
			new TemplateVariableGroup("portal-services", restrictedVariables);

		portalServicesTemplateVariableGroup.setAutocompleteEnabled(false);

		portalServicesTemplateVariableGroup.addServiceLocatorVariables(
			GroupLocalService.class, GroupService.class,
			LayoutLocalService.class, LayoutService.class,
			OrganizationLocalService.class, OrganizationService.class,
			UserLocalService.class, UserService.class);

		templateVariableGroups.put(
			portalServicesTemplateVariableGroup.getLabel(),
			portalServicesTemplateVariableGroup);

		return templateVariableGroups;
	}

	public Map<String, Object> getHelperUtilities(
		ClassLoader classLoader, boolean restricted) {

		Map<String, Object>[] helperUtilitiesArray = _helperUtilitiesMaps.get(
			classLoader);

		if (helperUtilitiesArray == null) {
			helperUtilitiesArray = (Map<String, Object>[])new Map<?, ?>[2];

			_helperUtilitiesMaps.put(classLoader, helperUtilitiesArray);
		}
		else {
			Map<String, Object> helperUtilities = null;

			if (restricted) {
				helperUtilities = helperUtilitiesArray[1];
			}
			else {
				helperUtilities = helperUtilitiesArray[0];
			}

			if (helperUtilities != null) {
				return helperUtilities;
			}
		}

		Map<String, Object> helperUtilities = new HashMap<>();

		populateCommonHelperUtilities(helperUtilities);
		populateExtraHelperUtilities(helperUtilities);

		if (restricted) {
			Set<String> restrictedVariables = getRestrictedVariables();

			for (String restrictedVariable : restrictedVariables) {
				helperUtilities.remove(restrictedVariable);
			}

			helperUtilitiesArray[1] = helperUtilities;
		}
		else {
			helperUtilitiesArray[0] = helperUtilities;
		}

		return helperUtilities;
	}

	public Set<String> getRestrictedVariables() {
		return Collections.emptySet();
	}

	public TemplateControlContext getTemplateControlContext() {
		return _pacl.getTemplateControlContext();
	}

	public void prepare(
		Map<String, Object> contextObjects, HttpServletRequest request) {

		// Request

		contextObjects.put("httpServletRequest", request);
		contextObjects.putIfAbsent("request", request);

		// Portlet config

		PortletConfig portletConfig = (PortletConfig)request.getAttribute(
			JavaConstants.JAVAX_PORTLET_CONFIG);

		if (portletConfig != null) {
			contextObjects.put("portletConfig", portletConfig);
		}

		// Render request

		final PortletRequest portletRequest =
			(PortletRequest)request.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);

		if (portletRequest != null) {
			if (portletRequest instanceof RenderRequest) {
				contextObjects.put("renderRequest", portletRequest);
			}
		}

		// Render response

		final PortletResponse portletResponse =
			(PortletResponse)request.getAttribute(
				JavaConstants.JAVAX_PORTLET_RESPONSE);

		if (portletResponse != null) {
			if (portletResponse instanceof RenderResponse) {
				contextObjects.put("renderResponse", portletResponse);
			}
		}

		// XML request

		if ((portletRequest != null) && (portletResponse != null)) {
			contextObjects.put(
				"portletRequestModelFactory",
				new PortletRequestModelFactory(
					portletRequest, portletResponse));

			// Deprecated

			contextObjects.put(
				"xmlRequest",
				new Object() {

					@Override
					public String toString() {
						PortletRequestModel portletRequestModel =
							new PortletRequestModel(
								portletRequest, portletResponse);

						return portletRequestModel.toXML();
					}

				});
		}

		// Theme display

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (themeDisplay != null) {
			Layout layout = themeDisplay.getLayout();
			List<Layout> layouts = themeDisplay.getLayouts();

			contextObjects.put("bodyCssClass", StringPool.BLANK);
			contextObjects.put("colorScheme", themeDisplay.getColorScheme());
			contextObjects.put("company", themeDisplay.getCompany());
			contextObjects.put("layout", layout);
			contextObjects.put("layouts", layouts);
			contextObjects.put(
				"layoutTypePortlet", themeDisplay.getLayoutTypePortlet());
			contextObjects.put("locale", themeDisplay.getLocale());
			contextObjects.put(
				"permissionChecker", themeDisplay.getPermissionChecker());
			contextObjects.put("plid", String.valueOf(themeDisplay.getPlid()));
			contextObjects.put(
				"portletDisplay", themeDisplay.getPortletDisplay());
			contextObjects.put("realUser", themeDisplay.getRealUser());
			contextObjects.put(
				"scopeGroupId", Long.valueOf(themeDisplay.getScopeGroupId()));
			contextObjects.put("themeDisplay", themeDisplay);
			contextObjects.put("timeZone", themeDisplay.getTimeZone());
			contextObjects.put("user", themeDisplay.getUser());

			// Navigation items

			if (layout != null) {
				try {
					List<NavItem> navItems = NavItem.fromLayouts(
						request, themeDisplay, contextObjects);

					contextObjects.put("navItems", navItems);
				}
				catch (PortalException pe) {
					_log.error(pe, pe);
				}
			}

			// Deprecated

			contextObjects.put(
				"portletGroupId", Long.valueOf(themeDisplay.getScopeGroupId()));
		}

		// Theme

		Theme theme = (Theme)request.getAttribute(WebKeys.THEME);

		if ((theme == null) && (themeDisplay != null)) {
			theme = themeDisplay.getTheme();
		}

		if (theme != null) {
			contextObjects.put("theme", theme);
		}

		// Tiles attributes

		prepareTiles(contextObjects, request);

		// Page title and subtitle

		ListMergeable<String> pageTitleListMergeable =
			(ListMergeable<String>)request.getAttribute(WebKeys.PAGE_TITLE);

		if (pageTitleListMergeable != null) {
			String pageTitle = pageTitleListMergeable.mergeToString(
				StringPool.SPACE);

			contextObjects.put("pageTitle", pageTitle);
		}

		ListMergeable<String> pageSubtitleListMergeable =
			(ListMergeable<String>)request.getAttribute(WebKeys.PAGE_SUBTITLE);

		if (pageSubtitleListMergeable != null) {
			String pageSubtitle = pageSubtitleListMergeable.mergeToString(
				StringPool.SPACE);

			contextObjects.put("pageSubtitle", pageSubtitle);
		}
	}

	public void removeAllHelperUtilities() {
		_helperUtilitiesMaps.clear();
	}

	public void removeHelperUtilities(ClassLoader classLoader) {
		_helperUtilitiesMaps.remove(classLoader);
	}

	public interface PACL {

		public TemplateControlContext getTemplateControlContext();

	}

	protected void populateCommonHelperUtilities(
		Map<String, Object> variables) {

		// Array util

		variables.put("arrayUtil", ArrayUtil_IW.getInstance());

		// Audit message factory

		try {
			variables.put(
				"auditMessageFactoryUtil",
				AuditMessageFactoryUtil.getAuditMessageFactory());
		}
		catch (SecurityException se) {
			_log.error(se, se);
		}

		// Audit router util

		try {
			variables.put("auditRouterUtil", AuditRouterUtil.getAuditRouter());
		}
		catch (SecurityException se) {
			_log.error(se, se);
		}

		// Browser sniffer

		try {
			variables.put(
				"browserSniffer", BrowserSnifferUtil.getBrowserSniffer());
		}
		catch (SecurityException se) {
			_log.error(se, se);
		}

		// Calendar factory

		try {
			variables.put(
				"calendarFactory", CalendarFactoryUtil.getCalendarFactory());
		}
		catch (SecurityException se) {
			_log.error(se, se);
		}

		// Date format

		try {
			variables.put(
				"dateFormatFactory",
				FastDateFormatFactoryUtil.getFastDateFormatFactory());
		}
		catch (SecurityException se) {
			_log.error(se, se);
		}

		// Date util

		variables.put("dateUtil", DateUtil_IW.getInstance());

		// Expando column service

		try {
			ServiceLocator serviceLocator = ServiceLocator.getInstance();

			// Service locator

			variables.put("serviceLocator", serviceLocator);

			try {
				variables.put(
					"expandoColumnLocalService",
					serviceLocator.findService(
						ExpandoColumnLocalService.class.getName()));
			}
			catch (SecurityException se) {
				_log.error(se, se);
			}

			// Expando row service

			try {
				variables.put(
					"expandoRowLocalService",
					serviceLocator.findService(
						ExpandoRowLocalService.class.getName()));
			}
			catch (SecurityException se) {
				_log.error(se, se);
			}

			// Expando table service

			try {
				variables.put(
					"expandoTableLocalService",
					serviceLocator.findService(
						ExpandoTableLocalService.class.getName()));
			}
			catch (SecurityException se) {
				_log.error(se, se);
			}

			// Expando value service

			try {
				variables.put(
					"expandoValueLocalService",
					serviceLocator.findService(
						ExpandoValueLocalService.class.getName()));
			}
			catch (SecurityException se) {
				_log.error(se, se);
			}
		}
		catch (SecurityException se) {
			_log.error(se, se);
		}

		// Getter util

		variables.put("getterUtil", GetterUtil_IW.getInstance());

		// Html util

		try {
			variables.put("htmlUtil", HtmlUtil.getHtml());
		}
		catch (SecurityException se) {
			_log.error(se, se);
		}

		// Http util

		try {
			variables.put("httpUtil", new HttpWrapper(HttpUtil.getHttp()));
		}
		catch (SecurityException se) {
			_log.error(se, se);
		}

		try {
			variables.put(
				"httpUtilUnsafe", new HttpWrapper(HttpUtil.getHttp(), false));
		}
		catch (SecurityException se) {
			_log.error(se, se);
		}

		// Image tool util

		try {
			variables.put("imageToolUtil", ImageToolUtil.getImageTool());
		}
		catch (SecurityException se) {
			_log.error(se, se);
		}

		// JSON factory util

		try {
			variables.put("jsonFactoryUtil", JSONFactoryUtil.getJSONFactory());
		}
		catch (SecurityException se) {
			_log.error(se, se);
		}

		// Language util

		try {
			variables.put("languageUtil", LanguageUtil.getLanguage());
		}
		catch (SecurityException se) {
			_log.error(se, se);
		}

		try {
			variables.put(
				"unicodeLanguageUtil",
				UnicodeLanguageUtil.getUnicodeLanguage());
		}
		catch (SecurityException se) {
			_log.error(se, se);
		}

		// Locale util

		try {
			variables.put("localeUtil", LocaleUtil.getInstance());
		}
		catch (SecurityException se) {
			_log.error(se, se);
		}

		// Param util

		variables.put("paramUtil", ParamUtil_IW.getInstance());

		// Portal util

		try {
			variables.put("portalUtil", PortalUtil.getPortal());
		}
		catch (SecurityException se) {
			_log.error(se, se);
		}

		try {
			variables.put("portal", PortalUtil.getPortal());
		}
		catch (SecurityException se) {
			_log.error(se, se);
		}

		// Prefs props util

		try {
			variables.put("prefsPropsUtil", PrefsPropsUtil.getPrefsProps());
		}
		catch (SecurityException se) {
			_log.error(se, se);
		}

		// Props util

		try {
			variables.put("propsUtil", PropsUtil.getProps());
		}
		catch (SecurityException se) {
			_log.error(se, se);
		}

		// Portlet mode factory

		variables.put(
			"portletModeFactory", PortletModeFactory_IW.getInstance());

		// Portlet URL factory

		try {
			variables.put(
				"portletURLFactory",
				PortletURLFactoryUtil.getPortletURLFactory());
		}
		catch (SecurityException se) {
			_log.error(se, se);
		}

		try {
			UtilLocator utilLocator = UtilLocator.getInstance();

			// Util locator

			variables.put("utilLocator", utilLocator);

			// SAX reader util

			try {
				variables.put(
					"saxReaderUtil",
					utilLocator.findUtil(SAXReader.class.getName()));
			}
			catch (SecurityException se) {
				_log.error(se, se);
			}
		}
		catch (SecurityException se) {
			_log.error(se, se);
		}

		// Session clicks

		variables.put("sessionClicks", SessionClicks_IW.getInstance());

		// Static field getter

		variables.put("staticFieldGetter", StaticFieldGetter.getInstance());

		// String util

		variables.put("stringUtil", StringUtil_IW.getInstance());

		// Time zone util

		variables.put("timeZoneUtil", TimeZoneUtil_IW.getInstance());

		// Unicode formatter

		variables.put("unicodeFormatter", UnicodeFormatter_IW.getInstance());

		// Validator

		variables.put("validator", Validator_IW.getInstance());

		// Web server servlet token

		try {
			variables.put(
				"webServerToken",
				WebServerServletTokenUtil.getWebServerServletToken());
		}
		catch (SecurityException se) {
			_log.error(se, se);
		}

		// Window state factory

		variables.put(
			"windowStateFactory", WindowStateFactory_IW.getInstance());

		// Permissions

		try {
			variables.put(
				"accountPermission",
				AccountPermissionUtil.getAccountPermission());
		}
		catch (SecurityException se) {
			_log.error(se, se);
		}

		try {
			variables.put(
				"commonPermission", CommonPermissionUtil.getCommonPermission());
		}
		catch (SecurityException se) {
			_log.error(se, se);
		}

		try {
			variables.put(
				"groupPermission", GroupPermissionUtil.getGroupPermission());
		}
		catch (SecurityException se) {
			_log.error(se, se);
		}

		try {
			variables.put(
				"layoutPermission", LayoutPermissionUtil.getLayoutPermission());
		}
		catch (SecurityException se) {
			_log.error(se, se);
		}

		try {
			variables.put(
				"organizationPermission",
				OrganizationPermissionUtil.getOrganizationPermission());
		}
		catch (SecurityException se) {
			_log.error(se, se);
		}

		try {
			variables.put(
				"passwordPolicyPermission",
				PasswordPolicyPermissionUtil.getPasswordPolicyPermission());
		}
		catch (SecurityException se) {
			_log.error(se, se);
		}

		try {
			variables.put(
				"portalPermission", PortalPermissionUtil.getPortalPermission());
		}
		catch (SecurityException se) {
			_log.error(se, se);
		}

		try {
			variables.put(
				"portletPermission",
				PortletPermissionUtil.getPortletPermission());
		}
		catch (SecurityException se) {
			_log.error(se, se);
		}

		Map<String, PortletProvider.Action> portletProviderActionMap =
			new HashMap<>();

		for (PortletProvider.Action action : PortletProvider.Action.values()) {
			portletProviderActionMap.put(action.name(), action);
		}

		try {
			variables.put("portletProviderAction", portletProviderActionMap);
		}
		catch (SecurityException se) {
			_log.error(se, se);
		}

		try {
			variables.put(
				"rolePermission", RolePermissionUtil.getRolePermission());
		}
		catch (SecurityException se) {
			_log.error(se, se);
		}

		try {
			variables.put(
				"userGroupPermission",
				UserGroupPermissionUtil.getUserGroupPermission());
		}
		catch (SecurityException se) {
			_log.error(se, se);
		}

		try {
			variables.put(
				"userPermission", UserPermissionUtil.getUserPermission());
		}
		catch (SecurityException se) {
			_log.error(se, se);
		}

		// Deprecated

		populateDeprecatedCommonHelperUtilities(variables);
	}

	@SuppressWarnings("deprecation")
	protected void populateDeprecatedCommonHelperUtilities(
		Map<String, Object> variables) {

		try {
			variables.put(
				"dateFormats",
				FastDateFormatFactoryUtil.getFastDateFormatFactory());
		}
		catch (SecurityException se) {
			_log.error(se, se);
		}

		try {
			variables.put(
				"imageToken",
				WebServerServletTokenUtil.getWebServerServletToken());
		}
		catch (SecurityException se) {
			_log.error(se, se);
		}

		try {
			variables.put(
				"locationPermission",
				OrganizationPermissionUtil.getOrganizationPermission());
		}
		catch (SecurityException se) {
			_log.error(se, se);
		}

		try {
			com.liferay.portal.kernel.util.Randomizer_IW randomizer =
				com.liferay.portal.kernel.util.Randomizer_IW.getInstance();

			variables.put("randomizer", randomizer.getWrappedInstance());
		}
		catch (SecurityException se) {
			_log.error(se, se);
		}
	}

	protected void populateExtraHelperUtilities(Map<String, Object> variables) {
	}

	protected void prepareTiles(
		Map<String, Object> contextObjects, HttpServletRequest request) {

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		ComponentContext componentContext =
			(ComponentContext)request.getAttribute(
				ComponentConstants.COMPONENT_CONTEXT);

		if (componentContext == null) {
			themeDisplay.setTilesSelectable(true);

			return;
		}

		String tilesTitle = (String)componentContext.getAttribute("title");

		themeDisplay.setTilesTitle(tilesTitle);

		contextObjects.put("tilesTitle", tilesTitle);

		String tilesContent = (String)componentContext.getAttribute("content");

		themeDisplay.setTilesContent(tilesContent);

		contextObjects.put("tilesContent", tilesContent);

		boolean tilesSelectable = GetterUtil.getBoolean(
			(String)componentContext.getAttribute("selectable"));

		themeDisplay.setTilesSelectable(tilesSelectable);

		contextObjects.put("tilesSelectable", tilesSelectable);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TemplateContextHelper.class);

	private static final PACL _pacl = new NoPACL();

	private final Map<ClassLoader, Map<String, Object>[]> _helperUtilitiesMaps =
		new ConcurrentHashMap<>();

	private static class HttpWrapper implements Http {

		public HttpWrapper(Http http) {
			this(http, true);
		}

		public HttpWrapper(Http http, boolean disableLocalNetworkAccess) {
			_http = http;
			_disableLocalNetworkAccess = disableLocalNetworkAccess;
		}

		@Override
		public String addParameter(String url, String name, boolean value) {
			return _http.addParameter(url, name, value);
		}

		@Override
		public String addParameter(String url, String name, double value) {
			return _http.addParameter(url, name, value);
		}

		@Override
		public String addParameter(String url, String name, int value) {
			return _http.addParameter(url, name, value);
		}

		@Override
		public String addParameter(String url, String name, long value) {
			return _http.addParameter(url, name, value);
		}

		@Override
		public String addParameter(String url, String name, short value) {
			return _http.addParameter(url, name, value);
		}

		@Override
		public String addParameter(String url, String name, String value) {
			return _http.addParameter(url, name, value);
		}

		@Override
		public String decodePath(String path) {
			return _http.decodePath(path);
		}

		@Override
		public String decodeURL(String url) {
			return _http.decodeURL(url);
		}

		/**
		 * @deprecated As of Wilberforce (7.0.x), replaced by {@link
		 *             #decodeURL(String)}
		 */
		@Deprecated
		@Override
		public String decodeURL(String url, boolean unescapeSpaces) {
			return _http.decodeURL(url, unescapeSpaces);
		}

		@Override
		public String encodeParameters(String url) {
			return _http.encodeParameters(url);
		}

		@Override
		public String encodePath(String path) {
			return _http.encodePath(path);
		}

		/**
		 * @deprecated As of Judson (7.1.x), replaced by {@link
		 *             URLCodec#encodeURL(String)}
		 */
		@Deprecated
		@Override
		public String encodeURL(String url) {
			return _http.encodeURL(url);
		}

		/**
		 * @deprecated As of Judson (7.1.x), replaced by {@link
		 *             URLCodec#encodeURL(String, boolean)}
		 */
		@Deprecated
		@Override
		public String encodeURL(String url, boolean escapeSpaces) {
			return _http.encodeURL(url, escapeSpaces);
		}

		@Override
		public String fixPath(String path) {
			return _http.fixPath(path);
		}

		@Override
		public String fixPath(String path, boolean leading, boolean trailing) {
			return _http.fixPath(path, leading, trailing);
		}

		@Override
		public String getCompleteURL(HttpServletRequest request) {
			return _http.getCompleteURL(request);
		}

		@Override
		public Cookie[] getCookies() {
			return _http.getCookies();
		}

		@Override
		public String getDomain(String url) {
			return _http.getDomain(url);
		}

		@Override
		public String getIpAddress(String url) {
			return _http.getIpAddress(url);
		}

		@Override
		public String getParameter(String url, String name) {
			return _http.getParameter(url, name);
		}

		@Override
		public String getParameter(String url, String name, boolean escaped) {
			return _http.getParameter(url, name, escaped);
		}

		@Override
		public Map<String, String[]> getParameterMap(String queryString) {
			return _http.getParameterMap(queryString);
		}

		@Override
		public String getPath(String url) {
			return _http.getPath(url);
		}

		@Override
		public String getProtocol(ActionRequest actionRequest) {
			return _http.getProtocol(actionRequest);
		}

		@Override
		public String getProtocol(boolean secure) {
			return _http.getProtocol(secure);
		}

		@Override
		public String getProtocol(HttpServletRequest request) {
			return _http.getProtocol(request);
		}

		@Override
		public String getProtocol(RenderRequest renderRequest) {
			return _http.getProtocol(renderRequest);
		}

		@Override
		public String getProtocol(String url) {
			return _http.getProtocol(url);
		}

		@Override
		public String getQueryString(String url) {
			return _http.getQueryString(url);
		}

		@Override
		public String getRequestURL(HttpServletRequest request) {
			return _http.getRequestURL(request);
		}

		@Override
		public boolean hasDomain(String url) {
			return _http.hasDomain(url);
		}

		@Override
		public boolean hasProtocol(String url) {
			return _http.hasProtocol(url);
		}

		@Override
		public boolean hasProxyConfig() {
			return _http.hasProxyConfig();
		}

		@Override
		public boolean isNonProxyHost(String host) {
			return _http.isNonProxyHost(host);
		}

		@Override
		public boolean isProxyHost(String host) {
			return _http.isProxyHost(host);
		}

		@Override
		public boolean isSecure(String url) {
			return _http.isSecure(url);
		}

		@Override
		public String normalizePath(String uri) {
			return _http.normalizePath(uri);
		}

		@Override
		public Map<String, String[]> parameterMapFromString(
			String queryString) {

			return _http.parameterMapFromString(queryString);
		}

		@Override
		public String parameterMapToString(Map<String, String[]> parameterMap) {
			return _http.parameterMapToString(parameterMap);
		}

		@Override
		public String parameterMapToString(
			Map<String, String[]> parameterMap, boolean addQuestion) {

			return _http.parameterMapToString(parameterMap, addQuestion);
		}

		@Override
		public String protocolize(String url, ActionRequest actionRequest) {
			return _http.protocolize(url, actionRequest);
		}

		@Override
		public String protocolize(String url, boolean secure) {
			return _http.protocolize(url, secure);
		}

		@Override
		public String protocolize(String url, HttpServletRequest request) {
			return _http.protocolize(url, request);
		}

		@Override
		public String protocolize(String url, int port, boolean secure) {
			return _http.protocolize(url, port, secure);
		}

		@Override
		public String protocolize(String url, RenderRequest renderRequest) {
			return _http.protocolize(url, renderRequest);
		}

		@Override
		public String removeDomain(String url) {
			return _http.removeDomain(url);
		}

		@Override
		public String removeParameter(String url, String name) {
			return _http.removeParameter(url, name);
		}

		@Override
		public String removePathParameters(String uri) {
			return _http.removePathParameters(uri);
		}

		@Override
		public String removeProtocol(String url) {
			return _http.removeProtocol(url);
		}

		@Override
		public String sanitizeHeader(String header) {
			return _http.sanitizeHeader(header);
		}

		@Override
		public String setParameter(String url, String name, boolean value) {
			return _http.setParameter(url, name, value);
		}

		@Override
		public String setParameter(String url, String name, double value) {
			return _http.setParameter(url, name, value);
		}

		@Override
		public String setParameter(String url, String name, int value) {
			return _http.setParameter(url, name, value);
		}

		@Override
		public String setParameter(String url, String name, long value) {
			return _http.setParameter(url, name, value);
		}

		@Override
		public String setParameter(String url, String name, short value) {
			return _http.setParameter(url, name, value);
		}

		@Override
		public String setParameter(String url, String name, String value) {
			return _http.setParameter(url, name, value);
		}

		@Override
		public String shortenURL(String url) {
			return _http.shortenURL(url);
		}

		/**
		 * @deprecated As of Judson (7.1.x), replaced by {@link
		 * 													#shortenURL(String)}
		 */
		@Deprecated
		@Override
		public String shortenURL(String url, int count) {
			return _http.shortenURL(url, count);
		}

		@Override
		public byte[] URLtoByteArray(Options options) throws IOException {
			if (isLocationAccessDenied(options.getLocation())) {
				_log.error(
					StringBundler.concat(
						"Denied access to resource ", options.getLocation(),
						" using $httpUtil variable from a template. Please ",
						"use restricted variable $httpUtilUnsafe to access ",
						"local network."));

				return new byte[0];
			}

			return _http.URLtoByteArray(options);
		}

		@Override
		public byte[] URLtoByteArray(String location) throws IOException {
			if (isLocationAccessDenied(location)) {
				_log.error(
					StringBundler.concat(
						"Denied access to resource ", location,
						" using $httpUtil variable from a template. Please ",
						"use restricted variable $httpUtilUnsafe to access ",
						"local network."));

				return new byte[0];
			}

			return _http.URLtoByteArray(location);
		}

		@Override
		public byte[] URLtoByteArray(String location, boolean post)
			throws IOException {

			if (isLocationAccessDenied(location)) {
				_log.error(
					StringBundler.concat(
						"Denied access to resource ", location,
						" using $httpUtil variable from a template. Please ",
						"use restricted variable $httpUtilUnsafe to access ",
						"local network."));

				return new byte[0];
			}

			return _http.URLtoByteArray(location, post);
		}

		@Override
		public InputStream URLtoInputStream(Options options)
			throws IOException {

			if (isLocationAccessDenied(options.getLocation())) {
				_log.error(
					StringBundler.concat(
						"Denied access to resource ", options.getLocation(),
						" using $httpUtil variable from a template. Please ",
						"use restricted variable $httpUtilUnsafe to access ",
						"local network."));

				return new ByteArrayInputStream(new byte[0]);
			}

			return _http.URLtoInputStream(options);
		}

		@Override
		public InputStream URLtoInputStream(String location)
			throws IOException {

			if (isLocationAccessDenied(location)) {
				_log.error(
					StringBundler.concat(
						"Denied access to resource ", location,
						" using $httpUtil variable from a template. Please ",
						"use restricted variable $httpUtilUnsafe to access ",
						"local network."));

				return new ByteArrayInputStream(new byte[0]);
			}

			return _http.URLtoInputStream(location);
		}

		@Override
		public InputStream URLtoInputStream(String location, boolean post)
			throws IOException {

			if (isLocationAccessDenied(location)) {
				_log.error(
					StringBundler.concat(
						"Denied access to resource ", location,
						" using $httpUtil variable from a template. Please ",
						"use restricted variable $httpUtilUnsafe to access ",
						"local network."));

				return new ByteArrayInputStream(new byte[0]);
			}

			return _http.URLtoInputStream(location, post);
		}

		@Override
		public String URLtoString(Options options) throws IOException {
			if (isLocationAccessDenied(options.getLocation())) {
				_log.error(
					StringBundler.concat(
						"Denied access to resource ", options.getLocation(),
						" using $httpUtil variable from a template. Please ",
						"use restricted variable $httpUtilUnsafe to access ",
						"local network."));

				return StringPool.BLANK;
			}

			return _http.URLtoString(options);
		}

		@Override
		public String URLtoString(String location) throws IOException {
			if (isLocationAccessDenied(location)) {
				_log.error(
					StringBundler.concat(
						"Denied access to resource ", location,
						" using $httpUtil variable from a template. Please ",
						"use restricted variable $httpUtilUnsafe to access ",
						"local network."));

				return StringPool.BLANK;
			}

			return _http.URLtoString(location);
		}

		@Override
		public String URLtoString(String location, boolean post)
			throws IOException {

			if (isLocationAccessDenied(location)) {
				_log.error(
					StringBundler.concat(
						"Denied access to resource ", location,
						" using $httpUtil variable from a template. Please ",
						"use restricted variable $httpUtilUnsafe to access ",
						"local network."));

				return StringPool.BLANK;
			}

			return _http.URLtoString(location, post);
		}

		@Override
		public String URLtoString(URL url) throws IOException {
			String protocol = url.getProtocol();

			if (!HTTP.equals(protocol) && !HTTPS.equals(protocol)) {
				_log.error(
					StringBundler.concat(
						"Denied access to resource ", url.toString(),
						". $httpUtil template variable supports only HTTP and ",
						"HTTPS protocols."));

				return StringPool.BLANK;
			}

			if (isLocationAccessDenied(url.toString())) {
				_log.error(
					StringBundler.concat(
						"Denied access to resource ", url.toString(),
						" using $httpUtil variable from a template. Please ",
						"use restricted variable $httpUtilUnsafe to access ",
						"local network."));

				return StringPool.BLANK;
			}

			return _http.URLtoString(url);
		}

		protected boolean isLocationAccessDenied(String location)
			throws IOException {

			if (_disableLocalNetworkAccess) {
				URL url = new URL(location);

				if (InetAddressUtil.isLocalInetAddress(
						InetAddress.getByName(url.getHost()))) {

					return true;
				}
			}

			return false;
		}

		private final boolean _disableLocalNetworkAccess;
		private final Http _http;

	}

	private static class NoPACL implements PACL {

		@Override
		public TemplateControlContext getTemplateControlContext() {
			ClassLoader contextClassLoader =
				ClassLoaderUtil.getContextClassLoader();

			return new TemplateControlContext(null, contextClassLoader);
		}

	}

}