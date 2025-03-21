/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipment.web.internal.display.context;

import com.liferay.commerce.constants.CommerceActionKeys;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.commerce.product.display.context.helper.CPRequestHelper;
import com.liferay.commerce.shipment.web.internal.portlet.action.helper.ActionHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Alessio Antonio Rendina
 */
public class BaseCommerceShipmentDisplayContext<T> {

	public BaseCommerceShipmentDisplayContext(
		ActionHelper actionHelper, HttpServletRequest httpServletRequest,
		PortletResourcePermission portletResourcePermission) {

		this.actionHelper = actionHelper;
		this.httpServletRequest = httpServletRequest;

		_portletResourcePermission = portletResourcePermission;

		cpRequestHelper = new CPRequestHelper(httpServletRequest);

		liferayPortletRequest = cpRequestHelper.getLiferayPortletRequest();
		liferayPortletResponse = cpRequestHelper.getLiferayPortletResponse();
	}

	public CommerceShipment getCommerceShipment() throws PortalException {
		if (_commerceShipment != null) {
			return _commerceShipment;
		}

		_commerceShipment = actionHelper.getCommerceShipment(
			cpRequestHelper.getRenderRequest());

		return _commerceShipment;
	}

	public long getCommerceShipmentId() throws PortalException {
		CommerceShipment commerceShipment = getCommerceShipment();

		if (commerceShipment == null) {
			return 0;
		}

		return commerceShipment.getCommerceShipmentId();
	}

	public String getKeywords() {
		return ParamUtil.getString(httpServletRequest, "keywords");
	}

	public PortletURL getPortletURL() throws PortalException {
		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		String redirect = ParamUtil.getString(httpServletRequest, "redirect");

		if (Validator.isNotNull(redirect)) {
			portletURL.setParameter("redirect", redirect);
		}

		CommerceShipment commerceShipment = getCommerceShipment();

		if (commerceShipment != null) {
			portletURL.setParameter(
				"commerceShipmentId", String.valueOf(getCommerceShipmentId()));
		}

		String delta = ParamUtil.getString(httpServletRequest, "delta");

		if (Validator.isNotNull(delta)) {
			portletURL.setParameter("delta", delta);
		}

		String deltaEntry = ParamUtil.getString(
			httpServletRequest, "deltaEntry");

		if (Validator.isNotNull(deltaEntry)) {
			portletURL.setParameter("deltaEntry", deltaEntry);
		}

		String keywords = getKeywords();

		if (Validator.isNotNull(keywords)) {
			portletURL.setParameter("keywords", keywords);
		}

		return portletURL;
	}

	public boolean hasManageCommerceShipmentsPermission() {
		return _portletResourcePermission.contains(
			cpRequestHelper.getPermissionChecker(), null,
			CommerceActionKeys.MANAGE_COMMERCE_SHIPMENTS);
	}

	protected final ActionHelper actionHelper;
	protected final CPRequestHelper cpRequestHelper;
	protected final HttpServletRequest httpServletRequest;
	protected final LiferayPortletRequest liferayPortletRequest;
	protected final LiferayPortletResponse liferayPortletResponse;

	private CommerceShipment _commerceShipment;
	private final PortletResourcePermission _portletResourcePermission;

}