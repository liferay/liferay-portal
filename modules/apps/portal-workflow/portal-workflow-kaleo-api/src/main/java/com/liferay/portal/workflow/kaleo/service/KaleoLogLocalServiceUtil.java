/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.workflow.kaleo.model.KaleoLog;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for KaleoLog. This utility wraps
 * <code>com.liferay.portal.workflow.kaleo.service.impl.KaleoLogLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see KaleoLogLocalService
 * @generated
 */
public class KaleoLogLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.workflow.kaleo.service.impl.KaleoLogLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static KaleoLog addActionExecutionKaleoLog(
			com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken
				kaleoInstanceToken,
			com.liferay.portal.workflow.kaleo.model.KaleoAction kaleoAction,
			long startTime, long endTime, String comment,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addActionExecutionKaleoLog(
			kaleoInstanceToken, kaleoAction, startTime, endTime, comment,
			serviceContext);
	}

	public static KaleoLog addInstanceEndKaleoLog(
			com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken
				kaleoInstanceToken,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addInstanceEndKaleoLog(
			kaleoInstanceToken, serviceContext);
	}

	public static KaleoLog addInstanceFailKaleoLog(
			com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken
				kaleoInstanceToken,
			String comment,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addInstanceFailKaleoLog(
			kaleoInstanceToken, comment, serviceContext);
	}

	public static KaleoLog addInstanceStartKaleoLog(
			com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken
				kaleoInstanceToken,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addInstanceStartKaleoLog(
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
	public static KaleoLog addKaleoLog(KaleoLog kaleoLog) {
		return getService().addKaleoLog(kaleoLog);
	}

	public static KaleoLog addNodeEntryKaleoLog(
			com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken
				kaleoInstanceToken,
			com.liferay.portal.workflow.kaleo.model.KaleoNode sourceKaleoNode,
			com.liferay.portal.workflow.kaleo.model.KaleoNode targetKaleoNode,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addNodeEntryKaleoLog(
			kaleoInstanceToken, sourceKaleoNode, targetKaleoNode,
			serviceContext);
	}

	public static KaleoLog addNodeExitKaleoLog(
			com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken
				kaleoInstanceToken,
			com.liferay.portal.workflow.kaleo.model.KaleoNode
				departingKaleoNode,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addNodeExitKaleoLog(
			kaleoInstanceToken, departingKaleoNode, serviceContext);
	}

	public static KaleoLog addTaskAssignmentKaleoLog(
			List
				<com.liferay.portal.workflow.kaleo.model.
					KaleoTaskAssignmentInstance>
						previousKaleoTaskAssignmentInstances,
			com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignmentInstance
				kaleoTaskAssignmentInstance,
			com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken
				kaleoTaskInstanceToken,
			String comment, Map<String, Serializable> workflowContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addTaskAssignmentKaleoLog(
			previousKaleoTaskAssignmentInstances, kaleoTaskAssignmentInstance,
			kaleoTaskInstanceToken, comment, workflowContext, serviceContext);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #addTaskAssignmentKaleoLog(List, KaleoTaskAssignmentInstance,
	 KaleoTaskInstanceToken, String, Map, ServiceContext)}}
	 */
	@Deprecated
	public static KaleoLog addTaskAssignmentKaleoLog(
			List
				<com.liferay.portal.workflow.kaleo.model.
					KaleoTaskAssignmentInstance>
						previousKaleoTaskAssignmentInstances,
			com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken
				kaleoTaskInstanceToken,
			String comment, Map<String, Serializable> workflowContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addTaskAssignmentKaleoLog(
			previousKaleoTaskAssignmentInstances, kaleoTaskInstanceToken,
			comment, workflowContext, serviceContext);
	}

	public static List<KaleoLog> addTaskAssignmentKaleoLogs(
			List
				<com.liferay.portal.workflow.kaleo.model.
					KaleoTaskAssignmentInstance>
						previousTaskAssignmentInstances,
			com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken
				kaleoTaskInstanceToken,
			String comment, Map<String, Serializable> workflowContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addTaskAssignmentKaleoLogs(
			previousTaskAssignmentInstances, kaleoTaskInstanceToken, comment,
			workflowContext, serviceContext);
	}

	public static KaleoLog addTaskCompletionKaleoLog(
			com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken
				kaleoTaskInstanceToken,
			String comment, Map<String, Serializable> workflowContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addTaskCompletionKaleoLog(
			kaleoTaskInstanceToken, comment, workflowContext, serviceContext);
	}

	public static KaleoLog addTaskUpdateKaleoLog(
			com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken
				kaleoTaskInstanceToken,
			String comment, Map<String, Serializable> workflowContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addTaskUpdateKaleoLog(
			kaleoTaskInstanceToken, comment, workflowContext, serviceContext);
	}

	/**
	 * Creates a new kaleo log with the primary key. Does not add the kaleo log to the database.
	 *
	 * @param kaleoLogId the primary key for the new kaleo log
	 * @return the new kaleo log
	 */
	public static KaleoLog createKaleoLog(long kaleoLogId) {
		return getService().createKaleoLog(kaleoLogId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteCompanyKaleoLogs(long companyId) {
		getService().deleteCompanyKaleoLogs(companyId);
	}

	public static void deleteKaleoDefinitionVersionKaleoLogs(
		long kaleoDefinitionVersionId) {

		getService().deleteKaleoDefinitionVersionKaleoLogs(
			kaleoDefinitionVersionId);
	}

	public static void deleteKaleoInstanceKaleoLogs(long kaleoInstanceId) {
		getService().deleteKaleoInstanceKaleoLogs(kaleoInstanceId);
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
	public static KaleoLog deleteKaleoLog(KaleoLog kaleoLog) {
		return getService().deleteKaleoLog(kaleoLog);
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
	public static KaleoLog deleteKaleoLog(long kaleoLogId)
		throws PortalException {

		return getService().deleteKaleoLog(kaleoLogId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoLogModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoLogModelImpl</code>.
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

	public static KaleoLog fetchKaleoLog(long kaleoLogId) {
		return getService().fetchKaleoLog(kaleoLogId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	public static List<KaleoLog> getKaleoInstanceKaleoLogs(
		long companyId, long kaleoInstanceId, List<Integer> logTypes, int start,
		int end, OrderByComparator<KaleoLog> orderByComparator) {

		return getService().getKaleoInstanceKaleoLogs(
			companyId, kaleoInstanceId, logTypes, start, end,
			orderByComparator);
	}

	public static int getKaleoInstanceKaleoLogsCount(
		long companyId, long kaleoInstanceId, List<Integer> logTypes) {

		return getService().getKaleoInstanceKaleoLogsCount(
			companyId, kaleoInstanceId, logTypes);
	}

	/**
	 * Returns the kaleo log with the primary key.
	 *
	 * @param kaleoLogId the primary key of the kaleo log
	 * @return the kaleo log
	 * @throws PortalException if a kaleo log with the primary key could not be found
	 */
	public static KaleoLog getKaleoLog(long kaleoLogId) throws PortalException {
		return getService().getKaleoLog(kaleoLogId);
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
	public static List<KaleoLog> getKaleoLogs(int start, int end) {
		return getService().getKaleoLogs(start, end);
	}

	/**
	 * Returns the number of kaleo logs.
	 *
	 * @return the number of kaleo logs
	 */
	public static int getKaleoLogsCount() {
		return getService().getKaleoLogsCount();
	}

	public static List<KaleoLog> getKaleoTaskInstanceTokenKaleoLogs(
		long companyId, long kaleoTaskInstanceTokenId, List<Integer> logTypes,
		int start, int end, OrderByComparator<KaleoLog> orderByComparator) {

		return getService().getKaleoTaskInstanceTokenKaleoLogs(
			companyId, kaleoTaskInstanceTokenId, logTypes, start, end,
			orderByComparator);
	}

	public static int getKaleoTaskInstanceTokenKaleoLogsCount(
		long companyId, long kaleoTaskInstanceTokenId, List<Integer> logTypes) {

		return getService().getKaleoTaskInstanceTokenKaleoLogsCount(
			companyId, kaleoTaskInstanceTokenId, logTypes);
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
	 * Updates the kaleo log in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoLogLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoLog the kaleo log
	 * @return the kaleo log that was updated
	 */
	public static KaleoLog updateKaleoLog(KaleoLog kaleoLog) {
		return getService().updateKaleoLog(kaleoLog);
	}

	public static KaleoLogLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<KaleoLogLocalService> _serviceSnapshot =
		new Snapshot<>(
			KaleoLogLocalServiceUtil.class, KaleoLogLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1239976332