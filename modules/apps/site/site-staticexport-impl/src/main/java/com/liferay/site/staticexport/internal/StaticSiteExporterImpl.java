/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.staticexport.internal;

import com.liferay.layout.renderer.LayoutPreviewRenderer;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.site.staticexport.StaticSiteExport;
import com.liferay.site.staticexport.StaticSiteExportLayout;
import com.liferay.site.staticexport.StaticSiteExporter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

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

		List<StaticSiteExportLayout> staticSiteExportLayouts =
			new ArrayList<>();

		Locale siteDefaultLocale = _portal.getSiteDefaultLocale(groupId);

		try {
			for (Layout layout : _getExportableLayouts(groupId)) {
				long segmentsExperienceId =
					_segmentsExperienceLocalService.
						fetchDefaultSegmentsExperienceId(layout.getPlid());

				for (Locale locale : locales) {
					staticSiteExportLayouts.add(
						new StaticSiteExportLayoutImpl(
							_layoutPreviewRenderer.render(
								layout, locale, segmentsExperienceId),
							locale,
							_getPath(
								layout.getFriendlyURL(locale), locale,
								siteDefaultLocale),
							layout.getPlid()));
				}
			}
		}
		catch (PortalException portalException) {
			throw portalException;
		}
		catch (Exception exception) {
			throw new PortalException(exception);
		}

		return new StaticSiteExportImpl(staticSiteExportLayouts);
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

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPreviewRenderer _layoutPreviewRenderer;

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}