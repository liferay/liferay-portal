/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.tags.navigation.web.internal.display.context;

import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagServiceUtil;
import com.liferay.asset.util.comparator.AssetTagCountComparator;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PrefsParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Vendel Toreki
 */
public class AssetTagsNavigationDisplayContext {

	public AssetTagsNavigationDisplayContext(RenderRequest renderRequest) {
		_renderRequest = renderRequest;

		_classNameId = PrefsParamUtil.getLong(
			renderRequest.getPreferences(), renderRequest, "classNameId");

		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_scopeGroupId = _themeDisplay.getScopeGroupId();
	}

	public List<AssetTag> getAssetTags() {
		if (_assetTags != null) {
			return _assetTags;
		}

		int maxAssetTags = PrefsParamUtil.getInteger(
			_renderRequest.getPreferences(), _renderRequest, "maxAssetTags",
			10);
		boolean showAssetCount = PrefsParamUtil.getBoolean(
			_renderRequest.getPreferences(), _renderRequest, "showAssetCount");

		if (showAssetCount && (_classNameId > 0)) {
			_assetTags = AssetTagServiceUtil.getTags(
				PortalUtil.getSiteGroupId(_scopeGroupId), _classNameId, null, 0,
				maxAssetTags, AssetTagCountComparator.getInstance(false));
		}
		else {
			_assetTags = AssetTagServiceUtil.getGroupTags(
				PortalUtil.getSiteGroupId(_scopeGroupId), 0, maxAssetTags,
				AssetTagCountComparator.getInstance(false));
		}

		_assetTags = ListUtil.sort(_assetTags);

		return _assetTags;
	}

	public Map<String, Integer> getScopedAssetCounts() {
		if (_scopedAssetCounts != null) {
			return _scopedAssetCounts;
		}

		_scopedAssetCounts = new HashMap<>();

		for (AssetTag assetTag : getAssetTags()) {
			_scopedAssetCounts.put(
				assetTag.getName(),
				AssetTagServiceUtil.getVisibleAssetsTagsCount(
					_scopeGroupId, _classNameId, assetTag.getName()));
		}

		return _scopedAssetCounts;
	}

	private List<AssetTag> _assetTags;
	private final long _classNameId;
	private final RenderRequest _renderRequest;
	private Map<String, Integer> _scopedAssetCounts;
	private final long _scopeGroupId;
	private final ThemeDisplay _themeDisplay;

}