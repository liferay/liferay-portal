/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.service.persistence.impl;

import com.liferay.cookies.exception.NoSuchCookiesConsentPreferenceException;
import com.liferay.cookies.model.CookiesConsentPreference;
import com.liferay.cookies.model.CookiesConsentPreferenceTable;
import com.liferay.cookies.model.impl.CookiesConsentPreferenceImpl;
import com.liferay.cookies.model.impl.CookiesConsentPreferenceModelImpl;
import com.liferay.cookies.service.persistence.CookiesConsentPreferencePersistence;
import com.liferay.cookies.service.persistence.CookiesConsentPreferenceUtil;
import com.liferay.cookies.service.persistence.impl.constants.CookiesPersistenceConstants;
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
 * The persistence implementation for the cookies consent preference service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Christopher Kian
 * @generated
 */
@Component(service = CookiesConsentPreferencePersistence.class)
public class CookiesConsentPreferencePersistenceImpl
	extends BasePersistenceImpl<CookiesConsentPreference>
	implements CookiesConsentPreferencePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CookiesConsentPreferenceUtil</code> to access the cookies consent preference persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CookiesConsentPreferenceImpl.class.getName();

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
	 * Returns all the cookies consent preferences where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching cookies consent preferences
	 */
	@Override
	public List<CookiesConsentPreference> findByUserId(long userId) {
		return findByUserId(userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cookies consent preferences where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CookiesConsentPreferenceModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of cookies consent preferences
	 * @param end the upper bound of the range of cookies consent preferences (not inclusive)
	 * @return the range of matching cookies consent preferences
	 */
	@Override
	public List<CookiesConsentPreference> findByUserId(
		long userId, int start, int end) {

		return findByUserId(userId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cookies consent preferences where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CookiesConsentPreferenceModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of cookies consent preferences
	 * @param end the upper bound of the range of cookies consent preferences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cookies consent preferences
	 */
	@Override
	public List<CookiesConsentPreference> findByUserId(
		long userId, int start, int end,
		OrderByComparator<CookiesConsentPreference> orderByComparator) {

		return findByUserId(userId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cookies consent preferences where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CookiesConsentPreferenceModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of cookies consent preferences
	 * @param end the upper bound of the range of cookies consent preferences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cookies consent preferences
	 */
	@Override
	public List<CookiesConsentPreference> findByUserId(
		long userId, int start, int end,
		OrderByComparator<CookiesConsentPreference> orderByComparator,
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

		List<CookiesConsentPreference> list = null;

		if (useFinderCache) {
			list = (List<CookiesConsentPreference>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CookiesConsentPreference cookiesConsentPreference : list) {
					if (userId != cookiesConsentPreference.getUserId()) {
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

			sb.append(_SQL_SELECT_COOKIESCONSENTPREFERENCE_WHERE);

			sb.append(_FINDER_COLUMN_USERID_USERID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CookiesConsentPreferenceModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(userId);

				list = (List<CookiesConsentPreference>)QueryUtil.list(
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
	 * Returns the first cookies consent preference in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cookies consent preference
	 * @throws NoSuchCookiesConsentPreferenceException if a matching cookies consent preference could not be found
	 */
	@Override
	public CookiesConsentPreference findByUserId_First(
			long userId,
			OrderByComparator<CookiesConsentPreference> orderByComparator)
		throws NoSuchCookiesConsentPreferenceException {

		CookiesConsentPreference cookiesConsentPreference = fetchByUserId_First(
			userId, orderByComparator);

		if (cookiesConsentPreference != null) {
			return cookiesConsentPreference;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append("}");

		throw new NoSuchCookiesConsentPreferenceException(sb.toString());
	}

	/**
	 * Returns the first cookies consent preference in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cookies consent preference, or <code>null</code> if a matching cookies consent preference could not be found
	 */
	@Override
	public CookiesConsentPreference fetchByUserId_First(
		long userId,
		OrderByComparator<CookiesConsentPreference> orderByComparator) {

		List<CookiesConsentPreference> list = findByUserId(
			userId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Removes all the cookies consent preferences where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		for (CookiesConsentPreference cookiesConsentPreference :
				findByUserId(
					userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(cookiesConsentPreference);
		}
	}

	/**
	 * Returns the number of cookies consent preferences where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching cookies consent preferences
	 */
	@Override
	public int countByUserId(long userId) {
		FinderPath finderPath = _finderPathCountByUserId;

		Object[] finderArgs = new Object[] {userId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_COOKIESCONSENTPREFERENCE_WHERE);

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
		"cookiesConsentPreference.userId = ?";

	private FinderPath _finderPathWithPaginationFindByExpirationDate;
	private FinderPath _finderPathWithoutPaginationFindByExpirationDate;
	private FinderPath _finderPathCountByExpirationDate;

	/**
	 * Returns all the cookies consent preferences where expirationDate = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @return the matching cookies consent preferences
	 */
	@Override
	public List<CookiesConsentPreference> findByExpirationDate(
		Date expirationDate) {

		return findByExpirationDate(
			expirationDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cookies consent preferences where expirationDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CookiesConsentPreferenceModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param start the lower bound of the range of cookies consent preferences
	 * @param end the upper bound of the range of cookies consent preferences (not inclusive)
	 * @return the range of matching cookies consent preferences
	 */
	@Override
	public List<CookiesConsentPreference> findByExpirationDate(
		Date expirationDate, int start, int end) {

		return findByExpirationDate(expirationDate, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cookies consent preferences where expirationDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CookiesConsentPreferenceModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param start the lower bound of the range of cookies consent preferences
	 * @param end the upper bound of the range of cookies consent preferences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cookies consent preferences
	 */
	@Override
	public List<CookiesConsentPreference> findByExpirationDate(
		Date expirationDate, int start, int end,
		OrderByComparator<CookiesConsentPreference> orderByComparator) {

		return findByExpirationDate(
			expirationDate, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cookies consent preferences where expirationDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CookiesConsentPreferenceModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param start the lower bound of the range of cookies consent preferences
	 * @param end the upper bound of the range of cookies consent preferences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cookies consent preferences
	 */
	@Override
	public List<CookiesConsentPreference> findByExpirationDate(
		Date expirationDate, int start, int end,
		OrderByComparator<CookiesConsentPreference> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByExpirationDate;
				finderArgs = new Object[] {_getTime(expirationDate)};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByExpirationDate;
			finderArgs = new Object[] {
				_getTime(expirationDate), start, end, orderByComparator
			};
		}

		List<CookiesConsentPreference> list = null;

		if (useFinderCache) {
			list = (List<CookiesConsentPreference>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CookiesConsentPreference cookiesConsentPreference : list) {
					if (!Objects.equals(
							expirationDate,
							cookiesConsentPreference.getExpirationDate())) {

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

			sb.append(_SQL_SELECT_COOKIESCONSENTPREFERENCE_WHERE);

			boolean bindExpirationDate = false;

			if (expirationDate == null) {
				sb.append(_FINDER_COLUMN_EXPIRATIONDATE_EXPIRATIONDATE_1);
			}
			else {
				bindExpirationDate = true;

				sb.append(_FINDER_COLUMN_EXPIRATIONDATE_EXPIRATIONDATE_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CookiesConsentPreferenceModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindExpirationDate) {
					queryPos.add(new Timestamp(expirationDate.getTime()));
				}

				list = (List<CookiesConsentPreference>)QueryUtil.list(
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
	 * Returns the first cookies consent preference in the ordered set where expirationDate = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cookies consent preference
	 * @throws NoSuchCookiesConsentPreferenceException if a matching cookies consent preference could not be found
	 */
	@Override
	public CookiesConsentPreference findByExpirationDate_First(
			Date expirationDate,
			OrderByComparator<CookiesConsentPreference> orderByComparator)
		throws NoSuchCookiesConsentPreferenceException {

		CookiesConsentPreference cookiesConsentPreference =
			fetchByExpirationDate_First(expirationDate, orderByComparator);

		if (cookiesConsentPreference != null) {
			return cookiesConsentPreference;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("expirationDate=");
		sb.append(expirationDate);

		sb.append("}");

		throw new NoSuchCookiesConsentPreferenceException(sb.toString());
	}

	/**
	 * Returns the first cookies consent preference in the ordered set where expirationDate = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cookies consent preference, or <code>null</code> if a matching cookies consent preference could not be found
	 */
	@Override
	public CookiesConsentPreference fetchByExpirationDate_First(
		Date expirationDate,
		OrderByComparator<CookiesConsentPreference> orderByComparator) {

		List<CookiesConsentPreference> list = findByExpirationDate(
			expirationDate, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Removes all the cookies consent preferences where expirationDate = &#63; from the database.
	 *
	 * @param expirationDate the expiration date
	 */
	@Override
	public void removeByExpirationDate(Date expirationDate) {
		for (CookiesConsentPreference cookiesConsentPreference :
				findByExpirationDate(
					expirationDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(cookiesConsentPreference);
		}
	}

	/**
	 * Returns the number of cookies consent preferences where expirationDate = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @return the number of matching cookies consent preferences
	 */
	@Override
	public int countByExpirationDate(Date expirationDate) {
		FinderPath finderPath = _finderPathCountByExpirationDate;

		Object[] finderArgs = new Object[] {_getTime(expirationDate)};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_COOKIESCONSENTPREFERENCE_WHERE);

			boolean bindExpirationDate = false;

			if (expirationDate == null) {
				sb.append(_FINDER_COLUMN_EXPIRATIONDATE_EXPIRATIONDATE_1);
			}
			else {
				bindExpirationDate = true;

				sb.append(_FINDER_COLUMN_EXPIRATIONDATE_EXPIRATIONDATE_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindExpirationDate) {
					queryPos.add(new Timestamp(expirationDate.getTime()));
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

	private static final String _FINDER_COLUMN_EXPIRATIONDATE_EXPIRATIONDATE_1 =
		"cookiesConsentPreference.expirationDate IS NULL";

	private static final String _FINDER_COLUMN_EXPIRATIONDATE_EXPIRATIONDATE_2 =
		"cookiesConsentPreference.expirationDate = ?";

	private FinderPath _finderPathWithPaginationFindByU_D;
	private FinderPath _finderPathWithoutPaginationFindByU_D;
	private FinderPath _finderPathCountByU_D;

	/**
	 * Returns all the cookies consent preferences where userId = &#63; and domain = &#63;.
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @return the matching cookies consent preferences
	 */
	@Override
	public List<CookiesConsentPreference> findByU_D(
		long userId, String domain) {

		return findByU_D(
			userId, domain, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cookies consent preferences where userId = &#63; and domain = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CookiesConsentPreferenceModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @param start the lower bound of the range of cookies consent preferences
	 * @param end the upper bound of the range of cookies consent preferences (not inclusive)
	 * @return the range of matching cookies consent preferences
	 */
	@Override
	public List<CookiesConsentPreference> findByU_D(
		long userId, String domain, int start, int end) {

		return findByU_D(userId, domain, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cookies consent preferences where userId = &#63; and domain = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CookiesConsentPreferenceModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @param start the lower bound of the range of cookies consent preferences
	 * @param end the upper bound of the range of cookies consent preferences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cookies consent preferences
	 */
	@Override
	public List<CookiesConsentPreference> findByU_D(
		long userId, String domain, int start, int end,
		OrderByComparator<CookiesConsentPreference> orderByComparator) {

		return findByU_D(userId, domain, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cookies consent preferences where userId = &#63; and domain = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CookiesConsentPreferenceModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @param start the lower bound of the range of cookies consent preferences
	 * @param end the upper bound of the range of cookies consent preferences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cookies consent preferences
	 */
	@Override
	public List<CookiesConsentPreference> findByU_D(
		long userId, String domain, int start, int end,
		OrderByComparator<CookiesConsentPreference> orderByComparator,
		boolean useFinderCache) {

		domain = Objects.toString(domain, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByU_D;
				finderArgs = new Object[] {userId, domain};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByU_D;
			finderArgs = new Object[] {
				userId, domain, start, end, orderByComparator
			};
		}

		List<CookiesConsentPreference> list = null;

		if (useFinderCache) {
			list = (List<CookiesConsentPreference>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CookiesConsentPreference cookiesConsentPreference : list) {
					if ((userId != cookiesConsentPreference.getUserId()) ||
						!domain.equals(cookiesConsentPreference.getDomain())) {

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

			sb.append(_SQL_SELECT_COOKIESCONSENTPREFERENCE_WHERE);

			sb.append(_FINDER_COLUMN_U_D_USERID_2);

			boolean bindDomain = false;

			if (domain.isEmpty()) {
				sb.append(_FINDER_COLUMN_U_D_DOMAIN_3);
			}
			else {
				bindDomain = true;

				sb.append(_FINDER_COLUMN_U_D_DOMAIN_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CookiesConsentPreferenceModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(userId);

				if (bindDomain) {
					queryPos.add(domain);
				}

				list = (List<CookiesConsentPreference>)QueryUtil.list(
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
	 * Returns the first cookies consent preference in the ordered set where userId = &#63; and domain = &#63;.
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cookies consent preference
	 * @throws NoSuchCookiesConsentPreferenceException if a matching cookies consent preference could not be found
	 */
	@Override
	public CookiesConsentPreference findByU_D_First(
			long userId, String domain,
			OrderByComparator<CookiesConsentPreference> orderByComparator)
		throws NoSuchCookiesConsentPreferenceException {

		CookiesConsentPreference cookiesConsentPreference = fetchByU_D_First(
			userId, domain, orderByComparator);

		if (cookiesConsentPreference != null) {
			return cookiesConsentPreference;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append(", domain=");
		sb.append(domain);

		sb.append("}");

		throw new NoSuchCookiesConsentPreferenceException(sb.toString());
	}

	/**
	 * Returns the first cookies consent preference in the ordered set where userId = &#63; and domain = &#63;.
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cookies consent preference, or <code>null</code> if a matching cookies consent preference could not be found
	 */
	@Override
	public CookiesConsentPreference fetchByU_D_First(
		long userId, String domain,
		OrderByComparator<CookiesConsentPreference> orderByComparator) {

		List<CookiesConsentPreference> list = findByU_D(
			userId, domain, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Removes all the cookies consent preferences where userId = &#63; and domain = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 */
	@Override
	public void removeByU_D(long userId, String domain) {
		for (CookiesConsentPreference cookiesConsentPreference :
				findByU_D(
					userId, domain, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(cookiesConsentPreference);
		}
	}

	/**
	 * Returns the number of cookies consent preferences where userId = &#63; and domain = &#63;.
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @return the number of matching cookies consent preferences
	 */
	@Override
	public int countByU_D(long userId, String domain) {
		domain = Objects.toString(domain, "");

		FinderPath finderPath = _finderPathCountByU_D;

		Object[] finderArgs = new Object[] {userId, domain};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_COOKIESCONSENTPREFERENCE_WHERE);

			sb.append(_FINDER_COLUMN_U_D_USERID_2);

			boolean bindDomain = false;

			if (domain.isEmpty()) {
				sb.append(_FINDER_COLUMN_U_D_DOMAIN_3);
			}
			else {
				bindDomain = true;

				sb.append(_FINDER_COLUMN_U_D_DOMAIN_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(userId);

				if (bindDomain) {
					queryPos.add(domain);
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

	private static final String _FINDER_COLUMN_U_D_USERID_2 =
		"cookiesConsentPreference.userId = ? AND ";

	private static final String _FINDER_COLUMN_U_D_DOMAIN_2 =
		"cookiesConsentPreference.domain = ?";

	private static final String _FINDER_COLUMN_U_D_DOMAIN_3 =
		"(cookiesConsentPreference.domain IS NULL OR cookiesConsentPreference.domain = '')";

	private FinderPath _finderPathFetchByU_D_N;

	/**
	 * Returns the cookies consent preference where userId = &#63; and domain = &#63; and name = &#63; or throws a <code>NoSuchCookiesConsentPreferenceException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @param name the name
	 * @return the matching cookies consent preference
	 * @throws NoSuchCookiesConsentPreferenceException if a matching cookies consent preference could not be found
	 */
	@Override
	public CookiesConsentPreference findByU_D_N(
			long userId, String domain, String name)
		throws NoSuchCookiesConsentPreferenceException {

		CookiesConsentPreference cookiesConsentPreference = fetchByU_D_N(
			userId, domain, name);

		if (cookiesConsentPreference == null) {
			StringBundler sb = new StringBundler(8);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("userId=");
			sb.append(userId);

			sb.append(", domain=");
			sb.append(domain);

			sb.append(", name=");
			sb.append(name);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchCookiesConsentPreferenceException(sb.toString());
		}

		return cookiesConsentPreference;
	}

	/**
	 * Returns the cookies consent preference where userId = &#63; and domain = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @param name the name
	 * @return the matching cookies consent preference, or <code>null</code> if a matching cookies consent preference could not be found
	 */
	@Override
	public CookiesConsentPreference fetchByU_D_N(
		long userId, String domain, String name) {

		return fetchByU_D_N(userId, domain, name, true);
	}

	/**
	 * Returns the cookies consent preference where userId = &#63; and domain = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cookies consent preference, or <code>null</code> if a matching cookies consent preference could not be found
	 */
	@Override
	public CookiesConsentPreference fetchByU_D_N(
		long userId, String domain, String name, boolean useFinderCache) {

		domain = Objects.toString(domain, "");
		name = Objects.toString(name, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {userId, domain, name};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByU_D_N, finderArgs, this);
		}

		if (result instanceof CookiesConsentPreference) {
			CookiesConsentPreference cookiesConsentPreference =
				(CookiesConsentPreference)result;

			if ((userId != cookiesConsentPreference.getUserId()) ||
				!Objects.equals(domain, cookiesConsentPreference.getDomain()) ||
				!Objects.equals(name, cookiesConsentPreference.getName())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(5);

			sb.append(_SQL_SELECT_COOKIESCONSENTPREFERENCE_WHERE);

			sb.append(_FINDER_COLUMN_U_D_N_USERID_2);

			boolean bindDomain = false;

			if (domain.isEmpty()) {
				sb.append(_FINDER_COLUMN_U_D_N_DOMAIN_3);
			}
			else {
				bindDomain = true;

				sb.append(_FINDER_COLUMN_U_D_N_DOMAIN_2);
			}

			boolean bindName = false;

			if (name.isEmpty()) {
				sb.append(_FINDER_COLUMN_U_D_N_NAME_3);
			}
			else {
				bindName = true;

				sb.append(_FINDER_COLUMN_U_D_N_NAME_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(userId);

				if (bindDomain) {
					queryPos.add(domain);
				}

				if (bindName) {
					queryPos.add(name);
				}

				List<CookiesConsentPreference> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByU_D_N, finderArgs, list);
					}
				}
				else {
					CookiesConsentPreference cookiesConsentPreference =
						list.get(0);

					result = cookiesConsentPreference;

					cacheResult(cookiesConsentPreference);
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
			return (CookiesConsentPreference)result;
		}
	}

	/**
	 * Removes the cookies consent preference where userId = &#63; and domain = &#63; and name = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @param name the name
	 * @return the cookies consent preference that was removed
	 */
	@Override
	public CookiesConsentPreference removeByU_D_N(
			long userId, String domain, String name)
		throws NoSuchCookiesConsentPreferenceException {

		CookiesConsentPreference cookiesConsentPreference = findByU_D_N(
			userId, domain, name);

		return remove(cookiesConsentPreference);
	}

	/**
	 * Returns the number of cookies consent preferences where userId = &#63; and domain = &#63; and name = &#63;.
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @param name the name
	 * @return the number of matching cookies consent preferences
	 */
	@Override
	public int countByU_D_N(long userId, String domain, String name) {
		CookiesConsentPreference cookiesConsentPreference = fetchByU_D_N(
			userId, domain, name);

		if (cookiesConsentPreference == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_U_D_N_USERID_2 =
		"cookiesConsentPreference.userId = ? AND ";

	private static final String _FINDER_COLUMN_U_D_N_DOMAIN_2 =
		"cookiesConsentPreference.domain = ? AND ";

	private static final String _FINDER_COLUMN_U_D_N_DOMAIN_3 =
		"(cookiesConsentPreference.domain IS NULL OR cookiesConsentPreference.domain = '') AND ";

	private static final String _FINDER_COLUMN_U_D_N_NAME_2 =
		"cookiesConsentPreference.name = ?";

	private static final String _FINDER_COLUMN_U_D_N_NAME_3 =
		"(cookiesConsentPreference.name IS NULL OR cookiesConsentPreference.name = '')";

	public CookiesConsentPreferencePersistenceImpl() {
		setModelClass(CookiesConsentPreference.class);

		setModelImplClass(CookiesConsentPreferenceImpl.class);
		setModelPKClass(long.class);

		setTable(CookiesConsentPreferenceTable.INSTANCE);
	}

	/**
	 * Caches the cookies consent preference in the entity cache if it is enabled.
	 *
	 * @param cookiesConsentPreference the cookies consent preference
	 */
	@Override
	public void cacheResult(CookiesConsentPreference cookiesConsentPreference) {
		entityCache.putResult(
			CookiesConsentPreferenceImpl.class,
			cookiesConsentPreference.getPrimaryKey(), cookiesConsentPreference);

		finderCache.putResult(
			_finderPathFetchByU_D_N,
			new Object[] {
				cookiesConsentPreference.getUserId(),
				cookiesConsentPreference.getDomain(),
				cookiesConsentPreference.getName()
			},
			cookiesConsentPreference);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the cookies consent preferences in the entity cache if it is enabled.
	 *
	 * @param cookiesConsentPreferences the cookies consent preferences
	 */
	@Override
	public void cacheResult(
		List<CookiesConsentPreference> cookiesConsentPreferences) {

		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (cookiesConsentPreferences.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (CookiesConsentPreference cookiesConsentPreference :
				cookiesConsentPreferences) {

			if (entityCache.getResult(
					CookiesConsentPreferenceImpl.class,
					cookiesConsentPreference.getPrimaryKey()) == null) {

				cacheResult(cookiesConsentPreference);
			}
		}
	}

	/**
	 * Clears the cache for all cookies consent preferences.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(CookiesConsentPreferenceImpl.class);

		finderCache.clearCache(CookiesConsentPreferenceImpl.class);
	}

	/**
	 * Clears the cache for the cookies consent preference.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(CookiesConsentPreference cookiesConsentPreference) {
		entityCache.removeResult(
			CookiesConsentPreferenceImpl.class, cookiesConsentPreference);
	}

	@Override
	public void clearCache(
		List<CookiesConsentPreference> cookiesConsentPreferences) {

		for (CookiesConsentPreference cookiesConsentPreference :
				cookiesConsentPreferences) {

			entityCache.removeResult(
				CookiesConsentPreferenceImpl.class, cookiesConsentPreference);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(CookiesConsentPreferenceImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				CookiesConsentPreferenceImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		CookiesConsentPreferenceModelImpl cookiesConsentPreferenceModelImpl) {

		Object[] args = new Object[] {
			cookiesConsentPreferenceModelImpl.getUserId(),
			cookiesConsentPreferenceModelImpl.getDomain(),
			cookiesConsentPreferenceModelImpl.getName()
		};

		finderCache.putResult(
			_finderPathFetchByU_D_N, args, cookiesConsentPreferenceModelImpl);
	}

	/**
	 * Creates a new cookies consent preference with the primary key. Does not add the cookies consent preference to the database.
	 *
	 * @param cookiesConsentPreferenceId the primary key for the new cookies consent preference
	 * @return the new cookies consent preference
	 */
	@Override
	public CookiesConsentPreference create(long cookiesConsentPreferenceId) {
		CookiesConsentPreference cookiesConsentPreference =
			new CookiesConsentPreferenceImpl();

		cookiesConsentPreference.setNew(true);
		cookiesConsentPreference.setPrimaryKey(cookiesConsentPreferenceId);

		cookiesConsentPreference.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return cookiesConsentPreference;
	}

	/**
	 * Removes the cookies consent preference with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param cookiesConsentPreferenceId the primary key of the cookies consent preference
	 * @return the cookies consent preference that was removed
	 * @throws NoSuchCookiesConsentPreferenceException if a cookies consent preference with the primary key could not be found
	 */
	@Override
	public CookiesConsentPreference remove(long cookiesConsentPreferenceId)
		throws NoSuchCookiesConsentPreferenceException {

		return remove((Serializable)cookiesConsentPreferenceId);
	}

	/**
	 * Removes the cookies consent preference with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the cookies consent preference
	 * @return the cookies consent preference that was removed
	 * @throws NoSuchCookiesConsentPreferenceException if a cookies consent preference with the primary key could not be found
	 */
	@Override
	public CookiesConsentPreference remove(Serializable primaryKey)
		throws NoSuchCookiesConsentPreferenceException {

		Session session = null;

		try {
			session = openSession();

			CookiesConsentPreference cookiesConsentPreference =
				(CookiesConsentPreference)session.get(
					CookiesConsentPreferenceImpl.class, primaryKey);

			if (cookiesConsentPreference == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchCookiesConsentPreferenceException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(cookiesConsentPreference);
		}
		catch (NoSuchCookiesConsentPreferenceException noSuchEntityException) {
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
	protected CookiesConsentPreference removeImpl(
		CookiesConsentPreference cookiesConsentPreference) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(cookiesConsentPreference)) {
				cookiesConsentPreference =
					(CookiesConsentPreference)session.get(
						CookiesConsentPreferenceImpl.class,
						cookiesConsentPreference.getPrimaryKeyObj());
			}

			if (cookiesConsentPreference != null) {
				session.delete(cookiesConsentPreference);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (cookiesConsentPreference != null) {
			clearCache(cookiesConsentPreference);
		}

		return cookiesConsentPreference;
	}

	@Override
	public CookiesConsentPreference updateImpl(
		CookiesConsentPreference cookiesConsentPreference) {

		boolean isNew = cookiesConsentPreference.isNew();

		if (!(cookiesConsentPreference instanceof
				CookiesConsentPreferenceModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(cookiesConsentPreference.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					cookiesConsentPreference);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in cookiesConsentPreference proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CookiesConsentPreference implementation " +
					cookiesConsentPreference.getClass());
		}

		CookiesConsentPreferenceModelImpl cookiesConsentPreferenceModelImpl =
			(CookiesConsentPreferenceModelImpl)cookiesConsentPreference;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(cookiesConsentPreference);
			}
			else {
				cookiesConsentPreference =
					(CookiesConsentPreference)session.merge(
						cookiesConsentPreference);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			CookiesConsentPreferenceImpl.class,
			cookiesConsentPreferenceModelImpl, false, true);

		cacheUniqueFindersCache(cookiesConsentPreferenceModelImpl);

		if (isNew) {
			cookiesConsentPreference.setNew(false);
		}

		cookiesConsentPreference.resetOriginalValues();

		return cookiesConsentPreference;
	}

	/**
	 * Returns the cookies consent preference with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the cookies consent preference
	 * @return the cookies consent preference
	 * @throws NoSuchCookiesConsentPreferenceException if a cookies consent preference with the primary key could not be found
	 */
	@Override
	public CookiesConsentPreference findByPrimaryKey(Serializable primaryKey)
		throws NoSuchCookiesConsentPreferenceException {

		CookiesConsentPreference cookiesConsentPreference = fetchByPrimaryKey(
			primaryKey);

		if (cookiesConsentPreference == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchCookiesConsentPreferenceException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return cookiesConsentPreference;
	}

	/**
	 * Returns the cookies consent preference with the primary key or throws a <code>NoSuchCookiesConsentPreferenceException</code> if it could not be found.
	 *
	 * @param cookiesConsentPreferenceId the primary key of the cookies consent preference
	 * @return the cookies consent preference
	 * @throws NoSuchCookiesConsentPreferenceException if a cookies consent preference with the primary key could not be found
	 */
	@Override
	public CookiesConsentPreference findByPrimaryKey(
			long cookiesConsentPreferenceId)
		throws NoSuchCookiesConsentPreferenceException {

		return findByPrimaryKey((Serializable)cookiesConsentPreferenceId);
	}

	/**
	 * Returns the cookies consent preference with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param cookiesConsentPreferenceId the primary key of the cookies consent preference
	 * @return the cookies consent preference, or <code>null</code> if a cookies consent preference with the primary key could not be found
	 */
	@Override
	public CookiesConsentPreference fetchByPrimaryKey(
		long cookiesConsentPreferenceId) {

		return fetchByPrimaryKey((Serializable)cookiesConsentPreferenceId);
	}

	/**
	 * Returns all the cookies consent preferences.
	 *
	 * @return the cookies consent preferences
	 */
	@Override
	public List<CookiesConsentPreference> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cookies consent preferences.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CookiesConsentPreferenceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cookies consent preferences
	 * @param end the upper bound of the range of cookies consent preferences (not inclusive)
	 * @return the range of cookies consent preferences
	 */
	@Override
	public List<CookiesConsentPreference> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the cookies consent preferences.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CookiesConsentPreferenceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cookies consent preferences
	 * @param end the upper bound of the range of cookies consent preferences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of cookies consent preferences
	 */
	@Override
	public List<CookiesConsentPreference> findAll(
		int start, int end,
		OrderByComparator<CookiesConsentPreference> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cookies consent preferences.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CookiesConsentPreferenceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cookies consent preferences
	 * @param end the upper bound of the range of cookies consent preferences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of cookies consent preferences
	 */
	@Override
	public List<CookiesConsentPreference> findAll(
		int start, int end,
		OrderByComparator<CookiesConsentPreference> orderByComparator,
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

		List<CookiesConsentPreference> list = null;

		if (useFinderCache) {
			list = (List<CookiesConsentPreference>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_COOKIESCONSENTPREFERENCE);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_COOKIESCONSENTPREFERENCE;

				sql = sql.concat(
					CookiesConsentPreferenceModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<CookiesConsentPreference>)QueryUtil.list(
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
	 * Removes all the cookies consent preferences from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (CookiesConsentPreference cookiesConsentPreference : findAll()) {
			remove(cookiesConsentPreference);
		}
	}

	/**
	 * Returns the number of cookies consent preferences.
	 *
	 * @return the number of cookies consent preferences
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
					_SQL_COUNT_COOKIESCONSENTPREFERENCE);

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
		return "cookiesConsentPreferenceId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COOKIESCONSENTPREFERENCE;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CookiesConsentPreferenceModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the cookies consent preference persistence.
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

		_finderPathWithPaginationFindByExpirationDate = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByExpirationDate",
			new String[] {
				Date.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"expirationDate"}, true);

		_finderPathWithoutPaginationFindByExpirationDate = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByExpirationDate",
			new String[] {Date.class.getName()},
			new String[] {"expirationDate"}, true);

		_finderPathCountByExpirationDate = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByExpirationDate",
			new String[] {Date.class.getName()},
			new String[] {"expirationDate"}, false);

		_finderPathWithPaginationFindByU_D = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_D",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"userId", "domain"}, true);

		_finderPathWithoutPaginationFindByU_D = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_D",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"userId", "domain"}, true);

		_finderPathCountByU_D = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByU_D",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"userId", "domain"}, false);

		_finderPathFetchByU_D_N = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByU_D_N",
			new String[] {
				Long.class.getName(), String.class.getName(),
				String.class.getName()
			},
			new String[] {"userId", "domain", "name"}, true);

		CookiesConsentPreferenceUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CookiesConsentPreferenceUtil.setPersistence(null);

		entityCache.removeCache(CookiesConsentPreferenceImpl.class.getName());
	}

	@Override
	@Reference(
		target = CookiesPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CookiesPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CookiesPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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

	private static final String _SQL_SELECT_COOKIESCONSENTPREFERENCE =
		"SELECT cookiesConsentPreference FROM CookiesConsentPreference cookiesConsentPreference";

	private static final String _SQL_SELECT_COOKIESCONSENTPREFERENCE_WHERE =
		"SELECT cookiesConsentPreference FROM CookiesConsentPreference cookiesConsentPreference WHERE ";

	private static final String _SQL_COUNT_COOKIESCONSENTPREFERENCE =
		"SELECT COUNT(cookiesConsentPreference) FROM CookiesConsentPreference cookiesConsentPreference";

	private static final String _SQL_COUNT_COOKIESCONSENTPREFERENCE_WHERE =
		"SELECT COUNT(cookiesConsentPreference) FROM CookiesConsentPreference cookiesConsentPreference WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS =
		"cookiesConsentPreference.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No CookiesConsentPreference exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CookiesConsentPreference exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CookiesConsentPreferencePersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1548955799