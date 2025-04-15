/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.display.context;

import com.liferay.blogs.constants.BlogsPortletKeys;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryServiceUtil;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.organizations.item.selector.OrganizationItemSelectorCriterion;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Mariano Álvaro Sáiz
 */
public class BlogsAggregatorViewDisplayContext {

	public BlogsAggregatorViewDisplayContext(
			HttpServletRequest httpServletRequest, RenderRequest renderRequest,
			RenderResponse renderResponse)
		throws PortalException {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_itemSelector = (ItemSelector)httpServletRequest.getAttribute(
			ItemSelector.class.getName());
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
		_portletPreferences =
			PortletPreferencesFactoryUtil.getPortletPreferences(
				httpServletRequest, BlogsPortletKeys.BLOGS_AGGREGATOR);
	}

	public int getMax() {
		if (_max != null) {
			return _max;
		}

		_max = GetterUtil.getInteger(_portletPreferences.getValue("max", "20"));

		return _max;
	}

	public long getOrganizationId() {
		if (_organizationId != null) {
			return _organizationId;
		}

		_organizationId = GetterUtil.getLong(
			_portletPreferences.getValue("organizationId", "0"));

		if (_organizationId == 0) {
			Group group = _themeDisplay.getScopeGroup();

			if (group.isOrganization()) {
				_organizationId = group.getOrganizationId();
			}
		}

		return _organizationId;
	}

	public String getOrganizationItemSelectorURL() {
		OrganizationItemSelectorCriterion organizationItemSelectorCriterion =
			new OrganizationItemSelectorCriterion();

		organizationItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new UUIDItemSelectorReturnType());

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(_httpServletRequest),
				_renderResponse.getNamespace() + "selectOrganization",
				organizationItemSelectorCriterion));
	}

	public PortletURL getPortletURL() {
		if (_portletURL != null) {
			return _portletURL;
		}

		_portletURL = PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/blogs_aggregator/view"
		).buildPortletURL();

		return _portletURL;
	}

	public SearchContainer<BlogsEntry> getSearchContainer()
		throws PortalException {

		_searchContainer = new SearchContainer(
			_renderRequest, null, null, SearchContainer.DEFAULT_CUR_PARAM, 5,
			getPortletURL(), null, null);

		List<BlogsEntry> blogsEntries = new ArrayList<>();

		if (Objects.equals(getSelectionMethod(), "users")) {
			if (getOrganizationId() > 0) {
				blogsEntries.addAll(
					BlogsEntryServiceUtil.getOrganizationEntries(
						getOrganizationId(), new Date(),
						WorkflowConstants.STATUS_APPROVED, getMax()));
			}
			else {
				blogsEntries.addAll(
					BlogsEntryServiceUtil.getGroupsEntries(
						_themeDisplay.getCompanyId(),
						_themeDisplay.getScopeGroupId(), new Date(),
						WorkflowConstants.STATUS_APPROVED, getMax()));
			}
		}
		else {
			blogsEntries.addAll(
				BlogsEntryServiceUtil.getGroupEntries(
					_themeDisplay.getScopeGroupId(), new Date(),
					WorkflowConstants.STATUS_APPROVED, getMax()));
		}

		_searchContainer.setResultsAndTotal(blogsEntries);

		return _searchContainer;
	}

	public String getSelectionMethod() {
		if (_selectionMethod != null) {
			return _selectionMethod;
		}

		_selectionMethod = _portletPreferences.getValue(
			"selectionMethod", "users");

		return _selectionMethod;
	}

	private final HttpServletRequest _httpServletRequest;
	private final ItemSelector _itemSelector;
	private Integer _max;
	private Long _organizationId;
	private final PortletPreferences _portletPreferences;
	private PortletURL _portletURL;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private SearchContainer<BlogsEntry> _searchContainer;
	private String _selectionMethod;
	private final ThemeDisplay _themeDisplay;

}