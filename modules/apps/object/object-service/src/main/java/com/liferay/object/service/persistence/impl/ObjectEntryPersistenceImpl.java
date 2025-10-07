/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.impl;

import com.liferay.object.exception.NoSuchObjectEntryException;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryTable;
import com.liferay.object.model.impl.ObjectEntryImpl;
import com.liferay.object.model.impl.ObjectEntryModelImpl;
import com.liferay.object.service.persistence.ObjectEntryPersistence;
import com.liferay.object.service.persistence.ObjectEntryUtil;
import com.liferay.object.service.persistence.impl.constants.ObjectPersistenceConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
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

import java.util.Collections;
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
 * The persistence implementation for the object entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = ObjectEntryPersistence.class)
public class ObjectEntryPersistenceImpl
	extends BasePersistenceImpl<ObjectEntry> implements ObjectEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ObjectEntryUtil</code> to access the object entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ObjectEntryImpl.class.getName();

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
	 * Returns all the object entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching object entries
	 */
	@Override
	public List<ObjectEntry> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @return the range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectEntry> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectEntry> orderByComparator,
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

		List<ObjectEntry> list = null;

		if (useFinderCache) {
			list = (List<ObjectEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectEntry objectEntry : list) {
					if (!uuid.equals(objectEntry.getUuid())) {
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

			sb.append(_SQL_SELECT_OBJECTENTRY_WHERE);

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
				sb.append(ObjectEntryModelImpl.ORDER_BY_JPQL);
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

				list = (List<ObjectEntry>)QueryUtil.list(
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
	 * Returns the first object entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByUuid_First(
			String uuid, OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = fetchByUuid_First(uuid, orderByComparator);

		if (objectEntry != null) {
			return objectEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchObjectEntryException(sb.toString());
	}

	/**
	 * Returns the first object entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByUuid_First(
		String uuid, OrderByComparator<ObjectEntry> orderByComparator) {

		List<ObjectEntry> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByUuid_Last(
			String uuid, OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = fetchByUuid_Last(uuid, orderByComparator);

		if (objectEntry != null) {
			return objectEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchObjectEntryException(sb.toString());
	}

	/**
	 * Returns the last object entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByUuid_Last(
		String uuid, OrderByComparator<ObjectEntry> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<ObjectEntry> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object entries before and after the current object entry in the ordered set where uuid = &#63;.
	 *
	 * @param objectEntryId the primary key of the current object entry
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry
	 * @throws NoSuchObjectEntryException if a object entry with the primary key could not be found
	 */
	@Override
	public ObjectEntry[] findByUuid_PrevAndNext(
			long objectEntryId, String uuid,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		uuid = Objects.toString(uuid, "");

		ObjectEntry objectEntry = findByPrimaryKey(objectEntryId);

		Session session = null;

		try {
			session = openSession();

			ObjectEntry[] array = new ObjectEntryImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, objectEntry, uuid, orderByComparator, true);

			array[1] = objectEntry;

			array[2] = getByUuid_PrevAndNext(
				session, objectEntry, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected ObjectEntry getByUuid_PrevAndNext(
		Session session, ObjectEntry objectEntry, String uuid,
		OrderByComparator<ObjectEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_OBJECTENTRY_WHERE);

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
			sb.append(ObjectEntryModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(objectEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (ObjectEntry objectEntry :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(objectEntry);
		}
	}

	/**
	 * Returns the number of object entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object entries
	 */
	@Override
	public int countByUuid(String uuid) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid;

		Object[] finderArgs = new Object[] {uuid};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_OBJECTENTRY_WHERE);

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
		"objectEntry.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(objectEntry.uuid IS NULL OR objectEntry.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;

	/**
	 * Returns the object entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchObjectEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = fetchByUUID_G(uuid, groupId);

		if (objectEntry == null) {
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

			throw new NoSuchObjectEntryException(sb.toString());
		}

		return objectEntry;
	}

	/**
	 * Returns the object entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the object entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByUUID_G(
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

		if (result instanceof ObjectEntry) {
			ObjectEntry objectEntry = (ObjectEntry)result;

			if (!Objects.equals(uuid, objectEntry.getUuid()) ||
				(groupId != objectEntry.getGroupId())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_OBJECTENTRY_WHERE);

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

				List<ObjectEntry> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByUUID_G, finderArgs, list);
					}
				}
				else {
					ObjectEntry objectEntry = list.get(0);

					result = objectEntry;

					cacheResult(objectEntry);
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
			return (ObjectEntry)result;
		}
	}

	/**
	 * Removes the object entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the object entry that was removed
	 */
	@Override
	public ObjectEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = findByUUID_G(uuid, groupId);

		return remove(objectEntry);
	}

	/**
	 * Returns the number of object entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching object entries
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		ObjectEntry objectEntry = fetchByUUID_G(uuid, groupId);

		if (objectEntry == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"objectEntry.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(objectEntry.uuid IS NULL OR objectEntry.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"objectEntry.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the object entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching object entries
	 */
	@Override
	public List<ObjectEntry> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @return the range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectEntry> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectEntry> orderByComparator,
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

		List<ObjectEntry> list = null;

		if (useFinderCache) {
			list = (List<ObjectEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectEntry objectEntry : list) {
					if (!uuid.equals(objectEntry.getUuid()) ||
						(companyId != objectEntry.getCompanyId())) {

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

			sb.append(_SQL_SELECT_OBJECTENTRY_WHERE);

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
				sb.append(ObjectEntryModelImpl.ORDER_BY_JPQL);
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

				list = (List<ObjectEntry>)QueryUtil.list(
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
	 * Returns the first object entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (objectEntry != null) {
			return objectEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchObjectEntryException(sb.toString());
	}

	/**
	 * Returns the first object entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectEntry> orderByComparator) {

		List<ObjectEntry> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (objectEntry != null) {
			return objectEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchObjectEntryException(sb.toString());
	}

	/**
	 * Returns the last object entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<ObjectEntry> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<ObjectEntry> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object entries before and after the current object entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param objectEntryId the primary key of the current object entry
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry
	 * @throws NoSuchObjectEntryException if a object entry with the primary key could not be found
	 */
	@Override
	public ObjectEntry[] findByUuid_C_PrevAndNext(
			long objectEntryId, String uuid, long companyId,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		uuid = Objects.toString(uuid, "");

		ObjectEntry objectEntry = findByPrimaryKey(objectEntryId);

		Session session = null;

		try {
			session = openSession();

			ObjectEntry[] array = new ObjectEntryImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, objectEntry, uuid, companyId, orderByComparator, true);

			array[1] = objectEntry;

			array[2] = getByUuid_C_PrevAndNext(
				session, objectEntry, uuid, companyId, orderByComparator,
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

	protected ObjectEntry getByUuid_C_PrevAndNext(
		Session session, ObjectEntry objectEntry, String uuid, long companyId,
		OrderByComparator<ObjectEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_OBJECTENTRY_WHERE);

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
			sb.append(ObjectEntryModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(objectEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (ObjectEntry objectEntry :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(objectEntry);
		}
	}

	/**
	 * Returns the number of object entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid_C;

		Object[] finderArgs = new Object[] {uuid, companyId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_OBJECTENTRY_WHERE);

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
		"objectEntry.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(objectEntry.uuid IS NULL OR objectEntry.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"objectEntry.companyId = ?";

	private FinderPath _finderPathFetchByHeadObjectEntryId;

	/**
	 * Returns the object entry where headObjectEntryId = &#63; or throws a <code>NoSuchObjectEntryException</code> if it could not be found.
	 *
	 * @param headObjectEntryId the head object entry ID
	 * @return the matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByHeadObjectEntryId(long headObjectEntryId)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = fetchByHeadObjectEntryId(headObjectEntryId);

		if (objectEntry == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("headObjectEntryId=");
			sb.append(headObjectEntryId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchObjectEntryException(sb.toString());
		}

		return objectEntry;
	}

	/**
	 * Returns the object entry where headObjectEntryId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param headObjectEntryId the head object entry ID
	 * @return the matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByHeadObjectEntryId(long headObjectEntryId) {
		return fetchByHeadObjectEntryId(headObjectEntryId, true);
	}

	/**
	 * Returns the object entry where headObjectEntryId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param headObjectEntryId the head object entry ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByHeadObjectEntryId(
		long headObjectEntryId, boolean useFinderCache) {

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {headObjectEntryId};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByHeadObjectEntryId, finderArgs, this);
		}

		if (result instanceof ObjectEntry) {
			ObjectEntry objectEntry = (ObjectEntry)result;

			if (headObjectEntryId != objectEntry.getHeadObjectEntryId()) {
				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_SELECT_OBJECTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_HEADOBJECTENTRYID_HEADOBJECTENTRYID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(headObjectEntryId);

				List<ObjectEntry> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByHeadObjectEntryId, finderArgs,
							list);
					}
				}
				else {
					if (list.size() > 1) {
						Collections.sort(list, Collections.reverseOrder());

						if (_log.isWarnEnabled()) {
							if (!useFinderCache) {
								finderArgs = new Object[] {headObjectEntryId};
							}

							_log.warn(
								"ObjectEntryPersistenceImpl.fetchByHeadObjectEntryId(long, boolean) with parameters (" +
									StringUtil.merge(finderArgs) +
										") yields a result set with more than 1 result. This violates the logical unique restriction. There is no order guarantee on which result is returned by this finder.");
						}
					}

					ObjectEntry objectEntry = list.get(0);

					result = objectEntry;

					cacheResult(objectEntry);
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
			return (ObjectEntry)result;
		}
	}

	/**
	 * Removes the object entry where headObjectEntryId = &#63; from the database.
	 *
	 * @param headObjectEntryId the head object entry ID
	 * @return the object entry that was removed
	 */
	@Override
	public ObjectEntry removeByHeadObjectEntryId(long headObjectEntryId)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = findByHeadObjectEntryId(headObjectEntryId);

		return remove(objectEntry);
	}

	/**
	 * Returns the number of object entries where headObjectEntryId = &#63;.
	 *
	 * @param headObjectEntryId the head object entry ID
	 * @return the number of matching object entries
	 */
	@Override
	public int countByHeadObjectEntryId(long headObjectEntryId) {
		ObjectEntry objectEntry = fetchByHeadObjectEntryId(headObjectEntryId);

		if (objectEntry == null) {
			return 0;
		}

		return 1;
	}

	private static final String
		_FINDER_COLUMN_HEADOBJECTENTRYID_HEADOBJECTENTRYID_2 =
			"objectEntry.headObjectEntryId = ? AND objectEntry.objectEntryId != objectEntry.headObjectEntryId";

	private FinderPath _finderPathWithPaginationFindByObjectDefinitionId;
	private FinderPath _finderPathWithoutPaginationFindByObjectDefinitionId;
	private FinderPath _finderPathCountByObjectDefinitionId;

	/**
	 * Returns all the object entries where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object entries
	 */
	@Override
	public List<ObjectEntry> findByObjectDefinitionId(long objectDefinitionId) {
		return findByObjectDefinitionId(
			objectDefinitionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object entries where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @return the range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end) {

		return findByObjectDefinitionId(objectDefinitionId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object entries where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectEntry> orderByComparator) {

		return findByObjectDefinitionId(
			objectDefinitionId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object entries where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectEntry> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath =
					_finderPathWithoutPaginationFindByObjectDefinitionId;
				finderArgs = new Object[] {objectDefinitionId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByObjectDefinitionId;
			finderArgs = new Object[] {
				objectDefinitionId, start, end, orderByComparator
			};
		}

		List<ObjectEntry> list = null;

		if (useFinderCache) {
			list = (List<ObjectEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectEntry objectEntry : list) {
					if (objectDefinitionId !=
							objectEntry.getObjectDefinitionId()) {

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

			sb.append(_SQL_SELECT_OBJECTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_OBJECTDEFINITIONID_OBJECTDEFINITIONID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId);

				list = (List<ObjectEntry>)QueryUtil.list(
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
	 * Returns the first object entry in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByObjectDefinitionId_First(
			long objectDefinitionId,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = fetchByObjectDefinitionId_First(
			objectDefinitionId, orderByComparator);

		if (objectEntry != null) {
			return objectEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append("}");

		throw new NoSuchObjectEntryException(sb.toString());
	}

	/**
	 * Returns the first object entry in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByObjectDefinitionId_First(
		long objectDefinitionId,
		OrderByComparator<ObjectEntry> orderByComparator) {

		List<ObjectEntry> list = findByObjectDefinitionId(
			objectDefinitionId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object entry in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByObjectDefinitionId_Last(
			long objectDefinitionId,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = fetchByObjectDefinitionId_Last(
			objectDefinitionId, orderByComparator);

		if (objectEntry != null) {
			return objectEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append("}");

		throw new NoSuchObjectEntryException(sb.toString());
	}

	/**
	 * Returns the last object entry in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByObjectDefinitionId_Last(
		long objectDefinitionId,
		OrderByComparator<ObjectEntry> orderByComparator) {

		int count = countByObjectDefinitionId(objectDefinitionId);

		if (count == 0) {
			return null;
		}

		List<ObjectEntry> list = findByObjectDefinitionId(
			objectDefinitionId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object entries before and after the current object entry in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectEntryId the primary key of the current object entry
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry
	 * @throws NoSuchObjectEntryException if a object entry with the primary key could not be found
	 */
	@Override
	public ObjectEntry[] findByObjectDefinitionId_PrevAndNext(
			long objectEntryId, long objectDefinitionId,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = findByPrimaryKey(objectEntryId);

		Session session = null;

		try {
			session = openSession();

			ObjectEntry[] array = new ObjectEntryImpl[3];

			array[0] = getByObjectDefinitionId_PrevAndNext(
				session, objectEntry, objectDefinitionId, orderByComparator,
				true);

			array[1] = objectEntry;

			array[2] = getByObjectDefinitionId_PrevAndNext(
				session, objectEntry, objectDefinitionId, orderByComparator,
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

	protected ObjectEntry getByObjectDefinitionId_PrevAndNext(
		Session session, ObjectEntry objectEntry, long objectDefinitionId,
		OrderByComparator<ObjectEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_OBJECTENTRY_WHERE);

		sb.append(_FINDER_COLUMN_OBJECTDEFINITIONID_OBJECTDEFINITIONID_2);

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
			sb.append(ObjectEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(objectDefinitionId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(objectEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object entries where objectDefinitionId = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 */
	@Override
	public void removeByObjectDefinitionId(long objectDefinitionId) {
		for (ObjectEntry objectEntry :
				findByObjectDefinitionId(
					objectDefinitionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(objectEntry);
		}
	}

	/**
	 * Returns the number of object entries where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object entries
	 */
	@Override
	public int countByObjectDefinitionId(long objectDefinitionId) {
		FinderPath finderPath = _finderPathCountByObjectDefinitionId;

		Object[] finderArgs = new Object[] {objectDefinitionId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_OBJECTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_OBJECTDEFINITIONID_OBJECTDEFINITIONID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId);

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

	private static final String
		_FINDER_COLUMN_OBJECTDEFINITIONID_OBJECTDEFINITIONID_2 =
			"objectEntry.objectDefinitionId = ? AND objectEntry.objectEntryId = objectEntry.headObjectEntryId";

	private FinderPath _finderPathWithPaginationFindByG_ODI;
	private FinderPath _finderPathWithoutPaginationFindByG_ODI;
	private FinderPath _finderPathCountByG_ODI;

	/**
	 * Returns all the object entries where groupId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object entries
	 */
	@Override
	public List<ObjectEntry> findByG_ODI(
		long groupId, long objectDefinitionId) {

		return findByG_ODI(
			groupId, objectDefinitionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the object entries where groupId = &#63; and objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @return the range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByG_ODI(
		long groupId, long objectDefinitionId, int start, int end) {

		return findByG_ODI(groupId, objectDefinitionId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object entries where groupId = &#63; and objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByG_ODI(
		long groupId, long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectEntry> orderByComparator) {

		return findByG_ODI(
			groupId, objectDefinitionId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object entries where groupId = &#63; and objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByG_ODI(
		long groupId, long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectEntry> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByG_ODI;
				finderArgs = new Object[] {groupId, objectDefinitionId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByG_ODI;
			finderArgs = new Object[] {
				groupId, objectDefinitionId, start, end, orderByComparator
			};
		}

		List<ObjectEntry> list = null;

		if (useFinderCache) {
			list = (List<ObjectEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectEntry objectEntry : list) {
					if ((groupId != objectEntry.getGroupId()) ||
						(objectDefinitionId !=
							objectEntry.getObjectDefinitionId())) {

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

			sb.append(_SQL_SELECT_OBJECTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_G_ODI_GROUPID_2);

			sb.append(_FINDER_COLUMN_G_ODI_OBJECTDEFINITIONID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				queryPos.add(objectDefinitionId);

				list = (List<ObjectEntry>)QueryUtil.list(
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
	 * Returns the first object entry in the ordered set where groupId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByG_ODI_First(
			long groupId, long objectDefinitionId,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = fetchByG_ODI_First(
			groupId, objectDefinitionId, orderByComparator);

		if (objectEntry != null) {
			return objectEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append("}");

		throw new NoSuchObjectEntryException(sb.toString());
	}

	/**
	 * Returns the first object entry in the ordered set where groupId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByG_ODI_First(
		long groupId, long objectDefinitionId,
		OrderByComparator<ObjectEntry> orderByComparator) {

		List<ObjectEntry> list = findByG_ODI(
			groupId, objectDefinitionId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object entry in the ordered set where groupId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByG_ODI_Last(
			long groupId, long objectDefinitionId,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = fetchByG_ODI_Last(
			groupId, objectDefinitionId, orderByComparator);

		if (objectEntry != null) {
			return objectEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append("}");

		throw new NoSuchObjectEntryException(sb.toString());
	}

	/**
	 * Returns the last object entry in the ordered set where groupId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByG_ODI_Last(
		long groupId, long objectDefinitionId,
		OrderByComparator<ObjectEntry> orderByComparator) {

		int count = countByG_ODI(groupId, objectDefinitionId);

		if (count == 0) {
			return null;
		}

		List<ObjectEntry> list = findByG_ODI(
			groupId, objectDefinitionId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object entries before and after the current object entry in the ordered set where groupId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param objectEntryId the primary key of the current object entry
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry
	 * @throws NoSuchObjectEntryException if a object entry with the primary key could not be found
	 */
	@Override
	public ObjectEntry[] findByG_ODI_PrevAndNext(
			long objectEntryId, long groupId, long objectDefinitionId,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = findByPrimaryKey(objectEntryId);

		Session session = null;

		try {
			session = openSession();

			ObjectEntry[] array = new ObjectEntryImpl[3];

			array[0] = getByG_ODI_PrevAndNext(
				session, objectEntry, groupId, objectDefinitionId,
				orderByComparator, true);

			array[1] = objectEntry;

			array[2] = getByG_ODI_PrevAndNext(
				session, objectEntry, groupId, objectDefinitionId,
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

	protected ObjectEntry getByG_ODI_PrevAndNext(
		Session session, ObjectEntry objectEntry, long groupId,
		long objectDefinitionId,
		OrderByComparator<ObjectEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_OBJECTENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_ODI_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_ODI_OBJECTDEFINITIONID_2);

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
			sb.append(ObjectEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(objectDefinitionId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(objectEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object entries where groupId = &#63; and objectDefinitionId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 */
	@Override
	public void removeByG_ODI(long groupId, long objectDefinitionId) {
		for (ObjectEntry objectEntry :
				findByG_ODI(
					groupId, objectDefinitionId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(objectEntry);
		}
	}

	/**
	 * Returns the number of object entries where groupId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object entries
	 */
	@Override
	public int countByG_ODI(long groupId, long objectDefinitionId) {
		FinderPath finderPath = _finderPathCountByG_ODI;

		Object[] finderArgs = new Object[] {groupId, objectDefinitionId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_OBJECTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_G_ODI_GROUPID_2);

			sb.append(_FINDER_COLUMN_G_ODI_OBJECTDEFINITIONID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				queryPos.add(objectDefinitionId);

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

	private static final String _FINDER_COLUMN_G_ODI_GROUPID_2 =
		"objectEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_ODI_OBJECTDEFINITIONID_2 =
		"objectEntry.objectDefinitionId = ? AND objectEntry.objectEntryId = objectEntry.headObjectEntryId";

	private FinderPath _finderPathWithPaginationFindByG_OEFI;
	private FinderPath _finderPathWithoutPaginationFindByG_OEFI;
	private FinderPath _finderPathCountByG_OEFI;

	/**
	 * Returns all the object entries where groupId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @return the matching object entries
	 */
	@Override
	public List<ObjectEntry> findByG_OEFI(
		long groupId, long objectEntryFolderId) {

		return findByG_OEFI(
			groupId, objectEntryFolderId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the object entries where groupId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @return the range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByG_OEFI(
		long groupId, long objectEntryFolderId, int start, int end) {

		return findByG_OEFI(groupId, objectEntryFolderId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object entries where groupId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByG_OEFI(
		long groupId, long objectEntryFolderId, int start, int end,
		OrderByComparator<ObjectEntry> orderByComparator) {

		return findByG_OEFI(
			groupId, objectEntryFolderId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object entries where groupId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByG_OEFI(
		long groupId, long objectEntryFolderId, int start, int end,
		OrderByComparator<ObjectEntry> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByG_OEFI;
				finderArgs = new Object[] {groupId, objectEntryFolderId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByG_OEFI;
			finderArgs = new Object[] {
				groupId, objectEntryFolderId, start, end, orderByComparator
			};
		}

		List<ObjectEntry> list = null;

		if (useFinderCache) {
			list = (List<ObjectEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectEntry objectEntry : list) {
					if ((groupId != objectEntry.getGroupId()) ||
						(objectEntryFolderId !=
							objectEntry.getObjectEntryFolderId())) {

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

			sb.append(_SQL_SELECT_OBJECTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_G_OEFI_GROUPID_2);

			sb.append(_FINDER_COLUMN_G_OEFI_OBJECTENTRYFOLDERID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				queryPos.add(objectEntryFolderId);

				list = (List<ObjectEntry>)QueryUtil.list(
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
	 * Returns the first object entry in the ordered set where groupId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByG_OEFI_First(
			long groupId, long objectEntryFolderId,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = fetchByG_OEFI_First(
			groupId, objectEntryFolderId, orderByComparator);

		if (objectEntry != null) {
			return objectEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", objectEntryFolderId=");
		sb.append(objectEntryFolderId);

		sb.append("}");

		throw new NoSuchObjectEntryException(sb.toString());
	}

	/**
	 * Returns the first object entry in the ordered set where groupId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByG_OEFI_First(
		long groupId, long objectEntryFolderId,
		OrderByComparator<ObjectEntry> orderByComparator) {

		List<ObjectEntry> list = findByG_OEFI(
			groupId, objectEntryFolderId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object entry in the ordered set where groupId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByG_OEFI_Last(
			long groupId, long objectEntryFolderId,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = fetchByG_OEFI_Last(
			groupId, objectEntryFolderId, orderByComparator);

		if (objectEntry != null) {
			return objectEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", objectEntryFolderId=");
		sb.append(objectEntryFolderId);

		sb.append("}");

		throw new NoSuchObjectEntryException(sb.toString());
	}

	/**
	 * Returns the last object entry in the ordered set where groupId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByG_OEFI_Last(
		long groupId, long objectEntryFolderId,
		OrderByComparator<ObjectEntry> orderByComparator) {

		int count = countByG_OEFI(groupId, objectEntryFolderId);

		if (count == 0) {
			return null;
		}

		List<ObjectEntry> list = findByG_OEFI(
			groupId, objectEntryFolderId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object entries before and after the current object entry in the ordered set where groupId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * @param objectEntryId the primary key of the current object entry
	 * @param groupId the group ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry
	 * @throws NoSuchObjectEntryException if a object entry with the primary key could not be found
	 */
	@Override
	public ObjectEntry[] findByG_OEFI_PrevAndNext(
			long objectEntryId, long groupId, long objectEntryFolderId,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = findByPrimaryKey(objectEntryId);

		Session session = null;

		try {
			session = openSession();

			ObjectEntry[] array = new ObjectEntryImpl[3];

			array[0] = getByG_OEFI_PrevAndNext(
				session, objectEntry, groupId, objectEntryFolderId,
				orderByComparator, true);

			array[1] = objectEntry;

			array[2] = getByG_OEFI_PrevAndNext(
				session, objectEntry, groupId, objectEntryFolderId,
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

	protected ObjectEntry getByG_OEFI_PrevAndNext(
		Session session, ObjectEntry objectEntry, long groupId,
		long objectEntryFolderId,
		OrderByComparator<ObjectEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_OBJECTENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_OEFI_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_OEFI_OBJECTENTRYFOLDERID_2);

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
			sb.append(ObjectEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(objectEntryFolderId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(objectEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object entries where groupId = &#63; and objectEntryFolderId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param objectEntryFolderId the object entry folder ID
	 */
	@Override
	public void removeByG_OEFI(long groupId, long objectEntryFolderId) {
		for (ObjectEntry objectEntry :
				findByG_OEFI(
					groupId, objectEntryFolderId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(objectEntry);
		}
	}

	/**
	 * Returns the number of object entries where groupId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @return the number of matching object entries
	 */
	@Override
	public int countByG_OEFI(long groupId, long objectEntryFolderId) {
		FinderPath finderPath = _finderPathCountByG_OEFI;

		Object[] finderArgs = new Object[] {groupId, objectEntryFolderId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_OBJECTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_G_OEFI_GROUPID_2);

			sb.append(_FINDER_COLUMN_G_OEFI_OBJECTENTRYFOLDERID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				queryPos.add(objectEntryFolderId);

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

	private static final String _FINDER_COLUMN_G_OEFI_GROUPID_2 =
		"objectEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_OEFI_OBJECTENTRYFOLDERID_2 =
		"objectEntry.objectEntryFolderId = ? AND objectEntry.objectEntryId = objectEntry.headObjectEntryId";

	private FinderPath _finderPathWithPaginationFindByU_ODI;
	private FinderPath _finderPathWithoutPaginationFindByU_ODI;
	private FinderPath _finderPathCountByU_ODI;

	/**
	 * Returns all the object entries where userId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param userId the user ID
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object entries
	 */
	@Override
	public List<ObjectEntry> findByU_ODI(long userId, long objectDefinitionId) {
		return findByU_ODI(
			userId, objectDefinitionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the object entries where userId = &#63; and objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @return the range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByU_ODI(
		long userId, long objectDefinitionId, int start, int end) {

		return findByU_ODI(userId, objectDefinitionId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object entries where userId = &#63; and objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByU_ODI(
		long userId, long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectEntry> orderByComparator) {

		return findByU_ODI(
			userId, objectDefinitionId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object entries where userId = &#63; and objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByU_ODI(
		long userId, long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectEntry> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByU_ODI;
				finderArgs = new Object[] {userId, objectDefinitionId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByU_ODI;
			finderArgs = new Object[] {
				userId, objectDefinitionId, start, end, orderByComparator
			};
		}

		List<ObjectEntry> list = null;

		if (useFinderCache) {
			list = (List<ObjectEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectEntry objectEntry : list) {
					if ((userId != objectEntry.getUserId()) ||
						(objectDefinitionId !=
							objectEntry.getObjectDefinitionId())) {

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

			sb.append(_SQL_SELECT_OBJECTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_U_ODI_USERID_2);

			sb.append(_FINDER_COLUMN_U_ODI_OBJECTDEFINITIONID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(userId);

				queryPos.add(objectDefinitionId);

				list = (List<ObjectEntry>)QueryUtil.list(
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
	 * Returns the first object entry in the ordered set where userId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param userId the user ID
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByU_ODI_First(
			long userId, long objectDefinitionId,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = fetchByU_ODI_First(
			userId, objectDefinitionId, orderByComparator);

		if (objectEntry != null) {
			return objectEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append(", objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append("}");

		throw new NoSuchObjectEntryException(sb.toString());
	}

	/**
	 * Returns the first object entry in the ordered set where userId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param userId the user ID
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByU_ODI_First(
		long userId, long objectDefinitionId,
		OrderByComparator<ObjectEntry> orderByComparator) {

		List<ObjectEntry> list = findByU_ODI(
			userId, objectDefinitionId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object entry in the ordered set where userId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param userId the user ID
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByU_ODI_Last(
			long userId, long objectDefinitionId,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = fetchByU_ODI_Last(
			userId, objectDefinitionId, orderByComparator);

		if (objectEntry != null) {
			return objectEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append(", objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append("}");

		throw new NoSuchObjectEntryException(sb.toString());
	}

	/**
	 * Returns the last object entry in the ordered set where userId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param userId the user ID
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByU_ODI_Last(
		long userId, long objectDefinitionId,
		OrderByComparator<ObjectEntry> orderByComparator) {

		int count = countByU_ODI(userId, objectDefinitionId);

		if (count == 0) {
			return null;
		}

		List<ObjectEntry> list = findByU_ODI(
			userId, objectDefinitionId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object entries before and after the current object entry in the ordered set where userId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param objectEntryId the primary key of the current object entry
	 * @param userId the user ID
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry
	 * @throws NoSuchObjectEntryException if a object entry with the primary key could not be found
	 */
	@Override
	public ObjectEntry[] findByU_ODI_PrevAndNext(
			long objectEntryId, long userId, long objectDefinitionId,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = findByPrimaryKey(objectEntryId);

		Session session = null;

		try {
			session = openSession();

			ObjectEntry[] array = new ObjectEntryImpl[3];

			array[0] = getByU_ODI_PrevAndNext(
				session, objectEntry, userId, objectDefinitionId,
				orderByComparator, true);

			array[1] = objectEntry;

			array[2] = getByU_ODI_PrevAndNext(
				session, objectEntry, userId, objectDefinitionId,
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

	protected ObjectEntry getByU_ODI_PrevAndNext(
		Session session, ObjectEntry objectEntry, long userId,
		long objectDefinitionId,
		OrderByComparator<ObjectEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_OBJECTENTRY_WHERE);

		sb.append(_FINDER_COLUMN_U_ODI_USERID_2);

		sb.append(_FINDER_COLUMN_U_ODI_OBJECTDEFINITIONID_2);

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
			sb.append(ObjectEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(userId);

		queryPos.add(objectDefinitionId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(objectEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object entries where userId = &#63; and objectDefinitionId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param objectDefinitionId the object definition ID
	 */
	@Override
	public void removeByU_ODI(long userId, long objectDefinitionId) {
		for (ObjectEntry objectEntry :
				findByU_ODI(
					userId, objectDefinitionId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(objectEntry);
		}
	}

	/**
	 * Returns the number of object entries where userId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param userId the user ID
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object entries
	 */
	@Override
	public int countByU_ODI(long userId, long objectDefinitionId) {
		FinderPath finderPath = _finderPathCountByU_ODI;

		Object[] finderArgs = new Object[] {userId, objectDefinitionId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_OBJECTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_U_ODI_USERID_2);

			sb.append(_FINDER_COLUMN_U_ODI_OBJECTDEFINITIONID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(userId);

				queryPos.add(objectDefinitionId);

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

	private static final String _FINDER_COLUMN_U_ODI_USERID_2 =
		"objectEntry.userId = ? AND ";

	private static final String _FINDER_COLUMN_U_ODI_OBJECTDEFINITIONID_2 =
		"objectEntry.objectDefinitionId = ? AND objectEntry.objectEntryId = objectEntry.headObjectEntryId";

	private FinderPath _finderPathWithPaginationFindByODI_NotS;
	private FinderPath _finderPathWithPaginationCountByODI_NotS;

	/**
	 * Returns all the object entries where objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @return the matching object entries
	 */
	@Override
	public List<ObjectEntry> findByODI_NotS(
		long objectDefinitionId, int status) {

		return findByODI_NotS(
			objectDefinitionId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the object entries where objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @return the range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByODI_NotS(
		long objectDefinitionId, int status, int start, int end) {

		return findByODI_NotS(objectDefinitionId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object entries where objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByODI_NotS(
		long objectDefinitionId, int status, int start, int end,
		OrderByComparator<ObjectEntry> orderByComparator) {

		return findByODI_NotS(
			objectDefinitionId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object entries where objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByODI_NotS(
		long objectDefinitionId, int status, int start, int end,
		OrderByComparator<ObjectEntry> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		finderPath = _finderPathWithPaginationFindByODI_NotS;
		finderArgs = new Object[] {
			objectDefinitionId, status, start, end, orderByComparator
		};

		List<ObjectEntry> list = null;

		if (useFinderCache) {
			list = (List<ObjectEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectEntry objectEntry : list) {
					if ((objectDefinitionId !=
							objectEntry.getObjectDefinitionId()) ||
						(status == objectEntry.getStatus())) {

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

			sb.append(_SQL_SELECT_OBJECTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_ODI_NOTS_OBJECTDEFINITIONID_2);

			sb.append(_FINDER_COLUMN_ODI_NOTS_STATUS_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId);

				queryPos.add(status);

				list = (List<ObjectEntry>)QueryUtil.list(
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
	 * Returns the first object entry in the ordered set where objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByODI_NotS_First(
			long objectDefinitionId, int status,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = fetchByODI_NotS_First(
			objectDefinitionId, status, orderByComparator);

		if (objectEntry != null) {
			return objectEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchObjectEntryException(sb.toString());
	}

	/**
	 * Returns the first object entry in the ordered set where objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByODI_NotS_First(
		long objectDefinitionId, int status,
		OrderByComparator<ObjectEntry> orderByComparator) {

		List<ObjectEntry> list = findByODI_NotS(
			objectDefinitionId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object entry in the ordered set where objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByODI_NotS_Last(
			long objectDefinitionId, int status,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = fetchByODI_NotS_Last(
			objectDefinitionId, status, orderByComparator);

		if (objectEntry != null) {
			return objectEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchObjectEntryException(sb.toString());
	}

	/**
	 * Returns the last object entry in the ordered set where objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByODI_NotS_Last(
		long objectDefinitionId, int status,
		OrderByComparator<ObjectEntry> orderByComparator) {

		int count = countByODI_NotS(objectDefinitionId, status);

		if (count == 0) {
			return null;
		}

		List<ObjectEntry> list = findByODI_NotS(
			objectDefinitionId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object entries before and after the current object entry in the ordered set where objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * @param objectEntryId the primary key of the current object entry
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry
	 * @throws NoSuchObjectEntryException if a object entry with the primary key could not be found
	 */
	@Override
	public ObjectEntry[] findByODI_NotS_PrevAndNext(
			long objectEntryId, long objectDefinitionId, int status,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = findByPrimaryKey(objectEntryId);

		Session session = null;

		try {
			session = openSession();

			ObjectEntry[] array = new ObjectEntryImpl[3];

			array[0] = getByODI_NotS_PrevAndNext(
				session, objectEntry, objectDefinitionId, status,
				orderByComparator, true);

			array[1] = objectEntry;

			array[2] = getByODI_NotS_PrevAndNext(
				session, objectEntry, objectDefinitionId, status,
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

	protected ObjectEntry getByODI_NotS_PrevAndNext(
		Session session, ObjectEntry objectEntry, long objectDefinitionId,
		int status, OrderByComparator<ObjectEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTENTRY_WHERE);

		sb.append(_FINDER_COLUMN_ODI_NOTS_OBJECTDEFINITIONID_2);

		sb.append(_FINDER_COLUMN_ODI_NOTS_STATUS_2);

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
			sb.append(ObjectEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(objectDefinitionId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(objectEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object entries where objectDefinitionId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 */
	@Override
	public void removeByODI_NotS(long objectDefinitionId, int status) {
		for (ObjectEntry objectEntry :
				findByODI_NotS(
					objectDefinitionId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(objectEntry);
		}
	}

	/**
	 * Returns the number of object entries where objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @return the number of matching object entries
	 */
	@Override
	public int countByODI_NotS(long objectDefinitionId, int status) {
		FinderPath finderPath = _finderPathWithPaginationCountByODI_NotS;

		Object[] finderArgs = new Object[] {objectDefinitionId, status};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_OBJECTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_ODI_NOTS_OBJECTDEFINITIONID_2);

			sb.append(_FINDER_COLUMN_ODI_NOTS_STATUS_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId);

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

	private static final String _FINDER_COLUMN_ODI_NOTS_OBJECTDEFINITIONID_2 =
		"objectEntry.objectDefinitionId = ? AND ";

	private static final String _FINDER_COLUMN_ODI_NOTS_STATUS_2 =
		"objectEntry.status != ? AND objectEntry.objectEntryId = objectEntry.headObjectEntryId";

	private FinderPath _finderPathWithPaginationFindByROEI_NotS;
	private FinderPath _finderPathWithPaginationCountByROEI_NotS;

	/**
	 * Returns all the object entries where rootObjectEntryId = &#63; and status &ne; &#63;.
	 *
	 * @param rootObjectEntryId the root object entry ID
	 * @param status the status
	 * @return the matching object entries
	 */
	@Override
	public List<ObjectEntry> findByROEI_NotS(
		long rootObjectEntryId, int status) {

		return findByROEI_NotS(
			rootObjectEntryId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the object entries where rootObjectEntryId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param rootObjectEntryId the root object entry ID
	 * @param status the status
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @return the range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByROEI_NotS(
		long rootObjectEntryId, int status, int start, int end) {

		return findByROEI_NotS(rootObjectEntryId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object entries where rootObjectEntryId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param rootObjectEntryId the root object entry ID
	 * @param status the status
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByROEI_NotS(
		long rootObjectEntryId, int status, int start, int end,
		OrderByComparator<ObjectEntry> orderByComparator) {

		return findByROEI_NotS(
			rootObjectEntryId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object entries where rootObjectEntryId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param rootObjectEntryId the root object entry ID
	 * @param status the status
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByROEI_NotS(
		long rootObjectEntryId, int status, int start, int end,
		OrderByComparator<ObjectEntry> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		finderPath = _finderPathWithPaginationFindByROEI_NotS;
		finderArgs = new Object[] {
			rootObjectEntryId, status, start, end, orderByComparator
		};

		List<ObjectEntry> list = null;

		if (useFinderCache) {
			list = (List<ObjectEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectEntry objectEntry : list) {
					if ((rootObjectEntryId !=
							objectEntry.getRootObjectEntryId()) ||
						(status == objectEntry.getStatus())) {

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

			sb.append(_SQL_SELECT_OBJECTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_ROEI_NOTS_ROOTOBJECTENTRYID_2);

			sb.append(_FINDER_COLUMN_ROEI_NOTS_STATUS_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(rootObjectEntryId);

				queryPos.add(status);

				list = (List<ObjectEntry>)QueryUtil.list(
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
	 * Returns the first object entry in the ordered set where rootObjectEntryId = &#63; and status &ne; &#63;.
	 *
	 * @param rootObjectEntryId the root object entry ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByROEI_NotS_First(
			long rootObjectEntryId, int status,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = fetchByROEI_NotS_First(
			rootObjectEntryId, status, orderByComparator);

		if (objectEntry != null) {
			return objectEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("rootObjectEntryId=");
		sb.append(rootObjectEntryId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchObjectEntryException(sb.toString());
	}

	/**
	 * Returns the first object entry in the ordered set where rootObjectEntryId = &#63; and status &ne; &#63;.
	 *
	 * @param rootObjectEntryId the root object entry ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByROEI_NotS_First(
		long rootObjectEntryId, int status,
		OrderByComparator<ObjectEntry> orderByComparator) {

		List<ObjectEntry> list = findByROEI_NotS(
			rootObjectEntryId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object entry in the ordered set where rootObjectEntryId = &#63; and status &ne; &#63;.
	 *
	 * @param rootObjectEntryId the root object entry ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByROEI_NotS_Last(
			long rootObjectEntryId, int status,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = fetchByROEI_NotS_Last(
			rootObjectEntryId, status, orderByComparator);

		if (objectEntry != null) {
			return objectEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("rootObjectEntryId=");
		sb.append(rootObjectEntryId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchObjectEntryException(sb.toString());
	}

	/**
	 * Returns the last object entry in the ordered set where rootObjectEntryId = &#63; and status &ne; &#63;.
	 *
	 * @param rootObjectEntryId the root object entry ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByROEI_NotS_Last(
		long rootObjectEntryId, int status,
		OrderByComparator<ObjectEntry> orderByComparator) {

		int count = countByROEI_NotS(rootObjectEntryId, status);

		if (count == 0) {
			return null;
		}

		List<ObjectEntry> list = findByROEI_NotS(
			rootObjectEntryId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object entries before and after the current object entry in the ordered set where rootObjectEntryId = &#63; and status &ne; &#63;.
	 *
	 * @param objectEntryId the primary key of the current object entry
	 * @param rootObjectEntryId the root object entry ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry
	 * @throws NoSuchObjectEntryException if a object entry with the primary key could not be found
	 */
	@Override
	public ObjectEntry[] findByROEI_NotS_PrevAndNext(
			long objectEntryId, long rootObjectEntryId, int status,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = findByPrimaryKey(objectEntryId);

		Session session = null;

		try {
			session = openSession();

			ObjectEntry[] array = new ObjectEntryImpl[3];

			array[0] = getByROEI_NotS_PrevAndNext(
				session, objectEntry, rootObjectEntryId, status,
				orderByComparator, true);

			array[1] = objectEntry;

			array[2] = getByROEI_NotS_PrevAndNext(
				session, objectEntry, rootObjectEntryId, status,
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

	protected ObjectEntry getByROEI_NotS_PrevAndNext(
		Session session, ObjectEntry objectEntry, long rootObjectEntryId,
		int status, OrderByComparator<ObjectEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTENTRY_WHERE);

		sb.append(_FINDER_COLUMN_ROEI_NOTS_ROOTOBJECTENTRYID_2);

		sb.append(_FINDER_COLUMN_ROEI_NOTS_STATUS_2);

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
			sb.append(ObjectEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(rootObjectEntryId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(objectEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object entries where rootObjectEntryId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param rootObjectEntryId the root object entry ID
	 * @param status the status
	 */
	@Override
	public void removeByROEI_NotS(long rootObjectEntryId, int status) {
		for (ObjectEntry objectEntry :
				findByROEI_NotS(
					rootObjectEntryId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(objectEntry);
		}
	}

	/**
	 * Returns the number of object entries where rootObjectEntryId = &#63; and status &ne; &#63;.
	 *
	 * @param rootObjectEntryId the root object entry ID
	 * @param status the status
	 * @return the number of matching object entries
	 */
	@Override
	public int countByROEI_NotS(long rootObjectEntryId, int status) {
		FinderPath finderPath = _finderPathWithPaginationCountByROEI_NotS;

		Object[] finderArgs = new Object[] {rootObjectEntryId, status};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_OBJECTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_ROEI_NOTS_ROOTOBJECTENTRYID_2);

			sb.append(_FINDER_COLUMN_ROEI_NOTS_STATUS_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(rootObjectEntryId);

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

	private static final String _FINDER_COLUMN_ROEI_NOTS_ROOTOBJECTENTRYID_2 =
		"objectEntry.rootObjectEntryId = ? AND ";

	private static final String _FINDER_COLUMN_ROEI_NOTS_STATUS_2 =
		"objectEntry.status != ? AND objectEntry.objectEntryId = objectEntry.headObjectEntryId";

	private FinderPath _finderPathWithPaginationFindByG_C_OEFI;
	private FinderPath _finderPathWithoutPaginationFindByG_C_OEFI;
	private FinderPath _finderPathCountByG_C_OEFI;

	/**
	 * Returns all the object entries where groupId = &#63; and companyId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @return the matching object entries
	 */
	@Override
	public List<ObjectEntry> findByG_C_OEFI(
		long groupId, long companyId, long objectEntryFolderId) {

		return findByG_C_OEFI(
			groupId, companyId, objectEntryFolderId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object entries where groupId = &#63; and companyId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @return the range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByG_C_OEFI(
		long groupId, long companyId, long objectEntryFolderId, int start,
		int end) {

		return findByG_C_OEFI(
			groupId, companyId, objectEntryFolderId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object entries where groupId = &#63; and companyId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByG_C_OEFI(
		long groupId, long companyId, long objectEntryFolderId, int start,
		int end, OrderByComparator<ObjectEntry> orderByComparator) {

		return findByG_C_OEFI(
			groupId, companyId, objectEntryFolderId, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object entries where groupId = &#63; and companyId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByG_C_OEFI(
		long groupId, long companyId, long objectEntryFolderId, int start,
		int end, OrderByComparator<ObjectEntry> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByG_C_OEFI;
				finderArgs = new Object[] {
					groupId, companyId, objectEntryFolderId
				};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByG_C_OEFI;
			finderArgs = new Object[] {
				groupId, companyId, objectEntryFolderId, start, end,
				orderByComparator
			};
		}

		List<ObjectEntry> list = null;

		if (useFinderCache) {
			list = (List<ObjectEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectEntry objectEntry : list) {
					if ((groupId != objectEntry.getGroupId()) ||
						(companyId != objectEntry.getCompanyId()) ||
						(objectEntryFolderId !=
							objectEntry.getObjectEntryFolderId())) {

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

			sb.append(_SQL_SELECT_OBJECTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_G_C_OEFI_GROUPID_2);

			sb.append(_FINDER_COLUMN_G_C_OEFI_COMPANYID_2);

			sb.append(_FINDER_COLUMN_G_C_OEFI_OBJECTENTRYFOLDERID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				queryPos.add(companyId);

				queryPos.add(objectEntryFolderId);

				list = (List<ObjectEntry>)QueryUtil.list(
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
	 * Returns the first object entry in the ordered set where groupId = &#63; and companyId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByG_C_OEFI_First(
			long groupId, long companyId, long objectEntryFolderId,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = fetchByG_C_OEFI_First(
			groupId, companyId, objectEntryFolderId, orderByComparator);

		if (objectEntry != null) {
			return objectEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append(", objectEntryFolderId=");
		sb.append(objectEntryFolderId);

		sb.append("}");

		throw new NoSuchObjectEntryException(sb.toString());
	}

	/**
	 * Returns the first object entry in the ordered set where groupId = &#63; and companyId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByG_C_OEFI_First(
		long groupId, long companyId, long objectEntryFolderId,
		OrderByComparator<ObjectEntry> orderByComparator) {

		List<ObjectEntry> list = findByG_C_OEFI(
			groupId, companyId, objectEntryFolderId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object entry in the ordered set where groupId = &#63; and companyId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByG_C_OEFI_Last(
			long groupId, long companyId, long objectEntryFolderId,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = fetchByG_C_OEFI_Last(
			groupId, companyId, objectEntryFolderId, orderByComparator);

		if (objectEntry != null) {
			return objectEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append(", objectEntryFolderId=");
		sb.append(objectEntryFolderId);

		sb.append("}");

		throw new NoSuchObjectEntryException(sb.toString());
	}

	/**
	 * Returns the last object entry in the ordered set where groupId = &#63; and companyId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByG_C_OEFI_Last(
		long groupId, long companyId, long objectEntryFolderId,
		OrderByComparator<ObjectEntry> orderByComparator) {

		int count = countByG_C_OEFI(groupId, companyId, objectEntryFolderId);

		if (count == 0) {
			return null;
		}

		List<ObjectEntry> list = findByG_C_OEFI(
			groupId, companyId, objectEntryFolderId, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object entries before and after the current object entry in the ordered set where groupId = &#63; and companyId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * @param objectEntryId the primary key of the current object entry
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry
	 * @throws NoSuchObjectEntryException if a object entry with the primary key could not be found
	 */
	@Override
	public ObjectEntry[] findByG_C_OEFI_PrevAndNext(
			long objectEntryId, long groupId, long companyId,
			long objectEntryFolderId,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = findByPrimaryKey(objectEntryId);

		Session session = null;

		try {
			session = openSession();

			ObjectEntry[] array = new ObjectEntryImpl[3];

			array[0] = getByG_C_OEFI_PrevAndNext(
				session, objectEntry, groupId, companyId, objectEntryFolderId,
				orderByComparator, true);

			array[1] = objectEntry;

			array[2] = getByG_C_OEFI_PrevAndNext(
				session, objectEntry, groupId, companyId, objectEntryFolderId,
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

	protected ObjectEntry getByG_C_OEFI_PrevAndNext(
		Session session, ObjectEntry objectEntry, long groupId, long companyId,
		long objectEntryFolderId,
		OrderByComparator<ObjectEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_OBJECTENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_C_OEFI_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_OEFI_COMPANYID_2);

		sb.append(_FINDER_COLUMN_G_C_OEFI_OBJECTENTRYFOLDERID_2);

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
			sb.append(ObjectEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(companyId);

		queryPos.add(objectEntryFolderId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(objectEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object entries where groupId = &#63; and companyId = &#63; and objectEntryFolderId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectEntryFolderId the object entry folder ID
	 */
	@Override
	public void removeByG_C_OEFI(
		long groupId, long companyId, long objectEntryFolderId) {

		for (ObjectEntry objectEntry :
				findByG_C_OEFI(
					groupId, companyId, objectEntryFolderId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(objectEntry);
		}
	}

	/**
	 * Returns the number of object entries where groupId = &#63; and companyId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @return the number of matching object entries
	 */
	@Override
	public int countByG_C_OEFI(
		long groupId, long companyId, long objectEntryFolderId) {

		FinderPath finderPath = _finderPathCountByG_C_OEFI;

		Object[] finderArgs = new Object[] {
			groupId, companyId, objectEntryFolderId
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_COUNT_OBJECTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_G_C_OEFI_GROUPID_2);

			sb.append(_FINDER_COLUMN_G_C_OEFI_COMPANYID_2);

			sb.append(_FINDER_COLUMN_G_C_OEFI_OBJECTENTRYFOLDERID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				queryPos.add(companyId);

				queryPos.add(objectEntryFolderId);

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

	private static final String _FINDER_COLUMN_G_C_OEFI_GROUPID_2 =
		"objectEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_OEFI_COMPANYID_2 =
		"objectEntry.companyId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_OEFI_OBJECTENTRYFOLDERID_2 =
		"objectEntry.objectEntryFolderId = ? AND objectEntry.objectEntryId = objectEntry.headObjectEntryId";

	private FinderPath _finderPathWithPaginationFindByG_ODI_S;
	private FinderPath _finderPathWithoutPaginationFindByG_ODI_S;
	private FinderPath _finderPathCountByG_ODI_S;

	/**
	 * Returns all the object entries where groupId = &#63; and objectDefinitionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @return the matching object entries
	 */
	@Override
	public List<ObjectEntry> findByG_ODI_S(
		long groupId, long objectDefinitionId, int status) {

		return findByG_ODI_S(
			groupId, objectDefinitionId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object entries where groupId = &#63; and objectDefinitionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @return the range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByG_ODI_S(
		long groupId, long objectDefinitionId, int status, int start, int end) {

		return findByG_ODI_S(
			groupId, objectDefinitionId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object entries where groupId = &#63; and objectDefinitionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByG_ODI_S(
		long groupId, long objectDefinitionId, int status, int start, int end,
		OrderByComparator<ObjectEntry> orderByComparator) {

		return findByG_ODI_S(
			groupId, objectDefinitionId, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the object entries where groupId = &#63; and objectDefinitionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByG_ODI_S(
		long groupId, long objectDefinitionId, int status, int start, int end,
		OrderByComparator<ObjectEntry> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByG_ODI_S;
				finderArgs = new Object[] {groupId, objectDefinitionId, status};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByG_ODI_S;
			finderArgs = new Object[] {
				groupId, objectDefinitionId, status, start, end,
				orderByComparator
			};
		}

		List<ObjectEntry> list = null;

		if (useFinderCache) {
			list = (List<ObjectEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectEntry objectEntry : list) {
					if ((groupId != objectEntry.getGroupId()) ||
						(objectDefinitionId !=
							objectEntry.getObjectDefinitionId()) ||
						(status != objectEntry.getStatus())) {

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

			sb.append(_SQL_SELECT_OBJECTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_G_ODI_S_GROUPID_2);

			sb.append(_FINDER_COLUMN_G_ODI_S_OBJECTDEFINITIONID_2);

			sb.append(_FINDER_COLUMN_G_ODI_S_STATUS_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				queryPos.add(objectDefinitionId);

				queryPos.add(status);

				list = (List<ObjectEntry>)QueryUtil.list(
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
	 * Returns the first object entry in the ordered set where groupId = &#63; and objectDefinitionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByG_ODI_S_First(
			long groupId, long objectDefinitionId, int status,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = fetchByG_ODI_S_First(
			groupId, objectDefinitionId, status, orderByComparator);

		if (objectEntry != null) {
			return objectEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchObjectEntryException(sb.toString());
	}

	/**
	 * Returns the first object entry in the ordered set where groupId = &#63; and objectDefinitionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByG_ODI_S_First(
		long groupId, long objectDefinitionId, int status,
		OrderByComparator<ObjectEntry> orderByComparator) {

		List<ObjectEntry> list = findByG_ODI_S(
			groupId, objectDefinitionId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object entry in the ordered set where groupId = &#63; and objectDefinitionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByG_ODI_S_Last(
			long groupId, long objectDefinitionId, int status,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = fetchByG_ODI_S_Last(
			groupId, objectDefinitionId, status, orderByComparator);

		if (objectEntry != null) {
			return objectEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchObjectEntryException(sb.toString());
	}

	/**
	 * Returns the last object entry in the ordered set where groupId = &#63; and objectDefinitionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByG_ODI_S_Last(
		long groupId, long objectDefinitionId, int status,
		OrderByComparator<ObjectEntry> orderByComparator) {

		int count = countByG_ODI_S(groupId, objectDefinitionId, status);

		if (count == 0) {
			return null;
		}

		List<ObjectEntry> list = findByG_ODI_S(
			groupId, objectDefinitionId, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object entries before and after the current object entry in the ordered set where groupId = &#63; and objectDefinitionId = &#63; and status = &#63;.
	 *
	 * @param objectEntryId the primary key of the current object entry
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry
	 * @throws NoSuchObjectEntryException if a object entry with the primary key could not be found
	 */
	@Override
	public ObjectEntry[] findByG_ODI_S_PrevAndNext(
			long objectEntryId, long groupId, long objectDefinitionId,
			int status, OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = findByPrimaryKey(objectEntryId);

		Session session = null;

		try {
			session = openSession();

			ObjectEntry[] array = new ObjectEntryImpl[3];

			array[0] = getByG_ODI_S_PrevAndNext(
				session, objectEntry, groupId, objectDefinitionId, status,
				orderByComparator, true);

			array[1] = objectEntry;

			array[2] = getByG_ODI_S_PrevAndNext(
				session, objectEntry, groupId, objectDefinitionId, status,
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

	protected ObjectEntry getByG_ODI_S_PrevAndNext(
		Session session, ObjectEntry objectEntry, long groupId,
		long objectDefinitionId, int status,
		OrderByComparator<ObjectEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_OBJECTENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_ODI_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_ODI_S_OBJECTDEFINITIONID_2);

		sb.append(_FINDER_COLUMN_G_ODI_S_STATUS_2);

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
			sb.append(ObjectEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(objectDefinitionId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(objectEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object entries where groupId = &#63; and objectDefinitionId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 */
	@Override
	public void removeByG_ODI_S(
		long groupId, long objectDefinitionId, int status) {

		for (ObjectEntry objectEntry :
				findByG_ODI_S(
					groupId, objectDefinitionId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(objectEntry);
		}
	}

	/**
	 * Returns the number of object entries where groupId = &#63; and objectDefinitionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @return the number of matching object entries
	 */
	@Override
	public int countByG_ODI_S(
		long groupId, long objectDefinitionId, int status) {

		FinderPath finderPath = _finderPathCountByG_ODI_S;

		Object[] finderArgs = new Object[] {
			groupId, objectDefinitionId, status
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_COUNT_OBJECTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_G_ODI_S_GROUPID_2);

			sb.append(_FINDER_COLUMN_G_ODI_S_OBJECTDEFINITIONID_2);

			sb.append(_FINDER_COLUMN_G_ODI_S_STATUS_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				queryPos.add(objectDefinitionId);

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

	private static final String _FINDER_COLUMN_G_ODI_S_GROUPID_2 =
		"objectEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_ODI_S_OBJECTDEFINITIONID_2 =
		"objectEntry.objectDefinitionId = ? AND ";

	private static final String _FINDER_COLUMN_G_ODI_S_STATUS_2 =
		"objectEntry.status = ? AND objectEntry.objectEntryId = objectEntry.headObjectEntryId";

	private FinderPath _finderPathWithPaginationFindByU_GtCD_ODI;
	private FinderPath _finderPathWithPaginationCountByU_GtCD_ODI;

	/**
	 * Returns all the object entries where userId = &#63; and createDate &gt; &#63; and objectDefinitionId = &#63;.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object entries
	 */
	@Override
	public List<ObjectEntry> findByU_GtCD_ODI(
		long userId, Date createDate, long objectDefinitionId) {

		return findByU_GtCD_ODI(
			userId, createDate, objectDefinitionId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object entries where userId = &#63; and createDate &gt; &#63; and objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @return the range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByU_GtCD_ODI(
		long userId, Date createDate, long objectDefinitionId, int start,
		int end) {

		return findByU_GtCD_ODI(
			userId, createDate, objectDefinitionId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object entries where userId = &#63; and createDate &gt; &#63; and objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByU_GtCD_ODI(
		long userId, Date createDate, long objectDefinitionId, int start,
		int end, OrderByComparator<ObjectEntry> orderByComparator) {

		return findByU_GtCD_ODI(
			userId, createDate, objectDefinitionId, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object entries where userId = &#63; and createDate &gt; &#63; and objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entries
	 */
	@Override
	public List<ObjectEntry> findByU_GtCD_ODI(
		long userId, Date createDate, long objectDefinitionId, int start,
		int end, OrderByComparator<ObjectEntry> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		finderPath = _finderPathWithPaginationFindByU_GtCD_ODI;
		finderArgs = new Object[] {
			userId, _getTime(createDate), objectDefinitionId, start, end,
			orderByComparator
		};

		List<ObjectEntry> list = null;

		if (useFinderCache) {
			list = (List<ObjectEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectEntry objectEntry : list) {
					if ((userId != objectEntry.getUserId()) ||
						(createDate.getTime() >= objectEntry.getCreateDate(
						).getTime()) ||
						(objectDefinitionId !=
							objectEntry.getObjectDefinitionId())) {

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

			sb.append(_SQL_SELECT_OBJECTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_U_GTCD_ODI_USERID_2);

			boolean bindCreateDate = false;

			if (createDate == null) {
				sb.append(_FINDER_COLUMN_U_GTCD_ODI_CREATEDATE_1);
			}
			else {
				bindCreateDate = true;

				sb.append(_FINDER_COLUMN_U_GTCD_ODI_CREATEDATE_2);
			}

			sb.append(_FINDER_COLUMN_U_GTCD_ODI_OBJECTDEFINITIONID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(userId);

				if (bindCreateDate) {
					queryPos.add(new Timestamp(createDate.getTime()));
				}

				queryPos.add(objectDefinitionId);

				list = (List<ObjectEntry>)QueryUtil.list(
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
	 * Returns the first object entry in the ordered set where userId = &#63; and createDate &gt; &#63; and objectDefinitionId = &#63;.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByU_GtCD_ODI_First(
			long userId, Date createDate, long objectDefinitionId,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = fetchByU_GtCD_ODI_First(
			userId, createDate, objectDefinitionId, orderByComparator);

		if (objectEntry != null) {
			return objectEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append(", createDate>");
		sb.append(createDate);

		sb.append(", objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append("}");

		throw new NoSuchObjectEntryException(sb.toString());
	}

	/**
	 * Returns the first object entry in the ordered set where userId = &#63; and createDate &gt; &#63; and objectDefinitionId = &#63;.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByU_GtCD_ODI_First(
		long userId, Date createDate, long objectDefinitionId,
		OrderByComparator<ObjectEntry> orderByComparator) {

		List<ObjectEntry> list = findByU_GtCD_ODI(
			userId, createDate, objectDefinitionId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object entry in the ordered set where userId = &#63; and createDate &gt; &#63; and objectDefinitionId = &#63;.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByU_GtCD_ODI_Last(
			long userId, Date createDate, long objectDefinitionId,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = fetchByU_GtCD_ODI_Last(
			userId, createDate, objectDefinitionId, orderByComparator);

		if (objectEntry != null) {
			return objectEntry;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append(", createDate>");
		sb.append(createDate);

		sb.append(", objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append("}");

		throw new NoSuchObjectEntryException(sb.toString());
	}

	/**
	 * Returns the last object entry in the ordered set where userId = &#63; and createDate &gt; &#63; and objectDefinitionId = &#63;.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByU_GtCD_ODI_Last(
		long userId, Date createDate, long objectDefinitionId,
		OrderByComparator<ObjectEntry> orderByComparator) {

		int count = countByU_GtCD_ODI(userId, createDate, objectDefinitionId);

		if (count == 0) {
			return null;
		}

		List<ObjectEntry> list = findByU_GtCD_ODI(
			userId, createDate, objectDefinitionId, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object entries before and after the current object entry in the ordered set where userId = &#63; and createDate &gt; &#63; and objectDefinitionId = &#63;.
	 *
	 * @param objectEntryId the primary key of the current object entry
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry
	 * @throws NoSuchObjectEntryException if a object entry with the primary key could not be found
	 */
	@Override
	public ObjectEntry[] findByU_GtCD_ODI_PrevAndNext(
			long objectEntryId, long userId, Date createDate,
			long objectDefinitionId,
			OrderByComparator<ObjectEntry> orderByComparator)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = findByPrimaryKey(objectEntryId);

		Session session = null;

		try {
			session = openSession();

			ObjectEntry[] array = new ObjectEntryImpl[3];

			array[0] = getByU_GtCD_ODI_PrevAndNext(
				session, objectEntry, userId, createDate, objectDefinitionId,
				orderByComparator, true);

			array[1] = objectEntry;

			array[2] = getByU_GtCD_ODI_PrevAndNext(
				session, objectEntry, userId, createDate, objectDefinitionId,
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

	protected ObjectEntry getByU_GtCD_ODI_PrevAndNext(
		Session session, ObjectEntry objectEntry, long userId, Date createDate,
		long objectDefinitionId,
		OrderByComparator<ObjectEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_OBJECTENTRY_WHERE);

		sb.append(_FINDER_COLUMN_U_GTCD_ODI_USERID_2);

		boolean bindCreateDate = false;

		if (createDate == null) {
			sb.append(_FINDER_COLUMN_U_GTCD_ODI_CREATEDATE_1);
		}
		else {
			bindCreateDate = true;

			sb.append(_FINDER_COLUMN_U_GTCD_ODI_CREATEDATE_2);
		}

		sb.append(_FINDER_COLUMN_U_GTCD_ODI_OBJECTDEFINITIONID_2);

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
			sb.append(ObjectEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(userId);

		if (bindCreateDate) {
			queryPos.add(new Timestamp(createDate.getTime()));
		}

		queryPos.add(objectDefinitionId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(objectEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object entries where userId = &#63; and createDate &gt; &#63; and objectDefinitionId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param objectDefinitionId the object definition ID
	 */
	@Override
	public void removeByU_GtCD_ODI(
		long userId, Date createDate, long objectDefinitionId) {

		for (ObjectEntry objectEntry :
				findByU_GtCD_ODI(
					userId, createDate, objectDefinitionId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(objectEntry);
		}
	}

	/**
	 * Returns the number of object entries where userId = &#63; and createDate &gt; &#63; and objectDefinitionId = &#63;.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object entries
	 */
	@Override
	public int countByU_GtCD_ODI(
		long userId, Date createDate, long objectDefinitionId) {

		FinderPath finderPath = _finderPathWithPaginationCountByU_GtCD_ODI;

		Object[] finderArgs = new Object[] {
			userId, _getTime(createDate), objectDefinitionId
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_COUNT_OBJECTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_U_GTCD_ODI_USERID_2);

			boolean bindCreateDate = false;

			if (createDate == null) {
				sb.append(_FINDER_COLUMN_U_GTCD_ODI_CREATEDATE_1);
			}
			else {
				bindCreateDate = true;

				sb.append(_FINDER_COLUMN_U_GTCD_ODI_CREATEDATE_2);
			}

			sb.append(_FINDER_COLUMN_U_GTCD_ODI_OBJECTDEFINITIONID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(userId);

				if (bindCreateDate) {
					queryPos.add(new Timestamp(createDate.getTime()));
				}

				queryPos.add(objectDefinitionId);

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

	private static final String _FINDER_COLUMN_U_GTCD_ODI_USERID_2 =
		"objectEntry.userId = ? AND ";

	private static final String _FINDER_COLUMN_U_GTCD_ODI_CREATEDATE_1 =
		"objectEntry.createDate IS NULL AND ";

	private static final String _FINDER_COLUMN_U_GTCD_ODI_CREATEDATE_2 =
		"objectEntry.createDate > ? AND ";

	private static final String _FINDER_COLUMN_U_GTCD_ODI_OBJECTDEFINITIONID_2 =
		"objectEntry.objectDefinitionId = ? AND objectEntry.objectEntryId = objectEntry.headObjectEntryId";

	private FinderPath _finderPathFetchByERC_G_C_ODI;

	/**
	 * Returns the object entry where externalReferenceCode = &#63; and groupId = &#63; and companyId = &#63; and objectDefinitionId = &#63; or throws a <code>NoSuchObjectEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry findByERC_G_C_ODI(
			String externalReferenceCode, long groupId, long companyId,
			long objectDefinitionId)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = fetchByERC_G_C_ODI(
			externalReferenceCode, groupId, companyId, objectDefinitionId);

		if (objectEntry == null) {
			StringBundler sb = new StringBundler(10);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("externalReferenceCode=");
			sb.append(externalReferenceCode);

			sb.append(", groupId=");
			sb.append(groupId);

			sb.append(", companyId=");
			sb.append(companyId);

			sb.append(", objectDefinitionId=");
			sb.append(objectDefinitionId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchObjectEntryException(sb.toString());
		}

		return objectEntry;
	}

	/**
	 * Returns the object entry where externalReferenceCode = &#63; and groupId = &#63; and companyId = &#63; and objectDefinitionId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByERC_G_C_ODI(
		String externalReferenceCode, long groupId, long companyId,
		long objectDefinitionId) {

		return fetchByERC_G_C_ODI(
			externalReferenceCode, groupId, companyId, objectDefinitionId,
			true);
	}

	/**
	 * Returns the object entry where externalReferenceCode = &#63; and groupId = &#63; and companyId = &#63; and objectDefinitionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public ObjectEntry fetchByERC_G_C_ODI(
		String externalReferenceCode, long groupId, long companyId,
		long objectDefinitionId, boolean useFinderCache) {

		externalReferenceCode = Objects.toString(externalReferenceCode, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {
				externalReferenceCode, groupId, companyId, objectDefinitionId
			};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByERC_G_C_ODI, finderArgs, this);
		}

		if (result instanceof ObjectEntry) {
			ObjectEntry objectEntry = (ObjectEntry)result;

			if (!Objects.equals(
					externalReferenceCode,
					objectEntry.getExternalReferenceCode()) ||
				(groupId != objectEntry.getGroupId()) ||
				(companyId != objectEntry.getCompanyId()) ||
				(objectDefinitionId != objectEntry.getObjectDefinitionId())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_SQL_SELECT_OBJECTENTRY_WHERE);

			boolean bindExternalReferenceCode = false;

			if (externalReferenceCode.isEmpty()) {
				sb.append(_FINDER_COLUMN_ERC_G_C_ODI_EXTERNALREFERENCECODE_3);
			}
			else {
				bindExternalReferenceCode = true;

				sb.append(_FINDER_COLUMN_ERC_G_C_ODI_EXTERNALREFERENCECODE_2);
			}

			sb.append(_FINDER_COLUMN_ERC_G_C_ODI_GROUPID_2);

			sb.append(_FINDER_COLUMN_ERC_G_C_ODI_COMPANYID_2);

			sb.append(_FINDER_COLUMN_ERC_G_C_ODI_OBJECTDEFINITIONID_2);

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

				queryPos.add(companyId);

				queryPos.add(objectDefinitionId);

				List<ObjectEntry> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByERC_G_C_ODI, finderArgs, list);
					}
				}
				else {
					ObjectEntry objectEntry = list.get(0);

					result = objectEntry;

					cacheResult(objectEntry);
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
			return (ObjectEntry)result;
		}
	}

	/**
	 * Removes the object entry where externalReferenceCode = &#63; and groupId = &#63; and companyId = &#63; and objectDefinitionId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the object entry that was removed
	 */
	@Override
	public ObjectEntry removeByERC_G_C_ODI(
			String externalReferenceCode, long groupId, long companyId,
			long objectDefinitionId)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = findByERC_G_C_ODI(
			externalReferenceCode, groupId, companyId, objectDefinitionId);

		return remove(objectEntry);
	}

	/**
	 * Returns the number of object entries where externalReferenceCode = &#63; and groupId = &#63; and companyId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object entries
	 */
	@Override
	public int countByERC_G_C_ODI(
		String externalReferenceCode, long groupId, long companyId,
		long objectDefinitionId) {

		ObjectEntry objectEntry = fetchByERC_G_C_ODI(
			externalReferenceCode, groupId, companyId, objectDefinitionId);

		if (objectEntry == null) {
			return 0;
		}

		return 1;
	}

	private static final String
		_FINDER_COLUMN_ERC_G_C_ODI_EXTERNALREFERENCECODE_2 =
			"objectEntry.externalReferenceCode = ? AND ";

	private static final String
		_FINDER_COLUMN_ERC_G_C_ODI_EXTERNALREFERENCECODE_3 =
			"(objectEntry.externalReferenceCode IS NULL OR objectEntry.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_G_C_ODI_GROUPID_2 =
		"objectEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_ERC_G_C_ODI_COMPANYID_2 =
		"objectEntry.companyId = ? AND ";

	private static final String
		_FINDER_COLUMN_ERC_G_C_ODI_OBJECTDEFINITIONID_2 =
			"objectEntry.objectDefinitionId = ?";

	public ObjectEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ObjectEntry.class);

		setModelImplClass(ObjectEntryImpl.class);
		setModelPKClass(long.class);

		setTable(ObjectEntryTable.INSTANCE);
	}

	/**
	 * Caches the object entry in the entity cache if it is enabled.
	 *
	 * @param objectEntry the object entry
	 */
	@Override
	public void cacheResult(ObjectEntry objectEntry) {
		entityCache.putResult(
			ObjectEntryImpl.class, objectEntry.getPrimaryKey(), objectEntry);

		finderCache.putResult(
			_finderPathFetchByUUID_G,
			new Object[] {objectEntry.getUuid(), objectEntry.getGroupId()},
			objectEntry);

		finderCache.putResult(
			_finderPathFetchByHeadObjectEntryId,
			new Object[] {objectEntry.getHeadObjectEntryId()}, objectEntry);

		finderCache.putResult(
			_finderPathFetchByERC_G_C_ODI,
			new Object[] {
				objectEntry.getExternalReferenceCode(),
				objectEntry.getGroupId(), objectEntry.getCompanyId(),
				objectEntry.getObjectDefinitionId()
			},
			objectEntry);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the object entries in the entity cache if it is enabled.
	 *
	 * @param objectEntries the object entries
	 */
	@Override
	public void cacheResult(List<ObjectEntry> objectEntries) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (objectEntries.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (ObjectEntry objectEntry : objectEntries) {
			if (entityCache.getResult(
					ObjectEntryImpl.class, objectEntry.getPrimaryKey()) ==
						null) {

				cacheResult(objectEntry);
			}
		}
	}

	/**
	 * Clears the cache for all object entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(ObjectEntryImpl.class);

		finderCache.clearCache(ObjectEntryImpl.class);
	}

	/**
	 * Clears the cache for the object entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(ObjectEntry objectEntry) {
		entityCache.removeResult(ObjectEntryImpl.class, objectEntry);
	}

	@Override
	public void clearCache(List<ObjectEntry> objectEntries) {
		for (ObjectEntry objectEntry : objectEntries) {
			entityCache.removeResult(ObjectEntryImpl.class, objectEntry);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(ObjectEntryImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(ObjectEntryImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		ObjectEntryModelImpl objectEntryModelImpl) {

		Object[] args = new Object[] {
			objectEntryModelImpl.getUuid(), objectEntryModelImpl.getGroupId()
		};

		finderCache.putResult(
			_finderPathFetchByUUID_G, args, objectEntryModelImpl);

		args = new Object[] {objectEntryModelImpl.getHeadObjectEntryId()};

		finderCache.putResult(
			_finderPathFetchByHeadObjectEntryId, args, objectEntryModelImpl);

		args = new Object[] {
			objectEntryModelImpl.getExternalReferenceCode(),
			objectEntryModelImpl.getGroupId(),
			objectEntryModelImpl.getCompanyId(),
			objectEntryModelImpl.getObjectDefinitionId()
		};

		finderCache.putResult(
			_finderPathFetchByERC_G_C_ODI, args, objectEntryModelImpl);
	}

	/**
	 * Creates a new object entry with the primary key. Does not add the object entry to the database.
	 *
	 * @param objectEntryId the primary key for the new object entry
	 * @return the new object entry
	 */
	@Override
	public ObjectEntry create(long objectEntryId) {
		ObjectEntry objectEntry = new ObjectEntryImpl();

		objectEntry.setNew(true);
		objectEntry.setPrimaryKey(objectEntryId);

		String uuid = PortalUUIDUtil.generate();

		objectEntry.setUuid(uuid);

		objectEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return objectEntry;
	}

	/**
	 * Removes the object entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectEntryId the primary key of the object entry
	 * @return the object entry that was removed
	 * @throws NoSuchObjectEntryException if a object entry with the primary key could not be found
	 */
	@Override
	public ObjectEntry remove(long objectEntryId)
		throws NoSuchObjectEntryException {

		return remove((Serializable)objectEntryId);
	}

	/**
	 * Removes the object entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the object entry
	 * @return the object entry that was removed
	 * @throws NoSuchObjectEntryException if a object entry with the primary key could not be found
	 */
	@Override
	public ObjectEntry remove(Serializable primaryKey)
		throws NoSuchObjectEntryException {

		Session session = null;

		try {
			session = openSession();

			ObjectEntry objectEntry = (ObjectEntry)session.get(
				ObjectEntryImpl.class, primaryKey);

			if (objectEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchObjectEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(objectEntry);
		}
		catch (NoSuchObjectEntryException noSuchEntityException) {
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
	protected ObjectEntry removeImpl(ObjectEntry objectEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(objectEntry)) {
				objectEntry = (ObjectEntry)session.get(
					ObjectEntryImpl.class, objectEntry.getPrimaryKeyObj());
			}

			if (objectEntry != null) {
				session.delete(objectEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (objectEntry != null) {
			clearCache(objectEntry);
		}

		return objectEntry;
	}

	@Override
	public ObjectEntry updateImpl(ObjectEntry objectEntry) {
		boolean isNew = objectEntry.isNew();

		if (!(objectEntry instanceof ObjectEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(objectEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(objectEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in objectEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ObjectEntry implementation " +
					objectEntry.getClass());
		}

		ObjectEntryModelImpl objectEntryModelImpl =
			(ObjectEntryModelImpl)objectEntry;

		if (Validator.isNull(objectEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			objectEntry.setUuid(uuid);
		}

		if (Validator.isNull(objectEntry.getExternalReferenceCode())) {
			objectEntry.setExternalReferenceCode(objectEntry.getUuid());
		}
		else {
			if (!Objects.equals(
					objectEntryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					objectEntry.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = objectEntry.getCompanyId();

					long groupId = objectEntry.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = objectEntry.getPrimaryKey();
					}

					try {
						objectEntry.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								ObjectEntry.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								objectEntry.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (objectEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				objectEntry.setCreateDate(date);
			}
			else {
				objectEntry.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!objectEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				objectEntry.setModifiedDate(date);
			}
			else {
				objectEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(objectEntry);
			}
			else {
				objectEntry = (ObjectEntry)session.merge(objectEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			ObjectEntryImpl.class, objectEntryModelImpl, false, true);

		cacheUniqueFindersCache(objectEntryModelImpl);

		if (isNew) {
			objectEntry.setNew(false);
		}

		objectEntry.resetOriginalValues();

		return objectEntry;
	}

	/**
	 * Returns the object entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the object entry
	 * @return the object entry
	 * @throws NoSuchObjectEntryException if a object entry with the primary key could not be found
	 */
	@Override
	public ObjectEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchObjectEntryException {

		ObjectEntry objectEntry = fetchByPrimaryKey(primaryKey);

		if (objectEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchObjectEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return objectEntry;
	}

	/**
	 * Returns the object entry with the primary key or throws a <code>NoSuchObjectEntryException</code> if it could not be found.
	 *
	 * @param objectEntryId the primary key of the object entry
	 * @return the object entry
	 * @throws NoSuchObjectEntryException if a object entry with the primary key could not be found
	 */
	@Override
	public ObjectEntry findByPrimaryKey(long objectEntryId)
		throws NoSuchObjectEntryException {

		return findByPrimaryKey((Serializable)objectEntryId);
	}

	/**
	 * Returns the object entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectEntryId the primary key of the object entry
	 * @return the object entry, or <code>null</code> if a object entry with the primary key could not be found
	 */
	@Override
	public ObjectEntry fetchByPrimaryKey(long objectEntryId) {
		return fetchByPrimaryKey((Serializable)objectEntryId);
	}

	/**
	 * Returns all the object entries.
	 *
	 * @return the object entries
	 */
	@Override
	public List<ObjectEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @return the range of object entries
	 */
	@Override
	public List<ObjectEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the object entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of object entries
	 */
	@Override
	public List<ObjectEntry> findAll(
		int start, int end, OrderByComparator<ObjectEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of object entries
	 */
	@Override
	public List<ObjectEntry> findAll(
		int start, int end, OrderByComparator<ObjectEntry> orderByComparator,
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

		List<ObjectEntry> list = null;

		if (useFinderCache) {
			list = (List<ObjectEntry>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_OBJECTENTRY);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_OBJECTENTRY;

				sql = sql.concat(ObjectEntryModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<ObjectEntry>)QueryUtil.list(
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
	 * Removes all the object entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (ObjectEntry objectEntry : findAll()) {
			remove(objectEntry);
		}
	}

	/**
	 * Returns the number of object entries.
	 *
	 * @return the number of object entries
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_OBJECTENTRY);

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
		return "objectEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OBJECTENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ObjectEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the object entry persistence.
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

		_finderPathFetchByHeadObjectEntryId = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByHeadObjectEntryId",
			new String[] {Long.class.getName()},
			new String[] {"headObjectEntryId"}, true);

		_finderPathWithPaginationFindByObjectDefinitionId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByObjectDefinitionId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"objectDefinitionId"}, true);

		_finderPathWithoutPaginationFindByObjectDefinitionId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"findByObjectDefinitionId", new String[] {Long.class.getName()},
			new String[] {"objectDefinitionId"}, true);

		_finderPathCountByObjectDefinitionId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByObjectDefinitionId", new String[] {Long.class.getName()},
			new String[] {"objectDefinitionId"}, false);

		_finderPathWithPaginationFindByG_ODI = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_ODI",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "objectDefinitionId"}, true);

		_finderPathWithoutPaginationFindByG_ODI = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_ODI",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "objectDefinitionId"}, true);

		_finderPathCountByG_ODI = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_ODI",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "objectDefinitionId"}, false);

		_finderPathWithPaginationFindByG_OEFI = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_OEFI",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "objectEntryFolderId"}, true);

		_finderPathWithoutPaginationFindByG_OEFI = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_OEFI",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "objectEntryFolderId"}, true);

		_finderPathCountByG_OEFI = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_OEFI",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "objectEntryFolderId"}, false);

		_finderPathWithPaginationFindByU_ODI = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_ODI",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"userId", "objectDefinitionId"}, true);

		_finderPathWithoutPaginationFindByU_ODI = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_ODI",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"userId", "objectDefinitionId"}, true);

		_finderPathCountByU_ODI = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByU_ODI",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"userId", "objectDefinitionId"}, false);

		_finderPathWithPaginationFindByODI_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI_NotS",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"objectDefinitionId", "status"}, true);

		_finderPathWithPaginationCountByODI_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByODI_NotS",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"objectDefinitionId", "status"}, false);

		_finderPathWithPaginationFindByROEI_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByROEI_NotS",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"rootObjectEntryId", "status"}, true);

		_finderPathWithPaginationCountByROEI_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByROEI_NotS",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"rootObjectEntryId", "status"}, false);

		_finderPathWithPaginationFindByG_C_OEFI = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_OEFI",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "companyId", "objectEntryFolderId"}, true);

		_finderPathWithoutPaginationFindByG_C_OEFI = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_OEFI",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"groupId", "companyId", "objectEntryFolderId"}, true);

		_finderPathCountByG_C_OEFI = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_OEFI",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"groupId", "companyId", "objectEntryFolderId"},
			false);

		_finderPathWithPaginationFindByG_ODI_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_ODI_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "objectDefinitionId", "status"}, true);

		_finderPathWithoutPaginationFindByG_ODI_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_ODI_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "objectDefinitionId", "status"}, true);

		_finderPathCountByG_ODI_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_ODI_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "objectDefinitionId", "status"}, false);

		_finderPathWithPaginationFindByU_GtCD_ODI = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_GtCD_ODI",
			new String[] {
				Long.class.getName(), Date.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"userId", "createDate", "objectDefinitionId"}, true);

		_finderPathWithPaginationCountByU_GtCD_ODI = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByU_GtCD_ODI",
			new String[] {
				Long.class.getName(), Date.class.getName(), Long.class.getName()
			},
			new String[] {"userId", "createDate", "objectDefinitionId"}, false);

		_finderPathFetchByERC_G_C_ODI = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_G_C_ODI",
			new String[] {
				String.class.getName(), Long.class.getName(),
				Long.class.getName(), Long.class.getName()
			},
			new String[] {
				"externalReferenceCode", "groupId", "companyId",
				"objectDefinitionId"
			},
			true);

		ObjectEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ObjectEntryUtil.setPersistence(null);

		entityCache.removeCache(ObjectEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = ObjectPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = ObjectPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = ObjectPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

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

	private static final String _SQL_SELECT_OBJECTENTRY =
		"SELECT objectEntry FROM ObjectEntry objectEntry";

	private static final String _SQL_SELECT_OBJECTENTRY_WHERE =
		"SELECT objectEntry FROM ObjectEntry objectEntry WHERE ";

	private static final String _SQL_COUNT_OBJECTENTRY =
		"SELECT COUNT(objectEntry) FROM ObjectEntry objectEntry";

	private static final String _SQL_COUNT_OBJECTENTRY_WHERE =
		"SELECT COUNT(objectEntry) FROM ObjectEntry objectEntry WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS = "objectEntry.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No ObjectEntry exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ObjectEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}