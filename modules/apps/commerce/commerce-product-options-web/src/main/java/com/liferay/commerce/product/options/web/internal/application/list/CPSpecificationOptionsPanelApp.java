/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.options.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.commerce.application.list.constants.CommercePanelCategoryKeys;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Portlet;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"panel.app.order:Integer=400",
		"panel.category.key=" + CommercePanelCategoryKeys.COMMERCE_PRODUCT_MANAGEMENT
	},
	service = PanelApp.class
)
public class CPSpecificationOptionsPanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "search-experiences";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "specifications");
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return CPPortletKeys.CP_SPECIFICATION_OPTIONS;
	}

	@Reference
	private Language _language;

	@Reference(
		target = "(jakarta.portlet.name=" + CPPortletKeys.CP_SPECIFICATION_OPTIONS + ")"
	)
	private Portlet _portlet;

}