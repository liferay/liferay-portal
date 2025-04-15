/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.util;

import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.asset.util.AssetEntryQueryProcessor;

import jakarta.portlet.PortletPreferences;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Pavel Savinov
 */
public interface AssetPublisherCustomizer {

	public Integer getDelta(HttpServletRequest httpServletRequest);

	public Integer getDelta(PortletPreferences portletPreferences);

	public String getPortletId();

	public boolean isEnablePermissions(HttpServletRequest httpServletRequest);

	public boolean isOrderingAndGroupingEnabled(
		HttpServletRequest httpServletRequest);

	public boolean isOrderingByTitleEnabled(
		HttpServletRequest httpServletRequest);

	public boolean isSelectionStyleEnabled(
		HttpServletRequest httpServletRequest);

	public boolean isShowAssetEntryQueryProcessor(
		AssetEntryQueryProcessor assetEntryQueryProcessor);

	public boolean isShowEnableAddContentButton(
		HttpServletRequest httpServletRequest);

	public boolean isShowEnableRelatedAssets(
		HttpServletRequest httpServletRequest);

	public boolean isShowScopeSelector(HttpServletRequest httpServletRequest);

	public boolean isShowSubtypeFieldsFilter(
		HttpServletRequest httpServletRequest);

	public void setAssetEntryQueryOptions(
		AssetEntryQuery assetEntryQuery, HttpServletRequest httpServletRequest);

}