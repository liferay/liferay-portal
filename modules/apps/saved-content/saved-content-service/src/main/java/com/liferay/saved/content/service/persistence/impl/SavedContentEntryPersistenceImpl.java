/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saved.content.service.persistence.impl;

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
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.saved.content.exception.NoSuchSavedContentEntryException;
import com.liferay.saved.content.model.SavedContentEntry;
import com.liferay.saved.content.model.SavedContentEntryTable;
import com.liferay.saved.content.model.impl.SavedContentEntryImpl;
import com.liferay.saved.content.model.impl.SavedContentEntryModelImpl;
import com.liferay.saved.content.service.persistence.SavedContentEntryPersistence;
import com.liferay.saved.content.service.persistence.SavedContentEntryUtil;
import com.liferay.saved.content.service.persistence.impl.constants.SavedContentEntryPersistenceConstants;

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
 * The persistence implementation for the saved content entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = SavedContentEntryPersistence.class)
public class SavedContentEntryPersistenceImpl
	extends BasePersistenceImpl<SavedContentEntry>
	implements SavedContentEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>SavedContentEntryUtil</code> to access the saved content entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		SavedContentEntryImpl.class.getName();

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
	 * Returns all the saved content entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the saved content entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @return the range of matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the saved content entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<SavedContentEntry> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the saved content entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<SavedContentEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SavedContentEntry.class)) {

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

			List<SavedContentEntry> list = null;

			if (useFinderCache) {
				list = (List<SavedContentEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (SavedContentEntry savedContentEntry : list) {
						if (!uuid.equals(savedContentEntry.getUuid())) {
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

				sb.append(_SQL_SELECT_SAVEDCONTENTENTRY_WHERE);

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
					sb.append(SavedContentEntryModelImpl.ORDER_BY_JPQL);
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

					list = (List<SavedContentEntry>)QueryUtil.list(
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
	 * Returns the first saved content entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saved content entry
	 * @throws NoSuchSavedContentEntryException if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry findByUuid_First(
			String uuid, OrderByComparator<SavedContentEntry> orderByComparator)
		throws NoSuchSavedContentEntryException {

		SavedContentEntry savedContentEntry = fetchByUuid_First(
			uuid, orderByComparator);

		if (savedContentEntry != null) {
			return savedContentEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchSavedContentEntryException(sb.toString());
	}

	/**
	 * Returns the first saved content entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saved content entry, or <code>null</code> if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry fetchByUuid_First(
		String uuid, OrderByComparator<SavedContentEntry> orderByComparator) {

		List<SavedContentEntry> list = findByUuid(
			uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last saved content entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching saved content entry
	 * @throws NoSuchSavedContentEntryException if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry findByUuid_Last(
			String uuid, OrderByComparator<SavedContentEntry> orderByComparator)
		throws NoSuchSavedContentEntryException {

		SavedContentEntry savedContentEntry = fetchByUuid_Last(
			uuid, orderByComparator);

		if (savedContentEntry != null) {
			return savedContentEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchSavedContentEntryException(sb.toString());
	}

	/**
	 * Returns the last saved content entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching saved content entry, or <code>null</code> if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry fetchByUuid_Last(
		String uuid, OrderByComparator<SavedContentEntry> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<SavedContentEntry> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the saved content entries before and after the current saved content entry in the ordered set where uuid = &#63;.
	 *
	 * @param savedContentEntryId the primary key of the current saved content entry
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next saved content entry
	 * @throws NoSuchSavedContentEntryException if a saved content entry with the primary key could not be found
	 */
	@Override
	public SavedContentEntry[] findByUuid_PrevAndNext(
			long savedContentEntryId, String uuid,
			OrderByComparator<SavedContentEntry> orderByComparator)
		throws NoSuchSavedContentEntryException {

		uuid = Objects.toString(uuid, "");

		SavedContentEntry savedContentEntry = findByPrimaryKey(
			savedContentEntryId);

		Session session = null;

		try {
			session = openSession();

			SavedContentEntry[] array = new SavedContentEntryImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, savedContentEntry, uuid, orderByComparator, true);

			array[1] = savedContentEntry;

			array[2] = getByUuid_PrevAndNext(
				session, savedContentEntry, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected SavedContentEntry getByUuid_PrevAndNext(
		Session session, SavedContentEntry savedContentEntry, String uuid,
		OrderByComparator<SavedContentEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_SAVEDCONTENTENTRY_WHERE);

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
			sb.append(SavedContentEntryModelImpl.ORDER_BY_JPQL);
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
						savedContentEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<SavedContentEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the saved content entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (SavedContentEntry savedContentEntry :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(savedContentEntry);
		}
	}

	/**
	 * Returns the number of saved content entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching saved content entries
	 */
	@Override
	public int countByUuid(String uuid) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SavedContentEntry.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid;

			Object[] finderArgs = new Object[] {uuid};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_SAVEDCONTENTENTRY_WHERE);

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
		"savedContentEntry.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(savedContentEntry.uuid IS NULL OR savedContentEntry.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;

	/**
	 * Returns the saved content entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchSavedContentEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching saved content entry
	 * @throws NoSuchSavedContentEntryException if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchSavedContentEntryException {

		SavedContentEntry savedContentEntry = fetchByUUID_G(uuid, groupId);

		if (savedContentEntry == null) {
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

			throw new NoSuchSavedContentEntryException(sb.toString());
		}

		return savedContentEntry;
	}

	/**
	 * Returns the saved content entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching saved content entry, or <code>null</code> if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the saved content entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching saved content entry, or <code>null</code> if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SavedContentEntry.class)) {

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

			if (result instanceof SavedContentEntry) {
				SavedContentEntry savedContentEntry = (SavedContentEntry)result;

				if (!Objects.equals(uuid, savedContentEntry.getUuid()) ||
					(groupId != savedContentEntry.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_SAVEDCONTENTENTRY_WHERE);

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

					List<SavedContentEntry> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByUUID_G, finderArgs, list);
						}
					}
					else {
						SavedContentEntry savedContentEntry = list.get(0);

						result = savedContentEntry;

						cacheResult(savedContentEntry);
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
				return (SavedContentEntry)result;
			}
		}
	}

	/**
	 * Removes the saved content entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the saved content entry that was removed
	 */
	@Override
	public SavedContentEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchSavedContentEntryException {

		SavedContentEntry savedContentEntry = findByUUID_G(uuid, groupId);

		return remove(savedContentEntry);
	}

	/**
	 * Returns the number of saved content entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching saved content entries
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		SavedContentEntry savedContentEntry = fetchByUUID_G(uuid, groupId);

		if (savedContentEntry == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"savedContentEntry.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(savedContentEntry.uuid IS NULL OR savedContentEntry.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"savedContentEntry.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the saved content entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the saved content entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @return the range of matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the saved content entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<SavedContentEntry> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the saved content entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<SavedContentEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SavedContentEntry.class)) {

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

			List<SavedContentEntry> list = null;

			if (useFinderCache) {
				list = (List<SavedContentEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (SavedContentEntry savedContentEntry : list) {
						if (!uuid.equals(savedContentEntry.getUuid()) ||
							(companyId != savedContentEntry.getCompanyId())) {

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

				sb.append(_SQL_SELECT_SAVEDCONTENTENTRY_WHERE);

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
					sb.append(SavedContentEntryModelImpl.ORDER_BY_JPQL);
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

					list = (List<SavedContentEntry>)QueryUtil.list(
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
	 * Returns the first saved content entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saved content entry
	 * @throws NoSuchSavedContentEntryException if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<SavedContentEntry> orderByComparator)
		throws NoSuchSavedContentEntryException {

		SavedContentEntry savedContentEntry = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (savedContentEntry != null) {
			return savedContentEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchSavedContentEntryException(sb.toString());
	}

	/**
	 * Returns the first saved content entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saved content entry, or <code>null</code> if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<SavedContentEntry> orderByComparator) {

		List<SavedContentEntry> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last saved content entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching saved content entry
	 * @throws NoSuchSavedContentEntryException if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<SavedContentEntry> orderByComparator)
		throws NoSuchSavedContentEntryException {

		SavedContentEntry savedContentEntry = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (savedContentEntry != null) {
			return savedContentEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchSavedContentEntryException(sb.toString());
	}

	/**
	 * Returns the last saved content entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching saved content entry, or <code>null</code> if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<SavedContentEntry> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<SavedContentEntry> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the saved content entries before and after the current saved content entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param savedContentEntryId the primary key of the current saved content entry
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next saved content entry
	 * @throws NoSuchSavedContentEntryException if a saved content entry with the primary key could not be found
	 */
	@Override
	public SavedContentEntry[] findByUuid_C_PrevAndNext(
			long savedContentEntryId, String uuid, long companyId,
			OrderByComparator<SavedContentEntry> orderByComparator)
		throws NoSuchSavedContentEntryException {

		uuid = Objects.toString(uuid, "");

		SavedContentEntry savedContentEntry = findByPrimaryKey(
			savedContentEntryId);

		Session session = null;

		try {
			session = openSession();

			SavedContentEntry[] array = new SavedContentEntryImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, savedContentEntry, uuid, companyId, orderByComparator,
				true);

			array[1] = savedContentEntry;

			array[2] = getByUuid_C_PrevAndNext(
				session, savedContentEntry, uuid, companyId, orderByComparator,
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

	protected SavedContentEntry getByUuid_C_PrevAndNext(
		Session session, SavedContentEntry savedContentEntry, String uuid,
		long companyId, OrderByComparator<SavedContentEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_SAVEDCONTENTENTRY_WHERE);

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
			sb.append(SavedContentEntryModelImpl.ORDER_BY_JPQL);
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
						savedContentEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<SavedContentEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the saved content entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (SavedContentEntry savedContentEntry :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(savedContentEntry);
		}
	}

	/**
	 * Returns the number of saved content entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching saved content entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SavedContentEntry.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid_C;

			Object[] finderArgs = new Object[] {uuid, companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_SAVEDCONTENTENTRY_WHERE);

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
		"savedContentEntry.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(savedContentEntry.uuid IS NULL OR savedContentEntry.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"savedContentEntry.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByGroupId;
	private FinderPath _finderPathWithoutPaginationFindByGroupId;
	private FinderPath _finderPathCountByGroupId;

	/**
	 * Returns all the saved content entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the saved content entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @return the range of matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByGroupId(
		long groupId, int start, int end) {

		return findByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the saved content entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<SavedContentEntry> orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the saved content entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<SavedContentEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SavedContentEntry.class)) {

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

			List<SavedContentEntry> list = null;

			if (useFinderCache) {
				list = (List<SavedContentEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (SavedContentEntry savedContentEntry : list) {
						if (groupId != savedContentEntry.getGroupId()) {
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

				sb.append(_SQL_SELECT_SAVEDCONTENTENTRY_WHERE);

				sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(SavedContentEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					list = (List<SavedContentEntry>)QueryUtil.list(
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
	 * Returns the first saved content entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saved content entry
	 * @throws NoSuchSavedContentEntryException if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry findByGroupId_First(
			long groupId,
			OrderByComparator<SavedContentEntry> orderByComparator)
		throws NoSuchSavedContentEntryException {

		SavedContentEntry savedContentEntry = fetchByGroupId_First(
			groupId, orderByComparator);

		if (savedContentEntry != null) {
			return savedContentEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchSavedContentEntryException(sb.toString());
	}

	/**
	 * Returns the first saved content entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saved content entry, or <code>null</code> if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry fetchByGroupId_First(
		long groupId, OrderByComparator<SavedContentEntry> orderByComparator) {

		List<SavedContentEntry> list = findByGroupId(
			groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last saved content entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching saved content entry
	 * @throws NoSuchSavedContentEntryException if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry findByGroupId_Last(
			long groupId,
			OrderByComparator<SavedContentEntry> orderByComparator)
		throws NoSuchSavedContentEntryException {

		SavedContentEntry savedContentEntry = fetchByGroupId_Last(
			groupId, orderByComparator);

		if (savedContentEntry != null) {
			return savedContentEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchSavedContentEntryException(sb.toString());
	}

	/**
	 * Returns the last saved content entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching saved content entry, or <code>null</code> if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry fetchByGroupId_Last(
		long groupId, OrderByComparator<SavedContentEntry> orderByComparator) {

		int count = countByGroupId(groupId);

		if (count == 0) {
			return null;
		}

		List<SavedContentEntry> list = findByGroupId(
			groupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the saved content entries before and after the current saved content entry in the ordered set where groupId = &#63;.
	 *
	 * @param savedContentEntryId the primary key of the current saved content entry
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next saved content entry
	 * @throws NoSuchSavedContentEntryException if a saved content entry with the primary key could not be found
	 */
	@Override
	public SavedContentEntry[] findByGroupId_PrevAndNext(
			long savedContentEntryId, long groupId,
			OrderByComparator<SavedContentEntry> orderByComparator)
		throws NoSuchSavedContentEntryException {

		SavedContentEntry savedContentEntry = findByPrimaryKey(
			savedContentEntryId);

		Session session = null;

		try {
			session = openSession();

			SavedContentEntry[] array = new SavedContentEntryImpl[3];

			array[0] = getByGroupId_PrevAndNext(
				session, savedContentEntry, groupId, orderByComparator, true);

			array[1] = savedContentEntry;

			array[2] = getByGroupId_PrevAndNext(
				session, savedContentEntry, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected SavedContentEntry getByGroupId_PrevAndNext(
		Session session, SavedContentEntry savedContentEntry, long groupId,
		OrderByComparator<SavedContentEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_SAVEDCONTENTENTRY_WHERE);

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
			sb.append(SavedContentEntryModelImpl.ORDER_BY_JPQL);
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
						savedContentEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<SavedContentEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the saved content entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching saved content entries that the user has permission to view
	 */
	@Override
	public List<SavedContentEntry> filterFindByGroupId(long groupId) {
		return filterFindByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the saved content entries that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @return the range of matching saved content entries that the user has permission to view
	 */
	@Override
	public List<SavedContentEntry> filterFindByGroupId(
		long groupId, int start, int end) {

		return filterFindByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the saved content entries that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching saved content entries that the user has permission to view
	 */
	@Override
	public List<SavedContentEntry> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<SavedContentEntry> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_SAVEDCONTENTENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_SAVEDCONTENTENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_SAVEDCONTENTENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					SavedContentEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(SavedContentEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), SavedContentEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, SavedContentEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, SavedContentEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			return (List<SavedContentEntry>)QueryUtil.list(
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
	 * Returns the saved content entries before and after the current saved content entry in the ordered set of saved content entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param savedContentEntryId the primary key of the current saved content entry
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next saved content entry
	 * @throws NoSuchSavedContentEntryException if a saved content entry with the primary key could not be found
	 */
	@Override
	public SavedContentEntry[] filterFindByGroupId_PrevAndNext(
			long savedContentEntryId, long groupId,
			OrderByComparator<SavedContentEntry> orderByComparator)
		throws NoSuchSavedContentEntryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByGroupId_PrevAndNext(
				savedContentEntryId, groupId, orderByComparator);
		}

		SavedContentEntry savedContentEntry = findByPrimaryKey(
			savedContentEntryId);

		Session session = null;

		try {
			session = openSession();

			SavedContentEntry[] array = new SavedContentEntryImpl[3];

			array[0] = filterGetByGroupId_PrevAndNext(
				session, savedContentEntry, groupId, orderByComparator, true);

			array[1] = savedContentEntry;

			array[2] = filterGetByGroupId_PrevAndNext(
				session, savedContentEntry, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected SavedContentEntry filterGetByGroupId_PrevAndNext(
		Session session, SavedContentEntry savedContentEntry, long groupId,
		OrderByComparator<SavedContentEntry> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_SAVEDCONTENTENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_SAVEDCONTENTENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_SAVEDCONTENTENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					SavedContentEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(SavedContentEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), SavedContentEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, SavedContentEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, SavedContentEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						savedContentEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<SavedContentEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the saved content entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		for (SavedContentEntry savedContentEntry :
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(savedContentEntry);
		}
	}

	/**
	 * Returns the number of saved content entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching saved content entries
	 */
	@Override
	public int countByGroupId(long groupId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SavedContentEntry.class)) {

			FinderPath finderPath = _finderPathCountByGroupId;

			Object[] finderArgs = new Object[] {groupId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_SAVEDCONTENTENTRY_WHERE);

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
	 * Returns the number of saved content entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching saved content entries that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByGroupId(groupId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<SavedContentEntry> savedContentEntries = findByGroupId(
				groupId);

			savedContentEntries = InlineSQLHelperUtil.filter(
				savedContentEntries, groupId);

			return savedContentEntries.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_SAVEDCONTENTENTRY_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), SavedContentEntry.class.getName(),
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
		"savedContentEntry.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUserId;
	private FinderPath _finderPathWithoutPaginationFindByUserId;
	private FinderPath _finderPathCountByUserId;

	/**
	 * Returns all the saved content entries where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByUserId(long userId) {
		return findByUserId(userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the saved content entries where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @return the range of matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByUserId(
		long userId, int start, int end) {

		return findByUserId(userId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the saved content entries where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByUserId(
		long userId, int start, int end,
		OrderByComparator<SavedContentEntry> orderByComparator) {

		return findByUserId(userId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the saved content entries where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByUserId(
		long userId, int start, int end,
		OrderByComparator<SavedContentEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SavedContentEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByUserId;
					finderArgs = new Object[] {userId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByUserId;
				finderArgs = new Object[] {
					userId, start, end, orderByComparator
				};
			}

			List<SavedContentEntry> list = null;

			if (useFinderCache) {
				list = (List<SavedContentEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (SavedContentEntry savedContentEntry : list) {
						if (userId != savedContentEntry.getUserId()) {
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

				sb.append(_SQL_SELECT_SAVEDCONTENTENTRY_WHERE);

				sb.append(_FINDER_COLUMN_USERID_USERID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(SavedContentEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(userId);

					list = (List<SavedContentEntry>)QueryUtil.list(
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
	 * Returns the first saved content entry in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saved content entry
	 * @throws NoSuchSavedContentEntryException if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry findByUserId_First(
			long userId, OrderByComparator<SavedContentEntry> orderByComparator)
		throws NoSuchSavedContentEntryException {

		SavedContentEntry savedContentEntry = fetchByUserId_First(
			userId, orderByComparator);

		if (savedContentEntry != null) {
			return savedContentEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append("}");

		throw new NoSuchSavedContentEntryException(sb.toString());
	}

	/**
	 * Returns the first saved content entry in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saved content entry, or <code>null</code> if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry fetchByUserId_First(
		long userId, OrderByComparator<SavedContentEntry> orderByComparator) {

		List<SavedContentEntry> list = findByUserId(
			userId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last saved content entry in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching saved content entry
	 * @throws NoSuchSavedContentEntryException if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry findByUserId_Last(
			long userId, OrderByComparator<SavedContentEntry> orderByComparator)
		throws NoSuchSavedContentEntryException {

		SavedContentEntry savedContentEntry = fetchByUserId_Last(
			userId, orderByComparator);

		if (savedContentEntry != null) {
			return savedContentEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append("}");

		throw new NoSuchSavedContentEntryException(sb.toString());
	}

	/**
	 * Returns the last saved content entry in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching saved content entry, or <code>null</code> if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry fetchByUserId_Last(
		long userId, OrderByComparator<SavedContentEntry> orderByComparator) {

		int count = countByUserId(userId);

		if (count == 0) {
			return null;
		}

		List<SavedContentEntry> list = findByUserId(
			userId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the saved content entries before and after the current saved content entry in the ordered set where userId = &#63;.
	 *
	 * @param savedContentEntryId the primary key of the current saved content entry
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next saved content entry
	 * @throws NoSuchSavedContentEntryException if a saved content entry with the primary key could not be found
	 */
	@Override
	public SavedContentEntry[] findByUserId_PrevAndNext(
			long savedContentEntryId, long userId,
			OrderByComparator<SavedContentEntry> orderByComparator)
		throws NoSuchSavedContentEntryException {

		SavedContentEntry savedContentEntry = findByPrimaryKey(
			savedContentEntryId);

		Session session = null;

		try {
			session = openSession();

			SavedContentEntry[] array = new SavedContentEntryImpl[3];

			array[0] = getByUserId_PrevAndNext(
				session, savedContentEntry, userId, orderByComparator, true);

			array[1] = savedContentEntry;

			array[2] = getByUserId_PrevAndNext(
				session, savedContentEntry, userId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected SavedContentEntry getByUserId_PrevAndNext(
		Session session, SavedContentEntry savedContentEntry, long userId,
		OrderByComparator<SavedContentEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_SAVEDCONTENTENTRY_WHERE);

		sb.append(_FINDER_COLUMN_USERID_USERID_2);

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
			sb.append(SavedContentEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(userId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						savedContentEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<SavedContentEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the saved content entries where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		for (SavedContentEntry savedContentEntry :
				findByUserId(
					userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(savedContentEntry);
		}
	}

	/**
	 * Returns the number of saved content entries where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching saved content entries
	 */
	@Override
	public int countByUserId(long userId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SavedContentEntry.class)) {

			FinderPath finderPath = _finderPathCountByUserId;

			Object[] finderArgs = new Object[] {userId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_SAVEDCONTENTENTRY_WHERE);

				sb.append(_FINDER_COLUMN_USERID_USERID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

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

	private static final String _FINDER_COLUMN_USERID_USERID_2 =
		"savedContentEntry.userId = ?";

	private FinderPath _finderPathWithPaginationFindByG_U;
	private FinderPath _finderPathWithoutPaginationFindByG_U;
	private FinderPath _finderPathCountByG_U;

	/**
	 * Returns all the saved content entries where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @return the matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByG_U(long groupId, long userId) {
		return findByG_U(
			groupId, userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the saved content entries where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @return the range of matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByG_U(
		long groupId, long userId, int start, int end) {

		return findByG_U(groupId, userId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the saved content entries where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByG_U(
		long groupId, long userId, int start, int end,
		OrderByComparator<SavedContentEntry> orderByComparator) {

		return findByG_U(groupId, userId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the saved content entries where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByG_U(
		long groupId, long userId, int start, int end,
		OrderByComparator<SavedContentEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SavedContentEntry.class)) {

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

			List<SavedContentEntry> list = null;

			if (useFinderCache) {
				list = (List<SavedContentEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (SavedContentEntry savedContentEntry : list) {
						if ((groupId != savedContentEntry.getGroupId()) ||
							(userId != savedContentEntry.getUserId())) {

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

				sb.append(_SQL_SELECT_SAVEDCONTENTENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_U_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_U_USERID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(SavedContentEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(userId);

					list = (List<SavedContentEntry>)QueryUtil.list(
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
	 * Returns the first saved content entry in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saved content entry
	 * @throws NoSuchSavedContentEntryException if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry findByG_U_First(
			long groupId, long userId,
			OrderByComparator<SavedContentEntry> orderByComparator)
		throws NoSuchSavedContentEntryException {

		SavedContentEntry savedContentEntry = fetchByG_U_First(
			groupId, userId, orderByComparator);

		if (savedContentEntry != null) {
			return savedContentEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append("}");

		throw new NoSuchSavedContentEntryException(sb.toString());
	}

	/**
	 * Returns the first saved content entry in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saved content entry, or <code>null</code> if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry fetchByG_U_First(
		long groupId, long userId,
		OrderByComparator<SavedContentEntry> orderByComparator) {

		List<SavedContentEntry> list = findByG_U(
			groupId, userId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last saved content entry in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching saved content entry
	 * @throws NoSuchSavedContentEntryException if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry findByG_U_Last(
			long groupId, long userId,
			OrderByComparator<SavedContentEntry> orderByComparator)
		throws NoSuchSavedContentEntryException {

		SavedContentEntry savedContentEntry = fetchByG_U_Last(
			groupId, userId, orderByComparator);

		if (savedContentEntry != null) {
			return savedContentEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append("}");

		throw new NoSuchSavedContentEntryException(sb.toString());
	}

	/**
	 * Returns the last saved content entry in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching saved content entry, or <code>null</code> if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry fetchByG_U_Last(
		long groupId, long userId,
		OrderByComparator<SavedContentEntry> orderByComparator) {

		int count = countByG_U(groupId, userId);

		if (count == 0) {
			return null;
		}

		List<SavedContentEntry> list = findByG_U(
			groupId, userId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the saved content entries before and after the current saved content entry in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param savedContentEntryId the primary key of the current saved content entry
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next saved content entry
	 * @throws NoSuchSavedContentEntryException if a saved content entry with the primary key could not be found
	 */
	@Override
	public SavedContentEntry[] findByG_U_PrevAndNext(
			long savedContentEntryId, long groupId, long userId,
			OrderByComparator<SavedContentEntry> orderByComparator)
		throws NoSuchSavedContentEntryException {

		SavedContentEntry savedContentEntry = findByPrimaryKey(
			savedContentEntryId);

		Session session = null;

		try {
			session = openSession();

			SavedContentEntry[] array = new SavedContentEntryImpl[3];

			array[0] = getByG_U_PrevAndNext(
				session, savedContentEntry, groupId, userId, orderByComparator,
				true);

			array[1] = savedContentEntry;

			array[2] = getByG_U_PrevAndNext(
				session, savedContentEntry, groupId, userId, orderByComparator,
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

	protected SavedContentEntry getByG_U_PrevAndNext(
		Session session, SavedContentEntry savedContentEntry, long groupId,
		long userId, OrderByComparator<SavedContentEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_SAVEDCONTENTENTRY_WHERE);

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
			sb.append(SavedContentEntryModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(
						savedContentEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<SavedContentEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the saved content entries that the user has permission to view where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @return the matching saved content entries that the user has permission to view
	 */
	@Override
	public List<SavedContentEntry> filterFindByG_U(long groupId, long userId) {
		return filterFindByG_U(
			groupId, userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the saved content entries that the user has permission to view where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @return the range of matching saved content entries that the user has permission to view
	 */
	@Override
	public List<SavedContentEntry> filterFindByG_U(
		long groupId, long userId, int start, int end) {

		return filterFindByG_U(groupId, userId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the saved content entries that the user has permissions to view where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching saved content entries that the user has permission to view
	 */
	@Override
	public List<SavedContentEntry> filterFindByG_U(
		long groupId, long userId, int start, int end,
		OrderByComparator<SavedContentEntry> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_SAVEDCONTENTENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_SAVEDCONTENTENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_USERID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_SAVEDCONTENTENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					SavedContentEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(SavedContentEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), SavedContentEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, SavedContentEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, SavedContentEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(userId);

			return (List<SavedContentEntry>)QueryUtil.list(
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
	 * Returns the saved content entries before and after the current saved content entry in the ordered set of saved content entries that the user has permission to view where groupId = &#63; and userId = &#63;.
	 *
	 * @param savedContentEntryId the primary key of the current saved content entry
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next saved content entry
	 * @throws NoSuchSavedContentEntryException if a saved content entry with the primary key could not be found
	 */
	@Override
	public SavedContentEntry[] filterFindByG_U_PrevAndNext(
			long savedContentEntryId, long groupId, long userId,
			OrderByComparator<SavedContentEntry> orderByComparator)
		throws NoSuchSavedContentEntryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_U_PrevAndNext(
				savedContentEntryId, groupId, userId, orderByComparator);
		}

		SavedContentEntry savedContentEntry = findByPrimaryKey(
			savedContentEntryId);

		Session session = null;

		try {
			session = openSession();

			SavedContentEntry[] array = new SavedContentEntryImpl[3];

			array[0] = filterGetByG_U_PrevAndNext(
				session, savedContentEntry, groupId, userId, orderByComparator,
				true);

			array[1] = savedContentEntry;

			array[2] = filterGetByG_U_PrevAndNext(
				session, savedContentEntry, groupId, userId, orderByComparator,
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

	protected SavedContentEntry filterGetByG_U_PrevAndNext(
		Session session, SavedContentEntry savedContentEntry, long groupId,
		long userId, OrderByComparator<SavedContentEntry> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_SAVEDCONTENTENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_SAVEDCONTENTENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_USERID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_SAVEDCONTENTENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					SavedContentEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(SavedContentEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), SavedContentEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, SavedContentEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, SavedContentEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(userId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						savedContentEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<SavedContentEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the saved content entries where groupId = &#63; and userId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 */
	@Override
	public void removeByG_U(long groupId, long userId) {
		for (SavedContentEntry savedContentEntry :
				findByG_U(
					groupId, userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(savedContentEntry);
		}
	}

	/**
	 * Returns the number of saved content entries where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @return the number of matching saved content entries
	 */
	@Override
	public int countByG_U(long groupId, long userId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SavedContentEntry.class)) {

			FinderPath finderPath = _finderPathCountByG_U;

			Object[] finderArgs = new Object[] {groupId, userId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_SAVEDCONTENTENTRY_WHERE);

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
	 * Returns the number of saved content entries that the user has permission to view where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @return the number of matching saved content entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_U(long groupId, long userId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_U(groupId, userId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<SavedContentEntry> savedContentEntries = findByG_U(
				groupId, userId);

			savedContentEntries = InlineSQLHelperUtil.filter(
				savedContentEntries, groupId);

			return savedContentEntries.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_SAVEDCONTENTENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_U_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_USERID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), SavedContentEntry.class.getName(),
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
		"savedContentEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_U_USERID_2 =
		"savedContentEntry.userId = ?";

	private FinderPath _finderPathWithPaginationFindByG_CN;
	private FinderPath _finderPathWithoutPaginationFindByG_CN;
	private FinderPath _finderPathCountByG_CN;

	/**
	 * Returns all the saved content entries where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByG_CN(long groupId, long classNameId) {
		return findByG_CN(
			groupId, classNameId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the saved content entries where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @return the range of matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByG_CN(
		long groupId, long classNameId, int start, int end) {

		return findByG_CN(groupId, classNameId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the saved content entries where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByG_CN(
		long groupId, long classNameId, int start, int end,
		OrderByComparator<SavedContentEntry> orderByComparator) {

		return findByG_CN(
			groupId, classNameId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the saved content entries where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByG_CN(
		long groupId, long classNameId, int start, int end,
		OrderByComparator<SavedContentEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SavedContentEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_CN;
					finderArgs = new Object[] {groupId, classNameId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_CN;
				finderArgs = new Object[] {
					groupId, classNameId, start, end, orderByComparator
				};
			}

			List<SavedContentEntry> list = null;

			if (useFinderCache) {
				list = (List<SavedContentEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (SavedContentEntry savedContentEntry : list) {
						if ((groupId != savedContentEntry.getGroupId()) ||
							(classNameId !=
								savedContentEntry.getClassNameId())) {

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

				sb.append(_SQL_SELECT_SAVEDCONTENTENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_CN_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_CN_CLASSNAMEID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(SavedContentEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(classNameId);

					list = (List<SavedContentEntry>)QueryUtil.list(
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
	 * Returns the first saved content entry in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saved content entry
	 * @throws NoSuchSavedContentEntryException if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry findByG_CN_First(
			long groupId, long classNameId,
			OrderByComparator<SavedContentEntry> orderByComparator)
		throws NoSuchSavedContentEntryException {

		SavedContentEntry savedContentEntry = fetchByG_CN_First(
			groupId, classNameId, orderByComparator);

		if (savedContentEntry != null) {
			return savedContentEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append("}");

		throw new NoSuchSavedContentEntryException(sb.toString());
	}

	/**
	 * Returns the first saved content entry in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saved content entry, or <code>null</code> if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry fetchByG_CN_First(
		long groupId, long classNameId,
		OrderByComparator<SavedContentEntry> orderByComparator) {

		List<SavedContentEntry> list = findByG_CN(
			groupId, classNameId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last saved content entry in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching saved content entry
	 * @throws NoSuchSavedContentEntryException if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry findByG_CN_Last(
			long groupId, long classNameId,
			OrderByComparator<SavedContentEntry> orderByComparator)
		throws NoSuchSavedContentEntryException {

		SavedContentEntry savedContentEntry = fetchByG_CN_Last(
			groupId, classNameId, orderByComparator);

		if (savedContentEntry != null) {
			return savedContentEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append("}");

		throw new NoSuchSavedContentEntryException(sb.toString());
	}

	/**
	 * Returns the last saved content entry in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching saved content entry, or <code>null</code> if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry fetchByG_CN_Last(
		long groupId, long classNameId,
		OrderByComparator<SavedContentEntry> orderByComparator) {

		int count = countByG_CN(groupId, classNameId);

		if (count == 0) {
			return null;
		}

		List<SavedContentEntry> list = findByG_CN(
			groupId, classNameId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the saved content entries before and after the current saved content entry in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param savedContentEntryId the primary key of the current saved content entry
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next saved content entry
	 * @throws NoSuchSavedContentEntryException if a saved content entry with the primary key could not be found
	 */
	@Override
	public SavedContentEntry[] findByG_CN_PrevAndNext(
			long savedContentEntryId, long groupId, long classNameId,
			OrderByComparator<SavedContentEntry> orderByComparator)
		throws NoSuchSavedContentEntryException {

		SavedContentEntry savedContentEntry = findByPrimaryKey(
			savedContentEntryId);

		Session session = null;

		try {
			session = openSession();

			SavedContentEntry[] array = new SavedContentEntryImpl[3];

			array[0] = getByG_CN_PrevAndNext(
				session, savedContentEntry, groupId, classNameId,
				orderByComparator, true);

			array[1] = savedContentEntry;

			array[2] = getByG_CN_PrevAndNext(
				session, savedContentEntry, groupId, classNameId,
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

	protected SavedContentEntry getByG_CN_PrevAndNext(
		Session session, SavedContentEntry savedContentEntry, long groupId,
		long classNameId,
		OrderByComparator<SavedContentEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_SAVEDCONTENTENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_CN_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_CN_CLASSNAMEID_2);

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
			sb.append(SavedContentEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(classNameId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						savedContentEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<SavedContentEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the saved content entries that the user has permission to view where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the matching saved content entries that the user has permission to view
	 */
	@Override
	public List<SavedContentEntry> filterFindByG_CN(
		long groupId, long classNameId) {

		return filterFindByG_CN(
			groupId, classNameId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the saved content entries that the user has permission to view where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @return the range of matching saved content entries that the user has permission to view
	 */
	@Override
	public List<SavedContentEntry> filterFindByG_CN(
		long groupId, long classNameId, int start, int end) {

		return filterFindByG_CN(groupId, classNameId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the saved content entries that the user has permissions to view where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching saved content entries that the user has permission to view
	 */
	@Override
	public List<SavedContentEntry> filterFindByG_CN(
		long groupId, long classNameId, int start, int end,
		OrderByComparator<SavedContentEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_CN(
				groupId, classNameId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_CN(
					groupId, classNameId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_SAVEDCONTENTENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_SAVEDCONTENTENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_CN_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_CN_CLASSNAMEID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_SAVEDCONTENTENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					SavedContentEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(SavedContentEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), SavedContentEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, SavedContentEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, SavedContentEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(classNameId);

			return (List<SavedContentEntry>)QueryUtil.list(
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
	 * Returns the saved content entries before and after the current saved content entry in the ordered set of saved content entries that the user has permission to view where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param savedContentEntryId the primary key of the current saved content entry
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next saved content entry
	 * @throws NoSuchSavedContentEntryException if a saved content entry with the primary key could not be found
	 */
	@Override
	public SavedContentEntry[] filterFindByG_CN_PrevAndNext(
			long savedContentEntryId, long groupId, long classNameId,
			OrderByComparator<SavedContentEntry> orderByComparator)
		throws NoSuchSavedContentEntryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_CN_PrevAndNext(
				savedContentEntryId, groupId, classNameId, orderByComparator);
		}

		SavedContentEntry savedContentEntry = findByPrimaryKey(
			savedContentEntryId);

		Session session = null;

		try {
			session = openSession();

			SavedContentEntry[] array = new SavedContentEntryImpl[3];

			array[0] = filterGetByG_CN_PrevAndNext(
				session, savedContentEntry, groupId, classNameId,
				orderByComparator, true);

			array[1] = savedContentEntry;

			array[2] = filterGetByG_CN_PrevAndNext(
				session, savedContentEntry, groupId, classNameId,
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

	protected SavedContentEntry filterGetByG_CN_PrevAndNext(
		Session session, SavedContentEntry savedContentEntry, long groupId,
		long classNameId,
		OrderByComparator<SavedContentEntry> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_SAVEDCONTENTENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_SAVEDCONTENTENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_CN_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_CN_CLASSNAMEID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_SAVEDCONTENTENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					SavedContentEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(SavedContentEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), SavedContentEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, SavedContentEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, SavedContentEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(classNameId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						savedContentEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<SavedContentEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the saved content entries where groupId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByG_CN(long groupId, long classNameId) {
		for (SavedContentEntry savedContentEntry :
				findByG_CN(
					groupId, classNameId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(savedContentEntry);
		}
	}

	/**
	 * Returns the number of saved content entries where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the number of matching saved content entries
	 */
	@Override
	public int countByG_CN(long groupId, long classNameId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SavedContentEntry.class)) {

			FinderPath finderPath = _finderPathCountByG_CN;

			Object[] finderArgs = new Object[] {groupId, classNameId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_SAVEDCONTENTENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_CN_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_CN_CLASSNAMEID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(classNameId);

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
	 * Returns the number of saved content entries that the user has permission to view where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the number of matching saved content entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_CN(long groupId, long classNameId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_CN(groupId, classNameId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<SavedContentEntry> savedContentEntries = findByG_CN(
				groupId, classNameId);

			savedContentEntries = InlineSQLHelperUtil.filter(
				savedContentEntries, groupId);

			return savedContentEntries.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_SAVEDCONTENTENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_CN_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_CN_CLASSNAMEID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), SavedContentEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(classNameId);

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

	private static final String _FINDER_COLUMN_G_CN_GROUPID_2 =
		"savedContentEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_CN_CLASSNAMEID_2 =
		"savedContentEntry.classNameId = ?";

	private FinderPath _finderPathWithPaginationFindByU_C;
	private FinderPath _finderPathWithoutPaginationFindByU_C;
	private FinderPath _finderPathCountByU_C;

	/**
	 * Returns all the saved content entries where userId = &#63; and classNameId = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @return the matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByU_C(long userId, long classNameId) {
		return findByU_C(
			userId, classNameId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the saved content entries where userId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @return the range of matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByU_C(
		long userId, long classNameId, int start, int end) {

		return findByU_C(userId, classNameId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the saved content entries where userId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByU_C(
		long userId, long classNameId, int start, int end,
		OrderByComparator<SavedContentEntry> orderByComparator) {

		return findByU_C(
			userId, classNameId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the saved content entries where userId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByU_C(
		long userId, long classNameId, int start, int end,
		OrderByComparator<SavedContentEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SavedContentEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByU_C;
					finderArgs = new Object[] {userId, classNameId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByU_C;
				finderArgs = new Object[] {
					userId, classNameId, start, end, orderByComparator
				};
			}

			List<SavedContentEntry> list = null;

			if (useFinderCache) {
				list = (List<SavedContentEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (SavedContentEntry savedContentEntry : list) {
						if ((userId != savedContentEntry.getUserId()) ||
							(classNameId !=
								savedContentEntry.getClassNameId())) {

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

				sb.append(_SQL_SELECT_SAVEDCONTENTENTRY_WHERE);

				sb.append(_FINDER_COLUMN_U_C_USERID_2);

				sb.append(_FINDER_COLUMN_U_C_CLASSNAMEID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(SavedContentEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(userId);

					queryPos.add(classNameId);

					list = (List<SavedContentEntry>)QueryUtil.list(
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
	 * Returns the first saved content entry in the ordered set where userId = &#63; and classNameId = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saved content entry
	 * @throws NoSuchSavedContentEntryException if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry findByU_C_First(
			long userId, long classNameId,
			OrderByComparator<SavedContentEntry> orderByComparator)
		throws NoSuchSavedContentEntryException {

		SavedContentEntry savedContentEntry = fetchByU_C_First(
			userId, classNameId, orderByComparator);

		if (savedContentEntry != null) {
			return savedContentEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append("}");

		throw new NoSuchSavedContentEntryException(sb.toString());
	}

	/**
	 * Returns the first saved content entry in the ordered set where userId = &#63; and classNameId = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saved content entry, or <code>null</code> if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry fetchByU_C_First(
		long userId, long classNameId,
		OrderByComparator<SavedContentEntry> orderByComparator) {

		List<SavedContentEntry> list = findByU_C(
			userId, classNameId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last saved content entry in the ordered set where userId = &#63; and classNameId = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching saved content entry
	 * @throws NoSuchSavedContentEntryException if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry findByU_C_Last(
			long userId, long classNameId,
			OrderByComparator<SavedContentEntry> orderByComparator)
		throws NoSuchSavedContentEntryException {

		SavedContentEntry savedContentEntry = fetchByU_C_Last(
			userId, classNameId, orderByComparator);

		if (savedContentEntry != null) {
			return savedContentEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append("}");

		throw new NoSuchSavedContentEntryException(sb.toString());
	}

	/**
	 * Returns the last saved content entry in the ordered set where userId = &#63; and classNameId = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching saved content entry, or <code>null</code> if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry fetchByU_C_Last(
		long userId, long classNameId,
		OrderByComparator<SavedContentEntry> orderByComparator) {

		int count = countByU_C(userId, classNameId);

		if (count == 0) {
			return null;
		}

		List<SavedContentEntry> list = findByU_C(
			userId, classNameId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the saved content entries before and after the current saved content entry in the ordered set where userId = &#63; and classNameId = &#63;.
	 *
	 * @param savedContentEntryId the primary key of the current saved content entry
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next saved content entry
	 * @throws NoSuchSavedContentEntryException if a saved content entry with the primary key could not be found
	 */
	@Override
	public SavedContentEntry[] findByU_C_PrevAndNext(
			long savedContentEntryId, long userId, long classNameId,
			OrderByComparator<SavedContentEntry> orderByComparator)
		throws NoSuchSavedContentEntryException {

		SavedContentEntry savedContentEntry = findByPrimaryKey(
			savedContentEntryId);

		Session session = null;

		try {
			session = openSession();

			SavedContentEntry[] array = new SavedContentEntryImpl[3];

			array[0] = getByU_C_PrevAndNext(
				session, savedContentEntry, userId, classNameId,
				orderByComparator, true);

			array[1] = savedContentEntry;

			array[2] = getByU_C_PrevAndNext(
				session, savedContentEntry, userId, classNameId,
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

	protected SavedContentEntry getByU_C_PrevAndNext(
		Session session, SavedContentEntry savedContentEntry, long userId,
		long classNameId,
		OrderByComparator<SavedContentEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_SAVEDCONTENTENTRY_WHERE);

		sb.append(_FINDER_COLUMN_U_C_USERID_2);

		sb.append(_FINDER_COLUMN_U_C_CLASSNAMEID_2);

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
			sb.append(SavedContentEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(userId);

		queryPos.add(classNameId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						savedContentEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<SavedContentEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the saved content entries where userId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByU_C(long userId, long classNameId) {
		for (SavedContentEntry savedContentEntry :
				findByU_C(
					userId, classNameId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(savedContentEntry);
		}
	}

	/**
	 * Returns the number of saved content entries where userId = &#63; and classNameId = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @return the number of matching saved content entries
	 */
	@Override
	public int countByU_C(long userId, long classNameId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SavedContentEntry.class)) {

			FinderPath finderPath = _finderPathCountByU_C;

			Object[] finderArgs = new Object[] {userId, classNameId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_SAVEDCONTENTENTRY_WHERE);

				sb.append(_FINDER_COLUMN_U_C_USERID_2);

				sb.append(_FINDER_COLUMN_U_C_CLASSNAMEID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(userId);

					queryPos.add(classNameId);

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

	private static final String _FINDER_COLUMN_U_C_USERID_2 =
		"savedContentEntry.userId = ? AND ";

	private static final String _FINDER_COLUMN_U_C_CLASSNAMEID_2 =
		"savedContentEntry.classNameId = ?";

	private FinderPath _finderPathWithPaginationFindByG_C_C;
	private FinderPath _finderPathWithoutPaginationFindByG_C_C;
	private FinderPath _finderPathCountByG_C_C;

	/**
	 * Returns all the saved content entries where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByG_C_C(
		long groupId, long classNameId, long classPK) {

		return findByG_C_C(
			groupId, classNameId, classPK, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the saved content entries where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @return the range of matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByG_C_C(
		long groupId, long classNameId, long classPK, int start, int end) {

		return findByG_C_C(groupId, classNameId, classPK, start, end, null);
	}

	/**
	 * Returns an ordered range of all the saved content entries where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByG_C_C(
		long groupId, long classNameId, long classPK, int start, int end,
		OrderByComparator<SavedContentEntry> orderByComparator) {

		return findByG_C_C(
			groupId, classNameId, classPK, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the saved content entries where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByG_C_C(
		long groupId, long classNameId, long classPK, int start, int end,
		OrderByComparator<SavedContentEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SavedContentEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_C_C;
					finderArgs = new Object[] {groupId, classNameId, classPK};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_C_C;
				finderArgs = new Object[] {
					groupId, classNameId, classPK, start, end, orderByComparator
				};
			}

			List<SavedContentEntry> list = null;

			if (useFinderCache) {
				list = (List<SavedContentEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (SavedContentEntry savedContentEntry : list) {
						if ((groupId != savedContentEntry.getGroupId()) ||
							(classNameId !=
								savedContentEntry.getClassNameId()) ||
							(classPK != savedContentEntry.getClassPK())) {

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

				sb.append(_SQL_SELECT_SAVEDCONTENTENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_C_C_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_C_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_G_C_C_CLASSPK_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(SavedContentEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(classNameId);

					queryPos.add(classPK);

					list = (List<SavedContentEntry>)QueryUtil.list(
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
	 * Returns the first saved content entry in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saved content entry
	 * @throws NoSuchSavedContentEntryException if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry findByG_C_C_First(
			long groupId, long classNameId, long classPK,
			OrderByComparator<SavedContentEntry> orderByComparator)
		throws NoSuchSavedContentEntryException {

		SavedContentEntry savedContentEntry = fetchByG_C_C_First(
			groupId, classNameId, classPK, orderByComparator);

		if (savedContentEntry != null) {
			return savedContentEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append("}");

		throw new NoSuchSavedContentEntryException(sb.toString());
	}

	/**
	 * Returns the first saved content entry in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saved content entry, or <code>null</code> if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry fetchByG_C_C_First(
		long groupId, long classNameId, long classPK,
		OrderByComparator<SavedContentEntry> orderByComparator) {

		List<SavedContentEntry> list = findByG_C_C(
			groupId, classNameId, classPK, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last saved content entry in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching saved content entry
	 * @throws NoSuchSavedContentEntryException if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry findByG_C_C_Last(
			long groupId, long classNameId, long classPK,
			OrderByComparator<SavedContentEntry> orderByComparator)
		throws NoSuchSavedContentEntryException {

		SavedContentEntry savedContentEntry = fetchByG_C_C_Last(
			groupId, classNameId, classPK, orderByComparator);

		if (savedContentEntry != null) {
			return savedContentEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append("}");

		throw new NoSuchSavedContentEntryException(sb.toString());
	}

	/**
	 * Returns the last saved content entry in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching saved content entry, or <code>null</code> if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry fetchByG_C_C_Last(
		long groupId, long classNameId, long classPK,
		OrderByComparator<SavedContentEntry> orderByComparator) {

		int count = countByG_C_C(groupId, classNameId, classPK);

		if (count == 0) {
			return null;
		}

		List<SavedContentEntry> list = findByG_C_C(
			groupId, classNameId, classPK, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the saved content entries before and after the current saved content entry in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param savedContentEntryId the primary key of the current saved content entry
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next saved content entry
	 * @throws NoSuchSavedContentEntryException if a saved content entry with the primary key could not be found
	 */
	@Override
	public SavedContentEntry[] findByG_C_C_PrevAndNext(
			long savedContentEntryId, long groupId, long classNameId,
			long classPK,
			OrderByComparator<SavedContentEntry> orderByComparator)
		throws NoSuchSavedContentEntryException {

		SavedContentEntry savedContentEntry = findByPrimaryKey(
			savedContentEntryId);

		Session session = null;

		try {
			session = openSession();

			SavedContentEntry[] array = new SavedContentEntryImpl[3];

			array[0] = getByG_C_C_PrevAndNext(
				session, savedContentEntry, groupId, classNameId, classPK,
				orderByComparator, true);

			array[1] = savedContentEntry;

			array[2] = getByG_C_C_PrevAndNext(
				session, savedContentEntry, groupId, classNameId, classPK,
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

	protected SavedContentEntry getByG_C_C_PrevAndNext(
		Session session, SavedContentEntry savedContentEntry, long groupId,
		long classNameId, long classPK,
		OrderByComparator<SavedContentEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_SAVEDCONTENTENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_C_C_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_C_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_G_C_C_CLASSPK_2);

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
			sb.append(SavedContentEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(classNameId);

		queryPos.add(classPK);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						savedContentEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<SavedContentEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the saved content entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching saved content entries that the user has permission to view
	 */
	@Override
	public List<SavedContentEntry> filterFindByG_C_C(
		long groupId, long classNameId, long classPK) {

		return filterFindByG_C_C(
			groupId, classNameId, classPK, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the saved content entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @return the range of matching saved content entries that the user has permission to view
	 */
	@Override
	public List<SavedContentEntry> filterFindByG_C_C(
		long groupId, long classNameId, long classPK, int start, int end) {

		return filterFindByG_C_C(
			groupId, classNameId, classPK, start, end, null);
	}

	/**
	 * Returns an ordered range of all the saved content entries that the user has permissions to view where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching saved content entries that the user has permission to view
	 */
	@Override
	public List<SavedContentEntry> filterFindByG_C_C(
		long groupId, long classNameId, long classPK, int start, int end,
		OrderByComparator<SavedContentEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_C_C(
				groupId, classNameId, classPK, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_C_C(
					groupId, classNameId, classPK, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_SAVEDCONTENTENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_SAVEDCONTENTENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_C_C_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_C_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_G_C_C_CLASSPK_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_SAVEDCONTENTENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					SavedContentEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(SavedContentEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), SavedContentEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, SavedContentEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, SavedContentEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(classNameId);

			queryPos.add(classPK);

			return (List<SavedContentEntry>)QueryUtil.list(
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
	 * Returns the saved content entries before and after the current saved content entry in the ordered set of saved content entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param savedContentEntryId the primary key of the current saved content entry
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next saved content entry
	 * @throws NoSuchSavedContentEntryException if a saved content entry with the primary key could not be found
	 */
	@Override
	public SavedContentEntry[] filterFindByG_C_C_PrevAndNext(
			long savedContentEntryId, long groupId, long classNameId,
			long classPK,
			OrderByComparator<SavedContentEntry> orderByComparator)
		throws NoSuchSavedContentEntryException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_C_C_PrevAndNext(
				savedContentEntryId, groupId, classNameId, classPK,
				orderByComparator);
		}

		SavedContentEntry savedContentEntry = findByPrimaryKey(
			savedContentEntryId);

		Session session = null;

		try {
			session = openSession();

			SavedContentEntry[] array = new SavedContentEntryImpl[3];

			array[0] = filterGetByG_C_C_PrevAndNext(
				session, savedContentEntry, groupId, classNameId, classPK,
				orderByComparator, true);

			array[1] = savedContentEntry;

			array[2] = filterGetByG_C_C_PrevAndNext(
				session, savedContentEntry, groupId, classNameId, classPK,
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

	protected SavedContentEntry filterGetByG_C_C_PrevAndNext(
		Session session, SavedContentEntry savedContentEntry, long groupId,
		long classNameId, long classPK,
		OrderByComparator<SavedContentEntry> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_SAVEDCONTENTENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_SAVEDCONTENTENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_C_C_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_C_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_G_C_C_CLASSPK_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_SAVEDCONTENTENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
					SavedContentEntryModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(SavedContentEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), SavedContentEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, SavedContentEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, SavedContentEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(classNameId);

		queryPos.add(classPK);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						savedContentEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<SavedContentEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the saved content entries where groupId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByG_C_C(long groupId, long classNameId, long classPK) {
		for (SavedContentEntry savedContentEntry :
				findByG_C_C(
					groupId, classNameId, classPK, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(savedContentEntry);
		}
	}

	/**
	 * Returns the number of saved content entries where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching saved content entries
	 */
	@Override
	public int countByG_C_C(long groupId, long classNameId, long classPK) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SavedContentEntry.class)) {

			FinderPath finderPath = _finderPathCountByG_C_C;

			Object[] finderArgs = new Object[] {groupId, classNameId, classPK};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_SAVEDCONTENTENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_C_C_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_C_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_G_C_C_CLASSPK_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

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
	}

	/**
	 * Returns the number of saved content entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching saved content entries that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_C(
		long groupId, long classNameId, long classPK) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_C_C(groupId, classNameId, classPK);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<SavedContentEntry> savedContentEntries = findByG_C_C(
				groupId, classNameId, classPK);

			savedContentEntries = InlineSQLHelperUtil.filter(
				savedContentEntries, groupId);

			return savedContentEntries.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_SAVEDCONTENTENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_C_C_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_C_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_G_C_C_CLASSPK_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), SavedContentEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

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

	private static final String _FINDER_COLUMN_G_C_C_GROUPID_2 =
		"savedContentEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_C_CLASSNAMEID_2 =
		"savedContentEntry.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_C_CLASSPK_2 =
		"savedContentEntry.classPK = ?";

	private FinderPath _finderPathWithPaginationFindByC_C_C;
	private FinderPath _finderPathWithoutPaginationFindByC_C_C;
	private FinderPath _finderPathCountByC_C_C;

	/**
	 * Returns all the saved content entries where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByC_C_C(
		long companyId, long classNameId, long classPK) {

		return findByC_C_C(
			companyId, classNameId, classPK, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the saved content entries where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @return the range of matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByC_C_C(
		long companyId, long classNameId, long classPK, int start, int end) {

		return findByC_C_C(companyId, classNameId, classPK, start, end, null);
	}

	/**
	 * Returns an ordered range of all the saved content entries where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByC_C_C(
		long companyId, long classNameId, long classPK, int start, int end,
		OrderByComparator<SavedContentEntry> orderByComparator) {

		return findByC_C_C(
			companyId, classNameId, classPK, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the saved content entries where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByC_C_C(
		long companyId, long classNameId, long classPK, int start, int end,
		OrderByComparator<SavedContentEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SavedContentEntry.class)) {

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

			List<SavedContentEntry> list = null;

			if (useFinderCache) {
				list = (List<SavedContentEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (SavedContentEntry savedContentEntry : list) {
						if ((companyId != savedContentEntry.getCompanyId()) ||
							(classNameId !=
								savedContentEntry.getClassNameId()) ||
							(classPK != savedContentEntry.getClassPK())) {

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

				sb.append(_SQL_SELECT_SAVEDCONTENTENTRY_WHERE);

				sb.append(_FINDER_COLUMN_C_C_C_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_C_C_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_C_C_C_CLASSPK_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(SavedContentEntryModelImpl.ORDER_BY_JPQL);
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

					list = (List<SavedContentEntry>)QueryUtil.list(
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
	 * Returns the first saved content entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saved content entry
	 * @throws NoSuchSavedContentEntryException if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry findByC_C_C_First(
			long companyId, long classNameId, long classPK,
			OrderByComparator<SavedContentEntry> orderByComparator)
		throws NoSuchSavedContentEntryException {

		SavedContentEntry savedContentEntry = fetchByC_C_C_First(
			companyId, classNameId, classPK, orderByComparator);

		if (savedContentEntry != null) {
			return savedContentEntry;
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

		throw new NoSuchSavedContentEntryException(sb.toString());
	}

	/**
	 * Returns the first saved content entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saved content entry, or <code>null</code> if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry fetchByC_C_C_First(
		long companyId, long classNameId, long classPK,
		OrderByComparator<SavedContentEntry> orderByComparator) {

		List<SavedContentEntry> list = findByC_C_C(
			companyId, classNameId, classPK, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last saved content entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching saved content entry
	 * @throws NoSuchSavedContentEntryException if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry findByC_C_C_Last(
			long companyId, long classNameId, long classPK,
			OrderByComparator<SavedContentEntry> orderByComparator)
		throws NoSuchSavedContentEntryException {

		SavedContentEntry savedContentEntry = fetchByC_C_C_Last(
			companyId, classNameId, classPK, orderByComparator);

		if (savedContentEntry != null) {
			return savedContentEntry;
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

		throw new NoSuchSavedContentEntryException(sb.toString());
	}

	/**
	 * Returns the last saved content entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching saved content entry, or <code>null</code> if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry fetchByC_C_C_Last(
		long companyId, long classNameId, long classPK,
		OrderByComparator<SavedContentEntry> orderByComparator) {

		int count = countByC_C_C(companyId, classNameId, classPK);

		if (count == 0) {
			return null;
		}

		List<SavedContentEntry> list = findByC_C_C(
			companyId, classNameId, classPK, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the saved content entries before and after the current saved content entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param savedContentEntryId the primary key of the current saved content entry
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next saved content entry
	 * @throws NoSuchSavedContentEntryException if a saved content entry with the primary key could not be found
	 */
	@Override
	public SavedContentEntry[] findByC_C_C_PrevAndNext(
			long savedContentEntryId, long companyId, long classNameId,
			long classPK,
			OrderByComparator<SavedContentEntry> orderByComparator)
		throws NoSuchSavedContentEntryException {

		SavedContentEntry savedContentEntry = findByPrimaryKey(
			savedContentEntryId);

		Session session = null;

		try {
			session = openSession();

			SavedContentEntry[] array = new SavedContentEntryImpl[3];

			array[0] = getByC_C_C_PrevAndNext(
				session, savedContentEntry, companyId, classNameId, classPK,
				orderByComparator, true);

			array[1] = savedContentEntry;

			array[2] = getByC_C_C_PrevAndNext(
				session, savedContentEntry, companyId, classNameId, classPK,
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

	protected SavedContentEntry getByC_C_C_PrevAndNext(
		Session session, SavedContentEntry savedContentEntry, long companyId,
		long classNameId, long classPK,
		OrderByComparator<SavedContentEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_SAVEDCONTENTENTRY_WHERE);

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
			sb.append(SavedContentEntryModelImpl.ORDER_BY_JPQL);
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
						savedContentEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<SavedContentEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the saved content entries where companyId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByC_C_C(long companyId, long classNameId, long classPK) {
		for (SavedContentEntry savedContentEntry :
				findByC_C_C(
					companyId, classNameId, classPK, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(savedContentEntry);
		}
	}

	/**
	 * Returns the number of saved content entries where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching saved content entries
	 */
	@Override
	public int countByC_C_C(long companyId, long classNameId, long classPK) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SavedContentEntry.class)) {

			FinderPath finderPath = _finderPathCountByC_C_C;

			Object[] finderArgs = new Object[] {
				companyId, classNameId, classPK
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_SAVEDCONTENTENTRY_WHERE);

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
	}

	private static final String _FINDER_COLUMN_C_C_C_COMPANYID_2 =
		"savedContentEntry.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_C_C_CLASSNAMEID_2 =
		"savedContentEntry.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_C_C_C_CLASSPK_2 =
		"savedContentEntry.classPK = ?";

	private FinderPath _finderPathFetchByG_U_C_C;

	/**
	 * Returns the saved content entry where groupId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63; or throws a <code>NoSuchSavedContentEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching saved content entry
	 * @throws NoSuchSavedContentEntryException if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry findByG_U_C_C(
			long groupId, long userId, long classNameId, long classPK)
		throws NoSuchSavedContentEntryException {

		SavedContentEntry savedContentEntry = fetchByG_U_C_C(
			groupId, userId, classNameId, classPK);

		if (savedContentEntry == null) {
			StringBundler sb = new StringBundler(10);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("groupId=");
			sb.append(groupId);

			sb.append(", userId=");
			sb.append(userId);

			sb.append(", classNameId=");
			sb.append(classNameId);

			sb.append(", classPK=");
			sb.append(classPK);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchSavedContentEntryException(sb.toString());
		}

		return savedContentEntry;
	}

	/**
	 * Returns the saved content entry where groupId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching saved content entry, or <code>null</code> if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry fetchByG_U_C_C(
		long groupId, long userId, long classNameId, long classPK) {

		return fetchByG_U_C_C(groupId, userId, classNameId, classPK, true);
	}

	/**
	 * Returns the saved content entry where groupId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching saved content entry, or <code>null</code> if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry fetchByG_U_C_C(
		long groupId, long userId, long classNameId, long classPK,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SavedContentEntry.class)) {

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {
					groupId, userId, classNameId, classPK
				};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByG_U_C_C, finderArgs, this);
			}

			if (result instanceof SavedContentEntry) {
				SavedContentEntry savedContentEntry = (SavedContentEntry)result;

				if ((groupId != savedContentEntry.getGroupId()) ||
					(userId != savedContentEntry.getUserId()) ||
					(classNameId != savedContentEntry.getClassNameId()) ||
					(classPK != savedContentEntry.getClassPK())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(6);

				sb.append(_SQL_SELECT_SAVEDCONTENTENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_U_C_C_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_U_C_C_USERID_2);

				sb.append(_FINDER_COLUMN_G_U_C_C_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_G_U_C_C_CLASSPK_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(userId);

					queryPos.add(classNameId);

					queryPos.add(classPK);

					List<SavedContentEntry> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByG_U_C_C, finderArgs, list);
						}
					}
					else {
						SavedContentEntry savedContentEntry = list.get(0);

						result = savedContentEntry;

						cacheResult(savedContentEntry);
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
				return (SavedContentEntry)result;
			}
		}
	}

	/**
	 * Removes the saved content entry where groupId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the saved content entry that was removed
	 */
	@Override
	public SavedContentEntry removeByG_U_C_C(
			long groupId, long userId, long classNameId, long classPK)
		throws NoSuchSavedContentEntryException {

		SavedContentEntry savedContentEntry = findByG_U_C_C(
			groupId, userId, classNameId, classPK);

		return remove(savedContentEntry);
	}

	/**
	 * Returns the number of saved content entries where groupId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching saved content entries
	 */
	@Override
	public int countByG_U_C_C(
		long groupId, long userId, long classNameId, long classPK) {

		SavedContentEntry savedContentEntry = fetchByG_U_C_C(
			groupId, userId, classNameId, classPK);

		if (savedContentEntry == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_G_U_C_C_GROUPID_2 =
		"savedContentEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_U_C_C_USERID_2 =
		"savedContentEntry.userId = ? AND ";

	private static final String _FINDER_COLUMN_G_U_C_C_CLASSNAMEID_2 =
		"savedContentEntry.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_G_U_C_C_CLASSPK_2 =
		"savedContentEntry.classPK = ?";

	private FinderPath _finderPathWithPaginationFindByC_U_C_C;
	private FinderPath _finderPathWithoutPaginationFindByC_U_C_C;
	private FinderPath _finderPathFetchByC_U_C_C;
	private FinderPath _finderPathCountByC_U_C_C;
	private FinderPath _finderPathWithPaginationCountByC_U_C_C;

	/**
	 * Returns all the saved content entries where companyId = &#63; and userId = &#63; and classNameId = &#63; and classPK = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPKs the class pks
	 * @return the matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByC_U_C_C(
		long companyId, long userId, long classNameId, long[] classPKs) {

		return findByC_U_C_C(
			companyId, userId, classNameId, classPKs, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the saved content entries where companyId = &#63; and userId = &#63; and classNameId = &#63; and classPK = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPKs the class pks
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @return the range of matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByC_U_C_C(
		long companyId, long userId, long classNameId, long[] classPKs,
		int start, int end) {

		return findByC_U_C_C(
			companyId, userId, classNameId, classPKs, start, end, null);
	}

	/**
	 * Returns an ordered range of all the saved content entries where companyId = &#63; and userId = &#63; and classNameId = &#63; and classPK = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPKs the class pks
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByC_U_C_C(
		long companyId, long userId, long classNameId, long[] classPKs,
		int start, int end,
		OrderByComparator<SavedContentEntry> orderByComparator) {

		return findByC_U_C_C(
			companyId, userId, classNameId, classPKs, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the saved content entries where companyId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPKs the class pks
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching saved content entries
	 */
	@Override
	public List<SavedContentEntry> findByC_U_C_C(
		long companyId, long userId, long classNameId, long[] classPKs,
		int start, int end,
		OrderByComparator<SavedContentEntry> orderByComparator,
		boolean useFinderCache) {

		if (classPKs == null) {
			classPKs = new long[0];
		}
		else if (classPKs.length > 1) {
			classPKs = ArrayUtil.sortedUnique(classPKs);
		}

		if (classPKs.length == 1) {
			SavedContentEntry savedContentEntry = fetchByC_U_C_C(
				companyId, userId, classNameId, classPKs[0]);

			if (savedContentEntry == null) {
				return Collections.emptyList();
			}
			else {
				List<SavedContentEntry> list = new ArrayList<SavedContentEntry>(
					1);

				list.add(savedContentEntry);

				return list;
			}
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SavedContentEntry.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						companyId, userId, classNameId,
						StringUtil.merge(classPKs)
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					companyId, userId, classNameId, StringUtil.merge(classPKs),
					start, end, orderByComparator
				};
			}

			List<SavedContentEntry> list = null;

			if (useFinderCache) {
				list = (List<SavedContentEntry>)finderCache.getResult(
					_finderPathWithPaginationFindByC_U_C_C, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (SavedContentEntry savedContentEntry : list) {
						if ((companyId != savedContentEntry.getCompanyId()) ||
							(userId != savedContentEntry.getUserId()) ||
							(classNameId !=
								savedContentEntry.getClassNameId()) ||
							!ArrayUtil.contains(
								classPKs, savedContentEntry.getClassPK())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_SELECT_SAVEDCONTENTENTRY_WHERE);

				sb.append(_FINDER_COLUMN_C_U_C_C_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_U_C_C_USERID_2);

				sb.append(_FINDER_COLUMN_C_U_C_C_CLASSNAMEID_2);

				if (classPKs.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_C_U_C_C_CLASSPK_7);

					sb.append(StringUtil.merge(classPKs));

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
					sb.append(SavedContentEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(userId);

					queryPos.add(classNameId);

					list = (List<SavedContentEntry>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(
							_finderPathWithPaginationFindByC_U_C_C, finderArgs,
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
	 * Returns the saved content entry where companyId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63; or throws a <code>NoSuchSavedContentEntryException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching saved content entry
	 * @throws NoSuchSavedContentEntryException if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry findByC_U_C_C(
			long companyId, long userId, long classNameId, long classPK)
		throws NoSuchSavedContentEntryException {

		SavedContentEntry savedContentEntry = fetchByC_U_C_C(
			companyId, userId, classNameId, classPK);

		if (savedContentEntry == null) {
			StringBundler sb = new StringBundler(10);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("companyId=");
			sb.append(companyId);

			sb.append(", userId=");
			sb.append(userId);

			sb.append(", classNameId=");
			sb.append(classNameId);

			sb.append(", classPK=");
			sb.append(classPK);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchSavedContentEntryException(sb.toString());
		}

		return savedContentEntry;
	}

	/**
	 * Returns the saved content entry where companyId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching saved content entry, or <code>null</code> if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry fetchByC_U_C_C(
		long companyId, long userId, long classNameId, long classPK) {

		return fetchByC_U_C_C(companyId, userId, classNameId, classPK, true);
	}

	/**
	 * Returns the saved content entry where companyId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching saved content entry, or <code>null</code> if a matching saved content entry could not be found
	 */
	@Override
	public SavedContentEntry fetchByC_U_C_C(
		long companyId, long userId, long classNameId, long classPK,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SavedContentEntry.class)) {

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {
					companyId, userId, classNameId, classPK
				};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByC_U_C_C, finderArgs, this);
			}

			if (result instanceof SavedContentEntry) {
				SavedContentEntry savedContentEntry = (SavedContentEntry)result;

				if ((companyId != savedContentEntry.getCompanyId()) ||
					(userId != savedContentEntry.getUserId()) ||
					(classNameId != savedContentEntry.getClassNameId()) ||
					(classPK != savedContentEntry.getClassPK())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(6);

				sb.append(_SQL_SELECT_SAVEDCONTENTENTRY_WHERE);

				sb.append(_FINDER_COLUMN_C_U_C_C_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_U_C_C_USERID_2);

				sb.append(_FINDER_COLUMN_C_U_C_C_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_C_U_C_C_CLASSPK_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(userId);

					queryPos.add(classNameId);

					queryPos.add(classPK);

					List<SavedContentEntry> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByC_U_C_C, finderArgs, list);
						}
					}
					else {
						SavedContentEntry savedContentEntry = list.get(0);

						result = savedContentEntry;

						cacheResult(savedContentEntry);
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
				return (SavedContentEntry)result;
			}
		}
	}

	/**
	 * Removes the saved content entry where companyId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the saved content entry that was removed
	 */
	@Override
	public SavedContentEntry removeByC_U_C_C(
			long companyId, long userId, long classNameId, long classPK)
		throws NoSuchSavedContentEntryException {

		SavedContentEntry savedContentEntry = findByC_U_C_C(
			companyId, userId, classNameId, classPK);

		return remove(savedContentEntry);
	}

	/**
	 * Returns the number of saved content entries where companyId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching saved content entries
	 */
	@Override
	public int countByC_U_C_C(
		long companyId, long userId, long classNameId, long classPK) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SavedContentEntry.class)) {

			FinderPath finderPath = _finderPathCountByC_U_C_C;

			Object[] finderArgs = new Object[] {
				companyId, userId, classNameId, classPK
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_SAVEDCONTENTENTRY_WHERE);

				sb.append(_FINDER_COLUMN_C_U_C_C_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_U_C_C_USERID_2);

				sb.append(_FINDER_COLUMN_C_U_C_C_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_C_U_C_C_CLASSPK_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(userId);

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
	}

	/**
	 * Returns the number of saved content entries where companyId = &#63; and userId = &#63; and classNameId = &#63; and classPK = any &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPKs the class pks
	 * @return the number of matching saved content entries
	 */
	@Override
	public int countByC_U_C_C(
		long companyId, long userId, long classNameId, long[] classPKs) {

		if (classPKs == null) {
			classPKs = new long[0];
		}
		else if (classPKs.length > 1) {
			classPKs = ArrayUtil.sortedUnique(classPKs);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SavedContentEntry.class)) {

			Object[] finderArgs = new Object[] {
				companyId, userId, classNameId, StringUtil.merge(classPKs)
			};

			Long count = (Long)finderCache.getResult(
				_finderPathWithPaginationCountByC_U_C_C, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_SAVEDCONTENTENTRY_WHERE);

				sb.append(_FINDER_COLUMN_C_U_C_C_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_U_C_C_USERID_2);

				sb.append(_FINDER_COLUMN_C_U_C_C_CLASSNAMEID_2);

				if (classPKs.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_C_U_C_C_CLASSPK_7);

					sb.append(StringUtil.merge(classPKs));

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

					queryPos.add(companyId);

					queryPos.add(userId);

					queryPos.add(classNameId);

					count = (Long)query.uniqueResult();

					finderCache.putResult(
						_finderPathWithPaginationCountByC_U_C_C, finderArgs,
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

	private static final String _FINDER_COLUMN_C_U_C_C_COMPANYID_2 =
		"savedContentEntry.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_U_C_C_USERID_2 =
		"savedContentEntry.userId = ? AND ";

	private static final String _FINDER_COLUMN_C_U_C_C_CLASSNAMEID_2 =
		"savedContentEntry.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_C_U_C_C_CLASSPK_2 =
		"savedContentEntry.classPK = ?";

	private static final String _FINDER_COLUMN_C_U_C_C_CLASSPK_7 =
		"savedContentEntry.classPK IN (";

	public SavedContentEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(SavedContentEntry.class);

		setModelImplClass(SavedContentEntryImpl.class);
		setModelPKClass(long.class);

		setTable(SavedContentEntryTable.INSTANCE);
	}

	/**
	 * Caches the saved content entry in the entity cache if it is enabled.
	 *
	 * @param savedContentEntry the saved content entry
	 */
	@Override
	public void cacheResult(SavedContentEntry savedContentEntry) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					savedContentEntry.getCtCollectionId())) {

			entityCache.putResult(
				SavedContentEntryImpl.class, savedContentEntry.getPrimaryKey(),
				savedContentEntry);

			finderCache.putResult(
				_finderPathFetchByUUID_G,
				new Object[] {
					savedContentEntry.getUuid(), savedContentEntry.getGroupId()
				},
				savedContentEntry);

			finderCache.putResult(
				_finderPathFetchByG_U_C_C,
				new Object[] {
					savedContentEntry.getGroupId(),
					savedContentEntry.getUserId(),
					savedContentEntry.getClassNameId(),
					savedContentEntry.getClassPK()
				},
				savedContentEntry);

			finderCache.putResult(
				_finderPathFetchByC_U_C_C,
				new Object[] {
					savedContentEntry.getCompanyId(),
					savedContentEntry.getUserId(),
					savedContentEntry.getClassNameId(),
					savedContentEntry.getClassPK()
				},
				savedContentEntry);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the saved content entries in the entity cache if it is enabled.
	 *
	 * @param savedContentEntries the saved content entries
	 */
	@Override
	public void cacheResult(List<SavedContentEntry> savedContentEntries) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (savedContentEntries.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (SavedContentEntry savedContentEntry : savedContentEntries) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						savedContentEntry.getCtCollectionId())) {

				if (entityCache.getResult(
						SavedContentEntryImpl.class,
						savedContentEntry.getPrimaryKey()) == null) {

					cacheResult(savedContentEntry);
				}
			}
		}
	}

	/**
	 * Clears the cache for all saved content entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(SavedContentEntryImpl.class);

		finderCache.clearCache(SavedContentEntryImpl.class);
	}

	/**
	 * Clears the cache for the saved content entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(SavedContentEntry savedContentEntry) {
		entityCache.removeResult(
			SavedContentEntryImpl.class, savedContentEntry);
	}

	@Override
	public void clearCache(List<SavedContentEntry> savedContentEntries) {
		for (SavedContentEntry savedContentEntry : savedContentEntries) {
			entityCache.removeResult(
				SavedContentEntryImpl.class, savedContentEntry);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(SavedContentEntryImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(SavedContentEntryImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		SavedContentEntryModelImpl savedContentEntryModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					savedContentEntryModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				savedContentEntryModelImpl.getUuid(),
				savedContentEntryModelImpl.getGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByUUID_G, args, savedContentEntryModelImpl);

			args = new Object[] {
				savedContentEntryModelImpl.getGroupId(),
				savedContentEntryModelImpl.getUserId(),
				savedContentEntryModelImpl.getClassNameId(),
				savedContentEntryModelImpl.getClassPK()
			};

			finderCache.putResult(
				_finderPathFetchByG_U_C_C, args, savedContentEntryModelImpl);

			args = new Object[] {
				savedContentEntryModelImpl.getCompanyId(),
				savedContentEntryModelImpl.getUserId(),
				savedContentEntryModelImpl.getClassNameId(),
				savedContentEntryModelImpl.getClassPK()
			};

			finderCache.putResult(
				_finderPathFetchByC_U_C_C, args, savedContentEntryModelImpl);
		}
	}

	/**
	 * Creates a new saved content entry with the primary key. Does not add the saved content entry to the database.
	 *
	 * @param savedContentEntryId the primary key for the new saved content entry
	 * @return the new saved content entry
	 */
	@Override
	public SavedContentEntry create(long savedContentEntryId) {
		SavedContentEntry savedContentEntry = new SavedContentEntryImpl();

		savedContentEntry.setNew(true);
		savedContentEntry.setPrimaryKey(savedContentEntryId);

		String uuid = PortalUUIDUtil.generate();

		savedContentEntry.setUuid(uuid);

		savedContentEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return savedContentEntry;
	}

	/**
	 * Removes the saved content entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param savedContentEntryId the primary key of the saved content entry
	 * @return the saved content entry that was removed
	 * @throws NoSuchSavedContentEntryException if a saved content entry with the primary key could not be found
	 */
	@Override
	public SavedContentEntry remove(long savedContentEntryId)
		throws NoSuchSavedContentEntryException {

		return remove((Serializable)savedContentEntryId);
	}

	/**
	 * Removes the saved content entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the saved content entry
	 * @return the saved content entry that was removed
	 * @throws NoSuchSavedContentEntryException if a saved content entry with the primary key could not be found
	 */
	@Override
	public SavedContentEntry remove(Serializable primaryKey)
		throws NoSuchSavedContentEntryException {

		Session session = null;

		try {
			session = openSession();

			SavedContentEntry savedContentEntry =
				(SavedContentEntry)session.get(
					SavedContentEntryImpl.class, primaryKey);

			if (savedContentEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchSavedContentEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(savedContentEntry);
		}
		catch (NoSuchSavedContentEntryException noSuchEntityException) {
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
	protected SavedContentEntry removeImpl(
		SavedContentEntry savedContentEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(savedContentEntry)) {
				savedContentEntry = (SavedContentEntry)session.get(
					SavedContentEntryImpl.class,
					savedContentEntry.getPrimaryKeyObj());
			}

			if ((savedContentEntry != null) &&
				ctPersistenceHelper.isRemove(savedContentEntry)) {

				session.delete(savedContentEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (savedContentEntry != null) {
			clearCache(savedContentEntry);
		}

		return savedContentEntry;
	}

	@Override
	public SavedContentEntry updateImpl(SavedContentEntry savedContentEntry) {
		boolean isNew = savedContentEntry.isNew();

		if (!(savedContentEntry instanceof SavedContentEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(savedContentEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					savedContentEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in savedContentEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom SavedContentEntry implementation " +
					savedContentEntry.getClass());
		}

		SavedContentEntryModelImpl savedContentEntryModelImpl =
			(SavedContentEntryModelImpl)savedContentEntry;

		if (Validator.isNull(savedContentEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			savedContentEntry.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (savedContentEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				savedContentEntry.setCreateDate(date);
			}
			else {
				savedContentEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!savedContentEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				savedContentEntry.setModifiedDate(date);
			}
			else {
				savedContentEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(savedContentEntry)) {
				if (!isNew) {
					session.evict(
						SavedContentEntryImpl.class,
						savedContentEntry.getPrimaryKeyObj());
				}

				session.save(savedContentEntry);
			}
			else {
				savedContentEntry = (SavedContentEntry)session.merge(
					savedContentEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			SavedContentEntryImpl.class, savedContentEntryModelImpl, false,
			true);

		cacheUniqueFindersCache(savedContentEntryModelImpl);

		if (isNew) {
			savedContentEntry.setNew(false);
		}

		savedContentEntry.resetOriginalValues();

		return savedContentEntry;
	}

	/**
	 * Returns the saved content entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the saved content entry
	 * @return the saved content entry
	 * @throws NoSuchSavedContentEntryException if a saved content entry with the primary key could not be found
	 */
	@Override
	public SavedContentEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchSavedContentEntryException {

		SavedContentEntry savedContentEntry = fetchByPrimaryKey(primaryKey);

		if (savedContentEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchSavedContentEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return savedContentEntry;
	}

	/**
	 * Returns the saved content entry with the primary key or throws a <code>NoSuchSavedContentEntryException</code> if it could not be found.
	 *
	 * @param savedContentEntryId the primary key of the saved content entry
	 * @return the saved content entry
	 * @throws NoSuchSavedContentEntryException if a saved content entry with the primary key could not be found
	 */
	@Override
	public SavedContentEntry findByPrimaryKey(long savedContentEntryId)
		throws NoSuchSavedContentEntryException {

		return findByPrimaryKey((Serializable)savedContentEntryId);
	}

	/**
	 * Returns the saved content entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the saved content entry
	 * @return the saved content entry, or <code>null</code> if a saved content entry with the primary key could not be found
	 */
	@Override
	public SavedContentEntry fetchByPrimaryKey(Serializable primaryKey) {
		if (ctPersistenceHelper.isProductionMode(
				SavedContentEntry.class, primaryKey)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		SavedContentEntry savedContentEntry =
			(SavedContentEntry)entityCache.getResult(
				SavedContentEntryImpl.class, primaryKey);

		if (savedContentEntry != null) {
			return savedContentEntry;
		}

		Session session = null;

		try {
			session = openSession();

			savedContentEntry = (SavedContentEntry)session.get(
				SavedContentEntryImpl.class, primaryKey);

			if (savedContentEntry != null) {
				cacheResult(savedContentEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return savedContentEntry;
	}

	/**
	 * Returns the saved content entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param savedContentEntryId the primary key of the saved content entry
	 * @return the saved content entry, or <code>null</code> if a saved content entry with the primary key could not be found
	 */
	@Override
	public SavedContentEntry fetchByPrimaryKey(long savedContentEntryId) {
		return fetchByPrimaryKey((Serializable)savedContentEntryId);
	}

	@Override
	public Map<Serializable, SavedContentEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (ctPersistenceHelper.isProductionMode(SavedContentEntry.class)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, SavedContentEntry> map =
			new HashMap<Serializable, SavedContentEntry>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			SavedContentEntry savedContentEntry = fetchByPrimaryKey(primaryKey);

			if (savedContentEntry != null) {
				map.put(primaryKey, savedContentEntry);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
						SavedContentEntry.class, primaryKey)) {

				SavedContentEntry savedContentEntry =
					(SavedContentEntry)entityCache.getResult(
						SavedContentEntryImpl.class, primaryKey);

				if (savedContentEntry == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, savedContentEntry);
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

			for (SavedContentEntry savedContentEntry :
					(List<SavedContentEntry>)query.list()) {

				map.put(
					savedContentEntry.getPrimaryKeyObj(), savedContentEntry);

				cacheResult(savedContentEntry);
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
	 * Returns all the saved content entries.
	 *
	 * @return the saved content entries
	 */
	@Override
	public List<SavedContentEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the saved content entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @return the range of saved content entries
	 */
	@Override
	public List<SavedContentEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the saved content entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of saved content entries
	 */
	@Override
	public List<SavedContentEntry> findAll(
		int start, int end,
		OrderByComparator<SavedContentEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the saved content entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SavedContentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of saved content entries
	 * @param end the upper bound of the range of saved content entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of saved content entries
	 */
	@Override
	public List<SavedContentEntry> findAll(
		int start, int end,
		OrderByComparator<SavedContentEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SavedContentEntry.class)) {

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

			List<SavedContentEntry> list = null;

			if (useFinderCache) {
				list = (List<SavedContentEntry>)finderCache.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_SAVEDCONTENTENTRY);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_SAVEDCONTENTENTRY;

					sql = sql.concat(SavedContentEntryModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<SavedContentEntry>)QueryUtil.list(
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
	 * Removes all the saved content entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (SavedContentEntry savedContentEntry : findAll()) {
			remove(savedContentEntry);
		}
	}

	/**
	 * Returns the number of saved content entries.
	 *
	 * @return the number of saved content entries
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SavedContentEntry.class)) {

			Long count = (Long)finderCache.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(
						_SQL_COUNT_SAVEDCONTENTENTRY);

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
		return "savedContentEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_SAVEDCONTENTENTRY;
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
		return SavedContentEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "SavedContentEntry";
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
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("savedContentEntryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "userId", "classNameId", "classPK"});

		_uniqueIndexColumnNames.add(
			new String[] {"companyId", "userId", "classNameId", "classPK"});
	}

	/**
	 * Initializes the saved content entry persistence.
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

		_finderPathWithPaginationFindByUserId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUserId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"userId"}, true);

		_finderPathWithoutPaginationFindByUserId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUserId",
			new String[] {Long.class.getName()}, new String[] {"userId"}, true);

		_finderPathCountByUserId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUserId",
			new String[] {Long.class.getName()}, new String[] {"userId"},
			false);

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

		_finderPathWithPaginationFindByG_CN = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_CN",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "classNameId"}, true);

		_finderPathWithoutPaginationFindByG_CN = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_CN",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "classNameId"}, true);

		_finderPathCountByG_CN = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_CN",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "classNameId"}, false);

		_finderPathWithPaginationFindByU_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_C",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"userId", "classNameId"}, true);

		_finderPathWithoutPaginationFindByU_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_C",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"userId", "classNameId"}, true);

		_finderPathCountByU_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByU_C",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"userId", "classNameId"}, false);

		_finderPathWithPaginationFindByG_C_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_C",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "classNameId", "classPK"}, true);

		_finderPathWithoutPaginationFindByG_C_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_C",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"groupId", "classNameId", "classPK"}, true);

		_finderPathCountByG_C_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_C",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"groupId", "classNameId", "classPK"}, false);

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

		_finderPathFetchByG_U_C_C = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByG_U_C_C",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Long.class.getName()
			},
			new String[] {"groupId", "userId", "classNameId", "classPK"}, true);

		_finderPathWithPaginationFindByC_U_C_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_U_C_C",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "userId", "classNameId", "classPK"},
			true);

		_finderPathWithoutPaginationFindByC_U_C_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_U_C_C",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Long.class.getName()
			},
			new String[] {"companyId", "userId", "classNameId", "classPK"},
			true);

		_finderPathFetchByC_U_C_C = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByC_U_C_C",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Long.class.getName()
			},
			new String[] {"companyId", "userId", "classNameId", "classPK"},
			true);

		_finderPathCountByC_U_C_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_U_C_C",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Long.class.getName()
			},
			new String[] {"companyId", "userId", "classNameId", "classPK"},
			false);

		_finderPathWithPaginationCountByC_U_C_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_U_C_C",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Long.class.getName()
			},
			new String[] {"companyId", "userId", "classNameId", "classPK"},
			false);

		SavedContentEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		SavedContentEntryUtil.setPersistence(null);

		entityCache.removeCache(SavedContentEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = SavedContentEntryPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = SavedContentEntryPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = SavedContentEntryPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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

	private static final String _SQL_SELECT_SAVEDCONTENTENTRY =
		"SELECT savedContentEntry FROM SavedContentEntry savedContentEntry";

	private static final String _SQL_SELECT_SAVEDCONTENTENTRY_WHERE =
		"SELECT savedContentEntry FROM SavedContentEntry savedContentEntry WHERE ";

	private static final String _SQL_COUNT_SAVEDCONTENTENTRY =
		"SELECT COUNT(savedContentEntry) FROM SavedContentEntry savedContentEntry";

	private static final String _SQL_COUNT_SAVEDCONTENTENTRY_WHERE =
		"SELECT COUNT(savedContentEntry) FROM SavedContentEntry savedContentEntry WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"savedContentEntry.savedContentEntryId";

	private static final String _FILTER_SQL_SELECT_SAVEDCONTENTENTRY_WHERE =
		"SELECT DISTINCT {savedContentEntry.*} FROM SavedContentEntry savedContentEntry WHERE ";

	private static final String
		_FILTER_SQL_SELECT_SAVEDCONTENTENTRY_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {SavedContentEntry.*} FROM (SELECT DISTINCT savedContentEntry.savedContentEntryId FROM SavedContentEntry savedContentEntry WHERE ";

	private static final String
		_FILTER_SQL_SELECT_SAVEDCONTENTENTRY_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN SavedContentEntry ON TEMP_TABLE.savedContentEntryId = SavedContentEntry.savedContentEntryId";

	private static final String _FILTER_SQL_COUNT_SAVEDCONTENTENTRY_WHERE =
		"SELECT COUNT(DISTINCT savedContentEntry.savedContentEntryId) AS COUNT_VALUE FROM SavedContentEntry savedContentEntry WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "savedContentEntry";

	private static final String _FILTER_ENTITY_TABLE = "SavedContentEntry";

	private static final String _ORDER_BY_ENTITY_ALIAS = "savedContentEntry.";

	private static final String _ORDER_BY_ENTITY_TABLE = "SavedContentEntry.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No SavedContentEntry exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No SavedContentEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		SavedContentEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}