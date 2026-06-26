/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.internal.model.listener;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.site.constants.SitemapConstants;

import org.osgi.service.component.annotations.Component;

/**
 * @author Cheryl Tang
 */
@Component(service = ModelListener.class)
public class AssetCategoryModelListener
	extends BaseSitemapModelListener<AssetCategory> {

	@Override
	protected String getAssetTypeKey() {
		return SitemapConstants.ASSET_TYPE_KEY_CATEGORIES;
	}

}