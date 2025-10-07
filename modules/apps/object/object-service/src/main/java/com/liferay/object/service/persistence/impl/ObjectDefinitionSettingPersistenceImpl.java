/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.impl;

import com.liferay.object.exception.NoSuchObjectDefinitionSettingException;
import com.liferay.object.model.ObjectDefinitionSetting;
import com.liferay.object.model.ObjectDefinitionSettingTable;
import com.liferay.object.model.impl.ObjectDefinitionSettingImpl;
import com.liferay.object.model.impl.ObjectDefinitionSettingModelImpl;
import com.liferay.object.service.persistence.ObjectDefinitionSettingPersistence;
import com.liferay.object.service.persistence.ObjectDefinitionSettingUtil;
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
 * The persistence implementation for the object definition setting service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = ObjectDefinitionSettingPersistence.class)
public class ObjectDefinitionSettingPersistenceImpl
	extends BasePersistenceImpl<ObjectDefinitionSetting>
	implements ObjectDefinitionSettingPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ObjectDefinitionSettingUtil</code> to access the object definition setting persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ObjectDefinitionSettingImpl.class.getName();

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
	 * Returns all the object definition settings where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching object definition settings
	 */
	@Override
	public List<ObjectDefinitionSetting> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object definition settings where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @return the range of matching object definition settings
	 */
	@Override
	public List<ObjectDefinitionSetting> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object definition settings where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definition settings
	 */
	@Override
	public List<ObjectDefinitionSetting> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object definition settings where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definition settings
	 */
	@Override
	public List<ObjectDefinitionSetting> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator,
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

		List<ObjectDefinitionSetting> list = null;

		if (useFinderCache) {
			list = (List<ObjectDefinitionSetting>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectDefinitionSetting objectDefinitionSetting : list) {
					if (!uuid.equals(objectDefinitionSetting.getUuid())) {
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

			sb.append(_SQL_SELECT_OBJECTDEFINITIONSETTING_WHERE);

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
				sb.append(ObjectDefinitionSettingModelImpl.ORDER_BY_JPQL);
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

				list = (List<ObjectDefinitionSetting>)QueryUtil.list(
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
	 * Returns the first object definition setting in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a matching object definition setting could not be found
	 */
	@Override
	public ObjectDefinitionSetting findByUuid_First(
			String uuid,
			OrderByComparator<ObjectDefinitionSetting> orderByComparator)
		throws NoSuchObjectDefinitionSettingException {

		ObjectDefinitionSetting objectDefinitionSetting = fetchByUuid_First(
			uuid, orderByComparator);

		if (objectDefinitionSetting != null) {
			return objectDefinitionSetting;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchObjectDefinitionSettingException(sb.toString());
	}

	/**
	 * Returns the first object definition setting in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition setting, or <code>null</code> if a matching object definition setting could not be found
	 */
	@Override
	public ObjectDefinitionSetting fetchByUuid_First(
		String uuid,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator) {

		List<ObjectDefinitionSetting> list = findByUuid(
			uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object definition setting in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a matching object definition setting could not be found
	 */
	@Override
	public ObjectDefinitionSetting findByUuid_Last(
			String uuid,
			OrderByComparator<ObjectDefinitionSetting> orderByComparator)
		throws NoSuchObjectDefinitionSettingException {

		ObjectDefinitionSetting objectDefinitionSetting = fetchByUuid_Last(
			uuid, orderByComparator);

		if (objectDefinitionSetting != null) {
			return objectDefinitionSetting;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchObjectDefinitionSettingException(sb.toString());
	}

	/**
	 * Returns the last object definition setting in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition setting, or <code>null</code> if a matching object definition setting could not be found
	 */
	@Override
	public ObjectDefinitionSetting fetchByUuid_Last(
		String uuid,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<ObjectDefinitionSetting> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object definition settings before and after the current object definition setting in the ordered set where uuid = &#63;.
	 *
	 * @param objectDefinitionSettingId the primary key of the current object definition setting
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a object definition setting with the primary key could not be found
	 */
	@Override
	public ObjectDefinitionSetting[] findByUuid_PrevAndNext(
			long objectDefinitionSettingId, String uuid,
			OrderByComparator<ObjectDefinitionSetting> orderByComparator)
		throws NoSuchObjectDefinitionSettingException {

		uuid = Objects.toString(uuid, "");

		ObjectDefinitionSetting objectDefinitionSetting = findByPrimaryKey(
			objectDefinitionSettingId);

		Session session = null;

		try {
			session = openSession();

			ObjectDefinitionSetting[] array =
				new ObjectDefinitionSettingImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, objectDefinitionSetting, uuid, orderByComparator,
				true);

			array[1] = objectDefinitionSetting;

			array[2] = getByUuid_PrevAndNext(
				session, objectDefinitionSetting, uuid, orderByComparator,
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

	protected ObjectDefinitionSetting getByUuid_PrevAndNext(
		Session session, ObjectDefinitionSetting objectDefinitionSetting,
		String uuid,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTDEFINITIONSETTING_WHERE);

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
			sb.append(ObjectDefinitionSettingModelImpl.ORDER_BY_JPQL);
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
						objectDefinitionSetting)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectDefinitionSetting> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object definition settings where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (ObjectDefinitionSetting objectDefinitionSetting :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(objectDefinitionSetting);
		}
	}

	/**
	 * Returns the number of object definition settings where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object definition settings
	 */
	@Override
	public int countByUuid(String uuid) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid;

		Object[] finderArgs = new Object[] {uuid};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_OBJECTDEFINITIONSETTING_WHERE);

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
		"objectDefinitionSetting.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(objectDefinitionSetting.uuid IS NULL OR objectDefinitionSetting.uuid = '')";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the object definition settings where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching object definition settings
	 */
	@Override
	public List<ObjectDefinitionSetting> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object definition settings where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @return the range of matching object definition settings
	 */
	@Override
	public List<ObjectDefinitionSetting> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object definition settings where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definition settings
	 */
	@Override
	public List<ObjectDefinitionSetting> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object definition settings where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definition settings
	 */
	@Override
	public List<ObjectDefinitionSetting> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator,
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

		List<ObjectDefinitionSetting> list = null;

		if (useFinderCache) {
			list = (List<ObjectDefinitionSetting>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectDefinitionSetting objectDefinitionSetting : list) {
					if (!uuid.equals(objectDefinitionSetting.getUuid()) ||
						(companyId != objectDefinitionSetting.getCompanyId())) {

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

			sb.append(_SQL_SELECT_OBJECTDEFINITIONSETTING_WHERE);

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
				sb.append(ObjectDefinitionSettingModelImpl.ORDER_BY_JPQL);
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

				list = (List<ObjectDefinitionSetting>)QueryUtil.list(
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
	 * Returns the first object definition setting in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a matching object definition setting could not be found
	 */
	@Override
	public ObjectDefinitionSetting findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectDefinitionSetting> orderByComparator)
		throws NoSuchObjectDefinitionSettingException {

		ObjectDefinitionSetting objectDefinitionSetting = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (objectDefinitionSetting != null) {
			return objectDefinitionSetting;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchObjectDefinitionSettingException(sb.toString());
	}

	/**
	 * Returns the first object definition setting in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition setting, or <code>null</code> if a matching object definition setting could not be found
	 */
	@Override
	public ObjectDefinitionSetting fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator) {

		List<ObjectDefinitionSetting> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object definition setting in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a matching object definition setting could not be found
	 */
	@Override
	public ObjectDefinitionSetting findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<ObjectDefinitionSetting> orderByComparator)
		throws NoSuchObjectDefinitionSettingException {

		ObjectDefinitionSetting objectDefinitionSetting = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (objectDefinitionSetting != null) {
			return objectDefinitionSetting;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchObjectDefinitionSettingException(sb.toString());
	}

	/**
	 * Returns the last object definition setting in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition setting, or <code>null</code> if a matching object definition setting could not be found
	 */
	@Override
	public ObjectDefinitionSetting fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<ObjectDefinitionSetting> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object definition settings before and after the current object definition setting in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param objectDefinitionSettingId the primary key of the current object definition setting
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a object definition setting with the primary key could not be found
	 */
	@Override
	public ObjectDefinitionSetting[] findByUuid_C_PrevAndNext(
			long objectDefinitionSettingId, String uuid, long companyId,
			OrderByComparator<ObjectDefinitionSetting> orderByComparator)
		throws NoSuchObjectDefinitionSettingException {

		uuid = Objects.toString(uuid, "");

		ObjectDefinitionSetting objectDefinitionSetting = findByPrimaryKey(
			objectDefinitionSettingId);

		Session session = null;

		try {
			session = openSession();

			ObjectDefinitionSetting[] array =
				new ObjectDefinitionSettingImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, objectDefinitionSetting, uuid, companyId,
				orderByComparator, true);

			array[1] = objectDefinitionSetting;

			array[2] = getByUuid_C_PrevAndNext(
				session, objectDefinitionSetting, uuid, companyId,
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

	protected ObjectDefinitionSetting getByUuid_C_PrevAndNext(
		Session session, ObjectDefinitionSetting objectDefinitionSetting,
		String uuid, long companyId,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTDEFINITIONSETTING_WHERE);

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
			sb.append(ObjectDefinitionSettingModelImpl.ORDER_BY_JPQL);
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
						objectDefinitionSetting)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectDefinitionSetting> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object definition settings where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (ObjectDefinitionSetting objectDefinitionSetting :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(objectDefinitionSetting);
		}
	}

	/**
	 * Returns the number of object definition settings where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object definition settings
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid_C;

		Object[] finderArgs = new Object[] {uuid, companyId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_OBJECTDEFINITIONSETTING_WHERE);

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
		"objectDefinitionSetting.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(objectDefinitionSetting.uuid IS NULL OR objectDefinitionSetting.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"objectDefinitionSetting.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByObjectDefinitionId;
	private FinderPath _finderPathWithoutPaginationFindByObjectDefinitionId;
	private FinderPath _finderPathCountByObjectDefinitionId;

	/**
	 * Returns all the object definition settings where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object definition settings
	 */
	@Override
	public List<ObjectDefinitionSetting> findByObjectDefinitionId(
		long objectDefinitionId) {

		return findByObjectDefinitionId(
			objectDefinitionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object definition settings where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @return the range of matching object definition settings
	 */
	@Override
	public List<ObjectDefinitionSetting> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end) {

		return findByObjectDefinitionId(objectDefinitionId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object definition settings where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definition settings
	 */
	@Override
	public List<ObjectDefinitionSetting> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator) {

		return findByObjectDefinitionId(
			objectDefinitionId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object definition settings where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definition settings
	 */
	@Override
	public List<ObjectDefinitionSetting> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator,
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

		List<ObjectDefinitionSetting> list = null;

		if (useFinderCache) {
			list = (List<ObjectDefinitionSetting>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectDefinitionSetting objectDefinitionSetting : list) {
					if (objectDefinitionId !=
							objectDefinitionSetting.getObjectDefinitionId()) {

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

			sb.append(_SQL_SELECT_OBJECTDEFINITIONSETTING_WHERE);

			sb.append(_FINDER_COLUMN_OBJECTDEFINITIONID_OBJECTDEFINITIONID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectDefinitionSettingModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(objectDefinitionId);

				list = (List<ObjectDefinitionSetting>)QueryUtil.list(
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
	 * Returns the first object definition setting in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a matching object definition setting could not be found
	 */
	@Override
	public ObjectDefinitionSetting findByObjectDefinitionId_First(
			long objectDefinitionId,
			OrderByComparator<ObjectDefinitionSetting> orderByComparator)
		throws NoSuchObjectDefinitionSettingException {

		ObjectDefinitionSetting objectDefinitionSetting =
			fetchByObjectDefinitionId_First(
				objectDefinitionId, orderByComparator);

		if (objectDefinitionSetting != null) {
			return objectDefinitionSetting;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append("}");

		throw new NoSuchObjectDefinitionSettingException(sb.toString());
	}

	/**
	 * Returns the first object definition setting in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition setting, or <code>null</code> if a matching object definition setting could not be found
	 */
	@Override
	public ObjectDefinitionSetting fetchByObjectDefinitionId_First(
		long objectDefinitionId,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator) {

		List<ObjectDefinitionSetting> list = findByObjectDefinitionId(
			objectDefinitionId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object definition setting in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a matching object definition setting could not be found
	 */
	@Override
	public ObjectDefinitionSetting findByObjectDefinitionId_Last(
			long objectDefinitionId,
			OrderByComparator<ObjectDefinitionSetting> orderByComparator)
		throws NoSuchObjectDefinitionSettingException {

		ObjectDefinitionSetting objectDefinitionSetting =
			fetchByObjectDefinitionId_Last(
				objectDefinitionId, orderByComparator);

		if (objectDefinitionSetting != null) {
			return objectDefinitionSetting;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("objectDefinitionId=");
		sb.append(objectDefinitionId);

		sb.append("}");

		throw new NoSuchObjectDefinitionSettingException(sb.toString());
	}

	/**
	 * Returns the last object definition setting in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition setting, or <code>null</code> if a matching object definition setting could not be found
	 */
	@Override
	public ObjectDefinitionSetting fetchByObjectDefinitionId_Last(
		long objectDefinitionId,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator) {

		int count = countByObjectDefinitionId(objectDefinitionId);

		if (count == 0) {
			return null;
		}

		List<ObjectDefinitionSetting> list = findByObjectDefinitionId(
			objectDefinitionId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object definition settings before and after the current object definition setting in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionSettingId the primary key of the current object definition setting
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a object definition setting with the primary key could not be found
	 */
	@Override
	public ObjectDefinitionSetting[] findByObjectDefinitionId_PrevAndNext(
			long objectDefinitionSettingId, long objectDefinitionId,
			OrderByComparator<ObjectDefinitionSetting> orderByComparator)
		throws NoSuchObjectDefinitionSettingException {

		ObjectDefinitionSetting objectDefinitionSetting = findByPrimaryKey(
			objectDefinitionSettingId);

		Session session = null;

		try {
			session = openSession();

			ObjectDefinitionSetting[] array =
				new ObjectDefinitionSettingImpl[3];

			array[0] = getByObjectDefinitionId_PrevAndNext(
				session, objectDefinitionSetting, objectDefinitionId,
				orderByComparator, true);

			array[1] = objectDefinitionSetting;

			array[2] = getByObjectDefinitionId_PrevAndNext(
				session, objectDefinitionSetting, objectDefinitionId,
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

	protected ObjectDefinitionSetting getByObjectDefinitionId_PrevAndNext(
		Session session, ObjectDefinitionSetting objectDefinitionSetting,
		long objectDefinitionId,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTDEFINITIONSETTING_WHERE);

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
			sb.append(ObjectDefinitionSettingModelImpl.ORDER_BY_JPQL);
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
						objectDefinitionSetting)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectDefinitionSetting> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object definition settings where objectDefinitionId = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 */
	@Override
	public void removeByObjectDefinitionId(long objectDefinitionId) {
		for (ObjectDefinitionSetting objectDefinitionSetting :
				findByObjectDefinitionId(
					objectDefinitionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(objectDefinitionSetting);
		}
	}

	/**
	 * Returns the number of object definition settings where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object definition settings
	 */
	@Override
	public int countByObjectDefinitionId(long objectDefinitionId) {
		FinderPath finderPath = _finderPathCountByObjectDefinitionId;

		Object[] finderArgs = new Object[] {objectDefinitionId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_OBJECTDEFINITIONSETTING_WHERE);

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
			"objectDefinitionSetting.objectDefinitionId = ?";

	private FinderPath _finderPathWithPaginationFindByC_N;
	private FinderPath _finderPathWithoutPaginationFindByC_N;
	private FinderPath _finderPathCountByC_N;

	/**
	 * Returns all the object definition settings where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching object definition settings
	 */
	@Override
	public List<ObjectDefinitionSetting> findByC_N(
		long companyId, String name) {

		return findByC_N(
			companyId, name, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object definition settings where companyId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @return the range of matching object definition settings
	 */
	@Override
	public List<ObjectDefinitionSetting> findByC_N(
		long companyId, String name, int start, int end) {

		return findByC_N(companyId, name, start, end, null);
	}

	/**
	 * Returns an ordered range of all the object definition settings where companyId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definition settings
	 */
	@Override
	public List<ObjectDefinitionSetting> findByC_N(
		long companyId, String name, int start, int end,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator) {

		return findByC_N(companyId, name, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object definition settings where companyId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definition settings
	 */
	@Override
	public List<ObjectDefinitionSetting> findByC_N(
		long companyId, String name, int start, int end,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator,
		boolean useFinderCache) {

		name = Objects.toString(name, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByC_N;
				finderArgs = new Object[] {companyId, name};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByC_N;
			finderArgs = new Object[] {
				companyId, name, start, end, orderByComparator
			};
		}

		List<ObjectDefinitionSetting> list = null;

		if (useFinderCache) {
			list = (List<ObjectDefinitionSetting>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ObjectDefinitionSetting objectDefinitionSetting : list) {
					if ((companyId != objectDefinitionSetting.getCompanyId()) ||
						!name.equals(objectDefinitionSetting.getName())) {

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

			sb.append(_SQL_SELECT_OBJECTDEFINITIONSETTING_WHERE);

			sb.append(_FINDER_COLUMN_C_N_COMPANYID_2);

			boolean bindName = false;

			if (name.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_N_NAME_3);
			}
			else {
				bindName = true;

				sb.append(_FINDER_COLUMN_C_N_NAME_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ObjectDefinitionSettingModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				if (bindName) {
					queryPos.add(name);
				}

				list = (List<ObjectDefinitionSetting>)QueryUtil.list(
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
	 * Returns the first object definition setting in the ordered set where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a matching object definition setting could not be found
	 */
	@Override
	public ObjectDefinitionSetting findByC_N_First(
			long companyId, String name,
			OrderByComparator<ObjectDefinitionSetting> orderByComparator)
		throws NoSuchObjectDefinitionSettingException {

		ObjectDefinitionSetting objectDefinitionSetting = fetchByC_N_First(
			companyId, name, orderByComparator);

		if (objectDefinitionSetting != null) {
			return objectDefinitionSetting;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", name=");
		sb.append(name);

		sb.append("}");

		throw new NoSuchObjectDefinitionSettingException(sb.toString());
	}

	/**
	 * Returns the first object definition setting in the ordered set where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition setting, or <code>null</code> if a matching object definition setting could not be found
	 */
	@Override
	public ObjectDefinitionSetting fetchByC_N_First(
		long companyId, String name,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator) {

		List<ObjectDefinitionSetting> list = findByC_N(
			companyId, name, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last object definition setting in the ordered set where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a matching object definition setting could not be found
	 */
	@Override
	public ObjectDefinitionSetting findByC_N_Last(
			long companyId, String name,
			OrderByComparator<ObjectDefinitionSetting> orderByComparator)
		throws NoSuchObjectDefinitionSettingException {

		ObjectDefinitionSetting objectDefinitionSetting = fetchByC_N_Last(
			companyId, name, orderByComparator);

		if (objectDefinitionSetting != null) {
			return objectDefinitionSetting;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", name=");
		sb.append(name);

		sb.append("}");

		throw new NoSuchObjectDefinitionSettingException(sb.toString());
	}

	/**
	 * Returns the last object definition setting in the ordered set where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition setting, or <code>null</code> if a matching object definition setting could not be found
	 */
	@Override
	public ObjectDefinitionSetting fetchByC_N_Last(
		long companyId, String name,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator) {

		int count = countByC_N(companyId, name);

		if (count == 0) {
			return null;
		}

		List<ObjectDefinitionSetting> list = findByC_N(
			companyId, name, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the object definition settings before and after the current object definition setting in the ordered set where companyId = &#63; and name = &#63;.
	 *
	 * @param objectDefinitionSettingId the primary key of the current object definition setting
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a object definition setting with the primary key could not be found
	 */
	@Override
	public ObjectDefinitionSetting[] findByC_N_PrevAndNext(
			long objectDefinitionSettingId, long companyId, String name,
			OrderByComparator<ObjectDefinitionSetting> orderByComparator)
		throws NoSuchObjectDefinitionSettingException {

		name = Objects.toString(name, "");

		ObjectDefinitionSetting objectDefinitionSetting = findByPrimaryKey(
			objectDefinitionSettingId);

		Session session = null;

		try {
			session = openSession();

			ObjectDefinitionSetting[] array =
				new ObjectDefinitionSettingImpl[3];

			array[0] = getByC_N_PrevAndNext(
				session, objectDefinitionSetting, companyId, name,
				orderByComparator, true);

			array[1] = objectDefinitionSetting;

			array[2] = getByC_N_PrevAndNext(
				session, objectDefinitionSetting, companyId, name,
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

	protected ObjectDefinitionSetting getByC_N_PrevAndNext(
		Session session, ObjectDefinitionSetting objectDefinitionSetting,
		long companyId, String name,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator,
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

		sb.append(_SQL_SELECT_OBJECTDEFINITIONSETTING_WHERE);

		sb.append(_FINDER_COLUMN_C_N_COMPANYID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_N_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_C_N_NAME_2);
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
			sb.append(ObjectDefinitionSettingModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (bindName) {
			queryPos.add(name);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						objectDefinitionSetting)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ObjectDefinitionSetting> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the object definition settings where companyId = &#63; and name = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 */
	@Override
	public void removeByC_N(long companyId, String name) {
		for (ObjectDefinitionSetting objectDefinitionSetting :
				findByC_N(
					companyId, name, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(objectDefinitionSetting);
		}
	}

	/**
	 * Returns the number of object definition settings where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching object definition settings
	 */
	@Override
	public int countByC_N(long companyId, String name) {
		name = Objects.toString(name, "");

		FinderPath finderPath = _finderPathCountByC_N;

		Object[] finderArgs = new Object[] {companyId, name};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_OBJECTDEFINITIONSETTING_WHERE);

			sb.append(_FINDER_COLUMN_C_N_COMPANYID_2);

			boolean bindName = false;

			if (name.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_N_NAME_3);
			}
			else {
				bindName = true;

				sb.append(_FINDER_COLUMN_C_N_NAME_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

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

	private static final String _FINDER_COLUMN_C_N_COMPANYID_2 =
		"objectDefinitionSetting.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_N_NAME_2 =
		"objectDefinitionSetting.name = ?";

	private static final String _FINDER_COLUMN_C_N_NAME_3 =
		"(objectDefinitionSetting.name IS NULL OR objectDefinitionSetting.name = '')";

	private FinderPath _finderPathFetchByODI_N;

	/**
	 * Returns the object definition setting where objectDefinitionId = &#63; and name = &#63; or throws a <code>NoSuchObjectDefinitionSettingException</code> if it could not be found.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the matching object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a matching object definition setting could not be found
	 */
	@Override
	public ObjectDefinitionSetting findByODI_N(
			long objectDefinitionId, String name)
		throws NoSuchObjectDefinitionSettingException {

		ObjectDefinitionSetting objectDefinitionSetting = fetchByODI_N(
			objectDefinitionId, name);

		if (objectDefinitionSetting == null) {
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

			throw new NoSuchObjectDefinitionSettingException(sb.toString());
		}

		return objectDefinitionSetting;
	}

	/**
	 * Returns the object definition setting where objectDefinitionId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the matching object definition setting, or <code>null</code> if a matching object definition setting could not be found
	 */
	@Override
	public ObjectDefinitionSetting fetchByODI_N(
		long objectDefinitionId, String name) {

		return fetchByODI_N(objectDefinitionId, name, true);
	}

	/**
	 * Returns the object definition setting where objectDefinitionId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object definition setting, or <code>null</code> if a matching object definition setting could not be found
	 */
	@Override
	public ObjectDefinitionSetting fetchByODI_N(
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

		if (result instanceof ObjectDefinitionSetting) {
			ObjectDefinitionSetting objectDefinitionSetting =
				(ObjectDefinitionSetting)result;

			if ((objectDefinitionId !=
					objectDefinitionSetting.getObjectDefinitionId()) ||
				!Objects.equals(name, objectDefinitionSetting.getName())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_OBJECTDEFINITIONSETTING_WHERE);

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

				List<ObjectDefinitionSetting> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByODI_N, finderArgs, list);
					}
				}
				else {
					ObjectDefinitionSetting objectDefinitionSetting = list.get(
						0);

					result = objectDefinitionSetting;

					cacheResult(objectDefinitionSetting);
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
			return (ObjectDefinitionSetting)result;
		}
	}

	/**
	 * Removes the object definition setting where objectDefinitionId = &#63; and name = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the object definition setting that was removed
	 */
	@Override
	public ObjectDefinitionSetting removeByODI_N(
			long objectDefinitionId, String name)
		throws NoSuchObjectDefinitionSettingException {

		ObjectDefinitionSetting objectDefinitionSetting = findByODI_N(
			objectDefinitionId, name);

		return remove(objectDefinitionSetting);
	}

	/**
	 * Returns the number of object definition settings where objectDefinitionId = &#63; and name = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the number of matching object definition settings
	 */
	@Override
	public int countByODI_N(long objectDefinitionId, String name) {
		ObjectDefinitionSetting objectDefinitionSetting = fetchByODI_N(
			objectDefinitionId, name);

		if (objectDefinitionSetting == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ODI_N_OBJECTDEFINITIONID_2 =
		"objectDefinitionSetting.objectDefinitionId = ? AND ";

	private static final String _FINDER_COLUMN_ODI_N_NAME_2 =
		"objectDefinitionSetting.name = ?";

	private static final String _FINDER_COLUMN_ODI_N_NAME_3 =
		"(objectDefinitionSetting.name IS NULL OR objectDefinitionSetting.name = '')";

	public ObjectDefinitionSettingPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ObjectDefinitionSetting.class);

		setModelImplClass(ObjectDefinitionSettingImpl.class);
		setModelPKClass(long.class);

		setTable(ObjectDefinitionSettingTable.INSTANCE);
	}

	/**
	 * Caches the object definition setting in the entity cache if it is enabled.
	 *
	 * @param objectDefinitionSetting the object definition setting
	 */
	@Override
	public void cacheResult(ObjectDefinitionSetting objectDefinitionSetting) {
		entityCache.putResult(
			ObjectDefinitionSettingImpl.class,
			objectDefinitionSetting.getPrimaryKey(), objectDefinitionSetting);

		finderCache.putResult(
			_finderPathFetchByODI_N,
			new Object[] {
				objectDefinitionSetting.getObjectDefinitionId(),
				objectDefinitionSetting.getName()
			},
			objectDefinitionSetting);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the object definition settings in the entity cache if it is enabled.
	 *
	 * @param objectDefinitionSettings the object definition settings
	 */
	@Override
	public void cacheResult(
		List<ObjectDefinitionSetting> objectDefinitionSettings) {

		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (objectDefinitionSettings.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (ObjectDefinitionSetting objectDefinitionSetting :
				objectDefinitionSettings) {

			if (entityCache.getResult(
					ObjectDefinitionSettingImpl.class,
					objectDefinitionSetting.getPrimaryKey()) == null) {

				cacheResult(objectDefinitionSetting);
			}
		}
	}

	/**
	 * Clears the cache for all object definition settings.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(ObjectDefinitionSettingImpl.class);

		finderCache.clearCache(ObjectDefinitionSettingImpl.class);
	}

	/**
	 * Clears the cache for the object definition setting.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(ObjectDefinitionSetting objectDefinitionSetting) {
		entityCache.removeResult(
			ObjectDefinitionSettingImpl.class, objectDefinitionSetting);
	}

	@Override
	public void clearCache(
		List<ObjectDefinitionSetting> objectDefinitionSettings) {

		for (ObjectDefinitionSetting objectDefinitionSetting :
				objectDefinitionSettings) {

			entityCache.removeResult(
				ObjectDefinitionSettingImpl.class, objectDefinitionSetting);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(ObjectDefinitionSettingImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				ObjectDefinitionSettingImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		ObjectDefinitionSettingModelImpl objectDefinitionSettingModelImpl) {

		Object[] args = new Object[] {
			objectDefinitionSettingModelImpl.getObjectDefinitionId(),
			objectDefinitionSettingModelImpl.getName()
		};

		finderCache.putResult(
			_finderPathFetchByODI_N, args, objectDefinitionSettingModelImpl);
	}

	/**
	 * Creates a new object definition setting with the primary key. Does not add the object definition setting to the database.
	 *
	 * @param objectDefinitionSettingId the primary key for the new object definition setting
	 * @return the new object definition setting
	 */
	@Override
	public ObjectDefinitionSetting create(long objectDefinitionSettingId) {
		ObjectDefinitionSetting objectDefinitionSetting =
			new ObjectDefinitionSettingImpl();

		objectDefinitionSetting.setNew(true);
		objectDefinitionSetting.setPrimaryKey(objectDefinitionSettingId);

		String uuid = PortalUUIDUtil.generate();

		objectDefinitionSetting.setUuid(uuid);

		objectDefinitionSetting.setCompanyId(CompanyThreadLocal.getCompanyId());

		return objectDefinitionSetting;
	}

	/**
	 * Removes the object definition setting with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectDefinitionSettingId the primary key of the object definition setting
	 * @return the object definition setting that was removed
	 * @throws NoSuchObjectDefinitionSettingException if a object definition setting with the primary key could not be found
	 */
	@Override
	public ObjectDefinitionSetting remove(long objectDefinitionSettingId)
		throws NoSuchObjectDefinitionSettingException {

		return remove((Serializable)objectDefinitionSettingId);
	}

	/**
	 * Removes the object definition setting with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the object definition setting
	 * @return the object definition setting that was removed
	 * @throws NoSuchObjectDefinitionSettingException if a object definition setting with the primary key could not be found
	 */
	@Override
	public ObjectDefinitionSetting remove(Serializable primaryKey)
		throws NoSuchObjectDefinitionSettingException {

		Session session = null;

		try {
			session = openSession();

			ObjectDefinitionSetting objectDefinitionSetting =
				(ObjectDefinitionSetting)session.get(
					ObjectDefinitionSettingImpl.class, primaryKey);

			if (objectDefinitionSetting == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchObjectDefinitionSettingException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(objectDefinitionSetting);
		}
		catch (NoSuchObjectDefinitionSettingException noSuchEntityException) {
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
	protected ObjectDefinitionSetting removeImpl(
		ObjectDefinitionSetting objectDefinitionSetting) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(objectDefinitionSetting)) {
				objectDefinitionSetting = (ObjectDefinitionSetting)session.get(
					ObjectDefinitionSettingImpl.class,
					objectDefinitionSetting.getPrimaryKeyObj());
			}

			if (objectDefinitionSetting != null) {
				session.delete(objectDefinitionSetting);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (objectDefinitionSetting != null) {
			clearCache(objectDefinitionSetting);
		}

		return objectDefinitionSetting;
	}

	@Override
	public ObjectDefinitionSetting updateImpl(
		ObjectDefinitionSetting objectDefinitionSetting) {

		boolean isNew = objectDefinitionSetting.isNew();

		if (!(objectDefinitionSetting instanceof
				ObjectDefinitionSettingModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(objectDefinitionSetting.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					objectDefinitionSetting);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in objectDefinitionSetting proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ObjectDefinitionSetting implementation " +
					objectDefinitionSetting.getClass());
		}

		ObjectDefinitionSettingModelImpl objectDefinitionSettingModelImpl =
			(ObjectDefinitionSettingModelImpl)objectDefinitionSetting;

		if (Validator.isNull(objectDefinitionSetting.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			objectDefinitionSetting.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (objectDefinitionSetting.getCreateDate() == null)) {
			if (serviceContext == null) {
				objectDefinitionSetting.setCreateDate(date);
			}
			else {
				objectDefinitionSetting.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!objectDefinitionSettingModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				objectDefinitionSetting.setModifiedDate(date);
			}
			else {
				objectDefinitionSetting.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(objectDefinitionSetting);
			}
			else {
				objectDefinitionSetting =
					(ObjectDefinitionSetting)session.merge(
						objectDefinitionSetting);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			ObjectDefinitionSettingImpl.class, objectDefinitionSettingModelImpl,
			false, true);

		cacheUniqueFindersCache(objectDefinitionSettingModelImpl);

		if (isNew) {
			objectDefinitionSetting.setNew(false);
		}

		objectDefinitionSetting.resetOriginalValues();

		return objectDefinitionSetting;
	}

	/**
	 * Returns the object definition setting with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the object definition setting
	 * @return the object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a object definition setting with the primary key could not be found
	 */
	@Override
	public ObjectDefinitionSetting findByPrimaryKey(Serializable primaryKey)
		throws NoSuchObjectDefinitionSettingException {

		ObjectDefinitionSetting objectDefinitionSetting = fetchByPrimaryKey(
			primaryKey);

		if (objectDefinitionSetting == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchObjectDefinitionSettingException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return objectDefinitionSetting;
	}

	/**
	 * Returns the object definition setting with the primary key or throws a <code>NoSuchObjectDefinitionSettingException</code> if it could not be found.
	 *
	 * @param objectDefinitionSettingId the primary key of the object definition setting
	 * @return the object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a object definition setting with the primary key could not be found
	 */
	@Override
	public ObjectDefinitionSetting findByPrimaryKey(
			long objectDefinitionSettingId)
		throws NoSuchObjectDefinitionSettingException {

		return findByPrimaryKey((Serializable)objectDefinitionSettingId);
	}

	/**
	 * Returns the object definition setting with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectDefinitionSettingId the primary key of the object definition setting
	 * @return the object definition setting, or <code>null</code> if a object definition setting with the primary key could not be found
	 */
	@Override
	public ObjectDefinitionSetting fetchByPrimaryKey(
		long objectDefinitionSettingId) {

		return fetchByPrimaryKey((Serializable)objectDefinitionSettingId);
	}

	/**
	 * Returns all the object definition settings.
	 *
	 * @return the object definition settings
	 */
	@Override
	public List<ObjectDefinitionSetting> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the object definition settings.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @return the range of object definition settings
	 */
	@Override
	public List<ObjectDefinitionSetting> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the object definition settings.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of object definition settings
	 */
	@Override
	public List<ObjectDefinitionSetting> findAll(
		int start, int end,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the object definition settings.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of object definition settings
	 */
	@Override
	public List<ObjectDefinitionSetting> findAll(
		int start, int end,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator,
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

		List<ObjectDefinitionSetting> list = null;

		if (useFinderCache) {
			list = (List<ObjectDefinitionSetting>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_OBJECTDEFINITIONSETTING);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_OBJECTDEFINITIONSETTING;

				sql = sql.concat(
					ObjectDefinitionSettingModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<ObjectDefinitionSetting>)QueryUtil.list(
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
	 * Removes all the object definition settings from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (ObjectDefinitionSetting objectDefinitionSetting : findAll()) {
			remove(objectDefinitionSetting);
		}
	}

	/**
	 * Returns the number of object definition settings.
	 *
	 * @return the number of object definition settings
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
					_SQL_COUNT_OBJECTDEFINITIONSETTING);

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
		return "objectDefinitionSettingId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OBJECTDEFINITIONSETTING;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ObjectDefinitionSettingModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the object definition setting persistence.
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

		_finderPathWithPaginationFindByC_N = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_N",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "name"}, true);

		_finderPathWithoutPaginationFindByC_N = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_N",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "name"}, true);

		_finderPathCountByC_N = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_N",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "name"}, false);

		_finderPathFetchByODI_N = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByODI_N",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"objectDefinitionId", "name"}, true);

		ObjectDefinitionSettingUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ObjectDefinitionSettingUtil.setPersistence(null);

		entityCache.removeCache(ObjectDefinitionSettingImpl.class.getName());
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

	private static final String _SQL_SELECT_OBJECTDEFINITIONSETTING =
		"SELECT objectDefinitionSetting FROM ObjectDefinitionSetting objectDefinitionSetting";

	private static final String _SQL_SELECT_OBJECTDEFINITIONSETTING_WHERE =
		"SELECT objectDefinitionSetting FROM ObjectDefinitionSetting objectDefinitionSetting WHERE ";

	private static final String _SQL_COUNT_OBJECTDEFINITIONSETTING =
		"SELECT COUNT(objectDefinitionSetting) FROM ObjectDefinitionSetting objectDefinitionSetting";

	private static final String _SQL_COUNT_OBJECTDEFINITIONSETTING_WHERE =
		"SELECT COUNT(objectDefinitionSetting) FROM ObjectDefinitionSetting objectDefinitionSetting WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS =
		"objectDefinitionSetting.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No ObjectDefinitionSetting exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ObjectDefinitionSetting exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectDefinitionSettingPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}