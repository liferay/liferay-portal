/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.http;

import com.liferay.object.service.ObjectEntryVersionServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>ObjectEntryVersionServiceUtil</code> service
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
 * @author Marco Leo
 * @generated
 */
public class ObjectEntryVersionServiceHttp {

	public static com.liferay.object.model.ObjectEntryVersion
			deleteObjectEntryVersion(
				HttpPrincipal httpPrincipal, long objectEntryId, int version)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryVersionServiceUtil.class, "deleteObjectEntryVersion",
				_deleteObjectEntryVersionParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectEntryId, version);

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

			return (com.liferay.object.model.ObjectEntryVersion)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.object.model.ObjectEntryVersion
			expireObjectEntryVersion(
				HttpPrincipal httpPrincipal,
				com.liferay.object.model.ObjectEntry objectEntry,
				com.liferay.portal.kernel.service.ServiceContext serviceContext,
				int version)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryVersionServiceUtil.class, "expireObjectEntryVersion",
				_expireObjectEntryVersionParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectEntry, serviceContext, version);

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

			return (com.liferay.object.model.ObjectEntryVersion)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void expireObjectEntryVersions(
			HttpPrincipal httpPrincipal,
			com.liferay.object.model.ObjectEntry objectEntry,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws Exception {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryVersionServiceUtil.class,
				"expireObjectEntryVersions",
				_expireObjectEntryVersionsParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectEntry, serviceContext);

			try {
				TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				if (exception instanceof Exception) {
					throw (Exception)exception;
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

	public static com.liferay.object.model.ObjectEntryVersion
			getObjectEntryVersion(
				HttpPrincipal httpPrincipal, long objectEntryId, int version)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryVersionServiceUtil.class, "getObjectEntryVersion",
				_getObjectEntryVersionParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectEntryId, version);

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

			return (com.liferay.object.model.ObjectEntryVersion)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.object.model.ObjectEntryVersion>
			getObjectEntryVersions(
				HttpPrincipal httpPrincipal, long objectEntryId, int start,
				int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryVersionServiceUtil.class, "getObjectEntryVersions",
				_getObjectEntryVersionsParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectEntryId, start, end);

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

			return (java.util.List<com.liferay.object.model.ObjectEntryVersion>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getObjectEntryVersionsCount(
			HttpPrincipal httpPrincipal, long objectEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ObjectEntryVersionServiceUtil.class,
				"getObjectEntryVersionsCount",
				_getObjectEntryVersionsCountParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, objectEntryId);

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

			return ((Integer)returnObj).intValue();
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		ObjectEntryVersionServiceHttp.class);

	private static final Class<?>[] _deleteObjectEntryVersionParameterTypes0 =
		new Class[] {long.class, int.class};
	private static final Class<?>[] _expireObjectEntryVersionParameterTypes1 =
		new Class[] {
			com.liferay.object.model.ObjectEntry.class,
			com.liferay.portal.kernel.service.ServiceContext.class, int.class
		};
	private static final Class<?>[] _expireObjectEntryVersionsParameterTypes2 =
		new Class[] {
			com.liferay.object.model.ObjectEntry.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _getObjectEntryVersionParameterTypes3 =
		new Class[] {long.class, int.class};
	private static final Class<?>[] _getObjectEntryVersionsParameterTypes4 =
		new Class[] {long.class, int.class, int.class};
	private static final Class<?>[]
		_getObjectEntryVersionsCountParameterTypes5 = new Class[] {long.class};

}