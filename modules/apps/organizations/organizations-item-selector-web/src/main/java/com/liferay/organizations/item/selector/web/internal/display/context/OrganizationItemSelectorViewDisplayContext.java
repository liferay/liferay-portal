/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.organizations.item.selector.web.internal.display.context;

import com.liferay.organizations.item.selector.OrganizationItemSelectorCriterion;
import com.liferay.organizations.item.selector.web.internal.search.OrganizationItemSelectorChecker;
import com.liferay.organizations.search.OrganizationSearch;
import com.liferay.organizations.search.OrganizationSearchTerms;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.usersadmin.util.UsersAdminUtil;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.LinkedHashMap;

/**
 * @author Alessio Antonio Rendina
 */
public class OrganizationItemSelectorViewDisplayContext {

	public OrganizationItemSelectorViewDisplayContext(
		OrganizationItemSelectorCriterion organizationItemSelectorCriterion,
		OrganizationLocalService organizationLocalService,
		HttpServletRequest httpServletRequest, Portal portal,
		PortletURL portletURL) {

		_organizationItemSelectorCriterion = organizationItemSelectorCriterion;
		_organizationLocalService = organizationLocalService;
		_portal = portal;
		_portletURL = portletURL;

		_renderRequest = (RenderRequest)httpServletRequest.getAttribute(
			JavaConstants.JAVAX_PORTLET_REQUEST);
		_renderResponse = (RenderResponse)httpServletRequest.getAttribute(
			JavaConstants.JAVAX_PORTLET_RESPONSE);
	}

	public String getOrderByCol() {
		return ParamUtil.getString(
			_renderRequest, SearchContainer.DEFAULT_ORDER_BY_COL_PARAM, "name");
	}

	public String getOrderByType() {
		return ParamUtil.getString(
			_renderRequest, SearchContainer.DEFAULT_ORDER_BY_TYPE_PARAM, "asc");
	}

	public SearchContainer<Organization> getSearchContainer()
		throws PortalException {

		if (_searchContainer != null) {
			return _searchContainer;
		}

		_searchContainer = new OrganizationSearch(_renderRequest, _portletURL);

		_searchContainer.setEmptyResultsMessage("no-organizations-were-found");
		_searchContainer.setOrderByCol(getOrderByCol());
		_searchContainer.setOrderByComparator(
			UsersAdminUtil.getOrganizationOrderByComparator(
				getOrderByCol(), getOrderByType()));
		_searchContainer.setOrderByType(getOrderByType());

		OrganizationSearchTerms organizationSearchTerms =
			(OrganizationSearchTerms)_searchContainer.getSearchTerms();

		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		LinkedHashMap<String, Object> params = new LinkedHashMap<>();

		if (!permissionChecker.hasPermission(
				null, Organization.class.getName(),
				Organization.class.getName(), ActionKeys.VIEW)) {

			params.put(
				"organizationsTree",
				_organizationLocalService.getUserOrganizations(
					_portal.getUserId(_renderRequest), true));
		}

		_searchContainer.setResultsAndTotal(
			() -> _organizationLocalService.search(
				CompanyThreadLocal.getCompanyId(),
				OrganizationConstants.ANY_PARENT_ORGANIZATION_ID,
				organizationSearchTerms.getKeywords(), null, null, null, params,
				_searchContainer.getStart(), _searchContainer.getEnd(),
				_searchContainer.getOrderByComparator()),
			_organizationLocalService.searchCount(
				CompanyThreadLocal.getCompanyId(),
				OrganizationConstants.ANY_PARENT_ORGANIZATION_ID,
				organizationSearchTerms.getKeywords(), null, null, null,
				params));

		_searchContainer.setRowChecker(
			new OrganizationItemSelectorChecker(
				_renderResponse,
				_organizationItemSelectorCriterion.
					getSelectedOrganizationIds()));

		return _searchContainer;
	}

	private final OrganizationItemSelectorCriterion
		_organizationItemSelectorCriterion;
	private final OrganizationLocalService _organizationLocalService;
	private final Portal _portal;
	private final PortletURL _portletURL;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private SearchContainer<Organization> _searchContainer;

}