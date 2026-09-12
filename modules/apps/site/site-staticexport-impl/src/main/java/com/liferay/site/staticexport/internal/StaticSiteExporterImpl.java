/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.staticexport.internal;

import com.liferay.layout.renderer.LayoutPreviewRenderer;
import com.liferay.layout.util.LayoutServiceContextHelper;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
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
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.site.staticexport.StaticSiteExport;
import com.liferay.site.staticexport.StaticSiteExportLayout;
import com.liferay.site.staticexport.StaticSiteExportResource;
import com.liferay.site.staticexport.StaticSiteExporter;

import jakarta.servlet.http.HttpServletRequest;

import java.io.File;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
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

		try (AutoCloseable autoCloseable =
				_layoutServiceContextHelper.getServiceContextAutoCloseable(
					_companyLocalService.getCompany(group.getCompanyId()))) {

			StaticSiteExportReportImpl staticSiteExportReportImpl =
				new StaticSiteExportReportImpl();

			List<StaticSiteExportLayout> staticSiteExportLayouts =
				_exportStaticSiteExportLayouts(
					groupId, locales, staticSiteExportReportImpl);

			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			return new StaticSiteExportImpl(
				staticSiteExportLayouts, staticSiteExportReportImpl,
				_fetchStaticSiteExportResources(
					serviceContext.getRequest(), staticSiteExportLayouts,
					staticSiteExportReportImpl));
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
			long groupId, Set<Locale> locales,
			StaticSiteExportReportImpl staticSiteExportReportImpl)
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
						new StaticSiteExportLayoutImpl(
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

					staticSiteExportReportImpl.addLayoutFailure(
						exception.getMessage(), friendlyURL);
				}
			}
		}

		return staticSiteExportLayouts;
	}

	private List<StaticSiteExportResource> _fetchStaticSiteExportResources(
			HttpServletRequest httpServletRequest,
			List<StaticSiteExportLayout> staticSiteExportLayouts,
			StaticSiteExportReportImpl staticSiteExportReportImpl)
		throws Exception {

		List<StaticSiteExportResource> staticSiteExportResources =
			new ArrayList<>();

		StaticSiteExportResourceHarvester staticSiteExportResourceHarvester =
			new StaticSiteExportResourceHarvester(_jsonFactory);

		Map<String, String> importMapPrefixes = new LinkedHashMap<>();
		Deque<String> urls = new ArrayDeque<>();

		for (StaticSiteExportLayout staticSiteExportLayout :
				staticSiteExportLayouts) {

			String html = staticSiteExportLayout.getHTML();

			importMapPrefixes.putAll(
				staticSiteExportResourceHarvester.harvestImportMapPrefixes(
					html));
			urls.addAll(staticSiteExportResourceHarvester.harvestHTML(html));
		}

		StaticSiteExportResourceFetcher staticSiteExportResourceFetcher =
			new StaticSiteExportResourceFetcher(
				_bundleContext, httpServletRequest,
				new DummyHttpServletResponse(),
				ServletContextPool.get(_portal.getServletContextName()),
				new StaticSiteExportBundleResourceResolver(_bundleContext));

		Set<String> visitedURLs = new HashSet<>();

		while (!urls.isEmpty()) {
			String url = urls.removeFirst();

			if (!visitedURLs.add(url)) {
				continue;
			}

			File file = null;

			try {
				file = staticSiteExportResourceFetcher.fetch(url);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug("Unable to fetch " + url, exception);
				}

				staticSiteExportReportImpl.addResourceFailure(
					exception.getMessage(), url);

				continue;
			}

			if (file == null) {
				staticSiteExportReportImpl.addResourceFailure(
					"No servlet serves the resource", url);

				continue;
			}

			staticSiteExportResources.add(
				new StaticSiteExportResourceImpl(
					file, StaticSiteExportResourcePathUtil.getPath(url), url));

			if (_isStylesheetURL(url)) {
				urls.addAll(
					staticSiteExportResourceHarvester.harvestCSS(
						FileUtil.read(file), url));
			}
			else if (_isScriptURL(url)) {
				urls.addAll(
					staticSiteExportResourceHarvester.harvestJS(
						importMapPrefixes, FileUtil.read(file), url));
			}
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

	private String _getPath(
		String friendlyURL, Locale locale, Locale siteDefaultLocale) {

		String path = StringUtil.removeFirst(friendlyURL, StringPool.SLASH);

		if (Objects.equals(locale, siteDefaultLocale)) {
			return path + ".html";
		}

		return StringBundler.concat(
			LocaleUtil.toLanguageId(locale), StringPool.SLASH, path, ".html");
	}

	private boolean _isScriptURL(String url) {
		if (url.endsWith(".js") || url.contains(".js?")) {
			return true;
		}

		return false;
	}

	private boolean _isStylesheetURL(String url) {
		if (url.endsWith(".css") || url.contains(".css?")) {
			return true;
		}

		return false;
	}

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