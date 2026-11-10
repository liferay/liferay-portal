/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.service;

import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
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
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
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

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for CommerceInventoryWarehouse. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Luca Pellizzon
 * @see CommerceInventoryWarehouseLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface CommerceInventoryWarehouseLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.commerce.inventory.service.impl.CommerceInventoryWarehouseLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the commerce inventory warehouse local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CommerceInventoryWarehouseLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the commerce inventory warehouse to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryWarehouseLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryWarehouse the commerce inventory warehouse
	 * @return the commerce inventory warehouse that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public CommerceInventoryWarehouse addCommerceInventoryWarehouse(
		CommerceInventoryWarehouse commerceInventoryWarehouse);

	@Indexable(type = IndexableType.REINDEX)
	public CommerceInventoryWarehouse addCommerceInventoryWarehouse(
			String externalReferenceCode, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, boolean active, String street1,
			String street2, String street3, String city, String zip,
			String commerceRegionCode, String commerceCountryCode,
			double latitude, double longitude, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Creates a new commerce inventory warehouse with the primary key. Does not add the commerce inventory warehouse to the database.
	 *
	 * @param commerceInventoryWarehouseId the primary key for the new commerce inventory warehouse
	 * @return the new commerce inventory warehouse
	 */
	@Transactional(enabled = false)
	public CommerceInventoryWarehouse createCommerceInventoryWarehouse(
		long commerceInventoryWarehouseId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the commerce inventory warehouse from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryWarehouseLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryWarehouse the commerce inventory warehouse
	 * @return the commerce inventory warehouse that was removed
	 * @throws PortalException
	 */
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommerceInventoryWarehouse deleteCommerceInventoryWarehouse(
			CommerceInventoryWarehouse commerceInventoryWarehouse)
		throws PortalException;

	/**
	 * Deletes the commerce inventory warehouse with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryWarehouseLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryWarehouseId the primary key of the commerce inventory warehouse
	 * @return the commerce inventory warehouse that was removed
	 * @throws PortalException if a commerce inventory warehouse with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public CommerceInventoryWarehouse deleteCommerceInventoryWarehouse(
			long commerceInventoryWarehouseId)
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryWarehouseModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryWarehouseModelImpl</code>.
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
	public CommerceInventoryWarehouse fetchCommerceInventoryWarehouse(
		long commerceInventoryWarehouseId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceInventoryWarehouse
		fetchCommerceInventoryWarehouseByExternalReferenceCode(
			String externalReferenceCode, long companyId);

	/**
	 * Returns the commerce inventory warehouse with the matching UUID and company.
	 *
	 * @param uuid the commerce inventory warehouse's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce inventory warehouse, or <code>null</code> if a matching commerce inventory warehouse could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceInventoryWarehouse
		fetchCommerceInventoryWarehouseByUuidAndCompanyId(
			String uuid, long companyId);

	@Indexable(type = IndexableType.REINDEX)
	public CommerceInventoryWarehouse geolocateCommerceInventoryWarehouse(
			long commerceInventoryWarehouseId, double latitude,
			double longitude)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	/**
	 * Returns the commerce inventory warehouse with the primary key.
	 *
	 * @param commerceInventoryWarehouseId the primary key of the commerce inventory warehouse
	 * @return the commerce inventory warehouse
	 * @throws PortalException if a commerce inventory warehouse with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceInventoryWarehouse getCommerceInventoryWarehouse(
			long commerceInventoryWarehouseId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceInventoryWarehouse
			getCommerceInventoryWarehouseByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException;

	/**
	 * Returns the commerce inventory warehouse with the matching UUID and company.
	 *
	 * @param uuid the commerce inventory warehouse's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce inventory warehouse
	 * @throws PortalException if a matching commerce inventory warehouse could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceInventoryWarehouse
			getCommerceInventoryWarehouseByUuidAndCompanyId(
				String uuid, long companyId)
		throws PortalException;

	/**
	 * Returns a range of all the commerce inventory warehouses.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryWarehouseModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce inventory warehouses
	 * @param end the upper bound of the range of commerce inventory warehouses (not inclusive)
	 * @return the range of commerce inventory warehouses
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceInventoryWarehouse> getCommerceInventoryWarehouses(
		int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceInventoryWarehouse> getCommerceInventoryWarehouses(
		long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceInventoryWarehouse> getCommerceInventoryWarehouses(
		long companyId, int start, int end,
		OrderByComparator<CommerceInventoryWarehouse> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceInventoryWarehouse> getCommerceInventoryWarehouses(
		long companyId, long accountEntryId, long groupId, boolean active);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceInventoryWarehouse> getCommerceInventoryWarehouses(
		long accountEntryId, long groupId, String sku);

	/**
	 * Returns the number of commerce inventory warehouses.
	 *
	 * @return the number of commerce inventory warehouses
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceInventoryWarehousesCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceInventoryWarehousesCount(
		long companyId, boolean active, String commerceCountryCode);

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

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceInventoryWarehouse> search(
			long companyId, Boolean active, String commerceCountryCode,
			String keywords, int start, int end, Sort sort)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCommerceInventoryWarehousesCount(
			long companyId, Boolean active, String commerceCountryCode,
			String keywords)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceInventoryWarehouse setActive(
			long commerceInventoryWarehouseId, boolean active)
		throws PortalException;

	/**
	 * Updates the commerce inventory warehouse in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryWarehouseLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryWarehouse the commerce inventory warehouse
	 * @return the commerce inventory warehouse that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public CommerceInventoryWarehouse updateCommerceInventoryWarehouse(
		CommerceInventoryWarehouse commerceInventoryWarehouse);

	@Indexable(type = IndexableType.REINDEX)
	public CommerceInventoryWarehouse updateCommerceInventoryWarehouse(
			long commerceInventoryWarehouseId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, boolean active, String street1,
			String street2, String street3, String city, String zip,
			String commerceRegionCode, String commerceCountryCode,
			double latitude, double longitude, long mvccVersion,
			ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceInventoryWarehouse
			updateCommerceInventoryWarehouseExternalReferenceCode(
				String externalReferenceCode, long commerceInventoryWarehouseId)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:-75231825