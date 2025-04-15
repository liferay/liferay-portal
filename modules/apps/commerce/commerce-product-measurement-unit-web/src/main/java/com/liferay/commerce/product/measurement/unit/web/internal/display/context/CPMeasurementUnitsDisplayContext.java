/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.measurement.unit.web.internal.display.context;

import com.liferay.commerce.product.constants.CPActionKeys;
import com.liferay.commerce.product.constants.CPMeasurementUnitConstants;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.measurement.unit.web.internal.util.CPMeasurementUnitUtil;
import com.liferay.commerce.product.model.CPMeasurementUnit;
import com.liferay.commerce.product.service.CPMeasurementUnitService;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemBuilder;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Alessio Antonio Rendina
 */
public class CPMeasurementUnitsDisplayContext {

	public CPMeasurementUnitsDisplayContext(
		CPMeasurementUnitService cpMeasurementUnitService,
		PortletResourcePermission portletResourcePermission,
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_cpMeasurementUnitService = cpMeasurementUnitService;
		_portletResourcePermission = portletResourcePermission;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
	}

	public CPMeasurementUnit getCPMeasurementUnit() throws PortalException {
		if (_cpMeasurementUnit != null) {
			return _cpMeasurementUnit;
		}

		long cpMeasurementUnitId = ParamUtil.getLong(
			_renderRequest, "cpMeasurementUnitId");

		if (cpMeasurementUnitId > 0) {
			_cpMeasurementUnit = _cpMeasurementUnitService.getCPMeasurementUnit(
				cpMeasurementUnitId);
		}

		return _cpMeasurementUnit;
	}

	public List<NavigationItem> getNavigationItems() {
		List<NavigationItem> navigationItems = new ArrayList<>();

		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String toolbarItem = ParamUtil.getString(
			_renderRequest, "toolbarItem",
			"view-all-dimension-product-measurement-units");

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", themeDisplay.getLocale(), getClass());

		navigationItems.add(
			_getNavigationItem(
				toolbarItem.equals(
					"view-all-dimension-product-measurement-units"),
				_getNavigationItemURL(
					"view-all-dimension-product-measurement-units",
					CPMeasurementUnitConstants.TYPE_DIMENSION),
				LanguageUtil.get(resourceBundle, "dimensions")));
		navigationItems.add(
			_getNavigationItem(
				toolbarItem.equals("view-all-unit-product-measurement-units"),
				_getNavigationItemURL(
					"view-all-unit-product-measurement-units",
					CPMeasurementUnitConstants.TYPE_UNIT),
				LanguageUtil.get(resourceBundle, "unit")));
		navigationItems.add(
			_getNavigationItem(
				toolbarItem.equals("view-all-weight-product-measurement-units"),
				_getNavigationItemURL(
					"view-all-weight-product-measurement-units",
					CPMeasurementUnitConstants.TYPE_WEIGHT),
				LanguageUtil.get(resourceBundle, "weight")));

		return navigationItems;
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_renderRequest, CPPortletKeys.CP_MEASUREMENT_UNIT, "priority");

		return _orderByCol;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_renderRequest, CPPortletKeys.CP_MEASUREMENT_UNIT, "asc");

		return _orderByType;
	}

	public PortletURL getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setParameter(
			"orderByCol", getOrderByCol()
		).setParameter(
			"orderByType", getOrderByType()
		).setParameter(
			"toolbarItem",
			() -> {
				String toolbarItem = ParamUtil.getString(
					_renderRequest, "toolbarItem");

				if (Validator.isNotNull(toolbarItem)) {
					return toolbarItem;
				}

				return null;
			}
		).setParameter(
			"type", getType()
		).buildPortletURL();
	}

	public CPMeasurementUnit getPrimaryCPMeasurementUnit()
		throws PortalException {

		if (_primaryCPMeasurementUnit != null) {
			return _primaryCPMeasurementUnit;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_primaryCPMeasurementUnit =
			_cpMeasurementUnitService.fetchPrimaryCPMeasurementUnit(
				themeDisplay.getCompanyId(), getType());

		return _primaryCPMeasurementUnit;
	}

	public SearchContainer<CPMeasurementUnit> getSearchContainer()
		throws PortalException {

		if (_searchContainer != null) {
			return _searchContainer;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String emptyResultsMessage = "there-are-no-measurement-units";

		_searchContainer = new SearchContainer<>(
			_renderRequest, getPortletURL(), null, emptyResultsMessage);

		_searchContainer.setOrderByCol(getOrderByCol());
		_searchContainer.setOrderByComparator(
			CPMeasurementUnitUtil.getCPMeasurementUnitOrderByComparator(
				getOrderByCol(), getOrderByType()));
		_searchContainer.setOrderByType(getOrderByType());
		_searchContainer.setResultsAndTotal(
			() -> _cpMeasurementUnitService.getCPMeasurementUnits(
				themeDisplay.getCompanyId(), getType(),
				_searchContainer.getStart(), _searchContainer.getEnd(),
				_searchContainer.getOrderByComparator()),
			_cpMeasurementUnitService.getCPMeasurementUnitsCount(
				themeDisplay.getCompanyId(), getType()));
		_searchContainer.setRowChecker(_getRowChecker());

		return _searchContainer;
	}

	public int getType() {
		return ParamUtil.getInteger(
			_renderRequest, "type", CPMeasurementUnitConstants.TYPE_DIMENSION);
	}

	public boolean hasManageCPMeasurementUnitsPermission()
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return _portletResourcePermission.contains(
			themeDisplay.getPermissionChecker(), null,
			CPActionKeys.MANAGE_COMMERCE_PRODUCT_MEASUREMENT_UNITS);
	}

	private NavigationItem _getNavigationItem(
		boolean active, String href, String label) {

		return NavigationItemBuilder.setActive(
			active
		).setHref(
			href
		).setLabel(
			label
		).build();
	}

	private String _getNavigationItemURL(String toolbarItem, int type) {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCPath(
			"/view.jsp"
		).setParameter(
			"toolbarItem", toolbarItem
		).setParameter(
			"type", type
		).buildString();
	}

	private RowChecker _getRowChecker() {
		if (_rowChecker == null) {
			_rowChecker = new EmptyOnClickRowChecker(_renderResponse);
		}

		return _rowChecker;
	}

	private CPMeasurementUnit _cpMeasurementUnit;
	private final CPMeasurementUnitService _cpMeasurementUnitService;
	private String _orderByCol;
	private String _orderByType;
	private final PortletResourcePermission _portletResourcePermission;
	private CPMeasurementUnit _primaryCPMeasurementUnit;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private RowChecker _rowChecker;
	private SearchContainer<CPMeasurementUnit> _searchContainer;

}