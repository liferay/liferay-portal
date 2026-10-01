/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.marketplace.store.web.internal.component.enabler;

import com.liferay.marketplace.store.web.internal.application.list.MarketplacePurchasedPanelApp;
import com.liferay.marketplace.store.web.internal.application.list.MarketplaceStorePanelApp;
import com.liferay.marketplace.store.web.internal.portlet.MarketplacePurchasedPortlet;
import com.liferay.marketplace.store.web.internal.portlet.MarketplaceStorePortlet;
import com.liferay.portal.kernel.util.PropsValues;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Caio Farias
 */
@Component(service = {})
public class ComponentEnabler {

	@Activate
	protected void activate(ComponentContext componentContext) {
		if (!PropsValues.FIPS_ENABLED) {
			componentContext.enableComponent(
				MarketplacePurchasedPanelApp.class.getName());
			componentContext.enableComponent(
				MarketplacePurchasedPortlet.class.getName());
			componentContext.enableComponent(
				MarketplaceStorePanelApp.class.getName());
			componentContext.enableComponent(
				MarketplaceStorePortlet.class.getName());
		}
	}

}