/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.service.persistence.impl;

import com.liferay.depot.exception.NoSuchEntryGroupRelException;
import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.depot.model.DepotEntryGroupRelTable;
import com.liferay.depot.model.impl.DepotEntryGroupRelImpl;
import com.liferay.depot.model.impl.DepotEntryGroupRelModelImpl;
import com.liferay.depot.service.persistence.DepotEntryGroupRelPersistence;
import com.liferay.depot.service.persistence.DepotEntryGroupRelUtil;
import com.liferay.depot.service.persistence.impl.constants.DepotPersistenceConstants;
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
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
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
 * The persistence implementation for the depot entry group rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = DepotEntryGroupRelPersistence.class)
public class DepotEntryGroupRelPersistenceImpl
	extends BasePersistenceImpl<DepotEntryGroupRel>
	implements DepotEntryGroupRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DepotEntryGroupRelUtil</code> to access the depot entry group rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DepotEntryGroupRelImpl.class.getName();

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
	 * Returns all the depot entry group rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching depot entry group rels
	 */
	@Override
	public List<DepotEntryGroupRel> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the depot entry group rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @return the range of matching depot entry group rels
	 */
	@Override
	public List<DepotEntryGroupRel> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the depot entry group rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching depot entry group rels
	 */
	@Override
	public List<DepotEntryGroupRel> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the depot entry group rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching depot entry group rels
	 */
	@Override
	public List<DepotEntryGroupRel> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DepotEntryGroupRel> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DepotEntryGroupRel.class)) {

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

			List<DepotEntryGroupRel> list = null;

			if (useFinderCache) {
				list = (List<DepotEntryGroupRel>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DepotEntryGroupRel depotEntryGroupRel : list) {
						if (!uuid.equals(depotEntryGroupRel.getUuid())) {
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

				sb.append(_SQL_SELECT_DEPOTENTRYGROUPREL_WHERE);

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
					sb.append(DepotEntryGroupRelModelImpl.ORDER_BY_JPQL);
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

					list = (List<DepotEntryGroupRel>)QueryUtil.list(
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
	 * Returns the first depot entry group rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	@Override
	public DepotEntryGroupRel findByUuid_First(
			String uuid,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws NoSuchEntryGroupRelException {

		DepotEntryGroupRel depotEntryGroupRel = fetchByUuid_First(
			uuid, orderByComparator);

		if (depotEntryGroupRel != null) {
			return depotEntryGroupRel;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchEntryGroupRelException(sb.toString());
	}

	/**
	 * Returns the first depot entry group rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	@Override
	public DepotEntryGroupRel fetchByUuid_First(
		String uuid, OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		List<DepotEntryGroupRel> list = findByUuid(
			uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last depot entry group rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	@Override
	public DepotEntryGroupRel findByUuid_Last(
			String uuid,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws NoSuchEntryGroupRelException {

		DepotEntryGroupRel depotEntryGroupRel = fetchByUuid_Last(
			uuid, orderByComparator);

		if (depotEntryGroupRel != null) {
			return depotEntryGroupRel;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchEntryGroupRelException(sb.toString());
	}

	/**
	 * Returns the last depot entry group rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	@Override
	public DepotEntryGroupRel fetchByUuid_Last(
		String uuid, OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<DepotEntryGroupRel> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the depot entry group rels before and after the current depot entry group rel in the ordered set where uuid = &#63;.
	 *
	 * @param depotEntryGroupRelId the primary key of the current depot entry group rel
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a depot entry group rel with the primary key could not be found
	 */
	@Override
	public DepotEntryGroupRel[] findByUuid_PrevAndNext(
			long depotEntryGroupRelId, String uuid,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws NoSuchEntryGroupRelException {

		uuid = Objects.toString(uuid, "");

		DepotEntryGroupRel depotEntryGroupRel = findByPrimaryKey(
			depotEntryGroupRelId);

		Session session = null;

		try {
			session = openSession();

			DepotEntryGroupRel[] array = new DepotEntryGroupRelImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, depotEntryGroupRel, uuid, orderByComparator, true);

			array[1] = depotEntryGroupRel;

			array[2] = getByUuid_PrevAndNext(
				session, depotEntryGroupRel, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DepotEntryGroupRel getByUuid_PrevAndNext(
		Session session, DepotEntryGroupRel depotEntryGroupRel, String uuid,
		OrderByComparator<DepotEntryGroupRel> orderByComparator,
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

		sb.append(_SQL_SELECT_DEPOTENTRYGROUPREL_WHERE);

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
			sb.append(DepotEntryGroupRelModelImpl.ORDER_BY_JPQL);
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
						depotEntryGroupRel)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DepotEntryGroupRel> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the depot entry group rels where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (DepotEntryGroupRel depotEntryGroupRel :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(depotEntryGroupRel);
		}
	}

	/**
	 * Returns the number of depot entry group rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching depot entry group rels
	 */
	@Override
	public int countByUuid(String uuid) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DepotEntryGroupRel.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid;

			Object[] finderArgs = new Object[] {uuid};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_DEPOTENTRYGROUPREL_WHERE);

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
		"depotEntryGroupRel.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(depotEntryGroupRel.uuid IS NULL OR depotEntryGroupRel.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;

	/**
	 * Returns the depot entry group rel where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchEntryGroupRelException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	@Override
	public DepotEntryGroupRel findByUUID_G(String uuid, long groupId)
		throws NoSuchEntryGroupRelException {

		DepotEntryGroupRel depotEntryGroupRel = fetchByUUID_G(uuid, groupId);

		if (depotEntryGroupRel == null) {
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

			throw new NoSuchEntryGroupRelException(sb.toString());
		}

		return depotEntryGroupRel;
	}

	/**
	 * Returns the depot entry group rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	@Override
	public DepotEntryGroupRel fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the depot entry group rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	@Override
	public DepotEntryGroupRel fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DepotEntryGroupRel.class)) {

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

			if (result instanceof DepotEntryGroupRel) {
				DepotEntryGroupRel depotEntryGroupRel =
					(DepotEntryGroupRel)result;

				if (!Objects.equals(uuid, depotEntryGroupRel.getUuid()) ||
					(groupId != depotEntryGroupRel.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_DEPOTENTRYGROUPREL_WHERE);

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

					List<DepotEntryGroupRel> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByUUID_G, finderArgs, list);
						}
					}
					else {
						DepotEntryGroupRel depotEntryGroupRel = list.get(0);

						result = depotEntryGroupRel;

						cacheResult(depotEntryGroupRel);
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
				return (DepotEntryGroupRel)result;
			}
		}
	}

	/**
	 * Removes the depot entry group rel where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the depot entry group rel that was removed
	 */
	@Override
	public DepotEntryGroupRel removeByUUID_G(String uuid, long groupId)
		throws NoSuchEntryGroupRelException {

		DepotEntryGroupRel depotEntryGroupRel = findByUUID_G(uuid, groupId);

		return remove(depotEntryGroupRel);
	}

	/**
	 * Returns the number of depot entry group rels where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching depot entry group rels
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		DepotEntryGroupRel depotEntryGroupRel = fetchByUUID_G(uuid, groupId);

		if (depotEntryGroupRel == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"depotEntryGroupRel.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(depotEntryGroupRel.uuid IS NULL OR depotEntryGroupRel.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"depotEntryGroupRel.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the depot entry group rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching depot entry group rels
	 */
	@Override
	public List<DepotEntryGroupRel> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the depot entry group rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @return the range of matching depot entry group rels
	 */
	@Override
	public List<DepotEntryGroupRel> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the depot entry group rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching depot entry group rels
	 */
	@Override
	public List<DepotEntryGroupRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the depot entry group rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching depot entry group rels
	 */
	@Override
	public List<DepotEntryGroupRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DepotEntryGroupRel> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DepotEntryGroupRel.class)) {

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

			List<DepotEntryGroupRel> list = null;

			if (useFinderCache) {
				list = (List<DepotEntryGroupRel>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DepotEntryGroupRel depotEntryGroupRel : list) {
						if (!uuid.equals(depotEntryGroupRel.getUuid()) ||
							(companyId != depotEntryGroupRel.getCompanyId())) {

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

				sb.append(_SQL_SELECT_DEPOTENTRYGROUPREL_WHERE);

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
					sb.append(DepotEntryGroupRelModelImpl.ORDER_BY_JPQL);
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

					list = (List<DepotEntryGroupRel>)QueryUtil.list(
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
	 * Returns the first depot entry group rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	@Override
	public DepotEntryGroupRel findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws NoSuchEntryGroupRelException {

		DepotEntryGroupRel depotEntryGroupRel = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (depotEntryGroupRel != null) {
			return depotEntryGroupRel;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchEntryGroupRelException(sb.toString());
	}

	/**
	 * Returns the first depot entry group rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	@Override
	public DepotEntryGroupRel fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		List<DepotEntryGroupRel> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last depot entry group rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	@Override
	public DepotEntryGroupRel findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws NoSuchEntryGroupRelException {

		DepotEntryGroupRel depotEntryGroupRel = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (depotEntryGroupRel != null) {
			return depotEntryGroupRel;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchEntryGroupRelException(sb.toString());
	}

	/**
	 * Returns the last depot entry group rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	@Override
	public DepotEntryGroupRel fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<DepotEntryGroupRel> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the depot entry group rels before and after the current depot entry group rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param depotEntryGroupRelId the primary key of the current depot entry group rel
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a depot entry group rel with the primary key could not be found
	 */
	@Override
	public DepotEntryGroupRel[] findByUuid_C_PrevAndNext(
			long depotEntryGroupRelId, String uuid, long companyId,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws NoSuchEntryGroupRelException {

		uuid = Objects.toString(uuid, "");

		DepotEntryGroupRel depotEntryGroupRel = findByPrimaryKey(
			depotEntryGroupRelId);

		Session session = null;

		try {
			session = openSession();

			DepotEntryGroupRel[] array = new DepotEntryGroupRelImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, depotEntryGroupRel, uuid, companyId, orderByComparator,
				true);

			array[1] = depotEntryGroupRel;

			array[2] = getByUuid_C_PrevAndNext(
				session, depotEntryGroupRel, uuid, companyId, orderByComparator,
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

	protected DepotEntryGroupRel getByUuid_C_PrevAndNext(
		Session session, DepotEntryGroupRel depotEntryGroupRel, String uuid,
		long companyId, OrderByComparator<DepotEntryGroupRel> orderByComparator,
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

		sb.append(_SQL_SELECT_DEPOTENTRYGROUPREL_WHERE);

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
			sb.append(DepotEntryGroupRelModelImpl.ORDER_BY_JPQL);
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
						depotEntryGroupRel)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DepotEntryGroupRel> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the depot entry group rels where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (DepotEntryGroupRel depotEntryGroupRel :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(depotEntryGroupRel);
		}
	}

	/**
	 * Returns the number of depot entry group rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching depot entry group rels
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DepotEntryGroupRel.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid_C;

			Object[] finderArgs = new Object[] {uuid, companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_DEPOTENTRYGROUPREL_WHERE);

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
		"depotEntryGroupRel.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(depotEntryGroupRel.uuid IS NULL OR depotEntryGroupRel.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"depotEntryGroupRel.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByDepotEntryId;
	private FinderPath _finderPathWithoutPaginationFindByDepotEntryId;
	private FinderPath _finderPathCountByDepotEntryId;

	/**
	 * Returns all the depot entry group rels where depotEntryId = &#63;.
	 *
	 * @param depotEntryId the depot entry ID
	 * @return the matching depot entry group rels
	 */
	@Override
	public List<DepotEntryGroupRel> findByDepotEntryId(long depotEntryId) {
		return findByDepotEntryId(
			depotEntryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the depot entry group rels where depotEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param depotEntryId the depot entry ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @return the range of matching depot entry group rels
	 */
	@Override
	public List<DepotEntryGroupRel> findByDepotEntryId(
		long depotEntryId, int start, int end) {

		return findByDepotEntryId(depotEntryId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the depot entry group rels where depotEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param depotEntryId the depot entry ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching depot entry group rels
	 */
	@Override
	public List<DepotEntryGroupRel> findByDepotEntryId(
		long depotEntryId, int start, int end,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		return findByDepotEntryId(
			depotEntryId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the depot entry group rels where depotEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param depotEntryId the depot entry ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching depot entry group rels
	 */
	@Override
	public List<DepotEntryGroupRel> findByDepotEntryId(
		long depotEntryId, int start, int end,
		OrderByComparator<DepotEntryGroupRel> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DepotEntryGroupRel.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByDepotEntryId;
					finderArgs = new Object[] {depotEntryId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByDepotEntryId;
				finderArgs = new Object[] {
					depotEntryId, start, end, orderByComparator
				};
			}

			List<DepotEntryGroupRel> list = null;

			if (useFinderCache) {
				list = (List<DepotEntryGroupRel>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DepotEntryGroupRel depotEntryGroupRel : list) {
						if (depotEntryId !=
								depotEntryGroupRel.getDepotEntryId()) {

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

				sb.append(_SQL_SELECT_DEPOTENTRYGROUPREL_WHERE);

				sb.append(_FINDER_COLUMN_DEPOTENTRYID_DEPOTENTRYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DepotEntryGroupRelModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(depotEntryId);

					list = (List<DepotEntryGroupRel>)QueryUtil.list(
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
	 * Returns the first depot entry group rel in the ordered set where depotEntryId = &#63;.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	@Override
	public DepotEntryGroupRel findByDepotEntryId_First(
			long depotEntryId,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws NoSuchEntryGroupRelException {

		DepotEntryGroupRel depotEntryGroupRel = fetchByDepotEntryId_First(
			depotEntryId, orderByComparator);

		if (depotEntryGroupRel != null) {
			return depotEntryGroupRel;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("depotEntryId=");
		sb.append(depotEntryId);

		sb.append("}");

		throw new NoSuchEntryGroupRelException(sb.toString());
	}

	/**
	 * Returns the first depot entry group rel in the ordered set where depotEntryId = &#63;.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	@Override
	public DepotEntryGroupRel fetchByDepotEntryId_First(
		long depotEntryId,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		List<DepotEntryGroupRel> list = findByDepotEntryId(
			depotEntryId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last depot entry group rel in the ordered set where depotEntryId = &#63;.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	@Override
	public DepotEntryGroupRel findByDepotEntryId_Last(
			long depotEntryId,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws NoSuchEntryGroupRelException {

		DepotEntryGroupRel depotEntryGroupRel = fetchByDepotEntryId_Last(
			depotEntryId, orderByComparator);

		if (depotEntryGroupRel != null) {
			return depotEntryGroupRel;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("depotEntryId=");
		sb.append(depotEntryId);

		sb.append("}");

		throw new NoSuchEntryGroupRelException(sb.toString());
	}

	/**
	 * Returns the last depot entry group rel in the ordered set where depotEntryId = &#63;.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	@Override
	public DepotEntryGroupRel fetchByDepotEntryId_Last(
		long depotEntryId,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		int count = countByDepotEntryId(depotEntryId);

		if (count == 0) {
			return null;
		}

		List<DepotEntryGroupRel> list = findByDepotEntryId(
			depotEntryId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the depot entry group rels before and after the current depot entry group rel in the ordered set where depotEntryId = &#63;.
	 *
	 * @param depotEntryGroupRelId the primary key of the current depot entry group rel
	 * @param depotEntryId the depot entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a depot entry group rel with the primary key could not be found
	 */
	@Override
	public DepotEntryGroupRel[] findByDepotEntryId_PrevAndNext(
			long depotEntryGroupRelId, long depotEntryId,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws NoSuchEntryGroupRelException {

		DepotEntryGroupRel depotEntryGroupRel = findByPrimaryKey(
			depotEntryGroupRelId);

		Session session = null;

		try {
			session = openSession();

			DepotEntryGroupRel[] array = new DepotEntryGroupRelImpl[3];

			array[0] = getByDepotEntryId_PrevAndNext(
				session, depotEntryGroupRel, depotEntryId, orderByComparator,
				true);

			array[1] = depotEntryGroupRel;

			array[2] = getByDepotEntryId_PrevAndNext(
				session, depotEntryGroupRel, depotEntryId, orderByComparator,
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

	protected DepotEntryGroupRel getByDepotEntryId_PrevAndNext(
		Session session, DepotEntryGroupRel depotEntryGroupRel,
		long depotEntryId,
		OrderByComparator<DepotEntryGroupRel> orderByComparator,
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

		sb.append(_SQL_SELECT_DEPOTENTRYGROUPREL_WHERE);

		sb.append(_FINDER_COLUMN_DEPOTENTRYID_DEPOTENTRYID_2);

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
			sb.append(DepotEntryGroupRelModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(depotEntryId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						depotEntryGroupRel)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DepotEntryGroupRel> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the depot entry group rels where depotEntryId = &#63; from the database.
	 *
	 * @param depotEntryId the depot entry ID
	 */
	@Override
	public void removeByDepotEntryId(long depotEntryId) {
		for (DepotEntryGroupRel depotEntryGroupRel :
				findByDepotEntryId(
					depotEntryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(depotEntryGroupRel);
		}
	}

	/**
	 * Returns the number of depot entry group rels where depotEntryId = &#63;.
	 *
	 * @param depotEntryId the depot entry ID
	 * @return the number of matching depot entry group rels
	 */
	@Override
	public int countByDepotEntryId(long depotEntryId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DepotEntryGroupRel.class)) {

			FinderPath finderPath = _finderPathCountByDepotEntryId;

			Object[] finderArgs = new Object[] {depotEntryId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_DEPOTENTRYGROUPREL_WHERE);

				sb.append(_FINDER_COLUMN_DEPOTENTRYID_DEPOTENTRYID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(depotEntryId);

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

	private static final String _FINDER_COLUMN_DEPOTENTRYID_DEPOTENTRYID_2 =
		"depotEntryGroupRel.depotEntryId = ?";

	private FinderPath _finderPathWithPaginationFindByToGroupId;
	private FinderPath _finderPathWithoutPaginationFindByToGroupId;
	private FinderPath _finderPathCountByToGroupId;

	/**
	 * Returns all the depot entry group rels where toGroupId = &#63;.
	 *
	 * @param toGroupId the to group ID
	 * @return the matching depot entry group rels
	 */
	@Override
	public List<DepotEntryGroupRel> findByToGroupId(long toGroupId) {
		return findByToGroupId(
			toGroupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the depot entry group rels where toGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param toGroupId the to group ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @return the range of matching depot entry group rels
	 */
	@Override
	public List<DepotEntryGroupRel> findByToGroupId(
		long toGroupId, int start, int end) {

		return findByToGroupId(toGroupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the depot entry group rels where toGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param toGroupId the to group ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching depot entry group rels
	 */
	@Override
	public List<DepotEntryGroupRel> findByToGroupId(
		long toGroupId, int start, int end,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		return findByToGroupId(toGroupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the depot entry group rels where toGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param toGroupId the to group ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching depot entry group rels
	 */
	@Override
	public List<DepotEntryGroupRel> findByToGroupId(
		long toGroupId, int start, int end,
		OrderByComparator<DepotEntryGroupRel> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DepotEntryGroupRel.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByToGroupId;
					finderArgs = new Object[] {toGroupId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByToGroupId;
				finderArgs = new Object[] {
					toGroupId, start, end, orderByComparator
				};
			}

			List<DepotEntryGroupRel> list = null;

			if (useFinderCache) {
				list = (List<DepotEntryGroupRel>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DepotEntryGroupRel depotEntryGroupRel : list) {
						if (toGroupId != depotEntryGroupRel.getToGroupId()) {
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

				sb.append(_SQL_SELECT_DEPOTENTRYGROUPREL_WHERE);

				sb.append(_FINDER_COLUMN_TOGROUPID_TOGROUPID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DepotEntryGroupRelModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(toGroupId);

					list = (List<DepotEntryGroupRel>)QueryUtil.list(
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
	 * Returns the first depot entry group rel in the ordered set where toGroupId = &#63;.
	 *
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	@Override
	public DepotEntryGroupRel findByToGroupId_First(
			long toGroupId,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws NoSuchEntryGroupRelException {

		DepotEntryGroupRel depotEntryGroupRel = fetchByToGroupId_First(
			toGroupId, orderByComparator);

		if (depotEntryGroupRel != null) {
			return depotEntryGroupRel;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("toGroupId=");
		sb.append(toGroupId);

		sb.append("}");

		throw new NoSuchEntryGroupRelException(sb.toString());
	}

	/**
	 * Returns the first depot entry group rel in the ordered set where toGroupId = &#63;.
	 *
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	@Override
	public DepotEntryGroupRel fetchByToGroupId_First(
		long toGroupId,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		List<DepotEntryGroupRel> list = findByToGroupId(
			toGroupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last depot entry group rel in the ordered set where toGroupId = &#63;.
	 *
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	@Override
	public DepotEntryGroupRel findByToGroupId_Last(
			long toGroupId,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws NoSuchEntryGroupRelException {

		DepotEntryGroupRel depotEntryGroupRel = fetchByToGroupId_Last(
			toGroupId, orderByComparator);

		if (depotEntryGroupRel != null) {
			return depotEntryGroupRel;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("toGroupId=");
		sb.append(toGroupId);

		sb.append("}");

		throw new NoSuchEntryGroupRelException(sb.toString());
	}

	/**
	 * Returns the last depot entry group rel in the ordered set where toGroupId = &#63;.
	 *
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	@Override
	public DepotEntryGroupRel fetchByToGroupId_Last(
		long toGroupId,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		int count = countByToGroupId(toGroupId);

		if (count == 0) {
			return null;
		}

		List<DepotEntryGroupRel> list = findByToGroupId(
			toGroupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the depot entry group rels before and after the current depot entry group rel in the ordered set where toGroupId = &#63;.
	 *
	 * @param depotEntryGroupRelId the primary key of the current depot entry group rel
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a depot entry group rel with the primary key could not be found
	 */
	@Override
	public DepotEntryGroupRel[] findByToGroupId_PrevAndNext(
			long depotEntryGroupRelId, long toGroupId,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws NoSuchEntryGroupRelException {

		DepotEntryGroupRel depotEntryGroupRel = findByPrimaryKey(
			depotEntryGroupRelId);

		Session session = null;

		try {
			session = openSession();

			DepotEntryGroupRel[] array = new DepotEntryGroupRelImpl[3];

			array[0] = getByToGroupId_PrevAndNext(
				session, depotEntryGroupRel, toGroupId, orderByComparator,
				true);

			array[1] = depotEntryGroupRel;

			array[2] = getByToGroupId_PrevAndNext(
				session, depotEntryGroupRel, toGroupId, orderByComparator,
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

	protected DepotEntryGroupRel getByToGroupId_PrevAndNext(
		Session session, DepotEntryGroupRel depotEntryGroupRel, long toGroupId,
		OrderByComparator<DepotEntryGroupRel> orderByComparator,
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

		sb.append(_SQL_SELECT_DEPOTENTRYGROUPREL_WHERE);

		sb.append(_FINDER_COLUMN_TOGROUPID_TOGROUPID_2);

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
			sb.append(DepotEntryGroupRelModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(toGroupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						depotEntryGroupRel)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DepotEntryGroupRel> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the depot entry group rels where toGroupId = &#63; from the database.
	 *
	 * @param toGroupId the to group ID
	 */
	@Override
	public void removeByToGroupId(long toGroupId) {
		for (DepotEntryGroupRel depotEntryGroupRel :
				findByToGroupId(
					toGroupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(depotEntryGroupRel);
		}
	}

	/**
	 * Returns the number of depot entry group rels where toGroupId = &#63;.
	 *
	 * @param toGroupId the to group ID
	 * @return the number of matching depot entry group rels
	 */
	@Override
	public int countByToGroupId(long toGroupId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DepotEntryGroupRel.class)) {

			FinderPath finderPath = _finderPathCountByToGroupId;

			Object[] finderArgs = new Object[] {toGroupId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_DEPOTENTRYGROUPREL_WHERE);

				sb.append(_FINDER_COLUMN_TOGROUPID_TOGROUPID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(toGroupId);

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

	private static final String _FINDER_COLUMN_TOGROUPID_TOGROUPID_2 =
		"depotEntryGroupRel.toGroupId = ?";

	private FinderPath _finderPathWithPaginationFindByDDMSA_TGI;
	private FinderPath _finderPathWithoutPaginationFindByDDMSA_TGI;
	private FinderPath _finderPathCountByDDMSA_TGI;

	/**
	 * Returns all the depot entry group rels where ddmStructuresAvailable = &#63; and toGroupId = &#63;.
	 *
	 * @param ddmStructuresAvailable the ddm structures available
	 * @param toGroupId the to group ID
	 * @return the matching depot entry group rels
	 */
	@Override
	public List<DepotEntryGroupRel> findByDDMSA_TGI(
		boolean ddmStructuresAvailable, long toGroupId) {

		return findByDDMSA_TGI(
			ddmStructuresAvailable, toGroupId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the depot entry group rels where ddmStructuresAvailable = &#63; and toGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param ddmStructuresAvailable the ddm structures available
	 * @param toGroupId the to group ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @return the range of matching depot entry group rels
	 */
	@Override
	public List<DepotEntryGroupRel> findByDDMSA_TGI(
		boolean ddmStructuresAvailable, long toGroupId, int start, int end) {

		return findByDDMSA_TGI(
			ddmStructuresAvailable, toGroupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the depot entry group rels where ddmStructuresAvailable = &#63; and toGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param ddmStructuresAvailable the ddm structures available
	 * @param toGroupId the to group ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching depot entry group rels
	 */
	@Override
	public List<DepotEntryGroupRel> findByDDMSA_TGI(
		boolean ddmStructuresAvailable, long toGroupId, int start, int end,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		return findByDDMSA_TGI(
			ddmStructuresAvailable, toGroupId, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the depot entry group rels where ddmStructuresAvailable = &#63; and toGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param ddmStructuresAvailable the ddm structures available
	 * @param toGroupId the to group ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching depot entry group rels
	 */
	@Override
	public List<DepotEntryGroupRel> findByDDMSA_TGI(
		boolean ddmStructuresAvailable, long toGroupId, int start, int end,
		OrderByComparator<DepotEntryGroupRel> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DepotEntryGroupRel.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByDDMSA_TGI;
					finderArgs = new Object[] {
						ddmStructuresAvailable, toGroupId
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByDDMSA_TGI;
				finderArgs = new Object[] {
					ddmStructuresAvailable, toGroupId, start, end,
					orderByComparator
				};
			}

			List<DepotEntryGroupRel> list = null;

			if (useFinderCache) {
				list = (List<DepotEntryGroupRel>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DepotEntryGroupRel depotEntryGroupRel : list) {
						if ((ddmStructuresAvailable !=
								depotEntryGroupRel.
									isDdmStructuresAvailable()) ||
							(toGroupId != depotEntryGroupRel.getToGroupId())) {

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

				sb.append(_SQL_SELECT_DEPOTENTRYGROUPREL_WHERE);

				sb.append(_FINDER_COLUMN_DDMSA_TGI_DDMSTRUCTURESAVAILABLE_2);

				sb.append(_FINDER_COLUMN_DDMSA_TGI_TOGROUPID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DepotEntryGroupRelModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(ddmStructuresAvailable);

					queryPos.add(toGroupId);

					list = (List<DepotEntryGroupRel>)QueryUtil.list(
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
	 * Returns the first depot entry group rel in the ordered set where ddmStructuresAvailable = &#63; and toGroupId = &#63;.
	 *
	 * @param ddmStructuresAvailable the ddm structures available
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	@Override
	public DepotEntryGroupRel findByDDMSA_TGI_First(
			boolean ddmStructuresAvailable, long toGroupId,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws NoSuchEntryGroupRelException {

		DepotEntryGroupRel depotEntryGroupRel = fetchByDDMSA_TGI_First(
			ddmStructuresAvailable, toGroupId, orderByComparator);

		if (depotEntryGroupRel != null) {
			return depotEntryGroupRel;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("ddmStructuresAvailable=");
		sb.append(ddmStructuresAvailable);

		sb.append(", toGroupId=");
		sb.append(toGroupId);

		sb.append("}");

		throw new NoSuchEntryGroupRelException(sb.toString());
	}

	/**
	 * Returns the first depot entry group rel in the ordered set where ddmStructuresAvailable = &#63; and toGroupId = &#63;.
	 *
	 * @param ddmStructuresAvailable the ddm structures available
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	@Override
	public DepotEntryGroupRel fetchByDDMSA_TGI_First(
		boolean ddmStructuresAvailable, long toGroupId,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		List<DepotEntryGroupRel> list = findByDDMSA_TGI(
			ddmStructuresAvailable, toGroupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last depot entry group rel in the ordered set where ddmStructuresAvailable = &#63; and toGroupId = &#63;.
	 *
	 * @param ddmStructuresAvailable the ddm structures available
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	@Override
	public DepotEntryGroupRel findByDDMSA_TGI_Last(
			boolean ddmStructuresAvailable, long toGroupId,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws NoSuchEntryGroupRelException {

		DepotEntryGroupRel depotEntryGroupRel = fetchByDDMSA_TGI_Last(
			ddmStructuresAvailable, toGroupId, orderByComparator);

		if (depotEntryGroupRel != null) {
			return depotEntryGroupRel;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("ddmStructuresAvailable=");
		sb.append(ddmStructuresAvailable);

		sb.append(", toGroupId=");
		sb.append(toGroupId);

		sb.append("}");

		throw new NoSuchEntryGroupRelException(sb.toString());
	}

	/**
	 * Returns the last depot entry group rel in the ordered set where ddmStructuresAvailable = &#63; and toGroupId = &#63;.
	 *
	 * @param ddmStructuresAvailable the ddm structures available
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	@Override
	public DepotEntryGroupRel fetchByDDMSA_TGI_Last(
		boolean ddmStructuresAvailable, long toGroupId,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		int count = countByDDMSA_TGI(ddmStructuresAvailable, toGroupId);

		if (count == 0) {
			return null;
		}

		List<DepotEntryGroupRel> list = findByDDMSA_TGI(
			ddmStructuresAvailable, toGroupId, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the depot entry group rels before and after the current depot entry group rel in the ordered set where ddmStructuresAvailable = &#63; and toGroupId = &#63;.
	 *
	 * @param depotEntryGroupRelId the primary key of the current depot entry group rel
	 * @param ddmStructuresAvailable the ddm structures available
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a depot entry group rel with the primary key could not be found
	 */
	@Override
	public DepotEntryGroupRel[] findByDDMSA_TGI_PrevAndNext(
			long depotEntryGroupRelId, boolean ddmStructuresAvailable,
			long toGroupId,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws NoSuchEntryGroupRelException {

		DepotEntryGroupRel depotEntryGroupRel = findByPrimaryKey(
			depotEntryGroupRelId);

		Session session = null;

		try {
			session = openSession();

			DepotEntryGroupRel[] array = new DepotEntryGroupRelImpl[3];

			array[0] = getByDDMSA_TGI_PrevAndNext(
				session, depotEntryGroupRel, ddmStructuresAvailable, toGroupId,
				orderByComparator, true);

			array[1] = depotEntryGroupRel;

			array[2] = getByDDMSA_TGI_PrevAndNext(
				session, depotEntryGroupRel, ddmStructuresAvailable, toGroupId,
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

	protected DepotEntryGroupRel getByDDMSA_TGI_PrevAndNext(
		Session session, DepotEntryGroupRel depotEntryGroupRel,
		boolean ddmStructuresAvailable, long toGroupId,
		OrderByComparator<DepotEntryGroupRel> orderByComparator,
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

		sb.append(_SQL_SELECT_DEPOTENTRYGROUPREL_WHERE);

		sb.append(_FINDER_COLUMN_DDMSA_TGI_DDMSTRUCTURESAVAILABLE_2);

		sb.append(_FINDER_COLUMN_DDMSA_TGI_TOGROUPID_2);

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
			sb.append(DepotEntryGroupRelModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(ddmStructuresAvailable);

		queryPos.add(toGroupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						depotEntryGroupRel)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DepotEntryGroupRel> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the depot entry group rels where ddmStructuresAvailable = &#63; and toGroupId = &#63; from the database.
	 *
	 * @param ddmStructuresAvailable the ddm structures available
	 * @param toGroupId the to group ID
	 */
	@Override
	public void removeByDDMSA_TGI(
		boolean ddmStructuresAvailable, long toGroupId) {

		for (DepotEntryGroupRel depotEntryGroupRel :
				findByDDMSA_TGI(
					ddmStructuresAvailable, toGroupId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(depotEntryGroupRel);
		}
	}

	/**
	 * Returns the number of depot entry group rels where ddmStructuresAvailable = &#63; and toGroupId = &#63;.
	 *
	 * @param ddmStructuresAvailable the ddm structures available
	 * @param toGroupId the to group ID
	 * @return the number of matching depot entry group rels
	 */
	@Override
	public int countByDDMSA_TGI(
		boolean ddmStructuresAvailable, long toGroupId) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DepotEntryGroupRel.class)) {

			FinderPath finderPath = _finderPathCountByDDMSA_TGI;

			Object[] finderArgs = new Object[] {
				ddmStructuresAvailable, toGroupId
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_DEPOTENTRYGROUPREL_WHERE);

				sb.append(_FINDER_COLUMN_DDMSA_TGI_DDMSTRUCTURESAVAILABLE_2);

				sb.append(_FINDER_COLUMN_DDMSA_TGI_TOGROUPID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(ddmStructuresAvailable);

					queryPos.add(toGroupId);

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
		_FINDER_COLUMN_DDMSA_TGI_DDMSTRUCTURESAVAILABLE_2 =
			"depotEntryGroupRel.ddmStructuresAvailable = ? AND ";

	private static final String _FINDER_COLUMN_DDMSA_TGI_TOGROUPID_2 =
		"depotEntryGroupRel.toGroupId = ?";

	private FinderPath _finderPathFetchByD_TGI;

	/**
	 * Returns the depot entry group rel where depotEntryId = &#63; and toGroupId = &#63; or throws a <code>NoSuchEntryGroupRelException</code> if it could not be found.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param toGroupId the to group ID
	 * @return the matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	@Override
	public DepotEntryGroupRel findByD_TGI(long depotEntryId, long toGroupId)
		throws NoSuchEntryGroupRelException {

		DepotEntryGroupRel depotEntryGroupRel = fetchByD_TGI(
			depotEntryId, toGroupId);

		if (depotEntryGroupRel == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("depotEntryId=");
			sb.append(depotEntryId);

			sb.append(", toGroupId=");
			sb.append(toGroupId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchEntryGroupRelException(sb.toString());
		}

		return depotEntryGroupRel;
	}

	/**
	 * Returns the depot entry group rel where depotEntryId = &#63; and toGroupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param toGroupId the to group ID
	 * @return the matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	@Override
	public DepotEntryGroupRel fetchByD_TGI(long depotEntryId, long toGroupId) {
		return fetchByD_TGI(depotEntryId, toGroupId, true);
	}

	/**
	 * Returns the depot entry group rel where depotEntryId = &#63; and toGroupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param toGroupId the to group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	@Override
	public DepotEntryGroupRel fetchByD_TGI(
		long depotEntryId, long toGroupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DepotEntryGroupRel.class)) {

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {depotEntryId, toGroupId};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByD_TGI, finderArgs, this);
			}

			if (result instanceof DepotEntryGroupRel) {
				DepotEntryGroupRel depotEntryGroupRel =
					(DepotEntryGroupRel)result;

				if ((depotEntryId != depotEntryGroupRel.getDepotEntryId()) ||
					(toGroupId != depotEntryGroupRel.getToGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_DEPOTENTRYGROUPREL_WHERE);

				sb.append(_FINDER_COLUMN_D_TGI_DEPOTENTRYID_2);

				sb.append(_FINDER_COLUMN_D_TGI_TOGROUPID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(depotEntryId);

					queryPos.add(toGroupId);

					List<DepotEntryGroupRel> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByD_TGI, finderArgs, list);
						}
					}
					else {
						DepotEntryGroupRel depotEntryGroupRel = list.get(0);

						result = depotEntryGroupRel;

						cacheResult(depotEntryGroupRel);
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
				return (DepotEntryGroupRel)result;
			}
		}
	}

	/**
	 * Removes the depot entry group rel where depotEntryId = &#63; and toGroupId = &#63; from the database.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param toGroupId the to group ID
	 * @return the depot entry group rel that was removed
	 */
	@Override
	public DepotEntryGroupRel removeByD_TGI(long depotEntryId, long toGroupId)
		throws NoSuchEntryGroupRelException {

		DepotEntryGroupRel depotEntryGroupRel = findByD_TGI(
			depotEntryId, toGroupId);

		return remove(depotEntryGroupRel);
	}

	/**
	 * Returns the number of depot entry group rels where depotEntryId = &#63; and toGroupId = &#63;.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param toGroupId the to group ID
	 * @return the number of matching depot entry group rels
	 */
	@Override
	public int countByD_TGI(long depotEntryId, long toGroupId) {
		DepotEntryGroupRel depotEntryGroupRel = fetchByD_TGI(
			depotEntryId, toGroupId);

		if (depotEntryGroupRel == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_D_TGI_DEPOTENTRYID_2 =
		"depotEntryGroupRel.depotEntryId = ? AND ";

	private static final String _FINDER_COLUMN_D_TGI_TOGROUPID_2 =
		"depotEntryGroupRel.toGroupId = ?";

	private FinderPath _finderPathWithPaginationFindByS_TGI;
	private FinderPath _finderPathWithoutPaginationFindByS_TGI;
	private FinderPath _finderPathCountByS_TGI;

	/**
	 * Returns all the depot entry group rels where searchable = &#63; and toGroupId = &#63;.
	 *
	 * @param searchable the searchable
	 * @param toGroupId the to group ID
	 * @return the matching depot entry group rels
	 */
	@Override
	public List<DepotEntryGroupRel> findByS_TGI(
		boolean searchable, long toGroupId) {

		return findByS_TGI(
			searchable, toGroupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the depot entry group rels where searchable = &#63; and toGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param searchable the searchable
	 * @param toGroupId the to group ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @return the range of matching depot entry group rels
	 */
	@Override
	public List<DepotEntryGroupRel> findByS_TGI(
		boolean searchable, long toGroupId, int start, int end) {

		return findByS_TGI(searchable, toGroupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the depot entry group rels where searchable = &#63; and toGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param searchable the searchable
	 * @param toGroupId the to group ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching depot entry group rels
	 */
	@Override
	public List<DepotEntryGroupRel> findByS_TGI(
		boolean searchable, long toGroupId, int start, int end,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		return findByS_TGI(
			searchable, toGroupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the depot entry group rels where searchable = &#63; and toGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param searchable the searchable
	 * @param toGroupId the to group ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching depot entry group rels
	 */
	@Override
	public List<DepotEntryGroupRel> findByS_TGI(
		boolean searchable, long toGroupId, int start, int end,
		OrderByComparator<DepotEntryGroupRel> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DepotEntryGroupRel.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByS_TGI;
					finderArgs = new Object[] {searchable, toGroupId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByS_TGI;
				finderArgs = new Object[] {
					searchable, toGroupId, start, end, orderByComparator
				};
			}

			List<DepotEntryGroupRel> list = null;

			if (useFinderCache) {
				list = (List<DepotEntryGroupRel>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DepotEntryGroupRel depotEntryGroupRel : list) {
						if ((searchable != depotEntryGroupRel.isSearchable()) ||
							(toGroupId != depotEntryGroupRel.getToGroupId())) {

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

				sb.append(_SQL_SELECT_DEPOTENTRYGROUPREL_WHERE);

				sb.append(_FINDER_COLUMN_S_TGI_SEARCHABLE_2);

				sb.append(_FINDER_COLUMN_S_TGI_TOGROUPID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DepotEntryGroupRelModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(searchable);

					queryPos.add(toGroupId);

					list = (List<DepotEntryGroupRel>)QueryUtil.list(
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
	 * Returns the first depot entry group rel in the ordered set where searchable = &#63; and toGroupId = &#63;.
	 *
	 * @param searchable the searchable
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	@Override
	public DepotEntryGroupRel findByS_TGI_First(
			boolean searchable, long toGroupId,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws NoSuchEntryGroupRelException {

		DepotEntryGroupRel depotEntryGroupRel = fetchByS_TGI_First(
			searchable, toGroupId, orderByComparator);

		if (depotEntryGroupRel != null) {
			return depotEntryGroupRel;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("searchable=");
		sb.append(searchable);

		sb.append(", toGroupId=");
		sb.append(toGroupId);

		sb.append("}");

		throw new NoSuchEntryGroupRelException(sb.toString());
	}

	/**
	 * Returns the first depot entry group rel in the ordered set where searchable = &#63; and toGroupId = &#63;.
	 *
	 * @param searchable the searchable
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	@Override
	public DepotEntryGroupRel fetchByS_TGI_First(
		boolean searchable, long toGroupId,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		List<DepotEntryGroupRel> list = findByS_TGI(
			searchable, toGroupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last depot entry group rel in the ordered set where searchable = &#63; and toGroupId = &#63;.
	 *
	 * @param searchable the searchable
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	@Override
	public DepotEntryGroupRel findByS_TGI_Last(
			boolean searchable, long toGroupId,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws NoSuchEntryGroupRelException {

		DepotEntryGroupRel depotEntryGroupRel = fetchByS_TGI_Last(
			searchable, toGroupId, orderByComparator);

		if (depotEntryGroupRel != null) {
			return depotEntryGroupRel;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("searchable=");
		sb.append(searchable);

		sb.append(", toGroupId=");
		sb.append(toGroupId);

		sb.append("}");

		throw new NoSuchEntryGroupRelException(sb.toString());
	}

	/**
	 * Returns the last depot entry group rel in the ordered set where searchable = &#63; and toGroupId = &#63;.
	 *
	 * @param searchable the searchable
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	@Override
	public DepotEntryGroupRel fetchByS_TGI_Last(
		boolean searchable, long toGroupId,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		int count = countByS_TGI(searchable, toGroupId);

		if (count == 0) {
			return null;
		}

		List<DepotEntryGroupRel> list = findByS_TGI(
			searchable, toGroupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the depot entry group rels before and after the current depot entry group rel in the ordered set where searchable = &#63; and toGroupId = &#63;.
	 *
	 * @param depotEntryGroupRelId the primary key of the current depot entry group rel
	 * @param searchable the searchable
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a depot entry group rel with the primary key could not be found
	 */
	@Override
	public DepotEntryGroupRel[] findByS_TGI_PrevAndNext(
			long depotEntryGroupRelId, boolean searchable, long toGroupId,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws NoSuchEntryGroupRelException {

		DepotEntryGroupRel depotEntryGroupRel = findByPrimaryKey(
			depotEntryGroupRelId);

		Session session = null;

		try {
			session = openSession();

			DepotEntryGroupRel[] array = new DepotEntryGroupRelImpl[3];

			array[0] = getByS_TGI_PrevAndNext(
				session, depotEntryGroupRel, searchable, toGroupId,
				orderByComparator, true);

			array[1] = depotEntryGroupRel;

			array[2] = getByS_TGI_PrevAndNext(
				session, depotEntryGroupRel, searchable, toGroupId,
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

	protected DepotEntryGroupRel getByS_TGI_PrevAndNext(
		Session session, DepotEntryGroupRel depotEntryGroupRel,
		boolean searchable, long toGroupId,
		OrderByComparator<DepotEntryGroupRel> orderByComparator,
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

		sb.append(_SQL_SELECT_DEPOTENTRYGROUPREL_WHERE);

		sb.append(_FINDER_COLUMN_S_TGI_SEARCHABLE_2);

		sb.append(_FINDER_COLUMN_S_TGI_TOGROUPID_2);

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
			sb.append(DepotEntryGroupRelModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(searchable);

		queryPos.add(toGroupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						depotEntryGroupRel)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DepotEntryGroupRel> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the depot entry group rels where searchable = &#63; and toGroupId = &#63; from the database.
	 *
	 * @param searchable the searchable
	 * @param toGroupId the to group ID
	 */
	@Override
	public void removeByS_TGI(boolean searchable, long toGroupId) {
		for (DepotEntryGroupRel depotEntryGroupRel :
				findByS_TGI(
					searchable, toGroupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(depotEntryGroupRel);
		}
	}

	/**
	 * Returns the number of depot entry group rels where searchable = &#63; and toGroupId = &#63;.
	 *
	 * @param searchable the searchable
	 * @param toGroupId the to group ID
	 * @return the number of matching depot entry group rels
	 */
	@Override
	public int countByS_TGI(boolean searchable, long toGroupId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DepotEntryGroupRel.class)) {

			FinderPath finderPath = _finderPathCountByS_TGI;

			Object[] finderArgs = new Object[] {searchable, toGroupId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_DEPOTENTRYGROUPREL_WHERE);

				sb.append(_FINDER_COLUMN_S_TGI_SEARCHABLE_2);

				sb.append(_FINDER_COLUMN_S_TGI_TOGROUPID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(searchable);

					queryPos.add(toGroupId);

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

	private static final String _FINDER_COLUMN_S_TGI_SEARCHABLE_2 =
		"depotEntryGroupRel.searchable = ? AND ";

	private static final String _FINDER_COLUMN_S_TGI_TOGROUPID_2 =
		"depotEntryGroupRel.toGroupId = ?";

	private FinderPath _finderPathWithPaginationFindByTGI_T;
	private FinderPath _finderPathWithoutPaginationFindByTGI_T;
	private FinderPath _finderPathCountByTGI_T;

	/**
	 * Returns all the depot entry group rels where toGroupId = &#63; and type = &#63;.
	 *
	 * @param toGroupId the to group ID
	 * @param type the type
	 * @return the matching depot entry group rels
	 */
	@Override
	public List<DepotEntryGroupRel> findByTGI_T(long toGroupId, int type) {
		return findByTGI_T(
			toGroupId, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the depot entry group rels where toGroupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param toGroupId the to group ID
	 * @param type the type
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @return the range of matching depot entry group rels
	 */
	@Override
	public List<DepotEntryGroupRel> findByTGI_T(
		long toGroupId, int type, int start, int end) {

		return findByTGI_T(toGroupId, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the depot entry group rels where toGroupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param toGroupId the to group ID
	 * @param type the type
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching depot entry group rels
	 */
	@Override
	public List<DepotEntryGroupRel> findByTGI_T(
		long toGroupId, int type, int start, int end,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		return findByTGI_T(
			toGroupId, type, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the depot entry group rels where toGroupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param toGroupId the to group ID
	 * @param type the type
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching depot entry group rels
	 */
	@Override
	public List<DepotEntryGroupRel> findByTGI_T(
		long toGroupId, int type, int start, int end,
		OrderByComparator<DepotEntryGroupRel> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DepotEntryGroupRel.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByTGI_T;
					finderArgs = new Object[] {toGroupId, type};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByTGI_T;
				finderArgs = new Object[] {
					toGroupId, type, start, end, orderByComparator
				};
			}

			List<DepotEntryGroupRel> list = null;

			if (useFinderCache) {
				list = (List<DepotEntryGroupRel>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DepotEntryGroupRel depotEntryGroupRel : list) {
						if ((toGroupId != depotEntryGroupRel.getToGroupId()) ||
							(type != depotEntryGroupRel.getType())) {

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

				sb.append(_SQL_SELECT_DEPOTENTRYGROUPREL_WHERE);

				sb.append(_FINDER_COLUMN_TGI_T_TOGROUPID_2);

				sb.append(_FINDER_COLUMN_TGI_T_TYPE_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DepotEntryGroupRelModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(toGroupId);

					queryPos.add(type);

					list = (List<DepotEntryGroupRel>)QueryUtil.list(
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
	 * Returns the first depot entry group rel in the ordered set where toGroupId = &#63; and type = &#63;.
	 *
	 * @param toGroupId the to group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	@Override
	public DepotEntryGroupRel findByTGI_T_First(
			long toGroupId, int type,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws NoSuchEntryGroupRelException {

		DepotEntryGroupRel depotEntryGroupRel = fetchByTGI_T_First(
			toGroupId, type, orderByComparator);

		if (depotEntryGroupRel != null) {
			return depotEntryGroupRel;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("toGroupId=");
		sb.append(toGroupId);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchEntryGroupRelException(sb.toString());
	}

	/**
	 * Returns the first depot entry group rel in the ordered set where toGroupId = &#63; and type = &#63;.
	 *
	 * @param toGroupId the to group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	@Override
	public DepotEntryGroupRel fetchByTGI_T_First(
		long toGroupId, int type,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		List<DepotEntryGroupRel> list = findByTGI_T(
			toGroupId, type, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last depot entry group rel in the ordered set where toGroupId = &#63; and type = &#63;.
	 *
	 * @param toGroupId the to group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	@Override
	public DepotEntryGroupRel findByTGI_T_Last(
			long toGroupId, int type,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws NoSuchEntryGroupRelException {

		DepotEntryGroupRel depotEntryGroupRel = fetchByTGI_T_Last(
			toGroupId, type, orderByComparator);

		if (depotEntryGroupRel != null) {
			return depotEntryGroupRel;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("toGroupId=");
		sb.append(toGroupId);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchEntryGroupRelException(sb.toString());
	}

	/**
	 * Returns the last depot entry group rel in the ordered set where toGroupId = &#63; and type = &#63;.
	 *
	 * @param toGroupId the to group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	@Override
	public DepotEntryGroupRel fetchByTGI_T_Last(
		long toGroupId, int type,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		int count = countByTGI_T(toGroupId, type);

		if (count == 0) {
			return null;
		}

		List<DepotEntryGroupRel> list = findByTGI_T(
			toGroupId, type, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the depot entry group rels before and after the current depot entry group rel in the ordered set where toGroupId = &#63; and type = &#63;.
	 *
	 * @param depotEntryGroupRelId the primary key of the current depot entry group rel
	 * @param toGroupId the to group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a depot entry group rel with the primary key could not be found
	 */
	@Override
	public DepotEntryGroupRel[] findByTGI_T_PrevAndNext(
			long depotEntryGroupRelId, long toGroupId, int type,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws NoSuchEntryGroupRelException {

		DepotEntryGroupRel depotEntryGroupRel = findByPrimaryKey(
			depotEntryGroupRelId);

		Session session = null;

		try {
			session = openSession();

			DepotEntryGroupRel[] array = new DepotEntryGroupRelImpl[3];

			array[0] = getByTGI_T_PrevAndNext(
				session, depotEntryGroupRel, toGroupId, type, orderByComparator,
				true);

			array[1] = depotEntryGroupRel;

			array[2] = getByTGI_T_PrevAndNext(
				session, depotEntryGroupRel, toGroupId, type, orderByComparator,
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

	protected DepotEntryGroupRel getByTGI_T_PrevAndNext(
		Session session, DepotEntryGroupRel depotEntryGroupRel, long toGroupId,
		int type, OrderByComparator<DepotEntryGroupRel> orderByComparator,
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

		sb.append(_SQL_SELECT_DEPOTENTRYGROUPREL_WHERE);

		sb.append(_FINDER_COLUMN_TGI_T_TOGROUPID_2);

		sb.append(_FINDER_COLUMN_TGI_T_TYPE_2);

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
			sb.append(DepotEntryGroupRelModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(toGroupId);

		queryPos.add(type);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						depotEntryGroupRel)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DepotEntryGroupRel> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the depot entry group rels where toGroupId = &#63; and type = &#63; from the database.
	 *
	 * @param toGroupId the to group ID
	 * @param type the type
	 */
	@Override
	public void removeByTGI_T(long toGroupId, int type) {
		for (DepotEntryGroupRel depotEntryGroupRel :
				findByTGI_T(
					toGroupId, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(depotEntryGroupRel);
		}
	}

	/**
	 * Returns the number of depot entry group rels where toGroupId = &#63; and type = &#63;.
	 *
	 * @param toGroupId the to group ID
	 * @param type the type
	 * @return the number of matching depot entry group rels
	 */
	@Override
	public int countByTGI_T(long toGroupId, int type) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DepotEntryGroupRel.class)) {

			FinderPath finderPath = _finderPathCountByTGI_T;

			Object[] finderArgs = new Object[] {toGroupId, type};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_DEPOTENTRYGROUPREL_WHERE);

				sb.append(_FINDER_COLUMN_TGI_T_TOGROUPID_2);

				sb.append(_FINDER_COLUMN_TGI_T_TYPE_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(toGroupId);

					queryPos.add(type);

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

	private static final String _FINDER_COLUMN_TGI_T_TOGROUPID_2 =
		"depotEntryGroupRel.toGroupId = ? AND ";

	private static final String _FINDER_COLUMN_TGI_T_TYPE_2 =
		"depotEntryGroupRel.type = ?";

	public DepotEntryGroupRelPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(DepotEntryGroupRel.class);

		setModelImplClass(DepotEntryGroupRelImpl.class);
		setModelPKClass(long.class);

		setTable(DepotEntryGroupRelTable.INSTANCE);
	}

	/**
	 * Caches the depot entry group rel in the entity cache if it is enabled.
	 *
	 * @param depotEntryGroupRel the depot entry group rel
	 */
	@Override
	public void cacheResult(DepotEntryGroupRel depotEntryGroupRel) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					depotEntryGroupRel.getCtCollectionId())) {

			entityCache.putResult(
				DepotEntryGroupRelImpl.class,
				depotEntryGroupRel.getPrimaryKey(), depotEntryGroupRel);

			finderCache.putResult(
				_finderPathFetchByUUID_G,
				new Object[] {
					depotEntryGroupRel.getUuid(),
					depotEntryGroupRel.getGroupId()
				},
				depotEntryGroupRel);

			finderCache.putResult(
				_finderPathFetchByD_TGI,
				new Object[] {
					depotEntryGroupRel.getDepotEntryId(),
					depotEntryGroupRel.getToGroupId()
				},
				depotEntryGroupRel);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the depot entry group rels in the entity cache if it is enabled.
	 *
	 * @param depotEntryGroupRels the depot entry group rels
	 */
	@Override
	public void cacheResult(List<DepotEntryGroupRel> depotEntryGroupRels) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (depotEntryGroupRels.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (DepotEntryGroupRel depotEntryGroupRel : depotEntryGroupRels) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						depotEntryGroupRel.getCtCollectionId())) {

				if (entityCache.getResult(
						DepotEntryGroupRelImpl.class,
						depotEntryGroupRel.getPrimaryKey()) == null) {

					cacheResult(depotEntryGroupRel);
				}
			}
		}
	}

	/**
	 * Clears the cache for all depot entry group rels.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(DepotEntryGroupRelImpl.class);

		finderCache.clearCache(DepotEntryGroupRelImpl.class);
	}

	/**
	 * Clears the cache for the depot entry group rel.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(DepotEntryGroupRel depotEntryGroupRel) {
		entityCache.removeResult(
			DepotEntryGroupRelImpl.class, depotEntryGroupRel);
	}

	@Override
	public void clearCache(List<DepotEntryGroupRel> depotEntryGroupRels) {
		for (DepotEntryGroupRel depotEntryGroupRel : depotEntryGroupRels) {
			entityCache.removeResult(
				DepotEntryGroupRelImpl.class, depotEntryGroupRel);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(DepotEntryGroupRelImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(DepotEntryGroupRelImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		DepotEntryGroupRelModelImpl depotEntryGroupRelModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					depotEntryGroupRelModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				depotEntryGroupRelModelImpl.getUuid(),
				depotEntryGroupRelModelImpl.getGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByUUID_G, args, depotEntryGroupRelModelImpl);

			args = new Object[] {
				depotEntryGroupRelModelImpl.getDepotEntryId(),
				depotEntryGroupRelModelImpl.getToGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByD_TGI, args, depotEntryGroupRelModelImpl);
		}
	}

	/**
	 * Creates a new depot entry group rel with the primary key. Does not add the depot entry group rel to the database.
	 *
	 * @param depotEntryGroupRelId the primary key for the new depot entry group rel
	 * @return the new depot entry group rel
	 */
	@Override
	public DepotEntryGroupRel create(long depotEntryGroupRelId) {
		DepotEntryGroupRel depotEntryGroupRel = new DepotEntryGroupRelImpl();

		depotEntryGroupRel.setNew(true);
		depotEntryGroupRel.setPrimaryKey(depotEntryGroupRelId);

		String uuid = PortalUUIDUtil.generate();

		depotEntryGroupRel.setUuid(uuid);

		depotEntryGroupRel.setCompanyId(CompanyThreadLocal.getCompanyId());

		return depotEntryGroupRel;
	}

	/**
	 * Removes the depot entry group rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param depotEntryGroupRelId the primary key of the depot entry group rel
	 * @return the depot entry group rel that was removed
	 * @throws NoSuchEntryGroupRelException if a depot entry group rel with the primary key could not be found
	 */
	@Override
	public DepotEntryGroupRel remove(long depotEntryGroupRelId)
		throws NoSuchEntryGroupRelException {

		return remove((Serializable)depotEntryGroupRelId);
	}

	/**
	 * Removes the depot entry group rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the depot entry group rel
	 * @return the depot entry group rel that was removed
	 * @throws NoSuchEntryGroupRelException if a depot entry group rel with the primary key could not be found
	 */
	@Override
	public DepotEntryGroupRel remove(Serializable primaryKey)
		throws NoSuchEntryGroupRelException {

		Session session = null;

		try {
			session = openSession();

			DepotEntryGroupRel depotEntryGroupRel =
				(DepotEntryGroupRel)session.get(
					DepotEntryGroupRelImpl.class, primaryKey);

			if (depotEntryGroupRel == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchEntryGroupRelException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(depotEntryGroupRel);
		}
		catch (NoSuchEntryGroupRelException noSuchEntityException) {
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
	protected DepotEntryGroupRel removeImpl(
		DepotEntryGroupRel depotEntryGroupRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(depotEntryGroupRel)) {
				depotEntryGroupRel = (DepotEntryGroupRel)session.get(
					DepotEntryGroupRelImpl.class,
					depotEntryGroupRel.getPrimaryKeyObj());
			}

			if ((depotEntryGroupRel != null) &&
				ctPersistenceHelper.isRemove(depotEntryGroupRel)) {

				session.delete(depotEntryGroupRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (depotEntryGroupRel != null) {
			clearCache(depotEntryGroupRel);
		}

		return depotEntryGroupRel;
	}

	@Override
	public DepotEntryGroupRel updateImpl(
		DepotEntryGroupRel depotEntryGroupRel) {

		boolean isNew = depotEntryGroupRel.isNew();

		if (!(depotEntryGroupRel instanceof DepotEntryGroupRelModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(depotEntryGroupRel.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					depotEntryGroupRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in depotEntryGroupRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DepotEntryGroupRel implementation " +
					depotEntryGroupRel.getClass());
		}

		DepotEntryGroupRelModelImpl depotEntryGroupRelModelImpl =
			(DepotEntryGroupRelModelImpl)depotEntryGroupRel;

		if (Validator.isNull(depotEntryGroupRel.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			depotEntryGroupRel.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (depotEntryGroupRel.getCreateDate() == null)) {
			if (serviceContext == null) {
				depotEntryGroupRel.setCreateDate(date);
			}
			else {
				depotEntryGroupRel.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!depotEntryGroupRelModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				depotEntryGroupRel.setModifiedDate(date);
			}
			else {
				depotEntryGroupRel.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(depotEntryGroupRel)) {
				if (!isNew) {
					session.evict(
						DepotEntryGroupRelImpl.class,
						depotEntryGroupRel.getPrimaryKeyObj());
				}

				session.save(depotEntryGroupRel);
			}
			else {
				depotEntryGroupRel = (DepotEntryGroupRel)session.merge(
					depotEntryGroupRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			DepotEntryGroupRelImpl.class, depotEntryGroupRelModelImpl, false,
			true);

		cacheUniqueFindersCache(depotEntryGroupRelModelImpl);

		if (isNew) {
			depotEntryGroupRel.setNew(false);
		}

		depotEntryGroupRel.resetOriginalValues();

		return depotEntryGroupRel;
	}

	/**
	 * Returns the depot entry group rel with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the depot entry group rel
	 * @return the depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a depot entry group rel with the primary key could not be found
	 */
	@Override
	public DepotEntryGroupRel findByPrimaryKey(Serializable primaryKey)
		throws NoSuchEntryGroupRelException {

		DepotEntryGroupRel depotEntryGroupRel = fetchByPrimaryKey(primaryKey);

		if (depotEntryGroupRel == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchEntryGroupRelException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return depotEntryGroupRel;
	}

	/**
	 * Returns the depot entry group rel with the primary key or throws a <code>NoSuchEntryGroupRelException</code> if it could not be found.
	 *
	 * @param depotEntryGroupRelId the primary key of the depot entry group rel
	 * @return the depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a depot entry group rel with the primary key could not be found
	 */
	@Override
	public DepotEntryGroupRel findByPrimaryKey(long depotEntryGroupRelId)
		throws NoSuchEntryGroupRelException {

		return findByPrimaryKey((Serializable)depotEntryGroupRelId);
	}

	/**
	 * Returns the depot entry group rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the depot entry group rel
	 * @return the depot entry group rel, or <code>null</code> if a depot entry group rel with the primary key could not be found
	 */
	@Override
	public DepotEntryGroupRel fetchByPrimaryKey(Serializable primaryKey) {
		if (ctPersistenceHelper.isProductionMode(
				DepotEntryGroupRel.class, primaryKey)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		DepotEntryGroupRel depotEntryGroupRel =
			(DepotEntryGroupRel)entityCache.getResult(
				DepotEntryGroupRelImpl.class, primaryKey);

		if (depotEntryGroupRel != null) {
			return depotEntryGroupRel;
		}

		Session session = null;

		try {
			session = openSession();

			depotEntryGroupRel = (DepotEntryGroupRel)session.get(
				DepotEntryGroupRelImpl.class, primaryKey);

			if (depotEntryGroupRel != null) {
				cacheResult(depotEntryGroupRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return depotEntryGroupRel;
	}

	/**
	 * Returns the depot entry group rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param depotEntryGroupRelId the primary key of the depot entry group rel
	 * @return the depot entry group rel, or <code>null</code> if a depot entry group rel with the primary key could not be found
	 */
	@Override
	public DepotEntryGroupRel fetchByPrimaryKey(long depotEntryGroupRelId) {
		return fetchByPrimaryKey((Serializable)depotEntryGroupRelId);
	}

	@Override
	public Map<Serializable, DepotEntryGroupRel> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (ctPersistenceHelper.isProductionMode(DepotEntryGroupRel.class)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, DepotEntryGroupRel> map =
			new HashMap<Serializable, DepotEntryGroupRel>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			DepotEntryGroupRel depotEntryGroupRel = fetchByPrimaryKey(
				primaryKey);

			if (depotEntryGroupRel != null) {
				map.put(primaryKey, depotEntryGroupRel);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
						DepotEntryGroupRel.class, primaryKey)) {

				DepotEntryGroupRel depotEntryGroupRel =
					(DepotEntryGroupRel)entityCache.getResult(
						DepotEntryGroupRelImpl.class, primaryKey);

				if (depotEntryGroupRel == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, depotEntryGroupRel);
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

			for (DepotEntryGroupRel depotEntryGroupRel :
					(List<DepotEntryGroupRel>)query.list()) {

				map.put(
					depotEntryGroupRel.getPrimaryKeyObj(), depotEntryGroupRel);

				cacheResult(depotEntryGroupRel);
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
	 * Returns all the depot entry group rels.
	 *
	 * @return the depot entry group rels
	 */
	@Override
	public List<DepotEntryGroupRel> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the depot entry group rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @return the range of depot entry group rels
	 */
	@Override
	public List<DepotEntryGroupRel> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the depot entry group rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of depot entry group rels
	 */
	@Override
	public List<DepotEntryGroupRel> findAll(
		int start, int end,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the depot entry group rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of depot entry group rels
	 */
	@Override
	public List<DepotEntryGroupRel> findAll(
		int start, int end,
		OrderByComparator<DepotEntryGroupRel> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DepotEntryGroupRel.class)) {

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

			List<DepotEntryGroupRel> list = null;

			if (useFinderCache) {
				list = (List<DepotEntryGroupRel>)finderCache.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_DEPOTENTRYGROUPREL);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_DEPOTENTRYGROUPREL;

					sql = sql.concat(DepotEntryGroupRelModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<DepotEntryGroupRel>)QueryUtil.list(
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
	 * Removes all the depot entry group rels from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (DepotEntryGroupRel depotEntryGroupRel : findAll()) {
			remove(depotEntryGroupRel);
		}
	}

	/**
	 * Returns the number of depot entry group rels.
	 *
	 * @return the number of depot entry group rels
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DepotEntryGroupRel.class)) {

			Long count = (Long)finderCache.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(
						_SQL_COUNT_DEPOTENTRYGROUPREL);

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
		return "depotEntryGroupRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DEPOTENTRYGROUPREL;
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
		return DepotEntryGroupRelModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DepotEntryGroupRel";
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
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("ddmStructuresAvailable");
		ctMergeColumnNames.add("depotEntryId");
		ctMergeColumnNames.add("searchable");
		ctMergeColumnNames.add("toGroupId");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("depotEntryGroupRelId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(new String[] {"depotEntryId", "toGroupId"});
	}

	/**
	 * Initializes the depot entry group rel persistence.
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

		_finderPathWithPaginationFindByDepotEntryId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByDepotEntryId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"depotEntryId"}, true);

		_finderPathWithoutPaginationFindByDepotEntryId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByDepotEntryId",
			new String[] {Long.class.getName()}, new String[] {"depotEntryId"},
			true);

		_finderPathCountByDepotEntryId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByDepotEntryId",
			new String[] {Long.class.getName()}, new String[] {"depotEntryId"},
			false);

		_finderPathWithPaginationFindByToGroupId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByToGroupId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"toGroupId"}, true);

		_finderPathWithoutPaginationFindByToGroupId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByToGroupId",
			new String[] {Long.class.getName()}, new String[] {"toGroupId"},
			true);

		_finderPathCountByToGroupId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByToGroupId",
			new String[] {Long.class.getName()}, new String[] {"toGroupId"},
			false);

		_finderPathWithPaginationFindByDDMSA_TGI = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByDDMSA_TGI",
			new String[] {
				Boolean.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"ddmStructuresAvailable", "toGroupId"}, true);

		_finderPathWithoutPaginationFindByDDMSA_TGI = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByDDMSA_TGI",
			new String[] {Boolean.class.getName(), Long.class.getName()},
			new String[] {"ddmStructuresAvailable", "toGroupId"}, true);

		_finderPathCountByDDMSA_TGI = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByDDMSA_TGI",
			new String[] {Boolean.class.getName(), Long.class.getName()},
			new String[] {"ddmStructuresAvailable", "toGroupId"}, false);

		_finderPathFetchByD_TGI = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByD_TGI",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"depotEntryId", "toGroupId"}, true);

		_finderPathWithPaginationFindByS_TGI = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByS_TGI",
			new String[] {
				Boolean.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"searchable", "toGroupId"}, true);

		_finderPathWithoutPaginationFindByS_TGI = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByS_TGI",
			new String[] {Boolean.class.getName(), Long.class.getName()},
			new String[] {"searchable", "toGroupId"}, true);

		_finderPathCountByS_TGI = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByS_TGI",
			new String[] {Boolean.class.getName(), Long.class.getName()},
			new String[] {"searchable", "toGroupId"}, false);

		_finderPathWithPaginationFindByTGI_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByTGI_T",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"toGroupId", "type_"}, true);

		_finderPathWithoutPaginationFindByTGI_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByTGI_T",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"toGroupId", "type_"}, true);

		_finderPathCountByTGI_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByTGI_T",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"toGroupId", "type_"}, false);

		DepotEntryGroupRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		DepotEntryGroupRelUtil.setPersistence(null);

		entityCache.removeCache(DepotEntryGroupRelImpl.class.getName());
	}

	@Override
	@Reference(
		target = DepotPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = DepotPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = DepotPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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

	private static final String _SQL_SELECT_DEPOTENTRYGROUPREL =
		"SELECT depotEntryGroupRel FROM DepotEntryGroupRel depotEntryGroupRel";

	private static final String _SQL_SELECT_DEPOTENTRYGROUPREL_WHERE =
		"SELECT depotEntryGroupRel FROM DepotEntryGroupRel depotEntryGroupRel WHERE ";

	private static final String _SQL_COUNT_DEPOTENTRYGROUPREL =
		"SELECT COUNT(depotEntryGroupRel) FROM DepotEntryGroupRel depotEntryGroupRel";

	private static final String _SQL_COUNT_DEPOTENTRYGROUPREL_WHERE =
		"SELECT COUNT(depotEntryGroupRel) FROM DepotEntryGroupRel depotEntryGroupRel WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS = "depotEntryGroupRel.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No DepotEntryGroupRel exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No DepotEntryGroupRel exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		DepotEntryGroupRelPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}