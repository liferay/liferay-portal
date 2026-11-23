/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;

/**
 * Provides a wrapper for {@link KaleoTaskInstanceTokenLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see KaleoTaskInstanceTokenLocalService
 * @generated
 */
public class KaleoTaskInstanceTokenLocalServiceWrapper
	implements KaleoTaskInstanceTokenLocalService,
			   ServiceWrapper<KaleoTaskInstanceTokenLocalService> {

	public KaleoTaskInstanceTokenLocalServiceWrapper() {
		this(null);
	}

	public KaleoTaskInstanceTokenLocalServiceWrapper(
		KaleoTaskInstanceTokenLocalService kaleoTaskInstanceTokenLocalService) {

		_kaleoTaskInstanceTokenLocalService =
			kaleoTaskInstanceTokenLocalService;
	}

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
	@Override
	public KaleoTaskInstanceToken addKaleoTaskInstanceToken(
		KaleoTaskInstanceToken kaleoTaskInstanceToken) {

		return _kaleoTaskInstanceTokenLocalService.addKaleoTaskInstanceToken(
			kaleoTaskInstanceToken);
	}

	@Override
	public KaleoTaskInstanceToken addKaleoTaskInstanceToken(
			long kaleoInstanceTokenId, long kaleoTaskId, String kaleoTaskName,
			java.util.Collection
				<com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignment>
					kaleoTaskAssignments,
			java.util.Date dueDate,
			java.util.Map<String, java.io.Serializable> workflowContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTaskInstanceTokenLocalService.addKaleoTaskInstanceToken(
			kaleoInstanceTokenId, kaleoTaskId, kaleoTaskName,
			kaleoTaskAssignments, dueDate, workflowContext, serviceContext);
	}

	@Override
	public KaleoTaskInstanceToken assignKaleoTaskInstanceToken(
			long kaleoTaskInstanceTokenId,
			java.util.Collection
				<com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignment>
					kaleoTaskAssignments,
			java.util.Map<String, java.io.Serializable> workflowContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTaskInstanceTokenLocalService.assignKaleoTaskInstanceToken(
			kaleoTaskInstanceTokenId, kaleoTaskAssignments, workflowContext,
			serviceContext);
	}

	@Override
	public KaleoTaskInstanceToken assignKaleoTaskInstanceToken(
			long kaleoTaskInstanceTokenId, String assigneeClassName,
			long assigneeClassPK,
			java.util.Map<String, java.io.Serializable> workflowContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTaskInstanceTokenLocalService.assignKaleoTaskInstanceToken(
			kaleoTaskInstanceTokenId, assigneeClassName, assigneeClassPK,
			workflowContext, serviceContext);
	}

	@Override
	public KaleoTaskInstanceToken completeKaleoTaskInstanceToken(
			long kaleoTaskInstanceTokenId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTaskInstanceTokenLocalService.
			completeKaleoTaskInstanceToken(
				kaleoTaskInstanceTokenId, serviceContext);
	}

	/**
	 * Creates a new kaleo task instance token with the primary key. Does not add the kaleo task instance token to the database.
	 *
	 * @param kaleoTaskInstanceTokenId the primary key for the new kaleo task instance token
	 * @return the new kaleo task instance token
	 */
	@Override
	public KaleoTaskInstanceToken createKaleoTaskInstanceToken(
		long kaleoTaskInstanceTokenId) {

		return _kaleoTaskInstanceTokenLocalService.createKaleoTaskInstanceToken(
			kaleoTaskInstanceTokenId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTaskInstanceTokenLocalService.createPersistedModel(
			primaryKeyObj);
	}

	@Override
	public void deleteCompanyKaleoTaskInstanceTokens(long companyId) {
		_kaleoTaskInstanceTokenLocalService.
			deleteCompanyKaleoTaskInstanceTokens(companyId);
	}

	@Override
	public void deleteKaleoDefinitionVersionKaleoTaskInstanceTokens(
		long kaleoDefinitionVersionId) {

		_kaleoTaskInstanceTokenLocalService.
			deleteKaleoDefinitionVersionKaleoTaskInstanceTokens(
				kaleoDefinitionVersionId);
	}

	@Override
	public void deleteKaleoInstanceKaleoTaskInstanceTokens(
		long kaleoInstanceId) {

		_kaleoTaskInstanceTokenLocalService.
			deleteKaleoInstanceKaleoTaskInstanceTokens(kaleoInstanceId);
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
	@Override
	public KaleoTaskInstanceToken deleteKaleoTaskInstanceToken(
		KaleoTaskInstanceToken kaleoTaskInstanceToken) {

		return _kaleoTaskInstanceTokenLocalService.deleteKaleoTaskInstanceToken(
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
	@Override
	public KaleoTaskInstanceToken deleteKaleoTaskInstanceToken(
			long kaleoTaskInstanceTokenId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTaskInstanceTokenLocalService.deleteKaleoTaskInstanceToken(
			kaleoTaskInstanceTokenId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTaskInstanceTokenLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _kaleoTaskInstanceTokenLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _kaleoTaskInstanceTokenLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _kaleoTaskInstanceTokenLocalService.dynamicQuery();
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

		return _kaleoTaskInstanceTokenLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _kaleoTaskInstanceTokenLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _kaleoTaskInstanceTokenLocalService.dynamicQuery(
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

		return _kaleoTaskInstanceTokenLocalService.dynamicQueryCount(
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

		return _kaleoTaskInstanceTokenLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public KaleoTaskInstanceToken fetchKaleoTaskInstanceToken(
		long kaleoTaskInstanceTokenId) {

		return _kaleoTaskInstanceTokenLocalService.fetchKaleoTaskInstanceToken(
			kaleoTaskInstanceTokenId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _kaleoTaskInstanceTokenLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _kaleoTaskInstanceTokenLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the kaleo task instance token with the primary key.
	 *
	 * @param kaleoTaskInstanceTokenId the primary key of the kaleo task instance token
	 * @return the kaleo task instance token
	 * @throws PortalException if a kaleo task instance token with the primary key could not be found
	 */
	@Override
	public KaleoTaskInstanceToken getKaleoTaskInstanceToken(
			long kaleoTaskInstanceTokenId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTaskInstanceTokenLocalService.getKaleoTaskInstanceToken(
			kaleoTaskInstanceTokenId);
	}

	@Override
	public java.util.List<KaleoTaskInstanceToken> getKaleoTaskInstanceTokens(
		Boolean completed, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KaleoTaskInstanceToken>
			orderByComparator,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoTaskInstanceTokenLocalService.getKaleoTaskInstanceTokens(
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
	@Override
	public java.util.List<KaleoTaskInstanceToken> getKaleoTaskInstanceTokens(
		int start, int end) {

		return _kaleoTaskInstanceTokenLocalService.getKaleoTaskInstanceTokens(
			start, end);
	}

	@Override
	public java.util.List<KaleoTaskInstanceToken> getKaleoTaskInstanceTokens(
		long kaleoInstanceId, Boolean completed, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KaleoTaskInstanceToken>
			orderByComparator,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoTaskInstanceTokenLocalService.getKaleoTaskInstanceTokens(
			kaleoInstanceId, completed, start, end, orderByComparator,
			serviceContext);
	}

	@Override
	public KaleoTaskInstanceToken getKaleoTaskInstanceTokens(
			long kaleoInstanceId, long kaleoTaskId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTaskInstanceTokenLocalService.getKaleoTaskInstanceTokens(
			kaleoInstanceId, kaleoTaskId);
	}

	@Override
	public java.util.List<KaleoTaskInstanceToken> getKaleoTaskInstanceTokens(
		String className, long classPK) {

		return _kaleoTaskInstanceTokenLocalService.getKaleoTaskInstanceTokens(
			className, classPK);
	}

	@Override
	public java.util.List<KaleoTaskInstanceToken> getKaleoTaskInstanceTokens(
		String assigneeClassName, long assigneeClassPK, Boolean completed,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KaleoTaskInstanceToken>
			orderByComparator,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoTaskInstanceTokenLocalService.getKaleoTaskInstanceTokens(
			assigneeClassName, assigneeClassPK, completed, start, end,
			orderByComparator, serviceContext);
	}

	/**
	 * Returns the number of kaleo task instance tokens.
	 *
	 * @return the number of kaleo task instance tokens
	 */
	@Override
	public int getKaleoTaskInstanceTokensCount() {
		return _kaleoTaskInstanceTokenLocalService.
			getKaleoTaskInstanceTokensCount();
	}

	@Override
	public int getKaleoTaskInstanceTokensCount(
		Boolean completed,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoTaskInstanceTokenLocalService.
			getKaleoTaskInstanceTokensCount(completed, serviceContext);
	}

	@Override
	public int getKaleoTaskInstanceTokensCount(
		long kaleoInstanceId, Boolean completed,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoTaskInstanceTokenLocalService.
			getKaleoTaskInstanceTokensCount(
				kaleoInstanceId, completed, serviceContext);
	}

	@Override
	public int getKaleoTaskInstanceTokensCount(
		String assigneeClassName, long assigneeClassPK, Boolean completed,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoTaskInstanceTokenLocalService.
			getKaleoTaskInstanceTokensCount(
				assigneeClassName, assigneeClassPK, completed, serviceContext);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _kaleoTaskInstanceTokenLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTaskInstanceTokenLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public java.util.List<KaleoTaskInstanceToken>
		getSubmittingUserKaleoTaskInstanceTokens(
			long userId, Boolean completed, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<KaleoTaskInstanceToken> orderByComparator,
			com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoTaskInstanceTokenLocalService.
			getSubmittingUserKaleoTaskInstanceTokens(
				userId, completed, start, end, orderByComparator,
				serviceContext);
	}

	@Override
	public int getSubmittingUserKaleoTaskInstanceTokensCount(
		long userId, Boolean completed,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoTaskInstanceTokenLocalService.
			getSubmittingUserKaleoTaskInstanceTokensCount(
				userId, completed, serviceContext);
	}

	@Override
	public boolean hasPendingKaleoTaskForms(long kaleoTaskInstanceTokenId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTaskInstanceTokenLocalService.hasPendingKaleoTaskForms(
			kaleoTaskInstanceTokenId);
	}

	@Override
	public boolean isNotifiableUser(long userId, long workflowTaskId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTaskInstanceTokenLocalService.isNotifiableUser(
			userId, workflowTaskId);
	}

	@Override
	public java.util.List<KaleoTaskInstanceToken> search(
		String keywords, Boolean completed, Boolean searchByUserRoles,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KaleoTaskInstanceToken>
			orderByComparator,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoTaskInstanceTokenLocalService.search(
			keywords, completed, searchByUserRoles, start, end,
			orderByComparator, serviceContext);
	}

	@Override
	public java.util.List<KaleoTaskInstanceToken> search(
		String taskName, String assetType, Long[] assetPrimaryKeys,
		java.util.Date dueDateGT, java.util.Date dueDateLT, Boolean completed,
		Boolean searchByUserRoles, boolean andOperator, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KaleoTaskInstanceToken>
			orderByComparator,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoTaskInstanceTokenLocalService.search(
			taskName, assetType, assetPrimaryKeys, dueDateGT, dueDateLT,
			completed, searchByUserRoles, andOperator, start, end,
			orderByComparator, serviceContext);
	}

	@Override
	public java.util.List<KaleoTaskInstanceToken> search(
		String assetTitle, String taskName, String[] assetTypes,
		Long[] assetPrimaryKeys, java.util.Date dueDateGT,
		java.util.Date dueDateLT, Boolean completed, Boolean searchByUserRoles,
		boolean andOperator, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KaleoTaskInstanceToken>
			orderByComparator,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoTaskInstanceTokenLocalService.search(
			assetTitle, taskName, assetTypes, assetPrimaryKeys, dueDateGT,
			dueDateLT, completed, searchByUserRoles, andOperator, start, end,
			orderByComparator, serviceContext);
	}

	@Override
	public java.util.List<KaleoTaskInstanceToken> search(
		String assetTitle, String[] taskNames, String[] assetTypes,
		Long[] assetPrimaryKeys, Long[] assigneeClassPKs,
		java.util.Date dueDateGT, java.util.Date dueDateLT, Boolean completed,
		Long[] kaleoInstanceIds, Boolean searchByUserRoles, boolean andOperator,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KaleoTaskInstanceToken>
			orderByComparator,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoTaskInstanceTokenLocalService.search(
			assetTitle, taskNames, assetTypes, assetPrimaryKeys,
			assigneeClassPKs, dueDateGT, dueDateLT, completed, kaleoInstanceIds,
			searchByUserRoles, andOperator, start, end, orderByComparator,
			serviceContext);
	}

	@Override
	public java.util.List<KaleoTaskInstanceToken> search(
		String assetTitle, String[] taskNames, String[] assetTypes,
		Long[] assetPrimaryKeys, String assigneeClassName,
		Long[] assigneeClassPKs, java.util.Date dueDateGT,
		java.util.Date dueDateLT, Boolean completed, Long kaleoDefinitionId,
		Long[] kaleoInstanceIds, Boolean searchByUserRoles, boolean andOperator,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KaleoTaskInstanceToken>
			orderByComparator,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoTaskInstanceTokenLocalService.search(
			assetTitle, taskNames, assetTypes, assetPrimaryKeys,
			assigneeClassName, assigneeClassPKs, dueDateGT, dueDateLT,
			completed, kaleoDefinitionId, kaleoInstanceIds, searchByUserRoles,
			andOperator, start, end, orderByComparator, serviceContext);
	}

	@Override
	public int searchCount(
		long kaleoInstanceId, Boolean completed, Boolean searchByUserRoles,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoTaskInstanceTokenLocalService.searchCount(
			kaleoInstanceId, completed, searchByUserRoles, serviceContext);
	}

	@Override
	public int searchCount(
		Long kaleoInstanceId, String assetTitle, String taskName,
		String[] assetTypes, Long[] assetPrimaryKeys, java.util.Date dueDateGT,
		java.util.Date dueDateLT, Boolean completed, Boolean searchByUserRoles,
		boolean andOperator,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoTaskInstanceTokenLocalService.searchCount(
			kaleoInstanceId, assetTitle, taskName, assetTypes, assetPrimaryKeys,
			dueDateGT, dueDateLT, completed, searchByUserRoles, andOperator,
			serviceContext);
	}

	@Override
	public int searchCount(
		String keywords, Boolean completed, Boolean searchByUserRoles,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoTaskInstanceTokenLocalService.searchCount(
			keywords, completed, searchByUserRoles, serviceContext);
	}

	@Override
	public int searchCount(
		String taskName, String assetType, Long[] assetPrimaryKeys,
		java.util.Date dueDateGT, java.util.Date dueDateLT, Boolean completed,
		Boolean searchByUserRoles, boolean andOperator,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoTaskInstanceTokenLocalService.searchCount(
			taskName, assetType, assetPrimaryKeys, dueDateGT, dueDateLT,
			completed, searchByUserRoles, andOperator, serviceContext);
	}

	@Override
	public int searchCount(
		String keywords, String[] assetTypes, Boolean completed,
		Boolean searchByUserRoles,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoTaskInstanceTokenLocalService.searchCount(
			keywords, assetTypes, completed, searchByUserRoles, serviceContext);
	}

	@Override
	public int searchCount(
		String assetTitle, String[] taskNames, String[] assetTypes,
		Long[] assetPrimaryKeys, Long[] assigneeClassPKs,
		java.util.Date dueDateGT, java.util.Date dueDateLT, Boolean completed,
		Long[] kaleoInstanceIds, Boolean searchByUserRoles, boolean andOperator,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoTaskInstanceTokenLocalService.searchCount(
			assetTitle, taskNames, assetTypes, assetPrimaryKeys,
			assigneeClassPKs, dueDateGT, dueDateLT, completed, kaleoInstanceIds,
			searchByUserRoles, andOperator, serviceContext);
	}

	@Override
	public int searchCount(
		String assetTitle, String[] taskNames, String[] assetTypes,
		Long[] assetPrimaryKeys, String assigneeClassName,
		Long[] assigneeClassPKs, java.util.Date dueDateGT,
		java.util.Date dueDateLT, Boolean completed, Long kaleoDefinitionId,
		Long[] kaleoInstanceIds, Boolean searchByUserRoles, boolean andOperator,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoTaskInstanceTokenLocalService.searchCount(
			assetTitle, taskNames, assetTypes, assetPrimaryKeys,
			assigneeClassName, assigneeClassPKs, dueDateGT, dueDateLT,
			completed, kaleoDefinitionId, kaleoInstanceIds, searchByUserRoles,
			andOperator, serviceContext);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<KaleoTaskInstanceToken> searchKaleoTaskInstanceTokens(
				String assetTitle, String[] taskNames, String[] assetTypes,
				Long[] assetPrimaryKeys, String assigneeClassName,
				Long[] assigneeClassPKs, java.util.Date dueDateGT,
				java.util.Date dueDateLT, Boolean completed,
				Long kaleoDefinitionId, Long[] kaleoInstanceIds,
				boolean searchByActiveWorkflowHandlers,
				Boolean searchByUserRoles, boolean andOperator, int start,
				int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<KaleoTaskInstanceToken> orderByComparator,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTaskInstanceTokenLocalService.
			searchKaleoTaskInstanceTokens(
				assetTitle, taskNames, assetTypes, assetPrimaryKeys,
				assigneeClassName, assigneeClassPKs, dueDateGT, dueDateLT,
				completed, kaleoDefinitionId, kaleoInstanceIds,
				searchByActiveWorkflowHandlers, searchByUserRoles, andOperator,
				start, end, orderByComparator, serviceContext);
	}

	@Override
	public KaleoTaskInstanceToken updateDueDate(
			long kaleoTaskInstanceTokenId, java.util.Date dueDate,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTaskInstanceTokenLocalService.updateDueDate(
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
	@Override
	public KaleoTaskInstanceToken updateKaleoTaskInstanceToken(
		KaleoTaskInstanceToken kaleoTaskInstanceToken) {

		return _kaleoTaskInstanceTokenLocalService.updateKaleoTaskInstanceToken(
			kaleoTaskInstanceToken);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _kaleoTaskInstanceTokenLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<KaleoTaskInstanceToken> getCTPersistence() {
		return _kaleoTaskInstanceTokenLocalService.getCTPersistence();
	}

	@Override
	public Class<KaleoTaskInstanceToken> getModelClass() {
		return _kaleoTaskInstanceTokenLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<KaleoTaskInstanceToken>, R, E>
				updateUnsafeFunction)
		throws E {

		return _kaleoTaskInstanceTokenLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public KaleoTaskInstanceTokenLocalService getWrappedService() {
		return _kaleoTaskInstanceTokenLocalService;
	}

	@Override
	public void setWrappedService(
		KaleoTaskInstanceTokenLocalService kaleoTaskInstanceTokenLocalService) {

		_kaleoTaskInstanceTokenLocalService =
			kaleoTaskInstanceTokenLocalService;
	}

	private KaleoTaskInstanceTokenLocalService
		_kaleoTaskInstanceTokenLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:107372611