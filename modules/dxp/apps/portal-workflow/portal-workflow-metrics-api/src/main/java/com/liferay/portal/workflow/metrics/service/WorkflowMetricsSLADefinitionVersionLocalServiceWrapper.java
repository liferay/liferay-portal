/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link WorkflowMetricsSLADefinitionVersionLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see WorkflowMetricsSLADefinitionVersionLocalService
 * @generated
 */
public class WorkflowMetricsSLADefinitionVersionLocalServiceWrapper
	implements ServiceWrapper<WorkflowMetricsSLADefinitionVersionLocalService>,
			   WorkflowMetricsSLADefinitionVersionLocalService {

	public WorkflowMetricsSLADefinitionVersionLocalServiceWrapper() {
		this(null);
	}

	public WorkflowMetricsSLADefinitionVersionLocalServiceWrapper(
		WorkflowMetricsSLADefinitionVersionLocalService
			workflowMetricsSLADefinitionVersionLocalService) {

		_workflowMetricsSLADefinitionVersionLocalService =
			workflowMetricsSLADefinitionVersionLocalService;
	}

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
	@Override
	public com.liferay.portal.workflow.metrics.model.
		WorkflowMetricsSLADefinitionVersion
			addWorkflowMetricsSLADefinitionVersion(
				com.liferay.portal.workflow.metrics.model.
					WorkflowMetricsSLADefinitionVersion
						workflowMetricsSLADefinitionVersion) {

		return _workflowMetricsSLADefinitionVersionLocalService.
			addWorkflowMetricsSLADefinitionVersion(
				workflowMetricsSLADefinitionVersion);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _workflowMetricsSLADefinitionVersionLocalService.
			createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new workflow metrics sla definition version with the primary key. Does not add the workflow metrics sla definition version to the database.
	 *
	 * @param workflowMetricsSLADefinitionVersionId the primary key for the new workflow metrics sla definition version
	 * @return the new workflow metrics sla definition version
	 */
	@Override
	public com.liferay.portal.workflow.metrics.model.
		WorkflowMetricsSLADefinitionVersion
			createWorkflowMetricsSLADefinitionVersion(
				long workflowMetricsSLADefinitionVersionId) {

		return _workflowMetricsSLADefinitionVersionLocalService.
			createWorkflowMetricsSLADefinitionVersion(
				workflowMetricsSLADefinitionVersionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _workflowMetricsSLADefinitionVersionLocalService.
			deletePersistedModel(persistedModel);
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
	@Override
	public com.liferay.portal.workflow.metrics.model.
		WorkflowMetricsSLADefinitionVersion
				deleteWorkflowMetricsSLADefinitionVersion(
					long workflowMetricsSLADefinitionVersionId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _workflowMetricsSLADefinitionVersionLocalService.
			deleteWorkflowMetricsSLADefinitionVersion(
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
	@Override
	public com.liferay.portal.workflow.metrics.model.
		WorkflowMetricsSLADefinitionVersion
			deleteWorkflowMetricsSLADefinitionVersion(
				com.liferay.portal.workflow.metrics.model.
					WorkflowMetricsSLADefinitionVersion
						workflowMetricsSLADefinitionVersion) {

		return _workflowMetricsSLADefinitionVersionLocalService.
			deleteWorkflowMetricsSLADefinitionVersion(
				workflowMetricsSLADefinitionVersion);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _workflowMetricsSLADefinitionVersionLocalService.dslQuery(
			dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _workflowMetricsSLADefinitionVersionLocalService.dslQueryCount(
			dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _workflowMetricsSLADefinitionVersionLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _workflowMetricsSLADefinitionVersionLocalService.dynamicQuery(
			dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _workflowMetricsSLADefinitionVersionLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _workflowMetricsSLADefinitionVersionLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _workflowMetricsSLADefinitionVersionLocalService.
			dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _workflowMetricsSLADefinitionVersionLocalService.
			dynamicQueryCount(dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.workflow.metrics.model.
		WorkflowMetricsSLADefinitionVersion
			fetchWorkflowMetricsSLADefinitionVersion(
				long workflowMetricsSLADefinitionVersionId) {

		return _workflowMetricsSLADefinitionVersionLocalService.
			fetchWorkflowMetricsSLADefinitionVersion(
				workflowMetricsSLADefinitionVersionId);
	}

	/**
	 * Returns the workflow metrics sla definition version matching the UUID and group.
	 *
	 * @param uuid the workflow metrics sla definition version's UUID
	 * @param groupId the primary key of the group
	 * @return the matching workflow metrics sla definition version, or <code>null</code> if a matching workflow metrics sla definition version could not be found
	 */
	@Override
	public com.liferay.portal.workflow.metrics.model.
		WorkflowMetricsSLADefinitionVersion
			fetchWorkflowMetricsSLADefinitionVersionByUuidAndGroupId(
				String uuid, long groupId) {

		return _workflowMetricsSLADefinitionVersionLocalService.
			fetchWorkflowMetricsSLADefinitionVersionByUuidAndGroupId(
				uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _workflowMetricsSLADefinitionVersionLocalService.
			getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _workflowMetricsSLADefinitionVersionLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _workflowMetricsSLADefinitionVersionLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _workflowMetricsSLADefinitionVersionLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _workflowMetricsSLADefinitionVersionLocalService.
			getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the workflow metrics sla definition version with the primary key.
	 *
	 * @param workflowMetricsSLADefinitionVersionId the primary key of the workflow metrics sla definition version
	 * @return the workflow metrics sla definition version
	 * @throws PortalException if a workflow metrics sla definition version with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.workflow.metrics.model.
		WorkflowMetricsSLADefinitionVersion
				getWorkflowMetricsSLADefinitionVersion(
					long workflowMetricsSLADefinitionVersionId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _workflowMetricsSLADefinitionVersionLocalService.
			getWorkflowMetricsSLADefinitionVersion(
				workflowMetricsSLADefinitionVersionId);
	}

	@Override
	public com.liferay.portal.workflow.metrics.model.
		WorkflowMetricsSLADefinitionVersion
				getWorkflowMetricsSLADefinitionVersion(
					long workflowMetricsSLADefinitionId, String version)
			throws com.liferay.portal.workflow.metrics.exception.
				NoSuchSLADefinitionVersionException {

		return _workflowMetricsSLADefinitionVersionLocalService.
			getWorkflowMetricsSLADefinitionVersion(
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
	@Override
	public com.liferay.portal.workflow.metrics.model.
		WorkflowMetricsSLADefinitionVersion
				getWorkflowMetricsSLADefinitionVersionByUuidAndGroupId(
					String uuid, long groupId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _workflowMetricsSLADefinitionVersionLocalService.
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
	@Override
	public java.util.List
		<com.liferay.portal.workflow.metrics.model.
			WorkflowMetricsSLADefinitionVersion>
				getWorkflowMetricsSLADefinitionVersions(int start, int end) {

		return _workflowMetricsSLADefinitionVersionLocalService.
			getWorkflowMetricsSLADefinitionVersions(start, end);
	}

	@Override
	public java.util.List
		<com.liferay.portal.workflow.metrics.model.
			WorkflowMetricsSLADefinitionVersion>
				getWorkflowMetricsSLADefinitionVersions(
					long workflowMetricsSLADefinitionId) {

		return _workflowMetricsSLADefinitionVersionLocalService.
			getWorkflowMetricsSLADefinitionVersions(
				workflowMetricsSLADefinitionId);
	}

	@Override
	public java.util.List
		<com.liferay.portal.workflow.metrics.model.
			WorkflowMetricsSLADefinitionVersion>
				getWorkflowMetricsSLADefinitionVersions(
					long workflowMetricsSLADefinitionId,
					com.liferay.portal.kernel.util.OrderByComparator
						<com.liferay.portal.workflow.metrics.model.
							WorkflowMetricsSLADefinitionVersion>
								orderByComparator) {

		return _workflowMetricsSLADefinitionVersionLocalService.
			getWorkflowMetricsSLADefinitionVersions(
				workflowMetricsSLADefinitionId, orderByComparator);
	}

	/**
	 * Returns all the workflow metrics sla definition versions matching the UUID and company.
	 *
	 * @param uuid the UUID of the workflow metrics sla definition versions
	 * @param companyId the primary key of the company
	 * @return the matching workflow metrics sla definition versions, or an empty list if no matches were found
	 */
	@Override
	public java.util.List
		<com.liferay.portal.workflow.metrics.model.
			WorkflowMetricsSLADefinitionVersion>
				getWorkflowMetricsSLADefinitionVersionsByUuidAndCompanyId(
					String uuid, long companyId) {

		return _workflowMetricsSLADefinitionVersionLocalService.
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
	@Override
	public java.util.List
		<com.liferay.portal.workflow.metrics.model.
			WorkflowMetricsSLADefinitionVersion>
				getWorkflowMetricsSLADefinitionVersionsByUuidAndCompanyId(
					String uuid, long companyId, int start, int end,
					com.liferay.portal.kernel.util.OrderByComparator
						<com.liferay.portal.workflow.metrics.model.
							WorkflowMetricsSLADefinitionVersion>
								orderByComparator) {

		return _workflowMetricsSLADefinitionVersionLocalService.
			getWorkflowMetricsSLADefinitionVersionsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of workflow metrics sla definition versions.
	 *
	 * @return the number of workflow metrics sla definition versions
	 */
	@Override
	public int getWorkflowMetricsSLADefinitionVersionsCount() {
		return _workflowMetricsSLADefinitionVersionLocalService.
			getWorkflowMetricsSLADefinitionVersionsCount();
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
	@Override
	public com.liferay.portal.workflow.metrics.model.
		WorkflowMetricsSLADefinitionVersion
			updateWorkflowMetricsSLADefinitionVersion(
				com.liferay.portal.workflow.metrics.model.
					WorkflowMetricsSLADefinitionVersion
						workflowMetricsSLADefinitionVersion) {

		return _workflowMetricsSLADefinitionVersionLocalService.
			updateWorkflowMetricsSLADefinitionVersion(
				workflowMetricsSLADefinitionVersion);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _workflowMetricsSLADefinitionVersionLocalService.
			getBasePersistence();
	}

	@Override
	public WorkflowMetricsSLADefinitionVersionLocalService getWrappedService() {
		return _workflowMetricsSLADefinitionVersionLocalService;
	}

	@Override
	public void setWrappedService(
		WorkflowMetricsSLADefinitionVersionLocalService
			workflowMetricsSLADefinitionVersionLocalService) {

		_workflowMetricsSLADefinitionVersionLocalService =
			workflowMetricsSLADefinitionVersionLocalService;
	}

	private WorkflowMetricsSLADefinitionVersionLocalService
		_workflowMetricsSLADefinitionVersionLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-891262354