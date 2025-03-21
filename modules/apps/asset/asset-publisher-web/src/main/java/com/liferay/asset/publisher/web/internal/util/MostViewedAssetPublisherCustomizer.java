/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.util;

import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.asset.publisher.util.AssetPublisherHelper;
import com.liferay.asset.publisher.web.internal.configuration.AssetPublisherWebConfiguration;
import com.liferay.portal.kernel.util.GetterUtil;

import jakarta.portlet.PortletPreferences;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Pavel Savinov
 */
public class MostViewedAssetPublisherCustomizer
	extends DefaultAssetPublisherCustomizer {

	public MostViewedAssetPublisherCustomizer(
		AssetPublisherHelper assetPublisherHelper,
		AssetPublisherWebConfiguration assetPublisherWebConfiguration) {

		super(assetPublisherHelper, assetPublisherWebConfiguration);
	}

	@Override
	public String getPortletId() {
		return AssetPublisherPortletKeys.MOST_VIEWED_ASSETS;
	}

	@Override
	public boolean isEnablePermissions(HttpServletRequest httpServletRequest) {
		if (!assetPublisherWebConfiguration.permissionCheckingConfigurable()) {
			return true;
		}

		PortletPreferences portletPreferences = getPortletPreferences(
			httpServletRequest);

		return GetterUtil.getBoolean(
			portletPreferences.getValue("enablePermissions", null), true);
	}

	@Override
	public boolean isOrderingAndGroupingEnabled(
		HttpServletRequest httpServletRequest) {

		return false;
	}

	@Override
	public boolean isOrderingByTitleEnabled(
		HttpServletRequest httpServletRequest) {

		return assetPublisherWebConfiguration.searchWithIndex();
	}

	@Override
	public boolean isSelectionStyleEnabled(
		HttpServletRequest httpServletRequest) {

		return false;
	}

	@Override
	public boolean isShowEnableAddContentButton(
		HttpServletRequest httpServletRequest) {

		return false;
	}

	@Override
	public boolean isShowSubtypeFieldsFilter(
		HttpServletRequest httpServletRequest) {

		return assetPublisherWebConfiguration.searchWithIndex();
	}

}