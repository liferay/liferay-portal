/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.seo.internal.util;

import com.liferay.layout.seo.model.LayoutSEOEntry;
import com.liferay.layout.seo.service.LayoutSEOEntryLocalService;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.impl.VirtualLayout;

/**
 * @author Javier Moral
 */
public class VirtualLayoutSEOEntryUtil {

	/**
	 * Returns the entry describing the visited page, or <code>null</code> when
	 * the layout is a display page template shared from a Design Library. A
	 * canonical URL is an address, so a shared template's address never applies
	 * to a consuming site. The template's own entry is still read for Open
	 * Graph tags, whose values are authored on the template and resolve against
	 * the mapped asset.
	 */
	public static LayoutSEOEntry fetchLayoutSEOEntry(
		Layout layout, LayoutSEOEntryLocalService layoutSEOEntryLocalService) {

		if (layout instanceof VirtualLayout) {
			VirtualLayout virtualLayout = (VirtualLayout)layout;

			if (virtualLayout.isSourceGroupDepot()) {
				return null;
			}
		}

		return layoutSEOEntryLocalService.fetchLayoutSEOEntry(
			layout.getGroupId(), layout.isPrivateLayout(),
			layout.getLayoutId());
	}

}