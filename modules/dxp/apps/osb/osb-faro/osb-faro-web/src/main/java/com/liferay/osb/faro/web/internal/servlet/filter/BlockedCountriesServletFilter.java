/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.servlet.filter;

import com.liferay.ip.geocoder.IPGeocoder;
import com.liferay.ip.geocoder.IPInfo;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.BaseFilter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shinn Lok
 */
@Component(
	property = {
		"dispatcher=FORWARD", "dispatcher=REQUEST", "servlet-context-name=",
		"servlet-filter-name=Blocked Countries Filter", "url-pattern=/*"
	},
	service = Filter.class
)
public class BlockedCountriesServletFilter extends BaseFilter {

	@Override
	protected Log getLog() {
		return _log;
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		if (_isBlockedCountry(httpServletRequest)) {
			httpServletResponse.sendError(
				HttpServletResponse.SC_FORBIDDEN,
				"This content is not available in your country");

			return;
		}

		filterChain.doFilter(httpServletRequest, httpServletResponse);
	}

	private boolean _isBlockedCountry(HttpServletRequest httpServletRequest) {
		IPInfo ipInfo = _ipGeocoder.getIPInfo(httpServletRequest);

		return _blockedCountryCodes.contains(ipInfo.getCountryCode());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BlockedCountriesServletFilter.class);

	private static final List<String> _blockedCountryCodes = Arrays.asList(
		"CU", "IR", "KP", "SY");

	@Reference
	private IPGeocoder _ipGeocoder;

}