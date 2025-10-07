/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.service.persistence.impl;

import com.liferay.bookmarks.exception.NoSuchFolderException;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.model.BookmarksFolderTable;
import com.liferay.bookmarks.model.impl.BookmarksFolderImpl;
import com.liferay.bookmarks.model.impl.BookmarksFolderModelImpl;
import com.liferay.bookmarks.service.persistence.BookmarksFolderPersistence;
import com.liferay.bookmarks.service.persistence.BookmarksFolderUtil;
import com.liferay.bookmarks.service.persistence.impl.constants.BookmarksPersistenceConstants;
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
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

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
 * The persistence implementation for the bookmarks folder service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = BookmarksFolderPersistence.class)
public class BookmarksFolderPersistenceImpl
	extends BasePersistenceImpl<BookmarksFolder>
	implements BookmarksFolderPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>BookmarksFolderUtil</code> to access the bookmarks folder persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		BookmarksFolderImpl.class.getName();

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
	 * Returns all the bookmarks folders where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the bookmarks folders where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @return the range of matching bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the bookmarks folders where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<BookmarksFolder> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the bookmarks folders where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<BookmarksFolder> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BookmarksFolder.class)) {

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

			List<BookmarksFolder> list = null;

			if (useFinderCache) {
				list = (List<BookmarksFolder>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BookmarksFolder bookmarksFolder : list) {
						if (!uuid.equals(bookmarksFolder.getUuid())) {
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

				sb.append(_SQL_SELECT_BOOKMARKSFOLDER_WHERE);

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
					sb.append(BookmarksFolderModelImpl.ORDER_BY_JPQL);
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

					list = (List<BookmarksFolder>)QueryUtil.list(
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
	 * Returns the first bookmarks folder in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching bookmarks folder
	 * @throws NoSuchFolderException if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder findByUuid_First(
			String uuid, OrderByComparator<BookmarksFolder> orderByComparator)
		throws NoSuchFolderException {

		BookmarksFolder bookmarksFolder = fetchByUuid_First(
			uuid, orderByComparator);

		if (bookmarksFolder != null) {
			return bookmarksFolder;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the first bookmarks folder in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching bookmarks folder, or <code>null</code> if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder fetchByUuid_First(
		String uuid, OrderByComparator<BookmarksFolder> orderByComparator) {

		List<BookmarksFolder> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last bookmarks folder in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching bookmarks folder
	 * @throws NoSuchFolderException if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder findByUuid_Last(
			String uuid, OrderByComparator<BookmarksFolder> orderByComparator)
		throws NoSuchFolderException {

		BookmarksFolder bookmarksFolder = fetchByUuid_Last(
			uuid, orderByComparator);

		if (bookmarksFolder != null) {
			return bookmarksFolder;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the last bookmarks folder in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching bookmarks folder, or <code>null</code> if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder fetchByUuid_Last(
		String uuid, OrderByComparator<BookmarksFolder> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<BookmarksFolder> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the bookmarks folders before and after the current bookmarks folder in the ordered set where uuid = &#63;.
	 *
	 * @param folderId the primary key of the current bookmarks folder
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next bookmarks folder
	 * @throws NoSuchFolderException if a bookmarks folder with the primary key could not be found
	 */
	@Override
	public BookmarksFolder[] findByUuid_PrevAndNext(
			long folderId, String uuid,
			OrderByComparator<BookmarksFolder> orderByComparator)
		throws NoSuchFolderException {

		uuid = Objects.toString(uuid, "");

		BookmarksFolder bookmarksFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			BookmarksFolder[] array = new BookmarksFolderImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, bookmarksFolder, uuid, orderByComparator, true);

			array[1] = bookmarksFolder;

			array[2] = getByUuid_PrevAndNext(
				session, bookmarksFolder, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected BookmarksFolder getByUuid_PrevAndNext(
		Session session, BookmarksFolder bookmarksFolder, String uuid,
		OrderByComparator<BookmarksFolder> orderByComparator,
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

		sb.append(_SQL_SELECT_BOOKMARKSFOLDER_WHERE);

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
			sb.append(BookmarksFolderModelImpl.ORDER_BY_JPQL);
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
						bookmarksFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BookmarksFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the bookmarks folders where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (BookmarksFolder bookmarksFolder :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(bookmarksFolder);
		}
	}

	/**
	 * Returns the number of bookmarks folders where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching bookmarks folders
	 */
	@Override
	public int countByUuid(String uuid) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BookmarksFolder.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid;

			Object[] finderArgs = new Object[] {uuid};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_BOOKMARKSFOLDER_WHERE);

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
		"bookmarksFolder.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(bookmarksFolder.uuid IS NULL OR bookmarksFolder.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;

	/**
	 * Returns the bookmarks folder where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchFolderException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching bookmarks folder
	 * @throws NoSuchFolderException if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder findByUUID_G(String uuid, long groupId)
		throws NoSuchFolderException {

		BookmarksFolder bookmarksFolder = fetchByUUID_G(uuid, groupId);

		if (bookmarksFolder == null) {
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

			throw new NoSuchFolderException(sb.toString());
		}

		return bookmarksFolder;
	}

	/**
	 * Returns the bookmarks folder where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching bookmarks folder, or <code>null</code> if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the bookmarks folder where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching bookmarks folder, or <code>null</code> if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BookmarksFolder.class)) {

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

			if (result instanceof BookmarksFolder) {
				BookmarksFolder bookmarksFolder = (BookmarksFolder)result;

				if (!Objects.equals(uuid, bookmarksFolder.getUuid()) ||
					(groupId != bookmarksFolder.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_BOOKMARKSFOLDER_WHERE);

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

					List<BookmarksFolder> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByUUID_G, finderArgs, list);
						}
					}
					else {
						BookmarksFolder bookmarksFolder = list.get(0);

						result = bookmarksFolder;

						cacheResult(bookmarksFolder);
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
				return (BookmarksFolder)result;
			}
		}
	}

	/**
	 * Removes the bookmarks folder where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the bookmarks folder that was removed
	 */
	@Override
	public BookmarksFolder removeByUUID_G(String uuid, long groupId)
		throws NoSuchFolderException {

		BookmarksFolder bookmarksFolder = findByUUID_G(uuid, groupId);

		return remove(bookmarksFolder);
	}

	/**
	 * Returns the number of bookmarks folders where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching bookmarks folders
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		BookmarksFolder bookmarksFolder = fetchByUUID_G(uuid, groupId);

		if (bookmarksFolder == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"bookmarksFolder.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(bookmarksFolder.uuid IS NULL OR bookmarksFolder.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"bookmarksFolder.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the bookmarks folders where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the bookmarks folders where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @return the range of matching bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the bookmarks folders where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<BookmarksFolder> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the bookmarks folders where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<BookmarksFolder> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BookmarksFolder.class)) {

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

			List<BookmarksFolder> list = null;

			if (useFinderCache) {
				list = (List<BookmarksFolder>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BookmarksFolder bookmarksFolder : list) {
						if (!uuid.equals(bookmarksFolder.getUuid()) ||
							(companyId != bookmarksFolder.getCompanyId())) {

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

				sb.append(_SQL_SELECT_BOOKMARKSFOLDER_WHERE);

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
					sb.append(BookmarksFolderModelImpl.ORDER_BY_JPQL);
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

					list = (List<BookmarksFolder>)QueryUtil.list(
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
	 * Returns the first bookmarks folder in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching bookmarks folder
	 * @throws NoSuchFolderException if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<BookmarksFolder> orderByComparator)
		throws NoSuchFolderException {

		BookmarksFolder bookmarksFolder = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (bookmarksFolder != null) {
			return bookmarksFolder;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the first bookmarks folder in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching bookmarks folder, or <code>null</code> if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<BookmarksFolder> orderByComparator) {

		List<BookmarksFolder> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last bookmarks folder in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching bookmarks folder
	 * @throws NoSuchFolderException if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<BookmarksFolder> orderByComparator)
		throws NoSuchFolderException {

		BookmarksFolder bookmarksFolder = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (bookmarksFolder != null) {
			return bookmarksFolder;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the last bookmarks folder in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching bookmarks folder, or <code>null</code> if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<BookmarksFolder> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<BookmarksFolder> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the bookmarks folders before and after the current bookmarks folder in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param folderId the primary key of the current bookmarks folder
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next bookmarks folder
	 * @throws NoSuchFolderException if a bookmarks folder with the primary key could not be found
	 */
	@Override
	public BookmarksFolder[] findByUuid_C_PrevAndNext(
			long folderId, String uuid, long companyId,
			OrderByComparator<BookmarksFolder> orderByComparator)
		throws NoSuchFolderException {

		uuid = Objects.toString(uuid, "");

		BookmarksFolder bookmarksFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			BookmarksFolder[] array = new BookmarksFolderImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, bookmarksFolder, uuid, companyId, orderByComparator,
				true);

			array[1] = bookmarksFolder;

			array[2] = getByUuid_C_PrevAndNext(
				session, bookmarksFolder, uuid, companyId, orderByComparator,
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

	protected BookmarksFolder getByUuid_C_PrevAndNext(
		Session session, BookmarksFolder bookmarksFolder, String uuid,
		long companyId, OrderByComparator<BookmarksFolder> orderByComparator,
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

		sb.append(_SQL_SELECT_BOOKMARKSFOLDER_WHERE);

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
			sb.append(BookmarksFolderModelImpl.ORDER_BY_JPQL);
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
						bookmarksFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BookmarksFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the bookmarks folders where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (BookmarksFolder bookmarksFolder :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(bookmarksFolder);
		}
	}

	/**
	 * Returns the number of bookmarks folders where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching bookmarks folders
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BookmarksFolder.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid_C;

			Object[] finderArgs = new Object[] {uuid, companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_BOOKMARKSFOLDER_WHERE);

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
		"bookmarksFolder.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(bookmarksFolder.uuid IS NULL OR bookmarksFolder.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"bookmarksFolder.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByGroupId;
	private FinderPath _finderPathWithoutPaginationFindByGroupId;
	private FinderPath _finderPathCountByGroupId;

	/**
	 * Returns all the bookmarks folders where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the bookmarks folders where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @return the range of matching bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findByGroupId(
		long groupId, int start, int end) {

		return findByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the bookmarks folders where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<BookmarksFolder> orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the bookmarks folders where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<BookmarksFolder> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BookmarksFolder.class)) {

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

			List<BookmarksFolder> list = null;

			if (useFinderCache) {
				list = (List<BookmarksFolder>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BookmarksFolder bookmarksFolder : list) {
						if (groupId != bookmarksFolder.getGroupId()) {
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

				sb.append(_SQL_SELECT_BOOKMARKSFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BookmarksFolderModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					list = (List<BookmarksFolder>)QueryUtil.list(
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
	 * Returns the first bookmarks folder in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching bookmarks folder
	 * @throws NoSuchFolderException if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder findByGroupId_First(
			long groupId, OrderByComparator<BookmarksFolder> orderByComparator)
		throws NoSuchFolderException {

		BookmarksFolder bookmarksFolder = fetchByGroupId_First(
			groupId, orderByComparator);

		if (bookmarksFolder != null) {
			return bookmarksFolder;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the first bookmarks folder in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching bookmarks folder, or <code>null</code> if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder fetchByGroupId_First(
		long groupId, OrderByComparator<BookmarksFolder> orderByComparator) {

		List<BookmarksFolder> list = findByGroupId(
			groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last bookmarks folder in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching bookmarks folder
	 * @throws NoSuchFolderException if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder findByGroupId_Last(
			long groupId, OrderByComparator<BookmarksFolder> orderByComparator)
		throws NoSuchFolderException {

		BookmarksFolder bookmarksFolder = fetchByGroupId_Last(
			groupId, orderByComparator);

		if (bookmarksFolder != null) {
			return bookmarksFolder;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the last bookmarks folder in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching bookmarks folder, or <code>null</code> if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder fetchByGroupId_Last(
		long groupId, OrderByComparator<BookmarksFolder> orderByComparator) {

		int count = countByGroupId(groupId);

		if (count == 0) {
			return null;
		}

		List<BookmarksFolder> list = findByGroupId(
			groupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the bookmarks folders before and after the current bookmarks folder in the ordered set where groupId = &#63;.
	 *
	 * @param folderId the primary key of the current bookmarks folder
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next bookmarks folder
	 * @throws NoSuchFolderException if a bookmarks folder with the primary key could not be found
	 */
	@Override
	public BookmarksFolder[] findByGroupId_PrevAndNext(
			long folderId, long groupId,
			OrderByComparator<BookmarksFolder> orderByComparator)
		throws NoSuchFolderException {

		BookmarksFolder bookmarksFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			BookmarksFolder[] array = new BookmarksFolderImpl[3];

			array[0] = getByGroupId_PrevAndNext(
				session, bookmarksFolder, groupId, orderByComparator, true);

			array[1] = bookmarksFolder;

			array[2] = getByGroupId_PrevAndNext(
				session, bookmarksFolder, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected BookmarksFolder getByGroupId_PrevAndNext(
		Session session, BookmarksFolder bookmarksFolder, long groupId,
		OrderByComparator<BookmarksFolder> orderByComparator,
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

		sb.append(_SQL_SELECT_BOOKMARKSFOLDER_WHERE);

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
			sb.append(BookmarksFolderModelImpl.ORDER_BY_JPQL);
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
						bookmarksFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BookmarksFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the bookmarks folders that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching bookmarks folders that the user has permission to view
	 */
	@Override
	public List<BookmarksFolder> filterFindByGroupId(long groupId) {
		return filterFindByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the bookmarks folders that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @return the range of matching bookmarks folders that the user has permission to view
	 */
	@Override
	public List<BookmarksFolder> filterFindByGroupId(
		long groupId, int start, int end) {

		return filterFindByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the bookmarks folders that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching bookmarks folders that the user has permission to view
	 */
	@Override
	public List<BookmarksFolder> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<BookmarksFolder> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_BOOKMARKSFOLDER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_BOOKMARKSFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_BOOKMARKSFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
					BookmarksFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BookmarksFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BookmarksFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, BookmarksFolderImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, BookmarksFolderImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			return (List<BookmarksFolder>)QueryUtil.list(
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
	 * Returns the bookmarks folders before and after the current bookmarks folder in the ordered set of bookmarks folders that the user has permission to view where groupId = &#63;.
	 *
	 * @param folderId the primary key of the current bookmarks folder
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next bookmarks folder
	 * @throws NoSuchFolderException if a bookmarks folder with the primary key could not be found
	 */
	@Override
	public BookmarksFolder[] filterFindByGroupId_PrevAndNext(
			long folderId, long groupId,
			OrderByComparator<BookmarksFolder> orderByComparator)
		throws NoSuchFolderException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByGroupId_PrevAndNext(
				folderId, groupId, orderByComparator);
		}

		BookmarksFolder bookmarksFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			BookmarksFolder[] array = new BookmarksFolderImpl[3];

			array[0] = filterGetByGroupId_PrevAndNext(
				session, bookmarksFolder, groupId, orderByComparator, true);

			array[1] = bookmarksFolder;

			array[2] = filterGetByGroupId_PrevAndNext(
				session, bookmarksFolder, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected BookmarksFolder filterGetByGroupId_PrevAndNext(
		Session session, BookmarksFolder bookmarksFolder, long groupId,
		OrderByComparator<BookmarksFolder> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_BOOKMARKSFOLDER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_BOOKMARKSFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_BOOKMARKSFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
					BookmarksFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BookmarksFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BookmarksFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, BookmarksFolderImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, BookmarksFolderImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						bookmarksFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BookmarksFolder> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the bookmarks folders where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		for (BookmarksFolder bookmarksFolder :
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(bookmarksFolder);
		}
	}

	/**
	 * Returns the number of bookmarks folders where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching bookmarks folders
	 */
	@Override
	public int countByGroupId(long groupId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BookmarksFolder.class)) {

			FinderPath finderPath = _finderPathCountByGroupId;

			Object[] finderArgs = new Object[] {groupId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_BOOKMARKSFOLDER_WHERE);

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
	 * Returns the number of bookmarks folders that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching bookmarks folders that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByGroupId(groupId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<BookmarksFolder> bookmarksFolders = findByGroupId(groupId);

			bookmarksFolders = InlineSQLHelperUtil.filter(
				bookmarksFolders, groupId);

			return bookmarksFolders.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_BOOKMARKSFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BookmarksFolder.class.getName(),
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
		"bookmarksFolder.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;

	/**
	 * Returns all the bookmarks folders where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the bookmarks folders where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @return the range of matching bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the bookmarks folders where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<BookmarksFolder> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the bookmarks folders where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<BookmarksFolder> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BookmarksFolder.class)) {

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

			List<BookmarksFolder> list = null;

			if (useFinderCache) {
				list = (List<BookmarksFolder>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BookmarksFolder bookmarksFolder : list) {
						if (companyId != bookmarksFolder.getCompanyId()) {
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

				sb.append(_SQL_SELECT_BOOKMARKSFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BookmarksFolderModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					list = (List<BookmarksFolder>)QueryUtil.list(
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
	 * Returns the first bookmarks folder in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching bookmarks folder
	 * @throws NoSuchFolderException if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder findByCompanyId_First(
			long companyId,
			OrderByComparator<BookmarksFolder> orderByComparator)
		throws NoSuchFolderException {

		BookmarksFolder bookmarksFolder = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (bookmarksFolder != null) {
			return bookmarksFolder;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the first bookmarks folder in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching bookmarks folder, or <code>null</code> if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder fetchByCompanyId_First(
		long companyId, OrderByComparator<BookmarksFolder> orderByComparator) {

		List<BookmarksFolder> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last bookmarks folder in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching bookmarks folder
	 * @throws NoSuchFolderException if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder findByCompanyId_Last(
			long companyId,
			OrderByComparator<BookmarksFolder> orderByComparator)
		throws NoSuchFolderException {

		BookmarksFolder bookmarksFolder = fetchByCompanyId_Last(
			companyId, orderByComparator);

		if (bookmarksFolder != null) {
			return bookmarksFolder;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the last bookmarks folder in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching bookmarks folder, or <code>null</code> if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder fetchByCompanyId_Last(
		long companyId, OrderByComparator<BookmarksFolder> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<BookmarksFolder> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the bookmarks folders before and after the current bookmarks folder in the ordered set where companyId = &#63;.
	 *
	 * @param folderId the primary key of the current bookmarks folder
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next bookmarks folder
	 * @throws NoSuchFolderException if a bookmarks folder with the primary key could not be found
	 */
	@Override
	public BookmarksFolder[] findByCompanyId_PrevAndNext(
			long folderId, long companyId,
			OrderByComparator<BookmarksFolder> orderByComparator)
		throws NoSuchFolderException {

		BookmarksFolder bookmarksFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			BookmarksFolder[] array = new BookmarksFolderImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, bookmarksFolder, companyId, orderByComparator, true);

			array[1] = bookmarksFolder;

			array[2] = getByCompanyId_PrevAndNext(
				session, bookmarksFolder, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected BookmarksFolder getByCompanyId_PrevAndNext(
		Session session, BookmarksFolder bookmarksFolder, long companyId,
		OrderByComparator<BookmarksFolder> orderByComparator,
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

		sb.append(_SQL_SELECT_BOOKMARKSFOLDER_WHERE);

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
			sb.append(BookmarksFolderModelImpl.ORDER_BY_JPQL);
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
						bookmarksFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BookmarksFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the bookmarks folders where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (BookmarksFolder bookmarksFolder :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(bookmarksFolder);
		}
	}

	/**
	 * Returns the number of bookmarks folders where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching bookmarks folders
	 */
	@Override
	public int countByCompanyId(long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BookmarksFolder.class)) {

			FinderPath finderPath = _finderPathCountByCompanyId;

			Object[] finderArgs = new Object[] {companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_BOOKMARKSFOLDER_WHERE);

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
		"bookmarksFolder.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByG_P;
	private FinderPath _finderPathWithoutPaginationFindByG_P;
	private FinderPath _finderPathCountByG_P;

	/**
	 * Returns all the bookmarks folders where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @return the matching bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findByG_P(long groupId, long parentFolderId) {
		return findByG_P(
			groupId, parentFolderId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the bookmarks folders where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @return the range of matching bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findByG_P(
		long groupId, long parentFolderId, int start, int end) {

		return findByG_P(groupId, parentFolderId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the bookmarks folders where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findByG_P(
		long groupId, long parentFolderId, int start, int end,
		OrderByComparator<BookmarksFolder> orderByComparator) {

		return findByG_P(
			groupId, parentFolderId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the bookmarks folders where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findByG_P(
		long groupId, long parentFolderId, int start, int end,
		OrderByComparator<BookmarksFolder> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BookmarksFolder.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_P;
					finderArgs = new Object[] {groupId, parentFolderId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_P;
				finderArgs = new Object[] {
					groupId, parentFolderId, start, end, orderByComparator
				};
			}

			List<BookmarksFolder> list = null;

			if (useFinderCache) {
				list = (List<BookmarksFolder>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BookmarksFolder bookmarksFolder : list) {
						if ((groupId != bookmarksFolder.getGroupId()) ||
							(parentFolderId !=
								bookmarksFolder.getParentFolderId())) {

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

				sb.append(_SQL_SELECT_BOOKMARKSFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_PARENTFOLDERID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BookmarksFolderModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(parentFolderId);

					list = (List<BookmarksFolder>)QueryUtil.list(
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
	 * Returns the first bookmarks folder in the ordered set where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching bookmarks folder
	 * @throws NoSuchFolderException if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder findByG_P_First(
			long groupId, long parentFolderId,
			OrderByComparator<BookmarksFolder> orderByComparator)
		throws NoSuchFolderException {

		BookmarksFolder bookmarksFolder = fetchByG_P_First(
			groupId, parentFolderId, orderByComparator);

		if (bookmarksFolder != null) {
			return bookmarksFolder;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", parentFolderId=");
		sb.append(parentFolderId);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the first bookmarks folder in the ordered set where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching bookmarks folder, or <code>null</code> if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder fetchByG_P_First(
		long groupId, long parentFolderId,
		OrderByComparator<BookmarksFolder> orderByComparator) {

		List<BookmarksFolder> list = findByG_P(
			groupId, parentFolderId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last bookmarks folder in the ordered set where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching bookmarks folder
	 * @throws NoSuchFolderException if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder findByG_P_Last(
			long groupId, long parentFolderId,
			OrderByComparator<BookmarksFolder> orderByComparator)
		throws NoSuchFolderException {

		BookmarksFolder bookmarksFolder = fetchByG_P_Last(
			groupId, parentFolderId, orderByComparator);

		if (bookmarksFolder != null) {
			return bookmarksFolder;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", parentFolderId=");
		sb.append(parentFolderId);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the last bookmarks folder in the ordered set where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching bookmarks folder, or <code>null</code> if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder fetchByG_P_Last(
		long groupId, long parentFolderId,
		OrderByComparator<BookmarksFolder> orderByComparator) {

		int count = countByG_P(groupId, parentFolderId);

		if (count == 0) {
			return null;
		}

		List<BookmarksFolder> list = findByG_P(
			groupId, parentFolderId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the bookmarks folders before and after the current bookmarks folder in the ordered set where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param folderId the primary key of the current bookmarks folder
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next bookmarks folder
	 * @throws NoSuchFolderException if a bookmarks folder with the primary key could not be found
	 */
	@Override
	public BookmarksFolder[] findByG_P_PrevAndNext(
			long folderId, long groupId, long parentFolderId,
			OrderByComparator<BookmarksFolder> orderByComparator)
		throws NoSuchFolderException {

		BookmarksFolder bookmarksFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			BookmarksFolder[] array = new BookmarksFolderImpl[3];

			array[0] = getByG_P_PrevAndNext(
				session, bookmarksFolder, groupId, parentFolderId,
				orderByComparator, true);

			array[1] = bookmarksFolder;

			array[2] = getByG_P_PrevAndNext(
				session, bookmarksFolder, groupId, parentFolderId,
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

	protected BookmarksFolder getByG_P_PrevAndNext(
		Session session, BookmarksFolder bookmarksFolder, long groupId,
		long parentFolderId,
		OrderByComparator<BookmarksFolder> orderByComparator,
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

		sb.append(_SQL_SELECT_BOOKMARKSFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_PARENTFOLDERID_2);

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
			sb.append(BookmarksFolderModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(parentFolderId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						bookmarksFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BookmarksFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the bookmarks folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @return the matching bookmarks folders that the user has permission to view
	 */
	@Override
	public List<BookmarksFolder> filterFindByG_P(
		long groupId, long parentFolderId) {

		return filterFindByG_P(
			groupId, parentFolderId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the bookmarks folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @return the range of matching bookmarks folders that the user has permission to view
	 */
	@Override
	public List<BookmarksFolder> filterFindByG_P(
		long groupId, long parentFolderId, int start, int end) {

		return filterFindByG_P(groupId, parentFolderId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the bookmarks folders that the user has permissions to view where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching bookmarks folders that the user has permission to view
	 */
	@Override
	public List<BookmarksFolder> filterFindByG_P(
		long groupId, long parentFolderId, int start, int end,
		OrderByComparator<BookmarksFolder> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P(
				groupId, parentFolderId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P(
					groupId, parentFolderId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
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
			sb.append(_FILTER_SQL_SELECT_BOOKMARKSFOLDER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_BOOKMARKSFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_PARENTFOLDERID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_BOOKMARKSFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
					BookmarksFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BookmarksFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BookmarksFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, BookmarksFolderImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, BookmarksFolderImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(parentFolderId);

			return (List<BookmarksFolder>)QueryUtil.list(
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
	 * Returns the bookmarks folders before and after the current bookmarks folder in the ordered set of bookmarks folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param folderId the primary key of the current bookmarks folder
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next bookmarks folder
	 * @throws NoSuchFolderException if a bookmarks folder with the primary key could not be found
	 */
	@Override
	public BookmarksFolder[] filterFindByG_P_PrevAndNext(
			long folderId, long groupId, long parentFolderId,
			OrderByComparator<BookmarksFolder> orderByComparator)
		throws NoSuchFolderException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_PrevAndNext(
				folderId, groupId, parentFolderId, orderByComparator);
		}

		BookmarksFolder bookmarksFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			BookmarksFolder[] array = new BookmarksFolderImpl[3];

			array[0] = filterGetByG_P_PrevAndNext(
				session, bookmarksFolder, groupId, parentFolderId,
				orderByComparator, true);

			array[1] = bookmarksFolder;

			array[2] = filterGetByG_P_PrevAndNext(
				session, bookmarksFolder, groupId, parentFolderId,
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

	protected BookmarksFolder filterGetByG_P_PrevAndNext(
		Session session, BookmarksFolder bookmarksFolder, long groupId,
		long parentFolderId,
		OrderByComparator<BookmarksFolder> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_BOOKMARKSFOLDER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_BOOKMARKSFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_PARENTFOLDERID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_BOOKMARKSFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
					BookmarksFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BookmarksFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BookmarksFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, BookmarksFolderImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, BookmarksFolderImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(parentFolderId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						bookmarksFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BookmarksFolder> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the bookmarks folders where groupId = &#63; and parentFolderId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 */
	@Override
	public void removeByG_P(long groupId, long parentFolderId) {
		for (BookmarksFolder bookmarksFolder :
				findByG_P(
					groupId, parentFolderId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(bookmarksFolder);
		}
	}

	/**
	 * Returns the number of bookmarks folders where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @return the number of matching bookmarks folders
	 */
	@Override
	public int countByG_P(long groupId, long parentFolderId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BookmarksFolder.class)) {

			FinderPath finderPath = _finderPathCountByG_P;

			Object[] finderArgs = new Object[] {groupId, parentFolderId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_BOOKMARKSFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_PARENTFOLDERID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(parentFolderId);

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
	 * Returns the number of bookmarks folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @return the number of matching bookmarks folders that the user has permission to view
	 */
	@Override
	public int filterCountByG_P(long groupId, long parentFolderId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P(groupId, parentFolderId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<BookmarksFolder> bookmarksFolders = findByG_P(
				groupId, parentFolderId);

			bookmarksFolders = InlineSQLHelperUtil.filter(
				bookmarksFolders, groupId);

			return bookmarksFolders.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_BOOKMARKSFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_PARENTFOLDERID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BookmarksFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(parentFolderId);

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

	private static final String _FINDER_COLUMN_G_P_GROUPID_2 =
		"bookmarksFolder.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_PARENTFOLDERID_2 =
		"bookmarksFolder.parentFolderId = ?";

	private FinderPath _finderPathWithPaginationFindByC_NotS;
	private FinderPath _finderPathWithPaginationCountByC_NotS;

	/**
	 * Returns all the bookmarks folders where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the matching bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findByC_NotS(long companyId, int status) {
		return findByC_NotS(
			companyId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the bookmarks folders where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @return the range of matching bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findByC_NotS(
		long companyId, int status, int start, int end) {

		return findByC_NotS(companyId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the bookmarks folders where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findByC_NotS(
		long companyId, int status, int start, int end,
		OrderByComparator<BookmarksFolder> orderByComparator) {

		return findByC_NotS(
			companyId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the bookmarks folders where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findByC_NotS(
		long companyId, int status, int start, int end,
		OrderByComparator<BookmarksFolder> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BookmarksFolder.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByC_NotS;
			finderArgs = new Object[] {
				companyId, status, start, end, orderByComparator
			};

			List<BookmarksFolder> list = null;

			if (useFinderCache) {
				list = (List<BookmarksFolder>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BookmarksFolder bookmarksFolder : list) {
						if ((companyId != bookmarksFolder.getCompanyId()) ||
							(status == bookmarksFolder.getStatus())) {

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

				sb.append(_SQL_SELECT_BOOKMARKSFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_C_NOTS_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_NOTS_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BookmarksFolderModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(status);

					list = (List<BookmarksFolder>)QueryUtil.list(
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
	 * Returns the first bookmarks folder in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching bookmarks folder
	 * @throws NoSuchFolderException if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder findByC_NotS_First(
			long companyId, int status,
			OrderByComparator<BookmarksFolder> orderByComparator)
		throws NoSuchFolderException {

		BookmarksFolder bookmarksFolder = fetchByC_NotS_First(
			companyId, status, orderByComparator);

		if (bookmarksFolder != null) {
			return bookmarksFolder;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the first bookmarks folder in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching bookmarks folder, or <code>null</code> if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder fetchByC_NotS_First(
		long companyId, int status,
		OrderByComparator<BookmarksFolder> orderByComparator) {

		List<BookmarksFolder> list = findByC_NotS(
			companyId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last bookmarks folder in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching bookmarks folder
	 * @throws NoSuchFolderException if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder findByC_NotS_Last(
			long companyId, int status,
			OrderByComparator<BookmarksFolder> orderByComparator)
		throws NoSuchFolderException {

		BookmarksFolder bookmarksFolder = fetchByC_NotS_Last(
			companyId, status, orderByComparator);

		if (bookmarksFolder != null) {
			return bookmarksFolder;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the last bookmarks folder in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching bookmarks folder, or <code>null</code> if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder fetchByC_NotS_Last(
		long companyId, int status,
		OrderByComparator<BookmarksFolder> orderByComparator) {

		int count = countByC_NotS(companyId, status);

		if (count == 0) {
			return null;
		}

		List<BookmarksFolder> list = findByC_NotS(
			companyId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the bookmarks folders before and after the current bookmarks folder in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param folderId the primary key of the current bookmarks folder
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next bookmarks folder
	 * @throws NoSuchFolderException if a bookmarks folder with the primary key could not be found
	 */
	@Override
	public BookmarksFolder[] findByC_NotS_PrevAndNext(
			long folderId, long companyId, int status,
			OrderByComparator<BookmarksFolder> orderByComparator)
		throws NoSuchFolderException {

		BookmarksFolder bookmarksFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			BookmarksFolder[] array = new BookmarksFolderImpl[3];

			array[0] = getByC_NotS_PrevAndNext(
				session, bookmarksFolder, companyId, status, orderByComparator,
				true);

			array[1] = bookmarksFolder;

			array[2] = getByC_NotS_PrevAndNext(
				session, bookmarksFolder, companyId, status, orderByComparator,
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

	protected BookmarksFolder getByC_NotS_PrevAndNext(
		Session session, BookmarksFolder bookmarksFolder, long companyId,
		int status, OrderByComparator<BookmarksFolder> orderByComparator,
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

		sb.append(_SQL_SELECT_BOOKMARKSFOLDER_WHERE);

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
			sb.append(BookmarksFolderModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(
						bookmarksFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BookmarksFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the bookmarks folders where companyId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 */
	@Override
	public void removeByC_NotS(long companyId, int status) {
		for (BookmarksFolder bookmarksFolder :
				findByC_NotS(
					companyId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(bookmarksFolder);
		}
	}

	/**
	 * Returns the number of bookmarks folders where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching bookmarks folders
	 */
	@Override
	public int countByC_NotS(long companyId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BookmarksFolder.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByC_NotS;

			Object[] finderArgs = new Object[] {companyId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_BOOKMARKSFOLDER_WHERE);

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
		"bookmarksFolder.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_NOTS_STATUS_2 =
		"bookmarksFolder.status != ?";

	private FinderPath _finderPathWithPaginationFindByG_P_S;
	private FinderPath _finderPathWithoutPaginationFindByG_P_S;
	private FinderPath _finderPathCountByG_P_S;

	/**
	 * Returns all the bookmarks folders where groupId = &#63; and parentFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @return the matching bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findByG_P_S(
		long groupId, long parentFolderId, int status) {

		return findByG_P_S(
			groupId, parentFolderId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the bookmarks folders where groupId = &#63; and parentFolderId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @return the range of matching bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findByG_P_S(
		long groupId, long parentFolderId, int status, int start, int end) {

		return findByG_P_S(groupId, parentFolderId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the bookmarks folders where groupId = &#63; and parentFolderId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findByG_P_S(
		long groupId, long parentFolderId, int status, int start, int end,
		OrderByComparator<BookmarksFolder> orderByComparator) {

		return findByG_P_S(
			groupId, parentFolderId, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the bookmarks folders where groupId = &#63; and parentFolderId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findByG_P_S(
		long groupId, long parentFolderId, int status, int start, int end,
		OrderByComparator<BookmarksFolder> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BookmarksFolder.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_P_S;
					finderArgs = new Object[] {groupId, parentFolderId, status};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_P_S;
				finderArgs = new Object[] {
					groupId, parentFolderId, status, start, end,
					orderByComparator
				};
			}

			List<BookmarksFolder> list = null;

			if (useFinderCache) {
				list = (List<BookmarksFolder>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BookmarksFolder bookmarksFolder : list) {
						if ((groupId != bookmarksFolder.getGroupId()) ||
							(parentFolderId !=
								bookmarksFolder.getParentFolderId()) ||
							(status != bookmarksFolder.getStatus())) {

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

				sb.append(_SQL_SELECT_BOOKMARKSFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_G_P_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_S_PARENTFOLDERID_2);

				sb.append(_FINDER_COLUMN_G_P_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BookmarksFolderModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(parentFolderId);

					queryPos.add(status);

					list = (List<BookmarksFolder>)QueryUtil.list(
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
	 * Returns the first bookmarks folder in the ordered set where groupId = &#63; and parentFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching bookmarks folder
	 * @throws NoSuchFolderException if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder findByG_P_S_First(
			long groupId, long parentFolderId, int status,
			OrderByComparator<BookmarksFolder> orderByComparator)
		throws NoSuchFolderException {

		BookmarksFolder bookmarksFolder = fetchByG_P_S_First(
			groupId, parentFolderId, status, orderByComparator);

		if (bookmarksFolder != null) {
			return bookmarksFolder;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", parentFolderId=");
		sb.append(parentFolderId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the first bookmarks folder in the ordered set where groupId = &#63; and parentFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching bookmarks folder, or <code>null</code> if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder fetchByG_P_S_First(
		long groupId, long parentFolderId, int status,
		OrderByComparator<BookmarksFolder> orderByComparator) {

		List<BookmarksFolder> list = findByG_P_S(
			groupId, parentFolderId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last bookmarks folder in the ordered set where groupId = &#63; and parentFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching bookmarks folder
	 * @throws NoSuchFolderException if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder findByG_P_S_Last(
			long groupId, long parentFolderId, int status,
			OrderByComparator<BookmarksFolder> orderByComparator)
		throws NoSuchFolderException {

		BookmarksFolder bookmarksFolder = fetchByG_P_S_Last(
			groupId, parentFolderId, status, orderByComparator);

		if (bookmarksFolder != null) {
			return bookmarksFolder;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", parentFolderId=");
		sb.append(parentFolderId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the last bookmarks folder in the ordered set where groupId = &#63; and parentFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching bookmarks folder, or <code>null</code> if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder fetchByG_P_S_Last(
		long groupId, long parentFolderId, int status,
		OrderByComparator<BookmarksFolder> orderByComparator) {

		int count = countByG_P_S(groupId, parentFolderId, status);

		if (count == 0) {
			return null;
		}

		List<BookmarksFolder> list = findByG_P_S(
			groupId, parentFolderId, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the bookmarks folders before and after the current bookmarks folder in the ordered set where groupId = &#63; and parentFolderId = &#63; and status = &#63;.
	 *
	 * @param folderId the primary key of the current bookmarks folder
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next bookmarks folder
	 * @throws NoSuchFolderException if a bookmarks folder with the primary key could not be found
	 */
	@Override
	public BookmarksFolder[] findByG_P_S_PrevAndNext(
			long folderId, long groupId, long parentFolderId, int status,
			OrderByComparator<BookmarksFolder> orderByComparator)
		throws NoSuchFolderException {

		BookmarksFolder bookmarksFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			BookmarksFolder[] array = new BookmarksFolderImpl[3];

			array[0] = getByG_P_S_PrevAndNext(
				session, bookmarksFolder, groupId, parentFolderId, status,
				orderByComparator, true);

			array[1] = bookmarksFolder;

			array[2] = getByG_P_S_PrevAndNext(
				session, bookmarksFolder, groupId, parentFolderId, status,
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

	protected BookmarksFolder getByG_P_S_PrevAndNext(
		Session session, BookmarksFolder bookmarksFolder, long groupId,
		long parentFolderId, int status,
		OrderByComparator<BookmarksFolder> orderByComparator,
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

		sb.append(_SQL_SELECT_BOOKMARKSFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_G_P_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_S_PARENTFOLDERID_2);

		sb.append(_FINDER_COLUMN_G_P_S_STATUS_2);

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
			sb.append(BookmarksFolderModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(parentFolderId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						bookmarksFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BookmarksFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the bookmarks folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @return the matching bookmarks folders that the user has permission to view
	 */
	@Override
	public List<BookmarksFolder> filterFindByG_P_S(
		long groupId, long parentFolderId, int status) {

		return filterFindByG_P_S(
			groupId, parentFolderId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the bookmarks folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @return the range of matching bookmarks folders that the user has permission to view
	 */
	@Override
	public List<BookmarksFolder> filterFindByG_P_S(
		long groupId, long parentFolderId, int status, int start, int end) {

		return filterFindByG_P_S(
			groupId, parentFolderId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the bookmarks folders that the user has permissions to view where groupId = &#63; and parentFolderId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching bookmarks folders that the user has permission to view
	 */
	@Override
	public List<BookmarksFolder> filterFindByG_P_S(
		long groupId, long parentFolderId, int status, int start, int end,
		OrderByComparator<BookmarksFolder> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_S(
				groupId, parentFolderId, status, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P_S(
					groupId, parentFolderId, status, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_BOOKMARKSFOLDER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_BOOKMARKSFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_S_PARENTFOLDERID_2);

		sb.append(_FINDER_COLUMN_G_P_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_BOOKMARKSFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
					BookmarksFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BookmarksFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BookmarksFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, BookmarksFolderImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, BookmarksFolderImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(parentFolderId);

			queryPos.add(status);

			return (List<BookmarksFolder>)QueryUtil.list(
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
	 * Returns the bookmarks folders before and after the current bookmarks folder in the ordered set of bookmarks folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63; and status = &#63;.
	 *
	 * @param folderId the primary key of the current bookmarks folder
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next bookmarks folder
	 * @throws NoSuchFolderException if a bookmarks folder with the primary key could not be found
	 */
	@Override
	public BookmarksFolder[] filterFindByG_P_S_PrevAndNext(
			long folderId, long groupId, long parentFolderId, int status,
			OrderByComparator<BookmarksFolder> orderByComparator)
		throws NoSuchFolderException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_S_PrevAndNext(
				folderId, groupId, parentFolderId, status, orderByComparator);
		}

		BookmarksFolder bookmarksFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			BookmarksFolder[] array = new BookmarksFolderImpl[3];

			array[0] = filterGetByG_P_S_PrevAndNext(
				session, bookmarksFolder, groupId, parentFolderId, status,
				orderByComparator, true);

			array[1] = bookmarksFolder;

			array[2] = filterGetByG_P_S_PrevAndNext(
				session, bookmarksFolder, groupId, parentFolderId, status,
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

	protected BookmarksFolder filterGetByG_P_S_PrevAndNext(
		Session session, BookmarksFolder bookmarksFolder, long groupId,
		long parentFolderId, int status,
		OrderByComparator<BookmarksFolder> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_BOOKMARKSFOLDER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_BOOKMARKSFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_S_PARENTFOLDERID_2);

		sb.append(_FINDER_COLUMN_G_P_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_BOOKMARKSFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
					BookmarksFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BookmarksFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BookmarksFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, BookmarksFolderImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, BookmarksFolderImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(parentFolderId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						bookmarksFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BookmarksFolder> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the bookmarks folders where groupId = &#63; and parentFolderId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 */
	@Override
	public void removeByG_P_S(long groupId, long parentFolderId, int status) {
		for (BookmarksFolder bookmarksFolder :
				findByG_P_S(
					groupId, parentFolderId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(bookmarksFolder);
		}
	}

	/**
	 * Returns the number of bookmarks folders where groupId = &#63; and parentFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @return the number of matching bookmarks folders
	 */
	@Override
	public int countByG_P_S(long groupId, long parentFolderId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BookmarksFolder.class)) {

			FinderPath finderPath = _finderPathCountByG_P_S;

			Object[] finderArgs = new Object[] {
				groupId, parentFolderId, status
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_BOOKMARKSFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_G_P_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_S_PARENTFOLDERID_2);

				sb.append(_FINDER_COLUMN_G_P_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(parentFolderId);

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
	 * Returns the number of bookmarks folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @return the number of matching bookmarks folders that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_S(
		long groupId, long parentFolderId, int status) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P_S(groupId, parentFolderId, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<BookmarksFolder> bookmarksFolders = findByG_P_S(
				groupId, parentFolderId, status);

			bookmarksFolders = InlineSQLHelperUtil.filter(
				bookmarksFolders, groupId);

			return bookmarksFolders.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_BOOKMARKSFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_G_P_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_S_PARENTFOLDERID_2);

		sb.append(_FINDER_COLUMN_G_P_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BookmarksFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(parentFolderId);

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

	private static final String _FINDER_COLUMN_G_P_S_GROUPID_2 =
		"bookmarksFolder.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_S_PARENTFOLDERID_2 =
		"bookmarksFolder.parentFolderId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_S_STATUS_2 =
		"bookmarksFolder.status = ?";

	private FinderPath _finderPathWithPaginationFindByG_P_NotS;
	private FinderPath _finderPathWithPaginationCountByG_P_NotS;

	/**
	 * Returns all the bookmarks folders where groupId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @return the matching bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findByG_P_NotS(
		long groupId, long parentFolderId, int status) {

		return findByG_P_NotS(
			groupId, parentFolderId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the bookmarks folders where groupId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @return the range of matching bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findByG_P_NotS(
		long groupId, long parentFolderId, int status, int start, int end) {

		return findByG_P_NotS(
			groupId, parentFolderId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the bookmarks folders where groupId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findByG_P_NotS(
		long groupId, long parentFolderId, int status, int start, int end,
		OrderByComparator<BookmarksFolder> orderByComparator) {

		return findByG_P_NotS(
			groupId, parentFolderId, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the bookmarks folders where groupId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findByG_P_NotS(
		long groupId, long parentFolderId, int status, int start, int end,
		OrderByComparator<BookmarksFolder> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BookmarksFolder.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByG_P_NotS;
			finderArgs = new Object[] {
				groupId, parentFolderId, status, start, end, orderByComparator
			};

			List<BookmarksFolder> list = null;

			if (useFinderCache) {
				list = (List<BookmarksFolder>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BookmarksFolder bookmarksFolder : list) {
						if ((groupId != bookmarksFolder.getGroupId()) ||
							(parentFolderId !=
								bookmarksFolder.getParentFolderId()) ||
							(status == bookmarksFolder.getStatus())) {

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

				sb.append(_SQL_SELECT_BOOKMARKSFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_G_P_NOTS_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_NOTS_PARENTFOLDERID_2);

				sb.append(_FINDER_COLUMN_G_P_NOTS_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BookmarksFolderModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(parentFolderId);

					queryPos.add(status);

					list = (List<BookmarksFolder>)QueryUtil.list(
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
	 * Returns the first bookmarks folder in the ordered set where groupId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching bookmarks folder
	 * @throws NoSuchFolderException if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder findByG_P_NotS_First(
			long groupId, long parentFolderId, int status,
			OrderByComparator<BookmarksFolder> orderByComparator)
		throws NoSuchFolderException {

		BookmarksFolder bookmarksFolder = fetchByG_P_NotS_First(
			groupId, parentFolderId, status, orderByComparator);

		if (bookmarksFolder != null) {
			return bookmarksFolder;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", parentFolderId=");
		sb.append(parentFolderId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the first bookmarks folder in the ordered set where groupId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching bookmarks folder, or <code>null</code> if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder fetchByG_P_NotS_First(
		long groupId, long parentFolderId, int status,
		OrderByComparator<BookmarksFolder> orderByComparator) {

		List<BookmarksFolder> list = findByG_P_NotS(
			groupId, parentFolderId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last bookmarks folder in the ordered set where groupId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching bookmarks folder
	 * @throws NoSuchFolderException if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder findByG_P_NotS_Last(
			long groupId, long parentFolderId, int status,
			OrderByComparator<BookmarksFolder> orderByComparator)
		throws NoSuchFolderException {

		BookmarksFolder bookmarksFolder = fetchByG_P_NotS_Last(
			groupId, parentFolderId, status, orderByComparator);

		if (bookmarksFolder != null) {
			return bookmarksFolder;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", parentFolderId=");
		sb.append(parentFolderId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the last bookmarks folder in the ordered set where groupId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching bookmarks folder, or <code>null</code> if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder fetchByG_P_NotS_Last(
		long groupId, long parentFolderId, int status,
		OrderByComparator<BookmarksFolder> orderByComparator) {

		int count = countByG_P_NotS(groupId, parentFolderId, status);

		if (count == 0) {
			return null;
		}

		List<BookmarksFolder> list = findByG_P_NotS(
			groupId, parentFolderId, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the bookmarks folders before and after the current bookmarks folder in the ordered set where groupId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param folderId the primary key of the current bookmarks folder
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next bookmarks folder
	 * @throws NoSuchFolderException if a bookmarks folder with the primary key could not be found
	 */
	@Override
	public BookmarksFolder[] findByG_P_NotS_PrevAndNext(
			long folderId, long groupId, long parentFolderId, int status,
			OrderByComparator<BookmarksFolder> orderByComparator)
		throws NoSuchFolderException {

		BookmarksFolder bookmarksFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			BookmarksFolder[] array = new BookmarksFolderImpl[3];

			array[0] = getByG_P_NotS_PrevAndNext(
				session, bookmarksFolder, groupId, parentFolderId, status,
				orderByComparator, true);

			array[1] = bookmarksFolder;

			array[2] = getByG_P_NotS_PrevAndNext(
				session, bookmarksFolder, groupId, parentFolderId, status,
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

	protected BookmarksFolder getByG_P_NotS_PrevAndNext(
		Session session, BookmarksFolder bookmarksFolder, long groupId,
		long parentFolderId, int status,
		OrderByComparator<BookmarksFolder> orderByComparator,
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

		sb.append(_SQL_SELECT_BOOKMARKSFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_G_P_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_NOTS_PARENTFOLDERID_2);

		sb.append(_FINDER_COLUMN_G_P_NOTS_STATUS_2);

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
			sb.append(BookmarksFolderModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(parentFolderId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						bookmarksFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BookmarksFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the bookmarks folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @return the matching bookmarks folders that the user has permission to view
	 */
	@Override
	public List<BookmarksFolder> filterFindByG_P_NotS(
		long groupId, long parentFolderId, int status) {

		return filterFindByG_P_NotS(
			groupId, parentFolderId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the bookmarks folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @return the range of matching bookmarks folders that the user has permission to view
	 */
	@Override
	public List<BookmarksFolder> filterFindByG_P_NotS(
		long groupId, long parentFolderId, int status, int start, int end) {

		return filterFindByG_P_NotS(
			groupId, parentFolderId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the bookmarks folders that the user has permissions to view where groupId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching bookmarks folders that the user has permission to view
	 */
	@Override
	public List<BookmarksFolder> filterFindByG_P_NotS(
		long groupId, long parentFolderId, int status, int start, int end,
		OrderByComparator<BookmarksFolder> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_NotS(
				groupId, parentFolderId, status, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P_NotS(
					groupId, parentFolderId, status, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_BOOKMARKSFOLDER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_BOOKMARKSFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_NOTS_PARENTFOLDERID_2);

		sb.append(_FINDER_COLUMN_G_P_NOTS_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_BOOKMARKSFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
					BookmarksFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BookmarksFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BookmarksFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, BookmarksFolderImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, BookmarksFolderImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(parentFolderId);

			queryPos.add(status);

			return (List<BookmarksFolder>)QueryUtil.list(
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
	 * Returns the bookmarks folders before and after the current bookmarks folder in the ordered set of bookmarks folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param folderId the primary key of the current bookmarks folder
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next bookmarks folder
	 * @throws NoSuchFolderException if a bookmarks folder with the primary key could not be found
	 */
	@Override
	public BookmarksFolder[] filterFindByG_P_NotS_PrevAndNext(
			long folderId, long groupId, long parentFolderId, int status,
			OrderByComparator<BookmarksFolder> orderByComparator)
		throws NoSuchFolderException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_NotS_PrevAndNext(
				folderId, groupId, parentFolderId, status, orderByComparator);
		}

		BookmarksFolder bookmarksFolder = findByPrimaryKey(folderId);

		Session session = null;

		try {
			session = openSession();

			BookmarksFolder[] array = new BookmarksFolderImpl[3];

			array[0] = filterGetByG_P_NotS_PrevAndNext(
				session, bookmarksFolder, groupId, parentFolderId, status,
				orderByComparator, true);

			array[1] = bookmarksFolder;

			array[2] = filterGetByG_P_NotS_PrevAndNext(
				session, bookmarksFolder, groupId, parentFolderId, status,
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

	protected BookmarksFolder filterGetByG_P_NotS_PrevAndNext(
		Session session, BookmarksFolder bookmarksFolder, long groupId,
		long parentFolderId, int status,
		OrderByComparator<BookmarksFolder> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_BOOKMARKSFOLDER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_BOOKMARKSFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_NOTS_PARENTFOLDERID_2);

		sb.append(_FINDER_COLUMN_G_P_NOTS_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_BOOKMARKSFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
					BookmarksFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BookmarksFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BookmarksFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, BookmarksFolderImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, BookmarksFolderImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(parentFolderId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						bookmarksFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BookmarksFolder> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the bookmarks folders where groupId = &#63; and parentFolderId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 */
	@Override
	public void removeByG_P_NotS(
		long groupId, long parentFolderId, int status) {

		for (BookmarksFolder bookmarksFolder :
				findByG_P_NotS(
					groupId, parentFolderId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(bookmarksFolder);
		}
	}

	/**
	 * Returns the number of bookmarks folders where groupId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @return the number of matching bookmarks folders
	 */
	@Override
	public int countByG_P_NotS(long groupId, long parentFolderId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BookmarksFolder.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByG_P_NotS;

			Object[] finderArgs = new Object[] {
				groupId, parentFolderId, status
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_BOOKMARKSFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_G_P_NOTS_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_NOTS_PARENTFOLDERID_2);

				sb.append(_FINDER_COLUMN_G_P_NOTS_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(parentFolderId);

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
	 * Returns the number of bookmarks folders that the user has permission to view where groupId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @return the number of matching bookmarks folders that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_NotS(
		long groupId, long parentFolderId, int status) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P_NotS(groupId, parentFolderId, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<BookmarksFolder> bookmarksFolders = findByG_P_NotS(
				groupId, parentFolderId, status);

			bookmarksFolders = InlineSQLHelperUtil.filter(
				bookmarksFolders, groupId);

			return bookmarksFolders.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_BOOKMARKSFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_G_P_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_NOTS_PARENTFOLDERID_2);

		sb.append(_FINDER_COLUMN_G_P_NOTS_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BookmarksFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(parentFolderId);

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

	private static final String _FINDER_COLUMN_G_P_NOTS_GROUPID_2 =
		"bookmarksFolder.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_NOTS_PARENTFOLDERID_2 =
		"bookmarksFolder.parentFolderId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_NOTS_STATUS_2 =
		"bookmarksFolder.status != ?";

	private FinderPath _finderPathWithPaginationFindByGtF_C_P_NotS;
	private FinderPath _finderPathWithPaginationCountByGtF_C_P_NotS;

	/**
	 * Returns all the bookmarks folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @return the matching bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findByGtF_C_P_NotS(
		long folderId, long companyId, long parentFolderId, int status) {

		return findByGtF_C_P_NotS(
			folderId, companyId, parentFolderId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the bookmarks folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @return the range of matching bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findByGtF_C_P_NotS(
		long folderId, long companyId, long parentFolderId, int status,
		int start, int end) {

		return findByGtF_C_P_NotS(
			folderId, companyId, parentFolderId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the bookmarks folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findByGtF_C_P_NotS(
		long folderId, long companyId, long parentFolderId, int status,
		int start, int end,
		OrderByComparator<BookmarksFolder> orderByComparator) {

		return findByGtF_C_P_NotS(
			folderId, companyId, parentFolderId, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the bookmarks folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findByGtF_C_P_NotS(
		long folderId, long companyId, long parentFolderId, int status,
		int start, int end,
		OrderByComparator<BookmarksFolder> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BookmarksFolder.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByGtF_C_P_NotS;
			finderArgs = new Object[] {
				folderId, companyId, parentFolderId, status, start, end,
				orderByComparator
			};

			List<BookmarksFolder> list = null;

			if (useFinderCache) {
				list = (List<BookmarksFolder>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BookmarksFolder bookmarksFolder : list) {
						if ((folderId >= bookmarksFolder.getFolderId()) ||
							(companyId != bookmarksFolder.getCompanyId()) ||
							(parentFolderId !=
								bookmarksFolder.getParentFolderId()) ||
							(status == bookmarksFolder.getStatus())) {

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

				sb.append(_SQL_SELECT_BOOKMARKSFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_GTF_C_P_NOTS_FOLDERID_2);

				sb.append(_FINDER_COLUMN_GTF_C_P_NOTS_COMPANYID_2);

				sb.append(_FINDER_COLUMN_GTF_C_P_NOTS_PARENTFOLDERID_2);

				sb.append(_FINDER_COLUMN_GTF_C_P_NOTS_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BookmarksFolderModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(folderId);

					queryPos.add(companyId);

					queryPos.add(parentFolderId);

					queryPos.add(status);

					list = (List<BookmarksFolder>)QueryUtil.list(
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
	 * Returns the first bookmarks folder in the ordered set where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching bookmarks folder
	 * @throws NoSuchFolderException if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder findByGtF_C_P_NotS_First(
			long folderId, long companyId, long parentFolderId, int status,
			OrderByComparator<BookmarksFolder> orderByComparator)
		throws NoSuchFolderException {

		BookmarksFolder bookmarksFolder = fetchByGtF_C_P_NotS_First(
			folderId, companyId, parentFolderId, status, orderByComparator);

		if (bookmarksFolder != null) {
			return bookmarksFolder;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("folderId>");
		sb.append(folderId);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append(", parentFolderId=");
		sb.append(parentFolderId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the first bookmarks folder in the ordered set where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching bookmarks folder, or <code>null</code> if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder fetchByGtF_C_P_NotS_First(
		long folderId, long companyId, long parentFolderId, int status,
		OrderByComparator<BookmarksFolder> orderByComparator) {

		List<BookmarksFolder> list = findByGtF_C_P_NotS(
			folderId, companyId, parentFolderId, status, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last bookmarks folder in the ordered set where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching bookmarks folder
	 * @throws NoSuchFolderException if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder findByGtF_C_P_NotS_Last(
			long folderId, long companyId, long parentFolderId, int status,
			OrderByComparator<BookmarksFolder> orderByComparator)
		throws NoSuchFolderException {

		BookmarksFolder bookmarksFolder = fetchByGtF_C_P_NotS_Last(
			folderId, companyId, parentFolderId, status, orderByComparator);

		if (bookmarksFolder != null) {
			return bookmarksFolder;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("folderId>");
		sb.append(folderId);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append(", parentFolderId=");
		sb.append(parentFolderId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchFolderException(sb.toString());
	}

	/**
	 * Returns the last bookmarks folder in the ordered set where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching bookmarks folder, or <code>null</code> if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder fetchByGtF_C_P_NotS_Last(
		long folderId, long companyId, long parentFolderId, int status,
		OrderByComparator<BookmarksFolder> orderByComparator) {

		int count = countByGtF_C_P_NotS(
			folderId, companyId, parentFolderId, status);

		if (count == 0) {
			return null;
		}

		List<BookmarksFolder> list = findByGtF_C_P_NotS(
			folderId, companyId, parentFolderId, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Removes all the bookmarks folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 */
	@Override
	public void removeByGtF_C_P_NotS(
		long folderId, long companyId, long parentFolderId, int status) {

		for (BookmarksFolder bookmarksFolder :
				findByGtF_C_P_NotS(
					folderId, companyId, parentFolderId, status,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(bookmarksFolder);
		}
	}

	/**
	 * Returns the number of bookmarks folders where folderId &gt; &#63; and companyId = &#63; and parentFolderId = &#63; and status &ne; &#63;.
	 *
	 * @param folderId the folder ID
	 * @param companyId the company ID
	 * @param parentFolderId the parent folder ID
	 * @param status the status
	 * @return the number of matching bookmarks folders
	 */
	@Override
	public int countByGtF_C_P_NotS(
		long folderId, long companyId, long parentFolderId, int status) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BookmarksFolder.class)) {

			FinderPath finderPath =
				_finderPathWithPaginationCountByGtF_C_P_NotS;

			Object[] finderArgs = new Object[] {
				folderId, companyId, parentFolderId, status
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_BOOKMARKSFOLDER_WHERE);

				sb.append(_FINDER_COLUMN_GTF_C_P_NOTS_FOLDERID_2);

				sb.append(_FINDER_COLUMN_GTF_C_P_NOTS_COMPANYID_2);

				sb.append(_FINDER_COLUMN_GTF_C_P_NOTS_PARENTFOLDERID_2);

				sb.append(_FINDER_COLUMN_GTF_C_P_NOTS_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(folderId);

					queryPos.add(companyId);

					queryPos.add(parentFolderId);

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

	private static final String _FINDER_COLUMN_GTF_C_P_NOTS_FOLDERID_2 =
		"bookmarksFolder.folderId > ? AND ";

	private static final String _FINDER_COLUMN_GTF_C_P_NOTS_COMPANYID_2 =
		"bookmarksFolder.companyId = ? AND ";

	private static final String _FINDER_COLUMN_GTF_C_P_NOTS_PARENTFOLDERID_2 =
		"bookmarksFolder.parentFolderId = ? AND ";

	private static final String _FINDER_COLUMN_GTF_C_P_NOTS_STATUS_2 =
		"bookmarksFolder.status != ?";

	public BookmarksFolderPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(BookmarksFolder.class);

		setModelImplClass(BookmarksFolderImpl.class);
		setModelPKClass(long.class);

		setTable(BookmarksFolderTable.INSTANCE);
	}

	/**
	 * Caches the bookmarks folder in the entity cache if it is enabled.
	 *
	 * @param bookmarksFolder the bookmarks folder
	 */
	@Override
	public void cacheResult(BookmarksFolder bookmarksFolder) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					bookmarksFolder.getCtCollectionId())) {

			entityCache.putResult(
				BookmarksFolderImpl.class, bookmarksFolder.getPrimaryKey(),
				bookmarksFolder);

			finderCache.putResult(
				_finderPathFetchByUUID_G,
				new Object[] {
					bookmarksFolder.getUuid(), bookmarksFolder.getGroupId()
				},
				bookmarksFolder);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the bookmarks folders in the entity cache if it is enabled.
	 *
	 * @param bookmarksFolders the bookmarks folders
	 */
	@Override
	public void cacheResult(List<BookmarksFolder> bookmarksFolders) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (bookmarksFolders.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (BookmarksFolder bookmarksFolder : bookmarksFolders) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						bookmarksFolder.getCtCollectionId())) {

				if (entityCache.getResult(
						BookmarksFolderImpl.class,
						bookmarksFolder.getPrimaryKey()) == null) {

					cacheResult(bookmarksFolder);
				}
			}
		}
	}

	/**
	 * Clears the cache for all bookmarks folders.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(BookmarksFolderImpl.class);

		finderCache.clearCache(BookmarksFolderImpl.class);
	}

	/**
	 * Clears the cache for the bookmarks folder.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(BookmarksFolder bookmarksFolder) {
		entityCache.removeResult(BookmarksFolderImpl.class, bookmarksFolder);
	}

	@Override
	public void clearCache(List<BookmarksFolder> bookmarksFolders) {
		for (BookmarksFolder bookmarksFolder : bookmarksFolders) {
			entityCache.removeResult(
				BookmarksFolderImpl.class, bookmarksFolder);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(BookmarksFolderImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(BookmarksFolderImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		BookmarksFolderModelImpl bookmarksFolderModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					bookmarksFolderModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				bookmarksFolderModelImpl.getUuid(),
				bookmarksFolderModelImpl.getGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByUUID_G, args, bookmarksFolderModelImpl);
		}
	}

	/**
	 * Creates a new bookmarks folder with the primary key. Does not add the bookmarks folder to the database.
	 *
	 * @param folderId the primary key for the new bookmarks folder
	 * @return the new bookmarks folder
	 */
	@Override
	public BookmarksFolder create(long folderId) {
		BookmarksFolder bookmarksFolder = new BookmarksFolderImpl();

		bookmarksFolder.setNew(true);
		bookmarksFolder.setPrimaryKey(folderId);

		String uuid = PortalUUIDUtil.generate();

		bookmarksFolder.setUuid(uuid);

		bookmarksFolder.setCompanyId(CompanyThreadLocal.getCompanyId());

		return bookmarksFolder;
	}

	/**
	 * Removes the bookmarks folder with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param folderId the primary key of the bookmarks folder
	 * @return the bookmarks folder that was removed
	 * @throws NoSuchFolderException if a bookmarks folder with the primary key could not be found
	 */
	@Override
	public BookmarksFolder remove(long folderId) throws NoSuchFolderException {
		return remove((Serializable)folderId);
	}

	/**
	 * Removes the bookmarks folder with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the bookmarks folder
	 * @return the bookmarks folder that was removed
	 * @throws NoSuchFolderException if a bookmarks folder with the primary key could not be found
	 */
	@Override
	public BookmarksFolder remove(Serializable primaryKey)
		throws NoSuchFolderException {

		Session session = null;

		try {
			session = openSession();

			BookmarksFolder bookmarksFolder = (BookmarksFolder)session.get(
				BookmarksFolderImpl.class, primaryKey);

			if (bookmarksFolder == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchFolderException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(bookmarksFolder);
		}
		catch (NoSuchFolderException noSuchEntityException) {
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
	protected BookmarksFolder removeImpl(BookmarksFolder bookmarksFolder) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(bookmarksFolder)) {
				bookmarksFolder = (BookmarksFolder)session.get(
					BookmarksFolderImpl.class,
					bookmarksFolder.getPrimaryKeyObj());
			}

			if ((bookmarksFolder != null) &&
				ctPersistenceHelper.isRemove(bookmarksFolder)) {

				session.delete(bookmarksFolder);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (bookmarksFolder != null) {
			clearCache(bookmarksFolder);
		}

		return bookmarksFolder;
	}

	@Override
	public BookmarksFolder updateImpl(BookmarksFolder bookmarksFolder) {
		boolean isNew = bookmarksFolder.isNew();

		if (!(bookmarksFolder instanceof BookmarksFolderModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(bookmarksFolder.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					bookmarksFolder);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in bookmarksFolder proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom BookmarksFolder implementation " +
					bookmarksFolder.getClass());
		}

		BookmarksFolderModelImpl bookmarksFolderModelImpl =
			(BookmarksFolderModelImpl)bookmarksFolder;

		if (Validator.isNull(bookmarksFolder.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			bookmarksFolder.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (bookmarksFolder.getCreateDate() == null)) {
			if (serviceContext == null) {
				bookmarksFolder.setCreateDate(date);
			}
			else {
				bookmarksFolder.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!bookmarksFolderModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				bookmarksFolder.setModifiedDate(date);
			}
			else {
				bookmarksFolder.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(bookmarksFolder)) {
				if (!isNew) {
					session.evict(
						BookmarksFolderImpl.class,
						bookmarksFolder.getPrimaryKeyObj());
				}

				session.save(bookmarksFolder);
			}
			else {
				bookmarksFolder = (BookmarksFolder)session.merge(
					bookmarksFolder);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			BookmarksFolderImpl.class, bookmarksFolderModelImpl, false, true);

		cacheUniqueFindersCache(bookmarksFolderModelImpl);

		if (isNew) {
			bookmarksFolder.setNew(false);
		}

		bookmarksFolder.resetOriginalValues();

		return bookmarksFolder;
	}

	/**
	 * Returns the bookmarks folder with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the bookmarks folder
	 * @return the bookmarks folder
	 * @throws NoSuchFolderException if a bookmarks folder with the primary key could not be found
	 */
	@Override
	public BookmarksFolder findByPrimaryKey(Serializable primaryKey)
		throws NoSuchFolderException {

		BookmarksFolder bookmarksFolder = fetchByPrimaryKey(primaryKey);

		if (bookmarksFolder == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchFolderException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return bookmarksFolder;
	}

	/**
	 * Returns the bookmarks folder with the primary key or throws a <code>NoSuchFolderException</code> if it could not be found.
	 *
	 * @param folderId the primary key of the bookmarks folder
	 * @return the bookmarks folder
	 * @throws NoSuchFolderException if a bookmarks folder with the primary key could not be found
	 */
	@Override
	public BookmarksFolder findByPrimaryKey(long folderId)
		throws NoSuchFolderException {

		return findByPrimaryKey((Serializable)folderId);
	}

	/**
	 * Returns the bookmarks folder with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the bookmarks folder
	 * @return the bookmarks folder, or <code>null</code> if a bookmarks folder with the primary key could not be found
	 */
	@Override
	public BookmarksFolder fetchByPrimaryKey(Serializable primaryKey) {
		if (ctPersistenceHelper.isProductionMode(
				BookmarksFolder.class, primaryKey)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		BookmarksFolder bookmarksFolder =
			(BookmarksFolder)entityCache.getResult(
				BookmarksFolderImpl.class, primaryKey);

		if (bookmarksFolder != null) {
			return bookmarksFolder;
		}

		Session session = null;

		try {
			session = openSession();

			bookmarksFolder = (BookmarksFolder)session.get(
				BookmarksFolderImpl.class, primaryKey);

			if (bookmarksFolder != null) {
				cacheResult(bookmarksFolder);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return bookmarksFolder;
	}

	/**
	 * Returns the bookmarks folder with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param folderId the primary key of the bookmarks folder
	 * @return the bookmarks folder, or <code>null</code> if a bookmarks folder with the primary key could not be found
	 */
	@Override
	public BookmarksFolder fetchByPrimaryKey(long folderId) {
		return fetchByPrimaryKey((Serializable)folderId);
	}

	@Override
	public Map<Serializable, BookmarksFolder> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (ctPersistenceHelper.isProductionMode(BookmarksFolder.class)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, BookmarksFolder> map =
			new HashMap<Serializable, BookmarksFolder>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			BookmarksFolder bookmarksFolder = fetchByPrimaryKey(primaryKey);

			if (bookmarksFolder != null) {
				map.put(primaryKey, bookmarksFolder);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
						BookmarksFolder.class, primaryKey)) {

				BookmarksFolder bookmarksFolder =
					(BookmarksFolder)entityCache.getResult(
						BookmarksFolderImpl.class, primaryKey);

				if (bookmarksFolder == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, bookmarksFolder);
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

			for (BookmarksFolder bookmarksFolder :
					(List<BookmarksFolder>)query.list()) {

				map.put(bookmarksFolder.getPrimaryKeyObj(), bookmarksFolder);

				cacheResult(bookmarksFolder);
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
	 * Returns all the bookmarks folders.
	 *
	 * @return the bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the bookmarks folders.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @return the range of bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the bookmarks folders.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findAll(
		int start, int end,
		OrderByComparator<BookmarksFolder> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the bookmarks folders.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of bookmarks folders
	 */
	@Override
	public List<BookmarksFolder> findAll(
		int start, int end,
		OrderByComparator<BookmarksFolder> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BookmarksFolder.class)) {

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

			List<BookmarksFolder> list = null;

			if (useFinderCache) {
				list = (List<BookmarksFolder>)finderCache.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_BOOKMARKSFOLDER);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_BOOKMARKSFOLDER;

					sql = sql.concat(BookmarksFolderModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<BookmarksFolder>)QueryUtil.list(
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
	 * Removes all the bookmarks folders from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (BookmarksFolder bookmarksFolder : findAll()) {
			remove(bookmarksFolder);
		}
	}

	/**
	 * Returns the number of bookmarks folders.
	 *
	 * @return the number of bookmarks folders
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BookmarksFolder.class)) {

			Long count = (Long)finderCache.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(
						_SQL_COUNT_BOOKMARKSFOLDER);

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
		return "folderId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_BOOKMARKSFOLDER;
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
		return BookmarksFolderModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "BookmarksFolder";
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
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("parentFolderId");
		ctMergeColumnNames.add("treePath");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("description");
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
			CTColumnResolutionType.PK, Collections.singleton("folderId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});
	}

	/**
	 * Initializes the bookmarks folder persistence.
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

		_finderPathWithPaginationFindByG_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "parentFolderId"}, true);

		_finderPathWithoutPaginationFindByG_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "parentFolderId"}, true);

		_finderPathCountByG_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "parentFolderId"}, false);

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

		_finderPathWithPaginationFindByG_P_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "parentFolderId", "status"}, true);

		_finderPathWithoutPaginationFindByG_P_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "parentFolderId", "status"}, true);

		_finderPathCountByG_P_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "parentFolderId", "status"}, false);

		_finderPathWithPaginationFindByG_P_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_NotS",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "parentFolderId", "status"}, true);

		_finderPathWithPaginationCountByG_P_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_P_NotS",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "parentFolderId", "status"}, false);

		_finderPathWithPaginationFindByGtF_C_P_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGtF_C_P_NotS",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"folderId", "companyId", "parentFolderId", "status"},
			true);

		_finderPathWithPaginationCountByGtF_C_P_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByGtF_C_P_NotS",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName()
			},
			new String[] {"folderId", "companyId", "parentFolderId", "status"},
			false);

		BookmarksFolderUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		BookmarksFolderUtil.setPersistence(null);

		entityCache.removeCache(BookmarksFolderImpl.class.getName());
	}

	@Override
	@Reference(
		target = BookmarksPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = BookmarksPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = BookmarksPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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

	private static final String _SQL_SELECT_BOOKMARKSFOLDER =
		"SELECT bookmarksFolder FROM BookmarksFolder bookmarksFolder";

	private static final String _SQL_SELECT_BOOKMARKSFOLDER_WHERE =
		"SELECT bookmarksFolder FROM BookmarksFolder bookmarksFolder WHERE ";

	private static final String _SQL_COUNT_BOOKMARKSFOLDER =
		"SELECT COUNT(bookmarksFolder) FROM BookmarksFolder bookmarksFolder";

	private static final String _SQL_COUNT_BOOKMARKSFOLDER_WHERE =
		"SELECT COUNT(bookmarksFolder) FROM BookmarksFolder bookmarksFolder WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"bookmarksFolder.folderId";

	private static final String _FILTER_SQL_SELECT_BOOKMARKSFOLDER_WHERE =
		"SELECT DISTINCT {bookmarksFolder.*} FROM BookmarksFolder bookmarksFolder WHERE ";

	private static final String
		_FILTER_SQL_SELECT_BOOKMARKSFOLDER_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {BookmarksFolder.*} FROM (SELECT DISTINCT bookmarksFolder.folderId FROM BookmarksFolder bookmarksFolder WHERE ";

	private static final String
		_FILTER_SQL_SELECT_BOOKMARKSFOLDER_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN BookmarksFolder ON TEMP_TABLE.folderId = BookmarksFolder.folderId";

	private static final String _FILTER_SQL_COUNT_BOOKMARKSFOLDER_WHERE =
		"SELECT COUNT(DISTINCT bookmarksFolder.folderId) AS COUNT_VALUE FROM BookmarksFolder bookmarksFolder WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "bookmarksFolder";

	private static final String _FILTER_ENTITY_TABLE = "BookmarksFolder";

	private static final String _ORDER_BY_ENTITY_ALIAS = "bookmarksFolder.";

	private static final String _ORDER_BY_ENTITY_TABLE = "BookmarksFolder.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No BookmarksFolder exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No BookmarksFolder exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		BookmarksFolderPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}