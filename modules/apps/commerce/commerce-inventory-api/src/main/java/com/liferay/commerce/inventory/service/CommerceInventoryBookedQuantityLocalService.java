/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.service;

import com.liferay.commerce.inventory.exception.NoSuchInventoryBookedQuantityException;
import com.liferay.commerce.inventory.model.CommerceInventoryBookedQuantity;
import com.liferay.commerce.inventory.type.CommerceInventoryAuditType;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for CommerceInventoryBookedQuantity. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Luca Pellizzon
 * @see CommerceInventoryBookedQuantityLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface CommerceInventoryBookedQuantityLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.commerce.inventory.service.impl.CommerceInventoryBookedQuantityLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the commerce inventory booked quantity local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CommerceInventoryBookedQuantityLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the commerce inventory booked quantity to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryBookedQuantityLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryBookedQuantity the commerce inventory booked quantity
	 * @return the commerce inventory booked quantity that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public CommerceInventoryBookedQuantity addCommerceInventoryBookedQuantity(
		CommerceInventoryBookedQuantity commerceInventoryBookedQuantity);

	@Indexable(type = IndexableType.REINDEX)
	public CommerceInventoryBookedQuantity addCommerceInventoryBookedQuantity(
			long userId, Date expirationDate, BigDecimal quantity, String sku,
			String unitOfMeasureKey, Map<String, String> context)
		throws PortalException;

	public void checkCommerceInventoryBookedQuantities();

	public CommerceInventoryBookedQuantity
			consumeCommerceInventoryBookedQuantity(
				long commerceInventoryBookedQuantityId, BigDecimal quantity)
		throws NoSuchInventoryBookedQuantityException;

	/**
	 * Creates a new commerce inventory booked quantity with the primary key. Does not add the commerce inventory booked quantity to the database.
	 *
	 * @param commerceInventoryBookedQuantityId the primary key for the new commerce inventory booked quantity
	 * @return the new commerce inventory booked quantity
	 */
	@Transactional(enabled = false)
	public CommerceInventoryBookedQuantity
		createCommerceInventoryBookedQuantity(
			long commerceInventoryBookedQuantityId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the commerce inventory booked quantity from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryBookedQuantityLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryBookedQuantity the commerce inventory booked quantity
	 * @return the commerce inventory booked quantity that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public CommerceInventoryBookedQuantity
		deleteCommerceInventoryBookedQuantity(
			CommerceInventoryBookedQuantity commerceInventoryBookedQuantity);

	/**
	 * Deletes the commerce inventory booked quantity with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryBookedQuantityLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryBookedQuantityId the primary key of the commerce inventory booked quantity
	 * @return the commerce inventory booked quantity that was removed
	 * @throws PortalException if a commerce inventory booked quantity with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public CommerceInventoryBookedQuantity
			deleteCommerceInventoryBookedQuantity(
				long commerceInventoryBookedQuantityId)
		throws PortalException;

	public CommerceInventoryBookedQuantity
			deleteCommerceInventoryBookedQuantity(
				long userId, long commerceInventoryBookedQuantityId,
				Map<String, String> context,
				CommerceInventoryAuditType commerceInventoryAuditType)
		throws PortalException;

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryBookedQuantityModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryBookedQuantityModelImpl</code>.
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
	public CommerceInventoryBookedQuantity fetchCommerceInventoryBookedQuantity(
		long commerceInventoryBookedQuantityId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	/**
	 * Returns a range of all the commerce inventory booked quantities.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryBookedQuantityModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce inventory booked quantities
	 * @param end the upper bound of the range of commerce inventory booked quantities (not inclusive)
	 * @return the range of commerce inventory booked quantities
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceInventoryBookedQuantity>
		getCommerceInventoryBookedQuantities(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceInventoryBookedQuantity>
		getCommerceInventoryBookedQuantities(
			long companyId, String sku, String unitOfMeasureKey, int start,
			int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceInventoryBookedQuantity>
			getCommerceInventoryBookedQuantities(
				long companyId, String keywords, String sku,
				String unitOfMeasureKey, int start, int end)
		throws PortalException;

	/**
	 * Returns the number of commerce inventory booked quantities.
	 *
	 * @return the number of commerce inventory booked quantities
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceInventoryBookedQuantitiesCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceInventoryBookedQuantitiesCount(
			long companyId, String keywords, String sku,
			String unitOfMeasureKey)
		throws PortalException;

	/**
	 * Returns the commerce inventory booked quantity with the primary key.
	 *
	 * @param commerceInventoryBookedQuantityId the primary key of the commerce inventory booked quantity
	 * @return the commerce inventory booked quantity
	 * @throws PortalException if a commerce inventory booked quantity with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceInventoryBookedQuantity getCommerceInventoryBookedQuantity(
			long commerceInventoryBookedQuantityId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BigDecimal getCommerceInventoryBookedQuantity(
		long companyId, long commerceChannelGroupId, String sku,
		String unitOfMeasureKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BigDecimal getCommerceInventoryBookedQuantity(
		long companyId, String sku, String unitOfMeasureKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

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

	public CommerceInventoryBookedQuantity resetCommerceInventoryBookedQuantity(
			long commerceInventoryBookedQuantityId, long userId,
			Date expirationDate, BigDecimal quantity, String sku,
			String unitOfMeasureKey, Map<String, String> context)
		throws PortalException;

	public CommerceInventoryBookedQuantity
			restockCommerceInventoryBookedQuantity(
				long userId, long commerceInventoryBookedQuantityId,
				Map<String, String> context)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<CommerceInventoryBookedQuantity>
			searchCommerceInventoryBookedQuantities(SearchContext searchContext)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCommerceInventoryBookedQuantitiesCount(
			SearchContext searchContext)
		throws PortalException;

	/**
	 * Updates the commerce inventory booked quantity in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryBookedQuantityLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryBookedQuantity the commerce inventory booked quantity
	 * @return the commerce inventory booked quantity that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public CommerceInventoryBookedQuantity
		updateCommerceInventoryBookedQuantity(
			CommerceInventoryBookedQuantity commerceInventoryBookedQuantity);

	@Indexable(type = IndexableType.REINDEX)
	public CommerceInventoryBookedQuantity
			updateCommerceInventoryBookedQuantity(
				long userId, long commerceInventoryBookedQuantityId,
				BigDecimal quantity, Map<String, String> context,
				long mvccVersion)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:392693913