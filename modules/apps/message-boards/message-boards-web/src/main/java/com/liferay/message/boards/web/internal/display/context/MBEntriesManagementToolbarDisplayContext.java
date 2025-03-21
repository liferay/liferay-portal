/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.util.comparator.CategoryModifiedDateComparator;
import com.liferay.message.boards.util.comparator.CategoryTitleComparator;
import com.liferay.message.boards.util.comparator.MBObjectsComparator;
import com.liferay.message.boards.util.comparator.ThreadModifiedDateComparator;
import com.liferay.message.boards.util.comparator.ThreadTitleComparator;
import com.liferay.message.boards.web.internal.security.permission.MBCategoryPermission;
import com.liferay.message.boards.web.internal.security.permission.MBMessagePermission;
import com.liferay.message.boards.web.internal.util.MBUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.trash.TrashHelper;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Sergio González
 */
public class MBEntriesManagementToolbarDisplayContext {

	public MBEntriesManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse, PortletURL currentURLObj,
		TrashHelper trashHelper) {

		_httpServletRequest = httpServletRequest;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_currentURLObj = currentURLObj;
		_trashHelper = trashHelper;

		_portalPreferences = PortletPreferencesFactoryUtil.getPortalPreferences(
			liferayPortletRequest);
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
		).add(
			dropdownItem -> {
				dropdownItem.putData("action", "lockEntries");
				dropdownItem.setIcon("lock");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "lock"));
				dropdownItem.setQuickAction(true);
			}
		).add(
			dropdownItem -> {
				dropdownItem.putData("action", "unlockEntries");
				dropdownItem.setIcon("unlock");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "unlock"));
				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	public Map<String, Object> getAdditionalProps() {
		return HashMapBuilder.<String, Object>put(
			"deleteEntriesCmd",
			() -> {
				if (_trashHelper.isTrashEnabled(
						_themeDisplay.getScopeGroupId())) {

					return Constants.MOVE_TO_TRASH;
				}

				return Constants.DELETE;
			}
		).put(
			"editEntryURL",
			() -> PortletURLBuilder.createActionURL(
				_liferayPortletResponse
			).setActionName(
				"/message_boards/edit_entry"
			).buildString()
		).put(
			"lockCmd", Constants.LOCK
		).put(
			"trashEnabled",
			() -> _trashHelper.isTrashEnabled(_themeDisplay.getScopeGroupId())
		).put(
			"unlockCmd", Constants.UNLOCK
		).build();
	}

	public List<String> getAvailableActions(MBCategory category)
		throws PortalException {

		List<String> availableActions = new ArrayList<>();

		if (MBCategoryPermission.contains(
				_themeDisplay.getPermissionChecker(), category,
				ActionKeys.DELETE)) {

			availableActions.add("deleteEntries");
		}

		return availableActions;
	}

	public List<String> getAvailableActions(MBMessage message)
		throws PortalException {

		List<String> availableActions = new ArrayList<>();

		PermissionChecker permissionChecker =
			_themeDisplay.getPermissionChecker();

		if (MBMessagePermission.contains(
				permissionChecker, message, ActionKeys.DELETE)) {

			availableActions.add("deleteEntries");
		}

		if (MBCategoryPermission.contains(
				permissionChecker, message.getGroupId(),
				message.getCategoryId(), ActionKeys.LOCK_THREAD)) {

			MBThread thread = message.getThread();

			if ((thread != null) && thread.isLocked()) {
				availableActions.add("unlockEntries");
			}
			else {
				availableActions.add("lockEntries");
			}
		}

		return availableActions;
	}

	public CreationMenu getCreationMenu() throws PortalException {
		CreationMenu creationMenu = null;

		long categoryId = MBUtil.getCategoryId(
			_httpServletRequest,
			(MBCategory)_httpServletRequest.getAttribute(
				WebKeys.MESSAGE_BOARDS_CATEGORY));

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (MBCategoryPermission.contains(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroupId(), categoryId,
				ActionKeys.ADD_CATEGORY)) {

			if (creationMenu == null) {
				creationMenu = new CreationMenu();
			}

			creationMenu.addDropdownItem(
				dropdownItem -> {
					dropdownItem.setHref(
						_liferayPortletResponse.createRenderURL(),
						"mvcRenderCommandName", "/message_boards/edit_category",
						"redirect", _currentURLObj.toString(),
						"parentCategoryId", String.valueOf(categoryId));
					dropdownItem.setLabel(
						LanguageUtil.get(
							_httpServletRequest, "category[message-board]"));
				});
		}

		if (MBCategoryPermission.contains(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroupId(), categoryId,
				ActionKeys.ADD_MESSAGE)) {

			if (creationMenu == null) {
				creationMenu = new CreationMenu();
			}

			creationMenu.addDropdownItem(
				dropdownItem -> {
					dropdownItem.setHref(
						_liferayPortletResponse.createRenderURL(),
						"mvcRenderCommandName", "/message_boards/edit_message",
						"redirect", _currentURLObj.toString(), "mbCategoryId",
						String.valueOf(categoryId));
					dropdownItem.setLabel(
						LanguageUtil.get(_httpServletRequest, "thread"));
				});
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
		String entriesNavigation = _getEntriesNavigation();

		return LabelItemListBuilder.add(
			() ->
				entriesNavigation.equals("threads") ||
				entriesNavigation.equals("categories"),
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						PortletURLUtil.clone(
							_currentURLObj, _liferayPortletResponse)
					).setParameter(
						"entriesNavigation", (String)null
					).buildString());

				labelItem.setCloseable(true);
				labelItem.setLabel(
					LanguageUtil.get(_httpServletRequest, entriesNavigation));
			}
		).build();
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_httpServletRequest, MBPortletKeys.MESSAGE_BOARDS_ADMIN,
			"modified-date");

		return _orderByCol;
	}

	public List<DropdownItem> getOrderByDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setActive(
					Objects.equals(getOrderByCol(), "title"));
				dropdownItem.setHref(
					_getCurrentSortingURL(), "orderByCol", "title");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "title"));
			}
		).add(
			dropdownItem -> {
				dropdownItem.setActive(
					Objects.equals(getOrderByCol(), "modified-date"));
				dropdownItem.setHref(
					_getCurrentSortingURL(), "orderByCol", "modified-date");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "modified-date"));
			}
		).build();
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest, MBPortletKeys.MESSAGE_BOARDS_ADMIN, "asc");

		return _orderByType;
	}

	public PortletURL getPortletURL() {
		long categoryId = MBUtil.getCategoryId(
			_httpServletRequest,
			(MBCategory)_httpServletRequest.getAttribute(
				WebKeys.MESSAGE_BOARDS_CATEGORY));

		PortletURL portletURL = _liferayPortletResponse.createRenderURL();

		if (categoryId == MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) {
			portletURL.setParameter(
				"mvcRenderCommandName", "/message_boards/view");
		}
		else {
			portletURL.setParameter(
				"mvcRenderCommandName", "/message_boards/view_category");
			portletURL.setParameter("mbCategoryId", String.valueOf(categoryId));
		}

		String keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		if (Validator.isNotNull(keywords)) {
			portletURL.setParameter("keywords", keywords);
		}

		String orderByCol = getOrderByCol();

		if (Validator.isNotNull(orderByCol)) {
			portletURL.setParameter("orderByCol", orderByCol);
		}

		String orderByType = getOrderByType();

		if (Validator.isNotNull(orderByType)) {
			portletURL.setParameter("orderByType", orderByType);
		}

		return portletURL;
	}

	public String getSearchActionURL() {
		long categoryId = MBUtil.getCategoryId(
			_httpServletRequest,
			(MBCategory)_httpServletRequest.getAttribute(
				WebKeys.MESSAGE_BOARDS_CATEGORY));

		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCRenderCommandName(
			"/message_boards_admin/search"
		).setRedirect(
			_currentURLObj
		).setParameter(
			"breadcrumbsCategoryId", categoryId
		).setParameter(
			"searchCategoryId", categoryId
		).buildString();
	}

	public PortletURL getSortingURL() throws PortletException {
		return PortletURLBuilder.create(
			_getCurrentSortingURL()
		).setParameter(
			"orderByType",
			Objects.equals(getOrderByType(), "asc") ? "desc" : "asc"
		).buildPortletURL();
	}

	public void populateOrder(SearchContainer searchContainer) {
		OrderByComparator orderByComparator = null;

		String orderByCol = getOrderByCol();

		searchContainer.setOrderByCol(orderByCol);

		boolean orderByAsc = false;

		if (Objects.equals(getOrderByType(), "asc")) {
			orderByAsc = true;
		}

		String entriesNavigation = _getEntriesNavigation();

		if (entriesNavigation.equals("all")) {
			orderByComparator = MBObjectsComparator.getInstance(orderByAsc);
		}
		else if (entriesNavigation.equals("threads")) {
			if (orderByCol.equals("modified-date")) {
				orderByComparator = ThreadModifiedDateComparator.getInstance(
					orderByAsc);
			}
			else if (orderByCol.equals("title")) {
				orderByComparator = ThreadTitleComparator.getInstance(
					orderByAsc);
			}
		}
		else if (entriesNavigation.equals("categories")) {
			if (orderByCol.equals("modified-date")) {
				orderByComparator = CategoryModifiedDateComparator.getInstance(
					orderByAsc);
			}
			else if (orderByCol.equals("title")) {
				orderByComparator = CategoryTitleComparator.getInstance(
					orderByAsc);
			}
		}

		searchContainer.setOrderByComparator(orderByComparator);
		searchContainer.setOrderByType(getOrderByType());
	}

	private PortletURL _getCurrentSortingURL() throws PortletException {
		PortletURL sortingURL = PortletURLUtil.clone(
			_currentURLObj, _liferayPortletResponse);

		long categoryId = MBUtil.getCategoryId(
			_httpServletRequest,
			(MBCategory)_httpServletRequest.getAttribute(
				WebKeys.MESSAGE_BOARDS_CATEGORY));

		if (categoryId == MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) {
			sortingURL.setParameter(
				"mvcRenderCommandName", "/message_boards/view");
		}
		else {
			sortingURL.setParameter(
				"mvcRenderCommandName", "/message_boards/view_category");
			sortingURL.setParameter("mbCategoryId", String.valueOf(categoryId));
		}

		sortingURL.setParameter(SearchContainer.DEFAULT_CUR_PARAM, "0");

		String keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		if (Validator.isNotNull(keywords)) {
			sortingURL.setParameter("keywords", keywords);
		}

		return sortingURL;
	}

	private String _getEntriesNavigation() {
		return ParamUtil.getString(
			_httpServletRequest, "entriesNavigation", "all");
	}

	private List<DropdownItem> _getFilterNavigationDropdownItems() {
		String entriesNavigation = _getEntriesNavigation();

		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setActive(entriesNavigation.equals("all"));

				PortletURL navigationPortletURL = PortletURLUtil.clone(
					_currentURLObj, _liferayPortletResponse);

				dropdownItem.setHref(
					navigationPortletURL, "entriesNavigation", "all");

				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "all"));
			}
		).add(
			dropdownItem -> {
				dropdownItem.setActive(entriesNavigation.equals("threads"));

				PortletURL navigationPortletURL = PortletURLUtil.clone(
					_currentURLObj, _liferayPortletResponse);

				dropdownItem.setHref(
					navigationPortletURL, "entriesNavigation", "threads");

				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "threads"));
			}
		).add(
			dropdownItem -> {
				dropdownItem.setActive(entriesNavigation.equals("categories"));

				PortletURL navigationPortletURL = PortletURLUtil.clone(
					_currentURLObj, _liferayPortletResponse);

				dropdownItem.setHref(
					navigationPortletURL, "entriesNavigation", "categories");

				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "categories"));
			}
		).build();
	}

	private final PortletURL _currentURLObj;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private String _orderByCol;
	private String _orderByType;
	private final PortalPreferences _portalPreferences;
	private final ThemeDisplay _themeDisplay;
	private final TrashHelper _trashHelper;

}