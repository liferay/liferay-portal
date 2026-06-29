/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipping.engine.fixed.service.persistence.impl;

import com.liferay.commerce.shipping.engine.fixed.exception.NoSuchShippingFixedOptionQualifierException;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOptionQualifier;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOptionQualifierTable;
import com.liferay.commerce.shipping.engine.fixed.model.impl.CommerceShippingFixedOptionQualifierImpl;
import com.liferay.commerce.shipping.engine.fixed.model.impl.CommerceShippingFixedOptionQualifierModelImpl;
import com.liferay.commerce.shipping.engine.fixed.service.persistence.CommerceShippingFixedOptionQualifierPersistence;
import com.liferay.commerce.shipping.engine.fixed.service.persistence.CommerceShippingFixedOptionQualifierUtil;
import com.liferay.commerce.shipping.engine.fixed.service.persistence.impl.constants.CommercePersistenceConstants;
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
 * The persistence implementation for the commerce shipping fixed option qualifier service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(service = CommerceShippingFixedOptionQualifierPersistence.class)
public class CommerceShippingFixedOptionQualifierPersistenceImpl
	extends BasePersistenceImpl
		<CommerceShippingFixedOptionQualifier,
		 NoSuchShippingFixedOptionQualifierException>
	implements CommerceShippingFixedOptionQualifierPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceShippingFixedOptionQualifierUtil</code> to access the commerce shipping fixed option qualifier persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceShippingFixedOptionQualifierImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceShippingFixedOptionQualifier,
		 NoSuchShippingFixedOptionQualifierException>
			_collectionPersistenceFinderByCommerceShippingFixedOptionId;

	/**
	 * Returns an ordered range of all the commerce shipping fixed option qualifiers where commerceShippingFixedOptionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShippingFixedOptionQualifierModelImpl</code>.
	 * </p>
	 *
	 * @param commerceShippingFixedOptionId the commerce shipping fixed option ID
	 * @param start the lower bound of the range of commerce shipping fixed option qualifiers
	 * @param end the upper bound of the range of commerce shipping fixed option qualifiers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipping fixed option qualifiers
	 */
	@Override
	public List<CommerceShippingFixedOptionQualifier>
		findByCommerceShippingFixedOptionId(
			long commerceShippingFixedOptionId, int start, int end,
			OrderByComparator<CommerceShippingFixedOptionQualifier>
				orderByComparator,
			boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceShippingFixedOptionId.find(
			finderCache, new Object[] {commerceShippingFixedOptionId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce shipping fixed option qualifier in the ordered set where commerceShippingFixedOptionId = &#63;.
	 *
	 * @param commerceShippingFixedOptionId the commerce shipping fixed option ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipping fixed option qualifier
	 * @throws NoSuchShippingFixedOptionQualifierException if a matching commerce shipping fixed option qualifier could not be found
	 */
	@Override
	public CommerceShippingFixedOptionQualifier
			findByCommerceShippingFixedOptionId_First(
				long commerceShippingFixedOptionId,
				OrderByComparator<CommerceShippingFixedOptionQualifier>
					orderByComparator)
		throws NoSuchShippingFixedOptionQualifierException {

		return _collectionPersistenceFinderByCommerceShippingFixedOptionId.
			findFirst(
				finderCache, new Object[] {commerceShippingFixedOptionId},
				orderByComparator);
	}

	/**
	 * Returns the first commerce shipping fixed option qualifier in the ordered set where commerceShippingFixedOptionId = &#63;.
	 *
	 * @param commerceShippingFixedOptionId the commerce shipping fixed option ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipping fixed option qualifier, or <code>null</code> if a matching commerce shipping fixed option qualifier could not be found
	 */
	@Override
	public CommerceShippingFixedOptionQualifier
		fetchByCommerceShippingFixedOptionId_First(
			long commerceShippingFixedOptionId,
			OrderByComparator<CommerceShippingFixedOptionQualifier>
				orderByComparator) {

		return _collectionPersistenceFinderByCommerceShippingFixedOptionId.
			fetchFirst(
				finderCache, new Object[] {commerceShippingFixedOptionId},
				orderByComparator);
	}

	/**
	 * Removes all the commerce shipping fixed option qualifiers where commerceShippingFixedOptionId = &#63; from the database.
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
	 * Returns the number of commerce shipping fixed option qualifiers where commerceShippingFixedOptionId = &#63;.
	 *
	 * @param commerceShippingFixedOptionId the commerce shipping fixed option ID
	 * @return the number of matching commerce shipping fixed option qualifiers
	 */
	@Override
	public int countByCommerceShippingFixedOptionId(
		long commerceShippingFixedOptionId) {

		return _collectionPersistenceFinderByCommerceShippingFixedOptionId.
			count(finderCache, new Object[] {commerceShippingFixedOptionId});
	}

	private CollectionPersistenceFinder
		<CommerceShippingFixedOptionQualifier,
		 NoSuchShippingFixedOptionQualifierException>
			_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the commerce shipping fixed option qualifiers where classNameId = &#63; and commerceShippingFixedOptionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShippingFixedOptionQualifierModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param commerceShippingFixedOptionId the commerce shipping fixed option ID
	 * @param start the lower bound of the range of commerce shipping fixed option qualifiers
	 * @param end the upper bound of the range of commerce shipping fixed option qualifiers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipping fixed option qualifiers
	 */
	@Override
	public List<CommerceShippingFixedOptionQualifier> findByC_C(
		long classNameId, long commerceShippingFixedOptionId, int start,
		int end,
		OrderByComparator<CommerceShippingFixedOptionQualifier>
			orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			finderCache,
			new Object[] {classNameId, commerceShippingFixedOptionId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce shipping fixed option qualifier in the ordered set where classNameId = &#63; and commerceShippingFixedOptionId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param commerceShippingFixedOptionId the commerce shipping fixed option ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipping fixed option qualifier
	 * @throws NoSuchShippingFixedOptionQualifierException if a matching commerce shipping fixed option qualifier could not be found
	 */
	@Override
	public CommerceShippingFixedOptionQualifier findByC_C_First(
			long classNameId, long commerceShippingFixedOptionId,
			OrderByComparator<CommerceShippingFixedOptionQualifier>
				orderByComparator)
		throws NoSuchShippingFixedOptionQualifierException {

		return _collectionPersistenceFinderByC_C.findFirst(
			finderCache,
			new Object[] {classNameId, commerceShippingFixedOptionId},
			orderByComparator);
	}

	/**
	 * Returns the first commerce shipping fixed option qualifier in the ordered set where classNameId = &#63; and commerceShippingFixedOptionId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param commerceShippingFixedOptionId the commerce shipping fixed option ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipping fixed option qualifier, or <code>null</code> if a matching commerce shipping fixed option qualifier could not be found
	 */
	@Override
	public CommerceShippingFixedOptionQualifier fetchByC_C_First(
		long classNameId, long commerceShippingFixedOptionId,
		OrderByComparator<CommerceShippingFixedOptionQualifier>
			orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			finderCache,
			new Object[] {classNameId, commerceShippingFixedOptionId},
			orderByComparator);
	}

	/**
	 * Removes all the commerce shipping fixed option qualifiers where classNameId = &#63; and commerceShippingFixedOptionId = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param commerceShippingFixedOptionId the commerce shipping fixed option ID
	 */
	@Override
	public void removeByC_C(
		long classNameId, long commerceShippingFixedOptionId) {

		_collectionPersistenceFinderByC_C.remove(
			finderCache,
			new Object[] {classNameId, commerceShippingFixedOptionId});
	}

	/**
	 * Returns the number of commerce shipping fixed option qualifiers where classNameId = &#63; and commerceShippingFixedOptionId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param commerceShippingFixedOptionId the commerce shipping fixed option ID
	 * @return the number of matching commerce shipping fixed option qualifiers
	 */
	@Override
	public int countByC_C(
		long classNameId, long commerceShippingFixedOptionId) {

		return _collectionPersistenceFinderByC_C.count(
			finderCache,
			new Object[] {classNameId, commerceShippingFixedOptionId});
	}

	private UniquePersistenceFinder
		<CommerceShippingFixedOptionQualifier,
		 NoSuchShippingFixedOptionQualifierException>
			_uniquePersistenceFinderByC_C_C;

	/**
	 * Returns the commerce shipping fixed option qualifier where classNameId = &#63; and classPK = &#63; and commerceShippingFixedOptionId = &#63; or throws a <code>NoSuchShippingFixedOptionQualifierException</code> if it could not be found.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param commerceShippingFixedOptionId the commerce shipping fixed option ID
	 * @return the matching commerce shipping fixed option qualifier
	 * @throws NoSuchShippingFixedOptionQualifierException if a matching commerce shipping fixed option qualifier could not be found
	 */
	@Override
	public CommerceShippingFixedOptionQualifier findByC_C_C(
			long classNameId, long classPK, long commerceShippingFixedOptionId)
		throws NoSuchShippingFixedOptionQualifierException {

		return _uniquePersistenceFinderByC_C_C.find(
			finderCache,
			new Object[] {classNameId, classPK, commerceShippingFixedOptionId});
	}

	/**
	 * Returns the commerce shipping fixed option qualifier where classNameId = &#63; and classPK = &#63; and commerceShippingFixedOptionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param commerceShippingFixedOptionId the commerce shipping fixed option ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce shipping fixed option qualifier, or <code>null</code> if a matching commerce shipping fixed option qualifier could not be found
	 */
	@Override
	public CommerceShippingFixedOptionQualifier fetchByC_C_C(
		long classNameId, long classPK, long commerceShippingFixedOptionId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByC_C_C.fetch(
			finderCache,
			new Object[] {classNameId, classPK, commerceShippingFixedOptionId},
			useFinderCache);
	}

	/**
	 * Removes the commerce shipping fixed option qualifier where classNameId = &#63; and classPK = &#63; and commerceShippingFixedOptionId = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param commerceShippingFixedOptionId the commerce shipping fixed option ID
	 * @return the commerce shipping fixed option qualifier that was removed
	 */
	@Override
	public CommerceShippingFixedOptionQualifier removeByC_C_C(
			long classNameId, long classPK, long commerceShippingFixedOptionId)
		throws NoSuchShippingFixedOptionQualifierException {

		CommerceShippingFixedOptionQualifier
			commerceShippingFixedOptionQualifier = findByC_C_C(
				classNameId, classPK, commerceShippingFixedOptionId);

		return remove(commerceShippingFixedOptionQualifier);
	}

	/**
	 * Returns the number of commerce shipping fixed option qualifiers where classNameId = &#63; and classPK = &#63; and commerceShippingFixedOptionId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param commerceShippingFixedOptionId the commerce shipping fixed option ID
	 * @return the number of matching commerce shipping fixed option qualifiers
	 */
	@Override
	public int countByC_C_C(
		long classNameId, long classPK, long commerceShippingFixedOptionId) {

		return _uniquePersistenceFinderByC_C_C.count(
			finderCache,
			new Object[] {classNameId, classPK, commerceShippingFixedOptionId});
	}

	public CommerceShippingFixedOptionQualifierPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put(
			"commerceShippingFixedOptionQualifierId",
			"CSFixedOptionQualifierId");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceShippingFixedOptionQualifier.class);

		setModelImplClass(CommerceShippingFixedOptionQualifierImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceShippingFixedOptionQualifierTable.INSTANCE);
	}

	/**
	 * Creates a new commerce shipping fixed option qualifier with the primary key. Does not add the commerce shipping fixed option qualifier to the database.
	 *
	 * @param commerceShippingFixedOptionQualifierId the primary key for the new commerce shipping fixed option qualifier
	 * @return the new commerce shipping fixed option qualifier
	 */
	@Override
	public CommerceShippingFixedOptionQualifier create(
		long commerceShippingFixedOptionQualifierId) {

		CommerceShippingFixedOptionQualifier
			commerceShippingFixedOptionQualifier =
				new CommerceShippingFixedOptionQualifierImpl();

		commerceShippingFixedOptionQualifier.setNew(true);
		commerceShippingFixedOptionQualifier.setPrimaryKey(
			commerceShippingFixedOptionQualifierId);

		commerceShippingFixedOptionQualifier.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return commerceShippingFixedOptionQualifier;
	}

	/**
	 * Removes the commerce shipping fixed option qualifier with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceShippingFixedOptionQualifierId the primary key of the commerce shipping fixed option qualifier
	 * @return the commerce shipping fixed option qualifier that was removed
	 * @throws NoSuchShippingFixedOptionQualifierException if a commerce shipping fixed option qualifier with the primary key could not be found
	 */
	@Override
	public CommerceShippingFixedOptionQualifier remove(
			long commerceShippingFixedOptionQualifierId)
		throws NoSuchShippingFixedOptionQualifierException {

		return remove((Serializable)commerceShippingFixedOptionQualifierId);
	}

	@Override
	protected CommerceShippingFixedOptionQualifier removeImpl(
		CommerceShippingFixedOptionQualifier
			commerceShippingFixedOptionQualifier) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceShippingFixedOptionQualifier)) {
				commerceShippingFixedOptionQualifier =
					(CommerceShippingFixedOptionQualifier)session.get(
						CommerceShippingFixedOptionQualifierImpl.class,
						commerceShippingFixedOptionQualifier.
							getPrimaryKeyObj());
			}

			if (commerceShippingFixedOptionQualifier != null) {
				session.delete(commerceShippingFixedOptionQualifier);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceShippingFixedOptionQualifier != null) {
			clearCache(commerceShippingFixedOptionQualifier);
		}

		return commerceShippingFixedOptionQualifier;
	}

	@Override
	public CommerceShippingFixedOptionQualifier updateImpl(
		CommerceShippingFixedOptionQualifier
			commerceShippingFixedOptionQualifier) {

		boolean isNew = commerceShippingFixedOptionQualifier.isNew();

		if (!(commerceShippingFixedOptionQualifier instanceof
				CommerceShippingFixedOptionQualifierModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					commerceShippingFixedOptionQualifier.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceShippingFixedOptionQualifier);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceShippingFixedOptionQualifier proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceShippingFixedOptionQualifier implementation " +
					commerceShippingFixedOptionQualifier.getClass());
		}

		CommerceShippingFixedOptionQualifierModelImpl
			commerceShippingFixedOptionQualifierModelImpl =
				(CommerceShippingFixedOptionQualifierModelImpl)
					commerceShippingFixedOptionQualifier;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew &&
			(commerceShippingFixedOptionQualifier.getCreateDate() == null)) {

			if (serviceContext == null) {
				commerceShippingFixedOptionQualifier.setCreateDate(date);
			}
			else {
				commerceShippingFixedOptionQualifier.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceShippingFixedOptionQualifierModelImpl.
				hasSetModifiedDate()) {

			if (serviceContext == null) {
				commerceShippingFixedOptionQualifier.setModifiedDate(date);
			}
			else {
				commerceShippingFixedOptionQualifier.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceShippingFixedOptionQualifier);
			}
			else {
				commerceShippingFixedOptionQualifier =
					(CommerceShippingFixedOptionQualifier)session.merge(
						commerceShippingFixedOptionQualifier);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceShippingFixedOptionQualifier, false);

		if (isNew) {
			commerceShippingFixedOptionQualifier.setNew(false);
		}

		commerceShippingFixedOptionQualifier.resetOriginalValues();

		return commerceShippingFixedOptionQualifier;
	}

	/**
	 * Returns the commerce shipping fixed option qualifier with the primary key or throws a <code>NoSuchShippingFixedOptionQualifierException</code> if it could not be found.
	 *
	 * @param commerceShippingFixedOptionQualifierId the primary key of the commerce shipping fixed option qualifier
	 * @return the commerce shipping fixed option qualifier
	 * @throws NoSuchShippingFixedOptionQualifierException if a commerce shipping fixed option qualifier with the primary key could not be found
	 */
	@Override
	public CommerceShippingFixedOptionQualifier findByPrimaryKey(
			long commerceShippingFixedOptionQualifierId)
		throws NoSuchShippingFixedOptionQualifierException {

		return findByPrimaryKey(
			(Serializable)commerceShippingFixedOptionQualifierId);
	}

	/**
	 * Returns the commerce shipping fixed option qualifier with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceShippingFixedOptionQualifierId the primary key of the commerce shipping fixed option qualifier
	 * @return the commerce shipping fixed option qualifier, or <code>null</code> if a commerce shipping fixed option qualifier with the primary key could not be found
	 */
	@Override
	public CommerceShippingFixedOptionQualifier fetchByPrimaryKey(
		long commerceShippingFixedOptionQualifierId) {

		return fetchByPrimaryKey(
			(Serializable)commerceShippingFixedOptionQualifierId);
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
		return "CSFixedOptionQualifierId";
	}

	@Override
	protected String getPKFieldName() {
		return "commerceShippingFixedOptionQualifierId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCESHIPPINGFIXEDOPTIONQUALIFIER;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceShippingFixedOptionQualifierModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce shipping fixed option qualifier persistence.
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
				_SQL_SELECT_COMMERCESHIPPINGFIXEDOPTIONQUALIFIER_WHERE,
				_SQL_COUNT_COMMERCESHIPPINGFIXEDOPTIONQUALIFIER_WHERE,
				CommerceShippingFixedOptionQualifierModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceShippingFixedOptionQualifier.",
					"commerceShippingFixedOptionId", FinderColumn.Type.LONG,
					"=", true, true,
					CommerceShippingFixedOptionQualifier::
						getCommerceShippingFixedOptionId));

		_collectionPersistenceFinderByC_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"classNameId", "commerceShippingFixedOptionId"},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "commerceShippingFixedOptionId"},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "commerceShippingFixedOptionId"},
				false),
			_SQL_SELECT_COMMERCESHIPPINGFIXEDOPTIONQUALIFIER_WHERE,
			_SQL_COUNT_COMMERCESHIPPINGFIXEDOPTIONQUALIFIER_WHERE,
			CommerceShippingFixedOptionQualifierModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"commerceShippingFixedOptionQualifier.", "classNameId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceShippingFixedOptionQualifier::getClassNameId),
			new FinderColumn<>(
				"commerceShippingFixedOptionQualifier.",
				"commerceShippingFixedOptionId", FinderColumn.Type.LONG, "=",
				true, true,
				CommerceShippingFixedOptionQualifier::
					getCommerceShippingFixedOptionId));

		_uniquePersistenceFinderByC_C_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {
					"classNameId", "classPK", "commerceShippingFixedOptionId"
				},
				0, 0, false,
				CommerceShippingFixedOptionQualifier::getClassNameId,
				CommerceShippingFixedOptionQualifier::getClassPK,
				CommerceShippingFixedOptionQualifier::
					getCommerceShippingFixedOptionId),
			_SQL_SELECT_COMMERCESHIPPINGFIXEDOPTIONQUALIFIER_WHERE, "",
			new FinderColumn<>(
				"commerceShippingFixedOptionQualifier.", "classNameId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceShippingFixedOptionQualifier::getClassNameId),
			new FinderColumn<>(
				"commerceShippingFixedOptionQualifier.", "classPK",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceShippingFixedOptionQualifier::getClassPK),
			new FinderColumn<>(
				"commerceShippingFixedOptionQualifier.",
				"commerceShippingFixedOptionId", FinderColumn.Type.LONG, "=",
				true, true,
				CommerceShippingFixedOptionQualifier::
					getCommerceShippingFixedOptionId));

		CommerceShippingFixedOptionQualifierUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceShippingFixedOptionQualifierUtil.setPersistence(null);

		entityCache.removeCache(
			CommerceShippingFixedOptionQualifierImpl.class.getName());
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
		CommerceShippingFixedOptionQualifierModelImpl.ENTITY_ALIAS + ".";

	private static final String
		_SQL_SELECT_COMMERCESHIPPINGFIXEDOPTIONQUALIFIER =
			"SELECT commerceShippingFixedOptionQualifier FROM CommerceShippingFixedOptionQualifier commerceShippingFixedOptionQualifier";

	private static final String
		_SQL_SELECT_COMMERCESHIPPINGFIXEDOPTIONQUALIFIER_WHERE =
			"SELECT commerceShippingFixedOptionQualifier FROM CommerceShippingFixedOptionQualifier commerceShippingFixedOptionQualifier WHERE ";

	private static final String
		_SQL_COUNT_COMMERCESHIPPINGFIXEDOPTIONQUALIFIER_WHERE =
			"SELECT COUNT(commerceShippingFixedOptionQualifier) FROM CommerceShippingFixedOptionQualifier commerceShippingFixedOptionQualifier WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommerceShippingFixedOptionQualifier exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceShippingFixedOptionQualifierPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"commerceShippingFixedOptionQualifierId"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1446043212