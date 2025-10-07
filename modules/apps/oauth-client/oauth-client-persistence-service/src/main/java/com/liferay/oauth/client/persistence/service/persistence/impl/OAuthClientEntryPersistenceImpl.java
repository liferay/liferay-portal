/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service.persistence.impl;

import com.liferay.oauth.client.persistence.exception.NoSuchOAuthClientEntryException;
import com.liferay.oauth.client.persistence.model.OAuthClientEntry;
import com.liferay.oauth.client.persistence.model.OAuthClientEntryTable;
import com.liferay.oauth.client.persistence.model.impl.OAuthClientEntryImpl;
import com.liferay.oauth.client.persistence.model.impl.OAuthClientEntryModelImpl;
import com.liferay.oauth.client.persistence.service.persistence.OAuthClientEntryPersistence;
import com.liferay.oauth.client.persistence.service.persistence.OAuthClientEntryUtil;
import com.liferay.oauth.client.persistence.service.persistence.impl.constants.OAuthClientPersistenceConstants;
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
import java.util.Objects;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the o auth client entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = OAuthClientEntryPersistence.class)
public class OAuthClientEntryPersistenceImpl
	extends BasePersistenceImpl<OAuthClientEntry>
	implements OAuthClientEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>OAuthClientEntryUtil</code> to access the o auth client entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		OAuthClientEntryImpl.class.getName();

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
	 * Returns all the o auth client entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching o auth client entries
	 */
	@Override
	public List<OAuthClientEntry> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the o auth client entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of o auth client entries
	 * @param end the upper bound of the range of o auth client entries (not inclusive)
	 * @return the range of matching o auth client entries
	 */
	@Override
	public List<OAuthClientEntry> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the o auth client entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of o auth client entries
	 * @param end the upper bound of the range of o auth client entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth client entries
	 */
	@Override
	public List<OAuthClientEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<OAuthClientEntry> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the o auth client entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of o auth client entries
	 * @param end the upper bound of the range of o auth client entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth client entries
	 */
	@Override
	public List<OAuthClientEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<OAuthClientEntry> orderByComparator,
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

		List<OAuthClientEntry> list = null;

		if (useFinderCache) {
			list = (List<OAuthClientEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (OAuthClientEntry oAuthClientEntry : list) {
					if (companyId != oAuthClientEntry.getCompanyId()) {
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

			sb.append(_SQL_SELECT_OAUTHCLIENTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(OAuthClientEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				list = (List<OAuthClientEntry>)QueryUtil.list(
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
	 * Returns the first o auth client entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client entry
	 * @throws NoSuchOAuthClientEntryException if a matching o auth client entry could not be found
	 */
	@Override
	public OAuthClientEntry findByCompanyId_First(
			long companyId,
			OrderByComparator<OAuthClientEntry> orderByComparator)
		throws NoSuchOAuthClientEntryException {

		OAuthClientEntry oAuthClientEntry = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (oAuthClientEntry != null) {
			return oAuthClientEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchOAuthClientEntryException(sb.toString());
	}

	/**
	 * Returns the first o auth client entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client entry, or <code>null</code> if a matching o auth client entry could not be found
	 */
	@Override
	public OAuthClientEntry fetchByCompanyId_First(
		long companyId, OrderByComparator<OAuthClientEntry> orderByComparator) {

		List<OAuthClientEntry> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last o auth client entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching o auth client entry
	 * @throws NoSuchOAuthClientEntryException if a matching o auth client entry could not be found
	 */
	@Override
	public OAuthClientEntry findByCompanyId_Last(
			long companyId,
			OrderByComparator<OAuthClientEntry> orderByComparator)
		throws NoSuchOAuthClientEntryException {

		OAuthClientEntry oAuthClientEntry = fetchByCompanyId_Last(
			companyId, orderByComparator);

		if (oAuthClientEntry != null) {
			return oAuthClientEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchOAuthClientEntryException(sb.toString());
	}

	/**
	 * Returns the last o auth client entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching o auth client entry, or <code>null</code> if a matching o auth client entry could not be found
	 */
	@Override
	public OAuthClientEntry fetchByCompanyId_Last(
		long companyId, OrderByComparator<OAuthClientEntry> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<OAuthClientEntry> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the o auth client entries before and after the current o auth client entry in the ordered set where companyId = &#63;.
	 *
	 * @param oAuthClientEntryId the primary key of the current o auth client entry
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next o auth client entry
	 * @throws NoSuchOAuthClientEntryException if a o auth client entry with the primary key could not be found
	 */
	@Override
	public OAuthClientEntry[] findByCompanyId_PrevAndNext(
			long oAuthClientEntryId, long companyId,
			OrderByComparator<OAuthClientEntry> orderByComparator)
		throws NoSuchOAuthClientEntryException {

		OAuthClientEntry oAuthClientEntry = findByPrimaryKey(
			oAuthClientEntryId);

		Session session = null;

		try {
			session = openSession();

			OAuthClientEntry[] array = new OAuthClientEntryImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, oAuthClientEntry, companyId, orderByComparator, true);

			array[1] = oAuthClientEntry;

			array[2] = getByCompanyId_PrevAndNext(
				session, oAuthClientEntry, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected OAuthClientEntry getByCompanyId_PrevAndNext(
		Session session, OAuthClientEntry oAuthClientEntry, long companyId,
		OrderByComparator<OAuthClientEntry> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_OAUTHCLIENTENTRY_WHERE);

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
			sb.append(OAuthClientEntryModelImpl.ORDER_BY_JPQL);
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
						oAuthClientEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<OAuthClientEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the o auth client entries that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching o auth client entries that the user has permission to view
	 */
	@Override
	public List<OAuthClientEntry> filterFindByCompanyId(long companyId) {
		return filterFindByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the o auth client entries that the user has permission to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of o auth client entries
	 * @param end the upper bound of the range of o auth client entries (not inclusive)
	 * @return the range of matching o auth client entries that the user has permission to view
	 */
	@Override
	public List<OAuthClientEntry> filterFindByCompanyId(
		long companyId, int start, int end) {

		return filterFindByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the o auth client entries that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of o auth client entries
	 * @param end the upper bound of the range of o auth client entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth client entries that the user has permission to view
	 */
	@Override
	public List<OAuthClientEntry> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<OAuthClientEntry> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_OAUTHCLIENTENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_OAUTHCLIENTENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_OAUTHCLIENTENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(
					OAuthClientEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(OAuthClientEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), OAuthClientEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, OAuthClientEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, OAuthClientEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			return (List<OAuthClientEntry>)QueryUtil.list(
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
	 * Returns the o auth client entries before and after the current o auth client entry in the ordered set of o auth client entries that the user has permission to view where companyId = &#63;.
	 *
	 * @param oAuthClientEntryId the primary key of the current o auth client entry
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next o auth client entry
	 * @throws NoSuchOAuthClientEntryException if a o auth client entry with the primary key could not be found
	 */
	@Override
	public OAuthClientEntry[] filterFindByCompanyId_PrevAndNext(
			long oAuthClientEntryId, long companyId,
			OrderByComparator<OAuthClientEntry> orderByComparator)
		throws NoSuchOAuthClientEntryException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByCompanyId_PrevAndNext(
				oAuthClientEntryId, companyId, orderByComparator);
		}

		OAuthClientEntry oAuthClientEntry = findByPrimaryKey(
			oAuthClientEntryId);

		Session session = null;

		try {
			session = openSession();

			OAuthClientEntry[] array = new OAuthClientEntryImpl[3];

			array[0] = filterGetByCompanyId_PrevAndNext(
				session, oAuthClientEntry, companyId, orderByComparator, true);

			array[1] = oAuthClientEntry;

			array[2] = filterGetByCompanyId_PrevAndNext(
				session, oAuthClientEntry, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected OAuthClientEntry filterGetByCompanyId_PrevAndNext(
		Session session, OAuthClientEntry oAuthClientEntry, long companyId,
		OrderByComparator<OAuthClientEntry> orderByComparator,
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

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_OAUTHCLIENTENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_OAUTHCLIENTENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_OAUTHCLIENTENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(
					OAuthClientEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(OAuthClientEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), OAuthClientEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, OAuthClientEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, OAuthClientEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						oAuthClientEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<OAuthClientEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the o auth client entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (OAuthClientEntry oAuthClientEntry :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(oAuthClientEntry);
		}
	}

	/**
	 * Returns the number of o auth client entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching o auth client entries
	 */
	@Override
	public int countByCompanyId(long companyId) {
		FinderPath finderPath = _finderPathCountByCompanyId;

		Object[] finderArgs = new Object[] {companyId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_OAUTHCLIENTENTRY_WHERE);

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
	 * Returns the number of o auth client entries that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching o auth client entries that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByCompanyId(companyId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<OAuthClientEntry> oAuthClientEntries = findByCompanyId(
				companyId);

			oAuthClientEntries = InlineSQLHelperUtil.filter(oAuthClientEntries);

			return oAuthClientEntries.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_OAUTHCLIENTENTRY_WHERE);

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), OAuthClientEntry.class.getName(),
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
		"oAuthClientEntry.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByUserId;
	private FinderPath _finderPathWithoutPaginationFindByUserId;
	private FinderPath _finderPathCountByUserId;

	/**
	 * Returns all the o auth client entries where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching o auth client entries
	 */
	@Override
	public List<OAuthClientEntry> findByUserId(long userId) {
		return findByUserId(userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the o auth client entries where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of o auth client entries
	 * @param end the upper bound of the range of o auth client entries (not inclusive)
	 * @return the range of matching o auth client entries
	 */
	@Override
	public List<OAuthClientEntry> findByUserId(
		long userId, int start, int end) {

		return findByUserId(userId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the o auth client entries where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of o auth client entries
	 * @param end the upper bound of the range of o auth client entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth client entries
	 */
	@Override
	public List<OAuthClientEntry> findByUserId(
		long userId, int start, int end,
		OrderByComparator<OAuthClientEntry> orderByComparator) {

		return findByUserId(userId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the o auth client entries where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of o auth client entries
	 * @param end the upper bound of the range of o auth client entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth client entries
	 */
	@Override
	public List<OAuthClientEntry> findByUserId(
		long userId, int start, int end,
		OrderByComparator<OAuthClientEntry> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByUserId;
				finderArgs = new Object[] {userId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByUserId;
			finderArgs = new Object[] {userId, start, end, orderByComparator};
		}

		List<OAuthClientEntry> list = null;

		if (useFinderCache) {
			list = (List<OAuthClientEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (OAuthClientEntry oAuthClientEntry : list) {
					if (userId != oAuthClientEntry.getUserId()) {
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

			sb.append(_SQL_SELECT_OAUTHCLIENTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_USERID_USERID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(OAuthClientEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(userId);

				list = (List<OAuthClientEntry>)QueryUtil.list(
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
	 * Returns the first o auth client entry in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client entry
	 * @throws NoSuchOAuthClientEntryException if a matching o auth client entry could not be found
	 */
	@Override
	public OAuthClientEntry findByUserId_First(
			long userId, OrderByComparator<OAuthClientEntry> orderByComparator)
		throws NoSuchOAuthClientEntryException {

		OAuthClientEntry oAuthClientEntry = fetchByUserId_First(
			userId, orderByComparator);

		if (oAuthClientEntry != null) {
			return oAuthClientEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append("}");

		throw new NoSuchOAuthClientEntryException(sb.toString());
	}

	/**
	 * Returns the first o auth client entry in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client entry, or <code>null</code> if a matching o auth client entry could not be found
	 */
	@Override
	public OAuthClientEntry fetchByUserId_First(
		long userId, OrderByComparator<OAuthClientEntry> orderByComparator) {

		List<OAuthClientEntry> list = findByUserId(
			userId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last o auth client entry in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching o auth client entry
	 * @throws NoSuchOAuthClientEntryException if a matching o auth client entry could not be found
	 */
	@Override
	public OAuthClientEntry findByUserId_Last(
			long userId, OrderByComparator<OAuthClientEntry> orderByComparator)
		throws NoSuchOAuthClientEntryException {

		OAuthClientEntry oAuthClientEntry = fetchByUserId_Last(
			userId, orderByComparator);

		if (oAuthClientEntry != null) {
			return oAuthClientEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append("}");

		throw new NoSuchOAuthClientEntryException(sb.toString());
	}

	/**
	 * Returns the last o auth client entry in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching o auth client entry, or <code>null</code> if a matching o auth client entry could not be found
	 */
	@Override
	public OAuthClientEntry fetchByUserId_Last(
		long userId, OrderByComparator<OAuthClientEntry> orderByComparator) {

		int count = countByUserId(userId);

		if (count == 0) {
			return null;
		}

		List<OAuthClientEntry> list = findByUserId(
			userId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the o auth client entries before and after the current o auth client entry in the ordered set where userId = &#63;.
	 *
	 * @param oAuthClientEntryId the primary key of the current o auth client entry
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next o auth client entry
	 * @throws NoSuchOAuthClientEntryException if a o auth client entry with the primary key could not be found
	 */
	@Override
	public OAuthClientEntry[] findByUserId_PrevAndNext(
			long oAuthClientEntryId, long userId,
			OrderByComparator<OAuthClientEntry> orderByComparator)
		throws NoSuchOAuthClientEntryException {

		OAuthClientEntry oAuthClientEntry = findByPrimaryKey(
			oAuthClientEntryId);

		Session session = null;

		try {
			session = openSession();

			OAuthClientEntry[] array = new OAuthClientEntryImpl[3];

			array[0] = getByUserId_PrevAndNext(
				session, oAuthClientEntry, userId, orderByComparator, true);

			array[1] = oAuthClientEntry;

			array[2] = getByUserId_PrevAndNext(
				session, oAuthClientEntry, userId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected OAuthClientEntry getByUserId_PrevAndNext(
		Session session, OAuthClientEntry oAuthClientEntry, long userId,
		OrderByComparator<OAuthClientEntry> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_OAUTHCLIENTENTRY_WHERE);

		sb.append(_FINDER_COLUMN_USERID_USERID_2);

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
			sb.append(OAuthClientEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(userId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						oAuthClientEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<OAuthClientEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the o auth client entries that the user has permission to view where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching o auth client entries that the user has permission to view
	 */
	@Override
	public List<OAuthClientEntry> filterFindByUserId(long userId) {
		return filterFindByUserId(
			userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the o auth client entries that the user has permission to view where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of o auth client entries
	 * @param end the upper bound of the range of o auth client entries (not inclusive)
	 * @return the range of matching o auth client entries that the user has permission to view
	 */
	@Override
	public List<OAuthClientEntry> filterFindByUserId(
		long userId, int start, int end) {

		return filterFindByUserId(userId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the o auth client entries that the user has permissions to view where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of o auth client entries
	 * @param end the upper bound of the range of o auth client entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth client entries that the user has permission to view
	 */
	@Override
	public List<OAuthClientEntry> filterFindByUserId(
		long userId, int start, int end,
		OrderByComparator<OAuthClientEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByUserId(userId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByUserId(
					userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_OAUTHCLIENTENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_OAUTHCLIENTENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_USERID_USERID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_OAUTHCLIENTENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(
					OAuthClientEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(OAuthClientEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), OAuthClientEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, OAuthClientEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, OAuthClientEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(userId);

			return (List<OAuthClientEntry>)QueryUtil.list(
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
	 * Returns the o auth client entries before and after the current o auth client entry in the ordered set of o auth client entries that the user has permission to view where userId = &#63;.
	 *
	 * @param oAuthClientEntryId the primary key of the current o auth client entry
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next o auth client entry
	 * @throws NoSuchOAuthClientEntryException if a o auth client entry with the primary key could not be found
	 */
	@Override
	public OAuthClientEntry[] filterFindByUserId_PrevAndNext(
			long oAuthClientEntryId, long userId,
			OrderByComparator<OAuthClientEntry> orderByComparator)
		throws NoSuchOAuthClientEntryException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByUserId_PrevAndNext(
				oAuthClientEntryId, userId, orderByComparator);
		}

		OAuthClientEntry oAuthClientEntry = findByPrimaryKey(
			oAuthClientEntryId);

		Session session = null;

		try {
			session = openSession();

			OAuthClientEntry[] array = new OAuthClientEntryImpl[3];

			array[0] = filterGetByUserId_PrevAndNext(
				session, oAuthClientEntry, userId, orderByComparator, true);

			array[1] = oAuthClientEntry;

			array[2] = filterGetByUserId_PrevAndNext(
				session, oAuthClientEntry, userId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected OAuthClientEntry filterGetByUserId_PrevAndNext(
		Session session, OAuthClientEntry oAuthClientEntry, long userId,
		OrderByComparator<OAuthClientEntry> orderByComparator,
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

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_OAUTHCLIENTENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_OAUTHCLIENTENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_USERID_USERID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_OAUTHCLIENTENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(
					OAuthClientEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(OAuthClientEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), OAuthClientEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, OAuthClientEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, OAuthClientEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(userId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						oAuthClientEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<OAuthClientEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the o auth client entries where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		for (OAuthClientEntry oAuthClientEntry :
				findByUserId(
					userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(oAuthClientEntry);
		}
	}

	/**
	 * Returns the number of o auth client entries where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching o auth client entries
	 */
	@Override
	public int countByUserId(long userId) {
		FinderPath finderPath = _finderPathCountByUserId;

		Object[] finderArgs = new Object[] {userId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_OAUTHCLIENTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_USERID_USERID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(userId);

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
	 * Returns the number of o auth client entries that the user has permission to view where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching o auth client entries that the user has permission to view
	 */
	@Override
	public int filterCountByUserId(long userId) {
		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByUserId(userId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<OAuthClientEntry> oAuthClientEntries = findByUserId(userId);

			oAuthClientEntries = InlineSQLHelperUtil.filter(oAuthClientEntries);

			return oAuthClientEntries.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_OAUTHCLIENTENTRY_WHERE);

		sb.append(_FINDER_COLUMN_USERID_USERID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), OAuthClientEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(userId);

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

	private static final String _FINDER_COLUMN_USERID_USERID_2 =
		"oAuthClientEntry.userId = ?";

	private FinderPath _finderPathWithPaginationFindByC_A;
	private FinderPath _finderPathWithoutPaginationFindByC_A;
	private FinderPath _finderPathCountByC_A;

	/**
	 * Returns all the o auth client entries where companyId = &#63; and authServerWellKnownURI = &#63;.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @return the matching o auth client entries
	 */
	@Override
	public List<OAuthClientEntry> findByC_A(
		long companyId, String authServerWellKnownURI) {

		return findByC_A(
			companyId, authServerWellKnownURI, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the o auth client entries where companyId = &#63; and authServerWellKnownURI = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param start the lower bound of the range of o auth client entries
	 * @param end the upper bound of the range of o auth client entries (not inclusive)
	 * @return the range of matching o auth client entries
	 */
	@Override
	public List<OAuthClientEntry> findByC_A(
		long companyId, String authServerWellKnownURI, int start, int end) {

		return findByC_A(companyId, authServerWellKnownURI, start, end, null);
	}

	/**
	 * Returns an ordered range of all the o auth client entries where companyId = &#63; and authServerWellKnownURI = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param start the lower bound of the range of o auth client entries
	 * @param end the upper bound of the range of o auth client entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth client entries
	 */
	@Override
	public List<OAuthClientEntry> findByC_A(
		long companyId, String authServerWellKnownURI, int start, int end,
		OrderByComparator<OAuthClientEntry> orderByComparator) {

		return findByC_A(
			companyId, authServerWellKnownURI, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the o auth client entries where companyId = &#63; and authServerWellKnownURI = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param start the lower bound of the range of o auth client entries
	 * @param end the upper bound of the range of o auth client entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth client entries
	 */
	@Override
	public List<OAuthClientEntry> findByC_A(
		long companyId, String authServerWellKnownURI, int start, int end,
		OrderByComparator<OAuthClientEntry> orderByComparator,
		boolean useFinderCache) {

		authServerWellKnownURI = Objects.toString(authServerWellKnownURI, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByC_A;
				finderArgs = new Object[] {companyId, authServerWellKnownURI};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByC_A;
			finderArgs = new Object[] {
				companyId, authServerWellKnownURI, start, end, orderByComparator
			};
		}

		List<OAuthClientEntry> list = null;

		if (useFinderCache) {
			list = (List<OAuthClientEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (OAuthClientEntry oAuthClientEntry : list) {
					if ((companyId != oAuthClientEntry.getCompanyId()) ||
						!authServerWellKnownURI.equals(
							oAuthClientEntry.getAuthServerWellKnownURI())) {

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

			sb.append(_SQL_SELECT_OAUTHCLIENTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_C_A_COMPANYID_2);

			boolean bindAuthServerWellKnownURI = false;

			if (authServerWellKnownURI.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_A_AUTHSERVERWELLKNOWNURI_3);
			}
			else {
				bindAuthServerWellKnownURI = true;

				sb.append(_FINDER_COLUMN_C_A_AUTHSERVERWELLKNOWNURI_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(OAuthClientEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				if (bindAuthServerWellKnownURI) {
					queryPos.add(authServerWellKnownURI);
				}

				list = (List<OAuthClientEntry>)QueryUtil.list(
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
	 * Returns the first o auth client entry in the ordered set where companyId = &#63; and authServerWellKnownURI = &#63;.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client entry
	 * @throws NoSuchOAuthClientEntryException if a matching o auth client entry could not be found
	 */
	@Override
	public OAuthClientEntry findByC_A_First(
			long companyId, String authServerWellKnownURI,
			OrderByComparator<OAuthClientEntry> orderByComparator)
		throws NoSuchOAuthClientEntryException {

		OAuthClientEntry oAuthClientEntry = fetchByC_A_First(
			companyId, authServerWellKnownURI, orderByComparator);

		if (oAuthClientEntry != null) {
			return oAuthClientEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", authServerWellKnownURI=");
		sb.append(authServerWellKnownURI);

		sb.append("}");

		throw new NoSuchOAuthClientEntryException(sb.toString());
	}

	/**
	 * Returns the first o auth client entry in the ordered set where companyId = &#63; and authServerWellKnownURI = &#63;.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client entry, or <code>null</code> if a matching o auth client entry could not be found
	 */
	@Override
	public OAuthClientEntry fetchByC_A_First(
		long companyId, String authServerWellKnownURI,
		OrderByComparator<OAuthClientEntry> orderByComparator) {

		List<OAuthClientEntry> list = findByC_A(
			companyId, authServerWellKnownURI, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last o auth client entry in the ordered set where companyId = &#63; and authServerWellKnownURI = &#63;.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching o auth client entry
	 * @throws NoSuchOAuthClientEntryException if a matching o auth client entry could not be found
	 */
	@Override
	public OAuthClientEntry findByC_A_Last(
			long companyId, String authServerWellKnownURI,
			OrderByComparator<OAuthClientEntry> orderByComparator)
		throws NoSuchOAuthClientEntryException {

		OAuthClientEntry oAuthClientEntry = fetchByC_A_Last(
			companyId, authServerWellKnownURI, orderByComparator);

		if (oAuthClientEntry != null) {
			return oAuthClientEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", authServerWellKnownURI=");
		sb.append(authServerWellKnownURI);

		sb.append("}");

		throw new NoSuchOAuthClientEntryException(sb.toString());
	}

	/**
	 * Returns the last o auth client entry in the ordered set where companyId = &#63; and authServerWellKnownURI = &#63;.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching o auth client entry, or <code>null</code> if a matching o auth client entry could not be found
	 */
	@Override
	public OAuthClientEntry fetchByC_A_Last(
		long companyId, String authServerWellKnownURI,
		OrderByComparator<OAuthClientEntry> orderByComparator) {

		int count = countByC_A(companyId, authServerWellKnownURI);

		if (count == 0) {
			return null;
		}

		List<OAuthClientEntry> list = findByC_A(
			companyId, authServerWellKnownURI, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the o auth client entries before and after the current o auth client entry in the ordered set where companyId = &#63; and authServerWellKnownURI = &#63;.
	 *
	 * @param oAuthClientEntryId the primary key of the current o auth client entry
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next o auth client entry
	 * @throws NoSuchOAuthClientEntryException if a o auth client entry with the primary key could not be found
	 */
	@Override
	public OAuthClientEntry[] findByC_A_PrevAndNext(
			long oAuthClientEntryId, long companyId,
			String authServerWellKnownURI,
			OrderByComparator<OAuthClientEntry> orderByComparator)
		throws NoSuchOAuthClientEntryException {

		authServerWellKnownURI = Objects.toString(authServerWellKnownURI, "");

		OAuthClientEntry oAuthClientEntry = findByPrimaryKey(
			oAuthClientEntryId);

		Session session = null;

		try {
			session = openSession();

			OAuthClientEntry[] array = new OAuthClientEntryImpl[3];

			array[0] = getByC_A_PrevAndNext(
				session, oAuthClientEntry, companyId, authServerWellKnownURI,
				orderByComparator, true);

			array[1] = oAuthClientEntry;

			array[2] = getByC_A_PrevAndNext(
				session, oAuthClientEntry, companyId, authServerWellKnownURI,
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

	protected OAuthClientEntry getByC_A_PrevAndNext(
		Session session, OAuthClientEntry oAuthClientEntry, long companyId,
		String authServerWellKnownURI,
		OrderByComparator<OAuthClientEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_OAUTHCLIENTENTRY_WHERE);

		sb.append(_FINDER_COLUMN_C_A_COMPANYID_2);

		boolean bindAuthServerWellKnownURI = false;

		if (authServerWellKnownURI.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_A_AUTHSERVERWELLKNOWNURI_3);
		}
		else {
			bindAuthServerWellKnownURI = true;

			sb.append(_FINDER_COLUMN_C_A_AUTHSERVERWELLKNOWNURI_2);
		}

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
			sb.append(OAuthClientEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (bindAuthServerWellKnownURI) {
			queryPos.add(authServerWellKnownURI);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						oAuthClientEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<OAuthClientEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the o auth client entries that the user has permission to view where companyId = &#63; and authServerWellKnownURI = &#63;.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @return the matching o auth client entries that the user has permission to view
	 */
	@Override
	public List<OAuthClientEntry> filterFindByC_A(
		long companyId, String authServerWellKnownURI) {

		return filterFindByC_A(
			companyId, authServerWellKnownURI, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the o auth client entries that the user has permission to view where companyId = &#63; and authServerWellKnownURI = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param start the lower bound of the range of o auth client entries
	 * @param end the upper bound of the range of o auth client entries (not inclusive)
	 * @return the range of matching o auth client entries that the user has permission to view
	 */
	@Override
	public List<OAuthClientEntry> filterFindByC_A(
		long companyId, String authServerWellKnownURI, int start, int end) {

		return filterFindByC_A(
			companyId, authServerWellKnownURI, start, end, null);
	}

	/**
	 * Returns an ordered range of all the o auth client entries that the user has permissions to view where companyId = &#63; and authServerWellKnownURI = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param start the lower bound of the range of o auth client entries
	 * @param end the upper bound of the range of o auth client entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth client entries that the user has permission to view
	 */
	@Override
	public List<OAuthClientEntry> filterFindByC_A(
		long companyId, String authServerWellKnownURI, int start, int end,
		OrderByComparator<OAuthClientEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_A(
				companyId, authServerWellKnownURI, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByC_A(
					companyId, authServerWellKnownURI, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator));
		}

		authServerWellKnownURI = Objects.toString(authServerWellKnownURI, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_OAUTHCLIENTENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_OAUTHCLIENTENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_A_COMPANYID_2);

		boolean bindAuthServerWellKnownURI = false;

		if (authServerWellKnownURI.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_A_AUTHSERVERWELLKNOWNURI_3);
		}
		else {
			bindAuthServerWellKnownURI = true;

			sb.append(_FINDER_COLUMN_C_A_AUTHSERVERWELLKNOWNURI_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_OAUTHCLIENTENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(
					OAuthClientEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(OAuthClientEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), OAuthClientEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, OAuthClientEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, OAuthClientEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			if (bindAuthServerWellKnownURI) {
				queryPos.add(authServerWellKnownURI);
			}

			return (List<OAuthClientEntry>)QueryUtil.list(
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
	 * Returns the o auth client entries before and after the current o auth client entry in the ordered set of o auth client entries that the user has permission to view where companyId = &#63; and authServerWellKnownURI = &#63;.
	 *
	 * @param oAuthClientEntryId the primary key of the current o auth client entry
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next o auth client entry
	 * @throws NoSuchOAuthClientEntryException if a o auth client entry with the primary key could not be found
	 */
	@Override
	public OAuthClientEntry[] filterFindByC_A_PrevAndNext(
			long oAuthClientEntryId, long companyId,
			String authServerWellKnownURI,
			OrderByComparator<OAuthClientEntry> orderByComparator)
		throws NoSuchOAuthClientEntryException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_A_PrevAndNext(
				oAuthClientEntryId, companyId, authServerWellKnownURI,
				orderByComparator);
		}

		authServerWellKnownURI = Objects.toString(authServerWellKnownURI, "");

		OAuthClientEntry oAuthClientEntry = findByPrimaryKey(
			oAuthClientEntryId);

		Session session = null;

		try {
			session = openSession();

			OAuthClientEntry[] array = new OAuthClientEntryImpl[3];

			array[0] = filterGetByC_A_PrevAndNext(
				session, oAuthClientEntry, companyId, authServerWellKnownURI,
				orderByComparator, true);

			array[1] = oAuthClientEntry;

			array[2] = filterGetByC_A_PrevAndNext(
				session, oAuthClientEntry, companyId, authServerWellKnownURI,
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

	protected OAuthClientEntry filterGetByC_A_PrevAndNext(
		Session session, OAuthClientEntry oAuthClientEntry, long companyId,
		String authServerWellKnownURI,
		OrderByComparator<OAuthClientEntry> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_OAUTHCLIENTENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_OAUTHCLIENTENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_A_COMPANYID_2);

		boolean bindAuthServerWellKnownURI = false;

		if (authServerWellKnownURI.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_A_AUTHSERVERWELLKNOWNURI_3);
		}
		else {
			bindAuthServerWellKnownURI = true;

			sb.append(_FINDER_COLUMN_C_A_AUTHSERVERWELLKNOWNURI_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_OAUTHCLIENTENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(
					OAuthClientEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(OAuthClientEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), OAuthClientEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, OAuthClientEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, OAuthClientEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(companyId);

		if (bindAuthServerWellKnownURI) {
			queryPos.add(authServerWellKnownURI);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						oAuthClientEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<OAuthClientEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the o auth client entries where companyId = &#63; and authServerWellKnownURI = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 */
	@Override
	public void removeByC_A(long companyId, String authServerWellKnownURI) {
		for (OAuthClientEntry oAuthClientEntry :
				findByC_A(
					companyId, authServerWellKnownURI, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(oAuthClientEntry);
		}
	}

	/**
	 * Returns the number of o auth client entries where companyId = &#63; and authServerWellKnownURI = &#63;.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @return the number of matching o auth client entries
	 */
	@Override
	public int countByC_A(long companyId, String authServerWellKnownURI) {
		authServerWellKnownURI = Objects.toString(authServerWellKnownURI, "");

		FinderPath finderPath = _finderPathCountByC_A;

		Object[] finderArgs = new Object[] {companyId, authServerWellKnownURI};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_OAUTHCLIENTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_C_A_COMPANYID_2);

			boolean bindAuthServerWellKnownURI = false;

			if (authServerWellKnownURI.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_A_AUTHSERVERWELLKNOWNURI_3);
			}
			else {
				bindAuthServerWellKnownURI = true;

				sb.append(_FINDER_COLUMN_C_A_AUTHSERVERWELLKNOWNURI_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				if (bindAuthServerWellKnownURI) {
					queryPos.add(authServerWellKnownURI);
				}

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
	 * Returns the number of o auth client entries that the user has permission to view where companyId = &#63; and authServerWellKnownURI = &#63;.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @return the number of matching o auth client entries that the user has permission to view
	 */
	@Override
	public int filterCountByC_A(long companyId, String authServerWellKnownURI) {
		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByC_A(companyId, authServerWellKnownURI);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<OAuthClientEntry> oAuthClientEntries = findByC_A(
				companyId, authServerWellKnownURI);

			oAuthClientEntries = InlineSQLHelperUtil.filter(oAuthClientEntries);

			return oAuthClientEntries.size();
		}

		authServerWellKnownURI = Objects.toString(authServerWellKnownURI, "");

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_OAUTHCLIENTENTRY_WHERE);

		sb.append(_FINDER_COLUMN_C_A_COMPANYID_2);

		boolean bindAuthServerWellKnownURI = false;

		if (authServerWellKnownURI.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_A_AUTHSERVERWELLKNOWNURI_3);
		}
		else {
			bindAuthServerWellKnownURI = true;

			sb.append(_FINDER_COLUMN_C_A_AUTHSERVERWELLKNOWNURI_2);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), OAuthClientEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			if (bindAuthServerWellKnownURI) {
				queryPos.add(authServerWellKnownURI);
			}

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

	private static final String _FINDER_COLUMN_C_A_COMPANYID_2 =
		"oAuthClientEntry.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_A_AUTHSERVERWELLKNOWNURI_2 =
		"oAuthClientEntry.authServerWellKnownURI = ?";

	private static final String _FINDER_COLUMN_C_A_AUTHSERVERWELLKNOWNURI_3 =
		"(oAuthClientEntry.authServerWellKnownURI IS NULL OR oAuthClientEntry.authServerWellKnownURI = '')";

	private FinderPath _finderPathFetchByC_A_C;

	/**
	 * Returns the o auth client entry where companyId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63; or throws a <code>NoSuchOAuthClientEntryException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @return the matching o auth client entry
	 * @throws NoSuchOAuthClientEntryException if a matching o auth client entry could not be found
	 */
	@Override
	public OAuthClientEntry findByC_A_C(
			long companyId, String authServerWellKnownURI, String clientId)
		throws NoSuchOAuthClientEntryException {

		OAuthClientEntry oAuthClientEntry = fetchByC_A_C(
			companyId, authServerWellKnownURI, clientId);

		if (oAuthClientEntry == null) {
			StringBundler sb = new StringBundler(8);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("companyId=");
			sb.append(companyId);

			sb.append(", authServerWellKnownURI=");
			sb.append(authServerWellKnownURI);

			sb.append(", clientId=");
			sb.append(clientId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchOAuthClientEntryException(sb.toString());
		}

		return oAuthClientEntry;
	}

	/**
	 * Returns the o auth client entry where companyId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @return the matching o auth client entry, or <code>null</code> if a matching o auth client entry could not be found
	 */
	@Override
	public OAuthClientEntry fetchByC_A_C(
		long companyId, String authServerWellKnownURI, String clientId) {

		return fetchByC_A_C(companyId, authServerWellKnownURI, clientId, true);
	}

	/**
	 * Returns the o auth client entry where companyId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching o auth client entry, or <code>null</code> if a matching o auth client entry could not be found
	 */
	@Override
	public OAuthClientEntry fetchByC_A_C(
		long companyId, String authServerWellKnownURI, String clientId,
		boolean useFinderCache) {

		authServerWellKnownURI = Objects.toString(authServerWellKnownURI, "");
		clientId = Objects.toString(clientId, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {
				companyId, authServerWellKnownURI, clientId
			};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByC_A_C, finderArgs, this);
		}

		if (result instanceof OAuthClientEntry) {
			OAuthClientEntry oAuthClientEntry = (OAuthClientEntry)result;

			if ((companyId != oAuthClientEntry.getCompanyId()) ||
				!Objects.equals(
					authServerWellKnownURI,
					oAuthClientEntry.getAuthServerWellKnownURI()) ||
				!Objects.equals(clientId, oAuthClientEntry.getClientId())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(5);

			sb.append(_SQL_SELECT_OAUTHCLIENTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_C_A_C_COMPANYID_2);

			boolean bindAuthServerWellKnownURI = false;

			if (authServerWellKnownURI.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_A_C_AUTHSERVERWELLKNOWNURI_3);
			}
			else {
				bindAuthServerWellKnownURI = true;

				sb.append(_FINDER_COLUMN_C_A_C_AUTHSERVERWELLKNOWNURI_2);
			}

			boolean bindClientId = false;

			if (clientId.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_A_C_CLIENTID_3);
			}
			else {
				bindClientId = true;

				sb.append(_FINDER_COLUMN_C_A_C_CLIENTID_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				if (bindAuthServerWellKnownURI) {
					queryPos.add(authServerWellKnownURI);
				}

				if (bindClientId) {
					queryPos.add(clientId);
				}

				List<OAuthClientEntry> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByC_A_C, finderArgs, list);
					}
				}
				else {
					OAuthClientEntry oAuthClientEntry = list.get(0);

					result = oAuthClientEntry;

					cacheResult(oAuthClientEntry);
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
			return (OAuthClientEntry)result;
		}
	}

	/**
	 * Removes the o auth client entry where companyId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @return the o auth client entry that was removed
	 */
	@Override
	public OAuthClientEntry removeByC_A_C(
			long companyId, String authServerWellKnownURI, String clientId)
		throws NoSuchOAuthClientEntryException {

		OAuthClientEntry oAuthClientEntry = findByC_A_C(
			companyId, authServerWellKnownURI, clientId);

		return remove(oAuthClientEntry);
	}

	/**
	 * Returns the number of o auth client entries where companyId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @return the number of matching o auth client entries
	 */
	@Override
	public int countByC_A_C(
		long companyId, String authServerWellKnownURI, String clientId) {

		OAuthClientEntry oAuthClientEntry = fetchByC_A_C(
			companyId, authServerWellKnownURI, clientId);

		if (oAuthClientEntry == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_C_A_C_COMPANYID_2 =
		"oAuthClientEntry.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_A_C_AUTHSERVERWELLKNOWNURI_2 =
		"oAuthClientEntry.authServerWellKnownURI = ? AND ";

	private static final String _FINDER_COLUMN_C_A_C_AUTHSERVERWELLKNOWNURI_3 =
		"(oAuthClientEntry.authServerWellKnownURI IS NULL OR oAuthClientEntry.authServerWellKnownURI = '') AND ";

	private static final String _FINDER_COLUMN_C_A_C_CLIENTID_2 =
		"oAuthClientEntry.clientId = ?";

	private static final String _FINDER_COLUMN_C_A_C_CLIENTID_3 =
		"(oAuthClientEntry.clientId IS NULL OR oAuthClientEntry.clientId = '')";

	public OAuthClientEntryPersistenceImpl() {
		setModelClass(OAuthClientEntry.class);

		setModelImplClass(OAuthClientEntryImpl.class);
		setModelPKClass(long.class);

		setTable(OAuthClientEntryTable.INSTANCE);
	}

	/**
	 * Caches the o auth client entry in the entity cache if it is enabled.
	 *
	 * @param oAuthClientEntry the o auth client entry
	 */
	@Override
	public void cacheResult(OAuthClientEntry oAuthClientEntry) {
		entityCache.putResult(
			OAuthClientEntryImpl.class, oAuthClientEntry.getPrimaryKey(),
			oAuthClientEntry);

		finderCache.putResult(
			_finderPathFetchByC_A_C,
			new Object[] {
				oAuthClientEntry.getCompanyId(),
				oAuthClientEntry.getAuthServerWellKnownURI(),
				oAuthClientEntry.getClientId()
			},
			oAuthClientEntry);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the o auth client entries in the entity cache if it is enabled.
	 *
	 * @param oAuthClientEntries the o auth client entries
	 */
	@Override
	public void cacheResult(List<OAuthClientEntry> oAuthClientEntries) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (oAuthClientEntries.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (OAuthClientEntry oAuthClientEntry : oAuthClientEntries) {
			if (entityCache.getResult(
					OAuthClientEntryImpl.class,
					oAuthClientEntry.getPrimaryKey()) == null) {

				cacheResult(oAuthClientEntry);
			}
		}
	}

	/**
	 * Clears the cache for all o auth client entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(OAuthClientEntryImpl.class);

		finderCache.clearCache(OAuthClientEntryImpl.class);
	}

	/**
	 * Clears the cache for the o auth client entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(OAuthClientEntry oAuthClientEntry) {
		entityCache.removeResult(OAuthClientEntryImpl.class, oAuthClientEntry);
	}

	@Override
	public void clearCache(List<OAuthClientEntry> oAuthClientEntries) {
		for (OAuthClientEntry oAuthClientEntry : oAuthClientEntries) {
			entityCache.removeResult(
				OAuthClientEntryImpl.class, oAuthClientEntry);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(OAuthClientEntryImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(OAuthClientEntryImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		OAuthClientEntryModelImpl oAuthClientEntryModelImpl) {

		Object[] args = new Object[] {
			oAuthClientEntryModelImpl.getCompanyId(),
			oAuthClientEntryModelImpl.getAuthServerWellKnownURI(),
			oAuthClientEntryModelImpl.getClientId()
		};

		finderCache.putResult(
			_finderPathFetchByC_A_C, args, oAuthClientEntryModelImpl);
	}

	/**
	 * Creates a new o auth client entry with the primary key. Does not add the o auth client entry to the database.
	 *
	 * @param oAuthClientEntryId the primary key for the new o auth client entry
	 * @return the new o auth client entry
	 */
	@Override
	public OAuthClientEntry create(long oAuthClientEntryId) {
		OAuthClientEntry oAuthClientEntry = new OAuthClientEntryImpl();

		oAuthClientEntry.setNew(true);
		oAuthClientEntry.setPrimaryKey(oAuthClientEntryId);

		oAuthClientEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return oAuthClientEntry;
	}

	/**
	 * Removes the o auth client entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param oAuthClientEntryId the primary key of the o auth client entry
	 * @return the o auth client entry that was removed
	 * @throws NoSuchOAuthClientEntryException if a o auth client entry with the primary key could not be found
	 */
	@Override
	public OAuthClientEntry remove(long oAuthClientEntryId)
		throws NoSuchOAuthClientEntryException {

		return remove((Serializable)oAuthClientEntryId);
	}

	/**
	 * Removes the o auth client entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the o auth client entry
	 * @return the o auth client entry that was removed
	 * @throws NoSuchOAuthClientEntryException if a o auth client entry with the primary key could not be found
	 */
	@Override
	public OAuthClientEntry remove(Serializable primaryKey)
		throws NoSuchOAuthClientEntryException {

		Session session = null;

		try {
			session = openSession();

			OAuthClientEntry oAuthClientEntry = (OAuthClientEntry)session.get(
				OAuthClientEntryImpl.class, primaryKey);

			if (oAuthClientEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchOAuthClientEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(oAuthClientEntry);
		}
		catch (NoSuchOAuthClientEntryException noSuchEntityException) {
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
	protected OAuthClientEntry removeImpl(OAuthClientEntry oAuthClientEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(oAuthClientEntry)) {
				oAuthClientEntry = (OAuthClientEntry)session.get(
					OAuthClientEntryImpl.class,
					oAuthClientEntry.getPrimaryKeyObj());
			}

			if (oAuthClientEntry != null) {
				session.delete(oAuthClientEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (oAuthClientEntry != null) {
			clearCache(oAuthClientEntry);
		}

		return oAuthClientEntry;
	}

	@Override
	public OAuthClientEntry updateImpl(OAuthClientEntry oAuthClientEntry) {
		boolean isNew = oAuthClientEntry.isNew();

		if (!(oAuthClientEntry instanceof OAuthClientEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(oAuthClientEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					oAuthClientEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in oAuthClientEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom OAuthClientEntry implementation " +
					oAuthClientEntry.getClass());
		}

		OAuthClientEntryModelImpl oAuthClientEntryModelImpl =
			(OAuthClientEntryModelImpl)oAuthClientEntry;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (oAuthClientEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				oAuthClientEntry.setCreateDate(date);
			}
			else {
				oAuthClientEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!oAuthClientEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				oAuthClientEntry.setModifiedDate(date);
			}
			else {
				oAuthClientEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(oAuthClientEntry);
			}
			else {
				oAuthClientEntry = (OAuthClientEntry)session.merge(
					oAuthClientEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			OAuthClientEntryImpl.class, oAuthClientEntryModelImpl, false, true);

		cacheUniqueFindersCache(oAuthClientEntryModelImpl);

		if (isNew) {
			oAuthClientEntry.setNew(false);
		}

		oAuthClientEntry.resetOriginalValues();

		return oAuthClientEntry;
	}

	/**
	 * Returns the o auth client entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the o auth client entry
	 * @return the o auth client entry
	 * @throws NoSuchOAuthClientEntryException if a o auth client entry with the primary key could not be found
	 */
	@Override
	public OAuthClientEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchOAuthClientEntryException {

		OAuthClientEntry oAuthClientEntry = fetchByPrimaryKey(primaryKey);

		if (oAuthClientEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchOAuthClientEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return oAuthClientEntry;
	}

	/**
	 * Returns the o auth client entry with the primary key or throws a <code>NoSuchOAuthClientEntryException</code> if it could not be found.
	 *
	 * @param oAuthClientEntryId the primary key of the o auth client entry
	 * @return the o auth client entry
	 * @throws NoSuchOAuthClientEntryException if a o auth client entry with the primary key could not be found
	 */
	@Override
	public OAuthClientEntry findByPrimaryKey(long oAuthClientEntryId)
		throws NoSuchOAuthClientEntryException {

		return findByPrimaryKey((Serializable)oAuthClientEntryId);
	}

	/**
	 * Returns the o auth client entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param oAuthClientEntryId the primary key of the o auth client entry
	 * @return the o auth client entry, or <code>null</code> if a o auth client entry with the primary key could not be found
	 */
	@Override
	public OAuthClientEntry fetchByPrimaryKey(long oAuthClientEntryId) {
		return fetchByPrimaryKey((Serializable)oAuthClientEntryId);
	}

	/**
	 * Returns all the o auth client entries.
	 *
	 * @return the o auth client entries
	 */
	@Override
	public List<OAuthClientEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the o auth client entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of o auth client entries
	 * @param end the upper bound of the range of o auth client entries (not inclusive)
	 * @return the range of o auth client entries
	 */
	@Override
	public List<OAuthClientEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the o auth client entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of o auth client entries
	 * @param end the upper bound of the range of o auth client entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of o auth client entries
	 */
	@Override
	public List<OAuthClientEntry> findAll(
		int start, int end,
		OrderByComparator<OAuthClientEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the o auth client entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of o auth client entries
	 * @param end the upper bound of the range of o auth client entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of o auth client entries
	 */
	@Override
	public List<OAuthClientEntry> findAll(
		int start, int end,
		OrderByComparator<OAuthClientEntry> orderByComparator,
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

		List<OAuthClientEntry> list = null;

		if (useFinderCache) {
			list = (List<OAuthClientEntry>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_OAUTHCLIENTENTRY);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_OAUTHCLIENTENTRY;

				sql = sql.concat(OAuthClientEntryModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<OAuthClientEntry>)QueryUtil.list(
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
	 * Removes all the o auth client entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (OAuthClientEntry oAuthClientEntry : findAll()) {
			remove(oAuthClientEntry);
		}
	}

	/**
	 * Returns the number of o auth client entries.
	 *
	 * @return the number of o auth client entries
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_OAUTHCLIENTENTRY);

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
		return "oAuthClientEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OAUTHCLIENTENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return OAuthClientEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the o auth client entry persistence.
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

		_finderPathWithPaginationFindByUserId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUserId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"userId"}, true);

		_finderPathWithoutPaginationFindByUserId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUserId",
			new String[] {Long.class.getName()}, new String[] {"userId"}, true);

		_finderPathCountByUserId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUserId",
			new String[] {Long.class.getName()}, new String[] {"userId"},
			false);

		_finderPathWithPaginationFindByC_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_A",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "authServerWellKnownURI"}, true);

		_finderPathWithoutPaginationFindByC_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_A",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "authServerWellKnownURI"}, true);

		_finderPathCountByC_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_A",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "authServerWellKnownURI"}, false);

		_finderPathFetchByC_A_C = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByC_A_C",
			new String[] {
				Long.class.getName(), String.class.getName(),
				String.class.getName()
			},
			new String[] {"companyId", "authServerWellKnownURI", "clientId"},
			true);

		OAuthClientEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		OAuthClientEntryUtil.setPersistence(null);

		entityCache.removeCache(OAuthClientEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = OAuthClientPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = OAuthClientPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = OAuthClientPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_OAUTHCLIENTENTRY =
		"SELECT oAuthClientEntry FROM OAuthClientEntry oAuthClientEntry";

	private static final String _SQL_SELECT_OAUTHCLIENTENTRY_WHERE =
		"SELECT oAuthClientEntry FROM OAuthClientEntry oAuthClientEntry WHERE ";

	private static final String _SQL_COUNT_OAUTHCLIENTENTRY =
		"SELECT COUNT(oAuthClientEntry) FROM OAuthClientEntry oAuthClientEntry";

	private static final String _SQL_COUNT_OAUTHCLIENTENTRY_WHERE =
		"SELECT COUNT(oAuthClientEntry) FROM OAuthClientEntry oAuthClientEntry WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"oAuthClientEntry.oAuthClientEntryId";

	private static final String _FILTER_SQL_SELECT_OAUTHCLIENTENTRY_WHERE =
		"SELECT DISTINCT {oAuthClientEntry.*} FROM OAuthClientEntry oAuthClientEntry WHERE ";

	private static final String
		_FILTER_SQL_SELECT_OAUTHCLIENTENTRY_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {OAuthClientEntry.*} FROM (SELECT DISTINCT oAuthClientEntry.oAuthClientEntryId FROM OAuthClientEntry oAuthClientEntry WHERE ";

	private static final String
		_FILTER_SQL_SELECT_OAUTHCLIENTENTRY_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN OAuthClientEntry ON TEMP_TABLE.oAuthClientEntryId = OAuthClientEntry.oAuthClientEntryId";

	private static final String _FILTER_SQL_COUNT_OAUTHCLIENTENTRY_WHERE =
		"SELECT COUNT(DISTINCT oAuthClientEntry.oAuthClientEntryId) AS COUNT_VALUE FROM OAuthClientEntry oAuthClientEntry WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "oAuthClientEntry";

	private static final String _FILTER_ENTITY_TABLE = "OAuthClientEntry";

	private static final String _ORDER_BY_ENTITY_ALIAS = "oAuthClientEntry.";

	private static final String _ORDER_BY_ENTITY_TABLE = "OAuthClientEntry.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No OAuthClientEntry exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No OAuthClientEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		OAuthClientEntryPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}