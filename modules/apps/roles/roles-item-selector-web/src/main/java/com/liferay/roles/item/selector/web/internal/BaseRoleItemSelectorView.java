/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.item.selector.web.internal;

import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.RoleService;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portlet.usersadmin.util.UsersAdminUtil;
import com.liferay.roles.admin.search.RoleSearch;
import com.liferay.roles.admin.search.RoleSearchTerms;
import com.liferay.roles.item.selector.web.internal.constants.RoleItemSelectorViewConstants;
import com.liferay.roles.item.selector.web.internal.display.context.RoleItemSelectorViewDisplayContext;
import com.liferay.roles.item.selector.web.internal.search.RoleItemSelectorChecker;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 * @author Roberto Díaz
 */
public abstract class BaseRoleItemSelectorView<T extends ItemSelectorCriterion>
	implements ItemSelectorView<T> {

	public ServletContext getServletContext() {
		return servletContext;
	}

	@Override
	public List<ItemSelectorReturnType> getSupportedItemSelectorReturnTypes() {
		return _supportedItemSelectorReturnTypes;
	}

	public abstract int getType();

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse, T t,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		ServletContext servletContext = getServletContext();

		RequestDispatcher requestDispatcher =
			servletContext.getRequestDispatcher("/role_item_selector.jsp");

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)servletRequest;

		RenderRequest renderRequest =
			(RenderRequest)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST);
		RenderResponse renderResponse =
			(RenderResponse)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);

		RoleItemSelectorViewDisplayContext roleItemSelectorViewDisplayContext =
			new RoleItemSelectorViewDisplayContext(
				httpServletRequest, t, itemSelectedEventName,
				_getSearchContainer(
					renderRequest, renderResponse, getCheckedRoleIds(t),
					getExcludedRoleNames(t), getType()),
				portal.getLiferayPortletRequest(renderRequest),
				portal.getLiferayPortletResponse(renderResponse));

		servletRequest.setAttribute(
			RoleItemSelectorViewConstants.
				ROLE_ITEM_SELECTOR_VIEW_DISPLAY_CONTEXT,
			roleItemSelectorViewDisplayContext);

		requestDispatcher.include(servletRequest, servletResponse);
	}

	protected abstract long[] getCheckedRoleIds(T t);

	protected abstract String[] getExcludedRoleNames(T t);

	@Reference
	protected Language language;

	@Reference
	protected Portal portal;

	@Reference
	protected RoleService roleService;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.roles.item.selector.web)"
	)
	protected ServletContext servletContext;

	private SearchContainer<Role> _getSearchContainer(
		RenderRequest renderRequest, RenderResponse renderResponse,
		long[] checkedRoleIds, String[] excludedRoleNames, int type) {

		PortletURL currentURL = PortletURLUtil.getCurrent(
			renderRequest, renderResponse);

		SearchContainer<Role> searchContainer = new RoleSearch(
			renderRequest, currentURL);

		searchContainer.setEmptyResultsMessage("no-roles-were-found");
		searchContainer.setOrderByComparator(
			UsersAdminUtil.getRoleOrderByComparator(
				searchContainer.getOrderByCol(),
				searchContainer.getOrderByType()));

		RoleSearchTerms searchTerms =
			(RoleSearchTerms)searchContainer.getSearchTerms();

		searchTerms.setType(type);

		searchContainer.setResultsAndTotal(
			() -> roleService.search(
				CompanyThreadLocal.getCompanyId(), searchTerms.getKeywords(),
				searchTerms.getTypesObj(), new LinkedHashMap<String, Object>(),
				searchContainer.getStart(), searchContainer.getEnd(),
				searchContainer.getOrderByComparator()),
			roleService.searchCount(
				CompanyThreadLocal.getCompanyId(), searchTerms.getKeywords(),
				searchTerms.getTypesObj(),
				new LinkedHashMap<String, Object>()));

		searchContainer.setRowChecker(
			new RoleItemSelectorChecker(
				renderResponse, checkedRoleIds, excludedRoleNames));

		return searchContainer;
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.singletonList(
			new UUIDItemSelectorReturnType());

}