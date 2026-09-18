/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service.http;

import com.liferay.commerce.discount.service.CommerceDiscountOrderTypeRelServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>CommerceDiscountOrderTypeRelServiceUtil</code> service
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
public class CommerceDiscountOrderTypeRelServiceHttp {

	public static
		com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel
				addCommerceDiscountOrderTypeRel(
					HttpPrincipal httpPrincipal, long commerceDiscountId,
					long commerceOrderTypeId, int priority,
					com.liferay.portal.kernel.service.ServiceContext
						serviceContext)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceDiscountOrderTypeRelServiceUtil.class,
				"addCommerceDiscountOrderTypeRel",
				_addCommerceDiscountOrderTypeRelParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceDiscountId, commerceOrderTypeId, priority,
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

			return (com.liferay.commerce.discount.model.
				CommerceDiscountOrderTypeRel)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void deleteCommerceDiscountOrderTypeRel(
			HttpPrincipal httpPrincipal, long commerceDiscountOrderTypeRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceDiscountOrderTypeRelServiceUtil.class,
				"deleteCommerceDiscountOrderTypeRel",
				_deleteCommerceDiscountOrderTypeRelParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceDiscountOrderTypeRelId);

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

	public static
		com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel
				fetchCommerceDiscountOrderTypeRel(
					HttpPrincipal httpPrincipal, long commerceDiscountId,
					long commerceOrderTypeId)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceDiscountOrderTypeRelServiceUtil.class,
				"fetchCommerceDiscountOrderTypeRel",
				_fetchCommerceDiscountOrderTypeRelParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceDiscountId, commerceOrderTypeId);

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

			return (com.liferay.commerce.discount.model.
				CommerceDiscountOrderTypeRel)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static
		com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel
				getCommerceDiscountOrderTypeRel(
					HttpPrincipal httpPrincipal,
					long commerceDiscountOrderTypeRelId)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceDiscountOrderTypeRelServiceUtil.class,
				"getCommerceDiscountOrderTypeRel",
				_getCommerceDiscountOrderTypeRelParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceDiscountOrderTypeRelId);

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

			return (com.liferay.commerce.discount.model.
				CommerceDiscountOrderTypeRel)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel>
				getCommerceDiscountOrderTypeRels(
					HttpPrincipal httpPrincipal, long commerceDiscountId,
					String name, int start, int end,
					com.liferay.portal.kernel.util.OrderByComparator
						<com.liferay.commerce.discount.model.
							CommerceDiscountOrderTypeRel> orderByComparator)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceDiscountOrderTypeRelServiceUtil.class,
				"getCommerceDiscountOrderTypeRels",
				_getCommerceDiscountOrderTypeRelsParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceDiscountId, name, start, end,
				orderByComparator);

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
				<com.liferay.commerce.discount.model.
					CommerceDiscountOrderTypeRel>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getCommerceDiscountOrderTypeRelsCount(
			HttpPrincipal httpPrincipal, long commerceDiscountId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceDiscountOrderTypeRelServiceUtil.class,
				"getCommerceDiscountOrderTypeRelsCount",
				_getCommerceDiscountOrderTypeRelsCountParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceDiscountId, name);

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

	public static
		com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel
				updatePriority(
					HttpPrincipal httpPrincipal,
					long commerceDiscountOrderTypeRelId, int priority)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceDiscountOrderTypeRelServiceUtil.class, "updatePriority",
				_updatePriorityParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceDiscountOrderTypeRelId, priority);

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

			return (com.liferay.commerce.discount.model.
				CommerceDiscountOrderTypeRel)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		CommerceDiscountOrderTypeRelServiceHttp.class);

	private static final Class<?>[]
		_addCommerceDiscountOrderTypeRelParameterTypes0 = new Class[] {
			long.class, long.class, int.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_deleteCommerceDiscountOrderTypeRelParameterTypes1 = new Class[] {
			long.class
		};
	private static final Class<?>[]
		_fetchCommerceDiscountOrderTypeRelParameterTypes2 = new Class[] {
			long.class, long.class
		};
	private static final Class<?>[]
		_getCommerceDiscountOrderTypeRelParameterTypes3 = new Class[] {
			long.class
		};
	private static final Class<?>[]
		_getCommerceDiscountOrderTypeRelsParameterTypes4 = new Class[] {
			long.class, String.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getCommerceDiscountOrderTypeRelsCountParameterTypes5 = new Class[] {
			long.class, String.class
		};
	private static final Class<?>[] _updatePriorityParameterTypes6 =
		new Class[] {long.class, int.class};

}
// LIFERAY-SERVICE-BUILDER-HASH:-1306972977