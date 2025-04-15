/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.marketplace.app.manager.web.internal.portlet;

import com.liferay.marketplace.app.manager.web.internal.constants.MarketplaceAppManagerPortletKeys;
import com.liferay.portal.kernel.portlet.ControlPanelEntry;
import com.liferay.portal.kernel.portlet.OmniadminControlPanelEntry;

import org.osgi.service.component.annotations.Component;

/**
 * @author Christopher Kian
 */
@Component(
	property = "jakarta.portlet.name=" + MarketplaceAppManagerPortletKeys.MARKETPLACE_APP_MANAGER,
	service = ControlPanelEntry.class
)
public class MarketplaceAppManagerControlPanelEntry
	extends OmniadminControlPanelEntry {
}