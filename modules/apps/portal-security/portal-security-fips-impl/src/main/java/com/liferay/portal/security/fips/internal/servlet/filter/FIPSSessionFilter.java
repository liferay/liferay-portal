/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.internal.servlet.filter;

import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.fips.configuration.FIPSSessionConfiguration;
import com.liferay.portal.security.fips.constants.FIPSConstants;
import com.liferay.portal.security.fips.util.FIPSUtil;
import com.liferay.portal.servlet.filters.BasePortalFilter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Manuele Castro
 */
@Component(
	property = {
		"dispatcher=FORWARD", "dispatcher=REQUEST", "servlet-context-name=",
		"servlet-filter-name=FIPS Session Lifetime Filter", "url-pattern=/*"
	},
	service = Filter.class
)
public class FIPSSessionFilter extends BasePortalFilter {

	@Override
	public boolean isFilterEnabled(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		if (!PropsValues.FIPS_ENABLED ||
			(CompanyThreadLocal.getCompanyId() == 0)) {

			return false;
		}

		return true;
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		HttpSession httpSession = httpServletRequest.getSession(false);

		if ((httpSession == null) ||
			(_portal.getUserId(httpServletRequest) == 0)) {

			super.processFilter(
				httpServletRequest, httpServletResponse, filterChain);

			return;
		}

		FIPSSessionConfiguration fipsSessionConfiguration =
			_configurationProvider.getCompanyConfiguration(
				FIPSSessionConfiguration.class,
				CompanyThreadLocal.getCompanyId());

		if (_isExpired(fipsSessionConfiguration, httpSession)) {
			httpSession.invalidate();
		}
		else {
			long idleTimeoutMinutes = FIPSUtil.toMinutes(
				fipsSessionConfiguration.idleTimeout(),
				fipsSessionConfiguration.idleTimeoutTimeUnit());

			int idleTimeout = (int)idleTimeoutMinutes * 60;

			httpSession.setMaxInactiveInterval(idleTimeout);

			httpServletRequest.setAttribute(
				WebKeys.FIPS_SESSION_IDLE_TIMEOUT, idleTimeout);
		}

		super.processFilter(
			httpServletRequest, httpServletResponse, filterChain);
	}

	private boolean _isExpired(
		FIPSSessionConfiguration fipsSessionConfiguration,
		HttpSession httpSession) {

		long sessionMaximumAge = GetterUtil.getLong(
			httpSession.getAttribute(FIPSConstants.FIPS_SESSION_MAXIMUM_AGE));

		if (sessionMaximumAge <= 0) {
			long maximumAgeMinutes = FIPSUtil.toMinutes(
				fipsSessionConfiguration.maximumAge(),
				fipsSessionConfiguration.maximumAgeTimeUnit());

			sessionMaximumAge =
				httpSession.getCreationTime() +
					(maximumAgeMinutes * Time.MINUTE);
		}

		if (System.currentTimeMillis() >= sessionMaximumAge) {
			return true;
		}

		return false;
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Portal _portal;

}