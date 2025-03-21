/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.knowledge.base.model.KBComment;
import com.liferay.knowledge.base.web.internal.security.permission.resource.KBCommentPermission;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Alejandro Tardín
 */
public class KBSuggestionListManagementToolbarDisplayContext {

	public KBSuggestionListManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SearchContainer<KBComment> searchContainer) {

		_httpServletRequest = httpServletRequest;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_searchContainer = searchContainer;

		_currentURLObj = PortletURLUtil.getCurrent(
			liferayPortletRequest, liferayPortletResponse);
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData("action", "deleteKBComments");
				dropdownItem.setIcon("trash");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "delete"));
				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	public List<String> getAvailableActions(KBComment kbComment)
		throws PortalException {

		if (KBCommentPermission.contains(
				_themeDisplay.getPermissionChecker(), kbComment,
				ActionKeys.DELETE)) {

			return Collections.singletonList("deleteKBComments");
		}

		return Collections.emptyList();
	}

	public String getClearResultsURL() {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCPath(
			"/admin/view_kb_suggestions.jsp"
		).buildString();
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
			() -> !navigation.equals("all"),
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						PortletURLUtil.clone(
							_currentURLObj, _liferayPortletResponse)
					).setNavigation(
						(String)null
					).buildString());

				labelItem.setDismissible(true);
				labelItem.setLabel(
					LanguageUtil.get(_httpServletRequest, navigation));
			}
		).build();
	}

	public List<DropdownItem> getOrderByDropdownItems() {
		return new DropdownItemList() {
			{
				Map<String, String> orderColumnsMap = new HashMap<>();

				String navigation = _getNavigation();

				if (navigation.equals("all")) {
					orderColumnsMap.put("status", "status");
				}

				orderColumnsMap.put("modified-date", "modified-date");
				orderColumnsMap.put("user-name", "user-name");

				for (Map.Entry<String, String> orderByColEntry :
						orderColumnsMap.entrySet()) {

					add(
						dropdownItem -> {
							String orderByCol = orderByColEntry.getKey();

							dropdownItem.setActive(
								orderByCol.equals(_getOrderByCol()));

							dropdownItem.setHref(
								_getCurrentSortingURL(), "orderByCol",
								orderByColEntry.getValue());
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, orderByCol));
						});
				}
			}
		};
	}

	public String getOrderByType() {
		return _searchContainer.getOrderByType();
	}

	public SearchContainer<KBComment> getSearchContainer() {
		return _searchContainer;
	}

	public PortletURL getSortingURL() throws PortletException {
		return PortletURLBuilder.create(
			_getCurrentSortingURL()
		).setParameter(
			"orderByType",
			() -> {
				if (Objects.equals(getOrderByType(), "asc")) {
					return "desc";
				}

				return "asc";
			}
		).buildPortletURL();
	}

	public int getTotal() {
		return _searchContainer.getTotal();
	}

	public boolean isDisabled() {
		String navigation = _getNavigation();

		if (navigation.equals("all") && !_searchContainer.hasResults()) {
			return true;
		}

		return false;
	}

	private PortletURL _getCurrentSortingURL() throws PortletException {
		return PortletURLBuilder.create(
			PortletURLUtil.clone(_currentURLObj, _liferayPortletResponse)
		).setParameter(
			"storeOrderByPreference", true
		).buildPortletURL();
	}

	private List<DropdownItem> _getFilterNavigationDropdownItems()
		throws PortletException {

		return new DropdownItemList() {
			{
				String navigation = _getNavigation();
				String[] navigationKeys = {
					"all", "new", "in-progress", "resolved"
				};

				PortletURL navigationURL = PortletURLBuilder.create(
					PortletURLUtil.clone(
						_currentURLObj, _liferayPortletResponse)
				).setParameter(
					"storeOrderByPreference", false
				).buildPortletURL();

				for (String navigationKey : navigationKeys) {
					add(
						dropdownItem -> {
							dropdownItem.setActive(
								navigation.equals(navigationKey));
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

	private String _getOrderByCol() {
		return _searchContainer.getOrderByCol();
	}

	private final PortletURL _currentURLObj;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final SearchContainer<KBComment> _searchContainer;
	private final ThemeDisplay _themeDisplay;

}