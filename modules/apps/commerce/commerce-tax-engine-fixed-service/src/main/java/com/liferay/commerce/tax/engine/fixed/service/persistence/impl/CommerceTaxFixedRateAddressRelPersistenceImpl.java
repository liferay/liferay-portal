/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.engine.fixed.service.persistence.impl;

import com.liferay.commerce.tax.engine.fixed.exception.NoSuchTaxFixedRateAddressRelException;
import com.liferay.commerce.tax.engine.fixed.model.CommerceTaxFixedRateAddressRel;
import com.liferay.commerce.tax.engine.fixed.model.CommerceTaxFixedRateAddressRelTable;
import com.liferay.commerce.tax.engine.fixed.model.impl.CommerceTaxFixedRateAddressRelImpl;
import com.liferay.commerce.tax.engine.fixed.model.impl.CommerceTaxFixedRateAddressRelModelImpl;
import com.liferay.commerce.tax.engine.fixed.service.persistence.CommerceTaxFixedRateAddressRelPersistence;
import com.liferay.commerce.tax.engine.fixed.service.persistence.CommerceTaxFixedRateAddressRelUtil;
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
 * The persistence implementation for the commerce tax fixed rate address rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(service = CommerceTaxFixedRateAddressRelPersistence.class)
public class CommerceTaxFixedRateAddressRelPersistenceImpl
	extends BasePersistenceImpl
		<CommerceTaxFixedRateAddressRel, NoSuchTaxFixedRateAddressRelException>
	implements CommerceTaxFixedRateAddressRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceTaxFixedRateAddressRelUtil</code> to access the commerce tax fixed rate address rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceTaxFixedRateAddressRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceTaxFixedRateAddressRel, NoSuchTaxFixedRateAddressRelException>
			_collectionPersistenceFinderByCommerceTaxMethodId;

	/**
	 * Returns an ordered range of all the commerce tax fixed rate address rels where commerceTaxMethodId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTaxFixedRateAddressRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param start the lower bound of the range of commerce tax fixed rate address rels
	 * @param end the upper bound of the range of commerce tax fixed rate address rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce tax fixed rate address rels
	 */
	@Override
	public List<CommerceTaxFixedRateAddressRel> findByCommerceTaxMethodId(
		long commerceTaxMethodId, int start, int end,
		OrderByComparator<CommerceTaxFixedRateAddressRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceTaxMethodId.find(
			finderCache, new Object[] {commerceTaxMethodId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce tax fixed rate address rel in the ordered set where commerceTaxMethodId = &#63;.
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tax fixed rate address rel
	 * @throws NoSuchTaxFixedRateAddressRelException if a matching commerce tax fixed rate address rel could not be found
	 */
	@Override
	public CommerceTaxFixedRateAddressRel findByCommerceTaxMethodId_First(
			long commerceTaxMethodId,
			OrderByComparator<CommerceTaxFixedRateAddressRel> orderByComparator)
		throws NoSuchTaxFixedRateAddressRelException {

		return _collectionPersistenceFinderByCommerceTaxMethodId.findFirst(
			finderCache, new Object[] {commerceTaxMethodId}, orderByComparator);
	}

	/**
	 * Returns the first commerce tax fixed rate address rel in the ordered set where commerceTaxMethodId = &#63;.
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tax fixed rate address rel, or <code>null</code> if a matching commerce tax fixed rate address rel could not be found
	 */
	@Override
	public CommerceTaxFixedRateAddressRel fetchByCommerceTaxMethodId_First(
		long commerceTaxMethodId,
		OrderByComparator<CommerceTaxFixedRateAddressRel> orderByComparator) {

		return _collectionPersistenceFinderByCommerceTaxMethodId.fetchFirst(
			finderCache, new Object[] {commerceTaxMethodId}, orderByComparator);
	}

	/**
	 * Removes all the commerce tax fixed rate address rels where commerceTaxMethodId = &#63; from the database.
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 */
	@Override
	public void removeByCommerceTaxMethodId(long commerceTaxMethodId) {
		_collectionPersistenceFinderByCommerceTaxMethodId.remove(
			finderCache, new Object[] {commerceTaxMethodId});
	}

	/**
	 * Returns the number of commerce tax fixed rate address rels where commerceTaxMethodId = &#63;.
	 *
	 * @param commerceTaxMethodId the commerce tax method ID
	 * @return the number of matching commerce tax fixed rate address rels
	 */
	@Override
	public int countByCommerceTaxMethodId(long commerceTaxMethodId) {
		return _collectionPersistenceFinderByCommerceTaxMethodId.count(
			finderCache, new Object[] {commerceTaxMethodId});
	}

	private CollectionPersistenceFinder
		<CommerceTaxFixedRateAddressRel, NoSuchTaxFixedRateAddressRelException>
			_collectionPersistenceFinderByCPTaxCategoryId;

	/**
	 * Returns an ordered range of all the commerce tax fixed rate address rels where CPTaxCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTaxFixedRateAddressRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPTaxCategoryId the cp tax category ID
	 * @param start the lower bound of the range of commerce tax fixed rate address rels
	 * @param end the upper bound of the range of commerce tax fixed rate address rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce tax fixed rate address rels
	 */
	@Override
	public List<CommerceTaxFixedRateAddressRel> findByCPTaxCategoryId(
		long CPTaxCategoryId, int start, int end,
		OrderByComparator<CommerceTaxFixedRateAddressRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCPTaxCategoryId.find(
			finderCache, new Object[] {CPTaxCategoryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce tax fixed rate address rel in the ordered set where CPTaxCategoryId = &#63;.
	 *
	 * @param CPTaxCategoryId the cp tax category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tax fixed rate address rel
	 * @throws NoSuchTaxFixedRateAddressRelException if a matching commerce tax fixed rate address rel could not be found
	 */
	@Override
	public CommerceTaxFixedRateAddressRel findByCPTaxCategoryId_First(
			long CPTaxCategoryId,
			OrderByComparator<CommerceTaxFixedRateAddressRel> orderByComparator)
		throws NoSuchTaxFixedRateAddressRelException {

		return _collectionPersistenceFinderByCPTaxCategoryId.findFirst(
			finderCache, new Object[] {CPTaxCategoryId}, orderByComparator);
	}

	/**
	 * Returns the first commerce tax fixed rate address rel in the ordered set where CPTaxCategoryId = &#63;.
	 *
	 * @param CPTaxCategoryId the cp tax category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tax fixed rate address rel, or <code>null</code> if a matching commerce tax fixed rate address rel could not be found
	 */
	@Override
	public CommerceTaxFixedRateAddressRel fetchByCPTaxCategoryId_First(
		long CPTaxCategoryId,
		OrderByComparator<CommerceTaxFixedRateAddressRel> orderByComparator) {

		return _collectionPersistenceFinderByCPTaxCategoryId.fetchFirst(
			finderCache, new Object[] {CPTaxCategoryId}, orderByComparator);
	}

	/**
	 * Removes all the commerce tax fixed rate address rels where CPTaxCategoryId = &#63; from the database.
	 *
	 * @param CPTaxCategoryId the cp tax category ID
	 */
	@Override
	public void removeByCPTaxCategoryId(long CPTaxCategoryId) {
		_collectionPersistenceFinderByCPTaxCategoryId.remove(
			finderCache, new Object[] {CPTaxCategoryId});
	}

	/**
	 * Returns the number of commerce tax fixed rate address rels where CPTaxCategoryId = &#63;.
	 *
	 * @param CPTaxCategoryId the cp tax category ID
	 * @return the number of matching commerce tax fixed rate address rels
	 */
	@Override
	public int countByCPTaxCategoryId(long CPTaxCategoryId) {
		return _collectionPersistenceFinderByCPTaxCategoryId.count(
			finderCache, new Object[] {CPTaxCategoryId});
	}

	private CollectionPersistenceFinder
		<CommerceTaxFixedRateAddressRel, NoSuchTaxFixedRateAddressRelException>
			_collectionPersistenceFinderByCountryId;

	/**
	 * Returns an ordered range of all the commerce tax fixed rate address rels where countryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTaxFixedRateAddressRelModelImpl</code>.
	 * </p>
	 *
	 * @param countryId the country ID
	 * @param start the lower bound of the range of commerce tax fixed rate address rels
	 * @param end the upper bound of the range of commerce tax fixed rate address rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce tax fixed rate address rels
	 */
	@Override
	public List<CommerceTaxFixedRateAddressRel> findByCountryId(
		long countryId, int start, int end,
		OrderByComparator<CommerceTaxFixedRateAddressRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCountryId.find(
			finderCache, new Object[] {countryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce tax fixed rate address rel in the ordered set where countryId = &#63;.
	 *
	 * @param countryId the country ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tax fixed rate address rel
	 * @throws NoSuchTaxFixedRateAddressRelException if a matching commerce tax fixed rate address rel could not be found
	 */
	@Override
	public CommerceTaxFixedRateAddressRel findByCountryId_First(
			long countryId,
			OrderByComparator<CommerceTaxFixedRateAddressRel> orderByComparator)
		throws NoSuchTaxFixedRateAddressRelException {

		return _collectionPersistenceFinderByCountryId.findFirst(
			finderCache, new Object[] {countryId}, orderByComparator);
	}

	/**
	 * Returns the first commerce tax fixed rate address rel in the ordered set where countryId = &#63;.
	 *
	 * @param countryId the country ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tax fixed rate address rel, or <code>null</code> if a matching commerce tax fixed rate address rel could not be found
	 */
	@Override
	public CommerceTaxFixedRateAddressRel fetchByCountryId_First(
		long countryId,
		OrderByComparator<CommerceTaxFixedRateAddressRel> orderByComparator) {

		return _collectionPersistenceFinderByCountryId.fetchFirst(
			finderCache, new Object[] {countryId}, orderByComparator);
	}

	/**
	 * Removes all the commerce tax fixed rate address rels where countryId = &#63; from the database.
	 *
	 * @param countryId the country ID
	 */
	@Override
	public void removeByCountryId(long countryId) {
		_collectionPersistenceFinderByCountryId.remove(
			finderCache, new Object[] {countryId});
	}

	/**
	 * Returns the number of commerce tax fixed rate address rels where countryId = &#63;.
	 *
	 * @param countryId the country ID
	 * @return the number of matching commerce tax fixed rate address rels
	 */
	@Override
	public int countByCountryId(long countryId) {
		return _collectionPersistenceFinderByCountryId.count(
			finderCache, new Object[] {countryId});
	}

	public CommerceTaxFixedRateAddressRelPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put(
			"commerceTaxFixedRateAddressRelId", "CTaxFixedRateAddressRelId");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceTaxFixedRateAddressRel.class);

		setModelImplClass(CommerceTaxFixedRateAddressRelImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceTaxFixedRateAddressRelTable.INSTANCE);
	}

	/**
	 * Creates a new commerce tax fixed rate address rel with the primary key. Does not add the commerce tax fixed rate address rel to the database.
	 *
	 * @param commerceTaxFixedRateAddressRelId the primary key for the new commerce tax fixed rate address rel
	 * @return the new commerce tax fixed rate address rel
	 */
	@Override
	public CommerceTaxFixedRateAddressRel create(
		long commerceTaxFixedRateAddressRelId) {

		CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel =
			new CommerceTaxFixedRateAddressRelImpl();

		commerceTaxFixedRateAddressRel.setNew(true);
		commerceTaxFixedRateAddressRel.setPrimaryKey(
			commerceTaxFixedRateAddressRelId);

		commerceTaxFixedRateAddressRel.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return commerceTaxFixedRateAddressRel;
	}

	/**
	 * Removes the commerce tax fixed rate address rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceTaxFixedRateAddressRelId the primary key of the commerce tax fixed rate address rel
	 * @return the commerce tax fixed rate address rel that was removed
	 * @throws NoSuchTaxFixedRateAddressRelException if a commerce tax fixed rate address rel with the primary key could not be found
	 */
	@Override
	public CommerceTaxFixedRateAddressRel remove(
			long commerceTaxFixedRateAddressRelId)
		throws NoSuchTaxFixedRateAddressRelException {

		return remove((Serializable)commerceTaxFixedRateAddressRelId);
	}

	@Override
	protected CommerceTaxFixedRateAddressRel removeImpl(
		CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceTaxFixedRateAddressRel)) {
				commerceTaxFixedRateAddressRel =
					(CommerceTaxFixedRateAddressRel)session.get(
						CommerceTaxFixedRateAddressRelImpl.class,
						commerceTaxFixedRateAddressRel.getPrimaryKeyObj());
			}

			if (commerceTaxFixedRateAddressRel != null) {
				session.delete(commerceTaxFixedRateAddressRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceTaxFixedRateAddressRel != null) {
			clearCache(commerceTaxFixedRateAddressRel);
		}

		return commerceTaxFixedRateAddressRel;
	}

	@Override
	public CommerceTaxFixedRateAddressRel updateImpl(
		CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel) {

		boolean isNew = commerceTaxFixedRateAddressRel.isNew();

		if (!(commerceTaxFixedRateAddressRel instanceof
				CommerceTaxFixedRateAddressRelModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					commerceTaxFixedRateAddressRel.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceTaxFixedRateAddressRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceTaxFixedRateAddressRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceTaxFixedRateAddressRel implementation " +
					commerceTaxFixedRateAddressRel.getClass());
		}

		CommerceTaxFixedRateAddressRelModelImpl
			commerceTaxFixedRateAddressRelModelImpl =
				(CommerceTaxFixedRateAddressRelModelImpl)
					commerceTaxFixedRateAddressRel;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceTaxFixedRateAddressRel.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceTaxFixedRateAddressRel.setCreateDate(date);
			}
			else {
				commerceTaxFixedRateAddressRel.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceTaxFixedRateAddressRelModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceTaxFixedRateAddressRel.setModifiedDate(date);
			}
			else {
				commerceTaxFixedRateAddressRel.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceTaxFixedRateAddressRel);
			}
			else {
				commerceTaxFixedRateAddressRel =
					(CommerceTaxFixedRateAddressRel)session.merge(
						commerceTaxFixedRateAddressRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceTaxFixedRateAddressRel, false);

		if (isNew) {
			commerceTaxFixedRateAddressRel.setNew(false);
		}

		commerceTaxFixedRateAddressRel.resetOriginalValues();

		return commerceTaxFixedRateAddressRel;
	}

	/**
	 * Returns the commerce tax fixed rate address rel with the primary key or throws a <code>NoSuchTaxFixedRateAddressRelException</code> if it could not be found.
	 *
	 * @param commerceTaxFixedRateAddressRelId the primary key of the commerce tax fixed rate address rel
	 * @return the commerce tax fixed rate address rel
	 * @throws NoSuchTaxFixedRateAddressRelException if a commerce tax fixed rate address rel with the primary key could not be found
	 */
	@Override
	public CommerceTaxFixedRateAddressRel findByPrimaryKey(
			long commerceTaxFixedRateAddressRelId)
		throws NoSuchTaxFixedRateAddressRelException {

		return findByPrimaryKey((Serializable)commerceTaxFixedRateAddressRelId);
	}

	/**
	 * Returns the commerce tax fixed rate address rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceTaxFixedRateAddressRelId the primary key of the commerce tax fixed rate address rel
	 * @return the commerce tax fixed rate address rel, or <code>null</code> if a commerce tax fixed rate address rel with the primary key could not be found
	 */
	@Override
	public CommerceTaxFixedRateAddressRel fetchByPrimaryKey(
		long commerceTaxFixedRateAddressRelId) {

		return fetchByPrimaryKey(
			(Serializable)commerceTaxFixedRateAddressRelId);
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
		return "CTaxFixedRateAddressRelId";
	}

	@Override
	protected String getPKFieldName() {
		return "commerceTaxFixedRateAddressRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCETAXFIXEDRATEADDRESSREL;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceTaxFixedRateAddressRelModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce tax fixed rate address rel persistence.
	 */
	@Activate
	public void activate() {
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
				_SQL_SELECT_COMMERCETAXFIXEDRATEADDRESSREL_WHERE,
				_SQL_COUNT_COMMERCETAXFIXEDRATEADDRESSREL_WHERE,
				CommerceTaxFixedRateAddressRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceTaxFixedRateAddressRel.", "commerceTaxMethodId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceTaxFixedRateAddressRel::getCommerceTaxMethodId));

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
				_SQL_SELECT_COMMERCETAXFIXEDRATEADDRESSREL_WHERE,
				_SQL_COUNT_COMMERCETAXFIXEDRATEADDRESSREL_WHERE,
				CommerceTaxFixedRateAddressRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceTaxFixedRateAddressRel.", "CPTaxCategoryId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceTaxFixedRateAddressRel::getCPTaxCategoryId));

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
				_SQL_SELECT_COMMERCETAXFIXEDRATEADDRESSREL_WHERE,
				_SQL_COUNT_COMMERCETAXFIXEDRATEADDRESSREL_WHERE,
				CommerceTaxFixedRateAddressRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceTaxFixedRateAddressRel.", "countryId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceTaxFixedRateAddressRel::getCountryId));

		CommerceTaxFixedRateAddressRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceTaxFixedRateAddressRelUtil.setPersistence(null);

		entityCache.removeCache(
			CommerceTaxFixedRateAddressRelImpl.class.getName());
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
		CommerceTaxFixedRateAddressRelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCETAXFIXEDRATEADDRESSREL =
		"SELECT commerceTaxFixedRateAddressRel FROM CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel";

	private static final String
		_SQL_SELECT_COMMERCETAXFIXEDRATEADDRESSREL_WHERE =
			"SELECT commerceTaxFixedRateAddressRel FROM CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel WHERE ";

	private static final String
		_SQL_COUNT_COMMERCETAXFIXEDRATEADDRESSREL_WHERE =
			"SELECT COUNT(commerceTaxFixedRateAddressRel) FROM CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommerceTaxFixedRateAddressRel exists with the key {";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"commerceTaxFixedRateAddressRelId"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-991742516