/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.marketplace.store.web.internal.portlet;

import com.liferay.marketplace.constants.MarketplaceStorePortletKeys;

import jakarta.portlet.Portlet;

import org.osgi.service.component.annotations.Component;

/**
 * @author Ryan Park
 * @author Joan Kim
 */
@Component(
	property = {
		"com.liferay.portlet.css-class-wrapper=marketplace-portlet",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.header-portlet-javascript=/js/legacy/main.js",
		"com.liferay.portlet.icon=/icons/purchased.png",
		"com.liferay.portlet.preferences-owned-by-group=false",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.description=",
		"jakarta.portlet.display-name=Purchased",
		"jakarta.portlet.init-param.add-process-action-success-action=false",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + MarketplaceStorePortletKeys.MARKETPLACE_PURCHASED,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class MarketplacePurchasedPortlet extends MarketplaceStorePortlet {

	@Override
	protected String getClientPortletId() {
		return MarketplaceStorePortletKeys.MARKETPLACE_PURCHASED;
	}

}