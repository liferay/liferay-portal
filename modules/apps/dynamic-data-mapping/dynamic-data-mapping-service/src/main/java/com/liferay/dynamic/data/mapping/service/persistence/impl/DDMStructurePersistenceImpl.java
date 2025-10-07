/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence.impl;

import com.liferay.dynamic.data.mapping.exception.NoSuchStructureException;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureTable;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureModelImpl;
import com.liferay.dynamic.data.mapping.service.persistence.DDMStructurePersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMStructureUtil;
import com.liferay.dynamic.data.mapping.service.persistence.impl.constants.DDMPersistenceConstants;
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
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.ArrayUtil;
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
 * The persistence implementation for the ddm structure service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = DDMStructurePersistence.class)
public class DDMStructurePersistenceImpl
	extends BasePersistenceImpl<DDMStructure>
	implements DDMStructurePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DDMStructureUtil</code> to access the ddm structure persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DDMStructureImpl.class.getName();

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
	 * Returns all the ddm structures where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm structures where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm structures where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddm structures where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMStructure.class)) {

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

			List<DDMStructure> list = null;

			if (useFinderCache) {
				list = (List<DDMStructure>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DDMStructure ddmStructure : list) {
						if (!uuid.equals(ddmStructure.getUuid())) {
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

				sb.append(_SQL_SELECT_DDMSTRUCTURE_WHERE);

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
					sb.append(DDMStructureModelImpl.ORDER_BY_JPQL);
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

					list = (List<DDMStructure>)QueryUtil.list(
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
	 * Returns the first ddm structure in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure findByUuid_First(
			String uuid, OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		DDMStructure ddmStructure = fetchByUuid_First(uuid, orderByComparator);

		if (ddmStructure != null) {
			return ddmStructure;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchStructureException(sb.toString());
	}

	/**
	 * Returns the first ddm structure in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByUuid_First(
		String uuid, OrderByComparator<DDMStructure> orderByComparator) {

		List<DDMStructure> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last ddm structure in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure findByUuid_Last(
			String uuid, OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		DDMStructure ddmStructure = fetchByUuid_Last(uuid, orderByComparator);

		if (ddmStructure != null) {
			return ddmStructure;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchStructureException(sb.toString());
	}

	/**
	 * Returns the last ddm structure in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByUuid_Last(
		String uuid, OrderByComparator<DDMStructure> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<DDMStructure> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set where uuid = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	@Override
	public DDMStructure[] findByUuid_PrevAndNext(
			long structureId, String uuid,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		uuid = Objects.toString(uuid, "");

		DDMStructure ddmStructure = findByPrimaryKey(structureId);

		Session session = null;

		try {
			session = openSession();

			DDMStructure[] array = new DDMStructureImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, ddmStructure, uuid, orderByComparator, true);

			array[1] = ddmStructure;

			array[2] = getByUuid_PrevAndNext(
				session, ddmStructure, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DDMStructure getByUuid_PrevAndNext(
		Session session, DDMStructure ddmStructure, String uuid,
		OrderByComparator<DDMStructure> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_DDMSTRUCTURE_WHERE);

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
			sb.append(DDMStructureModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(ddmStructure)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMStructure> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the ddm structures where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (DDMStructure ddmStructure :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(ddmStructure);
		}
	}

	/**
	 * Returns the number of ddm structures where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching ddm structures
	 */
	@Override
	public int countByUuid(String uuid) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMStructure.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid;

			Object[] finderArgs = new Object[] {uuid};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_DDMSTRUCTURE_WHERE);

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
		"ddmStructure.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(ddmStructure.uuid IS NULL OR ddmStructure.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;

	/**
	 * Returns the ddm structure where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchStructureException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure findByUUID_G(String uuid, long groupId)
		throws NoSuchStructureException {

		DDMStructure ddmStructure = fetchByUUID_G(uuid, groupId);

		if (ddmStructure == null) {
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

			throw new NoSuchStructureException(sb.toString());
		}

		return ddmStructure;
	}

	/**
	 * Returns the ddm structure where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the ddm structure where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMStructure.class)) {

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

			if (result instanceof DDMStructure) {
				DDMStructure ddmStructure = (DDMStructure)result;

				if (!Objects.equals(uuid, ddmStructure.getUuid()) ||
					(groupId != ddmStructure.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_DDMSTRUCTURE_WHERE);

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

					List<DDMStructure> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByUUID_G, finderArgs, list);
						}
					}
					else {
						DDMStructure ddmStructure = list.get(0);

						result = ddmStructure;

						cacheResult(ddmStructure);
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
				return (DDMStructure)result;
			}
		}
	}

	/**
	 * Removes the ddm structure where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the ddm structure that was removed
	 */
	@Override
	public DDMStructure removeByUUID_G(String uuid, long groupId)
		throws NoSuchStructureException {

		DDMStructure ddmStructure = findByUUID_G(uuid, groupId);

		return remove(ddmStructure);
	}

	/**
	 * Returns the number of ddm structures where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching ddm structures
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		DDMStructure ddmStructure = fetchByUUID_G(uuid, groupId);

		if (ddmStructure == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"ddmStructure.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(ddmStructure.uuid IS NULL OR ddmStructure.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"ddmStructure.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the ddm structures where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm structures where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm structures where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddm structures where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMStructure.class)) {

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

			List<DDMStructure> list = null;

			if (useFinderCache) {
				list = (List<DDMStructure>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DDMStructure ddmStructure : list) {
						if (!uuid.equals(ddmStructure.getUuid()) ||
							(companyId != ddmStructure.getCompanyId())) {

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

				sb.append(_SQL_SELECT_DDMSTRUCTURE_WHERE);

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
					sb.append(DDMStructureModelImpl.ORDER_BY_JPQL);
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

					list = (List<DDMStructure>)QueryUtil.list(
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
	 * Returns the first ddm structure in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		DDMStructure ddmStructure = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (ddmStructure != null) {
			return ddmStructure;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchStructureException(sb.toString());
	}

	/**
	 * Returns the first ddm structure in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<DDMStructure> orderByComparator) {

		List<DDMStructure> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last ddm structure in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		DDMStructure ddmStructure = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (ddmStructure != null) {
			return ddmStructure;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchStructureException(sb.toString());
	}

	/**
	 * Returns the last ddm structure in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<DDMStructure> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<DDMStructure> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	@Override
	public DDMStructure[] findByUuid_C_PrevAndNext(
			long structureId, String uuid, long companyId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		uuid = Objects.toString(uuid, "");

		DDMStructure ddmStructure = findByPrimaryKey(structureId);

		Session session = null;

		try {
			session = openSession();

			DDMStructure[] array = new DDMStructureImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, ddmStructure, uuid, companyId, orderByComparator,
				true);

			array[1] = ddmStructure;

			array[2] = getByUuid_C_PrevAndNext(
				session, ddmStructure, uuid, companyId, orderByComparator,
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

	protected DDMStructure getByUuid_C_PrevAndNext(
		Session session, DDMStructure ddmStructure, String uuid, long companyId,
		OrderByComparator<DDMStructure> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_DDMSTRUCTURE_WHERE);

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
			sb.append(DDMStructureModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(ddmStructure)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMStructure> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the ddm structures where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (DDMStructure ddmStructure :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(ddmStructure);
		}
	}

	/**
	 * Returns the number of ddm structures where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching ddm structures
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMStructure.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid_C;

			Object[] finderArgs = new Object[] {uuid, companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_DDMSTRUCTURE_WHERE);

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
		"ddmStructure.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(ddmStructure.uuid IS NULL OR ddmStructure.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"ddmStructure.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByGroupId;
	private FinderPath _finderPathWithoutPaginationFindByGroupId;
	private FinderPath _finderPathCountByGroupId;
	private FinderPath _finderPathWithPaginationCountByGroupId;

	/**
	 * Returns all the ddm structures where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm structures where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByGroupId(long groupId, int start, int end) {
		return findByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMStructure.class)) {

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

			List<DDMStructure> list = null;

			if (useFinderCache) {
				list = (List<DDMStructure>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DDMStructure ddmStructure : list) {
						if (groupId != ddmStructure.getGroupId()) {
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

				sb.append(_SQL_SELECT_DDMSTRUCTURE_WHERE);

				sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DDMStructureModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					list = (List<DDMStructure>)QueryUtil.list(
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
	 * Returns the first ddm structure in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure findByGroupId_First(
			long groupId, OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		DDMStructure ddmStructure = fetchByGroupId_First(
			groupId, orderByComparator);

		if (ddmStructure != null) {
			return ddmStructure;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchStructureException(sb.toString());
	}

	/**
	 * Returns the first ddm structure in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByGroupId_First(
		long groupId, OrderByComparator<DDMStructure> orderByComparator) {

		List<DDMStructure> list = findByGroupId(
			groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last ddm structure in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure findByGroupId_Last(
			long groupId, OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		DDMStructure ddmStructure = fetchByGroupId_Last(
			groupId, orderByComparator);

		if (ddmStructure != null) {
			return ddmStructure;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchStructureException(sb.toString());
	}

	/**
	 * Returns the last ddm structure in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByGroupId_Last(
		long groupId, OrderByComparator<DDMStructure> orderByComparator) {

		int count = countByGroupId(groupId);

		if (count == 0) {
			return null;
		}

		List<DDMStructure> list = findByGroupId(
			groupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set where groupId = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	@Override
	public DDMStructure[] findByGroupId_PrevAndNext(
			long structureId, long groupId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		DDMStructure ddmStructure = findByPrimaryKey(structureId);

		Session session = null;

		try {
			session = openSession();

			DDMStructure[] array = new DDMStructureImpl[3];

			array[0] = getByGroupId_PrevAndNext(
				session, ddmStructure, groupId, orderByComparator, true);

			array[1] = ddmStructure;

			array[2] = getByGroupId_PrevAndNext(
				session, ddmStructure, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DDMStructure getByGroupId_PrevAndNext(
		Session session, DDMStructure ddmStructure, long groupId,
		OrderByComparator<DDMStructure> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_DDMSTRUCTURE_WHERE);

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
			sb.append(DDMStructureModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(ddmStructure)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMStructure> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the ddm structures that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching ddm structures that the user has permission to view
	 */
	@Override
	public List<DDMStructure> filterFindByGroupId(long groupId) {
		return filterFindByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm structures that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures that the user has permission to view
	 */
	@Override
	public List<DDMStructure> filterFindByGroupId(
		long groupId, int start, int end) {

		return filterFindByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm structures that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures that the user has permission to view
	 */
	@Override
	public List<DDMStructure> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_DDMSTRUCTURE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DDMSTRUCTURE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DDMSTRUCTURE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DDMStructureModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DDMStructureModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMStructure.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, DDMStructureImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, DDMStructureImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			return (List<DDMStructure>)QueryUtil.list(
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
	 * Returns the ddm structures before and after the current ddm structure in the ordered set of ddm structures that the user has permission to view where groupId = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	@Override
	public DDMStructure[] filterFindByGroupId_PrevAndNext(
			long structureId, long groupId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByGroupId_PrevAndNext(
				structureId, groupId, orderByComparator);
		}

		DDMStructure ddmStructure = findByPrimaryKey(structureId);

		Session session = null;

		try {
			session = openSession();

			DDMStructure[] array = new DDMStructureImpl[3];

			array[0] = filterGetByGroupId_PrevAndNext(
				session, ddmStructure, groupId, orderByComparator, true);

			array[1] = ddmStructure;

			array[2] = filterGetByGroupId_PrevAndNext(
				session, ddmStructure, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DDMStructure filterGetByGroupId_PrevAndNext(
		Session session, DDMStructure ddmStructure, long groupId,
		OrderByComparator<DDMStructure> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_DDMSTRUCTURE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DDMSTRUCTURE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DDMSTRUCTURE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DDMStructureModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DDMStructureModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMStructure.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DDMStructureImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DDMStructureImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(ddmStructure)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMStructure> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the ddm structures that the user has permission to view where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the matching ddm structures that the user has permission to view
	 */
	@Override
	public List<DDMStructure> filterFindByGroupId(long[] groupIds) {
		return filterFindByGroupId(
			groupIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm structures that the user has permission to view where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures that the user has permission to view
	 */
	@Override
	public List<DDMStructure> filterFindByGroupId(
		long[] groupIds, int start, int end) {

		return filterFindByGroupId(groupIds, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm structures that the user has permission to view where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures that the user has permission to view
	 */
	@Override
	public List<DDMStructure> filterFindByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupIds)) {
			return findByGroupId(groupIds, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByGroupId(
					groupIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator),
				groupIds);
		}

		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		StringBundler sb = new StringBundler();

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_DDMSTRUCTURE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DDMSTRUCTURE_NO_INLINE_DISTINCT_WHERE_1);
		}

		if (groupIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_GROUPID_GROUPID_7);

			sb.append(StringUtil.merge(groupIds));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DDMSTRUCTURE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DDMStructureModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DDMStructureModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMStructure.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupIds);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, DDMStructureImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, DDMStructureImpl.class);
			}

			return (List<DDMStructure>)QueryUtil.list(
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
	 * Returns all the ddm structures where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @return the matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByGroupId(long[] groupIds) {
		return findByGroupId(
			groupIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm structures where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByGroupId(
		long[] groupIds, int start, int end) {

		return findByGroupId(groupIds, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm structures where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		return findByGroupId(groupIds, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		if (groupIds.length == 1) {
			return findByGroupId(groupIds[0], start, end, orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMStructure.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {StringUtil.merge(groupIds)};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					StringUtil.merge(groupIds), start, end, orderByComparator
				};
			}

			List<DDMStructure> list = null;

			if (useFinderCache) {
				list = (List<DDMStructure>)finderCache.getResult(
					_finderPathWithPaginationFindByGroupId, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DDMStructure ddmStructure : list) {
						if (!ArrayUtil.contains(
								groupIds, ddmStructure.getGroupId())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_SELECT_DDMSTRUCTURE_WHERE);

				if (groupIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_GROUPID_GROUPID_7);

					sb.append(StringUtil.merge(groupIds));

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
					sb.append(DDMStructureModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<DDMStructure>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(
							_finderPathWithPaginationFindByGroupId, finderArgs,
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
	 * Removes all the ddm structures where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		for (DDMStructure ddmStructure :
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(ddmStructure);
		}
	}

	/**
	 * Returns the number of ddm structures where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching ddm structures
	 */
	@Override
	public int countByGroupId(long groupId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMStructure.class)) {

			FinderPath finderPath = _finderPathCountByGroupId;

			Object[] finderArgs = new Object[] {groupId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_DDMSTRUCTURE_WHERE);

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
	 * Returns the number of ddm structures where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching ddm structures
	 */
	@Override
	public int countByGroupId(long[] groupIds) {
		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMStructure.class)) {

			Object[] finderArgs = new Object[] {StringUtil.merge(groupIds)};

			Long count = (Long)finderCache.getResult(
				_finderPathWithPaginationCountByGroupId, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_DDMSTRUCTURE_WHERE);

				if (groupIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_GROUPID_GROUPID_7);

					sb.append(StringUtil.merge(groupIds));

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

					count = (Long)query.uniqueResult();

					finderCache.putResult(
						_finderPathWithPaginationCountByGroupId, finderArgs,
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

	/**
	 * Returns the number of ddm structures that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching ddm structures that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByGroupId(groupId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DDMStructure> ddmStructures = findByGroupId(groupId);

			ddmStructures = InlineSQLHelperUtil.filter(ddmStructures, groupId);

			return ddmStructures.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_DDMSTRUCTURE_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMStructure.class.getName(),
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

	/**
	 * Returns the number of ddm structures that the user has permission to view where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching ddm structures that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long[] groupIds) {
		if (!InlineSQLHelperUtil.isEnabled(groupIds)) {
			return countByGroupId(groupIds);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DDMStructure> ddmStructures = InlineSQLHelperUtil.filter(
				findByGroupId(groupIds), groupIds);

			return ddmStructures.size();
		}

		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_DDMSTRUCTURE_WHERE);

		if (groupIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_GROUPID_GROUPID_7);

			sb.append(StringUtil.merge(groupIds));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMStructure.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupIds);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

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
		"ddmStructure.groupId = ?";

	private static final String _FINDER_COLUMN_GROUPID_GROUPID_7 =
		"ddmStructure.groupId IN (";

	private FinderPath _finderPathWithPaginationFindByParentStructureId;
	private FinderPath _finderPathWithoutPaginationFindByParentStructureId;
	private FinderPath _finderPathCountByParentStructureId;

	/**
	 * Returns all the ddm structures where parentStructureId = &#63;.
	 *
	 * @param parentStructureId the parent structure ID
	 * @return the matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByParentStructureId(long parentStructureId) {
		return findByParentStructureId(
			parentStructureId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm structures where parentStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param parentStructureId the parent structure ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByParentStructureId(
		long parentStructureId, int start, int end) {

		return findByParentStructureId(parentStructureId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm structures where parentStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param parentStructureId the parent structure ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByParentStructureId(
		long parentStructureId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		return findByParentStructureId(
			parentStructureId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddm structures where parentStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param parentStructureId the parent structure ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByParentStructureId(
		long parentStructureId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMStructure.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath =
						_finderPathWithoutPaginationFindByParentStructureId;
					finderArgs = new Object[] {parentStructureId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByParentStructureId;
				finderArgs = new Object[] {
					parentStructureId, start, end, orderByComparator
				};
			}

			List<DDMStructure> list = null;

			if (useFinderCache) {
				list = (List<DDMStructure>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DDMStructure ddmStructure : list) {
						if (parentStructureId !=
								ddmStructure.getParentStructureId()) {

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

				sb.append(_SQL_SELECT_DDMSTRUCTURE_WHERE);

				sb.append(_FINDER_COLUMN_PARENTSTRUCTUREID_PARENTSTRUCTUREID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DDMStructureModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(parentStructureId);

					list = (List<DDMStructure>)QueryUtil.list(
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
	 * Returns the first ddm structure in the ordered set where parentStructureId = &#63;.
	 *
	 * @param parentStructureId the parent structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure findByParentStructureId_First(
			long parentStructureId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		DDMStructure ddmStructure = fetchByParentStructureId_First(
			parentStructureId, orderByComparator);

		if (ddmStructure != null) {
			return ddmStructure;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("parentStructureId=");
		sb.append(parentStructureId);

		sb.append("}");

		throw new NoSuchStructureException(sb.toString());
	}

	/**
	 * Returns the first ddm structure in the ordered set where parentStructureId = &#63;.
	 *
	 * @param parentStructureId the parent structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByParentStructureId_First(
		long parentStructureId,
		OrderByComparator<DDMStructure> orderByComparator) {

		List<DDMStructure> list = findByParentStructureId(
			parentStructureId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last ddm structure in the ordered set where parentStructureId = &#63;.
	 *
	 * @param parentStructureId the parent structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure findByParentStructureId_Last(
			long parentStructureId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		DDMStructure ddmStructure = fetchByParentStructureId_Last(
			parentStructureId, orderByComparator);

		if (ddmStructure != null) {
			return ddmStructure;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("parentStructureId=");
		sb.append(parentStructureId);

		sb.append("}");

		throw new NoSuchStructureException(sb.toString());
	}

	/**
	 * Returns the last ddm structure in the ordered set where parentStructureId = &#63;.
	 *
	 * @param parentStructureId the parent structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByParentStructureId_Last(
		long parentStructureId,
		OrderByComparator<DDMStructure> orderByComparator) {

		int count = countByParentStructureId(parentStructureId);

		if (count == 0) {
			return null;
		}

		List<DDMStructure> list = findByParentStructureId(
			parentStructureId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set where parentStructureId = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param parentStructureId the parent structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	@Override
	public DDMStructure[] findByParentStructureId_PrevAndNext(
			long structureId, long parentStructureId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		DDMStructure ddmStructure = findByPrimaryKey(structureId);

		Session session = null;

		try {
			session = openSession();

			DDMStructure[] array = new DDMStructureImpl[3];

			array[0] = getByParentStructureId_PrevAndNext(
				session, ddmStructure, parentStructureId, orderByComparator,
				true);

			array[1] = ddmStructure;

			array[2] = getByParentStructureId_PrevAndNext(
				session, ddmStructure, parentStructureId, orderByComparator,
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

	protected DDMStructure getByParentStructureId_PrevAndNext(
		Session session, DDMStructure ddmStructure, long parentStructureId,
		OrderByComparator<DDMStructure> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_DDMSTRUCTURE_WHERE);

		sb.append(_FINDER_COLUMN_PARENTSTRUCTUREID_PARENTSTRUCTUREID_2);

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
			sb.append(DDMStructureModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(parentStructureId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(ddmStructure)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMStructure> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the ddm structures where parentStructureId = &#63; from the database.
	 *
	 * @param parentStructureId the parent structure ID
	 */
	@Override
	public void removeByParentStructureId(long parentStructureId) {
		for (DDMStructure ddmStructure :
				findByParentStructureId(
					parentStructureId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(ddmStructure);
		}
	}

	/**
	 * Returns the number of ddm structures where parentStructureId = &#63;.
	 *
	 * @param parentStructureId the parent structure ID
	 * @return the number of matching ddm structures
	 */
	@Override
	public int countByParentStructureId(long parentStructureId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMStructure.class)) {

			FinderPath finderPath = _finderPathCountByParentStructureId;

			Object[] finderArgs = new Object[] {parentStructureId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_DDMSTRUCTURE_WHERE);

				sb.append(_FINDER_COLUMN_PARENTSTRUCTUREID_PARENTSTRUCTUREID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(parentStructureId);

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

	private static final String
		_FINDER_COLUMN_PARENTSTRUCTUREID_PARENTSTRUCTUREID_2 =
			"ddmStructure.parentStructureId = ?";

	private FinderPath _finderPathWithPaginationFindByStructureKey;
	private FinderPath _finderPathWithoutPaginationFindByStructureKey;
	private FinderPath _finderPathCountByStructureKey;

	/**
	 * Returns all the ddm structures where structureKey = &#63;.
	 *
	 * @param structureKey the structure key
	 * @return the matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByStructureKey(String structureKey) {
		return findByStructureKey(
			structureKey, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm structures where structureKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param structureKey the structure key
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByStructureKey(
		String structureKey, int start, int end) {

		return findByStructureKey(structureKey, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm structures where structureKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param structureKey the structure key
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByStructureKey(
		String structureKey, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		return findByStructureKey(
			structureKey, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddm structures where structureKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param structureKey the structure key
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByStructureKey(
		String structureKey, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMStructure.class)) {

			structureKey = Objects.toString(structureKey, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByStructureKey;
					finderArgs = new Object[] {structureKey};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByStructureKey;
				finderArgs = new Object[] {
					structureKey, start, end, orderByComparator
				};
			}

			List<DDMStructure> list = null;

			if (useFinderCache) {
				list = (List<DDMStructure>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DDMStructure ddmStructure : list) {
						if (!structureKey.equals(
								ddmStructure.getStructureKey())) {

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

				sb.append(_SQL_SELECT_DDMSTRUCTURE_WHERE);

				boolean bindStructureKey = false;

				if (structureKey.isEmpty()) {
					sb.append(_FINDER_COLUMN_STRUCTUREKEY_STRUCTUREKEY_3);
				}
				else {
					bindStructureKey = true;

					sb.append(_FINDER_COLUMN_STRUCTUREKEY_STRUCTUREKEY_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DDMStructureModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindStructureKey) {
						queryPos.add(structureKey);
					}

					list = (List<DDMStructure>)QueryUtil.list(
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
	 * Returns the first ddm structure in the ordered set where structureKey = &#63;.
	 *
	 * @param structureKey the structure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure findByStructureKey_First(
			String structureKey,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		DDMStructure ddmStructure = fetchByStructureKey_First(
			structureKey, orderByComparator);

		if (ddmStructure != null) {
			return ddmStructure;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("structureKey=");
		sb.append(structureKey);

		sb.append("}");

		throw new NoSuchStructureException(sb.toString());
	}

	/**
	 * Returns the first ddm structure in the ordered set where structureKey = &#63;.
	 *
	 * @param structureKey the structure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByStructureKey_First(
		String structureKey,
		OrderByComparator<DDMStructure> orderByComparator) {

		List<DDMStructure> list = findByStructureKey(
			structureKey, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last ddm structure in the ordered set where structureKey = &#63;.
	 *
	 * @param structureKey the structure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure findByStructureKey_Last(
			String structureKey,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		DDMStructure ddmStructure = fetchByStructureKey_Last(
			structureKey, orderByComparator);

		if (ddmStructure != null) {
			return ddmStructure;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("structureKey=");
		sb.append(structureKey);

		sb.append("}");

		throw new NoSuchStructureException(sb.toString());
	}

	/**
	 * Returns the last ddm structure in the ordered set where structureKey = &#63;.
	 *
	 * @param structureKey the structure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByStructureKey_Last(
		String structureKey,
		OrderByComparator<DDMStructure> orderByComparator) {

		int count = countByStructureKey(structureKey);

		if (count == 0) {
			return null;
		}

		List<DDMStructure> list = findByStructureKey(
			structureKey, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set where structureKey = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param structureKey the structure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	@Override
	public DDMStructure[] findByStructureKey_PrevAndNext(
			long structureId, String structureKey,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		structureKey = Objects.toString(structureKey, "");

		DDMStructure ddmStructure = findByPrimaryKey(structureId);

		Session session = null;

		try {
			session = openSession();

			DDMStructure[] array = new DDMStructureImpl[3];

			array[0] = getByStructureKey_PrevAndNext(
				session, ddmStructure, structureKey, orderByComparator, true);

			array[1] = ddmStructure;

			array[2] = getByStructureKey_PrevAndNext(
				session, ddmStructure, structureKey, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DDMStructure getByStructureKey_PrevAndNext(
		Session session, DDMStructure ddmStructure, String structureKey,
		OrderByComparator<DDMStructure> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_DDMSTRUCTURE_WHERE);

		boolean bindStructureKey = false;

		if (structureKey.isEmpty()) {
			sb.append(_FINDER_COLUMN_STRUCTUREKEY_STRUCTUREKEY_3);
		}
		else {
			bindStructureKey = true;

			sb.append(_FINDER_COLUMN_STRUCTUREKEY_STRUCTUREKEY_2);
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
			sb.append(DDMStructureModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindStructureKey) {
			queryPos.add(structureKey);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(ddmStructure)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMStructure> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the ddm structures where structureKey = &#63; from the database.
	 *
	 * @param structureKey the structure key
	 */
	@Override
	public void removeByStructureKey(String structureKey) {
		for (DDMStructure ddmStructure :
				findByStructureKey(
					structureKey, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(ddmStructure);
		}
	}

	/**
	 * Returns the number of ddm structures where structureKey = &#63;.
	 *
	 * @param structureKey the structure key
	 * @return the number of matching ddm structures
	 */
	@Override
	public int countByStructureKey(String structureKey) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMStructure.class)) {

			structureKey = Objects.toString(structureKey, "");

			FinderPath finderPath = _finderPathCountByStructureKey;

			Object[] finderArgs = new Object[] {structureKey};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_DDMSTRUCTURE_WHERE);

				boolean bindStructureKey = false;

				if (structureKey.isEmpty()) {
					sb.append(_FINDER_COLUMN_STRUCTUREKEY_STRUCTUREKEY_3);
				}
				else {
					bindStructureKey = true;

					sb.append(_FINDER_COLUMN_STRUCTUREKEY_STRUCTUREKEY_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindStructureKey) {
						queryPos.add(structureKey);
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

	private static final String _FINDER_COLUMN_STRUCTUREKEY_STRUCTUREKEY_2 =
		"ddmStructure.structureKey = ?";

	private static final String _FINDER_COLUMN_STRUCTUREKEY_STRUCTUREKEY_3 =
		"(ddmStructure.structureKey IS NULL OR ddmStructure.structureKey = '')";

	private FinderPath _finderPathWithPaginationFindByG_P;
	private FinderPath _finderPathWithoutPaginationFindByG_P;
	private FinderPath _finderPathCountByG_P;

	/**
	 * Returns all the ddm structures where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @return the matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByG_P(long groupId, long parentStructureId) {
		return findByG_P(
			groupId, parentStructureId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the ddm structures where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByG_P(
		long groupId, long parentStructureId, int start, int end) {

		return findByG_P(groupId, parentStructureId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByG_P(
		long groupId, long parentStructureId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		return findByG_P(
			groupId, parentStructureId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByG_P(
		long groupId, long parentStructureId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMStructure.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_P;
					finderArgs = new Object[] {groupId, parentStructureId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_P;
				finderArgs = new Object[] {
					groupId, parentStructureId, start, end, orderByComparator
				};
			}

			List<DDMStructure> list = null;

			if (useFinderCache) {
				list = (List<DDMStructure>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DDMStructure ddmStructure : list) {
						if ((groupId != ddmStructure.getGroupId()) ||
							(parentStructureId !=
								ddmStructure.getParentStructureId())) {

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

				sb.append(_SQL_SELECT_DDMSTRUCTURE_WHERE);

				sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_PARENTSTRUCTUREID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DDMStructureModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(parentStructureId);

					list = (List<DDMStructure>)QueryUtil.list(
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
	 * Returns the first ddm structure in the ordered set where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure findByG_P_First(
			long groupId, long parentStructureId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		DDMStructure ddmStructure = fetchByG_P_First(
			groupId, parentStructureId, orderByComparator);

		if (ddmStructure != null) {
			return ddmStructure;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", parentStructureId=");
		sb.append(parentStructureId);

		sb.append("}");

		throw new NoSuchStructureException(sb.toString());
	}

	/**
	 * Returns the first ddm structure in the ordered set where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByG_P_First(
		long groupId, long parentStructureId,
		OrderByComparator<DDMStructure> orderByComparator) {

		List<DDMStructure> list = findByG_P(
			groupId, parentStructureId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last ddm structure in the ordered set where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure findByG_P_Last(
			long groupId, long parentStructureId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		DDMStructure ddmStructure = fetchByG_P_Last(
			groupId, parentStructureId, orderByComparator);

		if (ddmStructure != null) {
			return ddmStructure;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", parentStructureId=");
		sb.append(parentStructureId);

		sb.append("}");

		throw new NoSuchStructureException(sb.toString());
	}

	/**
	 * Returns the last ddm structure in the ordered set where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByG_P_Last(
		long groupId, long parentStructureId,
		OrderByComparator<DDMStructure> orderByComparator) {

		int count = countByG_P(groupId, parentStructureId);

		if (count == 0) {
			return null;
		}

		List<DDMStructure> list = findByG_P(
			groupId, parentStructureId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	@Override
	public DDMStructure[] findByG_P_PrevAndNext(
			long structureId, long groupId, long parentStructureId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		DDMStructure ddmStructure = findByPrimaryKey(structureId);

		Session session = null;

		try {
			session = openSession();

			DDMStructure[] array = new DDMStructureImpl[3];

			array[0] = getByG_P_PrevAndNext(
				session, ddmStructure, groupId, parentStructureId,
				orderByComparator, true);

			array[1] = ddmStructure;

			array[2] = getByG_P_PrevAndNext(
				session, ddmStructure, groupId, parentStructureId,
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

	protected DDMStructure getByG_P_PrevAndNext(
		Session session, DDMStructure ddmStructure, long groupId,
		long parentStructureId,
		OrderByComparator<DDMStructure> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_DDMSTRUCTURE_WHERE);

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_PARENTSTRUCTUREID_2);

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
			sb.append(DDMStructureModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(parentStructureId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(ddmStructure)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMStructure> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the ddm structures that the user has permission to view where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @return the matching ddm structures that the user has permission to view
	 */
	@Override
	public List<DDMStructure> filterFindByG_P(
		long groupId, long parentStructureId) {

		return filterFindByG_P(
			groupId, parentStructureId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the ddm structures that the user has permission to view where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures that the user has permission to view
	 */
	@Override
	public List<DDMStructure> filterFindByG_P(
		long groupId, long parentStructureId, int start, int end) {

		return filterFindByG_P(groupId, parentStructureId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm structures that the user has permissions to view where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures that the user has permission to view
	 */
	@Override
	public List<DDMStructure> filterFindByG_P(
		long groupId, long parentStructureId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P(
				groupId, parentStructureId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P(
					groupId, parentStructureId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
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
			sb.append(_FILTER_SQL_SELECT_DDMSTRUCTURE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DDMSTRUCTURE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_PARENTSTRUCTUREID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DDMSTRUCTURE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DDMStructureModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DDMStructureModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMStructure.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, DDMStructureImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, DDMStructureImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(parentStructureId);

			return (List<DDMStructure>)QueryUtil.list(
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
	 * Returns the ddm structures before and after the current ddm structure in the ordered set of ddm structures that the user has permission to view where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	@Override
	public DDMStructure[] filterFindByG_P_PrevAndNext(
			long structureId, long groupId, long parentStructureId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_PrevAndNext(
				structureId, groupId, parentStructureId, orderByComparator);
		}

		DDMStructure ddmStructure = findByPrimaryKey(structureId);

		Session session = null;

		try {
			session = openSession();

			DDMStructure[] array = new DDMStructureImpl[3];

			array[0] = filterGetByG_P_PrevAndNext(
				session, ddmStructure, groupId, parentStructureId,
				orderByComparator, true);

			array[1] = ddmStructure;

			array[2] = filterGetByG_P_PrevAndNext(
				session, ddmStructure, groupId, parentStructureId,
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

	protected DDMStructure filterGetByG_P_PrevAndNext(
		Session session, DDMStructure ddmStructure, long groupId,
		long parentStructureId,
		OrderByComparator<DDMStructure> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_DDMSTRUCTURE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DDMSTRUCTURE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_PARENTSTRUCTUREID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DDMSTRUCTURE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DDMStructureModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DDMStructureModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMStructure.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DDMStructureImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DDMStructureImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(parentStructureId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(ddmStructure)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMStructure> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the ddm structures where groupId = &#63; and parentStructureId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 */
	@Override
	public void removeByG_P(long groupId, long parentStructureId) {
		for (DDMStructure ddmStructure :
				findByG_P(
					groupId, parentStructureId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(ddmStructure);
		}
	}

	/**
	 * Returns the number of ddm structures where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @return the number of matching ddm structures
	 */
	@Override
	public int countByG_P(long groupId, long parentStructureId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMStructure.class)) {

			FinderPath finderPath = _finderPathCountByG_P;

			Object[] finderArgs = new Object[] {groupId, parentStructureId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_DDMSTRUCTURE_WHERE);

				sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_PARENTSTRUCTUREID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(parentStructureId);

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
	 * Returns the number of ddm structures that the user has permission to view where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @return the number of matching ddm structures that the user has permission to view
	 */
	@Override
	public int filterCountByG_P(long groupId, long parentStructureId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P(groupId, parentStructureId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DDMStructure> ddmStructures = findByG_P(
				groupId, parentStructureId);

			ddmStructures = InlineSQLHelperUtil.filter(ddmStructures, groupId);

			return ddmStructures.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_DDMSTRUCTURE_WHERE);

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_PARENTSTRUCTUREID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMStructure.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(parentStructureId);

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

	private static final String _FINDER_COLUMN_G_P_GROUPID_2 =
		"ddmStructure.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_PARENTSTRUCTUREID_2 =
		"ddmStructure.parentStructureId = ?";

	private FinderPath _finderPathWithPaginationFindByG_C;
	private FinderPath _finderPathWithoutPaginationFindByG_C;
	private FinderPath _finderPathCountByG_C;
	private FinderPath _finderPathWithPaginationCountByG_C;

	/**
	 * Returns all the ddm structures where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByG_C(long groupId, long classNameId) {
		return findByG_C(
			groupId, classNameId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm structures where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByG_C(
		long groupId, long classNameId, int start, int end) {

		return findByG_C(groupId, classNameId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByG_C(
		long groupId, long classNameId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		return findByG_C(
			groupId, classNameId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByG_C(
		long groupId, long classNameId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMStructure.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_C;
					finderArgs = new Object[] {groupId, classNameId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_C;
				finderArgs = new Object[] {
					groupId, classNameId, start, end, orderByComparator
				};
			}

			List<DDMStructure> list = null;

			if (useFinderCache) {
				list = (List<DDMStructure>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DDMStructure ddmStructure : list) {
						if ((groupId != ddmStructure.getGroupId()) ||
							(classNameId != ddmStructure.getClassNameId())) {

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

				sb.append(_SQL_SELECT_DDMSTRUCTURE_WHERE);

				sb.append(_FINDER_COLUMN_G_C_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_CLASSNAMEID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DDMStructureModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(classNameId);

					list = (List<DDMStructure>)QueryUtil.list(
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
	 * Returns the first ddm structure in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure findByG_C_First(
			long groupId, long classNameId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		DDMStructure ddmStructure = fetchByG_C_First(
			groupId, classNameId, orderByComparator);

		if (ddmStructure != null) {
			return ddmStructure;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append("}");

		throw new NoSuchStructureException(sb.toString());
	}

	/**
	 * Returns the first ddm structure in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByG_C_First(
		long groupId, long classNameId,
		OrderByComparator<DDMStructure> orderByComparator) {

		List<DDMStructure> list = findByG_C(
			groupId, classNameId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last ddm structure in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure findByG_C_Last(
			long groupId, long classNameId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		DDMStructure ddmStructure = fetchByG_C_Last(
			groupId, classNameId, orderByComparator);

		if (ddmStructure != null) {
			return ddmStructure;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append("}");

		throw new NoSuchStructureException(sb.toString());
	}

	/**
	 * Returns the last ddm structure in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByG_C_Last(
		long groupId, long classNameId,
		OrderByComparator<DDMStructure> orderByComparator) {

		int count = countByG_C(groupId, classNameId);

		if (count == 0) {
			return null;
		}

		List<DDMStructure> list = findByG_C(
			groupId, classNameId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	@Override
	public DDMStructure[] findByG_C_PrevAndNext(
			long structureId, long groupId, long classNameId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		DDMStructure ddmStructure = findByPrimaryKey(structureId);

		Session session = null;

		try {
			session = openSession();

			DDMStructure[] array = new DDMStructureImpl[3];

			array[0] = getByG_C_PrevAndNext(
				session, ddmStructure, groupId, classNameId, orderByComparator,
				true);

			array[1] = ddmStructure;

			array[2] = getByG_C_PrevAndNext(
				session, ddmStructure, groupId, classNameId, orderByComparator,
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

	protected DDMStructure getByG_C_PrevAndNext(
		Session session, DDMStructure ddmStructure, long groupId,
		long classNameId, OrderByComparator<DDMStructure> orderByComparator,
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

		sb.append(_SQL_SELECT_DDMSTRUCTURE_WHERE);

		sb.append(_FINDER_COLUMN_G_C_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_CLASSNAMEID_2);

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
			sb.append(DDMStructureModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(ddmStructure)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMStructure> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the ddm structures that the user has permission to view where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the matching ddm structures that the user has permission to view
	 */
	@Override
	public List<DDMStructure> filterFindByG_C(long groupId, long classNameId) {
		return filterFindByG_C(
			groupId, classNameId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm structures that the user has permission to view where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures that the user has permission to view
	 */
	@Override
	public List<DDMStructure> filterFindByG_C(
		long groupId, long classNameId, int start, int end) {

		return filterFindByG_C(groupId, classNameId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm structures that the user has permissions to view where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures that the user has permission to view
	 */
	@Override
	public List<DDMStructure> filterFindByG_C(
		long groupId, long classNameId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_C(
				groupId, classNameId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_C(
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
			sb.append(_FILTER_SQL_SELECT_DDMSTRUCTURE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DDMSTRUCTURE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_C_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_CLASSNAMEID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DDMSTRUCTURE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DDMStructureModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DDMStructureModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMStructure.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, DDMStructureImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, DDMStructureImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(classNameId);

			return (List<DDMStructure>)QueryUtil.list(
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
	 * Returns the ddm structures before and after the current ddm structure in the ordered set of ddm structures that the user has permission to view where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	@Override
	public DDMStructure[] filterFindByG_C_PrevAndNext(
			long structureId, long groupId, long classNameId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_C_PrevAndNext(
				structureId, groupId, classNameId, orderByComparator);
		}

		DDMStructure ddmStructure = findByPrimaryKey(structureId);

		Session session = null;

		try {
			session = openSession();

			DDMStructure[] array = new DDMStructureImpl[3];

			array[0] = filterGetByG_C_PrevAndNext(
				session, ddmStructure, groupId, classNameId, orderByComparator,
				true);

			array[1] = ddmStructure;

			array[2] = filterGetByG_C_PrevAndNext(
				session, ddmStructure, groupId, classNameId, orderByComparator,
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

	protected DDMStructure filterGetByG_C_PrevAndNext(
		Session session, DDMStructure ddmStructure, long groupId,
		long classNameId, OrderByComparator<DDMStructure> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_DDMSTRUCTURE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DDMSTRUCTURE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_C_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_CLASSNAMEID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DDMSTRUCTURE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DDMStructureModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DDMStructureModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMStructure.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DDMStructureImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DDMStructureImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(classNameId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(ddmStructure)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMStructure> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the ddm structures that the user has permission to view where groupId = any &#63; and classNameId = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @return the matching ddm structures that the user has permission to view
	 */
	@Override
	public List<DDMStructure> filterFindByG_C(
		long[] groupIds, long classNameId) {

		return filterFindByG_C(
			groupIds, classNameId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm structures that the user has permission to view where groupId = any &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures that the user has permission to view
	 */
	@Override
	public List<DDMStructure> filterFindByG_C(
		long[] groupIds, long classNameId, int start, int end) {

		return filterFindByG_C(groupIds, classNameId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm structures that the user has permission to view where groupId = any &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures that the user has permission to view
	 */
	@Override
	public List<DDMStructure> filterFindByG_C(
		long[] groupIds, long classNameId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupIds)) {
			return findByG_C(
				groupIds, classNameId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_C(
					groupIds, classNameId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator),
				groupIds);
		}

		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		StringBundler sb = new StringBundler();

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_DDMSTRUCTURE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DDMSTRUCTURE_NO_INLINE_DISTINCT_WHERE_1);
		}

		if (groupIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_C_GROUPID_7);

			sb.append(StringUtil.merge(groupIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_G_C_CLASSNAMEID_2);

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DDMSTRUCTURE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DDMStructureModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DDMStructureModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMStructure.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupIds);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, DDMStructureImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, DDMStructureImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(classNameId);

			return (List<DDMStructure>)QueryUtil.list(
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
	 * Returns all the ddm structures where groupId = any &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @return the matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByG_C(long[] groupIds, long classNameId) {
		return findByG_C(
			groupIds, classNameId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm structures where groupId = any &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByG_C(
		long[] groupIds, long classNameId, int start, int end) {

		return findByG_C(groupIds, classNameId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm structures where groupId = any &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByG_C(
		long[] groupIds, long classNameId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		return findByG_C(
			groupIds, classNameId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63; and classNameId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByG_C(
		long[] groupIds, long classNameId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		if (groupIds.length == 1) {
			return findByG_C(
				groupIds[0], classNameId, start, end, orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMStructure.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						StringUtil.merge(groupIds), classNameId
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					StringUtil.merge(groupIds), classNameId, start, end,
					orderByComparator
				};
			}

			List<DDMStructure> list = null;

			if (useFinderCache) {
				list = (List<DDMStructure>)finderCache.getResult(
					_finderPathWithPaginationFindByG_C, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DDMStructure ddmStructure : list) {
						if (!ArrayUtil.contains(
								groupIds, ddmStructure.getGroupId()) ||
							(classNameId != ddmStructure.getClassNameId())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_SELECT_DDMSTRUCTURE_WHERE);

				if (groupIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_C_GROUPID_7);

					sb.append(StringUtil.merge(groupIds));

					sb.append(")");

					sb.append(")");

					sb.append(WHERE_AND);
				}

				sb.append(_FINDER_COLUMN_G_C_CLASSNAMEID_2);

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DDMStructureModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(classNameId);

					list = (List<DDMStructure>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(
							_finderPathWithPaginationFindByG_C, finderArgs,
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
	 * Removes all the ddm structures where groupId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByG_C(long groupId, long classNameId) {
		for (DDMStructure ddmStructure :
				findByG_C(
					groupId, classNameId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(ddmStructure);
		}
	}

	/**
	 * Returns the number of ddm structures where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the number of matching ddm structures
	 */
	@Override
	public int countByG_C(long groupId, long classNameId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMStructure.class)) {

			FinderPath finderPath = _finderPathCountByG_C;

			Object[] finderArgs = new Object[] {groupId, classNameId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_DDMSTRUCTURE_WHERE);

				sb.append(_FINDER_COLUMN_G_C_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_CLASSNAMEID_2);

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
	 * Returns the number of ddm structures where groupId = any &#63; and classNameId = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @return the number of matching ddm structures
	 */
	@Override
	public int countByG_C(long[] groupIds, long classNameId) {
		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMStructure.class)) {

			Object[] finderArgs = new Object[] {
				StringUtil.merge(groupIds), classNameId
			};

			Long count = (Long)finderCache.getResult(
				_finderPathWithPaginationCountByG_C, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_DDMSTRUCTURE_WHERE);

				if (groupIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_C_GROUPID_7);

					sb.append(StringUtil.merge(groupIds));

					sb.append(")");

					sb.append(")");

					sb.append(WHERE_AND);
				}

				sb.append(_FINDER_COLUMN_G_C_CLASSNAMEID_2);

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(classNameId);

					count = (Long)query.uniqueResult();

					finderCache.putResult(
						_finderPathWithPaginationCountByG_C, finderArgs, count);
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
	 * Returns the number of ddm structures that the user has permission to view where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the number of matching ddm structures that the user has permission to view
	 */
	@Override
	public int filterCountByG_C(long groupId, long classNameId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_C(groupId, classNameId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DDMStructure> ddmStructures = findByG_C(groupId, classNameId);

			ddmStructures = InlineSQLHelperUtil.filter(ddmStructures, groupId);

			return ddmStructures.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_DDMSTRUCTURE_WHERE);

		sb.append(_FINDER_COLUMN_G_C_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_CLASSNAMEID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMStructure.class.getName(),
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

	/**
	 * Returns the number of ddm structures that the user has permission to view where groupId = any &#63; and classNameId = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @return the number of matching ddm structures that the user has permission to view
	 */
	@Override
	public int filterCountByG_C(long[] groupIds, long classNameId) {
		if (!InlineSQLHelperUtil.isEnabled(groupIds)) {
			return countByG_C(groupIds, classNameId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DDMStructure> ddmStructures = InlineSQLHelperUtil.filter(
				findByG_C(groupIds, classNameId), groupIds);

			return ddmStructures.size();
		}

		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_DDMSTRUCTURE_WHERE);

		if (groupIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_C_GROUPID_7);

			sb.append(StringUtil.merge(groupIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_G_C_CLASSNAMEID_2);

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMStructure.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupIds);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

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

	private static final String _FINDER_COLUMN_G_C_GROUPID_2 =
		"ddmStructure.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_GROUPID_7 =
		"ddmStructure.groupId IN (";

	private static final String _FINDER_COLUMN_G_C_CLASSNAMEID_2 =
		"ddmStructure.classNameId = ?";

	private FinderPath _finderPathWithPaginationFindByC_C;
	private FinderPath _finderPathWithoutPaginationFindByC_C;
	private FinderPath _finderPathCountByC_C;

	/**
	 * Returns all the ddm structures where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByC_C(long companyId, long classNameId) {
		return findByC_C(
			companyId, classNameId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm structures where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByC_C(
		long companyId, long classNameId, int start, int end) {

		return findByC_C(companyId, classNameId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm structures where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByC_C(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		return findByC_C(
			companyId, classNameId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddm structures where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByC_C(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMStructure.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_C;
					finderArgs = new Object[] {companyId, classNameId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_C;
				finderArgs = new Object[] {
					companyId, classNameId, start, end, orderByComparator
				};
			}

			List<DDMStructure> list = null;

			if (useFinderCache) {
				list = (List<DDMStructure>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DDMStructure ddmStructure : list) {
						if ((companyId != ddmStructure.getCompanyId()) ||
							(classNameId != ddmStructure.getClassNameId())) {

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

				sb.append(_SQL_SELECT_DDMSTRUCTURE_WHERE);

				sb.append(_FINDER_COLUMN_C_C_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_C_CLASSNAMEID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DDMStructureModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(classNameId);

					list = (List<DDMStructure>)QueryUtil.list(
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
	 * Returns the first ddm structure in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure findByC_C_First(
			long companyId, long classNameId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		DDMStructure ddmStructure = fetchByC_C_First(
			companyId, classNameId, orderByComparator);

		if (ddmStructure != null) {
			return ddmStructure;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append("}");

		throw new NoSuchStructureException(sb.toString());
	}

	/**
	 * Returns the first ddm structure in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByC_C_First(
		long companyId, long classNameId,
		OrderByComparator<DDMStructure> orderByComparator) {

		List<DDMStructure> list = findByC_C(
			companyId, classNameId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last ddm structure in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure findByC_C_Last(
			long companyId, long classNameId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		DDMStructure ddmStructure = fetchByC_C_Last(
			companyId, classNameId, orderByComparator);

		if (ddmStructure != null) {
			return ddmStructure;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append("}");

		throw new NoSuchStructureException(sb.toString());
	}

	/**
	 * Returns the last ddm structure in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByC_C_Last(
		long companyId, long classNameId,
		OrderByComparator<DDMStructure> orderByComparator) {

		int count = countByC_C(companyId, classNameId);

		if (count == 0) {
			return null;
		}

		List<DDMStructure> list = findByC_C(
			companyId, classNameId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	@Override
	public DDMStructure[] findByC_C_PrevAndNext(
			long structureId, long companyId, long classNameId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		DDMStructure ddmStructure = findByPrimaryKey(structureId);

		Session session = null;

		try {
			session = openSession();

			DDMStructure[] array = new DDMStructureImpl[3];

			array[0] = getByC_C_PrevAndNext(
				session, ddmStructure, companyId, classNameId,
				orderByComparator, true);

			array[1] = ddmStructure;

			array[2] = getByC_C_PrevAndNext(
				session, ddmStructure, companyId, classNameId,
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

	protected DDMStructure getByC_C_PrevAndNext(
		Session session, DDMStructure ddmStructure, long companyId,
		long classNameId, OrderByComparator<DDMStructure> orderByComparator,
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

		sb.append(_SQL_SELECT_DDMSTRUCTURE_WHERE);

		sb.append(_FINDER_COLUMN_C_C_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_C_CLASSNAMEID_2);

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
			sb.append(DDMStructureModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(classNameId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(ddmStructure)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMStructure> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the ddm structures where companyId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByC_C(long companyId, long classNameId) {
		for (DDMStructure ddmStructure :
				findByC_C(
					companyId, classNameId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(ddmStructure);
		}
	}

	/**
	 * Returns the number of ddm structures where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching ddm structures
	 */
	@Override
	public int countByC_C(long companyId, long classNameId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMStructure.class)) {

			FinderPath finderPath = _finderPathCountByC_C;

			Object[] finderArgs = new Object[] {companyId, classNameId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_DDMSTRUCTURE_WHERE);

				sb.append(_FINDER_COLUMN_C_C_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_C_CLASSNAMEID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

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

	private static final String _FINDER_COLUMN_C_C_COMPANYID_2 =
		"ddmStructure.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_C_CLASSNAMEID_2 =
		"ddmStructure.classNameId = ?";

	private FinderPath _finderPathFetchByERC_G_C;

	/**
	 * Returns the ddm structure where externalReferenceCode = &#63; and groupId = &#63; and classNameId = &#63; or throws a <code>NoSuchStructureException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure findByERC_G_C(
			String externalReferenceCode, long groupId, long classNameId)
		throws NoSuchStructureException {

		DDMStructure ddmStructure = fetchByERC_G_C(
			externalReferenceCode, groupId, classNameId);

		if (ddmStructure == null) {
			StringBundler sb = new StringBundler(8);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("externalReferenceCode=");
			sb.append(externalReferenceCode);

			sb.append(", groupId=");
			sb.append(groupId);

			sb.append(", classNameId=");
			sb.append(classNameId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchStructureException(sb.toString());
		}

		return ddmStructure;
	}

	/**
	 * Returns the ddm structure where externalReferenceCode = &#63; and groupId = &#63; and classNameId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByERC_G_C(
		String externalReferenceCode, long groupId, long classNameId) {

		return fetchByERC_G_C(
			externalReferenceCode, groupId, classNameId, true);
	}

	/**
	 * Returns the ddm structure where externalReferenceCode = &#63; and groupId = &#63; and classNameId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByERC_G_C(
		String externalReferenceCode, long groupId, long classNameId,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMStructure.class)) {

			externalReferenceCode = Objects.toString(externalReferenceCode, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {
					externalReferenceCode, groupId, classNameId
				};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByERC_G_C, finderArgs, this);
			}

			if (result instanceof DDMStructure) {
				DDMStructure ddmStructure = (DDMStructure)result;

				if (!Objects.equals(
						externalReferenceCode,
						ddmStructure.getExternalReferenceCode()) ||
					(groupId != ddmStructure.getGroupId()) ||
					(classNameId != ddmStructure.getClassNameId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_SELECT_DDMSTRUCTURE_WHERE);

				boolean bindExternalReferenceCode = false;

				if (externalReferenceCode.isEmpty()) {
					sb.append(_FINDER_COLUMN_ERC_G_C_EXTERNALREFERENCECODE_3);
				}
				else {
					bindExternalReferenceCode = true;

					sb.append(_FINDER_COLUMN_ERC_G_C_EXTERNALREFERENCECODE_2);
				}

				sb.append(_FINDER_COLUMN_ERC_G_C_GROUPID_2);

				sb.append(_FINDER_COLUMN_ERC_G_C_CLASSNAMEID_2);

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

					queryPos.add(classNameId);

					List<DDMStructure> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByERC_G_C, finderArgs, list);
						}
					}
					else {
						DDMStructure ddmStructure = list.get(0);

						result = ddmStructure;

						cacheResult(ddmStructure);
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
				return (DDMStructure)result;
			}
		}
	}

	/**
	 * Removes the ddm structure where externalReferenceCode = &#63; and groupId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the ddm structure that was removed
	 */
	@Override
	public DDMStructure removeByERC_G_C(
			String externalReferenceCode, long groupId, long classNameId)
		throws NoSuchStructureException {

		DDMStructure ddmStructure = findByERC_G_C(
			externalReferenceCode, groupId, classNameId);

		return remove(ddmStructure);
	}

	/**
	 * Returns the number of ddm structures where externalReferenceCode = &#63; and groupId = &#63; and classNameId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the number of matching ddm structures
	 */
	@Override
	public int countByERC_G_C(
		String externalReferenceCode, long groupId, long classNameId) {

		DDMStructure ddmStructure = fetchByERC_G_C(
			externalReferenceCode, groupId, classNameId);

		if (ddmStructure == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ERC_G_C_EXTERNALREFERENCECODE_2 =
		"ddmStructure.externalReferenceCode = ? AND ";

	private static final String _FINDER_COLUMN_ERC_G_C_EXTERNALREFERENCECODE_3 =
		"(ddmStructure.externalReferenceCode IS NULL OR ddmStructure.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_G_C_GROUPID_2 =
		"ddmStructure.groupId = ? AND ";

	private static final String _FINDER_COLUMN_ERC_G_C_CLASSNAMEID_2 =
		"ddmStructure.classNameId = ?";

	private FinderPath _finderPathFetchByG_C_S;

	/**
	 * Returns the ddm structure where groupId = &#63; and classNameId = &#63; and structureKey = &#63; or throws a <code>NoSuchStructureException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param structureKey the structure key
	 * @return the matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure findByG_C_S(
			long groupId, long classNameId, String structureKey)
		throws NoSuchStructureException {

		DDMStructure ddmStructure = fetchByG_C_S(
			groupId, classNameId, structureKey);

		if (ddmStructure == null) {
			StringBundler sb = new StringBundler(8);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("groupId=");
			sb.append(groupId);

			sb.append(", classNameId=");
			sb.append(classNameId);

			sb.append(", structureKey=");
			sb.append(structureKey);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchStructureException(sb.toString());
		}

		return ddmStructure;
	}

	/**
	 * Returns the ddm structure where groupId = &#63; and classNameId = &#63; and structureKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param structureKey the structure key
	 * @return the matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByG_C_S(
		long groupId, long classNameId, String structureKey) {

		return fetchByG_C_S(groupId, classNameId, structureKey, true);
	}

	/**
	 * Returns the ddm structure where groupId = &#63; and classNameId = &#63; and structureKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param structureKey the structure key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByG_C_S(
		long groupId, long classNameId, String structureKey,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMStructure.class)) {

			structureKey = Objects.toString(structureKey, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {groupId, classNameId, structureKey};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByG_C_S, finderArgs, this);
			}

			if (result instanceof DDMStructure) {
				DDMStructure ddmStructure = (DDMStructure)result;

				if ((groupId != ddmStructure.getGroupId()) ||
					(classNameId != ddmStructure.getClassNameId()) ||
					!Objects.equals(
						structureKey, ddmStructure.getStructureKey())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_SELECT_DDMSTRUCTURE_WHERE);

				sb.append(_FINDER_COLUMN_G_C_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_S_CLASSNAMEID_2);

				boolean bindStructureKey = false;

				if (structureKey.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_C_S_STRUCTUREKEY_3);
				}
				else {
					bindStructureKey = true;

					sb.append(_FINDER_COLUMN_G_C_S_STRUCTUREKEY_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(classNameId);

					if (bindStructureKey) {
						queryPos.add(structureKey);
					}

					List<DDMStructure> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByG_C_S, finderArgs, list);
						}
					}
					else {
						DDMStructure ddmStructure = list.get(0);

						result = ddmStructure;

						cacheResult(ddmStructure);
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
				return (DDMStructure)result;
			}
		}
	}

	/**
	 * Removes the ddm structure where groupId = &#63; and classNameId = &#63; and structureKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param structureKey the structure key
	 * @return the ddm structure that was removed
	 */
	@Override
	public DDMStructure removeByG_C_S(
			long groupId, long classNameId, String structureKey)
		throws NoSuchStructureException {

		DDMStructure ddmStructure = findByG_C_S(
			groupId, classNameId, structureKey);

		return remove(ddmStructure);
	}

	/**
	 * Returns the number of ddm structures where groupId = &#63; and classNameId = &#63; and structureKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param structureKey the structure key
	 * @return the number of matching ddm structures
	 */
	@Override
	public int countByG_C_S(
		long groupId, long classNameId, String structureKey) {

		DDMStructure ddmStructure = fetchByG_C_S(
			groupId, classNameId, structureKey);

		if (ddmStructure == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_G_C_S_GROUPID_2 =
		"ddmStructure.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_S_CLASSNAMEID_2 =
		"ddmStructure.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_S_STRUCTUREKEY_2 =
		"ddmStructure.structureKey = ?";

	private static final String _FINDER_COLUMN_G_C_S_STRUCTUREKEY_3 =
		"(ddmStructure.structureKey IS NULL OR ddmStructure.structureKey = '')";

	private FinderPath _finderPathWithPaginationFindByG_N_D;
	private FinderPath _finderPathWithoutPaginationFindByG_N_D;
	private FinderPath _finderPathCountByG_N_D;

	/**
	 * Returns all the ddm structures where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @return the matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByG_N_D(
		long groupId, String name, String description) {

		return findByG_N_D(
			groupId, name, description, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the ddm structures where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByG_N_D(
		long groupId, String name, String description, int start, int end) {

		return findByG_N_D(groupId, name, description, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByG_N_D(
		long groupId, String name, String description, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		return findByG_N_D(
			groupId, name, description, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByG_N_D(
		long groupId, String name, String description, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMStructure.class)) {

			name = Objects.toString(name, "");
			description = Objects.toString(description, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_N_D;
					finderArgs = new Object[] {groupId, name, description};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_N_D;
				finderArgs = new Object[] {
					groupId, name, description, start, end, orderByComparator
				};
			}

			List<DDMStructure> list = null;

			if (useFinderCache) {
				list = (List<DDMStructure>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DDMStructure ddmStructure : list) {
						if ((groupId != ddmStructure.getGroupId()) ||
							!name.equals(ddmStructure.getName()) ||
							!description.equals(
								ddmStructure.getDescription())) {

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

				sb.append(_SQL_SELECT_DDMSTRUCTURE_WHERE);

				sb.append(_FINDER_COLUMN_G_N_D_GROUPID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_N_D_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_G_N_D_NAME_2);
				}

				boolean bindDescription = false;

				if (description.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_N_D_DESCRIPTION_3);
				}
				else {
					bindDescription = true;

					sb.append(_FINDER_COLUMN_G_N_D_DESCRIPTION_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DDMStructureModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					if (bindName) {
						queryPos.add(name);
					}

					if (bindDescription) {
						queryPos.add(description);
					}

					list = (List<DDMStructure>)QueryUtil.list(
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
	 * Returns the first ddm structure in the ordered set where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure findByG_N_D_First(
			long groupId, String name, String description,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		DDMStructure ddmStructure = fetchByG_N_D_First(
			groupId, name, description, orderByComparator);

		if (ddmStructure != null) {
			return ddmStructure;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", name=");
		sb.append(name);

		sb.append(", description=");
		sb.append(description);

		sb.append("}");

		throw new NoSuchStructureException(sb.toString());
	}

	/**
	 * Returns the first ddm structure in the ordered set where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByG_N_D_First(
		long groupId, String name, String description,
		OrderByComparator<DDMStructure> orderByComparator) {

		List<DDMStructure> list = findByG_N_D(
			groupId, name, description, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last ddm structure in the ordered set where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure findByG_N_D_Last(
			long groupId, String name, String description,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		DDMStructure ddmStructure = fetchByG_N_D_Last(
			groupId, name, description, orderByComparator);

		if (ddmStructure != null) {
			return ddmStructure;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", name=");
		sb.append(name);

		sb.append(", description=");
		sb.append(description);

		sb.append("}");

		throw new NoSuchStructureException(sb.toString());
	}

	/**
	 * Returns the last ddm structure in the ordered set where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByG_N_D_Last(
		long groupId, String name, String description,
		OrderByComparator<DDMStructure> orderByComparator) {

		int count = countByG_N_D(groupId, name, description);

		if (count == 0) {
			return null;
		}

		List<DDMStructure> list = findByG_N_D(
			groupId, name, description, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	@Override
	public DDMStructure[] findByG_N_D_PrevAndNext(
			long structureId, long groupId, String name, String description,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		name = Objects.toString(name, "");
		description = Objects.toString(description, "");

		DDMStructure ddmStructure = findByPrimaryKey(structureId);

		Session session = null;

		try {
			session = openSession();

			DDMStructure[] array = new DDMStructureImpl[3];

			array[0] = getByG_N_D_PrevAndNext(
				session, ddmStructure, groupId, name, description,
				orderByComparator, true);

			array[1] = ddmStructure;

			array[2] = getByG_N_D_PrevAndNext(
				session, ddmStructure, groupId, name, description,
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

	protected DDMStructure getByG_N_D_PrevAndNext(
		Session session, DDMStructure ddmStructure, long groupId, String name,
		String description, OrderByComparator<DDMStructure> orderByComparator,
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

		sb.append(_SQL_SELECT_DDMSTRUCTURE_WHERE);

		sb.append(_FINDER_COLUMN_G_N_D_GROUPID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_N_D_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_G_N_D_NAME_2);
		}

		boolean bindDescription = false;

		if (description.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_N_D_DESCRIPTION_3);
		}
		else {
			bindDescription = true;

			sb.append(_FINDER_COLUMN_G_N_D_DESCRIPTION_2);
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
			sb.append(DDMStructureModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		if (bindName) {
			queryPos.add(name);
		}

		if (bindDescription) {
			queryPos.add(description);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(ddmStructure)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMStructure> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the ddm structures that the user has permission to view where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @return the matching ddm structures that the user has permission to view
	 */
	@Override
	public List<DDMStructure> filterFindByG_N_D(
		long groupId, String name, String description) {

		return filterFindByG_N_D(
			groupId, name, description, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the ddm structures that the user has permission to view where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures that the user has permission to view
	 */
	@Override
	public List<DDMStructure> filterFindByG_N_D(
		long groupId, String name, String description, int start, int end) {

		return filterFindByG_N_D(groupId, name, description, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm structures that the user has permissions to view where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures that the user has permission to view
	 */
	@Override
	public List<DDMStructure> filterFindByG_N_D(
		long groupId, String name, String description, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_N_D(
				groupId, name, description, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_N_D(
					groupId, name, description, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		name = Objects.toString(name, "");
		description = Objects.toString(description, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_DDMSTRUCTURE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DDMSTRUCTURE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_N_D_GROUPID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_N_D_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_G_N_D_NAME_2);
		}

		boolean bindDescription = false;

		if (description.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_N_D_DESCRIPTION_3);
		}
		else {
			bindDescription = true;

			sb.append(_FINDER_COLUMN_G_N_D_DESCRIPTION_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DDMSTRUCTURE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DDMStructureModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DDMStructureModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMStructure.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, DDMStructureImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, DDMStructureImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			if (bindName) {
				queryPos.add(name);
			}

			if (bindDescription) {
				queryPos.add(description);
			}

			return (List<DDMStructure>)QueryUtil.list(
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
	 * Returns the ddm structures before and after the current ddm structure in the ordered set of ddm structures that the user has permission to view where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	@Override
	public DDMStructure[] filterFindByG_N_D_PrevAndNext(
			long structureId, long groupId, String name, String description,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_N_D_PrevAndNext(
				structureId, groupId, name, description, orderByComparator);
		}

		name = Objects.toString(name, "");
		description = Objects.toString(description, "");

		DDMStructure ddmStructure = findByPrimaryKey(structureId);

		Session session = null;

		try {
			session = openSession();

			DDMStructure[] array = new DDMStructureImpl[3];

			array[0] = filterGetByG_N_D_PrevAndNext(
				session, ddmStructure, groupId, name, description,
				orderByComparator, true);

			array[1] = ddmStructure;

			array[2] = filterGetByG_N_D_PrevAndNext(
				session, ddmStructure, groupId, name, description,
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

	protected DDMStructure filterGetByG_N_D_PrevAndNext(
		Session session, DDMStructure ddmStructure, long groupId, String name,
		String description, OrderByComparator<DDMStructure> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_DDMSTRUCTURE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DDMSTRUCTURE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_N_D_GROUPID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_N_D_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_G_N_D_NAME_2);
		}

		boolean bindDescription = false;

		if (description.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_N_D_DESCRIPTION_3);
		}
		else {
			bindDescription = true;

			sb.append(_FINDER_COLUMN_G_N_D_DESCRIPTION_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DDMSTRUCTURE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DDMStructureModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DDMStructureModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMStructure.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DDMStructureImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DDMStructureImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (bindName) {
			queryPos.add(name);
		}

		if (bindDescription) {
			queryPos.add(description);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(ddmStructure)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMStructure> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the ddm structures where groupId = &#63; and name = &#63; and description = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 */
	@Override
	public void removeByG_N_D(long groupId, String name, String description) {
		for (DDMStructure ddmStructure :
				findByG_N_D(
					groupId, name, description, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(ddmStructure);
		}
	}

	/**
	 * Returns the number of ddm structures where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @return the number of matching ddm structures
	 */
	@Override
	public int countByG_N_D(long groupId, String name, String description) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMStructure.class)) {

			name = Objects.toString(name, "");
			description = Objects.toString(description, "");

			FinderPath finderPath = _finderPathCountByG_N_D;

			Object[] finderArgs = new Object[] {groupId, name, description};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_DDMSTRUCTURE_WHERE);

				sb.append(_FINDER_COLUMN_G_N_D_GROUPID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_N_D_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_G_N_D_NAME_2);
				}

				boolean bindDescription = false;

				if (description.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_N_D_DESCRIPTION_3);
				}
				else {
					bindDescription = true;

					sb.append(_FINDER_COLUMN_G_N_D_DESCRIPTION_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					if (bindName) {
						queryPos.add(name);
					}

					if (bindDescription) {
						queryPos.add(description);
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

	/**
	 * Returns the number of ddm structures that the user has permission to view where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @return the number of matching ddm structures that the user has permission to view
	 */
	@Override
	public int filterCountByG_N_D(
		long groupId, String name, String description) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_N_D(groupId, name, description);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DDMStructure> ddmStructures = findByG_N_D(
				groupId, name, description);

			ddmStructures = InlineSQLHelperUtil.filter(ddmStructures, groupId);

			return ddmStructures.size();
		}

		name = Objects.toString(name, "");
		description = Objects.toString(description, "");

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_DDMSTRUCTURE_WHERE);

		sb.append(_FINDER_COLUMN_G_N_D_GROUPID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_N_D_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_G_N_D_NAME_2);
		}

		boolean bindDescription = false;

		if (description.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_N_D_DESCRIPTION_3);
		}
		else {
			bindDescription = true;

			sb.append(_FINDER_COLUMN_G_N_D_DESCRIPTION_2);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMStructure.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			if (bindName) {
				queryPos.add(name);
			}

			if (bindDescription) {
				queryPos.add(description);
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

	private static final String _FINDER_COLUMN_G_N_D_GROUPID_2 =
		"ddmStructure.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_N_D_NAME_2 =
		"ddmStructure.name = ? AND ";

	private static final String _FINDER_COLUMN_G_N_D_NAME_3 =
		"(ddmStructure.name IS NULL OR ddmStructure.name = '') AND ";

	private static final String _FINDER_COLUMN_G_N_D_DESCRIPTION_2 =
		"CAST_CLOB_TEXT(ddmStructure.description) = ?";

	private static final String _FINDER_COLUMN_G_N_D_DESCRIPTION_3 =
		"(ddmStructure.description IS NULL OR CAST_CLOB_TEXT(ddmStructure.description) = '')";

	private FinderPath _finderPathWithPaginationFindByG_C_N_D;
	private FinderPath _finderPathWithoutPaginationFindByG_C_N_D;
	private FinderPath _finderPathCountByG_C_N_D;
	private FinderPath _finderPathWithPaginationCountByG_C_N_D;

	/**
	 * Returns all the ddm structures where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @return the matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByG_C_N_D(
		long groupId, long classNameId, String name, String description) {

		return findByG_C_N_D(
			groupId, classNameId, name, description, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm structures where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByG_C_N_D(
		long groupId, long classNameId, String name, String description,
		int start, int end) {

		return findByG_C_N_D(
			groupId, classNameId, name, description, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByG_C_N_D(
		long groupId, long classNameId, String name, String description,
		int start, int end, OrderByComparator<DDMStructure> orderByComparator) {

		return findByG_C_N_D(
			groupId, classNameId, name, description, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByG_C_N_D(
		long groupId, long classNameId, String name, String description,
		int start, int end, OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMStructure.class)) {

			name = Objects.toString(name, "");
			description = Objects.toString(description, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_C_N_D;
					finderArgs = new Object[] {
						groupId, classNameId, name, description
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_C_N_D;
				finderArgs = new Object[] {
					groupId, classNameId, name, description, start, end,
					orderByComparator
				};
			}

			List<DDMStructure> list = null;

			if (useFinderCache) {
				list = (List<DDMStructure>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DDMStructure ddmStructure : list) {
						if ((groupId != ddmStructure.getGroupId()) ||
							(classNameId != ddmStructure.getClassNameId()) ||
							!name.equals(ddmStructure.getName()) ||
							!description.equals(
								ddmStructure.getDescription())) {

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

				sb.append(_SQL_SELECT_DDMSTRUCTURE_WHERE);

				sb.append(_FINDER_COLUMN_G_C_N_D_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_N_D_CLASSNAMEID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_C_N_D_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_G_C_N_D_NAME_2);
				}

				boolean bindDescription = false;

				if (description.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_C_N_D_DESCRIPTION_3);
				}
				else {
					bindDescription = true;

					sb.append(_FINDER_COLUMN_G_C_N_D_DESCRIPTION_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DDMStructureModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(classNameId);

					if (bindName) {
						queryPos.add(name);
					}

					if (bindDescription) {
						queryPos.add(description);
					}

					list = (List<DDMStructure>)QueryUtil.list(
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
	 * Returns the first ddm structure in the ordered set where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure findByG_C_N_D_First(
			long groupId, long classNameId, String name, String description,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		DDMStructure ddmStructure = fetchByG_C_N_D_First(
			groupId, classNameId, name, description, orderByComparator);

		if (ddmStructure != null) {
			return ddmStructure;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", name=");
		sb.append(name);

		sb.append(", description=");
		sb.append(description);

		sb.append("}");

		throw new NoSuchStructureException(sb.toString());
	}

	/**
	 * Returns the first ddm structure in the ordered set where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByG_C_N_D_First(
		long groupId, long classNameId, String name, String description,
		OrderByComparator<DDMStructure> orderByComparator) {

		List<DDMStructure> list = findByG_C_N_D(
			groupId, classNameId, name, description, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last ddm structure in the ordered set where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure findByG_C_N_D_Last(
			long groupId, long classNameId, String name, String description,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		DDMStructure ddmStructure = fetchByG_C_N_D_Last(
			groupId, classNameId, name, description, orderByComparator);

		if (ddmStructure != null) {
			return ddmStructure;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", name=");
		sb.append(name);

		sb.append(", description=");
		sb.append(description);

		sb.append("}");

		throw new NoSuchStructureException(sb.toString());
	}

	/**
	 * Returns the last ddm structure in the ordered set where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchByG_C_N_D_Last(
		long groupId, long classNameId, String name, String description,
		OrderByComparator<DDMStructure> orderByComparator) {

		int count = countByG_C_N_D(groupId, classNameId, name, description);

		if (count == 0) {
			return null;
		}

		List<DDMStructure> list = findByG_C_N_D(
			groupId, classNameId, name, description, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	@Override
	public DDMStructure[] findByG_C_N_D_PrevAndNext(
			long structureId, long groupId, long classNameId, String name,
			String description,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		name = Objects.toString(name, "");
		description = Objects.toString(description, "");

		DDMStructure ddmStructure = findByPrimaryKey(structureId);

		Session session = null;

		try {
			session = openSession();

			DDMStructure[] array = new DDMStructureImpl[3];

			array[0] = getByG_C_N_D_PrevAndNext(
				session, ddmStructure, groupId, classNameId, name, description,
				orderByComparator, true);

			array[1] = ddmStructure;

			array[2] = getByG_C_N_D_PrevAndNext(
				session, ddmStructure, groupId, classNameId, name, description,
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

	protected DDMStructure getByG_C_N_D_PrevAndNext(
		Session session, DDMStructure ddmStructure, long groupId,
		long classNameId, String name, String description,
		OrderByComparator<DDMStructure> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		sb.append(_SQL_SELECT_DDMSTRUCTURE_WHERE);

		sb.append(_FINDER_COLUMN_G_C_N_D_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_N_D_CLASSNAMEID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_C_N_D_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_G_C_N_D_NAME_2);
		}

		boolean bindDescription = false;

		if (description.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_C_N_D_DESCRIPTION_3);
		}
		else {
			bindDescription = true;

			sb.append(_FINDER_COLUMN_G_C_N_D_DESCRIPTION_2);
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
			sb.append(DDMStructureModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(classNameId);

		if (bindName) {
			queryPos.add(name);
		}

		if (bindDescription) {
			queryPos.add(description);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(ddmStructure)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMStructure> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the ddm structures that the user has permission to view where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @return the matching ddm structures that the user has permission to view
	 */
	@Override
	public List<DDMStructure> filterFindByG_C_N_D(
		long groupId, long classNameId, String name, String description) {

		return filterFindByG_C_N_D(
			groupId, classNameId, name, description, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm structures that the user has permission to view where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures that the user has permission to view
	 */
	@Override
	public List<DDMStructure> filterFindByG_C_N_D(
		long groupId, long classNameId, String name, String description,
		int start, int end) {

		return filterFindByG_C_N_D(
			groupId, classNameId, name, description, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm structures that the user has permissions to view where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures that the user has permission to view
	 */
	@Override
	public List<DDMStructure> filterFindByG_C_N_D(
		long groupId, long classNameId, String name, String description,
		int start, int end, OrderByComparator<DDMStructure> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_C_N_D(
				groupId, classNameId, name, description, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_C_N_D(
					groupId, classNameId, name, description, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		name = Objects.toString(name, "");
		description = Objects.toString(description, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(7);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_DDMSTRUCTURE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DDMSTRUCTURE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_C_N_D_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_N_D_CLASSNAMEID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_C_N_D_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_G_C_N_D_NAME_2);
		}

		boolean bindDescription = false;

		if (description.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_C_N_D_DESCRIPTION_3);
		}
		else {
			bindDescription = true;

			sb.append(_FINDER_COLUMN_G_C_N_D_DESCRIPTION_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DDMSTRUCTURE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DDMStructureModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DDMStructureModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMStructure.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, DDMStructureImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, DDMStructureImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(classNameId);

			if (bindName) {
				queryPos.add(name);
			}

			if (bindDescription) {
				queryPos.add(description);
			}

			return (List<DDMStructure>)QueryUtil.list(
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
	 * Returns the ddm structures before and after the current ddm structure in the ordered set of ddm structures that the user has permission to view where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	@Override
	public DDMStructure[] filterFindByG_C_N_D_PrevAndNext(
			long structureId, long groupId, long classNameId, String name,
			String description,
			OrderByComparator<DDMStructure> orderByComparator)
		throws NoSuchStructureException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_C_N_D_PrevAndNext(
				structureId, groupId, classNameId, name, description,
				orderByComparator);
		}

		name = Objects.toString(name, "");
		description = Objects.toString(description, "");

		DDMStructure ddmStructure = findByPrimaryKey(structureId);

		Session session = null;

		try {
			session = openSession();

			DDMStructure[] array = new DDMStructureImpl[3];

			array[0] = filterGetByG_C_N_D_PrevAndNext(
				session, ddmStructure, groupId, classNameId, name, description,
				orderByComparator, true);

			array[1] = ddmStructure;

			array[2] = filterGetByG_C_N_D_PrevAndNext(
				session, ddmStructure, groupId, classNameId, name, description,
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

	protected DDMStructure filterGetByG_C_N_D_PrevAndNext(
		Session session, DDMStructure ddmStructure, long groupId,
		long classNameId, String name, String description,
		OrderByComparator<DDMStructure> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_DDMSTRUCTURE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DDMSTRUCTURE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_C_N_D_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_N_D_CLASSNAMEID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_C_N_D_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_G_C_N_D_NAME_2);
		}

		boolean bindDescription = false;

		if (description.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_C_N_D_DESCRIPTION_3);
		}
		else {
			bindDescription = true;

			sb.append(_FINDER_COLUMN_G_C_N_D_DESCRIPTION_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DDMSTRUCTURE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DDMStructureModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DDMStructureModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMStructure.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DDMStructureImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DDMStructureImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(classNameId);

		if (bindName) {
			queryPos.add(name);
		}

		if (bindDescription) {
			queryPos.add(description);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(ddmStructure)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMStructure> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the ddm structures that the user has permission to view where groupId = any &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @return the matching ddm structures that the user has permission to view
	 */
	@Override
	public List<DDMStructure> filterFindByG_C_N_D(
		long[] groupIds, long classNameId, String name, String description) {

		return filterFindByG_C_N_D(
			groupIds, classNameId, name, description, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm structures that the user has permission to view where groupId = any &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures that the user has permission to view
	 */
	@Override
	public List<DDMStructure> filterFindByG_C_N_D(
		long[] groupIds, long classNameId, String name, String description,
		int start, int end) {

		return filterFindByG_C_N_D(
			groupIds, classNameId, name, description, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm structures that the user has permission to view where groupId = any &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures that the user has permission to view
	 */
	@Override
	public List<DDMStructure> filterFindByG_C_N_D(
		long[] groupIds, long classNameId, String name, String description,
		int start, int end, OrderByComparator<DDMStructure> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupIds)) {
			return findByG_C_N_D(
				groupIds, classNameId, name, description, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_C_N_D(
					groupIds, classNameId, name, description, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupIds);
		}

		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		name = Objects.toString(name, "");
		description = Objects.toString(description, "");

		StringBundler sb = new StringBundler();

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_DDMSTRUCTURE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DDMSTRUCTURE_NO_INLINE_DISTINCT_WHERE_1);
		}

		if (groupIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_C_N_D_GROUPID_7);

			sb.append(StringUtil.merge(groupIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_G_C_N_D_CLASSNAMEID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_C_N_D_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_G_C_N_D_NAME_2);
		}

		boolean bindDescription = false;

		if (description.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_C_N_D_DESCRIPTION_3);
		}
		else {
			bindDescription = true;

			sb.append(_FINDER_COLUMN_G_C_N_D_DESCRIPTION_2);
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DDMSTRUCTURE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DDMStructureModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DDMStructureModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMStructure.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupIds);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, DDMStructureImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, DDMStructureImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(classNameId);

			if (bindName) {
				queryPos.add(name);
			}

			if (bindDescription) {
				queryPos.add(description);
			}

			return (List<DDMStructure>)QueryUtil.list(
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
	 * Returns all the ddm structures where groupId = any &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @return the matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByG_C_N_D(
		long[] groupIds, long classNameId, String name, String description) {

		return findByG_C_N_D(
			groupIds, classNameId, name, description, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm structures where groupId = any &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByG_C_N_D(
		long[] groupIds, long classNameId, String name, String description,
		int start, int end) {

		return findByG_C_N_D(
			groupIds, classNameId, name, description, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm structures where groupId = any &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByG_C_N_D(
		long[] groupIds, long classNameId, String name, String description,
		int start, int end, OrderByComparator<DDMStructure> orderByComparator) {

		return findByG_C_N_D(
			groupIds, classNameId, name, description, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	@Override
	public List<DDMStructure> findByG_C_N_D(
		long[] groupIds, long classNameId, String name, String description,
		int start, int end, OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		name = Objects.toString(name, "");
		description = Objects.toString(description, "");

		if (groupIds.length == 1) {
			return findByG_C_N_D(
				groupIds[0], classNameId, name, description, start, end,
				orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMStructure.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						StringUtil.merge(groupIds), classNameId, name,
						description
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					StringUtil.merge(groupIds), classNameId, name, description,
					start, end, orderByComparator
				};
			}

			List<DDMStructure> list = null;

			if (useFinderCache) {
				list = (List<DDMStructure>)finderCache.getResult(
					_finderPathWithPaginationFindByG_C_N_D, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DDMStructure ddmStructure : list) {
						if (!ArrayUtil.contains(
								groupIds, ddmStructure.getGroupId()) ||
							(classNameId != ddmStructure.getClassNameId()) ||
							!name.equals(ddmStructure.getName()) ||
							!description.equals(
								ddmStructure.getDescription())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_SELECT_DDMSTRUCTURE_WHERE);

				if (groupIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_C_N_D_GROUPID_7);

					sb.append(StringUtil.merge(groupIds));

					sb.append(")");

					sb.append(")");

					sb.append(WHERE_AND);
				}

				sb.append(_FINDER_COLUMN_G_C_N_D_CLASSNAMEID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_C_N_D_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_G_C_N_D_NAME_2);
				}

				boolean bindDescription = false;

				if (description.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_C_N_D_DESCRIPTION_3);
				}
				else {
					bindDescription = true;

					sb.append(_FINDER_COLUMN_G_C_N_D_DESCRIPTION_2);
				}

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DDMStructureModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(classNameId);

					if (bindName) {
						queryPos.add(name);
					}

					if (bindDescription) {
						queryPos.add(description);
					}

					list = (List<DDMStructure>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(
							_finderPathWithPaginationFindByG_C_N_D, finderArgs,
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
	 * Removes all the ddm structures where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 */
	@Override
	public void removeByG_C_N_D(
		long groupId, long classNameId, String name, String description) {

		for (DDMStructure ddmStructure :
				findByG_C_N_D(
					groupId, classNameId, name, description, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(ddmStructure);
		}
	}

	/**
	 * Returns the number of ddm structures where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @return the number of matching ddm structures
	 */
	@Override
	public int countByG_C_N_D(
		long groupId, long classNameId, String name, String description) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMStructure.class)) {

			name = Objects.toString(name, "");
			description = Objects.toString(description, "");

			FinderPath finderPath = _finderPathCountByG_C_N_D;

			Object[] finderArgs = new Object[] {
				groupId, classNameId, name, description
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_DDMSTRUCTURE_WHERE);

				sb.append(_FINDER_COLUMN_G_C_N_D_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_N_D_CLASSNAMEID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_C_N_D_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_G_C_N_D_NAME_2);
				}

				boolean bindDescription = false;

				if (description.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_C_N_D_DESCRIPTION_3);
				}
				else {
					bindDescription = true;

					sb.append(_FINDER_COLUMN_G_C_N_D_DESCRIPTION_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(classNameId);

					if (bindName) {
						queryPos.add(name);
					}

					if (bindDescription) {
						queryPos.add(description);
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

	/**
	 * Returns the number of ddm structures where groupId = any &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @return the number of matching ddm structures
	 */
	@Override
	public int countByG_C_N_D(
		long[] groupIds, long classNameId, String name, String description) {

		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		name = Objects.toString(name, "");
		description = Objects.toString(description, "");

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMStructure.class)) {

			Object[] finderArgs = new Object[] {
				StringUtil.merge(groupIds), classNameId, name, description
			};

			Long count = (Long)finderCache.getResult(
				_finderPathWithPaginationCountByG_C_N_D, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_DDMSTRUCTURE_WHERE);

				if (groupIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_C_N_D_GROUPID_7);

					sb.append(StringUtil.merge(groupIds));

					sb.append(")");

					sb.append(")");

					sb.append(WHERE_AND);
				}

				sb.append(_FINDER_COLUMN_G_C_N_D_CLASSNAMEID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_C_N_D_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_G_C_N_D_NAME_2);
				}

				boolean bindDescription = false;

				if (description.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_C_N_D_DESCRIPTION_3);
				}
				else {
					bindDescription = true;

					sb.append(_FINDER_COLUMN_G_C_N_D_DESCRIPTION_2);
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

					queryPos.add(classNameId);

					if (bindName) {
						queryPos.add(name);
					}

					if (bindDescription) {
						queryPos.add(description);
					}

					count = (Long)query.uniqueResult();

					finderCache.putResult(
						_finderPathWithPaginationCountByG_C_N_D, finderArgs,
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

	/**
	 * Returns the number of ddm structures that the user has permission to view where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @return the number of matching ddm structures that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_N_D(
		long groupId, long classNameId, String name, String description) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_C_N_D(groupId, classNameId, name, description);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DDMStructure> ddmStructures = findByG_C_N_D(
				groupId, classNameId, name, description);

			ddmStructures = InlineSQLHelperUtil.filter(ddmStructures, groupId);

			return ddmStructures.size();
		}

		name = Objects.toString(name, "");
		description = Objects.toString(description, "");

		StringBundler sb = new StringBundler(5);

		sb.append(_FILTER_SQL_COUNT_DDMSTRUCTURE_WHERE);

		sb.append(_FINDER_COLUMN_G_C_N_D_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_N_D_CLASSNAMEID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_C_N_D_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_G_C_N_D_NAME_2);
		}

		boolean bindDescription = false;

		if (description.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_C_N_D_DESCRIPTION_3);
		}
		else {
			bindDescription = true;

			sb.append(_FINDER_COLUMN_G_C_N_D_DESCRIPTION_2);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMStructure.class.getName(),
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

			if (bindName) {
				queryPos.add(name);
			}

			if (bindDescription) {
				queryPos.add(description);
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

	/**
	 * Returns the number of ddm structures that the user has permission to view where groupId = any &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @return the number of matching ddm structures that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_N_D(
		long[] groupIds, long classNameId, String name, String description) {

		if (!InlineSQLHelperUtil.isEnabled(groupIds)) {
			return countByG_C_N_D(groupIds, classNameId, name, description);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DDMStructure> ddmStructures = InlineSQLHelperUtil.filter(
				findByG_C_N_D(groupIds, classNameId, name, description),
				groupIds);

			return ddmStructures.size();
		}

		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		name = Objects.toString(name, "");
		description = Objects.toString(description, "");

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_DDMSTRUCTURE_WHERE);

		if (groupIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_C_N_D_GROUPID_7);

			sb.append(StringUtil.merge(groupIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_G_C_N_D_CLASSNAMEID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_C_N_D_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_G_C_N_D_NAME_2);
		}

		boolean bindDescription = false;

		if (description.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_C_N_D_DESCRIPTION_3);
		}
		else {
			bindDescription = true;

			sb.append(_FINDER_COLUMN_G_C_N_D_DESCRIPTION_2);
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMStructure.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupIds);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(classNameId);

			if (bindName) {
				queryPos.add(name);
			}

			if (bindDescription) {
				queryPos.add(description);
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

	private static final String _FINDER_COLUMN_G_C_N_D_GROUPID_2 =
		"ddmStructure.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_N_D_GROUPID_7 =
		"ddmStructure.groupId IN (";

	private static final String _FINDER_COLUMN_G_C_N_D_CLASSNAMEID_2 =
		"ddmStructure.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_N_D_NAME_2 =
		"ddmStructure.name = ? AND ";

	private static final String _FINDER_COLUMN_G_C_N_D_NAME_3 =
		"(ddmStructure.name IS NULL OR ddmStructure.name = '') AND ";

	private static final String _FINDER_COLUMN_G_C_N_D_DESCRIPTION_2 =
		"CAST_CLOB_TEXT(ddmStructure.description) = ?";

	private static final String _FINDER_COLUMN_G_C_N_D_DESCRIPTION_3 =
		"(ddmStructure.description IS NULL OR CAST_CLOB_TEXT(ddmStructure.description) = '')";

	public DDMStructurePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(DDMStructure.class);

		setModelImplClass(DDMStructureImpl.class);
		setModelPKClass(long.class);

		setTable(DDMStructureTable.INSTANCE);
	}

	/**
	 * Caches the ddm structure in the entity cache if it is enabled.
	 *
	 * @param ddmStructure the ddm structure
	 */
	@Override
	public void cacheResult(DDMStructure ddmStructure) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ddmStructure.getCtCollectionId())) {

			entityCache.putResult(
				DDMStructureImpl.class, ddmStructure.getPrimaryKey(),
				ddmStructure);

			finderCache.putResult(
				_finderPathFetchByUUID_G,
				new Object[] {
					ddmStructure.getUuid(), ddmStructure.getGroupId()
				},
				ddmStructure);

			finderCache.putResult(
				_finderPathFetchByERC_G_C,
				new Object[] {
					ddmStructure.getExternalReferenceCode(),
					ddmStructure.getGroupId(), ddmStructure.getClassNameId()
				},
				ddmStructure);

			finderCache.putResult(
				_finderPathFetchByG_C_S,
				new Object[] {
					ddmStructure.getGroupId(), ddmStructure.getClassNameId(),
					ddmStructure.getStructureKey()
				},
				ddmStructure);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the ddm structures in the entity cache if it is enabled.
	 *
	 * @param ddmStructures the ddm structures
	 */
	@Override
	public void cacheResult(List<DDMStructure> ddmStructures) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (ddmStructures.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (DDMStructure ddmStructure : ddmStructures) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						ddmStructure.getCtCollectionId())) {

				DDMStructure cachedDDMStructure =
					(DDMStructure)entityCache.getResult(
						DDMStructureImpl.class, ddmStructure.getPrimaryKey());

				if (cachedDDMStructure == null) {
					cacheResult(ddmStructure);
				}
				else {
					DDMStructureModelImpl ddmStructureModelImpl =
						(DDMStructureModelImpl)ddmStructure;
					DDMStructureModelImpl cachedDDMStructureModelImpl =
						(DDMStructureModelImpl)cachedDDMStructure;

					ddmStructureModelImpl.setClassName(
						cachedDDMStructureModelImpl.getClassName());

					ddmStructureModelImpl.setDDMForm(
						cachedDDMStructureModelImpl.getDDMForm());

					ddmStructureModelImpl.setDDMFormFieldsMap(
						cachedDDMStructureModelImpl.getDDMFormFieldsMap());
				}
			}
		}
	}

	/**
	 * Clears the cache for all ddm structures.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(DDMStructureImpl.class);

		finderCache.clearCache(DDMStructureImpl.class);
	}

	/**
	 * Clears the cache for the ddm structure.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(DDMStructure ddmStructure) {
		entityCache.removeResult(DDMStructureImpl.class, ddmStructure);
	}

	@Override
	public void clearCache(List<DDMStructure> ddmStructures) {
		for (DDMStructure ddmStructure : ddmStructures) {
			entityCache.removeResult(DDMStructureImpl.class, ddmStructure);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(DDMStructureImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(DDMStructureImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		DDMStructureModelImpl ddmStructureModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ddmStructureModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				ddmStructureModelImpl.getUuid(),
				ddmStructureModelImpl.getGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByUUID_G, args, ddmStructureModelImpl);

			args = new Object[] {
				ddmStructureModelImpl.getExternalReferenceCode(),
				ddmStructureModelImpl.getGroupId(),
				ddmStructureModelImpl.getClassNameId()
			};

			finderCache.putResult(
				_finderPathFetchByERC_G_C, args, ddmStructureModelImpl);

			args = new Object[] {
				ddmStructureModelImpl.getGroupId(),
				ddmStructureModelImpl.getClassNameId(),
				ddmStructureModelImpl.getStructureKey()
			};

			finderCache.putResult(
				_finderPathFetchByG_C_S, args, ddmStructureModelImpl);
		}
	}

	/**
	 * Creates a new ddm structure with the primary key. Does not add the ddm structure to the database.
	 *
	 * @param structureId the primary key for the new ddm structure
	 * @return the new ddm structure
	 */
	@Override
	public DDMStructure create(long structureId) {
		DDMStructure ddmStructure = new DDMStructureImpl();

		ddmStructure.setNew(true);
		ddmStructure.setPrimaryKey(structureId);

		String uuid = PortalUUIDUtil.generate();

		ddmStructure.setUuid(uuid);

		ddmStructure.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ddmStructure;
	}

	/**
	 * Removes the ddm structure with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param structureId the primary key of the ddm structure
	 * @return the ddm structure that was removed
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	@Override
	public DDMStructure remove(long structureId)
		throws NoSuchStructureException {

		return remove((Serializable)structureId);
	}

	/**
	 * Removes the ddm structure with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the ddm structure
	 * @return the ddm structure that was removed
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	@Override
	public DDMStructure remove(Serializable primaryKey)
		throws NoSuchStructureException {

		Session session = null;

		try {
			session = openSession();

			DDMStructure ddmStructure = (DDMStructure)session.get(
				DDMStructureImpl.class, primaryKey);

			if (ddmStructure == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchStructureException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(ddmStructure);
		}
		catch (NoSuchStructureException noSuchEntityException) {
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
	protected DDMStructure removeImpl(DDMStructure ddmStructure) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ddmStructure)) {
				ddmStructure = (DDMStructure)session.get(
					DDMStructureImpl.class, ddmStructure.getPrimaryKeyObj());
			}

			if ((ddmStructure != null) &&
				ctPersistenceHelper.isRemove(ddmStructure)) {

				session.delete(ddmStructure);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ddmStructure != null) {
			clearCache(ddmStructure);
		}

		return ddmStructure;
	}

	@Override
	public DDMStructure updateImpl(DDMStructure ddmStructure) {
		boolean isNew = ddmStructure.isNew();

		if (!(ddmStructure instanceof DDMStructureModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ddmStructure.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					ddmStructure);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ddmStructure proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DDMStructure implementation " +
					ddmStructure.getClass());
		}

		DDMStructureModelImpl ddmStructureModelImpl =
			(DDMStructureModelImpl)ddmStructure;

		if (Validator.isNull(ddmStructure.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			ddmStructure.setUuid(uuid);
		}

		if (Validator.isNull(ddmStructure.getExternalReferenceCode())) {
			ddmStructure.setExternalReferenceCode(ddmStructure.getUuid());
		}
		else {
			if (!Objects.equals(
					ddmStructureModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					ddmStructure.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = ddmStructure.getCompanyId();

					long groupId = ddmStructure.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = ddmStructure.getPrimaryKey();
					}

					try {
						ddmStructure.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								DDMStructure.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								ddmStructure.getExternalReferenceCode(), null));
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

		if (isNew && (ddmStructure.getCreateDate() == null)) {
			if (serviceContext == null) {
				ddmStructure.setCreateDate(date);
			}
			else {
				ddmStructure.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!ddmStructureModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				ddmStructure.setModifiedDate(date);
			}
			else {
				ddmStructure.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(ddmStructure)) {
				if (!isNew) {
					session.evict(
						DDMStructureImpl.class,
						ddmStructure.getPrimaryKeyObj());
				}

				session.save(ddmStructure);
			}
			else {
				ddmStructure = (DDMStructure)session.merge(ddmStructure);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			DDMStructureImpl.class, ddmStructureModelImpl, false, true);

		cacheUniqueFindersCache(ddmStructureModelImpl);

		if (isNew) {
			ddmStructure.setNew(false);
		}

		ddmStructure.resetOriginalValues();

		return ddmStructure;
	}

	/**
	 * Returns the ddm structure with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the ddm structure
	 * @return the ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	@Override
	public DDMStructure findByPrimaryKey(Serializable primaryKey)
		throws NoSuchStructureException {

		DDMStructure ddmStructure = fetchByPrimaryKey(primaryKey);

		if (ddmStructure == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchStructureException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return ddmStructure;
	}

	/**
	 * Returns the ddm structure with the primary key or throws a <code>NoSuchStructureException</code> if it could not be found.
	 *
	 * @param structureId the primary key of the ddm structure
	 * @return the ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	@Override
	public DDMStructure findByPrimaryKey(long structureId)
		throws NoSuchStructureException {

		return findByPrimaryKey((Serializable)structureId);
	}

	/**
	 * Returns the ddm structure with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the ddm structure
	 * @return the ddm structure, or <code>null</code> if a ddm structure with the primary key could not be found
	 */
	@Override
	public DDMStructure fetchByPrimaryKey(Serializable primaryKey) {
		if (ctPersistenceHelper.isProductionMode(
				DDMStructure.class, primaryKey)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		DDMStructure ddmStructure = (DDMStructure)entityCache.getResult(
			DDMStructureImpl.class, primaryKey);

		if (ddmStructure != null) {
			return ddmStructure;
		}

		Session session = null;

		try {
			session = openSession();

			ddmStructure = (DDMStructure)session.get(
				DDMStructureImpl.class, primaryKey);

			if (ddmStructure != null) {
				cacheResult(ddmStructure);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return ddmStructure;
	}

	/**
	 * Returns the ddm structure with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param structureId the primary key of the ddm structure
	 * @return the ddm structure, or <code>null</code> if a ddm structure with the primary key could not be found
	 */
	@Override
	public DDMStructure fetchByPrimaryKey(long structureId) {
		return fetchByPrimaryKey((Serializable)structureId);
	}

	@Override
	public Map<Serializable, DDMStructure> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (ctPersistenceHelper.isProductionMode(DDMStructure.class)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, DDMStructure> map =
			new HashMap<Serializable, DDMStructure>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			DDMStructure ddmStructure = fetchByPrimaryKey(primaryKey);

			if (ddmStructure != null) {
				map.put(primaryKey, ddmStructure);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
						DDMStructure.class, primaryKey)) {

				DDMStructure ddmStructure = (DDMStructure)entityCache.getResult(
					DDMStructureImpl.class, primaryKey);

				if (ddmStructure == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, ddmStructure);
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

			for (DDMStructure ddmStructure : (List<DDMStructure>)query.list()) {
				map.put(ddmStructure.getPrimaryKeyObj(), ddmStructure);

				cacheResult(ddmStructure);
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
	 * Returns all the ddm structures.
	 *
	 * @return the ddm structures
	 */
	@Override
	public List<DDMStructure> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm structures.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of ddm structures
	 */
	@Override
	public List<DDMStructure> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm structures.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of ddm structures
	 */
	@Override
	public List<DDMStructure> findAll(
		int start, int end, OrderByComparator<DDMStructure> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddm structures.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of ddm structures
	 */
	@Override
	public List<DDMStructure> findAll(
		int start, int end, OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMStructure.class)) {

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

			List<DDMStructure> list = null;

			if (useFinderCache) {
				list = (List<DDMStructure>)finderCache.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_DDMSTRUCTURE);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_DDMSTRUCTURE;

					sql = sql.concat(DDMStructureModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<DDMStructure>)QueryUtil.list(
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
	 * Removes all the ddm structures from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (DDMStructure ddmStructure : findAll()) {
			remove(ddmStructure);
		}
	}

	/**
	 * Returns the number of ddm structures.
	 *
	 * @return the number of ddm structures
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMStructure.class)) {

			Long count = (Long)finderCache.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(_SQL_COUNT_DDMSTRUCTURE);

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
		return "structureId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DDMSTRUCTURE;
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
		return DDMStructureModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DDMStructure";
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
		ctMergeColumnNames.add("externalReferenceCode");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctMergeColumnNames.add("versionUserId");
		ctMergeColumnNames.add("versionUserName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("parentStructureId");
		ctStrictColumnNames.add("classNameId");
		ctMergeColumnNames.add("structureKey");
		ctMergeColumnNames.add("version");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("definition");
		ctMergeColumnNames.add("storageType");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("structureId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId", "classNameId"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "classNameId", "structureKey"});
	}

	/**
	 * Initializes the ddm structure persistence.
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

		_finderPathWithPaginationCountByGroupId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByGroupId",
			new String[] {Long.class.getName()}, new String[] {"groupId"},
			false);

		_finderPathWithPaginationFindByParentStructureId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByParentStructureId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"parentStructureId"}, true);

		_finderPathWithoutPaginationFindByParentStructureId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"findByParentStructureId", new String[] {Long.class.getName()},
			new String[] {"parentStructureId"}, true);

		_finderPathCountByParentStructureId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByParentStructureId", new String[] {Long.class.getName()},
			new String[] {"parentStructureId"}, false);

		_finderPathWithPaginationFindByStructureKey = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByStructureKey",
			new String[] {
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"structureKey"}, true);

		_finderPathWithoutPaginationFindByStructureKey = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByStructureKey",
			new String[] {String.class.getName()},
			new String[] {"structureKey"}, true);

		_finderPathCountByStructureKey = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByStructureKey",
			new String[] {String.class.getName()},
			new String[] {"structureKey"}, false);

		_finderPathWithPaginationFindByG_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "parentStructureId"}, true);

		_finderPathWithoutPaginationFindByG_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "parentStructureId"}, true);

		_finderPathCountByG_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "parentStructureId"}, false);

		_finderPathWithPaginationFindByG_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "classNameId"}, true);

		_finderPathWithoutPaginationFindByG_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "classNameId"}, true);

		_finderPathCountByG_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "classNameId"}, false);

		_finderPathWithPaginationCountByG_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_C",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "classNameId"}, false);

		_finderPathWithPaginationFindByC_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "classNameId"}, true);

		_finderPathWithoutPaginationFindByC_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"companyId", "classNameId"}, true);

		_finderPathCountByC_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"companyId", "classNameId"}, false);

		_finderPathFetchByERC_G_C = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_G_C",
			new String[] {
				String.class.getName(), Long.class.getName(),
				Long.class.getName()
			},
			new String[] {"externalReferenceCode", "groupId", "classNameId"},
			true);

		_finderPathFetchByG_C_S = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByG_C_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName()
			},
			new String[] {"groupId", "classNameId", "structureKey"}, true);

		_finderPathWithPaginationFindByG_N_D = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_N_D",
			new String[] {
				Long.class.getName(), String.class.getName(),
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "name", "description"}, true);

		_finderPathWithoutPaginationFindByG_N_D = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_N_D",
			new String[] {
				Long.class.getName(), String.class.getName(),
				String.class.getName()
			},
			new String[] {"groupId", "name", "description"}, true);

		_finderPathCountByG_N_D = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_N_D",
			new String[] {
				Long.class.getName(), String.class.getName(),
				String.class.getName()
			},
			new String[] {"groupId", "name", "description"}, false);

		_finderPathWithPaginationFindByG_C_N_D = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_N_D",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "classNameId", "name", "description"},
			true);

		_finderPathWithoutPaginationFindByG_C_N_D = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_N_D",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName(), String.class.getName()
			},
			new String[] {"groupId", "classNameId", "name", "description"},
			true);

		_finderPathCountByG_C_N_D = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_N_D",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName(), String.class.getName()
			},
			new String[] {"groupId", "classNameId", "name", "description"},
			false);

		_finderPathWithPaginationCountByG_C_N_D = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_C_N_D",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName(), String.class.getName()
			},
			new String[] {"groupId", "classNameId", "name", "description"},
			false);

		DDMStructureUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		DDMStructureUtil.setPersistence(null);

		entityCache.removeCache(DDMStructureImpl.class.getName());
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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

	private static final String _SQL_SELECT_DDMSTRUCTURE =
		"SELECT ddmStructure FROM DDMStructure ddmStructure";

	private static final String _SQL_SELECT_DDMSTRUCTURE_WHERE =
		"SELECT ddmStructure FROM DDMStructure ddmStructure WHERE ";

	private static final String _SQL_COUNT_DDMSTRUCTURE =
		"SELECT COUNT(ddmStructure) FROM DDMStructure ddmStructure";

	private static final String _SQL_COUNT_DDMSTRUCTURE_WHERE =
		"SELECT COUNT(ddmStructure) FROM DDMStructure ddmStructure WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"ddmStructure.structureId";

	private static final String _FILTER_SQL_SELECT_DDMSTRUCTURE_WHERE =
		"SELECT DISTINCT {ddmStructure.*} FROM DDMStructure ddmStructure WHERE ";

	private static final String
		_FILTER_SQL_SELECT_DDMSTRUCTURE_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {DDMStructure.*} FROM (SELECT DISTINCT ddmStructure.structureId FROM DDMStructure ddmStructure WHERE ";

	private static final String
		_FILTER_SQL_SELECT_DDMSTRUCTURE_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN DDMStructure ON TEMP_TABLE.structureId = DDMStructure.structureId";

	private static final String _FILTER_SQL_COUNT_DDMSTRUCTURE_WHERE =
		"SELECT COUNT(DISTINCT ddmStructure.structureId) AS COUNT_VALUE FROM DDMStructure ddmStructure WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "ddmStructure";

	private static final String _FILTER_ENTITY_TABLE = "DDMStructure";

	private static final String _ORDER_BY_ENTITY_ALIAS = "ddmStructure.";

	private static final String _ORDER_BY_ENTITY_TABLE = "DDMStructure.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No DDMStructure exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No DDMStructure exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		DDMStructurePersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}