/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.http;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.ListTypeServiceUtil;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>ListTypeServiceUtil</code> service
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
public class ListTypeServiceHttp {

	public static com.liferay.portal.kernel.model.ListType fetchListType(
		HttpPrincipal httpPrincipal, long companyId, String name, String type) {

		try {
			MethodKey methodKey = new MethodKey(
				ListTypeServiceUtil.class, "fetchListType",
				_fetchListTypeParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, name, type);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (com.liferay.portal.kernel.model.ListType)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.ListType getListType(
			HttpPrincipal httpPrincipal, long listTypeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ListTypeServiceUtil.class, "getListType",
				_getListTypeParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, listTypeId);

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

			return (com.liferay.portal.kernel.model.ListType)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.model.ListType getListType(
			HttpPrincipal httpPrincipal, long companyId, String name,
			String type)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ListTypeServiceUtil.class, "getListType",
				_getListTypeParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, name, type);

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

			return (com.liferay.portal.kernel.model.ListType)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static long getListTypeId(
			HttpPrincipal httpPrincipal, long companyId, String name,
			String type)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ListTypeServiceUtil.class, "getListTypeId",
				_getListTypeIdParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, name, type);

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

			return ((Long)returnObj).longValue();
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.portal.kernel.model.ListType>
		getListTypes(HttpPrincipal httpPrincipal, long companyId, String type) {

		try {
			MethodKey methodKey = new MethodKey(
				ListTypeServiceUtil.class, "getListTypes",
				_getListTypesParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, type);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List<com.liferay.portal.kernel.model.ListType>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void validate(
			HttpPrincipal httpPrincipal, long listTypeId, long classNameId,
			String type)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ListTypeServiceUtil.class, "validate",
				_validateParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, listTypeId, classNameId, type);

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

	public static void validate(
			HttpPrincipal httpPrincipal, long listTypeId, String type)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				ListTypeServiceUtil.class, "validate",
				_validateParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, listTypeId, type);

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

	private static Log _log = LogFactoryUtil.getLog(ListTypeServiceHttp.class);

	private static final Class<?>[] _fetchListTypeParameterTypes0 =
		new Class[] {long.class, String.class, String.class};
	private static final Class<?>[] _getListTypeParameterTypes1 = new Class[] {
		long.class
	};
	private static final Class<?>[] _getListTypeParameterTypes2 = new Class[] {
		long.class, String.class, String.class
	};
	private static final Class<?>[] _getListTypeIdParameterTypes3 =
		new Class[] {long.class, String.class, String.class};
	private static final Class<?>[] _getListTypesParameterTypes4 = new Class[] {
		long.class, String.class
	};
	private static final Class<?>[] _validateParameterTypes5 = new Class[] {
		long.class, long.class, String.class
	};
	private static final Class<?>[] _validateParameterTypes6 = new Class[] {
		long.class, String.class
	};

}
// LIFERAY-SERVICE-BUILDER-HASH:901028312