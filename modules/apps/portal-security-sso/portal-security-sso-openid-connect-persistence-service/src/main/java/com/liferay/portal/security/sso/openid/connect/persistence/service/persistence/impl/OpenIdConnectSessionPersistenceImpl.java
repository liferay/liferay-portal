/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.persistence.service.persistence.impl;

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
import com.liferay.portal.security.sso.openid.connect.persistence.exception.NoSuchSessionException;
import com.liferay.portal.security.sso.openid.connect.persistence.model.OpenIdConnectSession;
import com.liferay.portal.security.sso.openid.connect.persistence.model.OpenIdConnectSessionTable;
import com.liferay.portal.security.sso.openid.connect.persistence.model.impl.OpenIdConnectSessionImpl;
import com.liferay.portal.security.sso.openid.connect.persistence.model.impl.OpenIdConnectSessionModelImpl;
import com.liferay.portal.security.sso.openid.connect.persistence.service.persistence.OpenIdConnectSessionPersistence;
import com.liferay.portal.security.sso.openid.connect.persistence.service.persistence.OpenIdConnectSessionUtil;
import com.liferay.portal.security.sso.openid.connect.persistence.service.persistence.impl.constants.OpenIdConnectPersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.sql.Timestamp;

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
 * The persistence implementation for the open ID connect session service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Arthur Chan
 * @generated
 */
@Component(service = OpenIdConnectSessionPersistence.class)
public class OpenIdConnectSessionPersistenceImpl
	extends BasePersistenceImpl<OpenIdConnectSession>
	implements OpenIdConnectSessionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>OpenIdConnectSessionUtil</code> to access the open ID connect session persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		OpenIdConnectSessionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByUserId;
	private FinderPath _finderPathWithoutPaginationFindByUserId;
	private FinderPath _finderPathCountByUserId;

	/**
	 * Returns all the open ID connect sessions where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching open ID connect sessions
	 */
	@Override
	public List<OpenIdConnectSession> findByUserId(long userId) {
		return findByUserId(userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the open ID connect sessions where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectSessionModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of open ID connect sessions
	 * @param end the upper bound of the range of open ID connect sessions (not inclusive)
	 * @return the range of matching open ID connect sessions
	 */
	@Override
	public List<OpenIdConnectSession> findByUserId(
		long userId, int start, int end) {

		return findByUserId(userId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the open ID connect sessions where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectSessionModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of open ID connect sessions
	 * @param end the upper bound of the range of open ID connect sessions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching open ID connect sessions
	 */
	@Override
	public List<OpenIdConnectSession> findByUserId(
		long userId, int start, int end,
		OrderByComparator<OpenIdConnectSession> orderByComparator) {

		return findByUserId(userId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the open ID connect sessions where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectSessionModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of open ID connect sessions
	 * @param end the upper bound of the range of open ID connect sessions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching open ID connect sessions
	 */
	@Override
	public List<OpenIdConnectSession> findByUserId(
		long userId, int start, int end,
		OrderByComparator<OpenIdConnectSession> orderByComparator,
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

		List<OpenIdConnectSession> list = null;

		if (useFinderCache) {
			list = (List<OpenIdConnectSession>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (OpenIdConnectSession openIdConnectSession : list) {
					if (userId != openIdConnectSession.getUserId()) {
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

			sb.append(_SQL_SELECT_OPENIDCONNECTSESSION_WHERE);

			sb.append(_FINDER_COLUMN_USERID_USERID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(OpenIdConnectSessionModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(userId);

				list = (List<OpenIdConnectSession>)QueryUtil.list(
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
	 * Returns the first open ID connect session in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching open ID connect session
	 * @throws NoSuchSessionException if a matching open ID connect session could not be found
	 */
	@Override
	public OpenIdConnectSession findByUserId_First(
			long userId,
			OrderByComparator<OpenIdConnectSession> orderByComparator)
		throws NoSuchSessionException {

		OpenIdConnectSession openIdConnectSession = fetchByUserId_First(
			userId, orderByComparator);

		if (openIdConnectSession != null) {
			return openIdConnectSession;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append("}");

		throw new NoSuchSessionException(sb.toString());
	}

	/**
	 * Returns the first open ID connect session in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching open ID connect session, or <code>null</code> if a matching open ID connect session could not be found
	 */
	@Override
	public OpenIdConnectSession fetchByUserId_First(
		long userId,
		OrderByComparator<OpenIdConnectSession> orderByComparator) {

		List<OpenIdConnectSession> list = findByUserId(
			userId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last open ID connect session in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching open ID connect session
	 * @throws NoSuchSessionException if a matching open ID connect session could not be found
	 */
	@Override
	public OpenIdConnectSession findByUserId_Last(
			long userId,
			OrderByComparator<OpenIdConnectSession> orderByComparator)
		throws NoSuchSessionException {

		OpenIdConnectSession openIdConnectSession = fetchByUserId_Last(
			userId, orderByComparator);

		if (openIdConnectSession != null) {
			return openIdConnectSession;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append("}");

		throw new NoSuchSessionException(sb.toString());
	}

	/**
	 * Returns the last open ID connect session in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching open ID connect session, or <code>null</code> if a matching open ID connect session could not be found
	 */
	@Override
	public OpenIdConnectSession fetchByUserId_Last(
		long userId,
		OrderByComparator<OpenIdConnectSession> orderByComparator) {

		int count = countByUserId(userId);

		if (count == 0) {
			return null;
		}

		List<OpenIdConnectSession> list = findByUserId(
			userId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the open ID connect sessions before and after the current open ID connect session in the ordered set where userId = &#63;.
	 *
	 * @param openIdConnectSessionId the primary key of the current open ID connect session
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next open ID connect session
	 * @throws NoSuchSessionException if a open ID connect session with the primary key could not be found
	 */
	@Override
	public OpenIdConnectSession[] findByUserId_PrevAndNext(
			long openIdConnectSessionId, long userId,
			OrderByComparator<OpenIdConnectSession> orderByComparator)
		throws NoSuchSessionException {

		OpenIdConnectSession openIdConnectSession = findByPrimaryKey(
			openIdConnectSessionId);

		Session session = null;

		try {
			session = openSession();

			OpenIdConnectSession[] array = new OpenIdConnectSessionImpl[3];

			array[0] = getByUserId_PrevAndNext(
				session, openIdConnectSession, userId, orderByComparator, true);

			array[1] = openIdConnectSession;

			array[2] = getByUserId_PrevAndNext(
				session, openIdConnectSession, userId, orderByComparator,
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

	protected OpenIdConnectSession getByUserId_PrevAndNext(
		Session session, OpenIdConnectSession openIdConnectSession, long userId,
		OrderByComparator<OpenIdConnectSession> orderByComparator,
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

		sb.append(_SQL_SELECT_OPENIDCONNECTSESSION_WHERE);

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
			sb.append(OpenIdConnectSessionModelImpl.ORDER_BY_JPQL);
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
						openIdConnectSession)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<OpenIdConnectSession> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the open ID connect sessions where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		for (OpenIdConnectSession openIdConnectSession :
				findByUserId(
					userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(openIdConnectSession);
		}
	}

	/**
	 * Returns the number of open ID connect sessions where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching open ID connect sessions
	 */
	@Override
	public int countByUserId(long userId) {
		FinderPath finderPath = _finderPathCountByUserId;

		Object[] finderArgs = new Object[] {userId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_OPENIDCONNECTSESSION_WHERE);

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

	private static final String _FINDER_COLUMN_USERID_USERID_2 =
		"openIdConnectSession.userId = ?";

	private FinderPath
		_finderPathWithPaginationFindByLtAccessTokenExpirationDate;
	private FinderPath
		_finderPathWithPaginationCountByLtAccessTokenExpirationDate;

	/**
	 * Returns all the open ID connect sessions where accessTokenExpirationDate &lt; &#63;.
	 *
	 * @param accessTokenExpirationDate the access token expiration date
	 * @return the matching open ID connect sessions
	 */
	@Override
	public List<OpenIdConnectSession> findByLtAccessTokenExpirationDate(
		Date accessTokenExpirationDate) {

		return findByLtAccessTokenExpirationDate(
			accessTokenExpirationDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the open ID connect sessions where accessTokenExpirationDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectSessionModelImpl</code>.
	 * </p>
	 *
	 * @param accessTokenExpirationDate the access token expiration date
	 * @param start the lower bound of the range of open ID connect sessions
	 * @param end the upper bound of the range of open ID connect sessions (not inclusive)
	 * @return the range of matching open ID connect sessions
	 */
	@Override
	public List<OpenIdConnectSession> findByLtAccessTokenExpirationDate(
		Date accessTokenExpirationDate, int start, int end) {

		return findByLtAccessTokenExpirationDate(
			accessTokenExpirationDate, start, end, null);
	}

	/**
	 * Returns an ordered range of all the open ID connect sessions where accessTokenExpirationDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectSessionModelImpl</code>.
	 * </p>
	 *
	 * @param accessTokenExpirationDate the access token expiration date
	 * @param start the lower bound of the range of open ID connect sessions
	 * @param end the upper bound of the range of open ID connect sessions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching open ID connect sessions
	 */
	@Override
	public List<OpenIdConnectSession> findByLtAccessTokenExpirationDate(
		Date accessTokenExpirationDate, int start, int end,
		OrderByComparator<OpenIdConnectSession> orderByComparator) {

		return findByLtAccessTokenExpirationDate(
			accessTokenExpirationDate, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the open ID connect sessions where accessTokenExpirationDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectSessionModelImpl</code>.
	 * </p>
	 *
	 * @param accessTokenExpirationDate the access token expiration date
	 * @param start the lower bound of the range of open ID connect sessions
	 * @param end the upper bound of the range of open ID connect sessions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching open ID connect sessions
	 */
	@Override
	public List<OpenIdConnectSession> findByLtAccessTokenExpirationDate(
		Date accessTokenExpirationDate, int start, int end,
		OrderByComparator<OpenIdConnectSession> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		finderPath = _finderPathWithPaginationFindByLtAccessTokenExpirationDate;
		finderArgs = new Object[] {
			_getTime(accessTokenExpirationDate), start, end, orderByComparator
		};

		List<OpenIdConnectSession> list = null;

		if (useFinderCache) {
			list = (List<OpenIdConnectSession>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (OpenIdConnectSession openIdConnectSession : list) {
					if (accessTokenExpirationDate.getTime() <=
							openIdConnectSession.getAccessTokenExpirationDate(
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

			sb.append(_SQL_SELECT_OPENIDCONNECTSESSION_WHERE);

			boolean bindAccessTokenExpirationDate = false;

			if (accessTokenExpirationDate == null) {
				sb.append(
					_FINDER_COLUMN_LTACCESSTOKENEXPIRATIONDATE_ACCESSTOKENEXPIRATIONDATE_1);
			}
			else {
				bindAccessTokenExpirationDate = true;

				sb.append(
					_FINDER_COLUMN_LTACCESSTOKENEXPIRATIONDATE_ACCESSTOKENEXPIRATIONDATE_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(OpenIdConnectSessionModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindAccessTokenExpirationDate) {
					queryPos.add(
						new Timestamp(accessTokenExpirationDate.getTime()));
				}

				list = (List<OpenIdConnectSession>)QueryUtil.list(
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
	 * Returns the first open ID connect session in the ordered set where accessTokenExpirationDate &lt; &#63;.
	 *
	 * @param accessTokenExpirationDate the access token expiration date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching open ID connect session
	 * @throws NoSuchSessionException if a matching open ID connect session could not be found
	 */
	@Override
	public OpenIdConnectSession findByLtAccessTokenExpirationDate_First(
			Date accessTokenExpirationDate,
			OrderByComparator<OpenIdConnectSession> orderByComparator)
		throws NoSuchSessionException {

		OpenIdConnectSession openIdConnectSession =
			fetchByLtAccessTokenExpirationDate_First(
				accessTokenExpirationDate, orderByComparator);

		if (openIdConnectSession != null) {
			return openIdConnectSession;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("accessTokenExpirationDate<");
		sb.append(accessTokenExpirationDate);

		sb.append("}");

		throw new NoSuchSessionException(sb.toString());
	}

	/**
	 * Returns the first open ID connect session in the ordered set where accessTokenExpirationDate &lt; &#63;.
	 *
	 * @param accessTokenExpirationDate the access token expiration date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching open ID connect session, or <code>null</code> if a matching open ID connect session could not be found
	 */
	@Override
	public OpenIdConnectSession fetchByLtAccessTokenExpirationDate_First(
		Date accessTokenExpirationDate,
		OrderByComparator<OpenIdConnectSession> orderByComparator) {

		List<OpenIdConnectSession> list = findByLtAccessTokenExpirationDate(
			accessTokenExpirationDate, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last open ID connect session in the ordered set where accessTokenExpirationDate &lt; &#63;.
	 *
	 * @param accessTokenExpirationDate the access token expiration date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching open ID connect session
	 * @throws NoSuchSessionException if a matching open ID connect session could not be found
	 */
	@Override
	public OpenIdConnectSession findByLtAccessTokenExpirationDate_Last(
			Date accessTokenExpirationDate,
			OrderByComparator<OpenIdConnectSession> orderByComparator)
		throws NoSuchSessionException {

		OpenIdConnectSession openIdConnectSession =
			fetchByLtAccessTokenExpirationDate_Last(
				accessTokenExpirationDate, orderByComparator);

		if (openIdConnectSession != null) {
			return openIdConnectSession;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("accessTokenExpirationDate<");
		sb.append(accessTokenExpirationDate);

		sb.append("}");

		throw new NoSuchSessionException(sb.toString());
	}

	/**
	 * Returns the last open ID connect session in the ordered set where accessTokenExpirationDate &lt; &#63;.
	 *
	 * @param accessTokenExpirationDate the access token expiration date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching open ID connect session, or <code>null</code> if a matching open ID connect session could not be found
	 */
	@Override
	public OpenIdConnectSession fetchByLtAccessTokenExpirationDate_Last(
		Date accessTokenExpirationDate,
		OrderByComparator<OpenIdConnectSession> orderByComparator) {

		int count = countByLtAccessTokenExpirationDate(
			accessTokenExpirationDate);

		if (count == 0) {
			return null;
		}

		List<OpenIdConnectSession> list = findByLtAccessTokenExpirationDate(
			accessTokenExpirationDate, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the open ID connect sessions before and after the current open ID connect session in the ordered set where accessTokenExpirationDate &lt; &#63;.
	 *
	 * @param openIdConnectSessionId the primary key of the current open ID connect session
	 * @param accessTokenExpirationDate the access token expiration date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next open ID connect session
	 * @throws NoSuchSessionException if a open ID connect session with the primary key could not be found
	 */
	@Override
	public OpenIdConnectSession[] findByLtAccessTokenExpirationDate_PrevAndNext(
			long openIdConnectSessionId, Date accessTokenExpirationDate,
			OrderByComparator<OpenIdConnectSession> orderByComparator)
		throws NoSuchSessionException {

		OpenIdConnectSession openIdConnectSession = findByPrimaryKey(
			openIdConnectSessionId);

		Session session = null;

		try {
			session = openSession();

			OpenIdConnectSession[] array = new OpenIdConnectSessionImpl[3];

			array[0] = getByLtAccessTokenExpirationDate_PrevAndNext(
				session, openIdConnectSession, accessTokenExpirationDate,
				orderByComparator, true);

			array[1] = openIdConnectSession;

			array[2] = getByLtAccessTokenExpirationDate_PrevAndNext(
				session, openIdConnectSession, accessTokenExpirationDate,
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

	protected OpenIdConnectSession getByLtAccessTokenExpirationDate_PrevAndNext(
		Session session, OpenIdConnectSession openIdConnectSession,
		Date accessTokenExpirationDate,
		OrderByComparator<OpenIdConnectSession> orderByComparator,
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

		sb.append(_SQL_SELECT_OPENIDCONNECTSESSION_WHERE);

		boolean bindAccessTokenExpirationDate = false;

		if (accessTokenExpirationDate == null) {
			sb.append(
				_FINDER_COLUMN_LTACCESSTOKENEXPIRATIONDATE_ACCESSTOKENEXPIRATIONDATE_1);
		}
		else {
			bindAccessTokenExpirationDate = true;

			sb.append(
				_FINDER_COLUMN_LTACCESSTOKENEXPIRATIONDATE_ACCESSTOKENEXPIRATIONDATE_2);
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
			sb.append(OpenIdConnectSessionModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindAccessTokenExpirationDate) {
			queryPos.add(new Timestamp(accessTokenExpirationDate.getTime()));
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						openIdConnectSession)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<OpenIdConnectSession> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the open ID connect sessions where accessTokenExpirationDate &lt; &#63; from the database.
	 *
	 * @param accessTokenExpirationDate the access token expiration date
	 */
	@Override
	public void removeByLtAccessTokenExpirationDate(
		Date accessTokenExpirationDate) {

		for (OpenIdConnectSession openIdConnectSession :
				findByLtAccessTokenExpirationDate(
					accessTokenExpirationDate, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(openIdConnectSession);
		}
	}

	/**
	 * Returns the number of open ID connect sessions where accessTokenExpirationDate &lt; &#63;.
	 *
	 * @param accessTokenExpirationDate the access token expiration date
	 * @return the number of matching open ID connect sessions
	 */
	@Override
	public int countByLtAccessTokenExpirationDate(
		Date accessTokenExpirationDate) {

		FinderPath finderPath =
			_finderPathWithPaginationCountByLtAccessTokenExpirationDate;

		Object[] finderArgs = new Object[] {
			_getTime(accessTokenExpirationDate)
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_OPENIDCONNECTSESSION_WHERE);

			boolean bindAccessTokenExpirationDate = false;

			if (accessTokenExpirationDate == null) {
				sb.append(
					_FINDER_COLUMN_LTACCESSTOKENEXPIRATIONDATE_ACCESSTOKENEXPIRATIONDATE_1);
			}
			else {
				bindAccessTokenExpirationDate = true;

				sb.append(
					_FINDER_COLUMN_LTACCESSTOKENEXPIRATIONDATE_ACCESSTOKENEXPIRATIONDATE_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindAccessTokenExpirationDate) {
					queryPos.add(
						new Timestamp(accessTokenExpirationDate.getTime()));
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

	private static final String
		_FINDER_COLUMN_LTACCESSTOKENEXPIRATIONDATE_ACCESSTOKENEXPIRATIONDATE_1 =
			"openIdConnectSession.accessTokenExpirationDate IS NULL";

	private static final String
		_FINDER_COLUMN_LTACCESSTOKENEXPIRATIONDATE_ACCESSTOKENEXPIRATIONDATE_2 =
			"openIdConnectSession.accessTokenExpirationDate < ?";

	private FinderPath _finderPathFetchByU_I;

	/**
	 * Returns the open ID connect session where userId = &#63; and issuer = &#63; or throws a <code>NoSuchSessionException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @param issuer the issuer
	 * @return the matching open ID connect session
	 * @throws NoSuchSessionException if a matching open ID connect session could not be found
	 */
	@Override
	public OpenIdConnectSession findByU_I(long userId, String issuer)
		throws NoSuchSessionException {

		OpenIdConnectSession openIdConnectSession = fetchByU_I(userId, issuer);

		if (openIdConnectSession == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("userId=");
			sb.append(userId);

			sb.append(", issuer=");
			sb.append(issuer);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchSessionException(sb.toString());
		}

		return openIdConnectSession;
	}

	/**
	 * Returns the open ID connect session where userId = &#63; and issuer = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param userId the user ID
	 * @param issuer the issuer
	 * @return the matching open ID connect session, or <code>null</code> if a matching open ID connect session could not be found
	 */
	@Override
	public OpenIdConnectSession fetchByU_I(long userId, String issuer) {
		return fetchByU_I(userId, issuer, true);
	}

	/**
	 * Returns the open ID connect session where userId = &#63; and issuer = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param issuer the issuer
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching open ID connect session, or <code>null</code> if a matching open ID connect session could not be found
	 */
	@Override
	public OpenIdConnectSession fetchByU_I(
		long userId, String issuer, boolean useFinderCache) {

		issuer = Objects.toString(issuer, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {userId, issuer};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByU_I, finderArgs, this);
		}

		if (result instanceof OpenIdConnectSession) {
			OpenIdConnectSession openIdConnectSession =
				(OpenIdConnectSession)result;

			if ((userId != openIdConnectSession.getUserId()) ||
				!Objects.equals(issuer, openIdConnectSession.getIssuer())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_OPENIDCONNECTSESSION_WHERE);

			sb.append(_FINDER_COLUMN_U_I_USERID_2);

			boolean bindIssuer = false;

			if (issuer.isEmpty()) {
				sb.append(_FINDER_COLUMN_U_I_ISSUER_3);
			}
			else {
				bindIssuer = true;

				sb.append(_FINDER_COLUMN_U_I_ISSUER_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(userId);

				if (bindIssuer) {
					queryPos.add(issuer);
				}

				List<OpenIdConnectSession> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByU_I, finderArgs, list);
					}
				}
				else {
					OpenIdConnectSession openIdConnectSession = list.get(0);

					result = openIdConnectSession;

					cacheResult(openIdConnectSession);
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
			return (OpenIdConnectSession)result;
		}
	}

	/**
	 * Removes the open ID connect session where userId = &#63; and issuer = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param issuer the issuer
	 * @return the open ID connect session that was removed
	 */
	@Override
	public OpenIdConnectSession removeByU_I(long userId, String issuer)
		throws NoSuchSessionException {

		OpenIdConnectSession openIdConnectSession = findByU_I(userId, issuer);

		return remove(openIdConnectSession);
	}

	/**
	 * Returns the number of open ID connect sessions where userId = &#63; and issuer = &#63;.
	 *
	 * @param userId the user ID
	 * @param issuer the issuer
	 * @return the number of matching open ID connect sessions
	 */
	@Override
	public int countByU_I(long userId, String issuer) {
		OpenIdConnectSession openIdConnectSession = fetchByU_I(userId, issuer);

		if (openIdConnectSession == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_U_I_USERID_2 =
		"openIdConnectSession.userId = ? AND ";

	private static final String _FINDER_COLUMN_U_I_ISSUER_2 =
		"openIdConnectSession.issuer = ?";

	private static final String _FINDER_COLUMN_U_I_ISSUER_3 =
		"(openIdConnectSession.issuer IS NULL OR openIdConnectSession.issuer = '')";

	private FinderPath _finderPathFetchByI_S;

	/**
	 * Returns the open ID connect session where issuer = &#63; and sessionId = &#63; or throws a <code>NoSuchSessionException</code> if it could not be found.
	 *
	 * @param issuer the issuer
	 * @param sessionId the session ID
	 * @return the matching open ID connect session
	 * @throws NoSuchSessionException if a matching open ID connect session could not be found
	 */
	@Override
	public OpenIdConnectSession findByI_S(String issuer, String sessionId)
		throws NoSuchSessionException {

		OpenIdConnectSession openIdConnectSession = fetchByI_S(
			issuer, sessionId);

		if (openIdConnectSession == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("issuer=");
			sb.append(issuer);

			sb.append(", sessionId=");
			sb.append(sessionId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchSessionException(sb.toString());
		}

		return openIdConnectSession;
	}

	/**
	 * Returns the open ID connect session where issuer = &#63; and sessionId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param issuer the issuer
	 * @param sessionId the session ID
	 * @return the matching open ID connect session, or <code>null</code> if a matching open ID connect session could not be found
	 */
	@Override
	public OpenIdConnectSession fetchByI_S(String issuer, String sessionId) {
		return fetchByI_S(issuer, sessionId, true);
	}

	/**
	 * Returns the open ID connect session where issuer = &#63; and sessionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param issuer the issuer
	 * @param sessionId the session ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching open ID connect session, or <code>null</code> if a matching open ID connect session could not be found
	 */
	@Override
	public OpenIdConnectSession fetchByI_S(
		String issuer, String sessionId, boolean useFinderCache) {

		issuer = Objects.toString(issuer, "");
		sessionId = Objects.toString(sessionId, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {issuer, sessionId};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByI_S, finderArgs, this);
		}

		if (result instanceof OpenIdConnectSession) {
			OpenIdConnectSession openIdConnectSession =
				(OpenIdConnectSession)result;

			if (!Objects.equals(issuer, openIdConnectSession.getIssuer()) ||
				!Objects.equals(
					sessionId, openIdConnectSession.getSessionId())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_OPENIDCONNECTSESSION_WHERE);

			boolean bindIssuer = false;

			if (issuer.isEmpty()) {
				sb.append(_FINDER_COLUMN_I_S_ISSUER_3);
			}
			else {
				bindIssuer = true;

				sb.append(_FINDER_COLUMN_I_S_ISSUER_2);
			}

			boolean bindSessionId = false;

			if (sessionId.isEmpty()) {
				sb.append(_FINDER_COLUMN_I_S_SESSIONID_3);
			}
			else {
				bindSessionId = true;

				sb.append(_FINDER_COLUMN_I_S_SESSIONID_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindIssuer) {
					queryPos.add(issuer);
				}

				if (bindSessionId) {
					queryPos.add(sessionId);
				}

				List<OpenIdConnectSession> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByI_S, finderArgs, list);
					}
				}
				else {
					OpenIdConnectSession openIdConnectSession = list.get(0);

					result = openIdConnectSession;

					cacheResult(openIdConnectSession);
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
			return (OpenIdConnectSession)result;
		}
	}

	/**
	 * Removes the open ID connect session where issuer = &#63; and sessionId = &#63; from the database.
	 *
	 * @param issuer the issuer
	 * @param sessionId the session ID
	 * @return the open ID connect session that was removed
	 */
	@Override
	public OpenIdConnectSession removeByI_S(String issuer, String sessionId)
		throws NoSuchSessionException {

		OpenIdConnectSession openIdConnectSession = findByI_S(
			issuer, sessionId);

		return remove(openIdConnectSession);
	}

	/**
	 * Returns the number of open ID connect sessions where issuer = &#63; and sessionId = &#63;.
	 *
	 * @param issuer the issuer
	 * @param sessionId the session ID
	 * @return the number of matching open ID connect sessions
	 */
	@Override
	public int countByI_S(String issuer, String sessionId) {
		OpenIdConnectSession openIdConnectSession = fetchByI_S(
			issuer, sessionId);

		if (openIdConnectSession == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_I_S_ISSUER_2 =
		"openIdConnectSession.issuer = ? AND ";

	private static final String _FINDER_COLUMN_I_S_ISSUER_3 =
		"(openIdConnectSession.issuer IS NULL OR openIdConnectSession.issuer = '') AND ";

	private static final String _FINDER_COLUMN_I_S_SESSIONID_2 =
		"openIdConnectSession.sessionId = ?";

	private static final String _FINDER_COLUMN_I_S_SESSIONID_3 =
		"(openIdConnectSession.sessionId IS NULL OR openIdConnectSession.sessionId = '')";

	private FinderPath _finderPathWithPaginationFindByC_A_C;
	private FinderPath _finderPathWithoutPaginationFindByC_A_C;
	private FinderPath _finderPathCountByC_A_C;

	/**
	 * Returns all the open ID connect sessions where companyId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @return the matching open ID connect sessions
	 */
	@Override
	public List<OpenIdConnectSession> findByC_A_C(
		long companyId, String authServerWellKnownURI, String clientId) {

		return findByC_A_C(
			companyId, authServerWellKnownURI, clientId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the open ID connect sessions where companyId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectSessionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @param start the lower bound of the range of open ID connect sessions
	 * @param end the upper bound of the range of open ID connect sessions (not inclusive)
	 * @return the range of matching open ID connect sessions
	 */
	@Override
	public List<OpenIdConnectSession> findByC_A_C(
		long companyId, String authServerWellKnownURI, String clientId,
		int start, int end) {

		return findByC_A_C(
			companyId, authServerWellKnownURI, clientId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the open ID connect sessions where companyId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectSessionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @param start the lower bound of the range of open ID connect sessions
	 * @param end the upper bound of the range of open ID connect sessions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching open ID connect sessions
	 */
	@Override
	public List<OpenIdConnectSession> findByC_A_C(
		long companyId, String authServerWellKnownURI, String clientId,
		int start, int end,
		OrderByComparator<OpenIdConnectSession> orderByComparator) {

		return findByC_A_C(
			companyId, authServerWellKnownURI, clientId, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the open ID connect sessions where companyId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectSessionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @param start the lower bound of the range of open ID connect sessions
	 * @param end the upper bound of the range of open ID connect sessions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching open ID connect sessions
	 */
	@Override
	public List<OpenIdConnectSession> findByC_A_C(
		long companyId, String authServerWellKnownURI, String clientId,
		int start, int end,
		OrderByComparator<OpenIdConnectSession> orderByComparator,
		boolean useFinderCache) {

		authServerWellKnownURI = Objects.toString(authServerWellKnownURI, "");
		clientId = Objects.toString(clientId, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByC_A_C;
				finderArgs = new Object[] {
					companyId, authServerWellKnownURI, clientId
				};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByC_A_C;
			finderArgs = new Object[] {
				companyId, authServerWellKnownURI, clientId, start, end,
				orderByComparator
			};
		}

		List<OpenIdConnectSession> list = null;

		if (useFinderCache) {
			list = (List<OpenIdConnectSession>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (OpenIdConnectSession openIdConnectSession : list) {
					if ((companyId != openIdConnectSession.getCompanyId()) ||
						!authServerWellKnownURI.equals(
							openIdConnectSession.getAuthServerWellKnownURI()) ||
						!clientId.equals(openIdConnectSession.getClientId())) {

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
					5 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				sb = new StringBundler(5);
			}

			sb.append(_SQL_SELECT_OPENIDCONNECTSESSION_WHERE);

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

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(OpenIdConnectSessionModelImpl.ORDER_BY_JPQL);
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

				list = (List<OpenIdConnectSession>)QueryUtil.list(
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
	 * Returns the first open ID connect session in the ordered set where companyId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching open ID connect session
	 * @throws NoSuchSessionException if a matching open ID connect session could not be found
	 */
	@Override
	public OpenIdConnectSession findByC_A_C_First(
			long companyId, String authServerWellKnownURI, String clientId,
			OrderByComparator<OpenIdConnectSession> orderByComparator)
		throws NoSuchSessionException {

		OpenIdConnectSession openIdConnectSession = fetchByC_A_C_First(
			companyId, authServerWellKnownURI, clientId, orderByComparator);

		if (openIdConnectSession != null) {
			return openIdConnectSession;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", authServerWellKnownURI=");
		sb.append(authServerWellKnownURI);

		sb.append(", clientId=");
		sb.append(clientId);

		sb.append("}");

		throw new NoSuchSessionException(sb.toString());
	}

	/**
	 * Returns the first open ID connect session in the ordered set where companyId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching open ID connect session, or <code>null</code> if a matching open ID connect session could not be found
	 */
	@Override
	public OpenIdConnectSession fetchByC_A_C_First(
		long companyId, String authServerWellKnownURI, String clientId,
		OrderByComparator<OpenIdConnectSession> orderByComparator) {

		List<OpenIdConnectSession> list = findByC_A_C(
			companyId, authServerWellKnownURI, clientId, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last open ID connect session in the ordered set where companyId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching open ID connect session
	 * @throws NoSuchSessionException if a matching open ID connect session could not be found
	 */
	@Override
	public OpenIdConnectSession findByC_A_C_Last(
			long companyId, String authServerWellKnownURI, String clientId,
			OrderByComparator<OpenIdConnectSession> orderByComparator)
		throws NoSuchSessionException {

		OpenIdConnectSession openIdConnectSession = fetchByC_A_C_Last(
			companyId, authServerWellKnownURI, clientId, orderByComparator);

		if (openIdConnectSession != null) {
			return openIdConnectSession;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", authServerWellKnownURI=");
		sb.append(authServerWellKnownURI);

		sb.append(", clientId=");
		sb.append(clientId);

		sb.append("}");

		throw new NoSuchSessionException(sb.toString());
	}

	/**
	 * Returns the last open ID connect session in the ordered set where companyId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching open ID connect session, or <code>null</code> if a matching open ID connect session could not be found
	 */
	@Override
	public OpenIdConnectSession fetchByC_A_C_Last(
		long companyId, String authServerWellKnownURI, String clientId,
		OrderByComparator<OpenIdConnectSession> orderByComparator) {

		int count = countByC_A_C(companyId, authServerWellKnownURI, clientId);

		if (count == 0) {
			return null;
		}

		List<OpenIdConnectSession> list = findByC_A_C(
			companyId, authServerWellKnownURI, clientId, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the open ID connect sessions before and after the current open ID connect session in the ordered set where companyId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63;.
	 *
	 * @param openIdConnectSessionId the primary key of the current open ID connect session
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next open ID connect session
	 * @throws NoSuchSessionException if a open ID connect session with the primary key could not be found
	 */
	@Override
	public OpenIdConnectSession[] findByC_A_C_PrevAndNext(
			long openIdConnectSessionId, long companyId,
			String authServerWellKnownURI, String clientId,
			OrderByComparator<OpenIdConnectSession> orderByComparator)
		throws NoSuchSessionException {

		authServerWellKnownURI = Objects.toString(authServerWellKnownURI, "");
		clientId = Objects.toString(clientId, "");

		OpenIdConnectSession openIdConnectSession = findByPrimaryKey(
			openIdConnectSessionId);

		Session session = null;

		try {
			session = openSession();

			OpenIdConnectSession[] array = new OpenIdConnectSessionImpl[3];

			array[0] = getByC_A_C_PrevAndNext(
				session, openIdConnectSession, companyId,
				authServerWellKnownURI, clientId, orderByComparator, true);

			array[1] = openIdConnectSession;

			array[2] = getByC_A_C_PrevAndNext(
				session, openIdConnectSession, companyId,
				authServerWellKnownURI, clientId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected OpenIdConnectSession getByC_A_C_PrevAndNext(
		Session session, OpenIdConnectSession openIdConnectSession,
		long companyId, String authServerWellKnownURI, String clientId,
		OrderByComparator<OpenIdConnectSession> orderByComparator,
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

		sb.append(_SQL_SELECT_OPENIDCONNECTSESSION_WHERE);

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
			sb.append(OpenIdConnectSessionModelImpl.ORDER_BY_JPQL);
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

		if (bindClientId) {
			queryPos.add(clientId);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						openIdConnectSession)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<OpenIdConnectSession> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the open ID connect sessions where companyId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 */
	@Override
	public void removeByC_A_C(
		long companyId, String authServerWellKnownURI, String clientId) {

		for (OpenIdConnectSession openIdConnectSession :
				findByC_A_C(
					companyId, authServerWellKnownURI, clientId,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(openIdConnectSession);
		}
	}

	/**
	 * Returns the number of open ID connect sessions where companyId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @return the number of matching open ID connect sessions
	 */
	@Override
	public int countByC_A_C(
		long companyId, String authServerWellKnownURI, String clientId) {

		authServerWellKnownURI = Objects.toString(authServerWellKnownURI, "");
		clientId = Objects.toString(clientId, "");

		FinderPath finderPath = _finderPathCountByC_A_C;

		Object[] finderArgs = new Object[] {
			companyId, authServerWellKnownURI, clientId
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_COUNT_OPENIDCONNECTSESSION_WHERE);

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

	private static final String _FINDER_COLUMN_C_A_C_COMPANYID_2 =
		"openIdConnectSession.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_A_C_AUTHSERVERWELLKNOWNURI_2 =
		"openIdConnectSession.authServerWellKnownURI = ? AND ";

	private static final String _FINDER_COLUMN_C_A_C_AUTHSERVERWELLKNOWNURI_3 =
		"(openIdConnectSession.authServerWellKnownURI IS NULL OR openIdConnectSession.authServerWellKnownURI = '') AND ";

	private static final String _FINDER_COLUMN_C_A_C_CLIENTID_2 =
		"openIdConnectSession.clientId = ?";

	private static final String _FINDER_COLUMN_C_A_C_CLIENTID_3 =
		"(openIdConnectSession.clientId IS NULL OR openIdConnectSession.clientId = '')";

	private FinderPath _finderPathFetchByU_A_C;

	/**
	 * Returns the open ID connect session where userId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63; or throws a <code>NoSuchSessionException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @return the matching open ID connect session
	 * @throws NoSuchSessionException if a matching open ID connect session could not be found
	 */
	@Override
	public OpenIdConnectSession findByU_A_C(
			long userId, String authServerWellKnownURI, String clientId)
		throws NoSuchSessionException {

		OpenIdConnectSession openIdConnectSession = fetchByU_A_C(
			userId, authServerWellKnownURI, clientId);

		if (openIdConnectSession == null) {
			StringBundler sb = new StringBundler(8);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("userId=");
			sb.append(userId);

			sb.append(", authServerWellKnownURI=");
			sb.append(authServerWellKnownURI);

			sb.append(", clientId=");
			sb.append(clientId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchSessionException(sb.toString());
		}

		return openIdConnectSession;
	}

	/**
	 * Returns the open ID connect session where userId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param userId the user ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @return the matching open ID connect session, or <code>null</code> if a matching open ID connect session could not be found
	 */
	@Override
	public OpenIdConnectSession fetchByU_A_C(
		long userId, String authServerWellKnownURI, String clientId) {

		return fetchByU_A_C(userId, authServerWellKnownURI, clientId, true);
	}

	/**
	 * Returns the open ID connect session where userId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching open ID connect session, or <code>null</code> if a matching open ID connect session could not be found
	 */
	@Override
	public OpenIdConnectSession fetchByU_A_C(
		long userId, String authServerWellKnownURI, String clientId,
		boolean useFinderCache) {

		authServerWellKnownURI = Objects.toString(authServerWellKnownURI, "");
		clientId = Objects.toString(clientId, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {
				userId, authServerWellKnownURI, clientId
			};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByU_A_C, finderArgs, this);
		}

		if (result instanceof OpenIdConnectSession) {
			OpenIdConnectSession openIdConnectSession =
				(OpenIdConnectSession)result;

			if ((userId != openIdConnectSession.getUserId()) ||
				!Objects.equals(
					authServerWellKnownURI,
					openIdConnectSession.getAuthServerWellKnownURI()) ||
				!Objects.equals(clientId, openIdConnectSession.getClientId())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(5);

			sb.append(_SQL_SELECT_OPENIDCONNECTSESSION_WHERE);

			sb.append(_FINDER_COLUMN_U_A_C_USERID_2);

			boolean bindAuthServerWellKnownURI = false;

			if (authServerWellKnownURI.isEmpty()) {
				sb.append(_FINDER_COLUMN_U_A_C_AUTHSERVERWELLKNOWNURI_3);
			}
			else {
				bindAuthServerWellKnownURI = true;

				sb.append(_FINDER_COLUMN_U_A_C_AUTHSERVERWELLKNOWNURI_2);
			}

			boolean bindClientId = false;

			if (clientId.isEmpty()) {
				sb.append(_FINDER_COLUMN_U_A_C_CLIENTID_3);
			}
			else {
				bindClientId = true;

				sb.append(_FINDER_COLUMN_U_A_C_CLIENTID_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(userId);

				if (bindAuthServerWellKnownURI) {
					queryPos.add(authServerWellKnownURI);
				}

				if (bindClientId) {
					queryPos.add(clientId);
				}

				List<OpenIdConnectSession> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByU_A_C, finderArgs, list);
					}
				}
				else {
					OpenIdConnectSession openIdConnectSession = list.get(0);

					result = openIdConnectSession;

					cacheResult(openIdConnectSession);
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
			return (OpenIdConnectSession)result;
		}
	}

	/**
	 * Removes the open ID connect session where userId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @return the open ID connect session that was removed
	 */
	@Override
	public OpenIdConnectSession removeByU_A_C(
			long userId, String authServerWellKnownURI, String clientId)
		throws NoSuchSessionException {

		OpenIdConnectSession openIdConnectSession = findByU_A_C(
			userId, authServerWellKnownURI, clientId);

		return remove(openIdConnectSession);
	}

	/**
	 * Returns the number of open ID connect sessions where userId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63;.
	 *
	 * @param userId the user ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @return the number of matching open ID connect sessions
	 */
	@Override
	public int countByU_A_C(
		long userId, String authServerWellKnownURI, String clientId) {

		OpenIdConnectSession openIdConnectSession = fetchByU_A_C(
			userId, authServerWellKnownURI, clientId);

		if (openIdConnectSession == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_U_A_C_USERID_2 =
		"openIdConnectSession.userId = ? AND ";

	private static final String _FINDER_COLUMN_U_A_C_AUTHSERVERWELLKNOWNURI_2 =
		"openIdConnectSession.authServerWellKnownURI = ? AND ";

	private static final String _FINDER_COLUMN_U_A_C_AUTHSERVERWELLKNOWNURI_3 =
		"(openIdConnectSession.authServerWellKnownURI IS NULL OR openIdConnectSession.authServerWellKnownURI = '') AND ";

	private static final String _FINDER_COLUMN_U_A_C_CLIENTID_2 =
		"openIdConnectSession.clientId = ?";

	private static final String _FINDER_COLUMN_U_A_C_CLIENTID_3 =
		"(openIdConnectSession.clientId IS NULL OR openIdConnectSession.clientId = '')";

	public OpenIdConnectSessionPersistenceImpl() {
		setModelClass(OpenIdConnectSession.class);

		setModelImplClass(OpenIdConnectSessionImpl.class);
		setModelPKClass(long.class);

		setTable(OpenIdConnectSessionTable.INSTANCE);
	}

	/**
	 * Caches the open ID connect session in the entity cache if it is enabled.
	 *
	 * @param openIdConnectSession the open ID connect session
	 */
	@Override
	public void cacheResult(OpenIdConnectSession openIdConnectSession) {
		entityCache.putResult(
			OpenIdConnectSessionImpl.class,
			openIdConnectSession.getPrimaryKey(), openIdConnectSession);

		finderCache.putResult(
			_finderPathFetchByU_I,
			new Object[] {
				openIdConnectSession.getUserId(),
				openIdConnectSession.getIssuer()
			},
			openIdConnectSession);

		finderCache.putResult(
			_finderPathFetchByI_S,
			new Object[] {
				openIdConnectSession.getIssuer(),
				openIdConnectSession.getSessionId()
			},
			openIdConnectSession);

		finderCache.putResult(
			_finderPathFetchByU_A_C,
			new Object[] {
				openIdConnectSession.getUserId(),
				openIdConnectSession.getAuthServerWellKnownURI(),
				openIdConnectSession.getClientId()
			},
			openIdConnectSession);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the open ID connect sessions in the entity cache if it is enabled.
	 *
	 * @param openIdConnectSessions the open ID connect sessions
	 */
	@Override
	public void cacheResult(List<OpenIdConnectSession> openIdConnectSessions) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (openIdConnectSessions.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (OpenIdConnectSession openIdConnectSession :
				openIdConnectSessions) {

			if (entityCache.getResult(
					OpenIdConnectSessionImpl.class,
					openIdConnectSession.getPrimaryKey()) == null) {

				cacheResult(openIdConnectSession);
			}
		}
	}

	/**
	 * Clears the cache for all open ID connect sessions.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(OpenIdConnectSessionImpl.class);

		finderCache.clearCache(OpenIdConnectSessionImpl.class);
	}

	/**
	 * Clears the cache for the open ID connect session.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(OpenIdConnectSession openIdConnectSession) {
		entityCache.removeResult(
			OpenIdConnectSessionImpl.class, openIdConnectSession);
	}

	@Override
	public void clearCache(List<OpenIdConnectSession> openIdConnectSessions) {
		for (OpenIdConnectSession openIdConnectSession :
				openIdConnectSessions) {

			entityCache.removeResult(
				OpenIdConnectSessionImpl.class, openIdConnectSession);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(OpenIdConnectSessionImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				OpenIdConnectSessionImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		OpenIdConnectSessionModelImpl openIdConnectSessionModelImpl) {

		Object[] args = new Object[] {
			openIdConnectSessionModelImpl.getUserId(),
			openIdConnectSessionModelImpl.getIssuer()
		};

		finderCache.putResult(
			_finderPathFetchByU_I, args, openIdConnectSessionModelImpl);

		args = new Object[] {
			openIdConnectSessionModelImpl.getIssuer(),
			openIdConnectSessionModelImpl.getSessionId()
		};

		finderCache.putResult(
			_finderPathFetchByI_S, args, openIdConnectSessionModelImpl);

		args = new Object[] {
			openIdConnectSessionModelImpl.getUserId(),
			openIdConnectSessionModelImpl.getAuthServerWellKnownURI(),
			openIdConnectSessionModelImpl.getClientId()
		};

		finderCache.putResult(
			_finderPathFetchByU_A_C, args, openIdConnectSessionModelImpl);
	}

	/**
	 * Creates a new open ID connect session with the primary key. Does not add the open ID connect session to the database.
	 *
	 * @param openIdConnectSessionId the primary key for the new open ID connect session
	 * @return the new open ID connect session
	 */
	@Override
	public OpenIdConnectSession create(long openIdConnectSessionId) {
		OpenIdConnectSession openIdConnectSession =
			new OpenIdConnectSessionImpl();

		openIdConnectSession.setNew(true);
		openIdConnectSession.setPrimaryKey(openIdConnectSessionId);

		openIdConnectSession.setCompanyId(CompanyThreadLocal.getCompanyId());

		return openIdConnectSession;
	}

	/**
	 * Removes the open ID connect session with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param openIdConnectSessionId the primary key of the open ID connect session
	 * @return the open ID connect session that was removed
	 * @throws NoSuchSessionException if a open ID connect session with the primary key could not be found
	 */
	@Override
	public OpenIdConnectSession remove(long openIdConnectSessionId)
		throws NoSuchSessionException {

		return remove((Serializable)openIdConnectSessionId);
	}

	/**
	 * Removes the open ID connect session with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the open ID connect session
	 * @return the open ID connect session that was removed
	 * @throws NoSuchSessionException if a open ID connect session with the primary key could not be found
	 */
	@Override
	public OpenIdConnectSession remove(Serializable primaryKey)
		throws NoSuchSessionException {

		Session session = null;

		try {
			session = openSession();

			OpenIdConnectSession openIdConnectSession =
				(OpenIdConnectSession)session.get(
					OpenIdConnectSessionImpl.class, primaryKey);

			if (openIdConnectSession == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchSessionException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(openIdConnectSession);
		}
		catch (NoSuchSessionException noSuchEntityException) {
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
	protected OpenIdConnectSession removeImpl(
		OpenIdConnectSession openIdConnectSession) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(openIdConnectSession)) {
				openIdConnectSession = (OpenIdConnectSession)session.get(
					OpenIdConnectSessionImpl.class,
					openIdConnectSession.getPrimaryKeyObj());
			}

			if (openIdConnectSession != null) {
				session.delete(openIdConnectSession);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (openIdConnectSession != null) {
			clearCache(openIdConnectSession);
		}

		return openIdConnectSession;
	}

	@Override
	public OpenIdConnectSession updateImpl(
		OpenIdConnectSession openIdConnectSession) {

		boolean isNew = openIdConnectSession.isNew();

		if (!(openIdConnectSession instanceof OpenIdConnectSessionModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(openIdConnectSession.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					openIdConnectSession);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in openIdConnectSession proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom OpenIdConnectSession implementation " +
					openIdConnectSession.getClass());
		}

		OpenIdConnectSessionModelImpl openIdConnectSessionModelImpl =
			(OpenIdConnectSessionModelImpl)openIdConnectSession;

		if (!openIdConnectSessionModelImpl.hasSetModifiedDate()) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				openIdConnectSession.setModifiedDate(date);
			}
			else {
				openIdConnectSession.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(openIdConnectSession);
			}
			else {
				openIdConnectSession = (OpenIdConnectSession)session.merge(
					openIdConnectSession);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			OpenIdConnectSessionImpl.class, openIdConnectSessionModelImpl,
			false, true);

		cacheUniqueFindersCache(openIdConnectSessionModelImpl);

		if (isNew) {
			openIdConnectSession.setNew(false);
		}

		openIdConnectSession.resetOriginalValues();

		return openIdConnectSession;
	}

	/**
	 * Returns the open ID connect session with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the open ID connect session
	 * @return the open ID connect session
	 * @throws NoSuchSessionException if a open ID connect session with the primary key could not be found
	 */
	@Override
	public OpenIdConnectSession findByPrimaryKey(Serializable primaryKey)
		throws NoSuchSessionException {

		OpenIdConnectSession openIdConnectSession = fetchByPrimaryKey(
			primaryKey);

		if (openIdConnectSession == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchSessionException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return openIdConnectSession;
	}

	/**
	 * Returns the open ID connect session with the primary key or throws a <code>NoSuchSessionException</code> if it could not be found.
	 *
	 * @param openIdConnectSessionId the primary key of the open ID connect session
	 * @return the open ID connect session
	 * @throws NoSuchSessionException if a open ID connect session with the primary key could not be found
	 */
	@Override
	public OpenIdConnectSession findByPrimaryKey(long openIdConnectSessionId)
		throws NoSuchSessionException {

		return findByPrimaryKey((Serializable)openIdConnectSessionId);
	}

	/**
	 * Returns the open ID connect session with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param openIdConnectSessionId the primary key of the open ID connect session
	 * @return the open ID connect session, or <code>null</code> if a open ID connect session with the primary key could not be found
	 */
	@Override
	public OpenIdConnectSession fetchByPrimaryKey(long openIdConnectSessionId) {
		return fetchByPrimaryKey((Serializable)openIdConnectSessionId);
	}

	/**
	 * Returns all the open ID connect sessions.
	 *
	 * @return the open ID connect sessions
	 */
	@Override
	public List<OpenIdConnectSession> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the open ID connect sessions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectSessionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of open ID connect sessions
	 * @param end the upper bound of the range of open ID connect sessions (not inclusive)
	 * @return the range of open ID connect sessions
	 */
	@Override
	public List<OpenIdConnectSession> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the open ID connect sessions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectSessionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of open ID connect sessions
	 * @param end the upper bound of the range of open ID connect sessions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of open ID connect sessions
	 */
	@Override
	public List<OpenIdConnectSession> findAll(
		int start, int end,
		OrderByComparator<OpenIdConnectSession> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the open ID connect sessions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectSessionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of open ID connect sessions
	 * @param end the upper bound of the range of open ID connect sessions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of open ID connect sessions
	 */
	@Override
	public List<OpenIdConnectSession> findAll(
		int start, int end,
		OrderByComparator<OpenIdConnectSession> orderByComparator,
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

		List<OpenIdConnectSession> list = null;

		if (useFinderCache) {
			list = (List<OpenIdConnectSession>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_OPENIDCONNECTSESSION);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_OPENIDCONNECTSESSION;

				sql = sql.concat(OpenIdConnectSessionModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<OpenIdConnectSession>)QueryUtil.list(
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
	 * Removes all the open ID connect sessions from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (OpenIdConnectSession openIdConnectSession : findAll()) {
			remove(openIdConnectSession);
		}
	}

	/**
	 * Returns the number of open ID connect sessions.
	 *
	 * @return the number of open ID connect sessions
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
					_SQL_COUNT_OPENIDCONNECTSESSION);

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
		return "openIdConnectSessionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OPENIDCONNECTSESSION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return OpenIdConnectSessionModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the open ID connect session persistence.
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

		_finderPathWithPaginationFindByLtAccessTokenExpirationDate =
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
				"findByLtAccessTokenExpirationDate",
				new String[] {
					Date.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"accessTokenExpirationDate"}, true);

		_finderPathWithPaginationCountByLtAccessTokenExpirationDate =
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
				"countByLtAccessTokenExpirationDate",
				new String[] {Date.class.getName()},
				new String[] {"accessTokenExpirationDate"}, false);

		_finderPathFetchByU_I = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByU_I",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"userId", "issuer"}, true);

		_finderPathFetchByI_S = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByI_S",
			new String[] {String.class.getName(), String.class.getName()},
			new String[] {"issuer", "sessionId"}, true);

		_finderPathWithPaginationFindByC_A_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_A_C",
			new String[] {
				Long.class.getName(), String.class.getName(),
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"companyId", "authServerWellKnownURI", "clientId"},
			true);

		_finderPathWithoutPaginationFindByC_A_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_A_C",
			new String[] {
				Long.class.getName(), String.class.getName(),
				String.class.getName()
			},
			new String[] {"companyId", "authServerWellKnownURI", "clientId"},
			true);

		_finderPathCountByC_A_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_A_C",
			new String[] {
				Long.class.getName(), String.class.getName(),
				String.class.getName()
			},
			new String[] {"companyId", "authServerWellKnownURI", "clientId"},
			false);

		_finderPathFetchByU_A_C = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByU_A_C",
			new String[] {
				Long.class.getName(), String.class.getName(),
				String.class.getName()
			},
			new String[] {"userId", "authServerWellKnownURI", "clientId"},
			true);

		OpenIdConnectSessionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		OpenIdConnectSessionUtil.setPersistence(null);

		entityCache.removeCache(OpenIdConnectSessionImpl.class.getName());
	}

	@Override
	@Reference(
		target = OpenIdConnectPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = OpenIdConnectPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = OpenIdConnectPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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

	private static final String _SQL_SELECT_OPENIDCONNECTSESSION =
		"SELECT openIdConnectSession FROM OpenIdConnectSession openIdConnectSession";

	private static final String _SQL_SELECT_OPENIDCONNECTSESSION_WHERE =
		"SELECT openIdConnectSession FROM OpenIdConnectSession openIdConnectSession WHERE ";

	private static final String _SQL_COUNT_OPENIDCONNECTSESSION =
		"SELECT COUNT(openIdConnectSession) FROM OpenIdConnectSession openIdConnectSession";

	private static final String _SQL_COUNT_OPENIDCONNECTSESSION_WHERE =
		"SELECT COUNT(openIdConnectSession) FROM OpenIdConnectSession openIdConnectSession WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS =
		"openIdConnectSession.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No OpenIdConnectSession exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No OpenIdConnectSession exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		OpenIdConnectSessionPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}