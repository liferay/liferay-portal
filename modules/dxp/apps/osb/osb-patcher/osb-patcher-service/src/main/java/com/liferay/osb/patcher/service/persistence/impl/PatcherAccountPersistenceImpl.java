/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence.impl;

import com.liferay.osb.patcher.exception.NoSuchPatcherAccountException;
import com.liferay.osb.patcher.model.PatcherAccount;
import com.liferay.osb.patcher.model.PatcherAccountTable;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.model.impl.PatcherAccountImpl;
import com.liferay.osb.patcher.model.impl.PatcherAccountModelImpl;
import com.liferay.osb.patcher.service.persistence.PatcherAccountPersistence;
import com.liferay.osb.patcher.service.persistence.PatcherAccountUtil;
import com.liferay.osb.patcher.service.persistence.impl.constants.OSBPatcherPersistenceConstants;
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
import com.liferay.portal.kernel.service.persistence.impl.TableMapper;
import com.liferay.portal.kernel.service.persistence.impl.TableMapperFactory;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.HashSet;
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
 * The persistence implementation for the patcher account service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = PatcherAccountPersistence.class)
public class PatcherAccountPersistenceImpl
	extends BasePersistenceImpl<PatcherAccount>
	implements PatcherAccountPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PatcherAccountUtil</code> to access the patcher account persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PatcherAccountImpl.class.getName();

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
	 * Returns all the patcher accounts where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching patcher accounts
	 */
	@Override
	public List<PatcherAccount> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher accounts where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherAccountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @return the range of matching patcher accounts
	 */
	@Override
	public List<PatcherAccount> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher accounts where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherAccountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher accounts
	 */
	@Override
	public List<PatcherAccount> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<PatcherAccount> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher accounts where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherAccountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher accounts
	 */
	@Override
	public List<PatcherAccount> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<PatcherAccount> orderByComparator,
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

		List<PatcherAccount> list = null;

		if (useFinderCache) {
			list = (List<PatcherAccount>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherAccount patcherAccount : list) {
					if (companyId != patcherAccount.getCompanyId()) {
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

			sb.append(_SQL_SELECT_PATCHERACCOUNT_WHERE);

			sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherAccountModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				list = (List<PatcherAccount>)QueryUtil.list(
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
	 * Returns the first patcher account in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher account
	 * @throws NoSuchPatcherAccountException if a matching patcher account could not be found
	 */
	@Override
	public PatcherAccount findByCompanyId_First(
			long companyId, OrderByComparator<PatcherAccount> orderByComparator)
		throws NoSuchPatcherAccountException {

		PatcherAccount patcherAccount = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (patcherAccount != null) {
			return patcherAccount;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchPatcherAccountException(sb.toString());
	}

	/**
	 * Returns the first patcher account in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher account, or <code>null</code> if a matching patcher account could not be found
	 */
	@Override
	public PatcherAccount fetchByCompanyId_First(
		long companyId, OrderByComparator<PatcherAccount> orderByComparator) {

		List<PatcherAccount> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last patcher account in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher account
	 * @throws NoSuchPatcherAccountException if a matching patcher account could not be found
	 */
	@Override
	public PatcherAccount findByCompanyId_Last(
			long companyId, OrderByComparator<PatcherAccount> orderByComparator)
		throws NoSuchPatcherAccountException {

		PatcherAccount patcherAccount = fetchByCompanyId_Last(
			companyId, orderByComparator);

		if (patcherAccount != null) {
			return patcherAccount;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchPatcherAccountException(sb.toString());
	}

	/**
	 * Returns the last patcher account in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher account, or <code>null</code> if a matching patcher account could not be found
	 */
	@Override
	public PatcherAccount fetchByCompanyId_Last(
		long companyId, OrderByComparator<PatcherAccount> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<PatcherAccount> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the patcher accounts before and after the current patcher account in the ordered set where companyId = &#63;.
	 *
	 * @param patcherAccountId the primary key of the current patcher account
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher account
	 * @throws NoSuchPatcherAccountException if a patcher account with the primary key could not be found
	 */
	@Override
	public PatcherAccount[] findByCompanyId_PrevAndNext(
			long patcherAccountId, long companyId,
			OrderByComparator<PatcherAccount> orderByComparator)
		throws NoSuchPatcherAccountException {

		PatcherAccount patcherAccount = findByPrimaryKey(patcherAccountId);

		Session session = null;

		try {
			session = openSession();

			PatcherAccount[] array = new PatcherAccountImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, patcherAccount, companyId, orderByComparator, true);

			array[1] = patcherAccount;

			array[2] = getByCompanyId_PrevAndNext(
				session, patcherAccount, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PatcherAccount getByCompanyId_PrevAndNext(
		Session session, PatcherAccount patcherAccount, long companyId,
		OrderByComparator<PatcherAccount> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_PATCHERACCOUNT_WHERE);

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
			sb.append(PatcherAccountModelImpl.ORDER_BY_JPQL);
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
						patcherAccount)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherAccount> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the patcher accounts that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching patcher accounts that the user has permission to view
	 */
	@Override
	public List<PatcherAccount> filterFindByCompanyId(long companyId) {
		return filterFindByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher accounts that the user has permission to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherAccountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @return the range of matching patcher accounts that the user has permission to view
	 */
	@Override
	public List<PatcherAccount> filterFindByCompanyId(
		long companyId, int start, int end) {

		return filterFindByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher accounts that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherAccountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher accounts that the user has permission to view
	 */
	@Override
	public List<PatcherAccount> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<PatcherAccount> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_PATCHERACCOUNT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERACCOUNT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERACCOUNT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherAccountModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherAccountModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherAccount.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, PatcherAccountImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, PatcherAccountImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			return (List<PatcherAccount>)QueryUtil.list(
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
	 * Returns the patcher accounts before and after the current patcher account in the ordered set of patcher accounts that the user has permission to view where companyId = &#63;.
	 *
	 * @param patcherAccountId the primary key of the current patcher account
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher account
	 * @throws NoSuchPatcherAccountException if a patcher account with the primary key could not be found
	 */
	@Override
	public PatcherAccount[] filterFindByCompanyId_PrevAndNext(
			long patcherAccountId, long companyId,
			OrderByComparator<PatcherAccount> orderByComparator)
		throws NoSuchPatcherAccountException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByCompanyId_PrevAndNext(
				patcherAccountId, companyId, orderByComparator);
		}

		PatcherAccount patcherAccount = findByPrimaryKey(patcherAccountId);

		Session session = null;

		try {
			session = openSession();

			PatcherAccount[] array = new PatcherAccountImpl[3];

			array[0] = filterGetByCompanyId_PrevAndNext(
				session, patcherAccount, companyId, orderByComparator, true);

			array[1] = patcherAccount;

			array[2] = filterGetByCompanyId_PrevAndNext(
				session, patcherAccount, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PatcherAccount filterGetByCompanyId_PrevAndNext(
		Session session, PatcherAccount patcherAccount, long companyId,
		OrderByComparator<PatcherAccount> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_PATCHERACCOUNT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERACCOUNT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERACCOUNT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherAccountModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherAccountModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherAccount.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherAccountImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherAccountImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						patcherAccount)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherAccount> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the patcher accounts where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (PatcherAccount patcherAccount :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(patcherAccount);
		}
	}

	/**
	 * Returns the number of patcher accounts where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching patcher accounts
	 */
	@Override
	public int countByCompanyId(long companyId) {
		FinderPath finderPath = _finderPathCountByCompanyId;

		Object[] finderArgs = new Object[] {companyId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_PATCHERACCOUNT_WHERE);

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
	 * Returns the number of patcher accounts that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching patcher accounts that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByCompanyId(companyId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PatcherAccount> patcherAccounts = findByCompanyId(companyId);

			patcherAccounts = InlineSQLHelperUtil.filter(patcherAccounts);

			return patcherAccounts.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_PATCHERACCOUNT_WHERE);

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherAccount.class.getName(),
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
		"patcherAccount.companyId = ?";

	private FinderPath _finderPathFetchByAccountEntryCode;

	/**
	 * Returns the patcher account where accountEntryCode = &#63; or throws a <code>NoSuchPatcherAccountException</code> if it could not be found.
	 *
	 * @param accountEntryCode the account entry code
	 * @return the matching patcher account
	 * @throws NoSuchPatcherAccountException if a matching patcher account could not be found
	 */
	@Override
	public PatcherAccount findByAccountEntryCode(String accountEntryCode)
		throws NoSuchPatcherAccountException {

		PatcherAccount patcherAccount = fetchByAccountEntryCode(
			accountEntryCode);

		if (patcherAccount == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("accountEntryCode=");
			sb.append(accountEntryCode);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchPatcherAccountException(sb.toString());
		}

		return patcherAccount;
	}

	/**
	 * Returns the patcher account where accountEntryCode = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param accountEntryCode the account entry code
	 * @return the matching patcher account, or <code>null</code> if a matching patcher account could not be found
	 */
	@Override
	public PatcherAccount fetchByAccountEntryCode(String accountEntryCode) {
		return fetchByAccountEntryCode(accountEntryCode, true);
	}

	/**
	 * Returns the patcher account where accountEntryCode = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param accountEntryCode the account entry code
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching patcher account, or <code>null</code> if a matching patcher account could not be found
	 */
	@Override
	public PatcherAccount fetchByAccountEntryCode(
		String accountEntryCode, boolean useFinderCache) {

		accountEntryCode = Objects.toString(accountEntryCode, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {accountEntryCode};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByAccountEntryCode, finderArgs, this);
		}

		if (result instanceof PatcherAccount) {
			PatcherAccount patcherAccount = (PatcherAccount)result;

			if (!Objects.equals(
					accountEntryCode, patcherAccount.getAccountEntryCode())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_SELECT_PATCHERACCOUNT_WHERE);

			boolean bindAccountEntryCode = false;

			if (accountEntryCode.isEmpty()) {
				sb.append(_FINDER_COLUMN_ACCOUNTENTRYCODE_ACCOUNTENTRYCODE_3);
			}
			else {
				bindAccountEntryCode = true;

				sb.append(_FINDER_COLUMN_ACCOUNTENTRYCODE_ACCOUNTENTRYCODE_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindAccountEntryCode) {
					queryPos.add(accountEntryCode);
				}

				List<PatcherAccount> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByAccountEntryCode, finderArgs,
							list);
					}
				}
				else {
					PatcherAccount patcherAccount = list.get(0);

					result = patcherAccount;

					cacheResult(patcherAccount);
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
			return (PatcherAccount)result;
		}
	}

	/**
	 * Removes the patcher account where accountEntryCode = &#63; from the database.
	 *
	 * @param accountEntryCode the account entry code
	 * @return the patcher account that was removed
	 */
	@Override
	public PatcherAccount removeByAccountEntryCode(String accountEntryCode)
		throws NoSuchPatcherAccountException {

		PatcherAccount patcherAccount = findByAccountEntryCode(
			accountEntryCode);

		return remove(patcherAccount);
	}

	/**
	 * Returns the number of patcher accounts where accountEntryCode = &#63;.
	 *
	 * @param accountEntryCode the account entry code
	 * @return the number of matching patcher accounts
	 */
	@Override
	public int countByAccountEntryCode(String accountEntryCode) {
		PatcherAccount patcherAccount = fetchByAccountEntryCode(
			accountEntryCode);

		if (patcherAccount == null) {
			return 0;
		}

		return 1;
	}

	private static final String
		_FINDER_COLUMN_ACCOUNTENTRYCODE_ACCOUNTENTRYCODE_2 =
			"patcherAccount.accountEntryCode = ?";

	private static final String
		_FINDER_COLUMN_ACCOUNTENTRYCODE_ACCOUNTENTRYCODE_3 =
			"(patcherAccount.accountEntryCode IS NULL OR patcherAccount.accountEntryCode = '')";

	private FinderPath _finderPathWithPaginationFindByC_LikeA;
	private FinderPath _finderPathWithPaginationCountByC_LikeA;

	/**
	 * Returns all the patcher accounts where companyId = &#63; and accountEntryCode LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryCode the account entry code
	 * @return the matching patcher accounts
	 */
	@Override
	public List<PatcherAccount> findByC_LikeA(
		long companyId, String accountEntryCode) {

		return findByC_LikeA(
			companyId, accountEntryCode, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the patcher accounts where companyId = &#63; and accountEntryCode LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherAccountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accountEntryCode the account entry code
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @return the range of matching patcher accounts
	 */
	@Override
	public List<PatcherAccount> findByC_LikeA(
		long companyId, String accountEntryCode, int start, int end) {

		return findByC_LikeA(companyId, accountEntryCode, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher accounts where companyId = &#63; and accountEntryCode LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherAccountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accountEntryCode the account entry code
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher accounts
	 */
	@Override
	public List<PatcherAccount> findByC_LikeA(
		long companyId, String accountEntryCode, int start, int end,
		OrderByComparator<PatcherAccount> orderByComparator) {

		return findByC_LikeA(
			companyId, accountEntryCode, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher accounts where companyId = &#63; and accountEntryCode LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherAccountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accountEntryCode the account entry code
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher accounts
	 */
	@Override
	public List<PatcherAccount> findByC_LikeA(
		long companyId, String accountEntryCode, int start, int end,
		OrderByComparator<PatcherAccount> orderByComparator,
		boolean useFinderCache) {

		accountEntryCode = Objects.toString(accountEntryCode, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		finderPath = _finderPathWithPaginationFindByC_LikeA;
		finderArgs = new Object[] {
			companyId, accountEntryCode, start, end, orderByComparator
		};

		List<PatcherAccount> list = null;

		if (useFinderCache) {
			list = (List<PatcherAccount>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherAccount patcherAccount : list) {
					if ((companyId != patcherAccount.getCompanyId()) ||
						!StringUtil.wildcardMatches(
							patcherAccount.getAccountEntryCode(),
							accountEntryCode, '_', '%', '\\', true)) {

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

			sb.append(_SQL_SELECT_PATCHERACCOUNT_WHERE);

			sb.append(_FINDER_COLUMN_C_LIKEA_COMPANYID_2);

			boolean bindAccountEntryCode = false;

			if (accountEntryCode.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_LIKEA_ACCOUNTENTRYCODE_3);
			}
			else {
				bindAccountEntryCode = true;

				sb.append(_FINDER_COLUMN_C_LIKEA_ACCOUNTENTRYCODE_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherAccountModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				if (bindAccountEntryCode) {
					queryPos.add(accountEntryCode);
				}

				list = (List<PatcherAccount>)QueryUtil.list(
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
	 * Returns the first patcher account in the ordered set where companyId = &#63; and accountEntryCode LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryCode the account entry code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher account
	 * @throws NoSuchPatcherAccountException if a matching patcher account could not be found
	 */
	@Override
	public PatcherAccount findByC_LikeA_First(
			long companyId, String accountEntryCode,
			OrderByComparator<PatcherAccount> orderByComparator)
		throws NoSuchPatcherAccountException {

		PatcherAccount patcherAccount = fetchByC_LikeA_First(
			companyId, accountEntryCode, orderByComparator);

		if (patcherAccount != null) {
			return patcherAccount;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", accountEntryCodeLIKE");
		sb.append(accountEntryCode);

		sb.append("}");

		throw new NoSuchPatcherAccountException(sb.toString());
	}

	/**
	 * Returns the first patcher account in the ordered set where companyId = &#63; and accountEntryCode LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryCode the account entry code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher account, or <code>null</code> if a matching patcher account could not be found
	 */
	@Override
	public PatcherAccount fetchByC_LikeA_First(
		long companyId, String accountEntryCode,
		OrderByComparator<PatcherAccount> orderByComparator) {

		List<PatcherAccount> list = findByC_LikeA(
			companyId, accountEntryCode, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last patcher account in the ordered set where companyId = &#63; and accountEntryCode LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryCode the account entry code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher account
	 * @throws NoSuchPatcherAccountException if a matching patcher account could not be found
	 */
	@Override
	public PatcherAccount findByC_LikeA_Last(
			long companyId, String accountEntryCode,
			OrderByComparator<PatcherAccount> orderByComparator)
		throws NoSuchPatcherAccountException {

		PatcherAccount patcherAccount = fetchByC_LikeA_Last(
			companyId, accountEntryCode, orderByComparator);

		if (patcherAccount != null) {
			return patcherAccount;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", accountEntryCodeLIKE");
		sb.append(accountEntryCode);

		sb.append("}");

		throw new NoSuchPatcherAccountException(sb.toString());
	}

	/**
	 * Returns the last patcher account in the ordered set where companyId = &#63; and accountEntryCode LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryCode the account entry code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher account, or <code>null</code> if a matching patcher account could not be found
	 */
	@Override
	public PatcherAccount fetchByC_LikeA_Last(
		long companyId, String accountEntryCode,
		OrderByComparator<PatcherAccount> orderByComparator) {

		int count = countByC_LikeA(companyId, accountEntryCode);

		if (count == 0) {
			return null;
		}

		List<PatcherAccount> list = findByC_LikeA(
			companyId, accountEntryCode, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the patcher accounts before and after the current patcher account in the ordered set where companyId = &#63; and accountEntryCode LIKE &#63;.
	 *
	 * @param patcherAccountId the primary key of the current patcher account
	 * @param companyId the company ID
	 * @param accountEntryCode the account entry code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher account
	 * @throws NoSuchPatcherAccountException if a patcher account with the primary key could not be found
	 */
	@Override
	public PatcherAccount[] findByC_LikeA_PrevAndNext(
			long patcherAccountId, long companyId, String accountEntryCode,
			OrderByComparator<PatcherAccount> orderByComparator)
		throws NoSuchPatcherAccountException {

		accountEntryCode = Objects.toString(accountEntryCode, "");

		PatcherAccount patcherAccount = findByPrimaryKey(patcherAccountId);

		Session session = null;

		try {
			session = openSession();

			PatcherAccount[] array = new PatcherAccountImpl[3];

			array[0] = getByC_LikeA_PrevAndNext(
				session, patcherAccount, companyId, accountEntryCode,
				orderByComparator, true);

			array[1] = patcherAccount;

			array[2] = getByC_LikeA_PrevAndNext(
				session, patcherAccount, companyId, accountEntryCode,
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

	protected PatcherAccount getByC_LikeA_PrevAndNext(
		Session session, PatcherAccount patcherAccount, long companyId,
		String accountEntryCode,
		OrderByComparator<PatcherAccount> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_PATCHERACCOUNT_WHERE);

		sb.append(_FINDER_COLUMN_C_LIKEA_COMPANYID_2);

		boolean bindAccountEntryCode = false;

		if (accountEntryCode.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_LIKEA_ACCOUNTENTRYCODE_3);
		}
		else {
			bindAccountEntryCode = true;

			sb.append(_FINDER_COLUMN_C_LIKEA_ACCOUNTENTRYCODE_2);
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
			sb.append(PatcherAccountModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (bindAccountEntryCode) {
			queryPos.add(accountEntryCode);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						patcherAccount)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherAccount> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the patcher accounts that the user has permission to view where companyId = &#63; and accountEntryCode LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryCode the account entry code
	 * @return the matching patcher accounts that the user has permission to view
	 */
	@Override
	public List<PatcherAccount> filterFindByC_LikeA(
		long companyId, String accountEntryCode) {

		return filterFindByC_LikeA(
			companyId, accountEntryCode, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the patcher accounts that the user has permission to view where companyId = &#63; and accountEntryCode LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherAccountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accountEntryCode the account entry code
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @return the range of matching patcher accounts that the user has permission to view
	 */
	@Override
	public List<PatcherAccount> filterFindByC_LikeA(
		long companyId, String accountEntryCode, int start, int end) {

		return filterFindByC_LikeA(
			companyId, accountEntryCode, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher accounts that the user has permissions to view where companyId = &#63; and accountEntryCode LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherAccountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accountEntryCode the account entry code
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher accounts that the user has permission to view
	 */
	@Override
	public List<PatcherAccount> filterFindByC_LikeA(
		long companyId, String accountEntryCode, int start, int end,
		OrderByComparator<PatcherAccount> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_LikeA(
				companyId, accountEntryCode, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByC_LikeA(
					companyId, accountEntryCode, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator));
		}

		accountEntryCode = Objects.toString(accountEntryCode, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERACCOUNT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERACCOUNT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_LIKEA_COMPANYID_2);

		boolean bindAccountEntryCode = false;

		if (accountEntryCode.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_LIKEA_ACCOUNTENTRYCODE_3);
		}
		else {
			bindAccountEntryCode = true;

			sb.append(_FINDER_COLUMN_C_LIKEA_ACCOUNTENTRYCODE_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERACCOUNT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherAccountModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherAccountModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherAccount.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, PatcherAccountImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, PatcherAccountImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			if (bindAccountEntryCode) {
				queryPos.add(accountEntryCode);
			}

			return (List<PatcherAccount>)QueryUtil.list(
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
	 * Returns the patcher accounts before and after the current patcher account in the ordered set of patcher accounts that the user has permission to view where companyId = &#63; and accountEntryCode LIKE &#63;.
	 *
	 * @param patcherAccountId the primary key of the current patcher account
	 * @param companyId the company ID
	 * @param accountEntryCode the account entry code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher account
	 * @throws NoSuchPatcherAccountException if a patcher account with the primary key could not be found
	 */
	@Override
	public PatcherAccount[] filterFindByC_LikeA_PrevAndNext(
			long patcherAccountId, long companyId, String accountEntryCode,
			OrderByComparator<PatcherAccount> orderByComparator)
		throws NoSuchPatcherAccountException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_LikeA_PrevAndNext(
				patcherAccountId, companyId, accountEntryCode,
				orderByComparator);
		}

		accountEntryCode = Objects.toString(accountEntryCode, "");

		PatcherAccount patcherAccount = findByPrimaryKey(patcherAccountId);

		Session session = null;

		try {
			session = openSession();

			PatcherAccount[] array = new PatcherAccountImpl[3];

			array[0] = filterGetByC_LikeA_PrevAndNext(
				session, patcherAccount, companyId, accountEntryCode,
				orderByComparator, true);

			array[1] = patcherAccount;

			array[2] = filterGetByC_LikeA_PrevAndNext(
				session, patcherAccount, companyId, accountEntryCode,
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

	protected PatcherAccount filterGetByC_LikeA_PrevAndNext(
		Session session, PatcherAccount patcherAccount, long companyId,
		String accountEntryCode,
		OrderByComparator<PatcherAccount> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_PATCHERACCOUNT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERACCOUNT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_LIKEA_COMPANYID_2);

		boolean bindAccountEntryCode = false;

		if (accountEntryCode.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_LIKEA_ACCOUNTENTRYCODE_3);
		}
		else {
			bindAccountEntryCode = true;

			sb.append(_FINDER_COLUMN_C_LIKEA_ACCOUNTENTRYCODE_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERACCOUNT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherAccountModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherAccountModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherAccount.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherAccountImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherAccountImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(companyId);

		if (bindAccountEntryCode) {
			queryPos.add(accountEntryCode);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						patcherAccount)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherAccount> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the patcher accounts where companyId = &#63; and accountEntryCode LIKE &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param accountEntryCode the account entry code
	 */
	@Override
	public void removeByC_LikeA(long companyId, String accountEntryCode) {
		for (PatcherAccount patcherAccount :
				findByC_LikeA(
					companyId, accountEntryCode, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(patcherAccount);
		}
	}

	/**
	 * Returns the number of patcher accounts where companyId = &#63; and accountEntryCode LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryCode the account entry code
	 * @return the number of matching patcher accounts
	 */
	@Override
	public int countByC_LikeA(long companyId, String accountEntryCode) {
		accountEntryCode = Objects.toString(accountEntryCode, "");

		FinderPath finderPath = _finderPathWithPaginationCountByC_LikeA;

		Object[] finderArgs = new Object[] {companyId, accountEntryCode};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_PATCHERACCOUNT_WHERE);

			sb.append(_FINDER_COLUMN_C_LIKEA_COMPANYID_2);

			boolean bindAccountEntryCode = false;

			if (accountEntryCode.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_LIKEA_ACCOUNTENTRYCODE_3);
			}
			else {
				bindAccountEntryCode = true;

				sb.append(_FINDER_COLUMN_C_LIKEA_ACCOUNTENTRYCODE_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				if (bindAccountEntryCode) {
					queryPos.add(accountEntryCode);
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
	 * Returns the number of patcher accounts that the user has permission to view where companyId = &#63; and accountEntryCode LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryCode the account entry code
	 * @return the number of matching patcher accounts that the user has permission to view
	 */
	@Override
	public int filterCountByC_LikeA(long companyId, String accountEntryCode) {
		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByC_LikeA(companyId, accountEntryCode);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PatcherAccount> patcherAccounts = findByC_LikeA(
				companyId, accountEntryCode);

			patcherAccounts = InlineSQLHelperUtil.filter(patcherAccounts);

			return patcherAccounts.size();
		}

		accountEntryCode = Objects.toString(accountEntryCode, "");

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_PATCHERACCOUNT_WHERE);

		sb.append(_FINDER_COLUMN_C_LIKEA_COMPANYID_2);

		boolean bindAccountEntryCode = false;

		if (accountEntryCode.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_LIKEA_ACCOUNTENTRYCODE_3);
		}
		else {
			bindAccountEntryCode = true;

			sb.append(_FINDER_COLUMN_C_LIKEA_ACCOUNTENTRYCODE_2);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherAccount.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			if (bindAccountEntryCode) {
				queryPos.add(accountEntryCode);
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

	private static final String _FINDER_COLUMN_C_LIKEA_COMPANYID_2 =
		"patcherAccount.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_LIKEA_ACCOUNTENTRYCODE_2 =
		"patcherAccount.accountEntryCode LIKE ?";

	private static final String _FINDER_COLUMN_C_LIKEA_ACCOUNTENTRYCODE_3 =
		"(patcherAccount.accountEntryCode IS NULL OR patcherAccount.accountEntryCode LIKE '')";

	public PatcherAccountPersistenceImpl() {
		setModelClass(PatcherAccount.class);

		setModelImplClass(PatcherAccountImpl.class);
		setModelPKClass(long.class);

		setTable(PatcherAccountTable.INSTANCE);
	}

	/**
	 * Caches the patcher account in the entity cache if it is enabled.
	 *
	 * @param patcherAccount the patcher account
	 */
	@Override
	public void cacheResult(PatcherAccount patcherAccount) {
		entityCache.putResult(
			PatcherAccountImpl.class, patcherAccount.getPrimaryKey(),
			patcherAccount);

		finderCache.putResult(
			_finderPathFetchByAccountEntryCode,
			new Object[] {patcherAccount.getAccountEntryCode()},
			patcherAccount);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the patcher accounts in the entity cache if it is enabled.
	 *
	 * @param patcherAccounts the patcher accounts
	 */
	@Override
	public void cacheResult(List<PatcherAccount> patcherAccounts) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (patcherAccounts.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (PatcherAccount patcherAccount : patcherAccounts) {
			if (entityCache.getResult(
					PatcherAccountImpl.class, patcherAccount.getPrimaryKey()) ==
						null) {

				cacheResult(patcherAccount);
			}
		}
	}

	/**
	 * Clears the cache for all patcher accounts.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(PatcherAccountImpl.class);

		finderCache.clearCache(PatcherAccountImpl.class);
	}

	/**
	 * Clears the cache for the patcher account.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(PatcherAccount patcherAccount) {
		entityCache.removeResult(PatcherAccountImpl.class, patcherAccount);
	}

	@Override
	public void clearCache(List<PatcherAccount> patcherAccounts) {
		for (PatcherAccount patcherAccount : patcherAccounts) {
			entityCache.removeResult(PatcherAccountImpl.class, patcherAccount);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(PatcherAccountImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(PatcherAccountImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		PatcherAccountModelImpl patcherAccountModelImpl) {

		Object[] args = new Object[] {
			patcherAccountModelImpl.getAccountEntryCode()
		};

		finderCache.putResult(
			_finderPathFetchByAccountEntryCode, args, patcherAccountModelImpl);
	}

	/**
	 * Creates a new patcher account with the primary key. Does not add the patcher account to the database.
	 *
	 * @param patcherAccountId the primary key for the new patcher account
	 * @return the new patcher account
	 */
	@Override
	public PatcherAccount create(long patcherAccountId) {
		PatcherAccount patcherAccount = new PatcherAccountImpl();

		patcherAccount.setNew(true);
		patcherAccount.setPrimaryKey(patcherAccountId);

		patcherAccount.setCompanyId(CompanyThreadLocal.getCompanyId());

		return patcherAccount;
	}

	/**
	 * Removes the patcher account with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherAccountId the primary key of the patcher account
	 * @return the patcher account that was removed
	 * @throws NoSuchPatcherAccountException if a patcher account with the primary key could not be found
	 */
	@Override
	public PatcherAccount remove(long patcherAccountId)
		throws NoSuchPatcherAccountException {

		return remove((Serializable)patcherAccountId);
	}

	/**
	 * Removes the patcher account with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the patcher account
	 * @return the patcher account that was removed
	 * @throws NoSuchPatcherAccountException if a patcher account with the primary key could not be found
	 */
	@Override
	public PatcherAccount remove(Serializable primaryKey)
		throws NoSuchPatcherAccountException {

		Session session = null;

		try {
			session = openSession();

			PatcherAccount patcherAccount = (PatcherAccount)session.get(
				PatcherAccountImpl.class, primaryKey);

			if (patcherAccount == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchPatcherAccountException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(patcherAccount);
		}
		catch (NoSuchPatcherAccountException noSuchEntityException) {
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
	protected PatcherAccount removeImpl(PatcherAccount patcherAccount) {
		patcherAccountToPatcherBuildTableMapper.
			deleteLeftPrimaryKeyTableMappings(patcherAccount.getPrimaryKey());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(patcherAccount)) {
				patcherAccount = (PatcherAccount)session.get(
					PatcherAccountImpl.class,
					patcherAccount.getPrimaryKeyObj());
			}

			if (patcherAccount != null) {
				session.delete(patcherAccount);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (patcherAccount != null) {
			clearCache(patcherAccount);
		}

		return patcherAccount;
	}

	@Override
	public PatcherAccount updateImpl(PatcherAccount patcherAccount) {
		boolean isNew = patcherAccount.isNew();

		if (!(patcherAccount instanceof PatcherAccountModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(patcherAccount.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					patcherAccount);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in patcherAccount proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom PatcherAccount implementation " +
					patcherAccount.getClass());
		}

		PatcherAccountModelImpl patcherAccountModelImpl =
			(PatcherAccountModelImpl)patcherAccount;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (patcherAccount.getCreateDate() == null)) {
			if (serviceContext == null) {
				patcherAccount.setCreateDate(date);
			}
			else {
				patcherAccount.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!patcherAccountModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				patcherAccount.setModifiedDate(date);
			}
			else {
				patcherAccount.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(patcherAccount);
			}
			else {
				patcherAccount = (PatcherAccount)session.merge(patcherAccount);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			PatcherAccountImpl.class, patcherAccountModelImpl, false, true);

		cacheUniqueFindersCache(patcherAccountModelImpl);

		if (isNew) {
			patcherAccount.setNew(false);
		}

		patcherAccount.resetOriginalValues();

		return patcherAccount;
	}

	/**
	 * Returns the patcher account with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the patcher account
	 * @return the patcher account
	 * @throws NoSuchPatcherAccountException if a patcher account with the primary key could not be found
	 */
	@Override
	public PatcherAccount findByPrimaryKey(Serializable primaryKey)
		throws NoSuchPatcherAccountException {

		PatcherAccount patcherAccount = fetchByPrimaryKey(primaryKey);

		if (patcherAccount == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchPatcherAccountException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return patcherAccount;
	}

	/**
	 * Returns the patcher account with the primary key or throws a <code>NoSuchPatcherAccountException</code> if it could not be found.
	 *
	 * @param patcherAccountId the primary key of the patcher account
	 * @return the patcher account
	 * @throws NoSuchPatcherAccountException if a patcher account with the primary key could not be found
	 */
	@Override
	public PatcherAccount findByPrimaryKey(long patcherAccountId)
		throws NoSuchPatcherAccountException {

		return findByPrimaryKey((Serializable)patcherAccountId);
	}

	/**
	 * Returns the patcher account with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherAccountId the primary key of the patcher account
	 * @return the patcher account, or <code>null</code> if a patcher account with the primary key could not be found
	 */
	@Override
	public PatcherAccount fetchByPrimaryKey(long patcherAccountId) {
		return fetchByPrimaryKey((Serializable)patcherAccountId);
	}

	/**
	 * Returns all the patcher accounts.
	 *
	 * @return the patcher accounts
	 */
	@Override
	public List<PatcherAccount> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher accounts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherAccountModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @return the range of patcher accounts
	 */
	@Override
	public List<PatcherAccount> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher accounts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherAccountModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher accounts
	 */
	@Override
	public List<PatcherAccount> findAll(
		int start, int end,
		OrderByComparator<PatcherAccount> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher accounts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherAccountModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of patcher accounts
	 */
	@Override
	public List<PatcherAccount> findAll(
		int start, int end, OrderByComparator<PatcherAccount> orderByComparator,
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

		List<PatcherAccount> list = null;

		if (useFinderCache) {
			list = (List<PatcherAccount>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_PATCHERACCOUNT);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_PATCHERACCOUNT;

				sql = sql.concat(PatcherAccountModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<PatcherAccount>)QueryUtil.list(
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
	 * Removes all the patcher accounts from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (PatcherAccount patcherAccount : findAll()) {
			remove(patcherAccount);
		}
	}

	/**
	 * Returns the number of patcher accounts.
	 *
	 * @return the number of patcher accounts
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_PATCHERACCOUNT);

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

	/**
	 * Returns the primaryKeys of patcher builds associated with the patcher account.
	 *
	 * @param pk the primary key of the patcher account
	 * @return long[] of the primaryKeys of patcher builds associated with the patcher account
	 */
	@Override
	public long[] getPatcherBuildPrimaryKeys(long pk) {
		long[] pks =
			patcherAccountToPatcherBuildTableMapper.getRightPrimaryKeys(pk);

		return pks.clone();
	}

	/**
	 * Returns all the patcher account associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @return the patcher accounts associated with the patcher build
	 */
	@Override
	public List<PatcherAccount> getPatcherBuildPatcherAccounts(long pk) {
		return getPatcherBuildPatcherAccounts(
			pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns all the patcher account associated with the patcher build.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherAccountModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of patcher accounts associated with the patcher build
	 */
	@Override
	public List<PatcherAccount> getPatcherBuildPatcherAccounts(
		long pk, int start, int end) {

		return getPatcherBuildPatcherAccounts(pk, start, end, null);
	}

	/**
	 * Returns all the patcher account associated with the patcher build.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherAccountModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher accounts associated with the patcher build
	 */
	@Override
	public List<PatcherAccount> getPatcherBuildPatcherAccounts(
		long pk, int start, int end,
		OrderByComparator<PatcherAccount> orderByComparator) {

		return patcherAccountToPatcherBuildTableMapper.getLeftBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of patcher builds associated with the patcher account.
	 *
	 * @param pk the primary key of the patcher account
	 * @return the number of patcher builds associated with the patcher account
	 */
	@Override
	public int getPatcherBuildsSize(long pk) {
		long[] pks =
			patcherAccountToPatcherBuildTableMapper.getRightPrimaryKeys(pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the patcher build is associated with the patcher account.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuildPK the primary key of the patcher build
	 * @return <code>true</code> if the patcher build is associated with the patcher account; <code>false</code> otherwise
	 */
	@Override
	public boolean containsPatcherBuild(long pk, long patcherBuildPK) {
		return patcherAccountToPatcherBuildTableMapper.containsTableMapping(
			pk, patcherBuildPK);
	}

	/**
	 * Returns <code>true</code> if the patcher account has any patcher builds associated with it.
	 *
	 * @param pk the primary key of the patcher account to check for associations with patcher builds
	 * @return <code>true</code> if the patcher account has any patcher builds associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsPatcherBuilds(long pk) {
		if (getPatcherBuildsSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the patcher account and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuildPK the primary key of the patcher build
	 * @return <code>true</code> if an association between the patcher account and the patcher build was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addPatcherBuild(long pk, long patcherBuildPK) {
		PatcherAccount patcherAccount = fetchByPrimaryKey(pk);

		if (patcherAccount == null) {
			return patcherAccountToPatcherBuildTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, patcherBuildPK);
		}
		else {
			return patcherAccountToPatcherBuildTableMapper.addTableMapping(
				patcherAccount.getCompanyId(), pk, patcherBuildPK);
		}
	}

	/**
	 * Adds an association between the patcher account and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuild the patcher build
	 * @return <code>true</code> if an association between the patcher account and the patcher build was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addPatcherBuild(long pk, PatcherBuild patcherBuild) {
		PatcherAccount patcherAccount = fetchByPrimaryKey(pk);

		if (patcherAccount == null) {
			return patcherAccountToPatcherBuildTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk,
				patcherBuild.getPrimaryKey());
		}
		else {
			return patcherAccountToPatcherBuildTableMapper.addTableMapping(
				patcherAccount.getCompanyId(), pk,
				patcherBuild.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the patcher account and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuildPKs the primary keys of the patcher builds
	 * @return <code>true</code> if at least one association between the patcher account and the patcher builds was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addPatcherBuilds(long pk, long[] patcherBuildPKs) {
		long companyId = 0;

		PatcherAccount patcherAccount = fetchByPrimaryKey(pk);

		if (patcherAccount == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = patcherAccount.getCompanyId();
		}

		long[] addedKeys =
			patcherAccountToPatcherBuildTableMapper.addTableMappings(
				companyId, pk, patcherBuildPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the patcher account and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuilds the patcher builds
	 * @return <code>true</code> if at least one association between the patcher account and the patcher builds was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addPatcherBuilds(long pk, List<PatcherBuild> patcherBuilds) {
		return addPatcherBuilds(
			pk,
			ListUtil.toLongArray(
				patcherBuilds, PatcherBuild.PATCHER_BUILD_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the patcher account and its patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account to clear the associated patcher builds from
	 */
	@Override
	public void clearPatcherBuilds(long pk) {
		patcherAccountToPatcherBuildTableMapper.
			deleteLeftPrimaryKeyTableMappings(pk);
	}

	/**
	 * Removes the association between the patcher account and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuildPK the primary key of the patcher build
	 */
	@Override
	public void removePatcherBuild(long pk, long patcherBuildPK) {
		patcherAccountToPatcherBuildTableMapper.deleteTableMapping(
			pk, patcherBuildPK);
	}

	/**
	 * Removes the association between the patcher account and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuild the patcher build
	 */
	@Override
	public void removePatcherBuild(long pk, PatcherBuild patcherBuild) {
		patcherAccountToPatcherBuildTableMapper.deleteTableMapping(
			pk, patcherBuild.getPrimaryKey());
	}

	/**
	 * Removes the association between the patcher account and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuildPKs the primary keys of the patcher builds
	 */
	@Override
	public void removePatcherBuilds(long pk, long[] patcherBuildPKs) {
		patcherAccountToPatcherBuildTableMapper.deleteTableMappings(
			pk, patcherBuildPKs);
	}

	/**
	 * Removes the association between the patcher account and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuilds the patcher builds
	 */
	@Override
	public void removePatcherBuilds(long pk, List<PatcherBuild> patcherBuilds) {
		removePatcherBuilds(
			pk,
			ListUtil.toLongArray(
				patcherBuilds, PatcherBuild.PATCHER_BUILD_ID_ACCESSOR));
	}

	/**
	 * Sets the patcher builds associated with the patcher account, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuildPKs the primary keys of the patcher builds to be associated with the patcher account
	 */
	@Override
	public void setPatcherBuilds(long pk, long[] patcherBuildPKs) {
		Set<Long> newPatcherBuildPKsSet = SetUtil.fromArray(patcherBuildPKs);
		Set<Long> oldPatcherBuildPKsSet = SetUtil.fromArray(
			patcherAccountToPatcherBuildTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removePatcherBuildPKsSet = new HashSet<Long>(
			oldPatcherBuildPKsSet);

		removePatcherBuildPKsSet.removeAll(newPatcherBuildPKsSet);

		patcherAccountToPatcherBuildTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removePatcherBuildPKsSet));

		newPatcherBuildPKsSet.removeAll(oldPatcherBuildPKsSet);

		long companyId = 0;

		PatcherAccount patcherAccount = fetchByPrimaryKey(pk);

		if (patcherAccount == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = patcherAccount.getCompanyId();
		}

		patcherAccountToPatcherBuildTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newPatcherBuildPKsSet));
	}

	/**
	 * Sets the patcher builds associated with the patcher account, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuilds the patcher builds to be associated with the patcher account
	 */
	@Override
	public void setPatcherBuilds(long pk, List<PatcherBuild> patcherBuilds) {
		try {
			long[] patcherBuildPKs = new long[patcherBuilds.size()];

			for (int i = 0; i < patcherBuilds.size(); i++) {
				PatcherBuild patcherBuild = patcherBuilds.get(i);

				patcherBuildPKs[i] = patcherBuild.getPrimaryKey();
			}

			setPatcherBuilds(pk, patcherBuildPKs);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "patcherAccountId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PATCHERACCOUNT;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return PatcherAccountModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the patcher account persistence.
	 */
	@Activate
	public void activate() {
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		patcherAccountToPatcherBuildTableMapper =
			TableMapperFactory.getTableMapper(
				"OSBPatcher_PAccounts_PBuilds#patcherAccountId",
				"OSBPatcher_PAccounts_PBuilds", "companyId", "patcherAccountId",
				"patcherBuildId", this, PatcherBuild.class);

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

		_finderPathFetchByAccountEntryCode = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByAccountEntryCode",
			new String[] {String.class.getName()},
			new String[] {"accountEntryCode"}, true);

		_finderPathWithPaginationFindByC_LikeA = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_LikeA",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "accountEntryCode"}, true);

		_finderPathWithPaginationCountByC_LikeA = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_LikeA",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "accountEntryCode"}, false);

		PatcherAccountUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		PatcherAccountUtil.setPersistence(null);

		entityCache.removeCache(PatcherAccountImpl.class.getName());

		TableMapperFactory.removeTableMapper(
			"OSBPatcher_PAccounts_PBuilds#patcherAccountId");
	}

	@Override
	@Reference(
		target = OSBPatcherPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = OSBPatcherPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = OSBPatcherPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	protected TableMapper<PatcherAccount, PatcherBuild>
		patcherAccountToPatcherBuildTableMapper;

	private static final String _SQL_SELECT_PATCHERACCOUNT =
		"SELECT patcherAccount FROM PatcherAccount patcherAccount";

	private static final String _SQL_SELECT_PATCHERACCOUNT_WHERE =
		"SELECT patcherAccount FROM PatcherAccount patcherAccount WHERE ";

	private static final String _SQL_COUNT_PATCHERACCOUNT =
		"SELECT COUNT(patcherAccount) FROM PatcherAccount patcherAccount";

	private static final String _SQL_COUNT_PATCHERACCOUNT_WHERE =
		"SELECT COUNT(patcherAccount) FROM PatcherAccount patcherAccount WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"patcherAccount.patcherAccountId";

	private static final String _FILTER_SQL_SELECT_PATCHERACCOUNT_WHERE =
		"SELECT DISTINCT {patcherAccount.*} FROM OSBPatcher_PatcherAccount patcherAccount WHERE ";

	private static final String
		_FILTER_SQL_SELECT_PATCHERACCOUNT_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {OSBPatcher_PatcherAccount.*} FROM (SELECT DISTINCT patcherAccount.patcherAccountId FROM OSBPatcher_PatcherAccount patcherAccount WHERE ";

	private static final String
		_FILTER_SQL_SELECT_PATCHERACCOUNT_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN OSBPatcher_PatcherAccount ON TEMP_TABLE.patcherAccountId = OSBPatcher_PatcherAccount.patcherAccountId";

	private static final String _FILTER_SQL_COUNT_PATCHERACCOUNT_WHERE =
		"SELECT COUNT(DISTINCT patcherAccount.patcherAccountId) AS COUNT_VALUE FROM OSBPatcher_PatcherAccount patcherAccount WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "patcherAccount";

	private static final String _FILTER_ENTITY_TABLE =
		"OSBPatcher_PatcherAccount";

	private static final String _ORDER_BY_ENTITY_ALIAS = "patcherAccount.";

	private static final String _ORDER_BY_ENTITY_TABLE =
		"OSBPatcher_PatcherAccount.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No PatcherAccount exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No PatcherAccount exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		PatcherAccountPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}