/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence.impl;

import com.liferay.commerce.exception.NoSuchAddressRestrictionException;
import com.liferay.commerce.model.CommerceAddressRestriction;
import com.liferay.commerce.model.CommerceAddressRestrictionTable;
import com.liferay.commerce.model.impl.CommerceAddressRestrictionImpl;
import com.liferay.commerce.model.impl.CommerceAddressRestrictionModelImpl;
import com.liferay.commerce.service.persistence.CommerceAddressRestrictionPersistence;
import com.liferay.commerce.service.persistence.CommerceAddressRestrictionUtil;
import com.liferay.commerce.service.persistence.impl.constants.CommercePersistenceConstants;
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
 * The persistence implementation for the commerce address restriction service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(service = CommerceAddressRestrictionPersistence.class)
public class CommerceAddressRestrictionPersistenceImpl
	extends BasePersistenceImpl
		<CommerceAddressRestriction, NoSuchAddressRestrictionException>
	implements CommerceAddressRestrictionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceAddressRestrictionUtil</code> to access the commerce address restriction persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceAddressRestrictionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceAddressRestriction, NoSuchAddressRestrictionException>
			_collectionPersistenceFinderByCountryId;

	/**
	 * Returns an ordered range of all the commerce address restrictions where countryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceAddressRestrictionModelImpl</code>.
	 * </p>
	 *
	 * @param countryId the country ID
	 * @param start the lower bound of the range of commerce address restrictions
	 * @param end the upper bound of the range of commerce address restrictions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce address restrictions
	 */
	@Override
	public List<CommerceAddressRestriction> findByCountryId(
		long countryId, int start, int end,
		OrderByComparator<CommerceAddressRestriction> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCountryId.find(
			finderCache, new Object[] {countryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce address restriction in the ordered set where countryId = &#63;.
	 *
	 * @param countryId the country ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce address restriction
	 * @throws NoSuchAddressRestrictionException if a matching commerce address restriction could not be found
	 */
	@Override
	public CommerceAddressRestriction findByCountryId_First(
			long countryId,
			OrderByComparator<CommerceAddressRestriction> orderByComparator)
		throws NoSuchAddressRestrictionException {

		return _collectionPersistenceFinderByCountryId.findFirst(
			finderCache, new Object[] {countryId}, orderByComparator);
	}

	/**
	 * Returns the first commerce address restriction in the ordered set where countryId = &#63;.
	 *
	 * @param countryId the country ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce address restriction, or <code>null</code> if a matching commerce address restriction could not be found
	 */
	@Override
	public CommerceAddressRestriction fetchByCountryId_First(
		long countryId,
		OrderByComparator<CommerceAddressRestriction> orderByComparator) {

		return _collectionPersistenceFinderByCountryId.fetchFirst(
			finderCache, new Object[] {countryId}, orderByComparator);
	}

	/**
	 * Removes all the commerce address restrictions where countryId = &#63; from the database.
	 *
	 * @param countryId the country ID
	 */
	@Override
	public void removeByCountryId(long countryId) {
		_collectionPersistenceFinderByCountryId.remove(
			finderCache, new Object[] {countryId});
	}

	/**
	 * Returns the number of commerce address restrictions where countryId = &#63;.
	 *
	 * @param countryId the country ID
	 * @return the number of matching commerce address restrictions
	 */
	@Override
	public int countByCountryId(long countryId) {
		return _collectionPersistenceFinderByCountryId.count(
			finderCache, new Object[] {countryId});
	}

	private CollectionPersistenceFinder
		<CommerceAddressRestriction, NoSuchAddressRestrictionException>
			_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the commerce address restrictions where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceAddressRestrictionModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of commerce address restrictions
	 * @param end the upper bound of the range of commerce address restrictions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce address restrictions
	 */
	@Override
	public List<CommerceAddressRestriction> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<CommerceAddressRestriction> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			finderCache, new Object[] {classNameId, classPK}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce address restriction in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce address restriction
	 * @throws NoSuchAddressRestrictionException if a matching commerce address restriction could not be found
	 */
	@Override
	public CommerceAddressRestriction findByC_C_First(
			long classNameId, long classPK,
			OrderByComparator<CommerceAddressRestriction> orderByComparator)
		throws NoSuchAddressRestrictionException {

		return _collectionPersistenceFinderByC_C.findFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Returns the first commerce address restriction in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce address restriction, or <code>null</code> if a matching commerce address restriction could not be found
	 */
	@Override
	public CommerceAddressRestriction fetchByC_C_First(
		long classNameId, long classPK,
		OrderByComparator<CommerceAddressRestriction> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Removes all the commerce address restrictions where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByC_C(long classNameId, long classPK) {
		_collectionPersistenceFinderByC_C.remove(
			finderCache, new Object[] {classNameId, classPK});
	}

	/**
	 * Returns the number of commerce address restrictions where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching commerce address restrictions
	 */
	@Override
	public int countByC_C(long classNameId, long classPK) {
		return _collectionPersistenceFinderByC_C.count(
			finderCache, new Object[] {classNameId, classPK});
	}

	private UniquePersistenceFinder
		<CommerceAddressRestriction, NoSuchAddressRestrictionException>
			_uniquePersistenceFinderByC_C_C;

	/**
	 * Returns the commerce address restriction where classNameId = &#63; and classPK = &#63; and countryId = &#63; or throws a <code>NoSuchAddressRestrictionException</code> if it could not be found.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param countryId the country ID
	 * @return the matching commerce address restriction
	 * @throws NoSuchAddressRestrictionException if a matching commerce address restriction could not be found
	 */
	@Override
	public CommerceAddressRestriction findByC_C_C(
			long classNameId, long classPK, long countryId)
		throws NoSuchAddressRestrictionException {

		return _uniquePersistenceFinderByC_C_C.find(
			finderCache, new Object[] {classNameId, classPK, countryId});
	}

	/**
	 * Returns the commerce address restriction where classNameId = &#63; and classPK = &#63; and countryId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param countryId the country ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce address restriction, or <code>null</code> if a matching commerce address restriction could not be found
	 */
	@Override
	public CommerceAddressRestriction fetchByC_C_C(
		long classNameId, long classPK, long countryId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByC_C_C.fetch(
			finderCache, new Object[] {classNameId, classPK, countryId},
			useFinderCache);
	}

	/**
	 * Removes the commerce address restriction where classNameId = &#63; and classPK = &#63; and countryId = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param countryId the country ID
	 * @return the commerce address restriction that was removed
	 */
	@Override
	public CommerceAddressRestriction removeByC_C_C(
			long classNameId, long classPK, long countryId)
		throws NoSuchAddressRestrictionException {

		CommerceAddressRestriction commerceAddressRestriction = findByC_C_C(
			classNameId, classPK, countryId);

		return remove(commerceAddressRestriction);
	}

	/**
	 * Returns the number of commerce address restrictions where classNameId = &#63; and classPK = &#63; and countryId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param countryId the country ID
	 * @return the number of matching commerce address restrictions
	 */
	@Override
	public int countByC_C_C(long classNameId, long classPK, long countryId) {
		return _uniquePersistenceFinderByC_C_C.count(
			finderCache, new Object[] {classNameId, classPK, countryId});
	}

	public CommerceAddressRestrictionPersistenceImpl() {
		setModelClass(CommerceAddressRestriction.class);

		setModelImplClass(CommerceAddressRestrictionImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceAddressRestrictionTable.INSTANCE);
	}

	/**
	 * Creates a new commerce address restriction with the primary key. Does not add the commerce address restriction to the database.
	 *
	 * @param commerceAddressRestrictionId the primary key for the new commerce address restriction
	 * @return the new commerce address restriction
	 */
	@Override
	public CommerceAddressRestriction create(
		long commerceAddressRestrictionId) {

		CommerceAddressRestriction commerceAddressRestriction =
			new CommerceAddressRestrictionImpl();

		commerceAddressRestriction.setNew(true);
		commerceAddressRestriction.setPrimaryKey(commerceAddressRestrictionId);

		commerceAddressRestriction.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return commerceAddressRestriction;
	}

	/**
	 * Removes the commerce address restriction with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceAddressRestrictionId the primary key of the commerce address restriction
	 * @return the commerce address restriction that was removed
	 * @throws NoSuchAddressRestrictionException if a commerce address restriction with the primary key could not be found
	 */
	@Override
	public CommerceAddressRestriction remove(long commerceAddressRestrictionId)
		throws NoSuchAddressRestrictionException {

		return remove((Serializable)commerceAddressRestrictionId);
	}

	@Override
	protected CommerceAddressRestriction removeImpl(
		CommerceAddressRestriction commerceAddressRestriction) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceAddressRestriction)) {
				commerceAddressRestriction =
					(CommerceAddressRestriction)session.get(
						CommerceAddressRestrictionImpl.class,
						commerceAddressRestriction.getPrimaryKeyObj());
			}

			if (commerceAddressRestriction != null) {
				session.delete(commerceAddressRestriction);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceAddressRestriction != null) {
			clearCache(commerceAddressRestriction);
		}

		return commerceAddressRestriction;
	}

	@Override
	public CommerceAddressRestriction updateImpl(
		CommerceAddressRestriction commerceAddressRestriction) {

		boolean isNew = commerceAddressRestriction.isNew();

		if (!(commerceAddressRestriction instanceof
				CommerceAddressRestrictionModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(commerceAddressRestriction.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceAddressRestriction);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceAddressRestriction proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceAddressRestriction implementation " +
					commerceAddressRestriction.getClass());
		}

		CommerceAddressRestrictionModelImpl
			commerceAddressRestrictionModelImpl =
				(CommerceAddressRestrictionModelImpl)commerceAddressRestriction;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceAddressRestriction.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceAddressRestriction.setCreateDate(date);
			}
			else {
				commerceAddressRestriction.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceAddressRestrictionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceAddressRestriction.setModifiedDate(date);
			}
			else {
				commerceAddressRestriction.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceAddressRestriction);
			}
			else {
				commerceAddressRestriction =
					(CommerceAddressRestriction)session.merge(
						commerceAddressRestriction);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceAddressRestriction, false);

		if (isNew) {
			commerceAddressRestriction.setNew(false);
		}

		commerceAddressRestriction.resetOriginalValues();

		return commerceAddressRestriction;
	}

	/**
	 * Returns the commerce address restriction with the primary key or throws a <code>NoSuchAddressRestrictionException</code> if it could not be found.
	 *
	 * @param commerceAddressRestrictionId the primary key of the commerce address restriction
	 * @return the commerce address restriction
	 * @throws NoSuchAddressRestrictionException if a commerce address restriction with the primary key could not be found
	 */
	@Override
	public CommerceAddressRestriction findByPrimaryKey(
			long commerceAddressRestrictionId)
		throws NoSuchAddressRestrictionException {

		return findByPrimaryKey((Serializable)commerceAddressRestrictionId);
	}

	/**
	 * Returns the commerce address restriction with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceAddressRestrictionId the primary key of the commerce address restriction
	 * @return the commerce address restriction, or <code>null</code> if a commerce address restriction with the primary key could not be found
	 */
	@Override
	public CommerceAddressRestriction fetchByPrimaryKey(
		long commerceAddressRestrictionId) {

		return fetchByPrimaryKey((Serializable)commerceAddressRestrictionId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "commerceAddressRestrictionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEADDRESSRESTRICTION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceAddressRestrictionModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce address restriction persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByCountryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCountryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"countryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCountryId", new String[] {Long.class.getName()},
					new String[] {"countryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCountryId", new String[] {Long.class.getName()},
					new String[] {"countryId"}, false),
				_SQL_SELECT_COMMERCEADDRESSRESTRICTION_WHERE,
				_SQL_COUNT_COMMERCEADDRESSRESTRICTION_WHERE,
				CommerceAddressRestrictionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"commerceAddressRestriction.", "countryId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceAddressRestriction::getCountryId));

		_collectionPersistenceFinderByC_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"classNameId", "classPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "classPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "classPK"}, false),
			_SQL_SELECT_COMMERCEADDRESSRESTRICTION_WHERE,
			_SQL_COUNT_COMMERCEADDRESSRESTRICTION_WHERE,
			CommerceAddressRestrictionModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"commerceAddressRestriction.", "classNameId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceAddressRestriction::getClassNameId),
			new FinderColumn<>(
				"commerceAddressRestriction.", "classPK",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceAddressRestriction::getClassPK));

		_uniquePersistenceFinderByC_C_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"classNameId", "classPK", "countryId"}, 0, 0,
				false, CommerceAddressRestriction::getClassNameId,
				CommerceAddressRestriction::getClassPK,
				CommerceAddressRestriction::getCountryId),
			_SQL_SELECT_COMMERCEADDRESSRESTRICTION_WHERE, "",
			new FinderColumn<>(
				"commerceAddressRestriction.", "classNameId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceAddressRestriction::getClassNameId),
			new FinderColumn<>(
				"commerceAddressRestriction.", "classPK",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceAddressRestriction::getClassPK),
			new FinderColumn<>(
				"commerceAddressRestriction.", "countryId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceAddressRestriction::getCountryId));

		CommerceAddressRestrictionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceAddressRestrictionUtil.setPersistence(null);

		entityCache.removeCache(CommerceAddressRestrictionImpl.class.getName());
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
		CommerceAddressRestrictionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCEADDRESSRESTRICTION =
		"SELECT commerceAddressRestriction FROM CommerceAddressRestriction commerceAddressRestriction";

	private static final String _SQL_SELECT_COMMERCEADDRESSRESTRICTION_WHERE =
		"SELECT commerceAddressRestriction FROM CommerceAddressRestriction commerceAddressRestriction WHERE ";

	private static final String _SQL_COUNT_COMMERCEADDRESSRESTRICTION_WHERE =
		"SELECT COUNT(commerceAddressRestriction) FROM CommerceAddressRestriction commerceAddressRestriction WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-61174664