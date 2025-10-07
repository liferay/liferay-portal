/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.redirect.service.persistence.impl;

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
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.redirect.exception.NoSuchEntryException;
import com.liferay.redirect.model.RedirectEntry;
import com.liferay.redirect.model.RedirectEntryTable;
import com.liferay.redirect.model.impl.RedirectEntryImpl;
import com.liferay.redirect.model.impl.RedirectEntryModelImpl;
import com.liferay.redirect.service.persistence.RedirectEntryPersistence;
import com.liferay.redirect.service.persistence.RedirectEntryUtil;
import com.liferay.redirect.service.persistence.impl.constants.RedirectPersistenceConstants;

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
 * The persistence implementation for the redirect entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = RedirectEntryPersistence.class)
public class RedirectEntryPersistenceImpl
	extends BasePersistenceImpl<RedirectEntry>
	implements RedirectEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>RedirectEntryUtil</code> to access the redirect entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		RedirectEntryImpl.class.getName();

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
	 * Returns all the redirect entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching redirect entries
	 */
	@Override
	public List<RedirectEntry> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the redirect entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RedirectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of redirect entries
	 * @param end the upper bound of the range of redirect entries (not inclusive)
	 * @return the range of matching redirect entries
	 */
	@Override
	public List<RedirectEntry> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the redirect entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RedirectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of redirect entries
	 * @param end the upper bound of the range of redirect entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching redirect entries
	 */
	@Override
	public List<RedirectEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<RedirectEntry> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the redirect entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RedirectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of redirect entries
	 * @param end the upper bound of the range of redirect entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching redirect entries
	 */
	@Override
	public List<RedirectEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<RedirectEntry> orderByComparator,
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

		List<RedirectEntry> list = null;

		if (useFinderCache) {
			list = (List<RedirectEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (RedirectEntry redirectEntry : list) {
					if (!uuid.equals(redirectEntry.getUuid())) {
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

			sb.append(_SQL_SELECT_REDIRECTENTRY_WHERE);

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
				sb.append(RedirectEntryModelImpl.ORDER_BY_JPQL);
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

				list = (List<RedirectEntry>)QueryUtil.list(
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
	 * Returns the first redirect entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching redirect entry
	 * @throws NoSuchEntryException if a matching redirect entry could not be found
	 */
	@Override
	public RedirectEntry findByUuid_First(
			String uuid, OrderByComparator<RedirectEntry> orderByComparator)
		throws NoSuchEntryException {

		RedirectEntry redirectEntry = fetchByUuid_First(
			uuid, orderByComparator);

		if (redirectEntry != null) {
			return redirectEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first redirect entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching redirect entry, or <code>null</code> if a matching redirect entry could not be found
	 */
	@Override
	public RedirectEntry fetchByUuid_First(
		String uuid, OrderByComparator<RedirectEntry> orderByComparator) {

		List<RedirectEntry> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last redirect entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching redirect entry
	 * @throws NoSuchEntryException if a matching redirect entry could not be found
	 */
	@Override
	public RedirectEntry findByUuid_Last(
			String uuid, OrderByComparator<RedirectEntry> orderByComparator)
		throws NoSuchEntryException {

		RedirectEntry redirectEntry = fetchByUuid_Last(uuid, orderByComparator);

		if (redirectEntry != null) {
			return redirectEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the last redirect entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching redirect entry, or <code>null</code> if a matching redirect entry could not be found
	 */
	@Override
	public RedirectEntry fetchByUuid_Last(
		String uuid, OrderByComparator<RedirectEntry> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<RedirectEntry> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the redirect entries before and after the current redirect entry in the ordered set where uuid = &#63;.
	 *
	 * @param redirectEntryId the primary key of the current redirect entry
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next redirect entry
	 * @throws NoSuchEntryException if a redirect entry with the primary key could not be found
	 */
	@Override
	public RedirectEntry[] findByUuid_PrevAndNext(
			long redirectEntryId, String uuid,
			OrderByComparator<RedirectEntry> orderByComparator)
		throws NoSuchEntryException {

		uuid = Objects.toString(uuid, "");

		RedirectEntry redirectEntry = findByPrimaryKey(redirectEntryId);

		Session session = null;

		try {
			session = openSession();

			RedirectEntry[] array = new RedirectEntryImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, redirectEntry, uuid, orderByComparator, true);

			array[1] = redirectEntry;

			array[2] = getByUuid_PrevAndNext(
				session, redirectEntry, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected RedirectEntry getByUuid_PrevAndNext(
		Session session, RedirectEntry redirectEntry, String uuid,
		OrderByComparator<RedirectEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_REDIRECTENTRY_WHERE);

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
			sb.append(RedirectEntryModelImpl.ORDER_BY_JPQL);
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
						redirectEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<RedirectEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the redirect entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (RedirectEntry redirectEntry :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(redirectEntry);
		}
	}

	/**
	 * Returns the number of redirect entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching redirect entries
	 */
	@Override
	public int countByUuid(String uuid) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid;

		Object[] finderArgs = new Object[] {uuid};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_REDIRECTENTRY_WHERE);

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

	private static final String _FINDER_COLUMN_UUID_UUID_2 =
		"redirectEntry.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(redirectEntry.uuid IS NULL OR redirectEntry.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;

	/**
	 * Returns the redirect entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching redirect entry
	 * @throws NoSuchEntryException if a matching redirect entry could not be found
	 */
	@Override
	public RedirectEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchEntryException {

		RedirectEntry redirectEntry = fetchByUUID_G(uuid, groupId);

		if (redirectEntry == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("uuid=");
			sb.append(uuid);

			sb.append(", groupId=");
			sb.append(groupId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchEntryException(sb.toString());
		}

		return redirectEntry;
	}

	/**
	 * Returns the redirect entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching redirect entry, or <code>null</code> if a matching redirect entry could not be found
	 */
	@Override
	public RedirectEntry fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the redirect entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching redirect entry, or <code>null</code> if a matching redirect entry could not be found
	 */
	@Override
	public RedirectEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		uuid = Objects.toString(uuid, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {uuid, groupId};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByUUID_G, finderArgs, this);
		}

		if (result instanceof RedirectEntry) {
			RedirectEntry redirectEntry = (RedirectEntry)result;

			if (!Objects.equals(uuid, redirectEntry.getUuid()) ||
				(groupId != redirectEntry.getGroupId())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_REDIRECTENTRY_WHERE);

			boolean bindUuid = false;

			if (uuid.isEmpty()) {
				sb.append(_FINDER_COLUMN_UUID_G_UUID_3);
			}
			else {
				bindUuid = true;

				sb.append(_FINDER_COLUMN_UUID_G_UUID_2);
			}

			sb.append(_FINDER_COLUMN_UUID_G_GROUPID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindUuid) {
					queryPos.add(uuid);
				}

				queryPos.add(groupId);

				List<RedirectEntry> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByUUID_G, finderArgs, list);
					}
				}
				else {
					RedirectEntry redirectEntry = list.get(0);

					result = redirectEntry;

					cacheResult(redirectEntry);
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
			return (RedirectEntry)result;
		}
	}

	/**
	 * Removes the redirect entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the redirect entry that was removed
	 */
	@Override
	public RedirectEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchEntryException {

		RedirectEntry redirectEntry = findByUUID_G(uuid, groupId);

		return remove(redirectEntry);
	}

	/**
	 * Returns the number of redirect entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching redirect entries
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		RedirectEntry redirectEntry = fetchByUUID_G(uuid, groupId);

		if (redirectEntry == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"redirectEntry.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(redirectEntry.uuid IS NULL OR redirectEntry.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"redirectEntry.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the redirect entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching redirect entries
	 */
	@Override
	public List<RedirectEntry> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the redirect entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RedirectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of redirect entries
	 * @param end the upper bound of the range of redirect entries (not inclusive)
	 * @return the range of matching redirect entries
	 */
	@Override
	public List<RedirectEntry> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the redirect entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RedirectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of redirect entries
	 * @param end the upper bound of the range of redirect entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching redirect entries
	 */
	@Override
	public List<RedirectEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<RedirectEntry> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the redirect entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RedirectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of redirect entries
	 * @param end the upper bound of the range of redirect entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching redirect entries
	 */
	@Override
	public List<RedirectEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<RedirectEntry> orderByComparator,
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

		List<RedirectEntry> list = null;

		if (useFinderCache) {
			list = (List<RedirectEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (RedirectEntry redirectEntry : list) {
					if (!uuid.equals(redirectEntry.getUuid()) ||
						(companyId != redirectEntry.getCompanyId())) {

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

			sb.append(_SQL_SELECT_REDIRECTENTRY_WHERE);

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
				sb.append(RedirectEntryModelImpl.ORDER_BY_JPQL);
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

				list = (List<RedirectEntry>)QueryUtil.list(
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
	 * Returns the first redirect entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching redirect entry
	 * @throws NoSuchEntryException if a matching redirect entry could not be found
	 */
	@Override
	public RedirectEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<RedirectEntry> orderByComparator)
		throws NoSuchEntryException {

		RedirectEntry redirectEntry = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (redirectEntry != null) {
			return redirectEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first redirect entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching redirect entry, or <code>null</code> if a matching redirect entry could not be found
	 */
	@Override
	public RedirectEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<RedirectEntry> orderByComparator) {

		List<RedirectEntry> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last redirect entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching redirect entry
	 * @throws NoSuchEntryException if a matching redirect entry could not be found
	 */
	@Override
	public RedirectEntry findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<RedirectEntry> orderByComparator)
		throws NoSuchEntryException {

		RedirectEntry redirectEntry = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (redirectEntry != null) {
			return redirectEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the last redirect entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching redirect entry, or <code>null</code> if a matching redirect entry could not be found
	 */
	@Override
	public RedirectEntry fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<RedirectEntry> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<RedirectEntry> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the redirect entries before and after the current redirect entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param redirectEntryId the primary key of the current redirect entry
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next redirect entry
	 * @throws NoSuchEntryException if a redirect entry with the primary key could not be found
	 */
	@Override
	public RedirectEntry[] findByUuid_C_PrevAndNext(
			long redirectEntryId, String uuid, long companyId,
			OrderByComparator<RedirectEntry> orderByComparator)
		throws NoSuchEntryException {

		uuid = Objects.toString(uuid, "");

		RedirectEntry redirectEntry = findByPrimaryKey(redirectEntryId);

		Session session = null;

		try {
			session = openSession();

			RedirectEntry[] array = new RedirectEntryImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, redirectEntry, uuid, companyId, orderByComparator,
				true);

			array[1] = redirectEntry;

			array[2] = getByUuid_C_PrevAndNext(
				session, redirectEntry, uuid, companyId, orderByComparator,
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

	protected RedirectEntry getByUuid_C_PrevAndNext(
		Session session, RedirectEntry redirectEntry, String uuid,
		long companyId, OrderByComparator<RedirectEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_REDIRECTENTRY_WHERE);

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
			sb.append(RedirectEntryModelImpl.ORDER_BY_JPQL);
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
						redirectEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<RedirectEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the redirect entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (RedirectEntry redirectEntry :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(redirectEntry);
		}
	}

	/**
	 * Returns the number of redirect entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching redirect entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid_C;

		Object[] finderArgs = new Object[] {uuid, companyId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_REDIRECTENTRY_WHERE);

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

	private static final String _FINDER_COLUMN_UUID_C_UUID_2 =
		"redirectEntry.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(redirectEntry.uuid IS NULL OR redirectEntry.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"redirectEntry.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByGroupId;
	private FinderPath _finderPathWithoutPaginationFindByGroupId;
	private FinderPath _finderPathCountByGroupId;

	/**
	 * Returns all the redirect entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching redirect entries
	 */
	@Override
	public List<RedirectEntry> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the redirect entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RedirectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of redirect entries
	 * @param end the upper bound of the range of redirect entries (not inclusive)
	 * @return the range of matching redirect entries
	 */
	@Override
	public List<RedirectEntry> findByGroupId(long groupId, int start, int end) {
		return findByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the redirect entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RedirectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of redirect entries
	 * @param end the upper bound of the range of redirect entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching redirect entries
	 */
	@Override
	public List<RedirectEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<RedirectEntry> orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the redirect entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RedirectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of redirect entries
	 * @param end the upper bound of the range of redirect entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching redirect entries
	 */
	@Override
	public List<RedirectEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<RedirectEntry> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByGroupId;
				finderArgs = new Object[] {groupId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByGroupId;
			finderArgs = new Object[] {groupId, start, end, orderByComparator};
		}

		List<RedirectEntry> list = null;

		if (useFinderCache) {
			list = (List<RedirectEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (RedirectEntry redirectEntry : list) {
					if (groupId != redirectEntry.getGroupId()) {
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

			sb.append(_SQL_SELECT_REDIRECTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(RedirectEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				list = (List<RedirectEntry>)QueryUtil.list(
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
	 * Returns the first redirect entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching redirect entry
	 * @throws NoSuchEntryException if a matching redirect entry could not be found
	 */
	@Override
	public RedirectEntry findByGroupId_First(
			long groupId, OrderByComparator<RedirectEntry> orderByComparator)
		throws NoSuchEntryException {

		RedirectEntry redirectEntry = fetchByGroupId_First(
			groupId, orderByComparator);

		if (redirectEntry != null) {
			return redirectEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first redirect entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching redirect entry, or <code>null</code> if a matching redirect entry could not be found
	 */
	@Override
	public RedirectEntry fetchByGroupId_First(
		long groupId, OrderByComparator<RedirectEntry> orderByComparator) {

		List<RedirectEntry> list = findByGroupId(
			groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last redirect entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching redirect entry
	 * @throws NoSuchEntryException if a matching redirect entry could not be found
	 */
	@Override
	public RedirectEntry findByGroupId_Last(
			long groupId, OrderByComparator<RedirectEntry> orderByComparator)
		throws NoSuchEntryException {

		RedirectEntry redirectEntry = fetchByGroupId_Last(
			groupId, orderByComparator);

		if (redirectEntry != null) {
			return redirectEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the last redirect entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching redirect entry, or <code>null</code> if a matching redirect entry could not be found
	 */
	@Override
	public RedirectEntry fetchByGroupId_Last(
		long groupId, OrderByComparator<RedirectEntry> orderByComparator) {

		int count = countByGroupId(groupId);

		if (count == 0) {
			return null;
		}

		List<RedirectEntry> list = findByGroupId(
			groupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the redirect entries before and after the current redirect entry in the ordered set where groupId = &#63;.
	 *
	 * @param redirectEntryId the primary key of the current redirect entry
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next redirect entry
	 * @throws NoSuchEntryException if a redirect entry with the primary key could not be found
	 */
	@Override
	public RedirectEntry[] findByGroupId_PrevAndNext(
			long redirectEntryId, long groupId,
			OrderByComparator<RedirectEntry> orderByComparator)
		throws NoSuchEntryException {

		RedirectEntry redirectEntry = findByPrimaryKey(redirectEntryId);

		Session session = null;

		try {
			session = openSession();

			RedirectEntry[] array = new RedirectEntryImpl[3];

			array[0] = getByGroupId_PrevAndNext(
				session, redirectEntry, groupId, orderByComparator, true);

			array[1] = redirectEntry;

			array[2] = getByGroupId_PrevAndNext(
				session, redirectEntry, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected RedirectEntry getByGroupId_PrevAndNext(
		Session session, RedirectEntry redirectEntry, long groupId,
		OrderByComparator<RedirectEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_REDIRECTENTRY_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

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
			sb.append(RedirectEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						redirectEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<RedirectEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the redirect entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching redirect entries that the user has permission to view
	 */
	@Override
	public List<RedirectEntry> filterFindByGroupId(long groupId) {
		return filterFindByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the redirect entries that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RedirectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of redirect entries
	 * @param end the upper bound of the range of redirect entries (not inclusive)
	 * @return the range of matching redirect entries that the user has permission to view
	 */
	@Override
	public List<RedirectEntry> filterFindByGroupId(
		long groupId, int start, int end) {

		return filterFindByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the redirect entries that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RedirectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of redirect entries
	 * @param end the upper bound of the range of redirect entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching redirect entries that the user has permission to view
	 */
	@Override
	public List<RedirectEntry> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<RedirectEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByGroupId(groupId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator),
				groupId);
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
			sb.append(_FILTER_SQL_SELECT_REDIRECTENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_REDIRECTENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_REDIRECTENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(RedirectEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(RedirectEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), RedirectEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, RedirectEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, RedirectEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			return (List<RedirectEntry>)QueryUtil.list(
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
	 * Returns the redirect entries before and after the current redirect entry in the ordered set of redirect entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param redirectEntryId the primary key of the current redirect entry
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next redirect entry
	 * @throws NoSuchEntryException if a redirect entry with the primary key could not be found
	 */
	@Override
	public RedirectEntry[] filterFindByGroupId_PrevAndNext(
			long redirectEntryId, long groupId,
			OrderByComparator<RedirectEntry> orderByComparator)
		throws NoSuchEntryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByGroupId_PrevAndNext(
				redirectEntryId, groupId, orderByComparator);
		}

		RedirectEntry redirectEntry = findByPrimaryKey(redirectEntryId);

		Session session = null;

		try {
			session = openSession();

			RedirectEntry[] array = new RedirectEntryImpl[3];

			array[0] = filterGetByGroupId_PrevAndNext(
				session, redirectEntry, groupId, orderByComparator, true);

			array[1] = redirectEntry;

			array[2] = filterGetByGroupId_PrevAndNext(
				session, redirectEntry, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected RedirectEntry filterGetByGroupId_PrevAndNext(
		Session session, RedirectEntry redirectEntry, long groupId,
		OrderByComparator<RedirectEntry> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_REDIRECTENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_REDIRECTENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_REDIRECTENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(RedirectEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(RedirectEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), RedirectEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, RedirectEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, RedirectEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						redirectEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<RedirectEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the redirect entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		for (RedirectEntry redirectEntry :
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(redirectEntry);
		}
	}

	/**
	 * Returns the number of redirect entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching redirect entries
	 */
	@Override
	public int countByGroupId(long groupId) {
		FinderPath finderPath = _finderPathCountByGroupId;

		Object[] finderArgs = new Object[] {groupId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_REDIRECTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

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
	 * Returns the number of redirect entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching redirect entries that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByGroupId(groupId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<RedirectEntry> redirectEntries = findByGroupId(groupId);

			redirectEntries = InlineSQLHelperUtil.filter(
				redirectEntries, groupId);

			return redirectEntries.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_REDIRECTENTRY_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), RedirectEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

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

	private static final String _FINDER_COLUMN_GROUPID_GROUPID_2 =
		"redirectEntry.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByG_D;
	private FinderPath _finderPathWithoutPaginationFindByG_D;
	private FinderPath _finderPathCountByG_D;

	/**
	 * Returns all the redirect entries where groupId = &#63; and destinationURL = &#63;.
	 *
	 * @param groupId the group ID
	 * @param destinationURL the destination url
	 * @return the matching redirect entries
	 */
	@Override
	public List<RedirectEntry> findByG_D(long groupId, String destinationURL) {
		return findByG_D(
			groupId, destinationURL, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the redirect entries where groupId = &#63; and destinationURL = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RedirectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param destinationURL the destination url
	 * @param start the lower bound of the range of redirect entries
	 * @param end the upper bound of the range of redirect entries (not inclusive)
	 * @return the range of matching redirect entries
	 */
	@Override
	public List<RedirectEntry> findByG_D(
		long groupId, String destinationURL, int start, int end) {

		return findByG_D(groupId, destinationURL, start, end, null);
	}

	/**
	 * Returns an ordered range of all the redirect entries where groupId = &#63; and destinationURL = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RedirectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param destinationURL the destination url
	 * @param start the lower bound of the range of redirect entries
	 * @param end the upper bound of the range of redirect entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching redirect entries
	 */
	@Override
	public List<RedirectEntry> findByG_D(
		long groupId, String destinationURL, int start, int end,
		OrderByComparator<RedirectEntry> orderByComparator) {

		return findByG_D(
			groupId, destinationURL, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the redirect entries where groupId = &#63; and destinationURL = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RedirectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param destinationURL the destination url
	 * @param start the lower bound of the range of redirect entries
	 * @param end the upper bound of the range of redirect entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching redirect entries
	 */
	@Override
	public List<RedirectEntry> findByG_D(
		long groupId, String destinationURL, int start, int end,
		OrderByComparator<RedirectEntry> orderByComparator,
		boolean useFinderCache) {

		destinationURL = Objects.toString(destinationURL, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByG_D;
				finderArgs = new Object[] {groupId, destinationURL};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByG_D;
			finderArgs = new Object[] {
				groupId, destinationURL, start, end, orderByComparator
			};
		}

		List<RedirectEntry> list = null;

		if (useFinderCache) {
			list = (List<RedirectEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (RedirectEntry redirectEntry : list) {
					if ((groupId != redirectEntry.getGroupId()) ||
						!destinationURL.equals(
							redirectEntry.getDestinationURL())) {

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

			sb.append(_SQL_SELECT_REDIRECTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_G_D_GROUPID_2);

			boolean bindDestinationURL = false;

			if (destinationURL.isEmpty()) {
				sb.append(_FINDER_COLUMN_G_D_DESTINATIONURL_3);
			}
			else {
				bindDestinationURL = true;

				sb.append(_FINDER_COLUMN_G_D_DESTINATIONURL_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(RedirectEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				if (bindDestinationURL) {
					queryPos.add(destinationURL);
				}

				list = (List<RedirectEntry>)QueryUtil.list(
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
	 * Returns the first redirect entry in the ordered set where groupId = &#63; and destinationURL = &#63;.
	 *
	 * @param groupId the group ID
	 * @param destinationURL the destination url
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching redirect entry
	 * @throws NoSuchEntryException if a matching redirect entry could not be found
	 */
	@Override
	public RedirectEntry findByG_D_First(
			long groupId, String destinationURL,
			OrderByComparator<RedirectEntry> orderByComparator)
		throws NoSuchEntryException {

		RedirectEntry redirectEntry = fetchByG_D_First(
			groupId, destinationURL, orderByComparator);

		if (redirectEntry != null) {
			return redirectEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", destinationURL=");
		sb.append(destinationURL);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first redirect entry in the ordered set where groupId = &#63; and destinationURL = &#63;.
	 *
	 * @param groupId the group ID
	 * @param destinationURL the destination url
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching redirect entry, or <code>null</code> if a matching redirect entry could not be found
	 */
	@Override
	public RedirectEntry fetchByG_D_First(
		long groupId, String destinationURL,
		OrderByComparator<RedirectEntry> orderByComparator) {

		List<RedirectEntry> list = findByG_D(
			groupId, destinationURL, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last redirect entry in the ordered set where groupId = &#63; and destinationURL = &#63;.
	 *
	 * @param groupId the group ID
	 * @param destinationURL the destination url
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching redirect entry
	 * @throws NoSuchEntryException if a matching redirect entry could not be found
	 */
	@Override
	public RedirectEntry findByG_D_Last(
			long groupId, String destinationURL,
			OrderByComparator<RedirectEntry> orderByComparator)
		throws NoSuchEntryException {

		RedirectEntry redirectEntry = fetchByG_D_Last(
			groupId, destinationURL, orderByComparator);

		if (redirectEntry != null) {
			return redirectEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", destinationURL=");
		sb.append(destinationURL);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the last redirect entry in the ordered set where groupId = &#63; and destinationURL = &#63;.
	 *
	 * @param groupId the group ID
	 * @param destinationURL the destination url
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching redirect entry, or <code>null</code> if a matching redirect entry could not be found
	 */
	@Override
	public RedirectEntry fetchByG_D_Last(
		long groupId, String destinationURL,
		OrderByComparator<RedirectEntry> orderByComparator) {

		int count = countByG_D(groupId, destinationURL);

		if (count == 0) {
			return null;
		}

		List<RedirectEntry> list = findByG_D(
			groupId, destinationURL, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the redirect entries before and after the current redirect entry in the ordered set where groupId = &#63; and destinationURL = &#63;.
	 *
	 * @param redirectEntryId the primary key of the current redirect entry
	 * @param groupId the group ID
	 * @param destinationURL the destination url
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next redirect entry
	 * @throws NoSuchEntryException if a redirect entry with the primary key could not be found
	 */
	@Override
	public RedirectEntry[] findByG_D_PrevAndNext(
			long redirectEntryId, long groupId, String destinationURL,
			OrderByComparator<RedirectEntry> orderByComparator)
		throws NoSuchEntryException {

		destinationURL = Objects.toString(destinationURL, "");

		RedirectEntry redirectEntry = findByPrimaryKey(redirectEntryId);

		Session session = null;

		try {
			session = openSession();

			RedirectEntry[] array = new RedirectEntryImpl[3];

			array[0] = getByG_D_PrevAndNext(
				session, redirectEntry, groupId, destinationURL,
				orderByComparator, true);

			array[1] = redirectEntry;

			array[2] = getByG_D_PrevAndNext(
				session, redirectEntry, groupId, destinationURL,
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

	protected RedirectEntry getByG_D_PrevAndNext(
		Session session, RedirectEntry redirectEntry, long groupId,
		String destinationURL,
		OrderByComparator<RedirectEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_REDIRECTENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_D_GROUPID_2);

		boolean bindDestinationURL = false;

		if (destinationURL.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_D_DESTINATIONURL_3);
		}
		else {
			bindDestinationURL = true;

			sb.append(_FINDER_COLUMN_G_D_DESTINATIONURL_2);
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
			sb.append(RedirectEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		if (bindDestinationURL) {
			queryPos.add(destinationURL);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						redirectEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<RedirectEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the redirect entries that the user has permission to view where groupId = &#63; and destinationURL = &#63;.
	 *
	 * @param groupId the group ID
	 * @param destinationURL the destination url
	 * @return the matching redirect entries that the user has permission to view
	 */
	@Override
	public List<RedirectEntry> filterFindByG_D(
		long groupId, String destinationURL) {

		return filterFindByG_D(
			groupId, destinationURL, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the redirect entries that the user has permission to view where groupId = &#63; and destinationURL = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RedirectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param destinationURL the destination url
	 * @param start the lower bound of the range of redirect entries
	 * @param end the upper bound of the range of redirect entries (not inclusive)
	 * @return the range of matching redirect entries that the user has permission to view
	 */
	@Override
	public List<RedirectEntry> filterFindByG_D(
		long groupId, String destinationURL, int start, int end) {

		return filterFindByG_D(groupId, destinationURL, start, end, null);
	}

	/**
	 * Returns an ordered range of all the redirect entries that the user has permissions to view where groupId = &#63; and destinationURL = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RedirectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param destinationURL the destination url
	 * @param start the lower bound of the range of redirect entries
	 * @param end the upper bound of the range of redirect entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching redirect entries that the user has permission to view
	 */
	@Override
	public List<RedirectEntry> filterFindByG_D(
		long groupId, String destinationURL, int start, int end,
		OrderByComparator<RedirectEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_D(
				groupId, destinationURL, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_D(
					groupId, destinationURL, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		destinationURL = Objects.toString(destinationURL, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_REDIRECTENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_REDIRECTENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_D_GROUPID_2);

		boolean bindDestinationURL = false;

		if (destinationURL.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_D_DESTINATIONURL_3);
		}
		else {
			bindDestinationURL = true;

			sb.append(_FINDER_COLUMN_G_D_DESTINATIONURL_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_REDIRECTENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(RedirectEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(RedirectEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), RedirectEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, RedirectEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, RedirectEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			if (bindDestinationURL) {
				queryPos.add(destinationURL);
			}

			return (List<RedirectEntry>)QueryUtil.list(
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
	 * Returns the redirect entries before and after the current redirect entry in the ordered set of redirect entries that the user has permission to view where groupId = &#63; and destinationURL = &#63;.
	 *
	 * @param redirectEntryId the primary key of the current redirect entry
	 * @param groupId the group ID
	 * @param destinationURL the destination url
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next redirect entry
	 * @throws NoSuchEntryException if a redirect entry with the primary key could not be found
	 */
	@Override
	public RedirectEntry[] filterFindByG_D_PrevAndNext(
			long redirectEntryId, long groupId, String destinationURL,
			OrderByComparator<RedirectEntry> orderByComparator)
		throws NoSuchEntryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_D_PrevAndNext(
				redirectEntryId, groupId, destinationURL, orderByComparator);
		}

		destinationURL = Objects.toString(destinationURL, "");

		RedirectEntry redirectEntry = findByPrimaryKey(redirectEntryId);

		Session session = null;

		try {
			session = openSession();

			RedirectEntry[] array = new RedirectEntryImpl[3];

			array[0] = filterGetByG_D_PrevAndNext(
				session, redirectEntry, groupId, destinationURL,
				orderByComparator, true);

			array[1] = redirectEntry;

			array[2] = filterGetByG_D_PrevAndNext(
				session, redirectEntry, groupId, destinationURL,
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

	protected RedirectEntry filterGetByG_D_PrevAndNext(
		Session session, RedirectEntry redirectEntry, long groupId,
		String destinationURL,
		OrderByComparator<RedirectEntry> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_REDIRECTENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_REDIRECTENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_D_GROUPID_2);

		boolean bindDestinationURL = false;

		if (destinationURL.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_D_DESTINATIONURL_3);
		}
		else {
			bindDestinationURL = true;

			sb.append(_FINDER_COLUMN_G_D_DESTINATIONURL_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_REDIRECTENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(RedirectEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(RedirectEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), RedirectEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, RedirectEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, RedirectEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (bindDestinationURL) {
			queryPos.add(destinationURL);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						redirectEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<RedirectEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the redirect entries where groupId = &#63; and destinationURL = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param destinationURL the destination url
	 */
	@Override
	public void removeByG_D(long groupId, String destinationURL) {
		for (RedirectEntry redirectEntry :
				findByG_D(
					groupId, destinationURL, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(redirectEntry);
		}
	}

	/**
	 * Returns the number of redirect entries where groupId = &#63; and destinationURL = &#63;.
	 *
	 * @param groupId the group ID
	 * @param destinationURL the destination url
	 * @return the number of matching redirect entries
	 */
	@Override
	public int countByG_D(long groupId, String destinationURL) {
		destinationURL = Objects.toString(destinationURL, "");

		FinderPath finderPath = _finderPathCountByG_D;

		Object[] finderArgs = new Object[] {groupId, destinationURL};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_REDIRECTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_G_D_GROUPID_2);

			boolean bindDestinationURL = false;

			if (destinationURL.isEmpty()) {
				sb.append(_FINDER_COLUMN_G_D_DESTINATIONURL_3);
			}
			else {
				bindDestinationURL = true;

				sb.append(_FINDER_COLUMN_G_D_DESTINATIONURL_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				if (bindDestinationURL) {
					queryPos.add(destinationURL);
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
	 * Returns the number of redirect entries that the user has permission to view where groupId = &#63; and destinationURL = &#63;.
	 *
	 * @param groupId the group ID
	 * @param destinationURL the destination url
	 * @return the number of matching redirect entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_D(long groupId, String destinationURL) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_D(groupId, destinationURL);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<RedirectEntry> redirectEntries = findByG_D(
				groupId, destinationURL);

			redirectEntries = InlineSQLHelperUtil.filter(
				redirectEntries, groupId);

			return redirectEntries.size();
		}

		destinationURL = Objects.toString(destinationURL, "");

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_REDIRECTENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_D_GROUPID_2);

		boolean bindDestinationURL = false;

		if (destinationURL.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_D_DESTINATIONURL_3);
		}
		else {
			bindDestinationURL = true;

			sb.append(_FINDER_COLUMN_G_D_DESTINATIONURL_2);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), RedirectEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			if (bindDestinationURL) {
				queryPos.add(destinationURL);
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

	private static final String _FINDER_COLUMN_G_D_GROUPID_2 =
		"redirectEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_D_DESTINATIONURL_2 =
		"redirectEntry.destinationURL = ?";

	private static final String _FINDER_COLUMN_G_D_DESTINATIONURL_3 =
		"(redirectEntry.destinationURL IS NULL OR redirectEntry.destinationURL = '')";

	private FinderPath _finderPathFetchByG_S;

	/**
	 * Returns the redirect entry where groupId = &#63; and sourceURL = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param sourceURL the source url
	 * @return the matching redirect entry
	 * @throws NoSuchEntryException if a matching redirect entry could not be found
	 */
	@Override
	public RedirectEntry findByG_S(long groupId, String sourceURL)
		throws NoSuchEntryException {

		RedirectEntry redirectEntry = fetchByG_S(groupId, sourceURL);

		if (redirectEntry == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("groupId=");
			sb.append(groupId);

			sb.append(", sourceURL=");
			sb.append(sourceURL);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchEntryException(sb.toString());
		}

		return redirectEntry;
	}

	/**
	 * Returns the redirect entry where groupId = &#63; and sourceURL = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param sourceURL the source url
	 * @return the matching redirect entry, or <code>null</code> if a matching redirect entry could not be found
	 */
	@Override
	public RedirectEntry fetchByG_S(long groupId, String sourceURL) {
		return fetchByG_S(groupId, sourceURL, true);
	}

	/**
	 * Returns the redirect entry where groupId = &#63; and sourceURL = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param sourceURL the source url
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching redirect entry, or <code>null</code> if a matching redirect entry could not be found
	 */
	@Override
	public RedirectEntry fetchByG_S(
		long groupId, String sourceURL, boolean useFinderCache) {

		sourceURL = Objects.toString(sourceURL, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {groupId, sourceURL};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByG_S, finderArgs, this);
		}

		if (result instanceof RedirectEntry) {
			RedirectEntry redirectEntry = (RedirectEntry)result;

			if ((groupId != redirectEntry.getGroupId()) ||
				!Objects.equals(sourceURL, redirectEntry.getSourceURL())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_REDIRECTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_G_S_GROUPID_2);

			boolean bindSourceURL = false;

			if (sourceURL.isEmpty()) {
				sb.append(_FINDER_COLUMN_G_S_SOURCEURL_3);
			}
			else {
				bindSourceURL = true;

				sb.append(_FINDER_COLUMN_G_S_SOURCEURL_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				if (bindSourceURL) {
					queryPos.add(sourceURL);
				}

				List<RedirectEntry> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByG_S, finderArgs, list);
					}
				}
				else {
					RedirectEntry redirectEntry = list.get(0);

					result = redirectEntry;

					cacheResult(redirectEntry);
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
			return (RedirectEntry)result;
		}
	}

	/**
	 * Removes the redirect entry where groupId = &#63; and sourceURL = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param sourceURL the source url
	 * @return the redirect entry that was removed
	 */
	@Override
	public RedirectEntry removeByG_S(long groupId, String sourceURL)
		throws NoSuchEntryException {

		RedirectEntry redirectEntry = findByG_S(groupId, sourceURL);

		return remove(redirectEntry);
	}

	/**
	 * Returns the number of redirect entries where groupId = &#63; and sourceURL = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sourceURL the source url
	 * @return the number of matching redirect entries
	 */
	@Override
	public int countByG_S(long groupId, String sourceURL) {
		RedirectEntry redirectEntry = fetchByG_S(groupId, sourceURL);

		if (redirectEntry == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_G_S_GROUPID_2 =
		"redirectEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_S_SOURCEURL_2 =
		"redirectEntry.sourceURL = ?";

	private static final String _FINDER_COLUMN_G_S_SOURCEURL_3 =
		"(redirectEntry.sourceURL IS NULL OR redirectEntry.sourceURL = '')";

	public RedirectEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("permanent", "permanent_");

		setDBColumnNames(dbColumnNames);

		setModelClass(RedirectEntry.class);

		setModelImplClass(RedirectEntryImpl.class);
		setModelPKClass(long.class);

		setTable(RedirectEntryTable.INSTANCE);
	}

	/**
	 * Caches the redirect entry in the entity cache if it is enabled.
	 *
	 * @param redirectEntry the redirect entry
	 */
	@Override
	public void cacheResult(RedirectEntry redirectEntry) {
		entityCache.putResult(
			RedirectEntryImpl.class, redirectEntry.getPrimaryKey(),
			redirectEntry);

		finderCache.putResult(
			_finderPathFetchByUUID_G,
			new Object[] {redirectEntry.getUuid(), redirectEntry.getGroupId()},
			redirectEntry);

		finderCache.putResult(
			_finderPathFetchByG_S,
			new Object[] {
				redirectEntry.getGroupId(), redirectEntry.getSourceURL()
			},
			redirectEntry);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the redirect entries in the entity cache if it is enabled.
	 *
	 * @param redirectEntries the redirect entries
	 */
	@Override
	public void cacheResult(List<RedirectEntry> redirectEntries) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (redirectEntries.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (RedirectEntry redirectEntry : redirectEntries) {
			if (entityCache.getResult(
					RedirectEntryImpl.class, redirectEntry.getPrimaryKey()) ==
						null) {

				cacheResult(redirectEntry);
			}
		}
	}

	/**
	 * Clears the cache for all redirect entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(RedirectEntryImpl.class);

		finderCache.clearCache(RedirectEntryImpl.class);
	}

	/**
	 * Clears the cache for the redirect entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(RedirectEntry redirectEntry) {
		entityCache.removeResult(RedirectEntryImpl.class, redirectEntry);
	}

	@Override
	public void clearCache(List<RedirectEntry> redirectEntries) {
		for (RedirectEntry redirectEntry : redirectEntries) {
			entityCache.removeResult(RedirectEntryImpl.class, redirectEntry);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(RedirectEntryImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(RedirectEntryImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		RedirectEntryModelImpl redirectEntryModelImpl) {

		Object[] args = new Object[] {
			redirectEntryModelImpl.getUuid(),
			redirectEntryModelImpl.getGroupId()
		};

		finderCache.putResult(
			_finderPathFetchByUUID_G, args, redirectEntryModelImpl);

		args = new Object[] {
			redirectEntryModelImpl.getGroupId(),
			redirectEntryModelImpl.getSourceURL()
		};

		finderCache.putResult(
			_finderPathFetchByG_S, args, redirectEntryModelImpl);
	}

	/**
	 * Creates a new redirect entry with the primary key. Does not add the redirect entry to the database.
	 *
	 * @param redirectEntryId the primary key for the new redirect entry
	 * @return the new redirect entry
	 */
	@Override
	public RedirectEntry create(long redirectEntryId) {
		RedirectEntry redirectEntry = new RedirectEntryImpl();

		redirectEntry.setNew(true);
		redirectEntry.setPrimaryKey(redirectEntryId);

		String uuid = PortalUUIDUtil.generate();

		redirectEntry.setUuid(uuid);

		redirectEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return redirectEntry;
	}

	/**
	 * Removes the redirect entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param redirectEntryId the primary key of the redirect entry
	 * @return the redirect entry that was removed
	 * @throws NoSuchEntryException if a redirect entry with the primary key could not be found
	 */
	@Override
	public RedirectEntry remove(long redirectEntryId)
		throws NoSuchEntryException {

		return remove((Serializable)redirectEntryId);
	}

	/**
	 * Removes the redirect entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the redirect entry
	 * @return the redirect entry that was removed
	 * @throws NoSuchEntryException if a redirect entry with the primary key could not be found
	 */
	@Override
	public RedirectEntry remove(Serializable primaryKey)
		throws NoSuchEntryException {

		Session session = null;

		try {
			session = openSession();

			RedirectEntry redirectEntry = (RedirectEntry)session.get(
				RedirectEntryImpl.class, primaryKey);

			if (redirectEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(redirectEntry);
		}
		catch (NoSuchEntryException noSuchEntityException) {
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
	protected RedirectEntry removeImpl(RedirectEntry redirectEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(redirectEntry)) {
				redirectEntry = (RedirectEntry)session.get(
					RedirectEntryImpl.class, redirectEntry.getPrimaryKeyObj());
			}

			if (redirectEntry != null) {
				session.delete(redirectEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (redirectEntry != null) {
			clearCache(redirectEntry);
		}

		return redirectEntry;
	}

	@Override
	public RedirectEntry updateImpl(RedirectEntry redirectEntry) {
		boolean isNew = redirectEntry.isNew();

		if (!(redirectEntry instanceof RedirectEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(redirectEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					redirectEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in redirectEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom RedirectEntry implementation " +
					redirectEntry.getClass());
		}

		RedirectEntryModelImpl redirectEntryModelImpl =
			(RedirectEntryModelImpl)redirectEntry;

		if (Validator.isNull(redirectEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			redirectEntry.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (redirectEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				redirectEntry.setCreateDate(date);
			}
			else {
				redirectEntry.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!redirectEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				redirectEntry.setModifiedDate(date);
			}
			else {
				redirectEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		long userId = GetterUtil.getLong(PrincipalThreadLocal.getName());

		if (userId > 0) {
			long companyId = redirectEntry.getCompanyId();

			long groupId = redirectEntry.getGroupId();

			long redirectEntryId = 0;

			if (!isNew) {
				redirectEntryId = redirectEntry.getPrimaryKey();
			}

			try {
				redirectEntry.setDestinationURL(
					SanitizerUtil.sanitize(
						companyId, groupId, userId,
						RedirectEntry.class.getName(), redirectEntryId,
						ContentTypes.TEXT_PLAIN, Sanitizer.MODE_ALL,
						redirectEntry.getDestinationURL(), null));

				redirectEntry.setSourceURL(
					SanitizerUtil.sanitize(
						companyId, groupId, userId,
						RedirectEntry.class.getName(), redirectEntryId,
						ContentTypes.TEXT_PLAIN, Sanitizer.MODE_ALL,
						redirectEntry.getSourceURL(), null));
			}
			catch (SanitizerException sanitizerException) {
				throw new SystemException(sanitizerException);
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(redirectEntry);
			}
			else {
				redirectEntry = (RedirectEntry)session.merge(redirectEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			RedirectEntryImpl.class, redirectEntryModelImpl, false, true);

		cacheUniqueFindersCache(redirectEntryModelImpl);

		if (isNew) {
			redirectEntry.setNew(false);
		}

		redirectEntry.resetOriginalValues();

		return redirectEntry;
	}

	/**
	 * Returns the redirect entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the redirect entry
	 * @return the redirect entry
	 * @throws NoSuchEntryException if a redirect entry with the primary key could not be found
	 */
	@Override
	public RedirectEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchEntryException {

		RedirectEntry redirectEntry = fetchByPrimaryKey(primaryKey);

		if (redirectEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return redirectEntry;
	}

	/**
	 * Returns the redirect entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param redirectEntryId the primary key of the redirect entry
	 * @return the redirect entry
	 * @throws NoSuchEntryException if a redirect entry with the primary key could not be found
	 */
	@Override
	public RedirectEntry findByPrimaryKey(long redirectEntryId)
		throws NoSuchEntryException {

		return findByPrimaryKey((Serializable)redirectEntryId);
	}

	/**
	 * Returns the redirect entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param redirectEntryId the primary key of the redirect entry
	 * @return the redirect entry, or <code>null</code> if a redirect entry with the primary key could not be found
	 */
	@Override
	public RedirectEntry fetchByPrimaryKey(long redirectEntryId) {
		return fetchByPrimaryKey((Serializable)redirectEntryId);
	}

	/**
	 * Returns all the redirect entries.
	 *
	 * @return the redirect entries
	 */
	@Override
	public List<RedirectEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the redirect entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RedirectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of redirect entries
	 * @param end the upper bound of the range of redirect entries (not inclusive)
	 * @return the range of redirect entries
	 */
	@Override
	public List<RedirectEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the redirect entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RedirectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of redirect entries
	 * @param end the upper bound of the range of redirect entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of redirect entries
	 */
	@Override
	public List<RedirectEntry> findAll(
		int start, int end,
		OrderByComparator<RedirectEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the redirect entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RedirectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of redirect entries
	 * @param end the upper bound of the range of redirect entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of redirect entries
	 */
	@Override
	public List<RedirectEntry> findAll(
		int start, int end, OrderByComparator<RedirectEntry> orderByComparator,
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

		List<RedirectEntry> list = null;

		if (useFinderCache) {
			list = (List<RedirectEntry>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_REDIRECTENTRY);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_REDIRECTENTRY;

				sql = sql.concat(RedirectEntryModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<RedirectEntry>)QueryUtil.list(
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
	 * Removes all the redirect entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (RedirectEntry redirectEntry : findAll()) {
			remove(redirectEntry);
		}
	}

	/**
	 * Returns the number of redirect entries.
	 *
	 * @return the number of redirect entries
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_REDIRECTENTRY);

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
		return "redirectEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_REDIRECTENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return RedirectEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the redirect entry persistence.
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

		_finderPathFetchByUUID_G = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"uuid_", "groupId"}, true);

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

		_finderPathWithPaginationFindByGroupId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGroupId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId"}, true);

		_finderPathWithoutPaginationFindByGroupId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByGroupId",
			new String[] {Long.class.getName()}, new String[] {"groupId"},
			true);

		_finderPathCountByGroupId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByGroupId",
			new String[] {Long.class.getName()}, new String[] {"groupId"},
			false);

		_finderPathWithPaginationFindByG_D = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_D",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "destinationURL"}, true);

		_finderPathWithoutPaginationFindByG_D = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_D",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"groupId", "destinationURL"}, true);

		_finderPathCountByG_D = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_D",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"groupId", "destinationURL"}, false);

		_finderPathFetchByG_S = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByG_S",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"groupId", "sourceURL"}, true);

		RedirectEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		RedirectEntryUtil.setPersistence(null);

		entityCache.removeCache(RedirectEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = RedirectPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = RedirectPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = RedirectPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_REDIRECTENTRY =
		"SELECT redirectEntry FROM RedirectEntry redirectEntry";

	private static final String _SQL_SELECT_REDIRECTENTRY_WHERE =
		"SELECT redirectEntry FROM RedirectEntry redirectEntry WHERE ";

	private static final String _SQL_COUNT_REDIRECTENTRY =
		"SELECT COUNT(redirectEntry) FROM RedirectEntry redirectEntry";

	private static final String _SQL_COUNT_REDIRECTENTRY_WHERE =
		"SELECT COUNT(redirectEntry) FROM RedirectEntry redirectEntry WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"redirectEntry.redirectEntryId";

	private static final String _FILTER_SQL_SELECT_REDIRECTENTRY_WHERE =
		"SELECT DISTINCT {redirectEntry.*} FROM RedirectEntry redirectEntry WHERE ";

	private static final String
		_FILTER_SQL_SELECT_REDIRECTENTRY_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {RedirectEntry.*} FROM (SELECT DISTINCT redirectEntry.redirectEntryId FROM RedirectEntry redirectEntry WHERE ";

	private static final String
		_FILTER_SQL_SELECT_REDIRECTENTRY_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN RedirectEntry ON TEMP_TABLE.redirectEntryId = RedirectEntry.redirectEntryId";

	private static final String _FILTER_SQL_COUNT_REDIRECTENTRY_WHERE =
		"SELECT COUNT(DISTINCT redirectEntry.redirectEntryId) AS COUNT_VALUE FROM RedirectEntry redirectEntry WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "redirectEntry";

	private static final String _FILTER_ENTITY_TABLE = "RedirectEntry";

	private static final String _ORDER_BY_ENTITY_ALIAS = "redirectEntry.";

	private static final String _ORDER_BY_ENTITY_TABLE = "RedirectEntry.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No RedirectEntry exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No RedirectEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		RedirectEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "permanent"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}