/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.compat730.service.persistence.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.compat730.exception.NoSuchLikeFinderEntryException;
import com.liferay.portal.tools.service.builder.test.compat730.model.LikeFinderEntry;
import com.liferay.portal.tools.service.builder.test.compat730.model.impl.LikeFinderEntryImpl;
import com.liferay.portal.tools.service.builder.test.compat730.model.impl.LikeFinderEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.compat730.service.persistence.LikeFinderEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat730.service.persistence.LikeFinderEntryUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * The persistence implementation for the like finder entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class LikeFinderEntryPersistenceImpl
	extends BasePersistenceImpl<LikeFinderEntry>
	implements LikeFinderEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LikeFinderEntryUtil</code> to access the like finder entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LikeFinderEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathFetchByO_O_P;
	private FinderPath _finderPathCountByO_O_P;

	/**
	 * Returns the like finder entry where ownerId = &#63; and ownerType = &#63; and portletId = &#63; or throws a <code>NoSuchLikeFinderEntryException</code> if it could not be found.
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @return the matching like finder entry
	 * @throws NoSuchLikeFinderEntryException if a matching like finder entry could not be found
	 */
	@Override
	public LikeFinderEntry findByO_O_P(
			long ownerId, int ownerType, String portletId)
		throws NoSuchLikeFinderEntryException {

		LikeFinderEntry likeFinderEntry = fetchByO_O_P(
			ownerId, ownerType, portletId);

		if (likeFinderEntry == null) {
			StringBundler sb = new StringBundler(8);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("ownerId=");
			sb.append(ownerId);

			sb.append(", ownerType=");
			sb.append(ownerType);

			sb.append(", portletId=");
			sb.append(portletId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchLikeFinderEntryException(sb.toString());
		}

		return likeFinderEntry;
	}

	/**
	 * Returns the like finder entry where ownerId = &#63; and ownerType = &#63; and portletId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @return the matching like finder entry, or <code>null</code> if a matching like finder entry could not be found
	 */
	@Override
	public LikeFinderEntry fetchByO_O_P(
		long ownerId, int ownerType, String portletId) {

		return fetchByO_O_P(ownerId, ownerType, portletId, true);
	}

	/**
	 * Returns the like finder entry where ownerId = &#63; and ownerType = &#63; and portletId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching like finder entry, or <code>null</code> if a matching like finder entry could not be found
	 */
	@Override
	public LikeFinderEntry fetchByO_O_P(
		long ownerId, int ownerType, String portletId, boolean useFinderCache) {

		portletId = Objects.toString(portletId, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {ownerId, ownerType, portletId};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByO_O_P, finderArgs, this);
		}

		if (result instanceof LikeFinderEntry) {
			LikeFinderEntry likeFinderEntry = (LikeFinderEntry)result;

			if ((ownerId != likeFinderEntry.getOwnerId()) ||
				(ownerType != likeFinderEntry.getOwnerType()) ||
				!Objects.equals(portletId, likeFinderEntry.getPortletId())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(5);

			sb.append(_SQL_SELECT_LIKEFINDERENTRY_WHERE);

			sb.append(_FINDER_COLUMN_O_O_P_OWNERID_2);

			sb.append(_FINDER_COLUMN_O_O_P_OWNERTYPE_2);

			boolean bindPortletId = false;

			if (portletId.isEmpty()) {
				sb.append(_FINDER_COLUMN_O_O_P_PORTLETID_3);
			}
			else {
				bindPortletId = true;

				sb.append(_FINDER_COLUMN_O_O_P_PORTLETID_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(ownerId);

				queryPos.add(ownerType);

				if (bindPortletId) {
					queryPos.add(portletId);
				}

				List<LikeFinderEntry> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByO_O_P, finderArgs, list);
					}
				}
				else {
					LikeFinderEntry likeFinderEntry = list.get(0);

					result = likeFinderEntry;

					cacheResult(likeFinderEntry);
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
			return (LikeFinderEntry)result;
		}
	}

	/**
	 * Removes the like finder entry where ownerId = &#63; and ownerType = &#63; and portletId = &#63; from the database.
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @return the like finder entry that was removed
	 */
	@Override
	public LikeFinderEntry removeByO_O_P(
			long ownerId, int ownerType, String portletId)
		throws NoSuchLikeFinderEntryException {

		LikeFinderEntry likeFinderEntry = findByO_O_P(
			ownerId, ownerType, portletId);

		return remove(likeFinderEntry);
	}

	/**
	 * Returns the number of like finder entries where ownerId = &#63; and ownerType = &#63; and portletId = &#63;.
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @return the number of matching like finder entries
	 */
	@Override
	public int countByO_O_P(long ownerId, int ownerType, String portletId) {
		portletId = Objects.toString(portletId, "");

		FinderPath finderPath = _finderPathCountByO_O_P;

		Object[] finderArgs = new Object[] {ownerId, ownerType, portletId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_COUNT_LIKEFINDERENTRY_WHERE);

			sb.append(_FINDER_COLUMN_O_O_P_OWNERID_2);

			sb.append(_FINDER_COLUMN_O_O_P_OWNERTYPE_2);

			boolean bindPortletId = false;

			if (portletId.isEmpty()) {
				sb.append(_FINDER_COLUMN_O_O_P_PORTLETID_3);
			}
			else {
				bindPortletId = true;

				sb.append(_FINDER_COLUMN_O_O_P_PORTLETID_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(ownerId);

				queryPos.add(ownerType);

				if (bindPortletId) {
					queryPos.add(portletId);
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

	private static final String _FINDER_COLUMN_O_O_P_OWNERID_2 =
		"likeFinderEntry.ownerId = ? AND ";

	private static final String _FINDER_COLUMN_O_O_P_OWNERTYPE_2 =
		"likeFinderEntry.ownerType = ? AND ";

	private static final String _FINDER_COLUMN_O_O_P_PORTLETID_2 =
		"likeFinderEntry.portletId = ?";

	private static final String _FINDER_COLUMN_O_O_P_PORTLETID_3 =
		"(likeFinderEntry.portletId IS NULL OR likeFinderEntry.portletId = '')";

	private FinderPath _finderPathWithPaginationFindByC_O_O_LikeP;
	private FinderPath _finderPathWithPaginationCountByC_O_O_LikeP;

	/**
	 * Returns all the like finder entries where companyId = &#63; and ownerId = &#63; and ownerType = &#63; and portletId LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @return the matching like finder entries
	 */
	@Override
	public List<LikeFinderEntry> findByC_O_O_LikeP(
		long companyId, long ownerId, int ownerType, String portletId) {

		return findByC_O_O_LikeP(
			companyId, ownerId, ownerType, portletId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the like finder entries where companyId = &#63; and ownerId = &#63; and ownerType = &#63; and portletId LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LikeFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @param start the lower bound of the range of like finder entries
	 * @param end the upper bound of the range of like finder entries (not inclusive)
	 * @return the range of matching like finder entries
	 */
	@Override
	public List<LikeFinderEntry> findByC_O_O_LikeP(
		long companyId, long ownerId, int ownerType, String portletId,
		int start, int end) {

		return findByC_O_O_LikeP(
			companyId, ownerId, ownerType, portletId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the like finder entries where companyId = &#63; and ownerId = &#63; and ownerType = &#63; and portletId LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LikeFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @param start the lower bound of the range of like finder entries
	 * @param end the upper bound of the range of like finder entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching like finder entries
	 */
	@Override
	public List<LikeFinderEntry> findByC_O_O_LikeP(
		long companyId, long ownerId, int ownerType, String portletId,
		int start, int end,
		OrderByComparator<LikeFinderEntry> orderByComparator) {

		return findByC_O_O_LikeP(
			companyId, ownerId, ownerType, portletId, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the like finder entries where companyId = &#63; and ownerId = &#63; and ownerType = &#63; and portletId LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LikeFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @param start the lower bound of the range of like finder entries
	 * @param end the upper bound of the range of like finder entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching like finder entries
	 */
	@Override
	public List<LikeFinderEntry> findByC_O_O_LikeP(
		long companyId, long ownerId, int ownerType, String portletId,
		int start, int end,
		OrderByComparator<LikeFinderEntry> orderByComparator,
		boolean useFinderCache) {

		portletId = Objects.toString(portletId, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		finderPath = _finderPathWithPaginationFindByC_O_O_LikeP;
		finderArgs = new Object[] {
			companyId, ownerId, ownerType, portletId, start, end,
			orderByComparator
		};

		List<LikeFinderEntry> list = null;

		if (useFinderCache) {
			list = (List<LikeFinderEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (LikeFinderEntry likeFinderEntry : list) {
					if ((companyId != likeFinderEntry.getCompanyId()) ||
						(ownerId != likeFinderEntry.getOwnerId()) ||
						(ownerType != likeFinderEntry.getOwnerType()) ||
						!StringUtil.wildcardMatches(
							likeFinderEntry.getPortletId(), portletId, '_', '%',
							'\\', true)) {

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

			sb.append(_SQL_SELECT_LIKEFINDERENTRY_WHERE);

			sb.append(_FINDER_COLUMN_C_O_O_LIKEP_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_O_O_LIKEP_OWNERID_2);

			sb.append(_FINDER_COLUMN_C_O_O_LIKEP_OWNERTYPE_2);

			boolean bindPortletId = false;

			if (portletId.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_O_O_LIKEP_PORTLETID_3);
			}
			else {
				bindPortletId = true;

				sb.append(_FINDER_COLUMN_C_O_O_LIKEP_PORTLETID_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator);
			}
			else {
				sb.append(LikeFinderEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(ownerId);

				queryPos.add(ownerType);

				if (bindPortletId) {
					queryPos.add(portletId);
				}

				list = (List<LikeFinderEntry>)QueryUtil.list(
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
	 * Returns the first like finder entry in the ordered set where companyId = &#63; and ownerId = &#63; and ownerType = &#63; and portletId LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching like finder entry
	 * @throws NoSuchLikeFinderEntryException if a matching like finder entry could not be found
	 */
	@Override
	public LikeFinderEntry findByC_O_O_LikeP_First(
			long companyId, long ownerId, int ownerType, String portletId,
			OrderByComparator<LikeFinderEntry> orderByComparator)
		throws NoSuchLikeFinderEntryException {

		LikeFinderEntry likeFinderEntry = fetchByC_O_O_LikeP_First(
			companyId, ownerId, ownerType, portletId, orderByComparator);

		if (likeFinderEntry != null) {
			return likeFinderEntry;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", ownerId=");
		sb.append(ownerId);

		sb.append(", ownerType=");
		sb.append(ownerType);

		sb.append(", portletIdLIKE");
		sb.append(portletId);

		sb.append("}");

		throw new NoSuchLikeFinderEntryException(sb.toString());
	}

	/**
	 * Returns the first like finder entry in the ordered set where companyId = &#63; and ownerId = &#63; and ownerType = &#63; and portletId LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching like finder entry, or <code>null</code> if a matching like finder entry could not be found
	 */
	@Override
	public LikeFinderEntry fetchByC_O_O_LikeP_First(
		long companyId, long ownerId, int ownerType, String portletId,
		OrderByComparator<LikeFinderEntry> orderByComparator) {

		List<LikeFinderEntry> list = findByC_O_O_LikeP(
			companyId, ownerId, ownerType, portletId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last like finder entry in the ordered set where companyId = &#63; and ownerId = &#63; and ownerType = &#63; and portletId LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching like finder entry
	 * @throws NoSuchLikeFinderEntryException if a matching like finder entry could not be found
	 */
	@Override
	public LikeFinderEntry findByC_O_O_LikeP_Last(
			long companyId, long ownerId, int ownerType, String portletId,
			OrderByComparator<LikeFinderEntry> orderByComparator)
		throws NoSuchLikeFinderEntryException {

		LikeFinderEntry likeFinderEntry = fetchByC_O_O_LikeP_Last(
			companyId, ownerId, ownerType, portletId, orderByComparator);

		if (likeFinderEntry != null) {
			return likeFinderEntry;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", ownerId=");
		sb.append(ownerId);

		sb.append(", ownerType=");
		sb.append(ownerType);

		sb.append(", portletIdLIKE");
		sb.append(portletId);

		sb.append("}");

		throw new NoSuchLikeFinderEntryException(sb.toString());
	}

	/**
	 * Returns the last like finder entry in the ordered set where companyId = &#63; and ownerId = &#63; and ownerType = &#63; and portletId LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching like finder entry, or <code>null</code> if a matching like finder entry could not be found
	 */
	@Override
	public LikeFinderEntry fetchByC_O_O_LikeP_Last(
		long companyId, long ownerId, int ownerType, String portletId,
		OrderByComparator<LikeFinderEntry> orderByComparator) {

		int count = countByC_O_O_LikeP(
			companyId, ownerId, ownerType, portletId);

		if (count == 0) {
			return null;
		}

		List<LikeFinderEntry> list = findByC_O_O_LikeP(
			companyId, ownerId, ownerType, portletId, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the like finder entries before and after the current like finder entry in the ordered set where companyId = &#63; and ownerId = &#63; and ownerType = &#63; and portletId LIKE &#63;.
	 *
	 * @param likeFinderEntryId the primary key of the current like finder entry
	 * @param companyId the company ID
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next like finder entry
	 * @throws NoSuchLikeFinderEntryException if a like finder entry with the primary key could not be found
	 */
	@Override
	public LikeFinderEntry[] findByC_O_O_LikeP_PrevAndNext(
			long likeFinderEntryId, long companyId, long ownerId, int ownerType,
			String portletId,
			OrderByComparator<LikeFinderEntry> orderByComparator)
		throws NoSuchLikeFinderEntryException {

		portletId = Objects.toString(portletId, "");

		LikeFinderEntry likeFinderEntry = findByPrimaryKey(likeFinderEntryId);

		Session session = null;

		try {
			session = openSession();

			LikeFinderEntry[] array = new LikeFinderEntryImpl[3];

			array[0] = getByC_O_O_LikeP_PrevAndNext(
				session, likeFinderEntry, companyId, ownerId, ownerType,
				portletId, orderByComparator, true);

			array[1] = likeFinderEntry;

			array[2] = getByC_O_O_LikeP_PrevAndNext(
				session, likeFinderEntry, companyId, ownerId, ownerType,
				portletId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected LikeFinderEntry getByC_O_O_LikeP_PrevAndNext(
		Session session, LikeFinderEntry likeFinderEntry, long companyId,
		long ownerId, int ownerType, String portletId,
		OrderByComparator<LikeFinderEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_LIKEFINDERENTRY_WHERE);

		sb.append(_FINDER_COLUMN_C_O_O_LIKEP_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_O_O_LIKEP_OWNERID_2);

		sb.append(_FINDER_COLUMN_C_O_O_LIKEP_OWNERTYPE_2);

		boolean bindPortletId = false;

		if (portletId.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_O_O_LIKEP_PORTLETID_3);
		}
		else {
			bindPortletId = true;

			sb.append(_FINDER_COLUMN_C_O_O_LIKEP_PORTLETID_2);
		}

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
			sb.append(LikeFinderEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(ownerId);

		queryPos.add(ownerType);

		if (bindPortletId) {
			queryPos.add(portletId);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						likeFinderEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LikeFinderEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the like finder entries where companyId = &#63; and ownerId = &#63; and ownerType = &#63; and portletId LIKE &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 */
	@Override
	public void removeByC_O_O_LikeP(
		long companyId, long ownerId, int ownerType, String portletId) {

		for (LikeFinderEntry likeFinderEntry :
				findByC_O_O_LikeP(
					companyId, ownerId, ownerType, portletId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(likeFinderEntry);
		}
	}

	/**
	 * Returns the number of like finder entries where companyId = &#63; and ownerId = &#63; and ownerType = &#63; and portletId LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @return the number of matching like finder entries
	 */
	@Override
	public int countByC_O_O_LikeP(
		long companyId, long ownerId, int ownerType, String portletId) {

		portletId = Objects.toString(portletId, "");

		FinderPath finderPath = _finderPathWithPaginationCountByC_O_O_LikeP;

		Object[] finderArgs = new Object[] {
			companyId, ownerId, ownerType, portletId
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(5);

			sb.append(_SQL_COUNT_LIKEFINDERENTRY_WHERE);

			sb.append(_FINDER_COLUMN_C_O_O_LIKEP_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_O_O_LIKEP_OWNERID_2);

			sb.append(_FINDER_COLUMN_C_O_O_LIKEP_OWNERTYPE_2);

			boolean bindPortletId = false;

			if (portletId.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_O_O_LIKEP_PORTLETID_3);
			}
			else {
				bindPortletId = true;

				sb.append(_FINDER_COLUMN_C_O_O_LIKEP_PORTLETID_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(ownerId);

				queryPos.add(ownerType);

				if (bindPortletId) {
					queryPos.add(portletId);
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

	private static final String _FINDER_COLUMN_C_O_O_LIKEP_COMPANYID_2 =
		"likeFinderEntry.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_O_O_LIKEP_OWNERID_2 =
		"likeFinderEntry.ownerId = ? AND ";

	private static final String _FINDER_COLUMN_C_O_O_LIKEP_OWNERTYPE_2 =
		"likeFinderEntry.ownerType = ? AND ";

	private static final String _FINDER_COLUMN_C_O_O_LIKEP_PORTLETID_2 =
		"likeFinderEntry.portletId LIKE ?";

	private static final String _FINDER_COLUMN_C_O_O_LIKEP_PORTLETID_3 =
		"(likeFinderEntry.portletId IS NULL OR likeFinderEntry.portletId LIKE '')";

	public LikeFinderEntryPersistenceImpl() {
		setModelClass(LikeFinderEntry.class);

		setModelImplClass(LikeFinderEntryImpl.class);
		setModelPKClass(long.class);
	}

	/**
	 * Caches the like finder entry in the entity cache if it is enabled.
	 *
	 * @param likeFinderEntry the like finder entry
	 */
	@Override
	public void cacheResult(LikeFinderEntry likeFinderEntry) {
		entityCache.putResult(
			LikeFinderEntryImpl.class, likeFinderEntry.getPrimaryKey(),
			likeFinderEntry);

		finderCache.putResult(
			_finderPathFetchByO_O_P,
			new Object[] {
				likeFinderEntry.getOwnerId(), likeFinderEntry.getOwnerType(),
				likeFinderEntry.getPortletId()
			},
			likeFinderEntry);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the like finder entries in the entity cache if it is enabled.
	 *
	 * @param likeFinderEntries the like finder entries
	 */
	@Override
	public void cacheResult(List<LikeFinderEntry> likeFinderEntries) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (likeFinderEntries.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (LikeFinderEntry likeFinderEntry : likeFinderEntries) {
			if (entityCache.getResult(
					LikeFinderEntryImpl.class,
					likeFinderEntry.getPrimaryKey()) == null) {

				cacheResult(likeFinderEntry);
			}
		}
	}

	/**
	 * Clears the cache for all like finder entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(LikeFinderEntryImpl.class);

		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the like finder entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(LikeFinderEntry likeFinderEntry) {
		entityCache.removeResult(LikeFinderEntryImpl.class, likeFinderEntry);
	}

	@Override
	public void clearCache(List<LikeFinderEntry> likeFinderEntries) {
		for (LikeFinderEntry likeFinderEntry : likeFinderEntries) {
			entityCache.removeResult(
				LikeFinderEntryImpl.class, likeFinderEntry);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(LikeFinderEntryImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		LikeFinderEntryModelImpl likeFinderEntryModelImpl) {

		Object[] args = new Object[] {
			likeFinderEntryModelImpl.getOwnerId(),
			likeFinderEntryModelImpl.getOwnerType(),
			likeFinderEntryModelImpl.getPortletId()
		};

		finderCache.putResult(
			_finderPathCountByO_O_P, args, Long.valueOf(1), false);
		finderCache.putResult(
			_finderPathFetchByO_O_P, args, likeFinderEntryModelImpl, false);
	}

	/**
	 * Creates a new like finder entry with the primary key. Does not add the like finder entry to the database.
	 *
	 * @param likeFinderEntryId the primary key for the new like finder entry
	 * @return the new like finder entry
	 */
	@Override
	public LikeFinderEntry create(long likeFinderEntryId) {
		LikeFinderEntry likeFinderEntry = new LikeFinderEntryImpl();

		likeFinderEntry.setNew(true);
		likeFinderEntry.setPrimaryKey(likeFinderEntryId);

		likeFinderEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return likeFinderEntry;
	}

	/**
	 * Removes the like finder entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param likeFinderEntryId the primary key of the like finder entry
	 * @return the like finder entry that was removed
	 * @throws NoSuchLikeFinderEntryException if a like finder entry with the primary key could not be found
	 */
	@Override
	public LikeFinderEntry remove(long likeFinderEntryId)
		throws NoSuchLikeFinderEntryException {

		return remove((Serializable)likeFinderEntryId);
	}

	/**
	 * Removes the like finder entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the like finder entry
	 * @return the like finder entry that was removed
	 * @throws NoSuchLikeFinderEntryException if a like finder entry with the primary key could not be found
	 */
	@Override
	public LikeFinderEntry remove(Serializable primaryKey)
		throws NoSuchLikeFinderEntryException {

		Session session = null;

		try {
			session = openSession();

			LikeFinderEntry likeFinderEntry = (LikeFinderEntry)session.get(
				LikeFinderEntryImpl.class, primaryKey);

			if (likeFinderEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchLikeFinderEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(likeFinderEntry);
		}
		catch (NoSuchLikeFinderEntryException noSuchEntityException) {
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
	protected LikeFinderEntry removeImpl(LikeFinderEntry likeFinderEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(likeFinderEntry)) {
				likeFinderEntry = (LikeFinderEntry)session.get(
					LikeFinderEntryImpl.class,
					likeFinderEntry.getPrimaryKeyObj());
			}

			if (likeFinderEntry != null) {
				session.delete(likeFinderEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (likeFinderEntry != null) {
			clearCache(likeFinderEntry);
		}

		return likeFinderEntry;
	}

	@Override
	public LikeFinderEntry updateImpl(LikeFinderEntry likeFinderEntry) {
		boolean isNew = likeFinderEntry.isNew();

		if (!(likeFinderEntry instanceof LikeFinderEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(likeFinderEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					likeFinderEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in likeFinderEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom LikeFinderEntry implementation " +
					likeFinderEntry.getClass());
		}

		LikeFinderEntryModelImpl likeFinderEntryModelImpl =
			(LikeFinderEntryModelImpl)likeFinderEntry;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(likeFinderEntry);
			}
			else {
				likeFinderEntry = (LikeFinderEntry)session.merge(
					likeFinderEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			LikeFinderEntryImpl.class, likeFinderEntryModelImpl, false, true);

		cacheUniqueFindersCache(likeFinderEntryModelImpl);

		if (isNew) {
			likeFinderEntry.setNew(false);
		}

		likeFinderEntry.resetOriginalValues();

		return likeFinderEntry;
	}

	/**
	 * Returns the like finder entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the like finder entry
	 * @return the like finder entry
	 * @throws NoSuchLikeFinderEntryException if a like finder entry with the primary key could not be found
	 */
	@Override
	public LikeFinderEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchLikeFinderEntryException {

		LikeFinderEntry likeFinderEntry = fetchByPrimaryKey(primaryKey);

		if (likeFinderEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchLikeFinderEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return likeFinderEntry;
	}

	/**
	 * Returns the like finder entry with the primary key or throws a <code>NoSuchLikeFinderEntryException</code> if it could not be found.
	 *
	 * @param likeFinderEntryId the primary key of the like finder entry
	 * @return the like finder entry
	 * @throws NoSuchLikeFinderEntryException if a like finder entry with the primary key could not be found
	 */
	@Override
	public LikeFinderEntry findByPrimaryKey(long likeFinderEntryId)
		throws NoSuchLikeFinderEntryException {

		return findByPrimaryKey((Serializable)likeFinderEntryId);
	}

	/**
	 * Returns the like finder entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param likeFinderEntryId the primary key of the like finder entry
	 * @return the like finder entry, or <code>null</code> if a like finder entry with the primary key could not be found
	 */
	@Override
	public LikeFinderEntry fetchByPrimaryKey(long likeFinderEntryId) {
		return fetchByPrimaryKey((Serializable)likeFinderEntryId);
	}

	/**
	 * Returns all the like finder entries.
	 *
	 * @return the like finder entries
	 */
	@Override
	public List<LikeFinderEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the like finder entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LikeFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of like finder entries
	 * @param end the upper bound of the range of like finder entries (not inclusive)
	 * @return the range of like finder entries
	 */
	@Override
	public List<LikeFinderEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the like finder entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LikeFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of like finder entries
	 * @param end the upper bound of the range of like finder entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of like finder entries
	 */
	@Override
	public List<LikeFinderEntry> findAll(
		int start, int end,
		OrderByComparator<LikeFinderEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the like finder entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LikeFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of like finder entries
	 * @param end the upper bound of the range of like finder entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of like finder entries
	 */
	@Override
	public List<LikeFinderEntry> findAll(
		int start, int end,
		OrderByComparator<LikeFinderEntry> orderByComparator,
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

		List<LikeFinderEntry> list = null;

		if (useFinderCache) {
			list = (List<LikeFinderEntry>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_LIKEFINDERENTRY);

				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_LIKEFINDERENTRY;

				sql = sql.concat(LikeFinderEntryModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<LikeFinderEntry>)QueryUtil.list(
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
	 * Removes all the like finder entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (LikeFinderEntry likeFinderEntry : findAll()) {
			remove(likeFinderEntry);
		}
	}

	/**
	 * Returns the number of like finder entries.
	 *
	 * @return the number of like finder entries
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
					"SELECT COUNT(likeFinderEntry) FROM LikeFinderEntry likeFinderEntry");

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
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "likeFinderEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LIKEFINDERENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return LikeFinderEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the like finder entry persistence.
	 */
	public void afterPropertiesSet() {
		Bundle bundle = FrameworkUtil.getBundle(
			LikeFinderEntryPersistenceImpl.class);

		_bundleContext = bundle.getBundleContext();

		_argumentsResolverServiceRegistration = _bundleContext.registerService(
			ArgumentsResolver.class,
			new LikeFinderEntryModelArgumentsResolver(),
			MapUtil.singletonDictionary(
				"model.class.name", LikeFinderEntry.class.getName()));

		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		_finderPathWithPaginationFindAll = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathWithoutPaginationFindAll = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathCountAll = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0], new String[0], false);

		_finderPathFetchByO_O_P = _createFinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByO_O_P",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				String.class.getName()
			},
			new String[] {"ownerId", "ownerType", "portletId"}, true);

		_finderPathCountByO_O_P = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByO_O_P",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				String.class.getName()
			},
			new String[] {"ownerId", "ownerType", "portletId"}, false);

		_finderPathWithPaginationFindByC_O_O_LikeP = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_O_O_LikeP",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "ownerId", "ownerType", "portletId"},
			true);

		_finderPathWithPaginationCountByC_O_O_LikeP = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_O_O_LikeP",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), String.class.getName()
			},
			new String[] {"companyId", "ownerId", "ownerType", "portletId"},
			false);

		LikeFinderEntryUtil.setPersistence(this);
	}

	public void destroy() {
		LikeFinderEntryUtil.setPersistence(null);

		entityCache.removeCache(LikeFinderEntryImpl.class.getName());

		_argumentsResolverServiceRegistration.unregister();

		for (ServiceRegistration<FinderPath> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}
	}

	private BundleContext _bundleContext;

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		LikeFinderEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_LIKEFINDERENTRY =
		"SELECT likeFinderEntry FROM LikeFinderEntry likeFinderEntry";

	private static final String _SQL_SELECT_LIKEFINDERENTRY_WHERE =
		"SELECT likeFinderEntry FROM LikeFinderEntry likeFinderEntry WHERE ";

	private static final String _SQL_COUNT_LIKEFINDERENTRY_WHERE =
		"SELECT COUNT(likeFinderEntry) FROM LikeFinderEntry likeFinderEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No LikeFinderEntry exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No LikeFinderEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		LikeFinderEntryPersistenceImpl.class);

	private FinderPath _createFinderPath(
		String cacheName, String methodName, String[] params,
		String[] columnNames, boolean baseModelResult) {

		FinderPath finderPath = new FinderPath(
			cacheName, methodName, params, columnNames, baseModelResult);

		if (!cacheName.equals(FINDER_CLASS_NAME_LIST_WITH_PAGINATION)) {
			_serviceRegistrations.add(
				_bundleContext.registerService(
					FinderPath.class, finderPath,
					MapUtil.singletonDictionary("cache.name", cacheName)));
		}

		return finderPath;
	}

	private Set<ServiceRegistration<FinderPath>> _serviceRegistrations =
		new HashSet<>();
	private ServiceRegistration<ArgumentsResolver>
		_argumentsResolverServiceRegistration;

	private static class LikeFinderEntryModelArgumentsResolver
		implements ArgumentsResolver {

		@Override
		public Object[] getArguments(
			FinderPath finderPath, BaseModel<?> baseModel, boolean checkColumn,
			boolean original) {

			String[] columnNames = finderPath.getColumnNames();

			if ((columnNames == null) || (columnNames.length == 0)) {
				if (baseModel.isNew()) {
					return new Object[0];
				}

				return null;
			}

			LikeFinderEntryModelImpl likeFinderEntryModelImpl =
				(LikeFinderEntryModelImpl)baseModel;

			long columnBitmask = likeFinderEntryModelImpl.getColumnBitmask();

			if (!checkColumn || (columnBitmask == 0)) {
				return _getValue(
					likeFinderEntryModelImpl, columnNames, original);
			}

			Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
				finderPath);

			if (finderPathColumnBitmask == null) {
				finderPathColumnBitmask = 0L;

				for (String columnName : columnNames) {
					finderPathColumnBitmask |=
						likeFinderEntryModelImpl.getColumnBitmask(columnName);
				}

				_finderPathColumnBitmasksCache.put(
					finderPath, finderPathColumnBitmask);
			}

			if ((columnBitmask & finderPathColumnBitmask) != 0) {
				return _getValue(
					likeFinderEntryModelImpl, columnNames, original);
			}

			return null;
		}

		private static Object[] _getValue(
			LikeFinderEntryModelImpl likeFinderEntryModelImpl,
			String[] columnNames, boolean original) {

			Object[] arguments = new Object[columnNames.length];

			for (int i = 0; i < arguments.length; i++) {
				String columnName = columnNames[i];

				if (original) {
					arguments[i] =
						likeFinderEntryModelImpl.getColumnOriginalValue(
							columnName);
				}
				else {
					arguments[i] = likeFinderEntryModelImpl.getColumnValue(
						columnName);
				}
			}

			return arguments;
		}

		private static final Map<FinderPath, Long>
			_finderPathColumnBitmasksCache = new ConcurrentHashMap<>();

	}

}
// LIFERAY-SERVICE-BUILDER-HASH:708804996