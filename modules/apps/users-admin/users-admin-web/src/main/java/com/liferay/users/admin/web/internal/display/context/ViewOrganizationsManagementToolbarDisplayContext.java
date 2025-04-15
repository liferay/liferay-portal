/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.ViewTypeItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.ViewTypeItemList;
import com.liferay.organizations.search.OrganizationSearch;
import com.liferay.organizations.search.OrganizationSearchTerms;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SortFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.PortalPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.util.PropsValues;
import com.liferay.users.admin.web.internal.search.OrganizationChecker;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author Pei-Jung Lan
 */
public class ViewOrganizationsManagementToolbarDisplayContext {

	public ViewOrganizationsManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse, String displayStyle) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
		_displayStyle = displayStyle;

		_currentURL = PortletURLUtil.getCurrent(renderRequest, renderResponse);
	}

	public List<DropdownItem> getActionDropdownItems() {
		String getActiveUsersURL = ResourceURLBuilder.createResourceURL(
			_renderResponse
		).setParameter(
			"className", Organization.class.getName()
		).setParameter(
			"status", String.valueOf(WorkflowConstants.STATUS_APPROVED)
		).setResourceID(
			"/users_admin/get_users_count"
		).buildString();
		String getInactiveUsersURL = ResourceURLBuilder.createResourceURL(
			_renderResponse
		).setParameter(
			"className", Organization.class.getName()
		).setParameter(
			"status", String.valueOf(WorkflowConstants.STATUS_INACTIVE)
		).setResourceID(
			"/users_admin/get_users_count"
		).buildString();

		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData(Constants.CMD, Constants.DELETE);
				dropdownItem.putData("action", "deleteOrganizations");
				dropdownItem.putData(
					"deleteOrganizationURL",
					PortletURLBuilder.createActionURL(
						_renderResponse
					).setActionName(
						"/users_admin/edit_organization"
					).buildString());
				dropdownItem.putData("getActiveUsersURL", getActiveUsersURL);
				dropdownItem.putData(
					"getInactiveUsersURL", getInactiveUsersURL);
				dropdownItem.setIcon("times-circle");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "delete"));
				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).buildString();
	}

	public CreationMenu getCreationMenu() throws PortalException {
		return new CreationMenu() {
			{
				for (String organizationType :
						OrganizationLocalServiceUtil.getTypes()) {

					addDropdownItem(
						dropdownItem -> {
							dropdownItem.setHref(
								_renderResponse.createRenderURL(),
								"mvcRenderCommandName",
								"/users_admin/edit_organization", "type",
								organizationType);
							dropdownItem.setLabel(
								LanguageUtil.format(
									_httpServletRequest, "add-x",
									organizationType));
						});
				}
			}
		};
	}

	public String getOrderByCol() {
		return _organizationSearch.getOrderByCol();
	}

	public List<DropdownItem> getOrderByDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setActive(Objects.equals(getOrderByCol(), "name"));
				dropdownItem.setHref(getPortletURL(), "orderByCol", "name");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "name"));
			}
		).add(
			dropdownItem -> {
				dropdownItem.setActive(Objects.equals(getOrderByCol(), "type"));
				dropdownItem.setHref(getPortletURL(), "orderByCol", "type");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "type"));
			}
		).build();
	}

	public String getOrderByType() {
		return _organizationSearch.getOrderByType();
	}

	public PortletURL getPortletURL() {
		try {
			return PortletURLBuilder.create(
				PortletURLUtil.clone(_currentURL, _renderResponse)
			).setParameter(
				"orderByCol", getOrderByCol()
			).setParameter(
				"orderByType", getOrderByType()
			).buildPortletURL();
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return _renderResponse.createRenderURL();
		}
	}

	public String getSearchActionURL() {
		PortletURL searchActionURL = getPortletURL();

		return searchActionURL.toString();
	}

	public SearchContainer<Organization> getSearchContainer(
			LinkedHashMap<String, Object> organizationParams,
			boolean filterManageableOrganizations)
		throws Exception {

		if (_organizationSearch != null) {
			return _organizationSearch;
		}

		PortletURL portletURL = (PortletURL)_httpServletRequest.getAttribute(
			"view.jsp-portletURL");

		OrganizationSearch organizationSearch = new OrganizationSearch(
			_renderRequest, portletURL);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		long parentOrganizationId =
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID;

		OrganizationSearchTerms organizationSearchTerms =
			(OrganizationSearchTerms)organizationSearch.getSearchTerms();

		String keywords = organizationSearchTerms.getKeywords();

		if (Validator.isNotNull(keywords) || filterManageableOrganizations) {
			parentOrganizationId =
				OrganizationConstants.ANY_PARENT_ORGANIZATION_ID;
		}
		else {
			parentOrganizationId = ParamUtil.getLong(
				_httpServletRequest, "parentOrganizationId",
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID);
		}

		Indexer<?> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			Organization.class);

		long searchParentOrganizationId = parentOrganizationId;

		if (indexer.isIndexerEnabled() &&
			PropsValues.ORGANIZATIONS_SEARCH_WITH_INDEX) {

			organizationParams.put("expandoAttributes", keywords);

			organizationSearch.setResultsAndTotal(
				OrganizationLocalServiceUtil.searchOrganizations(
					themeDisplay.getCompanyId(), searchParentOrganizationId,
					keywords, organizationParams, organizationSearch.getStart(),
					organizationSearch.getEnd(),
					SortFactoryUtil.getSort(
						Organization.class, organizationSearch.getOrderByCol(),
						organizationSearch.getOrderByType())));
		}
		else {
			organizationSearch.setResultsAndTotal(
				() -> OrganizationLocalServiceUtil.search(
					themeDisplay.getCompanyId(), searchParentOrganizationId,
					keywords, organizationSearchTerms.getType(),
					organizationSearchTerms.getRegionIdObj(),
					organizationSearchTerms.getCountryIdObj(),
					organizationParams, organizationSearch.getStart(),
					organizationSearch.getEnd(),
					organizationSearch.getOrderByComparator()),
				OrganizationLocalServiceUtil.searchCount(
					themeDisplay.getCompanyId(), searchParentOrganizationId,
					keywords, organizationSearchTerms.getType(),
					organizationSearchTerms.getRegionIdObj(),
					organizationSearchTerms.getCountryIdObj(),
					organizationParams));
		}

		organizationSearch.setRowChecker(
			new OrganizationChecker(_renderResponse) {
				{
					setRowIds("rowIdsOrganization");
				}
			});

		_organizationSearch = organizationSearch;

		return _organizationSearch;
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
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return PortalPermissionUtil.contains(
			themeDisplay.getPermissionChecker(), ActionKeys.ADD_ORGANIZATION);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewOrganizationsManagementToolbarDisplayContext.class);

	private final PortletURL _currentURL;
	private final String _displayStyle;
	private final HttpServletRequest _httpServletRequest;
	private OrganizationSearch _organizationSearch;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}