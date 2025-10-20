/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.model.WorkflowInstanceLink;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link WorkflowInstanceLinkLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see WorkflowInstanceLinkLocalService
 * @generated
 */
public class WorkflowInstanceLinkLocalServiceWrapper
	implements ServiceWrapper<WorkflowInstanceLinkLocalService>,
			   WorkflowInstanceLinkLocalService {

	public WorkflowInstanceLinkLocalServiceWrapper() {
		this(null);
	}

	public WorkflowInstanceLinkLocalServiceWrapper(
		WorkflowInstanceLinkLocalService workflowInstanceLinkLocalService) {

		_workflowInstanceLinkLocalService = workflowInstanceLinkLocalService;
	}

	@Override
	public WorkflowInstanceLink addWorkflowInstanceLink(
			long userId, long companyId, long groupId, String className,
			long classPK, long workflowInstanceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _workflowInstanceLinkLocalService.addWorkflowInstanceLink(
			userId, companyId, groupId, className, classPK, workflowInstanceId);
	}

	/**
	 * Adds the workflow instance link to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect WorkflowInstanceLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param workflowInstanceLink the workflow instance link
	 * @return the workflow instance link that was added
	 */
	@Override
	public WorkflowInstanceLink addWorkflowInstanceLink(
		WorkflowInstanceLink workflowInstanceLink) {

		return _workflowInstanceLinkLocalService.addWorkflowInstanceLink(
			workflowInstanceLink);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _workflowInstanceLinkLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Creates a new workflow instance link with the primary key. Does not add the workflow instance link to the database.
	 *
	 * @param workflowInstanceLinkId the primary key for the new workflow instance link
	 * @return the new workflow instance link
	 */
	@Override
	public WorkflowInstanceLink createWorkflowInstanceLink(
		long workflowInstanceLinkId) {

		return _workflowInstanceLinkLocalService.createWorkflowInstanceLink(
			workflowInstanceLinkId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _workflowInstanceLinkLocalService.deletePersistedModel(
			persistedModel);
	}

	/**
	 * Deletes the workflow instance link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect WorkflowInstanceLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param workflowInstanceLinkId the primary key of the workflow instance link
	 * @return the workflow instance link that was removed
	 * @throws PortalException if a workflow instance link with the primary key could not be found
	 */
	@Override
	public WorkflowInstanceLink deleteWorkflowInstanceLink(
			long workflowInstanceLinkId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _workflowInstanceLinkLocalService.deleteWorkflowInstanceLink(
			workflowInstanceLinkId);
	}

	@Override
	public WorkflowInstanceLink deleteWorkflowInstanceLink(
			long companyId, long groupId, String className, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _workflowInstanceLinkLocalService.deleteWorkflowInstanceLink(
			companyId, groupId, className, classPK);
	}

	/**
	 * Deletes the workflow instance link from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect WorkflowInstanceLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param workflowInstanceLink the workflow instance link
	 * @return the workflow instance link that was removed
	 * @throws PortalException
	 */
	@Override
	public WorkflowInstanceLink deleteWorkflowInstanceLink(
			WorkflowInstanceLink workflowInstanceLink)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _workflowInstanceLinkLocalService.deleteWorkflowInstanceLink(
			workflowInstanceLink);
	}

	@Override
	public void deleteWorkflowInstanceLinkByWorkflowInstanceId(
			long workflowInstanceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_workflowInstanceLinkLocalService.
			deleteWorkflowInstanceLinkByWorkflowInstanceId(workflowInstanceId);
	}

	@Override
	public void deleteWorkflowInstanceLinks(
			long companyId, long groupId, String className, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		_workflowInstanceLinkLocalService.deleteWorkflowInstanceLinks(
			companyId, groupId, className, classPK);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _workflowInstanceLinkLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _workflowInstanceLinkLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _workflowInstanceLinkLocalService.dynamicQuery();
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

		return _workflowInstanceLinkLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.WorkflowInstanceLinkModelImpl</code>.
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

		return _workflowInstanceLinkLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.WorkflowInstanceLinkModelImpl</code>.
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

		return _workflowInstanceLinkLocalService.dynamicQuery(
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

		return _workflowInstanceLinkLocalService.dynamicQueryCount(
			dynamicQuery);
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

		return _workflowInstanceLinkLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public WorkflowInstanceLink fetchWorkflowInstanceLink(
		long workflowInstanceLinkId) {

		return _workflowInstanceLinkLocalService.fetchWorkflowInstanceLink(
			workflowInstanceLinkId);
	}

	@Override
	public WorkflowInstanceLink fetchWorkflowInstanceLink(
		long companyId, long groupId, String className, long classPK) {

		return _workflowInstanceLinkLocalService.fetchWorkflowInstanceLink(
			companyId, groupId, className, classPK);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _workflowInstanceLinkLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _workflowInstanceLinkLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _workflowInstanceLinkLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _workflowInstanceLinkLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public String getState(
			long companyId, long groupId, String className, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _workflowInstanceLinkLocalService.getState(
			companyId, groupId, className, classPK);
	}

	/**
	 * Returns the workflow instance link with the primary key.
	 *
	 * @param workflowInstanceLinkId the primary key of the workflow instance link
	 * @return the workflow instance link
	 * @throws PortalException if a workflow instance link with the primary key could not be found
	 */
	@Override
	public WorkflowInstanceLink getWorkflowInstanceLink(
			long workflowInstanceLinkId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _workflowInstanceLinkLocalService.getWorkflowInstanceLink(
			workflowInstanceLinkId);
	}

	@Override
	public WorkflowInstanceLink getWorkflowInstanceLink(
			long companyId, long groupId, String className, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _workflowInstanceLinkLocalService.getWorkflowInstanceLink(
			companyId, groupId, className, classPK);
	}

	/**
	 * Returns a range of all the workflow instance links.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.WorkflowInstanceLinkModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of workflow instance links
	 * @param end the upper bound of the range of workflow instance links (not inclusive)
	 * @return the range of workflow instance links
	 */
	@Override
	public java.util.List<WorkflowInstanceLink> getWorkflowInstanceLinks(
		int start, int end) {

		return _workflowInstanceLinkLocalService.getWorkflowInstanceLinks(
			start, end);
	}

	@Override
	public java.util.List<WorkflowInstanceLink> getWorkflowInstanceLinks(
		long companyId, long groupId, String className, long classPK) {

		return _workflowInstanceLinkLocalService.getWorkflowInstanceLinks(
			companyId, groupId, className, classPK);
	}

	@Override
	public java.util.List<WorkflowInstanceLink> getWorkflowInstanceLinks(
		long companyId, String className) {

		return _workflowInstanceLinkLocalService.getWorkflowInstanceLinks(
			companyId, className);
	}

	/**
	 * Returns the number of workflow instance links.
	 *
	 * @return the number of workflow instance links
	 */
	@Override
	public int getWorkflowInstanceLinksCount() {
		return _workflowInstanceLinkLocalService.
			getWorkflowInstanceLinksCount();
	}

	@Override
	public boolean hasWorkflowInstanceLink(
		long companyId, long groupId, String className, long classPK) {

		return _workflowInstanceLinkLocalService.hasWorkflowInstanceLink(
			companyId, groupId, className, classPK);
	}

	@Override
	public boolean isEnded(
			long companyId, long groupId, String className, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _workflowInstanceLinkLocalService.isEnded(
			companyId, groupId, className, classPK);
	}

	@Override
	public void startWorkflowInstance(
			long companyId, long groupId, long userId, String className,
			long classPK,
			java.util.Map<String, java.io.Serializable> workflowContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_workflowInstanceLinkLocalService.startWorkflowInstance(
			companyId, groupId, userId, className, classPK, workflowContext);
	}

	@Override
	public void startWorkflowInstance(
			long companyId, long groupId, long userId, String className,
			long classPK,
			java.util.Map<String, java.io.Serializable> workflowContext,
			boolean waitForCompletion)
		throws com.liferay.portal.kernel.exception.PortalException {

		_workflowInstanceLinkLocalService.startWorkflowInstance(
			companyId, groupId, userId, className, classPK, workflowContext,
			waitForCompletion);
	}

	@Override
	public void updateClassPK(
			long companyId, long groupId, String className, long oldClassPK,
			long newClassPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		_workflowInstanceLinkLocalService.updateClassPK(
			companyId, groupId, className, oldClassPK, newClassPK);
	}

	/**
	 * Updates the workflow instance link in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect WorkflowInstanceLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param workflowInstanceLink the workflow instance link
	 * @return the workflow instance link that was updated
	 */
	@Override
	public WorkflowInstanceLink updateWorkflowInstanceLink(
		WorkflowInstanceLink workflowInstanceLink) {

		return _workflowInstanceLinkLocalService.updateWorkflowInstanceLink(
			workflowInstanceLink);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _workflowInstanceLinkLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<WorkflowInstanceLink> getCTPersistence() {
		return _workflowInstanceLinkLocalService.getCTPersistence();
	}

	@Override
	public Class<WorkflowInstanceLink> getModelClass() {
		return _workflowInstanceLinkLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<WorkflowInstanceLink>, R, E>
				updateUnsafeFunction)
		throws E {

		return _workflowInstanceLinkLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public WorkflowInstanceLinkLocalService getWrappedService() {
		return _workflowInstanceLinkLocalService;
	}

	@Override
	public void setWrappedService(
		WorkflowInstanceLinkLocalService workflowInstanceLinkLocalService) {

		_workflowInstanceLinkLocalService = workflowInstanceLinkLocalService;
	}

	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

}