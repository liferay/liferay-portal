/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.item.selector.web.internal.display.context;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.users.admin.item.selector.web.internal.search.UserItemSelectorChecker;
import com.liferay.users.admin.search.UserSearch;
import com.liferay.users.admin.search.UserSearchTerms;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Alessio Antonio Rendina
 */
public class UserItemSelectorViewDisplayContext {

	public UserItemSelectorViewDisplayContext(
		HttpServletRequest httpServletRequest, PortletURL portletURL,
		UserLocalService userLocalService) {

		_portletURL = portletURL;
		_userLocalService = userLocalService;

		_portletRequest = (PortletRequest)httpServletRequest.getAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST);
		_renderResponse = (RenderResponse)httpServletRequest.getAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE);
	}

	public SearchContainer<User> getSearchContainer() {
		if (_searchContainer != null) {
			return _searchContainer;
		}

		_searchContainer = new UserSearch(_portletRequest, _portletURL);

		UserSearchTerms userSearchTerms =
			(UserSearchTerms)_searchContainer.getSearchTerms();

		_searchContainer.setResultsAndTotal(
			() -> _userLocalService.search(
				CompanyThreadLocal.getCompanyId(),
				userSearchTerms.getKeywords(), userSearchTerms.getStatus(),
				null, _searchContainer.getStart(), _searchContainer.getEnd(),
				_searchContainer.getOrderByComparator()),
			_userLocalService.searchCount(
				CompanyThreadLocal.getCompanyId(),
				userSearchTerms.getKeywords(), userSearchTerms.getStatus(),
				null));

		_searchContainer.setRowChecker(
			new UserItemSelectorChecker(
				_renderResponse, _getCheckedUserIds(),
				_isCheckedUseIdsEnable()));

		return _searchContainer;
	}

	private long[] _getCheckedUserIds() {
		return ParamUtil.getLongValues(_portletRequest, "checkedUserIds");
	}

	private boolean _isCheckedUseIdsEnable() {
		return ParamUtil.getBoolean(_portletRequest, "checkedUserIdsEnabled");
	}

	private final PortletRequest _portletRequest;
	private final PortletURL _portletURL;
	private final RenderResponse _renderResponse;
	private SearchContainer<User> _searchContainer;
	private final UserLocalService _userLocalService;

}