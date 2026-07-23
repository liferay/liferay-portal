/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service;

import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for CommerceDiscount. This utility wraps
 * <code>com.liferay.commerce.discount.service.impl.CommerceDiscountLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see CommerceDiscountLocalService
 * @generated
 */
public class CommerceDiscountLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.discount.service.impl.CommerceDiscountLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce discount to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscount the commerce discount
	 * @return the commerce discount that was added
	 */
	public static CommerceDiscount addCommerceDiscount(
		CommerceDiscount commerceDiscount) {

		return getService().addCommerceDiscount(commerceDiscount);
	}

	public static CommerceDiscount addCommerceDiscount(
			long userId, String title, String target, boolean useCouponCode,
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
			userId, title, target, useCouponCode, couponCode, usePercentage,
			maximumDiscountAmount, level1, level2, level3, level4,
			limitationType, limitationTimes, active, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	public static CommerceDiscount addCommerceDiscount(
			long userId, String title, String target, boolean useCouponCode,
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
			userId, title, target, useCouponCode, couponCode, usePercentage,
			maximumDiscountAmount, level, level1, level2, level3, level4,
			limitationType, limitationTimes, rulesConjunction, active,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, serviceContext);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #addCommerceDiscount(String, long, String, String, boolean,
	 String, boolean, BigDecimal, String, BigDecimal, BigDecimal,
	 BigDecimal, BigDecimal, String, int, boolean, boolean, int,
	 int, int, int, int, int, int, int, int, int, boolean,
	 ServiceContext)}
	 */
	@Deprecated
	public static CommerceDiscount addCommerceDiscount(
			long userId, String title, String target, boolean useCouponCode,
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
			String externalReferenceCode, boolean neverExpire,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceDiscount(
			userId, title, target, useCouponCode, couponCode, usePercentage,
			maximumDiscountAmount, level, level1, level2, level3, level4,
			limitationType, limitationTimes, rulesConjunction, active,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			externalReferenceCode, neverExpire, serviceContext);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #addCommerceDiscount(String, long, String, String, boolean, String,
	 boolean, BigDecimal, String, BigDecimal, BigDecimal,
	 BigDecimal, BigDecimal, BigDecimal, String, int, int,
	 boolean, boolean, int, int, int, int, int, int, int, int,
	 int, int, boolean, ServiceContext)}
	 */
	@Deprecated
	public static CommerceDiscount addCommerceDiscount(
			long userId, String title, String target, boolean useCouponCode,
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
			int expirationDateMinute, String externalReferenceCode,
			boolean neverExpire,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceDiscount(
			userId, title, target, useCouponCode, couponCode, usePercentage,
			maximumDiscountAmount, level, level1, level2, level3, level4,
			limitationType, limitationTimes, limitationTimesPerAccount,
			rulesConjunction, active, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, externalReferenceCode,
			neverExpire, serviceContext);
	}

	public static CommerceDiscount addCommerceDiscount(
			String externalReferenceCode, long userId, String title,
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

		return getService().addCommerceDiscount(
			externalReferenceCode, userId, title, target, useCouponCode,
			couponCode, usePercentage, maximumDiscountAmount, level, level1,
			level2, level3, level4, limitationType, limitationTimes,
			rulesConjunction, active, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	public static CommerceDiscount addCommerceDiscount(
			String externalReferenceCode, long userId, String title,
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

		return getService().addCommerceDiscount(
			externalReferenceCode, userId, title, target, useCouponCode,
			couponCode, usePercentage, maximumDiscountAmount, level, level1,
			level2, level3, level4, limitationType, limitationTimes,
			limitationTimesPerAccount, rulesConjunction, active,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, serviceContext);
	}

	public static CommerceDiscount addOrUpdateCommerceDiscount(
			String externalReferenceCode, long userId, long commerceDiscountId,
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

		return getService().addOrUpdateCommerceDiscount(
			externalReferenceCode, userId, commerceDiscountId, title, target,
			useCouponCode, couponCode, usePercentage, maximumDiscountAmount,
			level1, level2, level3, level4, limitationType, limitationTimes,
			active, displayDateMonth, displayDateDay, displayDateYear,
			displayDateHour, displayDateMinute, expirationDateMonth,
			expirationDateDay, expirationDateYear, expirationDateHour,
			expirationDateMinute, neverExpire, serviceContext);
	}

	public static CommerceDiscount addOrUpdateCommerceDiscount(
			String externalReferenceCode, long userId, long commerceDiscountId,
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

		return getService().addOrUpdateCommerceDiscount(
			externalReferenceCode, userId, commerceDiscountId, title, target,
			useCouponCode, couponCode, usePercentage, maximumDiscountAmount,
			level, level1, level2, level3, level4, limitationType,
			limitationTimes, rulesConjunction, active, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	public static CommerceDiscount addOrUpdateCommerceDiscount(
			String externalReferenceCode, long userId, long commerceDiscountId,
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
		throws PortalException {

		return getService().addOrUpdateCommerceDiscount(
			externalReferenceCode, userId, commerceDiscountId, title, target,
			useCouponCode, couponCode, usePercentage, maximumDiscountAmount,
			level, level1, level2, level3, level4, limitationType,
			limitationTimes, limitationTimesPerAccount, rulesConjunction,
			active, displayDateMonth, displayDateDay, displayDateYear,
			displayDateHour, displayDateMinute, expirationDateMonth,
			expirationDateDay, expirationDateYear, expirationDateHour,
			expirationDateMinute, neverExpire, serviceContext);
	}

	public static void checkCommerceDiscounts() throws PortalException {
		getService().checkCommerceDiscounts();
	}

	/**
	 * Creates a new commerce discount with the primary key. Does not add the commerce discount to the database.
	 *
	 * @param commerceDiscountId the primary key for the new commerce discount
	 * @return the new commerce discount
	 */
	public static CommerceDiscount createCommerceDiscount(
		long commerceDiscountId) {

		return getService().createCommerceDiscount(commerceDiscountId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the commerce discount from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscount the commerce discount
	 * @return the commerce discount that was removed
	 * @throws PortalException
	 */
	public static CommerceDiscount deleteCommerceDiscount(
			CommerceDiscount commerceDiscount)
		throws PortalException {

		return getService().deleteCommerceDiscount(commerceDiscount);
	}

	/**
	 * Deletes the commerce discount with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountId the primary key of the commerce discount
	 * @return the commerce discount that was removed
	 * @throws PortalException if a commerce discount with the primary key could not be found
	 */
	public static CommerceDiscount deleteCommerceDiscount(
			long commerceDiscountId)
		throws PortalException {

		return getService().deleteCommerceDiscount(commerceDiscountId);
	}

	public static void deleteCommerceDiscounts(long companyId)
		throws PortalException {

		getService().deleteCommerceDiscounts(companyId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static <T> T dslQuery(DSLQuery dslQuery) {
		return getService().dslQuery(dslQuery);
	}

	public static int dslQueryCount(DSLQuery dslQuery) {
		return getService().dslQueryCount(dslQuery);
	}

	public static DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static CommerceDiscount fetchCommerceDiscount(
		long commerceDiscountId) {

		return getService().fetchCommerceDiscount(commerceDiscountId);
	}

	public static CommerceDiscount fetchCommerceDiscountByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return getService().fetchCommerceDiscountByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce discount with the matching UUID and company.
	 *
	 * @param uuid the commerce discount's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce discount, or <code>null</code> if a matching commerce discount could not be found
	 */
	public static CommerceDiscount fetchCommerceDiscountByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().fetchCommerceDiscountByUuidAndCompanyId(
			uuid, companyId);
	}

	public static CommerceDiscount fetchDefaultCommerceDiscount(
		long commerceChannelAccountEntryRelId, long cpDefinitionId,
		long cpInstanceId, String unitOfMeasureKey) {

		return getService().fetchDefaultCommerceDiscount(
			commerceChannelAccountEntryRelId, cpDefinitionId, cpInstanceId,
			unitOfMeasureKey);
	}

	public static List<CommerceDiscount>
		getAccountAndChannelAndOrderTypeCommerceDiscounts(
			long commerceAccountId, long commerceChannelId,
			long commerceOrderTypeId, long cpDefinitionId, long cpInstanceId,
			String unitOfMeasureKey) {

		return getService().getAccountAndChannelAndOrderTypeCommerceDiscounts(
			commerceAccountId, commerceChannelId, commerceOrderTypeId,
			cpDefinitionId, cpInstanceId, unitOfMeasureKey);
	}

	public static List<CommerceDiscount>
		getAccountAndChannelAndOrderTypeCommerceDiscounts(
			long commerceAccountId, long commerceChannelId,
			long commerceOrderTypeId, String target) {

		return getService().getAccountAndChannelAndOrderTypeCommerceDiscounts(
			commerceAccountId, commerceChannelId, commerceOrderTypeId, target);
	}

	public static List<CommerceDiscount> getAccountAndChannelCommerceDiscounts(
		long commerceAccountId, long commerceChannelId, long cpDefinitionId,
		long cpInstanceId, String unitOfMeasureKey) {

		return getService().getAccountAndChannelCommerceDiscounts(
			commerceAccountId, commerceChannelId, cpDefinitionId, cpInstanceId,
			unitOfMeasureKey);
	}

	public static List<CommerceDiscount> getAccountAndChannelCommerceDiscounts(
		long commerceAccountId, long commerceChannelId, String target) {

		return getService().getAccountAndChannelCommerceDiscounts(
			commerceAccountId, commerceChannelId, target);
	}

	public static List<CommerceDiscount>
		getAccountAndOrderTypeCommerceDiscounts(
			long commerceAccountId, long commerceOrderTypeId, String target) {

		return getService().getAccountAndOrderTypeCommerceDiscounts(
			commerceAccountId, commerceOrderTypeId, target);
	}

	public static List<CommerceDiscount>
		getAccountCommerceAndOrderTypeDiscounts(
			long commerceAccountId, long commerceOrderTypeId,
			long cpDefinitionId, long cpInstanceId, String unitOfMeasureKey) {

		return getService().getAccountCommerceAndOrderTypeDiscounts(
			commerceAccountId, commerceOrderTypeId, cpDefinitionId,
			cpInstanceId, unitOfMeasureKey);
	}

	public static List<CommerceDiscount> getAccountCommerceDiscounts(
		long commerceAccountId, long cpDefinitionId, long cpInstanceId,
		String unitOfMeasureKey) {

		return getService().getAccountCommerceDiscounts(
			commerceAccountId, cpDefinitionId, cpInstanceId, unitOfMeasureKey);
	}

	public static List<CommerceDiscount> getAccountCommerceDiscounts(
		long commerceAccountId, String target) {

		return getService().getAccountCommerceDiscounts(
			commerceAccountId, target);
	}

	public static List<CommerceDiscount>
		getAccountGroupAndChannelAndOrderTypeCommerceDiscount(
			long[] commerceAccountGroupIds, long commerceChannelId,
			long commerceOrderTypeId, long cpDefinitionId, long cpInstanceId,
			String unitOfMeasureKey) {

		return getService().
			getAccountGroupAndChannelAndOrderTypeCommerceDiscount(
				commerceAccountGroupIds, commerceChannelId, commerceOrderTypeId,
				cpDefinitionId, cpInstanceId, unitOfMeasureKey);
	}

	public static List<CommerceDiscount>
		getAccountGroupAndChannelAndOrderTypeCommerceDiscount(
			long[] commerceAccountGroupIds, long commerceChannelId,
			long commerceOrderTypeId, String target) {

		return getService().
			getAccountGroupAndChannelAndOrderTypeCommerceDiscount(
				commerceAccountGroupIds, commerceChannelId, commerceOrderTypeId,
				target);
	}

	public static List<CommerceDiscount>
		getAccountGroupAndChannelCommerceDiscount(
			long[] commerceAccountGroupIds, long commerceChannelId,
			long cpDefinitionId, long cpInstanceId, String unitOfMeasureKey) {

		return getService().getAccountGroupAndChannelCommerceDiscount(
			commerceAccountGroupIds, commerceChannelId, cpDefinitionId,
			cpInstanceId, unitOfMeasureKey);
	}

	public static List<CommerceDiscount>
		getAccountGroupAndChannelCommerceDiscount(
			long[] commerceAccountGroupIds, long commerceChannelId,
			String target) {

		return getService().getAccountGroupAndChannelCommerceDiscount(
			commerceAccountGroupIds, commerceChannelId, target);
	}

	public static List<CommerceDiscount>
		getAccountGroupAndOrderTypeCommerceDiscount(
			long[] commerceAccountGroupIds, long commerceOrderTypeId,
			long cpDefinitionId, long cpInstanceId, String unitOfMeasureKey) {

		return getService().getAccountGroupAndOrderTypeCommerceDiscount(
			commerceAccountGroupIds, commerceOrderTypeId, cpDefinitionId,
			cpInstanceId, unitOfMeasureKey);
	}

	public static List<CommerceDiscount>
		getAccountGroupAndOrderTypeCommerceDiscount(
			long[] commerceAccountGroupIds, long commerceOrderTypeId,
			String target) {

		return getService().getAccountGroupAndOrderTypeCommerceDiscount(
			commerceAccountGroupIds, commerceOrderTypeId, target);
	}

	public static List<CommerceDiscount> getAccountGroupCommerceDiscount(
		long[] commerceAccountGroupIds, long cpDefinitionId, long cpInstanceId,
		String unitOfMeasureKey) {

		return getService().getAccountGroupCommerceDiscount(
			commerceAccountGroupIds, cpDefinitionId, cpInstanceId,
			unitOfMeasureKey);
	}

	public static List<CommerceDiscount> getAccountGroupCommerceDiscount(
		long[] commerceAccountGroupIds, String target) {

		return getService().getAccountGroupCommerceDiscount(
			commerceAccountGroupIds, target);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static CommerceDiscount getActiveCommerceDiscount(
			long companyId, String couponCode, boolean active)
		throws PortalException {

		return getService().getActiveCommerceDiscount(
			companyId, couponCode, active);
	}

	public static int getActiveCommerceDiscountsCount(
		long companyId, String couponCode, boolean active) {

		return getService().getActiveCommerceDiscountsCount(
			companyId, couponCode, active);
	}

	public static List<CommerceDiscount>
		getChannelAndOrderTypeCommerceDiscounts(
			long commerceChannelId, long commerceOrderTypeId,
			long cpDefinitionId, long cpInstanceId, String unitOfMeasureKey) {

		return getService().getChannelAndOrderTypeCommerceDiscounts(
			commerceChannelId, commerceOrderTypeId, cpDefinitionId,
			cpInstanceId, unitOfMeasureKey);
	}

	public static List<CommerceDiscount>
		getChannelAndOrderTypeCommerceDiscounts(
			long commerceChannelId, long commerceOrderTypeId, String target) {

		return getService().getChannelAndOrderTypeCommerceDiscounts(
			commerceChannelId, commerceOrderTypeId, target);
	}

	public static List<CommerceDiscount> getChannelCommerceDiscounts(
		long commerceChannelId, long cpDefinitionId, long cpInstanceId,
		String unitOfMeasureKey) {

		return getService().getChannelCommerceDiscounts(
			commerceChannelId, cpDefinitionId, cpInstanceId, unitOfMeasureKey);
	}

	public static List<CommerceDiscount> getChannelCommerceDiscounts(
		long commerceChannelId, String target) {

		return getService().getChannelCommerceDiscounts(
			commerceChannelId, target);
	}

	/**
	 * Returns the commerce discount with the primary key.
	 *
	 * @param commerceDiscountId the primary key of the commerce discount
	 * @return the commerce discount
	 * @throws PortalException if a commerce discount with the primary key could not be found
	 */
	public static CommerceDiscount getCommerceDiscount(long commerceDiscountId)
		throws PortalException {

		return getService().getCommerceDiscount(commerceDiscountId);
	}

	public static CommerceDiscount getCommerceDiscountByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getCommerceDiscountByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce discount with the matching UUID and company.
	 *
	 * @param uuid the commerce discount's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce discount
	 * @throws PortalException if a matching commerce discount could not be found
	 */
	public static CommerceDiscount getCommerceDiscountByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException {

		return getService().getCommerceDiscountByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of all the commerce discounts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce discounts
	 * @param end the upper bound of the range of commerce discounts (not inclusive)
	 * @return the range of commerce discounts
	 */
	public static List<CommerceDiscount> getCommerceDiscounts(
		int start, int end) {

		return getService().getCommerceDiscounts(start, end);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public static List<CommerceDiscount> getCommerceDiscounts(
		long companyId, String couponCode) {

		return getService().getCommerceDiscounts(companyId, couponCode);
	}

	public static List<CommerceDiscount> getCommerceDiscounts(
		long companyId, String level, boolean active, int status) {

		return getService().getCommerceDiscounts(
			companyId, level, active, status);
	}

	/**
	 * Returns the number of commerce discounts.
	 *
	 * @return the number of commerce discounts
	 */
	public static int getCommerceDiscountsCount() {
		return getService().getCommerceDiscountsCount();
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public static int getCommerceDiscountsCount(
		long companyId, String couponCode) {

		return getService().getCommerceDiscountsCount(companyId, couponCode);
	}

	public static int getCommerceDiscountsCountByPricingClassId(
		long commercePricingClassId, String title) {

		return getService().getCommerceDiscountsCountByPricingClassId(
			commercePricingClassId, title);
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	public static CommerceDiscount getOrAddEmptyCommerceDiscount(
			String externalReferenceCode, long companyId, long userId)
		throws PortalException {

		return getService().getOrAddEmptyCommerceDiscount(
			externalReferenceCode, companyId, userId);
	}

	public static List<CommerceDiscount> getOrderTypeCommerceDiscounts(
		long commerceOrderTypeId, long cpDefinitionId, long cpInstanceId,
		String unitOfMeasureKey) {

		return getService().getOrderTypeCommerceDiscounts(
			commerceOrderTypeId, cpDefinitionId, cpInstanceId,
			unitOfMeasureKey);
	}

	public static List<CommerceDiscount> getOrderTypeCommerceDiscounts(
		long commerceOrderTypeId, String target) {

		return getService().getOrderTypeCommerceDiscounts(
			commerceOrderTypeId, target);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static List<CommerceDiscount> getPriceListCommerceDiscounts(
		long[] commerceDiscountIds, long cpDefinitionId) {

		return getService().getPriceListCommerceDiscounts(
			commerceDiscountIds, cpDefinitionId);
	}

	public static List<CommerceDiscount> getUnqualifiedCommerceDiscounts(
		long companyId, long cpDefinitionId, long cpInstanceId,
		String unitOfMeasureKey) {

		return getService().getUnqualifiedCommerceDiscounts(
			companyId, cpDefinitionId, cpInstanceId, unitOfMeasureKey);
	}

	public static List<CommerceDiscount> getUnqualifiedCommerceDiscounts(
		long companyId, String target) {

		return getService().getUnqualifiedCommerceDiscounts(companyId, target);
	}

	public static int getValidCommerceDiscountsCount(
		long commerceDiscountId, long cpDefinitionId, long cpInstanceId,
		String unitOfMeasureKey) {

		return getService().getValidCommerceDiscountsCount(
			commerceDiscountId, cpDefinitionId, cpInstanceId, unitOfMeasureKey);
	}

	public static int getValidCommerceDiscountsCount(
		long commerceAccountId, long[] commerceAccountGroupIds,
		long commerceChannelId, long commerceDiscountId) {

		return getService().getValidCommerceDiscountsCount(
			commerceAccountId, commerceAccountGroupIds, commerceChannelId,
			commerceDiscountId);
	}

	public static CommerceDiscount incrementCommerceDiscountNumberOfUse(
			long commerceDiscountId)
		throws PortalException {

		return getService().incrementCommerceDiscountNumberOfUse(
			commerceDiscountId);
	}

	public static List<CommerceDiscount> searchByCommercePricingClassId(
		long commercePricingClassId, String title, int start, int end) {

		return getService().searchByCommercePricingClassId(
			commercePricingClassId, title, start, end);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CommerceDiscount> searchCommerceDiscounts(
				long companyId, long[] groupIds, String keywords, int status,
				int start, int end, com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchCommerceDiscounts(
			companyId, groupIds, keywords, status, start, end, sort);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CommerceDiscount> searchCommerceDiscounts(
				com.liferay.portal.kernel.search.SearchContext searchContext)
			throws PortalException {

		return getService().searchCommerceDiscounts(searchContext);
	}

	/**
	 * Updates the commerce discount in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscount the commerce discount
	 * @return the commerce discount that was updated
	 */
	public static CommerceDiscount updateCommerceDiscount(
		CommerceDiscount commerceDiscount) {

		return getService().updateCommerceDiscount(commerceDiscount);
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

	public static CommerceDiscount updateStatus(
			long userId, long commerceDiscountId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		return getService().updateStatus(
			userId, commerceDiscountId, status, serviceContext,
			workflowContext);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #addOrUpdateCommerceDiscount(String, long, long, String, String,
	 boolean, String, boolean, BigDecimal, BigDecimal,
	 BigDecimal, BigDecimal, BigDecimal, String, int, boolean,
	 int, int, int, int, int, int, int, int, int, int, boolean,
	 ServiceContext)}
	 */
	@Deprecated
	public static CommerceDiscount upsertCommerceDiscount(
			long userId, long commerceDiscountId, String title, String target,
			boolean useCouponCode, String couponCode, boolean usePercentage,
			java.math.BigDecimal maximumDiscountAmount,
			java.math.BigDecimal level1, java.math.BigDecimal level2,
			java.math.BigDecimal level3, java.math.BigDecimal level4,
			String limitationType, int limitationTimes, boolean active,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			String externalReferenceCode, boolean neverExpire,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().upsertCommerceDiscount(
			userId, commerceDiscountId, title, target, useCouponCode,
			couponCode, usePercentage, maximumDiscountAmount, level1, level2,
			level3, level4, limitationType, limitationTimes, active,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			externalReferenceCode, neverExpire, serviceContext);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #addOrUpdateCommerceDiscount(String, long, long, String, String,
	 boolean, String, boolean, BigDecimal, String, BigDecimal,
	 BigDecimal, BigDecimal, BigDecimal, String, int, boolean,
	 boolean, int, int, int, int, int, int, int, int, int, int,
	 boolean, ServiceContext)}
	 */
	@Deprecated
	public static CommerceDiscount upsertCommerceDiscount(
			long userId, long commerceDiscountId, String title, String target,
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
			String externalReferenceCode, boolean neverExpire,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().upsertCommerceDiscount(
			userId, commerceDiscountId, title, target, useCouponCode,
			couponCode, usePercentage, maximumDiscountAmount, level, level1,
			level2, level3, level4, limitationType, limitationTimes,
			rulesConjunction, active, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, externalReferenceCode,
			neverExpire, serviceContext);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #addOrUpdateCommerceDiscount(String, long, long, String, String,
	 boolean, String, boolean, BigDecimal, String, BigDecimal,
	 BigDecimal, BigDecimal, BigDecimal, String, int, int,
	 boolean, boolean, int, int, int, int, int, int, int, int,
	 int, int, boolean, ServiceContext)}
	 */
	@Deprecated
	public static CommerceDiscount upsertCommerceDiscount(
			long userId, long commerceDiscountId, String title, String target,
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
			int expirationDateMinute, String externalReferenceCode,
			boolean neverExpire,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().upsertCommerceDiscount(
			userId, commerceDiscountId, title, target, useCouponCode,
			couponCode, usePercentage, maximumDiscountAmount, level, level1,
			level2, level3, level4, limitationType, limitationTimes,
			limitationTimesPerAccount, rulesConjunction, active,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			externalReferenceCode, neverExpire, serviceContext);
	}

	public static CommerceDiscountLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceDiscountLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceDiscountLocalServiceUtil.class,
			CommerceDiscountLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1028861521