/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context;

import com.liferay.depot.util.SiteConnectedGroupGroupProviderUtil;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryTypeServiceUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemListBuilder;
import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Locale;

/**
 * @author Brian I. Kim
 */
public class DLViewMoreMenuItemsDisplayContext {

	public DLViewMoreMenuItemsDisplayContext(
		long folderId, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_folderId = folderId;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
	}

	public String getClearResultsURL() {
		return getSearchActionURL();
	}

	public String getDLFileEntryTypeScopeName(
			DLFileEntryType dlFileEntryType, Locale locale)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Group scopeGroup = themeDisplay.getScopeGroup();

		if (dlFileEntryType.getGroupId() == scopeGroup.getGroupId()) {
			if (scopeGroup.isDepot()) {
				return LanguageUtil.get(
					_httpServletRequest, "current-asset-library");
			}

			return LanguageUtil.get(_httpServletRequest, "current-site");
		}

		Group dlFileEntryTypeGroup = GroupLocalServiceUtil.getGroup(
			dlFileEntryType.getGroupId());

		return dlFileEntryTypeGroup.getName(locale);
	}

	public String getEventName() {
		if (_eventName != null) {
			return _eventName;
		}

		_eventName = ParamUtil.getString(
			_renderRequest, "eventName",
			_renderResponse.getNamespace() + "selectAddMenuItem");

		return _eventName;
	}

	public List<NavigationItem> getNavigationItems() {
		return NavigationItemListBuilder.add(
			navigationItem -> {
				navigationItem.setActive(true);
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "document-types"));
			}
		).build();
	}

	public PortletURL getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCPath(
			"/document_library/view_more_menu_items.jsp"
		).setParameter(
			"eventName", getEventName()
		).setParameter(
			"folderId", _folderId
		).buildPortletURL();
	}

	public String getSearchActionURL() {
		return String.valueOf(getPortletURL());
	}

	public SearchContainer<DLFileEntryType> getSearchContainer()
		throws PortalException {

		if (_searchContainer != null) {
			return _searchContainer;
		}

		SearchContainer<DLFileEntryType> searchContainer = new SearchContainer(
			_renderRequest, new DisplayTerms(_httpServletRequest),
			new DisplayTerms(_httpServletRequest),
			SearchContainer.DEFAULT_CUR_PARAM, SearchContainer.DEFAULT_DELTA,
			getPortletURL(), null,
			LanguageUtil.get(_httpServletRequest, "there-are-no-results"));

		DisplayTerms displayTerms = searchContainer.getSearchTerms();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		long folderId = _getPrimaryFolderId(_folderId);
		boolean includeBasicFileEntryType = ParamUtil.getBoolean(
			_renderRequest, "includeBasicFileEntryType");

		searchContainer.setResultsAndTotal(
			() -> DLFileEntryTypeServiceUtil.search(
				themeDisplay.getCompanyId(), folderId,
				SiteConnectedGroupGroupProviderUtil.
					getCurrentAndAncestorSiteAndDepotGroupIds(
						themeDisplay.getScopeGroupId(), false, true),
				displayTerms.getKeywords(), includeBasicFileEntryType,
				_inherited, searchContainer.getStart(),
				searchContainer.getEnd()),
			DLFileEntryTypeServiceUtil.searchCount(
				themeDisplay.getCompanyId(), folderId,
				SiteConnectedGroupGroupProviderUtil.
					getCurrentAndAncestorSiteAndDepotGroupIds(
						themeDisplay.getScopeGroupId(), false, true),
				displayTerms.getKeywords(), includeBasicFileEntryType,
				_inherited));

		_searchContainer = searchContainer;

		return _searchContainer;
	}

	public int getTotalItems() throws PortalException {
		SearchContainer<DLFileEntryType> searchContainer = getSearchContainer();

		return searchContainer.getTotal();
	}

	private long _getPrimaryFolderId(long folderId) throws PortalException {
		while (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			Folder folder = DLAppServiceUtil.getFolder(folderId);

			if ((folder != null) && (folder.getModel() instanceof DLFolder)) {
				DLFolder dlFolder = (DLFolder)folder.getModel();

				if (dlFolder.getRestrictionType() ==
						DLFolderConstants.
							RESTRICTION_TYPE_FILE_ENTRY_TYPES_AND_WORKFLOW) {

					_inherited = false;

					break;
				}

				folderId = dlFolder.getParentFolderId();
			}
		}

		return folderId;
	}

	private String _eventName;
	private final long _folderId;
	private final HttpServletRequest _httpServletRequest;
	private boolean _inherited = true;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private SearchContainer<DLFileEntryType> _searchContainer;

}