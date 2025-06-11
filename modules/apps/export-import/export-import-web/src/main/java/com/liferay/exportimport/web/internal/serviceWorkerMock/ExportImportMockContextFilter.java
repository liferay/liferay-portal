package com.liferay.exportimport.web.internal.exportImportMockContextFilter;

/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.BaseFilter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;

/**
 * @author Jaime León
 */
@Component(
	property = {
		"servlet-context-name=", "servlet-filter-name=Export Import Mock Context Filter",
		"url-pattern=/*",
	},
	service = Filter.class
)
public class ExportImportMockContextFilter extends BaseFilter {

	@Override
	protected Log getLog() {
		return _log;
	}

	@Override
	protected void processFilter(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		if (FeatureFlagManagerUtil.isEnabled("LPD-11309")){
			try {
				// TODO: Probar a meter la barra para que esten los dos scopes igual
				httpServletResponse.setHeader("Service-Worker-Allowed", "/group");
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}

		filterChain.doFilter(httpServletRequest, httpServletResponse);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExportImportMockContextFilter.class);

}