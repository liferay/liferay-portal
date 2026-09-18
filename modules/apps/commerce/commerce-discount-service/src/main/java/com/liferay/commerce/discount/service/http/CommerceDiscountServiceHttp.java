/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service.http;

import com.liferay.commerce.discount.service.CommerceDiscountServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

/**
 * Provides the HTTP utility for the
 * <code>CommerceDiscountServiceUtil</code> service
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
public class CommerceDiscountServiceHttp {

	public static com.liferay.commerce.discount.model.CommerceDiscount
			addCommerceDiscount(
				HttpPrincipal httpPrincipal, String title, String target,
				boolean useCouponCode, String couponCode, boolean usePercentage,
				java.math.BigDecimal maximumDiscountAmount,
				java.math.BigDecimal level1, java.math.BigDecimal level2,
				java.math.BigDecimal level3, java.math.BigDecimal level4,
				String limitationType, int limitationTimes, boolean active,
				int displayDateMonth, int displayDateDay, int displayDateYear,
				int displayDateHour, int displayDateMinute,
				int expirationDateMonth, int expirationDateDay,
				int expirationDateYear, int expirationDateHour,
				int expirationDateMinute, boolean neverExpire,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceDiscountServiceUtil.class, "addCommerceDiscount",
				_addCommerceDiscountParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, title, target, useCouponCode, couponCode,
				usePercentage, maximumDiscountAmount, level1, level2, level3,
				level4, limitationType, limitationTimes, active,
				displayDateMonth, displayDateDay, displayDateYear,
				displayDateHour, displayDateMinute, expirationDateMonth,
				expirationDateDay, expirationDateYear, expirationDateHour,
				expirationDateMinute, neverExpire, serviceContext);

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

			return (com.liferay.commerce.discount.model.CommerceDiscount)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.discount.model.CommerceDiscount
			addCommerceDiscount(
				HttpPrincipal httpPrincipal, String title, String target,
				boolean useCouponCode, String couponCode, boolean usePercentage,
				java.math.BigDecimal maximumDiscountAmount, String level,
				java.math.BigDecimal level1, java.math.BigDecimal level2,
				java.math.BigDecimal level3, java.math.BigDecimal level4,
				String limitationType, int limitationTimes,
				boolean rulesConjunction, boolean active, int displayDateMonth,
				int displayDateDay, int displayDateYear, int displayDateHour,
				int displayDateMinute, int expirationDateMonth,
				int expirationDateDay, int expirationDateYear,
				int expirationDateHour, int expirationDateMinute,
				boolean neverExpire,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceDiscountServiceUtil.class, "addCommerceDiscount",
				_addCommerceDiscountParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, title, target, useCouponCode, couponCode,
				usePercentage, maximumDiscountAmount, level, level1, level2,
				level3, level4, limitationType, limitationTimes,
				rulesConjunction, active, displayDateMonth, displayDateDay,
				displayDateYear, displayDateHour, displayDateMinute,
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

			return (com.liferay.commerce.discount.model.CommerceDiscount)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.discount.model.CommerceDiscount
			addCommerceDiscount(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				String title, String target, boolean useCouponCode,
				String couponCode, boolean usePercentage,
				java.math.BigDecimal maximumDiscountAmount, String level,
				java.math.BigDecimal level1, java.math.BigDecimal level2,
				java.math.BigDecimal level3, java.math.BigDecimal level4,
				String limitationType, int limitationTimes,
				int limitationTimesPerAccount, boolean rulesConjunction,
				boolean active, int displayDateMonth, int displayDateDay,
				int displayDateYear, int displayDateHour, int displayDateMinute,
				int expirationDateMonth, int expirationDateDay,
				int expirationDateYear, int expirationDateHour,
				int expirationDateMinute, boolean neverExpire,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceDiscountServiceUtil.class, "addCommerceDiscount",
				_addCommerceDiscountParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, title, target, useCouponCode,
				couponCode, usePercentage, maximumDiscountAmount, level, level1,
				level2, level3, level4, limitationType, limitationTimes,
				limitationTimesPerAccount, rulesConjunction, active,
				displayDateMonth, displayDateDay, displayDateYear,
				displayDateHour, displayDateMinute, expirationDateMonth,
				expirationDateDay, expirationDateYear, expirationDateHour,
				expirationDateMinute, neverExpire, serviceContext);

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

			return (com.liferay.commerce.discount.model.CommerceDiscount)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.discount.model.CommerceDiscount
			addOrUpdateCommerceDiscount(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long commerceDiscountId, String title, String target,
				boolean useCouponCode, String couponCode, boolean usePercentage,
				java.math.BigDecimal maximumDiscountAmount,
				java.math.BigDecimal level1, java.math.BigDecimal level2,
				java.math.BigDecimal level3, java.math.BigDecimal level4,
				String limitationType, int limitationTimes, boolean active,
				int displayDateMonth, int displayDateDay, int displayDateYear,
				int displayDateHour, int displayDateMinute,
				int expirationDateMonth, int expirationDateDay,
				int expirationDateYear, int expirationDateHour,
				int expirationDateMinute, boolean neverExpire,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceDiscountServiceUtil.class,
				"addOrUpdateCommerceDiscount",
				_addOrUpdateCommerceDiscountParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, commerceDiscountId, title,
				target, useCouponCode, couponCode, usePercentage,
				maximumDiscountAmount, level1, level2, level3, level4,
				limitationType, limitationTimes, active, displayDateMonth,
				displayDateDay, displayDateYear, displayDateHour,
				displayDateMinute, expirationDateMonth, expirationDateDay,
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

			return (com.liferay.commerce.discount.model.CommerceDiscount)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.discount.model.CommerceDiscount
			addOrUpdateCommerceDiscount(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long commerceDiscountId, String title, String target,
				boolean useCouponCode, String couponCode, boolean usePercentage,
				java.math.BigDecimal maximumDiscountAmount, String level,
				java.math.BigDecimal level1, java.math.BigDecimal level2,
				java.math.BigDecimal level3, java.math.BigDecimal level4,
				String limitationType, int limitationTimes,
				boolean rulesConjunction, boolean active, int displayDateMonth,
				int displayDateDay, int displayDateYear, int displayDateHour,
				int displayDateMinute, int expirationDateMonth,
				int expirationDateDay, int expirationDateYear,
				int expirationDateHour, int expirationDateMinute,
				boolean neverExpire,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceDiscountServiceUtil.class,
				"addOrUpdateCommerceDiscount",
				_addOrUpdateCommerceDiscountParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, commerceDiscountId, title,
				target, useCouponCode, couponCode, usePercentage,
				maximumDiscountAmount, level, level1, level2, level3, level4,
				limitationType, limitationTimes, rulesConjunction, active,
				displayDateMonth, displayDateDay, displayDateYear,
				displayDateHour, displayDateMinute, expirationDateMonth,
				expirationDateDay, expirationDateYear, expirationDateHour,
				expirationDateMinute, neverExpire, serviceContext);

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

			return (com.liferay.commerce.discount.model.CommerceDiscount)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.discount.model.CommerceDiscount
			addOrUpdateCommerceDiscount(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long commerceDiscountId, String title, String target,
				boolean useCouponCode, String couponCode, boolean usePercentage,
				java.math.BigDecimal maximumDiscountAmount, String level,
				java.math.BigDecimal level1, java.math.BigDecimal level2,
				java.math.BigDecimal level3, java.math.BigDecimal level4,
				String limitationType, int limitationTimes,
				int limitationTimesPerAccount, boolean rulesConjunction,
				boolean active, int displayDateMonth, int displayDateDay,
				int displayDateYear, int displayDateHour, int displayDateMinute,
				int expirationDateMonth, int expirationDateDay,
				int expirationDateYear, int expirationDateHour,
				int expirationDateMinute, boolean neverExpire,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceDiscountServiceUtil.class,
				"addOrUpdateCommerceDiscount",
				_addOrUpdateCommerceDiscountParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, commerceDiscountId, title,
				target, useCouponCode, couponCode, usePercentage,
				maximumDiscountAmount, level, level1, level2, level3, level4,
				limitationType, limitationTimes, limitationTimesPerAccount,
				rulesConjunction, active, displayDateMonth, displayDateDay,
				displayDateYear, displayDateHour, displayDateMinute,
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

			return (com.liferay.commerce.discount.model.CommerceDiscount)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static void deleteCommerceDiscount(
			HttpPrincipal httpPrincipal, long commerceDiscountId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceDiscountServiceUtil.class, "deleteCommerceDiscount",
				_deleteCommerceDiscountParameterTypes6);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceDiscountId);

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

	public static com.liferay.commerce.discount.model.CommerceDiscount
			fetchCommerceDiscount(
				HttpPrincipal httpPrincipal, long commerceDiscountId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceDiscountServiceUtil.class, "fetchCommerceDiscount",
				_fetchCommerceDiscountParameterTypes7);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceDiscountId);

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

			return (com.liferay.commerce.discount.model.CommerceDiscount)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.discount.model.CommerceDiscount
			fetchCommerceDiscountByExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceDiscountServiceUtil.class,
				"fetchCommerceDiscountByExternalReferenceCode",
				_fetchCommerceDiscountByExternalReferenceCodeParameterTypes8);

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

			return (com.liferay.commerce.discount.model.CommerceDiscount)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.discount.model.CommerceDiscount
			getCommerceDiscount(
				HttpPrincipal httpPrincipal, long commerceDiscountId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceDiscountServiceUtil.class, "getCommerceDiscount",
				_getCommerceDiscountParameterTypes9);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceDiscountId);

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

			return (com.liferay.commerce.discount.model.CommerceDiscount)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.commerce.discount.model.CommerceDiscount>
				getCommerceDiscounts(
					HttpPrincipal httpPrincipal, long companyId,
					String couponCode)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceDiscountServiceUtil.class, "getCommerceDiscounts",
				_getCommerceDiscountsParameterTypes10);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, couponCode);

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
				<com.liferay.commerce.discount.model.CommerceDiscount>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.commerce.discount.model.CommerceDiscount>
				getCommerceDiscounts(
					HttpPrincipal httpPrincipal, long companyId, String level,
					boolean active, int status)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceDiscountServiceUtil.class, "getCommerceDiscounts",
				_getCommerceDiscountsParameterTypes11);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, level, active, status);

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
				<com.liferay.commerce.discount.model.CommerceDiscount>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static int getCommerceDiscountsCount(
			HttpPrincipal httpPrincipal, long companyId, String couponCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceDiscountServiceUtil.class, "getCommerceDiscountsCount",
				_getCommerceDiscountsCountParameterTypes12);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, couponCode);

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

	public static int getCommerceDiscountsCountByPricingClassId(
			HttpPrincipal httpPrincipal, long commercePricingClassId,
			String title)
		throws com.liferay.portal.kernel.security.auth.PrincipalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceDiscountServiceUtil.class,
				"getCommerceDiscountsCountByPricingClassId",
				_getCommerceDiscountsCountByPricingClassIdParameterTypes13);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commercePricingClassId, title);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				if (exception instanceof
						com.liferay.portal.kernel.security.auth.
							PrincipalException) {

					throw (com.liferay.portal.kernel.security.auth.
						PrincipalException)exception;
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

	public static com.liferay.commerce.discount.model.CommerceDiscount
			getOrAddEmptyCommerceDiscount(
				HttpPrincipal httpPrincipal, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceDiscountServiceUtil.class,
				"getOrAddEmptyCommerceDiscount",
				_getOrAddEmptyCommerceDiscountParameterTypes14);

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

			return (com.liferay.commerce.discount.model.CommerceDiscount)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static java.util.List
		<com.liferay.commerce.discount.model.CommerceDiscount>
				searchByCommercePricingClassId(
					HttpPrincipal httpPrincipal, long commercePricingClassId,
					String title, int start, int end)
			throws com.liferay.portal.kernel.security.auth.PrincipalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceDiscountServiceUtil.class,
				"searchByCommercePricingClassId",
				_searchByCommercePricingClassIdParameterTypes15);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commercePricingClassId, title, start, end);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception exception) {
				if (exception instanceof
						com.liferay.portal.kernel.security.auth.
							PrincipalException) {

					throw (com.liferay.portal.kernel.security.auth.
						PrincipalException)exception;
				}

				throw new com.liferay.portal.kernel.exception.SystemException(
					exception);
			}

			return (java.util.List
				<com.liferay.commerce.discount.model.CommerceDiscount>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.commerce.discount.model.CommerceDiscount>
				searchCommerceDiscounts(
					HttpPrincipal httpPrincipal, long companyId,
					String keywords, int status, int start, int end,
					com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceDiscountServiceUtil.class, "searchCommerceDiscounts",
				_searchCommerceDiscountsParameterTypes16);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, companyId, keywords, status, start, end, sort);

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
				<com.liferay.commerce.discount.model.CommerceDiscount>)
					returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.discount.model.CommerceDiscount
			updateCommerceDiscount(
				HttpPrincipal httpPrincipal, long commerceDiscountId,
				String title, String target, boolean useCouponCode,
				String couponCode, boolean usePercentage,
				java.math.BigDecimal maximumDiscountAmount,
				java.math.BigDecimal level1, java.math.BigDecimal level2,
				java.math.BigDecimal level3, java.math.BigDecimal level4,
				String limitationType, int limitationTimes, boolean active,
				int displayDateMonth, int displayDateDay, int displayDateYear,
				int displayDateHour, int displayDateMinute,
				int expirationDateMonth, int expirationDateDay,
				int expirationDateYear, int expirationDateHour,
				int expirationDateMinute, boolean neverExpire,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceDiscountServiceUtil.class, "updateCommerceDiscount",
				_updateCommerceDiscountParameterTypes17);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceDiscountId, title, target, useCouponCode,
				couponCode, usePercentage, maximumDiscountAmount, level1,
				level2, level3, level4, limitationType, limitationTimes, active,
				displayDateMonth, displayDateDay, displayDateYear,
				displayDateHour, displayDateMinute, expirationDateMonth,
				expirationDateDay, expirationDateYear, expirationDateHour,
				expirationDateMinute, neverExpire, serviceContext);

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

			return (com.liferay.commerce.discount.model.CommerceDiscount)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.discount.model.CommerceDiscount
			updateCommerceDiscount(
				HttpPrincipal httpPrincipal, long commerceDiscountId,
				String title, String target, boolean useCouponCode,
				String couponCode, boolean usePercentage,
				java.math.BigDecimal maximumDiscountAmount, String level,
				java.math.BigDecimal level1, java.math.BigDecimal level2,
				java.math.BigDecimal level3, java.math.BigDecimal level4,
				String limitationType, int limitationTimes,
				boolean rulesConjunction, boolean active, int displayDateMonth,
				int displayDateDay, int displayDateYear, int displayDateHour,
				int displayDateMinute, int expirationDateMonth,
				int expirationDateDay, int expirationDateYear,
				int expirationDateHour, int expirationDateMinute,
				boolean neverExpire,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceDiscountServiceUtil.class, "updateCommerceDiscount",
				_updateCommerceDiscountParameterTypes18);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceDiscountId, title, target, useCouponCode,
				couponCode, usePercentage, maximumDiscountAmount, level, level1,
				level2, level3, level4, limitationType, limitationTimes,
				rulesConjunction, active, displayDateMonth, displayDateDay,
				displayDateYear, displayDateHour, displayDateMinute,
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

			return (com.liferay.commerce.discount.model.CommerceDiscount)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.discount.model.CommerceDiscount
			updateCommerceDiscount(
				HttpPrincipal httpPrincipal, long commerceDiscountId,
				String title, String target, boolean useCouponCode,
				String couponCode, boolean usePercentage,
				java.math.BigDecimal maximumDiscountAmount, String level,
				java.math.BigDecimal level1, java.math.BigDecimal level2,
				java.math.BigDecimal level3, java.math.BigDecimal level4,
				String limitationType, int limitationTimes,
				int limitationTimesPerAccount, boolean rulesConjunction,
				boolean active, int displayDateMonth, int displayDateDay,
				int displayDateYear, int displayDateHour, int displayDateMinute,
				int expirationDateMonth, int expirationDateDay,
				int expirationDateYear, int expirationDateHour,
				int expirationDateMinute, boolean neverExpire,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceDiscountServiceUtil.class, "updateCommerceDiscount",
				_updateCommerceDiscountParameterTypes19);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceDiscountId, title, target, useCouponCode,
				couponCode, usePercentage, maximumDiscountAmount, level, level1,
				level2, level3, level4, limitationType, limitationTimes,
				limitationTimesPerAccount, rulesConjunction, active,
				displayDateMonth, displayDateDay, displayDateYear,
				displayDateHour, displayDateMinute, expirationDateMonth,
				expirationDateDay, expirationDateYear, expirationDateHour,
				expirationDateMinute, neverExpire, serviceContext);

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

			return (com.liferay.commerce.discount.model.CommerceDiscount)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.discount.model.CommerceDiscount
			updateCommerceDiscountExternalReferenceCode(
				HttpPrincipal httpPrincipal, long commerceDiscountId,
				String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceDiscountServiceUtil.class,
				"updateCommerceDiscountExternalReferenceCode",
				_updateCommerceDiscountExternalReferenceCodeParameterTypes20);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, commerceDiscountId, externalReferenceCode);

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

			return (com.liferay.commerce.discount.model.CommerceDiscount)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	public static com.liferay.commerce.discount.model.CommerceDiscount
			updateCommerceDiscountExternalReferenceCode(
				HttpPrincipal httpPrincipal, String externalReferenceCode,
				long commerceDiscountId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				CommerceDiscountServiceUtil.class,
				"updateCommerceDiscountExternalReferenceCode",
				_updateCommerceDiscountExternalReferenceCodeParameterTypes21);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, externalReferenceCode, commerceDiscountId);

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

			return (com.liferay.commerce.discount.model.CommerceDiscount)
				returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException
					systemException) {

			_log.error(systemException, systemException);

			throw systemException;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		CommerceDiscountServiceHttp.class);

	private static final Class<?>[] _addCommerceDiscountParameterTypes0 =
		new Class[] {
			String.class, String.class, boolean.class, String.class,
			boolean.class, java.math.BigDecimal.class,
			java.math.BigDecimal.class, java.math.BigDecimal.class,
			java.math.BigDecimal.class, java.math.BigDecimal.class,
			String.class, int.class, boolean.class, int.class, int.class,
			int.class, int.class, int.class, int.class, int.class, int.class,
			int.class, int.class, boolean.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _addCommerceDiscountParameterTypes1 =
		new Class[] {
			String.class, String.class, boolean.class, String.class,
			boolean.class, java.math.BigDecimal.class, String.class,
			java.math.BigDecimal.class, java.math.BigDecimal.class,
			java.math.BigDecimal.class, java.math.BigDecimal.class,
			String.class, int.class, boolean.class, boolean.class, int.class,
			int.class, int.class, int.class, int.class, int.class, int.class,
			int.class, int.class, int.class, boolean.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _addCommerceDiscountParameterTypes2 =
		new Class[] {
			String.class, String.class, String.class, boolean.class,
			String.class, boolean.class, java.math.BigDecimal.class,
			String.class, java.math.BigDecimal.class,
			java.math.BigDecimal.class, java.math.BigDecimal.class,
			java.math.BigDecimal.class, String.class, int.class, int.class,
			boolean.class, boolean.class, int.class, int.class, int.class,
			int.class, int.class, int.class, int.class, int.class, int.class,
			int.class, boolean.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_addOrUpdateCommerceDiscountParameterTypes3 = new Class[] {
			String.class, long.class, String.class, String.class, boolean.class,
			String.class, boolean.class, java.math.BigDecimal.class,
			java.math.BigDecimal.class, java.math.BigDecimal.class,
			java.math.BigDecimal.class, java.math.BigDecimal.class,
			String.class, int.class, boolean.class, int.class, int.class,
			int.class, int.class, int.class, int.class, int.class, int.class,
			int.class, int.class, boolean.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_addOrUpdateCommerceDiscountParameterTypes4 = new Class[] {
			String.class, long.class, String.class, String.class, boolean.class,
			String.class, boolean.class, java.math.BigDecimal.class,
			String.class, java.math.BigDecimal.class,
			java.math.BigDecimal.class, java.math.BigDecimal.class,
			java.math.BigDecimal.class, String.class, int.class, boolean.class,
			boolean.class, int.class, int.class, int.class, int.class,
			int.class, int.class, int.class, int.class, int.class, int.class,
			boolean.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_addOrUpdateCommerceDiscountParameterTypes5 = new Class[] {
			String.class, long.class, String.class, String.class, boolean.class,
			String.class, boolean.class, java.math.BigDecimal.class,
			String.class, java.math.BigDecimal.class,
			java.math.BigDecimal.class, java.math.BigDecimal.class,
			java.math.BigDecimal.class, String.class, int.class, int.class,
			boolean.class, boolean.class, int.class, int.class, int.class,
			int.class, int.class, int.class, int.class, int.class, int.class,
			int.class, boolean.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _deleteCommerceDiscountParameterTypes6 =
		new Class[] {long.class};
	private static final Class<?>[] _fetchCommerceDiscountParameterTypes7 =
		new Class[] {long.class};
	private static final Class<?>[]
		_fetchCommerceDiscountByExternalReferenceCodeParameterTypes8 =
			new Class[] {String.class, long.class};
	private static final Class<?>[] _getCommerceDiscountParameterTypes9 =
		new Class[] {long.class};
	private static final Class<?>[] _getCommerceDiscountsParameterTypes10 =
		new Class[] {long.class, String.class};
	private static final Class<?>[] _getCommerceDiscountsParameterTypes11 =
		new Class[] {long.class, String.class, boolean.class, int.class};
	private static final Class<?>[] _getCommerceDiscountsCountParameterTypes12 =
		new Class[] {long.class, String.class};
	private static final Class<?>[]
		_getCommerceDiscountsCountByPricingClassIdParameterTypes13 =
			new Class[] {long.class, String.class};
	private static final Class<?>[]
		_getOrAddEmptyCommerceDiscountParameterTypes14 = new Class[] {
			String.class
		};
	private static final Class<?>[]
		_searchByCommercePricingClassIdParameterTypes15 = new Class[] {
			long.class, String.class, int.class, int.class
		};
	private static final Class<?>[] _searchCommerceDiscountsParameterTypes16 =
		new Class[] {
			long.class, String.class, int.class, int.class, int.class,
			com.liferay.portal.kernel.search.Sort.class
		};
	private static final Class<?>[] _updateCommerceDiscountParameterTypes17 =
		new Class[] {
			long.class, String.class, String.class, boolean.class, String.class,
			boolean.class, java.math.BigDecimal.class,
			java.math.BigDecimal.class, java.math.BigDecimal.class,
			java.math.BigDecimal.class, java.math.BigDecimal.class,
			String.class, int.class, boolean.class, int.class, int.class,
			int.class, int.class, int.class, int.class, int.class, int.class,
			int.class, int.class, boolean.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _updateCommerceDiscountParameterTypes18 =
		new Class[] {
			long.class, String.class, String.class, boolean.class, String.class,
			boolean.class, java.math.BigDecimal.class, String.class,
			java.math.BigDecimal.class, java.math.BigDecimal.class,
			java.math.BigDecimal.class, java.math.BigDecimal.class,
			String.class, int.class, boolean.class, boolean.class, int.class,
			int.class, int.class, int.class, int.class, int.class, int.class,
			int.class, int.class, int.class, boolean.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[] _updateCommerceDiscountParameterTypes19 =
		new Class[] {
			long.class, String.class, String.class, boolean.class, String.class,
			boolean.class, java.math.BigDecimal.class, String.class,
			java.math.BigDecimal.class, java.math.BigDecimal.class,
			java.math.BigDecimal.class, java.math.BigDecimal.class,
			String.class, int.class, int.class, boolean.class, boolean.class,
			int.class, int.class, int.class, int.class, int.class, int.class,
			int.class, int.class, int.class, int.class, boolean.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_updateCommerceDiscountExternalReferenceCodeParameterTypes20 =
			new Class[] {long.class, String.class};
	private static final Class<?>[]
		_updateCommerceDiscountExternalReferenceCodeParameterTypes21 =
			new Class[] {String.class, long.class};

}
// LIFERAY-SERVICE-BUILDER-HASH:1133424547