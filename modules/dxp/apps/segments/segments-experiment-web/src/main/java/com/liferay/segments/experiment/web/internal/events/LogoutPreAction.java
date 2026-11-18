/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.experiment.web.internal.events;

import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuEntry;
import com.liferay.segments.experiment.web.internal.constants.ProductNavigationControlMenuEntryConstants;
import com.liferay.segments.experiment.web.internal.util.SegmentsCookieManagerUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Yurena Cabrera
 */
public class LogoutPreAction extends Action {

	public LogoutPreAction(
		ProductNavigationControlMenuEntry
			segmentsExperimentProductNavigationControlMenuEntry) {

		_segmentsExperimentProductNavigationControlMenuEntry =
			segmentsExperimentProductNavigationControlMenuEntry;
	}

	@Override
	public void run(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws ActionException {

		_segmentsExperimentProductNavigationControlMenuEntry.setPanelState(
			httpServletRequest,
			ProductNavigationControlMenuEntryConstants.SESSION_CLICKS_KEY,
			"closed");

		SegmentsCookieManagerUtil.cleanCookieLogoutAction(
			httpServletRequest, httpServletResponse);
	}

	private final ProductNavigationControlMenuEntry
		_segmentsExperimentProductNavigationControlMenuEntry;

}