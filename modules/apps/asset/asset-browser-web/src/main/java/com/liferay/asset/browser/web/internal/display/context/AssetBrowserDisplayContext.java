/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.browser.web.internal.display.context;

import com.liferay.asset.browser.web.internal.constants.AssetBrowserPortletKeys;
import com.liferay.asset.browser.web.internal.search.AddAssetEntryChecker;
import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.util.AssetHelper;
import com.liferay.depot.util.SiteConnectedGroupGroupProviderUtil;
import com.liferay.item.selector.criteria.asset.criterion.AssetEntryItemSelectorCriterion;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class AssetBrowserDisplayContext {

	public AssetBrowserDisplayContext(
		AssetEntryLocalService assetEntryLocalService, AssetHelper assetHelper,
		AssetEntryItemSelectorCriterion assetEntryItemSelectorCriterion,
		HttpServletRequest httpServletRequest, Portal portal,
		PortletURL portletURL, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_assetEntryLocalService = assetEntryLocalService;
		_assetHelper = assetHelper;
		_assetEntryItemSelectorCriterion = assetEntryItemSelectorCriterion;
		_httpServletRequest = httpServletRequest;
		_portal = portal;
		_portletURL = portletURL;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public SearchContainer<AssetEntry> getAssetEntrySearchContainer()
		throws PortalException {

		if (_assetEntrySearchContainer != null) {
			return _assetEntrySearchContainer;
		}

		SearchContainer<AssetEntry> assetEntrySearchContainer =
			new SearchContainer<>(_renderRequest, _portletURL, null, null);

		assetEntrySearchContainer.setOrderByCol(getOrderByCol());
		assetEntrySearchContainer.setOrderByType(getOrderByType());

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Sort sort = null;

		boolean orderByAsc = false;

		if (Objects.equals(getOrderByType(), "asc")) {
			orderByAsc = true;
		}

		if (Objects.equals(getOrderByCol(), "modified-date")) {
			sort = new Sort(Field.MODIFIED_DATE, Sort.LONG_TYPE, !orderByAsc);
		}
		else if (Objects.equals(getOrderByCol(), "title")) {
			sort = new Sort(
				Field.getSortableFieldName(
					"localized_title_".concat(themeDisplay.getLanguageId())),
				Sort.STRING_TYPE, !orderByAsc);
		}

		Hits hits = _assetEntryLocalService.search(
			themeDisplay.getCompanyId(), _getFilterGroupIds(),
			themeDisplay.getUserId(), _getClassNameIds(),
			getSubtypeSelectionId(), _getKeywords(),
			_assetEntryItemSelectorCriterion.isShowNonindexable(),
			_getStatuses(), assetEntrySearchContainer.getStart(),
			assetEntrySearchContainer.getEnd(), sort);

		assetEntrySearchContainer.setResultsAndTotal(
			() -> _assetHelper.getAssetEntries(hits), hits.getLength());

		if (isMultipleSelection()) {
			assetEntrySearchContainer.setRowChecker(
				new AddAssetEntryChecker(
					_renderResponse, getRefererAssetEntryId()));
		}

		_assetEntrySearchContainer = assetEntrySearchContainer;

		return _assetEntrySearchContainer;
	}

	public AssetRendererFactory<?> getAssetRendererFactory() {
		if (_assetRendererFactory != null) {
			return _assetRendererFactory;
		}

		_assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				getTypeSelection());

		return _assetRendererFactory;
	}

	public long getGroupId() {
		if (_assetEntryItemSelectorCriterion.getGroupId() ==
				_themeDisplay.getRefererGroupId()) {

			return _themeDisplay.getScopeGroupId();
		}

		return _assetEntryItemSelectorCriterion.getGroupId();
	}

	public long getRefererAssetEntryId() {
		if (_refererAssetEntryId != null) {
			return _refererAssetEntryId;
		}

		_refererAssetEntryId = ParamUtil.getLong(
			_httpServletRequest, "refererAssetEntryId");

		return _refererAssetEntryId;
	}

	public long[] getSelectedGroupIds() {
		long[] selectedGroupIds =
			_assetEntryItemSelectorCriterion.getSelectedGroupIds();

		if (ArrayUtil.isNotEmpty(selectedGroupIds)) {
			return selectedGroupIds;
		}

		try {
			return _portal.getSharedContentSiteGroupIds(
				_themeDisplay.getCompanyId(),
				ParamUtil.getLong(_httpServletRequest, "selectedGroupId"),
				_themeDisplay.getUserId());
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return new long[0];
	}

	public long getSubtypeSelectionId() {
		return _assetEntryItemSelectorCriterion.getSubtypeSelectionId();
	}

	public String getTypeSelection() {
		return _assetEntryItemSelectorCriterion.getTypeSelection();
	}

	public boolean isMultipleSelection() {
		return !_assetEntryItemSelectorCriterion.isSingleSelect();
	}

	public boolean isSearchEverywhere() {
		if (_searchEverywhere != null) {
			return _searchEverywhere;
		}

		_searchEverywhere = Objects.equals(
			ParamUtil.getString(_httpServletRequest, "scope"), "everywhere");

		return _searchEverywhere;
	}

	public boolean isShowAssetEntryStatus() {
		if (_assetEntryItemSelectorCriterion.isShowNonindexable() ||
			_assetEntryItemSelectorCriterion.isShowScheduled()) {

			return true;
		}

		return false;
	}

	protected String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_httpServletRequest, AssetBrowserPortletKeys.ASSET_BROWSER,
			"modified-date");

		return _orderByCol;
	}

	protected String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest, AssetBrowserPortletKeys.ASSET_BROWSER, "asc");

		return _orderByType;
	}

	private long[] _getClassNameIds() {
		if (_classNameIds != null) {
			return _classNameIds;
		}

		AssetRendererFactory<?> assetRendererFactory =
			getAssetRendererFactory();

		if (assetRendererFactory != null) {
			_classNameIds = new long[] {assetRendererFactory.getClassNameId()};
		}

		return _classNameIds;
	}

	private long[] _getFilterGroupIds() throws PortalException {
		if (_filterGroupIds != null) {
			return _filterGroupIds;
		}

		if (getGroupId() == 0) {
			_filterGroupIds = getSelectedGroupIds();
		}
		else if (!isSearchEverywhere()) {
			_filterGroupIds = new long[] {getGroupId()};
		}
		else {
			_filterGroupIds =
				SiteConnectedGroupGroupProviderUtil.
					getCurrentAndAncestorSiteAndDepotGroupIds(getGroupId());
		}

		return _filterGroupIds;
	}

	private String _getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		return _keywords;
	}

	private int[] _getStatuses() {
		int[] statuses = {WorkflowConstants.STATUS_APPROVED};

		if (_assetEntryItemSelectorCriterion.isShowScheduled()) {
			statuses = new int[] {
				WorkflowConstants.STATUS_APPROVED,
				WorkflowConstants.STATUS_SCHEDULED
			};
		}

		return statuses;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetBrowserDisplayContext.class);

	private final AssetEntryItemSelectorCriterion
		_assetEntryItemSelectorCriterion;
	private final AssetEntryLocalService _assetEntryLocalService;
	private SearchContainer<AssetEntry> _assetEntrySearchContainer;
	private final AssetHelper _assetHelper;
	private AssetRendererFactory<?> _assetRendererFactory;
	private long[] _classNameIds;
	private long[] _filterGroupIds;
	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private String _orderByCol;
	private String _orderByType;
	private final Portal _portal;
	private final PortletURL _portletURL;
	private Long _refererAssetEntryId;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private Boolean _searchEverywhere;
	private final ThemeDisplay _themeDisplay;

}