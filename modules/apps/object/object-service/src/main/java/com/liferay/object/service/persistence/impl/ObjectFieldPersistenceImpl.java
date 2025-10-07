/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.impl;

import com.liferay.object.exception.NoSuchObjectFieldException;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldTable;
import com.liferay.object.model.impl.ObjectFieldImpl;
import com.liferay.object.model.impl.ObjectFieldModelImpl;
import com.liferay.object.service.persistence.ObjectFieldPersistence;
import com.liferay.object.service.persistence.ObjectFieldUtil;
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
 * The persistence implementation for the object field service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = ObjectFieldPersistence.class)
public class ObjectFieldPersistenceImpl
	extends BasePersistenceImpl<ObjectField> implements ObjectFieldPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ObjectFieldUtil</code> to access the object field persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ObjectFieldImpl.class.getName();

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
	 * Returns all the object fields where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching object fields
	 */
	@Override
	public List<ObjectField> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object fields where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	@Override
	public List<ObjectField> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object fields where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectField> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object fields where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectField> orderByComparator,
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

		List<ObjectField> list = null;

		if (useFinderCache) {
			list = (List<ObjectField>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectField objectField : list) {
					if (!uuid.equals(objectField.getUuid())) {
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

			sb.append(_SQL_SELECT_OBJECTFIELD_WHERE);

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
				sb.append(ObjectFieldModelImpl.ORDER_BY_JPQL);
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

				list = (List<ObjectField>)QueryUtil.list(
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
	 * Returns the first object field in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByUuid_First(
			String uuid, OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		ObjectField objectField = fetchByUuid_First(uuid, orderByComparator);

		if (objectField != null) {
			return objectField;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchObjectFieldException(sb.toString());
	}

	/**
	 * Returns the first object field in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByUuid_First(
		String uuid, OrderByComparator<ObjectField> orderByComparator) {

		List<ObjectField> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object field in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByUuid_Last(
			String uuid, OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		ObjectField objectField = fetchByUuid_Last(uuid, orderByComparator);

		if (objectField != null) {
			return objectField;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchObjectFieldException(sb.toString());
	}

	/**
	 * Returns the last object field in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByUuid_Last(
		String uuid, OrderByComparator<ObjectField> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<ObjectField> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object fields before and after the current object field in the ordered set where uuid = &#63;.
	 *
	 * @param objectFieldId the primary key of the current object field
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object field
	 * @throws NoSuchObjectFieldException if a object field with the primary key could not be found
	 */
	@Override
	public ObjectField[] findByUuid_PrevAndNext(
			long objectFieldId, String uuid,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		uuid = Objects.toString(uuid, "");

		ObjectField objectField = findByPrimaryKey(objectFieldId);

		Session session = null;

		try {
			session = openSession();

			ObjectField[] array = new ObjectFieldImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, objectField, uuid, orderByComparator, true);

			array[1] = objectField;

			array[2] = getByUuid_PrevAndNext(
				session, objectField, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected ObjectField getByUuid_PrevAndNext(
		Session session, ObjectField objectField, String uuid,
		OrderByComparator<ObjectField> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_OBJECTFIELD_WHERE);

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
			sb.append(ObjectFieldModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(objectField)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectField> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object fields where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (ObjectField objectField :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(objectField);
		}
	}

	/**
	 * Returns the number of object fields where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object fields
	 */
	@Override
	public int countByUuid(String uuid) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid;

		Object[] finderArgs = new Object[] {uuid};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_OBJECTFIELD_WHERE);

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
		"objectField.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(objectField.uuid IS NULL OR objectField.uuid = '')";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the object fields where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching object fields
	 */
	@Override
	public List<ObjectField> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object fields where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	@Override
	public List<ObjectField> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object fields where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectField> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object fields where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectField> orderByComparator,
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

		List<ObjectField> list = null;

		if (useFinderCache) {
			list = (List<ObjectField>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectField objectField : list) {
					if (!uuid.equals(objectField.getUuid()) ||
						(companyId != objectField.getCompanyId())) {

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

			sb.append(_SQL_SELECT_OBJECTFIELD_WHERE);

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
				sb.append(ObjectFieldModelImpl.ORDER_BY_JPQL);
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

				list = (List<ObjectField>)QueryUtil.list(
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
	 * Returns the first object field in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		ObjectField objectField = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (objectField != null) {
			return objectField;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchObjectFieldException(sb.toString());
	}

	/**
	 * Returns the first object field in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectField> orderByComparator) {

		List<ObjectField> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object field in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		ObjectField objectField = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (objectField != null) {
			return objectField;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchObjectFieldException(sb.toString());
	}

	/**
	 * Returns the last object field in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<ObjectField> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<ObjectField> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object fields before and after the current object field in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param objectFieldId the primary key of the current object field
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object field
	 * @throws NoSuchObjectFieldException if a object field with the primary key could not be found
	 */
	@Override
	public ObjectField[] findByUuid_C_PrevAndNext(
			long objectFieldId, String uuid, long companyId,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		uuid = Objects.toString(uuid, "");

		ObjectField objectField = findByPrimaryKey(objectFieldId);

		Session session = null;

		try {
			session = openSession();

			ObjectField[] array = new ObjectFieldImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, objectField, uuid, companyId, orderByComparator, true);

			array[1] = objectField;

			array[2] = getByUuid_C_PrevAndNext(
				session, objectField, uuid, companyId, orderByComparator,
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

	protected ObjectField getByUuid_C_PrevAndNext(
		Session session, ObjectField objectField, String uuid, long companyId,
		OrderByComparator<ObjectField> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_OBJECTFIELD_WHERE);

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
			sb.append(ObjectFieldModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(objectField)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectField> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object fields where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (ObjectField objectField :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(objectField);
		}
	}

	/**
	 * Returns the number of object fields where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object fields
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid_C;

		Object[] finderArgs = new Object[] {uuid, companyId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_OBJECTFIELD_WHERE);

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
		"objectField.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(objectField.uuid IS NULL OR objectField.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"objectField.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;

	/**
	 * Returns all the object fields where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching object fields
	 */
	@Override
	public List<ObjectField> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object fields where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	@Override
	public List<ObjectField> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object fields where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<ObjectField> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object fields where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<ObjectField> orderByComparator,
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

		List<ObjectField> list = null;

		if (useFinderCache) {
			list = (List<ObjectField>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectField objectField : list) {
					if (companyId != objectField.getCompanyId()) {
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

			sb.append(_SQL_SELECT_OBJECTFIELD_WHERE);

			sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectFieldModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				list = (List<ObjectField>)QueryUtil.list(
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
	 * Returns the first object field in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByCompanyId_First(
			long companyId, OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		ObjectField objectField = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (objectField != null) {
			return objectField;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchObjectFieldException(sb.toString());
	}

	/**
	 * Returns the first object field in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByCompanyId_First(
		long companyId, OrderByComparator<ObjectField> orderByComparator) {

		List<ObjectField> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object field in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByCompanyId_Last(
			long companyId, OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		ObjectField objectField = fetchByCompanyId_Last(
			companyId, orderByComparator);

		if (objectField != null) {
			return objectField;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchObjectFieldException(sb.toString());
	}

	/**
	 * Returns the last object field in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByCompanyId_Last(
		long companyId, OrderByComparator<ObjectField> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<ObjectField> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object fields before and after the current object field in the ordered set where companyId = &#63;.
	 *
	 * @param objectFieldId the primary key of the current object field
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object field
	 * @throws NoSuchObjectFieldException if a object field with the primary key could not be found
	 */
	@Override
	public ObjectField[] findByCompanyId_PrevAndNext(
			long objectFieldId, long companyId,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		ObjectField objectField = findByPrimaryKey(objectFieldId);

		Session session = null;

		try {
			session = openSession();

			ObjectField[] array = new ObjectFieldImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, objectField, companyId, orderByComparator, true);

			array[1] = objectField;

			array[2] = getByCompanyId_PrevAndNext(
				session, objectField, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected ObjectField getByCompanyId_PrevAndNext(
		Session session, ObjectField objectField, long companyId,
		OrderByComparator<ObjectField> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_OBJECTFIELD_WHERE);

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
			sb.append(ObjectFieldModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(objectField)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectField> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object fields where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (ObjectField objectField :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(objectField);
		}
	}

	/**
	 * Returns the number of object fields where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching object fields
	 */
	@Override
	public int countByCompanyId(long companyId) {
		FinderPath finderPath = _finderPathCountByCompanyId;

		Object[] finderArgs = new Object[] {companyId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_OBJECTFIELD_WHERE);

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
		"objectField.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByListTypeDefinitionId;
	private FinderPath _finderPathWithoutPaginationFindByListTypeDefinitionId;
	private FinderPath _finderPathCountByListTypeDefinitionId;

	/**
	 * Returns all the object fields where listTypeDefinitionId = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @return the matching object fields
	 */
	@Override
	public List<ObjectField> findByListTypeDefinitionId(
		long listTypeDefinitionId) {

		return findByListTypeDefinitionId(
			listTypeDefinitionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object fields where listTypeDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	@Override
	public List<ObjectField> findByListTypeDefinitionId(
		long listTypeDefinitionId, int start, int end) {

		return findByListTypeDefinitionId(
			listTypeDefinitionId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object fields where listTypeDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByListTypeDefinitionId(
		long listTypeDefinitionId, int start, int end,
		OrderByComparator<ObjectField> orderByComparator) {

		return findByListTypeDefinitionId(
			listTypeDefinitionId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object fields where listTypeDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByListTypeDefinitionId(
		long listTypeDefinitionId, int start, int end,
		OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath =
					_finderPathWithoutPaginationFindByListTypeDefinitionId;
				finderArgs = new Object[] {listTypeDefinitionId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByListTypeDefinitionId;
			finderArgs = new Object[] {
				listTypeDefinitionId, start, end, orderByComparator
			};
		}

		List<ObjectField> list = null;

		if (useFinderCache) {
			list = (List<ObjectField>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectField objectField : list) {
					if (listTypeDefinitionId !=
							objectField.getListTypeDefinitionId()) {

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

			sb.append(_SQL_SELECT_OBJECTFIELD_WHERE);

			sb.append(
				_FINDER_COLUMN_LISTTYPEDEFINITIONID_LISTTYPEDEFINITIONID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectFieldModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(listTypeDefinitionId);

				list = (List<ObjectField>)QueryUtil.list(
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
	 * Returns the first object field in the ordered set where listTypeDefinitionId = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByListTypeDefinitionId_First(
			long listTypeDefinitionId,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		ObjectField objectField = fetchByListTypeDefinitionId_First(
			listTypeDefinitionId, orderByComparator);

		if (objectField != null) {
			return objectField;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("listTypeDefinitionId=");
		sb.append(listTypeDefinitionId);

		sb.append("}");

		throw new NoSuchObjectFieldException(sb.toString());
	}

	/**
	 * Returns the first object field in the ordered set where listTypeDefinitionId = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByListTypeDefinitionId_First(
		long listTypeDefinitionId,
		OrderByComparator<ObjectField> orderByComparator) {

		List<ObjectField> list = findByListTypeDefinitionId(
			listTypeDefinitionId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object field in the ordered set where listTypeDefinitionId = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByListTypeDefinitionId_Last(
			long listTypeDefinitionId,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		ObjectField objectField = fetchByListTypeDefinitionId_Last(
			listTypeDefinitionId, orderByComparator);

		if (objectField != null) {
			return objectField;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("listTypeDefinitionId=");
		sb.append(listTypeDefinitionId);

		sb.append("}");

		throw new NoSuchObjectFieldException(sb.toString());
	}

	/**
	 * Returns the last object field in the ordered set where listTypeDefinitionId = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByListTypeDefinitionId_Last(
		long listTypeDefinitionId,
		OrderByComparator<ObjectField> orderByComparator) {

		int count = countByListTypeDefinitionId(listTypeDefinitionId);

		if (count == 0) {
			return null;
		}

		List<ObjectField> list = findByListTypeDefinitionId(
			listTypeDefinitionId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object fields before and after the current object field in the ordered set where listTypeDefinitionId = &#63;.
	 *
	 * @param objectFieldId the primary key of the current object field
	 * @param listTypeDefinitionId the list type definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object field
	 * @throws NoSuchObjectFieldException if a object field with the primary key could not be found
	 */
	@Override
	public ObjectField[] findByListTypeDefinitionId_PrevAndNext(
			long objectFieldId, long listTypeDefinitionId,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		ObjectField objectField = findByPrimaryKey(objectFieldId);

		Session session = null;

		try {
			session = openSession();

			ObjectField[] array = new ObjectFieldImpl[3];

			array[0] = getByListTypeDefinitionId_PrevAndNext(
				session, objectField, listTypeDefinitionId, orderByComparator,
				true);

			array[1] = objectField;

			array[2] = getByListTypeDefinitionId_PrevAndNext(
				session, objectField, listTypeDefinitionId, orderByComparator,
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

	protected ObjectField getByListTypeDefinitionId_PrevAndNext(
		Session session, ObjectField objectField, long listTypeDefinitionId,
		OrderByComparator<ObjectField> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_OBJECTFIELD_WHERE);

		sb.append(_FINDER_COLUMN_LISTTYPEDEFINITIONID_LISTTYPEDEFINITIONID_2);

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
			sb.append(ObjectFieldModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(listTypeDefinitionId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(objectField)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectField> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object fields where listTypeDefinitionId = &#63; from the database.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 */
	@Override
	public void removeByListTypeDefinitionId(long listTypeDefinitionId) {
		for (ObjectField objectField :
				findByListTypeDefinitionId(
					listTypeDefinitionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(objectField);
		}
	}

	/**
	 * Returns the number of object fields where listTypeDefinitionId = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @return the number of matching object fields
	 */
	@Override
	public int countByListTypeDefinitionId(long listTypeDefinitionId) {
		FinderPath finderPath = _finderPathCountByListTypeDefinitionId;

		Object[] finderArgs = new Object[] {listTypeDefinitionId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_OBJECTFIELD_WHERE);

			sb.append(
				_FINDER_COLUMN_LISTTYPEDEFINITIONID_LISTTYPEDEFINITIONID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(listTypeDefinitionId);

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
		_FINDER_COLUMN_LISTTYPEDEFINITIONID_LISTTYPEDEFINITIONID_2 =
			"objectField.listTypeDefinitionId = ?";

	private FinderPath _finderPathWithPaginationFindByObjectDefinitionId;
	private FinderPath _finderPathWithoutPaginationFindByObjectDefinitionId;
	private FinderPath _finderPathCountByObjectDefinitionId;

	/**
	 * Returns all the object fields where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object fields
	 */
	@Override
	public List<ObjectField> findByObjectDefinitionId(long objectDefinitionId) {
		return findByObjectDefinitionId(
			objectDefinitionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object fields where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	@Override
	public List<ObjectField> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end) {

		return findByObjectDefinitionId(objectDefinitionId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectField> orderByComparator) {

		return findByObjectDefinitionId(
			objectDefinitionId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectField> orderByComparator,
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

		List<ObjectField> list = null;

		if (useFinderCache) {
			list = (List<ObjectField>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectField objectField : list) {
					if (objectDefinitionId !=
							objectField.getObjectDefinitionId()) {

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

			sb.append(_SQL_SELECT_OBJECTFIELD_WHERE);

			sb.append(_FINDER_COLUMN_OBJECTDEFINITIONID_OBJECTDEFINITIONID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectFieldModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId);

				list = (List<ObjectField>)QueryUtil.list(
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
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByObjectDefinitionId_First(
			long objectDefinitionId,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		ObjectField objectField = fetchByObjectDefinitionId_First(
			objectDefinitionId, orderByComparator);

		if (objectField != null) {
			return objectField;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append("}");

		throw new NoSuchObjectFieldException(sb.toString());
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByObjectDefinitionId_First(
		long objectDefinitionId,
		OrderByComparator<ObjectField> orderByComparator) {

		List<ObjectField> list = findByObjectDefinitionId(
			objectDefinitionId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object field in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByObjectDefinitionId_Last(
			long objectDefinitionId,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		ObjectField objectField = fetchByObjectDefinitionId_Last(
			objectDefinitionId, orderByComparator);

		if (objectField != null) {
			return objectField;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append("}");

		throw new NoSuchObjectFieldException(sb.toString());
	}

	/**
	 * Returns the last object field in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByObjectDefinitionId_Last(
		long objectDefinitionId,
		OrderByComparator<ObjectField> orderByComparator) {

		int count = countByObjectDefinitionId(objectDefinitionId);

		if (count == 0) {
			return null;
		}

		List<ObjectField> list = findByObjectDefinitionId(
			objectDefinitionId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object fields before and after the current object field in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectFieldId the primary key of the current object field
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object field
	 * @throws NoSuchObjectFieldException if a object field with the primary key could not be found
	 */
	@Override
	public ObjectField[] findByObjectDefinitionId_PrevAndNext(
			long objectFieldId, long objectDefinitionId,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		ObjectField objectField = findByPrimaryKey(objectFieldId);

		Session session = null;

		try {
			session = openSession();

			ObjectField[] array = new ObjectFieldImpl[3];

			array[0] = getByObjectDefinitionId_PrevAndNext(
				session, objectField, objectDefinitionId, orderByComparator,
				true);

			array[1] = objectField;

			array[2] = getByObjectDefinitionId_PrevAndNext(
				session, objectField, objectDefinitionId, orderByComparator,
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

	protected ObjectField getByObjectDefinitionId_PrevAndNext(
		Session session, ObjectField objectField, long objectDefinitionId,
		OrderByComparator<ObjectField> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_OBJECTFIELD_WHERE);

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
			sb.append(ObjectFieldModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(objectDefinitionId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(objectField)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectField> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object fields where objectDefinitionId = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 */
	@Override
	public void removeByObjectDefinitionId(long objectDefinitionId) {
		for (ObjectField objectField :
				findByObjectDefinitionId(
					objectDefinitionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(objectField);
		}
	}

	/**
	 * Returns the number of object fields where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object fields
	 */
	@Override
	public int countByObjectDefinitionId(long objectDefinitionId) {
		FinderPath finderPath = _finderPathCountByObjectDefinitionId;

		Object[] finderArgs = new Object[] {objectDefinitionId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_OBJECTFIELD_WHERE);

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
			"objectField.objectDefinitionId = ?";

	private FinderPath _finderPathWithPaginationFindByC_U;
	private FinderPath _finderPathWithoutPaginationFindByC_U;
	private FinderPath _finderPathCountByC_U;

	/**
	 * Returns all the object fields where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the matching object fields
	 */
	@Override
	public List<ObjectField> findByC_U(long companyId, long userId) {
		return findByC_U(
			companyId, userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object fields where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	@Override
	public List<ObjectField> findByC_U(
		long companyId, long userId, int start, int end) {

		return findByC_U(companyId, userId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object fields where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<ObjectField> orderByComparator) {

		return findByC_U(
			companyId, userId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object fields where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<ObjectField> orderByComparator,
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

		List<ObjectField> list = null;

		if (useFinderCache) {
			list = (List<ObjectField>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectField objectField : list) {
					if ((companyId != objectField.getCompanyId()) ||
						(userId != objectField.getUserId())) {

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

			sb.append(_SQL_SELECT_OBJECTFIELD_WHERE);

			sb.append(_FINDER_COLUMN_C_U_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_U_USERID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectFieldModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(userId);

				list = (List<ObjectField>)QueryUtil.list(
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
	 * Returns the first object field in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByC_U_First(
			long companyId, long userId,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		ObjectField objectField = fetchByC_U_First(
			companyId, userId, orderByComparator);

		if (objectField != null) {
			return objectField;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append("}");

		throw new NoSuchObjectFieldException(sb.toString());
	}

	/**
	 * Returns the first object field in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByC_U_First(
		long companyId, long userId,
		OrderByComparator<ObjectField> orderByComparator) {

		List<ObjectField> list = findByC_U(
			companyId, userId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object field in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByC_U_Last(
			long companyId, long userId,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		ObjectField objectField = fetchByC_U_Last(
			companyId, userId, orderByComparator);

		if (objectField != null) {
			return objectField;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append("}");

		throw new NoSuchObjectFieldException(sb.toString());
	}

	/**
	 * Returns the last object field in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByC_U_Last(
		long companyId, long userId,
		OrderByComparator<ObjectField> orderByComparator) {

		int count = countByC_U(companyId, userId);

		if (count == 0) {
			return null;
		}

		List<ObjectField> list = findByC_U(
			companyId, userId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object fields before and after the current object field in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param objectFieldId the primary key of the current object field
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object field
	 * @throws NoSuchObjectFieldException if a object field with the primary key could not be found
	 */
	@Override
	public ObjectField[] findByC_U_PrevAndNext(
			long objectFieldId, long companyId, long userId,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		ObjectField objectField = findByPrimaryKey(objectFieldId);

		Session session = null;

		try {
			session = openSession();

			ObjectField[] array = new ObjectFieldImpl[3];

			array[0] = getByC_U_PrevAndNext(
				session, objectField, companyId, userId, orderByComparator,
				true);

			array[1] = objectField;

			array[2] = getByC_U_PrevAndNext(
				session, objectField, companyId, userId, orderByComparator,
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

	protected ObjectField getByC_U_PrevAndNext(
		Session session, ObjectField objectField, long companyId, long userId,
		OrderByComparator<ObjectField> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_OBJECTFIELD_WHERE);

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
			sb.append(ObjectFieldModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(objectField)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectField> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object fields where companyId = &#63; and userId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 */
	@Override
	public void removeByC_U(long companyId, long userId) {
		for (ObjectField objectField :
				findByC_U(
					companyId, userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(objectField);
		}
	}

	/**
	 * Returns the number of object fields where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching object fields
	 */
	@Override
	public int countByC_U(long companyId, long userId) {
		FinderPath finderPath = _finderPathCountByC_U;

		Object[] finderArgs = new Object[] {companyId, userId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_OBJECTFIELD_WHERE);

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
		"objectField.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_U_USERID_2 =
		"objectField.userId = ?";

	private FinderPath _finderPathWithPaginationFindByLTDI_S;
	private FinderPath _finderPathWithoutPaginationFindByLTDI_S;
	private FinderPath _finderPathCountByLTDI_S;

	/**
	 * Returns all the object fields where listTypeDefinitionId = &#63; and state = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param state the state
	 * @return the matching object fields
	 */
	@Override
	public List<ObjectField> findByLTDI_S(
		long listTypeDefinitionId, boolean state) {

		return findByLTDI_S(
			listTypeDefinitionId, state, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the object fields where listTypeDefinitionId = &#63; and state = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param state the state
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	@Override
	public List<ObjectField> findByLTDI_S(
		long listTypeDefinitionId, boolean state, int start, int end) {

		return findByLTDI_S(listTypeDefinitionId, state, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object fields where listTypeDefinitionId = &#63; and state = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param state the state
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByLTDI_S(
		long listTypeDefinitionId, boolean state, int start, int end,
		OrderByComparator<ObjectField> orderByComparator) {

		return findByLTDI_S(
			listTypeDefinitionId, state, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object fields where listTypeDefinitionId = &#63; and state = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param state the state
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByLTDI_S(
		long listTypeDefinitionId, boolean state, int start, int end,
		OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByLTDI_S;
				finderArgs = new Object[] {listTypeDefinitionId, state};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByLTDI_S;
			finderArgs = new Object[] {
				listTypeDefinitionId, state, start, end, orderByComparator
			};
		}

		List<ObjectField> list = null;

		if (useFinderCache) {
			list = (List<ObjectField>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectField objectField : list) {
					if ((listTypeDefinitionId !=
							objectField.getListTypeDefinitionId()) ||
						(state != objectField.isState())) {

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

			sb.append(_SQL_SELECT_OBJECTFIELD_WHERE);

			sb.append(_FINDER_COLUMN_LTDI_S_LISTTYPEDEFINITIONID_2);

			sb.append(_FINDER_COLUMN_LTDI_S_STATE_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectFieldModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(listTypeDefinitionId);

				queryPos.add(state);

				list = (List<ObjectField>)QueryUtil.list(
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
	 * Returns the first object field in the ordered set where listTypeDefinitionId = &#63; and state = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param state the state
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByLTDI_S_First(
			long listTypeDefinitionId, boolean state,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		ObjectField objectField = fetchByLTDI_S_First(
			listTypeDefinitionId, state, orderByComparator);

		if (objectField != null) {
			return objectField;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("listTypeDefinitionId=");
		sb.append(listTypeDefinitionId);

		sb.append(", state=");
		sb.append(state);

		sb.append("}");

		throw new NoSuchObjectFieldException(sb.toString());
	}

	/**
	 * Returns the first object field in the ordered set where listTypeDefinitionId = &#63; and state = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param state the state
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByLTDI_S_First(
		long listTypeDefinitionId, boolean state,
		OrderByComparator<ObjectField> orderByComparator) {

		List<ObjectField> list = findByLTDI_S(
			listTypeDefinitionId, state, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object field in the ordered set where listTypeDefinitionId = &#63; and state = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param state the state
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByLTDI_S_Last(
			long listTypeDefinitionId, boolean state,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		ObjectField objectField = fetchByLTDI_S_Last(
			listTypeDefinitionId, state, orderByComparator);

		if (objectField != null) {
			return objectField;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("listTypeDefinitionId=");
		sb.append(listTypeDefinitionId);

		sb.append(", state=");
		sb.append(state);

		sb.append("}");

		throw new NoSuchObjectFieldException(sb.toString());
	}

	/**
	 * Returns the last object field in the ordered set where listTypeDefinitionId = &#63; and state = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param state the state
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByLTDI_S_Last(
		long listTypeDefinitionId, boolean state,
		OrderByComparator<ObjectField> orderByComparator) {

		int count = countByLTDI_S(listTypeDefinitionId, state);

		if (count == 0) {
			return null;
		}

		List<ObjectField> list = findByLTDI_S(
			listTypeDefinitionId, state, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object fields before and after the current object field in the ordered set where listTypeDefinitionId = &#63; and state = &#63;.
	 *
	 * @param objectFieldId the primary key of the current object field
	 * @param listTypeDefinitionId the list type definition ID
	 * @param state the state
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object field
	 * @throws NoSuchObjectFieldException if a object field with the primary key could not be found
	 */
	@Override
	public ObjectField[] findByLTDI_S_PrevAndNext(
			long objectFieldId, long listTypeDefinitionId, boolean state,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		ObjectField objectField = findByPrimaryKey(objectFieldId);

		Session session = null;

		try {
			session = openSession();

			ObjectField[] array = new ObjectFieldImpl[3];

			array[0] = getByLTDI_S_PrevAndNext(
				session, objectField, listTypeDefinitionId, state,
				orderByComparator, true);

			array[1] = objectField;

			array[2] = getByLTDI_S_PrevAndNext(
				session, objectField, listTypeDefinitionId, state,
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

	protected ObjectField getByLTDI_S_PrevAndNext(
		Session session, ObjectField objectField, long listTypeDefinitionId,
		boolean state, OrderByComparator<ObjectField> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTFIELD_WHERE);

		sb.append(_FINDER_COLUMN_LTDI_S_LISTTYPEDEFINITIONID_2);

		sb.append(_FINDER_COLUMN_LTDI_S_STATE_2);

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
			sb.append(ObjectFieldModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(listTypeDefinitionId);

		queryPos.add(state);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(objectField)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectField> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object fields where listTypeDefinitionId = &#63; and state = &#63; from the database.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param state the state
	 */
	@Override
	public void removeByLTDI_S(long listTypeDefinitionId, boolean state) {
		for (ObjectField objectField :
				findByLTDI_S(
					listTypeDefinitionId, state, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(objectField);
		}
	}

	/**
	 * Returns the number of object fields where listTypeDefinitionId = &#63; and state = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param state the state
	 * @return the number of matching object fields
	 */
	@Override
	public int countByLTDI_S(long listTypeDefinitionId, boolean state) {
		FinderPath finderPath = _finderPathCountByLTDI_S;

		Object[] finderArgs = new Object[] {listTypeDefinitionId, state};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_OBJECTFIELD_WHERE);

			sb.append(_FINDER_COLUMN_LTDI_S_LISTTYPEDEFINITIONID_2);

			sb.append(_FINDER_COLUMN_LTDI_S_STATE_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(listTypeDefinitionId);

				queryPos.add(state);

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

	private static final String _FINDER_COLUMN_LTDI_S_LISTTYPEDEFINITIONID_2 =
		"objectField.listTypeDefinitionId = ? AND ";

	private static final String _FINDER_COLUMN_LTDI_S_STATE_2 =
		"objectField.state = ?";

	private FinderPath _finderPathWithPaginationFindByODI_BT;
	private FinderPath _finderPathWithoutPaginationFindByODI_BT;
	private FinderPath _finderPathCountByODI_BT;

	/**
	 * Returns all the object fields where objectDefinitionId = &#63; and businessType = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param businessType the business type
	 * @return the matching object fields
	 */
	@Override
	public List<ObjectField> findByODI_BT(
		long objectDefinitionId, String businessType) {

		return findByODI_BT(
			objectDefinitionId, businessType, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object fields where objectDefinitionId = &#63; and businessType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param businessType the business type
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	@Override
	public List<ObjectField> findByODI_BT(
		long objectDefinitionId, String businessType, int start, int end) {

		return findByODI_BT(objectDefinitionId, businessType, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and businessType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param businessType the business type
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByODI_BT(
		long objectDefinitionId, String businessType, int start, int end,
		OrderByComparator<ObjectField> orderByComparator) {

		return findByODI_BT(
			objectDefinitionId, businessType, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and businessType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param businessType the business type
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByODI_BT(
		long objectDefinitionId, String businessType, int start, int end,
		OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		businessType = Objects.toString(businessType, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByODI_BT;
				finderArgs = new Object[] {objectDefinitionId, businessType};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByODI_BT;
			finderArgs = new Object[] {
				objectDefinitionId, businessType, start, end, orderByComparator
			};
		}

		List<ObjectField> list = null;

		if (useFinderCache) {
			list = (List<ObjectField>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectField objectField : list) {
					if ((objectDefinitionId !=
							objectField.getObjectDefinitionId()) ||
						!businessType.equals(objectField.getBusinessType())) {

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

			sb.append(_SQL_SELECT_OBJECTFIELD_WHERE);

			sb.append(_FINDER_COLUMN_ODI_BT_OBJECTDEFINITIONID_2);

			boolean bindBusinessType = false;

			if (businessType.isEmpty()) {
				sb.append(_FINDER_COLUMN_ODI_BT_BUSINESSTYPE_3);
			}
			else {
				bindBusinessType = true;

				sb.append(_FINDER_COLUMN_ODI_BT_BUSINESSTYPE_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectFieldModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId);

				if (bindBusinessType) {
					queryPos.add(businessType);
				}

				list = (List<ObjectField>)QueryUtil.list(
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
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and businessType = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param businessType the business type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByODI_BT_First(
			long objectDefinitionId, String businessType,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		ObjectField objectField = fetchByODI_BT_First(
			objectDefinitionId, businessType, orderByComparator);

		if (objectField != null) {
			return objectField;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append(", businessType=");
		sb.append(businessType);

		sb.append("}");

		throw new NoSuchObjectFieldException(sb.toString());
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and businessType = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param businessType the business type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByODI_BT_First(
		long objectDefinitionId, String businessType,
		OrderByComparator<ObjectField> orderByComparator) {

		List<ObjectField> list = findByODI_BT(
			objectDefinitionId, businessType, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object field in the ordered set where objectDefinitionId = &#63; and businessType = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param businessType the business type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByODI_BT_Last(
			long objectDefinitionId, String businessType,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		ObjectField objectField = fetchByODI_BT_Last(
			objectDefinitionId, businessType, orderByComparator);

		if (objectField != null) {
			return objectField;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append(", businessType=");
		sb.append(businessType);

		sb.append("}");

		throw new NoSuchObjectFieldException(sb.toString());
	}

	/**
	 * Returns the last object field in the ordered set where objectDefinitionId = &#63; and businessType = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param businessType the business type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByODI_BT_Last(
		long objectDefinitionId, String businessType,
		OrderByComparator<ObjectField> orderByComparator) {

		int count = countByODI_BT(objectDefinitionId, businessType);

		if (count == 0) {
			return null;
		}

		List<ObjectField> list = findByODI_BT(
			objectDefinitionId, businessType, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object fields before and after the current object field in the ordered set where objectDefinitionId = &#63; and businessType = &#63;.
	 *
	 * @param objectFieldId the primary key of the current object field
	 * @param objectDefinitionId the object definition ID
	 * @param businessType the business type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object field
	 * @throws NoSuchObjectFieldException if a object field with the primary key could not be found
	 */
	@Override
	public ObjectField[] findByODI_BT_PrevAndNext(
			long objectFieldId, long objectDefinitionId, String businessType,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		businessType = Objects.toString(businessType, "");

		ObjectField objectField = findByPrimaryKey(objectFieldId);

		Session session = null;

		try {
			session = openSession();

			ObjectField[] array = new ObjectFieldImpl[3];

			array[0] = getByODI_BT_PrevAndNext(
				session, objectField, objectDefinitionId, businessType,
				orderByComparator, true);

			array[1] = objectField;

			array[2] = getByODI_BT_PrevAndNext(
				session, objectField, objectDefinitionId, businessType,
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

	protected ObjectField getByODI_BT_PrevAndNext(
		Session session, ObjectField objectField, long objectDefinitionId,
		String businessType, OrderByComparator<ObjectField> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTFIELD_WHERE);

		sb.append(_FINDER_COLUMN_ODI_BT_OBJECTDEFINITIONID_2);

		boolean bindBusinessType = false;

		if (businessType.isEmpty()) {
			sb.append(_FINDER_COLUMN_ODI_BT_BUSINESSTYPE_3);
		}
		else {
			bindBusinessType = true;

			sb.append(_FINDER_COLUMN_ODI_BT_BUSINESSTYPE_2);
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
			sb.append(ObjectFieldModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(objectDefinitionId);

		if (bindBusinessType) {
			queryPos.add(businessType);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(objectField)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectField> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object fields where objectDefinitionId = &#63; and businessType = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param businessType the business type
	 */
	@Override
	public void removeByODI_BT(long objectDefinitionId, String businessType) {
		for (ObjectField objectField :
				findByODI_BT(
					objectDefinitionId, businessType, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(objectField);
		}
	}

	/**
	 * Returns the number of object fields where objectDefinitionId = &#63; and businessType = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param businessType the business type
	 * @return the number of matching object fields
	 */
	@Override
	public int countByODI_BT(long objectDefinitionId, String businessType) {
		businessType = Objects.toString(businessType, "");

		FinderPath finderPath = _finderPathCountByODI_BT;

		Object[] finderArgs = new Object[] {objectDefinitionId, businessType};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_OBJECTFIELD_WHERE);

			sb.append(_FINDER_COLUMN_ODI_BT_OBJECTDEFINITIONID_2);

			boolean bindBusinessType = false;

			if (businessType.isEmpty()) {
				sb.append(_FINDER_COLUMN_ODI_BT_BUSINESSTYPE_3);
			}
			else {
				bindBusinessType = true;

				sb.append(_FINDER_COLUMN_ODI_BT_BUSINESSTYPE_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId);

				if (bindBusinessType) {
					queryPos.add(businessType);
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

	private static final String _FINDER_COLUMN_ODI_BT_OBJECTDEFINITIONID_2 =
		"objectField.objectDefinitionId = ? AND ";

	private static final String _FINDER_COLUMN_ODI_BT_BUSINESSTYPE_2 =
		"objectField.businessType = ?";

	private static final String _FINDER_COLUMN_ODI_BT_BUSINESSTYPE_3 =
		"(objectField.businessType IS NULL OR objectField.businessType = '')";

	private FinderPath _finderPathWithPaginationFindByODI_DTN;
	private FinderPath _finderPathWithoutPaginationFindByODI_DTN;
	private FinderPath _finderPathCountByODI_DTN;

	/**
	 * Returns all the object fields where objectDefinitionId = &#63; and dbTableName = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbTableName the db table name
	 * @return the matching object fields
	 */
	@Override
	public List<ObjectField> findByODI_DTN(
		long objectDefinitionId, String dbTableName) {

		return findByODI_DTN(
			objectDefinitionId, dbTableName, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object fields where objectDefinitionId = &#63; and dbTableName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbTableName the db table name
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	@Override
	public List<ObjectField> findByODI_DTN(
		long objectDefinitionId, String dbTableName, int start, int end) {

		return findByODI_DTN(objectDefinitionId, dbTableName, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and dbTableName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbTableName the db table name
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByODI_DTN(
		long objectDefinitionId, String dbTableName, int start, int end,
		OrderByComparator<ObjectField> orderByComparator) {

		return findByODI_DTN(
			objectDefinitionId, dbTableName, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and dbTableName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbTableName the db table name
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByODI_DTN(
		long objectDefinitionId, String dbTableName, int start, int end,
		OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		dbTableName = Objects.toString(dbTableName, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByODI_DTN;
				finderArgs = new Object[] {objectDefinitionId, dbTableName};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByODI_DTN;
			finderArgs = new Object[] {
				objectDefinitionId, dbTableName, start, end, orderByComparator
			};
		}

		List<ObjectField> list = null;

		if (useFinderCache) {
			list = (List<ObjectField>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectField objectField : list) {
					if ((objectDefinitionId !=
							objectField.getObjectDefinitionId()) ||
						!dbTableName.equals(objectField.getDBTableName())) {

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

			sb.append(_SQL_SELECT_OBJECTFIELD_WHERE);

			sb.append(_FINDER_COLUMN_ODI_DTN_OBJECTDEFINITIONID_2);

			boolean bindDBTableName = false;

			if (dbTableName.isEmpty()) {
				sb.append(_FINDER_COLUMN_ODI_DTN_DBTABLENAME_3);
			}
			else {
				bindDBTableName = true;

				sb.append(_FINDER_COLUMN_ODI_DTN_DBTABLENAME_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectFieldModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId);

				if (bindDBTableName) {
					queryPos.add(dbTableName);
				}

				list = (List<ObjectField>)QueryUtil.list(
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
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and dbTableName = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbTableName the db table name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByODI_DTN_First(
			long objectDefinitionId, String dbTableName,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		ObjectField objectField = fetchByODI_DTN_First(
			objectDefinitionId, dbTableName, orderByComparator);

		if (objectField != null) {
			return objectField;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append(", dbTableName=");
		sb.append(dbTableName);

		sb.append("}");

		throw new NoSuchObjectFieldException(sb.toString());
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and dbTableName = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbTableName the db table name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByODI_DTN_First(
		long objectDefinitionId, String dbTableName,
		OrderByComparator<ObjectField> orderByComparator) {

		List<ObjectField> list = findByODI_DTN(
			objectDefinitionId, dbTableName, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object field in the ordered set where objectDefinitionId = &#63; and dbTableName = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbTableName the db table name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByODI_DTN_Last(
			long objectDefinitionId, String dbTableName,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		ObjectField objectField = fetchByODI_DTN_Last(
			objectDefinitionId, dbTableName, orderByComparator);

		if (objectField != null) {
			return objectField;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append(", dbTableName=");
		sb.append(dbTableName);

		sb.append("}");

		throw new NoSuchObjectFieldException(sb.toString());
	}

	/**
	 * Returns the last object field in the ordered set where objectDefinitionId = &#63; and dbTableName = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbTableName the db table name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByODI_DTN_Last(
		long objectDefinitionId, String dbTableName,
		OrderByComparator<ObjectField> orderByComparator) {

		int count = countByODI_DTN(objectDefinitionId, dbTableName);

		if (count == 0) {
			return null;
		}

		List<ObjectField> list = findByODI_DTN(
			objectDefinitionId, dbTableName, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object fields before and after the current object field in the ordered set where objectDefinitionId = &#63; and dbTableName = &#63;.
	 *
	 * @param objectFieldId the primary key of the current object field
	 * @param objectDefinitionId the object definition ID
	 * @param dbTableName the db table name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object field
	 * @throws NoSuchObjectFieldException if a object field with the primary key could not be found
	 */
	@Override
	public ObjectField[] findByODI_DTN_PrevAndNext(
			long objectFieldId, long objectDefinitionId, String dbTableName,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		dbTableName = Objects.toString(dbTableName, "");

		ObjectField objectField = findByPrimaryKey(objectFieldId);

		Session session = null;

		try {
			session = openSession();

			ObjectField[] array = new ObjectFieldImpl[3];

			array[0] = getByODI_DTN_PrevAndNext(
				session, objectField, objectDefinitionId, dbTableName,
				orderByComparator, true);

			array[1] = objectField;

			array[2] = getByODI_DTN_PrevAndNext(
				session, objectField, objectDefinitionId, dbTableName,
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

	protected ObjectField getByODI_DTN_PrevAndNext(
		Session session, ObjectField objectField, long objectDefinitionId,
		String dbTableName, OrderByComparator<ObjectField> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTFIELD_WHERE);

		sb.append(_FINDER_COLUMN_ODI_DTN_OBJECTDEFINITIONID_2);

		boolean bindDBTableName = false;

		if (dbTableName.isEmpty()) {
			sb.append(_FINDER_COLUMN_ODI_DTN_DBTABLENAME_3);
		}
		else {
			bindDBTableName = true;

			sb.append(_FINDER_COLUMN_ODI_DTN_DBTABLENAME_2);
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
			sb.append(ObjectFieldModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(objectDefinitionId);

		if (bindDBTableName) {
			queryPos.add(dbTableName);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(objectField)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectField> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object fields where objectDefinitionId = &#63; and dbTableName = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbTableName the db table name
	 */
	@Override
	public void removeByODI_DTN(long objectDefinitionId, String dbTableName) {
		for (ObjectField objectField :
				findByODI_DTN(
					objectDefinitionId, dbTableName, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(objectField);
		}
	}

	/**
	 * Returns the number of object fields where objectDefinitionId = &#63; and dbTableName = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbTableName the db table name
	 * @return the number of matching object fields
	 */
	@Override
	public int countByODI_DTN(long objectDefinitionId, String dbTableName) {
		dbTableName = Objects.toString(dbTableName, "");

		FinderPath finderPath = _finderPathCountByODI_DTN;

		Object[] finderArgs = new Object[] {objectDefinitionId, dbTableName};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_OBJECTFIELD_WHERE);

			sb.append(_FINDER_COLUMN_ODI_DTN_OBJECTDEFINITIONID_2);

			boolean bindDBTableName = false;

			if (dbTableName.isEmpty()) {
				sb.append(_FINDER_COLUMN_ODI_DTN_DBTABLENAME_3);
			}
			else {
				bindDBTableName = true;

				sb.append(_FINDER_COLUMN_ODI_DTN_DBTABLENAME_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId);

				if (bindDBTableName) {
					queryPos.add(dbTableName);
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

	private static final String _FINDER_COLUMN_ODI_DTN_OBJECTDEFINITIONID_2 =
		"objectField.objectDefinitionId = ? AND ";

	private static final String _FINDER_COLUMN_ODI_DTN_DBTABLENAME_2 =
		"objectField.dbTableName = ?";

	private static final String _FINDER_COLUMN_ODI_DTN_DBTABLENAME_3 =
		"(objectField.dbTableName IS NULL OR objectField.dbTableName = '')";

	private FinderPath _finderPathWithPaginationFindByODI_I;
	private FinderPath _finderPathWithoutPaginationFindByODI_I;
	private FinderPath _finderPathCountByODI_I;

	/**
	 * Returns all the object fields where objectDefinitionId = &#63; and indexed = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param indexed the indexed
	 * @return the matching object fields
	 */
	@Override
	public List<ObjectField> findByODI_I(
		long objectDefinitionId, boolean indexed) {

		return findByODI_I(
			objectDefinitionId, indexed, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the object fields where objectDefinitionId = &#63; and indexed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param indexed the indexed
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	@Override
	public List<ObjectField> findByODI_I(
		long objectDefinitionId, boolean indexed, int start, int end) {

		return findByODI_I(objectDefinitionId, indexed, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and indexed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param indexed the indexed
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByODI_I(
		long objectDefinitionId, boolean indexed, int start, int end,
		OrderByComparator<ObjectField> orderByComparator) {

		return findByODI_I(
			objectDefinitionId, indexed, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and indexed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param indexed the indexed
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByODI_I(
		long objectDefinitionId, boolean indexed, int start, int end,
		OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByODI_I;
				finderArgs = new Object[] {objectDefinitionId, indexed};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByODI_I;
			finderArgs = new Object[] {
				objectDefinitionId, indexed, start, end, orderByComparator
			};
		}

		List<ObjectField> list = null;

		if (useFinderCache) {
			list = (List<ObjectField>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectField objectField : list) {
					if ((objectDefinitionId !=
							objectField.getObjectDefinitionId()) ||
						(indexed != objectField.isIndexed())) {

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

			sb.append(_SQL_SELECT_OBJECTFIELD_WHERE);

			sb.append(_FINDER_COLUMN_ODI_I_OBJECTDEFINITIONID_2);

			sb.append(_FINDER_COLUMN_ODI_I_INDEXED_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectFieldModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId);

				queryPos.add(indexed);

				list = (List<ObjectField>)QueryUtil.list(
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
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and indexed = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param indexed the indexed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByODI_I_First(
			long objectDefinitionId, boolean indexed,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		ObjectField objectField = fetchByODI_I_First(
			objectDefinitionId, indexed, orderByComparator);

		if (objectField != null) {
			return objectField;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append(", indexed=");
		sb.append(indexed);

		sb.append("}");

		throw new NoSuchObjectFieldException(sb.toString());
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and indexed = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param indexed the indexed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByODI_I_First(
		long objectDefinitionId, boolean indexed,
		OrderByComparator<ObjectField> orderByComparator) {

		List<ObjectField> list = findByODI_I(
			objectDefinitionId, indexed, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object field in the ordered set where objectDefinitionId = &#63; and indexed = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param indexed the indexed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByODI_I_Last(
			long objectDefinitionId, boolean indexed,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		ObjectField objectField = fetchByODI_I_Last(
			objectDefinitionId, indexed, orderByComparator);

		if (objectField != null) {
			return objectField;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append(", indexed=");
		sb.append(indexed);

		sb.append("}");

		throw new NoSuchObjectFieldException(sb.toString());
	}

	/**
	 * Returns the last object field in the ordered set where objectDefinitionId = &#63; and indexed = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param indexed the indexed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByODI_I_Last(
		long objectDefinitionId, boolean indexed,
		OrderByComparator<ObjectField> orderByComparator) {

		int count = countByODI_I(objectDefinitionId, indexed);

		if (count == 0) {
			return null;
		}

		List<ObjectField> list = findByODI_I(
			objectDefinitionId, indexed, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object fields before and after the current object field in the ordered set where objectDefinitionId = &#63; and indexed = &#63;.
	 *
	 * @param objectFieldId the primary key of the current object field
	 * @param objectDefinitionId the object definition ID
	 * @param indexed the indexed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object field
	 * @throws NoSuchObjectFieldException if a object field with the primary key could not be found
	 */
	@Override
	public ObjectField[] findByODI_I_PrevAndNext(
			long objectFieldId, long objectDefinitionId, boolean indexed,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		ObjectField objectField = findByPrimaryKey(objectFieldId);

		Session session = null;

		try {
			session = openSession();

			ObjectField[] array = new ObjectFieldImpl[3];

			array[0] = getByODI_I_PrevAndNext(
				session, objectField, objectDefinitionId, indexed,
				orderByComparator, true);

			array[1] = objectField;

			array[2] = getByODI_I_PrevAndNext(
				session, objectField, objectDefinitionId, indexed,
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

	protected ObjectField getByODI_I_PrevAndNext(
		Session session, ObjectField objectField, long objectDefinitionId,
		boolean indexed, OrderByComparator<ObjectField> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTFIELD_WHERE);

		sb.append(_FINDER_COLUMN_ODI_I_OBJECTDEFINITIONID_2);

		sb.append(_FINDER_COLUMN_ODI_I_INDEXED_2);

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
			sb.append(ObjectFieldModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(objectDefinitionId);

		queryPos.add(indexed);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(objectField)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectField> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object fields where objectDefinitionId = &#63; and indexed = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param indexed the indexed
	 */
	@Override
	public void removeByODI_I(long objectDefinitionId, boolean indexed) {
		for (ObjectField objectField :
				findByODI_I(
					objectDefinitionId, indexed, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(objectField);
		}
	}

	/**
	 * Returns the number of object fields where objectDefinitionId = &#63; and indexed = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param indexed the indexed
	 * @return the number of matching object fields
	 */
	@Override
	public int countByODI_I(long objectDefinitionId, boolean indexed) {
		FinderPath finderPath = _finderPathCountByODI_I;

		Object[] finderArgs = new Object[] {objectDefinitionId, indexed};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_OBJECTFIELD_WHERE);

			sb.append(_FINDER_COLUMN_ODI_I_OBJECTDEFINITIONID_2);

			sb.append(_FINDER_COLUMN_ODI_I_INDEXED_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId);

				queryPos.add(indexed);

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

	private static final String _FINDER_COLUMN_ODI_I_OBJECTDEFINITIONID_2 =
		"objectField.objectDefinitionId = ? AND ";

	private static final String _FINDER_COLUMN_ODI_I_INDEXED_2 =
		"objectField.indexed = ?";

	private FinderPath _finderPathWithPaginationFindByODI_L;
	private FinderPath _finderPathWithoutPaginationFindByODI_L;
	private FinderPath _finderPathCountByODI_L;

	/**
	 * Returns all the object fields where objectDefinitionId = &#63; and localized = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @return the matching object fields
	 */
	@Override
	public List<ObjectField> findByODI_L(
		long objectDefinitionId, boolean localized) {

		return findByODI_L(
			objectDefinitionId, localized, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the object fields where objectDefinitionId = &#63; and localized = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	@Override
	public List<ObjectField> findByODI_L(
		long objectDefinitionId, boolean localized, int start, int end) {

		return findByODI_L(objectDefinitionId, localized, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and localized = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByODI_L(
		long objectDefinitionId, boolean localized, int start, int end,
		OrderByComparator<ObjectField> orderByComparator) {

		return findByODI_L(
			objectDefinitionId, localized, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and localized = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByODI_L(
		long objectDefinitionId, boolean localized, int start, int end,
		OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByODI_L;
				finderArgs = new Object[] {objectDefinitionId, localized};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByODI_L;
			finderArgs = new Object[] {
				objectDefinitionId, localized, start, end, orderByComparator
			};
		}

		List<ObjectField> list = null;

		if (useFinderCache) {
			list = (List<ObjectField>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectField objectField : list) {
					if ((objectDefinitionId !=
							objectField.getObjectDefinitionId()) ||
						(localized != objectField.isLocalized())) {

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

			sb.append(_SQL_SELECT_OBJECTFIELD_WHERE);

			sb.append(_FINDER_COLUMN_ODI_L_OBJECTDEFINITIONID_2);

			sb.append(_FINDER_COLUMN_ODI_L_LOCALIZED_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectFieldModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId);

				queryPos.add(localized);

				list = (List<ObjectField>)QueryUtil.list(
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
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and localized = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByODI_L_First(
			long objectDefinitionId, boolean localized,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		ObjectField objectField = fetchByODI_L_First(
			objectDefinitionId, localized, orderByComparator);

		if (objectField != null) {
			return objectField;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append(", localized=");
		sb.append(localized);

		sb.append("}");

		throw new NoSuchObjectFieldException(sb.toString());
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and localized = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByODI_L_First(
		long objectDefinitionId, boolean localized,
		OrderByComparator<ObjectField> orderByComparator) {

		List<ObjectField> list = findByODI_L(
			objectDefinitionId, localized, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object field in the ordered set where objectDefinitionId = &#63; and localized = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByODI_L_Last(
			long objectDefinitionId, boolean localized,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		ObjectField objectField = fetchByODI_L_Last(
			objectDefinitionId, localized, orderByComparator);

		if (objectField != null) {
			return objectField;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append(", localized=");
		sb.append(localized);

		sb.append("}");

		throw new NoSuchObjectFieldException(sb.toString());
	}

	/**
	 * Returns the last object field in the ordered set where objectDefinitionId = &#63; and localized = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByODI_L_Last(
		long objectDefinitionId, boolean localized,
		OrderByComparator<ObjectField> orderByComparator) {

		int count = countByODI_L(objectDefinitionId, localized);

		if (count == 0) {
			return null;
		}

		List<ObjectField> list = findByODI_L(
			objectDefinitionId, localized, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object fields before and after the current object field in the ordered set where objectDefinitionId = &#63; and localized = &#63;.
	 *
	 * @param objectFieldId the primary key of the current object field
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object field
	 * @throws NoSuchObjectFieldException if a object field with the primary key could not be found
	 */
	@Override
	public ObjectField[] findByODI_L_PrevAndNext(
			long objectFieldId, long objectDefinitionId, boolean localized,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		ObjectField objectField = findByPrimaryKey(objectFieldId);

		Session session = null;

		try {
			session = openSession();

			ObjectField[] array = new ObjectFieldImpl[3];

			array[0] = getByODI_L_PrevAndNext(
				session, objectField, objectDefinitionId, localized,
				orderByComparator, true);

			array[1] = objectField;

			array[2] = getByODI_L_PrevAndNext(
				session, objectField, objectDefinitionId, localized,
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

	protected ObjectField getByODI_L_PrevAndNext(
		Session session, ObjectField objectField, long objectDefinitionId,
		boolean localized, OrderByComparator<ObjectField> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTFIELD_WHERE);

		sb.append(_FINDER_COLUMN_ODI_L_OBJECTDEFINITIONID_2);

		sb.append(_FINDER_COLUMN_ODI_L_LOCALIZED_2);

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
			sb.append(ObjectFieldModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(objectDefinitionId);

		queryPos.add(localized);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(objectField)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectField> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object fields where objectDefinitionId = &#63; and localized = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 */
	@Override
	public void removeByODI_L(long objectDefinitionId, boolean localized) {
		for (ObjectField objectField :
				findByODI_L(
					objectDefinitionId, localized, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(objectField);
		}
	}

	/**
	 * Returns the number of object fields where objectDefinitionId = &#63; and localized = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @return the number of matching object fields
	 */
	@Override
	public int countByODI_L(long objectDefinitionId, boolean localized) {
		FinderPath finderPath = _finderPathCountByODI_L;

		Object[] finderArgs = new Object[] {objectDefinitionId, localized};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_OBJECTFIELD_WHERE);

			sb.append(_FINDER_COLUMN_ODI_L_OBJECTDEFINITIONID_2);

			sb.append(_FINDER_COLUMN_ODI_L_LOCALIZED_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId);

				queryPos.add(localized);

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

	private static final String _FINDER_COLUMN_ODI_L_OBJECTDEFINITIONID_2 =
		"objectField.objectDefinitionId = ? AND ";

	private static final String _FINDER_COLUMN_ODI_L_LOCALIZED_2 =
		"objectField.localized = ?";

	private FinderPath _finderPathFetchByODI_N;

	/**
	 * Returns the object field where objectDefinitionId = &#63; and name = &#63; or throws a <code>NoSuchObjectFieldException</code> if it could not be found.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByODI_N(long objectDefinitionId, String name)
		throws NoSuchObjectFieldException {

		ObjectField objectField = fetchByODI_N(objectDefinitionId, name);

		if (objectField == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("objectDefinitionId=");
			sb.append(objectDefinitionId);

			sb.append(", name=");
			sb.append(name);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchObjectFieldException(sb.toString());
		}

		return objectField;
	}

	/**
	 * Returns the object field where objectDefinitionId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByODI_N(long objectDefinitionId, String name) {
		return fetchByODI_N(objectDefinitionId, name, true);
	}

	/**
	 * Returns the object field where objectDefinitionId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByODI_N(
		long objectDefinitionId, String name, boolean useFinderCache) {

		name = Objects.toString(name, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {objectDefinitionId, name};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByODI_N, finderArgs, this);
		}

		if (result instanceof ObjectField) {
			ObjectField objectField = (ObjectField)result;

			if ((objectDefinitionId != objectField.getObjectDefinitionId()) ||
				!Objects.equals(name, objectField.getName())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_OBJECTFIELD_WHERE);

			sb.append(_FINDER_COLUMN_ODI_N_OBJECTDEFINITIONID_2);

			boolean bindName = false;

			if (name.isEmpty()) {
				sb.append(_FINDER_COLUMN_ODI_N_NAME_3);
			}
			else {
				bindName = true;

				sb.append(_FINDER_COLUMN_ODI_N_NAME_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId);

				if (bindName) {
					queryPos.add(name);
				}

				List<ObjectField> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByODI_N, finderArgs, list);
					}
				}
				else {
					if (list.size() > 1) {
						Collections.sort(list, Collections.reverseOrder());

						if (_log.isWarnEnabled()) {
							if (!useFinderCache) {
								finderArgs = new Object[] {
									objectDefinitionId, name
								};
							}

							_log.warn(
								"ObjectFieldPersistenceImpl.fetchByODI_N(long, String, boolean) with parameters (" +
									StringUtil.merge(finderArgs) +
										") yields a result set with more than 1 result. This violates the logical unique restriction. There is no order guarantee on which result is returned by this finder.");
						}
					}

					ObjectField objectField = list.get(0);

					result = objectField;

					cacheResult(objectField);
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
			return (ObjectField)result;
		}
	}

	/**
	 * Removes the object field where objectDefinitionId = &#63; and name = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the object field that was removed
	 */
	@Override
	public ObjectField removeByODI_N(long objectDefinitionId, String name)
		throws NoSuchObjectFieldException {

		ObjectField objectField = findByODI_N(objectDefinitionId, name);

		return remove(objectField);
	}

	/**
	 * Returns the number of object fields where objectDefinitionId = &#63; and name = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the number of matching object fields
	 */
	@Override
	public int countByODI_N(long objectDefinitionId, String name) {
		ObjectField objectField = fetchByODI_N(objectDefinitionId, name);

		if (objectField == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ODI_N_OBJECTDEFINITIONID_2 =
		"objectField.objectDefinitionId = ? AND ";

	private static final String _FINDER_COLUMN_ODI_N_NAME_2 =
		"objectField.name = ?";

	private static final String _FINDER_COLUMN_ODI_N_NAME_3 =
		"(objectField.name IS NULL OR objectField.name = '')";

	private FinderPath _finderPathWithPaginationFindByODI_S;
	private FinderPath _finderPathWithoutPaginationFindByODI_S;
	private FinderPath _finderPathCountByODI_S;

	/**
	 * Returns all the object fields where objectDefinitionId = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param system the system
	 * @return the matching object fields
	 */
	@Override
	public List<ObjectField> findByODI_S(
		long objectDefinitionId, boolean system) {

		return findByODI_S(
			objectDefinitionId, system, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the object fields where objectDefinitionId = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param system the system
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	@Override
	public List<ObjectField> findByODI_S(
		long objectDefinitionId, boolean system, int start, int end) {

		return findByODI_S(objectDefinitionId, system, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param system the system
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByODI_S(
		long objectDefinitionId, boolean system, int start, int end,
		OrderByComparator<ObjectField> orderByComparator) {

		return findByODI_S(
			objectDefinitionId, system, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param system the system
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByODI_S(
		long objectDefinitionId, boolean system, int start, int end,
		OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByODI_S;
				finderArgs = new Object[] {objectDefinitionId, system};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByODI_S;
			finderArgs = new Object[] {
				objectDefinitionId, system, start, end, orderByComparator
			};
		}

		List<ObjectField> list = null;

		if (useFinderCache) {
			list = (List<ObjectField>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectField objectField : list) {
					if ((objectDefinitionId !=
							objectField.getObjectDefinitionId()) ||
						(system != objectField.isSystem())) {

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

			sb.append(_SQL_SELECT_OBJECTFIELD_WHERE);

			sb.append(_FINDER_COLUMN_ODI_S_OBJECTDEFINITIONID_2);

			sb.append(_FINDER_COLUMN_ODI_S_SYSTEM_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectFieldModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId);

				queryPos.add(system);

				list = (List<ObjectField>)QueryUtil.list(
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
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByODI_S_First(
			long objectDefinitionId, boolean system,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		ObjectField objectField = fetchByODI_S_First(
			objectDefinitionId, system, orderByComparator);

		if (objectField != null) {
			return objectField;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append(", system=");
		sb.append(system);

		sb.append("}");

		throw new NoSuchObjectFieldException(sb.toString());
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByODI_S_First(
		long objectDefinitionId, boolean system,
		OrderByComparator<ObjectField> orderByComparator) {

		List<ObjectField> list = findByODI_S(
			objectDefinitionId, system, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object field in the ordered set where objectDefinitionId = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByODI_S_Last(
			long objectDefinitionId, boolean system,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		ObjectField objectField = fetchByODI_S_Last(
			objectDefinitionId, system, orderByComparator);

		if (objectField != null) {
			return objectField;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append(", system=");
		sb.append(system);

		sb.append("}");

		throw new NoSuchObjectFieldException(sb.toString());
	}

	/**
	 * Returns the last object field in the ordered set where objectDefinitionId = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByODI_S_Last(
		long objectDefinitionId, boolean system,
		OrderByComparator<ObjectField> orderByComparator) {

		int count = countByODI_S(objectDefinitionId, system);

		if (count == 0) {
			return null;
		}

		List<ObjectField> list = findByODI_S(
			objectDefinitionId, system, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object fields before and after the current object field in the ordered set where objectDefinitionId = &#63; and system = &#63;.
	 *
	 * @param objectFieldId the primary key of the current object field
	 * @param objectDefinitionId the object definition ID
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object field
	 * @throws NoSuchObjectFieldException if a object field with the primary key could not be found
	 */
	@Override
	public ObjectField[] findByODI_S_PrevAndNext(
			long objectFieldId, long objectDefinitionId, boolean system,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		ObjectField objectField = findByPrimaryKey(objectFieldId);

		Session session = null;

		try {
			session = openSession();

			ObjectField[] array = new ObjectFieldImpl[3];

			array[0] = getByODI_S_PrevAndNext(
				session, objectField, objectDefinitionId, system,
				orderByComparator, true);

			array[1] = objectField;

			array[2] = getByODI_S_PrevAndNext(
				session, objectField, objectDefinitionId, system,
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

	protected ObjectField getByODI_S_PrevAndNext(
		Session session, ObjectField objectField, long objectDefinitionId,
		boolean system, OrderByComparator<ObjectField> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTFIELD_WHERE);

		sb.append(_FINDER_COLUMN_ODI_S_OBJECTDEFINITIONID_2);

		sb.append(_FINDER_COLUMN_ODI_S_SYSTEM_2);

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
			sb.append(ObjectFieldModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(objectDefinitionId);

		queryPos.add(system);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(objectField)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectField> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object fields where objectDefinitionId = &#63; and system = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param system the system
	 */
	@Override
	public void removeByODI_S(long objectDefinitionId, boolean system) {
		for (ObjectField objectField :
				findByODI_S(
					objectDefinitionId, system, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(objectField);
		}
	}

	/**
	 * Returns the number of object fields where objectDefinitionId = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param system the system
	 * @return the number of matching object fields
	 */
	@Override
	public int countByODI_S(long objectDefinitionId, boolean system) {
		FinderPath finderPath = _finderPathCountByODI_S;

		Object[] finderArgs = new Object[] {objectDefinitionId, system};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_OBJECTFIELD_WHERE);

			sb.append(_FINDER_COLUMN_ODI_S_OBJECTDEFINITIONID_2);

			sb.append(_FINDER_COLUMN_ODI_S_SYSTEM_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId);

				queryPos.add(system);

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

	private static final String _FINDER_COLUMN_ODI_S_OBJECTDEFINITIONID_2 =
		"objectField.objectDefinitionId = ? AND ";

	private static final String _FINDER_COLUMN_ODI_S_SYSTEM_2 =
		"objectField.system = ?";

	private FinderPath _finderPathFetchByERC_C_ODI;

	/**
	 * Returns the object field where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63; or throws a <code>NoSuchObjectFieldException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByERC_C_ODI(
			String externalReferenceCode, long companyId,
			long objectDefinitionId)
		throws NoSuchObjectFieldException {

		ObjectField objectField = fetchByERC_C_ODI(
			externalReferenceCode, companyId, objectDefinitionId);

		if (objectField == null) {
			StringBundler sb = new StringBundler(8);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("externalReferenceCode=");
			sb.append(externalReferenceCode);

			sb.append(", companyId=");
			sb.append(companyId);

			sb.append(", objectDefinitionId=");
			sb.append(objectDefinitionId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchObjectFieldException(sb.toString());
		}

		return objectField;
	}

	/**
	 * Returns the object field where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByERC_C_ODI(
		String externalReferenceCode, long companyId, long objectDefinitionId) {

		return fetchByERC_C_ODI(
			externalReferenceCode, companyId, objectDefinitionId, true);
	}

	/**
	 * Returns the object field where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByERC_C_ODI(
		String externalReferenceCode, long companyId, long objectDefinitionId,
		boolean useFinderCache) {

		externalReferenceCode = Objects.toString(externalReferenceCode, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {
				externalReferenceCode, companyId, objectDefinitionId
			};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByERC_C_ODI, finderArgs, this);
		}

		if (result instanceof ObjectField) {
			ObjectField objectField = (ObjectField)result;

			if (!Objects.equals(
					externalReferenceCode,
					objectField.getExternalReferenceCode()) ||
				(companyId != objectField.getCompanyId()) ||
				(objectDefinitionId != objectField.getObjectDefinitionId())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(5);

			sb.append(_SQL_SELECT_OBJECTFIELD_WHERE);

			boolean bindExternalReferenceCode = false;

			if (externalReferenceCode.isEmpty()) {
				sb.append(_FINDER_COLUMN_ERC_C_ODI_EXTERNALREFERENCECODE_3);
			}
			else {
				bindExternalReferenceCode = true;

				sb.append(_FINDER_COLUMN_ERC_C_ODI_EXTERNALREFERENCECODE_2);
			}

			sb.append(_FINDER_COLUMN_ERC_C_ODI_COMPANYID_2);

			sb.append(_FINDER_COLUMN_ERC_C_ODI_OBJECTDEFINITIONID_2);

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

				queryPos.add(objectDefinitionId);

				List<ObjectField> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByERC_C_ODI, finderArgs, list);
					}
				}
				else {
					ObjectField objectField = list.get(0);

					result = objectField;

					cacheResult(objectField);
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
			return (ObjectField)result;
		}
	}

	/**
	 * Removes the object field where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the object field that was removed
	 */
	@Override
	public ObjectField removeByERC_C_ODI(
			String externalReferenceCode, long companyId,
			long objectDefinitionId)
		throws NoSuchObjectFieldException {

		ObjectField objectField = findByERC_C_ODI(
			externalReferenceCode, companyId, objectDefinitionId);

		return remove(objectField);
	}

	/**
	 * Returns the number of object fields where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object fields
	 */
	@Override
	public int countByERC_C_ODI(
		String externalReferenceCode, long companyId, long objectDefinitionId) {

		ObjectField objectField = fetchByERC_C_ODI(
			externalReferenceCode, companyId, objectDefinitionId);

		if (objectField == null) {
			return 0;
		}

		return 1;
	}

	private static final String
		_FINDER_COLUMN_ERC_C_ODI_EXTERNALREFERENCECODE_2 =
			"objectField.externalReferenceCode = ? AND ";

	private static final String
		_FINDER_COLUMN_ERC_C_ODI_EXTERNALREFERENCECODE_3 =
			"(objectField.externalReferenceCode IS NULL OR objectField.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_C_ODI_COMPANYID_2 =
		"objectField.companyId = ? AND ";

	private static final String _FINDER_COLUMN_ERC_C_ODI_OBJECTDEFINITIONID_2 =
		"objectField.objectDefinitionId = ?";

	private FinderPath _finderPathWithPaginationFindByODI_DBT_I;
	private FinderPath _finderPathWithoutPaginationFindByODI_DBT_I;
	private FinderPath _finderPathCountByODI_DBT_I;

	/**
	 * Returns all the object fields where objectDefinitionId = &#63; and dbType = &#63; and indexed = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbType the db type
	 * @param indexed the indexed
	 * @return the matching object fields
	 */
	@Override
	public List<ObjectField> findByODI_DBT_I(
		long objectDefinitionId, String dbType, boolean indexed) {

		return findByODI_DBT_I(
			objectDefinitionId, dbType, indexed, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object fields where objectDefinitionId = &#63; and dbType = &#63; and indexed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbType the db type
	 * @param indexed the indexed
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	@Override
	public List<ObjectField> findByODI_DBT_I(
		long objectDefinitionId, String dbType, boolean indexed, int start,
		int end) {

		return findByODI_DBT_I(
			objectDefinitionId, dbType, indexed, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and dbType = &#63; and indexed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbType the db type
	 * @param indexed the indexed
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByODI_DBT_I(
		long objectDefinitionId, String dbType, boolean indexed, int start,
		int end, OrderByComparator<ObjectField> orderByComparator) {

		return findByODI_DBT_I(
			objectDefinitionId, dbType, indexed, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and dbType = &#63; and indexed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbType the db type
	 * @param indexed the indexed
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByODI_DBT_I(
		long objectDefinitionId, String dbType, boolean indexed, int start,
		int end, OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		dbType = Objects.toString(dbType, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByODI_DBT_I;
				finderArgs = new Object[] {objectDefinitionId, dbType, indexed};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByODI_DBT_I;
			finderArgs = new Object[] {
				objectDefinitionId, dbType, indexed, start, end,
				orderByComparator
			};
		}

		List<ObjectField> list = null;

		if (useFinderCache) {
			list = (List<ObjectField>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectField objectField : list) {
					if ((objectDefinitionId !=
							objectField.getObjectDefinitionId()) ||
						!dbType.equals(objectField.getDBType()) ||
						(indexed != objectField.isIndexed())) {

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

			sb.append(_SQL_SELECT_OBJECTFIELD_WHERE);

			sb.append(_FINDER_COLUMN_ODI_DBT_I_OBJECTDEFINITIONID_2);

			boolean bindDBType = false;

			if (dbType.isEmpty()) {
				sb.append(_FINDER_COLUMN_ODI_DBT_I_DBTYPE_3);
			}
			else {
				bindDBType = true;

				sb.append(_FINDER_COLUMN_ODI_DBT_I_DBTYPE_2);
			}

			sb.append(_FINDER_COLUMN_ODI_DBT_I_INDEXED_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectFieldModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId);

				if (bindDBType) {
					queryPos.add(dbType);
				}

				queryPos.add(indexed);

				list = (List<ObjectField>)QueryUtil.list(
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
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and dbType = &#63; and indexed = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbType the db type
	 * @param indexed the indexed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByODI_DBT_I_First(
			long objectDefinitionId, String dbType, boolean indexed,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		ObjectField objectField = fetchByODI_DBT_I_First(
			objectDefinitionId, dbType, indexed, orderByComparator);

		if (objectField != null) {
			return objectField;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append(", dbType=");
		sb.append(dbType);

		sb.append(", indexed=");
		sb.append(indexed);

		sb.append("}");

		throw new NoSuchObjectFieldException(sb.toString());
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and dbType = &#63; and indexed = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbType the db type
	 * @param indexed the indexed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByODI_DBT_I_First(
		long objectDefinitionId, String dbType, boolean indexed,
		OrderByComparator<ObjectField> orderByComparator) {

		List<ObjectField> list = findByODI_DBT_I(
			objectDefinitionId, dbType, indexed, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object field in the ordered set where objectDefinitionId = &#63; and dbType = &#63; and indexed = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbType the db type
	 * @param indexed the indexed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByODI_DBT_I_Last(
			long objectDefinitionId, String dbType, boolean indexed,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		ObjectField objectField = fetchByODI_DBT_I_Last(
			objectDefinitionId, dbType, indexed, orderByComparator);

		if (objectField != null) {
			return objectField;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append(", dbType=");
		sb.append(dbType);

		sb.append(", indexed=");
		sb.append(indexed);

		sb.append("}");

		throw new NoSuchObjectFieldException(sb.toString());
	}

	/**
	 * Returns the last object field in the ordered set where objectDefinitionId = &#63; and dbType = &#63; and indexed = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbType the db type
	 * @param indexed the indexed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByODI_DBT_I_Last(
		long objectDefinitionId, String dbType, boolean indexed,
		OrderByComparator<ObjectField> orderByComparator) {

		int count = countByODI_DBT_I(objectDefinitionId, dbType, indexed);

		if (count == 0) {
			return null;
		}

		List<ObjectField> list = findByODI_DBT_I(
			objectDefinitionId, dbType, indexed, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object fields before and after the current object field in the ordered set where objectDefinitionId = &#63; and dbType = &#63; and indexed = &#63;.
	 *
	 * @param objectFieldId the primary key of the current object field
	 * @param objectDefinitionId the object definition ID
	 * @param dbType the db type
	 * @param indexed the indexed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object field
	 * @throws NoSuchObjectFieldException if a object field with the primary key could not be found
	 */
	@Override
	public ObjectField[] findByODI_DBT_I_PrevAndNext(
			long objectFieldId, long objectDefinitionId, String dbType,
			boolean indexed, OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		dbType = Objects.toString(dbType, "");

		ObjectField objectField = findByPrimaryKey(objectFieldId);

		Session session = null;

		try {
			session = openSession();

			ObjectField[] array = new ObjectFieldImpl[3];

			array[0] = getByODI_DBT_I_PrevAndNext(
				session, objectField, objectDefinitionId, dbType, indexed,
				orderByComparator, true);

			array[1] = objectField;

			array[2] = getByODI_DBT_I_PrevAndNext(
				session, objectField, objectDefinitionId, dbType, indexed,
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

	protected ObjectField getByODI_DBT_I_PrevAndNext(
		Session session, ObjectField objectField, long objectDefinitionId,
		String dbType, boolean indexed,
		OrderByComparator<ObjectField> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_OBJECTFIELD_WHERE);

		sb.append(_FINDER_COLUMN_ODI_DBT_I_OBJECTDEFINITIONID_2);

		boolean bindDBType = false;

		if (dbType.isEmpty()) {
			sb.append(_FINDER_COLUMN_ODI_DBT_I_DBTYPE_3);
		}
		else {
			bindDBType = true;

			sb.append(_FINDER_COLUMN_ODI_DBT_I_DBTYPE_2);
		}

		sb.append(_FINDER_COLUMN_ODI_DBT_I_INDEXED_2);

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
			sb.append(ObjectFieldModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(objectDefinitionId);

		if (bindDBType) {
			queryPos.add(dbType);
		}

		queryPos.add(indexed);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(objectField)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectField> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object fields where objectDefinitionId = &#63; and dbType = &#63; and indexed = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbType the db type
	 * @param indexed the indexed
	 */
	@Override
	public void removeByODI_DBT_I(
		long objectDefinitionId, String dbType, boolean indexed) {

		for (ObjectField objectField :
				findByODI_DBT_I(
					objectDefinitionId, dbType, indexed, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(objectField);
		}
	}

	/**
	 * Returns the number of object fields where objectDefinitionId = &#63; and dbType = &#63; and indexed = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbType the db type
	 * @param indexed the indexed
	 * @return the number of matching object fields
	 */
	@Override
	public int countByODI_DBT_I(
		long objectDefinitionId, String dbType, boolean indexed) {

		dbType = Objects.toString(dbType, "");

		FinderPath finderPath = _finderPathCountByODI_DBT_I;

		Object[] finderArgs = new Object[] {
			objectDefinitionId, dbType, indexed
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_COUNT_OBJECTFIELD_WHERE);

			sb.append(_FINDER_COLUMN_ODI_DBT_I_OBJECTDEFINITIONID_2);

			boolean bindDBType = false;

			if (dbType.isEmpty()) {
				sb.append(_FINDER_COLUMN_ODI_DBT_I_DBTYPE_3);
			}
			else {
				bindDBType = true;

				sb.append(_FINDER_COLUMN_ODI_DBT_I_DBTYPE_2);
			}

			sb.append(_FINDER_COLUMN_ODI_DBT_I_INDEXED_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId);

				if (bindDBType) {
					queryPos.add(dbType);
				}

				queryPos.add(indexed);

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

	private static final String _FINDER_COLUMN_ODI_DBT_I_OBJECTDEFINITIONID_2 =
		"objectField.objectDefinitionId = ? AND ";

	private static final String _FINDER_COLUMN_ODI_DBT_I_DBTYPE_2 =
		"objectField.dbType = ? AND ";

	private static final String _FINDER_COLUMN_ODI_DBT_I_DBTYPE_3 =
		"(objectField.dbType IS NULL OR objectField.dbType = '') AND ";

	private static final String _FINDER_COLUMN_ODI_DBT_I_INDEXED_2 =
		"objectField.indexed = ?";

	private FinderPath _finderPathWithPaginationFindByODI_L_S;
	private FinderPath _finderPathWithoutPaginationFindByODI_L_S;
	private FinderPath _finderPathCountByODI_L_S;

	/**
	 * Returns all the object fields where objectDefinitionId = &#63; and localized = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param system the system
	 * @return the matching object fields
	 */
	@Override
	public List<ObjectField> findByODI_L_S(
		long objectDefinitionId, boolean localized, boolean system) {

		return findByODI_L_S(
			objectDefinitionId, localized, system, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object fields where objectDefinitionId = &#63; and localized = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param system the system
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	@Override
	public List<ObjectField> findByODI_L_S(
		long objectDefinitionId, boolean localized, boolean system, int start,
		int end) {

		return findByODI_L_S(
			objectDefinitionId, localized, system, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and localized = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param system the system
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByODI_L_S(
		long objectDefinitionId, boolean localized, boolean system, int start,
		int end, OrderByComparator<ObjectField> orderByComparator) {

		return findByODI_L_S(
			objectDefinitionId, localized, system, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and localized = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param system the system
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	@Override
	public List<ObjectField> findByODI_L_S(
		long objectDefinitionId, boolean localized, boolean system, int start,
		int end, OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByODI_L_S;
				finderArgs = new Object[] {
					objectDefinitionId, localized, system
				};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByODI_L_S;
			finderArgs = new Object[] {
				objectDefinitionId, localized, system, start, end,
				orderByComparator
			};
		}

		List<ObjectField> list = null;

		if (useFinderCache) {
			list = (List<ObjectField>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectField objectField : list) {
					if ((objectDefinitionId !=
							objectField.getObjectDefinitionId()) ||
						(localized != objectField.isLocalized()) ||
						(system != objectField.isSystem())) {

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

			sb.append(_SQL_SELECT_OBJECTFIELD_WHERE);

			sb.append(_FINDER_COLUMN_ODI_L_S_OBJECTDEFINITIONID_2);

			sb.append(_FINDER_COLUMN_ODI_L_S_LOCALIZED_2);

			sb.append(_FINDER_COLUMN_ODI_L_S_SYSTEM_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectFieldModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId);

				queryPos.add(localized);

				queryPos.add(system);

				list = (List<ObjectField>)QueryUtil.list(
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
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and localized = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByODI_L_S_First(
			long objectDefinitionId, boolean localized, boolean system,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		ObjectField objectField = fetchByODI_L_S_First(
			objectDefinitionId, localized, system, orderByComparator);

		if (objectField != null) {
			return objectField;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append(", localized=");
		sb.append(localized);

		sb.append(", system=");
		sb.append(system);

		sb.append("}");

		throw new NoSuchObjectFieldException(sb.toString());
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and localized = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByODI_L_S_First(
		long objectDefinitionId, boolean localized, boolean system,
		OrderByComparator<ObjectField> orderByComparator) {

		List<ObjectField> list = findByODI_L_S(
			objectDefinitionId, localized, system, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object field in the ordered set where objectDefinitionId = &#63; and localized = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	@Override
	public ObjectField findByODI_L_S_Last(
			long objectDefinitionId, boolean localized, boolean system,
			OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		ObjectField objectField = fetchByODI_L_S_Last(
			objectDefinitionId, localized, system, orderByComparator);

		if (objectField != null) {
			return objectField;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append(", localized=");
		sb.append(localized);

		sb.append(", system=");
		sb.append(system);

		sb.append("}");

		throw new NoSuchObjectFieldException(sb.toString());
	}

	/**
	 * Returns the last object field in the ordered set where objectDefinitionId = &#63; and localized = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field, or <code>null</code> if a matching object field could not be found
	 */
	@Override
	public ObjectField fetchByODI_L_S_Last(
		long objectDefinitionId, boolean localized, boolean system,
		OrderByComparator<ObjectField> orderByComparator) {

		int count = countByODI_L_S(objectDefinitionId, localized, system);

		if (count == 0) {
			return null;
		}

		List<ObjectField> list = findByODI_L_S(
			objectDefinitionId, localized, system, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object fields before and after the current object field in the ordered set where objectDefinitionId = &#63; and localized = &#63; and system = &#63;.
	 *
	 * @param objectFieldId the primary key of the current object field
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object field
	 * @throws NoSuchObjectFieldException if a object field with the primary key could not be found
	 */
	@Override
	public ObjectField[] findByODI_L_S_PrevAndNext(
			long objectFieldId, long objectDefinitionId, boolean localized,
			boolean system, OrderByComparator<ObjectField> orderByComparator)
		throws NoSuchObjectFieldException {

		ObjectField objectField = findByPrimaryKey(objectFieldId);

		Session session = null;

		try {
			session = openSession();

			ObjectField[] array = new ObjectFieldImpl[3];

			array[0] = getByODI_L_S_PrevAndNext(
				session, objectField, objectDefinitionId, localized, system,
				orderByComparator, true);

			array[1] = objectField;

			array[2] = getByODI_L_S_PrevAndNext(
				session, objectField, objectDefinitionId, localized, system,
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

	protected ObjectField getByODI_L_S_PrevAndNext(
		Session session, ObjectField objectField, long objectDefinitionId,
		boolean localized, boolean system,
		OrderByComparator<ObjectField> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_OBJECTFIELD_WHERE);

		sb.append(_FINDER_COLUMN_ODI_L_S_OBJECTDEFINITIONID_2);

		sb.append(_FINDER_COLUMN_ODI_L_S_LOCALIZED_2);

		sb.append(_FINDER_COLUMN_ODI_L_S_SYSTEM_2);

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
			sb.append(ObjectFieldModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(objectDefinitionId);

		queryPos.add(localized);

		queryPos.add(system);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(objectField)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectField> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object fields where objectDefinitionId = &#63; and localized = &#63; and system = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param system the system
	 */
	@Override
	public void removeByODI_L_S(
		long objectDefinitionId, boolean localized, boolean system) {

		for (ObjectField objectField :
				findByODI_L_S(
					objectDefinitionId, localized, system, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(objectField);
		}
	}

	/**
	 * Returns the number of object fields where objectDefinitionId = &#63; and localized = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param system the system
	 * @return the number of matching object fields
	 */
	@Override
	public int countByODI_L_S(
		long objectDefinitionId, boolean localized, boolean system) {

		FinderPath finderPath = _finderPathCountByODI_L_S;

		Object[] finderArgs = new Object[] {
			objectDefinitionId, localized, system
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_COUNT_OBJECTFIELD_WHERE);

			sb.append(_FINDER_COLUMN_ODI_L_S_OBJECTDEFINITIONID_2);

			sb.append(_FINDER_COLUMN_ODI_L_S_LOCALIZED_2);

			sb.append(_FINDER_COLUMN_ODI_L_S_SYSTEM_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId);

				queryPos.add(localized);

				queryPos.add(system);

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

	private static final String _FINDER_COLUMN_ODI_L_S_OBJECTDEFINITIONID_2 =
		"objectField.objectDefinitionId = ? AND ";

	private static final String _FINDER_COLUMN_ODI_L_S_LOCALIZED_2 =
		"objectField.localized = ? AND ";

	private static final String _FINDER_COLUMN_ODI_L_S_SYSTEM_2 =
		"objectField.system = ?";

	public ObjectFieldPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("state", "state_");
		dbColumnNames.put("system", "system_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ObjectField.class);

		setModelImplClass(ObjectFieldImpl.class);
		setModelPKClass(long.class);

		setTable(ObjectFieldTable.INSTANCE);
	}

	/**
	 * Caches the object field in the entity cache if it is enabled.
	 *
	 * @param objectField the object field
	 */
	@Override
	public void cacheResult(ObjectField objectField) {
		entityCache.putResult(
			ObjectFieldImpl.class, objectField.getPrimaryKey(), objectField);

		finderCache.putResult(
			_finderPathFetchByODI_N,
			new Object[] {
				objectField.getObjectDefinitionId(), objectField.getName()
			},
			objectField);

		finderCache.putResult(
			_finderPathFetchByERC_C_ODI,
			new Object[] {
				objectField.getExternalReferenceCode(),
				objectField.getCompanyId(), objectField.getObjectDefinitionId()
			},
			objectField);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the object fields in the entity cache if it is enabled.
	 *
	 * @param objectFields the object fields
	 */
	@Override
	public void cacheResult(List<ObjectField> objectFields) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (objectFields.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (ObjectField objectField : objectFields) {
			if (entityCache.getResult(
					ObjectFieldImpl.class, objectField.getPrimaryKey()) ==
						null) {

				cacheResult(objectField);
			}
		}
	}

	/**
	 * Clears the cache for all object fields.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(ObjectFieldImpl.class);

		finderCache.clearCache(ObjectFieldImpl.class);
	}

	/**
	 * Clears the cache for the object field.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(ObjectField objectField) {
		entityCache.removeResult(ObjectFieldImpl.class, objectField);
	}

	@Override
	public void clearCache(List<ObjectField> objectFields) {
		for (ObjectField objectField : objectFields) {
			entityCache.removeResult(ObjectFieldImpl.class, objectField);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(ObjectFieldImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(ObjectFieldImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		ObjectFieldModelImpl objectFieldModelImpl) {

		Object[] args = new Object[] {
			objectFieldModelImpl.getObjectDefinitionId(),
			objectFieldModelImpl.getName()
		};

		finderCache.putResult(
			_finderPathFetchByODI_N, args, objectFieldModelImpl);

		args = new Object[] {
			objectFieldModelImpl.getExternalReferenceCode(),
			objectFieldModelImpl.getCompanyId(),
			objectFieldModelImpl.getObjectDefinitionId()
		};

		finderCache.putResult(
			_finderPathFetchByERC_C_ODI, args, objectFieldModelImpl);
	}

	/**
	 * Creates a new object field with the primary key. Does not add the object field to the database.
	 *
	 * @param objectFieldId the primary key for the new object field
	 * @return the new object field
	 */
	@Override
	public ObjectField create(long objectFieldId) {
		ObjectField objectField = new ObjectFieldImpl();

		objectField.setNew(true);
		objectField.setPrimaryKey(objectFieldId);

		String uuid = PortalUUIDUtil.generate();

		objectField.setUuid(uuid);

		objectField.setCompanyId(CompanyThreadLocal.getCompanyId());

		return objectField;
	}

	/**
	 * Removes the object field with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectFieldId the primary key of the object field
	 * @return the object field that was removed
	 * @throws NoSuchObjectFieldException if a object field with the primary key could not be found
	 */
	@Override
	public ObjectField remove(long objectFieldId)
		throws NoSuchObjectFieldException {

		return remove((Serializable)objectFieldId);
	}

	/**
	 * Removes the object field with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the object field
	 * @return the object field that was removed
	 * @throws NoSuchObjectFieldException if a object field with the primary key could not be found
	 */
	@Override
	public ObjectField remove(Serializable primaryKey)
		throws NoSuchObjectFieldException {

		Session session = null;

		try {
			session = openSession();

			ObjectField objectField = (ObjectField)session.get(
				ObjectFieldImpl.class, primaryKey);

			if (objectField == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchObjectFieldException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(objectField);
		}
		catch (NoSuchObjectFieldException noSuchEntityException) {
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
	protected ObjectField removeImpl(ObjectField objectField) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(objectField)) {
				objectField = (ObjectField)session.get(
					ObjectFieldImpl.class, objectField.getPrimaryKeyObj());
			}

			if (objectField != null) {
				session.delete(objectField);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (objectField != null) {
			clearCache(objectField);
		}

		return objectField;
	}

	@Override
	public ObjectField updateImpl(ObjectField objectField) {
		boolean isNew = objectField.isNew();

		if (!(objectField instanceof ObjectFieldModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(objectField.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(objectField);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in objectField proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ObjectField implementation " +
					objectField.getClass());
		}

		ObjectFieldModelImpl objectFieldModelImpl =
			(ObjectFieldModelImpl)objectField;

		if (Validator.isNull(objectField.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			objectField.setUuid(uuid);
		}

		if (Validator.isNull(objectField.getExternalReferenceCode())) {
			objectField.setExternalReferenceCode(objectField.getUuid());
		}
		else {
			if (!Objects.equals(
					objectFieldModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					objectField.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = objectField.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = objectField.getPrimaryKey();
					}

					try {
						objectField.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								ObjectField.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								objectField.getExternalReferenceCode(), null));
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

		if (isNew && (objectField.getCreateDate() == null)) {
			if (serviceContext == null) {
				objectField.setCreateDate(date);
			}
			else {
				objectField.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!objectFieldModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				objectField.setModifiedDate(date);
			}
			else {
				objectField.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(objectField);
			}
			else {
				objectField = (ObjectField)session.merge(objectField);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			ObjectFieldImpl.class, objectFieldModelImpl, false, true);

		cacheUniqueFindersCache(objectFieldModelImpl);

		if (isNew) {
			objectField.setNew(false);
		}

		objectField.resetOriginalValues();

		return objectField;
	}

	/**
	 * Returns the object field with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the object field
	 * @return the object field
	 * @throws NoSuchObjectFieldException if a object field with the primary key could not be found
	 */
	@Override
	public ObjectField findByPrimaryKey(Serializable primaryKey)
		throws NoSuchObjectFieldException {

		ObjectField objectField = fetchByPrimaryKey(primaryKey);

		if (objectField == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchObjectFieldException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return objectField;
	}

	/**
	 * Returns the object field with the primary key or throws a <code>NoSuchObjectFieldException</code> if it could not be found.
	 *
	 * @param objectFieldId the primary key of the object field
	 * @return the object field
	 * @throws NoSuchObjectFieldException if a object field with the primary key could not be found
	 */
	@Override
	public ObjectField findByPrimaryKey(long objectFieldId)
		throws NoSuchObjectFieldException {

		return findByPrimaryKey((Serializable)objectFieldId);
	}

	/**
	 * Returns the object field with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectFieldId the primary key of the object field
	 * @return the object field, or <code>null</code> if a object field with the primary key could not be found
	 */
	@Override
	public ObjectField fetchByPrimaryKey(long objectFieldId) {
		return fetchByPrimaryKey((Serializable)objectFieldId);
	}

	/**
	 * Returns all the object fields.
	 *
	 * @return the object fields
	 */
	@Override
	public List<ObjectField> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object fields.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of object fields
	 */
	@Override
	public List<ObjectField> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the object fields.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of object fields
	 */
	@Override
	public List<ObjectField> findAll(
		int start, int end, OrderByComparator<ObjectField> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object fields.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of object fields
	 */
	@Override
	public List<ObjectField> findAll(
		int start, int end, OrderByComparator<ObjectField> orderByComparator,
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

		List<ObjectField> list = null;

		if (useFinderCache) {
			list = (List<ObjectField>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_OBJECTFIELD);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_OBJECTFIELD;

				sql = sql.concat(ObjectFieldModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<ObjectField>)QueryUtil.list(
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
	 * Removes all the object fields from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (ObjectField objectField : findAll()) {
			remove(objectField);
		}
	}

	/**
	 * Returns the number of object fields.
	 *
	 * @return the number of object fields
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_OBJECTFIELD);

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
		return "objectFieldId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OBJECTFIELD;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ObjectFieldModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the object field persistence.
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

		_finderPathWithPaginationFindByListTypeDefinitionId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
			"findByListTypeDefinitionId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"listTypeDefinitionId"}, true);

		_finderPathWithoutPaginationFindByListTypeDefinitionId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"findByListTypeDefinitionId", new String[] {Long.class.getName()},
			new String[] {"listTypeDefinitionId"}, true);

		_finderPathCountByListTypeDefinitionId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByListTypeDefinitionId", new String[] {Long.class.getName()},
			new String[] {"listTypeDefinitionId"}, false);

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

		_finderPathWithPaginationFindByLTDI_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByLTDI_S",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"listTypeDefinitionId", "state_"}, true);

		_finderPathWithoutPaginationFindByLTDI_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByLTDI_S",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"listTypeDefinitionId", "state_"}, true);

		_finderPathCountByLTDI_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByLTDI_S",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"listTypeDefinitionId", "state_"}, false);

		_finderPathWithPaginationFindByODI_BT = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI_BT",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"objectDefinitionId", "businessType"}, true);

		_finderPathWithoutPaginationFindByODI_BT = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByODI_BT",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"objectDefinitionId", "businessType"}, true);

		_finderPathCountByODI_BT = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByODI_BT",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"objectDefinitionId", "businessType"}, false);

		_finderPathWithPaginationFindByODI_DTN = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI_DTN",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"objectDefinitionId", "dbTableName"}, true);

		_finderPathWithoutPaginationFindByODI_DTN = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByODI_DTN",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"objectDefinitionId", "dbTableName"}, true);

		_finderPathCountByODI_DTN = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByODI_DTN",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"objectDefinitionId", "dbTableName"}, false);

		_finderPathWithPaginationFindByODI_I = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI_I",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"objectDefinitionId", "indexed"}, true);

		_finderPathWithoutPaginationFindByODI_I = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByODI_I",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"objectDefinitionId", "indexed"}, true);

		_finderPathCountByODI_I = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByODI_I",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"objectDefinitionId", "indexed"}, false);

		_finderPathWithPaginationFindByODI_L = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI_L",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"objectDefinitionId", "localized"}, true);

		_finderPathWithoutPaginationFindByODI_L = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByODI_L",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"objectDefinitionId", "localized"}, true);

		_finderPathCountByODI_L = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByODI_L",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"objectDefinitionId", "localized"}, false);

		_finderPathFetchByODI_N = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByODI_N",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"objectDefinitionId", "name"}, true);

		_finderPathWithPaginationFindByODI_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI_S",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"objectDefinitionId", "system_"}, true);

		_finderPathWithoutPaginationFindByODI_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByODI_S",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"objectDefinitionId", "system_"}, true);

		_finderPathCountByODI_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByODI_S",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"objectDefinitionId", "system_"}, false);

		_finderPathFetchByERC_C_ODI = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_C_ODI",
			new String[] {
				String.class.getName(), Long.class.getName(),
				Long.class.getName()
			},
			new String[] {
				"externalReferenceCode", "companyId", "objectDefinitionId"
			},
			true);

		_finderPathWithPaginationFindByODI_DBT_I = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI_DBT_I",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Boolean.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"objectDefinitionId", "dbType", "indexed"}, true);

		_finderPathWithoutPaginationFindByODI_DBT_I = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByODI_DBT_I",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Boolean.class.getName()
			},
			new String[] {"objectDefinitionId", "dbType", "indexed"}, true);

		_finderPathCountByODI_DBT_I = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByODI_DBT_I",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Boolean.class.getName()
			},
			new String[] {"objectDefinitionId", "dbType", "indexed"}, false);

		_finderPathWithPaginationFindByODI_L_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI_L_S",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Boolean.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"objectDefinitionId", "localized", "system_"}, true);

		_finderPathWithoutPaginationFindByODI_L_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByODI_L_S",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Boolean.class.getName()
			},
			new String[] {"objectDefinitionId", "localized", "system_"}, true);

		_finderPathCountByODI_L_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByODI_L_S",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Boolean.class.getName()
			},
			new String[] {"objectDefinitionId", "localized", "system_"}, false);

		ObjectFieldUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ObjectFieldUtil.setPersistence(null);

		entityCache.removeCache(ObjectFieldImpl.class.getName());
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

	private static final String _SQL_SELECT_OBJECTFIELD =
		"SELECT objectField FROM ObjectField objectField";

	private static final String _SQL_SELECT_OBJECTFIELD_WHERE =
		"SELECT objectField FROM ObjectField objectField WHERE ";

	private static final String _SQL_COUNT_OBJECTFIELD =
		"SELECT COUNT(objectField) FROM ObjectField objectField";

	private static final String _SQL_COUNT_OBJECTFIELD_WHERE =
		"SELECT COUNT(objectField) FROM ObjectField objectField WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS = "objectField.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No ObjectField exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ObjectField exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectFieldPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "state", "system"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}