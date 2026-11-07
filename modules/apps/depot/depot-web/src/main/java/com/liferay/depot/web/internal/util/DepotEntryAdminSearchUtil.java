/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.util;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.search.DepotEntrySearch;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.item.selector.criteria.group.criterion.GroupItemSelectorCriterion;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.search.GroupSearch;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import java.util.List;

/**
 * @author Roberto Díaz
 */
public class DepotEntryAdminSearchUtil {

	public static DepotEntrySearch getDepotEntrySearch(
			DepotEntryService depotEntryService,
			GroupItemSelectorCriterion groupItemSelectorCriterion,
			Language language, PortletRequest portletRequest,
			PortletResponse portletResponse, PortletURL portletURL)
		throws PortalException {

		if (Validator.isNull(ParamUtil.getString(portletRequest, "keywords")) &&
			!groupItemSelectorCriterion.isIncludeAllVisibleGroups()) {

			return _getGroupConnectedDepotEntrySearch(
				depotEntryService,
				groupItemSelectorCriterion.getDepotEntryType(), language,
				portletRequest, portletResponse, portletURL);
		}

		return _getDepotEntrySearch(
			depotEntryService, groupItemSelectorCriterion.getDepotEntryType(),
			language, portletRequest, portletResponse, portletURL);
	}

	public static DepotEntrySearch getDepotEntrySearch(
			DepotEntryService depotEntryService, int depotEntryType,
			Language language, PortletRequest portletRequest,
			PortletResponse portletResponse, PortletURL portletURL)
		throws PortalException {

		return _getDepotEntrySearch(
			depotEntryService, depotEntryType, language, portletRequest,
			portletResponse, portletURL);
	}

	public static GroupSearch getGroupSearch(
			DepotEntryService depotEntryService,
			GroupItemSelectorCriterion groupItemSelectorCriterion,
			Language language, PortletRequest portletRequest,
			PortletResponse portletResponse, PortletURL portletURL)
		throws PortalException {

		DepotEntrySearch depotEntrySearch = getDepotEntrySearch(
			depotEntryService, groupItemSelectorCriterion, language,
			portletRequest, portletResponse, portletURL);

		GroupSearch groupSearch = new GroupSearch(portletRequest, portletURL);

		groupSearch.setEmptyResultsMessage(
			depotEntrySearch.getEmptyResultsMessage());
		groupSearch.setResultsAndTotal(
			() -> TransformUtil.transform(
				depotEntrySearch.getResults(),
				depotEntry -> depotEntry.getGroup()),
			depotEntrySearch.getTotal());

		return groupSearch;
	}

	private static BooleanClause[] _getBooleanClauses() {
		BooleanQuery booleanQuery = new BooleanQuery();

		BooleanFilter booleanFilter = new BooleanFilter();

		TermsFilter termsFilter = new TermsFilter(Field.STAGING_GROUP);

		termsFilter.addValue("false");

		booleanFilter.add(termsFilter, BooleanClauseOccur.MUST);

		booleanQuery.setPreBooleanFilter(booleanFilter);

		return new BooleanClause[] {
			new BooleanClause<>(booleanQuery, BooleanClauseOccur.MUST)
		};
	}

	private static DepotEntrySearch _getDepotEntrySearch(
			DepotEntryService depotEntryService, int depotEntryType,
			Language language, PortletRequest portletRequest,
			PortletResponse portletResponse, PortletURL portletURL)
		throws PortalException {

		DepotEntrySearch depotEntrySearch = new DepotEntrySearch(
			portletRequest, portletResponse, portletURL, "depotEntries");

		depotEntrySearch.setEmptyResultsMessage(
			language.get(
				portletRequest.getLocale(), "no-asset-libraries-were-found"));
		depotEntrySearch.setResultsAndTotal(
			() -> _getResults(
				depotEntrySearch, depotEntryService, depotEntryType,
				portletRequest),
			_getTotal(depotEntryType, portletRequest));

		return depotEntrySearch;
	}

	private static DepotEntrySearch _getGroupConnectedDepotEntrySearch(
			DepotEntryService depotEntryService, int depotEntryType,
			Language language, PortletRequest portletRequest,
			PortletResponse portletResponse, PortletURL portletURL)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		DepotEntrySearch depotEntrySearch = new DepotEntrySearch(
			portletRequest, portletResponse, portletURL, "depotEntries");

		depotEntrySearch.setEmptyResultsMessage(
			language.get(
				portletRequest.getLocale(),
				(depotEntryType == DepotConstants.TYPE_SPACE) ?
					"no-spaces-were-found" : "no-asset-libraries-were-found"));
		depotEntrySearch.setResultsAndTotal(
			() -> depotEntryService.getGroupConnectedDepotEntries(
				themeDisplay.getScopeGroupId(), depotEntryType,
				depotEntrySearch.getStart(), depotEntrySearch.getEnd()),
			depotEntryService.getGroupConnectedDepotEntriesCount(
				themeDisplay.getScopeGroupId(), depotEntryType));

		return depotEntrySearch;
	}

	private static List<DepotEntry> _getResults(
			DepotEntrySearch depotEntrySearch,
			DepotEntryService depotEntryService, int depotEntryType,
			PortletRequest portletRequest)
		throws PortalException {

		Indexer<Object> indexer = IndexerRegistryUtil.getIndexer(
			DepotEntry.class.getName());

		SearchContext searchContext = _getSearchContext(
			depotEntryType, portletRequest);

		searchContext.setEnd(depotEntrySearch.getEnd());
		searchContext.setSorts(new Sort(Field.NAME, false));
		searchContext.setStart(depotEntrySearch.getStart());

		Hits hits = indexer.search(searchContext);

		return TransformUtil.transformToList(
			hits.getDocs(),
			document -> depotEntryService.getDepotEntry(
				GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK))));
	}

	private static SearchContext _getSearchContext(
		int depotEntryType, PortletRequest portletRequest) {

		SearchContext searchContext = new SearchContext();

		searchContext.setAttribute(Field.TYPE, depotEntryType);
		searchContext.setBooleanClauses(_getBooleanClauses());
		searchContext.setEnd(QueryUtil.ALL_POS);
		searchContext.setStart(QueryUtil.ALL_POS);

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		searchContext.setCompanyId(themeDisplay.getCompanyId());

		String keywords = ParamUtil.getString(portletRequest, "keywords");

		if (Validator.isNotNull(keywords)) {
			searchContext.setKeywords(keywords);
		}

		return searchContext;
	}

	private static int _getTotal(
			int depotEntryType, PortletRequest portletRequest)
		throws SearchException {

		Indexer<Object> indexer = IndexerRegistryUtil.getIndexer(
			DepotEntry.class.getName());

		return (int)indexer.searchCount(
			_getSearchContext(depotEntryType, portletRequest));
	}

}