/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.service.persistence.impl;

import com.liferay.commerce.inventory.exception.NoSuchInventoryWarehouseRelException;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseRel;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseRelTable;
import com.liferay.commerce.inventory.model.impl.CommerceInventoryWarehouseRelImpl;
import com.liferay.commerce.inventory.model.impl.CommerceInventoryWarehouseRelModelImpl;
import com.liferay.commerce.inventory.service.persistence.CommerceInventoryWarehouseRelPersistence;
import com.liferay.commerce.inventory.service.persistence.CommerceInventoryWarehouseRelUtil;
import com.liferay.commerce.inventory.service.persistence.impl.constants.CommercePersistenceConstants;
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
 * The persistence implementation for the commerce inventory warehouse rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Luca Pellizzon
 * @generated
 */
@Component(service = CommerceInventoryWarehouseRelPersistence.class)
public class CommerceInventoryWarehouseRelPersistenceImpl
	extends BasePersistenceImpl
		<CommerceInventoryWarehouseRel, NoSuchInventoryWarehouseRelException>
	implements CommerceInventoryWarehouseRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceInventoryWarehouseRelUtil</code> to access the commerce inventory warehouse rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceInventoryWarehouseRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceInventoryWarehouseRel, NoSuchInventoryWarehouseRelException>
			_collectionPersistenceFinderByCommerceInventoryWarehouseId;

	/**
	 * Returns an ordered range of all the commerce inventory warehouse rels where commerceInventoryWarehouseId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param start the lower bound of the range of commerce inventory warehouse rels
	 * @param end the upper bound of the range of commerce inventory warehouse rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory warehouse rels
	 */
	@Override
	public List<CommerceInventoryWarehouseRel>
		findByCommerceInventoryWarehouseId(
			long commerceInventoryWarehouseId, int start, int end,
			OrderByComparator<CommerceInventoryWarehouseRel> orderByComparator,
			boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceInventoryWarehouseId.find(
			finderCache, new Object[] {commerceInventoryWarehouseId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce inventory warehouse rel in the ordered set where commerceInventoryWarehouseId = &#63;.
	 *
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory warehouse rel
	 * @throws NoSuchInventoryWarehouseRelException if a matching commerce inventory warehouse rel could not be found
	 */
	@Override
	public CommerceInventoryWarehouseRel
			findByCommerceInventoryWarehouseId_First(
				long commerceInventoryWarehouseId,
				OrderByComparator<CommerceInventoryWarehouseRel>
					orderByComparator)
		throws NoSuchInventoryWarehouseRelException {

		return _collectionPersistenceFinderByCommerceInventoryWarehouseId.
			findFirst(
				finderCache, new Object[] {commerceInventoryWarehouseId},
				orderByComparator);
	}

	/**
	 * Returns the first commerce inventory warehouse rel in the ordered set where commerceInventoryWarehouseId = &#63;.
	 *
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory warehouse rel, or <code>null</code> if a matching commerce inventory warehouse rel could not be found
	 */
	@Override
	public CommerceInventoryWarehouseRel
		fetchByCommerceInventoryWarehouseId_First(
			long commerceInventoryWarehouseId,
			OrderByComparator<CommerceInventoryWarehouseRel>
				orderByComparator) {

		return _collectionPersistenceFinderByCommerceInventoryWarehouseId.
			fetchFirst(
				finderCache, new Object[] {commerceInventoryWarehouseId},
				orderByComparator);
	}

	/**
	 * Removes all the commerce inventory warehouse rels where commerceInventoryWarehouseId = &#63; from the database.
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
	 * Returns the number of commerce inventory warehouse rels where commerceInventoryWarehouseId = &#63;.
	 *
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @return the number of matching commerce inventory warehouse rels
	 */
	@Override
	public int countByCommerceInventoryWarehouseId(
		long commerceInventoryWarehouseId) {

		return _collectionPersistenceFinderByCommerceInventoryWarehouseId.count(
			finderCache, new Object[] {commerceInventoryWarehouseId});
	}

	private CollectionPersistenceFinder
		<CommerceInventoryWarehouseRel, NoSuchInventoryWarehouseRelException>
			_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the commerce inventory warehouse rels where classNameId = &#63; and commerceInventoryWarehouseId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceInventoryWarehouseRelModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param start the lower bound of the range of commerce inventory warehouse rels
	 * @param end the upper bound of the range of commerce inventory warehouse rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory warehouse rels
	 */
	@Override
	public List<CommerceInventoryWarehouseRel> findByC_C(
		long classNameId, long commerceInventoryWarehouseId, int start, int end,
		OrderByComparator<CommerceInventoryWarehouseRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			finderCache,
			new Object[] {classNameId, commerceInventoryWarehouseId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce inventory warehouse rel in the ordered set where classNameId = &#63; and commerceInventoryWarehouseId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory warehouse rel
	 * @throws NoSuchInventoryWarehouseRelException if a matching commerce inventory warehouse rel could not be found
	 */
	@Override
	public CommerceInventoryWarehouseRel findByC_C_First(
			long classNameId, long commerceInventoryWarehouseId,
			OrderByComparator<CommerceInventoryWarehouseRel> orderByComparator)
		throws NoSuchInventoryWarehouseRelException {

		return _collectionPersistenceFinderByC_C.findFirst(
			finderCache,
			new Object[] {classNameId, commerceInventoryWarehouseId},
			orderByComparator);
	}

	/**
	 * Returns the first commerce inventory warehouse rel in the ordered set where classNameId = &#63; and commerceInventoryWarehouseId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory warehouse rel, or <code>null</code> if a matching commerce inventory warehouse rel could not be found
	 */
	@Override
	public CommerceInventoryWarehouseRel fetchByC_C_First(
		long classNameId, long commerceInventoryWarehouseId,
		OrderByComparator<CommerceInventoryWarehouseRel> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			finderCache,
			new Object[] {classNameId, commerceInventoryWarehouseId},
			orderByComparator);
	}

	/**
	 * Removes all the commerce inventory warehouse rels where classNameId = &#63; and commerceInventoryWarehouseId = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 */
	@Override
	public void removeByC_C(
		long classNameId, long commerceInventoryWarehouseId) {

		_collectionPersistenceFinderByC_C.remove(
			finderCache,
			new Object[] {classNameId, commerceInventoryWarehouseId});
	}

	/**
	 * Returns the number of commerce inventory warehouse rels where classNameId = &#63; and commerceInventoryWarehouseId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @return the number of matching commerce inventory warehouse rels
	 */
	@Override
	public int countByC_C(long classNameId, long commerceInventoryWarehouseId) {
		return _collectionPersistenceFinderByC_C.count(
			finderCache,
			new Object[] {classNameId, commerceInventoryWarehouseId});
	}

	private UniquePersistenceFinder
		<CommerceInventoryWarehouseRel, NoSuchInventoryWarehouseRelException>
			_uniquePersistenceFinderByC_C_CIWI;

	/**
	 * Returns the commerce inventory warehouse rel where classNameId = &#63; and classPK = &#63; and commerceInventoryWarehouseId = &#63; or throws a <code>NoSuchInventoryWarehouseRelException</code> if it could not be found.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @return the matching commerce inventory warehouse rel
	 * @throws NoSuchInventoryWarehouseRelException if a matching commerce inventory warehouse rel could not be found
	 */
	@Override
	public CommerceInventoryWarehouseRel findByC_C_CIWI(
			long classNameId, long classPK, long commerceInventoryWarehouseId)
		throws NoSuchInventoryWarehouseRelException {

		return _uniquePersistenceFinderByC_C_CIWI.find(
			finderCache,
			new Object[] {classNameId, classPK, commerceInventoryWarehouseId});
	}

	/**
	 * Returns the commerce inventory warehouse rel where classNameId = &#63; and classPK = &#63; and commerceInventoryWarehouseId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce inventory warehouse rel, or <code>null</code> if a matching commerce inventory warehouse rel could not be found
	 */
	@Override
	public CommerceInventoryWarehouseRel fetchByC_C_CIWI(
		long classNameId, long classPK, long commerceInventoryWarehouseId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByC_C_CIWI.fetch(
			finderCache,
			new Object[] {classNameId, classPK, commerceInventoryWarehouseId},
			useFinderCache);
	}

	/**
	 * Removes the commerce inventory warehouse rel where classNameId = &#63; and classPK = &#63; and commerceInventoryWarehouseId = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @return the commerce inventory warehouse rel that was removed
	 */
	@Override
	public CommerceInventoryWarehouseRel removeByC_C_CIWI(
			long classNameId, long classPK, long commerceInventoryWarehouseId)
		throws NoSuchInventoryWarehouseRelException {

		CommerceInventoryWarehouseRel commerceInventoryWarehouseRel =
			findByC_C_CIWI(classNameId, classPK, commerceInventoryWarehouseId);

		return remove(commerceInventoryWarehouseRel);
	}

	/**
	 * Returns the number of commerce inventory warehouse rels where classNameId = &#63; and classPK = &#63; and commerceInventoryWarehouseId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @return the number of matching commerce inventory warehouse rels
	 */
	@Override
	public int countByC_C_CIWI(
		long classNameId, long classPK, long commerceInventoryWarehouseId) {

		return _uniquePersistenceFinderByC_C_CIWI.count(
			finderCache,
			new Object[] {classNameId, classPK, commerceInventoryWarehouseId});
	}

	public CommerceInventoryWarehouseRelPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put(
			"commerceInventoryWarehouseRelId", "CIWarehouseRelId");
		dbColumnNames.put("commerceInventoryWarehouseId", "CIWarehouseId");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceInventoryWarehouseRel.class);

		setModelImplClass(CommerceInventoryWarehouseRelImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceInventoryWarehouseRelTable.INSTANCE);
	}

	/**
	 * Creates a new commerce inventory warehouse rel with the primary key. Does not add the commerce inventory warehouse rel to the database.
	 *
	 * @param commerceInventoryWarehouseRelId the primary key for the new commerce inventory warehouse rel
	 * @return the new commerce inventory warehouse rel
	 */
	@Override
	public CommerceInventoryWarehouseRel create(
		long commerceInventoryWarehouseRelId) {

		CommerceInventoryWarehouseRel commerceInventoryWarehouseRel =
			new CommerceInventoryWarehouseRelImpl();

		commerceInventoryWarehouseRel.setNew(true);
		commerceInventoryWarehouseRel.setPrimaryKey(
			commerceInventoryWarehouseRelId);

		commerceInventoryWarehouseRel.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return commerceInventoryWarehouseRel;
	}

	/**
	 * Removes the commerce inventory warehouse rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceInventoryWarehouseRelId the primary key of the commerce inventory warehouse rel
	 * @return the commerce inventory warehouse rel that was removed
	 * @throws NoSuchInventoryWarehouseRelException if a commerce inventory warehouse rel with the primary key could not be found
	 */
	@Override
	public CommerceInventoryWarehouseRel remove(
			long commerceInventoryWarehouseRelId)
		throws NoSuchInventoryWarehouseRelException {

		return remove((Serializable)commerceInventoryWarehouseRelId);
	}

	@Override
	protected CommerceInventoryWarehouseRel removeImpl(
		CommerceInventoryWarehouseRel commerceInventoryWarehouseRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceInventoryWarehouseRel)) {
				commerceInventoryWarehouseRel =
					(CommerceInventoryWarehouseRel)session.get(
						CommerceInventoryWarehouseRelImpl.class,
						commerceInventoryWarehouseRel.getPrimaryKeyObj());
			}

			if (commerceInventoryWarehouseRel != null) {
				session.delete(commerceInventoryWarehouseRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceInventoryWarehouseRel != null) {
			clearCache(commerceInventoryWarehouseRel);
		}

		return commerceInventoryWarehouseRel;
	}

	@Override
	public CommerceInventoryWarehouseRel updateImpl(
		CommerceInventoryWarehouseRel commerceInventoryWarehouseRel) {

		boolean isNew = commerceInventoryWarehouseRel.isNew();

		if (!(commerceInventoryWarehouseRel instanceof
				CommerceInventoryWarehouseRelModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					commerceInventoryWarehouseRel.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceInventoryWarehouseRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceInventoryWarehouseRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceInventoryWarehouseRel implementation " +
					commerceInventoryWarehouseRel.getClass());
		}

		CommerceInventoryWarehouseRelModelImpl
			commerceInventoryWarehouseRelModelImpl =
				(CommerceInventoryWarehouseRelModelImpl)
					commerceInventoryWarehouseRel;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceInventoryWarehouseRel.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceInventoryWarehouseRel.setCreateDate(date);
			}
			else {
				commerceInventoryWarehouseRel.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceInventoryWarehouseRelModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceInventoryWarehouseRel.setModifiedDate(date);
			}
			else {
				commerceInventoryWarehouseRel.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceInventoryWarehouseRel);
			}
			else {
				commerceInventoryWarehouseRel =
					(CommerceInventoryWarehouseRel)session.merge(
						commerceInventoryWarehouseRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceInventoryWarehouseRel, false);

		if (isNew) {
			commerceInventoryWarehouseRel.setNew(false);
		}

		commerceInventoryWarehouseRel.resetOriginalValues();

		return commerceInventoryWarehouseRel;
	}

	/**
	 * Returns the commerce inventory warehouse rel with the primary key or throws a <code>NoSuchInventoryWarehouseRelException</code> if it could not be found.
	 *
	 * @param commerceInventoryWarehouseRelId the primary key of the commerce inventory warehouse rel
	 * @return the commerce inventory warehouse rel
	 * @throws NoSuchInventoryWarehouseRelException if a commerce inventory warehouse rel with the primary key could not be found
	 */
	@Override
	public CommerceInventoryWarehouseRel findByPrimaryKey(
			long commerceInventoryWarehouseRelId)
		throws NoSuchInventoryWarehouseRelException {

		return findByPrimaryKey((Serializable)commerceInventoryWarehouseRelId);
	}

	/**
	 * Returns the commerce inventory warehouse rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceInventoryWarehouseRelId the primary key of the commerce inventory warehouse rel
	 * @return the commerce inventory warehouse rel, or <code>null</code> if a commerce inventory warehouse rel with the primary key could not be found
	 */
	@Override
	public CommerceInventoryWarehouseRel fetchByPrimaryKey(
		long commerceInventoryWarehouseRelId) {

		return fetchByPrimaryKey((Serializable)commerceInventoryWarehouseRelId);
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
		return "CIWarehouseRelId";
	}

	@Override
	protected String getPKFieldName() {
		return "commerceInventoryWarehouseRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEINVENTORYWAREHOUSEREL;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceInventoryWarehouseRelModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce inventory warehouse rel persistence.
	 */
	@Activate
	public void activate() {
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
					new String[] {"CIWarehouseId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommerceInventoryWarehouseId",
					new String[] {Long.class.getName()},
					new String[] {"CIWarehouseId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommerceInventoryWarehouseId",
					new String[] {Long.class.getName()},
					new String[] {"CIWarehouseId"}, false),
				_SQL_SELECT_COMMERCEINVENTORYWAREHOUSEREL_WHERE,
				_SQL_COUNT_COMMERCEINVENTORYWAREHOUSEREL_WHERE,
				CommerceInventoryWarehouseRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceInventoryWarehouseRel.",
					"commerceInventoryWarehouseId", "CIWarehouseId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceInventoryWarehouseRel::
						getCommerceInventoryWarehouseId));

		_collectionPersistenceFinderByC_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"classNameId", "CIWarehouseId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "CIWarehouseId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "CIWarehouseId"}, false),
			_SQL_SELECT_COMMERCEINVENTORYWAREHOUSEREL_WHERE,
			_SQL_COUNT_COMMERCEINVENTORYWAREHOUSEREL_WHERE,
			CommerceInventoryWarehouseRelModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"commerceInventoryWarehouseRel.", "classNameId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceInventoryWarehouseRel::getClassNameId),
			new FinderColumn<>(
				"commerceInventoryWarehouseRel.",
				"commerceInventoryWarehouseId", "CIWarehouseId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceInventoryWarehouseRel::
					getCommerceInventoryWarehouseId));

		_uniquePersistenceFinderByC_C_CIWI = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_C_CIWI",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"classNameId", "classPK", "CIWarehouseId"}, 0, 0,
				false, CommerceInventoryWarehouseRel::getClassNameId,
				CommerceInventoryWarehouseRel::getClassPK,
				CommerceInventoryWarehouseRel::getCommerceInventoryWarehouseId),
			_SQL_SELECT_COMMERCEINVENTORYWAREHOUSEREL_WHERE, "",
			new FinderColumn<>(
				"commerceInventoryWarehouseRel.", "classNameId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceInventoryWarehouseRel::getClassNameId),
			new FinderColumn<>(
				"commerceInventoryWarehouseRel.", "classPK",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceInventoryWarehouseRel::getClassPK),
			new FinderColumn<>(
				"commerceInventoryWarehouseRel.",
				"commerceInventoryWarehouseId", "CIWarehouseId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceInventoryWarehouseRel::
					getCommerceInventoryWarehouseId));

		CommerceInventoryWarehouseRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceInventoryWarehouseRelUtil.setPersistence(null);

		entityCache.removeCache(
			CommerceInventoryWarehouseRelImpl.class.getName());
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
		CommerceInventoryWarehouseRelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCEINVENTORYWAREHOUSEREL =
		"SELECT commerceInventoryWarehouseRel FROM CommerceInventoryWarehouseRel commerceInventoryWarehouseRel";

	private static final String
		_SQL_SELECT_COMMERCEINVENTORYWAREHOUSEREL_WHERE =
			"SELECT commerceInventoryWarehouseRel FROM CommerceInventoryWarehouseRel commerceInventoryWarehouseRel WHERE ";

	private static final String _SQL_COUNT_COMMERCEINVENTORYWAREHOUSEREL_WHERE =
		"SELECT COUNT(commerceInventoryWarehouseRel) FROM CommerceInventoryWarehouseRel commerceInventoryWarehouseRel WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommerceInventoryWarehouseRel exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceInventoryWarehouseRelPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {
			"commerceInventoryWarehouseRelId", "commerceInventoryWarehouseId"
		});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1429440903