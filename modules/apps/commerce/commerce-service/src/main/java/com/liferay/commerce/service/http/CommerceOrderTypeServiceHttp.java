/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.http;

import com.liferay.commerce.service.CommerceOrderTypeServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>CommerceOrderTypeServiceUtil</code> service
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
 * @author Alessio Antonio Rendina
 * @generated
 */
public class CommerceOrderTypeServiceHttp {

	public static com.liferay.commerce.model.CommerceOrderType
			addCommerceOrderType(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				java.util.Map<java.util.Locale, String> nameMap,
				java.util.Map<java.util.Locale, String> descriptionMap,
				boolean active, int displayDateMonth, int displayDateDay,
				int displayDateYear, int displayDateHour, int displayDateMinute,
				int displayOrder, int expirationDateMonth,
				int expirationDateDay, int expirationDateYear,
				int expirationDateHour, int expirationDateMinute,
				boolean neverExpire,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderTypeServiceUtil.class, "addCommerceOrderType",
				_addCommerceOrderTypeParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, nameMap, descriptionMap,
				active, displayDateMonth, displayDateDay, displayDateYear,
				displayDateHour, displayDateMinute, displayOrder,
				expirationDateMonth, expirationDateDay, expirationDateYear,
				expirationDateHour, expirationDateMinute, neverExpire,
				serviceContext);

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

			return (com.liferay.commerce.model.CommerceOrderType)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.model.CommerceOrderType
			deleteCommerceOrderType(
				HttpPrincipal httpPrincipal, long commerceOrderTypeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderTypeServiceUtil.class, "deleteCommerceOrderType",
				_deleteCommerceOrderTypeParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceOrderTypeId);

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

			return (com.liferay.commerce.model.CommerceOrderType)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.model.CommerceOrderType
			fetchCommerceOrderType(
				HttpPrincipal httpPrincipal, long commerceOrderTypeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderTypeServiceUtil.class, "fetchCommerceOrderType",
				_fetchCommerceOrderTypeParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceOrderTypeId);

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

			return (com.liferay.commerce.model.CommerceOrderType)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.model.CommerceOrderType
			fetchCommerceOrderTypeByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderTypeServiceUtil.class,
				"fetchCommerceOrderTypeByExternalReferenceCode",
				_fetchCommerceOrderTypeByExternalReferenceCodeParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, companyId);

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

			return (com.liferay.commerce.model.CommerceOrderType)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.model.CommerceOrderType
			getCommerceOrderType(
				HttpPrincipal httpPrincipal, long commerceOrderTypeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderTypeServiceUtil.class, "getCommerceOrderType",
				_getCommerceOrderTypeParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceOrderTypeId);

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

			return (com.liferay.commerce.model.CommerceOrderType)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.commerce.model.CommerceOrderType>
			getCommerceOrderTypes(
				HttpPrincipal httpPrincipal, String className, long classPK,
				boolean active, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderTypeServiceUtil.class, "getCommerceOrderTypes",
				_getCommerceOrderTypesParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, className, classPK, active, start, end);

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
				<com.liferay.commerce.model.CommerceOrderType>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getCommerceOrderTypesCount(
			HttpPrincipal httpPrincipal, String className, long classPK,
			boolean active)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderTypeServiceUtil.class,
				"getCommerceOrderTypesCount",
				_getCommerceOrderTypesCountParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, className, classPK, active);

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

	public static com.liferay.commerce.model.CommerceOrderType
			getOrAddEmptyCommerceOrderType(
				HttpPrincipal httpPrincipal, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderTypeServiceUtil.class,
				"getOrAddEmptyCommerceOrderType",
				_getOrAddEmptyCommerceOrderTypeParameterTypes7);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode);

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

			return (com.liferay.commerce.model.CommerceOrderType)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.model.CommerceOrderType
			updateCommerceOrderType(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long commerceOrderTypeId,
				java.util.Map<java.util.Locale, String> nameMap,
				java.util.Map<java.util.Locale, String> descriptionMap,
				boolean active, int displayDateMonth, int displayDateDay,
				int displayDateYear, int displayDateHour, int displayDateMinute,
				int displayOrder, int expirationDateMonth,
				int expirationDateDay, int expirationDateYear,
				int expirationDateHour, int expirationDateMinute,
				boolean neverExpire,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderTypeServiceUtil.class, "updateCommerceOrderType",
				_updateCommerceOrderTypeParameterTypes8);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, commerceOrderTypeId, nameMap,
				descriptionMap, active, displayDateMonth, displayDateDay,
				displayDateYear, displayDateHour, displayDateMinute,
				displayOrder, expirationDateMonth, expirationDateDay,
				expirationDateYear, expirationDateHour, expirationDateMinute,
				neverExpire, serviceContext);

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

			return (com.liferay.commerce.model.CommerceOrderType)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.model.CommerceOrderType
			updateCommerceOrderTypeExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long commerceOrderTypeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderTypeServiceUtil.class,
				"updateCommerceOrderTypeExternalReferenceCode",
				_updateCommerceOrderTypeExternalReferenceCodeParameterTypes9);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, commerceOrderTypeId);

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

			return (com.liferay.commerce.model.CommerceOrderType)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		CommerceOrderTypeServiceHttp.class);

	private static final Class<?>[] _addCommerceOrderTypeParameterTypes0 =
		new Class[] {
			String.class, java.util.Map.class, java.util.Map.class,
			boolean.class, int.class, int.class, int.class, int.class,
			int.class, int.class, int.class, int.class, int.class, int.class,
			int.class, boolean.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _deleteCommerceOrderTypeParameterTypes1 =
		new Class[] {long.class};
	private static final Class<?>[] _fetchCommerceOrderTypeParameterTypes2 =
		new Class[] {long.class};
	private static final Class<?>[]
		_fetchCommerceOrderTypeByExternalReferenceCodeParameterTypes3 =
			new Class[] {String.class, long.class};
	private static final Class<?>[] _getCommerceOrderTypeParameterTypes4 =
		new Class[] {long.class};
	private static final Class<?>[] _getCommerceOrderTypesParameterTypes5 =
		new Class[] {
			String.class, long.class, boolean.class, int.class, int.class
		};
	private static final Class<?>[] _getCommerceOrderTypesCountParameterTypes6 =
		new Class[] {String.class, long.class, boolean.class};
	private static final Class<?>[]
		_getOrAddEmptyCommerceOrderTypeParameterTypes7 = new Class[] {
			String.class
		};
	private static final Class<?>[] _updateCommerceOrderTypeParameterTypes8 =
		new Class[] {
			String.class, long.class, java.util.Map.class, java.util.Map.class,
			boolean.class, int.class, int.class, int.class, int.class,
			int.class, int.class, int.class, int.class, int.class, int.class,
			int.class, boolean.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_updateCommerceOrderTypeExternalReferenceCodeParameterTypes9 =
			new Class[] {String.class, long.class};

}
// LIFERAY-SERVICE-BUILDER-HASH:-380170375