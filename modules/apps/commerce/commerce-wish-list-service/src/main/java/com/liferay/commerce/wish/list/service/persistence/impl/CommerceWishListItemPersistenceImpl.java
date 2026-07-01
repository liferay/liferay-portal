/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.wish.list.service.persistence.impl;

import com.liferay.commerce.wish.list.exception.NoSuchWishListItemException;
import com.liferay.commerce.wish.list.model.CommerceWishListItem;
import com.liferay.commerce.wish.list.model.CommerceWishListItemTable;
import com.liferay.commerce.wish.list.model.impl.CommerceWishListItemImpl;
import com.liferay.commerce.wish.list.model.impl.CommerceWishListItemModelImpl;
import com.liferay.commerce.wish.list.service.persistence.CommerceWishListItemPersistence;
import com.liferay.commerce.wish.list.service.persistence.CommerceWishListItemUtil;
import com.liferay.commerce.wish.list.service.persistence.impl.constants.CommercePersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the commerce wish list item service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Andrea Di Giorgi
 * @generated
 */
@Component(service = CommerceWishListItemPersistence.class)
public class CommerceWishListItemPersistenceImpl
	extends BasePersistenceImpl
		<CommerceWishListItem, NoSuchWishListItemException>
	implements CommerceWishListItemPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceWishListItemUtil</code> to access the commerce wish list item persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceWishListItemImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceWishListItem, NoSuchWishListItemException>
			_collectionPersistenceFinderByCommerceWishListId;

	/**
	 * Returns an ordered range of all the commerce wish list items where commerceWishListId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceWishListItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceWishListId the commerce wish list ID
	 * @param start the lower bound of the range of commerce wish list items
	 * @param end the upper bound of the range of commerce wish list items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce wish list items
	 */
	@Override
	public List<CommerceWishListItem> findByCommerceWishListId(
		long commerceWishListId, int start, int end,
		OrderByComparator<CommerceWishListItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceWishListId.find(
			finderCache, new Object[] {commerceWishListId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce wish list item in the ordered set where commerceWishListId = &#63;.
	 *
	 * @param commerceWishListId the commerce wish list ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce wish list item
	 * @throws NoSuchWishListItemException if a matching commerce wish list item could not be found
	 */
	@Override
	public CommerceWishListItem findByCommerceWishListId_First(
			long commerceWishListId,
			OrderByComparator<CommerceWishListItem> orderByComparator)
		throws NoSuchWishListItemException {

		return _collectionPersistenceFinderByCommerceWishListId.findFirst(
			finderCache, new Object[] {commerceWishListId}, orderByComparator);
	}

	/**
	 * Returns the first commerce wish list item in the ordered set where commerceWishListId = &#63;.
	 *
	 * @param commerceWishListId the commerce wish list ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce wish list item, or <code>null</code> if a matching commerce wish list item could not be found
	 */
	@Override
	public CommerceWishListItem fetchByCommerceWishListId_First(
		long commerceWishListId,
		OrderByComparator<CommerceWishListItem> orderByComparator) {

		return _collectionPersistenceFinderByCommerceWishListId.fetchFirst(
			finderCache, new Object[] {commerceWishListId}, orderByComparator);
	}

	/**
	 * Removes all the commerce wish list items where commerceWishListId = &#63; from the database.
	 *
	 * @param commerceWishListId the commerce wish list ID
	 */
	@Override
	public void removeByCommerceWishListId(long commerceWishListId) {
		_collectionPersistenceFinderByCommerceWishListId.remove(
			finderCache, new Object[] {commerceWishListId});
	}

	/**
	 * Returns the number of commerce wish list items where commerceWishListId = &#63;.
	 *
	 * @param commerceWishListId the commerce wish list ID
	 * @return the number of matching commerce wish list items
	 */
	@Override
	public int countByCommerceWishListId(long commerceWishListId) {
		return _collectionPersistenceFinderByCommerceWishListId.count(
			finderCache, new Object[] {commerceWishListId});
	}

	private CollectionPersistenceFinder
		<CommerceWishListItem, NoSuchWishListItemException>
			_collectionPersistenceFinderByCPInstanceUuid;

	/**
	 * Returns an ordered range of all the commerce wish list items where CPInstanceUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceWishListItemModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param start the lower bound of the range of commerce wish list items
	 * @param end the upper bound of the range of commerce wish list items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce wish list items
	 */
	@Override
	public List<CommerceWishListItem> findByCPInstanceUuid(
		String CPInstanceUuid, int start, int end,
		OrderByComparator<CommerceWishListItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCPInstanceUuid.find(
			finderCache, new Object[] {CPInstanceUuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce wish list item in the ordered set where CPInstanceUuid = &#63;.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce wish list item
	 * @throws NoSuchWishListItemException if a matching commerce wish list item could not be found
	 */
	@Override
	public CommerceWishListItem findByCPInstanceUuid_First(
			String CPInstanceUuid,
			OrderByComparator<CommerceWishListItem> orderByComparator)
		throws NoSuchWishListItemException {

		return _collectionPersistenceFinderByCPInstanceUuid.findFirst(
			finderCache, new Object[] {CPInstanceUuid}, orderByComparator);
	}

	/**
	 * Returns the first commerce wish list item in the ordered set where CPInstanceUuid = &#63;.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce wish list item, or <code>null</code> if a matching commerce wish list item could not be found
	 */
	@Override
	public CommerceWishListItem fetchByCPInstanceUuid_First(
		String CPInstanceUuid,
		OrderByComparator<CommerceWishListItem> orderByComparator) {

		return _collectionPersistenceFinderByCPInstanceUuid.fetchFirst(
			finderCache, new Object[] {CPInstanceUuid}, orderByComparator);
	}

	/**
	 * Removes all the commerce wish list items where CPInstanceUuid = &#63; from the database.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 */
	@Override
	public void removeByCPInstanceUuid(String CPInstanceUuid) {
		_collectionPersistenceFinderByCPInstanceUuid.remove(
			finderCache, new Object[] {CPInstanceUuid});
	}

	/**
	 * Returns the number of commerce wish list items where CPInstanceUuid = &#63;.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @return the number of matching commerce wish list items
	 */
	@Override
	public int countByCPInstanceUuid(String CPInstanceUuid) {
		return _collectionPersistenceFinderByCPInstanceUuid.count(
			finderCache, new Object[] {CPInstanceUuid});
	}

	private CollectionPersistenceFinder
		<CommerceWishListItem, NoSuchWishListItemException>
			_collectionPersistenceFinderByCProductId;

	/**
	 * Returns an ordered range of all the commerce wish list items where CProductId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceWishListItemModelImpl</code>.
	 * </p>
	 *
	 * @param CProductId the c product ID
	 * @param start the lower bound of the range of commerce wish list items
	 * @param end the upper bound of the range of commerce wish list items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce wish list items
	 */
	@Override
	public List<CommerceWishListItem> findByCProductId(
		long CProductId, int start, int end,
		OrderByComparator<CommerceWishListItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCProductId.find(
			finderCache, new Object[] {CProductId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce wish list item in the ordered set where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce wish list item
	 * @throws NoSuchWishListItemException if a matching commerce wish list item could not be found
	 */
	@Override
	public CommerceWishListItem findByCProductId_First(
			long CProductId,
			OrderByComparator<CommerceWishListItem> orderByComparator)
		throws NoSuchWishListItemException {

		return _collectionPersistenceFinderByCProductId.findFirst(
			finderCache, new Object[] {CProductId}, orderByComparator);
	}

	/**
	 * Returns the first commerce wish list item in the ordered set where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce wish list item, or <code>null</code> if a matching commerce wish list item could not be found
	 */
	@Override
	public CommerceWishListItem fetchByCProductId_First(
		long CProductId,
		OrderByComparator<CommerceWishListItem> orderByComparator) {

		return _collectionPersistenceFinderByCProductId.fetchFirst(
			finderCache, new Object[] {CProductId}, orderByComparator);
	}

	/**
	 * Removes all the commerce wish list items where CProductId = &#63; from the database.
	 *
	 * @param CProductId the c product ID
	 */
	@Override
	public void removeByCProductId(long CProductId) {
		_collectionPersistenceFinderByCProductId.remove(
			finderCache, new Object[] {CProductId});
	}

	/**
	 * Returns the number of commerce wish list items where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @return the number of matching commerce wish list items
	 */
	@Override
	public int countByCProductId(long CProductId) {
		return _collectionPersistenceFinderByCProductId.count(
			finderCache, new Object[] {CProductId});
	}

	private CollectionPersistenceFinder
		<CommerceWishListItem, NoSuchWishListItemException>
			_collectionPersistenceFinderByCW_CPI;

	/**
	 * Returns an ordered range of all the commerce wish list items where commerceWishListId = &#63; and CPInstanceUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceWishListItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceWishListId the commerce wish list ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @param start the lower bound of the range of commerce wish list items
	 * @param end the upper bound of the range of commerce wish list items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce wish list items
	 */
	@Override
	public List<CommerceWishListItem> findByCW_CPI(
		long commerceWishListId, String CPInstanceUuid, int start, int end,
		OrderByComparator<CommerceWishListItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCW_CPI.find(
			finderCache, new Object[] {commerceWishListId, CPInstanceUuid},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce wish list item in the ordered set where commerceWishListId = &#63; and CPInstanceUuid = &#63;.
	 *
	 * @param commerceWishListId the commerce wish list ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce wish list item
	 * @throws NoSuchWishListItemException if a matching commerce wish list item could not be found
	 */
	@Override
	public CommerceWishListItem findByCW_CPI_First(
			long commerceWishListId, String CPInstanceUuid,
			OrderByComparator<CommerceWishListItem> orderByComparator)
		throws NoSuchWishListItemException {

		return _collectionPersistenceFinderByCW_CPI.findFirst(
			finderCache, new Object[] {commerceWishListId, CPInstanceUuid},
			orderByComparator);
	}

	/**
	 * Returns the first commerce wish list item in the ordered set where commerceWishListId = &#63; and CPInstanceUuid = &#63;.
	 *
	 * @param commerceWishListId the commerce wish list ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce wish list item, or <code>null</code> if a matching commerce wish list item could not be found
	 */
	@Override
	public CommerceWishListItem fetchByCW_CPI_First(
		long commerceWishListId, String CPInstanceUuid,
		OrderByComparator<CommerceWishListItem> orderByComparator) {

		return _collectionPersistenceFinderByCW_CPI.fetchFirst(
			finderCache, new Object[] {commerceWishListId, CPInstanceUuid},
			orderByComparator);
	}

	/**
	 * Removes all the commerce wish list items where commerceWishListId = &#63; and CPInstanceUuid = &#63; from the database.
	 *
	 * @param commerceWishListId the commerce wish list ID
	 * @param CPInstanceUuid the cp instance uuid
	 */
	@Override
	public void removeByCW_CPI(long commerceWishListId, String CPInstanceUuid) {
		_collectionPersistenceFinderByCW_CPI.remove(
			finderCache, new Object[] {commerceWishListId, CPInstanceUuid});
	}

	/**
	 * Returns the number of commerce wish list items where commerceWishListId = &#63; and CPInstanceUuid = &#63;.
	 *
	 * @param commerceWishListId the commerce wish list ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @return the number of matching commerce wish list items
	 */
	@Override
	public int countByCW_CPI(long commerceWishListId, String CPInstanceUuid) {
		return _collectionPersistenceFinderByCW_CPI.count(
			finderCache, new Object[] {commerceWishListId, CPInstanceUuid});
	}

	private CollectionPersistenceFinder
		<CommerceWishListItem, NoSuchWishListItemException>
			_collectionPersistenceFinderByCW_CP;

	/**
	 * Returns an ordered range of all the commerce wish list items where commerceWishListId = &#63; and CProductId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceWishListItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceWishListId the commerce wish list ID
	 * @param CProductId the c product ID
	 * @param start the lower bound of the range of commerce wish list items
	 * @param end the upper bound of the range of commerce wish list items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce wish list items
	 */
	@Override
	public List<CommerceWishListItem> findByCW_CP(
		long commerceWishListId, long CProductId, int start, int end,
		OrderByComparator<CommerceWishListItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCW_CP.find(
			finderCache, new Object[] {commerceWishListId, CProductId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce wish list item in the ordered set where commerceWishListId = &#63; and CProductId = &#63;.
	 *
	 * @param commerceWishListId the commerce wish list ID
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce wish list item
	 * @throws NoSuchWishListItemException if a matching commerce wish list item could not be found
	 */
	@Override
	public CommerceWishListItem findByCW_CP_First(
			long commerceWishListId, long CProductId,
			OrderByComparator<CommerceWishListItem> orderByComparator)
		throws NoSuchWishListItemException {

		return _collectionPersistenceFinderByCW_CP.findFirst(
			finderCache, new Object[] {commerceWishListId, CProductId},
			orderByComparator);
	}

	/**
	 * Returns the first commerce wish list item in the ordered set where commerceWishListId = &#63; and CProductId = &#63;.
	 *
	 * @param commerceWishListId the commerce wish list ID
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce wish list item, or <code>null</code> if a matching commerce wish list item could not be found
	 */
	@Override
	public CommerceWishListItem fetchByCW_CP_First(
		long commerceWishListId, long CProductId,
		OrderByComparator<CommerceWishListItem> orderByComparator) {

		return _collectionPersistenceFinderByCW_CP.fetchFirst(
			finderCache, new Object[] {commerceWishListId, CProductId},
			orderByComparator);
	}

	/**
	 * Removes all the commerce wish list items where commerceWishListId = &#63; and CProductId = &#63; from the database.
	 *
	 * @param commerceWishListId the commerce wish list ID
	 * @param CProductId the c product ID
	 */
	@Override
	public void removeByCW_CP(long commerceWishListId, long CProductId) {
		_collectionPersistenceFinderByCW_CP.remove(
			finderCache, new Object[] {commerceWishListId, CProductId});
	}

	/**
	 * Returns the number of commerce wish list items where commerceWishListId = &#63; and CProductId = &#63;.
	 *
	 * @param commerceWishListId the commerce wish list ID
	 * @param CProductId the c product ID
	 * @return the number of matching commerce wish list items
	 */
	@Override
	public int countByCW_CP(long commerceWishListId, long CProductId) {
		return _collectionPersistenceFinderByCW_CP.count(
			finderCache, new Object[] {commerceWishListId, CProductId});
	}

	private UniquePersistenceFinder
		<CommerceWishListItem, NoSuchWishListItemException>
			_uniquePersistenceFinderByCW_CPI_CP;

	/**
	 * Returns the commerce wish list item where commerceWishListId = &#63; and CPInstanceUuid = &#63; and CProductId = &#63; or throws a <code>NoSuchWishListItemException</code> if it could not be found.
	 *
	 * @param commerceWishListId the commerce wish list ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @param CProductId the c product ID
	 * @return the matching commerce wish list item
	 * @throws NoSuchWishListItemException if a matching commerce wish list item could not be found
	 */
	@Override
	public CommerceWishListItem findByCW_CPI_CP(
			long commerceWishListId, String CPInstanceUuid, long CProductId)
		throws NoSuchWishListItemException {

		return _uniquePersistenceFinderByCW_CPI_CP.find(
			finderCache,
			new Object[] {commerceWishListId, CPInstanceUuid, CProductId});
	}

	/**
	 * Returns the commerce wish list item where commerceWishListId = &#63; and CPInstanceUuid = &#63; and CProductId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param commerceWishListId the commerce wish list ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @param CProductId the c product ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce wish list item, or <code>null</code> if a matching commerce wish list item could not be found
	 */
	@Override
	public CommerceWishListItem fetchByCW_CPI_CP(
		long commerceWishListId, String CPInstanceUuid, long CProductId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByCW_CPI_CP.fetch(
			finderCache,
			new Object[] {commerceWishListId, CPInstanceUuid, CProductId},
			useFinderCache);
	}

	/**
	 * Removes the commerce wish list item where commerceWishListId = &#63; and CPInstanceUuid = &#63; and CProductId = &#63; from the database.
	 *
	 * @param commerceWishListId the commerce wish list ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @param CProductId the c product ID
	 * @return the commerce wish list item that was removed
	 */
	@Override
	public CommerceWishListItem removeByCW_CPI_CP(
			long commerceWishListId, String CPInstanceUuid, long CProductId)
		throws NoSuchWishListItemException {

		CommerceWishListItem commerceWishListItem = findByCW_CPI_CP(
			commerceWishListId, CPInstanceUuid, CProductId);

		return remove(commerceWishListItem);
	}

	/**
	 * Returns the number of commerce wish list items where commerceWishListId = &#63; and CPInstanceUuid = &#63; and CProductId = &#63;.
	 *
	 * @param commerceWishListId the commerce wish list ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @param CProductId the c product ID
	 * @return the number of matching commerce wish list items
	 */
	@Override
	public int countByCW_CPI_CP(
		long commerceWishListId, String CPInstanceUuid, long CProductId) {

		return _uniquePersistenceFinderByCW_CPI_CP.count(
			finderCache,
			new Object[] {commerceWishListId, CPInstanceUuid, CProductId});
	}

	public CommerceWishListItemPersistenceImpl() {
		setModelClass(CommerceWishListItem.class);

		setModelImplClass(CommerceWishListItemImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceWishListItemTable.INSTANCE);
	}

	/**
	 * Creates a new commerce wish list item with the primary key. Does not add the commerce wish list item to the database.
	 *
	 * @param commerceWishListItemId the primary key for the new commerce wish list item
	 * @return the new commerce wish list item
	 */
	@Override
	public CommerceWishListItem create(long commerceWishListItemId) {
		CommerceWishListItem commerceWishListItem =
			new CommerceWishListItemImpl();

		commerceWishListItem.setNew(true);
		commerceWishListItem.setPrimaryKey(commerceWishListItemId);

		commerceWishListItem.setCompanyId(CompanyThreadLocal.getCompanyId());

		return commerceWishListItem;
	}

	/**
	 * Removes the commerce wish list item with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceWishListItemId the primary key of the commerce wish list item
	 * @return the commerce wish list item that was removed
	 * @throws NoSuchWishListItemException if a commerce wish list item with the primary key could not be found
	 */
	@Override
	public CommerceWishListItem remove(long commerceWishListItemId)
		throws NoSuchWishListItemException {

		return remove((Serializable)commerceWishListItemId);
	}

	@Override
	protected CommerceWishListItem removeImpl(
		CommerceWishListItem commerceWishListItem) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceWishListItem)) {
				commerceWishListItem = (CommerceWishListItem)session.get(
					CommerceWishListItemImpl.class,
					commerceWishListItem.getPrimaryKeyObj());
			}

			if (commerceWishListItem != null) {
				session.delete(commerceWishListItem);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceWishListItem != null) {
			clearCache(commerceWishListItem);
		}

		return commerceWishListItem;
	}

	@Override
	public CommerceWishListItem updateImpl(
		CommerceWishListItem commerceWishListItem) {

		boolean isNew = commerceWishListItem.isNew();

		if (!(commerceWishListItem instanceof CommerceWishListItemModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(commerceWishListItem.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceWishListItem);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceWishListItem proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceWishListItem implementation " +
					commerceWishListItem.getClass());
		}

		CommerceWishListItemModelImpl commerceWishListItemModelImpl =
			(CommerceWishListItemModelImpl)commerceWishListItem;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceWishListItem.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceWishListItem.setCreateDate(date);
			}
			else {
				commerceWishListItem.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceWishListItemModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceWishListItem.setModifiedDate(date);
			}
			else {
				commerceWishListItem.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceWishListItem);
			}
			else {
				commerceWishListItem = (CommerceWishListItem)session.merge(
					commerceWishListItem);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceWishListItem, false);

		if (isNew) {
			commerceWishListItem.setNew(false);
		}

		commerceWishListItem.resetOriginalValues();

		return commerceWishListItem;
	}

	/**
	 * Returns the commerce wish list item with the primary key or throws a <code>NoSuchWishListItemException</code> if it could not be found.
	 *
	 * @param commerceWishListItemId the primary key of the commerce wish list item
	 * @return the commerce wish list item
	 * @throws NoSuchWishListItemException if a commerce wish list item with the primary key could not be found
	 */
	@Override
	public CommerceWishListItem findByPrimaryKey(long commerceWishListItemId)
		throws NoSuchWishListItemException {

		return findByPrimaryKey((Serializable)commerceWishListItemId);
	}

	/**
	 * Returns the commerce wish list item with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceWishListItemId the primary key of the commerce wish list item
	 * @return the commerce wish list item, or <code>null</code> if a commerce wish list item with the primary key could not be found
	 */
	@Override
	public CommerceWishListItem fetchByPrimaryKey(long commerceWishListItemId) {
		return fetchByPrimaryKey((Serializable)commerceWishListItemId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "commerceWishListItemId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEWISHLISTITEM;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceWishListItemModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce wish list item persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByCommerceWishListId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommerceWishListId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commerceWishListId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommerceWishListId",
					new String[] {Long.class.getName()},
					new String[] {"commerceWishListId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommerceWishListId",
					new String[] {Long.class.getName()},
					new String[] {"commerceWishListId"}, false),
				_SQL_SELECT_COMMERCEWISHLISTITEM_WHERE,
				_SQL_COUNT_COMMERCEWISHLISTITEM_WHERE,
				CommerceWishListItemModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceWishListItem.", "commerceWishListId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceWishListItem::getCommerceWishListId));

		_collectionPersistenceFinderByCPInstanceUuid =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCPInstanceUuid",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"CPInstanceUuid"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCPInstanceUuid",
					new String[] {String.class.getName()},
					new String[] {"CPInstanceUuid"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCPInstanceUuid",
					new String[] {String.class.getName()},
					new String[] {"CPInstanceUuid"}, 0, 1, false, null),
				_SQL_SELECT_COMMERCEWISHLISTITEM_WHERE,
				_SQL_COUNT_COMMERCEWISHLISTITEM_WHERE,
				CommerceWishListItemModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceWishListItem.", "CPInstanceUuid",
					FinderColumn.Type.STRING, "=", true, true,
					CommerceWishListItem::getCPInstanceUuid));

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
				_SQL_SELECT_COMMERCEWISHLISTITEM_WHERE,
				_SQL_COUNT_COMMERCEWISHLISTITEM_WHERE,
				CommerceWishListItemModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceWishListItem.", "CProductId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceWishListItem::getCProductId));

		_collectionPersistenceFinderByCW_CPI =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCW_CPI",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commerceWishListId", "CPInstanceUuid"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCW_CPI",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"commerceWishListId", "CPInstanceUuid"}, 0, 2,
					true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCW_CPI",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"commerceWishListId", "CPInstanceUuid"}, 0, 2,
					false, null),
				_SQL_SELECT_COMMERCEWISHLISTITEM_WHERE,
				_SQL_COUNT_COMMERCEWISHLISTITEM_WHERE,
				CommerceWishListItemModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceWishListItem.", "commerceWishListId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceWishListItem::getCommerceWishListId),
				new FinderColumn<>(
					"commerceWishListItem.", "CPInstanceUuid",
					FinderColumn.Type.STRING, "=", true, true,
					CommerceWishListItem::getCPInstanceUuid));

		_collectionPersistenceFinderByCW_CP = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCW_CP",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"commerceWishListId", "CProductId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCW_CP",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"commerceWishListId", "CProductId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCW_CP",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"commerceWishListId", "CProductId"}, false),
			_SQL_SELECT_COMMERCEWISHLISTITEM_WHERE,
			_SQL_COUNT_COMMERCEWISHLISTITEM_WHERE,
			CommerceWishListItemModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"commerceWishListItem.", "commerceWishListId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceWishListItem::getCommerceWishListId),
			new FinderColumn<>(
				"commerceWishListItem.", "CProductId", FinderColumn.Type.LONG,
				"=", true, true, CommerceWishListItem::getCProductId));

		_uniquePersistenceFinderByCW_CPI_CP = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByCW_CPI_CP",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Long.class.getName()
				},
				new String[] {
					"commerceWishListId", "CPInstanceUuid", "CProductId"
				},
				0, 2, false, CommerceWishListItem::getCommerceWishListId,
				convertNullFunction(CommerceWishListItem::getCPInstanceUuid),
				CommerceWishListItem::getCProductId),
			_SQL_SELECT_COMMERCEWISHLISTITEM_WHERE, "",
			new FinderColumn<>(
				"commerceWishListItem.", "commerceWishListId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceWishListItem::getCommerceWishListId),
			new FinderColumn<>(
				"commerceWishListItem.", "CPInstanceUuid",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceWishListItem::getCPInstanceUuid),
			new FinderColumn<>(
				"commerceWishListItem.", "CProductId", FinderColumn.Type.LONG,
				"=", true, true, CommerceWishListItem::getCProductId));

		CommerceWishListItemUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceWishListItemUtil.setPersistence(null);

		entityCache.removeCache(CommerceWishListItemImpl.class.getName());
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
		CommerceWishListItemModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCEWISHLISTITEM =
		"SELECT commerceWishListItem FROM CommerceWishListItem commerceWishListItem";

	private static final String _SQL_SELECT_COMMERCEWISHLISTITEM_WHERE =
		"SELECT commerceWishListItem FROM CommerceWishListItem commerceWishListItem WHERE ";

	private static final String _SQL_COUNT_COMMERCEWISHLISTITEM_WHERE =
		"SELECT COUNT(commerceWishListItem) FROM CommerceWishListItem commerceWishListItem WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommerceWishListItem exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceWishListItemPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-826781943