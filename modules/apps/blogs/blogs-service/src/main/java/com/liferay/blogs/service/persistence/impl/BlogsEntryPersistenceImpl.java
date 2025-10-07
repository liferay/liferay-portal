/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.service.persistence.impl;

import com.liferay.blogs.exception.DuplicateBlogsEntryExternalReferenceCodeException;
import com.liferay.blogs.exception.NoSuchEntryException;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.model.BlogsEntryTable;
import com.liferay.blogs.model.impl.BlogsEntryImpl;
import com.liferay.blogs.model.impl.BlogsEntryModelImpl;
import com.liferay.blogs.service.persistence.BlogsEntryPersistence;
import com.liferay.blogs.service.persistence.BlogsEntryUtil;
import com.liferay.blogs.service.persistence.impl.constants.BlogsPersistenceConstants;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
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
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.ArrayUtil;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
 * The persistence implementation for the blogs entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = BlogsEntryPersistence.class)
public class BlogsEntryPersistenceImpl
	extends BasePersistenceImpl<BlogsEntry> implements BlogsEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>BlogsEntryUtil</code> to access the blogs entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		BlogsEntryImpl.class.getName();

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
	 * Returns all the blogs entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the blogs entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

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

			List<BlogsEntry> list = null;

			if (useFinderCache) {
				list = (List<BlogsEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BlogsEntry blogsEntry : list) {
						if (!uuid.equals(blogsEntry.getUuid())) {
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

				sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

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
					sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
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

					list = (List<BlogsEntry>)QueryUtil.list(
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
	}

	/**
	 * Returns the first blogs entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByUuid_First(
			String uuid, OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByUuid_First(uuid, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first blogs entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByUuid_First(
		String uuid, OrderByComparator<BlogsEntry> orderByComparator) {

		List<BlogsEntry> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last blogs entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByUuid_Last(
			String uuid, OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByUuid_Last(uuid, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the last blogs entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByUuid_Last(
		String uuid, OrderByComparator<BlogsEntry> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<BlogsEntry> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the blogs entries before and after the current blogs entry in the ordered set where uuid = &#63;.
	 *
	 * @param entryId the primary key of the current blogs entry
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry[] findByUuid_PrevAndNext(
			long entryId, String uuid,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		uuid = Objects.toString(uuid, "");

		BlogsEntry blogsEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			BlogsEntry[] array = new BlogsEntryImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, blogsEntry, uuid, orderByComparator, true);

			array[1] = blogsEntry;

			array[2] = getByUuid_PrevAndNext(
				session, blogsEntry, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected BlogsEntry getByUuid_PrevAndNext(
		Session session, BlogsEntry blogsEntry, String uuid,
		OrderByComparator<BlogsEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

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
			sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(blogsEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BlogsEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the blogs entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (BlogsEntry blogsEntry :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(blogsEntry);
		}
	}

	/**
	 * Returns the number of blogs entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByUuid(String uuid) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid;

			Object[] finderArgs = new Object[] {uuid};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_BLOGSENTRY_WHERE);

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
	}

	private static final String _FINDER_COLUMN_UUID_UUID_2 =
		"blogsEntry.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(blogsEntry.uuid IS NULL OR blogsEntry.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;

	/**
	 * Returns the blogs entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByUUID_G(uuid, groupId);

		if (blogsEntry == null) {
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

		return blogsEntry;
	}

	/**
	 * Returns the blogs entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the blogs entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

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

			if (result instanceof BlogsEntry) {
				BlogsEntry blogsEntry = (BlogsEntry)result;

				if (!Objects.equals(uuid, blogsEntry.getUuid()) ||
					(groupId != blogsEntry.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

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

					List<BlogsEntry> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByUUID_G, finderArgs, list);
						}
					}
					else {
						BlogsEntry blogsEntry = list.get(0);

						result = blogsEntry;

						cacheResult(blogsEntry);
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
				return (BlogsEntry)result;
			}
		}
	}

	/**
	 * Removes the blogs entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the blogs entry that was removed
	 */
	@Override
	public BlogsEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = findByUUID_G(uuid, groupId);

		return remove(blogsEntry);
	}

	/**
	 * Returns the number of blogs entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		BlogsEntry blogsEntry = fetchByUUID_G(uuid, groupId);

		if (blogsEntry == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"blogsEntry.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(blogsEntry.uuid IS NULL OR blogsEntry.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"blogsEntry.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the blogs entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the blogs entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

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

			List<BlogsEntry> list = null;

			if (useFinderCache) {
				list = (List<BlogsEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BlogsEntry blogsEntry : list) {
						if (!uuid.equals(blogsEntry.getUuid()) ||
							(companyId != blogsEntry.getCompanyId())) {

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

				sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

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
					sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
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

					list = (List<BlogsEntry>)QueryUtil.list(
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
	}

	/**
	 * Returns the first blogs entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
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
	 * Returns the first blogs entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<BlogsEntry> orderByComparator) {

		List<BlogsEntry> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last blogs entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
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
	 * Returns the last blogs entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<BlogsEntry> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<BlogsEntry> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the blogs entries before and after the current blogs entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param entryId the primary key of the current blogs entry
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry[] findByUuid_C_PrevAndNext(
			long entryId, String uuid, long companyId,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		uuid = Objects.toString(uuid, "");

		BlogsEntry blogsEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			BlogsEntry[] array = new BlogsEntryImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, blogsEntry, uuid, companyId, orderByComparator, true);

			array[1] = blogsEntry;

			array[2] = getByUuid_C_PrevAndNext(
				session, blogsEntry, uuid, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected BlogsEntry getByUuid_C_PrevAndNext(
		Session session, BlogsEntry blogsEntry, String uuid, long companyId,
		OrderByComparator<BlogsEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

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
			sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(blogsEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BlogsEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the blogs entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (BlogsEntry blogsEntry :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(blogsEntry);
		}
	}

	/**
	 * Returns the number of blogs entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid_C;

			Object[] finderArgs = new Object[] {uuid, companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_BLOGSENTRY_WHERE);

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
	}

	private static final String _FINDER_COLUMN_UUID_C_UUID_2 =
		"blogsEntry.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(blogsEntry.uuid IS NULL OR blogsEntry.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"blogsEntry.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByGroupId;
	private FinderPath _finderPathWithoutPaginationFindByGroupId;
	private FinderPath _finderPathCountByGroupId;

	/**
	 * Returns all the blogs entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the blogs entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByGroupId(long groupId, int start, int end) {
		return findByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

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
				finderArgs = new Object[] {
					groupId, start, end, orderByComparator
				};
			}

			List<BlogsEntry> list = null;

			if (useFinderCache) {
				list = (List<BlogsEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BlogsEntry blogsEntry : list) {
						if (groupId != blogsEntry.getGroupId()) {
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

				sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					list = (List<BlogsEntry>)QueryUtil.list(
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
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByGroupId_First(
			long groupId, OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByGroupId_First(
			groupId, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByGroupId_First(
		long groupId, OrderByComparator<BlogsEntry> orderByComparator) {

		List<BlogsEntry> list = findByGroupId(groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last blogs entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByGroupId_Last(
			long groupId, OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByGroupId_Last(groupId, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the last blogs entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByGroupId_Last(
		long groupId, OrderByComparator<BlogsEntry> orderByComparator) {

		int count = countByGroupId(groupId);

		if (count == 0) {
			return null;
		}

		List<BlogsEntry> list = findByGroupId(
			groupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the blogs entries before and after the current blogs entry in the ordered set where groupId = &#63;.
	 *
	 * @param entryId the primary key of the current blogs entry
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry[] findByGroupId_PrevAndNext(
			long entryId, long groupId,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			BlogsEntry[] array = new BlogsEntryImpl[3];

			array[0] = getByGroupId_PrevAndNext(
				session, blogsEntry, groupId, orderByComparator, true);

			array[1] = blogsEntry;

			array[2] = getByGroupId_PrevAndNext(
				session, blogsEntry, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected BlogsEntry getByGroupId_PrevAndNext(
		Session session, BlogsEntry blogsEntry, long groupId,
		OrderByComparator<BlogsEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

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
			sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(blogsEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BlogsEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the blogs entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByGroupId(long groupId) {
		return filterFindByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the blogs entries that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByGroupId(
		long groupId, int start, int end) {

		return filterFindByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, BlogsEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, BlogsEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			return (List<BlogsEntry>)QueryUtil.list(
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
	 * Returns the blogs entries before and after the current blogs entry in the ordered set of blogs entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param entryId the primary key of the current blogs entry
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry[] filterFindByGroupId_PrevAndNext(
			long entryId, long groupId,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByGroupId_PrevAndNext(
				entryId, groupId, orderByComparator);
		}

		BlogsEntry blogsEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			BlogsEntry[] array = new BlogsEntryImpl[3];

			array[0] = filterGetByGroupId_PrevAndNext(
				session, blogsEntry, groupId, orderByComparator, true);

			array[1] = blogsEntry;

			array[2] = filterGetByGroupId_PrevAndNext(
				session, blogsEntry, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected BlogsEntry filterGetByGroupId_PrevAndNext(
		Session session, BlogsEntry blogsEntry, long groupId,
		OrderByComparator<BlogsEntry> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, BlogsEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, BlogsEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(blogsEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BlogsEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		for (BlogsEntry blogsEntry :
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(blogsEntry);
		}
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByGroupId(long groupId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = _finderPathCountByGroupId;

			Object[] finderArgs = new Object[] {groupId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_BLOGSENTRY_WHERE);

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
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByGroupId(groupId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<BlogsEntry> blogsEntries = findByGroupId(groupId);

			blogsEntries = InlineSQLHelperUtil.filter(blogsEntries, groupId);

			return blogsEntries.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_BLOGSENTRY_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
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
		"blogsEntry.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;

	/**
	 * Returns all the blogs entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the blogs entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

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

			List<BlogsEntry> list = null;

			if (useFinderCache) {
				list = (List<BlogsEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BlogsEntry blogsEntry : list) {
						if (companyId != blogsEntry.getCompanyId()) {
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

				sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					list = (List<BlogsEntry>)QueryUtil.list(
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
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByCompanyId_First(
			long companyId, OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByCompanyId_First(
		long companyId, OrderByComparator<BlogsEntry> orderByComparator) {

		List<BlogsEntry> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last blogs entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByCompanyId_Last(
			long companyId, OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByCompanyId_Last(
			companyId, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the last blogs entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByCompanyId_Last(
		long companyId, OrderByComparator<BlogsEntry> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<BlogsEntry> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the blogs entries before and after the current blogs entry in the ordered set where companyId = &#63;.
	 *
	 * @param entryId the primary key of the current blogs entry
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry[] findByCompanyId_PrevAndNext(
			long entryId, long companyId,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			BlogsEntry[] array = new BlogsEntryImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, blogsEntry, companyId, orderByComparator, true);

			array[1] = blogsEntry;

			array[2] = getByCompanyId_PrevAndNext(
				session, blogsEntry, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected BlogsEntry getByCompanyId_PrevAndNext(
		Session session, BlogsEntry blogsEntry, long companyId,
		OrderByComparator<BlogsEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

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
			sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(blogsEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BlogsEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the blogs entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (BlogsEntry blogsEntry :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(blogsEntry);
		}
	}

	/**
	 * Returns the number of blogs entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByCompanyId(long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = _finderPathCountByCompanyId;

			Object[] finderArgs = new Object[] {companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_BLOGSENTRY_WHERE);

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
	}

	private static final String _FINDER_COLUMN_COMPANYID_COMPANYID_2 =
		"blogsEntry.companyId = ?";

	private FinderPath _finderPathFetchByG_UT;

	/**
	 * Returns the blogs entry where groupId = &#63; and urlTitle = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @return the matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByG_UT(long groupId, String urlTitle)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByG_UT(groupId, urlTitle);

		if (blogsEntry == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("groupId=");
			sb.append(groupId);

			sb.append(", urlTitle=");
			sb.append(urlTitle);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchEntryException(sb.toString());
		}

		return blogsEntry;
	}

	/**
	 * Returns the blogs entry where groupId = &#63; and urlTitle = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @return the matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_UT(long groupId, String urlTitle) {
		return fetchByG_UT(groupId, urlTitle, true);
	}

	/**
	 * Returns the blogs entry where groupId = &#63; and urlTitle = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_UT(
		long groupId, String urlTitle, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			urlTitle = Objects.toString(urlTitle, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {groupId, urlTitle};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByG_UT, finderArgs, this);
			}

			if (result instanceof BlogsEntry) {
				BlogsEntry blogsEntry = (BlogsEntry)result;

				if ((groupId != blogsEntry.getGroupId()) ||
					!Objects.equals(urlTitle, blogsEntry.getUrlTitle())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_UT_GROUPID_2);

				boolean bindUrlTitle = false;

				if (urlTitle.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_UT_URLTITLE_3);
				}
				else {
					bindUrlTitle = true;

					sb.append(_FINDER_COLUMN_G_UT_URLTITLE_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					if (bindUrlTitle) {
						queryPos.add(urlTitle);
					}

					List<BlogsEntry> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByG_UT, finderArgs, list);
						}
					}
					else {
						BlogsEntry blogsEntry = list.get(0);

						result = blogsEntry;

						cacheResult(blogsEntry);
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
				return (BlogsEntry)result;
			}
		}
	}

	/**
	 * Removes the blogs entry where groupId = &#63; and urlTitle = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @return the blogs entry that was removed
	 */
	@Override
	public BlogsEntry removeByG_UT(long groupId, String urlTitle)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = findByG_UT(groupId, urlTitle);

		return remove(blogsEntry);
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByG_UT(long groupId, String urlTitle) {
		BlogsEntry blogsEntry = fetchByG_UT(groupId, urlTitle);

		if (blogsEntry == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_G_UT_GROUPID_2 =
		"blogsEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_UT_URLTITLE_2 =
		"blogsEntry.urlTitle = ?";

	private static final String _FINDER_COLUMN_G_UT_URLTITLE_3 =
		"(blogsEntry.urlTitle IS NULL OR blogsEntry.urlTitle = '')";

	private FinderPath _finderPathWithPaginationFindByG_LtD;
	private FinderPath _finderPathWithPaginationCountByG_LtD;

	/**
	 * Returns all the blogs entries where groupId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_LtD(long groupId, Date displayDate) {
		return findByG_LtD(
			groupId, displayDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the blogs entries where groupId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_LtD(
		long groupId, Date displayDate, int start, int end) {

		return findByG_LtD(groupId, displayDate, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_LtD(
		long groupId, Date displayDate, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return findByG_LtD(
			groupId, displayDate, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_LtD(
		long groupId, Date displayDate, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByG_LtD;
			finderArgs = new Object[] {
				groupId, _getTime(displayDate), start, end, orderByComparator
			};

			List<BlogsEntry> list = null;

			if (useFinderCache) {
				list = (List<BlogsEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BlogsEntry blogsEntry : list) {
						if ((groupId != blogsEntry.getGroupId()) ||
							(displayDate.getTime() <= blogsEntry.getDisplayDate(
							).getTime())) {

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

				sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_LTD_GROUPID_2);

				boolean bindDisplayDate = false;

				if (displayDate == null) {
					sb.append(_FINDER_COLUMN_G_LTD_DISPLAYDATE_1);
				}
				else {
					bindDisplayDate = true;

					sb.append(_FINDER_COLUMN_G_LTD_DISPLAYDATE_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					if (bindDisplayDate) {
						queryPos.add(new Timestamp(displayDate.getTime()));
					}

					list = (List<BlogsEntry>)QueryUtil.list(
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
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByG_LtD_First(
			long groupId, Date displayDate,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByG_LtD_First(
			groupId, displayDate, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", displayDate<");
		sb.append(displayDate);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_LtD_First(
		long groupId, Date displayDate,
		OrderByComparator<BlogsEntry> orderByComparator) {

		List<BlogsEntry> list = findByG_LtD(
			groupId, displayDate, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last blogs entry in the ordered set where groupId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByG_LtD_Last(
			long groupId, Date displayDate,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByG_LtD_Last(
			groupId, displayDate, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", displayDate<");
		sb.append(displayDate);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the last blogs entry in the ordered set where groupId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_LtD_Last(
		long groupId, Date displayDate,
		OrderByComparator<BlogsEntry> orderByComparator) {

		int count = countByG_LtD(groupId, displayDate);

		if (count == 0) {
			return null;
		}

		List<BlogsEntry> list = findByG_LtD(
			groupId, displayDate, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the blogs entries before and after the current blogs entry in the ordered set where groupId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param entryId the primary key of the current blogs entry
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry[] findByG_LtD_PrevAndNext(
			long entryId, long groupId, Date displayDate,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			BlogsEntry[] array = new BlogsEntryImpl[3];

			array[0] = getByG_LtD_PrevAndNext(
				session, blogsEntry, groupId, displayDate, orderByComparator,
				true);

			array[1] = blogsEntry;

			array[2] = getByG_LtD_PrevAndNext(
				session, blogsEntry, groupId, displayDate, orderByComparator,
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

	protected BlogsEntry getByG_LtD_PrevAndNext(
		Session session, BlogsEntry blogsEntry, long groupId, Date displayDate,
		OrderByComparator<BlogsEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_LTD_GROUPID_2);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_G_LTD_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_G_LTD_DISPLAYDATE_2);
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
			sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		if (bindDisplayDate) {
			queryPos.add(new Timestamp(displayDate.getTime()));
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(blogsEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BlogsEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the blogs entries that the user has permission to view where groupId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @return the matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_LtD(long groupId, Date displayDate) {
		return filterFindByG_LtD(
			groupId, displayDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the blogs entries that the user has permission to view where groupId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_LtD(
		long groupId, Date displayDate, int start, int end) {

		return filterFindByG_LtD(groupId, displayDate, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_LtD(
		long groupId, Date displayDate, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_LtD(
				groupId, displayDate, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_LtD(
					groupId, displayDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator),
				groupId);
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
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_LTD_GROUPID_2);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_G_LTD_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_G_LTD_DISPLAYDATE_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, BlogsEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, BlogsEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			if (bindDisplayDate) {
				queryPos.add(new Timestamp(displayDate.getTime()));
			}

			return (List<BlogsEntry>)QueryUtil.list(
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
	 * Returns the blogs entries before and after the current blogs entry in the ordered set of blogs entries that the user has permission to view where groupId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param entryId the primary key of the current blogs entry
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry[] filterFindByG_LtD_PrevAndNext(
			long entryId, long groupId, Date displayDate,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_LtD_PrevAndNext(
				entryId, groupId, displayDate, orderByComparator);
		}

		BlogsEntry blogsEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			BlogsEntry[] array = new BlogsEntryImpl[3];

			array[0] = filterGetByG_LtD_PrevAndNext(
				session, blogsEntry, groupId, displayDate, orderByComparator,
				true);

			array[1] = blogsEntry;

			array[2] = filterGetByG_LtD_PrevAndNext(
				session, blogsEntry, groupId, displayDate, orderByComparator,
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

	protected BlogsEntry filterGetByG_LtD_PrevAndNext(
		Session session, BlogsEntry blogsEntry, long groupId, Date displayDate,
		OrderByComparator<BlogsEntry> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_LTD_GROUPID_2);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_G_LTD_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_G_LTD_DISPLAYDATE_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, BlogsEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, BlogsEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (bindDisplayDate) {
			queryPos.add(new Timestamp(displayDate.getTime()));
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(blogsEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BlogsEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; and displayDate &lt; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 */
	@Override
	public void removeByG_LtD(long groupId, Date displayDate) {
		for (BlogsEntry blogsEntry :
				findByG_LtD(
					groupId, displayDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(blogsEntry);
		}
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByG_LtD(long groupId, Date displayDate) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByG_LtD;

			Object[] finderArgs = new Object[] {groupId, _getTime(displayDate)};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_LTD_GROUPID_2);

				boolean bindDisplayDate = false;

				if (displayDate == null) {
					sb.append(_FINDER_COLUMN_G_LTD_DISPLAYDATE_1);
				}
				else {
					bindDisplayDate = true;

					sb.append(_FINDER_COLUMN_G_LTD_DISPLAYDATE_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					if (bindDisplayDate) {
						queryPos.add(new Timestamp(displayDate.getTime()));
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
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_LtD(long groupId, Date displayDate) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_LtD(groupId, displayDate);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<BlogsEntry> blogsEntries = findByG_LtD(groupId, displayDate);

			blogsEntries = InlineSQLHelperUtil.filter(blogsEntries, groupId);

			return blogsEntries.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_BLOGSENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_LTD_GROUPID_2);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_G_LTD_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_G_LTD_DISPLAYDATE_2);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			if (bindDisplayDate) {
				queryPos.add(new Timestamp(displayDate.getTime()));
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

	private static final String _FINDER_COLUMN_G_LTD_GROUPID_2 =
		"blogsEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_LTD_DISPLAYDATE_1 =
		"blogsEntry.displayDate IS NULL";

	private static final String _FINDER_COLUMN_G_LTD_DISPLAYDATE_2 =
		"blogsEntry.displayDate < ?";

	private FinderPath _finderPathWithPaginationFindByG_S;
	private FinderPath _finderPathWithoutPaginationFindByG_S;
	private FinderPath _finderPathCountByG_S;

	/**
	 * Returns all the blogs entries where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_S(long groupId, int status) {
		return findByG_S(
			groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the blogs entries where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_S(
		long groupId, int status, int start, int end) {

		return findByG_S(groupId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return findByG_S(groupId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_S;
					finderArgs = new Object[] {groupId, status};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_S;
				finderArgs = new Object[] {
					groupId, status, start, end, orderByComparator
				};
			}

			List<BlogsEntry> list = null;

			if (useFinderCache) {
				list = (List<BlogsEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BlogsEntry blogsEntry : list) {
						if ((groupId != blogsEntry.getGroupId()) ||
							(status != blogsEntry.getStatus())) {

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

				sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(status);

					list = (List<BlogsEntry>)QueryUtil.list(
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
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByG_S_First(
			long groupId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByG_S_First(
			groupId, status, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_S_First(
		long groupId, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		List<BlogsEntry> list = findByG_S(
			groupId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last blogs entry in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByG_S_Last(
			long groupId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByG_S_Last(
			groupId, status, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the last blogs entry in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_S_Last(
		long groupId, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		int count = countByG_S(groupId, status);

		if (count == 0) {
			return null;
		}

		List<BlogsEntry> list = findByG_S(
			groupId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the blogs entries before and after the current blogs entry in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param entryId the primary key of the current blogs entry
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry[] findByG_S_PrevAndNext(
			long entryId, long groupId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			BlogsEntry[] array = new BlogsEntryImpl[3];

			array[0] = getByG_S_PrevAndNext(
				session, blogsEntry, groupId, status, orderByComparator, true);

			array[1] = blogsEntry;

			array[2] = getByG_S_PrevAndNext(
				session, blogsEntry, groupId, status, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected BlogsEntry getByG_S_PrevAndNext(
		Session session, BlogsEntry blogsEntry, long groupId, int status,
		OrderByComparator<BlogsEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_S_STATUS_2);

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
			sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(blogsEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BlogsEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the blogs entries that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_S(long groupId, int status) {
		return filterFindByG_S(
			groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the blogs entries that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_S(
		long groupId, int status, int start, int end) {

		return filterFindByG_S(groupId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_S(groupId, status, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_S(
					groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator),
				groupId);
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
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, BlogsEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, BlogsEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(status);

			return (List<BlogsEntry>)QueryUtil.list(
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
	 * Returns the blogs entries before and after the current blogs entry in the ordered set of blogs entries that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param entryId the primary key of the current blogs entry
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry[] filterFindByG_S_PrevAndNext(
			long entryId, long groupId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_S_PrevAndNext(
				entryId, groupId, status, orderByComparator);
		}

		BlogsEntry blogsEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			BlogsEntry[] array = new BlogsEntryImpl[3];

			array[0] = filterGetByG_S_PrevAndNext(
				session, blogsEntry, groupId, status, orderByComparator, true);

			array[1] = blogsEntry;

			array[2] = filterGetByG_S_PrevAndNext(
				session, blogsEntry, groupId, status, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected BlogsEntry filterGetByG_S_PrevAndNext(
		Session session, BlogsEntry blogsEntry, long groupId, int status,
		OrderByComparator<BlogsEntry> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, BlogsEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, BlogsEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(blogsEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BlogsEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 */
	@Override
	public void removeByG_S(long groupId, int status) {
		for (BlogsEntry blogsEntry :
				findByG_S(
					groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(blogsEntry);
		}
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByG_S(long groupId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = _finderPathCountByG_S;

			Object[] finderArgs = new Object[] {groupId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

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
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_S(long groupId, int status) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_S(groupId, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<BlogsEntry> blogsEntries = findByG_S(groupId, status);

			blogsEntries = InlineSQLHelperUtil.filter(blogsEntries, groupId);

			return blogsEntries.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_BLOGSENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

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

	private static final String _FINDER_COLUMN_G_S_GROUPID_2 =
		"blogsEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_S_STATUS_2 =
		"blogsEntry.status = ?";

	private FinderPath _finderPathWithPaginationFindByG_NotS;
	private FinderPath _finderPathWithPaginationCountByG_NotS;

	/**
	 * Returns all the blogs entries where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_NotS(long groupId, int status) {
		return findByG_NotS(
			groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the blogs entries where groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_NotS(
		long groupId, int status, int start, int end) {

		return findByG_NotS(groupId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_NotS(
		long groupId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return findByG_NotS(
			groupId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_NotS(
		long groupId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByG_NotS;
			finderArgs = new Object[] {
				groupId, status, start, end, orderByComparator
			};

			List<BlogsEntry> list = null;

			if (useFinderCache) {
				list = (List<BlogsEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BlogsEntry blogsEntry : list) {
						if ((groupId != blogsEntry.getGroupId()) ||
							(status == blogsEntry.getStatus())) {

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

				sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_NOTS_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_NOTS_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(status);

					list = (List<BlogsEntry>)QueryUtil.list(
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
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByG_NotS_First(
			long groupId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByG_NotS_First(
			groupId, status, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_NotS_First(
		long groupId, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		List<BlogsEntry> list = findByG_NotS(
			groupId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last blogs entry in the ordered set where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByG_NotS_Last(
			long groupId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByG_NotS_Last(
			groupId, status, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the last blogs entry in the ordered set where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_NotS_Last(
		long groupId, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		int count = countByG_NotS(groupId, status);

		if (count == 0) {
			return null;
		}

		List<BlogsEntry> list = findByG_NotS(
			groupId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the blogs entries before and after the current blogs entry in the ordered set where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param entryId the primary key of the current blogs entry
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry[] findByG_NotS_PrevAndNext(
			long entryId, long groupId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			BlogsEntry[] array = new BlogsEntryImpl[3];

			array[0] = getByG_NotS_PrevAndNext(
				session, blogsEntry, groupId, status, orderByComparator, true);

			array[1] = blogsEntry;

			array[2] = getByG_NotS_PrevAndNext(
				session, blogsEntry, groupId, status, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected BlogsEntry getByG_NotS_PrevAndNext(
		Session session, BlogsEntry blogsEntry, long groupId, int status,
		OrderByComparator<BlogsEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_NOTS_STATUS_2);

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
			sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(blogsEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BlogsEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the blogs entries that the user has permission to view where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_NotS(long groupId, int status) {
		return filterFindByG_NotS(
			groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the blogs entries that the user has permission to view where groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_NotS(
		long groupId, int status, int start, int end) {

		return filterFindByG_NotS(groupId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_NotS(
		long groupId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_NotS(groupId, status, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_NotS(
					groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator),
				groupId);
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
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_NOTS_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, BlogsEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, BlogsEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(status);

			return (List<BlogsEntry>)QueryUtil.list(
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
	 * Returns the blogs entries before and after the current blogs entry in the ordered set of blogs entries that the user has permission to view where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param entryId the primary key of the current blogs entry
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry[] filterFindByG_NotS_PrevAndNext(
			long entryId, long groupId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_NotS_PrevAndNext(
				entryId, groupId, status, orderByComparator);
		}

		BlogsEntry blogsEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			BlogsEntry[] array = new BlogsEntryImpl[3];

			array[0] = filterGetByG_NotS_PrevAndNext(
				session, blogsEntry, groupId, status, orderByComparator, true);

			array[1] = blogsEntry;

			array[2] = filterGetByG_NotS_PrevAndNext(
				session, blogsEntry, groupId, status, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected BlogsEntry filterGetByG_NotS_PrevAndNext(
		Session session, BlogsEntry blogsEntry, long groupId, int status,
		OrderByComparator<BlogsEntry> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_NOTS_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, BlogsEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, BlogsEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(blogsEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BlogsEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 */
	@Override
	public void removeByG_NotS(long groupId, int status) {
		for (BlogsEntry blogsEntry :
				findByG_NotS(
					groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(blogsEntry);
		}
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByG_NotS(long groupId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByG_NotS;

			Object[] finderArgs = new Object[] {groupId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_NOTS_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_NOTS_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

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
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_NotS(long groupId, int status) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_NotS(groupId, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<BlogsEntry> blogsEntries = findByG_NotS(groupId, status);

			blogsEntries = InlineSQLHelperUtil.filter(blogsEntries, groupId);

			return blogsEntries.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_BLOGSENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_NOTS_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

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

	private static final String _FINDER_COLUMN_G_NOTS_GROUPID_2 =
		"blogsEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_NOTS_STATUS_2 =
		"blogsEntry.status != ?";

	private FinderPath _finderPathWithPaginationFindByC_U;
	private FinderPath _finderPathWithoutPaginationFindByC_U;
	private FinderPath _finderPathCountByC_U;

	/**
	 * Returns all the blogs entries where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_U(long companyId, long userId) {
		return findByC_U(
			companyId, userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the blogs entries where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_U(
		long companyId, long userId, int start, int end) {

		return findByC_U(companyId, userId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return findByC_U(
			companyId, userId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

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

			List<BlogsEntry> list = null;

			if (useFinderCache) {
				list = (List<BlogsEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BlogsEntry blogsEntry : list) {
						if ((companyId != blogsEntry.getCompanyId()) ||
							(userId != blogsEntry.getUserId())) {

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

				sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_C_U_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_U_USERID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(userId);

					list = (List<BlogsEntry>)QueryUtil.list(
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
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByC_U_First(
			long companyId, long userId,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByC_U_First(
			companyId, userId, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByC_U_First(
		long companyId, long userId,
		OrderByComparator<BlogsEntry> orderByComparator) {

		List<BlogsEntry> list = findByC_U(
			companyId, userId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last blogs entry in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByC_U_Last(
			long companyId, long userId,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByC_U_Last(
			companyId, userId, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the last blogs entry in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByC_U_Last(
		long companyId, long userId,
		OrderByComparator<BlogsEntry> orderByComparator) {

		int count = countByC_U(companyId, userId);

		if (count == 0) {
			return null;
		}

		List<BlogsEntry> list = findByC_U(
			companyId, userId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the blogs entries before and after the current blogs entry in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param entryId the primary key of the current blogs entry
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry[] findByC_U_PrevAndNext(
			long entryId, long companyId, long userId,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			BlogsEntry[] array = new BlogsEntryImpl[3];

			array[0] = getByC_U_PrevAndNext(
				session, blogsEntry, companyId, userId, orderByComparator,
				true);

			array[1] = blogsEntry;

			array[2] = getByC_U_PrevAndNext(
				session, blogsEntry, companyId, userId, orderByComparator,
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

	protected BlogsEntry getByC_U_PrevAndNext(
		Session session, BlogsEntry blogsEntry, long companyId, long userId,
		OrderByComparator<BlogsEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

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
			sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(blogsEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BlogsEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the blogs entries where companyId = &#63; and userId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 */
	@Override
	public void removeByC_U(long companyId, long userId) {
		for (BlogsEntry blogsEntry :
				findByC_U(
					companyId, userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(blogsEntry);
		}
	}

	/**
	 * Returns the number of blogs entries where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByC_U(long companyId, long userId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = _finderPathCountByC_U;

			Object[] finderArgs = new Object[] {companyId, userId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_BLOGSENTRY_WHERE);

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
	}

	private static final String _FINDER_COLUMN_C_U_COMPANYID_2 =
		"blogsEntry.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_U_USERID_2 =
		"blogsEntry.userId = ?";

	private FinderPath _finderPathWithPaginationFindByC_LtD;
	private FinderPath _finderPathWithPaginationCountByC_LtD;

	/**
	 * Returns all the blogs entries where companyId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_LtD(long companyId, Date displayDate) {
		return findByC_LtD(
			companyId, displayDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the blogs entries where companyId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_LtD(
		long companyId, Date displayDate, int start, int end) {

		return findByC_LtD(companyId, displayDate, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_LtD(
		long companyId, Date displayDate, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return findByC_LtD(
			companyId, displayDate, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_LtD(
		long companyId, Date displayDate, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByC_LtD;
			finderArgs = new Object[] {
				companyId, _getTime(displayDate), start, end, orderByComparator
			};

			List<BlogsEntry> list = null;

			if (useFinderCache) {
				list = (List<BlogsEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BlogsEntry blogsEntry : list) {
						if ((companyId != blogsEntry.getCompanyId()) ||
							(displayDate.getTime() <= blogsEntry.getDisplayDate(
							).getTime())) {

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

				sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_C_LTD_COMPANYID_2);

				boolean bindDisplayDate = false;

				if (displayDate == null) {
					sb.append(_FINDER_COLUMN_C_LTD_DISPLAYDATE_1);
				}
				else {
					bindDisplayDate = true;

					sb.append(_FINDER_COLUMN_C_LTD_DISPLAYDATE_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					if (bindDisplayDate) {
						queryPos.add(new Timestamp(displayDate.getTime()));
					}

					list = (List<BlogsEntry>)QueryUtil.list(
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
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByC_LtD_First(
			long companyId, Date displayDate,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByC_LtD_First(
			companyId, displayDate, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", displayDate<");
		sb.append(displayDate);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByC_LtD_First(
		long companyId, Date displayDate,
		OrderByComparator<BlogsEntry> orderByComparator) {

		List<BlogsEntry> list = findByC_LtD(
			companyId, displayDate, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last blogs entry in the ordered set where companyId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByC_LtD_Last(
			long companyId, Date displayDate,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByC_LtD_Last(
			companyId, displayDate, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", displayDate<");
		sb.append(displayDate);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the last blogs entry in the ordered set where companyId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByC_LtD_Last(
		long companyId, Date displayDate,
		OrderByComparator<BlogsEntry> orderByComparator) {

		int count = countByC_LtD(companyId, displayDate);

		if (count == 0) {
			return null;
		}

		List<BlogsEntry> list = findByC_LtD(
			companyId, displayDate, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the blogs entries before and after the current blogs entry in the ordered set where companyId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param entryId the primary key of the current blogs entry
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry[] findByC_LtD_PrevAndNext(
			long entryId, long companyId, Date displayDate,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			BlogsEntry[] array = new BlogsEntryImpl[3];

			array[0] = getByC_LtD_PrevAndNext(
				session, blogsEntry, companyId, displayDate, orderByComparator,
				true);

			array[1] = blogsEntry;

			array[2] = getByC_LtD_PrevAndNext(
				session, blogsEntry, companyId, displayDate, orderByComparator,
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

	protected BlogsEntry getByC_LtD_PrevAndNext(
		Session session, BlogsEntry blogsEntry, long companyId,
		Date displayDate, OrderByComparator<BlogsEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

		sb.append(_FINDER_COLUMN_C_LTD_COMPANYID_2);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_C_LTD_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_C_LTD_DISPLAYDATE_2);
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
			sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (bindDisplayDate) {
			queryPos.add(new Timestamp(displayDate.getTime()));
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(blogsEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BlogsEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the blogs entries where companyId = &#63; and displayDate &lt; &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 */
	@Override
	public void removeByC_LtD(long companyId, Date displayDate) {
		for (BlogsEntry blogsEntry :
				findByC_LtD(
					companyId, displayDate, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(blogsEntry);
		}
	}

	/**
	 * Returns the number of blogs entries where companyId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByC_LtD(long companyId, Date displayDate) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByC_LtD;

			Object[] finderArgs = new Object[] {
				companyId, _getTime(displayDate)
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_C_LTD_COMPANYID_2);

				boolean bindDisplayDate = false;

				if (displayDate == null) {
					sb.append(_FINDER_COLUMN_C_LTD_DISPLAYDATE_1);
				}
				else {
					bindDisplayDate = true;

					sb.append(_FINDER_COLUMN_C_LTD_DISPLAYDATE_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					if (bindDisplayDate) {
						queryPos.add(new Timestamp(displayDate.getTime()));
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
	}

	private static final String _FINDER_COLUMN_C_LTD_COMPANYID_2 =
		"blogsEntry.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_LTD_DISPLAYDATE_1 =
		"blogsEntry.displayDate IS NULL";

	private static final String _FINDER_COLUMN_C_LTD_DISPLAYDATE_2 =
		"blogsEntry.displayDate < ?";

	private FinderPath _finderPathWithPaginationFindByC_S;
	private FinderPath _finderPathWithoutPaginationFindByC_S;
	private FinderPath _finderPathCountByC_S;

	/**
	 * Returns all the blogs entries where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_S(long companyId, int status) {
		return findByC_S(
			companyId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the blogs entries where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_S(
		long companyId, int status, int start, int end) {

		return findByC_S(companyId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_S(
		long companyId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return findByC_S(
			companyId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_S(
		long companyId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_S;
					finderArgs = new Object[] {companyId, status};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_S;
				finderArgs = new Object[] {
					companyId, status, start, end, orderByComparator
				};
			}

			List<BlogsEntry> list = null;

			if (useFinderCache) {
				list = (List<BlogsEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BlogsEntry blogsEntry : list) {
						if ((companyId != blogsEntry.getCompanyId()) ||
							(status != blogsEntry.getStatus())) {

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

				sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_C_S_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(status);

					list = (List<BlogsEntry>)QueryUtil.list(
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
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByC_S_First(
			long companyId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByC_S_First(
			companyId, status, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByC_S_First(
		long companyId, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		List<BlogsEntry> list = findByC_S(
			companyId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last blogs entry in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByC_S_Last(
			long companyId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByC_S_Last(
			companyId, status, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the last blogs entry in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByC_S_Last(
		long companyId, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		int count = countByC_S(companyId, status);

		if (count == 0) {
			return null;
		}

		List<BlogsEntry> list = findByC_S(
			companyId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the blogs entries before and after the current blogs entry in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param entryId the primary key of the current blogs entry
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry[] findByC_S_PrevAndNext(
			long entryId, long companyId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			BlogsEntry[] array = new BlogsEntryImpl[3];

			array[0] = getByC_S_PrevAndNext(
				session, blogsEntry, companyId, status, orderByComparator,
				true);

			array[1] = blogsEntry;

			array[2] = getByC_S_PrevAndNext(
				session, blogsEntry, companyId, status, orderByComparator,
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

	protected BlogsEntry getByC_S_PrevAndNext(
		Session session, BlogsEntry blogsEntry, long companyId, int status,
		OrderByComparator<BlogsEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

		sb.append(_FINDER_COLUMN_C_S_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_S_STATUS_2);

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
			sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(blogsEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BlogsEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the blogs entries where companyId = &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 */
	@Override
	public void removeByC_S(long companyId, int status) {
		for (BlogsEntry blogsEntry :
				findByC_S(
					companyId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(blogsEntry);
		}
	}

	/**
	 * Returns the number of blogs entries where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByC_S(long companyId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = _finderPathCountByC_S;

			Object[] finderArgs = new Object[] {companyId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_C_S_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

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
	}

	private static final String _FINDER_COLUMN_C_S_COMPANYID_2 =
		"blogsEntry.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_S_STATUS_2 =
		"blogsEntry.status = ?";

	private FinderPath _finderPathWithPaginationFindByC_NotS;
	private FinderPath _finderPathWithPaginationCountByC_NotS;

	/**
	 * Returns all the blogs entries where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_NotS(long companyId, int status) {
		return findByC_NotS(
			companyId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the blogs entries where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_NotS(
		long companyId, int status, int start, int end) {

		return findByC_NotS(companyId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_NotS(
		long companyId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return findByC_NotS(
			companyId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_NotS(
		long companyId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByC_NotS;
			finderArgs = new Object[] {
				companyId, status, start, end, orderByComparator
			};

			List<BlogsEntry> list = null;

			if (useFinderCache) {
				list = (List<BlogsEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BlogsEntry blogsEntry : list) {
						if ((companyId != blogsEntry.getCompanyId()) ||
							(status == blogsEntry.getStatus())) {

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

				sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_C_NOTS_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_NOTS_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(status);

					list = (List<BlogsEntry>)QueryUtil.list(
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
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByC_NotS_First(
			long companyId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByC_NotS_First(
			companyId, status, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByC_NotS_First(
		long companyId, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		List<BlogsEntry> list = findByC_NotS(
			companyId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last blogs entry in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByC_NotS_Last(
			long companyId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByC_NotS_Last(
			companyId, status, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the last blogs entry in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByC_NotS_Last(
		long companyId, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		int count = countByC_NotS(companyId, status);

		if (count == 0) {
			return null;
		}

		List<BlogsEntry> list = findByC_NotS(
			companyId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the blogs entries before and after the current blogs entry in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param entryId the primary key of the current blogs entry
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry[] findByC_NotS_PrevAndNext(
			long entryId, long companyId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			BlogsEntry[] array = new BlogsEntryImpl[3];

			array[0] = getByC_NotS_PrevAndNext(
				session, blogsEntry, companyId, status, orderByComparator,
				true);

			array[1] = blogsEntry;

			array[2] = getByC_NotS_PrevAndNext(
				session, blogsEntry, companyId, status, orderByComparator,
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

	protected BlogsEntry getByC_NotS_PrevAndNext(
		Session session, BlogsEntry blogsEntry, long companyId, int status,
		OrderByComparator<BlogsEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

		sb.append(_FINDER_COLUMN_C_NOTS_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_NOTS_STATUS_2);

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
			sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(blogsEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BlogsEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the blogs entries where companyId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 */
	@Override
	public void removeByC_NotS(long companyId, int status) {
		for (BlogsEntry blogsEntry :
				findByC_NotS(
					companyId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(blogsEntry);
		}
	}

	/**
	 * Returns the number of blogs entries where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByC_NotS(long companyId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByC_NotS;

			Object[] finderArgs = new Object[] {companyId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_C_NOTS_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_NOTS_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

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
	}

	private static final String _FINDER_COLUMN_C_NOTS_COMPANYID_2 =
		"blogsEntry.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_NOTS_STATUS_2 =
		"blogsEntry.status != ?";

	private FinderPath _finderPathWithPaginationFindByLtD_S;
	private FinderPath _finderPathWithPaginationCountByLtD_S;

	/**
	 * Returns all the blogs entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByLtD_S(Date displayDate, int status) {
		return findByLtD_S(
			displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the blogs entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByLtD_S(
		Date displayDate, int status, int start, int end) {

		return findByLtD_S(displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return findByLtD_S(
			displayDate, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByLtD_S;
			finderArgs = new Object[] {
				_getTime(displayDate), status, start, end, orderByComparator
			};

			List<BlogsEntry> list = null;

			if (useFinderCache) {
				list = (List<BlogsEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BlogsEntry blogsEntry : list) {
						if ((displayDate.getTime() <= blogsEntry.getDisplayDate(
							).getTime()) ||
							(status != blogsEntry.getStatus())) {

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

				sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

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
					sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
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

					list = (List<BlogsEntry>)QueryUtil.list(
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
	}

	/**
	 * Returns the first blogs entry in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByLtD_S_First(
			Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByLtD_S_First(
			displayDate, status, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("displayDate<");
		sb.append(displayDate);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first blogs entry in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByLtD_S_First(
		Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		List<BlogsEntry> list = findByLtD_S(
			displayDate, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last blogs entry in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByLtD_S_Last(
			Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByLtD_S_Last(
			displayDate, status, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("displayDate<");
		sb.append(displayDate);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the last blogs entry in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByLtD_S_Last(
		Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		int count = countByLtD_S(displayDate, status);

		if (count == 0) {
			return null;
		}

		List<BlogsEntry> list = findByLtD_S(
			displayDate, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the blogs entries before and after the current blogs entry in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param entryId the primary key of the current blogs entry
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry[] findByLtD_S_PrevAndNext(
			long entryId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			BlogsEntry[] array = new BlogsEntryImpl[3];

			array[0] = getByLtD_S_PrevAndNext(
				session, blogsEntry, displayDate, status, orderByComparator,
				true);

			array[1] = blogsEntry;

			array[2] = getByLtD_S_PrevAndNext(
				session, blogsEntry, displayDate, status, orderByComparator,
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

	protected BlogsEntry getByLtD_S_PrevAndNext(
		Session session, BlogsEntry blogsEntry, Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

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
			sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(blogsEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BlogsEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the blogs entries where displayDate &lt; &#63; and status = &#63; from the database.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 */
	@Override
	public void removeByLtD_S(Date displayDate, int status) {
		for (BlogsEntry blogsEntry :
				findByLtD_S(
					displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(blogsEntry);
		}
	}

	/**
	 * Returns the number of blogs entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByLtD_S(Date displayDate, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByLtD_S;

			Object[] finderArgs = new Object[] {_getTime(displayDate), status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_BLOGSENTRY_WHERE);

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
	}

	private static final String _FINDER_COLUMN_LTD_S_DISPLAYDATE_1 =
		"blogsEntry.displayDate IS NULL AND ";

	private static final String _FINDER_COLUMN_LTD_S_DISPLAYDATE_2 =
		"blogsEntry.displayDate < ? AND ";

	private static final String _FINDER_COLUMN_LTD_S_STATUS_2 =
		"blogsEntry.status = ?";

	private FinderPath _finderPathWithPaginationFindByG_U_LtD;
	private FinderPath _finderPathWithPaginationCountByG_U_LtD;

	/**
	 * Returns all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_LtD(
		long groupId, long userId, Date displayDate) {

		return findByG_U_LtD(
			groupId, userId, displayDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_LtD(
		long groupId, long userId, Date displayDate, int start, int end) {

		return findByG_U_LtD(groupId, userId, displayDate, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_LtD(
		long groupId, long userId, Date displayDate, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return findByG_U_LtD(
			groupId, userId, displayDate, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_LtD(
		long groupId, long userId, Date displayDate, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByG_U_LtD;
			finderArgs = new Object[] {
				groupId, userId, _getTime(displayDate), start, end,
				orderByComparator
			};

			List<BlogsEntry> list = null;

			if (useFinderCache) {
				list = (List<BlogsEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BlogsEntry blogsEntry : list) {
						if ((groupId != blogsEntry.getGroupId()) ||
							(userId != blogsEntry.getUserId()) ||
							(displayDate.getTime() <= blogsEntry.getDisplayDate(
							).getTime())) {

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

				sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_U_LTD_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_U_LTD_USERID_2);

				boolean bindDisplayDate = false;

				if (displayDate == null) {
					sb.append(_FINDER_COLUMN_G_U_LTD_DISPLAYDATE_1);
				}
				else {
					bindDisplayDate = true;

					sb.append(_FINDER_COLUMN_G_U_LTD_DISPLAYDATE_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(userId);

					if (bindDisplayDate) {
						queryPos.add(new Timestamp(displayDate.getTime()));
					}

					list = (List<BlogsEntry>)QueryUtil.list(
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
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and userId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByG_U_LtD_First(
			long groupId, long userId, Date displayDate,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByG_U_LtD_First(
			groupId, userId, displayDate, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append(", displayDate<");
		sb.append(displayDate);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and userId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_U_LtD_First(
		long groupId, long userId, Date displayDate,
		OrderByComparator<BlogsEntry> orderByComparator) {

		List<BlogsEntry> list = findByG_U_LtD(
			groupId, userId, displayDate, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last blogs entry in the ordered set where groupId = &#63; and userId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByG_U_LtD_Last(
			long groupId, long userId, Date displayDate,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByG_U_LtD_Last(
			groupId, userId, displayDate, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append(", displayDate<");
		sb.append(displayDate);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the last blogs entry in the ordered set where groupId = &#63; and userId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_U_LtD_Last(
		long groupId, long userId, Date displayDate,
		OrderByComparator<BlogsEntry> orderByComparator) {

		int count = countByG_U_LtD(groupId, userId, displayDate);

		if (count == 0) {
			return null;
		}

		List<BlogsEntry> list = findByG_U_LtD(
			groupId, userId, displayDate, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the blogs entries before and after the current blogs entry in the ordered set where groupId = &#63; and userId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param entryId the primary key of the current blogs entry
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry[] findByG_U_LtD_PrevAndNext(
			long entryId, long groupId, long userId, Date displayDate,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			BlogsEntry[] array = new BlogsEntryImpl[3];

			array[0] = getByG_U_LtD_PrevAndNext(
				session, blogsEntry, groupId, userId, displayDate,
				orderByComparator, true);

			array[1] = blogsEntry;

			array[2] = getByG_U_LtD_PrevAndNext(
				session, blogsEntry, groupId, userId, displayDate,
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

	protected BlogsEntry getByG_U_LtD_PrevAndNext(
		Session session, BlogsEntry blogsEntry, long groupId, long userId,
		Date displayDate, OrderByComparator<BlogsEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_U_LTD_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_LTD_USERID_2);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_G_U_LTD_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_G_U_LTD_DISPLAYDATE_2);
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
			sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(userId);

		if (bindDisplayDate) {
			queryPos.add(new Timestamp(displayDate.getTime()));
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(blogsEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BlogsEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @return the matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_U_LtD(
		long groupId, long userId, Date displayDate) {

		return filterFindByG_U_LtD(
			groupId, userId, displayDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_U_LtD(
		long groupId, long userId, Date displayDate, int start, int end) {

		return filterFindByG_U_LtD(
			groupId, userId, displayDate, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_U_LtD(
		long groupId, long userId, Date displayDate, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_U_LtD(
				groupId, userId, displayDate, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_U_LtD(
					groupId, userId, displayDate, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
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
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_LTD_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_LTD_USERID_2);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_G_U_LTD_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_G_U_LTD_DISPLAYDATE_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, BlogsEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, BlogsEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(userId);

			if (bindDisplayDate) {
				queryPos.add(new Timestamp(displayDate.getTime()));
			}

			return (List<BlogsEntry>)QueryUtil.list(
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
	 * Returns the blogs entries before and after the current blogs entry in the ordered set of blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param entryId the primary key of the current blogs entry
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry[] filterFindByG_U_LtD_PrevAndNext(
			long entryId, long groupId, long userId, Date displayDate,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_U_LtD_PrevAndNext(
				entryId, groupId, userId, displayDate, orderByComparator);
		}

		BlogsEntry blogsEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			BlogsEntry[] array = new BlogsEntryImpl[3];

			array[0] = filterGetByG_U_LtD_PrevAndNext(
				session, blogsEntry, groupId, userId, displayDate,
				orderByComparator, true);

			array[1] = blogsEntry;

			array[2] = filterGetByG_U_LtD_PrevAndNext(
				session, blogsEntry, groupId, userId, displayDate,
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

	protected BlogsEntry filterGetByG_U_LtD_PrevAndNext(
		Session session, BlogsEntry blogsEntry, long groupId, long userId,
		Date displayDate, OrderByComparator<BlogsEntry> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_LTD_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_LTD_USERID_2);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_G_U_LTD_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_G_U_LTD_DISPLAYDATE_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, BlogsEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, BlogsEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(userId);

		if (bindDisplayDate) {
			queryPos.add(new Timestamp(displayDate.getTime()));
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(blogsEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BlogsEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 */
	@Override
	public void removeByG_U_LtD(long groupId, long userId, Date displayDate) {
		for (BlogsEntry blogsEntry :
				findByG_U_LtD(
					groupId, userId, displayDate, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(blogsEntry);
		}
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByG_U_LtD(long groupId, long userId, Date displayDate) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByG_U_LtD;

			Object[] finderArgs = new Object[] {
				groupId, userId, _getTime(displayDate)
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_U_LTD_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_U_LTD_USERID_2);

				boolean bindDisplayDate = false;

				if (displayDate == null) {
					sb.append(_FINDER_COLUMN_G_U_LTD_DISPLAYDATE_1);
				}
				else {
					bindDisplayDate = true;

					sb.append(_FINDER_COLUMN_G_U_LTD_DISPLAYDATE_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(userId);

					if (bindDisplayDate) {
						queryPos.add(new Timestamp(displayDate.getTime()));
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
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_U_LtD(
		long groupId, long userId, Date displayDate) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_U_LtD(groupId, userId, displayDate);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<BlogsEntry> blogsEntries = findByG_U_LtD(
				groupId, userId, displayDate);

			blogsEntries = InlineSQLHelperUtil.filter(blogsEntries, groupId);

			return blogsEntries.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_BLOGSENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_U_LTD_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_LTD_USERID_2);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_G_U_LTD_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_G_U_LTD_DISPLAYDATE_2);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(userId);

			if (bindDisplayDate) {
				queryPos.add(new Timestamp(displayDate.getTime()));
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

	private static final String _FINDER_COLUMN_G_U_LTD_GROUPID_2 =
		"blogsEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_U_LTD_USERID_2 =
		"blogsEntry.userId = ? AND ";

	private static final String _FINDER_COLUMN_G_U_LTD_DISPLAYDATE_1 =
		"blogsEntry.displayDate IS NULL";

	private static final String _FINDER_COLUMN_G_U_LTD_DISPLAYDATE_2 =
		"blogsEntry.displayDate < ?";

	private FinderPath _finderPathWithPaginationFindByG_U_S;
	private FinderPath _finderPathWithoutPaginationFindByG_U_S;
	private FinderPath _finderPathCountByG_U_S;
	private FinderPath _finderPathWithPaginationCountByG_U_S;

	/**
	 * Returns all the blogs entries where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_S(long groupId, long userId, int status) {
		return findByG_U_S(
			groupId, userId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the blogs entries where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_S(
		long groupId, long userId, int status, int start, int end) {

		return findByG_U_S(groupId, userId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_S(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return findByG_U_S(
			groupId, userId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_S(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_U_S;
					finderArgs = new Object[] {groupId, userId, status};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_U_S;
				finderArgs = new Object[] {
					groupId, userId, status, start, end, orderByComparator
				};
			}

			List<BlogsEntry> list = null;

			if (useFinderCache) {
				list = (List<BlogsEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BlogsEntry blogsEntry : list) {
						if ((groupId != blogsEntry.getGroupId()) ||
							(userId != blogsEntry.getUserId()) ||
							(status != blogsEntry.getStatus())) {

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

				sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_U_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_U_S_USERID_2);

				sb.append(_FINDER_COLUMN_G_U_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(userId);

					queryPos.add(status);

					list = (List<BlogsEntry>)QueryUtil.list(
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
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByG_U_S_First(
			long groupId, long userId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByG_U_S_First(
			groupId, userId, status, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_U_S_First(
		long groupId, long userId, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		List<BlogsEntry> list = findByG_U_S(
			groupId, userId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last blogs entry in the ordered set where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByG_U_S_Last(
			long groupId, long userId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByG_U_S_Last(
			groupId, userId, status, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the last blogs entry in the ordered set where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_U_S_Last(
		long groupId, long userId, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		int count = countByG_U_S(groupId, userId, status);

		if (count == 0) {
			return null;
		}

		List<BlogsEntry> list = findByG_U_S(
			groupId, userId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the blogs entries before and after the current blogs entry in the ordered set where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param entryId the primary key of the current blogs entry
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry[] findByG_U_S_PrevAndNext(
			long entryId, long groupId, long userId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			BlogsEntry[] array = new BlogsEntryImpl[3];

			array[0] = getByG_U_S_PrevAndNext(
				session, blogsEntry, groupId, userId, status, orderByComparator,
				true);

			array[1] = blogsEntry;

			array[2] = getByG_U_S_PrevAndNext(
				session, blogsEntry, groupId, userId, status, orderByComparator,
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

	protected BlogsEntry getByG_U_S_PrevAndNext(
		Session session, BlogsEntry blogsEntry, long groupId, long userId,
		int status, OrderByComparator<BlogsEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_U_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_S_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_S_STATUS_2);

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
			sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(userId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(blogsEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BlogsEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_U_S(
		long groupId, long userId, int status) {

		return filterFindByG_U_S(
			groupId, userId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_U_S(
		long groupId, long userId, int status, int start, int end) {

		return filterFindByG_U_S(groupId, userId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_U_S(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_U_S(
				groupId, userId, status, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_U_S(
					groupId, userId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
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
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_S_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, BlogsEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, BlogsEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(userId);

			queryPos.add(status);

			return (List<BlogsEntry>)QueryUtil.list(
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
	 * Returns the blogs entries before and after the current blogs entry in the ordered set of blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param entryId the primary key of the current blogs entry
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry[] filterFindByG_U_S_PrevAndNext(
			long entryId, long groupId, long userId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_U_S_PrevAndNext(
				entryId, groupId, userId, status, orderByComparator);
		}

		BlogsEntry blogsEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			BlogsEntry[] array = new BlogsEntryImpl[3];

			array[0] = filterGetByG_U_S_PrevAndNext(
				session, blogsEntry, groupId, userId, status, orderByComparator,
				true);

			array[1] = blogsEntry;

			array[2] = filterGetByG_U_S_PrevAndNext(
				session, blogsEntry, groupId, userId, status, orderByComparator,
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

	protected BlogsEntry filterGetByG_U_S_PrevAndNext(
		Session session, BlogsEntry blogsEntry, long groupId, long userId,
		int status, OrderByComparator<BlogsEntry> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_S_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, BlogsEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, BlogsEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(userId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(blogsEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BlogsEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @return the matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_U_S(
		long groupId, long userId, int[] statuses) {

		return filterFindByG_U_S(
			groupId, userId, statuses, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_U_S(
		long groupId, long userId, int[] statuses, int start, int end) {

		return filterFindByG_U_S(groupId, userId, statuses, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_U_S(
		long groupId, long userId, int[] statuses, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_U_S(
				groupId, userId, statuses, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_U_S(
					groupId, userId, statuses, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		if (statuses == null) {
			statuses = new int[0];
		}
		else if (statuses.length > 1) {
			statuses = ArrayUtil.sortedUnique(statuses);
		}

		StringBundler sb = new StringBundler();

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_S_USERID_2);

		if (statuses.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_U_S_STATUS_7);

			sb.append(StringUtil.merge(statuses));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, BlogsEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, BlogsEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(userId);

			return (List<BlogsEntry>)QueryUtil.list(
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
	 * Returns all the blogs entries where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_S(
		long groupId, long userId, int[] statuses) {

		return findByG_U_S(
			groupId, userId, statuses, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the blogs entries where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_S(
		long groupId, long userId, int[] statuses, int start, int end) {

		return findByG_U_S(groupId, userId, statuses, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_S(
		long groupId, long userId, int[] statuses, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return findByG_U_S(
			groupId, userId, statuses, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and userId = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_S(
		long groupId, long userId, int[] statuses, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		if (statuses == null) {
			statuses = new int[0];
		}
		else if (statuses.length > 1) {
			statuses = ArrayUtil.sortedUnique(statuses);
		}

		if (statuses.length == 1) {
			return findByG_U_S(
				groupId, userId, statuses[0], start, end, orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						groupId, userId, StringUtil.merge(statuses)
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					groupId, userId, StringUtil.merge(statuses), start, end,
					orderByComparator
				};
			}

			List<BlogsEntry> list = null;

			if (useFinderCache) {
				list = (List<BlogsEntry>)finderCache.getResult(
					_finderPathWithPaginationFindByG_U_S, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BlogsEntry blogsEntry : list) {
						if ((groupId != blogsEntry.getGroupId()) ||
							(userId != blogsEntry.getUserId()) ||
							!ArrayUtil.contains(
								statuses, blogsEntry.getStatus())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_U_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_U_S_USERID_2);

				if (statuses.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_U_S_STATUS_7);

					sb.append(StringUtil.merge(statuses));

					sb.append(")");

					sb.append(")");
				}

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(userId);

					list = (List<BlogsEntry>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(
							_finderPathWithPaginationFindByG_U_S, finderArgs,
							list);
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
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; and userId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 */
	@Override
	public void removeByG_U_S(long groupId, long userId, int status) {
		for (BlogsEntry blogsEntry :
				findByG_U_S(
					groupId, userId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(blogsEntry);
		}
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByG_U_S(long groupId, long userId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = _finderPathCountByG_U_S;

			Object[] finderArgs = new Object[] {groupId, userId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_U_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_U_S_USERID_2);

				sb.append(_FINDER_COLUMN_G_U_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(userId);

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
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByG_U_S(long groupId, long userId, int[] statuses) {
		if (statuses == null) {
			statuses = new int[0];
		}
		else if (statuses.length > 1) {
			statuses = ArrayUtil.sortedUnique(statuses);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			Object[] finderArgs = new Object[] {
				groupId, userId, StringUtil.merge(statuses)
			};

			Long count = (Long)finderCache.getResult(
				_finderPathWithPaginationCountByG_U_S, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_U_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_U_S_USERID_2);

				if (statuses.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_U_S_STATUS_7);

					sb.append(StringUtil.merge(statuses));

					sb.append(")");

					sb.append(")");
				}

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(userId);

					count = (Long)query.uniqueResult();

					finderCache.putResult(
						_finderPathWithPaginationCountByG_U_S, finderArgs,
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
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_U_S(long groupId, long userId, int status) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_U_S(groupId, userId, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<BlogsEntry> blogsEntries = findByG_U_S(
				groupId, userId, status);

			blogsEntries = InlineSQLHelperUtil.filter(blogsEntries, groupId);

			return blogsEntries.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_BLOGSENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_U_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_S_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(userId);

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

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_U_S(long groupId, long userId, int[] statuses) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_U_S(groupId, userId, statuses);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<BlogsEntry> blogsEntries = InlineSQLHelperUtil.filter(
				findByG_U_S(groupId, userId, statuses), groupId);

			return blogsEntries.size();
		}

		if (statuses == null) {
			statuses = new int[0];
		}
		else if (statuses.length > 1) {
			statuses = ArrayUtil.sortedUnique(statuses);
		}

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_BLOGSENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_U_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_S_USERID_2);

		if (statuses.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_U_S_STATUS_7);

			sb.append(StringUtil.merge(statuses));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(userId);

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

	private static final String _FINDER_COLUMN_G_U_S_GROUPID_2 =
		"blogsEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_U_S_USERID_2 =
		"blogsEntry.userId = ? AND ";

	private static final String _FINDER_COLUMN_G_U_S_STATUS_2 =
		"blogsEntry.status = ?";

	private static final String _FINDER_COLUMN_G_U_S_STATUS_7 =
		"blogsEntry.status IN (";

	private FinderPath _finderPathWithPaginationFindByG_U_NotS;
	private FinderPath _finderPathWithPaginationCountByG_U_NotS;

	/**
	 * Returns all the blogs entries where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_NotS(
		long groupId, long userId, int status) {

		return findByG_U_NotS(
			groupId, userId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the blogs entries where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_NotS(
		long groupId, long userId, int status, int start, int end) {

		return findByG_U_NotS(groupId, userId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_NotS(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return findByG_U_NotS(
			groupId, userId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_NotS(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByG_U_NotS;
			finderArgs = new Object[] {
				groupId, userId, status, start, end, orderByComparator
			};

			List<BlogsEntry> list = null;

			if (useFinderCache) {
				list = (List<BlogsEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BlogsEntry blogsEntry : list) {
						if ((groupId != blogsEntry.getGroupId()) ||
							(userId != blogsEntry.getUserId()) ||
							(status == blogsEntry.getStatus())) {

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

				sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_U_NOTS_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_U_NOTS_USERID_2);

				sb.append(_FINDER_COLUMN_G_U_NOTS_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(userId);

					queryPos.add(status);

					list = (List<BlogsEntry>)QueryUtil.list(
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
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByG_U_NotS_First(
			long groupId, long userId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByG_U_NotS_First(
			groupId, userId, status, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_U_NotS_First(
		long groupId, long userId, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		List<BlogsEntry> list = findByG_U_NotS(
			groupId, userId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last blogs entry in the ordered set where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByG_U_NotS_Last(
			long groupId, long userId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByG_U_NotS_Last(
			groupId, userId, status, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the last blogs entry in the ordered set where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_U_NotS_Last(
		long groupId, long userId, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		int count = countByG_U_NotS(groupId, userId, status);

		if (count == 0) {
			return null;
		}

		List<BlogsEntry> list = findByG_U_NotS(
			groupId, userId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the blogs entries before and after the current blogs entry in the ordered set where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param entryId the primary key of the current blogs entry
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry[] findByG_U_NotS_PrevAndNext(
			long entryId, long groupId, long userId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			BlogsEntry[] array = new BlogsEntryImpl[3];

			array[0] = getByG_U_NotS_PrevAndNext(
				session, blogsEntry, groupId, userId, status, orderByComparator,
				true);

			array[1] = blogsEntry;

			array[2] = getByG_U_NotS_PrevAndNext(
				session, blogsEntry, groupId, userId, status, orderByComparator,
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

	protected BlogsEntry getByG_U_NotS_PrevAndNext(
		Session session, BlogsEntry blogsEntry, long groupId, long userId,
		int status, OrderByComparator<BlogsEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_U_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_NOTS_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_NOTS_STATUS_2);

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
			sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(userId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(blogsEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BlogsEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_U_NotS(
		long groupId, long userId, int status) {

		return filterFindByG_U_NotS(
			groupId, userId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_U_NotS(
		long groupId, long userId, int status, int start, int end) {

		return filterFindByG_U_NotS(groupId, userId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_U_NotS(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_U_NotS(
				groupId, userId, status, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_U_NotS(
					groupId, userId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
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
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_NOTS_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_NOTS_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, BlogsEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, BlogsEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(userId);

			queryPos.add(status);

			return (List<BlogsEntry>)QueryUtil.list(
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
	 * Returns the blogs entries before and after the current blogs entry in the ordered set of blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param entryId the primary key of the current blogs entry
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry[] filterFindByG_U_NotS_PrevAndNext(
			long entryId, long groupId, long userId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_U_NotS_PrevAndNext(
				entryId, groupId, userId, status, orderByComparator);
		}

		BlogsEntry blogsEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			BlogsEntry[] array = new BlogsEntryImpl[3];

			array[0] = filterGetByG_U_NotS_PrevAndNext(
				session, blogsEntry, groupId, userId, status, orderByComparator,
				true);

			array[1] = blogsEntry;

			array[2] = filterGetByG_U_NotS_PrevAndNext(
				session, blogsEntry, groupId, userId, status, orderByComparator,
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

	protected BlogsEntry filterGetByG_U_NotS_PrevAndNext(
		Session session, BlogsEntry blogsEntry, long groupId, long userId,
		int status, OrderByComparator<BlogsEntry> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_NOTS_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_NOTS_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, BlogsEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, BlogsEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(userId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(blogsEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BlogsEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; and userId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 */
	@Override
	public void removeByG_U_NotS(long groupId, long userId, int status) {
		for (BlogsEntry blogsEntry :
				findByG_U_NotS(
					groupId, userId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(blogsEntry);
		}
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByG_U_NotS(long groupId, long userId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByG_U_NotS;

			Object[] finderArgs = new Object[] {groupId, userId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_U_NOTS_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_U_NOTS_USERID_2);

				sb.append(_FINDER_COLUMN_G_U_NOTS_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(userId);

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
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_U_NotS(long groupId, long userId, int status) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_U_NotS(groupId, userId, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<BlogsEntry> blogsEntries = findByG_U_NotS(
				groupId, userId, status);

			blogsEntries = InlineSQLHelperUtil.filter(blogsEntries, groupId);

			return blogsEntries.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_BLOGSENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_U_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_NOTS_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_NOTS_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(userId);

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

	private static final String _FINDER_COLUMN_G_U_NOTS_GROUPID_2 =
		"blogsEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_U_NOTS_USERID_2 =
		"blogsEntry.userId = ? AND ";

	private static final String _FINDER_COLUMN_G_U_NOTS_STATUS_2 =
		"blogsEntry.status != ?";

	private FinderPath _finderPathWithPaginationFindByG_D_S;
	private FinderPath _finderPathWithoutPaginationFindByG_D_S;
	private FinderPath _finderPathCountByG_D_S;

	/**
	 * Returns all the blogs entries where groupId = &#63; and displayDate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_D_S(
		long groupId, Date displayDate, int status) {

		return findByG_D_S(
			groupId, displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the blogs entries where groupId = &#63; and displayDate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_D_S(
		long groupId, Date displayDate, int status, int start, int end) {

		return findByG_D_S(groupId, displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and displayDate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_D_S(
		long groupId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return findByG_D_S(
			groupId, displayDate, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and displayDate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_D_S(
		long groupId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_D_S;
					finderArgs = new Object[] {
						groupId, _getTime(displayDate), status
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_D_S;
				finderArgs = new Object[] {
					groupId, _getTime(displayDate), status, start, end,
					orderByComparator
				};
			}

			List<BlogsEntry> list = null;

			if (useFinderCache) {
				list = (List<BlogsEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BlogsEntry blogsEntry : list) {
						if ((groupId != blogsEntry.getGroupId()) ||
							!Objects.equals(
								displayDate, blogsEntry.getDisplayDate()) ||
							(status != blogsEntry.getStatus())) {

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

				sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_D_S_GROUPID_2);

				boolean bindDisplayDate = false;

				if (displayDate == null) {
					sb.append(_FINDER_COLUMN_G_D_S_DISPLAYDATE_1);
				}
				else {
					bindDisplayDate = true;

					sb.append(_FINDER_COLUMN_G_D_S_DISPLAYDATE_2);
				}

				sb.append(_FINDER_COLUMN_G_D_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					if (bindDisplayDate) {
						queryPos.add(new Timestamp(displayDate.getTime()));
					}

					queryPos.add(status);

					list = (List<BlogsEntry>)QueryUtil.list(
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
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and displayDate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByG_D_S_First(
			long groupId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByG_D_S_First(
			groupId, displayDate, status, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", displayDate=");
		sb.append(displayDate);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and displayDate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_D_S_First(
		long groupId, Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		List<BlogsEntry> list = findByG_D_S(
			groupId, displayDate, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last blogs entry in the ordered set where groupId = &#63; and displayDate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByG_D_S_Last(
			long groupId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByG_D_S_Last(
			groupId, displayDate, status, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", displayDate=");
		sb.append(displayDate);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the last blogs entry in the ordered set where groupId = &#63; and displayDate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_D_S_Last(
		long groupId, Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		int count = countByG_D_S(groupId, displayDate, status);

		if (count == 0) {
			return null;
		}

		List<BlogsEntry> list = findByG_D_S(
			groupId, displayDate, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the blogs entries before and after the current blogs entry in the ordered set where groupId = &#63; and displayDate = &#63; and status = &#63;.
	 *
	 * @param entryId the primary key of the current blogs entry
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry[] findByG_D_S_PrevAndNext(
			long entryId, long groupId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			BlogsEntry[] array = new BlogsEntryImpl[3];

			array[0] = getByG_D_S_PrevAndNext(
				session, blogsEntry, groupId, displayDate, status,
				orderByComparator, true);

			array[1] = blogsEntry;

			array[2] = getByG_D_S_PrevAndNext(
				session, blogsEntry, groupId, displayDate, status,
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

	protected BlogsEntry getByG_D_S_PrevAndNext(
		Session session, BlogsEntry blogsEntry, long groupId, Date displayDate,
		int status, OrderByComparator<BlogsEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_D_S_GROUPID_2);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_G_D_S_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_G_D_S_DISPLAYDATE_2);
		}

		sb.append(_FINDER_COLUMN_G_D_S_STATUS_2);

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
			sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		if (bindDisplayDate) {
			queryPos.add(new Timestamp(displayDate.getTime()));
		}

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(blogsEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BlogsEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the blogs entries that the user has permission to view where groupId = &#63; and displayDate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_D_S(
		long groupId, Date displayDate, int status) {

		return filterFindByG_D_S(
			groupId, displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the blogs entries that the user has permission to view where groupId = &#63; and displayDate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_D_S(
		long groupId, Date displayDate, int status, int start, int end) {

		return filterFindByG_D_S(
			groupId, displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63; and displayDate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_D_S(
		long groupId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_D_S(
				groupId, displayDate, status, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_D_S(
					groupId, displayDate, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
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
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_D_S_GROUPID_2);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_G_D_S_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_G_D_S_DISPLAYDATE_2);
		}

		sb.append(_FINDER_COLUMN_G_D_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, BlogsEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, BlogsEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			if (bindDisplayDate) {
				queryPos.add(new Timestamp(displayDate.getTime()));
			}

			queryPos.add(status);

			return (List<BlogsEntry>)QueryUtil.list(
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
	 * Returns the blogs entries before and after the current blogs entry in the ordered set of blogs entries that the user has permission to view where groupId = &#63; and displayDate = &#63; and status = &#63;.
	 *
	 * @param entryId the primary key of the current blogs entry
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry[] filterFindByG_D_S_PrevAndNext(
			long entryId, long groupId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_D_S_PrevAndNext(
				entryId, groupId, displayDate, status, orderByComparator);
		}

		BlogsEntry blogsEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			BlogsEntry[] array = new BlogsEntryImpl[3];

			array[0] = filterGetByG_D_S_PrevAndNext(
				session, blogsEntry, groupId, displayDate, status,
				orderByComparator, true);

			array[1] = blogsEntry;

			array[2] = filterGetByG_D_S_PrevAndNext(
				session, blogsEntry, groupId, displayDate, status,
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

	protected BlogsEntry filterGetByG_D_S_PrevAndNext(
		Session session, BlogsEntry blogsEntry, long groupId, Date displayDate,
		int status, OrderByComparator<BlogsEntry> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_D_S_GROUPID_2);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_G_D_S_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_G_D_S_DISPLAYDATE_2);
		}

		sb.append(_FINDER_COLUMN_G_D_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, BlogsEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, BlogsEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (bindDisplayDate) {
			queryPos.add(new Timestamp(displayDate.getTime()));
		}

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(blogsEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BlogsEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; and displayDate = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 */
	@Override
	public void removeByG_D_S(long groupId, Date displayDate, int status) {
		for (BlogsEntry blogsEntry :
				findByG_D_S(
					groupId, displayDate, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(blogsEntry);
		}
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and displayDate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByG_D_S(long groupId, Date displayDate, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = _finderPathCountByG_D_S;

			Object[] finderArgs = new Object[] {
				groupId, _getTime(displayDate), status
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_D_S_GROUPID_2);

				boolean bindDisplayDate = false;

				if (displayDate == null) {
					sb.append(_FINDER_COLUMN_G_D_S_DISPLAYDATE_1);
				}
				else {
					bindDisplayDate = true;

					sb.append(_FINDER_COLUMN_G_D_S_DISPLAYDATE_2);
				}

				sb.append(_FINDER_COLUMN_G_D_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

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
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and displayDate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_D_S(long groupId, Date displayDate, int status) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_D_S(groupId, displayDate, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<BlogsEntry> blogsEntries = findByG_D_S(
				groupId, displayDate, status);

			blogsEntries = InlineSQLHelperUtil.filter(blogsEntries, groupId);

			return blogsEntries.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_BLOGSENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_D_S_GROUPID_2);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_G_D_S_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_G_D_S_DISPLAYDATE_2);
		}

		sb.append(_FINDER_COLUMN_G_D_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

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

	private static final String _FINDER_COLUMN_G_D_S_GROUPID_2 =
		"blogsEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_D_S_DISPLAYDATE_1 =
		"blogsEntry.displayDate IS NULL AND ";

	private static final String _FINDER_COLUMN_G_D_S_DISPLAYDATE_2 =
		"blogsEntry.displayDate = ? AND ";

	private static final String _FINDER_COLUMN_G_D_S_STATUS_2 =
		"blogsEntry.status = ?";

	private FinderPath _finderPathWithPaginationFindByG_GtD_S;
	private FinderPath _finderPathWithPaginationCountByG_GtD_S;

	/**
	 * Returns all the blogs entries where groupId = &#63; and displayDate &gt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_GtD_S(
		long groupId, Date displayDate, int status) {

		return findByG_GtD_S(
			groupId, displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the blogs entries where groupId = &#63; and displayDate &gt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_GtD_S(
		long groupId, Date displayDate, int status, int start, int end) {

		return findByG_GtD_S(groupId, displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and displayDate &gt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_GtD_S(
		long groupId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return findByG_GtD_S(
			groupId, displayDate, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and displayDate &gt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_GtD_S(
		long groupId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByG_GtD_S;
			finderArgs = new Object[] {
				groupId, _getTime(displayDate), status, start, end,
				orderByComparator
			};

			List<BlogsEntry> list = null;

			if (useFinderCache) {
				list = (List<BlogsEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BlogsEntry blogsEntry : list) {
						if ((groupId != blogsEntry.getGroupId()) ||
							(displayDate.getTime() >= blogsEntry.getDisplayDate(
							).getTime()) ||
							(status != blogsEntry.getStatus())) {

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

				sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_GTD_S_GROUPID_2);

				boolean bindDisplayDate = false;

				if (displayDate == null) {
					sb.append(_FINDER_COLUMN_G_GTD_S_DISPLAYDATE_1);
				}
				else {
					bindDisplayDate = true;

					sb.append(_FINDER_COLUMN_G_GTD_S_DISPLAYDATE_2);
				}

				sb.append(_FINDER_COLUMN_G_GTD_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					if (bindDisplayDate) {
						queryPos.add(new Timestamp(displayDate.getTime()));
					}

					queryPos.add(status);

					list = (List<BlogsEntry>)QueryUtil.list(
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
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and displayDate &gt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByG_GtD_S_First(
			long groupId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByG_GtD_S_First(
			groupId, displayDate, status, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", displayDate>");
		sb.append(displayDate);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and displayDate &gt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_GtD_S_First(
		long groupId, Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		List<BlogsEntry> list = findByG_GtD_S(
			groupId, displayDate, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last blogs entry in the ordered set where groupId = &#63; and displayDate &gt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByG_GtD_S_Last(
			long groupId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByG_GtD_S_Last(
			groupId, displayDate, status, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", displayDate>");
		sb.append(displayDate);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the last blogs entry in the ordered set where groupId = &#63; and displayDate &gt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_GtD_S_Last(
		long groupId, Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		int count = countByG_GtD_S(groupId, displayDate, status);

		if (count == 0) {
			return null;
		}

		List<BlogsEntry> list = findByG_GtD_S(
			groupId, displayDate, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the blogs entries before and after the current blogs entry in the ordered set where groupId = &#63; and displayDate &gt; &#63; and status = &#63;.
	 *
	 * @param entryId the primary key of the current blogs entry
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry[] findByG_GtD_S_PrevAndNext(
			long entryId, long groupId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			BlogsEntry[] array = new BlogsEntryImpl[3];

			array[0] = getByG_GtD_S_PrevAndNext(
				session, blogsEntry, groupId, displayDate, status,
				orderByComparator, true);

			array[1] = blogsEntry;

			array[2] = getByG_GtD_S_PrevAndNext(
				session, blogsEntry, groupId, displayDate, status,
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

	protected BlogsEntry getByG_GtD_S_PrevAndNext(
		Session session, BlogsEntry blogsEntry, long groupId, Date displayDate,
		int status, OrderByComparator<BlogsEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_GTD_S_GROUPID_2);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_G_GTD_S_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_G_GTD_S_DISPLAYDATE_2);
		}

		sb.append(_FINDER_COLUMN_G_GTD_S_STATUS_2);

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
			sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		if (bindDisplayDate) {
			queryPos.add(new Timestamp(displayDate.getTime()));
		}

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(blogsEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BlogsEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the blogs entries that the user has permission to view where groupId = &#63; and displayDate &gt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_GtD_S(
		long groupId, Date displayDate, int status) {

		return filterFindByG_GtD_S(
			groupId, displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the blogs entries that the user has permission to view where groupId = &#63; and displayDate &gt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_GtD_S(
		long groupId, Date displayDate, int status, int start, int end) {

		return filterFindByG_GtD_S(
			groupId, displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63; and displayDate &gt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_GtD_S(
		long groupId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_GtD_S(
				groupId, displayDate, status, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_GtD_S(
					groupId, displayDate, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
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
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_GTD_S_GROUPID_2);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_G_GTD_S_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_G_GTD_S_DISPLAYDATE_2);
		}

		sb.append(_FINDER_COLUMN_G_GTD_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, BlogsEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, BlogsEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			if (bindDisplayDate) {
				queryPos.add(new Timestamp(displayDate.getTime()));
			}

			queryPos.add(status);

			return (List<BlogsEntry>)QueryUtil.list(
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
	 * Returns the blogs entries before and after the current blogs entry in the ordered set of blogs entries that the user has permission to view where groupId = &#63; and displayDate &gt; &#63; and status = &#63;.
	 *
	 * @param entryId the primary key of the current blogs entry
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry[] filterFindByG_GtD_S_PrevAndNext(
			long entryId, long groupId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_GtD_S_PrevAndNext(
				entryId, groupId, displayDate, status, orderByComparator);
		}

		BlogsEntry blogsEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			BlogsEntry[] array = new BlogsEntryImpl[3];

			array[0] = filterGetByG_GtD_S_PrevAndNext(
				session, blogsEntry, groupId, displayDate, status,
				orderByComparator, true);

			array[1] = blogsEntry;

			array[2] = filterGetByG_GtD_S_PrevAndNext(
				session, blogsEntry, groupId, displayDate, status,
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

	protected BlogsEntry filterGetByG_GtD_S_PrevAndNext(
		Session session, BlogsEntry blogsEntry, long groupId, Date displayDate,
		int status, OrderByComparator<BlogsEntry> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_GTD_S_GROUPID_2);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_G_GTD_S_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_G_GTD_S_DISPLAYDATE_2);
		}

		sb.append(_FINDER_COLUMN_G_GTD_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, BlogsEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, BlogsEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (bindDisplayDate) {
			queryPos.add(new Timestamp(displayDate.getTime()));
		}

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(blogsEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BlogsEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; and displayDate &gt; &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 */
	@Override
	public void removeByG_GtD_S(long groupId, Date displayDate, int status) {
		for (BlogsEntry blogsEntry :
				findByG_GtD_S(
					groupId, displayDate, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(blogsEntry);
		}
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and displayDate &gt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByG_GtD_S(long groupId, Date displayDate, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByG_GtD_S;

			Object[] finderArgs = new Object[] {
				groupId, _getTime(displayDate), status
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_GTD_S_GROUPID_2);

				boolean bindDisplayDate = false;

				if (displayDate == null) {
					sb.append(_FINDER_COLUMN_G_GTD_S_DISPLAYDATE_1);
				}
				else {
					bindDisplayDate = true;

					sb.append(_FINDER_COLUMN_G_GTD_S_DISPLAYDATE_2);
				}

				sb.append(_FINDER_COLUMN_G_GTD_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

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
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and displayDate &gt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_GtD_S(
		long groupId, Date displayDate, int status) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_GtD_S(groupId, displayDate, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<BlogsEntry> blogsEntries = findByG_GtD_S(
				groupId, displayDate, status);

			blogsEntries = InlineSQLHelperUtil.filter(blogsEntries, groupId);

			return blogsEntries.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_BLOGSENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_GTD_S_GROUPID_2);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_G_GTD_S_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_G_GTD_S_DISPLAYDATE_2);
		}

		sb.append(_FINDER_COLUMN_G_GTD_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

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

	private static final String _FINDER_COLUMN_G_GTD_S_GROUPID_2 =
		"blogsEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_GTD_S_DISPLAYDATE_1 =
		"blogsEntry.displayDate IS NULL AND ";

	private static final String _FINDER_COLUMN_G_GTD_S_DISPLAYDATE_2 =
		"blogsEntry.displayDate > ? AND ";

	private static final String _FINDER_COLUMN_G_GTD_S_STATUS_2 =
		"blogsEntry.status = ?";

	private FinderPath _finderPathWithPaginationFindByG_LtD_S;
	private FinderPath _finderPathWithPaginationCountByG_LtD_S;

	/**
	 * Returns all the blogs entries where groupId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_LtD_S(
		long groupId, Date displayDate, int status) {

		return findByG_LtD_S(
			groupId, displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the blogs entries where groupId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_LtD_S(
		long groupId, Date displayDate, int status, int start, int end) {

		return findByG_LtD_S(groupId, displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_LtD_S(
		long groupId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return findByG_LtD_S(
			groupId, displayDate, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_LtD_S(
		long groupId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByG_LtD_S;
			finderArgs = new Object[] {
				groupId, _getTime(displayDate), status, start, end,
				orderByComparator
			};

			List<BlogsEntry> list = null;

			if (useFinderCache) {
				list = (List<BlogsEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BlogsEntry blogsEntry : list) {
						if ((groupId != blogsEntry.getGroupId()) ||
							(displayDate.getTime() <= blogsEntry.getDisplayDate(
							).getTime()) ||
							(status != blogsEntry.getStatus())) {

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

				sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_LTD_S_GROUPID_2);

				boolean bindDisplayDate = false;

				if (displayDate == null) {
					sb.append(_FINDER_COLUMN_G_LTD_S_DISPLAYDATE_1);
				}
				else {
					bindDisplayDate = true;

					sb.append(_FINDER_COLUMN_G_LTD_S_DISPLAYDATE_2);
				}

				sb.append(_FINDER_COLUMN_G_LTD_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					if (bindDisplayDate) {
						queryPos.add(new Timestamp(displayDate.getTime()));
					}

					queryPos.add(status);

					list = (List<BlogsEntry>)QueryUtil.list(
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
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByG_LtD_S_First(
			long groupId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByG_LtD_S_First(
			groupId, displayDate, status, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", displayDate<");
		sb.append(displayDate);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_LtD_S_First(
		long groupId, Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		List<BlogsEntry> list = findByG_LtD_S(
			groupId, displayDate, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last blogs entry in the ordered set where groupId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByG_LtD_S_Last(
			long groupId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByG_LtD_S_Last(
			groupId, displayDate, status, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", displayDate<");
		sb.append(displayDate);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the last blogs entry in the ordered set where groupId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_LtD_S_Last(
		long groupId, Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		int count = countByG_LtD_S(groupId, displayDate, status);

		if (count == 0) {
			return null;
		}

		List<BlogsEntry> list = findByG_LtD_S(
			groupId, displayDate, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the blogs entries before and after the current blogs entry in the ordered set where groupId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param entryId the primary key of the current blogs entry
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry[] findByG_LtD_S_PrevAndNext(
			long entryId, long groupId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			BlogsEntry[] array = new BlogsEntryImpl[3];

			array[0] = getByG_LtD_S_PrevAndNext(
				session, blogsEntry, groupId, displayDate, status,
				orderByComparator, true);

			array[1] = blogsEntry;

			array[2] = getByG_LtD_S_PrevAndNext(
				session, blogsEntry, groupId, displayDate, status,
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

	protected BlogsEntry getByG_LtD_S_PrevAndNext(
		Session session, BlogsEntry blogsEntry, long groupId, Date displayDate,
		int status, OrderByComparator<BlogsEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_LTD_S_GROUPID_2);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_G_LTD_S_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_G_LTD_S_DISPLAYDATE_2);
		}

		sb.append(_FINDER_COLUMN_G_LTD_S_STATUS_2);

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
			sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		if (bindDisplayDate) {
			queryPos.add(new Timestamp(displayDate.getTime()));
		}

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(blogsEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BlogsEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the blogs entries that the user has permission to view where groupId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_LtD_S(
		long groupId, Date displayDate, int status) {

		return filterFindByG_LtD_S(
			groupId, displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the blogs entries that the user has permission to view where groupId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_LtD_S(
		long groupId, Date displayDate, int status, int start, int end) {

		return filterFindByG_LtD_S(
			groupId, displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_LtD_S(
		long groupId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_LtD_S(
				groupId, displayDate, status, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_LtD_S(
					groupId, displayDate, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
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
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_LTD_S_GROUPID_2);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_G_LTD_S_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_G_LTD_S_DISPLAYDATE_2);
		}

		sb.append(_FINDER_COLUMN_G_LTD_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, BlogsEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, BlogsEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			if (bindDisplayDate) {
				queryPos.add(new Timestamp(displayDate.getTime()));
			}

			queryPos.add(status);

			return (List<BlogsEntry>)QueryUtil.list(
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
	 * Returns the blogs entries before and after the current blogs entry in the ordered set of blogs entries that the user has permission to view where groupId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param entryId the primary key of the current blogs entry
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry[] filterFindByG_LtD_S_PrevAndNext(
			long entryId, long groupId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_LtD_S_PrevAndNext(
				entryId, groupId, displayDate, status, orderByComparator);
		}

		BlogsEntry blogsEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			BlogsEntry[] array = new BlogsEntryImpl[3];

			array[0] = filterGetByG_LtD_S_PrevAndNext(
				session, blogsEntry, groupId, displayDate, status,
				orderByComparator, true);

			array[1] = blogsEntry;

			array[2] = filterGetByG_LtD_S_PrevAndNext(
				session, blogsEntry, groupId, displayDate, status,
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

	protected BlogsEntry filterGetByG_LtD_S_PrevAndNext(
		Session session, BlogsEntry blogsEntry, long groupId, Date displayDate,
		int status, OrderByComparator<BlogsEntry> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_LTD_S_GROUPID_2);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_G_LTD_S_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_G_LTD_S_DISPLAYDATE_2);
		}

		sb.append(_FINDER_COLUMN_G_LTD_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, BlogsEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, BlogsEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (bindDisplayDate) {
			queryPos.add(new Timestamp(displayDate.getTime()));
		}

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(blogsEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BlogsEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; and displayDate &lt; &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 */
	@Override
	public void removeByG_LtD_S(long groupId, Date displayDate, int status) {
		for (BlogsEntry blogsEntry :
				findByG_LtD_S(
					groupId, displayDate, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(blogsEntry);
		}
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByG_LtD_S(long groupId, Date displayDate, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByG_LtD_S;

			Object[] finderArgs = new Object[] {
				groupId, _getTime(displayDate), status
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_LTD_S_GROUPID_2);

				boolean bindDisplayDate = false;

				if (displayDate == null) {
					sb.append(_FINDER_COLUMN_G_LTD_S_DISPLAYDATE_1);
				}
				else {
					bindDisplayDate = true;

					sb.append(_FINDER_COLUMN_G_LTD_S_DISPLAYDATE_2);
				}

				sb.append(_FINDER_COLUMN_G_LTD_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

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
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_LtD_S(
		long groupId, Date displayDate, int status) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_LtD_S(groupId, displayDate, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<BlogsEntry> blogsEntries = findByG_LtD_S(
				groupId, displayDate, status);

			blogsEntries = InlineSQLHelperUtil.filter(blogsEntries, groupId);

			return blogsEntries.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_BLOGSENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_LTD_S_GROUPID_2);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_G_LTD_S_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_G_LTD_S_DISPLAYDATE_2);
		}

		sb.append(_FINDER_COLUMN_G_LTD_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

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

	private static final String _FINDER_COLUMN_G_LTD_S_GROUPID_2 =
		"blogsEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_LTD_S_DISPLAYDATE_1 =
		"blogsEntry.displayDate IS NULL AND ";

	private static final String _FINDER_COLUMN_G_LTD_S_DISPLAYDATE_2 =
		"blogsEntry.displayDate < ? AND ";

	private static final String _FINDER_COLUMN_G_LTD_S_STATUS_2 =
		"blogsEntry.status = ?";

	private FinderPath _finderPathWithPaginationFindByG_LtD_NotS;
	private FinderPath _finderPathWithPaginationCountByG_LtD_NotS;

	/**
	 * Returns all the blogs entries where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_LtD_NotS(
		long groupId, Date displayDate, int status) {

		return findByG_LtD_NotS(
			groupId, displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the blogs entries where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_LtD_NotS(
		long groupId, Date displayDate, int status, int start, int end) {

		return findByG_LtD_NotS(groupId, displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_LtD_NotS(
		long groupId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return findByG_LtD_NotS(
			groupId, displayDate, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_LtD_NotS(
		long groupId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByG_LtD_NotS;
			finderArgs = new Object[] {
				groupId, _getTime(displayDate), status, start, end,
				orderByComparator
			};

			List<BlogsEntry> list = null;

			if (useFinderCache) {
				list = (List<BlogsEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BlogsEntry blogsEntry : list) {
						if ((groupId != blogsEntry.getGroupId()) ||
							(displayDate.getTime() <= blogsEntry.getDisplayDate(
							).getTime()) ||
							(status == blogsEntry.getStatus())) {

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

				sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_LTD_NOTS_GROUPID_2);

				boolean bindDisplayDate = false;

				if (displayDate == null) {
					sb.append(_FINDER_COLUMN_G_LTD_NOTS_DISPLAYDATE_1);
				}
				else {
					bindDisplayDate = true;

					sb.append(_FINDER_COLUMN_G_LTD_NOTS_DISPLAYDATE_2);
				}

				sb.append(_FINDER_COLUMN_G_LTD_NOTS_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					if (bindDisplayDate) {
						queryPos.add(new Timestamp(displayDate.getTime()));
					}

					queryPos.add(status);

					list = (List<BlogsEntry>)QueryUtil.list(
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
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByG_LtD_NotS_First(
			long groupId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByG_LtD_NotS_First(
			groupId, displayDate, status, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", displayDate<");
		sb.append(displayDate);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_LtD_NotS_First(
		long groupId, Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		List<BlogsEntry> list = findByG_LtD_NotS(
			groupId, displayDate, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last blogs entry in the ordered set where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByG_LtD_NotS_Last(
			long groupId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByG_LtD_NotS_Last(
			groupId, displayDate, status, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", displayDate<");
		sb.append(displayDate);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the last blogs entry in the ordered set where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_LtD_NotS_Last(
		long groupId, Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		int count = countByG_LtD_NotS(groupId, displayDate, status);

		if (count == 0) {
			return null;
		}

		List<BlogsEntry> list = findByG_LtD_NotS(
			groupId, displayDate, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the blogs entries before and after the current blogs entry in the ordered set where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param entryId the primary key of the current blogs entry
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry[] findByG_LtD_NotS_PrevAndNext(
			long entryId, long groupId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			BlogsEntry[] array = new BlogsEntryImpl[3];

			array[0] = getByG_LtD_NotS_PrevAndNext(
				session, blogsEntry, groupId, displayDate, status,
				orderByComparator, true);

			array[1] = blogsEntry;

			array[2] = getByG_LtD_NotS_PrevAndNext(
				session, blogsEntry, groupId, displayDate, status,
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

	protected BlogsEntry getByG_LtD_NotS_PrevAndNext(
		Session session, BlogsEntry blogsEntry, long groupId, Date displayDate,
		int status, OrderByComparator<BlogsEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_LTD_NOTS_GROUPID_2);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_G_LTD_NOTS_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_G_LTD_NOTS_DISPLAYDATE_2);
		}

		sb.append(_FINDER_COLUMN_G_LTD_NOTS_STATUS_2);

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
			sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		if (bindDisplayDate) {
			queryPos.add(new Timestamp(displayDate.getTime()));
		}

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(blogsEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BlogsEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the blogs entries that the user has permission to view where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_LtD_NotS(
		long groupId, Date displayDate, int status) {

		return filterFindByG_LtD_NotS(
			groupId, displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the blogs entries that the user has permission to view where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_LtD_NotS(
		long groupId, Date displayDate, int status, int start, int end) {

		return filterFindByG_LtD_NotS(
			groupId, displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_LtD_NotS(
		long groupId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_LtD_NotS(
				groupId, displayDate, status, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_LtD_NotS(
					groupId, displayDate, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
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
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_LTD_NOTS_GROUPID_2);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_G_LTD_NOTS_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_G_LTD_NOTS_DISPLAYDATE_2);
		}

		sb.append(_FINDER_COLUMN_G_LTD_NOTS_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, BlogsEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, BlogsEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			if (bindDisplayDate) {
				queryPos.add(new Timestamp(displayDate.getTime()));
			}

			queryPos.add(status);

			return (List<BlogsEntry>)QueryUtil.list(
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
	 * Returns the blogs entries before and after the current blogs entry in the ordered set of blogs entries that the user has permission to view where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param entryId the primary key of the current blogs entry
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry[] filterFindByG_LtD_NotS_PrevAndNext(
			long entryId, long groupId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_LtD_NotS_PrevAndNext(
				entryId, groupId, displayDate, status, orderByComparator);
		}

		BlogsEntry blogsEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			BlogsEntry[] array = new BlogsEntryImpl[3];

			array[0] = filterGetByG_LtD_NotS_PrevAndNext(
				session, blogsEntry, groupId, displayDate, status,
				orderByComparator, true);

			array[1] = blogsEntry;

			array[2] = filterGetByG_LtD_NotS_PrevAndNext(
				session, blogsEntry, groupId, displayDate, status,
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

	protected BlogsEntry filterGetByG_LtD_NotS_PrevAndNext(
		Session session, BlogsEntry blogsEntry, long groupId, Date displayDate,
		int status, OrderByComparator<BlogsEntry> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_LTD_NOTS_GROUPID_2);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_G_LTD_NOTS_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_G_LTD_NOTS_DISPLAYDATE_2);
		}

		sb.append(_FINDER_COLUMN_G_LTD_NOTS_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, BlogsEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, BlogsEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (bindDisplayDate) {
			queryPos.add(new Timestamp(displayDate.getTime()));
		}

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(blogsEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BlogsEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 */
	@Override
	public void removeByG_LtD_NotS(long groupId, Date displayDate, int status) {
		for (BlogsEntry blogsEntry :
				findByG_LtD_NotS(
					groupId, displayDate, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(blogsEntry);
		}
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByG_LtD_NotS(long groupId, Date displayDate, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByG_LtD_NotS;

			Object[] finderArgs = new Object[] {
				groupId, _getTime(displayDate), status
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_LTD_NOTS_GROUPID_2);

				boolean bindDisplayDate = false;

				if (displayDate == null) {
					sb.append(_FINDER_COLUMN_G_LTD_NOTS_DISPLAYDATE_1);
				}
				else {
					bindDisplayDate = true;

					sb.append(_FINDER_COLUMN_G_LTD_NOTS_DISPLAYDATE_2);
				}

				sb.append(_FINDER_COLUMN_G_LTD_NOTS_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

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
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_LtD_NotS(
		long groupId, Date displayDate, int status) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_LtD_NotS(groupId, displayDate, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<BlogsEntry> blogsEntries = findByG_LtD_NotS(
				groupId, displayDate, status);

			blogsEntries = InlineSQLHelperUtil.filter(blogsEntries, groupId);

			return blogsEntries.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_BLOGSENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_LTD_NOTS_GROUPID_2);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_G_LTD_NOTS_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_G_LTD_NOTS_DISPLAYDATE_2);
		}

		sb.append(_FINDER_COLUMN_G_LTD_NOTS_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

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

	private static final String _FINDER_COLUMN_G_LTD_NOTS_GROUPID_2 =
		"blogsEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_LTD_NOTS_DISPLAYDATE_1 =
		"blogsEntry.displayDate IS NULL AND ";

	private static final String _FINDER_COLUMN_G_LTD_NOTS_DISPLAYDATE_2 =
		"blogsEntry.displayDate < ? AND ";

	private static final String _FINDER_COLUMN_G_LTD_NOTS_STATUS_2 =
		"blogsEntry.status != ?";

	private FinderPath _finderPathWithPaginationFindByC_U_S;
	private FinderPath _finderPathWithoutPaginationFindByC_U_S;
	private FinderPath _finderPathCountByC_U_S;

	/**
	 * Returns all the blogs entries where companyId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_U_S(
		long companyId, long userId, int status) {

		return findByC_U_S(
			companyId, userId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the blogs entries where companyId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_U_S(
		long companyId, long userId, int status, int start, int end) {

		return findByC_U_S(companyId, userId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_U_S(
		long companyId, long userId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return findByC_U_S(
			companyId, userId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_U_S(
		long companyId, long userId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_U_S;
					finderArgs = new Object[] {companyId, userId, status};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_U_S;
				finderArgs = new Object[] {
					companyId, userId, status, start, end, orderByComparator
				};
			}

			List<BlogsEntry> list = null;

			if (useFinderCache) {
				list = (List<BlogsEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BlogsEntry blogsEntry : list) {
						if ((companyId != blogsEntry.getCompanyId()) ||
							(userId != blogsEntry.getUserId()) ||
							(status != blogsEntry.getStatus())) {

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

				sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_C_U_S_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_U_S_USERID_2);

				sb.append(_FINDER_COLUMN_C_U_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(userId);

					queryPos.add(status);

					list = (List<BlogsEntry>)QueryUtil.list(
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
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByC_U_S_First(
			long companyId, long userId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByC_U_S_First(
			companyId, userId, status, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByC_U_S_First(
		long companyId, long userId, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		List<BlogsEntry> list = findByC_U_S(
			companyId, userId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last blogs entry in the ordered set where companyId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByC_U_S_Last(
			long companyId, long userId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByC_U_S_Last(
			companyId, userId, status, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the last blogs entry in the ordered set where companyId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByC_U_S_Last(
		long companyId, long userId, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		int count = countByC_U_S(companyId, userId, status);

		if (count == 0) {
			return null;
		}

		List<BlogsEntry> list = findByC_U_S(
			companyId, userId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the blogs entries before and after the current blogs entry in the ordered set where companyId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param entryId the primary key of the current blogs entry
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry[] findByC_U_S_PrevAndNext(
			long entryId, long companyId, long userId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			BlogsEntry[] array = new BlogsEntryImpl[3];

			array[0] = getByC_U_S_PrevAndNext(
				session, blogsEntry, companyId, userId, status,
				orderByComparator, true);

			array[1] = blogsEntry;

			array[2] = getByC_U_S_PrevAndNext(
				session, blogsEntry, companyId, userId, status,
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

	protected BlogsEntry getByC_U_S_PrevAndNext(
		Session session, BlogsEntry blogsEntry, long companyId, long userId,
		int status, OrderByComparator<BlogsEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

		sb.append(_FINDER_COLUMN_C_U_S_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_U_S_USERID_2);

		sb.append(_FINDER_COLUMN_C_U_S_STATUS_2);

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
			sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(userId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(blogsEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BlogsEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the blogs entries where companyId = &#63; and userId = &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 */
	@Override
	public void removeByC_U_S(long companyId, long userId, int status) {
		for (BlogsEntry blogsEntry :
				findByC_U_S(
					companyId, userId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(blogsEntry);
		}
	}

	/**
	 * Returns the number of blogs entries where companyId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByC_U_S(long companyId, long userId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = _finderPathCountByC_U_S;

			Object[] finderArgs = new Object[] {companyId, userId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_C_U_S_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_U_S_USERID_2);

				sb.append(_FINDER_COLUMN_C_U_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(userId);

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
	}

	private static final String _FINDER_COLUMN_C_U_S_COMPANYID_2 =
		"blogsEntry.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_U_S_USERID_2 =
		"blogsEntry.userId = ? AND ";

	private static final String _FINDER_COLUMN_C_U_S_STATUS_2 =
		"blogsEntry.status = ?";

	private FinderPath _finderPathWithPaginationFindByC_U_NotS;
	private FinderPath _finderPathWithPaginationCountByC_U_NotS;

	/**
	 * Returns all the blogs entries where companyId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_U_NotS(
		long companyId, long userId, int status) {

		return findByC_U_NotS(
			companyId, userId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the blogs entries where companyId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_U_NotS(
		long companyId, long userId, int status, int start, int end) {

		return findByC_U_NotS(companyId, userId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_U_NotS(
		long companyId, long userId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return findByC_U_NotS(
			companyId, userId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_U_NotS(
		long companyId, long userId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByC_U_NotS;
			finderArgs = new Object[] {
				companyId, userId, status, start, end, orderByComparator
			};

			List<BlogsEntry> list = null;

			if (useFinderCache) {
				list = (List<BlogsEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BlogsEntry blogsEntry : list) {
						if ((companyId != blogsEntry.getCompanyId()) ||
							(userId != blogsEntry.getUserId()) ||
							(status == blogsEntry.getStatus())) {

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

				sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_C_U_NOTS_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_U_NOTS_USERID_2);

				sb.append(_FINDER_COLUMN_C_U_NOTS_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(userId);

					queryPos.add(status);

					list = (List<BlogsEntry>)QueryUtil.list(
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
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByC_U_NotS_First(
			long companyId, long userId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByC_U_NotS_First(
			companyId, userId, status, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByC_U_NotS_First(
		long companyId, long userId, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		List<BlogsEntry> list = findByC_U_NotS(
			companyId, userId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last blogs entry in the ordered set where companyId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByC_U_NotS_Last(
			long companyId, long userId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByC_U_NotS_Last(
			companyId, userId, status, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the last blogs entry in the ordered set where companyId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByC_U_NotS_Last(
		long companyId, long userId, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		int count = countByC_U_NotS(companyId, userId, status);

		if (count == 0) {
			return null;
		}

		List<BlogsEntry> list = findByC_U_NotS(
			companyId, userId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the blogs entries before and after the current blogs entry in the ordered set where companyId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param entryId the primary key of the current blogs entry
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry[] findByC_U_NotS_PrevAndNext(
			long entryId, long companyId, long userId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			BlogsEntry[] array = new BlogsEntryImpl[3];

			array[0] = getByC_U_NotS_PrevAndNext(
				session, blogsEntry, companyId, userId, status,
				orderByComparator, true);

			array[1] = blogsEntry;

			array[2] = getByC_U_NotS_PrevAndNext(
				session, blogsEntry, companyId, userId, status,
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

	protected BlogsEntry getByC_U_NotS_PrevAndNext(
		Session session, BlogsEntry blogsEntry, long companyId, long userId,
		int status, OrderByComparator<BlogsEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

		sb.append(_FINDER_COLUMN_C_U_NOTS_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_U_NOTS_USERID_2);

		sb.append(_FINDER_COLUMN_C_U_NOTS_STATUS_2);

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
			sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(userId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(blogsEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BlogsEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the blogs entries where companyId = &#63; and userId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 */
	@Override
	public void removeByC_U_NotS(long companyId, long userId, int status) {
		for (BlogsEntry blogsEntry :
				findByC_U_NotS(
					companyId, userId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(blogsEntry);
		}
	}

	/**
	 * Returns the number of blogs entries where companyId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByC_U_NotS(long companyId, long userId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByC_U_NotS;

			Object[] finderArgs = new Object[] {companyId, userId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_C_U_NOTS_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_U_NOTS_USERID_2);

				sb.append(_FINDER_COLUMN_C_U_NOTS_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(userId);

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
	}

	private static final String _FINDER_COLUMN_C_U_NOTS_COMPANYID_2 =
		"blogsEntry.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_U_NOTS_USERID_2 =
		"blogsEntry.userId = ? AND ";

	private static final String _FINDER_COLUMN_C_U_NOTS_STATUS_2 =
		"blogsEntry.status != ?";

	private FinderPath _finderPathWithPaginationFindByC_LtD_S;
	private FinderPath _finderPathWithPaginationCountByC_LtD_S;

	/**
	 * Returns all the blogs entries where companyId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_LtD_S(
		long companyId, Date displayDate, int status) {

		return findByC_LtD_S(
			companyId, displayDate, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the blogs entries where companyId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_LtD_S(
		long companyId, Date displayDate, int status, int start, int end) {

		return findByC_LtD_S(companyId, displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_LtD_S(
		long companyId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return findByC_LtD_S(
			companyId, displayDate, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_LtD_S(
		long companyId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByC_LtD_S;
			finderArgs = new Object[] {
				companyId, _getTime(displayDate), status, start, end,
				orderByComparator
			};

			List<BlogsEntry> list = null;

			if (useFinderCache) {
				list = (List<BlogsEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BlogsEntry blogsEntry : list) {
						if ((companyId != blogsEntry.getCompanyId()) ||
							(displayDate.getTime() <= blogsEntry.getDisplayDate(
							).getTime()) ||
							(status != blogsEntry.getStatus())) {

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

				sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_C_LTD_S_COMPANYID_2);

				boolean bindDisplayDate = false;

				if (displayDate == null) {
					sb.append(_FINDER_COLUMN_C_LTD_S_DISPLAYDATE_1);
				}
				else {
					bindDisplayDate = true;

					sb.append(_FINDER_COLUMN_C_LTD_S_DISPLAYDATE_2);
				}

				sb.append(_FINDER_COLUMN_C_LTD_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					if (bindDisplayDate) {
						queryPos.add(new Timestamp(displayDate.getTime()));
					}

					queryPos.add(status);

					list = (List<BlogsEntry>)QueryUtil.list(
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
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByC_LtD_S_First(
			long companyId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByC_LtD_S_First(
			companyId, displayDate, status, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", displayDate<");
		sb.append(displayDate);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByC_LtD_S_First(
		long companyId, Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		List<BlogsEntry> list = findByC_LtD_S(
			companyId, displayDate, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last blogs entry in the ordered set where companyId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByC_LtD_S_Last(
			long companyId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByC_LtD_S_Last(
			companyId, displayDate, status, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", displayDate<");
		sb.append(displayDate);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the last blogs entry in the ordered set where companyId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByC_LtD_S_Last(
		long companyId, Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		int count = countByC_LtD_S(companyId, displayDate, status);

		if (count == 0) {
			return null;
		}

		List<BlogsEntry> list = findByC_LtD_S(
			companyId, displayDate, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the blogs entries before and after the current blogs entry in the ordered set where companyId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param entryId the primary key of the current blogs entry
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry[] findByC_LtD_S_PrevAndNext(
			long entryId, long companyId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			BlogsEntry[] array = new BlogsEntryImpl[3];

			array[0] = getByC_LtD_S_PrevAndNext(
				session, blogsEntry, companyId, displayDate, status,
				orderByComparator, true);

			array[1] = blogsEntry;

			array[2] = getByC_LtD_S_PrevAndNext(
				session, blogsEntry, companyId, displayDate, status,
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

	protected BlogsEntry getByC_LtD_S_PrevAndNext(
		Session session, BlogsEntry blogsEntry, long companyId,
		Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

		sb.append(_FINDER_COLUMN_C_LTD_S_COMPANYID_2);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_C_LTD_S_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_C_LTD_S_DISPLAYDATE_2);
		}

		sb.append(_FINDER_COLUMN_C_LTD_S_STATUS_2);

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
			sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (bindDisplayDate) {
			queryPos.add(new Timestamp(displayDate.getTime()));
		}

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(blogsEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BlogsEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the blogs entries where companyId = &#63; and displayDate &lt; &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 */
	@Override
	public void removeByC_LtD_S(long companyId, Date displayDate, int status) {
		for (BlogsEntry blogsEntry :
				findByC_LtD_S(
					companyId, displayDate, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(blogsEntry);
		}
	}

	/**
	 * Returns the number of blogs entries where companyId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByC_LtD_S(long companyId, Date displayDate, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByC_LtD_S;

			Object[] finderArgs = new Object[] {
				companyId, _getTime(displayDate), status
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_C_LTD_S_COMPANYID_2);

				boolean bindDisplayDate = false;

				if (displayDate == null) {
					sb.append(_FINDER_COLUMN_C_LTD_S_DISPLAYDATE_1);
				}
				else {
					bindDisplayDate = true;

					sb.append(_FINDER_COLUMN_C_LTD_S_DISPLAYDATE_2);
				}

				sb.append(_FINDER_COLUMN_C_LTD_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

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
	}

	private static final String _FINDER_COLUMN_C_LTD_S_COMPANYID_2 =
		"blogsEntry.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_LTD_S_DISPLAYDATE_1 =
		"blogsEntry.displayDate IS NULL AND ";

	private static final String _FINDER_COLUMN_C_LTD_S_DISPLAYDATE_2 =
		"blogsEntry.displayDate < ? AND ";

	private static final String _FINDER_COLUMN_C_LTD_S_STATUS_2 =
		"blogsEntry.status = ?";

	private FinderPath _finderPathWithPaginationFindByC_LtD_NotS;
	private FinderPath _finderPathWithPaginationCountByC_LtD_NotS;

	/**
	 * Returns all the blogs entries where companyId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_LtD_NotS(
		long companyId, Date displayDate, int status) {

		return findByC_LtD_NotS(
			companyId, displayDate, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the blogs entries where companyId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_LtD_NotS(
		long companyId, Date displayDate, int status, int start, int end) {

		return findByC_LtD_NotS(
			companyId, displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_LtD_NotS(
		long companyId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return findByC_LtD_NotS(
			companyId, displayDate, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByC_LtD_NotS(
		long companyId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByC_LtD_NotS;
			finderArgs = new Object[] {
				companyId, _getTime(displayDate), status, start, end,
				orderByComparator
			};

			List<BlogsEntry> list = null;

			if (useFinderCache) {
				list = (List<BlogsEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BlogsEntry blogsEntry : list) {
						if ((companyId != blogsEntry.getCompanyId()) ||
							(displayDate.getTime() <= blogsEntry.getDisplayDate(
							).getTime()) ||
							(status == blogsEntry.getStatus())) {

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

				sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_C_LTD_NOTS_COMPANYID_2);

				boolean bindDisplayDate = false;

				if (displayDate == null) {
					sb.append(_FINDER_COLUMN_C_LTD_NOTS_DISPLAYDATE_1);
				}
				else {
					bindDisplayDate = true;

					sb.append(_FINDER_COLUMN_C_LTD_NOTS_DISPLAYDATE_2);
				}

				sb.append(_FINDER_COLUMN_C_LTD_NOTS_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					if (bindDisplayDate) {
						queryPos.add(new Timestamp(displayDate.getTime()));
					}

					queryPos.add(status);

					list = (List<BlogsEntry>)QueryUtil.list(
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
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByC_LtD_NotS_First(
			long companyId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByC_LtD_NotS_First(
			companyId, displayDate, status, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", displayDate<");
		sb.append(displayDate);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByC_LtD_NotS_First(
		long companyId, Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		List<BlogsEntry> list = findByC_LtD_NotS(
			companyId, displayDate, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last blogs entry in the ordered set where companyId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByC_LtD_NotS_Last(
			long companyId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByC_LtD_NotS_Last(
			companyId, displayDate, status, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", displayDate<");
		sb.append(displayDate);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the last blogs entry in the ordered set where companyId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByC_LtD_NotS_Last(
		long companyId, Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		int count = countByC_LtD_NotS(companyId, displayDate, status);

		if (count == 0) {
			return null;
		}

		List<BlogsEntry> list = findByC_LtD_NotS(
			companyId, displayDate, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the blogs entries before and after the current blogs entry in the ordered set where companyId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param entryId the primary key of the current blogs entry
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry[] findByC_LtD_NotS_PrevAndNext(
			long entryId, long companyId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			BlogsEntry[] array = new BlogsEntryImpl[3];

			array[0] = getByC_LtD_NotS_PrevAndNext(
				session, blogsEntry, companyId, displayDate, status,
				orderByComparator, true);

			array[1] = blogsEntry;

			array[2] = getByC_LtD_NotS_PrevAndNext(
				session, blogsEntry, companyId, displayDate, status,
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

	protected BlogsEntry getByC_LtD_NotS_PrevAndNext(
		Session session, BlogsEntry blogsEntry, long companyId,
		Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

		sb.append(_FINDER_COLUMN_C_LTD_NOTS_COMPANYID_2);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_C_LTD_NOTS_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_C_LTD_NOTS_DISPLAYDATE_2);
		}

		sb.append(_FINDER_COLUMN_C_LTD_NOTS_STATUS_2);

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
			sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (bindDisplayDate) {
			queryPos.add(new Timestamp(displayDate.getTime()));
		}

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(blogsEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BlogsEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the blogs entries where companyId = &#63; and displayDate &lt; &#63; and status &ne; &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 */
	@Override
	public void removeByC_LtD_NotS(
		long companyId, Date displayDate, int status) {

		for (BlogsEntry blogsEntry :
				findByC_LtD_NotS(
					companyId, displayDate, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(blogsEntry);
		}
	}

	/**
	 * Returns the number of blogs entries where companyId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByC_LtD_NotS(long companyId, Date displayDate, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByC_LtD_NotS;

			Object[] finderArgs = new Object[] {
				companyId, _getTime(displayDate), status
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_C_LTD_NOTS_COMPANYID_2);

				boolean bindDisplayDate = false;

				if (displayDate == null) {
					sb.append(_FINDER_COLUMN_C_LTD_NOTS_DISPLAYDATE_1);
				}
				else {
					bindDisplayDate = true;

					sb.append(_FINDER_COLUMN_C_LTD_NOTS_DISPLAYDATE_2);
				}

				sb.append(_FINDER_COLUMN_C_LTD_NOTS_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

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
	}

	private static final String _FINDER_COLUMN_C_LTD_NOTS_COMPANYID_2 =
		"blogsEntry.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_LTD_NOTS_DISPLAYDATE_1 =
		"blogsEntry.displayDate IS NULL AND ";

	private static final String _FINDER_COLUMN_C_LTD_NOTS_DISPLAYDATE_2 =
		"blogsEntry.displayDate < ? AND ";

	private static final String _FINDER_COLUMN_C_LTD_NOTS_STATUS_2 =
		"blogsEntry.status != ?";

	private FinderPath _finderPathWithPaginationFindByG_U_LtD_S;
	private FinderPath _finderPathWithPaginationCountByG_U_LtD_S;

	/**
	 * Returns all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_LtD_S(
		long groupId, long userId, Date displayDate, int status) {

		return findByG_U_LtD_S(
			groupId, userId, displayDate, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_LtD_S(
		long groupId, long userId, Date displayDate, int status, int start,
		int end) {

		return findByG_U_LtD_S(
			groupId, userId, displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_LtD_S(
		long groupId, long userId, Date displayDate, int status, int start,
		int end, OrderByComparator<BlogsEntry> orderByComparator) {

		return findByG_U_LtD_S(
			groupId, userId, displayDate, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_LtD_S(
		long groupId, long userId, Date displayDate, int status, int start,
		int end, OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByG_U_LtD_S;
			finderArgs = new Object[] {
				groupId, userId, _getTime(displayDate), status, start, end,
				orderByComparator
			};

			List<BlogsEntry> list = null;

			if (useFinderCache) {
				list = (List<BlogsEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BlogsEntry blogsEntry : list) {
						if ((groupId != blogsEntry.getGroupId()) ||
							(userId != blogsEntry.getUserId()) ||
							(displayDate.getTime() <= blogsEntry.getDisplayDate(
							).getTime()) ||
							(status != blogsEntry.getStatus())) {

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

				sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_U_LTD_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_U_LTD_S_USERID_2);

				boolean bindDisplayDate = false;

				if (displayDate == null) {
					sb.append(_FINDER_COLUMN_G_U_LTD_S_DISPLAYDATE_1);
				}
				else {
					bindDisplayDate = true;

					sb.append(_FINDER_COLUMN_G_U_LTD_S_DISPLAYDATE_2);
				}

				sb.append(_FINDER_COLUMN_G_U_LTD_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(userId);

					if (bindDisplayDate) {
						queryPos.add(new Timestamp(displayDate.getTime()));
					}

					queryPos.add(status);

					list = (List<BlogsEntry>)QueryUtil.list(
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
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByG_U_LtD_S_First(
			long groupId, long userId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByG_U_LtD_S_First(
			groupId, userId, displayDate, status, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append(", displayDate<");
		sb.append(displayDate);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_U_LtD_S_First(
		long groupId, long userId, Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		List<BlogsEntry> list = findByG_U_LtD_S(
			groupId, userId, displayDate, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last blogs entry in the ordered set where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByG_U_LtD_S_Last(
			long groupId, long userId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByG_U_LtD_S_Last(
			groupId, userId, displayDate, status, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append(", displayDate<");
		sb.append(displayDate);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the last blogs entry in the ordered set where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_U_LtD_S_Last(
		long groupId, long userId, Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		int count = countByG_U_LtD_S(groupId, userId, displayDate, status);

		if (count == 0) {
			return null;
		}

		List<BlogsEntry> list = findByG_U_LtD_S(
			groupId, userId, displayDate, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the blogs entries before and after the current blogs entry in the ordered set where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param entryId the primary key of the current blogs entry
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry[] findByG_U_LtD_S_PrevAndNext(
			long entryId, long groupId, long userId, Date displayDate,
			int status, OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			BlogsEntry[] array = new BlogsEntryImpl[3];

			array[0] = getByG_U_LtD_S_PrevAndNext(
				session, blogsEntry, groupId, userId, displayDate, status,
				orderByComparator, true);

			array[1] = blogsEntry;

			array[2] = getByG_U_LtD_S_PrevAndNext(
				session, blogsEntry, groupId, userId, displayDate, status,
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

	protected BlogsEntry getByG_U_LtD_S_PrevAndNext(
		Session session, BlogsEntry blogsEntry, long groupId, long userId,
		Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_U_LTD_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_LTD_S_USERID_2);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_G_U_LTD_S_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_G_U_LTD_S_DISPLAYDATE_2);
		}

		sb.append(_FINDER_COLUMN_G_U_LTD_S_STATUS_2);

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
			sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(userId);

		if (bindDisplayDate) {
			queryPos.add(new Timestamp(displayDate.getTime()));
		}

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(blogsEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BlogsEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_U_LtD_S(
		long groupId, long userId, Date displayDate, int status) {

		return filterFindByG_U_LtD_S(
			groupId, userId, displayDate, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_U_LtD_S(
		long groupId, long userId, Date displayDate, int status, int start,
		int end) {

		return filterFindByG_U_LtD_S(
			groupId, userId, displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_U_LtD_S(
		long groupId, long userId, Date displayDate, int status, int start,
		int end, OrderByComparator<BlogsEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_U_LtD_S(
				groupId, userId, displayDate, status, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_U_LtD_S(
					groupId, userId, displayDate, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
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
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_LTD_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_LTD_S_USERID_2);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_G_U_LTD_S_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_G_U_LTD_S_DISPLAYDATE_2);
		}

		sb.append(_FINDER_COLUMN_G_U_LTD_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, BlogsEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, BlogsEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(userId);

			if (bindDisplayDate) {
				queryPos.add(new Timestamp(displayDate.getTime()));
			}

			queryPos.add(status);

			return (List<BlogsEntry>)QueryUtil.list(
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
	 * Returns the blogs entries before and after the current blogs entry in the ordered set of blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param entryId the primary key of the current blogs entry
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry[] filterFindByG_U_LtD_S_PrevAndNext(
			long entryId, long groupId, long userId, Date displayDate,
			int status, OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_U_LtD_S_PrevAndNext(
				entryId, groupId, userId, displayDate, status,
				orderByComparator);
		}

		BlogsEntry blogsEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			BlogsEntry[] array = new BlogsEntryImpl[3];

			array[0] = filterGetByG_U_LtD_S_PrevAndNext(
				session, blogsEntry, groupId, userId, displayDate, status,
				orderByComparator, true);

			array[1] = blogsEntry;

			array[2] = filterGetByG_U_LtD_S_PrevAndNext(
				session, blogsEntry, groupId, userId, displayDate, status,
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

	protected BlogsEntry filterGetByG_U_LtD_S_PrevAndNext(
		Session session, BlogsEntry blogsEntry, long groupId, long userId,
		Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_LTD_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_LTD_S_USERID_2);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_G_U_LTD_S_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_G_U_LTD_S_DISPLAYDATE_2);
		}

		sb.append(_FINDER_COLUMN_G_U_LTD_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, BlogsEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, BlogsEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(userId);

		if (bindDisplayDate) {
			queryPos.add(new Timestamp(displayDate.getTime()));
		}

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(blogsEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BlogsEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 */
	@Override
	public void removeByG_U_LtD_S(
		long groupId, long userId, Date displayDate, int status) {

		for (BlogsEntry blogsEntry :
				findByG_U_LtD_S(
					groupId, userId, displayDate, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(blogsEntry);
		}
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByG_U_LtD_S(
		long groupId, long userId, Date displayDate, int status) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByG_U_LtD_S;

			Object[] finderArgs = new Object[] {
				groupId, userId, _getTime(displayDate), status
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_U_LTD_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_U_LTD_S_USERID_2);

				boolean bindDisplayDate = false;

				if (displayDate == null) {
					sb.append(_FINDER_COLUMN_G_U_LTD_S_DISPLAYDATE_1);
				}
				else {
					bindDisplayDate = true;

					sb.append(_FINDER_COLUMN_G_U_LTD_S_DISPLAYDATE_2);
				}

				sb.append(_FINDER_COLUMN_G_U_LTD_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(userId);

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
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_U_LtD_S(
		long groupId, long userId, Date displayDate, int status) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_U_LtD_S(groupId, userId, displayDate, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<BlogsEntry> blogsEntries = findByG_U_LtD_S(
				groupId, userId, displayDate, status);

			blogsEntries = InlineSQLHelperUtil.filter(blogsEntries, groupId);

			return blogsEntries.size();
		}

		StringBundler sb = new StringBundler(5);

		sb.append(_FILTER_SQL_COUNT_BLOGSENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_U_LTD_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_LTD_S_USERID_2);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_G_U_LTD_S_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_G_U_LTD_S_DISPLAYDATE_2);
		}

		sb.append(_FINDER_COLUMN_G_U_LTD_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(userId);

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

	private static final String _FINDER_COLUMN_G_U_LTD_S_GROUPID_2 =
		"blogsEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_U_LTD_S_USERID_2 =
		"blogsEntry.userId = ? AND ";

	private static final String _FINDER_COLUMN_G_U_LTD_S_DISPLAYDATE_1 =
		"blogsEntry.displayDate IS NULL AND ";

	private static final String _FINDER_COLUMN_G_U_LTD_S_DISPLAYDATE_2 =
		"blogsEntry.displayDate < ? AND ";

	private static final String _FINDER_COLUMN_G_U_LTD_S_STATUS_2 =
		"blogsEntry.status = ?";

	private FinderPath _finderPathWithPaginationFindByG_U_LtD_NotS;
	private FinderPath _finderPathWithPaginationCountByG_U_LtD_NotS;

	/**
	 * Returns all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_LtD_NotS(
		long groupId, long userId, Date displayDate, int status) {

		return findByG_U_LtD_NotS(
			groupId, userId, displayDate, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_LtD_NotS(
		long groupId, long userId, Date displayDate, int status, int start,
		int end) {

		return findByG_U_LtD_NotS(
			groupId, userId, displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_LtD_NotS(
		long groupId, long userId, Date displayDate, int status, int start,
		int end, OrderByComparator<BlogsEntry> orderByComparator) {

		return findByG_U_LtD_NotS(
			groupId, userId, displayDate, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	@Override
	public List<BlogsEntry> findByG_U_LtD_NotS(
		long groupId, long userId, Date displayDate, int status, int start,
		int end, OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByG_U_LtD_NotS;
			finderArgs = new Object[] {
				groupId, userId, _getTime(displayDate), status, start, end,
				orderByComparator
			};

			List<BlogsEntry> list = null;

			if (useFinderCache) {
				list = (List<BlogsEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BlogsEntry blogsEntry : list) {
						if ((groupId != blogsEntry.getGroupId()) ||
							(userId != blogsEntry.getUserId()) ||
							(displayDate.getTime() <= blogsEntry.getDisplayDate(
							).getTime()) ||
							(status == blogsEntry.getStatus())) {

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

				sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_U_LTD_NOTS_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_U_LTD_NOTS_USERID_2);

				boolean bindDisplayDate = false;

				if (displayDate == null) {
					sb.append(_FINDER_COLUMN_G_U_LTD_NOTS_DISPLAYDATE_1);
				}
				else {
					bindDisplayDate = true;

					sb.append(_FINDER_COLUMN_G_U_LTD_NOTS_DISPLAYDATE_2);
				}

				sb.append(_FINDER_COLUMN_G_U_LTD_NOTS_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(userId);

					if (bindDisplayDate) {
						queryPos.add(new Timestamp(displayDate.getTime()));
					}

					queryPos.add(status);

					list = (List<BlogsEntry>)QueryUtil.list(
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
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByG_U_LtD_NotS_First(
			long groupId, long userId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByG_U_LtD_NotS_First(
			groupId, userId, displayDate, status, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append(", displayDate<");
		sb.append(displayDate);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_U_LtD_NotS_First(
		long groupId, long userId, Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		List<BlogsEntry> list = findByG_U_LtD_NotS(
			groupId, userId, displayDate, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last blogs entry in the ordered set where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByG_U_LtD_NotS_Last(
			long groupId, long userId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByG_U_LtD_NotS_Last(
			groupId, userId, displayDate, status, orderByComparator);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append(", displayDate<");
		sb.append(displayDate);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the last blogs entry in the ordered set where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByG_U_LtD_NotS_Last(
		long groupId, long userId, Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		int count = countByG_U_LtD_NotS(groupId, userId, displayDate, status);

		if (count == 0) {
			return null;
		}

		List<BlogsEntry> list = findByG_U_LtD_NotS(
			groupId, userId, displayDate, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the blogs entries before and after the current blogs entry in the ordered set where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param entryId the primary key of the current blogs entry
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry[] findByG_U_LtD_NotS_PrevAndNext(
			long entryId, long groupId, long userId, Date displayDate,
			int status, OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			BlogsEntry[] array = new BlogsEntryImpl[3];

			array[0] = getByG_U_LtD_NotS_PrevAndNext(
				session, blogsEntry, groupId, userId, displayDate, status,
				orderByComparator, true);

			array[1] = blogsEntry;

			array[2] = getByG_U_LtD_NotS_PrevAndNext(
				session, blogsEntry, groupId, userId, displayDate, status,
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

	protected BlogsEntry getByG_U_LtD_NotS_PrevAndNext(
		Session session, BlogsEntry blogsEntry, long groupId, long userId,
		Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_U_LTD_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_LTD_NOTS_USERID_2);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_G_U_LTD_NOTS_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_G_U_LTD_NOTS_DISPLAYDATE_2);
		}

		sb.append(_FINDER_COLUMN_G_U_LTD_NOTS_STATUS_2);

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
			sb.append(BlogsEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(userId);

		if (bindDisplayDate) {
			queryPos.add(new Timestamp(displayDate.getTime()));
		}

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(blogsEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BlogsEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_U_LtD_NotS(
		long groupId, long userId, Date displayDate, int status) {

		return filterFindByG_U_LtD_NotS(
			groupId, userId, displayDate, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_U_LtD_NotS(
		long groupId, long userId, Date displayDate, int status, int start,
		int end) {

		return filterFindByG_U_LtD_NotS(
			groupId, userId, displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	@Override
	public List<BlogsEntry> filterFindByG_U_LtD_NotS(
		long groupId, long userId, Date displayDate, int status, int start,
		int end, OrderByComparator<BlogsEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_U_LtD_NotS(
				groupId, userId, displayDate, status, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_U_LtD_NotS(
					groupId, userId, displayDate, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
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
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_LTD_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_LTD_NOTS_USERID_2);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_G_U_LTD_NOTS_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_G_U_LTD_NOTS_DISPLAYDATE_2);
		}

		sb.append(_FINDER_COLUMN_G_U_LTD_NOTS_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, BlogsEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, BlogsEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(userId);

			if (bindDisplayDate) {
				queryPos.add(new Timestamp(displayDate.getTime()));
			}

			queryPos.add(status);

			return (List<BlogsEntry>)QueryUtil.list(
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
	 * Returns the blogs entries before and after the current blogs entry in the ordered set of blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param entryId the primary key of the current blogs entry
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry[] filterFindByG_U_LtD_NotS_PrevAndNext(
			long entryId, long groupId, long userId, Date displayDate,
			int status, OrderByComparator<BlogsEntry> orderByComparator)
		throws NoSuchEntryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_U_LtD_NotS_PrevAndNext(
				entryId, groupId, userId, displayDate, status,
				orderByComparator);
		}

		BlogsEntry blogsEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			BlogsEntry[] array = new BlogsEntryImpl[3];

			array[0] = filterGetByG_U_LtD_NotS_PrevAndNext(
				session, blogsEntry, groupId, userId, displayDate, status,
				orderByComparator, true);

			array[1] = blogsEntry;

			array[2] = filterGetByG_U_LtD_NotS_PrevAndNext(
				session, blogsEntry, groupId, userId, displayDate, status,
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

	protected BlogsEntry filterGetByG_U_LtD_NotS_PrevAndNext(
		Session session, BlogsEntry blogsEntry, long groupId, long userId,
		Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_LTD_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_LTD_NOTS_USERID_2);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_G_U_LTD_NOTS_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_G_U_LTD_NOTS_DISPLAYDATE_2);
		}

		sb.append(_FINDER_COLUMN_G_U_LTD_NOTS_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BlogsEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, BlogsEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, BlogsEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(userId);

		if (bindDisplayDate) {
			queryPos.add(new Timestamp(displayDate.getTime()));
		}

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(blogsEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BlogsEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 */
	@Override
	public void removeByG_U_LtD_NotS(
		long groupId, long userId, Date displayDate, int status) {

		for (BlogsEntry blogsEntry :
				findByG_U_LtD_NotS(
					groupId, userId, displayDate, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(blogsEntry);
		}
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByG_U_LtD_NotS(
		long groupId, long userId, Date displayDate, int status) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			FinderPath finderPath =
				_finderPathWithPaginationCountByG_U_LtD_NotS;

			Object[] finderArgs = new Object[] {
				groupId, userId, _getTime(displayDate), status
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_BLOGSENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_U_LTD_NOTS_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_U_LTD_NOTS_USERID_2);

				boolean bindDisplayDate = false;

				if (displayDate == null) {
					sb.append(_FINDER_COLUMN_G_U_LTD_NOTS_DISPLAYDATE_1);
				}
				else {
					bindDisplayDate = true;

					sb.append(_FINDER_COLUMN_G_U_LTD_NOTS_DISPLAYDATE_2);
				}

				sb.append(_FINDER_COLUMN_G_U_LTD_NOTS_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(userId);

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
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_U_LtD_NotS(
		long groupId, long userId, Date displayDate, int status) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_U_LtD_NotS(groupId, userId, displayDate, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<BlogsEntry> blogsEntries = findByG_U_LtD_NotS(
				groupId, userId, displayDate, status);

			blogsEntries = InlineSQLHelperUtil.filter(blogsEntries, groupId);

			return blogsEntries.size();
		}

		StringBundler sb = new StringBundler(5);

		sb.append(_FILTER_SQL_COUNT_BLOGSENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_U_LTD_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_LTD_NOTS_USERID_2);

		boolean bindDisplayDate = false;

		if (displayDate == null) {
			sb.append(_FINDER_COLUMN_G_U_LTD_NOTS_DISPLAYDATE_1);
		}
		else {
			bindDisplayDate = true;

			sb.append(_FINDER_COLUMN_G_U_LTD_NOTS_DISPLAYDATE_2);
		}

		sb.append(_FINDER_COLUMN_G_U_LTD_NOTS_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BlogsEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(userId);

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

	private static final String _FINDER_COLUMN_G_U_LTD_NOTS_GROUPID_2 =
		"blogsEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_U_LTD_NOTS_USERID_2 =
		"blogsEntry.userId = ? AND ";

	private static final String _FINDER_COLUMN_G_U_LTD_NOTS_DISPLAYDATE_1 =
		"blogsEntry.displayDate IS NULL AND ";

	private static final String _FINDER_COLUMN_G_U_LTD_NOTS_DISPLAYDATE_2 =
		"blogsEntry.displayDate < ? AND ";

	private static final String _FINDER_COLUMN_G_U_LTD_NOTS_STATUS_2 =
		"blogsEntry.status != ?";

	private FinderPath _finderPathFetchByERC_G;

	/**
	 * Returns the blogs entry where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry findByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByERC_G(externalReferenceCode, groupId);

		if (blogsEntry == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("externalReferenceCode=");
			sb.append(externalReferenceCode);

			sb.append(", groupId=");
			sb.append(groupId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchEntryException(sb.toString());
		}

		return blogsEntry;
	}

	/**
	 * Returns the blogs entry where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByERC_G(String externalReferenceCode, long groupId) {
		return fetchByERC_G(externalReferenceCode, groupId, true);
	}

	/**
	 * Returns the blogs entry where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			externalReferenceCode = Objects.toString(externalReferenceCode, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {externalReferenceCode, groupId};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByERC_G, finderArgs, this);
			}

			if (result instanceof BlogsEntry) {
				BlogsEntry blogsEntry = (BlogsEntry)result;

				if (!Objects.equals(
						externalReferenceCode,
						blogsEntry.getExternalReferenceCode()) ||
					(groupId != blogsEntry.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_BLOGSENTRY_WHERE);

				boolean bindExternalReferenceCode = false;

				if (externalReferenceCode.isEmpty()) {
					sb.append(_FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_3);
				}
				else {
					bindExternalReferenceCode = true;

					sb.append(_FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_2);
				}

				sb.append(_FINDER_COLUMN_ERC_G_GROUPID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindExternalReferenceCode) {
						queryPos.add(externalReferenceCode);
					}

					queryPos.add(groupId);

					List<BlogsEntry> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByERC_G, finderArgs, list);
						}
					}
					else {
						BlogsEntry blogsEntry = list.get(0);

						result = blogsEntry;

						cacheResult(blogsEntry);
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
				return (BlogsEntry)result;
			}
		}
	}

	/**
	 * Removes the blogs entry where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the blogs entry that was removed
	 */
	@Override
	public BlogsEntry removeByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = findByERC_G(externalReferenceCode, groupId);

		return remove(blogsEntry);
	}

	/**
	 * Returns the number of blogs entries where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching blogs entries
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		BlogsEntry blogsEntry = fetchByERC_G(externalReferenceCode, groupId);

		if (blogsEntry == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_2 =
		"blogsEntry.externalReferenceCode = ? AND ";

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_3 =
		"(blogsEntry.externalReferenceCode IS NULL OR blogsEntry.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_G_GROUPID_2 =
		"blogsEntry.groupId = ?";

	public BlogsEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(BlogsEntry.class);

		setModelImplClass(BlogsEntryImpl.class);
		setModelPKClass(long.class);

		setTable(BlogsEntryTable.INSTANCE);
	}

	/**
	 * Caches the blogs entry in the entity cache if it is enabled.
	 *
	 * @param blogsEntry the blogs entry
	 */
	@Override
	public void cacheResult(BlogsEntry blogsEntry) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					blogsEntry.getCtCollectionId())) {

			entityCache.putResult(
				BlogsEntryImpl.class, blogsEntry.getPrimaryKey(), blogsEntry);

			finderCache.putResult(
				_finderPathFetchByUUID_G,
				new Object[] {blogsEntry.getUuid(), blogsEntry.getGroupId()},
				blogsEntry);

			finderCache.putResult(
				_finderPathFetchByG_UT,
				new Object[] {
					blogsEntry.getGroupId(), blogsEntry.getUrlTitle()
				},
				blogsEntry);

			finderCache.putResult(
				_finderPathFetchByERC_G,
				new Object[] {
					blogsEntry.getExternalReferenceCode(),
					blogsEntry.getGroupId()
				},
				blogsEntry);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the blogs entries in the entity cache if it is enabled.
	 *
	 * @param blogsEntries the blogs entries
	 */
	@Override
	public void cacheResult(List<BlogsEntry> blogsEntries) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (blogsEntries.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (BlogsEntry blogsEntry : blogsEntries) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						blogsEntry.getCtCollectionId())) {

				if (entityCache.getResult(
						BlogsEntryImpl.class, blogsEntry.getPrimaryKey()) ==
							null) {

					cacheResult(blogsEntry);
				}
			}
		}
	}

	/**
	 * Clears the cache for all blogs entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(BlogsEntryImpl.class);

		finderCache.clearCache(BlogsEntryImpl.class);
	}

	/**
	 * Clears the cache for the blogs entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(BlogsEntry blogsEntry) {
		entityCache.removeResult(BlogsEntryImpl.class, blogsEntry);
	}

	@Override
	public void clearCache(List<BlogsEntry> blogsEntries) {
		for (BlogsEntry blogsEntry : blogsEntries) {
			entityCache.removeResult(BlogsEntryImpl.class, blogsEntry);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(BlogsEntryImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(BlogsEntryImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		BlogsEntryModelImpl blogsEntryModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					blogsEntryModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				blogsEntryModelImpl.getUuid(), blogsEntryModelImpl.getGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByUUID_G, args, blogsEntryModelImpl);

			args = new Object[] {
				blogsEntryModelImpl.getGroupId(),
				blogsEntryModelImpl.getUrlTitle()
			};

			finderCache.putResult(
				_finderPathFetchByG_UT, args, blogsEntryModelImpl);

			args = new Object[] {
				blogsEntryModelImpl.getExternalReferenceCode(),
				blogsEntryModelImpl.getGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByERC_G, args, blogsEntryModelImpl);
		}
	}

	/**
	 * Creates a new blogs entry with the primary key. Does not add the blogs entry to the database.
	 *
	 * @param entryId the primary key for the new blogs entry
	 * @return the new blogs entry
	 */
	@Override
	public BlogsEntry create(long entryId) {
		BlogsEntry blogsEntry = new BlogsEntryImpl();

		blogsEntry.setNew(true);
		blogsEntry.setPrimaryKey(entryId);

		String uuid = PortalUUIDUtil.generate();

		blogsEntry.setUuid(uuid);

		blogsEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return blogsEntry;
	}

	/**
	 * Removes the blogs entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param entryId the primary key of the blogs entry
	 * @return the blogs entry that was removed
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry remove(long entryId) throws NoSuchEntryException {
		return remove((Serializable)entryId);
	}

	/**
	 * Removes the blogs entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the blogs entry
	 * @return the blogs entry that was removed
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry remove(Serializable primaryKey)
		throws NoSuchEntryException {

		Session session = null;

		try {
			session = openSession();

			BlogsEntry blogsEntry = (BlogsEntry)session.get(
				BlogsEntryImpl.class, primaryKey);

			if (blogsEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(blogsEntry);
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
	protected BlogsEntry removeImpl(BlogsEntry blogsEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(blogsEntry)) {
				blogsEntry = (BlogsEntry)session.get(
					BlogsEntryImpl.class, blogsEntry.getPrimaryKeyObj());
			}

			if ((blogsEntry != null) &&
				ctPersistenceHelper.isRemove(blogsEntry)) {

				session.delete(blogsEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (blogsEntry != null) {
			clearCache(blogsEntry);
		}

		return blogsEntry;
	}

	@Override
	public BlogsEntry updateImpl(BlogsEntry blogsEntry) {
		boolean isNew = blogsEntry.isNew();

		if (!(blogsEntry instanceof BlogsEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(blogsEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(blogsEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in blogsEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom BlogsEntry implementation " +
					blogsEntry.getClass());
		}

		BlogsEntryModelImpl blogsEntryModelImpl =
			(BlogsEntryModelImpl)blogsEntry;

		if (Validator.isNull(blogsEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			blogsEntry.setUuid(uuid);
		}

		if (Validator.isNull(blogsEntry.getExternalReferenceCode())) {
			blogsEntry.setExternalReferenceCode(blogsEntry.getUuid());
		}
		else {
			if (!Objects.equals(
					blogsEntryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					blogsEntry.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = blogsEntry.getCompanyId();

					long groupId = blogsEntry.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = blogsEntry.getPrimaryKey();
					}

					try {
						blogsEntry.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								BlogsEntry.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								blogsEntry.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			BlogsEntry ercBlogsEntry = fetchByERC_G(
				blogsEntry.getExternalReferenceCode(), blogsEntry.getGroupId());

			if (isNew) {
				if (ercBlogsEntry != null) {
					throw new DuplicateBlogsEntryExternalReferenceCodeException(
						"Duplicate blogs entry with external reference code " +
							blogsEntry.getExternalReferenceCode() +
								" and group " + blogsEntry.getGroupId());
				}
			}
			else {
				if ((ercBlogsEntry != null) &&
					(blogsEntry.getEntryId() != ercBlogsEntry.getEntryId())) {

					throw new DuplicateBlogsEntryExternalReferenceCodeException(
						"Duplicate blogs entry with external reference code " +
							blogsEntry.getExternalReferenceCode() +
								" and group " + blogsEntry.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (blogsEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				blogsEntry.setCreateDate(date);
			}
			else {
				blogsEntry.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!blogsEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				blogsEntry.setModifiedDate(date);
			}
			else {
				blogsEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		long userId = GetterUtil.getLong(PrincipalThreadLocal.getName());

		if (userId > 0) {
			long companyId = blogsEntry.getCompanyId();

			long groupId = blogsEntry.getGroupId();

			long entryId = 0;

			if (!isNew) {
				entryId = blogsEntry.getPrimaryKey();
			}

			try {
				blogsEntry.setTitle(
					SanitizerUtil.sanitize(
						companyId, groupId, userId, BlogsEntry.class.getName(),
						entryId, ContentTypes.TEXT_PLAIN, Sanitizer.MODE_ALL,
						blogsEntry.getTitle(), null));

				blogsEntry.setContent(
					SanitizerUtil.sanitize(
						companyId, groupId, userId, BlogsEntry.class.getName(),
						entryId, ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
						blogsEntry.getContent(), null));

				blogsEntry.setCoverImageCaption(
					SanitizerUtil.sanitize(
						companyId, groupId, userId, BlogsEntry.class.getName(),
						entryId, ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
						blogsEntry.getCoverImageCaption(), null));
			}
			catch (SanitizerException sanitizerException) {
				throw new SystemException(sanitizerException);
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(blogsEntry)) {
				if (!isNew) {
					session.evict(
						BlogsEntryImpl.class, blogsEntry.getPrimaryKeyObj());
				}

				session.save(blogsEntry);
			}
			else {
				blogsEntry = (BlogsEntry)session.merge(blogsEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			BlogsEntryImpl.class, blogsEntryModelImpl, false, true);

		cacheUniqueFindersCache(blogsEntryModelImpl);

		if (isNew) {
			blogsEntry.setNew(false);
		}

		blogsEntry.resetOriginalValues();

		return blogsEntry;
	}

	/**
	 * Returns the blogs entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the blogs entry
	 * @return the blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchEntryException {

		BlogsEntry blogsEntry = fetchByPrimaryKey(primaryKey);

		if (blogsEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return blogsEntry;
	}

	/**
	 * Returns the blogs entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param entryId the primary key of the blogs entry
	 * @return the blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry findByPrimaryKey(long entryId)
		throws NoSuchEntryException {

		return findByPrimaryKey((Serializable)entryId);
	}

	/**
	 * Returns the blogs entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the blogs entry
	 * @return the blogs entry, or <code>null</code> if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry fetchByPrimaryKey(Serializable primaryKey) {
		if (ctPersistenceHelper.isProductionMode(
				BlogsEntry.class, primaryKey)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		BlogsEntry blogsEntry = (BlogsEntry)entityCache.getResult(
			BlogsEntryImpl.class, primaryKey);

		if (blogsEntry != null) {
			return blogsEntry;
		}

		Session session = null;

		try {
			session = openSession();

			blogsEntry = (BlogsEntry)session.get(
				BlogsEntryImpl.class, primaryKey);

			if (blogsEntry != null) {
				cacheResult(blogsEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return blogsEntry;
	}

	/**
	 * Returns the blogs entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param entryId the primary key of the blogs entry
	 * @return the blogs entry, or <code>null</code> if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry fetchByPrimaryKey(long entryId) {
		return fetchByPrimaryKey((Serializable)entryId);
	}

	@Override
	public Map<Serializable, BlogsEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (ctPersistenceHelper.isProductionMode(BlogsEntry.class)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, BlogsEntry> map =
			new HashMap<Serializable, BlogsEntry>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			BlogsEntry blogsEntry = fetchByPrimaryKey(primaryKey);

			if (blogsEntry != null) {
				map.put(primaryKey, blogsEntry);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
						BlogsEntry.class, primaryKey)) {

				BlogsEntry blogsEntry = (BlogsEntry)entityCache.getResult(
					BlogsEntryImpl.class, primaryKey);

				if (blogsEntry == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, blogsEntry);
				}
			}
		}

		if (uncachedPrimaryKeys == null) {
			return map;
		}

		if ((databaseInMaxParameters > 0) &&
			(primaryKeys.size() > databaseInMaxParameters)) {

			Iterator<Serializable> iterator = primaryKeys.iterator();

			while (iterator.hasNext()) {
				Set<Serializable> page = new HashSet<>();

				for (int i = 0;
					 (i < databaseInMaxParameters) && iterator.hasNext(); i++) {

					page.add(iterator.next());
				}

				map.putAll(fetchByPrimaryKeys(page));
			}

			return map;
		}

		StringBundler sb = new StringBundler((primaryKeys.size() * 2) + 1);

		sb.append(getSelectSQL());
		sb.append(" WHERE ");
		sb.append(getPKDBName());
		sb.append(" IN (");

		for (Serializable primaryKey : primaryKeys) {
			sb.append((long)primaryKey);

			sb.append(",");
		}

		sb.setIndex(sb.index() - 1);

		sb.append(")");

		String sql = sb.toString();

		Session session = null;

		try {
			session = openSession();

			Query query = session.createQuery(sql);

			for (BlogsEntry blogsEntry : (List<BlogsEntry>)query.list()) {
				map.put(blogsEntry.getPrimaryKeyObj(), blogsEntry);

				cacheResult(blogsEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return map;
	}

	/**
	 * Returns all the blogs entries.
	 *
	 * @return the blogs entries
	 */
	@Override
	public List<BlogsEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the blogs entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of blogs entries
	 */
	@Override
	public List<BlogsEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the blogs entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of blogs entries
	 */
	@Override
	public List<BlogsEntry> findAll(
		int start, int end, OrderByComparator<BlogsEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the blogs entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of blogs entries
	 */
	@Override
	public List<BlogsEntry> findAll(
		int start, int end, OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

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

			List<BlogsEntry> list = null;

			if (useFinderCache) {
				list = (List<BlogsEntry>)finderCache.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_BLOGSENTRY);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_BLOGSENTRY;

					sql = sql.concat(BlogsEntryModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<BlogsEntry>)QueryUtil.list(
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
	}

	/**
	 * Removes all the blogs entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (BlogsEntry blogsEntry : findAll()) {
			remove(blogsEntry);
		}
	}

	/**
	 * Returns the number of blogs entries.
	 *
	 * @return the number of blogs entries
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BlogsEntry.class)) {

			Long count = (Long)finderCache.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(_SQL_COUNT_BLOGSENTRY);

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
		return "entryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_BLOGSENTRY;
	}

	@Override
	public Set<String> getCTColumnNames(
		CTColumnResolutionType ctColumnResolutionType) {

		return _ctColumnNamesMap.getOrDefault(
			ctColumnResolutionType, Collections.emptySet());
	}

	@Override
	public List<String> getMappingTableNames() {
		return _mappingTableNames;
	}

	@Override
	public Map<String, Integer> getTableColumnsMap() {
		return BlogsEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "BlogsEntry";
	}

	@Override
	public List<String[]> getUniqueIndexColumnNames() {
		return _uniqueIndexColumnNames;
	}

	private static final Map<CTColumnResolutionType, Set<String>>
		_ctColumnNamesMap = new EnumMap<CTColumnResolutionType, Set<String>>(
			CTColumnResolutionType.class);
	private static final List<String> _mappingTableNames =
		new ArrayList<String>();
	private static final List<String[]> _uniqueIndexColumnNames =
		new ArrayList<String[]>();

	static {
		Set<String> ctControlColumnNames = new HashSet<String>();
		Set<String> ctIgnoreColumnNames = new HashSet<String>();
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("uuid_");
		ctStrictColumnNames.add("externalReferenceCode");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("title");
		ctMergeColumnNames.add("subtitle");
		ctMergeColumnNames.add("urlTitle");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("content");
		ctMergeColumnNames.add("displayDate");
		ctMergeColumnNames.add("allowPingbacks");
		ctMergeColumnNames.add("allowTrackbacks");
		ctMergeColumnNames.add("trackbacks");
		ctMergeColumnNames.add("coverImageCaption");
		ctMergeColumnNames.add("coverImageFileEntryId");
		ctMergeColumnNames.add("coverImageURL");
		ctMergeColumnNames.add("smallImage");
		ctMergeColumnNames.add("smallImageFileEntryId");
		ctMergeColumnNames.add("smallImageId");
		ctMergeColumnNames.add("smallImageURL");
		ctMergeColumnNames.add("lastPublishDate");
		ctMergeColumnNames.add("status");
		ctMergeColumnNames.add("statusByUserId");
		ctMergeColumnNames.add("statusByUserName");
		ctMergeColumnNames.add("statusDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("entryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(new String[] {"groupId", "urlTitle"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the blogs entry persistence.
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

		_finderPathFetchByG_UT = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByG_UT",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"groupId", "urlTitle"}, true);

		_finderPathWithPaginationFindByG_LtD = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_LtD",
			new String[] {
				Long.class.getName(), Date.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "displayDate"}, true);

		_finderPathWithPaginationCountByG_LtD = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_LtD",
			new String[] {Long.class.getName(), Date.class.getName()},
			new String[] {"groupId", "displayDate"}, false);

		_finderPathWithPaginationFindByG_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_S",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "status"}, true);

		_finderPathWithoutPaginationFindByG_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_S",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"groupId", "status"}, true);

		_finderPathCountByG_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_S",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"groupId", "status"}, false);

		_finderPathWithPaginationFindByG_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_NotS",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "status"}, true);

		_finderPathWithPaginationCountByG_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_NotS",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"groupId", "status"}, false);

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

		_finderPathWithPaginationFindByC_LtD = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_LtD",
			new String[] {
				Long.class.getName(), Date.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "displayDate"}, true);

		_finderPathWithPaginationCountByC_LtD = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_LtD",
			new String[] {Long.class.getName(), Date.class.getName()},
			new String[] {"companyId", "displayDate"}, false);

		_finderPathWithPaginationFindByC_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_S",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "status"}, true);

		_finderPathWithoutPaginationFindByC_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_S",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"companyId", "status"}, true);

		_finderPathCountByC_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_S",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"companyId", "status"}, false);

		_finderPathWithPaginationFindByC_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_NotS",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "status"}, true);

		_finderPathWithPaginationCountByC_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_NotS",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"companyId", "status"}, false);

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

		_finderPathWithPaginationFindByG_U_LtD = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_U_LtD",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Date.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "userId", "displayDate"}, true);

		_finderPathWithPaginationCountByG_U_LtD = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_U_LtD",
			new String[] {
				Long.class.getName(), Long.class.getName(), Date.class.getName()
			},
			new String[] {"groupId", "userId", "displayDate"}, false);

		_finderPathWithPaginationFindByG_U_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_U_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "userId", "status"}, true);

		_finderPathWithoutPaginationFindByG_U_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_U_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "userId", "status"}, true);

		_finderPathCountByG_U_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_U_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "userId", "status"}, false);

		_finderPathWithPaginationCountByG_U_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_U_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "userId", "status"}, false);

		_finderPathWithPaginationFindByG_U_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_U_NotS",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "userId", "status"}, true);

		_finderPathWithPaginationCountByG_U_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_U_NotS",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "userId", "status"}, false);

		_finderPathWithPaginationFindByG_D_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_D_S",
			new String[] {
				Long.class.getName(), Date.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "displayDate", "status"}, true);

		_finderPathWithoutPaginationFindByG_D_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_D_S",
			new String[] {
				Long.class.getName(), Date.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "displayDate", "status"}, true);

		_finderPathCountByG_D_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_D_S",
			new String[] {
				Long.class.getName(), Date.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "displayDate", "status"}, false);

		_finderPathWithPaginationFindByG_GtD_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_GtD_S",
			new String[] {
				Long.class.getName(), Date.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "displayDate", "status"}, true);

		_finderPathWithPaginationCountByG_GtD_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_GtD_S",
			new String[] {
				Long.class.getName(), Date.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "displayDate", "status"}, false);

		_finderPathWithPaginationFindByG_LtD_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_LtD_S",
			new String[] {
				Long.class.getName(), Date.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "displayDate", "status"}, true);

		_finderPathWithPaginationCountByG_LtD_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_LtD_S",
			new String[] {
				Long.class.getName(), Date.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "displayDate", "status"}, false);

		_finderPathWithPaginationFindByG_LtD_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_LtD_NotS",
			new String[] {
				Long.class.getName(), Date.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "displayDate", "status"}, true);

		_finderPathWithPaginationCountByG_LtD_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_LtD_NotS",
			new String[] {
				Long.class.getName(), Date.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "displayDate", "status"}, false);

		_finderPathWithPaginationFindByC_U_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_U_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"companyId", "userId", "status"}, true);

		_finderPathWithoutPaginationFindByC_U_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_U_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"companyId", "userId", "status"}, true);

		_finderPathCountByC_U_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_U_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"companyId", "userId", "status"}, false);

		_finderPathWithPaginationFindByC_U_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_U_NotS",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"companyId", "userId", "status"}, true);

		_finderPathWithPaginationCountByC_U_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_U_NotS",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"companyId", "userId", "status"}, false);

		_finderPathWithPaginationFindByC_LtD_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_LtD_S",
			new String[] {
				Long.class.getName(), Date.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"companyId", "displayDate", "status"}, true);

		_finderPathWithPaginationCountByC_LtD_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_LtD_S",
			new String[] {
				Long.class.getName(), Date.class.getName(),
				Integer.class.getName()
			},
			new String[] {"companyId", "displayDate", "status"}, false);

		_finderPathWithPaginationFindByC_LtD_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_LtD_NotS",
			new String[] {
				Long.class.getName(), Date.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"companyId", "displayDate", "status"}, true);

		_finderPathWithPaginationCountByC_LtD_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_LtD_NotS",
			new String[] {
				Long.class.getName(), Date.class.getName(),
				Integer.class.getName()
			},
			new String[] {"companyId", "displayDate", "status"}, false);

		_finderPathWithPaginationFindByG_U_LtD_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_U_LtD_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Date.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "userId", "displayDate", "status"}, true);

		_finderPathWithPaginationCountByG_U_LtD_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_U_LtD_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Date.class.getName(), Integer.class.getName()
			},
			new String[] {"groupId", "userId", "displayDate", "status"}, false);

		_finderPathWithPaginationFindByG_U_LtD_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_U_LtD_NotS",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Date.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "userId", "displayDate", "status"}, true);

		_finderPathWithPaginationCountByG_U_LtD_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_U_LtD_NotS",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Date.class.getName(), Integer.class.getName()
			},
			new String[] {"groupId", "userId", "displayDate", "status"}, false);

		_finderPathFetchByERC_G = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"externalReferenceCode", "groupId"}, true);

		BlogsEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		BlogsEntryUtil.setPersistence(null);

		entityCache.removeCache(BlogsEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = BlogsPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = BlogsPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = BlogsPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected CTPersistenceHelper ctPersistenceHelper;

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

	private static final String _SQL_SELECT_BLOGSENTRY =
		"SELECT blogsEntry FROM BlogsEntry blogsEntry";

	private static final String _SQL_SELECT_BLOGSENTRY_WHERE =
		"SELECT blogsEntry FROM BlogsEntry blogsEntry WHERE ";

	private static final String _SQL_COUNT_BLOGSENTRY =
		"SELECT COUNT(blogsEntry) FROM BlogsEntry blogsEntry";

	private static final String _SQL_COUNT_BLOGSENTRY_WHERE =
		"SELECT COUNT(blogsEntry) FROM BlogsEntry blogsEntry WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"blogsEntry.entryId";

	private static final String _FILTER_SQL_SELECT_BLOGSENTRY_WHERE =
		"SELECT DISTINCT {blogsEntry.*} FROM BlogsEntry blogsEntry WHERE ";

	private static final String
		_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {BlogsEntry.*} FROM (SELECT DISTINCT blogsEntry.entryId FROM BlogsEntry blogsEntry WHERE ";

	private static final String
		_FILTER_SQL_SELECT_BLOGSENTRY_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN BlogsEntry ON TEMP_TABLE.entryId = BlogsEntry.entryId";

	private static final String _FILTER_SQL_COUNT_BLOGSENTRY_WHERE =
		"SELECT COUNT(DISTINCT blogsEntry.entryId) AS COUNT_VALUE FROM BlogsEntry blogsEntry WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "blogsEntry";

	private static final String _FILTER_ENTITY_TABLE = "BlogsEntry";

	private static final String _ORDER_BY_ENTITY_ALIAS = "blogsEntry.";

	private static final String _ORDER_BY_ENTITY_TABLE = "BlogsEntry.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No BlogsEntry exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No BlogsEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		BlogsEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}