/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.http;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionVersionServiceUtil;

/**
 * Provides the HTTP utility for the
 * <code>KaleoDefinitionVersionServiceUtil</code> service
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
public class KaleoDefinitionVersionServiceHttp {

	public static void deleteKaleoDefinitionVersions(
			HttpPrincipal httpPrincipal, long companyId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KaleoDefinitionVersionServiceUtil.class,
				"deleteKaleoDefinitionVersions",
				_deleteKaleoDefinitionVersionsParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, name);

			try {
				TunnelUtil.invoke(httpPrincipal, methodHandler);
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
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion
			getKaleoDefinitionVersion(
				HttpPrincipal httpPrincipal, long companyId, String name,
				String version)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KaleoDefinitionVersionServiceUtil.class,
				"getKaleoDefinitionVersion",
				_getKaleoDefinitionVersionParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, name, version);

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

			return (com.liferay.portal.workflow.kaleo.model.
				KaleoDefinitionVersion)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion>
				getKaleoDefinitionVersions(
					HttpPrincipal httpPrincipal, long companyId, String name)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				KaleoDefinitionVersionServiceUtil.class,
				"getKaleoDefinitionVersions",
				_getKaleoDefinitionVersionsParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, name);

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

			return (java.util.List
				<com.liferay.portal.workflow.kaleo.model.
					KaleoDefinitionVersion>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		KaleoDefinitionVersionServiceHttp.class);

	private static final Class<?>[]
		_deleteKaleoDefinitionVersionsParameterTypes0 = new Class[] {
			long.class, String.class
		};
	private static final Class<?>[] _getKaleoDefinitionVersionParameterTypes1 =
		new Class[] {long.class, String.class, String.class};
	private static final Class<?>[] _getKaleoDefinitionVersionsParameterTypes2 =
		new Class[] {long.class, String.class};

}
// LIFERAY-SERVICE-BUILDER-HASH:-1873962181