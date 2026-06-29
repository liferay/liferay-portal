/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.service.persistence.impl;

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
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
import com.liferay.portal.workflow.metrics.exception.NoSuchSLADefinitionException;
import com.liferay.portal.workflow.metrics.model.WorkflowMetricsSLADefinition;
import com.liferay.portal.workflow.metrics.model.WorkflowMetricsSLADefinitionTable;
import com.liferay.portal.workflow.metrics.model.impl.WorkflowMetricsSLADefinitionImpl;
import com.liferay.portal.workflow.metrics.model.impl.WorkflowMetricsSLADefinitionModelImpl;
import com.liferay.portal.workflow.metrics.service.persistence.WorkflowMetricsSLADefinitionPersistence;
import com.liferay.portal.workflow.metrics.service.persistence.WorkflowMetricsSLADefinitionUtil;
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
 * The persistence implementation for the workflow metrics sla definition service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = WorkflowMetricsSLADefinitionPersistence.class)
public class WorkflowMetricsSLADefinitionPersistenceImpl
	extends BasePersistenceImpl
		<WorkflowMetricsSLADefinition, NoSuchSLADefinitionException>
	implements WorkflowMetricsSLADefinitionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>WorkflowMetricsSLADefinitionUtil</code> to access the workflow metrics sla definition persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		WorkflowMetricsSLADefinitionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<WorkflowMetricsSLADefinition, NoSuchSLADefinitionException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the workflow metrics sla definitions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowMetricsSLADefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of workflow metrics sla definitions
	 * @param end the upper bound of the range of workflow metrics sla definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow metrics sla definitions
	 */
	@Override
	public List<WorkflowMetricsSLADefinition> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<WorkflowMetricsSLADefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first workflow metrics sla definition in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow metrics sla definition
	 * @throws NoSuchSLADefinitionException if a matching workflow metrics sla definition could not be found
	 */
	@Override
	public WorkflowMetricsSLADefinition findByUuid_First(
			String uuid,
			OrderByComparator<WorkflowMetricsSLADefinition> orderByComparator)
		throws NoSuchSLADefinitionException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first workflow metrics sla definition in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow metrics sla definition, or <code>null</code> if a matching workflow metrics sla definition could not be found
	 */
	@Override
	public WorkflowMetricsSLADefinition fetchByUuid_First(
		String uuid,
		OrderByComparator<WorkflowMetricsSLADefinition> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the workflow metrics sla definitions where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of workflow metrics sla definitions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching workflow metrics sla definitions
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<WorkflowMetricsSLADefinition, NoSuchSLADefinitionException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the workflow metrics sla definition where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchSLADefinitionException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching workflow metrics sla definition
	 * @throws NoSuchSLADefinitionException if a matching workflow metrics sla definition could not be found
	 */
	@Override
	public WorkflowMetricsSLADefinition findByUUID_G(String uuid, long groupId)
		throws NoSuchSLADefinitionException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the workflow metrics sla definition where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching workflow metrics sla definition, or <code>null</code> if a matching workflow metrics sla definition could not be found
	 */
	@Override
	public WorkflowMetricsSLADefinition fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the workflow metrics sla definition where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the workflow metrics sla definition that was removed
	 */
	@Override
	public WorkflowMetricsSLADefinition removeByUUID_G(
			String uuid, long groupId)
		throws NoSuchSLADefinitionException {

		WorkflowMetricsSLADefinition workflowMetricsSLADefinition =
			findByUUID_G(uuid, groupId);

		return remove(workflowMetricsSLADefinition);
	}

	/**
	 * Returns the number of workflow metrics sla definitions where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching workflow metrics sla definitions
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<WorkflowMetricsSLADefinition, NoSuchSLADefinitionException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the workflow metrics sla definitions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowMetricsSLADefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of workflow metrics sla definitions
	 * @param end the upper bound of the range of workflow metrics sla definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow metrics sla definitions
	 */
	@Override
	public List<WorkflowMetricsSLADefinition> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<WorkflowMetricsSLADefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first workflow metrics sla definition in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow metrics sla definition
	 * @throws NoSuchSLADefinitionException if a matching workflow metrics sla definition could not be found
	 */
	@Override
	public WorkflowMetricsSLADefinition findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<WorkflowMetricsSLADefinition> orderByComparator)
		throws NoSuchSLADefinitionException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first workflow metrics sla definition in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow metrics sla definition, or <code>null</code> if a matching workflow metrics sla definition could not be found
	 */
	@Override
	public WorkflowMetricsSLADefinition fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<WorkflowMetricsSLADefinition> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the workflow metrics sla definitions where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of workflow metrics sla definitions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching workflow metrics sla definitions
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private UniquePersistenceFinder
		<WorkflowMetricsSLADefinition, NoSuchSLADefinitionException>
			_uniquePersistenceFinderByWMSLAD_A;

	/**
	 * Returns the workflow metrics sla definition where workflowMetricsSLADefinitionId = &#63; and active = &#63; or throws a <code>NoSuchSLADefinitionException</code> if it could not be found.
	 *
	 * @param workflowMetricsSLADefinitionId the workflow metrics sla definition ID
	 * @param active the active
	 * @return the matching workflow metrics sla definition
	 * @throws NoSuchSLADefinitionException if a matching workflow metrics sla definition could not be found
	 */
	@Override
	public WorkflowMetricsSLADefinition findByWMSLAD_A(
			long workflowMetricsSLADefinitionId, boolean active)
		throws NoSuchSLADefinitionException {

		return _uniquePersistenceFinderByWMSLAD_A.find(
			finderCache, new Object[] {workflowMetricsSLADefinitionId, active});
	}

	/**
	 * Returns the workflow metrics sla definition where workflowMetricsSLADefinitionId = &#63; and active = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param workflowMetricsSLADefinitionId the workflow metrics sla definition ID
	 * @param active the active
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching workflow metrics sla definition, or <code>null</code> if a matching workflow metrics sla definition could not be found
	 */
	@Override
	public WorkflowMetricsSLADefinition fetchByWMSLAD_A(
		long workflowMetricsSLADefinitionId, boolean active,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByWMSLAD_A.fetch(
			finderCache, new Object[] {workflowMetricsSLADefinitionId, active},
			useFinderCache);
	}

	/**
	 * Removes the workflow metrics sla definition where workflowMetricsSLADefinitionId = &#63; and active = &#63; from the database.
	 *
	 * @param workflowMetricsSLADefinitionId the workflow metrics sla definition ID
	 * @param active the active
	 * @return the workflow metrics sla definition that was removed
	 */
	@Override
	public WorkflowMetricsSLADefinition removeByWMSLAD_A(
			long workflowMetricsSLADefinitionId, boolean active)
		throws NoSuchSLADefinitionException {

		WorkflowMetricsSLADefinition workflowMetricsSLADefinition =
			findByWMSLAD_A(workflowMetricsSLADefinitionId, active);

		return remove(workflowMetricsSLADefinition);
	}

	/**
	 * Returns the number of workflow metrics sla definitions where workflowMetricsSLADefinitionId = &#63; and active = &#63;.
	 *
	 * @param workflowMetricsSLADefinitionId the workflow metrics sla definition ID
	 * @param active the active
	 * @return the number of matching workflow metrics sla definitions
	 */
	@Override
	public int countByWMSLAD_A(
		long workflowMetricsSLADefinitionId, boolean active) {

		return _uniquePersistenceFinderByWMSLAD_A.count(
			finderCache, new Object[] {workflowMetricsSLADefinitionId, active});
	}

	private CollectionPersistenceFinder
		<WorkflowMetricsSLADefinition, NoSuchSLADefinitionException>
			_collectionPersistenceFinderByC_S;

	/**
	 * Returns an ordered range of all the workflow metrics sla definitions where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowMetricsSLADefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of workflow metrics sla definitions
	 * @param end the upper bound of the range of workflow metrics sla definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow metrics sla definitions
	 */
	@Override
	public List<WorkflowMetricsSLADefinition> findByC_S(
		long companyId, int status, int start, int end,
		OrderByComparator<WorkflowMetricsSLADefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_S.find(
			finderCache, new Object[] {companyId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first workflow metrics sla definition in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow metrics sla definition
	 * @throws NoSuchSLADefinitionException if a matching workflow metrics sla definition could not be found
	 */
	@Override
	public WorkflowMetricsSLADefinition findByC_S_First(
			long companyId, int status,
			OrderByComparator<WorkflowMetricsSLADefinition> orderByComparator)
		throws NoSuchSLADefinitionException {

		return _collectionPersistenceFinderByC_S.findFirst(
			finderCache, new Object[] {companyId, status}, orderByComparator);
	}

	/**
	 * Returns the first workflow metrics sla definition in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow metrics sla definition, or <code>null</code> if a matching workflow metrics sla definition could not be found
	 */
	@Override
	public WorkflowMetricsSLADefinition fetchByC_S_First(
		long companyId, int status,
		OrderByComparator<WorkflowMetricsSLADefinition> orderByComparator) {

		return _collectionPersistenceFinderByC_S.fetchFirst(
			finderCache, new Object[] {companyId, status}, orderByComparator);
	}

	/**
	 * Removes all the workflow metrics sla definitions where companyId = &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 */
	@Override
	public void removeByC_S(long companyId, int status) {
		_collectionPersistenceFinderByC_S.remove(
			finderCache, new Object[] {companyId, status});
	}

	/**
	 * Returns the number of workflow metrics sla definitions where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching workflow metrics sla definitions
	 */
	@Override
	public int countByC_S(long companyId, int status) {
		return _collectionPersistenceFinderByC_S.count(
			finderCache, new Object[] {companyId, status});
	}

	private CollectionPersistenceFinder
		<WorkflowMetricsSLADefinition, NoSuchSLADefinitionException>
			_collectionPersistenceFinderByC_A_P;

	/**
	 * Returns an ordered range of all the workflow metrics sla definitions where companyId = &#63; and active = &#63; and processId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowMetricsSLADefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param processId the process ID
	 * @param start the lower bound of the range of workflow metrics sla definitions
	 * @param end the upper bound of the range of workflow metrics sla definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow metrics sla definitions
	 */
	@Override
	public List<WorkflowMetricsSLADefinition> findByC_A_P(
		long companyId, boolean active, long processId, int start, int end,
		OrderByComparator<WorkflowMetricsSLADefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_A_P.find(
			finderCache, new Object[] {companyId, active, processId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first workflow metrics sla definition in the ordered set where companyId = &#63; and active = &#63; and processId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param processId the process ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow metrics sla definition
	 * @throws NoSuchSLADefinitionException if a matching workflow metrics sla definition could not be found
	 */
	@Override
	public WorkflowMetricsSLADefinition findByC_A_P_First(
			long companyId, boolean active, long processId,
			OrderByComparator<WorkflowMetricsSLADefinition> orderByComparator)
		throws NoSuchSLADefinitionException {

		return _collectionPersistenceFinderByC_A_P.findFirst(
			finderCache, new Object[] {companyId, active, processId},
			orderByComparator);
	}

	/**
	 * Returns the first workflow metrics sla definition in the ordered set where companyId = &#63; and active = &#63; and processId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param processId the process ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow metrics sla definition, or <code>null</code> if a matching workflow metrics sla definition could not be found
	 */
	@Override
	public WorkflowMetricsSLADefinition fetchByC_A_P_First(
		long companyId, boolean active, long processId,
		OrderByComparator<WorkflowMetricsSLADefinition> orderByComparator) {

		return _collectionPersistenceFinderByC_A_P.fetchFirst(
			finderCache, new Object[] {companyId, active, processId},
			orderByComparator);
	}

	/**
	 * Removes all the workflow metrics sla definitions where companyId = &#63; and active = &#63; and processId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param processId the process ID
	 */
	@Override
	public void removeByC_A_P(long companyId, boolean active, long processId) {
		_collectionPersistenceFinderByC_A_P.remove(
			finderCache, new Object[] {companyId, active, processId});
	}

	/**
	 * Returns the number of workflow metrics sla definitions where companyId = &#63; and active = &#63; and processId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param processId the process ID
	 * @return the number of matching workflow metrics sla definitions
	 */
	@Override
	public int countByC_A_P(long companyId, boolean active, long processId) {
		return _collectionPersistenceFinderByC_A_P.count(
			finderCache, new Object[] {companyId, active, processId});
	}

	private CollectionPersistenceFinder
		<WorkflowMetricsSLADefinition, NoSuchSLADefinitionException>
			_collectionPersistenceFinderByC_A_N_P;

	/**
	 * Returns an ordered range of all the workflow metrics sla definitions where companyId = &#63; and active = &#63; and name = &#63; and processId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowMetricsSLADefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param name the name
	 * @param processId the process ID
	 * @param start the lower bound of the range of workflow metrics sla definitions
	 * @param end the upper bound of the range of workflow metrics sla definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow metrics sla definitions
	 */
	@Override
	public List<WorkflowMetricsSLADefinition> findByC_A_N_P(
		long companyId, boolean active, String name, long processId, int start,
		int end,
		OrderByComparator<WorkflowMetricsSLADefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_A_N_P.find(
			finderCache, new Object[] {companyId, active, name, processId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first workflow metrics sla definition in the ordered set where companyId = &#63; and active = &#63; and name = &#63; and processId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param name the name
	 * @param processId the process ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow metrics sla definition
	 * @throws NoSuchSLADefinitionException if a matching workflow metrics sla definition could not be found
	 */
	@Override
	public WorkflowMetricsSLADefinition findByC_A_N_P_First(
			long companyId, boolean active, String name, long processId,
			OrderByComparator<WorkflowMetricsSLADefinition> orderByComparator)
		throws NoSuchSLADefinitionException {

		return _collectionPersistenceFinderByC_A_N_P.findFirst(
			finderCache, new Object[] {companyId, active, name, processId},
			orderByComparator);
	}

	/**
	 * Returns the first workflow metrics sla definition in the ordered set where companyId = &#63; and active = &#63; and name = &#63; and processId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param name the name
	 * @param processId the process ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow metrics sla definition, or <code>null</code> if a matching workflow metrics sla definition could not be found
	 */
	@Override
	public WorkflowMetricsSLADefinition fetchByC_A_N_P_First(
		long companyId, boolean active, String name, long processId,
		OrderByComparator<WorkflowMetricsSLADefinition> orderByComparator) {

		return _collectionPersistenceFinderByC_A_N_P.fetchFirst(
			finderCache, new Object[] {companyId, active, name, processId},
			orderByComparator);
	}

	/**
	 * Removes all the workflow metrics sla definitions where companyId = &#63; and active = &#63; and name = &#63; and processId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param name the name
	 * @param processId the process ID
	 */
	@Override
	public void removeByC_A_N_P(
		long companyId, boolean active, String name, long processId) {

		_collectionPersistenceFinderByC_A_N_P.remove(
			finderCache, new Object[] {companyId, active, name, processId});
	}

	/**
	 * Returns the number of workflow metrics sla definitions where companyId = &#63; and active = &#63; and name = &#63; and processId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param name the name
	 * @param processId the process ID
	 * @return the number of matching workflow metrics sla definitions
	 */
	@Override
	public int countByC_A_N_P(
		long companyId, boolean active, String name, long processId) {

		return _collectionPersistenceFinderByC_A_N_P.count(
			finderCache, new Object[] {companyId, active, name, processId});
	}

	private CollectionPersistenceFinder
		<WorkflowMetricsSLADefinition, NoSuchSLADefinitionException>
			_collectionPersistenceFinderByC_A_P_S;

	/**
	 * Returns an ordered range of all the workflow metrics sla definitions where companyId = &#63; and active = &#63; and processId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowMetricsSLADefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param processId the process ID
	 * @param status the status
	 * @param start the lower bound of the range of workflow metrics sla definitions
	 * @param end the upper bound of the range of workflow metrics sla definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow metrics sla definitions
	 */
	@Override
	public List<WorkflowMetricsSLADefinition> findByC_A_P_S(
		long companyId, boolean active, long processId, int status, int start,
		int end,
		OrderByComparator<WorkflowMetricsSLADefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_A_P_S.find(
			finderCache, new Object[] {companyId, active, processId, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first workflow metrics sla definition in the ordered set where companyId = &#63; and active = &#63; and processId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param processId the process ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow metrics sla definition
	 * @throws NoSuchSLADefinitionException if a matching workflow metrics sla definition could not be found
	 */
	@Override
	public WorkflowMetricsSLADefinition findByC_A_P_S_First(
			long companyId, boolean active, long processId, int status,
			OrderByComparator<WorkflowMetricsSLADefinition> orderByComparator)
		throws NoSuchSLADefinitionException {

		return _collectionPersistenceFinderByC_A_P_S.findFirst(
			finderCache, new Object[] {companyId, active, processId, status},
			orderByComparator);
	}

	/**
	 * Returns the first workflow metrics sla definition in the ordered set where companyId = &#63; and active = &#63; and processId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param processId the process ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow metrics sla definition, or <code>null</code> if a matching workflow metrics sla definition could not be found
	 */
	@Override
	public WorkflowMetricsSLADefinition fetchByC_A_P_S_First(
		long companyId, boolean active, long processId, int status,
		OrderByComparator<WorkflowMetricsSLADefinition> orderByComparator) {

		return _collectionPersistenceFinderByC_A_P_S.fetchFirst(
			finderCache, new Object[] {companyId, active, processId, status},
			orderByComparator);
	}

	/**
	 * Removes all the workflow metrics sla definitions where companyId = &#63; and active = &#63; and processId = &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param processId the process ID
	 * @param status the status
	 */
	@Override
	public void removeByC_A_P_S(
		long companyId, boolean active, long processId, int status) {

		_collectionPersistenceFinderByC_A_P_S.remove(
			finderCache, new Object[] {companyId, active, processId, status});
	}

	/**
	 * Returns the number of workflow metrics sla definitions where companyId = &#63; and active = &#63; and processId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param processId the process ID
	 * @param status the status
	 * @return the number of matching workflow metrics sla definitions
	 */
	@Override
	public int countByC_A_P_S(
		long companyId, boolean active, long processId, int status) {

		return _collectionPersistenceFinderByC_A_P_S.count(
			finderCache, new Object[] {companyId, active, processId, status});
	}

	private CollectionPersistenceFinder
		<WorkflowMetricsSLADefinition, NoSuchSLADefinitionException>
			_collectionPersistenceFinderByC_A_P_NotPV_S;

	/**
	 * Returns all the workflow metrics sla definitions where companyId = &#63; and active = &#63; and processId = &#63; and processVersion &ne; &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param processId the process ID
	 * @param processVersion the process version
	 * @param status the status
	 * @return the matching workflow metrics sla definitions
	 */
	@Override
	public List<WorkflowMetricsSLADefinition> findByC_A_P_NotPV_S(
		long companyId, boolean active, long processId, String processVersion,
		int status) {

		return findByC_A_P_NotPV_S(
			companyId, active, processId, processVersion, status,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the workflow metrics sla definitions where companyId = &#63; and active = &#63; and processId = &#63; and processVersion &ne; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowMetricsSLADefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param processId the process ID
	 * @param processVersion the process version
	 * @param status the status
	 * @param start the lower bound of the range of workflow metrics sla definitions
	 * @param end the upper bound of the range of workflow metrics sla definitions (not inclusive)
	 * @return the range of matching workflow metrics sla definitions
	 */
	@Override
	public List<WorkflowMetricsSLADefinition> findByC_A_P_NotPV_S(
		long companyId, boolean active, long processId, String processVersion,
		int status, int start, int end) {

		return findByC_A_P_NotPV_S(
			companyId, active, processId, processVersion, status, start, end,
			null);
	}

	/**
	 * Returns an ordered range of all the workflow metrics sla definitions where companyId = &#63; and active = &#63; and processId = &#63; and processVersion &ne; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowMetricsSLADefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param processId the process ID
	 * @param processVersion the process version
	 * @param status the status
	 * @param start the lower bound of the range of workflow metrics sla definitions
	 * @param end the upper bound of the range of workflow metrics sla definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching workflow metrics sla definitions
	 */
	@Override
	public List<WorkflowMetricsSLADefinition> findByC_A_P_NotPV_S(
		long companyId, boolean active, long processId, String processVersion,
		int status, int start, int end,
		OrderByComparator<WorkflowMetricsSLADefinition> orderByComparator) {

		return findByC_A_P_NotPV_S(
			companyId, active, processId, processVersion, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the workflow metrics sla definitions where companyId = &#63; and active = &#63; and processId = &#63; and processVersion &ne; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowMetricsSLADefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param processId the process ID
	 * @param processVersion the process version
	 * @param status the status
	 * @param start the lower bound of the range of workflow metrics sla definitions
	 * @param end the upper bound of the range of workflow metrics sla definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow metrics sla definitions
	 */
	@Override
	public List<WorkflowMetricsSLADefinition> findByC_A_P_NotPV_S(
		long companyId, boolean active, long processId, String processVersion,
		int status, int start, int end,
		OrderByComparator<WorkflowMetricsSLADefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_A_P_NotPV_S.find(
			finderCache,
			new Object[] {companyId, active, processId, processVersion, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first workflow metrics sla definition in the ordered set where companyId = &#63; and active = &#63; and processId = &#63; and processVersion &ne; &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param processId the process ID
	 * @param processVersion the process version
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow metrics sla definition
	 * @throws NoSuchSLADefinitionException if a matching workflow metrics sla definition could not be found
	 */
	@Override
	public WorkflowMetricsSLADefinition findByC_A_P_NotPV_S_First(
			long companyId, boolean active, long processId,
			String processVersion, int status,
			OrderByComparator<WorkflowMetricsSLADefinition> orderByComparator)
		throws NoSuchSLADefinitionException {

		return _collectionPersistenceFinderByC_A_P_NotPV_S.findFirst(
			finderCache,
			new Object[] {companyId, active, processId, processVersion, status},
			orderByComparator);
	}

	/**
	 * Returns the first workflow metrics sla definition in the ordered set where companyId = &#63; and active = &#63; and processId = &#63; and processVersion &ne; &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param processId the process ID
	 * @param processVersion the process version
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow metrics sla definition, or <code>null</code> if a matching workflow metrics sla definition could not be found
	 */
	@Override
	public WorkflowMetricsSLADefinition fetchByC_A_P_NotPV_S_First(
		long companyId, boolean active, long processId, String processVersion,
		int status,
		OrderByComparator<WorkflowMetricsSLADefinition> orderByComparator) {

		return _collectionPersistenceFinderByC_A_P_NotPV_S.fetchFirst(
			finderCache,
			new Object[] {companyId, active, processId, processVersion, status},
			orderByComparator);
	}

	/**
	 * Removes all the workflow metrics sla definitions where companyId = &#63; and active = &#63; and processId = &#63; and processVersion &ne; &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param processId the process ID
	 * @param processVersion the process version
	 * @param status the status
	 */
	@Override
	public void removeByC_A_P_NotPV_S(
		long companyId, boolean active, long processId, String processVersion,
		int status) {

		_collectionPersistenceFinderByC_A_P_NotPV_S.remove(
			finderCache,
			new Object[] {
				companyId, active, processId, processVersion, status
			});
	}

	/**
	 * Returns the number of workflow metrics sla definitions where companyId = &#63; and active = &#63; and processId = &#63; and processVersion &ne; &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param processId the process ID
	 * @param processVersion the process version
	 * @param status the status
	 * @return the number of matching workflow metrics sla definitions
	 */
	@Override
	public int countByC_A_P_NotPV_S(
		long companyId, boolean active, long processId, String processVersion,
		int status) {

		return _collectionPersistenceFinderByC_A_P_NotPV_S.count(
			finderCache,
			new Object[] {
				companyId, active, processId, processVersion, status
			});
	}

	public WorkflowMetricsSLADefinitionPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put(
			"workflowMetricsSLADefinitionId", "wmSLADefinitionId");
		dbColumnNames.put("active", "active_");

		setDBColumnNames(dbColumnNames);

		setModelClass(WorkflowMetricsSLADefinition.class);

		setModelImplClass(WorkflowMetricsSLADefinitionImpl.class);
		setModelPKClass(long.class);

		setTable(WorkflowMetricsSLADefinitionTable.INSTANCE);
	}

	/**
	 * Creates a new workflow metrics sla definition with the primary key. Does not add the workflow metrics sla definition to the database.
	 *
	 * @param workflowMetricsSLADefinitionId the primary key for the new workflow metrics sla definition
	 * @return the new workflow metrics sla definition
	 */
	@Override
	public WorkflowMetricsSLADefinition create(
		long workflowMetricsSLADefinitionId) {

		WorkflowMetricsSLADefinition workflowMetricsSLADefinition =
			new WorkflowMetricsSLADefinitionImpl();

		workflowMetricsSLADefinition.setNew(true);
		workflowMetricsSLADefinition.setPrimaryKey(
			workflowMetricsSLADefinitionId);

		String uuid = PortalUUIDUtil.generate();

		workflowMetricsSLADefinition.setUuid(uuid);

		workflowMetricsSLADefinition.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return workflowMetricsSLADefinition;
	}

	/**
	 * Removes the workflow metrics sla definition with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param workflowMetricsSLADefinitionId the primary key of the workflow metrics sla definition
	 * @return the workflow metrics sla definition that was removed
	 * @throws NoSuchSLADefinitionException if a workflow metrics sla definition with the primary key could not be found
	 */
	@Override
	public WorkflowMetricsSLADefinition remove(
			long workflowMetricsSLADefinitionId)
		throws NoSuchSLADefinitionException {

		return remove((Serializable)workflowMetricsSLADefinitionId);
	}

	@Override
	protected WorkflowMetricsSLADefinition removeImpl(
		WorkflowMetricsSLADefinition workflowMetricsSLADefinition) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(workflowMetricsSLADefinition)) {
				workflowMetricsSLADefinition =
					(WorkflowMetricsSLADefinition)session.get(
						WorkflowMetricsSLADefinitionImpl.class,
						workflowMetricsSLADefinition.getPrimaryKeyObj());
			}

			if (workflowMetricsSLADefinition != null) {
				session.delete(workflowMetricsSLADefinition);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (workflowMetricsSLADefinition != null) {
			clearCache(workflowMetricsSLADefinition);
		}

		return workflowMetricsSLADefinition;
	}

	@Override
	public WorkflowMetricsSLADefinition updateImpl(
		WorkflowMetricsSLADefinition workflowMetricsSLADefinition) {

		boolean isNew = workflowMetricsSLADefinition.isNew();

		if (!(workflowMetricsSLADefinition instanceof
				WorkflowMetricsSLADefinitionModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					workflowMetricsSLADefinition.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					workflowMetricsSLADefinition);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in workflowMetricsSLADefinition proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom WorkflowMetricsSLADefinition implementation " +
					workflowMetricsSLADefinition.getClass());
		}

		WorkflowMetricsSLADefinitionModelImpl
			workflowMetricsSLADefinitionModelImpl =
				(WorkflowMetricsSLADefinitionModelImpl)
					workflowMetricsSLADefinition;

		if (Validator.isNull(workflowMetricsSLADefinition.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			workflowMetricsSLADefinition.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (workflowMetricsSLADefinition.getCreateDate() == null)) {
			if (serviceContext == null) {
				workflowMetricsSLADefinition.setCreateDate(date);
			}
			else {
				workflowMetricsSLADefinition.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!workflowMetricsSLADefinitionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				workflowMetricsSLADefinition.setModifiedDate(date);
			}
			else {
				workflowMetricsSLADefinition.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(workflowMetricsSLADefinition);
			}
			else {
				workflowMetricsSLADefinition =
					(WorkflowMetricsSLADefinition)session.merge(
						workflowMetricsSLADefinition);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(workflowMetricsSLADefinition, false);

		if (isNew) {
			workflowMetricsSLADefinition.setNew(false);
		}

		workflowMetricsSLADefinition.resetOriginalValues();

		return workflowMetricsSLADefinition;
	}

	/**
	 * Returns the workflow metrics sla definition with the primary key or throws a <code>NoSuchSLADefinitionException</code> if it could not be found.
	 *
	 * @param workflowMetricsSLADefinitionId the primary key of the workflow metrics sla definition
	 * @return the workflow metrics sla definition
	 * @throws NoSuchSLADefinitionException if a workflow metrics sla definition with the primary key could not be found
	 */
	@Override
	public WorkflowMetricsSLADefinition findByPrimaryKey(
			long workflowMetricsSLADefinitionId)
		throws NoSuchSLADefinitionException {

		return findByPrimaryKey((Serializable)workflowMetricsSLADefinitionId);
	}

	/**
	 * Returns the workflow metrics sla definition with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param workflowMetricsSLADefinitionId the primary key of the workflow metrics sla definition
	 * @return the workflow metrics sla definition, or <code>null</code> if a workflow metrics sla definition with the primary key could not be found
	 */
	@Override
	public WorkflowMetricsSLADefinition fetchByPrimaryKey(
		long workflowMetricsSLADefinitionId) {

		return fetchByPrimaryKey((Serializable)workflowMetricsSLADefinitionId);
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
		return "wmSLADefinitionId";
	}

	@Override
	protected String getPKFieldName() {
		return "workflowMetricsSLADefinitionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_WORKFLOWMETRICSSLADEFINITION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return WorkflowMetricsSLADefinitionModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the workflow metrics sla definition persistence.
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
			_SQL_SELECT_WORKFLOWMETRICSSLADEFINITION_WHERE,
			_SQL_COUNT_WORKFLOWMETRICSSLADEFINITION_WHERE,
			WorkflowMetricsSLADefinitionModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"workflowMetricsSLADefinition.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				WorkflowMetricsSLADefinition::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(WorkflowMetricsSLADefinition::getUuid),
				WorkflowMetricsSLADefinition::getGroupId),
			_SQL_SELECT_WORKFLOWMETRICSSLADEFINITION_WHERE, "",
			new FinderColumn<>(
				"workflowMetricsSLADefinition.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				WorkflowMetricsSLADefinition::getUuid),
			new FinderColumn<>(
				"workflowMetricsSLADefinition.", "groupId",
				FinderColumn.Type.LONG, "=", true, true,
				WorkflowMetricsSLADefinition::getGroupId));

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
				_SQL_SELECT_WORKFLOWMETRICSSLADEFINITION_WHERE,
				_SQL_COUNT_WORKFLOWMETRICSSLADEFINITION_WHERE,
				WorkflowMetricsSLADefinitionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"workflowMetricsSLADefinition.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					WorkflowMetricsSLADefinition::getUuid),
				new FinderColumn<>(
					"workflowMetricsSLADefinition.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					WorkflowMetricsSLADefinition::getCompanyId));

		_uniquePersistenceFinderByWMSLAD_A = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByWMSLAD_A",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"wmSLADefinitionId", "active_"}, 0, 0, false,
				WorkflowMetricsSLADefinition::getWorkflowMetricsSLADefinitionId,
				WorkflowMetricsSLADefinition::isActive),
			_SQL_SELECT_WORKFLOWMETRICSSLADEFINITION_WHERE, "",
			new FinderColumn<>(
				"workflowMetricsSLADefinition.",
				"workflowMetricsSLADefinitionId", "wmSLADefinitionId",
				FinderColumn.Type.LONG, "=", true, true,
				WorkflowMetricsSLADefinition::
					getWorkflowMetricsSLADefinitionId),
			new FinderColumn<>(
				"workflowMetricsSLADefinition.", "active", "active_",
				FinderColumn.Type.BOOLEAN, "=", true, true,
				WorkflowMetricsSLADefinition::isActive));

		_collectionPersistenceFinderByC_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_S",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"companyId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"companyId", "status"}, false),
			_SQL_SELECT_WORKFLOWMETRICSSLADEFINITION_WHERE,
			_SQL_COUNT_WORKFLOWMETRICSSLADEFINITION_WHERE,
			WorkflowMetricsSLADefinitionModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"workflowMetricsSLADefinition.", "companyId",
				FinderColumn.Type.LONG, "=", true, true,
				WorkflowMetricsSLADefinition::getCompanyId),
			new FinderColumn<>(
				"workflowMetricsSLADefinition.", "status",
				FinderColumn.Type.INTEGER, "=", true, true,
				WorkflowMetricsSLADefinition::getStatus));

		_collectionPersistenceFinderByC_A_P = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_A_P",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"companyId", "active_", "processId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_A_P",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Long.class.getName()
				},
				new String[] {"companyId", "active_", "processId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_A_P",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Long.class.getName()
				},
				new String[] {"companyId", "active_", "processId"}, false),
			_SQL_SELECT_WORKFLOWMETRICSSLADEFINITION_WHERE,
			_SQL_COUNT_WORKFLOWMETRICSSLADEFINITION_WHERE,
			WorkflowMetricsSLADefinitionModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"workflowMetricsSLADefinition.", "companyId",
				FinderColumn.Type.LONG, "=", true, true,
				WorkflowMetricsSLADefinition::getCompanyId),
			new FinderColumn<>(
				"workflowMetricsSLADefinition.", "active", "active_",
				FinderColumn.Type.BOOLEAN, "=", true, true,
				WorkflowMetricsSLADefinition::isActive),
			new FinderColumn<>(
				"workflowMetricsSLADefinition.", "processId",
				FinderColumn.Type.LONG, "=", true, true,
				WorkflowMetricsSLADefinition::getProcessId));

		_collectionPersistenceFinderByC_A_N_P =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_A_N_P",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "active_", "name", "processId"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_A_N_P",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName(), Long.class.getName()
					},
					new String[] {"companyId", "active_", "name", "processId"},
					0, 4, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_A_N_P",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName(), Long.class.getName()
					},
					new String[] {"companyId", "active_", "name", "processId"},
					0, 4, false, null),
				_SQL_SELECT_WORKFLOWMETRICSSLADEFINITION_WHERE,
				_SQL_COUNT_WORKFLOWMETRICSSLADEFINITION_WHERE,
				WorkflowMetricsSLADefinitionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"workflowMetricsSLADefinition.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					WorkflowMetricsSLADefinition::getCompanyId),
				new FinderColumn<>(
					"workflowMetricsSLADefinition.", "active", "active_",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					WorkflowMetricsSLADefinition::isActive),
				new FinderColumn<>(
					"workflowMetricsSLADefinition.", "name",
					FinderColumn.Type.STRING, "=", true, true,
					WorkflowMetricsSLADefinition::getName),
				new FinderColumn<>(
					"workflowMetricsSLADefinition.", "processId",
					FinderColumn.Type.LONG, "=", true, true,
					WorkflowMetricsSLADefinition::getProcessId));

		_collectionPersistenceFinderByC_A_P_S =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_A_P_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"companyId", "active_", "processId", "status"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_A_P_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {
						"companyId", "active_", "processId", "status"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_A_P_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {
						"companyId", "active_", "processId", "status"
					},
					false),
				_SQL_SELECT_WORKFLOWMETRICSSLADEFINITION_WHERE,
				_SQL_COUNT_WORKFLOWMETRICSSLADEFINITION_WHERE,
				WorkflowMetricsSLADefinitionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"workflowMetricsSLADefinition.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					WorkflowMetricsSLADefinition::getCompanyId),
				new FinderColumn<>(
					"workflowMetricsSLADefinition.", "active", "active_",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					WorkflowMetricsSLADefinition::isActive),
				new FinderColumn<>(
					"workflowMetricsSLADefinition.", "processId",
					FinderColumn.Type.LONG, "=", true, true,
					WorkflowMetricsSLADefinition::getProcessId),
				new FinderColumn<>(
					"workflowMetricsSLADefinition.", "status",
					FinderColumn.Type.INTEGER, "=", true, true,
					WorkflowMetricsSLADefinition::getStatus));

		_collectionPersistenceFinderByC_A_P_NotPV_S =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByC_A_P_NotPV_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"companyId", "active_", "processId", "processVersion",
						"status"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByC_A_P_NotPV_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Long.class.getName(), String.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"companyId", "active_", "processId", "processVersion",
						"status"
					},
					false),
				_SQL_SELECT_WORKFLOWMETRICSSLADEFINITION_WHERE,
				_SQL_COUNT_WORKFLOWMETRICSSLADEFINITION_WHERE,
				WorkflowMetricsSLADefinitionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"workflowMetricsSLADefinition.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					WorkflowMetricsSLADefinition::getCompanyId),
				new FinderColumn<>(
					"workflowMetricsSLADefinition.", "active", "active_",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					WorkflowMetricsSLADefinition::isActive),
				new FinderColumn<>(
					"workflowMetricsSLADefinition.", "processId",
					FinderColumn.Type.LONG, "=", true, true,
					WorkflowMetricsSLADefinition::getProcessId),
				new FinderColumn<>(
					"workflowMetricsSLADefinition.", "processVersion",
					FinderColumn.Type.STRING, "!=", true, true,
					WorkflowMetricsSLADefinition::getProcessVersion),
				new FinderColumn<>(
					"workflowMetricsSLADefinition.", "status",
					FinderColumn.Type.INTEGER, "=", true, true,
					WorkflowMetricsSLADefinition::getStatus));

		WorkflowMetricsSLADefinitionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		WorkflowMetricsSLADefinitionUtil.setPersistence(null);

		entityCache.removeCache(
			WorkflowMetricsSLADefinitionImpl.class.getName());
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
		WorkflowMetricsSLADefinitionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_WORKFLOWMETRICSSLADEFINITION =
		"SELECT workflowMetricsSLADefinition FROM WorkflowMetricsSLADefinition workflowMetricsSLADefinition";

	private static final String _SQL_SELECT_WORKFLOWMETRICSSLADEFINITION_WHERE =
		"SELECT workflowMetricsSLADefinition FROM WorkflowMetricsSLADefinition workflowMetricsSLADefinition WHERE ";

	private static final String _SQL_COUNT_WORKFLOWMETRICSSLADEFINITION_WHERE =
		"SELECT COUNT(workflowMetricsSLADefinition) FROM WorkflowMetricsSLADefinition workflowMetricsSLADefinition WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No WorkflowMetricsSLADefinition exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		WorkflowMetricsSLADefinitionPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "workflowMetricsSLADefinitionId", "active"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1245456515