/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipping.engine.fixed.service.persistence.impl;

import com.liferay.commerce.shipping.engine.fixed.exception.NoSuchShippingFixedOptionException;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOption;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOptionTable;
import com.liferay.commerce.shipping.engine.fixed.model.impl.CommerceShippingFixedOptionImpl;
import com.liferay.commerce.shipping.engine.fixed.model.impl.CommerceShippingFixedOptionModelImpl;
import com.liferay.commerce.shipping.engine.fixed.service.persistence.CommerceShippingFixedOptionPersistence;
import com.liferay.commerce.shipping.engine.fixed.service.persistence.CommerceShippingFixedOptionUtil;
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
 * The persistence implementation for the commerce shipping fixed option service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(service = CommerceShippingFixedOptionPersistence.class)
public class CommerceShippingFixedOptionPersistenceImpl
	extends BasePersistenceImpl
		<CommerceShippingFixedOption, NoSuchShippingFixedOptionException>
	implements CommerceShippingFixedOptionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceShippingFixedOptionUtil</code> to access the commerce shipping fixed option persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceShippingFixedOptionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceShippingFixedOption, NoSuchShippingFixedOptionException>
			_collectionPersistenceFinderByCommerceShippingMethodId;

	/**
	 * Returns an ordered range of all the commerce shipping fixed options where commerceShippingMethodId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShippingFixedOptionModelImpl</code>.
	 * </p>
	 *
	 * @param commerceShippingMethodId the commerce shipping method ID
	 * @param start the lower bound of the range of commerce shipping fixed options
	 * @param end the upper bound of the range of commerce shipping fixed options (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipping fixed options
	 */
	@Override
	public List<CommerceShippingFixedOption> findByCommerceShippingMethodId(
		long commerceShippingMethodId, int start, int end,
		OrderByComparator<CommerceShippingFixedOption> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceShippingMethodId.find(
			finderCache, new Object[] {commerceShippingMethodId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce shipping fixed option in the ordered set where commerceShippingMethodId = &#63;.
	 *
	 * @param commerceShippingMethodId the commerce shipping method ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipping fixed option
	 * @throws NoSuchShippingFixedOptionException if a matching commerce shipping fixed option could not be found
	 */
	@Override
	public CommerceShippingFixedOption findByCommerceShippingMethodId_First(
			long commerceShippingMethodId,
			OrderByComparator<CommerceShippingFixedOption> orderByComparator)
		throws NoSuchShippingFixedOptionException {

		return _collectionPersistenceFinderByCommerceShippingMethodId.findFirst(
			finderCache, new Object[] {commerceShippingMethodId},
			orderByComparator);
	}

	/**
	 * Returns the first commerce shipping fixed option in the ordered set where commerceShippingMethodId = &#63;.
	 *
	 * @param commerceShippingMethodId the commerce shipping method ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipping fixed option, or <code>null</code> if a matching commerce shipping fixed option could not be found
	 */
	@Override
	public CommerceShippingFixedOption fetchByCommerceShippingMethodId_First(
		long commerceShippingMethodId,
		OrderByComparator<CommerceShippingFixedOption> orderByComparator) {

		return _collectionPersistenceFinderByCommerceShippingMethodId.
			fetchFirst(
				finderCache, new Object[] {commerceShippingMethodId},
				orderByComparator);
	}

	/**
	 * Removes all the commerce shipping fixed options where commerceShippingMethodId = &#63; from the database.
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
	 * Returns the number of commerce shipping fixed options where commerceShippingMethodId = &#63;.
	 *
	 * @param commerceShippingMethodId the commerce shipping method ID
	 * @return the number of matching commerce shipping fixed options
	 */
	@Override
	public int countByCommerceShippingMethodId(long commerceShippingMethodId) {
		return _collectionPersistenceFinderByCommerceShippingMethodId.count(
			finderCache, new Object[] {commerceShippingMethodId});
	}

	private UniquePersistenceFinder
		<CommerceShippingFixedOption, NoSuchShippingFixedOptionException>
			_uniquePersistenceFinderByC_K;

	/**
	 * Returns the commerce shipping fixed option where companyId = &#63; and key = &#63; or throws a <code>NoSuchShippingFixedOptionException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param key the key
	 * @return the matching commerce shipping fixed option
	 * @throws NoSuchShippingFixedOptionException if a matching commerce shipping fixed option could not be found
	 */
	@Override
	public CommerceShippingFixedOption findByC_K(long companyId, String key)
		throws NoSuchShippingFixedOptionException {

		return _uniquePersistenceFinderByC_K.find(
			finderCache, new Object[] {companyId, key});
	}

	/**
	 * Returns the commerce shipping fixed option where companyId = &#63; and key = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param key the key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce shipping fixed option, or <code>null</code> if a matching commerce shipping fixed option could not be found
	 */
	@Override
	public CommerceShippingFixedOption fetchByC_K(
		long companyId, String key, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_K.fetch(
			finderCache, new Object[] {companyId, key}, useFinderCache);
	}

	/**
	 * Removes the commerce shipping fixed option where companyId = &#63; and key = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param key the key
	 * @return the commerce shipping fixed option that was removed
	 */
	@Override
	public CommerceShippingFixedOption removeByC_K(long companyId, String key)
		throws NoSuchShippingFixedOptionException {

		CommerceShippingFixedOption commerceShippingFixedOption = findByC_K(
			companyId, key);

		return remove(commerceShippingFixedOption);
	}

	/**
	 * Returns the number of commerce shipping fixed options where companyId = &#63; and key = &#63;.
	 *
	 * @param companyId the company ID
	 * @param key the key
	 * @return the number of matching commerce shipping fixed options
	 */
	@Override
	public int countByC_K(long companyId, String key) {
		return _uniquePersistenceFinderByC_K.count(
			finderCache, new Object[] {companyId, key});
	}

	public CommerceShippingFixedOptionPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("key", "key_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceShippingFixedOption.class);

		setModelImplClass(CommerceShippingFixedOptionImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceShippingFixedOptionTable.INSTANCE);
	}

	/**
	 * Creates a new commerce shipping fixed option with the primary key. Does not add the commerce shipping fixed option to the database.
	 *
	 * @param commerceShippingFixedOptionId the primary key for the new commerce shipping fixed option
	 * @return the new commerce shipping fixed option
	 */
	@Override
	public CommerceShippingFixedOption create(
		long commerceShippingFixedOptionId) {

		CommerceShippingFixedOption commerceShippingFixedOption =
			new CommerceShippingFixedOptionImpl();

		commerceShippingFixedOption.setNew(true);
		commerceShippingFixedOption.setPrimaryKey(
			commerceShippingFixedOptionId);

		commerceShippingFixedOption.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return commerceShippingFixedOption;
	}

	/**
	 * Removes the commerce shipping fixed option with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceShippingFixedOptionId the primary key of the commerce shipping fixed option
	 * @return the commerce shipping fixed option that was removed
	 * @throws NoSuchShippingFixedOptionException if a commerce shipping fixed option with the primary key could not be found
	 */
	@Override
	public CommerceShippingFixedOption remove(
			long commerceShippingFixedOptionId)
		throws NoSuchShippingFixedOptionException {

		return remove((Serializable)commerceShippingFixedOptionId);
	}

	@Override
	protected CommerceShippingFixedOption removeImpl(
		CommerceShippingFixedOption commerceShippingFixedOption) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceShippingFixedOption)) {
				commerceShippingFixedOption =
					(CommerceShippingFixedOption)session.get(
						CommerceShippingFixedOptionImpl.class,
						commerceShippingFixedOption.getPrimaryKeyObj());
			}

			if (commerceShippingFixedOption != null) {
				session.delete(commerceShippingFixedOption);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceShippingFixedOption != null) {
			clearCache(commerceShippingFixedOption);
		}

		return commerceShippingFixedOption;
	}

	@Override
	public CommerceShippingFixedOption updateImpl(
		CommerceShippingFixedOption commerceShippingFixedOption) {

		boolean isNew = commerceShippingFixedOption.isNew();

		if (!(commerceShippingFixedOption instanceof
				CommerceShippingFixedOptionModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					commerceShippingFixedOption.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceShippingFixedOption);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceShippingFixedOption proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceShippingFixedOption implementation " +
					commerceShippingFixedOption.getClass());
		}

		CommerceShippingFixedOptionModelImpl
			commerceShippingFixedOptionModelImpl =
				(CommerceShippingFixedOptionModelImpl)
					commerceShippingFixedOption;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceShippingFixedOption.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceShippingFixedOption.setCreateDate(date);
			}
			else {
				commerceShippingFixedOption.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceShippingFixedOptionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceShippingFixedOption.setModifiedDate(date);
			}
			else {
				commerceShippingFixedOption.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceShippingFixedOption);
			}
			else {
				commerceShippingFixedOption =
					(CommerceShippingFixedOption)session.merge(
						commerceShippingFixedOption);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceShippingFixedOption, false);

		if (isNew) {
			commerceShippingFixedOption.setNew(false);
		}

		commerceShippingFixedOption.resetOriginalValues();

		return commerceShippingFixedOption;
	}

	/**
	 * Returns the commerce shipping fixed option with the primary key or throws a <code>NoSuchShippingFixedOptionException</code> if it could not be found.
	 *
	 * @param commerceShippingFixedOptionId the primary key of the commerce shipping fixed option
	 * @return the commerce shipping fixed option
	 * @throws NoSuchShippingFixedOptionException if a commerce shipping fixed option with the primary key could not be found
	 */
	@Override
	public CommerceShippingFixedOption findByPrimaryKey(
			long commerceShippingFixedOptionId)
		throws NoSuchShippingFixedOptionException {

		return findByPrimaryKey((Serializable)commerceShippingFixedOptionId);
	}

	/**
	 * Returns the commerce shipping fixed option with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceShippingFixedOptionId the primary key of the commerce shipping fixed option
	 * @return the commerce shipping fixed option, or <code>null</code> if a commerce shipping fixed option with the primary key could not be found
	 */
	@Override
	public CommerceShippingFixedOption fetchByPrimaryKey(
		long commerceShippingFixedOptionId) {

		return fetchByPrimaryKey((Serializable)commerceShippingFixedOptionId);
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
		return "commerceShippingFixedOptionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCESHIPPINGFIXEDOPTION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceShippingFixedOptionModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce shipping fixed option persistence.
	 */
	@Activate
	public void activate() {
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
				_SQL_SELECT_COMMERCESHIPPINGFIXEDOPTION_WHERE,
				_SQL_COUNT_COMMERCESHIPPINGFIXEDOPTION_WHERE,
				CommerceShippingFixedOptionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"commerceShippingFixedOption.", "commerceShippingMethodId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceShippingFixedOption::getCommerceShippingMethodId));

		_uniquePersistenceFinderByC_K = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_K",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "key_"}, 0, 2, false,
				CommerceShippingFixedOption::getCompanyId,
				convertNullFunction(CommerceShippingFixedOption::getKey)),
			_SQL_SELECT_COMMERCESHIPPINGFIXEDOPTION_WHERE, "",
			new FinderColumn<>(
				"commerceShippingFixedOption.", "companyId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceShippingFixedOption::getCompanyId),
			new FinderColumn<>(
				"commerceShippingFixedOption.", "key", "key_",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceShippingFixedOption::getKey));

		CommerceShippingFixedOptionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceShippingFixedOptionUtil.setPersistence(null);

		entityCache.removeCache(
			CommerceShippingFixedOptionImpl.class.getName());
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
		CommerceShippingFixedOptionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCESHIPPINGFIXEDOPTION =
		"SELECT commerceShippingFixedOption FROM CommerceShippingFixedOption commerceShippingFixedOption";

	private static final String _SQL_SELECT_COMMERCESHIPPINGFIXEDOPTION_WHERE =
		"SELECT commerceShippingFixedOption FROM CommerceShippingFixedOption commerceShippingFixedOption WHERE ";

	private static final String _SQL_COUNT_COMMERCESHIPPINGFIXEDOPTION_WHERE =
		"SELECT COUNT(commerceShippingFixedOption) FROM CommerceShippingFixedOption commerceShippingFixedOption WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"key"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1286990889