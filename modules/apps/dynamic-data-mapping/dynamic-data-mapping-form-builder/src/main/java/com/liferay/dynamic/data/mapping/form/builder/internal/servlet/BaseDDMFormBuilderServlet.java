/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.builder.internal.servlet;

import com.liferay.portal.events.EventsProcessorUtil;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author Pedro Queiroz
 * @author Rafael Praxedes
 */
public class BaseDDMFormBuilderServlet extends HttpServlet {

	@Override
	public void service(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		_createContext(httpServletRequest, httpServletResponse);

		super.service(httpServletRequest, httpServletResponse);
	}

	private void _createContext(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			EventsProcessorUtil.process(
				PropsKeys.SERVLET_SERVICE_EVENTS_PRE,
				PropsValues.SERVLET_SERVICE_EVENTS_PRE, httpServletRequest,
				httpServletResponse);
		}
		catch (ActionException actionException) {
			if (_log.isDebugEnabled()) {
				_log.debug(actionException);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseDDMFormBuilderServlet.class);

}