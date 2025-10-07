/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.impl;

import com.liferay.object.exception.NoSuchObjectEntryVersionException;
import com.liferay.object.model.ObjectEntryVersion;
import com.liferay.object.model.ObjectEntryVersionTable;
import com.liferay.object.model.impl.ObjectEntryVersionImpl;
import com.liferay.object.model.impl.ObjectEntryVersionModelImpl;
import com.liferay.object.service.persistence.ObjectEntryVersionPersistence;
import com.liferay.object.service.persistence.ObjectEntryVersionUtil;
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
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
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
 * The persistence implementation for the object entry version service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = ObjectEntryVersionPersistence.class)
public class ObjectEntryVersionPersistenceImpl
	extends BasePersistenceImpl<ObjectEntryVersion>
	implements ObjectEntryVersionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ObjectEntryVersionUtil</code> to access the object entry version persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ObjectEntryVersionImpl.class.getName();

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
	 * Returns all the object entry versions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching object entry versions
	 */
	@Override
	public List<ObjectEntryVersion> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object entry versions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @return the range of matching object entry versions
	 */
	@Override
	public List<ObjectEntryVersion> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object entry versions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entry versions
	 */
	@Override
	public List<ObjectEntryVersion> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object entry versions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entry versions
	 */
	@Override
	public List<ObjectEntryVersion> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectEntryVersion> orderByComparator,
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

		List<ObjectEntryVersion> list = null;

		if (useFinderCache) {
			list = (List<ObjectEntryVersion>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectEntryVersion objectEntryVersion : list) {
					if (!uuid.equals(objectEntryVersion.getUuid())) {
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

			sb.append(_SQL_SELECT_OBJECTENTRYVERSION_WHERE);

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
				sb.append(ObjectEntryVersionModelImpl.ORDER_BY_JPQL);
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

				list = (List<ObjectEntryVersion>)QueryUtil.list(
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
	 * Returns the first object entry version in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion findByUuid_First(
			String uuid,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws NoSuchObjectEntryVersionException {

		ObjectEntryVersion objectEntryVersion = fetchByUuid_First(
			uuid, orderByComparator);

		if (objectEntryVersion != null) {
			return objectEntryVersion;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchObjectEntryVersionException(sb.toString());
	}

	/**
	 * Returns the first object entry version in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion fetchByUuid_First(
		String uuid, OrderByComparator<ObjectEntryVersion> orderByComparator) {

		List<ObjectEntryVersion> list = findByUuid(
			uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object entry version in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion findByUuid_Last(
			String uuid,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws NoSuchObjectEntryVersionException {

		ObjectEntryVersion objectEntryVersion = fetchByUuid_Last(
			uuid, orderByComparator);

		if (objectEntryVersion != null) {
			return objectEntryVersion;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchObjectEntryVersionException(sb.toString());
	}

	/**
	 * Returns the last object entry version in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion fetchByUuid_Last(
		String uuid, OrderByComparator<ObjectEntryVersion> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<ObjectEntryVersion> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object entry versions before and after the current object entry version in the ordered set where uuid = &#63;.
	 *
	 * @param objectEntryVersionId the primary key of the current object entry version
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry version
	 * @throws NoSuchObjectEntryVersionException if a object entry version with the primary key could not be found
	 */
	@Override
	public ObjectEntryVersion[] findByUuid_PrevAndNext(
			long objectEntryVersionId, String uuid,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws NoSuchObjectEntryVersionException {

		uuid = Objects.toString(uuid, "");

		ObjectEntryVersion objectEntryVersion = findByPrimaryKey(
			objectEntryVersionId);

		Session session = null;

		try {
			session = openSession();

			ObjectEntryVersion[] array = new ObjectEntryVersionImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, objectEntryVersion, uuid, orderByComparator, true);

			array[1] = objectEntryVersion;

			array[2] = getByUuid_PrevAndNext(
				session, objectEntryVersion, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected ObjectEntryVersion getByUuid_PrevAndNext(
		Session session, ObjectEntryVersion objectEntryVersion, String uuid,
		OrderByComparator<ObjectEntryVersion> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTENTRYVERSION_WHERE);

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
			sb.append(ObjectEntryVersionModelImpl.ORDER_BY_JPQL);
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
						objectEntryVersion)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectEntryVersion> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object entry versions where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (ObjectEntryVersion objectEntryVersion :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(objectEntryVersion);
		}
	}

	/**
	 * Returns the number of object entry versions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object entry versions
	 */
	@Override
	public int countByUuid(String uuid) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid;

		Object[] finderArgs = new Object[] {uuid};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_OBJECTENTRYVERSION_WHERE);

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
		"objectEntryVersion.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(objectEntryVersion.uuid IS NULL OR objectEntryVersion.uuid = '')";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the object entry versions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching object entry versions
	 */
	@Override
	public List<ObjectEntryVersion> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object entry versions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @return the range of matching object entry versions
	 */
	@Override
	public List<ObjectEntryVersion> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object entry versions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entry versions
	 */
	@Override
	public List<ObjectEntryVersion> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object entry versions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entry versions
	 */
	@Override
	public List<ObjectEntryVersion> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectEntryVersion> orderByComparator,
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

		List<ObjectEntryVersion> list = null;

		if (useFinderCache) {
			list = (List<ObjectEntryVersion>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectEntryVersion objectEntryVersion : list) {
					if (!uuid.equals(objectEntryVersion.getUuid()) ||
						(companyId != objectEntryVersion.getCompanyId())) {

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

			sb.append(_SQL_SELECT_OBJECTENTRYVERSION_WHERE);

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
				sb.append(ObjectEntryVersionModelImpl.ORDER_BY_JPQL);
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

				list = (List<ObjectEntryVersion>)QueryUtil.list(
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
	 * Returns the first object entry version in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws NoSuchObjectEntryVersionException {

		ObjectEntryVersion objectEntryVersion = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (objectEntryVersion != null) {
			return objectEntryVersion;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchObjectEntryVersionException(sb.toString());
	}

	/**
	 * Returns the first object entry version in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		List<ObjectEntryVersion> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object entry version in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws NoSuchObjectEntryVersionException {

		ObjectEntryVersion objectEntryVersion = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (objectEntryVersion != null) {
			return objectEntryVersion;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchObjectEntryVersionException(sb.toString());
	}

	/**
	 * Returns the last object entry version in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<ObjectEntryVersion> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object entry versions before and after the current object entry version in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param objectEntryVersionId the primary key of the current object entry version
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry version
	 * @throws NoSuchObjectEntryVersionException if a object entry version with the primary key could not be found
	 */
	@Override
	public ObjectEntryVersion[] findByUuid_C_PrevAndNext(
			long objectEntryVersionId, String uuid, long companyId,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws NoSuchObjectEntryVersionException {

		uuid = Objects.toString(uuid, "");

		ObjectEntryVersion objectEntryVersion = findByPrimaryKey(
			objectEntryVersionId);

		Session session = null;

		try {
			session = openSession();

			ObjectEntryVersion[] array = new ObjectEntryVersionImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, objectEntryVersion, uuid, companyId, orderByComparator,
				true);

			array[1] = objectEntryVersion;

			array[2] = getByUuid_C_PrevAndNext(
				session, objectEntryVersion, uuid, companyId, orderByComparator,
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

	protected ObjectEntryVersion getByUuid_C_PrevAndNext(
		Session session, ObjectEntryVersion objectEntryVersion, String uuid,
		long companyId, OrderByComparator<ObjectEntryVersion> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTENTRYVERSION_WHERE);

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
			sb.append(ObjectEntryVersionModelImpl.ORDER_BY_JPQL);
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
						objectEntryVersion)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectEntryVersion> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object entry versions where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (ObjectEntryVersion objectEntryVersion :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(objectEntryVersion);
		}
	}

	/**
	 * Returns the number of object entry versions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object entry versions
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid_C;

		Object[] finderArgs = new Object[] {uuid, companyId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_OBJECTENTRYVERSION_WHERE);

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
		"objectEntryVersion.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(objectEntryVersion.uuid IS NULL OR objectEntryVersion.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"objectEntryVersion.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByObjectDefinitionId;
	private FinderPath _finderPathWithoutPaginationFindByObjectDefinitionId;
	private FinderPath _finderPathCountByObjectDefinitionId;

	/**
	 * Returns all the object entry versions where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object entry versions
	 */
	@Override
	public List<ObjectEntryVersion> findByObjectDefinitionId(
		long objectDefinitionId) {

		return findByObjectDefinitionId(
			objectDefinitionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object entry versions where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @return the range of matching object entry versions
	 */
	@Override
	public List<ObjectEntryVersion> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end) {

		return findByObjectDefinitionId(objectDefinitionId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object entry versions where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entry versions
	 */
	@Override
	public List<ObjectEntryVersion> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		return findByObjectDefinitionId(
			objectDefinitionId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object entry versions where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entry versions
	 */
	@Override
	public List<ObjectEntryVersion> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectEntryVersion> orderByComparator,
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

		List<ObjectEntryVersion> list = null;

		if (useFinderCache) {
			list = (List<ObjectEntryVersion>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectEntryVersion objectEntryVersion : list) {
					if (objectDefinitionId !=
							objectEntryVersion.getObjectDefinitionId()) {

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

			sb.append(_SQL_SELECT_OBJECTENTRYVERSION_WHERE);

			sb.append(_FINDER_COLUMN_OBJECTDEFINITIONID_OBJECTDEFINITIONID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectEntryVersionModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId);

				list = (List<ObjectEntryVersion>)QueryUtil.list(
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
	 * Returns the first object entry version in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion findByObjectDefinitionId_First(
			long objectDefinitionId,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws NoSuchObjectEntryVersionException {

		ObjectEntryVersion objectEntryVersion = fetchByObjectDefinitionId_First(
			objectDefinitionId, orderByComparator);

		if (objectEntryVersion != null) {
			return objectEntryVersion;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append("}");

		throw new NoSuchObjectEntryVersionException(sb.toString());
	}

	/**
	 * Returns the first object entry version in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion fetchByObjectDefinitionId_First(
		long objectDefinitionId,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		List<ObjectEntryVersion> list = findByObjectDefinitionId(
			objectDefinitionId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object entry version in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion findByObjectDefinitionId_Last(
			long objectDefinitionId,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws NoSuchObjectEntryVersionException {

		ObjectEntryVersion objectEntryVersion = fetchByObjectDefinitionId_Last(
			objectDefinitionId, orderByComparator);

		if (objectEntryVersion != null) {
			return objectEntryVersion;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append("}");

		throw new NoSuchObjectEntryVersionException(sb.toString());
	}

	/**
	 * Returns the last object entry version in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion fetchByObjectDefinitionId_Last(
		long objectDefinitionId,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		int count = countByObjectDefinitionId(objectDefinitionId);

		if (count == 0) {
			return null;
		}

		List<ObjectEntryVersion> list = findByObjectDefinitionId(
			objectDefinitionId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object entry versions before and after the current object entry version in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectEntryVersionId the primary key of the current object entry version
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry version
	 * @throws NoSuchObjectEntryVersionException if a object entry version with the primary key could not be found
	 */
	@Override
	public ObjectEntryVersion[] findByObjectDefinitionId_PrevAndNext(
			long objectEntryVersionId, long objectDefinitionId,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws NoSuchObjectEntryVersionException {

		ObjectEntryVersion objectEntryVersion = findByPrimaryKey(
			objectEntryVersionId);

		Session session = null;

		try {
			session = openSession();

			ObjectEntryVersion[] array = new ObjectEntryVersionImpl[3];

			array[0] = getByObjectDefinitionId_PrevAndNext(
				session, objectEntryVersion, objectDefinitionId,
				orderByComparator, true);

			array[1] = objectEntryVersion;

			array[2] = getByObjectDefinitionId_PrevAndNext(
				session, objectEntryVersion, objectDefinitionId,
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

	protected ObjectEntryVersion getByObjectDefinitionId_PrevAndNext(
		Session session, ObjectEntryVersion objectEntryVersion,
		long objectDefinitionId,
		OrderByComparator<ObjectEntryVersion> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTENTRYVERSION_WHERE);

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
			sb.append(ObjectEntryVersionModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(objectDefinitionId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						objectEntryVersion)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectEntryVersion> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object entry versions where objectDefinitionId = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 */
	@Override
	public void removeByObjectDefinitionId(long objectDefinitionId) {
		for (ObjectEntryVersion objectEntryVersion :
				findByObjectDefinitionId(
					objectDefinitionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(objectEntryVersion);
		}
	}

	/**
	 * Returns the number of object entry versions where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object entry versions
	 */
	@Override
	public int countByObjectDefinitionId(long objectDefinitionId) {
		FinderPath finderPath = _finderPathCountByObjectDefinitionId;

		Object[] finderArgs = new Object[] {objectDefinitionId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_OBJECTENTRYVERSION_WHERE);

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
			"objectEntryVersion.objectDefinitionId = ?";

	private FinderPath _finderPathWithPaginationFindByObjectEntryId;
	private FinderPath _finderPathWithoutPaginationFindByObjectEntryId;
	private FinderPath _finderPathCountByObjectEntryId;

	/**
	 * Returns all the object entry versions where objectEntryId = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @return the matching object entry versions
	 */
	@Override
	public List<ObjectEntryVersion> findByObjectEntryId(long objectEntryId) {
		return findByObjectEntryId(
			objectEntryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object entry versions where objectEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param objectEntryId the object entry ID
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @return the range of matching object entry versions
	 */
	@Override
	public List<ObjectEntryVersion> findByObjectEntryId(
		long objectEntryId, int start, int end) {

		return findByObjectEntryId(objectEntryId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object entry versions where objectEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param objectEntryId the object entry ID
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entry versions
	 */
	@Override
	public List<ObjectEntryVersion> findByObjectEntryId(
		long objectEntryId, int start, int end,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		return findByObjectEntryId(
			objectEntryId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object entry versions where objectEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param objectEntryId the object entry ID
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entry versions
	 */
	@Override
	public List<ObjectEntryVersion> findByObjectEntryId(
		long objectEntryId, int start, int end,
		OrderByComparator<ObjectEntryVersion> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByObjectEntryId;
				finderArgs = new Object[] {objectEntryId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByObjectEntryId;
			finderArgs = new Object[] {
				objectEntryId, start, end, orderByComparator
			};
		}

		List<ObjectEntryVersion> list = null;

		if (useFinderCache) {
			list = (List<ObjectEntryVersion>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectEntryVersion objectEntryVersion : list) {
					if (objectEntryId !=
							objectEntryVersion.getObjectEntryId()) {

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

			sb.append(_SQL_SELECT_OBJECTENTRYVERSION_WHERE);

			sb.append(_FINDER_COLUMN_OBJECTENTRYID_OBJECTENTRYID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectEntryVersionModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectEntryId);

				list = (List<ObjectEntryVersion>)QueryUtil.list(
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
	 * Returns the first object entry version in the ordered set where objectEntryId = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion findByObjectEntryId_First(
			long objectEntryId,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws NoSuchObjectEntryVersionException {

		ObjectEntryVersion objectEntryVersion = fetchByObjectEntryId_First(
			objectEntryId, orderByComparator);

		if (objectEntryVersion != null) {
			return objectEntryVersion;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectEntryId=");
		sb.append(objectEntryId);

		sb.append("}");

		throw new NoSuchObjectEntryVersionException(sb.toString());
	}

	/**
	 * Returns the first object entry version in the ordered set where objectEntryId = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion fetchByObjectEntryId_First(
		long objectEntryId,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		List<ObjectEntryVersion> list = findByObjectEntryId(
			objectEntryId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object entry version in the ordered set where objectEntryId = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion findByObjectEntryId_Last(
			long objectEntryId,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws NoSuchObjectEntryVersionException {

		ObjectEntryVersion objectEntryVersion = fetchByObjectEntryId_Last(
			objectEntryId, orderByComparator);

		if (objectEntryVersion != null) {
			return objectEntryVersion;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectEntryId=");
		sb.append(objectEntryId);

		sb.append("}");

		throw new NoSuchObjectEntryVersionException(sb.toString());
	}

	/**
	 * Returns the last object entry version in the ordered set where objectEntryId = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion fetchByObjectEntryId_Last(
		long objectEntryId,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		int count = countByObjectEntryId(objectEntryId);

		if (count == 0) {
			return null;
		}

		List<ObjectEntryVersion> list = findByObjectEntryId(
			objectEntryId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object entry versions before and after the current object entry version in the ordered set where objectEntryId = &#63;.
	 *
	 * @param objectEntryVersionId the primary key of the current object entry version
	 * @param objectEntryId the object entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry version
	 * @throws NoSuchObjectEntryVersionException if a object entry version with the primary key could not be found
	 */
	@Override
	public ObjectEntryVersion[] findByObjectEntryId_PrevAndNext(
			long objectEntryVersionId, long objectEntryId,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws NoSuchObjectEntryVersionException {

		ObjectEntryVersion objectEntryVersion = findByPrimaryKey(
			objectEntryVersionId);

		Session session = null;

		try {
			session = openSession();

			ObjectEntryVersion[] array = new ObjectEntryVersionImpl[3];

			array[0] = getByObjectEntryId_PrevAndNext(
				session, objectEntryVersion, objectEntryId, orderByComparator,
				true);

			array[1] = objectEntryVersion;

			array[2] = getByObjectEntryId_PrevAndNext(
				session, objectEntryVersion, objectEntryId, orderByComparator,
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

	protected ObjectEntryVersion getByObjectEntryId_PrevAndNext(
		Session session, ObjectEntryVersion objectEntryVersion,
		long objectEntryId,
		OrderByComparator<ObjectEntryVersion> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTENTRYVERSION_WHERE);

		sb.append(_FINDER_COLUMN_OBJECTENTRYID_OBJECTENTRYID_2);

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
			sb.append(ObjectEntryVersionModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(objectEntryId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						objectEntryVersion)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectEntryVersion> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object entry versions where objectEntryId = &#63; from the database.
	 *
	 * @param objectEntryId the object entry ID
	 */
	@Override
	public void removeByObjectEntryId(long objectEntryId) {
		for (ObjectEntryVersion objectEntryVersion :
				findByObjectEntryId(
					objectEntryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(objectEntryVersion);
		}
	}

	/**
	 * Returns the number of object entry versions where objectEntryId = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @return the number of matching object entry versions
	 */
	@Override
	public int countByObjectEntryId(long objectEntryId) {
		FinderPath finderPath = _finderPathCountByObjectEntryId;

		Object[] finderArgs = new Object[] {objectEntryId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_OBJECTENTRYVERSION_WHERE);

			sb.append(_FINDER_COLUMN_OBJECTENTRYID_OBJECTENTRYID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectEntryId);

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

	private static final String _FINDER_COLUMN_OBJECTENTRYID_OBJECTENTRYID_2 =
		"objectEntryVersion.objectEntryId = ?";

	private FinderPath _finderPathWithPaginationFindByC_CD;
	private FinderPath _finderPathWithoutPaginationFindByC_CD;
	private FinderPath _finderPathCountByC_CD;

	/**
	 * Returns all the object entry versions where companyId = &#63; and createDate = &#63;.
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @return the matching object entry versions
	 */
	@Override
	public List<ObjectEntryVersion> findByC_CD(
		long companyId, Date createDate) {

		return findByC_CD(
			companyId, createDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object entry versions where companyId = &#63; and createDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @return the range of matching object entry versions
	 */
	@Override
	public List<ObjectEntryVersion> findByC_CD(
		long companyId, Date createDate, int start, int end) {

		return findByC_CD(companyId, createDate, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object entry versions where companyId = &#63; and createDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entry versions
	 */
	@Override
	public List<ObjectEntryVersion> findByC_CD(
		long companyId, Date createDate, int start, int end,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		return findByC_CD(
			companyId, createDate, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object entry versions where companyId = &#63; and createDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entry versions
	 */
	@Override
	public List<ObjectEntryVersion> findByC_CD(
		long companyId, Date createDate, int start, int end,
		OrderByComparator<ObjectEntryVersion> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByC_CD;
				finderArgs = new Object[] {companyId, _getTime(createDate)};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByC_CD;
			finderArgs = new Object[] {
				companyId, _getTime(createDate), start, end, orderByComparator
			};
		}

		List<ObjectEntryVersion> list = null;

		if (useFinderCache) {
			list = (List<ObjectEntryVersion>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectEntryVersion objectEntryVersion : list) {
					if ((companyId != objectEntryVersion.getCompanyId()) ||
						!Objects.equals(
							createDate, objectEntryVersion.getCreateDate())) {

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

			sb.append(_SQL_SELECT_OBJECTENTRYVERSION_WHERE);

			sb.append(_FINDER_COLUMN_C_CD_COMPANYID_2);

			boolean bindCreateDate = false;

			if (createDate == null) {
				sb.append(_FINDER_COLUMN_C_CD_CREATEDATE_1);
			}
			else {
				bindCreateDate = true;

				sb.append(_FINDER_COLUMN_C_CD_CREATEDATE_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectEntryVersionModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				if (bindCreateDate) {
					queryPos.add(new Timestamp(createDate.getTime()));
				}

				list = (List<ObjectEntryVersion>)QueryUtil.list(
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
	 * Returns the first object entry version in the ordered set where companyId = &#63; and createDate = &#63;.
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion findByC_CD_First(
			long companyId, Date createDate,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws NoSuchObjectEntryVersionException {

		ObjectEntryVersion objectEntryVersion = fetchByC_CD_First(
			companyId, createDate, orderByComparator);

		if (objectEntryVersion != null) {
			return objectEntryVersion;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", createDate=");
		sb.append(createDate);

		sb.append("}");

		throw new NoSuchObjectEntryVersionException(sb.toString());
	}

	/**
	 * Returns the first object entry version in the ordered set where companyId = &#63; and createDate = &#63;.
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion fetchByC_CD_First(
		long companyId, Date createDate,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		List<ObjectEntryVersion> list = findByC_CD(
			companyId, createDate, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object entry version in the ordered set where companyId = &#63; and createDate = &#63;.
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion findByC_CD_Last(
			long companyId, Date createDate,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws NoSuchObjectEntryVersionException {

		ObjectEntryVersion objectEntryVersion = fetchByC_CD_Last(
			companyId, createDate, orderByComparator);

		if (objectEntryVersion != null) {
			return objectEntryVersion;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", createDate=");
		sb.append(createDate);

		sb.append("}");

		throw new NoSuchObjectEntryVersionException(sb.toString());
	}

	/**
	 * Returns the last object entry version in the ordered set where companyId = &#63; and createDate = &#63;.
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion fetchByC_CD_Last(
		long companyId, Date createDate,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		int count = countByC_CD(companyId, createDate);

		if (count == 0) {
			return null;
		}

		List<ObjectEntryVersion> list = findByC_CD(
			companyId, createDate, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object entry versions before and after the current object entry version in the ordered set where companyId = &#63; and createDate = &#63;.
	 *
	 * @param objectEntryVersionId the primary key of the current object entry version
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry version
	 * @throws NoSuchObjectEntryVersionException if a object entry version with the primary key could not be found
	 */
	@Override
	public ObjectEntryVersion[] findByC_CD_PrevAndNext(
			long objectEntryVersionId, long companyId, Date createDate,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws NoSuchObjectEntryVersionException {

		ObjectEntryVersion objectEntryVersion = findByPrimaryKey(
			objectEntryVersionId);

		Session session = null;

		try {
			session = openSession();

			ObjectEntryVersion[] array = new ObjectEntryVersionImpl[3];

			array[0] = getByC_CD_PrevAndNext(
				session, objectEntryVersion, companyId, createDate,
				orderByComparator, true);

			array[1] = objectEntryVersion;

			array[2] = getByC_CD_PrevAndNext(
				session, objectEntryVersion, companyId, createDate,
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

	protected ObjectEntryVersion getByC_CD_PrevAndNext(
		Session session, ObjectEntryVersion objectEntryVersion, long companyId,
		Date createDate,
		OrderByComparator<ObjectEntryVersion> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTENTRYVERSION_WHERE);

		sb.append(_FINDER_COLUMN_C_CD_COMPANYID_2);

		boolean bindCreateDate = false;

		if (createDate == null) {
			sb.append(_FINDER_COLUMN_C_CD_CREATEDATE_1);
		}
		else {
			bindCreateDate = true;

			sb.append(_FINDER_COLUMN_C_CD_CREATEDATE_2);
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
			sb.append(ObjectEntryVersionModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (bindCreateDate) {
			queryPos.add(new Timestamp(createDate.getTime()));
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						objectEntryVersion)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectEntryVersion> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object entry versions where companyId = &#63; and createDate = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 */
	@Override
	public void removeByC_CD(long companyId, Date createDate) {
		for (ObjectEntryVersion objectEntryVersion :
				findByC_CD(
					companyId, createDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(objectEntryVersion);
		}
	}

	/**
	 * Returns the number of object entry versions where companyId = &#63; and createDate = &#63;.
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @return the number of matching object entry versions
	 */
	@Override
	public int countByC_CD(long companyId, Date createDate) {
		FinderPath finderPath = _finderPathCountByC_CD;

		Object[] finderArgs = new Object[] {companyId, _getTime(createDate)};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_OBJECTENTRYVERSION_WHERE);

			sb.append(_FINDER_COLUMN_C_CD_COMPANYID_2);

			boolean bindCreateDate = false;

			if (createDate == null) {
				sb.append(_FINDER_COLUMN_C_CD_CREATEDATE_1);
			}
			else {
				bindCreateDate = true;

				sb.append(_FINDER_COLUMN_C_CD_CREATEDATE_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				if (bindCreateDate) {
					queryPos.add(new Timestamp(createDate.getTime()));
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

	private static final String _FINDER_COLUMN_C_CD_COMPANYID_2 =
		"objectEntryVersion.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_CD_CREATEDATE_1 =
		"objectEntryVersion.createDate IS NULL";

	private static final String _FINDER_COLUMN_C_CD_CREATEDATE_2 =
		"objectEntryVersion.createDate = ?";

	private FinderPath _finderPathFetchByOEI_V;

	/**
	 * Returns the object entry version where objectEntryId = &#63; and version = &#63; or throws a <code>NoSuchObjectEntryVersionException</code> if it could not be found.
	 *
	 * @param objectEntryId the object entry ID
	 * @param version the version
	 * @return the matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion findByOEI_V(long objectEntryId, int version)
		throws NoSuchObjectEntryVersionException {

		ObjectEntryVersion objectEntryVersion = fetchByOEI_V(
			objectEntryId, version);

		if (objectEntryVersion == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("objectEntryId=");
			sb.append(objectEntryId);

			sb.append(", version=");
			sb.append(version);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchObjectEntryVersionException(sb.toString());
		}

		return objectEntryVersion;
	}

	/**
	 * Returns the object entry version where objectEntryId = &#63; and version = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param objectEntryId the object entry ID
	 * @param version the version
	 * @return the matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion fetchByOEI_V(long objectEntryId, int version) {
		return fetchByOEI_V(objectEntryId, version, true);
	}

	/**
	 * Returns the object entry version where objectEntryId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param objectEntryId the object entry ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion fetchByOEI_V(
		long objectEntryId, int version, boolean useFinderCache) {

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {objectEntryId, version};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByOEI_V, finderArgs, this);
		}

		if (result instanceof ObjectEntryVersion) {
			ObjectEntryVersion objectEntryVersion = (ObjectEntryVersion)result;

			if ((objectEntryId != objectEntryVersion.getObjectEntryId()) ||
				(version != objectEntryVersion.getVersion())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_OBJECTENTRYVERSION_WHERE);

			sb.append(_FINDER_COLUMN_OEI_V_OBJECTENTRYID_2);

			sb.append(_FINDER_COLUMN_OEI_V_VERSION_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectEntryId);

				queryPos.add(version);

				List<ObjectEntryVersion> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByOEI_V, finderArgs, list);
					}
				}
				else {
					ObjectEntryVersion objectEntryVersion = list.get(0);

					result = objectEntryVersion;

					cacheResult(objectEntryVersion);
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
			return (ObjectEntryVersion)result;
		}
	}

	/**
	 * Removes the object entry version where objectEntryId = &#63; and version = &#63; from the database.
	 *
	 * @param objectEntryId the object entry ID
	 * @param version the version
	 * @return the object entry version that was removed
	 */
	@Override
	public ObjectEntryVersion removeByOEI_V(long objectEntryId, int version)
		throws NoSuchObjectEntryVersionException {

		ObjectEntryVersion objectEntryVersion = findByOEI_V(
			objectEntryId, version);

		return remove(objectEntryVersion);
	}

	/**
	 * Returns the number of object entry versions where objectEntryId = &#63; and version = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param version the version
	 * @return the number of matching object entry versions
	 */
	@Override
	public int countByOEI_V(long objectEntryId, int version) {
		ObjectEntryVersion objectEntryVersion = fetchByOEI_V(
			objectEntryId, version);

		if (objectEntryVersion == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_OEI_V_OBJECTENTRYID_2 =
		"objectEntryVersion.objectEntryId = ? AND ";

	private static final String _FINDER_COLUMN_OEI_V_VERSION_2 =
		"objectEntryVersion.version = ?";

	private FinderPath _finderPathWithPaginationFindByOEI_S;
	private FinderPath _finderPathWithoutPaginationFindByOEI_S;
	private FinderPath _finderPathCountByOEI_S;

	/**
	 * Returns all the object entry versions where objectEntryId = &#63; and status = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param status the status
	 * @return the matching object entry versions
	 */
	@Override
	public List<ObjectEntryVersion> findByOEI_S(
		long objectEntryId, int status) {

		return findByOEI_S(
			objectEntryId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object entry versions where objectEntryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param objectEntryId the object entry ID
	 * @param status the status
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @return the range of matching object entry versions
	 */
	@Override
	public List<ObjectEntryVersion> findByOEI_S(
		long objectEntryId, int status, int start, int end) {

		return findByOEI_S(objectEntryId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object entry versions where objectEntryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param objectEntryId the object entry ID
	 * @param status the status
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entry versions
	 */
	@Override
	public List<ObjectEntryVersion> findByOEI_S(
		long objectEntryId, int status, int start, int end,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		return findByOEI_S(
			objectEntryId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object entry versions where objectEntryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param objectEntryId the object entry ID
	 * @param status the status
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entry versions
	 */
	@Override
	public List<ObjectEntryVersion> findByOEI_S(
		long objectEntryId, int status, int start, int end,
		OrderByComparator<ObjectEntryVersion> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByOEI_S;
				finderArgs = new Object[] {objectEntryId, status};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByOEI_S;
			finderArgs = new Object[] {
				objectEntryId, status, start, end, orderByComparator
			};
		}

		List<ObjectEntryVersion> list = null;

		if (useFinderCache) {
			list = (List<ObjectEntryVersion>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectEntryVersion objectEntryVersion : list) {
					if ((objectEntryId !=
							objectEntryVersion.getObjectEntryId()) ||
						(status != objectEntryVersion.getStatus())) {

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

			sb.append(_SQL_SELECT_OBJECTENTRYVERSION_WHERE);

			sb.append(_FINDER_COLUMN_OEI_S_OBJECTENTRYID_2);

			sb.append(_FINDER_COLUMN_OEI_S_STATUS_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectEntryVersionModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectEntryId);

				queryPos.add(status);

				list = (List<ObjectEntryVersion>)QueryUtil.list(
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
	 * Returns the first object entry version in the ordered set where objectEntryId = &#63; and status = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion findByOEI_S_First(
			long objectEntryId, int status,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws NoSuchObjectEntryVersionException {

		ObjectEntryVersion objectEntryVersion = fetchByOEI_S_First(
			objectEntryId, status, orderByComparator);

		if (objectEntryVersion != null) {
			return objectEntryVersion;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectEntryId=");
		sb.append(objectEntryId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchObjectEntryVersionException(sb.toString());
	}

	/**
	 * Returns the first object entry version in the ordered set where objectEntryId = &#63; and status = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion fetchByOEI_S_First(
		long objectEntryId, int status,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		List<ObjectEntryVersion> list = findByOEI_S(
			objectEntryId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object entry version in the ordered set where objectEntryId = &#63; and status = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion findByOEI_S_Last(
			long objectEntryId, int status,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws NoSuchObjectEntryVersionException {

		ObjectEntryVersion objectEntryVersion = fetchByOEI_S_Last(
			objectEntryId, status, orderByComparator);

		if (objectEntryVersion != null) {
			return objectEntryVersion;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectEntryId=");
		sb.append(objectEntryId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchObjectEntryVersionException(sb.toString());
	}

	/**
	 * Returns the last object entry version in the ordered set where objectEntryId = &#63; and status = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	@Override
	public ObjectEntryVersion fetchByOEI_S_Last(
		long objectEntryId, int status,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		int count = countByOEI_S(objectEntryId, status);

		if (count == 0) {
			return null;
		}

		List<ObjectEntryVersion> list = findByOEI_S(
			objectEntryId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object entry versions before and after the current object entry version in the ordered set where objectEntryId = &#63; and status = &#63;.
	 *
	 * @param objectEntryVersionId the primary key of the current object entry version
	 * @param objectEntryId the object entry ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry version
	 * @throws NoSuchObjectEntryVersionException if a object entry version with the primary key could not be found
	 */
	@Override
	public ObjectEntryVersion[] findByOEI_S_PrevAndNext(
			long objectEntryVersionId, long objectEntryId, int status,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws NoSuchObjectEntryVersionException {

		ObjectEntryVersion objectEntryVersion = findByPrimaryKey(
			objectEntryVersionId);

		Session session = null;

		try {
			session = openSession();

			ObjectEntryVersion[] array = new ObjectEntryVersionImpl[3];

			array[0] = getByOEI_S_PrevAndNext(
				session, objectEntryVersion, objectEntryId, status,
				orderByComparator, true);

			array[1] = objectEntryVersion;

			array[2] = getByOEI_S_PrevAndNext(
				session, objectEntryVersion, objectEntryId, status,
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

	protected ObjectEntryVersion getByOEI_S_PrevAndNext(
		Session session, ObjectEntryVersion objectEntryVersion,
		long objectEntryId, int status,
		OrderByComparator<ObjectEntryVersion> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTENTRYVERSION_WHERE);

		sb.append(_FINDER_COLUMN_OEI_S_OBJECTENTRYID_2);

		sb.append(_FINDER_COLUMN_OEI_S_STATUS_2);

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
			sb.append(ObjectEntryVersionModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(objectEntryId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						objectEntryVersion)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectEntryVersion> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object entry versions where objectEntryId = &#63; and status = &#63; from the database.
	 *
	 * @param objectEntryId the object entry ID
	 * @param status the status
	 */
	@Override
	public void removeByOEI_S(long objectEntryId, int status) {
		for (ObjectEntryVersion objectEntryVersion :
				findByOEI_S(
					objectEntryId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(objectEntryVersion);
		}
	}

	/**
	 * Returns the number of object entry versions where objectEntryId = &#63; and status = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param status the status
	 * @return the number of matching object entry versions
	 */
	@Override
	public int countByOEI_S(long objectEntryId, int status) {
		FinderPath finderPath = _finderPathCountByOEI_S;

		Object[] finderArgs = new Object[] {objectEntryId, status};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_OBJECTENTRYVERSION_WHERE);

			sb.append(_FINDER_COLUMN_OEI_S_OBJECTENTRYID_2);

			sb.append(_FINDER_COLUMN_OEI_S_STATUS_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectEntryId);

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

	private static final String _FINDER_COLUMN_OEI_S_OBJECTENTRYID_2 =
		"objectEntryVersion.objectEntryId = ? AND ";

	private static final String _FINDER_COLUMN_OEI_S_STATUS_2 =
		"objectEntryVersion.status = ?";

	public ObjectEntryVersionPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ObjectEntryVersion.class);

		setModelImplClass(ObjectEntryVersionImpl.class);
		setModelPKClass(long.class);

		setTable(ObjectEntryVersionTable.INSTANCE);
	}

	/**
	 * Caches the object entry version in the entity cache if it is enabled.
	 *
	 * @param objectEntryVersion the object entry version
	 */
	@Override
	public void cacheResult(ObjectEntryVersion objectEntryVersion) {
		entityCache.putResult(
			ObjectEntryVersionImpl.class, objectEntryVersion.getPrimaryKey(),
			objectEntryVersion);

		finderCache.putResult(
			_finderPathFetchByOEI_V,
			new Object[] {
				objectEntryVersion.getObjectEntryId(),
				objectEntryVersion.getVersion()
			},
			objectEntryVersion);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the object entry versions in the entity cache if it is enabled.
	 *
	 * @param objectEntryVersions the object entry versions
	 */
	@Override
	public void cacheResult(List<ObjectEntryVersion> objectEntryVersions) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (objectEntryVersions.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (ObjectEntryVersion objectEntryVersion : objectEntryVersions) {
			if (entityCache.getResult(
					ObjectEntryVersionImpl.class,
					objectEntryVersion.getPrimaryKey()) == null) {

				cacheResult(objectEntryVersion);
			}
		}
	}

	/**
	 * Clears the cache for all object entry versions.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(ObjectEntryVersionImpl.class);

		finderCache.clearCache(ObjectEntryVersionImpl.class);
	}

	/**
	 * Clears the cache for the object entry version.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(ObjectEntryVersion objectEntryVersion) {
		entityCache.removeResult(
			ObjectEntryVersionImpl.class, objectEntryVersion);
	}

	@Override
	public void clearCache(List<ObjectEntryVersion> objectEntryVersions) {
		for (ObjectEntryVersion objectEntryVersion : objectEntryVersions) {
			entityCache.removeResult(
				ObjectEntryVersionImpl.class, objectEntryVersion);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(ObjectEntryVersionImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(ObjectEntryVersionImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		ObjectEntryVersionModelImpl objectEntryVersionModelImpl) {

		Object[] args = new Object[] {
			objectEntryVersionModelImpl.getObjectEntryId(),
			objectEntryVersionModelImpl.getVersion()
		};

		finderCache.putResult(
			_finderPathFetchByOEI_V, args, objectEntryVersionModelImpl);
	}

	/**
	 * Creates a new object entry version with the primary key. Does not add the object entry version to the database.
	 *
	 * @param objectEntryVersionId the primary key for the new object entry version
	 * @return the new object entry version
	 */
	@Override
	public ObjectEntryVersion create(long objectEntryVersionId) {
		ObjectEntryVersion objectEntryVersion = new ObjectEntryVersionImpl();

		objectEntryVersion.setNew(true);
		objectEntryVersion.setPrimaryKey(objectEntryVersionId);

		String uuid = PortalUUIDUtil.generate();

		objectEntryVersion.setUuid(uuid);

		objectEntryVersion.setCompanyId(CompanyThreadLocal.getCompanyId());

		return objectEntryVersion;
	}

	/**
	 * Removes the object entry version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectEntryVersionId the primary key of the object entry version
	 * @return the object entry version that was removed
	 * @throws NoSuchObjectEntryVersionException if a object entry version with the primary key could not be found
	 */
	@Override
	public ObjectEntryVersion remove(long objectEntryVersionId)
		throws NoSuchObjectEntryVersionException {

		return remove((Serializable)objectEntryVersionId);
	}

	/**
	 * Removes the object entry version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the object entry version
	 * @return the object entry version that was removed
	 * @throws NoSuchObjectEntryVersionException if a object entry version with the primary key could not be found
	 */
	@Override
	public ObjectEntryVersion remove(Serializable primaryKey)
		throws NoSuchObjectEntryVersionException {

		Session session = null;

		try {
			session = openSession();

			ObjectEntryVersion objectEntryVersion =
				(ObjectEntryVersion)session.get(
					ObjectEntryVersionImpl.class, primaryKey);

			if (objectEntryVersion == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchObjectEntryVersionException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(objectEntryVersion);
		}
		catch (NoSuchObjectEntryVersionException noSuchEntityException) {
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
	protected ObjectEntryVersion removeImpl(
		ObjectEntryVersion objectEntryVersion) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(objectEntryVersion)) {
				objectEntryVersion = (ObjectEntryVersion)session.get(
					ObjectEntryVersionImpl.class,
					objectEntryVersion.getPrimaryKeyObj());
			}

			if (objectEntryVersion != null) {
				session.delete(objectEntryVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (objectEntryVersion != null) {
			clearCache(objectEntryVersion);
		}

		return objectEntryVersion;
	}

	@Override
	public ObjectEntryVersion updateImpl(
		ObjectEntryVersion objectEntryVersion) {

		boolean isNew = objectEntryVersion.isNew();

		if (!(objectEntryVersion instanceof ObjectEntryVersionModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(objectEntryVersion.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					objectEntryVersion);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in objectEntryVersion proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ObjectEntryVersion implementation " +
					objectEntryVersion.getClass());
		}

		ObjectEntryVersionModelImpl objectEntryVersionModelImpl =
			(ObjectEntryVersionModelImpl)objectEntryVersion;

		if (Validator.isNull(objectEntryVersion.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			objectEntryVersion.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (objectEntryVersion.getCreateDate() == null)) {
			if (serviceContext == null) {
				objectEntryVersion.setCreateDate(date);
			}
			else {
				objectEntryVersion.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!objectEntryVersionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				objectEntryVersion.setModifiedDate(date);
			}
			else {
				objectEntryVersion.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(objectEntryVersion);
			}
			else {
				objectEntryVersion = (ObjectEntryVersion)session.merge(
					objectEntryVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			ObjectEntryVersionImpl.class, objectEntryVersionModelImpl, false,
			true);

		cacheUniqueFindersCache(objectEntryVersionModelImpl);

		if (isNew) {
			objectEntryVersion.setNew(false);
		}

		objectEntryVersion.resetOriginalValues();

		return objectEntryVersion;
	}

	/**
	 * Returns the object entry version with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the object entry version
	 * @return the object entry version
	 * @throws NoSuchObjectEntryVersionException if a object entry version with the primary key could not be found
	 */
	@Override
	public ObjectEntryVersion findByPrimaryKey(Serializable primaryKey)
		throws NoSuchObjectEntryVersionException {

		ObjectEntryVersion objectEntryVersion = fetchByPrimaryKey(primaryKey);

		if (objectEntryVersion == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchObjectEntryVersionException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return objectEntryVersion;
	}

	/**
	 * Returns the object entry version with the primary key or throws a <code>NoSuchObjectEntryVersionException</code> if it could not be found.
	 *
	 * @param objectEntryVersionId the primary key of the object entry version
	 * @return the object entry version
	 * @throws NoSuchObjectEntryVersionException if a object entry version with the primary key could not be found
	 */
	@Override
	public ObjectEntryVersion findByPrimaryKey(long objectEntryVersionId)
		throws NoSuchObjectEntryVersionException {

		return findByPrimaryKey((Serializable)objectEntryVersionId);
	}

	/**
	 * Returns the object entry version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectEntryVersionId the primary key of the object entry version
	 * @return the object entry version, or <code>null</code> if a object entry version with the primary key could not be found
	 */
	@Override
	public ObjectEntryVersion fetchByPrimaryKey(long objectEntryVersionId) {
		return fetchByPrimaryKey((Serializable)objectEntryVersionId);
	}

	/**
	 * Returns all the object entry versions.
	 *
	 * @return the object entry versions
	 */
	@Override
	public List<ObjectEntryVersion> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object entry versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @return the range of object entry versions
	 */
	@Override
	public List<ObjectEntryVersion> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the object entry versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of object entry versions
	 */
	@Override
	public List<ObjectEntryVersion> findAll(
		int start, int end,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object entry versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of object entry versions
	 */
	@Override
	public List<ObjectEntryVersion> findAll(
		int start, int end,
		OrderByComparator<ObjectEntryVersion> orderByComparator,
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

		List<ObjectEntryVersion> list = null;

		if (useFinderCache) {
			list = (List<ObjectEntryVersion>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_OBJECTENTRYVERSION);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_OBJECTENTRYVERSION;

				sql = sql.concat(ObjectEntryVersionModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<ObjectEntryVersion>)QueryUtil.list(
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
	 * Removes all the object entry versions from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (ObjectEntryVersion objectEntryVersion : findAll()) {
			remove(objectEntryVersion);
		}
	}

	/**
	 * Returns the number of object entry versions.
	 *
	 * @return the number of object entry versions
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
					_SQL_COUNT_OBJECTENTRYVERSION);

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
		return "objectEntryVersionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OBJECTENTRYVERSION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ObjectEntryVersionModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the object entry version persistence.
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

		_finderPathWithPaginationFindByObjectEntryId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByObjectEntryId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"objectEntryId"}, true);

		_finderPathWithoutPaginationFindByObjectEntryId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByObjectEntryId",
			new String[] {Long.class.getName()}, new String[] {"objectEntryId"},
			true);

		_finderPathCountByObjectEntryId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByObjectEntryId",
			new String[] {Long.class.getName()}, new String[] {"objectEntryId"},
			false);

		_finderPathWithPaginationFindByC_CD = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_CD",
			new String[] {
				Long.class.getName(), Date.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "createDate"}, true);

		_finderPathWithoutPaginationFindByC_CD = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_CD",
			new String[] {Long.class.getName(), Date.class.getName()},
			new String[] {"companyId", "createDate"}, true);

		_finderPathCountByC_CD = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_CD",
			new String[] {Long.class.getName(), Date.class.getName()},
			new String[] {"companyId", "createDate"}, false);

		_finderPathFetchByOEI_V = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByOEI_V",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"objectEntryId", "version"}, true);

		_finderPathWithPaginationFindByOEI_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByOEI_S",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"objectEntryId", "status"}, true);

		_finderPathWithoutPaginationFindByOEI_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByOEI_S",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"objectEntryId", "status"}, true);

		_finderPathCountByOEI_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByOEI_S",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"objectEntryId", "status"}, false);

		ObjectEntryVersionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ObjectEntryVersionUtil.setPersistence(null);

		entityCache.removeCache(ObjectEntryVersionImpl.class.getName());
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

	private static final String _SQL_SELECT_OBJECTENTRYVERSION =
		"SELECT objectEntryVersion FROM ObjectEntryVersion objectEntryVersion";

	private static final String _SQL_SELECT_OBJECTENTRYVERSION_WHERE =
		"SELECT objectEntryVersion FROM ObjectEntryVersion objectEntryVersion WHERE ";

	private static final String _SQL_COUNT_OBJECTENTRYVERSION =
		"SELECT COUNT(objectEntryVersion) FROM ObjectEntryVersion objectEntryVersion";

	private static final String _SQL_COUNT_OBJECTENTRYVERSION_WHERE =
		"SELECT COUNT(objectEntryVersion) FROM ObjectEntryVersion objectEntryVersion WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS = "objectEntryVersion.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No ObjectEntryVersion exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ObjectEntryVersion exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryVersionPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}