/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.http;

import com.liferay.commerce.service.CommerceOrderItemServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>CommerceOrderItemServiceUtil</code> service
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
public class CommerceOrderItemServiceHttp {

	public static com.liferay.commerce.model.CommerceOrderItem
			addCommerceOrderItem(
				HttpPrincipal httpPrincipal, long commerceOrderId,
				long cpInstanceId, String json, java.math.BigDecimal quantity,
				long replacedCPInstanceId, java.math.BigDecimal shippedQuantity,
				String unitOfMeasureKey,
				com.liferay.commerce.context.CommerceContext commerceContext,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class, "addCommerceOrderItem",
				_addCommerceOrderItemParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceOrderId, cpInstanceId, json, quantity,
				replacedCPInstanceId, shippedQuantity, unitOfMeasureKey,
				commerceContext, serviceContext);

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

			return (com.liferay.commerce.model.CommerceOrderItem)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.model.CommerceOrderItem
			addOrUpdateCommerceOrderItem(
				HttpPrincipal httpPrincipal, long commerceOrderId,
				long cpInstanceId, String json, java.math.BigDecimal quantity,
				long replacedCPInstanceId, java.math.BigDecimal shippedQuantity,
				String unitOfMeasureKey,
				com.liferay.commerce.context.CommerceContext commerceContext,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class,
				"addOrUpdateCommerceOrderItem",
				_addOrUpdateCommerceOrderItemParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceOrderId, cpInstanceId, json, quantity,
				replacedCPInstanceId, shippedQuantity, unitOfMeasureKey,
				commerceContext, serviceContext);

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

			return (com.liferay.commerce.model.CommerceOrderItem)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int countSubscriptionCommerceOrderItems(
			HttpPrincipal httpPrincipal, long commerceOrderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class,
				"countSubscriptionCommerceOrderItems",
				_countSubscriptionCommerceOrderItemsParameterTypes2);

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

	public static void deleteCommerceOrderItem(
			HttpPrincipal httpPrincipal, long commerceOrderItemId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class, "deleteCommerceOrderItem",
				_deleteCommerceOrderItemParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceOrderItemId);

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

	public static void deleteCommerceOrderItem(
			HttpPrincipal httpPrincipal, long commerceOrderItemId,
			com.liferay.commerce.context.CommerceContext commerceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class, "deleteCommerceOrderItem",
				_deleteCommerceOrderItemParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceOrderItemId, commerceContext);

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

	public static void deleteCommerceOrderItems(
			HttpPrincipal httpPrincipal, long commerceOrderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class, "deleteCommerceOrderItems",
				_deleteCommerceOrderItemsParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceOrderId);

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

	public static void deleteMissingCommerceOrderItems(
			HttpPrincipal httpPrincipal, long commerceOrderId,
			Long[] commerceOrderItemIds, String[] externalReferenceCodes)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class,
				"deleteMissingCommerceOrderItems",
				_deleteMissingCommerceOrderItemsParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceOrderId, commerceOrderItemIds,
				externalReferenceCodes);

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

	public static com.liferay.commerce.model.CommerceOrderItem
			fetchCommerceOrderItem(
				HttpPrincipal httpPrincipal, long commerceOrderItemId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class, "fetchCommerceOrderItem",
				_fetchCommerceOrderItemParameterTypes7);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceOrderItemId);

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

			return (com.liferay.commerce.model.CommerceOrderItem)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.model.CommerceOrderItem
			fetchCommerceOrderItemByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class,
				"fetchCommerceOrderItemByExternalReferenceCode",
				_fetchCommerceOrderItemByExternalReferenceCodeParameterTypes8);

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

			return (com.liferay.commerce.model.CommerceOrderItem)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.commerce.model.CommerceOrderItem>
			getAvailableForShipmentCommerceOrderItems(
				HttpPrincipal httpPrincipal, long commerceOrderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class,
				"getAvailableForShipmentCommerceOrderItems",
				_getAvailableForShipmentCommerceOrderItemsParameterTypes9);

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

			return (java.util.List
				<com.liferay.commerce.model.CommerceOrderItem>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.commerce.model.CommerceOrderItem>
			getChildCommerceOrderItems(
				HttpPrincipal httpPrincipal, long parentCommerceOrderItemId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class,
				"getChildCommerceOrderItems",
				_getChildCommerceOrderItemsParameterTypes10);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, parentCommerceOrderItemId);

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
				<com.liferay.commerce.model.CommerceOrderItem>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.math.BigDecimal
			getCommerceInventoryWarehouseItemQuantity(
				HttpPrincipal httpPrincipal, long commerceOrderItemId,
				long commerceInventoryWarehouseId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class,
				"getCommerceInventoryWarehouseItemQuantity",
				_getCommerceInventoryWarehouseItemQuantityParameterTypes11);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceOrderItemId, commerceInventoryWarehouseId);

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

			return (java.math.BigDecimal)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.model.CommerceOrderItem
			getCommerceOrderItem(
				HttpPrincipal httpPrincipal, long commerceOrderItemId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class, "getCommerceOrderItem",
				_getCommerceOrderItemParameterTypes12);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceOrderItemId);

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

			return (com.liferay.commerce.model.CommerceOrderItem)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.commerce.model.CommerceOrderItem>
			getCommerceOrderItems(
				HttpPrincipal httpPrincipal, long commerceOrderId, int start,
				int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class, "getCommerceOrderItems",
				_getCommerceOrderItemsParameterTypes13);

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

			return (java.util.List
				<com.liferay.commerce.model.CommerceOrderItem>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.commerce.model.CommerceOrderItem>
			getCommerceOrderItems(
				HttpPrincipal httpPrincipal, long groupId,
				long commerceAccountId, int[] orderStatuses, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class, "getCommerceOrderItems",
				_getCommerceOrderItemsParameterTypes14);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, commerceAccountId, orderStatuses, start,
				end);

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
				<com.liferay.commerce.model.CommerceOrderItem>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getCommerceOrderItemsCount(
			HttpPrincipal httpPrincipal, long commerceOrderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class,
				"getCommerceOrderItemsCount",
				_getCommerceOrderItemsCountParameterTypes15);

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

	public static int getCommerceOrderItemsCount(
			HttpPrincipal httpPrincipal, long commerceOrderId,
			long cpInstanceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class,
				"getCommerceOrderItemsCount",
				_getCommerceOrderItemsCountParameterTypes16);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceOrderId, cpInstanceId);

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

	public static int getCommerceOrderItemsCount(
			HttpPrincipal httpPrincipal, long groupId, long commerceAccountId,
			int[] orderStatuses)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class,
				"getCommerceOrderItemsCount",
				_getCommerceOrderItemsCountParameterTypes17);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, commerceAccountId, orderStatuses);

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

	public static java.math.BigDecimal getCommerceOrderItemsQuantity(
			HttpPrincipal httpPrincipal, long commerceOrderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class,
				"getCommerceOrderItemsQuantity",
				_getCommerceOrderItemsQuantityParameterTypes18);

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

			return (java.math.BigDecimal)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List<com.liferay.commerce.model.CommerceOrderItem>
			getParentCommerceOrderItems(
				HttpPrincipal httpPrincipal, long commerceOrderId,
				long parentCommerceOrderItemId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.commerce.model.CommerceOrderItem>
						orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class,
				"getParentCommerceOrderItems",
				_getParentCommerceOrderItemsParameterTypes19);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceOrderId, parentCommerceOrderItemId, start,
				end, orderByComparator);

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
				<com.liferay.commerce.model.CommerceOrderItem>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getParentCommerceOrderItemsCount(
			HttpPrincipal httpPrincipal, long commerceOrderId,
			long parentCommerceOrderItemId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class,
				"getParentCommerceOrderItemsCount",
				_getParentCommerceOrderItemsCountParameterTypes20);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceOrderId, parentCommerceOrderItemId);

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

	public static java.util.List<com.liferay.commerce.model.CommerceOrderItem>
			getSupplierCommerceOrderItems(
				HttpPrincipal httpPrincipal, long customerCommerceOrderItemId,
				int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class,
				"getSupplierCommerceOrderItems",
				_getSupplierCommerceOrderItemsParameterTypes21);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, customerCommerceOrderItemId, start, end);

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
				<com.liferay.commerce.model.CommerceOrderItem>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.model.CommerceOrderItem
			importCommerceOrderItem(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long commerceOrderItemId, long commerceOrderId,
				long cpInstanceId, String cpMeasurementUnitKey, String json,
				java.math.BigDecimal quantity,
				java.math.BigDecimal shippedQuantity,
				java.math.BigDecimal unitOfMeasureIncrementalOrderQuantity,
				String unitOfMeasureKey,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class, "importCommerceOrderItem",
				_importCommerceOrderItemParameterTypes22);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, commerceOrderItemId,
				commerceOrderId, cpInstanceId, cpMeasurementUnitKey, json,
				quantity, shippedQuantity,
				unitOfMeasureIncrementalOrderQuantity, unitOfMeasureKey,
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

			return (com.liferay.commerce.model.CommerceOrderItem)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.commerce.model.CommerceOrderItem> searchCommerceOrderItems(
				HttpPrincipal httpPrincipal, long commerceOrderId,
				long parentCommerceOrderItemId, String keywords, int start,
				int end, com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class, "searchCommerceOrderItems",
				_searchCommerceOrderItemsParameterTypes23);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceOrderId, parentCommerceOrderItemId, keywords,
				start, end, sort);

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

			return (com.liferay.portal.kernel.search.BaseModelSearchResult
				<com.liferay.commerce.model.CommerceOrderItem>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.commerce.model.CommerceOrderItem> searchCommerceOrderItems(
				HttpPrincipal httpPrincipal, long commerceOrderId,
				String keywords, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class, "searchCommerceOrderItems",
				_searchCommerceOrderItemsParameterTypes24);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceOrderId, keywords, start, end, sort);

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

			return (com.liferay.portal.kernel.search.BaseModelSearchResult
				<com.liferay.commerce.model.CommerceOrderItem>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.commerce.model.CommerceOrderItem> searchCommerceOrderItems(
				HttpPrincipal httpPrincipal, long commerceOrderId, String name,
				String sku, boolean andOperator, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class, "searchCommerceOrderItems",
				_searchCommerceOrderItemsParameterTypes25);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceOrderId, name, sku, andOperator, start, end,
				sort);

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

			return (com.liferay.portal.kernel.search.BaseModelSearchResult
				<com.liferay.commerce.model.CommerceOrderItem>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.model.CommerceOrderItem
			updateCommerceOrderItem(
				HttpPrincipal httpPrincipal, long commerceOrderItemId,
				java.math.BigDecimal quantity,
				com.liferay.commerce.context.CommerceContext commerceContext,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class, "updateCommerceOrderItem",
				_updateCommerceOrderItemParameterTypes26);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceOrderItemId, quantity, commerceContext,
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

			return (com.liferay.commerce.model.CommerceOrderItem)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.model.CommerceOrderItem
			updateCommerceOrderItem(
				HttpPrincipal httpPrincipal, long commerceOrderItemId,
				long cpMeasurementUnitId, java.math.BigDecimal quantity,
				com.liferay.commerce.context.CommerceContext commerceContext,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class, "updateCommerceOrderItem",
				_updateCommerceOrderItemParameterTypes27);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceOrderItemId, cpMeasurementUnitId, quantity,
				commerceContext, serviceContext);

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

			return (com.liferay.commerce.model.CommerceOrderItem)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.model.CommerceOrderItem
			updateCommerceOrderItem(
				HttpPrincipal httpPrincipal, long commerceOrderItemId,
				long cpMeasurementUnitId, java.math.BigDecimal quantity,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class, "updateCommerceOrderItem",
				_updateCommerceOrderItemParameterTypes28);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceOrderItemId, cpMeasurementUnitId, quantity,
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

			return (com.liferay.commerce.model.CommerceOrderItem)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.model.CommerceOrderItem
			updateCommerceOrderItem(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long commerceOrderItemId, String json,
				java.math.BigDecimal quantity,
				com.liferay.commerce.context.CommerceContext commerceContext,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class, "updateCommerceOrderItem",
				_updateCommerceOrderItemParameterTypes29);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, commerceOrderItemId, json,
				quantity, commerceContext, serviceContext);

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

			return (com.liferay.commerce.model.CommerceOrderItem)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.model.CommerceOrderItem
			updateCommerceOrderItemDeliveryDate(
				HttpPrincipal httpPrincipal, long commerceOrderItemId,
				java.util.Date requestedDeliveryDate)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class,
				"updateCommerceOrderItemDeliveryDate",
				_updateCommerceOrderItemDeliveryDateParameterTypes30);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceOrderItemId, requestedDeliveryDate);

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

			return (com.liferay.commerce.model.CommerceOrderItem)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.model.CommerceOrderItem
			updateCommerceOrderItemInfo(
				HttpPrincipal httpPrincipal, long commerceOrderItemId,
				long shippingAddressId, String deliveryGroupName,
				String printedNote)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class,
				"updateCommerceOrderItemInfo",
				_updateCommerceOrderItemInfoParameterTypes31);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceOrderItemId, shippingAddressId,
				deliveryGroupName, printedNote);

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

			return (com.liferay.commerce.model.CommerceOrderItem)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.model.CommerceOrderItem
			updateCommerceOrderItemInfo(
				HttpPrincipal httpPrincipal, long commerceOrderItemId,
				long shippingAddressId, String deliveryGroupName,
				String printedNote, int requestedDeliveryDateMonth,
				int requestedDeliveryDateDay, int requestedDeliveryDateYear)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class,
				"updateCommerceOrderItemInfo",
				_updateCommerceOrderItemInfoParameterTypes32);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceOrderItemId, shippingAddressId,
				deliveryGroupName, printedNote, requestedDeliveryDateMonth,
				requestedDeliveryDateDay, requestedDeliveryDateYear);

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

			return (com.liferay.commerce.model.CommerceOrderItem)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.model.CommerceOrderItem
			updateCommerceOrderItemInfo(
				HttpPrincipal httpPrincipal, long commerceOrderItemId,
				String deliveryGroupName, long shippingAddressId,
				String printedNote, int requestedDeliveryDateMonth,
				int requestedDeliveryDateDay, int requestedDeliveryDateYear,
				int requestedDeliveryDateHour, int requestedDeliveryDateMinute,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class,
				"updateCommerceOrderItemInfo",
				_updateCommerceOrderItemInfoParameterTypes33);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceOrderItemId, deliveryGroupName,
				shippingAddressId, printedNote, requestedDeliveryDateMonth,
				requestedDeliveryDateDay, requestedDeliveryDateYear,
				requestedDeliveryDateHour, requestedDeliveryDateMinute,
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

			return (com.liferay.commerce.model.CommerceOrderItem)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.model.CommerceOrderItem
			updateCommerceOrderItemPrices(
				HttpPrincipal httpPrincipal, long commerceOrderItemId,
				java.math.BigDecimal discountAmount,
				java.math.BigDecimal discountPercentageLevel1,
				java.math.BigDecimal discountPercentageLevel2,
				java.math.BigDecimal discountPercentageLevel3,
				java.math.BigDecimal discountPercentageLevel4,
				java.math.BigDecimal finalPrice,
				java.math.BigDecimal promoPrice, java.math.BigDecimal unitPrice)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class,
				"updateCommerceOrderItemPrices",
				_updateCommerceOrderItemPricesParameterTypes34);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceOrderItemId, discountAmount,
				discountPercentageLevel1, discountPercentageLevel2,
				discountPercentageLevel3, discountPercentageLevel4, finalPrice,
				promoPrice, unitPrice);

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

			return (com.liferay.commerce.model.CommerceOrderItem)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.model.CommerceOrderItem
			updateCommerceOrderItemPrices(
				HttpPrincipal httpPrincipal, long commerceOrderItemId,
				java.math.BigDecimal discountAmount,
				java.math.BigDecimal discountAmountWithTaxAmount,
				java.math.BigDecimal discountPercentageLevel1,
				java.math.BigDecimal discountPercentageLevel1WithTaxAmount,
				java.math.BigDecimal discountPercentageLevel2,
				java.math.BigDecimal discountPercentageLevel2WithTaxAmount,
				java.math.BigDecimal discountPercentageLevel3,
				java.math.BigDecimal discountPercentageLevel3WithTaxAmount,
				java.math.BigDecimal discountPercentageLevel4,
				java.math.BigDecimal discountPercentageLevel4WithTaxAmount,
				java.math.BigDecimal finalPrice,
				java.math.BigDecimal finalPriceWithTaxAmount,
				java.math.BigDecimal promoPrice,
				java.math.BigDecimal promoPriceWithTaxAmount,
				java.math.BigDecimal unitPrice,
				java.math.BigDecimal unitPriceWithTaxAmount)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class,
				"updateCommerceOrderItemPrices",
				_updateCommerceOrderItemPricesParameterTypes35);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceOrderItemId, discountAmount,
				discountAmountWithTaxAmount, discountPercentageLevel1,
				discountPercentageLevel1WithTaxAmount, discountPercentageLevel2,
				discountPercentageLevel2WithTaxAmount, discountPercentageLevel3,
				discountPercentageLevel3WithTaxAmount, discountPercentageLevel4,
				discountPercentageLevel4WithTaxAmount, finalPrice,
				finalPriceWithTaxAmount, promoPrice, promoPriceWithTaxAmount,
				unitPrice, unitPriceWithTaxAmount);

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

			return (com.liferay.commerce.model.CommerceOrderItem)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.model.CommerceOrderItem
			updateCommerceOrderItemUnitPrice(
				HttpPrincipal httpPrincipal, long commerceOrderItemId,
				java.math.BigDecimal unitPrice)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class,
				"updateCommerceOrderItemUnitPrice",
				_updateCommerceOrderItemUnitPriceParameterTypes36);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceOrderItemId, unitPrice);

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

			return (com.liferay.commerce.model.CommerceOrderItem)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.model.CommerceOrderItem
			updateCommerceOrderItemUnitPrice(
				HttpPrincipal httpPrincipal, long commerceOrderItemId,
				java.math.BigDecimal quantity, java.math.BigDecimal unitPrice)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class,
				"updateCommerceOrderItemUnitPrice",
				_updateCommerceOrderItemUnitPriceParameterTypes37);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceOrderItemId, quantity, unitPrice);

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

			return (com.liferay.commerce.model.CommerceOrderItem)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.model.CommerceOrderItem
			updateCustomFields(
				HttpPrincipal httpPrincipal, long commerceOrderItemId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class, "updateCustomFields",
				_updateCustomFieldsParameterTypes38);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceOrderItemId, serviceContext);

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

			return (com.liferay.commerce.model.CommerceOrderItem)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.model.CommerceOrderItem
			updateExternalReferenceCode(
				HttpPrincipal httpPrincipal, long commerceOrderItemId,
				String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceOrderItemServiceUtil.class,
				"updateExternalReferenceCode",
				_updateExternalReferenceCodeParameterTypes39);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceOrderItemId, externalReferenceCode);

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

			return (com.liferay.commerce.model.CommerceOrderItem)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		CommerceOrderItemServiceHttp.class);

	private static final Class<?>[] _addCommerceOrderItemParameterTypes0 =
		new Class[] {
			long.class, long.class, String.class, java.math.BigDecimal.class,
			long.class, java.math.BigDecimal.class, String.class,
			com.liferay.commerce.context.CommerceContext.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_addOrUpdateCommerceOrderItemParameterTypes1 = new Class[] {
			long.class, long.class, String.class, java.math.BigDecimal.class,
			long.class, java.math.BigDecimal.class, String.class,
			com.liferay.commerce.context.CommerceContext.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_countSubscriptionCommerceOrderItemsParameterTypes2 = new Class[] {
			long.class
		};
	private static final Class<?>[] _deleteCommerceOrderItemParameterTypes3 =
		new Class[] {long.class};
	private static final Class<?>[] _deleteCommerceOrderItemParameterTypes4 =
		new Class[] {
			long.class, com.liferay.commerce.context.CommerceContext.class
		};
	private static final Class<?>[] _deleteCommerceOrderItemsParameterTypes5 =
		new Class[] {long.class};
	private static final Class<?>[]
		_deleteMissingCommerceOrderItemsParameterTypes6 = new Class[] {
			long.class, Long[].class, String[].class
		};
	private static final Class<?>[] _fetchCommerceOrderItemParameterTypes7 =
		new Class[] {long.class};
	private static final Class<?>[]
		_fetchCommerceOrderItemByExternalReferenceCodeParameterTypes8 =
			new Class[] {String.class, long.class};
	private static final Class<?>[]
		_getAvailableForShipmentCommerceOrderItemsParameterTypes9 =
			new Class[] {long.class};
	private static final Class<?>[]
		_getChildCommerceOrderItemsParameterTypes10 = new Class[] {long.class};
	private static final Class<?>[]
		_getCommerceInventoryWarehouseItemQuantityParameterTypes11 =
			new Class[] {long.class, long.class};
	private static final Class<?>[] _getCommerceOrderItemParameterTypes12 =
		new Class[] {long.class};
	private static final Class<?>[] _getCommerceOrderItemsParameterTypes13 =
		new Class[] {long.class, int.class, int.class};
	private static final Class<?>[] _getCommerceOrderItemsParameterTypes14 =
		new Class[] {long.class, long.class, int[].class, int.class, int.class};
	private static final Class<?>[]
		_getCommerceOrderItemsCountParameterTypes15 = new Class[] {long.class};
	private static final Class<?>[]
		_getCommerceOrderItemsCountParameterTypes16 = new Class[] {
			long.class, long.class
		};
	private static final Class<?>[]
		_getCommerceOrderItemsCountParameterTypes17 = new Class[] {
			long.class, long.class, int[].class
		};
	private static final Class<?>[]
		_getCommerceOrderItemsQuantityParameterTypes18 = new Class[] {
			long.class
		};
	private static final Class<?>[]
		_getParentCommerceOrderItemsParameterTypes19 = new Class[] {
			long.class, long.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getParentCommerceOrderItemsCountParameterTypes20 = new Class[] {
			long.class, long.class
		};
	private static final Class<?>[]
		_getSupplierCommerceOrderItemsParameterTypes21 = new Class[] {
			long.class, int.class, int.class
		};
	private static final Class<?>[] _importCommerceOrderItemParameterTypes22 =
		new Class[] {
			String.class, long.class, long.class, long.class, String.class,
			String.class, java.math.BigDecimal.class,
			java.math.BigDecimal.class, java.math.BigDecimal.class,
			String.class, com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _searchCommerceOrderItemsParameterTypes23 =
		new Class[] {
			long.class, long.class, String.class, int.class, int.class,
			com.liferay.portal.kernel.search.Sort.class
		};
	private static final Class<?>[] _searchCommerceOrderItemsParameterTypes24 =
		new Class[] {
			long.class, String.class, int.class, int.class,
			com.liferay.portal.kernel.search.Sort.class
		};
	private static final Class<?>[] _searchCommerceOrderItemsParameterTypes25 =
		new Class[] {
			long.class, String.class, String.class, boolean.class, int.class,
			int.class, com.liferay.portal.kernel.search.Sort.class
		};
	private static final Class<?>[] _updateCommerceOrderItemParameterTypes26 =
		new Class[] {
			long.class, java.math.BigDecimal.class,
			com.liferay.commerce.context.CommerceContext.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _updateCommerceOrderItemParameterTypes27 =
		new Class[] {
			long.class, long.class, java.math.BigDecimal.class,
			com.liferay.commerce.context.CommerceContext.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _updateCommerceOrderItemParameterTypes28 =
		new Class[] {
			long.class, long.class, java.math.BigDecimal.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _updateCommerceOrderItemParameterTypes29 =
		new Class[] {
			String.class, long.class, String.class, java.math.BigDecimal.class,
			com.liferay.commerce.context.CommerceContext.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_updateCommerceOrderItemDeliveryDateParameterTypes30 = new Class[] {
			long.class, java.util.Date.class
		};
	private static final Class<?>[]
		_updateCommerceOrderItemInfoParameterTypes31 = new Class[] {
			long.class, long.class, String.class, String.class
		};
	private static final Class<?>[]
		_updateCommerceOrderItemInfoParameterTypes32 = new Class[] {
			long.class, long.class, String.class, String.class, int.class,
			int.class, int.class
		};
	private static final Class<?>[]
		_updateCommerceOrderItemInfoParameterTypes33 = new Class[] {
			long.class, String.class, long.class, String.class, int.class,
			int.class, int.class, int.class, int.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_updateCommerceOrderItemPricesParameterTypes34 = new Class[] {
			long.class, java.math.BigDecimal.class, java.math.BigDecimal.class,
			java.math.BigDecimal.class, java.math.BigDecimal.class,
			java.math.BigDecimal.class, java.math.BigDecimal.class,
			java.math.BigDecimal.class, java.math.BigDecimal.class
		};
	private static final Class<?>[]
		_updateCommerceOrderItemPricesParameterTypes35 = new Class[] {
			long.class, java.math.BigDecimal.class, java.math.BigDecimal.class,
			java.math.BigDecimal.class, java.math.BigDecimal.class,
			java.math.BigDecimal.class, java.math.BigDecimal.class,
			java.math.BigDecimal.class, java.math.BigDecimal.class,
			java.math.BigDecimal.class, java.math.BigDecimal.class,
			java.math.BigDecimal.class, java.math.BigDecimal.class,
			java.math.BigDecimal.class, java.math.BigDecimal.class,
			java.math.BigDecimal.class, java.math.BigDecimal.class
		};
	private static final Class<?>[]
		_updateCommerceOrderItemUnitPriceParameterTypes36 = new Class[] {
			long.class, java.math.BigDecimal.class
		};
	private static final Class<?>[]
		_updateCommerceOrderItemUnitPriceParameterTypes37 = new Class[] {
			long.class, java.math.BigDecimal.class, java.math.BigDecimal.class
		};
	private static final Class<?>[] _updateCustomFieldsParameterTypes38 =
		new Class[] {
			long.class, com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_updateExternalReferenceCodeParameterTypes39 = new Class[] {
			long.class, String.class
		};

}