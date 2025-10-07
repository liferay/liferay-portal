/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.service.persistence.impl;

import com.liferay.commerce.payment.exception.DuplicateCommercePaymentEntryExternalReferenceCodeException;
import com.liferay.commerce.payment.exception.NoSuchPaymentEntryException;
import com.liferay.commerce.payment.model.CommercePaymentEntry;
import com.liferay.commerce.payment.model.CommercePaymentEntryTable;
import com.liferay.commerce.payment.model.impl.CommercePaymentEntryImpl;
import com.liferay.commerce.payment.model.impl.CommercePaymentEntryModelImpl;
import com.liferay.commerce.payment.service.persistence.CommercePaymentEntryPersistence;
import com.liferay.commerce.payment.service.persistence.CommercePaymentEntryUtil;
import com.liferay.commerce.payment.service.persistence.impl.constants.CommercePersistenceConstants;
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
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.HashMap;
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
 * The persistence implementation for the commerce payment entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Luca Pellizzon
 * @generated
 */
@Component(service = CommercePaymentEntryPersistence.class)
public class CommercePaymentEntryPersistenceImpl
	extends BasePersistenceImpl<CommercePaymentEntry>
	implements CommercePaymentEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommercePaymentEntryUtil</code> to access the commerce payment entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommercePaymentEntryImpl.class.getName();

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
	 * Returns all the commerce payment entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching commerce payment entries
	 */
	@Override
	public List<CommercePaymentEntry> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce payment entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce payment entries
	 * @param end the upper bound of the range of commerce payment entries (not inclusive)
	 * @return the range of matching commerce payment entries
	 */
	@Override
	public List<CommercePaymentEntry> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce payment entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce payment entries
	 * @param end the upper bound of the range of commerce payment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce payment entries
	 */
	@Override
	public List<CommercePaymentEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CommercePaymentEntry> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce payment entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce payment entries
	 * @param end the upper bound of the range of commerce payment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce payment entries
	 */
	@Override
	public List<CommercePaymentEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CommercePaymentEntry> orderByComparator,
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

		List<CommercePaymentEntry> list = null;

		if (useFinderCache) {
			list = (List<CommercePaymentEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommercePaymentEntry commercePaymentEntry : list) {
					if (companyId != commercePaymentEntry.getCompanyId()) {
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

			sb.append(_SQL_SELECT_COMMERCEPAYMENTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CommercePaymentEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				list = (List<CommercePaymentEntry>)QueryUtil.list(
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
	 * Returns the first commerce payment entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce payment entry
	 * @throws NoSuchPaymentEntryException if a matching commerce payment entry could not be found
	 */
	@Override
	public CommercePaymentEntry findByCompanyId_First(
			long companyId,
			OrderByComparator<CommercePaymentEntry> orderByComparator)
		throws NoSuchPaymentEntryException {

		CommercePaymentEntry commercePaymentEntry = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (commercePaymentEntry != null) {
			return commercePaymentEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchPaymentEntryException(sb.toString());
	}

	/**
	 * Returns the first commerce payment entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce payment entry, or <code>null</code> if a matching commerce payment entry could not be found
	 */
	@Override
	public CommercePaymentEntry fetchByCompanyId_First(
		long companyId,
		OrderByComparator<CommercePaymentEntry> orderByComparator) {

		List<CommercePaymentEntry> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce payment entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce payment entry
	 * @throws NoSuchPaymentEntryException if a matching commerce payment entry could not be found
	 */
	@Override
	public CommercePaymentEntry findByCompanyId_Last(
			long companyId,
			OrderByComparator<CommercePaymentEntry> orderByComparator)
		throws NoSuchPaymentEntryException {

		CommercePaymentEntry commercePaymentEntry = fetchByCompanyId_Last(
			companyId, orderByComparator);

		if (commercePaymentEntry != null) {
			return commercePaymentEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchPaymentEntryException(sb.toString());
	}

	/**
	 * Returns the last commerce payment entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce payment entry, or <code>null</code> if a matching commerce payment entry could not be found
	 */
	@Override
	public CommercePaymentEntry fetchByCompanyId_Last(
		long companyId,
		OrderByComparator<CommercePaymentEntry> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<CommercePaymentEntry> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce payment entries before and after the current commerce payment entry in the ordered set where companyId = &#63;.
	 *
	 * @param commercePaymentEntryId the primary key of the current commerce payment entry
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce payment entry
	 * @throws NoSuchPaymentEntryException if a commerce payment entry with the primary key could not be found
	 */
	@Override
	public CommercePaymentEntry[] findByCompanyId_PrevAndNext(
			long commercePaymentEntryId, long companyId,
			OrderByComparator<CommercePaymentEntry> orderByComparator)
		throws NoSuchPaymentEntryException {

		CommercePaymentEntry commercePaymentEntry = findByPrimaryKey(
			commercePaymentEntryId);

		Session session = null;

		try {
			session = openSession();

			CommercePaymentEntry[] array = new CommercePaymentEntryImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, commercePaymentEntry, companyId, orderByComparator,
				true);

			array[1] = commercePaymentEntry;

			array[2] = getByCompanyId_PrevAndNext(
				session, commercePaymentEntry, companyId, orderByComparator,
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

	protected CommercePaymentEntry getByCompanyId_PrevAndNext(
		Session session, CommercePaymentEntry commercePaymentEntry,
		long companyId,
		OrderByComparator<CommercePaymentEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_COMMERCEPAYMENTENTRY_WHERE);

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
			sb.append(CommercePaymentEntryModelImpl.ORDER_BY_JPQL);
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
						commercePaymentEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommercePaymentEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the commerce payment entries that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching commerce payment entries that the user has permission to view
	 */
	@Override
	public List<CommercePaymentEntry> filterFindByCompanyId(long companyId) {
		return filterFindByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce payment entries that the user has permission to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce payment entries
	 * @param end the upper bound of the range of commerce payment entries (not inclusive)
	 * @return the range of matching commerce payment entries that the user has permission to view
	 */
	@Override
	public List<CommercePaymentEntry> filterFindByCompanyId(
		long companyId, int start, int end) {

		return filterFindByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce payment entries that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce payment entries
	 * @param end the upper bound of the range of commerce payment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce payment entries that the user has permission to view
	 */
	@Override
	public List<CommercePaymentEntry> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CommercePaymentEntry> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_COMMERCEPAYMENTENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEPAYMENTENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEPAYMENTENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					CommercePaymentEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommercePaymentEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommercePaymentEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, CommercePaymentEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, CommercePaymentEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			return (List<CommercePaymentEntry>)QueryUtil.list(
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
	 * Returns the commerce payment entries before and after the current commerce payment entry in the ordered set of commerce payment entries that the user has permission to view where companyId = &#63;.
	 *
	 * @param commercePaymentEntryId the primary key of the current commerce payment entry
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce payment entry
	 * @throws NoSuchPaymentEntryException if a commerce payment entry with the primary key could not be found
	 */
	@Override
	public CommercePaymentEntry[] filterFindByCompanyId_PrevAndNext(
			long commercePaymentEntryId, long companyId,
			OrderByComparator<CommercePaymentEntry> orderByComparator)
		throws NoSuchPaymentEntryException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByCompanyId_PrevAndNext(
				commercePaymentEntryId, companyId, orderByComparator);
		}

		CommercePaymentEntry commercePaymentEntry = findByPrimaryKey(
			commercePaymentEntryId);

		Session session = null;

		try {
			session = openSession();

			CommercePaymentEntry[] array = new CommercePaymentEntryImpl[3];

			array[0] = filterGetByCompanyId_PrevAndNext(
				session, commercePaymentEntry, companyId, orderByComparator,
				true);

			array[1] = commercePaymentEntry;

			array[2] = filterGetByCompanyId_PrevAndNext(
				session, commercePaymentEntry, companyId, orderByComparator,
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

	protected CommercePaymentEntry filterGetByCompanyId_PrevAndNext(
		Session session, CommercePaymentEntry commercePaymentEntry,
		long companyId,
		OrderByComparator<CommercePaymentEntry> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_COMMERCEPAYMENTENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEPAYMENTENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEPAYMENTENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					CommercePaymentEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommercePaymentEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommercePaymentEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, CommercePaymentEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, CommercePaymentEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commercePaymentEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommercePaymentEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce payment entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (CommercePaymentEntry commercePaymentEntry :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(commercePaymentEntry);
		}
	}

	/**
	 * Returns the number of commerce payment entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching commerce payment entries
	 */
	@Override
	public int countByCompanyId(long companyId) {
		FinderPath finderPath = _finderPathCountByCompanyId;

		Object[] finderArgs = new Object[] {companyId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_COMMERCEPAYMENTENTRY_WHERE);

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
	 * Returns the number of commerce payment entries that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching commerce payment entries that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByCompanyId(companyId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CommercePaymentEntry> commercePaymentEntries = findByCompanyId(
				companyId);

			commercePaymentEntries = InlineSQLHelperUtil.filter(
				commercePaymentEntries);

			return commercePaymentEntries.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_COMMERCEPAYMENTENTRY_WHERE);

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommercePaymentEntry.class.getName(),
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
		"commercePaymentEntry.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByC_C_C;
	private FinderPath _finderPathWithoutPaginationFindByC_C_C;
	private FinderPath _finderPathCountByC_C_C;

	/**
	 * Returns all the commerce payment entries where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching commerce payment entries
	 */
	@Override
	public List<CommercePaymentEntry> findByC_C_C(
		long companyId, long classNameId, long classPK) {

		return findByC_C_C(
			companyId, classNameId, classPK, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce payment entries where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of commerce payment entries
	 * @param end the upper bound of the range of commerce payment entries (not inclusive)
	 * @return the range of matching commerce payment entries
	 */
	@Override
	public List<CommercePaymentEntry> findByC_C_C(
		long companyId, long classNameId, long classPK, int start, int end) {

		return findByC_C_C(companyId, classNameId, classPK, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce payment entries where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of commerce payment entries
	 * @param end the upper bound of the range of commerce payment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce payment entries
	 */
	@Override
	public List<CommercePaymentEntry> findByC_C_C(
		long companyId, long classNameId, long classPK, int start, int end,
		OrderByComparator<CommercePaymentEntry> orderByComparator) {

		return findByC_C_C(
			companyId, classNameId, classPK, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the commerce payment entries where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of commerce payment entries
	 * @param end the upper bound of the range of commerce payment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce payment entries
	 */
	@Override
	public List<CommercePaymentEntry> findByC_C_C(
		long companyId, long classNameId, long classPK, int start, int end,
		OrderByComparator<CommercePaymentEntry> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByC_C_C;
				finderArgs = new Object[] {companyId, classNameId, classPK};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByC_C_C;
			finderArgs = new Object[] {
				companyId, classNameId, classPK, start, end, orderByComparator
			};
		}

		List<CommercePaymentEntry> list = null;

		if (useFinderCache) {
			list = (List<CommercePaymentEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommercePaymentEntry commercePaymentEntry : list) {
					if ((companyId != commercePaymentEntry.getCompanyId()) ||
						(classNameId !=
							commercePaymentEntry.getClassNameId()) ||
						(classPK != commercePaymentEntry.getClassPK())) {

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

			sb.append(_SQL_SELECT_COMMERCEPAYMENTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_C_C_C_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_C_C_CLASSNAMEID_2);

			sb.append(_FINDER_COLUMN_C_C_C_CLASSPK_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CommercePaymentEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(classNameId);

				queryPos.add(classPK);

				list = (List<CommercePaymentEntry>)QueryUtil.list(
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
	 * Returns the first commerce payment entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce payment entry
	 * @throws NoSuchPaymentEntryException if a matching commerce payment entry could not be found
	 */
	@Override
	public CommercePaymentEntry findByC_C_C_First(
			long companyId, long classNameId, long classPK,
			OrderByComparator<CommercePaymentEntry> orderByComparator)
		throws NoSuchPaymentEntryException {

		CommercePaymentEntry commercePaymentEntry = fetchByC_C_C_First(
			companyId, classNameId, classPK, orderByComparator);

		if (commercePaymentEntry != null) {
			return commercePaymentEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append("}");

		throw new NoSuchPaymentEntryException(sb.toString());
	}

	/**
	 * Returns the first commerce payment entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce payment entry, or <code>null</code> if a matching commerce payment entry could not be found
	 */
	@Override
	public CommercePaymentEntry fetchByC_C_C_First(
		long companyId, long classNameId, long classPK,
		OrderByComparator<CommercePaymentEntry> orderByComparator) {

		List<CommercePaymentEntry> list = findByC_C_C(
			companyId, classNameId, classPK, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce payment entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce payment entry
	 * @throws NoSuchPaymentEntryException if a matching commerce payment entry could not be found
	 */
	@Override
	public CommercePaymentEntry findByC_C_C_Last(
			long companyId, long classNameId, long classPK,
			OrderByComparator<CommercePaymentEntry> orderByComparator)
		throws NoSuchPaymentEntryException {

		CommercePaymentEntry commercePaymentEntry = fetchByC_C_C_Last(
			companyId, classNameId, classPK, orderByComparator);

		if (commercePaymentEntry != null) {
			return commercePaymentEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append("}");

		throw new NoSuchPaymentEntryException(sb.toString());
	}

	/**
	 * Returns the last commerce payment entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce payment entry, or <code>null</code> if a matching commerce payment entry could not be found
	 */
	@Override
	public CommercePaymentEntry fetchByC_C_C_Last(
		long companyId, long classNameId, long classPK,
		OrderByComparator<CommercePaymentEntry> orderByComparator) {

		int count = countByC_C_C(companyId, classNameId, classPK);

		if (count == 0) {
			return null;
		}

		List<CommercePaymentEntry> list = findByC_C_C(
			companyId, classNameId, classPK, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce payment entries before and after the current commerce payment entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param commercePaymentEntryId the primary key of the current commerce payment entry
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce payment entry
	 * @throws NoSuchPaymentEntryException if a commerce payment entry with the primary key could not be found
	 */
	@Override
	public CommercePaymentEntry[] findByC_C_C_PrevAndNext(
			long commercePaymentEntryId, long companyId, long classNameId,
			long classPK,
			OrderByComparator<CommercePaymentEntry> orderByComparator)
		throws NoSuchPaymentEntryException {

		CommercePaymentEntry commercePaymentEntry = findByPrimaryKey(
			commercePaymentEntryId);

		Session session = null;

		try {
			session = openSession();

			CommercePaymentEntry[] array = new CommercePaymentEntryImpl[3];

			array[0] = getByC_C_C_PrevAndNext(
				session, commercePaymentEntry, companyId, classNameId, classPK,
				orderByComparator, true);

			array[1] = commercePaymentEntry;

			array[2] = getByC_C_C_PrevAndNext(
				session, commercePaymentEntry, companyId, classNameId, classPK,
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

	protected CommercePaymentEntry getByC_C_C_PrevAndNext(
		Session session, CommercePaymentEntry commercePaymentEntry,
		long companyId, long classNameId, long classPK,
		OrderByComparator<CommercePaymentEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_COMMERCEPAYMENTENTRY_WHERE);

		sb.append(_FINDER_COLUMN_C_C_C_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_C_C_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_C_C_C_CLASSPK_2);

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
			sb.append(CommercePaymentEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(classNameId);

		queryPos.add(classPK);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commercePaymentEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommercePaymentEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the commerce payment entries that the user has permission to view where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching commerce payment entries that the user has permission to view
	 */
	@Override
	public List<CommercePaymentEntry> filterFindByC_C_C(
		long companyId, long classNameId, long classPK) {

		return filterFindByC_C_C(
			companyId, classNameId, classPK, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce payment entries that the user has permission to view where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of commerce payment entries
	 * @param end the upper bound of the range of commerce payment entries (not inclusive)
	 * @return the range of matching commerce payment entries that the user has permission to view
	 */
	@Override
	public List<CommercePaymentEntry> filterFindByC_C_C(
		long companyId, long classNameId, long classPK, int start, int end) {

		return filterFindByC_C_C(
			companyId, classNameId, classPK, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce payment entries that the user has permissions to view where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of commerce payment entries
	 * @param end the upper bound of the range of commerce payment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce payment entries that the user has permission to view
	 */
	@Override
	public List<CommercePaymentEntry> filterFindByC_C_C(
		long companyId, long classNameId, long classPK, int start, int end,
		OrderByComparator<CommercePaymentEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_C_C(
				companyId, classNameId, classPK, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByC_C_C(
					companyId, classNameId, classPK, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator));
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_COMMERCEPAYMENTENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEPAYMENTENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_C_C_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_C_C_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_C_C_C_CLASSPK_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEPAYMENTENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					CommercePaymentEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommercePaymentEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommercePaymentEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, CommercePaymentEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, CommercePaymentEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			queryPos.add(classNameId);

			queryPos.add(classPK);

			return (List<CommercePaymentEntry>)QueryUtil.list(
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
	 * Returns the commerce payment entries before and after the current commerce payment entry in the ordered set of commerce payment entries that the user has permission to view where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param commercePaymentEntryId the primary key of the current commerce payment entry
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce payment entry
	 * @throws NoSuchPaymentEntryException if a commerce payment entry with the primary key could not be found
	 */
	@Override
	public CommercePaymentEntry[] filterFindByC_C_C_PrevAndNext(
			long commercePaymentEntryId, long companyId, long classNameId,
			long classPK,
			OrderByComparator<CommercePaymentEntry> orderByComparator)
		throws NoSuchPaymentEntryException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_C_C_PrevAndNext(
				commercePaymentEntryId, companyId, classNameId, classPK,
				orderByComparator);
		}

		CommercePaymentEntry commercePaymentEntry = findByPrimaryKey(
			commercePaymentEntryId);

		Session session = null;

		try {
			session = openSession();

			CommercePaymentEntry[] array = new CommercePaymentEntryImpl[3];

			array[0] = filterGetByC_C_C_PrevAndNext(
				session, commercePaymentEntry, companyId, classNameId, classPK,
				orderByComparator, true);

			array[1] = commercePaymentEntry;

			array[2] = filterGetByC_C_C_PrevAndNext(
				session, commercePaymentEntry, companyId, classNameId, classPK,
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

	protected CommercePaymentEntry filterGetByC_C_C_PrevAndNext(
		Session session, CommercePaymentEntry commercePaymentEntry,
		long companyId, long classNameId, long classPK,
		OrderByComparator<CommercePaymentEntry> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_COMMERCEPAYMENTENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEPAYMENTENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_C_C_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_C_C_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_C_C_C_CLASSPK_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEPAYMENTENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					CommercePaymentEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommercePaymentEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommercePaymentEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, CommercePaymentEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, CommercePaymentEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(companyId);

		queryPos.add(classNameId);

		queryPos.add(classPK);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commercePaymentEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommercePaymentEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce payment entries where companyId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByC_C_C(long companyId, long classNameId, long classPK) {
		for (CommercePaymentEntry commercePaymentEntry :
				findByC_C_C(
					companyId, classNameId, classPK, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(commercePaymentEntry);
		}
	}

	/**
	 * Returns the number of commerce payment entries where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching commerce payment entries
	 */
	@Override
	public int countByC_C_C(long companyId, long classNameId, long classPK) {
		FinderPath finderPath = _finderPathCountByC_C_C;

		Object[] finderArgs = new Object[] {companyId, classNameId, classPK};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_COUNT_COMMERCEPAYMENTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_C_C_C_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_C_C_CLASSNAMEID_2);

			sb.append(_FINDER_COLUMN_C_C_C_CLASSPK_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(classNameId);

				queryPos.add(classPK);

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
	 * Returns the number of commerce payment entries that the user has permission to view where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching commerce payment entries that the user has permission to view
	 */
	@Override
	public int filterCountByC_C_C(
		long companyId, long classNameId, long classPK) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByC_C_C(companyId, classNameId, classPK);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CommercePaymentEntry> commercePaymentEntries = findByC_C_C(
				companyId, classNameId, classPK);

			commercePaymentEntries = InlineSQLHelperUtil.filter(
				commercePaymentEntries);

			return commercePaymentEntries.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_COMMERCEPAYMENTENTRY_WHERE);

		sb.append(_FINDER_COLUMN_C_C_C_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_C_C_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_C_C_C_CLASSPK_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommercePaymentEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			queryPos.add(classNameId);

			queryPos.add(classPK);

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

	private static final String _FINDER_COLUMN_C_C_C_COMPANYID_2 =
		"commercePaymentEntry.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_C_C_CLASSNAMEID_2 =
		"commercePaymentEntry.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_C_C_C_CLASSPK_2 =
		"commercePaymentEntry.classPK = ?";

	private FinderPath _finderPathWithPaginationFindByC_C_C_T;
	private FinderPath _finderPathWithoutPaginationFindByC_C_C_T;
	private FinderPath _finderPathCountByC_C_C_T;

	/**
	 * Returns all the commerce payment entries where companyId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @return the matching commerce payment entries
	 */
	@Override
	public List<CommercePaymentEntry> findByC_C_C_T(
		long companyId, long classNameId, long classPK, int type) {

		return findByC_C_C_T(
			companyId, classNameId, classPK, type, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce payment entries where companyId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param start the lower bound of the range of commerce payment entries
	 * @param end the upper bound of the range of commerce payment entries (not inclusive)
	 * @return the range of matching commerce payment entries
	 */
	@Override
	public List<CommercePaymentEntry> findByC_C_C_T(
		long companyId, long classNameId, long classPK, int type, int start,
		int end) {

		return findByC_C_C_T(
			companyId, classNameId, classPK, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce payment entries where companyId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param start the lower bound of the range of commerce payment entries
	 * @param end the upper bound of the range of commerce payment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce payment entries
	 */
	@Override
	public List<CommercePaymentEntry> findByC_C_C_T(
		long companyId, long classNameId, long classPK, int type, int start,
		int end, OrderByComparator<CommercePaymentEntry> orderByComparator) {

		return findByC_C_C_T(
			companyId, classNameId, classPK, type, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce payment entries where companyId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param start the lower bound of the range of commerce payment entries
	 * @param end the upper bound of the range of commerce payment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce payment entries
	 */
	@Override
	public List<CommercePaymentEntry> findByC_C_C_T(
		long companyId, long classNameId, long classPK, int type, int start,
		int end, OrderByComparator<CommercePaymentEntry> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByC_C_C_T;
				finderArgs = new Object[] {
					companyId, classNameId, classPK, type
				};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByC_C_C_T;
			finderArgs = new Object[] {
				companyId, classNameId, classPK, type, start, end,
				orderByComparator
			};
		}

		List<CommercePaymentEntry> list = null;

		if (useFinderCache) {
			list = (List<CommercePaymentEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommercePaymentEntry commercePaymentEntry : list) {
					if ((companyId != commercePaymentEntry.getCompanyId()) ||
						(classNameId !=
							commercePaymentEntry.getClassNameId()) ||
						(classPK != commercePaymentEntry.getClassPK()) ||
						(type != commercePaymentEntry.getType())) {

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
					6 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				sb = new StringBundler(6);
			}

			sb.append(_SQL_SELECT_COMMERCEPAYMENTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_C_C_C_T_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_C_C_T_CLASSNAMEID_2);

			sb.append(_FINDER_COLUMN_C_C_C_T_CLASSPK_2);

			sb.append(_FINDER_COLUMN_C_C_C_T_TYPE_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CommercePaymentEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(classNameId);

				queryPos.add(classPK);

				queryPos.add(type);

				list = (List<CommercePaymentEntry>)QueryUtil.list(
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
	 * Returns the first commerce payment entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce payment entry
	 * @throws NoSuchPaymentEntryException if a matching commerce payment entry could not be found
	 */
	@Override
	public CommercePaymentEntry findByC_C_C_T_First(
			long companyId, long classNameId, long classPK, int type,
			OrderByComparator<CommercePaymentEntry> orderByComparator)
		throws NoSuchPaymentEntryException {

		CommercePaymentEntry commercePaymentEntry = fetchByC_C_C_T_First(
			companyId, classNameId, classPK, type, orderByComparator);

		if (commercePaymentEntry != null) {
			return commercePaymentEntry;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchPaymentEntryException(sb.toString());
	}

	/**
	 * Returns the first commerce payment entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce payment entry, or <code>null</code> if a matching commerce payment entry could not be found
	 */
	@Override
	public CommercePaymentEntry fetchByC_C_C_T_First(
		long companyId, long classNameId, long classPK, int type,
		OrderByComparator<CommercePaymentEntry> orderByComparator) {

		List<CommercePaymentEntry> list = findByC_C_C_T(
			companyId, classNameId, classPK, type, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce payment entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce payment entry
	 * @throws NoSuchPaymentEntryException if a matching commerce payment entry could not be found
	 */
	@Override
	public CommercePaymentEntry findByC_C_C_T_Last(
			long companyId, long classNameId, long classPK, int type,
			OrderByComparator<CommercePaymentEntry> orderByComparator)
		throws NoSuchPaymentEntryException {

		CommercePaymentEntry commercePaymentEntry = fetchByC_C_C_T_Last(
			companyId, classNameId, classPK, type, orderByComparator);

		if (commercePaymentEntry != null) {
			return commercePaymentEntry;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchPaymentEntryException(sb.toString());
	}

	/**
	 * Returns the last commerce payment entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce payment entry, or <code>null</code> if a matching commerce payment entry could not be found
	 */
	@Override
	public CommercePaymentEntry fetchByC_C_C_T_Last(
		long companyId, long classNameId, long classPK, int type,
		OrderByComparator<CommercePaymentEntry> orderByComparator) {

		int count = countByC_C_C_T(companyId, classNameId, classPK, type);

		if (count == 0) {
			return null;
		}

		List<CommercePaymentEntry> list = findByC_C_C_T(
			companyId, classNameId, classPK, type, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce payment entries before and after the current commerce payment entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param commercePaymentEntryId the primary key of the current commerce payment entry
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce payment entry
	 * @throws NoSuchPaymentEntryException if a commerce payment entry with the primary key could not be found
	 */
	@Override
	public CommercePaymentEntry[] findByC_C_C_T_PrevAndNext(
			long commercePaymentEntryId, long companyId, long classNameId,
			long classPK, int type,
			OrderByComparator<CommercePaymentEntry> orderByComparator)
		throws NoSuchPaymentEntryException {

		CommercePaymentEntry commercePaymentEntry = findByPrimaryKey(
			commercePaymentEntryId);

		Session session = null;

		try {
			session = openSession();

			CommercePaymentEntry[] array = new CommercePaymentEntryImpl[3];

			array[0] = getByC_C_C_T_PrevAndNext(
				session, commercePaymentEntry, companyId, classNameId, classPK,
				type, orderByComparator, true);

			array[1] = commercePaymentEntry;

			array[2] = getByC_C_C_T_PrevAndNext(
				session, commercePaymentEntry, companyId, classNameId, classPK,
				type, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CommercePaymentEntry getByC_C_C_T_PrevAndNext(
		Session session, CommercePaymentEntry commercePaymentEntry,
		long companyId, long classNameId, long classPK, int type,
		OrderByComparator<CommercePaymentEntry> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		sb.append(_SQL_SELECT_COMMERCEPAYMENTENTRY_WHERE);

		sb.append(_FINDER_COLUMN_C_C_C_T_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_C_C_T_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_C_C_C_T_CLASSPK_2);

		sb.append(_FINDER_COLUMN_C_C_C_T_TYPE_2);

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
			sb.append(CommercePaymentEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(classNameId);

		queryPos.add(classPK);

		queryPos.add(type);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commercePaymentEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommercePaymentEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the commerce payment entries that the user has permission to view where companyId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @return the matching commerce payment entries that the user has permission to view
	 */
	@Override
	public List<CommercePaymentEntry> filterFindByC_C_C_T(
		long companyId, long classNameId, long classPK, int type) {

		return filterFindByC_C_C_T(
			companyId, classNameId, classPK, type, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce payment entries that the user has permission to view where companyId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param start the lower bound of the range of commerce payment entries
	 * @param end the upper bound of the range of commerce payment entries (not inclusive)
	 * @return the range of matching commerce payment entries that the user has permission to view
	 */
	@Override
	public List<CommercePaymentEntry> filterFindByC_C_C_T(
		long companyId, long classNameId, long classPK, int type, int start,
		int end) {

		return filterFindByC_C_C_T(
			companyId, classNameId, classPK, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce payment entries that the user has permissions to view where companyId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param start the lower bound of the range of commerce payment entries
	 * @param end the upper bound of the range of commerce payment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce payment entries that the user has permission to view
	 */
	@Override
	public List<CommercePaymentEntry> filterFindByC_C_C_T(
		long companyId, long classNameId, long classPK, int type, int start,
		int end, OrderByComparator<CommercePaymentEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_C_C_T(
				companyId, classNameId, classPK, type, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByC_C_C_T(
					companyId, classNameId, classPK, type, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator));
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(7);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_COMMERCEPAYMENTENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEPAYMENTENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_C_C_T_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_C_C_T_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_C_C_C_T_CLASSPK_2);

		sb.append(_FINDER_COLUMN_C_C_C_T_TYPE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEPAYMENTENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					CommercePaymentEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommercePaymentEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommercePaymentEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, CommercePaymentEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, CommercePaymentEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			queryPos.add(classNameId);

			queryPos.add(classPK);

			queryPos.add(type);

			return (List<CommercePaymentEntry>)QueryUtil.list(
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
	 * Returns the commerce payment entries before and after the current commerce payment entry in the ordered set of commerce payment entries that the user has permission to view where companyId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param commercePaymentEntryId the primary key of the current commerce payment entry
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce payment entry
	 * @throws NoSuchPaymentEntryException if a commerce payment entry with the primary key could not be found
	 */
	@Override
	public CommercePaymentEntry[] filterFindByC_C_C_T_PrevAndNext(
			long commercePaymentEntryId, long companyId, long classNameId,
			long classPK, int type,
			OrderByComparator<CommercePaymentEntry> orderByComparator)
		throws NoSuchPaymentEntryException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_C_C_T_PrevAndNext(
				commercePaymentEntryId, companyId, classNameId, classPK, type,
				orderByComparator);
		}

		CommercePaymentEntry commercePaymentEntry = findByPrimaryKey(
			commercePaymentEntryId);

		Session session = null;

		try {
			session = openSession();

			CommercePaymentEntry[] array = new CommercePaymentEntryImpl[3];

			array[0] = filterGetByC_C_C_T_PrevAndNext(
				session, commercePaymentEntry, companyId, classNameId, classPK,
				type, orderByComparator, true);

			array[1] = commercePaymentEntry;

			array[2] = filterGetByC_C_C_T_PrevAndNext(
				session, commercePaymentEntry, companyId, classNameId, classPK,
				type, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CommercePaymentEntry filterGetByC_C_C_T_PrevAndNext(
		Session session, CommercePaymentEntry commercePaymentEntry,
		long companyId, long classNameId, long classPK, int type,
		OrderByComparator<CommercePaymentEntry> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				8 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(7);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_COMMERCEPAYMENTENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEPAYMENTENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_C_C_T_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_C_C_T_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_C_C_C_T_CLASSPK_2);

		sb.append(_FINDER_COLUMN_C_C_C_T_TYPE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEPAYMENTENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					CommercePaymentEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommercePaymentEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommercePaymentEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, CommercePaymentEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, CommercePaymentEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(companyId);

		queryPos.add(classNameId);

		queryPos.add(classPK);

		queryPos.add(type);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commercePaymentEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommercePaymentEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce payment entries where companyId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 */
	@Override
	public void removeByC_C_C_T(
		long companyId, long classNameId, long classPK, int type) {

		for (CommercePaymentEntry commercePaymentEntry :
				findByC_C_C_T(
					companyId, classNameId, classPK, type, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(commercePaymentEntry);
		}
	}

	/**
	 * Returns the number of commerce payment entries where companyId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @return the number of matching commerce payment entries
	 */
	@Override
	public int countByC_C_C_T(
		long companyId, long classNameId, long classPK, int type) {

		FinderPath finderPath = _finderPathCountByC_C_C_T;

		Object[] finderArgs = new Object[] {
			companyId, classNameId, classPK, type
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(5);

			sb.append(_SQL_COUNT_COMMERCEPAYMENTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_C_C_C_T_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_C_C_T_CLASSNAMEID_2);

			sb.append(_FINDER_COLUMN_C_C_C_T_CLASSPK_2);

			sb.append(_FINDER_COLUMN_C_C_C_T_TYPE_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(classNameId);

				queryPos.add(classPK);

				queryPos.add(type);

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
	 * Returns the number of commerce payment entries that the user has permission to view where companyId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @return the number of matching commerce payment entries that the user has permission to view
	 */
	@Override
	public int filterCountByC_C_C_T(
		long companyId, long classNameId, long classPK, int type) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByC_C_C_T(companyId, classNameId, classPK, type);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CommercePaymentEntry> commercePaymentEntries = findByC_C_C_T(
				companyId, classNameId, classPK, type);

			commercePaymentEntries = InlineSQLHelperUtil.filter(
				commercePaymentEntries);

			return commercePaymentEntries.size();
		}

		StringBundler sb = new StringBundler(5);

		sb.append(_FILTER_SQL_COUNT_COMMERCEPAYMENTENTRY_WHERE);

		sb.append(_FINDER_COLUMN_C_C_C_T_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_C_C_T_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_C_C_C_T_CLASSPK_2);

		sb.append(_FINDER_COLUMN_C_C_C_T_TYPE_2_SQL);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommercePaymentEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			queryPos.add(classNameId);

			queryPos.add(classPK);

			queryPos.add(type);

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

	private static final String _FINDER_COLUMN_C_C_C_T_COMPANYID_2 =
		"commercePaymentEntry.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_C_C_T_CLASSNAMEID_2 =
		"commercePaymentEntry.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_C_C_C_T_CLASSPK_2 =
		"commercePaymentEntry.classPK = ? AND ";

	private static final String _FINDER_COLUMN_C_C_C_T_TYPE_2 =
		"commercePaymentEntry.type = ?";

	private static final String _FINDER_COLUMN_C_C_C_T_TYPE_2_SQL =
		"commercePaymentEntry.type_ = ?";

	private FinderPath _finderPathWithPaginationFindByC_C_C_P_T;
	private FinderPath _finderPathWithoutPaginationFindByC_C_C_P_T;
	private FinderPath _finderPathCountByC_C_C_P_T;

	/**
	 * Returns all the commerce payment entries where companyId = &#63; and classNameId = &#63; and classPK = &#63; and paymentStatus = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param paymentStatus the payment status
	 * @param type the type
	 * @return the matching commerce payment entries
	 */
	@Override
	public List<CommercePaymentEntry> findByC_C_C_P_T(
		long companyId, long classNameId, long classPK, int paymentStatus,
		int type) {

		return findByC_C_C_P_T(
			companyId, classNameId, classPK, paymentStatus, type,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce payment entries where companyId = &#63; and classNameId = &#63; and classPK = &#63; and paymentStatus = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param paymentStatus the payment status
	 * @param type the type
	 * @param start the lower bound of the range of commerce payment entries
	 * @param end the upper bound of the range of commerce payment entries (not inclusive)
	 * @return the range of matching commerce payment entries
	 */
	@Override
	public List<CommercePaymentEntry> findByC_C_C_P_T(
		long companyId, long classNameId, long classPK, int paymentStatus,
		int type, int start, int end) {

		return findByC_C_C_P_T(
			companyId, classNameId, classPK, paymentStatus, type, start, end,
			null);
	}

	/**
	 * Returns an ordered range of all the commerce payment entries where companyId = &#63; and classNameId = &#63; and classPK = &#63; and paymentStatus = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param paymentStatus the payment status
	 * @param type the type
	 * @param start the lower bound of the range of commerce payment entries
	 * @param end the upper bound of the range of commerce payment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce payment entries
	 */
	@Override
	public List<CommercePaymentEntry> findByC_C_C_P_T(
		long companyId, long classNameId, long classPK, int paymentStatus,
		int type, int start, int end,
		OrderByComparator<CommercePaymentEntry> orderByComparator) {

		return findByC_C_C_P_T(
			companyId, classNameId, classPK, paymentStatus, type, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce payment entries where companyId = &#63; and classNameId = &#63; and classPK = &#63; and paymentStatus = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param paymentStatus the payment status
	 * @param type the type
	 * @param start the lower bound of the range of commerce payment entries
	 * @param end the upper bound of the range of commerce payment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce payment entries
	 */
	@Override
	public List<CommercePaymentEntry> findByC_C_C_P_T(
		long companyId, long classNameId, long classPK, int paymentStatus,
		int type, int start, int end,
		OrderByComparator<CommercePaymentEntry> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByC_C_C_P_T;
				finderArgs = new Object[] {
					companyId, classNameId, classPK, paymentStatus, type
				};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByC_C_C_P_T;
			finderArgs = new Object[] {
				companyId, classNameId, classPK, paymentStatus, type, start,
				end, orderByComparator
			};
		}

		List<CommercePaymentEntry> list = null;

		if (useFinderCache) {
			list = (List<CommercePaymentEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommercePaymentEntry commercePaymentEntry : list) {
					if ((companyId != commercePaymentEntry.getCompanyId()) ||
						(classNameId !=
							commercePaymentEntry.getClassNameId()) ||
						(classPK != commercePaymentEntry.getClassPK()) ||
						(paymentStatus !=
							commercePaymentEntry.getPaymentStatus()) ||
						(type != commercePaymentEntry.getType())) {

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
					7 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				sb = new StringBundler(7);
			}

			sb.append(_SQL_SELECT_COMMERCEPAYMENTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_C_C_C_P_T_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_C_C_P_T_CLASSNAMEID_2);

			sb.append(_FINDER_COLUMN_C_C_C_P_T_CLASSPK_2);

			sb.append(_FINDER_COLUMN_C_C_C_P_T_PAYMENTSTATUS_2);

			sb.append(_FINDER_COLUMN_C_C_C_P_T_TYPE_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CommercePaymentEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(classNameId);

				queryPos.add(classPK);

				queryPos.add(paymentStatus);

				queryPos.add(type);

				list = (List<CommercePaymentEntry>)QueryUtil.list(
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
	 * Returns the first commerce payment entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63; and paymentStatus = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param paymentStatus the payment status
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce payment entry
	 * @throws NoSuchPaymentEntryException if a matching commerce payment entry could not be found
	 */
	@Override
	public CommercePaymentEntry findByC_C_C_P_T_First(
			long companyId, long classNameId, long classPK, int paymentStatus,
			int type, OrderByComparator<CommercePaymentEntry> orderByComparator)
		throws NoSuchPaymentEntryException {

		CommercePaymentEntry commercePaymentEntry = fetchByC_C_C_P_T_First(
			companyId, classNameId, classPK, paymentStatus, type,
			orderByComparator);

		if (commercePaymentEntry != null) {
			return commercePaymentEntry;
		}

		StringBundler sb = new StringBundler(12);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append(", paymentStatus=");
		sb.append(paymentStatus);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchPaymentEntryException(sb.toString());
	}

	/**
	 * Returns the first commerce payment entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63; and paymentStatus = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param paymentStatus the payment status
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce payment entry, or <code>null</code> if a matching commerce payment entry could not be found
	 */
	@Override
	public CommercePaymentEntry fetchByC_C_C_P_T_First(
		long companyId, long classNameId, long classPK, int paymentStatus,
		int type, OrderByComparator<CommercePaymentEntry> orderByComparator) {

		List<CommercePaymentEntry> list = findByC_C_C_P_T(
			companyId, classNameId, classPK, paymentStatus, type, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce payment entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63; and paymentStatus = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param paymentStatus the payment status
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce payment entry
	 * @throws NoSuchPaymentEntryException if a matching commerce payment entry could not be found
	 */
	@Override
	public CommercePaymentEntry findByC_C_C_P_T_Last(
			long companyId, long classNameId, long classPK, int paymentStatus,
			int type, OrderByComparator<CommercePaymentEntry> orderByComparator)
		throws NoSuchPaymentEntryException {

		CommercePaymentEntry commercePaymentEntry = fetchByC_C_C_P_T_Last(
			companyId, classNameId, classPK, paymentStatus, type,
			orderByComparator);

		if (commercePaymentEntry != null) {
			return commercePaymentEntry;
		}

		StringBundler sb = new StringBundler(12);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append(", paymentStatus=");
		sb.append(paymentStatus);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchPaymentEntryException(sb.toString());
	}

	/**
	 * Returns the last commerce payment entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63; and paymentStatus = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param paymentStatus the payment status
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce payment entry, or <code>null</code> if a matching commerce payment entry could not be found
	 */
	@Override
	public CommercePaymentEntry fetchByC_C_C_P_T_Last(
		long companyId, long classNameId, long classPK, int paymentStatus,
		int type, OrderByComparator<CommercePaymentEntry> orderByComparator) {

		int count = countByC_C_C_P_T(
			companyId, classNameId, classPK, paymentStatus, type);

		if (count == 0) {
			return null;
		}

		List<CommercePaymentEntry> list = findByC_C_C_P_T(
			companyId, classNameId, classPK, paymentStatus, type, count - 1,
			count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce payment entries before and after the current commerce payment entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63; and paymentStatus = &#63; and type = &#63;.
	 *
	 * @param commercePaymentEntryId the primary key of the current commerce payment entry
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param paymentStatus the payment status
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce payment entry
	 * @throws NoSuchPaymentEntryException if a commerce payment entry with the primary key could not be found
	 */
	@Override
	public CommercePaymentEntry[] findByC_C_C_P_T_PrevAndNext(
			long commercePaymentEntryId, long companyId, long classNameId,
			long classPK, int paymentStatus, int type,
			OrderByComparator<CommercePaymentEntry> orderByComparator)
		throws NoSuchPaymentEntryException {

		CommercePaymentEntry commercePaymentEntry = findByPrimaryKey(
			commercePaymentEntryId);

		Session session = null;

		try {
			session = openSession();

			CommercePaymentEntry[] array = new CommercePaymentEntryImpl[3];

			array[0] = getByC_C_C_P_T_PrevAndNext(
				session, commercePaymentEntry, companyId, classNameId, classPK,
				paymentStatus, type, orderByComparator, true);

			array[1] = commercePaymentEntry;

			array[2] = getByC_C_C_P_T_PrevAndNext(
				session, commercePaymentEntry, companyId, classNameId, classPK,
				paymentStatus, type, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CommercePaymentEntry getByC_C_C_P_T_PrevAndNext(
		Session session, CommercePaymentEntry commercePaymentEntry,
		long companyId, long classNameId, long classPK, int paymentStatus,
		int type, OrderByComparator<CommercePaymentEntry> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				8 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(7);
		}

		sb.append(_SQL_SELECT_COMMERCEPAYMENTENTRY_WHERE);

		sb.append(_FINDER_COLUMN_C_C_C_P_T_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_C_C_P_T_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_C_C_C_P_T_CLASSPK_2);

		sb.append(_FINDER_COLUMN_C_C_C_P_T_PAYMENTSTATUS_2);

		sb.append(_FINDER_COLUMN_C_C_C_P_T_TYPE_2);

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
			sb.append(CommercePaymentEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(classNameId);

		queryPos.add(classPK);

		queryPos.add(paymentStatus);

		queryPos.add(type);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commercePaymentEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommercePaymentEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the commerce payment entries that the user has permission to view where companyId = &#63; and classNameId = &#63; and classPK = &#63; and paymentStatus = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param paymentStatus the payment status
	 * @param type the type
	 * @return the matching commerce payment entries that the user has permission to view
	 */
	@Override
	public List<CommercePaymentEntry> filterFindByC_C_C_P_T(
		long companyId, long classNameId, long classPK, int paymentStatus,
		int type) {

		return filterFindByC_C_C_P_T(
			companyId, classNameId, classPK, paymentStatus, type,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce payment entries that the user has permission to view where companyId = &#63; and classNameId = &#63; and classPK = &#63; and paymentStatus = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param paymentStatus the payment status
	 * @param type the type
	 * @param start the lower bound of the range of commerce payment entries
	 * @param end the upper bound of the range of commerce payment entries (not inclusive)
	 * @return the range of matching commerce payment entries that the user has permission to view
	 */
	@Override
	public List<CommercePaymentEntry> filterFindByC_C_C_P_T(
		long companyId, long classNameId, long classPK, int paymentStatus,
		int type, int start, int end) {

		return filterFindByC_C_C_P_T(
			companyId, classNameId, classPK, paymentStatus, type, start, end,
			null);
	}

	/**
	 * Returns an ordered range of all the commerce payment entries that the user has permissions to view where companyId = &#63; and classNameId = &#63; and classPK = &#63; and paymentStatus = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param paymentStatus the payment status
	 * @param type the type
	 * @param start the lower bound of the range of commerce payment entries
	 * @param end the upper bound of the range of commerce payment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce payment entries that the user has permission to view
	 */
	@Override
	public List<CommercePaymentEntry> filterFindByC_C_C_P_T(
		long companyId, long classNameId, long classPK, int paymentStatus,
		int type, int start, int end,
		OrderByComparator<CommercePaymentEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_C_C_P_T(
				companyId, classNameId, classPK, paymentStatus, type, start,
				end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByC_C_C_P_T(
					companyId, classNameId, classPK, paymentStatus, type,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator));
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(8);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_COMMERCEPAYMENTENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEPAYMENTENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_C_C_P_T_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_C_C_P_T_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_C_C_C_P_T_CLASSPK_2);

		sb.append(_FINDER_COLUMN_C_C_C_P_T_PAYMENTSTATUS_2);

		sb.append(_FINDER_COLUMN_C_C_C_P_T_TYPE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEPAYMENTENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					CommercePaymentEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommercePaymentEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommercePaymentEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, CommercePaymentEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, CommercePaymentEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			queryPos.add(classNameId);

			queryPos.add(classPK);

			queryPos.add(paymentStatus);

			queryPos.add(type);

			return (List<CommercePaymentEntry>)QueryUtil.list(
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
	 * Returns the commerce payment entries before and after the current commerce payment entry in the ordered set of commerce payment entries that the user has permission to view where companyId = &#63; and classNameId = &#63; and classPK = &#63; and paymentStatus = &#63; and type = &#63;.
	 *
	 * @param commercePaymentEntryId the primary key of the current commerce payment entry
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param paymentStatus the payment status
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce payment entry
	 * @throws NoSuchPaymentEntryException if a commerce payment entry with the primary key could not be found
	 */
	@Override
	public CommercePaymentEntry[] filterFindByC_C_C_P_T_PrevAndNext(
			long commercePaymentEntryId, long companyId, long classNameId,
			long classPK, int paymentStatus, int type,
			OrderByComparator<CommercePaymentEntry> orderByComparator)
		throws NoSuchPaymentEntryException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_C_C_P_T_PrevAndNext(
				commercePaymentEntryId, companyId, classNameId, classPK,
				paymentStatus, type, orderByComparator);
		}

		CommercePaymentEntry commercePaymentEntry = findByPrimaryKey(
			commercePaymentEntryId);

		Session session = null;

		try {
			session = openSession();

			CommercePaymentEntry[] array = new CommercePaymentEntryImpl[3];

			array[0] = filterGetByC_C_C_P_T_PrevAndNext(
				session, commercePaymentEntry, companyId, classNameId, classPK,
				paymentStatus, type, orderByComparator, true);

			array[1] = commercePaymentEntry;

			array[2] = filterGetByC_C_C_P_T_PrevAndNext(
				session, commercePaymentEntry, companyId, classNameId, classPK,
				paymentStatus, type, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CommercePaymentEntry filterGetByC_C_C_P_T_PrevAndNext(
		Session session, CommercePaymentEntry commercePaymentEntry,
		long companyId, long classNameId, long classPK, int paymentStatus,
		int type, OrderByComparator<CommercePaymentEntry> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				9 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(8);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_COMMERCEPAYMENTENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEPAYMENTENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_C_C_P_T_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_C_C_P_T_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_C_C_C_P_T_CLASSPK_2);

		sb.append(_FINDER_COLUMN_C_C_C_P_T_PAYMENTSTATUS_2);

		sb.append(_FINDER_COLUMN_C_C_C_P_T_TYPE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEPAYMENTENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					CommercePaymentEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommercePaymentEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommercePaymentEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, CommercePaymentEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, CommercePaymentEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(companyId);

		queryPos.add(classNameId);

		queryPos.add(classPK);

		queryPos.add(paymentStatus);

		queryPos.add(type);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commercePaymentEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommercePaymentEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce payment entries where companyId = &#63; and classNameId = &#63; and classPK = &#63; and paymentStatus = &#63; and type = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param paymentStatus the payment status
	 * @param type the type
	 */
	@Override
	public void removeByC_C_C_P_T(
		long companyId, long classNameId, long classPK, int paymentStatus,
		int type) {

		for (CommercePaymentEntry commercePaymentEntry :
				findByC_C_C_P_T(
					companyId, classNameId, classPK, paymentStatus, type,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(commercePaymentEntry);
		}
	}

	/**
	 * Returns the number of commerce payment entries where companyId = &#63; and classNameId = &#63; and classPK = &#63; and paymentStatus = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param paymentStatus the payment status
	 * @param type the type
	 * @return the number of matching commerce payment entries
	 */
	@Override
	public int countByC_C_C_P_T(
		long companyId, long classNameId, long classPK, int paymentStatus,
		int type) {

		FinderPath finderPath = _finderPathCountByC_C_C_P_T;

		Object[] finderArgs = new Object[] {
			companyId, classNameId, classPK, paymentStatus, type
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_SQL_COUNT_COMMERCEPAYMENTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_C_C_C_P_T_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_C_C_P_T_CLASSNAMEID_2);

			sb.append(_FINDER_COLUMN_C_C_C_P_T_CLASSPK_2);

			sb.append(_FINDER_COLUMN_C_C_C_P_T_PAYMENTSTATUS_2);

			sb.append(_FINDER_COLUMN_C_C_C_P_T_TYPE_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(classNameId);

				queryPos.add(classPK);

				queryPos.add(paymentStatus);

				queryPos.add(type);

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
	 * Returns the number of commerce payment entries that the user has permission to view where companyId = &#63; and classNameId = &#63; and classPK = &#63; and paymentStatus = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param paymentStatus the payment status
	 * @param type the type
	 * @return the number of matching commerce payment entries that the user has permission to view
	 */
	@Override
	public int filterCountByC_C_C_P_T(
		long companyId, long classNameId, long classPK, int paymentStatus,
		int type) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByC_C_C_P_T(
				companyId, classNameId, classPK, paymentStatus, type);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CommercePaymentEntry> commercePaymentEntries = findByC_C_C_P_T(
				companyId, classNameId, classPK, paymentStatus, type);

			commercePaymentEntries = InlineSQLHelperUtil.filter(
				commercePaymentEntries);

			return commercePaymentEntries.size();
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_FILTER_SQL_COUNT_COMMERCEPAYMENTENTRY_WHERE);

		sb.append(_FINDER_COLUMN_C_C_C_P_T_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_C_C_P_T_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_C_C_C_P_T_CLASSPK_2);

		sb.append(_FINDER_COLUMN_C_C_C_P_T_PAYMENTSTATUS_2);

		sb.append(_FINDER_COLUMN_C_C_C_P_T_TYPE_2_SQL);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommercePaymentEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			queryPos.add(classNameId);

			queryPos.add(classPK);

			queryPos.add(paymentStatus);

			queryPos.add(type);

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

	private static final String _FINDER_COLUMN_C_C_C_P_T_COMPANYID_2 =
		"commercePaymentEntry.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_C_C_P_T_CLASSNAMEID_2 =
		"commercePaymentEntry.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_C_C_C_P_T_CLASSPK_2 =
		"commercePaymentEntry.classPK = ? AND ";

	private static final String _FINDER_COLUMN_C_C_C_P_T_PAYMENTSTATUS_2 =
		"commercePaymentEntry.paymentStatus = ? AND ";

	private static final String _FINDER_COLUMN_C_C_C_P_T_TYPE_2 =
		"commercePaymentEntry.type = ?";

	private static final String _FINDER_COLUMN_C_C_C_P_T_TYPE_2_SQL =
		"commercePaymentEntry.type_ = ?";

	private FinderPath _finderPathFetchByERC_C;

	/**
	 * Returns the commerce payment entry where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchPaymentEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce payment entry
	 * @throws NoSuchPaymentEntryException if a matching commerce payment entry could not be found
	 */
	@Override
	public CommercePaymentEntry findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchPaymentEntryException {

		CommercePaymentEntry commercePaymentEntry = fetchByERC_C(
			externalReferenceCode, companyId);

		if (commercePaymentEntry == null) {
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

			throw new NoSuchPaymentEntryException(sb.toString());
		}

		return commercePaymentEntry;
	}

	/**
	 * Returns the commerce payment entry where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce payment entry, or <code>null</code> if a matching commerce payment entry could not be found
	 */
	@Override
	public CommercePaymentEntry fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return fetchByERC_C(externalReferenceCode, companyId, true);
	}

	/**
	 * Returns the commerce payment entry where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce payment entry, or <code>null</code> if a matching commerce payment entry could not be found
	 */
	@Override
	public CommercePaymentEntry fetchByERC_C(
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

		if (result instanceof CommercePaymentEntry) {
			CommercePaymentEntry commercePaymentEntry =
				(CommercePaymentEntry)result;

			if (!Objects.equals(
					externalReferenceCode,
					commercePaymentEntry.getExternalReferenceCode()) ||
				(companyId != commercePaymentEntry.getCompanyId())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_COMMERCEPAYMENTENTRY_WHERE);

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

				List<CommercePaymentEntry> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByERC_C, finderArgs, list);
					}
				}
				else {
					CommercePaymentEntry commercePaymentEntry = list.get(0);

					result = commercePaymentEntry;

					cacheResult(commercePaymentEntry);
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
			return (CommercePaymentEntry)result;
		}
	}

	/**
	 * Removes the commerce payment entry where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the commerce payment entry that was removed
	 */
	@Override
	public CommercePaymentEntry removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchPaymentEntryException {

		CommercePaymentEntry commercePaymentEntry = findByERC_C(
			externalReferenceCode, companyId);

		return remove(commercePaymentEntry);
	}

	/**
	 * Returns the number of commerce payment entries where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching commerce payment entries
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		CommercePaymentEntry commercePaymentEntry = fetchByERC_C(
			externalReferenceCode, companyId);

		if (commercePaymentEntry == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ERC_C_EXTERNALREFERENCECODE_2 =
		"commercePaymentEntry.externalReferenceCode = ? AND ";

	private static final String _FINDER_COLUMN_ERC_C_EXTERNALREFERENCECODE_3 =
		"(commercePaymentEntry.externalReferenceCode IS NULL OR commercePaymentEntry.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_C_COMPANYID_2 =
		"commercePaymentEntry.companyId = ?";

	public CommercePaymentEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommercePaymentEntry.class);

		setModelImplClass(CommercePaymentEntryImpl.class);
		setModelPKClass(long.class);

		setTable(CommercePaymentEntryTable.INSTANCE);
	}

	/**
	 * Caches the commerce payment entry in the entity cache if it is enabled.
	 *
	 * @param commercePaymentEntry the commerce payment entry
	 */
	@Override
	public void cacheResult(CommercePaymentEntry commercePaymentEntry) {
		entityCache.putResult(
			CommercePaymentEntryImpl.class,
			commercePaymentEntry.getPrimaryKey(), commercePaymentEntry);

		finderCache.putResult(
			_finderPathFetchByERC_C,
			new Object[] {
				commercePaymentEntry.getExternalReferenceCode(),
				commercePaymentEntry.getCompanyId()
			},
			commercePaymentEntry);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the commerce payment entries in the entity cache if it is enabled.
	 *
	 * @param commercePaymentEntries the commerce payment entries
	 */
	@Override
	public void cacheResult(List<CommercePaymentEntry> commercePaymentEntries) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (commercePaymentEntries.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (CommercePaymentEntry commercePaymentEntry :
				commercePaymentEntries) {

			if (entityCache.getResult(
					CommercePaymentEntryImpl.class,
					commercePaymentEntry.getPrimaryKey()) == null) {

				cacheResult(commercePaymentEntry);
			}
		}
	}

	/**
	 * Clears the cache for all commerce payment entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(CommercePaymentEntryImpl.class);

		finderCache.clearCache(CommercePaymentEntryImpl.class);
	}

	/**
	 * Clears the cache for the commerce payment entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(CommercePaymentEntry commercePaymentEntry) {
		entityCache.removeResult(
			CommercePaymentEntryImpl.class, commercePaymentEntry);
	}

	@Override
	public void clearCache(List<CommercePaymentEntry> commercePaymentEntries) {
		for (CommercePaymentEntry commercePaymentEntry :
				commercePaymentEntries) {

			entityCache.removeResult(
				CommercePaymentEntryImpl.class, commercePaymentEntry);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(CommercePaymentEntryImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				CommercePaymentEntryImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		CommercePaymentEntryModelImpl commercePaymentEntryModelImpl) {

		Object[] args = new Object[] {
			commercePaymentEntryModelImpl.getExternalReferenceCode(),
			commercePaymentEntryModelImpl.getCompanyId()
		};

		finderCache.putResult(
			_finderPathFetchByERC_C, args, commercePaymentEntryModelImpl);
	}

	/**
	 * Creates a new commerce payment entry with the primary key. Does not add the commerce payment entry to the database.
	 *
	 * @param commercePaymentEntryId the primary key for the new commerce payment entry
	 * @return the new commerce payment entry
	 */
	@Override
	public CommercePaymentEntry create(long commercePaymentEntryId) {
		CommercePaymentEntry commercePaymentEntry =
			new CommercePaymentEntryImpl();

		commercePaymentEntry.setNew(true);
		commercePaymentEntry.setPrimaryKey(commercePaymentEntryId);

		commercePaymentEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return commercePaymentEntry;
	}

	/**
	 * Removes the commerce payment entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commercePaymentEntryId the primary key of the commerce payment entry
	 * @return the commerce payment entry that was removed
	 * @throws NoSuchPaymentEntryException if a commerce payment entry with the primary key could not be found
	 */
	@Override
	public CommercePaymentEntry remove(long commercePaymentEntryId)
		throws NoSuchPaymentEntryException {

		return remove((Serializable)commercePaymentEntryId);
	}

	/**
	 * Removes the commerce payment entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the commerce payment entry
	 * @return the commerce payment entry that was removed
	 * @throws NoSuchPaymentEntryException if a commerce payment entry with the primary key could not be found
	 */
	@Override
	public CommercePaymentEntry remove(Serializable primaryKey)
		throws NoSuchPaymentEntryException {

		Session session = null;

		try {
			session = openSession();

			CommercePaymentEntry commercePaymentEntry =
				(CommercePaymentEntry)session.get(
					CommercePaymentEntryImpl.class, primaryKey);

			if (commercePaymentEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchPaymentEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(commercePaymentEntry);
		}
		catch (NoSuchPaymentEntryException noSuchEntityException) {
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
	protected CommercePaymentEntry removeImpl(
		CommercePaymentEntry commercePaymentEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commercePaymentEntry)) {
				commercePaymentEntry = (CommercePaymentEntry)session.get(
					CommercePaymentEntryImpl.class,
					commercePaymentEntry.getPrimaryKeyObj());
			}

			if (commercePaymentEntry != null) {
				session.delete(commercePaymentEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commercePaymentEntry != null) {
			clearCache(commercePaymentEntry);
		}

		return commercePaymentEntry;
	}

	@Override
	public CommercePaymentEntry updateImpl(
		CommercePaymentEntry commercePaymentEntry) {

		boolean isNew = commercePaymentEntry.isNew();

		if (!(commercePaymentEntry instanceof CommercePaymentEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(commercePaymentEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					commercePaymentEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commercePaymentEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommercePaymentEntry implementation " +
					commercePaymentEntry.getClass());
		}

		CommercePaymentEntryModelImpl commercePaymentEntryModelImpl =
			(CommercePaymentEntryModelImpl)commercePaymentEntry;

		if (Validator.isNull(commercePaymentEntry.getExternalReferenceCode())) {
			commercePaymentEntry.setExternalReferenceCode(
				String.valueOf(commercePaymentEntry.getPrimaryKey()));
		}
		else {
			if (!Objects.equals(
					commercePaymentEntryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					commercePaymentEntry.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = commercePaymentEntry.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = commercePaymentEntry.getPrimaryKey();
					}

					try {
						commercePaymentEntry.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								CommercePaymentEntry.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								commercePaymentEntry.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			CommercePaymentEntry ercCommercePaymentEntry = fetchByERC_C(
				commercePaymentEntry.getExternalReferenceCode(),
				commercePaymentEntry.getCompanyId());

			if (isNew) {
				if (ercCommercePaymentEntry != null) {
					throw new DuplicateCommercePaymentEntryExternalReferenceCodeException(
						"Duplicate commerce payment entry with external reference code " +
							commercePaymentEntry.getExternalReferenceCode() +
								" and company " +
									commercePaymentEntry.getCompanyId());
				}
			}
			else {
				if ((ercCommercePaymentEntry != null) &&
					(commercePaymentEntry.getCommercePaymentEntryId() !=
						ercCommercePaymentEntry.getCommercePaymentEntryId())) {

					throw new DuplicateCommercePaymentEntryExternalReferenceCodeException(
						"Duplicate commerce payment entry with external reference code " +
							commercePaymentEntry.getExternalReferenceCode() +
								" and company " +
									commercePaymentEntry.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commercePaymentEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				commercePaymentEntry.setCreateDate(date);
			}
			else {
				commercePaymentEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commercePaymentEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commercePaymentEntry.setModifiedDate(date);
			}
			else {
				commercePaymentEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commercePaymentEntry);
			}
			else {
				commercePaymentEntry = (CommercePaymentEntry)session.merge(
					commercePaymentEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			CommercePaymentEntryImpl.class, commercePaymentEntryModelImpl,
			false, true);

		cacheUniqueFindersCache(commercePaymentEntryModelImpl);

		if (isNew) {
			commercePaymentEntry.setNew(false);
		}

		commercePaymentEntry.resetOriginalValues();

		return commercePaymentEntry;
	}

	/**
	 * Returns the commerce payment entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the commerce payment entry
	 * @return the commerce payment entry
	 * @throws NoSuchPaymentEntryException if a commerce payment entry with the primary key could not be found
	 */
	@Override
	public CommercePaymentEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchPaymentEntryException {

		CommercePaymentEntry commercePaymentEntry = fetchByPrimaryKey(
			primaryKey);

		if (commercePaymentEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchPaymentEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return commercePaymentEntry;
	}

	/**
	 * Returns the commerce payment entry with the primary key or throws a <code>NoSuchPaymentEntryException</code> if it could not be found.
	 *
	 * @param commercePaymentEntryId the primary key of the commerce payment entry
	 * @return the commerce payment entry
	 * @throws NoSuchPaymentEntryException if a commerce payment entry with the primary key could not be found
	 */
	@Override
	public CommercePaymentEntry findByPrimaryKey(long commercePaymentEntryId)
		throws NoSuchPaymentEntryException {

		return findByPrimaryKey((Serializable)commercePaymentEntryId);
	}

	/**
	 * Returns the commerce payment entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commercePaymentEntryId the primary key of the commerce payment entry
	 * @return the commerce payment entry, or <code>null</code> if a commerce payment entry with the primary key could not be found
	 */
	@Override
	public CommercePaymentEntry fetchByPrimaryKey(long commercePaymentEntryId) {
		return fetchByPrimaryKey((Serializable)commercePaymentEntryId);
	}

	/**
	 * Returns all the commerce payment entries.
	 *
	 * @return the commerce payment entries
	 */
	@Override
	public List<CommercePaymentEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce payment entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce payment entries
	 * @param end the upper bound of the range of commerce payment entries (not inclusive)
	 * @return the range of commerce payment entries
	 */
	@Override
	public List<CommercePaymentEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce payment entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce payment entries
	 * @param end the upper bound of the range of commerce payment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of commerce payment entries
	 */
	@Override
	public List<CommercePaymentEntry> findAll(
		int start, int end,
		OrderByComparator<CommercePaymentEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce payment entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce payment entries
	 * @param end the upper bound of the range of commerce payment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of commerce payment entries
	 */
	@Override
	public List<CommercePaymentEntry> findAll(
		int start, int end,
		OrderByComparator<CommercePaymentEntry> orderByComparator,
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

		List<CommercePaymentEntry> list = null;

		if (useFinderCache) {
			list = (List<CommercePaymentEntry>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_COMMERCEPAYMENTENTRY);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_COMMERCEPAYMENTENTRY;

				sql = sql.concat(CommercePaymentEntryModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<CommercePaymentEntry>)QueryUtil.list(
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
	 * Removes all the commerce payment entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (CommercePaymentEntry commercePaymentEntry : findAll()) {
			remove(commercePaymentEntry);
		}
	}

	/**
	 * Returns the number of commerce payment entries.
	 *
	 * @return the number of commerce payment entries
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
					_SQL_COUNT_COMMERCEPAYMENTENTRY);

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
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "commercePaymentEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEPAYMENTENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommercePaymentEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce payment entry persistence.
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

		_finderPathWithPaginationFindByC_C_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_C",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"companyId", "classNameId", "classPK"}, true);

		_finderPathWithoutPaginationFindByC_C_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C_C",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"companyId", "classNameId", "classPK"}, true);

		_finderPathCountByC_C_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C_C",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"companyId", "classNameId", "classPK"}, false);

		_finderPathWithPaginationFindByC_C_C_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_C_T",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "classNameId", "classPK", "type_"},
			true);

		_finderPathWithoutPaginationFindByC_C_C_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C_C_T",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName()
			},
			new String[] {"companyId", "classNameId", "classPK", "type_"},
			true);

		_finderPathCountByC_C_C_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C_C_T",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName()
			},
			new String[] {"companyId", "classNameId", "classPK", "type_"},
			false);

		_finderPathWithPaginationFindByC_C_C_P_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_C_P_T",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {
				"companyId", "classNameId", "classPK", "paymentStatus", "type_"
			},
			true);

		_finderPathWithoutPaginationFindByC_C_C_P_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C_C_P_T",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName()
			},
			new String[] {
				"companyId", "classNameId", "classPK", "paymentStatus", "type_"
			},
			true);

		_finderPathCountByC_C_C_P_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C_C_P_T",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName()
			},
			new String[] {
				"companyId", "classNameId", "classPK", "paymentStatus", "type_"
			},
			false);

		_finderPathFetchByERC_C = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"externalReferenceCode", "companyId"}, true);

		CommercePaymentEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommercePaymentEntryUtil.setPersistence(null);

		entityCache.removeCache(CommercePaymentEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_COMMERCEPAYMENTENTRY =
		"SELECT commercePaymentEntry FROM CommercePaymentEntry commercePaymentEntry";

	private static final String _SQL_SELECT_COMMERCEPAYMENTENTRY_WHERE =
		"SELECT commercePaymentEntry FROM CommercePaymentEntry commercePaymentEntry WHERE ";

	private static final String _SQL_COUNT_COMMERCEPAYMENTENTRY =
		"SELECT COUNT(commercePaymentEntry) FROM CommercePaymentEntry commercePaymentEntry";

	private static final String _SQL_COUNT_COMMERCEPAYMENTENTRY_WHERE =
		"SELECT COUNT(commercePaymentEntry) FROM CommercePaymentEntry commercePaymentEntry WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"commercePaymentEntry.commercePaymentEntryId";

	private static final String _FILTER_SQL_SELECT_COMMERCEPAYMENTENTRY_WHERE =
		"SELECT DISTINCT {commercePaymentEntry.*} FROM CommercePaymentEntry commercePaymentEntry WHERE ";

	private static final String
		_FILTER_SQL_SELECT_COMMERCEPAYMENTENTRY_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {CommercePaymentEntry.*} FROM (SELECT DISTINCT commercePaymentEntry.commercePaymentEntryId FROM CommercePaymentEntry commercePaymentEntry WHERE ";

	private static final String
		_FILTER_SQL_SELECT_COMMERCEPAYMENTENTRY_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN CommercePaymentEntry ON TEMP_TABLE.commercePaymentEntryId = CommercePaymentEntry.commercePaymentEntryId";

	private static final String _FILTER_SQL_COUNT_COMMERCEPAYMENTENTRY_WHERE =
		"SELECT COUNT(DISTINCT commercePaymentEntry.commercePaymentEntryId) AS COUNT_VALUE FROM CommercePaymentEntry commercePaymentEntry WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "commercePaymentEntry";

	private static final String _FILTER_ENTITY_TABLE = "CommercePaymentEntry";

	private static final String _ORDER_BY_ENTITY_ALIAS =
		"commercePaymentEntry.";

	private static final String _ORDER_BY_ENTITY_TABLE =
		"CommercePaymentEntry.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No CommercePaymentEntry exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommercePaymentEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommercePaymentEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}