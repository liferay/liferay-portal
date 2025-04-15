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
import com.liferay.portal.kernel.exception.PortalException;
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
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.redirect.model.RedirectEntry;
import com.liferay.redirect.model.RedirectEntryModel;
import com.liferay.redirect.service.RedirectEntryLocalService;
import com.liferay.redirect.service.RedirectEntryService;
import com.liferay.redirect.web.internal.search.RedirectEntrySearch;
import com.liferay.redirect.web.internal.util.RedirectUtil;
import com.liferay.redirect.web.internal.util.comparator.RedirectComparator;
import com.liferay.redirect.web.internal.util.comparator.RedirectDateComparator;
import com.liferay.staging.StagingGroupHelper;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletURL;
import jakarta.portlet.ResourceURL;

import jakarta.servlet.http.HttpServletRequest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Alejandro Tardín
 */
public class RedirectEntriesDisplayContext {

	public RedirectEntriesDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		ModelResourcePermission<RedirectEntry> modelResourcePermission,
		PortletResourcePermission portletResourcePermission,
		RedirectEntryLocalService redirectEntryLocalService,
		RedirectEntryService redirectEntryService,
		StagingGroupHelper stagingGroupHelper) {

		_httpServletRequest = httpServletRequest;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_modelResourcePermission = modelResourcePermission;
		_portletResourcePermission = portletResourcePermission;
		_redirectEntryLocalService = redirectEntryLocalService;
		_redirectEntryService = redirectEntryService;
		_stagingGroupHelper = stagingGroupHelper;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_expirationDateFormat = DateFormat.getDateInstance(
			SimpleDateFormat.SHORT, _themeDisplay.getLocale());
	}

	public String formatExpirationDate(Date expirationDate) {
		return _expirationDateFormat.format(expirationDate);
	}

	public DropdownItemList getActionDropdownItems(
		RedirectEntry redirectEntry) {

		return DropdownItemListBuilder.add(
			() -> _modelResourcePermission.contains(
				_themeDisplay.getPermissionChecker(), redirectEntry,
				ActionKeys.UPDATE),
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.createRenderURL(
						_liferayPortletResponse
					).setMVCRenderCommandName(
						"/redirect/edit_redirect_entry"
					).setRedirect(
						_getPortletURL()
					).setParameter(
						"redirectEntryId", redirectEntry.getRedirectEntryId()
					).buildRenderURL());
				dropdownItem.setIcon("pencil");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "edit"));
			}
		).add(
			() -> _modelResourcePermission.contains(
				_themeDisplay.getPermissionChecker(), redirectEntry,
				ActionKeys.DELETE),
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.createActionURL(
						_liferayPortletResponse
					).setActionName(
						"/redirect/delete_redirect_entry"
					).setParameter(
						"redirectEntryId", redirectEntry.getRedirectEntryId()
					).buildActionURL());
				dropdownItem.setIcon("trash");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "delete"));
			}
		).build();
	}

	public String getActionURL() throws Exception {
		SearchContainer<RedirectEntry> searchContainer = getSearchContainer();

		return String.valueOf(searchContainer.getIteratorURL());
	}

	public String getAvailableActions(RedirectEntry redirectEntry)
		throws PortalException {

		if (_modelResourcePermission.contains(
				_themeDisplay.getPermissionChecker(), redirectEntry,
				ActionKeys.DELETE)) {

			return "deleteSelectedRedirectEntries";
		}

		return StringPool.BLANK;
	}

	public String getExpirationDateInputValue(RedirectEntry redirectEntry) {
		if (redirectEntry == null) {
			return null;
		}

		Date expirationDate = redirectEntry.getExpirationDate();

		if (expirationDate == null) {
			return null;
		}

		DateFormat simpleDateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd", _themeDisplay.getLocale());

		return simpleDateFormat.format(expirationDate);
	}

	public RedirectEntriesManagementToolbarDisplayContext
			getRedirectManagementToolbarDisplayContext()
		throws Exception {

		return new RedirectEntriesManagementToolbarDisplayContext(
			_httpServletRequest, _liferayPortletRequest,
			_liferayPortletResponse, _portletResourcePermission,
			getSearchContainer());
	}

	public SearchContainer<RedirectEntry> getSearchContainer()
		throws Exception {

		if (_redirectEntrySearch != null) {
			return _redirectEntrySearch;
		}

		_redirectEntrySearch = new RedirectEntrySearch(
			_liferayPortletRequest, _liferayPortletResponse, _getPortletURL(),
			getSearchContainerId());

		if (_redirectEntrySearch.isSearch()) {
			_populateWithSearchIndex(_redirectEntrySearch);
		}
		else {
			_populateWithDatabase(_redirectEntrySearch);
		}

		return _redirectEntrySearch;
	}

	public String getSearchContainerId() {
		return "redirectEntries";
	}

	public String getSidebarPanelURL() {
		ResourceURL resourceURL = _liferayPortletResponse.createResourceURL();

		resourceURL.setResourceID("/redirect/info_panel");

		return resourceURL.toString();
	}

	public String getSourceURL(RedirectEntry redirectEntry) {
		return RedirectUtil.getGroupBaseURL(_themeDisplay) + StringPool.SLASH +
			redirectEntry.getSourceURL();
	}

	public boolean isLiveGroup() {
		if (_liveGroup != null) {
			return _liveGroup;
		}

		boolean liveGroup = false;

		if (_stagingGroupHelper.isLiveGroup(_themeDisplay.getScopeGroupId())) {
			liveGroup = true;
		}

		_liveGroup = liveGroup;

		return _liveGroup;
	}

	public boolean isStagingGroup() {
		if (_stagingGroup != null) {
			return _stagingGroup;
		}

		boolean stagingGroup = false;

		if (_stagingGroupHelper.isLocalStagingGroup(
				_themeDisplay.getScopeGroup()) ||
			_stagingGroupHelper.isRemoteStagingGroup(
				_themeDisplay.getScopeGroup())) {

			stagingGroup = true;
		}

		_stagingGroup = stagingGroup;

		return _stagingGroup;
	}

	private OrderByComparator<RedirectEntry> _getOrderByComparator() {
		boolean orderByAsc = StringUtil.equals(
			_redirectEntrySearch.getOrderByType(), "asc");

		if (Objects.equals(
				_redirectEntrySearch.getOrderByCol(), "source-url")) {

			return new RedirectComparator<>(
				"RedirectEntry", "sourceURL", RedirectEntryModel::getSourceURL,
				!orderByAsc);
		}

		if (Objects.equals(
				_redirectEntrySearch.getOrderByCol(), "destination-url")) {

			return new RedirectComparator<>(
				"RedirectEntry", "destinationURL",
				RedirectEntryModel::getDestinationURL, !orderByAsc);
		}

		if (Objects.equals(
				_redirectEntrySearch.getOrderByCol(), "latest-occurrence")) {

			return new RedirectDateComparator<>(
				"RedirectEntry", "lastOccurrenceDate",
				RedirectEntryModel::getModifiedDate, !orderByAsc);
		}

		if (Objects.equals(
				_redirectEntrySearch.getOrderByCol(), "modified-date")) {

			return new RedirectDateComparator<>(
				"RedirectEntry", "modifiedDate",
				RedirectEntryModel::getModifiedDate, !orderByAsc);
		}

		return new RedirectDateComparator<>(
			"RedirectEntry", "createDate", RedirectEntryModel::getCreateDate,
			!orderByAsc);
	}

	private PortletURL _getPortletURL() throws PortletException {
		return PortletURLUtil.clone(
			PortletURLUtil.getCurrent(
				_liferayPortletRequest, _liferayPortletResponse),
			_liferayPortletResponse);
	}

	private Sort _getSorts() {
		boolean orderByAsc = StringUtil.equals(
			_redirectEntrySearch.getOrderByType(), "asc");

		if (Objects.equals(
				_redirectEntrySearch.getOrderByCol(), "source-url")) {

			return new Sort(
				Field.getSortableFieldName("sourceURL"), Sort.STRING_TYPE,
				!orderByAsc);
		}

		if (Objects.equals(
				_redirectEntrySearch.getOrderByCol(), "destination-url")) {

			return new Sort(
				Field.getSortableFieldName("destinationURL"), Sort.STRING_TYPE,
				!orderByAsc);
		}

		if (Objects.equals(
				_redirectEntrySearch.getOrderByCol(), "latest-occurrence")) {

			return new Sort(
				Field.getSortableFieldName("lastOccurrenceDate"),
				Sort.LONG_TYPE, !orderByAsc);
		}

		if (Objects.equals(
				_redirectEntrySearch.getOrderByCol(), "modified-date")) {

			return new Sort(
				Field.getSortableFieldName(Field.MODIFIED_DATE), Sort.LONG_TYPE,
				!orderByAsc);
		}

		return new Sort(Field.CREATE_DATE, Sort.LONG_TYPE, !orderByAsc);
	}

	private void _populateWithDatabase(RedirectEntrySearch redirectEntrySearch)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		redirectEntrySearch.setResultsAndTotal(
			() -> _redirectEntryService.getRedirectEntries(
				themeDisplay.getScopeGroupId(), _redirectEntrySearch.getStart(),
				_redirectEntrySearch.getEnd(), _getOrderByComparator()),
			_redirectEntryService.getRedirectEntriesCount(
				themeDisplay.getScopeGroupId()));
	}

	private void _populateWithSearchIndex(
			RedirectEntrySearch redirectEntrySearch)
		throws Exception {

		Indexer<RedirectEntry> indexer = IndexerRegistryUtil.getIndexer(
			RedirectEntry.class);

		SearchContext searchContext = SearchContextFactory.getInstance(
			PortalUtil.getHttpServletRequest(_liferayPortletRequest));

		searchContext.setAttribute(Field.STATUS, WorkflowConstants.STATUS_ANY);
		searchContext.setAttribute(
			"groupBaseURL", RedirectUtil.getGroupBaseURL(_themeDisplay));
		searchContext.setEnd(redirectEntrySearch.getEnd());
		searchContext.setSorts(_getSorts());
		searchContext.setStart(redirectEntrySearch.getStart());

		Hits hits = indexer.search(searchContext);

		List<SearchResult> searchResults = SearchResultUtil.getSearchResults(
			hits, LocaleUtil.getDefault());

		redirectEntrySearch.setResultsAndTotal(
			() -> TransformUtil.transform(
				searchResults,
				searchResult -> _redirectEntryLocalService.fetchRedirectEntry(
					searchResult.getClassPK())),
			hits.getLength());
	}

	private final DateFormat _expirationDateFormat;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private Boolean _liveGroup;
	private final ModelResourcePermission<RedirectEntry>
		_modelResourcePermission;
	private final PortletResourcePermission _portletResourcePermission;
	private final RedirectEntryLocalService _redirectEntryLocalService;
	private RedirectEntrySearch _redirectEntrySearch;
	private final RedirectEntryService _redirectEntryService;
	private Boolean _stagingGroup;
	private final StagingGroupHelper _stagingGroupHelper;
	private final ThemeDisplay _themeDisplay;

}