/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.service.persistence.impl;

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.workflow.metrics.exception.NoSuchSLADefinitionVersionException;
import com.liferay.portal.workflow.metrics.model.WorkflowMetricsSLADefinitionVersion;
import com.liferay.portal.workflow.metrics.model.WorkflowMetricsSLADefinitionVersionTable;
import com.liferay.portal.workflow.metrics.model.impl.WorkflowMetricsSLADefinitionVersionImpl;
import com.liferay.portal.workflow.metrics.model.impl.WorkflowMetricsSLADefinitionVersionModelImpl;
import com.liferay.portal.workflow.metrics.service.persistence.WorkflowMetricsSLADefinitionVersionPersistence;
import com.liferay.portal.workflow.metrics.service.persistence.WorkflowMetricsSLADefinitionVersionUtil;
import com.liferay.portal.workflow.metrics.service.persistence.impl.constants.WorkflowMetricsPersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the workflow metrics sla definition version service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = WorkflowMetricsSLADefinitionVersionPersistence.class)
public class WorkflowMetricsSLADefinitionVersionPersistenceImpl
	extends BasePersistenceImpl
		<WorkflowMetricsSLADefinitionVersion,
		 NoSuchSLADefinitionVersionException>
	implements WorkflowMetricsSLADefinitionVersionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>WorkflowMetricsSLADefinitionVersionUtil</code> to access the workflow metrics sla definition version persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		WorkflowMetricsSLADefinitionVersionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<WorkflowMetricsSLADefinitionVersion,
		 NoSuchSLADefinitionVersionException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the workflow metrics sla definition versions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowMetricsSLADefinitionVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of workflow metrics sla definition versions
	 * @param end the upper bound of the range of workflow metrics sla definition versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow metrics sla definition versions
	 */
	@Override
	public List<WorkflowMetricsSLADefinitionVersion> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<WorkflowMetricsSLADefinitionVersion>
			orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first workflow metrics sla definition version in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow metrics sla definition version
	 * @throws NoSuchSLADefinitionVersionException if a matching workflow metrics sla definition version could not be found
	 */
	@Override
	public WorkflowMetricsSLADefinitionVersion findByUuid_First(
			String uuid,
			OrderByComparator<WorkflowMetricsSLADefinitionVersion>
				orderByComparator)
		throws NoSuchSLADefinitionVersionException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first workflow metrics sla definition version in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow metrics sla definition version, or <code>null</code> if a matching workflow metrics sla definition version could not be found
	 */
	@Override
	public WorkflowMetricsSLADefinitionVersion fetchByUuid_First(
		String uuid,
		OrderByComparator<WorkflowMetricsSLADefinitionVersion>
			orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the workflow metrics sla definition versions where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of workflow metrics sla definition versions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching workflow metrics sla definition versions
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<WorkflowMetricsSLADefinitionVersion,
		 NoSuchSLADefinitionVersionException> _uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the workflow metrics sla definition version where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchSLADefinitionVersionException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching workflow metrics sla definition version
	 * @throws NoSuchSLADefinitionVersionException if a matching workflow metrics sla definition version could not be found
	 */
	@Override
	public WorkflowMetricsSLADefinitionVersion findByUUID_G(
			String uuid, long groupId)
		throws NoSuchSLADefinitionVersionException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the workflow metrics sla definition version where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching workflow metrics sla definition version, or <code>null</code> if a matching workflow metrics sla definition version could not be found
	 */
	@Override
	public WorkflowMetricsSLADefinitionVersion fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the workflow metrics sla definition version where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the workflow metrics sla definition version that was removed
	 */
	@Override
	public WorkflowMetricsSLADefinitionVersion removeByUUID_G(
			String uuid, long groupId)
		throws NoSuchSLADefinitionVersionException {

		WorkflowMetricsSLADefinitionVersion
			workflowMetricsSLADefinitionVersion = findByUUID_G(uuid, groupId);

		return remove(workflowMetricsSLADefinitionVersion);
	}

	/**
	 * Returns the number of workflow metrics sla definition versions where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching workflow metrics sla definition versions
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<WorkflowMetricsSLADefinitionVersion,
		 NoSuchSLADefinitionVersionException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the workflow metrics sla definition versions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowMetricsSLADefinitionVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of workflow metrics sla definition versions
	 * @param end the upper bound of the range of workflow metrics sla definition versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow metrics sla definition versions
	 */
	@Override
	public List<WorkflowMetricsSLADefinitionVersion> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<WorkflowMetricsSLADefinitionVersion>
			orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first workflow metrics sla definition version in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow metrics sla definition version
	 * @throws NoSuchSLADefinitionVersionException if a matching workflow metrics sla definition version could not be found
	 */
	@Override
	public WorkflowMetricsSLADefinitionVersion findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<WorkflowMetricsSLADefinitionVersion>
				orderByComparator)
		throws NoSuchSLADefinitionVersionException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first workflow metrics sla definition version in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow metrics sla definition version, or <code>null</code> if a matching workflow metrics sla definition version could not be found
	 */
	@Override
	public WorkflowMetricsSLADefinitionVersion fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<WorkflowMetricsSLADefinitionVersion>
			orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the workflow metrics sla definition versions where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		_collectionPersistenceFinderByUuid_C.remove(
			finderCache, new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of workflow metrics sla definition versions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching workflow metrics sla definition versions
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<WorkflowMetricsSLADefinitionVersion,
		 NoSuchSLADefinitionVersionException>
			_collectionPersistenceFinderByWorkflowMetricsSLADefinitionId;

	/**
	 * Returns an ordered range of all the workflow metrics sla definition versions where workflowMetricsSLADefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowMetricsSLADefinitionVersionModelImpl</code>.
	 * </p>
	 *
	 * @param workflowMetricsSLADefinitionId the workflow metrics sla definition ID
	 * @param start the lower bound of the range of workflow metrics sla definition versions
	 * @param end the upper bound of the range of workflow metrics sla definition versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow metrics sla definition versions
	 */
	@Override
	public List<WorkflowMetricsSLADefinitionVersion>
		findByWorkflowMetricsSLADefinitionId(
			long workflowMetricsSLADefinitionId, int start, int end,
			OrderByComparator<WorkflowMetricsSLADefinitionVersion>
				orderByComparator,
			boolean useFinderCache) {

		return _collectionPersistenceFinderByWorkflowMetricsSLADefinitionId.
			find(
				finderCache, new Object[] {workflowMetricsSLADefinitionId},
				start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first workflow metrics sla definition version in the ordered set where workflowMetricsSLADefinitionId = &#63;.
	 *
	 * @param workflowMetricsSLADefinitionId the workflow metrics sla definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow metrics sla definition version
	 * @throws NoSuchSLADefinitionVersionException if a matching workflow metrics sla definition version could not be found
	 */
	@Override
	public WorkflowMetricsSLADefinitionVersion
			findByWorkflowMetricsSLADefinitionId_First(
				long workflowMetricsSLADefinitionId,
				OrderByComparator<WorkflowMetricsSLADefinitionVersion>
					orderByComparator)
		throws NoSuchSLADefinitionVersionException {

		return _collectionPersistenceFinderByWorkflowMetricsSLADefinitionId.
			findFirst(
				finderCache, new Object[] {workflowMetricsSLADefinitionId},
				orderByComparator);
	}

	/**
	 * Returns the first workflow metrics sla definition version in the ordered set where workflowMetricsSLADefinitionId = &#63;.
	 *
	 * @param workflowMetricsSLADefinitionId the workflow metrics sla definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow metrics sla definition version, or <code>null</code> if a matching workflow metrics sla definition version could not be found
	 */
	@Override
	public WorkflowMetricsSLADefinitionVersion
		fetchByWorkflowMetricsSLADefinitionId_First(
			long workflowMetricsSLADefinitionId,
			OrderByComparator<WorkflowMetricsSLADefinitionVersion>
				orderByComparator) {

		return _collectionPersistenceFinderByWorkflowMetricsSLADefinitionId.
			fetchFirst(
				finderCache, new Object[] {workflowMetricsSLADefinitionId},
				orderByComparator);
	}

	/**
	 * Removes all the workflow metrics sla definition versions where workflowMetricsSLADefinitionId = &#63; from the database.
	 *
	 * @param workflowMetricsSLADefinitionId the workflow metrics sla definition ID
	 */
	@Override
	public void removeByWorkflowMetricsSLADefinitionId(
		long workflowMetricsSLADefinitionId) {

		_collectionPersistenceFinderByWorkflowMetricsSLADefinitionId.remove(
			finderCache, new Object[] {workflowMetricsSLADefinitionId});
	}

	/**
	 * Returns the number of workflow metrics sla definition versions where workflowMetricsSLADefinitionId = &#63;.
	 *
	 * @param workflowMetricsSLADefinitionId the workflow metrics sla definition ID
	 * @return the number of matching workflow metrics sla definition versions
	 */
	@Override
	public int countByWorkflowMetricsSLADefinitionId(
		long workflowMetricsSLADefinitionId) {

		return _collectionPersistenceFinderByWorkflowMetricsSLADefinitionId.
			count(finderCache, new Object[] {workflowMetricsSLADefinitionId});
	}

	private UniquePersistenceFinder
		<WorkflowMetricsSLADefinitionVersion,
		 NoSuchSLADefinitionVersionException>
			_uniquePersistenceFinderByV_WMSLAD;

	/**
	 * Returns the workflow metrics sla definition version where version = &#63; and workflowMetricsSLADefinitionId = &#63; or throws a <code>NoSuchSLADefinitionVersionException</code> if it could not be found.
	 *
	 * @param version the version
	 * @param workflowMetricsSLADefinitionId the workflow metrics sla definition ID
	 * @return the matching workflow metrics sla definition version
	 * @throws NoSuchSLADefinitionVersionException if a matching workflow metrics sla definition version could not be found
	 */
	@Override
	public WorkflowMetricsSLADefinitionVersion findByV_WMSLAD(
			String version, long workflowMetricsSLADefinitionId)
		throws NoSuchSLADefinitionVersionException {

		return _uniquePersistenceFinderByV_WMSLAD.find(
			finderCache,
			new Object[] {version, workflowMetricsSLADefinitionId});
	}

	/**
	 * Returns the workflow metrics sla definition version where version = &#63; and workflowMetricsSLADefinitionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param version the version
	 * @param workflowMetricsSLADefinitionId the workflow metrics sla definition ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching workflow metrics sla definition version, or <code>null</code> if a matching workflow metrics sla definition version could not be found
	 */
	@Override
	public WorkflowMetricsSLADefinitionVersion fetchByV_WMSLAD(
		String version, long workflowMetricsSLADefinitionId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByV_WMSLAD.fetch(
			finderCache, new Object[] {version, workflowMetricsSLADefinitionId},
			useFinderCache);
	}

	/**
	 * Removes the workflow metrics sla definition version where version = &#63; and workflowMetricsSLADefinitionId = &#63; from the database.
	 *
	 * @param version the version
	 * @param workflowMetricsSLADefinitionId the workflow metrics sla definition ID
	 * @return the workflow metrics sla definition version that was removed
	 */
	@Override
	public WorkflowMetricsSLADefinitionVersion removeByV_WMSLAD(
			String version, long workflowMetricsSLADefinitionId)
		throws NoSuchSLADefinitionVersionException {

		WorkflowMetricsSLADefinitionVersion
			workflowMetricsSLADefinitionVersion = findByV_WMSLAD(
				version, workflowMetricsSLADefinitionId);

		return remove(workflowMetricsSLADefinitionVersion);
	}

	/**
	 * Returns the number of workflow metrics sla definition versions where version = &#63; and workflowMetricsSLADefinitionId = &#63;.
	 *
	 * @param version the version
	 * @param workflowMetricsSLADefinitionId the workflow metrics sla definition ID
	 * @return the number of matching workflow metrics sla definition versions
	 */
	@Override
	public int countByV_WMSLAD(
		String version, long workflowMetricsSLADefinitionId) {

		return _uniquePersistenceFinderByV_WMSLAD.count(
			finderCache,
			new Object[] {version, workflowMetricsSLADefinitionId});
	}

	public WorkflowMetricsSLADefinitionVersionPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put(
			"workflowMetricsSLADefinitionVersionId",
			"wmSLADefinitionVersionId");
		dbColumnNames.put("active", "active_");
		dbColumnNames.put(
			"workflowMetricsSLADefinitionId", "wmSLADefinitionId");

		setDBColumnNames(dbColumnNames);

		setModelClass(WorkflowMetricsSLADefinitionVersion.class);

		setModelImplClass(WorkflowMetricsSLADefinitionVersionImpl.class);
		setModelPKClass(long.class);

		setTable(WorkflowMetricsSLADefinitionVersionTable.INSTANCE);
	}

	/**
	 * Creates a new workflow metrics sla definition version with the primary key. Does not add the workflow metrics sla definition version to the database.
	 *
	 * @param workflowMetricsSLADefinitionVersionId the primary key for the new workflow metrics sla definition version
	 * @return the new workflow metrics sla definition version
	 */
	@Override
	public WorkflowMetricsSLADefinitionVersion create(
		long workflowMetricsSLADefinitionVersionId) {

		WorkflowMetricsSLADefinitionVersion
			workflowMetricsSLADefinitionVersion =
				new WorkflowMetricsSLADefinitionVersionImpl();

		workflowMetricsSLADefinitionVersion.setNew(true);
		workflowMetricsSLADefinitionVersion.setPrimaryKey(
			workflowMetricsSLADefinitionVersionId);

		String uuid = PortalUUIDUtil.generate();

		workflowMetricsSLADefinitionVersion.setUuid(uuid);

		workflowMetricsSLADefinitionVersion.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return workflowMetricsSLADefinitionVersion;
	}

	/**
	 * Removes the workflow metrics sla definition version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param workflowMetricsSLADefinitionVersionId the primary key of the workflow metrics sla definition version
	 * @return the workflow metrics sla definition version that was removed
	 * @throws NoSuchSLADefinitionVersionException if a workflow metrics sla definition version with the primary key could not be found
	 */
	@Override
	public WorkflowMetricsSLADefinitionVersion remove(
			long workflowMetricsSLADefinitionVersionId)
		throws NoSuchSLADefinitionVersionException {

		return remove((Serializable)workflowMetricsSLADefinitionVersionId);
	}

	@Override
	protected WorkflowMetricsSLADefinitionVersion removeImpl(
		WorkflowMetricsSLADefinitionVersion
			workflowMetricsSLADefinitionVersion) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(workflowMetricsSLADefinitionVersion)) {
				workflowMetricsSLADefinitionVersion =
					(WorkflowMetricsSLADefinitionVersion)session.get(
						WorkflowMetricsSLADefinitionVersionImpl.class,
						workflowMetricsSLADefinitionVersion.getPrimaryKeyObj());
			}

			if (workflowMetricsSLADefinitionVersion != null) {
				session.delete(workflowMetricsSLADefinitionVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (workflowMetricsSLADefinitionVersion != null) {
			clearCache(workflowMetricsSLADefinitionVersion);
		}

		return workflowMetricsSLADefinitionVersion;
	}

	@Override
	public WorkflowMetricsSLADefinitionVersion updateImpl(
		WorkflowMetricsSLADefinitionVersion
			workflowMetricsSLADefinitionVersion) {

		boolean isNew = workflowMetricsSLADefinitionVersion.isNew();

		if (!(workflowMetricsSLADefinitionVersion instanceof
				WorkflowMetricsSLADefinitionVersionModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					workflowMetricsSLADefinitionVersion.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					workflowMetricsSLADefinitionVersion);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in workflowMetricsSLADefinitionVersion proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom WorkflowMetricsSLADefinitionVersion implementation " +
					workflowMetricsSLADefinitionVersion.getClass());
		}

		WorkflowMetricsSLADefinitionVersionModelImpl
			workflowMetricsSLADefinitionVersionModelImpl =
				(WorkflowMetricsSLADefinitionVersionModelImpl)
					workflowMetricsSLADefinitionVersion;

		if (Validator.isNull(workflowMetricsSLADefinitionVersion.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			workflowMetricsSLADefinitionVersion.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew &&
			(workflowMetricsSLADefinitionVersion.getCreateDate() == null)) {

			if (serviceContext == null) {
				workflowMetricsSLADefinitionVersion.setCreateDate(date);
			}
			else {
				workflowMetricsSLADefinitionVersion.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!workflowMetricsSLADefinitionVersionModelImpl.
				hasSetModifiedDate()) {

			if (serviceContext == null) {
				workflowMetricsSLADefinitionVersion.setModifiedDate(date);
			}
			else {
				workflowMetricsSLADefinitionVersion.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(workflowMetricsSLADefinitionVersion);
			}
			else {
				workflowMetricsSLADefinitionVersion =
					(WorkflowMetricsSLADefinitionVersion)session.merge(
						workflowMetricsSLADefinitionVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(workflowMetricsSLADefinitionVersion, false);

		if (isNew) {
			workflowMetricsSLADefinitionVersion.setNew(false);
		}

		workflowMetricsSLADefinitionVersion.resetOriginalValues();

		return workflowMetricsSLADefinitionVersion;
	}

	/**
	 * Returns the workflow metrics sla definition version with the primary key or throws a <code>NoSuchSLADefinitionVersionException</code> if it could not be found.
	 *
	 * @param workflowMetricsSLADefinitionVersionId the primary key of the workflow metrics sla definition version
	 * @return the workflow metrics sla definition version
	 * @throws NoSuchSLADefinitionVersionException if a workflow metrics sla definition version with the primary key could not be found
	 */
	@Override
	public WorkflowMetricsSLADefinitionVersion findByPrimaryKey(
			long workflowMetricsSLADefinitionVersionId)
		throws NoSuchSLADefinitionVersionException {

		return findByPrimaryKey(
			(Serializable)workflowMetricsSLADefinitionVersionId);
	}

	/**
	 * Returns the workflow metrics sla definition version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param workflowMetricsSLADefinitionVersionId the primary key of the workflow metrics sla definition version
	 * @return the workflow metrics sla definition version, or <code>null</code> if a workflow metrics sla definition version with the primary key could not be found
	 */
	@Override
	public WorkflowMetricsSLADefinitionVersion fetchByPrimaryKey(
		long workflowMetricsSLADefinitionVersionId) {

		return fetchByPrimaryKey(
			(Serializable)workflowMetricsSLADefinitionVersionId);
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
		return "wmSLADefinitionVersionId";
	}

	@Override
	protected String getPKFieldName() {
		return "workflowMetricsSLADefinitionVersionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_WORKFLOWMETRICSSLADEFINITIONVERSION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return WorkflowMetricsSLADefinitionVersionModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the workflow metrics sla definition version persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByUuid = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
				new String[] {
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"uuid_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, false, null),
			_SQL_SELECT_WORKFLOWMETRICSSLADEFINITIONVERSION_WHERE,
			_SQL_COUNT_WORKFLOWMETRICSSLADEFINITIONVERSION_WHERE,
			WorkflowMetricsSLADefinitionVersionModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"workflowMetricsSLADefinitionVersion.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				WorkflowMetricsSLADefinitionVersion::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(
					WorkflowMetricsSLADefinitionVersion::getUuid),
				WorkflowMetricsSLADefinitionVersion::getGroupId),
			_SQL_SELECT_WORKFLOWMETRICSSLADEFINITIONVERSION_WHERE, "",
			new FinderColumn<>(
				"workflowMetricsSLADefinitionVersion.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				WorkflowMetricsSLADefinitionVersion::getUuid),
			new FinderColumn<>(
				"workflowMetricsSLADefinitionVersion.", "groupId",
				FinderColumn.Type.LONG, "=", true, true,
				WorkflowMetricsSLADefinitionVersion::getGroupId));

		_collectionPersistenceFinderByUuid_C =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid_C",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"uuid_", "companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, false, null),
				_SQL_SELECT_WORKFLOWMETRICSSLADEFINITIONVERSION_WHERE,
				_SQL_COUNT_WORKFLOWMETRICSSLADEFINITIONVERSION_WHERE,
				WorkflowMetricsSLADefinitionVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"workflowMetricsSLADefinitionVersion.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					WorkflowMetricsSLADefinitionVersion::getUuid),
				new FinderColumn<>(
					"workflowMetricsSLADefinitionVersion.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					WorkflowMetricsSLADefinitionVersion::getCompanyId));

		_collectionPersistenceFinderByWorkflowMetricsSLADefinitionId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByWorkflowMetricsSLADefinitionId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"wmSLADefinitionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByWorkflowMetricsSLADefinitionId",
					new String[] {Long.class.getName()},
					new String[] {"wmSLADefinitionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByWorkflowMetricsSLADefinitionId",
					new String[] {Long.class.getName()},
					new String[] {"wmSLADefinitionId"}, false),
				_SQL_SELECT_WORKFLOWMETRICSSLADEFINITIONVERSION_WHERE,
				_SQL_COUNT_WORKFLOWMETRICSSLADEFINITIONVERSION_WHERE,
				WorkflowMetricsSLADefinitionVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"workflowMetricsSLADefinitionVersion.",
					"workflowMetricsSLADefinitionId", "wmSLADefinitionId",
					FinderColumn.Type.LONG, "=", true, true,
					WorkflowMetricsSLADefinitionVersion::
						getWorkflowMetricsSLADefinitionId));

		_uniquePersistenceFinderByV_WMSLAD = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByV_WMSLAD",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"version", "wmSLADefinitionId"}, 0, 1, false,
				convertNullFunction(
					WorkflowMetricsSLADefinitionVersion::getVersion),
				WorkflowMetricsSLADefinitionVersion::
					getWorkflowMetricsSLADefinitionId),
			_SQL_SELECT_WORKFLOWMETRICSSLADEFINITIONVERSION_WHERE, "",
			new FinderColumn<>(
				"workflowMetricsSLADefinitionVersion.", "version",
				FinderColumn.Type.STRING, "=", true, true,
				WorkflowMetricsSLADefinitionVersion::getVersion),
			new FinderColumn<>(
				"workflowMetricsSLADefinitionVersion.",
				"workflowMetricsSLADefinitionId", "wmSLADefinitionId",
				FinderColumn.Type.LONG, "=", true, true,
				WorkflowMetricsSLADefinitionVersion::
					getWorkflowMetricsSLADefinitionId));

		WorkflowMetricsSLADefinitionVersionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		WorkflowMetricsSLADefinitionVersionUtil.setPersistence(null);

		entityCache.removeCache(
			WorkflowMetricsSLADefinitionVersionImpl.class.getName());
	}

	@Override
	@Reference(
		target = WorkflowMetricsPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = WorkflowMetricsPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = WorkflowMetricsPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		WorkflowMetricsSLADefinitionVersionModelImpl.ENTITY_ALIAS + ".";

	private static final String
		_SQL_SELECT_WORKFLOWMETRICSSLADEFINITIONVERSION =
			"SELECT workflowMetricsSLADefinitionVersion FROM WorkflowMetricsSLADefinitionVersion workflowMetricsSLADefinitionVersion";

	private static final String
		_SQL_SELECT_WORKFLOWMETRICSSLADEFINITIONVERSION_WHERE =
			"SELECT workflowMetricsSLADefinitionVersion FROM WorkflowMetricsSLADefinitionVersion workflowMetricsSLADefinitionVersion WHERE ";

	private static final String
		_SQL_COUNT_WORKFLOWMETRICSSLADEFINITIONVERSION_WHERE =
			"SELECT COUNT(workflowMetricsSLADefinitionVersion) FROM WorkflowMetricsSLADefinitionVersion workflowMetricsSLADefinitionVersion WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No WorkflowMetricsSLADefinitionVersion exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		WorkflowMetricsSLADefinitionVersionPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {
			"uuid", "workflowMetricsSLADefinitionVersionId", "active",
			"workflowMetricsSLADefinitionId"
		});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-855885717