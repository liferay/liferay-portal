/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.staticexport.internal;

import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.layout.renderer.LayoutPreviewRenderer;
import com.liferay.layout.util.LayoutServiceContextHelper;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutFriendlyURLComposite;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.portlet.FriendlyURLResolver;
import com.liferay.portal.kernel.portlet.FriendlyURLResolverRegistryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.servlet.DummyHttpServletResponse;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.site.staticexport.DisplayPageStaticSiteExportEntry;
import com.liferay.site.staticexport.LayoutStaticSiteExportEntry;
import com.liferay.site.staticexport.StaticSiteExport;
import com.liferay.site.staticexport.StaticSiteExportEntry;
import com.liferay.site.staticexport.StaticSiteExportReport;
import com.liferay.site.staticexport.StaticSiteExportResource;
import com.liferay.site.staticexport.StaticSiteExporter;

import jakarta.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.IOException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(service = StaticSiteExporter.class)
public class StaticSiteExporterImpl implements StaticSiteExporter {

	@Override
	public StaticSiteExport export(long groupId, Set<Locale> locales)
		throws PortalException {

		return _export(
			groupId, _layoutService.getLayouts(groupId, false), locales);
	}

	@Override
	public StaticSiteExport export(
			long groupId, Set<Long> layoutIds, Set<Locale> locales)
		throws PortalException {

		List<Layout> layouts = new ArrayList<>();

		for (long layoutId : layoutIds) {
			layouts.add(_layoutService.getLayout(groupId, false, layoutId));
		}

		return _export(groupId, layouts, locales);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	private void _addPortalHostName(
		String hostName, Set<String> portalHostNames) {

		if (Validator.isNotNull(hostName)) {
			portalHostNames.add(
				StringUtil.toLowerCase(StringUtil.trim(hostName)));
		}
	}

	private StaticSiteExport _export(
			long groupId, List<Layout> layouts, Set<Locale> locales)
		throws PortalException {

		Group group = _groupLocalService.getGroup(groupId);

		Company company = _companyLocalService.getCompany(group.getCompanyId());

		try (AutoCloseable autoCloseable =
				_layoutServiceContextHelper.getServiceContextAutoCloseable(
					company)) {

			List<StaticSiteExportReport.Failure> layoutFailures =
				new ArrayList<>();

			String portalURL = _portal.getPortalURL(
				company.getVirtualHostname(),
				_portal.getPortalServerPort(false), false);

			Set<String> portalHostNames = _getPortalHostNames(
				company, group, portalURL);

			List<StaticSiteExportEntryRendering>
				staticSiteExportEntryRenderings =
					_getStaticSiteExportEntryRenderings(
						group, layoutFailures, layouts, locales,
						portalHostNames);

			List<StaticSiteExportReport.Failure> resourceFailures =
				new ArrayList<>();

			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Map<String, String> resourcePaths = new HashMap<>();

			List<StaticSiteExportResource> staticSiteExportResources =
				_fetchStaticSiteExportResources(
					serviceContext.getRequest(), locales, portalHostNames,
					portalURL, resourceFailures, resourcePaths,
					TransformUtil.transform(
						staticSiteExportEntryRenderings,
						StaticSiteExportEntryRendering::
							getStaticSiteExportDocument));

			StaticSiteExportURLRewriter staticSiteExportURLRewriter =
				new StaticSiteExportURLRewriter(
					_getPagePathsMap(group, staticSiteExportEntryRenderings),
					portalHostNames, resourcePaths);

			return new StaticSiteExportImpl(
				_rewriteStaticSiteExportEntryRenderings(
					_portal.getSiteDefaultLocale(groupId),
					staticSiteExportEntryRenderings,
					staticSiteExportURLRewriter),
				new StaticSiteExportReport(layoutFailures, resourceFailures),
				staticSiteExportResources);
		}
		catch (PortalException portalException) {
			throw portalException;
		}
		catch (Exception exception) {
			throw new PortalException(exception);
		}
	}

	private void _exportDisplayPageStaticSiteExportEntryRenderings(
		Group group, List<StaticSiteExportReport.Failure> layoutFailures,
		Locale locale, Set<String> portalHostNames,
		List<StaticSiteExportEntryRendering> staticSiteExportEntryRenderings) {

		Deque<String> friendlyURLs = new ArrayDeque<>();

		for (StaticSiteExportEntryRendering staticSiteExportEntryRendering :
				staticSiteExportEntryRenderings) {

			if (Objects.equals(
					staticSiteExportEntryRendering.getLocale(), locale)) {

				friendlyURLs.addAll(
					_getDisplayPageFriendlyURLs(
						group, portalHostNames,
						staticSiteExportEntryRendering.
							getStaticSiteExportDocument()));
			}
		}

		Map<String, StaticSiteExportEntryRendering>
			displayPageStaticSiteExportEntryRenderings = new HashMap<>();
		Set<String> exportedFriendlyURLs = new HashSet<>();

		while (!friendlyURLs.isEmpty()) {
			String friendlyURL = friendlyURLs.poll();

			if (!exportedFriendlyURLs.add(friendlyURL)) {
				continue;
			}

			try {
				StaticSiteExportEntryRendering
					displayPageStaticSiteExportEntryRendering =
						_getDisplayPageStaticSiteExportEntryRendering(
							friendlyURL, group, locale);

				if (displayPageStaticSiteExportEntryRendering == null) {
					continue;
				}

				StaticSiteExportEntryRendering
					exportedStaticSiteExportEntryRendering =
						displayPageStaticSiteExportEntryRenderings.get(
							displayPageStaticSiteExportEntryRendering.getKey());

				if (exportedStaticSiteExportEntryRendering != null) {
					Set<String> exportedDisplayPageFriendlyURLs =
						exportedStaticSiteExportEntryRendering.
							getFriendlyURLs();

					exportedDisplayPageFriendlyURLs.addAll(
						displayPageStaticSiteExportEntryRendering.
							getFriendlyURLs());

					continue;
				}

				displayPageStaticSiteExportEntryRenderings.put(
					displayPageStaticSiteExportEntryRendering.getKey(),
					displayPageStaticSiteExportEntryRendering);

				staticSiteExportEntryRenderings.add(
					displayPageStaticSiteExportEntryRendering);

				friendlyURLs.addAll(
					_getDisplayPageFriendlyURLs(
						group, portalHostNames,
						displayPageStaticSiteExportEntryRendering.
							getStaticSiteExportDocument()));
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn("Unable to render " + friendlyURL, exception);
				}

				layoutFailures.add(
					new StaticSiteExportReport.Failure(
						exception.getMessage(), friendlyURL));
			}
		}
	}

	private StaticSiteExportResourceFile _fetchStaticSiteExportResourceFile(
		boolean optional, List<StaticSiteExportReport.Failure> resourceFailures,
		StaticSiteExportResourceFetcher staticSiteExportResourceFetcher,
		String url) {

		try {
			StaticSiteExportResourceFile staticSiteExportResourceFile =
				staticSiteExportResourceFetcher.fetch(url);

			if (staticSiteExportResourceFile != null) {
				return staticSiteExportResourceFile;
			}

			if (!optional) {
				resourceFailures.add(
					new StaticSiteExportReport.Failure(
						"No servlet serves the resource", url));
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to fetch " + url, exception);
			}

			if (!optional) {
				resourceFailures.add(
					new StaticSiteExportReport.Failure(
						exception.getMessage(), url));
			}
		}

		return null;
	}

	private List<StaticSiteExportResource> _fetchStaticSiteExportResources(
		HttpServletRequest httpServletRequest, Set<Locale> locales,
		Set<String> portalHostNames, String portalURL,
		List<StaticSiteExportReport.Failure> resourceFailures,
		Map<String, String> resourcePaths,
		Collection<StaticSiteExportDocument> staticSiteExportDocuments) {

		List<StaticSiteExportResource> staticSiteExportResources =
			new ArrayList<>();

		Set<String> fetchedURLs = new HashSet<>();
		Set<String> moduleNames = new HashSet<>();

		StaticSiteExportBundleResourceResolver
			staticSiteExportBundleResourceResolver =
				new StaticSiteExportBundleResourceResolver(_bundleContext);

		StaticSiteExportResourceFetcher staticSiteExportResourceFetcher =
			new StaticSiteExportResourceFetcher(
				httpServletRequest, new DummyHttpServletResponse(), portalURL,
				ServletContextPool.get(_portal.getServletContextName()),
				staticSiteExportBundleResourceResolver);

		for (StaticSiteExportDocument staticSiteExportDocument :
				staticSiteExportDocuments) {

			Set<String> resourceURLs = _getResourceURLs(
				portalHostNames, staticSiteExportDocument);

			_fetchStaticSiteExportResources(
				fetchedURLs, locales, moduleNames, false, portalHostNames,
				resourceFailures, resourcePaths,
				staticSiteExportBundleResourceResolver,
				staticSiteExportResourceFetcher, staticSiteExportResources,
				resourceURLs);

			_fetchStaticSiteExportResources(
				fetchedURLs, locales, moduleNames, true, portalHostNames,
				resourceFailures, resourcePaths,
				staticSiteExportBundleResourceResolver,
				staticSiteExportResourceFetcher, staticSiteExportResources,
				_getModuleURLs(
					staticSiteExportDocument.getHTML(), locales, moduleNames,
					staticSiteExportBundleResourceResolver));
			_fetchStaticSiteExportResources(
				fetchedURLs, locales, moduleNames, true, portalHostNames,
				resourceFailures, resourcePaths,
				staticSiteExportBundleResourceResolver,
				staticSiteExportResourceFetcher, staticSiteExportResources,
				_getAUIModuleURLs(
					moduleNames, resourceURLs,
					staticSiteExportBundleResourceResolver));
		}

		return staticSiteExportResources;
	}

	private void _fetchStaticSiteExportResources(
		Set<String> fetchedURLs, Set<Locale> locales, Set<String> moduleNames,
		boolean optional, Set<String> portalHostNames,
		List<StaticSiteExportReport.Failure> resourceFailures,
		Map<String, String> resourcePaths,
		StaticSiteExportBundleResourceResolver
			staticSiteExportBundleResourceResolver,
		StaticSiteExportResourceFetcher staticSiteExportResourceFetcher,
		List<StaticSiteExportResource> staticSiteExportResources,
		Set<String> urls) {

		for (String url : urls) {
			if (!fetchedURLs.add(url)) {
				continue;
			}

			StaticSiteExportResourceFile staticSiteExportResourceFile =
				_fetchStaticSiteExportResourceFile(
					optional, resourceFailures, staticSiteExportResourceFetcher,
					url);

			if (staticSiteExportResourceFile == null) {
				continue;
			}

			_fetchStaticSiteExportResources(
				fetchedURLs, locales, moduleNames, true, portalHostNames,
				resourceFailures, resourcePaths,
				staticSiteExportBundleResourceResolver,
				staticSiteExportResourceFetcher, staticSiteExportResources,
				_getNestedResourceURLs(
					locales, moduleNames, portalHostNames,
					staticSiteExportBundleResourceResolver,
					staticSiteExportResourceFile, url));

			try {
				if (Objects.equals(
						staticSiteExportResourceFile.getExtension(), "css")) {

					_rewriteStylesheet(
						staticSiteExportResourceFile.getFile(), portalHostNames,
						resourcePaths, url);
				}

				_putStaticSiteExportResource(
					resourcePaths, staticSiteExportResourceFile,
					staticSiteExportResources, url);
			}
			catch (IOException ioException) {
				if (_log.isDebugEnabled()) {
					_log.debug("Unable to read " + url, ioException);
				}

				FileUtil.delete(staticSiteExportResourceFile.getFile());

				if (!optional) {
					resourceFailures.add(
						new StaticSiteExportReport.Failure(
							ioException.getMessage(), url));
				}
			}
		}
	}

	private Set<String> _getAUIModuleURLs(
		Set<String> moduleNames, Set<String> resourceURLs,
		StaticSiteExportBundleResourceResolver
			staticSiteExportBundleResourceResolver) {

		if (!_hasAUIModuleURL(resourceURLs) ||
			!moduleNames.add(_AUI_MODULE_NAME)) {

			return Collections.emptySet();
		}

		Set<String> auiModuleURLs = new LinkedHashSet<>();

		try {
			for (String resourcePath :
					staticSiteExportBundleResourceResolver.getResourcePaths(
						_AUI_MODULE_NAME, "/aui/")) {

				if (resourcePath.endsWith(".js") &&
					!resourcePath.endsWith("-min.js") &&
					!resourcePath.contains("/lang/")) {

					continue;
				}

				auiModuleURLs.add(
					StringBundler.concat(
						"/o/", _AUI_MODULE_NAME, resourcePath));
			}

			for (String resourcePath :
					staticSiteExportBundleResourceResolver.getResourcePaths(
						_AUI_MODULE_NAME, "/liferay/")) {

				auiModuleURLs.add(
					StringBundler.concat(
						"/o/", _AUI_MODULE_NAME, resourcePath));
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to list the resources of " + _AUI_MODULE_NAME,
					exception);
			}
		}

		return auiModuleURLs;
	}

	private Map<String, String> _getAlternatePaths(
		Map<Locale, String> paths, Locale siteDefaultLocale) {

		Map<String, String> alternatePaths = new TreeMap<>();

		for (Map.Entry<Locale, String> entry : paths.entrySet()) {
			alternatePaths.put(
				LocaleUtil.toW3cLanguageId(entry.getKey()),
				StringPool.SLASH + entry.getValue());
		}

		String path = paths.get(siteDefaultLocale);

		if (path != null) {
			alternatePaths.put("x-default", StringPool.SLASH + path);
		}

		return alternatePaths;
	}

	private Set<String> _getDisplayPageFriendlyURLs(
		Group group, Set<String> portalHostNames,
		StaticSiteExportDocument staticSiteExportDocument) {

		Set<String> displayPageFriendlyURLs = new LinkedHashSet<>();

		String siteURL =
			_portal.getPathFriendlyURLPublic() + group.getFriendlyURL();
		String[] urlSeparators =
			FriendlyURLResolverRegistryUtil.getURLSeparators(
				group.getCompanyId());

		for (String url : staticSiteExportDocument.getURLs()) {
			StaticSiteExportURL staticSiteExportURL = new StaticSiteExportURL(
				portalHostNames, url);

			if (staticSiteExportURL.hasScheme() ||
				staticSiteExportURL.isExternal() ||
				(staticSiteExportURL.getQueryString() != null)) {

				continue;
			}

			String path = staticSiteExportURL.getURL();

			int index = path.indexOf(siteURL);

			if ((index == -1) ||
				((index > 0) &&
				 (path.lastIndexOf(CharPool.SLASH, index - 1) != 0))) {

				continue;
			}

			String friendlyURL = path.substring(index + siteURL.length());

			for (String urlSeparator : urlSeparators) {
				if (friendlyURL.startsWith(urlSeparator)) {
					displayPageFriendlyURLs.add(friendlyURL);

					break;
				}
			}
		}

		return displayPageFriendlyURLs;
	}

	private StaticSiteExportEntryRendering
			_getDisplayPageStaticSiteExportEntryRendering(
				String friendlyURL, Group group, Locale locale)
		throws Exception {

		FriendlyURLResolver friendlyURLResolver =
			FriendlyURLResolverRegistryUtil.getFriendlyURLResolver(
				group.getCompanyId(), _getURLSeparator(friendlyURL));

		if (friendlyURLResolver == null) {
			return null;
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		LayoutFriendlyURLComposite layoutFriendlyURLComposite =
			friendlyURLResolver.getLayoutFriendlyURLComposite(
				group.getCompanyId(), group.getGroupId(), false, friendlyURL,
				new HashMap<>(),
				HashMapBuilder.<String, Object>put(
					WebKeys.LOCALE, locale
				).put(
					"request", serviceContext.getRequest()
				).build());

		Layout layout = layoutFriendlyURLComposite.getLayout();

		try (AutoCloseable autoCloseable =
				_layoutServiceContextHelper.getServiceContextAutoCloseable(
					layout)) {

			serviceContext = ServiceContextThreadLocal.getServiceContext();

			HttpServletRequest httpServletRequest = serviceContext.getRequest();

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			themeDisplay.setLanguageId(LocaleUtil.toLanguageId(locale));
			themeDisplay.setLocale(locale);

			friendlyURLResolver.getActualURL(
				group.getCompanyId(), group.getGroupId(), false,
				_portal.getPathMain(), friendlyURL, new HashMap<>(),
				HashMapBuilder.<String, Object>put(
					WebKeys.LOCALE, locale
				).put(
					"request", httpServletRequest
				).build());

			LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
				(LayoutDisplayPageObjectProvider<?>)
					httpServletRequest.getAttribute(
						LayoutDisplayPageWebKeys.
							LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER);

			if (layoutDisplayPageObjectProvider == null) {
				return null;
			}

			String localizedFriendlyURL =
				layoutFriendlyURLComposite.getFriendlyURL();

			String html = _render(
				layout, locale,
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(layout.getPlid()),
				serviceContext);

			String path = _getPath(
				URLCodec.decodeURL(localizedFriendlyURL), locale);

			return new StaticSiteExportEntryRendering(
				SetUtil.fromArray(friendlyURL, localizedFriendlyURL),
				StringBundler.concat(
					layoutDisplayPageObjectProvider.getClassName(),
					StringPool.COLON,
					layoutDisplayPageObjectProvider.getClassPK()),
				locale, path, new StaticSiteExportDocument(html, _jsonFactory),
				renderedHTML -> new DisplayPageStaticSiteExportEntry(
					layoutDisplayPageObjectProvider.getClassName(),
					layoutDisplayPageObjectProvider.getClassPK(),
					layoutDisplayPageObjectProvider.getExternalReferenceCode(),
					renderedHTML, layout.getExternalReferenceCode(), locale,
					path));
		}
	}

	private List<Layout> _getExportableLayouts(List<Layout> layouts) {
		List<Layout> exportableLayouts = new ArrayList<>();

		for (Layout layout : layouts) {
			if (layout.isHidden() || !layout.isPublished() ||
				layout.isSystem() ||
				(layout.getStatus() != WorkflowConstants.STATUS_APPROVED) ||
				!Objects.equals(
					layout.getType(), LayoutConstants.TYPE_CONTENT)) {

				continue;
			}

			exportableLayouts.add(layout);
		}

		return exportableLayouts;
	}

	private Set<String> _getModuleURLs(
		String content, Set<Locale> locales, Set<String> moduleNames,
		StaticSiteExportBundleResourceResolver
			staticSiteExportBundleResourceResolver) {

		Set<String> moduleURLs = new LinkedHashSet<>();

		Matcher matcher = _moduleNamePattern.matcher(content);

		while (matcher.find()) {
			String moduleName = matcher.group(1);

			if (!moduleNames.add(moduleName)) {
				continue;
			}

			for (Locale locale : locales) {
				moduleURLs.add(
					StringBundler.concat(
						"/o/js/language/", LocaleUtil.toLanguageId(locale),
						StringPool.SLASH, moduleName, "/all.js"));
			}

			try {
				for (String resourcePath :
						staticSiteExportBundleResourceResolver.getResourcePaths(
							moduleName, "/__liferay__/")) {

					if (resourcePath.endsWith(".map")) {
						continue;
					}

					if (HashedFilesUtil.containsHash(resourcePath)) {
						resourcePath = HashedFilesUtil.removeHash(resourcePath);
					}

					moduleURLs.add(
						StringBundler.concat("/o/", moduleName, resourcePath));
				}

				for (String resourcePath :
						staticSiteExportBundleResourceResolver.getResourcePaths(
							moduleName, StringPool.SLASH)) {

					if (!resourcePath.endsWith(".css") ||
						!HashedFilesUtil.containsHash(resourcePath)) {

						continue;
					}

					moduleURLs.add(
						StringBundler.concat("/o/", moduleName, resourcePath));
				}
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to list the resources of " + moduleName,
						exception);
				}
			}
		}

		return moduleURLs;
	}

	private Set<String> _getNestedResourceURLs(
		Set<Locale> locales, Set<String> moduleNames,
		Set<String> portalHostNames,
		StaticSiteExportBundleResourceResolver
			staticSiteExportBundleResourceResolver,
		StaticSiteExportResourceFile staticSiteExportResourceFile, String url) {

		String extension = staticSiteExportResourceFile.getExtension();

		try {
			if (Objects.equals(extension, "css")) {
				return _getStylesheetResourceURLs(
					FileUtil.read(staticSiteExportResourceFile.getFile()),
					portalHostNames, url);
			}

			if (Objects.equals(extension, "js")) {
				return _getModuleURLs(
					FileUtil.read(staticSiteExportResourceFile.getFile()),
					locales, moduleNames,
					staticSiteExportBundleResourceResolver);
			}
		}
		catch (IOException ioException) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to read " + url, ioException);
			}
		}

		return Collections.emptySet();
	}

	private List<String> _getOpenGraphLocaleAlternates(
		Locale locale, Set<Locale> locales) {

		List<String> openGraphLocaleAlternates = new ArrayList<>();

		for (Locale alternateLocale : locales) {
			if (!Objects.equals(alternateLocale, locale)) {
				openGraphLocaleAlternates.add(
					LocaleUtil.toLanguageId(alternateLocale));
			}
		}

		Collections.sort(openGraphLocaleAlternates);

		return openGraphLocaleAlternates;
	}

	private Map<Locale, Map<String, String>> _getPagePathsMap(
		Group group,
		List<StaticSiteExportEntryRendering> staticSiteExportEntryRenderings) {

		Map<Locale, Map<String, String>> pagePathsMap = new HashMap<>();

		Set<Locale> locales = new HashSet<>();
		Map<String, String> localizedPagePaths = new HashMap<>();
		String siteURL =
			_portal.getPathFriendlyURLPublic() + group.getFriendlyURL();

		for (StaticSiteExportEntryRendering staticSiteExportEntryRendering :
				staticSiteExportEntryRenderings) {

			Locale locale = staticSiteExportEntryRendering.getLocale();

			locales.add(locale);

			String languageId = LocaleUtil.toLanguageId(locale);

			String i18nPathLanguageId = _portal.getI18nPathLanguageId(
				locale, languageId);

			String path = staticSiteExportEntryRendering.getPath();

			for (String friendlyURL :
					staticSiteExportEntryRendering.getFriendlyURLs()) {

				String url = siteURL + friendlyURL;

				localizedPagePaths.put(
					StringBundler.concat(
						StringPool.SLASH, i18nPathLanguageId, url),
					path);
				localizedPagePaths.put(
					StringBundler.concat(StringPool.SLASH, languageId, url),
					path);
				localizedPagePaths.put(
					StringBundler.concat(
						StringPool.SLASH, locale.getLanguage(), url),
					path);
			}
		}

		for (Locale locale : locales) {
			Map<String, String> pagePaths = new HashMap<>(localizedPagePaths);

			for (StaticSiteExportEntryRendering staticSiteExportEntryRendering :
					staticSiteExportEntryRenderings) {

				if (!Objects.equals(
						staticSiteExportEntryRendering.getLocale(), locale)) {

					continue;
				}

				for (StaticSiteExportEntryRendering
						urlStaticSiteExportEntryRendering :
							staticSiteExportEntryRenderings) {

					if (!Objects.equals(
							urlStaticSiteExportEntryRendering.getKey(),
							staticSiteExportEntryRendering.getKey())) {

						continue;
					}

					for (String friendlyURL :
							urlStaticSiteExportEntryRendering.
								getFriendlyURLs()) {

						pagePaths.put(
							siteURL + friendlyURL,
							staticSiteExportEntryRendering.getPath());
					}
				}
			}

			pagePathsMap.put(locale, pagePaths);
		}

		return pagePathsMap;
	}

	private String _getPath(String friendlyURL, Locale locale) {
		return StringBundler.concat(
			_portal.getI18nPathLanguageId(
				locale, LocaleUtil.toLanguageId(locale)),
			friendlyURL, ".html");
	}

	private Set<String> _getPortalHostNames(
		Company company, Group group, String portalURL) {

		Set<String> portalHostNames = new HashSet<>();

		_addPortalHostName(company.getVirtualHostname(), portalHostNames);

		LayoutSet layoutSet = group.getPublicLayoutSet();

		Map<String, String> virtualHostnames = layoutSet.getVirtualHostnames();

		for (String virtualHostname : virtualHostnames.keySet()) {
			_addPortalHostName(virtualHostname, portalHostNames);
		}

		StaticSiteExportURL staticSiteExportURL = new StaticSiteExportURL(
			portalURL);

		_addPortalHostName(staticSiteExportURL.getHostName(), portalHostNames);

		_addPortalHostName(PropsValues.WEB_SERVER_HOST, portalHostNames);

		for (String validHost : PropsValues.VIRTUAL_HOSTS_VALID_HOSTS) {
			if (!Objects.equals(validHost, StringPool.STAR)) {
				_addPortalHostName(validHost, portalHostNames);
			}
		}

		return portalHostNames;
	}

	private Set<String> _getResourceURLs(
		Set<String> portalHostNames,
		StaticSiteExportDocument staticSiteExportDocument) {

		Set<String> resourceURLs = new LinkedHashSet<>();

		for (String url : staticSiteExportDocument.getURLs()) {
			if (Validator.isNull(url)) {
				continue;
			}

			StaticSiteExportURL staticSiteExportURL = new StaticSiteExportURL(
				portalHostNames, url);

			if (staticSiteExportURL.isResource()) {
				resourceURLs.add(staticSiteExportURL.getURL());
			}
		}

		for (String url : staticSiteExportDocument.getEmbeddedURLs()) {
			if (Validator.isNull(url)) {
				continue;
			}

			StaticSiteExportURL staticSiteExportURL = new StaticSiteExportURL(
				portalHostNames, url);

			if (staticSiteExportURL.isExternal()) {
				resourceURLs.add(staticSiteExportURL.getURL());
			}
		}

		return resourceURLs;
	}

	private List<StaticSiteExportEntryRendering>
			_getStaticSiteExportEntryRenderings(
				Group group,
				List<StaticSiteExportReport.Failure> layoutFailures,
				List<Layout> layouts, Set<Locale> locales,
				Set<String> portalHostNames)
		throws PortalException {

		List<StaticSiteExportEntryRendering> staticSiteExportEntryRenderings =
			new ArrayList<>();

		Layout defaultLayout = _layoutLocalService.fetchFirstLayout(
			group.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);

		for (Layout layout : _getExportableLayouts(layouts)) {
			long segmentsExperienceId =
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(layout.getPlid());

			for (Locale locale : locales) {
				String friendlyURL = layout.getFriendlyURL(locale);

				Set<String> friendlyURLs = SetUtil.fromArray(friendlyURL);

				if ((defaultLayout != null) &&
					(defaultLayout.getPlid() == layout.getPlid())) {

					friendlyURLs.add(StringPool.BLANK);
					friendlyURLs.add(StringPool.SLASH);
				}

				String path = _getPath(friendlyURL, locale);

				try {
					staticSiteExportEntryRenderings.add(
						new StaticSiteExportEntryRendering(
							friendlyURLs, layout.getExternalReferenceCode(),
							locale, path,
							new StaticSiteExportDocument(
								_render(layout, locale, segmentsExperienceId),
								_jsonFactory),
							renderedHTML -> new LayoutStaticSiteExportEntry(
								layout.getExternalReferenceCode(), renderedHTML,
								locale, path)));
				}
				catch (Exception exception) {
					if (_log.isWarnEnabled()) {
						_log.warn("Unable to render " + friendlyURL, exception);
					}

					layoutFailures.add(
						new StaticSiteExportReport.Failure(
							exception.getMessage(), friendlyURL));
				}
			}
		}

		for (Locale locale : locales) {
			_exportDisplayPageStaticSiteExportEntryRenderings(
				group, layoutFailures, locale, portalHostNames,
				staticSiteExportEntryRenderings);
		}

		return staticSiteExportEntryRenderings;
	}

	private Set<String> _getStylesheetResourceURLs(
		String content, Set<String> portalHostNames, String url) {

		Set<String> stylesheetResourceURLs = new LinkedHashSet<>();

		StaticSiteExportURL staticSiteExportURL = new StaticSiteExportURL(
			portalHostNames, url);

		Matcher matcher = _stylesheetResourceURLPattern.matcher(content);

		while (matcher.find()) {
			String stylesheetResourceURL = matcher.group(1);

			if (stylesheetResourceURL == null) {
				stylesheetResourceURL = matcher.group(2);
			}

			StaticSiteExportURL resolvedStaticSiteExportURL =
				staticSiteExportURL.resolve(stylesheetResourceURL);

			if (resolvedStaticSiteExportURL != null) {
				stylesheetResourceURLs.add(
					resolvedStaticSiteExportURL.getURL());
			}
		}

		return stylesheetResourceURLs;
	}

	private String _getURLSeparator(String friendlyURL) {
		int index = friendlyURL.indexOf(CharPool.SLASH, 1);

		if (index == -1) {
			return friendlyURL;
		}

		return friendlyURL.substring(0, index + 1);
	}

	private boolean _hasAUIModuleURL(Set<String> resourceURLs) {
		for (String resourceURL : resourceURLs) {
			if (resourceURL.startsWith("/o/" + _AUI_MODULE_NAME + "/")) {
				return true;
			}
		}

		return false;
	}

	private void _putStaticSiteExportResource(
		Map<String, String> resourcePaths,
		StaticSiteExportResourceFile staticSiteExportResourceFile,
		List<StaticSiteExportResource> staticSiteExportResources, String url) {

		StaticSiteExportURL staticSiteExportURL = new StaticSiteExportURL(url);

		StaticSiteExportResource staticSiteExportResource =
			new StaticSiteExportResource(
				staticSiteExportResourceFile.getFile(),
				staticSiteExportURL.getArchivePath(
					staticSiteExportResourceFile.getExtension()),
				url);

		staticSiteExportResources.add(staticSiteExportResource);

		resourcePaths.put(url, staticSiteExportResource.getPath());
	}

	private String _render(
			Layout layout, Locale locale, long segmentsExperienceId)
		throws Exception {

		try (AutoCloseable autoCloseable =
				_layoutServiceContextHelper.getServiceContextAutoCloseable(
					layout)) {

			return _render(
				layout, locale, segmentsExperienceId,
				ServiceContextThreadLocal.getServiceContext());
		}
	}

	private String _render(
			Layout layout, Locale locale, long segmentsExperienceId,
			ServiceContext serviceContext)
		throws Exception {

		HttpServletRequest httpServletRequest = serviceContext.getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		themeDisplay.setCDNBaseURL(StringPool.BLANK);
		themeDisplay.setThemeJsFastLoad(false);

		return _layoutPreviewRenderer.render(
			layout, locale, segmentsExperienceId, serviceContext);
	}

	private List<StaticSiteExportEntry> _rewriteStaticSiteExportEntryRenderings(
		Locale siteDefaultLocale,
		List<StaticSiteExportEntryRendering> staticSiteExportEntryRenderings,
		StaticSiteExportURLRewriter staticSiteExportURLRewriter) {

		Map<String, Map<Locale, String>> pathsMap = new HashMap<>();

		for (StaticSiteExportEntryRendering staticSiteExportEntryRendering :
				staticSiteExportEntryRenderings) {

			Map<Locale, String> paths = pathsMap.get(
				staticSiteExportEntryRendering.getKey());

			if (paths == null) {
				paths = new HashMap<>();

				pathsMap.put(staticSiteExportEntryRendering.getKey(), paths);
			}

			paths.put(
				staticSiteExportEntryRendering.getLocale(),
				staticSiteExportEntryRendering.getPath());
		}

		List<StaticSiteExportEntry> staticSiteExportEntries = new ArrayList<>();

		for (StaticSiteExportEntryRendering staticSiteExportEntryRendering :
				staticSiteExportEntryRenderings) {

			StaticSiteExportDocument staticSiteExportDocument =
				staticSiteExportEntryRendering.getStaticSiteExportDocument();

			staticSiteExportURLRewriter.rewrite(
				staticSiteExportEntryRendering.getLocale(),
				staticSiteExportDocument);

			Map<Locale, String> paths = pathsMap.get(
				staticSiteExportEntryRendering.getKey());

			String canonicalPath =
				StringPool.SLASH + staticSiteExportEntryRendering.getPath();

			staticSiteExportDocument.replaceLinks(
				_getAlternatePaths(paths, siteDefaultLocale), canonicalPath);
			staticSiteExportDocument.replaceOpenGraphMetaTags(
				canonicalPath,
				_getOpenGraphLocaleAlternates(
					staticSiteExportEntryRendering.getLocale(),
					paths.keySet()));

			staticSiteExportEntries.add(
				staticSiteExportEntryRendering.toStaticSiteExportEntry());
		}

		return staticSiteExportEntries;
	}

	private void _rewriteStylesheet(
			File file, Set<String> portalHostNames,
			Map<String, String> resourcePaths, String url)
		throws IOException {

		String content = FileUtil.read(file);

		StringBundler sb = new StringBundler();

		int index = 0;

		StaticSiteExportURL staticSiteExportURL = new StaticSiteExportURL(
			portalHostNames, url);

		Matcher matcher = _stylesheetResourceURLPattern.matcher(content);

		while (matcher.find()) {
			int groupIndex = 1;

			if (matcher.group(groupIndex) == null) {
				groupIndex = 2;
			}

			String stylesheetResourceURL = matcher.group(groupIndex);

			StaticSiteExportURL resolvedStaticSiteExportURL =
				staticSiteExportURL.resolve(stylesheetResourceURL);

			if (resolvedStaticSiteExportURL == null) {
				continue;
			}

			String path = resourcePaths.get(
				resolvedStaticSiteExportURL.getURL());

			if (path == null) {
				continue;
			}

			StaticSiteExportURL stylesheetResourceStaticSiteExportURL =
				new StaticSiteExportURL(stylesheetResourceURL);

			sb.append(content.substring(index, matcher.start(groupIndex)));
			sb.append(StringPool.SLASH);
			sb.append(path);
			sb.append(stylesheetResourceStaticSiteExportURL.getURIFragment());

			index = matcher.end(groupIndex);
		}

		if (index == 0) {
			return;
		}

		sb.append(content.substring(index));

		FileUtil.write(file, sb.toString());
	}

	private static final String _AUI_MODULE_NAME = "frontend-js-aui-web";

	private static final Log _log = LogFactoryUtil.getLog(
		StaticSiteExporterImpl.class);

	private static final Pattern _moduleNamePattern = Pattern.compile(
		"([a-z0-9][a-z0-9.\\-]*)/__liferay__/");
	private static final Pattern _stylesheetResourceURLPattern =
		Pattern.compile(
			"url\\(\\s*[\"']?([^)\"'\\s]+)|@import\\s+[\"']([^\"']+)");

	private BundleContext _bundleContext;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPreviewRenderer _layoutPreviewRenderer;

	@Reference
	private LayoutService _layoutService;

	@Reference
	private LayoutServiceContextHelper _layoutServiceContextHelper;

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}