/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.taglib.internal.display.context;

import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagServiceUtil;
import com.liferay.asset.util.comparator.AssetTagCountComparator;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mikel Lorza
 */
public class AssetTagsNavigationDisplayContext {

	public AssetTagsNavigationDisplayContext(
		HttpServletRequest httpServletRequest, RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderResponse = renderResponse;

		_classNameId = GetterUtil.getLong(
			httpServletRequest.getAttribute(
				"liferay-asset:asset-tags-navigation:classNameId"));
		_displayStyle = GetterUtil.getString(
			httpServletRequest.getAttribute(
				"liferay-asset:asset-tags-navigation:displayStyle"));
		_hidePortletWhenEmpty = GetterUtil.getBoolean(
			httpServletRequest.getAttribute(
				"liferay-asset:asset-tags-navigation:hidePortletWhenEmpty"));
		_maxAssetTags = GetterUtil.getInteger(
			httpServletRequest.getAttribute(
				"liferay-asset:asset-tags-navigation:maxAssetTags"));
		_showAssetCount = GetterUtil.getBoolean(
			httpServletRequest.getAttribute(
				"liferay-asset:asset-tags-navigation:showAssetCount"));
		_showZeroAssetCount = GetterUtil.getBoolean(
			httpServletRequest.getAttribute(
				"liferay-asset:asset-tags-navigation:showZeroAssetCount"));

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (_showBreadcrumb()) {
			PortalUtil.addPortletBreadcrumbEntry(
				httpServletRequest, _getTag(), _themeDisplay.getURLCurrent(),
				null, false);
		}
	}

	public Map<String, Object> getData() {
		List<AssetTag> assetTags = _getAssetTags();

		if (assetTags.isEmpty()) {
			return Collections.emptyMap();
		}

		boolean displayStyleCloud = _displayStyle.equals("cloud");

		return HashMapBuilder.<String, Object>put(
			"assetTags",
			() -> {
				if (displayStyleCloud) {
					_calculateCounts(assetTags);
				}

				double multiplier = _getMultiplier();

				JSONArray assetTagsJSONArray =
					JSONFactoryUtil.createJSONArray();

				for (AssetTag assetTag : assetTags) {
					String tagName = assetTag.getName();

					int count = _getVisibleAssetsTagsCount(tagName);

					if (!_showZeroAssetCount && (count == 0)) {
						continue;
					}

					boolean selected = StringUtil.equals(tagName, _getTag());

					assetTagsJSONArray.put(
						JSONUtil.put(
							"count",
							() -> {
								if (!isShowAssetCount()) {
									return null;
								}

								return count;
							}
						).put(
							"popularity",
							() -> {
								if (!displayStyleCloud) {
									return 1;
								}

								return _getPopularity(multiplier, count);
							}
						).put(
							"selected", selected
						).put(
							"tagName", () -> tagName
						).put(
							"tagURL", () -> _getTagURL(selected, tagName)
						));
				}

				return assetTagsJSONArray;
			}
		).put(
			"displayStyle",
			() -> {
				if (displayStyleCloud) {
					return "tag-cloud";
				}

				return "tag-list";
			}
		).build();
	}

	public String getTagSelectedCssClass(JSONObject jsonObject) {
		if (jsonObject.getBoolean("selected")) {
			return "tag-selected";
		}

		return StringPool.BLANK;
	}

	public boolean isHidePortletWhenEmpty() {
		return _hidePortletWhenEmpty;
	}

	public boolean isShowAssetCount() {
		return _showAssetCount;
	}

	private void _calculateCounts(List<AssetTag> assetTags) {
		for (AssetTag assetTag : assetTags) {
			int count = _getVisibleAssetsTagsCount(assetTag.getName());

			if (!_showZeroAssetCount && (count == 0)) {
				continue;
			}

			_maxCount = Math.max(_maxCount, count);
			_minCount = Math.min(_minCount, count);
		}
	}

	private List<AssetTag> _getAssetTags() {
		List<AssetTag> assetTags;

		if (isShowAssetCount() && (_classNameId > 0)) {
			assetTags = AssetTagServiceUtil.getTags(
				PortalUtil.getSiteGroupId(_themeDisplay.getScopeGroupId()),
				_classNameId, null, 0, _maxAssetTags,
				AssetTagCountComparator.getInstance(false));
		}
		else {
			assetTags = AssetTagServiceUtil.getGroupTags(
				PortalUtil.getSiteGroupId(_themeDisplay.getScopeGroupId()), 0,
				_maxAssetTags, AssetTagCountComparator.getInstance(false));
		}

		return assetTags;
	}

	private double _getMultiplier() {
		if (_maxCount != _minCount) {
			return (double)5 / (_maxCount - _minCount);
		}

		return 1;
	}

	private int _getPopularity(double multiplier, int count) {
		return GetterUtil.getInteger(
			1 + ((_maxCount - (_maxCount - (count - _minCount))) * multiplier));
	}

	private String _getTag() {
		if (_tag == null) {
			_tag = ParamUtil.getString(_httpServletRequest, "tag");
		}

		return _tag;
	}

	private String _getTagURL(boolean selected, String tagName) {
		return HtmlUtil.escape(
			PortletURLBuilder.createRenderURL(
				_renderResponse
			).setParameter(
				"tag",
				() -> {
					if (selected) {
						return StringPool.BLANK;
					}

					return tagName;
				}
			).buildString());
	}

	private Integer _getVisibleAssetsTagsCount(String tagName) {
		if (_assetTagCounts.containsKey(tagName)) {
			return _assetTagCounts.get(tagName);
		}

		int count = AssetTagServiceUtil.getVisibleAssetsTagsCount(
			_themeDisplay.getScopeGroupId(), _classNameId, tagName);

		_assetTagCounts.put(tagName, count);

		return count;
	}

	private boolean _showBreadcrumb() {
		return Validator.isNotNull(_getTag());
	}

	private final Map<String, Integer> _assetTagCounts = new HashMap<>();
	private final long _classNameId;
	private final String _displayStyle;
	private final boolean _hidePortletWhenEmpty;
	private final HttpServletRequest _httpServletRequest;
	private final int _maxAssetTags;
	private int _maxCount = 1;
	private int _minCount = 1;
	private final RenderResponse _renderResponse;
	private final boolean _showAssetCount;
	private final boolean _showZeroAssetCount;
	private String _tag;
	private final ThemeDisplay _themeDisplay;

}