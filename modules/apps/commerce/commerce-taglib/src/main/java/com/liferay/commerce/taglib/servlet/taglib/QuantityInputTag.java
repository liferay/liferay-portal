/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.taglib.servlet.taglib;

import com.liferay.commerce.constants.CPDefinitionInventoryConstants;
import com.liferay.commerce.model.CPDefinitionInventory;
import com.liferay.commerce.service.CPDefinitionInventoryLocalServiceUtil;
import com.liferay.commerce.taglib.servlet.taglib.internal.servlet.ServletContextUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

import java.math.BigDecimal;

import java.util.Objects;

/**
 * @author Marco Leo
 * @author Luca Pellizzon
 */
public class QuantityInputTag extends IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		_allowedOrderQuantities = new BigDecimal[0];
		_maxOrderQuantity =
			CPDefinitionInventoryConstants.DEFAULT_MAX_ORDER_QUANTITY;
		_minOrderQuantity =
			CPDefinitionInventoryConstants.DEFAULT_MIN_ORDER_QUANTITY;
		_multipleOrderQuantity =
			CPDefinitionInventoryConstants.DEFAULT_MULTIPLE_ORDER_QUANTITY;

		CPDefinitionInventory cpDefinitionInventory =
			CPDefinitionInventoryLocalServiceUtil.
				fetchCPDefinitionInventoryByCPDefinitionId(_cpDefinitionId);

		if (cpDefinitionInventory != null) {
			_allowedOrderQuantities =
				cpDefinitionInventory.getAllowedOrderQuantitiesArray();

			_maxOrderQuantity = cpDefinitionInventory.getMaxOrderQuantity();

			_minOrderQuantity = cpDefinitionInventory.getMinOrderQuantity();

			_multipleOrderQuantity =
				cpDefinitionInventory.getMultipleOrderQuantity();
		}

		if (Objects.equals(_value, BigDecimal.ZERO)) {
			_value = _minOrderQuantity;
		}

		return super.doStartTag();
	}

	public long getCPDefinitionId() {
		return _cpDefinitionId;
	}

	public String getName() {
		return _name;
	}

	public BigDecimal getValue() {
		return _value;
	}

	public boolean isShowLabel() {
		return _showLabel;
	}

	public boolean isUseSelect() {
		return _useSelect;
	}

	public void setCPDefinitionId(long cpDefinitionId) {
		_cpDefinitionId = cpDefinitionId;
	}

	public void setName(String name) {
		_name = name;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setShowLabel(boolean showLabel) {
		_showLabel = showLabel;
	}

	public void setUseSelect(boolean useSelect) {
		_useSelect = useSelect;
	}

	public void setValue(BigDecimal value) {
		_value = value;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_allowedOrderQuantities = null;
		_cpDefinitionId = 0;
		_maxOrderQuantity = BigDecimal.ZERO;
		_minOrderQuantity = BigDecimal.ZERO;
		_multipleOrderQuantity = BigDecimal.ZERO;
		_name = null;
		_showLabel = true;
		_useSelect = true;
		_value = BigDecimal.ZERO;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest = getRequest();

		httpServletRequest.setAttribute(
			"liferay-commerce:quantity-input:allowedOrderQuantities",
			_allowedOrderQuantities);
		httpServletRequest.setAttribute(
			"liferay-commerce:quantity-input:cpDefinitionId", _cpDefinitionId);
		httpServletRequest.setAttribute(
			"liferay-commerce:quantity-input:maxOrderQuantity",
			_maxOrderQuantity);
		httpServletRequest.setAttribute(
			"liferay-commerce:quantity-input:minOrderQuantity",
			_minOrderQuantity);
		httpServletRequest.setAttribute(
			"liferay-commerce:quantity-input:multipleOrderQuantity",
			_multipleOrderQuantity);
		httpServletRequest.setAttribute(
			"liferay-commerce:quantity-input:name", _name);
		httpServletRequest.setAttribute(
			"liferay-commerce:quantity-input:showLabel", _showLabel);
		httpServletRequest.setAttribute(
			"liferay-commerce:quantity-input:useSelect", _useSelect);
		httpServletRequest.setAttribute(
			"liferay-commerce:quantity-input:value", _value);
	}

	private static final String _PAGE = "/quantity_input/page.jsp";

	private BigDecimal[] _allowedOrderQuantities;
	private long _cpDefinitionId;
	private BigDecimal _maxOrderQuantity = BigDecimal.ZERO;
	private BigDecimal _minOrderQuantity = BigDecimal.ZERO;
	private BigDecimal _multipleOrderQuantity = BigDecimal.ZERO;
	private String _name;
	private boolean _showLabel = true;
	private boolean _useSelect = true;
	private BigDecimal _value = BigDecimal.ZERO;

}