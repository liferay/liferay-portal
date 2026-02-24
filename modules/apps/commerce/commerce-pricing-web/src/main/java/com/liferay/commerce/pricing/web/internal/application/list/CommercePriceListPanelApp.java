/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.commerce.application.list.constants.CommercePanelCategoryKeys;
import com.liferay.commerce.pricing.constants.CommercePricingConstants;
import com.liferay.commerce.pricing.constants.CommercePricingPortletKeys;
import com.liferay.commerce.pricing.web.internal.util.CommercePricingUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"panel.app.order:Integer=100",
		"panel.category.key=" + CommercePanelCategoryKeys.COMMERCE_PRICING
	},
	service = PanelApp.class
)
public class CommercePriceListPanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "price-list";
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return CommercePricingPortletKeys.COMMERCE_PRICE_LIST;
	}

	@Override
	public boolean isShow(PermissionChecker permissionChecker, Group group)
		throws PortalException {

		boolean show = super.isShow(permissionChecker, group);

		if (show) {
			boolean viewCommercePriceLists = PortletPermissionUtil.contains(
				permissionChecker,
				CommercePricingPortletKeys.COMMERCE_PRICE_LIST,
				ActionKeys.VIEW);

			if (!viewCommercePriceLists ||
				!Objects.equals(
					CommercePricingUtil.getPricingEngineVersion(
						_configurationProvider),
					CommercePricingConstants.VERSION_2_0)) {

				show = false;
			}
		}

		return show;
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference(
		target = "(jakarta.portlet.name=" + CommercePricingPortletKeys.COMMERCE_PRICE_LIST + ")"
	)
	private Portlet _portlet;

}