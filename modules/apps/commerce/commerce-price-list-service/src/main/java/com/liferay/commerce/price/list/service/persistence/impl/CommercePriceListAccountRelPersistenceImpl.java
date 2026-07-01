/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service.persistence.impl;

import com.liferay.commerce.price.list.exception.NoSuchPriceListAccountRelException;
import com.liferay.commerce.price.list.model.CommercePriceListAccountRel;
import com.liferay.commerce.price.list.model.CommercePriceListAccountRelTable;
import com.liferay.commerce.price.list.model.impl.CommercePriceListAccountRelImpl;
import com.liferay.commerce.price.list.model.impl.CommercePriceListAccountRelModelImpl;
import com.liferay.commerce.price.list.service.persistence.CommercePriceListAccountRelPersistence;
import com.liferay.commerce.price.list.service.persistence.CommercePriceListAccountRelUtil;
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
 * The persistence implementation for the commerce price list account rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(service = CommercePriceListAccountRelPersistence.class)
public class CommercePriceListAccountRelPersistenceImpl
	extends BasePersistenceImpl
		<CommercePriceListAccountRel, NoSuchPriceListAccountRelException>
	implements CommercePriceListAccountRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommercePriceListAccountRelUtil</code> to access the commerce price list account rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommercePriceListAccountRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommercePriceListAccountRel, NoSuchPriceListAccountRelException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the commerce price list account rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePriceListAccountRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce price list account rels
	 * @param end the upper bound of the range of commerce price list account rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce price list account rels
	 */
	@Override
	public List<CommercePriceListAccountRel> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommercePriceListAccountRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce price list account rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price list account rel
	 * @throws NoSuchPriceListAccountRelException if a matching commerce price list account rel could not be found
	 */
	@Override
	public CommercePriceListAccountRel findByUuid_First(
			String uuid,
			OrderByComparator<CommercePriceListAccountRel> orderByComparator)
		throws NoSuchPriceListAccountRelException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first commerce price list account rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price list account rel, or <code>null</code> if a matching commerce price list account rel could not be found
	 */
	@Override
	public CommercePriceListAccountRel fetchByUuid_First(
		String uuid,
		OrderByComparator<CommercePriceListAccountRel> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the commerce price list account rels where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of commerce price list account rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce price list account rels
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<CommercePriceListAccountRel, NoSuchPriceListAccountRelException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the commerce price list account rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePriceListAccountRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce price list account rels
	 * @param end the upper bound of the range of commerce price list account rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce price list account rels
	 */
	@Override
	public List<CommercePriceListAccountRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommercePriceListAccountRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce price list account rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price list account rel
	 * @throws NoSuchPriceListAccountRelException if a matching commerce price list account rel could not be found
	 */
	@Override
	public CommercePriceListAccountRel findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommercePriceListAccountRel> orderByComparator)
		throws NoSuchPriceListAccountRelException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first commerce price list account rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price list account rel, or <code>null</code> if a matching commerce price list account rel could not be found
	 */
	@Override
	public CommercePriceListAccountRel fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommercePriceListAccountRel> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the commerce price list account rels where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of commerce price list account rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce price list account rels
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<CommercePriceListAccountRel, NoSuchPriceListAccountRelException>
			_collectionPersistenceFinderByCommercePriceListId;

	/**
	 * Returns an ordered range of all the commerce price list account rels where commercePriceListId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePriceListAccountRelModelImpl</code>.
	 * </p>
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param start the lower bound of the range of commerce price list account rels
	 * @param end the upper bound of the range of commerce price list account rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce price list account rels
	 */
	@Override
	public List<CommercePriceListAccountRel> findByCommercePriceListId(
		long commercePriceListId, int start, int end,
		OrderByComparator<CommercePriceListAccountRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCommercePriceListId.find(
			finderCache, new Object[] {commercePriceListId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce price list account rel in the ordered set where commercePriceListId = &#63;.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price list account rel
	 * @throws NoSuchPriceListAccountRelException if a matching commerce price list account rel could not be found
	 */
	@Override
	public CommercePriceListAccountRel findByCommercePriceListId_First(
			long commercePriceListId,
			OrderByComparator<CommercePriceListAccountRel> orderByComparator)
		throws NoSuchPriceListAccountRelException {

		return _collectionPersistenceFinderByCommercePriceListId.findFirst(
			finderCache, new Object[] {commercePriceListId}, orderByComparator);
	}

	/**
	 * Returns the first commerce price list account rel in the ordered set where commercePriceListId = &#63;.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price list account rel, or <code>null</code> if a matching commerce price list account rel could not be found
	 */
	@Override
	public CommercePriceListAccountRel fetchByCommercePriceListId_First(
		long commercePriceListId,
		OrderByComparator<CommercePriceListAccountRel> orderByComparator) {

		return _collectionPersistenceFinderByCommercePriceListId.fetchFirst(
			finderCache, new Object[] {commercePriceListId}, orderByComparator);
	}

	/**
	 * Removes all the commerce price list account rels where commercePriceListId = &#63; from the database.
	 *
	 * @param commercePriceListId the commerce price list ID
	 */
	@Override
	public void removeByCommercePriceListId(long commercePriceListId) {
		_collectionPersistenceFinderByCommercePriceListId.remove(
			finderCache, new Object[] {commercePriceListId});
	}

	/**
	 * Returns the number of commerce price list account rels where commercePriceListId = &#63;.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @return the number of matching commerce price list account rels
	 */
	@Override
	public int countByCommercePriceListId(long commercePriceListId) {
		return _collectionPersistenceFinderByCommercePriceListId.count(
			finderCache, new Object[] {commercePriceListId});
	}

	private UniquePersistenceFinder
		<CommercePriceListAccountRel, NoSuchPriceListAccountRelException>
			_uniquePersistenceFinderByCAI_CPI;

	/**
	 * Returns the commerce price list account rel where commerceAccountId = &#63; and commercePriceListId = &#63; or throws a <code>NoSuchPriceListAccountRelException</code> if it could not be found.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commercePriceListId the commerce price list ID
	 * @return the matching commerce price list account rel
	 * @throws NoSuchPriceListAccountRelException if a matching commerce price list account rel could not be found
	 */
	@Override
	public CommercePriceListAccountRel findByCAI_CPI(
			long commerceAccountId, long commercePriceListId)
		throws NoSuchPriceListAccountRelException {

		return _uniquePersistenceFinderByCAI_CPI.find(
			finderCache, new Object[] {commerceAccountId, commercePriceListId});
	}

	/**
	 * Returns the commerce price list account rel where commerceAccountId = &#63; and commercePriceListId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commercePriceListId the commerce price list ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce price list account rel, or <code>null</code> if a matching commerce price list account rel could not be found
	 */
	@Override
	public CommercePriceListAccountRel fetchByCAI_CPI(
		long commerceAccountId, long commercePriceListId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByCAI_CPI.fetch(
			finderCache, new Object[] {commerceAccountId, commercePriceListId},
			useFinderCache);
	}

	/**
	 * Removes the commerce price list account rel where commerceAccountId = &#63; and commercePriceListId = &#63; from the database.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commercePriceListId the commerce price list ID
	 * @return the commerce price list account rel that was removed
	 */
	@Override
	public CommercePriceListAccountRel removeByCAI_CPI(
			long commerceAccountId, long commercePriceListId)
		throws NoSuchPriceListAccountRelException {

		CommercePriceListAccountRel commercePriceListAccountRel = findByCAI_CPI(
			commerceAccountId, commercePriceListId);

		return remove(commercePriceListAccountRel);
	}

	/**
	 * Returns the number of commerce price list account rels where commerceAccountId = &#63; and commercePriceListId = &#63;.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commercePriceListId the commerce price list ID
	 * @return the number of matching commerce price list account rels
	 */
	@Override
	public int countByCAI_CPI(
		long commerceAccountId, long commercePriceListId) {

		return _uniquePersistenceFinderByCAI_CPI.count(
			finderCache, new Object[] {commerceAccountId, commercePriceListId});
	}

	public CommercePriceListAccountRelPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("order", "order_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommercePriceListAccountRel.class);

		setModelImplClass(CommercePriceListAccountRelImpl.class);
		setModelPKClass(long.class);

		setTable(CommercePriceListAccountRelTable.INSTANCE);
	}

	/**
	 * Creates a new commerce price list account rel with the primary key. Does not add the commerce price list account rel to the database.
	 *
	 * @param commercePriceListAccountRelId the primary key for the new commerce price list account rel
	 * @return the new commerce price list account rel
	 */
	@Override
	public CommercePriceListAccountRel create(
		long commercePriceListAccountRelId) {

		CommercePriceListAccountRel commercePriceListAccountRel =
			new CommercePriceListAccountRelImpl();

		commercePriceListAccountRel.setNew(true);
		commercePriceListAccountRel.setPrimaryKey(
			commercePriceListAccountRelId);

		String uuid = PortalUUIDUtil.generate();

		commercePriceListAccountRel.setUuid(uuid);

		commercePriceListAccountRel.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return commercePriceListAccountRel;
	}

	/**
	 * Removes the commerce price list account rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commercePriceListAccountRelId the primary key of the commerce price list account rel
	 * @return the commerce price list account rel that was removed
	 * @throws NoSuchPriceListAccountRelException if a commerce price list account rel with the primary key could not be found
	 */
	@Override
	public CommercePriceListAccountRel remove(
			long commercePriceListAccountRelId)
		throws NoSuchPriceListAccountRelException {

		return remove((Serializable)commercePriceListAccountRelId);
	}

	@Override
	protected CommercePriceListAccountRel removeImpl(
		CommercePriceListAccountRel commercePriceListAccountRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commercePriceListAccountRel)) {
				commercePriceListAccountRel =
					(CommercePriceListAccountRel)session.get(
						CommercePriceListAccountRelImpl.class,
						commercePriceListAccountRel.getPrimaryKeyObj());
			}

			if ((commercePriceListAccountRel != null) &&
				ctPersistenceHelper.isRemove(commercePriceListAccountRel)) {

				session.delete(commercePriceListAccountRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commercePriceListAccountRel != null) {
			clearCache(commercePriceListAccountRel);
		}

		return commercePriceListAccountRel;
	}

	@Override
	public CommercePriceListAccountRel updateImpl(
		CommercePriceListAccountRel commercePriceListAccountRel) {

		boolean isNew = commercePriceListAccountRel.isNew();

		if (!(commercePriceListAccountRel instanceof
				CommercePriceListAccountRelModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					commercePriceListAccountRel.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					commercePriceListAccountRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commercePriceListAccountRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommercePriceListAccountRel implementation " +
					commercePriceListAccountRel.getClass());
		}

		CommercePriceListAccountRelModelImpl
			commercePriceListAccountRelModelImpl =
				(CommercePriceListAccountRelModelImpl)
					commercePriceListAccountRel;

		if (Validator.isNull(commercePriceListAccountRel.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			commercePriceListAccountRel.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commercePriceListAccountRel.getCreateDate() == null)) {
			if (serviceContext == null) {
				commercePriceListAccountRel.setCreateDate(date);
			}
			else {
				commercePriceListAccountRel.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commercePriceListAccountRelModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commercePriceListAccountRel.setModifiedDate(date);
			}
			else {
				commercePriceListAccountRel.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(commercePriceListAccountRel)) {
				if (!isNew) {
					session.evict(
						CommercePriceListAccountRelImpl.class,
						commercePriceListAccountRel.getPrimaryKeyObj());
				}

				session.save(commercePriceListAccountRel);
			}
			else {
				commercePriceListAccountRel =
					(CommercePriceListAccountRel)session.merge(
						commercePriceListAccountRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commercePriceListAccountRel, false);

		if (isNew) {
			commercePriceListAccountRel.setNew(false);
		}

		commercePriceListAccountRel.resetOriginalValues();

		return commercePriceListAccountRel;
	}

	/**
	 * Returns the commerce price list account rel with the primary key or throws a <code>NoSuchPriceListAccountRelException</code> if it could not be found.
	 *
	 * @param commercePriceListAccountRelId the primary key of the commerce price list account rel
	 * @return the commerce price list account rel
	 * @throws NoSuchPriceListAccountRelException if a commerce price list account rel with the primary key could not be found
	 */
	@Override
	public CommercePriceListAccountRel findByPrimaryKey(
			long commercePriceListAccountRelId)
		throws NoSuchPriceListAccountRelException {

		return findByPrimaryKey((Serializable)commercePriceListAccountRelId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the commerce price list account rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commercePriceListAccountRelId the primary key of the commerce price list account rel
	 * @return the commerce price list account rel, or <code>null</code> if a commerce price list account rel with the primary key could not be found
	 */
	@Override
	public CommercePriceListAccountRel fetchByPrimaryKey(
		long commercePriceListAccountRelId) {

		return fetchByPrimaryKey((Serializable)commercePriceListAccountRelId);
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
		return "commercePriceListAccountRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEPRICELISTACCOUNTREL;
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
		return CommercePriceListAccountRelModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CommercePriceListAccountRel";
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
		ctMergeColumnNames.add("commerceAccountId");
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
			Collections.singleton("commercePriceListAccountRelId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"commerceAccountId", "commercePriceListId"});
	}

	/**
	 * Initializes the commerce price list account rel persistence.
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
			_SQL_SELECT_COMMERCEPRICELISTACCOUNTREL_WHERE,
			_SQL_COUNT_COMMERCEPRICELISTACCOUNTREL_WHERE,
			CommercePriceListAccountRelModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"commercePriceListAccountRel.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CommercePriceListAccountRel::getUuid));

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
				_SQL_SELECT_COMMERCEPRICELISTACCOUNTREL_WHERE,
				_SQL_COUNT_COMMERCEPRICELISTACCOUNTREL_WHERE,
				CommercePriceListAccountRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commercePriceListAccountRel.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CommercePriceListAccountRel::getUuid),
				new FinderColumn<>(
					"commercePriceListAccountRel.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CommercePriceListAccountRel::getCompanyId));

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
				_SQL_SELECT_COMMERCEPRICELISTACCOUNTREL_WHERE,
				_SQL_COUNT_COMMERCEPRICELISTACCOUNTREL_WHERE,
				CommercePriceListAccountRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commercePriceListAccountRel.", "commercePriceListId",
					FinderColumn.Type.LONG, "=", true, true,
					CommercePriceListAccountRel::getCommercePriceListId));

		_uniquePersistenceFinderByCAI_CPI = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByCAI_CPI",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"commerceAccountId", "commercePriceListId"}, 0, 0,
				false, CommercePriceListAccountRel::getCommerceAccountId,
				CommercePriceListAccountRel::getCommercePriceListId),
			_SQL_SELECT_COMMERCEPRICELISTACCOUNTREL_WHERE, "",
			new FinderColumn<>(
				"commercePriceListAccountRel.", "commerceAccountId",
				FinderColumn.Type.LONG, "=", true, true,
				CommercePriceListAccountRel::getCommerceAccountId),
			new FinderColumn<>(
				"commercePriceListAccountRel.", "commercePriceListId",
				FinderColumn.Type.LONG, "=", true, true,
				CommercePriceListAccountRel::getCommercePriceListId));

		CommercePriceListAccountRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommercePriceListAccountRelUtil.setPersistence(null);

		entityCache.removeCache(
			CommercePriceListAccountRelImpl.class.getName());
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
		CommercePriceListAccountRelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCEPRICELISTACCOUNTREL =
		"SELECT commercePriceListAccountRel FROM CommercePriceListAccountRel commercePriceListAccountRel";

	private static final String _SQL_SELECT_COMMERCEPRICELISTACCOUNTREL_WHERE =
		"SELECT commercePriceListAccountRel FROM CommercePriceListAccountRel commercePriceListAccountRel WHERE ";

	private static final String _SQL_COUNT_COMMERCEPRICELISTACCOUNTREL_WHERE =
		"SELECT COUNT(commercePriceListAccountRel) FROM CommercePriceListAccountRel commercePriceListAccountRel WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommercePriceListAccountRel exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommercePriceListAccountRelPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "order"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-306714975