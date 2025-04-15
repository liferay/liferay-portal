/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.set.prototype.internal.exportimport.data.handler;

import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.layout.set.prototype.constants.LayoutSetPrototypePortletKeys;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Daniela Zapata Riesco
 */
@Component(
	property = "jakarta.portlet.name=" + LayoutSetPrototypePortletKeys.SITE_TEMPLATE_SETTINGS,
	service = PortletDataHandler.class
)
public class SiteTemplateSettingsPortletDataHandler
	extends LayoutSetPrototypePortletDataHandler {

	@Activate
	@Override
	protected void activate() {
		super.activate();
	}

}