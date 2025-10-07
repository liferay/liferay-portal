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
import com.liferay.portal.kernel.portlet.LayoutFriendlyURLSeparatorComposite;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
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

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian Wing Shun Chan
 * @author Eduardo Lundgren
 * @author Marco Leo
 */
@ProviderType
public interface Portal {

	public static final String FRIENDLY_URL_SEPARATOR = "/-/";

	public static final String JSESSIONID = ";jsessionid=";

	public static final String PATH_IMAGE = "/image";

	public static final String PATH_MAIN = "/c";

	public static final String PATH_MODULE = "/o";

	public static final String PATH_PORTAL_LAYOUT = "/portal/layout";

	public static final String PORTAL_REALM = "PortalRealm";

	public static final String PORTLET_XML_FILE_NAME_CUSTOM =
		"portlet-custom.xml";

	public static final String PORTLET_XML_FILE_NAME_STANDARD = "portlet.xml";

	public static final String TEMP_OBFUSCATION_VALUE =
		"TEMP_OBFUSCATION_VALUE";

	/**
	 * Appends the description to the current meta description of the page.
	 *
	 * @param description the description to append to the current meta
	 *        description
	 * @param httpServletRequest the servlet request for the page
	 */
	public void addPageDescription(
		String description, HttpServletRequest httpServletRequest);

	/**
	 * Appends the keywords to the current meta keywords of the page.
	 *
	 * @param keywords the keywords to add to the current meta keywords
	 *        (comma-separated)
	 * @param httpServletRequest the servlet request for the page
	 */
	public void addPageKeywords(
		String keywords, HttpServletRequest httpServletRequest);

	/**
	 * Appends the subtitle to the current subtitle of the page.
	 *
	 * @param subtitle the subtitle to append to the current subtitle
	 * @param httpServletRequest the servlet request for the page
	 */
	public void addPageSubtitle(
		String subtitle, HttpServletRequest httpServletRequest);

	/**
	 * Appends the title to the current title of the page.
	 *
	 * @param title the title to append to the current title
	 * @param httpServletRequest the servlet request for the page
	 */
	public void addPageTitle(
		String title, HttpServletRequest httpServletRequest);

	/**
	 * Adds an entry to the portlet breadcrumbs for the page.
	 *
	 * @param httpServletRequest the servlet request for the page
	 * @param title the title of the new breakcrumb entry
	 * @param url the URL of the new breadcrumb entry
	 */
	public void addPortletBreadcrumbEntry(
		HttpServletRequest httpServletRequest, String title, String url);

	/**
	 * Adds an entry to the portlet breadcrumbs for the page.
	 *
	 * @param httpServletRequest the servlet request for the page
	 * @param title the title of the new breakcrumb entry
	 * @param url the URL of the new breadcrumb entry
	 * @param data the HTML5 data parameters of the new breadcrumb entry
	 */
	public void addPortletBreadcrumbEntry(
		HttpServletRequest httpServletRequest, String title, String url,
		Map<String, Object> data);

	/**
	 * Adds an entry to the portlet breadcrumbs for the page.
	 *
	 * @param httpServletRequest the servlet request for the page
	 * @param title the title of the new breakcrumb entry
	 * @param url the URL of the new breadcrumb entry
	 * @param data the HTML5 data parameters of the new breadcrumb entry
	 * @param portletBreadcrumbEntry whether the entry is a portlet breadcrumb
	 *        entry
	 */
	public void addPortletBreadcrumbEntry(
		HttpServletRequest httpServletRequest, String title, String url,
		Map<String, Object> data, boolean portletBreadcrumbEntry);

	/**
	 * Adds the default resource permissions for the portlet to the page.
	 *
	 * @param  httpServletRequest the servlet request for the page
	 * @param  portlet the portlet
	 * @throws PortalException if a portal exception occurred
	 */
	public void addPortletDefaultResource(
			HttpServletRequest httpServletRequest, Portlet portlet)
		throws PortalException;

	public void addPortletDefaultResource(
			long companyId, Layout layout, Portlet portlet)
		throws PortalException;

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
	 * @param  layout the current layout
	 * @param  url the URL
	 * @param  doAsUser whether to include doAsUserId and doAsLanguageId in the
	 *         URL if they are available. If <code>false</code>, doAsUserId and
	 *         doAsUserLanguageId will never be added.
	 * @return the URL with the preserved parameters added
	 */
	public String addPreservedParameters(
		ThemeDisplay themeDisplay, Layout layout, String url, boolean doAsUser);

	/**
	 * Adds the preserved parameters doAsUserId, doAsUserLanguageId,
	 * doAsGroupId, and refererPlid to the URL.
	 *
	 * @param  themeDisplay the current theme display
	 * @param  url the URL
	 * @return the URL with the preserved parameters added
	 */
	public String addPreservedParameters(ThemeDisplay themeDisplay, String url);

	public String addPreservedParameters(
		ThemeDisplay themeDisplay, String url, boolean typeControlPanel,
		boolean doAsUser);

	/**
	 * Copies the request parameters to the render parameters, unless a
	 * parameter with that name already exists in the render parameters.
	 *
	 * @param actionRequest the request from which to get the request parameters
	 * @param actionResponse the response to receive the render parameters
	 */
	public void copyRequestParameters(
		ActionRequest actionRequest, ActionResponse actionResponse);

	/**
	 * Escapes the URL for use in a redirect and checks that security settings
	 * allow the URL is allowed for redirects.
	 *
	 * @param  url the URL to escape
	 * @return the escaped URL, or <code>null</code> if the URL is not an
	 *         allowed for redirects
	 */
	public String escapeRedirect(String url);

	public String fetchClassName(long classNameId);

	/**
	 * Generates a random key to identify the request based on the input string.
	 *
	 * @param  httpServletRequest the servlet request for the page
	 * @param  input the input string
	 * @return the generated key
	 */
	public String generateRandomKey(
		HttpServletRequest httpServletRequest, String input);

	public String getAbsoluteURL(
		HttpServletRequest httpServletRequest, String url);

	public LayoutQueryStringComposite getActualLayoutQueryStringComposite(
			long groupId, boolean privateLayout, String friendlyURL,
			Map<String, String[]> params, Map<String, Object> requestContext)
		throws PortalException;

	public String getActualURL(
			long groupId, boolean privateLayout, String mainPath,
			String friendlyURL, Map<String, String[]> params,
			Map<String, Object> requestContext)
		throws PortalException;

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
	 * @param  locale the locale of the alternate (translated) page
	 * @param  layout the page being requested
	 * @return the alternate URL for the requested canonical URL in the given
	 *         locale
	 * @throws PortalException if a portal exception occurred
	 */
	public String getAlternateURL(
			String canonicalURL, ThemeDisplay themeDisplay, Locale locale,
			Layout layout)
		throws PortalException;

	public Map<Locale, String> getAlternateURLs(
			String canonicalURL, ThemeDisplay themeDisplay, Layout layout)
		throws PortalException;

	public Map<Locale, String> getAlternateURLs(
			String canonicalURL, ThemeDisplay themeDisplay, Layout layout,
			Set<Locale> availableLocales)
		throws PortalException;

	public long[] getAncestorSiteGroupIds(long groupId);

	/**
	 * Returns the canonical URL of the page, to distinguish it among its
	 * translations.
	 *
	 * @param  completeURL the complete URL of the page
	 * @param  themeDisplay the current theme display
	 * @param  layout the layout. If it is <code>null</code>, then it is
	 *         generated for the current layout
	 * @return the canonical URL
	 * @throws PortalException if a portal exception occurred
	 */
	public String getCanonicalURL(
			String completeURL, ThemeDisplay themeDisplay, Layout layout)
		throws PortalException;

	/**
	 * Returns the canonical URL of the page, to distinguish it among its
	 * translations.
	 *
	 * @param  completeURL the complete URL of the page
	 * @param  themeDisplay the current theme display
	 * @param  layout the layout. If it is <code>null</code>, then it is
	 *         generated for the current layout
	 * @param  forceLayoutFriendlyURL adds the page friendly URL to the
	 *         canonical URL even if it is not needed
	 * @return the canonical URL
	 * @throws PortalException if a portal exception occurred
	 */
	public String getCanonicalURL(
			String completeURL, ThemeDisplay themeDisplay, Layout layout,
			boolean forceLayoutFriendlyURL)
		throws PortalException;

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
	public String getCanonicalURL(
			String completeURL, ThemeDisplay themeDisplay, Layout layout,
			boolean forceLayoutFriendlyURL, boolean includeQueryString)
		throws PortalException;

	/**
	 * Returns the secure (HTTPS) or insecure (HTTP) content distribution
	 * network (CDN) host address for this portal.
	 *
	 * @param  secure whether to get the secure or insecure CDN host address
	 * @return the CDN host address
	 */
	public String getCDNHost(boolean secure);

	public String getCDNHost(HttpServletRequest httpServletRequest)
		throws PortalException;

	/**
	 * Returns the insecure (HTTP) content distribution network (CDN) host
	 * address
	 *
	 * @param  companyId the company ID of a site
	 * @return the CDN host address
	 */
	public String getCDNHostHttp(long companyId);

	/**
	 * Returns the secure (HTTPS) content distribution network (CDN) host
	 * address
	 *
	 * @param  companyId the company ID of a site
	 * @return the CDN host address
	 */
	public String getCDNHostHttps(long companyId);

	/**
	 * Returns the fully qualified name of the class from its ID.
	 *
	 * @param  classNameId the ID of the class
	 * @return the fully qualified name of the class
	 */
	public String getClassName(long classNameId);

	/**
	 * Returns the ID of the class from its class object.
	 *
	 * @param  clazz the class object
	 * @return the ID of the class
	 */
	public long getClassNameId(Class<?> clazz);

	/**
	 * Returns the ID of the class from its fully qualified name.
	 *
	 * @param  value the fully qualified name of the class
	 * @return the ID of the class
	 */
	public long getClassNameId(String value);

	public Company getCompany(HttpServletRequest httpServletRequest)
		throws PortalException;

	public Company getCompany(PortletRequest portletRequest)
		throws PortalException;

	public long getCompanyId(HttpServletRequest httpServletRequest);

	public long getCompanyId(PortletRequest portletRequest);

	public long[] getCompanyIds();

	public Set<String> getComputerAddresses();

	public String getComputerName();

	public String getControlPanelFullURL(
			long scopeGroupId, String ppid, Map<String, String[]> params)
		throws PortalException;

	public long getControlPanelPlid(long companyId) throws PortalException;

	public long getControlPanelPlid(PortletRequest portletRequest)
		throws PortalException;

	public PortletURL getControlPanelPortletURL(
		HttpServletRequest httpServletRequest, Group group, String portletId,
		long refererGroupId, long refererPlid, String lifecycle);

	public PortletURL getControlPanelPortletURL(
		HttpServletRequest httpServletRequest, String portletId,
		String lifecycle);

	public PortletURL getControlPanelPortletURL(
		PortletRequest portletRequest, Group group, String portletId,
		long refererGroupId, long refererPlid, String lifecycle);

	public PortletURL getControlPanelPortletURL(
		PortletRequest portletRequest, String portletId, String lifecycle);

	public String getCreateAccountURL(
			HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay)
		throws Exception;

	public long[] getCurrentAndAncestorSiteGroupIds(long groupId);

	public long[] getCurrentAndAncestorSiteGroupIds(
		long groupId, boolean checkContentSharingWithChildrenEnabled);

	public long[] getCurrentAndAncestorSiteGroupIds(long[] groupIds);

	public long[] getCurrentAndAncestorSiteGroupIds(
		long[] groupIds, boolean checkContentSharingWithChildrenEnabled);

	public List<Group> getCurrentAndAncestorSiteGroups(
		long groupId, boolean checkContentSharingWithChildrenEnabled);

	public List<Group> getCurrentAndAncestorSiteGroups(
		long[] groupIds, boolean checkContentSharingWithChildrenEnabled);

	public String getCurrentCompleteURL(HttpServletRequest httpServletRequest);

	public String getCurrentURL(HttpServletRequest httpServletRequest);

	public String getCurrentURL(PortletRequest portletRequest);

	public String getCustomSQLFunctionIsNotNull();

	public String getCustomSQLFunctionIsNull();

	/**
	 * Returns the date object for the specified month, day, and year, or
	 * <code>null</code> if the date is invalid.
	 *
	 * @param  month the month (0-based, meaning 0 for January)
	 * @param  day the day of the month
	 * @param  year the year
	 * @return the date object, or <code>null</code> if the date is invalid
	 */
	public Date getDate(int month, int day, int year);

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
	public Date getDate(
			int month, int day, int year,
			Class<? extends PortalException> clazz)
		throws PortalException;

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
	public Date getDate(
			int month, int day, int year, int hour, int min,
			Class<? extends PortalException> clazz)
		throws PortalException;

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
	public Date getDate(
			int month, int day, int year, int hour, int min, TimeZone timeZone,
			Class<? extends PortalException> clazz)
		throws PortalException;

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
	public Date getDate(
			int month, int day, int year, TimeZone timeZone,
			Class<? extends PortalException> clazz)
		throws PortalException;

	public long getDefaultCompanyId();

	public String getEmailFromAddress(
		PortletPreferences portletPreferences, long companyId,
		String defaultValue);

	public String getEmailFromName(
		PortletPreferences portletPreferences, long companyId,
		String defaultValue);

	public String getForwardedHost(HttpServletRequest httpServletRequest);

	public int getForwardedPort(HttpServletRequest httpServletRequest);

	public String getFullName(
		String firstName, String middleName, String lastName);

	public String getGoogleGadgetURL(Portlet portlet, ThemeDisplay themeDisplay)
		throws PortalException;

	public String getGroupFriendlyURL(
			LayoutSet layoutSet, ThemeDisplay themeDisplay,
			boolean canonicalURL, boolean controlPanel)
		throws PortalException;

	public String getGroupFriendlyURL(
			LayoutSet layoutSet, ThemeDisplay themeDisplay, Locale locale)
		throws PortalException;

	public int[] getGroupFriendlyURLIndex(String requestURI);

	public String getHomeURL(HttpServletRequest httpServletRequest)
		throws PortalException;

	public String getHost(HttpServletRequest httpServletRequest);

	public String getHost(PortletRequest portletRequest);

	public HttpServletRequest getHttpServletRequest(
		PortletRequest portletRequest);

	public HttpServletResponse getHttpServletResponse(
		PortletResponse portletResponse);

	public String getI18nPathLanguageId(
		Locale locale, String defaultI18nPathLanguageId);

	public String getJsSafePortletId(String portletId);

	public String getLayoutActualURL(Layout layout);

	public String getLayoutActualURL(Layout layout, String mainPath);

	public String getLayoutActualURL(
			long groupId, boolean privateLayout, String mainPath,
			String friendlyURL, Map<String, String[]> params,
			Map<String, Object> requestContext)
		throws PortalException;

	public String getLayoutFriendlyURL(Layout layout, ThemeDisplay themeDisplay)
		throws PortalException;

	public String getLayoutFriendlyURL(
			Layout layout, ThemeDisplay themeDisplay, Locale locale)
		throws PortalException;

	public String getLayoutFriendlyURL(ThemeDisplay themeDisplay)
		throws PortalException;

	public LayoutFriendlyURLSeparatorComposite
			getLayoutFriendlyURLSeparatorComposite(
				long groupId, boolean privateLayout, String friendlyURL,
				Map<String, String[]> params,
				Map<String, Object> requestContext)
		throws PortalException;

	public String getLayoutFullURL(Layout layout, ThemeDisplay themeDisplay)
		throws PortalException;

	public String getLayoutFullURL(
			Layout layout, ThemeDisplay themeDisplay, boolean doAsUser)
		throws PortalException;

	public String getLayoutFullURL(long groupId, String portletId)
		throws PortalException;

	public String getLayoutFullURL(
			long groupId, String portletId, boolean secure)
		throws PortalException;

	public String getLayoutFullURL(ThemeDisplay themeDisplay)
		throws PortalException;

	public String getLayoutRelativeURL(Layout layout, ThemeDisplay themeDisplay)
		throws PortalException;

	public String getLayoutRelativeURL(
			Layout layout, ThemeDisplay themeDisplay, boolean doAsUser)
		throws PortalException;

	public String getLayoutSetDisplayURL(
			LayoutSet layoutSet, boolean secureConnection)
		throws PortalException;

	public String getLayoutSetFriendlyURL(
			LayoutSet layoutSet, ThemeDisplay themeDisplay)
		throws PortalException;

	public String getLayoutTarget(Layout layout);

	public String getLayoutURL(Layout layout, ThemeDisplay themeDisplay)
		throws PortalException;

	public String getLayoutURL(
			Layout layout, ThemeDisplay themeDisplay, boolean doAsUser)
		throws PortalException;

	public String getLayoutURL(
			Layout layout, ThemeDisplay themeDisplay, Locale locale)
		throws PortalException;

	public String getLayoutURL(ThemeDisplay themeDisplay)
		throws PortalException;

	public LiferayPortletRequest getLiferayPortletRequest(
		PortletRequest portletRequest);

	public LiferayPortletResponse getLiferayPortletResponse(
		PortletResponse portletResponse);

	public Locale getLocale(HttpServletRequest httpServletRequest);

	public Locale getLocale(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, boolean initialize);

	public Locale getLocale(PortletRequest portletRequest);

	public String getNetvibesURL(Portlet portlet, ThemeDisplay themeDisplay)
		throws PortalException;

	public String getNewPortletTitle(
		String portletTitle, String oldScopeName, String newScopeName);

	public HttpServletRequest getOriginalServletRequest(
		HttpServletRequest httpServletRequest);

	public String getPathContext();

	public String getPathContext(HttpServletRequest httpServletRequest);

	public String getPathContext(PortletRequest portletRequest);

	public String getPathContext(String contextPath);

	public String getPathFriendlyURLPrivateGroup();

	public String getPathFriendlyURLPrivateUser();

	public String getPathFriendlyURLPublic();

	public String getPathImage();

	public String getPathMain();

	public String getPathModule();

	public String getPathProxy();

	public long getPlidFromFriendlyURL(long companyId, String friendlyURL);

	public long getPlidFromPortletId(
			long groupId, boolean privateLayout, String portletId)
		throws PortalException;

	public long getPlidFromPortletId(long groupId, String portletId)
		throws PortalException;

	public int getPortalLocalPort(boolean secure);

	public Properties getPortalProperties();

	public int getPortalServerPort(boolean secure);

	public String getPortalURL(HttpServletRequest httpServletRequest);

	public String getPortalURL(
		HttpServletRequest httpServletRequest, boolean secure);

	public String getPortalURL(Layout layout, ThemeDisplay themeDisplay)
		throws PortalException;

	public String getPortalURL(LayoutSet layoutSet, ThemeDisplay themeDisplay);

	public String getPortalURL(PortletRequest portletRequest);

	public String getPortalURL(PortletRequest portletRequest, boolean secure);

	public String getPortalURL(
		String serverName, int serverPort, boolean secure);

	public String getPortalURL(ThemeDisplay themeDisplay)
		throws PortalException;

	public PortletConfig getPortletConfig(
			long companyId, String portletId, ServletContext servletContext)
		throws PortletException;

	public String getPortletDescription(
		Portlet portlet, ServletContext servletContext, Locale locale);

	public String getPortletDescription(Portlet portlet, User user);

	public String getPortletDescription(String portletId, Locale locale);

	public String getPortletDescription(String portletId, String languageId);

	public String getPortletDescription(String portletId, User user);

	public LayoutQueryStringComposite
		getPortletFriendlyURLMapperLayoutQueryStringComposite(
			String url, Map<String, String[]> params,
			Map<String, Object> requestContext);

	public String getPortletId(HttpServletRequest httpServletRequest);

	public String getPortletId(PortletRequest portletRequest);

	public String getPortletLongTitle(
		Portlet portlet, ServletContext servletContext, Locale locale);

	public String getPortletLongTitle(String portletId, Locale locale);

	public String getPortletNamespace(String portletId);

	public String getPortletTitle(Portlet portlet, Locale locale);

	public String getPortletTitle(
		Portlet portlet, ServletContext servletContext, Locale locale);

	public String getPortletTitle(Portlet portlet, String languageId);

	public String getPortletTitle(Portlet portlet, User user);

	public String getPortletTitle(PortletRequest portletRequest);

	public String getPortletTitle(PortletResponse portletResponse);

	public String getPortletTitle(String portletId, Locale locale);

	public String getPortletTitle(
		String portletId, ResourceBundle resourceBundle);

	public String getPortletTitle(String portletId, String languageId);

	public String getPortletTitle(String portletId, User user);

	public PortletPreferences getPreferences(
		HttpServletRequest httpServletRequest);

	public PreferencesValidator getPreferencesValidator(Portlet portlet);

	public String getRelativeHomeURL(HttpServletRequest httpServletRequest)
		throws PortalException;

	public ResourceBundle getResourceBundle(Locale locale);

	public long getScopeGroupId(HttpServletRequest httpServletRequest)
		throws PortalException;

	public long getScopeGroupId(
			HttpServletRequest httpServletRequest, String portletId)
		throws PortalException;

	public long getScopeGroupId(
			HttpServletRequest httpServletRequest, String portletId,
			boolean checkStagingGroup)
		throws PortalException;

	public long getScopeGroupId(Layout layout);

	public long getScopeGroupId(Layout layout, String portletId);

	public long getScopeGroupId(long plid);

	public long getScopeGroupId(PortletRequest portletRequest)
		throws PortalException;

	public User getSelectedUser(HttpServletRequest httpServletRequest)
		throws PortalException;

	public User getSelectedUser(
			HttpServletRequest httpServletRequest, boolean checkPermission)
		throws PortalException;

	public User getSelectedUser(PortletRequest portletRequest)
		throws PortalException;

	public User getSelectedUser(
			PortletRequest portletRequest, boolean checkPermission)
		throws PortalException;

	public String getServletContextName();

	public long[] getSharedContentSiteGroupIds(
			long companyId, long groupId, long userId)
		throws PortalException;

	public String getSiteAdminURL(
			String portalURL, Group group, String ppid,
			Map<String, String[]> params)
		throws PortalException;

	public String getSiteAdminURL(
			ThemeDisplay themeDisplay, String ppid,
			Map<String, String[]> params)
		throws PortalException;

	public Locale getSiteDefaultLocale(Group group) throws PortalException;

	public Locale getSiteDefaultLocale(long groupId) throws PortalException;

	public long getSiteGroupId(long groupId);

	/**
	 * Returns the URL of the login page for the current site if one is
	 * available.
	 *
	 * @param  themeDisplay the theme display for the current page
	 * @return the URL of the login page for the current site, or
	 *         <code>null</code> if one is not available
	 * @throws PortalException if a portal exception occurred
	 */
	public String getSiteLoginURL(ThemeDisplay themeDisplay)
		throws PortalException;

	public String getStaticResourceURL(
		HttpServletRequest httpServletRequest, String uri);

	public String getStaticResourceURL(
		HttpServletRequest httpServletRequest, String uri, long timestamp);

	public String getStaticResourceURL(
		HttpServletRequest httpServletRequest, String uri, String queryString);

	public String getStaticResourceURL(
		HttpServletRequest httpServletRequest, String uri, String queryString,
		long timestamp);

	public String getStrutsAction(HttpServletRequest httpServletRequest);

	public String[] getSystemGroups();

	public String[] getSystemOrganizationRoles();

	public String[] getSystemRoles();

	public String[] getSystemSiteRoles();

	public String getUniqueElementId(
		HttpServletRequest httpServletRequest, String namespace, String id);

	public String getUniqueElementId(
		PortletRequest request, String namespace, String id);

	public UploadPortletRequest getUploadPortletRequest(
		PortletRequest portletRequest);

	public UploadServletRequest getUploadServletRequest(
		HttpServletRequest httpServletRequest);

	public UploadServletRequest getUploadServletRequest(
		HttpServletRequest httpServletRequest, int fileSizeThreshold,
		String location);

	public Date getUptime();

	public String getURLWithSessionId(String url, String sessionId);

	public User getUser(HttpServletRequest httpServletRequest)
		throws PortalException;

	public User getUser(PortletRequest portletRequest) throws PortalException;

	public String getUserEmailAddress(long userId);

	public long getUserId(HttpServletRequest httpServletRequest);

	public long getUserId(PortletRequest portletRequest);

	public String getUserName(BaseModel<?> baseModel);

	public String getUserName(long userId, String defaultUserName);

	public String getUserName(
		long userId, String defaultUserName,
		HttpServletRequest httpServletRequest);

	public String getUserName(
		long userId, String defaultUserName, String userAttribute);

	public String getUserName(
		long userId, String defaultUserName, String userAttribute,
		HttpServletRequest httpServletRequest);

	public String getUserPassword(HttpServletRequest httpServletRequest);

	public String getUserPassword(HttpSession httpSession);

	public String getUserPassword(PortletRequest portletRequest);

	public String getValidPortalDomain(long companyId, String domain);

	public long getValidUserId(long companyId, long userId)
		throws PortalException;

	public NavigableMap<String, String> getVirtualHostnames(
		LayoutSet layoutSet);

	public String getWidgetURL(Portlet portlet, ThemeDisplay themeDisplay)
		throws PortalException;

	public User initUser(HttpServletRequest httpServletRequest)
		throws Exception;

	public boolean isCDNDynamicResourcesEnabled(
			HttpServletRequest httpServletRequest)
		throws PortalException;

	public boolean isCDNDynamicResourcesEnabled(long companyId);

	public boolean isCompanyAdmin(User user) throws Exception;

	public boolean isCustomPortletMode(PortletMode portletMode);

	public boolean isForwardedSecure(HttpServletRequest httpServletRequest);

	public boolean isGroupAdmin(User user, long groupId) throws Exception;

	public boolean isGroupControlPanelPath(String path);

	public boolean isGroupFriendlyURL(
		String fullURL, String groupFriendlyURL, String layoutFriendlyURL);

	public boolean isGroupOwner(User user, long groupId) throws Exception;

	public boolean isLayoutDescendant(Layout layout, long layoutId)
		throws PortalException;

	public boolean isLayoutSitemapable(Layout layout);

	public boolean isLoginRedirectRequired(
		HttpServletRequest httpServletRequest);

	public boolean isMultipartRequest(HttpServletRequest httpServletRequest);

	public boolean isOmniadmin(long userId);

	public boolean isOmniadmin(User user);

	public boolean isReservedParameter(String name);

	public boolean isRightToLeft(HttpServletRequest httpServletRequest);

	public boolean isRSSFeedsEnabled();

	public boolean isSecure(HttpServletRequest httpServletRequest);

	public boolean isSkipPortletContentRendering(
		Group group, LayoutTypePortlet layoutTypePortlet,
		PortletDisplay portletDisplay, String portletName);

	public boolean isSystemGroup(String groupName);

	public boolean isSystemRole(String roleName);

	public boolean isValidPortalDomain(long companyId, String domain);

	public boolean isValidResourceId(String resourceId);

	public void resetCDNHosts();

	public void sendError(
			Exception exception, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws IOException;

	public void sendError(
			Exception exception, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException;

	public void sendError(
			int status, Exception exception, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws IOException;

	public void sendError(
			int status, Exception exception,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException;

	public void sendRSSFeedsDisabledError(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException;

	public void sendRSSFeedsDisabledError(
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws IOException, ServletException;

	/**
	 * Sets the description for the page, overriding the existing page
	 * description.
	 */
	public void setPageDescription(
		String description, HttpServletRequest httpServletRequest);

	/**
	 * Sets the keywords for the page, overriding the existing page keywords.
	 */
	public void setPageKeywords(
		String keywords, HttpServletRequest httpServletRequest);

	/**
	 * Sets the subtitle for the page, overriding the existing page subtitle.
	 */
	public void setPageSubtitle(
		String subtitle, HttpServletRequest httpServletRequest);

	/**
	 * Sets the whole title for the page, overriding the existing page whole
	 * title.
	 */
	public void setPageTitle(
		String title, HttpServletRequest httpServletRequest);

	public void setPortalInetSocketAddresses(
		HttpServletRequest httpServletRequest);

	public String[] stripURLAnchor(String url, String separator);

	public String transformCustomSQL(String sql);

	public String transformSQL(String sql);

	public void updateImageId(
			BaseModel<?> baseModel, boolean image, byte[] bytes,
			String fieldName, long maxSize, int maxHeight, int maxWidth)
		throws PortalException;

	public PortletMode updatePortletMode(
			String portletId, User user, Layout layout, PortletMode portletMode,
			HttpServletRequest httpServletRequest)
		throws PortalException;

	public String updateRedirect(
		String redirect, String oldPath, String newPath);

	public WindowState updateWindowState(
		String portletId, User user, Layout layout, WindowState windowState,
		HttpServletRequest httpServletRequest);

}