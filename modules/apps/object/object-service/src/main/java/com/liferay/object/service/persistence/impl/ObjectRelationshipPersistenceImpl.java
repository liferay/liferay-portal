/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.impl;

import com.liferay.object.exception.NoSuchObjectRelationshipException;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.model.ObjectRelationshipTable;
import com.liferay.object.model.impl.ObjectRelationshipImpl;
import com.liferay.object.model.impl.ObjectRelationshipModelImpl;
import com.liferay.object.service.persistence.ObjectRelationshipPersistence;
import com.liferay.object.service.persistence.ObjectRelationshipUtil;
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
 * The persistence implementation for the object relationship service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = ObjectRelationshipPersistence.class)
public class ObjectRelationshipPersistenceImpl
	extends BasePersistenceImpl<ObjectRelationship>
	implements ObjectRelationshipPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ObjectRelationshipUtil</code> to access the object relationship persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ObjectRelationshipImpl.class.getName();

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
	 * Returns all the object relationships where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object relationships where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object relationships where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object relationships where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
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

		List<ObjectRelationship> list = null;

		if (useFinderCache) {
			list = (List<ObjectRelationship>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectRelationship objectRelationship : list) {
					if (!uuid.equals(objectRelationship.getUuid())) {
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

			sb.append(_SQL_SELECT_OBJECTRELATIONSHIP_WHERE);

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
				sb.append(ObjectRelationshipModelImpl.ORDER_BY_JPQL);
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

				list = (List<ObjectRelationship>)QueryUtil.list(
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
	 * Returns the first object relationship in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByUuid_First(
			String uuid,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = fetchByUuid_First(
			uuid, orderByComparator);

		if (objectRelationship != null) {
			return objectRelationship;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchObjectRelationshipException(sb.toString());
	}

	/**
	 * Returns the first object relationship in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByUuid_First(
		String uuid, OrderByComparator<ObjectRelationship> orderByComparator) {

		List<ObjectRelationship> list = findByUuid(
			uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object relationship in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByUuid_Last(
			String uuid,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = fetchByUuid_Last(
			uuid, orderByComparator);

		if (objectRelationship != null) {
			return objectRelationship;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchObjectRelationshipException(sb.toString());
	}

	/**
	 * Returns the last object relationship in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByUuid_Last(
		String uuid, OrderByComparator<ObjectRelationship> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<ObjectRelationship> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where uuid = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	@Override
	public ObjectRelationship[] findByUuid_PrevAndNext(
			long objectRelationshipId, String uuid,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		uuid = Objects.toString(uuid, "");

		ObjectRelationship objectRelationship = findByPrimaryKey(
			objectRelationshipId);

		Session session = null;

		try {
			session = openSession();

			ObjectRelationship[] array = new ObjectRelationshipImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, objectRelationship, uuid, orderByComparator, true);

			array[1] = objectRelationship;

			array[2] = getByUuid_PrevAndNext(
				session, objectRelationship, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected ObjectRelationship getByUuid_PrevAndNext(
		Session session, ObjectRelationship objectRelationship, String uuid,
		OrderByComparator<ObjectRelationship> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTRELATIONSHIP_WHERE);

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
			sb.append(ObjectRelationshipModelImpl.ORDER_BY_JPQL);
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
						objectRelationship)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectRelationship> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object relationships where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (ObjectRelationship objectRelationship :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(objectRelationship);
		}
	}

	/**
	 * Returns the number of object relationships where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByUuid(String uuid) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid;

		Object[] finderArgs = new Object[] {uuid};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_OBJECTRELATIONSHIP_WHERE);

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
		"objectRelationship.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(objectRelationship.uuid IS NULL OR objectRelationship.uuid = '')";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the object relationships where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object relationships where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object relationships where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object relationships where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
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

		List<ObjectRelationship> list = null;

		if (useFinderCache) {
			list = (List<ObjectRelationship>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectRelationship objectRelationship : list) {
					if (!uuid.equals(objectRelationship.getUuid()) ||
						(companyId != objectRelationship.getCompanyId())) {

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

			sb.append(_SQL_SELECT_OBJECTRELATIONSHIP_WHERE);

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
				sb.append(ObjectRelationshipModelImpl.ORDER_BY_JPQL);
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

				list = (List<ObjectRelationship>)QueryUtil.list(
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
	 * Returns the first object relationship in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (objectRelationship != null) {
			return objectRelationship;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchObjectRelationshipException(sb.toString());
	}

	/**
	 * Returns the first object relationship in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		List<ObjectRelationship> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object relationship in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (objectRelationship != null) {
			return objectRelationship;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchObjectRelationshipException(sb.toString());
	}

	/**
	 * Returns the last object relationship in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<ObjectRelationship> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	@Override
	public ObjectRelationship[] findByUuid_C_PrevAndNext(
			long objectRelationshipId, String uuid, long companyId,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		uuid = Objects.toString(uuid, "");

		ObjectRelationship objectRelationship = findByPrimaryKey(
			objectRelationshipId);

		Session session = null;

		try {
			session = openSession();

			ObjectRelationship[] array = new ObjectRelationshipImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, objectRelationship, uuid, companyId, orderByComparator,
				true);

			array[1] = objectRelationship;

			array[2] = getByUuid_C_PrevAndNext(
				session, objectRelationship, uuid, companyId, orderByComparator,
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

	protected ObjectRelationship getByUuid_C_PrevAndNext(
		Session session, ObjectRelationship objectRelationship, String uuid,
		long companyId, OrderByComparator<ObjectRelationship> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTRELATIONSHIP_WHERE);

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
			sb.append(ObjectRelationshipModelImpl.ORDER_BY_JPQL);
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
						objectRelationship)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectRelationship> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object relationships where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (ObjectRelationship objectRelationship :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(objectRelationship);
		}
	}

	/**
	 * Returns the number of object relationships where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid_C;

		Object[] finderArgs = new Object[] {uuid, companyId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_OBJECTRELATIONSHIP_WHERE);

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
		"objectRelationship.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(objectRelationship.uuid IS NULL OR objectRelationship.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"objectRelationship.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;

	/**
	 * Returns all the object relationships where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object relationships where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object relationships where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object relationships where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

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

		List<ObjectRelationship> list = null;

		if (useFinderCache) {
			list = (List<ObjectRelationship>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectRelationship objectRelationship : list) {
					if (companyId != objectRelationship.getCompanyId()) {
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

			sb.append(_SQL_SELECT_OBJECTRELATIONSHIP_WHERE);

			sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectRelationshipModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				list = (List<ObjectRelationship>)QueryUtil.list(
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
	 * Returns the first object relationship in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByCompanyId_First(
			long companyId,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (objectRelationship != null) {
			return objectRelationship;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchObjectRelationshipException(sb.toString());
	}

	/**
	 * Returns the first object relationship in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByCompanyId_First(
		long companyId,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		List<ObjectRelationship> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object relationship in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByCompanyId_Last(
			long companyId,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = fetchByCompanyId_Last(
			companyId, orderByComparator);

		if (objectRelationship != null) {
			return objectRelationship;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchObjectRelationshipException(sb.toString());
	}

	/**
	 * Returns the last object relationship in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByCompanyId_Last(
		long companyId,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<ObjectRelationship> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where companyId = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	@Override
	public ObjectRelationship[] findByCompanyId_PrevAndNext(
			long objectRelationshipId, long companyId,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = findByPrimaryKey(
			objectRelationshipId);

		Session session = null;

		try {
			session = openSession();

			ObjectRelationship[] array = new ObjectRelationshipImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, objectRelationship, companyId, orderByComparator,
				true);

			array[1] = objectRelationship;

			array[2] = getByCompanyId_PrevAndNext(
				session, objectRelationship, companyId, orderByComparator,
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

	protected ObjectRelationship getByCompanyId_PrevAndNext(
		Session session, ObjectRelationship objectRelationship, long companyId,
		OrderByComparator<ObjectRelationship> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTRELATIONSHIP_WHERE);

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
			sb.append(ObjectRelationshipModelImpl.ORDER_BY_JPQL);
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
						objectRelationship)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectRelationship> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object relationships where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (ObjectRelationship objectRelationship :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(objectRelationship);
		}
	}

	/**
	 * Returns the number of object relationships where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByCompanyId(long companyId) {
		FinderPath finderPath = _finderPathCountByCompanyId;

		Object[] finderArgs = new Object[] {companyId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_OBJECTRELATIONSHIP_WHERE);

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

	private static final String _FINDER_COLUMN_COMPANYID_COMPANYID_2 =
		"objectRelationship.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByObjectDefinitionId1;
	private FinderPath _finderPathWithoutPaginationFindByObjectDefinitionId1;
	private FinderPath _finderPathCountByObjectDefinitionId1;

	/**
	 * Returns all the object relationships where objectDefinitionId1 = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @return the matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByObjectDefinitionId1(
		long objectDefinitionId1) {

		return findByObjectDefinitionId1(
			objectDefinitionId1, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object relationships where objectDefinitionId1 = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByObjectDefinitionId1(
		long objectDefinitionId1, int start, int end) {

		return findByObjectDefinitionId1(objectDefinitionId1, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByObjectDefinitionId1(
		long objectDefinitionId1, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return findByObjectDefinitionId1(
			objectDefinitionId1, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByObjectDefinitionId1(
		long objectDefinitionId1, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath =
					_finderPathWithoutPaginationFindByObjectDefinitionId1;
				finderArgs = new Object[] {objectDefinitionId1};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByObjectDefinitionId1;
			finderArgs = new Object[] {
				objectDefinitionId1, start, end, orderByComparator
			};
		}

		List<ObjectRelationship> list = null;

		if (useFinderCache) {
			list = (List<ObjectRelationship>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectRelationship objectRelationship : list) {
					if (objectDefinitionId1 !=
							objectRelationship.getObjectDefinitionId1()) {

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

			sb.append(_SQL_SELECT_OBJECTRELATIONSHIP_WHERE);

			sb.append(_FINDER_COLUMN_OBJECTDEFINITIONID1_OBJECTDEFINITIONID1_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectRelationshipModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId1);

				list = (List<ObjectRelationship>)QueryUtil.list(
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
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByObjectDefinitionId1_First(
			long objectDefinitionId1,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship =
			fetchByObjectDefinitionId1_First(
				objectDefinitionId1, orderByComparator);

		if (objectRelationship != null) {
			return objectRelationship;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId1=");
		sb.append(objectDefinitionId1);

		sb.append("}");

		throw new NoSuchObjectRelationshipException(sb.toString());
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByObjectDefinitionId1_First(
		long objectDefinitionId1,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		List<ObjectRelationship> list = findByObjectDefinitionId1(
			objectDefinitionId1, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByObjectDefinitionId1_Last(
			long objectDefinitionId1,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = fetchByObjectDefinitionId1_Last(
			objectDefinitionId1, orderByComparator);

		if (objectRelationship != null) {
			return objectRelationship;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId1=");
		sb.append(objectDefinitionId1);

		sb.append("}");

		throw new NoSuchObjectRelationshipException(sb.toString());
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByObjectDefinitionId1_Last(
		long objectDefinitionId1,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		int count = countByObjectDefinitionId1(objectDefinitionId1);

		if (count == 0) {
			return null;
		}

		List<ObjectRelationship> list = findByObjectDefinitionId1(
			objectDefinitionId1, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where objectDefinitionId1 = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param objectDefinitionId1 the object definition id1
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	@Override
	public ObjectRelationship[] findByObjectDefinitionId1_PrevAndNext(
			long objectRelationshipId, long objectDefinitionId1,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = findByPrimaryKey(
			objectRelationshipId);

		Session session = null;

		try {
			session = openSession();

			ObjectRelationship[] array = new ObjectRelationshipImpl[3];

			array[0] = getByObjectDefinitionId1_PrevAndNext(
				session, objectRelationship, objectDefinitionId1,
				orderByComparator, true);

			array[1] = objectRelationship;

			array[2] = getByObjectDefinitionId1_PrevAndNext(
				session, objectRelationship, objectDefinitionId1,
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

	protected ObjectRelationship getByObjectDefinitionId1_PrevAndNext(
		Session session, ObjectRelationship objectRelationship,
		long objectDefinitionId1,
		OrderByComparator<ObjectRelationship> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTRELATIONSHIP_WHERE);

		sb.append(_FINDER_COLUMN_OBJECTDEFINITIONID1_OBJECTDEFINITIONID1_2);

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
			sb.append(ObjectRelationshipModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(objectDefinitionId1);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						objectRelationship)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectRelationship> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object relationships where objectDefinitionId1 = &#63; from the database.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 */
	@Override
	public void removeByObjectDefinitionId1(long objectDefinitionId1) {
		for (ObjectRelationship objectRelationship :
				findByObjectDefinitionId1(
					objectDefinitionId1, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(objectRelationship);
		}
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId1 = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByObjectDefinitionId1(long objectDefinitionId1) {
		FinderPath finderPath = _finderPathCountByObjectDefinitionId1;

		Object[] finderArgs = new Object[] {objectDefinitionId1};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_OBJECTRELATIONSHIP_WHERE);

			sb.append(_FINDER_COLUMN_OBJECTDEFINITIONID1_OBJECTDEFINITIONID1_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId1);

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
		_FINDER_COLUMN_OBJECTDEFINITIONID1_OBJECTDEFINITIONID1_2 =
			"objectRelationship.objectDefinitionId1 = ?";

	private FinderPath _finderPathWithPaginationFindByObjectDefinitionId2;
	private FinderPath _finderPathWithoutPaginationFindByObjectDefinitionId2;
	private FinderPath _finderPathCountByObjectDefinitionId2;

	/**
	 * Returns all the object relationships where objectDefinitionId2 = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @return the matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByObjectDefinitionId2(
		long objectDefinitionId2) {

		return findByObjectDefinitionId2(
			objectDefinitionId2, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object relationships where objectDefinitionId2 = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByObjectDefinitionId2(
		long objectDefinitionId2, int start, int end) {

		return findByObjectDefinitionId2(objectDefinitionId2, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId2 = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByObjectDefinitionId2(
		long objectDefinitionId2, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return findByObjectDefinitionId2(
			objectDefinitionId2, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId2 = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByObjectDefinitionId2(
		long objectDefinitionId2, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath =
					_finderPathWithoutPaginationFindByObjectDefinitionId2;
				finderArgs = new Object[] {objectDefinitionId2};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByObjectDefinitionId2;
			finderArgs = new Object[] {
				objectDefinitionId2, start, end, orderByComparator
			};
		}

		List<ObjectRelationship> list = null;

		if (useFinderCache) {
			list = (List<ObjectRelationship>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectRelationship objectRelationship : list) {
					if (objectDefinitionId2 !=
							objectRelationship.getObjectDefinitionId2()) {

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

			sb.append(_SQL_SELECT_OBJECTRELATIONSHIP_WHERE);

			sb.append(_FINDER_COLUMN_OBJECTDEFINITIONID2_OBJECTDEFINITIONID2_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectRelationshipModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId2);

				list = (List<ObjectRelationship>)QueryUtil.list(
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
	 * Returns the first object relationship in the ordered set where objectDefinitionId2 = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByObjectDefinitionId2_First(
			long objectDefinitionId2,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship =
			fetchByObjectDefinitionId2_First(
				objectDefinitionId2, orderByComparator);

		if (objectRelationship != null) {
			return objectRelationship;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId2=");
		sb.append(objectDefinitionId2);

		sb.append("}");

		throw new NoSuchObjectRelationshipException(sb.toString());
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId2 = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByObjectDefinitionId2_First(
		long objectDefinitionId2,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		List<ObjectRelationship> list = findByObjectDefinitionId2(
			objectDefinitionId2, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId2 = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByObjectDefinitionId2_Last(
			long objectDefinitionId2,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = fetchByObjectDefinitionId2_Last(
			objectDefinitionId2, orderByComparator);

		if (objectRelationship != null) {
			return objectRelationship;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId2=");
		sb.append(objectDefinitionId2);

		sb.append("}");

		throw new NoSuchObjectRelationshipException(sb.toString());
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId2 = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByObjectDefinitionId2_Last(
		long objectDefinitionId2,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		int count = countByObjectDefinitionId2(objectDefinitionId2);

		if (count == 0) {
			return null;
		}

		List<ObjectRelationship> list = findByObjectDefinitionId2(
			objectDefinitionId2, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where objectDefinitionId2 = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param objectDefinitionId2 the object definition id2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	@Override
	public ObjectRelationship[] findByObjectDefinitionId2_PrevAndNext(
			long objectRelationshipId, long objectDefinitionId2,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = findByPrimaryKey(
			objectRelationshipId);

		Session session = null;

		try {
			session = openSession();

			ObjectRelationship[] array = new ObjectRelationshipImpl[3];

			array[0] = getByObjectDefinitionId2_PrevAndNext(
				session, objectRelationship, objectDefinitionId2,
				orderByComparator, true);

			array[1] = objectRelationship;

			array[2] = getByObjectDefinitionId2_PrevAndNext(
				session, objectRelationship, objectDefinitionId2,
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

	protected ObjectRelationship getByObjectDefinitionId2_PrevAndNext(
		Session session, ObjectRelationship objectRelationship,
		long objectDefinitionId2,
		OrderByComparator<ObjectRelationship> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTRELATIONSHIP_WHERE);

		sb.append(_FINDER_COLUMN_OBJECTDEFINITIONID2_OBJECTDEFINITIONID2_2);

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
			sb.append(ObjectRelationshipModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(objectDefinitionId2);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						objectRelationship)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectRelationship> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object relationships where objectDefinitionId2 = &#63; from the database.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 */
	@Override
	public void removeByObjectDefinitionId2(long objectDefinitionId2) {
		for (ObjectRelationship objectRelationship :
				findByObjectDefinitionId2(
					objectDefinitionId2, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(objectRelationship);
		}
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId2 = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByObjectDefinitionId2(long objectDefinitionId2) {
		FinderPath finderPath = _finderPathCountByObjectDefinitionId2;

		Object[] finderArgs = new Object[] {objectDefinitionId2};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_OBJECTRELATIONSHIP_WHERE);

			sb.append(_FINDER_COLUMN_OBJECTDEFINITIONID2_OBJECTDEFINITIONID2_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId2);

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
		_FINDER_COLUMN_OBJECTDEFINITIONID2_OBJECTDEFINITIONID2_2 =
			"objectRelationship.objectDefinitionId2 = ?";

	private FinderPath _finderPathFetchByObjectFieldId2;

	/**
	 * Returns the object relationship where objectFieldId2 = &#63; or throws a <code>NoSuchObjectRelationshipException</code> if it could not be found.
	 *
	 * @param objectFieldId2 the object field id2
	 * @return the matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByObjectFieldId2(long objectFieldId2)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = fetchByObjectFieldId2(
			objectFieldId2);

		if (objectRelationship == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("objectFieldId2=");
			sb.append(objectFieldId2);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchObjectRelationshipException(sb.toString());
		}

		return objectRelationship;
	}

	/**
	 * Returns the object relationship where objectFieldId2 = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param objectFieldId2 the object field id2
	 * @return the matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByObjectFieldId2(long objectFieldId2) {
		return fetchByObjectFieldId2(objectFieldId2, true);
	}

	/**
	 * Returns the object relationship where objectFieldId2 = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param objectFieldId2 the object field id2
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByObjectFieldId2(
		long objectFieldId2, boolean useFinderCache) {

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {objectFieldId2};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByObjectFieldId2, finderArgs, this);
		}

		if (result instanceof ObjectRelationship) {
			ObjectRelationship objectRelationship = (ObjectRelationship)result;

			if (objectFieldId2 != objectRelationship.getObjectFieldId2()) {
				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_SELECT_OBJECTRELATIONSHIP_WHERE);

			sb.append(_FINDER_COLUMN_OBJECTFIELDID2_OBJECTFIELDID2_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectFieldId2);

				List<ObjectRelationship> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByObjectFieldId2, finderArgs, list);
					}
				}
				else {
					if (list.size() > 1) {
						Collections.sort(list, Collections.reverseOrder());

						if (_log.isWarnEnabled()) {
							if (!useFinderCache) {
								finderArgs = new Object[] {objectFieldId2};
							}

							_log.warn(
								"ObjectRelationshipPersistenceImpl.fetchByObjectFieldId2(long, boolean) with parameters (" +
									StringUtil.merge(finderArgs) +
										") yields a result set with more than 1 result. This violates the logical unique restriction. There is no order guarantee on which result is returned by this finder.");
						}
					}

					ObjectRelationship objectRelationship = list.get(0);

					result = objectRelationship;

					cacheResult(objectRelationship);
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
			return (ObjectRelationship)result;
		}
	}

	/**
	 * Removes the object relationship where objectFieldId2 = &#63; from the database.
	 *
	 * @param objectFieldId2 the object field id2
	 * @return the object relationship that was removed
	 */
	@Override
	public ObjectRelationship removeByObjectFieldId2(long objectFieldId2)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = findByObjectFieldId2(
			objectFieldId2);

		return remove(objectRelationship);
	}

	/**
	 * Returns the number of object relationships where objectFieldId2 = &#63;.
	 *
	 * @param objectFieldId2 the object field id2
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByObjectFieldId2(long objectFieldId2) {
		ObjectRelationship objectRelationship = fetchByObjectFieldId2(
			objectFieldId2);

		if (objectRelationship == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_OBJECTFIELDID2_OBJECTFIELDID2_2 =
		"objectRelationship.objectFieldId2 = ?";

	private FinderPath _finderPathWithPaginationFindByParameterObjectFieldId;
	private FinderPath _finderPathWithoutPaginationFindByParameterObjectFieldId;
	private FinderPath _finderPathCountByParameterObjectFieldId;

	/**
	 * Returns all the object relationships where parameterObjectFieldId = &#63;.
	 *
	 * @param parameterObjectFieldId the parameter object field ID
	 * @return the matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByParameterObjectFieldId(
		long parameterObjectFieldId) {

		return findByParameterObjectFieldId(
			parameterObjectFieldId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object relationships where parameterObjectFieldId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param parameterObjectFieldId the parameter object field ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByParameterObjectFieldId(
		long parameterObjectFieldId, int start, int end) {

		return findByParameterObjectFieldId(
			parameterObjectFieldId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object relationships where parameterObjectFieldId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param parameterObjectFieldId the parameter object field ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByParameterObjectFieldId(
		long parameterObjectFieldId, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return findByParameterObjectFieldId(
			parameterObjectFieldId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object relationships where parameterObjectFieldId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param parameterObjectFieldId the parameter object field ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByParameterObjectFieldId(
		long parameterObjectFieldId, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath =
					_finderPathWithoutPaginationFindByParameterObjectFieldId;
				finderArgs = new Object[] {parameterObjectFieldId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByParameterObjectFieldId;
			finderArgs = new Object[] {
				parameterObjectFieldId, start, end, orderByComparator
			};
		}

		List<ObjectRelationship> list = null;

		if (useFinderCache) {
			list = (List<ObjectRelationship>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectRelationship objectRelationship : list) {
					if (parameterObjectFieldId !=
							objectRelationship.getParameterObjectFieldId()) {

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

			sb.append(_SQL_SELECT_OBJECTRELATIONSHIP_WHERE);

			sb.append(
				_FINDER_COLUMN_PARAMETEROBJECTFIELDID_PARAMETEROBJECTFIELDID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectRelationshipModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(parameterObjectFieldId);

				list = (List<ObjectRelationship>)QueryUtil.list(
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
	 * Returns the first object relationship in the ordered set where parameterObjectFieldId = &#63;.
	 *
	 * @param parameterObjectFieldId the parameter object field ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByParameterObjectFieldId_First(
			long parameterObjectFieldId,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship =
			fetchByParameterObjectFieldId_First(
				parameterObjectFieldId, orderByComparator);

		if (objectRelationship != null) {
			return objectRelationship;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("parameterObjectFieldId=");
		sb.append(parameterObjectFieldId);

		sb.append("}");

		throw new NoSuchObjectRelationshipException(sb.toString());
	}

	/**
	 * Returns the first object relationship in the ordered set where parameterObjectFieldId = &#63;.
	 *
	 * @param parameterObjectFieldId the parameter object field ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByParameterObjectFieldId_First(
		long parameterObjectFieldId,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		List<ObjectRelationship> list = findByParameterObjectFieldId(
			parameterObjectFieldId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object relationship in the ordered set where parameterObjectFieldId = &#63;.
	 *
	 * @param parameterObjectFieldId the parameter object field ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByParameterObjectFieldId_Last(
			long parameterObjectFieldId,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship =
			fetchByParameterObjectFieldId_Last(
				parameterObjectFieldId, orderByComparator);

		if (objectRelationship != null) {
			return objectRelationship;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("parameterObjectFieldId=");
		sb.append(parameterObjectFieldId);

		sb.append("}");

		throw new NoSuchObjectRelationshipException(sb.toString());
	}

	/**
	 * Returns the last object relationship in the ordered set where parameterObjectFieldId = &#63;.
	 *
	 * @param parameterObjectFieldId the parameter object field ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByParameterObjectFieldId_Last(
		long parameterObjectFieldId,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		int count = countByParameterObjectFieldId(parameterObjectFieldId);

		if (count == 0) {
			return null;
		}

		List<ObjectRelationship> list = findByParameterObjectFieldId(
			parameterObjectFieldId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where parameterObjectFieldId = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param parameterObjectFieldId the parameter object field ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	@Override
	public ObjectRelationship[] findByParameterObjectFieldId_PrevAndNext(
			long objectRelationshipId, long parameterObjectFieldId,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = findByPrimaryKey(
			objectRelationshipId);

		Session session = null;

		try {
			session = openSession();

			ObjectRelationship[] array = new ObjectRelationshipImpl[3];

			array[0] = getByParameterObjectFieldId_PrevAndNext(
				session, objectRelationship, parameterObjectFieldId,
				orderByComparator, true);

			array[1] = objectRelationship;

			array[2] = getByParameterObjectFieldId_PrevAndNext(
				session, objectRelationship, parameterObjectFieldId,
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

	protected ObjectRelationship getByParameterObjectFieldId_PrevAndNext(
		Session session, ObjectRelationship objectRelationship,
		long parameterObjectFieldId,
		OrderByComparator<ObjectRelationship> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTRELATIONSHIP_WHERE);

		sb.append(
			_FINDER_COLUMN_PARAMETEROBJECTFIELDID_PARAMETEROBJECTFIELDID_2);

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
			sb.append(ObjectRelationshipModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(parameterObjectFieldId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						objectRelationship)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectRelationship> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object relationships where parameterObjectFieldId = &#63; from the database.
	 *
	 * @param parameterObjectFieldId the parameter object field ID
	 */
	@Override
	public void removeByParameterObjectFieldId(long parameterObjectFieldId) {
		for (ObjectRelationship objectRelationship :
				findByParameterObjectFieldId(
					parameterObjectFieldId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(objectRelationship);
		}
	}

	/**
	 * Returns the number of object relationships where parameterObjectFieldId = &#63;.
	 *
	 * @param parameterObjectFieldId the parameter object field ID
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByParameterObjectFieldId(long parameterObjectFieldId) {
		FinderPath finderPath = _finderPathCountByParameterObjectFieldId;

		Object[] finderArgs = new Object[] {parameterObjectFieldId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_OBJECTRELATIONSHIP_WHERE);

			sb.append(
				_FINDER_COLUMN_PARAMETEROBJECTFIELDID_PARAMETEROBJECTFIELDID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(parameterObjectFieldId);

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
		_FINDER_COLUMN_PARAMETEROBJECTFIELDID_PARAMETEROBJECTFIELDID_2 =
			"objectRelationship.parameterObjectFieldId = ?";

	private FinderPath _finderPathWithPaginationFindByC_U;
	private FinderPath _finderPathWithoutPaginationFindByC_U;
	private FinderPath _finderPathCountByC_U;

	/**
	 * Returns all the object relationships where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByC_U(long companyId, long userId) {
		return findByC_U(
			companyId, userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object relationships where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByC_U(
		long companyId, long userId, int start, int end) {

		return findByC_U(companyId, userId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object relationships where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return findByC_U(
			companyId, userId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object relationships where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

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

		List<ObjectRelationship> list = null;

		if (useFinderCache) {
			list = (List<ObjectRelationship>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectRelationship objectRelationship : list) {
					if ((companyId != objectRelationship.getCompanyId()) ||
						(userId != objectRelationship.getUserId())) {

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

			sb.append(_SQL_SELECT_OBJECTRELATIONSHIP_WHERE);

			sb.append(_FINDER_COLUMN_C_U_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_U_USERID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectRelationshipModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(userId);

				list = (List<ObjectRelationship>)QueryUtil.list(
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
	 * Returns the first object relationship in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByC_U_First(
			long companyId, long userId,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = fetchByC_U_First(
			companyId, userId, orderByComparator);

		if (objectRelationship != null) {
			return objectRelationship;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append("}");

		throw new NoSuchObjectRelationshipException(sb.toString());
	}

	/**
	 * Returns the first object relationship in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByC_U_First(
		long companyId, long userId,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		List<ObjectRelationship> list = findByC_U(
			companyId, userId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object relationship in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByC_U_Last(
			long companyId, long userId,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = fetchByC_U_Last(
			companyId, userId, orderByComparator);

		if (objectRelationship != null) {
			return objectRelationship;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append("}");

		throw new NoSuchObjectRelationshipException(sb.toString());
	}

	/**
	 * Returns the last object relationship in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByC_U_Last(
		long companyId, long userId,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		int count = countByC_U(companyId, userId);

		if (count == 0) {
			return null;
		}

		List<ObjectRelationship> list = findByC_U(
			companyId, userId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	@Override
	public ObjectRelationship[] findByC_U_PrevAndNext(
			long objectRelationshipId, long companyId, long userId,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = findByPrimaryKey(
			objectRelationshipId);

		Session session = null;

		try {
			session = openSession();

			ObjectRelationship[] array = new ObjectRelationshipImpl[3];

			array[0] = getByC_U_PrevAndNext(
				session, objectRelationship, companyId, userId,
				orderByComparator, true);

			array[1] = objectRelationship;

			array[2] = getByC_U_PrevAndNext(
				session, objectRelationship, companyId, userId,
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

	protected ObjectRelationship getByC_U_PrevAndNext(
		Session session, ObjectRelationship objectRelationship, long companyId,
		long userId, OrderByComparator<ObjectRelationship> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTRELATIONSHIP_WHERE);

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
			sb.append(ObjectRelationshipModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(
						objectRelationship)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectRelationship> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object relationships where companyId = &#63; and userId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 */
	@Override
	public void removeByC_U(long companyId, long userId) {
		for (ObjectRelationship objectRelationship :
				findByC_U(
					companyId, userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(objectRelationship);
		}
	}

	/**
	 * Returns the number of object relationships where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByC_U(long companyId, long userId) {
		FinderPath finderPath = _finderPathCountByC_U;

		Object[] finderArgs = new Object[] {companyId, userId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_OBJECTRELATIONSHIP_WHERE);

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

	private static final String _FINDER_COLUMN_C_U_COMPANYID_2 =
		"objectRelationship.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_U_USERID_2 =
		"objectRelationship.userId = ?";

	private FinderPath _finderPathWithPaginationFindByODI1_E;
	private FinderPath _finderPathWithoutPaginationFindByODI1_E;
	private FinderPath _finderPathCountByODI1_E;

	/**
	 * Returns all the object relationships where objectDefinitionId1 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param edge the edge
	 * @return the matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI1_E(
		long objectDefinitionId1, boolean edge) {

		return findByODI1_E(
			objectDefinitionId1, edge, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the object relationships where objectDefinitionId1 = &#63; and edge = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param edge the edge
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI1_E(
		long objectDefinitionId1, boolean edge, int start, int end) {

		return findByODI1_E(objectDefinitionId1, edge, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and edge = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param edge the edge
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI1_E(
		long objectDefinitionId1, boolean edge, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return findByODI1_E(
			objectDefinitionId1, edge, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and edge = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param edge the edge
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI1_E(
		long objectDefinitionId1, boolean edge, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByODI1_E;
				finderArgs = new Object[] {objectDefinitionId1, edge};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByODI1_E;
			finderArgs = new Object[] {
				objectDefinitionId1, edge, start, end, orderByComparator
			};
		}

		List<ObjectRelationship> list = null;

		if (useFinderCache) {
			list = (List<ObjectRelationship>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectRelationship objectRelationship : list) {
					if ((objectDefinitionId1 !=
							objectRelationship.getObjectDefinitionId1()) ||
						(edge != objectRelationship.isEdge())) {

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

			sb.append(_SQL_SELECT_OBJECTRELATIONSHIP_WHERE);

			sb.append(_FINDER_COLUMN_ODI1_E_OBJECTDEFINITIONID1_2);

			sb.append(_FINDER_COLUMN_ODI1_E_EDGE_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectRelationshipModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId1);

				queryPos.add(edge);

				list = (List<ObjectRelationship>)QueryUtil.list(
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
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param edge the edge
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByODI1_E_First(
			long objectDefinitionId1, boolean edge,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = fetchByODI1_E_First(
			objectDefinitionId1, edge, orderByComparator);

		if (objectRelationship != null) {
			return objectRelationship;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId1=");
		sb.append(objectDefinitionId1);

		sb.append(", edge=");
		sb.append(edge);

		sb.append("}");

		throw new NoSuchObjectRelationshipException(sb.toString());
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param edge the edge
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByODI1_E_First(
		long objectDefinitionId1, boolean edge,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		List<ObjectRelationship> list = findByODI1_E(
			objectDefinitionId1, edge, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param edge the edge
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByODI1_E_Last(
			long objectDefinitionId1, boolean edge,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = fetchByODI1_E_Last(
			objectDefinitionId1, edge, orderByComparator);

		if (objectRelationship != null) {
			return objectRelationship;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId1=");
		sb.append(objectDefinitionId1);

		sb.append(", edge=");
		sb.append(edge);

		sb.append("}");

		throw new NoSuchObjectRelationshipException(sb.toString());
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param edge the edge
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByODI1_E_Last(
		long objectDefinitionId1, boolean edge,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		int count = countByODI1_E(objectDefinitionId1, edge);

		if (count == 0) {
			return null;
		}

		List<ObjectRelationship> list = findByODI1_E(
			objectDefinitionId1, edge, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where objectDefinitionId1 = &#63; and edge = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param objectDefinitionId1 the object definition id1
	 * @param edge the edge
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	@Override
	public ObjectRelationship[] findByODI1_E_PrevAndNext(
			long objectRelationshipId, long objectDefinitionId1, boolean edge,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = findByPrimaryKey(
			objectRelationshipId);

		Session session = null;

		try {
			session = openSession();

			ObjectRelationship[] array = new ObjectRelationshipImpl[3];

			array[0] = getByODI1_E_PrevAndNext(
				session, objectRelationship, objectDefinitionId1, edge,
				orderByComparator, true);

			array[1] = objectRelationship;

			array[2] = getByODI1_E_PrevAndNext(
				session, objectRelationship, objectDefinitionId1, edge,
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

	protected ObjectRelationship getByODI1_E_PrevAndNext(
		Session session, ObjectRelationship objectRelationship,
		long objectDefinitionId1, boolean edge,
		OrderByComparator<ObjectRelationship> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTRELATIONSHIP_WHERE);

		sb.append(_FINDER_COLUMN_ODI1_E_OBJECTDEFINITIONID1_2);

		sb.append(_FINDER_COLUMN_ODI1_E_EDGE_2);

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
			sb.append(ObjectRelationshipModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(objectDefinitionId1);

		queryPos.add(edge);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						objectRelationship)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectRelationship> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object relationships where objectDefinitionId1 = &#63; and edge = &#63; from the database.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param edge the edge
	 */
	@Override
	public void removeByODI1_E(long objectDefinitionId1, boolean edge) {
		for (ObjectRelationship objectRelationship :
				findByODI1_E(
					objectDefinitionId1, edge, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(objectRelationship);
		}
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId1 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param edge the edge
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByODI1_E(long objectDefinitionId1, boolean edge) {
		FinderPath finderPath = _finderPathCountByODI1_E;

		Object[] finderArgs = new Object[] {objectDefinitionId1, edge};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_OBJECTRELATIONSHIP_WHERE);

			sb.append(_FINDER_COLUMN_ODI1_E_OBJECTDEFINITIONID1_2);

			sb.append(_FINDER_COLUMN_ODI1_E_EDGE_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId1);

				queryPos.add(edge);

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

	private static final String _FINDER_COLUMN_ODI1_E_OBJECTDEFINITIONID1_2 =
		"objectRelationship.objectDefinitionId1 = ? AND ";

	private static final String _FINDER_COLUMN_ODI1_E_EDGE_2 =
		"objectRelationship.edge = ?";

	private FinderPath _finderPathWithPaginationFindByODI1_N;
	private FinderPath _finderPathWithoutPaginationFindByODI1_N;
	private FinderPath _finderPathCountByODI1_N;

	/**
	 * Returns all the object relationships where objectDefinitionId1 = &#63; and name = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param name the name
	 * @return the matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI1_N(
		long objectDefinitionId1, String name) {

		return findByODI1_N(
			objectDefinitionId1, name, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the object relationships where objectDefinitionId1 = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param name the name
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI1_N(
		long objectDefinitionId1, String name, int start, int end) {

		return findByODI1_N(objectDefinitionId1, name, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param name the name
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI1_N(
		long objectDefinitionId1, String name, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return findByODI1_N(
			objectDefinitionId1, name, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param name the name
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI1_N(
		long objectDefinitionId1, String name, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		name = Objects.toString(name, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByODI1_N;
				finderArgs = new Object[] {objectDefinitionId1, name};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByODI1_N;
			finderArgs = new Object[] {
				objectDefinitionId1, name, start, end, orderByComparator
			};
		}

		List<ObjectRelationship> list = null;

		if (useFinderCache) {
			list = (List<ObjectRelationship>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectRelationship objectRelationship : list) {
					if ((objectDefinitionId1 !=
							objectRelationship.getObjectDefinitionId1()) ||
						!name.equals(objectRelationship.getName())) {

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

			sb.append(_SQL_SELECT_OBJECTRELATIONSHIP_WHERE);

			sb.append(_FINDER_COLUMN_ODI1_N_OBJECTDEFINITIONID1_2);

			boolean bindName = false;

			if (name.isEmpty()) {
				sb.append(_FINDER_COLUMN_ODI1_N_NAME_3);
			}
			else {
				bindName = true;

				sb.append(_FINDER_COLUMN_ODI1_N_NAME_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectRelationshipModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId1);

				if (bindName) {
					queryPos.add(name);
				}

				list = (List<ObjectRelationship>)QueryUtil.list(
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
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and name = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByODI1_N_First(
			long objectDefinitionId1, String name,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = fetchByODI1_N_First(
			objectDefinitionId1, name, orderByComparator);

		if (objectRelationship != null) {
			return objectRelationship;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId1=");
		sb.append(objectDefinitionId1);

		sb.append(", name=");
		sb.append(name);

		sb.append("}");

		throw new NoSuchObjectRelationshipException(sb.toString());
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and name = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByODI1_N_First(
		long objectDefinitionId1, String name,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		List<ObjectRelationship> list = findByODI1_N(
			objectDefinitionId1, name, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and name = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByODI1_N_Last(
			long objectDefinitionId1, String name,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = fetchByODI1_N_Last(
			objectDefinitionId1, name, orderByComparator);

		if (objectRelationship != null) {
			return objectRelationship;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId1=");
		sb.append(objectDefinitionId1);

		sb.append(", name=");
		sb.append(name);

		sb.append("}");

		throw new NoSuchObjectRelationshipException(sb.toString());
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and name = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByODI1_N_Last(
		long objectDefinitionId1, String name,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		int count = countByODI1_N(objectDefinitionId1, name);

		if (count == 0) {
			return null;
		}

		List<ObjectRelationship> list = findByODI1_N(
			objectDefinitionId1, name, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where objectDefinitionId1 = &#63; and name = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param objectDefinitionId1 the object definition id1
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	@Override
	public ObjectRelationship[] findByODI1_N_PrevAndNext(
			long objectRelationshipId, long objectDefinitionId1, String name,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		name = Objects.toString(name, "");

		ObjectRelationship objectRelationship = findByPrimaryKey(
			objectRelationshipId);

		Session session = null;

		try {
			session = openSession();

			ObjectRelationship[] array = new ObjectRelationshipImpl[3];

			array[0] = getByODI1_N_PrevAndNext(
				session, objectRelationship, objectDefinitionId1, name,
				orderByComparator, true);

			array[1] = objectRelationship;

			array[2] = getByODI1_N_PrevAndNext(
				session, objectRelationship, objectDefinitionId1, name,
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

	protected ObjectRelationship getByODI1_N_PrevAndNext(
		Session session, ObjectRelationship objectRelationship,
		long objectDefinitionId1, String name,
		OrderByComparator<ObjectRelationship> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTRELATIONSHIP_WHERE);

		sb.append(_FINDER_COLUMN_ODI1_N_OBJECTDEFINITIONID1_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_ODI1_N_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_ODI1_N_NAME_2);
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
			sb.append(ObjectRelationshipModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(objectDefinitionId1);

		if (bindName) {
			queryPos.add(name);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						objectRelationship)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectRelationship> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object relationships where objectDefinitionId1 = &#63; and name = &#63; from the database.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param name the name
	 */
	@Override
	public void removeByODI1_N(long objectDefinitionId1, String name) {
		for (ObjectRelationship objectRelationship :
				findByODI1_N(
					objectDefinitionId1, name, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(objectRelationship);
		}
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId1 = &#63; and name = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param name the name
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByODI1_N(long objectDefinitionId1, String name) {
		name = Objects.toString(name, "");

		FinderPath finderPath = _finderPathCountByODI1_N;

		Object[] finderArgs = new Object[] {objectDefinitionId1, name};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_OBJECTRELATIONSHIP_WHERE);

			sb.append(_FINDER_COLUMN_ODI1_N_OBJECTDEFINITIONID1_2);

			boolean bindName = false;

			if (name.isEmpty()) {
				sb.append(_FINDER_COLUMN_ODI1_N_NAME_3);
			}
			else {
				bindName = true;

				sb.append(_FINDER_COLUMN_ODI1_N_NAME_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId1);

				if (bindName) {
					queryPos.add(name);
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

	private static final String _FINDER_COLUMN_ODI1_N_OBJECTDEFINITIONID1_2 =
		"objectRelationship.objectDefinitionId1 = ? AND ";

	private static final String _FINDER_COLUMN_ODI1_N_NAME_2 =
		"objectRelationship.name = ?";

	private static final String _FINDER_COLUMN_ODI1_N_NAME_3 =
		"(objectRelationship.name IS NULL OR objectRelationship.name = '')";

	private FinderPath _finderPathWithPaginationFindByODI1_R;
	private FinderPath _finderPathWithoutPaginationFindByODI1_R;
	private FinderPath _finderPathCountByODI1_R;

	/**
	 * Returns all the object relationships where objectDefinitionId1 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @return the matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI1_R(
		long objectDefinitionId1, boolean reverse) {

		return findByODI1_R(
			objectDefinitionId1, reverse, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the object relationships where objectDefinitionId1 = &#63; and reverse = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI1_R(
		long objectDefinitionId1, boolean reverse, int start, int end) {

		return findByODI1_R(objectDefinitionId1, reverse, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and reverse = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI1_R(
		long objectDefinitionId1, boolean reverse, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return findByODI1_R(
			objectDefinitionId1, reverse, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and reverse = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI1_R(
		long objectDefinitionId1, boolean reverse, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByODI1_R;
				finderArgs = new Object[] {objectDefinitionId1, reverse};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByODI1_R;
			finderArgs = new Object[] {
				objectDefinitionId1, reverse, start, end, orderByComparator
			};
		}

		List<ObjectRelationship> list = null;

		if (useFinderCache) {
			list = (List<ObjectRelationship>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectRelationship objectRelationship : list) {
					if ((objectDefinitionId1 !=
							objectRelationship.getObjectDefinitionId1()) ||
						(reverse != objectRelationship.isReverse())) {

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

			sb.append(_SQL_SELECT_OBJECTRELATIONSHIP_WHERE);

			sb.append(_FINDER_COLUMN_ODI1_R_OBJECTDEFINITIONID1_2);

			sb.append(_FINDER_COLUMN_ODI1_R_REVERSE_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectRelationshipModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId1);

				queryPos.add(reverse);

				list = (List<ObjectRelationship>)QueryUtil.list(
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
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByODI1_R_First(
			long objectDefinitionId1, boolean reverse,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = fetchByODI1_R_First(
			objectDefinitionId1, reverse, orderByComparator);

		if (objectRelationship != null) {
			return objectRelationship;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId1=");
		sb.append(objectDefinitionId1);

		sb.append(", reverse=");
		sb.append(reverse);

		sb.append("}");

		throw new NoSuchObjectRelationshipException(sb.toString());
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByODI1_R_First(
		long objectDefinitionId1, boolean reverse,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		List<ObjectRelationship> list = findByODI1_R(
			objectDefinitionId1, reverse, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByODI1_R_Last(
			long objectDefinitionId1, boolean reverse,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = fetchByODI1_R_Last(
			objectDefinitionId1, reverse, orderByComparator);

		if (objectRelationship != null) {
			return objectRelationship;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId1=");
		sb.append(objectDefinitionId1);

		sb.append(", reverse=");
		sb.append(reverse);

		sb.append("}");

		throw new NoSuchObjectRelationshipException(sb.toString());
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByODI1_R_Last(
		long objectDefinitionId1, boolean reverse,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		int count = countByODI1_R(objectDefinitionId1, reverse);

		if (count == 0) {
			return null;
		}

		List<ObjectRelationship> list = findByODI1_R(
			objectDefinitionId1, reverse, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where objectDefinitionId1 = &#63; and reverse = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	@Override
	public ObjectRelationship[] findByODI1_R_PrevAndNext(
			long objectRelationshipId, long objectDefinitionId1,
			boolean reverse,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = findByPrimaryKey(
			objectRelationshipId);

		Session session = null;

		try {
			session = openSession();

			ObjectRelationship[] array = new ObjectRelationshipImpl[3];

			array[0] = getByODI1_R_PrevAndNext(
				session, objectRelationship, objectDefinitionId1, reverse,
				orderByComparator, true);

			array[1] = objectRelationship;

			array[2] = getByODI1_R_PrevAndNext(
				session, objectRelationship, objectDefinitionId1, reverse,
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

	protected ObjectRelationship getByODI1_R_PrevAndNext(
		Session session, ObjectRelationship objectRelationship,
		long objectDefinitionId1, boolean reverse,
		OrderByComparator<ObjectRelationship> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTRELATIONSHIP_WHERE);

		sb.append(_FINDER_COLUMN_ODI1_R_OBJECTDEFINITIONID1_2);

		sb.append(_FINDER_COLUMN_ODI1_R_REVERSE_2);

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
			sb.append(ObjectRelationshipModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(objectDefinitionId1);

		queryPos.add(reverse);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						objectRelationship)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectRelationship> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object relationships where objectDefinitionId1 = &#63; and reverse = &#63; from the database.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 */
	@Override
	public void removeByODI1_R(long objectDefinitionId1, boolean reverse) {
		for (ObjectRelationship objectRelationship :
				findByODI1_R(
					objectDefinitionId1, reverse, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(objectRelationship);
		}
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId1 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByODI1_R(long objectDefinitionId1, boolean reverse) {
		FinderPath finderPath = _finderPathCountByODI1_R;

		Object[] finderArgs = new Object[] {objectDefinitionId1, reverse};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_OBJECTRELATIONSHIP_WHERE);

			sb.append(_FINDER_COLUMN_ODI1_R_OBJECTDEFINITIONID1_2);

			sb.append(_FINDER_COLUMN_ODI1_R_REVERSE_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId1);

				queryPos.add(reverse);

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

	private static final String _FINDER_COLUMN_ODI1_R_OBJECTDEFINITIONID1_2 =
		"objectRelationship.objectDefinitionId1 = ? AND ";

	private static final String _FINDER_COLUMN_ODI1_R_REVERSE_2 =
		"objectRelationship.reverse = ?";

	private FinderPath _finderPathWithPaginationFindByODI2_E;
	private FinderPath _finderPathWithoutPaginationFindByODI2_E;
	private FinderPath _finderPathCountByODI2_E;

	/**
	 * Returns all the object relationships where objectDefinitionId2 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param edge the edge
	 * @return the matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI2_E(
		long objectDefinitionId2, boolean edge) {

		return findByODI2_E(
			objectDefinitionId2, edge, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the object relationships where objectDefinitionId2 = &#63; and edge = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param edge the edge
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI2_E(
		long objectDefinitionId2, boolean edge, int start, int end) {

		return findByODI2_E(objectDefinitionId2, edge, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId2 = &#63; and edge = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param edge the edge
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI2_E(
		long objectDefinitionId2, boolean edge, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return findByODI2_E(
			objectDefinitionId2, edge, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId2 = &#63; and edge = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param edge the edge
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI2_E(
		long objectDefinitionId2, boolean edge, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByODI2_E;
				finderArgs = new Object[] {objectDefinitionId2, edge};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByODI2_E;
			finderArgs = new Object[] {
				objectDefinitionId2, edge, start, end, orderByComparator
			};
		}

		List<ObjectRelationship> list = null;

		if (useFinderCache) {
			list = (List<ObjectRelationship>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectRelationship objectRelationship : list) {
					if ((objectDefinitionId2 !=
							objectRelationship.getObjectDefinitionId2()) ||
						(edge != objectRelationship.isEdge())) {

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

			sb.append(_SQL_SELECT_OBJECTRELATIONSHIP_WHERE);

			sb.append(_FINDER_COLUMN_ODI2_E_OBJECTDEFINITIONID2_2);

			sb.append(_FINDER_COLUMN_ODI2_E_EDGE_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectRelationshipModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId2);

				queryPos.add(edge);

				list = (List<ObjectRelationship>)QueryUtil.list(
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
	 * Returns the first object relationship in the ordered set where objectDefinitionId2 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param edge the edge
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByODI2_E_First(
			long objectDefinitionId2, boolean edge,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = fetchByODI2_E_First(
			objectDefinitionId2, edge, orderByComparator);

		if (objectRelationship != null) {
			return objectRelationship;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId2=");
		sb.append(objectDefinitionId2);

		sb.append(", edge=");
		sb.append(edge);

		sb.append("}");

		throw new NoSuchObjectRelationshipException(sb.toString());
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId2 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param edge the edge
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByODI2_E_First(
		long objectDefinitionId2, boolean edge,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		List<ObjectRelationship> list = findByODI2_E(
			objectDefinitionId2, edge, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId2 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param edge the edge
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByODI2_E_Last(
			long objectDefinitionId2, boolean edge,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = fetchByODI2_E_Last(
			objectDefinitionId2, edge, orderByComparator);

		if (objectRelationship != null) {
			return objectRelationship;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId2=");
		sb.append(objectDefinitionId2);

		sb.append(", edge=");
		sb.append(edge);

		sb.append("}");

		throw new NoSuchObjectRelationshipException(sb.toString());
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId2 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param edge the edge
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByODI2_E_Last(
		long objectDefinitionId2, boolean edge,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		int count = countByODI2_E(objectDefinitionId2, edge);

		if (count == 0) {
			return null;
		}

		List<ObjectRelationship> list = findByODI2_E(
			objectDefinitionId2, edge, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where objectDefinitionId2 = &#63; and edge = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param objectDefinitionId2 the object definition id2
	 * @param edge the edge
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	@Override
	public ObjectRelationship[] findByODI2_E_PrevAndNext(
			long objectRelationshipId, long objectDefinitionId2, boolean edge,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = findByPrimaryKey(
			objectRelationshipId);

		Session session = null;

		try {
			session = openSession();

			ObjectRelationship[] array = new ObjectRelationshipImpl[3];

			array[0] = getByODI2_E_PrevAndNext(
				session, objectRelationship, objectDefinitionId2, edge,
				orderByComparator, true);

			array[1] = objectRelationship;

			array[2] = getByODI2_E_PrevAndNext(
				session, objectRelationship, objectDefinitionId2, edge,
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

	protected ObjectRelationship getByODI2_E_PrevAndNext(
		Session session, ObjectRelationship objectRelationship,
		long objectDefinitionId2, boolean edge,
		OrderByComparator<ObjectRelationship> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTRELATIONSHIP_WHERE);

		sb.append(_FINDER_COLUMN_ODI2_E_OBJECTDEFINITIONID2_2);

		sb.append(_FINDER_COLUMN_ODI2_E_EDGE_2);

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
			sb.append(ObjectRelationshipModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(objectDefinitionId2);

		queryPos.add(edge);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						objectRelationship)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectRelationship> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object relationships where objectDefinitionId2 = &#63; and edge = &#63; from the database.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param edge the edge
	 */
	@Override
	public void removeByODI2_E(long objectDefinitionId2, boolean edge) {
		for (ObjectRelationship objectRelationship :
				findByODI2_E(
					objectDefinitionId2, edge, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(objectRelationship);
		}
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId2 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param edge the edge
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByODI2_E(long objectDefinitionId2, boolean edge) {
		FinderPath finderPath = _finderPathCountByODI2_E;

		Object[] finderArgs = new Object[] {objectDefinitionId2, edge};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_OBJECTRELATIONSHIP_WHERE);

			sb.append(_FINDER_COLUMN_ODI2_E_OBJECTDEFINITIONID2_2);

			sb.append(_FINDER_COLUMN_ODI2_E_EDGE_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId2);

				queryPos.add(edge);

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

	private static final String _FINDER_COLUMN_ODI2_E_OBJECTDEFINITIONID2_2 =
		"objectRelationship.objectDefinitionId2 = ? AND ";

	private static final String _FINDER_COLUMN_ODI2_E_EDGE_2 =
		"objectRelationship.edge = ?";

	private FinderPath _finderPathWithPaginationFindByODI2_R;
	private FinderPath _finderPathWithoutPaginationFindByODI2_R;
	private FinderPath _finderPathCountByODI2_R;

	/**
	 * Returns all the object relationships where objectDefinitionId2 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @return the matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI2_R(
		long objectDefinitionId2, boolean reverse) {

		return findByODI2_R(
			objectDefinitionId2, reverse, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the object relationships where objectDefinitionId2 = &#63; and reverse = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI2_R(
		long objectDefinitionId2, boolean reverse, int start, int end) {

		return findByODI2_R(objectDefinitionId2, reverse, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId2 = &#63; and reverse = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI2_R(
		long objectDefinitionId2, boolean reverse, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return findByODI2_R(
			objectDefinitionId2, reverse, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId2 = &#63; and reverse = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI2_R(
		long objectDefinitionId2, boolean reverse, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByODI2_R;
				finderArgs = new Object[] {objectDefinitionId2, reverse};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByODI2_R;
			finderArgs = new Object[] {
				objectDefinitionId2, reverse, start, end, orderByComparator
			};
		}

		List<ObjectRelationship> list = null;

		if (useFinderCache) {
			list = (List<ObjectRelationship>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectRelationship objectRelationship : list) {
					if ((objectDefinitionId2 !=
							objectRelationship.getObjectDefinitionId2()) ||
						(reverse != objectRelationship.isReverse())) {

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

			sb.append(_SQL_SELECT_OBJECTRELATIONSHIP_WHERE);

			sb.append(_FINDER_COLUMN_ODI2_R_OBJECTDEFINITIONID2_2);

			sb.append(_FINDER_COLUMN_ODI2_R_REVERSE_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectRelationshipModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId2);

				queryPos.add(reverse);

				list = (List<ObjectRelationship>)QueryUtil.list(
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
	 * Returns the first object relationship in the ordered set where objectDefinitionId2 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByODI2_R_First(
			long objectDefinitionId2, boolean reverse,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = fetchByODI2_R_First(
			objectDefinitionId2, reverse, orderByComparator);

		if (objectRelationship != null) {
			return objectRelationship;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId2=");
		sb.append(objectDefinitionId2);

		sb.append(", reverse=");
		sb.append(reverse);

		sb.append("}");

		throw new NoSuchObjectRelationshipException(sb.toString());
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId2 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByODI2_R_First(
		long objectDefinitionId2, boolean reverse,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		List<ObjectRelationship> list = findByODI2_R(
			objectDefinitionId2, reverse, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId2 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByODI2_R_Last(
			long objectDefinitionId2, boolean reverse,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = fetchByODI2_R_Last(
			objectDefinitionId2, reverse, orderByComparator);

		if (objectRelationship != null) {
			return objectRelationship;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId2=");
		sb.append(objectDefinitionId2);

		sb.append(", reverse=");
		sb.append(reverse);

		sb.append("}");

		throw new NoSuchObjectRelationshipException(sb.toString());
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId2 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByODI2_R_Last(
		long objectDefinitionId2, boolean reverse,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		int count = countByODI2_R(objectDefinitionId2, reverse);

		if (count == 0) {
			return null;
		}

		List<ObjectRelationship> list = findByODI2_R(
			objectDefinitionId2, reverse, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where objectDefinitionId2 = &#63; and reverse = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	@Override
	public ObjectRelationship[] findByODI2_R_PrevAndNext(
			long objectRelationshipId, long objectDefinitionId2,
			boolean reverse,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = findByPrimaryKey(
			objectRelationshipId);

		Session session = null;

		try {
			session = openSession();

			ObjectRelationship[] array = new ObjectRelationshipImpl[3];

			array[0] = getByODI2_R_PrevAndNext(
				session, objectRelationship, objectDefinitionId2, reverse,
				orderByComparator, true);

			array[1] = objectRelationship;

			array[2] = getByODI2_R_PrevAndNext(
				session, objectRelationship, objectDefinitionId2, reverse,
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

	protected ObjectRelationship getByODI2_R_PrevAndNext(
		Session session, ObjectRelationship objectRelationship,
		long objectDefinitionId2, boolean reverse,
		OrderByComparator<ObjectRelationship> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTRELATIONSHIP_WHERE);

		sb.append(_FINDER_COLUMN_ODI2_R_OBJECTDEFINITIONID2_2);

		sb.append(_FINDER_COLUMN_ODI2_R_REVERSE_2);

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
			sb.append(ObjectRelationshipModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(objectDefinitionId2);

		queryPos.add(reverse);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						objectRelationship)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectRelationship> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object relationships where objectDefinitionId2 = &#63; and reverse = &#63; from the database.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 */
	@Override
	public void removeByODI2_R(long objectDefinitionId2, boolean reverse) {
		for (ObjectRelationship objectRelationship :
				findByODI2_R(
					objectDefinitionId2, reverse, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(objectRelationship);
		}
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId2 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByODI2_R(long objectDefinitionId2, boolean reverse) {
		FinderPath finderPath = _finderPathCountByODI2_R;

		Object[] finderArgs = new Object[] {objectDefinitionId2, reverse};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_OBJECTRELATIONSHIP_WHERE);

			sb.append(_FINDER_COLUMN_ODI2_R_OBJECTDEFINITIONID2_2);

			sb.append(_FINDER_COLUMN_ODI2_R_REVERSE_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId2);

				queryPos.add(reverse);

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

	private static final String _FINDER_COLUMN_ODI2_R_OBJECTDEFINITIONID2_2 =
		"objectRelationship.objectDefinitionId2 = ? AND ";

	private static final String _FINDER_COLUMN_ODI2_R_REVERSE_2 =
		"objectRelationship.reverse = ?";

	private FinderPath _finderPathFetchByDTN_R;

	/**
	 * Returns the object relationship where dbTableName = &#63; and reverse = &#63; or throws a <code>NoSuchObjectRelationshipException</code> if it could not be found.
	 *
	 * @param dbTableName the db table name
	 * @param reverse the reverse
	 * @return the matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByDTN_R(String dbTableName, boolean reverse)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = fetchByDTN_R(
			dbTableName, reverse);

		if (objectRelationship == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("dbTableName=");
			sb.append(dbTableName);

			sb.append(", reverse=");
			sb.append(reverse);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchObjectRelationshipException(sb.toString());
		}

		return objectRelationship;
	}

	/**
	 * Returns the object relationship where dbTableName = &#63; and reverse = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param dbTableName the db table name
	 * @param reverse the reverse
	 * @return the matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByDTN_R(
		String dbTableName, boolean reverse) {

		return fetchByDTN_R(dbTableName, reverse, true);
	}

	/**
	 * Returns the object relationship where dbTableName = &#63; and reverse = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param dbTableName the db table name
	 * @param reverse the reverse
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByDTN_R(
		String dbTableName, boolean reverse, boolean useFinderCache) {

		dbTableName = Objects.toString(dbTableName, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {dbTableName, reverse};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByDTN_R, finderArgs, this);
		}

		if (result instanceof ObjectRelationship) {
			ObjectRelationship objectRelationship = (ObjectRelationship)result;

			if (!Objects.equals(
					dbTableName, objectRelationship.getDBTableName()) ||
				(reverse != objectRelationship.isReverse())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_OBJECTRELATIONSHIP_WHERE);

			boolean bindDBTableName = false;

			if (dbTableName.isEmpty()) {
				sb.append(_FINDER_COLUMN_DTN_R_DBTABLENAME_3);
			}
			else {
				bindDBTableName = true;

				sb.append(_FINDER_COLUMN_DTN_R_DBTABLENAME_2);
			}

			sb.append(_FINDER_COLUMN_DTN_R_REVERSE_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindDBTableName) {
					queryPos.add(dbTableName);
				}

				queryPos.add(reverse);

				List<ObjectRelationship> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByDTN_R, finderArgs, list);
					}
				}
				else {
					if (list.size() > 1) {
						Collections.sort(list, Collections.reverseOrder());

						if (_log.isWarnEnabled()) {
							if (!useFinderCache) {
								finderArgs = new Object[] {
									dbTableName, reverse
								};
							}

							_log.warn(
								"ObjectRelationshipPersistenceImpl.fetchByDTN_R(String, boolean, boolean) with parameters (" +
									StringUtil.merge(finderArgs) +
										") yields a result set with more than 1 result. This violates the logical unique restriction. There is no order guarantee on which result is returned by this finder.");
						}
					}

					ObjectRelationship objectRelationship = list.get(0);

					result = objectRelationship;

					cacheResult(objectRelationship);
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
			return (ObjectRelationship)result;
		}
	}

	/**
	 * Removes the object relationship where dbTableName = &#63; and reverse = &#63; from the database.
	 *
	 * @param dbTableName the db table name
	 * @param reverse the reverse
	 * @return the object relationship that was removed
	 */
	@Override
	public ObjectRelationship removeByDTN_R(String dbTableName, boolean reverse)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = findByDTN_R(
			dbTableName, reverse);

		return remove(objectRelationship);
	}

	/**
	 * Returns the number of object relationships where dbTableName = &#63; and reverse = &#63;.
	 *
	 * @param dbTableName the db table name
	 * @param reverse the reverse
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByDTN_R(String dbTableName, boolean reverse) {
		ObjectRelationship objectRelationship = fetchByDTN_R(
			dbTableName, reverse);

		if (objectRelationship == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_DTN_R_DBTABLENAME_2 =
		"objectRelationship.dbTableName = ? AND ";

	private static final String _FINDER_COLUMN_DTN_R_DBTABLENAME_3 =
		"(objectRelationship.dbTableName IS NULL OR objectRelationship.dbTableName = '') AND ";

	private static final String _FINDER_COLUMN_DTN_R_REVERSE_2 =
		"objectRelationship.reverse = ?";

	private FinderPath _finderPathFetchByERC_C_ODI1;

	/**
	 * Returns the object relationship where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId1 = &#63; or throws a <code>NoSuchObjectRelationshipException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId1 the object definition id1
	 * @return the matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByERC_C_ODI1(
			String externalReferenceCode, long companyId,
			long objectDefinitionId1)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = fetchByERC_C_ODI1(
			externalReferenceCode, companyId, objectDefinitionId1);

		if (objectRelationship == null) {
			StringBundler sb = new StringBundler(8);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("externalReferenceCode=");
			sb.append(externalReferenceCode);

			sb.append(", companyId=");
			sb.append(companyId);

			sb.append(", objectDefinitionId1=");
			sb.append(objectDefinitionId1);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchObjectRelationshipException(sb.toString());
		}

		return objectRelationship;
	}

	/**
	 * Returns the object relationship where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId1 = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId1 the object definition id1
	 * @return the matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByERC_C_ODI1(
		String externalReferenceCode, long companyId,
		long objectDefinitionId1) {

		return fetchByERC_C_ODI1(
			externalReferenceCode, companyId, objectDefinitionId1, true);
	}

	/**
	 * Returns the object relationship where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId1 = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId1 the object definition id1
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByERC_C_ODI1(
		String externalReferenceCode, long companyId, long objectDefinitionId1,
		boolean useFinderCache) {

		externalReferenceCode = Objects.toString(externalReferenceCode, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {
				externalReferenceCode, companyId, objectDefinitionId1
			};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByERC_C_ODI1, finderArgs, this);
		}

		if (result instanceof ObjectRelationship) {
			ObjectRelationship objectRelationship = (ObjectRelationship)result;

			if (!Objects.equals(
					externalReferenceCode,
					objectRelationship.getExternalReferenceCode()) ||
				(companyId != objectRelationship.getCompanyId()) ||
				(objectDefinitionId1 !=
					objectRelationship.getObjectDefinitionId1())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(5);

			sb.append(_SQL_SELECT_OBJECTRELATIONSHIP_WHERE);

			boolean bindExternalReferenceCode = false;

			if (externalReferenceCode.isEmpty()) {
				sb.append(_FINDER_COLUMN_ERC_C_ODI1_EXTERNALREFERENCECODE_3);
			}
			else {
				bindExternalReferenceCode = true;

				sb.append(_FINDER_COLUMN_ERC_C_ODI1_EXTERNALREFERENCECODE_2);
			}

			sb.append(_FINDER_COLUMN_ERC_C_ODI1_COMPANYID_2);

			sb.append(_FINDER_COLUMN_ERC_C_ODI1_OBJECTDEFINITIONID1_2);

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

				queryPos.add(objectDefinitionId1);

				List<ObjectRelationship> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByERC_C_ODI1, finderArgs, list);
					}
				}
				else {
					if (list.size() > 1) {
						Collections.sort(list, Collections.reverseOrder());

						if (_log.isWarnEnabled()) {
							if (!useFinderCache) {
								finderArgs = new Object[] {
									externalReferenceCode, companyId,
									objectDefinitionId1
								};
							}

							_log.warn(
								"ObjectRelationshipPersistenceImpl.fetchByERC_C_ODI1(String, long, long, boolean) with parameters (" +
									StringUtil.merge(finderArgs) +
										") yields a result set with more than 1 result. This violates the logical unique restriction. There is no order guarantee on which result is returned by this finder.");
						}
					}

					ObjectRelationship objectRelationship = list.get(0);

					result = objectRelationship;

					cacheResult(objectRelationship);
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
			return (ObjectRelationship)result;
		}
	}

	/**
	 * Removes the object relationship where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId1 = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId1 the object definition id1
	 * @return the object relationship that was removed
	 */
	@Override
	public ObjectRelationship removeByERC_C_ODI1(
			String externalReferenceCode, long companyId,
			long objectDefinitionId1)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = findByERC_C_ODI1(
			externalReferenceCode, companyId, objectDefinitionId1);

		return remove(objectRelationship);
	}

	/**
	 * Returns the number of object relationships where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId1 = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId1 the object definition id1
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByERC_C_ODI1(
		String externalReferenceCode, long companyId,
		long objectDefinitionId1) {

		ObjectRelationship objectRelationship = fetchByERC_C_ODI1(
			externalReferenceCode, companyId, objectDefinitionId1);

		if (objectRelationship == null) {
			return 0;
		}

		return 1;
	}

	private static final String
		_FINDER_COLUMN_ERC_C_ODI1_EXTERNALREFERENCECODE_2 =
			"objectRelationship.externalReferenceCode = ? AND ";

	private static final String
		_FINDER_COLUMN_ERC_C_ODI1_EXTERNALREFERENCECODE_3 =
			"(objectRelationship.externalReferenceCode IS NULL OR objectRelationship.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_C_ODI1_COMPANYID_2 =
		"objectRelationship.companyId = ? AND ";

	private static final String
		_FINDER_COLUMN_ERC_C_ODI1_OBJECTDEFINITIONID1_2 =
			"objectRelationship.objectDefinitionId1 = ?";

	private FinderPath _finderPathWithPaginationFindByODI1_ODI2_T;
	private FinderPath _finderPathWithoutPaginationFindByODI1_ODI2_T;
	private FinderPath _finderPathCountByODI1_ODI2_T;

	/**
	 * Returns all the object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param type the type
	 * @return the matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI1_ODI2_T(
		long objectDefinitionId1, long objectDefinitionId2, String type) {

		return findByODI1_ODI2_T(
			objectDefinitionId1, objectDefinitionId2, type, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI1_ODI2_T(
		long objectDefinitionId1, long objectDefinitionId2, String type,
		int start, int end) {

		return findByODI1_ODI2_T(
			objectDefinitionId1, objectDefinitionId2, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI1_ODI2_T(
		long objectDefinitionId1, long objectDefinitionId2, String type,
		int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return findByODI1_ODI2_T(
			objectDefinitionId1, objectDefinitionId2, type, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI1_ODI2_T(
		long objectDefinitionId1, long objectDefinitionId2, String type,
		int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		type = Objects.toString(type, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByODI1_ODI2_T;
				finderArgs = new Object[] {
					objectDefinitionId1, objectDefinitionId2, type
				};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByODI1_ODI2_T;
			finderArgs = new Object[] {
				objectDefinitionId1, objectDefinitionId2, type, start, end,
				orderByComparator
			};
		}

		List<ObjectRelationship> list = null;

		if (useFinderCache) {
			list = (List<ObjectRelationship>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectRelationship objectRelationship : list) {
					if ((objectDefinitionId1 !=
							objectRelationship.getObjectDefinitionId1()) ||
						(objectDefinitionId2 !=
							objectRelationship.getObjectDefinitionId2()) ||
						!type.equals(objectRelationship.getType())) {

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

			sb.append(_SQL_SELECT_OBJECTRELATIONSHIP_WHERE);

			sb.append(_FINDER_COLUMN_ODI1_ODI2_T_OBJECTDEFINITIONID1_2);

			sb.append(_FINDER_COLUMN_ODI1_ODI2_T_OBJECTDEFINITIONID2_2);

			boolean bindType = false;

			if (type.isEmpty()) {
				sb.append(_FINDER_COLUMN_ODI1_ODI2_T_TYPE_3);
			}
			else {
				bindType = true;

				sb.append(_FINDER_COLUMN_ODI1_ODI2_T_TYPE_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectRelationshipModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId1);

				queryPos.add(objectDefinitionId2);

				if (bindType) {
					queryPos.add(type);
				}

				list = (List<ObjectRelationship>)QueryUtil.list(
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
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByODI1_ODI2_T_First(
			long objectDefinitionId1, long objectDefinitionId2, String type,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = fetchByODI1_ODI2_T_First(
			objectDefinitionId1, objectDefinitionId2, type, orderByComparator);

		if (objectRelationship != null) {
			return objectRelationship;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId1=");
		sb.append(objectDefinitionId1);

		sb.append(", objectDefinitionId2=");
		sb.append(objectDefinitionId2);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchObjectRelationshipException(sb.toString());
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByODI1_ODI2_T_First(
		long objectDefinitionId1, long objectDefinitionId2, String type,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		List<ObjectRelationship> list = findByODI1_ODI2_T(
			objectDefinitionId1, objectDefinitionId2, type, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByODI1_ODI2_T_Last(
			long objectDefinitionId1, long objectDefinitionId2, String type,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = fetchByODI1_ODI2_T_Last(
			objectDefinitionId1, objectDefinitionId2, type, orderByComparator);

		if (objectRelationship != null) {
			return objectRelationship;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId1=");
		sb.append(objectDefinitionId1);

		sb.append(", objectDefinitionId2=");
		sb.append(objectDefinitionId2);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchObjectRelationshipException(sb.toString());
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByODI1_ODI2_T_Last(
		long objectDefinitionId1, long objectDefinitionId2, String type,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		int count = countByODI1_ODI2_T(
			objectDefinitionId1, objectDefinitionId2, type);

		if (count == 0) {
			return null;
		}

		List<ObjectRelationship> list = findByODI1_ODI2_T(
			objectDefinitionId1, objectDefinitionId2, type, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and type = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	@Override
	public ObjectRelationship[] findByODI1_ODI2_T_PrevAndNext(
			long objectRelationshipId, long objectDefinitionId1,
			long objectDefinitionId2, String type,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		type = Objects.toString(type, "");

		ObjectRelationship objectRelationship = findByPrimaryKey(
			objectRelationshipId);

		Session session = null;

		try {
			session = openSession();

			ObjectRelationship[] array = new ObjectRelationshipImpl[3];

			array[0] = getByODI1_ODI2_T_PrevAndNext(
				session, objectRelationship, objectDefinitionId1,
				objectDefinitionId2, type, orderByComparator, true);

			array[1] = objectRelationship;

			array[2] = getByODI1_ODI2_T_PrevAndNext(
				session, objectRelationship, objectDefinitionId1,
				objectDefinitionId2, type, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected ObjectRelationship getByODI1_ODI2_T_PrevAndNext(
		Session session, ObjectRelationship objectRelationship,
		long objectDefinitionId1, long objectDefinitionId2, String type,
		OrderByComparator<ObjectRelationship> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTRELATIONSHIP_WHERE);

		sb.append(_FINDER_COLUMN_ODI1_ODI2_T_OBJECTDEFINITIONID1_2);

		sb.append(_FINDER_COLUMN_ODI1_ODI2_T_OBJECTDEFINITIONID2_2);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_ODI1_ODI2_T_TYPE_3);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_ODI1_ODI2_T_TYPE_2);
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
			sb.append(ObjectRelationshipModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(objectDefinitionId1);

		queryPos.add(objectDefinitionId2);

		if (bindType) {
			queryPos.add(type);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						objectRelationship)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectRelationship> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and type = &#63; from the database.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param type the type
	 */
	@Override
	public void removeByODI1_ODI2_T(
		long objectDefinitionId1, long objectDefinitionId2, String type) {

		for (ObjectRelationship objectRelationship :
				findByODI1_ODI2_T(
					objectDefinitionId1, objectDefinitionId2, type,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(objectRelationship);
		}
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param type the type
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByODI1_ODI2_T(
		long objectDefinitionId1, long objectDefinitionId2, String type) {

		type = Objects.toString(type, "");

		FinderPath finderPath = _finderPathCountByODI1_ODI2_T;

		Object[] finderArgs = new Object[] {
			objectDefinitionId1, objectDefinitionId2, type
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_COUNT_OBJECTRELATIONSHIP_WHERE);

			sb.append(_FINDER_COLUMN_ODI1_ODI2_T_OBJECTDEFINITIONID1_2);

			sb.append(_FINDER_COLUMN_ODI1_ODI2_T_OBJECTDEFINITIONID2_2);

			boolean bindType = false;

			if (type.isEmpty()) {
				sb.append(_FINDER_COLUMN_ODI1_ODI2_T_TYPE_3);
			}
			else {
				bindType = true;

				sb.append(_FINDER_COLUMN_ODI1_ODI2_T_TYPE_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId1);

				queryPos.add(objectDefinitionId2);

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

	private static final String
		_FINDER_COLUMN_ODI1_ODI2_T_OBJECTDEFINITIONID1_2 =
			"objectRelationship.objectDefinitionId1 = ? AND ";

	private static final String
		_FINDER_COLUMN_ODI1_ODI2_T_OBJECTDEFINITIONID2_2 =
			"objectRelationship.objectDefinitionId2 = ? AND ";

	private static final String _FINDER_COLUMN_ODI1_ODI2_T_TYPE_2 =
		"objectRelationship.type = ?";

	private static final String _FINDER_COLUMN_ODI1_ODI2_T_TYPE_3 =
		"(objectRelationship.type IS NULL OR objectRelationship.type = '')";

	private FinderPath _finderPathWithPaginationFindByODI1_DT_R;
	private FinderPath _finderPathWithoutPaginationFindByODI1_DT_R;
	private FinderPath _finderPathCountByODI1_DT_R;

	/**
	 * Returns all the object relationships where objectDefinitionId1 = &#63; and deletionType = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param deletionType the deletion type
	 * @param reverse the reverse
	 * @return the matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI1_DT_R(
		long objectDefinitionId1, String deletionType, boolean reverse) {

		return findByODI1_DT_R(
			objectDefinitionId1, deletionType, reverse, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object relationships where objectDefinitionId1 = &#63; and deletionType = &#63; and reverse = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param deletionType the deletion type
	 * @param reverse the reverse
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI1_DT_R(
		long objectDefinitionId1, String deletionType, boolean reverse,
		int start, int end) {

		return findByODI1_DT_R(
			objectDefinitionId1, deletionType, reverse, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and deletionType = &#63; and reverse = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param deletionType the deletion type
	 * @param reverse the reverse
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI1_DT_R(
		long objectDefinitionId1, String deletionType, boolean reverse,
		int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return findByODI1_DT_R(
			objectDefinitionId1, deletionType, reverse, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and deletionType = &#63; and reverse = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param deletionType the deletion type
	 * @param reverse the reverse
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI1_DT_R(
		long objectDefinitionId1, String deletionType, boolean reverse,
		int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		deletionType = Objects.toString(deletionType, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByODI1_DT_R;
				finderArgs = new Object[] {
					objectDefinitionId1, deletionType, reverse
				};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByODI1_DT_R;
			finderArgs = new Object[] {
				objectDefinitionId1, deletionType, reverse, start, end,
				orderByComparator
			};
		}

		List<ObjectRelationship> list = null;

		if (useFinderCache) {
			list = (List<ObjectRelationship>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectRelationship objectRelationship : list) {
					if ((objectDefinitionId1 !=
							objectRelationship.getObjectDefinitionId1()) ||
						!deletionType.equals(
							objectRelationship.getDeletionType()) ||
						(reverse != objectRelationship.isReverse())) {

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

			sb.append(_SQL_SELECT_OBJECTRELATIONSHIP_WHERE);

			sb.append(_FINDER_COLUMN_ODI1_DT_R_OBJECTDEFINITIONID1_2);

			boolean bindDeletionType = false;

			if (deletionType.isEmpty()) {
				sb.append(_FINDER_COLUMN_ODI1_DT_R_DELETIONTYPE_3);
			}
			else {
				bindDeletionType = true;

				sb.append(_FINDER_COLUMN_ODI1_DT_R_DELETIONTYPE_2);
			}

			sb.append(_FINDER_COLUMN_ODI1_DT_R_REVERSE_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectRelationshipModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId1);

				if (bindDeletionType) {
					queryPos.add(deletionType);
				}

				queryPos.add(reverse);

				list = (List<ObjectRelationship>)QueryUtil.list(
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
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and deletionType = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param deletionType the deletion type
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByODI1_DT_R_First(
			long objectDefinitionId1, String deletionType, boolean reverse,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = fetchByODI1_DT_R_First(
			objectDefinitionId1, deletionType, reverse, orderByComparator);

		if (objectRelationship != null) {
			return objectRelationship;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId1=");
		sb.append(objectDefinitionId1);

		sb.append(", deletionType=");
		sb.append(deletionType);

		sb.append(", reverse=");
		sb.append(reverse);

		sb.append("}");

		throw new NoSuchObjectRelationshipException(sb.toString());
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and deletionType = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param deletionType the deletion type
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByODI1_DT_R_First(
		long objectDefinitionId1, String deletionType, boolean reverse,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		List<ObjectRelationship> list = findByODI1_DT_R(
			objectDefinitionId1, deletionType, reverse, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and deletionType = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param deletionType the deletion type
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByODI1_DT_R_Last(
			long objectDefinitionId1, String deletionType, boolean reverse,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = fetchByODI1_DT_R_Last(
			objectDefinitionId1, deletionType, reverse, orderByComparator);

		if (objectRelationship != null) {
			return objectRelationship;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId1=");
		sb.append(objectDefinitionId1);

		sb.append(", deletionType=");
		sb.append(deletionType);

		sb.append(", reverse=");
		sb.append(reverse);

		sb.append("}");

		throw new NoSuchObjectRelationshipException(sb.toString());
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and deletionType = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param deletionType the deletion type
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByODI1_DT_R_Last(
		long objectDefinitionId1, String deletionType, boolean reverse,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		int count = countByODI1_DT_R(
			objectDefinitionId1, deletionType, reverse);

		if (count == 0) {
			return null;
		}

		List<ObjectRelationship> list = findByODI1_DT_R(
			objectDefinitionId1, deletionType, reverse, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where objectDefinitionId1 = &#63; and deletionType = &#63; and reverse = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param objectDefinitionId1 the object definition id1
	 * @param deletionType the deletion type
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	@Override
	public ObjectRelationship[] findByODI1_DT_R_PrevAndNext(
			long objectRelationshipId, long objectDefinitionId1,
			String deletionType, boolean reverse,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		deletionType = Objects.toString(deletionType, "");

		ObjectRelationship objectRelationship = findByPrimaryKey(
			objectRelationshipId);

		Session session = null;

		try {
			session = openSession();

			ObjectRelationship[] array = new ObjectRelationshipImpl[3];

			array[0] = getByODI1_DT_R_PrevAndNext(
				session, objectRelationship, objectDefinitionId1, deletionType,
				reverse, orderByComparator, true);

			array[1] = objectRelationship;

			array[2] = getByODI1_DT_R_PrevAndNext(
				session, objectRelationship, objectDefinitionId1, deletionType,
				reverse, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected ObjectRelationship getByODI1_DT_R_PrevAndNext(
		Session session, ObjectRelationship objectRelationship,
		long objectDefinitionId1, String deletionType, boolean reverse,
		OrderByComparator<ObjectRelationship> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTRELATIONSHIP_WHERE);

		sb.append(_FINDER_COLUMN_ODI1_DT_R_OBJECTDEFINITIONID1_2);

		boolean bindDeletionType = false;

		if (deletionType.isEmpty()) {
			sb.append(_FINDER_COLUMN_ODI1_DT_R_DELETIONTYPE_3);
		}
		else {
			bindDeletionType = true;

			sb.append(_FINDER_COLUMN_ODI1_DT_R_DELETIONTYPE_2);
		}

		sb.append(_FINDER_COLUMN_ODI1_DT_R_REVERSE_2);

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
			sb.append(ObjectRelationshipModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(objectDefinitionId1);

		if (bindDeletionType) {
			queryPos.add(deletionType);
		}

		queryPos.add(reverse);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						objectRelationship)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectRelationship> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object relationships where objectDefinitionId1 = &#63; and deletionType = &#63; and reverse = &#63; from the database.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param deletionType the deletion type
	 * @param reverse the reverse
	 */
	@Override
	public void removeByODI1_DT_R(
		long objectDefinitionId1, String deletionType, boolean reverse) {

		for (ObjectRelationship objectRelationship :
				findByODI1_DT_R(
					objectDefinitionId1, deletionType, reverse,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(objectRelationship);
		}
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId1 = &#63; and deletionType = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param deletionType the deletion type
	 * @param reverse the reverse
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByODI1_DT_R(
		long objectDefinitionId1, String deletionType, boolean reverse) {

		deletionType = Objects.toString(deletionType, "");

		FinderPath finderPath = _finderPathCountByODI1_DT_R;

		Object[] finderArgs = new Object[] {
			objectDefinitionId1, deletionType, reverse
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_COUNT_OBJECTRELATIONSHIP_WHERE);

			sb.append(_FINDER_COLUMN_ODI1_DT_R_OBJECTDEFINITIONID1_2);

			boolean bindDeletionType = false;

			if (deletionType.isEmpty()) {
				sb.append(_FINDER_COLUMN_ODI1_DT_R_DELETIONTYPE_3);
			}
			else {
				bindDeletionType = true;

				sb.append(_FINDER_COLUMN_ODI1_DT_R_DELETIONTYPE_2);
			}

			sb.append(_FINDER_COLUMN_ODI1_DT_R_REVERSE_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId1);

				if (bindDeletionType) {
					queryPos.add(deletionType);
				}

				queryPos.add(reverse);

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

	private static final String _FINDER_COLUMN_ODI1_DT_R_OBJECTDEFINITIONID1_2 =
		"objectRelationship.objectDefinitionId1 = ? AND ";

	private static final String _FINDER_COLUMN_ODI1_DT_R_DELETIONTYPE_2 =
		"objectRelationship.deletionType = ? AND ";

	private static final String _FINDER_COLUMN_ODI1_DT_R_DELETIONTYPE_3 =
		"(objectRelationship.deletionType IS NULL OR objectRelationship.deletionType = '') AND ";

	private static final String _FINDER_COLUMN_ODI1_DT_R_REVERSE_2 =
		"objectRelationship.reverse = ?";

	private FinderPath _finderPathWithPaginationFindByODI1_R_T;
	private FinderPath _finderPathWithoutPaginationFindByODI1_R_T;
	private FinderPath _finderPathCountByODI1_R_T;

	/**
	 * Returns all the object relationships where objectDefinitionId1 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param type the type
	 * @return the matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI1_R_T(
		long objectDefinitionId1, boolean reverse, String type) {

		return findByODI1_R_T(
			objectDefinitionId1, reverse, type, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object relationships where objectDefinitionId1 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI1_R_T(
		long objectDefinitionId1, boolean reverse, String type, int start,
		int end) {

		return findByODI1_R_T(
			objectDefinitionId1, reverse, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI1_R_T(
		long objectDefinitionId1, boolean reverse, String type, int start,
		int end, OrderByComparator<ObjectRelationship> orderByComparator) {

		return findByODI1_R_T(
			objectDefinitionId1, reverse, type, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI1_R_T(
		long objectDefinitionId1, boolean reverse, String type, int start,
		int end, OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		type = Objects.toString(type, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByODI1_R_T;
				finderArgs = new Object[] {objectDefinitionId1, reverse, type};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByODI1_R_T;
			finderArgs = new Object[] {
				objectDefinitionId1, reverse, type, start, end,
				orderByComparator
			};
		}

		List<ObjectRelationship> list = null;

		if (useFinderCache) {
			list = (List<ObjectRelationship>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectRelationship objectRelationship : list) {
					if ((objectDefinitionId1 !=
							objectRelationship.getObjectDefinitionId1()) ||
						(reverse != objectRelationship.isReverse()) ||
						!type.equals(objectRelationship.getType())) {

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

			sb.append(_SQL_SELECT_OBJECTRELATIONSHIP_WHERE);

			sb.append(_FINDER_COLUMN_ODI1_R_T_OBJECTDEFINITIONID1_2);

			sb.append(_FINDER_COLUMN_ODI1_R_T_REVERSE_2);

			boolean bindType = false;

			if (type.isEmpty()) {
				sb.append(_FINDER_COLUMN_ODI1_R_T_TYPE_3);
			}
			else {
				bindType = true;

				sb.append(_FINDER_COLUMN_ODI1_R_T_TYPE_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectRelationshipModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId1);

				queryPos.add(reverse);

				if (bindType) {
					queryPos.add(type);
				}

				list = (List<ObjectRelationship>)QueryUtil.list(
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
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByODI1_R_T_First(
			long objectDefinitionId1, boolean reverse, String type,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = fetchByODI1_R_T_First(
			objectDefinitionId1, reverse, type, orderByComparator);

		if (objectRelationship != null) {
			return objectRelationship;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId1=");
		sb.append(objectDefinitionId1);

		sb.append(", reverse=");
		sb.append(reverse);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchObjectRelationshipException(sb.toString());
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByODI1_R_T_First(
		long objectDefinitionId1, boolean reverse, String type,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		List<ObjectRelationship> list = findByODI1_R_T(
			objectDefinitionId1, reverse, type, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByODI1_R_T_Last(
			long objectDefinitionId1, boolean reverse, String type,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = fetchByODI1_R_T_Last(
			objectDefinitionId1, reverse, type, orderByComparator);

		if (objectRelationship != null) {
			return objectRelationship;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId1=");
		sb.append(objectDefinitionId1);

		sb.append(", reverse=");
		sb.append(reverse);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchObjectRelationshipException(sb.toString());
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByODI1_R_T_Last(
		long objectDefinitionId1, boolean reverse, String type,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		int count = countByODI1_R_T(objectDefinitionId1, reverse, type);

		if (count == 0) {
			return null;
		}

		List<ObjectRelationship> list = findByODI1_R_T(
			objectDefinitionId1, reverse, type, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where objectDefinitionId1 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	@Override
	public ObjectRelationship[] findByODI1_R_T_PrevAndNext(
			long objectRelationshipId, long objectDefinitionId1,
			boolean reverse, String type,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		type = Objects.toString(type, "");

		ObjectRelationship objectRelationship = findByPrimaryKey(
			objectRelationshipId);

		Session session = null;

		try {
			session = openSession();

			ObjectRelationship[] array = new ObjectRelationshipImpl[3];

			array[0] = getByODI1_R_T_PrevAndNext(
				session, objectRelationship, objectDefinitionId1, reverse, type,
				orderByComparator, true);

			array[1] = objectRelationship;

			array[2] = getByODI1_R_T_PrevAndNext(
				session, objectRelationship, objectDefinitionId1, reverse, type,
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

	protected ObjectRelationship getByODI1_R_T_PrevAndNext(
		Session session, ObjectRelationship objectRelationship,
		long objectDefinitionId1, boolean reverse, String type,
		OrderByComparator<ObjectRelationship> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTRELATIONSHIP_WHERE);

		sb.append(_FINDER_COLUMN_ODI1_R_T_OBJECTDEFINITIONID1_2);

		sb.append(_FINDER_COLUMN_ODI1_R_T_REVERSE_2);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_ODI1_R_T_TYPE_3);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_ODI1_R_T_TYPE_2);
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
			sb.append(ObjectRelationshipModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(objectDefinitionId1);

		queryPos.add(reverse);

		if (bindType) {
			queryPos.add(type);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						objectRelationship)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectRelationship> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object relationships where objectDefinitionId1 = &#63; and reverse = &#63; and type = &#63; from the database.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param type the type
	 */
	@Override
	public void removeByODI1_R_T(
		long objectDefinitionId1, boolean reverse, String type) {

		for (ObjectRelationship objectRelationship :
				findByODI1_R_T(
					objectDefinitionId1, reverse, type, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(objectRelationship);
		}
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId1 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param type the type
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByODI1_R_T(
		long objectDefinitionId1, boolean reverse, String type) {

		type = Objects.toString(type, "");

		FinderPath finderPath = _finderPathCountByODI1_R_T;

		Object[] finderArgs = new Object[] {objectDefinitionId1, reverse, type};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_COUNT_OBJECTRELATIONSHIP_WHERE);

			sb.append(_FINDER_COLUMN_ODI1_R_T_OBJECTDEFINITIONID1_2);

			sb.append(_FINDER_COLUMN_ODI1_R_T_REVERSE_2);

			boolean bindType = false;

			if (type.isEmpty()) {
				sb.append(_FINDER_COLUMN_ODI1_R_T_TYPE_3);
			}
			else {
				bindType = true;

				sb.append(_FINDER_COLUMN_ODI1_R_T_TYPE_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId1);

				queryPos.add(reverse);

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

	private static final String _FINDER_COLUMN_ODI1_R_T_OBJECTDEFINITIONID1_2 =
		"objectRelationship.objectDefinitionId1 = ? AND ";

	private static final String _FINDER_COLUMN_ODI1_R_T_REVERSE_2 =
		"objectRelationship.reverse = ? AND ";

	private static final String _FINDER_COLUMN_ODI1_R_T_TYPE_2 =
		"objectRelationship.type = ?";

	private static final String _FINDER_COLUMN_ODI1_R_T_TYPE_3 =
		"(objectRelationship.type IS NULL OR objectRelationship.type = '')";

	private FinderPath _finderPathWithPaginationFindByODI2_R_T;
	private FinderPath _finderPathWithoutPaginationFindByODI2_R_T;
	private FinderPath _finderPathCountByODI2_R_T;

	/**
	 * Returns all the object relationships where objectDefinitionId2 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param type the type
	 * @return the matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI2_R_T(
		long objectDefinitionId2, boolean reverse, String type) {

		return findByODI2_R_T(
			objectDefinitionId2, reverse, type, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object relationships where objectDefinitionId2 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI2_R_T(
		long objectDefinitionId2, boolean reverse, String type, int start,
		int end) {

		return findByODI2_R_T(
			objectDefinitionId2, reverse, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId2 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI2_R_T(
		long objectDefinitionId2, boolean reverse, String type, int start,
		int end, OrderByComparator<ObjectRelationship> orderByComparator) {

		return findByODI2_R_T(
			objectDefinitionId2, reverse, type, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId2 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI2_R_T(
		long objectDefinitionId2, boolean reverse, String type, int start,
		int end, OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		type = Objects.toString(type, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByODI2_R_T;
				finderArgs = new Object[] {objectDefinitionId2, reverse, type};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByODI2_R_T;
			finderArgs = new Object[] {
				objectDefinitionId2, reverse, type, start, end,
				orderByComparator
			};
		}

		List<ObjectRelationship> list = null;

		if (useFinderCache) {
			list = (List<ObjectRelationship>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectRelationship objectRelationship : list) {
					if ((objectDefinitionId2 !=
							objectRelationship.getObjectDefinitionId2()) ||
						(reverse != objectRelationship.isReverse()) ||
						!type.equals(objectRelationship.getType())) {

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

			sb.append(_SQL_SELECT_OBJECTRELATIONSHIP_WHERE);

			sb.append(_FINDER_COLUMN_ODI2_R_T_OBJECTDEFINITIONID2_2);

			sb.append(_FINDER_COLUMN_ODI2_R_T_REVERSE_2);

			boolean bindType = false;

			if (type.isEmpty()) {
				sb.append(_FINDER_COLUMN_ODI2_R_T_TYPE_3);
			}
			else {
				bindType = true;

				sb.append(_FINDER_COLUMN_ODI2_R_T_TYPE_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectRelationshipModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId2);

				queryPos.add(reverse);

				if (bindType) {
					queryPos.add(type);
				}

				list = (List<ObjectRelationship>)QueryUtil.list(
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
	 * Returns the first object relationship in the ordered set where objectDefinitionId2 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByODI2_R_T_First(
			long objectDefinitionId2, boolean reverse, String type,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = fetchByODI2_R_T_First(
			objectDefinitionId2, reverse, type, orderByComparator);

		if (objectRelationship != null) {
			return objectRelationship;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId2=");
		sb.append(objectDefinitionId2);

		sb.append(", reverse=");
		sb.append(reverse);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchObjectRelationshipException(sb.toString());
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId2 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByODI2_R_T_First(
		long objectDefinitionId2, boolean reverse, String type,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		List<ObjectRelationship> list = findByODI2_R_T(
			objectDefinitionId2, reverse, type, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId2 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByODI2_R_T_Last(
			long objectDefinitionId2, boolean reverse, String type,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = fetchByODI2_R_T_Last(
			objectDefinitionId2, reverse, type, orderByComparator);

		if (objectRelationship != null) {
			return objectRelationship;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId2=");
		sb.append(objectDefinitionId2);

		sb.append(", reverse=");
		sb.append(reverse);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchObjectRelationshipException(sb.toString());
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId2 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByODI2_R_T_Last(
		long objectDefinitionId2, boolean reverse, String type,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		int count = countByODI2_R_T(objectDefinitionId2, reverse, type);

		if (count == 0) {
			return null;
		}

		List<ObjectRelationship> list = findByODI2_R_T(
			objectDefinitionId2, reverse, type, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where objectDefinitionId2 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	@Override
	public ObjectRelationship[] findByODI2_R_T_PrevAndNext(
			long objectRelationshipId, long objectDefinitionId2,
			boolean reverse, String type,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		type = Objects.toString(type, "");

		ObjectRelationship objectRelationship = findByPrimaryKey(
			objectRelationshipId);

		Session session = null;

		try {
			session = openSession();

			ObjectRelationship[] array = new ObjectRelationshipImpl[3];

			array[0] = getByODI2_R_T_PrevAndNext(
				session, objectRelationship, objectDefinitionId2, reverse, type,
				orderByComparator, true);

			array[1] = objectRelationship;

			array[2] = getByODI2_R_T_PrevAndNext(
				session, objectRelationship, objectDefinitionId2, reverse, type,
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

	protected ObjectRelationship getByODI2_R_T_PrevAndNext(
		Session session, ObjectRelationship objectRelationship,
		long objectDefinitionId2, boolean reverse, String type,
		OrderByComparator<ObjectRelationship> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTRELATIONSHIP_WHERE);

		sb.append(_FINDER_COLUMN_ODI2_R_T_OBJECTDEFINITIONID2_2);

		sb.append(_FINDER_COLUMN_ODI2_R_T_REVERSE_2);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_ODI2_R_T_TYPE_3);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_ODI2_R_T_TYPE_2);
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
			sb.append(ObjectRelationshipModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(objectDefinitionId2);

		queryPos.add(reverse);

		if (bindType) {
			queryPos.add(type);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						objectRelationship)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectRelationship> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object relationships where objectDefinitionId2 = &#63; and reverse = &#63; and type = &#63; from the database.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param type the type
	 */
	@Override
	public void removeByODI2_R_T(
		long objectDefinitionId2, boolean reverse, String type) {

		for (ObjectRelationship objectRelationship :
				findByODI2_R_T(
					objectDefinitionId2, reverse, type, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(objectRelationship);
		}
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId2 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param type the type
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByODI2_R_T(
		long objectDefinitionId2, boolean reverse, String type) {

		type = Objects.toString(type, "");

		FinderPath finderPath = _finderPathCountByODI2_R_T;

		Object[] finderArgs = new Object[] {objectDefinitionId2, reverse, type};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_COUNT_OBJECTRELATIONSHIP_WHERE);

			sb.append(_FINDER_COLUMN_ODI2_R_T_OBJECTDEFINITIONID2_2);

			sb.append(_FINDER_COLUMN_ODI2_R_T_REVERSE_2);

			boolean bindType = false;

			if (type.isEmpty()) {
				sb.append(_FINDER_COLUMN_ODI2_R_T_TYPE_3);
			}
			else {
				bindType = true;

				sb.append(_FINDER_COLUMN_ODI2_R_T_TYPE_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId2);

				queryPos.add(reverse);

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

	private static final String _FINDER_COLUMN_ODI2_R_T_OBJECTDEFINITIONID2_2 =
		"objectRelationship.objectDefinitionId2 = ? AND ";

	private static final String _FINDER_COLUMN_ODI2_R_T_REVERSE_2 =
		"objectRelationship.reverse = ? AND ";

	private static final String _FINDER_COLUMN_ODI2_R_T_TYPE_2 =
		"objectRelationship.type = ?";

	private static final String _FINDER_COLUMN_ODI2_R_T_TYPE_3 =
		"(objectRelationship.type IS NULL OR objectRelationship.type = '')";

	private FinderPath _finderPathWithPaginationFindByODI1_ODI2_N_T;
	private FinderPath _finderPathWithoutPaginationFindByODI1_ODI2_N_T;
	private FinderPath _finderPathCountByODI1_ODI2_N_T;

	/**
	 * Returns all the object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param type the type
	 * @return the matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI1_ODI2_N_T(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		String type) {

		return findByODI1_ODI2_N_T(
			objectDefinitionId1, objectDefinitionId2, name, type,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI1_ODI2_N_T(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		String type, int start, int end) {

		return findByODI1_ODI2_N_T(
			objectDefinitionId1, objectDefinitionId2, name, type, start, end,
			null);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI1_ODI2_N_T(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		String type, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return findByODI1_ODI2_N_T(
			objectDefinitionId1, objectDefinitionId2, name, type, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	@Override
	public List<ObjectRelationship> findByODI1_ODI2_N_T(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		String type, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		name = Objects.toString(name, "");
		type = Objects.toString(type, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByODI1_ODI2_N_T;
				finderArgs = new Object[] {
					objectDefinitionId1, objectDefinitionId2, name, type
				};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByODI1_ODI2_N_T;
			finderArgs = new Object[] {
				objectDefinitionId1, objectDefinitionId2, name, type, start,
				end, orderByComparator
			};
		}

		List<ObjectRelationship> list = null;

		if (useFinderCache) {
			list = (List<ObjectRelationship>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectRelationship objectRelationship : list) {
					if ((objectDefinitionId1 !=
							objectRelationship.getObjectDefinitionId1()) ||
						(objectDefinitionId2 !=
							objectRelationship.getObjectDefinitionId2()) ||
						!name.equals(objectRelationship.getName()) ||
						!type.equals(objectRelationship.getType())) {

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

			sb.append(_SQL_SELECT_OBJECTRELATIONSHIP_WHERE);

			sb.append(_FINDER_COLUMN_ODI1_ODI2_N_T_OBJECTDEFINITIONID1_2);

			sb.append(_FINDER_COLUMN_ODI1_ODI2_N_T_OBJECTDEFINITIONID2_2);

			boolean bindName = false;

			if (name.isEmpty()) {
				sb.append(_FINDER_COLUMN_ODI1_ODI2_N_T_NAME_3);
			}
			else {
				bindName = true;

				sb.append(_FINDER_COLUMN_ODI1_ODI2_N_T_NAME_2);
			}

			boolean bindType = false;

			if (type.isEmpty()) {
				sb.append(_FINDER_COLUMN_ODI1_ODI2_N_T_TYPE_3);
			}
			else {
				bindType = true;

				sb.append(_FINDER_COLUMN_ODI1_ODI2_N_T_TYPE_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectRelationshipModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId1);

				queryPos.add(objectDefinitionId2);

				if (bindName) {
					queryPos.add(name);
				}

				if (bindType) {
					queryPos.add(type);
				}

				list = (List<ObjectRelationship>)QueryUtil.list(
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
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByODI1_ODI2_N_T_First(
			long objectDefinitionId1, long objectDefinitionId2, String name,
			String type,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = fetchByODI1_ODI2_N_T_First(
			objectDefinitionId1, objectDefinitionId2, name, type,
			orderByComparator);

		if (objectRelationship != null) {
			return objectRelationship;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId1=");
		sb.append(objectDefinitionId1);

		sb.append(", objectDefinitionId2=");
		sb.append(objectDefinitionId2);

		sb.append(", name=");
		sb.append(name);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchObjectRelationshipException(sb.toString());
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByODI1_ODI2_N_T_First(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		String type, OrderByComparator<ObjectRelationship> orderByComparator) {

		List<ObjectRelationship> list = findByODI1_ODI2_N_T(
			objectDefinitionId1, objectDefinitionId2, name, type, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByODI1_ODI2_N_T_Last(
			long objectDefinitionId1, long objectDefinitionId2, String name,
			String type,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = fetchByODI1_ODI2_N_T_Last(
			objectDefinitionId1, objectDefinitionId2, name, type,
			orderByComparator);

		if (objectRelationship != null) {
			return objectRelationship;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId1=");
		sb.append(objectDefinitionId1);

		sb.append(", objectDefinitionId2=");
		sb.append(objectDefinitionId2);

		sb.append(", name=");
		sb.append(name);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchObjectRelationshipException(sb.toString());
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByODI1_ODI2_N_T_Last(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		String type, OrderByComparator<ObjectRelationship> orderByComparator) {

		int count = countByODI1_ODI2_N_T(
			objectDefinitionId1, objectDefinitionId2, name, type);

		if (count == 0) {
			return null;
		}

		List<ObjectRelationship> list = findByODI1_ODI2_N_T(
			objectDefinitionId1, objectDefinitionId2, name, type, count - 1,
			count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	@Override
	public ObjectRelationship[] findByODI1_ODI2_N_T_PrevAndNext(
			long objectRelationshipId, long objectDefinitionId1,
			long objectDefinitionId2, String name, String type,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws NoSuchObjectRelationshipException {

		name = Objects.toString(name, "");
		type = Objects.toString(type, "");

		ObjectRelationship objectRelationship = findByPrimaryKey(
			objectRelationshipId);

		Session session = null;

		try {
			session = openSession();

			ObjectRelationship[] array = new ObjectRelationshipImpl[3];

			array[0] = getByODI1_ODI2_N_T_PrevAndNext(
				session, objectRelationship, objectDefinitionId1,
				objectDefinitionId2, name, type, orderByComparator, true);

			array[1] = objectRelationship;

			array[2] = getByODI1_ODI2_N_T_PrevAndNext(
				session, objectRelationship, objectDefinitionId1,
				objectDefinitionId2, name, type, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected ObjectRelationship getByODI1_ODI2_N_T_PrevAndNext(
		Session session, ObjectRelationship objectRelationship,
		long objectDefinitionId1, long objectDefinitionId2, String name,
		String type, OrderByComparator<ObjectRelationship> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTRELATIONSHIP_WHERE);

		sb.append(_FINDER_COLUMN_ODI1_ODI2_N_T_OBJECTDEFINITIONID1_2);

		sb.append(_FINDER_COLUMN_ODI1_ODI2_N_T_OBJECTDEFINITIONID2_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_ODI1_ODI2_N_T_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_ODI1_ODI2_N_T_NAME_2);
		}

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_ODI1_ODI2_N_T_TYPE_3);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_ODI1_ODI2_N_T_TYPE_2);
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
			sb.append(ObjectRelationshipModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(objectDefinitionId1);

		queryPos.add(objectDefinitionId2);

		if (bindName) {
			queryPos.add(name);
		}

		if (bindType) {
			queryPos.add(type);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						objectRelationship)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectRelationship> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and type = &#63; from the database.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param type the type
	 */
	@Override
	public void removeByODI1_ODI2_N_T(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		String type) {

		for (ObjectRelationship objectRelationship :
				findByODI1_ODI2_N_T(
					objectDefinitionId1, objectDefinitionId2, name, type,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(objectRelationship);
		}
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param type the type
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByODI1_ODI2_N_T(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		String type) {

		name = Objects.toString(name, "");
		type = Objects.toString(type, "");

		FinderPath finderPath = _finderPathCountByODI1_ODI2_N_T;

		Object[] finderArgs = new Object[] {
			objectDefinitionId1, objectDefinitionId2, name, type
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(5);

			sb.append(_SQL_COUNT_OBJECTRELATIONSHIP_WHERE);

			sb.append(_FINDER_COLUMN_ODI1_ODI2_N_T_OBJECTDEFINITIONID1_2);

			sb.append(_FINDER_COLUMN_ODI1_ODI2_N_T_OBJECTDEFINITIONID2_2);

			boolean bindName = false;

			if (name.isEmpty()) {
				sb.append(_FINDER_COLUMN_ODI1_ODI2_N_T_NAME_3);
			}
			else {
				bindName = true;

				sb.append(_FINDER_COLUMN_ODI1_ODI2_N_T_NAME_2);
			}

			boolean bindType = false;

			if (type.isEmpty()) {
				sb.append(_FINDER_COLUMN_ODI1_ODI2_N_T_TYPE_3);
			}
			else {
				bindType = true;

				sb.append(_FINDER_COLUMN_ODI1_ODI2_N_T_TYPE_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId1);

				queryPos.add(objectDefinitionId2);

				if (bindName) {
					queryPos.add(name);
				}

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

	private static final String
		_FINDER_COLUMN_ODI1_ODI2_N_T_OBJECTDEFINITIONID1_2 =
			"objectRelationship.objectDefinitionId1 = ? AND ";

	private static final String
		_FINDER_COLUMN_ODI1_ODI2_N_T_OBJECTDEFINITIONID2_2 =
			"objectRelationship.objectDefinitionId2 = ? AND ";

	private static final String _FINDER_COLUMN_ODI1_ODI2_N_T_NAME_2 =
		"objectRelationship.name = ? AND ";

	private static final String _FINDER_COLUMN_ODI1_ODI2_N_T_NAME_3 =
		"(objectRelationship.name IS NULL OR objectRelationship.name = '') AND ";

	private static final String _FINDER_COLUMN_ODI1_ODI2_N_T_TYPE_2 =
		"objectRelationship.type = ?";

	private static final String _FINDER_COLUMN_ODI1_ODI2_N_T_TYPE_3 =
		"(objectRelationship.type IS NULL OR objectRelationship.type = '')";

	private FinderPath _finderPathFetchByODI1_ODI2_N_R_T;

	/**
	 * Returns the object relationship where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and reverse = &#63; and type = &#63; or throws a <code>NoSuchObjectRelationshipException</code> if it could not be found.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param reverse the reverse
	 * @param type the type
	 * @return the matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship findByODI1_ODI2_N_R_T(
			long objectDefinitionId1, long objectDefinitionId2, String name,
			boolean reverse, String type)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = fetchByODI1_ODI2_N_R_T(
			objectDefinitionId1, objectDefinitionId2, name, reverse, type);

		if (objectRelationship == null) {
			StringBundler sb = new StringBundler(12);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("objectDefinitionId1=");
			sb.append(objectDefinitionId1);

			sb.append(", objectDefinitionId2=");
			sb.append(objectDefinitionId2);

			sb.append(", name=");
			sb.append(name);

			sb.append(", reverse=");
			sb.append(reverse);

			sb.append(", type=");
			sb.append(type);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchObjectRelationshipException(sb.toString());
		}

		return objectRelationship;
	}

	/**
	 * Returns the object relationship where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and reverse = &#63; and type = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param reverse the reverse
	 * @param type the type
	 * @return the matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByODI1_ODI2_N_R_T(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		boolean reverse, String type) {

		return fetchByODI1_ODI2_N_R_T(
			objectDefinitionId1, objectDefinitionId2, name, reverse, type,
			true);
	}

	/**
	 * Returns the object relationship where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and reverse = &#63; and type = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param reverse the reverse
	 * @param type the type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Override
	public ObjectRelationship fetchByODI1_ODI2_N_R_T(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		boolean reverse, String type, boolean useFinderCache) {

		name = Objects.toString(name, "");
		type = Objects.toString(type, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {
				objectDefinitionId1, objectDefinitionId2, name, reverse, type
			};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByODI1_ODI2_N_R_T, finderArgs, this);
		}

		if (result instanceof ObjectRelationship) {
			ObjectRelationship objectRelationship = (ObjectRelationship)result;

			if ((objectDefinitionId1 !=
					objectRelationship.getObjectDefinitionId1()) ||
				(objectDefinitionId2 !=
					objectRelationship.getObjectDefinitionId2()) ||
				!Objects.equals(name, objectRelationship.getName()) ||
				(reverse != objectRelationship.isReverse()) ||
				!Objects.equals(type, objectRelationship.getType())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(7);

			sb.append(_SQL_SELECT_OBJECTRELATIONSHIP_WHERE);

			sb.append(_FINDER_COLUMN_ODI1_ODI2_N_R_T_OBJECTDEFINITIONID1_2);

			sb.append(_FINDER_COLUMN_ODI1_ODI2_N_R_T_OBJECTDEFINITIONID2_2);

			boolean bindName = false;

			if (name.isEmpty()) {
				sb.append(_FINDER_COLUMN_ODI1_ODI2_N_R_T_NAME_3);
			}
			else {
				bindName = true;

				sb.append(_FINDER_COLUMN_ODI1_ODI2_N_R_T_NAME_2);
			}

			sb.append(_FINDER_COLUMN_ODI1_ODI2_N_R_T_REVERSE_2);

			boolean bindType = false;

			if (type.isEmpty()) {
				sb.append(_FINDER_COLUMN_ODI1_ODI2_N_R_T_TYPE_3);
			}
			else {
				bindType = true;

				sb.append(_FINDER_COLUMN_ODI1_ODI2_N_R_T_TYPE_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId1);

				queryPos.add(objectDefinitionId2);

				if (bindName) {
					queryPos.add(name);
				}

				queryPos.add(reverse);

				if (bindType) {
					queryPos.add(type);
				}

				List<ObjectRelationship> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByODI1_ODI2_N_R_T, finderArgs,
							list);
					}
				}
				else {
					if (list.size() > 1) {
						Collections.sort(list, Collections.reverseOrder());

						if (_log.isWarnEnabled()) {
							if (!useFinderCache) {
								finderArgs = new Object[] {
									objectDefinitionId1, objectDefinitionId2,
									name, reverse, type
								};
							}

							_log.warn(
								"ObjectRelationshipPersistenceImpl.fetchByODI1_ODI2_N_R_T(long, long, String, boolean, String, boolean) with parameters (" +
									StringUtil.merge(finderArgs) +
										") yields a result set with more than 1 result. This violates the logical unique restriction. There is no order guarantee on which result is returned by this finder.");
						}
					}

					ObjectRelationship objectRelationship = list.get(0);

					result = objectRelationship;

					cacheResult(objectRelationship);
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
			return (ObjectRelationship)result;
		}
	}

	/**
	 * Removes the object relationship where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and reverse = &#63; and type = &#63; from the database.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param reverse the reverse
	 * @param type the type
	 * @return the object relationship that was removed
	 */
	@Override
	public ObjectRelationship removeByODI1_ODI2_N_R_T(
			long objectDefinitionId1, long objectDefinitionId2, String name,
			boolean reverse, String type)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = findByODI1_ODI2_N_R_T(
			objectDefinitionId1, objectDefinitionId2, name, reverse, type);

		return remove(objectRelationship);
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param reverse the reverse
	 * @param type the type
	 * @return the number of matching object relationships
	 */
	@Override
	public int countByODI1_ODI2_N_R_T(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		boolean reverse, String type) {

		ObjectRelationship objectRelationship = fetchByODI1_ODI2_N_R_T(
			objectDefinitionId1, objectDefinitionId2, name, reverse, type);

		if (objectRelationship == null) {
			return 0;
		}

		return 1;
	}

	private static final String
		_FINDER_COLUMN_ODI1_ODI2_N_R_T_OBJECTDEFINITIONID1_2 =
			"objectRelationship.objectDefinitionId1 = ? AND ";

	private static final String
		_FINDER_COLUMN_ODI1_ODI2_N_R_T_OBJECTDEFINITIONID2_2 =
			"objectRelationship.objectDefinitionId2 = ? AND ";

	private static final String _FINDER_COLUMN_ODI1_ODI2_N_R_T_NAME_2 =
		"objectRelationship.name = ? AND ";

	private static final String _FINDER_COLUMN_ODI1_ODI2_N_R_T_NAME_3 =
		"(objectRelationship.name IS NULL OR objectRelationship.name = '') AND ";

	private static final String _FINDER_COLUMN_ODI1_ODI2_N_R_T_REVERSE_2 =
		"objectRelationship.reverse = ? AND ";

	private static final String _FINDER_COLUMN_ODI1_ODI2_N_R_T_TYPE_2 =
		"objectRelationship.type = ?";

	private static final String _FINDER_COLUMN_ODI1_ODI2_N_R_T_TYPE_3 =
		"(objectRelationship.type IS NULL OR objectRelationship.type = '')";

	public ObjectRelationshipPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("system", "system_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ObjectRelationship.class);

		setModelImplClass(ObjectRelationshipImpl.class);
		setModelPKClass(long.class);

		setTable(ObjectRelationshipTable.INSTANCE);
	}

	/**
	 * Caches the object relationship in the entity cache if it is enabled.
	 *
	 * @param objectRelationship the object relationship
	 */
	@Override
	public void cacheResult(ObjectRelationship objectRelationship) {
		entityCache.putResult(
			ObjectRelationshipImpl.class, objectRelationship.getPrimaryKey(),
			objectRelationship);

		finderCache.putResult(
			_finderPathFetchByObjectFieldId2,
			new Object[] {objectRelationship.getObjectFieldId2()},
			objectRelationship);

		finderCache.putResult(
			_finderPathFetchByDTN_R,
			new Object[] {
				objectRelationship.getDBTableName(),
				objectRelationship.isReverse()
			},
			objectRelationship);

		finderCache.putResult(
			_finderPathFetchByERC_C_ODI1,
			new Object[] {
				objectRelationship.getExternalReferenceCode(),
				objectRelationship.getCompanyId(),
				objectRelationship.getObjectDefinitionId1()
			},
			objectRelationship);

		finderCache.putResult(
			_finderPathFetchByODI1_ODI2_N_R_T,
			new Object[] {
				objectRelationship.getObjectDefinitionId1(),
				objectRelationship.getObjectDefinitionId2(),
				objectRelationship.getName(), objectRelationship.isReverse(),
				objectRelationship.getType()
			},
			objectRelationship);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the object relationships in the entity cache if it is enabled.
	 *
	 * @param objectRelationships the object relationships
	 */
	@Override
	public void cacheResult(List<ObjectRelationship> objectRelationships) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (objectRelationships.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (ObjectRelationship objectRelationship : objectRelationships) {
			if (entityCache.getResult(
					ObjectRelationshipImpl.class,
					objectRelationship.getPrimaryKey()) == null) {

				cacheResult(objectRelationship);
			}
		}
	}

	/**
	 * Clears the cache for all object relationships.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(ObjectRelationshipImpl.class);

		finderCache.clearCache(ObjectRelationshipImpl.class);
	}

	/**
	 * Clears the cache for the object relationship.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(ObjectRelationship objectRelationship) {
		entityCache.removeResult(
			ObjectRelationshipImpl.class, objectRelationship);
	}

	@Override
	public void clearCache(List<ObjectRelationship> objectRelationships) {
		for (ObjectRelationship objectRelationship : objectRelationships) {
			entityCache.removeResult(
				ObjectRelationshipImpl.class, objectRelationship);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(ObjectRelationshipImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(ObjectRelationshipImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		ObjectRelationshipModelImpl objectRelationshipModelImpl) {

		Object[] args = new Object[] {
			objectRelationshipModelImpl.getObjectFieldId2()
		};

		finderCache.putResult(
			_finderPathFetchByObjectFieldId2, args,
			objectRelationshipModelImpl);

		args = new Object[] {
			objectRelationshipModelImpl.getDBTableName(),
			objectRelationshipModelImpl.isReverse()
		};

		finderCache.putResult(
			_finderPathFetchByDTN_R, args, objectRelationshipModelImpl);

		args = new Object[] {
			objectRelationshipModelImpl.getExternalReferenceCode(),
			objectRelationshipModelImpl.getCompanyId(),
			objectRelationshipModelImpl.getObjectDefinitionId1()
		};

		finderCache.putResult(
			_finderPathFetchByERC_C_ODI1, args, objectRelationshipModelImpl);

		args = new Object[] {
			objectRelationshipModelImpl.getObjectDefinitionId1(),
			objectRelationshipModelImpl.getObjectDefinitionId2(),
			objectRelationshipModelImpl.getName(),
			objectRelationshipModelImpl.isReverse(),
			objectRelationshipModelImpl.getType()
		};

		finderCache.putResult(
			_finderPathFetchByODI1_ODI2_N_R_T, args,
			objectRelationshipModelImpl);
	}

	/**
	 * Creates a new object relationship with the primary key. Does not add the object relationship to the database.
	 *
	 * @param objectRelationshipId the primary key for the new object relationship
	 * @return the new object relationship
	 */
	@Override
	public ObjectRelationship create(long objectRelationshipId) {
		ObjectRelationship objectRelationship = new ObjectRelationshipImpl();

		objectRelationship.setNew(true);
		objectRelationship.setPrimaryKey(objectRelationshipId);

		String uuid = PortalUUIDUtil.generate();

		objectRelationship.setUuid(uuid);

		objectRelationship.setCompanyId(CompanyThreadLocal.getCompanyId());

		return objectRelationship;
	}

	/**
	 * Removes the object relationship with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectRelationshipId the primary key of the object relationship
	 * @return the object relationship that was removed
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	@Override
	public ObjectRelationship remove(long objectRelationshipId)
		throws NoSuchObjectRelationshipException {

		return remove((Serializable)objectRelationshipId);
	}

	/**
	 * Removes the object relationship with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the object relationship
	 * @return the object relationship that was removed
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	@Override
	public ObjectRelationship remove(Serializable primaryKey)
		throws NoSuchObjectRelationshipException {

		Session session = null;

		try {
			session = openSession();

			ObjectRelationship objectRelationship =
				(ObjectRelationship)session.get(
					ObjectRelationshipImpl.class, primaryKey);

			if (objectRelationship == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchObjectRelationshipException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(objectRelationship);
		}
		catch (NoSuchObjectRelationshipException noSuchEntityException) {
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
	protected ObjectRelationship removeImpl(
		ObjectRelationship objectRelationship) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(objectRelationship)) {
				objectRelationship = (ObjectRelationship)session.get(
					ObjectRelationshipImpl.class,
					objectRelationship.getPrimaryKeyObj());
			}

			if (objectRelationship != null) {
				session.delete(objectRelationship);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (objectRelationship != null) {
			clearCache(objectRelationship);
		}

		return objectRelationship;
	}

	@Override
	public ObjectRelationship updateImpl(
		ObjectRelationship objectRelationship) {

		boolean isNew = objectRelationship.isNew();

		if (!(objectRelationship instanceof ObjectRelationshipModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(objectRelationship.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					objectRelationship);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in objectRelationship proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ObjectRelationship implementation " +
					objectRelationship.getClass());
		}

		ObjectRelationshipModelImpl objectRelationshipModelImpl =
			(ObjectRelationshipModelImpl)objectRelationship;

		if (Validator.isNull(objectRelationship.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			objectRelationship.setUuid(uuid);
		}

		if (Validator.isNull(objectRelationship.getExternalReferenceCode())) {
			objectRelationship.setExternalReferenceCode(
				objectRelationship.getUuid());
		}
		else {
			if (!Objects.equals(
					objectRelationshipModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					objectRelationship.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = objectRelationship.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = objectRelationship.getPrimaryKey();
					}

					try {
						objectRelationship.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								ObjectRelationship.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								objectRelationship.getExternalReferenceCode(),
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

		if (isNew && (objectRelationship.getCreateDate() == null)) {
			if (serviceContext == null) {
				objectRelationship.setCreateDate(date);
			}
			else {
				objectRelationship.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!objectRelationshipModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				objectRelationship.setModifiedDate(date);
			}
			else {
				objectRelationship.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(objectRelationship);
			}
			else {
				objectRelationship = (ObjectRelationship)session.merge(
					objectRelationship);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			ObjectRelationshipImpl.class, objectRelationshipModelImpl, false,
			true);

		cacheUniqueFindersCache(objectRelationshipModelImpl);

		if (isNew) {
			objectRelationship.setNew(false);
		}

		objectRelationship.resetOriginalValues();

		return objectRelationship;
	}

	/**
	 * Returns the object relationship with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the object relationship
	 * @return the object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	@Override
	public ObjectRelationship findByPrimaryKey(Serializable primaryKey)
		throws NoSuchObjectRelationshipException {

		ObjectRelationship objectRelationship = fetchByPrimaryKey(primaryKey);

		if (objectRelationship == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchObjectRelationshipException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return objectRelationship;
	}

	/**
	 * Returns the object relationship with the primary key or throws a <code>NoSuchObjectRelationshipException</code> if it could not be found.
	 *
	 * @param objectRelationshipId the primary key of the object relationship
	 * @return the object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	@Override
	public ObjectRelationship findByPrimaryKey(long objectRelationshipId)
		throws NoSuchObjectRelationshipException {

		return findByPrimaryKey((Serializable)objectRelationshipId);
	}

	/**
	 * Returns the object relationship with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectRelationshipId the primary key of the object relationship
	 * @return the object relationship, or <code>null</code> if a object relationship with the primary key could not be found
	 */
	@Override
	public ObjectRelationship fetchByPrimaryKey(long objectRelationshipId) {
		return fetchByPrimaryKey((Serializable)objectRelationshipId);
	}

	/**
	 * Returns all the object relationships.
	 *
	 * @return the object relationships
	 */
	@Override
	public List<ObjectRelationship> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object relationships.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of object relationships
	 */
	@Override
	public List<ObjectRelationship> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the object relationships.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of object relationships
	 */
	@Override
	public List<ObjectRelationship> findAll(
		int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object relationships.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of object relationships
	 */
	@Override
	public List<ObjectRelationship> findAll(
		int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
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

		List<ObjectRelationship> list = null;

		if (useFinderCache) {
			list = (List<ObjectRelationship>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_OBJECTRELATIONSHIP);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_OBJECTRELATIONSHIP;

				sql = sql.concat(ObjectRelationshipModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<ObjectRelationship>)QueryUtil.list(
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
	 * Removes all the object relationships from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (ObjectRelationship objectRelationship : findAll()) {
			remove(objectRelationship);
		}
	}

	/**
	 * Returns the number of object relationships.
	 *
	 * @return the number of object relationships
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
					_SQL_COUNT_OBJECTRELATIONSHIP);

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
		return "objectRelationshipId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OBJECTRELATIONSHIP;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ObjectRelationshipModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the object relationship persistence.
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

		_finderPathWithPaginationFindByObjectDefinitionId1 = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByObjectDefinitionId1",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"objectDefinitionId1"}, true);

		_finderPathWithoutPaginationFindByObjectDefinitionId1 = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"findByObjectDefinitionId1", new String[] {Long.class.getName()},
			new String[] {"objectDefinitionId1"}, true);

		_finderPathCountByObjectDefinitionId1 = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByObjectDefinitionId1", new String[] {Long.class.getName()},
			new String[] {"objectDefinitionId1"}, false);

		_finderPathWithPaginationFindByObjectDefinitionId2 = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByObjectDefinitionId2",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"objectDefinitionId2"}, true);

		_finderPathWithoutPaginationFindByObjectDefinitionId2 = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"findByObjectDefinitionId2", new String[] {Long.class.getName()},
			new String[] {"objectDefinitionId2"}, true);

		_finderPathCountByObjectDefinitionId2 = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByObjectDefinitionId2", new String[] {Long.class.getName()},
			new String[] {"objectDefinitionId2"}, false);

		_finderPathFetchByObjectFieldId2 = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByObjectFieldId2",
			new String[] {Long.class.getName()},
			new String[] {"objectFieldId2"}, true);

		_finderPathWithPaginationFindByParameterObjectFieldId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
			"findByParameterObjectFieldId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"parameterObjectFieldId"}, true);

		_finderPathWithoutPaginationFindByParameterObjectFieldId =
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
				"findByParameterObjectFieldId",
				new String[] {Long.class.getName()},
				new String[] {"parameterObjectFieldId"}, true);

		_finderPathCountByParameterObjectFieldId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByParameterObjectFieldId",
			new String[] {Long.class.getName()},
			new String[] {"parameterObjectFieldId"}, false);

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

		_finderPathWithPaginationFindByODI1_E = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI1_E",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"objectDefinitionId1", "edge"}, true);

		_finderPathWithoutPaginationFindByODI1_E = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByODI1_E",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"objectDefinitionId1", "edge"}, true);

		_finderPathCountByODI1_E = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByODI1_E",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"objectDefinitionId1", "edge"}, false);

		_finderPathWithPaginationFindByODI1_N = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI1_N",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"objectDefinitionId1", "name"}, true);

		_finderPathWithoutPaginationFindByODI1_N = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByODI1_N",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"objectDefinitionId1", "name"}, true);

		_finderPathCountByODI1_N = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByODI1_N",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"objectDefinitionId1", "name"}, false);

		_finderPathWithPaginationFindByODI1_R = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI1_R",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"objectDefinitionId1", "reverse"}, true);

		_finderPathWithoutPaginationFindByODI1_R = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByODI1_R",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"objectDefinitionId1", "reverse"}, true);

		_finderPathCountByODI1_R = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByODI1_R",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"objectDefinitionId1", "reverse"}, false);

		_finderPathWithPaginationFindByODI2_E = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI2_E",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"objectDefinitionId2", "edge"}, true);

		_finderPathWithoutPaginationFindByODI2_E = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByODI2_E",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"objectDefinitionId2", "edge"}, true);

		_finderPathCountByODI2_E = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByODI2_E",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"objectDefinitionId2", "edge"}, false);

		_finderPathWithPaginationFindByODI2_R = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI2_R",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"objectDefinitionId2", "reverse"}, true);

		_finderPathWithoutPaginationFindByODI2_R = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByODI2_R",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"objectDefinitionId2", "reverse"}, true);

		_finderPathCountByODI2_R = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByODI2_R",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"objectDefinitionId2", "reverse"}, false);

		_finderPathFetchByDTN_R = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByDTN_R",
			new String[] {String.class.getName(), Boolean.class.getName()},
			new String[] {"dbTableName", "reverse"}, true);

		_finderPathFetchByERC_C_ODI1 = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_C_ODI1",
			new String[] {
				String.class.getName(), Long.class.getName(),
				Long.class.getName()
			},
			new String[] {
				"externalReferenceCode", "companyId", "objectDefinitionId1"
			},
			true);

		_finderPathWithPaginationFindByODI1_ODI2_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI1_ODI2_T",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {
				"objectDefinitionId1", "objectDefinitionId2", "type_"
			},
			true);

		_finderPathWithoutPaginationFindByODI1_ODI2_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByODI1_ODI2_T",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName()
			},
			new String[] {
				"objectDefinitionId1", "objectDefinitionId2", "type_"
			},
			true);

		_finderPathCountByODI1_ODI2_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByODI1_ODI2_T",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName()
			},
			new String[] {
				"objectDefinitionId1", "objectDefinitionId2", "type_"
			},
			false);

		_finderPathWithPaginationFindByODI1_DT_R = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI1_DT_R",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Boolean.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"objectDefinitionId1", "deletionType", "reverse"},
			true);

		_finderPathWithoutPaginationFindByODI1_DT_R = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByODI1_DT_R",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Boolean.class.getName()
			},
			new String[] {"objectDefinitionId1", "deletionType", "reverse"},
			true);

		_finderPathCountByODI1_DT_R = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByODI1_DT_R",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Boolean.class.getName()
			},
			new String[] {"objectDefinitionId1", "deletionType", "reverse"},
			false);

		_finderPathWithPaginationFindByODI1_R_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI1_R_T",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"objectDefinitionId1", "reverse", "type_"}, true);

		_finderPathWithoutPaginationFindByODI1_R_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByODI1_R_T",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName()
			},
			new String[] {"objectDefinitionId1", "reverse", "type_"}, true);

		_finderPathCountByODI1_R_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByODI1_R_T",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName()
			},
			new String[] {"objectDefinitionId1", "reverse", "type_"}, false);

		_finderPathWithPaginationFindByODI2_R_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI2_R_T",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"objectDefinitionId2", "reverse", "type_"}, true);

		_finderPathWithoutPaginationFindByODI2_R_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByODI2_R_T",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName()
			},
			new String[] {"objectDefinitionId2", "reverse", "type_"}, true);

		_finderPathCountByODI2_R_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByODI2_R_T",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName()
			},
			new String[] {"objectDefinitionId2", "reverse", "type_"}, false);

		_finderPathWithPaginationFindByODI1_ODI2_N_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI1_ODI2_N_T",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {
				"objectDefinitionId1", "objectDefinitionId2", "name", "type_"
			},
			true);

		_finderPathWithoutPaginationFindByODI1_ODI2_N_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByODI1_ODI2_N_T",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName(), String.class.getName()
			},
			new String[] {
				"objectDefinitionId1", "objectDefinitionId2", "name", "type_"
			},
			true);

		_finderPathCountByODI1_ODI2_N_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByODI1_ODI2_N_T",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName(), String.class.getName()
			},
			new String[] {
				"objectDefinitionId1", "objectDefinitionId2", "name", "type_"
			},
			false);

		_finderPathFetchByODI1_ODI2_N_R_T = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByODI1_ODI2_N_R_T",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName(), Boolean.class.getName(),
				String.class.getName()
			},
			new String[] {
				"objectDefinitionId1", "objectDefinitionId2", "name", "reverse",
				"type_"
			},
			true);

		ObjectRelationshipUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ObjectRelationshipUtil.setPersistence(null);

		entityCache.removeCache(ObjectRelationshipImpl.class.getName());
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

	private static final String _SQL_SELECT_OBJECTRELATIONSHIP =
		"SELECT objectRelationship FROM ObjectRelationship objectRelationship";

	private static final String _SQL_SELECT_OBJECTRELATIONSHIP_WHERE =
		"SELECT objectRelationship FROM ObjectRelationship objectRelationship WHERE ";

	private static final String _SQL_COUNT_OBJECTRELATIONSHIP =
		"SELECT COUNT(objectRelationship) FROM ObjectRelationship objectRelationship";

	private static final String _SQL_COUNT_OBJECTRELATIONSHIP_WHERE =
		"SELECT COUNT(objectRelationship) FROM ObjectRelationship objectRelationship WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS = "objectRelationship.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No ObjectRelationship exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ObjectRelationship exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectRelationshipPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "system", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}