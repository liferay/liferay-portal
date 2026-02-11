/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.applications.menu.web.internal.home;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.commerce.application.list.constants.CommercePanelCategoryKeys;
import com.liferay.product.navigation.applications.menu.web.internal.constants.CommerceHomePortletKeys;
import com.liferay.portal.kernel.model.Portlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.Locale;


/**
 * @author Iván Zaera Avellón
 */
@Component(
	property = {
		"panel.app.order:Integer=1000",
		"panel.category.key=" + CommercePanelCategoryKeys.COMMERCE_HOME
	},
	service = PanelApp.class
)
public class CommerceHomePanelApp extends BasePanelApp {

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return CommerceHomePortletKeys.COMMERCE_HOME;
	}

    @Override
	public String getLabel(Locale locale) {
		return "Home";
	}

	@Reference(
		target = "(jakarta.portlet.name=" + CommerceHomePortletKeys.COMMERCE_HOME + ")"
	)
	private Portlet _portlet;

}