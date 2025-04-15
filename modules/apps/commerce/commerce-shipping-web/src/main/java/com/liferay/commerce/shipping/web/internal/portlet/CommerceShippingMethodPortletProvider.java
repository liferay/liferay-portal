/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipping.web.internal.portlet;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.BasePortletProvider;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = "model.class.name=com.liferay.commerce.model.CommerceShippingMethod",
	service = PortletProvider.class
)
public class CommerceShippingMethodPortletProvider extends BasePortletProvider {

	@Override
	public String getPortletName() {
		return CommercePortletKeys.COMMERCE_SHIPPING_METHODS;
	}

	@Override
	public PortletURL getPortletURL(
			HttpServletRequest httpServletRequest, Group group)
		throws PortalException {

		return _portal.getControlPanelPortletURL(
			httpServletRequest, group, getPortletName(), 0, 0,
			PortletRequest.RENDER_PHASE);
	}

	@Override
	public Action[] getSupportedActions() {
		return _supportedActions;
	}

	@Reference
	private Portal _portal;

	private final Action[] _supportedActions = {Action.EDIT};

}