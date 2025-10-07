/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.persistence.impl;

import com.liferay.change.tracking.exception.NoSuchRemoteException;
import com.liferay.change.tracking.model.CTRemote;
import com.liferay.change.tracking.model.CTRemoteTable;
import com.liferay.change.tracking.model.impl.CTRemoteImpl;
import com.liferay.change.tracking.model.impl.CTRemoteModelImpl;
import com.liferay.change.tracking.service.persistence.CTRemotePersistence;
import com.liferay.change.tracking.service.persistence.CTRemoteUtil;
import com.liferay.change.tracking.service.persistence.impl.constants.CTPersistenceConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the ct remote service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = CTRemotePersistence.class)
public class CTRemotePersistenceImpl
	extends BasePersistenceImpl<CTRemote> implements CTRemotePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CTRemoteUtil</code> to access the ct remote persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CTRemoteImpl.class.getName();

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
	 * Returns all the ct remotes where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching ct remotes
	 */
	@Override
	public List<CTRemote> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ct remotes where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTRemoteModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ct remotes
	 * @param end the upper bound of the range of ct remotes (not inclusive)
	 * @return the range of matching ct remotes
	 */
	@Override
	public List<CTRemote> findByCompanyId(long companyId, int start, int end) {
		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ct remotes where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTRemoteModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ct remotes
	 * @param end the upper bound of the range of ct remotes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ct remotes
	 */
	@Override
	public List<CTRemote> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CTRemote> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ct remotes where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTRemoteModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ct remotes
	 * @param end the upper bound of the range of ct remotes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ct remotes
	 */
	@Override
	public List<CTRemote> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CTRemote> orderByComparator, boolean useFinderCache) {

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

		List<CTRemote> list = null;

		if (useFinderCache) {
			list = (List<CTRemote>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CTRemote ctRemote : list) {
					if (companyId != ctRemote.getCompanyId()) {
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

			sb.append(_SQL_SELECT_CTREMOTE_WHERE);

			sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CTRemoteModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				list = (List<CTRemote>)QueryUtil.list(
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
	 * Returns the first ct remote in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct remote
	 * @throws NoSuchRemoteException if a matching ct remote could not be found
	 */
	@Override
	public CTRemote findByCompanyId_First(
			long companyId, OrderByComparator<CTRemote> orderByComparator)
		throws NoSuchRemoteException {

		CTRemote ctRemote = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (ctRemote != null) {
			return ctRemote;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchRemoteException(sb.toString());
	}

	/**
	 * Returns the first ct remote in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct remote, or <code>null</code> if a matching ct remote could not be found
	 */
	@Override
	public CTRemote fetchByCompanyId_First(
		long companyId, OrderByComparator<CTRemote> orderByComparator) {

		List<CTRemote> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last ct remote in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ct remote
	 * @throws NoSuchRemoteException if a matching ct remote could not be found
	 */
	@Override
	public CTRemote findByCompanyId_Last(
			long companyId, OrderByComparator<CTRemote> orderByComparator)
		throws NoSuchRemoteException {

		CTRemote ctRemote = fetchByCompanyId_Last(companyId, orderByComparator);

		if (ctRemote != null) {
			return ctRemote;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchRemoteException(sb.toString());
	}

	/**
	 * Returns the last ct remote in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ct remote, or <code>null</code> if a matching ct remote could not be found
	 */
	@Override
	public CTRemote fetchByCompanyId_Last(
		long companyId, OrderByComparator<CTRemote> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<CTRemote> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the ct remotes before and after the current ct remote in the ordered set where companyId = &#63;.
	 *
	 * @param ctRemoteId the primary key of the current ct remote
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ct remote
	 * @throws NoSuchRemoteException if a ct remote with the primary key could not be found
	 */
	@Override
	public CTRemote[] findByCompanyId_PrevAndNext(
			long ctRemoteId, long companyId,
			OrderByComparator<CTRemote> orderByComparator)
		throws NoSuchRemoteException {

		CTRemote ctRemote = findByPrimaryKey(ctRemoteId);

		Session session = null;

		try {
			session = openSession();

			CTRemote[] array = new CTRemoteImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, ctRemote, companyId, orderByComparator, true);

			array[1] = ctRemote;

			array[2] = getByCompanyId_PrevAndNext(
				session, ctRemote, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CTRemote getByCompanyId_PrevAndNext(
		Session session, CTRemote ctRemote, long companyId,
		OrderByComparator<CTRemote> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_CTREMOTE_WHERE);

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
			sb.append(CTRemoteModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(ctRemote)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CTRemote> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the ct remotes that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching ct remotes that the user has permission to view
	 */
	@Override
	public List<CTRemote> filterFindByCompanyId(long companyId) {
		return filterFindByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ct remotes that the user has permission to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTRemoteModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ct remotes
	 * @param end the upper bound of the range of ct remotes (not inclusive)
	 * @return the range of matching ct remotes that the user has permission to view
	 */
	@Override
	public List<CTRemote> filterFindByCompanyId(
		long companyId, int start, int end) {

		return filterFindByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ct remotes that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTRemoteModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ct remotes
	 * @param end the upper bound of the range of ct remotes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ct remotes that the user has permission to view
	 */
	@Override
	public List<CTRemote> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CTRemote> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByCompanyId(companyId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator));
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				3 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(4);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_CTREMOTE_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_CTREMOTE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_CTREMOTE_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			if (getDB().isSupportsInlineDistinct()) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator, true);
			}
			else {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_TABLE, orderByComparator, true);
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(CTRemoteModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CTRemoteModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CTRemote.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, CTRemoteImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, CTRemoteImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			return (List<CTRemote>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns the ct remotes before and after the current ct remote in the ordered set of ct remotes that the user has permission to view where companyId = &#63;.
	 *
	 * @param ctRemoteId the primary key of the current ct remote
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ct remote
	 * @throws NoSuchRemoteException if a ct remote with the primary key could not be found
	 */
	@Override
	public CTRemote[] filterFindByCompanyId_PrevAndNext(
			long ctRemoteId, long companyId,
			OrderByComparator<CTRemote> orderByComparator)
		throws NoSuchRemoteException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByCompanyId_PrevAndNext(
				ctRemoteId, companyId, orderByComparator);
		}

		CTRemote ctRemote = findByPrimaryKey(ctRemoteId);

		Session session = null;

		try {
			session = openSession();

			CTRemote[] array = new CTRemoteImpl[3];

			array[0] = filterGetByCompanyId_PrevAndNext(
				session, ctRemote, companyId, orderByComparator, true);

			array[1] = ctRemote;

			array[2] = filterGetByCompanyId_PrevAndNext(
				session, ctRemote, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CTRemote filterGetByCompanyId_PrevAndNext(
		Session session, CTRemote ctRemote, long companyId,
		OrderByComparator<CTRemote> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_CTREMOTE_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_CTREMOTE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_CTREMOTE_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByConditionFields[i],
							true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByConditionFields[i],
							true));
				}

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
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByFields[i], true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByFields[i], true));
				}

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
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(CTRemoteModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CTRemoteModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CTRemote.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, CTRemoteImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, CTRemoteImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(ctRemote)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CTRemote> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the ct remotes where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (CTRemote ctRemote :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(ctRemote);
		}
	}

	/**
	 * Returns the number of ct remotes where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching ct remotes
	 */
	@Override
	public int countByCompanyId(long companyId) {
		FinderPath finderPath = _finderPathCountByCompanyId;

		Object[] finderArgs = new Object[] {companyId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_CTREMOTE_WHERE);

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

	/**
	 * Returns the number of ct remotes that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching ct remotes that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByCompanyId(companyId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CTRemote> ctRemotes = findByCompanyId(companyId);

			ctRemotes = InlineSQLHelperUtil.filter(ctRemotes);

			return ctRemotes.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_CTREMOTE_WHERE);

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CTRemote.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			Long count = (Long)sqlQuery.uniqueResult();

			return count.intValue();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private static final String _FINDER_COLUMN_COMPANYID_COMPANYID_2 =
		"ctRemote.companyId = ?";

	public CTRemotePersistenceImpl() {
		setModelClass(CTRemote.class);

		setModelImplClass(CTRemoteImpl.class);
		setModelPKClass(long.class);

		setTable(CTRemoteTable.INSTANCE);
	}

	/**
	 * Caches the ct remote in the entity cache if it is enabled.
	 *
	 * @param ctRemote the ct remote
	 */
	@Override
	public void cacheResult(CTRemote ctRemote) {
		entityCache.putResult(
			CTRemoteImpl.class, ctRemote.getPrimaryKey(), ctRemote);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the ct remotes in the entity cache if it is enabled.
	 *
	 * @param ctRemotes the ct remotes
	 */
	@Override
	public void cacheResult(List<CTRemote> ctRemotes) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (ctRemotes.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (CTRemote ctRemote : ctRemotes) {
			if (entityCache.getResult(
					CTRemoteImpl.class, ctRemote.getPrimaryKey()) == null) {

				cacheResult(ctRemote);
			}
		}
	}

	/**
	 * Clears the cache for all ct remotes.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(CTRemoteImpl.class);

		finderCache.clearCache(CTRemoteImpl.class);
	}

	/**
	 * Clears the cache for the ct remote.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(CTRemote ctRemote) {
		entityCache.removeResult(CTRemoteImpl.class, ctRemote);
	}

	@Override
	public void clearCache(List<CTRemote> ctRemotes) {
		for (CTRemote ctRemote : ctRemotes) {
			entityCache.removeResult(CTRemoteImpl.class, ctRemote);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(CTRemoteImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(CTRemoteImpl.class, primaryKey);
		}
	}

	/**
	 * Creates a new ct remote with the primary key. Does not add the ct remote to the database.
	 *
	 * @param ctRemoteId the primary key for the new ct remote
	 * @return the new ct remote
	 */
	@Override
	public CTRemote create(long ctRemoteId) {
		CTRemote ctRemote = new CTRemoteImpl();

		ctRemote.setNew(true);
		ctRemote.setPrimaryKey(ctRemoteId);

		ctRemote.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ctRemote;
	}

	/**
	 * Removes the ct remote with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param ctRemoteId the primary key of the ct remote
	 * @return the ct remote that was removed
	 * @throws NoSuchRemoteException if a ct remote with the primary key could not be found
	 */
	@Override
	public CTRemote remove(long ctRemoteId) throws NoSuchRemoteException {
		return remove((Serializable)ctRemoteId);
	}

	/**
	 * Removes the ct remote with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the ct remote
	 * @return the ct remote that was removed
	 * @throws NoSuchRemoteException if a ct remote with the primary key could not be found
	 */
	@Override
	public CTRemote remove(Serializable primaryKey)
		throws NoSuchRemoteException {

		Session session = null;

		try {
			session = openSession();

			CTRemote ctRemote = (CTRemote)session.get(
				CTRemoteImpl.class, primaryKey);

			if (ctRemote == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchRemoteException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(ctRemote);
		}
		catch (NoSuchRemoteException noSuchEntityException) {
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
	protected CTRemote removeImpl(CTRemote ctRemote) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ctRemote)) {
				ctRemote = (CTRemote)session.get(
					CTRemoteImpl.class, ctRemote.getPrimaryKeyObj());
			}

			if (ctRemote != null) {
				session.delete(ctRemote);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ctRemote != null) {
			clearCache(ctRemote);
		}

		return ctRemote;
	}

	@Override
	public CTRemote updateImpl(CTRemote ctRemote) {
		boolean isNew = ctRemote.isNew();

		if (!(ctRemote instanceof CTRemoteModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ctRemote.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(ctRemote);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ctRemote proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CTRemote implementation " +
					ctRemote.getClass());
		}

		CTRemoteModelImpl ctRemoteModelImpl = (CTRemoteModelImpl)ctRemote;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (ctRemote.getCreateDate() == null)) {
			if (serviceContext == null) {
				ctRemote.setCreateDate(date);
			}
			else {
				ctRemote.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!ctRemoteModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				ctRemote.setModifiedDate(date);
			}
			else {
				ctRemote.setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(ctRemote);
			}
			else {
				ctRemote = (CTRemote)session.merge(ctRemote);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			CTRemoteImpl.class, ctRemoteModelImpl, false, true);

		if (isNew) {
			ctRemote.setNew(false);
		}

		ctRemote.resetOriginalValues();

		return ctRemote;
	}

	/**
	 * Returns the ct remote with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the ct remote
	 * @return the ct remote
	 * @throws NoSuchRemoteException if a ct remote with the primary key could not be found
	 */
	@Override
	public CTRemote findByPrimaryKey(Serializable primaryKey)
		throws NoSuchRemoteException {

		CTRemote ctRemote = fetchByPrimaryKey(primaryKey);

		if (ctRemote == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchRemoteException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return ctRemote;
	}

	/**
	 * Returns the ct remote with the primary key or throws a <code>NoSuchRemoteException</code> if it could not be found.
	 *
	 * @param ctRemoteId the primary key of the ct remote
	 * @return the ct remote
	 * @throws NoSuchRemoteException if a ct remote with the primary key could not be found
	 */
	@Override
	public CTRemote findByPrimaryKey(long ctRemoteId)
		throws NoSuchRemoteException {

		return findByPrimaryKey((Serializable)ctRemoteId);
	}

	/**
	 * Returns the ct remote with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param ctRemoteId the primary key of the ct remote
	 * @return the ct remote, or <code>null</code> if a ct remote with the primary key could not be found
	 */
	@Override
	public CTRemote fetchByPrimaryKey(long ctRemoteId) {
		return fetchByPrimaryKey((Serializable)ctRemoteId);
	}

	/**
	 * Returns all the ct remotes.
	 *
	 * @return the ct remotes
	 */
	@Override
	public List<CTRemote> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ct remotes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTRemoteModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ct remotes
	 * @param end the upper bound of the range of ct remotes (not inclusive)
	 * @return the range of ct remotes
	 */
	@Override
	public List<CTRemote> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the ct remotes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTRemoteModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ct remotes
	 * @param end the upper bound of the range of ct remotes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of ct remotes
	 */
	@Override
	public List<CTRemote> findAll(
		int start, int end, OrderByComparator<CTRemote> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ct remotes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTRemoteModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ct remotes
	 * @param end the upper bound of the range of ct remotes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of ct remotes
	 */
	@Override
	public List<CTRemote> findAll(
		int start, int end, OrderByComparator<CTRemote> orderByComparator,
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

		List<CTRemote> list = null;

		if (useFinderCache) {
			list = (List<CTRemote>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_CTREMOTE);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_CTREMOTE;

				sql = sql.concat(CTRemoteModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<CTRemote>)QueryUtil.list(
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
	 * Removes all the ct remotes from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (CTRemote ctRemote : findAll()) {
			remove(ctRemote);
		}
	}

	/**
	 * Returns the number of ct remotes.
	 *
	 * @return the number of ct remotes
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_CTREMOTE);

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
		return "ctRemoteId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CTREMOTE;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CTRemoteModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the ct remote persistence.
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

		CTRemoteUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CTRemoteUtil.setPersistence(null);

		entityCache.removeCache(CTRemoteImpl.class.getName());
	}

	@Override
	@Reference(
		target = CTPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CTPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CTPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_CTREMOTE =
		"SELECT ctRemote FROM CTRemote ctRemote";

	private static final String _SQL_SELECT_CTREMOTE_WHERE =
		"SELECT ctRemote FROM CTRemote ctRemote WHERE ";

	private static final String _SQL_COUNT_CTREMOTE =
		"SELECT COUNT(ctRemote) FROM CTRemote ctRemote";

	private static final String _SQL_COUNT_CTREMOTE_WHERE =
		"SELECT COUNT(ctRemote) FROM CTRemote ctRemote WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"ctRemote.ctRemoteId";

	private static final String _FILTER_SQL_SELECT_CTREMOTE_WHERE =
		"SELECT DISTINCT {ctRemote.*} FROM CTRemote ctRemote WHERE ";

	private static final String
		_FILTER_SQL_SELECT_CTREMOTE_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {CTRemote.*} FROM (SELECT DISTINCT ctRemote.ctRemoteId FROM CTRemote ctRemote WHERE ";

	private static final String
		_FILTER_SQL_SELECT_CTREMOTE_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN CTRemote ON TEMP_TABLE.ctRemoteId = CTRemote.ctRemoteId";

	private static final String _FILTER_SQL_COUNT_CTREMOTE_WHERE =
		"SELECT COUNT(DISTINCT ctRemote.ctRemoteId) AS COUNT_VALUE FROM CTRemote ctRemote WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "ctRemote";

	private static final String _FILTER_ENTITY_TABLE = "CTRemote";

	private static final String _ORDER_BY_ENTITY_ALIAS = "ctRemote.";

	private static final String _ORDER_BY_ENTITY_TABLE = "CTRemote.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No CTRemote exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CTRemote exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CTRemotePersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}