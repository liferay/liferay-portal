/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.writer.interceptor;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.WriterInterceptor;
import jakarta.ws.rs.ext.WriterInterceptorContext;

import java.io.IOException;

/**
 * @author Carlos Correa
 * @author Luis Ortiz
 * @author Alejandro Tardín
 */
@Provider
public class ProblemWriterInterceptor implements WriterInterceptor {

	@Override
	public void aroundWriteTo(WriterInterceptorContext writerInterceptorContext)
		throws IOException {

		if (!_log.isDebugEnabled()) {
			writerInterceptorContext.proceed();

			return;
		}

		if (Problem.class.isAssignableFrom(
				writerInterceptorContext.getType())) {

			Problem problem = (Problem)writerInterceptorContext.getEntity();

			_log.debug(problem, problem.getThrowable());
		}

		writerInterceptorContext.proceed();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ProblemWriterInterceptor.class);

}