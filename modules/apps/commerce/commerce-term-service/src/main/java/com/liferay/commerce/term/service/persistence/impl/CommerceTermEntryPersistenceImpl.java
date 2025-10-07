/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.term.service.persistence.impl;

import com.liferay.commerce.term.exception.DuplicateCommerceTermEntryExternalReferenceCodeException;
import com.liferay.commerce.term.exception.NoSuchTermEntryException;
import com.liferay.commerce.term.model.CommerceTermEntry;
import com.liferay.commerce.term.model.CommerceTermEntryTable;
import com.liferay.commerce.term.model.impl.CommerceTermEntryImpl;
import com.liferay.commerce.term.model.impl.CommerceTermEntryModelImpl;
import com.liferay.commerce.term.service.persistence.CTermEntryLocalizationPersistence;
import com.liferay.commerce.term.service.persistence.CommerceTermEntryPersistence;
import com.liferay.commerce.term.service.persistence.CommerceTermEntryUtil;
import com.liferay.commerce.term.service.persistence.impl.constants.CommercePersistenceConstants;
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
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.sql.Timestamp;

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
 * The persistence implementation for the commerce term entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Luca Pellizzon
 * @generated
 */
@Component(service = CommerceTermEntryPersistence.class)
public class CommerceTermEntryPersistenceImpl
	extends BasePersistenceImpl<CommerceTermEntry>
	implements CommerceTermEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceTermEntryUtil</code> to access the commerce term entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceTermEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByUuid;
	private FinderPath _finderPathWithoutPaginationFindByUuid;
	private FinderPath _finderPathCountByUuid;

	/**
	 * Returns all the commerce term entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching commerce term entries
	 */
	@Override
	public List<CommerceTermEntry> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce term entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @return the range of matching commerce term entries
	 */
	@Override
	public List<CommerceTermEntry> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce term entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce term entries
	 */
	@Override
	public List<CommerceTermEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceTermEntry> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce term entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce term entries
	 */
	@Override
	public List<CommerceTermEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceTermEntry> orderByComparator,
		boolean useFinderCache) {

		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByUuid;
				finderArgs = new Object[] {uuid};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByUuid;
			finderArgs = new Object[] {uuid, start, end, orderByComparator};
		}

		List<CommerceTermEntry> list = null;

		if (useFinderCache) {
			list = (List<CommerceTermEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommerceTermEntry commerceTermEntry : list) {
					if (!uuid.equals(commerceTermEntry.getUuid())) {
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

			sb.append(_SQL_SELECT_COMMERCETERMENTRY_WHERE);

			boolean bindUuid = false;

			if (uuid.isEmpty()) {
				sb.append(_FINDER_COLUMN_UUID_UUID_3);
			}
			else {
				bindUuid = true;

				sb.append(_FINDER_COLUMN_UUID_UUID_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CommerceTermEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindUuid) {
					queryPos.add(uuid);
				}

				list = (List<CommerceTermEntry>)QueryUtil.list(
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
	 * Returns the first commerce term entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce term entry
	 * @throws NoSuchTermEntryException if a matching commerce term entry could not be found
	 */
	@Override
	public CommerceTermEntry findByUuid_First(
			String uuid, OrderByComparator<CommerceTermEntry> orderByComparator)
		throws NoSuchTermEntryException {

		CommerceTermEntry commerceTermEntry = fetchByUuid_First(
			uuid, orderByComparator);

		if (commerceTermEntry != null) {
			return commerceTermEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchTermEntryException(sb.toString());
	}

	/**
	 * Returns the first commerce term entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce term entry, or <code>null</code> if a matching commerce term entry could not be found
	 */
	@Override
	public CommerceTermEntry fetchByUuid_First(
		String uuid, OrderByComparator<CommerceTermEntry> orderByComparator) {

		List<CommerceTermEntry> list = findByUuid(
			uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce term entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce term entry
	 * @throws NoSuchTermEntryException if a matching commerce term entry could not be found
	 */
	@Override
	public CommerceTermEntry findByUuid_Last(
			String uuid, OrderByComparator<CommerceTermEntry> orderByComparator)
		throws NoSuchTermEntryException {

		CommerceTermEntry commerceTermEntry = fetchByUuid_Last(
			uuid, orderByComparator);

		if (commerceTermEntry != null) {
			return commerceTermEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchTermEntryException(sb.toString());
	}

	/**
	 * Returns the last commerce term entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce term entry, or <code>null</code> if a matching commerce term entry could not be found
	 */
	@Override
	public CommerceTermEntry fetchByUuid_Last(
		String uuid, OrderByComparator<CommerceTermEntry> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<CommerceTermEntry> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce term entries before and after the current commerce term entry in the ordered set where uuid = &#63;.
	 *
	 * @param commerceTermEntryId the primary key of the current commerce term entry
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce term entry
	 * @throws NoSuchTermEntryException if a commerce term entry with the primary key could not be found
	 */
	@Override
	public CommerceTermEntry[] findByUuid_PrevAndNext(
			long commerceTermEntryId, String uuid,
			OrderByComparator<CommerceTermEntry> orderByComparator)
		throws NoSuchTermEntryException {

		uuid = Objects.toString(uuid, "");

		CommerceTermEntry commerceTermEntry = findByPrimaryKey(
			commerceTermEntryId);

		Session session = null;

		try {
			session = openSession();

			CommerceTermEntry[] array = new CommerceTermEntryImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, commerceTermEntry, uuid, orderByComparator, true);

			array[1] = commerceTermEntry;

			array[2] = getByUuid_PrevAndNext(
				session, commerceTermEntry, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CommerceTermEntry getByUuid_PrevAndNext(
		Session session, CommerceTermEntry commerceTermEntry, String uuid,
		OrderByComparator<CommerceTermEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_COMMERCETERMENTRY_WHERE);

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_UUID_3);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_UUID_2);
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
			sb.append(CommerceTermEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindUuid) {
			queryPos.add(uuid);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceTermEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceTermEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the commerce term entries that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching commerce term entries that the user has permission to view
	 */
	@Override
	public List<CommerceTermEntry> filterFindByUuid(String uuid) {
		return filterFindByUuid(
			uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce term entries that the user has permission to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @return the range of matching commerce term entries that the user has permission to view
	 */
	@Override
	public List<CommerceTermEntry> filterFindByUuid(
		String uuid, int start, int end) {

		return filterFindByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce term entries that the user has permissions to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce term entries that the user has permission to view
	 */
	@Override
	public List<CommerceTermEntry> filterFindByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceTermEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByUuid(uuid, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByUuid(
					uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator));
		}

		uuid = Objects.toString(uuid, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				3 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(4);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_COMMERCETERMENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCETERMENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_UUID_3_SQL);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_UUID_2_SQL);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCETERMENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceTermEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceTermEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceTermEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, CommerceTermEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, CommerceTermEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindUuid) {
				queryPos.add(uuid);
			}

			return (List<CommerceTermEntry>)QueryUtil.list(
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
	 * Returns the commerce term entries before and after the current commerce term entry in the ordered set of commerce term entries that the user has permission to view where uuid = &#63;.
	 *
	 * @param commerceTermEntryId the primary key of the current commerce term entry
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce term entry
	 * @throws NoSuchTermEntryException if a commerce term entry with the primary key could not be found
	 */
	@Override
	public CommerceTermEntry[] filterFindByUuid_PrevAndNext(
			long commerceTermEntryId, String uuid,
			OrderByComparator<CommerceTermEntry> orderByComparator)
		throws NoSuchTermEntryException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByUuid_PrevAndNext(
				commerceTermEntryId, uuid, orderByComparator);
		}

		uuid = Objects.toString(uuid, "");

		CommerceTermEntry commerceTermEntry = findByPrimaryKey(
			commerceTermEntryId);

		Session session = null;

		try {
			session = openSession();

			CommerceTermEntry[] array = new CommerceTermEntryImpl[3];

			array[0] = filterGetByUuid_PrevAndNext(
				session, commerceTermEntry, uuid, orderByComparator, true);

			array[1] = commerceTermEntry;

			array[2] = filterGetByUuid_PrevAndNext(
				session, commerceTermEntry, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CommerceTermEntry filterGetByUuid_PrevAndNext(
		Session session, CommerceTermEntry commerceTermEntry, String uuid,
		OrderByComparator<CommerceTermEntry> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_COMMERCETERMENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCETERMENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_UUID_3_SQL);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_UUID_2_SQL);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCETERMENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceTermEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceTermEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceTermEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, CommerceTermEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, CommerceTermEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		if (bindUuid) {
			queryPos.add(uuid);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceTermEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceTermEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce term entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (CommerceTermEntry commerceTermEntry :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(commerceTermEntry);
		}
	}

	/**
	 * Returns the number of commerce term entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce term entries
	 */
	@Override
	public int countByUuid(String uuid) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid;

		Object[] finderArgs = new Object[] {uuid};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_COMMERCETERMENTRY_WHERE);

			boolean bindUuid = false;

			if (uuid.isEmpty()) {
				sb.append(_FINDER_COLUMN_UUID_UUID_3);
			}
			else {
				bindUuid = true;

				sb.append(_FINDER_COLUMN_UUID_UUID_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindUuid) {
					queryPos.add(uuid);
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
	 * Returns the number of commerce term entries that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce term entries that the user has permission to view
	 */
	@Override
	public int filterCountByUuid(String uuid) {
		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByUuid(uuid);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CommerceTermEntry> commerceTermEntries = findByUuid(uuid);

			commerceTermEntries = InlineSQLHelperUtil.filter(
				commerceTermEntries);

			return commerceTermEntries.size();
		}

		uuid = Objects.toString(uuid, "");

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_COMMERCETERMENTRY_WHERE);

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_UUID_3_SQL);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_UUID_2_SQL);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceTermEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindUuid) {
				queryPos.add(uuid);
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

	private static final String _FINDER_COLUMN_UUID_UUID_2 =
		"commerceTermEntry.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(commerceTermEntry.uuid IS NULL OR commerceTermEntry.uuid = '')";

	private static final String _FINDER_COLUMN_UUID_UUID_2_SQL =
		"commerceTermEntry.uuid_ = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3_SQL =
		"(commerceTermEntry.uuid_ IS NULL OR commerceTermEntry.uuid_ = '')";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the commerce term entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching commerce term entries
	 */
	@Override
	public List<CommerceTermEntry> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce term entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @return the range of matching commerce term entries
	 */
	@Override
	public List<CommerceTermEntry> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce term entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce term entries
	 */
	@Override
	public List<CommerceTermEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceTermEntry> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce term entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce term entries
	 */
	@Override
	public List<CommerceTermEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceTermEntry> orderByComparator,
		boolean useFinderCache) {

		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByUuid_C;
				finderArgs = new Object[] {uuid, companyId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByUuid_C;
			finderArgs = new Object[] {
				uuid, companyId, start, end, orderByComparator
			};
		}

		List<CommerceTermEntry> list = null;

		if (useFinderCache) {
			list = (List<CommerceTermEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommerceTermEntry commerceTermEntry : list) {
					if (!uuid.equals(commerceTermEntry.getUuid()) ||
						(companyId != commerceTermEntry.getCompanyId())) {

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

			sb.append(_SQL_SELECT_COMMERCETERMENTRY_WHERE);

			boolean bindUuid = false;

			if (uuid.isEmpty()) {
				sb.append(_FINDER_COLUMN_UUID_C_UUID_3);
			}
			else {
				bindUuid = true;

				sb.append(_FINDER_COLUMN_UUID_C_UUID_2);
			}

			sb.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CommerceTermEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindUuid) {
					queryPos.add(uuid);
				}

				queryPos.add(companyId);

				list = (List<CommerceTermEntry>)QueryUtil.list(
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
	 * Returns the first commerce term entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce term entry
	 * @throws NoSuchTermEntryException if a matching commerce term entry could not be found
	 */
	@Override
	public CommerceTermEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommerceTermEntry> orderByComparator)
		throws NoSuchTermEntryException {

		CommerceTermEntry commerceTermEntry = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (commerceTermEntry != null) {
			return commerceTermEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchTermEntryException(sb.toString());
	}

	/**
	 * Returns the first commerce term entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce term entry, or <code>null</code> if a matching commerce term entry could not be found
	 */
	@Override
	public CommerceTermEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommerceTermEntry> orderByComparator) {

		List<CommerceTermEntry> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce term entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce term entry
	 * @throws NoSuchTermEntryException if a matching commerce term entry could not be found
	 */
	@Override
	public CommerceTermEntry findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<CommerceTermEntry> orderByComparator)
		throws NoSuchTermEntryException {

		CommerceTermEntry commerceTermEntry = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (commerceTermEntry != null) {
			return commerceTermEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchTermEntryException(sb.toString());
	}

	/**
	 * Returns the last commerce term entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce term entry, or <code>null</code> if a matching commerce term entry could not be found
	 */
	@Override
	public CommerceTermEntry fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<CommerceTermEntry> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<CommerceTermEntry> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce term entries before and after the current commerce term entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param commerceTermEntryId the primary key of the current commerce term entry
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce term entry
	 * @throws NoSuchTermEntryException if a commerce term entry with the primary key could not be found
	 */
	@Override
	public CommerceTermEntry[] findByUuid_C_PrevAndNext(
			long commerceTermEntryId, String uuid, long companyId,
			OrderByComparator<CommerceTermEntry> orderByComparator)
		throws NoSuchTermEntryException {

		uuid = Objects.toString(uuid, "");

		CommerceTermEntry commerceTermEntry = findByPrimaryKey(
			commerceTermEntryId);

		Session session = null;

		try {
			session = openSession();

			CommerceTermEntry[] array = new CommerceTermEntryImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, commerceTermEntry, uuid, companyId, orderByComparator,
				true);

			array[1] = commerceTermEntry;

			array[2] = getByUuid_C_PrevAndNext(
				session, commerceTermEntry, uuid, companyId, orderByComparator,
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

	protected CommerceTermEntry getByUuid_C_PrevAndNext(
		Session session, CommerceTermEntry commerceTermEntry, String uuid,
		long companyId, OrderByComparator<CommerceTermEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_COMMERCETERMENTRY_WHERE);

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_C_UUID_3);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_C_UUID_2);
		}

		sb.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

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
			sb.append(CommerceTermEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindUuid) {
			queryPos.add(uuid);
		}

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceTermEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceTermEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the commerce term entries that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching commerce term entries that the user has permission to view
	 */
	@Override
	public List<CommerceTermEntry> filterFindByUuid_C(
		String uuid, long companyId) {

		return filterFindByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce term entries that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @return the range of matching commerce term entries that the user has permission to view
	 */
	@Override
	public List<CommerceTermEntry> filterFindByUuid_C(
		String uuid, long companyId, int start, int end) {

		return filterFindByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce term entries that the user has permissions to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce term entries that the user has permission to view
	 */
	@Override
	public List<CommerceTermEntry> filterFindByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceTermEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByUuid_C(uuid, companyId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator));
		}

		uuid = Objects.toString(uuid, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_COMMERCETERMENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCETERMENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_C_UUID_3_SQL);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_C_UUID_2_SQL);
		}

		sb.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCETERMENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceTermEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceTermEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceTermEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, CommerceTermEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, CommerceTermEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindUuid) {
				queryPos.add(uuid);
			}

			queryPos.add(companyId);

			return (List<CommerceTermEntry>)QueryUtil.list(
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
	 * Returns the commerce term entries before and after the current commerce term entry in the ordered set of commerce term entries that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param commerceTermEntryId the primary key of the current commerce term entry
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce term entry
	 * @throws NoSuchTermEntryException if a commerce term entry with the primary key could not be found
	 */
	@Override
	public CommerceTermEntry[] filterFindByUuid_C_PrevAndNext(
			long commerceTermEntryId, String uuid, long companyId,
			OrderByComparator<CommerceTermEntry> orderByComparator)
		throws NoSuchTermEntryException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByUuid_C_PrevAndNext(
				commerceTermEntryId, uuid, companyId, orderByComparator);
		}

		uuid = Objects.toString(uuid, "");

		CommerceTermEntry commerceTermEntry = findByPrimaryKey(
			commerceTermEntryId);

		Session session = null;

		try {
			session = openSession();

			CommerceTermEntry[] array = new CommerceTermEntryImpl[3];

			array[0] = filterGetByUuid_C_PrevAndNext(
				session, commerceTermEntry, uuid, companyId, orderByComparator,
				true);

			array[1] = commerceTermEntry;

			array[2] = filterGetByUuid_C_PrevAndNext(
				session, commerceTermEntry, uuid, companyId, orderByComparator,
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

	protected CommerceTermEntry filterGetByUuid_C_PrevAndNext(
		Session session, CommerceTermEntry commerceTermEntry, String uuid,
		long companyId, OrderByComparator<CommerceTermEntry> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_COMMERCETERMENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCETERMENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_C_UUID_3_SQL);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_C_UUID_2_SQL);
		}

		sb.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCETERMENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceTermEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceTermEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceTermEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, CommerceTermEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, CommerceTermEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		if (bindUuid) {
			queryPos.add(uuid);
		}

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceTermEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceTermEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce term entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (CommerceTermEntry commerceTermEntry :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(commerceTermEntry);
		}
	}

	/**
	 * Returns the number of commerce term entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce term entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid_C;

		Object[] finderArgs = new Object[] {uuid, companyId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_COMMERCETERMENTRY_WHERE);

			boolean bindUuid = false;

			if (uuid.isEmpty()) {
				sb.append(_FINDER_COLUMN_UUID_C_UUID_3);
			}
			else {
				bindUuid = true;

				sb.append(_FINDER_COLUMN_UUID_C_UUID_2);
			}

			sb.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindUuid) {
					queryPos.add(uuid);
				}

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
	 * Returns the number of commerce term entries that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce term entries that the user has permission to view
	 */
	@Override
	public int filterCountByUuid_C(String uuid, long companyId) {
		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByUuid_C(uuid, companyId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CommerceTermEntry> commerceTermEntries = findByUuid_C(
				uuid, companyId);

			commerceTermEntries = InlineSQLHelperUtil.filter(
				commerceTermEntries);

			return commerceTermEntries.size();
		}

		uuid = Objects.toString(uuid, "");

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_COMMERCETERMENTRY_WHERE);

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_C_UUID_3_SQL);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_C_UUID_2_SQL);
		}

		sb.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceTermEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindUuid) {
				queryPos.add(uuid);
			}

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

	private static final String _FINDER_COLUMN_UUID_C_UUID_2 =
		"commerceTermEntry.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(commerceTermEntry.uuid IS NULL OR commerceTermEntry.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_2_SQL =
		"commerceTermEntry.uuid_ = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3_SQL =
		"(commerceTermEntry.uuid_ IS NULL OR commerceTermEntry.uuid_ = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"commerceTermEntry.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByC_A;
	private FinderPath _finderPathWithoutPaginationFindByC_A;
	private FinderPath _finderPathCountByC_A;

	/**
	 * Returns all the commerce term entries where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the matching commerce term entries
	 */
	@Override
	public List<CommerceTermEntry> findByC_A(long companyId, boolean active) {
		return findByC_A(
			companyId, active, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce term entries where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @return the range of matching commerce term entries
	 */
	@Override
	public List<CommerceTermEntry> findByC_A(
		long companyId, boolean active, int start, int end) {

		return findByC_A(companyId, active, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce term entries where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce term entries
	 */
	@Override
	public List<CommerceTermEntry> findByC_A(
		long companyId, boolean active, int start, int end,
		OrderByComparator<CommerceTermEntry> orderByComparator) {

		return findByC_A(
			companyId, active, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce term entries where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce term entries
	 */
	@Override
	public List<CommerceTermEntry> findByC_A(
		long companyId, boolean active, int start, int end,
		OrderByComparator<CommerceTermEntry> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByC_A;
				finderArgs = new Object[] {companyId, active};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByC_A;
			finderArgs = new Object[] {
				companyId, active, start, end, orderByComparator
			};
		}

		List<CommerceTermEntry> list = null;

		if (useFinderCache) {
			list = (List<CommerceTermEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommerceTermEntry commerceTermEntry : list) {
					if ((companyId != commerceTermEntry.getCompanyId()) ||
						(active != commerceTermEntry.isActive())) {

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

			sb.append(_SQL_SELECT_COMMERCETERMENTRY_WHERE);

			sb.append(_FINDER_COLUMN_C_A_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_A_ACTIVE_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CommerceTermEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(active);

				list = (List<CommerceTermEntry>)QueryUtil.list(
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
	 * Returns the first commerce term entry in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce term entry
	 * @throws NoSuchTermEntryException if a matching commerce term entry could not be found
	 */
	@Override
	public CommerceTermEntry findByC_A_First(
			long companyId, boolean active,
			OrderByComparator<CommerceTermEntry> orderByComparator)
		throws NoSuchTermEntryException {

		CommerceTermEntry commerceTermEntry = fetchByC_A_First(
			companyId, active, orderByComparator);

		if (commerceTermEntry != null) {
			return commerceTermEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", active=");
		sb.append(active);

		sb.append("}");

		throw new NoSuchTermEntryException(sb.toString());
	}

	/**
	 * Returns the first commerce term entry in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce term entry, or <code>null</code> if a matching commerce term entry could not be found
	 */
	@Override
	public CommerceTermEntry fetchByC_A_First(
		long companyId, boolean active,
		OrderByComparator<CommerceTermEntry> orderByComparator) {

		List<CommerceTermEntry> list = findByC_A(
			companyId, active, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce term entry in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce term entry
	 * @throws NoSuchTermEntryException if a matching commerce term entry could not be found
	 */
	@Override
	public CommerceTermEntry findByC_A_Last(
			long companyId, boolean active,
			OrderByComparator<CommerceTermEntry> orderByComparator)
		throws NoSuchTermEntryException {

		CommerceTermEntry commerceTermEntry = fetchByC_A_Last(
			companyId, active, orderByComparator);

		if (commerceTermEntry != null) {
			return commerceTermEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", active=");
		sb.append(active);

		sb.append("}");

		throw new NoSuchTermEntryException(sb.toString());
	}

	/**
	 * Returns the last commerce term entry in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce term entry, or <code>null</code> if a matching commerce term entry could not be found
	 */
	@Override
	public CommerceTermEntry fetchByC_A_Last(
		long companyId, boolean active,
		OrderByComparator<CommerceTermEntry> orderByComparator) {

		int count = countByC_A(companyId, active);

		if (count == 0) {
			return null;
		}

		List<CommerceTermEntry> list = findByC_A(
			companyId, active, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce term entries before and after the current commerce term entry in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param commerceTermEntryId the primary key of the current commerce term entry
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce term entry
	 * @throws NoSuchTermEntryException if a commerce term entry with the primary key could not be found
	 */
	@Override
	public CommerceTermEntry[] findByC_A_PrevAndNext(
			long commerceTermEntryId, long companyId, boolean active,
			OrderByComparator<CommerceTermEntry> orderByComparator)
		throws NoSuchTermEntryException {

		CommerceTermEntry commerceTermEntry = findByPrimaryKey(
			commerceTermEntryId);

		Session session = null;

		try {
			session = openSession();

			CommerceTermEntry[] array = new CommerceTermEntryImpl[3];

			array[0] = getByC_A_PrevAndNext(
				session, commerceTermEntry, companyId, active,
				orderByComparator, true);

			array[1] = commerceTermEntry;

			array[2] = getByC_A_PrevAndNext(
				session, commerceTermEntry, companyId, active,
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

	protected CommerceTermEntry getByC_A_PrevAndNext(
		Session session, CommerceTermEntry commerceTermEntry, long companyId,
		boolean active, OrderByComparator<CommerceTermEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_COMMERCETERMENTRY_WHERE);

		sb.append(_FINDER_COLUMN_C_A_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_A_ACTIVE_2);

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
			sb.append(CommerceTermEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(active);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceTermEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceTermEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the commerce term entries that the user has permission to view where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the matching commerce term entries that the user has permission to view
	 */
	@Override
	public List<CommerceTermEntry> filterFindByC_A(
		long companyId, boolean active) {

		return filterFindByC_A(
			companyId, active, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce term entries that the user has permission to view where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @return the range of matching commerce term entries that the user has permission to view
	 */
	@Override
	public List<CommerceTermEntry> filterFindByC_A(
		long companyId, boolean active, int start, int end) {

		return filterFindByC_A(companyId, active, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce term entries that the user has permissions to view where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce term entries that the user has permission to view
	 */
	@Override
	public List<CommerceTermEntry> filterFindByC_A(
		long companyId, boolean active, int start, int end,
		OrderByComparator<CommerceTermEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_A(companyId, active, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByC_A(
					companyId, active, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator));
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
			sb.append(_FILTER_SQL_SELECT_COMMERCETERMENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCETERMENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_A_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_A_ACTIVE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCETERMENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceTermEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceTermEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceTermEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, CommerceTermEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, CommerceTermEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			queryPos.add(active);

			return (List<CommerceTermEntry>)QueryUtil.list(
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
	 * Returns the commerce term entries before and after the current commerce term entry in the ordered set of commerce term entries that the user has permission to view where companyId = &#63; and active = &#63;.
	 *
	 * @param commerceTermEntryId the primary key of the current commerce term entry
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce term entry
	 * @throws NoSuchTermEntryException if a commerce term entry with the primary key could not be found
	 */
	@Override
	public CommerceTermEntry[] filterFindByC_A_PrevAndNext(
			long commerceTermEntryId, long companyId, boolean active,
			OrderByComparator<CommerceTermEntry> orderByComparator)
		throws NoSuchTermEntryException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_A_PrevAndNext(
				commerceTermEntryId, companyId, active, orderByComparator);
		}

		CommerceTermEntry commerceTermEntry = findByPrimaryKey(
			commerceTermEntryId);

		Session session = null;

		try {
			session = openSession();

			CommerceTermEntry[] array = new CommerceTermEntryImpl[3];

			array[0] = filterGetByC_A_PrevAndNext(
				session, commerceTermEntry, companyId, active,
				orderByComparator, true);

			array[1] = commerceTermEntry;

			array[2] = filterGetByC_A_PrevAndNext(
				session, commerceTermEntry, companyId, active,
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

	protected CommerceTermEntry filterGetByC_A_PrevAndNext(
		Session session, CommerceTermEntry commerceTermEntry, long companyId,
		boolean active, OrderByComparator<CommerceTermEntry> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_COMMERCETERMENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCETERMENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_A_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_A_ACTIVE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCETERMENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceTermEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceTermEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceTermEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, CommerceTermEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, CommerceTermEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(companyId);

		queryPos.add(active);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceTermEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceTermEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce term entries where companyId = &#63; and active = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 */
	@Override
	public void removeByC_A(long companyId, boolean active) {
		for (CommerceTermEntry commerceTermEntry :
				findByC_A(
					companyId, active, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(commerceTermEntry);
		}
	}

	/**
	 * Returns the number of commerce term entries where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the number of matching commerce term entries
	 */
	@Override
	public int countByC_A(long companyId, boolean active) {
		FinderPath finderPath = _finderPathCountByC_A;

		Object[] finderArgs = new Object[] {companyId, active};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_COMMERCETERMENTRY_WHERE);

			sb.append(_FINDER_COLUMN_C_A_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_A_ACTIVE_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(active);

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
	 * Returns the number of commerce term entries that the user has permission to view where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the number of matching commerce term entries that the user has permission to view
	 */
	@Override
	public int filterCountByC_A(long companyId, boolean active) {
		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByC_A(companyId, active);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CommerceTermEntry> commerceTermEntries = findByC_A(
				companyId, active);

			commerceTermEntries = InlineSQLHelperUtil.filter(
				commerceTermEntries);

			return commerceTermEntries.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_COMMERCETERMENTRY_WHERE);

		sb.append(_FINDER_COLUMN_C_A_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_A_ACTIVE_2_SQL);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceTermEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			queryPos.add(active);

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
		"commerceTermEntry.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_A_ACTIVE_2 =
		"commerceTermEntry.active = ?";

	private static final String _FINDER_COLUMN_C_A_ACTIVE_2_SQL =
		"commerceTermEntry.active_ = ?";

	private FinderPath _finderPathFetchByC_N;

	/**
	 * Returns the commerce term entry where companyId = &#63; and name = &#63; or throws a <code>NoSuchTermEntryException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching commerce term entry
	 * @throws NoSuchTermEntryException if a matching commerce term entry could not be found
	 */
	@Override
	public CommerceTermEntry findByC_N(long companyId, String name)
		throws NoSuchTermEntryException {

		CommerceTermEntry commerceTermEntry = fetchByC_N(companyId, name);

		if (commerceTermEntry == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("companyId=");
			sb.append(companyId);

			sb.append(", name=");
			sb.append(name);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchTermEntryException(sb.toString());
		}

		return commerceTermEntry;
	}

	/**
	 * Returns the commerce term entry where companyId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching commerce term entry, or <code>null</code> if a matching commerce term entry could not be found
	 */
	@Override
	public CommerceTermEntry fetchByC_N(long companyId, String name) {
		return fetchByC_N(companyId, name, true);
	}

	/**
	 * Returns the commerce term entry where companyId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce term entry, or <code>null</code> if a matching commerce term entry could not be found
	 */
	@Override
	public CommerceTermEntry fetchByC_N(
		long companyId, String name, boolean useFinderCache) {

		name = Objects.toString(name, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {companyId, name};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByC_N, finderArgs, this);
		}

		if (result instanceof CommerceTermEntry) {
			CommerceTermEntry commerceTermEntry = (CommerceTermEntry)result;

			if ((companyId != commerceTermEntry.getCompanyId()) ||
				!Objects.equals(name, commerceTermEntry.getName())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_COMMERCETERMENTRY_WHERE);

			sb.append(_FINDER_COLUMN_C_N_COMPANYID_2);

			boolean bindName = false;

			if (name.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_N_NAME_3);
			}
			else {
				bindName = true;

				sb.append(_FINDER_COLUMN_C_N_NAME_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				if (bindName) {
					queryPos.add(name);
				}

				List<CommerceTermEntry> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByC_N, finderArgs, list);
					}
				}
				else {
					CommerceTermEntry commerceTermEntry = list.get(0);

					result = commerceTermEntry;

					cacheResult(commerceTermEntry);
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
			return (CommerceTermEntry)result;
		}
	}

	/**
	 * Removes the commerce term entry where companyId = &#63; and name = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the commerce term entry that was removed
	 */
	@Override
	public CommerceTermEntry removeByC_N(long companyId, String name)
		throws NoSuchTermEntryException {

		CommerceTermEntry commerceTermEntry = findByC_N(companyId, name);

		return remove(commerceTermEntry);
	}

	/**
	 * Returns the number of commerce term entries where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching commerce term entries
	 */
	@Override
	public int countByC_N(long companyId, String name) {
		CommerceTermEntry commerceTermEntry = fetchByC_N(companyId, name);

		if (commerceTermEntry == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_C_N_COMPANYID_2 =
		"commerceTermEntry.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_N_NAME_2 =
		"commerceTermEntry.name = ?";

	private static final String _FINDER_COLUMN_C_N_NAME_3 =
		"(commerceTermEntry.name IS NULL OR commerceTermEntry.name = '')";

	private FinderPath _finderPathWithPaginationFindByC_LikeType;
	private FinderPath _finderPathWithPaginationCountByC_LikeType;

	/**
	 * Returns all the commerce term entries where companyId = &#63; and type LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @return the matching commerce term entries
	 */
	@Override
	public List<CommerceTermEntry> findByC_LikeType(
		long companyId, String type) {

		return findByC_LikeType(
			companyId, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce term entries where companyId = &#63; and type LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @return the range of matching commerce term entries
	 */
	@Override
	public List<CommerceTermEntry> findByC_LikeType(
		long companyId, String type, int start, int end) {

		return findByC_LikeType(companyId, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce term entries where companyId = &#63; and type LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce term entries
	 */
	@Override
	public List<CommerceTermEntry> findByC_LikeType(
		long companyId, String type, int start, int end,
		OrderByComparator<CommerceTermEntry> orderByComparator) {

		return findByC_LikeType(
			companyId, type, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce term entries where companyId = &#63; and type LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce term entries
	 */
	@Override
	public List<CommerceTermEntry> findByC_LikeType(
		long companyId, String type, int start, int end,
		OrderByComparator<CommerceTermEntry> orderByComparator,
		boolean useFinderCache) {

		type = Objects.toString(type, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		finderPath = _finderPathWithPaginationFindByC_LikeType;
		finderArgs = new Object[] {
			companyId, type, start, end, orderByComparator
		};

		List<CommerceTermEntry> list = null;

		if (useFinderCache) {
			list = (List<CommerceTermEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommerceTermEntry commerceTermEntry : list) {
					if ((companyId != commerceTermEntry.getCompanyId()) ||
						!StringUtil.wildcardMatches(
							commerceTermEntry.getType(), type, '_', '%', '\\',
							true)) {

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

			sb.append(_SQL_SELECT_COMMERCETERMENTRY_WHERE);

			sb.append(_FINDER_COLUMN_C_LIKETYPE_COMPANYID_2);

			boolean bindType = false;

			if (type.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_LIKETYPE_TYPE_3);
			}
			else {
				bindType = true;

				sb.append(_FINDER_COLUMN_C_LIKETYPE_TYPE_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CommerceTermEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				if (bindType) {
					queryPos.add(type);
				}

				list = (List<CommerceTermEntry>)QueryUtil.list(
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
	 * Returns the first commerce term entry in the ordered set where companyId = &#63; and type LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce term entry
	 * @throws NoSuchTermEntryException if a matching commerce term entry could not be found
	 */
	@Override
	public CommerceTermEntry findByC_LikeType_First(
			long companyId, String type,
			OrderByComparator<CommerceTermEntry> orderByComparator)
		throws NoSuchTermEntryException {

		CommerceTermEntry commerceTermEntry = fetchByC_LikeType_First(
			companyId, type, orderByComparator);

		if (commerceTermEntry != null) {
			return commerceTermEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", typeLIKE");
		sb.append(type);

		sb.append("}");

		throw new NoSuchTermEntryException(sb.toString());
	}

	/**
	 * Returns the first commerce term entry in the ordered set where companyId = &#63; and type LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce term entry, or <code>null</code> if a matching commerce term entry could not be found
	 */
	@Override
	public CommerceTermEntry fetchByC_LikeType_First(
		long companyId, String type,
		OrderByComparator<CommerceTermEntry> orderByComparator) {

		List<CommerceTermEntry> list = findByC_LikeType(
			companyId, type, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce term entry in the ordered set where companyId = &#63; and type LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce term entry
	 * @throws NoSuchTermEntryException if a matching commerce term entry could not be found
	 */
	@Override
	public CommerceTermEntry findByC_LikeType_Last(
			long companyId, String type,
			OrderByComparator<CommerceTermEntry> orderByComparator)
		throws NoSuchTermEntryException {

		CommerceTermEntry commerceTermEntry = fetchByC_LikeType_Last(
			companyId, type, orderByComparator);

		if (commerceTermEntry != null) {
			return commerceTermEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", typeLIKE");
		sb.append(type);

		sb.append("}");

		throw new NoSuchTermEntryException(sb.toString());
	}

	/**
	 * Returns the last commerce term entry in the ordered set where companyId = &#63; and type LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce term entry, or <code>null</code> if a matching commerce term entry could not be found
	 */
	@Override
	public CommerceTermEntry fetchByC_LikeType_Last(
		long companyId, String type,
		OrderByComparator<CommerceTermEntry> orderByComparator) {

		int count = countByC_LikeType(companyId, type);

		if (count == 0) {
			return null;
		}

		List<CommerceTermEntry> list = findByC_LikeType(
			companyId, type, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce term entries before and after the current commerce term entry in the ordered set where companyId = &#63; and type LIKE &#63;.
	 *
	 * @param commerceTermEntryId the primary key of the current commerce term entry
	 * @param companyId the company ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce term entry
	 * @throws NoSuchTermEntryException if a commerce term entry with the primary key could not be found
	 */
	@Override
	public CommerceTermEntry[] findByC_LikeType_PrevAndNext(
			long commerceTermEntryId, long companyId, String type,
			OrderByComparator<CommerceTermEntry> orderByComparator)
		throws NoSuchTermEntryException {

		type = Objects.toString(type, "");

		CommerceTermEntry commerceTermEntry = findByPrimaryKey(
			commerceTermEntryId);

		Session session = null;

		try {
			session = openSession();

			CommerceTermEntry[] array = new CommerceTermEntryImpl[3];

			array[0] = getByC_LikeType_PrevAndNext(
				session, commerceTermEntry, companyId, type, orderByComparator,
				true);

			array[1] = commerceTermEntry;

			array[2] = getByC_LikeType_PrevAndNext(
				session, commerceTermEntry, companyId, type, orderByComparator,
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

	protected CommerceTermEntry getByC_LikeType_PrevAndNext(
		Session session, CommerceTermEntry commerceTermEntry, long companyId,
		String type, OrderByComparator<CommerceTermEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_COMMERCETERMENTRY_WHERE);

		sb.append(_FINDER_COLUMN_C_LIKETYPE_COMPANYID_2);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_LIKETYPE_TYPE_3);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_C_LIKETYPE_TYPE_2);
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
			sb.append(CommerceTermEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (bindType) {
			queryPos.add(type);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceTermEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceTermEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the commerce term entries that the user has permission to view where companyId = &#63; and type LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @return the matching commerce term entries that the user has permission to view
	 */
	@Override
	public List<CommerceTermEntry> filterFindByC_LikeType(
		long companyId, String type) {

		return filterFindByC_LikeType(
			companyId, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce term entries that the user has permission to view where companyId = &#63; and type LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @return the range of matching commerce term entries that the user has permission to view
	 */
	@Override
	public List<CommerceTermEntry> filterFindByC_LikeType(
		long companyId, String type, int start, int end) {

		return filterFindByC_LikeType(companyId, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce term entries that the user has permissions to view where companyId = &#63; and type LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce term entries that the user has permission to view
	 */
	@Override
	public List<CommerceTermEntry> filterFindByC_LikeType(
		long companyId, String type, int start, int end,
		OrderByComparator<CommerceTermEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_LikeType(
				companyId, type, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByC_LikeType(
					companyId, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator));
		}

		type = Objects.toString(type, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_COMMERCETERMENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCETERMENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_LIKETYPE_COMPANYID_2);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_LIKETYPE_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_C_LIKETYPE_TYPE_2_SQL);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCETERMENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceTermEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceTermEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceTermEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, CommerceTermEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, CommerceTermEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			if (bindType) {
				queryPos.add(type);
			}

			return (List<CommerceTermEntry>)QueryUtil.list(
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
	 * Returns the commerce term entries before and after the current commerce term entry in the ordered set of commerce term entries that the user has permission to view where companyId = &#63; and type LIKE &#63;.
	 *
	 * @param commerceTermEntryId the primary key of the current commerce term entry
	 * @param companyId the company ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce term entry
	 * @throws NoSuchTermEntryException if a commerce term entry with the primary key could not be found
	 */
	@Override
	public CommerceTermEntry[] filterFindByC_LikeType_PrevAndNext(
			long commerceTermEntryId, long companyId, String type,
			OrderByComparator<CommerceTermEntry> orderByComparator)
		throws NoSuchTermEntryException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_LikeType_PrevAndNext(
				commerceTermEntryId, companyId, type, orderByComparator);
		}

		type = Objects.toString(type, "");

		CommerceTermEntry commerceTermEntry = findByPrimaryKey(
			commerceTermEntryId);

		Session session = null;

		try {
			session = openSession();

			CommerceTermEntry[] array = new CommerceTermEntryImpl[3];

			array[0] = filterGetByC_LikeType_PrevAndNext(
				session, commerceTermEntry, companyId, type, orderByComparator,
				true);

			array[1] = commerceTermEntry;

			array[2] = filterGetByC_LikeType_PrevAndNext(
				session, commerceTermEntry, companyId, type, orderByComparator,
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

	protected CommerceTermEntry filterGetByC_LikeType_PrevAndNext(
		Session session, CommerceTermEntry commerceTermEntry, long companyId,
		String type, OrderByComparator<CommerceTermEntry> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_COMMERCETERMENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCETERMENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_LIKETYPE_COMPANYID_2);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_LIKETYPE_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_C_LIKETYPE_TYPE_2_SQL);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCETERMENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceTermEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceTermEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceTermEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, CommerceTermEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, CommerceTermEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(companyId);

		if (bindType) {
			queryPos.add(type);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceTermEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceTermEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce term entries where companyId = &#63; and type LIKE &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 */
	@Override
	public void removeByC_LikeType(long companyId, String type) {
		for (CommerceTermEntry commerceTermEntry :
				findByC_LikeType(
					companyId, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(commerceTermEntry);
		}
	}

	/**
	 * Returns the number of commerce term entries where companyId = &#63; and type LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @return the number of matching commerce term entries
	 */
	@Override
	public int countByC_LikeType(long companyId, String type) {
		type = Objects.toString(type, "");

		FinderPath finderPath = _finderPathWithPaginationCountByC_LikeType;

		Object[] finderArgs = new Object[] {companyId, type};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_COMMERCETERMENTRY_WHERE);

			sb.append(_FINDER_COLUMN_C_LIKETYPE_COMPANYID_2);

			boolean bindType = false;

			if (type.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_LIKETYPE_TYPE_3);
			}
			else {
				bindType = true;

				sb.append(_FINDER_COLUMN_C_LIKETYPE_TYPE_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				if (bindType) {
					queryPos.add(type);
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
	 * Returns the number of commerce term entries that the user has permission to view where companyId = &#63; and type LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @return the number of matching commerce term entries that the user has permission to view
	 */
	@Override
	public int filterCountByC_LikeType(long companyId, String type) {
		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByC_LikeType(companyId, type);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CommerceTermEntry> commerceTermEntries = findByC_LikeType(
				companyId, type);

			commerceTermEntries = InlineSQLHelperUtil.filter(
				commerceTermEntries);

			return commerceTermEntries.size();
		}

		type = Objects.toString(type, "");

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_COMMERCETERMENTRY_WHERE);

		sb.append(_FINDER_COLUMN_C_LIKETYPE_COMPANYID_2);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_LIKETYPE_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_C_LIKETYPE_TYPE_2_SQL);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceTermEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			if (bindType) {
				queryPos.add(type);
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

	private static final String _FINDER_COLUMN_C_LIKETYPE_COMPANYID_2 =
		"commerceTermEntry.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_LIKETYPE_TYPE_2 =
		"commerceTermEntry.type LIKE ?";

	private static final String _FINDER_COLUMN_C_LIKETYPE_TYPE_3 =
		"(commerceTermEntry.type IS NULL OR commerceTermEntry.type LIKE '')";

	private static final String _FINDER_COLUMN_C_LIKETYPE_TYPE_2_SQL =
		"commerceTermEntry.type_ LIKE ?";

	private static final String _FINDER_COLUMN_C_LIKETYPE_TYPE_3_SQL =
		"(commerceTermEntry.type_ IS NULL OR commerceTermEntry.type_ LIKE '')";

	private FinderPath _finderPathWithPaginationFindByLtD_S;
	private FinderPath _finderPathWithPaginationCountByLtD_S;

	/**
	 * Returns all the commerce term entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching commerce term entries
	 */
	@Override
	public List<CommerceTermEntry> findByLtD_S(Date displayDate, int status) {
		return findByLtD_S(
			displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce term entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @return the range of matching commerce term entries
	 */
	@Override
	public List<CommerceTermEntry> findByLtD_S(
		Date displayDate, int status, int start, int end) {

		return findByLtD_S(displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce term entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce term entries
	 */
	@Override
	public List<CommerceTermEntry> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<CommerceTermEntry> orderByComparator) {

		return findByLtD_S(
			displayDate, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce term entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce term entries
	 */
	@Override
	public List<CommerceTermEntry> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<CommerceTermEntry> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		finderPath = _finderPathWithPaginationFindByLtD_S;
		finderArgs = new Object[] {
			_getTime(displayDate), status, start, end, orderByComparator
		};

		List<CommerceTermEntry> list = null;

		if (useFinderCache) {
			list = (List<CommerceTermEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommerceTermEntry commerceTermEntry : list) {
					if ((displayDate.getTime() <=
							commerceTermEntry.getDisplayDate(
							).getTime()) ||
						(status != commerceTermEntry.getStatus())) {

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

			sb.append(_SQL_SELECT_COMMERCETERMENTRY_WHERE);

			boolean bindDisplayDate = false;

			if (displayDate == null) {
				sb.append(_FINDER_COLUMN_LTD_S_DISPLAYDATE_1);
			}
			else {
				bindDisplayDate = true;

				sb.append(_FINDER_COLUMN_LTD_S_DISPLAYDATE_2);
			}

			sb.append(_FINDER_COLUMN_LTD_S_STATUS_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CommerceTermEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindDisplayDate) {
					queryPos.add(new Timestamp(displayDate.getTime()));
				}

				queryPos.add(status);

				list = (List<CommerceTermEntry>)QueryUtil.list(
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
	 * Returns the first commerce term entry in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce term entry
	 * @throws NoSuchTermEntryException if a matching commerce term entry could not be found
	 */
	@Override
	public CommerceTermEntry findByLtD_S_First(
			Date displayDate, int status,
			OrderByComparator<CommerceTermEntry> orderByComparator)
		throws NoSuchTermEntryException {

		CommerceTermEntry commerceTermEntry = fetchByLtD_S_First(
			displayDate, status, orderByComparator);

		if (commerceTermEntry != null) {
			return commerceTermEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("displayDate<");
		sb.append(displayDate);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchTermEntryException(sb.toString());
	}

	/**
	 * Returns the first commerce term entry in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce term entry, or <code>null</code> if a matching commerce term entry could not be found
	 */
	@Override
	public CommerceTermEntry fetchByLtD_S_First(
		Date displayDate, int status,
		OrderByComparator<CommerceTermEntry> orderByComparator) {

		List<CommerceTermEntry> list = findByLtD_S(
			displayDate, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce term entry in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce term entry
	 * @throws NoSuchTermEntryException if a matching commerce term entry could not be found
	 */
	@Override
	public CommerceTermEntry findByLtD_S_Last(
			Date displayDate, int status,
			OrderByComparator<CommerceTermEntry> orderByComparator)
		throws NoSuchTermEntryException {

		CommerceTermEntry commerceTermEntry = fetchByLtD_S_Last(
			displayDate, status, orderByComparator);

		if (commerceTermEntry != null) {
			return commerceTermEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("displayDate<");
		sb.append(displayDate);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchTermEntryException(sb.toString());
	}

	/**
	 * Returns the last commerce term entry in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce term entry, or <code>null</code> if a matching commerce term entry could not be found
	 */
	@Override
	public CommerceTermEntry fetchByLtD_S_Last(
		Date displayDate, int status,
		OrderByComparator<CommerceTermEntry> orderByComparator) {

		int count = countByLtD_S(displayDate, status);

		if (count == 0) {
			return null;
		}

		List<CommerceTermEntry> list = findByLtD_S(
			displayDate, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce term entries before and after the current commerce term entry in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param commerceTermEntryId the primary key of the current commerce term entry
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce term entry
	 * @throws NoSuchTermEntryException if a commerce term entry with the primary key could not be found
	 */
	@Override
	public CommerceTermEntry[] findByLtD_S_PrevAndNext(
			long commerceTermEntryId, Date displayDate, int status,
			OrderByComparator<CommerceTermEntry> orderByComparator)
		throws NoSuchTermEntryException {

		CommerceTermEntry commerceTermEntry = findByPrimaryKey(
			commerceTermEntryId);

		Session session = null;

		try {
			session = openSession();

			CommerceTermEntry[] array = new CommerceTermEntryImpl[3];

			array[0] = getByLtD_S_PrevAndNext(
				session, commerceTermEntry, displayDate, status,
				orderByComparator, true);

			array[1] = commerceTermEntry;

			array[2] = getByLtD_S_PrevAndNext(
				session, commerceTermEntry, displayDate, status,
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

	protected CommerceTermEntry getByLtD_S_PrevAndNext(
		Session session, CommerceTermEntry commerceTermEntry, Date displayDate,
		int status, OrderByComparator<CommerceTermEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_COMMERCETERMENTRY_WHERE);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_LTD_S_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_LTD_S_DISPLAYDATE_2);
		}

		sb.append(_FINDER_COLUMN_LTD_S_STATUS_2);

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
			sb.append(CommerceTermEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindDisplayDate) {
			queryPos.add(new Timestamp(displayDate.getTime()));
		}

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceTermEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceTermEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the commerce term entries that the user has permission to view where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching commerce term entries that the user has permission to view
	 */
	@Override
	public List<CommerceTermEntry> filterFindByLtD_S(
		Date displayDate, int status) {

		return filterFindByLtD_S(
			displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce term entries that the user has permission to view where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @return the range of matching commerce term entries that the user has permission to view
	 */
	@Override
	public List<CommerceTermEntry> filterFindByLtD_S(
		Date displayDate, int status, int start, int end) {

		return filterFindByLtD_S(displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce term entries that the user has permissions to view where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce term entries that the user has permission to view
	 */
	@Override
	public List<CommerceTermEntry> filterFindByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<CommerceTermEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByLtD_S(
				displayDate, status, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByLtD_S(
					displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator));
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
			sb.append(_FILTER_SQL_SELECT_COMMERCETERMENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCETERMENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_LTD_S_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_LTD_S_DISPLAYDATE_2);
		}

		sb.append(_FINDER_COLUMN_LTD_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCETERMENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceTermEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceTermEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceTermEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, CommerceTermEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, CommerceTermEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindDisplayDate) {
				queryPos.add(new Timestamp(displayDate.getTime()));
			}

			queryPos.add(status);

			return (List<CommerceTermEntry>)QueryUtil.list(
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
	 * Returns the commerce term entries before and after the current commerce term entry in the ordered set of commerce term entries that the user has permission to view where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param commerceTermEntryId the primary key of the current commerce term entry
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce term entry
	 * @throws NoSuchTermEntryException if a commerce term entry with the primary key could not be found
	 */
	@Override
	public CommerceTermEntry[] filterFindByLtD_S_PrevAndNext(
			long commerceTermEntryId, Date displayDate, int status,
			OrderByComparator<CommerceTermEntry> orderByComparator)
		throws NoSuchTermEntryException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByLtD_S_PrevAndNext(
				commerceTermEntryId, displayDate, status, orderByComparator);
		}

		CommerceTermEntry commerceTermEntry = findByPrimaryKey(
			commerceTermEntryId);

		Session session = null;

		try {
			session = openSession();

			CommerceTermEntry[] array = new CommerceTermEntryImpl[3];

			array[0] = filterGetByLtD_S_PrevAndNext(
				session, commerceTermEntry, displayDate, status,
				orderByComparator, true);

			array[1] = commerceTermEntry;

			array[2] = filterGetByLtD_S_PrevAndNext(
				session, commerceTermEntry, displayDate, status,
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

	protected CommerceTermEntry filterGetByLtD_S_PrevAndNext(
		Session session, CommerceTermEntry commerceTermEntry, Date displayDate,
		int status, OrderByComparator<CommerceTermEntry> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_COMMERCETERMENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCETERMENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_LTD_S_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_LTD_S_DISPLAYDATE_2);
		}

		sb.append(_FINDER_COLUMN_LTD_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCETERMENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceTermEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceTermEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceTermEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, CommerceTermEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, CommerceTermEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		if (bindDisplayDate) {
			queryPos.add(new Timestamp(displayDate.getTime()));
		}

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceTermEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceTermEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce term entries where displayDate &lt; &#63; and status = &#63; from the database.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 */
	@Override
	public void removeByLtD_S(Date displayDate, int status) {
		for (CommerceTermEntry commerceTermEntry :
				findByLtD_S(
					displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(commerceTermEntry);
		}
	}

	/**
	 * Returns the number of commerce term entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching commerce term entries
	 */
	@Override
	public int countByLtD_S(Date displayDate, int status) {
		FinderPath finderPath = _finderPathWithPaginationCountByLtD_S;

		Object[] finderArgs = new Object[] {_getTime(displayDate), status};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_COMMERCETERMENTRY_WHERE);

			boolean bindDisplayDate = false;

			if (displayDate == null) {
				sb.append(_FINDER_COLUMN_LTD_S_DISPLAYDATE_1);
			}
			else {
				bindDisplayDate = true;

				sb.append(_FINDER_COLUMN_LTD_S_DISPLAYDATE_2);
			}

			sb.append(_FINDER_COLUMN_LTD_S_STATUS_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindDisplayDate) {
					queryPos.add(new Timestamp(displayDate.getTime()));
				}

				queryPos.add(status);

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
	 * Returns the number of commerce term entries that the user has permission to view where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching commerce term entries that the user has permission to view
	 */
	@Override
	public int filterCountByLtD_S(Date displayDate, int status) {
		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByLtD_S(displayDate, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CommerceTermEntry> commerceTermEntries = findByLtD_S(
				displayDate, status);

			commerceTermEntries = InlineSQLHelperUtil.filter(
				commerceTermEntries);

			return commerceTermEntries.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_COMMERCETERMENTRY_WHERE);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_LTD_S_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_LTD_S_DISPLAYDATE_2);
		}

		sb.append(_FINDER_COLUMN_LTD_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceTermEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindDisplayDate) {
				queryPos.add(new Timestamp(displayDate.getTime()));
			}

			queryPos.add(status);

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

	private static final String _FINDER_COLUMN_LTD_S_DISPLAYDATE_1 =
		"commerceTermEntry.displayDate IS NULL AND ";

	private static final String _FINDER_COLUMN_LTD_S_DISPLAYDATE_2 =
		"commerceTermEntry.displayDate < ? AND ";

	private static final String _FINDER_COLUMN_LTD_S_STATUS_2 =
		"commerceTermEntry.status = ?";

	private FinderPath _finderPathWithPaginationFindByLtE_S;
	private FinderPath _finderPathWithPaginationCountByLtE_S;

	/**
	 * Returns all the commerce term entries where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @return the matching commerce term entries
	 */
	@Override
	public List<CommerceTermEntry> findByLtE_S(
		Date expirationDate, int status) {

		return findByLtE_S(
			expirationDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce term entries where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @return the range of matching commerce term entries
	 */
	@Override
	public List<CommerceTermEntry> findByLtE_S(
		Date expirationDate, int status, int start, int end) {

		return findByLtE_S(expirationDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce term entries where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce term entries
	 */
	@Override
	public List<CommerceTermEntry> findByLtE_S(
		Date expirationDate, int status, int start, int end,
		OrderByComparator<CommerceTermEntry> orderByComparator) {

		return findByLtE_S(
			expirationDate, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce term entries where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce term entries
	 */
	@Override
	public List<CommerceTermEntry> findByLtE_S(
		Date expirationDate, int status, int start, int end,
		OrderByComparator<CommerceTermEntry> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		finderPath = _finderPathWithPaginationFindByLtE_S;
		finderArgs = new Object[] {
			_getTime(expirationDate), status, start, end, orderByComparator
		};

		List<CommerceTermEntry> list = null;

		if (useFinderCache) {
			list = (List<CommerceTermEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommerceTermEntry commerceTermEntry : list) {
					if ((expirationDate.getTime() <=
							commerceTermEntry.getExpirationDate(
							).getTime()) ||
						(status != commerceTermEntry.getStatus())) {

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

			sb.append(_SQL_SELECT_COMMERCETERMENTRY_WHERE);

			boolean bindExpirationDate = false;

			if (expirationDate == null) {
				sb.append(_FINDER_COLUMN_LTE_S_EXPIRATIONDATE_1);
			}
			else {
				bindExpirationDate = true;

				sb.append(_FINDER_COLUMN_LTE_S_EXPIRATIONDATE_2);
			}

			sb.append(_FINDER_COLUMN_LTE_S_STATUS_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CommerceTermEntryModelImpl.ORDER_BY_JPQL);
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

				queryPos.add(status);

				list = (List<CommerceTermEntry>)QueryUtil.list(
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
	 * Returns the first commerce term entry in the ordered set where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce term entry
	 * @throws NoSuchTermEntryException if a matching commerce term entry could not be found
	 */
	@Override
	public CommerceTermEntry findByLtE_S_First(
			Date expirationDate, int status,
			OrderByComparator<CommerceTermEntry> orderByComparator)
		throws NoSuchTermEntryException {

		CommerceTermEntry commerceTermEntry = fetchByLtE_S_First(
			expirationDate, status, orderByComparator);

		if (commerceTermEntry != null) {
			return commerceTermEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("expirationDate<");
		sb.append(expirationDate);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchTermEntryException(sb.toString());
	}

	/**
	 * Returns the first commerce term entry in the ordered set where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce term entry, or <code>null</code> if a matching commerce term entry could not be found
	 */
	@Override
	public CommerceTermEntry fetchByLtE_S_First(
		Date expirationDate, int status,
		OrderByComparator<CommerceTermEntry> orderByComparator) {

		List<CommerceTermEntry> list = findByLtE_S(
			expirationDate, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce term entry in the ordered set where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce term entry
	 * @throws NoSuchTermEntryException if a matching commerce term entry could not be found
	 */
	@Override
	public CommerceTermEntry findByLtE_S_Last(
			Date expirationDate, int status,
			OrderByComparator<CommerceTermEntry> orderByComparator)
		throws NoSuchTermEntryException {

		CommerceTermEntry commerceTermEntry = fetchByLtE_S_Last(
			expirationDate, status, orderByComparator);

		if (commerceTermEntry != null) {
			return commerceTermEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("expirationDate<");
		sb.append(expirationDate);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchTermEntryException(sb.toString());
	}

	/**
	 * Returns the last commerce term entry in the ordered set where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce term entry, or <code>null</code> if a matching commerce term entry could not be found
	 */
	@Override
	public CommerceTermEntry fetchByLtE_S_Last(
		Date expirationDate, int status,
		OrderByComparator<CommerceTermEntry> orderByComparator) {

		int count = countByLtE_S(expirationDate, status);

		if (count == 0) {
			return null;
		}

		List<CommerceTermEntry> list = findByLtE_S(
			expirationDate, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce term entries before and after the current commerce term entry in the ordered set where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param commerceTermEntryId the primary key of the current commerce term entry
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce term entry
	 * @throws NoSuchTermEntryException if a commerce term entry with the primary key could not be found
	 */
	@Override
	public CommerceTermEntry[] findByLtE_S_PrevAndNext(
			long commerceTermEntryId, Date expirationDate, int status,
			OrderByComparator<CommerceTermEntry> orderByComparator)
		throws NoSuchTermEntryException {

		CommerceTermEntry commerceTermEntry = findByPrimaryKey(
			commerceTermEntryId);

		Session session = null;

		try {
			session = openSession();

			CommerceTermEntry[] array = new CommerceTermEntryImpl[3];

			array[0] = getByLtE_S_PrevAndNext(
				session, commerceTermEntry, expirationDate, status,
				orderByComparator, true);

			array[1] = commerceTermEntry;

			array[2] = getByLtE_S_PrevAndNext(
				session, commerceTermEntry, expirationDate, status,
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

	protected CommerceTermEntry getByLtE_S_PrevAndNext(
		Session session, CommerceTermEntry commerceTermEntry,
		Date expirationDate, int status,
		OrderByComparator<CommerceTermEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_COMMERCETERMENTRY_WHERE);

		boolean bindExpirationDate = false;

		if (expirationDate == null) {
			sb.append(_FINDER_COLUMN_LTE_S_EXPIRATIONDATE_1);
		}
		else {
			bindExpirationDate = true;

			sb.append(_FINDER_COLUMN_LTE_S_EXPIRATIONDATE_2);
		}

		sb.append(_FINDER_COLUMN_LTE_S_STATUS_2);

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
			sb.append(CommerceTermEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindExpirationDate) {
			queryPos.add(new Timestamp(expirationDate.getTime()));
		}

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceTermEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceTermEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the commerce term entries that the user has permission to view where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @return the matching commerce term entries that the user has permission to view
	 */
	@Override
	public List<CommerceTermEntry> filterFindByLtE_S(
		Date expirationDate, int status) {

		return filterFindByLtE_S(
			expirationDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce term entries that the user has permission to view where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @return the range of matching commerce term entries that the user has permission to view
	 */
	@Override
	public List<CommerceTermEntry> filterFindByLtE_S(
		Date expirationDate, int status, int start, int end) {

		return filterFindByLtE_S(expirationDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce term entries that the user has permissions to view where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce term entries that the user has permission to view
	 */
	@Override
	public List<CommerceTermEntry> filterFindByLtE_S(
		Date expirationDate, int status, int start, int end,
		OrderByComparator<CommerceTermEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByLtE_S(
				expirationDate, status, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByLtE_S(
					expirationDate, status, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_COMMERCETERMENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCETERMENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindExpirationDate = false;

		if (expirationDate == null) {
			sb.append(_FINDER_COLUMN_LTE_S_EXPIRATIONDATE_1);
		}
		else {
			bindExpirationDate = true;

			sb.append(_FINDER_COLUMN_LTE_S_EXPIRATIONDATE_2);
		}

		sb.append(_FINDER_COLUMN_LTE_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCETERMENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceTermEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceTermEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceTermEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, CommerceTermEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, CommerceTermEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindExpirationDate) {
				queryPos.add(new Timestamp(expirationDate.getTime()));
			}

			queryPos.add(status);

			return (List<CommerceTermEntry>)QueryUtil.list(
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
	 * Returns the commerce term entries before and after the current commerce term entry in the ordered set of commerce term entries that the user has permission to view where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param commerceTermEntryId the primary key of the current commerce term entry
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce term entry
	 * @throws NoSuchTermEntryException if a commerce term entry with the primary key could not be found
	 */
	@Override
	public CommerceTermEntry[] filterFindByLtE_S_PrevAndNext(
			long commerceTermEntryId, Date expirationDate, int status,
			OrderByComparator<CommerceTermEntry> orderByComparator)
		throws NoSuchTermEntryException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByLtE_S_PrevAndNext(
				commerceTermEntryId, expirationDate, status, orderByComparator);
		}

		CommerceTermEntry commerceTermEntry = findByPrimaryKey(
			commerceTermEntryId);

		Session session = null;

		try {
			session = openSession();

			CommerceTermEntry[] array = new CommerceTermEntryImpl[3];

			array[0] = filterGetByLtE_S_PrevAndNext(
				session, commerceTermEntry, expirationDate, status,
				orderByComparator, true);

			array[1] = commerceTermEntry;

			array[2] = filterGetByLtE_S_PrevAndNext(
				session, commerceTermEntry, expirationDate, status,
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

	protected CommerceTermEntry filterGetByLtE_S_PrevAndNext(
		Session session, CommerceTermEntry commerceTermEntry,
		Date expirationDate, int status,
		OrderByComparator<CommerceTermEntry> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_COMMERCETERMENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCETERMENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindExpirationDate = false;

		if (expirationDate == null) {
			sb.append(_FINDER_COLUMN_LTE_S_EXPIRATIONDATE_1);
		}
		else {
			bindExpirationDate = true;

			sb.append(_FINDER_COLUMN_LTE_S_EXPIRATIONDATE_2);
		}

		sb.append(_FINDER_COLUMN_LTE_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCETERMENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceTermEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceTermEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceTermEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, CommerceTermEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, CommerceTermEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		if (bindExpirationDate) {
			queryPos.add(new Timestamp(expirationDate.getTime()));
		}

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceTermEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceTermEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce term entries where expirationDate &lt; &#63; and status = &#63; from the database.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 */
	@Override
	public void removeByLtE_S(Date expirationDate, int status) {
		for (CommerceTermEntry commerceTermEntry :
				findByLtE_S(
					expirationDate, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(commerceTermEntry);
		}
	}

	/**
	 * Returns the number of commerce term entries where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @return the number of matching commerce term entries
	 */
	@Override
	public int countByLtE_S(Date expirationDate, int status) {
		FinderPath finderPath = _finderPathWithPaginationCountByLtE_S;

		Object[] finderArgs = new Object[] {_getTime(expirationDate), status};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_COMMERCETERMENTRY_WHERE);

			boolean bindExpirationDate = false;

			if (expirationDate == null) {
				sb.append(_FINDER_COLUMN_LTE_S_EXPIRATIONDATE_1);
			}
			else {
				bindExpirationDate = true;

				sb.append(_FINDER_COLUMN_LTE_S_EXPIRATIONDATE_2);
			}

			sb.append(_FINDER_COLUMN_LTE_S_STATUS_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindExpirationDate) {
					queryPos.add(new Timestamp(expirationDate.getTime()));
				}

				queryPos.add(status);

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
	 * Returns the number of commerce term entries that the user has permission to view where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @return the number of matching commerce term entries that the user has permission to view
	 */
	@Override
	public int filterCountByLtE_S(Date expirationDate, int status) {
		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByLtE_S(expirationDate, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CommerceTermEntry> commerceTermEntries = findByLtE_S(
				expirationDate, status);

			commerceTermEntries = InlineSQLHelperUtil.filter(
				commerceTermEntries);

			return commerceTermEntries.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_COMMERCETERMENTRY_WHERE);

		boolean bindExpirationDate = false;

		if (expirationDate == null) {
			sb.append(_FINDER_COLUMN_LTE_S_EXPIRATIONDATE_1);
		}
		else {
			bindExpirationDate = true;

			sb.append(_FINDER_COLUMN_LTE_S_EXPIRATIONDATE_2);
		}

		sb.append(_FINDER_COLUMN_LTE_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceTermEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindExpirationDate) {
				queryPos.add(new Timestamp(expirationDate.getTime()));
			}

			queryPos.add(status);

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

	private static final String _FINDER_COLUMN_LTE_S_EXPIRATIONDATE_1 =
		"commerceTermEntry.expirationDate IS NULL AND ";

	private static final String _FINDER_COLUMN_LTE_S_EXPIRATIONDATE_2 =
		"commerceTermEntry.expirationDate < ? AND ";

	private static final String _FINDER_COLUMN_LTE_S_STATUS_2 =
		"commerceTermEntry.status = ?";

	private FinderPath _finderPathWithPaginationFindByC_A_LikeType;
	private FinderPath _finderPathWithPaginationCountByC_A_LikeType;

	/**
	 * Returns all the commerce term entries where companyId = &#63; and active = &#63; and type LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param type the type
	 * @return the matching commerce term entries
	 */
	@Override
	public List<CommerceTermEntry> findByC_A_LikeType(
		long companyId, boolean active, String type) {

		return findByC_A_LikeType(
			companyId, active, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the commerce term entries where companyId = &#63; and active = &#63; and type LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param type the type
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @return the range of matching commerce term entries
	 */
	@Override
	public List<CommerceTermEntry> findByC_A_LikeType(
		long companyId, boolean active, String type, int start, int end) {

		return findByC_A_LikeType(companyId, active, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce term entries where companyId = &#63; and active = &#63; and type LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param type the type
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce term entries
	 */
	@Override
	public List<CommerceTermEntry> findByC_A_LikeType(
		long companyId, boolean active, String type, int start, int end,
		OrderByComparator<CommerceTermEntry> orderByComparator) {

		return findByC_A_LikeType(
			companyId, active, type, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce term entries where companyId = &#63; and active = &#63; and type LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param type the type
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce term entries
	 */
	@Override
	public List<CommerceTermEntry> findByC_A_LikeType(
		long companyId, boolean active, String type, int start, int end,
		OrderByComparator<CommerceTermEntry> orderByComparator,
		boolean useFinderCache) {

		type = Objects.toString(type, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		finderPath = _finderPathWithPaginationFindByC_A_LikeType;
		finderArgs = new Object[] {
			companyId, active, type, start, end, orderByComparator
		};

		List<CommerceTermEntry> list = null;

		if (useFinderCache) {
			list = (List<CommerceTermEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (CommerceTermEntry commerceTermEntry : list) {
					if ((companyId != commerceTermEntry.getCompanyId()) ||
						(active != commerceTermEntry.isActive()) ||
						!StringUtil.wildcardMatches(
							commerceTermEntry.getType(), type, '_', '%', '\\',
							true)) {

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

			sb.append(_SQL_SELECT_COMMERCETERMENTRY_WHERE);

			sb.append(_FINDER_COLUMN_C_A_LIKETYPE_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_A_LIKETYPE_ACTIVE_2);

			boolean bindType = false;

			if (type.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_A_LIKETYPE_TYPE_3);
			}
			else {
				bindType = true;

				sb.append(_FINDER_COLUMN_C_A_LIKETYPE_TYPE_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(CommerceTermEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(active);

				if (bindType) {
					queryPos.add(type);
				}

				list = (List<CommerceTermEntry>)QueryUtil.list(
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
	 * Returns the first commerce term entry in the ordered set where companyId = &#63; and active = &#63; and type LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce term entry
	 * @throws NoSuchTermEntryException if a matching commerce term entry could not be found
	 */
	@Override
	public CommerceTermEntry findByC_A_LikeType_First(
			long companyId, boolean active, String type,
			OrderByComparator<CommerceTermEntry> orderByComparator)
		throws NoSuchTermEntryException {

		CommerceTermEntry commerceTermEntry = fetchByC_A_LikeType_First(
			companyId, active, type, orderByComparator);

		if (commerceTermEntry != null) {
			return commerceTermEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", active=");
		sb.append(active);

		sb.append(", typeLIKE");
		sb.append(type);

		sb.append("}");

		throw new NoSuchTermEntryException(sb.toString());
	}

	/**
	 * Returns the first commerce term entry in the ordered set where companyId = &#63; and active = &#63; and type LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce term entry, or <code>null</code> if a matching commerce term entry could not be found
	 */
	@Override
	public CommerceTermEntry fetchByC_A_LikeType_First(
		long companyId, boolean active, String type,
		OrderByComparator<CommerceTermEntry> orderByComparator) {

		List<CommerceTermEntry> list = findByC_A_LikeType(
			companyId, active, type, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last commerce term entry in the ordered set where companyId = &#63; and active = &#63; and type LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce term entry
	 * @throws NoSuchTermEntryException if a matching commerce term entry could not be found
	 */
	@Override
	public CommerceTermEntry findByC_A_LikeType_Last(
			long companyId, boolean active, String type,
			OrderByComparator<CommerceTermEntry> orderByComparator)
		throws NoSuchTermEntryException {

		CommerceTermEntry commerceTermEntry = fetchByC_A_LikeType_Last(
			companyId, active, type, orderByComparator);

		if (commerceTermEntry != null) {
			return commerceTermEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", active=");
		sb.append(active);

		sb.append(", typeLIKE");
		sb.append(type);

		sb.append("}");

		throw new NoSuchTermEntryException(sb.toString());
	}

	/**
	 * Returns the last commerce term entry in the ordered set where companyId = &#63; and active = &#63; and type LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce term entry, or <code>null</code> if a matching commerce term entry could not be found
	 */
	@Override
	public CommerceTermEntry fetchByC_A_LikeType_Last(
		long companyId, boolean active, String type,
		OrderByComparator<CommerceTermEntry> orderByComparator) {

		int count = countByC_A_LikeType(companyId, active, type);

		if (count == 0) {
			return null;
		}

		List<CommerceTermEntry> list = findByC_A_LikeType(
			companyId, active, type, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the commerce term entries before and after the current commerce term entry in the ordered set where companyId = &#63; and active = &#63; and type LIKE &#63;.
	 *
	 * @param commerceTermEntryId the primary key of the current commerce term entry
	 * @param companyId the company ID
	 * @param active the active
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce term entry
	 * @throws NoSuchTermEntryException if a commerce term entry with the primary key could not be found
	 */
	@Override
	public CommerceTermEntry[] findByC_A_LikeType_PrevAndNext(
			long commerceTermEntryId, long companyId, boolean active,
			String type, OrderByComparator<CommerceTermEntry> orderByComparator)
		throws NoSuchTermEntryException {

		type = Objects.toString(type, "");

		CommerceTermEntry commerceTermEntry = findByPrimaryKey(
			commerceTermEntryId);

		Session session = null;

		try {
			session = openSession();

			CommerceTermEntry[] array = new CommerceTermEntryImpl[3];

			array[0] = getByC_A_LikeType_PrevAndNext(
				session, commerceTermEntry, companyId, active, type,
				orderByComparator, true);

			array[1] = commerceTermEntry;

			array[2] = getByC_A_LikeType_PrevAndNext(
				session, commerceTermEntry, companyId, active, type,
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

	protected CommerceTermEntry getByC_A_LikeType_PrevAndNext(
		Session session, CommerceTermEntry commerceTermEntry, long companyId,
		boolean active, String type,
		OrderByComparator<CommerceTermEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_COMMERCETERMENTRY_WHERE);

		sb.append(_FINDER_COLUMN_C_A_LIKETYPE_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_A_LIKETYPE_ACTIVE_2);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_A_LIKETYPE_TYPE_3);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_C_A_LIKETYPE_TYPE_2);
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
			sb.append(CommerceTermEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(active);

		if (bindType) {
			queryPos.add(type);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceTermEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceTermEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the commerce term entries that the user has permission to view where companyId = &#63; and active = &#63; and type LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param type the type
	 * @return the matching commerce term entries that the user has permission to view
	 */
	@Override
	public List<CommerceTermEntry> filterFindByC_A_LikeType(
		long companyId, boolean active, String type) {

		return filterFindByC_A_LikeType(
			companyId, active, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the commerce term entries that the user has permission to view where companyId = &#63; and active = &#63; and type LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param type the type
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @return the range of matching commerce term entries that the user has permission to view
	 */
	@Override
	public List<CommerceTermEntry> filterFindByC_A_LikeType(
		long companyId, boolean active, String type, int start, int end) {

		return filterFindByC_A_LikeType(
			companyId, active, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce term entries that the user has permissions to view where companyId = &#63; and active = &#63; and type LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param type the type
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce term entries that the user has permission to view
	 */
	@Override
	public List<CommerceTermEntry> filterFindByC_A_LikeType(
		long companyId, boolean active, String type, int start, int end,
		OrderByComparator<CommerceTermEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_A_LikeType(
				companyId, active, type, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByC_A_LikeType(
					companyId, active, type, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator));
		}

		type = Objects.toString(type, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_COMMERCETERMENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCETERMENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_A_LIKETYPE_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_A_LIKETYPE_ACTIVE_2_SQL);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_A_LIKETYPE_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_C_A_LIKETYPE_TYPE_2_SQL);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCETERMENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceTermEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceTermEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceTermEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, CommerceTermEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, CommerceTermEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			queryPos.add(active);

			if (bindType) {
				queryPos.add(type);
			}

			return (List<CommerceTermEntry>)QueryUtil.list(
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
	 * Returns the commerce term entries before and after the current commerce term entry in the ordered set of commerce term entries that the user has permission to view where companyId = &#63; and active = &#63; and type LIKE &#63;.
	 *
	 * @param commerceTermEntryId the primary key of the current commerce term entry
	 * @param companyId the company ID
	 * @param active the active
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce term entry
	 * @throws NoSuchTermEntryException if a commerce term entry with the primary key could not be found
	 */
	@Override
	public CommerceTermEntry[] filterFindByC_A_LikeType_PrevAndNext(
			long commerceTermEntryId, long companyId, boolean active,
			String type, OrderByComparator<CommerceTermEntry> orderByComparator)
		throws NoSuchTermEntryException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_A_LikeType_PrevAndNext(
				commerceTermEntryId, companyId, active, type,
				orderByComparator);
		}

		type = Objects.toString(type, "");

		CommerceTermEntry commerceTermEntry = findByPrimaryKey(
			commerceTermEntryId);

		Session session = null;

		try {
			session = openSession();

			CommerceTermEntry[] array = new CommerceTermEntryImpl[3];

			array[0] = filterGetByC_A_LikeType_PrevAndNext(
				session, commerceTermEntry, companyId, active, type,
				orderByComparator, true);

			array[1] = commerceTermEntry;

			array[2] = filterGetByC_A_LikeType_PrevAndNext(
				session, commerceTermEntry, companyId, active, type,
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

	protected CommerceTermEntry filterGetByC_A_LikeType_PrevAndNext(
		Session session, CommerceTermEntry commerceTermEntry, long companyId,
		boolean active, String type,
		OrderByComparator<CommerceTermEntry> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_COMMERCETERMENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCETERMENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_A_LIKETYPE_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_A_LIKETYPE_ACTIVE_2_SQL);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_A_LIKETYPE_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_C_A_LIKETYPE_TYPE_2_SQL);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_COMMERCETERMENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					CommerceTermEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CommerceTermEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceTermEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, CommerceTermEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, CommerceTermEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(companyId);

		queryPos.add(active);

		if (bindType) {
			queryPos.add(type);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						commerceTermEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CommerceTermEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the commerce term entries where companyId = &#63; and active = &#63; and type LIKE &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param type the type
	 */
	@Override
	public void removeByC_A_LikeType(
		long companyId, boolean active, String type) {

		for (CommerceTermEntry commerceTermEntry :
				findByC_A_LikeType(
					companyId, active, type, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(commerceTermEntry);
		}
	}

	/**
	 * Returns the number of commerce term entries where companyId = &#63; and active = &#63; and type LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param type the type
	 * @return the number of matching commerce term entries
	 */
	@Override
	public int countByC_A_LikeType(
		long companyId, boolean active, String type) {

		type = Objects.toString(type, "");

		FinderPath finderPath = _finderPathWithPaginationCountByC_A_LikeType;

		Object[] finderArgs = new Object[] {companyId, active, type};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_COUNT_COMMERCETERMENTRY_WHERE);

			sb.append(_FINDER_COLUMN_C_A_LIKETYPE_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_A_LIKETYPE_ACTIVE_2);

			boolean bindType = false;

			if (type.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_A_LIKETYPE_TYPE_3);
			}
			else {
				bindType = true;

				sb.append(_FINDER_COLUMN_C_A_LIKETYPE_TYPE_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(active);

				if (bindType) {
					queryPos.add(type);
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
	 * Returns the number of commerce term entries that the user has permission to view where companyId = &#63; and active = &#63; and type LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param type the type
	 * @return the number of matching commerce term entries that the user has permission to view
	 */
	@Override
	public int filterCountByC_A_LikeType(
		long companyId, boolean active, String type) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByC_A_LikeType(companyId, active, type);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<CommerceTermEntry> commerceTermEntries = findByC_A_LikeType(
				companyId, active, type);

			commerceTermEntries = InlineSQLHelperUtil.filter(
				commerceTermEntries);

			return commerceTermEntries.size();
		}

		type = Objects.toString(type, "");

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_COMMERCETERMENTRY_WHERE);

		sb.append(_FINDER_COLUMN_C_A_LIKETYPE_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_A_LIKETYPE_ACTIVE_2_SQL);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_A_LIKETYPE_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_C_A_LIKETYPE_TYPE_2_SQL);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), CommerceTermEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			queryPos.add(active);

			if (bindType) {
				queryPos.add(type);
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

	private static final String _FINDER_COLUMN_C_A_LIKETYPE_COMPANYID_2 =
		"commerceTermEntry.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_A_LIKETYPE_ACTIVE_2 =
		"commerceTermEntry.active = ? AND ";

	private static final String _FINDER_COLUMN_C_A_LIKETYPE_ACTIVE_2_SQL =
		"commerceTermEntry.active_ = ? AND ";

	private static final String _FINDER_COLUMN_C_A_LIKETYPE_TYPE_2 =
		"commerceTermEntry.type LIKE ?";

	private static final String _FINDER_COLUMN_C_A_LIKETYPE_TYPE_3 =
		"(commerceTermEntry.type IS NULL OR commerceTermEntry.type LIKE '')";

	private static final String _FINDER_COLUMN_C_A_LIKETYPE_TYPE_2_SQL =
		"commerceTermEntry.type_ LIKE ?";

	private static final String _FINDER_COLUMN_C_A_LIKETYPE_TYPE_3_SQL =
		"(commerceTermEntry.type_ IS NULL OR commerceTermEntry.type_ LIKE '')";

	private FinderPath _finderPathFetchByC_P_T;

	/**
	 * Returns the commerce term entry where companyId = &#63; and priority = &#63; and type = &#63; or throws a <code>NoSuchTermEntryException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param priority the priority
	 * @param type the type
	 * @return the matching commerce term entry
	 * @throws NoSuchTermEntryException if a matching commerce term entry could not be found
	 */
	@Override
	public CommerceTermEntry findByC_P_T(
			long companyId, double priority, String type)
		throws NoSuchTermEntryException {

		CommerceTermEntry commerceTermEntry = fetchByC_P_T(
			companyId, priority, type);

		if (commerceTermEntry == null) {
			StringBundler sb = new StringBundler(8);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("companyId=");
			sb.append(companyId);

			sb.append(", priority=");
			sb.append(priority);

			sb.append(", type=");
			sb.append(type);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchTermEntryException(sb.toString());
		}

		return commerceTermEntry;
	}

	/**
	 * Returns the commerce term entry where companyId = &#63; and priority = &#63; and type = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param priority the priority
	 * @param type the type
	 * @return the matching commerce term entry, or <code>null</code> if a matching commerce term entry could not be found
	 */
	@Override
	public CommerceTermEntry fetchByC_P_T(
		long companyId, double priority, String type) {

		return fetchByC_P_T(companyId, priority, type, true);
	}

	/**
	 * Returns the commerce term entry where companyId = &#63; and priority = &#63; and type = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param priority the priority
	 * @param type the type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce term entry, or <code>null</code> if a matching commerce term entry could not be found
	 */
	@Override
	public CommerceTermEntry fetchByC_P_T(
		long companyId, double priority, String type, boolean useFinderCache) {

		type = Objects.toString(type, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {companyId, priority, type};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByC_P_T, finderArgs, this);
		}

		if (result instanceof CommerceTermEntry) {
			CommerceTermEntry commerceTermEntry = (CommerceTermEntry)result;

			if ((companyId != commerceTermEntry.getCompanyId()) ||
				(priority != commerceTermEntry.getPriority()) ||
				!Objects.equals(type, commerceTermEntry.getType())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(5);

			sb.append(_SQL_SELECT_COMMERCETERMENTRY_WHERE);

			sb.append(_FINDER_COLUMN_C_P_T_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_P_T_PRIORITY_2);

			boolean bindType = false;

			if (type.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_P_T_TYPE_3);
			}
			else {
				bindType = true;

				sb.append(_FINDER_COLUMN_C_P_T_TYPE_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(priority);

				if (bindType) {
					queryPos.add(type);
				}

				List<CommerceTermEntry> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByC_P_T, finderArgs, list);
					}
				}
				else {
					CommerceTermEntry commerceTermEntry = list.get(0);

					result = commerceTermEntry;

					cacheResult(commerceTermEntry);
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
			return (CommerceTermEntry)result;
		}
	}

	/**
	 * Removes the commerce term entry where companyId = &#63; and priority = &#63; and type = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param priority the priority
	 * @param type the type
	 * @return the commerce term entry that was removed
	 */
	@Override
	public CommerceTermEntry removeByC_P_T(
			long companyId, double priority, String type)
		throws NoSuchTermEntryException {

		CommerceTermEntry commerceTermEntry = findByC_P_T(
			companyId, priority, type);

		return remove(commerceTermEntry);
	}

	/**
	 * Returns the number of commerce term entries where companyId = &#63; and priority = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param priority the priority
	 * @param type the type
	 * @return the number of matching commerce term entries
	 */
	@Override
	public int countByC_P_T(long companyId, double priority, String type) {
		CommerceTermEntry commerceTermEntry = fetchByC_P_T(
			companyId, priority, type);

		if (commerceTermEntry == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_C_P_T_COMPANYID_2 =
		"commerceTermEntry.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_P_T_PRIORITY_2 =
		"commerceTermEntry.priority = ? AND ";

	private static final String _FINDER_COLUMN_C_P_T_TYPE_2 =
		"commerceTermEntry.type = ?";

	private static final String _FINDER_COLUMN_C_P_T_TYPE_3 =
		"(commerceTermEntry.type IS NULL OR commerceTermEntry.type = '')";

	private FinderPath _finderPathFetchByERC_C;

	/**
	 * Returns the commerce term entry where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchTermEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce term entry
	 * @throws NoSuchTermEntryException if a matching commerce term entry could not be found
	 */
	@Override
	public CommerceTermEntry findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchTermEntryException {

		CommerceTermEntry commerceTermEntry = fetchByERC_C(
			externalReferenceCode, companyId);

		if (commerceTermEntry == null) {
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

			throw new NoSuchTermEntryException(sb.toString());
		}

		return commerceTermEntry;
	}

	/**
	 * Returns the commerce term entry where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce term entry, or <code>null</code> if a matching commerce term entry could not be found
	 */
	@Override
	public CommerceTermEntry fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return fetchByERC_C(externalReferenceCode, companyId, true);
	}

	/**
	 * Returns the commerce term entry where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce term entry, or <code>null</code> if a matching commerce term entry could not be found
	 */
	@Override
	public CommerceTermEntry fetchByERC_C(
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

		if (result instanceof CommerceTermEntry) {
			CommerceTermEntry commerceTermEntry = (CommerceTermEntry)result;

			if (!Objects.equals(
					externalReferenceCode,
					commerceTermEntry.getExternalReferenceCode()) ||
				(companyId != commerceTermEntry.getCompanyId())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_COMMERCETERMENTRY_WHERE);

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

				List<CommerceTermEntry> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByERC_C, finderArgs, list);
					}
				}
				else {
					CommerceTermEntry commerceTermEntry = list.get(0);

					result = commerceTermEntry;

					cacheResult(commerceTermEntry);
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
			return (CommerceTermEntry)result;
		}
	}

	/**
	 * Removes the commerce term entry where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the commerce term entry that was removed
	 */
	@Override
	public CommerceTermEntry removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchTermEntryException {

		CommerceTermEntry commerceTermEntry = findByERC_C(
			externalReferenceCode, companyId);

		return remove(commerceTermEntry);
	}

	/**
	 * Returns the number of commerce term entries where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching commerce term entries
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		CommerceTermEntry commerceTermEntry = fetchByERC_C(
			externalReferenceCode, companyId);

		if (commerceTermEntry == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ERC_C_EXTERNALREFERENCECODE_2 =
		"commerceTermEntry.externalReferenceCode = ? AND ";

	private static final String _FINDER_COLUMN_ERC_C_EXTERNALREFERENCECODE_3 =
		"(commerceTermEntry.externalReferenceCode IS NULL OR commerceTermEntry.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_C_COMPANYID_2 =
		"commerceTermEntry.companyId = ?";

	public CommerceTermEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("active", "active_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceTermEntry.class);

		setModelImplClass(CommerceTermEntryImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceTermEntryTable.INSTANCE);
	}

	/**
	 * Caches the commerce term entry in the entity cache if it is enabled.
	 *
	 * @param commerceTermEntry the commerce term entry
	 */
	@Override
	public void cacheResult(CommerceTermEntry commerceTermEntry) {
		entityCache.putResult(
			CommerceTermEntryImpl.class, commerceTermEntry.getPrimaryKey(),
			commerceTermEntry);

		finderCache.putResult(
			_finderPathFetchByC_N,
			new Object[] {
				commerceTermEntry.getCompanyId(), commerceTermEntry.getName()
			},
			commerceTermEntry);

		finderCache.putResult(
			_finderPathFetchByC_P_T,
			new Object[] {
				commerceTermEntry.getCompanyId(),
				commerceTermEntry.getPriority(), commerceTermEntry.getType()
			},
			commerceTermEntry);

		finderCache.putResult(
			_finderPathFetchByERC_C,
			new Object[] {
				commerceTermEntry.getExternalReferenceCode(),
				commerceTermEntry.getCompanyId()
			},
			commerceTermEntry);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the commerce term entries in the entity cache if it is enabled.
	 *
	 * @param commerceTermEntries the commerce term entries
	 */
	@Override
	public void cacheResult(List<CommerceTermEntry> commerceTermEntries) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (commerceTermEntries.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (CommerceTermEntry commerceTermEntry : commerceTermEntries) {
			if (entityCache.getResult(
					CommerceTermEntryImpl.class,
					commerceTermEntry.getPrimaryKey()) == null) {

				cacheResult(commerceTermEntry);
			}
		}
	}

	/**
	 * Clears the cache for all commerce term entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(CommerceTermEntryImpl.class);

		finderCache.clearCache(CommerceTermEntryImpl.class);
	}

	/**
	 * Clears the cache for the commerce term entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(CommerceTermEntry commerceTermEntry) {
		entityCache.removeResult(
			CommerceTermEntryImpl.class, commerceTermEntry);
	}

	@Override
	public void clearCache(List<CommerceTermEntry> commerceTermEntries) {
		for (CommerceTermEntry commerceTermEntry : commerceTermEntries) {
			entityCache.removeResult(
				CommerceTermEntryImpl.class, commerceTermEntry);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(CommerceTermEntryImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(CommerceTermEntryImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		CommerceTermEntryModelImpl commerceTermEntryModelImpl) {

		Object[] args = new Object[] {
			commerceTermEntryModelImpl.getCompanyId(),
			commerceTermEntryModelImpl.getName()
		};

		finderCache.putResult(
			_finderPathFetchByC_N, args, commerceTermEntryModelImpl);

		args = new Object[] {
			commerceTermEntryModelImpl.getCompanyId(),
			commerceTermEntryModelImpl.getPriority(),
			commerceTermEntryModelImpl.getType()
		};

		finderCache.putResult(
			_finderPathFetchByC_P_T, args, commerceTermEntryModelImpl);

		args = new Object[] {
			commerceTermEntryModelImpl.getExternalReferenceCode(),
			commerceTermEntryModelImpl.getCompanyId()
		};

		finderCache.putResult(
			_finderPathFetchByERC_C, args, commerceTermEntryModelImpl);
	}

	/**
	 * Creates a new commerce term entry with the primary key. Does not add the commerce term entry to the database.
	 *
	 * @param commerceTermEntryId the primary key for the new commerce term entry
	 * @return the new commerce term entry
	 */
	@Override
	public CommerceTermEntry create(long commerceTermEntryId) {
		CommerceTermEntry commerceTermEntry = new CommerceTermEntryImpl();

		commerceTermEntry.setNew(true);
		commerceTermEntry.setPrimaryKey(commerceTermEntryId);

		String uuid = PortalUUIDUtil.generate();

		commerceTermEntry.setUuid(uuid);

		commerceTermEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return commerceTermEntry;
	}

	/**
	 * Removes the commerce term entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceTermEntryId the primary key of the commerce term entry
	 * @return the commerce term entry that was removed
	 * @throws NoSuchTermEntryException if a commerce term entry with the primary key could not be found
	 */
	@Override
	public CommerceTermEntry remove(long commerceTermEntryId)
		throws NoSuchTermEntryException {

		return remove((Serializable)commerceTermEntryId);
	}

	/**
	 * Removes the commerce term entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the commerce term entry
	 * @return the commerce term entry that was removed
	 * @throws NoSuchTermEntryException if a commerce term entry with the primary key could not be found
	 */
	@Override
	public CommerceTermEntry remove(Serializable primaryKey)
		throws NoSuchTermEntryException {

		Session session = null;

		try {
			session = openSession();

			CommerceTermEntry commerceTermEntry =
				(CommerceTermEntry)session.get(
					CommerceTermEntryImpl.class, primaryKey);

			if (commerceTermEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchTermEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(commerceTermEntry);
		}
		catch (NoSuchTermEntryException noSuchEntityException) {
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
	protected CommerceTermEntry removeImpl(
		CommerceTermEntry commerceTermEntry) {

		cTermEntryLocalizationPersistence.removeByCommerceTermEntryId(
			commerceTermEntry.getCommerceTermEntryId());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceTermEntry)) {
				commerceTermEntry = (CommerceTermEntry)session.get(
					CommerceTermEntryImpl.class,
					commerceTermEntry.getPrimaryKeyObj());
			}

			if (commerceTermEntry != null) {
				session.delete(commerceTermEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceTermEntry != null) {
			clearCache(commerceTermEntry);
		}

		return commerceTermEntry;
	}

	@Override
	public CommerceTermEntry updateImpl(CommerceTermEntry commerceTermEntry) {
		boolean isNew = commerceTermEntry.isNew();

		if (!(commerceTermEntry instanceof CommerceTermEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(commerceTermEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceTermEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceTermEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceTermEntry implementation " +
					commerceTermEntry.getClass());
		}

		CommerceTermEntryModelImpl commerceTermEntryModelImpl =
			(CommerceTermEntryModelImpl)commerceTermEntry;

		if (Validator.isNull(commerceTermEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			commerceTermEntry.setUuid(uuid);
		}

		if (Validator.isNull(commerceTermEntry.getExternalReferenceCode())) {
			commerceTermEntry.setExternalReferenceCode(
				commerceTermEntry.getUuid());
		}
		else {
			if (!Objects.equals(
					commerceTermEntryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					commerceTermEntry.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = commerceTermEntry.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = commerceTermEntry.getPrimaryKey();
					}

					try {
						commerceTermEntry.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								CommerceTermEntry.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								commerceTermEntry.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			CommerceTermEntry ercCommerceTermEntry = fetchByERC_C(
				commerceTermEntry.getExternalReferenceCode(),
				commerceTermEntry.getCompanyId());

			if (isNew) {
				if (ercCommerceTermEntry != null) {
					throw new DuplicateCommerceTermEntryExternalReferenceCodeException(
						"Duplicate commerce term entry with external reference code " +
							commerceTermEntry.getExternalReferenceCode() +
								" and company " +
									commerceTermEntry.getCompanyId());
				}
			}
			else {
				if ((ercCommerceTermEntry != null) &&
					(commerceTermEntry.getCommerceTermEntryId() !=
						ercCommerceTermEntry.getCommerceTermEntryId())) {

					throw new DuplicateCommerceTermEntryExternalReferenceCodeException(
						"Duplicate commerce term entry with external reference code " +
							commerceTermEntry.getExternalReferenceCode() +
								" and company " +
									commerceTermEntry.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceTermEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceTermEntry.setCreateDate(date);
			}
			else {
				commerceTermEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceTermEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceTermEntry.setModifiedDate(date);
			}
			else {
				commerceTermEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceTermEntry);
			}
			else {
				commerceTermEntry = (CommerceTermEntry)session.merge(
					commerceTermEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			CommerceTermEntryImpl.class, commerceTermEntryModelImpl, false,
			true);

		cacheUniqueFindersCache(commerceTermEntryModelImpl);

		if (isNew) {
			commerceTermEntry.setNew(false);
		}

		commerceTermEntry.resetOriginalValues();

		return commerceTermEntry;
	}

	/**
	 * Returns the commerce term entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the commerce term entry
	 * @return the commerce term entry
	 * @throws NoSuchTermEntryException if a commerce term entry with the primary key could not be found
	 */
	@Override
	public CommerceTermEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchTermEntryException {

		CommerceTermEntry commerceTermEntry = fetchByPrimaryKey(primaryKey);

		if (commerceTermEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchTermEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return commerceTermEntry;
	}

	/**
	 * Returns the commerce term entry with the primary key or throws a <code>NoSuchTermEntryException</code> if it could not be found.
	 *
	 * @param commerceTermEntryId the primary key of the commerce term entry
	 * @return the commerce term entry
	 * @throws NoSuchTermEntryException if a commerce term entry with the primary key could not be found
	 */
	@Override
	public CommerceTermEntry findByPrimaryKey(long commerceTermEntryId)
		throws NoSuchTermEntryException {

		return findByPrimaryKey((Serializable)commerceTermEntryId);
	}

	/**
	 * Returns the commerce term entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceTermEntryId the primary key of the commerce term entry
	 * @return the commerce term entry, or <code>null</code> if a commerce term entry with the primary key could not be found
	 */
	@Override
	public CommerceTermEntry fetchByPrimaryKey(long commerceTermEntryId) {
		return fetchByPrimaryKey((Serializable)commerceTermEntryId);
	}

	/**
	 * Returns all the commerce term entries.
	 *
	 * @return the commerce term entries
	 */
	@Override
	public List<CommerceTermEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce term entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @return the range of commerce term entries
	 */
	@Override
	public List<CommerceTermEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce term entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of commerce term entries
	 */
	@Override
	public List<CommerceTermEntry> findAll(
		int start, int end,
		OrderByComparator<CommerceTermEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce term entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of commerce term entries
	 */
	@Override
	public List<CommerceTermEntry> findAll(
		int start, int end,
		OrderByComparator<CommerceTermEntry> orderByComparator,
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

		List<CommerceTermEntry> list = null;

		if (useFinderCache) {
			list = (List<CommerceTermEntry>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_COMMERCETERMENTRY);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_COMMERCETERMENTRY;

				sql = sql.concat(CommerceTermEntryModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<CommerceTermEntry>)QueryUtil.list(
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
	 * Removes all the commerce term entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (CommerceTermEntry commerceTermEntry : findAll()) {
			remove(commerceTermEntry);
		}
	}

	/**
	 * Returns the number of commerce term entries.
	 *
	 * @return the number of commerce term entries
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_COMMERCETERMENTRY);

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
		return "commerceTermEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCETERMENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceTermEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce term entry persistence.
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

		_finderPathWithPaginationFindByUuid = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
			new String[] {
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"uuid_"}, true);

		_finderPathWithoutPaginationFindByUuid = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
			new String[] {String.class.getName()}, new String[] {"uuid_"},
			true);

		_finderPathCountByUuid = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
			new String[] {String.class.getName()}, new String[] {"uuid_"},
			false);

		_finderPathWithPaginationFindByUuid_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid_C",
			new String[] {
				String.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"uuid_", "companyId"}, true);

		_finderPathWithoutPaginationFindByUuid_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid_C",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"uuid_", "companyId"}, true);

		_finderPathCountByUuid_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid_C",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"uuid_", "companyId"}, false);

		_finderPathWithPaginationFindByC_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_A",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "active_"}, true);

		_finderPathWithoutPaginationFindByC_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_A",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"companyId", "active_"}, true);

		_finderPathCountByC_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_A",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"companyId", "active_"}, false);

		_finderPathFetchByC_N = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByC_N",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "name"}, true);

		_finderPathWithPaginationFindByC_LikeType = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_LikeType",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "type_"}, true);

		_finderPathWithPaginationCountByC_LikeType = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_LikeType",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "type_"}, false);

		_finderPathWithPaginationFindByLtD_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByLtD_S",
			new String[] {
				Date.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"displayDate", "status"}, true);

		_finderPathWithPaginationCountByLtD_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByLtD_S",
			new String[] {Date.class.getName(), Integer.class.getName()},
			new String[] {"displayDate", "status"}, false);

		_finderPathWithPaginationFindByLtE_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByLtE_S",
			new String[] {
				Date.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"expirationDate", "status"}, true);

		_finderPathWithPaginationCountByLtE_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByLtE_S",
			new String[] {Date.class.getName(), Integer.class.getName()},
			new String[] {"expirationDate", "status"}, false);

		_finderPathWithPaginationFindByC_A_LikeType = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_A_LikeType",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"companyId", "active_", "type_"}, true);

		_finderPathWithPaginationCountByC_A_LikeType = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_A_LikeType",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName()
			},
			new String[] {"companyId", "active_", "type_"}, false);

		_finderPathFetchByC_P_T = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByC_P_T",
			new String[] {
				Long.class.getName(), Double.class.getName(),
				String.class.getName()
			},
			new String[] {"companyId", "priority", "type_"}, true);

		_finderPathFetchByERC_C = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"externalReferenceCode", "companyId"}, true);

		CommerceTermEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceTermEntryUtil.setPersistence(null);

		entityCache.removeCache(CommerceTermEntryImpl.class.getName());
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

	@Reference
	protected CTermEntryLocalizationPersistence
		cTermEntryLocalizationPersistence;

	private static Long _getTime(Date date) {
		if (date == null) {
			return null;
		}

		return date.getTime();
	}

	private static final String _SQL_SELECT_COMMERCETERMENTRY =
		"SELECT commerceTermEntry FROM CommerceTermEntry commerceTermEntry";

	private static final String _SQL_SELECT_COMMERCETERMENTRY_WHERE =
		"SELECT commerceTermEntry FROM CommerceTermEntry commerceTermEntry WHERE ";

	private static final String _SQL_COUNT_COMMERCETERMENTRY =
		"SELECT COUNT(commerceTermEntry) FROM CommerceTermEntry commerceTermEntry";

	private static final String _SQL_COUNT_COMMERCETERMENTRY_WHERE =
		"SELECT COUNT(commerceTermEntry) FROM CommerceTermEntry commerceTermEntry WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"commerceTermEntry.commerceTermEntryId";

	private static final String _FILTER_SQL_SELECT_COMMERCETERMENTRY_WHERE =
		"SELECT DISTINCT {commerceTermEntry.*} FROM CommerceTermEntry commerceTermEntry WHERE ";

	private static final String
		_FILTER_SQL_SELECT_COMMERCETERMENTRY_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {CommerceTermEntry.*} FROM (SELECT DISTINCT commerceTermEntry.commerceTermEntryId FROM CommerceTermEntry commerceTermEntry WHERE ";

	private static final String
		_FILTER_SQL_SELECT_COMMERCETERMENTRY_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN CommerceTermEntry ON TEMP_TABLE.commerceTermEntryId = CommerceTermEntry.commerceTermEntryId";

	private static final String _FILTER_SQL_COUNT_COMMERCETERMENTRY_WHERE =
		"SELECT COUNT(DISTINCT commerceTermEntry.commerceTermEntryId) AS COUNT_VALUE FROM CommerceTermEntry commerceTermEntry WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "commerceTermEntry";

	private static final String _FILTER_ENTITY_TABLE = "CommerceTermEntry";

	private static final String _ORDER_BY_ENTITY_ALIAS = "commerceTermEntry.";

	private static final String _ORDER_BY_ENTITY_TABLE = "CommerceTermEntry.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No CommerceTermEntry exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommerceTermEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceTermEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "active", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}