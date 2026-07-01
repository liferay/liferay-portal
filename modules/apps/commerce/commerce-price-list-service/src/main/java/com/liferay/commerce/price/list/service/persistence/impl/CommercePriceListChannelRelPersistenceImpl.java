/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service.persistence.impl;

import com.liferay.commerce.price.list.exception.NoSuchPriceListChannelRelException;
import com.liferay.commerce.price.list.model.CommercePriceListChannelRel;
import com.liferay.commerce.price.list.model.CommercePriceListChannelRelTable;
import com.liferay.commerce.price.list.model.impl.CommercePriceListChannelRelImpl;
import com.liferay.commerce.price.list.model.impl.CommercePriceListChannelRelModelImpl;
import com.liferay.commerce.price.list.service.persistence.CommercePriceListChannelRelPersistence;
import com.liferay.commerce.price.list.service.persistence.CommercePriceListChannelRelUtil;
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
 * The persistence implementation for the commerce price list channel rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(service = CommercePriceListChannelRelPersistence.class)
public class CommercePriceListChannelRelPersistenceImpl
	extends BasePersistenceImpl
		<CommercePriceListChannelRel, NoSuchPriceListChannelRelException>
	implements CommercePriceListChannelRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommercePriceListChannelRelUtil</code> to access the commerce price list channel rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommercePriceListChannelRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommercePriceListChannelRel, NoSuchPriceListChannelRelException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the commerce price list channel rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePriceListChannelRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce price list channel rels
	 * @param end the upper bound of the range of commerce price list channel rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce price list channel rels
	 */
	@Override
	public List<CommercePriceListChannelRel> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommercePriceListChannelRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce price list channel rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price list channel rel
	 * @throws NoSuchPriceListChannelRelException if a matching commerce price list channel rel could not be found
	 */
	@Override
	public CommercePriceListChannelRel findByUuid_First(
			String uuid,
			OrderByComparator<CommercePriceListChannelRel> orderByComparator)
		throws NoSuchPriceListChannelRelException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first commerce price list channel rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price list channel rel, or <code>null</code> if a matching commerce price list channel rel could not be found
	 */
	@Override
	public CommercePriceListChannelRel fetchByUuid_First(
		String uuid,
		OrderByComparator<CommercePriceListChannelRel> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the commerce price list channel rels where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of commerce price list channel rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce price list channel rels
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<CommercePriceListChannelRel, NoSuchPriceListChannelRelException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the commerce price list channel rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePriceListChannelRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce price list channel rels
	 * @param end the upper bound of the range of commerce price list channel rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce price list channel rels
	 */
	@Override
	public List<CommercePriceListChannelRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommercePriceListChannelRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce price list channel rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price list channel rel
	 * @throws NoSuchPriceListChannelRelException if a matching commerce price list channel rel could not be found
	 */
	@Override
	public CommercePriceListChannelRel findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommercePriceListChannelRel> orderByComparator)
		throws NoSuchPriceListChannelRelException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first commerce price list channel rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price list channel rel, or <code>null</code> if a matching commerce price list channel rel could not be found
	 */
	@Override
	public CommercePriceListChannelRel fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommercePriceListChannelRel> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the commerce price list channel rels where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of commerce price list channel rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce price list channel rels
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<CommercePriceListChannelRel, NoSuchPriceListChannelRelException>
			_collectionPersistenceFinderByCommercePriceListId;

	/**
	 * Returns an ordered range of all the commerce price list channel rels where commercePriceListId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePriceListChannelRelModelImpl</code>.
	 * </p>
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param start the lower bound of the range of commerce price list channel rels
	 * @param end the upper bound of the range of commerce price list channel rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce price list channel rels
	 */
	@Override
	public List<CommercePriceListChannelRel> findByCommercePriceListId(
		long commercePriceListId, int start, int end,
		OrderByComparator<CommercePriceListChannelRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCommercePriceListId.find(
			finderCache, new Object[] {commercePriceListId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce price list channel rel in the ordered set where commercePriceListId = &#63;.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price list channel rel
	 * @throws NoSuchPriceListChannelRelException if a matching commerce price list channel rel could not be found
	 */
	@Override
	public CommercePriceListChannelRel findByCommercePriceListId_First(
			long commercePriceListId,
			OrderByComparator<CommercePriceListChannelRel> orderByComparator)
		throws NoSuchPriceListChannelRelException {

		return _collectionPersistenceFinderByCommercePriceListId.findFirst(
			finderCache, new Object[] {commercePriceListId}, orderByComparator);
	}

	/**
	 * Returns the first commerce price list channel rel in the ordered set where commercePriceListId = &#63;.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price list channel rel, or <code>null</code> if a matching commerce price list channel rel could not be found
	 */
	@Override
	public CommercePriceListChannelRel fetchByCommercePriceListId_First(
		long commercePriceListId,
		OrderByComparator<CommercePriceListChannelRel> orderByComparator) {

		return _collectionPersistenceFinderByCommercePriceListId.fetchFirst(
			finderCache, new Object[] {commercePriceListId}, orderByComparator);
	}

	/**
	 * Removes all the commerce price list channel rels where commercePriceListId = &#63; from the database.
	 *
	 * @param commercePriceListId the commerce price list ID
	 */
	@Override
	public void removeByCommercePriceListId(long commercePriceListId) {
		_collectionPersistenceFinderByCommercePriceListId.remove(
			finderCache, new Object[] {commercePriceListId});
	}

	/**
	 * Returns the number of commerce price list channel rels where commercePriceListId = &#63;.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @return the number of matching commerce price list channel rels
	 */
	@Override
	public int countByCommercePriceListId(long commercePriceListId) {
		return _collectionPersistenceFinderByCommercePriceListId.count(
			finderCache, new Object[] {commercePriceListId});
	}

	private UniquePersistenceFinder
		<CommercePriceListChannelRel, NoSuchPriceListChannelRelException>
			_uniquePersistenceFinderByCCI_CPI;

	/**
	 * Returns the commerce price list channel rel where commerceChannelId = &#63; and commercePriceListId = &#63; or throws a <code>NoSuchPriceListChannelRelException</code> if it could not be found.
	 *
	 * @param commerceChannelId the commerce channel ID
	 * @param commercePriceListId the commerce price list ID
	 * @return the matching commerce price list channel rel
	 * @throws NoSuchPriceListChannelRelException if a matching commerce price list channel rel could not be found
	 */
	@Override
	public CommercePriceListChannelRel findByCCI_CPI(
			long commerceChannelId, long commercePriceListId)
		throws NoSuchPriceListChannelRelException {

		return _uniquePersistenceFinderByCCI_CPI.find(
			finderCache, new Object[] {commerceChannelId, commercePriceListId});
	}

	/**
	 * Returns the commerce price list channel rel where commerceChannelId = &#63; and commercePriceListId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param commerceChannelId the commerce channel ID
	 * @param commercePriceListId the commerce price list ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce price list channel rel, or <code>null</code> if a matching commerce price list channel rel could not be found
	 */
	@Override
	public CommercePriceListChannelRel fetchByCCI_CPI(
		long commerceChannelId, long commercePriceListId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByCCI_CPI.fetch(
			finderCache, new Object[] {commerceChannelId, commercePriceListId},
			useFinderCache);
	}

	/**
	 * Removes the commerce price list channel rel where commerceChannelId = &#63; and commercePriceListId = &#63; from the database.
	 *
	 * @param commerceChannelId the commerce channel ID
	 * @param commercePriceListId the commerce price list ID
	 * @return the commerce price list channel rel that was removed
	 */
	@Override
	public CommercePriceListChannelRel removeByCCI_CPI(
			long commerceChannelId, long commercePriceListId)
		throws NoSuchPriceListChannelRelException {

		CommercePriceListChannelRel commercePriceListChannelRel = findByCCI_CPI(
			commerceChannelId, commercePriceListId);

		return remove(commercePriceListChannelRel);
	}

	/**
	 * Returns the number of commerce price list channel rels where commerceChannelId = &#63; and commercePriceListId = &#63;.
	 *
	 * @param commerceChannelId the commerce channel ID
	 * @param commercePriceListId the commerce price list ID
	 * @return the number of matching commerce price list channel rels
	 */
	@Override
	public int countByCCI_CPI(
		long commerceChannelId, long commercePriceListId) {

		return _uniquePersistenceFinderByCCI_CPI.count(
			finderCache, new Object[] {commerceChannelId, commercePriceListId});
	}

	public CommercePriceListChannelRelPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("order", "order_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommercePriceListChannelRel.class);

		setModelImplClass(CommercePriceListChannelRelImpl.class);
		setModelPKClass(long.class);

		setTable(CommercePriceListChannelRelTable.INSTANCE);
	}

	/**
	 * Creates a new commerce price list channel rel with the primary key. Does not add the commerce price list channel rel to the database.
	 *
	 * @param CommercePriceListChannelRelId the primary key for the new commerce price list channel rel
	 * @return the new commerce price list channel rel
	 */
	@Override
	public CommercePriceListChannelRel create(
		long CommercePriceListChannelRelId) {

		CommercePriceListChannelRel commercePriceListChannelRel =
			new CommercePriceListChannelRelImpl();

		commercePriceListChannelRel.setNew(true);
		commercePriceListChannelRel.setPrimaryKey(
			CommercePriceListChannelRelId);

		String uuid = PortalUUIDUtil.generate();

		commercePriceListChannelRel.setUuid(uuid);

		commercePriceListChannelRel.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return commercePriceListChannelRel;
	}

	/**
	 * Removes the commerce price list channel rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CommercePriceListChannelRelId the primary key of the commerce price list channel rel
	 * @return the commerce price list channel rel that was removed
	 * @throws NoSuchPriceListChannelRelException if a commerce price list channel rel with the primary key could not be found
	 */
	@Override
	public CommercePriceListChannelRel remove(
			long CommercePriceListChannelRelId)
		throws NoSuchPriceListChannelRelException {

		return remove((Serializable)CommercePriceListChannelRelId);
	}

	@Override
	protected CommercePriceListChannelRel removeImpl(
		CommercePriceListChannelRel commercePriceListChannelRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commercePriceListChannelRel)) {
				commercePriceListChannelRel =
					(CommercePriceListChannelRel)session.get(
						CommercePriceListChannelRelImpl.class,
						commercePriceListChannelRel.getPrimaryKeyObj());
			}

			if ((commercePriceListChannelRel != null) &&
				ctPersistenceHelper.isRemove(commercePriceListChannelRel)) {

				session.delete(commercePriceListChannelRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commercePriceListChannelRel != null) {
			clearCache(commercePriceListChannelRel);
		}

		return commercePriceListChannelRel;
	}

	@Override
	public CommercePriceListChannelRel updateImpl(
		CommercePriceListChannelRel commercePriceListChannelRel) {

		boolean isNew = commercePriceListChannelRel.isNew();

		if (!(commercePriceListChannelRel instanceof
				CommercePriceListChannelRelModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					commercePriceListChannelRel.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					commercePriceListChannelRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commercePriceListChannelRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommercePriceListChannelRel implementation " +
					commercePriceListChannelRel.getClass());
		}

		CommercePriceListChannelRelModelImpl
			commercePriceListChannelRelModelImpl =
				(CommercePriceListChannelRelModelImpl)
					commercePriceListChannelRel;

		if (Validator.isNull(commercePriceListChannelRel.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			commercePriceListChannelRel.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commercePriceListChannelRel.getCreateDate() == null)) {
			if (serviceContext == null) {
				commercePriceListChannelRel.setCreateDate(date);
			}
			else {
				commercePriceListChannelRel.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commercePriceListChannelRelModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commercePriceListChannelRel.setModifiedDate(date);
			}
			else {
				commercePriceListChannelRel.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(commercePriceListChannelRel)) {
				if (!isNew) {
					session.evict(
						CommercePriceListChannelRelImpl.class,
						commercePriceListChannelRel.getPrimaryKeyObj());
				}

				session.save(commercePriceListChannelRel);
			}
			else {
				commercePriceListChannelRel =
					(CommercePriceListChannelRel)session.merge(
						commercePriceListChannelRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commercePriceListChannelRel, false);

		if (isNew) {
			commercePriceListChannelRel.setNew(false);
		}

		commercePriceListChannelRel.resetOriginalValues();

		return commercePriceListChannelRel;
	}

	/**
	 * Returns the commerce price list channel rel with the primary key or throws a <code>NoSuchPriceListChannelRelException</code> if it could not be found.
	 *
	 * @param CommercePriceListChannelRelId the primary key of the commerce price list channel rel
	 * @return the commerce price list channel rel
	 * @throws NoSuchPriceListChannelRelException if a commerce price list channel rel with the primary key could not be found
	 */
	@Override
	public CommercePriceListChannelRel findByPrimaryKey(
			long CommercePriceListChannelRelId)
		throws NoSuchPriceListChannelRelException {

		return findByPrimaryKey((Serializable)CommercePriceListChannelRelId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the commerce price list channel rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CommercePriceListChannelRelId the primary key of the commerce price list channel rel
	 * @return the commerce price list channel rel, or <code>null</code> if a commerce price list channel rel with the primary key could not be found
	 */
	@Override
	public CommercePriceListChannelRel fetchByPrimaryKey(
		long CommercePriceListChannelRelId) {

		return fetchByPrimaryKey((Serializable)CommercePriceListChannelRelId);
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
		return "CommercePriceListChannelRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEPRICELISTCHANNELREL;
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
		return CommercePriceListChannelRelModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CommercePriceListChannelRel";
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
		ctMergeColumnNames.add("commerceChannelId");
		ctMergeColumnNames.add("commercePriceListId");
		ctMergeColumnNames.add("order_");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("CommercePriceListChannelRelId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"commerceChannelId", "commercePriceListId"});
	}

	/**
	 * Initializes the commerce price list channel rel persistence.
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
			_SQL_SELECT_COMMERCEPRICELISTCHANNELREL_WHERE,
			_SQL_COUNT_COMMERCEPRICELISTCHANNELREL_WHERE,
			CommercePriceListChannelRelModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"commercePriceListChannelRel.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CommercePriceListChannelRel::getUuid));

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
				_SQL_SELECT_COMMERCEPRICELISTCHANNELREL_WHERE,
				_SQL_COUNT_COMMERCEPRICELISTCHANNELREL_WHERE,
				CommercePriceListChannelRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commercePriceListChannelRel.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CommercePriceListChannelRel::getUuid),
				new FinderColumn<>(
					"commercePriceListChannelRel.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CommercePriceListChannelRel::getCompanyId));

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
				_SQL_SELECT_COMMERCEPRICELISTCHANNELREL_WHERE,
				_SQL_COUNT_COMMERCEPRICELISTCHANNELREL_WHERE,
				CommercePriceListChannelRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commercePriceListChannelRel.", "commercePriceListId",
					FinderColumn.Type.LONG, "=", true, true,
					CommercePriceListChannelRel::getCommercePriceListId));

		_uniquePersistenceFinderByCCI_CPI = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByCCI_CPI",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"commerceChannelId", "commercePriceListId"}, 0, 0,
				false, CommercePriceListChannelRel::getCommerceChannelId,
				CommercePriceListChannelRel::getCommercePriceListId),
			_SQL_SELECT_COMMERCEPRICELISTCHANNELREL_WHERE, "",
			new FinderColumn<>(
				"commercePriceListChannelRel.", "commerceChannelId",
				FinderColumn.Type.LONG, "=", true, true,
				CommercePriceListChannelRel::getCommerceChannelId),
			new FinderColumn<>(
				"commercePriceListChannelRel.", "commercePriceListId",
				FinderColumn.Type.LONG, "=", true, true,
				CommercePriceListChannelRel::getCommercePriceListId));

		CommercePriceListChannelRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommercePriceListChannelRelUtil.setPersistence(null);

		entityCache.removeCache(
			CommercePriceListChannelRelImpl.class.getName());
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
		CommercePriceListChannelRelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCEPRICELISTCHANNELREL =
		"SELECT commercePriceListChannelRel FROM CommercePriceListChannelRel commercePriceListChannelRel";

	private static final String _SQL_SELECT_COMMERCEPRICELISTCHANNELREL_WHERE =
		"SELECT commercePriceListChannelRel FROM CommercePriceListChannelRel commercePriceListChannelRel WHERE ";

	private static final String _SQL_COUNT_COMMERCEPRICELISTCHANNELREL_WHERE =
		"SELECT COUNT(commercePriceListChannelRel) FROM CommercePriceListChannelRel commercePriceListChannelRel WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommercePriceListChannelRel exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommercePriceListChannelRelPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "order"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-590047925