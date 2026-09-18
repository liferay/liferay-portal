/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service;

import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;

import java.util.List;

/**
 * Provides the remote service utility for CommerceDiscount. This utility wraps
 * <code>com.liferay.commerce.discount.service.impl.CommerceDiscountServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Marco Leo
 * @see CommerceDiscountService
 * @generated
 */
public class CommerceDiscountServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.discount.service.impl.CommerceDiscountServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static CommerceDiscount addCommerceDiscount(
			String title, String target, boolean useCouponCode,
			String couponCode, boolean usePercentage,
			java.math.BigDecimal maximumDiscountAmount,
			java.math.BigDecimal level1, java.math.BigDecimal level2,
			java.math.BigDecimal level3, java.math.BigDecimal level4,
			String limitationType, int limitationTimes, boolean active,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceDiscount(
			title, target, useCouponCode, couponCode, usePercentage,
			maximumDiscountAmount, level1, level2, level3, level4,
			limitationType, limitationTimes, active, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	public static CommerceDiscount addCommerceDiscount(
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
		throws PortalException {

		return getService().addCommerceDiscount(
			title, target, useCouponCode, couponCode, usePercentage,
			maximumDiscountAmount, level, level1, level2, level3, level4,
			limitationType, limitationTimes, rulesConjunction, active,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, serviceContext);
	}

	public static CommerceDiscount addCommerceDiscount(
			String externalReferenceCode, String title, String target,
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
		throws PortalException {

		return getService().addCommerceDiscount(
			externalReferenceCode, title, target, useCouponCode, couponCode,
			usePercentage, maximumDiscountAmount, level, level1, level2, level3,
			level4, limitationType, limitationTimes, limitationTimesPerAccount,
			rulesConjunction, active, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	public static CommerceDiscount addOrUpdateCommerceDiscount(
			String externalReferenceCode, long commerceDiscountId, String title,
			String target, boolean useCouponCode, String couponCode,
			boolean usePercentage, java.math.BigDecimal maximumDiscountAmount,
			java.math.BigDecimal level1, java.math.BigDecimal level2,
			java.math.BigDecimal level3, java.math.BigDecimal level4,
			String limitationType, int limitationTimes, boolean active,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateCommerceDiscount(
			externalReferenceCode, commerceDiscountId, title, target,
			useCouponCode, couponCode, usePercentage, maximumDiscountAmount,
			level1, level2, level3, level4, limitationType, limitationTimes,
			active, displayDateMonth, displayDateDay, displayDateYear,
			displayDateHour, displayDateMinute, expirationDateMonth,
			expirationDateDay, expirationDateYear, expirationDateHour,
			expirationDateMinute, neverExpire, serviceContext);
	}

	public static CommerceDiscount addOrUpdateCommerceDiscount(
			String externalReferenceCode, long commerceDiscountId, String title,
			String target, boolean useCouponCode, String couponCode,
			boolean usePercentage, java.math.BigDecimal maximumDiscountAmount,
			String level, java.math.BigDecimal level1,
			java.math.BigDecimal level2, java.math.BigDecimal level3,
			java.math.BigDecimal level4, String limitationType,
			int limitationTimes, boolean rulesConjunction, boolean active,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateCommerceDiscount(
			externalReferenceCode, commerceDiscountId, title, target,
			useCouponCode, couponCode, usePercentage, maximumDiscountAmount,
			level, level1, level2, level3, level4, limitationType,
			limitationTimes, rulesConjunction, active, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	public static CommerceDiscount addOrUpdateCommerceDiscount(
			String externalReferenceCode, long commerceDiscountId, String title,
			String target, boolean useCouponCode, String couponCode,
			boolean usePercentage, java.math.BigDecimal maximumDiscountAmount,
			String level, java.math.BigDecimal level1,
			java.math.BigDecimal level2, java.math.BigDecimal level3,
			java.math.BigDecimal level4, String limitationType,
			int limitationTimes, int limitationTimesPerAccount,
			boolean rulesConjunction, boolean active, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateCommerceDiscount(
			externalReferenceCode, commerceDiscountId, title, target,
			useCouponCode, couponCode, usePercentage, maximumDiscountAmount,
			level, level1, level2, level3, level4, limitationType,
			limitationTimes, limitationTimesPerAccount, rulesConjunction,
			active, displayDateMonth, displayDateDay, displayDateYear,
			displayDateHour, displayDateMinute, expirationDateMonth,
			expirationDateDay, expirationDateYear, expirationDateHour,
			expirationDateMinute, neverExpire, serviceContext);
	}

	public static void deleteCommerceDiscount(long commerceDiscountId)
		throws PortalException {

		getService().deleteCommerceDiscount(commerceDiscountId);
	}

	public static CommerceDiscount fetchCommerceDiscount(
			long commerceDiscountId)
		throws PortalException {

		return getService().fetchCommerceDiscount(commerceDiscountId);
	}

	public static CommerceDiscount fetchCommerceDiscountByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().fetchCommerceDiscountByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static CommerceDiscount getCommerceDiscount(long commerceDiscountId)
		throws PortalException {

		return getService().getCommerceDiscount(commerceDiscountId);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public static List<CommerceDiscount> getCommerceDiscounts(
			long companyId, String couponCode)
		throws PortalException {

		return getService().getCommerceDiscounts(companyId, couponCode);
	}

	public static List<CommerceDiscount> getCommerceDiscounts(
			long companyId, String level, boolean active, int status)
		throws PortalException {

		return getService().getCommerceDiscounts(
			companyId, level, active, status);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public static int getCommerceDiscountsCount(
			long companyId, String couponCode)
		throws PortalException {

		return getService().getCommerceDiscountsCount(companyId, couponCode);
	}

	public static int getCommerceDiscountsCountByPricingClassId(
			long commercePricingClassId, String title)
		throws com.liferay.portal.kernel.security.auth.PrincipalException {

		return getService().getCommerceDiscountsCountByPricingClassId(
			commercePricingClassId, title);
	}

	public static CommerceDiscount getOrAddEmptyCommerceDiscount(
			String externalReferenceCode)
		throws PortalException {

		return getService().getOrAddEmptyCommerceDiscount(
			externalReferenceCode);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static List<CommerceDiscount> searchByCommercePricingClassId(
			long commercePricingClassId, String title, int start, int end)
		throws com.liferay.portal.kernel.security.auth.PrincipalException {

		return getService().searchByCommercePricingClassId(
			commercePricingClassId, title, start, end);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CommerceDiscount> searchCommerceDiscounts(
				long companyId, String keywords, int status, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchCommerceDiscounts(
			companyId, keywords, status, start, end, sort);
	}

	public static CommerceDiscount updateCommerceDiscount(
			long commerceDiscountId, String title, String target,
			boolean useCouponCode, String couponCode, boolean usePercentage,
			java.math.BigDecimal maximumDiscountAmount,
			java.math.BigDecimal level1, java.math.BigDecimal level2,
			java.math.BigDecimal level3, java.math.BigDecimal level4,
			String limitationType, int limitationTimes, boolean active,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCommerceDiscount(
			commerceDiscountId, title, target, useCouponCode, couponCode,
			usePercentage, maximumDiscountAmount, level1, level2, level3,
			level4, limitationType, limitationTimes, active, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	public static CommerceDiscount updateCommerceDiscount(
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
		throws PortalException {

		return getService().updateCommerceDiscount(
			commerceDiscountId, title, target, useCouponCode, couponCode,
			usePercentage, maximumDiscountAmount, level, level1, level2, level3,
			level4, limitationType, limitationTimes, rulesConjunction, active,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, serviceContext);
	}

	public static CommerceDiscount updateCommerceDiscount(
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
		throws PortalException {

		return getService().updateCommerceDiscount(
			commerceDiscountId, title, target, useCouponCode, couponCode,
			usePercentage, maximumDiscountAmount, level, level1, level2, level3,
			level4, limitationType, limitationTimes, limitationTimesPerAccount,
			rulesConjunction, active, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #updateCommerceDiscountExternalReferenceCode(String, long)}
	 */
	@Deprecated
	public static CommerceDiscount updateCommerceDiscountExternalReferenceCode(
			long commerceDiscountId, String externalReferenceCode)
		throws PortalException {

		return getService().updateCommerceDiscountExternalReferenceCode(
			commerceDiscountId, externalReferenceCode);
	}

	public static CommerceDiscount updateCommerceDiscountExternalReferenceCode(
			String externalReferenceCode, long commerceDiscountId)
		throws PortalException {

		return getService().updateCommerceDiscountExternalReferenceCode(
			externalReferenceCode, commerceDiscountId);
	}

	public static CommerceDiscountService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceDiscountService> _serviceSnapshot =
		new Snapshot<>(
			CommerceDiscountServiceUtil.class, CommerceDiscountService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:632151865