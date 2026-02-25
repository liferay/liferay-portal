/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.dispatch.constants.DispatchPortletKeys;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author guywandji
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"panel.app.order:Integer=1000",
		"panel.category.key=" + PanelCategoryKeys.CONTROL_PANEL_CONFIGURATION
	},
	service = PanelApp.class
)
public class DispatchPanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "sheets";
	}

	@Override
	public String getKey() {
		return _KEY;
	}

	@Override
	public String getLabel(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return _language.get(resourceBundle, _KEY);
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return DispatchPortletKeys.DISPATCH;
	}

	private static final String _KEY = "dispatch";

	@Reference
	private Language _language;

	@Reference(
		target = "(jakarta.portlet.name=" + DispatchPortletKeys.DISPATCH + ")"
	)
	private Portlet _portlet;

}