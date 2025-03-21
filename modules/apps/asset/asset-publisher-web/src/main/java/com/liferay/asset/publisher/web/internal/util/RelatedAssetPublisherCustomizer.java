/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.util;

import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.asset.publisher.util.AssetPublisherHelper;
import com.liferay.asset.publisher.web.internal.configuration.AssetPublisherWebConfiguration;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Set;

/**
 * @author Pavel Savinov
 */
public class RelatedAssetPublisherCustomizer
	extends DefaultAssetPublisherCustomizer {

	public RelatedAssetPublisherCustomizer(
		AssetPublisherHelper assetPublisherHelper,
		AssetPublisherWebConfiguration assetPublisherWebConfiguration) {

		super(assetPublisherHelper, assetPublisherWebConfiguration);
	}

	@Override
	public String getPortletId() {
		return AssetPublisherPortletKeys.RELATED_ASSETS;
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

		return false;
	}

	@Override
	public boolean isSelectionStyleEnabled(
		HttpServletRequest httpServletRequest) {

		return false;
	}

	@Override
	public boolean isShowEnableRelatedAssets(
		HttpServletRequest httpServletRequest) {

		return false;
	}

	@Override
	public boolean isShowScopeSelector(HttpServletRequest httpServletRequest) {
		return false;
	}

	@Override
	public boolean isShowSubtypeFieldsFilter(
		HttpServletRequest httpServletRequest) {

		return false;
	}

	@Override
	public void setAssetEntryQueryOptions(
		AssetEntryQuery assetEntryQuery,
		HttpServletRequest httpServletRequest) {

		Set<Long> linkedAssetEntryIds =
			(Set<Long>)httpServletRequest.getAttribute(
				WebKeys.LINKED_ASSET_ENTRY_IDS);

		if (SetUtil.isNotEmpty(linkedAssetEntryIds)) {
			assetEntryQuery.setLinkedAssetEntryIds(
				ArrayUtil.toLongArray(linkedAssetEntryIds));
		}
	}

}