/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service.impl;

import com.liferay.commerce.discount.constants.CommerceDiscountActionKeys;
import com.liferay.commerce.discount.exception.NoSuchDiscountException;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.service.base.CommerceDiscountServiceBaseImpl;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.Validator;

import java.math.BigDecimal;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceDiscount"
	},
	service = AopService.class
)
public class CommerceDiscountServiceImpl
	extends CommerceDiscountServiceBaseImpl {

	@Override
	public CommerceDiscount addCommerceDiscount(
			String title, String target, boolean useCouponCode,
			String couponCode, boolean usePercentage,
			BigDecimal maximumDiscountAmount, BigDecimal level1,
			BigDecimal level2, BigDecimal level3, BigDecimal level4,
			String limitationType, int limitationTimes, boolean active,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, ServiceContext serviceContext)
		throws PortalException {

		PortletResourcePermission portletResourcePermission =
			_commerceDiscountResourcePermission.getPortletResourcePermission();

		portletResourcePermission.check(
			getPermissionChecker(), null,
			CommerceDiscountActionKeys.ADD_COMMERCE_DISCOUNT);

		return commerceDiscountLocalService.addCommerceDiscount(
			getUserId(), title, target, useCouponCode, couponCode,
			usePercentage, maximumDiscountAmount, level1, level2, level3,
			level4, limitationType, limitationTimes, active, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	@Override
	public CommerceDiscount addCommerceDiscount(
			String title, String target, boolean useCouponCode,
			String couponCode, boolean usePercentage,
			BigDecimal maximumDiscountAmount, String level, BigDecimal level1,
			BigDecimal level2, BigDecimal level3, BigDecimal level4,
			String limitationType, int limitationTimes,
			boolean rulesConjunction, boolean active, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, ServiceContext serviceContext)
		throws PortalException {

		PortletResourcePermission portletResourcePermission =
			_commerceDiscountResourcePermission.getPortletResourcePermission();

		portletResourcePermission.check(
			getPermissionChecker(), null,
			CommerceDiscountActionKeys.ADD_COMMERCE_DISCOUNT);

		return commerceDiscountLocalService.addCommerceDiscount(
			getUserId(), title, target, useCouponCode, couponCode,
			usePercentage, maximumDiscountAmount, level, level1, level2, level3,
			level4, limitationType, limitationTimes, rulesConjunction, active,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, serviceContext);
	}

	@Override
	public CommerceDiscount addCommerceDiscount(
			String externalReferenceCode, String title, String target,
			boolean useCouponCode, String couponCode, boolean usePercentage,
			BigDecimal maximumDiscountAmount, String level, BigDecimal level1,
			BigDecimal level2, BigDecimal level3, BigDecimal level4,
			String limitationType, int limitationTimes,
			int limitationTimesPerAccount, boolean rulesConjunction,
			boolean active, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire,
			ServiceContext serviceContext)
		throws PortalException {

		PortletResourcePermission portletResourcePermission =
			_commerceDiscountResourcePermission.getPortletResourcePermission();

		portletResourcePermission.check(
			getPermissionChecker(), null,
			CommerceDiscountActionKeys.ADD_COMMERCE_DISCOUNT);

		return commerceDiscountLocalService.addCommerceDiscount(
			externalReferenceCode, getUserId(), title, target, useCouponCode,
			couponCode, usePercentage, maximumDiscountAmount, level, level1,
			level2, level3, level4, limitationType, limitationTimes,
			limitationTimesPerAccount, rulesConjunction, active,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, serviceContext);
	}

	@Override
	public CommerceDiscount addOrUpdateCommerceDiscount(
			String externalReferenceCode, long commerceDiscountId, String title,
			String target, boolean useCouponCode, String couponCode,
			boolean usePercentage, BigDecimal maximumDiscountAmount,
			BigDecimal level1, BigDecimal level2, BigDecimal level3,
			BigDecimal level4, String limitationType, int limitationTimes,
			boolean active, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire,
			ServiceContext serviceContext)
		throws PortalException {

		return addOrUpdateCommerceDiscount(
			externalReferenceCode, commerceDiscountId, title, target,
			useCouponCode, couponCode, usePercentage, maximumDiscountAmount,
			StringPool.BLANK, level1, level2, level3, level4, limitationType,
			limitationTimes, true, active, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	@Override
	public CommerceDiscount addOrUpdateCommerceDiscount(
			String externalReferenceCode, long commerceDiscountId, String title,
			String target, boolean useCouponCode, String couponCode,
			boolean usePercentage, BigDecimal maximumDiscountAmount,
			String level, BigDecimal level1, BigDecimal level2,
			BigDecimal level3, BigDecimal level4, String limitationType,
			int limitationTimes, boolean rulesConjunction, boolean active,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, ServiceContext serviceContext)
		throws PortalException {

		return addOrUpdateCommerceDiscount(
			externalReferenceCode, commerceDiscountId, title, target,
			useCouponCode, couponCode, usePercentage, maximumDiscountAmount,
			level, level1, level2, level3, level4, limitationType,
			limitationTimes, 0, rulesConjunction, active, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	@Override
	public CommerceDiscount addOrUpdateCommerceDiscount(
			String externalReferenceCode, long commerceDiscountId, String title,
			String target, boolean useCouponCode, String couponCode,
			boolean usePercentage, BigDecimal maximumDiscountAmount,
			String level, BigDecimal level1, BigDecimal level2,
			BigDecimal level3, BigDecimal level4, String limitationType,
			int limitationTimes, int limitationTimesPerAccount,
			boolean rulesConjunction, boolean active, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, ServiceContext serviceContext)
		throws PortalException {

		// Update

		if (commerceDiscountId > 0) {
			try {
				return updateCommerceDiscount(
					commerceDiscountId, title, target, useCouponCode,
					couponCode, usePercentage, maximumDiscountAmount, level,
					level1, level2, level3, level4, limitationType,
					limitationTimes, limitationTimesPerAccount,
					rulesConjunction, active, displayDateMonth, displayDateDay,
					displayDateYear, displayDateHour, displayDateMinute,
					expirationDateMonth, expirationDateDay, expirationDateYear,
					expirationDateHour, expirationDateMinute, neverExpire,
					serviceContext);
			}
			catch (NoSuchDiscountException noSuchDiscountException) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to find discount with ID: " +
							commerceDiscountId,
						noSuchDiscountException);
				}
			}
		}

		if (!Validator.isBlank(externalReferenceCode)) {
			CommerceDiscount commerceDiscount =
				commerceDiscountPersistence.fetchByERC_C(
					externalReferenceCode, serviceContext.getCompanyId());

			if (commerceDiscount != null) {
				return updateCommerceDiscount(
					commerceDiscountId, title, target, useCouponCode,
					couponCode, usePercentage, maximumDiscountAmount, level,
					level1, level2, level3, level4, limitationType,
					limitationTimes, limitationTimesPerAccount,
					rulesConjunction, active, displayDateMonth, displayDateDay,
					displayDateYear, displayDateHour, displayDateMinute,
					expirationDateMonth, expirationDateDay, expirationDateYear,
					expirationDateHour, expirationDateMinute, neverExpire,
					serviceContext);
			}
		}

		// Add

		return addCommerceDiscount(
			externalReferenceCode, title, target, useCouponCode, couponCode,
			usePercentage, maximumDiscountAmount, level, level1, level2, level3,
			level4, limitationType, limitationTimes, limitationTimesPerAccount,
			rulesConjunction, active, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	@Override
	public void deleteCommerceDiscount(long commerceDiscountId)
		throws PortalException {

		_commerceDiscountResourcePermission.check(
			getPermissionChecker(), commerceDiscountId, ActionKeys.DELETE);

		commerceDiscountLocalService.deleteCommerceDiscount(commerceDiscountId);
	}

	@Override
	public CommerceDiscount fetchCommerceDiscount(long commerceDiscountId)
		throws PortalException {

		CommerceDiscount commerceDiscount =
			commerceDiscountPersistence.fetchByPrimaryKey(commerceDiscountId);

		if (commerceDiscount != null) {
			_commerceDiscountResourcePermission.check(
				getPermissionChecker(), commerceDiscount, ActionKeys.VIEW);
		}

		return commerceDiscount;
	}

	@Override
	public CommerceDiscount fetchCommerceDiscountByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		CommerceDiscount commerceDiscount =
			commerceDiscountLocalService.
				fetchCommerceDiscountByExternalReferenceCode(
					externalReferenceCode, companyId);

		if (commerceDiscount != null) {
			_commerceDiscountResourcePermission.check(
				getPermissionChecker(), commerceDiscount, ActionKeys.VIEW);
		}

		return commerceDiscount;
	}

	@Override
	public CommerceDiscount getCommerceDiscount(long commerceDiscountId)
		throws PortalException {

		_commerceDiscountResourcePermission.check(
			getPermissionChecker(), commerceDiscountId, ActionKeys.VIEW);

		return commerceDiscountPersistence.findByPrimaryKey(commerceDiscountId);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	@Override
	public List<CommerceDiscount> getCommerceDiscounts(
			long companyId, String couponCode)
		throws PortalException {

		return commerceDiscountPersistence.filterFindByC_C(
			companyId, couponCode);
	}

	@Override
	public List<CommerceDiscount> getCommerceDiscounts(
			long companyId, String level, boolean active, int status)
		throws PortalException {

		PortletResourcePermission portletResourcePermission =
			_commerceDiscountResourcePermission.getPortletResourcePermission();

		portletResourcePermission.check(
			getPermissionChecker(), null,
			CommerceDiscountActionKeys.VIEW_COMMERCE_DISCOUNTS);

		return commerceDiscountPersistence.findByC_L_A_S(
			companyId, level, active, status);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	@Override
	public int getCommerceDiscountsCount(long companyId, String couponCode)
		throws PortalException {

		return commerceDiscountPersistence.filterCountByC_C(
			companyId, couponCode);
	}

	@Override
	public int getCommerceDiscountsCountByPricingClassId(
			long commercePricingClassId, String title)
		throws PrincipalException {

		return commerceDiscountFinder.countByCommercePricingClassId(
			commercePricingClassId, title, true);
	}

	@Override
	public List<CommerceDiscount> searchByCommercePricingClassId(
			long commercePricingClassId, String title, int start, int end)
		throws PrincipalException {

		return commerceDiscountFinder.findByCommercePricingClassId(
			commercePricingClassId, title, start, end, true);
	}

	@Override
	public BaseModelSearchResult<CommerceDiscount> searchCommerceDiscounts(
			long companyId, String keywords, int status, int start, int end,
			Sort sort)
		throws PortalException {

		return commerceDiscountLocalService.searchCommerceDiscounts(
			companyId,
			TransformUtil.transformToLongArray(
				_commerceChannelService.search(companyId),
				CommerceChannel::getGroupId),
			keywords, status, start, end, sort);
	}

	@Override
	public CommerceDiscount updateCommerceDiscount(
			long commerceDiscountId, String title, String target,
			boolean useCouponCode, String couponCode, boolean usePercentage,
			BigDecimal maximumDiscountAmount, BigDecimal level1,
			BigDecimal level2, BigDecimal level3, BigDecimal level4,
			String limitationType, int limitationTimes, boolean active,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, ServiceContext serviceContext)
		throws PortalException {

		_commerceDiscountResourcePermission.check(
			getPermissionChecker(), commerceDiscountId, ActionKeys.UPDATE);

		return commerceDiscountLocalService.updateCommerceDiscount(
			commerceDiscountId, title, target, useCouponCode, couponCode,
			usePercentage, maximumDiscountAmount, level1, level2, level3,
			level4, limitationType, limitationTimes, active, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	@Override
	public CommerceDiscount updateCommerceDiscount(
			long commerceDiscountId, String title, String target,
			boolean useCouponCode, String couponCode, boolean usePercentage,
			BigDecimal maximumDiscountAmount, String level, BigDecimal level1,
			BigDecimal level2, BigDecimal level3, BigDecimal level4,
			String limitationType, int limitationTimes,
			boolean rulesConjunction, boolean active, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, ServiceContext serviceContext)
		throws PortalException {

		_commerceDiscountResourcePermission.check(
			getPermissionChecker(), commerceDiscountId, ActionKeys.UPDATE);

		return commerceDiscountLocalService.updateCommerceDiscount(
			commerceDiscountId, title, target, useCouponCode, couponCode,
			usePercentage, maximumDiscountAmount, level, level1, level2, level3,
			level4, limitationType, limitationTimes, rulesConjunction, active,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, serviceContext);
	}

	@Override
	public CommerceDiscount updateCommerceDiscount(
			long commerceDiscountId, String title, String target,
			boolean useCouponCode, String couponCode, boolean usePercentage,
			BigDecimal maximumDiscountAmount, String level, BigDecimal level1,
			BigDecimal level2, BigDecimal level3, BigDecimal level4,
			String limitationType, int limitationTimes,
			int limitationTimesPerAccount, boolean rulesConjunction,
			boolean active, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire,
			ServiceContext serviceContext)
		throws PortalException {

		_commerceDiscountResourcePermission.check(
			getPermissionChecker(), commerceDiscountId, ActionKeys.UPDATE);

		return commerceDiscountLocalService.updateCommerceDiscount(
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
	 *             #updateCommerceDiscountExternalReferenceCode(String, long)}
	 */
	@Deprecated
	@Override
	public CommerceDiscount updateCommerceDiscountExternalReferenceCode(
			long commerceDiscountId, String externalReferenceCode)
		throws PortalException {

		return updateCommerceDiscountExternalReferenceCode(
			externalReferenceCode, commerceDiscountId);
	}

	@Override
	public CommerceDiscount updateCommerceDiscountExternalReferenceCode(
			String externalReferenceCode, long commerceDiscountId)
		throws PortalException {

		_commerceDiscountResourcePermission.check(
			getPermissionChecker(), commerceDiscountId, ActionKeys.UPDATE);

		return commerceDiscountLocalService.
			updateCommerceDiscountExternalReferenceCode(
				externalReferenceCode, commerceDiscountId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceDiscountServiceImpl.class);

	@Reference
	private CommerceChannelService _commerceChannelService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.discount.model.CommerceDiscount)"
	)
	private ModelResourcePermission<CommerceDiscount>
		_commerceDiscountResourcePermission;

}