/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.redirect.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.search.SearchResult;
import com.liferay.portal.kernel.search.SearchResultUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.redirect.model.RedirectNotFoundEntry;
import com.liferay.redirect.service.RedirectNotFoundEntryLocalService;
import com.liferay.redirect.web.internal.search.RedirectNotFoundEntrySearch;
import com.liferay.redirect.web.internal.util.RedirectUtil;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.time.Duration;
import java.time.Instant;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Alejandro Tardín
 */
public class RedirectNotFoundEntriesDisplayContext {

	public RedirectNotFoundEntriesDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		PortletResourcePermission portletResourcePermission,
		RedirectNotFoundEntryLocalService redirectNotFoundEntryLocalService) {

		_httpServletRequest = httpServletRequest;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_portletResourcePermission = portletResourcePermission;
		_redirectNotFoundEntryLocalService = redirectNotFoundEntryLocalService;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public DropdownItemList getActionDropdownItems(
		RedirectNotFoundEntry redirectNotFoundEntry) {

		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.createActionURL(
						_liferayPortletResponse
					).setActionName(
						"/redirect/edit_redirect_not_found_entry"
					).setRedirect(
						_themeDisplay.getURLCurrent()
					).setParameter(
						"ignored", !redirectNotFoundEntry.isIgnored()
					).setParameter(
						"redirectNotFoundEntryId",
						redirectNotFoundEntry.getRedirectNotFoundEntryId()
					).buildActionURL());

				String label = "ignore";

				if (redirectNotFoundEntry.isIgnored()) {
					label = "unignore";
				}

				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, label));
			}
		).add(
			() -> {
				StagingGroupHelper stagingGroupHelper =
					StagingGroupHelperUtil.getStagingGroupHelper();

				return _portletResourcePermission.contains(
					_themeDisplay.getPermissionChecker(),
					_themeDisplay.getScopeGroupId(), ActionKeys.ADD_ENTRY) &&
					   !(stagingGroupHelper.isLocalStagingGroup(
						   redirectNotFoundEntry.getGroupId()) ||
						 stagingGroupHelper.isRemoteStagingGroup(
							 redirectNotFoundEntry.getGroupId()));
			},
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.createRenderURL(
						_liferayPortletResponse
					).setMVCRenderCommandName(
						"/redirect/edit_redirect_entry"
					).setRedirect(
						_themeDisplay.getURLCurrent()
					).setParameter(
						"sourceURL", redirectNotFoundEntry.getUrl()
					).buildRenderURL());
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "create-redirect"));
			}
		).build();
	}

	public String getActionURL() throws Exception {
		SearchContainer<RedirectNotFoundEntry> searchContainer =
			getSearchContainer();

		return String.valueOf(searchContainer.getIteratorURL());
	}

	public String getAvailableActions(
		RedirectNotFoundEntry redirectNotFoundEntry) {

		if (redirectNotFoundEntry.isIgnored()) {
			return "unignoreSelectedRedirectNotFoundEntries";
		}

		return "ignoreSelectedRedirectNotFoundEntries";
	}

	public String getEmptyResultsMessage() throws Exception {
		SearchContainer<RedirectNotFoundEntry> searchContainer =
			getSearchContainer();

		return searchContainer.getEmptyResultsMessage();
	}

	public RedirectNotFoundEntriesManagementToolbarDisplayContext
			getRedirectNotFoundEntriesManagementToolbarDisplayContext()
		throws Exception {

		return new RedirectNotFoundEntriesManagementToolbarDisplayContext(
			_httpServletRequest, _liferayPortletRequest,
			_liferayPortletResponse, _redirectNotFoundEntryLocalService,
			getSearchContainer());
	}

	public SearchContainer<RedirectNotFoundEntry> getSearchContainer()
		throws Exception {

		if (_redirectNotFoundEntrySearch != null) {
			return _redirectNotFoundEntrySearch;
		}

		_redirectNotFoundEntrySearch = new RedirectNotFoundEntrySearch(
			_liferayPortletRequest, _liferayPortletResponse, _getPortletURL(),
			getSearchContainerId());

		_populateWithSearchIndex(_redirectNotFoundEntrySearch);

		return _redirectNotFoundEntrySearch;
	}

	public String getSearchContainerId() {
		return "redirectNotFoundEntries";
	}

	public String getURL(RedirectNotFoundEntry redirectNotFoundEntry) {
		return RedirectUtil.getGroupBaseURL(_themeDisplay) + StringPool.SLASH +
			redirectNotFoundEntry.getUrl();
	}

	public boolean hasResults() throws Exception {
		SearchContainer<RedirectNotFoundEntry> searchContainer =
			getSearchContainer();

		if (searchContainer.getTotal() == 0) {
			return false;
		}

		return true;
	}

	private Boolean _getIgnored() {
		String filterType = ParamUtil.getString(
			_httpServletRequest, "filterType", "active-urls");

		if (filterType.equals("all")) {
			return null;
		}

		return filterType.equals("ignored-urls");
	}

	private Date _getMinModifiedDate() {
		int days = Integer.valueOf(
			ParamUtil.getString(_httpServletRequest, "filterDate", "0"));

		if (days == 0) {
			return null;
		}

		Instant instant = Instant.now();

		return Date.from(instant.minus(Duration.ofDays(days)));
	}

	private PortletURL _getPortletURL() throws Exception {
		return PortletURLUtil.clone(
			PortletURLUtil.getCurrent(
				_liferayPortletRequest, _liferayPortletResponse),
			_liferayPortletResponse);
	}

	private Sort _getSorts() {
		if (Objects.equals(
				_redirectNotFoundEntrySearch.getOrderByCol(),
				"modified-date")) {

			return new Sort(
				Field.getSortableFieldName(Field.MODIFIED_DATE), Sort.LONG_TYPE,
				StringUtil.equals(
					_redirectNotFoundEntrySearch.getOrderByType(), "asc"));
		}

		return new Sort(
			Field.getSortableFieldName("requestCount"), Sort.LONG_TYPE,
			StringUtil.equals(
				_redirectNotFoundEntrySearch.getOrderByType(), "asc"));
	}

	private void _populateWithSearchIndex(
			RedirectNotFoundEntrySearch redirectNotFoundEntrySearch)
		throws Exception {

		Indexer<RedirectNotFoundEntry> indexer = IndexerRegistryUtil.getIndexer(
			RedirectNotFoundEntry.class);

		SearchContext searchContext = SearchContextFactory.getInstance(
			PortalUtil.getHttpServletRequest(_liferayPortletRequest));

		searchContext.setAttribute(Field.STATUS, WorkflowConstants.STATUS_ANY);
		searchContext.setAttribute(
			"groupBaseURL", RedirectUtil.getGroupBaseURL(_themeDisplay));
		searchContext.setAttribute("ignored", _getIgnored());
		searchContext.setAttribute("minModifiedDate", _getMinModifiedDate());
		searchContext.setEnd(redirectNotFoundEntrySearch.getEnd());
		searchContext.setSorts(_getSorts());
		searchContext.setStart(redirectNotFoundEntrySearch.getStart());

		Hits hits = indexer.search(searchContext);

		List<SearchResult> searchResults = SearchResultUtil.getSearchResults(
			hits, LocaleUtil.getDefault());

		redirectNotFoundEntrySearch.setResultsAndTotal(
			() -> TransformUtil.transform(
				searchResults,
				searchResult ->
					_redirectNotFoundEntryLocalService.
						fetchRedirectNotFoundEntry(searchResult.getClassPK())),
			hits.getLength());
	}

	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final PortletResourcePermission _portletResourcePermission;
	private final RedirectNotFoundEntryLocalService
		_redirectNotFoundEntryLocalService;
	private RedirectNotFoundEntrySearch _redirectNotFoundEntrySearch;
	private final ThemeDisplay _themeDisplay;

}