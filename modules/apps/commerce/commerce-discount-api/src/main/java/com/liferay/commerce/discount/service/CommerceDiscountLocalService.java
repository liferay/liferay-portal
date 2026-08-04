/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service;

import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.List;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for CommerceDiscount. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Marco Leo
 * @see CommerceDiscountLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface CommerceDiscountLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.commerce.discount.service.impl.CommerceDiscountLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the commerce discount local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CommerceDiscountLocalServiceUtil} if injection and service tracking are not available.
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
	@Indexable(type = IndexableType.REINDEX)
	public CommerceDiscount addCommerceDiscount(
		CommerceDiscount commerceDiscount);

	@Indexable(type = IndexableType.REINDEX)
	public CommerceDiscount addCommerceDiscount(
			long userId, String title, String target, boolean useCouponCode,
			String couponCode, boolean usePercentage,
			BigDecimal maximumDiscountAmount, BigDecimal level1,
			BigDecimal level2, BigDecimal level3, BigDecimal level4,
			String limitationType, int limitationTimes, boolean active,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceDiscount addCommerceDiscount(
			long userId, String title, String target, boolean useCouponCode,
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
		throws PortalException;

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #addCommerceDiscount(String, long, String, String, boolean,
	 String, boolean, BigDecimal, String, BigDecimal, BigDecimal,
	 BigDecimal, BigDecimal, String, int, boolean, boolean, int,
	 int, int, int, int, int, int, int, int, int, boolean,
	 ServiceContext)}
	 */
	@Deprecated
	@Indexable(type = IndexableType.REINDEX)
	public CommerceDiscount addCommerceDiscount(
			long userId, String title, String target, boolean useCouponCode,
			String couponCode, boolean usePercentage,
			BigDecimal maximumDiscountAmount, String level, BigDecimal level1,
			BigDecimal level2, BigDecimal level3, BigDecimal level4,
			String limitationType, int limitationTimes,
			boolean rulesConjunction, boolean active, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			String externalReferenceCode, boolean neverExpire,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #addCommerceDiscount(String, long, String, String, boolean, String,
	 boolean, BigDecimal, String, BigDecimal, BigDecimal,
	 BigDecimal, BigDecimal, BigDecimal, String, int, int,
	 boolean, boolean, int, int, int, int, int, int, int, int,
	 int, int, boolean, ServiceContext)}
	 */
	@Deprecated
	@Indexable(type = IndexableType.REINDEX)
	public CommerceDiscount addCommerceDiscount(
			long userId, String title, String target, boolean useCouponCode,
			String couponCode, boolean usePercentage,
			BigDecimal maximumDiscountAmount, String level, BigDecimal level1,
			BigDecimal level2, BigDecimal level3, BigDecimal level4,
			String limitationType, int limitationTimes,
			int limitationTimesPerAccount, boolean rulesConjunction,
			boolean active, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, String externalReferenceCode,
			boolean neverExpire, ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceDiscount addCommerceDiscount(
			String externalReferenceCode, long userId, String title,
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
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceDiscount addCommerceDiscount(
			String externalReferenceCode, long userId, String title,
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
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceDiscount addOrUpdateCommerceDiscount(
			String externalReferenceCode, long userId, long commerceDiscountId,
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
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceDiscount addOrUpdateCommerceDiscount(
			String externalReferenceCode, long userId, long commerceDiscountId,
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
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceDiscount addOrUpdateCommerceDiscount(
			String externalReferenceCode, long userId, long commerceDiscountId,
			String title, String target, boolean useCouponCode,
			String couponCode, boolean usePercentage,
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
		throws PortalException;

	public void checkCommerceDiscounts() throws PortalException;

	/**
	 * Creates a new commerce discount with the primary key. Does not add the commerce discount to the database.
	 *
	 * @param commerceDiscountId the primary key for the new commerce discount
	 * @return the new commerce discount
	 */
	@Transactional(enabled = false)
	public CommerceDiscount createCommerceDiscount(long commerceDiscountId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

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
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommerceDiscount deleteCommerceDiscount(
			CommerceDiscount commerceDiscount)
		throws PortalException;

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
	@Indexable(type = IndexableType.DELETE)
	public CommerceDiscount deleteCommerceDiscount(long commerceDiscountId)
		throws PortalException;

	public void deleteCommerceDiscounts(long companyId) throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> T dslQuery(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int dslQueryCount(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DynamicQuery dynamicQuery();

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(DynamicQuery dynamicQuery);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(
		DynamicQuery dynamicQuery, Projection projection);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceDiscount fetchCommerceDiscount(long commerceDiscountId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceDiscount fetchCommerceDiscountByExternalReferenceCode(
		String externalReferenceCode, long companyId);

	/**
	 * Returns the commerce discount with the matching UUID and company.
	 *
	 * @param uuid the commerce discount's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce discount, or <code>null</code> if a matching commerce discount could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceDiscount fetchCommerceDiscountByUuidAndCompanyId(
		String uuid, long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceDiscount fetchDefaultCommerceDiscount(
		long commerceChannelAccountEntryRelId, long cpDefinitionId,
		long cpInstanceId, String unitOfMeasureKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscount>
		getAccountAndChannelAndOrderTypeCommerceDiscounts(
			long commerceAccountId, long commerceChannelId,
			long commerceOrderTypeId, long cpDefinitionId, long cpInstanceId,
			String unitOfMeasureKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscount>
		getAccountAndChannelAndOrderTypeCommerceDiscounts(
			long commerceAccountId, long commerceChannelId,
			long commerceOrderTypeId, String target);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscount> getAccountAndChannelCommerceDiscounts(
		long commerceAccountId, long commerceChannelId, long cpDefinitionId,
		long cpInstanceId, String unitOfMeasureKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscount> getAccountAndChannelCommerceDiscounts(
		long commerceAccountId, long commerceChannelId, String target);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscount> getAccountAndOrderTypeCommerceDiscounts(
		long commerceAccountId, long commerceOrderTypeId, String target);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscount> getAccountCommerceAndOrderTypeDiscounts(
		long commerceAccountId, long commerceOrderTypeId, long cpDefinitionId,
		long cpInstanceId, String unitOfMeasureKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscount> getAccountCommerceDiscounts(
		long commerceAccountId, long cpDefinitionId, long cpInstanceId,
		String unitOfMeasureKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscount> getAccountCommerceDiscounts(
		long commerceAccountId, String target);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscount>
		getAccountGroupAndChannelAndOrderTypeCommerceDiscount(
			long[] commerceAccountGroupIds, long commerceChannelId,
			long commerceOrderTypeId, long cpDefinitionId, long cpInstanceId,
			String unitOfMeasureKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscount>
		getAccountGroupAndChannelAndOrderTypeCommerceDiscount(
			long[] commerceAccountGroupIds, long commerceChannelId,
			long commerceOrderTypeId, String target);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscount> getAccountGroupAndChannelCommerceDiscount(
		long[] commerceAccountGroupIds, long commerceChannelId,
		long cpDefinitionId, long cpInstanceId, String unitOfMeasureKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscount> getAccountGroupAndChannelCommerceDiscount(
		long[] commerceAccountGroupIds, long commerceChannelId, String target);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscount> getAccountGroupAndOrderTypeCommerceDiscount(
		long[] commerceAccountGroupIds, long commerceOrderTypeId,
		long cpDefinitionId, long cpInstanceId, String unitOfMeasureKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscount> getAccountGroupAndOrderTypeCommerceDiscount(
		long[] commerceAccountGroupIds, long commerceOrderTypeId,
		String target);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscount> getAccountGroupCommerceDiscount(
		long[] commerceAccountGroupIds, long cpDefinitionId, long cpInstanceId,
		String unitOfMeasureKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscount> getAccountGroupCommerceDiscount(
		long[] commerceAccountGroupIds, String target);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceDiscount getActiveCommerceDiscount(
			long companyId, String couponCode, boolean active)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getActiveCommerceDiscountsCount(
		long companyId, String couponCode, boolean active);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscount> getChannelAndOrderTypeCommerceDiscounts(
		long commerceChannelId, long commerceOrderTypeId, long cpDefinitionId,
		long cpInstanceId, String unitOfMeasureKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscount> getChannelAndOrderTypeCommerceDiscounts(
		long commerceChannelId, long commerceOrderTypeId, String target);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscount> getChannelCommerceDiscounts(
		long commerceChannelId, long cpDefinitionId, long cpInstanceId,
		String unitOfMeasureKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscount> getChannelCommerceDiscounts(
		long commerceChannelId, String target);

	/**
	 * Returns the commerce discount with the primary key.
	 *
	 * @param commerceDiscountId the primary key of the commerce discount
	 * @return the commerce discount
	 * @throws PortalException if a commerce discount with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceDiscount getCommerceDiscount(long commerceDiscountId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceDiscount getCommerceDiscountByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException;

	/**
	 * Returns the commerce discount with the matching UUID and company.
	 *
	 * @param uuid the commerce discount's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce discount
	 * @throws PortalException if a matching commerce discount could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceDiscount getCommerceDiscountByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException;

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscount> getCommerceDiscounts(int start, int end);

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscount> getCommerceDiscounts(
		long companyId, String couponCode);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscount> getCommerceDiscounts(
		long companyId, String level, boolean active, int status);

	/**
	 * Returns the number of commerce discounts.
	 *
	 * @return the number of commerce discounts
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceDiscountsCount();

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceDiscountsCount(long companyId, String couponCode);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceDiscountsCountByPricingClassId(
		long commercePricingClassId, String title);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceDiscount getOrAddEmptyCommerceDiscount(
			String externalReferenceCode, long companyId, long userId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscount> getOrderTypeCommerceDiscounts(
		long commerceOrderTypeId, long cpDefinitionId, long cpInstanceId,
		String unitOfMeasureKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscount> getOrderTypeCommerceDiscounts(
		long commerceOrderTypeId, String target);

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscount> getPriceListCommerceDiscounts(
		long[] commerceDiscountIds, long cpDefinitionId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscount> getUnqualifiedCommerceDiscounts(
		long companyId, long cpDefinitionId, long cpInstanceId,
		String unitOfMeasureKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscount> getUnqualifiedCommerceDiscounts(
		long companyId, String target);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getValidCommerceDiscountsCount(
		long commerceDiscountId, long cpDefinitionId, long cpInstanceId,
		String unitOfMeasureKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getValidCommerceDiscountsCount(
		long commerceAccountId, long[] commerceAccountGroupIds,
		long commerceChannelId, long commerceDiscountId);

	@Indexable(type = IndexableType.REINDEX)
	public CommerceDiscount incrementCommerceDiscountNumberOfUse(
			long commerceDiscountId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscount> searchByCommercePricingClassId(
		long commercePricingClassId, String title, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<CommerceDiscount> searchCommerceDiscounts(
			long companyId, long[] groupIds, String keywords, int status,
			int start, int end, Sort sort)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<CommerceDiscount> searchCommerceDiscounts(
			SearchContext searchContext)
		throws PortalException;

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
	@Indexable(type = IndexableType.REINDEX)
	public CommerceDiscount updateCommerceDiscount(
		CommerceDiscount commerceDiscount);

	@Indexable(type = IndexableType.REINDEX)
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
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
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
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
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
		throws PortalException;

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #updateCommerceDiscountExternalReferenceCode(String, long)}
	 */
	@Deprecated
	@Indexable(type = IndexableType.REINDEX)
	public CommerceDiscount updateCommerceDiscountExternalReferenceCode(
			long commerceDiscountId, String externalReferenceCode)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceDiscount updateCommerceDiscountExternalReferenceCode(
			String externalReferenceCode, long commerceDiscountId)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceDiscount updateStatus(
			long userId, long commerceDiscountId, int status,
			ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException;

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #addOrUpdateCommerceDiscount(String, long, long, String, String,
	 boolean, String, boolean, BigDecimal, BigDecimal,
	 BigDecimal, BigDecimal, BigDecimal, String, int, boolean,
	 int, int, int, int, int, int, int, int, int, int, boolean,
	 ServiceContext)}
	 */
	@Deprecated
	@Indexable(type = IndexableType.REINDEX)
	public CommerceDiscount upsertCommerceDiscount(
			long userId, long commerceDiscountId, String title, String target,
			boolean useCouponCode, String couponCode, boolean usePercentage,
			BigDecimal maximumDiscountAmount, BigDecimal level1,
			BigDecimal level2, BigDecimal level3, BigDecimal level4,
			String limitationType, int limitationTimes, boolean active,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			String externalReferenceCode, boolean neverExpire,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #addOrUpdateCommerceDiscount(String, long, long, String, String,
	 boolean, String, boolean, BigDecimal, String, BigDecimal,
	 BigDecimal, BigDecimal, BigDecimal, String, int, boolean,
	 boolean, int, int, int, int, int, int, int, int, int, int,
	 boolean, ServiceContext)}
	 */
	@Deprecated
	@Indexable(type = IndexableType.REINDEX)
	public CommerceDiscount upsertCommerceDiscount(
			long userId, long commerceDiscountId, String title, String target,
			boolean useCouponCode, String couponCode, boolean usePercentage,
			BigDecimal maximumDiscountAmount, String level, BigDecimal level1,
			BigDecimal level2, BigDecimal level3, BigDecimal level4,
			String limitationType, int limitationTimes,
			boolean rulesConjunction, boolean active, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			String externalReferenceCode, boolean neverExpire,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #addOrUpdateCommerceDiscount(String, long, long, String, String,
	 boolean, String, boolean, BigDecimal, String, BigDecimal,
	 BigDecimal, BigDecimal, BigDecimal, String, int, int,
	 boolean, boolean, int, int, int, int, int, int, int, int,
	 int, int, boolean, ServiceContext)}
	 */
	@Deprecated
	@Indexable(type = IndexableType.REINDEX)
	public CommerceDiscount upsertCommerceDiscount(
			long userId, long commerceDiscountId, String title, String target,
			boolean useCouponCode, String couponCode, boolean usePercentage,
			BigDecimal maximumDiscountAmount, String level, BigDecimal level1,
			BigDecimal level2, BigDecimal level3, BigDecimal level4,
			String limitationType, int limitationTimes,
			int limitationTimesPerAccount, boolean rulesConjunction,
			boolean active, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, String externalReferenceCode,
			boolean neverExpire, ServiceContext serviceContext)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:2107546501