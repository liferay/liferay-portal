/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.forms.service.persistence.impl;

import com.liferay.petra.string.StringBundler;
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
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.workflow.kaleo.forms.exception.NoSuchKaleoProcessException;
import com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess;
import com.liferay.portal.workflow.kaleo.forms.model.KaleoProcessTable;
import com.liferay.portal.workflow.kaleo.forms.model.impl.KaleoProcessImpl;
import com.liferay.portal.workflow.kaleo.forms.model.impl.KaleoProcessModelImpl;
import com.liferay.portal.workflow.kaleo.forms.service.persistence.KaleoProcessPersistence;
import com.liferay.portal.workflow.kaleo.forms.service.persistence.KaleoProcessUtil;
import com.liferay.portal.workflow.kaleo.forms.service.persistence.impl.constants.KaleoFormsPersistenceConstants;

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
 * The persistence implementation for the kaleo process service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marcellus Tavares
 * @generated
 */
@Component(service = KaleoProcessPersistence.class)
public class KaleoProcessPersistenceImpl
	extends BasePersistenceImpl<KaleoProcess>
	implements KaleoProcessPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>KaleoProcessUtil</code> to access the kaleo process persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		KaleoProcessImpl.class.getName();

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
	 * Returns all the kaleo processes where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching kaleo processes
	 */
	@Override
	public List<KaleoProcess> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kaleo processes where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoProcessModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of kaleo processes
	 * @param end the upper bound of the range of kaleo processes (not inclusive)
	 * @return the range of matching kaleo processes
	 */
	@Override
	public List<KaleoProcess> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kaleo processes where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoProcessModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of kaleo processes
	 * @param end the upper bound of the range of kaleo processes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kaleo processes
	 */
	@Override
	public List<KaleoProcess> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<KaleoProcess> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the kaleo processes where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoProcessModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of kaleo processes
	 * @param end the upper bound of the range of kaleo processes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo processes
	 */
	@Override
	public List<KaleoProcess> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<KaleoProcess> orderByComparator,
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

		List<KaleoProcess> list = null;

		if (useFinderCache) {
			list = (List<KaleoProcess>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (KaleoProcess kaleoProcess : list) {
					if (!uuid.equals(kaleoProcess.getUuid())) {
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

			sb.append(_SQL_SELECT_KALEOPROCESS_WHERE);

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
				sb.append(KaleoProcessModelImpl.ORDER_BY_JPQL);
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

				list = (List<KaleoProcess>)QueryUtil.list(
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
	 * Returns the first kaleo process in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo process
	 * @throws NoSuchKaleoProcessException if a matching kaleo process could not be found
	 */
	@Override
	public KaleoProcess findByUuid_First(
			String uuid, OrderByComparator<KaleoProcess> orderByComparator)
		throws NoSuchKaleoProcessException {

		KaleoProcess kaleoProcess = fetchByUuid_First(uuid, orderByComparator);

		if (kaleoProcess != null) {
			return kaleoProcess;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchKaleoProcessException(sb.toString());
	}

	/**
	 * Returns the first kaleo process in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo process, or <code>null</code> if a matching kaleo process could not be found
	 */
	@Override
	public KaleoProcess fetchByUuid_First(
		String uuid, OrderByComparator<KaleoProcess> orderByComparator) {

		List<KaleoProcess> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last kaleo process in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kaleo process
	 * @throws NoSuchKaleoProcessException if a matching kaleo process could not be found
	 */
	@Override
	public KaleoProcess findByUuid_Last(
			String uuid, OrderByComparator<KaleoProcess> orderByComparator)
		throws NoSuchKaleoProcessException {

		KaleoProcess kaleoProcess = fetchByUuid_Last(uuid, orderByComparator);

		if (kaleoProcess != null) {
			return kaleoProcess;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchKaleoProcessException(sb.toString());
	}

	/**
	 * Returns the last kaleo process in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kaleo process, or <code>null</code> if a matching kaleo process could not be found
	 */
	@Override
	public KaleoProcess fetchByUuid_Last(
		String uuid, OrderByComparator<KaleoProcess> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<KaleoProcess> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the kaleo processes before and after the current kaleo process in the ordered set where uuid = &#63;.
	 *
	 * @param kaleoProcessId the primary key of the current kaleo process
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kaleo process
	 * @throws NoSuchKaleoProcessException if a kaleo process with the primary key could not be found
	 */
	@Override
	public KaleoProcess[] findByUuid_PrevAndNext(
			long kaleoProcessId, String uuid,
			OrderByComparator<KaleoProcess> orderByComparator)
		throws NoSuchKaleoProcessException {

		uuid = Objects.toString(uuid, "");

		KaleoProcess kaleoProcess = findByPrimaryKey(kaleoProcessId);

		Session session = null;

		try {
			session = openSession();

			KaleoProcess[] array = new KaleoProcessImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, kaleoProcess, uuid, orderByComparator, true);

			array[1] = kaleoProcess;

			array[2] = getByUuid_PrevAndNext(
				session, kaleoProcess, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected KaleoProcess getByUuid_PrevAndNext(
		Session session, KaleoProcess kaleoProcess, String uuid,
		OrderByComparator<KaleoProcess> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_KALEOPROCESS_WHERE);

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
			sb.append(KaleoProcessModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(kaleoProcess)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<KaleoProcess> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the kaleo processes where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (KaleoProcess kaleoProcess :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(kaleoProcess);
		}
	}

	/**
	 * Returns the number of kaleo processes where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching kaleo processes
	 */
	@Override
	public int countByUuid(String uuid) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid;

		Object[] finderArgs = new Object[] {uuid};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_KALEOPROCESS_WHERE);

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
		"kaleoProcess.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(kaleoProcess.uuid IS NULL OR kaleoProcess.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;

	/**
	 * Returns the kaleo process where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchKaleoProcessException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching kaleo process
	 * @throws NoSuchKaleoProcessException if a matching kaleo process could not be found
	 */
	@Override
	public KaleoProcess findByUUID_G(String uuid, long groupId)
		throws NoSuchKaleoProcessException {

		KaleoProcess kaleoProcess = fetchByUUID_G(uuid, groupId);

		if (kaleoProcess == null) {
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

			throw new NoSuchKaleoProcessException(sb.toString());
		}

		return kaleoProcess;
	}

	/**
	 * Returns the kaleo process where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching kaleo process, or <code>null</code> if a matching kaleo process could not be found
	 */
	@Override
	public KaleoProcess fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the kaleo process where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kaleo process, or <code>null</code> if a matching kaleo process could not be found
	 */
	@Override
	public KaleoProcess fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

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

		if (result instanceof KaleoProcess) {
			KaleoProcess kaleoProcess = (KaleoProcess)result;

			if (!Objects.equals(uuid, kaleoProcess.getUuid()) ||
				(groupId != kaleoProcess.getGroupId())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_KALEOPROCESS_WHERE);

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

				List<KaleoProcess> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByUUID_G, finderArgs, list);
					}
				}
				else {
					KaleoProcess kaleoProcess = list.get(0);

					result = kaleoProcess;

					cacheResult(kaleoProcess);
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
			return (KaleoProcess)result;
		}
	}

	/**
	 * Removes the kaleo process where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the kaleo process that was removed
	 */
	@Override
	public KaleoProcess removeByUUID_G(String uuid, long groupId)
		throws NoSuchKaleoProcessException {

		KaleoProcess kaleoProcess = findByUUID_G(uuid, groupId);

		return remove(kaleoProcess);
	}

	/**
	 * Returns the number of kaleo processes where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching kaleo processes
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		KaleoProcess kaleoProcess = fetchByUUID_G(uuid, groupId);

		if (kaleoProcess == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"kaleoProcess.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(kaleoProcess.uuid IS NULL OR kaleoProcess.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"kaleoProcess.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the kaleo processes where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching kaleo processes
	 */
	@Override
	public List<KaleoProcess> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kaleo processes where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoProcessModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kaleo processes
	 * @param end the upper bound of the range of kaleo processes (not inclusive)
	 * @return the range of matching kaleo processes
	 */
	@Override
	public List<KaleoProcess> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kaleo processes where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoProcessModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kaleo processes
	 * @param end the upper bound of the range of kaleo processes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kaleo processes
	 */
	@Override
	public List<KaleoProcess> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<KaleoProcess> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the kaleo processes where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoProcessModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kaleo processes
	 * @param end the upper bound of the range of kaleo processes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo processes
	 */
	@Override
	public List<KaleoProcess> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<KaleoProcess> orderByComparator,
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

		List<KaleoProcess> list = null;

		if (useFinderCache) {
			list = (List<KaleoProcess>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (KaleoProcess kaleoProcess : list) {
					if (!uuid.equals(kaleoProcess.getUuid()) ||
						(companyId != kaleoProcess.getCompanyId())) {

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

			sb.append(_SQL_SELECT_KALEOPROCESS_WHERE);

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
				sb.append(KaleoProcessModelImpl.ORDER_BY_JPQL);
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

				list = (List<KaleoProcess>)QueryUtil.list(
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
	 * Returns the first kaleo process in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo process
	 * @throws NoSuchKaleoProcessException if a matching kaleo process could not be found
	 */
	@Override
	public KaleoProcess findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<KaleoProcess> orderByComparator)
		throws NoSuchKaleoProcessException {

		KaleoProcess kaleoProcess = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (kaleoProcess != null) {
			return kaleoProcess;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchKaleoProcessException(sb.toString());
	}

	/**
	 * Returns the first kaleo process in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo process, or <code>null</code> if a matching kaleo process could not be found
	 */
	@Override
	public KaleoProcess fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<KaleoProcess> orderByComparator) {

		List<KaleoProcess> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last kaleo process in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kaleo process
	 * @throws NoSuchKaleoProcessException if a matching kaleo process could not be found
	 */
	@Override
	public KaleoProcess findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<KaleoProcess> orderByComparator)
		throws NoSuchKaleoProcessException {

		KaleoProcess kaleoProcess = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (kaleoProcess != null) {
			return kaleoProcess;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchKaleoProcessException(sb.toString());
	}

	/**
	 * Returns the last kaleo process in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kaleo process, or <code>null</code> if a matching kaleo process could not be found
	 */
	@Override
	public KaleoProcess fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<KaleoProcess> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<KaleoProcess> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the kaleo processes before and after the current kaleo process in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param kaleoProcessId the primary key of the current kaleo process
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kaleo process
	 * @throws NoSuchKaleoProcessException if a kaleo process with the primary key could not be found
	 */
	@Override
	public KaleoProcess[] findByUuid_C_PrevAndNext(
			long kaleoProcessId, String uuid, long companyId,
			OrderByComparator<KaleoProcess> orderByComparator)
		throws NoSuchKaleoProcessException {

		uuid = Objects.toString(uuid, "");

		KaleoProcess kaleoProcess = findByPrimaryKey(kaleoProcessId);

		Session session = null;

		try {
			session = openSession();

			KaleoProcess[] array = new KaleoProcessImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, kaleoProcess, uuid, companyId, orderByComparator,
				true);

			array[1] = kaleoProcess;

			array[2] = getByUuid_C_PrevAndNext(
				session, kaleoProcess, uuid, companyId, orderByComparator,
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

	protected KaleoProcess getByUuid_C_PrevAndNext(
		Session session, KaleoProcess kaleoProcess, String uuid, long companyId,
		OrderByComparator<KaleoProcess> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_KALEOPROCESS_WHERE);

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
			sb.append(KaleoProcessModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(kaleoProcess)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<KaleoProcess> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the kaleo processes where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (KaleoProcess kaleoProcess :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(kaleoProcess);
		}
	}

	/**
	 * Returns the number of kaleo processes where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching kaleo processes
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid_C;

		Object[] finderArgs = new Object[] {uuid, companyId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_KALEOPROCESS_WHERE);

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
		"kaleoProcess.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(kaleoProcess.uuid IS NULL OR kaleoProcess.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"kaleoProcess.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByGroupId;
	private FinderPath _finderPathWithoutPaginationFindByGroupId;
	private FinderPath _finderPathCountByGroupId;

	/**
	 * Returns all the kaleo processes where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching kaleo processes
	 */
	@Override
	public List<KaleoProcess> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kaleo processes where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoProcessModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of kaleo processes
	 * @param end the upper bound of the range of kaleo processes (not inclusive)
	 * @return the range of matching kaleo processes
	 */
	@Override
	public List<KaleoProcess> findByGroupId(long groupId, int start, int end) {
		return findByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kaleo processes where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoProcessModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of kaleo processes
	 * @param end the upper bound of the range of kaleo processes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kaleo processes
	 */
	@Override
	public List<KaleoProcess> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<KaleoProcess> orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the kaleo processes where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoProcessModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of kaleo processes
	 * @param end the upper bound of the range of kaleo processes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo processes
	 */
	@Override
	public List<KaleoProcess> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<KaleoProcess> orderByComparator,
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

		List<KaleoProcess> list = null;

		if (useFinderCache) {
			list = (List<KaleoProcess>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (KaleoProcess kaleoProcess : list) {
					if (groupId != kaleoProcess.getGroupId()) {
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

			sb.append(_SQL_SELECT_KALEOPROCESS_WHERE);

			sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(KaleoProcessModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				list = (List<KaleoProcess>)QueryUtil.list(
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
	 * Returns the first kaleo process in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo process
	 * @throws NoSuchKaleoProcessException if a matching kaleo process could not be found
	 */
	@Override
	public KaleoProcess findByGroupId_First(
			long groupId, OrderByComparator<KaleoProcess> orderByComparator)
		throws NoSuchKaleoProcessException {

		KaleoProcess kaleoProcess = fetchByGroupId_First(
			groupId, orderByComparator);

		if (kaleoProcess != null) {
			return kaleoProcess;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchKaleoProcessException(sb.toString());
	}

	/**
	 * Returns the first kaleo process in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo process, or <code>null</code> if a matching kaleo process could not be found
	 */
	@Override
	public KaleoProcess fetchByGroupId_First(
		long groupId, OrderByComparator<KaleoProcess> orderByComparator) {

		List<KaleoProcess> list = findByGroupId(
			groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last kaleo process in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kaleo process
	 * @throws NoSuchKaleoProcessException if a matching kaleo process could not be found
	 */
	@Override
	public KaleoProcess findByGroupId_Last(
			long groupId, OrderByComparator<KaleoProcess> orderByComparator)
		throws NoSuchKaleoProcessException {

		KaleoProcess kaleoProcess = fetchByGroupId_Last(
			groupId, orderByComparator);

		if (kaleoProcess != null) {
			return kaleoProcess;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchKaleoProcessException(sb.toString());
	}

	/**
	 * Returns the last kaleo process in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kaleo process, or <code>null</code> if a matching kaleo process could not be found
	 */
	@Override
	public KaleoProcess fetchByGroupId_Last(
		long groupId, OrderByComparator<KaleoProcess> orderByComparator) {

		int count = countByGroupId(groupId);

		if (count == 0) {
			return null;
		}

		List<KaleoProcess> list = findByGroupId(
			groupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the kaleo processes before and after the current kaleo process in the ordered set where groupId = &#63;.
	 *
	 * @param kaleoProcessId the primary key of the current kaleo process
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kaleo process
	 * @throws NoSuchKaleoProcessException if a kaleo process with the primary key could not be found
	 */
	@Override
	public KaleoProcess[] findByGroupId_PrevAndNext(
			long kaleoProcessId, long groupId,
			OrderByComparator<KaleoProcess> orderByComparator)
		throws NoSuchKaleoProcessException {

		KaleoProcess kaleoProcess = findByPrimaryKey(kaleoProcessId);

		Session session = null;

		try {
			session = openSession();

			KaleoProcess[] array = new KaleoProcessImpl[3];

			array[0] = getByGroupId_PrevAndNext(
				session, kaleoProcess, groupId, orderByComparator, true);

			array[1] = kaleoProcess;

			array[2] = getByGroupId_PrevAndNext(
				session, kaleoProcess, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected KaleoProcess getByGroupId_PrevAndNext(
		Session session, KaleoProcess kaleoProcess, long groupId,
		OrderByComparator<KaleoProcess> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_KALEOPROCESS_WHERE);

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
			sb.append(KaleoProcessModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(kaleoProcess)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<KaleoProcess> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the kaleo processes that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching kaleo processes that the user has permission to view
	 */
	@Override
	public List<KaleoProcess> filterFindByGroupId(long groupId) {
		return filterFindByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kaleo processes that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoProcessModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of kaleo processes
	 * @param end the upper bound of the range of kaleo processes (not inclusive)
	 * @return the range of matching kaleo processes that the user has permission to view
	 */
	@Override
	public List<KaleoProcess> filterFindByGroupId(
		long groupId, int start, int end) {

		return filterFindByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the kaleo processes that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoProcessModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of kaleo processes
	 * @param end the upper bound of the range of kaleo processes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kaleo processes that the user has permission to view
	 */
	@Override
	public List<KaleoProcess> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<KaleoProcess> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_KALEOPROCESS_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_KALEOPROCESS_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_KALEOPROCESS_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(KaleoProcessModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(KaleoProcessModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), KaleoProcess.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, KaleoProcessImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, KaleoProcessImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			return (List<KaleoProcess>)QueryUtil.list(
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
	 * Returns the kaleo processes before and after the current kaleo process in the ordered set of kaleo processes that the user has permission to view where groupId = &#63;.
	 *
	 * @param kaleoProcessId the primary key of the current kaleo process
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kaleo process
	 * @throws NoSuchKaleoProcessException if a kaleo process with the primary key could not be found
	 */
	@Override
	public KaleoProcess[] filterFindByGroupId_PrevAndNext(
			long kaleoProcessId, long groupId,
			OrderByComparator<KaleoProcess> orderByComparator)
		throws NoSuchKaleoProcessException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByGroupId_PrevAndNext(
				kaleoProcessId, groupId, orderByComparator);
		}

		KaleoProcess kaleoProcess = findByPrimaryKey(kaleoProcessId);

		Session session = null;

		try {
			session = openSession();

			KaleoProcess[] array = new KaleoProcessImpl[3];

			array[0] = filterGetByGroupId_PrevAndNext(
				session, kaleoProcess, groupId, orderByComparator, true);

			array[1] = kaleoProcess;

			array[2] = filterGetByGroupId_PrevAndNext(
				session, kaleoProcess, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected KaleoProcess filterGetByGroupId_PrevAndNext(
		Session session, KaleoProcess kaleoProcess, long groupId,
		OrderByComparator<KaleoProcess> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_KALEOPROCESS_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_KALEOPROCESS_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_KALEOPROCESS_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(KaleoProcessModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(KaleoProcessModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), KaleoProcess.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, KaleoProcessImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, KaleoProcessImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(kaleoProcess)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<KaleoProcess> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the kaleo processes where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		for (KaleoProcess kaleoProcess :
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(kaleoProcess);
		}
	}

	/**
	 * Returns the number of kaleo processes where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching kaleo processes
	 */
	@Override
	public int countByGroupId(long groupId) {
		FinderPath finderPath = _finderPathCountByGroupId;

		Object[] finderArgs = new Object[] {groupId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_KALEOPROCESS_WHERE);

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

	/**
	 * Returns the number of kaleo processes that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching kaleo processes that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByGroupId(groupId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<KaleoProcess> kaleoProcesses = findByGroupId(groupId);

			kaleoProcesses = InlineSQLHelperUtil.filter(
				kaleoProcesses, groupId);

			return kaleoProcesses.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_KALEOPROCESS_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), KaleoProcess.class.getName(),
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
		"kaleoProcess.groupId = ?";

	private FinderPath _finderPathFetchByDDLRecordSetId;

	/**
	 * Returns the kaleo process where DDLRecordSetId = &#63; or throws a <code>NoSuchKaleoProcessException</code> if it could not be found.
	 *
	 * @param DDLRecordSetId the ddl record set ID
	 * @return the matching kaleo process
	 * @throws NoSuchKaleoProcessException if a matching kaleo process could not be found
	 */
	@Override
	public KaleoProcess findByDDLRecordSetId(long DDLRecordSetId)
		throws NoSuchKaleoProcessException {

		KaleoProcess kaleoProcess = fetchByDDLRecordSetId(DDLRecordSetId);

		if (kaleoProcess == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("DDLRecordSetId=");
			sb.append(DDLRecordSetId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchKaleoProcessException(sb.toString());
		}

		return kaleoProcess;
	}

	/**
	 * Returns the kaleo process where DDLRecordSetId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param DDLRecordSetId the ddl record set ID
	 * @return the matching kaleo process, or <code>null</code> if a matching kaleo process could not be found
	 */
	@Override
	public KaleoProcess fetchByDDLRecordSetId(long DDLRecordSetId) {
		return fetchByDDLRecordSetId(DDLRecordSetId, true);
	}

	/**
	 * Returns the kaleo process where DDLRecordSetId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param DDLRecordSetId the ddl record set ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kaleo process, or <code>null</code> if a matching kaleo process could not be found
	 */
	@Override
	public KaleoProcess fetchByDDLRecordSetId(
		long DDLRecordSetId, boolean useFinderCache) {

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {DDLRecordSetId};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByDDLRecordSetId, finderArgs, this);
		}

		if (result instanceof KaleoProcess) {
			KaleoProcess kaleoProcess = (KaleoProcess)result;

			if (DDLRecordSetId != kaleoProcess.getDDLRecordSetId()) {
				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_SELECT_KALEOPROCESS_WHERE);

			sb.append(_FINDER_COLUMN_DDLRECORDSETID_DDLRECORDSETID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(DDLRecordSetId);

				List<KaleoProcess> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByDDLRecordSetId, finderArgs, list);
					}
				}
				else {
					if (list.size() > 1) {
						Collections.sort(list, Collections.reverseOrder());

						if (_log.isWarnEnabled()) {
							if (!useFinderCache) {
								finderArgs = new Object[] {DDLRecordSetId};
							}

							_log.warn(
								"KaleoProcessPersistenceImpl.fetchByDDLRecordSetId(long, boolean) with parameters (" +
									StringUtil.merge(finderArgs) +
										") yields a result set with more than 1 result. This violates the logical unique restriction. There is no order guarantee on which result is returned by this finder.");
						}
					}

					KaleoProcess kaleoProcess = list.get(0);

					result = kaleoProcess;

					cacheResult(kaleoProcess);
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
			return (KaleoProcess)result;
		}
	}

	/**
	 * Removes the kaleo process where DDLRecordSetId = &#63; from the database.
	 *
	 * @param DDLRecordSetId the ddl record set ID
	 * @return the kaleo process that was removed
	 */
	@Override
	public KaleoProcess removeByDDLRecordSetId(long DDLRecordSetId)
		throws NoSuchKaleoProcessException {

		KaleoProcess kaleoProcess = findByDDLRecordSetId(DDLRecordSetId);

		return remove(kaleoProcess);
	}

	/**
	 * Returns the number of kaleo processes where DDLRecordSetId = &#63;.
	 *
	 * @param DDLRecordSetId the ddl record set ID
	 * @return the number of matching kaleo processes
	 */
	@Override
	public int countByDDLRecordSetId(long DDLRecordSetId) {
		KaleoProcess kaleoProcess = fetchByDDLRecordSetId(DDLRecordSetId);

		if (kaleoProcess == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_DDLRECORDSETID_DDLRECORDSETID_2 =
		"kaleoProcess.DDLRecordSetId = ?";

	public KaleoProcessPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(KaleoProcess.class);

		setModelImplClass(KaleoProcessImpl.class);
		setModelPKClass(long.class);

		setTable(KaleoProcessTable.INSTANCE);
	}

	/**
	 * Caches the kaleo process in the entity cache if it is enabled.
	 *
	 * @param kaleoProcess the kaleo process
	 */
	@Override
	public void cacheResult(KaleoProcess kaleoProcess) {
		entityCache.putResult(
			KaleoProcessImpl.class, kaleoProcess.getPrimaryKey(), kaleoProcess);

		finderCache.putResult(
			_finderPathFetchByUUID_G,
			new Object[] {kaleoProcess.getUuid(), kaleoProcess.getGroupId()},
			kaleoProcess);

		finderCache.putResult(
			_finderPathFetchByDDLRecordSetId,
			new Object[] {kaleoProcess.getDDLRecordSetId()}, kaleoProcess);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the kaleo processes in the entity cache if it is enabled.
	 *
	 * @param kaleoProcesses the kaleo processes
	 */
	@Override
	public void cacheResult(List<KaleoProcess> kaleoProcesses) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (kaleoProcesses.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (KaleoProcess kaleoProcess : kaleoProcesses) {
			if (entityCache.getResult(
					KaleoProcessImpl.class, kaleoProcess.getPrimaryKey()) ==
						null) {

				cacheResult(kaleoProcess);
			}
		}
	}

	/**
	 * Clears the cache for all kaleo processes.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(KaleoProcessImpl.class);

		finderCache.clearCache(KaleoProcessImpl.class);
	}

	/**
	 * Clears the cache for the kaleo process.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(KaleoProcess kaleoProcess) {
		entityCache.removeResult(KaleoProcessImpl.class, kaleoProcess);
	}

	@Override
	public void clearCache(List<KaleoProcess> kaleoProcesses) {
		for (KaleoProcess kaleoProcess : kaleoProcesses) {
			entityCache.removeResult(KaleoProcessImpl.class, kaleoProcess);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(KaleoProcessImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(KaleoProcessImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		KaleoProcessModelImpl kaleoProcessModelImpl) {

		Object[] args = new Object[] {
			kaleoProcessModelImpl.getUuid(), kaleoProcessModelImpl.getGroupId()
		};

		finderCache.putResult(
			_finderPathFetchByUUID_G, args, kaleoProcessModelImpl);

		args = new Object[] {kaleoProcessModelImpl.getDDLRecordSetId()};

		finderCache.putResult(
			_finderPathFetchByDDLRecordSetId, args, kaleoProcessModelImpl);
	}

	/**
	 * Creates a new kaleo process with the primary key. Does not add the kaleo process to the database.
	 *
	 * @param kaleoProcessId the primary key for the new kaleo process
	 * @return the new kaleo process
	 */
	@Override
	public KaleoProcess create(long kaleoProcessId) {
		KaleoProcess kaleoProcess = new KaleoProcessImpl();

		kaleoProcess.setNew(true);
		kaleoProcess.setPrimaryKey(kaleoProcessId);

		String uuid = PortalUUIDUtil.generate();

		kaleoProcess.setUuid(uuid);

		kaleoProcess.setCompanyId(CompanyThreadLocal.getCompanyId());

		return kaleoProcess;
	}

	/**
	 * Removes the kaleo process with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kaleoProcessId the primary key of the kaleo process
	 * @return the kaleo process that was removed
	 * @throws NoSuchKaleoProcessException if a kaleo process with the primary key could not be found
	 */
	@Override
	public KaleoProcess remove(long kaleoProcessId)
		throws NoSuchKaleoProcessException {

		return remove((Serializable)kaleoProcessId);
	}

	/**
	 * Removes the kaleo process with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the kaleo process
	 * @return the kaleo process that was removed
	 * @throws NoSuchKaleoProcessException if a kaleo process with the primary key could not be found
	 */
	@Override
	public KaleoProcess remove(Serializable primaryKey)
		throws NoSuchKaleoProcessException {

		Session session = null;

		try {
			session = openSession();

			KaleoProcess kaleoProcess = (KaleoProcess)session.get(
				KaleoProcessImpl.class, primaryKey);

			if (kaleoProcess == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchKaleoProcessException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(kaleoProcess);
		}
		catch (NoSuchKaleoProcessException noSuchEntityException) {
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
	protected KaleoProcess removeImpl(KaleoProcess kaleoProcess) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(kaleoProcess)) {
				kaleoProcess = (KaleoProcess)session.get(
					KaleoProcessImpl.class, kaleoProcess.getPrimaryKeyObj());
			}

			if (kaleoProcess != null) {
				session.delete(kaleoProcess);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (kaleoProcess != null) {
			clearCache(kaleoProcess);
		}

		return kaleoProcess;
	}

	@Override
	public KaleoProcess updateImpl(KaleoProcess kaleoProcess) {
		boolean isNew = kaleoProcess.isNew();

		if (!(kaleoProcess instanceof KaleoProcessModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(kaleoProcess.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					kaleoProcess);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in kaleoProcess proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom KaleoProcess implementation " +
					kaleoProcess.getClass());
		}

		KaleoProcessModelImpl kaleoProcessModelImpl =
			(KaleoProcessModelImpl)kaleoProcess;

		if (Validator.isNull(kaleoProcess.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			kaleoProcess.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (kaleoProcess.getCreateDate() == null)) {
			if (serviceContext == null) {
				kaleoProcess.setCreateDate(date);
			}
			else {
				kaleoProcess.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!kaleoProcessModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				kaleoProcess.setModifiedDate(date);
			}
			else {
				kaleoProcess.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(kaleoProcess);
			}
			else {
				kaleoProcess = (KaleoProcess)session.merge(kaleoProcess);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			KaleoProcessImpl.class, kaleoProcessModelImpl, false, true);

		cacheUniqueFindersCache(kaleoProcessModelImpl);

		if (isNew) {
			kaleoProcess.setNew(false);
		}

		kaleoProcess.resetOriginalValues();

		return kaleoProcess;
	}

	/**
	 * Returns the kaleo process with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the kaleo process
	 * @return the kaleo process
	 * @throws NoSuchKaleoProcessException if a kaleo process with the primary key could not be found
	 */
	@Override
	public KaleoProcess findByPrimaryKey(Serializable primaryKey)
		throws NoSuchKaleoProcessException {

		KaleoProcess kaleoProcess = fetchByPrimaryKey(primaryKey);

		if (kaleoProcess == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchKaleoProcessException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return kaleoProcess;
	}

	/**
	 * Returns the kaleo process with the primary key or throws a <code>NoSuchKaleoProcessException</code> if it could not be found.
	 *
	 * @param kaleoProcessId the primary key of the kaleo process
	 * @return the kaleo process
	 * @throws NoSuchKaleoProcessException if a kaleo process with the primary key could not be found
	 */
	@Override
	public KaleoProcess findByPrimaryKey(long kaleoProcessId)
		throws NoSuchKaleoProcessException {

		return findByPrimaryKey((Serializable)kaleoProcessId);
	}

	/**
	 * Returns the kaleo process with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param kaleoProcessId the primary key of the kaleo process
	 * @return the kaleo process, or <code>null</code> if a kaleo process with the primary key could not be found
	 */
	@Override
	public KaleoProcess fetchByPrimaryKey(long kaleoProcessId) {
		return fetchByPrimaryKey((Serializable)kaleoProcessId);
	}

	/**
	 * Returns all the kaleo processes.
	 *
	 * @return the kaleo processes
	 */
	@Override
	public List<KaleoProcess> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the kaleo processes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoProcessModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kaleo processes
	 * @param end the upper bound of the range of kaleo processes (not inclusive)
	 * @return the range of kaleo processes
	 */
	@Override
	public List<KaleoProcess> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the kaleo processes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoProcessModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kaleo processes
	 * @param end the upper bound of the range of kaleo processes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of kaleo processes
	 */
	@Override
	public List<KaleoProcess> findAll(
		int start, int end, OrderByComparator<KaleoProcess> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the kaleo processes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoProcessModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kaleo processes
	 * @param end the upper bound of the range of kaleo processes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of kaleo processes
	 */
	@Override
	public List<KaleoProcess> findAll(
		int start, int end, OrderByComparator<KaleoProcess> orderByComparator,
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

		List<KaleoProcess> list = null;

		if (useFinderCache) {
			list = (List<KaleoProcess>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_KALEOPROCESS);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_KALEOPROCESS;

				sql = sql.concat(KaleoProcessModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<KaleoProcess>)QueryUtil.list(
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
	 * Removes all the kaleo processes from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (KaleoProcess kaleoProcess : findAll()) {
			remove(kaleoProcess);
		}
	}

	/**
	 * Returns the number of kaleo processes.
	 *
	 * @return the number of kaleo processes
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_KALEOPROCESS);

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
		return "kaleoProcessId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_KALEOPROCESS;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return KaleoProcessModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the kaleo process persistence.
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

		_finderPathFetchByDDLRecordSetId = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByDDLRecordSetId",
			new String[] {Long.class.getName()},
			new String[] {"DDLRecordSetId"}, true);

		KaleoProcessUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		KaleoProcessUtil.setPersistence(null);

		entityCache.removeCache(KaleoProcessImpl.class.getName());
	}

	@Override
	@Reference(
		target = KaleoFormsPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = KaleoFormsPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = KaleoFormsPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_KALEOPROCESS =
		"SELECT kaleoProcess FROM KaleoProcess kaleoProcess";

	private static final String _SQL_SELECT_KALEOPROCESS_WHERE =
		"SELECT kaleoProcess FROM KaleoProcess kaleoProcess WHERE ";

	private static final String _SQL_COUNT_KALEOPROCESS =
		"SELECT COUNT(kaleoProcess) FROM KaleoProcess kaleoProcess";

	private static final String _SQL_COUNT_KALEOPROCESS_WHERE =
		"SELECT COUNT(kaleoProcess) FROM KaleoProcess kaleoProcess WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"kaleoProcess.kaleoProcessId";

	private static final String _FILTER_SQL_SELECT_KALEOPROCESS_WHERE =
		"SELECT DISTINCT {kaleoProcess.*} FROM KaleoProcess kaleoProcess WHERE ";

	private static final String
		_FILTER_SQL_SELECT_KALEOPROCESS_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {KaleoProcess.*} FROM (SELECT DISTINCT kaleoProcess.kaleoProcessId FROM KaleoProcess kaleoProcess WHERE ";

	private static final String
		_FILTER_SQL_SELECT_KALEOPROCESS_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN KaleoProcess ON TEMP_TABLE.kaleoProcessId = KaleoProcess.kaleoProcessId";

	private static final String _FILTER_SQL_COUNT_KALEOPROCESS_WHERE =
		"SELECT COUNT(DISTINCT kaleoProcess.kaleoProcessId) AS COUNT_VALUE FROM KaleoProcess kaleoProcess WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "kaleoProcess";

	private static final String _FILTER_ENTITY_TABLE = "KaleoProcess";

	private static final String _ORDER_BY_ENTITY_ALIAS = "kaleoProcess.";

	private static final String _ORDER_BY_ENTITY_TABLE = "KaleoProcess.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No KaleoProcess exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No KaleoProcess exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		KaleoProcessPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}