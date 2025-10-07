/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service.persistence.impl;

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
import com.liferay.segments.exception.DuplicateSegmentsExperienceExternalReferenceCodeException;
import com.liferay.segments.exception.NoSuchExperienceException;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.model.SegmentsExperienceTable;
import com.liferay.segments.model.impl.SegmentsExperienceImpl;
import com.liferay.segments.model.impl.SegmentsExperienceModelImpl;
import com.liferay.segments.service.persistence.SegmentsExperiencePersistence;
import com.liferay.segments.service.persistence.SegmentsExperienceUtil;
import com.liferay.segments.service.persistence.impl.constants.SegmentsPersistenceConstants;

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
 * The persistence implementation for the segments experience service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Eduardo Garcia
 * @generated
 */
@Component(service = SegmentsExperiencePersistence.class)
public class SegmentsExperiencePersistenceImpl
	extends BasePersistenceImpl<SegmentsExperience>
	implements SegmentsExperiencePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>SegmentsExperienceUtil</code> to access the segments experience persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		SegmentsExperienceImpl.class.getName();

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
	 * Returns all the segments experiences where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the segments experiences where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the segments experiences where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the segments experiences where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SegmentsExperience.class)) {

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

			List<SegmentsExperience> list = null;

			if (useFinderCache) {
				list = (List<SegmentsExperience>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (SegmentsExperience segmentsExperience : list) {
						if (!uuid.equals(segmentsExperience.getUuid())) {
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

				sb.append(_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);

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
					sb.append(SegmentsExperienceModelImpl.ORDER_BY_JPQL);
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

					list = (List<SegmentsExperience>)QueryUtil.list(
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
	 * Returns the first segments experience in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findByUuid_First(
			String uuid,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = fetchByUuid_First(
			uuid, orderByComparator);

		if (segmentsExperience != null) {
			return segmentsExperience;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchExperienceException(sb.toString());
	}

	/**
	 * Returns the first segments experience in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByUuid_First(
		String uuid, OrderByComparator<SegmentsExperience> orderByComparator) {

		List<SegmentsExperience> list = findByUuid(
			uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last segments experience in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findByUuid_Last(
			String uuid,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = fetchByUuid_Last(
			uuid, orderByComparator);

		if (segmentsExperience != null) {
			return segmentsExperience;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchExperienceException(sb.toString());
	}

	/**
	 * Returns the last segments experience in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByUuid_Last(
		String uuid, OrderByComparator<SegmentsExperience> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<SegmentsExperience> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the segments experiences before and after the current segments experience in the ordered set where uuid = &#63;.
	 *
	 * @param segmentsExperienceId the primary key of the current segments experience
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	@Override
	public SegmentsExperience[] findByUuid_PrevAndNext(
			long segmentsExperienceId, String uuid,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		uuid = Objects.toString(uuid, "");

		SegmentsExperience segmentsExperience = findByPrimaryKey(
			segmentsExperienceId);

		Session session = null;

		try {
			session = openSession();

			SegmentsExperience[] array = new SegmentsExperienceImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, segmentsExperience, uuid, orderByComparator, true);

			array[1] = segmentsExperience;

			array[2] = getByUuid_PrevAndNext(
				session, segmentsExperience, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected SegmentsExperience getByUuid_PrevAndNext(
		Session session, SegmentsExperience segmentsExperience, String uuid,
		OrderByComparator<SegmentsExperience> orderByComparator,
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

		sb.append(_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);

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
			sb.append(SegmentsExperienceModelImpl.ORDER_BY_JPQL);
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
						segmentsExperience)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<SegmentsExperience> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the segments experiences where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (SegmentsExperience segmentsExperience :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(segmentsExperience);
		}
	}

	/**
	 * Returns the number of segments experiences where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching segments experiences
	 */
	@Override
	public int countByUuid(String uuid) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SegmentsExperience.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid;

			Object[] finderArgs = new Object[] {uuid};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_SEGMENTSEXPERIENCE_WHERE);

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
		"segmentsExperience.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(segmentsExperience.uuid IS NULL OR segmentsExperience.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;

	/**
	 * Returns the segments experience where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchExperienceException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findByUUID_G(String uuid, long groupId)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = fetchByUUID_G(uuid, groupId);

		if (segmentsExperience == null) {
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

			throw new NoSuchExperienceException(sb.toString());
		}

		return segmentsExperience;
	}

	/**
	 * Returns the segments experience where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the segments experience where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SegmentsExperience.class)) {

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

			if (result instanceof SegmentsExperience) {
				SegmentsExperience segmentsExperience =
					(SegmentsExperience)result;

				if (!Objects.equals(uuid, segmentsExperience.getUuid()) ||
					(groupId != segmentsExperience.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);

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

					List<SegmentsExperience> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByUUID_G, finderArgs, list);
						}
					}
					else {
						SegmentsExperience segmentsExperience = list.get(0);

						result = segmentsExperience;

						cacheResult(segmentsExperience);
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
				return (SegmentsExperience)result;
			}
		}
	}

	/**
	 * Removes the segments experience where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the segments experience that was removed
	 */
	@Override
	public SegmentsExperience removeByUUID_G(String uuid, long groupId)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = findByUUID_G(uuid, groupId);

		return remove(segmentsExperience);
	}

	/**
	 * Returns the number of segments experiences where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching segments experiences
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		SegmentsExperience segmentsExperience = fetchByUUID_G(uuid, groupId);

		if (segmentsExperience == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"segmentsExperience.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(segmentsExperience.uuid IS NULL OR segmentsExperience.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"segmentsExperience.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the segments experiences where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the segments experiences where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the segments experiences where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the segments experiences where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SegmentsExperience.class)) {

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

			List<SegmentsExperience> list = null;

			if (useFinderCache) {
				list = (List<SegmentsExperience>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (SegmentsExperience segmentsExperience : list) {
						if (!uuid.equals(segmentsExperience.getUuid()) ||
							(companyId != segmentsExperience.getCompanyId())) {

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

				sb.append(_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);

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
					sb.append(SegmentsExperienceModelImpl.ORDER_BY_JPQL);
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

					list = (List<SegmentsExperience>)QueryUtil.list(
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
	 * Returns the first segments experience in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (segmentsExperience != null) {
			return segmentsExperience;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchExperienceException(sb.toString());
	}

	/**
	 * Returns the first segments experience in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		List<SegmentsExperience> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last segments experience in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (segmentsExperience != null) {
			return segmentsExperience;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchExperienceException(sb.toString());
	}

	/**
	 * Returns the last segments experience in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<SegmentsExperience> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the segments experiences before and after the current segments experience in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param segmentsExperienceId the primary key of the current segments experience
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	@Override
	public SegmentsExperience[] findByUuid_C_PrevAndNext(
			long segmentsExperienceId, String uuid, long companyId,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		uuid = Objects.toString(uuid, "");

		SegmentsExperience segmentsExperience = findByPrimaryKey(
			segmentsExperienceId);

		Session session = null;

		try {
			session = openSession();

			SegmentsExperience[] array = new SegmentsExperienceImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, segmentsExperience, uuid, companyId, orderByComparator,
				true);

			array[1] = segmentsExperience;

			array[2] = getByUuid_C_PrevAndNext(
				session, segmentsExperience, uuid, companyId, orderByComparator,
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

	protected SegmentsExperience getByUuid_C_PrevAndNext(
		Session session, SegmentsExperience segmentsExperience, String uuid,
		long companyId, OrderByComparator<SegmentsExperience> orderByComparator,
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

		sb.append(_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);

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
			sb.append(SegmentsExperienceModelImpl.ORDER_BY_JPQL);
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
						segmentsExperience)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<SegmentsExperience> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the segments experiences where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (SegmentsExperience segmentsExperience :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(segmentsExperience);
		}
	}

	/**
	 * Returns the number of segments experiences where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching segments experiences
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SegmentsExperience.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid_C;

			Object[] finderArgs = new Object[] {uuid, companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_SEGMENTSEXPERIENCE_WHERE);

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
		"segmentsExperience.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(segmentsExperience.uuid IS NULL OR segmentsExperience.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"segmentsExperience.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByGroupId;
	private FinderPath _finderPathWithoutPaginationFindByGroupId;
	private FinderPath _finderPathCountByGroupId;

	/**
	 * Returns all the segments experiences where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the segments experiences where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByGroupId(
		long groupId, int start, int end) {

		return findByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SegmentsExperience.class)) {

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

			List<SegmentsExperience> list = null;

			if (useFinderCache) {
				list = (List<SegmentsExperience>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (SegmentsExperience segmentsExperience : list) {
						if (groupId != segmentsExperience.getGroupId()) {
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

				sb.append(_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);

				sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(SegmentsExperienceModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					list = (List<SegmentsExperience>)QueryUtil.list(
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
	 * Returns the first segments experience in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findByGroupId_First(
			long groupId,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = fetchByGroupId_First(
			groupId, orderByComparator);

		if (segmentsExperience != null) {
			return segmentsExperience;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchExperienceException(sb.toString());
	}

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByGroupId_First(
		long groupId, OrderByComparator<SegmentsExperience> orderByComparator) {

		List<SegmentsExperience> list = findByGroupId(
			groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last segments experience in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findByGroupId_Last(
			long groupId,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = fetchByGroupId_Last(
			groupId, orderByComparator);

		if (segmentsExperience != null) {
			return segmentsExperience;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchExperienceException(sb.toString());
	}

	/**
	 * Returns the last segments experience in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByGroupId_Last(
		long groupId, OrderByComparator<SegmentsExperience> orderByComparator) {

		int count = countByGroupId(groupId);

		if (count == 0) {
			return null;
		}

		List<SegmentsExperience> list = findByGroupId(
			groupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the segments experiences before and after the current segments experience in the ordered set where groupId = &#63;.
	 *
	 * @param segmentsExperienceId the primary key of the current segments experience
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	@Override
	public SegmentsExperience[] findByGroupId_PrevAndNext(
			long segmentsExperienceId, long groupId,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = findByPrimaryKey(
			segmentsExperienceId);

		Session session = null;

		try {
			session = openSession();

			SegmentsExperience[] array = new SegmentsExperienceImpl[3];

			array[0] = getByGroupId_PrevAndNext(
				session, segmentsExperience, groupId, orderByComparator, true);

			array[1] = segmentsExperience;

			array[2] = getByGroupId_PrevAndNext(
				session, segmentsExperience, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected SegmentsExperience getByGroupId_PrevAndNext(
		Session session, SegmentsExperience segmentsExperience, long groupId,
		OrderByComparator<SegmentsExperience> orderByComparator,
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

		sb.append(_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);

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
			sb.append(SegmentsExperienceModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						segmentsExperience)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<SegmentsExperience> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the segments experiences that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByGroupId(long groupId) {
		return filterFindByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the segments experiences that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByGroupId(
		long groupId, int start, int end) {

		return filterFindByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the segments experiences that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_NO_INLINE_DISTINCT_WHERE_2);
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
					SegmentsExperienceModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(SegmentsExperienceModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), SegmentsExperience.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, SegmentsExperienceImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, SegmentsExperienceImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			return (List<SegmentsExperience>)QueryUtil.list(
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
	 * Returns the segments experiences before and after the current segments experience in the ordered set of segments experiences that the user has permission to view where groupId = &#63;.
	 *
	 * @param segmentsExperienceId the primary key of the current segments experience
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	@Override
	public SegmentsExperience[] filterFindByGroupId_PrevAndNext(
			long segmentsExperienceId, long groupId,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByGroupId_PrevAndNext(
				segmentsExperienceId, groupId, orderByComparator);
		}

		SegmentsExperience segmentsExperience = findByPrimaryKey(
			segmentsExperienceId);

		Session session = null;

		try {
			session = openSession();

			SegmentsExperience[] array = new SegmentsExperienceImpl[3];

			array[0] = filterGetByGroupId_PrevAndNext(
				session, segmentsExperience, groupId, orderByComparator, true);

			array[1] = segmentsExperience;

			array[2] = filterGetByGroupId_PrevAndNext(
				session, segmentsExperience, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected SegmentsExperience filterGetByGroupId_PrevAndNext(
		Session session, SegmentsExperience segmentsExperience, long groupId,
		OrderByComparator<SegmentsExperience> orderByComparator,
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

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_NO_INLINE_DISTINCT_WHERE_2);
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
					SegmentsExperienceModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(SegmentsExperienceModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), SegmentsExperience.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, SegmentsExperienceImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, SegmentsExperienceImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						segmentsExperience)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<SegmentsExperience> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the segments experiences where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		for (SegmentsExperience segmentsExperience :
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(segmentsExperience);
		}
	}

	/**
	 * Returns the number of segments experiences where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching segments experiences
	 */
	@Override
	public int countByGroupId(long groupId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SegmentsExperience.class)) {

			FinderPath finderPath = _finderPathCountByGroupId;

			Object[] finderArgs = new Object[] {groupId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_SEGMENTSEXPERIENCE_WHERE);

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
	 * Returns the number of segments experiences that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching segments experiences that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByGroupId(groupId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<SegmentsExperience> segmentsExperiences = findByGroupId(
				groupId);

			segmentsExperiences = InlineSQLHelperUtil.filter(
				segmentsExperiences, groupId);

			return segmentsExperiences.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_SEGMENTSEXPERIENCE_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), SegmentsExperience.class.getName(),
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

	private static final String _FINDER_COLUMN_GROUPID_GROUPID_2 =
		"segmentsExperience.groupId = ?";

	private FinderPath _finderPathWithPaginationFindBySegmentsEntryId;
	private FinderPath _finderPathWithoutPaginationFindBySegmentsEntryId;
	private FinderPath _finderPathCountBySegmentsEntryId;

	/**
	 * Returns all the segments experiences where segmentsEntryId = &#63;.
	 *
	 * @param segmentsEntryId the segments entry ID
	 * @return the matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findBySegmentsEntryId(
		long segmentsEntryId) {

		return findBySegmentsEntryId(
			segmentsEntryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the segments experiences where segmentsEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param segmentsEntryId the segments entry ID
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findBySegmentsEntryId(
		long segmentsEntryId, int start, int end) {

		return findBySegmentsEntryId(segmentsEntryId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the segments experiences where segmentsEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param segmentsEntryId the segments entry ID
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findBySegmentsEntryId(
		long segmentsEntryId, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return findBySegmentsEntryId(
			segmentsEntryId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the segments experiences where segmentsEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param segmentsEntryId the segments entry ID
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findBySegmentsEntryId(
		long segmentsEntryId, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SegmentsExperience.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath =
						_finderPathWithoutPaginationFindBySegmentsEntryId;
					finderArgs = new Object[] {segmentsEntryId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindBySegmentsEntryId;
				finderArgs = new Object[] {
					segmentsEntryId, start, end, orderByComparator
				};
			}

			List<SegmentsExperience> list = null;

			if (useFinderCache) {
				list = (List<SegmentsExperience>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (SegmentsExperience segmentsExperience : list) {
						if (segmentsEntryId !=
								segmentsExperience.getSegmentsEntryId()) {

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

				sb.append(_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);

				sb.append(_FINDER_COLUMN_SEGMENTSENTRYID_SEGMENTSENTRYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(SegmentsExperienceModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(segmentsEntryId);

					list = (List<SegmentsExperience>)QueryUtil.list(
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
	 * Returns the first segments experience in the ordered set where segmentsEntryId = &#63;.
	 *
	 * @param segmentsEntryId the segments entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findBySegmentsEntryId_First(
			long segmentsEntryId,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = fetchBySegmentsEntryId_First(
			segmentsEntryId, orderByComparator);

		if (segmentsExperience != null) {
			return segmentsExperience;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("segmentsEntryId=");
		sb.append(segmentsEntryId);

		sb.append("}");

		throw new NoSuchExperienceException(sb.toString());
	}

	/**
	 * Returns the first segments experience in the ordered set where segmentsEntryId = &#63;.
	 *
	 * @param segmentsEntryId the segments entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchBySegmentsEntryId_First(
		long segmentsEntryId,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		List<SegmentsExperience> list = findBySegmentsEntryId(
			segmentsEntryId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last segments experience in the ordered set where segmentsEntryId = &#63;.
	 *
	 * @param segmentsEntryId the segments entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findBySegmentsEntryId_Last(
			long segmentsEntryId,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = fetchBySegmentsEntryId_Last(
			segmentsEntryId, orderByComparator);

		if (segmentsExperience != null) {
			return segmentsExperience;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("segmentsEntryId=");
		sb.append(segmentsEntryId);

		sb.append("}");

		throw new NoSuchExperienceException(sb.toString());
	}

	/**
	 * Returns the last segments experience in the ordered set where segmentsEntryId = &#63;.
	 *
	 * @param segmentsEntryId the segments entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchBySegmentsEntryId_Last(
		long segmentsEntryId,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		int count = countBySegmentsEntryId(segmentsEntryId);

		if (count == 0) {
			return null;
		}

		List<SegmentsExperience> list = findBySegmentsEntryId(
			segmentsEntryId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the segments experiences before and after the current segments experience in the ordered set where segmentsEntryId = &#63;.
	 *
	 * @param segmentsExperienceId the primary key of the current segments experience
	 * @param segmentsEntryId the segments entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	@Override
	public SegmentsExperience[] findBySegmentsEntryId_PrevAndNext(
			long segmentsExperienceId, long segmentsEntryId,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = findByPrimaryKey(
			segmentsExperienceId);

		Session session = null;

		try {
			session = openSession();

			SegmentsExperience[] array = new SegmentsExperienceImpl[3];

			array[0] = getBySegmentsEntryId_PrevAndNext(
				session, segmentsExperience, segmentsEntryId, orderByComparator,
				true);

			array[1] = segmentsExperience;

			array[2] = getBySegmentsEntryId_PrevAndNext(
				session, segmentsExperience, segmentsEntryId, orderByComparator,
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

	protected SegmentsExperience getBySegmentsEntryId_PrevAndNext(
		Session session, SegmentsExperience segmentsExperience,
		long segmentsEntryId,
		OrderByComparator<SegmentsExperience> orderByComparator,
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

		sb.append(_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);

		sb.append(_FINDER_COLUMN_SEGMENTSENTRYID_SEGMENTSENTRYID_2);

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
			sb.append(SegmentsExperienceModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(segmentsEntryId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						segmentsExperience)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<SegmentsExperience> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the segments experiences where segmentsEntryId = &#63; from the database.
	 *
	 * @param segmentsEntryId the segments entry ID
	 */
	@Override
	public void removeBySegmentsEntryId(long segmentsEntryId) {
		for (SegmentsExperience segmentsExperience :
				findBySegmentsEntryId(
					segmentsEntryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(segmentsExperience);
		}
	}

	/**
	 * Returns the number of segments experiences where segmentsEntryId = &#63;.
	 *
	 * @param segmentsEntryId the segments entry ID
	 * @return the number of matching segments experiences
	 */
	@Override
	public int countBySegmentsEntryId(long segmentsEntryId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SegmentsExperience.class)) {

			FinderPath finderPath = _finderPathCountBySegmentsEntryId;

			Object[] finderArgs = new Object[] {segmentsEntryId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_SEGMENTSEXPERIENCE_WHERE);

				sb.append(_FINDER_COLUMN_SEGMENTSENTRYID_SEGMENTSENTRYID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(segmentsEntryId);

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
		_FINDER_COLUMN_SEGMENTSENTRYID_SEGMENTSENTRYID_2 =
			"segmentsExperience.segmentsEntryId = ?";

	private FinderPath _finderPathWithPaginationFindByG_P;
	private FinderPath _finderPathWithoutPaginationFindByG_P;
	private FinderPath _finderPathCountByG_P;

	/**
	 * Returns all the segments experiences where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @return the matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_P(long groupId, long plid) {
		return findByG_P(
			groupId, plid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the segments experiences where groupId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_P(
		long groupId, long plid, int start, int end) {

		return findByG_P(groupId, plid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_P(
		long groupId, long plid, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return findByG_P(groupId, plid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_P(
		long groupId, long plid, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SegmentsExperience.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_P;
					finderArgs = new Object[] {groupId, plid};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_P;
				finderArgs = new Object[] {
					groupId, plid, start, end, orderByComparator
				};
			}

			List<SegmentsExperience> list = null;

			if (useFinderCache) {
				list = (List<SegmentsExperience>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (SegmentsExperience segmentsExperience : list) {
						if ((groupId != segmentsExperience.getGroupId()) ||
							(plid != segmentsExperience.getPlid())) {

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

				sb.append(_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);

				sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_PLID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(SegmentsExperienceModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(plid);

					list = (List<SegmentsExperience>)QueryUtil.list(
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
	 * Returns the first segments experience in the ordered set where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findByG_P_First(
			long groupId, long plid,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = fetchByG_P_First(
			groupId, plid, orderByComparator);

		if (segmentsExperience != null) {
			return segmentsExperience;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", plid=");
		sb.append(plid);

		sb.append("}");

		throw new NoSuchExperienceException(sb.toString());
	}

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByG_P_First(
		long groupId, long plid,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		List<SegmentsExperience> list = findByG_P(
			groupId, plid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last segments experience in the ordered set where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findByG_P_Last(
			long groupId, long plid,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = fetchByG_P_Last(
			groupId, plid, orderByComparator);

		if (segmentsExperience != null) {
			return segmentsExperience;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", plid=");
		sb.append(plid);

		sb.append("}");

		throw new NoSuchExperienceException(sb.toString());
	}

	/**
	 * Returns the last segments experience in the ordered set where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByG_P_Last(
		long groupId, long plid,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		int count = countByG_P(groupId, plid);

		if (count == 0) {
			return null;
		}

		List<SegmentsExperience> list = findByG_P(
			groupId, plid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the segments experiences before and after the current segments experience in the ordered set where groupId = &#63; and plid = &#63;.
	 *
	 * @param segmentsExperienceId the primary key of the current segments experience
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	@Override
	public SegmentsExperience[] findByG_P_PrevAndNext(
			long segmentsExperienceId, long groupId, long plid,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = findByPrimaryKey(
			segmentsExperienceId);

		Session session = null;

		try {
			session = openSession();

			SegmentsExperience[] array = new SegmentsExperienceImpl[3];

			array[0] = getByG_P_PrevAndNext(
				session, segmentsExperience, groupId, plid, orderByComparator,
				true);

			array[1] = segmentsExperience;

			array[2] = getByG_P_PrevAndNext(
				session, segmentsExperience, groupId, plid, orderByComparator,
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

	protected SegmentsExperience getByG_P_PrevAndNext(
		Session session, SegmentsExperience segmentsExperience, long groupId,
		long plid, OrderByComparator<SegmentsExperience> orderByComparator,
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

		sb.append(_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_PLID_2);

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
			sb.append(SegmentsExperienceModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(plid);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						segmentsExperience)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<SegmentsExperience> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the segments experiences that the user has permission to view where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @return the matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByG_P(long groupId, long plid) {
		return filterFindByG_P(
			groupId, plid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the segments experiences that the user has permission to view where groupId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByG_P(
		long groupId, long plid, int start, int end) {

		return filterFindByG_P(groupId, plid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the segments experiences that the user has permissions to view where groupId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByG_P(
		long groupId, long plid, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P(groupId, plid, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P(
					groupId, plid, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_PLID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_NO_INLINE_DISTINCT_WHERE_2);
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
					SegmentsExperienceModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(SegmentsExperienceModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), SegmentsExperience.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, SegmentsExperienceImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, SegmentsExperienceImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(plid);

			return (List<SegmentsExperience>)QueryUtil.list(
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
	 * Returns the segments experiences before and after the current segments experience in the ordered set of segments experiences that the user has permission to view where groupId = &#63; and plid = &#63;.
	 *
	 * @param segmentsExperienceId the primary key of the current segments experience
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	@Override
	public SegmentsExperience[] filterFindByG_P_PrevAndNext(
			long segmentsExperienceId, long groupId, long plid,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_PrevAndNext(
				segmentsExperienceId, groupId, plid, orderByComparator);
		}

		SegmentsExperience segmentsExperience = findByPrimaryKey(
			segmentsExperienceId);

		Session session = null;

		try {
			session = openSession();

			SegmentsExperience[] array = new SegmentsExperienceImpl[3];

			array[0] = filterGetByG_P_PrevAndNext(
				session, segmentsExperience, groupId, plid, orderByComparator,
				true);

			array[1] = segmentsExperience;

			array[2] = filterGetByG_P_PrevAndNext(
				session, segmentsExperience, groupId, plid, orderByComparator,
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

	protected SegmentsExperience filterGetByG_P_PrevAndNext(
		Session session, SegmentsExperience segmentsExperience, long groupId,
		long plid, OrderByComparator<SegmentsExperience> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_PLID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_NO_INLINE_DISTINCT_WHERE_2);
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
					SegmentsExperienceModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(SegmentsExperienceModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), SegmentsExperience.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, SegmentsExperienceImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, SegmentsExperienceImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(plid);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						segmentsExperience)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<SegmentsExperience> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the segments experiences where groupId = &#63; and plid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 */
	@Override
	public void removeByG_P(long groupId, long plid) {
		for (SegmentsExperience segmentsExperience :
				findByG_P(
					groupId, plid, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(segmentsExperience);
		}
	}

	/**
	 * Returns the number of segments experiences where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @return the number of matching segments experiences
	 */
	@Override
	public int countByG_P(long groupId, long plid) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SegmentsExperience.class)) {

			FinderPath finderPath = _finderPathCountByG_P;

			Object[] finderArgs = new Object[] {groupId, plid};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_SEGMENTSEXPERIENCE_WHERE);

				sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_PLID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(plid);

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
	 * Returns the number of segments experiences that the user has permission to view where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @return the number of matching segments experiences that the user has permission to view
	 */
	@Override
	public int filterCountByG_P(long groupId, long plid) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P(groupId, plid);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<SegmentsExperience> segmentsExperiences = findByG_P(
				groupId, plid);

			segmentsExperiences = InlineSQLHelperUtil.filter(
				segmentsExperiences, groupId);

			return segmentsExperiences.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_SEGMENTSEXPERIENCE_WHERE);

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_PLID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), SegmentsExperience.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(plid);

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
		"segmentsExperience.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_PLID_2 =
		"segmentsExperience.plid = ?";

	private FinderPath _finderPathWithPaginationFindByG_S_P;
	private FinderPath _finderPathWithoutPaginationFindByG_S_P;
	private FinderPath _finderPathCountByG_S_P;

	/**
	 * Returns all the segments experiences where groupId = &#63; and segmentsEntryId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryId the segments entry ID
	 * @param plid the plid
	 * @return the matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_S_P(
		long groupId, long segmentsEntryId, long plid) {

		return findByG_S_P(
			groupId, segmentsEntryId, plid, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the segments experiences where groupId = &#63; and segmentsEntryId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryId the segments entry ID
	 * @param plid the plid
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_S_P(
		long groupId, long segmentsEntryId, long plid, int start, int end) {

		return findByG_S_P(groupId, segmentsEntryId, plid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and segmentsEntryId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryId the segments entry ID
	 * @param plid the plid
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_S_P(
		long groupId, long segmentsEntryId, long plid, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return findByG_S_P(
			groupId, segmentsEntryId, plid, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and segmentsEntryId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryId the segments entry ID
	 * @param plid the plid
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_S_P(
		long groupId, long segmentsEntryId, long plid, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SegmentsExperience.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_S_P;
					finderArgs = new Object[] {groupId, segmentsEntryId, plid};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_S_P;
				finderArgs = new Object[] {
					groupId, segmentsEntryId, plid, start, end,
					orderByComparator
				};
			}

			List<SegmentsExperience> list = null;

			if (useFinderCache) {
				list = (List<SegmentsExperience>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (SegmentsExperience segmentsExperience : list) {
						if ((groupId != segmentsExperience.getGroupId()) ||
							(segmentsEntryId !=
								segmentsExperience.getSegmentsEntryId()) ||
							(plid != segmentsExperience.getPlid())) {

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

				sb.append(_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);

				sb.append(_FINDER_COLUMN_G_S_P_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_S_P_SEGMENTSENTRYID_2);

				sb.append(_FINDER_COLUMN_G_S_P_PLID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(SegmentsExperienceModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(segmentsEntryId);

					queryPos.add(plid);

					list = (List<SegmentsExperience>)QueryUtil.list(
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
	 * Returns the first segments experience in the ordered set where groupId = &#63; and segmentsEntryId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryId the segments entry ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findByG_S_P_First(
			long groupId, long segmentsEntryId, long plid,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = fetchByG_S_P_First(
			groupId, segmentsEntryId, plid, orderByComparator);

		if (segmentsExperience != null) {
			return segmentsExperience;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", segmentsEntryId=");
		sb.append(segmentsEntryId);

		sb.append(", plid=");
		sb.append(plid);

		sb.append("}");

		throw new NoSuchExperienceException(sb.toString());
	}

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and segmentsEntryId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryId the segments entry ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByG_S_P_First(
		long groupId, long segmentsEntryId, long plid,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		List<SegmentsExperience> list = findByG_S_P(
			groupId, segmentsEntryId, plid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last segments experience in the ordered set where groupId = &#63; and segmentsEntryId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryId the segments entry ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findByG_S_P_Last(
			long groupId, long segmentsEntryId, long plid,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = fetchByG_S_P_Last(
			groupId, segmentsEntryId, plid, orderByComparator);

		if (segmentsExperience != null) {
			return segmentsExperience;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", segmentsEntryId=");
		sb.append(segmentsEntryId);

		sb.append(", plid=");
		sb.append(plid);

		sb.append("}");

		throw new NoSuchExperienceException(sb.toString());
	}

	/**
	 * Returns the last segments experience in the ordered set where groupId = &#63; and segmentsEntryId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryId the segments entry ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByG_S_P_Last(
		long groupId, long segmentsEntryId, long plid,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		int count = countByG_S_P(groupId, segmentsEntryId, plid);

		if (count == 0) {
			return null;
		}

		List<SegmentsExperience> list = findByG_S_P(
			groupId, segmentsEntryId, plid, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the segments experiences before and after the current segments experience in the ordered set where groupId = &#63; and segmentsEntryId = &#63; and plid = &#63;.
	 *
	 * @param segmentsExperienceId the primary key of the current segments experience
	 * @param groupId the group ID
	 * @param segmentsEntryId the segments entry ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	@Override
	public SegmentsExperience[] findByG_S_P_PrevAndNext(
			long segmentsExperienceId, long groupId, long segmentsEntryId,
			long plid, OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = findByPrimaryKey(
			segmentsExperienceId);

		Session session = null;

		try {
			session = openSession();

			SegmentsExperience[] array = new SegmentsExperienceImpl[3];

			array[0] = getByG_S_P_PrevAndNext(
				session, segmentsExperience, groupId, segmentsEntryId, plid,
				orderByComparator, true);

			array[1] = segmentsExperience;

			array[2] = getByG_S_P_PrevAndNext(
				session, segmentsExperience, groupId, segmentsEntryId, plid,
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

	protected SegmentsExperience getByG_S_P_PrevAndNext(
		Session session, SegmentsExperience segmentsExperience, long groupId,
		long segmentsEntryId, long plid,
		OrderByComparator<SegmentsExperience> orderByComparator,
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

		sb.append(_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);

		sb.append(_FINDER_COLUMN_G_S_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_S_P_SEGMENTSENTRYID_2);

		sb.append(_FINDER_COLUMN_G_S_P_PLID_2);

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
			sb.append(SegmentsExperienceModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(segmentsEntryId);

		queryPos.add(plid);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						segmentsExperience)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<SegmentsExperience> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryId the segments entry ID
	 * @param plid the plid
	 * @return the matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByG_S_P(
		long groupId, long segmentsEntryId, long plid) {

		return filterFindByG_S_P(
			groupId, segmentsEntryId, plid, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryId the segments entry ID
	 * @param plid the plid
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByG_S_P(
		long groupId, long segmentsEntryId, long plid, int start, int end) {

		return filterFindByG_S_P(
			groupId, segmentsEntryId, plid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the segments experiences that the user has permissions to view where groupId = &#63; and segmentsEntryId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryId the segments entry ID
	 * @param plid the plid
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByG_S_P(
		long groupId, long segmentsEntryId, long plid, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_S_P(
				groupId, segmentsEntryId, plid, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_S_P(
					groupId, segmentsEntryId, plid, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
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
			sb.append(_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_S_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_S_P_SEGMENTSENTRYID_2);

		sb.append(_FINDER_COLUMN_G_S_P_PLID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_NO_INLINE_DISTINCT_WHERE_2);
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
					SegmentsExperienceModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(SegmentsExperienceModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), SegmentsExperience.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, SegmentsExperienceImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, SegmentsExperienceImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(segmentsEntryId);

			queryPos.add(plid);

			return (List<SegmentsExperience>)QueryUtil.list(
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
	 * Returns the segments experiences before and after the current segments experience in the ordered set of segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryId = &#63; and plid = &#63;.
	 *
	 * @param segmentsExperienceId the primary key of the current segments experience
	 * @param groupId the group ID
	 * @param segmentsEntryId the segments entry ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	@Override
	public SegmentsExperience[] filterFindByG_S_P_PrevAndNext(
			long segmentsExperienceId, long groupId, long segmentsEntryId,
			long plid, OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_S_P_PrevAndNext(
				segmentsExperienceId, groupId, segmentsEntryId, plid,
				orderByComparator);
		}

		SegmentsExperience segmentsExperience = findByPrimaryKey(
			segmentsExperienceId);

		Session session = null;

		try {
			session = openSession();

			SegmentsExperience[] array = new SegmentsExperienceImpl[3];

			array[0] = filterGetByG_S_P_PrevAndNext(
				session, segmentsExperience, groupId, segmentsEntryId, plid,
				orderByComparator, true);

			array[1] = segmentsExperience;

			array[2] = filterGetByG_S_P_PrevAndNext(
				session, segmentsExperience, groupId, segmentsEntryId, plid,
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

	protected SegmentsExperience filterGetByG_S_P_PrevAndNext(
		Session session, SegmentsExperience segmentsExperience, long groupId,
		long segmentsEntryId, long plid,
		OrderByComparator<SegmentsExperience> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_S_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_S_P_SEGMENTSENTRYID_2);

		sb.append(_FINDER_COLUMN_G_S_P_PLID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_NO_INLINE_DISTINCT_WHERE_2);
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
					SegmentsExperienceModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(SegmentsExperienceModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), SegmentsExperience.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, SegmentsExperienceImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, SegmentsExperienceImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(segmentsEntryId);

		queryPos.add(plid);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						segmentsExperience)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<SegmentsExperience> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the segments experiences where groupId = &#63; and segmentsEntryId = &#63; and plid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryId the segments entry ID
	 * @param plid the plid
	 */
	@Override
	public void removeByG_S_P(long groupId, long segmentsEntryId, long plid) {
		for (SegmentsExperience segmentsExperience :
				findByG_S_P(
					groupId, segmentsEntryId, plid, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(segmentsExperience);
		}
	}

	/**
	 * Returns the number of segments experiences where groupId = &#63; and segmentsEntryId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryId the segments entry ID
	 * @param plid the plid
	 * @return the number of matching segments experiences
	 */
	@Override
	public int countByG_S_P(long groupId, long segmentsEntryId, long plid) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SegmentsExperience.class)) {

			FinderPath finderPath = _finderPathCountByG_S_P;

			Object[] finderArgs = new Object[] {groupId, segmentsEntryId, plid};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_SEGMENTSEXPERIENCE_WHERE);

				sb.append(_FINDER_COLUMN_G_S_P_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_S_P_SEGMENTSENTRYID_2);

				sb.append(_FINDER_COLUMN_G_S_P_PLID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(segmentsEntryId);

					queryPos.add(plid);

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
	 * Returns the number of segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryId the segments entry ID
	 * @param plid the plid
	 * @return the number of matching segments experiences that the user has permission to view
	 */
	@Override
	public int filterCountByG_S_P(
		long groupId, long segmentsEntryId, long plid) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_S_P(groupId, segmentsEntryId, plid);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<SegmentsExperience> segmentsExperiences = findByG_S_P(
				groupId, segmentsEntryId, plid);

			segmentsExperiences = InlineSQLHelperUtil.filter(
				segmentsExperiences, groupId);

			return segmentsExperiences.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_SEGMENTSEXPERIENCE_WHERE);

		sb.append(_FINDER_COLUMN_G_S_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_S_P_SEGMENTSENTRYID_2);

		sb.append(_FINDER_COLUMN_G_S_P_PLID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), SegmentsExperience.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(segmentsEntryId);

			queryPos.add(plid);

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

	private static final String _FINDER_COLUMN_G_S_P_GROUPID_2 =
		"segmentsExperience.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_S_P_SEGMENTSENTRYID_2 =
		"segmentsExperience.segmentsEntryId = ? AND ";

	private static final String _FINDER_COLUMN_G_S_P_PLID_2 =
		"segmentsExperience.plid = ?";

	private FinderPath _finderPathFetchByG_SEK_P;

	/**
	 * Returns the segments experience where groupId = &#63; and segmentsExperienceKey = &#63; and plid = &#63; or throws a <code>NoSuchExperienceException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceKey the segments experience key
	 * @param plid the plid
	 * @return the matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findByG_SEK_P(
			long groupId, String segmentsExperienceKey, long plid)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = fetchByG_SEK_P(
			groupId, segmentsExperienceKey, plid);

		if (segmentsExperience == null) {
			StringBundler sb = new StringBundler(8);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("groupId=");
			sb.append(groupId);

			sb.append(", segmentsExperienceKey=");
			sb.append(segmentsExperienceKey);

			sb.append(", plid=");
			sb.append(plid);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchExperienceException(sb.toString());
		}

		return segmentsExperience;
	}

	/**
	 * Returns the segments experience where groupId = &#63; and segmentsExperienceKey = &#63; and plid = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceKey the segments experience key
	 * @param plid the plid
	 * @return the matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByG_SEK_P(
		long groupId, String segmentsExperienceKey, long plid) {

		return fetchByG_SEK_P(groupId, segmentsExperienceKey, plid, true);
	}

	/**
	 * Returns the segments experience where groupId = &#63; and segmentsExperienceKey = &#63; and plid = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceKey the segments experience key
	 * @param plid the plid
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByG_SEK_P(
		long groupId, String segmentsExperienceKey, long plid,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SegmentsExperience.class)) {

			segmentsExperienceKey = Objects.toString(segmentsExperienceKey, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {
					groupId, segmentsExperienceKey, plid
				};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByG_SEK_P, finderArgs, this);
			}

			if (result instanceof SegmentsExperience) {
				SegmentsExperience segmentsExperience =
					(SegmentsExperience)result;

				if ((groupId != segmentsExperience.getGroupId()) ||
					!Objects.equals(
						segmentsExperienceKey,
						segmentsExperience.getSegmentsExperienceKey()) ||
					(plid != segmentsExperience.getPlid())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);

				sb.append(_FINDER_COLUMN_G_SEK_P_GROUPID_2);

				boolean bindSegmentsExperienceKey = false;

				if (segmentsExperienceKey.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_SEK_P_SEGMENTSEXPERIENCEKEY_3);
				}
				else {
					bindSegmentsExperienceKey = true;

					sb.append(_FINDER_COLUMN_G_SEK_P_SEGMENTSEXPERIENCEKEY_2);
				}

				sb.append(_FINDER_COLUMN_G_SEK_P_PLID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					if (bindSegmentsExperienceKey) {
						queryPos.add(segmentsExperienceKey);
					}

					queryPos.add(plid);

					List<SegmentsExperience> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByG_SEK_P, finderArgs, list);
						}
					}
					else {
						SegmentsExperience segmentsExperience = list.get(0);

						result = segmentsExperience;

						cacheResult(segmentsExperience);
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
				return (SegmentsExperience)result;
			}
		}
	}

	/**
	 * Removes the segments experience where groupId = &#63; and segmentsExperienceKey = &#63; and plid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceKey the segments experience key
	 * @param plid the plid
	 * @return the segments experience that was removed
	 */
	@Override
	public SegmentsExperience removeByG_SEK_P(
			long groupId, String segmentsExperienceKey, long plid)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = findByG_SEK_P(
			groupId, segmentsExperienceKey, plid);

		return remove(segmentsExperience);
	}

	/**
	 * Returns the number of segments experiences where groupId = &#63; and segmentsExperienceKey = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceKey the segments experience key
	 * @param plid the plid
	 * @return the number of matching segments experiences
	 */
	@Override
	public int countByG_SEK_P(
		long groupId, String segmentsExperienceKey, long plid) {

		SegmentsExperience segmentsExperience = fetchByG_SEK_P(
			groupId, segmentsExperienceKey, plid);

		if (segmentsExperience == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_G_SEK_P_GROUPID_2 =
		"segmentsExperience.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_SEK_P_SEGMENTSEXPERIENCEKEY_2 =
		"segmentsExperience.segmentsExperienceKey = ? AND ";

	private static final String _FINDER_COLUMN_G_SEK_P_SEGMENTSEXPERIENCEKEY_3 =
		"(segmentsExperience.segmentsExperienceKey IS NULL OR segmentsExperience.segmentsExperienceKey = '') AND ";

	private static final String _FINDER_COLUMN_G_SEK_P_PLID_2 =
		"segmentsExperience.plid = ?";

	private FinderPath _finderPathFetchByG_P_P;

	/**
	 * Returns the segments experience where groupId = &#63; and plid = &#63; and priority = &#63; or throws a <code>NoSuchExperienceException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findByG_P_P(long groupId, long plid, int priority)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = fetchByG_P_P(
			groupId, plid, priority);

		if (segmentsExperience == null) {
			StringBundler sb = new StringBundler(8);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("groupId=");
			sb.append(groupId);

			sb.append(", plid=");
			sb.append(plid);

			sb.append(", priority=");
			sb.append(priority);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchExperienceException(sb.toString());
		}

		return segmentsExperience;
	}

	/**
	 * Returns the segments experience where groupId = &#63; and plid = &#63; and priority = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByG_P_P(
		long groupId, long plid, int priority) {

		return fetchByG_P_P(groupId, plid, priority, true);
	}

	/**
	 * Returns the segments experience where groupId = &#63; and plid = &#63; and priority = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByG_P_P(
		long groupId, long plid, int priority, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SegmentsExperience.class)) {

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {groupId, plid, priority};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByG_P_P, finderArgs, this);
			}

			if (result instanceof SegmentsExperience) {
				SegmentsExperience segmentsExperience =
					(SegmentsExperience)result;

				if ((groupId != segmentsExperience.getGroupId()) ||
					(plid != segmentsExperience.getPlid()) ||
					(priority != segmentsExperience.getPriority())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);

				sb.append(_FINDER_COLUMN_G_P_P_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_P_PLID_2);

				sb.append(_FINDER_COLUMN_G_P_P_PRIORITY_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(plid);

					queryPos.add(priority);

					List<SegmentsExperience> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByG_P_P, finderArgs, list);
						}
					}
					else {
						SegmentsExperience segmentsExperience = list.get(0);

						result = segmentsExperience;

						cacheResult(segmentsExperience);
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
				return (SegmentsExperience)result;
			}
		}
	}

	/**
	 * Removes the segments experience where groupId = &#63; and plid = &#63; and priority = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the segments experience that was removed
	 */
	@Override
	public SegmentsExperience removeByG_P_P(
			long groupId, long plid, int priority)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = findByG_P_P(
			groupId, plid, priority);

		return remove(segmentsExperience);
	}

	/**
	 * Returns the number of segments experiences where groupId = &#63; and plid = &#63; and priority = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the number of matching segments experiences
	 */
	@Override
	public int countByG_P_P(long groupId, long plid, int priority) {
		SegmentsExperience segmentsExperience = fetchByG_P_P(
			groupId, plid, priority);

		if (segmentsExperience == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_G_P_P_GROUPID_2 =
		"segmentsExperience.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_P_PLID_2 =
		"segmentsExperience.plid = ? AND ";

	private static final String _FINDER_COLUMN_G_P_P_PRIORITY_2 =
		"segmentsExperience.priority = ?";

	private FinderPath _finderPathWithPaginationFindByG_P_GtP;
	private FinderPath _finderPathWithPaginationCountByG_P_GtP;

	/**
	 * Returns all the segments experiences where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_P_GtP(
		long groupId, long plid, int priority) {

		return findByG_P_GtP(
			groupId, plid, priority, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the segments experiences where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_P_GtP(
		long groupId, long plid, int priority, int start, int end) {

		return findByG_P_GtP(groupId, plid, priority, start, end, null);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_P_GtP(
		long groupId, long plid, int priority, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return findByG_P_GtP(
			groupId, plid, priority, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_P_GtP(
		long groupId, long plid, int priority, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SegmentsExperience.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByG_P_GtP;
			finderArgs = new Object[] {
				groupId, plid, priority, start, end, orderByComparator
			};

			List<SegmentsExperience> list = null;

			if (useFinderCache) {
				list = (List<SegmentsExperience>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (SegmentsExperience segmentsExperience : list) {
						if ((groupId != segmentsExperience.getGroupId()) ||
							(plid != segmentsExperience.getPlid()) ||
							(priority >= segmentsExperience.getPriority())) {

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

				sb.append(_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);

				sb.append(_FINDER_COLUMN_G_P_GTP_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_GTP_PLID_2);

				sb.append(_FINDER_COLUMN_G_P_GTP_PRIORITY_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(SegmentsExperienceModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(plid);

					queryPos.add(priority);

					list = (List<SegmentsExperience>)QueryUtil.list(
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
	 * Returns the first segments experience in the ordered set where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findByG_P_GtP_First(
			long groupId, long plid, int priority,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = fetchByG_P_GtP_First(
			groupId, plid, priority, orderByComparator);

		if (segmentsExperience != null) {
			return segmentsExperience;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", plid=");
		sb.append(plid);

		sb.append(", priority>");
		sb.append(priority);

		sb.append("}");

		throw new NoSuchExperienceException(sb.toString());
	}

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByG_P_GtP_First(
		long groupId, long plid, int priority,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		List<SegmentsExperience> list = findByG_P_GtP(
			groupId, plid, priority, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last segments experience in the ordered set where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findByG_P_GtP_Last(
			long groupId, long plid, int priority,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = fetchByG_P_GtP_Last(
			groupId, plid, priority, orderByComparator);

		if (segmentsExperience != null) {
			return segmentsExperience;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", plid=");
		sb.append(plid);

		sb.append(", priority>");
		sb.append(priority);

		sb.append("}");

		throw new NoSuchExperienceException(sb.toString());
	}

	/**
	 * Returns the last segments experience in the ordered set where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByG_P_GtP_Last(
		long groupId, long plid, int priority,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		int count = countByG_P_GtP(groupId, plid, priority);

		if (count == 0) {
			return null;
		}

		List<SegmentsExperience> list = findByG_P_GtP(
			groupId, plid, priority, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the segments experiences before and after the current segments experience in the ordered set where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * @param segmentsExperienceId the primary key of the current segments experience
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	@Override
	public SegmentsExperience[] findByG_P_GtP_PrevAndNext(
			long segmentsExperienceId, long groupId, long plid, int priority,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = findByPrimaryKey(
			segmentsExperienceId);

		Session session = null;

		try {
			session = openSession();

			SegmentsExperience[] array = new SegmentsExperienceImpl[3];

			array[0] = getByG_P_GtP_PrevAndNext(
				session, segmentsExperience, groupId, plid, priority,
				orderByComparator, true);

			array[1] = segmentsExperience;

			array[2] = getByG_P_GtP_PrevAndNext(
				session, segmentsExperience, groupId, plid, priority,
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

	protected SegmentsExperience getByG_P_GtP_PrevAndNext(
		Session session, SegmentsExperience segmentsExperience, long groupId,
		long plid, int priority,
		OrderByComparator<SegmentsExperience> orderByComparator,
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

		sb.append(_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);

		sb.append(_FINDER_COLUMN_G_P_GTP_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_GTP_PLID_2);

		sb.append(_FINDER_COLUMN_G_P_GTP_PRIORITY_2);

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
			sb.append(SegmentsExperienceModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(plid);

		queryPos.add(priority);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						segmentsExperience)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<SegmentsExperience> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByG_P_GtP(
		long groupId, long plid, int priority) {

		return filterFindByG_P_GtP(
			groupId, plid, priority, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByG_P_GtP(
		long groupId, long plid, int priority, int start, int end) {

		return filterFindByG_P_GtP(groupId, plid, priority, start, end, null);
	}

	/**
	 * Returns an ordered range of all the segments experiences that the user has permissions to view where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByG_P_GtP(
		long groupId, long plid, int priority, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_GtP(
				groupId, plid, priority, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P_GtP(
					groupId, plid, priority, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
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
			sb.append(_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_GTP_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_GTP_PLID_2);

		sb.append(_FINDER_COLUMN_G_P_GTP_PRIORITY_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_NO_INLINE_DISTINCT_WHERE_2);
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
					SegmentsExperienceModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(SegmentsExperienceModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), SegmentsExperience.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, SegmentsExperienceImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, SegmentsExperienceImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(plid);

			queryPos.add(priority);

			return (List<SegmentsExperience>)QueryUtil.list(
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
	 * Returns the segments experiences before and after the current segments experience in the ordered set of segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * @param segmentsExperienceId the primary key of the current segments experience
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	@Override
	public SegmentsExperience[] filterFindByG_P_GtP_PrevAndNext(
			long segmentsExperienceId, long groupId, long plid, int priority,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_GtP_PrevAndNext(
				segmentsExperienceId, groupId, plid, priority,
				orderByComparator);
		}

		SegmentsExperience segmentsExperience = findByPrimaryKey(
			segmentsExperienceId);

		Session session = null;

		try {
			session = openSession();

			SegmentsExperience[] array = new SegmentsExperienceImpl[3];

			array[0] = filterGetByG_P_GtP_PrevAndNext(
				session, segmentsExperience, groupId, plid, priority,
				orderByComparator, true);

			array[1] = segmentsExperience;

			array[2] = filterGetByG_P_GtP_PrevAndNext(
				session, segmentsExperience, groupId, plid, priority,
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

	protected SegmentsExperience filterGetByG_P_GtP_PrevAndNext(
		Session session, SegmentsExperience segmentsExperience, long groupId,
		long plid, int priority,
		OrderByComparator<SegmentsExperience> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_GTP_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_GTP_PLID_2);

		sb.append(_FINDER_COLUMN_G_P_GTP_PRIORITY_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_NO_INLINE_DISTINCT_WHERE_2);
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
					SegmentsExperienceModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(SegmentsExperienceModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), SegmentsExperience.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, SegmentsExperienceImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, SegmentsExperienceImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(plid);

		queryPos.add(priority);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						segmentsExperience)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<SegmentsExperience> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the segments experiences where groupId = &#63; and plid = &#63; and priority &gt; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 */
	@Override
	public void removeByG_P_GtP(long groupId, long plid, int priority) {
		for (SegmentsExperience segmentsExperience :
				findByG_P_GtP(
					groupId, plid, priority, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(segmentsExperience);
		}
	}

	/**
	 * Returns the number of segments experiences where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the number of matching segments experiences
	 */
	@Override
	public int countByG_P_GtP(long groupId, long plid, int priority) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SegmentsExperience.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByG_P_GtP;

			Object[] finderArgs = new Object[] {groupId, plid, priority};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_SEGMENTSEXPERIENCE_WHERE);

				sb.append(_FINDER_COLUMN_G_P_GTP_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_GTP_PLID_2);

				sb.append(_FINDER_COLUMN_G_P_GTP_PRIORITY_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(plid);

					queryPos.add(priority);

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
	 * Returns the number of segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the number of matching segments experiences that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_GtP(long groupId, long plid, int priority) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P_GtP(groupId, plid, priority);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<SegmentsExperience> segmentsExperiences = findByG_P_GtP(
				groupId, plid, priority);

			segmentsExperiences = InlineSQLHelperUtil.filter(
				segmentsExperiences, groupId);

			return segmentsExperiences.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_SEGMENTSEXPERIENCE_WHERE);

		sb.append(_FINDER_COLUMN_G_P_GTP_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_GTP_PLID_2);

		sb.append(_FINDER_COLUMN_G_P_GTP_PRIORITY_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), SegmentsExperience.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(plid);

			queryPos.add(priority);

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

	private static final String _FINDER_COLUMN_G_P_GTP_GROUPID_2 =
		"segmentsExperience.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_GTP_PLID_2 =
		"segmentsExperience.plid = ? AND ";

	private static final String _FINDER_COLUMN_G_P_GTP_PRIORITY_2 =
		"segmentsExperience.priority > ?";

	private FinderPath _finderPathWithPaginationFindByG_P_LtP;
	private FinderPath _finderPathWithPaginationCountByG_P_LtP;

	/**
	 * Returns all the segments experiences where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_P_LtP(
		long groupId, long plid, int priority) {

		return findByG_P_LtP(
			groupId, plid, priority, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the segments experiences where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_P_LtP(
		long groupId, long plid, int priority, int start, int end) {

		return findByG_P_LtP(groupId, plid, priority, start, end, null);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_P_LtP(
		long groupId, long plid, int priority, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return findByG_P_LtP(
			groupId, plid, priority, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_P_LtP(
		long groupId, long plid, int priority, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SegmentsExperience.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByG_P_LtP;
			finderArgs = new Object[] {
				groupId, plid, priority, start, end, orderByComparator
			};

			List<SegmentsExperience> list = null;

			if (useFinderCache) {
				list = (List<SegmentsExperience>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (SegmentsExperience segmentsExperience : list) {
						if ((groupId != segmentsExperience.getGroupId()) ||
							(plid != segmentsExperience.getPlid()) ||
							(priority <= segmentsExperience.getPriority())) {

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

				sb.append(_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);

				sb.append(_FINDER_COLUMN_G_P_LTP_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_LTP_PLID_2);

				sb.append(_FINDER_COLUMN_G_P_LTP_PRIORITY_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(SegmentsExperienceModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(plid);

					queryPos.add(priority);

					list = (List<SegmentsExperience>)QueryUtil.list(
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
	 * Returns the first segments experience in the ordered set where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findByG_P_LtP_First(
			long groupId, long plid, int priority,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = fetchByG_P_LtP_First(
			groupId, plid, priority, orderByComparator);

		if (segmentsExperience != null) {
			return segmentsExperience;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", plid=");
		sb.append(plid);

		sb.append(", priority<");
		sb.append(priority);

		sb.append("}");

		throw new NoSuchExperienceException(sb.toString());
	}

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByG_P_LtP_First(
		long groupId, long plid, int priority,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		List<SegmentsExperience> list = findByG_P_LtP(
			groupId, plid, priority, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last segments experience in the ordered set where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findByG_P_LtP_Last(
			long groupId, long plid, int priority,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = fetchByG_P_LtP_Last(
			groupId, plid, priority, orderByComparator);

		if (segmentsExperience != null) {
			return segmentsExperience;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", plid=");
		sb.append(plid);

		sb.append(", priority<");
		sb.append(priority);

		sb.append("}");

		throw new NoSuchExperienceException(sb.toString());
	}

	/**
	 * Returns the last segments experience in the ordered set where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByG_P_LtP_Last(
		long groupId, long plid, int priority,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		int count = countByG_P_LtP(groupId, plid, priority);

		if (count == 0) {
			return null;
		}

		List<SegmentsExperience> list = findByG_P_LtP(
			groupId, plid, priority, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the segments experiences before and after the current segments experience in the ordered set where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * @param segmentsExperienceId the primary key of the current segments experience
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	@Override
	public SegmentsExperience[] findByG_P_LtP_PrevAndNext(
			long segmentsExperienceId, long groupId, long plid, int priority,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = findByPrimaryKey(
			segmentsExperienceId);

		Session session = null;

		try {
			session = openSession();

			SegmentsExperience[] array = new SegmentsExperienceImpl[3];

			array[0] = getByG_P_LtP_PrevAndNext(
				session, segmentsExperience, groupId, plid, priority,
				orderByComparator, true);

			array[1] = segmentsExperience;

			array[2] = getByG_P_LtP_PrevAndNext(
				session, segmentsExperience, groupId, plid, priority,
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

	protected SegmentsExperience getByG_P_LtP_PrevAndNext(
		Session session, SegmentsExperience segmentsExperience, long groupId,
		long plid, int priority,
		OrderByComparator<SegmentsExperience> orderByComparator,
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

		sb.append(_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);

		sb.append(_FINDER_COLUMN_G_P_LTP_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_LTP_PLID_2);

		sb.append(_FINDER_COLUMN_G_P_LTP_PRIORITY_2);

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
			sb.append(SegmentsExperienceModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(plid);

		queryPos.add(priority);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						segmentsExperience)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<SegmentsExperience> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByG_P_LtP(
		long groupId, long plid, int priority) {

		return filterFindByG_P_LtP(
			groupId, plid, priority, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByG_P_LtP(
		long groupId, long plid, int priority, int start, int end) {

		return filterFindByG_P_LtP(groupId, plid, priority, start, end, null);
	}

	/**
	 * Returns an ordered range of all the segments experiences that the user has permissions to view where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByG_P_LtP(
		long groupId, long plid, int priority, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_LtP(
				groupId, plid, priority, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P_LtP(
					groupId, plid, priority, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
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
			sb.append(_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_LTP_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_LTP_PLID_2);

		sb.append(_FINDER_COLUMN_G_P_LTP_PRIORITY_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_NO_INLINE_DISTINCT_WHERE_2);
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
					SegmentsExperienceModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(SegmentsExperienceModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), SegmentsExperience.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, SegmentsExperienceImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, SegmentsExperienceImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(plid);

			queryPos.add(priority);

			return (List<SegmentsExperience>)QueryUtil.list(
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
	 * Returns the segments experiences before and after the current segments experience in the ordered set of segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * @param segmentsExperienceId the primary key of the current segments experience
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	@Override
	public SegmentsExperience[] filterFindByG_P_LtP_PrevAndNext(
			long segmentsExperienceId, long groupId, long plid, int priority,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_LtP_PrevAndNext(
				segmentsExperienceId, groupId, plid, priority,
				orderByComparator);
		}

		SegmentsExperience segmentsExperience = findByPrimaryKey(
			segmentsExperienceId);

		Session session = null;

		try {
			session = openSession();

			SegmentsExperience[] array = new SegmentsExperienceImpl[3];

			array[0] = filterGetByG_P_LtP_PrevAndNext(
				session, segmentsExperience, groupId, plid, priority,
				orderByComparator, true);

			array[1] = segmentsExperience;

			array[2] = filterGetByG_P_LtP_PrevAndNext(
				session, segmentsExperience, groupId, plid, priority,
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

	protected SegmentsExperience filterGetByG_P_LtP_PrevAndNext(
		Session session, SegmentsExperience segmentsExperience, long groupId,
		long plid, int priority,
		OrderByComparator<SegmentsExperience> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_LTP_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_LTP_PLID_2);

		sb.append(_FINDER_COLUMN_G_P_LTP_PRIORITY_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_NO_INLINE_DISTINCT_WHERE_2);
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
					SegmentsExperienceModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(SegmentsExperienceModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), SegmentsExperience.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, SegmentsExperienceImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, SegmentsExperienceImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(plid);

		queryPos.add(priority);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						segmentsExperience)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<SegmentsExperience> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the segments experiences where groupId = &#63; and plid = &#63; and priority &lt; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 */
	@Override
	public void removeByG_P_LtP(long groupId, long plid, int priority) {
		for (SegmentsExperience segmentsExperience :
				findByG_P_LtP(
					groupId, plid, priority, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(segmentsExperience);
		}
	}

	/**
	 * Returns the number of segments experiences where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the number of matching segments experiences
	 */
	@Override
	public int countByG_P_LtP(long groupId, long plid, int priority) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SegmentsExperience.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByG_P_LtP;

			Object[] finderArgs = new Object[] {groupId, plid, priority};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_SEGMENTSEXPERIENCE_WHERE);

				sb.append(_FINDER_COLUMN_G_P_LTP_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_LTP_PLID_2);

				sb.append(_FINDER_COLUMN_G_P_LTP_PRIORITY_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(plid);

					queryPos.add(priority);

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
	 * Returns the number of segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the number of matching segments experiences that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_LtP(long groupId, long plid, int priority) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P_LtP(groupId, plid, priority);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<SegmentsExperience> segmentsExperiences = findByG_P_LtP(
				groupId, plid, priority);

			segmentsExperiences = InlineSQLHelperUtil.filter(
				segmentsExperiences, groupId);

			return segmentsExperiences.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_SEGMENTSEXPERIENCE_WHERE);

		sb.append(_FINDER_COLUMN_G_P_LTP_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_LTP_PLID_2);

		sb.append(_FINDER_COLUMN_G_P_LTP_PRIORITY_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), SegmentsExperience.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(plid);

			queryPos.add(priority);

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

	private static final String _FINDER_COLUMN_G_P_LTP_GROUPID_2 =
		"segmentsExperience.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_LTP_PLID_2 =
		"segmentsExperience.plid = ? AND ";

	private static final String _FINDER_COLUMN_G_P_LTP_PRIORITY_2 =
		"segmentsExperience.priority < ?";

	private FinderPath _finderPathWithPaginationFindByG_P_A;
	private FinderPath _finderPathWithoutPaginationFindByG_P_A;
	private FinderPath _finderPathCountByG_P_A;

	/**
	 * Returns all the segments experiences where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @return the matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_P_A(
		long groupId, long plid, boolean active) {

		return findByG_P_A(
			groupId, plid, active, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the segments experiences where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_P_A(
		long groupId, long plid, boolean active, int start, int end) {

		return findByG_P_A(groupId, plid, active, start, end, null);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_P_A(
		long groupId, long plid, boolean active, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return findByG_P_A(
			groupId, plid, active, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_P_A(
		long groupId, long plid, boolean active, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SegmentsExperience.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_P_A;
					finderArgs = new Object[] {groupId, plid, active};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_P_A;
				finderArgs = new Object[] {
					groupId, plid, active, start, end, orderByComparator
				};
			}

			List<SegmentsExperience> list = null;

			if (useFinderCache) {
				list = (List<SegmentsExperience>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (SegmentsExperience segmentsExperience : list) {
						if ((groupId != segmentsExperience.getGroupId()) ||
							(plid != segmentsExperience.getPlid()) ||
							(active != segmentsExperience.isActive())) {

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

				sb.append(_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);

				sb.append(_FINDER_COLUMN_G_P_A_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_A_PLID_2);

				sb.append(_FINDER_COLUMN_G_P_A_ACTIVE_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(SegmentsExperienceModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(plid);

					queryPos.add(active);

					list = (List<SegmentsExperience>)QueryUtil.list(
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
	 * Returns the first segments experience in the ordered set where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findByG_P_A_First(
			long groupId, long plid, boolean active,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = fetchByG_P_A_First(
			groupId, plid, active, orderByComparator);

		if (segmentsExperience != null) {
			return segmentsExperience;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", plid=");
		sb.append(plid);

		sb.append(", active=");
		sb.append(active);

		sb.append("}");

		throw new NoSuchExperienceException(sb.toString());
	}

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByG_P_A_First(
		long groupId, long plid, boolean active,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		List<SegmentsExperience> list = findByG_P_A(
			groupId, plid, active, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last segments experience in the ordered set where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findByG_P_A_Last(
			long groupId, long plid, boolean active,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = fetchByG_P_A_Last(
			groupId, plid, active, orderByComparator);

		if (segmentsExperience != null) {
			return segmentsExperience;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", plid=");
		sb.append(plid);

		sb.append(", active=");
		sb.append(active);

		sb.append("}");

		throw new NoSuchExperienceException(sb.toString());
	}

	/**
	 * Returns the last segments experience in the ordered set where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByG_P_A_Last(
		long groupId, long plid, boolean active,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		int count = countByG_P_A(groupId, plid, active);

		if (count == 0) {
			return null;
		}

		List<SegmentsExperience> list = findByG_P_A(
			groupId, plid, active, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the segments experiences before and after the current segments experience in the ordered set where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param segmentsExperienceId the primary key of the current segments experience
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	@Override
	public SegmentsExperience[] findByG_P_A_PrevAndNext(
			long segmentsExperienceId, long groupId, long plid, boolean active,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = findByPrimaryKey(
			segmentsExperienceId);

		Session session = null;

		try {
			session = openSession();

			SegmentsExperience[] array = new SegmentsExperienceImpl[3];

			array[0] = getByG_P_A_PrevAndNext(
				session, segmentsExperience, groupId, plid, active,
				orderByComparator, true);

			array[1] = segmentsExperience;

			array[2] = getByG_P_A_PrevAndNext(
				session, segmentsExperience, groupId, plid, active,
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

	protected SegmentsExperience getByG_P_A_PrevAndNext(
		Session session, SegmentsExperience segmentsExperience, long groupId,
		long plid, boolean active,
		OrderByComparator<SegmentsExperience> orderByComparator,
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

		sb.append(_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);

		sb.append(_FINDER_COLUMN_G_P_A_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_A_PLID_2);

		sb.append(_FINDER_COLUMN_G_P_A_ACTIVE_2);

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
			sb.append(SegmentsExperienceModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(plid);

		queryPos.add(active);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						segmentsExperience)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<SegmentsExperience> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @return the matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByG_P_A(
		long groupId, long plid, boolean active) {

		return filterFindByG_P_A(
			groupId, plid, active, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByG_P_A(
		long groupId, long plid, boolean active, int start, int end) {

		return filterFindByG_P_A(groupId, plid, active, start, end, null);
	}

	/**
	 * Returns an ordered range of all the segments experiences that the user has permissions to view where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByG_P_A(
		long groupId, long plid, boolean active, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_A(
				groupId, plid, active, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P_A(
					groupId, plid, active, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator),
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
			sb.append(_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_A_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_A_PLID_2);

		sb.append(_FINDER_COLUMN_G_P_A_ACTIVE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_NO_INLINE_DISTINCT_WHERE_2);
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
					SegmentsExperienceModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(SegmentsExperienceModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), SegmentsExperience.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, SegmentsExperienceImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, SegmentsExperienceImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(plid);

			queryPos.add(active);

			return (List<SegmentsExperience>)QueryUtil.list(
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
	 * Returns the segments experiences before and after the current segments experience in the ordered set of segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param segmentsExperienceId the primary key of the current segments experience
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	@Override
	public SegmentsExperience[] filterFindByG_P_A_PrevAndNext(
			long segmentsExperienceId, long groupId, long plid, boolean active,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_A_PrevAndNext(
				segmentsExperienceId, groupId, plid, active, orderByComparator);
		}

		SegmentsExperience segmentsExperience = findByPrimaryKey(
			segmentsExperienceId);

		Session session = null;

		try {
			session = openSession();

			SegmentsExperience[] array = new SegmentsExperienceImpl[3];

			array[0] = filterGetByG_P_A_PrevAndNext(
				session, segmentsExperience, groupId, plid, active,
				orderByComparator, true);

			array[1] = segmentsExperience;

			array[2] = filterGetByG_P_A_PrevAndNext(
				session, segmentsExperience, groupId, plid, active,
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

	protected SegmentsExperience filterGetByG_P_A_PrevAndNext(
		Session session, SegmentsExperience segmentsExperience, long groupId,
		long plid, boolean active,
		OrderByComparator<SegmentsExperience> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_A_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_A_PLID_2);

		sb.append(_FINDER_COLUMN_G_P_A_ACTIVE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_NO_INLINE_DISTINCT_WHERE_2);
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
					SegmentsExperienceModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(SegmentsExperienceModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), SegmentsExperience.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, SegmentsExperienceImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, SegmentsExperienceImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(plid);

		queryPos.add(active);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						segmentsExperience)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<SegmentsExperience> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the segments experiences where groupId = &#63; and plid = &#63; and active = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 */
	@Override
	public void removeByG_P_A(long groupId, long plid, boolean active) {
		for (SegmentsExperience segmentsExperience :
				findByG_P_A(
					groupId, plid, active, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(segmentsExperience);
		}
	}

	/**
	 * Returns the number of segments experiences where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @return the number of matching segments experiences
	 */
	@Override
	public int countByG_P_A(long groupId, long plid, boolean active) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SegmentsExperience.class)) {

			FinderPath finderPath = _finderPathCountByG_P_A;

			Object[] finderArgs = new Object[] {groupId, plid, active};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_SEGMENTSEXPERIENCE_WHERE);

				sb.append(_FINDER_COLUMN_G_P_A_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_A_PLID_2);

				sb.append(_FINDER_COLUMN_G_P_A_ACTIVE_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(plid);

					queryPos.add(active);

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
	 * Returns the number of segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @return the number of matching segments experiences that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_A(long groupId, long plid, boolean active) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P_A(groupId, plid, active);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<SegmentsExperience> segmentsExperiences = findByG_P_A(
				groupId, plid, active);

			segmentsExperiences = InlineSQLHelperUtil.filter(
				segmentsExperiences, groupId);

			return segmentsExperiences.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_SEGMENTSEXPERIENCE_WHERE);

		sb.append(_FINDER_COLUMN_G_P_A_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_A_PLID_2);

		sb.append(_FINDER_COLUMN_G_P_A_ACTIVE_2_SQL);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), SegmentsExperience.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(plid);

			queryPos.add(active);

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

	private static final String _FINDER_COLUMN_G_P_A_GROUPID_2 =
		"segmentsExperience.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_A_PLID_2 =
		"segmentsExperience.plid = ? AND ";

	private static final String _FINDER_COLUMN_G_P_A_ACTIVE_2 =
		"segmentsExperience.active = ?";

	private static final String _FINDER_COLUMN_G_P_A_ACTIVE_2_SQL =
		"segmentsExperience.active_ = ?";

	private FinderPath _finderPathWithPaginationFindByG_S_P_A;
	private FinderPath _finderPathWithoutPaginationFindByG_S_P_A;
	private FinderPath _finderPathCountByG_S_P_A;
	private FinderPath _finderPathWithPaginationCountByG_S_P_A;

	/**
	 * Returns all the segments experiences where groupId = &#63; and segmentsEntryId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryId the segments entry ID
	 * @param plid the plid
	 * @param active the active
	 * @return the matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_S_P_A(
		long groupId, long segmentsEntryId, long plid, boolean active) {

		return findByG_S_P_A(
			groupId, segmentsEntryId, plid, active, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the segments experiences where groupId = &#63; and segmentsEntryId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryId the segments entry ID
	 * @param plid the plid
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_S_P_A(
		long groupId, long segmentsEntryId, long plid, boolean active,
		int start, int end) {

		return findByG_S_P_A(
			groupId, segmentsEntryId, plid, active, start, end, null);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and segmentsEntryId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryId the segments entry ID
	 * @param plid the plid
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_S_P_A(
		long groupId, long segmentsEntryId, long plid, boolean active,
		int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return findByG_S_P_A(
			groupId, segmentsEntryId, plid, active, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and segmentsEntryId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryId the segments entry ID
	 * @param plid the plid
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_S_P_A(
		long groupId, long segmentsEntryId, long plid, boolean active,
		int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SegmentsExperience.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_S_P_A;
					finderArgs = new Object[] {
						groupId, segmentsEntryId, plid, active
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_S_P_A;
				finderArgs = new Object[] {
					groupId, segmentsEntryId, plid, active, start, end,
					orderByComparator
				};
			}

			List<SegmentsExperience> list = null;

			if (useFinderCache) {
				list = (List<SegmentsExperience>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (SegmentsExperience segmentsExperience : list) {
						if ((groupId != segmentsExperience.getGroupId()) ||
							(segmentsEntryId !=
								segmentsExperience.getSegmentsEntryId()) ||
							(plid != segmentsExperience.getPlid()) ||
							(active != segmentsExperience.isActive())) {

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

				sb.append(_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);

				sb.append(_FINDER_COLUMN_G_S_P_A_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_S_P_A_SEGMENTSENTRYID_2);

				sb.append(_FINDER_COLUMN_G_S_P_A_PLID_2);

				sb.append(_FINDER_COLUMN_G_S_P_A_ACTIVE_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(SegmentsExperienceModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(segmentsEntryId);

					queryPos.add(plid);

					queryPos.add(active);

					list = (List<SegmentsExperience>)QueryUtil.list(
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
	 * Returns the first segments experience in the ordered set where groupId = &#63; and segmentsEntryId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryId the segments entry ID
	 * @param plid the plid
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findByG_S_P_A_First(
			long groupId, long segmentsEntryId, long plid, boolean active,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = fetchByG_S_P_A_First(
			groupId, segmentsEntryId, plid, active, orderByComparator);

		if (segmentsExperience != null) {
			return segmentsExperience;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", segmentsEntryId=");
		sb.append(segmentsEntryId);

		sb.append(", plid=");
		sb.append(plid);

		sb.append(", active=");
		sb.append(active);

		sb.append("}");

		throw new NoSuchExperienceException(sb.toString());
	}

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and segmentsEntryId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryId the segments entry ID
	 * @param plid the plid
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByG_S_P_A_First(
		long groupId, long segmentsEntryId, long plid, boolean active,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		List<SegmentsExperience> list = findByG_S_P_A(
			groupId, segmentsEntryId, plid, active, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last segments experience in the ordered set where groupId = &#63; and segmentsEntryId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryId the segments entry ID
	 * @param plid the plid
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findByG_S_P_A_Last(
			long groupId, long segmentsEntryId, long plid, boolean active,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = fetchByG_S_P_A_Last(
			groupId, segmentsEntryId, plid, active, orderByComparator);

		if (segmentsExperience != null) {
			return segmentsExperience;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", segmentsEntryId=");
		sb.append(segmentsEntryId);

		sb.append(", plid=");
		sb.append(plid);

		sb.append(", active=");
		sb.append(active);

		sb.append("}");

		throw new NoSuchExperienceException(sb.toString());
	}

	/**
	 * Returns the last segments experience in the ordered set where groupId = &#63; and segmentsEntryId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryId the segments entry ID
	 * @param plid the plid
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByG_S_P_A_Last(
		long groupId, long segmentsEntryId, long plid, boolean active,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		int count = countByG_S_P_A(groupId, segmentsEntryId, plid, active);

		if (count == 0) {
			return null;
		}

		List<SegmentsExperience> list = findByG_S_P_A(
			groupId, segmentsEntryId, plid, active, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the segments experiences before and after the current segments experience in the ordered set where groupId = &#63; and segmentsEntryId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param segmentsExperienceId the primary key of the current segments experience
	 * @param groupId the group ID
	 * @param segmentsEntryId the segments entry ID
	 * @param plid the plid
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	@Override
	public SegmentsExperience[] findByG_S_P_A_PrevAndNext(
			long segmentsExperienceId, long groupId, long segmentsEntryId,
			long plid, boolean active,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = findByPrimaryKey(
			segmentsExperienceId);

		Session session = null;

		try {
			session = openSession();

			SegmentsExperience[] array = new SegmentsExperienceImpl[3];

			array[0] = getByG_S_P_A_PrevAndNext(
				session, segmentsExperience, groupId, segmentsEntryId, plid,
				active, orderByComparator, true);

			array[1] = segmentsExperience;

			array[2] = getByG_S_P_A_PrevAndNext(
				session, segmentsExperience, groupId, segmentsEntryId, plid,
				active, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected SegmentsExperience getByG_S_P_A_PrevAndNext(
		Session session, SegmentsExperience segmentsExperience, long groupId,
		long segmentsEntryId, long plid, boolean active,
		OrderByComparator<SegmentsExperience> orderByComparator,
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

		sb.append(_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);

		sb.append(_FINDER_COLUMN_G_S_P_A_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_S_P_A_SEGMENTSENTRYID_2);

		sb.append(_FINDER_COLUMN_G_S_P_A_PLID_2);

		sb.append(_FINDER_COLUMN_G_S_P_A_ACTIVE_2);

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
			sb.append(SegmentsExperienceModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(segmentsEntryId);

		queryPos.add(plid);

		queryPos.add(active);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						segmentsExperience)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<SegmentsExperience> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryId the segments entry ID
	 * @param plid the plid
	 * @param active the active
	 * @return the matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByG_S_P_A(
		long groupId, long segmentsEntryId, long plid, boolean active) {

		return filterFindByG_S_P_A(
			groupId, segmentsEntryId, plid, active, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryId the segments entry ID
	 * @param plid the plid
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByG_S_P_A(
		long groupId, long segmentsEntryId, long plid, boolean active,
		int start, int end) {

		return filterFindByG_S_P_A(
			groupId, segmentsEntryId, plid, active, start, end, null);
	}

	/**
	 * Returns an ordered range of all the segments experiences that the user has permissions to view where groupId = &#63; and segmentsEntryId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryId the segments entry ID
	 * @param plid the plid
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByG_S_P_A(
		long groupId, long segmentsEntryId, long plid, boolean active,
		int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_S_P_A(
				groupId, segmentsEntryId, plid, active, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_S_P_A(
					groupId, segmentsEntryId, plid, active, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(7);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_S_P_A_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_S_P_A_SEGMENTSENTRYID_2);

		sb.append(_FINDER_COLUMN_G_S_P_A_PLID_2);

		sb.append(_FINDER_COLUMN_G_S_P_A_ACTIVE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_NO_INLINE_DISTINCT_WHERE_2);
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
					SegmentsExperienceModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(SegmentsExperienceModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), SegmentsExperience.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, SegmentsExperienceImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, SegmentsExperienceImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(segmentsEntryId);

			queryPos.add(plid);

			queryPos.add(active);

			return (List<SegmentsExperience>)QueryUtil.list(
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
	 * Returns the segments experiences before and after the current segments experience in the ordered set of segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param segmentsExperienceId the primary key of the current segments experience
	 * @param groupId the group ID
	 * @param segmentsEntryId the segments entry ID
	 * @param plid the plid
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	@Override
	public SegmentsExperience[] filterFindByG_S_P_A_PrevAndNext(
			long segmentsExperienceId, long groupId, long segmentsEntryId,
			long plid, boolean active,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws NoSuchExperienceException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_S_P_A_PrevAndNext(
				segmentsExperienceId, groupId, segmentsEntryId, plid, active,
				orderByComparator);
		}

		SegmentsExperience segmentsExperience = findByPrimaryKey(
			segmentsExperienceId);

		Session session = null;

		try {
			session = openSession();

			SegmentsExperience[] array = new SegmentsExperienceImpl[3];

			array[0] = filterGetByG_S_P_A_PrevAndNext(
				session, segmentsExperience, groupId, segmentsEntryId, plid,
				active, orderByComparator, true);

			array[1] = segmentsExperience;

			array[2] = filterGetByG_S_P_A_PrevAndNext(
				session, segmentsExperience, groupId, segmentsEntryId, plid,
				active, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected SegmentsExperience filterGetByG_S_P_A_PrevAndNext(
		Session session, SegmentsExperience segmentsExperience, long groupId,
		long segmentsEntryId, long plid, boolean active,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_S_P_A_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_S_P_A_SEGMENTSENTRYID_2);

		sb.append(_FINDER_COLUMN_G_S_P_A_PLID_2);

		sb.append(_FINDER_COLUMN_G_S_P_A_ACTIVE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_NO_INLINE_DISTINCT_WHERE_2);
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
					SegmentsExperienceModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(SegmentsExperienceModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), SegmentsExperience.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, SegmentsExperienceImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, SegmentsExperienceImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(segmentsEntryId);

		queryPos.add(plid);

		queryPos.add(active);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						segmentsExperience)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<SegmentsExperience> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryId = any &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryIds the segments entry IDs
	 * @param plid the plid
	 * @param active the active
	 * @return the matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByG_S_P_A(
		long groupId, long[] segmentsEntryIds, long plid, boolean active) {

		return filterFindByG_S_P_A(
			groupId, segmentsEntryIds, plid, active, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryId = any &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryIds the segments entry IDs
	 * @param plid the plid
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByG_S_P_A(
		long groupId, long[] segmentsEntryIds, long plid, boolean active,
		int start, int end) {

		return filterFindByG_S_P_A(
			groupId, segmentsEntryIds, plid, active, start, end, null);
	}

	/**
	 * Returns an ordered range of all the segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryId = any &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryIds the segments entry IDs
	 * @param plid the plid
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences that the user has permission to view
	 */
	@Override
	public List<SegmentsExperience> filterFindByG_S_P_A(
		long groupId, long[] segmentsEntryIds, long plid, boolean active,
		int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_S_P_A(
				groupId, segmentsEntryIds, plid, active, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_S_P_A(
					groupId, segmentsEntryIds, plid, active, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		if (segmentsEntryIds == null) {
			segmentsEntryIds = new long[0];
		}
		else if (segmentsEntryIds.length > 1) {
			segmentsEntryIds = ArrayUtil.sortedUnique(segmentsEntryIds);
		}

		StringBundler sb = new StringBundler();

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_S_P_A_GROUPID_2);

		if (segmentsEntryIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_S_P_A_SEGMENTSENTRYID_7);

			sb.append(StringUtil.merge(segmentsEntryIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_G_S_P_A_PLID_2);

		sb.append(_FINDER_COLUMN_G_S_P_A_ACTIVE_2_SQL);

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_NO_INLINE_DISTINCT_WHERE_2);
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
					SegmentsExperienceModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(SegmentsExperienceModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), SegmentsExperience.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, SegmentsExperienceImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, SegmentsExperienceImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(plid);

			queryPos.add(active);

			return (List<SegmentsExperience>)QueryUtil.list(
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
	 * Returns all the segments experiences where groupId = &#63; and segmentsEntryId = any &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryIds the segments entry IDs
	 * @param plid the plid
	 * @param active the active
	 * @return the matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_S_P_A(
		long groupId, long[] segmentsEntryIds, long plid, boolean active) {

		return findByG_S_P_A(
			groupId, segmentsEntryIds, plid, active, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the segments experiences where groupId = &#63; and segmentsEntryId = any &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryIds the segments entry IDs
	 * @param plid the plid
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_S_P_A(
		long groupId, long[] segmentsEntryIds, long plid, boolean active,
		int start, int end) {

		return findByG_S_P_A(
			groupId, segmentsEntryIds, plid, active, start, end, null);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and segmentsEntryId = any &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryIds the segments entry IDs
	 * @param plid the plid
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_S_P_A(
		long groupId, long[] segmentsEntryIds, long plid, boolean active,
		int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return findByG_S_P_A(
			groupId, segmentsEntryIds, plid, active, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and segmentsEntryId = &#63; and plid = &#63; and active = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryIds the segments entry IDs
	 * @param plid the plid
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	@Override
	public List<SegmentsExperience> findByG_S_P_A(
		long groupId, long[] segmentsEntryIds, long plid, boolean active,
		int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		if (segmentsEntryIds == null) {
			segmentsEntryIds = new long[0];
		}
		else if (segmentsEntryIds.length > 1) {
			segmentsEntryIds = ArrayUtil.sortedUnique(segmentsEntryIds);
		}

		if (segmentsEntryIds.length == 1) {
			return findByG_S_P_A(
				groupId, segmentsEntryIds[0], plid, active, start, end,
				orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SegmentsExperience.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						groupId, StringUtil.merge(segmentsEntryIds), plid,
						active
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					groupId, StringUtil.merge(segmentsEntryIds), plid, active,
					start, end, orderByComparator
				};
			}

			List<SegmentsExperience> list = null;

			if (useFinderCache) {
				list = (List<SegmentsExperience>)finderCache.getResult(
					_finderPathWithPaginationFindByG_S_P_A, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (SegmentsExperience segmentsExperience : list) {
						if ((groupId != segmentsExperience.getGroupId()) ||
							!ArrayUtil.contains(
								segmentsEntryIds,
								segmentsExperience.getSegmentsEntryId()) ||
							(plid != segmentsExperience.getPlid()) ||
							(active != segmentsExperience.isActive())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);

				sb.append(_FINDER_COLUMN_G_S_P_A_GROUPID_2);

				if (segmentsEntryIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_S_P_A_SEGMENTSENTRYID_7);

					sb.append(StringUtil.merge(segmentsEntryIds));

					sb.append(")");

					sb.append(")");

					sb.append(WHERE_AND);
				}

				sb.append(_FINDER_COLUMN_G_S_P_A_PLID_2);

				sb.append(_FINDER_COLUMN_G_S_P_A_ACTIVE_2);

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(SegmentsExperienceModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(plid);

					queryPos.add(active);

					list = (List<SegmentsExperience>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(
							_finderPathWithPaginationFindByG_S_P_A, finderArgs,
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
	 * Removes all the segments experiences where groupId = &#63; and segmentsEntryId = &#63; and plid = &#63; and active = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryId the segments entry ID
	 * @param plid the plid
	 * @param active the active
	 */
	@Override
	public void removeByG_S_P_A(
		long groupId, long segmentsEntryId, long plid, boolean active) {

		for (SegmentsExperience segmentsExperience :
				findByG_S_P_A(
					groupId, segmentsEntryId, plid, active, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(segmentsExperience);
		}
	}

	/**
	 * Returns the number of segments experiences where groupId = &#63; and segmentsEntryId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryId the segments entry ID
	 * @param plid the plid
	 * @param active the active
	 * @return the number of matching segments experiences
	 */
	@Override
	public int countByG_S_P_A(
		long groupId, long segmentsEntryId, long plid, boolean active) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SegmentsExperience.class)) {

			FinderPath finderPath = _finderPathCountByG_S_P_A;

			Object[] finderArgs = new Object[] {
				groupId, segmentsEntryId, plid, active
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_SEGMENTSEXPERIENCE_WHERE);

				sb.append(_FINDER_COLUMN_G_S_P_A_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_S_P_A_SEGMENTSENTRYID_2);

				sb.append(_FINDER_COLUMN_G_S_P_A_PLID_2);

				sb.append(_FINDER_COLUMN_G_S_P_A_ACTIVE_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(segmentsEntryId);

					queryPos.add(plid);

					queryPos.add(active);

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
	 * Returns the number of segments experiences where groupId = &#63; and segmentsEntryId = any &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryIds the segments entry IDs
	 * @param plid the plid
	 * @param active the active
	 * @return the number of matching segments experiences
	 */
	@Override
	public int countByG_S_P_A(
		long groupId, long[] segmentsEntryIds, long plid, boolean active) {

		if (segmentsEntryIds == null) {
			segmentsEntryIds = new long[0];
		}
		else if (segmentsEntryIds.length > 1) {
			segmentsEntryIds = ArrayUtil.sortedUnique(segmentsEntryIds);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SegmentsExperience.class)) {

			Object[] finderArgs = new Object[] {
				groupId, StringUtil.merge(segmentsEntryIds), plid, active
			};

			Long count = (Long)finderCache.getResult(
				_finderPathWithPaginationCountByG_S_P_A, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_SEGMENTSEXPERIENCE_WHERE);

				sb.append(_FINDER_COLUMN_G_S_P_A_GROUPID_2);

				if (segmentsEntryIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_S_P_A_SEGMENTSENTRYID_7);

					sb.append(StringUtil.merge(segmentsEntryIds));

					sb.append(")");

					sb.append(")");

					sb.append(WHERE_AND);
				}

				sb.append(_FINDER_COLUMN_G_S_P_A_PLID_2);

				sb.append(_FINDER_COLUMN_G_S_P_A_ACTIVE_2);

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(plid);

					queryPos.add(active);

					count = (Long)query.uniqueResult();

					finderCache.putResult(
						_finderPathWithPaginationCountByG_S_P_A, finderArgs,
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
	 * Returns the number of segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryId the segments entry ID
	 * @param plid the plid
	 * @param active the active
	 * @return the number of matching segments experiences that the user has permission to view
	 */
	@Override
	public int filterCountByG_S_P_A(
		long groupId, long segmentsEntryId, long plid, boolean active) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_S_P_A(groupId, segmentsEntryId, plid, active);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<SegmentsExperience> segmentsExperiences = findByG_S_P_A(
				groupId, segmentsEntryId, plid, active);

			segmentsExperiences = InlineSQLHelperUtil.filter(
				segmentsExperiences, groupId);

			return segmentsExperiences.size();
		}

		StringBundler sb = new StringBundler(5);

		sb.append(_FILTER_SQL_COUNT_SEGMENTSEXPERIENCE_WHERE);

		sb.append(_FINDER_COLUMN_G_S_P_A_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_S_P_A_SEGMENTSENTRYID_2);

		sb.append(_FINDER_COLUMN_G_S_P_A_PLID_2);

		sb.append(_FINDER_COLUMN_G_S_P_A_ACTIVE_2_SQL);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), SegmentsExperience.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(segmentsEntryId);

			queryPos.add(plid);

			queryPos.add(active);

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
	 * Returns the number of segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryId = any &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryIds the segments entry IDs
	 * @param plid the plid
	 * @param active the active
	 * @return the number of matching segments experiences that the user has permission to view
	 */
	@Override
	public int filterCountByG_S_P_A(
		long groupId, long[] segmentsEntryIds, long plid, boolean active) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_S_P_A(groupId, segmentsEntryIds, plid, active);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<SegmentsExperience> segmentsExperiences =
				InlineSQLHelperUtil.filter(
					findByG_S_P_A(groupId, segmentsEntryIds, plid, active),
					groupId);

			return segmentsExperiences.size();
		}

		if (segmentsEntryIds == null) {
			segmentsEntryIds = new long[0];
		}
		else if (segmentsEntryIds.length > 1) {
			segmentsEntryIds = ArrayUtil.sortedUnique(segmentsEntryIds);
		}

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_SEGMENTSEXPERIENCE_WHERE);

		sb.append(_FINDER_COLUMN_G_S_P_A_GROUPID_2);

		if (segmentsEntryIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_S_P_A_SEGMENTSENTRYID_7);

			sb.append(StringUtil.merge(segmentsEntryIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_G_S_P_A_PLID_2);

		sb.append(_FINDER_COLUMN_G_S_P_A_ACTIVE_2_SQL);

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), SegmentsExperience.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(plid);

			queryPos.add(active);

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

	private static final String _FINDER_COLUMN_G_S_P_A_GROUPID_2 =
		"segmentsExperience.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_S_P_A_SEGMENTSENTRYID_2 =
		"segmentsExperience.segmentsEntryId = ? AND ";

	private static final String _FINDER_COLUMN_G_S_P_A_SEGMENTSENTRYID_7 =
		"segmentsExperience.segmentsEntryId IN (";

	private static final String _FINDER_COLUMN_G_S_P_A_PLID_2 =
		"segmentsExperience.plid = ? AND ";

	private static final String _FINDER_COLUMN_G_S_P_A_ACTIVE_2 =
		"segmentsExperience.active = ?";

	private static final String _FINDER_COLUMN_G_S_P_A_ACTIVE_2_SQL =
		"segmentsExperience.active_ = ?";

	private FinderPath _finderPathFetchByERC_G;

	/**
	 * Returns the segments experience where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchExperienceException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience findByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = fetchByERC_G(
			externalReferenceCode, groupId);

		if (segmentsExperience == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("externalReferenceCode=");
			sb.append(externalReferenceCode);

			sb.append(", groupId=");
			sb.append(groupId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchExperienceException(sb.toString());
		}

		return segmentsExperience;
	}

	/**
	 * Returns the segments experience where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return fetchByERC_G(externalReferenceCode, groupId, true);
	}

	/**
	 * Returns the segments experience where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Override
	public SegmentsExperience fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SegmentsExperience.class)) {

			externalReferenceCode = Objects.toString(externalReferenceCode, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {externalReferenceCode, groupId};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByERC_G, finderArgs, this);
			}

			if (result instanceof SegmentsExperience) {
				SegmentsExperience segmentsExperience =
					(SegmentsExperience)result;

				if (!Objects.equals(
						externalReferenceCode,
						segmentsExperience.getExternalReferenceCode()) ||
					(groupId != segmentsExperience.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE);

				boolean bindExternalReferenceCode = false;

				if (externalReferenceCode.isEmpty()) {
					sb.append(_FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_3);
				}
				else {
					bindExternalReferenceCode = true;

					sb.append(_FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_2);
				}

				sb.append(_FINDER_COLUMN_ERC_G_GROUPID_2);

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

					List<SegmentsExperience> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByERC_G, finderArgs, list);
						}
					}
					else {
						SegmentsExperience segmentsExperience = list.get(0);

						result = segmentsExperience;

						cacheResult(segmentsExperience);
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
				return (SegmentsExperience)result;
			}
		}
	}

	/**
	 * Removes the segments experience where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the segments experience that was removed
	 */
	@Override
	public SegmentsExperience removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = findByERC_G(
			externalReferenceCode, groupId);

		return remove(segmentsExperience);
	}

	/**
	 * Returns the number of segments experiences where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching segments experiences
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		SegmentsExperience segmentsExperience = fetchByERC_G(
			externalReferenceCode, groupId);

		if (segmentsExperience == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_2 =
		"segmentsExperience.externalReferenceCode = ? AND ";

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_3 =
		"(segmentsExperience.externalReferenceCode IS NULL OR segmentsExperience.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_G_GROUPID_2 =
		"segmentsExperience.groupId = ?";

	public SegmentsExperiencePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("active", "active_");

		setDBColumnNames(dbColumnNames);

		setModelClass(SegmentsExperience.class);

		setModelImplClass(SegmentsExperienceImpl.class);
		setModelPKClass(long.class);

		setTable(SegmentsExperienceTable.INSTANCE);
	}

	/**
	 * Caches the segments experience in the entity cache if it is enabled.
	 *
	 * @param segmentsExperience the segments experience
	 */
	@Override
	public void cacheResult(SegmentsExperience segmentsExperience) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					segmentsExperience.getCtCollectionId())) {

			entityCache.putResult(
				SegmentsExperienceImpl.class,
				segmentsExperience.getPrimaryKey(), segmentsExperience);

			finderCache.putResult(
				_finderPathFetchByUUID_G,
				new Object[] {
					segmentsExperience.getUuid(),
					segmentsExperience.getGroupId()
				},
				segmentsExperience);

			finderCache.putResult(
				_finderPathFetchByG_SEK_P,
				new Object[] {
					segmentsExperience.getGroupId(),
					segmentsExperience.getSegmentsExperienceKey(),
					segmentsExperience.getPlid()
				},
				segmentsExperience);

			finderCache.putResult(
				_finderPathFetchByG_P_P,
				new Object[] {
					segmentsExperience.getGroupId(),
					segmentsExperience.getPlid(),
					segmentsExperience.getPriority()
				},
				segmentsExperience);

			finderCache.putResult(
				_finderPathFetchByERC_G,
				new Object[] {
					segmentsExperience.getExternalReferenceCode(),
					segmentsExperience.getGroupId()
				},
				segmentsExperience);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the segments experiences in the entity cache if it is enabled.
	 *
	 * @param segmentsExperiences the segments experiences
	 */
	@Override
	public void cacheResult(List<SegmentsExperience> segmentsExperiences) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (segmentsExperiences.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (SegmentsExperience segmentsExperience : segmentsExperiences) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						segmentsExperience.getCtCollectionId())) {

				if (entityCache.getResult(
						SegmentsExperienceImpl.class,
						segmentsExperience.getPrimaryKey()) == null) {

					cacheResult(segmentsExperience);
				}
			}
		}
	}

	/**
	 * Clears the cache for all segments experiences.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(SegmentsExperienceImpl.class);

		finderCache.clearCache(SegmentsExperienceImpl.class);
	}

	/**
	 * Clears the cache for the segments experience.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(SegmentsExperience segmentsExperience) {
		entityCache.removeResult(
			SegmentsExperienceImpl.class, segmentsExperience);
	}

	@Override
	public void clearCache(List<SegmentsExperience> segmentsExperiences) {
		for (SegmentsExperience segmentsExperience : segmentsExperiences) {
			entityCache.removeResult(
				SegmentsExperienceImpl.class, segmentsExperience);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(SegmentsExperienceImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(SegmentsExperienceImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		SegmentsExperienceModelImpl segmentsExperienceModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					segmentsExperienceModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				segmentsExperienceModelImpl.getUuid(),
				segmentsExperienceModelImpl.getGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByUUID_G, args, segmentsExperienceModelImpl);

			args = new Object[] {
				segmentsExperienceModelImpl.getGroupId(),
				segmentsExperienceModelImpl.getSegmentsExperienceKey(),
				segmentsExperienceModelImpl.getPlid()
			};

			finderCache.putResult(
				_finderPathFetchByG_SEK_P, args, segmentsExperienceModelImpl);

			args = new Object[] {
				segmentsExperienceModelImpl.getGroupId(),
				segmentsExperienceModelImpl.getPlid(),
				segmentsExperienceModelImpl.getPriority()
			};

			finderCache.putResult(
				_finderPathFetchByG_P_P, args, segmentsExperienceModelImpl);

			args = new Object[] {
				segmentsExperienceModelImpl.getExternalReferenceCode(),
				segmentsExperienceModelImpl.getGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByERC_G, args, segmentsExperienceModelImpl);
		}
	}

	/**
	 * Creates a new segments experience with the primary key. Does not add the segments experience to the database.
	 *
	 * @param segmentsExperienceId the primary key for the new segments experience
	 * @return the new segments experience
	 */
	@Override
	public SegmentsExperience create(long segmentsExperienceId) {
		SegmentsExperience segmentsExperience = new SegmentsExperienceImpl();

		segmentsExperience.setNew(true);
		segmentsExperience.setPrimaryKey(segmentsExperienceId);

		String uuid = PortalUUIDUtil.generate();

		segmentsExperience.setUuid(uuid);

		segmentsExperience.setCompanyId(CompanyThreadLocal.getCompanyId());

		return segmentsExperience;
	}

	/**
	 * Removes the segments experience with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param segmentsExperienceId the primary key of the segments experience
	 * @return the segments experience that was removed
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	@Override
	public SegmentsExperience remove(long segmentsExperienceId)
		throws NoSuchExperienceException {

		return remove((Serializable)segmentsExperienceId);
	}

	/**
	 * Removes the segments experience with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the segments experience
	 * @return the segments experience that was removed
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	@Override
	public SegmentsExperience remove(Serializable primaryKey)
		throws NoSuchExperienceException {

		Session session = null;

		try {
			session = openSession();

			SegmentsExperience segmentsExperience =
				(SegmentsExperience)session.get(
					SegmentsExperienceImpl.class, primaryKey);

			if (segmentsExperience == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchExperienceException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(segmentsExperience);
		}
		catch (NoSuchExperienceException noSuchEntityException) {
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
	protected SegmentsExperience removeImpl(
		SegmentsExperience segmentsExperience) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(segmentsExperience)) {
				segmentsExperience = (SegmentsExperience)session.get(
					SegmentsExperienceImpl.class,
					segmentsExperience.getPrimaryKeyObj());
			}

			if ((segmentsExperience != null) &&
				ctPersistenceHelper.isRemove(segmentsExperience)) {

				session.delete(segmentsExperience);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (segmentsExperience != null) {
			clearCache(segmentsExperience);
		}

		return segmentsExperience;
	}

	@Override
	public SegmentsExperience updateImpl(
		SegmentsExperience segmentsExperience) {

		boolean isNew = segmentsExperience.isNew();

		if (!(segmentsExperience instanceof SegmentsExperienceModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(segmentsExperience.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					segmentsExperience);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in segmentsExperience proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom SegmentsExperience implementation " +
					segmentsExperience.getClass());
		}

		SegmentsExperienceModelImpl segmentsExperienceModelImpl =
			(SegmentsExperienceModelImpl)segmentsExperience;

		if (Validator.isNull(segmentsExperience.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			segmentsExperience.setUuid(uuid);
		}

		if (Validator.isNull(segmentsExperience.getExternalReferenceCode())) {
			segmentsExperience.setExternalReferenceCode(
				segmentsExperience.getUuid());
		}
		else {
			if (!Objects.equals(
					segmentsExperienceModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					segmentsExperience.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = segmentsExperience.getCompanyId();

					long groupId = segmentsExperience.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = segmentsExperience.getPrimaryKey();
					}

					try {
						segmentsExperience.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								SegmentsExperience.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								segmentsExperience.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			SegmentsExperience ercSegmentsExperience = fetchByERC_G(
				segmentsExperience.getExternalReferenceCode(),
				segmentsExperience.getGroupId());

			if (isNew) {
				if (ercSegmentsExperience != null) {
					throw new DuplicateSegmentsExperienceExternalReferenceCodeException(
						"Duplicate segments experience with external reference code " +
							segmentsExperience.getExternalReferenceCode() +
								" and group " +
									segmentsExperience.getGroupId());
				}
			}
			else {
				if ((ercSegmentsExperience != null) &&
					(segmentsExperience.getSegmentsExperienceId() !=
						ercSegmentsExperience.getSegmentsExperienceId())) {

					throw new DuplicateSegmentsExperienceExternalReferenceCodeException(
						"Duplicate segments experience with external reference code " +
							segmentsExperience.getExternalReferenceCode() +
								" and group " +
									segmentsExperience.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (segmentsExperience.getCreateDate() == null)) {
			if (serviceContext == null) {
				segmentsExperience.setCreateDate(date);
			}
			else {
				segmentsExperience.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!segmentsExperienceModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				segmentsExperience.setModifiedDate(date);
			}
			else {
				segmentsExperience.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(segmentsExperience)) {
				if (!isNew) {
					session.evict(
						SegmentsExperienceImpl.class,
						segmentsExperience.getPrimaryKeyObj());
				}

				session.save(segmentsExperience);
			}
			else {
				segmentsExperience = (SegmentsExperience)session.merge(
					segmentsExperience);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			SegmentsExperienceImpl.class, segmentsExperienceModelImpl, false,
			true);

		cacheUniqueFindersCache(segmentsExperienceModelImpl);

		if (isNew) {
			segmentsExperience.setNew(false);
		}

		segmentsExperience.resetOriginalValues();

		return segmentsExperience;
	}

	/**
	 * Returns the segments experience with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the segments experience
	 * @return the segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	@Override
	public SegmentsExperience findByPrimaryKey(Serializable primaryKey)
		throws NoSuchExperienceException {

		SegmentsExperience segmentsExperience = fetchByPrimaryKey(primaryKey);

		if (segmentsExperience == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchExperienceException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return segmentsExperience;
	}

	/**
	 * Returns the segments experience with the primary key or throws a <code>NoSuchExperienceException</code> if it could not be found.
	 *
	 * @param segmentsExperienceId the primary key of the segments experience
	 * @return the segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	@Override
	public SegmentsExperience findByPrimaryKey(long segmentsExperienceId)
		throws NoSuchExperienceException {

		return findByPrimaryKey((Serializable)segmentsExperienceId);
	}

	/**
	 * Returns the segments experience with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the segments experience
	 * @return the segments experience, or <code>null</code> if a segments experience with the primary key could not be found
	 */
	@Override
	public SegmentsExperience fetchByPrimaryKey(Serializable primaryKey) {
		if (ctPersistenceHelper.isProductionMode(
				SegmentsExperience.class, primaryKey)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		SegmentsExperience segmentsExperience =
			(SegmentsExperience)entityCache.getResult(
				SegmentsExperienceImpl.class, primaryKey);

		if (segmentsExperience != null) {
			return segmentsExperience;
		}

		Session session = null;

		try {
			session = openSession();

			segmentsExperience = (SegmentsExperience)session.get(
				SegmentsExperienceImpl.class, primaryKey);

			if (segmentsExperience != null) {
				cacheResult(segmentsExperience);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return segmentsExperience;
	}

	/**
	 * Returns the segments experience with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param segmentsExperienceId the primary key of the segments experience
	 * @return the segments experience, or <code>null</code> if a segments experience with the primary key could not be found
	 */
	@Override
	public SegmentsExperience fetchByPrimaryKey(long segmentsExperienceId) {
		return fetchByPrimaryKey((Serializable)segmentsExperienceId);
	}

	@Override
	public Map<Serializable, SegmentsExperience> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (ctPersistenceHelper.isProductionMode(SegmentsExperience.class)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, SegmentsExperience> map =
			new HashMap<Serializable, SegmentsExperience>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			SegmentsExperience segmentsExperience = fetchByPrimaryKey(
				primaryKey);

			if (segmentsExperience != null) {
				map.put(primaryKey, segmentsExperience);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
						SegmentsExperience.class, primaryKey)) {

				SegmentsExperience segmentsExperience =
					(SegmentsExperience)entityCache.getResult(
						SegmentsExperienceImpl.class, primaryKey);

				if (segmentsExperience == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, segmentsExperience);
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

			for (SegmentsExperience segmentsExperience :
					(List<SegmentsExperience>)query.list()) {

				map.put(
					segmentsExperience.getPrimaryKeyObj(), segmentsExperience);

				cacheResult(segmentsExperience);
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
	 * Returns all the segments experiences.
	 *
	 * @return the segments experiences
	 */
	@Override
	public List<SegmentsExperience> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the segments experiences.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of segments experiences
	 */
	@Override
	public List<SegmentsExperience> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the segments experiences.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of segments experiences
	 */
	@Override
	public List<SegmentsExperience> findAll(
		int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the segments experiences.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of segments experiences
	 */
	@Override
	public List<SegmentsExperience> findAll(
		int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SegmentsExperience.class)) {

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

			List<SegmentsExperience> list = null;

			if (useFinderCache) {
				list = (List<SegmentsExperience>)finderCache.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_SEGMENTSEXPERIENCE);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_SEGMENTSEXPERIENCE;

					sql = sql.concat(SegmentsExperienceModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<SegmentsExperience>)QueryUtil.list(
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
	 * Removes all the segments experiences from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (SegmentsExperience segmentsExperience : findAll()) {
			remove(segmentsExperience);
		}
	}

	/**
	 * Returns the number of segments experiences.
	 *
	 * @return the number of segments experiences
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					SegmentsExperience.class)) {

			Long count = (Long)finderCache.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(
						_SQL_COUNT_SEGMENTSEXPERIENCE);

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
		return "segmentsExperienceId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_SEGMENTSEXPERIENCE;
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
		return SegmentsExperienceModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "SegmentsExperience";
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
		ctStrictColumnNames.add("externalReferenceCode");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("segmentsEntryId");
		ctMergeColumnNames.add("segmentsExperienceKey");
		ctMergeColumnNames.add("plid");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("priority");
		ctMergeColumnNames.add("active_");
		ctMergeColumnNames.add("typeSettings");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("segmentsExperienceId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "segmentsExperienceKey", "plid"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "plid", "priority"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the segments experience persistence.
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

		_finderPathWithPaginationFindBySegmentsEntryId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findBySegmentsEntryId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"segmentsEntryId"}, true);

		_finderPathWithoutPaginationFindBySegmentsEntryId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findBySegmentsEntryId",
			new String[] {Long.class.getName()},
			new String[] {"segmentsEntryId"}, true);

		_finderPathCountBySegmentsEntryId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countBySegmentsEntryId",
			new String[] {Long.class.getName()},
			new String[] {"segmentsEntryId"}, false);

		_finderPathWithPaginationFindByG_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "plid"}, true);

		_finderPathWithoutPaginationFindByG_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "plid"}, true);

		_finderPathCountByG_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "plid"}, false);

		_finderPathWithPaginationFindByG_S_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_S_P",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "segmentsEntryId", "plid"}, true);

		_finderPathWithoutPaginationFindByG_S_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_S_P",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"groupId", "segmentsEntryId", "plid"}, true);

		_finderPathCountByG_S_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_S_P",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"groupId", "segmentsEntryId", "plid"}, false);

		_finderPathFetchByG_SEK_P = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByG_SEK_P",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Long.class.getName()
			},
			new String[] {"groupId", "segmentsExperienceKey", "plid"}, true);

		_finderPathFetchByG_P_P = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByG_P_P",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "plid", "priority"}, true);

		_finderPathWithPaginationFindByG_P_GtP = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_GtP",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "plid", "priority"}, true);

		_finderPathWithPaginationCountByG_P_GtP = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_P_GtP",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "plid", "priority"}, false);

		_finderPathWithPaginationFindByG_P_LtP = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_LtP",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "plid", "priority"}, true);

		_finderPathWithPaginationCountByG_P_LtP = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_P_LtP",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "plid", "priority"}, false);

		_finderPathWithPaginationFindByG_P_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_A",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Boolean.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "plid", "active_"}, true);

		_finderPathWithoutPaginationFindByG_P_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P_A",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Boolean.class.getName()
			},
			new String[] {"groupId", "plid", "active_"}, true);

		_finderPathCountByG_P_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P_A",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Boolean.class.getName()
			},
			new String[] {"groupId", "plid", "active_"}, false);

		_finderPathWithPaginationFindByG_S_P_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_S_P_A",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "segmentsEntryId", "plid", "active_"},
			true);

		_finderPathWithoutPaginationFindByG_S_P_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_S_P_A",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Boolean.class.getName()
			},
			new String[] {"groupId", "segmentsEntryId", "plid", "active_"},
			true);

		_finderPathCountByG_S_P_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_S_P_A",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Boolean.class.getName()
			},
			new String[] {"groupId", "segmentsEntryId", "plid", "active_"},
			false);

		_finderPathWithPaginationCountByG_S_P_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_S_P_A",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Boolean.class.getName()
			},
			new String[] {"groupId", "segmentsEntryId", "plid", "active_"},
			false);

		_finderPathFetchByERC_G = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"externalReferenceCode", "groupId"}, true);

		SegmentsExperienceUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		SegmentsExperienceUtil.setPersistence(null);

		entityCache.removeCache(SegmentsExperienceImpl.class.getName());
	}

	@Override
	@Reference(
		target = SegmentsPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = SegmentsPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = SegmentsPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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

	private static final String _SQL_SELECT_SEGMENTSEXPERIENCE =
		"SELECT segmentsExperience FROM SegmentsExperience segmentsExperience";

	private static final String _SQL_SELECT_SEGMENTSEXPERIENCE_WHERE =
		"SELECT segmentsExperience FROM SegmentsExperience segmentsExperience WHERE ";

	private static final String _SQL_COUNT_SEGMENTSEXPERIENCE =
		"SELECT COUNT(segmentsExperience) FROM SegmentsExperience segmentsExperience";

	private static final String _SQL_COUNT_SEGMENTSEXPERIENCE_WHERE =
		"SELECT COUNT(segmentsExperience) FROM SegmentsExperience segmentsExperience WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"segmentsExperience.segmentsExperienceId";

	private static final String _FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_WHERE =
		"SELECT DISTINCT {segmentsExperience.*} FROM SegmentsExperience segmentsExperience WHERE ";

	private static final String
		_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {SegmentsExperience.*} FROM (SELECT DISTINCT segmentsExperience.segmentsExperienceId FROM SegmentsExperience segmentsExperience WHERE ";

	private static final String
		_FILTER_SQL_SELECT_SEGMENTSEXPERIENCE_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN SegmentsExperience ON TEMP_TABLE.segmentsExperienceId = SegmentsExperience.segmentsExperienceId";

	private static final String _FILTER_SQL_COUNT_SEGMENTSEXPERIENCE_WHERE =
		"SELECT COUNT(DISTINCT segmentsExperience.segmentsExperienceId) AS COUNT_VALUE FROM SegmentsExperience segmentsExperience WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "segmentsExperience";

	private static final String _FILTER_ENTITY_TABLE = "SegmentsExperience";

	private static final String _ORDER_BY_ENTITY_ALIAS = "segmentsExperience.";

	private static final String _ORDER_BY_ENTITY_TABLE = "SegmentsExperience.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No SegmentsExperience exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No SegmentsExperience exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		SegmentsExperiencePersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "active"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}