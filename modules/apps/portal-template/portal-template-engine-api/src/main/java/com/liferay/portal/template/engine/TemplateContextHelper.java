/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template.engine;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.image.ImageToolUtil_IW;
import com.liferay.portal.kernel.audit.AuditMessageFactory;
import com.liferay.portal.kernel.audit.AuditRouterUtil;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletModeFactory_IW;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletRequestModel;
import com.liferay.portal.kernel.portlet.PortletRequestModelFactory;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.WindowStateFactory_IW;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.OrganizationService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil_IW;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.service.permission.OrganizationPermissionUtil_IW;
import com.liferay.portal.kernel.service.permission.PortalPermissionUtil_IW;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil_IW;
import com.liferay.portal.kernel.service.permission.RolePermissionUtil_IW;
import com.liferay.portal.kernel.service.permission.UserPermissionUtil_IW;
import com.liferay.portal.kernel.template.TemplateContextContributor;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.template.TemplateHandlerRegistryUtil;
import com.liferay.portal.kernel.template.TemplateVariableGroup;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil_IW;
import com.liferay.portal.kernel.util.CalendarFactoryUtil_IW;
import com.liferay.portal.kernel.util.DateUtil_IW;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.GetterUtil_IW;
import com.liferay.portal.kernel.util.HtmlUtil_IW;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.InetAddressUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListMergeable;
import com.liferay.portal.kernel.util.LocaleUtil_IW;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.ParamUtil_IW;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsUtil_IW;
import com.liferay.portal.kernel.util.SessionClicks_IW;
import com.liferay.portal.kernel.util.StaticFieldGetter;
import com.liferay.portal.kernel.util.StringUtil_IW;
import com.liferay.portal.kernel.util.TimeZoneUtil_IW;
import com.liferay.portal.kernel.util.URLCodec_IW;
import com.liferay.portal.kernel.util.UnicodeFormatter_IW;
import com.liferay.portal.kernel.util.Validator_IW;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.webserver.WebServerServletTokenUtil;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.service.permission.CommonPermissionUtil_IW;
import com.liferay.portal.service.permission.PasswordPolicyPermissionUtil_IW;
import com.liferay.portal.service.permission.UserGroupPermissionUtil_IW;
import com.liferay.portal.servlet.BrowserSnifferUtil_IW;
import com.liferay.portal.struts.Definition;
import com.liferay.portal.struts.TilesUtil;
import com.liferay.portal.template.ServiceLocator;

import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import org.osgi.framework.BundleContext;

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

		TemplateVariableGroup portalServicesTemplateVariableGroup =
			new TemplateVariableGroup(
				"portal-services",
				templateHandler.getRestrictedVariables(language));

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

	public void destory() {
		_serviceTrackerList.close();
	}

	public Map<String, Object> getHelperUtilities(boolean restricted) {
		if (_helperUtilitiesMapArray == null) {
			_helperUtilitiesMapArray = (Map<String, Object>[])new Map<?, ?>[2];

			Map<String, Object> helperUtilities = new HashMap<>();

			populateCommonHelperUtilities(helperUtilities);
			populateExtraHelperUtilities(helperUtilities, false);

			_helperUtilitiesMapArray[0] = helperUtilities;

			Map<String, Object> restrictedHelperUtilities = new HashMap<>();

			populateCommonHelperUtilities(restrictedHelperUtilities);
			populateExtraHelperUtilities(restrictedHelperUtilities, true);

			Set<String> restrictedVariables = getRestrictedVariables();

			for (String restrictedVariable : restrictedVariables) {
				restrictedHelperUtilities.remove(restrictedVariable);
			}

			_helperUtilitiesMapArray[1] = restrictedHelperUtilities;
		}

		if (restricted) {
			return _helperUtilitiesMapArray[1];
		}

		return _helperUtilitiesMapArray[0];
	}

	public Set<String> getRestrictedVariables() {
		return Collections.emptySet();
	}

	public void init(BundleContext bundleContext) {
		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, TemplateContextContributor.class,
			"(type=" + TemplateContextContributor.TYPE_GLOBAL + ")");
	}

	public void prepare(
		Map<String, Object> contextObjects,
		HttpServletRequest httpServletRequest) {

		Map<String, Function<HttpServletRequest, Object>> lazyContextFunctions =
			new AbstractMap<>() {

				@Override
				public Set<Entry<String, Function<HttpServletRequest, Object>>>
					entrySet() {

					return Collections.emptySet();
				}

				@Override
				public Function<HttpServletRequest, Object> put(
					String key, Function<HttpServletRequest, Object> function) {

					Object value = function.apply(httpServletRequest);

					if (value != null) {
						contextObjects.put(key, value);
					}

					return null;
				}

			};

		// Content security policy nonce

		lazyContextFunctions.put(
			"nonceAttribute",
			ContentSecurityPolicyNonceProviderUtil::getNonceAttribute);

		// Request

		lazyContextFunctions.put(
			"request", curHttpServletRequest -> curHttpServletRequest);

		// Portlet config

		lazyContextFunctions.put(
			"portletConfig",
			curHttpServletRequest ->
				(PortletConfig)curHttpServletRequest.getAttribute(
					JavaConstants.JAKARTA_PORTLET_CONFIG));

		// Render request

		lazyContextFunctions.put(
			"renderRequest", TemplateContextHelper::_getRenderRequest);

		// Render response

		lazyContextFunctions.put(
			"renderResponse", TemplateContextHelper::_getRenderResponse);

		// XML request

		lazyContextFunctions.put(
			"portletRequestModelFactory",
			TemplateContextHelper::_getPortletRequestModelFactory);

		// Deprecated

		lazyContextFunctions.put(
			"xmlRequest", TemplateContextHelper::_getXMLRequest);

		// Theme display

		lazyContextFunctions.put(
			"bodyCssClass", TemplateContextHelper::_getBodyCssClass);

		lazyContextFunctions.put(
			"colorScheme",
			_getThemeDisplayFunction(ThemeDisplay::getColorScheme));
		lazyContextFunctions.put(
			"company", _getThemeDisplayFunction(ThemeDisplay::getCompany));
		lazyContextFunctions.put(
			"layout", _getThemeDisplayFunction(ThemeDisplay::getLayout));
		lazyContextFunctions.put(
			"layouts", _getThemeDisplayFunction(ThemeDisplay::getLayouts));
		lazyContextFunctions.put(
			"layoutTypePortlet",
			_getThemeDisplayFunction(ThemeDisplay::getLayoutTypePortlet));
		lazyContextFunctions.put(
			"locale", _getThemeDisplayFunction(ThemeDisplay::getLocale));
		lazyContextFunctions.put(
			"permissionChecker",
			_getThemeDisplayFunction(ThemeDisplay::getPermissionChecker));
		lazyContextFunctions.put(
			"plid",
			_getThemeDisplayFunction(
				curThemeDisplay -> String.valueOf(curThemeDisplay.getPlid())));
		lazyContextFunctions.put(
			"portletDisplay",
			_getThemeDisplayFunction(ThemeDisplay::getPortletDisplay));
		lazyContextFunctions.put(
			"realUser", _getThemeDisplayFunction(ThemeDisplay::getRealUser));
		lazyContextFunctions.put(
			"scopeGroupId",
			_getThemeDisplayFunction(ThemeDisplay::getScopeGroupId));
		lazyContextFunctions.put(
			"siteSpritemap",
			_getThemeDisplayFunction(ThemeDisplay::getPathThemeSpritemap));
		lazyContextFunctions.put(
			"themeDisplay",
			curHttpServletRequest ->
				(ThemeDisplay)curHttpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY));
		lazyContextFunctions.put(
			"timeZone", _getThemeDisplayFunction(ThemeDisplay::getTimeZone));

		lazyContextFunctions.put(
			"user", _getThemeDisplayFunction(TemplateContextHelper::_getUser));

		// Deprecated

		lazyContextFunctions.put(
			"portletGroupId",
			_getThemeDisplayFunction(ThemeDisplay::getScopeGroupId));

		// Theme

		lazyContextFunctions.put("theme", TemplateContextHelper::_getTheme);

		// Tiles attributes

		prepareTiles(contextObjects, httpServletRequest);

		// Page title and subtitle

		lazyContextFunctions.put(
			"pageTitle",
			curHttpServletRequest -> _mergeToString(
				curHttpServletRequest, WebKeys.PAGE_TITLE));

		lazyContextFunctions.put(
			"pageSubtitle",
			curHttpServletRequest -> _mergeToString(
				curHttpServletRequest, WebKeys.PAGE_SUBTITLE));
	}

	public void removeAllHelperUtilities() {
		_helperUtilitiesMapArray = null;
	}

	protected Iterable<TemplateContextContributor>
		getTemplateContextContributors() {

		return _serviceTrackerList;
	}

	protected void populateCommonHelperUtilities(
		Map<String, Object> variables) {

		// Array util

		variables.put("arrayUtil", ArrayUtil_IW.getInstance());

		// Audit message factory

		try {
			variables.put("auditMessageFactoryUtil", new AuditMessageFactory());
		}
		catch (SecurityException securityException) {
			_log.error(securityException);
		}

		// Audit router util

		try {
			variables.put("auditRouterUtil", AuditRouterUtil.getAuditRouter());
		}
		catch (SecurityException securityException) {
			_log.error(securityException);
		}

		// Browser sniffer

		try {
			variables.put(
				"browserSniffer", BrowserSnifferUtil_IW.getInstance());
		}
		catch (SecurityException securityException) {
			_log.error(securityException);
		}

		// Calendar factory

		variables.put("calendarFactory", CalendarFactoryUtil_IW.getInstance());

		// Date format

		try {
			variables.put(
				"dateFormatFactory",
				FastDateFormatFactoryUtil.getFastDateFormatFactory());
		}
		catch (SecurityException securityException) {
			_log.error(securityException);
		}

		// Date util

		variables.put("dateUtil", DateUtil_IW.getInstance());

		// Getter util

		variables.put("getterUtil", GetterUtil_IW.getInstance());

		// Html util

		try {
			variables.put("htmlUtil", HtmlUtil_IW.getInstance());
		}
		catch (SecurityException securityException) {
			_log.error(securityException);
		}

		// Http util

		try {
			variables.put("httpUtil", new HttpWrapper(HttpUtil.getHttp()));
		}
		catch (SecurityException securityException) {
			_log.error(securityException);
		}

		try {
			variables.put(
				"httpUtilUnsafe", new HttpWrapper(HttpUtil.getHttp(), false));
		}
		catch (SecurityException securityException) {
			_log.error(securityException);
		}

		// Image tool util

		try {
			variables.put("imageToolUtil", ImageToolUtil_IW.getInstance());
		}
		catch (SecurityException securityException) {
			_log.error(securityException);
		}

		// JSON factory util

		try {
			variables.put("jsonFactoryUtil", JSONFactoryUtil.getJSONFactory());
		}
		catch (SecurityException securityException) {
			_log.error(securityException);
		}

		// Language util

		try {
			variables.put("languageUtil", LanguageUtil.getLanguage());
		}
		catch (SecurityException securityException) {
			_log.error(securityException);
		}

		try {
			variables.put(
				"unicodeLanguageUtil", UnicodeFormatter_IW.getInstance());
		}
		catch (SecurityException securityException) {
			_log.error(securityException);
		}

		// Locale util

		try {
			variables.put("localeUtil", LocaleUtil_IW.getInstance());
		}
		catch (SecurityException securityException) {
			_log.error(securityException);
		}

		// Content security policy nonce

		variables.put("nonceAttribute", StringPool.BLANK);

		// Param util

		variables.put("paramUtil", ParamUtil_IW.getInstance());

		// Portal util

		try {
			variables.put("portalUtil", PortalUtil.getPortal());
		}
		catch (SecurityException securityException) {
			_log.error(securityException);
		}

		try {
			variables.put("portal", PortalUtil.getPortal());
		}
		catch (SecurityException securityException) {
			_log.error(securityException);
		}

		// Prefs props util

		try {
			variables.put("prefsPropsUtil", PrefsPropsUtil.getPrefsProps());
		}
		catch (SecurityException securityException) {
			_log.error(securityException);
		}

		// Props util

		try {
			variables.put("propsUtil", PropsUtil_IW.getInstance());
		}
		catch (SecurityException securityException) {
			_log.error(securityException);
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
		catch (SecurityException securityException) {
			_log.error(securityException);
		}

		// SAX reader util

		variables.put("saxReaderUtil", SAXReaderUtil.getSAXReader());

		// Service locator

		try {
			variables.put("serviceLocator", ServiceLocator.getInstance());
		}
		catch (SecurityException securityException) {
			_log.error(securityException);
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

		// URL codec

		variables.put("urlCodec", URLCodec_IW.getInstance());

		// Validator

		variables.put("validator", Validator_IW.getInstance());

		// Web server servlet token

		try {
			variables.put(
				"webServerToken",
				WebServerServletTokenUtil.getWebServerServletToken());
		}
		catch (SecurityException securityException) {
			_log.error(securityException);
		}

		// Window state factory

		variables.put(
			"windowStateFactory", WindowStateFactory_IW.getInstance());

		// Permissions

		try {
			variables.put(
				"commonPermission", CommonPermissionUtil_IW.getInstance());
		}
		catch (SecurityException securityException) {
			_log.error(securityException);
		}

		try {
			variables.put(
				"groupPermission", GroupPermissionUtil_IW.getInstance());
		}
		catch (SecurityException securityException) {
			_log.error(securityException);
		}

		try {
			variables.put(
				"layoutPermission", LayoutPermissionUtil.getLayoutPermission());
		}
		catch (SecurityException securityException) {
			_log.error(securityException);
		}

		try {
			variables.put(
				"organizationPermission",
				OrganizationPermissionUtil_IW.getInstance());
		}
		catch (SecurityException securityException) {
			_log.error(securityException);
		}

		try {
			variables.put(
				"passwordPolicyPermission",
				PasswordPolicyPermissionUtil_IW.getInstance());
		}
		catch (SecurityException securityException) {
			_log.error(securityException);
		}

		try {
			variables.put(
				"portalPermission", PortalPermissionUtil_IW.getInstance());
		}
		catch (SecurityException securityException) {
			_log.error(securityException);
		}

		try {
			variables.put(
				"portletPermission", PortletPermissionUtil_IW.getInstance());
		}
		catch (SecurityException securityException) {
			_log.error(securityException);
		}

		Map<String, PortletProvider.Action> portletProviderActionMap =
			new HashMap<>();

		for (PortletProvider.Action action : PortletProvider.Action.values()) {
			portletProviderActionMap.put(action.name(), action);
		}

		try {
			variables.put("portletProviderAction", portletProviderActionMap);
		}
		catch (SecurityException securityException) {
			_log.error(securityException);
		}

		try {
			variables.put(
				"rolePermission", RolePermissionUtil_IW.getInstance());
		}
		catch (SecurityException securityException) {
			_log.error(securityException);
		}

		try {
			variables.put(
				"userGroupPermission",
				UserGroupPermissionUtil_IW.getInstance());
		}
		catch (SecurityException securityException) {
			_log.error(securityException);
		}

		try {
			variables.put(
				"userPermission", UserPermissionUtil_IW.getInstance());
		}
		catch (SecurityException securityException) {
			_log.error(securityException);
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
		catch (SecurityException securityException) {
			_log.error(securityException);
		}

		try {
			variables.put(
				"imageToken",
				WebServerServletTokenUtil.getWebServerServletToken());
		}
		catch (SecurityException securityException) {
			_log.error(securityException);
		}

		try {
			variables.put(
				"locationPermission",
				OrganizationPermissionUtil_IW.getInstance());
		}
		catch (SecurityException securityException) {
			_log.error(securityException);
		}

		try {
			variables.put("random", new Random());
		}
		catch (SecurityException securityException) {
			_log.error(securityException);
		}
	}

	protected void populateExtraHelperUtilities(
		Map<String, Object> variables, boolean restricted) {
	}

	protected void prepareTiles(
		Map<String, Object> contextObjects,
		HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Definition definition = (Definition)httpServletRequest.getAttribute(
			TilesUtil.DEFINITION);

		if (definition == null) {
			themeDisplay.setTilesSelectable(true);

			return;
		}

		Map<String, String> attributes = definition.getAttributes();

		String tilesTitle = attributes.get("title");

		themeDisplay.setTilesTitle(tilesTitle);

		contextObjects.put("tilesTitle", tilesTitle);

		String tilesContent = attributes.get("content");

		themeDisplay.setTilesContent(tilesContent);

		contextObjects.put("tilesContent", tilesContent);

		boolean tilesSelectable = GetterUtil.getBoolean(
			attributes.get("selectable"));

		themeDisplay.setTilesSelectable(tilesSelectable);

		contextObjects.put("tilesSelectable", tilesSelectable);
	}

	private static Object _getBodyCssClass(
		HttpServletRequest httpServletRequest) {

		if (httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY) == null) {
			return null;
		}

		HttpServletRequest originalHttpServletRequest =
			PortalUtil.getOriginalServletRequest(httpServletRequest);

		String namespace = PortalUtil.getPortletNamespace(
			ParamUtil.getString(httpServletRequest, "p_p_id"));

		return ParamUtil.getString(
			originalHttpServletRequest, namespace + "bodyCssClass");
	}

	private static Object _getPortletRequestModelFactory(
		HttpServletRequest httpServletRequest) {

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST);
		PortletResponse portletResponse =
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);

		if ((portletRequest == null) || (portletResponse == null)) {
			return null;
		}

		return new PortletRequestModelFactory(portletRequest, portletResponse);
	}

	private static Object _getRenderRequest(
		HttpServletRequest httpServletRequest) {

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST);

		if ((portletRequest != null) &&
			(portletRequest instanceof RenderRequest)) {

			return portletRequest;
		}

		return null;
	}

	private static Object _getRenderResponse(
		HttpServletRequest httpServletRequest) {

		PortletResponse portletResponse =
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);

		if ((portletResponse != null) &&
			(portletResponse instanceof RenderResponse)) {

			return portletResponse;
		}

		return null;
	}

	private static Object _getTheme(HttpServletRequest httpServletRequest) {
		Theme theme = (Theme)httpServletRequest.getAttribute(WebKeys.THEME);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if ((theme == null) && (themeDisplay != null)) {
			theme = themeDisplay.getTheme();
		}

		return theme;
	}

	private static Object _getUser(ThemeDisplay themeDisplay) {
		User user = themeDisplay.getUser();

		long userId = PrincipalThreadLocal.getUserId();

		if ((user == null) || (user.getUserId() != userId)) {
			User principalUser = UserLocalServiceUtil.fetchUser(userId);

			if (principalUser != null) {
				user = principalUser;
			}
		}

		return user;
	}

	private static Object _getXMLRequest(
		HttpServletRequest httpServletRequest) {

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST);
		PortletResponse portletResponse =
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);

		if ((portletRequest == null) || (portletResponse == null)) {
			return null;
		}

		return new Object() {

			@Override
			public String toString() {
				PortletRequestModel portletRequestModel =
					new PortletRequestModel(portletRequest, portletResponse);

				return portletRequestModel.toXML();
			}

		};
	}

	private Function<HttpServletRequest, Object> _getThemeDisplayFunction(
		Function<ThemeDisplay, Object> function) {

		return httpServletRequest -> {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			if (themeDisplay == null) {
				return null;
			}

			return function.apply(themeDisplay);
		};
	}

	private Object _mergeToString(
		HttpServletRequest httpServletRequest, String name) {

		ListMergeable<String> listMergeable =
			(ListMergeable<String>)httpServletRequest.getAttribute(name);

		if (listMergeable == null) {
			return null;
		}

		return listMergeable.mergeToString(StringPool.SPACE);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TemplateContextHelper.class);

	private Map<String, Object>[] _helperUtilitiesMapArray;
	private ServiceTrackerList<TemplateContextContributor> _serviceTrackerList;

	private static class HttpWrapper implements Http {

		public HttpWrapper(Http http) {
			this(http, true);
		}

		public HttpWrapper(Http http, boolean disableLocalNetworkAccess) {
			_http = http;
			_disableLocalNetworkAccess = disableLocalNetworkAccess;
		}

		@Override
		public byte[] URLtoByteArray(Options options) throws IOException {
			if (isLocationAccessDenied(options.getLocation())) {
				throw new IOException(
					StringBundler.concat(
						"Denied access to resource ", options.getLocation(),
						" using $httpUtil variable from a template. Please ",
						"use restricted variable $httpUtilUnsafe to access ",
						"local network."));
			}

			options.setFollowRedirects(false);

			return _http.URLtoByteArray(options);
		}

		@Override
		public byte[] URLtoByteArray(String location) throws IOException {
			Options options = new Options();

			options.setLocation(location);

			return URLtoByteArray(options);
		}

		@Override
		public byte[] URLtoByteArray(String location, boolean post)
			throws IOException {

			Options options = new Options();

			options.setLocation(location);
			options.setPost(post);

			return URLtoByteArray(options);
		}

		@Override
		public InputStream URLtoInputStream(Options options)
			throws IOException {

			if (isLocationAccessDenied(options.getLocation())) {
				throw new IOException(
					StringBundler.concat(
						"Denied access to resource ", options.getLocation(),
						" using $httpUtil variable from a template. Please ",
						"use restricted variable $httpUtilUnsafe to access ",
						"local network."));
			}

			options.setFollowRedirects(false);

			return _http.URLtoInputStream(options);
		}

		@Override
		public InputStream URLtoInputStream(String location)
			throws IOException {

			Options options = new Options();

			options.setLocation(location);

			return URLtoInputStream(options);
		}

		@Override
		public InputStream URLtoInputStream(String location, boolean post)
			throws IOException {

			Options options = new Options();

			options.setLocation(location);
			options.setPost(post);

			return URLtoInputStream(options);
		}

		@Override
		public String URLtoString(Options options) throws IOException {
			if (isLocationAccessDenied(options.getLocation())) {
				throw new IOException(
					StringBundler.concat(
						"Denied access to resource ", options.getLocation(),
						" using $httpUtil variable from a template. Please ",
						"use restricted variable $httpUtilUnsafe to access ",
						"local network."));
			}

			options.setFollowRedirects(false);

			return _http.URLtoString(options);
		}

		@Override
		public String URLtoString(String location) throws IOException {
			Options options = new Options();

			options.setLocation(location);

			return URLtoString(options);
		}

		@Override
		public String URLtoString(String location, boolean post)
			throws IOException {

			Options options = new Options();

			options.setLocation(location);
			options.setPost(post);

			return URLtoString(options);
		}

		@Override
		public String URLtoString(URL url) throws IOException {
			String protocol = url.getProtocol();

			if (!HTTP.equals(protocol) && !HTTPS.equals(protocol)) {
				throw new IOException(
					StringBundler.concat(
						"Denied access to resource ", url,
						". $httpUtil template variable supports only HTTP and ",
						"HTTPS protocols."));
			}

			if (isLocationAccessDenied(url.toString())) {
				throw new IOException(
					StringBundler.concat(
						"Denied access to resource ", url,
						" using $httpUtil variable from a template. Please ",
						"use restricted variable $httpUtilUnsafe to access ",
						"local network."));
			}

			Options options = new Options();

			options.setFollowRedirects(false);
			options.setLocation(url.toString());

			return URLtoString(options);
		}

		@Override
		public Cookie[] getCookies() {
			return _http.getCookies();
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

		protected boolean isLocationAccessDenied(String location)
			throws IOException {

			if (_disableLocalNetworkAccess) {
				URL url = new URL(location);

				if (InetAddressUtil.isLocalInetAddress(
						InetAddressUtil.getInetAddressByName(url.getHost()))) {

					return true;
				}
			}

			return false;
		}

		private final boolean _disableLocalNetworkAccess;
		private final Http _http;

	}

}