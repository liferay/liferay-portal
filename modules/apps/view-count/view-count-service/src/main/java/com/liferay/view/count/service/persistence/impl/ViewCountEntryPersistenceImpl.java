/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.view.count.service.persistence.impl;

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
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.view.count.exception.NoSuchEntryException;
import com.liferay.view.count.model.ViewCountEntry;
import com.liferay.view.count.model.ViewCountEntryTable;
import com.liferay.view.count.model.impl.ViewCountEntryImpl;
import com.liferay.view.count.model.impl.ViewCountEntryModelImpl;
import com.liferay.view.count.service.persistence.ViewCountEntryPK;
import com.liferay.view.count.service.persistence.ViewCountEntryPersistence;
import com.liferay.view.count.service.persistence.ViewCountEntryUtil;
import com.liferay.view.count.service.persistence.impl.constants.ViewCountPersistenceConstants;

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
 * The persistence implementation for the view count entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Preston Crary
 * @generated
 */
@Component(service = ViewCountEntryPersistence.class)
public class ViewCountEntryPersistenceImpl
	extends BasePersistenceImpl<ViewCountEntry>
	implements ViewCountEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ViewCountEntryUtil</code> to access the view count entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ViewCountEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByC_CN;
	private FinderPath _finderPathWithoutPaginationFindByC_CN;
	private FinderPath _finderPathCountByC_CN;

	/**
	 * Returns all the view count entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the matching view count entries
	 */
	@Override
	public List<ViewCountEntry> findByC_CN(long companyId, long classNameId) {
		return findByC_CN(
			companyId, classNameId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the view count entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ViewCountEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of view count entries
	 * @param end the upper bound of the range of view count entries (not inclusive)
	 * @return the range of matching view count entries
	 */
	@Override
	public List<ViewCountEntry> findByC_CN(
		long companyId, long classNameId, int start, int end) {

		return findByC_CN(companyId, classNameId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the view count entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ViewCountEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of view count entries
	 * @param end the upper bound of the range of view count entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching view count entries
	 */
	@Override
	public List<ViewCountEntry> findByC_CN(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<ViewCountEntry> orderByComparator) {

		return findByC_CN(
			companyId, classNameId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the view count entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ViewCountEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of view count entries
	 * @param end the upper bound of the range of view count entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching view count entries
	 */
	@Override
	public List<ViewCountEntry> findByC_CN(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<ViewCountEntry> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByC_CN;
				finderArgs = new Object[] {companyId, classNameId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByC_CN;
			finderArgs = new Object[] {
				companyId, classNameId, start, end, orderByComparator
			};
		}

		List<ViewCountEntry> list = null;

		if (useFinderCache) {
			list = (List<ViewCountEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ViewCountEntry viewCountEntry : list) {
					if ((companyId != viewCountEntry.getCompanyId()) ||
						(classNameId != viewCountEntry.getClassNameId())) {

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
					4 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				sb = new StringBundler(4);
			}

			sb.append(_SQL_SELECT_VIEWCOUNTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_C_CN_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_CN_CLASSNAMEID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ViewCountEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(classNameId);

				list = (List<ViewCountEntry>)QueryUtil.list(
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
	 * Returns the first view count entry in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching view count entry
	 * @throws NoSuchEntryException if a matching view count entry could not be found
	 */
	@Override
	public ViewCountEntry findByC_CN_First(
			long companyId, long classNameId,
			OrderByComparator<ViewCountEntry> orderByComparator)
		throws NoSuchEntryException {

		ViewCountEntry viewCountEntry = fetchByC_CN_First(
			companyId, classNameId, orderByComparator);

		if (viewCountEntry != null) {
			return viewCountEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first view count entry in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching view count entry, or <code>null</code> if a matching view count entry could not be found
	 */
	@Override
	public ViewCountEntry fetchByC_CN_First(
		long companyId, long classNameId,
		OrderByComparator<ViewCountEntry> orderByComparator) {

		List<ViewCountEntry> list = findByC_CN(
			companyId, classNameId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last view count entry in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching view count entry
	 * @throws NoSuchEntryException if a matching view count entry could not be found
	 */
	@Override
	public ViewCountEntry findByC_CN_Last(
			long companyId, long classNameId,
			OrderByComparator<ViewCountEntry> orderByComparator)
		throws NoSuchEntryException {

		ViewCountEntry viewCountEntry = fetchByC_CN_Last(
			companyId, classNameId, orderByComparator);

		if (viewCountEntry != null) {
			return viewCountEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the last view count entry in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching view count entry, or <code>null</code> if a matching view count entry could not be found
	 */
	@Override
	public ViewCountEntry fetchByC_CN_Last(
		long companyId, long classNameId,
		OrderByComparator<ViewCountEntry> orderByComparator) {

		int count = countByC_CN(companyId, classNameId);

		if (count == 0) {
			return null;
		}

		List<ViewCountEntry> list = findByC_CN(
			companyId, classNameId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the view count entries before and after the current view count entry in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param viewCountEntryPK the primary key of the current view count entry
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next view count entry
	 * @throws NoSuchEntryException if a view count entry with the primary key could not be found
	 */
	@Override
	public ViewCountEntry[] findByC_CN_PrevAndNext(
			ViewCountEntryPK viewCountEntryPK, long companyId, long classNameId,
			OrderByComparator<ViewCountEntry> orderByComparator)
		throws NoSuchEntryException {

		ViewCountEntry viewCountEntry = findByPrimaryKey(viewCountEntryPK);

		Session session = null;

		try {
			session = openSession();

			ViewCountEntry[] array = new ViewCountEntryImpl[3];

			array[0] = getByC_CN_PrevAndNext(
				session, viewCountEntry, companyId, classNameId,
				orderByComparator, true);

			array[1] = viewCountEntry;

			array[2] = getByC_CN_PrevAndNext(
				session, viewCountEntry, companyId, classNameId,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected ViewCountEntry getByC_CN_PrevAndNext(
		Session session, ViewCountEntry viewCountEntry, long companyId,
		long classNameId, OrderByComparator<ViewCountEntry> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_VIEWCOUNTENTRY_WHERE);

		sb.append(_FINDER_COLUMN_C_CN_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_CN_CLASSNAMEID_2);

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
			sb.append(ViewCountEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(classNameId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						viewCountEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ViewCountEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the view count entries where companyId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByC_CN(long companyId, long classNameId) {
		for (ViewCountEntry viewCountEntry :
				findByC_CN(
					companyId, classNameId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(viewCountEntry);
		}
	}

	/**
	 * Returns the number of view count entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching view count entries
	 */
	@Override
	public int countByC_CN(long companyId, long classNameId) {
		FinderPath finderPath = _finderPathCountByC_CN;

		Object[] finderArgs = new Object[] {companyId, classNameId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_VIEWCOUNTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_C_CN_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_CN_CLASSNAMEID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(classNameId);

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

	private static final String _FINDER_COLUMN_C_CN_COMPANYID_2 =
		"viewCountEntry.primaryKey.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_CN_CLASSNAMEID_2 =
		"viewCountEntry.primaryKey.classNameId = ?";

	public ViewCountEntryPersistenceImpl() {
		setModelClass(ViewCountEntry.class);

		setModelImplClass(ViewCountEntryImpl.class);
		setModelPKClass(ViewCountEntryPK.class);

		setTable(ViewCountEntryTable.INSTANCE);
	}

	/**
	 * Caches the view count entry in the entity cache if it is enabled.
	 *
	 * @param viewCountEntry the view count entry
	 */
	@Override
	public void cacheResult(ViewCountEntry viewCountEntry) {
		entityCache.putResult(
			ViewCountEntryImpl.class, viewCountEntry.getPrimaryKey(),
			viewCountEntry);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the view count entries in the entity cache if it is enabled.
	 *
	 * @param viewCountEntries the view count entries
	 */
	@Override
	public void cacheResult(List<ViewCountEntry> viewCountEntries) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (viewCountEntries.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (ViewCountEntry viewCountEntry : viewCountEntries) {
			if (entityCache.getResult(
					ViewCountEntryImpl.class, viewCountEntry.getPrimaryKey()) ==
						null) {

				cacheResult(viewCountEntry);
			}
		}
	}

	/**
	 * Clears the cache for all view count entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(ViewCountEntryImpl.class);

		finderCache.clearCache(ViewCountEntryImpl.class);
	}

	/**
	 * Clears the cache for the view count entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(ViewCountEntry viewCountEntry) {
		entityCache.removeResult(ViewCountEntryImpl.class, viewCountEntry);
	}

	@Override
	public void clearCache(List<ViewCountEntry> viewCountEntries) {
		for (ViewCountEntry viewCountEntry : viewCountEntries) {
			entityCache.removeResult(ViewCountEntryImpl.class, viewCountEntry);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(ViewCountEntryImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(ViewCountEntryImpl.class, primaryKey);
		}
	}

	/**
	 * Creates a new view count entry with the primary key. Does not add the view count entry to the database.
	 *
	 * @param viewCountEntryPK the primary key for the new view count entry
	 * @return the new view count entry
	 */
	@Override
	public ViewCountEntry create(ViewCountEntryPK viewCountEntryPK) {
		ViewCountEntry viewCountEntry = new ViewCountEntryImpl();

		viewCountEntry.setNew(true);
		viewCountEntry.setPrimaryKey(viewCountEntryPK);

		viewCountEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return viewCountEntry;
	}

	/**
	 * Removes the view count entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param viewCountEntryPK the primary key of the view count entry
	 * @return the view count entry that was removed
	 * @throws NoSuchEntryException if a view count entry with the primary key could not be found
	 */
	@Override
	public ViewCountEntry remove(ViewCountEntryPK viewCountEntryPK)
		throws NoSuchEntryException {

		return remove((Serializable)viewCountEntryPK);
	}

	/**
	 * Removes the view count entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the view count entry
	 * @return the view count entry that was removed
	 * @throws NoSuchEntryException if a view count entry with the primary key could not be found
	 */
	@Override
	public ViewCountEntry remove(Serializable primaryKey)
		throws NoSuchEntryException {

		Session session = null;

		try {
			session = openSession();

			ViewCountEntry viewCountEntry = (ViewCountEntry)session.get(
				ViewCountEntryImpl.class, primaryKey);

			if (viewCountEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(viewCountEntry);
		}
		catch (NoSuchEntryException noSuchEntityException) {
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
	protected ViewCountEntry removeImpl(ViewCountEntry viewCountEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(viewCountEntry)) {
				viewCountEntry = (ViewCountEntry)session.get(
					ViewCountEntryImpl.class,
					viewCountEntry.getPrimaryKeyObj());
			}

			if (viewCountEntry != null) {
				session.delete(viewCountEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (viewCountEntry != null) {
			clearCache(viewCountEntry);
		}

		return viewCountEntry;
	}

	@Override
	public ViewCountEntry updateImpl(ViewCountEntry viewCountEntry) {
		boolean isNew = viewCountEntry.isNew();

		if (!(viewCountEntry instanceof ViewCountEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(viewCountEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					viewCountEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in viewCountEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ViewCountEntry implementation " +
					viewCountEntry.getClass());
		}

		ViewCountEntryModelImpl viewCountEntryModelImpl =
			(ViewCountEntryModelImpl)viewCountEntry;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(viewCountEntry);
			}
			else {
				viewCountEntry = (ViewCountEntry)session.merge(viewCountEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			ViewCountEntryImpl.class, viewCountEntryModelImpl, false, true);

		if (isNew) {
			viewCountEntry.setNew(false);
		}

		viewCountEntry.resetOriginalValues();

		return viewCountEntry;
	}

	/**
	 * Returns the view count entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the view count entry
	 * @return the view count entry
	 * @throws NoSuchEntryException if a view count entry with the primary key could not be found
	 */
	@Override
	public ViewCountEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchEntryException {

		ViewCountEntry viewCountEntry = fetchByPrimaryKey(primaryKey);

		if (viewCountEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return viewCountEntry;
	}

	/**
	 * Returns the view count entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param viewCountEntryPK the primary key of the view count entry
	 * @return the view count entry
	 * @throws NoSuchEntryException if a view count entry with the primary key could not be found
	 */
	@Override
	public ViewCountEntry findByPrimaryKey(ViewCountEntryPK viewCountEntryPK)
		throws NoSuchEntryException {

		return findByPrimaryKey((Serializable)viewCountEntryPK);
	}

	/**
	 * Returns the view count entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param viewCountEntryPK the primary key of the view count entry
	 * @return the view count entry, or <code>null</code> if a view count entry with the primary key could not be found
	 */
	@Override
	public ViewCountEntry fetchByPrimaryKey(ViewCountEntryPK viewCountEntryPK) {
		return fetchByPrimaryKey((Serializable)viewCountEntryPK);
	}

	/**
	 * Returns all the view count entries.
	 *
	 * @return the view count entries
	 */
	@Override
	public List<ViewCountEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the view count entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ViewCountEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of view count entries
	 * @param end the upper bound of the range of view count entries (not inclusive)
	 * @return the range of view count entries
	 */
	@Override
	public List<ViewCountEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the view count entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ViewCountEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of view count entries
	 * @param end the upper bound of the range of view count entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of view count entries
	 */
	@Override
	public List<ViewCountEntry> findAll(
		int start, int end,
		OrderByComparator<ViewCountEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the view count entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ViewCountEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of view count entries
	 * @param end the upper bound of the range of view count entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of view count entries
	 */
	@Override
	public List<ViewCountEntry> findAll(
		int start, int end, OrderByComparator<ViewCountEntry> orderByComparator,
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

		List<ViewCountEntry> list = null;

		if (useFinderCache) {
			list = (List<ViewCountEntry>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_VIEWCOUNTENTRY);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_VIEWCOUNTENTRY;

				sql = sql.concat(ViewCountEntryModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<ViewCountEntry>)QueryUtil.list(
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
	 * Removes all the view count entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (ViewCountEntry viewCountEntry : findAll()) {
			remove(viewCountEntry);
		}
	}

	/**
	 * Returns the number of view count entries.
	 *
	 * @return the number of view count entries
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_VIEWCOUNTENTRY);

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
	public Set<String> getCompoundPKColumnNames() {
		return _compoundPKColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "viewCountEntryPK";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_VIEWCOUNTENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ViewCountEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the view count entry persistence.
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

		_finderPathWithPaginationFindByC_CN = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_CN",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "classNameId"}, true);

		_finderPathWithoutPaginationFindByC_CN = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_CN",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"companyId", "classNameId"}, true);

		_finderPathCountByC_CN = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_CN",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"companyId", "classNameId"}, false);

		ViewCountEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ViewCountEntryUtil.setPersistence(null);

		entityCache.removeCache(ViewCountEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = ViewCountPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = ViewCountPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = ViewCountPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_VIEWCOUNTENTRY =
		"SELECT viewCountEntry FROM ViewCountEntry viewCountEntry";

	private static final String _SQL_SELECT_VIEWCOUNTENTRY_WHERE =
		"SELECT viewCountEntry FROM ViewCountEntry viewCountEntry WHERE ";

	private static final String _SQL_COUNT_VIEWCOUNTENTRY =
		"SELECT COUNT(viewCountEntry) FROM ViewCountEntry viewCountEntry";

	private static final String _SQL_COUNT_VIEWCOUNTENTRY_WHERE =
		"SELECT COUNT(viewCountEntry) FROM ViewCountEntry viewCountEntry WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS = "viewCountEntry.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No ViewCountEntry exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ViewCountEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		ViewCountEntryPersistenceImpl.class);

	private static final Set<String> _compoundPKColumnNames = SetUtil.fromArray(
		new String[] {"companyId", "classNameId", "classPK"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}