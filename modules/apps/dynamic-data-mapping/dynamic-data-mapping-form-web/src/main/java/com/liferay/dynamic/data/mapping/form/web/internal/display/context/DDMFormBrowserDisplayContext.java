/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.display.context;

import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.form.web.internal.display.context.helper.DDMFormWebRequestHelper;
import com.liferay.dynamic.data.mapping.form.web.internal.search.DDMFormInstanceSearch;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceService;
import com.liferay.dynamic.data.mapping.util.comparator.DDMFormInstanceModifiedDateComparator;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemListBuilder;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.SearchDisplayStyleUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Jürgen Kappler
 */
public class DDMFormBrowserDisplayContext {

	public DDMFormBrowserDisplayContext(
		DDMFormInstanceService ddmFormInstanceService,
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_ddmFormInstanceService = ddmFormInstanceService;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);

		_formWebRequestHelper = new DDMFormWebRequestHelper(
			_httpServletRequest);
	}

	public String getClearResultsURL() throws PortletException {
		return PortletURLBuilder.create(
			PortletURLUtil.clone(getPortletURL(), _renderResponse)
		).setKeywords(
			StringPool.BLANK
		).buildString();
	}

	public DDMFormInstanceSearch getDDMFormInstanceSearch()
		throws PortalException {

		if (_ddmFormInstanceSearch != null) {
			return _ddmFormInstanceSearch;
		}

		PortletURL portletURL = PortletURLBuilder.create(
			getPortletURL()
		).setParameter(
			"displayStyle", getDisplayStyle()
		).buildPortletURL();

		DDMFormInstanceSearch ddmFormInstanceSearch = new DDMFormInstanceSearch(
			_renderRequest, portletURL);

		if (ddmFormInstanceSearch.isSearch()) {
			ddmFormInstanceSearch.setEmptyResultsMessage("no-forms-were-found");
		}
		else {
			ddmFormInstanceSearch.setEmptyResultsMessage("there-are-no-forms");
		}

		ddmFormInstanceSearch.setOrderByCol(getOrderByCol());
		ddmFormInstanceSearch.setOrderByComparator(
			_getDDMFormInstanceOrderByComparator(getOrderByType()));
		ddmFormInstanceSearch.setOrderByType(getOrderByType());
		ddmFormInstanceSearch.setResultsAndTotal(
			() -> _ddmFormInstanceService.search(
				_formWebRequestHelper.getCompanyId(),
				_formWebRequestHelper.getScopeGroupId(), getKeywords(),
				ddmFormInstanceSearch.getStart(),
				ddmFormInstanceSearch.getEnd(),
				ddmFormInstanceSearch.getOrderByComparator()),
			getTotalItems());

		_ddmFormInstanceSearch = ddmFormInstanceSearch;

		return _ddmFormInstanceSearch;
	}

	public String getDisplayStyle() {
		if (Validator.isNotNull(_displayStyle)) {
			return _displayStyle;
		}

		_displayStyle = SearchDisplayStyleUtil.getDisplayStyle(
			_httpServletRequest,
			DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_BROWSER, "list");

		return _displayStyle;
	}

	public String getEventName() {
		if (Validator.isNotNull(_eventName)) {
			return _eventName;
		}

		_eventName = ParamUtil.getString(
			_httpServletRequest, "eventName",
			_renderResponse.getNamespace() + "selectDDMForm");

		return _eventName;
	}

	public String getKeywords() {
		if (Validator.isNotNull(_keywords)) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		return _keywords;
	}

	public List<NavigationItem> getNavigationItems() {
		return NavigationItemListBuilder.add(
			navigationItem -> {
				navigationItem.setActive(true);
				navigationItem.setHref(
					_renderResponse.createRenderURL(), "mvcPath",
					"/browser/view.jsp");

				HttpServletRequest httpServletRequest =
					_formWebRequestHelper.getRequest();

				navigationItem.setLabel(
					LanguageUtil.get(httpServletRequest, "entries"));
			}
		).build();
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_httpServletRequest,
			DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_BROWSER, "modified-date");

		return _orderByCol;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest,
			DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_BROWSER, "asc");

		return _orderByType;
	}

	public List<DropdownItem> getOrderItemsDropdownItems() {
		return DropdownItemListBuilder.add(
			getOrderByDropdownItem("modified-date")
		).build();
	}

	public PortletURL getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCPath(
			"/browser/view.jsp"
		).setKeywords(
			() -> {
				String keywords = getKeywords();

				if (Validator.isNotNull(keywords)) {
					return keywords;
				}

				return null;
			}
		).setParameter(
			"delta",
			() -> {
				String delta = ParamUtil.getString(_renderRequest, "delta");

				if (Validator.isNotNull(delta)) {
					return delta;
				}

				return null;
			}
		).setParameter(
			"displayStyle", getDisplayStyle()
		).setParameter(
			"displayStyle",
			() -> {
				String displayStyle = ParamUtil.getString(
					_renderRequest, "displayStyle");

				if (Validator.isNotNull(displayStyle)) {
					return getDisplayStyle();
				}

				return null;
			}
		).setParameter(
			"eventName", getEventName()
		).setParameter(
			"orderByCol", getOrderByCol()
		).setParameter(
			"orderByCol",
			() -> {
				String orderByCol = getOrderByCol();

				if (Validator.isNotNull(orderByCol)) {
					return orderByCol;
				}

				return null;
			}
		).setParameter(
			"orderByType", getOrderByType()
		).setParameter(
			"orderByType",
			() -> {
				String orderByType = getOrderByType();

				if (Validator.isNotNull(orderByType)) {
					return orderByType;
				}

				return null;
			}
		).buildPortletURL();
	}

	public String getSearchActionURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCPath(
			"/browser/view.jsp"
		).setParameter(
			"displayStyle", getDisplayStyle()
		).setParameter(
			"eventName", getEventName()
		).setParameter(
			"orderByCol", getOrderByCol()
		).setParameter(
			"orderByType", getOrderByType()
		).buildString();
	}

	public String getSearchContainerId() {
		return "ddmFormInstance";
	}

	public String getSortingURL() throws Exception {
		return PortletURLBuilder.create(
			PortletURLUtil.clone(getPortletURL(), _renderResponse)
		).setParameter(
			"orderByType",
			() -> {
				String orderByType = ParamUtil.getString(
					_renderRequest, "orderByType");

				if (orderByType.equals("asc")) {
					return "desc";
				}

				return "asc";
			}
		).buildString();
	}

	public int getTotalItems() {
		if (_formInstanceSearchTotal != null) {
			return _formInstanceSearchTotal;
		}

		_formInstanceSearchTotal = _ddmFormInstanceService.searchCount(
			_formWebRequestHelper.getCompanyId(),
			_formWebRequestHelper.getScopeGroupId(), getKeywords());

		return _formInstanceSearchTotal;
	}

	public boolean isDisabledManagementBar() {
		if (getTotalItems() <= 0) {
			return true;
		}

		return false;
	}

	protected UnsafeConsumer<DropdownItem, Exception> getOrderByDropdownItem(
		String orderByCol) {

		return dropdownItem -> {
			dropdownItem.setActive(orderByCol.equals(getOrderByCol()));
			dropdownItem.setHref(getPortletURL(), "orderByCol", orderByCol);
			dropdownItem.setLabel(
				LanguageUtil.get(
					_formWebRequestHelper.getRequest(), orderByCol));
		};
	}

	private OrderByComparator<DDMFormInstance>
		_getDDMFormInstanceOrderByComparator(String orderByType) {

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		return new DDMFormInstanceModifiedDateComparator(orderByAsc);
	}

	private DDMFormInstanceSearch _ddmFormInstanceSearch;
	private final DDMFormInstanceService _ddmFormInstanceService;
	private String _displayStyle;
	private String _eventName;
	private Integer _formInstanceSearchTotal;
	private final DDMFormWebRequestHelper _formWebRequestHelper;
	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private String _orderByCol;
	private String _orderByType;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}