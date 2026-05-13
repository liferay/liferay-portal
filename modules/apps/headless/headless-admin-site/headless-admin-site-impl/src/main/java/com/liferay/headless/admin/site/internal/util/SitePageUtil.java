/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.util;

import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutServiceUtil;

/**
 * @author Lourdes Fernández Besada
 */
public class SitePageUtil {

	public static Layout getSitePageLayout(
			long groupId, String sitePageExternalReferenceCode)
		throws Exception {

		Layout layout = LayoutServiceUtil.getLayoutByExternalReferenceCode(
			sitePageExternalReferenceCode, groupId);

		validateSitePageLayout(layout);

		return layout;
	}

	public static void validateSitePageLayout(Layout layout) {
		if (layout.isDraftLayout() || layout.isTypeAssetDisplay() ||
			layout.isTypeUtility()) {

			throw new IllegalArgumentException(
				"This page type cannot be modified through this endpoint");
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryLocalServiceUtil.
				fetchLayoutPageTemplateEntryByPlid(layout.getPlid());

		if (layoutPageTemplateEntry != null) {
			throw new IllegalArgumentException(
				"This page type cannot be modified through this endpoint");
		}
	}

}