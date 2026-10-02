/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.http;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;
import com.liferay.portal.workflow.kaleo.service.KaleoInstanceServiceUtil;

/**
 * Provides the HTTP utility for the
 * <code>KaleoInstanceServiceUtil</code> service
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
public class KaleoInstanceServiceHttp {

	public static com.liferay.portal.workflow.kaleo.model.KaleoInstance
			addKaleoInstance(
				HttpPrincipal httpPrincipal, String kaleoDefinitionName,
				Integer kaleoDefinitionVersion, String transitionName,
				java.util.Map<String, java.io.Serializable> workflowContext,
				com.liferay.portal.kernel.service.ServiceContext serviceContext,
				boolean waitForCompletion)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KaleoInstanceServiceUtil.class, "addKaleoInstance",
				_addKaleoInstanceParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, kaleoDefinitionName, kaleoDefinitionVersion,
				transitionName, workflowContext, serviceContext,
				waitForCompletion);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				if (exception instanceof
						com.liferay.portal.kernel.exception.PortalException) {

					throw (com.liferay.portal.kernel.exception.PortalException)
						exception;
				}

				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (com.liferay.portal.workflow.kaleo.model.KaleoInstance)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.workflow.kaleo.model.KaleoInstance
			getKaleoInstance(HttpPrincipal httpPrincipal, long kaleoInstanceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KaleoInstanceServiceUtil.class, "getKaleoInstance",
				_getKaleoInstanceParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, kaleoInstanceId);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				if (exception instanceof
						com.liferay.portal.kernel.exception.PortalException) {

					throw (com.liferay.portal.kernel.exception.PortalException)
						exception;
				}

				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (com.liferay.portal.workflow.kaleo.model.KaleoInstance)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.workflow.kaleo.model.KaleoInstance
			updateKaleoInstance(
				HttpPrincipal httpPrincipal, long kaleoInstanceId,
				java.util.Map<String, java.io.Serializable> workflowContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KaleoInstanceServiceUtil.class, "updateKaleoInstance",
				_updateKaleoInstanceParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, kaleoInstanceId, workflowContext);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				if (exception instanceof
						com.liferay.portal.kernel.exception.PortalException) {

					throw (com.liferay.portal.kernel.exception.PortalException)
						exception;
				}

				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (com.liferay.portal.workflow.kaleo.model.KaleoInstance)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		KaleoInstanceServiceHttp.class);

	private static final Class<?>[] _addKaleoInstanceParameterTypes0 =
		new Class[] {
			String.class, Integer.class, String.class, java.util.Map.class,
			com.liferay.portal.kernel.service.ServiceContext.class,
			boolean.class
		};
	private static final Class<?>[] _getKaleoInstanceParameterTypes1 =
		new Class[] {long.class};
	private static final Class<?>[] _updateKaleoInstanceParameterTypes2 =
		new Class[] {long.class, java.util.Map.class};

}
// LIFERAY-SERVICE-BUILDER-HASH:1136246755