/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.service.persistence.impl;

import com.liferay.commerce.payment.exception.NoSuchPaymentEntryAuditException;
import com.liferay.commerce.payment.model.CommercePaymentEntryAudit;
import com.liferay.commerce.payment.model.CommercePaymentEntryAuditTable;
import com.liferay.commerce.payment.model.impl.CommercePaymentEntryAuditImpl;
import com.liferay.commerce.payment.model.impl.CommercePaymentEntryAuditModelImpl;
import com.liferay.commerce.payment.service.persistence.CommercePaymentEntryAuditPersistence;
import com.liferay.commerce.payment.service.persistence.CommercePaymentEntryAuditUtil;
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
 * The persistence implementation for the commerce payment entry audit service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Luca Pellizzon
 * @generated
 */
@Component(service = CommercePaymentEntryAuditPersistence.class)
public class CommercePaymentEntryAuditPersistenceImpl
	extends BasePersistenceImpl<CommercePaymentEntryAudit>
	implements CommercePaymentEntryAuditPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommercePaymentEntryAuditUtil</code> to access the commerce payment entry audit persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommercePaymentEntryAuditImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByCommercePaymentEntryId;
	private FinderPath _finderPathWithoutPaginationFindByCommercePaymentEntryId;
	private FinderPath _finderPathCountByCommercePaymentEntryId;

	/**
	 * Returns all the commerce payment entry audits where commercePaymentEntryId = &#63;.
	 *
	 * @param commercePaymentEntryId the commerce payment entry ID
	 * @return the matching commerce payment entry audits
	 */
	@Override
	public List<CommercePaymentEntryAudit> findByCommercePaymentEntryId(
		long commercePaymentEntryId) {

		return findByCommercePaymentEntryId(
			commercePaymentEntryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce payment entry audits where commercePaymentEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryAuditModelImpl</code>.
	 * </p>
	 *
	 * @param commercePaymentEntryId the commerce payment entry ID
	 * @param start the lower bound of the range of commerce payment entry audits
	 * @param end the upper bound of the range of commerce payment entry audits (not inclusive)
	 * @return the range of matching commerce payment entry audits
	 */
	@Override
	public List<CommercePaymentEntryAudit> findByCommercePaymentEntryId(
		long commercePaymentEntryId, int start, int end) {

		return findByCommercePaymentEntryId(
			commercePaymentEntryId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce payment entry audits where commercePaymentEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryAuditModelImpl</code>.
	 * </p>
	 *
	 * @param commercePaymentEntryId the commerce payment entry ID
	 * @param start the lower bound of the range of commerce payment entry audits
	 * @param end the upper bound of the range of commerce payment entry audits (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce payment entry audits
	 */
	@Override
	public List<CommercePaymentEntryAudit> findByCommercePaymentEntryId(
		long commercePaymentEntryId, int start, int end,
		OrderByComparator<CommercePaymentEntryAudit> orderByComparator) {

		return findByCommercePaymentEntryId(
			commercePaymentEntryId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce payment entry audits where commercePaymentEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryAuditModelImpl</code>.
	 * </p>
	 *
	 * @param commercePaymentEntryId the commerce payment entry ID
	 * @param start the lower bound of the range of commerce payment entry audits
	 * @param end the upper bound of the range of commerce payment entry audits (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce payment entry audits
	 */
	@Override
	public List<CommercePaymentEntryAudit> findByCommercePaymentEntryId(
		long commercePaymentEntryId, int start, int end,
		OrderByComparator<CommercePaymentEntryAudit> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath =
					_finderPathWithoutPaginationFindByCommercePaymentEntryId;
				finderArgs = new Object[] {commercePaymentEntryId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByCommercePaymentEntryId;
			finderArgs = new Object[] {
				commercePaymentEntryId, start, end, orderByComparator
			};
		}

		List<CommercePaymentEntryAudit> list = null;

		if (useFinderCache) {
			list = (List<CommercePaymentEntryAudit>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommercePaymentEntryAudit commercePaymentEntryAudit :
						list) {

					if (commercePaymentEntryId !=
							commercePaymentEntryAudit.
								getCommercePaymentEntryId()) {

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

			sb.append(_SQL_SELECT_COMMERCEPAYMENTENTRYAUDIT_WHERE);

			sb.append(
				_FINDER_COLUMN_COMMERCEPAYMENTENTRYID_COMMERCEPAYMENTENTRYID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CommercePaymentEntryAuditModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(commercePaymentEntryId);

				list = (List<CommercePaymentEntryAudit>)QueryUtil.list(
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
	 * Returns the first commerce payment entry audit in the ordered set where commercePaymentEntryId = &#63;.
	 *
	 * @param commercePaymentEntryId the commerce payment entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce payment entry audit
	 * @throws NoSuchPaymentEntryAuditException if a matching commerce payment entry audit could not be found
	 */
	@Override
	public CommercePaymentEntryAudit findByCommercePaymentEntryId_First(
			long commercePaymentEntryId,
			OrderByComparator<CommercePaymentEntryAudit> orderByComparator)
		throws NoSuchPaymentEntryAuditException {

		CommercePaymentEntryAudit commercePaymentEntryAudit =
			fetchByCommercePaymentEntryId_First(
				commercePaymentEntryId, orderByComparator);

		if (commercePaymentEntryAudit != null) {
			return commercePaymentEntryAudit;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("commercePaymentEntryId=");
		sb.append(commercePaymentEntryId);

		sb.append("}");

		throw new NoSuchPaymentEntryAuditException(sb.toString());
	}

	/**
	 * Returns the first commerce payment entry audit in the ordered set where commercePaymentEntryId = &#63;.
	 *
	 * @param commercePaymentEntryId the commerce payment entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce payment entry audit, or <code>null</code> if a matching commerce payment entry audit could not be found
	 */
	@Override
	public CommercePaymentEntryAudit fetchByCommercePaymentEntryId_First(
		long commercePaymentEntryId,
		OrderByComparator<CommercePaymentEntryAudit> orderByComparator) {

		List<CommercePaymentEntryAudit> list = findByCommercePaymentEntryId(
			commercePaymentEntryId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce payment entry audit in the ordered set where commercePaymentEntryId = &#63;.
	 *
	 * @param commercePaymentEntryId the commerce payment entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce payment entry audit
	 * @throws NoSuchPaymentEntryAuditException if a matching commerce payment entry audit could not be found
	 */
	@Override
	public CommercePaymentEntryAudit findByCommercePaymentEntryId_Last(
			long commercePaymentEntryId,
			OrderByComparator<CommercePaymentEntryAudit> orderByComparator)
		throws NoSuchPaymentEntryAuditException {

		CommercePaymentEntryAudit commercePaymentEntryAudit =
			fetchByCommercePaymentEntryId_Last(
				commercePaymentEntryId, orderByComparator);

		if (commercePaymentEntryAudit != null) {
			return commercePaymentEntryAudit;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("commercePaymentEntryId=");
		sb.append(commercePaymentEntryId);

		sb.append("}");

		throw new NoSuchPaymentEntryAuditException(sb.toString());
	}

	/**
	 * Returns the last commerce payment entry audit in the ordered set where commercePaymentEntryId = &#63;.
	 *
	 * @param commercePaymentEntryId the commerce payment entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce payment entry audit, or <code>null</code> if a matching commerce payment entry audit could not be found
	 */
	@Override
	public CommercePaymentEntryAudit fetchByCommercePaymentEntryId_Last(
		long commercePaymentEntryId,
		OrderByComparator<CommercePaymentEntryAudit> orderByComparator) {

		int count = countByCommercePaymentEntryId(commercePaymentEntryId);

		if (count == 0) {
			return null;
		}

		List<CommercePaymentEntryAudit> list = findByCommercePaymentEntryId(
			commercePaymentEntryId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce payment entry audits before and after the current commerce payment entry audit in the ordered set where commercePaymentEntryId = &#63;.
	 *
	 * @param commercePaymentEntryAuditId the primary key of the current commerce payment entry audit
	 * @param commercePaymentEntryId the commerce payment entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce payment entry audit
	 * @throws NoSuchPaymentEntryAuditException if a commerce payment entry audit with the primary key could not be found
	 */
	@Override
	public CommercePaymentEntryAudit[] findByCommercePaymentEntryId_PrevAndNext(
			long commercePaymentEntryAuditId, long commercePaymentEntryId,
			OrderByComparator<CommercePaymentEntryAudit> orderByComparator)
		throws NoSuchPaymentEntryAuditException {

		CommercePaymentEntryAudit commercePaymentEntryAudit = findByPrimaryKey(
			commercePaymentEntryAuditId);

		Session session = null;

		try {
			session = openSession();

			CommercePaymentEntryAudit[] array =
				new CommercePaymentEntryAuditImpl[3];

			array[0] = getByCommercePaymentEntryId_PrevAndNext(
				session, commercePaymentEntryAudit, commercePaymentEntryId,
				orderByComparator, true);

			array[1] = commercePaymentEntryAudit;

			array[2] = getByCommercePaymentEntryId_PrevAndNext(
				session, commercePaymentEntryAudit, commercePaymentEntryId,
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

	protected CommercePaymentEntryAudit getByCommercePaymentEntryId_PrevAndNext(
		Session session, CommercePaymentEntryAudit commercePaymentEntryAudit,
		long commercePaymentEntryId,
		OrderByComparator<CommercePaymentEntryAudit> orderByComparator,
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

		sb.append(_SQL_SELECT_COMMERCEPAYMENTENTRYAUDIT_WHERE);

		sb.append(
			_FINDER_COLUMN_COMMERCEPAYMENTENTRYID_COMMERCEPAYMENTENTRYID_2);

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
			sb.append(CommercePaymentEntryAuditModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(commercePaymentEntryId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commercePaymentEntryAudit)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommercePaymentEntryAudit> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the commerce payment entry audits that the user has permission to view where commercePaymentEntryId = &#63;.
	 *
	 * @param commercePaymentEntryId the commerce payment entry ID
	 * @return the matching commerce payment entry audits that the user has permission to view
	 */
	@Override
	public List<CommercePaymentEntryAudit> filterFindByCommercePaymentEntryId(
		long commercePaymentEntryId) {

		return filterFindByCommercePaymentEntryId(
			commercePaymentEntryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce payment entry audits that the user has permission to view where commercePaymentEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryAuditModelImpl</code>.
	 * </p>
	 *
	 * @param commercePaymentEntryId the commerce payment entry ID
	 * @param start the lower bound of the range of commerce payment entry audits
	 * @param end the upper bound of the range of commerce payment entry audits (not inclusive)
	 * @return the range of matching commerce payment entry audits that the user has permission to view
	 */
	@Override
	public List<CommercePaymentEntryAudit> filterFindByCommercePaymentEntryId(
		long commercePaymentEntryId, int start, int end) {

		return filterFindByCommercePaymentEntryId(
			commercePaymentEntryId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce payment entry audits that the user has permissions to view where commercePaymentEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryAuditModelImpl</code>.
	 * </p>
	 *
	 * @param commercePaymentEntryId the commerce payment entry ID
	 * @param start the lower bound of the range of commerce payment entry audits
	 * @param end the upper bound of the range of commerce payment entry audits (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce payment entry audits that the user has permission to view
	 */
	@Override
	public List<CommercePaymentEntryAudit> filterFindByCommercePaymentEntryId(
		long commercePaymentEntryId, int start, int end,
		OrderByComparator<CommercePaymentEntryAudit> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByCommercePaymentEntryId(
				commercePaymentEntryId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByCommercePaymentEntryId(
					commercePaymentEntryId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator));
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
			sb.append(_FILTER_SQL_SELECT_COMMERCEPAYMENTENTRYAUDIT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEPAYMENTENTRYAUDIT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(
			_FINDER_COLUMN_COMMERCEPAYMENTENTRYID_COMMERCEPAYMENTENTRYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEPAYMENTENTRYAUDIT_NO_INLINE_DISTINCT_WHERE_2);
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
					CommercePaymentEntryAuditModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommercePaymentEntryAuditModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommercePaymentEntryAudit.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, CommercePaymentEntryAuditImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, CommercePaymentEntryAuditImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(commercePaymentEntryId);

			return (List<CommercePaymentEntryAudit>)QueryUtil.list(
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
	 * Returns the commerce payment entry audits before and after the current commerce payment entry audit in the ordered set of commerce payment entry audits that the user has permission to view where commercePaymentEntryId = &#63;.
	 *
	 * @param commercePaymentEntryAuditId the primary key of the current commerce payment entry audit
	 * @param commercePaymentEntryId the commerce payment entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce payment entry audit
	 * @throws NoSuchPaymentEntryAuditException if a commerce payment entry audit with the primary key could not be found
	 */
	@Override
	public CommercePaymentEntryAudit[]
			filterFindByCommercePaymentEntryId_PrevAndNext(
				long commercePaymentEntryAuditId, long commercePaymentEntryId,
				OrderByComparator<CommercePaymentEntryAudit> orderByComparator)
		throws NoSuchPaymentEntryAuditException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByCommercePaymentEntryId_PrevAndNext(
				commercePaymentEntryAuditId, commercePaymentEntryId,
				orderByComparator);
		}

		CommercePaymentEntryAudit commercePaymentEntryAudit = findByPrimaryKey(
			commercePaymentEntryAuditId);

		Session session = null;

		try {
			session = openSession();

			CommercePaymentEntryAudit[] array =
				new CommercePaymentEntryAuditImpl[3];

			array[0] = filterGetByCommercePaymentEntryId_PrevAndNext(
				session, commercePaymentEntryAudit, commercePaymentEntryId,
				orderByComparator, true);

			array[1] = commercePaymentEntryAudit;

			array[2] = filterGetByCommercePaymentEntryId_PrevAndNext(
				session, commercePaymentEntryAudit, commercePaymentEntryId,
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

	protected CommercePaymentEntryAudit
		filterGetByCommercePaymentEntryId_PrevAndNext(
			Session session,
			CommercePaymentEntryAudit commercePaymentEntryAudit,
			long commercePaymentEntryId,
			OrderByComparator<CommercePaymentEntryAudit> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_COMMERCEPAYMENTENTRYAUDIT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEPAYMENTENTRYAUDIT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(
			_FINDER_COLUMN_COMMERCEPAYMENTENTRYID_COMMERCEPAYMENTENTRYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCEPAYMENTENTRYAUDIT_NO_INLINE_DISTINCT_WHERE_2);
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
					CommercePaymentEntryAuditModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommercePaymentEntryAuditModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommercePaymentEntryAudit.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, CommercePaymentEntryAuditImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, CommercePaymentEntryAuditImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(commercePaymentEntryId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commercePaymentEntryAudit)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommercePaymentEntryAudit> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce payment entry audits where commercePaymentEntryId = &#63; from the database.
	 *
	 * @param commercePaymentEntryId the commerce payment entry ID
	 */
	@Override
	public void removeByCommercePaymentEntryId(long commercePaymentEntryId) {
		for (CommercePaymentEntryAudit commercePaymentEntryAudit :
				findByCommercePaymentEntryId(
					commercePaymentEntryId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(commercePaymentEntryAudit);
		}
	}

	/**
	 * Returns the number of commerce payment entry audits where commercePaymentEntryId = &#63;.
	 *
	 * @param commercePaymentEntryId the commerce payment entry ID
	 * @return the number of matching commerce payment entry audits
	 */
	@Override
	public int countByCommercePaymentEntryId(long commercePaymentEntryId) {
		FinderPath finderPath = _finderPathCountByCommercePaymentEntryId;

		Object[] finderArgs = new Object[] {commercePaymentEntryId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_COMMERCEPAYMENTENTRYAUDIT_WHERE);

			sb.append(
				_FINDER_COLUMN_COMMERCEPAYMENTENTRYID_COMMERCEPAYMENTENTRYID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(commercePaymentEntryId);

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
	 * Returns the number of commerce payment entry audits that the user has permission to view where commercePaymentEntryId = &#63;.
	 *
	 * @param commercePaymentEntryId the commerce payment entry ID
	 * @return the number of matching commerce payment entry audits that the user has permission to view
	 */
	@Override
	public int filterCountByCommercePaymentEntryId(
		long commercePaymentEntryId) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByCommercePaymentEntryId(commercePaymentEntryId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CommercePaymentEntryAudit> commercePaymentEntryAudits =
				findByCommercePaymentEntryId(commercePaymentEntryId);

			commercePaymentEntryAudits = InlineSQLHelperUtil.filter(
				commercePaymentEntryAudits);

			return commercePaymentEntryAudits.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_COMMERCEPAYMENTENTRYAUDIT_WHERE);

		sb.append(
			_FINDER_COLUMN_COMMERCEPAYMENTENTRYID_COMMERCEPAYMENTENTRYID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommercePaymentEntryAudit.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(commercePaymentEntryId);

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

	private static final String
		_FINDER_COLUMN_COMMERCEPAYMENTENTRYID_COMMERCEPAYMENTENTRYID_2 =
			"commercePaymentEntryAudit.commercePaymentEntryId = ?";

	public CommercePaymentEntryAuditPersistenceImpl() {
		setModelClass(CommercePaymentEntryAudit.class);

		setModelImplClass(CommercePaymentEntryAuditImpl.class);
		setModelPKClass(long.class);

		setTable(CommercePaymentEntryAuditTable.INSTANCE);
	}

	/**
	 * Caches the commerce payment entry audit in the entity cache if it is enabled.
	 *
	 * @param commercePaymentEntryAudit the commerce payment entry audit
	 */
	@Override
	public void cacheResult(
		CommercePaymentEntryAudit commercePaymentEntryAudit) {

		entityCache.putResult(
			CommercePaymentEntryAuditImpl.class,
			commercePaymentEntryAudit.getPrimaryKey(),
			commercePaymentEntryAudit);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the commerce payment entry audits in the entity cache if it is enabled.
	 *
	 * @param commercePaymentEntryAudits the commerce payment entry audits
	 */
	@Override
	public void cacheResult(
		List<CommercePaymentEntryAudit> commercePaymentEntryAudits) {

		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (commercePaymentEntryAudits.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (CommercePaymentEntryAudit commercePaymentEntryAudit :
				commercePaymentEntryAudits) {

			if (entityCache.getResult(
					CommercePaymentEntryAuditImpl.class,
					commercePaymentEntryAudit.getPrimaryKey()) == null) {

				cacheResult(commercePaymentEntryAudit);
			}
		}
	}

	/**
	 * Clears the cache for all commerce payment entry audits.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(CommercePaymentEntryAuditImpl.class);

		finderCache.clearCache(CommercePaymentEntryAuditImpl.class);
	}

	/**
	 * Clears the cache for the commerce payment entry audit.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(
		CommercePaymentEntryAudit commercePaymentEntryAudit) {

		entityCache.removeResult(
			CommercePaymentEntryAuditImpl.class, commercePaymentEntryAudit);
	}

	@Override
	public void clearCache(
		List<CommercePaymentEntryAudit> commercePaymentEntryAudits) {

		for (CommercePaymentEntryAudit commercePaymentEntryAudit :
				commercePaymentEntryAudits) {

			entityCache.removeResult(
				CommercePaymentEntryAuditImpl.class, commercePaymentEntryAudit);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(CommercePaymentEntryAuditImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				CommercePaymentEntryAuditImpl.class, primaryKey);
		}
	}

	/**
	 * Creates a new commerce payment entry audit with the primary key. Does not add the commerce payment entry audit to the database.
	 *
	 * @param commercePaymentEntryAuditId the primary key for the new commerce payment entry audit
	 * @return the new commerce payment entry audit
	 */
	@Override
	public CommercePaymentEntryAudit create(long commercePaymentEntryAuditId) {
		CommercePaymentEntryAudit commercePaymentEntryAudit =
			new CommercePaymentEntryAuditImpl();

		commercePaymentEntryAudit.setNew(true);
		commercePaymentEntryAudit.setPrimaryKey(commercePaymentEntryAuditId);

		commercePaymentEntryAudit.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return commercePaymentEntryAudit;
	}

	/**
	 * Removes the commerce payment entry audit with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commercePaymentEntryAuditId the primary key of the commerce payment entry audit
	 * @return the commerce payment entry audit that was removed
	 * @throws NoSuchPaymentEntryAuditException if a commerce payment entry audit with the primary key could not be found
	 */
	@Override
	public CommercePaymentEntryAudit remove(long commercePaymentEntryAuditId)
		throws NoSuchPaymentEntryAuditException {

		return remove((Serializable)commercePaymentEntryAuditId);
	}

	/**
	 * Removes the commerce payment entry audit with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the commerce payment entry audit
	 * @return the commerce payment entry audit that was removed
	 * @throws NoSuchPaymentEntryAuditException if a commerce payment entry audit with the primary key could not be found
	 */
	@Override
	public CommercePaymentEntryAudit remove(Serializable primaryKey)
		throws NoSuchPaymentEntryAuditException {

		Session session = null;

		try {
			session = openSession();

			CommercePaymentEntryAudit commercePaymentEntryAudit =
				(CommercePaymentEntryAudit)session.get(
					CommercePaymentEntryAuditImpl.class, primaryKey);

			if (commercePaymentEntryAudit == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchPaymentEntryAuditException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(commercePaymentEntryAudit);
		}
		catch (NoSuchPaymentEntryAuditException noSuchEntityException) {
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
	protected CommercePaymentEntryAudit removeImpl(
		CommercePaymentEntryAudit commercePaymentEntryAudit) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commercePaymentEntryAudit)) {
				commercePaymentEntryAudit =
					(CommercePaymentEntryAudit)session.get(
						CommercePaymentEntryAuditImpl.class,
						commercePaymentEntryAudit.getPrimaryKeyObj());
			}

			if (commercePaymentEntryAudit != null) {
				session.delete(commercePaymentEntryAudit);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commercePaymentEntryAudit != null) {
			clearCache(commercePaymentEntryAudit);
		}

		return commercePaymentEntryAudit;
	}

	@Override
	public CommercePaymentEntryAudit updateImpl(
		CommercePaymentEntryAudit commercePaymentEntryAudit) {

		boolean isNew = commercePaymentEntryAudit.isNew();

		if (!(commercePaymentEntryAudit instanceof
				CommercePaymentEntryAuditModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(commercePaymentEntryAudit.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					commercePaymentEntryAudit);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commercePaymentEntryAudit proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommercePaymentEntryAudit implementation " +
					commercePaymentEntryAudit.getClass());
		}

		CommercePaymentEntryAuditModelImpl commercePaymentEntryAuditModelImpl =
			(CommercePaymentEntryAuditModelImpl)commercePaymentEntryAudit;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commercePaymentEntryAudit.getCreateDate() == null)) {
			if (serviceContext == null) {
				commercePaymentEntryAudit.setCreateDate(date);
			}
			else {
				commercePaymentEntryAudit.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commercePaymentEntryAuditModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commercePaymentEntryAudit.setModifiedDate(date);
			}
			else {
				commercePaymentEntryAudit.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commercePaymentEntryAudit);
			}
			else {
				commercePaymentEntryAudit =
					(CommercePaymentEntryAudit)session.merge(
						commercePaymentEntryAudit);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			CommercePaymentEntryAuditImpl.class,
			commercePaymentEntryAuditModelImpl, false, true);

		if (isNew) {
			commercePaymentEntryAudit.setNew(false);
		}

		commercePaymentEntryAudit.resetOriginalValues();

		return commercePaymentEntryAudit;
	}

	/**
	 * Returns the commerce payment entry audit with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the commerce payment entry audit
	 * @return the commerce payment entry audit
	 * @throws NoSuchPaymentEntryAuditException if a commerce payment entry audit with the primary key could not be found
	 */
	@Override
	public CommercePaymentEntryAudit findByPrimaryKey(Serializable primaryKey)
		throws NoSuchPaymentEntryAuditException {

		CommercePaymentEntryAudit commercePaymentEntryAudit = fetchByPrimaryKey(
			primaryKey);

		if (commercePaymentEntryAudit == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchPaymentEntryAuditException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return commercePaymentEntryAudit;
	}

	/**
	 * Returns the commerce payment entry audit with the primary key or throws a <code>NoSuchPaymentEntryAuditException</code> if it could not be found.
	 *
	 * @param commercePaymentEntryAuditId the primary key of the commerce payment entry audit
	 * @return the commerce payment entry audit
	 * @throws NoSuchPaymentEntryAuditException if a commerce payment entry audit with the primary key could not be found
	 */
	@Override
	public CommercePaymentEntryAudit findByPrimaryKey(
			long commercePaymentEntryAuditId)
		throws NoSuchPaymentEntryAuditException {

		return findByPrimaryKey((Serializable)commercePaymentEntryAuditId);
	}

	/**
	 * Returns the commerce payment entry audit with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commercePaymentEntryAuditId the primary key of the commerce payment entry audit
	 * @return the commerce payment entry audit, or <code>null</code> if a commerce payment entry audit with the primary key could not be found
	 */
	@Override
	public CommercePaymentEntryAudit fetchByPrimaryKey(
		long commercePaymentEntryAuditId) {

		return fetchByPrimaryKey((Serializable)commercePaymentEntryAuditId);
	}

	/**
	 * Returns all the commerce payment entry audits.
	 *
	 * @return the commerce payment entry audits
	 */
	@Override
	public List<CommercePaymentEntryAudit> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce payment entry audits.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryAuditModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce payment entry audits
	 * @param end the upper bound of the range of commerce payment entry audits (not inclusive)
	 * @return the range of commerce payment entry audits
	 */
	@Override
	public List<CommercePaymentEntryAudit> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce payment entry audits.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryAuditModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce payment entry audits
	 * @param end the upper bound of the range of commerce payment entry audits (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of commerce payment entry audits
	 */
	@Override
	public List<CommercePaymentEntryAudit> findAll(
		int start, int end,
		OrderByComparator<CommercePaymentEntryAudit> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce payment entry audits.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentEntryAuditModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce payment entry audits
	 * @param end the upper bound of the range of commerce payment entry audits (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of commerce payment entry audits
	 */
	@Override
	public List<CommercePaymentEntryAudit> findAll(
		int start, int end,
		OrderByComparator<CommercePaymentEntryAudit> orderByComparator,
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

		List<CommercePaymentEntryAudit> list = null;

		if (useFinderCache) {
			list = (List<CommercePaymentEntryAudit>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_COMMERCEPAYMENTENTRYAUDIT);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_COMMERCEPAYMENTENTRYAUDIT;

				sql = sql.concat(
					CommercePaymentEntryAuditModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<CommercePaymentEntryAudit>)QueryUtil.list(
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
	 * Removes all the commerce payment entry audits from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (CommercePaymentEntryAudit commercePaymentEntryAudit : findAll()) {
			remove(commercePaymentEntryAudit);
		}
	}

	/**
	 * Returns the number of commerce payment entry audits.
	 *
	 * @return the number of commerce payment entry audits
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
					_SQL_COUNT_COMMERCEPAYMENTENTRYAUDIT);

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
		return "commercePaymentEntryAuditId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEPAYMENTENTRYAUDIT;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommercePaymentEntryAuditModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce payment entry audit persistence.
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

		_finderPathWithPaginationFindByCommercePaymentEntryId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
			"findByCommercePaymentEntryId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"commercePaymentEntryId"}, true);

		_finderPathWithoutPaginationFindByCommercePaymentEntryId =
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
				"findByCommercePaymentEntryId",
				new String[] {Long.class.getName()},
				new String[] {"commercePaymentEntryId"}, true);

		_finderPathCountByCommercePaymentEntryId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByCommercePaymentEntryId",
			new String[] {Long.class.getName()},
			new String[] {"commercePaymentEntryId"}, false);

		CommercePaymentEntryAuditUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommercePaymentEntryAuditUtil.setPersistence(null);

		entityCache.removeCache(CommercePaymentEntryAuditImpl.class.getName());
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

	private static final String _SQL_SELECT_COMMERCEPAYMENTENTRYAUDIT =
		"SELECT commercePaymentEntryAudit FROM CommercePaymentEntryAudit commercePaymentEntryAudit";

	private static final String _SQL_SELECT_COMMERCEPAYMENTENTRYAUDIT_WHERE =
		"SELECT commercePaymentEntryAudit FROM CommercePaymentEntryAudit commercePaymentEntryAudit WHERE ";

	private static final String _SQL_COUNT_COMMERCEPAYMENTENTRYAUDIT =
		"SELECT COUNT(commercePaymentEntryAudit) FROM CommercePaymentEntryAudit commercePaymentEntryAudit";

	private static final String _SQL_COUNT_COMMERCEPAYMENTENTRYAUDIT_WHERE =
		"SELECT COUNT(commercePaymentEntryAudit) FROM CommercePaymentEntryAudit commercePaymentEntryAudit WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"commercePaymentEntryAudit.commercePaymentEntryAuditId";

	private static final String
		_FILTER_SQL_SELECT_COMMERCEPAYMENTENTRYAUDIT_WHERE =
			"SELECT DISTINCT {commercePaymentEntryAudit.*} FROM CommercePaymentEntryAudit commercePaymentEntryAudit WHERE ";

	private static final String
		_FILTER_SQL_SELECT_COMMERCEPAYMENTENTRYAUDIT_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {CommercePaymentEntryAudit.*} FROM (SELECT DISTINCT commercePaymentEntryAudit.commercePaymentEntryAuditId FROM CommercePaymentEntryAudit commercePaymentEntryAudit WHERE ";

	private static final String
		_FILTER_SQL_SELECT_COMMERCEPAYMENTENTRYAUDIT_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN CommercePaymentEntryAudit ON TEMP_TABLE.commercePaymentEntryAuditId = CommercePaymentEntryAudit.commercePaymentEntryAuditId";

	private static final String
		_FILTER_SQL_COUNT_COMMERCEPAYMENTENTRYAUDIT_WHERE =
			"SELECT COUNT(DISTINCT commercePaymentEntryAudit.commercePaymentEntryAuditId) AS COUNT_VALUE FROM CommercePaymentEntryAudit commercePaymentEntryAudit WHERE ";

	private static final String _FILTER_ENTITY_ALIAS =
		"commercePaymentEntryAudit";

	private static final String _FILTER_ENTITY_TABLE =
		"CommercePaymentEntryAudit";

	private static final String _ORDER_BY_ENTITY_ALIAS =
		"commercePaymentEntryAudit.";

	private static final String _ORDER_BY_ENTITY_TABLE =
		"CommercePaymentEntryAudit.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No CommercePaymentEntryAudit exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommercePaymentEntryAudit exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommercePaymentEntryAuditPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}