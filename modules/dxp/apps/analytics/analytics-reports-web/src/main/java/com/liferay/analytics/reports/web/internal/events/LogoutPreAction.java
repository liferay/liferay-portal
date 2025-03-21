/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.web.internal.events;

import com.liferay.analytics.reports.web.internal.constants.ProductNavigationControlMenuEntryConstants;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuEntry;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yurena Cabrera
 */
@Component(property = "key=logout.events.pre", service = LifecycleAction.class)
public class LogoutPreAction extends Action {

	@Override
	public void run(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws ActionException {

		_analyticsReportsProductNavigationControlMenuEntry.setPanelState(
			httpServletRequest,
			ProductNavigationControlMenuEntryConstants.SESSION_CLICKS_KEY,
			"closed");
	}

	@Reference(
		target = "(component.name=com.liferay.analytics.reports.web.internal.product.navigation.control.menu.AnalyticsReportsProductNavigationControlMenuEntry)"
	)
	private ProductNavigationControlMenuEntry
		_analyticsReportsProductNavigationControlMenuEntry;

}