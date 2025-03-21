/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.tags.item.selector.web.internal.display.context;

import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagService;
import com.liferay.asset.tags.item.selector.AssetTagsItemSelectorCriterion;
import com.liferay.asset.tags.item.selector.web.internal.search.EntriesChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.asset.util.comparator.AssetTagNameComparator;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Stefan Tanasie
 */
public class AssetTagsDisplayContext {

	public AssetTagsDisplayContext(
		AssetTagsItemSelectorCriterion assetTagsItemSelectorCriterion,
		AssetTagService assetTagService, GroupLocalService groupLocalService,
		HttpServletRequest httpServletRequest, PortletURL portletURL,
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_assetTagsItemSelectorCriterion = assetTagsItemSelectorCriterion;
		_assetTagService = assetTagService;
		_groupLocalService = groupLocalService;
		_httpServletRequest = httpServletRequest;
		_portletURL = portletURL;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
	}

	public SearchContainer<AssetTag> getTagSearchContainer() {
		if (_tagsSearchContainer != null) {
			return _tagsSearchContainer;
		}

		SearchContainer<AssetTag> tagsSearchContainer = new SearchContainer<>(
			_renderRequest, _portletURL, null, "there-are-no-tags");

		tagsSearchContainer.setOrderByCol("name");

		boolean orderByAsc = false;

		String orderByType = _getOrderByType();

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		tagsSearchContainer.setOrderByComparator(
			new AssetTagNameComparator(orderByAsc));
		tagsSearchContainer.setOrderByType(orderByType);

		tagsSearchContainer.setResultsAndTotal(
			() -> _assetTagService.getTags(
				_getGroupIds(), _getKeywords(), tagsSearchContainer.getStart(),
				tagsSearchContainer.getEnd(),
				tagsSearchContainer.getOrderByComparator()),
			_assetTagService.getTagsCount(_getGroupIds(), _getKeywords()));

		if (_assetTagsItemSelectorCriterion.isMultiSelection()) {
			String[] selectedTagNames = StringUtil.split(
				ParamUtil.getString(_renderRequest, "selectedTagNames"));

			tagsSearchContainer.setRowChecker(
				new EntriesChecker(_renderResponse, selectedTagNames));
		}

		_tagsSearchContainer = tagsSearchContainer;

		return _tagsSearchContainer;
	}

	private long[] _getGroupIds() {
		if (_groupIds != null) {
			return _groupIds;
		}

		if (_assetTagsItemSelectorCriterion.isAllGroupIds()) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)_httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			_groupIds = ArrayUtil.toLongArray(
				_groupLocalService.getGroupIds(
					themeDisplay.getCompanyId(), true));
		}
		else {
			_groupIds = _assetTagsItemSelectorCriterion.getGroupIds();
		}

		return _groupIds;
	}

	private String _getKeywords() {
		if (Validator.isNotNull(_keywords)) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_httpServletRequest, "keywords", null);

		return _keywords;
	}

	private String _getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = ParamUtil.getString(
			_httpServletRequest, "orderByType", "asc");

		return _orderByType;
	}

	private final AssetTagService _assetTagService;
	private final AssetTagsItemSelectorCriterion
		_assetTagsItemSelectorCriterion;
	private long[] _groupIds;
	private final GroupLocalService _groupLocalService;
	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private String _orderByType;
	private final PortletURL _portletURL;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private SearchContainer<AssetTag> _tagsSearchContainer;

}