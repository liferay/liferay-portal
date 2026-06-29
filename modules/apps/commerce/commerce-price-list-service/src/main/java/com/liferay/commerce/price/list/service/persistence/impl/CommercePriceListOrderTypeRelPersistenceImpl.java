/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service.persistence.impl;

import com.liferay.commerce.price.list.exception.NoSuchPriceListOrderTypeRelException;
import com.liferay.commerce.price.list.model.CommercePriceListOrderTypeRel;
import com.liferay.commerce.price.list.model.CommercePriceListOrderTypeRelTable;
import com.liferay.commerce.price.list.model.impl.CommercePriceListOrderTypeRelImpl;
import com.liferay.commerce.price.list.model.impl.CommercePriceListOrderTypeRelModelImpl;
import com.liferay.commerce.price.list.service.persistence.CommercePriceListOrderTypeRelPersistence;
import com.liferay.commerce.price.list.service.persistence.CommercePriceListOrderTypeRelUtil;
import com.liferay.commerce.price.list.service.persistence.impl.constants.CommercePersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
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
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the commerce price list order type rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(service = CommercePriceListOrderTypeRelPersistence.class)
public class CommercePriceListOrderTypeRelPersistenceImpl
	extends BasePersistenceImpl
		<CommercePriceListOrderTypeRel, NoSuchPriceListOrderTypeRelException>
	implements CommercePriceListOrderTypeRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommercePriceListOrderTypeRelUtil</code> to access the commerce price list order type rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommercePriceListOrderTypeRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommercePriceListOrderTypeRel, NoSuchPriceListOrderTypeRelException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the commerce price list order type rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePriceListOrderTypeRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce price list order type rels
	 * @param end the upper bound of the range of commerce price list order type rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce price list order type rels
	 */
	@Override
	public List<CommercePriceListOrderTypeRel> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommercePriceListOrderTypeRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce price list order type rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price list order type rel
	 * @throws NoSuchPriceListOrderTypeRelException if a matching commerce price list order type rel could not be found
	 */
	@Override
	public CommercePriceListOrderTypeRel findByUuid_First(
			String uuid,
			OrderByComparator<CommercePriceListOrderTypeRel> orderByComparator)
		throws NoSuchPriceListOrderTypeRelException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first commerce price list order type rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price list order type rel, or <code>null</code> if a matching commerce price list order type rel could not be found
	 */
	@Override
	public CommercePriceListOrderTypeRel fetchByUuid_First(
		String uuid,
		OrderByComparator<CommercePriceListOrderTypeRel> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the commerce price list order type rels where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of commerce price list order type rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce price list order type rels
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<CommercePriceListOrderTypeRel, NoSuchPriceListOrderTypeRelException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the commerce price list order type rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePriceListOrderTypeRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce price list order type rels
	 * @param end the upper bound of the range of commerce price list order type rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce price list order type rels
	 */
	@Override
	public List<CommercePriceListOrderTypeRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommercePriceListOrderTypeRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce price list order type rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price list order type rel
	 * @throws NoSuchPriceListOrderTypeRelException if a matching commerce price list order type rel could not be found
	 */
	@Override
	public CommercePriceListOrderTypeRel findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommercePriceListOrderTypeRel> orderByComparator)
		throws NoSuchPriceListOrderTypeRelException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first commerce price list order type rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price list order type rel, or <code>null</code> if a matching commerce price list order type rel could not be found
	 */
	@Override
	public CommercePriceListOrderTypeRel fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommercePriceListOrderTypeRel> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the commerce price list order type rels where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of commerce price list order type rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce price list order type rels
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<CommercePriceListOrderTypeRel, NoSuchPriceListOrderTypeRelException>
			_collectionPersistenceFinderByCommercePriceListId;

	/**
	 * Returns an ordered range of all the commerce price list order type rels where commercePriceListId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePriceListOrderTypeRelModelImpl</code>.
	 * </p>
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param start the lower bound of the range of commerce price list order type rels
	 * @param end the upper bound of the range of commerce price list order type rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce price list order type rels
	 */
	@Override
	public List<CommercePriceListOrderTypeRel> findByCommercePriceListId(
		long commercePriceListId, int start, int end,
		OrderByComparator<CommercePriceListOrderTypeRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCommercePriceListId.find(
			finderCache, new Object[] {commercePriceListId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce price list order type rel in the ordered set where commercePriceListId = &#63;.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price list order type rel
	 * @throws NoSuchPriceListOrderTypeRelException if a matching commerce price list order type rel could not be found
	 */
	@Override
	public CommercePriceListOrderTypeRel findByCommercePriceListId_First(
			long commercePriceListId,
			OrderByComparator<CommercePriceListOrderTypeRel> orderByComparator)
		throws NoSuchPriceListOrderTypeRelException {

		return _collectionPersistenceFinderByCommercePriceListId.findFirst(
			finderCache, new Object[] {commercePriceListId}, orderByComparator);
	}

	/**
	 * Returns the first commerce price list order type rel in the ordered set where commercePriceListId = &#63;.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price list order type rel, or <code>null</code> if a matching commerce price list order type rel could not be found
	 */
	@Override
	public CommercePriceListOrderTypeRel fetchByCommercePriceListId_First(
		long commercePriceListId,
		OrderByComparator<CommercePriceListOrderTypeRel> orderByComparator) {

		return _collectionPersistenceFinderByCommercePriceListId.fetchFirst(
			finderCache, new Object[] {commercePriceListId}, orderByComparator);
	}

	/**
	 * Removes all the commerce price list order type rels where commercePriceListId = &#63; from the database.
	 *
	 * @param commercePriceListId the commerce price list ID
	 */
	@Override
	public void removeByCommercePriceListId(long commercePriceListId) {
		_collectionPersistenceFinderByCommercePriceListId.remove(
			finderCache, new Object[] {commercePriceListId});
	}

	/**
	 * Returns the number of commerce price list order type rels where commercePriceListId = &#63;.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @return the number of matching commerce price list order type rels
	 */
	@Override
	public int countByCommercePriceListId(long commercePriceListId) {
		return _collectionPersistenceFinderByCommercePriceListId.count(
			finderCache, new Object[] {commercePriceListId});
	}

	private UniquePersistenceFinder
		<CommercePriceListOrderTypeRel, NoSuchPriceListOrderTypeRelException>
			_uniquePersistenceFinderByCPI_COTI;

	/**
	 * Returns the commerce price list order type rel where commercePriceListId = &#63; and commerceOrderTypeId = &#63; or throws a <code>NoSuchPriceListOrderTypeRelException</code> if it could not be found.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param commerceOrderTypeId the commerce order type ID
	 * @return the matching commerce price list order type rel
	 * @throws NoSuchPriceListOrderTypeRelException if a matching commerce price list order type rel could not be found
	 */
	@Override
	public CommercePriceListOrderTypeRel findByCPI_COTI(
			long commercePriceListId, long commerceOrderTypeId)
		throws NoSuchPriceListOrderTypeRelException {

		return _uniquePersistenceFinderByCPI_COTI.find(
			finderCache,
			new Object[] {commercePriceListId, commerceOrderTypeId});
	}

	/**
	 * Returns the commerce price list order type rel where commercePriceListId = &#63; and commerceOrderTypeId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param commerceOrderTypeId the commerce order type ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce price list order type rel, or <code>null</code> if a matching commerce price list order type rel could not be found
	 */
	@Override
	public CommercePriceListOrderTypeRel fetchByCPI_COTI(
		long commercePriceListId, long commerceOrderTypeId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByCPI_COTI.fetch(
			finderCache,
			new Object[] {commercePriceListId, commerceOrderTypeId},
			useFinderCache);
	}

	/**
	 * Removes the commerce price list order type rel where commercePriceListId = &#63; and commerceOrderTypeId = &#63; from the database.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param commerceOrderTypeId the commerce order type ID
	 * @return the commerce price list order type rel that was removed
	 */
	@Override
	public CommercePriceListOrderTypeRel removeByCPI_COTI(
			long commercePriceListId, long commerceOrderTypeId)
		throws NoSuchPriceListOrderTypeRelException {

		CommercePriceListOrderTypeRel commercePriceListOrderTypeRel =
			findByCPI_COTI(commercePriceListId, commerceOrderTypeId);

		return remove(commercePriceListOrderTypeRel);
	}

	/**
	 * Returns the number of commerce price list order type rels where commercePriceListId = &#63; and commerceOrderTypeId = &#63;.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param commerceOrderTypeId the commerce order type ID
	 * @return the number of matching commerce price list order type rels
	 */
	@Override
	public int countByCPI_COTI(
		long commercePriceListId, long commerceOrderTypeId) {

		return _uniquePersistenceFinderByCPI_COTI.count(
			finderCache,
			new Object[] {commercePriceListId, commerceOrderTypeId});
	}

	public CommercePriceListOrderTypeRelPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put(
			"commercePriceListOrderTypeRelId", "CPriceListOrderTypeRelId");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommercePriceListOrderTypeRel.class);

		setModelImplClass(CommercePriceListOrderTypeRelImpl.class);
		setModelPKClass(long.class);

		setTable(CommercePriceListOrderTypeRelTable.INSTANCE);
	}

	/**
	 * Creates a new commerce price list order type rel with the primary key. Does not add the commerce price list order type rel to the database.
	 *
	 * @param commercePriceListOrderTypeRelId the primary key for the new commerce price list order type rel
	 * @return the new commerce price list order type rel
	 */
	@Override
	public CommercePriceListOrderTypeRel create(
		long commercePriceListOrderTypeRelId) {

		CommercePriceListOrderTypeRel commercePriceListOrderTypeRel =
			new CommercePriceListOrderTypeRelImpl();

		commercePriceListOrderTypeRel.setNew(true);
		commercePriceListOrderTypeRel.setPrimaryKey(
			commercePriceListOrderTypeRelId);

		String uuid = PortalUUIDUtil.generate();

		commercePriceListOrderTypeRel.setUuid(uuid);

		commercePriceListOrderTypeRel.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return commercePriceListOrderTypeRel;
	}

	/**
	 * Removes the commerce price list order type rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commercePriceListOrderTypeRelId the primary key of the commerce price list order type rel
	 * @return the commerce price list order type rel that was removed
	 * @throws NoSuchPriceListOrderTypeRelException if a commerce price list order type rel with the primary key could not be found
	 */
	@Override
	public CommercePriceListOrderTypeRel remove(
			long commercePriceListOrderTypeRelId)
		throws NoSuchPriceListOrderTypeRelException {

		return remove((Serializable)commercePriceListOrderTypeRelId);
	}

	@Override
	protected CommercePriceListOrderTypeRel removeImpl(
		CommercePriceListOrderTypeRel commercePriceListOrderTypeRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commercePriceListOrderTypeRel)) {
				commercePriceListOrderTypeRel =
					(CommercePriceListOrderTypeRel)session.get(
						CommercePriceListOrderTypeRelImpl.class,
						commercePriceListOrderTypeRel.getPrimaryKeyObj());
			}

			if ((commercePriceListOrderTypeRel != null) &&
				ctPersistenceHelper.isRemove(commercePriceListOrderTypeRel)) {

				session.delete(commercePriceListOrderTypeRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commercePriceListOrderTypeRel != null) {
			clearCache(commercePriceListOrderTypeRel);
		}

		return commercePriceListOrderTypeRel;
	}

	@Override
	public CommercePriceListOrderTypeRel updateImpl(
		CommercePriceListOrderTypeRel commercePriceListOrderTypeRel) {

		boolean isNew = commercePriceListOrderTypeRel.isNew();

		if (!(commercePriceListOrderTypeRel instanceof
				CommercePriceListOrderTypeRelModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					commercePriceListOrderTypeRel.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					commercePriceListOrderTypeRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commercePriceListOrderTypeRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommercePriceListOrderTypeRel implementation " +
					commercePriceListOrderTypeRel.getClass());
		}

		CommercePriceListOrderTypeRelModelImpl
			commercePriceListOrderTypeRelModelImpl =
				(CommercePriceListOrderTypeRelModelImpl)
					commercePriceListOrderTypeRel;

		if (Validator.isNull(commercePriceListOrderTypeRel.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			commercePriceListOrderTypeRel.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commercePriceListOrderTypeRel.getCreateDate() == null)) {
			if (serviceContext == null) {
				commercePriceListOrderTypeRel.setCreateDate(date);
			}
			else {
				commercePriceListOrderTypeRel.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commercePriceListOrderTypeRelModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commercePriceListOrderTypeRel.setModifiedDate(date);
			}
			else {
				commercePriceListOrderTypeRel.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(commercePriceListOrderTypeRel)) {
				if (!isNew) {
					session.evict(
						CommercePriceListOrderTypeRelImpl.class,
						commercePriceListOrderTypeRel.getPrimaryKeyObj());
				}

				session.save(commercePriceListOrderTypeRel);
			}
			else {
				commercePriceListOrderTypeRel =
					(CommercePriceListOrderTypeRel)session.merge(
						commercePriceListOrderTypeRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commercePriceListOrderTypeRel, false);

		if (isNew) {
			commercePriceListOrderTypeRel.setNew(false);
		}

		commercePriceListOrderTypeRel.resetOriginalValues();

		return commercePriceListOrderTypeRel;
	}

	/**
	 * Returns the commerce price list order type rel with the primary key or throws a <code>NoSuchPriceListOrderTypeRelException</code> if it could not be found.
	 *
	 * @param commercePriceListOrderTypeRelId the primary key of the commerce price list order type rel
	 * @return the commerce price list order type rel
	 * @throws NoSuchPriceListOrderTypeRelException if a commerce price list order type rel with the primary key could not be found
	 */
	@Override
	public CommercePriceListOrderTypeRel findByPrimaryKey(
			long commercePriceListOrderTypeRelId)
		throws NoSuchPriceListOrderTypeRelException {

		return findByPrimaryKey((Serializable)commercePriceListOrderTypeRelId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the commerce price list order type rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commercePriceListOrderTypeRelId the primary key of the commerce price list order type rel
	 * @return the commerce price list order type rel, or <code>null</code> if a commerce price list order type rel with the primary key could not be found
	 */
	@Override
	public CommercePriceListOrderTypeRel fetchByPrimaryKey(
		long commercePriceListOrderTypeRelId) {

		return fetchByPrimaryKey((Serializable)commercePriceListOrderTypeRelId);
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
		return "CPriceListOrderTypeRelId";
	}

	@Override
	protected String getPKFieldName() {
		return "commercePriceListOrderTypeRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEPRICELISTORDERTYPEREL;
	}

	@Override
	public Set<String> getCTColumnNames(
		CTColumnResolutionType ctColumnResolutionType) {

		return _ctColumnNamesMap.getOrDefault(
			ctColumnResolutionType, Collections.emptySet());
	}

	@Override
	public List<String> getMappingTableNames() {
		return _mappingTableNames;
	}

	@Override
	public Map<String, Integer> getTableColumnsMap() {
		return CommercePriceListOrderTypeRelModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CommercePriceListOrderTypeRel";
	}

	@Override
	public List<String[]> getUniqueIndexColumnNames() {
		return _uniqueIndexColumnNames;
	}

	private static final Map<CTColumnResolutionType, Set<String>>
		_ctColumnNamesMap = new EnumMap<CTColumnResolutionType, Set<String>>(
			CTColumnResolutionType.class);
	private static final List<String> _mappingTableNames =
		new ArrayList<String>();
	private static final List<String[]> _uniqueIndexColumnNames =
		new ArrayList<String[]>();

	static {
		Set<String> ctControlColumnNames = new HashSet<String>();
		Set<String> ctIgnoreColumnNames = new HashSet<String>();
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("uuid_");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("commercePriceListId");
		ctMergeColumnNames.add("commerceOrderTypeId");
		ctMergeColumnNames.add("priority");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("CPriceListOrderTypeRelId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"commercePriceListId", "commerceOrderTypeId"});
	}

	/**
	 * Initializes the commerce price list order type rel persistence.
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
			_SQL_SELECT_COMMERCEPRICELISTORDERTYPEREL_WHERE,
			_SQL_COUNT_COMMERCEPRICELISTORDERTYPEREL_WHERE,
			CommercePriceListOrderTypeRelModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"commercePriceListOrderTypeRel.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CommercePriceListOrderTypeRel::getUuid));

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
				_SQL_SELECT_COMMERCEPRICELISTORDERTYPEREL_WHERE,
				_SQL_COUNT_COMMERCEPRICELISTORDERTYPEREL_WHERE,
				CommercePriceListOrderTypeRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commercePriceListOrderTypeRel.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CommercePriceListOrderTypeRel::getUuid),
				new FinderColumn<>(
					"commercePriceListOrderTypeRel.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CommercePriceListOrderTypeRel::getCompanyId));

		_collectionPersistenceFinderByCommercePriceListId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommercePriceListId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commercePriceListId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommercePriceListId",
					new String[] {Long.class.getName()},
					new String[] {"commercePriceListId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommercePriceListId",
					new String[] {Long.class.getName()},
					new String[] {"commercePriceListId"}, false),
				_SQL_SELECT_COMMERCEPRICELISTORDERTYPEREL_WHERE,
				_SQL_COUNT_COMMERCEPRICELISTORDERTYPEREL_WHERE,
				CommercePriceListOrderTypeRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commercePriceListOrderTypeRel.", "commercePriceListId",
					FinderColumn.Type.LONG, "=", true, true,
					CommercePriceListOrderTypeRel::getCommercePriceListId));

		_uniquePersistenceFinderByCPI_COTI = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByCPI_COTI",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"commercePriceListId", "commerceOrderTypeId"}, 0,
				0, false, CommercePriceListOrderTypeRel::getCommercePriceListId,
				CommercePriceListOrderTypeRel::getCommerceOrderTypeId),
			_SQL_SELECT_COMMERCEPRICELISTORDERTYPEREL_WHERE, "",
			new FinderColumn<>(
				"commercePriceListOrderTypeRel.", "commercePriceListId",
				FinderColumn.Type.LONG, "=", true, true,
				CommercePriceListOrderTypeRel::getCommercePriceListId),
			new FinderColumn<>(
				"commercePriceListOrderTypeRel.", "commerceOrderTypeId",
				FinderColumn.Type.LONG, "=", true, true,
				CommercePriceListOrderTypeRel::getCommerceOrderTypeId));

		CommercePriceListOrderTypeRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommercePriceListOrderTypeRelUtil.setPersistence(null);

		entityCache.removeCache(
			CommercePriceListOrderTypeRelImpl.class.getName());
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
	protected CTPersistenceHelper ctPersistenceHelper;

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		CommercePriceListOrderTypeRelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCEPRICELISTORDERTYPEREL =
		"SELECT commercePriceListOrderTypeRel FROM CommercePriceListOrderTypeRel commercePriceListOrderTypeRel";

	private static final String
		_SQL_SELECT_COMMERCEPRICELISTORDERTYPEREL_WHERE =
			"SELECT commercePriceListOrderTypeRel FROM CommercePriceListOrderTypeRel commercePriceListOrderTypeRel WHERE ";

	private static final String _SQL_COUNT_COMMERCEPRICELISTORDERTYPEREL_WHERE =
		"SELECT COUNT(commercePriceListOrderTypeRel) FROM CommercePriceListOrderTypeRel commercePriceListOrderTypeRel WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommercePriceListOrderTypeRel exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommercePriceListOrderTypeRelPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "commercePriceListOrderTypeRelId"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:309910795