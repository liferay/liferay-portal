/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.http;

import com.liferay.commerce.service.CommerceShipmentServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>CommerceShipmentServiceUtil</code> service
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
public class CommerceShipmentServiceHttp {

	public static com.liferay.commerce.model.CommerceShipment
			addCommerceShipment(
				HttpPrincipal httpPrincipal, long commerceOrderId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceShipmentServiceUtil.class, "addCommerceShipment",
				_addCommerceShipmentParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceOrderId, serviceContext);

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

			return (com.liferay.commerce.model.CommerceShipment)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.model.CommerceShipment
			addCommerceShipment(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId, long commerceAccountId, long commerceAddressId,
				long commerceShippingMethodId,
				String commerceShippingOptionName,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceShipmentServiceUtil.class, "addCommerceShipment",
				_addCommerceShipmentParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId, commerceAccountId,
				commerceAddressId, commerceShippingMethodId,
				commerceShippingOptionName, serviceContext);

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

			return (com.liferay.commerce.model.CommerceShipment)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void deleteCommerceShipment(
			HttpPrincipal httpPrincipal, long commerceShipmentId,
			boolean restoreStockQuantity)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceShipmentServiceUtil.class, "deleteCommerceShipment",
				_deleteCommerceShipmentParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceShipmentId, restoreStockQuantity);

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

	public static com.liferay.commerce.model.CommerceShipment
			fetchCommerceShipmentByExternalReferenceCode(
				HttpPrincipal httpPrincipal, long companyId,
				String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceShipmentServiceUtil.class,
				"fetchCommerceShipmentByExternalReferenceCode",
				_fetchCommerceShipmentByExternalReferenceCodeParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, externalReferenceCode);

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

			return (com.liferay.commerce.model.CommerceShipment)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.model.CommerceShipment
			getCommerceShipment(
				HttpPrincipal httpPrincipal, long commerceShipmentId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceShipmentServiceUtil.class, "getCommerceShipment",
				_getCommerceShipmentParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceShipmentId);

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

			return (com.liferay.commerce.model.CommerceShipment)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.model.CommerceShipment
			getCommerceShipment(
				HttpPrincipal httpPrincipal, long groupId,
				long commerceShipmentId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceShipmentServiceUtil.class, "getCommerceShipment",
				_getCommerceShipmentParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, commerceShipmentId);

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

			return (com.liferay.commerce.model.CommerceShipment)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.commerce.model.CommerceShipment>
			getCommerceShipments(
				HttpPrincipal httpPrincipal, long companyId, int status,
				int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.commerce.model.CommerceShipment>
						orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceShipmentServiceUtil.class, "getCommerceShipments",
				_getCommerceShipmentsParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, status, start, end, orderByComparator);

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

			return (java.util.List<com.liferay.commerce.model.CommerceShipment>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.commerce.model.CommerceShipment>
			getCommerceShipments(
				HttpPrincipal httpPrincipal, long companyId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.commerce.model.CommerceShipment>
						orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceShipmentServiceUtil.class, "getCommerceShipments",
				_getCommerceShipmentsParameterTypes7);

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

			return (java.util.List<com.liferay.commerce.model.CommerceShipment>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.commerce.model.CommerceShipment>
			getCommerceShipments(
				HttpPrincipal httpPrincipal, long companyId,
				long commerceAddressId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.commerce.model.CommerceShipment>
						orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceShipmentServiceUtil.class, "getCommerceShipments",
				_getCommerceShipmentsParameterTypes8);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, commerceAddressId, start, end,
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

			return (java.util.List<com.liferay.commerce.model.CommerceShipment>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.commerce.model.CommerceShipment>
			getCommerceShipments(
				HttpPrincipal httpPrincipal, long companyId, long[] groupIds,
				long[] commerceAccountIds, String keywords,
				int[] shipmentStatuses, boolean excludeShipmentStatus,
				int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceShipmentServiceUtil.class, "getCommerceShipments",
				_getCommerceShipmentsParameterTypes9);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, groupIds, commerceAccountIds, keywords,
				shipmentStatuses, excludeShipmentStatus, start, end);

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

			return (java.util.List<com.liferay.commerce.model.CommerceShipment>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.commerce.model.CommerceShipment>
			getCommerceShipmentsByOrderId(
				HttpPrincipal httpPrincipal, long commerceOrderId, int start,
				int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceShipmentServiceUtil.class,
				"getCommerceShipmentsByOrderId",
				_getCommerceShipmentsByOrderIdParameterTypes10);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceOrderId, start, end);

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

			return (java.util.List<com.liferay.commerce.model.CommerceShipment>)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getCommerceShipmentsCount(
			HttpPrincipal httpPrincipal, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceShipmentServiceUtil.class, "getCommerceShipmentsCount",
				_getCommerceShipmentsCountParameterTypes11);

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

	public static int getCommerceShipmentsCount(
			HttpPrincipal httpPrincipal, long companyId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceShipmentServiceUtil.class, "getCommerceShipmentsCount",
				_getCommerceShipmentsCountParameterTypes12);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, status);

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

	public static int getCommerceShipmentsCount(
			HttpPrincipal httpPrincipal, long companyId, long commerceAddressId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceShipmentServiceUtil.class, "getCommerceShipmentsCount",
				_getCommerceShipmentsCountParameterTypes13);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, commerceAddressId);

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

	public static int getCommerceShipmentsCount(
			HttpPrincipal httpPrincipal, long companyId, long[] groupIds,
			long[] commerceAccountIds, String keywords, int[] shipmentStatuses,
			boolean excludeShipmentStatus)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceShipmentServiceUtil.class, "getCommerceShipmentsCount",
				_getCommerceShipmentsCountParameterTypes14);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, groupIds, commerceAccountIds, keywords,
				shipmentStatuses, excludeShipmentStatus);

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

	public static int getCommerceShipmentsCountByOrderId(
			HttpPrincipal httpPrincipal, long commerceOrderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceShipmentServiceUtil.class,
				"getCommerceShipmentsCountByOrderId",
				_getCommerceShipmentsCountByOrderIdParameterTypes15);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceOrderId);

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

	public static com.liferay.commerce.model.CommerceShipment
			reprocessCommerceShipment(
				HttpPrincipal httpPrincipal, long commerceShipmentId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceShipmentServiceUtil.class, "reprocessCommerceShipment",
				_reprocessCommerceShipmentParameterTypes16);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceShipmentId);

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

			return (com.liferay.commerce.model.CommerceShipment)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.model.CommerceShipment updateAddress(
			HttpPrincipal httpPrincipal, String externalReferenceCode,
			long commerceShipmentId, String name, String description,
			String street1, String street2, String street3, String city,
			String zip, long regionId, long countryId, String phoneNumber,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceShipmentServiceUtil.class, "updateAddress",
				_updateAddressParameterTypes17);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, commerceShipmentId, name,
				description, street1, street2, street3, city, zip, regionId,
				countryId, phoneNumber, serviceContext);

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

			return (com.liferay.commerce.model.CommerceShipment)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.model.CommerceShipment
			updateCarrierDetails(
				HttpPrincipal httpPrincipal, long commerceShipmentId,
				long commerceShippingMethodId, String carrier,
				String trackingNumber, String trackingURL)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceShipmentServiceUtil.class, "updateCarrierDetails",
				_updateCarrierDetailsParameterTypes18);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceShipmentId, commerceShippingMethodId,
				carrier, trackingNumber, trackingURL);

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

			return (com.liferay.commerce.model.CommerceShipment)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.model.CommerceShipment
			updateCommerceShipment(
				HttpPrincipal httpPrincipal,
				com.liferay.commerce.model.CommerceShipment commerceShipment)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceShipmentServiceUtil.class, "updateCommerceShipment",
				_updateCommerceShipmentParameterTypes19);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceShipment);

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

			return (com.liferay.commerce.model.CommerceShipment)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.model.CommerceShipment
			updateCommerceShipment(
				HttpPrincipal httpPrincipal, long commerceShipmentId,
				long commerceShippingMethodId, String carrier,
				int expectedDateMonth, int expectedDateDay,
				int expectedDateYear, int expectedDateHour,
				int expectedDateMinute, int shippingDateMonth,
				int shippingDateDay, int shippingDateYear, int shippingDateHour,
				int shippingDateMinute, String trackingNumber,
				String trackingURL, int status,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceShipmentServiceUtil.class, "updateCommerceShipment",
				_updateCommerceShipmentParameterTypes20);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceShipmentId, commerceShippingMethodId,
				carrier, expectedDateMonth, expectedDateDay, expectedDateYear,
				expectedDateHour, expectedDateMinute, shippingDateMonth,
				shippingDateDay, shippingDateYear, shippingDateHour,
				shippingDateMinute, trackingNumber, trackingURL, status,
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

			return (com.liferay.commerce.model.CommerceShipment)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.model.CommerceShipment
			updateCommerceShipment(
				HttpPrincipal httpPrincipal, long commerceShipmentId,
				long commerceShippingMethodId, String carrier,
				int expectedDateMonth, int expectedDateDay,
				int expectedDateYear, int expectedDateHour,
				int expectedDateMinute, int shippingDateMonth,
				int shippingDateDay, int shippingDateYear, int shippingDateHour,
				int shippingDateMinute, String trackingNumber,
				String trackingURL, int status, String name, String description,
				String street1, String street2, String street3, String city,
				String zip, long regionId, long countryId, String phoneNumber,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceShipmentServiceUtil.class, "updateCommerceShipment",
				_updateCommerceShipmentParameterTypes21);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceShipmentId, commerceShippingMethodId,
				carrier, expectedDateMonth, expectedDateDay, expectedDateYear,
				expectedDateHour, expectedDateMinute, shippingDateMonth,
				shippingDateDay, shippingDateYear, shippingDateHour,
				shippingDateMinute, trackingNumber, trackingURL, status, name,
				description, street1, street2, street3, city, zip, regionId,
				countryId, phoneNumber, serviceContext);

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

			return (com.liferay.commerce.model.CommerceShipment)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.model.CommerceShipment
			updateExpectedDate(
				HttpPrincipal httpPrincipal, long commerceShipmentId,
				int expectedDateMonth, int expectedDateDay,
				int expectedDateYear, int expectedDateHour,
				int expectedDateMinute)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceShipmentServiceUtil.class, "updateExpectedDate",
				_updateExpectedDateParameterTypes22);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceShipmentId, expectedDateMonth,
				expectedDateDay, expectedDateYear, expectedDateHour,
				expectedDateMinute);

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

			return (com.liferay.commerce.model.CommerceShipment)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.model.CommerceShipment
			updateExternalReferenceCode(
				HttpPrincipal httpPrincipal, long commerceShipmentId,
				String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceShipmentServiceUtil.class,
				"updateExternalReferenceCode",
				_updateExternalReferenceCodeParameterTypes23);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceShipmentId, externalReferenceCode);

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

			return (com.liferay.commerce.model.CommerceShipment)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.model.CommerceShipment
			updateShippingDate(
				HttpPrincipal httpPrincipal, long commerceShipmentId,
				int shippingDateMonth, int shippingDateDay,
				int shippingDateYear, int shippingDateHour,
				int shippingDateMinute)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceShipmentServiceUtil.class, "updateShippingDate",
				_updateShippingDateParameterTypes24);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceShipmentId, shippingDateMonth,
				shippingDateDay, shippingDateYear, shippingDateHour,
				shippingDateMinute);

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

			return (com.liferay.commerce.model.CommerceShipment)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.model.CommerceShipment updateStatus(
			HttpPrincipal httpPrincipal, long commerceShipmentId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceShipmentServiceUtil.class, "updateStatus",
				_updateStatusParameterTypes25);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceShipmentId, status);

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

			return (com.liferay.commerce.model.CommerceShipment)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		CommerceShipmentServiceHttp.class);

	private static final Class<?>[] _addCommerceShipmentParameterTypes0 =
		new Class[] {
			long.class, com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _addCommerceShipmentParameterTypes1 =
		new Class[] {
			String.class, long.class, long.class, long.class, long.class,
			String.class, com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _deleteCommerceShipmentParameterTypes2 =
		new Class[] {long.class, boolean.class};
	private static final Class<?>[]
		_fetchCommerceShipmentByExternalReferenceCodeParameterTypes3 =
			new Class[] {long.class, String.class};
	private static final Class<?>[] _getCommerceShipmentParameterTypes4 =
		new Class[] {long.class};
	private static final Class<?>[] _getCommerceShipmentParameterTypes5 =
		new Class[] {long.class, long.class};
	private static final Class<?>[] _getCommerceShipmentsParameterTypes6 =
		new Class[] {
			long.class, int.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getCommerceShipmentsParameterTypes7 =
		new Class[] {
			long.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getCommerceShipmentsParameterTypes8 =
		new Class[] {
			long.class, long.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[] _getCommerceShipmentsParameterTypes9 =
		new Class[] {
			long.class, long[].class, long[].class, String.class, int[].class,
			boolean.class, int.class, int.class
		};
	private static final Class<?>[]
		_getCommerceShipmentsByOrderIdParameterTypes10 = new Class[] {
			long.class, int.class, int.class
		};
	private static final Class<?>[] _getCommerceShipmentsCountParameterTypes11 =
		new Class[] {long.class};
	private static final Class<?>[] _getCommerceShipmentsCountParameterTypes12 =
		new Class[] {long.class, int.class};
	private static final Class<?>[] _getCommerceShipmentsCountParameterTypes13 =
		new Class[] {long.class, long.class};
	private static final Class<?>[] _getCommerceShipmentsCountParameterTypes14 =
		new Class[] {
			long.class, long[].class, long[].class, String.class, int[].class,
			boolean.class
		};
	private static final Class<?>[]
		_getCommerceShipmentsCountByOrderIdParameterTypes15 = new Class[] {
			long.class
		};
	private static final Class<?>[] _reprocessCommerceShipmentParameterTypes16 =
		new Class[] {long.class};
	private static final Class<?>[] _updateAddressParameterTypes17 =
		new Class[] {
			String.class, long.class, String.class, String.class, String.class,
			String.class, String.class, String.class, String.class, long.class,
			long.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _updateCarrierDetailsParameterTypes18 =
		new Class[] {
			long.class, long.class, String.class, String.class, String.class
		};
	private static final Class<?>[] _updateCommerceShipmentParameterTypes19 =
		new Class[] {com.liferay.commerce.model.CommerceShipment.class};
	private static final Class<?>[] _updateCommerceShipmentParameterTypes20 =
		new Class[] {
			long.class, long.class, String.class, int.class, int.class,
			int.class, int.class, int.class, int.class, int.class, int.class,
			int.class, int.class, String.class, String.class, int.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _updateCommerceShipmentParameterTypes21 =
		new Class[] {
			long.class, long.class, String.class, int.class, int.class,
			int.class, int.class, int.class, int.class, int.class, int.class,
			int.class, int.class, String.class, String.class, int.class,
			String.class, String.class, String.class, String.class,
			String.class, String.class, String.class, long.class, long.class,
			String.class, com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _updateExpectedDateParameterTypes22 =
		new Class[] {
			long.class, int.class, int.class, int.class, int.class, int.class
		};
	private static final Class<?>[]
		_updateExternalReferenceCodeParameterTypes23 = new Class[] {
			long.class, String.class
		};
	private static final Class<?>[] _updateShippingDateParameterTypes24 =
		new Class[] {
			long.class, int.class, int.class, int.class, int.class, int.class
		};
	private static final Class<?>[] _updateStatusParameterTypes25 =
		new Class[] {long.class, int.class};

}
// LIFERAY-SERVICE-BUILDER-HASH:-1384728397