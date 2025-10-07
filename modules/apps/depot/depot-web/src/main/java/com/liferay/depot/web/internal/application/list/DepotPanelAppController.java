/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.application.list;

import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppRegistry;
import com.liferay.application.list.display.context.logic.PanelCategoryHelper;
import com.liferay.depot.constants.DepotPortletKeys;
import com.liferay.depot.web.internal.application.controller.DepotApplicationController;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = DepotPanelAppController.class)
public class DepotPanelAppController {

	public boolean isShow(PanelApp panelApp, long groupId) {
		String portletId = panelApp.getPortletId();

		if (_isAlwaysShow(portletId)) {
			return true;
		}

		return _depotApplicationController.isEnabled(portletId, groupId);
	}

	public boolean isShow(String portletId) {
		if (_isAlwaysShow(portletId)) {
			return true;
		}

		return _depotApplicationController.isEnabled(portletId);
	}

	@Activate
	protected void activate() {
		_panelCategoryHelper = new PanelCategoryHelper(_panelAppRegistry);
	}

	private boolean _isAlwaysShow(String portletId) {
		if (portletId.equals(DepotPortletKeys.DEPOT_ADMIN) ||
			portletId.equals(DepotPortletKeys.DEPOT_SETTINGS) ||
			_panelCategoryHelper.isControlPanelApp(portletId) ||
			_panelCategoryHelper.isApplicationsMenuApp(portletId)) {

			return true;
		}

		return false;
	}

	@Reference
	private DepotApplicationController _depotApplicationController;

	@Reference
	private PanelAppRegistry _panelAppRegistry;

	private PanelCategoryHelper _panelCategoryHelper;

}