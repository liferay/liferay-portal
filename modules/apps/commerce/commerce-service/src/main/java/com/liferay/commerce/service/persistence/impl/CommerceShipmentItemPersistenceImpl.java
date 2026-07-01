/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence.impl;

import com.liferay.commerce.exception.DuplicateCommerceShipmentItemExternalReferenceCodeException;
import com.liferay.commerce.exception.NoSuchShipmentItemException;
import com.liferay.commerce.model.CommerceShipmentItem;
import com.liferay.commerce.model.CommerceShipmentItemTable;
import com.liferay.commerce.model.impl.CommerceShipmentItemImpl;
import com.liferay.commerce.model.impl.CommerceShipmentItemModelImpl;
import com.liferay.commerce.service.persistence.CommerceShipmentItemPersistence;
import com.liferay.commerce.service.persistence.CommerceShipmentItemUtil;
import com.liferay.commerce.service.persistence.impl.constants.CommercePersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.exception.SystemException;
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

import java.math.BigDecimal;

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
 * The persistence implementation for the commerce shipment item service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(service = CommerceShipmentItemPersistence.class)
public class CommerceShipmentItemPersistenceImpl
	extends BasePersistenceImpl
		<CommerceShipmentItem, NoSuchShipmentItemException>
	implements CommerceShipmentItemPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceShipmentItemUtil</code> to access the commerce shipment item persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceShipmentItemImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceShipmentItem, NoSuchShipmentItemException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the commerce shipment items where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipment items
	 */
	@Override
	public List<CommerceShipmentItem> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceShipmentItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce shipment item in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	@Override
	public CommerceShipmentItem findByUuid_First(
			String uuid,
			OrderByComparator<CommerceShipmentItem> orderByComparator)
		throws NoSuchShipmentItemException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first commerce shipment item in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	@Override
	public CommerceShipmentItem fetchByUuid_First(
		String uuid,
		OrderByComparator<CommerceShipmentItem> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the commerce shipment items where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of commerce shipment items where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce shipment items
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<CommerceShipmentItem, NoSuchShipmentItemException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the commerce shipment item where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchShipmentItemException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	@Override
	public CommerceShipmentItem findByUUID_G(String uuid, long groupId)
		throws NoSuchShipmentItemException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the commerce shipment item where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	@Override
	public CommerceShipmentItem fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the commerce shipment item where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the commerce shipment item that was removed
	 */
	@Override
	public CommerceShipmentItem removeByUUID_G(String uuid, long groupId)
		throws NoSuchShipmentItemException {

		CommerceShipmentItem commerceShipmentItem = findByUUID_G(uuid, groupId);

		return remove(commerceShipmentItem);
	}

	/**
	 * Returns the number of commerce shipment items where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching commerce shipment items
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<CommerceShipmentItem, NoSuchShipmentItemException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the commerce shipment items where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipment items
	 */
	@Override
	public List<CommerceShipmentItem> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceShipmentItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce shipment item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	@Override
	public CommerceShipmentItem findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommerceShipmentItem> orderByComparator)
		throws NoSuchShipmentItemException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first commerce shipment item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	@Override
	public CommerceShipmentItem fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommerceShipmentItem> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the commerce shipment items where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of commerce shipment items where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce shipment items
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<CommerceShipmentItem, NoSuchShipmentItemException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the commerce shipment items where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipment items
	 */
	@Override
	public List<CommerceShipmentItem> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CommerceShipmentItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce shipment item in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	@Override
	public CommerceShipmentItem findByGroupId_First(
			long groupId,
			OrderByComparator<CommerceShipmentItem> orderByComparator)
		throws NoSuchShipmentItemException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first commerce shipment item in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	@Override
	public CommerceShipmentItem fetchByGroupId_First(
		long groupId,
		OrderByComparator<CommerceShipmentItem> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Removes all the commerce shipment items where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of commerce shipment items where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching commerce shipment items
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	private CollectionPersistenceFinder
		<CommerceShipmentItem, NoSuchShipmentItemException>
			_collectionPersistenceFinderByCommerceShipmentId;

	/**
	 * Returns an ordered range of all the commerce shipment items where commerceShipmentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipment items
	 */
	@Override
	public List<CommerceShipmentItem> findByCommerceShipmentId(
		long commerceShipmentId, int start, int end,
		OrderByComparator<CommerceShipmentItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceShipmentId.find(
			finderCache, new Object[] {commerceShipmentId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce shipment item in the ordered set where commerceShipmentId = &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	@Override
	public CommerceShipmentItem findByCommerceShipmentId_First(
			long commerceShipmentId,
			OrderByComparator<CommerceShipmentItem> orderByComparator)
		throws NoSuchShipmentItemException {

		return _collectionPersistenceFinderByCommerceShipmentId.findFirst(
			finderCache, new Object[] {commerceShipmentId}, orderByComparator);
	}

	/**
	 * Returns the first commerce shipment item in the ordered set where commerceShipmentId = &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	@Override
	public CommerceShipmentItem fetchByCommerceShipmentId_First(
		long commerceShipmentId,
		OrderByComparator<CommerceShipmentItem> orderByComparator) {

		return _collectionPersistenceFinderByCommerceShipmentId.fetchFirst(
			finderCache, new Object[] {commerceShipmentId}, orderByComparator);
	}

	/**
	 * Removes all the commerce shipment items where commerceShipmentId = &#63; from the database.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 */
	@Override
	public void removeByCommerceShipmentId(long commerceShipmentId) {
		_collectionPersistenceFinderByCommerceShipmentId.remove(
			finderCache, new Object[] {commerceShipmentId});
	}

	/**
	 * Returns the number of commerce shipment items where commerceShipmentId = &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @return the number of matching commerce shipment items
	 */
	@Override
	public int countByCommerceShipmentId(long commerceShipmentId) {
		return _collectionPersistenceFinderByCommerceShipmentId.count(
			finderCache, new Object[] {commerceShipmentId});
	}

	private CollectionPersistenceFinder
		<CommerceShipmentItem, NoSuchShipmentItemException>
			_collectionPersistenceFinderByCommerceOrderItemId;

	/**
	 * Returns an ordered range of all the commerce shipment items where commerceOrderItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderItemId the commerce order item ID
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipment items
	 */
	@Override
	public List<CommerceShipmentItem> findByCommerceOrderItemId(
		long commerceOrderItemId, int start, int end,
		OrderByComparator<CommerceShipmentItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceOrderItemId.find(
			finderCache, new Object[] {commerceOrderItemId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce shipment item in the ordered set where commerceOrderItemId = &#63;.
	 *
	 * @param commerceOrderItemId the commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	@Override
	public CommerceShipmentItem findByCommerceOrderItemId_First(
			long commerceOrderItemId,
			OrderByComparator<CommerceShipmentItem> orderByComparator)
		throws NoSuchShipmentItemException {

		return _collectionPersistenceFinderByCommerceOrderItemId.findFirst(
			finderCache, new Object[] {commerceOrderItemId}, orderByComparator);
	}

	/**
	 * Returns the first commerce shipment item in the ordered set where commerceOrderItemId = &#63;.
	 *
	 * @param commerceOrderItemId the commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	@Override
	public CommerceShipmentItem fetchByCommerceOrderItemId_First(
		long commerceOrderItemId,
		OrderByComparator<CommerceShipmentItem> orderByComparator) {

		return _collectionPersistenceFinderByCommerceOrderItemId.fetchFirst(
			finderCache, new Object[] {commerceOrderItemId}, orderByComparator);
	}

	/**
	 * Removes all the commerce shipment items where commerceOrderItemId = &#63; from the database.
	 *
	 * @param commerceOrderItemId the commerce order item ID
	 */
	@Override
	public void removeByCommerceOrderItemId(long commerceOrderItemId) {
		_collectionPersistenceFinderByCommerceOrderItemId.remove(
			finderCache, new Object[] {commerceOrderItemId});
	}

	/**
	 * Returns the number of commerce shipment items where commerceOrderItemId = &#63;.
	 *
	 * @param commerceOrderItemId the commerce order item ID
	 * @return the number of matching commerce shipment items
	 */
	@Override
	public int countByCommerceOrderItemId(long commerceOrderItemId) {
		return _collectionPersistenceFinderByCommerceOrderItemId.count(
			finderCache, new Object[] {commerceOrderItemId});
	}

	private CollectionPersistenceFinder
		<CommerceShipmentItem, NoSuchShipmentItemException>
			_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the commerce shipment items where commerceShipmentId = &#63; and commerceOrderItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipment items
	 */
	@Override
	public List<CommerceShipmentItem> findByC_C(
		long commerceShipmentId, long commerceOrderItemId, int start, int end,
		OrderByComparator<CommerceShipmentItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			finderCache, new Object[] {commerceShipmentId, commerceOrderItemId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce shipment item in the ordered set where commerceShipmentId = &#63; and commerceOrderItemId = &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	@Override
	public CommerceShipmentItem findByC_C_First(
			long commerceShipmentId, long commerceOrderItemId,
			OrderByComparator<CommerceShipmentItem> orderByComparator)
		throws NoSuchShipmentItemException {

		return _collectionPersistenceFinderByC_C.findFirst(
			finderCache, new Object[] {commerceShipmentId, commerceOrderItemId},
			orderByComparator);
	}

	/**
	 * Returns the first commerce shipment item in the ordered set where commerceShipmentId = &#63; and commerceOrderItemId = &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	@Override
	public CommerceShipmentItem fetchByC_C_First(
		long commerceShipmentId, long commerceOrderItemId,
		OrderByComparator<CommerceShipmentItem> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			finderCache, new Object[] {commerceShipmentId, commerceOrderItemId},
			orderByComparator);
	}

	/**
	 * Removes all the commerce shipment items where commerceShipmentId = &#63; and commerceOrderItemId = &#63; from the database.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 */
	@Override
	public void removeByC_C(long commerceShipmentId, long commerceOrderItemId) {
		_collectionPersistenceFinderByC_C.remove(
			finderCache,
			new Object[] {commerceShipmentId, commerceOrderItemId});
	}

	/**
	 * Returns the number of commerce shipment items where commerceShipmentId = &#63; and commerceOrderItemId = &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @return the number of matching commerce shipment items
	 */
	@Override
	public int countByC_C(long commerceShipmentId, long commerceOrderItemId) {
		return _collectionPersistenceFinderByC_C.count(
			finderCache,
			new Object[] {commerceShipmentId, commerceOrderItemId});
	}

	private UniquePersistenceFinder
		<CommerceShipmentItem, NoSuchShipmentItemException>
			_uniquePersistenceFinderByC_C_C;

	/**
	 * Returns the commerce shipment item where commerceShipmentId = &#63; and commerceOrderItemId = &#63; and commerceInventoryWarehouseId = &#63; or throws a <code>NoSuchShipmentItemException</code> if it could not be found.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @return the matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	@Override
	public CommerceShipmentItem findByC_C_C(
			long commerceShipmentId, long commerceOrderItemId,
			long commerceInventoryWarehouseId)
		throws NoSuchShipmentItemException {

		return _uniquePersistenceFinderByC_C_C.find(
			finderCache,
			new Object[] {
				commerceShipmentId, commerceOrderItemId,
				commerceInventoryWarehouseId
			});
	}

	/**
	 * Returns the commerce shipment item where commerceShipmentId = &#63; and commerceOrderItemId = &#63; and commerceInventoryWarehouseId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	@Override
	public CommerceShipmentItem fetchByC_C_C(
		long commerceShipmentId, long commerceOrderItemId,
		long commerceInventoryWarehouseId, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_C_C.fetch(
			finderCache,
			new Object[] {
				commerceShipmentId, commerceOrderItemId,
				commerceInventoryWarehouseId
			},
			useFinderCache);
	}

	/**
	 * Removes the commerce shipment item where commerceShipmentId = &#63; and commerceOrderItemId = &#63; and commerceInventoryWarehouseId = &#63; from the database.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @return the commerce shipment item that was removed
	 */
	@Override
	public CommerceShipmentItem removeByC_C_C(
			long commerceShipmentId, long commerceOrderItemId,
			long commerceInventoryWarehouseId)
		throws NoSuchShipmentItemException {

		CommerceShipmentItem commerceShipmentItem = findByC_C_C(
			commerceShipmentId, commerceOrderItemId,
			commerceInventoryWarehouseId);

		return remove(commerceShipmentItem);
	}

	/**
	 * Returns the number of commerce shipment items where commerceShipmentId = &#63; and commerceOrderItemId = &#63; and commerceInventoryWarehouseId = &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @return the number of matching commerce shipment items
	 */
	@Override
	public int countByC_C_C(
		long commerceShipmentId, long commerceOrderItemId,
		long commerceInventoryWarehouseId) {

		return _uniquePersistenceFinderByC_C_C.count(
			finderCache,
			new Object[] {
				commerceShipmentId, commerceOrderItemId,
				commerceInventoryWarehouseId
			});
	}

	private CollectionPersistenceFinder
		<CommerceShipmentItem, NoSuchShipmentItemException>
			_collectionPersistenceFinderByC_NotC_GteQ;

	/**
	 * Returns all the commerce shipment items where commerceShipmentId = &#63; and commerceInventoryWarehouseId &ne; &#63; and quantity &ge; &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param quantity the quantity
	 * @return the matching commerce shipment items
	 */
	@Override
	public List<CommerceShipmentItem> findByC_NotC_GteQ(
		long commerceShipmentId, long commerceInventoryWarehouseId,
		BigDecimal quantity) {

		return findByC_NotC_GteQ(
			commerceShipmentId, commerceInventoryWarehouseId, quantity,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce shipment items where commerceShipmentId = &#63; and commerceInventoryWarehouseId &ne; &#63; and quantity &ge; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param quantity the quantity
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @return the range of matching commerce shipment items
	 */
	@Override
	public List<CommerceShipmentItem> findByC_NotC_GteQ(
		long commerceShipmentId, long commerceInventoryWarehouseId,
		BigDecimal quantity, int start, int end) {

		return findByC_NotC_GteQ(
			commerceShipmentId, commerceInventoryWarehouseId, quantity, start,
			end, null);
	}

	/**
	 * Returns an ordered range of all the commerce shipment items where commerceShipmentId = &#63; and commerceInventoryWarehouseId &ne; &#63; and quantity &ge; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param quantity the quantity
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce shipment items
	 */
	@Override
	public List<CommerceShipmentItem> findByC_NotC_GteQ(
		long commerceShipmentId, long commerceInventoryWarehouseId,
		BigDecimal quantity, int start, int end,
		OrderByComparator<CommerceShipmentItem> orderByComparator) {

		return findByC_NotC_GteQ(
			commerceShipmentId, commerceInventoryWarehouseId, quantity, start,
			end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce shipment items where commerceShipmentId = &#63; and commerceInventoryWarehouseId &ne; &#63; and quantity &ge; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param quantity the quantity
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipment items
	 */
	@Override
	public List<CommerceShipmentItem> findByC_NotC_GteQ(
		long commerceShipmentId, long commerceInventoryWarehouseId,
		BigDecimal quantity, int start, int end,
		OrderByComparator<CommerceShipmentItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_NotC_GteQ.find(
			finderCache,
			new Object[] {
				commerceShipmentId, commerceInventoryWarehouseId, quantity
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce shipment item in the ordered set where commerceShipmentId = &#63; and commerceInventoryWarehouseId &ne; &#63; and quantity &ge; &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param quantity the quantity
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	@Override
	public CommerceShipmentItem findByC_NotC_GteQ_First(
			long commerceShipmentId, long commerceInventoryWarehouseId,
			BigDecimal quantity,
			OrderByComparator<CommerceShipmentItem> orderByComparator)
		throws NoSuchShipmentItemException {

		return _collectionPersistenceFinderByC_NotC_GteQ.findFirst(
			finderCache,
			new Object[] {
				commerceShipmentId, commerceInventoryWarehouseId, quantity
			},
			orderByComparator);
	}

	/**
	 * Returns the first commerce shipment item in the ordered set where commerceShipmentId = &#63; and commerceInventoryWarehouseId &ne; &#63; and quantity &ge; &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param quantity the quantity
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	@Override
	public CommerceShipmentItem fetchByC_NotC_GteQ_First(
		long commerceShipmentId, long commerceInventoryWarehouseId,
		BigDecimal quantity,
		OrderByComparator<CommerceShipmentItem> orderByComparator) {

		return _collectionPersistenceFinderByC_NotC_GteQ.fetchFirst(
			finderCache,
			new Object[] {
				commerceShipmentId, commerceInventoryWarehouseId, quantity
			},
			orderByComparator);
	}

	/**
	 * Removes all the commerce shipment items where commerceShipmentId = &#63; and commerceInventoryWarehouseId &ne; &#63; and quantity &ge; &#63; from the database.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param quantity the quantity
	 */
	@Override
	public void removeByC_NotC_GteQ(
		long commerceShipmentId, long commerceInventoryWarehouseId,
		BigDecimal quantity) {

		_collectionPersistenceFinderByC_NotC_GteQ.remove(
			finderCache,
			new Object[] {
				commerceShipmentId, commerceInventoryWarehouseId, quantity
			});
	}

	/**
	 * Returns the number of commerce shipment items where commerceShipmentId = &#63; and commerceInventoryWarehouseId &ne; &#63; and quantity &ge; &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param quantity the quantity
	 * @return the number of matching commerce shipment items
	 */
	@Override
	public int countByC_NotC_GteQ(
		long commerceShipmentId, long commerceInventoryWarehouseId,
		BigDecimal quantity) {

		return _collectionPersistenceFinderByC_NotC_GteQ.count(
			finderCache,
			new Object[] {
				commerceShipmentId, commerceInventoryWarehouseId, quantity
			});
	}

	private UniquePersistenceFinder
		<CommerceShipmentItem, NoSuchShipmentItemException>
			_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the commerce shipment item where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchShipmentItemException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	@Override
	public CommerceShipmentItem findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchShipmentItemException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the commerce shipment item where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	@Override
	public CommerceShipmentItem fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the commerce shipment item where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the commerce shipment item that was removed
	 */
	@Override
	public CommerceShipmentItem removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchShipmentItemException {

		CommerceShipmentItem commerceShipmentItem = findByERC_C(
			externalReferenceCode, companyId);

		return remove(commerceShipmentItem);
	}

	/**
	 * Returns the number of commerce shipment items where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching commerce shipment items
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public CommerceShipmentItemPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceShipmentItem.class);

		setModelImplClass(CommerceShipmentItemImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceShipmentItemTable.INSTANCE);
	}

	/**
	 * Creates a new commerce shipment item with the primary key. Does not add the commerce shipment item to the database.
	 *
	 * @param commerceShipmentItemId the primary key for the new commerce shipment item
	 * @return the new commerce shipment item
	 */
	@Override
	public CommerceShipmentItem create(long commerceShipmentItemId) {
		CommerceShipmentItem commerceShipmentItem =
			new CommerceShipmentItemImpl();

		commerceShipmentItem.setNew(true);
		commerceShipmentItem.setPrimaryKey(commerceShipmentItemId);

		String uuid = PortalUUIDUtil.generate();

		commerceShipmentItem.setUuid(uuid);

		commerceShipmentItem.setCompanyId(CompanyThreadLocal.getCompanyId());

		return commerceShipmentItem;
	}

	/**
	 * Removes the commerce shipment item with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceShipmentItemId the primary key of the commerce shipment item
	 * @return the commerce shipment item that was removed
	 * @throws NoSuchShipmentItemException if a commerce shipment item with the primary key could not be found
	 */
	@Override
	public CommerceShipmentItem remove(long commerceShipmentItemId)
		throws NoSuchShipmentItemException {

		return remove((Serializable)commerceShipmentItemId);
	}

	@Override
	protected CommerceShipmentItem removeImpl(
		CommerceShipmentItem commerceShipmentItem) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceShipmentItem)) {
				commerceShipmentItem = (CommerceShipmentItem)session.get(
					CommerceShipmentItemImpl.class,
					commerceShipmentItem.getPrimaryKeyObj());
			}

			if (commerceShipmentItem != null) {
				session.delete(commerceShipmentItem);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceShipmentItem != null) {
			clearCache(commerceShipmentItem);
		}

		return commerceShipmentItem;
	}

	@Override
	public CommerceShipmentItem updateImpl(
		CommerceShipmentItem commerceShipmentItem) {

		boolean isNew = commerceShipmentItem.isNew();

		if (!(commerceShipmentItem instanceof CommerceShipmentItemModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(commerceShipmentItem.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceShipmentItem);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceShipmentItem proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceShipmentItem implementation " +
					commerceShipmentItem.getClass());
		}

		CommerceShipmentItemModelImpl commerceShipmentItemModelImpl =
			(CommerceShipmentItemModelImpl)commerceShipmentItem;

		if (Validator.isNull(commerceShipmentItem.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			commerceShipmentItem.setUuid(uuid);
		}

		if (Validator.isNull(commerceShipmentItem.getExternalReferenceCode())) {
			commerceShipmentItem.setExternalReferenceCode(
				commerceShipmentItem.getUuid());
		}
		else {
			if (!Objects.equals(
					commerceShipmentItemModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					commerceShipmentItem.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = commerceShipmentItem.getCompanyId();

					long groupId = commerceShipmentItem.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = commerceShipmentItem.getPrimaryKey();
					}

					try {
						commerceShipmentItem.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								CommerceShipmentItem.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								commerceShipmentItem.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			CommerceShipmentItem ercCommerceShipmentItem = fetchByERC_C(
				commerceShipmentItem.getExternalReferenceCode(),
				commerceShipmentItem.getCompanyId());

			if (isNew) {
				if (ercCommerceShipmentItem != null) {
					throw new DuplicateCommerceShipmentItemExternalReferenceCodeException(
						"Duplicate commerce shipment item with external reference code " +
							commerceShipmentItem.getExternalReferenceCode() +
								" and company " +
									commerceShipmentItem.getCompanyId());
				}
			}
			else {
				if ((ercCommerceShipmentItem != null) &&
					(commerceShipmentItem.getCommerceShipmentItemId() !=
						ercCommerceShipmentItem.getCommerceShipmentItemId())) {

					throw new DuplicateCommerceShipmentItemExternalReferenceCodeException(
						"Duplicate commerce shipment item with external reference code " +
							commerceShipmentItem.getExternalReferenceCode() +
								" and company " +
									commerceShipmentItem.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceShipmentItem.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceShipmentItem.setCreateDate(date);
			}
			else {
				commerceShipmentItem.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceShipmentItemModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceShipmentItem.setModifiedDate(date);
			}
			else {
				commerceShipmentItem.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceShipmentItem);
			}
			else {
				commerceShipmentItem = (CommerceShipmentItem)session.merge(
					commerceShipmentItem);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceShipmentItem, false);

		if (isNew) {
			commerceShipmentItem.setNew(false);
		}

		commerceShipmentItem.resetOriginalValues();

		return commerceShipmentItem;
	}

	/**
	 * Returns the commerce shipment item with the primary key or throws a <code>NoSuchShipmentItemException</code> if it could not be found.
	 *
	 * @param commerceShipmentItemId the primary key of the commerce shipment item
	 * @return the commerce shipment item
	 * @throws NoSuchShipmentItemException if a commerce shipment item with the primary key could not be found
	 */
	@Override
	public CommerceShipmentItem findByPrimaryKey(long commerceShipmentItemId)
		throws NoSuchShipmentItemException {

		return findByPrimaryKey((Serializable)commerceShipmentItemId);
	}

	/**
	 * Returns the commerce shipment item with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceShipmentItemId the primary key of the commerce shipment item
	 * @return the commerce shipment item, or <code>null</code> if a commerce shipment item with the primary key could not be found
	 */
	@Override
	public CommerceShipmentItem fetchByPrimaryKey(long commerceShipmentItemId) {
		return fetchByPrimaryKey((Serializable)commerceShipmentItemId);
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
		return "commerceShipmentItemId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCESHIPMENTITEM;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceShipmentItemModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce shipment item persistence.
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
			_SQL_SELECT_COMMERCESHIPMENTITEM_WHERE,
			_SQL_COUNT_COMMERCESHIPMENTITEM_WHERE,
			CommerceShipmentItemModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"commerceShipmentItem.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceShipmentItem::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(CommerceShipmentItem::getUuid),
				CommerceShipmentItem::getGroupId),
			_SQL_SELECT_COMMERCESHIPMENTITEM_WHERE, "",
			new FinderColumn<>(
				"commerceShipmentItem.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceShipmentItem::getUuid),
			new FinderColumn<>(
				"commerceShipmentItem.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, CommerceShipmentItem::getGroupId));

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
				_SQL_SELECT_COMMERCESHIPMENTITEM_WHERE,
				_SQL_COUNT_COMMERCESHIPMENTITEM_WHERE,
				CommerceShipmentItemModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"commerceShipmentItem.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CommerceShipmentItem::getUuid),
				new FinderColumn<>(
					"commerceShipmentItem.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceShipmentItem::getCompanyId));

		_collectionPersistenceFinderByGroupId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGroupId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, false),
				_SQL_SELECT_COMMERCESHIPMENTITEM_WHERE,
				_SQL_COUNT_COMMERCESHIPMENTITEM_WHERE,
				CommerceShipmentItemModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"commerceShipmentItem.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, CommerceShipmentItem::getGroupId));

		_collectionPersistenceFinderByCommerceShipmentId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommerceShipmentId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commerceShipmentId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommerceShipmentId",
					new String[] {Long.class.getName()},
					new String[] {"commerceShipmentId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommerceShipmentId",
					new String[] {Long.class.getName()},
					new String[] {"commerceShipmentId"}, false),
				_SQL_SELECT_COMMERCESHIPMENTITEM_WHERE,
				_SQL_COUNT_COMMERCESHIPMENTITEM_WHERE,
				CommerceShipmentItemModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"commerceShipmentItem.", "commerceShipmentId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceShipmentItem::getCommerceShipmentId));

		_collectionPersistenceFinderByCommerceOrderItemId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommerceOrderItemId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commerceOrderItemId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommerceOrderItemId",
					new String[] {Long.class.getName()},
					new String[] {"commerceOrderItemId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommerceOrderItemId",
					new String[] {Long.class.getName()},
					new String[] {"commerceOrderItemId"}, false),
				_SQL_SELECT_COMMERCESHIPMENTITEM_WHERE,
				_SQL_COUNT_COMMERCESHIPMENTITEM_WHERE,
				CommerceShipmentItemModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"commerceShipmentItem.", "commerceOrderItemId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceShipmentItem::getCommerceOrderItemId));

		_collectionPersistenceFinderByC_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"commerceShipmentId", "commerceOrderItemId"},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"commerceShipmentId", "commerceOrderItemId"},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"commerceShipmentId", "commerceOrderItemId"},
				false),
			_SQL_SELECT_COMMERCESHIPMENTITEM_WHERE,
			_SQL_COUNT_COMMERCESHIPMENTITEM_WHERE,
			CommerceShipmentItemModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"commerceShipmentItem.", "commerceShipmentId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceShipmentItem::getCommerceShipmentId),
			new FinderColumn<>(
				"commerceShipmentItem.", "commerceOrderItemId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceShipmentItem::getCommerceOrderItemId));

		_uniquePersistenceFinderByC_C_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {
					"commerceShipmentId", "commerceOrderItemId",
					"commerceInventoryWarehouseId"
				},
				0, 0, false, CommerceShipmentItem::getCommerceShipmentId,
				CommerceShipmentItem::getCommerceOrderItemId,
				CommerceShipmentItem::getCommerceInventoryWarehouseId),
			_SQL_SELECT_COMMERCESHIPMENTITEM_WHERE, "",
			new FinderColumn<>(
				"commerceShipmentItem.", "commerceShipmentId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceShipmentItem::getCommerceShipmentId),
			new FinderColumn<>(
				"commerceShipmentItem.", "commerceOrderItemId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceShipmentItem::getCommerceOrderItemId),
			new FinderColumn<>(
				"commerceShipmentItem.", "commerceInventoryWarehouseId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceShipmentItem::getCommerceInventoryWarehouseId));

		_collectionPersistenceFinderByC_NotC_GteQ =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_NotC_GteQ",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						BigDecimal.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"commerceShipmentId", "commerceInventoryWarehouseId",
						"quantity"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByC_NotC_GteQ",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						BigDecimal.class.getName()
					},
					new String[] {
						"commerceShipmentId", "commerceInventoryWarehouseId",
						"quantity"
					},
					false),
				_SQL_SELECT_COMMERCESHIPMENTITEM_WHERE,
				_SQL_COUNT_COMMERCESHIPMENTITEM_WHERE,
				CommerceShipmentItemModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"commerceShipmentItem.", "commerceShipmentId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceShipmentItem::getCommerceShipmentId),
				new FinderColumn<>(
					"commerceShipmentItem.", "commerceInventoryWarehouseId",
					FinderColumn.Type.LONG, "!=", true, true,
					CommerceShipmentItem::getCommerceInventoryWarehouseId),
				new FinderColumn<>(
					"commerceShipmentItem.", "quantity",
					FinderColumn.Type.BIG_DECIMAL, ">=", true, true,
					CommerceShipmentItem::getQuantity));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(
					CommerceShipmentItem::getExternalReferenceCode),
				CommerceShipmentItem::getCompanyId),
			_SQL_SELECT_COMMERCESHIPMENTITEM_WHERE, "",
			new FinderColumn<>(
				"commerceShipmentItem.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceShipmentItem::getExternalReferenceCode),
			new FinderColumn<>(
				"commerceShipmentItem.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, CommerceShipmentItem::getCompanyId));

		CommerceShipmentItemUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceShipmentItemUtil.setPersistence(null);

		entityCache.removeCache(CommerceShipmentItemImpl.class.getName());
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
		CommerceShipmentItemModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCESHIPMENTITEM =
		"SELECT commerceShipmentItem FROM CommerceShipmentItem commerceShipmentItem";

	private static final String _SQL_SELECT_COMMERCESHIPMENTITEM_WHERE =
		"SELECT commerceShipmentItem FROM CommerceShipmentItem commerceShipmentItem WHERE ";

	private static final String _SQL_COUNT_COMMERCESHIPMENTITEM_WHERE =
		"SELECT COUNT(commerceShipmentItem) FROM CommerceShipmentItem commerceShipmentItem WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1215410835