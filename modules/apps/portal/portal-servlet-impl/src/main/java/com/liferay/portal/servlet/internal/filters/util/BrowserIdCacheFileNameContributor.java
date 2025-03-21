/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.internal.filters.util;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.servlet.BrowserSnifferUtil;
import com.liferay.portal.servlet.filters.util.CacheFileNameContributor;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Ugurcan Cetin
 */
@Component(service = CacheFileNameContributor.class)
public class BrowserIdCacheFileNameContributor
	implements CacheFileNameContributor {

	@Override
	public String getParameterName() {
		return "browserId";
	}

	@Override
	public String getParameterValue(HttpServletRequest httpServletRequest) {
		String browserId = ParamUtil.getString(httpServletRequest, "browserId");

		if (ArrayUtil.contains(_BROWSER_IDS, browserId)) {
			return browserId;
		}

		return null;
	}

	private static final String[] _BROWSER_IDS = {
		BrowserSnifferUtil.BROWSER_ID_EDGE,
		BrowserSnifferUtil.BROWSER_ID_FIREFOX, BrowserSnifferUtil.BROWSER_ID_IE,
		BrowserSnifferUtil.BROWSER_ID_OTHER
	};

}