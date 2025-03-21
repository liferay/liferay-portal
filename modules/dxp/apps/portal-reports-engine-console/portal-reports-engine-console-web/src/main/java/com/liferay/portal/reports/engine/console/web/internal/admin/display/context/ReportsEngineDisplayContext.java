/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.web.internal.admin.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.ViewTypeItemList;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanParamUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.reports.engine.console.constants.ReportsEngineConsolePortletKeys;
import com.liferay.portal.reports.engine.console.model.Definition;
import com.liferay.portal.reports.engine.console.service.DefinitionServiceUtil;
import com.liferay.portal.reports.engine.console.service.EntryServiceUtil;
import com.liferay.portal.reports.engine.console.service.SourceServiceUtil;
import com.liferay.portal.reports.engine.console.web.internal.admin.constants.ReportsEngineWebKeys;
import com.liferay.portal.reports.engine.console.web.internal.admin.display.context.helper.ReportsEngineRequestHelper;
import com.liferay.portal.reports.engine.console.web.internal.admin.search.DefinitionDisplayTerms;
import com.liferay.portal.reports.engine.console.web.internal.admin.search.DefinitionSearch;
import com.liferay.portal.reports.engine.console.web.internal.admin.search.EntryDisplayTerms;
import com.liferay.portal.reports.engine.console.web.internal.admin.search.EntrySearch;
import com.liferay.portal.reports.engine.console.web.internal.admin.search.SourceDisplayTerms;
import com.liferay.portal.reports.engine.console.web.internal.admin.search.SourceSearch;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Rafael Praxedes
 */
public class ReportsEngineDisplayContext {

	public ReportsEngineDisplayContext(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;

		_httpServletRequest = PortalUtil.getHttpServletRequest(
			liferayPortletRequest);

		_reportsEngineRequestHelper = new ReportsEngineRequestHelper(
			_httpServletRequest);

		_themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).buildString();
	}

	public CreationMenu getCreationMenu() throws PortalException {
		if (isReportsTabSelected()) {
			return null;
		}

		return CreationMenuBuilder.addPrimaryDropdownItem(
			() -> isDefinitionsTabSelected(),
			dropdownItem -> {
				dropdownItem.setHref(
					_liferayPortletResponse.createRenderURL(), "mvcPath",
					"/admin/definition/edit_definition.jsp", "redirect",
					PortalUtil.getCurrentURL(
						_reportsEngineRequestHelper.getRequest()));
				dropdownItem.setLabel(
					LanguageUtil.get(
						_reportsEngineRequestHelper.getRequest(), "add"));
			}
		).addPrimaryDropdownItem(
			() -> isSourcesTabSelected(),
			dropdownItem -> {
				dropdownItem.setHref(
					_liferayPortletResponse.createRenderURL(), "mvcPath",
					"/admin/data_source/edit_data_source.jsp", "redirect",
					PortalUtil.getCurrentURL(
						_reportsEngineRequestHelper.getRequest()));
				dropdownItem.setLabel(
					LanguageUtil.get(
						_reportsEngineRequestHelper.getRequest(), "add"));
			}
		).build();
	}

	public String getDisplayStyle() {
		if (_displayStyle == null) {
			PortalPreferences portalPreferences =
				PortletPreferencesFactoryUtil.getPortalPreferences(
					_liferayPortletRequest);

			_displayStyle = ParamUtil.getString(
				_liferayPortletRequest, "displayStyle");

			if (Validator.isNull(_displayStyle)) {
				_displayStyle = portalPreferences.getValue(
					ReportsEngineConsolePortletKeys.REPORTS_ADMIN,
					"display-style", "list");
			}
			else if (ArrayUtil.contains(_DISPLAY_VIEWS, _displayStyle)) {
				portalPreferences.setValue(
					ReportsEngineConsolePortletKeys.REPORTS_ADMIN,
					"display-style", _displayStyle);
			}

			if (!ArrayUtil.contains(_DISPLAY_VIEWS, _displayStyle)) {
				_displayStyle = _DISPLAY_VIEWS[0];
			}
		}

		return _displayStyle;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest, ReportsEngineConsolePortletKeys.REPORTS_ADMIN,
			"asc");

		return _orderByType;
	}

	public List<DropdownItem> getOrderItemsDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setActive(
					Objects.equals(_getOrderByCol(), "create-date"));
				dropdownItem.setHref(
					getPortletURL(), "orderByCol", "create-date");
				dropdownItem.setLabel(
					LanguageUtil.get(
						_reportsEngineRequestHelper.getRequest(),
						"create-date"));
			}
		).build();
	}

	public PortletURL getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setNavigation(
			() -> {
				String navigation = ParamUtil.getString(
					_httpServletRequest, "navigation");

				if (Validator.isNotNull(navigation)) {
					return _getNavigation();
				}

				return null;
			}
		).setTabs1(
			_getTabs1()
		).buildPortletURL();
	}

	public String getReportParameters() throws JSONException {
		Definition definition = (Definition)_httpServletRequest.getAttribute(
			ReportsEngineWebKeys.DEFINITION);

		String reportParameters = BeanParamUtil.getString(
			definition, _httpServletRequest, "reportParameters");

		if (!JSONUtil.isJSONArray(reportParameters)) {
			return null;
		}

		JSONArray reportParametersJSONArray = JSONFactoryUtil.createJSONArray(
			reportParameters);

		for (int i = 0; i < reportParametersJSONArray.length(); i++) {
			JSONObject reportParameterJSONObject =
				reportParametersJSONArray.getJSONObject(i);

			if (reportParameterJSONObject.has("value")) {
				reportParameterJSONObject.put(
					"value",
					HtmlUtil.escapeJS(
						reportParameterJSONObject.getString("value")));
			}

			if (reportParameterJSONObject.has("key")) {
				reportParameterJSONObject.put(
					"key",
					HtmlUtil.escapeJS(
						reportParameterJSONObject.getString("key")));
			}
		}

		return reportParametersJSONArray.toString();
	}

	public SearchContainer<?> getSearchContainer() throws PortalException {
		if (_searchContainer == null) {
			if (isDefinitionsTabSelected()) {
				_searchContainer = _getDefinitionSearch();
			}
			else if (isReportsTabSelected()) {
				_searchContainer = _getEntrySearch();
			}
			else if (isSourcesTabSelected()) {
				_searchContainer = _getSourceSearch();
			}
		}

		return _searchContainer;
	}

	public String getSearchURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setParameter(
			"groupId",
			() -> {
				ThemeDisplay themeDisplay =
					_reportsEngineRequestHelper.getThemeDisplay();

				return themeDisplay.getScopeGroupId();
			}
		).buildString();
	}

	public String getSortingURL() {
		return PortletURLBuilder.createRenderURL(
			_reportsEngineRequestHelper.getLiferayPortletResponse()
		).setTabs1(
			_getTabs1()
		).setParameter(
			"orderByCol", _getOrderByCol()
		).setParameter(
			"orderByType",
			Objects.equals(getOrderByType(), "asc") ? "desc" : "asc"
		).buildString();
	}

	public int getTotalItems() throws PortalException {
		SearchContainer<?> searchContainer = getSearchContainer();

		return searchContainer.getTotal();
	}

	public ViewTypeItemList getViewTypes() {
		return new ViewTypeItemList(getPortletURL(), getDisplayStyle()) {
			{
				addTableViewTypeItem();
			}
		};
	}

	public boolean isAdminPortlet() {
		String portletName = _getPortletName();

		return portletName.equals(
			ReportsEngineConsolePortletKeys.REPORTS_ADMIN);
	}

	public boolean isDefinitionsTabSelected() {
		String tabs1 = _getTabs1();

		if (tabs1.equals("definitions")) {
			return true;
		}

		return false;
	}

	public boolean isDisabled() throws PortalException {
		if (getTotalItems() == 0) {
			return true;
		}

		return false;
	}

	public boolean isReportsTabSelected() {
		String tabs1 = _getTabs1();

		if (tabs1.equals("reports")) {
			return true;
		}

		return false;
	}

	public boolean isSourcesTabSelected() {
		String tabs1 = _getTabs1();

		if (tabs1.equals("sources")) {
			return true;
		}

		return false;
	}

	private DefinitionSearch _getDefinitionSearch() throws PortalException {
		DefinitionSearch definitionSearch = new DefinitionSearch(
			_reportsEngineRequestHelper.getRenderRequest(), getPortletURL());

		DefinitionDisplayTerms displayTerms =
			(DefinitionDisplayTerms)definitionSearch.getDisplayTerms();

		if (definitionSearch.isSearch()) {
			definitionSearch.setEmptyResultsMessage(
				"no-definitions-were-found");
		}

		if (displayTerms.isAdvancedSearch()) {
			definitionSearch.setResultsAndTotal(
				() -> DefinitionServiceUtil.getDefinitions(
					_themeDisplay.getSiteGroupId(),
					displayTerms.getDefinitionName(),
					displayTerms.getDescription(), displayTerms.getSourceId(),
					displayTerms.getReportName(), displayTerms.isAndOperator(),
					definitionSearch.getStart(), definitionSearch.getEnd(),
					definitionSearch.getOrderByComparator()),
				DefinitionServiceUtil.getDefinitionsCount(
					_themeDisplay.getSiteGroupId(),
					displayTerms.getDefinitionName(),
					displayTerms.getDescription(), displayTerms.getSourceId(),
					displayTerms.getReportName(),
					displayTerms.isAndOperator()));
		}
		else {
			definitionSearch.setResultsAndTotal(
				() -> DefinitionServiceUtil.getDefinitions(
					_themeDisplay.getSiteGroupId(), displayTerms.getKeywords(),
					displayTerms.getKeywords(), null,
					displayTerms.getKeywords(), false,
					definitionSearch.getStart(), definitionSearch.getEnd(),
					definitionSearch.getOrderByComparator()),
				DefinitionServiceUtil.getDefinitionsCount(
					_themeDisplay.getSiteGroupId(), displayTerms.getKeywords(),
					displayTerms.getKeywords(), null,
					displayTerms.getKeywords(), false));
		}

		return definitionSearch;
	}

	private EntrySearch _getEntrySearch() throws PortalException {
		EntrySearch entrySearch = new EntrySearch(
			_reportsEngineRequestHelper.getRenderRequest(), getPortletURL());

		EntryDisplayTerms displayTerms =
			(EntryDisplayTerms)entrySearch.getDisplayTerms();

		if (entrySearch.isSearch()) {
			entrySearch.setEmptyResultsMessage("no-reports-were-found");
		}

		if (displayTerms.isAdvancedSearch()) {
			Date startDate = PortalUtil.getDate(
				displayTerms.getStartDateMonth(),
				displayTerms.getStartDateDay(), displayTerms.getStartDateYear(),
				_themeDisplay.getTimeZone(), null);
			Date endDate = PortalUtil.getDate(
				displayTerms.getEndDateMonth(),
				displayTerms.getEndDateDay() + 1, displayTerms.getEndDateYear(),
				_themeDisplay.getTimeZone(), null);

			entrySearch.setResultsAndTotal(
				() -> EntryServiceUtil.getEntries(
					_themeDisplay.getSiteGroupId(),
					displayTerms.getDefinitionName(), null, startDate, endDate,
					displayTerms.isAndOperator(), entrySearch.getStart(),
					entrySearch.getEnd(), entrySearch.getOrderByComparator()),
				EntryServiceUtil.getEntriesCount(
					_themeDisplay.getSiteGroupId(),
					displayTerms.getDefinitionName(), null, startDate, endDate,
					displayTerms.isAndOperator()));
		}
		else {
			entrySearch.setResultsAndTotal(
				() -> EntryServiceUtil.getEntries(
					_themeDisplay.getSiteGroupId(), displayTerms.getKeywords(),
					null, null, null, false, entrySearch.getStart(),
					entrySearch.getEnd(), entrySearch.getOrderByComparator()),
				EntryServiceUtil.getEntriesCount(
					_themeDisplay.getSiteGroupId(), displayTerms.getKeywords(),
					null, null, null, false));
		}

		return entrySearch;
	}

	private String _getNavigation() {
		if (_navigation != null) {
			return _navigation;
		}

		_navigation = ParamUtil.getString(
			_httpServletRequest, "navigation", "all");

		return _navigation;
	}

	private String _getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_httpServletRequest, ReportsEngineConsolePortletKeys.REPORTS_ADMIN,
			"create-date");

		return _orderByCol;
	}

	private String _getPortletName() {
		return _reportsEngineRequestHelper.getPortletName();
	}

	private SourceSearch _getSourceSearch() throws PortalException {
		SourceSearch sourceSearch = new SourceSearch(
			_reportsEngineRequestHelper.getRenderRequest(), getPortletURL());

		SourceDisplayTerms displayTerms =
			(SourceDisplayTerms)sourceSearch.getDisplayTerms();

		if (sourceSearch.isSearch()) {
			sourceSearch.setEmptyResultsMessage("no-sources-were-found");
		}

		if (displayTerms.isAdvancedSearch()) {
			sourceSearch.setResultsAndTotal(
				() -> SourceServiceUtil.getSources(
					_themeDisplay.getSiteGroupId(), displayTerms.getName(),
					displayTerms.getDriverUrl(), displayTerms.isAndOperator(),
					sourceSearch.getStart(), sourceSearch.getEnd(),
					sourceSearch.getOrderByComparator()),
				SourceServiceUtil.getSourcesCount(
					_themeDisplay.getSiteGroupId(), displayTerms.getName(),
					displayTerms.getDriverUrl(), displayTerms.isAndOperator()));
		}
		else {
			sourceSearch.setResultsAndTotal(
				() -> SourceServiceUtil.getSources(
					_themeDisplay.getSiteGroupId(), displayTerms.getKeywords(),
					displayTerms.getKeywords(), false, sourceSearch.getStart(),
					sourceSearch.getEnd(), sourceSearch.getOrderByComparator()),
				SourceServiceUtil.getSourcesCount(
					_themeDisplay.getSiteGroupId(), displayTerms.getKeywords(),
					displayTerms.getKeywords(), false));
		}

		return sourceSearch;
	}

	private String _getTabs1() {
		return ParamUtil.getString(_liferayPortletRequest, "tabs1", "reports");
	}

	private static final String[] _DISPLAY_VIEWS = {"list"};

	private String _displayStyle;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private String _navigation;
	private String _orderByCol;
	private String _orderByType;
	private final ReportsEngineRequestHelper _reportsEngineRequestHelper;
	private SearchContainer<?> _searchContainer;
	private final ThemeDisplay _themeDisplay;

}