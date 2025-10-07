/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.documentlibrary.service.persistence.impl;

import com.liferay.document.library.kernel.exception.DuplicateDLFileEntryExternalReferenceCodeException;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryTable;
import com.liferay.document.library.kernel.service.persistence.DLFileEntryPersistence;
import com.liferay.document.library.kernel.service.persistence.DLFileEntryUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
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
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
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
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl;

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

/**
 * The persistence implementation for the document library file entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class DLFileEntryPersistenceImpl
	extends BasePersistenceImpl<DLFileEntry> implements DLFileEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DLFileEntryUtil</code> to access the document library file entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DLFileEntryImpl.class.getName();

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
	 * Returns all the document library file entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

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

			List<DLFileEntry> list = null;

			if (useFinderCache) {
				list = (List<DLFileEntry>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFileEntry dlFileEntry : list) {
						if (!uuid.equals(dlFileEntry.getUuid())) {
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

				sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

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
					sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
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

					list = (List<DLFileEntry>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(finderPath, finderArgs, list);
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
	 * Returns the first document library file entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByUuid_First(
			String uuid, OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByUuid_First(uuid, orderByComparator);

		if (dlFileEntry != null) {
			return dlFileEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchFileEntryException(sb.toString());
	}

	/**
	 * Returns the first document library file entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByUuid_First(
		String uuid, OrderByComparator<DLFileEntry> orderByComparator) {

		List<DLFileEntry> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library file entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByUuid_Last(
			String uuid, OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByUuid_Last(uuid, orderByComparator);

		if (dlFileEntry != null) {
			return dlFileEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchFileEntryException(sb.toString());
	}

	/**
	 * Returns the last document library file entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByUuid_Last(
		String uuid, OrderByComparator<DLFileEntry> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<DLFileEntry> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set where uuid = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	@Override
	public DLFileEntry[] findByUuid_PrevAndNext(
			long fileEntryId, String uuid,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		uuid = Objects.toString(uuid, "");

		DLFileEntry dlFileEntry = findByPrimaryKey(fileEntryId);

		Session session = null;

		try {
			session = openSession();

			DLFileEntry[] array = new DLFileEntryImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, dlFileEntry, uuid, orderByComparator, true);

			array[1] = dlFileEntry;

			array[2] = getByUuid_PrevAndNext(
				session, dlFileEntry, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DLFileEntry getByUuid_PrevAndNext(
		Session session, DLFileEntry dlFileEntry, String uuid,
		OrderByComparator<DLFileEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

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
			sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(dlFileEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFileEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library file entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (DLFileEntry dlFileEntry :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(dlFileEntry);
		}
	}

	/**
	 * Returns the number of document library file entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByUuid(String uuid) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid;

			Object[] finderArgs = new Object[] {uuid};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_DLFILEENTRY_WHERE);

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

					FinderCacheUtil.putResult(finderPath, finderArgs, count);
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
		"dlFileEntry.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(dlFileEntry.uuid IS NULL OR dlFileEntry.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;

	/**
	 * Returns the document library file entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchFileEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByUUID_G(uuid, groupId);

		if (dlFileEntry == null) {
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

			throw new NoSuchFileEntryException(sb.toString());
		}

		return dlFileEntry;
	}

	/**
	 * Returns the document library file entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the document library file entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			uuid = Objects.toString(uuid, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {uuid, groupId};
			}

			Object result = null;

			if (useFinderCache) {
				result = FinderCacheUtil.getResult(
					_finderPathFetchByUUID_G, finderArgs, this);
			}

			if (result instanceof DLFileEntry) {
				DLFileEntry dlFileEntry = (DLFileEntry)result;

				if (!Objects.equals(uuid, dlFileEntry.getUuid()) ||
					(groupId != dlFileEntry.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

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

					List<DLFileEntry> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							FinderCacheUtil.putResult(
								_finderPathFetchByUUID_G, finderArgs, list);
						}
					}
					else {
						DLFileEntry dlFileEntry = list.get(0);

						result = dlFileEntry;

						cacheResult(dlFileEntry);
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
				return (DLFileEntry)result;
			}
		}
	}

	/**
	 * Removes the document library file entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the document library file entry that was removed
	 */
	@Override
	public DLFileEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = findByUUID_G(uuid, groupId);

		return remove(dlFileEntry);
	}

	/**
	 * Returns the number of document library file entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		DLFileEntry dlFileEntry = fetchByUUID_G(uuid, groupId);

		if (dlFileEntry == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"dlFileEntry.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(dlFileEntry.uuid IS NULL OR dlFileEntry.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"dlFileEntry.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the document library file entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

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

			List<DLFileEntry> list = null;

			if (useFinderCache) {
				list = (List<DLFileEntry>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFileEntry dlFileEntry : list) {
						if (!uuid.equals(dlFileEntry.getUuid()) ||
							(companyId != dlFileEntry.getCompanyId())) {

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

				sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

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
					sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
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

					list = (List<DLFileEntry>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(finderPath, finderArgs, list);
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
	 * Returns the first document library file entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (dlFileEntry != null) {
			return dlFileEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchFileEntryException(sb.toString());
	}

	/**
	 * Returns the first document library file entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<DLFileEntry> orderByComparator) {

		List<DLFileEntry> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library file entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (dlFileEntry != null) {
			return dlFileEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchFileEntryException(sb.toString());
	}

	/**
	 * Returns the last document library file entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<DLFileEntry> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<DLFileEntry> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	@Override
	public DLFileEntry[] findByUuid_C_PrevAndNext(
			long fileEntryId, String uuid, long companyId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		uuid = Objects.toString(uuid, "");

		DLFileEntry dlFileEntry = findByPrimaryKey(fileEntryId);

		Session session = null;

		try {
			session = openSession();

			DLFileEntry[] array = new DLFileEntryImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, dlFileEntry, uuid, companyId, orderByComparator, true);

			array[1] = dlFileEntry;

			array[2] = getByUuid_C_PrevAndNext(
				session, dlFileEntry, uuid, companyId, orderByComparator,
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

	protected DLFileEntry getByUuid_C_PrevAndNext(
		Session session, DLFileEntry dlFileEntry, String uuid, long companyId,
		OrderByComparator<DLFileEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

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
			sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(dlFileEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFileEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library file entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (DLFileEntry dlFileEntry :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(dlFileEntry);
		}
	}

	/**
	 * Returns the number of document library file entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid_C;

			Object[] finderArgs = new Object[] {uuid, companyId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_DLFILEENTRY_WHERE);

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

					FinderCacheUtil.putResult(finderPath, finderArgs, count);
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
		"dlFileEntry.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(dlFileEntry.uuid IS NULL OR dlFileEntry.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"dlFileEntry.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByGroupId;
	private FinderPath _finderPathWithoutPaginationFindByGroupId;
	private FinderPath _finderPathCountByGroupId;

	/**
	 * Returns all the document library file entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByGroupId(long groupId, int start, int end) {
		return findByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

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

			List<DLFileEntry> list = null;

			if (useFinderCache) {
				list = (List<DLFileEntry>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFileEntry dlFileEntry : list) {
						if (groupId != dlFileEntry.getGroupId()) {
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

				sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					list = (List<DLFileEntry>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(finderPath, finderArgs, list);
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
	 * Returns the first document library file entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByGroupId_First(
			long groupId, OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByGroupId_First(
			groupId, orderByComparator);

		if (dlFileEntry != null) {
			return dlFileEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchFileEntryException(sb.toString());
	}

	/**
	 * Returns the first document library file entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByGroupId_First(
		long groupId, OrderByComparator<DLFileEntry> orderByComparator) {

		List<DLFileEntry> list = findByGroupId(
			groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library file entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByGroupId_Last(
			long groupId, OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByGroupId_Last(
			groupId, orderByComparator);

		if (dlFileEntry != null) {
			return dlFileEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchFileEntryException(sb.toString());
	}

	/**
	 * Returns the last document library file entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByGroupId_Last(
		long groupId, OrderByComparator<DLFileEntry> orderByComparator) {

		int count = countByGroupId(groupId);

		if (count == 0) {
			return null;
		}

		List<DLFileEntry> list = findByGroupId(
			groupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set where groupId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	@Override
	public DLFileEntry[] findByGroupId_PrevAndNext(
			long fileEntryId, long groupId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = findByPrimaryKey(fileEntryId);

		Session session = null;

		try {
			session = openSession();

			DLFileEntry[] array = new DLFileEntryImpl[3];

			array[0] = getByGroupId_PrevAndNext(
				session, dlFileEntry, groupId, orderByComparator, true);

			array[1] = dlFileEntry;

			array[2] = getByGroupId_PrevAndNext(
				session, dlFileEntry, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DLFileEntry getByGroupId_PrevAndNext(
		Session session, DLFileEntry dlFileEntry, long groupId,
		OrderByComparator<DLFileEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

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
			sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFileEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFileEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the document library file entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching document library file entries that the user has permission to view
	 */
	@Override
	public List<DLFileEntry> filterFindByGroupId(long groupId) {
		return filterFindByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file entries that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries that the user has permission to view
	 */
	@Override
	public List<DLFileEntry> filterFindByGroupId(
		long groupId, int start, int end) {

		return filterFindByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file entries that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries that the user has permission to view
	 */
	@Override
	public List<DLFileEntry> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_DLFILEENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DLFILEENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DLFILEENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DLFileEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DLFileEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFileEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DLFileEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DLFileEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			return (List<DLFileEntry>)QueryUtil.list(
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
	 * Returns the document library file entries before and after the current document library file entry in the ordered set of document library file entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	@Override
	public DLFileEntry[] filterFindByGroupId_PrevAndNext(
			long fileEntryId, long groupId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByGroupId_PrevAndNext(
				fileEntryId, groupId, orderByComparator);
		}

		DLFileEntry dlFileEntry = findByPrimaryKey(fileEntryId);

		Session session = null;

		try {
			session = openSession();

			DLFileEntry[] array = new DLFileEntryImpl[3];

			array[0] = filterGetByGroupId_PrevAndNext(
				session, dlFileEntry, groupId, orderByComparator, true);

			array[1] = dlFileEntry;

			array[2] = filterGetByGroupId_PrevAndNext(
				session, dlFileEntry, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DLFileEntry filterGetByGroupId_PrevAndNext(
		Session session, DLFileEntry dlFileEntry, long groupId,
		OrderByComparator<DLFileEntry> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_DLFILEENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DLFILEENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DLFILEENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DLFileEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DLFileEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFileEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DLFileEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DLFileEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFileEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFileEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library file entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		for (DLFileEntry dlFileEntry :
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(dlFileEntry);
		}
	}

	/**
	 * Returns the number of document library file entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByGroupId(long groupId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			FinderPath finderPath = _finderPathCountByGroupId;

			Object[] finderArgs = new Object[] {groupId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(finderPath, finderArgs, count);
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
	 * Returns the number of document library file entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching document library file entries that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByGroupId(groupId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DLFileEntry> dlFileEntries = findByGroupId(groupId);

			dlFileEntries = InlineSQLHelperUtil.filter(dlFileEntries, groupId);

			return dlFileEntries.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_DLFILEENTRY_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFileEntry.class.getName(),
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
		"dlFileEntry.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;

	/**
	 * Returns all the document library file entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

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

			List<DLFileEntry> list = null;

			if (useFinderCache) {
				list = (List<DLFileEntry>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFileEntry dlFileEntry : list) {
						if (companyId != dlFileEntry.getCompanyId()) {
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

				sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					list = (List<DLFileEntry>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(finderPath, finderArgs, list);
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
	 * Returns the first document library file entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByCompanyId_First(
			long companyId, OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (dlFileEntry != null) {
			return dlFileEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchFileEntryException(sb.toString());
	}

	/**
	 * Returns the first document library file entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByCompanyId_First(
		long companyId, OrderByComparator<DLFileEntry> orderByComparator) {

		List<DLFileEntry> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library file entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByCompanyId_Last(
			long companyId, OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByCompanyId_Last(
			companyId, orderByComparator);

		if (dlFileEntry != null) {
			return dlFileEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchFileEntryException(sb.toString());
	}

	/**
	 * Returns the last document library file entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByCompanyId_Last(
		long companyId, OrderByComparator<DLFileEntry> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<DLFileEntry> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set where companyId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	@Override
	public DLFileEntry[] findByCompanyId_PrevAndNext(
			long fileEntryId, long companyId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = findByPrimaryKey(fileEntryId);

		Session session = null;

		try {
			session = openSession();

			DLFileEntry[] array = new DLFileEntryImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, dlFileEntry, companyId, orderByComparator, true);

			array[1] = dlFileEntry;

			array[2] = getByCompanyId_PrevAndNext(
				session, dlFileEntry, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DLFileEntry getByCompanyId_PrevAndNext(
		Session session, DLFileEntry dlFileEntry, long companyId,
		OrderByComparator<DLFileEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

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
			sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFileEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFileEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library file entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (DLFileEntry dlFileEntry :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(dlFileEntry);
		}
	}

	/**
	 * Returns the number of document library file entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByCompanyId(long companyId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			FinderPath finderPath = _finderPathCountByCompanyId;

			Object[] finderArgs = new Object[] {companyId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(finderPath, finderArgs, count);
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
		"dlFileEntry.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByRepositoryId;
	private FinderPath _finderPathWithoutPaginationFindByRepositoryId;
	private FinderPath _finderPathCountByRepositoryId;

	/**
	 * Returns all the document library file entries where repositoryId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @return the matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByRepositoryId(long repositoryId) {
		return findByRepositoryId(
			repositoryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file entries where repositoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param repositoryId the repository ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByRepositoryId(
		long repositoryId, int start, int end) {

		return findByRepositoryId(repositoryId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file entries where repositoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param repositoryId the repository ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByRepositoryId(
		long repositoryId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return findByRepositoryId(
			repositoryId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where repositoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param repositoryId the repository ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByRepositoryId(
		long repositoryId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByRepositoryId;
					finderArgs = new Object[] {repositoryId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByRepositoryId;
				finderArgs = new Object[] {
					repositoryId, start, end, orderByComparator
				};
			}

			List<DLFileEntry> list = null;

			if (useFinderCache) {
				list = (List<DLFileEntry>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFileEntry dlFileEntry : list) {
						if (repositoryId != dlFileEntry.getRepositoryId()) {
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

				sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_REPOSITORYID_REPOSITORYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(repositoryId);

					list = (List<DLFileEntry>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(finderPath, finderArgs, list);
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
	 * Returns the first document library file entry in the ordered set where repositoryId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByRepositoryId_First(
			long repositoryId, OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByRepositoryId_First(
			repositoryId, orderByComparator);

		if (dlFileEntry != null) {
			return dlFileEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("repositoryId=");
		sb.append(repositoryId);

		sb.append("}");

		throw new NoSuchFileEntryException(sb.toString());
	}

	/**
	 * Returns the first document library file entry in the ordered set where repositoryId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByRepositoryId_First(
		long repositoryId, OrderByComparator<DLFileEntry> orderByComparator) {

		List<DLFileEntry> list = findByRepositoryId(
			repositoryId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library file entry in the ordered set where repositoryId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByRepositoryId_Last(
			long repositoryId, OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByRepositoryId_Last(
			repositoryId, orderByComparator);

		if (dlFileEntry != null) {
			return dlFileEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("repositoryId=");
		sb.append(repositoryId);

		sb.append("}");

		throw new NoSuchFileEntryException(sb.toString());
	}

	/**
	 * Returns the last document library file entry in the ordered set where repositoryId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByRepositoryId_Last(
		long repositoryId, OrderByComparator<DLFileEntry> orderByComparator) {

		int count = countByRepositoryId(repositoryId);

		if (count == 0) {
			return null;
		}

		List<DLFileEntry> list = findByRepositoryId(
			repositoryId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set where repositoryId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param repositoryId the repository ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	@Override
	public DLFileEntry[] findByRepositoryId_PrevAndNext(
			long fileEntryId, long repositoryId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = findByPrimaryKey(fileEntryId);

		Session session = null;

		try {
			session = openSession();

			DLFileEntry[] array = new DLFileEntryImpl[3];

			array[0] = getByRepositoryId_PrevAndNext(
				session, dlFileEntry, repositoryId, orderByComparator, true);

			array[1] = dlFileEntry;

			array[2] = getByRepositoryId_PrevAndNext(
				session, dlFileEntry, repositoryId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DLFileEntry getByRepositoryId_PrevAndNext(
		Session session, DLFileEntry dlFileEntry, long repositoryId,
		OrderByComparator<DLFileEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

		sb.append(_FINDER_COLUMN_REPOSITORYID_REPOSITORYID_2);

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
			sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(repositoryId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFileEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFileEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library file entries where repositoryId = &#63; from the database.
	 *
	 * @param repositoryId the repository ID
	 */
	@Override
	public void removeByRepositoryId(long repositoryId) {
		for (DLFileEntry dlFileEntry :
				findByRepositoryId(
					repositoryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(dlFileEntry);
		}
	}

	/**
	 * Returns the number of document library file entries where repositoryId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByRepositoryId(long repositoryId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			FinderPath finderPath = _finderPathCountByRepositoryId;

			Object[] finderArgs = new Object[] {repositoryId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_REPOSITORYID_REPOSITORYID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(repositoryId);

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(finderPath, finderArgs, count);
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

	private static final String _FINDER_COLUMN_REPOSITORYID_REPOSITORYID_2 =
		"dlFileEntry.repositoryId = ?";

	private FinderPath _finderPathWithPaginationFindByMimeType;
	private FinderPath _finderPathWithoutPaginationFindByMimeType;
	private FinderPath _finderPathCountByMimeType;

	/**
	 * Returns all the document library file entries where mimeType = &#63;.
	 *
	 * @param mimeType the mime type
	 * @return the matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByMimeType(String mimeType) {
		return findByMimeType(
			mimeType, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file entries where mimeType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param mimeType the mime type
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByMimeType(
		String mimeType, int start, int end) {

		return findByMimeType(mimeType, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file entries where mimeType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param mimeType the mime type
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByMimeType(
		String mimeType, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return findByMimeType(mimeType, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where mimeType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param mimeType the mime type
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByMimeType(
		String mimeType, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			mimeType = Objects.toString(mimeType, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByMimeType;
					finderArgs = new Object[] {mimeType};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByMimeType;
				finderArgs = new Object[] {
					mimeType, start, end, orderByComparator
				};
			}

			List<DLFileEntry> list = null;

			if (useFinderCache) {
				list = (List<DLFileEntry>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFileEntry dlFileEntry : list) {
						if (!mimeType.equals(dlFileEntry.getMimeType())) {
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

				sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

				boolean bindMimeType = false;

				if (mimeType.isEmpty()) {
					sb.append(_FINDER_COLUMN_MIMETYPE_MIMETYPE_3);
				}
				else {
					bindMimeType = true;

					sb.append(_FINDER_COLUMN_MIMETYPE_MIMETYPE_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindMimeType) {
						queryPos.add(mimeType);
					}

					list = (List<DLFileEntry>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(finderPath, finderArgs, list);
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
	 * Returns the first document library file entry in the ordered set where mimeType = &#63;.
	 *
	 * @param mimeType the mime type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByMimeType_First(
			String mimeType, OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByMimeType_First(
			mimeType, orderByComparator);

		if (dlFileEntry != null) {
			return dlFileEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("mimeType=");
		sb.append(mimeType);

		sb.append("}");

		throw new NoSuchFileEntryException(sb.toString());
	}

	/**
	 * Returns the first document library file entry in the ordered set where mimeType = &#63;.
	 *
	 * @param mimeType the mime type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByMimeType_First(
		String mimeType, OrderByComparator<DLFileEntry> orderByComparator) {

		List<DLFileEntry> list = findByMimeType(
			mimeType, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library file entry in the ordered set where mimeType = &#63;.
	 *
	 * @param mimeType the mime type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByMimeType_Last(
			String mimeType, OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByMimeType_Last(
			mimeType, orderByComparator);

		if (dlFileEntry != null) {
			return dlFileEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("mimeType=");
		sb.append(mimeType);

		sb.append("}");

		throw new NoSuchFileEntryException(sb.toString());
	}

	/**
	 * Returns the last document library file entry in the ordered set where mimeType = &#63;.
	 *
	 * @param mimeType the mime type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByMimeType_Last(
		String mimeType, OrderByComparator<DLFileEntry> orderByComparator) {

		int count = countByMimeType(mimeType);

		if (count == 0) {
			return null;
		}

		List<DLFileEntry> list = findByMimeType(
			mimeType, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set where mimeType = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param mimeType the mime type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	@Override
	public DLFileEntry[] findByMimeType_PrevAndNext(
			long fileEntryId, String mimeType,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		mimeType = Objects.toString(mimeType, "");

		DLFileEntry dlFileEntry = findByPrimaryKey(fileEntryId);

		Session session = null;

		try {
			session = openSession();

			DLFileEntry[] array = new DLFileEntryImpl[3];

			array[0] = getByMimeType_PrevAndNext(
				session, dlFileEntry, mimeType, orderByComparator, true);

			array[1] = dlFileEntry;

			array[2] = getByMimeType_PrevAndNext(
				session, dlFileEntry, mimeType, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DLFileEntry getByMimeType_PrevAndNext(
		Session session, DLFileEntry dlFileEntry, String mimeType,
		OrderByComparator<DLFileEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

		boolean bindMimeType = false;

		if (mimeType.isEmpty()) {
			sb.append(_FINDER_COLUMN_MIMETYPE_MIMETYPE_3);
		}
		else {
			bindMimeType = true;

			sb.append(_FINDER_COLUMN_MIMETYPE_MIMETYPE_2);
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
			sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindMimeType) {
			queryPos.add(mimeType);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFileEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFileEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library file entries where mimeType = &#63; from the database.
	 *
	 * @param mimeType the mime type
	 */
	@Override
	public void removeByMimeType(String mimeType) {
		for (DLFileEntry dlFileEntry :
				findByMimeType(
					mimeType, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(dlFileEntry);
		}
	}

	/**
	 * Returns the number of document library file entries where mimeType = &#63;.
	 *
	 * @param mimeType the mime type
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByMimeType(String mimeType) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			mimeType = Objects.toString(mimeType, "");

			FinderPath finderPath = _finderPathCountByMimeType;

			Object[] finderArgs = new Object[] {mimeType};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_DLFILEENTRY_WHERE);

				boolean bindMimeType = false;

				if (mimeType.isEmpty()) {
					sb.append(_FINDER_COLUMN_MIMETYPE_MIMETYPE_3);
				}
				else {
					bindMimeType = true;

					sb.append(_FINDER_COLUMN_MIMETYPE_MIMETYPE_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindMimeType) {
						queryPos.add(mimeType);
					}

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(finderPath, finderArgs, count);
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

	private static final String _FINDER_COLUMN_MIMETYPE_MIMETYPE_2 =
		"dlFileEntry.mimeType = ?";

	private static final String _FINDER_COLUMN_MIMETYPE_MIMETYPE_3 =
		"(dlFileEntry.mimeType IS NULL OR dlFileEntry.mimeType = '')";

	private FinderPath _finderPathWithPaginationFindByFileEntryTypeId;
	private FinderPath _finderPathWithoutPaginationFindByFileEntryTypeId;
	private FinderPath _finderPathCountByFileEntryTypeId;

	/**
	 * Returns all the document library file entries where fileEntryTypeId = &#63;.
	 *
	 * @param fileEntryTypeId the file entry type ID
	 * @return the matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByFileEntryTypeId(long fileEntryTypeId) {
		return findByFileEntryTypeId(
			fileEntryTypeId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file entries where fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByFileEntryTypeId(
		long fileEntryTypeId, int start, int end) {

		return findByFileEntryTypeId(fileEntryTypeId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file entries where fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByFileEntryTypeId(
		long fileEntryTypeId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return findByFileEntryTypeId(
			fileEntryTypeId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByFileEntryTypeId(
		long fileEntryTypeId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath =
						_finderPathWithoutPaginationFindByFileEntryTypeId;
					finderArgs = new Object[] {fileEntryTypeId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByFileEntryTypeId;
				finderArgs = new Object[] {
					fileEntryTypeId, start, end, orderByComparator
				};
			}

			List<DLFileEntry> list = null;

			if (useFinderCache) {
				list = (List<DLFileEntry>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFileEntry dlFileEntry : list) {
						if (fileEntryTypeId !=
								dlFileEntry.getFileEntryTypeId()) {

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

				sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_FILEENTRYTYPEID_FILEENTRYTYPEID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(fileEntryTypeId);

					list = (List<DLFileEntry>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(finderPath, finderArgs, list);
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
	 * Returns the first document library file entry in the ordered set where fileEntryTypeId = &#63;.
	 *
	 * @param fileEntryTypeId the file entry type ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByFileEntryTypeId_First(
			long fileEntryTypeId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByFileEntryTypeId_First(
			fileEntryTypeId, orderByComparator);

		if (dlFileEntry != null) {
			return dlFileEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("fileEntryTypeId=");
		sb.append(fileEntryTypeId);

		sb.append("}");

		throw new NoSuchFileEntryException(sb.toString());
	}

	/**
	 * Returns the first document library file entry in the ordered set where fileEntryTypeId = &#63;.
	 *
	 * @param fileEntryTypeId the file entry type ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByFileEntryTypeId_First(
		long fileEntryTypeId,
		OrderByComparator<DLFileEntry> orderByComparator) {

		List<DLFileEntry> list = findByFileEntryTypeId(
			fileEntryTypeId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library file entry in the ordered set where fileEntryTypeId = &#63;.
	 *
	 * @param fileEntryTypeId the file entry type ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByFileEntryTypeId_Last(
			long fileEntryTypeId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByFileEntryTypeId_Last(
			fileEntryTypeId, orderByComparator);

		if (dlFileEntry != null) {
			return dlFileEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("fileEntryTypeId=");
		sb.append(fileEntryTypeId);

		sb.append("}");

		throw new NoSuchFileEntryException(sb.toString());
	}

	/**
	 * Returns the last document library file entry in the ordered set where fileEntryTypeId = &#63;.
	 *
	 * @param fileEntryTypeId the file entry type ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByFileEntryTypeId_Last(
		long fileEntryTypeId,
		OrderByComparator<DLFileEntry> orderByComparator) {

		int count = countByFileEntryTypeId(fileEntryTypeId);

		if (count == 0) {
			return null;
		}

		List<DLFileEntry> list = findByFileEntryTypeId(
			fileEntryTypeId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set where fileEntryTypeId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param fileEntryTypeId the file entry type ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	@Override
	public DLFileEntry[] findByFileEntryTypeId_PrevAndNext(
			long fileEntryId, long fileEntryTypeId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = findByPrimaryKey(fileEntryId);

		Session session = null;

		try {
			session = openSession();

			DLFileEntry[] array = new DLFileEntryImpl[3];

			array[0] = getByFileEntryTypeId_PrevAndNext(
				session, dlFileEntry, fileEntryTypeId, orderByComparator, true);

			array[1] = dlFileEntry;

			array[2] = getByFileEntryTypeId_PrevAndNext(
				session, dlFileEntry, fileEntryTypeId, orderByComparator,
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

	protected DLFileEntry getByFileEntryTypeId_PrevAndNext(
		Session session, DLFileEntry dlFileEntry, long fileEntryTypeId,
		OrderByComparator<DLFileEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

		sb.append(_FINDER_COLUMN_FILEENTRYTYPEID_FILEENTRYTYPEID_2);

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
			sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(fileEntryTypeId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFileEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFileEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library file entries where fileEntryTypeId = &#63; from the database.
	 *
	 * @param fileEntryTypeId the file entry type ID
	 */
	@Override
	public void removeByFileEntryTypeId(long fileEntryTypeId) {
		for (DLFileEntry dlFileEntry :
				findByFileEntryTypeId(
					fileEntryTypeId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(dlFileEntry);
		}
	}

	/**
	 * Returns the number of document library file entries where fileEntryTypeId = &#63;.
	 *
	 * @param fileEntryTypeId the file entry type ID
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByFileEntryTypeId(long fileEntryTypeId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			FinderPath finderPath = _finderPathCountByFileEntryTypeId;

			Object[] finderArgs = new Object[] {fileEntryTypeId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_FILEENTRYTYPEID_FILEENTRYTYPEID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(fileEntryTypeId);

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(finderPath, finderArgs, count);
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

	private static final String
		_FINDER_COLUMN_FILEENTRYTYPEID_FILEENTRYTYPEID_2 =
			"dlFileEntry.fileEntryTypeId = ?";

	private FinderPath _finderPathWithPaginationFindBySmallImageId;
	private FinderPath _finderPathWithoutPaginationFindBySmallImageId;
	private FinderPath _finderPathCountBySmallImageId;

	/**
	 * Returns all the document library file entries where smallImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @return the matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findBySmallImageId(long smallImageId) {
		return findBySmallImageId(
			smallImageId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file entries where smallImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param smallImageId the small image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findBySmallImageId(
		long smallImageId, int start, int end) {

		return findBySmallImageId(smallImageId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file entries where smallImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param smallImageId the small image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findBySmallImageId(
		long smallImageId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return findBySmallImageId(
			smallImageId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where smallImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param smallImageId the small image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findBySmallImageId(
		long smallImageId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindBySmallImageId;
					finderArgs = new Object[] {smallImageId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindBySmallImageId;
				finderArgs = new Object[] {
					smallImageId, start, end, orderByComparator
				};
			}

			List<DLFileEntry> list = null;

			if (useFinderCache) {
				list = (List<DLFileEntry>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFileEntry dlFileEntry : list) {
						if (smallImageId != dlFileEntry.getSmallImageId()) {
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

				sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_SMALLIMAGEID_SMALLIMAGEID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(smallImageId);

					list = (List<DLFileEntry>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(finderPath, finderArgs, list);
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
	 * Returns the first document library file entry in the ordered set where smallImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findBySmallImageId_First(
			long smallImageId, OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchBySmallImageId_First(
			smallImageId, orderByComparator);

		if (dlFileEntry != null) {
			return dlFileEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("smallImageId=");
		sb.append(smallImageId);

		sb.append("}");

		throw new NoSuchFileEntryException(sb.toString());
	}

	/**
	 * Returns the first document library file entry in the ordered set where smallImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchBySmallImageId_First(
		long smallImageId, OrderByComparator<DLFileEntry> orderByComparator) {

		List<DLFileEntry> list = findBySmallImageId(
			smallImageId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library file entry in the ordered set where smallImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findBySmallImageId_Last(
			long smallImageId, OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchBySmallImageId_Last(
			smallImageId, orderByComparator);

		if (dlFileEntry != null) {
			return dlFileEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("smallImageId=");
		sb.append(smallImageId);

		sb.append("}");

		throw new NoSuchFileEntryException(sb.toString());
	}

	/**
	 * Returns the last document library file entry in the ordered set where smallImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchBySmallImageId_Last(
		long smallImageId, OrderByComparator<DLFileEntry> orderByComparator) {

		int count = countBySmallImageId(smallImageId);

		if (count == 0) {
			return null;
		}

		List<DLFileEntry> list = findBySmallImageId(
			smallImageId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set where smallImageId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param smallImageId the small image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	@Override
	public DLFileEntry[] findBySmallImageId_PrevAndNext(
			long fileEntryId, long smallImageId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = findByPrimaryKey(fileEntryId);

		Session session = null;

		try {
			session = openSession();

			DLFileEntry[] array = new DLFileEntryImpl[3];

			array[0] = getBySmallImageId_PrevAndNext(
				session, dlFileEntry, smallImageId, orderByComparator, true);

			array[1] = dlFileEntry;

			array[2] = getBySmallImageId_PrevAndNext(
				session, dlFileEntry, smallImageId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DLFileEntry getBySmallImageId_PrevAndNext(
		Session session, DLFileEntry dlFileEntry, long smallImageId,
		OrderByComparator<DLFileEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

		sb.append(_FINDER_COLUMN_SMALLIMAGEID_SMALLIMAGEID_2);

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
			sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(smallImageId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFileEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFileEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library file entries where smallImageId = &#63; from the database.
	 *
	 * @param smallImageId the small image ID
	 */
	@Override
	public void removeBySmallImageId(long smallImageId) {
		for (DLFileEntry dlFileEntry :
				findBySmallImageId(
					smallImageId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(dlFileEntry);
		}
	}

	/**
	 * Returns the number of document library file entries where smallImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countBySmallImageId(long smallImageId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			FinderPath finderPath = _finderPathCountBySmallImageId;

			Object[] finderArgs = new Object[] {smallImageId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_SMALLIMAGEID_SMALLIMAGEID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(smallImageId);

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(finderPath, finderArgs, count);
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

	private static final String _FINDER_COLUMN_SMALLIMAGEID_SMALLIMAGEID_2 =
		"dlFileEntry.smallImageId = ?";

	private FinderPath _finderPathWithPaginationFindByLargeImageId;
	private FinderPath _finderPathWithoutPaginationFindByLargeImageId;
	private FinderPath _finderPathCountByLargeImageId;

	/**
	 * Returns all the document library file entries where largeImageId = &#63;.
	 *
	 * @param largeImageId the large image ID
	 * @return the matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByLargeImageId(long largeImageId) {
		return findByLargeImageId(
			largeImageId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file entries where largeImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param largeImageId the large image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByLargeImageId(
		long largeImageId, int start, int end) {

		return findByLargeImageId(largeImageId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file entries where largeImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param largeImageId the large image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByLargeImageId(
		long largeImageId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return findByLargeImageId(
			largeImageId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where largeImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param largeImageId the large image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByLargeImageId(
		long largeImageId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByLargeImageId;
					finderArgs = new Object[] {largeImageId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByLargeImageId;
				finderArgs = new Object[] {
					largeImageId, start, end, orderByComparator
				};
			}

			List<DLFileEntry> list = null;

			if (useFinderCache) {
				list = (List<DLFileEntry>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFileEntry dlFileEntry : list) {
						if (largeImageId != dlFileEntry.getLargeImageId()) {
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

				sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_LARGEIMAGEID_LARGEIMAGEID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(largeImageId);

					list = (List<DLFileEntry>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(finderPath, finderArgs, list);
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
	 * Returns the first document library file entry in the ordered set where largeImageId = &#63;.
	 *
	 * @param largeImageId the large image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByLargeImageId_First(
			long largeImageId, OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByLargeImageId_First(
			largeImageId, orderByComparator);

		if (dlFileEntry != null) {
			return dlFileEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("largeImageId=");
		sb.append(largeImageId);

		sb.append("}");

		throw new NoSuchFileEntryException(sb.toString());
	}

	/**
	 * Returns the first document library file entry in the ordered set where largeImageId = &#63;.
	 *
	 * @param largeImageId the large image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByLargeImageId_First(
		long largeImageId, OrderByComparator<DLFileEntry> orderByComparator) {

		List<DLFileEntry> list = findByLargeImageId(
			largeImageId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library file entry in the ordered set where largeImageId = &#63;.
	 *
	 * @param largeImageId the large image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByLargeImageId_Last(
			long largeImageId, OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByLargeImageId_Last(
			largeImageId, orderByComparator);

		if (dlFileEntry != null) {
			return dlFileEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("largeImageId=");
		sb.append(largeImageId);

		sb.append("}");

		throw new NoSuchFileEntryException(sb.toString());
	}

	/**
	 * Returns the last document library file entry in the ordered set where largeImageId = &#63;.
	 *
	 * @param largeImageId the large image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByLargeImageId_Last(
		long largeImageId, OrderByComparator<DLFileEntry> orderByComparator) {

		int count = countByLargeImageId(largeImageId);

		if (count == 0) {
			return null;
		}

		List<DLFileEntry> list = findByLargeImageId(
			largeImageId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set where largeImageId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param largeImageId the large image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	@Override
	public DLFileEntry[] findByLargeImageId_PrevAndNext(
			long fileEntryId, long largeImageId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = findByPrimaryKey(fileEntryId);

		Session session = null;

		try {
			session = openSession();

			DLFileEntry[] array = new DLFileEntryImpl[3];

			array[0] = getByLargeImageId_PrevAndNext(
				session, dlFileEntry, largeImageId, orderByComparator, true);

			array[1] = dlFileEntry;

			array[2] = getByLargeImageId_PrevAndNext(
				session, dlFileEntry, largeImageId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DLFileEntry getByLargeImageId_PrevAndNext(
		Session session, DLFileEntry dlFileEntry, long largeImageId,
		OrderByComparator<DLFileEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

		sb.append(_FINDER_COLUMN_LARGEIMAGEID_LARGEIMAGEID_2);

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
			sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(largeImageId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFileEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFileEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library file entries where largeImageId = &#63; from the database.
	 *
	 * @param largeImageId the large image ID
	 */
	@Override
	public void removeByLargeImageId(long largeImageId) {
		for (DLFileEntry dlFileEntry :
				findByLargeImageId(
					largeImageId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(dlFileEntry);
		}
	}

	/**
	 * Returns the number of document library file entries where largeImageId = &#63;.
	 *
	 * @param largeImageId the large image ID
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByLargeImageId(long largeImageId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			FinderPath finderPath = _finderPathCountByLargeImageId;

			Object[] finderArgs = new Object[] {largeImageId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_LARGEIMAGEID_LARGEIMAGEID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(largeImageId);

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(finderPath, finderArgs, count);
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

	private static final String _FINDER_COLUMN_LARGEIMAGEID_LARGEIMAGEID_2 =
		"dlFileEntry.largeImageId = ?";

	private FinderPath _finderPathWithPaginationFindByCustom1ImageId;
	private FinderPath _finderPathWithoutPaginationFindByCustom1ImageId;
	private FinderPath _finderPathCountByCustom1ImageId;

	/**
	 * Returns all the document library file entries where custom1ImageId = &#63;.
	 *
	 * @param custom1ImageId the custom1 image ID
	 * @return the matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByCustom1ImageId(long custom1ImageId) {
		return findByCustom1ImageId(
			custom1ImageId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file entries where custom1ImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param custom1ImageId the custom1 image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByCustom1ImageId(
		long custom1ImageId, int start, int end) {

		return findByCustom1ImageId(custom1ImageId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file entries where custom1ImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param custom1ImageId the custom1 image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByCustom1ImageId(
		long custom1ImageId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return findByCustom1ImageId(
			custom1ImageId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where custom1ImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param custom1ImageId the custom1 image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByCustom1ImageId(
		long custom1ImageId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath =
						_finderPathWithoutPaginationFindByCustom1ImageId;
					finderArgs = new Object[] {custom1ImageId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByCustom1ImageId;
				finderArgs = new Object[] {
					custom1ImageId, start, end, orderByComparator
				};
			}

			List<DLFileEntry> list = null;

			if (useFinderCache) {
				list = (List<DLFileEntry>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFileEntry dlFileEntry : list) {
						if (custom1ImageId != dlFileEntry.getCustom1ImageId()) {
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

				sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_CUSTOM1IMAGEID_CUSTOM1IMAGEID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(custom1ImageId);

					list = (List<DLFileEntry>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(finderPath, finderArgs, list);
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
	 * Returns the first document library file entry in the ordered set where custom1ImageId = &#63;.
	 *
	 * @param custom1ImageId the custom1 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByCustom1ImageId_First(
			long custom1ImageId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByCustom1ImageId_First(
			custom1ImageId, orderByComparator);

		if (dlFileEntry != null) {
			return dlFileEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("custom1ImageId=");
		sb.append(custom1ImageId);

		sb.append("}");

		throw new NoSuchFileEntryException(sb.toString());
	}

	/**
	 * Returns the first document library file entry in the ordered set where custom1ImageId = &#63;.
	 *
	 * @param custom1ImageId the custom1 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByCustom1ImageId_First(
		long custom1ImageId, OrderByComparator<DLFileEntry> orderByComparator) {

		List<DLFileEntry> list = findByCustom1ImageId(
			custom1ImageId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library file entry in the ordered set where custom1ImageId = &#63;.
	 *
	 * @param custom1ImageId the custom1 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByCustom1ImageId_Last(
			long custom1ImageId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByCustom1ImageId_Last(
			custom1ImageId, orderByComparator);

		if (dlFileEntry != null) {
			return dlFileEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("custom1ImageId=");
		sb.append(custom1ImageId);

		sb.append("}");

		throw new NoSuchFileEntryException(sb.toString());
	}

	/**
	 * Returns the last document library file entry in the ordered set where custom1ImageId = &#63;.
	 *
	 * @param custom1ImageId the custom1 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByCustom1ImageId_Last(
		long custom1ImageId, OrderByComparator<DLFileEntry> orderByComparator) {

		int count = countByCustom1ImageId(custom1ImageId);

		if (count == 0) {
			return null;
		}

		List<DLFileEntry> list = findByCustom1ImageId(
			custom1ImageId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set where custom1ImageId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param custom1ImageId the custom1 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	@Override
	public DLFileEntry[] findByCustom1ImageId_PrevAndNext(
			long fileEntryId, long custom1ImageId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = findByPrimaryKey(fileEntryId);

		Session session = null;

		try {
			session = openSession();

			DLFileEntry[] array = new DLFileEntryImpl[3];

			array[0] = getByCustom1ImageId_PrevAndNext(
				session, dlFileEntry, custom1ImageId, orderByComparator, true);

			array[1] = dlFileEntry;

			array[2] = getByCustom1ImageId_PrevAndNext(
				session, dlFileEntry, custom1ImageId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DLFileEntry getByCustom1ImageId_PrevAndNext(
		Session session, DLFileEntry dlFileEntry, long custom1ImageId,
		OrderByComparator<DLFileEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

		sb.append(_FINDER_COLUMN_CUSTOM1IMAGEID_CUSTOM1IMAGEID_2);

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
			sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(custom1ImageId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFileEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFileEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library file entries where custom1ImageId = &#63; from the database.
	 *
	 * @param custom1ImageId the custom1 image ID
	 */
	@Override
	public void removeByCustom1ImageId(long custom1ImageId) {
		for (DLFileEntry dlFileEntry :
				findByCustom1ImageId(
					custom1ImageId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(dlFileEntry);
		}
	}

	/**
	 * Returns the number of document library file entries where custom1ImageId = &#63;.
	 *
	 * @param custom1ImageId the custom1 image ID
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByCustom1ImageId(long custom1ImageId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			FinderPath finderPath = _finderPathCountByCustom1ImageId;

			Object[] finderArgs = new Object[] {custom1ImageId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_CUSTOM1IMAGEID_CUSTOM1IMAGEID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(custom1ImageId);

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(finderPath, finderArgs, count);
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

	private static final String _FINDER_COLUMN_CUSTOM1IMAGEID_CUSTOM1IMAGEID_2 =
		"dlFileEntry.custom1ImageId = ?";

	private FinderPath _finderPathWithPaginationFindByCustom2ImageId;
	private FinderPath _finderPathWithoutPaginationFindByCustom2ImageId;
	private FinderPath _finderPathCountByCustom2ImageId;

	/**
	 * Returns all the document library file entries where custom2ImageId = &#63;.
	 *
	 * @param custom2ImageId the custom2 image ID
	 * @return the matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByCustom2ImageId(long custom2ImageId) {
		return findByCustom2ImageId(
			custom2ImageId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file entries where custom2ImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param custom2ImageId the custom2 image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByCustom2ImageId(
		long custom2ImageId, int start, int end) {

		return findByCustom2ImageId(custom2ImageId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file entries where custom2ImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param custom2ImageId the custom2 image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByCustom2ImageId(
		long custom2ImageId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return findByCustom2ImageId(
			custom2ImageId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where custom2ImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param custom2ImageId the custom2 image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByCustom2ImageId(
		long custom2ImageId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath =
						_finderPathWithoutPaginationFindByCustom2ImageId;
					finderArgs = new Object[] {custom2ImageId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByCustom2ImageId;
				finderArgs = new Object[] {
					custom2ImageId, start, end, orderByComparator
				};
			}

			List<DLFileEntry> list = null;

			if (useFinderCache) {
				list = (List<DLFileEntry>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFileEntry dlFileEntry : list) {
						if (custom2ImageId != dlFileEntry.getCustom2ImageId()) {
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

				sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_CUSTOM2IMAGEID_CUSTOM2IMAGEID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(custom2ImageId);

					list = (List<DLFileEntry>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(finderPath, finderArgs, list);
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
	 * Returns the first document library file entry in the ordered set where custom2ImageId = &#63;.
	 *
	 * @param custom2ImageId the custom2 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByCustom2ImageId_First(
			long custom2ImageId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByCustom2ImageId_First(
			custom2ImageId, orderByComparator);

		if (dlFileEntry != null) {
			return dlFileEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("custom2ImageId=");
		sb.append(custom2ImageId);

		sb.append("}");

		throw new NoSuchFileEntryException(sb.toString());
	}

	/**
	 * Returns the first document library file entry in the ordered set where custom2ImageId = &#63;.
	 *
	 * @param custom2ImageId the custom2 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByCustom2ImageId_First(
		long custom2ImageId, OrderByComparator<DLFileEntry> orderByComparator) {

		List<DLFileEntry> list = findByCustom2ImageId(
			custom2ImageId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library file entry in the ordered set where custom2ImageId = &#63;.
	 *
	 * @param custom2ImageId the custom2 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByCustom2ImageId_Last(
			long custom2ImageId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByCustom2ImageId_Last(
			custom2ImageId, orderByComparator);

		if (dlFileEntry != null) {
			return dlFileEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("custom2ImageId=");
		sb.append(custom2ImageId);

		sb.append("}");

		throw new NoSuchFileEntryException(sb.toString());
	}

	/**
	 * Returns the last document library file entry in the ordered set where custom2ImageId = &#63;.
	 *
	 * @param custom2ImageId the custom2 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByCustom2ImageId_Last(
		long custom2ImageId, OrderByComparator<DLFileEntry> orderByComparator) {

		int count = countByCustom2ImageId(custom2ImageId);

		if (count == 0) {
			return null;
		}

		List<DLFileEntry> list = findByCustom2ImageId(
			custom2ImageId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set where custom2ImageId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param custom2ImageId the custom2 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	@Override
	public DLFileEntry[] findByCustom2ImageId_PrevAndNext(
			long fileEntryId, long custom2ImageId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = findByPrimaryKey(fileEntryId);

		Session session = null;

		try {
			session = openSession();

			DLFileEntry[] array = new DLFileEntryImpl[3];

			array[0] = getByCustom2ImageId_PrevAndNext(
				session, dlFileEntry, custom2ImageId, orderByComparator, true);

			array[1] = dlFileEntry;

			array[2] = getByCustom2ImageId_PrevAndNext(
				session, dlFileEntry, custom2ImageId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DLFileEntry getByCustom2ImageId_PrevAndNext(
		Session session, DLFileEntry dlFileEntry, long custom2ImageId,
		OrderByComparator<DLFileEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

		sb.append(_FINDER_COLUMN_CUSTOM2IMAGEID_CUSTOM2IMAGEID_2);

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
			sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(custom2ImageId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFileEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFileEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library file entries where custom2ImageId = &#63; from the database.
	 *
	 * @param custom2ImageId the custom2 image ID
	 */
	@Override
	public void removeByCustom2ImageId(long custom2ImageId) {
		for (DLFileEntry dlFileEntry :
				findByCustom2ImageId(
					custom2ImageId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(dlFileEntry);
		}
	}

	/**
	 * Returns the number of document library file entries where custom2ImageId = &#63;.
	 *
	 * @param custom2ImageId the custom2 image ID
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByCustom2ImageId(long custom2ImageId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			FinderPath finderPath = _finderPathCountByCustom2ImageId;

			Object[] finderArgs = new Object[] {custom2ImageId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_CUSTOM2IMAGEID_CUSTOM2IMAGEID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(custom2ImageId);

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(finderPath, finderArgs, count);
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

	private static final String _FINDER_COLUMN_CUSTOM2IMAGEID_CUSTOM2IMAGEID_2 =
		"dlFileEntry.custom2ImageId = ?";

	private FinderPath _finderPathWithPaginationFindByG_U;
	private FinderPath _finderPathWithoutPaginationFindByG_U;
	private FinderPath _finderPathCountByG_U;

	/**
	 * Returns all the document library file entries where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @return the matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByG_U(long groupId, long userId) {
		return findByG_U(
			groupId, userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file entries where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByG_U(
		long groupId, long userId, int start, int end) {

		return findByG_U(groupId, userId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByG_U(
		long groupId, long userId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return findByG_U(groupId, userId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByG_U(
		long groupId, long userId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_U;
					finderArgs = new Object[] {groupId, userId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_U;
				finderArgs = new Object[] {
					groupId, userId, start, end, orderByComparator
				};
			}

			List<DLFileEntry> list = null;

			if (useFinderCache) {
				list = (List<DLFileEntry>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFileEntry dlFileEntry : list) {
						if ((groupId != dlFileEntry.getGroupId()) ||
							(userId != dlFileEntry.getUserId())) {

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

				sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_U_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_U_USERID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(userId);

					list = (List<DLFileEntry>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(finderPath, finderArgs, list);
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
	 * Returns the first document library file entry in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByG_U_First(
			long groupId, long userId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByG_U_First(
			groupId, userId, orderByComparator);

		if (dlFileEntry != null) {
			return dlFileEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append("}");

		throw new NoSuchFileEntryException(sb.toString());
	}

	/**
	 * Returns the first document library file entry in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByG_U_First(
		long groupId, long userId,
		OrderByComparator<DLFileEntry> orderByComparator) {

		List<DLFileEntry> list = findByG_U(
			groupId, userId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library file entry in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByG_U_Last(
			long groupId, long userId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByG_U_Last(
			groupId, userId, orderByComparator);

		if (dlFileEntry != null) {
			return dlFileEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append("}");

		throw new NoSuchFileEntryException(sb.toString());
	}

	/**
	 * Returns the last document library file entry in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByG_U_Last(
		long groupId, long userId,
		OrderByComparator<DLFileEntry> orderByComparator) {

		int count = countByG_U(groupId, userId);

		if (count == 0) {
			return null;
		}

		List<DLFileEntry> list = findByG_U(
			groupId, userId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	@Override
	public DLFileEntry[] findByG_U_PrevAndNext(
			long fileEntryId, long groupId, long userId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = findByPrimaryKey(fileEntryId);

		Session session = null;

		try {
			session = openSession();

			DLFileEntry[] array = new DLFileEntryImpl[3];

			array[0] = getByG_U_PrevAndNext(
				session, dlFileEntry, groupId, userId, orderByComparator, true);

			array[1] = dlFileEntry;

			array[2] = getByG_U_PrevAndNext(
				session, dlFileEntry, groupId, userId, orderByComparator,
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

	protected DLFileEntry getByG_U_PrevAndNext(
		Session session, DLFileEntry dlFileEntry, long groupId, long userId,
		OrderByComparator<DLFileEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_U_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_USERID_2);

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
			sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(userId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFileEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFileEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the document library file entries that the user has permission to view where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @return the matching document library file entries that the user has permission to view
	 */
	@Override
	public List<DLFileEntry> filterFindByG_U(long groupId, long userId) {
		return filterFindByG_U(
			groupId, userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file entries that the user has permission to view where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries that the user has permission to view
	 */
	@Override
	public List<DLFileEntry> filterFindByG_U(
		long groupId, long userId, int start, int end) {

		return filterFindByG_U(groupId, userId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file entries that the user has permissions to view where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries that the user has permission to view
	 */
	@Override
	public List<DLFileEntry> filterFindByG_U(
		long groupId, long userId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_U(groupId, userId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_U(
					groupId, userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_DLFILEENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DLFILEENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_USERID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DLFILEENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DLFileEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DLFileEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFileEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DLFileEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DLFileEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(userId);

			return (List<DLFileEntry>)QueryUtil.list(
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
	 * Returns the document library file entries before and after the current document library file entry in the ordered set of document library file entries that the user has permission to view where groupId = &#63; and userId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	@Override
	public DLFileEntry[] filterFindByG_U_PrevAndNext(
			long fileEntryId, long groupId, long userId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_U_PrevAndNext(
				fileEntryId, groupId, userId, orderByComparator);
		}

		DLFileEntry dlFileEntry = findByPrimaryKey(fileEntryId);

		Session session = null;

		try {
			session = openSession();

			DLFileEntry[] array = new DLFileEntryImpl[3];

			array[0] = filterGetByG_U_PrevAndNext(
				session, dlFileEntry, groupId, userId, orderByComparator, true);

			array[1] = dlFileEntry;

			array[2] = filterGetByG_U_PrevAndNext(
				session, dlFileEntry, groupId, userId, orderByComparator,
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

	protected DLFileEntry filterGetByG_U_PrevAndNext(
		Session session, DLFileEntry dlFileEntry, long groupId, long userId,
		OrderByComparator<DLFileEntry> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_DLFILEENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DLFILEENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_USERID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DLFILEENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DLFileEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DLFileEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFileEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DLFileEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DLFileEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(userId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFileEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFileEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library file entries where groupId = &#63; and userId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 */
	@Override
	public void removeByG_U(long groupId, long userId) {
		for (DLFileEntry dlFileEntry :
				findByG_U(
					groupId, userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(dlFileEntry);
		}
	}

	/**
	 * Returns the number of document library file entries where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByG_U(long groupId, long userId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			FinderPath finderPath = _finderPathCountByG_U;

			Object[] finderArgs = new Object[] {groupId, userId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_U_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_U_USERID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(userId);

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(finderPath, finderArgs, count);
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
	 * Returns the number of document library file entries that the user has permission to view where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @return the number of matching document library file entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_U(long groupId, long userId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_U(groupId, userId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DLFileEntry> dlFileEntries = findByG_U(groupId, userId);

			dlFileEntries = InlineSQLHelperUtil.filter(dlFileEntries, groupId);

			return dlFileEntries.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_DLFILEENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_U_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_USERID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFileEntry.class.getName(),
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

	private static final String _FINDER_COLUMN_G_U_GROUPID_2 =
		"dlFileEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_U_USERID_2 =
		"dlFileEntry.userId = ?";

	private FinderPath _finderPathWithPaginationFindByG_F;
	private FinderPath _finderPathWithoutPaginationFindByG_F;
	private FinderPath _finderPathCountByG_F;
	private FinderPath _finderPathWithPaginationCountByG_F;

	/**
	 * Returns all the document library file entries where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @return the matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByG_F(long groupId, long folderId) {
		return findByG_F(
			groupId, folderId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file entries where groupId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByG_F(
		long groupId, long folderId, int start, int end) {

		return findByG_F(groupId, folderId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByG_F(
		long groupId, long folderId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return findByG_F(
			groupId, folderId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByG_F(
		long groupId, long folderId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_F;
					finderArgs = new Object[] {groupId, folderId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_F;
				finderArgs = new Object[] {
					groupId, folderId, start, end, orderByComparator
				};
			}

			List<DLFileEntry> list = null;

			if (useFinderCache) {
				list = (List<DLFileEntry>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFileEntry dlFileEntry : list) {
						if ((groupId != dlFileEntry.getGroupId()) ||
							(folderId != dlFileEntry.getFolderId())) {

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

				sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_F_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_F_FOLDERID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(folderId);

					list = (List<DLFileEntry>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(finderPath, finderArgs, list);
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
	 * Returns the first document library file entry in the ordered set where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByG_F_First(
			long groupId, long folderId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByG_F_First(
			groupId, folderId, orderByComparator);

		if (dlFileEntry != null) {
			return dlFileEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", folderId=");
		sb.append(folderId);

		sb.append("}");

		throw new NoSuchFileEntryException(sb.toString());
	}

	/**
	 * Returns the first document library file entry in the ordered set where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByG_F_First(
		long groupId, long folderId,
		OrderByComparator<DLFileEntry> orderByComparator) {

		List<DLFileEntry> list = findByG_F(
			groupId, folderId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library file entry in the ordered set where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByG_F_Last(
			long groupId, long folderId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByG_F_Last(
			groupId, folderId, orderByComparator);

		if (dlFileEntry != null) {
			return dlFileEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", folderId=");
		sb.append(folderId);

		sb.append("}");

		throw new NoSuchFileEntryException(sb.toString());
	}

	/**
	 * Returns the last document library file entry in the ordered set where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByG_F_Last(
		long groupId, long folderId,
		OrderByComparator<DLFileEntry> orderByComparator) {

		int count = countByG_F(groupId, folderId);

		if (count == 0) {
			return null;
		}

		List<DLFileEntry> list = findByG_F(
			groupId, folderId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set where groupId = &#63; and folderId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	@Override
	public DLFileEntry[] findByG_F_PrevAndNext(
			long fileEntryId, long groupId, long folderId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = findByPrimaryKey(fileEntryId);

		Session session = null;

		try {
			session = openSession();

			DLFileEntry[] array = new DLFileEntryImpl[3];

			array[0] = getByG_F_PrevAndNext(
				session, dlFileEntry, groupId, folderId, orderByComparator,
				true);

			array[1] = dlFileEntry;

			array[2] = getByG_F_PrevAndNext(
				session, dlFileEntry, groupId, folderId, orderByComparator,
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

	protected DLFileEntry getByG_F_PrevAndNext(
		Session session, DLFileEntry dlFileEntry, long groupId, long folderId,
		OrderByComparator<DLFileEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_F_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_F_FOLDERID_2);

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
			sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(folderId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFileEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFileEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the document library file entries that the user has permission to view where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @return the matching document library file entries that the user has permission to view
	 */
	@Override
	public List<DLFileEntry> filterFindByG_F(long groupId, long folderId) {
		return filterFindByG_F(
			groupId, folderId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file entries that the user has permission to view where groupId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries that the user has permission to view
	 */
	@Override
	public List<DLFileEntry> filterFindByG_F(
		long groupId, long folderId, int start, int end) {

		return filterFindByG_F(groupId, folderId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file entries that the user has permissions to view where groupId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries that the user has permission to view
	 */
	@Override
	public List<DLFileEntry> filterFindByG_F(
		long groupId, long folderId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_F(groupId, folderId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_F(
					groupId, folderId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_DLFILEENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DLFILEENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_F_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_F_FOLDERID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DLFILEENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DLFileEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DLFileEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFileEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DLFileEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DLFileEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(folderId);

			return (List<DLFileEntry>)QueryUtil.list(
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
	 * Returns the document library file entries before and after the current document library file entry in the ordered set of document library file entries that the user has permission to view where groupId = &#63; and folderId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	@Override
	public DLFileEntry[] filterFindByG_F_PrevAndNext(
			long fileEntryId, long groupId, long folderId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_F_PrevAndNext(
				fileEntryId, groupId, folderId, orderByComparator);
		}

		DLFileEntry dlFileEntry = findByPrimaryKey(fileEntryId);

		Session session = null;

		try {
			session = openSession();

			DLFileEntry[] array = new DLFileEntryImpl[3];

			array[0] = filterGetByG_F_PrevAndNext(
				session, dlFileEntry, groupId, folderId, orderByComparator,
				true);

			array[1] = dlFileEntry;

			array[2] = filterGetByG_F_PrevAndNext(
				session, dlFileEntry, groupId, folderId, orderByComparator,
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

	protected DLFileEntry filterGetByG_F_PrevAndNext(
		Session session, DLFileEntry dlFileEntry, long groupId, long folderId,
		OrderByComparator<DLFileEntry> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_DLFILEENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DLFILEENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_F_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_F_FOLDERID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DLFILEENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DLFileEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DLFileEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFileEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DLFileEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DLFileEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(folderId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFileEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFileEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the document library file entries that the user has permission to view where groupId = &#63; and folderId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @return the matching document library file entries that the user has permission to view
	 */
	@Override
	public List<DLFileEntry> filterFindByG_F(long groupId, long[] folderIds) {
		return filterFindByG_F(
			groupId, folderIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file entries that the user has permission to view where groupId = &#63; and folderId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries that the user has permission to view
	 */
	@Override
	public List<DLFileEntry> filterFindByG_F(
		long groupId, long[] folderIds, int start, int end) {

		return filterFindByG_F(groupId, folderIds, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file entries that the user has permission to view where groupId = &#63; and folderId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries that the user has permission to view
	 */
	@Override
	public List<DLFileEntry> filterFindByG_F(
		long groupId, long[] folderIds, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_F(groupId, folderIds, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_F(
					groupId, folderIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator),
				groupId);
		}

		if (folderIds == null) {
			folderIds = new long[0];
		}
		else if (folderIds.length > 1) {
			folderIds = ArrayUtil.sortedUnique(folderIds);
		}

		StringBundler sb = new StringBundler();

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_DLFILEENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DLFILEENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_F_GROUPID_2);

		if (folderIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_F_FOLDERID_7);

			sb.append(StringUtil.merge(folderIds));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DLFILEENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DLFileEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DLFileEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFileEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DLFileEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DLFileEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			return (List<DLFileEntry>)QueryUtil.list(
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
	 * Returns all the document library file entries where groupId = &#63; and folderId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @return the matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByG_F(long groupId, long[] folderIds) {
		return findByG_F(
			groupId, folderIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file entries where groupId = &#63; and folderId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByG_F(
		long groupId, long[] folderIds, int start, int end) {

		return findByG_F(groupId, folderIds, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and folderId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByG_F(
		long groupId, long[] folderIds, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return findByG_F(
			groupId, folderIds, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and folderId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByG_F(
		long groupId, long[] folderIds, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		if (folderIds == null) {
			folderIds = new long[0];
		}
		else if (folderIds.length > 1) {
			folderIds = ArrayUtil.sortedUnique(folderIds);
		}

		if (folderIds.length == 1) {
			return findByG_F(
				groupId, folderIds[0], start, end, orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						groupId, StringUtil.merge(folderIds)
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					groupId, StringUtil.merge(folderIds), start, end,
					orderByComparator
				};
			}

			List<DLFileEntry> list = null;

			if (useFinderCache) {
				list = (List<DLFileEntry>)FinderCacheUtil.getResult(
					_finderPathWithPaginationFindByG_F, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFileEntry dlFileEntry : list) {
						if ((groupId != dlFileEntry.getGroupId()) ||
							!ArrayUtil.contains(
								folderIds, dlFileEntry.getFolderId())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_F_GROUPID_2);

				if (folderIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_F_FOLDERID_7);

					sb.append(StringUtil.merge(folderIds));

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
					sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					list = (List<DLFileEntry>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(
							_finderPathWithPaginationFindByG_F, finderArgs,
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
	 * Removes all the document library file entries where groupId = &#63; and folderId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 */
	@Override
	public void removeByG_F(long groupId, long folderId) {
		for (DLFileEntry dlFileEntry :
				findByG_F(
					groupId, folderId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(dlFileEntry);
		}
	}

	/**
	 * Returns the number of document library file entries where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByG_F(long groupId, long folderId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			FinderPath finderPath = _finderPathCountByG_F;

			Object[] finderArgs = new Object[] {groupId, folderId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_F_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_F_FOLDERID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(folderId);

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(finderPath, finderArgs, count);
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
	 * Returns the number of document library file entries where groupId = &#63; and folderId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByG_F(long groupId, long[] folderIds) {
		if (folderIds == null) {
			folderIds = new long[0];
		}
		else if (folderIds.length > 1) {
			folderIds = ArrayUtil.sortedUnique(folderIds);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			Object[] finderArgs = new Object[] {
				groupId, StringUtil.merge(folderIds)
			};

			Long count = (Long)FinderCacheUtil.getResult(
				_finderPathWithPaginationCountByG_F, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_F_GROUPID_2);

				if (folderIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_F_FOLDERID_7);

					sb.append(StringUtil.merge(folderIds));

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

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(
						_finderPathWithPaginationCountByG_F, finderArgs, count);
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
	 * Returns the number of document library file entries that the user has permission to view where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @return the number of matching document library file entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_F(long groupId, long folderId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_F(groupId, folderId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DLFileEntry> dlFileEntries = findByG_F(groupId, folderId);

			dlFileEntries = InlineSQLHelperUtil.filter(dlFileEntries, groupId);

			return dlFileEntries.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_DLFILEENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_F_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_F_FOLDERID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFileEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(folderId);

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
	 * Returns the number of document library file entries that the user has permission to view where groupId = &#63; and folderId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @return the number of matching document library file entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_F(long groupId, long[] folderIds) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_F(groupId, folderIds);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DLFileEntry> dlFileEntries = InlineSQLHelperUtil.filter(
				findByG_F(groupId, folderIds), groupId);

			return dlFileEntries.size();
		}

		if (folderIds == null) {
			folderIds = new long[0];
		}
		else if (folderIds.length > 1) {
			folderIds = ArrayUtil.sortedUnique(folderIds);
		}

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_DLFILEENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_F_GROUPID_2);

		if (folderIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_F_FOLDERID_7);

			sb.append(StringUtil.merge(folderIds));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFileEntry.class.getName(),
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

	private static final String _FINDER_COLUMN_G_F_GROUPID_2 =
		"dlFileEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_F_FOLDERID_2 =
		"dlFileEntry.folderId = ?";

	private static final String _FINDER_COLUMN_G_F_FOLDERID_7 =
		"dlFileEntry.folderId IN (";

	private FinderPath _finderPathWithPaginationFindByR_F;
	private FinderPath _finderPathWithoutPaginationFindByR_F;
	private FinderPath _finderPathCountByR_F;

	/**
	 * Returns all the document library file entries where repositoryId = &#63; and folderId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param folderId the folder ID
	 * @return the matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByR_F(long repositoryId, long folderId) {
		return findByR_F(
			repositoryId, folderId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file entries where repositoryId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param repositoryId the repository ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByR_F(
		long repositoryId, long folderId, int start, int end) {

		return findByR_F(repositoryId, folderId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file entries where repositoryId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param repositoryId the repository ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByR_F(
		long repositoryId, long folderId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return findByR_F(
			repositoryId, folderId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where repositoryId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param repositoryId the repository ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByR_F(
		long repositoryId, long folderId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByR_F;
					finderArgs = new Object[] {repositoryId, folderId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByR_F;
				finderArgs = new Object[] {
					repositoryId, folderId, start, end, orderByComparator
				};
			}

			List<DLFileEntry> list = null;

			if (useFinderCache) {
				list = (List<DLFileEntry>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFileEntry dlFileEntry : list) {
						if ((repositoryId != dlFileEntry.getRepositoryId()) ||
							(folderId != dlFileEntry.getFolderId())) {

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

				sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_R_F_REPOSITORYID_2);

				sb.append(_FINDER_COLUMN_R_F_FOLDERID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(repositoryId);

					queryPos.add(folderId);

					list = (List<DLFileEntry>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(finderPath, finderArgs, list);
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
	 * Returns the first document library file entry in the ordered set where repositoryId = &#63; and folderId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByR_F_First(
			long repositoryId, long folderId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByR_F_First(
			repositoryId, folderId, orderByComparator);

		if (dlFileEntry != null) {
			return dlFileEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("repositoryId=");
		sb.append(repositoryId);

		sb.append(", folderId=");
		sb.append(folderId);

		sb.append("}");

		throw new NoSuchFileEntryException(sb.toString());
	}

	/**
	 * Returns the first document library file entry in the ordered set where repositoryId = &#63; and folderId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByR_F_First(
		long repositoryId, long folderId,
		OrderByComparator<DLFileEntry> orderByComparator) {

		List<DLFileEntry> list = findByR_F(
			repositoryId, folderId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library file entry in the ordered set where repositoryId = &#63; and folderId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByR_F_Last(
			long repositoryId, long folderId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByR_F_Last(
			repositoryId, folderId, orderByComparator);

		if (dlFileEntry != null) {
			return dlFileEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("repositoryId=");
		sb.append(repositoryId);

		sb.append(", folderId=");
		sb.append(folderId);

		sb.append("}");

		throw new NoSuchFileEntryException(sb.toString());
	}

	/**
	 * Returns the last document library file entry in the ordered set where repositoryId = &#63; and folderId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByR_F_Last(
		long repositoryId, long folderId,
		OrderByComparator<DLFileEntry> orderByComparator) {

		int count = countByR_F(repositoryId, folderId);

		if (count == 0) {
			return null;
		}

		List<DLFileEntry> list = findByR_F(
			repositoryId, folderId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set where repositoryId = &#63; and folderId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param repositoryId the repository ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	@Override
	public DLFileEntry[] findByR_F_PrevAndNext(
			long fileEntryId, long repositoryId, long folderId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = findByPrimaryKey(fileEntryId);

		Session session = null;

		try {
			session = openSession();

			DLFileEntry[] array = new DLFileEntryImpl[3];

			array[0] = getByR_F_PrevAndNext(
				session, dlFileEntry, repositoryId, folderId, orderByComparator,
				true);

			array[1] = dlFileEntry;

			array[2] = getByR_F_PrevAndNext(
				session, dlFileEntry, repositoryId, folderId, orderByComparator,
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

	protected DLFileEntry getByR_F_PrevAndNext(
		Session session, DLFileEntry dlFileEntry, long repositoryId,
		long folderId, OrderByComparator<DLFileEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

		sb.append(_FINDER_COLUMN_R_F_REPOSITORYID_2);

		sb.append(_FINDER_COLUMN_R_F_FOLDERID_2);

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
			sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(repositoryId);

		queryPos.add(folderId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFileEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFileEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library file entries where repositoryId = &#63; and folderId = &#63; from the database.
	 *
	 * @param repositoryId the repository ID
	 * @param folderId the folder ID
	 */
	@Override
	public void removeByR_F(long repositoryId, long folderId) {
		for (DLFileEntry dlFileEntry :
				findByR_F(
					repositoryId, folderId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(dlFileEntry);
		}
	}

	/**
	 * Returns the number of document library file entries where repositoryId = &#63; and folderId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param folderId the folder ID
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByR_F(long repositoryId, long folderId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			FinderPath finderPath = _finderPathCountByR_F;

			Object[] finderArgs = new Object[] {repositoryId, folderId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_R_F_REPOSITORYID_2);

				sb.append(_FINDER_COLUMN_R_F_FOLDERID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(repositoryId);

					queryPos.add(folderId);

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(finderPath, finderArgs, count);
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

	private static final String _FINDER_COLUMN_R_F_REPOSITORYID_2 =
		"dlFileEntry.repositoryId = ? AND ";

	private static final String _FINDER_COLUMN_R_F_FOLDERID_2 =
		"dlFileEntry.folderId = ?";

	private FinderPath _finderPathWithPaginationFindByF_N;
	private FinderPath _finderPathWithoutPaginationFindByF_N;
	private FinderPath _finderPathCountByF_N;

	/**
	 * Returns all the document library file entries where folderId = &#63; and name = &#63;.
	 *
	 * @param folderId the folder ID
	 * @param name the name
	 * @return the matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByF_N(long folderId, String name) {
		return findByF_N(
			folderId, name, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file entries where folderId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param folderId the folder ID
	 * @param name the name
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByF_N(
		long folderId, String name, int start, int end) {

		return findByF_N(folderId, name, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file entries where folderId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param folderId the folder ID
	 * @param name the name
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByF_N(
		long folderId, String name, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return findByF_N(folderId, name, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where folderId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param folderId the folder ID
	 * @param name the name
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByF_N(
		long folderId, String name, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			name = Objects.toString(name, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByF_N;
					finderArgs = new Object[] {folderId, name};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByF_N;
				finderArgs = new Object[] {
					folderId, name, start, end, orderByComparator
				};
			}

			List<DLFileEntry> list = null;

			if (useFinderCache) {
				list = (List<DLFileEntry>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFileEntry dlFileEntry : list) {
						if ((folderId != dlFileEntry.getFolderId()) ||
							!name.equals(dlFileEntry.getName())) {

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

				sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_F_N_FOLDERID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_F_N_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_F_N_NAME_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(folderId);

					if (bindName) {
						queryPos.add(name);
					}

					list = (List<DLFileEntry>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(finderPath, finderArgs, list);
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
	 * Returns the first document library file entry in the ordered set where folderId = &#63; and name = &#63;.
	 *
	 * @param folderId the folder ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByF_N_First(
			long folderId, String name,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByF_N_First(
			folderId, name, orderByComparator);

		if (dlFileEntry != null) {
			return dlFileEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("folderId=");
		sb.append(folderId);

		sb.append(", name=");
		sb.append(name);

		sb.append("}");

		throw new NoSuchFileEntryException(sb.toString());
	}

	/**
	 * Returns the first document library file entry in the ordered set where folderId = &#63; and name = &#63;.
	 *
	 * @param folderId the folder ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByF_N_First(
		long folderId, String name,
		OrderByComparator<DLFileEntry> orderByComparator) {

		List<DLFileEntry> list = findByF_N(
			folderId, name, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library file entry in the ordered set where folderId = &#63; and name = &#63;.
	 *
	 * @param folderId the folder ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByF_N_Last(
			long folderId, String name,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByF_N_Last(
			folderId, name, orderByComparator);

		if (dlFileEntry != null) {
			return dlFileEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("folderId=");
		sb.append(folderId);

		sb.append(", name=");
		sb.append(name);

		sb.append("}");

		throw new NoSuchFileEntryException(sb.toString());
	}

	/**
	 * Returns the last document library file entry in the ordered set where folderId = &#63; and name = &#63;.
	 *
	 * @param folderId the folder ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByF_N_Last(
		long folderId, String name,
		OrderByComparator<DLFileEntry> orderByComparator) {

		int count = countByF_N(folderId, name);

		if (count == 0) {
			return null;
		}

		List<DLFileEntry> list = findByF_N(
			folderId, name, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set where folderId = &#63; and name = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param folderId the folder ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	@Override
	public DLFileEntry[] findByF_N_PrevAndNext(
			long fileEntryId, long folderId, String name,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		name = Objects.toString(name, "");

		DLFileEntry dlFileEntry = findByPrimaryKey(fileEntryId);

		Session session = null;

		try {
			session = openSession();

			DLFileEntry[] array = new DLFileEntryImpl[3];

			array[0] = getByF_N_PrevAndNext(
				session, dlFileEntry, folderId, name, orderByComparator, true);

			array[1] = dlFileEntry;

			array[2] = getByF_N_PrevAndNext(
				session, dlFileEntry, folderId, name, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DLFileEntry getByF_N_PrevAndNext(
		Session session, DLFileEntry dlFileEntry, long folderId, String name,
		OrderByComparator<DLFileEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

		sb.append(_FINDER_COLUMN_F_N_FOLDERID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_F_N_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_F_N_NAME_2);
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
			sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(folderId);

		if (bindName) {
			queryPos.add(name);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFileEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFileEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library file entries where folderId = &#63; and name = &#63; from the database.
	 *
	 * @param folderId the folder ID
	 * @param name the name
	 */
	@Override
	public void removeByF_N(long folderId, String name) {
		for (DLFileEntry dlFileEntry :
				findByF_N(
					folderId, name, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(dlFileEntry);
		}
	}

	/**
	 * Returns the number of document library file entries where folderId = &#63; and name = &#63;.
	 *
	 * @param folderId the folder ID
	 * @param name the name
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByF_N(long folderId, String name) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			name = Objects.toString(name, "");

			FinderPath finderPath = _finderPathCountByF_N;

			Object[] finderArgs = new Object[] {folderId, name};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_F_N_FOLDERID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_F_N_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_F_N_NAME_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(folderId);

					if (bindName) {
						queryPos.add(name);
					}

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(finderPath, finderArgs, count);
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

	private static final String _FINDER_COLUMN_F_N_FOLDERID_2 =
		"dlFileEntry.folderId = ? AND ";

	private static final String _FINDER_COLUMN_F_N_NAME_2 =
		"dlFileEntry.name = ?";

	private static final String _FINDER_COLUMN_F_N_NAME_3 =
		"(dlFileEntry.name IS NULL OR dlFileEntry.name = '')";

	private FinderPath _finderPathWithPaginationFindByG_U_F;
	private FinderPath _finderPathWithoutPaginationFindByG_U_F;
	private FinderPath _finderPathCountByG_U_F;
	private FinderPath _finderPathWithPaginationCountByG_U_F;

	/**
	 * Returns all the document library file entries where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @return the matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByG_U_F(
		long groupId, long userId, long folderId) {

		return findByG_U_F(
			groupId, userId, folderId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the document library file entries where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByG_U_F(
		long groupId, long userId, long folderId, int start, int end) {

		return findByG_U_F(groupId, userId, folderId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByG_U_F(
		long groupId, long userId, long folderId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return findByG_U_F(
			groupId, userId, folderId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByG_U_F(
		long groupId, long userId, long folderId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_U_F;
					finderArgs = new Object[] {groupId, userId, folderId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_U_F;
				finderArgs = new Object[] {
					groupId, userId, folderId, start, end, orderByComparator
				};
			}

			List<DLFileEntry> list = null;

			if (useFinderCache) {
				list = (List<DLFileEntry>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFileEntry dlFileEntry : list) {
						if ((groupId != dlFileEntry.getGroupId()) ||
							(userId != dlFileEntry.getUserId()) ||
							(folderId != dlFileEntry.getFolderId())) {

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

				sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_U_F_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_U_F_USERID_2);

				sb.append(_FINDER_COLUMN_G_U_F_FOLDERID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(userId);

					queryPos.add(folderId);

					list = (List<DLFileEntry>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(finderPath, finderArgs, list);
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
	 * Returns the first document library file entry in the ordered set where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByG_U_F_First(
			long groupId, long userId, long folderId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByG_U_F_First(
			groupId, userId, folderId, orderByComparator);

		if (dlFileEntry != null) {
			return dlFileEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append(", folderId=");
		sb.append(folderId);

		sb.append("}");

		throw new NoSuchFileEntryException(sb.toString());
	}

	/**
	 * Returns the first document library file entry in the ordered set where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByG_U_F_First(
		long groupId, long userId, long folderId,
		OrderByComparator<DLFileEntry> orderByComparator) {

		List<DLFileEntry> list = findByG_U_F(
			groupId, userId, folderId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library file entry in the ordered set where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByG_U_F_Last(
			long groupId, long userId, long folderId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByG_U_F_Last(
			groupId, userId, folderId, orderByComparator);

		if (dlFileEntry != null) {
			return dlFileEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append(", folderId=");
		sb.append(folderId);

		sb.append("}");

		throw new NoSuchFileEntryException(sb.toString());
	}

	/**
	 * Returns the last document library file entry in the ordered set where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByG_U_F_Last(
		long groupId, long userId, long folderId,
		OrderByComparator<DLFileEntry> orderByComparator) {

		int count = countByG_U_F(groupId, userId, folderId);

		if (count == 0) {
			return null;
		}

		List<DLFileEntry> list = findByG_U_F(
			groupId, userId, folderId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	@Override
	public DLFileEntry[] findByG_U_F_PrevAndNext(
			long fileEntryId, long groupId, long userId, long folderId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = findByPrimaryKey(fileEntryId);

		Session session = null;

		try {
			session = openSession();

			DLFileEntry[] array = new DLFileEntryImpl[3];

			array[0] = getByG_U_F_PrevAndNext(
				session, dlFileEntry, groupId, userId, folderId,
				orderByComparator, true);

			array[1] = dlFileEntry;

			array[2] = getByG_U_F_PrevAndNext(
				session, dlFileEntry, groupId, userId, folderId,
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

	protected DLFileEntry getByG_U_F_PrevAndNext(
		Session session, DLFileEntry dlFileEntry, long groupId, long userId,
		long folderId, OrderByComparator<DLFileEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_U_F_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_F_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_F_FOLDERID_2);

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
			sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(userId);

		queryPos.add(folderId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFileEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFileEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the document library file entries that the user has permission to view where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @return the matching document library file entries that the user has permission to view
	 */
	@Override
	public List<DLFileEntry> filterFindByG_U_F(
		long groupId, long userId, long folderId) {

		return filterFindByG_U_F(
			groupId, userId, folderId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the document library file entries that the user has permission to view where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries that the user has permission to view
	 */
	@Override
	public List<DLFileEntry> filterFindByG_U_F(
		long groupId, long userId, long folderId, int start, int end) {

		return filterFindByG_U_F(groupId, userId, folderId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file entries that the user has permissions to view where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries that the user has permission to view
	 */
	@Override
	public List<DLFileEntry> filterFindByG_U_F(
		long groupId, long userId, long folderId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_U_F(
				groupId, userId, folderId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_U_F(
					groupId, userId, folderId, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_DLFILEENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DLFILEENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_F_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_F_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_F_FOLDERID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DLFILEENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DLFileEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DLFileEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFileEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DLFileEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DLFileEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(userId);

			queryPos.add(folderId);

			return (List<DLFileEntry>)QueryUtil.list(
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
	 * Returns the document library file entries before and after the current document library file entry in the ordered set of document library file entries that the user has permission to view where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	@Override
	public DLFileEntry[] filterFindByG_U_F_PrevAndNext(
			long fileEntryId, long groupId, long userId, long folderId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_U_F_PrevAndNext(
				fileEntryId, groupId, userId, folderId, orderByComparator);
		}

		DLFileEntry dlFileEntry = findByPrimaryKey(fileEntryId);

		Session session = null;

		try {
			session = openSession();

			DLFileEntry[] array = new DLFileEntryImpl[3];

			array[0] = filterGetByG_U_F_PrevAndNext(
				session, dlFileEntry, groupId, userId, folderId,
				orderByComparator, true);

			array[1] = dlFileEntry;

			array[2] = filterGetByG_U_F_PrevAndNext(
				session, dlFileEntry, groupId, userId, folderId,
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

	protected DLFileEntry filterGetByG_U_F_PrevAndNext(
		Session session, DLFileEntry dlFileEntry, long groupId, long userId,
		long folderId, OrderByComparator<DLFileEntry> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_DLFILEENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DLFILEENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_F_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_F_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_F_FOLDERID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DLFILEENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DLFileEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DLFileEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFileEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DLFileEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DLFileEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(userId);

		queryPos.add(folderId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFileEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFileEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the document library file entries that the user has permission to view where groupId = &#63; and userId = &#63; and folderId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderIds the folder IDs
	 * @return the matching document library file entries that the user has permission to view
	 */
	@Override
	public List<DLFileEntry> filterFindByG_U_F(
		long groupId, long userId, long[] folderIds) {

		return filterFindByG_U_F(
			groupId, userId, folderIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the document library file entries that the user has permission to view where groupId = &#63; and userId = &#63; and folderId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderIds the folder IDs
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries that the user has permission to view
	 */
	@Override
	public List<DLFileEntry> filterFindByG_U_F(
		long groupId, long userId, long[] folderIds, int start, int end) {

		return filterFindByG_U_F(groupId, userId, folderIds, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file entries that the user has permission to view where groupId = &#63; and userId = &#63; and folderId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderIds the folder IDs
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries that the user has permission to view
	 */
	@Override
	public List<DLFileEntry> filterFindByG_U_F(
		long groupId, long userId, long[] folderIds, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_U_F(
				groupId, userId, folderIds, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_U_F(
					groupId, userId, folderIds, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		if (folderIds == null) {
			folderIds = new long[0];
		}
		else if (folderIds.length > 1) {
			folderIds = ArrayUtil.sortedUnique(folderIds);
		}

		StringBundler sb = new StringBundler();

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_DLFILEENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DLFILEENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_F_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_F_USERID_2);

		if (folderIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_U_F_FOLDERID_7);

			sb.append(StringUtil.merge(folderIds));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DLFILEENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DLFileEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DLFileEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFileEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DLFileEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DLFileEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(userId);

			return (List<DLFileEntry>)QueryUtil.list(
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
	 * Returns all the document library file entries where groupId = &#63; and userId = &#63; and folderId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderIds the folder IDs
	 * @return the matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByG_U_F(
		long groupId, long userId, long[] folderIds) {

		return findByG_U_F(
			groupId, userId, folderIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the document library file entries where groupId = &#63; and userId = &#63; and folderId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderIds the folder IDs
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByG_U_F(
		long groupId, long userId, long[] folderIds, int start, int end) {

		return findByG_U_F(groupId, userId, folderIds, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and userId = &#63; and folderId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderIds the folder IDs
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByG_U_F(
		long groupId, long userId, long[] folderIds, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return findByG_U_F(
			groupId, userId, folderIds, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and userId = &#63; and folderId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderIds the folder IDs
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByG_U_F(
		long groupId, long userId, long[] folderIds, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		if (folderIds == null) {
			folderIds = new long[0];
		}
		else if (folderIds.length > 1) {
			folderIds = ArrayUtil.sortedUnique(folderIds);
		}

		if (folderIds.length == 1) {
			return findByG_U_F(
				groupId, userId, folderIds[0], start, end, orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						groupId, userId, StringUtil.merge(folderIds)
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					groupId, userId, StringUtil.merge(folderIds), start, end,
					orderByComparator
				};
			}

			List<DLFileEntry> list = null;

			if (useFinderCache) {
				list = (List<DLFileEntry>)FinderCacheUtil.getResult(
					_finderPathWithPaginationFindByG_U_F, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFileEntry dlFileEntry : list) {
						if ((groupId != dlFileEntry.getGroupId()) ||
							(userId != dlFileEntry.getUserId()) ||
							!ArrayUtil.contains(
								folderIds, dlFileEntry.getFolderId())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_U_F_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_U_F_USERID_2);

				if (folderIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_U_F_FOLDERID_7);

					sb.append(StringUtil.merge(folderIds));

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
					sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(userId);

					list = (List<DLFileEntry>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(
							_finderPathWithPaginationFindByG_U_F, finderArgs,
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
	 * Removes all the document library file entries where groupId = &#63; and userId = &#63; and folderId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 */
	@Override
	public void removeByG_U_F(long groupId, long userId, long folderId) {
		for (DLFileEntry dlFileEntry :
				findByG_U_F(
					groupId, userId, folderId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(dlFileEntry);
		}
	}

	/**
	 * Returns the number of document library file entries where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByG_U_F(long groupId, long userId, long folderId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			FinderPath finderPath = _finderPathCountByG_U_F;

			Object[] finderArgs = new Object[] {groupId, userId, folderId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_U_F_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_U_F_USERID_2);

				sb.append(_FINDER_COLUMN_G_U_F_FOLDERID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(userId);

					queryPos.add(folderId);

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(finderPath, finderArgs, count);
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
	 * Returns the number of document library file entries where groupId = &#63; and userId = &#63; and folderId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderIds the folder IDs
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByG_U_F(long groupId, long userId, long[] folderIds) {
		if (folderIds == null) {
			folderIds = new long[0];
		}
		else if (folderIds.length > 1) {
			folderIds = ArrayUtil.sortedUnique(folderIds);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			Object[] finderArgs = new Object[] {
				groupId, userId, StringUtil.merge(folderIds)
			};

			Long count = (Long)FinderCacheUtil.getResult(
				_finderPathWithPaginationCountByG_U_F, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_U_F_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_U_F_USERID_2);

				if (folderIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_U_F_FOLDERID_7);

					sb.append(StringUtil.merge(folderIds));

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

					FinderCacheUtil.putResult(
						_finderPathWithPaginationCountByG_U_F, finderArgs,
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
	 * Returns the number of document library file entries that the user has permission to view where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @return the number of matching document library file entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_U_F(long groupId, long userId, long folderId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_U_F(groupId, userId, folderId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DLFileEntry> dlFileEntries = findByG_U_F(
				groupId, userId, folderId);

			dlFileEntries = InlineSQLHelperUtil.filter(dlFileEntries, groupId);

			return dlFileEntries.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_DLFILEENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_U_F_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_F_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_F_FOLDERID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFileEntry.class.getName(),
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

			queryPos.add(folderId);

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
	 * Returns the number of document library file entries that the user has permission to view where groupId = &#63; and userId = &#63; and folderId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderIds the folder IDs
	 * @return the number of matching document library file entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_U_F(long groupId, long userId, long[] folderIds) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_U_F(groupId, userId, folderIds);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DLFileEntry> dlFileEntries = InlineSQLHelperUtil.filter(
				findByG_U_F(groupId, userId, folderIds), groupId);

			return dlFileEntries.size();
		}

		if (folderIds == null) {
			folderIds = new long[0];
		}
		else if (folderIds.length > 1) {
			folderIds = ArrayUtil.sortedUnique(folderIds);
		}

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_DLFILEENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_U_F_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_F_USERID_2);

		if (folderIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_U_F_FOLDERID_7);

			sb.append(StringUtil.merge(folderIds));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFileEntry.class.getName(),
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

	private static final String _FINDER_COLUMN_G_U_F_GROUPID_2 =
		"dlFileEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_U_F_USERID_2 =
		"dlFileEntry.userId = ? AND ";

	private static final String _FINDER_COLUMN_G_U_F_FOLDERID_2 =
		"dlFileEntry.folderId = ?";

	private static final String _FINDER_COLUMN_G_U_F_FOLDERID_7 =
		"dlFileEntry.folderId IN (";

	private FinderPath _finderPathFetchByG_F_N;

	/**
	 * Returns the document library file entry where groupId = &#63; and folderId = &#63; and name = &#63; or throws a <code>NoSuchFileEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param name the name
	 * @return the matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByG_F_N(long groupId, long folderId, String name)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByG_F_N(groupId, folderId, name);

		if (dlFileEntry == null) {
			StringBundler sb = new StringBundler(8);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("groupId=");
			sb.append(groupId);

			sb.append(", folderId=");
			sb.append(folderId);

			sb.append(", name=");
			sb.append(name);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchFileEntryException(sb.toString());
		}

		return dlFileEntry;
	}

	/**
	 * Returns the document library file entry where groupId = &#63; and folderId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param name the name
	 * @return the matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByG_F_N(long groupId, long folderId, String name) {
		return fetchByG_F_N(groupId, folderId, name, true);
	}

	/**
	 * Returns the document library file entry where groupId = &#63; and folderId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByG_F_N(
		long groupId, long folderId, String name, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			name = Objects.toString(name, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {groupId, folderId, name};
			}

			Object result = null;

			if (useFinderCache) {
				result = FinderCacheUtil.getResult(
					_finderPathFetchByG_F_N, finderArgs, this);
			}

			if (result instanceof DLFileEntry) {
				DLFileEntry dlFileEntry = (DLFileEntry)result;

				if ((groupId != dlFileEntry.getGroupId()) ||
					(folderId != dlFileEntry.getFolderId()) ||
					!Objects.equals(name, dlFileEntry.getName())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_F_N_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_F_N_FOLDERID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_F_N_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_G_F_N_NAME_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(folderId);

					if (bindName) {
						queryPos.add(name);
					}

					List<DLFileEntry> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							FinderCacheUtil.putResult(
								_finderPathFetchByG_F_N, finderArgs, list);
						}
					}
					else {
						DLFileEntry dlFileEntry = list.get(0);

						result = dlFileEntry;

						cacheResult(dlFileEntry);
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
				return (DLFileEntry)result;
			}
		}
	}

	/**
	 * Removes the document library file entry where groupId = &#63; and folderId = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param name the name
	 * @return the document library file entry that was removed
	 */
	@Override
	public DLFileEntry removeByG_F_N(long groupId, long folderId, String name)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = findByG_F_N(groupId, folderId, name);

		return remove(dlFileEntry);
	}

	/**
	 * Returns the number of document library file entries where groupId = &#63; and folderId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param name the name
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByG_F_N(long groupId, long folderId, String name) {
		DLFileEntry dlFileEntry = fetchByG_F_N(groupId, folderId, name);

		if (dlFileEntry == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_G_F_N_GROUPID_2 =
		"dlFileEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_F_N_FOLDERID_2 =
		"dlFileEntry.folderId = ? AND ";

	private static final String _FINDER_COLUMN_G_F_N_NAME_2 =
		"dlFileEntry.name = ?";

	private static final String _FINDER_COLUMN_G_F_N_NAME_3 =
		"(dlFileEntry.name IS NULL OR dlFileEntry.name = '')";

	private FinderPath _finderPathFetchByG_F_FN;

	/**
	 * Returns the document library file entry where groupId = &#63; and folderId = &#63; and fileName = &#63; or throws a <code>NoSuchFileEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileName the file name
	 * @return the matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByG_F_FN(
			long groupId, long folderId, String fileName)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByG_F_FN(groupId, folderId, fileName);

		if (dlFileEntry == null) {
			StringBundler sb = new StringBundler(8);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("groupId=");
			sb.append(groupId);

			sb.append(", folderId=");
			sb.append(folderId);

			sb.append(", fileName=");
			sb.append(fileName);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchFileEntryException(sb.toString());
		}

		return dlFileEntry;
	}

	/**
	 * Returns the document library file entry where groupId = &#63; and folderId = &#63; and fileName = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileName the file name
	 * @return the matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByG_F_FN(
		long groupId, long folderId, String fileName) {

		return fetchByG_F_FN(groupId, folderId, fileName, true);
	}

	/**
	 * Returns the document library file entry where groupId = &#63; and folderId = &#63; and fileName = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileName the file name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByG_F_FN(
		long groupId, long folderId, String fileName, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			fileName = Objects.toString(fileName, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {groupId, folderId, fileName};
			}

			Object result = null;

			if (useFinderCache) {
				result = FinderCacheUtil.getResult(
					_finderPathFetchByG_F_FN, finderArgs, this);
			}

			if (result instanceof DLFileEntry) {
				DLFileEntry dlFileEntry = (DLFileEntry)result;

				if ((groupId != dlFileEntry.getGroupId()) ||
					(folderId != dlFileEntry.getFolderId()) ||
					!Objects.equals(fileName, dlFileEntry.getFileName())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_F_FN_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_F_FN_FOLDERID_2);

				boolean bindFileName = false;

				if (fileName.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_F_FN_FILENAME_3);
				}
				else {
					bindFileName = true;

					sb.append(_FINDER_COLUMN_G_F_FN_FILENAME_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(folderId);

					if (bindFileName) {
						queryPos.add(fileName);
					}

					List<DLFileEntry> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							FinderCacheUtil.putResult(
								_finderPathFetchByG_F_FN, finderArgs, list);
						}
					}
					else {
						DLFileEntry dlFileEntry = list.get(0);

						result = dlFileEntry;

						cacheResult(dlFileEntry);
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
				return (DLFileEntry)result;
			}
		}
	}

	/**
	 * Removes the document library file entry where groupId = &#63; and folderId = &#63; and fileName = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileName the file name
	 * @return the document library file entry that was removed
	 */
	@Override
	public DLFileEntry removeByG_F_FN(
			long groupId, long folderId, String fileName)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = findByG_F_FN(groupId, folderId, fileName);

		return remove(dlFileEntry);
	}

	/**
	 * Returns the number of document library file entries where groupId = &#63; and folderId = &#63; and fileName = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileName the file name
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByG_F_FN(long groupId, long folderId, String fileName) {
		DLFileEntry dlFileEntry = fetchByG_F_FN(groupId, folderId, fileName);

		if (dlFileEntry == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_G_F_FN_GROUPID_2 =
		"dlFileEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_F_FN_FOLDERID_2 =
		"dlFileEntry.folderId = ? AND ";

	private static final String _FINDER_COLUMN_G_F_FN_FILENAME_2 =
		"dlFileEntry.fileName = ?";

	private static final String _FINDER_COLUMN_G_F_FN_FILENAME_3 =
		"(dlFileEntry.fileName IS NULL OR dlFileEntry.fileName = '')";

	private FinderPath _finderPathFetchByG_F_T;

	/**
	 * Returns the document library file entry where groupId = &#63; and folderId = &#63; and title = &#63; or throws a <code>NoSuchFileEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param title the title
	 * @return the matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByG_F_T(long groupId, long folderId, String title)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByG_F_T(groupId, folderId, title);

		if (dlFileEntry == null) {
			StringBundler sb = new StringBundler(8);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("groupId=");
			sb.append(groupId);

			sb.append(", folderId=");
			sb.append(folderId);

			sb.append(", title=");
			sb.append(title);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchFileEntryException(sb.toString());
		}

		return dlFileEntry;
	}

	/**
	 * Returns the document library file entry where groupId = &#63; and folderId = &#63; and title = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param title the title
	 * @return the matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByG_F_T(long groupId, long folderId, String title) {
		return fetchByG_F_T(groupId, folderId, title, true);
	}

	/**
	 * Returns the document library file entry where groupId = &#63; and folderId = &#63; and title = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param title the title
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByG_F_T(
		long groupId, long folderId, String title, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			title = Objects.toString(title, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {groupId, folderId, title};
			}

			Object result = null;

			if (useFinderCache) {
				result = FinderCacheUtil.getResult(
					_finderPathFetchByG_F_T, finderArgs, this);
			}

			if (result instanceof DLFileEntry) {
				DLFileEntry dlFileEntry = (DLFileEntry)result;

				if ((groupId != dlFileEntry.getGroupId()) ||
					(folderId != dlFileEntry.getFolderId()) ||
					!Objects.equals(title, dlFileEntry.getTitle())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_F_T_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_F_T_FOLDERID_2);

				boolean bindTitle = false;

				if (title.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_F_T_TITLE_3);
				}
				else {
					bindTitle = true;

					sb.append(_FINDER_COLUMN_G_F_T_TITLE_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(folderId);

					if (bindTitle) {
						queryPos.add(title);
					}

					List<DLFileEntry> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							FinderCacheUtil.putResult(
								_finderPathFetchByG_F_T, finderArgs, list);
						}
					}
					else {
						DLFileEntry dlFileEntry = list.get(0);

						result = dlFileEntry;

						cacheResult(dlFileEntry);
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
				return (DLFileEntry)result;
			}
		}
	}

	/**
	 * Removes the document library file entry where groupId = &#63; and folderId = &#63; and title = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param title the title
	 * @return the document library file entry that was removed
	 */
	@Override
	public DLFileEntry removeByG_F_T(long groupId, long folderId, String title)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = findByG_F_T(groupId, folderId, title);

		return remove(dlFileEntry);
	}

	/**
	 * Returns the number of document library file entries where groupId = &#63; and folderId = &#63; and title = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param title the title
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByG_F_T(long groupId, long folderId, String title) {
		DLFileEntry dlFileEntry = fetchByG_F_T(groupId, folderId, title);

		if (dlFileEntry == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_G_F_T_GROUPID_2 =
		"dlFileEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_F_T_FOLDERID_2 =
		"dlFileEntry.folderId = ? AND ";

	private static final String _FINDER_COLUMN_G_F_T_TITLE_2 =
		"dlFileEntry.title = ?";

	private static final String _FINDER_COLUMN_G_F_T_TITLE_3 =
		"(dlFileEntry.title IS NULL OR dlFileEntry.title = '')";

	private FinderPath _finderPathWithPaginationFindByG_F_F;
	private FinderPath _finderPathWithoutPaginationFindByG_F_F;
	private FinderPath _finderPathCountByG_F_F;
	private FinderPath _finderPathWithPaginationCountByG_F_F;

	/**
	 * Returns all the document library file entries where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @return the matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByG_F_F(
		long groupId, long folderId, long fileEntryTypeId) {

		return findByG_F_F(
			groupId, folderId, fileEntryTypeId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file entries where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByG_F_F(
		long groupId, long folderId, long fileEntryTypeId, int start, int end) {

		return findByG_F_F(
			groupId, folderId, fileEntryTypeId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByG_F_F(
		long groupId, long folderId, long fileEntryTypeId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return findByG_F_F(
			groupId, folderId, fileEntryTypeId, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByG_F_F(
		long groupId, long folderId, long fileEntryTypeId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_F_F;
					finderArgs = new Object[] {
						groupId, folderId, fileEntryTypeId
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_F_F;
				finderArgs = new Object[] {
					groupId, folderId, fileEntryTypeId, start, end,
					orderByComparator
				};
			}

			List<DLFileEntry> list = null;

			if (useFinderCache) {
				list = (List<DLFileEntry>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFileEntry dlFileEntry : list) {
						if ((groupId != dlFileEntry.getGroupId()) ||
							(folderId != dlFileEntry.getFolderId()) ||
							(fileEntryTypeId !=
								dlFileEntry.getFileEntryTypeId())) {

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

				sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_F_F_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_F_F_FOLDERID_2);

				sb.append(_FINDER_COLUMN_G_F_F_FILEENTRYTYPEID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(folderId);

					queryPos.add(fileEntryTypeId);

					list = (List<DLFileEntry>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(finderPath, finderArgs, list);
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
	 * Returns the first document library file entry in the ordered set where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByG_F_F_First(
			long groupId, long folderId, long fileEntryTypeId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByG_F_F_First(
			groupId, folderId, fileEntryTypeId, orderByComparator);

		if (dlFileEntry != null) {
			return dlFileEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", folderId=");
		sb.append(folderId);

		sb.append(", fileEntryTypeId=");
		sb.append(fileEntryTypeId);

		sb.append("}");

		throw new NoSuchFileEntryException(sb.toString());
	}

	/**
	 * Returns the first document library file entry in the ordered set where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByG_F_F_First(
		long groupId, long folderId, long fileEntryTypeId,
		OrderByComparator<DLFileEntry> orderByComparator) {

		List<DLFileEntry> list = findByG_F_F(
			groupId, folderId, fileEntryTypeId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library file entry in the ordered set where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByG_F_F_Last(
			long groupId, long folderId, long fileEntryTypeId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByG_F_F_Last(
			groupId, folderId, fileEntryTypeId, orderByComparator);

		if (dlFileEntry != null) {
			return dlFileEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", folderId=");
		sb.append(folderId);

		sb.append(", fileEntryTypeId=");
		sb.append(fileEntryTypeId);

		sb.append("}");

		throw new NoSuchFileEntryException(sb.toString());
	}

	/**
	 * Returns the last document library file entry in the ordered set where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByG_F_F_Last(
		long groupId, long folderId, long fileEntryTypeId,
		OrderByComparator<DLFileEntry> orderByComparator) {

		int count = countByG_F_F(groupId, folderId, fileEntryTypeId);

		if (count == 0) {
			return null;
		}

		List<DLFileEntry> list = findByG_F_F(
			groupId, folderId, fileEntryTypeId, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	@Override
	public DLFileEntry[] findByG_F_F_PrevAndNext(
			long fileEntryId, long groupId, long folderId, long fileEntryTypeId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = findByPrimaryKey(fileEntryId);

		Session session = null;

		try {
			session = openSession();

			DLFileEntry[] array = new DLFileEntryImpl[3];

			array[0] = getByG_F_F_PrevAndNext(
				session, dlFileEntry, groupId, folderId, fileEntryTypeId,
				orderByComparator, true);

			array[1] = dlFileEntry;

			array[2] = getByG_F_F_PrevAndNext(
				session, dlFileEntry, groupId, folderId, fileEntryTypeId,
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

	protected DLFileEntry getByG_F_F_PrevAndNext(
		Session session, DLFileEntry dlFileEntry, long groupId, long folderId,
		long fileEntryTypeId, OrderByComparator<DLFileEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_F_F_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_F_F_FOLDERID_2);

		sb.append(_FINDER_COLUMN_G_F_F_FILEENTRYTYPEID_2);

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
			sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(folderId);

		queryPos.add(fileEntryTypeId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFileEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFileEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the document library file entries that the user has permission to view where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @return the matching document library file entries that the user has permission to view
	 */
	@Override
	public List<DLFileEntry> filterFindByG_F_F(
		long groupId, long folderId, long fileEntryTypeId) {

		return filterFindByG_F_F(
			groupId, folderId, fileEntryTypeId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file entries that the user has permission to view where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries that the user has permission to view
	 */
	@Override
	public List<DLFileEntry> filterFindByG_F_F(
		long groupId, long folderId, long fileEntryTypeId, int start, int end) {

		return filterFindByG_F_F(
			groupId, folderId, fileEntryTypeId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file entries that the user has permissions to view where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries that the user has permission to view
	 */
	@Override
	public List<DLFileEntry> filterFindByG_F_F(
		long groupId, long folderId, long fileEntryTypeId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_F_F(
				groupId, folderId, fileEntryTypeId, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_F_F(
					groupId, folderId, fileEntryTypeId, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_DLFILEENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DLFILEENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_F_F_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_F_F_FOLDERID_2);

		sb.append(_FINDER_COLUMN_G_F_F_FILEENTRYTYPEID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DLFILEENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DLFileEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DLFileEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFileEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DLFileEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DLFileEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(folderId);

			queryPos.add(fileEntryTypeId);

			return (List<DLFileEntry>)QueryUtil.list(
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
	 * Returns the document library file entries before and after the current document library file entry in the ordered set of document library file entries that the user has permission to view where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	@Override
	public DLFileEntry[] filterFindByG_F_F_PrevAndNext(
			long fileEntryId, long groupId, long folderId, long fileEntryTypeId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_F_F_PrevAndNext(
				fileEntryId, groupId, folderId, fileEntryTypeId,
				orderByComparator);
		}

		DLFileEntry dlFileEntry = findByPrimaryKey(fileEntryId);

		Session session = null;

		try {
			session = openSession();

			DLFileEntry[] array = new DLFileEntryImpl[3];

			array[0] = filterGetByG_F_F_PrevAndNext(
				session, dlFileEntry, groupId, folderId, fileEntryTypeId,
				orderByComparator, true);

			array[1] = dlFileEntry;

			array[2] = filterGetByG_F_F_PrevAndNext(
				session, dlFileEntry, groupId, folderId, fileEntryTypeId,
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

	protected DLFileEntry filterGetByG_F_F_PrevAndNext(
		Session session, DLFileEntry dlFileEntry, long groupId, long folderId,
		long fileEntryTypeId, OrderByComparator<DLFileEntry> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_DLFILEENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DLFILEENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_F_F_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_F_F_FOLDERID_2);

		sb.append(_FINDER_COLUMN_G_F_F_FILEENTRYTYPEID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DLFILEENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DLFileEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DLFileEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFileEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DLFileEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DLFileEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(folderId);

		queryPos.add(fileEntryTypeId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFileEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFileEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the document library file entries that the user has permission to view where groupId = &#63; and folderId = any &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param fileEntryTypeId the file entry type ID
	 * @return the matching document library file entries that the user has permission to view
	 */
	@Override
	public List<DLFileEntry> filterFindByG_F_F(
		long groupId, long[] folderIds, long fileEntryTypeId) {

		return filterFindByG_F_F(
			groupId, folderIds, fileEntryTypeId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file entries that the user has permission to view where groupId = &#63; and folderId = any &#63; and fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries that the user has permission to view
	 */
	@Override
	public List<DLFileEntry> filterFindByG_F_F(
		long groupId, long[] folderIds, long fileEntryTypeId, int start,
		int end) {

		return filterFindByG_F_F(
			groupId, folderIds, fileEntryTypeId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file entries that the user has permission to view where groupId = &#63; and folderId = any &#63; and fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries that the user has permission to view
	 */
	@Override
	public List<DLFileEntry> filterFindByG_F_F(
		long groupId, long[] folderIds, long fileEntryTypeId, int start,
		int end, OrderByComparator<DLFileEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_F_F(
				groupId, folderIds, fileEntryTypeId, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_F_F(
					groupId, folderIds, fileEntryTypeId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		if (folderIds == null) {
			folderIds = new long[0];
		}
		else if (folderIds.length > 1) {
			folderIds = ArrayUtil.sortedUnique(folderIds);
		}

		StringBundler sb = new StringBundler();

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_DLFILEENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DLFILEENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_F_F_GROUPID_2);

		if (folderIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_F_F_FOLDERID_7);

			sb.append(StringUtil.merge(folderIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_G_F_F_FILEENTRYTYPEID_2);

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DLFILEENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DLFileEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DLFileEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFileEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DLFileEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DLFileEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(fileEntryTypeId);

			return (List<DLFileEntry>)QueryUtil.list(
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
	 * Returns all the document library file entries where groupId = &#63; and folderId = any &#63; and fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param fileEntryTypeId the file entry type ID
	 * @return the matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByG_F_F(
		long groupId, long[] folderIds, long fileEntryTypeId) {

		return findByG_F_F(
			groupId, folderIds, fileEntryTypeId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file entries where groupId = &#63; and folderId = any &#63; and fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByG_F_F(
		long groupId, long[] folderIds, long fileEntryTypeId, int start,
		int end) {

		return findByG_F_F(
			groupId, folderIds, fileEntryTypeId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and folderId = any &#63; and fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByG_F_F(
		long groupId, long[] folderIds, long fileEntryTypeId, int start,
		int end, OrderByComparator<DLFileEntry> orderByComparator) {

		return findByG_F_F(
			groupId, folderIds, fileEntryTypeId, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByG_F_F(
		long groupId, long[] folderIds, long fileEntryTypeId, int start,
		int end, OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		if (folderIds == null) {
			folderIds = new long[0];
		}
		else if (folderIds.length > 1) {
			folderIds = ArrayUtil.sortedUnique(folderIds);
		}

		if (folderIds.length == 1) {
			return findByG_F_F(
				groupId, folderIds[0], fileEntryTypeId, start, end,
				orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						groupId, StringUtil.merge(folderIds), fileEntryTypeId
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					groupId, StringUtil.merge(folderIds), fileEntryTypeId,
					start, end, orderByComparator
				};
			}

			List<DLFileEntry> list = null;

			if (useFinderCache) {
				list = (List<DLFileEntry>)FinderCacheUtil.getResult(
					_finderPathWithPaginationFindByG_F_F, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFileEntry dlFileEntry : list) {
						if ((groupId != dlFileEntry.getGroupId()) ||
							!ArrayUtil.contains(
								folderIds, dlFileEntry.getFolderId()) ||
							(fileEntryTypeId !=
								dlFileEntry.getFileEntryTypeId())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_F_F_GROUPID_2);

				if (folderIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_F_F_FOLDERID_7);

					sb.append(StringUtil.merge(folderIds));

					sb.append(")");

					sb.append(")");

					sb.append(WHERE_AND);
				}

				sb.append(_FINDER_COLUMN_G_F_F_FILEENTRYTYPEID_2);

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(fileEntryTypeId);

					list = (List<DLFileEntry>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(
							_finderPathWithPaginationFindByG_F_F, finderArgs,
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
	 * Removes all the document library file entries where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 */
	@Override
	public void removeByG_F_F(
		long groupId, long folderId, long fileEntryTypeId) {

		for (DLFileEntry dlFileEntry :
				findByG_F_F(
					groupId, folderId, fileEntryTypeId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(dlFileEntry);
		}
	}

	/**
	 * Returns the number of document library file entries where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByG_F_F(long groupId, long folderId, long fileEntryTypeId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			FinderPath finderPath = _finderPathCountByG_F_F;

			Object[] finderArgs = new Object[] {
				groupId, folderId, fileEntryTypeId
			};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_F_F_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_F_F_FOLDERID_2);

				sb.append(_FINDER_COLUMN_G_F_F_FILEENTRYTYPEID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(folderId);

					queryPos.add(fileEntryTypeId);

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(finderPath, finderArgs, count);
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
	 * Returns the number of document library file entries where groupId = &#63; and folderId = any &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param fileEntryTypeId the file entry type ID
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByG_F_F(
		long groupId, long[] folderIds, long fileEntryTypeId) {

		if (folderIds == null) {
			folderIds = new long[0];
		}
		else if (folderIds.length > 1) {
			folderIds = ArrayUtil.sortedUnique(folderIds);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			Object[] finderArgs = new Object[] {
				groupId, StringUtil.merge(folderIds), fileEntryTypeId
			};

			Long count = (Long)FinderCacheUtil.getResult(
				_finderPathWithPaginationCountByG_F_F, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_F_F_GROUPID_2);

				if (folderIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_F_F_FOLDERID_7);

					sb.append(StringUtil.merge(folderIds));

					sb.append(")");

					sb.append(")");

					sb.append(WHERE_AND);
				}

				sb.append(_FINDER_COLUMN_G_F_F_FILEENTRYTYPEID_2);

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

					queryPos.add(fileEntryTypeId);

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(
						_finderPathWithPaginationCountByG_F_F, finderArgs,
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
	 * Returns the number of document library file entries that the user has permission to view where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @return the number of matching document library file entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_F_F(
		long groupId, long folderId, long fileEntryTypeId) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_F_F(groupId, folderId, fileEntryTypeId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DLFileEntry> dlFileEntries = findByG_F_F(
				groupId, folderId, fileEntryTypeId);

			dlFileEntries = InlineSQLHelperUtil.filter(dlFileEntries, groupId);

			return dlFileEntries.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_DLFILEENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_F_F_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_F_F_FOLDERID_2);

		sb.append(_FINDER_COLUMN_G_F_F_FILEENTRYTYPEID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFileEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(folderId);

			queryPos.add(fileEntryTypeId);

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
	 * Returns the number of document library file entries that the user has permission to view where groupId = &#63; and folderId = any &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param fileEntryTypeId the file entry type ID
	 * @return the number of matching document library file entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_F_F(
		long groupId, long[] folderIds, long fileEntryTypeId) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_F_F(groupId, folderIds, fileEntryTypeId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DLFileEntry> dlFileEntries = InlineSQLHelperUtil.filter(
				findByG_F_F(groupId, folderIds, fileEntryTypeId), groupId);

			return dlFileEntries.size();
		}

		if (folderIds == null) {
			folderIds = new long[0];
		}
		else if (folderIds.length > 1) {
			folderIds = ArrayUtil.sortedUnique(folderIds);
		}

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_DLFILEENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_F_F_GROUPID_2);

		if (folderIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_F_F_FOLDERID_7);

			sb.append(StringUtil.merge(folderIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_G_F_F_FILEENTRYTYPEID_2);

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFileEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(fileEntryTypeId);

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

	private static final String _FINDER_COLUMN_G_F_F_GROUPID_2 =
		"dlFileEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_F_F_FOLDERID_2 =
		"dlFileEntry.folderId = ? AND ";

	private static final String _FINDER_COLUMN_G_F_F_FOLDERID_7 =
		"dlFileEntry.folderId IN (";

	private static final String _FINDER_COLUMN_G_F_F_FILEENTRYTYPEID_2 =
		"dlFileEntry.fileEntryTypeId = ?";

	private FinderPath _finderPathWithPaginationFindByC_C_C;
	private FinderPath _finderPathWithoutPaginationFindByC_C_C;
	private FinderPath _finderPathCountByC_C_C;

	/**
	 * Returns all the document library file entries where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByC_C_C(
		long companyId, long classNameId, long classPK) {

		return findByC_C_C(
			companyId, classNameId, classPK, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file entries where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByC_C_C(
		long companyId, long classNameId, long classPK, int start, int end) {

		return findByC_C_C(companyId, classNameId, classPK, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file entries where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByC_C_C(
		long companyId, long classNameId, long classPK, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return findByC_C_C(
			companyId, classNameId, classPK, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByC_C_C(
		long companyId, long classNameId, long classPK, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

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
					companyId, classNameId, classPK, start, end,
					orderByComparator
				};
			}

			List<DLFileEntry> list = null;

			if (useFinderCache) {
				list = (List<DLFileEntry>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFileEntry dlFileEntry : list) {
						if ((companyId != dlFileEntry.getCompanyId()) ||
							(classNameId != dlFileEntry.getClassNameId()) ||
							(classPK != dlFileEntry.getClassPK())) {

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

				sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_C_C_C_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_C_C_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_C_C_C_CLASSPK_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
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

					list = (List<DLFileEntry>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(finderPath, finderArgs, list);
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
	 * Returns the first document library file entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByC_C_C_First(
			long companyId, long classNameId, long classPK,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByC_C_C_First(
			companyId, classNameId, classPK, orderByComparator);

		if (dlFileEntry != null) {
			return dlFileEntry;
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

		throw new NoSuchFileEntryException(sb.toString());
	}

	/**
	 * Returns the first document library file entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByC_C_C_First(
		long companyId, long classNameId, long classPK,
		OrderByComparator<DLFileEntry> orderByComparator) {

		List<DLFileEntry> list = findByC_C_C(
			companyId, classNameId, classPK, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library file entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByC_C_C_Last(
			long companyId, long classNameId, long classPK,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByC_C_C_Last(
			companyId, classNameId, classPK, orderByComparator);

		if (dlFileEntry != null) {
			return dlFileEntry;
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

		throw new NoSuchFileEntryException(sb.toString());
	}

	/**
	 * Returns the last document library file entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByC_C_C_Last(
		long companyId, long classNameId, long classPK,
		OrderByComparator<DLFileEntry> orderByComparator) {

		int count = countByC_C_C(companyId, classNameId, classPK);

		if (count == 0) {
			return null;
		}

		List<DLFileEntry> list = findByC_C_C(
			companyId, classNameId, classPK, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	@Override
	public DLFileEntry[] findByC_C_C_PrevAndNext(
			long fileEntryId, long companyId, long classNameId, long classPK,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = findByPrimaryKey(fileEntryId);

		Session session = null;

		try {
			session = openSession();

			DLFileEntry[] array = new DLFileEntryImpl[3];

			array[0] = getByC_C_C_PrevAndNext(
				session, dlFileEntry, companyId, classNameId, classPK,
				orderByComparator, true);

			array[1] = dlFileEntry;

			array[2] = getByC_C_C_PrevAndNext(
				session, dlFileEntry, companyId, classNameId, classPK,
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

	protected DLFileEntry getByC_C_C_PrevAndNext(
		Session session, DLFileEntry dlFileEntry, long companyId,
		long classNameId, long classPK,
		OrderByComparator<DLFileEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

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
			sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(dlFileEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFileEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library file entries where companyId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByC_C_C(long companyId, long classNameId, long classPK) {
		for (DLFileEntry dlFileEntry :
				findByC_C_C(
					companyId, classNameId, classPK, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(dlFileEntry);
		}
	}

	/**
	 * Returns the number of document library file entries where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByC_C_C(long companyId, long classNameId, long classPK) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			FinderPath finderPath = _finderPathCountByC_C_C;

			Object[] finderArgs = new Object[] {
				companyId, classNameId, classPK
			};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_DLFILEENTRY_WHERE);

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

					FinderCacheUtil.putResult(finderPath, finderArgs, count);
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

	private static final String _FINDER_COLUMN_C_C_C_COMPANYID_2 =
		"dlFileEntry.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_C_C_CLASSNAMEID_2 =
		"dlFileEntry.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_C_C_C_CLASSPK_2 =
		"dlFileEntry.classPK = ?";

	private FinderPath _finderPathWithPaginationFindByS_L_C1_C2;
	private FinderPath _finderPathWithoutPaginationFindByS_L_C1_C2;
	private FinderPath _finderPathCountByS_L_C1_C2;

	/**
	 * Returns all the document library file entries where smallImageId = &#63; and largeImageId = &#63; and custom1ImageId = &#63; and custom2ImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @param largeImageId the large image ID
	 * @param custom1ImageId the custom1 image ID
	 * @param custom2ImageId the custom2 image ID
	 * @return the matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByS_L_C1_C2(
		long smallImageId, long largeImageId, long custom1ImageId,
		long custom2ImageId) {

		return findByS_L_C1_C2(
			smallImageId, largeImageId, custom1ImageId, custom2ImageId,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file entries where smallImageId = &#63; and largeImageId = &#63; and custom1ImageId = &#63; and custom2ImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param smallImageId the small image ID
	 * @param largeImageId the large image ID
	 * @param custom1ImageId the custom1 image ID
	 * @param custom2ImageId the custom2 image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByS_L_C1_C2(
		long smallImageId, long largeImageId, long custom1ImageId,
		long custom2ImageId, int start, int end) {

		return findByS_L_C1_C2(
			smallImageId, largeImageId, custom1ImageId, custom2ImageId, start,
			end, null);
	}

	/**
	 * Returns an ordered range of all the document library file entries where smallImageId = &#63; and largeImageId = &#63; and custom1ImageId = &#63; and custom2ImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param smallImageId the small image ID
	 * @param largeImageId the large image ID
	 * @param custom1ImageId the custom1 image ID
	 * @param custom2ImageId the custom2 image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByS_L_C1_C2(
		long smallImageId, long largeImageId, long custom1ImageId,
		long custom2ImageId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return findByS_L_C1_C2(
			smallImageId, largeImageId, custom1ImageId, custom2ImageId, start,
			end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries where smallImageId = &#63; and largeImageId = &#63; and custom1ImageId = &#63; and custom2ImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param smallImageId the small image ID
	 * @param largeImageId the large image ID
	 * @param custom1ImageId the custom1 image ID
	 * @param custom2ImageId the custom2 image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	@Override
	public List<DLFileEntry> findByS_L_C1_C2(
		long smallImageId, long largeImageId, long custom1ImageId,
		long custom2ImageId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByS_L_C1_C2;
					finderArgs = new Object[] {
						smallImageId, largeImageId, custom1ImageId,
						custom2ImageId
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByS_L_C1_C2;
				finderArgs = new Object[] {
					smallImageId, largeImageId, custom1ImageId, custom2ImageId,
					start, end, orderByComparator
				};
			}

			List<DLFileEntry> list = null;

			if (useFinderCache) {
				list = (List<DLFileEntry>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFileEntry dlFileEntry : list) {
						if ((smallImageId != dlFileEntry.getSmallImageId()) ||
							(largeImageId != dlFileEntry.getLargeImageId()) ||
							(custom1ImageId !=
								dlFileEntry.getCustom1ImageId()) ||
							(custom2ImageId !=
								dlFileEntry.getCustom2ImageId())) {

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

				sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_S_L_C1_C2_SMALLIMAGEID_2);

				sb.append(_FINDER_COLUMN_S_L_C1_C2_LARGEIMAGEID_2);

				sb.append(_FINDER_COLUMN_S_L_C1_C2_CUSTOM1IMAGEID_2);

				sb.append(_FINDER_COLUMN_S_L_C1_C2_CUSTOM2IMAGEID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(smallImageId);

					queryPos.add(largeImageId);

					queryPos.add(custom1ImageId);

					queryPos.add(custom2ImageId);

					list = (List<DLFileEntry>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(finderPath, finderArgs, list);
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
	 * Returns the first document library file entry in the ordered set where smallImageId = &#63; and largeImageId = &#63; and custom1ImageId = &#63; and custom2ImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @param largeImageId the large image ID
	 * @param custom1ImageId the custom1 image ID
	 * @param custom2ImageId the custom2 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByS_L_C1_C2_First(
			long smallImageId, long largeImageId, long custom1ImageId,
			long custom2ImageId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByS_L_C1_C2_First(
			smallImageId, largeImageId, custom1ImageId, custom2ImageId,
			orderByComparator);

		if (dlFileEntry != null) {
			return dlFileEntry;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("smallImageId=");
		sb.append(smallImageId);

		sb.append(", largeImageId=");
		sb.append(largeImageId);

		sb.append(", custom1ImageId=");
		sb.append(custom1ImageId);

		sb.append(", custom2ImageId=");
		sb.append(custom2ImageId);

		sb.append("}");

		throw new NoSuchFileEntryException(sb.toString());
	}

	/**
	 * Returns the first document library file entry in the ordered set where smallImageId = &#63; and largeImageId = &#63; and custom1ImageId = &#63; and custom2ImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @param largeImageId the large image ID
	 * @param custom1ImageId the custom1 image ID
	 * @param custom2ImageId the custom2 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByS_L_C1_C2_First(
		long smallImageId, long largeImageId, long custom1ImageId,
		long custom2ImageId, OrderByComparator<DLFileEntry> orderByComparator) {

		List<DLFileEntry> list = findByS_L_C1_C2(
			smallImageId, largeImageId, custom1ImageId, custom2ImageId, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library file entry in the ordered set where smallImageId = &#63; and largeImageId = &#63; and custom1ImageId = &#63; and custom2ImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @param largeImageId the large image ID
	 * @param custom1ImageId the custom1 image ID
	 * @param custom2ImageId the custom2 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByS_L_C1_C2_Last(
			long smallImageId, long largeImageId, long custom1ImageId,
			long custom2ImageId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByS_L_C1_C2_Last(
			smallImageId, largeImageId, custom1ImageId, custom2ImageId,
			orderByComparator);

		if (dlFileEntry != null) {
			return dlFileEntry;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("smallImageId=");
		sb.append(smallImageId);

		sb.append(", largeImageId=");
		sb.append(largeImageId);

		sb.append(", custom1ImageId=");
		sb.append(custom1ImageId);

		sb.append(", custom2ImageId=");
		sb.append(custom2ImageId);

		sb.append("}");

		throw new NoSuchFileEntryException(sb.toString());
	}

	/**
	 * Returns the last document library file entry in the ordered set where smallImageId = &#63; and largeImageId = &#63; and custom1ImageId = &#63; and custom2ImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @param largeImageId the large image ID
	 * @param custom1ImageId the custom1 image ID
	 * @param custom2ImageId the custom2 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByS_L_C1_C2_Last(
		long smallImageId, long largeImageId, long custom1ImageId,
		long custom2ImageId, OrderByComparator<DLFileEntry> orderByComparator) {

		int count = countByS_L_C1_C2(
			smallImageId, largeImageId, custom1ImageId, custom2ImageId);

		if (count == 0) {
			return null;
		}

		List<DLFileEntry> list = findByS_L_C1_C2(
			smallImageId, largeImageId, custom1ImageId, custom2ImageId,
			count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set where smallImageId = &#63; and largeImageId = &#63; and custom1ImageId = &#63; and custom2ImageId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param smallImageId the small image ID
	 * @param largeImageId the large image ID
	 * @param custom1ImageId the custom1 image ID
	 * @param custom2ImageId the custom2 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	@Override
	public DLFileEntry[] findByS_L_C1_C2_PrevAndNext(
			long fileEntryId, long smallImageId, long largeImageId,
			long custom1ImageId, long custom2ImageId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = findByPrimaryKey(fileEntryId);

		Session session = null;

		try {
			session = openSession();

			DLFileEntry[] array = new DLFileEntryImpl[3];

			array[0] = getByS_L_C1_C2_PrevAndNext(
				session, dlFileEntry, smallImageId, largeImageId,
				custom1ImageId, custom2ImageId, orderByComparator, true);

			array[1] = dlFileEntry;

			array[2] = getByS_L_C1_C2_PrevAndNext(
				session, dlFileEntry, smallImageId, largeImageId,
				custom1ImageId, custom2ImageId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DLFileEntry getByS_L_C1_C2_PrevAndNext(
		Session session, DLFileEntry dlFileEntry, long smallImageId,
		long largeImageId, long custom1ImageId, long custom2ImageId,
		OrderByComparator<DLFileEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

		sb.append(_FINDER_COLUMN_S_L_C1_C2_SMALLIMAGEID_2);

		sb.append(_FINDER_COLUMN_S_L_C1_C2_LARGEIMAGEID_2);

		sb.append(_FINDER_COLUMN_S_L_C1_C2_CUSTOM1IMAGEID_2);

		sb.append(_FINDER_COLUMN_S_L_C1_C2_CUSTOM2IMAGEID_2);

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
			sb.append(DLFileEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(smallImageId);

		queryPos.add(largeImageId);

		queryPos.add(custom1ImageId);

		queryPos.add(custom2ImageId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlFileEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFileEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library file entries where smallImageId = &#63; and largeImageId = &#63; and custom1ImageId = &#63; and custom2ImageId = &#63; from the database.
	 *
	 * @param smallImageId the small image ID
	 * @param largeImageId the large image ID
	 * @param custom1ImageId the custom1 image ID
	 * @param custom2ImageId the custom2 image ID
	 */
	@Override
	public void removeByS_L_C1_C2(
		long smallImageId, long largeImageId, long custom1ImageId,
		long custom2ImageId) {

		for (DLFileEntry dlFileEntry :
				findByS_L_C1_C2(
					smallImageId, largeImageId, custom1ImageId, custom2ImageId,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(dlFileEntry);
		}
	}

	/**
	 * Returns the number of document library file entries where smallImageId = &#63; and largeImageId = &#63; and custom1ImageId = &#63; and custom2ImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @param largeImageId the large image ID
	 * @param custom1ImageId the custom1 image ID
	 * @param custom2ImageId the custom2 image ID
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByS_L_C1_C2(
		long smallImageId, long largeImageId, long custom1ImageId,
		long custom2ImageId) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			FinderPath finderPath = _finderPathCountByS_L_C1_C2;

			Object[] finderArgs = new Object[] {
				smallImageId, largeImageId, custom1ImageId, custom2ImageId
			};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_DLFILEENTRY_WHERE);

				sb.append(_FINDER_COLUMN_S_L_C1_C2_SMALLIMAGEID_2);

				sb.append(_FINDER_COLUMN_S_L_C1_C2_LARGEIMAGEID_2);

				sb.append(_FINDER_COLUMN_S_L_C1_C2_CUSTOM1IMAGEID_2);

				sb.append(_FINDER_COLUMN_S_L_C1_C2_CUSTOM2IMAGEID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(smallImageId);

					queryPos.add(largeImageId);

					queryPos.add(custom1ImageId);

					queryPos.add(custom2ImageId);

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(finderPath, finderArgs, count);
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

	private static final String _FINDER_COLUMN_S_L_C1_C2_SMALLIMAGEID_2 =
		"dlFileEntry.smallImageId = ? AND ";

	private static final String _FINDER_COLUMN_S_L_C1_C2_LARGEIMAGEID_2 =
		"dlFileEntry.largeImageId = ? AND ";

	private static final String _FINDER_COLUMN_S_L_C1_C2_CUSTOM1IMAGEID_2 =
		"dlFileEntry.custom1ImageId = ? AND ";

	private static final String _FINDER_COLUMN_S_L_C1_C2_CUSTOM2IMAGEID_2 =
		"dlFileEntry.custom2ImageId = ?";

	private FinderPath _finderPathFetchByERC_G;

	/**
	 * Returns the document library file entry where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchFileEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry findByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByERC_G(externalReferenceCode, groupId);

		if (dlFileEntry == null) {
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

			throw new NoSuchFileEntryException(sb.toString());
		}

		return dlFileEntry;
	}

	/**
	 * Returns the document library file entry where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return fetchByERC_G(externalReferenceCode, groupId, true);
	}

	/**
	 * Returns the document library file entry where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			externalReferenceCode = Objects.toString(externalReferenceCode, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {externalReferenceCode, groupId};
			}

			Object result = null;

			if (useFinderCache) {
				result = FinderCacheUtil.getResult(
					_finderPathFetchByERC_G, finderArgs, this);
			}

			if (result instanceof DLFileEntry) {
				DLFileEntry dlFileEntry = (DLFileEntry)result;

				if (!Objects.equals(
						externalReferenceCode,
						dlFileEntry.getExternalReferenceCode()) ||
					(groupId != dlFileEntry.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_DLFILEENTRY_WHERE);

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

					List<DLFileEntry> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							FinderCacheUtil.putResult(
								_finderPathFetchByERC_G, finderArgs, list);
						}
					}
					else {
						DLFileEntry dlFileEntry = list.get(0);

						result = dlFileEntry;

						cacheResult(dlFileEntry);
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
				return (DLFileEntry)result;
			}
		}
	}

	/**
	 * Removes the document library file entry where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the document library file entry that was removed
	 */
	@Override
	public DLFileEntry removeByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = findByERC_G(externalReferenceCode, groupId);

		return remove(dlFileEntry);
	}

	/**
	 * Returns the number of document library file entries where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching document library file entries
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		DLFileEntry dlFileEntry = fetchByERC_G(externalReferenceCode, groupId);

		if (dlFileEntry == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_2 =
		"dlFileEntry.externalReferenceCode = ? AND ";

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_3 =
		"(dlFileEntry.externalReferenceCode IS NULL OR dlFileEntry.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_G_GROUPID_2 =
		"dlFileEntry.groupId = ?";

	public DLFileEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("size", "size_");

		setDBColumnNames(dbColumnNames);

		setModelClass(DLFileEntry.class);

		setModelImplClass(DLFileEntryImpl.class);
		setModelPKClass(long.class);

		setTable(DLFileEntryTable.INSTANCE);
	}

	/**
	 * Caches the document library file entry in the entity cache if it is enabled.
	 *
	 * @param dlFileEntry the document library file entry
	 */
	@Override
	public void cacheResult(DLFileEntry dlFileEntry) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					dlFileEntry.getCtCollectionId())) {

			EntityCacheUtil.putResult(
				DLFileEntryImpl.class, dlFileEntry.getPrimaryKey(),
				dlFileEntry);

			FinderCacheUtil.putResult(
				_finderPathFetchByUUID_G,
				new Object[] {dlFileEntry.getUuid(), dlFileEntry.getGroupId()},
				dlFileEntry);

			FinderCacheUtil.putResult(
				_finderPathFetchByG_F_N,
				new Object[] {
					dlFileEntry.getGroupId(), dlFileEntry.getFolderId(),
					dlFileEntry.getName()
				},
				dlFileEntry);

			FinderCacheUtil.putResult(
				_finderPathFetchByG_F_FN,
				new Object[] {
					dlFileEntry.getGroupId(), dlFileEntry.getFolderId(),
					dlFileEntry.getFileName()
				},
				dlFileEntry);

			FinderCacheUtil.putResult(
				_finderPathFetchByG_F_T,
				new Object[] {
					dlFileEntry.getGroupId(), dlFileEntry.getFolderId(),
					dlFileEntry.getTitle()
				},
				dlFileEntry);

			FinderCacheUtil.putResult(
				_finderPathFetchByERC_G,
				new Object[] {
					dlFileEntry.getExternalReferenceCode(),
					dlFileEntry.getGroupId()
				},
				dlFileEntry);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the document library file entries in the entity cache if it is enabled.
	 *
	 * @param dlFileEntries the document library file entries
	 */
	@Override
	public void cacheResult(List<DLFileEntry> dlFileEntries) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (dlFileEntries.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (DLFileEntry dlFileEntry : dlFileEntries) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						dlFileEntry.getCtCollectionId())) {

				if (EntityCacheUtil.getResult(
						DLFileEntryImpl.class, dlFileEntry.getPrimaryKey()) ==
							null) {

					cacheResult(dlFileEntry);
				}
			}
		}
	}

	/**
	 * Clears the cache for all document library file entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		EntityCacheUtil.clearCache(DLFileEntryImpl.class);

		FinderCacheUtil.clearCache(DLFileEntryImpl.class);
	}

	/**
	 * Clears the cache for the document library file entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(DLFileEntry dlFileEntry) {
		EntityCacheUtil.removeResult(DLFileEntryImpl.class, dlFileEntry);
	}

	@Override
	public void clearCache(List<DLFileEntry> dlFileEntries) {
		for (DLFileEntry dlFileEntry : dlFileEntries) {
			EntityCacheUtil.removeResult(DLFileEntryImpl.class, dlFileEntry);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		FinderCacheUtil.clearCache(DLFileEntryImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			EntityCacheUtil.removeResult(DLFileEntryImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		DLFileEntryModelImpl dlFileEntryModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					dlFileEntryModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				dlFileEntryModelImpl.getUuid(),
				dlFileEntryModelImpl.getGroupId()
			};

			FinderCacheUtil.putResult(
				_finderPathFetchByUUID_G, args, dlFileEntryModelImpl);

			args = new Object[] {
				dlFileEntryModelImpl.getGroupId(),
				dlFileEntryModelImpl.getFolderId(),
				dlFileEntryModelImpl.getName()
			};

			FinderCacheUtil.putResult(
				_finderPathFetchByG_F_N, args, dlFileEntryModelImpl);

			args = new Object[] {
				dlFileEntryModelImpl.getGroupId(),
				dlFileEntryModelImpl.getFolderId(),
				dlFileEntryModelImpl.getFileName()
			};

			FinderCacheUtil.putResult(
				_finderPathFetchByG_F_FN, args, dlFileEntryModelImpl);

			args = new Object[] {
				dlFileEntryModelImpl.getGroupId(),
				dlFileEntryModelImpl.getFolderId(),
				dlFileEntryModelImpl.getTitle()
			};

			FinderCacheUtil.putResult(
				_finderPathFetchByG_F_T, args, dlFileEntryModelImpl);

			args = new Object[] {
				dlFileEntryModelImpl.getExternalReferenceCode(),
				dlFileEntryModelImpl.getGroupId()
			};

			FinderCacheUtil.putResult(
				_finderPathFetchByERC_G, args, dlFileEntryModelImpl);
		}
	}

	/**
	 * Creates a new document library file entry with the primary key. Does not add the document library file entry to the database.
	 *
	 * @param fileEntryId the primary key for the new document library file entry
	 * @return the new document library file entry
	 */
	@Override
	public DLFileEntry create(long fileEntryId) {
		DLFileEntry dlFileEntry = new DLFileEntryImpl();

		dlFileEntry.setNew(true);
		dlFileEntry.setPrimaryKey(fileEntryId);

		String uuid = PortalUUIDUtil.generate();

		dlFileEntry.setUuid(uuid);

		dlFileEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return dlFileEntry;
	}

	/**
	 * Removes the document library file entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param fileEntryId the primary key of the document library file entry
	 * @return the document library file entry that was removed
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	@Override
	public DLFileEntry remove(long fileEntryId)
		throws NoSuchFileEntryException {

		return remove((Serializable)fileEntryId);
	}

	/**
	 * Removes the document library file entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the document library file entry
	 * @return the document library file entry that was removed
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	@Override
	public DLFileEntry remove(Serializable primaryKey)
		throws NoSuchFileEntryException {

		Session session = null;

		try {
			session = openSession();

			DLFileEntry dlFileEntry = (DLFileEntry)session.get(
				DLFileEntryImpl.class, primaryKey);

			if (dlFileEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchFileEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(dlFileEntry);
		}
		catch (NoSuchFileEntryException noSuchEntityException) {
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
	protected DLFileEntry removeImpl(DLFileEntry dlFileEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(dlFileEntry)) {
				dlFileEntry = (DLFileEntry)session.get(
					DLFileEntryImpl.class, dlFileEntry.getPrimaryKeyObj());
			}

			if ((dlFileEntry != null) &&
				CTPersistenceHelperUtil.isRemove(dlFileEntry)) {

				session.delete(dlFileEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (dlFileEntry != null) {
			clearCache(dlFileEntry);
		}

		return dlFileEntry;
	}

	@Override
	public DLFileEntry updateImpl(DLFileEntry dlFileEntry) {
		boolean isNew = dlFileEntry.isNew();

		if (!(dlFileEntry instanceof DLFileEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(dlFileEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(dlFileEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in dlFileEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DLFileEntry implementation " +
					dlFileEntry.getClass());
		}

		DLFileEntryModelImpl dlFileEntryModelImpl =
			(DLFileEntryModelImpl)dlFileEntry;

		if (Validator.isNull(dlFileEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			dlFileEntry.setUuid(uuid);
		}

		if (Validator.isNull(dlFileEntry.getExternalReferenceCode())) {
			dlFileEntry.setExternalReferenceCode(dlFileEntry.getUuid());
		}
		else {
			if (!Objects.equals(
					dlFileEntryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					dlFileEntry.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = dlFileEntry.getCompanyId();

					long groupId = dlFileEntry.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = dlFileEntry.getPrimaryKey();
					}

					try {
						dlFileEntry.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								DLFileEntry.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								dlFileEntry.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			DLFileEntry ercDLFileEntry = fetchByERC_G(
				dlFileEntry.getExternalReferenceCode(),
				dlFileEntry.getGroupId());

			if (isNew) {
				if (ercDLFileEntry != null) {
					throw new DuplicateDLFileEntryExternalReferenceCodeException(
						"Duplicate document library file entry with external reference code " +
							dlFileEntry.getExternalReferenceCode() +
								" and group " + dlFileEntry.getGroupId());
				}
			}
			else {
				if ((ercDLFileEntry != null) &&
					(dlFileEntry.getFileEntryId() !=
						ercDLFileEntry.getFileEntryId())) {

					throw new DuplicateDLFileEntryExternalReferenceCodeException(
						"Duplicate document library file entry with external reference code " +
							dlFileEntry.getExternalReferenceCode() +
								" and group " + dlFileEntry.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (dlFileEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				dlFileEntry.setCreateDate(date);
			}
			else {
				dlFileEntry.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!dlFileEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				dlFileEntry.setModifiedDate(date);
			}
			else {
				dlFileEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(dlFileEntry)) {
				if (!isNew) {
					session.evict(
						DLFileEntryImpl.class, dlFileEntry.getPrimaryKeyObj());
				}

				session.save(dlFileEntry);
			}
			else {
				dlFileEntry = (DLFileEntry)session.merge(dlFileEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		EntityCacheUtil.putResult(
			DLFileEntryImpl.class, dlFileEntryModelImpl, false, true);

		cacheUniqueFindersCache(dlFileEntryModelImpl);

		if (isNew) {
			dlFileEntry.setNew(false);
		}

		dlFileEntry.resetOriginalValues();

		return dlFileEntry;
	}

	/**
	 * Returns the document library file entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the document library file entry
	 * @return the document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	@Override
	public DLFileEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchFileEntryException {

		DLFileEntry dlFileEntry = fetchByPrimaryKey(primaryKey);

		if (dlFileEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchFileEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return dlFileEntry;
	}

	/**
	 * Returns the document library file entry with the primary key or throws a <code>NoSuchFileEntryException</code> if it could not be found.
	 *
	 * @param fileEntryId the primary key of the document library file entry
	 * @return the document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	@Override
	public DLFileEntry findByPrimaryKey(long fileEntryId)
		throws NoSuchFileEntryException {

		return findByPrimaryKey((Serializable)fileEntryId);
	}

	/**
	 * Returns the document library file entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the document library file entry
	 * @return the document library file entry, or <code>null</code> if a document library file entry with the primary key could not be found
	 */
	@Override
	public DLFileEntry fetchByPrimaryKey(Serializable primaryKey) {
		if (CTPersistenceHelperUtil.isProductionMode(
				DLFileEntry.class, primaryKey)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		DLFileEntry dlFileEntry = (DLFileEntry)EntityCacheUtil.getResult(
			DLFileEntryImpl.class, primaryKey);

		if (dlFileEntry != null) {
			return dlFileEntry;
		}

		Session session = null;

		try {
			session = openSession();

			dlFileEntry = (DLFileEntry)session.get(
				DLFileEntryImpl.class, primaryKey);

			if (dlFileEntry != null) {
				cacheResult(dlFileEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return dlFileEntry;
	}

	/**
	 * Returns the document library file entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param fileEntryId the primary key of the document library file entry
	 * @return the document library file entry, or <code>null</code> if a document library file entry with the primary key could not be found
	 */
	@Override
	public DLFileEntry fetchByPrimaryKey(long fileEntryId) {
		return fetchByPrimaryKey((Serializable)fileEntryId);
	}

	@Override
	public Map<Serializable, DLFileEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (CTPersistenceHelperUtil.isProductionMode(DLFileEntry.class)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, DLFileEntry> map =
			new HashMap<Serializable, DLFileEntry>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			DLFileEntry dlFileEntry = fetchByPrimaryKey(primaryKey);

			if (dlFileEntry != null) {
				map.put(primaryKey, dlFileEntry);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
						DLFileEntry.class, primaryKey)) {

				DLFileEntry dlFileEntry =
					(DLFileEntry)EntityCacheUtil.getResult(
						DLFileEntryImpl.class, primaryKey);

				if (dlFileEntry == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, dlFileEntry);
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

			for (DLFileEntry dlFileEntry : (List<DLFileEntry>)query.list()) {
				map.put(dlFileEntry.getPrimaryKeyObj(), dlFileEntry);

				cacheResult(dlFileEntry);
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
	 * Returns all the document library file entries.
	 *
	 * @return the document library file entries
	 */
	@Override
	public List<DLFileEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of document library file entries
	 */
	@Override
	public List<DLFileEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of document library file entries
	 */
	@Override
	public List<DLFileEntry> findAll(
		int start, int end, OrderByComparator<DLFileEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library file entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of document library file entries
	 */
	@Override
	public List<DLFileEntry> findAll(
		int start, int end, OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

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

			List<DLFileEntry> list = null;

			if (useFinderCache) {
				list = (List<DLFileEntry>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_DLFILEENTRY);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_DLFILEENTRY;

					sql = sql.concat(DLFileEntryModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<DLFileEntry>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(finderPath, finderArgs, list);
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
	 * Removes all the document library file entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (DLFileEntry dlFileEntry : findAll()) {
			remove(dlFileEntry);
		}
	}

	/**
	 * Returns the number of document library file entries.
	 *
	 * @return the number of document library file entries
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileEntry.class)) {

			Long count = (Long)FinderCacheUtil.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(_SQL_COUNT_DLFILEENTRY);

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(
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
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "fileEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DLFILEENTRY;
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
		return DLFileEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DLFileEntry";
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
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("repositoryId");
		ctMergeColumnNames.add("folderId");
		ctMergeColumnNames.add("treePath");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("fileName");
		ctMergeColumnNames.add("extension");
		ctMergeColumnNames.add("mimeType");
		ctMergeColumnNames.add("title");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("extraSettings");
		ctMergeColumnNames.add("fileEntryTypeId");
		ctMergeColumnNames.add("version");
		ctMergeColumnNames.add("size_");
		ctMergeColumnNames.add("smallImageId");
		ctMergeColumnNames.add("largeImageId");
		ctMergeColumnNames.add("custom1ImageId");
		ctMergeColumnNames.add("custom2ImageId");
		ctMergeColumnNames.add("manualCheckInRequired");
		ctMergeColumnNames.add("displayDate");
		ctMergeColumnNames.add("expirationDate");
		ctMergeColumnNames.add("reviewDate");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("fileEntryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "folderId", "name"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "folderId", "fileName"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "folderId", "title"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the document library file entry persistence.
	 */
	public void afterPropertiesSet() {
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

		_finderPathWithPaginationFindByRepositoryId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByRepositoryId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"repositoryId"}, true);

		_finderPathWithoutPaginationFindByRepositoryId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByRepositoryId",
			new String[] {Long.class.getName()}, new String[] {"repositoryId"},
			true);

		_finderPathCountByRepositoryId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByRepositoryId",
			new String[] {Long.class.getName()}, new String[] {"repositoryId"},
			false);

		_finderPathWithPaginationFindByMimeType = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByMimeType",
			new String[] {
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"mimeType"}, true);

		_finderPathWithoutPaginationFindByMimeType = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByMimeType",
			new String[] {String.class.getName()}, new String[] {"mimeType"},
			true);

		_finderPathCountByMimeType = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByMimeType",
			new String[] {String.class.getName()}, new String[] {"mimeType"},
			false);

		_finderPathWithPaginationFindByFileEntryTypeId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByFileEntryTypeId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"fileEntryTypeId"}, true);

		_finderPathWithoutPaginationFindByFileEntryTypeId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByFileEntryTypeId",
			new String[] {Long.class.getName()},
			new String[] {"fileEntryTypeId"}, true);

		_finderPathCountByFileEntryTypeId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByFileEntryTypeId",
			new String[] {Long.class.getName()},
			new String[] {"fileEntryTypeId"}, false);

		_finderPathWithPaginationFindBySmallImageId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findBySmallImageId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"smallImageId"}, true);

		_finderPathWithoutPaginationFindBySmallImageId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findBySmallImageId",
			new String[] {Long.class.getName()}, new String[] {"smallImageId"},
			true);

		_finderPathCountBySmallImageId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countBySmallImageId",
			new String[] {Long.class.getName()}, new String[] {"smallImageId"},
			false);

		_finderPathWithPaginationFindByLargeImageId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByLargeImageId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"largeImageId"}, true);

		_finderPathWithoutPaginationFindByLargeImageId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByLargeImageId",
			new String[] {Long.class.getName()}, new String[] {"largeImageId"},
			true);

		_finderPathCountByLargeImageId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByLargeImageId",
			new String[] {Long.class.getName()}, new String[] {"largeImageId"},
			false);

		_finderPathWithPaginationFindByCustom1ImageId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCustom1ImageId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"custom1ImageId"}, true);

		_finderPathWithoutPaginationFindByCustom1ImageId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCustom1ImageId",
			new String[] {Long.class.getName()},
			new String[] {"custom1ImageId"}, true);

		_finderPathCountByCustom1ImageId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCustom1ImageId",
			new String[] {Long.class.getName()},
			new String[] {"custom1ImageId"}, false);

		_finderPathWithPaginationFindByCustom2ImageId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCustom2ImageId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"custom2ImageId"}, true);

		_finderPathWithoutPaginationFindByCustom2ImageId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCustom2ImageId",
			new String[] {Long.class.getName()},
			new String[] {"custom2ImageId"}, true);

		_finderPathCountByCustom2ImageId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCustom2ImageId",
			new String[] {Long.class.getName()},
			new String[] {"custom2ImageId"}, false);

		_finderPathWithPaginationFindByG_U = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_U",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "userId"}, true);

		_finderPathWithoutPaginationFindByG_U = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_U",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "userId"}, true);

		_finderPathCountByG_U = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_U",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "userId"}, false);

		_finderPathWithPaginationFindByG_F = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_F",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "folderId"}, true);

		_finderPathWithoutPaginationFindByG_F = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_F",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "folderId"}, true);

		_finderPathCountByG_F = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_F",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "folderId"}, false);

		_finderPathWithPaginationCountByG_F = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_F",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "folderId"}, false);

		_finderPathWithPaginationFindByR_F = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByR_F",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"repositoryId", "folderId"}, true);

		_finderPathWithoutPaginationFindByR_F = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByR_F",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"repositoryId", "folderId"}, true);

		_finderPathCountByR_F = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByR_F",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"repositoryId", "folderId"}, false);

		_finderPathWithPaginationFindByF_N = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByF_N",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"folderId", "name"}, true);

		_finderPathWithoutPaginationFindByF_N = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByF_N",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"folderId", "name"}, true);

		_finderPathCountByF_N = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByF_N",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"folderId", "name"}, false);

		_finderPathWithPaginationFindByG_U_F = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_U_F",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "userId", "folderId"}, true);

		_finderPathWithoutPaginationFindByG_U_F = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_U_F",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"groupId", "userId", "folderId"}, true);

		_finderPathCountByG_U_F = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_U_F",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"groupId", "userId", "folderId"}, false);

		_finderPathWithPaginationCountByG_U_F = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_U_F",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"groupId", "userId", "folderId"}, false);

		_finderPathFetchByG_F_N = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByG_F_N",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName()
			},
			new String[] {"groupId", "folderId", "name"}, true);

		_finderPathFetchByG_F_FN = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByG_F_FN",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName()
			},
			new String[] {"groupId", "folderId", "fileName"}, true);

		_finderPathFetchByG_F_T = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByG_F_T",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName()
			},
			new String[] {"groupId", "folderId", "title"}, true);

		_finderPathWithPaginationFindByG_F_F = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_F_F",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "folderId", "fileEntryTypeId"}, true);

		_finderPathWithoutPaginationFindByG_F_F = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_F_F",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"groupId", "folderId", "fileEntryTypeId"}, true);

		_finderPathCountByG_F_F = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_F_F",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"groupId", "folderId", "fileEntryTypeId"}, false);

		_finderPathWithPaginationCountByG_F_F = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_F_F",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"groupId", "folderId", "fileEntryTypeId"}, false);

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

		_finderPathWithPaginationFindByS_L_C1_C2 = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByS_L_C1_C2",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {
				"smallImageId", "largeImageId", "custom1ImageId",
				"custom2ImageId"
			},
			true);

		_finderPathWithoutPaginationFindByS_L_C1_C2 = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByS_L_C1_C2",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Long.class.getName()
			},
			new String[] {
				"smallImageId", "largeImageId", "custom1ImageId",
				"custom2ImageId"
			},
			true);

		_finderPathCountByS_L_C1_C2 = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByS_L_C1_C2",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Long.class.getName()
			},
			new String[] {
				"smallImageId", "largeImageId", "custom1ImageId",
				"custom2ImageId"
			},
			false);

		_finderPathFetchByERC_G = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"externalReferenceCode", "groupId"}, true);

		DLFileEntryUtil.setPersistence(this);
	}

	public void destroy() {
		DLFileEntryUtil.setPersistence(null);

		EntityCacheUtil.removeCache(DLFileEntryImpl.class.getName());
	}

	private static final String _SQL_SELECT_DLFILEENTRY =
		"SELECT dlFileEntry FROM DLFileEntry dlFileEntry";

	private static final String _SQL_SELECT_DLFILEENTRY_WHERE =
		"SELECT dlFileEntry FROM DLFileEntry dlFileEntry WHERE ";

	private static final String _SQL_COUNT_DLFILEENTRY =
		"SELECT COUNT(dlFileEntry) FROM DLFileEntry dlFileEntry";

	private static final String _SQL_COUNT_DLFILEENTRY_WHERE =
		"SELECT COUNT(dlFileEntry) FROM DLFileEntry dlFileEntry WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"dlFileEntry.fileEntryId";

	private static final String _FILTER_SQL_SELECT_DLFILEENTRY_WHERE =
		"SELECT DISTINCT {dlFileEntry.*} FROM DLFileEntry dlFileEntry WHERE ";

	private static final String
		_FILTER_SQL_SELECT_DLFILEENTRY_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {DLFileEntry.*} FROM (SELECT DISTINCT dlFileEntry.fileEntryId FROM DLFileEntry dlFileEntry WHERE ";

	private static final String
		_FILTER_SQL_SELECT_DLFILEENTRY_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN DLFileEntry ON TEMP_TABLE.fileEntryId = DLFileEntry.fileEntryId";

	private static final String _FILTER_SQL_COUNT_DLFILEENTRY_WHERE =
		"SELECT COUNT(DISTINCT dlFileEntry.fileEntryId) AS COUNT_VALUE FROM DLFileEntry dlFileEntry WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "dlFileEntry";

	private static final String _FILTER_ENTITY_TABLE = "DLFileEntry";

	private static final String _ORDER_BY_ENTITY_ALIAS = "dlFileEntry.";

	private static final String _ORDER_BY_ENTITY_TABLE = "DLFileEntry.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No DLFileEntry exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No DLFileEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		DLFileEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "size"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}