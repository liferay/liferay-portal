/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.service.persistence.impl;

import com.liferay.commerce.inventory.exception.DuplicateCommerceInventoryReplenishmentItemExternalReferenceCodeException;
import com.liferay.commerce.inventory.exception.NoSuchInventoryReplenishmentItemException;
import com.liferay.commerce.inventory.model.CommerceInventoryReplenishmentItem;
import com.liferay.commerce.inventory.model.CommerceInventoryReplenishmentItemTable;
import com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemImpl;
import com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl;
import com.liferay.commerce.inventory.service.persistence.CommerceInventoryReplenishmentItemPersistence;
import com.liferay.commerce.inventory.service.persistence.CommerceInventoryReplenishmentItemUtil;
import com.liferay.commerce.inventory.service.persistence.impl.constants.CommercePersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the commerce inventory replenishment item service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Luca Pellizzon
 * @generated
 */
@Component(service = CommerceInventoryReplenishmentItemPersistence.class)
public class CommerceInventoryReplenishmentItemPersistenceImpl
	extends BasePersistenceImpl
		<CommerceInventoryReplenishmentItem,
		 NoSuchInventoryReplenishmentItemException>
	implements CommerceInventoryReplenishmentItemPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceInventoryReplenishmentItemUtil</code> to access the commerce inventory replenishment item persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceInventoryReplenishmentItemImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceInventoryReplenishmentItem,
		 NoSuchInventoryReplenishmentItemException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the commerce inventory replenishment items where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory replenishment items
	 */
	@Override
	public List<CommerceInventoryReplenishmentItem> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceInventoryReplenishmentItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce inventory replenishment item in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory replenishment item
	 * @throws NoSuchInventoryReplenishmentItemException if a matching commerce inventory replenishment item could not be found
	 */
	@Override
	public CommerceInventoryReplenishmentItem findByUuid_First(
			String uuid,
			OrderByComparator<CommerceInventoryReplenishmentItem>
				orderByComparator)
		throws NoSuchInventoryReplenishmentItemException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first commerce inventory replenishment item in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory replenishment item, or <code>null</code> if a matching commerce inventory replenishment item could not be found
	 */
	@Override
	public CommerceInventoryReplenishmentItem fetchByUuid_First(
		String uuid,
		OrderByComparator<CommerceInventoryReplenishmentItem>
			orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the commerce inventory replenishment items where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of commerce inventory replenishment items where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce inventory replenishment items
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<CommerceInventoryReplenishmentItem,
		 NoSuchInventoryReplenishmentItemException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the commerce inventory replenishment items where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory replenishment items
	 */
	@Override
	public List<CommerceInventoryReplenishmentItem> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceInventoryReplenishmentItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce inventory replenishment item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory replenishment item
	 * @throws NoSuchInventoryReplenishmentItemException if a matching commerce inventory replenishment item could not be found
	 */
	@Override
	public CommerceInventoryReplenishmentItem findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommerceInventoryReplenishmentItem>
				orderByComparator)
		throws NoSuchInventoryReplenishmentItemException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first commerce inventory replenishment item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory replenishment item, or <code>null</code> if a matching commerce inventory replenishment item could not be found
	 */
	@Override
	public CommerceInventoryReplenishmentItem fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommerceInventoryReplenishmentItem>
			orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the commerce inventory replenishment items where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		_collectionPersistenceFinderByUuid_C.remove(
			finderCache, new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of commerce inventory replenishment items where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce inventory replenishment items
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<CommerceInventoryReplenishmentItem,
		 NoSuchInventoryReplenishmentItemException>
			_collectionPersistenceFinderByCommerceInventoryWarehouseId;

	/**
	 * Returns an ordered range of all the commerce inventory replenishment items where commerceInventoryWarehouseId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory replenishment items
	 */
	@Override
	public List<CommerceInventoryReplenishmentItem>
		findByCommerceInventoryWarehouseId(
			long commerceInventoryWarehouseId, int start, int end,
			OrderByComparator<CommerceInventoryReplenishmentItem>
				orderByComparator,
			boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceInventoryWarehouseId.find(
			finderCache, new Object[] {commerceInventoryWarehouseId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce inventory replenishment item in the ordered set where commerceInventoryWarehouseId = &#63;.
	 *
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory replenishment item
	 * @throws NoSuchInventoryReplenishmentItemException if a matching commerce inventory replenishment item could not be found
	 */
	@Override
	public CommerceInventoryReplenishmentItem
			findByCommerceInventoryWarehouseId_First(
				long commerceInventoryWarehouseId,
				OrderByComparator<CommerceInventoryReplenishmentItem>
					orderByComparator)
		throws NoSuchInventoryReplenishmentItemException {

		return _collectionPersistenceFinderByCommerceInventoryWarehouseId.
			findFirst(
				finderCache, new Object[] {commerceInventoryWarehouseId},
				orderByComparator);
	}

	/**
	 * Returns the first commerce inventory replenishment item in the ordered set where commerceInventoryWarehouseId = &#63;.
	 *
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory replenishment item, or <code>null</code> if a matching commerce inventory replenishment item could not be found
	 */
	@Override
	public CommerceInventoryReplenishmentItem
		fetchByCommerceInventoryWarehouseId_First(
			long commerceInventoryWarehouseId,
			OrderByComparator<CommerceInventoryReplenishmentItem>
				orderByComparator) {

		return _collectionPersistenceFinderByCommerceInventoryWarehouseId.
			fetchFirst(
				finderCache, new Object[] {commerceInventoryWarehouseId},
				orderByComparator);
	}

	/**
	 * Removes all the commerce inventory replenishment items where commerceInventoryWarehouseId = &#63; from the database.
	 *
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 */
	@Override
	public void removeByCommerceInventoryWarehouseId(
		long commerceInventoryWarehouseId) {

		_collectionPersistenceFinderByCommerceInventoryWarehouseId.remove(
			finderCache, new Object[] {commerceInventoryWarehouseId});
	}

	/**
	 * Returns the number of commerce inventory replenishment items where commerceInventoryWarehouseId = &#63;.
	 *
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @return the number of matching commerce inventory replenishment items
	 */
	@Override
	public int countByCommerceInventoryWarehouseId(
		long commerceInventoryWarehouseId) {

		return _collectionPersistenceFinderByCommerceInventoryWarehouseId.count(
			finderCache, new Object[] {commerceInventoryWarehouseId});
	}

	private CollectionPersistenceFinder
		<CommerceInventoryReplenishmentItem,
		 NoSuchInventoryReplenishmentItemException>
			_collectionPersistenceFinderByAvailabilityDate;

	/**
	 * Returns an ordered range of all the commerce inventory replenishment items where availabilityDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param availabilityDate the availability date
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory replenishment items
	 */
	@Override
	public List<CommerceInventoryReplenishmentItem> findByAvailabilityDate(
		Date availabilityDate, int start, int end,
		OrderByComparator<CommerceInventoryReplenishmentItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByAvailabilityDate.find(
			finderCache, new Object[] {availabilityDate}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce inventory replenishment item in the ordered set where availabilityDate = &#63;.
	 *
	 * @param availabilityDate the availability date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory replenishment item
	 * @throws NoSuchInventoryReplenishmentItemException if a matching commerce inventory replenishment item could not be found
	 */
	@Override
	public CommerceInventoryReplenishmentItem findByAvailabilityDate_First(
			Date availabilityDate,
			OrderByComparator<CommerceInventoryReplenishmentItem>
				orderByComparator)
		throws NoSuchInventoryReplenishmentItemException {

		return _collectionPersistenceFinderByAvailabilityDate.findFirst(
			finderCache, new Object[] {availabilityDate}, orderByComparator);
	}

	/**
	 * Returns the first commerce inventory replenishment item in the ordered set where availabilityDate = &#63;.
	 *
	 * @param availabilityDate the availability date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory replenishment item, or <code>null</code> if a matching commerce inventory replenishment item could not be found
	 */
	@Override
	public CommerceInventoryReplenishmentItem fetchByAvailabilityDate_First(
		Date availabilityDate,
		OrderByComparator<CommerceInventoryReplenishmentItem>
			orderByComparator) {

		return _collectionPersistenceFinderByAvailabilityDate.fetchFirst(
			finderCache, new Object[] {availabilityDate}, orderByComparator);
	}

	/**
	 * Removes all the commerce inventory replenishment items where availabilityDate = &#63; from the database.
	 *
	 * @param availabilityDate the availability date
	 */
	@Override
	public void removeByAvailabilityDate(Date availabilityDate) {
		_collectionPersistenceFinderByAvailabilityDate.remove(
			finderCache, new Object[] {availabilityDate});
	}

	/**
	 * Returns the number of commerce inventory replenishment items where availabilityDate = &#63;.
	 *
	 * @param availabilityDate the availability date
	 * @return the number of matching commerce inventory replenishment items
	 */
	@Override
	public int countByAvailabilityDate(Date availabilityDate) {
		return _collectionPersistenceFinderByAvailabilityDate.count(
			finderCache, new Object[] {availabilityDate});
	}

	private CollectionPersistenceFinder
		<CommerceInventoryReplenishmentItem,
		 NoSuchInventoryReplenishmentItemException>
			_collectionPersistenceFinderBySku;

	/**
	 * Returns an ordered range of all the commerce inventory replenishment items where sku = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param sku the sku
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory replenishment items
	 */
	@Override
	public List<CommerceInventoryReplenishmentItem> findBySku(
		String sku, int start, int end,
		OrderByComparator<CommerceInventoryReplenishmentItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderBySku.find(
			finderCache, new Object[] {sku}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce inventory replenishment item in the ordered set where sku = &#63;.
	 *
	 * @param sku the sku
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory replenishment item
	 * @throws NoSuchInventoryReplenishmentItemException if a matching commerce inventory replenishment item could not be found
	 */
	@Override
	public CommerceInventoryReplenishmentItem findBySku_First(
			String sku,
			OrderByComparator<CommerceInventoryReplenishmentItem>
				orderByComparator)
		throws NoSuchInventoryReplenishmentItemException {

		return _collectionPersistenceFinderBySku.findFirst(
			finderCache, new Object[] {sku}, orderByComparator);
	}

	/**
	 * Returns the first commerce inventory replenishment item in the ordered set where sku = &#63;.
	 *
	 * @param sku the sku
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory replenishment item, or <code>null</code> if a matching commerce inventory replenishment item could not be found
	 */
	@Override
	public CommerceInventoryReplenishmentItem fetchBySku_First(
		String sku,
		OrderByComparator<CommerceInventoryReplenishmentItem>
			orderByComparator) {

		return _collectionPersistenceFinderBySku.fetchFirst(
			finderCache, new Object[] {sku}, orderByComparator);
	}

	/**
	 * Removes all the commerce inventory replenishment items where sku = &#63; from the database.
	 *
	 * @param sku the sku
	 */
	@Override
	public void removeBySku(String sku) {
		_collectionPersistenceFinderBySku.remove(
			finderCache, new Object[] {sku});
	}

	/**
	 * Returns the number of commerce inventory replenishment items where sku = &#63;.
	 *
	 * @param sku the sku
	 * @return the number of matching commerce inventory replenishment items
	 */
	@Override
	public int countBySku(String sku) {
		return _collectionPersistenceFinderBySku.count(
			finderCache, new Object[] {sku});
	}

	private CollectionPersistenceFinder
		<CommerceInventoryReplenishmentItem,
		 NoSuchInventoryReplenishmentItemException>
			_collectionPersistenceFinderByC_S_U;

	/**
	 * Returns an ordered range of all the commerce inventory replenishment items where companyId = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory replenishment items
	 */
	@Override
	public List<CommerceInventoryReplenishmentItem> findByC_S_U(
		long companyId, String sku, String unitOfMeasureKey, int start, int end,
		OrderByComparator<CommerceInventoryReplenishmentItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_S_U.find(
			finderCache, new Object[] {companyId, sku, unitOfMeasureKey}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce inventory replenishment item in the ordered set where companyId = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory replenishment item
	 * @throws NoSuchInventoryReplenishmentItemException if a matching commerce inventory replenishment item could not be found
	 */
	@Override
	public CommerceInventoryReplenishmentItem findByC_S_U_First(
			long companyId, String sku, String unitOfMeasureKey,
			OrderByComparator<CommerceInventoryReplenishmentItem>
				orderByComparator)
		throws NoSuchInventoryReplenishmentItemException {

		return _collectionPersistenceFinderByC_S_U.findFirst(
			finderCache, new Object[] {companyId, sku, unitOfMeasureKey},
			orderByComparator);
	}

	/**
	 * Returns the first commerce inventory replenishment item in the ordered set where companyId = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory replenishment item, or <code>null</code> if a matching commerce inventory replenishment item could not be found
	 */
	@Override
	public CommerceInventoryReplenishmentItem fetchByC_S_U_First(
		long companyId, String sku, String unitOfMeasureKey,
		OrderByComparator<CommerceInventoryReplenishmentItem>
			orderByComparator) {

		return _collectionPersistenceFinderByC_S_U.fetchFirst(
			finderCache, new Object[] {companyId, sku, unitOfMeasureKey},
			orderByComparator);
	}

	/**
	 * Removes all the commerce inventory replenishment items where companyId = &#63; and sku = &#63; and unitOfMeasureKey = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 */
	@Override
	public void removeByC_S_U(
		long companyId, String sku, String unitOfMeasureKey) {

		_collectionPersistenceFinderByC_S_U.remove(
			finderCache, new Object[] {companyId, sku, unitOfMeasureKey});
	}

	/**
	 * Returns the number of commerce inventory replenishment items where companyId = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @return the number of matching commerce inventory replenishment items
	 */
	@Override
	public int countByC_S_U(
		long companyId, String sku, String unitOfMeasureKey) {

		return _collectionPersistenceFinderByC_S_U.count(
			finderCache, new Object[] {companyId, sku, unitOfMeasureKey});
	}

	private CollectionPersistenceFinder
		<CommerceInventoryReplenishmentItem,
		 NoSuchInventoryReplenishmentItemException>
			_collectionPersistenceFinderByAD_S_U;

	/**
	 * Returns an ordered range of all the commerce inventory replenishment items where availabilityDate = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param availabilityDate the availability date
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory replenishment items
	 */
	@Override
	public List<CommerceInventoryReplenishmentItem> findByAD_S_U(
		Date availabilityDate, String sku, String unitOfMeasureKey, int start,
		int end,
		OrderByComparator<CommerceInventoryReplenishmentItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByAD_S_U.find(
			finderCache, new Object[] {availabilityDate, sku, unitOfMeasureKey},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce inventory replenishment item in the ordered set where availabilityDate = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param availabilityDate the availability date
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory replenishment item
	 * @throws NoSuchInventoryReplenishmentItemException if a matching commerce inventory replenishment item could not be found
	 */
	@Override
	public CommerceInventoryReplenishmentItem findByAD_S_U_First(
			Date availabilityDate, String sku, String unitOfMeasureKey,
			OrderByComparator<CommerceInventoryReplenishmentItem>
				orderByComparator)
		throws NoSuchInventoryReplenishmentItemException {

		return _collectionPersistenceFinderByAD_S_U.findFirst(
			finderCache, new Object[] {availabilityDate, sku, unitOfMeasureKey},
			orderByComparator);
	}

	/**
	 * Returns the first commerce inventory replenishment item in the ordered set where availabilityDate = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param availabilityDate the availability date
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory replenishment item, or <code>null</code> if a matching commerce inventory replenishment item could not be found
	 */
	@Override
	public CommerceInventoryReplenishmentItem fetchByAD_S_U_First(
		Date availabilityDate, String sku, String unitOfMeasureKey,
		OrderByComparator<CommerceInventoryReplenishmentItem>
			orderByComparator) {

		return _collectionPersistenceFinderByAD_S_U.fetchFirst(
			finderCache, new Object[] {availabilityDate, sku, unitOfMeasureKey},
			orderByComparator);
	}

	/**
	 * Removes all the commerce inventory replenishment items where availabilityDate = &#63; and sku = &#63; and unitOfMeasureKey = &#63; from the database.
	 *
	 * @param availabilityDate the availability date
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 */
	@Override
	public void removeByAD_S_U(
		Date availabilityDate, String sku, String unitOfMeasureKey) {

		_collectionPersistenceFinderByAD_S_U.remove(
			finderCache,
			new Object[] {availabilityDate, sku, unitOfMeasureKey});
	}

	/**
	 * Returns the number of commerce inventory replenishment items where availabilityDate = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param availabilityDate the availability date
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @return the number of matching commerce inventory replenishment items
	 */
	@Override
	public int countByAD_S_U(
		Date availabilityDate, String sku, String unitOfMeasureKey) {

		return _collectionPersistenceFinderByAD_S_U.count(
			finderCache,
			new Object[] {availabilityDate, sku, unitOfMeasureKey});
	}

	private UniquePersistenceFinder
		<CommerceInventoryReplenishmentItem,
		 NoSuchInventoryReplenishmentItemException>
			_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the commerce inventory replenishment item where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchInventoryReplenishmentItemException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce inventory replenishment item
	 * @throws NoSuchInventoryReplenishmentItemException if a matching commerce inventory replenishment item could not be found
	 */
	@Override
	public CommerceInventoryReplenishmentItem findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchInventoryReplenishmentItemException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the commerce inventory replenishment item where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce inventory replenishment item, or <code>null</code> if a matching commerce inventory replenishment item could not be found
	 */
	@Override
	public CommerceInventoryReplenishmentItem fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the commerce inventory replenishment item where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the commerce inventory replenishment item that was removed
	 */
	@Override
	public CommerceInventoryReplenishmentItem removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchInventoryReplenishmentItemException {

		CommerceInventoryReplenishmentItem commerceInventoryReplenishmentItem =
			findByERC_C(externalReferenceCode, companyId);

		return remove(commerceInventoryReplenishmentItem);
	}

	/**
	 * Returns the number of commerce inventory replenishment items where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching commerce inventory replenishment items
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public CommerceInventoryReplenishmentItemPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put(
			"commerceInventoryReplenishmentItemId", "CIReplenishmentItemId");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceInventoryReplenishmentItem.class);

		setModelImplClass(CommerceInventoryReplenishmentItemImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceInventoryReplenishmentItemTable.INSTANCE);
	}

	/**
	 * Creates a new commerce inventory replenishment item with the primary key. Does not add the commerce inventory replenishment item to the database.
	 *
	 * @param commerceInventoryReplenishmentItemId the primary key for the new commerce inventory replenishment item
	 * @return the new commerce inventory replenishment item
	 */
	@Override
	public CommerceInventoryReplenishmentItem create(
		long commerceInventoryReplenishmentItemId) {

		CommerceInventoryReplenishmentItem commerceInventoryReplenishmentItem =
			new CommerceInventoryReplenishmentItemImpl();

		commerceInventoryReplenishmentItem.setNew(true);
		commerceInventoryReplenishmentItem.setPrimaryKey(
			commerceInventoryReplenishmentItemId);

		String uuid = PortalUUIDUtil.generate();

		commerceInventoryReplenishmentItem.setUuid(uuid);

		commerceInventoryReplenishmentItem.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return commerceInventoryReplenishmentItem;
	}

	/**
	 * Removes the commerce inventory replenishment item with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceInventoryReplenishmentItemId the primary key of the commerce inventory replenishment item
	 * @return the commerce inventory replenishment item that was removed
	 * @throws NoSuchInventoryReplenishmentItemException if a commerce inventory replenishment item with the primary key could not be found
	 */
	@Override
	public CommerceInventoryReplenishmentItem remove(
			long commerceInventoryReplenishmentItemId)
		throws NoSuchInventoryReplenishmentItemException {

		return remove((Serializable)commerceInventoryReplenishmentItemId);
	}

	@Override
	protected CommerceInventoryReplenishmentItem removeImpl(
		CommerceInventoryReplenishmentItem commerceInventoryReplenishmentItem) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceInventoryReplenishmentItem)) {
				commerceInventoryReplenishmentItem =
					(CommerceInventoryReplenishmentItem)session.get(
						CommerceInventoryReplenishmentItemImpl.class,
						commerceInventoryReplenishmentItem.getPrimaryKeyObj());
			}

			if (commerceInventoryReplenishmentItem != null) {
				session.delete(commerceInventoryReplenishmentItem);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceInventoryReplenishmentItem != null) {
			clearCache(commerceInventoryReplenishmentItem);
		}

		return commerceInventoryReplenishmentItem;
	}

	@Override
	public CommerceInventoryReplenishmentItem updateImpl(
		CommerceInventoryReplenishmentItem commerceInventoryReplenishmentItem) {

		boolean isNew = commerceInventoryReplenishmentItem.isNew();

		if (!(commerceInventoryReplenishmentItem instanceof
				CommerceInventoryReplenishmentItemModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					commerceInventoryReplenishmentItem.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceInventoryReplenishmentItem);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceInventoryReplenishmentItem proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceInventoryReplenishmentItem implementation " +
					commerceInventoryReplenishmentItem.getClass());
		}

		CommerceInventoryReplenishmentItemModelImpl
			commerceInventoryReplenishmentItemModelImpl =
				(CommerceInventoryReplenishmentItemModelImpl)
					commerceInventoryReplenishmentItem;

		if (Validator.isNull(commerceInventoryReplenishmentItem.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			commerceInventoryReplenishmentItem.setUuid(uuid);
		}

		if (Validator.isNull(
				commerceInventoryReplenishmentItem.
					getExternalReferenceCode())) {

			commerceInventoryReplenishmentItem.setExternalReferenceCode(
				commerceInventoryReplenishmentItem.getUuid());
		}
		else {
			if (!Objects.equals(
					commerceInventoryReplenishmentItemModelImpl.
						getColumnOriginalValue("externalReferenceCode"),
					commerceInventoryReplenishmentItem.
						getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId =
						commerceInventoryReplenishmentItem.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK =
							commerceInventoryReplenishmentItem.getPrimaryKey();
					}

					try {
						commerceInventoryReplenishmentItem.
							setExternalReferenceCode(
								SanitizerUtil.sanitize(
									companyId, groupId, userId,
									CommerceInventoryReplenishmentItem.class.
										getName(),
									classPK, ContentTypes.TEXT_HTML,
									Sanitizer.MODE_ALL,
									commerceInventoryReplenishmentItem.
										getExternalReferenceCode(),
									null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			CommerceInventoryReplenishmentItem
				ercCommerceInventoryReplenishmentItem = fetchByERC_C(
					commerceInventoryReplenishmentItem.
						getExternalReferenceCode(),
					commerceInventoryReplenishmentItem.getCompanyId());

			if (isNew) {
				if (ercCommerceInventoryReplenishmentItem != null) {
					throw new DuplicateCommerceInventoryReplenishmentItemExternalReferenceCodeException(
						"Duplicate commerce inventory replenishment item with external reference code " +
							commerceInventoryReplenishmentItem.
								getExternalReferenceCode() + " and company " +
									commerceInventoryReplenishmentItem.
										getCompanyId());
				}
			}
			else {
				if ((ercCommerceInventoryReplenishmentItem != null) &&
					(commerceInventoryReplenishmentItem.
						getCommerceInventoryReplenishmentItemId() !=
							ercCommerceInventoryReplenishmentItem.
								getCommerceInventoryReplenishmentItemId())) {

					throw new DuplicateCommerceInventoryReplenishmentItemExternalReferenceCodeException(
						"Duplicate commerce inventory replenishment item with external reference code " +
							commerceInventoryReplenishmentItem.
								getExternalReferenceCode() + " and company " +
									commerceInventoryReplenishmentItem.
										getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew &&
			(commerceInventoryReplenishmentItem.getCreateDate() == null)) {

			if (serviceContext == null) {
				commerceInventoryReplenishmentItem.setCreateDate(date);
			}
			else {
				commerceInventoryReplenishmentItem.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceInventoryReplenishmentItemModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceInventoryReplenishmentItem.setModifiedDate(date);
			}
			else {
				commerceInventoryReplenishmentItem.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceInventoryReplenishmentItem);
			}
			else {
				commerceInventoryReplenishmentItem =
					(CommerceInventoryReplenishmentItem)session.merge(
						commerceInventoryReplenishmentItem);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceInventoryReplenishmentItem, false);

		if (isNew) {
			commerceInventoryReplenishmentItem.setNew(false);
		}

		commerceInventoryReplenishmentItem.resetOriginalValues();

		return commerceInventoryReplenishmentItem;
	}

	/**
	 * Returns the commerce inventory replenishment item with the primary key or throws a <code>NoSuchInventoryReplenishmentItemException</code> if it could not be found.
	 *
	 * @param commerceInventoryReplenishmentItemId the primary key of the commerce inventory replenishment item
	 * @return the commerce inventory replenishment item
	 * @throws NoSuchInventoryReplenishmentItemException if a commerce inventory replenishment item with the primary key could not be found
	 */
	@Override
	public CommerceInventoryReplenishmentItem findByPrimaryKey(
			long commerceInventoryReplenishmentItemId)
		throws NoSuchInventoryReplenishmentItemException {

		return findByPrimaryKey(
			(Serializable)commerceInventoryReplenishmentItemId);
	}

	/**
	 * Returns the commerce inventory replenishment item with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceInventoryReplenishmentItemId the primary key of the commerce inventory replenishment item
	 * @return the commerce inventory replenishment item, or <code>null</code> if a commerce inventory replenishment item with the primary key could not be found
	 */
	@Override
	public CommerceInventoryReplenishmentItem fetchByPrimaryKey(
		long commerceInventoryReplenishmentItemId) {

		return fetchByPrimaryKey(
			(Serializable)commerceInventoryReplenishmentItemId);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "CIReplenishmentItemId";
	}

	@Override
	protected String getPKFieldName() {
		return "commerceInventoryReplenishmentItemId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEINVENTORYREPLENISHMENTITEM;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceInventoryReplenishmentItemModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce inventory replenishment item persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByUuid = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
				new String[] {
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"uuid_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, false, null),
			_SQL_SELECT_COMMERCEINVENTORYREPLENISHMENTITEM_WHERE,
			_SQL_COUNT_COMMERCEINVENTORYREPLENISHMENTITEM_WHERE,
			CommerceInventoryReplenishmentItemModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"commerceInventoryReplenishmentItem.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceInventoryReplenishmentItem::getUuid));

		_collectionPersistenceFinderByUuid_C =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid_C",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"uuid_", "companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, false, null),
				_SQL_SELECT_COMMERCEINVENTORYREPLENISHMENTITEM_WHERE,
				_SQL_COUNT_COMMERCEINVENTORYREPLENISHMENTITEM_WHERE,
				CommerceInventoryReplenishmentItemModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceInventoryReplenishmentItem.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CommerceInventoryReplenishmentItem::getUuid),
				new FinderColumn<>(
					"commerceInventoryReplenishmentItem.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceInventoryReplenishmentItem::getCompanyId));

		_collectionPersistenceFinderByCommerceInventoryWarehouseId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommerceInventoryWarehouseId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commerceInventoryWarehouseId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommerceInventoryWarehouseId",
					new String[] {Long.class.getName()},
					new String[] {"commerceInventoryWarehouseId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommerceInventoryWarehouseId",
					new String[] {Long.class.getName()},
					new String[] {"commerceInventoryWarehouseId"}, false),
				_SQL_SELECT_COMMERCEINVENTORYREPLENISHMENTITEM_WHERE,
				_SQL_COUNT_COMMERCEINVENTORYREPLENISHMENTITEM_WHERE,
				CommerceInventoryReplenishmentItemModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceInventoryReplenishmentItem.",
					"commerceInventoryWarehouseId", FinderColumn.Type.LONG, "=",
					true, true,
					CommerceInventoryReplenishmentItem::
						getCommerceInventoryWarehouseId));

		_collectionPersistenceFinderByAvailabilityDate =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByAvailabilityDate",
					new String[] {
						Date.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"availabilityDate"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByAvailabilityDate",
					new String[] {Date.class.getName()},
					new String[] {"availabilityDate"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByAvailabilityDate",
					new String[] {Date.class.getName()},
					new String[] {"availabilityDate"}, false),
				_SQL_SELECT_COMMERCEINVENTORYREPLENISHMENTITEM_WHERE,
				_SQL_COUNT_COMMERCEINVENTORYREPLENISHMENTITEM_WHERE,
				CommerceInventoryReplenishmentItemModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceInventoryReplenishmentItem.", "availabilityDate",
					FinderColumn.Type.DATE, "=", true, true,
					CommerceInventoryReplenishmentItem::getAvailabilityDate));

		_collectionPersistenceFinderBySku = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findBySku",
				new String[] {
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"sku"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findBySku",
				new String[] {String.class.getName()}, new String[] {"sku"}, 0,
				1, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countBySku",
				new String[] {String.class.getName()}, new String[] {"sku"}, 0,
				1, false, null),
			_SQL_SELECT_COMMERCEINVENTORYREPLENISHMENTITEM_WHERE,
			_SQL_COUNT_COMMERCEINVENTORYREPLENISHMENTITEM_WHERE,
			CommerceInventoryReplenishmentItemModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"commerceInventoryReplenishmentItem.", "sku",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceInventoryReplenishmentItem::getSku));

		_collectionPersistenceFinderByC_S_U = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_S_U",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"companyId", "sku", "unitOfMeasureKey"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_S_U",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {"companyId", "sku", "unitOfMeasureKey"}, 0, 6,
				true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_S_U",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {"companyId", "sku", "unitOfMeasureKey"}, 0, 6,
				false, null),
			_SQL_SELECT_COMMERCEINVENTORYREPLENISHMENTITEM_WHERE,
			_SQL_COUNT_COMMERCEINVENTORYREPLENISHMENTITEM_WHERE,
			CommerceInventoryReplenishmentItemModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"commerceInventoryReplenishmentItem.", "companyId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceInventoryReplenishmentItem::getCompanyId),
			new FinderColumn<>(
				"commerceInventoryReplenishmentItem.", "sku",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceInventoryReplenishmentItem::getSku),
			new FinderColumn<>(
				"commerceInventoryReplenishmentItem.", "unitOfMeasureKey",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceInventoryReplenishmentItem::getUnitOfMeasureKey));

		_collectionPersistenceFinderByAD_S_U =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByAD_S_U",
					new String[] {
						Date.class.getName(), String.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"availabilityDate", "sku", "unitOfMeasureKey"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByAD_S_U",
					new String[] {
						Date.class.getName(), String.class.getName(),
						String.class.getName()
					},
					new String[] {
						"availabilityDate", "sku", "unitOfMeasureKey"
					},
					0, 6, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByAD_S_U",
					new String[] {
						Date.class.getName(), String.class.getName(),
						String.class.getName()
					},
					new String[] {
						"availabilityDate", "sku", "unitOfMeasureKey"
					},
					0, 6, false, null),
				_SQL_SELECT_COMMERCEINVENTORYREPLENISHMENTITEM_WHERE,
				_SQL_COUNT_COMMERCEINVENTORYREPLENISHMENTITEM_WHERE,
				CommerceInventoryReplenishmentItemModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceInventoryReplenishmentItem.", "availabilityDate",
					FinderColumn.Type.DATE, "=", true, true,
					CommerceInventoryReplenishmentItem::getAvailabilityDate),
				new FinderColumn<>(
					"commerceInventoryReplenishmentItem.", "sku",
					FinderColumn.Type.STRING, "=", true, true,
					CommerceInventoryReplenishmentItem::getSku),
				new FinderColumn<>(
					"commerceInventoryReplenishmentItem.", "unitOfMeasureKey",
					FinderColumn.Type.STRING, "=", true, true,
					CommerceInventoryReplenishmentItem::getUnitOfMeasureKey));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(
					CommerceInventoryReplenishmentItem::
						getExternalReferenceCode),
				CommerceInventoryReplenishmentItem::getCompanyId),
			_SQL_SELECT_COMMERCEINVENTORYREPLENISHMENTITEM_WHERE, "",
			new FinderColumn<>(
				"commerceInventoryReplenishmentItem.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceInventoryReplenishmentItem::getExternalReferenceCode),
			new FinderColumn<>(
				"commerceInventoryReplenishmentItem.", "companyId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceInventoryReplenishmentItem::getCompanyId));

		CommerceInventoryReplenishmentItemUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceInventoryReplenishmentItemUtil.setPersistence(null);

		entityCache.removeCache(
			CommerceInventoryReplenishmentItemImpl.class.getName());
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		CommerceInventoryReplenishmentItemModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCEINVENTORYREPLENISHMENTITEM =
		"SELECT commerceInventoryReplenishmentItem FROM CommerceInventoryReplenishmentItem commerceInventoryReplenishmentItem";

	private static final String
		_SQL_SELECT_COMMERCEINVENTORYREPLENISHMENTITEM_WHERE =
			"SELECT commerceInventoryReplenishmentItem FROM CommerceInventoryReplenishmentItem commerceInventoryReplenishmentItem WHERE ";

	private static final String
		_SQL_COUNT_COMMERCEINVENTORYREPLENISHMENTITEM_WHERE =
			"SELECT COUNT(commerceInventoryReplenishmentItem) FROM CommerceInventoryReplenishmentItem commerceInventoryReplenishmentItem WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommerceInventoryReplenishmentItem exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceInventoryReplenishmentItemPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "commerceInventoryReplenishmentItemId"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-704049108