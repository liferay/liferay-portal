/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.util;

import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.asset.publisher.util.AssetPublisherHelper;
import com.liferay.asset.publisher.web.internal.configuration.AssetPublisherWebConfiguration;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.util.PropsValues;

import jakarta.portlet.PortletPreferences;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Pavel Savinov
 */
public class RecentContentAssetPublisherCustomizer
	extends DefaultAssetPublisherCustomizer {

	public RecentContentAssetPublisherCustomizer(
		AssetPublisherHelper assetPublisherHelper,
		AssetPublisherWebConfiguration assetPublisherWebConfiguration) {

		super(assetPublisherHelper, assetPublisherWebConfiguration);
	}

	@Override
	public Integer getDelta(HttpServletRequest httpServletRequest) {
		PortletPreferences portletPreferences = getPortletPreferences(
			httpServletRequest);

		return GetterUtil.getInteger(
			portletPreferences.getValue("delta", null),
			PropsValues.RECENT_CONTENT_MAX_DISPLAY_ITEMS);
	}

	@Override
	public String getPortletId() {
		return AssetPublisherPortletKeys.RECENT_CONTENT;
	}

	@Override
	public boolean isEnablePermissions(HttpServletRequest httpServletRequest) {
		return true;
	}

	@Override
	public boolean isOrderingAndGroupingEnabled(
		HttpServletRequest httpServletRequest) {

		return true;
	}

	@Override
	public boolean isOrderingByTitleEnabled(
		HttpServletRequest httpServletRequest) {

		return assetPublisherWebConfiguration.searchWithIndex();
	}

	@Override
	public boolean isShowSubtypeFieldsFilter(
		HttpServletRequest httpServletRequest) {

		return true;
	}

}