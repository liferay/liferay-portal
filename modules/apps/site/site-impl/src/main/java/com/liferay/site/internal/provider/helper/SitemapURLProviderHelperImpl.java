/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.internal.provider.helper;

import com.liferay.layout.admin.kernel.model.LayoutTypePortletConstants;
import com.liferay.layout.seo.model.LayoutSEOEntry;
import com.liferay.layout.seo.service.LayoutSEOEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.impl.VirtualLayout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.site.provider.helper.SitemapURLProviderHelper;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(service = SitemapURLProviderHelper.class)
public class SitemapURLProviderHelperImpl implements SitemapURLProviderHelper {

	@Override
	public boolean isExcludeLayoutFromSitemap(Layout layout) {
		if (layout == null) {
			return true;
		}

		UnicodeProperties typeSettingsUnicodeProperties =
			layout.getTypeSettingsProperties();

		if (!GetterUtil.getBoolean(
				typeSettingsUnicodeProperties.getProperty(
					LayoutTypePortletConstants.SITEMAP_INCLUDE),
				true) ||
			!layout.isPublished()) {

			return true;
		}

		LayoutSEOEntry layoutSEOEntry = _fetchLayoutSEOEntry(layout);

		if ((layoutSEOEntry != null) &&
			layoutSEOEntry.isCanonicalURLEnabled()) {

			return true;
		}

		Map<Locale, String> robotsMap = layout.getRobotsMap();

		for (Map.Entry<Locale, String> entry : robotsMap.entrySet()) {
			String value = entry.getValue();

			if (StringUtil.containsIgnoreCase(
					value, "nofollow", StringPool.BLANK) ||
				StringUtil.containsIgnoreCase(
					value, "noindex", StringPool.BLANK)) {

				return true;
			}
		}

		long parentPlid = layout.getParentPlid();

		while (parentPlid > 0) {
			Layout parentLayout = _layoutLocalService.fetchLayout(parentPlid);

			if (parentLayout == null) {
				break;
			}

			typeSettingsUnicodeProperties =
				parentLayout.getTypeSettingsProperties();

			if (!GetterUtil.getBoolean(
					typeSettingsUnicodeProperties.getProperty(
						"sitemap-include-child-layouts"),
					true)) {

				return true;
			}

			parentPlid = parentLayout.getParentPlid();
		}

		return false;
	}

	private LayoutSEOEntry _fetchLayoutSEOEntry(Layout layout) {
		if (layout instanceof VirtualLayout) {
			VirtualLayout virtualLayout = (VirtualLayout)layout;

			if (virtualLayout.isSourceGroupDepot()) {
				return null;
			}
		}

		return _layoutSEOEntryLocalService.fetchLayoutSEOEntry(
			layout.getGroupId(), layout.isPrivateLayout(),
			layout.getLayoutId());
	}

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutSEOEntryLocalService _layoutSEOEntryLocalService;

}