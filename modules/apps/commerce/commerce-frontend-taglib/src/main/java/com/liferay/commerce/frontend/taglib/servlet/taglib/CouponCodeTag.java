/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.frontend.taglib.servlet.taglib;

import com.liferay.commerce.frontend.taglib.internal.servlet.ServletContextUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.service.CommerceOrderServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Alessio Antonio Rendina
 */
public class CouponCodeTag extends IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		try {
			_commerceOrder = CommerceOrderServiceUtil.getCommerceOrder(
				_commerceOrderId);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return SKIP_BODY;
		}

		return super.doStartTag();
	}

	public long getCommerceOrderId() {
		return _commerceOrderId;
	}

	public void setCommerceOrderId(long commerceOrderId) {
		_commerceOrderId = commerceOrderId;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_commerceOrder = null;
		_commerceOrderId = 0;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		HttpServletRequest parentHttpServletRequest = getRequest();

		parentHttpServletRequest.setAttribute(
			"liferay-commerce:coupon-code:commerceOrder", _commerceOrder);
	}

	private static final String _PAGE = "/coupon_code/page.jsp";

	private static final Log _log = LogFactoryUtil.getLog(CouponCodeTag.class);

	private CommerceOrder _commerceOrder;
	private long _commerceOrderId;

}