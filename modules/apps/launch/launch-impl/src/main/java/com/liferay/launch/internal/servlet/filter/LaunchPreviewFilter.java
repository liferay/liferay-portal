/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.servlet.filter;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.launch.LaunchPreviewThreadLocal;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.preview.PreviewableResolverUtil;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = {
		"dispatcher=FORWARD", "dispatcher=REQUEST", "servlet-context-name=",
		"servlet-filter-name=Launch Preview Filter", "url-pattern=/*"
	},
	service = Filter.class
)
public class LaunchPreviewFilter extends BaseLaunchFilter {

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		long previewLaunchSetId = ParamUtil.getLong(
			httpServletRequest, "previewLaunchSetId", -1);

		if ((previewLaunchSetId == -1) ||
			(PreviewableResolverUtil.getPreviewId() != null)) {

			processFilter(
				LaunchPreviewFilter.class.getName(), httpServletRequest,
				httpServletResponse, filterChain);

			return;
		}

		long companyId = CompanyThreadLocal.getCompanyId();

		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-72278")) {
			processFilter(
				LaunchPreviewFilter.class.getName(), httpServletRequest,
				httpServletResponse, filterChain);

			return;
		}

		String mode = ParamUtil.getString(httpServletRequest, "p_l_mode");

		if (!mode.equals(Constants.PREVIEW)) {
			httpServletResponse.sendRedirect(
				HttpComponentsUtil.addParameter(
					_portal.getCurrentURL(httpServletRequest), "p_l_mode",
					Constants.PREVIEW));

			return;
		}

		User user = _portal.getUser(httpServletRequest);

		if ((user == null) || user.isGuestUser()) {
			httpServletResponse.sendRedirect(
				HttpComponentsUtil.addParameter(
					_portal.getPathMain() + "/portal/login", "redirect",
					_portal.getCurrentCompleteURL(httpServletRequest)));

			return;
		}

		try (SafeCloseable safeCloseable =
				LaunchPreviewThreadLocal.setLaunchSetIdWithSafeCloseable(
					previewLaunchSetId)) {

			processFilter(
				filterChain, httpServletRequest, httpServletResponse,
				getPreviewableMap(companyId, previewLaunchSetId));
		}
	}

	@Reference
	private Portal _portal;

}