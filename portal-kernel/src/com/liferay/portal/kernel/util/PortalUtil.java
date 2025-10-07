/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutQueryStringComposite;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.InvokerPortlet;
import com.liferay.portal.kernel.portlet.LayoutFriendlyURLSeparatorComposite;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletInstanceFactoryUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.upload.UploadServletRequest;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;
import jakarta.portlet.PreferencesValidator;
import jakarta.portlet.WindowState;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TimeZone;

/**
 * @author Brian Wing Shun Chan
 * @author Eduardo Lundgren
 * @author Juan Fernández
 */
public class PortalUtil {

	/**
	 * Appends the description to the current meta description of the page in
	 * the request.
	 *
	 * @param description the description to append to the current meta
	 *        description
	 * @param httpServletRequest the servlet request for the page
	 */
	public static void addPageDescription(
		String description, HttpServletRequest httpServletRequest) {

		_portal.addPageDescription(description, httpServletRequest);
	}

	/**
	 * Appends the keywords to the current meta keywords of the page in the
	 * request.
	 *
	 * @param keywords the keywords to add to the current meta keywords
	 *        (comma-separated)
	 * @param httpServletRequest the servlet request for the page
	 */
	public static void addPageKeywords(
		String keywords, HttpServletRequest httpServletRequest) {

		_portal.addPageKeywords(keywords, httpServletRequest);
	}

	/**
	 * Appends the subtitle to the current subtitle of the page in the request.
	 *
	 * @param subtitle the subtitle to append to the current subtitle
	 * @param httpServletRequest the servlet request for the page
	 */
	public static void addPageSubtitle(
		String subtitle, HttpServletRequest httpServletRequest) {

		_portal.addPageSubtitle(subtitle, httpServletRequest);
	}

	/**
	 * Appends the title to the current title of the page in the request.
	 *
	 * @param title the title to append to the current title
	 * @param httpServletRequest the servlet request for the page
	 */
	public static void addPageTitle(
		String title, HttpServletRequest httpServletRequest) {

		_portal.addPageTitle(title, httpServletRequest);
	}

	/**
	 * Adds an entry to the portlet breadcrumbs for the page in the request.
	 *
	 * @param httpServletRequest the servlet request for the page
	 * @param title the title of the new breadcrumb entry
	 * @param url the URL of the new breadcrumb entry
	 */
	public static void addPortletBreadcrumbEntry(
		HttpServletRequest httpServletRequest, String title, String url) {

		_portal.addPortletBreadcrumbEntry(httpServletRequest, title, url);
	}

	/**
	 * Adds an entry to the portlet breadcrumbs for the page in the request.
	 *
	 * @param httpServletRequest the servlet request for the page
	 * @param title the title of the new breadcrumb entry
	 * @param url the URL of the new breadcrumb entry
	 * @param data the HTML5 data parameters of the new breadcrumb entry
	 */
	public static void addPortletBreadcrumbEntry(
		HttpServletRequest httpServletRequest, String title, String url,
		Map<String, Object> data) {

		_portal.addPortletBreadcrumbEntry(httpServletRequest, title, url, data);
	}

	/**
	 * Adds an entry to the portlet breadcrumbs for the page in the request.
	 *
	 * @param httpServletRequest the servlet request for the page
	 * @param title the title of the new breadcrumb entry
	 * @param url the URL of the new breadcrumb entry
	 * @param data the HTML5 data parameters of the new breadcrumb entry
	 * @param portletBreadcrumbEntry whether the entry is a portlet breadcrumb
	 *        entry
	 */
	public static void addPortletBreadcrumbEntry(
		HttpServletRequest httpServletRequest, String title, String url,
		Map<String, Object> data, boolean portletBreadcrumbEntry) {

		_portal.addPortletBreadcrumbEntry(
			httpServletRequest, title, url, data, portletBreadcrumbEntry);
	}

	/**
	 * Adds the default resource permissions for the portlet to the page in the
	 * request.
	 *
	 * @param  httpServletRequest the servlet request for the page
	 * @param  portlet the portlet
	 * @throws PortalException if a portal exception occurred
	 */
	public static void addPortletDefaultResource(
			HttpServletRequest httpServletRequest, Portlet portlet)
		throws PortalException {

		_portal.addPortletDefaultResource(httpServletRequest, portlet);
	}

	public static void addPortletDefaultResource(
			long companyId, Layout layout, Portlet portlet)
		throws PortalException {

		_portal.addPortletDefaultResource(companyId, layout, portlet);
	}

	/**
	 * Adds the preserved parameters doAsGroupId and refererPlid to the URL,
	 * optionally adding doAsUserId and doAsUserLanguageId as well.
	 *
	 * <p>
	 * Preserved parameters are parameters that should be sent with every
	 * request as the user navigates the portal.
	 * </p>
	 *
	 * @param  themeDisplay the current theme display
	 * @param  layout the current page
	 * @param  url the URL
	 * @param  doAsUser whether to include doAsUserId and doAsLanguageId in the
	 *         URL if they are available. If <code>false</code>, doAsUserId and
	 *         doAsUserLanguageId will never be added.
	 * @return the URL with the preserved parameters added
	 */
	public static String addPreservedParameters(
		ThemeDisplay themeDisplay, Layout layout, String url,
		boolean doAsUser) {

		return _portal.addPreservedParameters(
			themeDisplay, layout, url, doAsUser);
	}

	/**
	 * Adds the preserved parameters doAsUserId, doAsUserLanguageId,
	 * doAsGroupId, and refererPlid to the URL.
	 *
	 * @param  themeDisplay the current theme display
	 * @param  url the URL
	 * @return the URL with the preserved parameters added
	 */
	public static String addPreservedParameters(
		ThemeDisplay themeDisplay, String url) {

		return _portal.addPreservedParameters(themeDisplay, url);
	}

	public static String addPreservedParameters(
		ThemeDisplay themeDisplay, String url, boolean typeControlPanel,
		boolean doAsUser) {

		return _portal.addPreservedParameters(
			themeDisplay, url, typeControlPanel, doAsUser);
	}

	/**
	 * Copies the request parameters to the render parameters, unless a
	 * parameter with that name already exists in the render parameters.
	 *
	 * @param actionRequest the request from which to get the request parameters
	 * @param actionResponse the response to receive the render parameters
	 */
	public static void copyRequestParameters(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		_portal.copyRequestParameters(actionRequest, actionResponse);
	}

	/**
	 * Escapes the URL for use in a redirect and checks that security settings
	 * allow the URL is allowed for redirects.
	 *
	 * @param  url the URL to escape
	 * @return the escaped URL, or <code>null</code> if the URL is not allowed
	 *         for redirects
	 */
	public static String escapeRedirect(String url) {
		return _portal.escapeRedirect(url);
	}

	public static String fetchClassName(long classNameId) {
		return _portal.fetchClassName(classNameId);
	}

	/**
	 * Generates a random key to identify the request based on the input string.
	 *
	 * @param  httpServletRequest the servlet request for the page
	 * @param  input the input string
	 * @return the generated key
	 */
	public static String generateRandomKey(
		HttpServletRequest httpServletRequest, String input) {

		return _portal.generateRandomKey(httpServletRequest, input);
	}

	public static String getAbsoluteURL(
		HttpServletRequest httpServletRequest, String url) {

		return _portal.getAbsoluteURL(httpServletRequest, url);
	}

	public static LayoutQueryStringComposite
			getActualLayoutQueryStringComposite(
				long groupId, boolean privateLayout, String friendlyURL,
				Map<String, String[]> params,
				Map<String, Object> requestContext)
		throws PortalException {

		return _portal.getActualLayoutQueryStringComposite(
			groupId, privateLayout, friendlyURL, params, requestContext);
	}

	public static String getActualURL(
			long groupId, boolean privateLayout, String mainPath,
			String friendlyURL, Map<String, String[]> params,
			Map<String, Object> requestContext)
		throws PortalException {

		return _portal.getActualURL(
			groupId, privateLayout, mainPath, friendlyURL, params,
			requestContext);
	}

	/**
	 * Returns the alternate URL for the requested canonical URL in the given
	 * locale.
	 *
	 * <p>
	 * The alternate URL lets search engines know that an equivalent page is
	 * available for the given locale. For more information, see <a
	 * href="https://support.google.com/webmasters/answer/189077?hl=en">https://support.google.com/webmasters/answer/189077?hl=en</a>.
	 * </p>
	 *
	 * @param  canonicalURL the canonical URL being requested. For more
	 *         information, see {@link #getCanonicalURL}.
	 * @param  themeDisplay the theme display
	 * @param  locale the locale of the alternate URL being generated
	 * @param  layout the page being requested
	 * @return the alternate URL for the requested canonical URL in the given
	 *         locale
	 * @throws PortalException if a portal exception occurred
	 */
	public static String getAlternateURL(
			String canonicalURL, ThemeDisplay themeDisplay, Locale locale,
			Layout layout)
		throws PortalException {

		return _portal.getAlternateURL(
			canonicalURL, themeDisplay, locale, layout);
	}

	public static Map<Locale, String> getAlternateURLs(
			String canonicalURL, ThemeDisplay themeDisplay, Layout layout)
		throws PortalException {

		return _portal.getAlternateURLs(canonicalURL, themeDisplay, layout);
	}

	public static Map<Locale, String> getAlternateURLs(
			String canonicalURL, ThemeDisplay themeDisplay, Layout layout,
			Set<Locale> availableLocales)
		throws PortalException {

		return _portal.getAlternateURLs(
			canonicalURL, themeDisplay, layout, availableLocales);
	}

	public static long[] getAncestorSiteGroupIds(long groupId) {
		return _portal.getAncestorSiteGroupIds(groupId);
	}

	/**
	 * Returns the canonical URL for the page. The canonical URL is often used
	 * to distinguish a preferred page from its translations.
	 *
	 * <p>
	 * A canonical URL for the page is the preferred URL to specify for a set of
	 * pages with similar or identical content. The canonical URL is used to
	 * inform search engines that several URLs point to the same page. It is
	 * also used to generate the URLs for site maps, the URLs that social
	 * bookmarks publish (Twitter, Facebook links, etc.), and the URLs in sent
	 * email. For more information, see <a
	 * href="https://support.google.com/webmasters/answer/139394?hl=en">https://support.google.com/webmasters/answer/139394?hl=en</a>.
	 * </p>
	 *
	 * @param  completeURL the complete URL of the page
	 * @param  themeDisplay the theme display
	 * @param  layout the page being requested (optionally <code>null</code>).
	 *         If <code>null</code> is specified, the current page is used.
	 * @return the canonical URL for the page
	 * @throws PortalException if a portal exception occurred
	 */
	public static String getCanonicalURL(
			String completeURL, ThemeDisplay themeDisplay, Layout layout)
		throws PortalException {

		return _portal.getCanonicalURL(completeURL, themeDisplay, layout);
	}

	/**
	 * Returns the canonical URL of the page, optionally including the page's
	 * friendly URL. The canonical URL is often used to distinguish a preferred
	 * page from its translations.
	 *
	 * <p>
	 * A canonical URL for the page is the preferred URL to specify for a set of
	 * pages with similar or identical content. The canonical URL is used to
	 * inform search engines that several URLs point to the same page. It is
	 * also used to generate the URLs for site maps, the URLs that social
	 * bookmarks publish (Twitter, Facebook links, etc.), and the URLs in sent
	 * email. For more information, see <a
	 * href="https://support.google.com/webmasters/answer/139394?hl=en">https://support.google.com/webmasters/answer/139394?hl=en</a>.
	 * </p>
	 *
	 * @param  completeURL the complete URL of the page
	 * @param  themeDisplay the current theme display
	 * @param  layout the page. If it is <code>null</code>, then it is generated
	 *         for the current page.
	 * @param  forceLayoutFriendlyURL whether to add the page's friendly URL to
	 *         the canonical URL
	 * @return the canonical URL of the page
	 * @throws PortalException if a portal exception occurred
	 */
	public static String getCanonicalURL(
			String completeURL, ThemeDisplay themeDisplay, Layout layout,
			boolean forceLayoutFriendlyURL)
		throws PortalException {

		return _portal.getCanonicalURL(
			completeURL, themeDisplay, layout, forceLayoutFriendlyURL);
	}

	/**
	 * Returns the canonical URL of the page. The canonical URL is often used to
	 * distinguish a preferred page from its translations.
	 *
	 * <p>
	 * A page's canonical URL is the preferred URL to specify for a set of pages
	 * with similar or identical content. The canonical URL is used to inform
	 * search engines that several URLs point to the same page. It is also used
	 * to generate the URLs for site maps, the URLs that social bookmarks
	 * publish (Twitter, Facebook links, etc.), and the URLs in sent email. For
	 * more information, see <a
	 * href="https://support.google.com/webmasters/answer/139394?hl=en">https://support.google.com/webmasters/answer/139394?hl=en</a>.
	 * </p>
	 *
	 * @param  completeURL the complete URL of the page
	 * @param  themeDisplay the theme display
	 * @param  layout the page being requested (optionally <code>null</code>).
	 *         If <code>null</code> is specified, the current page is used.
	 * @param  forceLayoutFriendlyURL whether to add the page's friendly URL to
	 *         the canonical URL
	 * @param  includeQueryString whether to add the URL query string to the
	 *         canonical URL
	 * @return the canonical URL
	 * @throws PortalException if a portal exception occurred
	 */
	public static String getCanonicalURL(
			String completeURL, ThemeDisplay themeDisplay, Layout layout,
			boolean forceLayoutFriendlyURL, boolean includeQueryString)
		throws PortalException {

		return _portal.getCanonicalURL(
			completeURL, themeDisplay, layout, forceLayoutFriendlyURL,
			includeQueryString);
	}

	/**
	 * Returns the secure (HTTPS) or insecure (HTTP) content distribution
	 * network (CDN) host address for this portal.
	 *
	 * @param  secure whether to get the secure CDN host address
	 * @return the CDN host address
	 */
	public static String getCDNHost(boolean secure) {
		return _portal.getCDNHost(secure);
	}

	public static String getCDNHost(HttpServletRequest httpServletRequest)
		throws PortalException {

		return _portal.getCDNHost(httpServletRequest);
	}

	/**
	 * Returns the insecure (HTTP) content distribution network (CDN) host
	 * address
	 *
	 * @param  companyId the company ID of a site
	 * @return the CDN host address
	 */
	public static String getCDNHostHttp(long companyId) {
		return _portal.getCDNHostHttp(companyId);
	}

	/**
	 * Returns the secure (HTTPS) content distribution network (CDN) host
	 * address
	 *
	 * @param  companyId the company ID of a site
	 * @return the CDN host address
	 */
	public static String getCDNHostHttps(long companyId) {
		return _portal.getCDNHostHttps(companyId);
	}

	/**
	 * Returns the fully qualified name of the class from its ID.
	 *
	 * @param  classNameId the ID of the class
	 * @return the fully qualified name of the class
	 */
	public static String getClassName(long classNameId) {
		return _portal.getClassName(classNameId);
	}

	/**
	 * Returns the ID of the class from its class object.
	 *
	 * @param  clazz the class object
	 * @return the ID of the class
	 */
	public static long getClassNameId(Class<?> clazz) {
		return _portal.getClassNameId(clazz);
	}

	/**
	 * Returns the ID of the class from its fully qualified name.
	 *
	 * @param  value the fully qualified name of the class
	 * @return the ID of the class
	 */
	public static long getClassNameId(String value) {
		return _portal.getClassNameId(value);
	}

	public static Company getCompany(HttpServletRequest httpServletRequest)
		throws PortalException {

		return _portal.getCompany(httpServletRequest);
	}

	public static Company getCompany(PortletRequest portletRequest)
		throws PortalException {

		return _portal.getCompany(portletRequest);
	}

	public static long getCompanyId(HttpServletRequest httpServletRequest) {
		return _portal.getCompanyId(httpServletRequest);
	}

	public static long getCompanyId(PortletRequest portletRequest) {
		return _portal.getCompanyId(portletRequest);
	}

	public static long[] getCompanyIds() {
		return _portal.getCompanyIds();
	}

	public static Set<String> getComputerAddresses() {
		return _portal.getComputerAddresses();
	}

	public static String getComputerName() {
		return _portal.getComputerName();
	}

	public static String getControlPanelFullURL(
			long scopeGroupId, String ppid, Map<String, String[]> params)
		throws PortalException {

		return _portal.getControlPanelFullURL(scopeGroupId, ppid, params);
	}

	public static long getControlPanelPlid(long companyId)
		throws PortalException {

		return _portal.getControlPanelPlid(companyId);
	}

	public static long getControlPanelPlid(PortletRequest portletRequest)
		throws PortalException {

		return _portal.getControlPanelPlid(portletRequest);
	}

	public static PortletURL getControlPanelPortletURL(
		HttpServletRequest httpServletRequest, Group group, String portletId,
		long refererGroupId, long refererPlid, String lifecycle) {

		return _portal.getControlPanelPortletURL(
			httpServletRequest, group, portletId, refererGroupId, refererPlid,
			lifecycle);
	}

	public static PortletURL getControlPanelPortletURL(
		HttpServletRequest httpServletRequest, String portletId,
		String lifecycle) {

		return _portal.getControlPanelPortletURL(
			httpServletRequest, portletId, lifecycle);
	}

	public static PortletURL getControlPanelPortletURL(
		PortletRequest portletRequest, Group group, String portletId,
		long refererGroupId, long refererPlid, String lifecycle) {

		return _portal.getControlPanelPortletURL(
			portletRequest, group, portletId, refererGroupId, refererPlid,
			lifecycle);
	}

	public static PortletURL getControlPanelPortletURL(
		PortletRequest portletRequest, String portletId, String lifecycle) {

		return _portal.getControlPanelPortletURL(
			portletRequest, portletId, lifecycle);
	}

	public static String getCreateAccountURL(
			HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay)
		throws Exception {

		return _portal.getCreateAccountURL(httpServletRequest, themeDisplay);
	}

	public static long[] getCurrentAndAncestorSiteGroupIds(long groupId) {
		return _portal.getCurrentAndAncestorSiteGroupIds(groupId);
	}

	public static long[] getCurrentAndAncestorSiteGroupIds(
		long groupId, boolean checkContentSharingWithChildrenEnabled) {

		return _portal.getCurrentAndAncestorSiteGroupIds(
			groupId, checkContentSharingWithChildrenEnabled);
	}

	public static long[] getCurrentAndAncestorSiteGroupIds(long[] groupIds) {
		return _portal.getCurrentAndAncestorSiteGroupIds(groupIds);
	}

	public static long[] getCurrentAndAncestorSiteGroupIds(
		long[] groupIds, boolean checkContentSharingWithChildrenEnabled) {

		return _portal.getCurrentAndAncestorSiteGroupIds(
			groupIds, checkContentSharingWithChildrenEnabled);
	}

	public static List<Group> getCurrentAndAncestorSiteGroups(
		long groupId, boolean checkContentSharingWithChildrenEnabled) {

		return _portal.getCurrentAndAncestorSiteGroups(
			groupId, checkContentSharingWithChildrenEnabled);
	}

	public static List<Group> getCurrentAndAncestorSiteGroups(
		long[] groupIds, boolean checkContentSharingWithChildrenEnabled) {

		return _portal.getCurrentAndAncestorSiteGroups(
			groupIds, checkContentSharingWithChildrenEnabled);
	}

	public static String getCurrentCompleteURL(
		HttpServletRequest httpServletRequest) {

		return _portal.getCurrentCompleteURL(httpServletRequest);
	}

	public static String getCurrentURL(HttpServletRequest httpServletRequest) {
		return _portal.getCurrentURL(httpServletRequest);
	}

	public static String getCurrentURL(PortletRequest portletRequest) {
		return _portal.getCurrentURL(portletRequest);
	}

	public static String getCustomSQLFunctionIsNotNull() {
		return _portal.getCustomSQLFunctionIsNotNull();
	}

	public static String getCustomSQLFunctionIsNull() {
		return _portal.getCustomSQLFunctionIsNull();
	}

	/**
	 * Returns the date object for the specified month, day, and year, or
	 * <code>null</code> if the date is invalid.
	 *
	 * @param  month the month (0-based, meaning 0 for January)
	 * @param  day the day of the month
	 * @param  year the year
	 * @return the date object, or <code>null</code> if the date is invalid
	 */
	public static Date getDate(int month, int day, int year) {
		return _portal.getDate(month, day, year);
	}

	/**
	 * Returns the date object for the specified month, day, and year,
	 * optionally throwing an exception if the date is invalid.
	 *
	 * @param  month the month (0-based, meaning 0 for January)
	 * @param  day the day of the month
	 * @param  year the year
	 * @param  clazz the exception class to throw if the date is invalid. If
	 *         <code>null</code>, no exception will be thrown for an invalid
	 *         date.
	 * @return the date object, or <code>null</code> if the date is invalid and
	 *         no exception to throw was provided
	 * @throws PortalException if a portal exception occurred
	 */
	public static Date getDate(
			int month, int day, int year,
			Class<? extends PortalException> clazz)
		throws PortalException {

		return _portal.getDate(month, day, year, clazz);
	}

	/**
	 * Returns the date object for the specified month, day, year, hour, and
	 * minute, optionally throwing an exception if the date is invalid.
	 *
	 * @param  month the month (0-based, meaning 0 for January)
	 * @param  day the day of the month
	 * @param  year the year
	 * @param  hour the hour (0-24)
	 * @param  min the minute of the hour
	 * @param  clazz the exception class to throw if the date is invalid. If
	 *         <code>null</code>, no exception will be thrown for an invalid
	 *         date.
	 * @return the date object, or <code>null</code> if the date is invalid and
	 *         no exception to throw was provided
	 * @throws PortalException if a portal exception occurred
	 */
	public static Date getDate(
			int month, int day, int year, int hour, int min,
			Class<? extends PortalException> clazz)
		throws PortalException {

		return _portal.getDate(month, day, year, hour, min, clazz);
	}

	/**
	 * Returns the date object for the specified month, day, year, hour, minute,
	 * and time zone, optionally throwing an exception if the date is invalid.
	 *
	 * @param  month the month (0-based, meaning 0 for January)
	 * @param  day the day of the month
	 * @param  year the year
	 * @param  hour the hour (0-24)
	 * @param  min the minute of the hour
	 * @param  timeZone the time zone of the date
	 * @param  clazz the exception class to throw if the date is invalid. If
	 *         <code>null</code>, no exception will be thrown for an invalid
	 *         date.
	 * @return the date object, or <code>null</code> if the date is invalid and
	 *         no exception to throw was provided
	 * @throws PortalException if a portal exception occurred
	 */
	public static Date getDate(
			int month, int day, int year, int hour, int min, TimeZone timeZone,
			Class<? extends PortalException> clazz)
		throws PortalException {

		return _portal.getDate(month, day, year, hour, min, timeZone, clazz);
	}

	/**
	 * Returns the date object for the specified month, day, year, and time
	 * zone, optionally throwing an exception if the date is invalid.
	 *
	 * @param  month the month (0-based, meaning 0 for January)
	 * @param  day the day of the month
	 * @param  year the year
	 * @param  timeZone the time zone of the date
	 * @param  clazz the exception class to throw if the date is invalid. If
	 *         <code>null</code>, no exception will be thrown for an invalid
	 *         date.
	 * @return the date object, or <code>null</code> if the date is invalid and
	 *         no exception to throw was provided
	 * @throws PortalException if a portal exception occurred
	 */
	public static Date getDate(
			int month, int day, int year, TimeZone timeZone,
			Class<? extends PortalException> clazz)
		throws PortalException {

		return _portal.getDate(month, day, year, timeZone, clazz);
	}

	public static long getDefaultCompanyId() {
		return _portal.getDefaultCompanyId();
	}

	public static String getEmailFromAddress(
		PortletPreferences portletPreferences, long companyId,
		String defaultValue) {

		return _portal.getEmailFromAddress(
			portletPreferences, companyId, defaultValue);
	}

	public static String getEmailFromName(
		PortletPreferences portletPreferences, long companyId,
		String defaultValue) {

		return _portal.getEmailFromName(
			portletPreferences, companyId, defaultValue);
	}

	public static String getForwardedHost(
		HttpServletRequest httpServletRequest) {

		return _portal.getForwardedHost(httpServletRequest);
	}

	public static int getForwardedPort(HttpServletRequest httpServletRequest) {
		return _portal.getForwardedPort(httpServletRequest);
	}

	public static String getFullName(
		String firstName, String middleName, String lastName) {

		return _portal.getFullName(firstName, middleName, lastName);
	}

	public static String getGoogleGadgetURL(
			Portlet portlet, ThemeDisplay themeDisplay)
		throws PortalException {

		return _portal.getGoogleGadgetURL(portlet, themeDisplay);
	}

	public static String getGroupFriendlyURL(
			LayoutSet layoutSet, ThemeDisplay themeDisplay,
			boolean canonicalURL, boolean controlPanel)
		throws PortalException {

		return _portal.getGroupFriendlyURL(
			layoutSet, themeDisplay, canonicalURL, controlPanel);
	}

	public static String getGroupFriendlyURL(
			LayoutSet layoutSet, ThemeDisplay themeDisplay, Locale locale)
		throws PortalException {

		return _portal.getGroupFriendlyURL(layoutSet, themeDisplay, locale);
	}

	public static int[] getGroupFriendlyURLIndex(String requestURI) {
		return _portal.getGroupFriendlyURLIndex(requestURI);
	}

	public static String getHomeURL(HttpServletRequest httpServletRequest)
		throws PortalException {

		return _portal.getHomeURL(httpServletRequest);
	}

	public static String getHost(HttpServletRequest httpServletRequest) {
		return _portal.getHost(httpServletRequest);
	}

	public static String getHost(PortletRequest portletRequest) {
		return _portal.getHost(portletRequest);
	}

	public static HttpServletRequest getHttpServletRequest(
		PortletRequest portletRequest) {

		return _portal.getHttpServletRequest(portletRequest);
	}

	public static HttpServletResponse getHttpServletResponse(
		PortletResponse portletResponse) {

		return _portal.getHttpServletResponse(portletResponse);
	}

	public static String getI18nPathLanguageId(
		Locale locale, String defaultI18nPathLanguageId) {

		return _portal.getI18nPathLanguageId(locale, defaultI18nPathLanguageId);
	}

	public static String getJsSafePortletId(String portletId) {
		return _portal.getJsSafePortletId(portletId);
	}

	public static String getLayoutActualURL(Layout layout) {
		return _portal.getLayoutActualURL(layout);
	}

	public static String getLayoutActualURL(Layout layout, String mainPath) {
		return _portal.getLayoutActualURL(layout, mainPath);
	}

	public static String getLayoutActualURL(
			long groupId, boolean privateLayout, String mainPath,
			String friendlyURL, Map<String, String[]> params,
			Map<String, Object> requestContext)
		throws PortalException {

		return _portal.getLayoutActualURL(
			groupId, privateLayout, mainPath, friendlyURL, params,
			requestContext);
	}

	public static String getLayoutFriendlyURL(
			Layout layout, ThemeDisplay themeDisplay)
		throws PortalException {

		return _portal.getLayoutFriendlyURL(layout, themeDisplay);
	}

	public static String getLayoutFriendlyURL(
			Layout layout, ThemeDisplay themeDisplay, Locale locale)
		throws PortalException {

		return _portal.getLayoutFriendlyURL(layout, themeDisplay, locale);
	}

	public static String getLayoutFriendlyURL(ThemeDisplay themeDisplay)
		throws PortalException {

		return _portal.getLayoutFriendlyURL(themeDisplay);
	}

	public static LayoutFriendlyURLSeparatorComposite
			getLayoutFriendlyURLSeparatorComposite(
				long groupId, boolean privateLayout, String friendlyURL,
				Map<String, String[]> params,
				Map<String, Object> requestContext)
		throws PortalException {

		return _portal.getLayoutFriendlyURLSeparatorComposite(
			groupId, privateLayout, friendlyURL, params, requestContext);
	}

	public static String getLayoutFullURL(
			Layout layout, ThemeDisplay themeDisplay)
		throws PortalException {

		return _portal.getLayoutFullURL(layout, themeDisplay);
	}

	public static String getLayoutFullURL(
			Layout layout, ThemeDisplay themeDisplay, boolean doAsUser)
		throws PortalException {

		return _portal.getLayoutFullURL(layout, themeDisplay, doAsUser);
	}

	public static String getLayoutFullURL(long groupId, String portletId)
		throws PortalException {

		return _portal.getLayoutFullURL(groupId, portletId);
	}

	public static String getLayoutFullURL(
			long groupId, String portletId, boolean secure)
		throws PortalException {

		return _portal.getLayoutFullURL(groupId, portletId, secure);
	}

	public static String getLayoutFullURL(ThemeDisplay themeDisplay)
		throws PortalException {

		return _portal.getLayoutFullURL(themeDisplay);
	}

	public static String getLayoutRelativeURL(
			Layout layout, ThemeDisplay themeDisplay)
		throws PortalException {

		return _portal.getLayoutRelativeURL(layout, themeDisplay);
	}

	public static String getLayoutRelativeURL(
			Layout layout, ThemeDisplay themeDisplay, boolean doAsUser)
		throws PortalException {

		return _portal.getLayoutRelativeURL(layout, themeDisplay, doAsUser);
	}

	public static String getLayoutSetDisplayURL(
			LayoutSet layoutSet, boolean secureConnection)
		throws PortalException {

		return _portal.getLayoutSetDisplayURL(layoutSet, secureConnection);
	}

	public static String getLayoutSetFriendlyURL(
			LayoutSet layoutSet, ThemeDisplay themeDisplay)
		throws PortalException {

		return _portal.getLayoutSetFriendlyURL(layoutSet, themeDisplay);
	}

	public static String getLayoutTarget(Layout layout) {
		return _portal.getLayoutTarget(layout);
	}

	public static String getLayoutURL(Layout layout, ThemeDisplay themeDisplay)
		throws PortalException {

		return _portal.getLayoutURL(layout, themeDisplay);
	}

	public static String getLayoutURL(
			Layout layout, ThemeDisplay themeDisplay, boolean doAsUser)
		throws PortalException {

		return _portal.getLayoutURL(layout, themeDisplay, doAsUser);
	}

	public static String getLayoutURL(
			Layout layout, ThemeDisplay themeDisplay, Locale locale)
		throws PortalException {

		return _portal.getLayoutURL(layout, themeDisplay, locale);
	}

	public static String getLayoutURL(ThemeDisplay themeDisplay)
		throws PortalException {

		return _portal.getLayoutURL(themeDisplay);
	}

	public static LiferayPortletRequest getLiferayPortletRequest(
		PortletRequest portletRequest) {

		return _portal.getLiferayPortletRequest(portletRequest);
	}

	public static LiferayPortletResponse getLiferayPortletResponse(
		PortletResponse portletResponse) {

		return _portal.getLiferayPortletResponse(portletResponse);
	}

	public static Locale getLocale(HttpServletRequest httpServletRequest) {
		return _portal.getLocale(httpServletRequest);
	}

	public static Locale getLocale(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, boolean initialize) {

		return _portal.getLocale(
			httpServletRequest, httpServletResponse, initialize);
	}

	public static Locale getLocale(PortletRequest portletRequest) {
		return _portal.getLocale(portletRequest);
	}

	public static String getNetvibesURL(
			Portlet portlet, ThemeDisplay themeDisplay)
		throws PortalException {

		return _portal.getNetvibesURL(portlet, themeDisplay);
	}

	public static String getNewPortletTitle(
		String portletTitle, String oldScopeName, String newScopeName) {

		return _portal.getNewPortletTitle(
			portletTitle, oldScopeName, newScopeName);
	}

	public static HttpServletRequest getOriginalServletRequest(
		HttpServletRequest httpServletRequest) {

		return _portal.getOriginalServletRequest(httpServletRequest);
	}

	public static String getPathContext() {
		return _portal.getPathContext();
	}

	public static String getPathContext(HttpServletRequest httpServletRequest) {
		return _portal.getPathContext(httpServletRequest);
	}

	public static String getPathContext(PortletRequest portletRequest) {
		return _portal.getPathContext(portletRequest);
	}

	public static String getPathContext(String contextPath) {
		return _portal.getPathContext(contextPath);
	}

	public static String getPathFriendlyURLPrivateGroup() {
		return _portal.getPathFriendlyURLPrivateGroup();
	}

	public static String getPathFriendlyURLPrivateUser() {
		return _portal.getPathFriendlyURLPrivateUser();
	}

	public static String getPathFriendlyURLPublic() {
		return _portal.getPathFriendlyURLPublic();
	}

	public static String getPathImage() {
		return _portal.getPathImage();
	}

	public static String getPathMain() {
		return _portal.getPathMain();
	}

	public static String getPathModule() {
		return _portal.getPathModule();
	}

	public static String getPathProxy() {
		return _portal.getPathProxy();
	}

	public static long getPlidFromFriendlyURL(
		long companyId, String friendlyURL) {

		return _portal.getPlidFromFriendlyURL(companyId, friendlyURL);
	}

	public static long getPlidFromPortletId(
			long groupId, boolean privateLayout, String portletId)
		throws PortalException {

		return _portal.getPlidFromPortletId(groupId, privateLayout, portletId);
	}

	public static long getPlidFromPortletId(long groupId, String portletId)
		throws PortalException {

		return _portal.getPlidFromPortletId(groupId, portletId);
	}

	public static Portal getPortal() {
		return _portal;
	}

	public static int getPortalLocalPort(boolean secure) {
		return _portal.getPortalLocalPort(secure);
	}

	public static Properties getPortalProperties() {
		return _portal.getPortalProperties();
	}

	public static int getPortalServerPort(boolean secure) {
		return _portal.getPortalServerPort(secure);
	}

	public static String getPortalURL(HttpServletRequest httpServletRequest) {
		return _portal.getPortalURL(httpServletRequest);
	}

	public static String getPortalURL(
		HttpServletRequest httpServletRequest, boolean secure) {

		return _portal.getPortalURL(httpServletRequest, secure);
	}

	public static String getPortalURL(Layout layout, ThemeDisplay themeDisplay)
		throws PortalException {

		return _portal.getPortalURL(layout, themeDisplay);
	}

	public static String getPortalURL(
		LayoutSet layoutSet, ThemeDisplay themeDisplay) {

		return _portal.getPortalURL(layoutSet, themeDisplay);
	}

	public static String getPortalURL(PortletRequest portletRequest) {
		return _portal.getPortalURL(portletRequest);
	}

	public static String getPortalURL(
		PortletRequest portletRequest, boolean secure) {

		return _portal.getPortalURL(portletRequest, secure);
	}

	public static String getPortalURL(
		String serverName, int serverPort, boolean secure) {

		return _portal.getPortalURL(serverName, serverPort, secure);
	}

	public static String getPortalURL(ThemeDisplay themeDisplay)
		throws PortalException {

		return _portal.getPortalURL(themeDisplay);
	}

	public static PortletConfig getPortletConfig(
			long companyId, String portletId, ServletContext servletContext)
		throws PortletException {

		Portlet portlet = PortletLocalServiceUtil.getPortletById(
			companyId, portletId);

		InvokerPortlet invokerPortlet = PortletInstanceFactoryUtil.create(
			portlet, servletContext);

		return invokerPortlet.getPortletConfig();
	}

	public static String getPortletDescription(
		Portlet portlet, ServletContext servletContext, Locale locale) {

		return _portal.getPortletDescription(portlet, servletContext, locale);
	}

	public static String getPortletDescription(Portlet portlet, User user) {
		return _portal.getPortletDescription(portlet, user);
	}

	public static String getPortletDescription(
		String portletId, Locale locale) {

		return _portal.getPortletDescription(portletId, locale);
	}

	public static String getPortletDescription(
		String portletId, String languageId) {

		return _portal.getPortletDescription(portletId, languageId);
	}

	public static String getPortletDescription(String portletId, User user) {
		return _portal.getPortletDescription(portletId, user);
	}

	public static String getPortletId(HttpServletRequest httpServletRequest) {
		return _portal.getPortletId(httpServletRequest);
	}

	public static String getPortletId(PortletRequest portletRequest) {
		return _portal.getPortletId(portletRequest);
	}

	public static String getPortletLongTitle(
		Portlet portlet, ServletContext servletContext, Locale locale) {

		return _portal.getPortletLongTitle(portlet, servletContext, locale);
	}

	public static String getPortletLongTitle(String portletId, Locale locale) {
		return _portal.getPortletLongTitle(portletId, locale);
	}

	public static String getPortletNamespace(String portletId) {
		return _portal.getPortletNamespace(portletId);
	}

	public static String getPortletTitle(Portlet portlet, Locale locale) {
		return _portal.getPortletTitle(portlet, locale);
	}

	public static String getPortletTitle(
		Portlet portlet, ServletContext servletContext, Locale locale) {

		return _portal.getPortletTitle(portlet, servletContext, locale);
	}

	public static String getPortletTitle(Portlet portlet, String languageId) {
		return _portal.getPortletTitle(portlet, languageId);
	}

	public static String getPortletTitle(Portlet portlet, User user) {
		return _portal.getPortletTitle(portlet, user);
	}

	public static String getPortletTitle(PortletRequest portletRequest) {
		return _portal.getPortletTitle(portletRequest);
	}

	public static String getPortletTitle(PortletResponse portletResponse) {
		return _portal.getPortletTitle(portletResponse);
	}

	public static String getPortletTitle(String portletId, Locale locale) {
		return _portal.getPortletTitle(portletId, locale);
	}

	public static String getPortletTitle(
		String portletId, ResourceBundle resourceBundle) {

		return _portal.getPortletTitle(portletId, resourceBundle);
	}

	public static String getPortletTitle(String portletId, String languageId) {
		return _portal.getPortletTitle(portletId, languageId);
	}

	public static String getPortletTitle(String portletId, User user) {
		return _portal.getPortletTitle(portletId, user);
	}

	public static PortletPreferences getPreferences(
		HttpServletRequest httpServletRequest) {

		return _portal.getPreferences(httpServletRequest);
	}

	public static PreferencesValidator getPreferencesValidator(
		Portlet portlet) {

		return _portal.getPreferencesValidator(portlet);
	}

	public static String getRelativeHomeURL(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		return _portal.getRelativeHomeURL(httpServletRequest);
	}

	public static ResourceBundle getResourceBundle(Locale locale) {
		return _portal.getResourceBundle(locale);
	}

	public static long getScopeGroupId(HttpServletRequest httpServletRequest)
		throws PortalException {

		return _portal.getScopeGroupId(httpServletRequest);
	}

	public static long getScopeGroupId(
			HttpServletRequest httpServletRequest, String portletId)
		throws PortalException {

		return _portal.getScopeGroupId(httpServletRequest, portletId);
	}

	public static long getScopeGroupId(
			HttpServletRequest httpServletRequest, String portletId,
			boolean checkStagingGroup)
		throws PortalException {

		return _portal.getScopeGroupId(
			httpServletRequest, portletId, checkStagingGroup);
	}

	public static long getScopeGroupId(Layout layout) {
		return _portal.getScopeGroupId(layout);
	}

	public static long getScopeGroupId(Layout layout, String portletId) {
		return _portal.getScopeGroupId(layout, portletId);
	}

	public static long getScopeGroupId(long plid) {
		return _portal.getScopeGroupId(plid);
	}

	public static long getScopeGroupId(PortletRequest portletRequest)
		throws PortalException {

		return _portal.getScopeGroupId(portletRequest);
	}

	public static User getSelectedUser(HttpServletRequest httpServletRequest)
		throws PortalException {

		return _portal.getSelectedUser(httpServletRequest);
	}

	public static User getSelectedUser(
			HttpServletRequest httpServletRequest, boolean checkPermission)
		throws PortalException {

		return _portal.getSelectedUser(httpServletRequest, checkPermission);
	}

	public static User getSelectedUser(PortletRequest portletRequest)
		throws PortalException {

		return _portal.getSelectedUser(portletRequest);
	}

	public static User getSelectedUser(
			PortletRequest portletRequest, boolean checkPermission)
		throws PortalException {

		return _portal.getSelectedUser(portletRequest, checkPermission);
	}

	public static String getServletContextName() {
		return _portal.getServletContextName();
	}

	public static long[] getSharedContentSiteGroupIds(
			long companyId, long groupId, long userId)
		throws PortalException {

		return _portal.getSharedContentSiteGroupIds(companyId, groupId, userId);
	}

	public static String getSiteAdminURL(
			ThemeDisplay themeDisplay, String ppid,
			Map<String, String[]> params)
		throws PortalException {

		return _portal.getSiteAdminURL(themeDisplay, ppid, params);
	}

	public static Locale getSiteDefaultLocale(Group group)
		throws PortalException {

		return _portal.getSiteDefaultLocale(group);
	}

	public static Locale getSiteDefaultLocale(long groupId)
		throws PortalException {

		return _portal.getSiteDefaultLocale(groupId);
	}

	public static long getSiteGroupId(long scopeGroupId) {
		return _portal.getSiteGroupId(scopeGroupId);
	}

	public static String getSiteLoginURL(ThemeDisplay themeDisplay)
		throws PortalException {

		return _portal.getSiteLoginURL(themeDisplay);
	}

	public static String getStaticResourceURL(
		HttpServletRequest httpServletRequest, String uri) {

		return _portal.getStaticResourceURL(httpServletRequest, uri);
	}

	public static String getStaticResourceURL(
		HttpServletRequest httpServletRequest, String uri, long timestamp) {

		return _portal.getStaticResourceURL(httpServletRequest, uri, timestamp);
	}

	public static String getStaticResourceURL(
		HttpServletRequest httpServletRequest, String uri, String queryString) {

		return _portal.getStaticResourceURL(
			httpServletRequest, uri, queryString);
	}

	public static String getStaticResourceURL(
		HttpServletRequest httpServletRequest, String uri, String queryString,
		long timestamp) {

		return _portal.getStaticResourceURL(
			httpServletRequest, uri, queryString, timestamp);
	}

	public static String getStrutsAction(
		HttpServletRequest httpServletRequest) {

		return _portal.getStrutsAction(httpServletRequest);
	}

	public static String[] getSystemGroups() {
		return _portal.getSystemGroups();
	}

	public static String[] getSystemOrganizationRoles() {
		return _portal.getSystemOrganizationRoles();
	}

	public static String[] getSystemRoles() {
		return _portal.getSystemRoles();
	}

	public static String[] getSystemSiteRoles() {
		return _portal.getSystemSiteRoles();
	}

	public static String getUniqueElementId(
		HttpServletRequest httpServletRequest, String namespace, String id) {

		return _portal.getUniqueElementId(httpServletRequest, namespace, id);
	}

	public static String getUniqueElementId(
		PortletRequest request, String namespace, String id) {

		return _portal.getUniqueElementId(request, namespace, id);
	}

	public static UploadPortletRequest getUploadPortletRequest(
		PortletRequest portletRequest) {

		return _portal.getUploadPortletRequest(portletRequest);
	}

	public static UploadServletRequest getUploadServletRequest(
		HttpServletRequest httpServletRequest) {

		return _portal.getUploadServletRequest(httpServletRequest);
	}

	public static UploadServletRequest getUploadServletRequest(
		HttpServletRequest httpServletRequest, int fileSizeThreshold,
		String location) {

		return _portal.getUploadServletRequest(
			httpServletRequest, fileSizeThreshold, location);
	}

	public static Date getUptime() {
		return _portal.getUptime();
	}

	public static String getURLWithSessionId(String url, String sessionId) {
		return _portal.getURLWithSessionId(url, sessionId);
	}

	public static User getUser(HttpServletRequest httpServletRequest)
		throws PortalException {

		return _portal.getUser(httpServletRequest);
	}

	public static User getUser(PortletRequest portletRequest)
		throws PortalException {

		return _portal.getUser(portletRequest);
	}

	public static String getUserEmailAddress(long userId) {
		return _portal.getUserEmailAddress(userId);
	}

	public static long getUserId(HttpServletRequest httpServletRequest) {
		return _portal.getUserId(httpServletRequest);
	}

	public static long getUserId(PortletRequest portletRequest) {
		return _portal.getUserId(portletRequest);
	}

	public static String getUserName(BaseModel<?> baseModel) {
		return _portal.getUserName(baseModel);
	}

	public static String getUserName(long userId, String defaultUserName) {
		return _portal.getUserName(userId, defaultUserName);
	}

	public static String getUserName(
		long userId, String defaultUserName,
		HttpServletRequest httpServletRequest) {

		return _portal.getUserName(userId, defaultUserName, httpServletRequest);
	}

	public static String getUserName(
		long userId, String defaultUserName, String userAttribute) {

		return _portal.getUserName(userId, defaultUserName, userAttribute);
	}

	public static String getUserName(
		long userId, String defaultUserName, String userAttribute,
		HttpServletRequest httpServletRequest) {

		return _portal.getUserName(
			userId, defaultUserName, userAttribute, httpServletRequest);
	}

	public static String getUserPassword(
		HttpServletRequest httpServletRequest) {

		return _portal.getUserPassword(httpServletRequest);
	}

	public static String getUserPassword(HttpSession httpSession) {
		return _portal.getUserPassword(httpSession);
	}

	public static String getUserPassword(PortletRequest portletRequest) {
		return _portal.getUserPassword(portletRequest);
	}

	public static String getValidPortalDomain(long companyId, String domain) {
		return _portal.getValidPortalDomain(companyId, domain);
	}

	public static long getValidUserId(long companyId, long userId)
		throws PortalException {

		return _portal.getValidUserId(companyId, userId);
	}

	public static NavigableMap<String, String> getVirtualHostnames(
		LayoutSet layoutSet) {

		return _portal.getVirtualHostnames(layoutSet);
	}

	public static String getWidgetURL(
			Portlet portlet, ThemeDisplay themeDisplay)
		throws PortalException {

		return _portal.getWidgetURL(portlet, themeDisplay);
	}

	public static User initUser(HttpServletRequest httpServletRequest)
		throws Exception {

		return _portal.initUser(httpServletRequest);
	}

	public static boolean isCDNDynamicResourcesEnabled(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		return _portal.isCDNDynamicResourcesEnabled(httpServletRequest);
	}

	public static boolean isCDNDynamicResourcesEnabled(long companyId) {
		return _portal.isCDNDynamicResourcesEnabled(companyId);
	}

	public static boolean isCompanyAdmin(User user) throws Exception {
		return _portal.isCompanyAdmin(user);
	}

	public static boolean isCustomPortletMode(PortletMode portletMode) {
		return _portal.isCustomPortletMode(portletMode);
	}

	public static boolean isForwardedSecure(
		HttpServletRequest httpServletRequest) {

		return _portal.isForwardedSecure(httpServletRequest);
	}

	public static boolean isGroupAdmin(User user, long groupId)
		throws Exception {

		return _portal.isGroupAdmin(user, groupId);
	}

	public static boolean isGroupControlPanelPath(String path) {
		return _portal.isGroupControlPanelPath(path);
	}

	public static boolean isGroupFriendlyURL(
		String fullURL, String groupFriendlyURL, String layoutFriendlyURL) {

		return _portal.isGroupFriendlyURL(
			fullURL, groupFriendlyURL, layoutFriendlyURL);
	}

	public static boolean isGroupOwner(User user, long groupId)
		throws Exception {

		return _portal.isGroupOwner(user, groupId);
	}

	public static boolean isLayoutDescendant(Layout layout, long layoutId)
		throws PortalException {

		return _portal.isLayoutDescendant(layout, layoutId);
	}

	public static boolean isLayoutSitemapable(Layout layout) {
		return _portal.isLayoutSitemapable(layout);
	}

	public static boolean isLoginRedirectRequired(
		HttpServletRequest httpServletRequest) {

		return _portal.isLoginRedirectRequired(httpServletRequest);
	}

	public static boolean isMultipartRequest(
		HttpServletRequest httpServletRequest) {

		return _portal.isMultipartRequest(httpServletRequest);
	}

	public static boolean isOmniadmin(long userId) {
		return _portal.isOmniadmin(userId);
	}

	public static boolean isOmniadmin(User user) {
		return _portal.isOmniadmin(user);
	}

	public static boolean isReservedParameter(String name) {
		return _portal.isReservedParameter(name);
	}

	public static boolean isRightToLeft(HttpServletRequest httpServletRequest) {
		return _portal.isRightToLeft(httpServletRequest);
	}

	public static boolean isRSSFeedsEnabled() {
		return _portal.isRSSFeedsEnabled();
	}

	public static boolean isSecure(HttpServletRequest httpServletRequest) {
		return _portal.isSecure(httpServletRequest);
	}

	public static boolean isSkipPortletContentRendering(
		Group group, LayoutTypePortlet layoutTypePortlet,
		PortletDisplay portletDisplay, String portletName) {

		return _portal.isSkipPortletContentRendering(
			group, layoutTypePortlet, portletDisplay, portletName);
	}

	public static boolean isSystemGroup(String groupName) {
		return _portal.isSystemGroup(groupName);
	}

	public static boolean isSystemRole(String roleName) {
		return _portal.isSystemRole(roleName);
	}

	public static boolean isValidPortalDomain(long companyId, String domain) {
		return _portal.isValidPortalDomain(companyId, domain);
	}

	public static boolean isValidResourceId(String resourceId) {
		return _portal.isValidResourceId(resourceId);
	}

	public static void resetCDNHosts() {
		_portal.resetCDNHosts();
	}

	public static void sendError(
			Exception exception, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws IOException {

		_portal.sendError(exception, actionRequest, actionResponse);
	}

	public static void sendError(
			Exception exception, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		_portal.sendError(exception, httpServletRequest, httpServletResponse);
	}

	public static void sendError(
			int status, Exception exception, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws IOException {

		_portal.sendError(status, exception, actionRequest, actionResponse);
	}

	public static void sendError(
			int status, Exception exception,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		_portal.sendError(
			status, exception, httpServletRequest, httpServletResponse);
	}

	public static void sendRSSFeedsDisabledError(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		_portal.sendRSSFeedsDisabledError(
			httpServletRequest, httpServletResponse);
	}

	public static void sendRSSFeedsDisabledError(
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws IOException, ServletException {

		_portal.sendRSSFeedsDisabledError(portletRequest, portletResponse);
	}

	/**
	 * Sets the description for a page. This overrides the existing page
	 * description.
	 */
	public static void setPageDescription(
		String description, HttpServletRequest httpServletRequest) {

		_portal.setPageDescription(description, httpServletRequest);
	}

	/**
	 * Sets the keywords for a page. This overrides the existing page keywords.
	 */
	public static void setPageKeywords(
		String keywords, HttpServletRequest httpServletRequest) {

		_portal.setPageKeywords(keywords, httpServletRequest);
	}

	/**
	 * Sets the subtitle for a page. This overrides the existing page subtitle.
	 */
	public static void setPageSubtitle(
		String subtitle, HttpServletRequest httpServletRequest) {

		_portal.setPageSubtitle(subtitle, httpServletRequest);
	}

	/**
	 * Sets the whole title for a page. This overrides the existing page whole
	 * title.
	 */
	public static void setPageTitle(
		String title, HttpServletRequest httpServletRequest) {

		_portal.setPageTitle(title, httpServletRequest);
	}

	public static void setPortalInetSocketAddresses(
		HttpServletRequest httpServletRequest) {

		_portal.setPortalInetSocketAddresses(httpServletRequest);
	}

	public static String[] stripURLAnchor(String url, String separator) {
		return _portal.stripURLAnchor(url, separator);
	}

	public static String transformCustomSQL(String sql) {
		return _portal.transformCustomSQL(sql);
	}

	public static String transformSQL(String sql) {
		return _portal.transformSQL(sql);
	}

	public static void updateImageId(
			BaseModel<?> baseModel, boolean hasImage, byte[] bytes,
			String fieldName, long maxSize, int maxHeight, int maxWidth)
		throws PortalException {

		_portal.updateImageId(
			baseModel, hasImage, bytes, fieldName, maxSize, maxHeight,
			maxWidth);
	}

	public static PortletMode updatePortletMode(
			String portletId, User user, Layout layout, PortletMode portletMode,
			HttpServletRequest httpServletRequest)
		throws PortalException {

		return _portal.updatePortletMode(
			portletId, user, layout, portletMode, httpServletRequest);
	}

	public static String updateRedirect(
		String redirect, String oldPath, String newPath) {

		return _portal.updateRedirect(redirect, oldPath, newPath);
	}

	public static WindowState updateWindowState(
		String portletId, User user, Layout layout, WindowState windowState,
		HttpServletRequest httpServletRequest) {

		return _portal.updateWindowState(
			portletId, user, layout, windowState, httpServletRequest);
	}

	public void setPortal(Portal portal) {
		_portal = portal;
	}

	private static Portal _portal;

}