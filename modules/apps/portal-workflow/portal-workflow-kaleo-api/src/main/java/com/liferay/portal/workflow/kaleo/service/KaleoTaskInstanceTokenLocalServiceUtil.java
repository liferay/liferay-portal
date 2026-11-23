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
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for KaleoTaskInstanceToken. This utility wraps
 * <code>com.liferay.portal.workflow.kaleo.service.impl.KaleoTaskInstanceTokenLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see KaleoTaskInstanceTokenLocalService
 * @generated
 */
public class KaleoTaskInstanceTokenLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.workflow.kaleo.service.impl.KaleoTaskInstanceTokenLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the kaleo task instance token to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoTaskInstanceTokenLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoTaskInstanceToken the kaleo task instance token
	 * @return the kaleo task instance token that was added
	 */
	public static KaleoTaskInstanceToken addKaleoTaskInstanceToken(
		KaleoTaskInstanceToken kaleoTaskInstanceToken) {

		return getService().addKaleoTaskInstanceToken(kaleoTaskInstanceToken);
	}

	public static KaleoTaskInstanceToken addKaleoTaskInstanceToken(
			long kaleoInstanceTokenId, long kaleoTaskId, String kaleoTaskName,
			java.util.Collection
				<com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignment>
					kaleoTaskAssignments,
			java.util.Date dueDate, Map<String, Serializable> workflowContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addKaleoTaskInstanceToken(
			kaleoInstanceTokenId, kaleoTaskId, kaleoTaskName,
			kaleoTaskAssignments, dueDate, workflowContext, serviceContext);
	}

	public static KaleoTaskInstanceToken assignKaleoTaskInstanceToken(
			long kaleoTaskInstanceTokenId,
			java.util.Collection
				<com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignment>
					kaleoTaskAssignments,
			Map<String, Serializable> workflowContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().assignKaleoTaskInstanceToken(
			kaleoTaskInstanceTokenId, kaleoTaskAssignments, workflowContext,
			serviceContext);
	}

	public static KaleoTaskInstanceToken assignKaleoTaskInstanceToken(
			long kaleoTaskInstanceTokenId, String assigneeClassName,
			long assigneeClassPK, Map<String, Serializable> workflowContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().assignKaleoTaskInstanceToken(
			kaleoTaskInstanceTokenId, assigneeClassName, assigneeClassPK,
			workflowContext, serviceContext);
	}

	public static KaleoTaskInstanceToken completeKaleoTaskInstanceToken(
			long kaleoTaskInstanceTokenId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().completeKaleoTaskInstanceToken(
			kaleoTaskInstanceTokenId, serviceContext);
	}

	/**
	 * Creates a new kaleo task instance token with the primary key. Does not add the kaleo task instance token to the database.
	 *
	 * @param kaleoTaskInstanceTokenId the primary key for the new kaleo task instance token
	 * @return the new kaleo task instance token
	 */
	public static KaleoTaskInstanceToken createKaleoTaskInstanceToken(
		long kaleoTaskInstanceTokenId) {

		return getService().createKaleoTaskInstanceToken(
			kaleoTaskInstanceTokenId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteCompanyKaleoTaskInstanceTokens(long companyId) {
		getService().deleteCompanyKaleoTaskInstanceTokens(companyId);
	}

	public static void deleteKaleoDefinitionVersionKaleoTaskInstanceTokens(
		long kaleoDefinitionVersionId) {

		getService().deleteKaleoDefinitionVersionKaleoTaskInstanceTokens(
			kaleoDefinitionVersionId);
	}

	public static void deleteKaleoInstanceKaleoTaskInstanceTokens(
		long kaleoInstanceId) {

		getService().deleteKaleoInstanceKaleoTaskInstanceTokens(
			kaleoInstanceId);
	}

	/**
	 * Deletes the kaleo task instance token from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoTaskInstanceTokenLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoTaskInstanceToken the kaleo task instance token
	 * @return the kaleo task instance token that was removed
	 */
	public static KaleoTaskInstanceToken deleteKaleoTaskInstanceToken(
		KaleoTaskInstanceToken kaleoTaskInstanceToken) {

		return getService().deleteKaleoTaskInstanceToken(
			kaleoTaskInstanceToken);
	}

	/**
	 * Deletes the kaleo task instance token with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoTaskInstanceTokenLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoTaskInstanceTokenId the primary key of the kaleo task instance token
	 * @return the kaleo task instance token that was removed
	 * @throws PortalException if a kaleo task instance token with the primary key could not be found
	 */
	public static KaleoTaskInstanceToken deleteKaleoTaskInstanceToken(
			long kaleoTaskInstanceTokenId)
		throws PortalException {

		return getService().deleteKaleoTaskInstanceToken(
			kaleoTaskInstanceTokenId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskInstanceTokenModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskInstanceTokenModelImpl</code>.
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

	public static KaleoTaskInstanceToken fetchKaleoTaskInstanceToken(
		long kaleoTaskInstanceTokenId) {

		return getService().fetchKaleoTaskInstanceToken(
			kaleoTaskInstanceTokenId);
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

	/**
	 * Returns the kaleo task instance token with the primary key.
	 *
	 * @param kaleoTaskInstanceTokenId the primary key of the kaleo task instance token
	 * @return the kaleo task instance token
	 * @throws PortalException if a kaleo task instance token with the primary key could not be found
	 */
	public static KaleoTaskInstanceToken getKaleoTaskInstanceToken(
			long kaleoTaskInstanceTokenId)
		throws PortalException {

		return getService().getKaleoTaskInstanceToken(kaleoTaskInstanceTokenId);
	}

	public static List<KaleoTaskInstanceToken> getKaleoTaskInstanceTokens(
		Boolean completed, int start, int end,
		OrderByComparator<KaleoTaskInstanceToken> orderByComparator,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().getKaleoTaskInstanceTokens(
			completed, start, end, orderByComparator, serviceContext);
	}

	/**
	 * Returns a range of all the kaleo task instance tokens.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskInstanceTokenModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kaleo task instance tokens
	 * @param end the upper bound of the range of kaleo task instance tokens (not inclusive)
	 * @return the range of kaleo task instance tokens
	 */
	public static List<KaleoTaskInstanceToken> getKaleoTaskInstanceTokens(
		int start, int end) {

		return getService().getKaleoTaskInstanceTokens(start, end);
	}

	public static List<KaleoTaskInstanceToken> getKaleoTaskInstanceTokens(
		long kaleoInstanceId, Boolean completed, int start, int end,
		OrderByComparator<KaleoTaskInstanceToken> orderByComparator,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().getKaleoTaskInstanceTokens(
			kaleoInstanceId, completed, start, end, orderByComparator,
			serviceContext);
	}

	public static KaleoTaskInstanceToken getKaleoTaskInstanceTokens(
			long kaleoInstanceId, long kaleoTaskId)
		throws PortalException {

		return getService().getKaleoTaskInstanceTokens(
			kaleoInstanceId, kaleoTaskId);
	}

	public static List<KaleoTaskInstanceToken> getKaleoTaskInstanceTokens(
		String className, long classPK) {

		return getService().getKaleoTaskInstanceTokens(className, classPK);
	}

	public static List<KaleoTaskInstanceToken> getKaleoTaskInstanceTokens(
		String assigneeClassName, long assigneeClassPK, Boolean completed,
		int start, int end,
		OrderByComparator<KaleoTaskInstanceToken> orderByComparator,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().getKaleoTaskInstanceTokens(
			assigneeClassName, assigneeClassPK, completed, start, end,
			orderByComparator, serviceContext);
	}

	/**
	 * Returns the number of kaleo task instance tokens.
	 *
	 * @return the number of kaleo task instance tokens
	 */
	public static int getKaleoTaskInstanceTokensCount() {
		return getService().getKaleoTaskInstanceTokensCount();
	}

	public static int getKaleoTaskInstanceTokensCount(
		Boolean completed,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().getKaleoTaskInstanceTokensCount(
			completed, serviceContext);
	}

	public static int getKaleoTaskInstanceTokensCount(
		long kaleoInstanceId, Boolean completed,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().getKaleoTaskInstanceTokensCount(
			kaleoInstanceId, completed, serviceContext);
	}

	public static int getKaleoTaskInstanceTokensCount(
		String assigneeClassName, long assigneeClassPK, Boolean completed,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().getKaleoTaskInstanceTokensCount(
			assigneeClassName, assigneeClassPK, completed, serviceContext);
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

	public static List<KaleoTaskInstanceToken>
		getSubmittingUserKaleoTaskInstanceTokens(
			long userId, Boolean completed, int start, int end,
			OrderByComparator<KaleoTaskInstanceToken> orderByComparator,
			com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().getSubmittingUserKaleoTaskInstanceTokens(
			userId, completed, start, end, orderByComparator, serviceContext);
	}

	public static int getSubmittingUserKaleoTaskInstanceTokensCount(
		long userId, Boolean completed,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().getSubmittingUserKaleoTaskInstanceTokensCount(
			userId, completed, serviceContext);
	}

	public static boolean hasPendingKaleoTaskForms(
			long kaleoTaskInstanceTokenId)
		throws PortalException {

		return getService().hasPendingKaleoTaskForms(kaleoTaskInstanceTokenId);
	}

	public static boolean isNotifiableUser(long userId, long workflowTaskId)
		throws PortalException {

		return getService().isNotifiableUser(userId, workflowTaskId);
	}

	public static List<KaleoTaskInstanceToken> search(
		String keywords, Boolean completed, Boolean searchByUserRoles,
		int start, int end,
		OrderByComparator<KaleoTaskInstanceToken> orderByComparator,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().search(
			keywords, completed, searchByUserRoles, start, end,
			orderByComparator, serviceContext);
	}

	public static List<KaleoTaskInstanceToken> search(
		String taskName, String assetType, Long[] assetPrimaryKeys,
		java.util.Date dueDateGT, java.util.Date dueDateLT, Boolean completed,
		Boolean searchByUserRoles, boolean andOperator, int start, int end,
		OrderByComparator<KaleoTaskInstanceToken> orderByComparator,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().search(
			taskName, assetType, assetPrimaryKeys, dueDateGT, dueDateLT,
			completed, searchByUserRoles, andOperator, start, end,
			orderByComparator, serviceContext);
	}

	public static List<KaleoTaskInstanceToken> search(
		String assetTitle, String taskName, String[] assetTypes,
		Long[] assetPrimaryKeys, java.util.Date dueDateGT,
		java.util.Date dueDateLT, Boolean completed, Boolean searchByUserRoles,
		boolean andOperator, int start, int end,
		OrderByComparator<KaleoTaskInstanceToken> orderByComparator,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().search(
			assetTitle, taskName, assetTypes, assetPrimaryKeys, dueDateGT,
			dueDateLT, completed, searchByUserRoles, andOperator, start, end,
			orderByComparator, serviceContext);
	}

	public static List<KaleoTaskInstanceToken> search(
		String assetTitle, String[] taskNames, String[] assetTypes,
		Long[] assetPrimaryKeys, Long[] assigneeClassPKs,
		java.util.Date dueDateGT, java.util.Date dueDateLT, Boolean completed,
		Long[] kaleoInstanceIds, Boolean searchByUserRoles, boolean andOperator,
		int start, int end,
		OrderByComparator<KaleoTaskInstanceToken> orderByComparator,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().search(
			assetTitle, taskNames, assetTypes, assetPrimaryKeys,
			assigneeClassPKs, dueDateGT, dueDateLT, completed, kaleoInstanceIds,
			searchByUserRoles, andOperator, start, end, orderByComparator,
			serviceContext);
	}

	public static List<KaleoTaskInstanceToken> search(
		String assetTitle, String[] taskNames, String[] assetTypes,
		Long[] assetPrimaryKeys, String assigneeClassName,
		Long[] assigneeClassPKs, java.util.Date dueDateGT,
		java.util.Date dueDateLT, Boolean completed, Long kaleoDefinitionId,
		Long[] kaleoInstanceIds, Boolean searchByUserRoles, boolean andOperator,
		int start, int end,
		OrderByComparator<KaleoTaskInstanceToken> orderByComparator,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().search(
			assetTitle, taskNames, assetTypes, assetPrimaryKeys,
			assigneeClassName, assigneeClassPKs, dueDateGT, dueDateLT,
			completed, kaleoDefinitionId, kaleoInstanceIds, searchByUserRoles,
			andOperator, start, end, orderByComparator, serviceContext);
	}

	public static int searchCount(
		long kaleoInstanceId, Boolean completed, Boolean searchByUserRoles,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().searchCount(
			kaleoInstanceId, completed, searchByUserRoles, serviceContext);
	}

	public static int searchCount(
		Long kaleoInstanceId, String assetTitle, String taskName,
		String[] assetTypes, Long[] assetPrimaryKeys, java.util.Date dueDateGT,
		java.util.Date dueDateLT, Boolean completed, Boolean searchByUserRoles,
		boolean andOperator,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().searchCount(
			kaleoInstanceId, assetTitle, taskName, assetTypes, assetPrimaryKeys,
			dueDateGT, dueDateLT, completed, searchByUserRoles, andOperator,
			serviceContext);
	}

	public static int searchCount(
		String keywords, Boolean completed, Boolean searchByUserRoles,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().searchCount(
			keywords, completed, searchByUserRoles, serviceContext);
	}

	public static int searchCount(
		String taskName, String assetType, Long[] assetPrimaryKeys,
		java.util.Date dueDateGT, java.util.Date dueDateLT, Boolean completed,
		Boolean searchByUserRoles, boolean andOperator,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().searchCount(
			taskName, assetType, assetPrimaryKeys, dueDateGT, dueDateLT,
			completed, searchByUserRoles, andOperator, serviceContext);
	}

	public static int searchCount(
		String keywords, String[] assetTypes, Boolean completed,
		Boolean searchByUserRoles,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().searchCount(
			keywords, assetTypes, completed, searchByUserRoles, serviceContext);
	}

	public static int searchCount(
		String assetTitle, String[] taskNames, String[] assetTypes,
		Long[] assetPrimaryKeys, Long[] assigneeClassPKs,
		java.util.Date dueDateGT, java.util.Date dueDateLT, Boolean completed,
		Long[] kaleoInstanceIds, Boolean searchByUserRoles, boolean andOperator,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().searchCount(
			assetTitle, taskNames, assetTypes, assetPrimaryKeys,
			assigneeClassPKs, dueDateGT, dueDateLT, completed, kaleoInstanceIds,
			searchByUserRoles, andOperator, serviceContext);
	}

	public static int searchCount(
		String assetTitle, String[] taskNames, String[] assetTypes,
		Long[] assetPrimaryKeys, String assigneeClassName,
		Long[] assigneeClassPKs, java.util.Date dueDateGT,
		java.util.Date dueDateLT, Boolean completed, Long kaleoDefinitionId,
		Long[] kaleoInstanceIds, Boolean searchByUserRoles, boolean andOperator,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().searchCount(
			assetTitle, taskNames, assetTypes, assetPrimaryKeys,
			assigneeClassName, assigneeClassPKs, dueDateGT, dueDateLT,
			completed, kaleoDefinitionId, kaleoInstanceIds, searchByUserRoles,
			andOperator, serviceContext);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<KaleoTaskInstanceToken> searchKaleoTaskInstanceTokens(
				String assetTitle, String[] taskNames, String[] assetTypes,
				Long[] assetPrimaryKeys, String assigneeClassName,
				Long[] assigneeClassPKs, java.util.Date dueDateGT,
				java.util.Date dueDateLT, Boolean completed,
				Long kaleoDefinitionId, Long[] kaleoInstanceIds,
				boolean searchByActiveWorkflowHandlers,
				Boolean searchByUserRoles, boolean andOperator, int start,
				int end,
				OrderByComparator<KaleoTaskInstanceToken> orderByComparator,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
			throws PortalException {

		return getService().searchKaleoTaskInstanceTokens(
			assetTitle, taskNames, assetTypes, assetPrimaryKeys,
			assigneeClassName, assigneeClassPKs, dueDateGT, dueDateLT,
			completed, kaleoDefinitionId, kaleoInstanceIds,
			searchByActiveWorkflowHandlers, searchByUserRoles, andOperator,
			start, end, orderByComparator, serviceContext);
	}

	public static KaleoTaskInstanceToken updateDueDate(
			long kaleoTaskInstanceTokenId, java.util.Date dueDate,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateDueDate(
			kaleoTaskInstanceTokenId, dueDate, serviceContext);
	}

	/**
	 * Updates the kaleo task instance token in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoTaskInstanceTokenLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoTaskInstanceToken the kaleo task instance token
	 * @return the kaleo task instance token that was updated
	 */
	public static KaleoTaskInstanceToken updateKaleoTaskInstanceToken(
		KaleoTaskInstanceToken kaleoTaskInstanceToken) {

		return getService().updateKaleoTaskInstanceToken(
			kaleoTaskInstanceToken);
	}

	public static KaleoTaskInstanceTokenLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<KaleoTaskInstanceTokenLocalService>
		_serviceSnapshot = new Snapshot<>(
			KaleoTaskInstanceTokenLocalServiceUtil.class,
			KaleoTaskInstanceTokenLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1976468245