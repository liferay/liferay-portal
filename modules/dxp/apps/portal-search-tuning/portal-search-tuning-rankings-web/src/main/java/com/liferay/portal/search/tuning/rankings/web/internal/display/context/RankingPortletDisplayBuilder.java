/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.portlet.SearchDisplayStyleUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.experiences.SXPBlueprintTitleProvider;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.sort.Sorts;
import com.liferay.portal.search.tuning.rankings.constants.ResultRankingsConstants;
import com.liferay.portal.search.tuning.rankings.index.Ranking;
import com.liferay.portal.search.tuning.rankings.index.RankingBuilderFactory;
import com.liferay.portal.search.tuning.rankings.index.name.RankingIndexName;
import com.liferay.portal.search.tuning.rankings.index.name.RankingIndexNameBuilder;
import com.liferay.portal.search.tuning.rankings.web.internal.constants.ResultRankingsPortletKeys;
import com.liferay.portal.search.tuning.rankings.web.internal.index.DocumentToRankingTranslatorUtil;
import com.liferay.portal.search.tuning.rankings.web.internal.index.RankingFields;
import com.liferay.portal.search.tuning.rankings.web.internal.request.SearchRankingRequest;
import com.liferay.portal.search.tuning.rankings.web.internal.request.SearchRankingResponse;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

/**
 * @author Kevin Tan
 */
public class RankingPortletDisplayBuilder {

	public RankingPortletDisplayBuilder(
		HttpServletRequest httpServletRequest, Language language, Portal portal,
		Queries queries, RankingBuilderFactory rankingBuilderFactory,
		RankingIndexNameBuilder rankingIndexNameBuilder,
		RenderRequest renderRequest, RenderResponse renderResponse,
		SearchEngineAdapter searchEngineAdapter,
		SearchEngineInformation searchEngineInformation, Sorts sorts) {

		_httpServletRequest = httpServletRequest;
		_language = language;
		_portal = portal;
		_queries = queries;
		_rankingBuilderFactory = rankingBuilderFactory;
		_rankingIndexNameBuilder = rankingIndexNameBuilder;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
		_searchEngineAdapter = searchEngineAdapter;
		_searchEngineInformation = searchEngineInformation;
		_sorts = sorts;
	}

	public RankingPortletDisplayContext build() {
		RankingPortletDisplayContext rankingPortletDisplayContext =
			new RankingPortletDisplayContext();

		if (Objects.equals(
				_searchEngineInformation.getVendorString(), "Solr")) {

			return rankingPortletDisplayContext;
		}

		SearchContainer<RankingEntryDisplayContext> searchContainer = _search();

		rankingPortletDisplayContext.setActionDropdownItems(
			getActionDropdownItems());
		rankingPortletDisplayContext.setClearResultsURL(getClearResultsURL());
		rankingPortletDisplayContext.setCreationMenu(getCreationMenu());
		rankingPortletDisplayContext.setDisabledManagementBar(
			isDisabledManagementBar(searchContainer));
		rankingPortletDisplayContext.setDisplayStyle(getDisplayStyle());
		rankingPortletDisplayContext.setFilterItemsDropdownItems(
			getFilterItemsDropdownItems());
		rankingPortletDisplayContext.setFilterLabelItems(getFilterLabelItems());
		rankingPortletDisplayContext.setOrderByType(getOrderByType());
		rankingPortletDisplayContext.setSearchActionURL(getSearchActionURL());
		rankingPortletDisplayContext.setSearchContainer(searchContainer);
		rankingPortletDisplayContext.setSortingURL(getSortingURL());
		rankingPortletDisplayContext.setTotalItems(searchContainer.getTotal());

		return rankingPortletDisplayContext;
	}

	public List<LabelItem> getFilterLabelItems() {
		return LabelItemListBuilder.add(
			() -> !Objects.equals(_getScope(), "all"),
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						_getPortletURL(getKeywords())
					).setParameter(
						"scope", "all"
					).buildString());

				labelItem.setCloseable(true);

				labelItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "scope") + ": " +
						LanguageUtil.get(_httpServletRequest, _getScope()));
			}
		).add(
			() -> !Objects.equals(_getStatus(), "all"),
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						_getPortletURL(getKeywords())
					).setParameter(
						"status", "all"
					).buildString());

				labelItem.setCloseable(true);

				labelItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "status") + ": " +
						LanguageUtil.get(_httpServletRequest, _getStatus()));
			}
		).build();
	}

	protected List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData(
					"action", "deactivateResultsRankingsEntries");
				dropdownItem.setIcon("hidden");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "deactivate"));
				dropdownItem.setQuickAction(true);
			}
		).add(
			dropdownItem -> {
				dropdownItem.putData(
					"action", "activateResultsRankingsEntries");
				dropdownItem.setIcon("undo");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "activate"));
				dropdownItem.setQuickAction(true);
			}
		).add(
			dropdownItem -> {
				dropdownItem.putData("action", "deleteResultsRankingsEntries");
				dropdownItem.setIcon("times-circle");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "delete"));
				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	@SuppressWarnings("deprecation")
	protected String getClearResultsURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/"
		).buildString();
	}

	protected CreationMenu getCreationMenu() {
		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					_renderResponse.createRenderURL(), "mvcRenderCommandName",
					"/result_rankings/add_results_rankings", "redirect",
					PortalUtil.getCurrentURL(_httpServletRequest));
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "new-ranking"));
			}
		).build();
	}

	protected String getDisplayStyle() {
		if (Validator.isNotNull(_displayStyle)) {
			return _displayStyle;
		}

		_displayStyle = SearchDisplayStyleUtil.getDisplayStyle(
			_renderRequest, ResultRankingsPortletKeys.RESULT_RANKINGS, "list");

		return _displayStyle;
	}

	protected List<DropdownItem> getFilterItemsDropdownItems() {
		DropdownItemListBuilder.DropdownItemListWrapper
			dropdownItemListWrapper =
				new DropdownItemListBuilder.DropdownItemListWrapper();

		if (FeatureFlagManagerUtil.isEnabled("LPD-6368")) {
			dropdownItemListWrapper.addGroup(
				dropdownGroupItem -> {
					dropdownGroupItem.setDropdownItems(
						_getFilterScopeDropdownItems());
					dropdownGroupItem.setLabel(
						LanguageUtil.get(
							_httpServletRequest, "filter-by-scope"));
				});
		}

		return dropdownItemListWrapper.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					_getFilterStatusDropdownItems());
				dropdownGroupItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "filter-by-status"));
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					_getOrderByDropdownItems(getKeywords()));
				dropdownGroupItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "order-by"));
			}
		).build();
	}

	protected String getKeywords() {
		return ParamUtil.getString(_httpServletRequest, "keywords");
	}

	protected String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest, ResultRankingsPortletKeys.RESULT_RANKINGS,
			"asc");

		return _orderByType;
	}

	protected String getSearchActionURL() {
		return String.valueOf(_getPortletURL(StringPool.BLANK));
	}

	protected SearchContainer<RankingEntryDisplayContext> getSearchContainer(
		String keywords) {

		String emptyResultMessage = _language.format(
			_httpServletRequest, "no-custom-results-yet",
			"<strong>" + HtmlUtil.escape(keywords) + "</strong>", false);

		SearchContainer<RankingEntryDisplayContext> searchContainer =
			new SearchContainer<>(
				_renderRequest, _getPortletURL(keywords), null,
				emptyResultMessage);

		searchContainer.setId("resultRankingsEntries");
		searchContainer.setOrderByCol(_getOrderByCol());
		searchContainer.setOrderByType(getOrderByType());
		searchContainer.setRowChecker(
			new EmptyOnClickRowChecker(_renderResponse));

		return searchContainer;
	}

	@SuppressWarnings("deprecation")
	protected String getSortingURL() {
		return PortletURLBuilder.create(
			_getPortletURL(getKeywords())
		).setParameter(
			"orderByType",
			Objects.equals(getOrderByType(), "asc") ? "desc" : "asc"
		).buildString();
	}

	protected boolean isDisabledManagementBar(
		SearchContainer<RankingEntryDisplayContext> searchContainer) {

		if (_hasResults(searchContainer) || _isSearch(getKeywords())) {
			return false;
		}

		return true;
	}

	protected Boolean isShowCreationMenu() {
		return true;
	}

	private RankingEntryDisplayContext _buildDisplayContext(
		SearchHit searchHit) {

		Ranking ranking = DocumentToRankingTranslatorUtil.translate(
			_rankingBuilderFactory, searchHit.getDocument(), searchHit.getId());

		SXPBlueprintTitleProvider sxpBlueprintTitleProvider =
			_sxpBlueprintTitleProviderSnapshot.get();

		if ((sxpBlueprintTitleProvider == null) &&
			!Validator.isBlank(
				ranking.getSXPBlueprintExternalReferenceCode())) {

			return null;
		}

		RankingEntryDisplayContextBuilder rankingEntryDisplayContextBuilder =
			new RankingEntryDisplayContextBuilder(ranking);

		if ((sxpBlueprintTitleProvider != null) &&
			!Validator.isBlank(
				ranking.getSXPBlueprintExternalReferenceCode())) {

			rankingEntryDisplayContextBuilder.sxpBlueprintTitle(
				sxpBlueprintTitleProvider.getSXPBlueprintTitle(
					_portal.getCompanyId(_httpServletRequest),
					_language.getLanguageId(_httpServletRequest),
					ranking.getSXPBlueprintExternalReferenceCode()));
		}

		return rankingEntryDisplayContextBuilder.build();
	}

	private RankingIndexName _buildRankingIndexName() {
		return _rankingIndexNameBuilder.getRankingIndexName(
			_portal.getCompanyId(_httpServletRequest));
	}

	private List<DropdownItem> _getFilterScopeDropdownItems() {
		String scope = _getScope();

		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setActive(scope.equals("all"));
				dropdownItem.setHref(
					_getPortletURL(getKeywords()), "scope", "all");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "all"));
			}
		).add(
			dropdownItem -> {
				dropdownItem.setActive(scope.equals("site"));
				dropdownItem.setHref(
					_getPortletURL(getKeywords()), "scope", "site");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "site"));
			}
		).add(
			dropdownItem -> {
				dropdownItem.setActive(scope.equals("blueprint"));
				dropdownItem.setHref(
					_getPortletURL(getKeywords()), "scope", "blueprint");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "blueprint"));
			}
		).add(
			dropdownItem -> {
				dropdownItem.setActive(scope.equals("everything"));
				dropdownItem.setHref(
					_getPortletURL(getKeywords()), "scope", "everything");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "everything"));
			}
		).build();
	}

	private List<DropdownItem> _getFilterStatusDropdownItems() {
		String status = _getStatus();

		DropdownItemListBuilder.DropdownItemListWrapper
			dropdownItemListWrapper =
				new DropdownItemListBuilder.DropdownItemListWrapper();

		dropdownItemListWrapper.add(
			dropdownItem -> {
				dropdownItem.setActive(status.equals("all"));
				dropdownItem.setHref(
					_getPortletURL(getKeywords()), "status", "all");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "all"));
			}
		).add(
			dropdownItem -> {
				dropdownItem.setActive(
					status.equals(ResultRankingsConstants.STATUS_ACTIVE));
				dropdownItem.setHref(
					_getPortletURL(getKeywords()), "status",
					ResultRankingsConstants.STATUS_ACTIVE);
				dropdownItem.setLabel(
					LanguageUtil.get(
						_httpServletRequest,
						ResultRankingsConstants.STATUS_ACTIVE));
			}
		).add(
			dropdownItem -> {
				dropdownItem.setActive(
					status.equals(ResultRankingsConstants.STATUS_INACTIVE));
				dropdownItem.setHref(
					_getPortletURL(getKeywords()), "status",
					ResultRankingsConstants.STATUS_INACTIVE);
				dropdownItem.setLabel(
					LanguageUtil.get(
						_httpServletRequest,
						ResultRankingsConstants.STATUS_INACTIVE));
			}
		);

		if (FeatureFlagManagerUtil.isEnabled("LPD-6368")) {
			return dropdownItemListWrapper.add(
				dropdownItem -> {
					dropdownItem.setActive(
						status.equals(
							ResultRankingsConstants.STATUS_NOT_APPLICABLE));
					dropdownItem.setHref(
						_getPortletURL(getKeywords()), "status",
						ResultRankingsConstants.STATUS_NOT_APPLICABLE);
					dropdownItem.setLabel(
						LanguageUtil.get(
							_httpServletRequest,
							ResultRankingsConstants.STATUS_NOT_APPLICABLE));
				}
			).build();
		}

		return dropdownItemListWrapper.build();
	}

	private String _getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_httpServletRequest, ResultRankingsPortletKeys.RESULT_RANKINGS,
			_ORDER_BY_COL);

		return _orderByCol;
	}

	private List<DropdownItem> _getOrderByDropdownItems(String keywords) {
		PortletURL portletURL = _getPortletURL(keywords);

		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setActive(
					Objects.equals(
						_getOrderByCol(), RankingFields.QUERY_STRING_KEYWORD));
				dropdownItem.setHref(
					portletURL, "orderByCol",
					RankingFields.QUERY_STRING_KEYWORD);
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "search-query"));
			}
		).add(
			dropdownItem -> {
				dropdownItem.setActive(
					Objects.equals(_getOrderByCol(), RankingFields.STATUS));
				dropdownItem.setHref(
					portletURL, "orderByCol", RankingFields.STATUS);
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "status"));
			}
		).build();
	}

	@SuppressWarnings("deprecation")
	private PortletURL _getPortletURL(String keywords) {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/"
		).setKeywords(
			() -> {
				if (!Validator.isBlank(keywords)) {
					return keywords;
				}

				return null;
			}
		).setParameter(
			"displayStyle", getDisplayStyle()
		).setParameter(
			"orderByCol", _getOrderByCol()
		).setParameter(
			"orderByType", getOrderByType()
		).setParameter(
			"scope", _getScope()
		).setParameter(
			"status", _getStatus()
		).buildPortletURL();
	}

	private String _getScope() {
		return ParamUtil.getString(_httpServletRequest, "scope", "all");
	}

	private String _getStatus() {
		return ParamUtil.getString(_httpServletRequest, "status", "all");
	}

	private boolean _hasResults(
		SearchContainer<RankingEntryDisplayContext> searchContainer) {

		if (searchContainer.getTotal() > 0) {
			return true;
		}

		return false;
	}

	private boolean _isSearch(String keywords) {
		return !Validator.isBlank(keywords);
	}

	private SearchContainer<RankingEntryDisplayContext> _search() {
		SearchContainer<RankingEntryDisplayContext> searchContainer =
			getSearchContainer(getKeywords());

		SearchRankingRequest searchRankingRequest = new SearchRankingRequest(
			_httpServletRequest, _queries, _buildRankingIndexName(), _sorts,
			searchContainer, _searchEngineAdapter);

		SearchRankingResponse searchRankingResponse =
			searchRankingRequest.search();

		SearchHits searchHits = searchRankingResponse.getSearchHits();

		searchContainer.setResultsAndTotal(
			() -> TransformUtil.transform(
				searchHits.getSearchHits(), this::_buildDisplayContext),
			searchRankingResponse.getTotalHits());

		searchContainer.setSearch(true);

		return searchContainer;
	}

	private static final String _ORDER_BY_COL =
		RankingFields.QUERY_STRING_KEYWORD;

	private static final Snapshot<SXPBlueprintTitleProvider>
		_sxpBlueprintTitleProviderSnapshot = new Snapshot<>(
			RankingPortletDisplayBuilder.class, SXPBlueprintTitleProvider.class,
			null, true);

	private String _displayStyle;
	private final HttpServletRequest _httpServletRequest;
	private final Language _language;
	private String _orderByCol;
	private String _orderByType;
	private final Portal _portal;
	private final Queries _queries;
	private final RankingBuilderFactory _rankingBuilderFactory;
	private final RankingIndexNameBuilder _rankingIndexNameBuilder;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final SearchEngineAdapter _searchEngineAdapter;
	private final SearchEngineInformation _searchEngineInformation;
	private final Sorts _sorts;

}