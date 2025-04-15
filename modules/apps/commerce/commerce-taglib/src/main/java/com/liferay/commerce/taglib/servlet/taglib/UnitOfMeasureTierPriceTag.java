/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.taglib.servlet.taglib;

import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.taglib.servlet.taglib.internal.servlet.ServletContextUtil;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Stefano Motta
 */
public class UnitOfMeasureTierPriceTag extends IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		try {
			HttpServletRequest httpServletRequest = getRequest();

			CommerceContext commerceContext =
				(CommerceContext)httpServletRequest.getAttribute(
					CommerceWebKeys.COMMERCE_CONTEXT);

			if ((commerceContext == null) ||
				(commerceContext.getCommerceChannelId() == 0)) {

				return SKIP_BODY;
			}

			_commerceAccountId = CommerceUtil.getCommerceAccountId(
				commerceContext);

			_commerceChannelId = commerceContext.getCommerceChannelId();
		}
		catch (Exception exception) {
			_log.error(exception);

			return SKIP_BODY;
		}

		return super.doStartTag();
	}

	public long getCPInstanceId() {
		return _cpInstanceId;
	}

	public long getCProductId() {
		return _cProductId;
	}

	public String getNamespace() {
		return _namespace;
	}

	public void setCPInstanceId(long cpInstanceId) {
		_cpInstanceId = cpInstanceId;
	}

	public void setCProductId(long cProductId) {
		_cProductId = cProductId;
	}

	public void setNamespace(String namespace) {
		_namespace = namespace;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_commerceAccountId = 0;
		_commerceChannelId = 0;
		_cpInstanceId = 0;
		_cProductId = 0;
		_namespace = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		setNamespacedAttribute(
			httpServletRequest, "commerceAccountId", _commerceAccountId);
		setNamespacedAttribute(
			httpServletRequest, "commerceChannelId", _commerceChannelId);
		setNamespacedAttribute(
			httpServletRequest, "cpInstanceId", _cpInstanceId);
		setNamespacedAttribute(httpServletRequest, "namespace", _namespace);
		setNamespacedAttribute(httpServletRequest, "productId", _cProductId);
	}

	private static final String _ATTRIBUTE_NAMESPACE =
		"liferay-commerce:unit-of-measure-tier-price:";

	private static final String _PAGE = "/unit_of_measure_tier_price/page.jsp";

	private static final Log _log = LogFactoryUtil.getLog(
		UnitOfMeasureTierPriceTag.class);

	private long _commerceAccountId;
	private long _commerceChannelId;
	private long _cpInstanceId;
	private long _cProductId;
	private String _namespace;

}