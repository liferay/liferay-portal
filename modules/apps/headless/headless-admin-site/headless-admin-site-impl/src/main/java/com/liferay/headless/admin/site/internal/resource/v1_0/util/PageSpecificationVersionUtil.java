/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0.util;

import com.liferay.headless.admin.site.internal.util.SitePageUtil;
import com.liferay.headless.common.spi.util.GroupUtil;
import com.liferay.layout.content.model.LayoutContentVersion;
import com.liferay.layout.content.service.LayoutContentVersionService;
import com.liferay.portal.kernel.model.Layout;

/**
 * @author Lourdes Fernández Besada
 */
public class PageSpecificationVersionUtil {

	public static Layout getLayout(
			long companyId, String siteExternalReferenceCode,
			String sitePageExternalReferenceCode)
		throws Exception {

		Layout layout = SitePageUtil.getSitePageLayout(
			GroupUtil.getGroupId(
				false, false, companyId, siteExternalReferenceCode),
			sitePageExternalReferenceCode);

		if (!layout.isTypeContent()) {
			throw new IllegalArgumentException(
				"The page must be a content page");
		}

		return layout;
	}

	public static LayoutContentVersion getLayoutContentVersion(
			long companyId, String externalReferenceCode, Layout layout,
			LayoutContentVersionService layoutContentVersionService,
			String siteExternalReferenceCode)
		throws Exception {

		LayoutContentVersion layoutContentVersion =
			layoutContentVersionService.
				getLayoutContentVersionByExternalReferenceCode(
					externalReferenceCode,
					GroupUtil.getStagingAwareGroupId(
						companyId, siteExternalReferenceCode));

		if (layoutContentVersion.getPlid() != layout.getPlid()) {
			throw new IllegalArgumentException(
				"The page specification version must belong to the site page");
		}

		return layoutContentVersion;
	}

}