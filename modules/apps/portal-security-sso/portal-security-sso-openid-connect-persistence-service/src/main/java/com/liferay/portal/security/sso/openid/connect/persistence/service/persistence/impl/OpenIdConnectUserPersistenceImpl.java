/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
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
import com.liferay.portal.security.sso.openid.connect.persistence.exception.NoSuchUserException;
import com.liferay.portal.security.sso.openid.connect.persistence.model.OpenIdConnectUser;
import com.liferay.portal.security.sso.openid.connect.persistence.model.OpenIdConnectUserTable;
import com.liferay.portal.security.sso.openid.connect.persistence.model.impl.OpenIdConnectUserImpl;
import com.liferay.portal.security.sso.openid.connect.persistence.model.impl.OpenIdConnectUserModelImpl;
import com.liferay.portal.security.sso.openid.connect.persistence.service.persistence.OpenIdConnectUserPersistence;
import com.liferay.portal.security.sso.openid.connect.persistence.service.persistence.OpenIdConnectUserUtil;
import com.liferay.portal.security.sso.openid.connect.persistence.service.persistence.impl.constants.OpenIdConnectPersistenceConstants;

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
 * The persistence implementation for the open ID connect user service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Arthur Chan
 * @generated
 */
@Component(service = OpenIdConnectUserPersistence.class)
public class OpenIdConnectUserPersistenceImpl
	extends BasePersistenceImpl<OpenIdConnectUser>
	implements OpenIdConnectUserPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>OpenIdConnectUserUtil</code> to access the open ID connect user persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		OpenIdConnectUserImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByC_U;
	private FinderPath _finderPathWithoutPaginationFindByC_U;
	private FinderPath _finderPathCountByC_U;

	/**
	 * Returns all the open ID connect users where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the matching open ID connect users
	 */
	@Override
	public List<OpenIdConnectUser> findByC_U(long companyId, long userId) {
		return findByC_U(
			companyId, userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the open ID connect users where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectUserModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of open ID connect users
	 * @param end the upper bound of the range of open ID connect users (not inclusive)
	 * @return the range of matching open ID connect users
	 */
	@Override
	public List<OpenIdConnectUser> findByC_U(
		long companyId, long userId, int start, int end) {

		return findByC_U(companyId, userId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the open ID connect users where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectUserModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of open ID connect users
	 * @param end the upper bound of the range of open ID connect users (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching open ID connect users
	 */
	@Override
	public List<OpenIdConnectUser> findByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<OpenIdConnectUser> orderByComparator) {

		return findByC_U(
			companyId, userId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the open ID connect users where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectUserModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of open ID connect users
	 * @param end the upper bound of the range of open ID connect users (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching open ID connect users
	 */
	@Override
	public List<OpenIdConnectUser> findByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<OpenIdConnectUser> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByC_U;
				finderArgs = new Object[] {companyId, userId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByC_U;
			finderArgs = new Object[] {
				companyId, userId, start, end, orderByComparator
			};
		}

		List<OpenIdConnectUser> list = null;

		if (useFinderCache) {
			list = (List<OpenIdConnectUser>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (OpenIdConnectUser openIdConnectUser : list) {
					if ((companyId != openIdConnectUser.getCompanyId()) ||
						(userId != openIdConnectUser.getUserId())) {

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

			sb.append(_SQL_SELECT_OPENIDCONNECTUSER_WHERE);

			sb.append(_FINDER_COLUMN_C_U_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_U_USERID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(OpenIdConnectUserModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(userId);

				list = (List<OpenIdConnectUser>)QueryUtil.list(
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
	 * Returns the first open ID connect user in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching open ID connect user
	 * @throws NoSuchUserException if a matching open ID connect user could not be found
	 */
	@Override
	public OpenIdConnectUser findByC_U_First(
			long companyId, long userId,
			OrderByComparator<OpenIdConnectUser> orderByComparator)
		throws NoSuchUserException {

		OpenIdConnectUser openIdConnectUser = fetchByC_U_First(
			companyId, userId, orderByComparator);

		if (openIdConnectUser != null) {
			return openIdConnectUser;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append("}");

		throw new NoSuchUserException(sb.toString());
	}

	/**
	 * Returns the first open ID connect user in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching open ID connect user, or <code>null</code> if a matching open ID connect user could not be found
	 */
	@Override
	public OpenIdConnectUser fetchByC_U_First(
		long companyId, long userId,
		OrderByComparator<OpenIdConnectUser> orderByComparator) {

		List<OpenIdConnectUser> list = findByC_U(
			companyId, userId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last open ID connect user in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching open ID connect user
	 * @throws NoSuchUserException if a matching open ID connect user could not be found
	 */
	@Override
	public OpenIdConnectUser findByC_U_Last(
			long companyId, long userId,
			OrderByComparator<OpenIdConnectUser> orderByComparator)
		throws NoSuchUserException {

		OpenIdConnectUser openIdConnectUser = fetchByC_U_Last(
			companyId, userId, orderByComparator);

		if (openIdConnectUser != null) {
			return openIdConnectUser;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append("}");

		throw new NoSuchUserException(sb.toString());
	}

	/**
	 * Returns the last open ID connect user in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching open ID connect user, or <code>null</code> if a matching open ID connect user could not be found
	 */
	@Override
	public OpenIdConnectUser fetchByC_U_Last(
		long companyId, long userId,
		OrderByComparator<OpenIdConnectUser> orderByComparator) {

		int count = countByC_U(companyId, userId);

		if (count == 0) {
			return null;
		}

		List<OpenIdConnectUser> list = findByC_U(
			companyId, userId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the open ID connect users before and after the current open ID connect user in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param openIdConnectUserId the primary key of the current open ID connect user
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next open ID connect user
	 * @throws NoSuchUserException if a open ID connect user with the primary key could not be found
	 */
	@Override
	public OpenIdConnectUser[] findByC_U_PrevAndNext(
			long openIdConnectUserId, long companyId, long userId,
			OrderByComparator<OpenIdConnectUser> orderByComparator)
		throws NoSuchUserException {

		OpenIdConnectUser openIdConnectUser = findByPrimaryKey(
			openIdConnectUserId);

		Session session = null;

		try {
			session = openSession();

			OpenIdConnectUser[] array = new OpenIdConnectUserImpl[3];

			array[0] = getByC_U_PrevAndNext(
				session, openIdConnectUser, companyId, userId,
				orderByComparator, true);

			array[1] = openIdConnectUser;

			array[2] = getByC_U_PrevAndNext(
				session, openIdConnectUser, companyId, userId,
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

	protected OpenIdConnectUser getByC_U_PrevAndNext(
		Session session, OpenIdConnectUser openIdConnectUser, long companyId,
		long userId, OrderByComparator<OpenIdConnectUser> orderByComparator,
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

		sb.append(_SQL_SELECT_OPENIDCONNECTUSER_WHERE);

		sb.append(_FINDER_COLUMN_C_U_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_U_USERID_2);

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
			sb.append(OpenIdConnectUserModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(userId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						openIdConnectUser)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<OpenIdConnectUser> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the open ID connect users where companyId = &#63; and userId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 */
	@Override
	public void removeByC_U(long companyId, long userId) {
		for (OpenIdConnectUser openIdConnectUser :
				findByC_U(
					companyId, userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(openIdConnectUser);
		}
	}

	/**
	 * Returns the number of open ID connect users where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching open ID connect users
	 */
	@Override
	public int countByC_U(long companyId, long userId) {
		FinderPath finderPath = _finderPathCountByC_U;

		Object[] finderArgs = new Object[] {companyId, userId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_OPENIDCONNECTUSER_WHERE);

			sb.append(_FINDER_COLUMN_C_U_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_U_USERID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

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

	private static final String _FINDER_COLUMN_C_U_COMPANYID_2 =
		"openIdConnectUser.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_U_USERID_2 =
		"openIdConnectUser.userId = ?";

	private FinderPath _finderPathFetchByC_I_S;

	/**
	 * Returns the open ID connect user where companyId = &#63; and issuer = &#63; and subject = &#63; or throws a <code>NoSuchUserException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param issuer the issuer
	 * @param subject the subject
	 * @return the matching open ID connect user
	 * @throws NoSuchUserException if a matching open ID connect user could not be found
	 */
	@Override
	public OpenIdConnectUser findByC_I_S(
			long companyId, String issuer, String subject)
		throws NoSuchUserException {

		OpenIdConnectUser openIdConnectUser = fetchByC_I_S(
			companyId, issuer, subject);

		if (openIdConnectUser == null) {
			StringBundler sb = new StringBundler(8);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("companyId=");
			sb.append(companyId);

			sb.append(", issuer=");
			sb.append(issuer);

			sb.append(", subject=");
			sb.append(subject);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchUserException(sb.toString());
		}

		return openIdConnectUser;
	}

	/**
	 * Returns the open ID connect user where companyId = &#63; and issuer = &#63; and subject = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param issuer the issuer
	 * @param subject the subject
	 * @return the matching open ID connect user, or <code>null</code> if a matching open ID connect user could not be found
	 */
	@Override
	public OpenIdConnectUser fetchByC_I_S(
		long companyId, String issuer, String subject) {

		return fetchByC_I_S(companyId, issuer, subject, true);
	}

	/**
	 * Returns the open ID connect user where companyId = &#63; and issuer = &#63; and subject = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param issuer the issuer
	 * @param subject the subject
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching open ID connect user, or <code>null</code> if a matching open ID connect user could not be found
	 */
	@Override
	public OpenIdConnectUser fetchByC_I_S(
		long companyId, String issuer, String subject, boolean useFinderCache) {

		issuer = Objects.toString(issuer, "");
		subject = Objects.toString(subject, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {companyId, issuer, subject};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByC_I_S, finderArgs, this);
		}

		if (result instanceof OpenIdConnectUser) {
			OpenIdConnectUser openIdConnectUser = (OpenIdConnectUser)result;

			if ((companyId != openIdConnectUser.getCompanyId()) ||
				!Objects.equals(issuer, openIdConnectUser.getIssuer()) ||
				!Objects.equals(subject, openIdConnectUser.getSubject())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(5);

			sb.append(_SQL_SELECT_OPENIDCONNECTUSER_WHERE);

			sb.append(_FINDER_COLUMN_C_I_S_COMPANYID_2);

			boolean bindIssuer = false;

			if (issuer.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_I_S_ISSUER_3);
			}
			else {
				bindIssuer = true;

				sb.append(_FINDER_COLUMN_C_I_S_ISSUER_2);
			}

			boolean bindSubject = false;

			if (subject.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_I_S_SUBJECT_3);
			}
			else {
				bindSubject = true;

				sb.append(_FINDER_COLUMN_C_I_S_SUBJECT_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				if (bindIssuer) {
					queryPos.add(issuer);
				}

				if (bindSubject) {
					queryPos.add(subject);
				}

				List<OpenIdConnectUser> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByC_I_S, finderArgs, list);
					}
				}
				else {
					OpenIdConnectUser openIdConnectUser = list.get(0);

					result = openIdConnectUser;

					cacheResult(openIdConnectUser);
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
			return (OpenIdConnectUser)result;
		}
	}

	/**
	 * Removes the open ID connect user where companyId = &#63; and issuer = &#63; and subject = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param issuer the issuer
	 * @param subject the subject
	 * @return the open ID connect user that was removed
	 */
	@Override
	public OpenIdConnectUser removeByC_I_S(
			long companyId, String issuer, String subject)
		throws NoSuchUserException {

		OpenIdConnectUser openIdConnectUser = findByC_I_S(
			companyId, issuer, subject);

		return remove(openIdConnectUser);
	}

	/**
	 * Returns the number of open ID connect users where companyId = &#63; and issuer = &#63; and subject = &#63;.
	 *
	 * @param companyId the company ID
	 * @param issuer the issuer
	 * @param subject the subject
	 * @return the number of matching open ID connect users
	 */
	@Override
	public int countByC_I_S(long companyId, String issuer, String subject) {
		OpenIdConnectUser openIdConnectUser = fetchByC_I_S(
			companyId, issuer, subject);

		if (openIdConnectUser == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_C_I_S_COMPANYID_2 =
		"openIdConnectUser.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_I_S_ISSUER_2 =
		"openIdConnectUser.issuer = ? AND ";

	private static final String _FINDER_COLUMN_C_I_S_ISSUER_3 =
		"(openIdConnectUser.issuer IS NULL OR openIdConnectUser.issuer = '') AND ";

	private static final String _FINDER_COLUMN_C_I_S_SUBJECT_2 =
		"openIdConnectUser.subject = ?";

	private static final String _FINDER_COLUMN_C_I_S_SUBJECT_3 =
		"(openIdConnectUser.subject IS NULL OR openIdConnectUser.subject = '')";

	public OpenIdConnectUserPersistenceImpl() {
		setModelClass(OpenIdConnectUser.class);

		setModelImplClass(OpenIdConnectUserImpl.class);
		setModelPKClass(long.class);

		setTable(OpenIdConnectUserTable.INSTANCE);
	}

	/**
	 * Caches the open ID connect user in the entity cache if it is enabled.
	 *
	 * @param openIdConnectUser the open ID connect user
	 */
	@Override
	public void cacheResult(OpenIdConnectUser openIdConnectUser) {
		entityCache.putResult(
			OpenIdConnectUserImpl.class, openIdConnectUser.getPrimaryKey(),
			openIdConnectUser);

		finderCache.putResult(
			_finderPathFetchByC_I_S,
			new Object[] {
				openIdConnectUser.getCompanyId(), openIdConnectUser.getIssuer(),
				openIdConnectUser.getSubject()
			},
			openIdConnectUser);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the open ID connect users in the entity cache if it is enabled.
	 *
	 * @param openIdConnectUsers the open ID connect users
	 */
	@Override
	public void cacheResult(List<OpenIdConnectUser> openIdConnectUsers) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (openIdConnectUsers.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (OpenIdConnectUser openIdConnectUser : openIdConnectUsers) {
			if (entityCache.getResult(
					OpenIdConnectUserImpl.class,
					openIdConnectUser.getPrimaryKey()) == null) {

				cacheResult(openIdConnectUser);
			}
		}
	}

	/**
	 * Clears the cache for all open ID connect users.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(OpenIdConnectUserImpl.class);

		finderCache.clearCache(OpenIdConnectUserImpl.class);
	}

	/**
	 * Clears the cache for the open ID connect user.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(OpenIdConnectUser openIdConnectUser) {
		entityCache.removeResult(
			OpenIdConnectUserImpl.class, openIdConnectUser);
	}

	@Override
	public void clearCache(List<OpenIdConnectUser> openIdConnectUsers) {
		for (OpenIdConnectUser openIdConnectUser : openIdConnectUsers) {
			entityCache.removeResult(
				OpenIdConnectUserImpl.class, openIdConnectUser);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(OpenIdConnectUserImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(OpenIdConnectUserImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		OpenIdConnectUserModelImpl openIdConnectUserModelImpl) {

		Object[] args = new Object[] {
			openIdConnectUserModelImpl.getCompanyId(),
			openIdConnectUserModelImpl.getIssuer(),
			openIdConnectUserModelImpl.getSubject()
		};

		finderCache.putResult(
			_finderPathFetchByC_I_S, args, openIdConnectUserModelImpl);
	}

	/**
	 * Creates a new open ID connect user with the primary key. Does not add the open ID connect user to the database.
	 *
	 * @param openIdConnectUserId the primary key for the new open ID connect user
	 * @return the new open ID connect user
	 */
	@Override
	public OpenIdConnectUser create(long openIdConnectUserId) {
		OpenIdConnectUser openIdConnectUser = new OpenIdConnectUserImpl();

		openIdConnectUser.setNew(true);
		openIdConnectUser.setPrimaryKey(openIdConnectUserId);

		openIdConnectUser.setCompanyId(CompanyThreadLocal.getCompanyId());

		return openIdConnectUser;
	}

	/**
	 * Removes the open ID connect user with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param openIdConnectUserId the primary key of the open ID connect user
	 * @return the open ID connect user that was removed
	 * @throws NoSuchUserException if a open ID connect user with the primary key could not be found
	 */
	@Override
	public OpenIdConnectUser remove(long openIdConnectUserId)
		throws NoSuchUserException {

		return remove((Serializable)openIdConnectUserId);
	}

	/**
	 * Removes the open ID connect user with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the open ID connect user
	 * @return the open ID connect user that was removed
	 * @throws NoSuchUserException if a open ID connect user with the primary key could not be found
	 */
	@Override
	public OpenIdConnectUser remove(Serializable primaryKey)
		throws NoSuchUserException {

		Session session = null;

		try {
			session = openSession();

			OpenIdConnectUser openIdConnectUser =
				(OpenIdConnectUser)session.get(
					OpenIdConnectUserImpl.class, primaryKey);

			if (openIdConnectUser == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchUserException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(openIdConnectUser);
		}
		catch (NoSuchUserException noSuchEntityException) {
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
	protected OpenIdConnectUser removeImpl(
		OpenIdConnectUser openIdConnectUser) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(openIdConnectUser)) {
				openIdConnectUser = (OpenIdConnectUser)session.get(
					OpenIdConnectUserImpl.class,
					openIdConnectUser.getPrimaryKeyObj());
			}

			if (openIdConnectUser != null) {
				session.delete(openIdConnectUser);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (openIdConnectUser != null) {
			clearCache(openIdConnectUser);
		}

		return openIdConnectUser;
	}

	@Override
	public OpenIdConnectUser updateImpl(OpenIdConnectUser openIdConnectUser) {
		boolean isNew = openIdConnectUser.isNew();

		if (!(openIdConnectUser instanceof OpenIdConnectUserModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(openIdConnectUser.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					openIdConnectUser);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in openIdConnectUser proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom OpenIdConnectUser implementation " +
					openIdConnectUser.getClass());
		}

		OpenIdConnectUserModelImpl openIdConnectUserModelImpl =
			(OpenIdConnectUserModelImpl)openIdConnectUser;

		if (isNew && (openIdConnectUser.getCreateDate() == null)) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				openIdConnectUser.setCreateDate(date);
			}
			else {
				openIdConnectUser.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(openIdConnectUser);
			}
			else {
				openIdConnectUser = (OpenIdConnectUser)session.merge(
					openIdConnectUser);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			OpenIdConnectUserImpl.class, openIdConnectUserModelImpl, false,
			true);

		cacheUniqueFindersCache(openIdConnectUserModelImpl);

		if (isNew) {
			openIdConnectUser.setNew(false);
		}

		openIdConnectUser.resetOriginalValues();

		return openIdConnectUser;
	}

	/**
	 * Returns the open ID connect user with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the open ID connect user
	 * @return the open ID connect user
	 * @throws NoSuchUserException if a open ID connect user with the primary key could not be found
	 */
	@Override
	public OpenIdConnectUser findByPrimaryKey(Serializable primaryKey)
		throws NoSuchUserException {

		OpenIdConnectUser openIdConnectUser = fetchByPrimaryKey(primaryKey);

		if (openIdConnectUser == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchUserException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return openIdConnectUser;
	}

	/**
	 * Returns the open ID connect user with the primary key or throws a <code>NoSuchUserException</code> if it could not be found.
	 *
	 * @param openIdConnectUserId the primary key of the open ID connect user
	 * @return the open ID connect user
	 * @throws NoSuchUserException if a open ID connect user with the primary key could not be found
	 */
	@Override
	public OpenIdConnectUser findByPrimaryKey(long openIdConnectUserId)
		throws NoSuchUserException {

		return findByPrimaryKey((Serializable)openIdConnectUserId);
	}

	/**
	 * Returns the open ID connect user with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param openIdConnectUserId the primary key of the open ID connect user
	 * @return the open ID connect user, or <code>null</code> if a open ID connect user with the primary key could not be found
	 */
	@Override
	public OpenIdConnectUser fetchByPrimaryKey(long openIdConnectUserId) {
		return fetchByPrimaryKey((Serializable)openIdConnectUserId);
	}

	/**
	 * Returns all the open ID connect users.
	 *
	 * @return the open ID connect users
	 */
	@Override
	public List<OpenIdConnectUser> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the open ID connect users.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectUserModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of open ID connect users
	 * @param end the upper bound of the range of open ID connect users (not inclusive)
	 * @return the range of open ID connect users
	 */
	@Override
	public List<OpenIdConnectUser> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the open ID connect users.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectUserModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of open ID connect users
	 * @param end the upper bound of the range of open ID connect users (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of open ID connect users
	 */
	@Override
	public List<OpenIdConnectUser> findAll(
		int start, int end,
		OrderByComparator<OpenIdConnectUser> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the open ID connect users.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectUserModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of open ID connect users
	 * @param end the upper bound of the range of open ID connect users (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of open ID connect users
	 */
	@Override
	public List<OpenIdConnectUser> findAll(
		int start, int end,
		OrderByComparator<OpenIdConnectUser> orderByComparator,
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

		List<OpenIdConnectUser> list = null;

		if (useFinderCache) {
			list = (List<OpenIdConnectUser>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_OPENIDCONNECTUSER);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_OPENIDCONNECTUSER;

				sql = sql.concat(OpenIdConnectUserModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<OpenIdConnectUser>)QueryUtil.list(
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
	 * Removes all the open ID connect users from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (OpenIdConnectUser openIdConnectUser : findAll()) {
			remove(openIdConnectUser);
		}
	}

	/**
	 * Returns the number of open ID connect users.
	 *
	 * @return the number of open ID connect users
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_OPENIDCONNECTUSER);

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
		return "openIdConnectUserId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OPENIDCONNECTUSER;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return OpenIdConnectUserModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the open ID connect user persistence.
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

		_finderPathWithPaginationFindByC_U = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_U",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "userId"}, true);

		_finderPathWithoutPaginationFindByC_U = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_U",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"companyId", "userId"}, true);

		_finderPathCountByC_U = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_U",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"companyId", "userId"}, false);

		_finderPathFetchByC_I_S = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByC_I_S",
			new String[] {
				Long.class.getName(), String.class.getName(),
				String.class.getName()
			},
			new String[] {"companyId", "issuer", "subject"}, true);

		OpenIdConnectUserUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		OpenIdConnectUserUtil.setPersistence(null);

		entityCache.removeCache(OpenIdConnectUserImpl.class.getName());
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

	private static final String _SQL_SELECT_OPENIDCONNECTUSER =
		"SELECT openIdConnectUser FROM OpenIdConnectUser openIdConnectUser";

	private static final String _SQL_SELECT_OPENIDCONNECTUSER_WHERE =
		"SELECT openIdConnectUser FROM OpenIdConnectUser openIdConnectUser WHERE ";

	private static final String _SQL_COUNT_OPENIDCONNECTUSER =
		"SELECT COUNT(openIdConnectUser) FROM OpenIdConnectUser openIdConnectUser";

	private static final String _SQL_COUNT_OPENIDCONNECTUSER_WHERE =
		"SELECT COUNT(openIdConnectUser) FROM OpenIdConnectUser openIdConnectUser WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS = "openIdConnectUser.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No OpenIdConnectUser exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No OpenIdConnectUser exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		OpenIdConnectUserPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}