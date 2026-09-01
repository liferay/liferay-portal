/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.internal.manager;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.journal.model.JournalArticle;
import com.liferay.layout.admin.kernel.model.LayoutTypePortletConstants;
import com.liferay.object.model.ObjectEntry;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.lock.DuplicateLockException;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.lock.LockManager;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutTypeController;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.SchedulerException;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.TriggerFactory;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.DirectRequestDispatcherFactoryUtil;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Attribute;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReader;
import com.liferay.portal.lock.service.LockLocalService;
import com.liferay.portal.theme.ThemeDisplayFactory;
import com.liferay.portal.util.LayoutTypeControllerTracker;
import com.liferay.redirect.provider.RedirectProvider;
import com.liferay.site.configuration.manager.SitemapConfigurationManager;
import com.liferay.site.constants.SitemapConstants;
import com.liferay.site.internal.constants.SitemapDestinationNames;
import com.liferay.site.internal.scheduler.XMLSitemapRegenerationSchedulerJobConfiguration;
import com.liferay.site.manager.SitemapManager;
import com.liferay.site.provider.SitemapURLProvider;
import com.liferay.site.service.SiteSitemapRegenerationEntryLocalService;
import com.liferay.site.storage.helper.SitemapStorageHelper;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 * @author Vilmos Papp
 */
@Component(service = SitemapManager.class)
public class SitemapManagerImpl implements SitemapManager {

	@Override
	public void addURLElement(
		Element element, String url,
		UnicodeProperties typeSettingsUnicodeProperties, Date modifiedDate,
		String canonicalURL, Map<Locale, String> alternateURLs, long groupId) {

		String path = HttpComponentsUtil.getPath(url);
		String contextPath = _portal.getPathContext();

		if (Validator.isNotNull(contextPath) && path.startsWith(contextPath)) {
			path = path.substring(contextPath.length());
		}

		String friendlyURL = _getFriendlyURL(groupId, path);

		if (friendlyURL.startsWith(StringPool.SLASH)) {
			friendlyURL = friendlyURL.substring(1);
		}

		String fullURL = path;

		if (fullURL.startsWith(StringPool.SLASH)) {
			fullURL = fullURL.substring(1);
		}

		RedirectProvider.Redirect redirect = _redirectProvider.getRedirect(
			groupId, friendlyURL, fullURL, null);

		if (redirect != null) {
			return;
		}

		Element urlElement = element.addElement("url");

		Element locElement = urlElement.addElement("loc");

		locElement.addText(encodeXML(url));

		if (modifiedDate != null) {
			DateFormat iso8601DateFormat = DateUtil.getISO8601Format();

			Element lastmodElement = urlElement.addElement("lastmod");

			lastmodElement.addText(iso8601DateFormat.format(modifiedDate));
		}

		if (typeSettingsUnicodeProperties == null) {
			if (Validator.isNotNull(
					PropsValues.SITES_SITEMAP_DEFAULT_CHANGE_FREQUENCY)) {

				Element changefreqElement = urlElement.addElement("changefreq");

				changefreqElement.addText(
					PropsValues.SITES_SITEMAP_DEFAULT_CHANGE_FREQUENCY);
			}

			if (Validator.isNotNull(
					PropsValues.SITES_SITEMAP_DEFAULT_PRIORITY)) {

				Element priorityElement = urlElement.addElement("priority");

				priorityElement.addText(
					PropsValues.SITES_SITEMAP_DEFAULT_PRIORITY);
			}
		}
		else {
			String changefreq = typeSettingsUnicodeProperties.getProperty(
				"sitemap-changefreq");

			if (Validator.isNotNull(changefreq)) {
				Element changefreqElement = urlElement.addElement("changefreq");

				changefreqElement.addText(changefreq);
			}
			else if (Validator.isNotNull(
						PropsValues.SITES_SITEMAP_DEFAULT_CHANGE_FREQUENCY)) {

				Element changefreqElement = urlElement.addElement("changefreq");

				changefreqElement.addText(
					PropsValues.SITES_SITEMAP_DEFAULT_CHANGE_FREQUENCY);
			}

			String priority = typeSettingsUnicodeProperties.getProperty(
				"sitemap-priority");

			if (Validator.isNotNull(priority)) {
				Element priorityElement = urlElement.addElement("priority");

				priorityElement.addText(priority);
			}
			else if (Validator.isNotNull(
						PropsValues.SITES_SITEMAP_DEFAULT_PRIORITY)) {

				Element priorityElement = urlElement.addElement("priority");

				priorityElement.addText(
					PropsValues.SITES_SITEMAP_DEFAULT_PRIORITY);
			}
		}

		if (alternateURLs != null) {
			for (Map.Entry<Locale, String> entry : alternateURLs.entrySet()) {
				Locale locale = entry.getKey();
				String href = entry.getValue();

				Element alternateURLElement = urlElement.addElement(
					"xhtml:link", "http://www.w3.org/1999/xhtml");

				alternateURLElement.addAttribute("href", href);
				alternateURLElement.addAttribute(
					"hreflang", LocaleUtil.toW3cLanguageId(locale));
				alternateURLElement.addAttribute("rel", "alternate");
			}

			Element alternateURLElement = urlElement.addElement(
				"xhtml:link", "http://www.w3.org/1999/xhtml");

			alternateURLElement.addAttribute("href", canonicalURL);
			alternateURLElement.addAttribute("hreflang", "x-default");
			alternateURLElement.addAttribute("rel", "alternate");
		}

		if (element.attributeValue(_ATTRIBUTE_NAME_PAGE) == null) {
			_removeOldestElement(element, urlElement);

			return;
		}

		_addEntryAndPaginate(element, urlElement);
	}

	@Override
	public void deleteRegenerateSitemapScheduledJobs(long companyId)
		throws PortalException {

		for (SchedulerResponse schedulerResponse :
				_getRegenerateSitemapSchedulerResponses(companyId)) {

			try {
				_schedulerEngineHelper.delete(
					schedulerResponse.getJobName(),
					schedulerResponse.getGroupName(), StorageType.PERSISTED);
			}
			catch (SchedulerException schedulerException) {
				SchedulerResponse existingSchedulerResponse =
					_schedulerEngineHelper.getScheduledJob(
						schedulerResponse.getJobName(),
						schedulerResponse.getGroupName(),
						StorageType.PERSISTED);

				if (existingSchedulerResponse != null) {
					throw schedulerException;
				}
			}
		}
	}

	@Override
	public String encodeXML(String input) {
		return StringUtil.replace(
			input, new char[] {'&', '<', '>', '\'', '\"'},
			new String[] {"&amp;", "&lt;", "&gt;", "&apos;", "&quot;"});
	}

	@Override
	public Map<Locale, String> getAlternateURLs(
			String canonicalURL, ThemeDisplay themeDisplay, Layout layout)
		throws PortalException {

		return _portal.getAlternateURLs(canonicalURL, themeDisplay, layout);
	}

	@Override
	public long getAssetTypeClassNameId(String assetTypeKey) {
		return GetterUtil.getLong(_assetTypeClassNameIds.get(assetTypeKey));
	}

	@Override
	public Map<Long, String> getAssetTypeKeys() {
		return _assetTypeKeys;
	}

	@Override
	public Date getNextRegenerateSitemapDate(long companyId)
		throws PortalException {

		Date nextRegenerateSitemapDate = null;

		int siteSitemapRegenerationEntriesCount =
			_siteSitemapRegenerationEntryLocalService.
				getSiteSitemapRegenerationEntriesCount(companyId);

		if (siteSitemapRegenerationEntriesCount > 0) {
			Class<?> clazz =
				XMLSitemapRegenerationSchedulerJobConfiguration.class;

			SchedulerResponse schedulerResponse =
				_schedulerEngineHelper.getScheduledJob(
					clazz.getName(), clazz.getName(),
					StorageType.MEMORY_CLUSTERED);

			if (schedulerResponse != null) {
				nextRegenerateSitemapDate =
					_schedulerEngineHelper.getNextFireDate(schedulerResponse);
			}
		}

		for (SchedulerResponse schedulerResponse :
				_getRegenerateSitemapSchedulerResponses(companyId)) {

			Date nextFireDate = _schedulerEngineHelper.getNextFireDate(
				schedulerResponse);

			if ((nextFireDate != null) &&
				((nextRegenerateSitemapDate == null) ||
				 nextFireDate.before(nextRegenerateSitemapDate))) {

				nextRegenerateSitemapDate = nextFireDate;
			}
		}

		return nextRegenerateSitemapDate;
	}

	@Override
	public String getSitemap(
			long groupId, boolean privateLayout, ThemeDisplay themeDisplay)
		throws PortalException {

		return getSitemap(null, groupId, privateLayout, themeDisplay);
	}

	@Override
	public String getSitemap(
			long assetTypeClassNameId, String layoutUuid, long groupId,
			boolean privateLayout, ThemeDisplay themeDisplay)
		throws PortalException {

		return getSitemap(
			assetTypeClassNameId, layoutUuid, groupId, 1, privateLayout,
			themeDisplay);
	}

	@Override
	public String getSitemap(
			long assetTypeClassNameId, String layoutUuid, long groupId,
			int page, boolean privateLayout, ThemeDisplay themeDisplay)
		throws PortalException {

		if (assetTypeClassNameId > 0) {
			long companyId = themeDisplay.getCompanyId();

			SitemapURLProvider sitemapURLProvider =
				_serviceTrackerMap.getService(assetTypeClassNameId);

			if ((sitemapURLProvider == null) ||
				!StringUtil.equals(
					_sitemapConfigurationManager.getXMLSitemapIndexMode(
						companyId),
					SitemapConstants.INDEX_MODE_ASSET_TYPE) ||
				!_sitemapConfigurationManager.isXMLSitemapIndexCompanyEnabled(
					companyId) ||
				!sitemapURLProvider.isInclude(companyId, groupId)) {

				return null;
			}

			return _getAssetTypeSitemap(
				assetTypeClassNameId, groupId, page, themeDisplay);
		}

		if (Validator.isNull(layoutUuid) &&
			_sitemapConfigurationManager.isXMLSitemapIndexCompanyEnabled(
				themeDisplay.getCompanyId())) {

			return _getIndexSitemap(groupId, privateLayout, themeDisplay);
		}

		return _getSitemap(groupId, layoutUuid, privateLayout, themeDisplay);
	}

	@Override
	public String getSitemap(
			String layoutUuid, long groupId, boolean privateLayout,
			ThemeDisplay themeDisplay)
		throws PortalException {

		return getSitemap(0, layoutUuid, groupId, privateLayout, themeDisplay);
	}

	@Override
	public InputStream getSitemapInputStream(
			String assetTypeKey, String layoutUuid, long groupId, int page,
			boolean privateLayout, ThemeDisplay themeDisplay)
		throws PortalException {

		long companyId = themeDisplay.getCompanyId();

		if (StringUtil.equals(
				_sitemapConfigurationManager.getXMLSitemapIndexMode(companyId),
				SitemapConstants.INDEX_MODE_ASSET_TYPE) &&
			_sitemapConfigurationManager.isCachedGenerationCompanyEnabled(
				companyId) &&
			_sitemapConfigurationManager.isXMLSitemapIndexCompanyEnabled(
				companyId)) {

			try {
				if (assetTypeKey == null) {
					return _sitemapStorageHelper.getSitemapInputStream(
						companyId, groupId);
				}

				return _sitemapStorageHelper.getSitemapInputStream(
					companyId, groupId, assetTypeKey, page);
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}
			}
		}

		String xml = getSitemap(
			getAssetTypeClassNameId(assetTypeKey), layoutUuid, groupId, page,
			privateLayout, themeDisplay);

		if (xml == null) {
			return null;
		}

		return new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public boolean isRegenerateSitemapInProgress(long companyId) {
		List<com.liferay.portal.lock.model.Lock> serviceBuilderLocks =
			_lockLocalService.getLocks(
				companyId, SitemapManagerImpl.class.getName());

		for (com.liferay.portal.lock.model.Lock serviceBuilderLock :
				serviceBuilderLocks) {

			if (!serviceBuilderLock.isExpired()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void regenerateSitemap(
			String assetTypeKey, long companyId, long groupId)
		throws PortalException {

		if (!_sitemapConfigurationManager.isIndexModeAssetTypeCompanyEnabled(
				companyId)) {

			return;
		}

		long assetTypeClassNameId = getAssetTypeClassNameId(assetTypeKey);

		if (assetTypeClassNameId <= 0) {
			return;
		}

		SitemapURLProvider sitemapURLProvider = _serviceTrackerMap.getService(
			assetTypeClassNameId);

		if (sitemapURLProvider == null) {
			return;
		}

		if (groupId != 0) {
			Group group = _groupLocalService.fetchGroup(groupId);

			if (group == null) {
				return;
			}

			if (!group.isCompany()) {
				_regenerateGroupSitemap(
					assetTypeClassNameId, assetTypeKey, companyId, groupId,
					sitemapURLProvider);

				return;
			}
		}

		for (Group group :
				_groupLocalService.getGroups(
					companyId, GroupConstants.ANY_PARENT_GROUP_ID, true)) {

			_regenerateGroupSitemap(
				assetTypeClassNameId, assetTypeKey, companyId,
				group.getGroupId(), sitemapURLProvider);
		}
	}

	@Override
	public void scheduleRegenerateSitemap(
		String assetTypeKey, long companyId, long groupId, Date startDate) {

		try {
			if (!_sitemapConfigurationManager.isCachedGenerationCompanyEnabled(
					companyId) ||
				!_sitemapConfigurationManager.
					isIndexModeAssetTypeCompanyEnabled(companyId)) {

				return;
			}

			boolean force = true;

			if (startDate == null) {
				force = false;

				startDate = new Date();
			}

			Group group = _groupLocalService.fetchGroup(groupId);

			if ((group == null) || group.isCompany()) {
				_scheduleCompanyRegenerateSitemap(
					assetTypeKey, companyId, force, startDate);
			}
			else {
				_scheduleGroupRegenerateSitemap(
					assetTypeKey, companyId, force, groupId, startDate);
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_maximumEntries = MAXIMUM_ENTRIES;

		Map<String, Long> assetTypeClassNameIds = new HashMap<>();
		Map<Long, String> assetTypeKeys = new HashMap<>();

		for (Map.Entry<String, String> entry :
				_assetTypeKeysByClassName.entrySet()) {

			long classNameId = _classNameLocalService.getClassNameId(
				entry.getKey());

			assetTypeClassNameIds.put(entry.getValue(), classNameId);
			assetTypeKeys.put(classNameId, entry.getValue());
		}

		_assetTypeClassNameIds = Collections.unmodifiableMap(
			assetTypeClassNameIds);
		_assetTypeKeys = Collections.unmodifiableMap(assetTypeKeys);

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			_bundleContext, SitemapURLProvider.class, null,
			(serviceReference, emitter) -> {
				SitemapURLProvider sitemapURLProvider =
					_bundleContext.getService(serviceReference);

				emitter.emit(
					_classNameLocalService.getClassNameId(
						sitemapURLProvider.getClassName()));
			});
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private void _addEntryAndPaginate(Element element, Element newElement) {
		int entries =
			GetterUtil.getInteger(element.attributeValue("entries")) + 1;

		int newElementSize = _getSize(newElement);

		int size =
			GetterUtil.getInteger(element.attributeValue("size")) +
				newElementSize;

		if ((entries <= _maximumEntries) && (size < _MAXIMUM_SIZE)) {
			element.addAttribute("entries", String.valueOf(entries));
			element.addAttribute("size", String.valueOf(size));

			return;
		}

		element.remove(newElement);

		int page = GetterUtil.getInteger(
			element.attributeValue(_ATTRIBUTE_NAME_PAGE));

		_consumePage(element, page);

		element.clearContent();

		_initEntriesAndSize(element);

		String assetTypeKey = element.attributeValue(
			_ATTRIBUTE_NAME_ASSET_TYPE_KEY);
		long companyId = GetterUtil.getLong(
			element.attributeValue(_ATTRIBUTE_NAME_COMPANY_ID));
		long groupId = GetterUtil.getLong(
			element.attributeValue(_ATTRIBUTE_NAME_GROUP_ID));

		_initPagination(assetTypeKey, companyId, element, groupId);

		element.addAttribute(_ATTRIBUTE_NAME_PAGE, String.valueOf(page + 1));

		element.add(newElement);

		element.addAttribute("entries", "1");
		element.addAttribute(
			"size",
			String.valueOf(
				GetterUtil.getInteger(element.attributeValue("size")) +
					newElementSize));
	}

	private void _addSitemapElement(
		String assetTypeKey, Element element, long groupId, Date modifiedDate,
		int page, String url) {

		Element sitemapElement = element.addElement("sitemap");

		Element locElement = sitemapElement.addElement("loc");

		StringBundler sb = new StringBundler(8);

		sb.append(url);
		sb.append(_portal.getPathContext());
		sb.append("/sitemap-");
		sb.append(assetTypeKey);
		sb.append(".xml?groupId=");
		sb.append(groupId);

		if (page > 0) {
			sb.append("&page=");
			sb.append(page);
		}

		locElement.addText(sb.toString());

		if (modifiedDate != null) {
			DateFormat iso8601DateFormat = DateUtil.getISO8601Format();

			Element lastmodElement = sitemapElement.addElement("lastmod");

			lastmodElement.addText(iso8601DateFormat.format(modifiedDate));
		}
	}

	private void _consumePage(Element element, int page) {
		_removeEntriesAndSize(element);
		_removePaginationAttributes(element);

		Document document = element.getDocument();

		try {
			UnsafeBiConsumer<Integer, String, PortalException>
				unsafeBiConsumer = _unsafeBiConsumerThreadLocal.get();

			unsafeBiConsumer.accept(page, document.asXML());
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}
	}

	private HttpServletRequest _createMockHttpServletRequest() {
		Map<String, Object> attributes = new HashMap<>();

		return ProxyUtil.newDelegateProxyInstance(
			HttpServletRequest.class.getClassLoader(), HttpServletRequest.class,
			new Object() {

				public Object getAttribute(String name) {
					return attributes.get(name);
				}

				public Enumeration<String> getAttributeNames() {
					return Collections.enumeration(attributes.keySet());
				}

				public String getContextPath() {
					return _portal.getPathContext();
				}

				public Cookie[] getCookies() {
					return new Cookie[0];
				}

				public Enumeration<String> getHeaderNames() {
					return Collections.emptyEnumeration();
				}

				public String getMethod() {
					return HttpMethods.GET;
				}

				public Map<String, String[]> getParameterMap() {
					return Collections.emptyMap();
				}

				public String[] getParameterValues(String name) {
					return new String[0];
				}

				public RequestDispatcher getRequestDispatcher(String path) {
					return DirectRequestDispatcherFactoryUtil.
						getRequestDispatcher(
							ServletContextPool.get(
								_portal.getServletContextName()),
							path);
				}

				public String getRequestURI() {
					return StringPool.SLASH;
				}

				public String getScheme() {
					return Http.HTTP;
				}

				public ServletContext getServletContext() {
					return ServletContextPool.get(
						_portal.getServletContextName());
				}

				public void removeAttribute(String name) {
					attributes.remove(name);
				}

				public void setAttribute(String name, Object value) {
					attributes.put(name, value);
				}

			},
			ProxyFactory.newDummyInstance(HttpServletRequest.class));
	}

	private Document _createSitemapDocument(
		String rootElementName, String rootElementNamespace) {

		Document document = _saxReader.createDocument();

		document.setXMLEncoding(StringPool.UTF8);

		Element rootElement = document.addElement(
			rootElementName, rootElementNamespace);

		rootElement.addAttribute("xmlns:xhtml", "http://www.w3.org/1999/xhtml");
		rootElement.addAttribute(
			"xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		rootElement.addAttribute(
			"xsi:schemaLocation",
			"http://www.w3.org/1999/xhtml " +
				"http://www.w3.org/2002/08/xhtml/xhtml1-strict.xsd");

		return document;
	}

	private ThemeDisplay _createThemeDisplay(long companyId, long groupId)
		throws PortalException {

		ThemeDisplay themeDisplay = ThemeDisplayFactory.create();

		Company company = _companyLocalService.getCompany(companyId);

		themeDisplay.setCompany(company);

		Locale locale = _portal.getSiteDefaultLocale(groupId);

		themeDisplay.setLanguageId(LocaleUtil.toLanguageId(locale));

		themeDisplay.setLayoutSet(
			_layoutSetLocalService.getLayoutSet(groupId, false));
		themeDisplay.setLocale(locale);

		String virtualHostname = company.getVirtualHostname();

		if (Validator.isNull(virtualHostname)) {
			virtualHostname = "localhost";
		}

		themeDisplay.setPortalDomain(virtualHostname);

		boolean secure = _isSecure();

		int portalServerPort = _portal.getPortalServerPort(secure);

		themeDisplay.setPortalURL(
			_portal.getPortalURL(virtualHostname, portalServerPort, secure));

		themeDisplay.setRequest(_createMockHttpServletRequest());
		themeDisplay.setScopeGroupId(groupId);
		themeDisplay.setServerName(virtualHostname);
		themeDisplay.setServerPort(portalServerPort);
		themeDisplay.setSiteDefaultLocale(locale);
		themeDisplay.setSiteGroupId(groupId);
		themeDisplay.setUser(_userLocalService.getGuestUser(companyId));

		return themeDisplay;
	}

	private void _deleteAssetTypeSitemapsFromPage(
			String assetTypeKey, long companyId, long groupId, int page)
		throws PortalException {

		while (_sitemapStorageHelper.hasSitemapFile(
					companyId, groupId, assetTypeKey, page)) {

			_sitemapStorageHelper.deleteSitemap(
				companyId, groupId, assetTypeKey, page);

			page++;
		}
	}

	private void _generateAssetTypeSitemap(
			long assetTypeClassNameId, long groupId, ThemeDisplay themeDisplay,
			UnsafeBiConsumer<Integer, String, PortalException> unsafeBiConsumer)
		throws PortalException {

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		_unsafeBiConsumerThreadLocal.set(unsafeBiConsumer);

		try {
			long companyId = themeDisplay.getCompanyId();

			PermissionThreadLocal.setPermissionChecker(
				_permissionCheckerFactory.create(
					_userLocalService.getGuestUser(companyId)));

			Document document = _createSitemapDocument(
				"urlset", "http://www.sitemaps.org/schemas/sitemap/0.9");

			Element rootElement = document.getRootElement();

			_initEntriesAndSize(rootElement);

			String assetTypeKey = _assetTypeKeys.get(assetTypeClassNameId);

			_initPagination(assetTypeKey, companyId, rootElement, groupId);

			SitemapURLProvider sitemapURLProvider =
				_serviceTrackerMap.getService(assetTypeClassNameId);

			for (LayoutSet curLayoutSet :
					_getLayoutSets(groupId, null, false, themeDisplay)) {

				sitemapURLProvider.visitLayoutSet(
					rootElement, curLayoutSet, themeDisplay);
			}

			_consumePage(
				rootElement,
				GetterUtil.getInteger(
					rootElement.attributeValue(_ATTRIBUTE_NAME_PAGE)));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);

			_unsafeBiConsumerThreadLocal.remove();
		}
	}

	private Date _getAssetTypeModifiedDate(
			long classNameId, long companyId, long groupId)
		throws PortalException {

		SitemapURLProvider sitemapURLProvider = _serviceTrackerMap.getService(
			classNameId);

		if (sitemapURLProvider == null) {
			return null;
		}

		return sitemapURLProvider.getModifiedDate(companyId, groupId);
	}

	private int _getAssetTypePageCount(
			String assetTypeKey, long companyId, long groupId)
		throws PortalException {

		int pageCount = 1;

		while (_sitemapStorageHelper.hasSitemapFile(
					companyId, groupId, assetTypeKey, pageCount + 1)) {

			pageCount++;
		}

		return pageCount;
	}

	private String _getAssetTypeSitemap(
			long assetTypeClassNameId, long groupId, int page,
			ThemeDisplay themeDisplay)
		throws PortalException {

		long companyId = themeDisplay.getCompanyId();

		if (!_sitemapConfigurationManager.isCachedGenerationCompanyEnabled(
				companyId)) {

			String[] xml = {null};

			_generateAssetTypeSitemap(
				assetTypeClassNameId, groupId, themeDisplay,
				(curPage, curXML) -> {
					if (curPage == page) {
						xml[0] = curXML;
					}
				});

			return xml[0];
		}

		String assetTypeKey = _assetTypeKeys.get(assetTypeClassNameId);

		if (!_sitemapStorageHelper.hasSitemapFile(
				companyId, groupId, assetTypeKey, page)) {

			if (page != 1) {
				return null;
			}

			_regenerateAssetTypeSitemap(
				assetTypeClassNameId, groupId, themeDisplay);
		}

		try {
			return StringUtil.read(
				_sitemapStorageHelper.getSitemapInputStream(
					companyId, groupId, assetTypeKey, page));
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		return null;
	}

	private String _getFriendlyURL(long groupId, String path) {
		int[] groupFriendlyURLIndex = _portal.getGroupFriendlyURLIndex(path);

		if (groupFriendlyURLIndex != null) {
			if (groupFriendlyURLIndex[1] < path.length()) {
				return path.substring(groupFriendlyURLIndex[1]);
			}

			return StringPool.BLANK;
		}

		long companyId = CompanyThreadLocal.getCompanyId();

		if (companyId == 0) {
			try {
				Group group = _groupLocalService.getGroup(groupId);

				companyId = group.getCompanyId();
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}

				companyId = 0;
			}
		}

		for (Locale availableLocale :
				_language.getAvailableLocales(companyId)) {

			String i18nPath =
				StringPool.SLASH + LocaleUtil.toLanguageId(availableLocale);

			if (path.startsWith(i18nPath + StringPool.SLASH) ||
				path.equals(i18nPath)) {

				path = path.substring(i18nPath.length());

				groupFriendlyURLIndex = _portal.getGroupFriendlyURLIndex(path);

				if (groupFriendlyURLIndex != null) {
					if (groupFriendlyURLIndex[1] < path.length()) {
						return path.substring(groupFriendlyURLIndex[1]);
					}

					return StringPool.BLANK;
				}

				return path;
			}
		}

		return path;
	}

	private String _getIndexSitemap(
			long groupId, boolean privateLayout, ThemeDisplay themeDisplay)
		throws PortalException {

		Document document = _createSitemapDocument(
			"sitemapindex", "http://www.sitemaps.org/schemas/sitemap/0.9");

		Element rootElement = document.getRootElement();

		_initEntriesAndSize(rootElement);

		long companyId = themeDisplay.getCompanyId();

		String xmlSitemapIndexMode =
			_sitemapConfigurationManager.getXMLSitemapIndexMode(companyId);

		boolean cachedGenerationCompanyEnabled =
			_sitemapConfigurationManager.isCachedGenerationCompanyEnabled(
				companyId);

		if (StringUtil.equals(
				xmlSitemapIndexMode, SitemapConstants.INDEX_MODE_ASSET_TYPE)) {

			String portalURL = themeDisplay.getPortalURL();

			for (Map.Entry<Long, String> entry : _assetTypeKeys.entrySet()) {
				long assetTypeClassNameId = entry.getKey();

				SitemapURLProvider sitemapURLProvider =
					_serviceTrackerMap.getService(assetTypeClassNameId);

				if ((sitemapURLProvider == null) ||
					!sitemapURLProvider.isInclude(companyId, groupId)) {

					continue;
				}

				int assetTypePageCount = 0;

				String assetTypeKey = entry.getValue();

				if (cachedGenerationCompanyEnabled) {
					if (!_sitemapStorageHelper.hasSitemapFile(
							companyId, groupId, assetTypeKey, 1)) {

						_regenerateAssetTypeSitemap(
							assetTypeClassNameId, groupId, themeDisplay);
					}

					assetTypePageCount = _getAssetTypePageCount(
						assetTypeKey, companyId, groupId);
				}
				else {
					int[] assetTypePageCountArray = {0};

					_generateAssetTypeSitemap(
						assetTypeClassNameId, groupId, themeDisplay,
						(page, xml) -> assetTypePageCountArray[0]++);

					assetTypePageCount = assetTypePageCountArray[0];
				}

				Date assetTypeModifiedDate = _getAssetTypeModifiedDate(
					assetTypeClassNameId, companyId, groupId);

				if (assetTypePageCount <= 1) {
					_addSitemapElement(
						assetTypeKey, rootElement, groupId,
						assetTypeModifiedDate, 0, portalURL);
				}
				else {
					for (int page = 1; page <= assetTypePageCount; page++) {
						_addSitemapElement(
							assetTypeKey, rootElement, groupId,
							assetTypeModifiedDate, page, portalURL);
					}
				}
			}
		}
		else {
			PermissionChecker originalPermissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			try {
				PermissionThreadLocal.setPermissionChecker(
					_permissionCheckerFactory.create(
						_userLocalService.getGuestUser(companyId)));

				for (LayoutSet layoutSet :
						_getLayoutSets(
							groupId, null, privateLayout, themeDisplay)) {

					_visitLayoutSet(rootElement, layoutSet, themeDisplay);
				}
			}
			finally {
				PermissionThreadLocal.setPermissionChecker(
					originalPermissionChecker);
			}
		}

		_removeEntriesAndSize(rootElement);

		String xml = document.asXML();

		if (StringUtil.equals(
				xmlSitemapIndexMode, SitemapConstants.INDEX_MODE_ASSET_TYPE) &&
			cachedGenerationCompanyEnabled) {

			try {
				_sitemapStorageHelper.storeSitemapFile(companyId, groupId, xml);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(exception);
				}
			}
		}

		return xml;
	}

	private List<LayoutSet> _getLayoutSets(
			long groupId, String layoutUuid, boolean privateLayout,
			ThemeDisplay themeDisplay)
		throws PortalException {

		LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
			groupId, privateLayout);

		if (Validator.isNotNull(layoutUuid) ||
			!_isCompanyVirtualHostname(themeDisplay)) {

			return ListUtil.fromArray(layoutSet);
		}

		Group group = _groupLocalService.getGroup(groupId);

		if (!group.isGuest()) {
			return ListUtil.fromArray(layoutSet);
		}

		try {
			List<LayoutSet> layoutSets = new ArrayList<>();

			if (MapUtil.isEmpty(layoutSet.getVirtualHostnames())) {
				layoutSets.add(layoutSet);
			}

			layoutSets.addAll(
				TransformUtil.transformToList(
					_sitemapConfigurationManager.getCompanySitemapGroupIds(
						themeDisplay.getCompanyId()),
					curGroupId -> {
						Group curGroup = _groupLocalService.fetchGroup(
							curGroupId);

						if ((curGroup == null) || curGroup.isGuest()) {
							return null;
						}

						LayoutSet curLayoutSet =
							_layoutSetLocalService.getLayoutSet(
								curGroup.getGroupId(), privateLayout);

						if (MapUtil.isNotEmpty(
								curLayoutSet.getVirtualHostnames())) {

							return null;
						}

						return curLayoutSet;
					}));

			return layoutSets;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return Collections.emptyList();
	}

	private List<SchedulerResponse> _getRegenerateSitemapSchedulerResponses(
			long companyId)
		throws SchedulerException {

		return TransformUtil.transform(
			_schedulerEngineHelper.getScheduledJobs(
				SitemapDestinationNames.SITEMAP_REGENERATION,
				StorageType.PERSISTED),
			schedulerResponse -> {
				Message message = schedulerResponse.getMessage();

				if ((message != null) &&
					(message.getLong("companyId") == companyId)) {

					return schedulerResponse;
				}

				return null;
			});
	}

	private String _getSchedulerJobName(
		String assetTypeKey, long companyId, long groupId) {

		return StringBundler.concat(
			assetTypeKey, StringPool.SLASH, groupId, StringPool.AT, companyId);
	}

	private String _getSitemap(
			long groupId, String layoutUuid, boolean privateLayout,
			ThemeDisplay themeDisplay)
		throws PortalException {

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				_permissionCheckerFactory.create(
					_userLocalService.getGuestUser(
						themeDisplay.getCompanyId())));

			Document document = _createSitemapDocument(
				"urlset", "http://www.sitemaps.org/schemas/sitemap/0.9");

			Element rootElement = document.getRootElement();

			_initEntriesAndSize(rootElement);

			_visitLayoutSets(
				rootElement,
				_getLayoutSets(
					groupId, layoutUuid, privateLayout, themeDisplay),
				layoutUuid, themeDisplay);

			_removeEntriesAndSize(rootElement);

			return document.asXML();
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}
	}

	private List<SitemapURLProvider> _getSitemapURLProviders() {
		return TransformUtil.transform(
			_serviceTrackerMap.keySet(),
			classNameId -> _serviceTrackerMap.getService(classNameId));
	}

	private int _getSize(Element element) {
		String string = element.asXML();

		byte[] bytes = string.getBytes();

		int offset = 0;

		String name = element.getName();

		if (name.equals("url")) {
			Set<Locale> availableLocales = _language.getAvailableLocales();

			int availableLocalesSize = availableLocales.size();

			offset = (availableLocalesSize + 1) * _ATTRIBUTE_XHTML.length;

			offset += _ATTRIBUTE_XMLNS.length;
		}

		return bytes.length - offset;
	}

	private void _initEntriesAndSize(Element element) {
		element.addAttribute("entries", "0");

		int size = _getSize(element);

		element.addAttribute("size", String.valueOf(size));
	}

	private void _initPagination(
		String assetTypeKey, long companyId, Element element, long groupId) {

		element.addAttribute(_ATTRIBUTE_NAME_ASSET_TYPE_KEY, assetTypeKey);
		element.addAttribute(
			_ATTRIBUTE_NAME_COMPANY_ID, String.valueOf(companyId));
		element.addAttribute(_ATTRIBUTE_NAME_GROUP_ID, String.valueOf(groupId));
		element.addAttribute(_ATTRIBUTE_NAME_PAGE, "1");
	}

	private boolean _isCompanyVirtualHostname(ThemeDisplay themeDisplay) {
		Company company = themeDisplay.getCompany();

		String virtualHostname = company.getVirtualHostname();

		if (Validator.isNull(virtualHostname)) {
			virtualHostname = "localhost";
		}

		return Objects.equals(virtualHostname, themeDisplay.getServerName());
	}

	private boolean _isSecure() {
		if (Objects.equals(
				Http.HTTPS,
				PropsUtil.get(PropsKeys.PORTAL_INSTANCE_PROTOCOL)) ||
			Objects.equals(
				Http.HTTPS, PropsUtil.get(PropsKeys.WEB_SERVER_PROTOCOL))) {

			return true;
		}

		return false;
	}

	private Lock _lockRegenerateSitemap(
			String assetTypeKey, long companyId, long groupId)
		throws PortalException {

		try {
			User guestUser = _userLocalService.getGuestUser(companyId);

			return _lockManager.lock(
				guestUser.getUserId(), SitemapManagerImpl.class.getName(),
				_getSchedulerJobName(assetTypeKey, companyId, groupId),
				SitemapManagerImpl.class.getName(), false, Time.MINUTE * 20,
				false);
		}
		catch (DuplicateLockException duplicateLockException) {
			if (_log.isDebugEnabled()) {
				_log.debug(duplicateLockException);
			}

			return null;
		}
	}

	private void _regenerateAssetTypeSitemap(
			long assetTypeClassNameId, long groupId, ThemeDisplay themeDisplay)
		throws PortalException {

		long companyId = themeDisplay.getCompanyId();

		String assetTypeKey = _assetTypeKeys.get(assetTypeClassNameId);

		int[] pages = {0};

		_generateAssetTypeSitemap(
			assetTypeClassNameId, groupId, themeDisplay,
			(page, xml) -> {
				pages[0] = page;

				_sitemapStorageHelper.storeSitemapFile(
					companyId, groupId, assetTypeKey, page, xml);
			});

		try {
			_deleteAssetTypeSitemapsFromPage(
				assetTypeKey, companyId, groupId, pages[0] + 1);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}
	}

	private void _regenerateGroupSitemap(
			long assetTypeClassNameId, String assetTypeKey, long companyId,
			long groupId, SitemapURLProvider sitemapURLProvider)
		throws PortalException {

		if (!sitemapURLProvider.isInclude(companyId, groupId)) {
			return;
		}

		Lock lock = _lockRegenerateSitemap(assetTypeKey, companyId, groupId);

		if ((lock == null) || !lock.isNew()) {
			return;
		}

		try {
			ThemeDisplay themeDisplay = _createThemeDisplay(companyId, groupId);

			_regenerateAssetTypeSitemap(
				assetTypeClassNameId, groupId, themeDisplay);

			_getIndexSitemap(groupId, false, themeDisplay);

			if (_sitemapConfigurationManager.isCachedGenerationCompanyEnabled(
					companyId)) {

				_sitemapStorageHelper.storeLastRegenerateSitemapDateFile(
					companyId);
			}
		}
		finally {
			_unlockRegenerateSitemap(assetTypeKey, companyId, groupId);
		}
	}

	private void _removeAttribute(Element element, String name) {
		Attribute attribute = element.attribute(name);

		if (attribute != null) {
			element.remove(attribute);
		}
	}

	private void _removeEntriesAndSize(Element element) {
		Attribute entriesAttribute = element.attribute("entries");
		Attribute sizeAttribute = element.attribute("size");

		if (_log.isDebugEnabled()) {
			StringBundler sb = new StringBundler(4);

			sb.append("Created site map with ");

			if (entriesAttribute != null) {
				sb.append(entriesAttribute.getValue());
			}
			else {
				sb.append("no");
			}

			sb.append(" entries and size ");

			if (sizeAttribute != null) {
				int size = GetterUtil.getInteger(sizeAttribute.getValue());

				sb.append(
					TextFormatter.formatStorageSize(
						size, LocaleUtil.fromLanguageId("en_US")));
			}
			else {
				sb.append("0");
			}

			_log.debug(sb.toString());
		}

		_removeAttribute(element, "entries");
		_removeAttribute(element, "size");
	}

	private void _removeOldestElement(Element element, Element newElement) {
		int entries = GetterUtil.getInteger(element.attributeValue("entries"));
		int size = GetterUtil.getInteger(element.attributeValue("size"));

		entries++;
		size += _getSize(newElement);

		while ((entries > _maximumEntries) || (size >= _MAXIMUM_SIZE)) {
			Element oldestUrlElement = element.element(newElement.getName());

			entries--;
			size -= _getSize(oldestUrlElement);

			element.remove(oldestUrlElement);
		}

		element.addAttribute("entries", String.valueOf(entries));
		element.addAttribute("size", String.valueOf(size));
	}

	private void _removePaginationAttributes(Element element) {
		_removeAttribute(element, _ATTRIBUTE_NAME_ASSET_TYPE_KEY);
		_removeAttribute(element, _ATTRIBUTE_NAME_COMPANY_ID);
		_removeAttribute(element, _ATTRIBUTE_NAME_GROUP_ID);
		_removeAttribute(element, _ATTRIBUTE_NAME_PAGE);
	}

	private void _scheduleCompanyRegenerateSitemap(
			String assetTypeKey, long companyId, boolean force, Date startDate)
		throws PortalException {

		for (Group group :
				_groupLocalService.getGroups(
					companyId, GroupConstants.ANY_PARENT_GROUP_ID, true)) {

			_scheduleGroupRegenerateSitemap(
				assetTypeKey, companyId, force, group.getGroupId(), startDate);
		}
	}

	private void _scheduleGroupRegenerateSitemap(
			String assetTypeKey, long companyId, boolean force, long groupId,
			Date startDate)
		throws PortalException {

		SitemapURLProvider sitemapURLProvider = _serviceTrackerMap.getService(
			getAssetTypeClassNameId(assetTypeKey));

		if ((sitemapURLProvider == null) ||
			!sitemapURLProvider.isInclude(companyId, groupId)) {

			return;
		}

		String schedulerJobName = _getSchedulerJobName(
			assetTypeKey, companyId, groupId);

		SchedulerResponse schedulerResponse =
			_schedulerEngineHelper.getScheduledJob(
				schedulerJobName, SitemapDestinationNames.SITEMAP_REGENERATION,
				StorageType.PERSISTED);

		if (schedulerResponse != null) {
			if (!force) {
				return;
			}

			_schedulerEngineHelper.delete(
				schedulerJobName, SitemapDestinationNames.SITEMAP_REGENERATION,
				StorageType.PERSISTED);
		}

		Message message = new Message();

		message.put("assetTypeKey", assetTypeKey);
		message.put("companyId", companyId);
		message.put("groupId", groupId);

		_schedulerEngineHelper.schedule(
			_triggerFactory.createTrigger(
				schedulerJobName, SitemapDestinationNames.SITEMAP_REGENERATION,
				startDate, null, 0, null),
			StorageType.PERSISTED,
			StringBundler.concat(
				"Sitemap regeneration for group ", groupId, " and asset type ",
				assetTypeKey),
			SitemapDestinationNames.SITEMAP_REGENERATION, message);
	}

	private void _unlockRegenerateSitemap(
		String assetTypeKey, long companyId, long groupId) {

		_lockManager.unlock(
			SitemapManagerImpl.class.getName(),
			_getSchedulerJobName(assetTypeKey, companyId, groupId));
	}

	private void _visitLayoutSet(
			Element element, LayoutSet layoutSet, ThemeDisplay themeDisplay)
		throws PortalException {

		if (layoutSet.isPrivateLayout()) {
			return;
		}

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		String portalURL = themeDisplay.getPortalURL();

		Map<String, LayoutTypeController> layoutTypeControllers =
			LayoutTypeControllerTracker.getLayoutTypeControllers();

		for (Map.Entry<String, LayoutTypeController> entry :
				layoutTypeControllers.entrySet()) {

			LayoutTypeController layoutTypeController = entry.getValue();

			if (!layoutTypeController.isSitemapable()) {
				continue;
			}

			List<Layout> layouts = _layoutLocalService.getAllLayouts(
				layoutSet.getGroupId(), layoutSet.isPrivateLayout(),
				entry.getKey());

			for (Layout layout : layouts) {
				if (layout.isSystem() && !layout.isTypeAssetDisplay()) {
					continue;
				}

				UnicodeProperties typeSettingsUnicodeProperties =
					layout.getTypeSettingsProperties();

				boolean sitemapInclude = GetterUtil.getBoolean(
					typeSettingsUnicodeProperties.getProperty(
						LayoutTypePortletConstants.SITEMAP_INCLUDE),
					true);

				if (!sitemapInclude || (permissionChecker == null) ||
					!_layoutModelResourcePermission.contains(
						permissionChecker, layout, ActionKeys.VIEW)) {

					continue;
				}

				Element sitemapElement = element.addElement("sitemap");

				Element locElement = sitemapElement.addElement("loc");

				locElement.addText(
					StringBundler.concat(
						portalURL, _portal.getPathContext(),
						"/sitemap.xml?p_l_id=", layout.getPlid(),
						"&layoutUuid=", layout.getUuid(), "&groupId=",
						layoutSet.getGroupId()));

				_removeOldestElement(element, sitemapElement);
			}
		}
	}

	private void _visitLayoutSets(
			Element element, List<LayoutSet> layoutSets, String layoutUuid,
			ThemeDisplay themeDisplay)
		throws PortalException {

		if (ListUtil.isEmpty(layoutSets)) {
			return;
		}

		for (SitemapURLProvider sitemapURLProvider :
				_getSitemapURLProviders()) {

			if (!sitemapURLProvider.isInclude(
					themeDisplay.getCompanyId(),
					themeDisplay.getScopeGroupId())) {

				continue;
			}

			if (Validator.isNull(layoutUuid)) {
				for (LayoutSet curLayoutSet : layoutSets) {
					sitemapURLProvider.visitLayoutSet(
						element, curLayoutSet, themeDisplay);
				}
			}
			else {
				sitemapURLProvider.visitLayout(
					element, layoutUuid, layoutSets.get(0), themeDisplay);
			}
		}
	}

	private static final String _ATTRIBUTE_NAME_ASSET_TYPE_KEY =
		"_assetTypeKey";

	private static final String _ATTRIBUTE_NAME_COMPANY_ID = "_companyId";

	private static final String _ATTRIBUTE_NAME_GROUP_ID = "_groupId";

	private static final String _ATTRIBUTE_NAME_PAGE = "_page";

	private static final byte[] _ATTRIBUTE_XHTML =
		" xmlns:xhtml=\"http://www.w3.org/1999/xhtml\"".getBytes();

	private static final byte[] _ATTRIBUTE_XMLNS =
		" xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"".getBytes();

	private static final int _MAXIMUM_SIZE = 50 * 1024 * 1024;

	private static final Log _log = LogFactoryUtil.getLog(
		SitemapManagerImpl.class.getName());

	private static final Map<String, String> _assetTypeKeysByClassName = Map.of(
		AssetCategory.class.getName(),
		SitemapConstants.ASSET_TYPE_KEY_CATEGORIES,
		JournalArticle.class.getName(),
		SitemapConstants.ASSET_TYPE_KEY_WEB_CONTENT, Layout.class.getName(),
		SitemapConstants.ASSET_TYPE_KEY_PAGES, ObjectEntry.class.getName(),
		SitemapConstants.ASSET_TYPE_KEY_OBJECT_ENTRIES);
	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();

	private Map<String, Long> _assetTypeClassNameIds;
	private Map<Long, String> _assetTypeKeys;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.model.Layout)"
	)
	private ModelResourcePermission<Layout> _layoutModelResourcePermission;

	@Reference
	private LayoutSetLocalService _layoutSetLocalService;

	@Reference
	private LockLocalService _lockLocalService;

	@Reference
	private LockManager _lockManager;

	private int _maximumEntries;

	@Reference
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Reference
	private Portal _portal;

	@Reference
	private RedirectProvider _redirectProvider;

	@Reference
	private SAXReader _saxReader;

	@Reference
	private SchedulerEngineHelper _schedulerEngineHelper;

	private ServiceTrackerMap<Long, SitemapURLProvider> _serviceTrackerMap;

	@Reference
	private SitemapConfigurationManager _sitemapConfigurationManager;

	@Reference
	private SitemapStorageHelper _sitemapStorageHelper;

	@Reference
	private SiteSitemapRegenerationEntryLocalService
		_siteSitemapRegenerationEntryLocalService;

	@Reference
	private TriggerFactory _triggerFactory;

	private final ThreadLocal
		<UnsafeBiConsumer<Integer, String, PortalException>>
			_unsafeBiConsumerThreadLocal = new ThreadLocal<>();

	@Reference
	private UserLocalService _userLocalService;

}