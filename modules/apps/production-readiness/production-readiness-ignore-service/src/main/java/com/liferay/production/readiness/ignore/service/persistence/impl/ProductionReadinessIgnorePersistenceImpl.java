/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.ignore.service.persistence.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.production.readiness.ignore.exception.NoSuchProductionReadinessIgnoreException;
import com.liferay.production.readiness.ignore.model.ProductionReadinessIgnore;
import com.liferay.production.readiness.ignore.model.ProductionReadinessIgnoreTable;
import com.liferay.production.readiness.ignore.model.impl.ProductionReadinessIgnoreImpl;
import com.liferay.production.readiness.ignore.model.impl.ProductionReadinessIgnoreModelImpl;
import com.liferay.production.readiness.ignore.service.persistence.ProductionReadinessIgnorePersistence;
import com.liferay.production.readiness.ignore.service.persistence.ProductionReadinessIgnoreUtil;
import com.liferay.production.readiness.ignore.service.persistence.impl.constants.PRPersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the production readiness ignore service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = ProductionReadinessIgnorePersistence.class)
public class ProductionReadinessIgnorePersistenceImpl
	extends BasePersistenceImpl<ProductionReadinessIgnore>
	implements ProductionReadinessIgnorePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ProductionReadinessIgnoreUtil</code> to access the production readiness ignore persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ProductionReadinessIgnoreImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;

	/**
	 * Returns all the production readiness ignores where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching production readiness ignores
	 */
	@Override
	public List<ProductionReadinessIgnore> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the production readiness ignores where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ProductionReadinessIgnoreModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of production readiness ignores
	 * @param end the upper bound of the range of production readiness ignores (not inclusive)
	 * @return the range of matching production readiness ignores
	 */
	@Override
	public List<ProductionReadinessIgnore> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the production readiness ignores where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ProductionReadinessIgnoreModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of production readiness ignores
	 * @param end the upper bound of the range of production readiness ignores (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching production readiness ignores
	 */
	@Override
	public List<ProductionReadinessIgnore> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<ProductionReadinessIgnore> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the production readiness ignores where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ProductionReadinessIgnoreModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of production readiness ignores
	 * @param end the upper bound of the range of production readiness ignores (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching production readiness ignores
	 */
	@Override
	public List<ProductionReadinessIgnore> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<ProductionReadinessIgnore> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByCompanyId;
				finderArgs = new Object[] {companyId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByCompanyId;
			finderArgs = new Object[] {
				companyId, start, end, orderByComparator
			};
		}

		List<ProductionReadinessIgnore> list = null;

		if (useFinderCache) {
			list = (List<ProductionReadinessIgnore>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ProductionReadinessIgnore productionReadinessIgnore :
						list) {

					if (companyId != productionReadinessIgnore.getCompanyId()) {
						list = null;

						break;
					}
				}
			}
		}

		if (list == null) {
			StringBundler sb = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					3 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				sb = new StringBundler(3);
			}

			sb.append(_SQL_SELECT_PRODUCTIONREADINESSIGNORE_WHERE);

			sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ProductionReadinessIgnoreModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				list = (List<ProductionReadinessIgnore>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Returns the first production readiness ignore in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching production readiness ignore
	 * @throws NoSuchProductionReadinessIgnoreException if a matching production readiness ignore could not be found
	 */
	@Override
	public ProductionReadinessIgnore findByCompanyId_First(
			long companyId,
			OrderByComparator<ProductionReadinessIgnore> orderByComparator)
		throws NoSuchProductionReadinessIgnoreException {

		ProductionReadinessIgnore productionReadinessIgnore =
			fetchByCompanyId_First(companyId, orderByComparator);

		if (productionReadinessIgnore != null) {
			return productionReadinessIgnore;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchProductionReadinessIgnoreException(sb.toString());
	}

	/**
	 * Returns the first production readiness ignore in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching production readiness ignore, or <code>null</code> if a matching production readiness ignore could not be found
	 */
	@Override
	public ProductionReadinessIgnore fetchByCompanyId_First(
		long companyId,
		OrderByComparator<ProductionReadinessIgnore> orderByComparator) {

		List<ProductionReadinessIgnore> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Removes all the production readiness ignores where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (ProductionReadinessIgnore productionReadinessIgnore :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(productionReadinessIgnore);
		}
	}

	/**
	 * Returns the number of production readiness ignores where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching production readiness ignores
	 */
	@Override
	public int countByCompanyId(long companyId) {
		FinderPath finderPath = _finderPathCountByCompanyId;

		Object[] finderArgs = new Object[] {companyId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_PRODUCTIONREADINESSIGNORE_WHERE);

			sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				count = (Long)query.uniqueResult();

				finderCache.putResult(finderPath, finderArgs, count);
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	private static final String _FINDER_COLUMN_COMPANYID_COMPANYID_2 =
		"productionReadinessIgnore.companyId = ?";

	private FinderPath _finderPathFetchByC_R;

	/**
	 * Returns the production readiness ignore where companyId = &#63; and ruleKey = &#63; or throws a <code>NoSuchProductionReadinessIgnoreException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param ruleKey the rule key
	 * @return the matching production readiness ignore
	 * @throws NoSuchProductionReadinessIgnoreException if a matching production readiness ignore could not be found
	 */
	@Override
	public ProductionReadinessIgnore findByC_R(long companyId, String ruleKey)
		throws NoSuchProductionReadinessIgnoreException {

		ProductionReadinessIgnore productionReadinessIgnore = fetchByC_R(
			companyId, ruleKey);

		if (productionReadinessIgnore == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("companyId=");
			sb.append(companyId);

			sb.append(", ruleKey=");
			sb.append(ruleKey);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchProductionReadinessIgnoreException(sb.toString());
		}

		return productionReadinessIgnore;
	}

	/**
	 * Returns the production readiness ignore where companyId = &#63; and ruleKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param ruleKey the rule key
	 * @return the matching production readiness ignore, or <code>null</code> if a matching production readiness ignore could not be found
	 */
	@Override
	public ProductionReadinessIgnore fetchByC_R(
		long companyId, String ruleKey) {

		return fetchByC_R(companyId, ruleKey, true);
	}

	/**
	 * Returns the production readiness ignore where companyId = &#63; and ruleKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param ruleKey the rule key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching production readiness ignore, or <code>null</code> if a matching production readiness ignore could not be found
	 */
	@Override
	public ProductionReadinessIgnore fetchByC_R(
		long companyId, String ruleKey, boolean useFinderCache) {

		ruleKey = Objects.toString(ruleKey, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {companyId, ruleKey};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByC_R, finderArgs, this);
		}

		if (result instanceof ProductionReadinessIgnore) {
			ProductionReadinessIgnore productionReadinessIgnore =
				(ProductionReadinessIgnore)result;

			if ((companyId != productionReadinessIgnore.getCompanyId()) ||
				!Objects.equals(
					ruleKey, productionReadinessIgnore.getRuleKey())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_PRODUCTIONREADINESSIGNORE_WHERE);

			sb.append(_FINDER_COLUMN_C_R_COMPANYID_2);

			boolean bindRuleKey = false;

			if (ruleKey.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_R_RULEKEY_3);
			}
			else {
				bindRuleKey = true;

				sb.append(_FINDER_COLUMN_C_R_RULEKEY_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				if (bindRuleKey) {
					queryPos.add(ruleKey);
				}

				List<ProductionReadinessIgnore> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByC_R, finderArgs, list);
					}
				}
				else {
					ProductionReadinessIgnore productionReadinessIgnore =
						list.get(0);

					result = productionReadinessIgnore;

					cacheResult(productionReadinessIgnore);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		if (result instanceof List<?>) {
			return null;
		}
		else {
			return (ProductionReadinessIgnore)result;
		}
	}

	/**
	 * Removes the production readiness ignore where companyId = &#63; and ruleKey = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param ruleKey the rule key
	 * @return the production readiness ignore that was removed
	 */
	@Override
	public ProductionReadinessIgnore removeByC_R(long companyId, String ruleKey)
		throws NoSuchProductionReadinessIgnoreException {

		ProductionReadinessIgnore productionReadinessIgnore = findByC_R(
			companyId, ruleKey);

		return remove(productionReadinessIgnore);
	}

	/**
	 * Returns the number of production readiness ignores where companyId = &#63; and ruleKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ruleKey the rule key
	 * @return the number of matching production readiness ignores
	 */
	@Override
	public int countByC_R(long companyId, String ruleKey) {
		ProductionReadinessIgnore productionReadinessIgnore = fetchByC_R(
			companyId, ruleKey);

		if (productionReadinessIgnore == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_C_R_COMPANYID_2 =
		"productionReadinessIgnore.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_R_RULEKEY_2 =
		"productionReadinessIgnore.ruleKey = ?";

	private static final String _FINDER_COLUMN_C_R_RULEKEY_3 =
		"(productionReadinessIgnore.ruleKey IS NULL OR productionReadinessIgnore.ruleKey = '')";

	public ProductionReadinessIgnorePersistenceImpl() {
		setModelClass(ProductionReadinessIgnore.class);

		setModelImplClass(ProductionReadinessIgnoreImpl.class);
		setModelPKClass(long.class);

		setTable(ProductionReadinessIgnoreTable.INSTANCE);
	}

	/**
	 * Caches the production readiness ignore in the entity cache if it is enabled.
	 *
	 * @param productionReadinessIgnore the production readiness ignore
	 */
	@Override
	public void cacheResult(
		ProductionReadinessIgnore productionReadinessIgnore) {

		entityCache.putResult(
			ProductionReadinessIgnoreImpl.class,
			productionReadinessIgnore.getPrimaryKey(),
			productionReadinessIgnore);

		finderCache.putResult(
			_finderPathFetchByC_R,
			new Object[] {
				productionReadinessIgnore.getCompanyId(),
				productionReadinessIgnore.getRuleKey()
			},
			productionReadinessIgnore);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the production readiness ignores in the entity cache if it is enabled.
	 *
	 * @param productionReadinessIgnores the production readiness ignores
	 */
	@Override
	public void cacheResult(
		List<ProductionReadinessIgnore> productionReadinessIgnores) {

		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (productionReadinessIgnores.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (ProductionReadinessIgnore productionReadinessIgnore :
				productionReadinessIgnores) {

			if (entityCache.getResult(
					ProductionReadinessIgnoreImpl.class,
					productionReadinessIgnore.getPrimaryKey()) == null) {

				cacheResult(productionReadinessIgnore);
			}
		}
	}

	/**
	 * Clears the cache for all production readiness ignores.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(ProductionReadinessIgnoreImpl.class);

		finderCache.clearCache(ProductionReadinessIgnoreImpl.class);
	}

	/**
	 * Clears the cache for the production readiness ignore.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(
		ProductionReadinessIgnore productionReadinessIgnore) {

		entityCache.removeResult(
			ProductionReadinessIgnoreImpl.class, productionReadinessIgnore);
	}

	@Override
	public void clearCache(
		List<ProductionReadinessIgnore> productionReadinessIgnores) {

		for (ProductionReadinessIgnore productionReadinessIgnore :
				productionReadinessIgnores) {

			entityCache.removeResult(
				ProductionReadinessIgnoreImpl.class, productionReadinessIgnore);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(ProductionReadinessIgnoreImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				ProductionReadinessIgnoreImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		ProductionReadinessIgnoreModelImpl productionReadinessIgnoreModelImpl) {

		Object[] args = new Object[] {
			productionReadinessIgnoreModelImpl.getCompanyId(),
			productionReadinessIgnoreModelImpl.getRuleKey()
		};

		finderCache.putResult(
			_finderPathFetchByC_R, args, productionReadinessIgnoreModelImpl);
	}

	/**
	 * Creates a new production readiness ignore with the primary key. Does not add the production readiness ignore to the database.
	 *
	 * @param productionReadinessIgnoreId the primary key for the new production readiness ignore
	 * @return the new production readiness ignore
	 */
	@Override
	public ProductionReadinessIgnore create(long productionReadinessIgnoreId) {
		ProductionReadinessIgnore productionReadinessIgnore =
			new ProductionReadinessIgnoreImpl();

		productionReadinessIgnore.setNew(true);
		productionReadinessIgnore.setPrimaryKey(productionReadinessIgnoreId);

		productionReadinessIgnore.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return productionReadinessIgnore;
	}

	/**
	 * Removes the production readiness ignore with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param productionReadinessIgnoreId the primary key of the production readiness ignore
	 * @return the production readiness ignore that was removed
	 * @throws NoSuchProductionReadinessIgnoreException if a production readiness ignore with the primary key could not be found
	 */
	@Override
	public ProductionReadinessIgnore remove(long productionReadinessIgnoreId)
		throws NoSuchProductionReadinessIgnoreException {

		return remove((Serializable)productionReadinessIgnoreId);
	}

	/**
	 * Removes the production readiness ignore with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the production readiness ignore
	 * @return the production readiness ignore that was removed
	 * @throws NoSuchProductionReadinessIgnoreException if a production readiness ignore with the primary key could not be found
	 */
	@Override
	public ProductionReadinessIgnore remove(Serializable primaryKey)
		throws NoSuchProductionReadinessIgnoreException {

		Session session = null;

		try {
			session = openSession();

			ProductionReadinessIgnore productionReadinessIgnore =
				(ProductionReadinessIgnore)session.get(
					ProductionReadinessIgnoreImpl.class, primaryKey);

			if (productionReadinessIgnore == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchProductionReadinessIgnoreException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(productionReadinessIgnore);
		}
		catch (NoSuchProductionReadinessIgnoreException noSuchEntityException) {
			throw noSuchEntityException;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	protected ProductionReadinessIgnore removeImpl(
		ProductionReadinessIgnore productionReadinessIgnore) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(productionReadinessIgnore)) {
				productionReadinessIgnore =
					(ProductionReadinessIgnore)session.get(
						ProductionReadinessIgnoreImpl.class,
						productionReadinessIgnore.getPrimaryKeyObj());
			}

			if (productionReadinessIgnore != null) {
				session.delete(productionReadinessIgnore);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (productionReadinessIgnore != null) {
			clearCache(productionReadinessIgnore);
		}

		return productionReadinessIgnore;
	}

	@Override
	public ProductionReadinessIgnore updateImpl(
		ProductionReadinessIgnore productionReadinessIgnore) {

		boolean isNew = productionReadinessIgnore.isNew();

		if (!(productionReadinessIgnore instanceof
				ProductionReadinessIgnoreModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(productionReadinessIgnore.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					productionReadinessIgnore);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in productionReadinessIgnore proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ProductionReadinessIgnore implementation " +
					productionReadinessIgnore.getClass());
		}

		ProductionReadinessIgnoreModelImpl productionReadinessIgnoreModelImpl =
			(ProductionReadinessIgnoreModelImpl)productionReadinessIgnore;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (productionReadinessIgnore.getCreateDate() == null)) {
			if (serviceContext == null) {
				productionReadinessIgnore.setCreateDate(date);
			}
			else {
				productionReadinessIgnore.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!productionReadinessIgnoreModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				productionReadinessIgnore.setModifiedDate(date);
			}
			else {
				productionReadinessIgnore.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(productionReadinessIgnore);
			}
			else {
				productionReadinessIgnore =
					(ProductionReadinessIgnore)session.merge(
						productionReadinessIgnore);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			ProductionReadinessIgnoreImpl.class,
			productionReadinessIgnoreModelImpl, false, true);

		cacheUniqueFindersCache(productionReadinessIgnoreModelImpl);

		if (isNew) {
			productionReadinessIgnore.setNew(false);
		}

		productionReadinessIgnore.resetOriginalValues();

		return productionReadinessIgnore;
	}

	/**
	 * Returns the production readiness ignore with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the production readiness ignore
	 * @return the production readiness ignore
	 * @throws NoSuchProductionReadinessIgnoreException if a production readiness ignore with the primary key could not be found
	 */
	@Override
	public ProductionReadinessIgnore findByPrimaryKey(Serializable primaryKey)
		throws NoSuchProductionReadinessIgnoreException {

		ProductionReadinessIgnore productionReadinessIgnore = fetchByPrimaryKey(
			primaryKey);

		if (productionReadinessIgnore == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchProductionReadinessIgnoreException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return productionReadinessIgnore;
	}

	/**
	 * Returns the production readiness ignore with the primary key or throws a <code>NoSuchProductionReadinessIgnoreException</code> if it could not be found.
	 *
	 * @param productionReadinessIgnoreId the primary key of the production readiness ignore
	 * @return the production readiness ignore
	 * @throws NoSuchProductionReadinessIgnoreException if a production readiness ignore with the primary key could not be found
	 */
	@Override
	public ProductionReadinessIgnore findByPrimaryKey(
			long productionReadinessIgnoreId)
		throws NoSuchProductionReadinessIgnoreException {

		return findByPrimaryKey((Serializable)productionReadinessIgnoreId);
	}

	/**
	 * Returns the production readiness ignore with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param productionReadinessIgnoreId the primary key of the production readiness ignore
	 * @return the production readiness ignore, or <code>null</code> if a production readiness ignore with the primary key could not be found
	 */
	@Override
	public ProductionReadinessIgnore fetchByPrimaryKey(
		long productionReadinessIgnoreId) {

		return fetchByPrimaryKey((Serializable)productionReadinessIgnoreId);
	}

	/**
	 * Returns all the production readiness ignores.
	 *
	 * @return the production readiness ignores
	 */
	@Override
	public List<ProductionReadinessIgnore> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the production readiness ignores.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ProductionReadinessIgnoreModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of production readiness ignores
	 * @param end the upper bound of the range of production readiness ignores (not inclusive)
	 * @return the range of production readiness ignores
	 */
	@Override
	public List<ProductionReadinessIgnore> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the production readiness ignores.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ProductionReadinessIgnoreModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of production readiness ignores
	 * @param end the upper bound of the range of production readiness ignores (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of production readiness ignores
	 */
	@Override
	public List<ProductionReadinessIgnore> findAll(
		int start, int end,
		OrderByComparator<ProductionReadinessIgnore> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the production readiness ignores.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ProductionReadinessIgnoreModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of production readiness ignores
	 * @param end the upper bound of the range of production readiness ignores (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of production readiness ignores
	 */
	@Override
	public List<ProductionReadinessIgnore> findAll(
		int start, int end,
		OrderByComparator<ProductionReadinessIgnore> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindAll;
				finderArgs = FINDER_ARGS_EMPTY;
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindAll;
			finderArgs = new Object[] {start, end, orderByComparator};
		}

		List<ProductionReadinessIgnore> list = null;

		if (useFinderCache) {
			list = (List<ProductionReadinessIgnore>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_PRODUCTIONREADINESSIGNORE);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_PRODUCTIONREADINESSIGNORE;

				sql = sql.concat(
					ProductionReadinessIgnoreModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<ProductionReadinessIgnore>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Removes all the production readiness ignores from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (ProductionReadinessIgnore productionReadinessIgnore : findAll()) {
			remove(productionReadinessIgnore);
		}
	}

	/**
	 * Returns the number of production readiness ignores.
	 *
	 * @return the number of production readiness ignores
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(
					_SQL_COUNT_PRODUCTIONREADINESSIGNORE);

				count = (Long)query.uniqueResult();

				finderCache.putResult(
					_finderPathCountAll, FINDER_ARGS_EMPTY, count);
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "productionReadinessIgnoreId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PRODUCTIONREADINESSIGNORE;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ProductionReadinessIgnoreModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the production readiness ignore persistence.
	 */
	@Activate
	public void activate() {
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		_finderPathWithPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathCountAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0], new String[0], false);

		_finderPathWithPaginationFindByCompanyId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCompanyId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"companyId"}, true);

		_finderPathWithoutPaginationFindByCompanyId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCompanyId",
			new String[] {Long.class.getName()}, new String[] {"companyId"},
			true);

		_finderPathCountByCompanyId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCompanyId",
			new String[] {Long.class.getName()}, new String[] {"companyId"},
			false);

		_finderPathFetchByC_R = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByC_R",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "ruleKey"}, true);

		ProductionReadinessIgnoreUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ProductionReadinessIgnoreUtil.setPersistence(null);

		entityCache.removeCache(ProductionReadinessIgnoreImpl.class.getName());
	}

	@Override
	@Reference(
		target = PRPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = PRPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = PRPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_PRODUCTIONREADINESSIGNORE =
		"SELECT productionReadinessIgnore FROM ProductionReadinessIgnore productionReadinessIgnore";

	private static final String _SQL_SELECT_PRODUCTIONREADINESSIGNORE_WHERE =
		"SELECT productionReadinessIgnore FROM ProductionReadinessIgnore productionReadinessIgnore WHERE ";

	private static final String _SQL_COUNT_PRODUCTIONREADINESSIGNORE =
		"SELECT COUNT(productionReadinessIgnore) FROM ProductionReadinessIgnore productionReadinessIgnore";

	private static final String _SQL_COUNT_PRODUCTIONREADINESSIGNORE_WHERE =
		"SELECT COUNT(productionReadinessIgnore) FROM ProductionReadinessIgnore productionReadinessIgnore WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS =
		"productionReadinessIgnore.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No ProductionReadinessIgnore exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ProductionReadinessIgnore exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		ProductionReadinessIgnorePersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1721872961