/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.workflow.kaleo.model.KaleoLog;

/**
 * Provides a wrapper for {@link KaleoLogLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see KaleoLogLocalService
 * @generated
 */
public class KaleoLogLocalServiceWrapper
	implements KaleoLogLocalService, ServiceWrapper<KaleoLogLocalService> {

	public KaleoLogLocalServiceWrapper() {
		this(null);
	}

	public KaleoLogLocalServiceWrapper(
		KaleoLogLocalService kaleoLogLocalService) {

		_kaleoLogLocalService = kaleoLogLocalService;
	}

	@Override
	public KaleoLog addActionExecutionKaleoLog(
			com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken
				kaleoInstanceToken,
			com.liferay.portal.workflow.kaleo.model.KaleoAction kaleoAction,
			long startTime, long endTime, String comment,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoLogLocalService.addActionExecutionKaleoLog(
			kaleoInstanceToken, kaleoAction, startTime, endTime, comment,
			serviceContext);
	}

	@Override
	public KaleoLog addInstanceEndKaleoLog(
			com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken
				kaleoInstanceToken,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoLogLocalService.addInstanceEndKaleoLog(
			kaleoInstanceToken, serviceContext);
	}

	@Override
	public KaleoLog addInstanceFailKaleoLog(
			com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken
				kaleoInstanceToken,
			String comment,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoLogLocalService.addInstanceFailKaleoLog(
			kaleoInstanceToken, comment, serviceContext);
	}

	@Override
	public KaleoLog addInstanceStartKaleoLog(
			com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken
				kaleoInstanceToken,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoLogLocalService.addInstanceStartKaleoLog(
			kaleoInstanceToken, serviceContext);
	}

	/**
	 * Adds the kaleo log to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoLogLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoLog the kaleo log
	 * @return the kaleo log that was added
	 */
	@Override
	public KaleoLog addKaleoLog(KaleoLog kaleoLog) {
		return _kaleoLogLocalService.addKaleoLog(kaleoLog);
	}

	@Override
	public KaleoLog addNodeEntryKaleoLog(
			com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken
				kaleoInstanceToken,
			com.liferay.portal.workflow.kaleo.model.KaleoNode sourceKaleoNode,
			com.liferay.portal.workflow.kaleo.model.KaleoNode targetKaleoNode,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoLogLocalService.addNodeEntryKaleoLog(
			kaleoInstanceToken, sourceKaleoNode, targetKaleoNode,
			serviceContext);
	}

	@Override
	public KaleoLog addNodeExitKaleoLog(
			com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken
				kaleoInstanceToken,
			com.liferay.portal.workflow.kaleo.model.KaleoNode
				departingKaleoNode,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoLogLocalService.addNodeExitKaleoLog(
			kaleoInstanceToken, departingKaleoNode, serviceContext);
	}

	@Override
	public KaleoLog addTaskAssignmentKaleoLog(
			java.util.List
				<com.liferay.portal.workflow.kaleo.model.
					KaleoTaskAssignmentInstance>
						previousKaleoTaskAssignmentInstances,
			com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignmentInstance
				kaleoTaskAssignmentInstance,
			com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken
				kaleoTaskInstanceToken,
			String comment,
			java.util.Map<String, java.io.Serializable> workflowContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoLogLocalService.addTaskAssignmentKaleoLog(
			previousKaleoTaskAssignmentInstances, kaleoTaskAssignmentInstance,
			kaleoTaskInstanceToken, comment, workflowContext, serviceContext);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #addTaskAssignmentKaleoLog(List, KaleoTaskAssignmentInstance,
	 KaleoTaskInstanceToken, String, Map, ServiceContext)}}
	 */
	@Deprecated
	@Override
	public KaleoLog addTaskAssignmentKaleoLog(
			java.util.List
				<com.liferay.portal.workflow.kaleo.model.
					KaleoTaskAssignmentInstance>
						previousKaleoTaskAssignmentInstances,
			com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken
				kaleoTaskInstanceToken,
			String comment,
			java.util.Map<String, java.io.Serializable> workflowContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoLogLocalService.addTaskAssignmentKaleoLog(
			previousKaleoTaskAssignmentInstances, kaleoTaskInstanceToken,
			comment, workflowContext, serviceContext);
	}

	@Override
	public java.util.List<KaleoLog> addTaskAssignmentKaleoLogs(
			java.util.List
				<com.liferay.portal.workflow.kaleo.model.
					KaleoTaskAssignmentInstance>
						previousTaskAssignmentInstances,
			com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken
				kaleoTaskInstanceToken,
			String comment,
			java.util.Map<String, java.io.Serializable> workflowContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoLogLocalService.addTaskAssignmentKaleoLogs(
			previousTaskAssignmentInstances, kaleoTaskInstanceToken, comment,
			workflowContext, serviceContext);
	}

	@Override
	public KaleoLog addTaskCompletionKaleoLog(
			com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken
				kaleoTaskInstanceToken,
			String comment,
			java.util.Map<String, java.io.Serializable> workflowContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoLogLocalService.addTaskCompletionKaleoLog(
			kaleoTaskInstanceToken, comment, workflowContext, serviceContext);
	}

	@Override
	public KaleoLog addTaskUpdateKaleoLog(
			com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken
				kaleoTaskInstanceToken,
			String comment,
			java.util.Map<String, java.io.Serializable> workflowContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoLogLocalService.addTaskUpdateKaleoLog(
			kaleoTaskInstanceToken, comment, workflowContext, serviceContext);
	}

	/**
	 * Creates a new kaleo log with the primary key. Does not add the kaleo log to the database.
	 *
	 * @param kaleoLogId the primary key for the new kaleo log
	 * @return the new kaleo log
	 */
	@Override
	public KaleoLog createKaleoLog(long kaleoLogId) {
		return _kaleoLogLocalService.createKaleoLog(kaleoLogId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoLogLocalService.createPersistedModel(primaryKeyObj);
	}

	@Override
	public void deleteCompanyKaleoLogs(long companyId) {
		_kaleoLogLocalService.deleteCompanyKaleoLogs(companyId);
	}

	@Override
	public void deleteKaleoDefinitionVersionKaleoLogs(
		long kaleoDefinitionVersionId) {

		_kaleoLogLocalService.deleteKaleoDefinitionVersionKaleoLogs(
			kaleoDefinitionVersionId);
	}

	@Override
	public void deleteKaleoInstanceKaleoLogs(long kaleoInstanceId) {
		_kaleoLogLocalService.deleteKaleoInstanceKaleoLogs(kaleoInstanceId);
	}

	/**
	 * Deletes the kaleo log from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoLogLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoLog the kaleo log
	 * @return the kaleo log that was removed
	 */
	@Override
	public KaleoLog deleteKaleoLog(KaleoLog kaleoLog) {
		return _kaleoLogLocalService.deleteKaleoLog(kaleoLog);
	}

	/**
	 * Deletes the kaleo log with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoLogLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoLogId the primary key of the kaleo log
	 * @return the kaleo log that was removed
	 * @throws PortalException if a kaleo log with the primary key could not be found
	 */
	@Override
	public KaleoLog deleteKaleoLog(long kaleoLogId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoLogLocalService.deleteKaleoLog(kaleoLogId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoLogLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _kaleoLogLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _kaleoLogLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _kaleoLogLocalService.dynamicQuery();
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

		return _kaleoLogLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoLogModelImpl</code>.
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

		return _kaleoLogLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoLogModelImpl</code>.
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

		return _kaleoLogLocalService.dynamicQuery(
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

		return _kaleoLogLocalService.dynamicQueryCount(dynamicQuery);
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

		return _kaleoLogLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public KaleoLog fetchKaleoLog(long kaleoLogId) {
		return _kaleoLogLocalService.fetchKaleoLog(kaleoLogId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _kaleoLogLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _kaleoLogLocalService.getIndexableActionableDynamicQuery();
	}

	@Override
	public java.util.List<KaleoLog> getKaleoInstanceKaleoLogs(
		long companyId, long kaleoInstanceId, java.util.List<Integer> logTypes,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KaleoLog>
			orderByComparator) {

		return _kaleoLogLocalService.getKaleoInstanceKaleoLogs(
			companyId, kaleoInstanceId, logTypes, start, end,
			orderByComparator);
	}

	@Override
	public int getKaleoInstanceKaleoLogsCount(
		long companyId, long kaleoInstanceId,
		java.util.List<Integer> logTypes) {

		return _kaleoLogLocalService.getKaleoInstanceKaleoLogsCount(
			companyId, kaleoInstanceId, logTypes);
	}

	/**
	 * Returns the kaleo log with the primary key.
	 *
	 * @param kaleoLogId the primary key of the kaleo log
	 * @return the kaleo log
	 * @throws PortalException if a kaleo log with the primary key could not be found
	 */
	@Override
	public KaleoLog getKaleoLog(long kaleoLogId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoLogLocalService.getKaleoLog(kaleoLogId);
	}

	/**
	 * Returns a range of all the kaleo logs.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoLogModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kaleo logs
	 * @param end the upper bound of the range of kaleo logs (not inclusive)
	 * @return the range of kaleo logs
	 */
	@Override
	public java.util.List<KaleoLog> getKaleoLogs(int start, int end) {
		return _kaleoLogLocalService.getKaleoLogs(start, end);
	}

	/**
	 * Returns the number of kaleo logs.
	 *
	 * @return the number of kaleo logs
	 */
	@Override
	public int getKaleoLogsCount() {
		return _kaleoLogLocalService.getKaleoLogsCount();
	}

	@Override
	public java.util.List<KaleoLog> getKaleoTaskInstanceTokenKaleoLogs(
		long companyId, long kaleoTaskInstanceTokenId,
		java.util.List<Integer> logTypes, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KaleoLog>
			orderByComparator) {

		return _kaleoLogLocalService.getKaleoTaskInstanceTokenKaleoLogs(
			companyId, kaleoTaskInstanceTokenId, logTypes, start, end,
			orderByComparator);
	}

	@Override
	public int getKaleoTaskInstanceTokenKaleoLogsCount(
		long companyId, long kaleoTaskInstanceTokenId,
		java.util.List<Integer> logTypes) {

		return _kaleoLogLocalService.getKaleoTaskInstanceTokenKaleoLogsCount(
			companyId, kaleoTaskInstanceTokenId, logTypes);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _kaleoLogLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoLogLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the kaleo log in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoLogLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoLog the kaleo log
	 * @return the kaleo log that was updated
	 */
	@Override
	public KaleoLog updateKaleoLog(KaleoLog kaleoLog) {
		return _kaleoLogLocalService.updateKaleoLog(kaleoLog);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _kaleoLogLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<KaleoLog> getCTPersistence() {
		return _kaleoLogLocalService.getCTPersistence();
	}

	@Override
	public Class<KaleoLog> getModelClass() {
		return _kaleoLogLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<KaleoLog>, R, E> updateUnsafeFunction)
		throws E {

		return _kaleoLogLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public KaleoLogLocalService getWrappedService() {
		return _kaleoLogLocalService;
	}

	@Override
	public void setWrappedService(KaleoLogLocalService kaleoLogLocalService) {
		_kaleoLogLocalService = kaleoLogLocalService;
	}

	private KaleoLogLocalService _kaleoLogLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:507614761