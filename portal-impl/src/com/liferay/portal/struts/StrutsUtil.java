/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.struts;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.DirectRequestDispatcherFactoryUtil;
import com.liferay.portal.struts.constants.ActionConstants;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author Brian Wing Shun Chan
 */
public class StrutsUtil {

	public static final String TEXT_HTML_DIR = "/html";

	public static void forward(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			ServletContext servletContext, String servletName,
			Throwable throwable, String uri)
		throws ServletException {

		if (_log.isDebugEnabled()) {
			_log.debug("Forward URI " + uri);
		}

		if (uri.equals(ActionConstants.COMMON_NULL)) {
			return;
		}

		if (!httpServletResponse.isCommitted()) {
			String path = TEXT_HTML_DIR.concat(uri);

			if (_log.isDebugEnabled()) {
				_log.debug("Forward path " + path);
			}

			RequestDispatcher requestDispatcher =
				DirectRequestDispatcherFactoryUtil.getRequestDispatcher(
					servletContext, path);

			if (throwable != null) {
				_setErrorPageAttributes(
					httpServletRequest, servletName, throwable);
			}

			try {
				requestDispatcher.forward(
					httpServletRequest, httpServletResponse);
			}
			catch (IOException ioException) {
				if (_log.isWarnEnabled()) {
					_log.warn(ioException);
				}
			}
			catch (RuntimeException | ServletException exception) {
				if (throwable == null) {
					if (exception instanceof ServletException) {
						ServletException servletException =
							(ServletException)exception;

						throwable = servletException.getRootCause();
					}

					if (throwable == null) {
						throwable = exception;
					}

					_setErrorPageAttributes(
						httpServletRequest, servletName, throwable);
				}

				String errorPath = TEXT_HTML_DIR + "/common/error.jsp";

				requestDispatcher =
					DirectRequestDispatcherFactoryUtil.getRequestDispatcher(
						servletContext, errorPath);

				try {
					requestDispatcher.forward(
						httpServletRequest, httpServletResponse);
				}
				catch (IOException ioException) {
					if (_log.isWarnEnabled()) {
						_log.warn(ioException);
					}
				}
				catch (ServletException servletException) {
					throw servletException;
				}
			}
			finally {
				if (throwable != null) {
					_removeErrorPageAttributes(httpServletRequest, throwable);
				}
			}
		}
		else if (_log.isWarnEnabled()) {
			_log.warn(uri + " is already committed");
		}
	}

	private static void _removeErrorPageAttributes(
		HttpServletRequest httpServletRequest, Throwable throwable) {

		if (throwable == httpServletRequest.getAttribute(
				RequestDispatcher.ERROR_EXCEPTION)) {

			httpServletRequest.removeAttribute(
				RequestDispatcher.ERROR_EXCEPTION);
			httpServletRequest.removeAttribute(
				RequestDispatcher.ERROR_EXCEPTION_TYPE);
			httpServletRequest.removeAttribute(RequestDispatcher.ERROR_MESSAGE);
			httpServletRequest.removeAttribute(
				RequestDispatcher.ERROR_REQUEST_URI);
			httpServletRequest.removeAttribute(
				RequestDispatcher.ERROR_SERVLET_NAME);
			httpServletRequest.removeAttribute(
				RequestDispatcher.ERROR_STATUS_CODE);
		}
	}

	private static void _setErrorPageAttributes(
		HttpServletRequest httpServletRequest, String servletName,
		Throwable throwable) {

		httpServletRequest.setAttribute(
			RequestDispatcher.ERROR_EXCEPTION, throwable);
		httpServletRequest.setAttribute(
			RequestDispatcher.ERROR_EXCEPTION_TYPE, throwable.getClass());
		httpServletRequest.setAttribute(
			RequestDispatcher.ERROR_MESSAGE, throwable.getMessage());
		httpServletRequest.setAttribute(
			RequestDispatcher.ERROR_REQUEST_URI,
			httpServletRequest.getRequestURI());
		httpServletRequest.setAttribute(
			RequestDispatcher.ERROR_SERVLET_NAME, servletName);
		httpServletRequest.setAttribute(
			RequestDispatcher.ERROR_STATUS_CODE,
			Integer.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
	}

	private static final Log _log = LogFactoryUtil.getLog(StrutsUtil.class);

}