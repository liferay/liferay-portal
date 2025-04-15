/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.groups.admin.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.ViewTypeItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.ViewTypeItemList;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.UserGroupServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.PortalPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.user.groups.admin.constants.UserGroupsAdminPortletKeys;
import com.liferay.user.groups.admin.search.UnsetUserUserGroupChecker;
import com.liferay.users.admin.item.selector.UserUserGroupItemSelectorCriterion;
import com.liferay.users.admin.search.UserSearch;
import com.liferay.users.admin.search.UserSearchTerms;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Pei-Jung Lan
 */
public class EditUserGroupAssignmentsManagementToolbarDisplayContext {

	public EditUserGroupAssignmentsManagementToolbarDisplayContext(
			HttpServletRequest httpServletRequest, RenderRequest renderRequest,
			RenderResponse renderResponse, String displayStyle, String mvcPath)
		throws PortalException {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
		_displayStyle = displayStyle;
		_mvcPath = mvcPath;

		_userGroup = UserGroupServiceUtil.fetchUserGroup(
			ParamUtil.getLong(httpServletRequest, "userGroupId"));
	}

	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			() -> _hasAddUserGroupPermission(),
			dropdownItem -> {
				dropdownItem.putData("action", "removeUsers");
				dropdownItem.setIcon("times-circle");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "remove"));
				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	public Map<String, Object> getAdditionalProps() {
		return HashMapBuilder.<String, Object>put(
			"editUserGroupAssignmentsURL",
			PortletURLBuilder.createActionURL(
				_renderResponse
			).setActionName(
				"editUserGroupAssignments"
			).buildString()
		).put(
			"portletURL", String.valueOf(getPortletURL())
		).put(
			"selectUsersURL",
			() -> {
				ItemSelector itemSelector =
					(ItemSelector)_httpServletRequest.getAttribute(
						ItemSelector.class.getName());

				UserUserGroupItemSelectorCriterion
					userUserGroupItemSelectorCriterion =
						new UserUserGroupItemSelectorCriterion();

				userUserGroupItemSelectorCriterion.
					setDesiredItemSelectorReturnTypes(
						new UUIDItemSelectorReturnType());
				userUserGroupItemSelectorCriterion.setUserGroupId(
					_userGroup.getUserGroupId());

				return String.valueOf(
					itemSelector.getItemSelectorURL(
						RequestBackedPortletURLFactoryUtil.create(
							_httpServletRequest),
						_renderResponse.getNamespace() + "selectUsers",
						userUserGroupItemSelectorCriterion));
			}
		).put(
			"userGroupName", () -> HtmlUtil.escape(_userGroup.getName())
		).build();
	}

	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).buildString();
	}

	public CreationMenu getCreationMenu() {
		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.putData("action", "addUsers");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "add-users"));
			}
		).build();
	}

	public String getKeywords() {
		if (Validator.isNull(_keywords)) {
			_keywords = ParamUtil.getString(_httpServletRequest, "keywords");
		}

		return _keywords;
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_httpServletRequest, UserGroupsAdminPortletKeys.USER_GROUPS_ADMIN,
			"edit-user-groups-order-by-col", "first-name");

		return _orderByCol;
	}

	public List<DropdownItem> getOrderByDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setActive(
					Objects.equals(getOrderByCol(), "first-name"));
				dropdownItem.setHref(
					getPortletURL(), "orderByCol", "first-name");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "first-name"));
			}
		).add(
			dropdownItem -> {
				dropdownItem.setActive(
					Objects.equals(getOrderByCol(), "screen-name"));
				dropdownItem.setHref(
					getPortletURL(), "orderByCol", "screen-name");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "screen-name"));
			}
		).build();
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest, UserGroupsAdminPortletKeys.USER_GROUPS_ADMIN,
			"edit-user-groups-order-by-type", "asc");

		return _orderByType;
	}

	public PortletURL getPortletURL() {
		PortletURL portletURL = PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCPath(
			_mvcPath
		).setRedirect(
			ParamUtil.getString(_httpServletRequest, "redirect")
		).setKeywords(
			() -> {
				if (Validator.isNotNull(getKeywords())) {
					return getKeywords();
				}

				return null;
			}
		).setParameter(
			"displayStyle", _displayStyle
		).setParameter(
			"orderByCol", getOrderByCol()
		).setParameter(
			"orderByType", getOrderByType()
		).setParameter(
			"userGroupId", _userGroup.getUserGroupId()
		).buildPortletURL();

		if (_userSearch != null) {
			portletURL.setParameter(
				_userSearch.getCurParam(),
				String.valueOf(_userSearch.getCur()));
			portletURL.setParameter(
				_userSearch.getDeltaParam(),
				String.valueOf(_userSearch.getDelta()));
		}

		return portletURL;
	}

	public String getSearchActionURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setRedirect(
			PortalUtil.getCurrentURL(_httpServletRequest)
		).buildString();
	}

	public SearchContainer<User> getSearchContainer(
			LinkedHashMap<String, Object> userParams)
		throws Exception {

		if (_userSearch != null) {
			return _userSearch;
		}

		UserSearch userSearch = new UserSearch(_renderRequest, getPortletURL());

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		UserSearchTerms searchTerms =
			(UserSearchTerms)userSearch.getSearchTerms();

		userSearch.setResultsAndTotal(
			() -> UserLocalServiceUtil.search(
				themeDisplay.getCompanyId(), searchTerms.getKeywords(),
				searchTerms.getStatus(), userParams, userSearch.getStart(),
				userSearch.getEnd(), userSearch.getOrderByComparator()),
			UserLocalServiceUtil.searchCount(
				themeDisplay.getCompanyId(), searchTerms.getKeywords(),
				searchTerms.getStatus(), userParams));

		userSearch.setRowChecker(
			new UnsetUserUserGroupChecker(_renderResponse, _userGroup));

		_userSearch = userSearch;

		return _userSearch;
	}

	public String getSortingURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setParameter(
			"orderByType",
			Objects.equals(getOrderByType(), "asc") ? "desc" : "asc"
		).buildString();
	}

	public List<ViewTypeItem> getViewTypeItems() {
		return new ViewTypeItemList(getPortletURL(), _displayStyle) {
			{
				addCardViewTypeItem();
				addListViewTypeItem();
				addTableViewTypeItem();
			}
		};
	}

	public boolean showCreationMenu() throws PortalException {
		return _hasAddUserGroupPermission();
	}

	private boolean _hasAddUserGroupPermission() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return PortalPermissionUtil.contains(
			themeDisplay.getPermissionChecker(), ActionKeys.ADD_USER_GROUP);
	}

	private final String _displayStyle;
	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private final String _mvcPath;
	private String _orderByCol;
	private String _orderByType;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final UserGroup _userGroup;
	private UserSearch _userSearch;

}