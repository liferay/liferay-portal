/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.http;

import com.liferay.commerce.service.CommerceAvailabilityEstimateServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>CommerceAvailabilityEstimateServiceUtil</code> service
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
public class CommerceAvailabilityEstimateServiceHttp {

	public static com.liferay.commerce.model.CommerceAvailabilityEstimate
			addCommerceAvailabilityEstimate(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				java.util.Map<java.util.Locale, String> titleMap,
				double priority,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceAvailabilityEstimateServiceUtil.class,
				"addCommerceAvailabilityEstimate",
				_addCommerceAvailabilityEstimateParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, titleMap, priority,
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

			return (com.liferay.commerce.model.CommerceAvailabilityEstimate)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void deleteCommerceAvailabilityEstimate(
			HttpPrincipal httpPrincipal, long commerceAvailabilityEstimateId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceAvailabilityEstimateServiceUtil.class,
				"deleteCommerceAvailabilityEstimate",
				_deleteCommerceAvailabilityEstimateParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceAvailabilityEstimateId);

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

	public static com.liferay.commerce.model.CommerceAvailabilityEstimate
			fetchCommerceAvailabilityEstimateByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceAvailabilityEstimateServiceUtil.class,
				"fetchCommerceAvailabilityEstimateByExternalReferenceCode",
				_fetchCommerceAvailabilityEstimateByExternalReferenceCodeParameterTypes2);

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

			return (com.liferay.commerce.model.CommerceAvailabilityEstimate)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.model.CommerceAvailabilityEstimate
			getCommerceAvailabilityEstimate(
				HttpPrincipal httpPrincipal,
				long commerceAvailabilityEstimateId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceAvailabilityEstimateServiceUtil.class,
				"getCommerceAvailabilityEstimate",
				_getCommerceAvailabilityEstimateParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceAvailabilityEstimateId);

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

			return (com.liferay.commerce.model.CommerceAvailabilityEstimate)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.commerce.model.CommerceAvailabilityEstimate>
				getCommerceAvailabilityEstimates(
					HttpPrincipal httpPrincipal, long companyId, int start,
					int end,
					com.liferay.portal.kernel.util.OrderByComparator
						<com.liferay.commerce.model.
							CommerceAvailabilityEstimate> orderByComparator)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceAvailabilityEstimateServiceUtil.class,
				"getCommerceAvailabilityEstimates",
				_getCommerceAvailabilityEstimatesParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, start, end, orderByComparator);

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
				<com.liferay.commerce.model.CommerceAvailabilityEstimate>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getCommerceAvailabilityEstimatesCount(
			HttpPrincipal httpPrincipal, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceAvailabilityEstimateServiceUtil.class,
				"getCommerceAvailabilityEstimatesCount",
				_getCommerceAvailabilityEstimatesCountParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId);

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

	public static com.liferay.commerce.model.CommerceAvailabilityEstimate
			getOrAddEmptyCommerceAvailabilityEstimate(
				HttpPrincipal httpPrincipal, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceAvailabilityEstimateServiceUtil.class,
				"getOrAddEmptyCommerceAvailabilityEstimate",
				_getOrAddEmptyCommerceAvailabilityEstimateParameterTypes6);

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

			return (com.liferay.commerce.model.CommerceAvailabilityEstimate)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.model.CommerceAvailabilityEstimate
			updateCommerceAvailabilityEstimate(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long commerceAvailabilityEstimateId,
				java.util.Map<java.util.Locale, String> titleMap,
				double priority,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceAvailabilityEstimateServiceUtil.class,
				"updateCommerceAvailabilityEstimate",
				_updateCommerceAvailabilityEstimateParameterTypes7);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode,
				commerceAvailabilityEstimateId, titleMap, priority,
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

			return (com.liferay.commerce.model.CommerceAvailabilityEstimate)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		CommerceAvailabilityEstimateServiceHttp.class);

	private static final Class<?>[]
		_addCommerceAvailabilityEstimateParameterTypes0 = new Class[] {
			String.class, java.util.Map.class, double.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_deleteCommerceAvailabilityEstimateParameterTypes1 = new Class[] {
			long.class
		};
	private static final Class<?>[]
		_fetchCommerceAvailabilityEstimateByExternalReferenceCodeParameterTypes2 =
			new Class[] {String.class, long.class};
	private static final Class<?>[]
		_getCommerceAvailabilityEstimateParameterTypes3 = new Class[] {
			long.class
		};
	private static final Class<?>[]
		_getCommerceAvailabilityEstimatesParameterTypes4 = new Class[] {
			long.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getCommerceAvailabilityEstimatesCountParameterTypes5 = new Class[] {
			long.class
		};
	private static final Class<?>[]
		_getOrAddEmptyCommerceAvailabilityEstimateParameterTypes6 =
			new Class[] {String.class};
	private static final Class<?>[]
		_updateCommerceAvailabilityEstimateParameterTypes7 = new Class[] {
			String.class, long.class, java.util.Map.class, double.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};

}
// LIFERAY-SERVICE-BUILDER-HASH:-1847468056