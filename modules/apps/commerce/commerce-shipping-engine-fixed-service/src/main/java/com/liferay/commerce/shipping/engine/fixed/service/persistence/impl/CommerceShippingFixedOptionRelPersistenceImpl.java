/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipping.engine.fixed.service.persistence.impl;

import com.liferay.commerce.shipping.engine.fixed.exception.NoSuchShippingFixedOptionRelException;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOptionRel;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOptionRelTable;
import com.liferay.commerce.shipping.engine.fixed.model.impl.CommerceShippingFixedOptionRelImpl;
import com.liferay.commerce.shipping.engine.fixed.model.impl.CommerceShippingFixedOptionRelModelImpl;
import com.liferay.commerce.shipping.engine.fixed.service.persistence.CommerceShippingFixedOptionRelPersistence;
import com.liferay.commerce.shipping.engine.fixed.service.persistence.CommerceShippingFixedOptionRelUtil;
import com.liferay.commerce.shipping.engine.fixed.service.persistence.impl.constants.CommercePersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
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
 * The persistence implementation for the commerce shipping fixed option rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(service = CommerceShippingFixedOptionRelPersistence.class)
public class CommerceShippingFixedOptionRelPersistenceImpl
	extends BasePersistenceImpl
		<CommerceShippingFixedOptionRel, NoSuchShippingFixedOptionRelException>
	implements CommerceShippingFixedOptionRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceShippingFixedOptionRelUtil</code> to access the commerce shipping fixed option rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceShippingFixedOptionRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceShippingFixedOptionRel, NoSuchShippingFixedOptionRelException>
			_collectionPersistenceFinderByCommerceShippingFixedOptionId;

	/**
	 * Returns an ordered range of all the commerce shipping fixed option rels where commerceShippingFixedOptionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShippingFixedOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceShippingFixedOptionId the commerce shipping fixed option ID
	 * @param start the lower bound of the range of commerce shipping fixed option rels
	 * @param end the upper bound of the range of commerce shipping fixed option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipping fixed option rels
	 */
	@Override
	public List<CommerceShippingFixedOptionRel>
		findByCommerceShippingFixedOptionId(
			long commerceShippingFixedOptionId, int start, int end,
			OrderByComparator<CommerceShippingFixedOptionRel> orderByComparator,
			boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceShippingFixedOptionId.find(
			finderCache, new Object[] {commerceShippingFixedOptionId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce shipping fixed option rel in the ordered set where commerceShippingFixedOptionId = &#63;.
	 *
	 * @param commerceShippingFixedOptionId the commerce shipping fixed option ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipping fixed option rel
	 * @throws NoSuchShippingFixedOptionRelException if a matching commerce shipping fixed option rel could not be found
	 */
	@Override
	public CommerceShippingFixedOptionRel
			findByCommerceShippingFixedOptionId_First(
				long commerceShippingFixedOptionId,
				OrderByComparator<CommerceShippingFixedOptionRel>
					orderByComparator)
		throws NoSuchShippingFixedOptionRelException {

		return _collectionPersistenceFinderByCommerceShippingFixedOptionId.
			findFirst(
				finderCache, new Object[] {commerceShippingFixedOptionId},
				orderByComparator);
	}

	/**
	 * Returns the first commerce shipping fixed option rel in the ordered set where commerceShippingFixedOptionId = &#63;.
	 *
	 * @param commerceShippingFixedOptionId the commerce shipping fixed option ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipping fixed option rel, or <code>null</code> if a matching commerce shipping fixed option rel could not be found
	 */
	@Override
	public CommerceShippingFixedOptionRel
		fetchByCommerceShippingFixedOptionId_First(
			long commerceShippingFixedOptionId,
			OrderByComparator<CommerceShippingFixedOptionRel>
				orderByComparator) {

		return _collectionPersistenceFinderByCommerceShippingFixedOptionId.
			fetchFirst(
				finderCache, new Object[] {commerceShippingFixedOptionId},
				orderByComparator);
	}

	/**
	 * Removes all the commerce shipping fixed option rels where commerceShippingFixedOptionId = &#63; from the database.
	 *
	 * @param commerceShippingFixedOptionId the commerce shipping fixed option ID
	 */
	@Override
	public void removeByCommerceShippingFixedOptionId(
		long commerceShippingFixedOptionId) {

		_collectionPersistenceFinderByCommerceShippingFixedOptionId.remove(
			finderCache, new Object[] {commerceShippingFixedOptionId});
	}

	/**
	 * Returns the number of commerce shipping fixed option rels where commerceShippingFixedOptionId = &#63;.
	 *
	 * @param commerceShippingFixedOptionId the commerce shipping fixed option ID
	 * @return the number of matching commerce shipping fixed option rels
	 */
	@Override
	public int countByCommerceShippingFixedOptionId(
		long commerceShippingFixedOptionId) {

		return _collectionPersistenceFinderByCommerceShippingFixedOptionId.
			count(finderCache, new Object[] {commerceShippingFixedOptionId});
	}

	private CollectionPersistenceFinder
		<CommerceShippingFixedOptionRel, NoSuchShippingFixedOptionRelException>
			_collectionPersistenceFinderByCommerceShippingMethodId;

	/**
	 * Returns an ordered range of all the commerce shipping fixed option rels where commerceShippingMethodId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShippingFixedOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceShippingMethodId the commerce shipping method ID
	 * @param start the lower bound of the range of commerce shipping fixed option rels
	 * @param end the upper bound of the range of commerce shipping fixed option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipping fixed option rels
	 */
	@Override
	public List<CommerceShippingFixedOptionRel> findByCommerceShippingMethodId(
		long commerceShippingMethodId, int start, int end,
		OrderByComparator<CommerceShippingFixedOptionRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceShippingMethodId.find(
			finderCache, new Object[] {commerceShippingMethodId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce shipping fixed option rel in the ordered set where commerceShippingMethodId = &#63;.
	 *
	 * @param commerceShippingMethodId the commerce shipping method ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipping fixed option rel
	 * @throws NoSuchShippingFixedOptionRelException if a matching commerce shipping fixed option rel could not be found
	 */
	@Override
	public CommerceShippingFixedOptionRel findByCommerceShippingMethodId_First(
			long commerceShippingMethodId,
			OrderByComparator<CommerceShippingFixedOptionRel> orderByComparator)
		throws NoSuchShippingFixedOptionRelException {

		return _collectionPersistenceFinderByCommerceShippingMethodId.findFirst(
			finderCache, new Object[] {commerceShippingMethodId},
			orderByComparator);
	}

	/**
	 * Returns the first commerce shipping fixed option rel in the ordered set where commerceShippingMethodId = &#63;.
	 *
	 * @param commerceShippingMethodId the commerce shipping method ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipping fixed option rel, or <code>null</code> if a matching commerce shipping fixed option rel could not be found
	 */
	@Override
	public CommerceShippingFixedOptionRel fetchByCommerceShippingMethodId_First(
		long commerceShippingMethodId,
		OrderByComparator<CommerceShippingFixedOptionRel> orderByComparator) {

		return _collectionPersistenceFinderByCommerceShippingMethodId.
			fetchFirst(
				finderCache, new Object[] {commerceShippingMethodId},
				orderByComparator);
	}

	/**
	 * Removes all the commerce shipping fixed option rels where commerceShippingMethodId = &#63; from the database.
	 *
	 * @param commerceShippingMethodId the commerce shipping method ID
	 */
	@Override
	public void removeByCommerceShippingMethodId(
		long commerceShippingMethodId) {

		_collectionPersistenceFinderByCommerceShippingMethodId.remove(
			finderCache, new Object[] {commerceShippingMethodId});
	}

	/**
	 * Returns the number of commerce shipping fixed option rels where commerceShippingMethodId = &#63;.
	 *
	 * @param commerceShippingMethodId the commerce shipping method ID
	 * @return the number of matching commerce shipping fixed option rels
	 */
	@Override
	public int countByCommerceShippingMethodId(long commerceShippingMethodId) {
		return _collectionPersistenceFinderByCommerceShippingMethodId.count(
			finderCache, new Object[] {commerceShippingMethodId});
	}

	private CollectionPersistenceFinder
		<CommerceShippingFixedOptionRel, NoSuchShippingFixedOptionRelException>
			_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the commerce shipping fixed option rels where commerceShippingFixedOptionId = &#63; and commerceShippingMethodId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShippingFixedOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceShippingFixedOptionId the commerce shipping fixed option ID
	 * @param commerceShippingMethodId the commerce shipping method ID
	 * @param start the lower bound of the range of commerce shipping fixed option rels
	 * @param end the upper bound of the range of commerce shipping fixed option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipping fixed option rels
	 */
	@Override
	public List<CommerceShippingFixedOptionRel> findByC_C(
		long commerceShippingFixedOptionId, long commerceShippingMethodId,
		int start, int end,
		OrderByComparator<CommerceShippingFixedOptionRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			finderCache,
			new Object[] {
				commerceShippingFixedOptionId, commerceShippingMethodId
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce shipping fixed option rel in the ordered set where commerceShippingFixedOptionId = &#63; and commerceShippingMethodId = &#63;.
	 *
	 * @param commerceShippingFixedOptionId the commerce shipping fixed option ID
	 * @param commerceShippingMethodId the commerce shipping method ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipping fixed option rel
	 * @throws NoSuchShippingFixedOptionRelException if a matching commerce shipping fixed option rel could not be found
	 */
	@Override
	public CommerceShippingFixedOptionRel findByC_C_First(
			long commerceShippingFixedOptionId, long commerceShippingMethodId,
			OrderByComparator<CommerceShippingFixedOptionRel> orderByComparator)
		throws NoSuchShippingFixedOptionRelException {

		return _collectionPersistenceFinderByC_C.findFirst(
			finderCache,
			new Object[] {
				commerceShippingFixedOptionId, commerceShippingMethodId
			},
			orderByComparator);
	}

	/**
	 * Returns the first commerce shipping fixed option rel in the ordered set where commerceShippingFixedOptionId = &#63; and commerceShippingMethodId = &#63;.
	 *
	 * @param commerceShippingFixedOptionId the commerce shipping fixed option ID
	 * @param commerceShippingMethodId the commerce shipping method ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipping fixed option rel, or <code>null</code> if a matching commerce shipping fixed option rel could not be found
	 */
	@Override
	public CommerceShippingFixedOptionRel fetchByC_C_First(
		long commerceShippingFixedOptionId, long commerceShippingMethodId,
		OrderByComparator<CommerceShippingFixedOptionRel> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			finderCache,
			new Object[] {
				commerceShippingFixedOptionId, commerceShippingMethodId
			},
			orderByComparator);
	}

	/**
	 * Removes all the commerce shipping fixed option rels where commerceShippingFixedOptionId = &#63; and commerceShippingMethodId = &#63; from the database.
	 *
	 * @param commerceShippingFixedOptionId the commerce shipping fixed option ID
	 * @param commerceShippingMethodId the commerce shipping method ID
	 */
	@Override
	public void removeByC_C(
		long commerceShippingFixedOptionId, long commerceShippingMethodId) {

		_collectionPersistenceFinderByC_C.remove(
			finderCache,
			new Object[] {
				commerceShippingFixedOptionId, commerceShippingMethodId
			});
	}

	/**
	 * Returns the number of commerce shipping fixed option rels where commerceShippingFixedOptionId = &#63; and commerceShippingMethodId = &#63;.
	 *
	 * @param commerceShippingFixedOptionId the commerce shipping fixed option ID
	 * @param commerceShippingMethodId the commerce shipping method ID
	 * @return the number of matching commerce shipping fixed option rels
	 */
	@Override
	public int countByC_C(
		long commerceShippingFixedOptionId, long commerceShippingMethodId) {

		return _collectionPersistenceFinderByC_C.count(
			finderCache,
			new Object[] {
				commerceShippingFixedOptionId, commerceShippingMethodId
			});
	}

	public CommerceShippingFixedOptionRelPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put(
			"commerceShippingFixedOptionRelId", "CShippingFixedOptionRelId");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceShippingFixedOptionRel.class);

		setModelImplClass(CommerceShippingFixedOptionRelImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceShippingFixedOptionRelTable.INSTANCE);
	}

	/**
	 * Creates a new commerce shipping fixed option rel with the primary key. Does not add the commerce shipping fixed option rel to the database.
	 *
	 * @param commerceShippingFixedOptionRelId the primary key for the new commerce shipping fixed option rel
	 * @return the new commerce shipping fixed option rel
	 */
	@Override
	public CommerceShippingFixedOptionRel create(
		long commerceShippingFixedOptionRelId) {

		CommerceShippingFixedOptionRel commerceShippingFixedOptionRel =
			new CommerceShippingFixedOptionRelImpl();

		commerceShippingFixedOptionRel.setNew(true);
		commerceShippingFixedOptionRel.setPrimaryKey(
			commerceShippingFixedOptionRelId);

		commerceShippingFixedOptionRel.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return commerceShippingFixedOptionRel;
	}

	/**
	 * Removes the commerce shipping fixed option rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceShippingFixedOptionRelId the primary key of the commerce shipping fixed option rel
	 * @return the commerce shipping fixed option rel that was removed
	 * @throws NoSuchShippingFixedOptionRelException if a commerce shipping fixed option rel with the primary key could not be found
	 */
	@Override
	public CommerceShippingFixedOptionRel remove(
			long commerceShippingFixedOptionRelId)
		throws NoSuchShippingFixedOptionRelException {

		return remove((Serializable)commerceShippingFixedOptionRelId);
	}

	@Override
	protected CommerceShippingFixedOptionRel removeImpl(
		CommerceShippingFixedOptionRel commerceShippingFixedOptionRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceShippingFixedOptionRel)) {
				commerceShippingFixedOptionRel =
					(CommerceShippingFixedOptionRel)session.get(
						CommerceShippingFixedOptionRelImpl.class,
						commerceShippingFixedOptionRel.getPrimaryKeyObj());
			}

			if (commerceShippingFixedOptionRel != null) {
				session.delete(commerceShippingFixedOptionRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceShippingFixedOptionRel != null) {
			clearCache(commerceShippingFixedOptionRel);
		}

		return commerceShippingFixedOptionRel;
	}

	@Override
	public CommerceShippingFixedOptionRel updateImpl(
		CommerceShippingFixedOptionRel commerceShippingFixedOptionRel) {

		boolean isNew = commerceShippingFixedOptionRel.isNew();

		if (!(commerceShippingFixedOptionRel instanceof
				CommerceShippingFixedOptionRelModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					commerceShippingFixedOptionRel.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceShippingFixedOptionRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceShippingFixedOptionRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceShippingFixedOptionRel implementation " +
					commerceShippingFixedOptionRel.getClass());
		}

		CommerceShippingFixedOptionRelModelImpl
			commerceShippingFixedOptionRelModelImpl =
				(CommerceShippingFixedOptionRelModelImpl)
					commerceShippingFixedOptionRel;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceShippingFixedOptionRel.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceShippingFixedOptionRel.setCreateDate(date);
			}
			else {
				commerceShippingFixedOptionRel.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceShippingFixedOptionRelModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceShippingFixedOptionRel.setModifiedDate(date);
			}
			else {
				commerceShippingFixedOptionRel.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceShippingFixedOptionRel);
			}
			else {
				commerceShippingFixedOptionRel =
					(CommerceShippingFixedOptionRel)session.merge(
						commerceShippingFixedOptionRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceShippingFixedOptionRel, false);

		if (isNew) {
			commerceShippingFixedOptionRel.setNew(false);
		}

		commerceShippingFixedOptionRel.resetOriginalValues();

		return commerceShippingFixedOptionRel;
	}

	/**
	 * Returns the commerce shipping fixed option rel with the primary key or throws a <code>NoSuchShippingFixedOptionRelException</code> if it could not be found.
	 *
	 * @param commerceShippingFixedOptionRelId the primary key of the commerce shipping fixed option rel
	 * @return the commerce shipping fixed option rel
	 * @throws NoSuchShippingFixedOptionRelException if a commerce shipping fixed option rel with the primary key could not be found
	 */
	@Override
	public CommerceShippingFixedOptionRel findByPrimaryKey(
			long commerceShippingFixedOptionRelId)
		throws NoSuchShippingFixedOptionRelException {

		return findByPrimaryKey((Serializable)commerceShippingFixedOptionRelId);
	}

	/**
	 * Returns the commerce shipping fixed option rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceShippingFixedOptionRelId the primary key of the commerce shipping fixed option rel
	 * @return the commerce shipping fixed option rel, or <code>null</code> if a commerce shipping fixed option rel with the primary key could not be found
	 */
	@Override
	public CommerceShippingFixedOptionRel fetchByPrimaryKey(
		long commerceShippingFixedOptionRelId) {

		return fetchByPrimaryKey(
			(Serializable)commerceShippingFixedOptionRelId);
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
		return "CShippingFixedOptionRelId";
	}

	@Override
	protected String getPKFieldName() {
		return "commerceShippingFixedOptionRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCESHIPPINGFIXEDOPTIONREL;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceShippingFixedOptionRelModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce shipping fixed option rel persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByCommerceShippingFixedOptionId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommerceShippingFixedOptionId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commerceShippingFixedOptionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommerceShippingFixedOptionId",
					new String[] {Long.class.getName()},
					new String[] {"commerceShippingFixedOptionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommerceShippingFixedOptionId",
					new String[] {Long.class.getName()},
					new String[] {"commerceShippingFixedOptionId"}, false),
				_SQL_SELECT_COMMERCESHIPPINGFIXEDOPTIONREL_WHERE,
				_SQL_COUNT_COMMERCESHIPPINGFIXEDOPTIONREL_WHERE,
				CommerceShippingFixedOptionRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceShippingFixedOptionRel.",
					"commerceShippingFixedOptionId", FinderColumn.Type.LONG,
					"=", true, true,
					CommerceShippingFixedOptionRel::
						getCommerceShippingFixedOptionId));

		_collectionPersistenceFinderByCommerceShippingMethodId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommerceShippingMethodId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commerceShippingMethodId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommerceShippingMethodId",
					new String[] {Long.class.getName()},
					new String[] {"commerceShippingMethodId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommerceShippingMethodId",
					new String[] {Long.class.getName()},
					new String[] {"commerceShippingMethodId"}, false),
				_SQL_SELECT_COMMERCESHIPPINGFIXEDOPTIONREL_WHERE,
				_SQL_COUNT_COMMERCESHIPPINGFIXEDOPTIONREL_WHERE,
				CommerceShippingFixedOptionRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceShippingFixedOptionRel.",
					"commerceShippingMethodId", FinderColumn.Type.LONG, "=",
					true, true,
					CommerceShippingFixedOptionRel::
						getCommerceShippingMethodId));

		_collectionPersistenceFinderByC_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {
					"commerceShippingFixedOptionId", "commerceShippingMethodId"
				},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {
					"commerceShippingFixedOptionId", "commerceShippingMethodId"
				},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {
					"commerceShippingFixedOptionId", "commerceShippingMethodId"
				},
				false),
			_SQL_SELECT_COMMERCESHIPPINGFIXEDOPTIONREL_WHERE,
			_SQL_COUNT_COMMERCESHIPPINGFIXEDOPTIONREL_WHERE,
			CommerceShippingFixedOptionRelModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"commerceShippingFixedOptionRel.",
				"commerceShippingFixedOptionId", FinderColumn.Type.LONG, "=",
				true, true,
				CommerceShippingFixedOptionRel::
					getCommerceShippingFixedOptionId),
			new FinderColumn<>(
				"commerceShippingFixedOptionRel.", "commerceShippingMethodId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceShippingFixedOptionRel::getCommerceShippingMethodId));

		CommerceShippingFixedOptionRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceShippingFixedOptionRelUtil.setPersistence(null);

		entityCache.removeCache(
			CommerceShippingFixedOptionRelImpl.class.getName());
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
		CommerceShippingFixedOptionRelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCESHIPPINGFIXEDOPTIONREL =
		"SELECT commerceShippingFixedOptionRel FROM CommerceShippingFixedOptionRel commerceShippingFixedOptionRel";

	private static final String
		_SQL_SELECT_COMMERCESHIPPINGFIXEDOPTIONREL_WHERE =
			"SELECT commerceShippingFixedOptionRel FROM CommerceShippingFixedOptionRel commerceShippingFixedOptionRel WHERE ";

	private static final String
		_SQL_COUNT_COMMERCESHIPPINGFIXEDOPTIONREL_WHERE =
			"SELECT COUNT(commerceShippingFixedOptionRel) FROM CommerceShippingFixedOptionRel commerceShippingFixedOptionRel WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommerceShippingFixedOptionRel exists with the key {";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"commerceShippingFixedOptionRelId"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1551821400