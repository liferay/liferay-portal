/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.service.persistence.impl;

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
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.saml.persistence.exception.NoSuchIdpSsoSessionException;
import com.liferay.saml.persistence.model.SamlIdpSsoSession;
import com.liferay.saml.persistence.model.SamlIdpSsoSessionTable;
import com.liferay.saml.persistence.model.impl.SamlIdpSsoSessionImpl;
import com.liferay.saml.persistence.model.impl.SamlIdpSsoSessionModelImpl;
import com.liferay.saml.persistence.service.persistence.SamlIdpSsoSessionPersistence;
import com.liferay.saml.persistence.service.persistence.SamlIdpSsoSessionUtil;
import com.liferay.saml.persistence.service.persistence.impl.constants.SamlPersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.sql.Timestamp;

import java.util.Collections;
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
 * The persistence implementation for the saml idp sso session service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Mika Koivisto
 * @generated
 */
@Component(service = SamlIdpSsoSessionPersistence.class)
public class SamlIdpSsoSessionPersistenceImpl
	extends BasePersistenceImpl<SamlIdpSsoSession>
	implements SamlIdpSsoSessionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>SamlIdpSsoSessionUtil</code> to access the saml idp sso session persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		SamlIdpSsoSessionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathFetchByUserId;

	/**
	 * Returns the saml idp sso session where userId = &#63; or throws a <code>NoSuchIdpSsoSessionException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @return the matching saml idp sso session
	 * @throws NoSuchIdpSsoSessionException if a matching saml idp sso session could not be found
	 */
	@Override
	public SamlIdpSsoSession findByUserId(long userId)
		throws NoSuchIdpSsoSessionException {

		SamlIdpSsoSession samlIdpSsoSession = fetchByUserId(userId);

		if (samlIdpSsoSession == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("userId=");
			sb.append(userId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchIdpSsoSessionException(sb.toString());
		}

		return samlIdpSsoSession;
	}

	/**
	 * Returns the saml idp sso session where userId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param userId the user ID
	 * @return the matching saml idp sso session, or <code>null</code> if a matching saml idp sso session could not be found
	 */
	@Override
	public SamlIdpSsoSession fetchByUserId(long userId) {
		return fetchByUserId(userId, true);
	}

	/**
	 * Returns the saml idp sso session where userId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching saml idp sso session, or <code>null</code> if a matching saml idp sso session could not be found
	 */
	@Override
	public SamlIdpSsoSession fetchByUserId(
		long userId, boolean useFinderCache) {

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {userId};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByUserId, finderArgs, this);
		}

		if (result instanceof SamlIdpSsoSession) {
			SamlIdpSsoSession samlIdpSsoSession = (SamlIdpSsoSession)result;

			if (userId != samlIdpSsoSession.getUserId()) {
				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_SELECT_SAMLIDPSSOSESSION_WHERE);

			sb.append(_FINDER_COLUMN_USERID_USERID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(userId);

				List<SamlIdpSsoSession> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByUserId, finderArgs, list);
					}
				}
				else {
					if (list.size() > 1) {
						Collections.sort(list, Collections.reverseOrder());

						if (_log.isWarnEnabled()) {
							if (!useFinderCache) {
								finderArgs = new Object[] {userId};
							}

							_log.warn(
								"SamlIdpSsoSessionPersistenceImpl.fetchByUserId(long, boolean) with parameters (" +
									StringUtil.merge(finderArgs) +
										") yields a result set with more than 1 result. This violates the logical unique restriction. There is no order guarantee on which result is returned by this finder.");
						}
					}

					SamlIdpSsoSession samlIdpSsoSession = list.get(0);

					result = samlIdpSsoSession;

					cacheResult(samlIdpSsoSession);
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
			return (SamlIdpSsoSession)result;
		}
	}

	/**
	 * Removes the saml idp sso session where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @return the saml idp sso session that was removed
	 */
	@Override
	public SamlIdpSsoSession removeByUserId(long userId)
		throws NoSuchIdpSsoSessionException {

		SamlIdpSsoSession samlIdpSsoSession = findByUserId(userId);

		return remove(samlIdpSsoSession);
	}

	/**
	 * Returns the number of saml idp sso sessions where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching saml idp sso sessions
	 */
	@Override
	public int countByUserId(long userId) {
		SamlIdpSsoSession samlIdpSsoSession = fetchByUserId(userId);

		if (samlIdpSsoSession == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_USERID_USERID_2 =
		"samlIdpSsoSession.userId = ?";

	private FinderPath _finderPathWithPaginationFindByLtCreateDate;
	private FinderPath _finderPathWithPaginationCountByLtCreateDate;

	/**
	 * Returns all the saml idp sso sessions where createDate &lt; &#63;.
	 *
	 * @param createDate the create date
	 * @return the matching saml idp sso sessions
	 */
	@Override
	public List<SamlIdpSsoSession> findByLtCreateDate(Date createDate) {
		return findByLtCreateDate(
			createDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the saml idp sso sessions where createDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlIdpSsoSessionModelImpl</code>.
	 * </p>
	 *
	 * @param createDate the create date
	 * @param start the lower bound of the range of saml idp sso sessions
	 * @param end the upper bound of the range of saml idp sso sessions (not inclusive)
	 * @return the range of matching saml idp sso sessions
	 */
	@Override
	public List<SamlIdpSsoSession> findByLtCreateDate(
		Date createDate, int start, int end) {

		return findByLtCreateDate(createDate, start, end, null);
	}

	/**
	 * Returns an ordered range of all the saml idp sso sessions where createDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlIdpSsoSessionModelImpl</code>.
	 * </p>
	 *
	 * @param createDate the create date
	 * @param start the lower bound of the range of saml idp sso sessions
	 * @param end the upper bound of the range of saml idp sso sessions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching saml idp sso sessions
	 */
	@Override
	public List<SamlIdpSsoSession> findByLtCreateDate(
		Date createDate, int start, int end,
		OrderByComparator<SamlIdpSsoSession> orderByComparator) {

		return findByLtCreateDate(
			createDate, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the saml idp sso sessions where createDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlIdpSsoSessionModelImpl</code>.
	 * </p>
	 *
	 * @param createDate the create date
	 * @param start the lower bound of the range of saml idp sso sessions
	 * @param end the upper bound of the range of saml idp sso sessions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching saml idp sso sessions
	 */
	@Override
	public List<SamlIdpSsoSession> findByLtCreateDate(
		Date createDate, int start, int end,
		OrderByComparator<SamlIdpSsoSession> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		finderPath = _finderPathWithPaginationFindByLtCreateDate;
		finderArgs = new Object[] {
			_getTime(createDate), start, end, orderByComparator
		};

		List<SamlIdpSsoSession> list = null;

		if (useFinderCache) {
			list = (List<SamlIdpSsoSession>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (SamlIdpSsoSession samlIdpSsoSession : list) {
					if (createDate.getTime() <= samlIdpSsoSession.getCreateDate(
						).getTime()) {

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

			sb.append(_SQL_SELECT_SAMLIDPSSOSESSION_WHERE);

			boolean bindCreateDate = false;

			if (createDate == null) {
				sb.append(_FINDER_COLUMN_LTCREATEDATE_CREATEDATE_1);
			}
			else {
				bindCreateDate = true;

				sb.append(_FINDER_COLUMN_LTCREATEDATE_CREATEDATE_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(SamlIdpSsoSessionModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindCreateDate) {
					queryPos.add(new Timestamp(createDate.getTime()));
				}

				list = (List<SamlIdpSsoSession>)QueryUtil.list(
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
	 * Returns the first saml idp sso session in the ordered set where createDate &lt; &#63;.
	 *
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saml idp sso session
	 * @throws NoSuchIdpSsoSessionException if a matching saml idp sso session could not be found
	 */
	@Override
	public SamlIdpSsoSession findByLtCreateDate_First(
			Date createDate,
			OrderByComparator<SamlIdpSsoSession> orderByComparator)
		throws NoSuchIdpSsoSessionException {

		SamlIdpSsoSession samlIdpSsoSession = fetchByLtCreateDate_First(
			createDate, orderByComparator);

		if (samlIdpSsoSession != null) {
			return samlIdpSsoSession;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("createDate<");
		sb.append(createDate);

		sb.append("}");

		throw new NoSuchIdpSsoSessionException(sb.toString());
	}

	/**
	 * Returns the first saml idp sso session in the ordered set where createDate &lt; &#63;.
	 *
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saml idp sso session, or <code>null</code> if a matching saml idp sso session could not be found
	 */
	@Override
	public SamlIdpSsoSession fetchByLtCreateDate_First(
		Date createDate,
		OrderByComparator<SamlIdpSsoSession> orderByComparator) {

		List<SamlIdpSsoSession> list = findByLtCreateDate(
			createDate, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last saml idp sso session in the ordered set where createDate &lt; &#63;.
	 *
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching saml idp sso session
	 * @throws NoSuchIdpSsoSessionException if a matching saml idp sso session could not be found
	 */
	@Override
	public SamlIdpSsoSession findByLtCreateDate_Last(
			Date createDate,
			OrderByComparator<SamlIdpSsoSession> orderByComparator)
		throws NoSuchIdpSsoSessionException {

		SamlIdpSsoSession samlIdpSsoSession = fetchByLtCreateDate_Last(
			createDate, orderByComparator);

		if (samlIdpSsoSession != null) {
			return samlIdpSsoSession;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("createDate<");
		sb.append(createDate);

		sb.append("}");

		throw new NoSuchIdpSsoSessionException(sb.toString());
	}

	/**
	 * Returns the last saml idp sso session in the ordered set where createDate &lt; &#63;.
	 *
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching saml idp sso session, or <code>null</code> if a matching saml idp sso session could not be found
	 */
	@Override
	public SamlIdpSsoSession fetchByLtCreateDate_Last(
		Date createDate,
		OrderByComparator<SamlIdpSsoSession> orderByComparator) {

		int count = countByLtCreateDate(createDate);

		if (count == 0) {
			return null;
		}

		List<SamlIdpSsoSession> list = findByLtCreateDate(
			createDate, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the saml idp sso sessions before and after the current saml idp sso session in the ordered set where createDate &lt; &#63;.
	 *
	 * @param samlIdpSsoSessionId the primary key of the current saml idp sso session
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next saml idp sso session
	 * @throws NoSuchIdpSsoSessionException if a saml idp sso session with the primary key could not be found
	 */
	@Override
	public SamlIdpSsoSession[] findByLtCreateDate_PrevAndNext(
			long samlIdpSsoSessionId, Date createDate,
			OrderByComparator<SamlIdpSsoSession> orderByComparator)
		throws NoSuchIdpSsoSessionException {

		SamlIdpSsoSession samlIdpSsoSession = findByPrimaryKey(
			samlIdpSsoSessionId);

		Session session = null;

		try {
			session = openSession();

			SamlIdpSsoSession[] array = new SamlIdpSsoSessionImpl[3];

			array[0] = getByLtCreateDate_PrevAndNext(
				session, samlIdpSsoSession, createDate, orderByComparator,
				true);

			array[1] = samlIdpSsoSession;

			array[2] = getByLtCreateDate_PrevAndNext(
				session, samlIdpSsoSession, createDate, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected SamlIdpSsoSession getByLtCreateDate_PrevAndNext(
		Session session, SamlIdpSsoSession samlIdpSsoSession, Date createDate,
		OrderByComparator<SamlIdpSsoSession> orderByComparator,
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

		sb.append(_SQL_SELECT_SAMLIDPSSOSESSION_WHERE);

		boolean bindCreateDate = false;

		if (createDate == null) {
			sb.append(_FINDER_COLUMN_LTCREATEDATE_CREATEDATE_1);
		}
		else {
			bindCreateDate = true;

			sb.append(_FINDER_COLUMN_LTCREATEDATE_CREATEDATE_2);
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
			sb.append(SamlIdpSsoSessionModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindCreateDate) {
			queryPos.add(new Timestamp(createDate.getTime()));
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						samlIdpSsoSession)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<SamlIdpSsoSession> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the saml idp sso sessions where createDate &lt; &#63; from the database.
	 *
	 * @param createDate the create date
	 */
	@Override
	public void removeByLtCreateDate(Date createDate) {
		for (SamlIdpSsoSession samlIdpSsoSession :
				findByLtCreateDate(
					createDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(samlIdpSsoSession);
		}
	}

	/**
	 * Returns the number of saml idp sso sessions where createDate &lt; &#63;.
	 *
	 * @param createDate the create date
	 * @return the number of matching saml idp sso sessions
	 */
	@Override
	public int countByLtCreateDate(Date createDate) {
		FinderPath finderPath = _finderPathWithPaginationCountByLtCreateDate;

		Object[] finderArgs = new Object[] {_getTime(createDate)};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_SAMLIDPSSOSESSION_WHERE);

			boolean bindCreateDate = false;

			if (createDate == null) {
				sb.append(_FINDER_COLUMN_LTCREATEDATE_CREATEDATE_1);
			}
			else {
				bindCreateDate = true;

				sb.append(_FINDER_COLUMN_LTCREATEDATE_CREATEDATE_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindCreateDate) {
					queryPos.add(new Timestamp(createDate.getTime()));
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

	private static final String _FINDER_COLUMN_LTCREATEDATE_CREATEDATE_1 =
		"samlIdpSsoSession.createDate IS NULL";

	private static final String _FINDER_COLUMN_LTCREATEDATE_CREATEDATE_2 =
		"samlIdpSsoSession.createDate < ?";

	private FinderPath _finderPathFetchBySamlIdpSsoSessionKey;

	/**
	 * Returns the saml idp sso session where samlIdpSsoSessionKey = &#63; or throws a <code>NoSuchIdpSsoSessionException</code> if it could not be found.
	 *
	 * @param samlIdpSsoSessionKey the saml idp sso session key
	 * @return the matching saml idp sso session
	 * @throws NoSuchIdpSsoSessionException if a matching saml idp sso session could not be found
	 */
	@Override
	public SamlIdpSsoSession findBySamlIdpSsoSessionKey(
			String samlIdpSsoSessionKey)
		throws NoSuchIdpSsoSessionException {

		SamlIdpSsoSession samlIdpSsoSession = fetchBySamlIdpSsoSessionKey(
			samlIdpSsoSessionKey);

		if (samlIdpSsoSession == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("samlIdpSsoSessionKey=");
			sb.append(samlIdpSsoSessionKey);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchIdpSsoSessionException(sb.toString());
		}

		return samlIdpSsoSession;
	}

	/**
	 * Returns the saml idp sso session where samlIdpSsoSessionKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param samlIdpSsoSessionKey the saml idp sso session key
	 * @return the matching saml idp sso session, or <code>null</code> if a matching saml idp sso session could not be found
	 */
	@Override
	public SamlIdpSsoSession fetchBySamlIdpSsoSessionKey(
		String samlIdpSsoSessionKey) {

		return fetchBySamlIdpSsoSessionKey(samlIdpSsoSessionKey, true);
	}

	/**
	 * Returns the saml idp sso session where samlIdpSsoSessionKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param samlIdpSsoSessionKey the saml idp sso session key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching saml idp sso session, or <code>null</code> if a matching saml idp sso session could not be found
	 */
	@Override
	public SamlIdpSsoSession fetchBySamlIdpSsoSessionKey(
		String samlIdpSsoSessionKey, boolean useFinderCache) {

		samlIdpSsoSessionKey = Objects.toString(samlIdpSsoSessionKey, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {samlIdpSsoSessionKey};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchBySamlIdpSsoSessionKey, finderArgs, this);
		}

		if (result instanceof SamlIdpSsoSession) {
			SamlIdpSsoSession samlIdpSsoSession = (SamlIdpSsoSession)result;

			if (!Objects.equals(
					samlIdpSsoSessionKey,
					samlIdpSsoSession.getSamlIdpSsoSessionKey())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_SELECT_SAMLIDPSSOSESSION_WHERE);

			boolean bindSamlIdpSsoSessionKey = false;

			if (samlIdpSsoSessionKey.isEmpty()) {
				sb.append(
					_FINDER_COLUMN_SAMLIDPSSOSESSIONKEY_SAMLIDPSSOSESSIONKEY_3);
			}
			else {
				bindSamlIdpSsoSessionKey = true;

				sb.append(
					_FINDER_COLUMN_SAMLIDPSSOSESSIONKEY_SAMLIDPSSOSESSIONKEY_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindSamlIdpSsoSessionKey) {
					queryPos.add(samlIdpSsoSessionKey);
				}

				List<SamlIdpSsoSession> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchBySamlIdpSsoSessionKey, finderArgs,
							list);
					}
				}
				else {
					if (list.size() > 1) {
						Collections.sort(list, Collections.reverseOrder());

						if (_log.isWarnEnabled()) {
							if (!useFinderCache) {
								finderArgs = new Object[] {
									samlIdpSsoSessionKey
								};
							}

							_log.warn(
								"SamlIdpSsoSessionPersistenceImpl.fetchBySamlIdpSsoSessionKey(String, boolean) with parameters (" +
									StringUtil.merge(finderArgs) +
										") yields a result set with more than 1 result. This violates the logical unique restriction. There is no order guarantee on which result is returned by this finder.");
						}
					}

					SamlIdpSsoSession samlIdpSsoSession = list.get(0);

					result = samlIdpSsoSession;

					cacheResult(samlIdpSsoSession);
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
			return (SamlIdpSsoSession)result;
		}
	}

	/**
	 * Removes the saml idp sso session where samlIdpSsoSessionKey = &#63; from the database.
	 *
	 * @param samlIdpSsoSessionKey the saml idp sso session key
	 * @return the saml idp sso session that was removed
	 */
	@Override
	public SamlIdpSsoSession removeBySamlIdpSsoSessionKey(
			String samlIdpSsoSessionKey)
		throws NoSuchIdpSsoSessionException {

		SamlIdpSsoSession samlIdpSsoSession = findBySamlIdpSsoSessionKey(
			samlIdpSsoSessionKey);

		return remove(samlIdpSsoSession);
	}

	/**
	 * Returns the number of saml idp sso sessions where samlIdpSsoSessionKey = &#63;.
	 *
	 * @param samlIdpSsoSessionKey the saml idp sso session key
	 * @return the number of matching saml idp sso sessions
	 */
	@Override
	public int countBySamlIdpSsoSessionKey(String samlIdpSsoSessionKey) {
		SamlIdpSsoSession samlIdpSsoSession = fetchBySamlIdpSsoSessionKey(
			samlIdpSsoSessionKey);

		if (samlIdpSsoSession == null) {
			return 0;
		}

		return 1;
	}

	private static final String
		_FINDER_COLUMN_SAMLIDPSSOSESSIONKEY_SAMLIDPSSOSESSIONKEY_2 =
			"samlIdpSsoSession.samlIdpSsoSessionKey = ?";

	private static final String
		_FINDER_COLUMN_SAMLIDPSSOSESSIONKEY_SAMLIDPSSOSESSIONKEY_3 =
			"(samlIdpSsoSession.samlIdpSsoSessionKey IS NULL OR samlIdpSsoSession.samlIdpSsoSessionKey = '')";

	public SamlIdpSsoSessionPersistenceImpl() {
		setModelClass(SamlIdpSsoSession.class);

		setModelImplClass(SamlIdpSsoSessionImpl.class);
		setModelPKClass(long.class);

		setTable(SamlIdpSsoSessionTable.INSTANCE);
	}

	/**
	 * Caches the saml idp sso session in the entity cache if it is enabled.
	 *
	 * @param samlIdpSsoSession the saml idp sso session
	 */
	@Override
	public void cacheResult(SamlIdpSsoSession samlIdpSsoSession) {
		entityCache.putResult(
			SamlIdpSsoSessionImpl.class, samlIdpSsoSession.getPrimaryKey(),
			samlIdpSsoSession);

		finderCache.putResult(
			_finderPathFetchByUserId,
			new Object[] {samlIdpSsoSession.getUserId()}, samlIdpSsoSession);

		finderCache.putResult(
			_finderPathFetchBySamlIdpSsoSessionKey,
			new Object[] {samlIdpSsoSession.getSamlIdpSsoSessionKey()},
			samlIdpSsoSession);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the saml idp sso sessions in the entity cache if it is enabled.
	 *
	 * @param samlIdpSsoSessions the saml idp sso sessions
	 */
	@Override
	public void cacheResult(List<SamlIdpSsoSession> samlIdpSsoSessions) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (samlIdpSsoSessions.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (SamlIdpSsoSession samlIdpSsoSession : samlIdpSsoSessions) {
			if (entityCache.getResult(
					SamlIdpSsoSessionImpl.class,
					samlIdpSsoSession.getPrimaryKey()) == null) {

				cacheResult(samlIdpSsoSession);
			}
		}
	}

	/**
	 * Clears the cache for all saml idp sso sessions.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(SamlIdpSsoSessionImpl.class);

		finderCache.clearCache(SamlIdpSsoSessionImpl.class);
	}

	/**
	 * Clears the cache for the saml idp sso session.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(SamlIdpSsoSession samlIdpSsoSession) {
		entityCache.removeResult(
			SamlIdpSsoSessionImpl.class, samlIdpSsoSession);
	}

	@Override
	public void clearCache(List<SamlIdpSsoSession> samlIdpSsoSessions) {
		for (SamlIdpSsoSession samlIdpSsoSession : samlIdpSsoSessions) {
			entityCache.removeResult(
				SamlIdpSsoSessionImpl.class, samlIdpSsoSession);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(SamlIdpSsoSessionImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(SamlIdpSsoSessionImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		SamlIdpSsoSessionModelImpl samlIdpSsoSessionModelImpl) {

		Object[] args = new Object[] {samlIdpSsoSessionModelImpl.getUserId()};

		finderCache.putResult(
			_finderPathFetchByUserId, args, samlIdpSsoSessionModelImpl);

		args = new Object[] {
			samlIdpSsoSessionModelImpl.getSamlIdpSsoSessionKey()
		};

		finderCache.putResult(
			_finderPathFetchBySamlIdpSsoSessionKey, args,
			samlIdpSsoSessionModelImpl);
	}

	/**
	 * Creates a new saml idp sso session with the primary key. Does not add the saml idp sso session to the database.
	 *
	 * @param samlIdpSsoSessionId the primary key for the new saml idp sso session
	 * @return the new saml idp sso session
	 */
	@Override
	public SamlIdpSsoSession create(long samlIdpSsoSessionId) {
		SamlIdpSsoSession samlIdpSsoSession = new SamlIdpSsoSessionImpl();

		samlIdpSsoSession.setNew(true);
		samlIdpSsoSession.setPrimaryKey(samlIdpSsoSessionId);

		samlIdpSsoSession.setCompanyId(CompanyThreadLocal.getCompanyId());

		return samlIdpSsoSession;
	}

	/**
	 * Removes the saml idp sso session with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param samlIdpSsoSessionId the primary key of the saml idp sso session
	 * @return the saml idp sso session that was removed
	 * @throws NoSuchIdpSsoSessionException if a saml idp sso session with the primary key could not be found
	 */
	@Override
	public SamlIdpSsoSession remove(long samlIdpSsoSessionId)
		throws NoSuchIdpSsoSessionException {

		return remove((Serializable)samlIdpSsoSessionId);
	}

	/**
	 * Removes the saml idp sso session with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the saml idp sso session
	 * @return the saml idp sso session that was removed
	 * @throws NoSuchIdpSsoSessionException if a saml idp sso session with the primary key could not be found
	 */
	@Override
	public SamlIdpSsoSession remove(Serializable primaryKey)
		throws NoSuchIdpSsoSessionException {

		Session session = null;

		try {
			session = openSession();

			SamlIdpSsoSession samlIdpSsoSession =
				(SamlIdpSsoSession)session.get(
					SamlIdpSsoSessionImpl.class, primaryKey);

			if (samlIdpSsoSession == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchIdpSsoSessionException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(samlIdpSsoSession);
		}
		catch (NoSuchIdpSsoSessionException noSuchEntityException) {
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
	protected SamlIdpSsoSession removeImpl(
		SamlIdpSsoSession samlIdpSsoSession) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(samlIdpSsoSession)) {
				samlIdpSsoSession = (SamlIdpSsoSession)session.get(
					SamlIdpSsoSessionImpl.class,
					samlIdpSsoSession.getPrimaryKeyObj());
			}

			if (samlIdpSsoSession != null) {
				session.delete(samlIdpSsoSession);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (samlIdpSsoSession != null) {
			clearCache(samlIdpSsoSession);
		}

		return samlIdpSsoSession;
	}

	@Override
	public SamlIdpSsoSession updateImpl(SamlIdpSsoSession samlIdpSsoSession) {
		boolean isNew = samlIdpSsoSession.isNew();

		if (!(samlIdpSsoSession instanceof SamlIdpSsoSessionModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(samlIdpSsoSession.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					samlIdpSsoSession);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in samlIdpSsoSession proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom SamlIdpSsoSession implementation " +
					samlIdpSsoSession.getClass());
		}

		SamlIdpSsoSessionModelImpl samlIdpSsoSessionModelImpl =
			(SamlIdpSsoSessionModelImpl)samlIdpSsoSession;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (samlIdpSsoSession.getCreateDate() == null)) {
			if (serviceContext == null) {
				samlIdpSsoSession.setCreateDate(date);
			}
			else {
				samlIdpSsoSession.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!samlIdpSsoSessionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				samlIdpSsoSession.setModifiedDate(date);
			}
			else {
				samlIdpSsoSession.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(samlIdpSsoSession);
			}
			else {
				samlIdpSsoSession = (SamlIdpSsoSession)session.merge(
					samlIdpSsoSession);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			SamlIdpSsoSessionImpl.class, samlIdpSsoSessionModelImpl, false,
			true);

		cacheUniqueFindersCache(samlIdpSsoSessionModelImpl);

		if (isNew) {
			samlIdpSsoSession.setNew(false);
		}

		samlIdpSsoSession.resetOriginalValues();

		return samlIdpSsoSession;
	}

	/**
	 * Returns the saml idp sso session with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the saml idp sso session
	 * @return the saml idp sso session
	 * @throws NoSuchIdpSsoSessionException if a saml idp sso session with the primary key could not be found
	 */
	@Override
	public SamlIdpSsoSession findByPrimaryKey(Serializable primaryKey)
		throws NoSuchIdpSsoSessionException {

		SamlIdpSsoSession samlIdpSsoSession = fetchByPrimaryKey(primaryKey);

		if (samlIdpSsoSession == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchIdpSsoSessionException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return samlIdpSsoSession;
	}

	/**
	 * Returns the saml idp sso session with the primary key or throws a <code>NoSuchIdpSsoSessionException</code> if it could not be found.
	 *
	 * @param samlIdpSsoSessionId the primary key of the saml idp sso session
	 * @return the saml idp sso session
	 * @throws NoSuchIdpSsoSessionException if a saml idp sso session with the primary key could not be found
	 */
	@Override
	public SamlIdpSsoSession findByPrimaryKey(long samlIdpSsoSessionId)
		throws NoSuchIdpSsoSessionException {

		return findByPrimaryKey((Serializable)samlIdpSsoSessionId);
	}

	/**
	 * Returns the saml idp sso session with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param samlIdpSsoSessionId the primary key of the saml idp sso session
	 * @return the saml idp sso session, or <code>null</code> if a saml idp sso session with the primary key could not be found
	 */
	@Override
	public SamlIdpSsoSession fetchByPrimaryKey(long samlIdpSsoSessionId) {
		return fetchByPrimaryKey((Serializable)samlIdpSsoSessionId);
	}

	/**
	 * Returns all the saml idp sso sessions.
	 *
	 * @return the saml idp sso sessions
	 */
	@Override
	public List<SamlIdpSsoSession> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the saml idp sso sessions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlIdpSsoSessionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of saml idp sso sessions
	 * @param end the upper bound of the range of saml idp sso sessions (not inclusive)
	 * @return the range of saml idp sso sessions
	 */
	@Override
	public List<SamlIdpSsoSession> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the saml idp sso sessions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlIdpSsoSessionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of saml idp sso sessions
	 * @param end the upper bound of the range of saml idp sso sessions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of saml idp sso sessions
	 */
	@Override
	public List<SamlIdpSsoSession> findAll(
		int start, int end,
		OrderByComparator<SamlIdpSsoSession> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the saml idp sso sessions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlIdpSsoSessionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of saml idp sso sessions
	 * @param end the upper bound of the range of saml idp sso sessions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of saml idp sso sessions
	 */
	@Override
	public List<SamlIdpSsoSession> findAll(
		int start, int end,
		OrderByComparator<SamlIdpSsoSession> orderByComparator,
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

		List<SamlIdpSsoSession> list = null;

		if (useFinderCache) {
			list = (List<SamlIdpSsoSession>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_SAMLIDPSSOSESSION);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_SAMLIDPSSOSESSION;

				sql = sql.concat(SamlIdpSsoSessionModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<SamlIdpSsoSession>)QueryUtil.list(
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
	 * Removes all the saml idp sso sessions from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (SamlIdpSsoSession samlIdpSsoSession : findAll()) {
			remove(samlIdpSsoSession);
		}
	}

	/**
	 * Returns the number of saml idp sso sessions.
	 *
	 * @return the number of saml idp sso sessions
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_SAMLIDPSSOSESSION);

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
		return "samlIdpSsoSessionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_SAMLIDPSSOSESSION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return SamlIdpSsoSessionModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the saml idp sso session persistence.
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

		_finderPathFetchByUserId = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByUserId",
			new String[] {Long.class.getName()}, new String[] {"userId"}, true);

		_finderPathWithPaginationFindByLtCreateDate = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByLtCreateDate",
			new String[] {
				Date.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"createDate"}, true);

		_finderPathWithPaginationCountByLtCreateDate = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByLtCreateDate",
			new String[] {Date.class.getName()}, new String[] {"createDate"},
			false);

		_finderPathFetchBySamlIdpSsoSessionKey = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchBySamlIdpSsoSessionKey",
			new String[] {String.class.getName()},
			new String[] {"samlIdpSsoSessionKey"}, true);

		SamlIdpSsoSessionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		SamlIdpSsoSessionUtil.setPersistence(null);

		entityCache.removeCache(SamlIdpSsoSessionImpl.class.getName());
	}

	@Override
	@Reference(
		target = SamlPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = SamlPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = SamlPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static Long _getTime(Date date) {
		if (date == null) {
			return null;
		}

		return date.getTime();
	}

	private static final String _SQL_SELECT_SAMLIDPSSOSESSION =
		"SELECT samlIdpSsoSession FROM SamlIdpSsoSession samlIdpSsoSession";

	private static final String _SQL_SELECT_SAMLIDPSSOSESSION_WHERE =
		"SELECT samlIdpSsoSession FROM SamlIdpSsoSession samlIdpSsoSession WHERE ";

	private static final String _SQL_COUNT_SAMLIDPSSOSESSION =
		"SELECT COUNT(samlIdpSsoSession) FROM SamlIdpSsoSession samlIdpSsoSession";

	private static final String _SQL_COUNT_SAMLIDPSSOSESSION_WHERE =
		"SELECT COUNT(samlIdpSsoSession) FROM SamlIdpSsoSession samlIdpSsoSession WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS = "samlIdpSsoSession.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No SamlIdpSsoSession exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No SamlIdpSsoSession exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		SamlIdpSsoSessionPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}