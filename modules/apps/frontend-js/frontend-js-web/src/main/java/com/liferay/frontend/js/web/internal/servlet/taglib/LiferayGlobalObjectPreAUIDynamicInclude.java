/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.internal.servlet.taglib;

import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.layout.seo.kernel.LayoutSEOLink;
import com.liferay.layout.seo.kernel.LayoutSEOLinkManager;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlag;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManager;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.impl.VirtualLayout;
import com.liferay.portal.kernel.security.auth.AuthToken;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.LayoutPermission;
import com.liferay.portal.kernel.servlet.BrowserMetadata;
import com.liferay.portal.kernel.servlet.PortalWebResourceConstants;
import com.liferay.portal.kernel.servlet.PortalWebResourcesUtil;
import com.liferay.portal.kernel.servlet.taglib.BaseDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.configuration.UploadServletRequestConfigurationProvider;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactory;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PrefsProps;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.UnicodeFormatter;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.servlet.BrowserSnifferUtil;
import com.liferay.portal.util.ShutdownUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;

import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera Avellón
 */
@Component(
	property = "service.ranking:Integer=" + Integer.MAX_VALUE,
	service = DynamicInclude.class
)
public class LiferayGlobalObjectPreAUIDynamicInclude
	extends BaseDynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		PrintWriter printWriter = httpServletResponse.getWriter();

		printWriter.print("<script");
		printWriter.print(
			ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(
				httpServletRequest));
		printWriter.print(" data-senna-track=\"temporary\"");
		printWriter.println(" type=\"text/javascript\">");

		StringBundler sb = new StringBundler();

		try {
			_renderLiferayAUI(httpServletRequest, sb);
			_renderLiferayBrowser(httpServletRequest, sb);
			_renderLiferayData(httpServletRequest, sb);
			_renderLiferayFeatureFlags(httpServletRequest, sb);
			_renderLiferayLanguage(sb);
			_renderLiferayPortlet(sb);
			_renderLiferayPortletKeys(sb);
			_renderLiferayPropsValues(httpServletRequest, sb);
			_renderLiferayThemeDisplay(httpServletRequest, sb);
			_renderLiferayUtil(sb);

			_renderValue(
				"authToken", sb, _authToken.getToken(httpServletRequest));

			String currentURL = _portal.getCurrentURL(httpServletRequest);

			_renderValue("currentURL", sb, HtmlUtil.escapeJS(currentURL));
			_renderValue(
				"currentURLEncoded", sb,
				HtmlUtil.escapeJS(URLCodec.encodeURL(currentURL)));
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to render Liferay global object", portalException);
		}

		String requestURL = String.valueOf(httpServletRequest.getRequestURL());

		printWriter.println(
			StringUtil.replace(
				_TPL_LIFERAY_JS,
				new String[] {"[$DEFINITION$]", "[$DEV_MODE$]"},
				new Object[] {sb, requestURL.startsWith("http://localhost")}));

		printWriter.println("</script>");
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(
			"/html/common/themes/top_js.jspf#resources");
	}

	private static String _read(String name) {
		try (InputStream inputStream =
				LiferayGlobalObjectPreAUIDynamicInclude.class.
					getResourceAsStream("dependencies/" + name)) {

			return StringUtil.read(inputStream);
		}
		catch (Exception exception) {
			_log.error("Unable to read template " + name, exception);
		}

		return StringPool.BLANK;
	}

	private String _getDateFormatPattern(Locale locale) {
		String languageId = LocaleUtil.toLanguageId(locale);

		String dateFormatPattern = _dateFormatPatterns.get(languageId);

		if (dateFormatPattern != null) {
			return dateFormatPattern;
		}

		SimpleDateFormat simpleDateFormat =
			(SimpleDateFormat)DateFormat.getDateInstance(
				DateFormat.SHORT, locale);

		dateFormatPattern = simpleDateFormat.toPattern();

		String delimiterString = StringPool.FORWARD_SLASH;
		boolean endDelimiter = false;

		for (char dateDelimiter : _DATE_DELIMITERS) {
			if (dateFormatPattern.indexOf(dateDelimiter) != -1) {
				delimiterString = String.valueOf(dateDelimiter);

				endDelimiter = dateFormatPattern.endsWith(delimiterString);

				break;
			}
		}

		int dayIndex = dateFormatPattern.indexOf('d');
		int monthIndex = dateFormatPattern.indexOf('M');
		int yearIndex = dateFormatPattern.indexOf('y');

		if ((yearIndex < dayIndex) && (yearIndex < monthIndex)) {
			dateFormatPattern = StringBundler.concat(
				"%Y", delimiterString, "%m", delimiterString, "%d");
		}
		else if (dayIndex < monthIndex) {
			dateFormatPattern = StringBundler.concat(
				"%d", delimiterString, "%m", delimiterString, "%Y");
		}
		else {
			dateFormatPattern = StringBundler.concat(
				"%m", delimiterString, "%d", delimiterString, "%Y");
		}

		if (endDelimiter) {
			dateFormatPattern += delimiterString;
		}

		_dateFormatPatterns.put(languageId, dateFormatPattern);

		return dateFormatPattern;
	}

	private void _renderLiferayAUI(
		HttpServletRequest httpServletRequest, StringBundler sb) {

		sb.append("AUI: {\n");

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		_renderMethod("getCombine", sb, themeDisplay.isThemeJsFastLoad());

		long lastModified = PortalWebResourcesUtil.getLastModified(
			PortalWebResourceConstants.RESOURCE_TYPE_JS);

		String comboURL = _portal.getStaticResourceURL(
			httpServletRequest,
			themeDisplay.getCDNDynamicResourcesHost() +
				themeDisplay.getPathContext() + "/combo/",
			"minifierType=", lastModified);

		_renderMethod("getComboPath", sb, comboURL + "&");

		_renderMethod(
			"getDateFormat", sb,
			_getDateFormatPattern(themeDisplay.getLocale()));
		_renderMethod(
			"getEditorCKEditorPath", sb,
			PortalWebResourcesUtil.getContextPath(
				PortalWebResourceConstants.RESOURCE_TYPE_EDITOR_CKEDITOR));

		String filterString = "raw";

		if (themeDisplay.isThemeJsFastLoad()) {
			filterString = "min";
		}
		else if (PropsValues.JAVASCRIPT_LOG_ENABLED) {
			filterString = "debug";
		}

		_renderMethod("getFilter", sb, filterString);

		String staticResourceURLParams = _portal.getStaticResourceURL(
			httpServletRequest, StringPool.BLANK, "minifierType=",
			lastModified);

		if (themeDisplay.isThemeJsFastLoad()) {
			_renderMethod("getFilterConfig", sb, null);
		}
		else {
			sb.append("getFilterConfig: () => ({\n");

			_renderMethod("replaceStr", sb, ".js" + staticResourceURLParams);
			_renderMethod("searchExp", sb, "\\\\.js$");

			sb.append("}),\n");
		}

		_renderMethod(
			"getJavaScriptRootPath", sb, themeDisplay.getPathJavaScript());
		_renderMethod(
			"getPortletRootPath", sb,
			themeDisplay.getPathContext() + "/html/portlet");
		_renderMethod(
			"getStaticResourceURLParams", sb, staticResourceURLParams);

		sb.append("},\n");
	}

	private void _renderLiferayBrowser(
		HttpServletRequest httpServletRequest, StringBundler sb) {

		sb.append("Browser: {\n");

		_renderMethod(
			"acceptsGzip", sb,
			BrowserSnifferUtil.acceptsGzip(httpServletRequest));

		String version = BrowserSnifferUtil.getVersion(httpServletRequest);

		_renderMethod("getMajorVersion", sb, version.isEmpty() ? "0" : version);

		_renderMethod(
			"getRevision", sb,
			BrowserSnifferUtil.getRevision(httpServletRequest));
		_renderMethod("getVersion", sb, version);

		BrowserMetadata browserMetadata = BrowserSnifferUtil.getBrowserMetadata(
			httpServletRequest);

		_renderMethod("isAir", sb, browserMetadata.isAir());
		_renderMethod("isChrome", sb, browserMetadata.isChrome());
		_renderMethod("isEdge", sb, browserMetadata.isEdge());
		_renderMethod("isFirefox", sb, browserMetadata.isFirefox());
		_renderMethod("isGecko", sb, browserMetadata.isGecko());
		_renderMethod("isIe", sb, browserMetadata.isIe());
		_renderMethod("isIphone", sb, browserMetadata.isIphone());
		_renderMethod("isLinux", sb, browserMetadata.isLinux());
		_renderMethod("isMac", sb, browserMetadata.isMac());
		_renderMethod("isMobile", sb, browserMetadata.isMobile());
		_renderMethod("isMozilla", sb, browserMetadata.isMozilla());
		_renderMethod("isOpera", sb, browserMetadata.isOpera());
		_renderMethod("isRtf", sb, browserMetadata.isRtf(version));
		_renderMethod("isSafari", sb, browserMetadata.isSafari());
		_renderMethod("isSun", sb, browserMetadata.isSun());
		_renderMethod("isWebKit", sb, browserMetadata.isWebKit());
		_renderMethod("isWindows", sb, browserMetadata.isWindows());

		sb.append("},\n");
	}

	private void _renderLiferayData(
			HttpServletRequest httpServletRequest, StringBundler sb)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		sb.append("Data: {\n");

		_renderValue("ICONS_INLINE_SVG", sb, true);
		_renderValue("NAV_SELECTOR", sb, "#navigation");
		_renderValue("NAV_SELECTOR_MOBILE", sb, "#navigationCollapse");

		LayoutTypePortlet layoutTypePortlet =
			themeDisplay.getLayoutTypePortlet();

		_renderMethod(
			"isCustomizationView", sb,
			layoutTypePortlet.isCustomizable() &&
			_layoutPermission.contains(
				themeDisplay.getPermissionChecker(), themeDisplay.getLayout(),
				ActionKeys.CUSTOMIZE));

		sb.append("notices: [\n");

		if (ShutdownUtil.isInProcess()) {
			sb.append("{\nmessage: '");

			Locale locale = themeDisplay.getLocale();

			sb.append(
				_language.format(
					locale,
					_language.get(
						locale,
						"the-portal-will-shutdown-for-maintenance-in-x-" +
							"minutes"),
					new Object[] {
						String.valueOf(
							(ShutdownUtil.getInProcess() / Time.MINUTE) + 1)
					},
					false));

			if (Validator.isNotNull(ShutdownUtil.getMessage())) {
				sb.append("<span class=\"custom-shutdown-message\">");
				sb.append(HtmlUtil.escape(ShutdownUtil.getMessage()));
				sb.append("</span>");
			}

			sb.append("',\ntitle: '");

			sb.append(_language.get(locale, "maintenance-alert"));

			sb.append("<span class=\"mx-2\">");

			Format format = _fastDateFormatFactory.getTime(locale);

			TimeZone timeZone = themeDisplay.getTimeZone();

			sb.append(
				format.format(
					Time.getDate(CalendarFactoryUtil.getCalendar(timeZone))));

			sb.append(StringPool.SPACE);

			sb.append(timeZone.getDisplayName(false, TimeZone.SHORT, locale));

			sb.append("</span>',\ntype: 'warning'\n},\n");
		}

		sb.append("],\n},\n");
	}

	private void _renderLiferayFeatureFlags(
		HttpServletRequest httpServletRequest, StringBundler sb) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		sb.append("FeatureFlags: {\n");

		for (FeatureFlag featureFlag :
				_featureFlagManager.getFeatureFlags(
					themeDisplay.getCompanyId(), null)) {

			sb.append(StringPool.APOSTROPHE);
			sb.append(featureFlag.getKey());
			sb.append("': ");
			sb.append(featureFlag.isEnabled());
			sb.append(",\n");
		}

		sb.append("},\n");
	}

	private void _renderLiferayLanguage(StringBundler sb) {
		StringBundler availableSB = new StringBundler();
		StringBundler directionSB = new StringBundler();

		for (Locale locale : _language.getAvailableLocales()) {
			String languageId = LocaleUtil.toLanguageId(locale);

			availableSB.append(StringPool.APOSTROPHE);
			availableSB.append(languageId);
			availableSB.append("': '");
			availableSB.append(
				_displayNames.computeIfAbsent(
					locale, key -> HtmlUtil.escapeJS(key.getDisplayName(key))));
			availableSB.append("',\n");

			directionSB.append(StringPool.APOSTROPHE);
			directionSB.append(languageId);
			directionSB.append("': '");
			directionSB.append(_language.get(locale, "lang.dir"));
			directionSB.append("',\n");
		}

		sb.append(
			StringUtil.replace(
				_TPL_LANGUAGE_JS,
				new String[] {"[$AVAILABLE$]", "[$DIRECTION$]"},
				new Object[] {availableSB, directionSB}));
		sb.append(StringPool.NEW_LINE);
	}

	private void _renderLiferayPortlet(StringBundler sb) {
		sb.append("Portlet: {\n");

		_renderStub(
			"frontend-js-components-web", "openModal", sb, "openPortletModal");
		_renderStub(
			"frontend-js-components-web", "openWindow", sb,
			"openPortletWindow");

		sb.append("},\n");
	}

	private void _renderLiferayPortletKeys(StringBundler sb) {
		sb.append("PortletKeys: {\n");

		_renderValue("DOCUMENT_LIBRARY", sb, PortletKeys.DOCUMENT_LIBRARY);
		_renderValue(
			"DYNAMIC_DATA_MAPPING", sb,
			"com_liferay_dynamic_data_mapping_web_portlet_DDMPortlet");
		_renderValue(
			"INSTANCE_SETTINGS", sb,
			"com_liferay_configuration_admin_web_portlet_" +
				"InstanceSettingsPortlet");
		_renderValue("ITEM_SELECTOR", sb, PortletKeys.ITEM_SELECTOR);

		sb.append("},\n");
	}

	private void _renderLiferayPropsValues(
		HttpServletRequest httpServletRequest, StringBundler sb) {

		sb.append("PropsValues: {\n");

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		_renderValue(
			"JAVASCRIPT_SINGLE_PAGE_APPLICATION_TIMEOUT", sb,
			_prefsProps.getInteger(
				themeDisplay.getCompanyId(),
				PropsKeys.JAVASCRIPT_SINGLE_PAGE_APPLICATION_TIMEOUT,
				PropsValues.JAVASCRIPT_SINGLE_PAGE_APPLICATION_TIMEOUT));

		_renderValue(
			"UPLOAD_SERVLET_REQUEST_IMPL_MAX_SIZE", sb,
			_uploadServletRequestConfigurationProvider.getMaxSize());

		sb.append("},\n");
	}

	private void _renderLiferayThemeDisplay(
			HttpServletRequest httpServletRequest, StringBundler sb)
		throws PortalException {

		sb.append("ThemeDisplay: {\n");

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		if (layout != null) {
			_renderMethod("getLayoutId", sb, layout.getLayoutId());

			Layout controlPanelLayout = themeDisplay.getControlPanelLayout();

			_renderMethod(
				"getLayoutRelativeControlPanelURL", sb,
				_portal.getLayoutRelativeURL(
					new VirtualLayout(
						controlPanelLayout, themeDisplay.getScopeGroup()),
					themeDisplay));

			_renderMethod(
				"getLayoutRelativeURL", sb,
				_portal.getLayoutRelativeURL(layout, themeDisplay));
			_renderMethod(
				"getLayoutURL", sb, _portal.getLayoutURL(layout, themeDisplay));
			_renderMethod("getParentLayoutId", sb, layout.getParentLayoutId());
			_renderMethod(
				"getPathFriendlyURLPublic", sb,
				_portal.getPathFriendlyURLPublic());
			_renderMethod("isControlPanel", sb, layout.isTypeControlPanel());
			_renderMethod("isPrivateLayout", sb, layout.isPrivateLayout());
			_renderMethod(
				"isVirtualLayout", sb, layout instanceof VirtualLayout);
		}

		_renderMethod(
			"getBCP47LanguageId", sb,
			_language.getBCP47LanguageId(httpServletRequest));

		String completeURL = _portal.getCurrentCompleteURL(httpServletRequest);

		LayoutSEOLink layoutSEOLink =
			_layoutSEOLinkManager.getCanonicalLayoutSEOLink(
				layout, themeDisplay.getLocale(),
				_portal.getCanonicalURL(
					completeURL, themeDisplay, layout, false, false),
				themeDisplay);

		_renderMethod(
			"getCanonicalURL", sb, HtmlUtil.escapeJS(layoutSEOLink.getHref()));

		_renderMethod("getCDNBaseURL", sb, themeDisplay.getCDNBaseURL());
		_renderMethod(
			"getCDNDynamicResourcesHost", sb,
			themeDisplay.getCDNDynamicResourcesHost());
		_renderMethod("getCDNHost", sb, themeDisplay.getCDNHost());
		_renderMethod(
			"getCompanyGroupId", sb, themeDisplay.getCompanyGroupId());
		_renderMethod("getCompanyId", sb, themeDisplay.getCompanyId());
		_renderMethod(
			"getDefaultLanguageId", sb,
			LocaleUtil.toLanguageId(themeDisplay.getSiteDefaultLocale()));
		_renderMethod(
			"getDoAsUserIdEncoded", sb,
			UnicodeFormatter.toString(themeDisplay.getDoAsUserId()));
		_renderMethod(
			"getLanguageId", sb, _language.getLanguageId(httpServletRequest));
		_renderMethod("getParentGroupId", sb, themeDisplay.getSiteGroupId());
		_renderMethod("getPathContext", sb, themeDisplay.getPathContext());
		_renderMethod("getPathImage", sb, themeDisplay.getPathImage());
		_renderMethod(
			"getPathJavaScript", sb, themeDisplay.getPathJavaScript());
		_renderMethod("getPathMain", sb, themeDisplay.getPathMain());
		_renderMethod(
			"getPathThemeImages", sb, themeDisplay.getPathThemeImages());
		_renderMethod("getPathThemeRoot", sb, themeDisplay.getPathThemeRoot());
		_renderMethod("getPlid", sb, themeDisplay.getPlid());
		_renderMethod("getPortalURL", sb, themeDisplay.getPortalURL());
		_renderMethod("getRealUserId", sb, themeDisplay.getRealUserId());
		_renderMethod("getRemoteAddr", sb, themeDisplay.getRemoteAddr());
		_renderMethod("getRemoteHost", sb, themeDisplay.getRemoteHost());

		Group scopeGroup = themeDisplay.getScopeGroup();

		_renderMethod("getScopeGroupId", sb, scopeGroup.getGroupId());

		Group liveGroup = _staging.getLiveGroup(scopeGroup);

		_renderMethod(
			"getScopeGroupIdOrLiveGroupId", sb, liveGroup.getGroupId());

		HttpSession httpSession = httpServletRequest.getSession(true);

		_renderMethod(
			"getSessionId", sb,
			PropsValues.SESSION_ENABLE_URL_WITH_SESSION_ID ?
				httpSession.getId() : StringPool.BLANK);

		_renderMethod(
			"getSiteAdminURL", sb,
			_portal.getSiteAdminURL(themeDisplay, StringPool.BLANK, null));
		_renderMethod("getSiteGroupId", sb, themeDisplay.getSiteGroupId());

		TimeZone timeZone = themeDisplay.getTimeZone();

		if (timeZone != null) {
			_renderMethod("getTimeZone", sb, timeZone.getID());
		}
		else {
			TimeZone defaultTimeZone = TimeZone.getDefault();

			_renderMethod("getTimeZone", sb, defaultTimeZone.getID());
		}

		_renderMethod(
			"getURLControlPanel", sb, themeDisplay.getURLControlPanel());
		_renderMethod(
			"getURLHome", sb, HtmlUtil.escapeJS(themeDisplay.getURLHome()));

		User user = themeDisplay.getUser();

		_renderMethod(
			"getUserEmailAddress", sb,
			themeDisplay.isSignedIn() ?
				HtmlUtil.escapeJS(user.getEmailAddress()) : StringPool.BLANK);

		_renderMethod("getUserId", sb, themeDisplay.getUserId());
		_renderMethod(
			"getUserName", sb,
			themeDisplay.isSignedIn() ?
				UnicodeFormatter.toString(user.getFullName()) :
					StringPool.BLANK);
		_renderMethod(
			"isAddSessionIdToURL", sb, themeDisplay.isAddSessionIdToURL());
		_renderMethod("isImpersonated", sb, themeDisplay.isImpersonated());
		_renderMethod("isSignedIn", sb, themeDisplay.isSignedIn());
		_renderMethod(
			"isStagedPortlet", sb,
			Validator.isNotNull(themeDisplay.getPpid()) ?
				liveGroup.isStagedPortlet(themeDisplay.getPpid()) : false);
		_renderMethod("isStateExclusive", sb, themeDisplay.isStateExclusive());
		_renderMethod("isStateMaximized", sb, themeDisplay.isStateMaximized());
		_renderMethod("isStatePopUp", sb, themeDisplay.isStatePopUp());

		sb.append("},\n");
	}

	private void _renderLiferayUtil(StringBundler sb) {
		sb.append("Util: {\n");
		sb.append(_TPL_WINDOW_JS);
		sb.append(StringPool.NEW_LINE);

		_renderStub(
			"frontend-js-components-web", "openAlertModal", sb,
			"openAlertModal");
		_renderStub(
			"frontend-js-components-web", "openConfirmModal", sb,
			"openConfirmModal");
		_renderStub("frontend-js-components-web", "openModal", sb, "openModal");
		_renderStub(
			"frontend-js-components-web", "openSelectionModal", sb,
			"openSelectionModal");
		_renderStub(
			"frontend-js-components-web", "openSimpleInputModal", sb,
			"openSimpleInputModal");
		_renderStub("frontend-js-components-web", "openToast", sb, "openToast");

		sb.append("},\n");
	}

	private void _renderMethod(
		String methodName, StringBundler sb, Object value) {

		sb.append(methodName);
		sb.append(": () => ");

		if (value == null) {
			sb.append("null");
		}
		else if (value instanceof Number || value instanceof String) {
			sb.append(StringPool.APOSTROPHE);
			sb.append(value);
			sb.append(StringPool.APOSTROPHE);
		}
		else {
			sb.append(value);
		}

		sb.append(",\n");
	}

	private void _renderStub(
		String contextPath, String methodName, StringBundler sb,
		String symbol) {

		sb.append(methodName);
		sb.append(": buildESMStub('");
		sb.append(contextPath);
		sb.append("', '");
		sb.append(symbol);
		sb.append("'),\n");
	}

	private void _renderValue(
		String fieldName, StringBundler sb, Object value) {

		sb.append(fieldName);
		sb.append(": ");

		if (value == null) {
			sb.append("null");
		}
		else if (value instanceof String) {
			sb.append(StringPool.APOSTROPHE);
			sb.append((String)value);
			sb.append(StringPool.APOSTROPHE);
		}
		else {
			sb.append(value);
		}

		sb.append(",\n");
	}

	private static final char[] _DATE_DELIMITERS = {
		CharPool.DASH, CharPool.FORWARD_SLASH, CharPool.PERIOD
	};

	private static final String _TPL_LANGUAGE_JS;

	private static final String _TPL_LIFERAY_JS;

	private static final String _TPL_WINDOW_JS;

	private static final Log _log = LogFactoryUtil.getLog(
		LiferayGlobalObjectPreAUIDynamicInclude.class);

	private static final Map<String, String> _dateFormatPatterns =
		new ConcurrentHashMap<>();

	static {
		_TPL_LANGUAGE_JS = _read("language.js.tpl");
		_TPL_LIFERAY_JS = _read("liferay.js.tpl");
		_TPL_WINDOW_JS = _read("window.js.tpl");
	}

	@Reference
	private AuthToken _authToken;

	private final Map<Locale, String> _displayNames = new ConcurrentHashMap<>();

	@Reference
	private FastDateFormatFactory _fastDateFormatFactory;

	@Reference
	private FeatureFlagManager _featureFlagManager;

	@Reference
	private Language _language;

	@Reference
	private LayoutPermission _layoutPermission;

	@Reference
	private LayoutSEOLinkManager _layoutSEOLinkManager;

	@Reference
	private Portal _portal;

	@Reference
	private PrefsProps _prefsProps;

	@Reference
	private Staging _staging;

	@Reference
	private UploadServletRequestConfigurationProvider
		_uploadServletRequestConfigurationProvider;

}