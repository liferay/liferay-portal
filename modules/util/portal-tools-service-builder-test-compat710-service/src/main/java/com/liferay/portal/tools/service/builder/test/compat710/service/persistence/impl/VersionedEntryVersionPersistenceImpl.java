/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.compat710.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.compat710.exception.NoSuchVersionedEntryVersionException;
import com.liferay.portal.tools.service.builder.test.compat710.model.VersionedEntryVersion;
import com.liferay.portal.tools.service.builder.test.compat710.model.impl.VersionedEntryVersionImpl;
import com.liferay.portal.tools.service.builder.test.compat710.model.impl.VersionedEntryVersionModelImpl;
import com.liferay.portal.tools.service.builder.test.compat710.service.persistence.VersionedEntryVersionPersistence;
import com.liferay.portal.tools.service.builder.test.compat710.service.persistence.VersionedEntryVersionUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the versioned entry version service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class VersionedEntryVersionPersistenceImpl
	extends BasePersistenceImpl<VersionedEntryVersion>
	implements VersionedEntryVersionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>VersionedEntryVersionUtil</code> to access the versioned entry version persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		VersionedEntryVersionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByVersionedEntryId;
	private FinderPath _finderPathWithoutPaginationFindByVersionedEntryId;
	private FinderPath _finderPathCountByVersionedEntryId;

	/**
	 * Returns all the versioned entry versions where versionedEntryId = &#63;.
	 *
	 * @param versionedEntryId the versioned entry ID
	 * @return the matching versioned entry versions
	 */
	@Override
	public List<VersionedEntryVersion> findByVersionedEntryId(
		long versionedEntryId) {

		return findByVersionedEntryId(
			versionedEntryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the versioned entry versions where versionedEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>VersionedEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param versionedEntryId the versioned entry ID
	 * @param start the lower bound of the range of versioned entry versions
	 * @param end the upper bound of the range of versioned entry versions (not inclusive)
	 * @return the range of matching versioned entry versions
	 */
	@Override
	public List<VersionedEntryVersion> findByVersionedEntryId(
		long versionedEntryId, int start, int end) {

		return findByVersionedEntryId(versionedEntryId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the versioned entry versions where versionedEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>VersionedEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param versionedEntryId the versioned entry ID
	 * @param start the lower bound of the range of versioned entry versions
	 * @param end the upper bound of the range of versioned entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching versioned entry versions
	 */
	@Override
	public List<VersionedEntryVersion> findByVersionedEntryId(
		long versionedEntryId, int start, int end,
		OrderByComparator<VersionedEntryVersion> orderByComparator) {

		return findByVersionedEntryId(
			versionedEntryId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the versioned entry versions where versionedEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>VersionedEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param versionedEntryId the versioned entry ID
	 * @param start the lower bound of the range of versioned entry versions
	 * @param end the upper bound of the range of versioned entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching versioned entry versions
	 */
	@Override
	public List<VersionedEntryVersion> findByVersionedEntryId(
		long versionedEntryId, int start, int end,
		OrderByComparator<VersionedEntryVersion> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByVersionedEntryId;
				finderArgs = new Object[] {versionedEntryId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByVersionedEntryId;
			finderArgs = new Object[] {
				versionedEntryId, start, end, orderByComparator
			};
		}

		List<VersionedEntryVersion> list = null;

		if (useFinderCache) {
			list = (List<VersionedEntryVersion>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (VersionedEntryVersion versionedEntryVersion : list) {
					if (versionedEntryId !=
							versionedEntryVersion.getVersionedEntryId()) {

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

			sb.append(_SQL_SELECT_VERSIONEDENTRYVERSION_WHERE);

			sb.append(_FINDER_COLUMN_VERSIONEDENTRYID_VERSIONEDENTRYID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator);
			}
			else {
				sb.append(VersionedEntryVersionModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(versionedEntryId);

				list = (List<VersionedEntryVersion>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				if (useFinderCache) {
					finderCache.removeResult(finderPath, finderArgs);
				}

				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Returns the first versioned entry version in the ordered set where versionedEntryId = &#63;.
	 *
	 * @param versionedEntryId the versioned entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching versioned entry version
	 * @throws NoSuchVersionedEntryVersionException if a matching versioned entry version could not be found
	 */
	@Override
	public VersionedEntryVersion findByVersionedEntryId_First(
			long versionedEntryId,
			OrderByComparator<VersionedEntryVersion> orderByComparator)
		throws NoSuchVersionedEntryVersionException {

		VersionedEntryVersion versionedEntryVersion =
			fetchByVersionedEntryId_First(versionedEntryId, orderByComparator);

		if (versionedEntryVersion != null) {
			return versionedEntryVersion;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("versionedEntryId=");
		sb.append(versionedEntryId);

		sb.append("}");

		throw new NoSuchVersionedEntryVersionException(sb.toString());
	}

	/**
	 * Returns the first versioned entry version in the ordered set where versionedEntryId = &#63;.
	 *
	 * @param versionedEntryId the versioned entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching versioned entry version, or <code>null</code> if a matching versioned entry version could not be found
	 */
	@Override
	public VersionedEntryVersion fetchByVersionedEntryId_First(
		long versionedEntryId,
		OrderByComparator<VersionedEntryVersion> orderByComparator) {

		List<VersionedEntryVersion> list = findByVersionedEntryId(
			versionedEntryId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last versioned entry version in the ordered set where versionedEntryId = &#63;.
	 *
	 * @param versionedEntryId the versioned entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching versioned entry version
	 * @throws NoSuchVersionedEntryVersionException if a matching versioned entry version could not be found
	 */
	@Override
	public VersionedEntryVersion findByVersionedEntryId_Last(
			long versionedEntryId,
			OrderByComparator<VersionedEntryVersion> orderByComparator)
		throws NoSuchVersionedEntryVersionException {

		VersionedEntryVersion versionedEntryVersion =
			fetchByVersionedEntryId_Last(versionedEntryId, orderByComparator);

		if (versionedEntryVersion != null) {
			return versionedEntryVersion;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("versionedEntryId=");
		sb.append(versionedEntryId);

		sb.append("}");

		throw new NoSuchVersionedEntryVersionException(sb.toString());
	}

	/**
	 * Returns the last versioned entry version in the ordered set where versionedEntryId = &#63;.
	 *
	 * @param versionedEntryId the versioned entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching versioned entry version, or <code>null</code> if a matching versioned entry version could not be found
	 */
	@Override
	public VersionedEntryVersion fetchByVersionedEntryId_Last(
		long versionedEntryId,
		OrderByComparator<VersionedEntryVersion> orderByComparator) {

		int count = countByVersionedEntryId(versionedEntryId);

		if (count == 0) {
			return null;
		}

		List<VersionedEntryVersion> list = findByVersionedEntryId(
			versionedEntryId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the versioned entry versions before and after the current versioned entry version in the ordered set where versionedEntryId = &#63;.
	 *
	 * @param versionedEntryVersionId the primary key of the current versioned entry version
	 * @param versionedEntryId the versioned entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next versioned entry version
	 * @throws NoSuchVersionedEntryVersionException if a versioned entry version with the primary key could not be found
	 */
	@Override
	public VersionedEntryVersion[] findByVersionedEntryId_PrevAndNext(
			long versionedEntryVersionId, long versionedEntryId,
			OrderByComparator<VersionedEntryVersion> orderByComparator)
		throws NoSuchVersionedEntryVersionException {

		VersionedEntryVersion versionedEntryVersion = findByPrimaryKey(
			versionedEntryVersionId);

		Session session = null;

		try {
			session = openSession();

			VersionedEntryVersion[] array = new VersionedEntryVersionImpl[3];

			array[0] = getByVersionedEntryId_PrevAndNext(
				session, versionedEntryVersion, versionedEntryId,
				orderByComparator, true);

			array[1] = versionedEntryVersion;

			array[2] = getByVersionedEntryId_PrevAndNext(
				session, versionedEntryVersion, versionedEntryId,
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

	protected VersionedEntryVersion getByVersionedEntryId_PrevAndNext(
		Session session, VersionedEntryVersion versionedEntryVersion,
		long versionedEntryId,
		OrderByComparator<VersionedEntryVersion> orderByComparator,
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

		sb.append(_SQL_SELECT_VERSIONEDENTRYVERSION_WHERE);

		sb.append(_FINDER_COLUMN_VERSIONEDENTRYID_VERSIONEDENTRYID_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ENTITY_ALIAS_PREFIX);
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
				sb.append(_ENTITY_ALIAS_PREFIX);
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
			sb.append(VersionedEntryVersionModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(versionedEntryId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						versionedEntryVersion)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<VersionedEntryVersion> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the versioned entry versions where versionedEntryId = &#63; from the database.
	 *
	 * @param versionedEntryId the versioned entry ID
	 */
	@Override
	public void removeByVersionedEntryId(long versionedEntryId) {
		for (VersionedEntryVersion versionedEntryVersion :
				findByVersionedEntryId(
					versionedEntryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(versionedEntryVersion);
		}
	}

	/**
	 * Returns the number of versioned entry versions where versionedEntryId = &#63;.
	 *
	 * @param versionedEntryId the versioned entry ID
	 * @return the number of matching versioned entry versions
	 */
	@Override
	public int countByVersionedEntryId(long versionedEntryId) {
		FinderPath finderPath = _finderPathCountByVersionedEntryId;

		Object[] finderArgs = new Object[] {versionedEntryId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_VERSIONEDENTRYVERSION_WHERE);

			sb.append(_FINDER_COLUMN_VERSIONEDENTRYID_VERSIONEDENTRYID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(versionedEntryId);

				count = (Long)query.uniqueResult();

				finderCache.putResult(finderPath, finderArgs, count);
			}
			catch (Exception exception) {
				finderCache.removeResult(finderPath, finderArgs);

				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	private static final String
		_FINDER_COLUMN_VERSIONEDENTRYID_VERSIONEDENTRYID_2 =
			"versionedEntryVersion.versionedEntryId = ?";

	private FinderPath _finderPathFetchByVersionedEntryId_Version;
	private FinderPath _finderPathCountByVersionedEntryId_Version;

	/**
	 * Returns the versioned entry version where versionedEntryId = &#63; and version = &#63; or throws a <code>NoSuchVersionedEntryVersionException</code> if it could not be found.
	 *
	 * @param versionedEntryId the versioned entry ID
	 * @param version the version
	 * @return the matching versioned entry version
	 * @throws NoSuchVersionedEntryVersionException if a matching versioned entry version could not be found
	 */
	@Override
	public VersionedEntryVersion findByVersionedEntryId_Version(
			long versionedEntryId, int version)
		throws NoSuchVersionedEntryVersionException {

		VersionedEntryVersion versionedEntryVersion =
			fetchByVersionedEntryId_Version(versionedEntryId, version);

		if (versionedEntryVersion == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("versionedEntryId=");
			sb.append(versionedEntryId);

			sb.append(", version=");
			sb.append(version);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchVersionedEntryVersionException(sb.toString());
		}

		return versionedEntryVersion;
	}

	/**
	 * Returns the versioned entry version where versionedEntryId = &#63; and version = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param versionedEntryId the versioned entry ID
	 * @param version the version
	 * @return the matching versioned entry version, or <code>null</code> if a matching versioned entry version could not be found
	 */
	@Override
	public VersionedEntryVersion fetchByVersionedEntryId_Version(
		long versionedEntryId, int version) {

		return fetchByVersionedEntryId_Version(versionedEntryId, version, true);
	}

	/**
	 * Returns the versioned entry version where versionedEntryId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param versionedEntryId the versioned entry ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching versioned entry version, or <code>null</code> if a matching versioned entry version could not be found
	 */
	@Override
	public VersionedEntryVersion fetchByVersionedEntryId_Version(
		long versionedEntryId, int version, boolean useFinderCache) {

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {versionedEntryId, version};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByVersionedEntryId_Version, finderArgs, this);
		}

		if (result instanceof VersionedEntryVersion) {
			VersionedEntryVersion versionedEntryVersion =
				(VersionedEntryVersion)result;

			if ((versionedEntryId !=
					versionedEntryVersion.getVersionedEntryId()) ||
				(version != versionedEntryVersion.getVersion())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_VERSIONEDENTRYVERSION_WHERE);

			sb.append(
				_FINDER_COLUMN_VERSIONEDENTRYID_VERSION_VERSIONEDENTRYID_2);

			sb.append(_FINDER_COLUMN_VERSIONEDENTRYID_VERSION_VERSION_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(versionedEntryId);

				queryPos.add(version);

				List<VersionedEntryVersion> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByVersionedEntryId_Version,
							finderArgs, list);
					}
				}
				else {
					VersionedEntryVersion versionedEntryVersion = list.get(0);

					result = versionedEntryVersion;

					cacheResult(versionedEntryVersion);
				}
			}
			catch (Exception exception) {
				if (useFinderCache) {
					finderCache.removeResult(
						_finderPathFetchByVersionedEntryId_Version, finderArgs);
				}

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
			return (VersionedEntryVersion)result;
		}
	}

	/**
	 * Removes the versioned entry version where versionedEntryId = &#63; and version = &#63; from the database.
	 *
	 * @param versionedEntryId the versioned entry ID
	 * @param version the version
	 * @return the versioned entry version that was removed
	 */
	@Override
	public VersionedEntryVersion removeByVersionedEntryId_Version(
			long versionedEntryId, int version)
		throws NoSuchVersionedEntryVersionException {

		VersionedEntryVersion versionedEntryVersion =
			findByVersionedEntryId_Version(versionedEntryId, version);

		return remove(versionedEntryVersion);
	}

	/**
	 * Returns the number of versioned entry versions where versionedEntryId = &#63; and version = &#63;.
	 *
	 * @param versionedEntryId the versioned entry ID
	 * @param version the version
	 * @return the number of matching versioned entry versions
	 */
	@Override
	public int countByVersionedEntryId_Version(
		long versionedEntryId, int version) {

		FinderPath finderPath = _finderPathCountByVersionedEntryId_Version;

		Object[] finderArgs = new Object[] {versionedEntryId, version};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_VERSIONEDENTRYVERSION_WHERE);

			sb.append(
				_FINDER_COLUMN_VERSIONEDENTRYID_VERSION_VERSIONEDENTRYID_2);

			sb.append(_FINDER_COLUMN_VERSIONEDENTRYID_VERSION_VERSION_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(versionedEntryId);

				queryPos.add(version);

				count = (Long)query.uniqueResult();

				finderCache.putResult(finderPath, finderArgs, count);
			}
			catch (Exception exception) {
				finderCache.removeResult(finderPath, finderArgs);

				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	private static final String
		_FINDER_COLUMN_VERSIONEDENTRYID_VERSION_VERSIONEDENTRYID_2 =
			"versionedEntryVersion.versionedEntryId = ? AND ";

	private static final String
		_FINDER_COLUMN_VERSIONEDENTRYID_VERSION_VERSION_2 =
			"versionedEntryVersion.version = ?";

	private FinderPath _finderPathWithPaginationFindByGroupId;
	private FinderPath _finderPathWithoutPaginationFindByGroupId;
	private FinderPath _finderPathCountByGroupId;

	/**
	 * Returns all the versioned entry versions where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching versioned entry versions
	 */
	@Override
	public List<VersionedEntryVersion> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the versioned entry versions where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>VersionedEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of versioned entry versions
	 * @param end the upper bound of the range of versioned entry versions (not inclusive)
	 * @return the range of matching versioned entry versions
	 */
	@Override
	public List<VersionedEntryVersion> findByGroupId(
		long groupId, int start, int end) {

		return findByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the versioned entry versions where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>VersionedEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of versioned entry versions
	 * @param end the upper bound of the range of versioned entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching versioned entry versions
	 */
	@Override
	public List<VersionedEntryVersion> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<VersionedEntryVersion> orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the versioned entry versions where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>VersionedEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of versioned entry versions
	 * @param end the upper bound of the range of versioned entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching versioned entry versions
	 */
	@Override
	public List<VersionedEntryVersion> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<VersionedEntryVersion> orderByComparator,
		boolean useFinderCache) {

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
			finderArgs = new Object[] {groupId, start, end, orderByComparator};
		}

		List<VersionedEntryVersion> list = null;

		if (useFinderCache) {
			list = (List<VersionedEntryVersion>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (VersionedEntryVersion versionedEntryVersion : list) {
					if (groupId != versionedEntryVersion.getGroupId()) {
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

			sb.append(_SQL_SELECT_VERSIONEDENTRYVERSION_WHERE);

			sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator);
			}
			else {
				sb.append(VersionedEntryVersionModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				list = (List<VersionedEntryVersion>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				if (useFinderCache) {
					finderCache.removeResult(finderPath, finderArgs);
				}

				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Returns the first versioned entry version in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching versioned entry version
	 * @throws NoSuchVersionedEntryVersionException if a matching versioned entry version could not be found
	 */
	@Override
	public VersionedEntryVersion findByGroupId_First(
			long groupId,
			OrderByComparator<VersionedEntryVersion> orderByComparator)
		throws NoSuchVersionedEntryVersionException {

		VersionedEntryVersion versionedEntryVersion = fetchByGroupId_First(
			groupId, orderByComparator);

		if (versionedEntryVersion != null) {
			return versionedEntryVersion;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchVersionedEntryVersionException(sb.toString());
	}

	/**
	 * Returns the first versioned entry version in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching versioned entry version, or <code>null</code> if a matching versioned entry version could not be found
	 */
	@Override
	public VersionedEntryVersion fetchByGroupId_First(
		long groupId,
		OrderByComparator<VersionedEntryVersion> orderByComparator) {

		List<VersionedEntryVersion> list = findByGroupId(
			groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last versioned entry version in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching versioned entry version
	 * @throws NoSuchVersionedEntryVersionException if a matching versioned entry version could not be found
	 */
	@Override
	public VersionedEntryVersion findByGroupId_Last(
			long groupId,
			OrderByComparator<VersionedEntryVersion> orderByComparator)
		throws NoSuchVersionedEntryVersionException {

		VersionedEntryVersion versionedEntryVersion = fetchByGroupId_Last(
			groupId, orderByComparator);

		if (versionedEntryVersion != null) {
			return versionedEntryVersion;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchVersionedEntryVersionException(sb.toString());
	}

	/**
	 * Returns the last versioned entry version in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching versioned entry version, or <code>null</code> if a matching versioned entry version could not be found
	 */
	@Override
	public VersionedEntryVersion fetchByGroupId_Last(
		long groupId,
		OrderByComparator<VersionedEntryVersion> orderByComparator) {

		int count = countByGroupId(groupId);

		if (count == 0) {
			return null;
		}

		List<VersionedEntryVersion> list = findByGroupId(
			groupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the versioned entry versions before and after the current versioned entry version in the ordered set where groupId = &#63;.
	 *
	 * @param versionedEntryVersionId the primary key of the current versioned entry version
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next versioned entry version
	 * @throws NoSuchVersionedEntryVersionException if a versioned entry version with the primary key could not be found
	 */
	@Override
	public VersionedEntryVersion[] findByGroupId_PrevAndNext(
			long versionedEntryVersionId, long groupId,
			OrderByComparator<VersionedEntryVersion> orderByComparator)
		throws NoSuchVersionedEntryVersionException {

		VersionedEntryVersion versionedEntryVersion = findByPrimaryKey(
			versionedEntryVersionId);

		Session session = null;

		try {
			session = openSession();

			VersionedEntryVersion[] array = new VersionedEntryVersionImpl[3];

			array[0] = getByGroupId_PrevAndNext(
				session, versionedEntryVersion, groupId, orderByComparator,
				true);

			array[1] = versionedEntryVersion;

			array[2] = getByGroupId_PrevAndNext(
				session, versionedEntryVersion, groupId, orderByComparator,
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

	protected VersionedEntryVersion getByGroupId_PrevAndNext(
		Session session, VersionedEntryVersion versionedEntryVersion,
		long groupId,
		OrderByComparator<VersionedEntryVersion> orderByComparator,
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

		sb.append(_SQL_SELECT_VERSIONEDENTRYVERSION_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ENTITY_ALIAS_PREFIX);
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
				sb.append(_ENTITY_ALIAS_PREFIX);
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
			sb.append(VersionedEntryVersionModelImpl.ORDER_BY_JPQL);
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
						versionedEntryVersion)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<VersionedEntryVersion> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the versioned entry versions where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		for (VersionedEntryVersion versionedEntryVersion :
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(versionedEntryVersion);
		}
	}

	/**
	 * Returns the number of versioned entry versions where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching versioned entry versions
	 */
	@Override
	public int countByGroupId(long groupId) {
		FinderPath finderPath = _finderPathCountByGroupId;

		Object[] finderArgs = new Object[] {groupId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_VERSIONEDENTRYVERSION_WHERE);

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
				finderCache.removeResult(finderPath, finderArgs);

				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	private static final String _FINDER_COLUMN_GROUPID_GROUPID_2 =
		"versionedEntryVersion.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByGroupId_Version;
	private FinderPath _finderPathWithoutPaginationFindByGroupId_Version;
	private FinderPath _finderPathCountByGroupId_Version;

	/**
	 * Returns all the versioned entry versions where groupId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @return the matching versioned entry versions
	 */
	@Override
	public List<VersionedEntryVersion> findByGroupId_Version(
		long groupId, int version) {

		return findByGroupId_Version(
			groupId, version, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the versioned entry versions where groupId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>VersionedEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @param start the lower bound of the range of versioned entry versions
	 * @param end the upper bound of the range of versioned entry versions (not inclusive)
	 * @return the range of matching versioned entry versions
	 */
	@Override
	public List<VersionedEntryVersion> findByGroupId_Version(
		long groupId, int version, int start, int end) {

		return findByGroupId_Version(groupId, version, start, end, null);
	}

	/**
	 * Returns an ordered range of all the versioned entry versions where groupId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>VersionedEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @param start the lower bound of the range of versioned entry versions
	 * @param end the upper bound of the range of versioned entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching versioned entry versions
	 */
	@Override
	public List<VersionedEntryVersion> findByGroupId_Version(
		long groupId, int version, int start, int end,
		OrderByComparator<VersionedEntryVersion> orderByComparator) {

		return findByGroupId_Version(
			groupId, version, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the versioned entry versions where groupId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>VersionedEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @param start the lower bound of the range of versioned entry versions
	 * @param end the upper bound of the range of versioned entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching versioned entry versions
	 */
	@Override
	public List<VersionedEntryVersion> findByGroupId_Version(
		long groupId, int version, int start, int end,
		OrderByComparator<VersionedEntryVersion> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByGroupId_Version;
				finderArgs = new Object[] {groupId, version};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByGroupId_Version;
			finderArgs = new Object[] {
				groupId, version, start, end, orderByComparator
			};
		}

		List<VersionedEntryVersion> list = null;

		if (useFinderCache) {
			list = (List<VersionedEntryVersion>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (VersionedEntryVersion versionedEntryVersion : list) {
					if ((groupId != versionedEntryVersion.getGroupId()) ||
						(version != versionedEntryVersion.getVersion())) {

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

			sb.append(_SQL_SELECT_VERSIONEDENTRYVERSION_WHERE);

			sb.append(_FINDER_COLUMN_GROUPID_VERSION_GROUPID_2);

			sb.append(_FINDER_COLUMN_GROUPID_VERSION_VERSION_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator);
			}
			else {
				sb.append(VersionedEntryVersionModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				queryPos.add(version);

				list = (List<VersionedEntryVersion>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				if (useFinderCache) {
					finderCache.removeResult(finderPath, finderArgs);
				}

				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Returns the first versioned entry version in the ordered set where groupId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching versioned entry version
	 * @throws NoSuchVersionedEntryVersionException if a matching versioned entry version could not be found
	 */
	@Override
	public VersionedEntryVersion findByGroupId_Version_First(
			long groupId, int version,
			OrderByComparator<VersionedEntryVersion> orderByComparator)
		throws NoSuchVersionedEntryVersionException {

		VersionedEntryVersion versionedEntryVersion =
			fetchByGroupId_Version_First(groupId, version, orderByComparator);

		if (versionedEntryVersion != null) {
			return versionedEntryVersion;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", version=");
		sb.append(version);

		sb.append("}");

		throw new NoSuchVersionedEntryVersionException(sb.toString());
	}

	/**
	 * Returns the first versioned entry version in the ordered set where groupId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching versioned entry version, or <code>null</code> if a matching versioned entry version could not be found
	 */
	@Override
	public VersionedEntryVersion fetchByGroupId_Version_First(
		long groupId, int version,
		OrderByComparator<VersionedEntryVersion> orderByComparator) {

		List<VersionedEntryVersion> list = findByGroupId_Version(
			groupId, version, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last versioned entry version in the ordered set where groupId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching versioned entry version
	 * @throws NoSuchVersionedEntryVersionException if a matching versioned entry version could not be found
	 */
	@Override
	public VersionedEntryVersion findByGroupId_Version_Last(
			long groupId, int version,
			OrderByComparator<VersionedEntryVersion> orderByComparator)
		throws NoSuchVersionedEntryVersionException {

		VersionedEntryVersion versionedEntryVersion =
			fetchByGroupId_Version_Last(groupId, version, orderByComparator);

		if (versionedEntryVersion != null) {
			return versionedEntryVersion;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", version=");
		sb.append(version);

		sb.append("}");

		throw new NoSuchVersionedEntryVersionException(sb.toString());
	}

	/**
	 * Returns the last versioned entry version in the ordered set where groupId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching versioned entry version, or <code>null</code> if a matching versioned entry version could not be found
	 */
	@Override
	public VersionedEntryVersion fetchByGroupId_Version_Last(
		long groupId, int version,
		OrderByComparator<VersionedEntryVersion> orderByComparator) {

		int count = countByGroupId_Version(groupId, version);

		if (count == 0) {
			return null;
		}

		List<VersionedEntryVersion> list = findByGroupId_Version(
			groupId, version, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the versioned entry versions before and after the current versioned entry version in the ordered set where groupId = &#63; and version = &#63;.
	 *
	 * @param versionedEntryVersionId the primary key of the current versioned entry version
	 * @param groupId the group ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next versioned entry version
	 * @throws NoSuchVersionedEntryVersionException if a versioned entry version with the primary key could not be found
	 */
	@Override
	public VersionedEntryVersion[] findByGroupId_Version_PrevAndNext(
			long versionedEntryVersionId, long groupId, int version,
			OrderByComparator<VersionedEntryVersion> orderByComparator)
		throws NoSuchVersionedEntryVersionException {

		VersionedEntryVersion versionedEntryVersion = findByPrimaryKey(
			versionedEntryVersionId);

		Session session = null;

		try {
			session = openSession();

			VersionedEntryVersion[] array = new VersionedEntryVersionImpl[3];

			array[0] = getByGroupId_Version_PrevAndNext(
				session, versionedEntryVersion, groupId, version,
				orderByComparator, true);

			array[1] = versionedEntryVersion;

			array[2] = getByGroupId_Version_PrevAndNext(
				session, versionedEntryVersion, groupId, version,
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

	protected VersionedEntryVersion getByGroupId_Version_PrevAndNext(
		Session session, VersionedEntryVersion versionedEntryVersion,
		long groupId, int version,
		OrderByComparator<VersionedEntryVersion> orderByComparator,
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

		sb.append(_SQL_SELECT_VERSIONEDENTRYVERSION_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_VERSION_GROUPID_2);

		sb.append(_FINDER_COLUMN_GROUPID_VERSION_VERSION_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ENTITY_ALIAS_PREFIX);
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
				sb.append(_ENTITY_ALIAS_PREFIX);
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
			sb.append(VersionedEntryVersionModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(version);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						versionedEntryVersion)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<VersionedEntryVersion> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the versioned entry versions where groupId = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param version the version
	 */
	@Override
	public void removeByGroupId_Version(long groupId, int version) {
		for (VersionedEntryVersion versionedEntryVersion :
				findByGroupId_Version(
					groupId, version, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(versionedEntryVersion);
		}
	}

	/**
	 * Returns the number of versioned entry versions where groupId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @return the number of matching versioned entry versions
	 */
	@Override
	public int countByGroupId_Version(long groupId, int version) {
		FinderPath finderPath = _finderPathCountByGroupId_Version;

		Object[] finderArgs = new Object[] {groupId, version};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_VERSIONEDENTRYVERSION_WHERE);

			sb.append(_FINDER_COLUMN_GROUPID_VERSION_GROUPID_2);

			sb.append(_FINDER_COLUMN_GROUPID_VERSION_VERSION_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				queryPos.add(version);

				count = (Long)query.uniqueResult();

				finderCache.putResult(finderPath, finderArgs, count);
			}
			catch (Exception exception) {
				finderCache.removeResult(finderPath, finderArgs);

				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	private static final String _FINDER_COLUMN_GROUPID_VERSION_GROUPID_2 =
		"versionedEntryVersion.groupId = ? AND ";

	private static final String _FINDER_COLUMN_GROUPID_VERSION_VERSION_2 =
		"versionedEntryVersion.version = ?";

	public VersionedEntryVersionPersistenceImpl() {
		setModelClass(VersionedEntryVersion.class);
	}

	/**
	 * Caches the versioned entry version in the entity cache if it is enabled.
	 *
	 * @param versionedEntryVersion the versioned entry version
	 */
	@Override
	public void cacheResult(VersionedEntryVersion versionedEntryVersion) {
		entityCache.putResult(
			VersionedEntryVersionModelImpl.ENTITY_CACHE_ENABLED,
			VersionedEntryVersionImpl.class,
			versionedEntryVersion.getPrimaryKey(), versionedEntryVersion);

		finderCache.putResult(
			_finderPathFetchByVersionedEntryId_Version,
			new Object[] {
				versionedEntryVersion.getVersionedEntryId(),
				versionedEntryVersion.getVersion()
			},
			versionedEntryVersion);

		versionedEntryVersion.resetOriginalValues();
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the versioned entry versions in the entity cache if it is enabled.
	 *
	 * @param versionedEntryVersions the versioned entry versions
	 */
	@Override
	public void cacheResult(
		List<VersionedEntryVersion> versionedEntryVersions) {

		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (versionedEntryVersions.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (VersionedEntryVersion versionedEntryVersion :
				versionedEntryVersions) {

			if (entityCache.getResult(
					VersionedEntryVersionModelImpl.ENTITY_CACHE_ENABLED,
					VersionedEntryVersionImpl.class,
					versionedEntryVersion.getPrimaryKey()) == null) {

				cacheResult(versionedEntryVersion);
			}
			else {
				versionedEntryVersion.resetOriginalValues();
			}
		}
	}

	/**
	 * Clears the cache for all versioned entry versions.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(VersionedEntryVersionImpl.class);

		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the versioned entry version.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(VersionedEntryVersion versionedEntryVersion) {
		entityCache.removeResult(
			VersionedEntryVersionModelImpl.ENTITY_CACHE_ENABLED,
			VersionedEntryVersionImpl.class,
			versionedEntryVersion.getPrimaryKey());

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		clearUniqueFindersCache(
			(VersionedEntryVersionModelImpl)versionedEntryVersion, true);
	}

	@Override
	public void clearCache(List<VersionedEntryVersion> versionedEntryVersions) {
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (VersionedEntryVersion versionedEntryVersion :
				versionedEntryVersions) {

			entityCache.removeResult(
				VersionedEntryVersionModelImpl.ENTITY_CACHE_ENABLED,
				VersionedEntryVersionImpl.class,
				versionedEntryVersion.getPrimaryKey());

			clearUniqueFindersCache(
				(VersionedEntryVersionModelImpl)versionedEntryVersion, true);
		}
	}

	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				VersionedEntryVersionModelImpl.ENTITY_CACHE_ENABLED,
				VersionedEntryVersionImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		VersionedEntryVersionModelImpl versionedEntryVersionModelImpl) {

		Object[] args = new Object[] {
			versionedEntryVersionModelImpl.getVersionedEntryId(),
			versionedEntryVersionModelImpl.getVersion()
		};

		finderCache.putResult(
			_finderPathCountByVersionedEntryId_Version, args, Long.valueOf(1),
			false);
		finderCache.putResult(
			_finderPathFetchByVersionedEntryId_Version, args,
			versionedEntryVersionModelImpl, false);
	}

	protected void clearUniqueFindersCache(
		VersionedEntryVersionModelImpl versionedEntryVersionModelImpl,
		boolean clearCurrent) {

		if (clearCurrent) {
			Object[] args = new Object[] {
				versionedEntryVersionModelImpl.getVersionedEntryId(),
				versionedEntryVersionModelImpl.getVersion()
			};

			finderCache.removeResult(
				_finderPathCountByVersionedEntryId_Version, args);
			finderCache.removeResult(
				_finderPathFetchByVersionedEntryId_Version, args);
		}

		if ((versionedEntryVersionModelImpl.getColumnBitmask() &
			 _finderPathFetchByVersionedEntryId_Version.getColumnBitmask()) !=
				 0) {

			Object[] args = new Object[] {
				versionedEntryVersionModelImpl.getOriginalVersionedEntryId(),
				versionedEntryVersionModelImpl.getOriginalVersion()
			};

			finderCache.removeResult(
				_finderPathCountByVersionedEntryId_Version, args);
			finderCache.removeResult(
				_finderPathFetchByVersionedEntryId_Version, args);
		}
	}

	/**
	 * Creates a new versioned entry version with the primary key. Does not add the versioned entry version to the database.
	 *
	 * @param versionedEntryVersionId the primary key for the new versioned entry version
	 * @return the new versioned entry version
	 */
	@Override
	public VersionedEntryVersion create(long versionedEntryVersionId) {
		VersionedEntryVersion versionedEntryVersion =
			new VersionedEntryVersionImpl();

		versionedEntryVersion.setNew(true);
		versionedEntryVersion.setPrimaryKey(versionedEntryVersionId);

		return versionedEntryVersion;
	}

	/**
	 * Removes the versioned entry version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param versionedEntryVersionId the primary key of the versioned entry version
	 * @return the versioned entry version that was removed
	 * @throws NoSuchVersionedEntryVersionException if a versioned entry version with the primary key could not be found
	 */
	@Override
	public VersionedEntryVersion remove(long versionedEntryVersionId)
		throws NoSuchVersionedEntryVersionException {

		return remove((Serializable)versionedEntryVersionId);
	}

	/**
	 * Removes the versioned entry version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the versioned entry version
	 * @return the versioned entry version that was removed
	 * @throws NoSuchVersionedEntryVersionException if a versioned entry version with the primary key could not be found
	 */
	@Override
	public VersionedEntryVersion remove(Serializable primaryKey)
		throws NoSuchVersionedEntryVersionException {

		Session session = null;

		try {
			session = openSession();

			VersionedEntryVersion versionedEntryVersion =
				(VersionedEntryVersion)session.get(
					VersionedEntryVersionImpl.class, primaryKey);

			if (versionedEntryVersion == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchVersionedEntryVersionException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(versionedEntryVersion);
		}
		catch (NoSuchVersionedEntryVersionException noSuchEntityException) {
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
	protected VersionedEntryVersion removeImpl(
		VersionedEntryVersion versionedEntryVersion) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(versionedEntryVersion)) {
				versionedEntryVersion = (VersionedEntryVersion)session.get(
					VersionedEntryVersionImpl.class,
					versionedEntryVersion.getPrimaryKeyObj());
			}

			if (versionedEntryVersion != null) {
				session.delete(versionedEntryVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (versionedEntryVersion != null) {
			clearCache(versionedEntryVersion);
		}

		return versionedEntryVersion;
	}

	@Override
	public VersionedEntryVersion updateImpl(
		VersionedEntryVersion versionedEntryVersion) {

		boolean isNew = versionedEntryVersion.isNew();

		if (!(versionedEntryVersion instanceof
				VersionedEntryVersionModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(versionedEntryVersion.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					versionedEntryVersion);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in versionedEntryVersion proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom VersionedEntryVersion implementation " +
					versionedEntryVersion.getClass());
		}

		VersionedEntryVersionModelImpl versionedEntryVersionModelImpl =
			(VersionedEntryVersionModelImpl)versionedEntryVersion;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(versionedEntryVersion);

				versionedEntryVersion.setNew(false);
			}
			else {
				throw new IllegalArgumentException(
					"VersionedEntryVersion is read only, create a new version instead");
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);

		if (!VersionedEntryVersionModelImpl.COLUMN_BITMASK_ENABLED) {
			finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
		}
		else if (isNew) {
			Object[] args = new Object[] {
				versionedEntryVersionModelImpl.getVersionedEntryId()
			};

			finderCache.removeResult(_finderPathCountByVersionedEntryId, args);
			finderCache.removeResult(
				_finderPathWithoutPaginationFindByVersionedEntryId, args);

			args = new Object[] {versionedEntryVersionModelImpl.getGroupId()};

			finderCache.removeResult(_finderPathCountByGroupId, args);
			finderCache.removeResult(
				_finderPathWithoutPaginationFindByGroupId, args);

			args = new Object[] {
				versionedEntryVersionModelImpl.getGroupId(),
				versionedEntryVersionModelImpl.getVersion()
			};

			finderCache.removeResult(_finderPathCountByGroupId_Version, args);
			finderCache.removeResult(
				_finderPathWithoutPaginationFindByGroupId_Version, args);

			finderCache.removeResult(_finderPathCountAll, FINDER_ARGS_EMPTY);
			finderCache.removeResult(
				_finderPathWithoutPaginationFindAll, FINDER_ARGS_EMPTY);
		}
		else {
			if ((versionedEntryVersionModelImpl.getColumnBitmask() &
				 _finderPathWithoutPaginationFindByVersionedEntryId.
					 getColumnBitmask()) != 0) {

				Object[] args = new Object[] {
					versionedEntryVersionModelImpl.getOriginalVersionedEntryId()
				};

				finderCache.removeResult(
					_finderPathCountByVersionedEntryId, args);
				finderCache.removeResult(
					_finderPathWithoutPaginationFindByVersionedEntryId, args);

				args = new Object[] {
					versionedEntryVersionModelImpl.getVersionedEntryId()
				};

				finderCache.removeResult(
					_finderPathCountByVersionedEntryId, args);
				finderCache.removeResult(
					_finderPathWithoutPaginationFindByVersionedEntryId, args);
			}

			if ((versionedEntryVersionModelImpl.getColumnBitmask() &
				 _finderPathWithoutPaginationFindByGroupId.
					 getColumnBitmask()) != 0) {

				Object[] args = new Object[] {
					versionedEntryVersionModelImpl.getOriginalGroupId()
				};

				finderCache.removeResult(_finderPathCountByGroupId, args);
				finderCache.removeResult(
					_finderPathWithoutPaginationFindByGroupId, args);

				args = new Object[] {
					versionedEntryVersionModelImpl.getGroupId()
				};

				finderCache.removeResult(_finderPathCountByGroupId, args);
				finderCache.removeResult(
					_finderPathWithoutPaginationFindByGroupId, args);
			}

			if ((versionedEntryVersionModelImpl.getColumnBitmask() &
				 _finderPathWithoutPaginationFindByGroupId_Version.
					 getColumnBitmask()) != 0) {

				Object[] args = new Object[] {
					versionedEntryVersionModelImpl.getOriginalGroupId(),
					versionedEntryVersionModelImpl.getOriginalVersion()
				};

				finderCache.removeResult(
					_finderPathCountByGroupId_Version, args);
				finderCache.removeResult(
					_finderPathWithoutPaginationFindByGroupId_Version, args);

				args = new Object[] {
					versionedEntryVersionModelImpl.getGroupId(),
					versionedEntryVersionModelImpl.getVersion()
				};

				finderCache.removeResult(
					_finderPathCountByGroupId_Version, args);
				finderCache.removeResult(
					_finderPathWithoutPaginationFindByGroupId_Version, args);
			}
		}

		entityCache.putResult(
			VersionedEntryVersionModelImpl.ENTITY_CACHE_ENABLED,
			VersionedEntryVersionImpl.class,
			versionedEntryVersion.getPrimaryKey(), versionedEntryVersion,
			false);

		clearUniqueFindersCache(versionedEntryVersionModelImpl, false);
		cacheUniqueFindersCache(versionedEntryVersionModelImpl);

		versionedEntryVersion.resetOriginalValues();

		return versionedEntryVersion;
	}

	/**
	 * Returns the versioned entry version with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the versioned entry version
	 * @return the versioned entry version
	 * @throws NoSuchVersionedEntryVersionException if a versioned entry version with the primary key could not be found
	 */
	@Override
	public VersionedEntryVersion findByPrimaryKey(Serializable primaryKey)
		throws NoSuchVersionedEntryVersionException {

		VersionedEntryVersion versionedEntryVersion = fetchByPrimaryKey(
			primaryKey);

		if (versionedEntryVersion == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchVersionedEntryVersionException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return versionedEntryVersion;
	}

	/**
	 * Returns the versioned entry version with the primary key or throws a <code>NoSuchVersionedEntryVersionException</code> if it could not be found.
	 *
	 * @param versionedEntryVersionId the primary key of the versioned entry version
	 * @return the versioned entry version
	 * @throws NoSuchVersionedEntryVersionException if a versioned entry version with the primary key could not be found
	 */
	@Override
	public VersionedEntryVersion findByPrimaryKey(long versionedEntryVersionId)
		throws NoSuchVersionedEntryVersionException {

		return findByPrimaryKey((Serializable)versionedEntryVersionId);
	}

	/**
	 * Returns the versioned entry version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the versioned entry version
	 * @return the versioned entry version, or <code>null</code> if a versioned entry version with the primary key could not be found
	 */
	@Override
	public VersionedEntryVersion fetchByPrimaryKey(Serializable primaryKey) {
		Serializable serializable = entityCache.getResult(
			VersionedEntryVersionModelImpl.ENTITY_CACHE_ENABLED,
			VersionedEntryVersionImpl.class, primaryKey);

		if (serializable == nullModel) {
			return null;
		}

		VersionedEntryVersion versionedEntryVersion =
			(VersionedEntryVersion)serializable;

		if (versionedEntryVersion == null) {
			Session session = null;

			try {
				session = openSession();

				versionedEntryVersion = (VersionedEntryVersion)session.get(
					VersionedEntryVersionImpl.class, primaryKey);

				if (versionedEntryVersion != null) {
					cacheResult(versionedEntryVersion);
				}
				else {
					entityCache.putResult(
						VersionedEntryVersionModelImpl.ENTITY_CACHE_ENABLED,
						VersionedEntryVersionImpl.class, primaryKey, nullModel);
				}
			}
			catch (Exception exception) {
				entityCache.removeResult(
					VersionedEntryVersionModelImpl.ENTITY_CACHE_ENABLED,
					VersionedEntryVersionImpl.class, primaryKey);

				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return versionedEntryVersion;
	}

	/**
	 * Returns the versioned entry version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param versionedEntryVersionId the primary key of the versioned entry version
	 * @return the versioned entry version, or <code>null</code> if a versioned entry version with the primary key could not be found
	 */
	@Override
	public VersionedEntryVersion fetchByPrimaryKey(
		long versionedEntryVersionId) {

		return fetchByPrimaryKey((Serializable)versionedEntryVersionId);
	}

	@Override
	public Map<Serializable, VersionedEntryVersion> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, VersionedEntryVersion> map =
			new HashMap<Serializable, VersionedEntryVersion>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			VersionedEntryVersion versionedEntryVersion = fetchByPrimaryKey(
				primaryKey);

			if (versionedEntryVersion != null) {
				map.put(primaryKey, versionedEntryVersion);
			}

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

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			Serializable serializable = entityCache.getResult(
				VersionedEntryVersionModelImpl.ENTITY_CACHE_ENABLED,
				VersionedEntryVersionImpl.class, primaryKey);

			if (serializable != nullModel) {
				if (serializable == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<Serializable>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, (VersionedEntryVersion)serializable);
				}
			}
		}

		if (uncachedPrimaryKeys == null) {
			return map;
		}

		StringBundler sb = new StringBundler(
			(uncachedPrimaryKeys.size() * 2) + 1);

		sb.append(_SQL_SELECT_VERSIONEDENTRYVERSION_WHERE_PKS_IN);

		for (Serializable primaryKey : uncachedPrimaryKeys) {
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

			for (VersionedEntryVersion versionedEntryVersion :
					(List<VersionedEntryVersion>)query.list()) {

				map.put(
					versionedEntryVersion.getPrimaryKeyObj(),
					versionedEntryVersion);

				cacheResult(versionedEntryVersion);

				uncachedPrimaryKeys.remove(
					versionedEntryVersion.getPrimaryKeyObj());
			}

			for (Serializable primaryKey : uncachedPrimaryKeys) {
				entityCache.putResult(
					VersionedEntryVersionModelImpl.ENTITY_CACHE_ENABLED,
					VersionedEntryVersionImpl.class, primaryKey, nullModel);
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
	 * Returns all the versioned entry versions.
	 *
	 * @return the versioned entry versions
	 */
	@Override
	public List<VersionedEntryVersion> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the versioned entry versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>VersionedEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of versioned entry versions
	 * @param end the upper bound of the range of versioned entry versions (not inclusive)
	 * @return the range of versioned entry versions
	 */
	@Override
	public List<VersionedEntryVersion> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the versioned entry versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>VersionedEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of versioned entry versions
	 * @param end the upper bound of the range of versioned entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of versioned entry versions
	 */
	@Override
	public List<VersionedEntryVersion> findAll(
		int start, int end,
		OrderByComparator<VersionedEntryVersion> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the versioned entry versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>VersionedEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of versioned entry versions
	 * @param end the upper bound of the range of versioned entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of versioned entry versions
	 */
	@Override
	public List<VersionedEntryVersion> findAll(
		int start, int end,
		OrderByComparator<VersionedEntryVersion> orderByComparator,
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

		List<VersionedEntryVersion> list = null;

		if (useFinderCache) {
			list = (List<VersionedEntryVersion>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_VERSIONEDENTRYVERSION);

				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_VERSIONEDENTRYVERSION;

				sql = sql.concat(VersionedEntryVersionModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<VersionedEntryVersion>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				if (useFinderCache) {
					finderCache.removeResult(finderPath, finderArgs);
				}

				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Removes all the versioned entry versions from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (VersionedEntryVersion versionedEntryVersion : findAll()) {
			remove(versionedEntryVersion);
		}
	}

	/**
	 * Returns the number of versioned entry versions.
	 *
	 * @return the number of versioned entry versions
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
					"SELECT COUNT(versionedEntryVersion) FROM VersionedEntryVersion versionedEntryVersion");

				count = (Long)query.uniqueResult();

				finderCache.putResult(
					_finderPathCountAll, FINDER_ARGS_EMPTY, count);
			}
			catch (Exception exception) {
				finderCache.removeResult(
					_finderPathCountAll, FINDER_ARGS_EMPTY);

				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return VersionedEntryVersionModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the versioned entry version persistence.
	 */
	public void afterPropertiesSet() {
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		_finderPathWithPaginationFindAll = new FinderPath(
			VersionedEntryVersionModelImpl.ENTITY_CACHE_ENABLED,
			VersionedEntryVersionModelImpl.FINDER_CACHE_ENABLED,
			VersionedEntryVersionImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0]);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			VersionedEntryVersionModelImpl.ENTITY_CACHE_ENABLED,
			VersionedEntryVersionModelImpl.FINDER_CACHE_ENABLED,
			VersionedEntryVersionImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll",
			new String[0]);

		_finderPathCountAll = new FinderPath(
			VersionedEntryVersionModelImpl.ENTITY_CACHE_ENABLED,
			VersionedEntryVersionModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0]);

		_finderPathWithPaginationFindByVersionedEntryId = new FinderPath(
			VersionedEntryVersionModelImpl.ENTITY_CACHE_ENABLED,
			VersionedEntryVersionModelImpl.FINDER_CACHE_ENABLED,
			VersionedEntryVersionImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByVersionedEntryId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			});

		_finderPathWithoutPaginationFindByVersionedEntryId = new FinderPath(
			VersionedEntryVersionModelImpl.ENTITY_CACHE_ENABLED,
			VersionedEntryVersionModelImpl.FINDER_CACHE_ENABLED,
			VersionedEntryVersionImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByVersionedEntryId",
			new String[] {Long.class.getName()},
			VersionedEntryVersionModelImpl.VERSIONEDENTRYID_COLUMN_BITMASK |
			VersionedEntryVersionModelImpl.VERSION_COLUMN_BITMASK);

		_finderPathCountByVersionedEntryId = new FinderPath(
			VersionedEntryVersionModelImpl.ENTITY_CACHE_ENABLED,
			VersionedEntryVersionModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByVersionedEntryId", new String[] {Long.class.getName()});

		_finderPathFetchByVersionedEntryId_Version = new FinderPath(
			VersionedEntryVersionModelImpl.ENTITY_CACHE_ENABLED,
			VersionedEntryVersionModelImpl.FINDER_CACHE_ENABLED,
			VersionedEntryVersionImpl.class, FINDER_CLASS_NAME_ENTITY,
			"fetchByVersionedEntryId_Version",
			new String[] {Long.class.getName(), Integer.class.getName()},
			VersionedEntryVersionModelImpl.VERSIONEDENTRYID_COLUMN_BITMASK |
			VersionedEntryVersionModelImpl.VERSION_COLUMN_BITMASK);

		_finderPathCountByVersionedEntryId_Version = new FinderPath(
			VersionedEntryVersionModelImpl.ENTITY_CACHE_ENABLED,
			VersionedEntryVersionModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByVersionedEntryId_Version",
			new String[] {Long.class.getName(), Integer.class.getName()});

		_finderPathWithPaginationFindByGroupId = new FinderPath(
			VersionedEntryVersionModelImpl.ENTITY_CACHE_ENABLED,
			VersionedEntryVersionModelImpl.FINDER_CACHE_ENABLED,
			VersionedEntryVersionImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGroupId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			});

		_finderPathWithoutPaginationFindByGroupId = new FinderPath(
			VersionedEntryVersionModelImpl.ENTITY_CACHE_ENABLED,
			VersionedEntryVersionModelImpl.FINDER_CACHE_ENABLED,
			VersionedEntryVersionImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByGroupId",
			new String[] {Long.class.getName()},
			VersionedEntryVersionModelImpl.GROUPID_COLUMN_BITMASK |
			VersionedEntryVersionModelImpl.VERSION_COLUMN_BITMASK);

		_finderPathCountByGroupId = new FinderPath(
			VersionedEntryVersionModelImpl.ENTITY_CACHE_ENABLED,
			VersionedEntryVersionModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByGroupId",
			new String[] {Long.class.getName()});

		_finderPathWithPaginationFindByGroupId_Version = new FinderPath(
			VersionedEntryVersionModelImpl.ENTITY_CACHE_ENABLED,
			VersionedEntryVersionModelImpl.FINDER_CACHE_ENABLED,
			VersionedEntryVersionImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGroupId_Version",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			});

		_finderPathWithoutPaginationFindByGroupId_Version = new FinderPath(
			VersionedEntryVersionModelImpl.ENTITY_CACHE_ENABLED,
			VersionedEntryVersionModelImpl.FINDER_CACHE_ENABLED,
			VersionedEntryVersionImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByGroupId_Version",
			new String[] {Long.class.getName(), Integer.class.getName()},
			VersionedEntryVersionModelImpl.GROUPID_COLUMN_BITMASK |
			VersionedEntryVersionModelImpl.VERSION_COLUMN_BITMASK);

		_finderPathCountByGroupId_Version = new FinderPath(
			VersionedEntryVersionModelImpl.ENTITY_CACHE_ENABLED,
			VersionedEntryVersionModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByGroupId_Version",
			new String[] {Long.class.getName(), Integer.class.getName()});

		VersionedEntryVersionUtil.setPersistence(this);
	}

	public void destroy() {
		VersionedEntryVersionUtil.setPersistence(null);

		entityCache.removeCache(VersionedEntryVersionImpl.class.getName());

		finderCache.removeCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.removeCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.removeCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		VersionedEntryVersionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_VERSIONEDENTRYVERSION =
		"SELECT versionedEntryVersion FROM VersionedEntryVersion versionedEntryVersion";

	private static final String _SQL_SELECT_VERSIONEDENTRYVERSION_WHERE_PKS_IN =
		"SELECT versionedEntryVersion FROM VersionedEntryVersion versionedEntryVersion WHERE versionedEntryVersionId IN (";

	private static final String _SQL_SELECT_VERSIONEDENTRYVERSION_WHERE =
		"SELECT versionedEntryVersion FROM VersionedEntryVersion versionedEntryVersion WHERE ";

	private static final String _SQL_COUNT_VERSIONEDENTRYVERSION_WHERE =
		"SELECT COUNT(versionedEntryVersion) FROM VersionedEntryVersion versionedEntryVersion WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No VersionedEntryVersion exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No VersionedEntryVersion exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		VersionedEntryVersionPersistenceImpl.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-486670362