/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.impl;

import com.liferay.object.exception.NoSuchObjectLayoutException;
import com.liferay.object.model.ObjectLayout;
import com.liferay.object.model.ObjectLayoutTable;
import com.liferay.object.model.impl.ObjectLayoutImpl;
import com.liferay.object.model.impl.ObjectLayoutModelImpl;
import com.liferay.object.service.persistence.ObjectLayoutPersistence;
import com.liferay.object.service.persistence.ObjectLayoutUtil;
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
 * The persistence implementation for the object layout service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = ObjectLayoutPersistence.class)
public class ObjectLayoutPersistenceImpl
	extends BasePersistenceImpl<ObjectLayout>
	implements ObjectLayoutPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ObjectLayoutUtil</code> to access the object layout persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ObjectLayoutImpl.class.getName();

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
	 * Returns all the object layouts where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching object layouts
	 */
	@Override
	public List<ObjectLayout> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object layouts where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @return the range of matching object layouts
	 */
	@Override
	public List<ObjectLayout> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object layouts where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object layouts
	 */
	@Override
	public List<ObjectLayout> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectLayout> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object layouts where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object layouts
	 */
	@Override
	public List<ObjectLayout> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectLayout> orderByComparator,
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

		List<ObjectLayout> list = null;

		if (useFinderCache) {
			list = (List<ObjectLayout>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectLayout objectLayout : list) {
					if (!uuid.equals(objectLayout.getUuid())) {
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

			sb.append(_SQL_SELECT_OBJECTLAYOUT_WHERE);

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
				sb.append(ObjectLayoutModelImpl.ORDER_BY_JPQL);
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

				list = (List<ObjectLayout>)QueryUtil.list(
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
	 * Returns the first object layout in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout
	 * @throws NoSuchObjectLayoutException if a matching object layout could not be found
	 */
	@Override
	public ObjectLayout findByUuid_First(
			String uuid, OrderByComparator<ObjectLayout> orderByComparator)
		throws NoSuchObjectLayoutException {

		ObjectLayout objectLayout = fetchByUuid_First(uuid, orderByComparator);

		if (objectLayout != null) {
			return objectLayout;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchObjectLayoutException(sb.toString());
	}

	/**
	 * Returns the first object layout in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout, or <code>null</code> if a matching object layout could not be found
	 */
	@Override
	public ObjectLayout fetchByUuid_First(
		String uuid, OrderByComparator<ObjectLayout> orderByComparator) {

		List<ObjectLayout> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object layout in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object layout
	 * @throws NoSuchObjectLayoutException if a matching object layout could not be found
	 */
	@Override
	public ObjectLayout findByUuid_Last(
			String uuid, OrderByComparator<ObjectLayout> orderByComparator)
		throws NoSuchObjectLayoutException {

		ObjectLayout objectLayout = fetchByUuid_Last(uuid, orderByComparator);

		if (objectLayout != null) {
			return objectLayout;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchObjectLayoutException(sb.toString());
	}

	/**
	 * Returns the last object layout in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object layout, or <code>null</code> if a matching object layout could not be found
	 */
	@Override
	public ObjectLayout fetchByUuid_Last(
		String uuid, OrderByComparator<ObjectLayout> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<ObjectLayout> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object layouts before and after the current object layout in the ordered set where uuid = &#63;.
	 *
	 * @param objectLayoutId the primary key of the current object layout
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object layout
	 * @throws NoSuchObjectLayoutException if a object layout with the primary key could not be found
	 */
	@Override
	public ObjectLayout[] findByUuid_PrevAndNext(
			long objectLayoutId, String uuid,
			OrderByComparator<ObjectLayout> orderByComparator)
		throws NoSuchObjectLayoutException {

		uuid = Objects.toString(uuid, "");

		ObjectLayout objectLayout = findByPrimaryKey(objectLayoutId);

		Session session = null;

		try {
			session = openSession();

			ObjectLayout[] array = new ObjectLayoutImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, objectLayout, uuid, orderByComparator, true);

			array[1] = objectLayout;

			array[2] = getByUuid_PrevAndNext(
				session, objectLayout, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected ObjectLayout getByUuid_PrevAndNext(
		Session session, ObjectLayout objectLayout, String uuid,
		OrderByComparator<ObjectLayout> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_OBJECTLAYOUT_WHERE);

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
			sb.append(ObjectLayoutModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(objectLayout)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectLayout> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object layouts where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (ObjectLayout objectLayout :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(objectLayout);
		}
	}

	/**
	 * Returns the number of object layouts where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object layouts
	 */
	@Override
	public int countByUuid(String uuid) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid;

		Object[] finderArgs = new Object[] {uuid};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_OBJECTLAYOUT_WHERE);

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
		"objectLayout.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(objectLayout.uuid IS NULL OR objectLayout.uuid = '')";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the object layouts where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching object layouts
	 */
	@Override
	public List<ObjectLayout> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object layouts where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @return the range of matching object layouts
	 */
	@Override
	public List<ObjectLayout> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object layouts where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object layouts
	 */
	@Override
	public List<ObjectLayout> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectLayout> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object layouts where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object layouts
	 */
	@Override
	public List<ObjectLayout> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectLayout> orderByComparator,
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

		List<ObjectLayout> list = null;

		if (useFinderCache) {
			list = (List<ObjectLayout>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectLayout objectLayout : list) {
					if (!uuid.equals(objectLayout.getUuid()) ||
						(companyId != objectLayout.getCompanyId())) {

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

			sb.append(_SQL_SELECT_OBJECTLAYOUT_WHERE);

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
				sb.append(ObjectLayoutModelImpl.ORDER_BY_JPQL);
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

				list = (List<ObjectLayout>)QueryUtil.list(
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
	 * Returns the first object layout in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout
	 * @throws NoSuchObjectLayoutException if a matching object layout could not be found
	 */
	@Override
	public ObjectLayout findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectLayout> orderByComparator)
		throws NoSuchObjectLayoutException {

		ObjectLayout objectLayout = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (objectLayout != null) {
			return objectLayout;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchObjectLayoutException(sb.toString());
	}

	/**
	 * Returns the first object layout in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout, or <code>null</code> if a matching object layout could not be found
	 */
	@Override
	public ObjectLayout fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectLayout> orderByComparator) {

		List<ObjectLayout> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object layout in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object layout
	 * @throws NoSuchObjectLayoutException if a matching object layout could not be found
	 */
	@Override
	public ObjectLayout findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<ObjectLayout> orderByComparator)
		throws NoSuchObjectLayoutException {

		ObjectLayout objectLayout = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (objectLayout != null) {
			return objectLayout;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchObjectLayoutException(sb.toString());
	}

	/**
	 * Returns the last object layout in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object layout, or <code>null</code> if a matching object layout could not be found
	 */
	@Override
	public ObjectLayout fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<ObjectLayout> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<ObjectLayout> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object layouts before and after the current object layout in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param objectLayoutId the primary key of the current object layout
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object layout
	 * @throws NoSuchObjectLayoutException if a object layout with the primary key could not be found
	 */
	@Override
	public ObjectLayout[] findByUuid_C_PrevAndNext(
			long objectLayoutId, String uuid, long companyId,
			OrderByComparator<ObjectLayout> orderByComparator)
		throws NoSuchObjectLayoutException {

		uuid = Objects.toString(uuid, "");

		ObjectLayout objectLayout = findByPrimaryKey(objectLayoutId);

		Session session = null;

		try {
			session = openSession();

			ObjectLayout[] array = new ObjectLayoutImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, objectLayout, uuid, companyId, orderByComparator,
				true);

			array[1] = objectLayout;

			array[2] = getByUuid_C_PrevAndNext(
				session, objectLayout, uuid, companyId, orderByComparator,
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

	protected ObjectLayout getByUuid_C_PrevAndNext(
		Session session, ObjectLayout objectLayout, String uuid, long companyId,
		OrderByComparator<ObjectLayout> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_OBJECTLAYOUT_WHERE);

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
			sb.append(ObjectLayoutModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(objectLayout)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectLayout> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object layouts where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (ObjectLayout objectLayout :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(objectLayout);
		}
	}

	/**
	 * Returns the number of object layouts where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object layouts
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid_C;

		Object[] finderArgs = new Object[] {uuid, companyId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_OBJECTLAYOUT_WHERE);

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
		"objectLayout.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(objectLayout.uuid IS NULL OR objectLayout.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"objectLayout.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByObjectDefinitionId;
	private FinderPath _finderPathWithoutPaginationFindByObjectDefinitionId;
	private FinderPath _finderPathCountByObjectDefinitionId;

	/**
	 * Returns all the object layouts where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object layouts
	 */
	@Override
	public List<ObjectLayout> findByObjectDefinitionId(
		long objectDefinitionId) {

		return findByObjectDefinitionId(
			objectDefinitionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object layouts where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @return the range of matching object layouts
	 */
	@Override
	public List<ObjectLayout> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end) {

		return findByObjectDefinitionId(objectDefinitionId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object layouts where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object layouts
	 */
	@Override
	public List<ObjectLayout> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectLayout> orderByComparator) {

		return findByObjectDefinitionId(
			objectDefinitionId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object layouts where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object layouts
	 */
	@Override
	public List<ObjectLayout> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectLayout> orderByComparator,
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

		List<ObjectLayout> list = null;

		if (useFinderCache) {
			list = (List<ObjectLayout>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectLayout objectLayout : list) {
					if (objectDefinitionId !=
							objectLayout.getObjectDefinitionId()) {

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

			sb.append(_SQL_SELECT_OBJECTLAYOUT_WHERE);

			sb.append(_FINDER_COLUMN_OBJECTDEFINITIONID_OBJECTDEFINITIONID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectLayoutModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId);

				list = (List<ObjectLayout>)QueryUtil.list(
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
	 * Returns the first object layout in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout
	 * @throws NoSuchObjectLayoutException if a matching object layout could not be found
	 */
	@Override
	public ObjectLayout findByObjectDefinitionId_First(
			long objectDefinitionId,
			OrderByComparator<ObjectLayout> orderByComparator)
		throws NoSuchObjectLayoutException {

		ObjectLayout objectLayout = fetchByObjectDefinitionId_First(
			objectDefinitionId, orderByComparator);

		if (objectLayout != null) {
			return objectLayout;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append("}");

		throw new NoSuchObjectLayoutException(sb.toString());
	}

	/**
	 * Returns the first object layout in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout, or <code>null</code> if a matching object layout could not be found
	 */
	@Override
	public ObjectLayout fetchByObjectDefinitionId_First(
		long objectDefinitionId,
		OrderByComparator<ObjectLayout> orderByComparator) {

		List<ObjectLayout> list = findByObjectDefinitionId(
			objectDefinitionId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object layout in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object layout
	 * @throws NoSuchObjectLayoutException if a matching object layout could not be found
	 */
	@Override
	public ObjectLayout findByObjectDefinitionId_Last(
			long objectDefinitionId,
			OrderByComparator<ObjectLayout> orderByComparator)
		throws NoSuchObjectLayoutException {

		ObjectLayout objectLayout = fetchByObjectDefinitionId_Last(
			objectDefinitionId, orderByComparator);

		if (objectLayout != null) {
			return objectLayout;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append("}");

		throw new NoSuchObjectLayoutException(sb.toString());
	}

	/**
	 * Returns the last object layout in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object layout, or <code>null</code> if a matching object layout could not be found
	 */
	@Override
	public ObjectLayout fetchByObjectDefinitionId_Last(
		long objectDefinitionId,
		OrderByComparator<ObjectLayout> orderByComparator) {

		int count = countByObjectDefinitionId(objectDefinitionId);

		if (count == 0) {
			return null;
		}

		List<ObjectLayout> list = findByObjectDefinitionId(
			objectDefinitionId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object layouts before and after the current object layout in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectLayoutId the primary key of the current object layout
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object layout
	 * @throws NoSuchObjectLayoutException if a object layout with the primary key could not be found
	 */
	@Override
	public ObjectLayout[] findByObjectDefinitionId_PrevAndNext(
			long objectLayoutId, long objectDefinitionId,
			OrderByComparator<ObjectLayout> orderByComparator)
		throws NoSuchObjectLayoutException {

		ObjectLayout objectLayout = findByPrimaryKey(objectLayoutId);

		Session session = null;

		try {
			session = openSession();

			ObjectLayout[] array = new ObjectLayoutImpl[3];

			array[0] = getByObjectDefinitionId_PrevAndNext(
				session, objectLayout, objectDefinitionId, orderByComparator,
				true);

			array[1] = objectLayout;

			array[2] = getByObjectDefinitionId_PrevAndNext(
				session, objectLayout, objectDefinitionId, orderByComparator,
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

	protected ObjectLayout getByObjectDefinitionId_PrevAndNext(
		Session session, ObjectLayout objectLayout, long objectDefinitionId,
		OrderByComparator<ObjectLayout> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_OBJECTLAYOUT_WHERE);

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
			sb.append(ObjectLayoutModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(objectDefinitionId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(objectLayout)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectLayout> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object layouts where objectDefinitionId = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 */
	@Override
	public void removeByObjectDefinitionId(long objectDefinitionId) {
		for (ObjectLayout objectLayout :
				findByObjectDefinitionId(
					objectDefinitionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(objectLayout);
		}
	}

	/**
	 * Returns the number of object layouts where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object layouts
	 */
	@Override
	public int countByObjectDefinitionId(long objectDefinitionId) {
		FinderPath finderPath = _finderPathCountByObjectDefinitionId;

		Object[] finderArgs = new Object[] {objectDefinitionId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_OBJECTLAYOUT_WHERE);

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
			"objectLayout.objectDefinitionId = ?";

	private FinderPath _finderPathWithPaginationFindByC_DOL;
	private FinderPath _finderPathWithoutPaginationFindByC_DOL;
	private FinderPath _finderPathCountByC_DOL;

	/**
	 * Returns all the object layouts where companyId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param companyId the company ID
	 * @param defaultObjectLayout the default object layout
	 * @return the matching object layouts
	 */
	@Override
	public List<ObjectLayout> findByC_DOL(
		long companyId, boolean defaultObjectLayout) {

		return findByC_DOL(
			companyId, defaultObjectLayout, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object layouts where companyId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param defaultObjectLayout the default object layout
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @return the range of matching object layouts
	 */
	@Override
	public List<ObjectLayout> findByC_DOL(
		long companyId, boolean defaultObjectLayout, int start, int end) {

		return findByC_DOL(companyId, defaultObjectLayout, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object layouts where companyId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param defaultObjectLayout the default object layout
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object layouts
	 */
	@Override
	public List<ObjectLayout> findByC_DOL(
		long companyId, boolean defaultObjectLayout, int start, int end,
		OrderByComparator<ObjectLayout> orderByComparator) {

		return findByC_DOL(
			companyId, defaultObjectLayout, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the object layouts where companyId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param defaultObjectLayout the default object layout
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object layouts
	 */
	@Override
	public List<ObjectLayout> findByC_DOL(
		long companyId, boolean defaultObjectLayout, int start, int end,
		OrderByComparator<ObjectLayout> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByC_DOL;
				finderArgs = new Object[] {companyId, defaultObjectLayout};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByC_DOL;
			finderArgs = new Object[] {
				companyId, defaultObjectLayout, start, end, orderByComparator
			};
		}

		List<ObjectLayout> list = null;

		if (useFinderCache) {
			list = (List<ObjectLayout>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectLayout objectLayout : list) {
					if ((companyId != objectLayout.getCompanyId()) ||
						(defaultObjectLayout !=
							objectLayout.isDefaultObjectLayout())) {

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

			sb.append(_SQL_SELECT_OBJECTLAYOUT_WHERE);

			sb.append(_FINDER_COLUMN_C_DOL_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_DOL_DEFAULTOBJECTLAYOUT_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectLayoutModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(defaultObjectLayout);

				list = (List<ObjectLayout>)QueryUtil.list(
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
	 * Returns the first object layout in the ordered set where companyId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param companyId the company ID
	 * @param defaultObjectLayout the default object layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout
	 * @throws NoSuchObjectLayoutException if a matching object layout could not be found
	 */
	@Override
	public ObjectLayout findByC_DOL_First(
			long companyId, boolean defaultObjectLayout,
			OrderByComparator<ObjectLayout> orderByComparator)
		throws NoSuchObjectLayoutException {

		ObjectLayout objectLayout = fetchByC_DOL_First(
			companyId, defaultObjectLayout, orderByComparator);

		if (objectLayout != null) {
			return objectLayout;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", defaultObjectLayout=");
		sb.append(defaultObjectLayout);

		sb.append("}");

		throw new NoSuchObjectLayoutException(sb.toString());
	}

	/**
	 * Returns the first object layout in the ordered set where companyId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param companyId the company ID
	 * @param defaultObjectLayout the default object layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout, or <code>null</code> if a matching object layout could not be found
	 */
	@Override
	public ObjectLayout fetchByC_DOL_First(
		long companyId, boolean defaultObjectLayout,
		OrderByComparator<ObjectLayout> orderByComparator) {

		List<ObjectLayout> list = findByC_DOL(
			companyId, defaultObjectLayout, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object layout in the ordered set where companyId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param companyId the company ID
	 * @param defaultObjectLayout the default object layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object layout
	 * @throws NoSuchObjectLayoutException if a matching object layout could not be found
	 */
	@Override
	public ObjectLayout findByC_DOL_Last(
			long companyId, boolean defaultObjectLayout,
			OrderByComparator<ObjectLayout> orderByComparator)
		throws NoSuchObjectLayoutException {

		ObjectLayout objectLayout = fetchByC_DOL_Last(
			companyId, defaultObjectLayout, orderByComparator);

		if (objectLayout != null) {
			return objectLayout;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", defaultObjectLayout=");
		sb.append(defaultObjectLayout);

		sb.append("}");

		throw new NoSuchObjectLayoutException(sb.toString());
	}

	/**
	 * Returns the last object layout in the ordered set where companyId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param companyId the company ID
	 * @param defaultObjectLayout the default object layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object layout, or <code>null</code> if a matching object layout could not be found
	 */
	@Override
	public ObjectLayout fetchByC_DOL_Last(
		long companyId, boolean defaultObjectLayout,
		OrderByComparator<ObjectLayout> orderByComparator) {

		int count = countByC_DOL(companyId, defaultObjectLayout);

		if (count == 0) {
			return null;
		}

		List<ObjectLayout> list = findByC_DOL(
			companyId, defaultObjectLayout, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object layouts before and after the current object layout in the ordered set where companyId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param objectLayoutId the primary key of the current object layout
	 * @param companyId the company ID
	 * @param defaultObjectLayout the default object layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object layout
	 * @throws NoSuchObjectLayoutException if a object layout with the primary key could not be found
	 */
	@Override
	public ObjectLayout[] findByC_DOL_PrevAndNext(
			long objectLayoutId, long companyId, boolean defaultObjectLayout,
			OrderByComparator<ObjectLayout> orderByComparator)
		throws NoSuchObjectLayoutException {

		ObjectLayout objectLayout = findByPrimaryKey(objectLayoutId);

		Session session = null;

		try {
			session = openSession();

			ObjectLayout[] array = new ObjectLayoutImpl[3];

			array[0] = getByC_DOL_PrevAndNext(
				session, objectLayout, companyId, defaultObjectLayout,
				orderByComparator, true);

			array[1] = objectLayout;

			array[2] = getByC_DOL_PrevAndNext(
				session, objectLayout, companyId, defaultObjectLayout,
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

	protected ObjectLayout getByC_DOL_PrevAndNext(
		Session session, ObjectLayout objectLayout, long companyId,
		boolean defaultObjectLayout,
		OrderByComparator<ObjectLayout> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_OBJECTLAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_C_DOL_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_DOL_DEFAULTOBJECTLAYOUT_2);

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
			sb.append(ObjectLayoutModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(defaultObjectLayout);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(objectLayout)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectLayout> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object layouts where companyId = &#63; and defaultObjectLayout = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param defaultObjectLayout the default object layout
	 */
	@Override
	public void removeByC_DOL(long companyId, boolean defaultObjectLayout) {
		for (ObjectLayout objectLayout :
				findByC_DOL(
					companyId, defaultObjectLayout, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(objectLayout);
		}
	}

	/**
	 * Returns the number of object layouts where companyId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param companyId the company ID
	 * @param defaultObjectLayout the default object layout
	 * @return the number of matching object layouts
	 */
	@Override
	public int countByC_DOL(long companyId, boolean defaultObjectLayout) {
		FinderPath finderPath = _finderPathCountByC_DOL;

		Object[] finderArgs = new Object[] {companyId, defaultObjectLayout};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_OBJECTLAYOUT_WHERE);

			sb.append(_FINDER_COLUMN_C_DOL_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_DOL_DEFAULTOBJECTLAYOUT_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(defaultObjectLayout);

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

	private static final String _FINDER_COLUMN_C_DOL_COMPANYID_2 =
		"objectLayout.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_DOL_DEFAULTOBJECTLAYOUT_2 =
		"objectLayout.defaultObjectLayout = ?";

	private FinderPath _finderPathWithPaginationFindByODI_DOL;
	private FinderPath _finderPathWithoutPaginationFindByODI_DOL;
	private FinderPath _finderPathCountByODI_DOL;

	/**
	 * Returns all the object layouts where objectDefinitionId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectLayout the default object layout
	 * @return the matching object layouts
	 */
	@Override
	public List<ObjectLayout> findByODI_DOL(
		long objectDefinitionId, boolean defaultObjectLayout) {

		return findByODI_DOL(
			objectDefinitionId, defaultObjectLayout, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object layouts where objectDefinitionId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectLayout the default object layout
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @return the range of matching object layouts
	 */
	@Override
	public List<ObjectLayout> findByODI_DOL(
		long objectDefinitionId, boolean defaultObjectLayout, int start,
		int end) {

		return findByODI_DOL(
			objectDefinitionId, defaultObjectLayout, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object layouts where objectDefinitionId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectLayout the default object layout
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object layouts
	 */
	@Override
	public List<ObjectLayout> findByODI_DOL(
		long objectDefinitionId, boolean defaultObjectLayout, int start,
		int end, OrderByComparator<ObjectLayout> orderByComparator) {

		return findByODI_DOL(
			objectDefinitionId, defaultObjectLayout, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object layouts where objectDefinitionId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectLayout the default object layout
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object layouts
	 */
	@Override
	public List<ObjectLayout> findByODI_DOL(
		long objectDefinitionId, boolean defaultObjectLayout, int start,
		int end, OrderByComparator<ObjectLayout> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByODI_DOL;
				finderArgs = new Object[] {
					objectDefinitionId, defaultObjectLayout
				};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByODI_DOL;
			finderArgs = new Object[] {
				objectDefinitionId, defaultObjectLayout, start, end,
				orderByComparator
			};
		}

		List<ObjectLayout> list = null;

		if (useFinderCache) {
			list = (List<ObjectLayout>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectLayout objectLayout : list) {
					if ((objectDefinitionId !=
							objectLayout.getObjectDefinitionId()) ||
						(defaultObjectLayout !=
							objectLayout.isDefaultObjectLayout())) {

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

			sb.append(_SQL_SELECT_OBJECTLAYOUT_WHERE);

			sb.append(_FINDER_COLUMN_ODI_DOL_OBJECTDEFINITIONID_2);

			sb.append(_FINDER_COLUMN_ODI_DOL_DEFAULTOBJECTLAYOUT_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectLayoutModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId);

				queryPos.add(defaultObjectLayout);

				list = (List<ObjectLayout>)QueryUtil.list(
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
	 * Returns the first object layout in the ordered set where objectDefinitionId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectLayout the default object layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout
	 * @throws NoSuchObjectLayoutException if a matching object layout could not be found
	 */
	@Override
	public ObjectLayout findByODI_DOL_First(
			long objectDefinitionId, boolean defaultObjectLayout,
			OrderByComparator<ObjectLayout> orderByComparator)
		throws NoSuchObjectLayoutException {

		ObjectLayout objectLayout = fetchByODI_DOL_First(
			objectDefinitionId, defaultObjectLayout, orderByComparator);

		if (objectLayout != null) {
			return objectLayout;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append(", defaultObjectLayout=");
		sb.append(defaultObjectLayout);

		sb.append("}");

		throw new NoSuchObjectLayoutException(sb.toString());
	}

	/**
	 * Returns the first object layout in the ordered set where objectDefinitionId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectLayout the default object layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout, or <code>null</code> if a matching object layout could not be found
	 */
	@Override
	public ObjectLayout fetchByODI_DOL_First(
		long objectDefinitionId, boolean defaultObjectLayout,
		OrderByComparator<ObjectLayout> orderByComparator) {

		List<ObjectLayout> list = findByODI_DOL(
			objectDefinitionId, defaultObjectLayout, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object layout in the ordered set where objectDefinitionId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectLayout the default object layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object layout
	 * @throws NoSuchObjectLayoutException if a matching object layout could not be found
	 */
	@Override
	public ObjectLayout findByODI_DOL_Last(
			long objectDefinitionId, boolean defaultObjectLayout,
			OrderByComparator<ObjectLayout> orderByComparator)
		throws NoSuchObjectLayoutException {

		ObjectLayout objectLayout = fetchByODI_DOL_Last(
			objectDefinitionId, defaultObjectLayout, orderByComparator);

		if (objectLayout != null) {
			return objectLayout;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append(", defaultObjectLayout=");
		sb.append(defaultObjectLayout);

		sb.append("}");

		throw new NoSuchObjectLayoutException(sb.toString());
	}

	/**
	 * Returns the last object layout in the ordered set where objectDefinitionId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectLayout the default object layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object layout, or <code>null</code> if a matching object layout could not be found
	 */
	@Override
	public ObjectLayout fetchByODI_DOL_Last(
		long objectDefinitionId, boolean defaultObjectLayout,
		OrderByComparator<ObjectLayout> orderByComparator) {

		int count = countByODI_DOL(objectDefinitionId, defaultObjectLayout);

		if (count == 0) {
			return null;
		}

		List<ObjectLayout> list = findByODI_DOL(
			objectDefinitionId, defaultObjectLayout, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object layouts before and after the current object layout in the ordered set where objectDefinitionId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param objectLayoutId the primary key of the current object layout
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectLayout the default object layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object layout
	 * @throws NoSuchObjectLayoutException if a object layout with the primary key could not be found
	 */
	@Override
	public ObjectLayout[] findByODI_DOL_PrevAndNext(
			long objectLayoutId, long objectDefinitionId,
			boolean defaultObjectLayout,
			OrderByComparator<ObjectLayout> orderByComparator)
		throws NoSuchObjectLayoutException {

		ObjectLayout objectLayout = findByPrimaryKey(objectLayoutId);

		Session session = null;

		try {
			session = openSession();

			ObjectLayout[] array = new ObjectLayoutImpl[3];

			array[0] = getByODI_DOL_PrevAndNext(
				session, objectLayout, objectDefinitionId, defaultObjectLayout,
				orderByComparator, true);

			array[1] = objectLayout;

			array[2] = getByODI_DOL_PrevAndNext(
				session, objectLayout, objectDefinitionId, defaultObjectLayout,
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

	protected ObjectLayout getByODI_DOL_PrevAndNext(
		Session session, ObjectLayout objectLayout, long objectDefinitionId,
		boolean defaultObjectLayout,
		OrderByComparator<ObjectLayout> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_OBJECTLAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_ODI_DOL_OBJECTDEFINITIONID_2);

		sb.append(_FINDER_COLUMN_ODI_DOL_DEFAULTOBJECTLAYOUT_2);

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
			sb.append(ObjectLayoutModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(objectDefinitionId);

		queryPos.add(defaultObjectLayout);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(objectLayout)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectLayout> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object layouts where objectDefinitionId = &#63; and defaultObjectLayout = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectLayout the default object layout
	 */
	@Override
	public void removeByODI_DOL(
		long objectDefinitionId, boolean defaultObjectLayout) {

		for (ObjectLayout objectLayout :
				findByODI_DOL(
					objectDefinitionId, defaultObjectLayout, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(objectLayout);
		}
	}

	/**
	 * Returns the number of object layouts where objectDefinitionId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectLayout the default object layout
	 * @return the number of matching object layouts
	 */
	@Override
	public int countByODI_DOL(
		long objectDefinitionId, boolean defaultObjectLayout) {

		FinderPath finderPath = _finderPathCountByODI_DOL;

		Object[] finderArgs = new Object[] {
			objectDefinitionId, defaultObjectLayout
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_OBJECTLAYOUT_WHERE);

			sb.append(_FINDER_COLUMN_ODI_DOL_OBJECTDEFINITIONID_2);

			sb.append(_FINDER_COLUMN_ODI_DOL_DEFAULTOBJECTLAYOUT_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId);

				queryPos.add(defaultObjectLayout);

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

	private static final String _FINDER_COLUMN_ODI_DOL_OBJECTDEFINITIONID_2 =
		"objectLayout.objectDefinitionId = ? AND ";

	private static final String _FINDER_COLUMN_ODI_DOL_DEFAULTOBJECTLAYOUT_2 =
		"objectLayout.defaultObjectLayout = ?";

	public ObjectLayoutPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ObjectLayout.class);

		setModelImplClass(ObjectLayoutImpl.class);
		setModelPKClass(long.class);

		setTable(ObjectLayoutTable.INSTANCE);
	}

	/**
	 * Caches the object layout in the entity cache if it is enabled.
	 *
	 * @param objectLayout the object layout
	 */
	@Override
	public void cacheResult(ObjectLayout objectLayout) {
		entityCache.putResult(
			ObjectLayoutImpl.class, objectLayout.getPrimaryKey(), objectLayout);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the object layouts in the entity cache if it is enabled.
	 *
	 * @param objectLayouts the object layouts
	 */
	@Override
	public void cacheResult(List<ObjectLayout> objectLayouts) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (objectLayouts.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (ObjectLayout objectLayout : objectLayouts) {
			if (entityCache.getResult(
					ObjectLayoutImpl.class, objectLayout.getPrimaryKey()) ==
						null) {

				cacheResult(objectLayout);
			}
		}
	}

	/**
	 * Clears the cache for all object layouts.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(ObjectLayoutImpl.class);

		finderCache.clearCache(ObjectLayoutImpl.class);
	}

	/**
	 * Clears the cache for the object layout.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(ObjectLayout objectLayout) {
		entityCache.removeResult(ObjectLayoutImpl.class, objectLayout);
	}

	@Override
	public void clearCache(List<ObjectLayout> objectLayouts) {
		for (ObjectLayout objectLayout : objectLayouts) {
			entityCache.removeResult(ObjectLayoutImpl.class, objectLayout);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(ObjectLayoutImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(ObjectLayoutImpl.class, primaryKey);
		}
	}

	/**
	 * Creates a new object layout with the primary key. Does not add the object layout to the database.
	 *
	 * @param objectLayoutId the primary key for the new object layout
	 * @return the new object layout
	 */
	@Override
	public ObjectLayout create(long objectLayoutId) {
		ObjectLayout objectLayout = new ObjectLayoutImpl();

		objectLayout.setNew(true);
		objectLayout.setPrimaryKey(objectLayoutId);

		String uuid = PortalUUIDUtil.generate();

		objectLayout.setUuid(uuid);

		objectLayout.setCompanyId(CompanyThreadLocal.getCompanyId());

		return objectLayout;
	}

	/**
	 * Removes the object layout with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectLayoutId the primary key of the object layout
	 * @return the object layout that was removed
	 * @throws NoSuchObjectLayoutException if a object layout with the primary key could not be found
	 */
	@Override
	public ObjectLayout remove(long objectLayoutId)
		throws NoSuchObjectLayoutException {

		return remove((Serializable)objectLayoutId);
	}

	/**
	 * Removes the object layout with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the object layout
	 * @return the object layout that was removed
	 * @throws NoSuchObjectLayoutException if a object layout with the primary key could not be found
	 */
	@Override
	public ObjectLayout remove(Serializable primaryKey)
		throws NoSuchObjectLayoutException {

		Session session = null;

		try {
			session = openSession();

			ObjectLayout objectLayout = (ObjectLayout)session.get(
				ObjectLayoutImpl.class, primaryKey);

			if (objectLayout == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchObjectLayoutException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(objectLayout);
		}
		catch (NoSuchObjectLayoutException noSuchEntityException) {
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
	protected ObjectLayout removeImpl(ObjectLayout objectLayout) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(objectLayout)) {
				objectLayout = (ObjectLayout)session.get(
					ObjectLayoutImpl.class, objectLayout.getPrimaryKeyObj());
			}

			if (objectLayout != null) {
				session.delete(objectLayout);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (objectLayout != null) {
			clearCache(objectLayout);
		}

		return objectLayout;
	}

	@Override
	public ObjectLayout updateImpl(ObjectLayout objectLayout) {
		boolean isNew = objectLayout.isNew();

		if (!(objectLayout instanceof ObjectLayoutModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(objectLayout.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					objectLayout);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in objectLayout proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ObjectLayout implementation " +
					objectLayout.getClass());
		}

		ObjectLayoutModelImpl objectLayoutModelImpl =
			(ObjectLayoutModelImpl)objectLayout;

		if (Validator.isNull(objectLayout.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			objectLayout.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (objectLayout.getCreateDate() == null)) {
			if (serviceContext == null) {
				objectLayout.setCreateDate(date);
			}
			else {
				objectLayout.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!objectLayoutModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				objectLayout.setModifiedDate(date);
			}
			else {
				objectLayout.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(objectLayout);
			}
			else {
				objectLayout = (ObjectLayout)session.merge(objectLayout);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			ObjectLayoutImpl.class, objectLayoutModelImpl, false, true);

		if (isNew) {
			objectLayout.setNew(false);
		}

		objectLayout.resetOriginalValues();

		return objectLayout;
	}

	/**
	 * Returns the object layout with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the object layout
	 * @return the object layout
	 * @throws NoSuchObjectLayoutException if a object layout with the primary key could not be found
	 */
	@Override
	public ObjectLayout findByPrimaryKey(Serializable primaryKey)
		throws NoSuchObjectLayoutException {

		ObjectLayout objectLayout = fetchByPrimaryKey(primaryKey);

		if (objectLayout == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchObjectLayoutException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return objectLayout;
	}

	/**
	 * Returns the object layout with the primary key or throws a <code>NoSuchObjectLayoutException</code> if it could not be found.
	 *
	 * @param objectLayoutId the primary key of the object layout
	 * @return the object layout
	 * @throws NoSuchObjectLayoutException if a object layout with the primary key could not be found
	 */
	@Override
	public ObjectLayout findByPrimaryKey(long objectLayoutId)
		throws NoSuchObjectLayoutException {

		return findByPrimaryKey((Serializable)objectLayoutId);
	}

	/**
	 * Returns the object layout with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectLayoutId the primary key of the object layout
	 * @return the object layout, or <code>null</code> if a object layout with the primary key could not be found
	 */
	@Override
	public ObjectLayout fetchByPrimaryKey(long objectLayoutId) {
		return fetchByPrimaryKey((Serializable)objectLayoutId);
	}

	/**
	 * Returns all the object layouts.
	 *
	 * @return the object layouts
	 */
	@Override
	public List<ObjectLayout> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object layouts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @return the range of object layouts
	 */
	@Override
	public List<ObjectLayout> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the object layouts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of object layouts
	 */
	@Override
	public List<ObjectLayout> findAll(
		int start, int end, OrderByComparator<ObjectLayout> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object layouts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of object layouts
	 */
	@Override
	public List<ObjectLayout> findAll(
		int start, int end, OrderByComparator<ObjectLayout> orderByComparator,
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

		List<ObjectLayout> list = null;

		if (useFinderCache) {
			list = (List<ObjectLayout>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_OBJECTLAYOUT);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_OBJECTLAYOUT;

				sql = sql.concat(ObjectLayoutModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<ObjectLayout>)QueryUtil.list(
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
	 * Removes all the object layouts from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (ObjectLayout objectLayout : findAll()) {
			remove(objectLayout);
		}
	}

	/**
	 * Returns the number of object layouts.
	 *
	 * @return the number of object layouts
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_OBJECTLAYOUT);

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
		return "objectLayoutId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OBJECTLAYOUT;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ObjectLayoutModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the object layout persistence.
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

		_finderPathWithPaginationFindByC_DOL = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_DOL",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "defaultObjectLayout"}, true);

		_finderPathWithoutPaginationFindByC_DOL = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_DOL",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"companyId", "defaultObjectLayout"}, true);

		_finderPathCountByC_DOL = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_DOL",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"companyId", "defaultObjectLayout"}, false);

		_finderPathWithPaginationFindByODI_DOL = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI_DOL",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"objectDefinitionId", "defaultObjectLayout"}, true);

		_finderPathWithoutPaginationFindByODI_DOL = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByODI_DOL",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"objectDefinitionId", "defaultObjectLayout"}, true);

		_finderPathCountByODI_DOL = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByODI_DOL",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"objectDefinitionId", "defaultObjectLayout"}, false);

		ObjectLayoutUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ObjectLayoutUtil.setPersistence(null);

		entityCache.removeCache(ObjectLayoutImpl.class.getName());
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

	private static final String _SQL_SELECT_OBJECTLAYOUT =
		"SELECT objectLayout FROM ObjectLayout objectLayout";

	private static final String _SQL_SELECT_OBJECTLAYOUT_WHERE =
		"SELECT objectLayout FROM ObjectLayout objectLayout WHERE ";

	private static final String _SQL_COUNT_OBJECTLAYOUT =
		"SELECT COUNT(objectLayout) FROM ObjectLayout objectLayout";

	private static final String _SQL_COUNT_OBJECTLAYOUT_WHERE =
		"SELECT COUNT(objectLayout) FROM ObjectLayout objectLayout WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS = "objectLayout.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No ObjectLayout exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ObjectLayout exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectLayoutPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}