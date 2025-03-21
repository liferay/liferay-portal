/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.filters.fragment;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.BufferCacheServletResponse;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.servlet.filters.BasePortalFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Brian Wing Shun Chan
 */
public class FragmentFilter extends BasePortalFilter {

	public static final String SKIP_FILTER =
		FragmentFilter.class.getName() + "#SKIP_FILTER";

	@Override
	public boolean isFilterEnabled(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		if (isFragment(httpServletRequest, httpServletResponse) &&
			!isAlreadyFiltered(httpServletRequest)) {

			return true;
		}

		return false;
	}

	protected String getContent(
		HttpServletRequest httpServletRequest, String content) {

		String fragmentId = ParamUtil.getString(httpServletRequest, "p_f_id");

		int x = content.indexOf("<!-- Begin fragment " + fragmentId + " -->");
		int y = content.indexOf("<!-- End fragment " + fragmentId + " -->");

		if ((x == -1) || (y == -1)) {
			return content;
		}

		x = content.indexOf(">", x);

		return content.substring(x + 1, y);
	}

	protected boolean isAlreadyFiltered(HttpServletRequest httpServletRequest) {
		if (httpServletRequest.getAttribute(SKIP_FILTER) != null) {
			return true;
		}

		return false;
	}

	protected boolean isFragment(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		String fragmentId = ParamUtil.getString(httpServletRequest, "p_f_id");

		if (Validator.isNotNull(fragmentId)) {
			return true;
		}

		return false;
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		httpServletRequest.setAttribute(SKIP_FILTER, Boolean.TRUE);

		if (_log.isDebugEnabled()) {
			String completeURL = HttpComponentsUtil.getCompleteURL(
				httpServletRequest);

			_log.debug("Fragmenting " + completeURL);
		}

		BufferCacheServletResponse bufferCacheServletResponse =
			new BufferCacheServletResponse(httpServletResponse);

		processFilter(
			FragmentFilter.class.getName(), httpServletRequest,
			bufferCacheServletResponse, filterChain);

		String content = bufferCacheServletResponse.getString();

		ServletResponseUtil.write(
			httpServletResponse, getContent(httpServletRequest, content));
	}

	private static final Log _log = LogFactoryUtil.getLog(FragmentFilter.class);

}