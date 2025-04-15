/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.item.selector.view.display.context;

import com.liferay.item.selector.ItemSelectorReturnTypeResolverHandler;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.search.SearchResult;
import com.liferay.portal.kernel.search.SearchResultUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.wiki.item.selector.WikiPageItemSelectorCriterion;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiNodeLocalService;
import com.liferay.wiki.service.WikiPageLocalServiceUtil;
import com.liferay.wiki.web.internal.item.selector.WikiPageItemSelectorReturnTypeResolver;
import com.liferay.wiki.web.internal.item.selector.view.WikiPageItemSelectorView;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Roberto Díaz
 */
public class WikiPageItemSelectorViewDisplayContext {

	public WikiPageItemSelectorViewDisplayContext(
		WikiPageItemSelectorCriterion wikiPageItemSelectorCriterion,
		WikiPageItemSelectorView wikiPageItemSelectorView,
		WikiNodeLocalService wikiNodeLocalService,
		ItemSelectorReturnTypeResolverHandler
			itemSelectorReturnTypeResolverHandler,
		String itemSelectedEventName, boolean search, PortletURL portletURL) {

		_wikiPageItemSelectorCriterion = wikiPageItemSelectorCriterion;
		_wikiPageItemSelectorView = wikiPageItemSelectorView;
		_wikiNodeLocalService = wikiNodeLocalService;
		_itemSelectorReturnTypeResolverHandler =
			itemSelectorReturnTypeResolverHandler;
		_itemSelectedEventName = itemSelectedEventName;
		_search = search;
		_portletURL = portletURL;
	}

	public String getItemSelectedEventName() {
		return _itemSelectedEventName;
	}

	public WikiNode getNode() throws PortalException {
		return _wikiNodeLocalService.getNode(
			_wikiPageItemSelectorCriterion.getNodeId());
	}

	public PortletURL getPortletURL(
			HttpServletRequest httpServletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws PortletException {

		return PortletURLBuilder.create(
			PortletURLUtil.clone(_portletURL, liferayPortletResponse)
		).setParameter(
			"selectedTab", getTitle(httpServletRequest.getLocale())
		).buildPortletURL();
	}

	public SearchContainer<WikiPage> getSearchContainer(
			HttpServletRequest httpServletRequest,
			LiferayPortletResponse liferayPortletResponse,
			RenderRequest renderRequest)
		throws Exception {

		if (_searchContainer != null) {
			return _searchContainer;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String emptyResultsMessage = null;

		if (isSearch()) {
			String keywords = ParamUtil.getString(
				httpServletRequest, "keywords");

			emptyResultsMessage = LanguageUtil.format(
				themeDisplay.getLocale(),
				"no-pages-were-found-that-matched-the-keywords-x",
				"<strong>" + HtmlUtil.escape(keywords) + "</strong>", false);
		}
		else {
			emptyResultsMessage = "there-are-no-pages";
		}

		_searchContainer = new SearchContainer(
			renderRequest,
			getPortletURL(httpServletRequest, liferayPortletResponse), null,
			emptyResultsMessage);

		WikiNode node = getNode();

		if (isSearch()) {
			Indexer<WikiPage> indexer = IndexerRegistryUtil.getIndexer(
				WikiPage.class);

			SearchContext searchContext = SearchContextFactory.getInstance(
				httpServletRequest);

			searchContext.setEnd(_searchContainer.getEnd());
			searchContext.setIncludeAttachments(false);
			searchContext.setIncludeDiscussions(false);
			searchContext.setNodeIds(new long[] {node.getNodeId()});
			searchContext.setStart(_searchContainer.getStart());

			Hits hits = indexer.search(searchContext);

			List<WikiPage> results = new ArrayList<>();

			for (SearchResult searchResult :
					SearchResultUtil.getSearchResults(
						hits, themeDisplay.getLocale())) {

				WikiPage wikiPage = WikiPageLocalServiceUtil.getPage(
					searchResult.getClassPK());

				results.add(wikiPage);
			}

			_searchContainer.setResultsAndTotal(
				() -> results, hits.getLength());
		}
		else {
			_searchContainer.setResultsAndTotal(
				() -> WikiPageLocalServiceUtil.getPages(
					node.getNodeId(), true, getStatus(),
					_searchContainer.getStart(), _searchContainer.getEnd()),
				WikiPageLocalServiceUtil.getPagesCount(
					node.getNodeId(), true, getStatus()));
		}

		return _searchContainer;
	}

	public int getStatus() throws PortalException {
		return _wikiPageItemSelectorCriterion.getStatus();
	}

	public String getTitle(Locale locale) {
		return _wikiPageItemSelectorView.getTitle(locale);
	}

	public WikiPageItemSelectorCriterion getWikiPageItemSelectorCriterion() {
		return _wikiPageItemSelectorCriterion;
	}

	public WikiPageItemSelectorReturnTypeResolver
		getWikiPageItemSelectorReturnTypeResolver() {

		return (WikiPageItemSelectorReturnTypeResolver)
			_itemSelectorReturnTypeResolverHandler.
				getItemSelectorReturnTypeResolver(
					_wikiPageItemSelectorCriterion, _wikiPageItemSelectorView,
					WikiPage.class);
	}

	public boolean isSearch() {
		return _search;
	}

	private final String _itemSelectedEventName;
	private final ItemSelectorReturnTypeResolverHandler
		_itemSelectorReturnTypeResolverHandler;
	private final PortletURL _portletURL;
	private final boolean _search;
	private SearchContainer<WikiPage> _searchContainer;
	private final WikiNodeLocalService _wikiNodeLocalService;
	private final WikiPageItemSelectorCriterion _wikiPageItemSelectorCriterion;
	private final WikiPageItemSelectorView _wikiPageItemSelectorView;

}