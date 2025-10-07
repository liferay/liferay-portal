/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service.persistence.impl;

import com.liferay.account.exception.DuplicateAccountRoleExternalReferenceCodeException;
import com.liferay.account.exception.NoSuchRoleException;
import com.liferay.account.model.AccountRole;
import com.liferay.account.model.AccountRoleTable;
import com.liferay.account.model.impl.AccountRoleImpl;
import com.liferay.account.model.impl.AccountRoleModelImpl;
import com.liferay.account.service.persistence.AccountRolePersistence;
import com.liferay.account.service.persistence.AccountRoleUtil;
import com.liferay.account.service.persistence.impl.constants.AccountPersistenceConstants;
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
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Collections;
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
 * The persistence implementation for the account role service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = AccountRolePersistence.class)
public class AccountRolePersistenceImpl
	extends BasePersistenceImpl<AccountRole> implements AccountRolePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>AccountRoleUtil</code> to access the account role persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		AccountRoleImpl.class.getName();

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
	 * Returns all the account roles where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching account roles
	 */
	@Override
	public List<AccountRole> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the account roles where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @return the range of matching account roles
	 */
	@Override
	public List<AccountRole> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the account roles where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching account roles
	 */
	@Override
	public List<AccountRole> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<AccountRole> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the account roles where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching account roles
	 */
	@Override
	public List<AccountRole> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<AccountRole> orderByComparator,
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

		List<AccountRole> list = null;

		if (useFinderCache) {
			list = (List<AccountRole>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (AccountRole accountRole : list) {
					if (companyId != accountRole.getCompanyId()) {
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

			sb.append(_SQL_SELECT_ACCOUNTROLE_WHERE);

			sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(AccountRoleModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				list = (List<AccountRole>)QueryUtil.list(
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
	 * Returns the first account role in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account role
	 * @throws NoSuchRoleException if a matching account role could not be found
	 */
	@Override
	public AccountRole findByCompanyId_First(
			long companyId, OrderByComparator<AccountRole> orderByComparator)
		throws NoSuchRoleException {

		AccountRole accountRole = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (accountRole != null) {
			return accountRole;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchRoleException(sb.toString());
	}

	/**
	 * Returns the first account role in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account role, or <code>null</code> if a matching account role could not be found
	 */
	@Override
	public AccountRole fetchByCompanyId_First(
		long companyId, OrderByComparator<AccountRole> orderByComparator) {

		List<AccountRole> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last account role in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching account role
	 * @throws NoSuchRoleException if a matching account role could not be found
	 */
	@Override
	public AccountRole findByCompanyId_Last(
			long companyId, OrderByComparator<AccountRole> orderByComparator)
		throws NoSuchRoleException {

		AccountRole accountRole = fetchByCompanyId_Last(
			companyId, orderByComparator);

		if (accountRole != null) {
			return accountRole;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchRoleException(sb.toString());
	}

	/**
	 * Returns the last account role in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching account role, or <code>null</code> if a matching account role could not be found
	 */
	@Override
	public AccountRole fetchByCompanyId_Last(
		long companyId, OrderByComparator<AccountRole> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<AccountRole> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the account roles before and after the current account role in the ordered set where companyId = &#63;.
	 *
	 * @param accountRoleId the primary key of the current account role
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next account role
	 * @throws NoSuchRoleException if a account role with the primary key could not be found
	 */
	@Override
	public AccountRole[] findByCompanyId_PrevAndNext(
			long accountRoleId, long companyId,
			OrderByComparator<AccountRole> orderByComparator)
		throws NoSuchRoleException {

		AccountRole accountRole = findByPrimaryKey(accountRoleId);

		Session session = null;

		try {
			session = openSession();

			AccountRole[] array = new AccountRoleImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, accountRole, companyId, orderByComparator, true);

			array[1] = accountRole;

			array[2] = getByCompanyId_PrevAndNext(
				session, accountRole, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected AccountRole getByCompanyId_PrevAndNext(
		Session session, AccountRole accountRole, long companyId,
		OrderByComparator<AccountRole> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_ACCOUNTROLE_WHERE);

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
			sb.append(AccountRoleModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(accountRole)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<AccountRole> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the account roles that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching account roles that the user has permission to view
	 */
	@Override
	public List<AccountRole> filterFindByCompanyId(long companyId) {
		return filterFindByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the account roles that the user has permission to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @return the range of matching account roles that the user has permission to view
	 */
	@Override
	public List<AccountRole> filterFindByCompanyId(
		long companyId, int start, int end) {

		return filterFindByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the account roles that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching account roles that the user has permission to view
	 */
	@Override
	public List<AccountRole> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<AccountRole> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_ACCOUNTROLE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_ACCOUNTROLE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_ACCOUNTROLE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(AccountRoleModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(AccountRoleModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AccountRole.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, AccountRoleImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, AccountRoleImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			return (List<AccountRole>)QueryUtil.list(
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
	 * Returns the account roles before and after the current account role in the ordered set of account roles that the user has permission to view where companyId = &#63;.
	 *
	 * @param accountRoleId the primary key of the current account role
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next account role
	 * @throws NoSuchRoleException if a account role with the primary key could not be found
	 */
	@Override
	public AccountRole[] filterFindByCompanyId_PrevAndNext(
			long accountRoleId, long companyId,
			OrderByComparator<AccountRole> orderByComparator)
		throws NoSuchRoleException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByCompanyId_PrevAndNext(
				accountRoleId, companyId, orderByComparator);
		}

		AccountRole accountRole = findByPrimaryKey(accountRoleId);

		Session session = null;

		try {
			session = openSession();

			AccountRole[] array = new AccountRoleImpl[3];

			array[0] = filterGetByCompanyId_PrevAndNext(
				session, accountRole, companyId, orderByComparator, true);

			array[1] = accountRole;

			array[2] = filterGetByCompanyId_PrevAndNext(
				session, accountRole, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected AccountRole filterGetByCompanyId_PrevAndNext(
		Session session, AccountRole accountRole, long companyId,
		OrderByComparator<AccountRole> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_ACCOUNTROLE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_ACCOUNTROLE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_ACCOUNTROLE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(AccountRoleModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(AccountRoleModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AccountRole.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, AccountRoleImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, AccountRoleImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(accountRole)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<AccountRole> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the account roles where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (AccountRole accountRole :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(accountRole);
		}
	}

	/**
	 * Returns the number of account roles where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching account roles
	 */
	@Override
	public int countByCompanyId(long companyId) {
		FinderPath finderPath = _finderPathCountByCompanyId;

		Object[] finderArgs = new Object[] {companyId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_ACCOUNTROLE_WHERE);

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
	 * Returns the number of account roles that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching account roles that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByCompanyId(companyId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<AccountRole> accountRoles = findByCompanyId(companyId);

			accountRoles = InlineSQLHelperUtil.filter(accountRoles);

			return accountRoles.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_ACCOUNTROLE_WHERE);

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AccountRole.class.getName(),
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
		"accountRole.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByAccountEntryId;
	private FinderPath _finderPathWithoutPaginationFindByAccountEntryId;
	private FinderPath _finderPathCountByAccountEntryId;
	private FinderPath _finderPathWithPaginationCountByAccountEntryId;

	/**
	 * Returns all the account roles where accountEntryId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @return the matching account roles
	 */
	@Override
	public List<AccountRole> findByAccountEntryId(long accountEntryId) {
		return findByAccountEntryId(
			accountEntryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the account roles where accountEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryId the account entry ID
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @return the range of matching account roles
	 */
	@Override
	public List<AccountRole> findByAccountEntryId(
		long accountEntryId, int start, int end) {

		return findByAccountEntryId(accountEntryId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the account roles where accountEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryId the account entry ID
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching account roles
	 */
	@Override
	public List<AccountRole> findByAccountEntryId(
		long accountEntryId, int start, int end,
		OrderByComparator<AccountRole> orderByComparator) {

		return findByAccountEntryId(
			accountEntryId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the account roles where accountEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryId the account entry ID
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching account roles
	 */
	@Override
	public List<AccountRole> findByAccountEntryId(
		long accountEntryId, int start, int end,
		OrderByComparator<AccountRole> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByAccountEntryId;
				finderArgs = new Object[] {accountEntryId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByAccountEntryId;
			finderArgs = new Object[] {
				accountEntryId, start, end, orderByComparator
			};
		}

		List<AccountRole> list = null;

		if (useFinderCache) {
			list = (List<AccountRole>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (AccountRole accountRole : list) {
					if (accountEntryId != accountRole.getAccountEntryId()) {
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

			sb.append(_SQL_SELECT_ACCOUNTROLE_WHERE);

			sb.append(_FINDER_COLUMN_ACCOUNTENTRYID_ACCOUNTENTRYID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(AccountRoleModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(accountEntryId);

				list = (List<AccountRole>)QueryUtil.list(
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
	 * Returns the first account role in the ordered set where accountEntryId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account role
	 * @throws NoSuchRoleException if a matching account role could not be found
	 */
	@Override
	public AccountRole findByAccountEntryId_First(
			long accountEntryId,
			OrderByComparator<AccountRole> orderByComparator)
		throws NoSuchRoleException {

		AccountRole accountRole = fetchByAccountEntryId_First(
			accountEntryId, orderByComparator);

		if (accountRole != null) {
			return accountRole;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("accountEntryId=");
		sb.append(accountEntryId);

		sb.append("}");

		throw new NoSuchRoleException(sb.toString());
	}

	/**
	 * Returns the first account role in the ordered set where accountEntryId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account role, or <code>null</code> if a matching account role could not be found
	 */
	@Override
	public AccountRole fetchByAccountEntryId_First(
		long accountEntryId, OrderByComparator<AccountRole> orderByComparator) {

		List<AccountRole> list = findByAccountEntryId(
			accountEntryId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last account role in the ordered set where accountEntryId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching account role
	 * @throws NoSuchRoleException if a matching account role could not be found
	 */
	@Override
	public AccountRole findByAccountEntryId_Last(
			long accountEntryId,
			OrderByComparator<AccountRole> orderByComparator)
		throws NoSuchRoleException {

		AccountRole accountRole = fetchByAccountEntryId_Last(
			accountEntryId, orderByComparator);

		if (accountRole != null) {
			return accountRole;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("accountEntryId=");
		sb.append(accountEntryId);

		sb.append("}");

		throw new NoSuchRoleException(sb.toString());
	}

	/**
	 * Returns the last account role in the ordered set where accountEntryId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching account role, or <code>null</code> if a matching account role could not be found
	 */
	@Override
	public AccountRole fetchByAccountEntryId_Last(
		long accountEntryId, OrderByComparator<AccountRole> orderByComparator) {

		int count = countByAccountEntryId(accountEntryId);

		if (count == 0) {
			return null;
		}

		List<AccountRole> list = findByAccountEntryId(
			accountEntryId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the account roles before and after the current account role in the ordered set where accountEntryId = &#63;.
	 *
	 * @param accountRoleId the primary key of the current account role
	 * @param accountEntryId the account entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next account role
	 * @throws NoSuchRoleException if a account role with the primary key could not be found
	 */
	@Override
	public AccountRole[] findByAccountEntryId_PrevAndNext(
			long accountRoleId, long accountEntryId,
			OrderByComparator<AccountRole> orderByComparator)
		throws NoSuchRoleException {

		AccountRole accountRole = findByPrimaryKey(accountRoleId);

		Session session = null;

		try {
			session = openSession();

			AccountRole[] array = new AccountRoleImpl[3];

			array[0] = getByAccountEntryId_PrevAndNext(
				session, accountRole, accountEntryId, orderByComparator, true);

			array[1] = accountRole;

			array[2] = getByAccountEntryId_PrevAndNext(
				session, accountRole, accountEntryId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected AccountRole getByAccountEntryId_PrevAndNext(
		Session session, AccountRole accountRole, long accountEntryId,
		OrderByComparator<AccountRole> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_ACCOUNTROLE_WHERE);

		sb.append(_FINDER_COLUMN_ACCOUNTENTRYID_ACCOUNTENTRYID_2);

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
			sb.append(AccountRoleModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(accountEntryId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(accountRole)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<AccountRole> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the account roles that the user has permission to view where accountEntryId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @return the matching account roles that the user has permission to view
	 */
	@Override
	public List<AccountRole> filterFindByAccountEntryId(long accountEntryId) {
		return filterFindByAccountEntryId(
			accountEntryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the account roles that the user has permission to view where accountEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryId the account entry ID
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @return the range of matching account roles that the user has permission to view
	 */
	@Override
	public List<AccountRole> filterFindByAccountEntryId(
		long accountEntryId, int start, int end) {

		return filterFindByAccountEntryId(accountEntryId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the account roles that the user has permissions to view where accountEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryId the account entry ID
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching account roles that the user has permission to view
	 */
	@Override
	public List<AccountRole> filterFindByAccountEntryId(
		long accountEntryId, int start, int end,
		OrderByComparator<AccountRole> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByAccountEntryId(
				accountEntryId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByAccountEntryId(
					accountEntryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_ACCOUNTROLE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_ACCOUNTROLE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_ACCOUNTENTRYID_ACCOUNTENTRYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_ACCOUNTROLE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(AccountRoleModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(AccountRoleModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AccountRole.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, AccountRoleImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, AccountRoleImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(accountEntryId);

			return (List<AccountRole>)QueryUtil.list(
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
	 * Returns the account roles before and after the current account role in the ordered set of account roles that the user has permission to view where accountEntryId = &#63;.
	 *
	 * @param accountRoleId the primary key of the current account role
	 * @param accountEntryId the account entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next account role
	 * @throws NoSuchRoleException if a account role with the primary key could not be found
	 */
	@Override
	public AccountRole[] filterFindByAccountEntryId_PrevAndNext(
			long accountRoleId, long accountEntryId,
			OrderByComparator<AccountRole> orderByComparator)
		throws NoSuchRoleException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByAccountEntryId_PrevAndNext(
				accountRoleId, accountEntryId, orderByComparator);
		}

		AccountRole accountRole = findByPrimaryKey(accountRoleId);

		Session session = null;

		try {
			session = openSession();

			AccountRole[] array = new AccountRoleImpl[3];

			array[0] = filterGetByAccountEntryId_PrevAndNext(
				session, accountRole, accountEntryId, orderByComparator, true);

			array[1] = accountRole;

			array[2] = filterGetByAccountEntryId_PrevAndNext(
				session, accountRole, accountEntryId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected AccountRole filterGetByAccountEntryId_PrevAndNext(
		Session session, AccountRole accountRole, long accountEntryId,
		OrderByComparator<AccountRole> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_ACCOUNTROLE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_ACCOUNTROLE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_ACCOUNTENTRYID_ACCOUNTENTRYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_ACCOUNTROLE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(AccountRoleModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(AccountRoleModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AccountRole.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, AccountRoleImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, AccountRoleImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(accountEntryId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(accountRole)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<AccountRole> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the account roles that the user has permission to view where accountEntryId = any &#63;.
	 *
	 * @param accountEntryIds the account entry IDs
	 * @return the matching account roles that the user has permission to view
	 */
	@Override
	public List<AccountRole> filterFindByAccountEntryId(
		long[] accountEntryIds) {

		return filterFindByAccountEntryId(
			accountEntryIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the account roles that the user has permission to view where accountEntryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryIds the account entry IDs
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @return the range of matching account roles that the user has permission to view
	 */
	@Override
	public List<AccountRole> filterFindByAccountEntryId(
		long[] accountEntryIds, int start, int end) {

		return filterFindByAccountEntryId(accountEntryIds, start, end, null);
	}

	/**
	 * Returns an ordered range of all the account roles that the user has permission to view where accountEntryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryIds the account entry IDs
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching account roles that the user has permission to view
	 */
	@Override
	public List<AccountRole> filterFindByAccountEntryId(
		long[] accountEntryIds, int start, int end,
		OrderByComparator<AccountRole> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByAccountEntryId(
				accountEntryIds, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByAccountEntryId(
					accountEntryIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator));
		}

		if (accountEntryIds == null) {
			accountEntryIds = new long[0];
		}
		else if (accountEntryIds.length > 1) {
			accountEntryIds = ArrayUtil.sortedUnique(accountEntryIds);
		}

		StringBundler sb = new StringBundler();

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_ACCOUNTROLE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_ACCOUNTROLE_NO_INLINE_DISTINCT_WHERE_1);
		}

		if (accountEntryIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_ACCOUNTENTRYID_ACCOUNTENTRYID_7);

			sb.append(StringUtil.merge(accountEntryIds));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_ACCOUNTROLE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(AccountRoleModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(AccountRoleModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AccountRole.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, AccountRoleImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, AccountRoleImpl.class);
			}

			return (List<AccountRole>)QueryUtil.list(
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
	 * Returns all the account roles where accountEntryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryIds the account entry IDs
	 * @return the matching account roles
	 */
	@Override
	public List<AccountRole> findByAccountEntryId(long[] accountEntryIds) {
		return findByAccountEntryId(
			accountEntryIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the account roles where accountEntryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryIds the account entry IDs
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @return the range of matching account roles
	 */
	@Override
	public List<AccountRole> findByAccountEntryId(
		long[] accountEntryIds, int start, int end) {

		return findByAccountEntryId(accountEntryIds, start, end, null);
	}

	/**
	 * Returns an ordered range of all the account roles where accountEntryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryIds the account entry IDs
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching account roles
	 */
	@Override
	public List<AccountRole> findByAccountEntryId(
		long[] accountEntryIds, int start, int end,
		OrderByComparator<AccountRole> orderByComparator) {

		return findByAccountEntryId(
			accountEntryIds, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the account roles where accountEntryId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryIds the account entry IDs
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching account roles
	 */
	@Override
	public List<AccountRole> findByAccountEntryId(
		long[] accountEntryIds, int start, int end,
		OrderByComparator<AccountRole> orderByComparator,
		boolean useFinderCache) {

		if (accountEntryIds == null) {
			accountEntryIds = new long[0];
		}
		else if (accountEntryIds.length > 1) {
			accountEntryIds = ArrayUtil.sortedUnique(accountEntryIds);
		}

		if (accountEntryIds.length == 1) {
			return findByAccountEntryId(
				accountEntryIds[0], start, end, orderByComparator);
		}

		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderArgs = new Object[] {StringUtil.merge(accountEntryIds)};
			}
		}
		else if (useFinderCache) {
			finderArgs = new Object[] {
				StringUtil.merge(accountEntryIds), start, end, orderByComparator
			};
		}

		List<AccountRole> list = null;

		if (useFinderCache) {
			list = (List<AccountRole>)finderCache.getResult(
				_finderPathWithPaginationFindByAccountEntryId, finderArgs,
				this);

			if ((list != null) && !list.isEmpty()) {
				for (AccountRole accountRole : list) {
					if (!ArrayUtil.contains(
							accountEntryIds, accountRole.getAccountEntryId())) {

						list = null;

						break;
					}
				}
			}
		}

		if (list == null) {
			StringBundler sb = new StringBundler();

			sb.append(_SQL_SELECT_ACCOUNTROLE_WHERE);

			if (accountEntryIds.length > 0) {
				sb.append("(");

				sb.append(_FINDER_COLUMN_ACCOUNTENTRYID_ACCOUNTENTRYID_7);

				sb.append(StringUtil.merge(accountEntryIds));

				sb.append(")");

				sb.append(")");
			}

			sb.setStringAt(
				removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(AccountRoleModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<AccountRole>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(
						_finderPathWithPaginationFindByAccountEntryId,
						finderArgs, list);
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
	 * Removes all the account roles where accountEntryId = &#63; from the database.
	 *
	 * @param accountEntryId the account entry ID
	 */
	@Override
	public void removeByAccountEntryId(long accountEntryId) {
		for (AccountRole accountRole :
				findByAccountEntryId(
					accountEntryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(accountRole);
		}
	}

	/**
	 * Returns the number of account roles where accountEntryId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @return the number of matching account roles
	 */
	@Override
	public int countByAccountEntryId(long accountEntryId) {
		FinderPath finderPath = _finderPathCountByAccountEntryId;

		Object[] finderArgs = new Object[] {accountEntryId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_ACCOUNTROLE_WHERE);

			sb.append(_FINDER_COLUMN_ACCOUNTENTRYID_ACCOUNTENTRYID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(accountEntryId);

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
	 * Returns the number of account roles where accountEntryId = any &#63;.
	 *
	 * @param accountEntryIds the account entry IDs
	 * @return the number of matching account roles
	 */
	@Override
	public int countByAccountEntryId(long[] accountEntryIds) {
		if (accountEntryIds == null) {
			accountEntryIds = new long[0];
		}
		else if (accountEntryIds.length > 1) {
			accountEntryIds = ArrayUtil.sortedUnique(accountEntryIds);
		}

		Object[] finderArgs = new Object[] {StringUtil.merge(accountEntryIds)};

		Long count = (Long)finderCache.getResult(
			_finderPathWithPaginationCountByAccountEntryId, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler();

			sb.append(_SQL_COUNT_ACCOUNTROLE_WHERE);

			if (accountEntryIds.length > 0) {
				sb.append("(");

				sb.append(_FINDER_COLUMN_ACCOUNTENTRYID_ACCOUNTENTRYID_7);

				sb.append(StringUtil.merge(accountEntryIds));

				sb.append(")");

				sb.append(")");
			}

			sb.setStringAt(
				removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				count = (Long)query.uniqueResult();

				finderCache.putResult(
					_finderPathWithPaginationCountByAccountEntryId, finderArgs,
					count);
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
	 * Returns the number of account roles that the user has permission to view where accountEntryId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @return the number of matching account roles that the user has permission to view
	 */
	@Override
	public int filterCountByAccountEntryId(long accountEntryId) {
		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByAccountEntryId(accountEntryId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<AccountRole> accountRoles = findByAccountEntryId(
				accountEntryId);

			accountRoles = InlineSQLHelperUtil.filter(accountRoles);

			return accountRoles.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_ACCOUNTROLE_WHERE);

		sb.append(_FINDER_COLUMN_ACCOUNTENTRYID_ACCOUNTENTRYID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AccountRole.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(accountEntryId);

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

	/**
	 * Returns the number of account roles that the user has permission to view where accountEntryId = any &#63;.
	 *
	 * @param accountEntryIds the account entry IDs
	 * @return the number of matching account roles that the user has permission to view
	 */
	@Override
	public int filterCountByAccountEntryId(long[] accountEntryIds) {
		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByAccountEntryId(accountEntryIds);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<AccountRole> accountRoles = InlineSQLHelperUtil.filter(
				findByAccountEntryId(accountEntryIds));

			return accountRoles.size();
		}

		if (accountEntryIds == null) {
			accountEntryIds = new long[0];
		}
		else if (accountEntryIds.length > 1) {
			accountEntryIds = ArrayUtil.sortedUnique(accountEntryIds);
		}

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_ACCOUNTROLE_WHERE);

		if (accountEntryIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_ACCOUNTENTRYID_ACCOUNTENTRYID_7);

			sb.append(StringUtil.merge(accountEntryIds));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AccountRole.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

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

	private static final String _FINDER_COLUMN_ACCOUNTENTRYID_ACCOUNTENTRYID_2 =
		"accountRole.accountEntryId = ?";

	private static final String _FINDER_COLUMN_ACCOUNTENTRYID_ACCOUNTENTRYID_7 =
		"accountRole.accountEntryId IN (";

	private FinderPath _finderPathFetchByRoleId;

	/**
	 * Returns the account role where roleId = &#63; or throws a <code>NoSuchRoleException</code> if it could not be found.
	 *
	 * @param roleId the role ID
	 * @return the matching account role
	 * @throws NoSuchRoleException if a matching account role could not be found
	 */
	@Override
	public AccountRole findByRoleId(long roleId) throws NoSuchRoleException {
		AccountRole accountRole = fetchByRoleId(roleId);

		if (accountRole == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("roleId=");
			sb.append(roleId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchRoleException(sb.toString());
		}

		return accountRole;
	}

	/**
	 * Returns the account role where roleId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param roleId the role ID
	 * @return the matching account role, or <code>null</code> if a matching account role could not be found
	 */
	@Override
	public AccountRole fetchByRoleId(long roleId) {
		return fetchByRoleId(roleId, true);
	}

	/**
	 * Returns the account role where roleId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param roleId the role ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching account role, or <code>null</code> if a matching account role could not be found
	 */
	@Override
	public AccountRole fetchByRoleId(long roleId, boolean useFinderCache) {
		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {roleId};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByRoleId, finderArgs, this);
		}

		if (result instanceof AccountRole) {
			AccountRole accountRole = (AccountRole)result;

			if (roleId != accountRole.getRoleId()) {
				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_SELECT_ACCOUNTROLE_WHERE);

			sb.append(_FINDER_COLUMN_ROLEID_ROLEID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(roleId);

				List<AccountRole> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByRoleId, finderArgs, list);
					}
				}
				else {
					if (list.size() > 1) {
						Collections.sort(list, Collections.reverseOrder());

						if (_log.isWarnEnabled()) {
							if (!useFinderCache) {
								finderArgs = new Object[] {roleId};
							}

							_log.warn(
								"AccountRolePersistenceImpl.fetchByRoleId(long, boolean) with parameters (" +
									StringUtil.merge(finderArgs) +
										") yields a result set with more than 1 result. This violates the logical unique restriction. There is no order guarantee on which result is returned by this finder.");
						}
					}

					AccountRole accountRole = list.get(0);

					result = accountRole;

					cacheResult(accountRole);
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
			return (AccountRole)result;
		}
	}

	/**
	 * Removes the account role where roleId = &#63; from the database.
	 *
	 * @param roleId the role ID
	 * @return the account role that was removed
	 */
	@Override
	public AccountRole removeByRoleId(long roleId) throws NoSuchRoleException {
		AccountRole accountRole = findByRoleId(roleId);

		return remove(accountRole);
	}

	/**
	 * Returns the number of account roles where roleId = &#63;.
	 *
	 * @param roleId the role ID
	 * @return the number of matching account roles
	 */
	@Override
	public int countByRoleId(long roleId) {
		AccountRole accountRole = fetchByRoleId(roleId);

		if (accountRole == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ROLEID_ROLEID_2 =
		"accountRole.roleId = ?";

	private FinderPath _finderPathWithPaginationFindByC_A;
	private FinderPath _finderPathWithoutPaginationFindByC_A;
	private FinderPath _finderPathCountByC_A;
	private FinderPath _finderPathWithPaginationCountByC_A;

	/**
	 * Returns all the account roles where companyId = &#63; and accountEntryId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryId the account entry ID
	 * @return the matching account roles
	 */
	@Override
	public List<AccountRole> findByC_A(long companyId, long accountEntryId) {
		return findByC_A(
			companyId, accountEntryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the account roles where companyId = &#63; and accountEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accountEntryId the account entry ID
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @return the range of matching account roles
	 */
	@Override
	public List<AccountRole> findByC_A(
		long companyId, long accountEntryId, int start, int end) {

		return findByC_A(companyId, accountEntryId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the account roles where companyId = &#63; and accountEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accountEntryId the account entry ID
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching account roles
	 */
	@Override
	public List<AccountRole> findByC_A(
		long companyId, long accountEntryId, int start, int end,
		OrderByComparator<AccountRole> orderByComparator) {

		return findByC_A(
			companyId, accountEntryId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the account roles where companyId = &#63; and accountEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accountEntryId the account entry ID
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching account roles
	 */
	@Override
	public List<AccountRole> findByC_A(
		long companyId, long accountEntryId, int start, int end,
		OrderByComparator<AccountRole> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByC_A;
				finderArgs = new Object[] {companyId, accountEntryId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByC_A;
			finderArgs = new Object[] {
				companyId, accountEntryId, start, end, orderByComparator
			};
		}

		List<AccountRole> list = null;

		if (useFinderCache) {
			list = (List<AccountRole>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (AccountRole accountRole : list) {
					if ((companyId != accountRole.getCompanyId()) ||
						(accountEntryId != accountRole.getAccountEntryId())) {

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

			sb.append(_SQL_SELECT_ACCOUNTROLE_WHERE);

			sb.append(_FINDER_COLUMN_C_A_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_A_ACCOUNTENTRYID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(AccountRoleModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(accountEntryId);

				list = (List<AccountRole>)QueryUtil.list(
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
	 * Returns the first account role in the ordered set where companyId = &#63; and accountEntryId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryId the account entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account role
	 * @throws NoSuchRoleException if a matching account role could not be found
	 */
	@Override
	public AccountRole findByC_A_First(
			long companyId, long accountEntryId,
			OrderByComparator<AccountRole> orderByComparator)
		throws NoSuchRoleException {

		AccountRole accountRole = fetchByC_A_First(
			companyId, accountEntryId, orderByComparator);

		if (accountRole != null) {
			return accountRole;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", accountEntryId=");
		sb.append(accountEntryId);

		sb.append("}");

		throw new NoSuchRoleException(sb.toString());
	}

	/**
	 * Returns the first account role in the ordered set where companyId = &#63; and accountEntryId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryId the account entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account role, or <code>null</code> if a matching account role could not be found
	 */
	@Override
	public AccountRole fetchByC_A_First(
		long companyId, long accountEntryId,
		OrderByComparator<AccountRole> orderByComparator) {

		List<AccountRole> list = findByC_A(
			companyId, accountEntryId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last account role in the ordered set where companyId = &#63; and accountEntryId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryId the account entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching account role
	 * @throws NoSuchRoleException if a matching account role could not be found
	 */
	@Override
	public AccountRole findByC_A_Last(
			long companyId, long accountEntryId,
			OrderByComparator<AccountRole> orderByComparator)
		throws NoSuchRoleException {

		AccountRole accountRole = fetchByC_A_Last(
			companyId, accountEntryId, orderByComparator);

		if (accountRole != null) {
			return accountRole;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", accountEntryId=");
		sb.append(accountEntryId);

		sb.append("}");

		throw new NoSuchRoleException(sb.toString());
	}

	/**
	 * Returns the last account role in the ordered set where companyId = &#63; and accountEntryId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryId the account entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching account role, or <code>null</code> if a matching account role could not be found
	 */
	@Override
	public AccountRole fetchByC_A_Last(
		long companyId, long accountEntryId,
		OrderByComparator<AccountRole> orderByComparator) {

		int count = countByC_A(companyId, accountEntryId);

		if (count == 0) {
			return null;
		}

		List<AccountRole> list = findByC_A(
			companyId, accountEntryId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the account roles before and after the current account role in the ordered set where companyId = &#63; and accountEntryId = &#63;.
	 *
	 * @param accountRoleId the primary key of the current account role
	 * @param companyId the company ID
	 * @param accountEntryId the account entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next account role
	 * @throws NoSuchRoleException if a account role with the primary key could not be found
	 */
	@Override
	public AccountRole[] findByC_A_PrevAndNext(
			long accountRoleId, long companyId, long accountEntryId,
			OrderByComparator<AccountRole> orderByComparator)
		throws NoSuchRoleException {

		AccountRole accountRole = findByPrimaryKey(accountRoleId);

		Session session = null;

		try {
			session = openSession();

			AccountRole[] array = new AccountRoleImpl[3];

			array[0] = getByC_A_PrevAndNext(
				session, accountRole, companyId, accountEntryId,
				orderByComparator, true);

			array[1] = accountRole;

			array[2] = getByC_A_PrevAndNext(
				session, accountRole, companyId, accountEntryId,
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

	protected AccountRole getByC_A_PrevAndNext(
		Session session, AccountRole accountRole, long companyId,
		long accountEntryId, OrderByComparator<AccountRole> orderByComparator,
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

		sb.append(_SQL_SELECT_ACCOUNTROLE_WHERE);

		sb.append(_FINDER_COLUMN_C_A_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_A_ACCOUNTENTRYID_2);

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
			sb.append(AccountRoleModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(accountEntryId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(accountRole)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<AccountRole> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the account roles that the user has permission to view where companyId = &#63; and accountEntryId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryId the account entry ID
	 * @return the matching account roles that the user has permission to view
	 */
	@Override
	public List<AccountRole> filterFindByC_A(
		long companyId, long accountEntryId) {

		return filterFindByC_A(
			companyId, accountEntryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the account roles that the user has permission to view where companyId = &#63; and accountEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accountEntryId the account entry ID
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @return the range of matching account roles that the user has permission to view
	 */
	@Override
	public List<AccountRole> filterFindByC_A(
		long companyId, long accountEntryId, int start, int end) {

		return filterFindByC_A(companyId, accountEntryId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the account roles that the user has permissions to view where companyId = &#63; and accountEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accountEntryId the account entry ID
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching account roles that the user has permission to view
	 */
	@Override
	public List<AccountRole> filterFindByC_A(
		long companyId, long accountEntryId, int start, int end,
		OrderByComparator<AccountRole> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_A(
				companyId, accountEntryId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByC_A(
					companyId, accountEntryId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator));
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_ACCOUNTROLE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_ACCOUNTROLE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_A_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_A_ACCOUNTENTRYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_ACCOUNTROLE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(AccountRoleModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(AccountRoleModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AccountRole.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, AccountRoleImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, AccountRoleImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			queryPos.add(accountEntryId);

			return (List<AccountRole>)QueryUtil.list(
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
	 * Returns the account roles before and after the current account role in the ordered set of account roles that the user has permission to view where companyId = &#63; and accountEntryId = &#63;.
	 *
	 * @param accountRoleId the primary key of the current account role
	 * @param companyId the company ID
	 * @param accountEntryId the account entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next account role
	 * @throws NoSuchRoleException if a account role with the primary key could not be found
	 */
	@Override
	public AccountRole[] filterFindByC_A_PrevAndNext(
			long accountRoleId, long companyId, long accountEntryId,
			OrderByComparator<AccountRole> orderByComparator)
		throws NoSuchRoleException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_A_PrevAndNext(
				accountRoleId, companyId, accountEntryId, orderByComparator);
		}

		AccountRole accountRole = findByPrimaryKey(accountRoleId);

		Session session = null;

		try {
			session = openSession();

			AccountRole[] array = new AccountRoleImpl[3];

			array[0] = filterGetByC_A_PrevAndNext(
				session, accountRole, companyId, accountEntryId,
				orderByComparator, true);

			array[1] = accountRole;

			array[2] = filterGetByC_A_PrevAndNext(
				session, accountRole, companyId, accountEntryId,
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

	protected AccountRole filterGetByC_A_PrevAndNext(
		Session session, AccountRole accountRole, long companyId,
		long accountEntryId, OrderByComparator<AccountRole> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_ACCOUNTROLE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_ACCOUNTROLE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_A_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_A_ACCOUNTENTRYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_ACCOUNTROLE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(AccountRoleModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(AccountRoleModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AccountRole.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, AccountRoleImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, AccountRoleImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(companyId);

		queryPos.add(accountEntryId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(accountRole)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<AccountRole> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the account roles that the user has permission to view where companyId = &#63; and accountEntryId = any &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryIds the account entry IDs
	 * @return the matching account roles that the user has permission to view
	 */
	@Override
	public List<AccountRole> filterFindByC_A(
		long companyId, long[] accountEntryIds) {

		return filterFindByC_A(
			companyId, accountEntryIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the account roles that the user has permission to view where companyId = &#63; and accountEntryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accountEntryIds the account entry IDs
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @return the range of matching account roles that the user has permission to view
	 */
	@Override
	public List<AccountRole> filterFindByC_A(
		long companyId, long[] accountEntryIds, int start, int end) {

		return filterFindByC_A(companyId, accountEntryIds, start, end, null);
	}

	/**
	 * Returns an ordered range of all the account roles that the user has permission to view where companyId = &#63; and accountEntryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accountEntryIds the account entry IDs
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching account roles that the user has permission to view
	 */
	@Override
	public List<AccountRole> filterFindByC_A(
		long companyId, long[] accountEntryIds, int start, int end,
		OrderByComparator<AccountRole> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_A(
				companyId, accountEntryIds, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByC_A(
					companyId, accountEntryIds, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator));
		}

		if (accountEntryIds == null) {
			accountEntryIds = new long[0];
		}
		else if (accountEntryIds.length > 1) {
			accountEntryIds = ArrayUtil.sortedUnique(accountEntryIds);
		}

		StringBundler sb = new StringBundler();

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_ACCOUNTROLE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_ACCOUNTROLE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_A_COMPANYID_2);

		if (accountEntryIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_C_A_ACCOUNTENTRYID_7);

			sb.append(StringUtil.merge(accountEntryIds));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_ACCOUNTROLE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(AccountRoleModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(AccountRoleModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AccountRole.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, AccountRoleImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, AccountRoleImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			return (List<AccountRole>)QueryUtil.list(
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
	 * Returns all the account roles where companyId = &#63; and accountEntryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accountEntryIds the account entry IDs
	 * @return the matching account roles
	 */
	@Override
	public List<AccountRole> findByC_A(long companyId, long[] accountEntryIds) {
		return findByC_A(
			companyId, accountEntryIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the account roles where companyId = &#63; and accountEntryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accountEntryIds the account entry IDs
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @return the range of matching account roles
	 */
	@Override
	public List<AccountRole> findByC_A(
		long companyId, long[] accountEntryIds, int start, int end) {

		return findByC_A(companyId, accountEntryIds, start, end, null);
	}

	/**
	 * Returns an ordered range of all the account roles where companyId = &#63; and accountEntryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accountEntryIds the account entry IDs
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching account roles
	 */
	@Override
	public List<AccountRole> findByC_A(
		long companyId, long[] accountEntryIds, int start, int end,
		OrderByComparator<AccountRole> orderByComparator) {

		return findByC_A(
			companyId, accountEntryIds, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the account roles where companyId = &#63; and accountEntryId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accountEntryIds the account entry IDs
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching account roles
	 */
	@Override
	public List<AccountRole> findByC_A(
		long companyId, long[] accountEntryIds, int start, int end,
		OrderByComparator<AccountRole> orderByComparator,
		boolean useFinderCache) {

		if (accountEntryIds == null) {
			accountEntryIds = new long[0];
		}
		else if (accountEntryIds.length > 1) {
			accountEntryIds = ArrayUtil.sortedUnique(accountEntryIds);
		}

		if (accountEntryIds.length == 1) {
			return findByC_A(
				companyId, accountEntryIds[0], start, end, orderByComparator);
		}

		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderArgs = new Object[] {
					companyId, StringUtil.merge(accountEntryIds)
				};
			}
		}
		else if (useFinderCache) {
			finderArgs = new Object[] {
				companyId, StringUtil.merge(accountEntryIds), start, end,
				orderByComparator
			};
		}

		List<AccountRole> list = null;

		if (useFinderCache) {
			list = (List<AccountRole>)finderCache.getResult(
				_finderPathWithPaginationFindByC_A, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (AccountRole accountRole : list) {
					if ((companyId != accountRole.getCompanyId()) ||
						!ArrayUtil.contains(
							accountEntryIds, accountRole.getAccountEntryId())) {

						list = null;

						break;
					}
				}
			}
		}

		if (list == null) {
			StringBundler sb = new StringBundler();

			sb.append(_SQL_SELECT_ACCOUNTROLE_WHERE);

			sb.append(_FINDER_COLUMN_C_A_COMPANYID_2);

			if (accountEntryIds.length > 0) {
				sb.append("(");

				sb.append(_FINDER_COLUMN_C_A_ACCOUNTENTRYID_7);

				sb.append(StringUtil.merge(accountEntryIds));

				sb.append(")");

				sb.append(")");
			}

			sb.setStringAt(
				removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(AccountRoleModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				list = (List<AccountRole>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(
						_finderPathWithPaginationFindByC_A, finderArgs, list);
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
	 * Removes all the account roles where companyId = &#63; and accountEntryId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param accountEntryId the account entry ID
	 */
	@Override
	public void removeByC_A(long companyId, long accountEntryId) {
		for (AccountRole accountRole :
				findByC_A(
					companyId, accountEntryId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(accountRole);
		}
	}

	/**
	 * Returns the number of account roles where companyId = &#63; and accountEntryId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryId the account entry ID
	 * @return the number of matching account roles
	 */
	@Override
	public int countByC_A(long companyId, long accountEntryId) {
		FinderPath finderPath = _finderPathCountByC_A;

		Object[] finderArgs = new Object[] {companyId, accountEntryId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_ACCOUNTROLE_WHERE);

			sb.append(_FINDER_COLUMN_C_A_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_A_ACCOUNTENTRYID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(accountEntryId);

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
	 * Returns the number of account roles where companyId = &#63; and accountEntryId = any &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryIds the account entry IDs
	 * @return the number of matching account roles
	 */
	@Override
	public int countByC_A(long companyId, long[] accountEntryIds) {
		if (accountEntryIds == null) {
			accountEntryIds = new long[0];
		}
		else if (accountEntryIds.length > 1) {
			accountEntryIds = ArrayUtil.sortedUnique(accountEntryIds);
		}

		Object[] finderArgs = new Object[] {
			companyId, StringUtil.merge(accountEntryIds)
		};

		Long count = (Long)finderCache.getResult(
			_finderPathWithPaginationCountByC_A, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler();

			sb.append(_SQL_COUNT_ACCOUNTROLE_WHERE);

			sb.append(_FINDER_COLUMN_C_A_COMPANYID_2);

			if (accountEntryIds.length > 0) {
				sb.append("(");

				sb.append(_FINDER_COLUMN_C_A_ACCOUNTENTRYID_7);

				sb.append(StringUtil.merge(accountEntryIds));

				sb.append(")");

				sb.append(")");
			}

			sb.setStringAt(
				removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				count = (Long)query.uniqueResult();

				finderCache.putResult(
					_finderPathWithPaginationCountByC_A, finderArgs, count);
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
	 * Returns the number of account roles that the user has permission to view where companyId = &#63; and accountEntryId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryId the account entry ID
	 * @return the number of matching account roles that the user has permission to view
	 */
	@Override
	public int filterCountByC_A(long companyId, long accountEntryId) {
		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByC_A(companyId, accountEntryId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<AccountRole> accountRoles = findByC_A(
				companyId, accountEntryId);

			accountRoles = InlineSQLHelperUtil.filter(accountRoles);

			return accountRoles.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_ACCOUNTROLE_WHERE);

		sb.append(_FINDER_COLUMN_C_A_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_A_ACCOUNTENTRYID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AccountRole.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			queryPos.add(accountEntryId);

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

	/**
	 * Returns the number of account roles that the user has permission to view where companyId = &#63; and accountEntryId = any &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryIds the account entry IDs
	 * @return the number of matching account roles that the user has permission to view
	 */
	@Override
	public int filterCountByC_A(long companyId, long[] accountEntryIds) {
		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByC_A(companyId, accountEntryIds);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<AccountRole> accountRoles = InlineSQLHelperUtil.filter(
				findByC_A(companyId, accountEntryIds));

			return accountRoles.size();
		}

		if (accountEntryIds == null) {
			accountEntryIds = new long[0];
		}
		else if (accountEntryIds.length > 1) {
			accountEntryIds = ArrayUtil.sortedUnique(accountEntryIds);
		}

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_ACCOUNTROLE_WHERE);

		sb.append(_FINDER_COLUMN_C_A_COMPANYID_2);

		if (accountEntryIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_C_A_ACCOUNTENTRYID_7);

			sb.append(StringUtil.merge(accountEntryIds));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), AccountRole.class.getName(),
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

	private static final String _FINDER_COLUMN_C_A_COMPANYID_2 =
		"accountRole.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_A_ACCOUNTENTRYID_2 =
		"accountRole.accountEntryId = ?";

	private static final String _FINDER_COLUMN_C_A_ACCOUNTENTRYID_7 =
		"accountRole.accountEntryId IN (";

	private FinderPath _finderPathFetchByERC_C;

	/**
	 * Returns the account role where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchRoleException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching account role
	 * @throws NoSuchRoleException if a matching account role could not be found
	 */
	@Override
	public AccountRole findByERC_C(String externalReferenceCode, long companyId)
		throws NoSuchRoleException {

		AccountRole accountRole = fetchByERC_C(
			externalReferenceCode, companyId);

		if (accountRole == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("externalReferenceCode=");
			sb.append(externalReferenceCode);

			sb.append(", companyId=");
			sb.append(companyId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchRoleException(sb.toString());
		}

		return accountRole;
	}

	/**
	 * Returns the account role where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching account role, or <code>null</code> if a matching account role could not be found
	 */
	@Override
	public AccountRole fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return fetchByERC_C(externalReferenceCode, companyId, true);
	}

	/**
	 * Returns the account role where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching account role, or <code>null</code> if a matching account role could not be found
	 */
	@Override
	public AccountRole fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		externalReferenceCode = Objects.toString(externalReferenceCode, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {externalReferenceCode, companyId};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByERC_C, finderArgs, this);
		}

		if (result instanceof AccountRole) {
			AccountRole accountRole = (AccountRole)result;

			if (!Objects.equals(
					externalReferenceCode,
					accountRole.getExternalReferenceCode()) ||
				(companyId != accountRole.getCompanyId())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_ACCOUNTROLE_WHERE);

			boolean bindExternalReferenceCode = false;

			if (externalReferenceCode.isEmpty()) {
				sb.append(_FINDER_COLUMN_ERC_C_EXTERNALREFERENCECODE_3);
			}
			else {
				bindExternalReferenceCode = true;

				sb.append(_FINDER_COLUMN_ERC_C_EXTERNALREFERENCECODE_2);
			}

			sb.append(_FINDER_COLUMN_ERC_C_COMPANYID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindExternalReferenceCode) {
					queryPos.add(externalReferenceCode);
				}

				queryPos.add(companyId);

				List<AccountRole> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByERC_C, finderArgs, list);
					}
				}
				else {
					AccountRole accountRole = list.get(0);

					result = accountRole;

					cacheResult(accountRole);
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
			return (AccountRole)result;
		}
	}

	/**
	 * Removes the account role where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the account role that was removed
	 */
	@Override
	public AccountRole removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchRoleException {

		AccountRole accountRole = findByERC_C(externalReferenceCode, companyId);

		return remove(accountRole);
	}

	/**
	 * Returns the number of account roles where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching account roles
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		AccountRole accountRole = fetchByERC_C(
			externalReferenceCode, companyId);

		if (accountRole == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ERC_C_EXTERNALREFERENCECODE_2 =
		"accountRole.externalReferenceCode = ? AND ";

	private static final String _FINDER_COLUMN_ERC_C_EXTERNALREFERENCECODE_3 =
		"(accountRole.externalReferenceCode IS NULL OR accountRole.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_C_COMPANYID_2 =
		"accountRole.companyId = ?";

	public AccountRolePersistenceImpl() {
		setModelClass(AccountRole.class);

		setModelImplClass(AccountRoleImpl.class);
		setModelPKClass(long.class);

		setTable(AccountRoleTable.INSTANCE);
	}

	/**
	 * Caches the account role in the entity cache if it is enabled.
	 *
	 * @param accountRole the account role
	 */
	@Override
	public void cacheResult(AccountRole accountRole) {
		entityCache.putResult(
			AccountRoleImpl.class, accountRole.getPrimaryKey(), accountRole);

		finderCache.putResult(
			_finderPathFetchByRoleId, new Object[] {accountRole.getRoleId()},
			accountRole);

		finderCache.putResult(
			_finderPathFetchByERC_C,
			new Object[] {
				accountRole.getExternalReferenceCode(),
				accountRole.getCompanyId()
			},
			accountRole);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the account roles in the entity cache if it is enabled.
	 *
	 * @param accountRoles the account roles
	 */
	@Override
	public void cacheResult(List<AccountRole> accountRoles) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (accountRoles.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (AccountRole accountRole : accountRoles) {
			if (entityCache.getResult(
					AccountRoleImpl.class, accountRole.getPrimaryKey()) ==
						null) {

				cacheResult(accountRole);
			}
		}
	}

	/**
	 * Clears the cache for all account roles.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(AccountRoleImpl.class);

		finderCache.clearCache(AccountRoleImpl.class);
	}

	/**
	 * Clears the cache for the account role.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(AccountRole accountRole) {
		entityCache.removeResult(AccountRoleImpl.class, accountRole);
	}

	@Override
	public void clearCache(List<AccountRole> accountRoles) {
		for (AccountRole accountRole : accountRoles) {
			entityCache.removeResult(AccountRoleImpl.class, accountRole);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(AccountRoleImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(AccountRoleImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		AccountRoleModelImpl accountRoleModelImpl) {

		Object[] args = new Object[] {accountRoleModelImpl.getRoleId()};

		finderCache.putResult(
			_finderPathFetchByRoleId, args, accountRoleModelImpl);

		args = new Object[] {
			accountRoleModelImpl.getExternalReferenceCode(),
			accountRoleModelImpl.getCompanyId()
		};

		finderCache.putResult(
			_finderPathFetchByERC_C, args, accountRoleModelImpl);
	}

	/**
	 * Creates a new account role with the primary key. Does not add the account role to the database.
	 *
	 * @param accountRoleId the primary key for the new account role
	 * @return the new account role
	 */
	@Override
	public AccountRole create(long accountRoleId) {
		AccountRole accountRole = new AccountRoleImpl();

		accountRole.setNew(true);
		accountRole.setPrimaryKey(accountRoleId);

		accountRole.setCompanyId(CompanyThreadLocal.getCompanyId());

		return accountRole;
	}

	/**
	 * Removes the account role with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param accountRoleId the primary key of the account role
	 * @return the account role that was removed
	 * @throws NoSuchRoleException if a account role with the primary key could not be found
	 */
	@Override
	public AccountRole remove(long accountRoleId) throws NoSuchRoleException {
		return remove((Serializable)accountRoleId);
	}

	/**
	 * Removes the account role with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the account role
	 * @return the account role that was removed
	 * @throws NoSuchRoleException if a account role with the primary key could not be found
	 */
	@Override
	public AccountRole remove(Serializable primaryKey)
		throws NoSuchRoleException {

		Session session = null;

		try {
			session = openSession();

			AccountRole accountRole = (AccountRole)session.get(
				AccountRoleImpl.class, primaryKey);

			if (accountRole == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchRoleException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(accountRole);
		}
		catch (NoSuchRoleException noSuchEntityException) {
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
	protected AccountRole removeImpl(AccountRole accountRole) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(accountRole)) {
				accountRole = (AccountRole)session.get(
					AccountRoleImpl.class, accountRole.getPrimaryKeyObj());
			}

			if (accountRole != null) {
				session.delete(accountRole);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (accountRole != null) {
			clearCache(accountRole);
		}

		return accountRole;
	}

	@Override
	public AccountRole updateImpl(AccountRole accountRole) {
		boolean isNew = accountRole.isNew();

		if (!(accountRole instanceof AccountRoleModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(accountRole.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(accountRole);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in accountRole proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom AccountRole implementation " +
					accountRole.getClass());
		}

		AccountRoleModelImpl accountRoleModelImpl =
			(AccountRoleModelImpl)accountRole;

		if (Validator.isNull(accountRole.getExternalReferenceCode())) {
			accountRole.setExternalReferenceCode(
				String.valueOf(accountRole.getPrimaryKey()));
		}
		else {
			if (!Objects.equals(
					accountRoleModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					accountRole.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = accountRole.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = accountRole.getPrimaryKey();
					}

					try {
						accountRole.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								AccountRole.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								accountRole.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			AccountRole ercAccountRole = fetchByERC_C(
				accountRole.getExternalReferenceCode(),
				accountRole.getCompanyId());

			if (isNew) {
				if (ercAccountRole != null) {
					throw new DuplicateAccountRoleExternalReferenceCodeException(
						"Duplicate account role with external reference code " +
							accountRole.getExternalReferenceCode() +
								" and company " + accountRole.getCompanyId());
				}
			}
			else {
				if ((ercAccountRole != null) &&
					(accountRole.getAccountRoleId() !=
						ercAccountRole.getAccountRoleId())) {

					throw new DuplicateAccountRoleExternalReferenceCodeException(
						"Duplicate account role with external reference code " +
							accountRole.getExternalReferenceCode() +
								" and company " + accountRole.getCompanyId());
				}
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(accountRole);
			}
			else {
				accountRole = (AccountRole)session.merge(accountRole);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			AccountRoleImpl.class, accountRoleModelImpl, false, true);

		cacheUniqueFindersCache(accountRoleModelImpl);

		if (isNew) {
			accountRole.setNew(false);
		}

		accountRole.resetOriginalValues();

		return accountRole;
	}

	/**
	 * Returns the account role with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the account role
	 * @return the account role
	 * @throws NoSuchRoleException if a account role with the primary key could not be found
	 */
	@Override
	public AccountRole findByPrimaryKey(Serializable primaryKey)
		throws NoSuchRoleException {

		AccountRole accountRole = fetchByPrimaryKey(primaryKey);

		if (accountRole == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchRoleException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return accountRole;
	}

	/**
	 * Returns the account role with the primary key or throws a <code>NoSuchRoleException</code> if it could not be found.
	 *
	 * @param accountRoleId the primary key of the account role
	 * @return the account role
	 * @throws NoSuchRoleException if a account role with the primary key could not be found
	 */
	@Override
	public AccountRole findByPrimaryKey(long accountRoleId)
		throws NoSuchRoleException {

		return findByPrimaryKey((Serializable)accountRoleId);
	}

	/**
	 * Returns the account role with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param accountRoleId the primary key of the account role
	 * @return the account role, or <code>null</code> if a account role with the primary key could not be found
	 */
	@Override
	public AccountRole fetchByPrimaryKey(long accountRoleId) {
		return fetchByPrimaryKey((Serializable)accountRoleId);
	}

	/**
	 * Returns all the account roles.
	 *
	 * @return the account roles
	 */
	@Override
	public List<AccountRole> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the account roles.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @return the range of account roles
	 */
	@Override
	public List<AccountRole> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the account roles.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of account roles
	 */
	@Override
	public List<AccountRole> findAll(
		int start, int end, OrderByComparator<AccountRole> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the account roles.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of account roles
	 */
	@Override
	public List<AccountRole> findAll(
		int start, int end, OrderByComparator<AccountRole> orderByComparator,
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

		List<AccountRole> list = null;

		if (useFinderCache) {
			list = (List<AccountRole>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_ACCOUNTROLE);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_ACCOUNTROLE;

				sql = sql.concat(AccountRoleModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<AccountRole>)QueryUtil.list(
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
	 * Removes all the account roles from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (AccountRole accountRole : findAll()) {
			remove(accountRole);
		}
	}

	/**
	 * Returns the number of account roles.
	 *
	 * @return the number of account roles
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_ACCOUNTROLE);

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
		return "accountRoleId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_ACCOUNTROLE;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return AccountRoleModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the account role persistence.
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

		_finderPathWithPaginationFindByAccountEntryId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByAccountEntryId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"accountEntryId"}, true);

		_finderPathWithoutPaginationFindByAccountEntryId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByAccountEntryId",
			new String[] {Long.class.getName()},
			new String[] {"accountEntryId"}, true);

		_finderPathCountByAccountEntryId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByAccountEntryId",
			new String[] {Long.class.getName()},
			new String[] {"accountEntryId"}, false);

		_finderPathWithPaginationCountByAccountEntryId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByAccountEntryId",
			new String[] {Long.class.getName()},
			new String[] {"accountEntryId"}, false);

		_finderPathFetchByRoleId = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByRoleId",
			new String[] {Long.class.getName()}, new String[] {"roleId"}, true);

		_finderPathWithPaginationFindByC_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_A",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "accountEntryId"}, true);

		_finderPathWithoutPaginationFindByC_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_A",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"companyId", "accountEntryId"}, true);

		_finderPathCountByC_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_A",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"companyId", "accountEntryId"}, false);

		_finderPathWithPaginationCountByC_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_A",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"companyId", "accountEntryId"}, false);

		_finderPathFetchByERC_C = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"externalReferenceCode", "companyId"}, true);

		AccountRoleUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		AccountRoleUtil.setPersistence(null);

		entityCache.removeCache(AccountRoleImpl.class.getName());
	}

	@Override
	@Reference(
		target = AccountPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = AccountPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = AccountPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_ACCOUNTROLE =
		"SELECT accountRole FROM AccountRole accountRole";

	private static final String _SQL_SELECT_ACCOUNTROLE_WHERE =
		"SELECT accountRole FROM AccountRole accountRole WHERE ";

	private static final String _SQL_COUNT_ACCOUNTROLE =
		"SELECT COUNT(accountRole) FROM AccountRole accountRole";

	private static final String _SQL_COUNT_ACCOUNTROLE_WHERE =
		"SELECT COUNT(accountRole) FROM AccountRole accountRole WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"accountRole.accountRoleId";

	private static final String _FILTER_SQL_SELECT_ACCOUNTROLE_WHERE =
		"SELECT DISTINCT {accountRole.*} FROM AccountRole accountRole WHERE ";

	private static final String
		_FILTER_SQL_SELECT_ACCOUNTROLE_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {AccountRole.*} FROM (SELECT DISTINCT accountRole.accountRoleId FROM AccountRole accountRole WHERE ";

	private static final String
		_FILTER_SQL_SELECT_ACCOUNTROLE_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN AccountRole ON TEMP_TABLE.accountRoleId = AccountRole.accountRoleId";

	private static final String _FILTER_SQL_COUNT_ACCOUNTROLE_WHERE =
		"SELECT COUNT(DISTINCT accountRole.accountRoleId) AS COUNT_VALUE FROM AccountRole accountRole WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "accountRole";

	private static final String _FILTER_ENTITY_TABLE = "AccountRole";

	private static final String _ORDER_BY_ENTITY_ALIAS = "accountRole.";

	private static final String _ORDER_BY_ENTITY_TABLE = "AccountRole.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No AccountRole exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No AccountRole exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		AccountRolePersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}