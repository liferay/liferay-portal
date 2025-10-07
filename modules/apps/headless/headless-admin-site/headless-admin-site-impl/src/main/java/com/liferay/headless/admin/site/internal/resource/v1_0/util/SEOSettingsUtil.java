/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0.util;

import com.liferay.headless.admin.site.dto.v1_0.SEOSettings;
import com.liferay.headless.admin.site.dto.v1_0.SitemapSettings;
import com.liferay.layout.seo.model.LayoutSEOEntry;
import com.liferay.layout.seo.service.LayoutSEOEntryLocalService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.HashMap;

/**
 * @author Jürgen Kappler
 * @author Javier de Arcos
 */
public class SEOSettingsUtil {

	public static SEOSettings getSeoSettings(
		LayoutSEOEntryLocalService layoutSEOEntryLocalService, Layout layout) {

		LayoutSEOEntry layoutSEOEntry =
			layoutSEOEntryLocalService.fetchLayoutSEOEntry(
				layout.getGroupId(), layout.isPrivateLayout(),
				layout.getLayoutId());

		return new SEOSettings() {
			{
				setCustomCanonicalURL_i18n(
					() -> {
						if ((layoutSEOEntry == null) ||
							!layoutSEOEntry.isCanonicalURLEnabled()) {

							return new HashMap<>();
						}

						return LocalizedMapUtil.getI18nMap(
							layoutSEOEntry.getCanonicalURLMap());
					});
				setDescription_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						layout.getDescriptionMap()));
				setHtmlTitle_i18n(
					() -> LocalizedMapUtil.getI18nMap(layout.getTitleMap()));
				setRobots_i18n(
					() -> LocalizedMapUtil.getI18nMap(layout.getRobotsMap()));
				setSeoKeywords_i18n(
					() -> LocalizedMapUtil.getI18nMap(layout.getKeywordsMap()));
				setSitemapSettings(
					() -> _toSitemapSettings(
						layout.getTypeSettingsProperties()));
			}
		};
	}

	private static SitemapSettings _toSitemapSettings(
		UnicodeProperties unicodeProperties) {

		String sitemapChangeFreq = unicodeProperties.getProperty(
			"sitemap-changefreq");
		String sitemapInclude = unicodeProperties.getProperty(
			"sitemap-include");
		String sitemapIncludeChildLayouts = unicodeProperties.getProperty(
			"sitemap-include-child-layouts");
		String sitemapPriority = unicodeProperties.getProperty(
			"sitemap-priority");

		if ((sitemapChangeFreq == null) && (sitemapInclude == null) &&
			(sitemapIncludeChildLayouts == null) && (sitemapPriority == null)) {

			return null;
		}

		return new SitemapSettings() {
			{
				setChangeFrequency(
					() -> {
						if (sitemapChangeFreq == null) {
							return null;
						}

						return ChangeFrequency.create(
							StringUtil.upperCaseFirstLetter(sitemapChangeFreq));
					});
				setInclude(
					() -> {
						if (sitemapInclude == null) {
							return null;
						}

						if (sitemapInclude.equals("0")) {
							return false;
						}

						if (sitemapInclude.equals("1")) {
							return true;
						}

						return null;
					});
				setIncludeChildSitePages(
					() -> {
						if (sitemapIncludeChildLayouts == null) {
							return null;
						}

						if (sitemapIncludeChildLayouts.equals("false")) {
							return false;
						}

						if (sitemapIncludeChildLayouts.equals("true")) {
							return true;
						}

						return null;
					});
				setPagePriority(
					() -> {
						if (sitemapPriority == null) {
							return null;
						}

						try {
							return Double.parseDouble(sitemapPriority);
						}
						catch (NumberFormatException numberFormatException) {
							if (_log.isWarnEnabled()) {
								_log.warn(numberFormatException);
							}

							return null;
						}
					});
			}
		};
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SEOSettingsUtil.class);

}