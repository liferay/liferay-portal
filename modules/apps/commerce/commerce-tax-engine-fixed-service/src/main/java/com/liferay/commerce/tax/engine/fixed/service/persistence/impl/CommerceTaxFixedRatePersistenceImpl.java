/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.engine.fixed.service.persistence.impl;

import com.liferay.commerce.tax.engine.fixed.exception.NoSuchTaxFixedRateException;
import com.liferay.commerce.tax.engine.fixed.model.CommerceTaxFixedRate;
import com.liferay.commerce.tax.engine.fixed.model.CommerceTaxFixedRateTable;
import com.liferay.commerce.tax.engine.fixed.model.impl.CommerceTaxFixedRateImpl;
import com.liferay.commerce.tax.engine.fixed.model.impl.CommerceTaxFixedRateModelImpl;
import com.liferay.commerce.tax.engine.fixed.service.persistence.CommerceTaxFixedRatePersistence;
import com.liferay.commerce.tax.engine.fixed.service.persistence.CommerceTaxFixedRateUtil;
import com.liferay.commerce.tax.engine.fixed.service.persistence.impl.constants.CommercePersistenceConstants;
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
 * The persistence implementation for the commerce tax fixed rate service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(service = CommerceTaxFixedRatePersistence.class)
public class CommerceTaxFixedRatePersistenceImpl
	extends BasePersistenceImpl
		<CommerceTaxFixedRate, NoSuchTaxFixedRateException>
	implements CommerceTaxFixedRatePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceTaxFixedRateUtil</code> to access the commerce tax fixed rate persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceTaxFixedRateImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceTaxFixedRate, NoSuchTaxFixedRateException>
			_collectionPersistenceFinderByCPTaxCategoryId;

	/**
	 * Returns an ordered range of all the commerce tax fixed rates where CPTaxCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTaxFixedRateModelImpl</code>.
	 * </p>
	 *
	 * @param CPTaxCategoryId the cp tax category ID
	 * @param start the lower bound of the range of commerce tax fixed rates
	 * @param end the upper bound of the range of commerce tax fixed rates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce tax fixed rates
	 */
	@Override
	public List<CommerceTaxFixedRate> findByCPTaxCategoryId(
		long CPTaxCategoryId, int start, int end,
		OrderByComparator<CommerceTaxFixedRate> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCPTaxCategoryId.find(
			finderCache, new Object[] {CPTaxCategoryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce tax fixed rate in the ordered set where CPTaxCategoryId = &#63;.
	 *
	 * @param CPTaxCategoryId the cp tax category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tax fixed rate
	 * @throws NoSuchTaxFixedRateException if a matching commerce tax fixed rate could not be found
	 */
	@Override
	public CommerceTaxFixedRate findByCPTaxCategoryId_First(
			long CPTaxCategoryId,
			OrderByComparator<CommerceTaxFixedRate> orderByComparator)
		throws NoSuchTaxFixedRateException {

		return _collectionPersistenceFinderByCPTaxCategoryId.findFirst(
			finderCache, new Object[] {CPTaxCategoryId}, orderByComparator);
	}

	/**
	 * Returns the first commerce tax fixed rate in the ordered set where CPTaxCategoryId = &#63;.
	 *
	 * @param CPTaxCategoryId the cp tax category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tax fixed rate, or <code>null</code> if a matching commerce tax fixed rate could not be found
	 */
	@Override
	public CommerceTaxFixedRate fetchByCPTaxCategoryId_First(
		long CPTaxCategoryId,
		OrderByComparator<CommerceTaxFixedRate> orderByComparator) {

		return _collectionPersistenceFinderByCPTaxCategoryId.fetchFirst(
			finderCache, new Object[] {CPTaxCategoryId}, orderByComparator);
	}

	/**
	 * Removes all the commerce tax fixed rates where CPTaxCategoryId = &#63; from the database.
	 *
	 * @param CPTaxCategoryId the cp tax category ID
	 */
	@Override
	public void removeByCPTaxCategoryId(long CPTaxCategoryId) {
		_collectionPersistenceFinderByCPTaxCategoryId.remove(
			finderCache, new Object[] {CPTaxCategoryId});
	}

	/**
	 * Returns the number of commerce tax fixed rates where CPTaxCategoryId = &#63;.
	 *
	 * @param CPTaxCategoryId the cp tax category ID
	 * @return the number of matching commerce tax fixed rates
	 */
	@Override
	public int countByCPTaxCategoryId(long CPTaxCategoryId) {
		return _collectionPersistenceFinderByCPTaxCategoryId.count(
			finderCache, new Object[] {CPTaxCategoryId});
	}

	private CollectionPersistenceFinder
		<CommerceTaxFixedRate, NoSuchTaxFixedRateException>
			_collectionPersistenceFinderByCommerceTaxMethodId;

	/**
	 * Returns an ordered range of all the commerce tax fixed rates where commerceTaxMethodId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTaxFixedRateModelImpl</code>.
	 * </p>
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param start the lower bound of the range of commerce tax fixed rates
	 * @param end the upper bound of the range of commerce tax fixed rates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce tax fixed rates
	 */
	@Override
	public List<CommerceTaxFixedRate> findByCommerceTaxMethodId(
		long commerceTaxMethodId, int start, int end,
		OrderByComparator<CommerceTaxFixedRate> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceTaxMethodId.find(
			finderCache, new Object[] {commerceTaxMethodId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce tax fixed rate in the ordered set where commerceTaxMethodId = &#63;.
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tax fixed rate
	 * @throws NoSuchTaxFixedRateException if a matching commerce tax fixed rate could not be found
	 */
	@Override
	public CommerceTaxFixedRate findByCommerceTaxMethodId_First(
			long commerceTaxMethodId,
			OrderByComparator<CommerceTaxFixedRate> orderByComparator)
		throws NoSuchTaxFixedRateException {

		return _collectionPersistenceFinderByCommerceTaxMethodId.findFirst(
			finderCache, new Object[] {commerceTaxMethodId}, orderByComparator);
	}

	/**
	 * Returns the first commerce tax fixed rate in the ordered set where commerceTaxMethodId = &#63;.
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tax fixed rate, or <code>null</code> if a matching commerce tax fixed rate could not be found
	 */
	@Override
	public CommerceTaxFixedRate fetchByCommerceTaxMethodId_First(
		long commerceTaxMethodId,
		OrderByComparator<CommerceTaxFixedRate> orderByComparator) {

		return _collectionPersistenceFinderByCommerceTaxMethodId.fetchFirst(
			finderCache, new Object[] {commerceTaxMethodId}, orderByComparator);
	}

	/**
	 * Removes all the commerce tax fixed rates where commerceTaxMethodId = &#63; from the database.
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 */
	@Override
	public void removeByCommerceTaxMethodId(long commerceTaxMethodId) {
		_collectionPersistenceFinderByCommerceTaxMethodId.remove(
			finderCache, new Object[] {commerceTaxMethodId});
	}

	/**
	 * Returns the number of commerce tax fixed rates where commerceTaxMethodId = &#63;.
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @return the number of matching commerce tax fixed rates
	 */
	@Override
	public int countByCommerceTaxMethodId(long commerceTaxMethodId) {
		return _collectionPersistenceFinderByCommerceTaxMethodId.count(
			finderCache, new Object[] {commerceTaxMethodId});
	}

	private UniquePersistenceFinder
		<CommerceTaxFixedRate, NoSuchTaxFixedRateException>
			_uniquePersistenceFinderByC_C;

	/**
	 * Returns the commerce tax fixed rate where CPTaxCategoryId = &#63; and commerceTaxMethodId = &#63; or throws a <code>NoSuchTaxFixedRateException</code> if it could not be found.
	 *
	 * @param CPTaxCategoryId the cp tax category ID
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @return the matching commerce tax fixed rate
	 * @throws NoSuchTaxFixedRateException if a matching commerce tax fixed rate could not be found
	 */
	@Override
	public CommerceTaxFixedRate findByC_C(
			long CPTaxCategoryId, long commerceTaxMethodId)
		throws NoSuchTaxFixedRateException {

		return _uniquePersistenceFinderByC_C.find(
			finderCache, new Object[] {CPTaxCategoryId, commerceTaxMethodId});
	}

	/**
	 * Returns the commerce tax fixed rate where CPTaxCategoryId = &#63; and commerceTaxMethodId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param CPTaxCategoryId the cp tax category ID
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce tax fixed rate, or <code>null</code> if a matching commerce tax fixed rate could not be found
	 */
	@Override
	public CommerceTaxFixedRate fetchByC_C(
		long CPTaxCategoryId, long commerceTaxMethodId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByC_C.fetch(
			finderCache, new Object[] {CPTaxCategoryId, commerceTaxMethodId},
			useFinderCache);
	}

	/**
	 * Removes the commerce tax fixed rate where CPTaxCategoryId = &#63; and commerceTaxMethodId = &#63; from the database.
	 *
	 * @param CPTaxCategoryId the cp tax category ID
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @return the commerce tax fixed rate that was removed
	 */
	@Override
	public CommerceTaxFixedRate removeByC_C(
			long CPTaxCategoryId, long commerceTaxMethodId)
		throws NoSuchTaxFixedRateException {

		CommerceTaxFixedRate commerceTaxFixedRate = findByC_C(
			CPTaxCategoryId, commerceTaxMethodId);

		return remove(commerceTaxFixedRate);
	}

	/**
	 * Returns the number of commerce tax fixed rates where CPTaxCategoryId = &#63; and commerceTaxMethodId = &#63;.
	 *
	 * @param CPTaxCategoryId the cp tax category ID
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @return the number of matching commerce tax fixed rates
	 */
	@Override
	public int countByC_C(long CPTaxCategoryId, long commerceTaxMethodId) {
		return _uniquePersistenceFinderByC_C.count(
			finderCache, new Object[] {CPTaxCategoryId, commerceTaxMethodId});
	}

	public CommerceTaxFixedRatePersistenceImpl() {
		setModelClass(CommerceTaxFixedRate.class);

		setModelImplClass(CommerceTaxFixedRateImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceTaxFixedRateTable.INSTANCE);
	}

	/**
	 * Creates a new commerce tax fixed rate with the primary key. Does not add the commerce tax fixed rate to the database.
	 *
	 * @param commerceTaxFixedRateId the primary key for the new commerce tax fixed rate
	 * @return the new commerce tax fixed rate
	 */
	@Override
	public CommerceTaxFixedRate create(long commerceTaxFixedRateId) {
		CommerceTaxFixedRate commerceTaxFixedRate =
			new CommerceTaxFixedRateImpl();

		commerceTaxFixedRate.setNew(true);
		commerceTaxFixedRate.setPrimaryKey(commerceTaxFixedRateId);

		commerceTaxFixedRate.setCompanyId(CompanyThreadLocal.getCompanyId());

		return commerceTaxFixedRate;
	}

	/**
	 * Removes the commerce tax fixed rate with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceTaxFixedRateId the primary key of the commerce tax fixed rate
	 * @return the commerce tax fixed rate that was removed
	 * @throws NoSuchTaxFixedRateException if a commerce tax fixed rate with the primary key could not be found
	 */
	@Override
	public CommerceTaxFixedRate remove(long commerceTaxFixedRateId)
		throws NoSuchTaxFixedRateException {

		return remove((Serializable)commerceTaxFixedRateId);
	}

	@Override
	protected CommerceTaxFixedRate removeImpl(
		CommerceTaxFixedRate commerceTaxFixedRate) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceTaxFixedRate)) {
				commerceTaxFixedRate = (CommerceTaxFixedRate)session.get(
					CommerceTaxFixedRateImpl.class,
					commerceTaxFixedRate.getPrimaryKeyObj());
			}

			if (commerceTaxFixedRate != null) {
				session.delete(commerceTaxFixedRate);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceTaxFixedRate != null) {
			clearCache(commerceTaxFixedRate);
		}

		return commerceTaxFixedRate;
	}

	@Override
	public CommerceTaxFixedRate updateImpl(
		CommerceTaxFixedRate commerceTaxFixedRate) {

		boolean isNew = commerceTaxFixedRate.isNew();

		if (!(commerceTaxFixedRate instanceof CommerceTaxFixedRateModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(commerceTaxFixedRate.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceTaxFixedRate);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceTaxFixedRate proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceTaxFixedRate implementation " +
					commerceTaxFixedRate.getClass());
		}

		CommerceTaxFixedRateModelImpl commerceTaxFixedRateModelImpl =
			(CommerceTaxFixedRateModelImpl)commerceTaxFixedRate;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceTaxFixedRate.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceTaxFixedRate.setCreateDate(date);
			}
			else {
				commerceTaxFixedRate.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceTaxFixedRateModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceTaxFixedRate.setModifiedDate(date);
			}
			else {
				commerceTaxFixedRate.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceTaxFixedRate);
			}
			else {
				commerceTaxFixedRate = (CommerceTaxFixedRate)session.merge(
					commerceTaxFixedRate);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceTaxFixedRate, false);

		if (isNew) {
			commerceTaxFixedRate.setNew(false);
		}

		commerceTaxFixedRate.resetOriginalValues();

		return commerceTaxFixedRate;
	}

	/**
	 * Returns the commerce tax fixed rate with the primary key or throws a <code>NoSuchTaxFixedRateException</code> if it could not be found.
	 *
	 * @param commerceTaxFixedRateId the primary key of the commerce tax fixed rate
	 * @return the commerce tax fixed rate
	 * @throws NoSuchTaxFixedRateException if a commerce tax fixed rate with the primary key could not be found
	 */
	@Override
	public CommerceTaxFixedRate findByPrimaryKey(long commerceTaxFixedRateId)
		throws NoSuchTaxFixedRateException {

		return findByPrimaryKey((Serializable)commerceTaxFixedRateId);
	}

	/**
	 * Returns the commerce tax fixed rate with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceTaxFixedRateId the primary key of the commerce tax fixed rate
	 * @return the commerce tax fixed rate, or <code>null</code> if a commerce tax fixed rate with the primary key could not be found
	 */
	@Override
	public CommerceTaxFixedRate fetchByPrimaryKey(long commerceTaxFixedRateId) {
		return fetchByPrimaryKey((Serializable)commerceTaxFixedRateId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "commerceTaxFixedRateId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCETAXFIXEDRATE;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceTaxFixedRateModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce tax fixed rate persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByCPTaxCategoryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCPTaxCategoryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"CPTaxCategoryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCPTaxCategoryId",
					new String[] {Long.class.getName()},
					new String[] {"CPTaxCategoryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCPTaxCategoryId",
					new String[] {Long.class.getName()},
					new String[] {"CPTaxCategoryId"}, false),
				_SQL_SELECT_COMMERCETAXFIXEDRATE_WHERE,
				_SQL_COUNT_COMMERCETAXFIXEDRATE_WHERE,
				CommerceTaxFixedRateModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"commerceTaxFixedRate.", "CPTaxCategoryId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceTaxFixedRate::getCPTaxCategoryId));

		_collectionPersistenceFinderByCommerceTaxMethodId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommerceTaxMethodId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commerceTaxMethodId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommerceTaxMethodId",
					new String[] {Long.class.getName()},
					new String[] {"commerceTaxMethodId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommerceTaxMethodId",
					new String[] {Long.class.getName()},
					new String[] {"commerceTaxMethodId"}, false),
				_SQL_SELECT_COMMERCETAXFIXEDRATE_WHERE,
				_SQL_COUNT_COMMERCETAXFIXEDRATE_WHERE,
				CommerceTaxFixedRateModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"commerceTaxFixedRate.", "commerceTaxMethodId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceTaxFixedRate::getCommerceTaxMethodId));

		_uniquePersistenceFinderByC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"CPTaxCategoryId", "commerceTaxMethodId"}, 0, 0,
				false, CommerceTaxFixedRate::getCPTaxCategoryId,
				CommerceTaxFixedRate::getCommerceTaxMethodId),
			_SQL_SELECT_COMMERCETAXFIXEDRATE_WHERE, "",
			new FinderColumn<>(
				"commerceTaxFixedRate.", "CPTaxCategoryId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceTaxFixedRate::getCPTaxCategoryId),
			new FinderColumn<>(
				"commerceTaxFixedRate.", "commerceTaxMethodId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceTaxFixedRate::getCommerceTaxMethodId));

		CommerceTaxFixedRateUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceTaxFixedRateUtil.setPersistence(null);

		entityCache.removeCache(CommerceTaxFixedRateImpl.class.getName());
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
		CommerceTaxFixedRateModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCETAXFIXEDRATE =
		"SELECT commerceTaxFixedRate FROM CommerceTaxFixedRate commerceTaxFixedRate";

	private static final String _SQL_SELECT_COMMERCETAXFIXEDRATE_WHERE =
		"SELECT commerceTaxFixedRate FROM CommerceTaxFixedRate commerceTaxFixedRate WHERE ";

	private static final String _SQL_COUNT_COMMERCETAXFIXEDRATE_WHERE =
		"SELECT COUNT(commerceTaxFixedRate) FROM CommerceTaxFixedRate commerceTaxFixedRate WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1926520219