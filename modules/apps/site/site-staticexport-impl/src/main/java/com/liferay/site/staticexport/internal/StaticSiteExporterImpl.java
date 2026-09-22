/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.staticexport.internal;

import com.liferay.layout.renderer.LayoutPreviewRenderer;
import com.liferay.layout.util.LayoutServiceContextHelper;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.servlet.DummyHttpServletResponse;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.site.staticexport.StaticSiteExport;
import com.liferay.site.staticexport.StaticSiteExportLayout;
import com.liferay.site.staticexport.StaticSiteExportReport;
import com.liferay.site.staticexport.StaticSiteExportResource;
import com.liferay.site.staticexport.StaticSiteExporter;

import jakarta.servlet.http.HttpServletRequest;

import java.io.File;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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

		Group group = _groupLocalService.getGroup(groupId);

		Company company = _companyLocalService.getCompany(group.getCompanyId());

		try (AutoCloseable autoCloseable =
				_layoutServiceContextHelper.getServiceContextAutoCloseable(
					company)) {

			List<StaticSiteExportReport.Failure> layoutFailures =
				new ArrayList<>();

			List<StaticSiteExportLayout> staticSiteExportLayouts =
				_exportStaticSiteExportLayouts(
					groupId, layoutFailures, locales);

			Map<StaticSiteExportLayout, StaticSiteExportDocument>
				staticSiteExportDocuments = new LinkedHashMap<>();

			for (StaticSiteExportLayout staticSiteExportLayout :
					staticSiteExportLayouts) {

				staticSiteExportDocuments.put(
					staticSiteExportLayout,
					new StaticSiteExportDocument(
						staticSiteExportLayout.getHTML(), _jsonFactory));
			}

			List<StaticSiteExportReport.Failure> resourceFailures =
				new ArrayList<>();

			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			String portalURL = _portal.getPortalURL(
				company.getVirtualHostname(),
				_portal.getPortalServerPort(false), false);

			List<StaticSiteExportResource> staticSiteExportResources =
				_fetchStaticSiteExportResources(
					serviceContext.getRequest(), portalURL, resourceFailures,
					staticSiteExportDocuments.values());

			StaticSiteExportURLRewriter staticSiteExportURLRewriter =
				new StaticSiteExportURLRewriter(
					_getPagePaths(group, portalURL, staticSiteExportLayouts),
					_getResourcePaths(staticSiteExportResources));

			return new StaticSiteExportImpl(
				_rewriteLayouts(
					staticSiteExportDocuments, staticSiteExportURLRewriter),
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

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	private List<StaticSiteExportLayout> _exportStaticSiteExportLayouts(
			long groupId, List<StaticSiteExportReport.Failure> layoutFailures,
			Set<Locale> locales)
		throws PortalException {

		List<StaticSiteExportLayout> staticSiteExportLayouts =
			new ArrayList<>();

		Locale siteDefaultLocale = _portal.getSiteDefaultLocale(groupId);

		for (Layout layout : _getExportableLayouts(groupId)) {
			long segmentsExperienceId =
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(layout.getPlid());

			for (Locale locale : locales) {
				String friendlyURL = layout.getFriendlyURL(locale);

				try {
					staticSiteExportLayouts.add(
						new StaticSiteExportLayout(
							_layoutPreviewRenderer.render(
								layout, locale, segmentsExperienceId),
							locale,
							_getPath(friendlyURL, locale, siteDefaultLocale),
							layout.getPlid()));
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

		return staticSiteExportLayouts;
	}

	private List<StaticSiteExportResource> _fetchStaticSiteExportResources(
		HttpServletRequest httpServletRequest, String portalURL,
		List<StaticSiteExportReport.Failure> resourceFailures,
		Collection<StaticSiteExportDocument> staticSiteExportDocuments) {

		List<StaticSiteExportResource> staticSiteExportResources =
			new ArrayList<>();

		StaticSiteExportResourceFetcher staticSiteExportResourceFetcher =
			new StaticSiteExportResourceFetcher(
				httpServletRequest, new DummyHttpServletResponse(), portalURL,
				ServletContextPool.get(_portal.getServletContextName()),
				new StaticSiteExportBundleResourceResolver(_bundleContext));

		for (String url : _getResourceURLs(staticSiteExportDocuments)) {
			File file = null;

			try {
				file = staticSiteExportResourceFetcher.fetch(url);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug("Unable to fetch " + url, exception);
				}

				resourceFailures.add(
					new StaticSiteExportReport.Failure(
						exception.getMessage(), url));

				continue;
			}

			if (file == null) {
				resourceFailures.add(
					new StaticSiteExportReport.Failure(
						"No servlet serves the resource", url));

				continue;
			}

			staticSiteExportResources.add(
				new StaticSiteExportResource(
					file, StaticSiteExportResourcePathUtil.getPath(url), url));
		}

		return staticSiteExportResources;
	}

	private List<Layout> _getExportableLayouts(long groupId) {
		List<Layout> layouts = new ArrayList<>();

		for (Layout layout : _layoutLocalService.getLayouts(groupId, false)) {
			if (layout.isHidden() || !layout.isPublished() ||
				layout.isSystem() ||
				(layout.getStatus() != WorkflowConstants.STATUS_APPROVED) ||
				!Objects.equals(
					layout.getType(), LayoutConstants.TYPE_CONTENT)) {

				continue;
			}

			layouts.add(layout);
		}

		return layouts;
	}

	private Map<String, String> _getPagePaths(
			Group group, String portalURL,
			List<StaticSiteExportLayout> staticSiteExportLayouts)
		throws PortalException {

		Map<String, String> pagePaths = new HashMap<>();

		Layout defaultLayout = _layoutLocalService.fetchFirstLayout(
			group.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);

		Locale siteDefaultLocale = _portal.getSiteDefaultLocale(
			group.getGroupId());

		String siteURL =
			_portal.getPathFriendlyURLPublic() + group.getFriendlyURL();

		for (StaticSiteExportLayout staticSiteExportLayout :
				staticSiteExportLayouts) {

			Layout layout = _layoutLocalService.fetchLayout(
				staticSiteExportLayout.getPlid());

			Locale locale = staticSiteExportLayout.getLocale();

			String path = staticSiteExportLayout.getPath();

			List<String> urls = new ArrayList<>();

			urls.add(siteURL + layout.getFriendlyURL(locale));

			if ((defaultLayout != null) &&
				(defaultLayout.getPlid() == layout.getPlid())) {

				urls.add(siteURL);
				urls.add(siteURL + StringPool.SLASH);
			}

			for (String url : urls) {
				if (Objects.equals(locale, siteDefaultLocale)) {
					_putPagePath(pagePaths, path, portalURL, url);
				}

				_putPagePath(
					pagePaths, path, portalURL,
					StringBundler.concat(
						StringPool.SLASH, LocaleUtil.toLanguageId(locale),
						url));
				_putPagePath(
					pagePaths, path, portalURL,
					StringBundler.concat(
						StringPool.SLASH, locale.getLanguage(), url));
			}
		}

		return pagePaths;
	}

	private String _getPath(
		String friendlyURL, Locale locale, Locale siteDefaultLocale) {

		String path = StringUtil.removeFirst(friendlyURL, StringPool.SLASH);

		if (Objects.equals(locale, siteDefaultLocale)) {
			return path + ".html";
		}

		return StringBundler.concat(
			LocaleUtil.toLanguageId(locale), StringPool.SLASH, path, ".html");
	}

	private Map<String, String> _getResourcePaths(
		List<StaticSiteExportResource> staticSiteExportResources) {

		Map<String, String> resourcePaths = new HashMap<>();

		for (StaticSiteExportResource staticSiteExportResource :
				staticSiteExportResources) {

			resourcePaths.put(
				staticSiteExportResource.getURL(),
				staticSiteExportResource.getPath());
		}

		return resourcePaths;
	}

	private Set<String> _getResourceURLs(
		Collection<StaticSiteExportDocument> staticSiteExportDocuments) {

		Set<String> resourceURLs = new LinkedHashSet<>();

		for (StaticSiteExportDocument staticSiteExportDocument :
				staticSiteExportDocuments) {

			for (String url : staticSiteExportDocument.getURLs()) {
				if (Validator.isNull(url)) {
					continue;
				}

				url = _removeURLFragment(StringUtil.trim(url));

				for (String resourcePrefix : _RESOURCE_PREFIXES) {
					if (url.startsWith(resourcePrefix)) {
						resourceURLs.add(url);

						break;
					}
				}
			}
		}

		return resourceURLs;
	}

	private void _putPagePath(
		Map<String, String> pagePaths, String path, String portalURL,
		String url) {

		pagePaths.put(portalURL + url, path);
		pagePaths.put(url, path);
	}

	private String _removeURLFragment(String url) {
		int index = url.indexOf(CharPool.POUND);

		if (index == -1) {
			return url;
		}

		return url.substring(0, index);
	}

	private List<StaticSiteExportLayout> _rewriteLayouts(
		Map<StaticSiteExportLayout, StaticSiteExportDocument>
			staticSiteExportDocuments,
		StaticSiteExportURLRewriter staticSiteExportURLRewriter) {

		List<StaticSiteExportLayout> rewrittenStaticSiteExportLayouts =
			new ArrayList<>();

		for (Map.Entry<StaticSiteExportLayout, StaticSiteExportDocument> entry :
				staticSiteExportDocuments.entrySet()) {

			StaticSiteExportLayout staticSiteExportLayout = entry.getKey();

			StaticSiteExportDocument staticSiteExportDocument =
				entry.getValue();

			staticSiteExportURLRewriter.rewrite(staticSiteExportDocument);

			rewrittenStaticSiteExportLayouts.add(
				new StaticSiteExportLayout(
					staticSiteExportDocument.getHTML(),
					staticSiteExportLayout.getLocale(),
					staticSiteExportLayout.getPath(),
					staticSiteExportLayout.getPlid()));
		}

		return rewrittenStaticSiteExportLayouts;
	}

	private static final String[] _RESOURCE_PREFIXES = {
		"/combo", "/documents/", "/image/", "/o/", "/webserver/"
	};

	private static final Log _log = LogFactoryUtil.getLog(
		StaticSiteExporterImpl.class);

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
	private LayoutServiceContextHelper _layoutServiceContextHelper;

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}