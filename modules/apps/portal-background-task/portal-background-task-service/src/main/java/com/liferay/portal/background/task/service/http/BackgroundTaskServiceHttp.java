/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.background.task.service.http;

import com.liferay.portal.background.task.service.BackgroundTaskServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>BackgroundTaskServiceUtil</code> service
 * utility. The
 * static methods of this class calls the same methods of the service utility.
 * However, the signatures are different because it requires an additional
 * <code>HttpPrincipal</code> parameter.
 *
 * <p>
 * The benefits of using the HTTP utility is that it is fast and allows for
 * tunneling without the cost of serializing to text. The drawback is that it
 * only works with Java.
 * </p>
 *
 * <p>
 * Set the property <b>tunnel.servlet.hosts.allowed</b> in portal.properties to
 * configure security.
 * </p>
 *
 * <p>
 * The HTTP utility is only generated for remote services.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class BackgroundTaskServiceHttp {

	public static String getBackgroundTaskStatusJSON(
		HttpPrincipal httpPrincipal, long backgroundTaskId) {

		try {
			MethodKey methodKey = new MethodKey(
				BackgroundTaskServiceUtil.class, "getBackgroundTaskStatusJSON",
				_getBackgroundTaskStatusJSONParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, backgroundTaskId);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (String)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getBackgroundTasksCount(
		HttpPrincipal httpPrincipal, long groupId, String taskExecutorClassName,
		boolean completed) {

		try {
			MethodKey methodKey = new MethodKey(
				BackgroundTaskServiceUtil.class, "getBackgroundTasksCount",
				_getBackgroundTasksCountParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, taskExecutorClassName, completed);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return ((Integer)returnObj).intValue();
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getBackgroundTasksCount(
		HttpPrincipal httpPrincipal, long groupId, String name,
		String taskExecutorClassName) {

		try {
			MethodKey methodKey = new MethodKey(
				BackgroundTaskServiceUtil.class, "getBackgroundTasksCount",
				_getBackgroundTasksCountParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, name, taskExecutorClassName);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return ((Integer)returnObj).intValue();
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		BackgroundTaskServiceHttp.class);

	private static final Class<?>[]
		_getBackgroundTaskStatusJSONParameterTypes0 = new Class[] {long.class};
	private static final Class<?>[] _getBackgroundTasksCountParameterTypes1 =
		new Class[] {long.class, String.class, boolean.class};
	private static final Class<?>[] _getBackgroundTasksCountParameterTypes2 =
		new Class[] {long.class, String.class, String.class};

}
// LIFERAY-SERVICE-BUILDER-HASH:-427986571