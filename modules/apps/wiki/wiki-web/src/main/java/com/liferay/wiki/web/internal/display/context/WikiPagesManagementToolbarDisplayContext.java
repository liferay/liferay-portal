/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.ViewTypeItemList;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.toolbar.contributor.PortletToolbarContributor;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.servlet.taglib.ui.Menu;
import com.liferay.portal.kernel.servlet.taglib.ui.URLMenuItem;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.trash.TrashHelper;
import com.liferay.wiki.constants.WikiWebKeys;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.web.internal.display.context.helper.WikiURLHelper;
import com.liferay.wiki.web.internal.security.permission.resource.WikiPagePermission;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Alejandro Tardín
 */
public class WikiPagesManagementToolbarDisplayContext {

	public WikiPagesManagementToolbarDisplayContext(
		String displayStyle, LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SearchContainer<WikiPage> searchContainer, TrashHelper trashHelper,
		WikiURLHelper wikiURLHelper) {

		_displayStyle = displayStyle;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_searchContainer = searchContainer;
		_trashHelper = trashHelper;
		_wikiURLHelper = wikiURLHelper;

		_currentURLObj = PortletURLUtil.getCurrent(
			liferayPortletRequest, liferayPortletResponse);

		_httpServletRequest = liferayPortletRequest.getHttpServletRequest();

		_themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData("action", "deletePages");
				dropdownItem.setIcon("trash");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "delete"));
				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	public Map<String, Object> getAdditionalProps() {
		return HashMapBuilder.<String, Object>put(
			"deletePagesCmd",
			() -> {
				if (_isTrashEnabled()) {
					return Constants.MOVE_TO_TRASH;
				}

				return Constants.DELETE;
			}
		).put(
			"deletePagesURL",
			() -> PortletURLBuilder.createActionURL(
				_liferayPortletResponse
			).setActionName(
				"/wiki/edit_page"
			).buildString()
		).put(
			"trashEnabled", _isTrashEnabled()
		).build();
	}

	public List<String> getAvailableActions(WikiPage wikiPage)
		throws PortalException {

		List<String> availableActions = new ArrayList<>();

		if (WikiPagePermission.contains(
				_themeDisplay.getPermissionChecker(), wikiPage,
				ActionKeys.DELETE)) {

			availableActions.add("deletePages");
		}

		return availableActions;
	}

	public PortletURL getClearResultsURL() {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCRenderCommandName(
			"/wiki/view_pages"
		).setRedirect(
			_currentURLObj
		).setParameter(
			"nodeId",
			() -> {
				WikiNode node = (WikiNode)_httpServletRequest.getAttribute(
					WikiWebKeys.WIKI_NODE);

				return node.getNodeId();
			}
		).buildPortletURL();
	}

	public CreationMenu getCreationMenu() {
		String keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		if (Validator.isNotNull(keywords)) {
			return null;
		}

		PortletToolbarContributor wikiPortletToolbarContributor =
			(PortletToolbarContributor)_httpServletRequest.getAttribute(
				WikiWebKeys.WIKI_PORTLET_TOOLBAR_CONTRIBUTOR);

		List<Menu> menus = wikiPortletToolbarContributor.getPortletTitleMenus(
			_liferayPortletRequest, _liferayPortletResponse);

		if (menus.isEmpty()) {
			return null;
		}

		return new CreationMenu() {
			{
				for (Menu menu : menus) {
					List<URLMenuItem> urlMenuItems =
						(List<URLMenuItem>)(List<?>)menu.getMenuItems();

					for (URLMenuItem urlMenuItem : urlMenuItems) {
						addDropdownItem(
							dropdownItem -> {
								dropdownItem.setHref(urlMenuItem.getURL());
								dropdownItem.setLabel(urlMenuItem.getLabel());
							});
					}
				}
			}
		};
	}

	public List<DropdownItem> getFilterDropdownItems() {
		if (Validator.isNotNull(
				ParamUtil.getString(_httpServletRequest, "keywords"))) {

			return null;
		}

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
			() -> !navigation.equals("all-pages"),
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						PortletURLUtil.clone(
							_getPortletURL(), _liferayPortletResponse)
					).setNavigation(
						(String)null
					).buildString());

				labelItem.setCloseable(true);
				labelItem.setLabel(
					LanguageUtil.get(_httpServletRequest, navigation));
			}
		).build();
	}

	public List<DropdownItem> getOrderByDropdownItems()
		throws PortletException {

		return new DropdownItemList() {
			{
				Map<String, String> orderColumns = HashMapBuilder.put(
					"modifiedDate", "modified-date"
				).put(
					"title", "title"
				).build();

				PortletURL portletURL = _getPortletURL();

				for (Map.Entry<String, String> orderByColEntry :
						orderColumns.entrySet()) {

					String orderByCol = orderByColEntry.getKey();

					add(
						dropdownItem -> {
							dropdownItem.setActive(
								orderByCol.equals(_getOrderByCol()));

							PortletURL orderByPortletURL = PortletURLUtil.clone(
								portletURL, _liferayPortletResponse);

							dropdownItem.setHref(
								orderByPortletURL, "orderByCol", orderByCol);

							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest,
									orderByColEntry.getValue()));
						});
				}
			}
		};
	}

	public PortletURL getSearchActionURL() {
		return PortletURLBuilder.create(
			_wikiURLHelper.getSearchURL()
		).setRedirect(
			_currentURLObj
		).setParameter(
			"nodeId",
			() -> {
				WikiNode node = (WikiNode)_httpServletRequest.getAttribute(
					WikiWebKeys.WIKI_NODE);

				return node.getNodeId();
			}
		).buildPortletURL();
	}

	public String getSortingOrder() {
		return _getOrderByType();
	}

	public PortletURL getSortingURL() throws PortletException {
		return PortletURLBuilder.create(
			_getPortletURL()
		).setParameter(
			"orderByCol", _getOrderByCol()
		).setParameter(
			"orderByType",
			Objects.equals(_getOrderByType(), "asc") ? "desc" : "asc"
		).buildPortletURL();
	}

	public int getTotalItems() {
		return _searchContainer.getTotal();
	}

	public ViewTypeItemList getViewTypes() {
		return new ViewTypeItemList(_currentURLObj, _displayStyle) {
			{
				addListViewTypeItem();
				addTableViewTypeItem();
			}
		};
	}

	public boolean isDisabled() {
		String navigation = _getNavigation();

		if (navigation.equals("all-pages") && !_searchContainer.hasResults()) {
			return true;
		}

		return false;
	}

	public boolean isSelectable() {
		return true;
	}

	public boolean isShowInfoButton() {
		return Validator.isNull(
			ParamUtil.getString(_httpServletRequest, "keywords"));
	}

	public boolean isShowSearch() {
		return true;
	}

	private List<DropdownItem> _getFilterNavigationDropdownItems()
		throws PortletException {

		return new DropdownItemList() {
			{
				String navigation = _getNavigation();

				String[] navigationKeys = {
					"all-pages", "draft-pages", "frontpage", "orphan-pages",
					"pending-pages", "recent-changes"
				};

				PortletURL portletURL = _getPortletURL();

				for (String navigationKey : navigationKeys) {
					add(
						dropdownItem -> {
							dropdownItem.setActive(
								navigation.equals(navigationKey));

							PortletURL navigationPortletURL =
								PortletURLUtil.clone(
									portletURL, _liferayPortletResponse);

							dropdownItem.setHref(
								navigationPortletURL, "navigation",
								navigationKey);

							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, navigationKey));
						});
				}
			}
		};
	}

	private String _getNavigation() {
		return ParamUtil.getString(
			_httpServletRequest, "navigation", "all-pages");
	}

	private String _getOrderByCol() {
		return _searchContainer.getOrderByCol();
	}

	private String _getOrderByType() {
		return _searchContainer.getOrderByType();
	}

	private PortletURL _getPortletURL() throws PortletException {
		return PortletURLBuilder.create(
			PortletURLUtil.clone(_currentURLObj, _liferayPortletResponse)
		).setMVCRenderCommandName(
			"/wiki/view_pages"
		).setRedirect(
			_currentURLObj
		).buildPortletURL();
	}

	private boolean _isTrashEnabled() {
		try {
			return _trashHelper.isTrashEnabled(
				PortalUtil.getScopeGroupId(_httpServletRequest));
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}
	}

	private final PortletURL _currentURLObj;
	private final String _displayStyle;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final SearchContainer<WikiPage> _searchContainer;
	private final ThemeDisplay _themeDisplay;
	private final TrashHelper _trashHelper;
	private final WikiURLHelper _wikiURLHelper;

}