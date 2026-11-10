/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.service;

import com.liferay.commerce.inventory.model.CIWarehouseItem;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseItem;
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
 * Provides the local service interface for CommerceInventoryWarehouseItem. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Luca Pellizzon
 * @see CommerceInventoryWarehouseItemLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface CommerceInventoryWarehouseItemLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.commerce.inventory.service.impl.CommerceInventoryWarehouseItemLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the commerce inventory warehouse item local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CommerceInventoryWarehouseItemLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the commerce inventory warehouse item to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryWarehouseItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryWarehouseItem the commerce inventory warehouse item
	 * @return the commerce inventory warehouse item that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public CommerceInventoryWarehouseItem addCommerceInventoryWarehouseItem(
		CommerceInventoryWarehouseItem commerceInventoryWarehouseItem);

	public CommerceInventoryWarehouseItem addCommerceInventoryWarehouseItem(
			String externalReferenceCode, long userId,
			long commerceInventoryWarehouseId, BigDecimal quantity,
			BigDecimal reservedQuantity, String sku, String unitOfMeasureKey)
		throws PortalException;

	public CommerceInventoryWarehouseItem
			addOrUpdateCommerceInventoryWarehouseItem(
				String externalReferenceCode, long companyId, long userId,
				long commerceInventoryWarehouseId, BigDecimal quantity,
				BigDecimal reservedQuantity, String sku,
				String unitOfMeasureKey)
		throws PortalException;

	public int countItemsByCompanyId(
		long companyId, String sku, boolean replacePermissionCheck);

	/**
	 * Creates a new commerce inventory warehouse item with the primary key. Does not add the commerce inventory warehouse item to the database.
	 *
	 * @param commerceInventoryWarehouseItemId the primary key for the new commerce inventory warehouse item
	 * @return the new commerce inventory warehouse item
	 */
	@Transactional(enabled = false)
	public CommerceInventoryWarehouseItem createCommerceInventoryWarehouseItem(
		long commerceInventoryWarehouseItemId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the commerce inventory warehouse item from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryWarehouseItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryWarehouseItem the commerce inventory warehouse item
	 * @return the commerce inventory warehouse item that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public CommerceInventoryWarehouseItem deleteCommerceInventoryWarehouseItem(
		CommerceInventoryWarehouseItem commerceInventoryWarehouseItem);

	/**
	 * Deletes the commerce inventory warehouse item with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryWarehouseItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryWarehouseItemId the primary key of the commerce inventory warehouse item
	 * @return the commerce inventory warehouse item that was removed
	 * @throws PortalException if a commerce inventory warehouse item with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public CommerceInventoryWarehouseItem deleteCommerceInventoryWarehouseItem(
			long commerceInventoryWarehouseItemId)
		throws PortalException;

	public void deleteCommerceInventoryWarehouseItems(
		long commerceInventoryWarehouseId);

	public void deleteCommerceInventoryWarehouseItems(
		long companyId, String sku, String unitOfMeasureKey);

	public void deleteCommerceInventoryWarehouseItemsByCompanyId(
		long companyId);

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryWarehouseItemModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryWarehouseItemModelImpl</code>.
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
	public CommerceInventoryWarehouseItem fetchCommerceInventoryWarehouseItem(
		long commerceInventoryWarehouseItemId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceInventoryWarehouseItem fetchCommerceInventoryWarehouseItem(
		long commerceInventoryWarehouseId, String sku, String unitOfMeasureKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceInventoryWarehouseItem
		fetchCommerceInventoryWarehouseItemByExternalReferenceCode(
			String externalReferenceCode, long companyId);

	/**
	 * Returns the commerce inventory warehouse item with the matching UUID and company.
	 *
	 * @param uuid the commerce inventory warehouse item's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce inventory warehouse item, or <code>null</code> if a matching commerce inventory warehouse item could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceInventoryWarehouseItem
		fetchCommerceInventoryWarehouseItemByUuidAndCompanyId(
			String uuid, long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Long> getCommerceInventoryWarehouseIds(
		long companyId, String sku, String unitOfMeasureKey);

	/**
	 * Returns the commerce inventory warehouse item with the primary key.
	 *
	 * @param commerceInventoryWarehouseItemId the primary key of the commerce inventory warehouse item
	 * @return the commerce inventory warehouse item
	 * @throws PortalException if a commerce inventory warehouse item with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceInventoryWarehouseItem getCommerceInventoryWarehouseItem(
			long commerceInventoryWarehouseItemId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceInventoryWarehouseItem
			getCommerceInventoryWarehouseItemByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException;

	/**
	 * Returns the commerce inventory warehouse item with the matching UUID and company.
	 *
	 * @param uuid the commerce inventory warehouse item's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce inventory warehouse item
	 * @throws PortalException if a matching commerce inventory warehouse item could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceInventoryWarehouseItem
			getCommerceInventoryWarehouseItemByUuidAndCompanyId(
				String uuid, long companyId)
		throws PortalException;

	/**
	 * Returns a range of all the commerce inventory warehouse items.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryWarehouseItemModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce inventory warehouse items
	 * @param end the upper bound of the range of commerce inventory warehouse items (not inclusive)
	 * @return the range of commerce inventory warehouse items
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceInventoryWarehouseItem>
		getCommerceInventoryWarehouseItems(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceInventoryWarehouseItem>
		getCommerceInventoryWarehouseItems(
			long commerceInventoryWarehouseId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceInventoryWarehouseItem>
		getCommerceInventoryWarehouseItemsByCompanyIdSkuAndUnitOfMeasureKey(
			long companyId, String sku, String unitOfMeasureKey, int start,
			int end, boolean replacePermissionCheck);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceInventoryWarehouseItem>
		getCommerceInventoryWarehouseItemsByModifiedDate(
			long companyId, Date startDate, Date endDate, int start, int end);

	/**
	 * Returns the number of commerce inventory warehouse items.
	 *
	 * @return the number of commerce inventory warehouse items
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceInventoryWarehouseItemsCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceInventoryWarehouseItemsCount(
		long companyId, long accountEntryId, long groupId, String sku,
		String unitOfMeasureKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceInventoryWarehouseItemsCount(
		long companyId, String sku, String unitOfMeasureKey,
		boolean replacePermissionCheck);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceInventoryWarehouseItemsCountByModifiedDate(
		long companyId, Date startDate, Date endDate);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CIWarehouseItem> getItemsByCompanyId(
		long companyId, String sku, int start, int end,
		boolean replacePermissionCheck);

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
	public BigDecimal getStockQuantity(
		long companyId, long accountEntryId, long groupId, String sku,
		String unitOfMeasureKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BigDecimal getStockQuantity(
		long companyId, String sku, String unitOfMeasureKey);

	public CommerceInventoryWarehouseItem
			increaseCommerceInventoryWarehouseItemQuantity(
				long userId, long commerceInventoryWarehouseItemId,
				BigDecimal quantity)
		throws PortalException;

	@Transactional(
		propagation = Propagation.REQUIRED, readOnly = false,
		rollbackFor = Exception.class
	)
	public void moveQuantitiesBetweenWarehouses(
			long userId, long fromCommerceInventoryWarehouseId,
			long toCommerceInventoryWarehouseId, BigDecimal quantity,
			String sku, String unitOfMeasureKey)
		throws PortalException;

	/**
	 * Updates the commerce inventory warehouse item in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryWarehouseItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryWarehouseItem the commerce inventory warehouse item
	 * @return the commerce inventory warehouse item that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public CommerceInventoryWarehouseItem updateCommerceInventoryWarehouseItem(
		CommerceInventoryWarehouseItem commerceInventoryWarehouseItem);

	public CommerceInventoryWarehouseItem updateCommerceInventoryWarehouseItem(
			long userId, long commerceInventoryWarehouseItemId,
			BigDecimal quantity, BigDecimal reservedQuantity,
			String unitOfMeasureKey, long mvccVersion)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:1020683036