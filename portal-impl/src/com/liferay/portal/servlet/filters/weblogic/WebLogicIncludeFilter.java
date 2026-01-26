/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.filters.weblogic;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.servlet.BaseFilter;
import com.liferay.portal.kernel.servlet.WrapHttpServletResponseFilter;
import com.liferay.portal.kernel.util.ServerDetector;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Minhchau Dang
 */
public class WebLogicIncludeFilter
	extends BaseFilter implements WrapHttpServletResponseFilter {

	@Override
	public HttpServletResponse getWrappedHttpServletResponse(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		WebLogicIncludeServletResponseFactory
			webLogicIncludeServletResponseFactory =
				_webLogicIncludeServletResponseFactorySnapshot.get();

		if (webLogicIncludeServletResponseFactory != null) {
			return webLogicIncludeServletResponseFactory.create(
				httpServletResponse);
		}

		return httpServletResponse;
	}

	@Override
	public boolean isFilterEnabled() {
		return ServerDetector.isWebLogic();
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WebLogicIncludeFilter.class);

	private static final Snapshot<WebLogicIncludeServletResponseFactory>
		_webLogicIncludeServletResponseFactorySnapshot = new Snapshot<>(
			WebLogicIncludeFilter.class,
			WebLogicIncludeServletResponseFactory.class);

}