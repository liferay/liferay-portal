/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.sample.service.persistence.impl;

import com.liferay.change.tracking.sample.exception.NoSuchCTSGrandParentException;
import com.liferay.change.tracking.sample.model.CTSGrandParent;
import com.liferay.change.tracking.sample.model.CTSGrandParentTable;
import com.liferay.change.tracking.sample.model.impl.CTSGrandParentImpl;
import com.liferay.change.tracking.sample.model.impl.CTSGrandParentModelImpl;
import com.liferay.change.tracking.sample.service.persistence.CTSGrandParentPersistence;
import com.liferay.change.tracking.sample.service.persistence.CTSGrandParentUtil;
import com.liferay.change.tracking.sample.service.persistence.impl.constants.CTSPersistenceConstants;
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
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the cts grand parent service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = CTSGrandParentPersistence.class)
public class CTSGrandParentPersistenceImpl
	extends BasePersistenceImpl<CTSGrandParent>
	implements CTSGrandParentPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CTSGrandParentUtil</code> to access the cts grand parent persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CTSGrandParentImpl.class.getName();

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
	 * Returns all the cts grand parents where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching cts grand parents
	 */
	@Override
	public List<CTSGrandParent> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cts grand parents where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSGrandParentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cts grand parents
	 * @param end the upper bound of the range of cts grand parents (not inclusive)
	 * @return the range of matching cts grand parents
	 */
	@Override
	public List<CTSGrandParent> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cts grand parents where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSGrandParentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cts grand parents
	 * @param end the upper bound of the range of cts grand parents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cts grand parents
	 */
	@Override
	public List<CTSGrandParent> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CTSGrandParent> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cts grand parents where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSGrandParentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cts grand parents
	 * @param end the upper bound of the range of cts grand parents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cts grand parents
	 */
	@Override
	public List<CTSGrandParent> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CTSGrandParent> orderByComparator,
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

		List<CTSGrandParent> list = null;

		if (useFinderCache) {
			list = (List<CTSGrandParent>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CTSGrandParent ctsGrandParent : list) {
					if (companyId != ctsGrandParent.getCompanyId()) {
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

			sb.append(_SQL_SELECT_CTSGRANDPARENT_WHERE);

			sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CTSGrandParentModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				list = (List<CTSGrandParent>)QueryUtil.list(
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
	 * Returns the first cts grand parent in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts grand parent
	 * @throws NoSuchCTSGrandParentException if a matching cts grand parent could not be found
	 */
	@Override
	public CTSGrandParent findByCompanyId_First(
			long companyId, OrderByComparator<CTSGrandParent> orderByComparator)
		throws NoSuchCTSGrandParentException {

		CTSGrandParent ctsGrandParent = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (ctsGrandParent != null) {
			return ctsGrandParent;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchCTSGrandParentException(sb.toString());
	}

	/**
	 * Returns the first cts grand parent in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts grand parent, or <code>null</code> if a matching cts grand parent could not be found
	 */
	@Override
	public CTSGrandParent fetchByCompanyId_First(
		long companyId, OrderByComparator<CTSGrandParent> orderByComparator) {

		List<CTSGrandParent> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last cts grand parent in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cts grand parent
	 * @throws NoSuchCTSGrandParentException if a matching cts grand parent could not be found
	 */
	@Override
	public CTSGrandParent findByCompanyId_Last(
			long companyId, OrderByComparator<CTSGrandParent> orderByComparator)
		throws NoSuchCTSGrandParentException {

		CTSGrandParent ctsGrandParent = fetchByCompanyId_Last(
			companyId, orderByComparator);

		if (ctsGrandParent != null) {
			return ctsGrandParent;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchCTSGrandParentException(sb.toString());
	}

	/**
	 * Returns the last cts grand parent in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cts grand parent, or <code>null</code> if a matching cts grand parent could not be found
	 */
	@Override
	public CTSGrandParent fetchByCompanyId_Last(
		long companyId, OrderByComparator<CTSGrandParent> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<CTSGrandParent> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the cts grand parents before and after the current cts grand parent in the ordered set where companyId = &#63;.
	 *
	 * @param ctsGrandParentId the primary key of the current cts grand parent
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cts grand parent
	 * @throws NoSuchCTSGrandParentException if a cts grand parent with the primary key could not be found
	 */
	@Override
	public CTSGrandParent[] findByCompanyId_PrevAndNext(
			long ctsGrandParentId, long companyId,
			OrderByComparator<CTSGrandParent> orderByComparator)
		throws NoSuchCTSGrandParentException {

		CTSGrandParent ctsGrandParent = findByPrimaryKey(ctsGrandParentId);

		Session session = null;

		try {
			session = openSession();

			CTSGrandParent[] array = new CTSGrandParentImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, ctsGrandParent, companyId, orderByComparator, true);

			array[1] = ctsGrandParent;

			array[2] = getByCompanyId_PrevAndNext(
				session, ctsGrandParent, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CTSGrandParent getByCompanyId_PrevAndNext(
		Session session, CTSGrandParent ctsGrandParent, long companyId,
		OrderByComparator<CTSGrandParent> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_CTSGRANDPARENT_WHERE);

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(CTSGrandParentModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						ctsGrandParent)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CTSGrandParent> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the cts grand parents where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (CTSGrandParent ctsGrandParent :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(ctsGrandParent);
		}
	}

	/**
	 * Returns the number of cts grand parents where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching cts grand parents
	 */
	@Override
	public int countByCompanyId(long companyId) {
		FinderPath finderPath = _finderPathCountByCompanyId;

		Object[] finderArgs = new Object[] {companyId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_CTSGRANDPARENT_WHERE);

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
		"ctsGrandParent.companyId = ?";

	public CTSGrandParentPersistenceImpl() {
		setModelClass(CTSGrandParent.class);

		setModelImplClass(CTSGrandParentImpl.class);
		setModelPKClass(long.class);

		setTable(CTSGrandParentTable.INSTANCE);
	}

	/**
	 * Caches the cts grand parent in the entity cache if it is enabled.
	 *
	 * @param ctsGrandParent the cts grand parent
	 */
	@Override
	public void cacheResult(CTSGrandParent ctsGrandParent) {
		entityCache.putResult(
			CTSGrandParentImpl.class, ctsGrandParent.getPrimaryKey(),
			ctsGrandParent);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the cts grand parents in the entity cache if it is enabled.
	 *
	 * @param ctsGrandParents the cts grand parents
	 */
	@Override
	public void cacheResult(List<CTSGrandParent> ctsGrandParents) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (ctsGrandParents.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (CTSGrandParent ctsGrandParent : ctsGrandParents) {
			if (entityCache.getResult(
					CTSGrandParentImpl.class, ctsGrandParent.getPrimaryKey()) ==
						null) {

				cacheResult(ctsGrandParent);
			}
		}
	}

	/**
	 * Clears the cache for all cts grand parents.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(CTSGrandParentImpl.class);

		finderCache.clearCache(CTSGrandParentImpl.class);
	}

	/**
	 * Clears the cache for the cts grand parent.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(CTSGrandParent ctsGrandParent) {
		entityCache.removeResult(CTSGrandParentImpl.class, ctsGrandParent);
	}

	@Override
	public void clearCache(List<CTSGrandParent> ctsGrandParents) {
		for (CTSGrandParent ctsGrandParent : ctsGrandParents) {
			entityCache.removeResult(CTSGrandParentImpl.class, ctsGrandParent);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(CTSGrandParentImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(CTSGrandParentImpl.class, primaryKey);
		}
	}

	/**
	 * Creates a new cts grand parent with the primary key. Does not add the cts grand parent to the database.
	 *
	 * @param ctsGrandParentId the primary key for the new cts grand parent
	 * @return the new cts grand parent
	 */
	@Override
	public CTSGrandParent create(long ctsGrandParentId) {
		CTSGrandParent ctsGrandParent = new CTSGrandParentImpl();

		ctsGrandParent.setNew(true);
		ctsGrandParent.setPrimaryKey(ctsGrandParentId);

		ctsGrandParent.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ctsGrandParent;
	}

	/**
	 * Removes the cts grand parent with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param ctsGrandParentId the primary key of the cts grand parent
	 * @return the cts grand parent that was removed
	 * @throws NoSuchCTSGrandParentException if a cts grand parent with the primary key could not be found
	 */
	@Override
	public CTSGrandParent remove(long ctsGrandParentId)
		throws NoSuchCTSGrandParentException {

		return remove((Serializable)ctsGrandParentId);
	}

	/**
	 * Removes the cts grand parent with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the cts grand parent
	 * @return the cts grand parent that was removed
	 * @throws NoSuchCTSGrandParentException if a cts grand parent with the primary key could not be found
	 */
	@Override
	public CTSGrandParent remove(Serializable primaryKey)
		throws NoSuchCTSGrandParentException {

		Session session = null;

		try {
			session = openSession();

			CTSGrandParent ctsGrandParent = (CTSGrandParent)session.get(
				CTSGrandParentImpl.class, primaryKey);

			if (ctsGrandParent == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchCTSGrandParentException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(ctsGrandParent);
		}
		catch (NoSuchCTSGrandParentException noSuchEntityException) {
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
	protected CTSGrandParent removeImpl(CTSGrandParent ctsGrandParent) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ctsGrandParent)) {
				ctsGrandParent = (CTSGrandParent)session.get(
					CTSGrandParentImpl.class,
					ctsGrandParent.getPrimaryKeyObj());
			}

			if (ctsGrandParent != null) {
				session.delete(ctsGrandParent);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ctsGrandParent != null) {
			clearCache(ctsGrandParent);
		}

		return ctsGrandParent;
	}

	@Override
	public CTSGrandParent updateImpl(CTSGrandParent ctsGrandParent) {
		boolean isNew = ctsGrandParent.isNew();

		if (!(ctsGrandParent instanceof CTSGrandParentModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ctsGrandParent.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					ctsGrandParent);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ctsGrandParent proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CTSGrandParent implementation " +
					ctsGrandParent.getClass());
		}

		CTSGrandParentModelImpl ctsGrandParentModelImpl =
			(CTSGrandParentModelImpl)ctsGrandParent;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(ctsGrandParent);
			}
			else {
				ctsGrandParent = (CTSGrandParent)session.merge(ctsGrandParent);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			CTSGrandParentImpl.class, ctsGrandParentModelImpl, false, true);

		if (isNew) {
			ctsGrandParent.setNew(false);
		}

		ctsGrandParent.resetOriginalValues();

		return ctsGrandParent;
	}

	/**
	 * Returns the cts grand parent with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the cts grand parent
	 * @return the cts grand parent
	 * @throws NoSuchCTSGrandParentException if a cts grand parent with the primary key could not be found
	 */
	@Override
	public CTSGrandParent findByPrimaryKey(Serializable primaryKey)
		throws NoSuchCTSGrandParentException {

		CTSGrandParent ctsGrandParent = fetchByPrimaryKey(primaryKey);

		if (ctsGrandParent == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchCTSGrandParentException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return ctsGrandParent;
	}

	/**
	 * Returns the cts grand parent with the primary key or throws a <code>NoSuchCTSGrandParentException</code> if it could not be found.
	 *
	 * @param ctsGrandParentId the primary key of the cts grand parent
	 * @return the cts grand parent
	 * @throws NoSuchCTSGrandParentException if a cts grand parent with the primary key could not be found
	 */
	@Override
	public CTSGrandParent findByPrimaryKey(long ctsGrandParentId)
		throws NoSuchCTSGrandParentException {

		return findByPrimaryKey((Serializable)ctsGrandParentId);
	}

	/**
	 * Returns the cts grand parent with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param ctsGrandParentId the primary key of the cts grand parent
	 * @return the cts grand parent, or <code>null</code> if a cts grand parent with the primary key could not be found
	 */
	@Override
	public CTSGrandParent fetchByPrimaryKey(long ctsGrandParentId) {
		return fetchByPrimaryKey((Serializable)ctsGrandParentId);
	}

	/**
	 * Returns all the cts grand parents.
	 *
	 * @return the cts grand parents
	 */
	@Override
	public List<CTSGrandParent> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cts grand parents.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSGrandParentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cts grand parents
	 * @param end the upper bound of the range of cts grand parents (not inclusive)
	 * @return the range of cts grand parents
	 */
	@Override
	public List<CTSGrandParent> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the cts grand parents.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSGrandParentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cts grand parents
	 * @param end the upper bound of the range of cts grand parents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of cts grand parents
	 */
	@Override
	public List<CTSGrandParent> findAll(
		int start, int end,
		OrderByComparator<CTSGrandParent> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cts grand parents.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSGrandParentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cts grand parents
	 * @param end the upper bound of the range of cts grand parents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of cts grand parents
	 */
	@Override
	public List<CTSGrandParent> findAll(
		int start, int end, OrderByComparator<CTSGrandParent> orderByComparator,
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

		List<CTSGrandParent> list = null;

		if (useFinderCache) {
			list = (List<CTSGrandParent>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_CTSGRANDPARENT);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_CTSGRANDPARENT;

				sql = sql.concat(CTSGrandParentModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<CTSGrandParent>)QueryUtil.list(
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
	 * Removes all the cts grand parents from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (CTSGrandParent ctsGrandParent : findAll()) {
			remove(ctsGrandParent);
		}
	}

	/**
	 * Returns the number of cts grand parents.
	 *
	 * @return the number of cts grand parents
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_CTSGRANDPARENT);

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
		return "ctsGrandParentId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CTSGRANDPARENT;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CTSGrandParentModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the cts grand parent persistence.
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

		CTSGrandParentUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CTSGrandParentUtil.setPersistence(null);

		entityCache.removeCache(CTSGrandParentImpl.class.getName());
	}

	@Override
	@Reference(
		target = CTSPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CTSPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CTSPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_CTSGRANDPARENT =
		"SELECT ctsGrandParent FROM CTSGrandParent ctsGrandParent";

	private static final String _SQL_SELECT_CTSGRANDPARENT_WHERE =
		"SELECT ctsGrandParent FROM CTSGrandParent ctsGrandParent WHERE ";

	private static final String _SQL_COUNT_CTSGRANDPARENT =
		"SELECT COUNT(ctsGrandParent) FROM CTSGrandParent ctsGrandParent";

	private static final String _SQL_COUNT_CTSGRANDPARENT_WHERE =
		"SELECT COUNT(ctsGrandParent) FROM CTSGrandParent ctsGrandParent WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS = "ctsGrandParent.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No CTSGrandParent exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CTSGrandParent exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CTSGrandParentPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}