/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.http;

import com.liferay.commerce.product.service.CPConfigurationEntryServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>CPConfigurationEntryServiceUtil</code> service
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
public class CPConfigurationEntryServiceHttp {

	public static com.liferay.commerce.product.model.CPConfigurationEntry
			addCPConfigurationEntry(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long groupId, long classNameId, long classPK,
				long cpConfigurationListId, long cpTaxCategoryId,
				String allowedOrderQuantities, boolean backOrders,
				long commerceAvailabilityEstimateId,
				String cpDefinitionInventoryEngine, double depth,
				boolean displayAvailability, boolean displayStockQuantity,
				boolean freeShipping, double height, String lowStockActivity,
				java.math.BigDecimal maxOrderQuantity,
				java.math.BigDecimal minOrderQuantity,
				java.math.BigDecimal minStockQuantity,
				java.math.BigDecimal multipleOrderQuantity, boolean purchasable,
				boolean shippable, double shippingExtraPrice,
				boolean shipSeparately, boolean taxExempt, double weight,
				double width)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPConfigurationEntryServiceUtil.class,
				"addCPConfigurationEntry",
				_addCPConfigurationEntryParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, groupId, classNameId, classPK,
				cpConfigurationListId, cpTaxCategoryId, allowedOrderQuantities,
				backOrders, commerceAvailabilityEstimateId,
				cpDefinitionInventoryEngine, depth, displayAvailability,
				displayStockQuantity, freeShipping, height, lowStockActivity,
				maxOrderQuantity, minOrderQuantity, minStockQuantity,
				multipleOrderQuantity, purchasable, shippable,
				shippingExtraPrice, shipSeparately, taxExempt, weight, width);

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

			return (com.liferay.commerce.product.model.CPConfigurationEntry)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void deleteCPConfigurationEntry(
			HttpPrincipal httpPrincipal, long cpConfigurationEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPConfigurationEntryServiceUtil.class,
				"deleteCPConfigurationEntry",
				_deleteCPConfigurationEntryParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpConfigurationEntryId);

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

	public static com.liferay.commerce.product.model.CPConfigurationEntry
			getCPConfigurationEntry(
				HttpPrincipal httpPrincipal, long cpConfigurationEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPConfigurationEntryServiceUtil.class,
				"getCPConfigurationEntry",
				_getCPConfigurationEntryParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpConfigurationEntryId);

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

			return (com.liferay.commerce.product.model.CPConfigurationEntry)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CPConfigurationEntry
			getCPConfigurationEntry(
				HttpPrincipal httpPrincipal, long classNameId, long classPK,
				long cpConfigurationListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPConfigurationEntryServiceUtil.class,
				"getCPConfigurationEntry",
				_getCPConfigurationEntryParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, classNameId, classPK, cpConfigurationListId);

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

			return (com.liferay.commerce.product.model.CPConfigurationEntry)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CPConfigurationEntry
			getCPConfigurationEntryByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPConfigurationEntryServiceUtil.class,
				"getCPConfigurationEntryByExternalReferenceCode",
				_getCPConfigurationEntryByExternalReferenceCodeParameterTypes4);

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

			return (com.liferay.commerce.product.model.CPConfigurationEntry)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.product.model.CPConfigurationEntry
			updateCPConfigurationEntry(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long cpConfigurationEntryId, long cpTaxCategoryId,
				String allowedOrderQuantities, boolean backOrders,
				long commerceAvailabilityEstimateId,
				String cpDefinitionInventoryEngine, double depth,
				boolean displayAvailability, boolean displayStockQuantity,
				boolean freeShipping, double height, String lowStockActivity,
				java.math.BigDecimal maxOrderQuantity,
				java.math.BigDecimal minOrderQuantity,
				java.math.BigDecimal minStockQuantity,
				java.math.BigDecimal multipleOrderQuantity, boolean purchasable,
				boolean shippable, double shippingExtraPrice,
				boolean shipSeparately, boolean taxExempt, double weight,
				double width)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CPConfigurationEntryServiceUtil.class,
				"updateCPConfigurationEntry",
				_updateCPConfigurationEntryParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, cpConfigurationEntryId,
				cpTaxCategoryId, allowedOrderQuantities, backOrders,
				commerceAvailabilityEstimateId, cpDefinitionInventoryEngine,
				depth, displayAvailability, displayStockQuantity, freeShipping,
				height, lowStockActivity, maxOrderQuantity, minOrderQuantity,
				minStockQuantity, multipleOrderQuantity, purchasable, shippable,
				shippingExtraPrice, shipSeparately, taxExempt, weight, width);

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

			return (com.liferay.commerce.product.model.CPConfigurationEntry)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		CPConfigurationEntryServiceHttp.class);

	private static final Class<?>[] _addCPConfigurationEntryParameterTypes0 =
		new Class[] {
			String.class, long.class, long.class, long.class, long.class,
			long.class, String.class, boolean.class, long.class, String.class,
			double.class, boolean.class, boolean.class, boolean.class,
			double.class, String.class, java.math.BigDecimal.class,
			java.math.BigDecimal.class, java.math.BigDecimal.class,
			java.math.BigDecimal.class, boolean.class, boolean.class,
			double.class, boolean.class, boolean.class, double.class,
			double.class
		};
	private static final Class<?>[] _deleteCPConfigurationEntryParameterTypes1 =
		new Class[] {long.class};
	private static final Class<?>[] _getCPConfigurationEntryParameterTypes2 =
		new Class[] {long.class};
	private static final Class<?>[] _getCPConfigurationEntryParameterTypes3 =
		new Class[] {long.class, long.class, long.class};
	private static final Class<?>[]
		_getCPConfigurationEntryByExternalReferenceCodeParameterTypes4 =
			new Class[] {String.class, long.class};
	private static final Class<?>[] _updateCPConfigurationEntryParameterTypes5 =
		new Class[] {
			String.class, long.class, long.class, String.class, boolean.class,
			long.class, String.class, double.class, boolean.class,
			boolean.class, boolean.class, double.class, String.class,
			java.math.BigDecimal.class, java.math.BigDecimal.class,
			java.math.BigDecimal.class, java.math.BigDecimal.class,
			boolean.class, boolean.class, double.class, boolean.class,
			boolean.class, double.class, double.class
		};

}