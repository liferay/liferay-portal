/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.tax.category.web.internal.display.context;

import com.liferay.commerce.frontend.model.HeaderActionModel;
import com.liferay.commerce.product.constants.CPActionKeys;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.display.context.helper.CPRequestHelper;
import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.commerce.product.service.CPTaxCategoryService;
import com.liferay.commerce.product.tax.category.web.internal.util.CPTaxCategoryUtil;
import com.liferay.commerce.product.util.comparator.CPTaxCategoryCreateDateComparator;
import com.liferay.commerce.tax.model.CommerceTaxMethod;
import com.liferay.commerce.tax.service.CommerceTaxMethodService;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alessio Antonio Rendina
 */
public class CPTaxCategoryDisplayContext {

	public CPTaxCategoryDisplayContext(
		CommerceTaxMethodService commerceTaxMethodService,
		CPTaxCategoryService cpTaxCategoryService,
		PortletResourcePermission portletResourcePermission,
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_commerceTaxMethodService = commerceTaxMethodService;
		_cpTaxCategoryService = cpTaxCategoryService;
		_portletResourcePermission = portletResourcePermission;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		cpRequestHelper = new CPRequestHelper(
			PortalUtil.getHttpServletRequest(renderRequest));
	}

	public CommerceTaxMethod getCommerceTaxMethod() throws PortalException {
		if (_commerceTaxMethod != null) {
			return _commerceTaxMethod;
		}

		long commerceTaxMethodId = ParamUtil.getLong(
			_renderRequest, "commerceTaxMethodId");

		if (commerceTaxMethodId > 0) {
			_commerceTaxMethod = _commerceTaxMethodService.getCommerceTaxMethod(
				commerceTaxMethodId);
		}

		return _commerceTaxMethod;
	}

	public long getCommerceTaxMethodId() throws PortalException {
		CommerceTaxMethod commerceTaxMethod = getCommerceTaxMethod();

		if (commerceTaxMethod == null) {
			return 0;
		}

		return commerceTaxMethod.getCommerceTaxMethodId();
	}

	public CPTaxCategory getCPTaxCategory() throws PortalException {
		if (_cpTaxCategory != null) {
			return _cpTaxCategory;
		}

		long cpTaxCategoryId = ParamUtil.getLong(
			_renderRequest, "cpTaxCategoryId");

		if (cpTaxCategoryId > 0) {
			_cpTaxCategory = _cpTaxCategoryService.getCPTaxCategory(
				cpTaxCategoryId);
		}

		return _cpTaxCategory;
	}

	public List<HeaderActionModel> getHeaderActionModels() {
		List<HeaderActionModel> headerActionModels = new ArrayList<>();

		RenderResponse renderResponse = cpRequestHelper.getRenderResponse();

		HeaderActionModel publishHeaderActionModel = new HeaderActionModel(
			"btn-primary", renderResponse.getNamespace() + "fm", null, null,
			"save");

		headerActionModels.add(publishHeaderActionModel);

		return headerActionModels;
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_renderRequest, CPPortletKeys.CP_TAX_CATEGORY, "create-date");

		return _orderByCol;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_renderRequest, CPPortletKeys.CP_TAX_CATEGORY, "desc");

		return _orderByType;
	}

	public PortletURL getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setParameter(
			"orderByCol", getOrderByCol()
		).setParameter(
			"orderByType", getOrderByType()
		).buildPortletURL();
	}

	public SearchContainer<CPTaxCategory> getSearchContainer()
		throws PortalException {

		if (_searchContainer != null) {
			return _searchContainer;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_searchContainer = new SearchContainer<>(
			_renderRequest, getPortletURL(), null,
			"there-are-no-tax-categories");

		_searchContainer.setOrderByCol(getOrderByCol());
		_searchContainer.setOrderByComparator(
			_getCPTaxCategoryOrderByComparator(
				getOrderByCol(), getOrderByType()));
		_searchContainer.setOrderByType(getOrderByType());

		if (Validator.isBlank(getKeywords())) {
			_searchContainer.setResultsAndTotal(
				() -> _cpTaxCategoryService.getCPTaxCategories(
					themeDisplay.getCompanyId(), _searchContainer.getStart(),
					_searchContainer.getEnd(),
					_searchContainer.getOrderByComparator()),
				_cpTaxCategoryService.getCPTaxCategoriesCount(
					themeDisplay.getCompanyId()));
		}
		else {
			_searchContainer.setResultsAndTotal(
				_cpTaxCategoryService.searchCPTaxCategories(
					themeDisplay.getCompanyId(), getKeywords(),
					_searchContainer.getStart(), _searchContainer.getEnd(),
					CPTaxCategoryUtil.getCPTaxCategorySort(
						getOrderByCol(), getOrderByType())));
		}

		_searchContainer.setRowChecker(_getRowChecker());

		return _searchContainer;
	}

	public boolean hasManageCPTaxCategoriesPermission() {
		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return _portletResourcePermission.contains(
			themeDisplay.getPermissionChecker(), null,
			CPActionKeys.MANAGE_COMMERCE_PRODUCT_TAX_CATEGORIES);
	}

	protected String getKeywords() {
		return ParamUtil.getString(_renderRequest, "keywords");
	}

	protected final CPRequestHelper cpRequestHelper;

	private OrderByComparator<CPTaxCategory> _getCPTaxCategoryOrderByComparator(
		String orderByCol, String orderByType) {

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		OrderByComparator<CPTaxCategory> orderByComparator = null;

		if (orderByCol.equals("create-date")) {
			orderByComparator = CPTaxCategoryCreateDateComparator.getInstance(
				orderByAsc);
		}

		return orderByComparator;
	}

	private RowChecker _getRowChecker() {
		if (_rowChecker == null) {
			_rowChecker = new EmptyOnClickRowChecker(_renderResponse);
		}

		return _rowChecker;
	}

	private CommerceTaxMethod _commerceTaxMethod;
	private final CommerceTaxMethodService _commerceTaxMethodService;
	private CPTaxCategory _cpTaxCategory;
	private final CPTaxCategoryService _cpTaxCategoryService;
	private String _orderByCol;
	private String _orderByType;
	private final PortletResourcePermission _portletResourcePermission;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private RowChecker _rowChecker;
	private SearchContainer<CPTaxCategory> _searchContainer;

}