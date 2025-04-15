/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.web.internal.display.lar;

import com.liferay.exportimport.kernel.lar.DataLevel;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.portal.reports.engine.console.constants.ReportsEngineConsolePortletKeys;
import com.liferay.portal.reports.engine.console.web.internal.admin.lar.AdminPortletDataHandler;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Arthur Chan
 */
@Component(
	property = "jakarta.portlet.name=" + ReportsEngineConsolePortletKeys.DISPLAY_REPORTS,
	service = PortletDataHandler.class
)
public class DisplayPortletDataHandler extends AdminPortletDataHandler {

	@Activate
	@Override
	protected void activate() {
		super.activate();

		setDataLevel(DataLevel.PORTLET_INSTANCE);
	}

}