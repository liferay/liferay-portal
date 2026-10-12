/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.sales.room.web.internal.struts;

import com.liferay.digital.sales.room.web.internal.constants.DSRWebKeys;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gianmarco Brunialti Masera
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "path=/dsr/analytics/store_filters", service = StrutsAction.class
)
public class StoreAnalyticsFiltersStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		HttpSession httpSession = originalHttpServletRequest.getSession();

		httpSession.setAttribute(
			DSRWebKeys.DSR_ANALYTICS_STORE_FILTERS,
			ParamUtil.getString(httpServletRequest, "filters"));

		return null;
	}

	@Reference
	private Portal _portal;

}