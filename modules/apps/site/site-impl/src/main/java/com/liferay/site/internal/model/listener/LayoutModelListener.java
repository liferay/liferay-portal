/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.internal.model.listener;

import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.site.constants.SitemapConstants;

import java.util.Collections;
import java.util.Set;

import org.osgi.service.component.annotations.Component;

/**
 * @author Cheryl Tang
 */
@Component(service = ModelListener.class)
public class LayoutModelListener extends BaseSitemapModelListener<Layout> {

	@Override
	protected String getAssetTypeKey() {
		return SitemapConstants.ASSET_TYPE_KEY_PAGES;
	}

	@Override
	protected Set<String> getLayoutUuids(Layout originalLayout, Layout layout) {
		if ((originalLayout != null) &&
			((originalLayout.getParentPlid() != layout.getParentPlid()) ||
			 (_isSitemapIncludeChildLayouts(originalLayout) !=
				 _isSitemapIncludeChildLayouts(layout)))) {

			return null;
		}

		return Collections.singleton(layout.getUuid());
	}

	private boolean _isSitemapIncludeChildLayouts(Layout layout) {
		UnicodeProperties typeSettingsUnicodeProperties =
			layout.getTypeSettingsProperties();

		return GetterUtil.getBoolean(
			typeSettingsUnicodeProperties.getProperty(
				"sitemap-include-child-layouts"),
			true);
	}

}