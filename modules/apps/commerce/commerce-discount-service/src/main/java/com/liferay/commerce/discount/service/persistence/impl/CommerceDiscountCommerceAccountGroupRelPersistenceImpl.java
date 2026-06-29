/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service.persistence.impl;

import com.liferay.commerce.discount.exception.NoSuchDiscountCommerceAccountGroupRelException;
import com.liferay.commerce.discount.model.CommerceDiscountCommerceAccountGroupRel;
import com.liferay.commerce.discount.model.CommerceDiscountCommerceAccountGroupRelTable;
import com.liferay.commerce.discount.model.impl.CommerceDiscountCommerceAccountGroupRelImpl;
import com.liferay.commerce.discount.model.impl.CommerceDiscountCommerceAccountGroupRelModelImpl;
import com.liferay.commerce.discount.service.persistence.CommerceDiscountCommerceAccountGroupRelPersistence;
import com.liferay.commerce.discount.service.persistence.CommerceDiscountCommerceAccountGroupRelUtil;
import com.liferay.commerce.discount.service.persistence.impl.constants.CommercePersistenceConstants;
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
import com.liferay.portal.kernel.util.SetUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the commerce discount commerce account group rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = CommerceDiscountCommerceAccountGroupRelPersistence.class)
public class CommerceDiscountCommerceAccountGroupRelPersistenceImpl
	extends BasePersistenceImpl
		<CommerceDiscountCommerceAccountGroupRel,
		 NoSuchDiscountCommerceAccountGroupRelException>
	implements CommerceDiscountCommerceAccountGroupRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceDiscountCommerceAccountGroupRelUtil</code> to access the commerce discount commerce account group rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceDiscountCommerceAccountGroupRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceDiscountCommerceAccountGroupRel,
		 NoSuchDiscountCommerceAccountGroupRelException>
			_collectionPersistenceFinderByCommerceDiscountId;

	/**
	 * Returns an ordered range of all the commerce discount commerce account group rels where commerceDiscountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountCommerceAccountGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param start the lower bound of the range of commerce discount commerce account group rels
	 * @param end the upper bound of the range of commerce discount commerce account group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discount commerce account group rels
	 */
	@Override
	public List<CommerceDiscountCommerceAccountGroupRel>
		findByCommerceDiscountId(
			long commerceDiscountId, int start, int end,
			OrderByComparator<CommerceDiscountCommerceAccountGroupRel>
				orderByComparator,
			boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceDiscountId.find(
			finderCache, new Object[] {commerceDiscountId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce discount commerce account group rel in the ordered set where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount commerce account group rel
	 * @throws NoSuchDiscountCommerceAccountGroupRelException if a matching commerce discount commerce account group rel could not be found
	 */
	@Override
	public CommerceDiscountCommerceAccountGroupRel
			findByCommerceDiscountId_First(
				long commerceDiscountId,
				OrderByComparator<CommerceDiscountCommerceAccountGroupRel>
					orderByComparator)
		throws NoSuchDiscountCommerceAccountGroupRelException {

		return _collectionPersistenceFinderByCommerceDiscountId.findFirst(
			finderCache, new Object[] {commerceDiscountId}, orderByComparator);
	}

	/**
	 * Returns the first commerce discount commerce account group rel in the ordered set where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount commerce account group rel, or <code>null</code> if a matching commerce discount commerce account group rel could not be found
	 */
	@Override
	public CommerceDiscountCommerceAccountGroupRel
		fetchByCommerceDiscountId_First(
			long commerceDiscountId,
			OrderByComparator<CommerceDiscountCommerceAccountGroupRel>
				orderByComparator) {

		return _collectionPersistenceFinderByCommerceDiscountId.fetchFirst(
			finderCache, new Object[] {commerceDiscountId}, orderByComparator);
	}

	/**
	 * Removes all the commerce discount commerce account group rels where commerceDiscountId = &#63; from the database.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 */
	@Override
	public void removeByCommerceDiscountId(long commerceDiscountId) {
		_collectionPersistenceFinderByCommerceDiscountId.remove(
			finderCache, new Object[] {commerceDiscountId});
	}

	/**
	 * Returns the number of commerce discount commerce account group rels where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @return the number of matching commerce discount commerce account group rels
	 */
	@Override
	public int countByCommerceDiscountId(long commerceDiscountId) {
		return _collectionPersistenceFinderByCommerceDiscountId.count(
			finderCache, new Object[] {commerceDiscountId});
	}

	private CollectionPersistenceFinder
		<CommerceDiscountCommerceAccountGroupRel,
		 NoSuchDiscountCommerceAccountGroupRelException>
			_collectionPersistenceFinderByCommerceAccountGroupId;

	/**
	 * Returns an ordered range of all the commerce discount commerce account group rels where commerceAccountGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountCommerceAccountGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceAccountGroupId the commerce account group ID
	 * @param start the lower bound of the range of commerce discount commerce account group rels
	 * @param end the upper bound of the range of commerce discount commerce account group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discount commerce account group rels
	 */
	@Override
	public List<CommerceDiscountCommerceAccountGroupRel>
		findByCommerceAccountGroupId(
			long commerceAccountGroupId, int start, int end,
			OrderByComparator<CommerceDiscountCommerceAccountGroupRel>
				orderByComparator,
			boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceAccountGroupId.find(
			finderCache, new Object[] {commerceAccountGroupId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce discount commerce account group rel in the ordered set where commerceAccountGroupId = &#63;.
	 *
	 * @param commerceAccountGroupId the commerce account group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount commerce account group rel
	 * @throws NoSuchDiscountCommerceAccountGroupRelException if a matching commerce discount commerce account group rel could not be found
	 */
	@Override
	public CommerceDiscountCommerceAccountGroupRel
			findByCommerceAccountGroupId_First(
				long commerceAccountGroupId,
				OrderByComparator<CommerceDiscountCommerceAccountGroupRel>
					orderByComparator)
		throws NoSuchDiscountCommerceAccountGroupRelException {

		return _collectionPersistenceFinderByCommerceAccountGroupId.findFirst(
			finderCache, new Object[] {commerceAccountGroupId},
			orderByComparator);
	}

	/**
	 * Returns the first commerce discount commerce account group rel in the ordered set where commerceAccountGroupId = &#63;.
	 *
	 * @param commerceAccountGroupId the commerce account group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount commerce account group rel, or <code>null</code> if a matching commerce discount commerce account group rel could not be found
	 */
	@Override
	public CommerceDiscountCommerceAccountGroupRel
		fetchByCommerceAccountGroupId_First(
			long commerceAccountGroupId,
			OrderByComparator<CommerceDiscountCommerceAccountGroupRel>
				orderByComparator) {

		return _collectionPersistenceFinderByCommerceAccountGroupId.fetchFirst(
			finderCache, new Object[] {commerceAccountGroupId},
			orderByComparator);
	}

	/**
	 * Removes all the commerce discount commerce account group rels where commerceAccountGroupId = &#63; from the database.
	 *
	 * @param commerceAccountGroupId the commerce account group ID
	 */
	@Override
	public void removeByCommerceAccountGroupId(long commerceAccountGroupId) {
		_collectionPersistenceFinderByCommerceAccountGroupId.remove(
			finderCache, new Object[] {commerceAccountGroupId});
	}

	/**
	 * Returns the number of commerce discount commerce account group rels where commerceAccountGroupId = &#63;.
	 *
	 * @param commerceAccountGroupId the commerce account group ID
	 * @return the number of matching commerce discount commerce account group rels
	 */
	@Override
	public int countByCommerceAccountGroupId(long commerceAccountGroupId) {
		return _collectionPersistenceFinderByCommerceAccountGroupId.count(
			finderCache, new Object[] {commerceAccountGroupId});
	}

	private UniquePersistenceFinder
		<CommerceDiscountCommerceAccountGroupRel,
		 NoSuchDiscountCommerceAccountGroupRelException>
			_uniquePersistenceFinderByCDI_CAGI;

	/**
	 * Returns the commerce discount commerce account group rel where commerceDiscountId = &#63; and commerceAccountGroupId = &#63; or throws a <code>NoSuchDiscountCommerceAccountGroupRelException</code> if it could not be found.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param commerceAccountGroupId the commerce account group ID
	 * @return the matching commerce discount commerce account group rel
	 * @throws NoSuchDiscountCommerceAccountGroupRelException if a matching commerce discount commerce account group rel could not be found
	 */
	@Override
	public CommerceDiscountCommerceAccountGroupRel findByCDI_CAGI(
			long commerceDiscountId, long commerceAccountGroupId)
		throws NoSuchDiscountCommerceAccountGroupRelException {

		return _uniquePersistenceFinderByCDI_CAGI.find(
			finderCache,
			new Object[] {commerceDiscountId, commerceAccountGroupId});
	}

	/**
	 * Returns the commerce discount commerce account group rel where commerceDiscountId = &#63; and commerceAccountGroupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param commerceAccountGroupId the commerce account group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce discount commerce account group rel, or <code>null</code> if a matching commerce discount commerce account group rel could not be found
	 */
	@Override
	public CommerceDiscountCommerceAccountGroupRel fetchByCDI_CAGI(
		long commerceDiscountId, long commerceAccountGroupId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByCDI_CAGI.fetch(
			finderCache,
			new Object[] {commerceDiscountId, commerceAccountGroupId},
			useFinderCache);
	}

	/**
	 * Removes the commerce discount commerce account group rel where commerceDiscountId = &#63; and commerceAccountGroupId = &#63; from the database.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param commerceAccountGroupId the commerce account group ID
	 * @return the commerce discount commerce account group rel that was removed
	 */
	@Override
	public CommerceDiscountCommerceAccountGroupRel removeByCDI_CAGI(
			long commerceDiscountId, long commerceAccountGroupId)
		throws NoSuchDiscountCommerceAccountGroupRelException {

		CommerceDiscountCommerceAccountGroupRel
			commerceDiscountCommerceAccountGroupRel = findByCDI_CAGI(
				commerceDiscountId, commerceAccountGroupId);

		return remove(commerceDiscountCommerceAccountGroupRel);
	}

	/**
	 * Returns the number of commerce discount commerce account group rels where commerceDiscountId = &#63; and commerceAccountGroupId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param commerceAccountGroupId the commerce account group ID
	 * @return the number of matching commerce discount commerce account group rels
	 */
	@Override
	public int countByCDI_CAGI(
		long commerceDiscountId, long commerceAccountGroupId) {

		return _uniquePersistenceFinderByCDI_CAGI.count(
			finderCache,
			new Object[] {commerceDiscountId, commerceAccountGroupId});
	}

	public CommerceDiscountCommerceAccountGroupRelPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put(
			"commerceDiscountCommerceAccountGroupRelId",
			"CDiscountCAccountGroupRelId");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceDiscountCommerceAccountGroupRel.class);

		setModelImplClass(CommerceDiscountCommerceAccountGroupRelImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceDiscountCommerceAccountGroupRelTable.INSTANCE);
	}

	/**
	 * Creates a new commerce discount commerce account group rel with the primary key. Does not add the commerce discount commerce account group rel to the database.
	 *
	 * @param commerceDiscountCommerceAccountGroupRelId the primary key for the new commerce discount commerce account group rel
	 * @return the new commerce discount commerce account group rel
	 */
	@Override
	public CommerceDiscountCommerceAccountGroupRel create(
		long commerceDiscountCommerceAccountGroupRelId) {

		CommerceDiscountCommerceAccountGroupRel
			commerceDiscountCommerceAccountGroupRel =
				new CommerceDiscountCommerceAccountGroupRelImpl();

		commerceDiscountCommerceAccountGroupRel.setNew(true);
		commerceDiscountCommerceAccountGroupRel.setPrimaryKey(
			commerceDiscountCommerceAccountGroupRelId);

		commerceDiscountCommerceAccountGroupRel.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return commerceDiscountCommerceAccountGroupRel;
	}

	/**
	 * Removes the commerce discount commerce account group rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceDiscountCommerceAccountGroupRelId the primary key of the commerce discount commerce account group rel
	 * @return the commerce discount commerce account group rel that was removed
	 * @throws NoSuchDiscountCommerceAccountGroupRelException if a commerce discount commerce account group rel with the primary key could not be found
	 */
	@Override
	public CommerceDiscountCommerceAccountGroupRel remove(
			long commerceDiscountCommerceAccountGroupRelId)
		throws NoSuchDiscountCommerceAccountGroupRelException {

		return remove((Serializable)commerceDiscountCommerceAccountGroupRelId);
	}

	@Override
	protected CommerceDiscountCommerceAccountGroupRel removeImpl(
		CommerceDiscountCommerceAccountGroupRel
			commerceDiscountCommerceAccountGroupRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceDiscountCommerceAccountGroupRel)) {
				commerceDiscountCommerceAccountGroupRel =
					(CommerceDiscountCommerceAccountGroupRel)session.get(
						CommerceDiscountCommerceAccountGroupRelImpl.class,
						commerceDiscountCommerceAccountGroupRel.
							getPrimaryKeyObj());
			}

			if (commerceDiscountCommerceAccountGroupRel != null) {
				session.delete(commerceDiscountCommerceAccountGroupRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceDiscountCommerceAccountGroupRel != null) {
			clearCache(commerceDiscountCommerceAccountGroupRel);
		}

		return commerceDiscountCommerceAccountGroupRel;
	}

	@Override
	public CommerceDiscountCommerceAccountGroupRel updateImpl(
		CommerceDiscountCommerceAccountGroupRel
			commerceDiscountCommerceAccountGroupRel) {

		boolean isNew = commerceDiscountCommerceAccountGroupRel.isNew();

		if (!(commerceDiscountCommerceAccountGroupRel instanceof
				CommerceDiscountCommerceAccountGroupRelModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					commerceDiscountCommerceAccountGroupRel.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceDiscountCommerceAccountGroupRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceDiscountCommerceAccountGroupRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceDiscountCommerceAccountGroupRel implementation " +
					commerceDiscountCommerceAccountGroupRel.getClass());
		}

		CommerceDiscountCommerceAccountGroupRelModelImpl
			commerceDiscountCommerceAccountGroupRelModelImpl =
				(CommerceDiscountCommerceAccountGroupRelModelImpl)
					commerceDiscountCommerceAccountGroupRel;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew &&
			(commerceDiscountCommerceAccountGroupRel.getCreateDate() == null)) {

			if (serviceContext == null) {
				commerceDiscountCommerceAccountGroupRel.setCreateDate(date);
			}
			else {
				commerceDiscountCommerceAccountGroupRel.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceDiscountCommerceAccountGroupRelModelImpl.
				hasSetModifiedDate()) {

			if (serviceContext == null) {
				commerceDiscountCommerceAccountGroupRel.setModifiedDate(date);
			}
			else {
				commerceDiscountCommerceAccountGroupRel.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceDiscountCommerceAccountGroupRel);
			}
			else {
				commerceDiscountCommerceAccountGroupRel =
					(CommerceDiscountCommerceAccountGroupRel)session.merge(
						commerceDiscountCommerceAccountGroupRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(
			commerceDiscountCommerceAccountGroupRel, false);

		if (isNew) {
			commerceDiscountCommerceAccountGroupRel.setNew(false);
		}

		commerceDiscountCommerceAccountGroupRel.resetOriginalValues();

		return commerceDiscountCommerceAccountGroupRel;
	}

	/**
	 * Returns the commerce discount commerce account group rel with the primary key or throws a <code>NoSuchDiscountCommerceAccountGroupRelException</code> if it could not be found.
	 *
	 * @param commerceDiscountCommerceAccountGroupRelId the primary key of the commerce discount commerce account group rel
	 * @return the commerce discount commerce account group rel
	 * @throws NoSuchDiscountCommerceAccountGroupRelException if a commerce discount commerce account group rel with the primary key could not be found
	 */
	@Override
	public CommerceDiscountCommerceAccountGroupRel findByPrimaryKey(
			long commerceDiscountCommerceAccountGroupRelId)
		throws NoSuchDiscountCommerceAccountGroupRelException {

		return findByPrimaryKey(
			(Serializable)commerceDiscountCommerceAccountGroupRelId);
	}

	/**
	 * Returns the commerce discount commerce account group rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceDiscountCommerceAccountGroupRelId the primary key of the commerce discount commerce account group rel
	 * @return the commerce discount commerce account group rel, or <code>null</code> if a commerce discount commerce account group rel with the primary key could not be found
	 */
	@Override
	public CommerceDiscountCommerceAccountGroupRel fetchByPrimaryKey(
		long commerceDiscountCommerceAccountGroupRelId) {

		return fetchByPrimaryKey(
			(Serializable)commerceDiscountCommerceAccountGroupRelId);
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
		return "CDiscountCAccountGroupRelId";
	}

	@Override
	protected String getPKFieldName() {
		return "commerceDiscountCommerceAccountGroupRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEDISCOUNTCOMMERCEACCOUNTGROUPREL;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceDiscountCommerceAccountGroupRelModelImpl.
			TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce discount commerce account group rel persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByCommerceDiscountId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommerceDiscountId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commerceDiscountId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommerceDiscountId",
					new String[] {Long.class.getName()},
					new String[] {"commerceDiscountId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommerceDiscountId",
					new String[] {Long.class.getName()},
					new String[] {"commerceDiscountId"}, false),
				_SQL_SELECT_COMMERCEDISCOUNTCOMMERCEACCOUNTGROUPREL_WHERE,
				_SQL_COUNT_COMMERCEDISCOUNTCOMMERCEACCOUNTGROUPREL_WHERE,
				CommerceDiscountCommerceAccountGroupRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceDiscountCommerceAccountGroupRel.",
					"commerceDiscountId", FinderColumn.Type.LONG, "=", true,
					true,
					CommerceDiscountCommerceAccountGroupRel::
						getCommerceDiscountId));

		_collectionPersistenceFinderByCommerceAccountGroupId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommerceAccountGroupId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commerceAccountGroupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommerceAccountGroupId",
					new String[] {Long.class.getName()},
					new String[] {"commerceAccountGroupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommerceAccountGroupId",
					new String[] {Long.class.getName()},
					new String[] {"commerceAccountGroupId"}, false),
				_SQL_SELECT_COMMERCEDISCOUNTCOMMERCEACCOUNTGROUPREL_WHERE,
				_SQL_COUNT_COMMERCEDISCOUNTCOMMERCEACCOUNTGROUPREL_WHERE,
				CommerceDiscountCommerceAccountGroupRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceDiscountCommerceAccountGroupRel.",
					"commerceAccountGroupId", FinderColumn.Type.LONG, "=", true,
					true,
					CommerceDiscountCommerceAccountGroupRel::
						getCommerceAccountGroupId));

		_uniquePersistenceFinderByCDI_CAGI = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByCDI_CAGI",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"commerceDiscountId", "commerceAccountGroupId"},
				0, 0, false,
				CommerceDiscountCommerceAccountGroupRel::getCommerceDiscountId,
				CommerceDiscountCommerceAccountGroupRel::
					getCommerceAccountGroupId),
			_SQL_SELECT_COMMERCEDISCOUNTCOMMERCEACCOUNTGROUPREL_WHERE, "",
			new FinderColumn<>(
				"commerceDiscountCommerceAccountGroupRel.",
				"commerceDiscountId", FinderColumn.Type.LONG, "=", true, true,
				CommerceDiscountCommerceAccountGroupRel::getCommerceDiscountId),
			new FinderColumn<>(
				"commerceDiscountCommerceAccountGroupRel.",
				"commerceAccountGroupId", FinderColumn.Type.LONG, "=", true,
				true,
				CommerceDiscountCommerceAccountGroupRel::
					getCommerceAccountGroupId));

		CommerceDiscountCommerceAccountGroupRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceDiscountCommerceAccountGroupRelUtil.setPersistence(null);

		entityCache.removeCache(
			CommerceDiscountCommerceAccountGroupRelImpl.class.getName());
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
		CommerceDiscountCommerceAccountGroupRelModelImpl.ENTITY_ALIAS + ".";

	private static final String
		_SQL_SELECT_COMMERCEDISCOUNTCOMMERCEACCOUNTGROUPREL =
			"SELECT commerceDiscountCommerceAccountGroupRel FROM CommerceDiscountCommerceAccountGroupRel commerceDiscountCommerceAccountGroupRel";

	private static final String
		_SQL_SELECT_COMMERCEDISCOUNTCOMMERCEACCOUNTGROUPREL_WHERE =
			"SELECT commerceDiscountCommerceAccountGroupRel FROM CommerceDiscountCommerceAccountGroupRel commerceDiscountCommerceAccountGroupRel WHERE ";

	private static final String
		_SQL_COUNT_COMMERCEDISCOUNTCOMMERCEACCOUNTGROUPREL_WHERE =
			"SELECT COUNT(commerceDiscountCommerceAccountGroupRel) FROM CommerceDiscountCommerceAccountGroupRel commerceDiscountCommerceAccountGroupRel WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommerceDiscountCommerceAccountGroupRel exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceDiscountCommerceAccountGroupRelPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"commerceDiscountCommerceAccountGroupRelId"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-802974682