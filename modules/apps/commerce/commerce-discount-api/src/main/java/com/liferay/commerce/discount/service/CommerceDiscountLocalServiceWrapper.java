/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CommerceDiscountLocalService}.
 *
 * @author Marco Leo
 * @see CommerceDiscountLocalService
 * @generated
 */
public class CommerceDiscountLocalServiceWrapper
	implements CommerceDiscountLocalService,
			   ServiceWrapper<CommerceDiscountLocalService> {

	public CommerceDiscountLocalServiceWrapper() {
		this(null);
	}

	public CommerceDiscountLocalServiceWrapper(
		CommerceDiscountLocalService commerceDiscountLocalService) {

		_commerceDiscountLocalService = commerceDiscountLocalService;
	}

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
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscount
		addCommerceDiscount(
			com.liferay.commerce.discount.model.CommerceDiscount
				commerceDiscount) {

		return _commerceDiscountLocalService.addCommerceDiscount(
			commerceDiscount);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscount
			addCommerceDiscount(
				long userId, String title, String target, boolean useCouponCode,
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

		return _commerceDiscountLocalService.addCommerceDiscount(
			userId, title, target, useCouponCode, couponCode, usePercentage,
			maximumDiscountAmount, level1, level2, level3, level4,
			limitationType, limitationTimes, active, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscount
			addCommerceDiscount(
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
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountLocalService.addCommerceDiscount(
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
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscount
			addCommerceDiscount(
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
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountLocalService.addCommerceDiscount(
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
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscount
			addCommerceDiscount(
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
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountLocalService.addCommerceDiscount(
			userId, title, target, useCouponCode, couponCode, usePercentage,
			maximumDiscountAmount, level, level1, level2, level3, level4,
			limitationType, limitationTimes, limitationTimesPerAccount,
			rulesConjunction, active, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, externalReferenceCode,
			neverExpire, serviceContext);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscount
			addCommerceDiscount(
				String externalReferenceCode, long userId, String title,
				String target, boolean useCouponCode, String couponCode,
				boolean usePercentage,
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

		return _commerceDiscountLocalService.addCommerceDiscount(
			externalReferenceCode, userId, title, target, useCouponCode,
			couponCode, usePercentage, maximumDiscountAmount, level, level1,
			level2, level3, level4, limitationType, limitationTimes,
			rulesConjunction, active, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscount
			addCommerceDiscount(
				String externalReferenceCode, long userId, String title,
				String target, boolean useCouponCode, String couponCode,
				boolean usePercentage,
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

		return _commerceDiscountLocalService.addCommerceDiscount(
			externalReferenceCode, userId, title, target, useCouponCode,
			couponCode, usePercentage, maximumDiscountAmount, level, level1,
			level2, level3, level4, limitationType, limitationTimes,
			limitationTimesPerAccount, rulesConjunction, active,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, serviceContext);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscount
			addOrUpdateCommerceDiscount(
				String externalReferenceCode, long userId,
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

		return _commerceDiscountLocalService.addOrUpdateCommerceDiscount(
			externalReferenceCode, userId, commerceDiscountId, title, target,
			useCouponCode, couponCode, usePercentage, maximumDiscountAmount,
			level1, level2, level3, level4, limitationType, limitationTimes,
			active, displayDateMonth, displayDateDay, displayDateYear,
			displayDateHour, displayDateMinute, expirationDateMonth,
			expirationDateDay, expirationDateYear, expirationDateHour,
			expirationDateMinute, neverExpire, serviceContext);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscount
			addOrUpdateCommerceDiscount(
				String externalReferenceCode, long userId,
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

		return _commerceDiscountLocalService.addOrUpdateCommerceDiscount(
			externalReferenceCode, userId, commerceDiscountId, title, target,
			useCouponCode, couponCode, usePercentage, maximumDiscountAmount,
			level, level1, level2, level3, level4, limitationType,
			limitationTimes, rulesConjunction, active, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscount
			addOrUpdateCommerceDiscount(
				String externalReferenceCode, long userId,
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

		return _commerceDiscountLocalService.addOrUpdateCommerceDiscount(
			externalReferenceCode, userId, commerceDiscountId, title, target,
			useCouponCode, couponCode, usePercentage, maximumDiscountAmount,
			level, level1, level2, level3, level4, limitationType,
			limitationTimes, limitationTimesPerAccount, rulesConjunction,
			active, displayDateMonth, displayDateDay, displayDateYear,
			displayDateHour, displayDateMinute, expirationDateMonth,
			expirationDateDay, expirationDateYear, expirationDateHour,
			expirationDateMinute, neverExpire, serviceContext);
	}

	@Override
	public void checkCommerceDiscounts()
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceDiscountLocalService.checkCommerceDiscounts();
	}

	/**
	 * Creates a new commerce discount with the primary key. Does not add the commerce discount to the database.
	 *
	 * @param commerceDiscountId the primary key for the new commerce discount
	 * @return the new commerce discount
	 */
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscount
		createCommerceDiscount(long commerceDiscountId) {

		return _commerceDiscountLocalService.createCommerceDiscount(
			commerceDiscountId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountLocalService.createPersistedModel(
			primaryKeyObj);
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
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscount
			deleteCommerceDiscount(
				com.liferay.commerce.discount.model.CommerceDiscount
					commerceDiscount)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountLocalService.deleteCommerceDiscount(
			commerceDiscount);
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
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscount
			deleteCommerceDiscount(long commerceDiscountId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountLocalService.deleteCommerceDiscount(
			commerceDiscountId);
	}

	@Override
	public void deleteCommerceDiscounts(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceDiscountLocalService.deleteCommerceDiscounts(companyId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commerceDiscountLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commerceDiscountLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commerceDiscountLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _commerceDiscountLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _commerceDiscountLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _commerceDiscountLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _commerceDiscountLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _commerceDiscountLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscount
		fetchCommerceDiscount(long commerceDiscountId) {

		return _commerceDiscountLocalService.fetchCommerceDiscount(
			commerceDiscountId);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscount
		fetchCommerceDiscountByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _commerceDiscountLocalService.
			fetchCommerceDiscountByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce discount with the matching UUID and company.
	 *
	 * @param uuid the commerce discount's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce discount, or <code>null</code> if a matching commerce discount could not be found
	 */
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscount
		fetchCommerceDiscountByUuidAndCompanyId(String uuid, long companyId) {

		return _commerceDiscountLocalService.
			fetchCommerceDiscountByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscount
		fetchDefaultCommerceDiscount(
			long commerceChannelAccountEntryRelId, long cpDefinitionId,
			long cpInstanceId, String unitOfMeasureKey) {

		return _commerceDiscountLocalService.fetchDefaultCommerceDiscount(
			commerceChannelAccountEntryRelId, cpDefinitionId, cpInstanceId,
			unitOfMeasureKey);
	}

	@Override
	public java.util.List<com.liferay.commerce.discount.model.CommerceDiscount>
		getAccountAndChannelAndOrderTypeCommerceDiscounts(
			long commerceAccountId, long commerceChannelId,
			long commerceOrderTypeId, long cpDefinitionId, long cpInstanceId,
			String unitOfMeasureKey) {

		return _commerceDiscountLocalService.
			getAccountAndChannelAndOrderTypeCommerceDiscounts(
				commerceAccountId, commerceChannelId, commerceOrderTypeId,
				cpDefinitionId, cpInstanceId, unitOfMeasureKey);
	}

	@Override
	public java.util.List<com.liferay.commerce.discount.model.CommerceDiscount>
		getAccountAndChannelAndOrderTypeCommerceDiscounts(
			long commerceAccountId, long commerceChannelId,
			long commerceOrderTypeId, String target) {

		return _commerceDiscountLocalService.
			getAccountAndChannelAndOrderTypeCommerceDiscounts(
				commerceAccountId, commerceChannelId, commerceOrderTypeId,
				target);
	}

	@Override
	public java.util.List<com.liferay.commerce.discount.model.CommerceDiscount>
		getAccountAndChannelCommerceDiscounts(
			long commerceAccountId, long commerceChannelId, long cpDefinitionId,
			long cpInstanceId, String unitOfMeasureKey) {

		return _commerceDiscountLocalService.
			getAccountAndChannelCommerceDiscounts(
				commerceAccountId, commerceChannelId, cpDefinitionId,
				cpInstanceId, unitOfMeasureKey);
	}

	@Override
	public java.util.List<com.liferay.commerce.discount.model.CommerceDiscount>
		getAccountAndChannelCommerceDiscounts(
			long commerceAccountId, long commerceChannelId, String target) {

		return _commerceDiscountLocalService.
			getAccountAndChannelCommerceDiscounts(
				commerceAccountId, commerceChannelId, target);
	}

	@Override
	public java.util.List<com.liferay.commerce.discount.model.CommerceDiscount>
		getAccountAndOrderTypeCommerceDiscounts(
			long commerceAccountId, long commerceOrderTypeId, String target) {

		return _commerceDiscountLocalService.
			getAccountAndOrderTypeCommerceDiscounts(
				commerceAccountId, commerceOrderTypeId, target);
	}

	@Override
	public java.util.List<com.liferay.commerce.discount.model.CommerceDiscount>
		getAccountCommerceAndOrderTypeDiscounts(
			long commerceAccountId, long commerceOrderTypeId,
			long cpDefinitionId, long cpInstanceId, String unitOfMeasureKey) {

		return _commerceDiscountLocalService.
			getAccountCommerceAndOrderTypeDiscounts(
				commerceAccountId, commerceOrderTypeId, cpDefinitionId,
				cpInstanceId, unitOfMeasureKey);
	}

	@Override
	public java.util.List<com.liferay.commerce.discount.model.CommerceDiscount>
		getAccountCommerceDiscounts(
			long commerceAccountId, long cpDefinitionId, long cpInstanceId,
			String unitOfMeasureKey) {

		return _commerceDiscountLocalService.getAccountCommerceDiscounts(
			commerceAccountId, cpDefinitionId, cpInstanceId, unitOfMeasureKey);
	}

	@Override
	public java.util.List<com.liferay.commerce.discount.model.CommerceDiscount>
		getAccountCommerceDiscounts(long commerceAccountId, String target) {

		return _commerceDiscountLocalService.getAccountCommerceDiscounts(
			commerceAccountId, target);
	}

	@Override
	public java.util.List<com.liferay.commerce.discount.model.CommerceDiscount>
		getAccountGroupAndChannelAndOrderTypeCommerceDiscount(
			long[] commerceAccountGroupIds, long commerceChannelId,
			long commerceOrderTypeId, long cpDefinitionId, long cpInstanceId,
			String unitOfMeasureKey) {

		return _commerceDiscountLocalService.
			getAccountGroupAndChannelAndOrderTypeCommerceDiscount(
				commerceAccountGroupIds, commerceChannelId, commerceOrderTypeId,
				cpDefinitionId, cpInstanceId, unitOfMeasureKey);
	}

	@Override
	public java.util.List<com.liferay.commerce.discount.model.CommerceDiscount>
		getAccountGroupAndChannelAndOrderTypeCommerceDiscount(
			long[] commerceAccountGroupIds, long commerceChannelId,
			long commerceOrderTypeId, String target) {

		return _commerceDiscountLocalService.
			getAccountGroupAndChannelAndOrderTypeCommerceDiscount(
				commerceAccountGroupIds, commerceChannelId, commerceOrderTypeId,
				target);
	}

	@Override
	public java.util.List<com.liferay.commerce.discount.model.CommerceDiscount>
		getAccountGroupAndChannelCommerceDiscount(
			long[] commerceAccountGroupIds, long commerceChannelId,
			long cpDefinitionId, long cpInstanceId, String unitOfMeasureKey) {

		return _commerceDiscountLocalService.
			getAccountGroupAndChannelCommerceDiscount(
				commerceAccountGroupIds, commerceChannelId, cpDefinitionId,
				cpInstanceId, unitOfMeasureKey);
	}

	@Override
	public java.util.List<com.liferay.commerce.discount.model.CommerceDiscount>
		getAccountGroupAndChannelCommerceDiscount(
			long[] commerceAccountGroupIds, long commerceChannelId,
			String target) {

		return _commerceDiscountLocalService.
			getAccountGroupAndChannelCommerceDiscount(
				commerceAccountGroupIds, commerceChannelId, target);
	}

	@Override
	public java.util.List<com.liferay.commerce.discount.model.CommerceDiscount>
		getAccountGroupAndOrderTypeCommerceDiscount(
			long[] commerceAccountGroupIds, long commerceOrderTypeId,
			long cpDefinitionId, long cpInstanceId, String unitOfMeasureKey) {

		return _commerceDiscountLocalService.
			getAccountGroupAndOrderTypeCommerceDiscount(
				commerceAccountGroupIds, commerceOrderTypeId, cpDefinitionId,
				cpInstanceId, unitOfMeasureKey);
	}

	@Override
	public java.util.List<com.liferay.commerce.discount.model.CommerceDiscount>
		getAccountGroupAndOrderTypeCommerceDiscount(
			long[] commerceAccountGroupIds, long commerceOrderTypeId,
			String target) {

		return _commerceDiscountLocalService.
			getAccountGroupAndOrderTypeCommerceDiscount(
				commerceAccountGroupIds, commerceOrderTypeId, target);
	}

	@Override
	public java.util.List<com.liferay.commerce.discount.model.CommerceDiscount>
		getAccountGroupCommerceDiscount(
			long[] commerceAccountGroupIds, long cpDefinitionId,
			long cpInstanceId, String unitOfMeasureKey) {

		return _commerceDiscountLocalService.getAccountGroupCommerceDiscount(
			commerceAccountGroupIds, cpDefinitionId, cpInstanceId,
			unitOfMeasureKey);
	}

	@Override
	public java.util.List<com.liferay.commerce.discount.model.CommerceDiscount>
		getAccountGroupCommerceDiscount(
			long[] commerceAccountGroupIds, String target) {

		return _commerceDiscountLocalService.getAccountGroupCommerceDiscount(
			commerceAccountGroupIds, target);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commerceDiscountLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscount
			getActiveCommerceDiscount(
				long companyId, String couponCode, boolean active)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountLocalService.getActiveCommerceDiscount(
			companyId, couponCode, active);
	}

	@Override
	public int getActiveCommerceDiscountsCount(
		long companyId, String couponCode, boolean active) {

		return _commerceDiscountLocalService.getActiveCommerceDiscountsCount(
			companyId, couponCode, active);
	}

	@Override
	public java.util.List<com.liferay.commerce.discount.model.CommerceDiscount>
		getChannelAndOrderTypeCommerceDiscounts(
			long commerceChannelId, long commerceOrderTypeId,
			long cpDefinitionId, long cpInstanceId, String unitOfMeasureKey) {

		return _commerceDiscountLocalService.
			getChannelAndOrderTypeCommerceDiscounts(
				commerceChannelId, commerceOrderTypeId, cpDefinitionId,
				cpInstanceId, unitOfMeasureKey);
	}

	@Override
	public java.util.List<com.liferay.commerce.discount.model.CommerceDiscount>
		getChannelAndOrderTypeCommerceDiscounts(
			long commerceChannelId, long commerceOrderTypeId, String target) {

		return _commerceDiscountLocalService.
			getChannelAndOrderTypeCommerceDiscounts(
				commerceChannelId, commerceOrderTypeId, target);
	}

	@Override
	public java.util.List<com.liferay.commerce.discount.model.CommerceDiscount>
		getChannelCommerceDiscounts(
			long commerceChannelId, long cpDefinitionId, long cpInstanceId,
			String unitOfMeasureKey) {

		return _commerceDiscountLocalService.getChannelCommerceDiscounts(
			commerceChannelId, cpDefinitionId, cpInstanceId, unitOfMeasureKey);
	}

	@Override
	public java.util.List<com.liferay.commerce.discount.model.CommerceDiscount>
		getChannelCommerceDiscounts(long commerceChannelId, String target) {

		return _commerceDiscountLocalService.getChannelCommerceDiscounts(
			commerceChannelId, target);
	}

	/**
	 * Returns the commerce discount with the primary key.
	 *
	 * @param commerceDiscountId the primary key of the commerce discount
	 * @return the commerce discount
	 * @throws PortalException if a commerce discount with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscount
			getCommerceDiscount(long commerceDiscountId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountLocalService.getCommerceDiscount(
			commerceDiscountId);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscount
			getCommerceDiscountByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountLocalService.
			getCommerceDiscountByExternalReferenceCode(
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
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscount
			getCommerceDiscountByUuidAndCompanyId(String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountLocalService.
			getCommerceDiscountByUuidAndCompanyId(uuid, companyId);
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
	@Override
	public java.util.List<com.liferay.commerce.discount.model.CommerceDiscount>
		getCommerceDiscounts(int start, int end) {

		return _commerceDiscountLocalService.getCommerceDiscounts(start, end);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	@Override
	public java.util.List<com.liferay.commerce.discount.model.CommerceDiscount>
		getCommerceDiscounts(long companyId, String couponCode) {

		return _commerceDiscountLocalService.getCommerceDiscounts(
			companyId, couponCode);
	}

	@Override
	public java.util.List<com.liferay.commerce.discount.model.CommerceDiscount>
		getCommerceDiscounts(
			long companyId, String level, boolean active, int status) {

		return _commerceDiscountLocalService.getCommerceDiscounts(
			companyId, level, active, status);
	}

	/**
	 * Returns the number of commerce discounts.
	 *
	 * @return the number of commerce discounts
	 */
	@Override
	public int getCommerceDiscountsCount() {
		return _commerceDiscountLocalService.getCommerceDiscountsCount();
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	@Override
	public int getCommerceDiscountsCount(long companyId, String couponCode) {
		return _commerceDiscountLocalService.getCommerceDiscountsCount(
			companyId, couponCode);
	}

	@Override
	public int getCommerceDiscountsCountByPricingClassId(
		long commercePricingClassId, String title) {

		return _commerceDiscountLocalService.
			getCommerceDiscountsCountByPricingClassId(
				commercePricingClassId, title);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _commerceDiscountLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commerceDiscountLocalService.
			getIndexableActionableDynamicQuery();
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscount
			getOrAddEmptyCommerceDiscount(
				String externalReferenceCode, long companyId, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountLocalService.getOrAddEmptyCommerceDiscount(
			externalReferenceCode, companyId, userId);
	}

	@Override
	public java.util.List<com.liferay.commerce.discount.model.CommerceDiscount>
		getOrderTypeCommerceDiscounts(
			long commerceOrderTypeId, long cpDefinitionId, long cpInstanceId,
			String unitOfMeasureKey) {

		return _commerceDiscountLocalService.getOrderTypeCommerceDiscounts(
			commerceOrderTypeId, cpDefinitionId, cpInstanceId,
			unitOfMeasureKey);
	}

	@Override
	public java.util.List<com.liferay.commerce.discount.model.CommerceDiscount>
		getOrderTypeCommerceDiscounts(long commerceOrderTypeId, String target) {

		return _commerceDiscountLocalService.getOrderTypeCommerceDiscounts(
			commerceOrderTypeId, target);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceDiscountLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public java.util.List<com.liferay.commerce.discount.model.CommerceDiscount>
		getPriceListCommerceDiscounts(
			long[] commerceDiscountIds, long cpDefinitionId) {

		return _commerceDiscountLocalService.getPriceListCommerceDiscounts(
			commerceDiscountIds, cpDefinitionId);
	}

	@Override
	public java.util.List<com.liferay.commerce.discount.model.CommerceDiscount>
		getUnqualifiedCommerceDiscounts(
			long companyId, long cpDefinitionId, long cpInstanceId,
			String unitOfMeasureKey) {

		return _commerceDiscountLocalService.getUnqualifiedCommerceDiscounts(
			companyId, cpDefinitionId, cpInstanceId, unitOfMeasureKey);
	}

	@Override
	public java.util.List<com.liferay.commerce.discount.model.CommerceDiscount>
		getUnqualifiedCommerceDiscounts(long companyId, String target) {

		return _commerceDiscountLocalService.getUnqualifiedCommerceDiscounts(
			companyId, target);
	}

	@Override
	public int getValidCommerceDiscountsCount(
		long commerceDiscountId, long cpDefinitionId, long cpInstanceId,
		String unitOfMeasureKey) {

		return _commerceDiscountLocalService.getValidCommerceDiscountsCount(
			commerceDiscountId, cpDefinitionId, cpInstanceId, unitOfMeasureKey);
	}

	@Override
	public int getValidCommerceDiscountsCount(
		long commerceAccountId, long[] commerceAccountGroupIds,
		long commerceChannelId, long commerceDiscountId) {

		return _commerceDiscountLocalService.getValidCommerceDiscountsCount(
			commerceAccountId, commerceAccountGroupIds, commerceChannelId,
			commerceDiscountId);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscount
			incrementCommerceDiscountNumberOfUse(long commerceDiscountId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountLocalService.
			incrementCommerceDiscountNumberOfUse(commerceDiscountId);
	}

	@Override
	public java.util.List<com.liferay.commerce.discount.model.CommerceDiscount>
		searchByCommercePricingClassId(
			long commercePricingClassId, String title, int start, int end) {

		return _commerceDiscountLocalService.searchByCommercePricingClassId(
			commercePricingClassId, title, start, end);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.commerce.discount.model.CommerceDiscount>
				searchCommerceDiscounts(
					long companyId, long[] groupIds, String keywords,
					int status, int start, int end,
					com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountLocalService.searchCommerceDiscounts(
			companyId, groupIds, keywords, status, start, end, sort);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.commerce.discount.model.CommerceDiscount>
				searchCommerceDiscounts(
					com.liferay.portal.kernel.search.SearchContext
						searchContext)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountLocalService.searchCommerceDiscounts(
			searchContext);
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
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscount
		updateCommerceDiscount(
			com.liferay.commerce.discount.model.CommerceDiscount
				commerceDiscount) {

		return _commerceDiscountLocalService.updateCommerceDiscount(
			commerceDiscount);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscount
			updateCommerceDiscount(
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

		return _commerceDiscountLocalService.updateCommerceDiscount(
			commerceDiscountId, title, target, useCouponCode, couponCode,
			usePercentage, maximumDiscountAmount, level1, level2, level3,
			level4, limitationType, limitationTimes, active, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscount
			updateCommerceDiscount(
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

		return _commerceDiscountLocalService.updateCommerceDiscount(
			commerceDiscountId, title, target, useCouponCode, couponCode,
			usePercentage, maximumDiscountAmount, level, level1, level2, level3,
			level4, limitationType, limitationTimes, rulesConjunction, active,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, serviceContext);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscount
			updateCommerceDiscount(
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

		return _commerceDiscountLocalService.updateCommerceDiscount(
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
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscount
			updateCommerceDiscountExternalReferenceCode(
				long commerceDiscountId, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountLocalService.
			updateCommerceDiscountExternalReferenceCode(
				commerceDiscountId, externalReferenceCode);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscount
			updateCommerceDiscountExternalReferenceCode(
				String externalReferenceCode, long commerceDiscountId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountLocalService.
			updateCommerceDiscountExternalReferenceCode(
				externalReferenceCode, commerceDiscountId);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscount updateStatus(
			long userId, long commerceDiscountId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			java.util.Map<String, java.io.Serializable> workflowContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountLocalService.updateStatus(
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
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscount
			upsertCommerceDiscount(
				long userId, long commerceDiscountId, String title,
				String target, boolean useCouponCode, String couponCode,
				boolean usePercentage,
				java.math.BigDecimal maximumDiscountAmount,
				java.math.BigDecimal level1, java.math.BigDecimal level2,
				java.math.BigDecimal level3, java.math.BigDecimal level4,
				String limitationType, int limitationTimes, boolean active,
				int displayDateMonth, int displayDateDay, int displayDateYear,
				int displayDateHour, int displayDateMinute,
				int expirationDateMonth, int expirationDateDay,
				int expirationDateYear, int expirationDateHour,
				int expirationDateMinute, String externalReferenceCode,
				boolean neverExpire,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountLocalService.upsertCommerceDiscount(
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
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscount
			upsertCommerceDiscount(
				long userId, long commerceDiscountId, String title,
				String target, boolean useCouponCode, String couponCode,
				boolean usePercentage,
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
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountLocalService.upsertCommerceDiscount(
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
	@Override
	public com.liferay.commerce.discount.model.CommerceDiscount
			upsertCommerceDiscount(
				long userId, long commerceDiscountId, String title,
				String target, boolean useCouponCode, String couponCode,
				boolean usePercentage,
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
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountLocalService.upsertCommerceDiscount(
			userId, commerceDiscountId, title, target, useCouponCode,
			couponCode, usePercentage, maximumDiscountAmount, level, level1,
			level2, level3, level4, limitationType, limitationTimes,
			limitationTimesPerAccount, rulesConjunction, active,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			externalReferenceCode, neverExpire, serviceContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commerceDiscountLocalService.getBasePersistence();
	}

	@Override
	public CommerceDiscountLocalService getWrappedService() {
		return _commerceDiscountLocalService;
	}

	@Override
	public void setWrappedService(
		CommerceDiscountLocalService commerceDiscountLocalService) {

		_commerceDiscountLocalService = commerceDiscountLocalService;
	}

	private CommerceDiscountLocalService _commerceDiscountLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:888709029