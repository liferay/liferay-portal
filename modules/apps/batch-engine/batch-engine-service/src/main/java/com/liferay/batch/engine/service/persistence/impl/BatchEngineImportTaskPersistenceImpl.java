/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.service.persistence.impl;

import com.liferay.batch.engine.exception.DuplicateBatchEngineImportTaskExternalReferenceCodeException;
import com.liferay.batch.engine.exception.NoSuchImportTaskException;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.batch.engine.model.BatchEngineImportTaskTable;
import com.liferay.batch.engine.model.impl.BatchEngineImportTaskImpl;
import com.liferay.batch.engine.model.impl.BatchEngineImportTaskModelImpl;
import com.liferay.batch.engine.service.persistence.BatchEngineImportTaskPersistence;
import com.liferay.batch.engine.service.persistence.BatchEngineImportTaskUtil;
import com.liferay.batch.engine.service.persistence.impl.constants.BatchEnginePersistenceConstants;
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
 * The persistence implementation for the batch engine import task service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Shuyang Zhou
 * @generated
 */
@Component(service = BatchEngineImportTaskPersistence.class)
public class BatchEngineImportTaskPersistenceImpl
	extends BasePersistenceImpl<BatchEngineImportTask>
	implements BatchEngineImportTaskPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>BatchEngineImportTaskUtil</code> to access the batch engine import task persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		BatchEngineImportTaskImpl.class.getName();

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
	 * Returns all the batch engine import tasks where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching batch engine import tasks
	 */
	@Override
	public List<BatchEngineImportTask> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the batch engine import tasks where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchEngineImportTaskModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of batch engine import tasks
	 * @param end the upper bound of the range of batch engine import tasks (not inclusive)
	 * @return the range of matching batch engine import tasks
	 */
	@Override
	public List<BatchEngineImportTask> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the batch engine import tasks where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchEngineImportTaskModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of batch engine import tasks
	 * @param end the upper bound of the range of batch engine import tasks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching batch engine import tasks
	 */
	@Override
	public List<BatchEngineImportTask> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<BatchEngineImportTask> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the batch engine import tasks where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchEngineImportTaskModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of batch engine import tasks
	 * @param end the upper bound of the range of batch engine import tasks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching batch engine import tasks
	 */
	@Override
	public List<BatchEngineImportTask> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<BatchEngineImportTask> orderByComparator,
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

		List<BatchEngineImportTask> list = null;

		if (useFinderCache) {
			list = (List<BatchEngineImportTask>)dummyFinderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (BatchEngineImportTask batchEngineImportTask : list) {
					if (!uuid.equals(batchEngineImportTask.getUuid())) {
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

			sb.append(_SQL_SELECT_BATCHENGINEIMPORTTASK_WHERE);

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
				sb.append(BatchEngineImportTaskModelImpl.ORDER_BY_JPQL);
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

				list = (List<BatchEngineImportTask>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					dummyFinderCache.putResult(finderPath, finderArgs, list);
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
	 * Returns the first batch engine import task in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch engine import task
	 * @throws NoSuchImportTaskException if a matching batch engine import task could not be found
	 */
	@Override
	public BatchEngineImportTask findByUuid_First(
			String uuid,
			OrderByComparator<BatchEngineImportTask> orderByComparator)
		throws NoSuchImportTaskException {

		BatchEngineImportTask batchEngineImportTask = fetchByUuid_First(
			uuid, orderByComparator);

		if (batchEngineImportTask != null) {
			return batchEngineImportTask;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchImportTaskException(sb.toString());
	}

	/**
	 * Returns the first batch engine import task in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch engine import task, or <code>null</code> if a matching batch engine import task could not be found
	 */
	@Override
	public BatchEngineImportTask fetchByUuid_First(
		String uuid,
		OrderByComparator<BatchEngineImportTask> orderByComparator) {

		List<BatchEngineImportTask> list = findByUuid(
			uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last batch engine import task in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching batch engine import task
	 * @throws NoSuchImportTaskException if a matching batch engine import task could not be found
	 */
	@Override
	public BatchEngineImportTask findByUuid_Last(
			String uuid,
			OrderByComparator<BatchEngineImportTask> orderByComparator)
		throws NoSuchImportTaskException {

		BatchEngineImportTask batchEngineImportTask = fetchByUuid_Last(
			uuid, orderByComparator);

		if (batchEngineImportTask != null) {
			return batchEngineImportTask;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchImportTaskException(sb.toString());
	}

	/**
	 * Returns the last batch engine import task in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching batch engine import task, or <code>null</code> if a matching batch engine import task could not be found
	 */
	@Override
	public BatchEngineImportTask fetchByUuid_Last(
		String uuid,
		OrderByComparator<BatchEngineImportTask> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<BatchEngineImportTask> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the batch engine import tasks before and after the current batch engine import task in the ordered set where uuid = &#63;.
	 *
	 * @param batchEngineImportTaskId the primary key of the current batch engine import task
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next batch engine import task
	 * @throws NoSuchImportTaskException if a batch engine import task with the primary key could not be found
	 */
	@Override
	public BatchEngineImportTask[] findByUuid_PrevAndNext(
			long batchEngineImportTaskId, String uuid,
			OrderByComparator<BatchEngineImportTask> orderByComparator)
		throws NoSuchImportTaskException {

		uuid = Objects.toString(uuid, "");

		BatchEngineImportTask batchEngineImportTask = findByPrimaryKey(
			batchEngineImportTaskId);

		Session session = null;

		try {
			session = openSession();

			BatchEngineImportTask[] array = new BatchEngineImportTaskImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, batchEngineImportTask, uuid, orderByComparator, true);

			array[1] = batchEngineImportTask;

			array[2] = getByUuid_PrevAndNext(
				session, batchEngineImportTask, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected BatchEngineImportTask getByUuid_PrevAndNext(
		Session session, BatchEngineImportTask batchEngineImportTask,
		String uuid, OrderByComparator<BatchEngineImportTask> orderByComparator,
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

		sb.append(_SQL_SELECT_BATCHENGINEIMPORTTASK_WHERE);

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
			sb.append(BatchEngineImportTaskModelImpl.ORDER_BY_JPQL);
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
						batchEngineImportTask)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BatchEngineImportTask> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the batch engine import tasks where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (BatchEngineImportTask batchEngineImportTask :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(batchEngineImportTask);
		}
	}

	/**
	 * Returns the number of batch engine import tasks where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching batch engine import tasks
	 */
	@Override
	public int countByUuid(String uuid) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid;

		Object[] finderArgs = new Object[] {uuid};

		Long count = (Long)dummyFinderCache.getResult(
			finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_BATCHENGINEIMPORTTASK_WHERE);

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

				dummyFinderCache.putResult(finderPath, finderArgs, count);
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
		"batchEngineImportTask.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(batchEngineImportTask.uuid IS NULL OR batchEngineImportTask.uuid = '')";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the batch engine import tasks where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching batch engine import tasks
	 */
	@Override
	public List<BatchEngineImportTask> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the batch engine import tasks where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchEngineImportTaskModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of batch engine import tasks
	 * @param end the upper bound of the range of batch engine import tasks (not inclusive)
	 * @return the range of matching batch engine import tasks
	 */
	@Override
	public List<BatchEngineImportTask> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the batch engine import tasks where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchEngineImportTaskModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of batch engine import tasks
	 * @param end the upper bound of the range of batch engine import tasks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching batch engine import tasks
	 */
	@Override
	public List<BatchEngineImportTask> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<BatchEngineImportTask> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the batch engine import tasks where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchEngineImportTaskModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of batch engine import tasks
	 * @param end the upper bound of the range of batch engine import tasks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching batch engine import tasks
	 */
	@Override
	public List<BatchEngineImportTask> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<BatchEngineImportTask> orderByComparator,
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

		List<BatchEngineImportTask> list = null;

		if (useFinderCache) {
			list = (List<BatchEngineImportTask>)dummyFinderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (BatchEngineImportTask batchEngineImportTask : list) {
					if (!uuid.equals(batchEngineImportTask.getUuid()) ||
						(companyId != batchEngineImportTask.getCompanyId())) {

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

			sb.append(_SQL_SELECT_BATCHENGINEIMPORTTASK_WHERE);

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
				sb.append(BatchEngineImportTaskModelImpl.ORDER_BY_JPQL);
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

				list = (List<BatchEngineImportTask>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					dummyFinderCache.putResult(finderPath, finderArgs, list);
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
	 * Returns the first batch engine import task in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch engine import task
	 * @throws NoSuchImportTaskException if a matching batch engine import task could not be found
	 */
	@Override
	public BatchEngineImportTask findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<BatchEngineImportTask> orderByComparator)
		throws NoSuchImportTaskException {

		BatchEngineImportTask batchEngineImportTask = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (batchEngineImportTask != null) {
			return batchEngineImportTask;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchImportTaskException(sb.toString());
	}

	/**
	 * Returns the first batch engine import task in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch engine import task, or <code>null</code> if a matching batch engine import task could not be found
	 */
	@Override
	public BatchEngineImportTask fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<BatchEngineImportTask> orderByComparator) {

		List<BatchEngineImportTask> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last batch engine import task in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching batch engine import task
	 * @throws NoSuchImportTaskException if a matching batch engine import task could not be found
	 */
	@Override
	public BatchEngineImportTask findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<BatchEngineImportTask> orderByComparator)
		throws NoSuchImportTaskException {

		BatchEngineImportTask batchEngineImportTask = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (batchEngineImportTask != null) {
			return batchEngineImportTask;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchImportTaskException(sb.toString());
	}

	/**
	 * Returns the last batch engine import task in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching batch engine import task, or <code>null</code> if a matching batch engine import task could not be found
	 */
	@Override
	public BatchEngineImportTask fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<BatchEngineImportTask> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<BatchEngineImportTask> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the batch engine import tasks before and after the current batch engine import task in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param batchEngineImportTaskId the primary key of the current batch engine import task
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next batch engine import task
	 * @throws NoSuchImportTaskException if a batch engine import task with the primary key could not be found
	 */
	@Override
	public BatchEngineImportTask[] findByUuid_C_PrevAndNext(
			long batchEngineImportTaskId, String uuid, long companyId,
			OrderByComparator<BatchEngineImportTask> orderByComparator)
		throws NoSuchImportTaskException {

		uuid = Objects.toString(uuid, "");

		BatchEngineImportTask batchEngineImportTask = findByPrimaryKey(
			batchEngineImportTaskId);

		Session session = null;

		try {
			session = openSession();

			BatchEngineImportTask[] array = new BatchEngineImportTaskImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, batchEngineImportTask, uuid, companyId,
				orderByComparator, true);

			array[1] = batchEngineImportTask;

			array[2] = getByUuid_C_PrevAndNext(
				session, batchEngineImportTask, uuid, companyId,
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

	protected BatchEngineImportTask getByUuid_C_PrevAndNext(
		Session session, BatchEngineImportTask batchEngineImportTask,
		String uuid, long companyId,
		OrderByComparator<BatchEngineImportTask> orderByComparator,
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

		sb.append(_SQL_SELECT_BATCHENGINEIMPORTTASK_WHERE);

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
			sb.append(BatchEngineImportTaskModelImpl.ORDER_BY_JPQL);
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
						batchEngineImportTask)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BatchEngineImportTask> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the batch engine import tasks where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (BatchEngineImportTask batchEngineImportTask :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(batchEngineImportTask);
		}
	}

	/**
	 * Returns the number of batch engine import tasks where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching batch engine import tasks
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid_C;

		Object[] finderArgs = new Object[] {uuid, companyId};

		Long count = (Long)dummyFinderCache.getResult(
			finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_BATCHENGINEIMPORTTASK_WHERE);

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

				dummyFinderCache.putResult(finderPath, finderArgs, count);
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
		"batchEngineImportTask.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(batchEngineImportTask.uuid IS NULL OR batchEngineImportTask.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"batchEngineImportTask.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;

	/**
	 * Returns all the batch engine import tasks where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching batch engine import tasks
	 */
	@Override
	public List<BatchEngineImportTask> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the batch engine import tasks where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchEngineImportTaskModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of batch engine import tasks
	 * @param end the upper bound of the range of batch engine import tasks (not inclusive)
	 * @return the range of matching batch engine import tasks
	 */
	@Override
	public List<BatchEngineImportTask> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the batch engine import tasks where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchEngineImportTaskModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of batch engine import tasks
	 * @param end the upper bound of the range of batch engine import tasks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching batch engine import tasks
	 */
	@Override
	public List<BatchEngineImportTask> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<BatchEngineImportTask> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the batch engine import tasks where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchEngineImportTaskModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of batch engine import tasks
	 * @param end the upper bound of the range of batch engine import tasks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching batch engine import tasks
	 */
	@Override
	public List<BatchEngineImportTask> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<BatchEngineImportTask> orderByComparator,
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

		List<BatchEngineImportTask> list = null;

		if (useFinderCache) {
			list = (List<BatchEngineImportTask>)dummyFinderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (BatchEngineImportTask batchEngineImportTask : list) {
					if (companyId != batchEngineImportTask.getCompanyId()) {
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

			sb.append(_SQL_SELECT_BATCHENGINEIMPORTTASK_WHERE);

			sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(BatchEngineImportTaskModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				list = (List<BatchEngineImportTask>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					dummyFinderCache.putResult(finderPath, finderArgs, list);
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
	 * Returns the first batch engine import task in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch engine import task
	 * @throws NoSuchImportTaskException if a matching batch engine import task could not be found
	 */
	@Override
	public BatchEngineImportTask findByCompanyId_First(
			long companyId,
			OrderByComparator<BatchEngineImportTask> orderByComparator)
		throws NoSuchImportTaskException {

		BatchEngineImportTask batchEngineImportTask = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (batchEngineImportTask != null) {
			return batchEngineImportTask;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchImportTaskException(sb.toString());
	}

	/**
	 * Returns the first batch engine import task in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch engine import task, or <code>null</code> if a matching batch engine import task could not be found
	 */
	@Override
	public BatchEngineImportTask fetchByCompanyId_First(
		long companyId,
		OrderByComparator<BatchEngineImportTask> orderByComparator) {

		List<BatchEngineImportTask> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last batch engine import task in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching batch engine import task
	 * @throws NoSuchImportTaskException if a matching batch engine import task could not be found
	 */
	@Override
	public BatchEngineImportTask findByCompanyId_Last(
			long companyId,
			OrderByComparator<BatchEngineImportTask> orderByComparator)
		throws NoSuchImportTaskException {

		BatchEngineImportTask batchEngineImportTask = fetchByCompanyId_Last(
			companyId, orderByComparator);

		if (batchEngineImportTask != null) {
			return batchEngineImportTask;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchImportTaskException(sb.toString());
	}

	/**
	 * Returns the last batch engine import task in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching batch engine import task, or <code>null</code> if a matching batch engine import task could not be found
	 */
	@Override
	public BatchEngineImportTask fetchByCompanyId_Last(
		long companyId,
		OrderByComparator<BatchEngineImportTask> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<BatchEngineImportTask> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the batch engine import tasks before and after the current batch engine import task in the ordered set where companyId = &#63;.
	 *
	 * @param batchEngineImportTaskId the primary key of the current batch engine import task
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next batch engine import task
	 * @throws NoSuchImportTaskException if a batch engine import task with the primary key could not be found
	 */
	@Override
	public BatchEngineImportTask[] findByCompanyId_PrevAndNext(
			long batchEngineImportTaskId, long companyId,
			OrderByComparator<BatchEngineImportTask> orderByComparator)
		throws NoSuchImportTaskException {

		BatchEngineImportTask batchEngineImportTask = findByPrimaryKey(
			batchEngineImportTaskId);

		Session session = null;

		try {
			session = openSession();

			BatchEngineImportTask[] array = new BatchEngineImportTaskImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, batchEngineImportTask, companyId, orderByComparator,
				true);

			array[1] = batchEngineImportTask;

			array[2] = getByCompanyId_PrevAndNext(
				session, batchEngineImportTask, companyId, orderByComparator,
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

	protected BatchEngineImportTask getByCompanyId_PrevAndNext(
		Session session, BatchEngineImportTask batchEngineImportTask,
		long companyId,
		OrderByComparator<BatchEngineImportTask> orderByComparator,
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

		sb.append(_SQL_SELECT_BATCHENGINEIMPORTTASK_WHERE);

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
			sb.append(BatchEngineImportTaskModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						batchEngineImportTask)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BatchEngineImportTask> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the batch engine import tasks where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (BatchEngineImportTask batchEngineImportTask :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(batchEngineImportTask);
		}
	}

	/**
	 * Returns the number of batch engine import tasks where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching batch engine import tasks
	 */
	@Override
	public int countByCompanyId(long companyId) {
		FinderPath finderPath = _finderPathCountByCompanyId;

		Object[] finderArgs = new Object[] {companyId};

		Long count = (Long)dummyFinderCache.getResult(
			finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_BATCHENGINEIMPORTTASK_WHERE);

			sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				count = (Long)query.uniqueResult();

				dummyFinderCache.putResult(finderPath, finderArgs, count);
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
		"batchEngineImportTask.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByExecuteStatus;
	private FinderPath _finderPathWithoutPaginationFindByExecuteStatus;
	private FinderPath _finderPathCountByExecuteStatus;

	/**
	 * Returns all the batch engine import tasks where executeStatus = &#63;.
	 *
	 * @param executeStatus the execute status
	 * @return the matching batch engine import tasks
	 */
	@Override
	public List<BatchEngineImportTask> findByExecuteStatus(
		String executeStatus) {

		return findByExecuteStatus(
			executeStatus, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the batch engine import tasks where executeStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchEngineImportTaskModelImpl</code>.
	 * </p>
	 *
	 * @param executeStatus the execute status
	 * @param start the lower bound of the range of batch engine import tasks
	 * @param end the upper bound of the range of batch engine import tasks (not inclusive)
	 * @return the range of matching batch engine import tasks
	 */
	@Override
	public List<BatchEngineImportTask> findByExecuteStatus(
		String executeStatus, int start, int end) {

		return findByExecuteStatus(executeStatus, start, end, null);
	}

	/**
	 * Returns an ordered range of all the batch engine import tasks where executeStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchEngineImportTaskModelImpl</code>.
	 * </p>
	 *
	 * @param executeStatus the execute status
	 * @param start the lower bound of the range of batch engine import tasks
	 * @param end the upper bound of the range of batch engine import tasks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching batch engine import tasks
	 */
	@Override
	public List<BatchEngineImportTask> findByExecuteStatus(
		String executeStatus, int start, int end,
		OrderByComparator<BatchEngineImportTask> orderByComparator) {

		return findByExecuteStatus(
			executeStatus, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the batch engine import tasks where executeStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchEngineImportTaskModelImpl</code>.
	 * </p>
	 *
	 * @param executeStatus the execute status
	 * @param start the lower bound of the range of batch engine import tasks
	 * @param end the upper bound of the range of batch engine import tasks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching batch engine import tasks
	 */
	@Override
	public List<BatchEngineImportTask> findByExecuteStatus(
		String executeStatus, int start, int end,
		OrderByComparator<BatchEngineImportTask> orderByComparator,
		boolean useFinderCache) {

		executeStatus = Objects.toString(executeStatus, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByExecuteStatus;
				finderArgs = new Object[] {executeStatus};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByExecuteStatus;
			finderArgs = new Object[] {
				executeStatus, start, end, orderByComparator
			};
		}

		List<BatchEngineImportTask> list = null;

		if (useFinderCache) {
			list = (List<BatchEngineImportTask>)dummyFinderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (BatchEngineImportTask batchEngineImportTask : list) {
					if (!executeStatus.equals(
							batchEngineImportTask.getExecuteStatus())) {

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

			sb.append(_SQL_SELECT_BATCHENGINEIMPORTTASK_WHERE);

			boolean bindExecuteStatus = false;

			if (executeStatus.isEmpty()) {
				sb.append(_FINDER_COLUMN_EXECUTESTATUS_EXECUTESTATUS_3);
			}
			else {
				bindExecuteStatus = true;

				sb.append(_FINDER_COLUMN_EXECUTESTATUS_EXECUTESTATUS_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(BatchEngineImportTaskModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindExecuteStatus) {
					queryPos.add(executeStatus);
				}

				list = (List<BatchEngineImportTask>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					dummyFinderCache.putResult(finderPath, finderArgs, list);
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
	 * Returns the first batch engine import task in the ordered set where executeStatus = &#63;.
	 *
	 * @param executeStatus the execute status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch engine import task
	 * @throws NoSuchImportTaskException if a matching batch engine import task could not be found
	 */
	@Override
	public BatchEngineImportTask findByExecuteStatus_First(
			String executeStatus,
			OrderByComparator<BatchEngineImportTask> orderByComparator)
		throws NoSuchImportTaskException {

		BatchEngineImportTask batchEngineImportTask =
			fetchByExecuteStatus_First(executeStatus, orderByComparator);

		if (batchEngineImportTask != null) {
			return batchEngineImportTask;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("executeStatus=");
		sb.append(executeStatus);

		sb.append("}");

		throw new NoSuchImportTaskException(sb.toString());
	}

	/**
	 * Returns the first batch engine import task in the ordered set where executeStatus = &#63;.
	 *
	 * @param executeStatus the execute status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch engine import task, or <code>null</code> if a matching batch engine import task could not be found
	 */
	@Override
	public BatchEngineImportTask fetchByExecuteStatus_First(
		String executeStatus,
		OrderByComparator<BatchEngineImportTask> orderByComparator) {

		List<BatchEngineImportTask> list = findByExecuteStatus(
			executeStatus, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last batch engine import task in the ordered set where executeStatus = &#63;.
	 *
	 * @param executeStatus the execute status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching batch engine import task
	 * @throws NoSuchImportTaskException if a matching batch engine import task could not be found
	 */
	@Override
	public BatchEngineImportTask findByExecuteStatus_Last(
			String executeStatus,
			OrderByComparator<BatchEngineImportTask> orderByComparator)
		throws NoSuchImportTaskException {

		BatchEngineImportTask batchEngineImportTask = fetchByExecuteStatus_Last(
			executeStatus, orderByComparator);

		if (batchEngineImportTask != null) {
			return batchEngineImportTask;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("executeStatus=");
		sb.append(executeStatus);

		sb.append("}");

		throw new NoSuchImportTaskException(sb.toString());
	}

	/**
	 * Returns the last batch engine import task in the ordered set where executeStatus = &#63;.
	 *
	 * @param executeStatus the execute status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching batch engine import task, or <code>null</code> if a matching batch engine import task could not be found
	 */
	@Override
	public BatchEngineImportTask fetchByExecuteStatus_Last(
		String executeStatus,
		OrderByComparator<BatchEngineImportTask> orderByComparator) {

		int count = countByExecuteStatus(executeStatus);

		if (count == 0) {
			return null;
		}

		List<BatchEngineImportTask> list = findByExecuteStatus(
			executeStatus, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the batch engine import tasks before and after the current batch engine import task in the ordered set where executeStatus = &#63;.
	 *
	 * @param batchEngineImportTaskId the primary key of the current batch engine import task
	 * @param executeStatus the execute status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next batch engine import task
	 * @throws NoSuchImportTaskException if a batch engine import task with the primary key could not be found
	 */
	@Override
	public BatchEngineImportTask[] findByExecuteStatus_PrevAndNext(
			long batchEngineImportTaskId, String executeStatus,
			OrderByComparator<BatchEngineImportTask> orderByComparator)
		throws NoSuchImportTaskException {

		executeStatus = Objects.toString(executeStatus, "");

		BatchEngineImportTask batchEngineImportTask = findByPrimaryKey(
			batchEngineImportTaskId);

		Session session = null;

		try {
			session = openSession();

			BatchEngineImportTask[] array = new BatchEngineImportTaskImpl[3];

			array[0] = getByExecuteStatus_PrevAndNext(
				session, batchEngineImportTask, executeStatus,
				orderByComparator, true);

			array[1] = batchEngineImportTask;

			array[2] = getByExecuteStatus_PrevAndNext(
				session, batchEngineImportTask, executeStatus,
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

	protected BatchEngineImportTask getByExecuteStatus_PrevAndNext(
		Session session, BatchEngineImportTask batchEngineImportTask,
		String executeStatus,
		OrderByComparator<BatchEngineImportTask> orderByComparator,
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

		sb.append(_SQL_SELECT_BATCHENGINEIMPORTTASK_WHERE);

		boolean bindExecuteStatus = false;

		if (executeStatus.isEmpty()) {
			sb.append(_FINDER_COLUMN_EXECUTESTATUS_EXECUTESTATUS_3);
		}
		else {
			bindExecuteStatus = true;

			sb.append(_FINDER_COLUMN_EXECUTESTATUS_EXECUTESTATUS_2);
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
			sb.append(BatchEngineImportTaskModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindExecuteStatus) {
			queryPos.add(executeStatus);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						batchEngineImportTask)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BatchEngineImportTask> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the batch engine import tasks where executeStatus = &#63; from the database.
	 *
	 * @param executeStatus the execute status
	 */
	@Override
	public void removeByExecuteStatus(String executeStatus) {
		for (BatchEngineImportTask batchEngineImportTask :
				findByExecuteStatus(
					executeStatus, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(batchEngineImportTask);
		}
	}

	/**
	 * Returns the number of batch engine import tasks where executeStatus = &#63;.
	 *
	 * @param executeStatus the execute status
	 * @return the number of matching batch engine import tasks
	 */
	@Override
	public int countByExecuteStatus(String executeStatus) {
		executeStatus = Objects.toString(executeStatus, "");

		FinderPath finderPath = _finderPathCountByExecuteStatus;

		Object[] finderArgs = new Object[] {executeStatus};

		Long count = (Long)dummyFinderCache.getResult(
			finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_BATCHENGINEIMPORTTASK_WHERE);

			boolean bindExecuteStatus = false;

			if (executeStatus.isEmpty()) {
				sb.append(_FINDER_COLUMN_EXECUTESTATUS_EXECUTESTATUS_3);
			}
			else {
				bindExecuteStatus = true;

				sb.append(_FINDER_COLUMN_EXECUTESTATUS_EXECUTESTATUS_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindExecuteStatus) {
					queryPos.add(executeStatus);
				}

				count = (Long)query.uniqueResult();

				dummyFinderCache.putResult(finderPath, finderArgs, count);
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

	private static final String _FINDER_COLUMN_EXECUTESTATUS_EXECUTESTATUS_2 =
		"batchEngineImportTask.executeStatus = ?";

	private static final String _FINDER_COLUMN_EXECUTESTATUS_EXECUTESTATUS_3 =
		"(batchEngineImportTask.executeStatus IS NULL OR batchEngineImportTask.executeStatus = '')";

	private FinderPath _finderPathFetchByERC_C;

	/**
	 * Returns the batch engine import task where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchImportTaskException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching batch engine import task
	 * @throws NoSuchImportTaskException if a matching batch engine import task could not be found
	 */
	@Override
	public BatchEngineImportTask findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchImportTaskException {

		BatchEngineImportTask batchEngineImportTask = fetchByERC_C(
			externalReferenceCode, companyId);

		if (batchEngineImportTask == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("externalReferenceCode=");
			sb.append(externalReferenceCode);

			sb.append(", companyId=");
			sb.append(companyId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchImportTaskException(sb.toString());
		}

		return batchEngineImportTask;
	}

	/**
	 * Returns the batch engine import task where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching batch engine import task, or <code>null</code> if a matching batch engine import task could not be found
	 */
	@Override
	public BatchEngineImportTask fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return fetchByERC_C(externalReferenceCode, companyId, true);
	}

	/**
	 * Returns the batch engine import task where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching batch engine import task, or <code>null</code> if a matching batch engine import task could not be found
	 */
	@Override
	public BatchEngineImportTask fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		externalReferenceCode = Objects.toString(externalReferenceCode, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {externalReferenceCode, companyId};
		}

		Object result = null;

		if (useFinderCache) {
			result = dummyFinderCache.getResult(
				_finderPathFetchByERC_C, finderArgs, this);
		}

		if (result instanceof BatchEngineImportTask) {
			BatchEngineImportTask batchEngineImportTask =
				(BatchEngineImportTask)result;

			if (!Objects.equals(
					externalReferenceCode,
					batchEngineImportTask.getExternalReferenceCode()) ||
				(companyId != batchEngineImportTask.getCompanyId())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_BATCHENGINEIMPORTTASK_WHERE);

			boolean bindExternalReferenceCode = false;

			if (externalReferenceCode.isEmpty()) {
				sb.append(_FINDER_COLUMN_ERC_C_EXTERNALREFERENCECODE_3);
			}
			else {
				bindExternalReferenceCode = true;

				sb.append(_FINDER_COLUMN_ERC_C_EXTERNALREFERENCECODE_2);
			}

			sb.append(_FINDER_COLUMN_ERC_C_COMPANYID_2);

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

				List<BatchEngineImportTask> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						dummyFinderCache.putResult(
							_finderPathFetchByERC_C, finderArgs, list);
					}
				}
				else {
					BatchEngineImportTask batchEngineImportTask = list.get(0);

					result = batchEngineImportTask;

					cacheResult(batchEngineImportTask);
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
			return (BatchEngineImportTask)result;
		}
	}

	/**
	 * Removes the batch engine import task where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the batch engine import task that was removed
	 */
	@Override
	public BatchEngineImportTask removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchImportTaskException {

		BatchEngineImportTask batchEngineImportTask = findByERC_C(
			externalReferenceCode, companyId);

		return remove(batchEngineImportTask);
	}

	/**
	 * Returns the number of batch engine import tasks where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching batch engine import tasks
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		BatchEngineImportTask batchEngineImportTask = fetchByERC_C(
			externalReferenceCode, companyId);

		if (batchEngineImportTask == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ERC_C_EXTERNALREFERENCECODE_2 =
		"batchEngineImportTask.externalReferenceCode = ? AND ";

	private static final String _FINDER_COLUMN_ERC_C_EXTERNALREFERENCECODE_3 =
		"(batchEngineImportTask.externalReferenceCode IS NULL OR batchEngineImportTask.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_C_COMPANYID_2 =
		"batchEngineImportTask.companyId = ?";

	public BatchEngineImportTaskPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(BatchEngineImportTask.class);

		setModelImplClass(BatchEngineImportTaskImpl.class);
		setModelPKClass(long.class);

		setTable(BatchEngineImportTaskTable.INSTANCE);
	}

	/**
	 * Caches the batch engine import task in the entity cache if it is enabled.
	 *
	 * @param batchEngineImportTask the batch engine import task
	 */
	@Override
	public void cacheResult(BatchEngineImportTask batchEngineImportTask) {
		dummyEntityCache.putResult(
			BatchEngineImportTaskImpl.class,
			batchEngineImportTask.getPrimaryKey(), batchEngineImportTask);

		dummyFinderCache.putResult(
			_finderPathFetchByERC_C,
			new Object[] {
				batchEngineImportTask.getExternalReferenceCode(),
				batchEngineImportTask.getCompanyId()
			},
			batchEngineImportTask);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the batch engine import tasks in the entity cache if it is enabled.
	 *
	 * @param batchEngineImportTasks the batch engine import tasks
	 */
	@Override
	public void cacheResult(
		List<BatchEngineImportTask> batchEngineImportTasks) {

		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (batchEngineImportTasks.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (BatchEngineImportTask batchEngineImportTask :
				batchEngineImportTasks) {

			if (dummyEntityCache.getResult(
					BatchEngineImportTaskImpl.class,
					batchEngineImportTask.getPrimaryKey()) == null) {

				cacheResult(batchEngineImportTask);
			}
		}
	}

	/**
	 * Clears the cache for all batch engine import tasks.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		dummyEntityCache.clearCache(BatchEngineImportTaskImpl.class);

		dummyFinderCache.clearCache(BatchEngineImportTaskImpl.class);
	}

	/**
	 * Clears the cache for the batch engine import task.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(BatchEngineImportTask batchEngineImportTask) {
		dummyEntityCache.removeResult(
			BatchEngineImportTaskImpl.class, batchEngineImportTask);
	}

	@Override
	public void clearCache(List<BatchEngineImportTask> batchEngineImportTasks) {
		for (BatchEngineImportTask batchEngineImportTask :
				batchEngineImportTasks) {

			dummyEntityCache.removeResult(
				BatchEngineImportTaskImpl.class, batchEngineImportTask);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		dummyFinderCache.clearCache(BatchEngineImportTaskImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			dummyEntityCache.removeResult(
				BatchEngineImportTaskImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		BatchEngineImportTaskModelImpl batchEngineImportTaskModelImpl) {

		Object[] args = new Object[] {
			batchEngineImportTaskModelImpl.getExternalReferenceCode(),
			batchEngineImportTaskModelImpl.getCompanyId()
		};

		dummyFinderCache.putResult(
			_finderPathFetchByERC_C, args, batchEngineImportTaskModelImpl);
	}

	/**
	 * Creates a new batch engine import task with the primary key. Does not add the batch engine import task to the database.
	 *
	 * @param batchEngineImportTaskId the primary key for the new batch engine import task
	 * @return the new batch engine import task
	 */
	@Override
	public BatchEngineImportTask create(long batchEngineImportTaskId) {
		BatchEngineImportTask batchEngineImportTask =
			new BatchEngineImportTaskImpl();

		batchEngineImportTask.setNew(true);
		batchEngineImportTask.setPrimaryKey(batchEngineImportTaskId);

		String uuid = PortalUUIDUtil.generate();

		batchEngineImportTask.setUuid(uuid);

		batchEngineImportTask.setCompanyId(CompanyThreadLocal.getCompanyId());

		return batchEngineImportTask;
	}

	/**
	 * Removes the batch engine import task with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param batchEngineImportTaskId the primary key of the batch engine import task
	 * @return the batch engine import task that was removed
	 * @throws NoSuchImportTaskException if a batch engine import task with the primary key could not be found
	 */
	@Override
	public BatchEngineImportTask remove(long batchEngineImportTaskId)
		throws NoSuchImportTaskException {

		return remove((Serializable)batchEngineImportTaskId);
	}

	/**
	 * Removes the batch engine import task with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the batch engine import task
	 * @return the batch engine import task that was removed
	 * @throws NoSuchImportTaskException if a batch engine import task with the primary key could not be found
	 */
	@Override
	public BatchEngineImportTask remove(Serializable primaryKey)
		throws NoSuchImportTaskException {

		Session session = null;

		try {
			session = openSession();

			BatchEngineImportTask batchEngineImportTask =
				(BatchEngineImportTask)session.get(
					BatchEngineImportTaskImpl.class, primaryKey);

			if (batchEngineImportTask == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchImportTaskException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(batchEngineImportTask);
		}
		catch (NoSuchImportTaskException noSuchEntityException) {
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
	protected BatchEngineImportTask removeImpl(
		BatchEngineImportTask batchEngineImportTask) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(batchEngineImportTask)) {
				batchEngineImportTask = (BatchEngineImportTask)session.get(
					BatchEngineImportTaskImpl.class,
					batchEngineImportTask.getPrimaryKeyObj());
			}

			if (batchEngineImportTask != null) {
				session.delete(batchEngineImportTask);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (batchEngineImportTask != null) {
			clearCache(batchEngineImportTask);
		}

		return batchEngineImportTask;
	}

	@Override
	public BatchEngineImportTask updateImpl(
		BatchEngineImportTask batchEngineImportTask) {

		boolean isNew = batchEngineImportTask.isNew();

		if (!(batchEngineImportTask instanceof
				BatchEngineImportTaskModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(batchEngineImportTask.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					batchEngineImportTask);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in batchEngineImportTask proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom BatchEngineImportTask implementation " +
					batchEngineImportTask.getClass());
		}

		BatchEngineImportTaskModelImpl batchEngineImportTaskModelImpl =
			(BatchEngineImportTaskModelImpl)batchEngineImportTask;

		if (Validator.isNull(batchEngineImportTask.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			batchEngineImportTask.setUuid(uuid);
		}

		if (Validator.isNull(
				batchEngineImportTask.getExternalReferenceCode())) {

			batchEngineImportTask.setExternalReferenceCode(
				batchEngineImportTask.getUuid());
		}
		else {
			if (!Objects.equals(
					batchEngineImportTaskModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					batchEngineImportTask.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = batchEngineImportTask.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = batchEngineImportTask.getPrimaryKey();
					}

					try {
						batchEngineImportTask.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								BatchEngineImportTask.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								batchEngineImportTask.
									getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			BatchEngineImportTask ercBatchEngineImportTask = fetchByERC_C(
				batchEngineImportTask.getExternalReferenceCode(),
				batchEngineImportTask.getCompanyId());

			if (isNew) {
				if (ercBatchEngineImportTask != null) {
					throw new DuplicateBatchEngineImportTaskExternalReferenceCodeException(
						"Duplicate batch engine import task with external reference code " +
							batchEngineImportTask.getExternalReferenceCode() +
								" and company " +
									batchEngineImportTask.getCompanyId());
				}
			}
			else {
				if ((ercBatchEngineImportTask != null) &&
					(batchEngineImportTask.getBatchEngineImportTaskId() !=
						ercBatchEngineImportTask.
							getBatchEngineImportTaskId())) {

					throw new DuplicateBatchEngineImportTaskExternalReferenceCodeException(
						"Duplicate batch engine import task with external reference code " +
							batchEngineImportTask.getExternalReferenceCode() +
								" and company " +
									batchEngineImportTask.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (batchEngineImportTask.getCreateDate() == null)) {
			if (serviceContext == null) {
				batchEngineImportTask.setCreateDate(date);
			}
			else {
				batchEngineImportTask.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!batchEngineImportTaskModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				batchEngineImportTask.setModifiedDate(date);
			}
			else {
				batchEngineImportTask.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(batchEngineImportTask);
			}
			else {
				batchEngineImportTask = (BatchEngineImportTask)session.merge(
					batchEngineImportTask);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		dummyEntityCache.putResult(
			BatchEngineImportTaskImpl.class, batchEngineImportTaskModelImpl,
			false, true);

		cacheUniqueFindersCache(batchEngineImportTaskModelImpl);

		if (isNew) {
			batchEngineImportTask.setNew(false);
		}

		batchEngineImportTask.resetOriginalValues();

		return batchEngineImportTask;
	}

	/**
	 * Returns the batch engine import task with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the batch engine import task
	 * @return the batch engine import task
	 * @throws NoSuchImportTaskException if a batch engine import task with the primary key could not be found
	 */
	@Override
	public BatchEngineImportTask findByPrimaryKey(Serializable primaryKey)
		throws NoSuchImportTaskException {

		BatchEngineImportTask batchEngineImportTask = fetchByPrimaryKey(
			primaryKey);

		if (batchEngineImportTask == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchImportTaskException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return batchEngineImportTask;
	}

	/**
	 * Returns the batch engine import task with the primary key or throws a <code>NoSuchImportTaskException</code> if it could not be found.
	 *
	 * @param batchEngineImportTaskId the primary key of the batch engine import task
	 * @return the batch engine import task
	 * @throws NoSuchImportTaskException if a batch engine import task with the primary key could not be found
	 */
	@Override
	public BatchEngineImportTask findByPrimaryKey(long batchEngineImportTaskId)
		throws NoSuchImportTaskException {

		return findByPrimaryKey((Serializable)batchEngineImportTaskId);
	}

	/**
	 * Returns the batch engine import task with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param batchEngineImportTaskId the primary key of the batch engine import task
	 * @return the batch engine import task, or <code>null</code> if a batch engine import task with the primary key could not be found
	 */
	@Override
	public BatchEngineImportTask fetchByPrimaryKey(
		long batchEngineImportTaskId) {

		return fetchByPrimaryKey((Serializable)batchEngineImportTaskId);
	}

	/**
	 * Returns all the batch engine import tasks.
	 *
	 * @return the batch engine import tasks
	 */
	@Override
	public List<BatchEngineImportTask> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the batch engine import tasks.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchEngineImportTaskModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of batch engine import tasks
	 * @param end the upper bound of the range of batch engine import tasks (not inclusive)
	 * @return the range of batch engine import tasks
	 */
	@Override
	public List<BatchEngineImportTask> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the batch engine import tasks.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchEngineImportTaskModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of batch engine import tasks
	 * @param end the upper bound of the range of batch engine import tasks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of batch engine import tasks
	 */
	@Override
	public List<BatchEngineImportTask> findAll(
		int start, int end,
		OrderByComparator<BatchEngineImportTask> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the batch engine import tasks.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchEngineImportTaskModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of batch engine import tasks
	 * @param end the upper bound of the range of batch engine import tasks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of batch engine import tasks
	 */
	@Override
	public List<BatchEngineImportTask> findAll(
		int start, int end,
		OrderByComparator<BatchEngineImportTask> orderByComparator,
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

		List<BatchEngineImportTask> list = null;

		if (useFinderCache) {
			list = (List<BatchEngineImportTask>)dummyFinderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_BATCHENGINEIMPORTTASK);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_BATCHENGINEIMPORTTASK;

				sql = sql.concat(BatchEngineImportTaskModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<BatchEngineImportTask>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					dummyFinderCache.putResult(finderPath, finderArgs, list);
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
	 * Removes all the batch engine import tasks from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (BatchEngineImportTask batchEngineImportTask : findAll()) {
			remove(batchEngineImportTask);
		}
	}

	/**
	 * Returns the number of batch engine import tasks.
	 *
	 * @return the number of batch engine import tasks
	 */
	@Override
	public int countAll() {
		Long count = (Long)dummyFinderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(
					_SQL_COUNT_BATCHENGINEIMPORTTASK);

				count = (Long)query.uniqueResult();

				dummyFinderCache.putResult(
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
		return dummyEntityCache;
	}

	@Override
	protected String getPKDBName() {
		return "batchEngineImportTaskId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_BATCHENGINEIMPORTTASK;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return BatchEngineImportTaskModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the batch engine import task persistence.
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

		_finderPathWithPaginationFindByExecuteStatus = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByExecuteStatus",
			new String[] {
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"executeStatus"}, true);

		_finderPathWithoutPaginationFindByExecuteStatus = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByExecuteStatus",
			new String[] {String.class.getName()},
			new String[] {"executeStatus"}, true);

		_finderPathCountByExecuteStatus = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByExecuteStatus",
			new String[] {String.class.getName()},
			new String[] {"executeStatus"}, false);

		_finderPathFetchByERC_C = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"externalReferenceCode", "companyId"}, true);

		BatchEngineImportTaskUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		BatchEngineImportTaskUtil.setPersistence(null);

		dummyEntityCache.removeCache(BatchEngineImportTaskImpl.class.getName());
	}

	@Override
	@Reference(
		target = BatchEnginePersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = BatchEnginePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = BatchEnginePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	private static final String _SQL_SELECT_BATCHENGINEIMPORTTASK =
		"SELECT batchEngineImportTask FROM BatchEngineImportTask batchEngineImportTask";

	private static final String _SQL_SELECT_BATCHENGINEIMPORTTASK_WHERE =
		"SELECT batchEngineImportTask FROM BatchEngineImportTask batchEngineImportTask WHERE ";

	private static final String _SQL_COUNT_BATCHENGINEIMPORTTASK =
		"SELECT COUNT(batchEngineImportTask) FROM BatchEngineImportTask batchEngineImportTask";

	private static final String _SQL_COUNT_BATCHENGINEIMPORTTASK_WHERE =
		"SELECT COUNT(batchEngineImportTask) FROM BatchEngineImportTask batchEngineImportTask WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS =
		"batchEngineImportTask.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No BatchEngineImportTask exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No BatchEngineImportTask exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		BatchEngineImportTaskPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return dummyFinderCache;
	}

}