/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.impl;

import com.liferay.object.exception.NoSuchObjectEntryFolderException;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectEntryFolderTable;
import com.liferay.object.model.impl.ObjectEntryFolderImpl;
import com.liferay.object.model.impl.ObjectEntryFolderModelImpl;
import com.liferay.object.service.persistence.ObjectEntryFolderPersistence;
import com.liferay.object.service.persistence.ObjectEntryFolderUtil;
import com.liferay.object.service.persistence.impl.constants.ObjectPersistenceConstants;
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
 * The persistence implementation for the object entry folder service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = ObjectEntryFolderPersistence.class)
public class ObjectEntryFolderPersistenceImpl
	extends BasePersistenceImpl<ObjectEntryFolder>
	implements ObjectEntryFolderPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ObjectEntryFolderUtil</code> to access the object entry folder persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ObjectEntryFolderImpl.class.getName();

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
	 * Returns all the object entry folders where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching object entry folders
	 */
	@Override
	public List<ObjectEntryFolder> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object entry folders where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @return the range of matching object entry folders
	 */
	@Override
	public List<ObjectEntryFolder> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object entry folders where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entry folders
	 */
	@Override
	public List<ObjectEntryFolder> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectEntryFolder> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object entry folders where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entry folders
	 */
	@Override
	public List<ObjectEntryFolder> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectEntryFolder> orderByComparator,
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

		List<ObjectEntryFolder> list = null;

		if (useFinderCache) {
			list = (List<ObjectEntryFolder>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectEntryFolder objectEntryFolder : list) {
					if (!uuid.equals(objectEntryFolder.getUuid())) {
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

			sb.append(_SQL_SELECT_OBJECTENTRYFOLDER_WHERE);

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
				sb.append(ObjectEntryFolderModelImpl.ORDER_BY_JPQL);
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

				list = (List<ObjectEntryFolder>)QueryUtil.list(
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
	 * Returns the first object entry folder in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry folder
	 * @throws NoSuchObjectEntryFolderException if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder findByUuid_First(
			String uuid, OrderByComparator<ObjectEntryFolder> orderByComparator)
		throws NoSuchObjectEntryFolderException {

		ObjectEntryFolder objectEntryFolder = fetchByUuid_First(
			uuid, orderByComparator);

		if (objectEntryFolder != null) {
			return objectEntryFolder;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchObjectEntryFolderException(sb.toString());
	}

	/**
	 * Returns the first object entry folder in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry folder, or <code>null</code> if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder fetchByUuid_First(
		String uuid, OrderByComparator<ObjectEntryFolder> orderByComparator) {

		List<ObjectEntryFolder> list = findByUuid(
			uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object entry folder in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry folder
	 * @throws NoSuchObjectEntryFolderException if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder findByUuid_Last(
			String uuid, OrderByComparator<ObjectEntryFolder> orderByComparator)
		throws NoSuchObjectEntryFolderException {

		ObjectEntryFolder objectEntryFolder = fetchByUuid_Last(
			uuid, orderByComparator);

		if (objectEntryFolder != null) {
			return objectEntryFolder;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchObjectEntryFolderException(sb.toString());
	}

	/**
	 * Returns the last object entry folder in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry folder, or <code>null</code> if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder fetchByUuid_Last(
		String uuid, OrderByComparator<ObjectEntryFolder> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<ObjectEntryFolder> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object entry folders before and after the current object entry folder in the ordered set where uuid = &#63;.
	 *
	 * @param objectEntryFolderId the primary key of the current object entry folder
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry folder
	 * @throws NoSuchObjectEntryFolderException if a object entry folder with the primary key could not be found
	 */
	@Override
	public ObjectEntryFolder[] findByUuid_PrevAndNext(
			long objectEntryFolderId, String uuid,
			OrderByComparator<ObjectEntryFolder> orderByComparator)
		throws NoSuchObjectEntryFolderException {

		uuid = Objects.toString(uuid, "");

		ObjectEntryFolder objectEntryFolder = findByPrimaryKey(
			objectEntryFolderId);

		Session session = null;

		try {
			session = openSession();

			ObjectEntryFolder[] array = new ObjectEntryFolderImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, objectEntryFolder, uuid, orderByComparator, true);

			array[1] = objectEntryFolder;

			array[2] = getByUuid_PrevAndNext(
				session, objectEntryFolder, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected ObjectEntryFolder getByUuid_PrevAndNext(
		Session session, ObjectEntryFolder objectEntryFolder, String uuid,
		OrderByComparator<ObjectEntryFolder> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTENTRYFOLDER_WHERE);

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
			sb.append(ObjectEntryFolderModelImpl.ORDER_BY_JPQL);
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
						objectEntryFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectEntryFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object entry folders where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (ObjectEntryFolder objectEntryFolder :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(objectEntryFolder);
		}
	}

	/**
	 * Returns the number of object entry folders where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object entry folders
	 */
	@Override
	public int countByUuid(String uuid) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid;

		Object[] finderArgs = new Object[] {uuid};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_OBJECTENTRYFOLDER_WHERE);

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
		"objectEntryFolder.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(objectEntryFolder.uuid IS NULL OR objectEntryFolder.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;

	/**
	 * Returns the object entry folder where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchObjectEntryFolderException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching object entry folder
	 * @throws NoSuchObjectEntryFolderException if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder findByUUID_G(String uuid, long groupId)
		throws NoSuchObjectEntryFolderException {

		ObjectEntryFolder objectEntryFolder = fetchByUUID_G(uuid, groupId);

		if (objectEntryFolder == null) {
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

			throw new NoSuchObjectEntryFolderException(sb.toString());
		}

		return objectEntryFolder;
	}

	/**
	 * Returns the object entry folder where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching object entry folder, or <code>null</code> if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the object entry folder where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object entry folder, or <code>null</code> if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder fetchByUUID_G(
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

		if (result instanceof ObjectEntryFolder) {
			ObjectEntryFolder objectEntryFolder = (ObjectEntryFolder)result;

			if (!Objects.equals(uuid, objectEntryFolder.getUuid()) ||
				(groupId != objectEntryFolder.getGroupId())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_OBJECTENTRYFOLDER_WHERE);

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

				List<ObjectEntryFolder> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByUUID_G, finderArgs, list);
					}
				}
				else {
					ObjectEntryFolder objectEntryFolder = list.get(0);

					result = objectEntryFolder;

					cacheResult(objectEntryFolder);
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
			return (ObjectEntryFolder)result;
		}
	}

	/**
	 * Removes the object entry folder where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the object entry folder that was removed
	 */
	@Override
	public ObjectEntryFolder removeByUUID_G(String uuid, long groupId)
		throws NoSuchObjectEntryFolderException {

		ObjectEntryFolder objectEntryFolder = findByUUID_G(uuid, groupId);

		return remove(objectEntryFolder);
	}

	/**
	 * Returns the number of object entry folders where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching object entry folders
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		ObjectEntryFolder objectEntryFolder = fetchByUUID_G(uuid, groupId);

		if (objectEntryFolder == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"objectEntryFolder.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(objectEntryFolder.uuid IS NULL OR objectEntryFolder.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"objectEntryFolder.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the object entry folders where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching object entry folders
	 */
	@Override
	public List<ObjectEntryFolder> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object entry folders where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @return the range of matching object entry folders
	 */
	@Override
	public List<ObjectEntryFolder> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object entry folders where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entry folders
	 */
	@Override
	public List<ObjectEntryFolder> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectEntryFolder> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object entry folders where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entry folders
	 */
	@Override
	public List<ObjectEntryFolder> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectEntryFolder> orderByComparator,
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

		List<ObjectEntryFolder> list = null;

		if (useFinderCache) {
			list = (List<ObjectEntryFolder>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectEntryFolder objectEntryFolder : list) {
					if (!uuid.equals(objectEntryFolder.getUuid()) ||
						(companyId != objectEntryFolder.getCompanyId())) {

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

			sb.append(_SQL_SELECT_OBJECTENTRYFOLDER_WHERE);

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
				sb.append(ObjectEntryFolderModelImpl.ORDER_BY_JPQL);
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

				list = (List<ObjectEntryFolder>)QueryUtil.list(
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
	 * Returns the first object entry folder in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry folder
	 * @throws NoSuchObjectEntryFolderException if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectEntryFolder> orderByComparator)
		throws NoSuchObjectEntryFolderException {

		ObjectEntryFolder objectEntryFolder = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (objectEntryFolder != null) {
			return objectEntryFolder;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchObjectEntryFolderException(sb.toString());
	}

	/**
	 * Returns the first object entry folder in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry folder, or <code>null</code> if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectEntryFolder> orderByComparator) {

		List<ObjectEntryFolder> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object entry folder in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry folder
	 * @throws NoSuchObjectEntryFolderException if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<ObjectEntryFolder> orderByComparator)
		throws NoSuchObjectEntryFolderException {

		ObjectEntryFolder objectEntryFolder = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (objectEntryFolder != null) {
			return objectEntryFolder;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchObjectEntryFolderException(sb.toString());
	}

	/**
	 * Returns the last object entry folder in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry folder, or <code>null</code> if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<ObjectEntryFolder> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<ObjectEntryFolder> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object entry folders before and after the current object entry folder in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param objectEntryFolderId the primary key of the current object entry folder
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry folder
	 * @throws NoSuchObjectEntryFolderException if a object entry folder with the primary key could not be found
	 */
	@Override
	public ObjectEntryFolder[] findByUuid_C_PrevAndNext(
			long objectEntryFolderId, String uuid, long companyId,
			OrderByComparator<ObjectEntryFolder> orderByComparator)
		throws NoSuchObjectEntryFolderException {

		uuid = Objects.toString(uuid, "");

		ObjectEntryFolder objectEntryFolder = findByPrimaryKey(
			objectEntryFolderId);

		Session session = null;

		try {
			session = openSession();

			ObjectEntryFolder[] array = new ObjectEntryFolderImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, objectEntryFolder, uuid, companyId, orderByComparator,
				true);

			array[1] = objectEntryFolder;

			array[2] = getByUuid_C_PrevAndNext(
				session, objectEntryFolder, uuid, companyId, orderByComparator,
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

	protected ObjectEntryFolder getByUuid_C_PrevAndNext(
		Session session, ObjectEntryFolder objectEntryFolder, String uuid,
		long companyId, OrderByComparator<ObjectEntryFolder> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTENTRYFOLDER_WHERE);

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
			sb.append(ObjectEntryFolderModelImpl.ORDER_BY_JPQL);
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
						objectEntryFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectEntryFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object entry folders where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (ObjectEntryFolder objectEntryFolder :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(objectEntryFolder);
		}
	}

	/**
	 * Returns the number of object entry folders where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object entry folders
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid_C;

		Object[] finderArgs = new Object[] {uuid, companyId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_OBJECTENTRYFOLDER_WHERE);

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
		"objectEntryFolder.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(objectEntryFolder.uuid IS NULL OR objectEntryFolder.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"objectEntryFolder.companyId = ?";

	private FinderPath _finderPathFetchByERC_G_C;

	/**
	 * Returns the object entry folder where externalReferenceCode = &#63; and groupId = &#63; and companyId = &#63; or throws a <code>NoSuchObjectEntryFolderException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @return the matching object entry folder
	 * @throws NoSuchObjectEntryFolderException if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder findByERC_G_C(
			String externalReferenceCode, long groupId, long companyId)
		throws NoSuchObjectEntryFolderException {

		ObjectEntryFolder objectEntryFolder = fetchByERC_G_C(
			externalReferenceCode, groupId, companyId);

		if (objectEntryFolder == null) {
			StringBundler sb = new StringBundler(8);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("externalReferenceCode=");
			sb.append(externalReferenceCode);

			sb.append(", groupId=");
			sb.append(groupId);

			sb.append(", companyId=");
			sb.append(companyId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchObjectEntryFolderException(sb.toString());
		}

		return objectEntryFolder;
	}

	/**
	 * Returns the object entry folder where externalReferenceCode = &#63; and groupId = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @return the matching object entry folder, or <code>null</code> if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder fetchByERC_G_C(
		String externalReferenceCode, long groupId, long companyId) {

		return fetchByERC_G_C(externalReferenceCode, groupId, companyId, true);
	}

	/**
	 * Returns the object entry folder where externalReferenceCode = &#63; and groupId = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object entry folder, or <code>null</code> if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder fetchByERC_G_C(
		String externalReferenceCode, long groupId, long companyId,
		boolean useFinderCache) {

		externalReferenceCode = Objects.toString(externalReferenceCode, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {
				externalReferenceCode, groupId, companyId
			};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByERC_G_C, finderArgs, this);
		}

		if (result instanceof ObjectEntryFolder) {
			ObjectEntryFolder objectEntryFolder = (ObjectEntryFolder)result;

			if (!Objects.equals(
					externalReferenceCode,
					objectEntryFolder.getExternalReferenceCode()) ||
				(groupId != objectEntryFolder.getGroupId()) ||
				(companyId != objectEntryFolder.getCompanyId())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(5);

			sb.append(_SQL_SELECT_OBJECTENTRYFOLDER_WHERE);

			boolean bindExternalReferenceCode = false;

			if (externalReferenceCode.isEmpty()) {
				sb.append(_FINDER_COLUMN_ERC_G_C_EXTERNALREFERENCECODE_3);
			}
			else {
				bindExternalReferenceCode = true;

				sb.append(_FINDER_COLUMN_ERC_G_C_EXTERNALREFERENCECODE_2);
			}

			sb.append(_FINDER_COLUMN_ERC_G_C_GROUPID_2);

			sb.append(_FINDER_COLUMN_ERC_G_C_COMPANYID_2);

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

				List<ObjectEntryFolder> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByERC_G_C, finderArgs, list);
					}
				}
				else {
					ObjectEntryFolder objectEntryFolder = list.get(0);

					result = objectEntryFolder;

					cacheResult(objectEntryFolder);
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
			return (ObjectEntryFolder)result;
		}
	}

	/**
	 * Removes the object entry folder where externalReferenceCode = &#63; and groupId = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @return the object entry folder that was removed
	 */
	@Override
	public ObjectEntryFolder removeByERC_G_C(
			String externalReferenceCode, long groupId, long companyId)
		throws NoSuchObjectEntryFolderException {

		ObjectEntryFolder objectEntryFolder = findByERC_G_C(
			externalReferenceCode, groupId, companyId);

		return remove(objectEntryFolder);
	}

	/**
	 * Returns the number of object entry folders where externalReferenceCode = &#63; and groupId = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @return the number of matching object entry folders
	 */
	@Override
	public int countByERC_G_C(
		String externalReferenceCode, long groupId, long companyId) {

		ObjectEntryFolder objectEntryFolder = fetchByERC_G_C(
			externalReferenceCode, groupId, companyId);

		if (objectEntryFolder == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ERC_G_C_EXTERNALREFERENCECODE_2 =
		"objectEntryFolder.externalReferenceCode = ? AND ";

	private static final String _FINDER_COLUMN_ERC_G_C_EXTERNALREFERENCECODE_3 =
		"(objectEntryFolder.externalReferenceCode IS NULL OR objectEntryFolder.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_G_C_GROUPID_2 =
		"objectEntryFolder.groupId = ? AND ";

	private static final String _FINDER_COLUMN_ERC_G_C_COMPANYID_2 =
		"objectEntryFolder.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByG_C_P;
	private FinderPath _finderPathWithoutPaginationFindByG_C_P;
	private FinderPath _finderPathCountByG_C_P;

	/**
	 * Returns all the object entry folders where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @return the matching object entry folders
	 */
	@Override
	public List<ObjectEntryFolder> findByG_C_P(
		long groupId, long companyId, long parentObjectEntryFolderId) {

		return findByG_C_P(
			groupId, companyId, parentObjectEntryFolderId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object entry folders where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @return the range of matching object entry folders
	 */
	@Override
	public List<ObjectEntryFolder> findByG_C_P(
		long groupId, long companyId, long parentObjectEntryFolderId, int start,
		int end) {

		return findByG_C_P(
			groupId, companyId, parentObjectEntryFolderId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object entry folders where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entry folders
	 */
	@Override
	public List<ObjectEntryFolder> findByG_C_P(
		long groupId, long companyId, long parentObjectEntryFolderId, int start,
		int end, OrderByComparator<ObjectEntryFolder> orderByComparator) {

		return findByG_C_P(
			groupId, companyId, parentObjectEntryFolderId, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object entry folders where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entry folders
	 */
	@Override
	public List<ObjectEntryFolder> findByG_C_P(
		long groupId, long companyId, long parentObjectEntryFolderId, int start,
		int end, OrderByComparator<ObjectEntryFolder> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByG_C_P;
				finderArgs = new Object[] {
					groupId, companyId, parentObjectEntryFolderId
				};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByG_C_P;
			finderArgs = new Object[] {
				groupId, companyId, parentObjectEntryFolderId, start, end,
				orderByComparator
			};
		}

		List<ObjectEntryFolder> list = null;

		if (useFinderCache) {
			list = (List<ObjectEntryFolder>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectEntryFolder objectEntryFolder : list) {
					if ((groupId != objectEntryFolder.getGroupId()) ||
						(companyId != objectEntryFolder.getCompanyId()) ||
						(parentObjectEntryFolderId !=
							objectEntryFolder.getParentObjectEntryFolderId())) {

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

			sb.append(_SQL_SELECT_OBJECTENTRYFOLDER_WHERE);

			sb.append(_FINDER_COLUMN_G_C_P_GROUPID_2);

			sb.append(_FINDER_COLUMN_G_C_P_COMPANYID_2);

			sb.append(_FINDER_COLUMN_G_C_P_PARENTOBJECTENTRYFOLDERID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectEntryFolderModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				queryPos.add(companyId);

				queryPos.add(parentObjectEntryFolderId);

				list = (List<ObjectEntryFolder>)QueryUtil.list(
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
	 * Returns the first object entry folder in the ordered set where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry folder
	 * @throws NoSuchObjectEntryFolderException if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder findByG_C_P_First(
			long groupId, long companyId, long parentObjectEntryFolderId,
			OrderByComparator<ObjectEntryFolder> orderByComparator)
		throws NoSuchObjectEntryFolderException {

		ObjectEntryFolder objectEntryFolder = fetchByG_C_P_First(
			groupId, companyId, parentObjectEntryFolderId, orderByComparator);

		if (objectEntryFolder != null) {
			return objectEntryFolder;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append(", parentObjectEntryFolderId=");
		sb.append(parentObjectEntryFolderId);

		sb.append("}");

		throw new NoSuchObjectEntryFolderException(sb.toString());
	}

	/**
	 * Returns the first object entry folder in the ordered set where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry folder, or <code>null</code> if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder fetchByG_C_P_First(
		long groupId, long companyId, long parentObjectEntryFolderId,
		OrderByComparator<ObjectEntryFolder> orderByComparator) {

		List<ObjectEntryFolder> list = findByG_C_P(
			groupId, companyId, parentObjectEntryFolderId, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object entry folder in the ordered set where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry folder
	 * @throws NoSuchObjectEntryFolderException if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder findByG_C_P_Last(
			long groupId, long companyId, long parentObjectEntryFolderId,
			OrderByComparator<ObjectEntryFolder> orderByComparator)
		throws NoSuchObjectEntryFolderException {

		ObjectEntryFolder objectEntryFolder = fetchByG_C_P_Last(
			groupId, companyId, parentObjectEntryFolderId, orderByComparator);

		if (objectEntryFolder != null) {
			return objectEntryFolder;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append(", parentObjectEntryFolderId=");
		sb.append(parentObjectEntryFolderId);

		sb.append("}");

		throw new NoSuchObjectEntryFolderException(sb.toString());
	}

	/**
	 * Returns the last object entry folder in the ordered set where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry folder, or <code>null</code> if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder fetchByG_C_P_Last(
		long groupId, long companyId, long parentObjectEntryFolderId,
		OrderByComparator<ObjectEntryFolder> orderByComparator) {

		int count = countByG_C_P(groupId, companyId, parentObjectEntryFolderId);

		if (count == 0) {
			return null;
		}

		List<ObjectEntryFolder> list = findByG_C_P(
			groupId, companyId, parentObjectEntryFolderId, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object entry folders before and after the current object entry folder in the ordered set where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63;.
	 *
	 * @param objectEntryFolderId the primary key of the current object entry folder
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry folder
	 * @throws NoSuchObjectEntryFolderException if a object entry folder with the primary key could not be found
	 */
	@Override
	public ObjectEntryFolder[] findByG_C_P_PrevAndNext(
			long objectEntryFolderId, long groupId, long companyId,
			long parentObjectEntryFolderId,
			OrderByComparator<ObjectEntryFolder> orderByComparator)
		throws NoSuchObjectEntryFolderException {

		ObjectEntryFolder objectEntryFolder = findByPrimaryKey(
			objectEntryFolderId);

		Session session = null;

		try {
			session = openSession();

			ObjectEntryFolder[] array = new ObjectEntryFolderImpl[3];

			array[0] = getByG_C_P_PrevAndNext(
				session, objectEntryFolder, groupId, companyId,
				parentObjectEntryFolderId, orderByComparator, true);

			array[1] = objectEntryFolder;

			array[2] = getByG_C_P_PrevAndNext(
				session, objectEntryFolder, groupId, companyId,
				parentObjectEntryFolderId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected ObjectEntryFolder getByG_C_P_PrevAndNext(
		Session session, ObjectEntryFolder objectEntryFolder, long groupId,
		long companyId, long parentObjectEntryFolderId,
		OrderByComparator<ObjectEntryFolder> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTENTRYFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_G_C_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_P_COMPANYID_2);

		sb.append(_FINDER_COLUMN_G_C_P_PARENTOBJECTENTRYFOLDERID_2);

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
			sb.append(ObjectEntryFolderModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(companyId);

		queryPos.add(parentObjectEntryFolderId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						objectEntryFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectEntryFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the object entry folders that the user has permission to view where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @return the matching object entry folders that the user has permission to view
	 */
	@Override
	public List<ObjectEntryFolder> filterFindByG_C_P(
		long groupId, long companyId, long parentObjectEntryFolderId) {

		return filterFindByG_C_P(
			groupId, companyId, parentObjectEntryFolderId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object entry folders that the user has permission to view where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @return the range of matching object entry folders that the user has permission to view
	 */
	@Override
	public List<ObjectEntryFolder> filterFindByG_C_P(
		long groupId, long companyId, long parentObjectEntryFolderId, int start,
		int end) {

		return filterFindByG_C_P(
			groupId, companyId, parentObjectEntryFolderId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object entry folders that the user has permissions to view where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entry folders that the user has permission to view
	 */
	@Override
	public List<ObjectEntryFolder> filterFindByG_C_P(
		long groupId, long companyId, long parentObjectEntryFolderId, int start,
		int end, OrderByComparator<ObjectEntryFolder> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_C_P(
				groupId, companyId, parentObjectEntryFolderId, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_C_P(
					groupId, companyId, parentObjectEntryFolderId,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator),
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
			sb.append(_FILTER_SQL_SELECT_OBJECTENTRYFOLDER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_OBJECTENTRYFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_C_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_P_COMPANYID_2);

		sb.append(_FINDER_COLUMN_G_C_P_PARENTOBJECTENTRYFOLDERID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_OBJECTENTRYFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
					ObjectEntryFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(ObjectEntryFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), ObjectEntryFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, ObjectEntryFolderImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, ObjectEntryFolderImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(companyId);

			queryPos.add(parentObjectEntryFolderId);

			return (List<ObjectEntryFolder>)QueryUtil.list(
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
	 * Returns the object entry folders before and after the current object entry folder in the ordered set of object entry folders that the user has permission to view where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63;.
	 *
	 * @param objectEntryFolderId the primary key of the current object entry folder
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry folder
	 * @throws NoSuchObjectEntryFolderException if a object entry folder with the primary key could not be found
	 */
	@Override
	public ObjectEntryFolder[] filterFindByG_C_P_PrevAndNext(
			long objectEntryFolderId, long groupId, long companyId,
			long parentObjectEntryFolderId,
			OrderByComparator<ObjectEntryFolder> orderByComparator)
		throws NoSuchObjectEntryFolderException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_C_P_PrevAndNext(
				objectEntryFolderId, groupId, companyId,
				parentObjectEntryFolderId, orderByComparator);
		}

		ObjectEntryFolder objectEntryFolder = findByPrimaryKey(
			objectEntryFolderId);

		Session session = null;

		try {
			session = openSession();

			ObjectEntryFolder[] array = new ObjectEntryFolderImpl[3];

			array[0] = filterGetByG_C_P_PrevAndNext(
				session, objectEntryFolder, groupId, companyId,
				parentObjectEntryFolderId, orderByComparator, true);

			array[1] = objectEntryFolder;

			array[2] = filterGetByG_C_P_PrevAndNext(
				session, objectEntryFolder, groupId, companyId,
				parentObjectEntryFolderId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected ObjectEntryFolder filterGetByG_C_P_PrevAndNext(
		Session session, ObjectEntryFolder objectEntryFolder, long groupId,
		long companyId, long parentObjectEntryFolderId,
		OrderByComparator<ObjectEntryFolder> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_OBJECTENTRYFOLDER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_OBJECTENTRYFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_C_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_P_COMPANYID_2);

		sb.append(_FINDER_COLUMN_G_C_P_PARENTOBJECTENTRYFOLDERID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_OBJECTENTRYFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
					ObjectEntryFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(ObjectEntryFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), ObjectEntryFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, ObjectEntryFolderImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, ObjectEntryFolderImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(companyId);

		queryPos.add(parentObjectEntryFolderId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						objectEntryFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectEntryFolder> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object entry folders where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 */
	@Override
	public void removeByG_C_P(
		long groupId, long companyId, long parentObjectEntryFolderId) {

		for (ObjectEntryFolder objectEntryFolder :
				findByG_C_P(
					groupId, companyId, parentObjectEntryFolderId,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(objectEntryFolder);
		}
	}

	/**
	 * Returns the number of object entry folders where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @return the number of matching object entry folders
	 */
	@Override
	public int countByG_C_P(
		long groupId, long companyId, long parentObjectEntryFolderId) {

		FinderPath finderPath = _finderPathCountByG_C_P;

		Object[] finderArgs = new Object[] {
			groupId, companyId, parentObjectEntryFolderId
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_COUNT_OBJECTENTRYFOLDER_WHERE);

			sb.append(_FINDER_COLUMN_G_C_P_GROUPID_2);

			sb.append(_FINDER_COLUMN_G_C_P_COMPANYID_2);

			sb.append(_FINDER_COLUMN_G_C_P_PARENTOBJECTENTRYFOLDERID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				queryPos.add(companyId);

				queryPos.add(parentObjectEntryFolderId);

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
	 * Returns the number of object entry folders that the user has permission to view where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @return the number of matching object entry folders that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_P(
		long groupId, long companyId, long parentObjectEntryFolderId) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_C_P(groupId, companyId, parentObjectEntryFolderId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<ObjectEntryFolder> objectEntryFolders = findByG_C_P(
				groupId, companyId, parentObjectEntryFolderId);

			objectEntryFolders = InlineSQLHelperUtil.filter(
				objectEntryFolders, groupId);

			return objectEntryFolders.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_OBJECTENTRYFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_G_C_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_P_COMPANYID_2);

		sb.append(_FINDER_COLUMN_G_C_P_PARENTOBJECTENTRYFOLDERID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), ObjectEntryFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(companyId);

			queryPos.add(parentObjectEntryFolderId);

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

	private static final String _FINDER_COLUMN_G_C_P_GROUPID_2 =
		"objectEntryFolder.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_P_COMPANYID_2 =
		"objectEntryFolder.companyId = ? AND ";

	private static final String
		_FINDER_COLUMN_G_C_P_PARENTOBJECTENTRYFOLDERID_2 =
			"objectEntryFolder.parentObjectEntryFolderId = ?";

	private FinderPath _finderPathWithPaginationFindByG_C_LikeT;
	private FinderPath _finderPathWithPaginationCountByG_C_LikeT;

	/**
	 * Returns all the object entry folders where groupId = &#63; and companyId = &#63; and treePath LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @return the matching object entry folders
	 */
	@Override
	public List<ObjectEntryFolder> findByG_C_LikeT(
		long groupId, long companyId, String treePath) {

		return findByG_C_LikeT(
			groupId, companyId, treePath, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the object entry folders where groupId = &#63; and companyId = &#63; and treePath LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @return the range of matching object entry folders
	 */
	@Override
	public List<ObjectEntryFolder> findByG_C_LikeT(
		long groupId, long companyId, String treePath, int start, int end) {

		return findByG_C_LikeT(groupId, companyId, treePath, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object entry folders where groupId = &#63; and companyId = &#63; and treePath LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entry folders
	 */
	@Override
	public List<ObjectEntryFolder> findByG_C_LikeT(
		long groupId, long companyId, String treePath, int start, int end,
		OrderByComparator<ObjectEntryFolder> orderByComparator) {

		return findByG_C_LikeT(
			groupId, companyId, treePath, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object entry folders where groupId = &#63; and companyId = &#63; and treePath LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entry folders
	 */
	@Override
	public List<ObjectEntryFolder> findByG_C_LikeT(
		long groupId, long companyId, String treePath, int start, int end,
		OrderByComparator<ObjectEntryFolder> orderByComparator,
		boolean useFinderCache) {

		treePath = Objects.toString(treePath, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		finderPath = _finderPathWithPaginationFindByG_C_LikeT;
		finderArgs = new Object[] {
			groupId, companyId, treePath, start, end, orderByComparator
		};

		List<ObjectEntryFolder> list = null;

		if (useFinderCache) {
			list = (List<ObjectEntryFolder>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectEntryFolder objectEntryFolder : list) {
					if ((groupId != objectEntryFolder.getGroupId()) ||
						(companyId != objectEntryFolder.getCompanyId()) ||
						!StringUtil.wildcardMatches(
							objectEntryFolder.getTreePath(), treePath, '_', '%',
							'\\', true)) {

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

			sb.append(_SQL_SELECT_OBJECTENTRYFOLDER_WHERE);

			sb.append(_FINDER_COLUMN_G_C_LIKET_GROUPID_2);

			sb.append(_FINDER_COLUMN_G_C_LIKET_COMPANYID_2);

			boolean bindTreePath = false;

			if (treePath.isEmpty()) {
				sb.append(_FINDER_COLUMN_G_C_LIKET_TREEPATH_3);
			}
			else {
				bindTreePath = true;

				sb.append(_FINDER_COLUMN_G_C_LIKET_TREEPATH_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectEntryFolderModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				queryPos.add(companyId);

				if (bindTreePath) {
					queryPos.add(treePath);
				}

				list = (List<ObjectEntryFolder>)QueryUtil.list(
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
	 * Returns the first object entry folder in the ordered set where groupId = &#63; and companyId = &#63; and treePath LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry folder
	 * @throws NoSuchObjectEntryFolderException if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder findByG_C_LikeT_First(
			long groupId, long companyId, String treePath,
			OrderByComparator<ObjectEntryFolder> orderByComparator)
		throws NoSuchObjectEntryFolderException {

		ObjectEntryFolder objectEntryFolder = fetchByG_C_LikeT_First(
			groupId, companyId, treePath, orderByComparator);

		if (objectEntryFolder != null) {
			return objectEntryFolder;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append(", treePathLIKE");
		sb.append(treePath);

		sb.append("}");

		throw new NoSuchObjectEntryFolderException(sb.toString());
	}

	/**
	 * Returns the first object entry folder in the ordered set where groupId = &#63; and companyId = &#63; and treePath LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry folder, or <code>null</code> if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder fetchByG_C_LikeT_First(
		long groupId, long companyId, String treePath,
		OrderByComparator<ObjectEntryFolder> orderByComparator) {

		List<ObjectEntryFolder> list = findByG_C_LikeT(
			groupId, companyId, treePath, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object entry folder in the ordered set where groupId = &#63; and companyId = &#63; and treePath LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry folder
	 * @throws NoSuchObjectEntryFolderException if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder findByG_C_LikeT_Last(
			long groupId, long companyId, String treePath,
			OrderByComparator<ObjectEntryFolder> orderByComparator)
		throws NoSuchObjectEntryFolderException {

		ObjectEntryFolder objectEntryFolder = fetchByG_C_LikeT_Last(
			groupId, companyId, treePath, orderByComparator);

		if (objectEntryFolder != null) {
			return objectEntryFolder;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append(", treePathLIKE");
		sb.append(treePath);

		sb.append("}");

		throw new NoSuchObjectEntryFolderException(sb.toString());
	}

	/**
	 * Returns the last object entry folder in the ordered set where groupId = &#63; and companyId = &#63; and treePath LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry folder, or <code>null</code> if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder fetchByG_C_LikeT_Last(
		long groupId, long companyId, String treePath,
		OrderByComparator<ObjectEntryFolder> orderByComparator) {

		int count = countByG_C_LikeT(groupId, companyId, treePath);

		if (count == 0) {
			return null;
		}

		List<ObjectEntryFolder> list = findByG_C_LikeT(
			groupId, companyId, treePath, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object entry folders before and after the current object entry folder in the ordered set where groupId = &#63; and companyId = &#63; and treePath LIKE &#63;.
	 *
	 * @param objectEntryFolderId the primary key of the current object entry folder
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry folder
	 * @throws NoSuchObjectEntryFolderException if a object entry folder with the primary key could not be found
	 */
	@Override
	public ObjectEntryFolder[] findByG_C_LikeT_PrevAndNext(
			long objectEntryFolderId, long groupId, long companyId,
			String treePath,
			OrderByComparator<ObjectEntryFolder> orderByComparator)
		throws NoSuchObjectEntryFolderException {

		treePath = Objects.toString(treePath, "");

		ObjectEntryFolder objectEntryFolder = findByPrimaryKey(
			objectEntryFolderId);

		Session session = null;

		try {
			session = openSession();

			ObjectEntryFolder[] array = new ObjectEntryFolderImpl[3];

			array[0] = getByG_C_LikeT_PrevAndNext(
				session, objectEntryFolder, groupId, companyId, treePath,
				orderByComparator, true);

			array[1] = objectEntryFolder;

			array[2] = getByG_C_LikeT_PrevAndNext(
				session, objectEntryFolder, groupId, companyId, treePath,
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

	protected ObjectEntryFolder getByG_C_LikeT_PrevAndNext(
		Session session, ObjectEntryFolder objectEntryFolder, long groupId,
		long companyId, String treePath,
		OrderByComparator<ObjectEntryFolder> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTENTRYFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_G_C_LIKET_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_LIKET_COMPANYID_2);

		boolean bindTreePath = false;

		if (treePath.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_C_LIKET_TREEPATH_3);
		}
		else {
			bindTreePath = true;

			sb.append(_FINDER_COLUMN_G_C_LIKET_TREEPATH_2);
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
			sb.append(ObjectEntryFolderModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(companyId);

		if (bindTreePath) {
			queryPos.add(treePath);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						objectEntryFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectEntryFolder> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the object entry folders that the user has permission to view where groupId = &#63; and companyId = &#63; and treePath LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @return the matching object entry folders that the user has permission to view
	 */
	@Override
	public List<ObjectEntryFolder> filterFindByG_C_LikeT(
		long groupId, long companyId, String treePath) {

		return filterFindByG_C_LikeT(
			groupId, companyId, treePath, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the object entry folders that the user has permission to view where groupId = &#63; and companyId = &#63; and treePath LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @return the range of matching object entry folders that the user has permission to view
	 */
	@Override
	public List<ObjectEntryFolder> filterFindByG_C_LikeT(
		long groupId, long companyId, String treePath, int start, int end) {

		return filterFindByG_C_LikeT(
			groupId, companyId, treePath, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object entry folders that the user has permissions to view where groupId = &#63; and companyId = &#63; and treePath LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entry folders that the user has permission to view
	 */
	@Override
	public List<ObjectEntryFolder> filterFindByG_C_LikeT(
		long groupId, long companyId, String treePath, int start, int end,
		OrderByComparator<ObjectEntryFolder> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_C_LikeT(
				groupId, companyId, treePath, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_C_LikeT(
					groupId, companyId, treePath, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		treePath = Objects.toString(treePath, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_OBJECTENTRYFOLDER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_OBJECTENTRYFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_C_LIKET_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_LIKET_COMPANYID_2);

		boolean bindTreePath = false;

		if (treePath.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_C_LIKET_TREEPATH_3);
		}
		else {
			bindTreePath = true;

			sb.append(_FINDER_COLUMN_G_C_LIKET_TREEPATH_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_OBJECTENTRYFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
					ObjectEntryFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(ObjectEntryFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), ObjectEntryFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, ObjectEntryFolderImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, ObjectEntryFolderImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(companyId);

			if (bindTreePath) {
				queryPos.add(treePath);
			}

			return (List<ObjectEntryFolder>)QueryUtil.list(
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
	 * Returns the object entry folders before and after the current object entry folder in the ordered set of object entry folders that the user has permission to view where groupId = &#63; and companyId = &#63; and treePath LIKE &#63;.
	 *
	 * @param objectEntryFolderId the primary key of the current object entry folder
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry folder
	 * @throws NoSuchObjectEntryFolderException if a object entry folder with the primary key could not be found
	 */
	@Override
	public ObjectEntryFolder[] filterFindByG_C_LikeT_PrevAndNext(
			long objectEntryFolderId, long groupId, long companyId,
			String treePath,
			OrderByComparator<ObjectEntryFolder> orderByComparator)
		throws NoSuchObjectEntryFolderException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_C_LikeT_PrevAndNext(
				objectEntryFolderId, groupId, companyId, treePath,
				orderByComparator);
		}

		treePath = Objects.toString(treePath, "");

		ObjectEntryFolder objectEntryFolder = findByPrimaryKey(
			objectEntryFolderId);

		Session session = null;

		try {
			session = openSession();

			ObjectEntryFolder[] array = new ObjectEntryFolderImpl[3];

			array[0] = filterGetByG_C_LikeT_PrevAndNext(
				session, objectEntryFolder, groupId, companyId, treePath,
				orderByComparator, true);

			array[1] = objectEntryFolder;

			array[2] = filterGetByG_C_LikeT_PrevAndNext(
				session, objectEntryFolder, groupId, companyId, treePath,
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

	protected ObjectEntryFolder filterGetByG_C_LikeT_PrevAndNext(
		Session session, ObjectEntryFolder objectEntryFolder, long groupId,
		long companyId, String treePath,
		OrderByComparator<ObjectEntryFolder> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_OBJECTENTRYFOLDER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_OBJECTENTRYFOLDER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_C_LIKET_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_LIKET_COMPANYID_2);

		boolean bindTreePath = false;

		if (treePath.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_C_LIKET_TREEPATH_3);
		}
		else {
			bindTreePath = true;

			sb.append(_FINDER_COLUMN_G_C_LIKET_TREEPATH_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_OBJECTENTRYFOLDER_NO_INLINE_DISTINCT_WHERE_2);
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
					ObjectEntryFolderModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(ObjectEntryFolderModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), ObjectEntryFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, ObjectEntryFolderImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, ObjectEntryFolderImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(companyId);

		if (bindTreePath) {
			queryPos.add(treePath);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						objectEntryFolder)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectEntryFolder> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object entry folders where groupId = &#63; and companyId = &#63; and treePath LIKE &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param treePath the tree path
	 */
	@Override
	public void removeByG_C_LikeT(
		long groupId, long companyId, String treePath) {

		for (ObjectEntryFolder objectEntryFolder :
				findByG_C_LikeT(
					groupId, companyId, treePath, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(objectEntryFolder);
		}
	}

	/**
	 * Returns the number of object entry folders where groupId = &#63; and companyId = &#63; and treePath LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @return the number of matching object entry folders
	 */
	@Override
	public int countByG_C_LikeT(long groupId, long companyId, String treePath) {
		treePath = Objects.toString(treePath, "");

		FinderPath finderPath = _finderPathWithPaginationCountByG_C_LikeT;

		Object[] finderArgs = new Object[] {groupId, companyId, treePath};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_COUNT_OBJECTENTRYFOLDER_WHERE);

			sb.append(_FINDER_COLUMN_G_C_LIKET_GROUPID_2);

			sb.append(_FINDER_COLUMN_G_C_LIKET_COMPANYID_2);

			boolean bindTreePath = false;

			if (treePath.isEmpty()) {
				sb.append(_FINDER_COLUMN_G_C_LIKET_TREEPATH_3);
			}
			else {
				bindTreePath = true;

				sb.append(_FINDER_COLUMN_G_C_LIKET_TREEPATH_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				queryPos.add(companyId);

				if (bindTreePath) {
					queryPos.add(treePath);
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
	 * Returns the number of object entry folders that the user has permission to view where groupId = &#63; and companyId = &#63; and treePath LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @return the number of matching object entry folders that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_LikeT(
		long groupId, long companyId, String treePath) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_C_LikeT(groupId, companyId, treePath);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<ObjectEntryFolder> objectEntryFolders = findByG_C_LikeT(
				groupId, companyId, treePath);

			objectEntryFolders = InlineSQLHelperUtil.filter(
				objectEntryFolders, groupId);

			return objectEntryFolders.size();
		}

		treePath = Objects.toString(treePath, "");

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_OBJECTENTRYFOLDER_WHERE);

		sb.append(_FINDER_COLUMN_G_C_LIKET_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_LIKET_COMPANYID_2);

		boolean bindTreePath = false;

		if (treePath.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_C_LIKET_TREEPATH_3);
		}
		else {
			bindTreePath = true;

			sb.append(_FINDER_COLUMN_G_C_LIKET_TREEPATH_2);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), ObjectEntryFolder.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(companyId);

			if (bindTreePath) {
				queryPos.add(treePath);
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

	private static final String _FINDER_COLUMN_G_C_LIKET_GROUPID_2 =
		"objectEntryFolder.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_LIKET_COMPANYID_2 =
		"objectEntryFolder.companyId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_LIKET_TREEPATH_2 =
		"objectEntryFolder.treePath LIKE ?";

	private static final String _FINDER_COLUMN_G_C_LIKET_TREEPATH_3 =
		"(objectEntryFolder.treePath IS NULL OR objectEntryFolder.treePath LIKE '')";

	private FinderPath _finderPathFetchByG_C_P_N;

	/**
	 * Returns the object entry folder where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63; and name = &#63; or throws a <code>NoSuchObjectEntryFolderException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param name the name
	 * @return the matching object entry folder
	 * @throws NoSuchObjectEntryFolderException if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder findByG_C_P_N(
			long groupId, long companyId, long parentObjectEntryFolderId,
			String name)
		throws NoSuchObjectEntryFolderException {

		ObjectEntryFolder objectEntryFolder = fetchByG_C_P_N(
			groupId, companyId, parentObjectEntryFolderId, name);

		if (objectEntryFolder == null) {
			StringBundler sb = new StringBundler(10);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("groupId=");
			sb.append(groupId);

			sb.append(", companyId=");
			sb.append(companyId);

			sb.append(", parentObjectEntryFolderId=");
			sb.append(parentObjectEntryFolderId);

			sb.append(", name=");
			sb.append(name);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchObjectEntryFolderException(sb.toString());
		}

		return objectEntryFolder;
	}

	/**
	 * Returns the object entry folder where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param name the name
	 * @return the matching object entry folder, or <code>null</code> if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder fetchByG_C_P_N(
		long groupId, long companyId, long parentObjectEntryFolderId,
		String name) {

		return fetchByG_C_P_N(
			groupId, companyId, parentObjectEntryFolderId, name, true);
	}

	/**
	 * Returns the object entry folder where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object entry folder, or <code>null</code> if a matching object entry folder could not be found
	 */
	@Override
	public ObjectEntryFolder fetchByG_C_P_N(
		long groupId, long companyId, long parentObjectEntryFolderId,
		String name, boolean useFinderCache) {

		name = Objects.toString(name, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {
				groupId, companyId, parentObjectEntryFolderId, name
			};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByG_C_P_N, finderArgs, this);
		}

		if (result instanceof ObjectEntryFolder) {
			ObjectEntryFolder objectEntryFolder = (ObjectEntryFolder)result;

			if ((groupId != objectEntryFolder.getGroupId()) ||
				(companyId != objectEntryFolder.getCompanyId()) ||
				(parentObjectEntryFolderId !=
					objectEntryFolder.getParentObjectEntryFolderId()) ||
				!Objects.equals(name, objectEntryFolder.getName())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_SQL_SELECT_OBJECTENTRYFOLDER_WHERE);

			sb.append(_FINDER_COLUMN_G_C_P_N_GROUPID_2);

			sb.append(_FINDER_COLUMN_G_C_P_N_COMPANYID_2);

			sb.append(_FINDER_COLUMN_G_C_P_N_PARENTOBJECTENTRYFOLDERID_2);

			boolean bindName = false;

			if (name.isEmpty()) {
				sb.append(_FINDER_COLUMN_G_C_P_N_NAME_3);
			}
			else {
				bindName = true;

				sb.append(_FINDER_COLUMN_G_C_P_N_NAME_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				queryPos.add(companyId);

				queryPos.add(parentObjectEntryFolderId);

				if (bindName) {
					queryPos.add(name);
				}

				List<ObjectEntryFolder> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByG_C_P_N, finderArgs, list);
					}
				}
				else {
					ObjectEntryFolder objectEntryFolder = list.get(0);

					result = objectEntryFolder;

					cacheResult(objectEntryFolder);
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
			return (ObjectEntryFolder)result;
		}
	}

	/**
	 * Removes the object entry folder where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param name the name
	 * @return the object entry folder that was removed
	 */
	@Override
	public ObjectEntryFolder removeByG_C_P_N(
			long groupId, long companyId, long parentObjectEntryFolderId,
			String name)
		throws NoSuchObjectEntryFolderException {

		ObjectEntryFolder objectEntryFolder = findByG_C_P_N(
			groupId, companyId, parentObjectEntryFolderId, name);

		return remove(objectEntryFolder);
	}

	/**
	 * Returns the number of object entry folders where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param name the name
	 * @return the number of matching object entry folders
	 */
	@Override
	public int countByG_C_P_N(
		long groupId, long companyId, long parentObjectEntryFolderId,
		String name) {

		ObjectEntryFolder objectEntryFolder = fetchByG_C_P_N(
			groupId, companyId, parentObjectEntryFolderId, name);

		if (objectEntryFolder == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_G_C_P_N_GROUPID_2 =
		"objectEntryFolder.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_P_N_COMPANYID_2 =
		"objectEntryFolder.companyId = ? AND ";

	private static final String
		_FINDER_COLUMN_G_C_P_N_PARENTOBJECTENTRYFOLDERID_2 =
			"objectEntryFolder.parentObjectEntryFolderId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_P_N_NAME_2 =
		"objectEntryFolder.name = ?";

	private static final String _FINDER_COLUMN_G_C_P_N_NAME_3 =
		"(objectEntryFolder.name IS NULL OR objectEntryFolder.name = '')";

	public ObjectEntryFolderPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ObjectEntryFolder.class);

		setModelImplClass(ObjectEntryFolderImpl.class);
		setModelPKClass(long.class);

		setTable(ObjectEntryFolderTable.INSTANCE);
	}

	/**
	 * Caches the object entry folder in the entity cache if it is enabled.
	 *
	 * @param objectEntryFolder the object entry folder
	 */
	@Override
	public void cacheResult(ObjectEntryFolder objectEntryFolder) {
		entityCache.putResult(
			ObjectEntryFolderImpl.class, objectEntryFolder.getPrimaryKey(),
			objectEntryFolder);

		finderCache.putResult(
			_finderPathFetchByUUID_G,
			new Object[] {
				objectEntryFolder.getUuid(), objectEntryFolder.getGroupId()
			},
			objectEntryFolder);

		finderCache.putResult(
			_finderPathFetchByERC_G_C,
			new Object[] {
				objectEntryFolder.getExternalReferenceCode(),
				objectEntryFolder.getGroupId(), objectEntryFolder.getCompanyId()
			},
			objectEntryFolder);

		finderCache.putResult(
			_finderPathFetchByG_C_P_N,
			new Object[] {
				objectEntryFolder.getGroupId(),
				objectEntryFolder.getCompanyId(),
				objectEntryFolder.getParentObjectEntryFolderId(),
				objectEntryFolder.getName()
			},
			objectEntryFolder);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the object entry folders in the entity cache if it is enabled.
	 *
	 * @param objectEntryFolders the object entry folders
	 */
	@Override
	public void cacheResult(List<ObjectEntryFolder> objectEntryFolders) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (objectEntryFolders.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (ObjectEntryFolder objectEntryFolder : objectEntryFolders) {
			if (entityCache.getResult(
					ObjectEntryFolderImpl.class,
					objectEntryFolder.getPrimaryKey()) == null) {

				cacheResult(objectEntryFolder);
			}
		}
	}

	/**
	 * Clears the cache for all object entry folders.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(ObjectEntryFolderImpl.class);

		finderCache.clearCache(ObjectEntryFolderImpl.class);
	}

	/**
	 * Clears the cache for the object entry folder.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(ObjectEntryFolder objectEntryFolder) {
		entityCache.removeResult(
			ObjectEntryFolderImpl.class, objectEntryFolder);
	}

	@Override
	public void clearCache(List<ObjectEntryFolder> objectEntryFolders) {
		for (ObjectEntryFolder objectEntryFolder : objectEntryFolders) {
			entityCache.removeResult(
				ObjectEntryFolderImpl.class, objectEntryFolder);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(ObjectEntryFolderImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(ObjectEntryFolderImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		ObjectEntryFolderModelImpl objectEntryFolderModelImpl) {

		Object[] args = new Object[] {
			objectEntryFolderModelImpl.getUuid(),
			objectEntryFolderModelImpl.getGroupId()
		};

		finderCache.putResult(
			_finderPathFetchByUUID_G, args, objectEntryFolderModelImpl);

		args = new Object[] {
			objectEntryFolderModelImpl.getExternalReferenceCode(),
			objectEntryFolderModelImpl.getGroupId(),
			objectEntryFolderModelImpl.getCompanyId()
		};

		finderCache.putResult(
			_finderPathFetchByERC_G_C, args, objectEntryFolderModelImpl);

		args = new Object[] {
			objectEntryFolderModelImpl.getGroupId(),
			objectEntryFolderModelImpl.getCompanyId(),
			objectEntryFolderModelImpl.getParentObjectEntryFolderId(),
			objectEntryFolderModelImpl.getName()
		};

		finderCache.putResult(
			_finderPathFetchByG_C_P_N, args, objectEntryFolderModelImpl);
	}

	/**
	 * Creates a new object entry folder with the primary key. Does not add the object entry folder to the database.
	 *
	 * @param objectEntryFolderId the primary key for the new object entry folder
	 * @return the new object entry folder
	 */
	@Override
	public ObjectEntryFolder create(long objectEntryFolderId) {
		ObjectEntryFolder objectEntryFolder = new ObjectEntryFolderImpl();

		objectEntryFolder.setNew(true);
		objectEntryFolder.setPrimaryKey(objectEntryFolderId);

		String uuid = PortalUUIDUtil.generate();

		objectEntryFolder.setUuid(uuid);

		objectEntryFolder.setCompanyId(CompanyThreadLocal.getCompanyId());

		return objectEntryFolder;
	}

	/**
	 * Removes the object entry folder with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectEntryFolderId the primary key of the object entry folder
	 * @return the object entry folder that was removed
	 * @throws NoSuchObjectEntryFolderException if a object entry folder with the primary key could not be found
	 */
	@Override
	public ObjectEntryFolder remove(long objectEntryFolderId)
		throws NoSuchObjectEntryFolderException {

		return remove((Serializable)objectEntryFolderId);
	}

	/**
	 * Removes the object entry folder with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the object entry folder
	 * @return the object entry folder that was removed
	 * @throws NoSuchObjectEntryFolderException if a object entry folder with the primary key could not be found
	 */
	@Override
	public ObjectEntryFolder remove(Serializable primaryKey)
		throws NoSuchObjectEntryFolderException {

		Session session = null;

		try {
			session = openSession();

			ObjectEntryFolder objectEntryFolder =
				(ObjectEntryFolder)session.get(
					ObjectEntryFolderImpl.class, primaryKey);

			if (objectEntryFolder == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchObjectEntryFolderException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(objectEntryFolder);
		}
		catch (NoSuchObjectEntryFolderException noSuchEntityException) {
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
	protected ObjectEntryFolder removeImpl(
		ObjectEntryFolder objectEntryFolder) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(objectEntryFolder)) {
				objectEntryFolder = (ObjectEntryFolder)session.get(
					ObjectEntryFolderImpl.class,
					objectEntryFolder.getPrimaryKeyObj());
			}

			if (objectEntryFolder != null) {
				session.delete(objectEntryFolder);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (objectEntryFolder != null) {
			clearCache(objectEntryFolder);
		}

		return objectEntryFolder;
	}

	@Override
	public ObjectEntryFolder updateImpl(ObjectEntryFolder objectEntryFolder) {
		boolean isNew = objectEntryFolder.isNew();

		if (!(objectEntryFolder instanceof ObjectEntryFolderModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(objectEntryFolder.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					objectEntryFolder);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in objectEntryFolder proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ObjectEntryFolder implementation " +
					objectEntryFolder.getClass());
		}

		ObjectEntryFolderModelImpl objectEntryFolderModelImpl =
			(ObjectEntryFolderModelImpl)objectEntryFolder;

		if (Validator.isNull(objectEntryFolder.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			objectEntryFolder.setUuid(uuid);
		}

		if (Validator.isNull(objectEntryFolder.getExternalReferenceCode())) {
			objectEntryFolder.setExternalReferenceCode(
				objectEntryFolder.getUuid());
		}
		else {
			if (!Objects.equals(
					objectEntryFolderModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					objectEntryFolder.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = objectEntryFolder.getCompanyId();

					long groupId = objectEntryFolder.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = objectEntryFolder.getPrimaryKey();
					}

					try {
						objectEntryFolder.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								ObjectEntryFolder.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								objectEntryFolder.getExternalReferenceCode(),
								null));
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

		if (isNew && (objectEntryFolder.getCreateDate() == null)) {
			if (serviceContext == null) {
				objectEntryFolder.setCreateDate(date);
			}
			else {
				objectEntryFolder.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!objectEntryFolderModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				objectEntryFolder.setModifiedDate(date);
			}
			else {
				objectEntryFolder.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(objectEntryFolder);
			}
			else {
				objectEntryFolder = (ObjectEntryFolder)session.merge(
					objectEntryFolder);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			ObjectEntryFolderImpl.class, objectEntryFolderModelImpl, false,
			true);

		cacheUniqueFindersCache(objectEntryFolderModelImpl);

		if (isNew) {
			objectEntryFolder.setNew(false);
		}

		objectEntryFolder.resetOriginalValues();

		return objectEntryFolder;
	}

	/**
	 * Returns the object entry folder with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the object entry folder
	 * @return the object entry folder
	 * @throws NoSuchObjectEntryFolderException if a object entry folder with the primary key could not be found
	 */
	@Override
	public ObjectEntryFolder findByPrimaryKey(Serializable primaryKey)
		throws NoSuchObjectEntryFolderException {

		ObjectEntryFolder objectEntryFolder = fetchByPrimaryKey(primaryKey);

		if (objectEntryFolder == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchObjectEntryFolderException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return objectEntryFolder;
	}

	/**
	 * Returns the object entry folder with the primary key or throws a <code>NoSuchObjectEntryFolderException</code> if it could not be found.
	 *
	 * @param objectEntryFolderId the primary key of the object entry folder
	 * @return the object entry folder
	 * @throws NoSuchObjectEntryFolderException if a object entry folder with the primary key could not be found
	 */
	@Override
	public ObjectEntryFolder findByPrimaryKey(long objectEntryFolderId)
		throws NoSuchObjectEntryFolderException {

		return findByPrimaryKey((Serializable)objectEntryFolderId);
	}

	/**
	 * Returns the object entry folder with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectEntryFolderId the primary key of the object entry folder
	 * @return the object entry folder, or <code>null</code> if a object entry folder with the primary key could not be found
	 */
	@Override
	public ObjectEntryFolder fetchByPrimaryKey(long objectEntryFolderId) {
		return fetchByPrimaryKey((Serializable)objectEntryFolderId);
	}

	/**
	 * Returns all the object entry folders.
	 *
	 * @return the object entry folders
	 */
	@Override
	public List<ObjectEntryFolder> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object entry folders.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @return the range of object entry folders
	 */
	@Override
	public List<ObjectEntryFolder> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the object entry folders.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of object entry folders
	 */
	@Override
	public List<ObjectEntryFolder> findAll(
		int start, int end,
		OrderByComparator<ObjectEntryFolder> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object entry folders.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of object entry folders
	 */
	@Override
	public List<ObjectEntryFolder> findAll(
		int start, int end,
		OrderByComparator<ObjectEntryFolder> orderByComparator,
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

		List<ObjectEntryFolder> list = null;

		if (useFinderCache) {
			list = (List<ObjectEntryFolder>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_OBJECTENTRYFOLDER);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_OBJECTENTRYFOLDER;

				sql = sql.concat(ObjectEntryFolderModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<ObjectEntryFolder>)QueryUtil.list(
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
	 * Removes all the object entry folders from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (ObjectEntryFolder objectEntryFolder : findAll()) {
			remove(objectEntryFolder);
		}
	}

	/**
	 * Returns the number of object entry folders.
	 *
	 * @return the number of object entry folders
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_OBJECTENTRYFOLDER);

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
		return "objectEntryFolderId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OBJECTENTRYFOLDER;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ObjectEntryFolderModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the object entry folder persistence.
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

		_finderPathFetchByERC_G_C = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_G_C",
			new String[] {
				String.class.getName(), Long.class.getName(),
				Long.class.getName()
			},
			new String[] {"externalReferenceCode", "groupId", "companyId"},
			true);

		_finderPathWithPaginationFindByG_C_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_P",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "companyId", "parentObjectEntryFolderId"},
			true);

		_finderPathWithoutPaginationFindByG_C_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_P",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"groupId", "companyId", "parentObjectEntryFolderId"},
			true);

		_finderPathCountByG_C_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_P",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"groupId", "companyId", "parentObjectEntryFolderId"},
			false);

		_finderPathWithPaginationFindByG_C_LikeT = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_LikeT",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "companyId", "treePath"}, true);

		_finderPathWithPaginationCountByG_C_LikeT = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_C_LikeT",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName()
			},
			new String[] {"groupId", "companyId", "treePath"}, false);

		_finderPathFetchByG_C_P_N = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByG_C_P_N",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), String.class.getName()
			},
			new String[] {
				"groupId", "companyId", "parentObjectEntryFolderId", "name"
			},
			true);

		ObjectEntryFolderUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ObjectEntryFolderUtil.setPersistence(null);

		entityCache.removeCache(ObjectEntryFolderImpl.class.getName());
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

	private static final String _SQL_SELECT_OBJECTENTRYFOLDER =
		"SELECT objectEntryFolder FROM ObjectEntryFolder objectEntryFolder";

	private static final String _SQL_SELECT_OBJECTENTRYFOLDER_WHERE =
		"SELECT objectEntryFolder FROM ObjectEntryFolder objectEntryFolder WHERE ";

	private static final String _SQL_COUNT_OBJECTENTRYFOLDER =
		"SELECT COUNT(objectEntryFolder) FROM ObjectEntryFolder objectEntryFolder";

	private static final String _SQL_COUNT_OBJECTENTRYFOLDER_WHERE =
		"SELECT COUNT(objectEntryFolder) FROM ObjectEntryFolder objectEntryFolder WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"objectEntryFolder.objectEntryFolderId";

	private static final String _FILTER_SQL_SELECT_OBJECTENTRYFOLDER_WHERE =
		"SELECT DISTINCT {objectEntryFolder.*} FROM ObjectEntryFolder objectEntryFolder WHERE ";

	private static final String
		_FILTER_SQL_SELECT_OBJECTENTRYFOLDER_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {ObjectEntryFolder.*} FROM (SELECT DISTINCT objectEntryFolder.objectEntryFolderId FROM ObjectEntryFolder objectEntryFolder WHERE ";

	private static final String
		_FILTER_SQL_SELECT_OBJECTENTRYFOLDER_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN ObjectEntryFolder ON TEMP_TABLE.objectEntryFolderId = ObjectEntryFolder.objectEntryFolderId";

	private static final String _FILTER_SQL_COUNT_OBJECTENTRYFOLDER_WHERE =
		"SELECT COUNT(DISTINCT objectEntryFolder.objectEntryFolderId) AS COUNT_VALUE FROM ObjectEntryFolder objectEntryFolder WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "objectEntryFolder";

	private static final String _FILTER_ENTITY_TABLE = "ObjectEntryFolder";

	private static final String _ORDER_BY_ENTITY_ALIAS = "objectEntryFolder.";

	private static final String _ORDER_BY_ENTITY_TABLE = "ObjectEntryFolder.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No ObjectEntryFolder exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ObjectEntryFolder exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryFolderPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}