/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.util;

import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.asset.publisher.util.AssetPublisherHelper;
import com.liferay.asset.publisher.web.internal.configuration.AssetPublisherWebConfiguration;
import com.liferay.asset.util.AssetEntryQueryProcessor;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Pavel Savinov
 */
public class DefaultAssetPublisherCustomizer
	implements AssetPublisherCustomizer {

	public DefaultAssetPublisherCustomizer(
		AssetPublisherHelper assetPublisherHelper,
		AssetPublisherWebConfiguration assetPublisherWebConfiguration) {

		_assetPublisherHelper = assetPublisherHelper;
		this.assetPublisherWebConfiguration = assetPublisherWebConfiguration;
	}

	@Override
	public Integer getDelta(HttpServletRequest httpServletRequest) {
		PortletPreferences portletPreferences = getPortletPreferences(
			httpServletRequest);

		return GetterUtil.getInteger(
			portletPreferences.getValue("delta", null),
			SearchContainer.DEFAULT_DELTA);
	}

	@Override
	public Integer getDelta(PortletPreferences portletPreferences) {
		return GetterUtil.getInteger(
			portletPreferences.getValue("delta", null),
			SearchContainer.DEFAULT_DELTA);
	}

	@Override
	public String getPortletId() {
		return AssetPublisherPortletKeys.ASSET_PUBLISHER;
	}

	@Override
	public boolean isEnablePermissions(HttpServletRequest httpServletRequest) {
		if (assetPublisherWebConfiguration.searchWithIndex() ||
			!assetPublisherWebConfiguration.permissionCheckingConfigurable()) {

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

		return true;
	}

	@Override
	public boolean isOrderingByTitleEnabled(
		HttpServletRequest httpServletRequest) {

		return assetPublisherWebConfiguration.searchWithIndex();
	}

	@Override
	public boolean isSelectionStyleEnabled(
		HttpServletRequest httpServletRequest) {

		return true;
	}

	@Override
	public boolean isShowAssetEntryQueryProcessor(
		AssetEntryQueryProcessor assetEntryQueryProcessor) {

		return true;
	}

	@Override
	public boolean isShowEnableAddContentButton(
		HttpServletRequest httpServletRequest) {

		return true;
	}

	@Override
	public boolean isShowEnableRelatedAssets(
		HttpServletRequest httpServletRequest) {

		return true;
	}

	@Override
	public boolean isShowScopeSelector(HttpServletRequest httpServletRequest) {
		return true;
	}

	@Override
	public boolean isShowSubtypeFieldsFilter(
		HttpServletRequest httpServletRequest) {

		return assetPublisherWebConfiguration.searchWithIndex();
	}

	@Override
	public void setAssetEntryQueryOptions(
		AssetEntryQuery assetEntryQuery,
		HttpServletRequest httpServletRequest) {

		if (ArrayUtil.isNotEmpty(assetEntryQuery.getGroupIds())) {
			return;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		assetEntryQuery.setGroupIds(
			_assetPublisherHelper.getGroupIds(
				getPortletPreferences(httpServletRequest),
				themeDisplay.getScopeGroupId(), themeDisplay.getLayout()));
	}

	protected PortletPreferences getPortletPreferences(
		HttpServletRequest httpServletRequest) {

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);

		if (portletRequest != null) {
			return portletRequest.getPreferences();
		}

		return null;
	}

	protected final AssetPublisherWebConfiguration
		assetPublisherWebConfiguration;

	private final AssetPublisherHelper _assetPublisherHelper;

}