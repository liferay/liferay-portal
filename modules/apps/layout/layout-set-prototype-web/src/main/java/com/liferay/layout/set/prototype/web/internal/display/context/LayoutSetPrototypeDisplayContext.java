/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.set.prototype.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.layout.set.prototype.constants.LayoutSetPrototypePortletKeys;
import com.liferay.layout.set.prototype.web.internal.servlet.taglib.util.LayoutSetPrototypeActionDropdownItemsProvider;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.portlet.SearchDisplayStyleUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.PortalPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.util.comparator.LayoutSetPrototypeCreateDateComparator;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class LayoutSetPrototypeDisplayContext {

	public LayoutSetPrototypeDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
	}

	public Boolean getActive() {
		String status = ParamUtil.get(_httpServletRequest, "status", "all");

		if (status.equals("active")) {
			return true;
		}
		else if (status.equals("inactive")) {
			return false;
		}

		return null;
	}

	public String getDisplayStyle() {
		if (Validator.isNotNull(_displayStyle)) {
			return _displayStyle;
		}

		_displayStyle = SearchDisplayStyleUtil.getDisplayStyle(
			_httpServletRequest,
			LayoutSetPrototypePortletKeys.LAYOUT_SET_PROTOTYPE, "list");

		return _displayStyle;
	}

	public List<DropdownItem> getLayoutSetPrototypeActionDropdownItems(
			LayoutSetPrototype layoutSetPrototype)
		throws Exception {

		LayoutSetPrototypeActionDropdownItemsProvider
			layoutSetPrototypeActionDropdownItemsProvider =
				new LayoutSetPrototypeActionDropdownItemsProvider(
					layoutSetPrototype, _renderRequest, _renderResponse);

		return layoutSetPrototypeActionDropdownItemsProvider.
			getActionDropdownItems();
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_httpServletRequest,
			LayoutSetPrototypePortletKeys.LAYOUT_SET_PROTOTYPE, "create-date");

		return _orderByCol;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest,
			LayoutSetPrototypePortletKeys.LAYOUT_SET_PROTOTYPE, "asc");

		return _orderByType;
	}

	public SearchContainer<LayoutSetPrototype> getSearchContainer() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		SearchContainer<LayoutSetPrototype> searchContainer =
			new SearchContainer(
				_renderRequest, _renderResponse.createRenderURL(), null,
				"there-are-no-site-templates");

		searchContainer.setId("layoutSetPrototype");
		searchContainer.setOrderByCol(getOrderByCol());

		boolean orderByAsc = false;

		if (Objects.equals(getOrderByType(), "asc")) {
			orderByAsc = true;
		}

		searchContainer.setOrderByComparator(
			LayoutSetPrototypeCreateDateComparator.getInstance(orderByAsc));
		searchContainer.setOrderByType(getOrderByType());
		searchContainer.setResultsAndTotal(
			() -> LayoutSetPrototypeLocalServiceUtil.search(
				themeDisplay.getCompanyId(), getActive(),
				searchContainer.getStart(), searchContainer.getEnd(),
				searchContainer.getOrderByComparator()),
			LayoutSetPrototypeLocalServiceUtil.searchCount(
				themeDisplay.getCompanyId(), getActive()));
		searchContainer.setRowChecker(
			new EmptyOnClickRowChecker(_renderResponse));

		return searchContainer;
	}

	public boolean isDescriptiveView() {
		return Objects.equals(getDisplayStyle(), "descriptive");
	}

	public boolean isIconView() {
		return Objects.equals(getDisplayStyle(), "icon");
	}

	public boolean isListView() {
		return Objects.equals(getDisplayStyle(), "list");
	}

	public boolean isShowAddButton() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return PortalPermissionUtil.contains(
			themeDisplay.getPermissionChecker(),
			ActionKeys.ADD_LAYOUT_SET_PROTOTYPE);
	}

	private String _displayStyle;
	private final HttpServletRequest _httpServletRequest;
	private String _orderByCol;
	private String _orderByType;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}