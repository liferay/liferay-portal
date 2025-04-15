/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.synonyms.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.sort.Sorts;
import com.liferay.portal.search.tuning.synonyms.index.name.SynonymSetIndexName;
import com.liferay.portal.search.tuning.synonyms.index.name.SynonymSetIndexNameBuilder;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.DocumentToSynonymSetTranslatorUtil;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSet;
import com.liferay.portal.search.tuning.synonyms.web.internal.request.SearchSynonymSetRequest;
import com.liferay.portal.search.tuning.synonyms.web.internal.request.SearchSynonymSetResponse;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.RenderURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

/**
 * @author Filipe Oshiro
 */
public class SynonymsDisplayBuilder {

	public SynonymsDisplayBuilder(
		HttpServletRequest httpServletRequest, Language language, Portal portal,
		Queries queries, RenderRequest renderRequest,
		RenderResponse renderResponse, SearchEngineAdapter searchEngineAdapter,
		SearchEngineInformation searchEngineInformation, Sorts sorts,
		SynonymSetIndexNameBuilder synonymSetIndexNameBuilder) {

		_httpServletRequest = httpServletRequest;
		_language = language;
		_portal = portal;
		_queries = queries;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
		_searchEngineAdapter = searchEngineAdapter;
		_searchEngineInformation = searchEngineInformation;
		_sorts = sorts;
		_synonymSetIndexNameBuilder = synonymSetIndexNameBuilder;
	}

	public SynonymsDisplayContext build() {
		SynonymsDisplayContext synonymsDisplayContext =
			new SynonymsDisplayContext();

		if (Objects.equals(
				_searchEngineInformation.getVendorString(), "Solr")) {

			return synonymsDisplayContext;
		}

		synonymsDisplayContext.setCreationMenu(getCreationMenu());

		SearchContainer<SynonymSetDisplayContext> searchContainer =
			_buildSearchContainer();

		List<SynonymSetDisplayContext> synonymSetDisplayContexts =
			searchContainer.getResults();

		synonymsDisplayContext.setDisabledManagementBar(
			isDisabledManagementBar(synonymSetDisplayContexts));

		synonymsDisplayContext.setDropdownItems(getDropdownItems());
		synonymsDisplayContext.setItemsTotal(synonymSetDisplayContexts.size());
		synonymsDisplayContext.setSearchContainer(searchContainer);

		return synonymsDisplayContext;
	}

	public String getDisplayedSynonymSet(String synonymSet) {
		return StringUtil.replace(synonymSet, ',', ", ");
	}

	protected CreationMenu getCreationMenu() {
		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					_renderResponse.createRenderURL(), "mvcRenderCommandName",
					"/synonyms/edit_synonym_sets", "redirect",
					_portal.getCurrentURL(_httpServletRequest));
				dropdownItem.setLabel(
					_language.get(_httpServletRequest, "new-synonym-set"));
			}
		).build();
	}

	protected List<DropdownItem> getDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData("action", "deleteMultipleSynonyms");
				dropdownItem.setIcon("times-circle");
				dropdownItem.setLabel(
					_language.get(_httpServletRequest, "delete"));
				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	protected boolean isDisabledManagementBar(
		List<SynonymSetDisplayContext> synonymSetDisplayContexts) {

		return synonymSetDisplayContexts.isEmpty();
	}

	private RenderURL _buildEditRenderURL(SynonymSet synonymSet) {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/synonyms/edit_synonym_sets"
		).setRedirect(
			_portal.getCurrentURL(_httpServletRequest)
		).setParameter(
			"synonymSetId", synonymSet.getSynonymSetDocumentId()
		).buildRenderURL();
	}

	private SearchContainer<SynonymSetDisplayContext> _buildSearchContainer() {
		SearchContainer<SynonymSetDisplayContext> searchContainer =
			new SearchContainer<>(
				_renderRequest, _getPortletURL(), null, "there-are-no-entries");

		searchContainer.setId("synonymSetsEntries");

		SearchSynonymSetRequest searchSynonymSetRequest =
			new SearchSynonymSetRequest(
				_buildSynonymSetIndexName(), _httpServletRequest, _queries,
				_sorts, searchContainer, _searchEngineAdapter);

		SearchSynonymSetResponse searchSynonymSetResponse =
			searchSynonymSetRequest.search();

		searchContainer.setResultsAndTotal(
			() -> TransformUtil.transform(
				DocumentToSynonymSetTranslatorUtil.translateAll(
					searchSynonymSetResponse.getSearchHits()),
				this::_buildSynonymSetDisplayContext),
			searchSynonymSetResponse.getTotalHits());

		searchContainer.setRowChecker(
			new EmptyOnClickRowChecker(_renderResponse));
		searchContainer.setSearch(true);

		return searchContainer;
	}

	private SynonymSetDisplayContext _buildSynonymSetDisplayContext(
		SynonymSet synonymSet) {

		SynonymSetDisplayContext synonymSetDisplayContext =
			new SynonymSetDisplayContext();

		String synonyms = synonymSet.getSynonyms();

		RenderURL editRenderURL = _buildEditRenderURL(synonymSet);

		synonymSetDisplayContext.setDropDownItems(
			_buildSynonymSetDropdownItemList(synonymSet, editRenderURL));
		synonymSetDisplayContext.setEditRenderURL(editRenderURL.toString());

		synonymSetDisplayContext.setDisplayedSynonymSet(
			getDisplayedSynonymSet(synonyms));
		synonymSetDisplayContext.setSynonymSetId(
			synonymSet.getSynonymSetDocumentId());
		synonymSetDisplayContext.setSynonyms(synonyms);

		return synonymSetDisplayContext;
	}

	private List<DropdownItem> _buildSynonymSetDropdownItemList(
		SynonymSet synonymSet, RenderURL editRenderURL) {

		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setHref(editRenderURL);
				dropdownItem.setLabel(
					_language.get(_httpServletRequest, "edit"));
				dropdownItem.setQuickAction(true);
			}
		).add(
			dropdownItem -> {
				dropdownItem.putData("action", "delete");
				dropdownItem.putData(
					"deleteURL",
					PortletURLBuilder.createActionURL(
						_renderResponse
					).setActionName(
						"/synonyms/delete_synonym_sets"
					).setCMD(
						Constants.DELETE
					).setRedirect(
						_portal.getCurrentURL(_httpServletRequest)
					).setParameter(
						"rowIds", synonymSet.getSynonymSetDocumentId()
					).buildString());

				dropdownItem.setIcon("trash");
				dropdownItem.setLabel(
					_language.get(_httpServletRequest, "delete"));
				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	private SynonymSetIndexName _buildSynonymSetIndexName() {
		return _synonymSetIndexNameBuilder.getSynonymSetIndexName(
			_portal.getCompanyId(_renderRequest));
	}

	private PortletURL _getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCPath(
			"/view.jsp"
		).buildPortletURL();
	}

	private final HttpServletRequest _httpServletRequest;
	private final Language _language;
	private final Portal _portal;
	private final Queries _queries;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final SearchEngineAdapter _searchEngineAdapter;
	private final SearchEngineInformation _searchEngineInformation;
	private final Sorts _sorts;
	private final SynonymSetIndexNameBuilder _synonymSetIndexNameBuilder;

}