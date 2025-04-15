/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.access.control.AccessControlThreadLocal;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ProtectedClassLoaderObjectInputStream;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Michael Weisser
 * @author Brian Wing Shun Chan
 */
public class TunnelServlet extends HttpServlet {

	@Override
	public void doPost(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if ((permissionChecker == null) || !permissionChecker.isSignedIn()) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unauthenticated access is forbidden");
			}

			httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);

			return;
		}

		ObjectInputStream objectInputStream = null;

		Thread thread = Thread.currentThread();

		try {
			objectInputStream = new ProtectedClassLoaderObjectInputStream(
				httpServletRequest.getInputStream(),
				thread.getContextClassLoader());
		}
		catch (IOException ioException) {
			if (_log.isWarnEnabled()) {
				_log.warn(ioException);
			}

			return;
		}

		Object returnObject = null;

		boolean remoteAccess = AccessControlThreadLocal.isRemoteAccess();

		try {
			AccessControlThreadLocal.setRemoteAccess(true);

			ObjectValuePair<HttpPrincipal, MethodHandler> ovp =
				(ObjectValuePair<HttpPrincipal, MethodHandler>)
					objectInputStream.readObject();

			MethodHandler methodHandler = ovp.getValue();

			if (_log.isDebugEnabled()) {
				_log.debug("Method handler " + methodHandler);
			}

			if (methodHandler != null) {
				MethodKey methodKey = methodHandler.getMethodKey();

				if (!isValidRequest(methodKey.getDeclaringClass())) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Invalid request " + methodKey.getDeclaringClass());
					}

					return;
				}

				returnObject = methodHandler.invoke();
			}
		}
		catch (InvocationTargetException invocationTargetException) {
			_log.error(invocationTargetException);

			Throwable throwable = invocationTargetException.getCause();

			if (throwable == null) {
				returnObject = new SystemException();
			}
			else if (PropsValues.TUNNEL_SERVLET_HIDE_EXCEPTION_DATA) {
				Class<?> clazz = throwable.getClass();

				if (throwable instanceof PortalException) {
					returnObject = new PortalException(
						"Invocation failed due to " + clazz.getName());
				}
				else {
					returnObject = new SystemException(
						"Invocation failed due to " + clazz.getName());
				}
			}
			else if (throwable instanceof PortalException) {
				returnObject = throwable;
			}
			else {
				returnObject = new SystemException(throwable.getMessage());
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}
		finally {
			AccessControlThreadLocal.setRemoteAccess(remoteAccess);
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Return object " + returnObject);
		}

		if (returnObject != null) {
			try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(
					httpServletResponse.getOutputStream())) {

				objectOutputStream.writeObject(returnObject);
			}
			catch (IOException ioException) {
				_log.error(ioException);

				throw ioException;
			}
		}
	}

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		PortalUtil.sendError(
			HttpServletResponse.SC_NOT_FOUND,
			new IllegalArgumentException("The GET method is not supported"),
			httpServletRequest, httpServletResponse);
	}

	protected boolean isValidRequest(Class<?> clazz) {
		String className = clazz.getName();

		if (className.contains(".service.") &&
			className.endsWith("ServiceUtil") &&
			!className.endsWith("LocalServiceUtil")) {

			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(TunnelServlet.class);

}