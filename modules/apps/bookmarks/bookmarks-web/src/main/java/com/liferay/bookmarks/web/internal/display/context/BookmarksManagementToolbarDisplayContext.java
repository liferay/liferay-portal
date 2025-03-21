/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.web.internal.display.context;

import com.liferay.bookmarks.configuration.BookmarksGroupServiceOverriddenConfiguration;
import com.liferay.bookmarks.constants.BookmarksFolderConstants;
import com.liferay.bookmarks.constants.BookmarksPortletKeys;
import com.liferay.bookmarks.constants.BookmarksWebKeys;
import com.liferay.bookmarks.service.BookmarksFolderServiceUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.ViewTypeItemList;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.toolbar.contributor.PortletToolbarContributor;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.servlet.taglib.ui.Menu;
import com.liferay.portal.kernel.servlet.taglib.ui.URLMenuItem;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.trash.TrashHelper;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Alejandro Tardín
 */
public class BookmarksManagementToolbarDisplayContext {

	public BookmarksManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		BookmarksGroupServiceOverriddenConfiguration
			bookmarksGroupServiceOverriddenConfiguration,
		PortalPreferences portalPreferences, TrashHelper trashHelper) {

		_httpServletRequest = httpServletRequest;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_bookmarksGroupServiceOverriddenConfiguration =
			bookmarksGroupServiceOverriddenConfiguration;
		_portalPreferences = portalPreferences;
		_trashHelper = trashHelper;

		_currentURLObj = PortletURLUtil.getCurrent(
			liferayPortletRequest, liferayPortletResponse);
		_folderId = GetterUtil.getLong(
			(String)httpServletRequest.getAttribute("view.jsp-folderId"));
		_searchContainer = (SearchContainer)httpServletRequest.getAttribute(
			"view.jsp-bookmarksSearchContainer");
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData("action", "deleteEntries");
				dropdownItem.setIcon("trash");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "delete"));
				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	public String getClearResultsURL() {
		return String.valueOf(_getPortletURL());
	}

	public CreationMenu getCreationMenu() {
		String portletName = _liferayPortletRequest.getPortletName();

		if (!portletName.equals(BookmarksPortletKeys.BOOKMARKS_ADMIN)) {
			return null;
		}

		PortletToolbarContributor bookmarksPortletToolbarContributor =
			(PortletToolbarContributor)_httpServletRequest.getAttribute(
				BookmarksWebKeys.BOOKMARKS_PORTLET_TOOLBAR_CONTRIBUTOR);

		List<Menu> menus =
			bookmarksPortletToolbarContributor.getPortletTitleMenus(
				_liferayPortletRequest, _liferayPortletResponse);

		if (menus.isEmpty()) {
			return null;
		}

		CreationMenu creationMenu = new CreationMenu();

		for (Menu menu : menus) {
			List<URLMenuItem> urlMenuItems =
				(List<URLMenuItem>)(List<?>)menu.getMenuItems();

			for (URLMenuItem urlMenuItem : urlMenuItems) {
				creationMenu.addDropdownItem(
					dropdownItem -> {
						dropdownItem.setHref(urlMenuItem.getURL());
						dropdownItem.setLabel(urlMenuItem.getLabel());
					});
			}
		}

		return creationMenu;
	}

	public List<DropdownItem> getFilterDropdownItems() {
		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					_getFilterNavigationDropdownItems());
				dropdownGroupItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "filter-by"));
			}
		).build();
	}

	public List<LabelItem> getFilterLabelItems() {
		String navigation = _getNavigation();

		return LabelItemListBuilder.add(
			() -> navigation.equals("mine"),
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					_removeNavigartionParameter(_currentURLObj));

				labelItem.setCloseable(true);

				User user = _themeDisplay.getUser();

				labelItem.setLabel(
					String.format(
						"%s: %s",
						LanguageUtil.get(_httpServletRequest, "owner"),
						user.getFullName()));
			}
		).add(
			() -> navigation.equals("recent"),
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					_removeNavigartionParameter(_currentURLObj));

				labelItem.setCloseable(true);
				labelItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "recent"));
			}
		).build();
	}

	public String getSearchActionURL() {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCRenderCommandName(
			"/bookmarks/view"
		).setRedirect(
			PortalUtil.getCurrentURL(_httpServletRequest)
		).setParameter(
			"folderId", _folderId
		).buildString();
	}

	public String getSearchContainerId() {
		return ParamUtil.getString(_httpServletRequest, "searchContainerId");
	}

	public int getTotalItems() {
		return _searchContainer.getTotal();
	}

	public ViewTypeItemList getViewTypes() {
		int curEntry = ParamUtil.getInteger(_httpServletRequest, "curEntry");
		int deltaEntry = ParamUtil.getInteger(
			_httpServletRequest, "deltaEntry");

		String displayStyle = ParamUtil.getString(
			_httpServletRequest, "displayStyle");

		if (Validator.isNull(displayStyle)) {
			displayStyle = _portalPreferences.getValue(
				BookmarksPortletKeys.BOOKMARKS, "display-style", "descriptive");
		}

		String keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		PortletURL displayStyleURL = _liferayPortletResponse.createRenderURL();

		if (Validator.isNull(keywords)) {
			if (_folderId ==
					BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

				displayStyleURL.setParameter(
					"mvcRenderCommandName", "/bookmarks/view");
			}
			else {
				displayStyleURL.setParameter(
					"mvcRenderCommandName", "/bookmarks/view_folder");
				displayStyleURL.setParameter(
					"folderId", String.valueOf(_folderId));
			}
		}
		else {
			displayStyleURL.setParameter(
				"mvcRenderCommandName", "/bookmarks/view");
			displayStyleURL.setParameter("folderId", String.valueOf(_folderId));
		}

		displayStyleURL.setParameter(
			"navigation", HtmlUtil.escapeJS(_getNavigation()));

		if (curEntry > 0) {
			displayStyleURL.setParameter("curEntry", String.valueOf(curEntry));
		}

		if (deltaEntry > 0) {
			displayStyleURL.setParameter(
				"deltaEntry", String.valueOf(deltaEntry));
		}

		return new ViewTypeItemList(displayStyleURL, displayStyle) {
			{
				addListViewTypeItem();
				addTableViewTypeItem();
			}
		};
	}

	public boolean isDisabled() {
		int foldersAndEntriesCount =
			BookmarksFolderServiceUtil.getFoldersAndEntriesCount(
				_themeDisplay.getScopeGroupId(), _folderId);

		String navigation = _getNavigation();

		if ((foldersAndEntriesCount == 0) && navigation.equals("all")) {
			return true;
		}

		return false;
	}

	public boolean isSelectable() {
		return true;
	}

	public boolean isShowSearch() {
		return _bookmarksGroupServiceOverriddenConfiguration.
			showFoldersSearch();
	}

	private List<DropdownItem> _getFilterNavigationDropdownItems() {
		return new DropdownItemList() {
			{
				String[] navigationKeys = null;

				if (_themeDisplay.isSignedIn()) {
					navigationKeys = new String[] {"all", "recent", "mine"};
				}
				else {
					navigationKeys = new String[] {"all", "recent"};
				}

				PortletURL portletURL = PortletURLBuilder.create(
					_getPortletURL()
				).setParameter(
					"folderId",
					BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID
				).buildPortletURL();

				for (String navigationKey : navigationKeys) {
					add(
						dropdownItem -> {
							dropdownItem.setActive(
								navigationKey.equals(_getNavigation()));

							PortletURL navigationURL = PortletURLUtil.clone(
								portletURL, _liferayPortletResponse);

							dropdownItem.setHref(
								navigationURL, "navigation", navigationKey);

							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, navigationKey));
						});
				}
			}
		};
	}

	private String _getNavigation() {
		return ParamUtil.getString(_httpServletRequest, "navigation", "all");
	}

	private PortletURL _getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setParameter(
			"categoryId", StringPool.BLANK
		).setParameter(
			"deltaEntry",
			() -> {
				int deltaEntry = ParamUtil.getInteger(
					_httpServletRequest, "deltaEntry");

				if (deltaEntry > 0) {
					return deltaEntry;
				}

				return null;
			}
		).setParameter(
			"folderId", _folderId
		).setParameter(
			"tag", StringPool.BLANK
		).buildPortletURL();
	}

	private String _removeNavigartionParameter(PortletURL portletURL)
		throws PortletException {

		return PortletURLBuilder.create(
			PortletURLUtil.clone(portletURL, _liferayPortletResponse)
		).setNavigation(
			(String)null
		).buildString();
	}

	private final BookmarksGroupServiceOverriddenConfiguration
		_bookmarksGroupServiceOverriddenConfiguration;
	private final PortletURL _currentURLObj;
	private final long _folderId;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final PortalPreferences _portalPreferences;
	private final SearchContainer<Object> _searchContainer;
	private final ThemeDisplay _themeDisplay;
	private final TrashHelper _trashHelper;

}