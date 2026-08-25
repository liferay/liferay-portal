/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service.http;

import com.liferay.commerce.price.list.service.CommercePriceEntryServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>CommercePriceEntryServiceUtil</code> service
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
public class CommercePriceEntryServiceHttp {

	public static com.liferay.commerce.price.list.model.CommercePriceEntry
			addCommercePriceEntry(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long cpInstanceId, long commercePriceListId,
				java.math.BigDecimal price, boolean priceOnApplication,
				java.math.BigDecimal promoPrice, String unitOfMeasureKey,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePriceEntryServiceUtil.class, "addCommercePriceEntry",
				_addCommercePriceEntryParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, cpInstanceId,
				commercePriceListId, price, priceOnApplication, promoPrice,
				unitOfMeasureKey, serviceContext);

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

			return (com.liferay.commerce.price.list.model.CommercePriceEntry)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.price.list.model.CommercePriceEntry
			addCommercePriceEntry(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long cProductId, String cpInstanceUuid,
				long commercePriceListId, java.math.BigDecimal price,
				boolean discountDiscovery, java.math.BigDecimal discountLevel1,
				java.math.BigDecimal discountLevel2,
				java.math.BigDecimal discountLevel3,
				java.math.BigDecimal discountLevel4, int displayDateMonth,
				int displayDateDay, int displayDateYear, int displayDateHour,
				int displayDateMinute, int expirationDateMonth,
				int expirationDateDay, int expirationDateYear,
				int expirationDateHour, int expirationDateMinute,
				boolean neverExpire, String unitOfMeasureKey,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePriceEntryServiceUtil.class, "addCommercePriceEntry",
				_addCommercePriceEntryParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, cProductId, cpInstanceUuid,
				commercePriceListId, price, discountDiscovery, discountLevel1,
				discountLevel2, discountLevel3, discountLevel4,
				displayDateMonth, displayDateDay, displayDateYear,
				displayDateHour, displayDateMinute, expirationDateMonth,
				expirationDateDay, expirationDateYear, expirationDateHour,
				expirationDateMinute, neverExpire, unitOfMeasureKey,
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

			return (com.liferay.commerce.price.list.model.CommercePriceEntry)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.price.list.model.CommercePriceEntry
			addOrUpdateCommercePriceEntry(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long commercePriceEntryId, long cProductId,
				String cpInstanceUuid, long commercePriceListId,
				java.math.BigDecimal price, java.math.BigDecimal promoPrice,
				String skuExternalReferenceCode, String unitOfMeasureKey,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePriceEntryServiceUtil.class,
				"addOrUpdateCommercePriceEntry",
				_addOrUpdateCommercePriceEntryParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, commercePriceEntryId,
				cProductId, cpInstanceUuid, commercePriceListId, price,
				promoPrice, skuExternalReferenceCode, unitOfMeasureKey,
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

			return (com.liferay.commerce.price.list.model.CommercePriceEntry)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.price.list.model.CommercePriceEntry
			addOrUpdateCommercePriceEntry(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long commercePriceEntryId, long cProductId,
				String cpInstanceUuid, long commercePriceListId,
				boolean discountDiscovery, java.math.BigDecimal discountLevel1,
				java.math.BigDecimal discountLevel2,
				java.math.BigDecimal discountLevel3,
				java.math.BigDecimal discountLevel4, int displayDateMonth,
				int displayDateDay, int displayDateYear, int displayDateHour,
				int displayDateMinute, int expirationDateMonth,
				int expirationDateDay, int expirationDateYear,
				int expirationDateHour, int expirationDateMinute,
				boolean neverExpire, java.math.BigDecimal price,
				boolean priceOnApplication, String skuExternalReferenceCode,
				String unitOfMeasureKey,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePriceEntryServiceUtil.class,
				"addOrUpdateCommercePriceEntry",
				_addOrUpdateCommercePriceEntryParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, commercePriceEntryId,
				cProductId, cpInstanceUuid, commercePriceListId,
				discountDiscovery, discountLevel1, discountLevel2,
				discountLevel3, discountLevel4, displayDateMonth,
				displayDateDay, displayDateYear, displayDateHour,
				displayDateMinute, expirationDateMonth, expirationDateDay,
				expirationDateYear, expirationDateHour, expirationDateMinute,
				neverExpire, price, priceOnApplication,
				skuExternalReferenceCode, unitOfMeasureKey, serviceContext);

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

			return (com.liferay.commerce.price.list.model.CommercePriceEntry)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void deleteCommercePriceEntry(
			HttpPrincipal httpPrincipal, long commercePriceEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePriceEntryServiceUtil.class, "deleteCommercePriceEntry",
				_deleteCommercePriceEntryParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commercePriceEntryId);

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

	public static com.liferay.commerce.price.list.model.CommercePriceEntry
			fetchCommercePriceEntry(
				HttpPrincipal httpPrincipal, long commercePriceEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePriceEntryServiceUtil.class, "fetchCommercePriceEntry",
				_fetchCommercePriceEntryParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commercePriceEntryId);

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

			return (com.liferay.commerce.price.list.model.CommercePriceEntry)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.price.list.model.CommercePriceEntry
			fetchCommercePriceEntryByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePriceEntryServiceUtil.class,
				"fetchCommercePriceEntryByExternalReferenceCode",
				_fetchCommercePriceEntryByExternalReferenceCodeParameterTypes6);

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

			return (com.liferay.commerce.price.list.model.CommercePriceEntry)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.commerce.price.list.model.CommercePriceEntry>
				getCommercePriceEntries(
					HttpPrincipal httpPrincipal, long commercePriceListId,
					int start, int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePriceEntryServiceUtil.class, "getCommercePriceEntries",
				_getCommercePriceEntriesParameterTypes7);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commercePriceListId, start, end);

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
				<com.liferay.commerce.price.list.model.CommercePriceEntry>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.commerce.price.list.model.CommercePriceEntry>
				getCommercePriceEntries(
					HttpPrincipal httpPrincipal, long commercePriceListId,
					int start, int end,
					com.liferay.portal.kernel.util.OrderByComparator
						<com.liferay.commerce.price.list.model.
							CommercePriceEntry> orderByComparator)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePriceEntryServiceUtil.class, "getCommercePriceEntries",
				_getCommercePriceEntriesParameterTypes8);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commercePriceListId, start, end, orderByComparator);

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
				<com.liferay.commerce.price.list.model.CommercePriceEntry>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getCommercePriceEntriesCount(
			HttpPrincipal httpPrincipal, long commercePriceListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePriceEntryServiceUtil.class,
				"getCommercePriceEntriesCount",
				_getCommercePriceEntriesCountParameterTypes9);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commercePriceListId);

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

	public static com.liferay.commerce.price.list.model.CommercePriceEntry
			getCommercePriceEntry(
				HttpPrincipal httpPrincipal, long commercePriceEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePriceEntryServiceUtil.class, "getCommercePriceEntry",
				_getCommercePriceEntryParameterTypes10);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commercePriceEntryId);

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

			return (com.liferay.commerce.price.list.model.CommercePriceEntry)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.price.list.model.CommercePriceEntry
		getInstanceBaseCommercePriceEntry(
			HttpPrincipal httpPrincipal, String cpInstanceUuid,
			String priceListType, String unitOfMeasureKey) {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePriceEntryServiceUtil.class,
				"getInstanceBaseCommercePriceEntry",
				_getInstanceBaseCommercePriceEntryParameterTypes11);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpInstanceUuid, priceListType, unitOfMeasureKey);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (com.liferay.commerce.price.list.model.CommercePriceEntry)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.commerce.price.list.model.CommercePriceEntry>
				getInstanceCommercePriceEntries(
					HttpPrincipal httpPrincipal, long cpInstanceId, int start,
					int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePriceEntryServiceUtil.class,
				"getInstanceCommercePriceEntries",
				_getInstanceCommercePriceEntriesParameterTypes12);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpInstanceId, start, end);

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
				<com.liferay.commerce.price.list.model.CommercePriceEntry>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getInstanceCommercePriceEntriesCount(
			HttpPrincipal httpPrincipal, long cpInstanceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePriceEntryServiceUtil.class,
				"getInstanceCommercePriceEntriesCount",
				_getInstanceCommercePriceEntriesCountParameterTypes13);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, cpInstanceId);

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

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.commerce.price.list.model.CommercePriceEntry>
				searchCommercePriceEntries(
					HttpPrincipal httpPrincipal, long companyId,
					long commercePriceListId, String keywords, int start,
					int end, com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePriceEntryServiceUtil.class,
				"searchCommercePriceEntries",
				_searchCommercePriceEntriesParameterTypes14);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, commercePriceListId, keywords, start, end,
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
				<com.liferay.commerce.price.list.model.CommercePriceEntry>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int searchCommercePriceEntriesCount(
			HttpPrincipal httpPrincipal, long companyId,
			long commercePriceListId, String keywords)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePriceEntryServiceUtil.class,
				"searchCommercePriceEntriesCount",
				_searchCommercePriceEntriesCountParameterTypes15);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, commercePriceListId, keywords);

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

	public static com.liferay.commerce.price.list.model.CommercePriceEntry
			updateCommercePriceEntry(
				HttpPrincipal httpPrincipal, long commercePriceEntryId,
				boolean bulkPricing, boolean discountDiscovery,
				java.math.BigDecimal discountLevel1,
				java.math.BigDecimal discountLevel2,
				java.math.BigDecimal discountLevel3,
				java.math.BigDecimal discountLevel4, int displayDateMonth,
				int displayDateDay, int displayDateYear, int displayDateHour,
				int displayDateMinute, int expirationDateMonth,
				int expirationDateDay, int expirationDateYear,
				int expirationDateHour, int expirationDateMinute,
				boolean neverExpire, java.math.BigDecimal price,
				boolean priceOnApplication, String unitOfMeasureKey,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePriceEntryServiceUtil.class, "updateCommercePriceEntry",
				_updateCommercePriceEntryParameterTypes16);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commercePriceEntryId, bulkPricing, discountDiscovery,
				discountLevel1, discountLevel2, discountLevel3, discountLevel4,
				displayDateMonth, displayDateDay, displayDateYear,
				displayDateHour, displayDateMinute, expirationDateMonth,
				expirationDateDay, expirationDateYear, expirationDateHour,
				expirationDateMinute, neverExpire, price, priceOnApplication,
				unitOfMeasureKey, serviceContext);

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

			return (com.liferay.commerce.price.list.model.CommercePriceEntry)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.price.list.model.CommercePriceEntry
			updateExternalReferenceCode(
				HttpPrincipal httpPrincipal,
				com.liferay.commerce.price.list.model.CommercePriceEntry
					commercePriceEntry,
				String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePriceEntryServiceUtil.class,
				"updateExternalReferenceCode",
				_updateExternalReferenceCodeParameterTypes17);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commercePriceEntry, externalReferenceCode);

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

			return (com.liferay.commerce.price.list.model.CommercePriceEntry)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.price.list.model.CommercePriceEntry
			updatePricingInfo(
				HttpPrincipal httpPrincipal, long commercePriceEntryId,
				boolean bulkPricing, java.math.BigDecimal price,
				boolean priceOnApplication, java.math.BigDecimal promoPrice,
				String unitOfMeasureKey,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommercePriceEntryServiceUtil.class, "updatePricingInfo",
				_updatePricingInfoParameterTypes18);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commercePriceEntryId, bulkPricing, price,
				priceOnApplication, promoPrice, unitOfMeasureKey,
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

			return (com.liferay.commerce.price.list.model.CommercePriceEntry)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		CommercePriceEntryServiceHttp.class);

	private static final Class<?>[] _addCommercePriceEntryParameterTypes0 =
		new Class[] {
			String.class, long.class, long.class, java.math.BigDecimal.class,
			boolean.class, java.math.BigDecimal.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _addCommercePriceEntryParameterTypes1 =
		new Class[] {
			String.class, long.class, String.class, long.class,
			java.math.BigDecimal.class, boolean.class,
			java.math.BigDecimal.class, java.math.BigDecimal.class,
			java.math.BigDecimal.class, java.math.BigDecimal.class, int.class,
			int.class, int.class, int.class, int.class, int.class, int.class,
			int.class, int.class, int.class, boolean.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_addOrUpdateCommercePriceEntryParameterTypes2 = new Class[] {
			String.class, long.class, long.class, String.class, long.class,
			java.math.BigDecimal.class, java.math.BigDecimal.class,
			String.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_addOrUpdateCommercePriceEntryParameterTypes3 = new Class[] {
			String.class, long.class, long.class, String.class, long.class,
			boolean.class, java.math.BigDecimal.class,
			java.math.BigDecimal.class, java.math.BigDecimal.class,
			java.math.BigDecimal.class, int.class, int.class, int.class,
			int.class, int.class, int.class, int.class, int.class, int.class,
			int.class, boolean.class, java.math.BigDecimal.class, boolean.class,
			String.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _deleteCommercePriceEntryParameterTypes4 =
		new Class[] {long.class};
	private static final Class<?>[] _fetchCommercePriceEntryParameterTypes5 =
		new Class[] {long.class};
	private static final Class<?>[]
		_fetchCommercePriceEntryByExternalReferenceCodeParameterTypes6 =
			new Class[] {String.class, long.class};
	private static final Class<?>[] _getCommercePriceEntriesParameterTypes7 =
		new Class[] {long.class, int.class, int.class};
	private static final Class<?>[] _getCommercePriceEntriesParameterTypes8 =
		new Class[] {
			long.class, int.class, int.class,
			com.liferay.portal.kernel.util.OrderByComparator.class
		};
	private static final Class<?>[]
		_getCommercePriceEntriesCountParameterTypes9 = new Class[] {long.class};
	private static final Class<?>[] _getCommercePriceEntryParameterTypes10 =
		new Class[] {long.class};
	private static final Class<?>[]
		_getInstanceBaseCommercePriceEntryParameterTypes11 = new Class[] {
			String.class, String.class, String.class
		};
	private static final Class<?>[]
		_getInstanceCommercePriceEntriesParameterTypes12 = new Class[] {
			long.class, int.class, int.class
		};
	private static final Class<?>[]
		_getInstanceCommercePriceEntriesCountParameterTypes13 = new Class[] {
			long.class
		};
	private static final Class<?>[]
		_searchCommercePriceEntriesParameterTypes14 = new Class[] {
			long.class, long.class, String.class, int.class, int.class,
			com.liferay.portal.kernel.search.Sort.class
		};
	private static final Class<?>[]
		_searchCommercePriceEntriesCountParameterTypes15 = new Class[] {
			long.class, long.class, String.class
		};
	private static final Class<?>[] _updateCommercePriceEntryParameterTypes16 =
		new Class[] {
			long.class, boolean.class, boolean.class,
			java.math.BigDecimal.class, java.math.BigDecimal.class,
			java.math.BigDecimal.class, java.math.BigDecimal.class, int.class,
			int.class, int.class, int.class, int.class, int.class, int.class,
			int.class, int.class, int.class, boolean.class,
			java.math.BigDecimal.class, boolean.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_updateExternalReferenceCodeParameterTypes17 = new Class[] {
			com.liferay.commerce.price.list.model.CommercePriceEntry.class,
			String.class
		};
	private static final Class<?>[] _updatePricingInfoParameterTypes18 =
		new Class[] {
			long.class, boolean.class, java.math.BigDecimal.class,
			boolean.class, java.math.BigDecimal.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};

}
// LIFERAY-SERVICE-BUILDER-HASH:1583959392