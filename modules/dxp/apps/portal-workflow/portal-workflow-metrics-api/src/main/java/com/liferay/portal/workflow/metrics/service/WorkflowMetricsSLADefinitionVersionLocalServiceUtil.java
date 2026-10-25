/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.workflow.metrics.model.WorkflowMetricsSLADefinitionVersion;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for WorkflowMetricsSLADefinitionVersion. This utility wraps
 * <code>com.liferay.portal.workflow.metrics.service.impl.WorkflowMetricsSLADefinitionVersionLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see WorkflowMetricsSLADefinitionVersionLocalService
 * @generated
 */
public class WorkflowMetricsSLADefinitionVersionLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.workflow.metrics.service.impl.WorkflowMetricsSLADefinitionVersionLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the workflow metrics sla definition version to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect WorkflowMetricsSLADefinitionVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param workflowMetricsSLADefinitionVersion the workflow metrics sla definition version
	 * @return the workflow metrics sla definition version that was added
	 */
	public static WorkflowMetricsSLADefinitionVersion
		addWorkflowMetricsSLADefinitionVersion(
			WorkflowMetricsSLADefinitionVersion
				workflowMetricsSLADefinitionVersion) {

		return getService().addWorkflowMetricsSLADefinitionVersion(
			workflowMetricsSLADefinitionVersion);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new workflow metrics sla definition version with the primary key. Does not add the workflow metrics sla definition version to the database.
	 *
	 * @param workflowMetricsSLADefinitionVersionId the primary key for the new workflow metrics sla definition version
	 * @return the new workflow metrics sla definition version
	 */
	public static WorkflowMetricsSLADefinitionVersion
		createWorkflowMetricsSLADefinitionVersion(
			long workflowMetricsSLADefinitionVersionId) {

		return getService().createWorkflowMetricsSLADefinitionVersion(
			workflowMetricsSLADefinitionVersionId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	/**
	 * Deletes the workflow metrics sla definition version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect WorkflowMetricsSLADefinitionVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param workflowMetricsSLADefinitionVersionId the primary key of the workflow metrics sla definition version
	 * @return the workflow metrics sla definition version that was removed
	 * @throws PortalException if a workflow metrics sla definition version with the primary key could not be found
	 */
	public static WorkflowMetricsSLADefinitionVersion
			deleteWorkflowMetricsSLADefinitionVersion(
				long workflowMetricsSLADefinitionVersionId)
		throws PortalException {

		return getService().deleteWorkflowMetricsSLADefinitionVersion(
			workflowMetricsSLADefinitionVersionId);
	}

	/**
	 * Deletes the workflow metrics sla definition version from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect WorkflowMetricsSLADefinitionVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param workflowMetricsSLADefinitionVersion the workflow metrics sla definition version
	 * @return the workflow metrics sla definition version that was removed
	 */
	public static WorkflowMetricsSLADefinitionVersion
		deleteWorkflowMetricsSLADefinitionVersion(
			WorkflowMetricsSLADefinitionVersion
				workflowMetricsSLADefinitionVersion) {

		return getService().deleteWorkflowMetricsSLADefinitionVersion(
			workflowMetricsSLADefinitionVersion);
	}

	public static <T> T dslQuery(DSLQuery dslQuery) {
		return getService().dslQuery(dslQuery);
	}

	public static int dslQueryCount(DSLQuery dslQuery) {
		return getService().dslQueryCount(dslQuery);
	}

	public static DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.metrics.model.impl.WorkflowMetricsSLADefinitionVersionModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.metrics.model.impl.WorkflowMetricsSLADefinitionVersionModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static WorkflowMetricsSLADefinitionVersion
		fetchWorkflowMetricsSLADefinitionVersion(
			long workflowMetricsSLADefinitionVersionId) {

		return getService().fetchWorkflowMetricsSLADefinitionVersion(
			workflowMetricsSLADefinitionVersionId);
	}

	/**
	 * Returns the workflow metrics sla definition version matching the UUID and group.
	 *
	 * @param uuid the workflow metrics sla definition version's UUID
	 * @param groupId the primary key of the group
	 * @return the matching workflow metrics sla definition version, or <code>null</code> if a matching workflow metrics sla definition version could not be found
	 */
	public static WorkflowMetricsSLADefinitionVersion
		fetchWorkflowMetricsSLADefinitionVersionByUuidAndGroupId(
			String uuid, long groupId) {

		return getService().
			fetchWorkflowMetricsSLADefinitionVersionByUuidAndGroupId(
				uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the workflow metrics sla definition version with the primary key.
	 *
	 * @param workflowMetricsSLADefinitionVersionId the primary key of the workflow metrics sla definition version
	 * @return the workflow metrics sla definition version
	 * @throws PortalException if a workflow metrics sla definition version with the primary key could not be found
	 */
	public static WorkflowMetricsSLADefinitionVersion
			getWorkflowMetricsSLADefinitionVersion(
				long workflowMetricsSLADefinitionVersionId)
		throws PortalException {

		return getService().getWorkflowMetricsSLADefinitionVersion(
			workflowMetricsSLADefinitionVersionId);
	}

	public static WorkflowMetricsSLADefinitionVersion
			getWorkflowMetricsSLADefinitionVersion(
				long workflowMetricsSLADefinitionId, String version)
		throws com.liferay.portal.workflow.metrics.exception.
			NoSuchSLADefinitionVersionException {

		return getService().getWorkflowMetricsSLADefinitionVersion(
			workflowMetricsSLADefinitionId, version);
	}

	/**
	 * Returns the workflow metrics sla definition version matching the UUID and group.
	 *
	 * @param uuid the workflow metrics sla definition version's UUID
	 * @param groupId the primary key of the group
	 * @return the matching workflow metrics sla definition version
	 * @throws PortalException if a matching workflow metrics sla definition version could not be found
	 */
	public static WorkflowMetricsSLADefinitionVersion
			getWorkflowMetricsSLADefinitionVersionByUuidAndGroupId(
				String uuid, long groupId)
		throws PortalException {

		return getService().
			getWorkflowMetricsSLADefinitionVersionByUuidAndGroupId(
				uuid, groupId);
	}

	/**
	 * Returns a range of all the workflow metrics sla definition versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.metrics.model.impl.WorkflowMetricsSLADefinitionVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of workflow metrics sla definition versions
	 * @param end the upper bound of the range of workflow metrics sla definition versions (not inclusive)
	 * @return the range of workflow metrics sla definition versions
	 */
	public static List<WorkflowMetricsSLADefinitionVersion>
		getWorkflowMetricsSLADefinitionVersions(int start, int end) {

		return getService().getWorkflowMetricsSLADefinitionVersions(start, end);
	}

	public static List<WorkflowMetricsSLADefinitionVersion>
		getWorkflowMetricsSLADefinitionVersions(
			long workflowMetricsSLADefinitionId) {

		return getService().getWorkflowMetricsSLADefinitionVersions(
			workflowMetricsSLADefinitionId);
	}

	public static List<WorkflowMetricsSLADefinitionVersion>
		getWorkflowMetricsSLADefinitionVersions(
			long workflowMetricsSLADefinitionId,
			OrderByComparator<WorkflowMetricsSLADefinitionVersion>
				orderByComparator) {

		return getService().getWorkflowMetricsSLADefinitionVersions(
			workflowMetricsSLADefinitionId, orderByComparator);
	}

	/**
	 * Returns all the workflow metrics sla definition versions matching the UUID and company.
	 *
	 * @param uuid the UUID of the workflow metrics sla definition versions
	 * @param companyId the primary key of the company
	 * @return the matching workflow metrics sla definition versions, or an empty list if no matches were found
	 */
	public static List<WorkflowMetricsSLADefinitionVersion>
		getWorkflowMetricsSLADefinitionVersionsByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().
			getWorkflowMetricsSLADefinitionVersionsByUuidAndCompanyId(
				uuid, companyId);
	}

	/**
	 * Returns a range of workflow metrics sla definition versions matching the UUID and company.
	 *
	 * @param uuid the UUID of the workflow metrics sla definition versions
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of workflow metrics sla definition versions
	 * @param end the upper bound of the range of workflow metrics sla definition versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching workflow metrics sla definition versions, or an empty list if no matches were found
	 */
	public static List<WorkflowMetricsSLADefinitionVersion>
		getWorkflowMetricsSLADefinitionVersionsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<WorkflowMetricsSLADefinitionVersion>
				orderByComparator) {

		return getService().
			getWorkflowMetricsSLADefinitionVersionsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of workflow metrics sla definition versions.
	 *
	 * @return the number of workflow metrics sla definition versions
	 */
	public static int getWorkflowMetricsSLADefinitionVersionsCount() {
		return getService().getWorkflowMetricsSLADefinitionVersionsCount();
	}

	/**
	 * Updates the workflow metrics sla definition version in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect WorkflowMetricsSLADefinitionVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param workflowMetricsSLADefinitionVersion the workflow metrics sla definition version
	 * @return the workflow metrics sla definition version that was updated
	 */
	public static WorkflowMetricsSLADefinitionVersion
		updateWorkflowMetricsSLADefinitionVersion(
			WorkflowMetricsSLADefinitionVersion
				workflowMetricsSLADefinitionVersion) {

		return getService().updateWorkflowMetricsSLADefinitionVersion(
			workflowMetricsSLADefinitionVersion);
	}

	public static WorkflowMetricsSLADefinitionVersionLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot
		<WorkflowMetricsSLADefinitionVersionLocalService> _serviceSnapshot =
			new Snapshot<>(
				WorkflowMetricsSLADefinitionVersionLocalServiceUtil.class,
				WorkflowMetricsSLADefinitionVersionLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1110709861