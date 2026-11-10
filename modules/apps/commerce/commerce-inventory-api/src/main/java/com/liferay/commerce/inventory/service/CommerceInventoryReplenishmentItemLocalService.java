/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.service;

import com.liferay.commerce.inventory.model.CommerceInventoryReplenishmentItem;
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
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
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

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for CommerceInventoryReplenishmentItem. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Luca Pellizzon
 * @see CommerceInventoryReplenishmentItemLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface CommerceInventoryReplenishmentItemLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.commerce.inventory.service.impl.CommerceInventoryReplenishmentItemLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the commerce inventory replenishment item local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CommerceInventoryReplenishmentItemLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the commerce inventory replenishment item to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryReplenishmentItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryReplenishmentItem the commerce inventory replenishment item
	 * @return the commerce inventory replenishment item that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public CommerceInventoryReplenishmentItem
		addCommerceInventoryReplenishmentItem(
			CommerceInventoryReplenishmentItem
				commerceInventoryReplenishmentItem);

	public CommerceInventoryReplenishmentItem
			addCommerceInventoryReplenishmentItem(
				String externalReferenceCode, long userId,
				long commerceInventoryWarehouseId, Date availabilityDate,
				BigDecimal quantity, String sku, String unitOfMeasureKey)
		throws PortalException;

	/**
	 * Creates a new commerce inventory replenishment item with the primary key. Does not add the commerce inventory replenishment item to the database.
	 *
	 * @param commerceInventoryReplenishmentItemId the primary key for the new commerce inventory replenishment item
	 * @return the new commerce inventory replenishment item
	 */
	@Transactional(enabled = false)
	public CommerceInventoryReplenishmentItem
		createCommerceInventoryReplenishmentItem(
			long commerceInventoryReplenishmentItemId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the commerce inventory replenishment item from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryReplenishmentItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryReplenishmentItem the commerce inventory replenishment item
	 * @return the commerce inventory replenishment item that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public CommerceInventoryReplenishmentItem
		deleteCommerceInventoryReplenishmentItem(
			CommerceInventoryReplenishmentItem
				commerceInventoryReplenishmentItem);

	/**
	 * Deletes the commerce inventory replenishment item with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryReplenishmentItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryReplenishmentItemId the primary key of the commerce inventory replenishment item
	 * @return the commerce inventory replenishment item that was removed
	 * @throws PortalException if a commerce inventory replenishment item with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public CommerceInventoryReplenishmentItem
			deleteCommerceInventoryReplenishmentItem(
				long commerceInventoryReplenishmentItemId)
		throws PortalException;

	public void deleteCommerceInventoryReplenishmentItems(
		long commerceInventoryWarehouseId);

	public void deleteCommerceInventoryReplenishmentItems(
		long companyId, String sku, String unitOfMeasureKey);

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
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
	public CommerceInventoryReplenishmentItem
		fetchCommerceInventoryReplenishmentItem(
			long commerceInventoryReplenishmentItemId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceInventoryReplenishmentItem
		fetchCommerceInventoryReplenishmentItem(
			long companyId, String sku, String unitOfMeasureKey,
			OrderByComparator<CommerceInventoryReplenishmentItem>
				orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceInventoryReplenishmentItem
		fetchCommerceInventoryReplenishmentItemByExternalReferenceCode(
			String externalReferenceCode, long companyId);

	/**
	 * Returns the commerce inventory replenishment item with the matching UUID and company.
	 *
	 * @param uuid the commerce inventory replenishment item's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce inventory replenishment item, or <code>null</code> if a matching commerce inventory replenishment item could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceInventoryReplenishmentItem
		fetchCommerceInventoryReplenishmentItemByUuidAndCompanyId(
			String uuid, long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	/**
	 * Returns the commerce inventory replenishment item with the primary key.
	 *
	 * @param commerceInventoryReplenishmentItemId the primary key of the commerce inventory replenishment item
	 * @return the commerce inventory replenishment item
	 * @throws PortalException if a commerce inventory replenishment item with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceInventoryReplenishmentItem
			getCommerceInventoryReplenishmentItem(
				long commerceInventoryReplenishmentItemId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceInventoryReplenishmentItem
			getCommerceInventoryReplenishmentItemByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException;

	/**
	 * Returns the commerce inventory replenishment item with the matching UUID and company.
	 *
	 * @param uuid the commerce inventory replenishment item's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce inventory replenishment item
	 * @throws PortalException if a matching commerce inventory replenishment item could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceInventoryReplenishmentItem
			getCommerceInventoryReplenishmentItemByUuidAndCompanyId(
				String uuid, long companyId)
		throws PortalException;

	/**
	 * Returns a range of all the commerce inventory replenishment items.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @return the range of commerce inventory replenishment items
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceInventoryReplenishmentItem>
		getCommerceInventoryReplenishmentItems(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceInventoryReplenishmentItem>
		getCommerceInventoryReplenishmentItemsByCompanyIdSkuAndUnitOfMeasureKey(
			long companyId, String sku, String unitOfMeasureKey, int start,
			int end, boolean replacePermissionCheck);

	/**
	 * Returns the number of commerce inventory replenishment items.
	 *
	 * @return the number of commerce inventory replenishment items
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceInventoryReplenishmentItemsCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BigDecimal getCommerceInventoryReplenishmentItemsCount(
		long commerceInventoryWarehouseId, String sku, String unitOfMeasureKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Long> getCommerceInventoryWarehouseIds(
		long companyId, String sku, String unitOfMeasureKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

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

	/**
	 * Updates the commerce inventory replenishment item in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryReplenishmentItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryReplenishmentItem the commerce inventory replenishment item
	 * @return the commerce inventory replenishment item that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public CommerceInventoryReplenishmentItem
		updateCommerceInventoryReplenishmentItem(
			CommerceInventoryReplenishmentItem
				commerceInventoryReplenishmentItem);

	public CommerceInventoryReplenishmentItem
			updateCommerceInventoryReplenishmentItem(
				String externalReferenceCode,
				long commerceInventoryReplenishmentItemId,
				Date availabilityDate, BigDecimal quantity, long mvccVersion)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:-448904167