/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.impl;

import com.liferay.object.exception.NoSuchObjectActionException;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectActionTable;
import com.liferay.object.model.impl.ObjectActionImpl;
import com.liferay.object.model.impl.ObjectActionModelImpl;
import com.liferay.object.service.persistence.ObjectActionPersistence;
import com.liferay.object.service.persistence.ObjectActionUtil;
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
 * The persistence implementation for the object action service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = ObjectActionPersistence.class)
public class ObjectActionPersistenceImpl
	extends BasePersistenceImpl<ObjectAction>
	implements ObjectActionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ObjectActionUtil</code> to access the object action persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ObjectActionImpl.class.getName();

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
	 * Returns all the object actions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching object actions
	 */
	@Override
	public List<ObjectAction> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object actions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @return the range of matching object actions
	 */
	@Override
	public List<ObjectAction> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object actions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object actions
	 */
	@Override
	public List<ObjectAction> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectAction> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object actions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object actions
	 */
	@Override
	public List<ObjectAction> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectAction> orderByComparator,
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

		List<ObjectAction> list = null;

		if (useFinderCache) {
			list = (List<ObjectAction>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectAction objectAction : list) {
					if (!uuid.equals(objectAction.getUuid())) {
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

			sb.append(_SQL_SELECT_OBJECTACTION_WHERE);

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
				sb.append(ObjectActionModelImpl.ORDER_BY_JPQL);
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

				list = (List<ObjectAction>)QueryUtil.list(
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
	 * Returns the first object action in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	@Override
	public ObjectAction findByUuid_First(
			String uuid, OrderByComparator<ObjectAction> orderByComparator)
		throws NoSuchObjectActionException {

		ObjectAction objectAction = fetchByUuid_First(uuid, orderByComparator);

		if (objectAction != null) {
			return objectAction;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchObjectActionException(sb.toString());
	}

	/**
	 * Returns the first object action in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action, or <code>null</code> if a matching object action could not be found
	 */
	@Override
	public ObjectAction fetchByUuid_First(
		String uuid, OrderByComparator<ObjectAction> orderByComparator) {

		List<ObjectAction> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object action in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	@Override
	public ObjectAction findByUuid_Last(
			String uuid, OrderByComparator<ObjectAction> orderByComparator)
		throws NoSuchObjectActionException {

		ObjectAction objectAction = fetchByUuid_Last(uuid, orderByComparator);

		if (objectAction != null) {
			return objectAction;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchObjectActionException(sb.toString());
	}

	/**
	 * Returns the last object action in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object action, or <code>null</code> if a matching object action could not be found
	 */
	@Override
	public ObjectAction fetchByUuid_Last(
		String uuid, OrderByComparator<ObjectAction> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<ObjectAction> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object actions before and after the current object action in the ordered set where uuid = &#63;.
	 *
	 * @param objectActionId the primary key of the current object action
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object action
	 * @throws NoSuchObjectActionException if a object action with the primary key could not be found
	 */
	@Override
	public ObjectAction[] findByUuid_PrevAndNext(
			long objectActionId, String uuid,
			OrderByComparator<ObjectAction> orderByComparator)
		throws NoSuchObjectActionException {

		uuid = Objects.toString(uuid, "");

		ObjectAction objectAction = findByPrimaryKey(objectActionId);

		Session session = null;

		try {
			session = openSession();

			ObjectAction[] array = new ObjectActionImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, objectAction, uuid, orderByComparator, true);

			array[1] = objectAction;

			array[2] = getByUuid_PrevAndNext(
				session, objectAction, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected ObjectAction getByUuid_PrevAndNext(
		Session session, ObjectAction objectAction, String uuid,
		OrderByComparator<ObjectAction> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_OBJECTACTION_WHERE);

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
			sb.append(ObjectActionModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(objectAction)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectAction> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object actions where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (ObjectAction objectAction :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(objectAction);
		}
	}

	/**
	 * Returns the number of object actions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object actions
	 */
	@Override
	public int countByUuid(String uuid) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid;

		Object[] finderArgs = new Object[] {uuid};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_OBJECTACTION_WHERE);

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
		"objectAction.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(objectAction.uuid IS NULL OR objectAction.uuid = '')";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the object actions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching object actions
	 */
	@Override
	public List<ObjectAction> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object actions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @return the range of matching object actions
	 */
	@Override
	public List<ObjectAction> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object actions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object actions
	 */
	@Override
	public List<ObjectAction> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectAction> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object actions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object actions
	 */
	@Override
	public List<ObjectAction> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectAction> orderByComparator,
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

		List<ObjectAction> list = null;

		if (useFinderCache) {
			list = (List<ObjectAction>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectAction objectAction : list) {
					if (!uuid.equals(objectAction.getUuid()) ||
						(companyId != objectAction.getCompanyId())) {

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

			sb.append(_SQL_SELECT_OBJECTACTION_WHERE);

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
				sb.append(ObjectActionModelImpl.ORDER_BY_JPQL);
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

				list = (List<ObjectAction>)QueryUtil.list(
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
	 * Returns the first object action in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	@Override
	public ObjectAction findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectAction> orderByComparator)
		throws NoSuchObjectActionException {

		ObjectAction objectAction = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (objectAction != null) {
			return objectAction;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchObjectActionException(sb.toString());
	}

	/**
	 * Returns the first object action in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action, or <code>null</code> if a matching object action could not be found
	 */
	@Override
	public ObjectAction fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectAction> orderByComparator) {

		List<ObjectAction> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object action in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	@Override
	public ObjectAction findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<ObjectAction> orderByComparator)
		throws NoSuchObjectActionException {

		ObjectAction objectAction = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (objectAction != null) {
			return objectAction;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchObjectActionException(sb.toString());
	}

	/**
	 * Returns the last object action in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object action, or <code>null</code> if a matching object action could not be found
	 */
	@Override
	public ObjectAction fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<ObjectAction> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<ObjectAction> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object actions before and after the current object action in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param objectActionId the primary key of the current object action
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object action
	 * @throws NoSuchObjectActionException if a object action with the primary key could not be found
	 */
	@Override
	public ObjectAction[] findByUuid_C_PrevAndNext(
			long objectActionId, String uuid, long companyId,
			OrderByComparator<ObjectAction> orderByComparator)
		throws NoSuchObjectActionException {

		uuid = Objects.toString(uuid, "");

		ObjectAction objectAction = findByPrimaryKey(objectActionId);

		Session session = null;

		try {
			session = openSession();

			ObjectAction[] array = new ObjectActionImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, objectAction, uuid, companyId, orderByComparator,
				true);

			array[1] = objectAction;

			array[2] = getByUuid_C_PrevAndNext(
				session, objectAction, uuid, companyId, orderByComparator,
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

	protected ObjectAction getByUuid_C_PrevAndNext(
		Session session, ObjectAction objectAction, String uuid, long companyId,
		OrderByComparator<ObjectAction> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_OBJECTACTION_WHERE);

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
			sb.append(ObjectActionModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(objectAction)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectAction> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object actions where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (ObjectAction objectAction :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(objectAction);
		}
	}

	/**
	 * Returns the number of object actions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object actions
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid_C;

		Object[] finderArgs = new Object[] {uuid, companyId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_OBJECTACTION_WHERE);

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
		"objectAction.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(objectAction.uuid IS NULL OR objectAction.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"objectAction.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByObjectDefinitionId;
	private FinderPath _finderPathWithoutPaginationFindByObjectDefinitionId;
	private FinderPath _finderPathCountByObjectDefinitionId;

	/**
	 * Returns all the object actions where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object actions
	 */
	@Override
	public List<ObjectAction> findByObjectDefinitionId(
		long objectDefinitionId) {

		return findByObjectDefinitionId(
			objectDefinitionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object actions where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @return the range of matching object actions
	 */
	@Override
	public List<ObjectAction> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end) {

		return findByObjectDefinitionId(objectDefinitionId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object actions where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object actions
	 */
	@Override
	public List<ObjectAction> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectAction> orderByComparator) {

		return findByObjectDefinitionId(
			objectDefinitionId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object actions where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object actions
	 */
	@Override
	public List<ObjectAction> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectAction> orderByComparator,
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

		List<ObjectAction> list = null;

		if (useFinderCache) {
			list = (List<ObjectAction>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectAction objectAction : list) {
					if (objectDefinitionId !=
							objectAction.getObjectDefinitionId()) {

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

			sb.append(_SQL_SELECT_OBJECTACTION_WHERE);

			sb.append(_FINDER_COLUMN_OBJECTDEFINITIONID_OBJECTDEFINITIONID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectActionModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId);

				list = (List<ObjectAction>)QueryUtil.list(
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
	 * Returns the first object action in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	@Override
	public ObjectAction findByObjectDefinitionId_First(
			long objectDefinitionId,
			OrderByComparator<ObjectAction> orderByComparator)
		throws NoSuchObjectActionException {

		ObjectAction objectAction = fetchByObjectDefinitionId_First(
			objectDefinitionId, orderByComparator);

		if (objectAction != null) {
			return objectAction;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append("}");

		throw new NoSuchObjectActionException(sb.toString());
	}

	/**
	 * Returns the first object action in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action, or <code>null</code> if a matching object action could not be found
	 */
	@Override
	public ObjectAction fetchByObjectDefinitionId_First(
		long objectDefinitionId,
		OrderByComparator<ObjectAction> orderByComparator) {

		List<ObjectAction> list = findByObjectDefinitionId(
			objectDefinitionId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object action in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	@Override
	public ObjectAction findByObjectDefinitionId_Last(
			long objectDefinitionId,
			OrderByComparator<ObjectAction> orderByComparator)
		throws NoSuchObjectActionException {

		ObjectAction objectAction = fetchByObjectDefinitionId_Last(
			objectDefinitionId, orderByComparator);

		if (objectAction != null) {
			return objectAction;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append("}");

		throw new NoSuchObjectActionException(sb.toString());
	}

	/**
	 * Returns the last object action in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object action, or <code>null</code> if a matching object action could not be found
	 */
	@Override
	public ObjectAction fetchByObjectDefinitionId_Last(
		long objectDefinitionId,
		OrderByComparator<ObjectAction> orderByComparator) {

		int count = countByObjectDefinitionId(objectDefinitionId);

		if (count == 0) {
			return null;
		}

		List<ObjectAction> list = findByObjectDefinitionId(
			objectDefinitionId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object actions before and after the current object action in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectActionId the primary key of the current object action
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object action
	 * @throws NoSuchObjectActionException if a object action with the primary key could not be found
	 */
	@Override
	public ObjectAction[] findByObjectDefinitionId_PrevAndNext(
			long objectActionId, long objectDefinitionId,
			OrderByComparator<ObjectAction> orderByComparator)
		throws NoSuchObjectActionException {

		ObjectAction objectAction = findByPrimaryKey(objectActionId);

		Session session = null;

		try {
			session = openSession();

			ObjectAction[] array = new ObjectActionImpl[3];

			array[0] = getByObjectDefinitionId_PrevAndNext(
				session, objectAction, objectDefinitionId, orderByComparator,
				true);

			array[1] = objectAction;

			array[2] = getByObjectDefinitionId_PrevAndNext(
				session, objectAction, objectDefinitionId, orderByComparator,
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

	protected ObjectAction getByObjectDefinitionId_PrevAndNext(
		Session session, ObjectAction objectAction, long objectDefinitionId,
		OrderByComparator<ObjectAction> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_OBJECTACTION_WHERE);

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
			sb.append(ObjectActionModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(objectDefinitionId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(objectAction)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectAction> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object actions where objectDefinitionId = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 */
	@Override
	public void removeByObjectDefinitionId(long objectDefinitionId) {
		for (ObjectAction objectAction :
				findByObjectDefinitionId(
					objectDefinitionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(objectAction);
		}
	}

	/**
	 * Returns the number of object actions where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object actions
	 */
	@Override
	public int countByObjectDefinitionId(long objectDefinitionId) {
		FinderPath finderPath = _finderPathCountByObjectDefinitionId;

		Object[] finderArgs = new Object[] {objectDefinitionId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_OBJECTACTION_WHERE);

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
			"objectAction.objectDefinitionId = ?";

	private FinderPath _finderPathFetchByODI_N;

	/**
	 * Returns the object action where objectDefinitionId = &#63; and name = &#63; or throws a <code>NoSuchObjectActionException</code> if it could not be found.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	@Override
	public ObjectAction findByODI_N(long objectDefinitionId, String name)
		throws NoSuchObjectActionException {

		ObjectAction objectAction = fetchByODI_N(objectDefinitionId, name);

		if (objectAction == null) {
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

			throw new NoSuchObjectActionException(sb.toString());
		}

		return objectAction;
	}

	/**
	 * Returns the object action where objectDefinitionId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the matching object action, or <code>null</code> if a matching object action could not be found
	 */
	@Override
	public ObjectAction fetchByODI_N(long objectDefinitionId, String name) {
		return fetchByODI_N(objectDefinitionId, name, true);
	}

	/**
	 * Returns the object action where objectDefinitionId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object action, or <code>null</code> if a matching object action could not be found
	 */
	@Override
	public ObjectAction fetchByODI_N(
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

		if (result instanceof ObjectAction) {
			ObjectAction objectAction = (ObjectAction)result;

			if ((objectDefinitionId != objectAction.getObjectDefinitionId()) ||
				!Objects.equals(name, objectAction.getName())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_OBJECTACTION_WHERE);

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

				List<ObjectAction> list = query.list();

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
								"ObjectActionPersistenceImpl.fetchByODI_N(long, String, boolean) with parameters (" +
									StringUtil.merge(finderArgs) +
										") yields a result set with more than 1 result. This violates the logical unique restriction. There is no order guarantee on which result is returned by this finder.");
						}
					}

					ObjectAction objectAction = list.get(0);

					result = objectAction;

					cacheResult(objectAction);
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
			return (ObjectAction)result;
		}
	}

	/**
	 * Removes the object action where objectDefinitionId = &#63; and name = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the object action that was removed
	 */
	@Override
	public ObjectAction removeByODI_N(long objectDefinitionId, String name)
		throws NoSuchObjectActionException {

		ObjectAction objectAction = findByODI_N(objectDefinitionId, name);

		return remove(objectAction);
	}

	/**
	 * Returns the number of object actions where objectDefinitionId = &#63; and name = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the number of matching object actions
	 */
	@Override
	public int countByODI_N(long objectDefinitionId, String name) {
		ObjectAction objectAction = fetchByODI_N(objectDefinitionId, name);

		if (objectAction == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ODI_N_OBJECTDEFINITIONID_2 =
		"objectAction.objectDefinitionId = ? AND ";

	private static final String _FINDER_COLUMN_ODI_N_NAME_2 =
		"objectAction.name = ?";

	private static final String _FINDER_COLUMN_ODI_N_NAME_3 =
		"(objectAction.name IS NULL OR objectAction.name = '')";

	private FinderPath _finderPathWithPaginationFindByA_OAEK;
	private FinderPath _finderPathWithoutPaginationFindByA_OAEK;
	private FinderPath _finderPathCountByA_OAEK;

	/**
	 * Returns all the object actions where active = &#63; and objectActionExecutorKey = &#63;.
	 *
	 * @param active the active
	 * @param objectActionExecutorKey the object action executor key
	 * @return the matching object actions
	 */
	@Override
	public List<ObjectAction> findByA_OAEK(
		boolean active, String objectActionExecutorKey) {

		return findByA_OAEK(
			active, objectActionExecutorKey, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object actions where active = &#63; and objectActionExecutorKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param objectActionExecutorKey the object action executor key
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @return the range of matching object actions
	 */
	@Override
	public List<ObjectAction> findByA_OAEK(
		boolean active, String objectActionExecutorKey, int start, int end) {

		return findByA_OAEK(active, objectActionExecutorKey, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object actions where active = &#63; and objectActionExecutorKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param objectActionExecutorKey the object action executor key
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object actions
	 */
	@Override
	public List<ObjectAction> findByA_OAEK(
		boolean active, String objectActionExecutorKey, int start, int end,
		OrderByComparator<ObjectAction> orderByComparator) {

		return findByA_OAEK(
			active, objectActionExecutorKey, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the object actions where active = &#63; and objectActionExecutorKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param objectActionExecutorKey the object action executor key
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object actions
	 */
	@Override
	public List<ObjectAction> findByA_OAEK(
		boolean active, String objectActionExecutorKey, int start, int end,
		OrderByComparator<ObjectAction> orderByComparator,
		boolean useFinderCache) {

		objectActionExecutorKey = Objects.toString(objectActionExecutorKey, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByA_OAEK;
				finderArgs = new Object[] {active, objectActionExecutorKey};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByA_OAEK;
			finderArgs = new Object[] {
				active, objectActionExecutorKey, start, end, orderByComparator
			};
		}

		List<ObjectAction> list = null;

		if (useFinderCache) {
			list = (List<ObjectAction>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectAction objectAction : list) {
					if ((active != objectAction.isActive()) ||
						!objectActionExecutorKey.equals(
							objectAction.getObjectActionExecutorKey())) {

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

			sb.append(_SQL_SELECT_OBJECTACTION_WHERE);

			sb.append(_FINDER_COLUMN_A_OAEK_ACTIVE_2);

			boolean bindObjectActionExecutorKey = false;

			if (objectActionExecutorKey.isEmpty()) {
				sb.append(_FINDER_COLUMN_A_OAEK_OBJECTACTIONEXECUTORKEY_3);
			}
			else {
				bindObjectActionExecutorKey = true;

				sb.append(_FINDER_COLUMN_A_OAEK_OBJECTACTIONEXECUTORKEY_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectActionModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(active);

				if (bindObjectActionExecutorKey) {
					queryPos.add(objectActionExecutorKey);
				}

				list = (List<ObjectAction>)QueryUtil.list(
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
	 * Returns the first object action in the ordered set where active = &#63; and objectActionExecutorKey = &#63;.
	 *
	 * @param active the active
	 * @param objectActionExecutorKey the object action executor key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	@Override
	public ObjectAction findByA_OAEK_First(
			boolean active, String objectActionExecutorKey,
			OrderByComparator<ObjectAction> orderByComparator)
		throws NoSuchObjectActionException {

		ObjectAction objectAction = fetchByA_OAEK_First(
			active, objectActionExecutorKey, orderByComparator);

		if (objectAction != null) {
			return objectAction;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("active=");
		sb.append(active);

		sb.append(", objectActionExecutorKey=");
		sb.append(objectActionExecutorKey);

		sb.append("}");

		throw new NoSuchObjectActionException(sb.toString());
	}

	/**
	 * Returns the first object action in the ordered set where active = &#63; and objectActionExecutorKey = &#63;.
	 *
	 * @param active the active
	 * @param objectActionExecutorKey the object action executor key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action, or <code>null</code> if a matching object action could not be found
	 */
	@Override
	public ObjectAction fetchByA_OAEK_First(
		boolean active, String objectActionExecutorKey,
		OrderByComparator<ObjectAction> orderByComparator) {

		List<ObjectAction> list = findByA_OAEK(
			active, objectActionExecutorKey, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object action in the ordered set where active = &#63; and objectActionExecutorKey = &#63;.
	 *
	 * @param active the active
	 * @param objectActionExecutorKey the object action executor key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	@Override
	public ObjectAction findByA_OAEK_Last(
			boolean active, String objectActionExecutorKey,
			OrderByComparator<ObjectAction> orderByComparator)
		throws NoSuchObjectActionException {

		ObjectAction objectAction = fetchByA_OAEK_Last(
			active, objectActionExecutorKey, orderByComparator);

		if (objectAction != null) {
			return objectAction;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("active=");
		sb.append(active);

		sb.append(", objectActionExecutorKey=");
		sb.append(objectActionExecutorKey);

		sb.append("}");

		throw new NoSuchObjectActionException(sb.toString());
	}

	/**
	 * Returns the last object action in the ordered set where active = &#63; and objectActionExecutorKey = &#63;.
	 *
	 * @param active the active
	 * @param objectActionExecutorKey the object action executor key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object action, or <code>null</code> if a matching object action could not be found
	 */
	@Override
	public ObjectAction fetchByA_OAEK_Last(
		boolean active, String objectActionExecutorKey,
		OrderByComparator<ObjectAction> orderByComparator) {

		int count = countByA_OAEK(active, objectActionExecutorKey);

		if (count == 0) {
			return null;
		}

		List<ObjectAction> list = findByA_OAEK(
			active, objectActionExecutorKey, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object actions before and after the current object action in the ordered set where active = &#63; and objectActionExecutorKey = &#63;.
	 *
	 * @param objectActionId the primary key of the current object action
	 * @param active the active
	 * @param objectActionExecutorKey the object action executor key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object action
	 * @throws NoSuchObjectActionException if a object action with the primary key could not be found
	 */
	@Override
	public ObjectAction[] findByA_OAEK_PrevAndNext(
			long objectActionId, boolean active, String objectActionExecutorKey,
			OrderByComparator<ObjectAction> orderByComparator)
		throws NoSuchObjectActionException {

		objectActionExecutorKey = Objects.toString(objectActionExecutorKey, "");

		ObjectAction objectAction = findByPrimaryKey(objectActionId);

		Session session = null;

		try {
			session = openSession();

			ObjectAction[] array = new ObjectActionImpl[3];

			array[0] = getByA_OAEK_PrevAndNext(
				session, objectAction, active, objectActionExecutorKey,
				orderByComparator, true);

			array[1] = objectAction;

			array[2] = getByA_OAEK_PrevAndNext(
				session, objectAction, active, objectActionExecutorKey,
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

	protected ObjectAction getByA_OAEK_PrevAndNext(
		Session session, ObjectAction objectAction, boolean active,
		String objectActionExecutorKey,
		OrderByComparator<ObjectAction> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_OBJECTACTION_WHERE);

		sb.append(_FINDER_COLUMN_A_OAEK_ACTIVE_2);

		boolean bindObjectActionExecutorKey = false;

		if (objectActionExecutorKey.isEmpty()) {
			sb.append(_FINDER_COLUMN_A_OAEK_OBJECTACTIONEXECUTORKEY_3);
		}
		else {
			bindObjectActionExecutorKey = true;

			sb.append(_FINDER_COLUMN_A_OAEK_OBJECTACTIONEXECUTORKEY_2);
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
			sb.append(ObjectActionModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(active);

		if (bindObjectActionExecutorKey) {
			queryPos.add(objectActionExecutorKey);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(objectAction)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectAction> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object actions where active = &#63; and objectActionExecutorKey = &#63; from the database.
	 *
	 * @param active the active
	 * @param objectActionExecutorKey the object action executor key
	 */
	@Override
	public void removeByA_OAEK(boolean active, String objectActionExecutorKey) {
		for (ObjectAction objectAction :
				findByA_OAEK(
					active, objectActionExecutorKey, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(objectAction);
		}
	}

	/**
	 * Returns the number of object actions where active = &#63; and objectActionExecutorKey = &#63;.
	 *
	 * @param active the active
	 * @param objectActionExecutorKey the object action executor key
	 * @return the number of matching object actions
	 */
	@Override
	public int countByA_OAEK(boolean active, String objectActionExecutorKey) {
		objectActionExecutorKey = Objects.toString(objectActionExecutorKey, "");

		FinderPath finderPath = _finderPathCountByA_OAEK;

		Object[] finderArgs = new Object[] {active, objectActionExecutorKey};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_OBJECTACTION_WHERE);

			sb.append(_FINDER_COLUMN_A_OAEK_ACTIVE_2);

			boolean bindObjectActionExecutorKey = false;

			if (objectActionExecutorKey.isEmpty()) {
				sb.append(_FINDER_COLUMN_A_OAEK_OBJECTACTIONEXECUTORKEY_3);
			}
			else {
				bindObjectActionExecutorKey = true;

				sb.append(_FINDER_COLUMN_A_OAEK_OBJECTACTIONEXECUTORKEY_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(active);

				if (bindObjectActionExecutorKey) {
					queryPos.add(objectActionExecutorKey);
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

	private static final String _FINDER_COLUMN_A_OAEK_ACTIVE_2 =
		"objectAction.active = ? AND ";

	private static final String
		_FINDER_COLUMN_A_OAEK_OBJECTACTIONEXECUTORKEY_2 =
			"objectAction.objectActionExecutorKey = ?";

	private static final String
		_FINDER_COLUMN_A_OAEK_OBJECTACTIONEXECUTORKEY_3 =
			"(objectAction.objectActionExecutorKey IS NULL OR objectAction.objectActionExecutorKey = '')";

	private FinderPath _finderPathFetchByERC_C_ODI;

	/**
	 * Returns the object action where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63; or throws a <code>NoSuchObjectActionException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	@Override
	public ObjectAction findByERC_C_ODI(
			String externalReferenceCode, long companyId,
			long objectDefinitionId)
		throws NoSuchObjectActionException {

		ObjectAction objectAction = fetchByERC_C_ODI(
			externalReferenceCode, companyId, objectDefinitionId);

		if (objectAction == null) {
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

			throw new NoSuchObjectActionException(sb.toString());
		}

		return objectAction;
	}

	/**
	 * Returns the object action where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object action, or <code>null</code> if a matching object action could not be found
	 */
	@Override
	public ObjectAction fetchByERC_C_ODI(
		String externalReferenceCode, long companyId, long objectDefinitionId) {

		return fetchByERC_C_ODI(
			externalReferenceCode, companyId, objectDefinitionId, true);
	}

	/**
	 * Returns the object action where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object action, or <code>null</code> if a matching object action could not be found
	 */
	@Override
	public ObjectAction fetchByERC_C_ODI(
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

		if (result instanceof ObjectAction) {
			ObjectAction objectAction = (ObjectAction)result;

			if (!Objects.equals(
					externalReferenceCode,
					objectAction.getExternalReferenceCode()) ||
				(companyId != objectAction.getCompanyId()) ||
				(objectDefinitionId != objectAction.getObjectDefinitionId())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(5);

			sb.append(_SQL_SELECT_OBJECTACTION_WHERE);

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

				List<ObjectAction> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByERC_C_ODI, finderArgs, list);
					}
				}
				else {
					ObjectAction objectAction = list.get(0);

					result = objectAction;

					cacheResult(objectAction);
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
			return (ObjectAction)result;
		}
	}

	/**
	 * Removes the object action where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the object action that was removed
	 */
	@Override
	public ObjectAction removeByERC_C_ODI(
			String externalReferenceCode, long companyId,
			long objectDefinitionId)
		throws NoSuchObjectActionException {

		ObjectAction objectAction = findByERC_C_ODI(
			externalReferenceCode, companyId, objectDefinitionId);

		return remove(objectAction);
	}

	/**
	 * Returns the number of object actions where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object actions
	 */
	@Override
	public int countByERC_C_ODI(
		String externalReferenceCode, long companyId, long objectDefinitionId) {

		ObjectAction objectAction = fetchByERC_C_ODI(
			externalReferenceCode, companyId, objectDefinitionId);

		if (objectAction == null) {
			return 0;
		}

		return 1;
	}

	private static final String
		_FINDER_COLUMN_ERC_C_ODI_EXTERNALREFERENCECODE_2 =
			"objectAction.externalReferenceCode = ? AND ";

	private static final String
		_FINDER_COLUMN_ERC_C_ODI_EXTERNALREFERENCECODE_3 =
			"(objectAction.externalReferenceCode IS NULL OR objectAction.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_C_ODI_COMPANYID_2 =
		"objectAction.companyId = ? AND ";

	private static final String _FINDER_COLUMN_ERC_C_ODI_OBJECTDEFINITIONID_2 =
		"objectAction.objectDefinitionId = ?";

	private FinderPath _finderPathWithPaginationFindByC_A_OATK;
	private FinderPath _finderPathWithoutPaginationFindByC_A_OATK;
	private FinderPath _finderPathCountByC_A_OATK;

	/**
	 * Returns all the object actions where companyId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @return the matching object actions
	 */
	@Override
	public List<ObjectAction> findByC_A_OATK(
		long companyId, boolean active, String objectActionTriggerKey) {

		return findByC_A_OATK(
			companyId, active, objectActionTriggerKey, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object actions where companyId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @return the range of matching object actions
	 */
	@Override
	public List<ObjectAction> findByC_A_OATK(
		long companyId, boolean active, String objectActionTriggerKey,
		int start, int end) {

		return findByC_A_OATK(
			companyId, active, objectActionTriggerKey, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object actions where companyId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object actions
	 */
	@Override
	public List<ObjectAction> findByC_A_OATK(
		long companyId, boolean active, String objectActionTriggerKey,
		int start, int end, OrderByComparator<ObjectAction> orderByComparator) {

		return findByC_A_OATK(
			companyId, active, objectActionTriggerKey, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object actions where companyId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object actions
	 */
	@Override
	public List<ObjectAction> findByC_A_OATK(
		long companyId, boolean active, String objectActionTriggerKey,
		int start, int end, OrderByComparator<ObjectAction> orderByComparator,
		boolean useFinderCache) {

		objectActionTriggerKey = Objects.toString(objectActionTriggerKey, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByC_A_OATK;
				finderArgs = new Object[] {
					companyId, active, objectActionTriggerKey
				};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByC_A_OATK;
			finderArgs = new Object[] {
				companyId, active, objectActionTriggerKey, start, end,
				orderByComparator
			};
		}

		List<ObjectAction> list = null;

		if (useFinderCache) {
			list = (List<ObjectAction>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectAction objectAction : list) {
					if ((companyId != objectAction.getCompanyId()) ||
						(active != objectAction.isActive()) ||
						!objectActionTriggerKey.equals(
							objectAction.getObjectActionTriggerKey())) {

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

			sb.append(_SQL_SELECT_OBJECTACTION_WHERE);

			sb.append(_FINDER_COLUMN_C_A_OATK_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_A_OATK_ACTIVE_2);

			boolean bindObjectActionTriggerKey = false;

			if (objectActionTriggerKey.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_A_OATK_OBJECTACTIONTRIGGERKEY_3);
			}
			else {
				bindObjectActionTriggerKey = true;

				sb.append(_FINDER_COLUMN_C_A_OATK_OBJECTACTIONTRIGGERKEY_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectActionModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(active);

				if (bindObjectActionTriggerKey) {
					queryPos.add(objectActionTriggerKey);
				}

				list = (List<ObjectAction>)QueryUtil.list(
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
	 * Returns the first object action in the ordered set where companyId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	@Override
	public ObjectAction findByC_A_OATK_First(
			long companyId, boolean active, String objectActionTriggerKey,
			OrderByComparator<ObjectAction> orderByComparator)
		throws NoSuchObjectActionException {

		ObjectAction objectAction = fetchByC_A_OATK_First(
			companyId, active, objectActionTriggerKey, orderByComparator);

		if (objectAction != null) {
			return objectAction;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", active=");
		sb.append(active);

		sb.append(", objectActionTriggerKey=");
		sb.append(objectActionTriggerKey);

		sb.append("}");

		throw new NoSuchObjectActionException(sb.toString());
	}

	/**
	 * Returns the first object action in the ordered set where companyId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action, or <code>null</code> if a matching object action could not be found
	 */
	@Override
	public ObjectAction fetchByC_A_OATK_First(
		long companyId, boolean active, String objectActionTriggerKey,
		OrderByComparator<ObjectAction> orderByComparator) {

		List<ObjectAction> list = findByC_A_OATK(
			companyId, active, objectActionTriggerKey, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object action in the ordered set where companyId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	@Override
	public ObjectAction findByC_A_OATK_Last(
			long companyId, boolean active, String objectActionTriggerKey,
			OrderByComparator<ObjectAction> orderByComparator)
		throws NoSuchObjectActionException {

		ObjectAction objectAction = fetchByC_A_OATK_Last(
			companyId, active, objectActionTriggerKey, orderByComparator);

		if (objectAction != null) {
			return objectAction;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", active=");
		sb.append(active);

		sb.append(", objectActionTriggerKey=");
		sb.append(objectActionTriggerKey);

		sb.append("}");

		throw new NoSuchObjectActionException(sb.toString());
	}

	/**
	 * Returns the last object action in the ordered set where companyId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object action, or <code>null</code> if a matching object action could not be found
	 */
	@Override
	public ObjectAction fetchByC_A_OATK_Last(
		long companyId, boolean active, String objectActionTriggerKey,
		OrderByComparator<ObjectAction> orderByComparator) {

		int count = countByC_A_OATK(companyId, active, objectActionTriggerKey);

		if (count == 0) {
			return null;
		}

		List<ObjectAction> list = findByC_A_OATK(
			companyId, active, objectActionTriggerKey, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object actions before and after the current object action in the ordered set where companyId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param objectActionId the primary key of the current object action
	 * @param companyId the company ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object action
	 * @throws NoSuchObjectActionException if a object action with the primary key could not be found
	 */
	@Override
	public ObjectAction[] findByC_A_OATK_PrevAndNext(
			long objectActionId, long companyId, boolean active,
			String objectActionTriggerKey,
			OrderByComparator<ObjectAction> orderByComparator)
		throws NoSuchObjectActionException {

		objectActionTriggerKey = Objects.toString(objectActionTriggerKey, "");

		ObjectAction objectAction = findByPrimaryKey(objectActionId);

		Session session = null;

		try {
			session = openSession();

			ObjectAction[] array = new ObjectActionImpl[3];

			array[0] = getByC_A_OATK_PrevAndNext(
				session, objectAction, companyId, active,
				objectActionTriggerKey, orderByComparator, true);

			array[1] = objectAction;

			array[2] = getByC_A_OATK_PrevAndNext(
				session, objectAction, companyId, active,
				objectActionTriggerKey, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected ObjectAction getByC_A_OATK_PrevAndNext(
		Session session, ObjectAction objectAction, long companyId,
		boolean active, String objectActionTriggerKey,
		OrderByComparator<ObjectAction> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_OBJECTACTION_WHERE);

		sb.append(_FINDER_COLUMN_C_A_OATK_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_A_OATK_ACTIVE_2);

		boolean bindObjectActionTriggerKey = false;

		if (objectActionTriggerKey.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_A_OATK_OBJECTACTIONTRIGGERKEY_3);
		}
		else {
			bindObjectActionTriggerKey = true;

			sb.append(_FINDER_COLUMN_C_A_OATK_OBJECTACTIONTRIGGERKEY_2);
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
			sb.append(ObjectActionModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(active);

		if (bindObjectActionTriggerKey) {
			queryPos.add(objectActionTriggerKey);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(objectAction)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectAction> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object actions where companyId = &#63; and active = &#63; and objectActionTriggerKey = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 */
	@Override
	public void removeByC_A_OATK(
		long companyId, boolean active, String objectActionTriggerKey) {

		for (ObjectAction objectAction :
				findByC_A_OATK(
					companyId, active, objectActionTriggerKey,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(objectAction);
		}
	}

	/**
	 * Returns the number of object actions where companyId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @return the number of matching object actions
	 */
	@Override
	public int countByC_A_OATK(
		long companyId, boolean active, String objectActionTriggerKey) {

		objectActionTriggerKey = Objects.toString(objectActionTriggerKey, "");

		FinderPath finderPath = _finderPathCountByC_A_OATK;

		Object[] finderArgs = new Object[] {
			companyId, active, objectActionTriggerKey
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_COUNT_OBJECTACTION_WHERE);

			sb.append(_FINDER_COLUMN_C_A_OATK_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_A_OATK_ACTIVE_2);

			boolean bindObjectActionTriggerKey = false;

			if (objectActionTriggerKey.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_A_OATK_OBJECTACTIONTRIGGERKEY_3);
			}
			else {
				bindObjectActionTriggerKey = true;

				sb.append(_FINDER_COLUMN_C_A_OATK_OBJECTACTIONTRIGGERKEY_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(active);

				if (bindObjectActionTriggerKey) {
					queryPos.add(objectActionTriggerKey);
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

	private static final String _FINDER_COLUMN_C_A_OATK_COMPANYID_2 =
		"objectAction.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_A_OATK_ACTIVE_2 =
		"objectAction.active = ? AND ";

	private static final String
		_FINDER_COLUMN_C_A_OATK_OBJECTACTIONTRIGGERKEY_2 =
			"objectAction.objectActionTriggerKey = ?";

	private static final String
		_FINDER_COLUMN_C_A_OATK_OBJECTACTIONTRIGGERKEY_3 =
			"(objectAction.objectActionTriggerKey IS NULL OR objectAction.objectActionTriggerKey = '')";

	private FinderPath _finderPathWithPaginationFindByO_A_OATK;
	private FinderPath _finderPathWithoutPaginationFindByO_A_OATK;
	private FinderPath _finderPathCountByO_A_OATK;

	/**
	 * Returns all the object actions where objectDefinitionId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @return the matching object actions
	 */
	@Override
	public List<ObjectAction> findByO_A_OATK(
		long objectDefinitionId, boolean active,
		String objectActionTriggerKey) {

		return findByO_A_OATK(
			objectDefinitionId, active, objectActionTriggerKey,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object actions where objectDefinitionId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @return the range of matching object actions
	 */
	@Override
	public List<ObjectAction> findByO_A_OATK(
		long objectDefinitionId, boolean active, String objectActionTriggerKey,
		int start, int end) {

		return findByO_A_OATK(
			objectDefinitionId, active, objectActionTriggerKey, start, end,
			null);
	}

	/**
	 * Returns an ordered range of all the object actions where objectDefinitionId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object actions
	 */
	@Override
	public List<ObjectAction> findByO_A_OATK(
		long objectDefinitionId, boolean active, String objectActionTriggerKey,
		int start, int end, OrderByComparator<ObjectAction> orderByComparator) {

		return findByO_A_OATK(
			objectDefinitionId, active, objectActionTriggerKey, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object actions where objectDefinitionId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object actions
	 */
	@Override
	public List<ObjectAction> findByO_A_OATK(
		long objectDefinitionId, boolean active, String objectActionTriggerKey,
		int start, int end, OrderByComparator<ObjectAction> orderByComparator,
		boolean useFinderCache) {

		objectActionTriggerKey = Objects.toString(objectActionTriggerKey, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByO_A_OATK;
				finderArgs = new Object[] {
					objectDefinitionId, active, objectActionTriggerKey
				};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByO_A_OATK;
			finderArgs = new Object[] {
				objectDefinitionId, active, objectActionTriggerKey, start, end,
				orderByComparator
			};
		}

		List<ObjectAction> list = null;

		if (useFinderCache) {
			list = (List<ObjectAction>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectAction objectAction : list) {
					if ((objectDefinitionId !=
							objectAction.getObjectDefinitionId()) ||
						(active != objectAction.isActive()) ||
						!objectActionTriggerKey.equals(
							objectAction.getObjectActionTriggerKey())) {

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

			sb.append(_SQL_SELECT_OBJECTACTION_WHERE);

			sb.append(_FINDER_COLUMN_O_A_OATK_OBJECTDEFINITIONID_2);

			sb.append(_FINDER_COLUMN_O_A_OATK_ACTIVE_2);

			boolean bindObjectActionTriggerKey = false;

			if (objectActionTriggerKey.isEmpty()) {
				sb.append(_FINDER_COLUMN_O_A_OATK_OBJECTACTIONTRIGGERKEY_3);
			}
			else {
				bindObjectActionTriggerKey = true;

				sb.append(_FINDER_COLUMN_O_A_OATK_OBJECTACTIONTRIGGERKEY_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectActionModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId);

				queryPos.add(active);

				if (bindObjectActionTriggerKey) {
					queryPos.add(objectActionTriggerKey);
				}

				list = (List<ObjectAction>)QueryUtil.list(
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
	 * Returns the first object action in the ordered set where objectDefinitionId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	@Override
	public ObjectAction findByO_A_OATK_First(
			long objectDefinitionId, boolean active,
			String objectActionTriggerKey,
			OrderByComparator<ObjectAction> orderByComparator)
		throws NoSuchObjectActionException {

		ObjectAction objectAction = fetchByO_A_OATK_First(
			objectDefinitionId, active, objectActionTriggerKey,
			orderByComparator);

		if (objectAction != null) {
			return objectAction;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append(", active=");
		sb.append(active);

		sb.append(", objectActionTriggerKey=");
		sb.append(objectActionTriggerKey);

		sb.append("}");

		throw new NoSuchObjectActionException(sb.toString());
	}

	/**
	 * Returns the first object action in the ordered set where objectDefinitionId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action, or <code>null</code> if a matching object action could not be found
	 */
	@Override
	public ObjectAction fetchByO_A_OATK_First(
		long objectDefinitionId, boolean active, String objectActionTriggerKey,
		OrderByComparator<ObjectAction> orderByComparator) {

		List<ObjectAction> list = findByO_A_OATK(
			objectDefinitionId, active, objectActionTriggerKey, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object action in the ordered set where objectDefinitionId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	@Override
	public ObjectAction findByO_A_OATK_Last(
			long objectDefinitionId, boolean active,
			String objectActionTriggerKey,
			OrderByComparator<ObjectAction> orderByComparator)
		throws NoSuchObjectActionException {

		ObjectAction objectAction = fetchByO_A_OATK_Last(
			objectDefinitionId, active, objectActionTriggerKey,
			orderByComparator);

		if (objectAction != null) {
			return objectAction;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append(", active=");
		sb.append(active);

		sb.append(", objectActionTriggerKey=");
		sb.append(objectActionTriggerKey);

		sb.append("}");

		throw new NoSuchObjectActionException(sb.toString());
	}

	/**
	 * Returns the last object action in the ordered set where objectDefinitionId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object action, or <code>null</code> if a matching object action could not be found
	 */
	@Override
	public ObjectAction fetchByO_A_OATK_Last(
		long objectDefinitionId, boolean active, String objectActionTriggerKey,
		OrderByComparator<ObjectAction> orderByComparator) {

		int count = countByO_A_OATK(
			objectDefinitionId, active, objectActionTriggerKey);

		if (count == 0) {
			return null;
		}

		List<ObjectAction> list = findByO_A_OATK(
			objectDefinitionId, active, objectActionTriggerKey, count - 1,
			count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object actions before and after the current object action in the ordered set where objectDefinitionId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param objectActionId the primary key of the current object action
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object action
	 * @throws NoSuchObjectActionException if a object action with the primary key could not be found
	 */
	@Override
	public ObjectAction[] findByO_A_OATK_PrevAndNext(
			long objectActionId, long objectDefinitionId, boolean active,
			String objectActionTriggerKey,
			OrderByComparator<ObjectAction> orderByComparator)
		throws NoSuchObjectActionException {

		objectActionTriggerKey = Objects.toString(objectActionTriggerKey, "");

		ObjectAction objectAction = findByPrimaryKey(objectActionId);

		Session session = null;

		try {
			session = openSession();

			ObjectAction[] array = new ObjectActionImpl[3];

			array[0] = getByO_A_OATK_PrevAndNext(
				session, objectAction, objectDefinitionId, active,
				objectActionTriggerKey, orderByComparator, true);

			array[1] = objectAction;

			array[2] = getByO_A_OATK_PrevAndNext(
				session, objectAction, objectDefinitionId, active,
				objectActionTriggerKey, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected ObjectAction getByO_A_OATK_PrevAndNext(
		Session session, ObjectAction objectAction, long objectDefinitionId,
		boolean active, String objectActionTriggerKey,
		OrderByComparator<ObjectAction> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_OBJECTACTION_WHERE);

		sb.append(_FINDER_COLUMN_O_A_OATK_OBJECTDEFINITIONID_2);

		sb.append(_FINDER_COLUMN_O_A_OATK_ACTIVE_2);

		boolean bindObjectActionTriggerKey = false;

		if (objectActionTriggerKey.isEmpty()) {
			sb.append(_FINDER_COLUMN_O_A_OATK_OBJECTACTIONTRIGGERKEY_3);
		}
		else {
			bindObjectActionTriggerKey = true;

			sb.append(_FINDER_COLUMN_O_A_OATK_OBJECTACTIONTRIGGERKEY_2);
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
			sb.append(ObjectActionModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(objectDefinitionId);

		queryPos.add(active);

		if (bindObjectActionTriggerKey) {
			queryPos.add(objectActionTriggerKey);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(objectAction)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectAction> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object actions where objectDefinitionId = &#63; and active = &#63; and objectActionTriggerKey = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 */
	@Override
	public void removeByO_A_OATK(
		long objectDefinitionId, boolean active,
		String objectActionTriggerKey) {

		for (ObjectAction objectAction :
				findByO_A_OATK(
					objectDefinitionId, active, objectActionTriggerKey,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(objectAction);
		}
	}

	/**
	 * Returns the number of object actions where objectDefinitionId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @return the number of matching object actions
	 */
	@Override
	public int countByO_A_OATK(
		long objectDefinitionId, boolean active,
		String objectActionTriggerKey) {

		objectActionTriggerKey = Objects.toString(objectActionTriggerKey, "");

		FinderPath finderPath = _finderPathCountByO_A_OATK;

		Object[] finderArgs = new Object[] {
			objectDefinitionId, active, objectActionTriggerKey
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_COUNT_OBJECTACTION_WHERE);

			sb.append(_FINDER_COLUMN_O_A_OATK_OBJECTDEFINITIONID_2);

			sb.append(_FINDER_COLUMN_O_A_OATK_ACTIVE_2);

			boolean bindObjectActionTriggerKey = false;

			if (objectActionTriggerKey.isEmpty()) {
				sb.append(_FINDER_COLUMN_O_A_OATK_OBJECTACTIONTRIGGERKEY_3);
			}
			else {
				bindObjectActionTriggerKey = true;

				sb.append(_FINDER_COLUMN_O_A_OATK_OBJECTACTIONTRIGGERKEY_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId);

				queryPos.add(active);

				if (bindObjectActionTriggerKey) {
					queryPos.add(objectActionTriggerKey);
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

	private static final String _FINDER_COLUMN_O_A_OATK_OBJECTDEFINITIONID_2 =
		"objectAction.objectDefinitionId = ? AND ";

	private static final String _FINDER_COLUMN_O_A_OATK_ACTIVE_2 =
		"objectAction.active = ? AND ";

	private static final String
		_FINDER_COLUMN_O_A_OATK_OBJECTACTIONTRIGGERKEY_2 =
			"objectAction.objectActionTriggerKey = ?";

	private static final String
		_FINDER_COLUMN_O_A_OATK_OBJECTACTIONTRIGGERKEY_3 =
			"(objectAction.objectActionTriggerKey IS NULL OR objectAction.objectActionTriggerKey = '')";

	private FinderPath _finderPathFetchByODI_A_N_OATK;

	/**
	 * Returns the object action where objectDefinitionId = &#63; and active = &#63; and name = &#63; and objectActionTriggerKey = &#63; or throws a <code>NoSuchObjectActionException</code> if it could not be found.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param name the name
	 * @param objectActionTriggerKey the object action trigger key
	 * @return the matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	@Override
	public ObjectAction findByODI_A_N_OATK(
			long objectDefinitionId, boolean active, String name,
			String objectActionTriggerKey)
		throws NoSuchObjectActionException {

		ObjectAction objectAction = fetchByODI_A_N_OATK(
			objectDefinitionId, active, name, objectActionTriggerKey);

		if (objectAction == null) {
			StringBundler sb = new StringBundler(10);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("objectDefinitionId=");
			sb.append(objectDefinitionId);

			sb.append(", active=");
			sb.append(active);

			sb.append(", name=");
			sb.append(name);

			sb.append(", objectActionTriggerKey=");
			sb.append(objectActionTriggerKey);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchObjectActionException(sb.toString());
		}

		return objectAction;
	}

	/**
	 * Returns the object action where objectDefinitionId = &#63; and active = &#63; and name = &#63; and objectActionTriggerKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param name the name
	 * @param objectActionTriggerKey the object action trigger key
	 * @return the matching object action, or <code>null</code> if a matching object action could not be found
	 */
	@Override
	public ObjectAction fetchByODI_A_N_OATK(
		long objectDefinitionId, boolean active, String name,
		String objectActionTriggerKey) {

		return fetchByODI_A_N_OATK(
			objectDefinitionId, active, name, objectActionTriggerKey, true);
	}

	/**
	 * Returns the object action where objectDefinitionId = &#63; and active = &#63; and name = &#63; and objectActionTriggerKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param name the name
	 * @param objectActionTriggerKey the object action trigger key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object action, or <code>null</code> if a matching object action could not be found
	 */
	@Override
	public ObjectAction fetchByODI_A_N_OATK(
		long objectDefinitionId, boolean active, String name,
		String objectActionTriggerKey, boolean useFinderCache) {

		name = Objects.toString(name, "");
		objectActionTriggerKey = Objects.toString(objectActionTriggerKey, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {
				objectDefinitionId, active, name, objectActionTriggerKey
			};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByODI_A_N_OATK, finderArgs, this);
		}

		if (result instanceof ObjectAction) {
			ObjectAction objectAction = (ObjectAction)result;

			if ((objectDefinitionId != objectAction.getObjectDefinitionId()) ||
				(active != objectAction.isActive()) ||
				!Objects.equals(name, objectAction.getName()) ||
				!Objects.equals(
					objectActionTriggerKey,
					objectAction.getObjectActionTriggerKey())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_SQL_SELECT_OBJECTACTION_WHERE);

			sb.append(_FINDER_COLUMN_ODI_A_N_OATK_OBJECTDEFINITIONID_2);

			sb.append(_FINDER_COLUMN_ODI_A_N_OATK_ACTIVE_2);

			boolean bindName = false;

			if (name.isEmpty()) {
				sb.append(_FINDER_COLUMN_ODI_A_N_OATK_NAME_3);
			}
			else {
				bindName = true;

				sb.append(_FINDER_COLUMN_ODI_A_N_OATK_NAME_2);
			}

			boolean bindObjectActionTriggerKey = false;

			if (objectActionTriggerKey.isEmpty()) {
				sb.append(_FINDER_COLUMN_ODI_A_N_OATK_OBJECTACTIONTRIGGERKEY_3);
			}
			else {
				bindObjectActionTriggerKey = true;

				sb.append(_FINDER_COLUMN_ODI_A_N_OATK_OBJECTACTIONTRIGGERKEY_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId);

				queryPos.add(active);

				if (bindName) {
					queryPos.add(name);
				}

				if (bindObjectActionTriggerKey) {
					queryPos.add(objectActionTriggerKey);
				}

				List<ObjectAction> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByODI_A_N_OATK, finderArgs, list);
					}
				}
				else {
					if (list.size() > 1) {
						Collections.sort(list, Collections.reverseOrder());

						if (_log.isWarnEnabled()) {
							if (!useFinderCache) {
								finderArgs = new Object[] {
									objectDefinitionId, active, name,
									objectActionTriggerKey
								};
							}

							_log.warn(
								"ObjectActionPersistenceImpl.fetchByODI_A_N_OATK(long, boolean, String, String, boolean) with parameters (" +
									StringUtil.merge(finderArgs) +
										") yields a result set with more than 1 result. This violates the logical unique restriction. There is no order guarantee on which result is returned by this finder.");
						}
					}

					ObjectAction objectAction = list.get(0);

					result = objectAction;

					cacheResult(objectAction);
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
			return (ObjectAction)result;
		}
	}

	/**
	 * Removes the object action where objectDefinitionId = &#63; and active = &#63; and name = &#63; and objectActionTriggerKey = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param name the name
	 * @param objectActionTriggerKey the object action trigger key
	 * @return the object action that was removed
	 */
	@Override
	public ObjectAction removeByODI_A_N_OATK(
			long objectDefinitionId, boolean active, String name,
			String objectActionTriggerKey)
		throws NoSuchObjectActionException {

		ObjectAction objectAction = findByODI_A_N_OATK(
			objectDefinitionId, active, name, objectActionTriggerKey);

		return remove(objectAction);
	}

	/**
	 * Returns the number of object actions where objectDefinitionId = &#63; and active = &#63; and name = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param name the name
	 * @param objectActionTriggerKey the object action trigger key
	 * @return the number of matching object actions
	 */
	@Override
	public int countByODI_A_N_OATK(
		long objectDefinitionId, boolean active, String name,
		String objectActionTriggerKey) {

		ObjectAction objectAction = fetchByODI_A_N_OATK(
			objectDefinitionId, active, name, objectActionTriggerKey);

		if (objectAction == null) {
			return 0;
		}

		return 1;
	}

	private static final String
		_FINDER_COLUMN_ODI_A_N_OATK_OBJECTDEFINITIONID_2 =
			"objectAction.objectDefinitionId = ? AND ";

	private static final String _FINDER_COLUMN_ODI_A_N_OATK_ACTIVE_2 =
		"objectAction.active = ? AND ";

	private static final String _FINDER_COLUMN_ODI_A_N_OATK_NAME_2 =
		"objectAction.name = ? AND ";

	private static final String _FINDER_COLUMN_ODI_A_N_OATK_NAME_3 =
		"(objectAction.name IS NULL OR objectAction.name = '') AND ";

	private static final String
		_FINDER_COLUMN_ODI_A_N_OATK_OBJECTACTIONTRIGGERKEY_2 =
			"objectAction.objectActionTriggerKey = ?";

	private static final String
		_FINDER_COLUMN_ODI_A_N_OATK_OBJECTACTIONTRIGGERKEY_3 =
			"(objectAction.objectActionTriggerKey IS NULL OR objectAction.objectActionTriggerKey = '')";

	public ObjectActionPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("active", "active_");
		dbColumnNames.put("system", "system_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ObjectAction.class);

		setModelImplClass(ObjectActionImpl.class);
		setModelPKClass(long.class);

		setTable(ObjectActionTable.INSTANCE);
	}

	/**
	 * Caches the object action in the entity cache if it is enabled.
	 *
	 * @param objectAction the object action
	 */
	@Override
	public void cacheResult(ObjectAction objectAction) {
		entityCache.putResult(
			ObjectActionImpl.class, objectAction.getPrimaryKey(), objectAction);

		finderCache.putResult(
			_finderPathFetchByODI_N,
			new Object[] {
				objectAction.getObjectDefinitionId(), objectAction.getName()
			},
			objectAction);

		finderCache.putResult(
			_finderPathFetchByERC_C_ODI,
			new Object[] {
				objectAction.getExternalReferenceCode(),
				objectAction.getCompanyId(),
				objectAction.getObjectDefinitionId()
			},
			objectAction);

		finderCache.putResult(
			_finderPathFetchByODI_A_N_OATK,
			new Object[] {
				objectAction.getObjectDefinitionId(), objectAction.isActive(),
				objectAction.getName(), objectAction.getObjectActionTriggerKey()
			},
			objectAction);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the object actions in the entity cache if it is enabled.
	 *
	 * @param objectActions the object actions
	 */
	@Override
	public void cacheResult(List<ObjectAction> objectActions) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (objectActions.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (ObjectAction objectAction : objectActions) {
			if (entityCache.getResult(
					ObjectActionImpl.class, objectAction.getPrimaryKey()) ==
						null) {

				cacheResult(objectAction);
			}
		}
	}

	/**
	 * Clears the cache for all object actions.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(ObjectActionImpl.class);

		finderCache.clearCache(ObjectActionImpl.class);
	}

	/**
	 * Clears the cache for the object action.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(ObjectAction objectAction) {
		entityCache.removeResult(ObjectActionImpl.class, objectAction);
	}

	@Override
	public void clearCache(List<ObjectAction> objectActions) {
		for (ObjectAction objectAction : objectActions) {
			entityCache.removeResult(ObjectActionImpl.class, objectAction);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(ObjectActionImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(ObjectActionImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		ObjectActionModelImpl objectActionModelImpl) {

		Object[] args = new Object[] {
			objectActionModelImpl.getObjectDefinitionId(),
			objectActionModelImpl.getName()
		};

		finderCache.putResult(
			_finderPathFetchByODI_N, args, objectActionModelImpl);

		args = new Object[] {
			objectActionModelImpl.getExternalReferenceCode(),
			objectActionModelImpl.getCompanyId(),
			objectActionModelImpl.getObjectDefinitionId()
		};

		finderCache.putResult(
			_finderPathFetchByERC_C_ODI, args, objectActionModelImpl);

		args = new Object[] {
			objectActionModelImpl.getObjectDefinitionId(),
			objectActionModelImpl.isActive(), objectActionModelImpl.getName(),
			objectActionModelImpl.getObjectActionTriggerKey()
		};

		finderCache.putResult(
			_finderPathFetchByODI_A_N_OATK, args, objectActionModelImpl);
	}

	/**
	 * Creates a new object action with the primary key. Does not add the object action to the database.
	 *
	 * @param objectActionId the primary key for the new object action
	 * @return the new object action
	 */
	@Override
	public ObjectAction create(long objectActionId) {
		ObjectAction objectAction = new ObjectActionImpl();

		objectAction.setNew(true);
		objectAction.setPrimaryKey(objectActionId);

		String uuid = PortalUUIDUtil.generate();

		objectAction.setUuid(uuid);

		objectAction.setCompanyId(CompanyThreadLocal.getCompanyId());

		return objectAction;
	}

	/**
	 * Removes the object action with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectActionId the primary key of the object action
	 * @return the object action that was removed
	 * @throws NoSuchObjectActionException if a object action with the primary key could not be found
	 */
	@Override
	public ObjectAction remove(long objectActionId)
		throws NoSuchObjectActionException {

		return remove((Serializable)objectActionId);
	}

	/**
	 * Removes the object action with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the object action
	 * @return the object action that was removed
	 * @throws NoSuchObjectActionException if a object action with the primary key could not be found
	 */
	@Override
	public ObjectAction remove(Serializable primaryKey)
		throws NoSuchObjectActionException {

		Session session = null;

		try {
			session = openSession();

			ObjectAction objectAction = (ObjectAction)session.get(
				ObjectActionImpl.class, primaryKey);

			if (objectAction == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchObjectActionException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(objectAction);
		}
		catch (NoSuchObjectActionException noSuchEntityException) {
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
	protected ObjectAction removeImpl(ObjectAction objectAction) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(objectAction)) {
				objectAction = (ObjectAction)session.get(
					ObjectActionImpl.class, objectAction.getPrimaryKeyObj());
			}

			if (objectAction != null) {
				session.delete(objectAction);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (objectAction != null) {
			clearCache(objectAction);
		}

		return objectAction;
	}

	@Override
	public ObjectAction updateImpl(ObjectAction objectAction) {
		boolean isNew = objectAction.isNew();

		if (!(objectAction instanceof ObjectActionModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(objectAction.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					objectAction);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in objectAction proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ObjectAction implementation " +
					objectAction.getClass());
		}

		ObjectActionModelImpl objectActionModelImpl =
			(ObjectActionModelImpl)objectAction;

		if (Validator.isNull(objectAction.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			objectAction.setUuid(uuid);
		}

		if (Validator.isNull(objectAction.getExternalReferenceCode())) {
			objectAction.setExternalReferenceCode(objectAction.getUuid());
		}
		else {
			if (!Objects.equals(
					objectActionModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					objectAction.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = objectAction.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = objectAction.getPrimaryKey();
					}

					try {
						objectAction.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								ObjectAction.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								objectAction.getExternalReferenceCode(), null));
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

		if (isNew && (objectAction.getCreateDate() == null)) {
			if (serviceContext == null) {
				objectAction.setCreateDate(date);
			}
			else {
				objectAction.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!objectActionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				objectAction.setModifiedDate(date);
			}
			else {
				objectAction.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(objectAction);
			}
			else {
				objectAction = (ObjectAction)session.merge(objectAction);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			ObjectActionImpl.class, objectActionModelImpl, false, true);

		cacheUniqueFindersCache(objectActionModelImpl);

		if (isNew) {
			objectAction.setNew(false);
		}

		objectAction.resetOriginalValues();

		return objectAction;
	}

	/**
	 * Returns the object action with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the object action
	 * @return the object action
	 * @throws NoSuchObjectActionException if a object action with the primary key could not be found
	 */
	@Override
	public ObjectAction findByPrimaryKey(Serializable primaryKey)
		throws NoSuchObjectActionException {

		ObjectAction objectAction = fetchByPrimaryKey(primaryKey);

		if (objectAction == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchObjectActionException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return objectAction;
	}

	/**
	 * Returns the object action with the primary key or throws a <code>NoSuchObjectActionException</code> if it could not be found.
	 *
	 * @param objectActionId the primary key of the object action
	 * @return the object action
	 * @throws NoSuchObjectActionException if a object action with the primary key could not be found
	 */
	@Override
	public ObjectAction findByPrimaryKey(long objectActionId)
		throws NoSuchObjectActionException {

		return findByPrimaryKey((Serializable)objectActionId);
	}

	/**
	 * Returns the object action with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectActionId the primary key of the object action
	 * @return the object action, or <code>null</code> if a object action with the primary key could not be found
	 */
	@Override
	public ObjectAction fetchByPrimaryKey(long objectActionId) {
		return fetchByPrimaryKey((Serializable)objectActionId);
	}

	/**
	 * Returns all the object actions.
	 *
	 * @return the object actions
	 */
	@Override
	public List<ObjectAction> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object actions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @return the range of object actions
	 */
	@Override
	public List<ObjectAction> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the object actions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of object actions
	 */
	@Override
	public List<ObjectAction> findAll(
		int start, int end, OrderByComparator<ObjectAction> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object actions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of object actions
	 */
	@Override
	public List<ObjectAction> findAll(
		int start, int end, OrderByComparator<ObjectAction> orderByComparator,
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

		List<ObjectAction> list = null;

		if (useFinderCache) {
			list = (List<ObjectAction>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_OBJECTACTION);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_OBJECTACTION;

				sql = sql.concat(ObjectActionModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<ObjectAction>)QueryUtil.list(
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
	 * Removes all the object actions from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (ObjectAction objectAction : findAll()) {
			remove(objectAction);
		}
	}

	/**
	 * Returns the number of object actions.
	 *
	 * @return the number of object actions
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_OBJECTACTION);

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
		return "objectActionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OBJECTACTION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ObjectActionModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the object action persistence.
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

		_finderPathFetchByODI_N = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByODI_N",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"objectDefinitionId", "name"}, true);

		_finderPathWithPaginationFindByA_OAEK = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByA_OAEK",
			new String[] {
				Boolean.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"active_", "objectActionExecutorKey"}, true);

		_finderPathWithoutPaginationFindByA_OAEK = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByA_OAEK",
			new String[] {Boolean.class.getName(), String.class.getName()},
			new String[] {"active_", "objectActionExecutorKey"}, true);

		_finderPathCountByA_OAEK = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByA_OAEK",
			new String[] {Boolean.class.getName(), String.class.getName()},
			new String[] {"active_", "objectActionExecutorKey"}, false);

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

		_finderPathWithPaginationFindByC_A_OATK = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_A_OATK",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"companyId", "active_", "objectActionTriggerKey"},
			true);

		_finderPathWithoutPaginationFindByC_A_OATK = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_A_OATK",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName()
			},
			new String[] {"companyId", "active_", "objectActionTriggerKey"},
			true);

		_finderPathCountByC_A_OATK = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_A_OATK",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName()
			},
			new String[] {"companyId", "active_", "objectActionTriggerKey"},
			false);

		_finderPathWithPaginationFindByO_A_OATK = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByO_A_OATK",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {
				"objectDefinitionId", "active_", "objectActionTriggerKey"
			},
			true);

		_finderPathWithoutPaginationFindByO_A_OATK = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByO_A_OATK",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName()
			},
			new String[] {
				"objectDefinitionId", "active_", "objectActionTriggerKey"
			},
			true);

		_finderPathCountByO_A_OATK = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByO_A_OATK",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName()
			},
			new String[] {
				"objectDefinitionId", "active_", "objectActionTriggerKey"
			},
			false);

		_finderPathFetchByODI_A_N_OATK = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByODI_A_N_OATK",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName(), String.class.getName()
			},
			new String[] {
				"objectDefinitionId", "active_", "name",
				"objectActionTriggerKey"
			},
			true);

		ObjectActionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ObjectActionUtil.setPersistence(null);

		entityCache.removeCache(ObjectActionImpl.class.getName());
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

	private static final String _SQL_SELECT_OBJECTACTION =
		"SELECT objectAction FROM ObjectAction objectAction";

	private static final String _SQL_SELECT_OBJECTACTION_WHERE =
		"SELECT objectAction FROM ObjectAction objectAction WHERE ";

	private static final String _SQL_COUNT_OBJECTACTION =
		"SELECT COUNT(objectAction) FROM ObjectAction objectAction";

	private static final String _SQL_COUNT_OBJECTACTION_WHERE =
		"SELECT COUNT(objectAction) FROM ObjectAction objectAction WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS = "objectAction.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No ObjectAction exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ObjectAction exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectActionPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "active", "system"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}