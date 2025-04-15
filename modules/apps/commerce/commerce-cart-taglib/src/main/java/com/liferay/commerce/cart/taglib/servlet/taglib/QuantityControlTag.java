/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.cart.taglib.servlet.taglib;

import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Alessio Antonio Rendina
 */
public class QuantityControlTag extends IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		try {
			_commerceOrderItem = commerceOrderItemService.getCommerceOrderItem(
				_commerceOrderItemId);

			if (!_useSelect) {
				_updateOnChange = false;
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return SKIP_BODY;
		}

		return super.doStartTag();
	}

	public long getCommerceOrderItemId() {
		return _commerceOrderItemId;
	}

	public boolean isShowInputLabel() {
		return _showInputLabel;
	}

	public boolean isUpdateOnChange() {
		return _updateOnChange;
	}

	public boolean isUseSelect() {
		return _useSelect;
	}

	public void setCommerceOrderItemId(long commerceOrderItemId) {
		_commerceOrderItemId = commerceOrderItemId;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		commerceOrderItemService = _commerceOrderItemServiceSnapshot.get();
		setServletContext(_servletContextSnapshot.get());
	}

	public void setShowInputLabel(boolean showInputLabel) {
		_showInputLabel = showInputLabel;
	}

	public void setUpdateOnChange(boolean updateOnChange) {
		_updateOnChange = updateOnChange;
	}

	public void setUseSelect(boolean useSelect) {
		_useSelect = useSelect;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_commerceOrderItem = null;
		_commerceOrderItemId = 0;
		_showInputLabel = false;
		_updateOnChange = true;
		_useSelect = true;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest = getRequest();

		httpServletRequest.setAttribute(
			"liferay-commerce-cart:quantity-control:commerceOrderItem",
			_commerceOrderItem);
		httpServletRequest.setAttribute(
			"liferay-commerce-cart:quantity-control:showInputLabel",
			_showInputLabel);
		httpServletRequest.setAttribute(
			"liferay-commerce-cart:quantity-control:updateOnChange",
			_updateOnChange);
		httpServletRequest.setAttribute(
			"liferay-commerce-cart:quantity-control:useSelect", _useSelect);
	}

	protected CommerceOrderItemService commerceOrderItemService;

	private static final String _PAGE = "/quantity_control/page.jsp";

	private static final Log _log = LogFactoryUtil.getLog(
		QuantityControlTag.class);

	private static final Snapshot<CommerceOrderItemService>
		_commerceOrderItemServiceSnapshot = new Snapshot<>(
			QuantityControlTag.class, CommerceOrderItemService.class);
	private static final Snapshot<ServletContext> _servletContextSnapshot =
		new Snapshot<>(
			QuantityControlTag.class, ServletContext.class,
			"(osgi.web.symbolicname=com.liferay.commerce.cart.taglib)");

	private CommerceOrderItem _commerceOrderItem;
	private long _commerceOrderItemId;
	private boolean _showInputLabel;
	private boolean _updateOnChange = true;
	private boolean _useSelect = true;

}