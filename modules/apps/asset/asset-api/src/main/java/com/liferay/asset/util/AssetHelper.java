/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.util;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.search.hits.SearchHits;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

/**
 * @author Eudaldo Alonso
 */
public interface AssetHelper {

	public static final int ASSET_ENTRY_ABSTRACT_LENGTH = 200;

	public static final char[] INVALID_CHARACTERS = {
		CharPool.AMPERSAND, CharPool.APOSTROPHE, CharPool.AT,
		CharPool.BACK_SLASH, CharPool.CLOSE_BRACKET, CharPool.CLOSE_CURLY_BRACE,
		CharPool.COLON, CharPool.COMMA, CharPool.EQUAL, CharPool.GREATER_THAN,
		CharPool.FORWARD_SLASH, CharPool.LESS_THAN, CharPool.NEW_LINE,
		CharPool.OPEN_BRACKET, CharPool.OPEN_CURLY_BRACE, CharPool.PERCENT,
		CharPool.PIPE, CharPool.PLUS, CharPool.POUND, CharPool.PRIME,
		CharPool.QUESTION, CharPool.QUOTE, CharPool.RETURN, CharPool.SEMICOLON,
		CharPool.SLASH, CharPool.STAR, CharPool.TILDE
	};

	public Set<String> addLayoutTags(
		HttpServletRequest httpServletRequest, List<AssetTag> assetTags);

	public PortletURL getAddPortletURL(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse, long groupId,
			String className, long classTypeId, long[] allAssetCategoryIds,
			String[] allAssetTagNames, String redirect)
		throws Exception;

	public String getAddURLPopUp(
		long groupId, long plid, PortletURL addPortletURL,
		boolean addDisplayPageParameter, Layout layout);

	public List<AssetEntry> getAssetEntries(Hits hits);

	public List<AssetEntry> getAssetEntries(SearchHits searchHits);

	public String getAssetKeywords(String className, long classPK);

	public String getAssetKeywords(
		String className, long classPK, Locale locale);

	public List<AssetPublisherAddItemHolder> getAssetPublisherAddItemHolders(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse, long groupId,
			long[] classNameIds, long[] classTypeIds,
			long[] allAssetCategoryIds, String[] allAssetTagNames,
			String redirect)
		throws Exception;

	public default boolean isValidWord(String word) {
		return true;
	}

	public Hits search(
			HttpServletRequest httpServletRequest,
			AssetEntryQuery assetEntryQuery, int start, int end)
		throws Exception;

	public Hits search(
			SearchContext searchContext, AssetEntryQuery assetEntryQuery,
			int start, int end)
		throws Exception;

	public SearchHits search(
			SearchContext searchContext,
			List<AssetEntryQuery> assetEntryQueries, int start, int end)
		throws Exception;

	public BaseModelSearchResult<AssetEntry> searchAssetEntries(
			AssetEntryQuery assetEntryQuery, long[] assetCategoryIds,
			String[] assetTagNames, Map<String, Serializable> attributes,
			long companyId, String keywords, Layout layout, Locale locale,
			long scopeGroupId, TimeZone timeZone, long userId, int start,
			int end)
		throws Exception;

	public BaseModelSearchResult<AssetEntry> searchAssetEntries(
			HttpServletRequest httpServletRequest,
			AssetEntryQuery assetEntryQuery, int start, int end)
		throws Exception;

	public BaseModelSearchResult<AssetEntry> searchAssetEntries(
			SearchContext searchContext, AssetEntryQuery assetEntryQuery,
			int start, int end)
		throws Exception;

	public long searchCount(
			SearchContext searchContext, AssetEntryQuery assetEntryQuery)
		throws Exception;

	public long searchCount(
			SearchContext searchContext,
			List<AssetEntryQuery> assetEntryQueries, int start, int end)
		throws Exception;

}