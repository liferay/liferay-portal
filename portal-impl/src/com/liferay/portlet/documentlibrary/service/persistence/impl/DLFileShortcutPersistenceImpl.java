/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.documentlibrary.service.persistence.impl;

import com.liferay.document.library.kernel.exception.DuplicateDLFileShortcutExternalReferenceCodeException;
import com.liferay.document.library.kernel.exception.NoSuchFileShortcutException;
import com.liferay.document.library.kernel.model.DLFileShortcut;
import com.liferay.document.library.kernel.model.DLFileShortcutTable;
import com.liferay.document.library.kernel.service.persistence.DLFileShortcutPersistence;
import com.liferay.document.library.kernel.service.persistence.DLFileShortcutUtil;
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
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portlet.documentlibrary.model.impl.DLFileShortcutImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFileShortcutModelImpl;

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
 * The persistence implementation for the document library file shortcut service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class DLFileShortcutPersistenceImpl
	extends BasePersistenceImpl<DLFileShortcut>
	implements DLFileShortcutPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DLFileShortcutUtil</code> to access the document library file shortcut persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DLFileShortcutImpl.class.getName();

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
	 * Returns all the document library file shortcuts where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file shortcuts where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @return the range of matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file shortcuts where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DLFileShortcut> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library file shortcuts where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DLFileShortcut> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileShortcut.class)) {

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

			List<DLFileShortcut> list = null;

			if (useFinderCache) {
				list = (List<DLFileShortcut>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFileShortcut dlFileShortcut : list) {
						if (!uuid.equals(dlFileShortcut.getUuid())) {
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

				sb.append(_SQL_SELECT_DLFILESHORTCUT_WHERE);

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
					sb.append(DLFileShortcutModelImpl.ORDER_BY_JPQL);
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

					list = (List<DLFileShortcut>)QueryUtil.list(
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
	 * Returns the first document library file shortcut in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file shortcut
	 * @throws NoSuchFileShortcutException if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut findByUuid_First(
			String uuid, OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		DLFileShortcut dlFileShortcut = fetchByUuid_First(
			uuid, orderByComparator);

		if (dlFileShortcut != null) {
			return dlFileShortcut;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchFileShortcutException(sb.toString());
	}

	/**
	 * Returns the first document library file shortcut in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file shortcut, or <code>null</code> if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut fetchByUuid_First(
		String uuid, OrderByComparator<DLFileShortcut> orderByComparator) {

		List<DLFileShortcut> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library file shortcut in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file shortcut
	 * @throws NoSuchFileShortcutException if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut findByUuid_Last(
			String uuid, OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		DLFileShortcut dlFileShortcut = fetchByUuid_Last(
			uuid, orderByComparator);

		if (dlFileShortcut != null) {
			return dlFileShortcut;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchFileShortcutException(sb.toString());
	}

	/**
	 * Returns the last document library file shortcut in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file shortcut, or <code>null</code> if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut fetchByUuid_Last(
		String uuid, OrderByComparator<DLFileShortcut> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<DLFileShortcut> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library file shortcuts before and after the current document library file shortcut in the ordered set where uuid = &#63;.
	 *
	 * @param fileShortcutId the primary key of the current document library file shortcut
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file shortcut
	 * @throws NoSuchFileShortcutException if a document library file shortcut with the primary key could not be found
	 */
	@Override
	public DLFileShortcut[] findByUuid_PrevAndNext(
			long fileShortcutId, String uuid,
			OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		uuid = Objects.toString(uuid, "");

		DLFileShortcut dlFileShortcut = findByPrimaryKey(fileShortcutId);

		Session session = null;

		try {
			session = openSession();

			DLFileShortcut[] array = new DLFileShortcutImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, dlFileShortcut, uuid, orderByComparator, true);

			array[1] = dlFileShortcut;

			array[2] = getByUuid_PrevAndNext(
				session, dlFileShortcut, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DLFileShortcut getByUuid_PrevAndNext(
		Session session, DLFileShortcut dlFileShortcut, String uuid,
		OrderByComparator<DLFileShortcut> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_DLFILESHORTCUT_WHERE);

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
			sb.append(DLFileShortcutModelImpl.ORDER_BY_JPQL);
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
						dlFileShortcut)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFileShortcut> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library file shortcuts where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (DLFileShortcut dlFileShortcut :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(dlFileShortcut);
		}
	}

	/**
	 * Returns the number of document library file shortcuts where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching document library file shortcuts
	 */
	@Override
	public int countByUuid(String uuid) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileShortcut.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid;

			Object[] finderArgs = new Object[] {uuid};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_DLFILESHORTCUT_WHERE);

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
		"dlFileShortcut.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(dlFileShortcut.uuid IS NULL OR dlFileShortcut.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;

	/**
	 * Returns the document library file shortcut where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchFileShortcutException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching document library file shortcut
	 * @throws NoSuchFileShortcutException if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut findByUUID_G(String uuid, long groupId)
		throws NoSuchFileShortcutException {

		DLFileShortcut dlFileShortcut = fetchByUUID_G(uuid, groupId);

		if (dlFileShortcut == null) {
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

			throw new NoSuchFileShortcutException(sb.toString());
		}

		return dlFileShortcut;
	}

	/**
	 * Returns the document library file shortcut where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching document library file shortcut, or <code>null</code> if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the document library file shortcut where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library file shortcut, or <code>null</code> if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileShortcut.class)) {

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

			if (result instanceof DLFileShortcut) {
				DLFileShortcut dlFileShortcut = (DLFileShortcut)result;

				if (!Objects.equals(uuid, dlFileShortcut.getUuid()) ||
					(groupId != dlFileShortcut.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_DLFILESHORTCUT_WHERE);

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

					List<DLFileShortcut> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							FinderCacheUtil.putResult(
								_finderPathFetchByUUID_G, finderArgs, list);
						}
					}
					else {
						DLFileShortcut dlFileShortcut = list.get(0);

						result = dlFileShortcut;

						cacheResult(dlFileShortcut);
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
				return (DLFileShortcut)result;
			}
		}
	}

	/**
	 * Removes the document library file shortcut where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the document library file shortcut that was removed
	 */
	@Override
	public DLFileShortcut removeByUUID_G(String uuid, long groupId)
		throws NoSuchFileShortcutException {

		DLFileShortcut dlFileShortcut = findByUUID_G(uuid, groupId);

		return remove(dlFileShortcut);
	}

	/**
	 * Returns the number of document library file shortcuts where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching document library file shortcuts
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		DLFileShortcut dlFileShortcut = fetchByUUID_G(uuid, groupId);

		if (dlFileShortcut == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"dlFileShortcut.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(dlFileShortcut.uuid IS NULL OR dlFileShortcut.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"dlFileShortcut.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the document library file shortcuts where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file shortcuts where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @return the range of matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file shortcuts where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DLFileShortcut> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library file shortcuts where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DLFileShortcut> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileShortcut.class)) {

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

			List<DLFileShortcut> list = null;

			if (useFinderCache) {
				list = (List<DLFileShortcut>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFileShortcut dlFileShortcut : list) {
						if (!uuid.equals(dlFileShortcut.getUuid()) ||
							(companyId != dlFileShortcut.getCompanyId())) {

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

				sb.append(_SQL_SELECT_DLFILESHORTCUT_WHERE);

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
					sb.append(DLFileShortcutModelImpl.ORDER_BY_JPQL);
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

					list = (List<DLFileShortcut>)QueryUtil.list(
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
	 * Returns the first document library file shortcut in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file shortcut
	 * @throws NoSuchFileShortcutException if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		DLFileShortcut dlFileShortcut = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (dlFileShortcut != null) {
			return dlFileShortcut;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchFileShortcutException(sb.toString());
	}

	/**
	 * Returns the first document library file shortcut in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file shortcut, or <code>null</code> if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<DLFileShortcut> orderByComparator) {

		List<DLFileShortcut> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library file shortcut in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file shortcut
	 * @throws NoSuchFileShortcutException if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		DLFileShortcut dlFileShortcut = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (dlFileShortcut != null) {
			return dlFileShortcut;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchFileShortcutException(sb.toString());
	}

	/**
	 * Returns the last document library file shortcut in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file shortcut, or <code>null</code> if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<DLFileShortcut> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<DLFileShortcut> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library file shortcuts before and after the current document library file shortcut in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param fileShortcutId the primary key of the current document library file shortcut
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file shortcut
	 * @throws NoSuchFileShortcutException if a document library file shortcut with the primary key could not be found
	 */
	@Override
	public DLFileShortcut[] findByUuid_C_PrevAndNext(
			long fileShortcutId, String uuid, long companyId,
			OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		uuid = Objects.toString(uuid, "");

		DLFileShortcut dlFileShortcut = findByPrimaryKey(fileShortcutId);

		Session session = null;

		try {
			session = openSession();

			DLFileShortcut[] array = new DLFileShortcutImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, dlFileShortcut, uuid, companyId, orderByComparator,
				true);

			array[1] = dlFileShortcut;

			array[2] = getByUuid_C_PrevAndNext(
				session, dlFileShortcut, uuid, companyId, orderByComparator,
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

	protected DLFileShortcut getByUuid_C_PrevAndNext(
		Session session, DLFileShortcut dlFileShortcut, String uuid,
		long companyId, OrderByComparator<DLFileShortcut> orderByComparator,
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

		sb.append(_SQL_SELECT_DLFILESHORTCUT_WHERE);

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
			sb.append(DLFileShortcutModelImpl.ORDER_BY_JPQL);
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
						dlFileShortcut)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFileShortcut> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library file shortcuts where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (DLFileShortcut dlFileShortcut :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(dlFileShortcut);
		}
	}

	/**
	 * Returns the number of document library file shortcuts where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching document library file shortcuts
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileShortcut.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid_C;

			Object[] finderArgs = new Object[] {uuid, companyId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_DLFILESHORTCUT_WHERE);

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
		"dlFileShortcut.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(dlFileShortcut.uuid IS NULL OR dlFileShortcut.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"dlFileShortcut.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByGroupId;
	private FinderPath _finderPathWithoutPaginationFindByGroupId;
	private FinderPath _finderPathCountByGroupId;

	/**
	 * Returns all the document library file shortcuts where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file shortcuts where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @return the range of matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByGroupId(
		long groupId, int start, int end) {

		return findByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file shortcuts where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DLFileShortcut> orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library file shortcuts where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DLFileShortcut> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileShortcut.class)) {

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

			List<DLFileShortcut> list = null;

			if (useFinderCache) {
				list = (List<DLFileShortcut>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFileShortcut dlFileShortcut : list) {
						if (groupId != dlFileShortcut.getGroupId()) {
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

				sb.append(_SQL_SELECT_DLFILESHORTCUT_WHERE);

				sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFileShortcutModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					list = (List<DLFileShortcut>)QueryUtil.list(
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
	 * Returns the first document library file shortcut in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file shortcut
	 * @throws NoSuchFileShortcutException if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut findByGroupId_First(
			long groupId, OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		DLFileShortcut dlFileShortcut = fetchByGroupId_First(
			groupId, orderByComparator);

		if (dlFileShortcut != null) {
			return dlFileShortcut;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchFileShortcutException(sb.toString());
	}

	/**
	 * Returns the first document library file shortcut in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file shortcut, or <code>null</code> if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut fetchByGroupId_First(
		long groupId, OrderByComparator<DLFileShortcut> orderByComparator) {

		List<DLFileShortcut> list = findByGroupId(
			groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library file shortcut in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file shortcut
	 * @throws NoSuchFileShortcutException if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut findByGroupId_Last(
			long groupId, OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		DLFileShortcut dlFileShortcut = fetchByGroupId_Last(
			groupId, orderByComparator);

		if (dlFileShortcut != null) {
			return dlFileShortcut;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchFileShortcutException(sb.toString());
	}

	/**
	 * Returns the last document library file shortcut in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file shortcut, or <code>null</code> if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut fetchByGroupId_Last(
		long groupId, OrderByComparator<DLFileShortcut> orderByComparator) {

		int count = countByGroupId(groupId);

		if (count == 0) {
			return null;
		}

		List<DLFileShortcut> list = findByGroupId(
			groupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library file shortcuts before and after the current document library file shortcut in the ordered set where groupId = &#63;.
	 *
	 * @param fileShortcutId the primary key of the current document library file shortcut
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file shortcut
	 * @throws NoSuchFileShortcutException if a document library file shortcut with the primary key could not be found
	 */
	@Override
	public DLFileShortcut[] findByGroupId_PrevAndNext(
			long fileShortcutId, long groupId,
			OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		DLFileShortcut dlFileShortcut = findByPrimaryKey(fileShortcutId);

		Session session = null;

		try {
			session = openSession();

			DLFileShortcut[] array = new DLFileShortcutImpl[3];

			array[0] = getByGroupId_PrevAndNext(
				session, dlFileShortcut, groupId, orderByComparator, true);

			array[1] = dlFileShortcut;

			array[2] = getByGroupId_PrevAndNext(
				session, dlFileShortcut, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DLFileShortcut getByGroupId_PrevAndNext(
		Session session, DLFileShortcut dlFileShortcut, long groupId,
		OrderByComparator<DLFileShortcut> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_DLFILESHORTCUT_WHERE);

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
			sb.append(DLFileShortcutModelImpl.ORDER_BY_JPQL);
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
						dlFileShortcut)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFileShortcut> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the document library file shortcuts that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching document library file shortcuts that the user has permission to view
	 */
	@Override
	public List<DLFileShortcut> filterFindByGroupId(long groupId) {
		return filterFindByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file shortcuts that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @return the range of matching document library file shortcuts that the user has permission to view
	 */
	@Override
	public List<DLFileShortcut> filterFindByGroupId(
		long groupId, int start, int end) {

		return filterFindByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file shortcuts that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file shortcuts that the user has permission to view
	 */
	@Override
	public List<DLFileShortcut> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DLFileShortcut> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_DLFILESHORTCUT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DLFILESHORTCUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DLFILESHORTCUT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DLFileShortcutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DLFileShortcutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFileShortcut.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, DLFileShortcutImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, DLFileShortcutImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			return (List<DLFileShortcut>)QueryUtil.list(
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
	 * Returns the document library file shortcuts before and after the current document library file shortcut in the ordered set of document library file shortcuts that the user has permission to view where groupId = &#63;.
	 *
	 * @param fileShortcutId the primary key of the current document library file shortcut
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file shortcut
	 * @throws NoSuchFileShortcutException if a document library file shortcut with the primary key could not be found
	 */
	@Override
	public DLFileShortcut[] filterFindByGroupId_PrevAndNext(
			long fileShortcutId, long groupId,
			OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByGroupId_PrevAndNext(
				fileShortcutId, groupId, orderByComparator);
		}

		DLFileShortcut dlFileShortcut = findByPrimaryKey(fileShortcutId);

		Session session = null;

		try {
			session = openSession();

			DLFileShortcut[] array = new DLFileShortcutImpl[3];

			array[0] = filterGetByGroupId_PrevAndNext(
				session, dlFileShortcut, groupId, orderByComparator, true);

			array[1] = dlFileShortcut;

			array[2] = filterGetByGroupId_PrevAndNext(
				session, dlFileShortcut, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DLFileShortcut filterGetByGroupId_PrevAndNext(
		Session session, DLFileShortcut dlFileShortcut, long groupId,
		OrderByComparator<DLFileShortcut> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_DLFILESHORTCUT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DLFILESHORTCUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DLFILESHORTCUT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DLFileShortcutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DLFileShortcutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFileShortcut.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DLFileShortcutImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DLFileShortcutImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						dlFileShortcut)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFileShortcut> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library file shortcuts where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		for (DLFileShortcut dlFileShortcut :
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(dlFileShortcut);
		}
	}

	/**
	 * Returns the number of document library file shortcuts where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching document library file shortcuts
	 */
	@Override
	public int countByGroupId(long groupId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileShortcut.class)) {

			FinderPath finderPath = _finderPathCountByGroupId;

			Object[] finderArgs = new Object[] {groupId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_DLFILESHORTCUT_WHERE);

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
	 * Returns the number of document library file shortcuts that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching document library file shortcuts that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByGroupId(groupId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DLFileShortcut> dlFileShortcuts = findByGroupId(groupId);

			dlFileShortcuts = InlineSQLHelperUtil.filter(
				dlFileShortcuts, groupId);

			return dlFileShortcuts.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_DLFILESHORTCUT_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFileShortcut.class.getName(),
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
		"dlFileShortcut.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;

	/**
	 * Returns all the document library file shortcuts where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file shortcuts where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @return the range of matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file shortcuts where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<DLFileShortcut> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library file shortcuts where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<DLFileShortcut> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileShortcut.class)) {

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

			List<DLFileShortcut> list = null;

			if (useFinderCache) {
				list = (List<DLFileShortcut>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFileShortcut dlFileShortcut : list) {
						if (companyId != dlFileShortcut.getCompanyId()) {
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

				sb.append(_SQL_SELECT_DLFILESHORTCUT_WHERE);

				sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFileShortcutModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					list = (List<DLFileShortcut>)QueryUtil.list(
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
	 * Returns the first document library file shortcut in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file shortcut
	 * @throws NoSuchFileShortcutException if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut findByCompanyId_First(
			long companyId, OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		DLFileShortcut dlFileShortcut = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (dlFileShortcut != null) {
			return dlFileShortcut;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchFileShortcutException(sb.toString());
	}

	/**
	 * Returns the first document library file shortcut in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file shortcut, or <code>null</code> if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut fetchByCompanyId_First(
		long companyId, OrderByComparator<DLFileShortcut> orderByComparator) {

		List<DLFileShortcut> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library file shortcut in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file shortcut
	 * @throws NoSuchFileShortcutException if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut findByCompanyId_Last(
			long companyId, OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		DLFileShortcut dlFileShortcut = fetchByCompanyId_Last(
			companyId, orderByComparator);

		if (dlFileShortcut != null) {
			return dlFileShortcut;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchFileShortcutException(sb.toString());
	}

	/**
	 * Returns the last document library file shortcut in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file shortcut, or <code>null</code> if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut fetchByCompanyId_Last(
		long companyId, OrderByComparator<DLFileShortcut> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<DLFileShortcut> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library file shortcuts before and after the current document library file shortcut in the ordered set where companyId = &#63;.
	 *
	 * @param fileShortcutId the primary key of the current document library file shortcut
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file shortcut
	 * @throws NoSuchFileShortcutException if a document library file shortcut with the primary key could not be found
	 */
	@Override
	public DLFileShortcut[] findByCompanyId_PrevAndNext(
			long fileShortcutId, long companyId,
			OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		DLFileShortcut dlFileShortcut = findByPrimaryKey(fileShortcutId);

		Session session = null;

		try {
			session = openSession();

			DLFileShortcut[] array = new DLFileShortcutImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, dlFileShortcut, companyId, orderByComparator, true);

			array[1] = dlFileShortcut;

			array[2] = getByCompanyId_PrevAndNext(
				session, dlFileShortcut, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DLFileShortcut getByCompanyId_PrevAndNext(
		Session session, DLFileShortcut dlFileShortcut, long companyId,
		OrderByComparator<DLFileShortcut> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_DLFILESHORTCUT_WHERE);

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
			sb.append(DLFileShortcutModelImpl.ORDER_BY_JPQL);
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
						dlFileShortcut)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFileShortcut> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library file shortcuts where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (DLFileShortcut dlFileShortcut :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(dlFileShortcut);
		}
	}

	/**
	 * Returns the number of document library file shortcuts where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching document library file shortcuts
	 */
	@Override
	public int countByCompanyId(long companyId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileShortcut.class)) {

			FinderPath finderPath = _finderPathCountByCompanyId;

			Object[] finderArgs = new Object[] {companyId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_DLFILESHORTCUT_WHERE);

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
		"dlFileShortcut.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByToFileEntryId;
	private FinderPath _finderPathWithoutPaginationFindByToFileEntryId;
	private FinderPath _finderPathCountByToFileEntryId;

	/**
	 * Returns all the document library file shortcuts where toFileEntryId = &#63;.
	 *
	 * @param toFileEntryId the to file entry ID
	 * @return the matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByToFileEntryId(long toFileEntryId) {
		return findByToFileEntryId(
			toFileEntryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file shortcuts where toFileEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param toFileEntryId the to file entry ID
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @return the range of matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByToFileEntryId(
		long toFileEntryId, int start, int end) {

		return findByToFileEntryId(toFileEntryId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file shortcuts where toFileEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param toFileEntryId the to file entry ID
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByToFileEntryId(
		long toFileEntryId, int start, int end,
		OrderByComparator<DLFileShortcut> orderByComparator) {

		return findByToFileEntryId(
			toFileEntryId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library file shortcuts where toFileEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param toFileEntryId the to file entry ID
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByToFileEntryId(
		long toFileEntryId, int start, int end,
		OrderByComparator<DLFileShortcut> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileShortcut.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath =
						_finderPathWithoutPaginationFindByToFileEntryId;
					finderArgs = new Object[] {toFileEntryId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByToFileEntryId;
				finderArgs = new Object[] {
					toFileEntryId, start, end, orderByComparator
				};
			}

			List<DLFileShortcut> list = null;

			if (useFinderCache) {
				list = (List<DLFileShortcut>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFileShortcut dlFileShortcut : list) {
						if (toFileEntryId !=
								dlFileShortcut.getToFileEntryId()) {

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

				sb.append(_SQL_SELECT_DLFILESHORTCUT_WHERE);

				sb.append(_FINDER_COLUMN_TOFILEENTRYID_TOFILEENTRYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFileShortcutModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(toFileEntryId);

					list = (List<DLFileShortcut>)QueryUtil.list(
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
	 * Returns the first document library file shortcut in the ordered set where toFileEntryId = &#63;.
	 *
	 * @param toFileEntryId the to file entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file shortcut
	 * @throws NoSuchFileShortcutException if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut findByToFileEntryId_First(
			long toFileEntryId,
			OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		DLFileShortcut dlFileShortcut = fetchByToFileEntryId_First(
			toFileEntryId, orderByComparator);

		if (dlFileShortcut != null) {
			return dlFileShortcut;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("toFileEntryId=");
		sb.append(toFileEntryId);

		sb.append("}");

		throw new NoSuchFileShortcutException(sb.toString());
	}

	/**
	 * Returns the first document library file shortcut in the ordered set where toFileEntryId = &#63;.
	 *
	 * @param toFileEntryId the to file entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file shortcut, or <code>null</code> if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut fetchByToFileEntryId_First(
		long toFileEntryId,
		OrderByComparator<DLFileShortcut> orderByComparator) {

		List<DLFileShortcut> list = findByToFileEntryId(
			toFileEntryId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library file shortcut in the ordered set where toFileEntryId = &#63;.
	 *
	 * @param toFileEntryId the to file entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file shortcut
	 * @throws NoSuchFileShortcutException if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut findByToFileEntryId_Last(
			long toFileEntryId,
			OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		DLFileShortcut dlFileShortcut = fetchByToFileEntryId_Last(
			toFileEntryId, orderByComparator);

		if (dlFileShortcut != null) {
			return dlFileShortcut;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("toFileEntryId=");
		sb.append(toFileEntryId);

		sb.append("}");

		throw new NoSuchFileShortcutException(sb.toString());
	}

	/**
	 * Returns the last document library file shortcut in the ordered set where toFileEntryId = &#63;.
	 *
	 * @param toFileEntryId the to file entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file shortcut, or <code>null</code> if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut fetchByToFileEntryId_Last(
		long toFileEntryId,
		OrderByComparator<DLFileShortcut> orderByComparator) {

		int count = countByToFileEntryId(toFileEntryId);

		if (count == 0) {
			return null;
		}

		List<DLFileShortcut> list = findByToFileEntryId(
			toFileEntryId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library file shortcuts before and after the current document library file shortcut in the ordered set where toFileEntryId = &#63;.
	 *
	 * @param fileShortcutId the primary key of the current document library file shortcut
	 * @param toFileEntryId the to file entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file shortcut
	 * @throws NoSuchFileShortcutException if a document library file shortcut with the primary key could not be found
	 */
	@Override
	public DLFileShortcut[] findByToFileEntryId_PrevAndNext(
			long fileShortcutId, long toFileEntryId,
			OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		DLFileShortcut dlFileShortcut = findByPrimaryKey(fileShortcutId);

		Session session = null;

		try {
			session = openSession();

			DLFileShortcut[] array = new DLFileShortcutImpl[3];

			array[0] = getByToFileEntryId_PrevAndNext(
				session, dlFileShortcut, toFileEntryId, orderByComparator,
				true);

			array[1] = dlFileShortcut;

			array[2] = getByToFileEntryId_PrevAndNext(
				session, dlFileShortcut, toFileEntryId, orderByComparator,
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

	protected DLFileShortcut getByToFileEntryId_PrevAndNext(
		Session session, DLFileShortcut dlFileShortcut, long toFileEntryId,
		OrderByComparator<DLFileShortcut> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_DLFILESHORTCUT_WHERE);

		sb.append(_FINDER_COLUMN_TOFILEENTRYID_TOFILEENTRYID_2);

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
			sb.append(DLFileShortcutModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(toFileEntryId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						dlFileShortcut)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFileShortcut> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library file shortcuts where toFileEntryId = &#63; from the database.
	 *
	 * @param toFileEntryId the to file entry ID
	 */
	@Override
	public void removeByToFileEntryId(long toFileEntryId) {
		for (DLFileShortcut dlFileShortcut :
				findByToFileEntryId(
					toFileEntryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(dlFileShortcut);
		}
	}

	/**
	 * Returns the number of document library file shortcuts where toFileEntryId = &#63;.
	 *
	 * @param toFileEntryId the to file entry ID
	 * @return the number of matching document library file shortcuts
	 */
	@Override
	public int countByToFileEntryId(long toFileEntryId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileShortcut.class)) {

			FinderPath finderPath = _finderPathCountByToFileEntryId;

			Object[] finderArgs = new Object[] {toFileEntryId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_DLFILESHORTCUT_WHERE);

				sb.append(_FINDER_COLUMN_TOFILEENTRYID_TOFILEENTRYID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(toFileEntryId);

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

	private static final String _FINDER_COLUMN_TOFILEENTRYID_TOFILEENTRYID_2 =
		"dlFileShortcut.toFileEntryId = ?";

	private FinderPath _finderPathWithPaginationFindByG_F;
	private FinderPath _finderPathWithoutPaginationFindByG_F;
	private FinderPath _finderPathCountByG_F;

	/**
	 * Returns all the document library file shortcuts where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @return the matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByG_F(long groupId, long folderId) {
		return findByG_F(
			groupId, folderId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file shortcuts where groupId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @return the range of matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByG_F(
		long groupId, long folderId, int start, int end) {

		return findByG_F(groupId, folderId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file shortcuts where groupId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByG_F(
		long groupId, long folderId, int start, int end,
		OrderByComparator<DLFileShortcut> orderByComparator) {

		return findByG_F(
			groupId, folderId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library file shortcuts where groupId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByG_F(
		long groupId, long folderId, int start, int end,
		OrderByComparator<DLFileShortcut> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileShortcut.class)) {

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

			List<DLFileShortcut> list = null;

			if (useFinderCache) {
				list = (List<DLFileShortcut>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFileShortcut dlFileShortcut : list) {
						if ((groupId != dlFileShortcut.getGroupId()) ||
							(folderId != dlFileShortcut.getFolderId())) {

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

				sb.append(_SQL_SELECT_DLFILESHORTCUT_WHERE);

				sb.append(_FINDER_COLUMN_G_F_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_F_FOLDERID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFileShortcutModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(folderId);

					list = (List<DLFileShortcut>)QueryUtil.list(
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
	 * Returns the first document library file shortcut in the ordered set where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file shortcut
	 * @throws NoSuchFileShortcutException if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut findByG_F_First(
			long groupId, long folderId,
			OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		DLFileShortcut dlFileShortcut = fetchByG_F_First(
			groupId, folderId, orderByComparator);

		if (dlFileShortcut != null) {
			return dlFileShortcut;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", folderId=");
		sb.append(folderId);

		sb.append("}");

		throw new NoSuchFileShortcutException(sb.toString());
	}

	/**
	 * Returns the first document library file shortcut in the ordered set where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file shortcut, or <code>null</code> if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut fetchByG_F_First(
		long groupId, long folderId,
		OrderByComparator<DLFileShortcut> orderByComparator) {

		List<DLFileShortcut> list = findByG_F(
			groupId, folderId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library file shortcut in the ordered set where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file shortcut
	 * @throws NoSuchFileShortcutException if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut findByG_F_Last(
			long groupId, long folderId,
			OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		DLFileShortcut dlFileShortcut = fetchByG_F_Last(
			groupId, folderId, orderByComparator);

		if (dlFileShortcut != null) {
			return dlFileShortcut;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", folderId=");
		sb.append(folderId);

		sb.append("}");

		throw new NoSuchFileShortcutException(sb.toString());
	}

	/**
	 * Returns the last document library file shortcut in the ordered set where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file shortcut, or <code>null</code> if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut fetchByG_F_Last(
		long groupId, long folderId,
		OrderByComparator<DLFileShortcut> orderByComparator) {

		int count = countByG_F(groupId, folderId);

		if (count == 0) {
			return null;
		}

		List<DLFileShortcut> list = findByG_F(
			groupId, folderId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library file shortcuts before and after the current document library file shortcut in the ordered set where groupId = &#63; and folderId = &#63;.
	 *
	 * @param fileShortcutId the primary key of the current document library file shortcut
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file shortcut
	 * @throws NoSuchFileShortcutException if a document library file shortcut with the primary key could not be found
	 */
	@Override
	public DLFileShortcut[] findByG_F_PrevAndNext(
			long fileShortcutId, long groupId, long folderId,
			OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		DLFileShortcut dlFileShortcut = findByPrimaryKey(fileShortcutId);

		Session session = null;

		try {
			session = openSession();

			DLFileShortcut[] array = new DLFileShortcutImpl[3];

			array[0] = getByG_F_PrevAndNext(
				session, dlFileShortcut, groupId, folderId, orderByComparator,
				true);

			array[1] = dlFileShortcut;

			array[2] = getByG_F_PrevAndNext(
				session, dlFileShortcut, groupId, folderId, orderByComparator,
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

	protected DLFileShortcut getByG_F_PrevAndNext(
		Session session, DLFileShortcut dlFileShortcut, long groupId,
		long folderId, OrderByComparator<DLFileShortcut> orderByComparator,
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

		sb.append(_SQL_SELECT_DLFILESHORTCUT_WHERE);

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
			sb.append(DLFileShortcutModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(
						dlFileShortcut)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFileShortcut> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the document library file shortcuts that the user has permission to view where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @return the matching document library file shortcuts that the user has permission to view
	 */
	@Override
	public List<DLFileShortcut> filterFindByG_F(long groupId, long folderId) {
		return filterFindByG_F(
			groupId, folderId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file shortcuts that the user has permission to view where groupId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @return the range of matching document library file shortcuts that the user has permission to view
	 */
	@Override
	public List<DLFileShortcut> filterFindByG_F(
		long groupId, long folderId, int start, int end) {

		return filterFindByG_F(groupId, folderId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file shortcuts that the user has permissions to view where groupId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file shortcuts that the user has permission to view
	 */
	@Override
	public List<DLFileShortcut> filterFindByG_F(
		long groupId, long folderId, int start, int end,
		OrderByComparator<DLFileShortcut> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_DLFILESHORTCUT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DLFILESHORTCUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_F_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_F_FOLDERID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DLFILESHORTCUT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DLFileShortcutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DLFileShortcutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFileShortcut.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, DLFileShortcutImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, DLFileShortcutImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(folderId);

			return (List<DLFileShortcut>)QueryUtil.list(
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
	 * Returns the document library file shortcuts before and after the current document library file shortcut in the ordered set of document library file shortcuts that the user has permission to view where groupId = &#63; and folderId = &#63;.
	 *
	 * @param fileShortcutId the primary key of the current document library file shortcut
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file shortcut
	 * @throws NoSuchFileShortcutException if a document library file shortcut with the primary key could not be found
	 */
	@Override
	public DLFileShortcut[] filterFindByG_F_PrevAndNext(
			long fileShortcutId, long groupId, long folderId,
			OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_F_PrevAndNext(
				fileShortcutId, groupId, folderId, orderByComparator);
		}

		DLFileShortcut dlFileShortcut = findByPrimaryKey(fileShortcutId);

		Session session = null;

		try {
			session = openSession();

			DLFileShortcut[] array = new DLFileShortcutImpl[3];

			array[0] = filterGetByG_F_PrevAndNext(
				session, dlFileShortcut, groupId, folderId, orderByComparator,
				true);

			array[1] = dlFileShortcut;

			array[2] = filterGetByG_F_PrevAndNext(
				session, dlFileShortcut, groupId, folderId, orderByComparator,
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

	protected DLFileShortcut filterGetByG_F_PrevAndNext(
		Session session, DLFileShortcut dlFileShortcut, long groupId,
		long folderId, OrderByComparator<DLFileShortcut> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_DLFILESHORTCUT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DLFILESHORTCUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_F_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_F_FOLDERID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DLFILESHORTCUT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DLFileShortcutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DLFileShortcutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFileShortcut.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DLFileShortcutImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DLFileShortcutImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(folderId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						dlFileShortcut)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFileShortcut> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library file shortcuts where groupId = &#63; and folderId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 */
	@Override
	public void removeByG_F(long groupId, long folderId) {
		for (DLFileShortcut dlFileShortcut :
				findByG_F(
					groupId, folderId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(dlFileShortcut);
		}
	}

	/**
	 * Returns the number of document library file shortcuts where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @return the number of matching document library file shortcuts
	 */
	@Override
	public int countByG_F(long groupId, long folderId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileShortcut.class)) {

			FinderPath finderPath = _finderPathCountByG_F;

			Object[] finderArgs = new Object[] {groupId, folderId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_DLFILESHORTCUT_WHERE);

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
	 * Returns the number of document library file shortcuts that the user has permission to view where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @return the number of matching document library file shortcuts that the user has permission to view
	 */
	@Override
	public int filterCountByG_F(long groupId, long folderId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_F(groupId, folderId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DLFileShortcut> dlFileShortcuts = findByG_F(groupId, folderId);

			dlFileShortcuts = InlineSQLHelperUtil.filter(
				dlFileShortcuts, groupId);

			return dlFileShortcuts.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_DLFILESHORTCUT_WHERE);

		sb.append(_FINDER_COLUMN_G_F_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_F_FOLDERID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFileShortcut.class.getName(),
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

	private static final String _FINDER_COLUMN_G_F_GROUPID_2 =
		"dlFileShortcut.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_F_FOLDERID_2 =
		"dlFileShortcut.folderId = ?";

	private FinderPath _finderPathWithPaginationFindByC_NotS;
	private FinderPath _finderPathWithPaginationCountByC_NotS;

	/**
	 * Returns all the document library file shortcuts where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByC_NotS(long companyId, int status) {
		return findByC_NotS(
			companyId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file shortcuts where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @return the range of matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByC_NotS(
		long companyId, int status, int start, int end) {

		return findByC_NotS(companyId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file shortcuts where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByC_NotS(
		long companyId, int status, int start, int end,
		OrderByComparator<DLFileShortcut> orderByComparator) {

		return findByC_NotS(
			companyId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library file shortcuts where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByC_NotS(
		long companyId, int status, int start, int end,
		OrderByComparator<DLFileShortcut> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileShortcut.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByC_NotS;
			finderArgs = new Object[] {
				companyId, status, start, end, orderByComparator
			};

			List<DLFileShortcut> list = null;

			if (useFinderCache) {
				list = (List<DLFileShortcut>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFileShortcut dlFileShortcut : list) {
						if ((companyId != dlFileShortcut.getCompanyId()) ||
							(status == dlFileShortcut.getStatus())) {

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

				sb.append(_SQL_SELECT_DLFILESHORTCUT_WHERE);

				sb.append(_FINDER_COLUMN_C_NOTS_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_NOTS_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFileShortcutModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(status);

					list = (List<DLFileShortcut>)QueryUtil.list(
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
	 * Returns the first document library file shortcut in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file shortcut
	 * @throws NoSuchFileShortcutException if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut findByC_NotS_First(
			long companyId, int status,
			OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		DLFileShortcut dlFileShortcut = fetchByC_NotS_First(
			companyId, status, orderByComparator);

		if (dlFileShortcut != null) {
			return dlFileShortcut;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchFileShortcutException(sb.toString());
	}

	/**
	 * Returns the first document library file shortcut in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file shortcut, or <code>null</code> if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut fetchByC_NotS_First(
		long companyId, int status,
		OrderByComparator<DLFileShortcut> orderByComparator) {

		List<DLFileShortcut> list = findByC_NotS(
			companyId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library file shortcut in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file shortcut
	 * @throws NoSuchFileShortcutException if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut findByC_NotS_Last(
			long companyId, int status,
			OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		DLFileShortcut dlFileShortcut = fetchByC_NotS_Last(
			companyId, status, orderByComparator);

		if (dlFileShortcut != null) {
			return dlFileShortcut;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchFileShortcutException(sb.toString());
	}

	/**
	 * Returns the last document library file shortcut in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file shortcut, or <code>null</code> if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut fetchByC_NotS_Last(
		long companyId, int status,
		OrderByComparator<DLFileShortcut> orderByComparator) {

		int count = countByC_NotS(companyId, status);

		if (count == 0) {
			return null;
		}

		List<DLFileShortcut> list = findByC_NotS(
			companyId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library file shortcuts before and after the current document library file shortcut in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param fileShortcutId the primary key of the current document library file shortcut
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file shortcut
	 * @throws NoSuchFileShortcutException if a document library file shortcut with the primary key could not be found
	 */
	@Override
	public DLFileShortcut[] findByC_NotS_PrevAndNext(
			long fileShortcutId, long companyId, int status,
			OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		DLFileShortcut dlFileShortcut = findByPrimaryKey(fileShortcutId);

		Session session = null;

		try {
			session = openSession();

			DLFileShortcut[] array = new DLFileShortcutImpl[3];

			array[0] = getByC_NotS_PrevAndNext(
				session, dlFileShortcut, companyId, status, orderByComparator,
				true);

			array[1] = dlFileShortcut;

			array[2] = getByC_NotS_PrevAndNext(
				session, dlFileShortcut, companyId, status, orderByComparator,
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

	protected DLFileShortcut getByC_NotS_PrevAndNext(
		Session session, DLFileShortcut dlFileShortcut, long companyId,
		int status, OrderByComparator<DLFileShortcut> orderByComparator,
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

		sb.append(_SQL_SELECT_DLFILESHORTCUT_WHERE);

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
			sb.append(DLFileShortcutModelImpl.ORDER_BY_JPQL);
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
						dlFileShortcut)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFileShortcut> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library file shortcuts where companyId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 */
	@Override
	public void removeByC_NotS(long companyId, int status) {
		for (DLFileShortcut dlFileShortcut :
				findByC_NotS(
					companyId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(dlFileShortcut);
		}
	}

	/**
	 * Returns the number of document library file shortcuts where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching document library file shortcuts
	 */
	@Override
	public int countByC_NotS(long companyId, int status) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileShortcut.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByC_NotS;

			Object[] finderArgs = new Object[] {companyId, status};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_DLFILESHORTCUT_WHERE);

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

	private static final String _FINDER_COLUMN_C_NOTS_COMPANYID_2 =
		"dlFileShortcut.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_NOTS_STATUS_2 =
		"dlFileShortcut.status != ?";

	private FinderPath _finderPathWithPaginationFindByG_F_A;
	private FinderPath _finderPathWithoutPaginationFindByG_F_A;
	private FinderPath _finderPathCountByG_F_A;

	/**
	 * Returns all the document library file shortcuts where groupId = &#63; and folderId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @return the matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByG_F_A(
		long groupId, long folderId, boolean active) {

		return findByG_F_A(
			groupId, folderId, active, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the document library file shortcuts where groupId = &#63; and folderId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @return the range of matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByG_F_A(
		long groupId, long folderId, boolean active, int start, int end) {

		return findByG_F_A(groupId, folderId, active, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file shortcuts where groupId = &#63; and folderId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByG_F_A(
		long groupId, long folderId, boolean active, int start, int end,
		OrderByComparator<DLFileShortcut> orderByComparator) {

		return findByG_F_A(
			groupId, folderId, active, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library file shortcuts where groupId = &#63; and folderId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByG_F_A(
		long groupId, long folderId, boolean active, int start, int end,
		OrderByComparator<DLFileShortcut> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileShortcut.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_F_A;
					finderArgs = new Object[] {groupId, folderId, active};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_F_A;
				finderArgs = new Object[] {
					groupId, folderId, active, start, end, orderByComparator
				};
			}

			List<DLFileShortcut> list = null;

			if (useFinderCache) {
				list = (List<DLFileShortcut>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFileShortcut dlFileShortcut : list) {
						if ((groupId != dlFileShortcut.getGroupId()) ||
							(folderId != dlFileShortcut.getFolderId()) ||
							(active != dlFileShortcut.isActive())) {

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

				sb.append(_SQL_SELECT_DLFILESHORTCUT_WHERE);

				sb.append(_FINDER_COLUMN_G_F_A_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_F_A_FOLDERID_2);

				sb.append(_FINDER_COLUMN_G_F_A_ACTIVE_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFileShortcutModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(folderId);

					queryPos.add(active);

					list = (List<DLFileShortcut>)QueryUtil.list(
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
	 * Returns the first document library file shortcut in the ordered set where groupId = &#63; and folderId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file shortcut
	 * @throws NoSuchFileShortcutException if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut findByG_F_A_First(
			long groupId, long folderId, boolean active,
			OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		DLFileShortcut dlFileShortcut = fetchByG_F_A_First(
			groupId, folderId, active, orderByComparator);

		if (dlFileShortcut != null) {
			return dlFileShortcut;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", folderId=");
		sb.append(folderId);

		sb.append(", active=");
		sb.append(active);

		sb.append("}");

		throw new NoSuchFileShortcutException(sb.toString());
	}

	/**
	 * Returns the first document library file shortcut in the ordered set where groupId = &#63; and folderId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file shortcut, or <code>null</code> if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut fetchByG_F_A_First(
		long groupId, long folderId, boolean active,
		OrderByComparator<DLFileShortcut> orderByComparator) {

		List<DLFileShortcut> list = findByG_F_A(
			groupId, folderId, active, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library file shortcut in the ordered set where groupId = &#63; and folderId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file shortcut
	 * @throws NoSuchFileShortcutException if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut findByG_F_A_Last(
			long groupId, long folderId, boolean active,
			OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		DLFileShortcut dlFileShortcut = fetchByG_F_A_Last(
			groupId, folderId, active, orderByComparator);

		if (dlFileShortcut != null) {
			return dlFileShortcut;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", folderId=");
		sb.append(folderId);

		sb.append(", active=");
		sb.append(active);

		sb.append("}");

		throw new NoSuchFileShortcutException(sb.toString());
	}

	/**
	 * Returns the last document library file shortcut in the ordered set where groupId = &#63; and folderId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file shortcut, or <code>null</code> if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut fetchByG_F_A_Last(
		long groupId, long folderId, boolean active,
		OrderByComparator<DLFileShortcut> orderByComparator) {

		int count = countByG_F_A(groupId, folderId, active);

		if (count == 0) {
			return null;
		}

		List<DLFileShortcut> list = findByG_F_A(
			groupId, folderId, active, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library file shortcuts before and after the current document library file shortcut in the ordered set where groupId = &#63; and folderId = &#63; and active = &#63;.
	 *
	 * @param fileShortcutId the primary key of the current document library file shortcut
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file shortcut
	 * @throws NoSuchFileShortcutException if a document library file shortcut with the primary key could not be found
	 */
	@Override
	public DLFileShortcut[] findByG_F_A_PrevAndNext(
			long fileShortcutId, long groupId, long folderId, boolean active,
			OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		DLFileShortcut dlFileShortcut = findByPrimaryKey(fileShortcutId);

		Session session = null;

		try {
			session = openSession();

			DLFileShortcut[] array = new DLFileShortcutImpl[3];

			array[0] = getByG_F_A_PrevAndNext(
				session, dlFileShortcut, groupId, folderId, active,
				orderByComparator, true);

			array[1] = dlFileShortcut;

			array[2] = getByG_F_A_PrevAndNext(
				session, dlFileShortcut, groupId, folderId, active,
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

	protected DLFileShortcut getByG_F_A_PrevAndNext(
		Session session, DLFileShortcut dlFileShortcut, long groupId,
		long folderId, boolean active,
		OrderByComparator<DLFileShortcut> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_DLFILESHORTCUT_WHERE);

		sb.append(_FINDER_COLUMN_G_F_A_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_F_A_FOLDERID_2);

		sb.append(_FINDER_COLUMN_G_F_A_ACTIVE_2);

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
			sb.append(DLFileShortcutModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(folderId);

		queryPos.add(active);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						dlFileShortcut)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFileShortcut> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the document library file shortcuts that the user has permission to view where groupId = &#63; and folderId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @return the matching document library file shortcuts that the user has permission to view
	 */
	@Override
	public List<DLFileShortcut> filterFindByG_F_A(
		long groupId, long folderId, boolean active) {

		return filterFindByG_F_A(
			groupId, folderId, active, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the document library file shortcuts that the user has permission to view where groupId = &#63; and folderId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @return the range of matching document library file shortcuts that the user has permission to view
	 */
	@Override
	public List<DLFileShortcut> filterFindByG_F_A(
		long groupId, long folderId, boolean active, int start, int end) {

		return filterFindByG_F_A(groupId, folderId, active, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file shortcuts that the user has permissions to view where groupId = &#63; and folderId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file shortcuts that the user has permission to view
	 */
	@Override
	public List<DLFileShortcut> filterFindByG_F_A(
		long groupId, long folderId, boolean active, int start, int end,
		OrderByComparator<DLFileShortcut> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_F_A(
				groupId, folderId, active, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_F_A(
					groupId, folderId, active, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_DLFILESHORTCUT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DLFILESHORTCUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_F_A_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_F_A_FOLDERID_2);

		sb.append(_FINDER_COLUMN_G_F_A_ACTIVE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DLFILESHORTCUT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DLFileShortcutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DLFileShortcutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFileShortcut.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, DLFileShortcutImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, DLFileShortcutImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(folderId);

			queryPos.add(active);

			return (List<DLFileShortcut>)QueryUtil.list(
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
	 * Returns the document library file shortcuts before and after the current document library file shortcut in the ordered set of document library file shortcuts that the user has permission to view where groupId = &#63; and folderId = &#63; and active = &#63;.
	 *
	 * @param fileShortcutId the primary key of the current document library file shortcut
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file shortcut
	 * @throws NoSuchFileShortcutException if a document library file shortcut with the primary key could not be found
	 */
	@Override
	public DLFileShortcut[] filterFindByG_F_A_PrevAndNext(
			long fileShortcutId, long groupId, long folderId, boolean active,
			OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_F_A_PrevAndNext(
				fileShortcutId, groupId, folderId, active, orderByComparator);
		}

		DLFileShortcut dlFileShortcut = findByPrimaryKey(fileShortcutId);

		Session session = null;

		try {
			session = openSession();

			DLFileShortcut[] array = new DLFileShortcutImpl[3];

			array[0] = filterGetByG_F_A_PrevAndNext(
				session, dlFileShortcut, groupId, folderId, active,
				orderByComparator, true);

			array[1] = dlFileShortcut;

			array[2] = filterGetByG_F_A_PrevAndNext(
				session, dlFileShortcut, groupId, folderId, active,
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

	protected DLFileShortcut filterGetByG_F_A_PrevAndNext(
		Session session, DLFileShortcut dlFileShortcut, long groupId,
		long folderId, boolean active,
		OrderByComparator<DLFileShortcut> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_DLFILESHORTCUT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DLFILESHORTCUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_F_A_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_F_A_FOLDERID_2);

		sb.append(_FINDER_COLUMN_G_F_A_ACTIVE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DLFILESHORTCUT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DLFileShortcutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DLFileShortcutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFileShortcut.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DLFileShortcutImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DLFileShortcutImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(folderId);

		queryPos.add(active);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						dlFileShortcut)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFileShortcut> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library file shortcuts where groupId = &#63; and folderId = &#63; and active = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 */
	@Override
	public void removeByG_F_A(long groupId, long folderId, boolean active) {
		for (DLFileShortcut dlFileShortcut :
				findByG_F_A(
					groupId, folderId, active, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(dlFileShortcut);
		}
	}

	/**
	 * Returns the number of document library file shortcuts where groupId = &#63; and folderId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @return the number of matching document library file shortcuts
	 */
	@Override
	public int countByG_F_A(long groupId, long folderId, boolean active) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileShortcut.class)) {

			FinderPath finderPath = _finderPathCountByG_F_A;

			Object[] finderArgs = new Object[] {groupId, folderId, active};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_DLFILESHORTCUT_WHERE);

				sb.append(_FINDER_COLUMN_G_F_A_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_F_A_FOLDERID_2);

				sb.append(_FINDER_COLUMN_G_F_A_ACTIVE_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(folderId);

					queryPos.add(active);

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
	 * Returns the number of document library file shortcuts that the user has permission to view where groupId = &#63; and folderId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @return the number of matching document library file shortcuts that the user has permission to view
	 */
	@Override
	public int filterCountByG_F_A(long groupId, long folderId, boolean active) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_F_A(groupId, folderId, active);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DLFileShortcut> dlFileShortcuts = findByG_F_A(
				groupId, folderId, active);

			dlFileShortcuts = InlineSQLHelperUtil.filter(
				dlFileShortcuts, groupId);

			return dlFileShortcuts.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_DLFILESHORTCUT_WHERE);

		sb.append(_FINDER_COLUMN_G_F_A_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_F_A_FOLDERID_2);

		sb.append(_FINDER_COLUMN_G_F_A_ACTIVE_2_SQL);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFileShortcut.class.getName(),
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

	private static final String _FINDER_COLUMN_G_F_A_GROUPID_2 =
		"dlFileShortcut.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_F_A_FOLDERID_2 =
		"dlFileShortcut.folderId = ? AND ";

	private static final String _FINDER_COLUMN_G_F_A_ACTIVE_2 =
		"dlFileShortcut.active = ?";

	private static final String _FINDER_COLUMN_G_F_A_ACTIVE_2_SQL =
		"dlFileShortcut.active_ = ?";

	private FinderPath _finderPathWithPaginationFindByG_F_A_S;
	private FinderPath _finderPathWithoutPaginationFindByG_F_A_S;
	private FinderPath _finderPathCountByG_F_A_S;

	/**
	 * Returns all the document library file shortcuts where groupId = &#63; and folderId = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @param status the status
	 * @return the matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByG_F_A_S(
		long groupId, long folderId, boolean active, int status) {

		return findByG_F_A_S(
			groupId, folderId, active, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file shortcuts where groupId = &#63; and folderId = &#63; and active = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @param status the status
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @return the range of matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByG_F_A_S(
		long groupId, long folderId, boolean active, int status, int start,
		int end) {

		return findByG_F_A_S(
			groupId, folderId, active, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file shortcuts where groupId = &#63; and folderId = &#63; and active = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @param status the status
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByG_F_A_S(
		long groupId, long folderId, boolean active, int status, int start,
		int end, OrderByComparator<DLFileShortcut> orderByComparator) {

		return findByG_F_A_S(
			groupId, folderId, active, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the document library file shortcuts where groupId = &#63; and folderId = &#63; and active = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @param status the status
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findByG_F_A_S(
		long groupId, long folderId, boolean active, int status, int start,
		int end, OrderByComparator<DLFileShortcut> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileShortcut.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_F_A_S;
					finderArgs = new Object[] {
						groupId, folderId, active, status
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_F_A_S;
				finderArgs = new Object[] {
					groupId, folderId, active, status, start, end,
					orderByComparator
				};
			}

			List<DLFileShortcut> list = null;

			if (useFinderCache) {
				list = (List<DLFileShortcut>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLFileShortcut dlFileShortcut : list) {
						if ((groupId != dlFileShortcut.getGroupId()) ||
							(folderId != dlFileShortcut.getFolderId()) ||
							(active != dlFileShortcut.isActive()) ||
							(status != dlFileShortcut.getStatus())) {

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

				sb.append(_SQL_SELECT_DLFILESHORTCUT_WHERE);

				sb.append(_FINDER_COLUMN_G_F_A_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_F_A_S_FOLDERID_2);

				sb.append(_FINDER_COLUMN_G_F_A_S_ACTIVE_2);

				sb.append(_FINDER_COLUMN_G_F_A_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLFileShortcutModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(folderId);

					queryPos.add(active);

					queryPos.add(status);

					list = (List<DLFileShortcut>)QueryUtil.list(
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
	 * Returns the first document library file shortcut in the ordered set where groupId = &#63; and folderId = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file shortcut
	 * @throws NoSuchFileShortcutException if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut findByG_F_A_S_First(
			long groupId, long folderId, boolean active, int status,
			OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		DLFileShortcut dlFileShortcut = fetchByG_F_A_S_First(
			groupId, folderId, active, status, orderByComparator);

		if (dlFileShortcut != null) {
			return dlFileShortcut;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", folderId=");
		sb.append(folderId);

		sb.append(", active=");
		sb.append(active);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchFileShortcutException(sb.toString());
	}

	/**
	 * Returns the first document library file shortcut in the ordered set where groupId = &#63; and folderId = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file shortcut, or <code>null</code> if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut fetchByG_F_A_S_First(
		long groupId, long folderId, boolean active, int status,
		OrderByComparator<DLFileShortcut> orderByComparator) {

		List<DLFileShortcut> list = findByG_F_A_S(
			groupId, folderId, active, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library file shortcut in the ordered set where groupId = &#63; and folderId = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file shortcut
	 * @throws NoSuchFileShortcutException if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut findByG_F_A_S_Last(
			long groupId, long folderId, boolean active, int status,
			OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		DLFileShortcut dlFileShortcut = fetchByG_F_A_S_Last(
			groupId, folderId, active, status, orderByComparator);

		if (dlFileShortcut != null) {
			return dlFileShortcut;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", folderId=");
		sb.append(folderId);

		sb.append(", active=");
		sb.append(active);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchFileShortcutException(sb.toString());
	}

	/**
	 * Returns the last document library file shortcut in the ordered set where groupId = &#63; and folderId = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file shortcut, or <code>null</code> if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut fetchByG_F_A_S_Last(
		long groupId, long folderId, boolean active, int status,
		OrderByComparator<DLFileShortcut> orderByComparator) {

		int count = countByG_F_A_S(groupId, folderId, active, status);

		if (count == 0) {
			return null;
		}

		List<DLFileShortcut> list = findByG_F_A_S(
			groupId, folderId, active, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library file shortcuts before and after the current document library file shortcut in the ordered set where groupId = &#63; and folderId = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param fileShortcutId the primary key of the current document library file shortcut
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file shortcut
	 * @throws NoSuchFileShortcutException if a document library file shortcut with the primary key could not be found
	 */
	@Override
	public DLFileShortcut[] findByG_F_A_S_PrevAndNext(
			long fileShortcutId, long groupId, long folderId, boolean active,
			int status, OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		DLFileShortcut dlFileShortcut = findByPrimaryKey(fileShortcutId);

		Session session = null;

		try {
			session = openSession();

			DLFileShortcut[] array = new DLFileShortcutImpl[3];

			array[0] = getByG_F_A_S_PrevAndNext(
				session, dlFileShortcut, groupId, folderId, active, status,
				orderByComparator, true);

			array[1] = dlFileShortcut;

			array[2] = getByG_F_A_S_PrevAndNext(
				session, dlFileShortcut, groupId, folderId, active, status,
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

	protected DLFileShortcut getByG_F_A_S_PrevAndNext(
		Session session, DLFileShortcut dlFileShortcut, long groupId,
		long folderId, boolean active, int status,
		OrderByComparator<DLFileShortcut> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		sb.append(_SQL_SELECT_DLFILESHORTCUT_WHERE);

		sb.append(_FINDER_COLUMN_G_F_A_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_F_A_S_FOLDERID_2);

		sb.append(_FINDER_COLUMN_G_F_A_S_ACTIVE_2);

		sb.append(_FINDER_COLUMN_G_F_A_S_STATUS_2);

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
			sb.append(DLFileShortcutModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(folderId);

		queryPos.add(active);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						dlFileShortcut)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFileShortcut> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the document library file shortcuts that the user has permission to view where groupId = &#63; and folderId = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @param status the status
	 * @return the matching document library file shortcuts that the user has permission to view
	 */
	@Override
	public List<DLFileShortcut> filterFindByG_F_A_S(
		long groupId, long folderId, boolean active, int status) {

		return filterFindByG_F_A_S(
			groupId, folderId, active, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file shortcuts that the user has permission to view where groupId = &#63; and folderId = &#63; and active = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @param status the status
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @return the range of matching document library file shortcuts that the user has permission to view
	 */
	@Override
	public List<DLFileShortcut> filterFindByG_F_A_S(
		long groupId, long folderId, boolean active, int status, int start,
		int end) {

		return filterFindByG_F_A_S(
			groupId, folderId, active, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file shortcuts that the user has permissions to view where groupId = &#63; and folderId = &#63; and active = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @param status the status
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file shortcuts that the user has permission to view
	 */
	@Override
	public List<DLFileShortcut> filterFindByG_F_A_S(
		long groupId, long folderId, boolean active, int status, int start,
		int end, OrderByComparator<DLFileShortcut> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_F_A_S(
				groupId, folderId, active, status, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_F_A_S(
					groupId, folderId, active, status, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_DLFILESHORTCUT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DLFILESHORTCUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_F_A_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_F_A_S_FOLDERID_2);

		sb.append(_FINDER_COLUMN_G_F_A_S_ACTIVE_2_SQL);

		sb.append(_FINDER_COLUMN_G_F_A_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DLFILESHORTCUT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DLFileShortcutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DLFileShortcutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFileShortcut.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, DLFileShortcutImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, DLFileShortcutImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(folderId);

			queryPos.add(active);

			queryPos.add(status);

			return (List<DLFileShortcut>)QueryUtil.list(
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
	 * Returns the document library file shortcuts before and after the current document library file shortcut in the ordered set of document library file shortcuts that the user has permission to view where groupId = &#63; and folderId = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param fileShortcutId the primary key of the current document library file shortcut
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file shortcut
	 * @throws NoSuchFileShortcutException if a document library file shortcut with the primary key could not be found
	 */
	@Override
	public DLFileShortcut[] filterFindByG_F_A_S_PrevAndNext(
			long fileShortcutId, long groupId, long folderId, boolean active,
			int status, OrderByComparator<DLFileShortcut> orderByComparator)
		throws NoSuchFileShortcutException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_F_A_S_PrevAndNext(
				fileShortcutId, groupId, folderId, active, status,
				orderByComparator);
		}

		DLFileShortcut dlFileShortcut = findByPrimaryKey(fileShortcutId);

		Session session = null;

		try {
			session = openSession();

			DLFileShortcut[] array = new DLFileShortcutImpl[3];

			array[0] = filterGetByG_F_A_S_PrevAndNext(
				session, dlFileShortcut, groupId, folderId, active, status,
				orderByComparator, true);

			array[1] = dlFileShortcut;

			array[2] = filterGetByG_F_A_S_PrevAndNext(
				session, dlFileShortcut, groupId, folderId, active, status,
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

	protected DLFileShortcut filterGetByG_F_A_S_PrevAndNext(
		Session session, DLFileShortcut dlFileShortcut, long groupId,
		long folderId, boolean active, int status,
		OrderByComparator<DLFileShortcut> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_DLFILESHORTCUT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DLFILESHORTCUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_F_A_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_F_A_S_FOLDERID_2);

		sb.append(_FINDER_COLUMN_G_F_A_S_ACTIVE_2_SQL);

		sb.append(_FINDER_COLUMN_G_F_A_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DLFILESHORTCUT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DLFileShortcutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DLFileShortcutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFileShortcut.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DLFileShortcutImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DLFileShortcutImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(folderId);

		queryPos.add(active);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						dlFileShortcut)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLFileShortcut> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library file shortcuts where groupId = &#63; and folderId = &#63; and active = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @param status the status
	 */
	@Override
	public void removeByG_F_A_S(
		long groupId, long folderId, boolean active, int status) {

		for (DLFileShortcut dlFileShortcut :
				findByG_F_A_S(
					groupId, folderId, active, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(dlFileShortcut);
		}
	}

	/**
	 * Returns the number of document library file shortcuts where groupId = &#63; and folderId = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @param status the status
	 * @return the number of matching document library file shortcuts
	 */
	@Override
	public int countByG_F_A_S(
		long groupId, long folderId, boolean active, int status) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileShortcut.class)) {

			FinderPath finderPath = _finderPathCountByG_F_A_S;

			Object[] finderArgs = new Object[] {
				groupId, folderId, active, status
			};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_DLFILESHORTCUT_WHERE);

				sb.append(_FINDER_COLUMN_G_F_A_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_F_A_S_FOLDERID_2);

				sb.append(_FINDER_COLUMN_G_F_A_S_ACTIVE_2);

				sb.append(_FINDER_COLUMN_G_F_A_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(folderId);

					queryPos.add(active);

					queryPos.add(status);

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
	 * Returns the number of document library file shortcuts that the user has permission to view where groupId = &#63; and folderId = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param active the active
	 * @param status the status
	 * @return the number of matching document library file shortcuts that the user has permission to view
	 */
	@Override
	public int filterCountByG_F_A_S(
		long groupId, long folderId, boolean active, int status) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_F_A_S(groupId, folderId, active, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DLFileShortcut> dlFileShortcuts = findByG_F_A_S(
				groupId, folderId, active, status);

			dlFileShortcuts = InlineSQLHelperUtil.filter(
				dlFileShortcuts, groupId);

			return dlFileShortcuts.size();
		}

		StringBundler sb = new StringBundler(5);

		sb.append(_FILTER_SQL_COUNT_DLFILESHORTCUT_WHERE);

		sb.append(_FINDER_COLUMN_G_F_A_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_F_A_S_FOLDERID_2);

		sb.append(_FINDER_COLUMN_G_F_A_S_ACTIVE_2_SQL);

		sb.append(_FINDER_COLUMN_G_F_A_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DLFileShortcut.class.getName(),
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

			queryPos.add(active);

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

	private static final String _FINDER_COLUMN_G_F_A_S_GROUPID_2 =
		"dlFileShortcut.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_F_A_S_FOLDERID_2 =
		"dlFileShortcut.folderId = ? AND ";

	private static final String _FINDER_COLUMN_G_F_A_S_ACTIVE_2 =
		"dlFileShortcut.active = ? AND ";

	private static final String _FINDER_COLUMN_G_F_A_S_ACTIVE_2_SQL =
		"dlFileShortcut.active_ = ? AND ";

	private static final String _FINDER_COLUMN_G_F_A_S_STATUS_2 =
		"dlFileShortcut.status = ?";

	private FinderPath _finderPathFetchByERC_G;

	/**
	 * Returns the document library file shortcut where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchFileShortcutException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching document library file shortcut
	 * @throws NoSuchFileShortcutException if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut findByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchFileShortcutException {

		DLFileShortcut dlFileShortcut = fetchByERC_G(
			externalReferenceCode, groupId);

		if (dlFileShortcut == null) {
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

			throw new NoSuchFileShortcutException(sb.toString());
		}

		return dlFileShortcut;
	}

	/**
	 * Returns the document library file shortcut where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching document library file shortcut, or <code>null</code> if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return fetchByERC_G(externalReferenceCode, groupId, true);
	}

	/**
	 * Returns the document library file shortcut where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library file shortcut, or <code>null</code> if a matching document library file shortcut could not be found
	 */
	@Override
	public DLFileShortcut fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileShortcut.class)) {

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

			if (result instanceof DLFileShortcut) {
				DLFileShortcut dlFileShortcut = (DLFileShortcut)result;

				if (!Objects.equals(
						externalReferenceCode,
						dlFileShortcut.getExternalReferenceCode()) ||
					(groupId != dlFileShortcut.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_DLFILESHORTCUT_WHERE);

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

					List<DLFileShortcut> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							FinderCacheUtil.putResult(
								_finderPathFetchByERC_G, finderArgs, list);
						}
					}
					else {
						DLFileShortcut dlFileShortcut = list.get(0);

						result = dlFileShortcut;

						cacheResult(dlFileShortcut);
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
				return (DLFileShortcut)result;
			}
		}
	}

	/**
	 * Removes the document library file shortcut where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the document library file shortcut that was removed
	 */
	@Override
	public DLFileShortcut removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchFileShortcutException {

		DLFileShortcut dlFileShortcut = findByERC_G(
			externalReferenceCode, groupId);

		return remove(dlFileShortcut);
	}

	/**
	 * Returns the number of document library file shortcuts where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching document library file shortcuts
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		DLFileShortcut dlFileShortcut = fetchByERC_G(
			externalReferenceCode, groupId);

		if (dlFileShortcut == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_2 =
		"dlFileShortcut.externalReferenceCode = ? AND ";

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_3 =
		"(dlFileShortcut.externalReferenceCode IS NULL OR dlFileShortcut.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_G_GROUPID_2 =
		"dlFileShortcut.groupId = ?";

	public DLFileShortcutPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("active", "active_");

		setDBColumnNames(dbColumnNames);

		setModelClass(DLFileShortcut.class);

		setModelImplClass(DLFileShortcutImpl.class);
		setModelPKClass(long.class);

		setTable(DLFileShortcutTable.INSTANCE);
	}

	/**
	 * Caches the document library file shortcut in the entity cache if it is enabled.
	 *
	 * @param dlFileShortcut the document library file shortcut
	 */
	@Override
	public void cacheResult(DLFileShortcut dlFileShortcut) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					dlFileShortcut.getCtCollectionId())) {

			EntityCacheUtil.putResult(
				DLFileShortcutImpl.class, dlFileShortcut.getPrimaryKey(),
				dlFileShortcut);

			FinderCacheUtil.putResult(
				_finderPathFetchByUUID_G,
				new Object[] {
					dlFileShortcut.getUuid(), dlFileShortcut.getGroupId()
				},
				dlFileShortcut);

			FinderCacheUtil.putResult(
				_finderPathFetchByERC_G,
				new Object[] {
					dlFileShortcut.getExternalReferenceCode(),
					dlFileShortcut.getGroupId()
				},
				dlFileShortcut);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the document library file shortcuts in the entity cache if it is enabled.
	 *
	 * @param dlFileShortcuts the document library file shortcuts
	 */
	@Override
	public void cacheResult(List<DLFileShortcut> dlFileShortcuts) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (dlFileShortcuts.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (DLFileShortcut dlFileShortcut : dlFileShortcuts) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						dlFileShortcut.getCtCollectionId())) {

				if (EntityCacheUtil.getResult(
						DLFileShortcutImpl.class,
						dlFileShortcut.getPrimaryKey()) == null) {

					cacheResult(dlFileShortcut);
				}
			}
		}
	}

	/**
	 * Clears the cache for all document library file shortcuts.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		EntityCacheUtil.clearCache(DLFileShortcutImpl.class);

		FinderCacheUtil.clearCache(DLFileShortcutImpl.class);
	}

	/**
	 * Clears the cache for the document library file shortcut.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(DLFileShortcut dlFileShortcut) {
		EntityCacheUtil.removeResult(DLFileShortcutImpl.class, dlFileShortcut);
	}

	@Override
	public void clearCache(List<DLFileShortcut> dlFileShortcuts) {
		for (DLFileShortcut dlFileShortcut : dlFileShortcuts) {
			EntityCacheUtil.removeResult(
				DLFileShortcutImpl.class, dlFileShortcut);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		FinderCacheUtil.clearCache(DLFileShortcutImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			EntityCacheUtil.removeResult(DLFileShortcutImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		DLFileShortcutModelImpl dlFileShortcutModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					dlFileShortcutModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				dlFileShortcutModelImpl.getUuid(),
				dlFileShortcutModelImpl.getGroupId()
			};

			FinderCacheUtil.putResult(
				_finderPathFetchByUUID_G, args, dlFileShortcutModelImpl);

			args = new Object[] {
				dlFileShortcutModelImpl.getExternalReferenceCode(),
				dlFileShortcutModelImpl.getGroupId()
			};

			FinderCacheUtil.putResult(
				_finderPathFetchByERC_G, args, dlFileShortcutModelImpl);
		}
	}

	/**
	 * Creates a new document library file shortcut with the primary key. Does not add the document library file shortcut to the database.
	 *
	 * @param fileShortcutId the primary key for the new document library file shortcut
	 * @return the new document library file shortcut
	 */
	@Override
	public DLFileShortcut create(long fileShortcutId) {
		DLFileShortcut dlFileShortcut = new DLFileShortcutImpl();

		dlFileShortcut.setNew(true);
		dlFileShortcut.setPrimaryKey(fileShortcutId);

		String uuid = PortalUUIDUtil.generate();

		dlFileShortcut.setUuid(uuid);

		dlFileShortcut.setCompanyId(CompanyThreadLocal.getCompanyId());

		return dlFileShortcut;
	}

	/**
	 * Removes the document library file shortcut with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param fileShortcutId the primary key of the document library file shortcut
	 * @return the document library file shortcut that was removed
	 * @throws NoSuchFileShortcutException if a document library file shortcut with the primary key could not be found
	 */
	@Override
	public DLFileShortcut remove(long fileShortcutId)
		throws NoSuchFileShortcutException {

		return remove((Serializable)fileShortcutId);
	}

	/**
	 * Removes the document library file shortcut with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the document library file shortcut
	 * @return the document library file shortcut that was removed
	 * @throws NoSuchFileShortcutException if a document library file shortcut with the primary key could not be found
	 */
	@Override
	public DLFileShortcut remove(Serializable primaryKey)
		throws NoSuchFileShortcutException {

		Session session = null;

		try {
			session = openSession();

			DLFileShortcut dlFileShortcut = (DLFileShortcut)session.get(
				DLFileShortcutImpl.class, primaryKey);

			if (dlFileShortcut == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchFileShortcutException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(dlFileShortcut);
		}
		catch (NoSuchFileShortcutException noSuchEntityException) {
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
	protected DLFileShortcut removeImpl(DLFileShortcut dlFileShortcut) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(dlFileShortcut)) {
				dlFileShortcut = (DLFileShortcut)session.get(
					DLFileShortcutImpl.class,
					dlFileShortcut.getPrimaryKeyObj());
			}

			if ((dlFileShortcut != null) &&
				CTPersistenceHelperUtil.isRemove(dlFileShortcut)) {

				session.delete(dlFileShortcut);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (dlFileShortcut != null) {
			clearCache(dlFileShortcut);
		}

		return dlFileShortcut;
	}

	@Override
	public DLFileShortcut updateImpl(DLFileShortcut dlFileShortcut) {
		boolean isNew = dlFileShortcut.isNew();

		if (!(dlFileShortcut instanceof DLFileShortcutModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(dlFileShortcut.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					dlFileShortcut);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in dlFileShortcut proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DLFileShortcut implementation " +
					dlFileShortcut.getClass());
		}

		DLFileShortcutModelImpl dlFileShortcutModelImpl =
			(DLFileShortcutModelImpl)dlFileShortcut;

		if (Validator.isNull(dlFileShortcut.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			dlFileShortcut.setUuid(uuid);
		}

		if (Validator.isNull(dlFileShortcut.getExternalReferenceCode())) {
			dlFileShortcut.setExternalReferenceCode(dlFileShortcut.getUuid());
		}
		else {
			if (!Objects.equals(
					dlFileShortcutModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					dlFileShortcut.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = dlFileShortcut.getCompanyId();

					long groupId = dlFileShortcut.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = dlFileShortcut.getPrimaryKey();
					}

					try {
						dlFileShortcut.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								DLFileShortcut.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								dlFileShortcut.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			DLFileShortcut ercDLFileShortcut = fetchByERC_G(
				dlFileShortcut.getExternalReferenceCode(),
				dlFileShortcut.getGroupId());

			if (isNew) {
				if (ercDLFileShortcut != null) {
					throw new DuplicateDLFileShortcutExternalReferenceCodeException(
						"Duplicate document library file shortcut with external reference code " +
							dlFileShortcut.getExternalReferenceCode() +
								" and group " + dlFileShortcut.getGroupId());
				}
			}
			else {
				if ((ercDLFileShortcut != null) &&
					(dlFileShortcut.getFileShortcutId() !=
						ercDLFileShortcut.getFileShortcutId())) {

					throw new DuplicateDLFileShortcutExternalReferenceCodeException(
						"Duplicate document library file shortcut with external reference code " +
							dlFileShortcut.getExternalReferenceCode() +
								" and group " + dlFileShortcut.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (dlFileShortcut.getCreateDate() == null)) {
			if (serviceContext == null) {
				dlFileShortcut.setCreateDate(date);
			}
			else {
				dlFileShortcut.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!dlFileShortcutModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				dlFileShortcut.setModifiedDate(date);
			}
			else {
				dlFileShortcut.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(dlFileShortcut)) {
				if (!isNew) {
					session.evict(
						DLFileShortcutImpl.class,
						dlFileShortcut.getPrimaryKeyObj());
				}

				session.save(dlFileShortcut);
			}
			else {
				dlFileShortcut = (DLFileShortcut)session.merge(dlFileShortcut);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		EntityCacheUtil.putResult(
			DLFileShortcutImpl.class, dlFileShortcutModelImpl, false, true);

		cacheUniqueFindersCache(dlFileShortcutModelImpl);

		if (isNew) {
			dlFileShortcut.setNew(false);
		}

		dlFileShortcut.resetOriginalValues();

		return dlFileShortcut;
	}

	/**
	 * Returns the document library file shortcut with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the document library file shortcut
	 * @return the document library file shortcut
	 * @throws NoSuchFileShortcutException if a document library file shortcut with the primary key could not be found
	 */
	@Override
	public DLFileShortcut findByPrimaryKey(Serializable primaryKey)
		throws NoSuchFileShortcutException {

		DLFileShortcut dlFileShortcut = fetchByPrimaryKey(primaryKey);

		if (dlFileShortcut == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchFileShortcutException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return dlFileShortcut;
	}

	/**
	 * Returns the document library file shortcut with the primary key or throws a <code>NoSuchFileShortcutException</code> if it could not be found.
	 *
	 * @param fileShortcutId the primary key of the document library file shortcut
	 * @return the document library file shortcut
	 * @throws NoSuchFileShortcutException if a document library file shortcut with the primary key could not be found
	 */
	@Override
	public DLFileShortcut findByPrimaryKey(long fileShortcutId)
		throws NoSuchFileShortcutException {

		return findByPrimaryKey((Serializable)fileShortcutId);
	}

	/**
	 * Returns the document library file shortcut with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the document library file shortcut
	 * @return the document library file shortcut, or <code>null</code> if a document library file shortcut with the primary key could not be found
	 */
	@Override
	public DLFileShortcut fetchByPrimaryKey(Serializable primaryKey) {
		if (CTPersistenceHelperUtil.isProductionMode(
				DLFileShortcut.class, primaryKey)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		DLFileShortcut dlFileShortcut =
			(DLFileShortcut)EntityCacheUtil.getResult(
				DLFileShortcutImpl.class, primaryKey);

		if (dlFileShortcut != null) {
			return dlFileShortcut;
		}

		Session session = null;

		try {
			session = openSession();

			dlFileShortcut = (DLFileShortcut)session.get(
				DLFileShortcutImpl.class, primaryKey);

			if (dlFileShortcut != null) {
				cacheResult(dlFileShortcut);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return dlFileShortcut;
	}

	/**
	 * Returns the document library file shortcut with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param fileShortcutId the primary key of the document library file shortcut
	 * @return the document library file shortcut, or <code>null</code> if a document library file shortcut with the primary key could not be found
	 */
	@Override
	public DLFileShortcut fetchByPrimaryKey(long fileShortcutId) {
		return fetchByPrimaryKey((Serializable)fileShortcutId);
	}

	@Override
	public Map<Serializable, DLFileShortcut> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (CTPersistenceHelperUtil.isProductionMode(DLFileShortcut.class)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, DLFileShortcut> map =
			new HashMap<Serializable, DLFileShortcut>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			DLFileShortcut dlFileShortcut = fetchByPrimaryKey(primaryKey);

			if (dlFileShortcut != null) {
				map.put(primaryKey, dlFileShortcut);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
						DLFileShortcut.class, primaryKey)) {

				DLFileShortcut dlFileShortcut =
					(DLFileShortcut)EntityCacheUtil.getResult(
						DLFileShortcutImpl.class, primaryKey);

				if (dlFileShortcut == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, dlFileShortcut);
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

			for (DLFileShortcut dlFileShortcut :
					(List<DLFileShortcut>)query.list()) {

				map.put(dlFileShortcut.getPrimaryKeyObj(), dlFileShortcut);

				cacheResult(dlFileShortcut);
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
	 * Returns all the document library file shortcuts.
	 *
	 * @return the document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library file shortcuts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @return the range of document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library file shortcuts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findAll(
		int start, int end,
		OrderByComparator<DLFileShortcut> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library file shortcuts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileShortcutModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of document library file shortcuts
	 * @param end the upper bound of the range of document library file shortcuts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of document library file shortcuts
	 */
	@Override
	public List<DLFileShortcut> findAll(
		int start, int end, OrderByComparator<DLFileShortcut> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileShortcut.class)) {

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

			List<DLFileShortcut> list = null;

			if (useFinderCache) {
				list = (List<DLFileShortcut>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_DLFILESHORTCUT);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_DLFILESHORTCUT;

					sql = sql.concat(DLFileShortcutModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<DLFileShortcut>)QueryUtil.list(
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
	 * Removes all the document library file shortcuts from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (DLFileShortcut dlFileShortcut : findAll()) {
			remove(dlFileShortcut);
		}
	}

	/**
	 * Returns the number of document library file shortcuts.
	 *
	 * @return the number of document library file shortcuts
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					DLFileShortcut.class)) {

			Long count = (Long)FinderCacheUtil.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(
						_SQL_COUNT_DLFILESHORTCUT);

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
		return "fileShortcutId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DLFILESHORTCUT;
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
		return DLFileShortcutModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DLFileShortcut";
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
		ctMergeColumnNames.add("repositoryId");
		ctMergeColumnNames.add("folderId");
		ctMergeColumnNames.add("toFileEntryId");
		ctMergeColumnNames.add("treePath");
		ctMergeColumnNames.add("active_");
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
			CTColumnResolutionType.PK, Collections.singleton("fileShortcutId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the document library file shortcut persistence.
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

		_finderPathWithPaginationFindByToFileEntryId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByToFileEntryId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"toFileEntryId"}, true);

		_finderPathWithoutPaginationFindByToFileEntryId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByToFileEntryId",
			new String[] {Long.class.getName()}, new String[] {"toFileEntryId"},
			true);

		_finderPathCountByToFileEntryId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByToFileEntryId",
			new String[] {Long.class.getName()}, new String[] {"toFileEntryId"},
			false);

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

		_finderPathWithPaginationFindByG_F_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_F_A",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Boolean.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "folderId", "active_"}, true);

		_finderPathWithoutPaginationFindByG_F_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_F_A",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Boolean.class.getName()
			},
			new String[] {"groupId", "folderId", "active_"}, true);

		_finderPathCountByG_F_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_F_A",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Boolean.class.getName()
			},
			new String[] {"groupId", "folderId", "active_"}, false);

		_finderPathWithPaginationFindByG_F_A_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_F_A_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Boolean.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "folderId", "active_", "status"}, true);

		_finderPathWithoutPaginationFindByG_F_A_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_F_A_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Boolean.class.getName(), Integer.class.getName()
			},
			new String[] {"groupId", "folderId", "active_", "status"}, true);

		_finderPathCountByG_F_A_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_F_A_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Boolean.class.getName(), Integer.class.getName()
			},
			new String[] {"groupId", "folderId", "active_", "status"}, false);

		_finderPathFetchByERC_G = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"externalReferenceCode", "groupId"}, true);

		DLFileShortcutUtil.setPersistence(this);
	}

	public void destroy() {
		DLFileShortcutUtil.setPersistence(null);

		EntityCacheUtil.removeCache(DLFileShortcutImpl.class.getName());
	}

	private static final String _SQL_SELECT_DLFILESHORTCUT =
		"SELECT dlFileShortcut FROM DLFileShortcut dlFileShortcut";

	private static final String _SQL_SELECT_DLFILESHORTCUT_WHERE =
		"SELECT dlFileShortcut FROM DLFileShortcut dlFileShortcut WHERE ";

	private static final String _SQL_COUNT_DLFILESHORTCUT =
		"SELECT COUNT(dlFileShortcut) FROM DLFileShortcut dlFileShortcut";

	private static final String _SQL_COUNT_DLFILESHORTCUT_WHERE =
		"SELECT COUNT(dlFileShortcut) FROM DLFileShortcut dlFileShortcut WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"dlFileShortcut.fileShortcutId";

	private static final String _FILTER_SQL_SELECT_DLFILESHORTCUT_WHERE =
		"SELECT DISTINCT {dlFileShortcut.*} FROM DLFileShortcut dlFileShortcut WHERE ";

	private static final String
		_FILTER_SQL_SELECT_DLFILESHORTCUT_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {DLFileShortcut.*} FROM (SELECT DISTINCT dlFileShortcut.fileShortcutId FROM DLFileShortcut dlFileShortcut WHERE ";

	private static final String
		_FILTER_SQL_SELECT_DLFILESHORTCUT_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN DLFileShortcut ON TEMP_TABLE.fileShortcutId = DLFileShortcut.fileShortcutId";

	private static final String _FILTER_SQL_COUNT_DLFILESHORTCUT_WHERE =
		"SELECT COUNT(DISTINCT dlFileShortcut.fileShortcutId) AS COUNT_VALUE FROM DLFileShortcut dlFileShortcut WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "dlFileShortcut";

	private static final String _FILTER_ENTITY_TABLE = "DLFileShortcut";

	private static final String _ORDER_BY_ENTITY_ALIAS = "dlFileShortcut.";

	private static final String _ORDER_BY_ENTITY_TABLE = "DLFileShortcut.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No DLFileShortcut exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No DLFileShortcut exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		DLFileShortcutPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "active"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}