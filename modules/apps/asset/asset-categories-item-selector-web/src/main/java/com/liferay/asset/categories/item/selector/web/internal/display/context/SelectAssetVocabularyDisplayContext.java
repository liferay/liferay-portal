/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.item.selector.web.internal.display.context;

import com.liferay.asset.categories.item.selector.web.internal.constants.AssetCategoryTreeNodeConstants;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.kernel.service.AssetVocabularyServiceUtil;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryServiceUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbEntryListBuilder;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rubén Pulido
 */
public class SelectAssetVocabularyDisplayContext {

	public SelectAssetVocabularyDisplayContext(
		HttpServletRequest httpServletRequest, PortletURL portletURL) {

		_httpServletRequest = httpServletRequest;
		_portletURL = portletURL;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public long getAssetCategoryTreeNodeId() {
		if (_assetCategoryTreeNodeId != null) {
			return _assetCategoryTreeNodeId;
		}

		_assetCategoryTreeNodeId = ParamUtil.getLong(
			_httpServletRequest, "assetCategoryTreeNodeId", -1);

		return _assetCategoryTreeNodeId;
	}

	public SearchContainer<AssetVocabulary> getAssetVocabularySearchContainer()
		throws PortalException {

		SearchContainer<AssetVocabulary> searchContainer =
			new SearchContainer<>(
				_getPortletRequest(), _portletURL, null,
				"there-are-no-items-to-display");

		List<AssetVocabulary> assetVocabularies = _getAssetVocabularies();

		searchContainer.setResultsAndTotal(
			() -> assetVocabularies, assetVocabularies.size());

		return searchContainer;
	}

	public String getAssetVocabularyTitle(AssetVocabulary assetVocabulary) {
		if (assetVocabulary == null) {
			return null;
		}

		return assetVocabulary.getTitle(_themeDisplay.getLocale());
	}

	public String getAssetVocabularyURL(long assetVocabularyId)
		throws PortletException {

		return _getAssetVocabularyURL(assetVocabularyId);
	}

	public List<BreadcrumbEntry> getBreadcrumbEntries() {
		return BreadcrumbEntryListBuilder.add(
			breadcrumbEntry -> {
				String backURL = ParamUtil.getString(
					_httpServletRequest, "backURL",
					PortalUtil.getCurrentURL(_httpServletRequest));

				breadcrumbEntry.setBrowsable(backURL != null);

				breadcrumbEntry.setTitle(
					LanguageUtil.get(
						_themeDisplay.getLocale(), "vocabularies"));
				breadcrumbEntry.setURL(backURL);
			}
		).build();
	}

	private List<AssetVocabulary> _getAssetVocabularies()
		throws PortalException {

		List<Long> groupIds = new ArrayList<>();

		groupIds.add(_themeDisplay.getCompanyGroupId());
		groupIds.add(_themeDisplay.getScopeGroupId());

		List<DepotEntry> depotEntries =
			DepotEntryServiceUtil.getCurrentAndGroupConnectedDepotEntries(
				_themeDisplay.getScopeGroupId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		for (DepotEntry depotEntry : depotEntries) {
			groupIds.add(depotEntry.getGroupId());
		}

		return AssetVocabularyServiceUtil.getGroupVocabularies(
			ArrayUtil.toLongArray(groupIds),
			new int[] {AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC});
	}

	private String _getAssetVocabularyURL(long assetVocabularyId)
		throws PortletException {

		PortletResponse portletResponse =
			(PortletResponse)_httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);

		return PortletURLBuilder.create(
			PortletURLUtil.clone(
				_portletURL,
				PortalUtil.getLiferayPortletResponse(portletResponse))
		).setBackURL(
			ParamUtil.getString(
				_httpServletRequest, "backURL",
				PortalUtil.getCurrentURL(_httpServletRequest))
		).setParameter(
			"assetCategoryTreeNodeId", assetVocabularyId
		).setParameter(
			"assetCategoryTreeNodeType",
			AssetCategoryTreeNodeConstants.TYPE_ASSET_VOCABULARY
		).buildString();
	}

	private PortletRequest _getPortletRequest() {
		return (PortletRequest)_httpServletRequest.getAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST);
	}

	private Long _assetCategoryTreeNodeId;
	private final HttpServletRequest _httpServletRequest;
	private final PortletURL _portletURL;
	private final ThemeDisplay _themeDisplay;

}