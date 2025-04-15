/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.item.selector.web.internal.display.context;

import com.liferay.asset.categories.item.selector.AssetCategoryTreeNodeItemSelectorReturnType;
import com.liferay.asset.categories.item.selector.web.internal.constants.AssetCategoryTreeNodeConstants;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Rubén Pulido
 */
public class SelectAssetCategoryTreeNodeDisplayContext {

	public SelectAssetCategoryTreeNodeDisplayContext(
		HttpServletRequest httpServletRequest, String itemSelectedEventName,
		PortletURL portletURL) {

		_httpServletRequest = httpServletRequest;
		_itemSelectedEventName = itemSelectedEventName;
		_portletURL = portletURL;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public SearchContainer<AssetCategory> getAssetCategorySearchContainer()
		throws PortalException {

		SearchContainer<AssetCategory> searchContainer = new SearchContainer<>(
			_getPortletRequest(), _portletURL, null,
			"there-are-no-items-to-display");

		List<AssetCategory> assetCategories = _getAssetCategories();

		searchContainer.setResultsAndTotal(
			() -> assetCategories, assetCategories.size());

		return searchContainer;
	}

	public long getAssetCategoryTreeNodeId() {
		if (_assetCategoryTreeNodeId != null) {
			return _assetCategoryTreeNodeId;
		}

		_assetCategoryTreeNodeId = ParamUtil.getLong(
			_httpServletRequest, "assetCategoryTreeNodeId", -1);

		return _assetCategoryTreeNodeId;
	}

	public String getAssetCategoryTreeNodeName() throws PortalException {
		String assetCategoryTreeNodeType = getAssetCategoryTreeNodeType();

		if (assetCategoryTreeNodeType.equals(
				AssetCategoryTreeNodeConstants.TYPE_ASSET_CATEGORY)) {

			AssetCategory assetCategory =
				AssetCategoryServiceUtil.fetchCategory(
					getAssetCategoryTreeNodeId());

			if (assetCategory != null) {
				return assetCategory.getTitle(_themeDisplay.getLocale());
			}
		}
		else if (assetCategoryTreeNodeType.equals(
					AssetCategoryTreeNodeConstants.TYPE_ASSET_VOCABULARY)) {

			AssetVocabulary assetVocabulary =
				AssetVocabularyServiceUtil.fetchVocabulary(
					getAssetCategoryTreeNodeId());

			if (assetVocabulary != null) {
				return assetVocabulary.getTitle(_themeDisplay.getLocale());
			}
		}

		return null;
	}

	public String getAssetCategoryTreeNodeType() {
		if (_assetCategoryTreeNodeType != null) {
			return _assetCategoryTreeNodeType;
		}

		_assetCategoryTreeNodeType = ParamUtil.get(
			_httpServletRequest, "assetCategoryTreeNodeType", StringPool.BLANK);

		return _assetCategoryTreeNodeType;
	}

	public String getAssetCategoryURL(long assetCategoryId)
		throws PortletException {

		return _getAssetCategoryTreeNodeURL(
			assetCategoryId,
			AssetCategoryTreeNodeConstants.TYPE_ASSET_CATEGORY);
	}

	public List<BreadcrumbEntry> getBreadcrumbEntries()
		throws PortalException, PortletException {

		return BreadcrumbEntryListBuilder.add(
			_getAssetVocabulariesBreadcrumbEntry()
		).add(
			_getAssetVocabularyBreadcrumbEntry()
		).addAll(
			_getAssetCategoryBreadcrumbEntries()
		).build();
	}

	public Map<String, Object> getContext(
		LiferayPortletResponse liferayPortletResponse) {

		return HashMapBuilder.<String, Object>put(
			"buttonClass", ".asset-category-tree-node-selector"
		).put(
			"containerId",
			liferayPortletResponse.getNamespace() +
				"assetCategoryTreeNodeSelector"
		).put(
			"eventName", getItemSelectedEventName()
		).put(
			"returnType",
			AssetCategoryTreeNodeItemSelectorReturnType.class.toString()
		).build();
	}

	public String getItemSelectedEventName() {
		return _itemSelectedEventName;
	}

	private BreadcrumbEntry _createBreadcrumbEntry(String title, String url) {
		return new BreadcrumbEntry() {
			{
				setBrowsable(url != null);
				setTitle(title);
				setURL(url);
			}
		};
	}

	private List<AssetCategory> _getAssetCategories() throws PortalException {
		String assetCategoryTreeNodeType = getAssetCategoryTreeNodeType();

		if (assetCategoryTreeNodeType.equals(
				AssetCategoryTreeNodeConstants.TYPE_ASSET_CATEGORY)) {

			int count = AssetCategoryServiceUtil.getChildCategoriesCount(
				getAssetCategoryTreeNodeId());

			return AssetCategoryServiceUtil.getChildCategories(
				getAssetCategoryTreeNodeId(), 0, count, null);
		}
		else if (assetCategoryTreeNodeType.equals(
					AssetCategoryTreeNodeConstants.TYPE_ASSET_VOCABULARY)) {

			AssetVocabulary assetVocabulary =
				AssetVocabularyServiceUtil.fetchVocabulary(
					getAssetCategoryTreeNodeId());

			if (assetVocabulary != null) {
				int count =
					AssetCategoryServiceUtil.getVocabularyRootCategoriesCount(
						assetVocabulary.getGroupId(),
						getAssetCategoryTreeNodeId());

				return AssetCategoryServiceUtil.getVocabularyRootCategories(
					assetVocabulary.getGroupId(), getAssetCategoryTreeNodeId(),
					0, count, null);
			}
		}

		return new ArrayList<>();
	}

	private List<BreadcrumbEntry> _getAssetCategoryBreadcrumbEntries()
		throws PortalException, PortletException {

		ArrayList<BreadcrumbEntry> breadcrumbEntries = new ArrayList<>();

		String assetCategoryTreeNodeType = getAssetCategoryTreeNodeType();

		if (!assetCategoryTreeNodeType.equals(
				AssetCategoryTreeNodeConstants.TYPE_ASSET_CATEGORY)) {

			return breadcrumbEntries;
		}

		AssetCategory assetCategory = AssetCategoryServiceUtil.fetchCategory(
			getAssetCategoryTreeNodeId());

		if (assetCategory != null) {
			List<AssetCategory> assetCategories = assetCategory.getAncestors();

			Iterator<AssetCategory> iterator = ListUtil.reverseIterator(
				assetCategories);

			while (iterator.hasNext()) {
				AssetCategory ancestorAssetCategory = iterator.next();

				breadcrumbEntries.add(
					_createBreadcrumbEntry(
						ancestorAssetCategory.getTitle(
							_themeDisplay.getLocale()),
						getAssetCategoryURL(
							ancestorAssetCategory.getCategoryId())));
			}

			breadcrumbEntries.add(
				_createBreadcrumbEntry(
					assetCategory.getTitle(_themeDisplay.getLocale()), null));
		}

		return breadcrumbEntries;
	}

	private String _getAssetCategoryTreeNodeURL(
			long assetCategoryTreeNodeId, String assetCategoryTreeNodeType)
		throws PortletException {

		PortletResponse portletResponse =
			(PortletResponse)_httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_RESPONSE);

		return PortletURLBuilder.create(
			PortletURLUtil.clone(
				_portletURL,
				PortalUtil.getLiferayPortletResponse(portletResponse))
		).setBackURL(
			ParamUtil.getString(
				_httpServletRequest, "backURL",
				PortalUtil.getCurrentURL(_httpServletRequest))
		).setParameter(
			"assetCategoryTreeNodeId", assetCategoryTreeNodeId
		).setParameter(
			"assetCategoryTreeNodeType", assetCategoryTreeNodeType
		).buildString();
	}

	private BreadcrumbEntry _getAssetVocabulariesBreadcrumbEntry() {
		String backURL = ParamUtil.getString(
			_httpServletRequest, "backURL",
			PortalUtil.getCurrentURL(_httpServletRequest));

		return _createBreadcrumbEntry(
			LanguageUtil.get(_themeDisplay.getLocale(), "vocabularies"),
			backURL);
	}

	private BreadcrumbEntry _getAssetVocabularyBreadcrumbEntry()
		throws PortalException, PortletException {

		long assetCategoryTreeNodeId = getAssetCategoryTreeNodeId();

		long assetVocabularyId = assetCategoryTreeNodeId;

		String assetCategoryTreeNodeType = getAssetCategoryTreeNodeType();

		if (assetCategoryTreeNodeType.equals(
				AssetCategoryTreeNodeConstants.TYPE_ASSET_CATEGORY)) {

			AssetCategory assetCategory =
				AssetCategoryServiceUtil.fetchCategory(assetCategoryTreeNodeId);

			assetVocabularyId = assetCategory.getVocabularyId();
		}

		AssetVocabulary assetVocabulary =
			AssetVocabularyServiceUtil.fetchVocabulary(assetVocabularyId);

		if (assetVocabulary == null) {
			return null;
		}

		return _createBreadcrumbEntry(
			assetVocabulary.getTitle(_themeDisplay.getLocale()),
			_getAssetCategoryTreeNodeURL(
				assetVocabularyId,
				AssetCategoryTreeNodeConstants.TYPE_ASSET_VOCABULARY));
	}

	private PortletRequest _getPortletRequest() {
		return (PortletRequest)_httpServletRequest.getAttribute(
			JavaConstants.JAVAX_PORTLET_REQUEST);
	}

	private Long _assetCategoryTreeNodeId;
	private String _assetCategoryTreeNodeType;
	private final HttpServletRequest _httpServletRequest;
	private final String _itemSelectedEventName;
	private final PortletURL _portletURL;
	private final ThemeDisplay _themeDisplay;

}