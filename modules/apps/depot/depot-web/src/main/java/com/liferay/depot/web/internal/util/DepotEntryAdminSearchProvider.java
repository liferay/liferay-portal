/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.util;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.item.selector.DepotGroupItemSelectorCriterion;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.search.DepotEntrySearch;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(service = DepotEntryAdminSearchProvider.class)
public class DepotEntryAdminSearchProvider {

	public DepotEntrySearch getDepotEntrySearch(
			DepotGroupItemSelectorCriterion depotGroupItemSelectorCriterion,
			PortletRequest portletRequest, PortletResponse portletResponse,
			PortletURL portletURL)
		throws PortalException {

		if (Validator.isNull(ParamUtil.getString(portletRequest, "keywords")) &&
			!depotGroupItemSelectorCriterion.isIncludeAllVisibleGroups()) {

			return _getGroupConnectedDepotEntrySearch(
				depotGroupItemSelectorCriterion.getDepotEntryType(),
				portletRequest, portletResponse, portletURL);
		}

		return _getDepotEntrySearch(
			depotGroupItemSelectorCriterion.getDepotEntryType(), portletRequest,
			portletResponse, portletURL);
	}

	public DepotEntrySearch getDepotEntrySearch(
			int depotEntryType, PortletRequest portletRequest,
			PortletResponse portletResponse, PortletURL portletURL)
		throws PortalException {

		return _getDepotEntrySearch(
			depotEntryType, portletRequest, portletResponse, portletURL);
	}

	private DepotEntrySearch _getDepotEntrySearch(
			int depotEntryType, PortletRequest portletRequest,
			PortletResponse portletResponse, PortletURL portletURL)
		throws PortalException {

		DepotEntrySearch depotEntrySearch = new DepotEntrySearch(
			portletRequest, portletResponse, portletURL, "depotEntries");

		depotEntrySearch.setEmptyResultsMessage(
			_language.get(
				portletRequest.getLocale(), "no-asset-libraries-were-found"));

		depotEntrySearch.setResultsAndTotal(
			() -> _getResults(depotEntryType, depotEntrySearch, portletRequest),
			_getTotal(depotEntryType, portletRequest));

		return depotEntrySearch;
	}

	private DepotEntrySearch _getGroupConnectedDepotEntrySearch(
			int depotEntryType, PortletRequest portletRequest,
			PortletResponse portletResponse, PortletURL portletURL)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		DepotEntrySearch depotEntrySearch = new DepotEntrySearch(
			portletRequest, portletResponse, portletURL, "depotEntries");

		depotEntrySearch.setEmptyResultsMessage(
			_language.get(
				portletRequest.getLocale(),
				(depotEntryType == DepotConstants.TYPE_SPACE) ?
					"no-spaces-were-found" : "no-asset-libraries-were-found"));

		depotEntrySearch.setResultsAndTotal(
			() -> _depotEntryService.getGroupConnectedDepotEntries(
				themeDisplay.getScopeGroupId(), depotEntryType,
				depotEntrySearch.getStart(), depotEntrySearch.getEnd()),
			_depotEntryService.getGroupConnectedDepotEntriesCount(
				themeDisplay.getScopeGroupId(), depotEntryType));

		return depotEntrySearch;
	}

	private List<DepotEntry> _getResults(
			int depotEntryType, DepotEntrySearch depotEntrySearch,
			PortletRequest portletRequest)
		throws PortalException {

		List<DepotEntry> depotEntries = new ArrayList<>();

		Indexer<Object> indexer = IndexerRegistryUtil.getIndexer(
			DepotEntry.class.getName());

		SearchContext searchContext = _getSearchContext(
			depotEntryType, portletRequest);

		searchContext.setEnd(depotEntrySearch.getEnd());
		searchContext.setSorts(new Sort(Field.NAME, false));
		searchContext.setStart(depotEntrySearch.getStart());

		Hits hits = indexer.search(searchContext);

		for (Document document : hits.getDocs()) {
			long classPK = GetterUtil.getLong(
				document.get(Field.ENTRY_CLASS_PK));

			DepotEntry depotEntry = _depotEntryService.getDepotEntry(classPK);

			if (depotEntry != null) {
				depotEntries.add(depotEntry);
			}
		}

		return depotEntries;
	}

	private SearchContext _getSearchContext(
		int depotEntryType, PortletRequest portletRequest) {

		SearchContext searchContext = new SearchContext();

		searchContext.setAttribute(Field.TYPE, depotEntryType);

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		searchContext.setCompanyId(themeDisplay.getCompanyId());

		String keywords = ParamUtil.getString(portletRequest, "keywords");

		if (Validator.isNotNull(keywords)) {
			searchContext.setKeywords(keywords);
		}

		return searchContext;
	}

	private int _getTotal(int depotEntryType, PortletRequest portletRequest)
		throws SearchException {

		Indexer<Object> indexer = IndexerRegistryUtil.getIndexer(
			DepotEntry.class.getName());

		return (int)indexer.searchCount(
			_getSearchContext(depotEntryType, portletRequest));
	}

	@Reference
	private DepotEntryService _depotEntryService;

	@Reference
	private Language _language;

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

}