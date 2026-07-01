/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence.impl;

import com.liferay.commerce.exception.DuplicateCommerceOrderItemExternalReferenceCodeException;
import com.liferay.commerce.exception.NoSuchOrderItemException;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceOrderItemTable;
import com.liferay.commerce.model.impl.CommerceOrderItemImpl;
import com.liferay.commerce.model.impl.CommerceOrderItemModelImpl;
import com.liferay.commerce.service.persistence.CommerceOrderItemPersistence;
import com.liferay.commerce.service.persistence.CommerceOrderItemUtil;
import com.liferay.commerce.service.persistence.impl.constants.CommercePersistenceConstants;
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
 * The persistence implementation for the commerce order item service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(service = CommerceOrderItemPersistence.class)
public class CommerceOrderItemPersistenceImpl
	extends BasePersistenceImpl<CommerceOrderItem, NoSuchOrderItemException>
	implements CommerceOrderItemPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceOrderItemUtil</code> to access the commerce order item persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceOrderItemImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceOrderItem, NoSuchOrderItemException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the commerce order items where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order items
	 */
	@Override
	public List<CommerceOrderItem> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceOrderItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce order item in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	@Override
	public CommerceOrderItem findByUuid_First(
			String uuid, OrderByComparator<CommerceOrderItem> orderByComparator)
		throws NoSuchOrderItemException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first commerce order item in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	@Override
	public CommerceOrderItem fetchByUuid_First(
		String uuid, OrderByComparator<CommerceOrderItem> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the commerce order items where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of commerce order items where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce order items
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<CommerceOrderItem, NoSuchOrderItemException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the commerce order item where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchOrderItemException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	@Override
	public CommerceOrderItem findByUUID_G(String uuid, long groupId)
		throws NoSuchOrderItemException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the commerce order item where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	@Override
	public CommerceOrderItem fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the commerce order item where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the commerce order item that was removed
	 */
	@Override
	public CommerceOrderItem removeByUUID_G(String uuid, long groupId)
		throws NoSuchOrderItemException {

		CommerceOrderItem commerceOrderItem = findByUUID_G(uuid, groupId);

		return remove(commerceOrderItem);
	}

	/**
	 * Returns the number of commerce order items where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching commerce order items
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<CommerceOrderItem, NoSuchOrderItemException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the commerce order items where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order items
	 */
	@Override
	public List<CommerceOrderItem> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceOrderItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	@Override
	public CommerceOrderItem findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommerceOrderItem> orderByComparator)
		throws NoSuchOrderItemException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first commerce order item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	@Override
	public CommerceOrderItem fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the commerce order items where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of commerce order items where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce order items
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<CommerceOrderItem, NoSuchOrderItemException>
			_collectionPersistenceFinderByCommerceInventoryBookedQuantityId;

	/**
	 * Returns an ordered range of all the commerce order items where commerceInventoryBookedQuantityId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceInventoryBookedQuantityId the commerce inventory booked quantity ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order items
	 */
	@Override
	public List<CommerceOrderItem> findByCommerceInventoryBookedQuantityId(
		long commerceInventoryBookedQuantityId, int start, int end,
		OrderByComparator<CommerceOrderItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceInventoryBookedQuantityId.
			find(
				finderCache, new Object[] {commerceInventoryBookedQuantityId},
				start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order item in the ordered set where commerceInventoryBookedQuantityId = &#63;.
	 *
	 * @param commerceInventoryBookedQuantityId the commerce inventory booked quantity ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	@Override
	public CommerceOrderItem findByCommerceInventoryBookedQuantityId_First(
			long commerceInventoryBookedQuantityId,
			OrderByComparator<CommerceOrderItem> orderByComparator)
		throws NoSuchOrderItemException {

		return _collectionPersistenceFinderByCommerceInventoryBookedQuantityId.
			findFirst(
				finderCache, new Object[] {commerceInventoryBookedQuantityId},
				orderByComparator);
	}

	/**
	 * Returns the first commerce order item in the ordered set where commerceInventoryBookedQuantityId = &#63;.
	 *
	 * @param commerceInventoryBookedQuantityId the commerce inventory booked quantity ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	@Override
	public CommerceOrderItem fetchByCommerceInventoryBookedQuantityId_First(
		long commerceInventoryBookedQuantityId,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return _collectionPersistenceFinderByCommerceInventoryBookedQuantityId.
			fetchFirst(
				finderCache, new Object[] {commerceInventoryBookedQuantityId},
				orderByComparator);
	}

	/**
	 * Removes all the commerce order items where commerceInventoryBookedQuantityId = &#63; from the database.
	 *
	 * @param commerceInventoryBookedQuantityId the commerce inventory booked quantity ID
	 */
	@Override
	public void removeByCommerceInventoryBookedQuantityId(
		long commerceInventoryBookedQuantityId) {

		_collectionPersistenceFinderByCommerceInventoryBookedQuantityId.remove(
			finderCache, new Object[] {commerceInventoryBookedQuantityId});
	}

	/**
	 * Returns the number of commerce order items where commerceInventoryBookedQuantityId = &#63;.
	 *
	 * @param commerceInventoryBookedQuantityId the commerce inventory booked quantity ID
	 * @return the number of matching commerce order items
	 */
	@Override
	public int countByCommerceInventoryBookedQuantityId(
		long commerceInventoryBookedQuantityId) {

		return _collectionPersistenceFinderByCommerceInventoryBookedQuantityId.
			count(
				finderCache, new Object[] {commerceInventoryBookedQuantityId});
	}

	private CollectionPersistenceFinder
		<CommerceOrderItem, NoSuchOrderItemException>
			_collectionPersistenceFinderByCommerceOrderId;

	/**
	 * Returns an ordered range of all the commerce order items where commerceOrderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order items
	 */
	@Override
	public List<CommerceOrderItem> findByCommerceOrderId(
		long commerceOrderId, int start, int end,
		OrderByComparator<CommerceOrderItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceOrderId.find(
			finderCache, new Object[] {commerceOrderId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order item in the ordered set where commerceOrderId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	@Override
	public CommerceOrderItem findByCommerceOrderId_First(
			long commerceOrderId,
			OrderByComparator<CommerceOrderItem> orderByComparator)
		throws NoSuchOrderItemException {

		return _collectionPersistenceFinderByCommerceOrderId.findFirst(
			finderCache, new Object[] {commerceOrderId}, orderByComparator);
	}

	/**
	 * Returns the first commerce order item in the ordered set where commerceOrderId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	@Override
	public CommerceOrderItem fetchByCommerceOrderId_First(
		long commerceOrderId,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return _collectionPersistenceFinderByCommerceOrderId.fetchFirst(
			finderCache, new Object[] {commerceOrderId}, orderByComparator);
	}

	/**
	 * Removes all the commerce order items where commerceOrderId = &#63; from the database.
	 *
	 * @param commerceOrderId the commerce order ID
	 */
	@Override
	public void removeByCommerceOrderId(long commerceOrderId) {
		_collectionPersistenceFinderByCommerceOrderId.remove(
			finderCache, new Object[] {commerceOrderId});
	}

	/**
	 * Returns the number of commerce order items where commerceOrderId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @return the number of matching commerce order items
	 */
	@Override
	public int countByCommerceOrderId(long commerceOrderId) {
		return _collectionPersistenceFinderByCommerceOrderId.count(
			finderCache, new Object[] {commerceOrderId});
	}

	private CollectionPersistenceFinder
		<CommerceOrderItem, NoSuchOrderItemException>
			_collectionPersistenceFinderByCPInstanceId;

	/**
	 * Returns an ordered range of all the commerce order items where CPInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order items
	 */
	@Override
	public List<CommerceOrderItem> findByCPInstanceId(
		long CPInstanceId, int start, int end,
		OrderByComparator<CommerceOrderItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCPInstanceId.find(
			finderCache, new Object[] {CPInstanceId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order item in the ordered set where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	@Override
	public CommerceOrderItem findByCPInstanceId_First(
			long CPInstanceId,
			OrderByComparator<CommerceOrderItem> orderByComparator)
		throws NoSuchOrderItemException {

		return _collectionPersistenceFinderByCPInstanceId.findFirst(
			finderCache, new Object[] {CPInstanceId}, orderByComparator);
	}

	/**
	 * Returns the first commerce order item in the ordered set where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	@Override
	public CommerceOrderItem fetchByCPInstanceId_First(
		long CPInstanceId,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return _collectionPersistenceFinderByCPInstanceId.fetchFirst(
			finderCache, new Object[] {CPInstanceId}, orderByComparator);
	}

	/**
	 * Removes all the commerce order items where CPInstanceId = &#63; from the database.
	 *
	 * @param CPInstanceId the cp instance ID
	 */
	@Override
	public void removeByCPInstanceId(long CPInstanceId) {
		_collectionPersistenceFinderByCPInstanceId.remove(
			finderCache, new Object[] {CPInstanceId});
	}

	/**
	 * Returns the number of commerce order items where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @return the number of matching commerce order items
	 */
	@Override
	public int countByCPInstanceId(long CPInstanceId) {
		return _collectionPersistenceFinderByCPInstanceId.count(
			finderCache, new Object[] {CPInstanceId});
	}

	private CollectionPersistenceFinder
		<CommerceOrderItem, NoSuchOrderItemException>
			_collectionPersistenceFinderByCProductId;

	/**
	 * Returns an ordered range of all the commerce order items where CProductId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param CProductId the c product ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order items
	 */
	@Override
	public List<CommerceOrderItem> findByCProductId(
		long CProductId, int start, int end,
		OrderByComparator<CommerceOrderItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCProductId.find(
			finderCache, new Object[] {CProductId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order item in the ordered set where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	@Override
	public CommerceOrderItem findByCProductId_First(
			long CProductId,
			OrderByComparator<CommerceOrderItem> orderByComparator)
		throws NoSuchOrderItemException {

		return _collectionPersistenceFinderByCProductId.findFirst(
			finderCache, new Object[] {CProductId}, orderByComparator);
	}

	/**
	 * Returns the first commerce order item in the ordered set where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	@Override
	public CommerceOrderItem fetchByCProductId_First(
		long CProductId,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return _collectionPersistenceFinderByCProductId.fetchFirst(
			finderCache, new Object[] {CProductId}, orderByComparator);
	}

	/**
	 * Removes all the commerce order items where CProductId = &#63; from the database.
	 *
	 * @param CProductId the c product ID
	 */
	@Override
	public void removeByCProductId(long CProductId) {
		_collectionPersistenceFinderByCProductId.remove(
			finderCache, new Object[] {CProductId});
	}

	/**
	 * Returns the number of commerce order items where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @return the number of matching commerce order items
	 */
	@Override
	public int countByCProductId(long CProductId) {
		return _collectionPersistenceFinderByCProductId.count(
			finderCache, new Object[] {CProductId});
	}

	private CollectionPersistenceFinder
		<CommerceOrderItem, NoSuchOrderItemException>
			_collectionPersistenceFinderByCustomerCommerceOrderItemId;

	/**
	 * Returns an ordered range of all the commerce order items where customerCommerceOrderItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param customerCommerceOrderItemId the customer commerce order item ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order items
	 */
	@Override
	public List<CommerceOrderItem> findByCustomerCommerceOrderItemId(
		long customerCommerceOrderItemId, int start, int end,
		OrderByComparator<CommerceOrderItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCustomerCommerceOrderItemId.find(
			finderCache, new Object[] {customerCommerceOrderItemId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order item in the ordered set where customerCommerceOrderItemId = &#63;.
	 *
	 * @param customerCommerceOrderItemId the customer commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	@Override
	public CommerceOrderItem findByCustomerCommerceOrderItemId_First(
			long customerCommerceOrderItemId,
			OrderByComparator<CommerceOrderItem> orderByComparator)
		throws NoSuchOrderItemException {

		return _collectionPersistenceFinderByCustomerCommerceOrderItemId.
			findFirst(
				finderCache, new Object[] {customerCommerceOrderItemId},
				orderByComparator);
	}

	/**
	 * Returns the first commerce order item in the ordered set where customerCommerceOrderItemId = &#63;.
	 *
	 * @param customerCommerceOrderItemId the customer commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	@Override
	public CommerceOrderItem fetchByCustomerCommerceOrderItemId_First(
		long customerCommerceOrderItemId,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return _collectionPersistenceFinderByCustomerCommerceOrderItemId.
			fetchFirst(
				finderCache, new Object[] {customerCommerceOrderItemId},
				orderByComparator);
	}

	/**
	 * Removes all the commerce order items where customerCommerceOrderItemId = &#63; from the database.
	 *
	 * @param customerCommerceOrderItemId the customer commerce order item ID
	 */
	@Override
	public void removeByCustomerCommerceOrderItemId(
		long customerCommerceOrderItemId) {

		_collectionPersistenceFinderByCustomerCommerceOrderItemId.remove(
			finderCache, new Object[] {customerCommerceOrderItemId});
	}

	/**
	 * Returns the number of commerce order items where customerCommerceOrderItemId = &#63;.
	 *
	 * @param customerCommerceOrderItemId the customer commerce order item ID
	 * @return the number of matching commerce order items
	 */
	@Override
	public int countByCustomerCommerceOrderItemId(
		long customerCommerceOrderItemId) {

		return _collectionPersistenceFinderByCustomerCommerceOrderItemId.count(
			finderCache, new Object[] {customerCommerceOrderItemId});
	}

	private CollectionPersistenceFinder
		<CommerceOrderItem, NoSuchOrderItemException>
			_collectionPersistenceFinderByParentCommerceOrderItemId;

	/**
	 * Returns an ordered range of all the commerce order items where parentCommerceOrderItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order items
	 */
	@Override
	public List<CommerceOrderItem> findByParentCommerceOrderItemId(
		long parentCommerceOrderItemId, int start, int end,
		OrderByComparator<CommerceOrderItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByParentCommerceOrderItemId.find(
			finderCache, new Object[] {parentCommerceOrderItemId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order item in the ordered set where parentCommerceOrderItemId = &#63;.
	 *
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	@Override
	public CommerceOrderItem findByParentCommerceOrderItemId_First(
			long parentCommerceOrderItemId,
			OrderByComparator<CommerceOrderItem> orderByComparator)
		throws NoSuchOrderItemException {

		return _collectionPersistenceFinderByParentCommerceOrderItemId.
			findFirst(
				finderCache, new Object[] {parentCommerceOrderItemId},
				orderByComparator);
	}

	/**
	 * Returns the first commerce order item in the ordered set where parentCommerceOrderItemId = &#63;.
	 *
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	@Override
	public CommerceOrderItem fetchByParentCommerceOrderItemId_First(
		long parentCommerceOrderItemId,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return _collectionPersistenceFinderByParentCommerceOrderItemId.
			fetchFirst(
				finderCache, new Object[] {parentCommerceOrderItemId},
				orderByComparator);
	}

	/**
	 * Removes all the commerce order items where parentCommerceOrderItemId = &#63; from the database.
	 *
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 */
	@Override
	public void removeByParentCommerceOrderItemId(
		long parentCommerceOrderItemId) {

		_collectionPersistenceFinderByParentCommerceOrderItemId.remove(
			finderCache, new Object[] {parentCommerceOrderItemId});
	}

	/**
	 * Returns the number of commerce order items where parentCommerceOrderItemId = &#63;.
	 *
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @return the number of matching commerce order items
	 */
	@Override
	public int countByParentCommerceOrderItemId(
		long parentCommerceOrderItemId) {

		return _collectionPersistenceFinderByParentCommerceOrderItemId.count(
			finderCache, new Object[] {parentCommerceOrderItemId});
	}

	private CollectionPersistenceFinder
		<CommerceOrderItem, NoSuchOrderItemException>
			_collectionPersistenceFinderByC_CPI;

	/**
	 * Returns an ordered range of all the commerce order items where commerceOrderId = &#63; and CPInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param CPInstanceId the cp instance ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order items
	 */
	@Override
	public List<CommerceOrderItem> findByC_CPI(
		long commerceOrderId, long CPInstanceId, int start, int end,
		OrderByComparator<CommerceOrderItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_CPI.find(
			finderCache, new Object[] {commerceOrderId, CPInstanceId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order item in the ordered set where commerceOrderId = &#63; and CPInstanceId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	@Override
	public CommerceOrderItem findByC_CPI_First(
			long commerceOrderId, long CPInstanceId,
			OrderByComparator<CommerceOrderItem> orderByComparator)
		throws NoSuchOrderItemException {

		return _collectionPersistenceFinderByC_CPI.findFirst(
			finderCache, new Object[] {commerceOrderId, CPInstanceId},
			orderByComparator);
	}

	/**
	 * Returns the first commerce order item in the ordered set where commerceOrderId = &#63; and CPInstanceId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	@Override
	public CommerceOrderItem fetchByC_CPI_First(
		long commerceOrderId, long CPInstanceId,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return _collectionPersistenceFinderByC_CPI.fetchFirst(
			finderCache, new Object[] {commerceOrderId, CPInstanceId},
			orderByComparator);
	}

	/**
	 * Removes all the commerce order items where commerceOrderId = &#63; and CPInstanceId = &#63; from the database.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param CPInstanceId the cp instance ID
	 */
	@Override
	public void removeByC_CPI(long commerceOrderId, long CPInstanceId) {
		_collectionPersistenceFinderByC_CPI.remove(
			finderCache, new Object[] {commerceOrderId, CPInstanceId});
	}

	/**
	 * Returns the number of commerce order items where commerceOrderId = &#63; and CPInstanceId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param CPInstanceId the cp instance ID
	 * @return the number of matching commerce order items
	 */
	@Override
	public int countByC_CPI(long commerceOrderId, long CPInstanceId) {
		return _collectionPersistenceFinderByC_CPI.count(
			finderCache, new Object[] {commerceOrderId, CPInstanceId});
	}

	private CollectionPersistenceFinder
		<CommerceOrderItem, NoSuchOrderItemException>
			_collectionPersistenceFinderByC_PCOI;

	/**
	 * Returns an ordered range of all the commerce order items where commerceOrderId = &#63; and parentCommerceOrderItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order items
	 */
	@Override
	public List<CommerceOrderItem> findByC_PCOI(
		long commerceOrderId, long parentCommerceOrderItemId, int start,
		int end, OrderByComparator<CommerceOrderItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_PCOI.find(
			finderCache,
			new Object[] {commerceOrderId, parentCommerceOrderItemId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order item in the ordered set where commerceOrderId = &#63; and parentCommerceOrderItemId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	@Override
	public CommerceOrderItem findByC_PCOI_First(
			long commerceOrderId, long parentCommerceOrderItemId,
			OrderByComparator<CommerceOrderItem> orderByComparator)
		throws NoSuchOrderItemException {

		return _collectionPersistenceFinderByC_PCOI.findFirst(
			finderCache,
			new Object[] {commerceOrderId, parentCommerceOrderItemId},
			orderByComparator);
	}

	/**
	 * Returns the first commerce order item in the ordered set where commerceOrderId = &#63; and parentCommerceOrderItemId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	@Override
	public CommerceOrderItem fetchByC_PCOI_First(
		long commerceOrderId, long parentCommerceOrderItemId,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return _collectionPersistenceFinderByC_PCOI.fetchFirst(
			finderCache,
			new Object[] {commerceOrderId, parentCommerceOrderItemId},
			orderByComparator);
	}

	/**
	 * Removes all the commerce order items where commerceOrderId = &#63; and parentCommerceOrderItemId = &#63; from the database.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 */
	@Override
	public void removeByC_PCOI(
		long commerceOrderId, long parentCommerceOrderItemId) {

		_collectionPersistenceFinderByC_PCOI.remove(
			finderCache,
			new Object[] {commerceOrderId, parentCommerceOrderItemId});
	}

	/**
	 * Returns the number of commerce order items where commerceOrderId = &#63; and parentCommerceOrderItemId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @return the number of matching commerce order items
	 */
	@Override
	public int countByC_PCOI(
		long commerceOrderId, long parentCommerceOrderItemId) {

		return _collectionPersistenceFinderByC_PCOI.count(
			finderCache,
			new Object[] {commerceOrderId, parentCommerceOrderItemId});
	}

	private CollectionPersistenceFinder
		<CommerceOrderItem, NoSuchOrderItemException>
			_collectionPersistenceFinderByC_S;

	/**
	 * Returns an ordered range of all the commerce order items where commerceOrderId = &#63; and subscription = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param subscription the subscription
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order items
	 */
	@Override
	public List<CommerceOrderItem> findByC_S(
		long commerceOrderId, boolean subscription, int start, int end,
		OrderByComparator<CommerceOrderItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_S.find(
			finderCache, new Object[] {commerceOrderId, subscription}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order item in the ordered set where commerceOrderId = &#63; and subscription = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param subscription the subscription
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	@Override
	public CommerceOrderItem findByC_S_First(
			long commerceOrderId, boolean subscription,
			OrderByComparator<CommerceOrderItem> orderByComparator)
		throws NoSuchOrderItemException {

		return _collectionPersistenceFinderByC_S.findFirst(
			finderCache, new Object[] {commerceOrderId, subscription},
			orderByComparator);
	}

	/**
	 * Returns the first commerce order item in the ordered set where commerceOrderId = &#63; and subscription = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param subscription the subscription
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	@Override
	public CommerceOrderItem fetchByC_S_First(
		long commerceOrderId, boolean subscription,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return _collectionPersistenceFinderByC_S.fetchFirst(
			finderCache, new Object[] {commerceOrderId, subscription},
			orderByComparator);
	}

	/**
	 * Removes all the commerce order items where commerceOrderId = &#63; and subscription = &#63; from the database.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param subscription the subscription
	 */
	@Override
	public void removeByC_S(long commerceOrderId, boolean subscription) {
		_collectionPersistenceFinderByC_S.remove(
			finderCache, new Object[] {commerceOrderId, subscription});
	}

	/**
	 * Returns the number of commerce order items where commerceOrderId = &#63; and subscription = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param subscription the subscription
	 * @return the number of matching commerce order items
	 */
	@Override
	public int countByC_S(long commerceOrderId, boolean subscription) {
		return _collectionPersistenceFinderByC_S.count(
			finderCache, new Object[] {commerceOrderId, subscription});
	}

	private UniquePersistenceFinder<CommerceOrderItem, NoSuchOrderItemException>
		_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the commerce order item where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchOrderItemException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	@Override
	public CommerceOrderItem findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchOrderItemException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the commerce order item where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	@Override
	public CommerceOrderItem fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the commerce order item where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the commerce order item that was removed
	 */
	@Override
	public CommerceOrderItem removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchOrderItemException {

		CommerceOrderItem commerceOrderItem = findByERC_C(
			externalReferenceCode, companyId);

		return remove(commerceOrderItem);
	}

	/**
	 * Returns the number of commerce order items where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching commerce order items
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public CommerceOrderItemPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put(
			"commerceInventoryBookedQuantityId", "CIBookedQuantityId");
		dbColumnNames.put(
			"deliverySubscriptionTypeSettings", "deliverySubTypeSettings");
		dbColumnNames.put(
			"discountPercentageLevel1WithTaxAmount",
			"discountPctLevel1WithTaxAmount");
		dbColumnNames.put(
			"discountPercentageLevel2WithTaxAmount",
			"discountPctLevel2WithTaxAmount");
		dbColumnNames.put(
			"discountPercentageLevel3WithTaxAmount",
			"discountPctLevel3WithTaxAmount");
		dbColumnNames.put(
			"discountPercentageLevel4WithTaxAmount",
			"discountPctLevel4WithTaxAmount");
		dbColumnNames.put(
			"unitOfMeasureIncrementalOrderQuantity",
			"UOMIncrementalOrderQuantity");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceOrderItem.class);

		setModelImplClass(CommerceOrderItemImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceOrderItemTable.INSTANCE);
	}

	/**
	 * Creates a new commerce order item with the primary key. Does not add the commerce order item to the database.
	 *
	 * @param commerceOrderItemId the primary key for the new commerce order item
	 * @return the new commerce order item
	 */
	@Override
	public CommerceOrderItem create(long commerceOrderItemId) {
		CommerceOrderItem commerceOrderItem = new CommerceOrderItemImpl();

		commerceOrderItem.setNew(true);
		commerceOrderItem.setPrimaryKey(commerceOrderItemId);

		String uuid = PortalUUIDUtil.generate();

		commerceOrderItem.setUuid(uuid);

		commerceOrderItem.setCompanyId(CompanyThreadLocal.getCompanyId());

		return commerceOrderItem;
	}

	/**
	 * Removes the commerce order item with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceOrderItemId the primary key of the commerce order item
	 * @return the commerce order item that was removed
	 * @throws NoSuchOrderItemException if a commerce order item with the primary key could not be found
	 */
	@Override
	public CommerceOrderItem remove(long commerceOrderItemId)
		throws NoSuchOrderItemException {

		return remove((Serializable)commerceOrderItemId);
	}

	@Override
	protected CommerceOrderItem removeImpl(
		CommerceOrderItem commerceOrderItem) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceOrderItem)) {
				commerceOrderItem = (CommerceOrderItem)session.get(
					CommerceOrderItemImpl.class,
					commerceOrderItem.getPrimaryKeyObj());
			}

			if (commerceOrderItem != null) {
				session.delete(commerceOrderItem);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceOrderItem != null) {
			clearCache(commerceOrderItem);
		}

		return commerceOrderItem;
	}

	@Override
	public CommerceOrderItem updateImpl(CommerceOrderItem commerceOrderItem) {
		boolean isNew = commerceOrderItem.isNew();

		if (!(commerceOrderItem instanceof CommerceOrderItemModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(commerceOrderItem.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceOrderItem);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceOrderItem proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceOrderItem implementation " +
					commerceOrderItem.getClass());
		}

		CommerceOrderItemModelImpl commerceOrderItemModelImpl =
			(CommerceOrderItemModelImpl)commerceOrderItem;

		if (Validator.isNull(commerceOrderItem.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			commerceOrderItem.setUuid(uuid);
		}

		if (Validator.isNull(commerceOrderItem.getExternalReferenceCode())) {
			commerceOrderItem.setExternalReferenceCode(
				commerceOrderItem.getUuid());
		}
		else {
			if (!Objects.equals(
					commerceOrderItemModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					commerceOrderItem.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = commerceOrderItem.getCompanyId();

					long groupId = commerceOrderItem.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = commerceOrderItem.getPrimaryKey();
					}

					try {
						commerceOrderItem.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								CommerceOrderItem.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								commerceOrderItem.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			CommerceOrderItem ercCommerceOrderItem = fetchByERC_C(
				commerceOrderItem.getExternalReferenceCode(),
				commerceOrderItem.getCompanyId());

			if (isNew) {
				if (ercCommerceOrderItem != null) {
					throw new DuplicateCommerceOrderItemExternalReferenceCodeException(
						"Duplicate commerce order item with external reference code " +
							commerceOrderItem.getExternalReferenceCode() +
								" and company " +
									commerceOrderItem.getCompanyId());
				}
			}
			else {
				if ((ercCommerceOrderItem != null) &&
					(commerceOrderItem.getCommerceOrderItemId() !=
						ercCommerceOrderItem.getCommerceOrderItemId())) {

					throw new DuplicateCommerceOrderItemExternalReferenceCodeException(
						"Duplicate commerce order item with external reference code " +
							commerceOrderItem.getExternalReferenceCode() +
								" and company " +
									commerceOrderItem.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceOrderItem.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceOrderItem.setCreateDate(date);
			}
			else {
				commerceOrderItem.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceOrderItemModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceOrderItem.setModifiedDate(date);
			}
			else {
				commerceOrderItem.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceOrderItem);
			}
			else {
				commerceOrderItem = (CommerceOrderItem)session.merge(
					commerceOrderItem);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceOrderItem, false);

		if (isNew) {
			commerceOrderItem.setNew(false);
		}

		commerceOrderItem.resetOriginalValues();

		return commerceOrderItem;
	}

	/**
	 * Returns the commerce order item with the primary key or throws a <code>NoSuchOrderItemException</code> if it could not be found.
	 *
	 * @param commerceOrderItemId the primary key of the commerce order item
	 * @return the commerce order item
	 * @throws NoSuchOrderItemException if a commerce order item with the primary key could not be found
	 */
	@Override
	public CommerceOrderItem findByPrimaryKey(long commerceOrderItemId)
		throws NoSuchOrderItemException {

		return findByPrimaryKey((Serializable)commerceOrderItemId);
	}

	/**
	 * Returns the commerce order item with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceOrderItemId the primary key of the commerce order item
	 * @return the commerce order item, or <code>null</code> if a commerce order item with the primary key could not be found
	 */
	@Override
	public CommerceOrderItem fetchByPrimaryKey(long commerceOrderItemId) {
		return fetchByPrimaryKey((Serializable)commerceOrderItemId);
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
		return "commerceOrderItemId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEORDERITEM;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceOrderItemModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce order item persistence.
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
			_SQL_SELECT_COMMERCEORDERITEM_WHERE,
			_SQL_COUNT_COMMERCEORDERITEM_WHERE,
			CommerceOrderItemModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"commerceOrderItem.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, CommerceOrderItem::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(CommerceOrderItem::getUuid),
				CommerceOrderItem::getGroupId),
			_SQL_SELECT_COMMERCEORDERITEM_WHERE, "",
			new FinderColumn<>(
				"commerceOrderItem.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, CommerceOrderItem::getUuid),
			new FinderColumn<>(
				"commerceOrderItem.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, CommerceOrderItem::getGroupId));

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
				_SQL_SELECT_COMMERCEORDERITEM_WHERE,
				_SQL_COUNT_COMMERCEORDERITEM_WHERE,
				CommerceOrderItemModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"commerceOrderItem.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CommerceOrderItem::getUuid),
				new FinderColumn<>(
					"commerceOrderItem.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, CommerceOrderItem::getCompanyId));

		_collectionPersistenceFinderByCommerceInventoryBookedQuantityId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommerceInventoryBookedQuantityId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"CIBookedQuantityId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommerceInventoryBookedQuantityId",
					new String[] {Long.class.getName()},
					new String[] {"CIBookedQuantityId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommerceInventoryBookedQuantityId",
					new String[] {Long.class.getName()},
					new String[] {"CIBookedQuantityId"}, false),
				_SQL_SELECT_COMMERCEORDERITEM_WHERE,
				_SQL_COUNT_COMMERCEORDERITEM_WHERE,
				CommerceOrderItemModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"commerceOrderItem.", "commerceInventoryBookedQuantityId",
					"CIBookedQuantityId", FinderColumn.Type.LONG, "=", true,
					true,
					CommerceOrderItem::getCommerceInventoryBookedQuantityId));

		_collectionPersistenceFinderByCommerceOrderId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommerceOrderId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commerceOrderId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommerceOrderId",
					new String[] {Long.class.getName()},
					new String[] {"commerceOrderId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommerceOrderId",
					new String[] {Long.class.getName()},
					new String[] {"commerceOrderId"}, false),
				_SQL_SELECT_COMMERCEORDERITEM_WHERE,
				_SQL_COUNT_COMMERCEORDERITEM_WHERE,
				CommerceOrderItemModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"commerceOrderItem.", "commerceOrderId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceOrderItem::getCommerceOrderId));

		_collectionPersistenceFinderByCPInstanceId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCPInstanceId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"CPInstanceId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCPInstanceId", new String[] {Long.class.getName()},
					new String[] {"CPInstanceId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCPInstanceId", new String[] {Long.class.getName()},
					new String[] {"CPInstanceId"}, false),
				_SQL_SELECT_COMMERCEORDERITEM_WHERE,
				_SQL_COUNT_COMMERCEORDERITEM_WHERE,
				CommerceOrderItemModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"commerceOrderItem.", "CPInstanceId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceOrderItem::getCPInstanceId));

		_collectionPersistenceFinderByCProductId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCProductId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"CProductId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCProductId", new String[] {Long.class.getName()},
					new String[] {"CProductId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCProductId", new String[] {Long.class.getName()},
					new String[] {"CProductId"}, false),
				_SQL_SELECT_COMMERCEORDERITEM_WHERE,
				_SQL_COUNT_COMMERCEORDERITEM_WHERE,
				CommerceOrderItemModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"commerceOrderItem.", "CProductId", FinderColumn.Type.LONG,
					"=", true, true, CommerceOrderItem::getCProductId));

		_collectionPersistenceFinderByCustomerCommerceOrderItemId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCustomerCommerceOrderItemId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"customerCommerceOrderItemId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCustomerCommerceOrderItemId",
					new String[] {Long.class.getName()},
					new String[] {"customerCommerceOrderItemId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCustomerCommerceOrderItemId",
					new String[] {Long.class.getName()},
					new String[] {"customerCommerceOrderItemId"}, false),
				_SQL_SELECT_COMMERCEORDERITEM_WHERE,
				_SQL_COUNT_COMMERCEORDERITEM_WHERE,
				CommerceOrderItemModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"commerceOrderItem.", "customerCommerceOrderItemId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceOrderItem::getCustomerCommerceOrderItemId));

		_collectionPersistenceFinderByParentCommerceOrderItemId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByParentCommerceOrderItemId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"parentCommerceOrderItemId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByParentCommerceOrderItemId",
					new String[] {Long.class.getName()},
					new String[] {"parentCommerceOrderItemId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByParentCommerceOrderItemId",
					new String[] {Long.class.getName()},
					new String[] {"parentCommerceOrderItemId"}, false),
				_SQL_SELECT_COMMERCEORDERITEM_WHERE,
				_SQL_COUNT_COMMERCEORDERITEM_WHERE,
				CommerceOrderItemModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"commerceOrderItem.", "parentCommerceOrderItemId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceOrderItem::getParentCommerceOrderItemId));

		_collectionPersistenceFinderByC_CPI = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_CPI",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"commerceOrderId", "CPInstanceId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_CPI",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"commerceOrderId", "CPInstanceId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_CPI",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"commerceOrderId", "CPInstanceId"}, false),
			_SQL_SELECT_COMMERCEORDERITEM_WHERE,
			_SQL_COUNT_COMMERCEORDERITEM_WHERE,
			CommerceOrderItemModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"commerceOrderItem.", "commerceOrderId", FinderColumn.Type.LONG,
				"=", true, true, CommerceOrderItem::getCommerceOrderId),
			new FinderColumn<>(
				"commerceOrderItem.", "CPInstanceId", FinderColumn.Type.LONG,
				"=", true, true, CommerceOrderItem::getCPInstanceId));

		_collectionPersistenceFinderByC_PCOI =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_PCOI",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"commerceOrderId", "parentCommerceOrderItemId"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_PCOI",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {
						"commerceOrderId", "parentCommerceOrderItemId"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_PCOI",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {
						"commerceOrderId", "parentCommerceOrderItemId"
					},
					false),
				_SQL_SELECT_COMMERCEORDERITEM_WHERE,
				_SQL_COUNT_COMMERCEORDERITEM_WHERE,
				CommerceOrderItemModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"commerceOrderItem.", "commerceOrderId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceOrderItem::getCommerceOrderId),
				new FinderColumn<>(
					"commerceOrderItem.", "parentCommerceOrderItemId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceOrderItem::getParentCommerceOrderItemId));

		_collectionPersistenceFinderByC_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_S",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"commerceOrderId", "subscription"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_S",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"commerceOrderId", "subscription"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_S",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"commerceOrderId", "subscription"}, false),
			_SQL_SELECT_COMMERCEORDERITEM_WHERE,
			_SQL_COUNT_COMMERCEORDERITEM_WHERE,
			CommerceOrderItemModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"commerceOrderItem.", "commerceOrderId", FinderColumn.Type.LONG,
				"=", true, true, CommerceOrderItem::getCommerceOrderId),
			new FinderColumn<>(
				"commerceOrderItem.", "subscription", FinderColumn.Type.BOOLEAN,
				"=", true, true, CommerceOrderItem::isSubscription));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(
					CommerceOrderItem::getExternalReferenceCode),
				CommerceOrderItem::getCompanyId),
			_SQL_SELECT_COMMERCEORDERITEM_WHERE, "",
			new FinderColumn<>(
				"commerceOrderItem.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceOrderItem::getExternalReferenceCode),
			new FinderColumn<>(
				"commerceOrderItem.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, CommerceOrderItem::getCompanyId));

		CommerceOrderItemUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceOrderItemUtil.setPersistence(null);

		entityCache.removeCache(CommerceOrderItemImpl.class.getName());
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
		CommerceOrderItemModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCEORDERITEM =
		"SELECT commerceOrderItem FROM CommerceOrderItem commerceOrderItem";

	private static final String _SQL_SELECT_COMMERCEORDERITEM_WHERE =
		"SELECT commerceOrderItem FROM CommerceOrderItem commerceOrderItem WHERE ";

	private static final String _SQL_COUNT_COMMERCEORDERITEM_WHERE =
		"SELECT COUNT(commerceOrderItem) FROM CommerceOrderItem commerceOrderItem WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommerceOrderItem exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceOrderItemPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {
			"uuid", "commerceInventoryBookedQuantityId",
			"deliverySubscriptionTypeSettings",
			"discountPercentageLevel1WithTaxAmount",
			"discountPercentageLevel2WithTaxAmount",
			"discountPercentageLevel3WithTaxAmount",
			"discountPercentageLevel4WithTaxAmount",
			"unitOfMeasureIncrementalOrderQuantity"
		});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:772564878