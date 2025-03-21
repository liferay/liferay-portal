/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.web.internal.display.context;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.list.asset.entry.provider.AssetListAssetEntryProvider;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryLocalServiceUtil;
import com.liferay.info.pagination.InfoPage;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.constants.SegmentsEntryConstants;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Jürgen Kappler
 */
public class AssetListItemsDisplayContext {

	public AssetListItemsDisplayContext(
		AssetListAssetEntryProvider assetListAssetEntryProvider,
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_assetListAssetEntryProvider = assetListAssetEntryProvider;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);

		_themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public SearchContainer<AssetEntry> getAssetListContentSearchContainer() {
		if (_assetListContentSearchContainer != null) {
			return _assetListContentSearchContainer;
		}

		SearchContainer<AssetEntry> searchContainer = new SearchContainer(
			_renderRequest, _getAssetListContentURL(), null,
			"there-are-no-asset-entries");

		InfoPage<AssetEntry> infoPage =
			_assetListAssetEntryProvider.getAssetEntriesInfoPage(
				getAssetListEntry(), new long[] {getSegmentsEntryId()}, null,
				null, StringPool.BLANK, StringPool.BLANK,
				searchContainer.getStart(), searchContainer.getEnd());

		searchContainer.setResultsAndTotal(
			() -> (List<AssetEntry>)infoPage.getPageItems(),
			infoPage.getTotalCount());

		_assetListContentSearchContainer = searchContainer;

		return _assetListContentSearchContainer;
	}

	public AssetListEntry getAssetListEntry() {
		if (_assetListEntry != null) {
			return _assetListEntry;
		}

		_assetListEntry = AssetListEntryLocalServiceUtil.fetchAssetListEntry(
			getAssetListEntryId());

		return _assetListEntry;
	}

	public long getAssetListEntryId() {
		if (_assetListEntryId != null) {
			return _assetListEntryId;
		}

		long assetListEntryId = ParamUtil.getLong(
			_httpServletRequest, "assetListEntryId");

		if (assetListEntryId <= 0) {
			assetListEntryId = ParamUtil.getLong(
				_httpServletRequest, "collectionPK");
		}

		_assetListEntryId = assetListEntryId;

		return _assetListEntryId;
	}

	public long getSegmentsEntryId() {
		if (_segmentsEntryId != null) {
			return _segmentsEntryId;
		}

		_segmentsEntryId = ParamUtil.getLong(
			_httpServletRequest, "segmentsEntryId",
			SegmentsEntryConstants.ID_DEFAULT);

		return _segmentsEntryId;
	}

	public boolean isShowActions() {
		if (_showActions != null) {
			return _showActions;
		}

		_showActions = ParamUtil.get(_renderRequest, "showActions", false);

		return _showActions;
	}

	private PortletURL _getAssetListContentURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCPath(
			"/view_asset_list_items.jsp"
		).setRedirect(
			_getRedirect()
		).setParameter(
			"assetListEntryId", getAssetListEntryId()
		).setParameter(
			"segmentsEntryId", getSegmentsEntryId()
		).buildPortletURL();
	}

	private String _getRedirect() {
		return ParamUtil.get(
			_renderRequest, "redirect", _themeDisplay.getURLCurrent());
	}

	private final AssetListAssetEntryProvider _assetListAssetEntryProvider;
	private SearchContainer<AssetEntry> _assetListContentSearchContainer;
	private AssetListEntry _assetListEntry;
	private Long _assetListEntryId;
	private final HttpServletRequest _httpServletRequest;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private Long _segmentsEntryId;
	private Boolean _showActions;
	private final ThemeDisplay _themeDisplay;

}