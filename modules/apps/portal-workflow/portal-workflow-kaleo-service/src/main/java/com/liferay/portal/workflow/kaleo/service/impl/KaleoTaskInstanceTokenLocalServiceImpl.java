/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.impl;

import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.workflow.kaleo.internal.search.KaleoTaskInstanceTokenField;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.model.KaleoTask;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignment;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignmentInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.assignment.AggregateKaleoTaskAssignmentSelector;
import com.liferay.portal.workflow.kaleo.runtime.util.WorkflowContextUtil;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskAssignmentInstanceLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskAssignmentLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskFormInstanceLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTimerInstanceTokenLocalService;
import com.liferay.portal.workflow.kaleo.service.base.KaleoTaskInstanceTokenLocalServiceBaseImpl;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoInstanceTokenPersistence;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoTaskAssignmentPersistence;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoTaskFormInstancePersistence;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoTaskFormPersistence;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoTaskInstanceTokenQuery;

import java.io.Serializable;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Marcellus Tavares
 */
@Component(
	property = "model.class.name=com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken",
	service = AopService.class
)
public class KaleoTaskInstanceTokenLocalServiceImpl
	extends KaleoTaskInstanceTokenLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public KaleoTaskInstanceToken addKaleoTaskInstanceToken(
			long kaleoInstanceTokenId, long kaleoTaskId, String kaleoTaskName,
			Collection<KaleoTaskAssignment> kaleoTaskAssignments, Date dueDate,
			Map<String, Serializable> workflowContext,
			ServiceContext serviceContext)
		throws PortalException {

		KaleoInstanceToken kaleoInstanceToken =
			_kaleoInstanceTokenPersistence.findByPrimaryKey(
				kaleoInstanceTokenId);

		User user = _userLocalService.getUser(
			serviceContext.getGuestOrUserId());
		Date date = new Date();

		long kaleoTaskInstanceTokenId = counterLocalService.increment();

		KaleoTaskInstanceToken kaleoTaskInstanceToken =
			kaleoTaskInstanceTokenPersistence.create(kaleoTaskInstanceTokenId);

		kaleoTaskInstanceToken.setGroupId(
			_staging.getLiveGroupId(serviceContext.getScopeGroupId()));
		kaleoTaskInstanceToken.setCompanyId(user.getCompanyId());
		kaleoTaskInstanceToken.setUserId(user.getUserId());
		kaleoTaskInstanceToken.setUserName(user.getFullName());
		kaleoTaskInstanceToken.setCreateDate(date);
		kaleoTaskInstanceToken.setModifiedDate(date);
		kaleoTaskInstanceToken.setKaleoDefinitionId(
			kaleoInstanceToken.getKaleoDefinitionId());
		kaleoTaskInstanceToken.setKaleoDefinitionVersionId(
			kaleoInstanceToken.getKaleoDefinitionVersionId());
		kaleoTaskInstanceToken.setKaleoInstanceId(
			kaleoInstanceToken.getKaleoInstanceId());
		kaleoTaskInstanceToken.setKaleoInstanceTokenId(kaleoInstanceTokenId);
		kaleoTaskInstanceToken.setKaleoTaskId(kaleoTaskId);
		kaleoTaskInstanceToken.setKaleoTaskName(kaleoTaskName);
		kaleoTaskInstanceToken.setDueDate(dueDate);

		if (workflowContext != null) {
			kaleoTaskInstanceToken.setClassName(
				(String)workflowContext.get(
					WorkflowConstants.CONTEXT_ENTRY_CLASS_NAME));

			if (workflowContext.containsKey(
					WorkflowConstants.CONTEXT_ENTRY_CLASS_PK)) {

				kaleoTaskInstanceToken.setClassPK(
					GetterUtil.getLong(
						(String)workflowContext.get(
							WorkflowConstants.CONTEXT_ENTRY_CLASS_PK)));
			}
		}

		kaleoTaskInstanceToken.setCompleted(false);
		kaleoTaskInstanceToken.setWorkflowContext(
			WorkflowContextUtil.convert(workflowContext));

		kaleoTaskInstanceToken = kaleoTaskInstanceTokenPersistence.update(
			kaleoTaskInstanceToken);

		_kaleoTaskAssignmentInstanceLocalService.addTaskAssignmentInstances(
			kaleoTaskInstanceToken, kaleoTaskAssignments, workflowContext,
			serviceContext);

		return kaleoTaskInstanceToken;
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public KaleoTaskInstanceToken assignKaleoTaskInstanceToken(
			long kaleoTaskInstanceTokenId,
			Collection<KaleoTaskAssignment> kaleoTaskAssignments,
			Map<String, Serializable> workflowContext,
			ServiceContext serviceContext)
		throws PortalException {

		KaleoTaskInstanceToken kaleoTaskInstanceToken =
			kaleoTaskInstanceTokenPersistence.findByPrimaryKey(
				kaleoTaskInstanceTokenId);

		kaleoTaskInstanceToken.setModifiedDate(new Date());
		kaleoTaskInstanceToken.setWorkflowContext(
			WorkflowContextUtil.convert(workflowContext));

		kaleoTaskInstanceToken = kaleoTaskInstanceTokenPersistence.update(
			kaleoTaskInstanceToken);

		_kaleoTaskAssignmentInstanceLocalService.
			assignKaleoTaskAssignmentInstances(
				kaleoTaskInstanceToken, kaleoTaskAssignments, workflowContext,
				serviceContext);

		return kaleoTaskInstanceToken;
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public KaleoTaskInstanceToken assignKaleoTaskInstanceToken(
			long kaleoTaskInstanceTokenId, String assigneeClassName,
			long assigneeClassPK, Map<String, Serializable> workflowContext,
			ServiceContext serviceContext)
		throws PortalException {

		KaleoTaskInstanceToken kaleoTaskInstanceToken =
			kaleoTaskInstanceTokenPersistence.findByPrimaryKey(
				kaleoTaskInstanceTokenId);

		kaleoTaskInstanceToken.setModifiedDate(new Date());
		kaleoTaskInstanceToken.setWorkflowContext(
			WorkflowContextUtil.convert(workflowContext));

		kaleoTaskInstanceToken = kaleoTaskInstanceTokenPersistence.update(
			kaleoTaskInstanceToken);

		_kaleoTaskAssignmentInstanceLocalService.
			assignKaleoTaskAssignmentInstance(
				kaleoTaskInstanceToken, assigneeClassName, assigneeClassPK,
				serviceContext);

		return kaleoTaskInstanceToken;
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public KaleoTaskInstanceToken completeKaleoTaskInstanceToken(
			long kaleoTaskInstanceTokenId, ServiceContext serviceContext)
		throws PortalException {

		// Kaleo task instance token

		KaleoTaskInstanceToken kaleoTaskInstanceToken =
			kaleoTaskInstanceTokenPersistence.findByPrimaryKey(
				kaleoTaskInstanceTokenId);

		kaleoTaskInstanceToken.setCompletionUserId(serviceContext.getUserId());
		kaleoTaskInstanceToken.setCompleted(true);
		kaleoTaskInstanceToken.setCompletionDate(new Date());

		kaleoTaskInstanceToken = kaleoTaskInstanceTokenPersistence.update(
			kaleoTaskInstanceToken);

		// Kaleo task assignment instance

		_kaleoTaskAssignmentInstanceLocalService.completeKaleoTaskInstanceToken(
			kaleoTaskInstanceTokenId, serviceContext);

		// Kaleo timers

		_kaleoTimerInstanceTokenLocalService.completeKaleoTimerInstanceTokens(
			kaleoTaskInstanceToken.getKaleoInstanceTokenId(), serviceContext);

		return kaleoTaskInstanceToken;
	}

	@Override
	public void deleteCompanyKaleoTaskInstanceTokens(long companyId) {

		// Kaleo task instance tokens

		for (KaleoTaskInstanceToken kaleoTaskInstanceToken :
				kaleoTaskInstanceTokenPersistence.findByCompanyId(companyId)) {

			kaleoTaskInstanceTokenLocalService.deleteKaleoTaskInstanceToken(
				kaleoTaskInstanceToken);
		}

		// Kaleo task assignment instances

		_kaleoTaskAssignmentInstanceLocalService.
			deleteCompanyKaleoTaskAssignmentInstances(companyId);

		// Kaleo task form instances

		_kaleoTaskFormInstanceLocalService.deleteCompanyKaleoTaskFormInstances(
			companyId);
	}

	@Override
	public void deleteKaleoDefinitionVersionKaleoTaskInstanceTokens(
		long kaleoDefinitionVersionId) {

		// Kaleo task instance tokens

		for (KaleoTaskInstanceToken kaleoTaskInstanceToken :
				kaleoTaskInstanceTokenPersistence.
					findByKaleoDefinitionVersionId(kaleoDefinitionVersionId)) {

			kaleoTaskInstanceTokenLocalService.deleteKaleoTaskInstanceToken(
				kaleoTaskInstanceToken);
		}

		// Kaleo task assignment instances

		_kaleoTaskAssignmentInstanceLocalService.
			deleteKaleoDefinitionVersionKaleoTaskAssignmentInstances(
				kaleoDefinitionVersionId);

		// Kaleo task form instances

		_kaleoTaskFormInstanceLocalService.
			deleteKaleoDefinitionVersionKaleoTaskFormInstances(
				kaleoDefinitionVersionId);
	}

	@Override
	public void deleteKaleoInstanceKaleoTaskInstanceTokens(
		long kaleoInstanceId) {

		// Kaleo task instance tokens

		for (KaleoTaskInstanceToken kaleoTaskInstanceToken :
				kaleoTaskInstanceTokenPersistence.findByKaleoInstanceId(
					kaleoInstanceId)) {

			kaleoTaskInstanceTokenLocalService.deleteKaleoTaskInstanceToken(
				kaleoTaskInstanceToken);
		}

		// Kaleo task assignment instances

		_kaleoTaskAssignmentInstanceLocalService.
			deleteKaleoInstanceKaleoTaskAssignmentInstances(kaleoInstanceId);

		// Kaleo task form instances

		_kaleoTaskFormInstanceLocalService.
			deleteKaleoInstanceKaleoTaskFormInstances(kaleoInstanceId);
	}

	@Override
	public KaleoTaskInstanceToken deleteKaleoTaskInstanceToken(
			long kaleoTaskInstanceTokenId)
		throws PortalException {

		KaleoTaskInstanceToken kaleoTaskInstanceToken =
			kaleoTaskInstanceTokenPersistence.remove(kaleoTaskInstanceTokenId);

		_kaleoTaskAssignmentInstanceLocalService.
			deleteKaleoTaskAssignmentInstances(kaleoTaskInstanceToken);

		_kaleoTaskFormInstanceLocalService.
			deleteKaleoTaskInstanceTokenKaleoTaskFormInstances(
				kaleoTaskInstanceTokenId);

		return kaleoTaskInstanceToken;
	}

	@Override
	public List<KaleoTaskInstanceToken> getCompanyKaleoTaskInstanceTokens(
		long companyId, int start, int end) {

		return kaleoTaskInstanceTokenPersistence.findByCompanyId(
			companyId, start, end);
	}

	@Override
	public int getCompanyKaleoTaskInstanceTokensCount(long companyId) {
		return kaleoTaskInstanceTokenPersistence.countByCompanyId(companyId);
	}

	@Override
	public List<KaleoTaskInstanceToken> getKaleoTaskInstanceTokens(
		Boolean completed, int start, int end,
		OrderByComparator<KaleoTaskInstanceToken> orderByComparator,
		ServiceContext serviceContext) {

		DynamicQuery dynamicQuery = _buildDynamicQuery(
			completed, serviceContext);

		return dynamicQuery(dynamicQuery, start, end, orderByComparator);
	}

	@Override
	public List<KaleoTaskInstanceToken> getKaleoTaskInstanceTokens(
		List<Long> roleIds, Boolean completed, int start, int end,
		OrderByComparator<KaleoTaskInstanceToken> orderByComparator,
		ServiceContext serviceContext) {

		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery =
			new KaleoTaskInstanceTokenQuery(serviceContext);

		kaleoTaskInstanceTokenQuery.setCompleted(completed);
		kaleoTaskInstanceTokenQuery.setEnd(end);
		kaleoTaskInstanceTokenQuery.setOrderByComparator(orderByComparator);
		kaleoTaskInstanceTokenQuery.setRoleIds(roleIds);
		kaleoTaskInstanceTokenQuery.setStart(start);

		return kaleoTaskInstanceTokenFinder.findKaleoTaskInstanceTokens(
			kaleoTaskInstanceTokenQuery);
	}

	@Override
	public List<KaleoTaskInstanceToken> getKaleoTaskInstanceTokens(
		long kaleoInstanceId, Boolean completed, int start, int end,
		OrderByComparator<KaleoTaskInstanceToken> orderByComparator,
		ServiceContext serviceContext) {

		long userId = serviceContext.getUserId();

		if (userId == 0) {
			DynamicQuery dynamicQuery = _buildDynamicQuery(
				kaleoInstanceId, completed, serviceContext);

			return dynamicQuery(dynamicQuery, start, end, orderByComparator);
		}

		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery =
			new KaleoTaskInstanceTokenQuery(serviceContext);

		kaleoTaskInstanceTokenQuery.setAssigneeClassName(User.class.getName());
		kaleoTaskInstanceTokenQuery.setAssigneeClassPKs(
			_getAssigneeClassPKs(serviceContext.getUserId()));
		kaleoTaskInstanceTokenQuery.setCompleted(completed);
		kaleoTaskInstanceTokenQuery.setEnd(end);
		kaleoTaskInstanceTokenQuery.setKaleoInstanceIds(
			_getKaleoInstanceIds(kaleoInstanceId));
		kaleoTaskInstanceTokenQuery.setOrderByComparator(orderByComparator);
		kaleoTaskInstanceTokenQuery.setStart(start);

		return kaleoTaskInstanceTokenFinder.findKaleoTaskInstanceTokens(
			kaleoTaskInstanceTokenQuery);
	}

	@Override
	public KaleoTaskInstanceToken getKaleoTaskInstanceTokens(
			long kaleoInstanceId, long kaleoTaskId)
		throws PortalException {

		return kaleoTaskInstanceTokenPersistence.findByKII_KTI(
			kaleoInstanceId, kaleoTaskId);
	}

	@Override
	public List<KaleoTaskInstanceToken> getKaleoTaskInstanceTokens(
		String className, long classPK) {

		return kaleoTaskInstanceTokenPersistence.findByCN_CPK(
			className, classPK);
	}

	@Override
	public List<KaleoTaskInstanceToken> getKaleoTaskInstanceTokens(
		String assigneeClassName, long assigneeClassPK, Boolean completed,
		int start, int end,
		OrderByComparator<KaleoTaskInstanceToken> orderByComparator,
		ServiceContext serviceContext) {

		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery =
			new KaleoTaskInstanceTokenQuery(serviceContext);

		kaleoTaskInstanceTokenQuery.setAssigneeClassName(assigneeClassName);
		kaleoTaskInstanceTokenQuery.setAssigneeClassPKs(
			_getAssigneeClassPKs(assigneeClassPK));
		kaleoTaskInstanceTokenQuery.setCompleted(completed);
		kaleoTaskInstanceTokenQuery.setEnd(end);
		kaleoTaskInstanceTokenQuery.setOrderByComparator(orderByComparator);
		kaleoTaskInstanceTokenQuery.setStart(start);

		return kaleoTaskInstanceTokenFinder.findKaleoTaskInstanceTokens(
			kaleoTaskInstanceTokenQuery);
	}

	@Override
	public int getKaleoTaskInstanceTokensCount(
		Boolean completed, ServiceContext serviceContext) {

		DynamicQuery dynamicQuery = _buildDynamicQuery(
			completed, serviceContext);

		return (int)dynamicQueryCount(dynamicQuery);
	}

	@Override
	public int getKaleoTaskInstanceTokensCount(
		List<Long> roleIds, Boolean completed, ServiceContext serviceContext) {

		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery =
			new KaleoTaskInstanceTokenQuery(serviceContext);

		kaleoTaskInstanceTokenQuery.setAssigneeClassName(Role.class.getName());
		kaleoTaskInstanceTokenQuery.setCompleted(completed);
		kaleoTaskInstanceTokenQuery.setRoleIds(roleIds);

		return kaleoTaskInstanceTokenFinder.countKaleoTaskInstanceTokens(
			kaleoTaskInstanceTokenQuery);
	}

	@Override
	public int getKaleoTaskInstanceTokensCount(
		long kaleoInstanceId, Boolean completed,
		ServiceContext serviceContext) {

		long userId = serviceContext.getUserId();

		if (userId == 0) {
			DynamicQuery dynamicQuery = _buildDynamicQuery(
				kaleoInstanceId, completed, serviceContext);

			return (int)dynamicQueryCount(dynamicQuery);
		}

		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery =
			new KaleoTaskInstanceTokenQuery(serviceContext);

		kaleoTaskInstanceTokenQuery.setAssigneeClassName(User.class.getName());
		kaleoTaskInstanceTokenQuery.setAssigneeClassPKs(
			_getAssigneeClassPKs(serviceContext.getUserId()));
		kaleoTaskInstanceTokenQuery.setCompleted(completed);
		kaleoTaskInstanceTokenQuery.setKaleoInstanceIds(
			_getKaleoInstanceIds(kaleoInstanceId));

		return kaleoTaskInstanceTokenFinder.countKaleoTaskInstanceTokens(
			kaleoTaskInstanceTokenQuery);
	}

	@Override
	public int getKaleoTaskInstanceTokensCount(
		String assigneeClassName, long assigneeClassPK, Boolean completed,
		ServiceContext serviceContext) {

		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery =
			new KaleoTaskInstanceTokenQuery(serviceContext);

		kaleoTaskInstanceTokenQuery.setAssigneeClassName(assigneeClassName);
		kaleoTaskInstanceTokenQuery.setAssigneeClassPKs(
			_getAssigneeClassPKs(assigneeClassPK));
		kaleoTaskInstanceTokenQuery.setCompleted(completed);

		return kaleoTaskInstanceTokenFinder.countKaleoTaskInstanceTokens(
			kaleoTaskInstanceTokenQuery);
	}

	@Override
	public List<KaleoTaskInstanceToken>
		getSubmittingUserKaleoTaskInstanceTokens(
			long userId, Boolean completed, int start, int end,
			OrderByComparator<KaleoTaskInstanceToken> orderByComparator,
			ServiceContext serviceContext) {

		if (completed == null) {
			return kaleoTaskInstanceTokenPersistence.findByC_U(
				serviceContext.getCompanyId(), userId, start, end,
				orderByComparator);
		}

		return kaleoTaskInstanceTokenPersistence.findByC_U_C(
			serviceContext.getCompanyId(), userId, completed, start, end,
			orderByComparator);
	}

	@Override
	public int getSubmittingUserKaleoTaskInstanceTokensCount(
		long userId, Boolean completed, ServiceContext serviceContext) {

		if (completed == null) {
			return kaleoTaskInstanceTokenPersistence.countByC_U(
				serviceContext.getCompanyId(), userId);
		}

		return kaleoTaskInstanceTokenPersistence.countByC_U_C(
			serviceContext.getCompanyId(), userId, completed);
	}

	@Override
	public boolean hasPendingKaleoTaskForms(long kaleoTaskInstanceTokenId)
		throws PortalException {

		KaleoTaskInstanceToken kaleoTaskInstanceToken =
			kaleoTaskInstanceTokenPersistence.findByPrimaryKey(
				kaleoTaskInstanceTokenId);

		int kaleoTaskFormsCount = _kaleoTaskFormPersistence.countByKaleoTaskId(
			kaleoTaskInstanceToken.getKaleoTaskId());

		int kaleoTaskFormInstancesCount =
			_kaleoTaskFormInstancePersistence.countByKaleoTaskInstanceTokenId(
				kaleoTaskInstanceTokenId);

		if (kaleoTaskFormsCount > kaleoTaskFormInstancesCount) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isNotifiableUser(long userId, long workflowTaskId)
		throws PortalException {

		KaleoTaskInstanceToken kaleoTaskInstanceToken =
			kaleoTaskInstanceTokenPersistence.findByPrimaryKey(workflowTaskId);

		Collection<KaleoTaskAssignment> kaleoTaskAssignments =
			_aggregateKaleoTaskAssignmentSelector.getKaleoTaskAssignments(
				_kaleoTaskAssignmentPersistence.findByKCN_KCPK(
					KaleoTask.class.getName(),
					kaleoTaskInstanceToken.getKaleoTaskId()),
				_createExecutionContext(kaleoTaskInstanceToken));

		for (KaleoTaskAssignment kaleoTaskAssignment : kaleoTaskAssignments) {
			if (_isNotifiableUser(
					kaleoTaskAssignment, kaleoTaskInstanceToken, userId)) {

				return true;
			}
		}

		// TODO Temporary workaround for LPS-188796

		for (KaleoTaskAssignmentInstance kaleoTaskAssignmentInstance :
				kaleoTaskInstanceToken.getKaleoTaskAssignmentInstances()) {

			KaleoTaskAssignment kaleoTaskAssignment =
				_kaleoTaskAssignmentLocalService.createKaleoTaskAssignment(0L);

			kaleoTaskAssignment.setAssigneeClassName(
				kaleoTaskAssignmentInstance.getAssigneeClassName());
			kaleoTaskAssignment.setAssigneeClassPK(
				kaleoTaskAssignmentInstance.getAssigneeClassPK());

			if (_isNotifiableUser(
					kaleoTaskAssignment, kaleoTaskInstanceToken, userId)) {

				return true;
			}
		}

		return false;
	}

	@Override
	public List<KaleoTaskInstanceToken> search(
		String keywords, Boolean completed, Boolean searchByUserRoles,
		int start, int end,
		OrderByComparator<KaleoTaskInstanceToken> orderByComparator,
		ServiceContext serviceContext) {

		return search(
			keywords, keywords, null, null, null, completed, searchByUserRoles,
			false, start, end, orderByComparator, serviceContext);
	}

	@Override
	public List<KaleoTaskInstanceToken> search(
		String taskName, String assetType, Long[] assetPrimaryKeys,
		Date dueDateGT, Date dueDateLT, Boolean completed,
		Boolean searchByUserRoles, boolean andOperator, int start, int end,
		OrderByComparator<KaleoTaskInstanceToken> orderByComparator,
		ServiceContext serviceContext) {

		return search(
			null, taskName, _getAssetTypes(assetType), assetPrimaryKeys,
			dueDateGT, dueDateLT, completed, searchByUserRoles, andOperator,
			start, end, orderByComparator, serviceContext);
	}

	@Override
	public List<KaleoTaskInstanceToken> search(
		String assetTitle, String taskName, String[] assetTypes,
		Long[] assetPrimaryKeys, Date dueDateGT, Date dueDateLT,
		Boolean completed, Boolean searchByUserRoles, boolean andOperator,
		int start, int end,
		OrderByComparator<KaleoTaskInstanceToken> orderByComparator,
		ServiceContext serviceContext) {

		return search(
			assetTitle, _getTaskNames(taskName), assetTypes, assetPrimaryKeys,
			null, dueDateGT, dueDateLT, completed, null, searchByUserRoles,
			andOperator, start, end, orderByComparator, serviceContext);
	}

	@Override
	public List<KaleoTaskInstanceToken> search(
		String keywords, String[] assetTypes, Boolean completed,
		Boolean searchByUserRoles, int start, int end,
		OrderByComparator<KaleoTaskInstanceToken> orderByComparator,
		ServiceContext serviceContext) {

		return search(
			keywords, keywords, assetTypes, null, null, null, completed,
			searchByUserRoles, false, start, end, orderByComparator,
			serviceContext);
	}

	@Override
	public List<KaleoTaskInstanceToken> search(
		String assetTitle, String[] taskNames, String[] assetTypes,
		Long[] assetPrimaryKeys, Long[] assigneeClassPKs, Date dueDateGT,
		Date dueDateLT, Boolean completed, Long[] kaleoInstanceIds,
		Boolean searchByUserRoles, boolean andOperator, int start, int end,
		OrderByComparator<KaleoTaskInstanceToken> orderByComparator,
		ServiceContext serviceContext) {

		return search(
			assetTitle, taskNames, assetTypes, assetPrimaryKeys, null,
			assigneeClassPKs, dueDateGT, dueDateLT, completed, null,
			kaleoInstanceIds, searchByUserRoles, andOperator, start, end,
			orderByComparator, serviceContext);
	}

	@Override
	public List<KaleoTaskInstanceToken> search(
		String assetTitle, String[] taskNames, String[] assetTypes,
		Long[] assetPrimaryKeys, String assigneeClassName,
		Long[] assigneeClassPKs, Date dueDateGT, Date dueDateLT,
		Boolean completed, Long kaleoDefinitionId, Long[] kaleoInstanceIds,
		Boolean searchByUserRoles, boolean andOperator, int start, int end,
		OrderByComparator<KaleoTaskInstanceToken> orderByComparator,
		ServiceContext serviceContext) {

		try {
			BaseModelSearchResult<KaleoTaskInstanceToken>
				baseModelSearchResult = searchKaleoTaskInstanceTokens(
					assetTitle, taskNames, assetTypes, assetPrimaryKeys,
					assigneeClassName, assigneeClassPKs, dueDateGT, dueDateLT,
					completed, kaleoDefinitionId, kaleoInstanceIds, false,
					searchByUserRoles, andOperator, start, end,
					orderByComparator, serviceContext);

			return baseModelSearchResult.getBaseModels();
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return Collections.emptyList();
	}

	@Override
	public int searchCount(
		long kaleoInstanceId, Boolean completed, Boolean searchByUserRoles,
		ServiceContext serviceContext) {

		return searchCount(
			kaleoInstanceId, null, null, null, null, null, null, completed,
			searchByUserRoles, false, serviceContext);
	}

	@Override
	public int searchCount(
		Long kaleoInstanceId, String assetTitle, String taskName,
		String[] assetTypes, Long[] assetPrimaryKeys, Date dueDateGT,
		Date dueDateLT, Boolean completed, Boolean searchByUserRoles,
		boolean andOperator, ServiceContext serviceContext) {

		return searchCount(
			assetTitle, _getTaskNames(taskName), assetTypes, assetPrimaryKeys,
			null, dueDateGT, dueDateLT, completed,
			_getKaleoInstanceIds(kaleoInstanceId), searchByUserRoles,
			andOperator, serviceContext);
	}

	@Override
	public int searchCount(
		String keywords, Boolean completed, Boolean searchByUserRoles,
		ServiceContext serviceContext) {

		return searchCount(
			keywords, keywords, null, null, null, completed, searchByUserRoles,
			false, serviceContext);
	}

	@Override
	public int searchCount(
		String taskName, String assetType, Long[] assetPrimaryKeys,
		Date dueDateGT, Date dueDateLT, Boolean completed,
		Boolean searchByUserRoles, boolean andOperator,
		ServiceContext serviceContext) {

		return searchCount(
			null, null, taskName, _getAssetTypes(assetType), assetPrimaryKeys,
			dueDateGT, dueDateLT, completed, searchByUserRoles, andOperator,
			serviceContext);
	}

	@Override
	public int searchCount(
		String keywords, String[] assetTypes, Boolean completed,
		Boolean searchByUserRoles, ServiceContext serviceContext) {

		return searchCount(
			null, keywords, keywords, assetTypes, null, null, null, completed,
			searchByUserRoles, false, serviceContext);
	}

	@Override
	public int searchCount(
		String assetTitle, String[] taskNames, String[] assetTypes,
		Long[] assetPrimaryKeys, Long[] assigneeClassPKs, Date dueDateGT,
		Date dueDateLT, Boolean completed, Long[] kaleoInstanceIds,
		Boolean searchByUserRoles, boolean andOperator,
		ServiceContext serviceContext) {

		return searchCount(
			assetTitle, taskNames, assetTypes, assetPrimaryKeys, null,
			assigneeClassPKs, dueDateGT, dueDateLT, completed, null,
			kaleoInstanceIds, searchByUserRoles, andOperator, serviceContext);
	}

	@Override
	public int searchCount(
		String assetTitle, String[] taskNames, String[] assetTypes,
		Long[] assetPrimaryKeys, String assigneeClassName,
		Long[] assigneeClassPKs, Date dueDateGT, Date dueDateLT,
		Boolean completed, Long kaleoDefinitionId, Long[] kaleoInstanceIds,
		Boolean searchByUserRoles, boolean andOperator,
		ServiceContext serviceContext) {

		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery =
			new KaleoTaskInstanceTokenQuery(serviceContext);

		kaleoTaskInstanceTokenQuery.setAndOperator(andOperator);
		kaleoTaskInstanceTokenQuery.setAssetTitle(assetTitle);
		kaleoTaskInstanceTokenQuery.setAssetTypes(assetTypes);
		kaleoTaskInstanceTokenQuery.setAssetPrimaryKeys(assetPrimaryKeys);
		kaleoTaskInstanceTokenQuery.setAssigneeClassName(assigneeClassName);
		kaleoTaskInstanceTokenQuery.setAssigneeClassPKs(assigneeClassPKs);
		kaleoTaskInstanceTokenQuery.setCompleted(completed);
		kaleoTaskInstanceTokenQuery.setDueDateGT(dueDateGT);
		kaleoTaskInstanceTokenQuery.setDueDateLT(dueDateLT);
		kaleoTaskInstanceTokenQuery.setKaleoDefinitionId(kaleoDefinitionId);
		kaleoTaskInstanceTokenQuery.setKaleoInstanceIds(kaleoInstanceIds);
		kaleoTaskInstanceTokenQuery.setSearchByUserRoles(searchByUserRoles);
		kaleoTaskInstanceTokenQuery.setTaskNames(taskNames);

		return _searchCount(
			HashMapBuilder.<String, Serializable>put(
				"kaleoTaskInstanceTokenQuery", kaleoTaskInstanceTokenQuery
			).build(),
			serviceContext);
	}

	@Override
	public BaseModelSearchResult<KaleoTaskInstanceToken>
			searchKaleoTaskInstanceTokens(
				String assetTitle, String[] taskNames, String[] assetTypes,
				Long[] assetPrimaryKeys, String assigneeClassName,
				Long[] assigneeClassPKs, Date dueDateGT, Date dueDateLT,
				Boolean completed, Long kaleoDefinitionId,
				Long[] kaleoInstanceIds, boolean searchByActiveWorkflowHandlers,
				Boolean searchByUserRoles, boolean andOperator, int start,
				int end,
				OrderByComparator<KaleoTaskInstanceToken> orderByComparator,
				ServiceContext serviceContext)
		throws PortalException {

		Hits hits = _search(
			HashMapBuilder.<String, Serializable>put(
				"kaleoTaskInstanceTokenQuery",
				() -> {
					KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery =
						new KaleoTaskInstanceTokenQuery(serviceContext);

					kaleoTaskInstanceTokenQuery.setAndOperator(andOperator);
					kaleoTaskInstanceTokenQuery.setAssetTitle(assetTitle);
					kaleoTaskInstanceTokenQuery.setAssetTypes(assetTypes);
					kaleoTaskInstanceTokenQuery.setAssetPrimaryKeys(
						assetPrimaryKeys);
					kaleoTaskInstanceTokenQuery.setAssigneeClassName(
						assigneeClassName);
					kaleoTaskInstanceTokenQuery.setAssigneeClassPKs(
						assigneeClassPKs);
					kaleoTaskInstanceTokenQuery.setCompleted(completed);
					kaleoTaskInstanceTokenQuery.setDueDateGT(dueDateGT);
					kaleoTaskInstanceTokenQuery.setDueDateLT(dueDateLT);
					kaleoTaskInstanceTokenQuery.setEnd(end);
					kaleoTaskInstanceTokenQuery.setKaleoDefinitionId(
						kaleoDefinitionId);
					kaleoTaskInstanceTokenQuery.setKaleoInstanceIds(
						kaleoInstanceIds);
					kaleoTaskInstanceTokenQuery.setOrderByComparator(
						orderByComparator);
					kaleoTaskInstanceTokenQuery.
						setSearchByActiveWorkflowHandlers(
							searchByActiveWorkflowHandlers);
					kaleoTaskInstanceTokenQuery.setSearchByUserRoles(
						searchByUserRoles);
					kaleoTaskInstanceTokenQuery.setStart(start);
					kaleoTaskInstanceTokenQuery.setTaskNames(taskNames);

					return kaleoTaskInstanceTokenQuery;
				}
			).build(),
			start, end, orderByComparator, serviceContext);

		return new BaseModelSearchResult<>(
			(List<KaleoTaskInstanceToken>)TransformUtil.transformToList(
				hits.getDocs(),
				document -> kaleoTaskInstanceTokenPersistence.fetchByPrimaryKey(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))),
			hits.getLength());
	}

	@Override
	public KaleoTaskInstanceToken updateDueDate(
			long kaleoTaskInstanceTokenId, Date dueDate,
			ServiceContext serviceContext)
		throws PortalException {

		KaleoTaskInstanceToken kaleoTaskInstanceToken =
			kaleoTaskInstanceTokenPersistence.findByPrimaryKey(
				kaleoTaskInstanceTokenId);

		kaleoTaskInstanceToken.setModifiedDate(new Date());

		if (dueDate != null) {
			Calendar cal = CalendarFactoryUtil.getCalendar(
				LocaleUtil.getDefault());

			cal.setTime(dueDate);

			kaleoTaskInstanceToken.setDueDate(cal.getTime());
		}

		return kaleoTaskInstanceTokenLocalService.updateKaleoTaskInstanceToken(
			kaleoTaskInstanceToken);
	}

	private static String _getSortableFieldName(String name, String type) {
		return Field.getSortableFieldName(
			StringBundler.concat(name, StringPool.UNDERLINE, type));
	}

	private void _addCompletedCriterion(
		DynamicQuery dynamicQuery, Boolean completed) {

		if (completed == null) {
			return;
		}

		Property property = PropertyFactoryUtil.forName("completed");

		dynamicQuery.add(property.eq(completed));
	}

	private DynamicQuery _buildDynamicQuery(
		Boolean completed, ServiceContext serviceContext) {

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			KaleoTaskInstanceToken.class, getClassLoader());

		Property property = PropertyFactoryUtil.forName("companyId");

		dynamicQuery.add(property.eq(serviceContext.getCompanyId()));

		_addCompletedCriterion(dynamicQuery, completed);

		return dynamicQuery;
	}

	private DynamicQuery _buildDynamicQuery(
		long kaleoInstanceId, Boolean completed,
		ServiceContext serviceContext) {

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			KaleoTaskInstanceToken.class, getClassLoader());

		Property companyIdProperty = PropertyFactoryUtil.forName("companyId");

		dynamicQuery.add(companyIdProperty.eq(serviceContext.getCompanyId()));

		Property kaleoInstanceIdProperty = PropertyFactoryUtil.forName(
			"kaleoInstanceId");

		dynamicQuery.add(kaleoInstanceIdProperty.eq(kaleoInstanceId));

		_addCompletedCriterion(dynamicQuery, completed);

		return dynamicQuery;
	}

	private SearchContext _buildSearchContext(
		Map<String, Serializable> searchAttributes, int start, int end,
		OrderByComparator<KaleoTaskInstanceToken> orderByComparator,
		ServiceContext serviceContext) {

		SearchContext searchContext = new SearchContext();

		searchContext.setAttributes(searchAttributes);
		searchContext.setCompanyId(serviceContext.getCompanyId());
		searchContext.setEnd(end);
		searchContext.setGroupIds(new long[] {-1L});
		searchContext.setStart(start);

		if (orderByComparator != null) {
			searchContext.setSorts(_getSortsFromComparator(orderByComparator));
		}

		searchContext.setUserId(serviceContext.getUserId());

		return searchContext;
	}

	private ExecutionContext _createExecutionContext(
			KaleoTaskInstanceToken kaleoTaskInstanceToken)
		throws PortalException {

		KaleoInstanceToken kaleoInstanceToken =
			kaleoTaskInstanceToken.getKaleoInstanceToken();

		Map<String, Serializable> workflowContext = WorkflowContextUtil.convert(
			kaleoTaskInstanceToken.getWorkflowContext());

		ServiceContext workflowContextServiceContext =
			(ServiceContext)workflowContext.get(
				WorkflowConstants.CONTEXT_SERVICE_CONTEXT);

		ExecutionContext executionContext = new ExecutionContext(
			kaleoInstanceToken, workflowContext, workflowContextServiceContext);

		executionContext.setKaleoTaskInstanceToken(kaleoTaskInstanceToken);

		return executionContext;
	}

	private String[] _getAssetTypes(String assetType) {
		if (Validator.isNull(assetType)) {
			return null;
		}

		return new String[] {assetType};
	}

	private Long[] _getAssigneeClassPKs(Long assigneeClassPK) {
		if (Validator.isNull(assigneeClassPK)) {
			return null;
		}

		return new Long[] {assigneeClassPK};
	}

	private Long[] _getKaleoInstanceIds(Long kaleoInstanceId) {
		if (Validator.isNull(kaleoInstanceId)) {
			return null;
		}

		return new Long[] {kaleoInstanceId};
	}

	private Sort[] _getSortsFromComparator(
		OrderByComparator<KaleoTaskInstanceToken> orderByComparator) {

		if (orderByComparator == null) {
			return null;
		}

		return TransformUtil.transform(
			orderByComparator.getOrderByFields(),
			orderByFieldName -> {
				String fieldName = _fieldNameOrderByCols.getOrDefault(
					orderByFieldName, orderByFieldName);

				int sortType = _fieldNameSortTypes.getOrDefault(
					fieldName, Sort.STRING_TYPE);

				boolean ascending = orderByComparator.isAscending();

				if (Objects.equals(
						orderByFieldName,
						KaleoTaskInstanceTokenField.COMPLETED)) {

					ascending = true;
				}

				return new Sort(fieldName, sortType, !ascending);
			},
			Sort.class);
	}

	private String[] _getTaskNames(String taskName) {
		if (Validator.isNull(taskName)) {
			return null;
		}

		return new String[] {taskName};
	}

	private boolean _isNotifiableUser(
			KaleoTaskAssignment kaleoTaskAssignment,
			KaleoTaskInstanceToken kaleoTaskInstanceToken, long userId)
		throws PortalException {

		if (Objects.equals(
				kaleoTaskAssignment.getAssigneeClassName(),
				User.class.getName())) {

			List<KaleoTaskAssignmentInstance> kaleoTaskAssignmentInstances =
				_kaleoTaskAssignmentInstanceLocalService.
					getKaleoTaskAssignmentInstances(
						kaleoTaskInstanceToken.getKaleoTaskInstanceTokenId());

			for (KaleoTaskAssignmentInstance kaleoTaskAssignmentInstance :
					kaleoTaskAssignmentInstances) {

				if (kaleoTaskAssignmentInstance.getAssigneeClassPK() ==
						userId) {

					return true;
				}
			}

			return false;
		}

		User user = _userLocalService.getUser(userId);

		for (Role role : user.getAllRoles()) {
			if (role.getRoleId() == kaleoTaskAssignment.getAssigneeClassPK()) {
				return true;
			}
		}

		return false;
	}

	private Hits _search(
			Map<String, Serializable> searchAttributes, int start, int end,
			OrderByComparator<KaleoTaskInstanceToken> orderByComparator,
			ServiceContext serviceContext)
		throws PortalException {

		Indexer<KaleoTaskInstanceToken> indexer =
			IndexerRegistryUtil.getIndexer(
				KaleoTaskInstanceToken.class.getName());

		return indexer.search(
			_buildSearchContext(
				searchAttributes, start, end, orderByComparator,
				serviceContext));
	}

	private int _searchCount(
		Map<String, Serializable> searchAttributes,
		ServiceContext serviceContext) {

		try {
			Indexer<KaleoTaskInstanceToken> indexer =
				IndexerRegistryUtil.getIndexer(
					KaleoTaskInstanceToken.class.getName());

			return (int)indexer.searchCount(
				_buildSearchContext(
					searchAttributes, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null, serviceContext));
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return 0;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		KaleoTaskInstanceTokenLocalServiceImpl.class);

	private static final Map<String, String> _fieldNameOrderByCols =
		HashMapBuilder.put(
			Field.CREATE_DATE,
			_getSortableFieldName(Field.CREATE_DATE, "Number")
		).put(
			Field.USER_ID, _getSortableFieldName(Field.USER_ID, "Number")
		).put(
			KaleoTaskInstanceTokenField.COMPLETED,
			_getSortableFieldName(
				KaleoTaskInstanceTokenField.COMPLETED, "String")
		).put(
			KaleoTaskInstanceTokenField.COMPLETION_DATE,
			_getSortableFieldName(
				KaleoTaskInstanceTokenField.COMPLETION_DATE, "Number")
		).put(
			KaleoTaskInstanceTokenField.DUE_DATE,
			_getSortableFieldName(
				KaleoTaskInstanceTokenField.DUE_DATE, "Number")
		).put(
			KaleoTaskInstanceTokenField.KALEO_INSTANCE_ID,
			_getSortableFieldName(
				KaleoTaskInstanceTokenField.KALEO_INSTANCE_ID, "Number")
		).put(
			KaleoTaskInstanceTokenField.KALEO_TASK_ID,
			_getSortableFieldName(
				KaleoTaskInstanceTokenField.KALEO_TASK_ID, "Number")
		).put(
			KaleoTaskInstanceTokenField.KALEO_TASK_INSTANCE_TOKEN_ID,
			_getSortableFieldName(
				KaleoTaskInstanceTokenField.KALEO_TASK_INSTANCE_TOKEN_ID,
				"Number")
		).put(
			"modifiedDate", _getSortableFieldName(Field.MODIFIED_DATE, "Number")
		).put(
			"name",
			_getSortableFieldName(
				KaleoTaskInstanceTokenField.TASK_NAME, "String")
		).build();
	private static final Map<String, Integer> _fieldNameSortTypes =
		HashMapBuilder.put(
			Field.CREATE_DATE, Sort.LONG_TYPE
		).put(
			Field.MODIFIED_DATE, Sort.LONG_TYPE
		).put(
			KaleoTaskInstanceTokenField.COMPLETION_DATE, Sort.LONG_TYPE
		).put(
			KaleoTaskInstanceTokenField.DUE_DATE, Sort.LONG_TYPE
		).build();

	@Reference
	private AggregateKaleoTaskAssignmentSelector
		_aggregateKaleoTaskAssignmentSelector;

	@Reference
	private KaleoInstanceTokenPersistence _kaleoInstanceTokenPersistence;

	@Reference
	private KaleoTaskAssignmentInstanceLocalService
		_kaleoTaskAssignmentInstanceLocalService;

	@Reference
	private KaleoTaskAssignmentLocalService _kaleoTaskAssignmentLocalService;

	@Reference
	private KaleoTaskAssignmentPersistence _kaleoTaskAssignmentPersistence;

	@Reference
	private KaleoTaskFormInstanceLocalService
		_kaleoTaskFormInstanceLocalService;

	@Reference
	private KaleoTaskFormInstancePersistence _kaleoTaskFormInstancePersistence;

	@Reference
	private KaleoTaskFormPersistence _kaleoTaskFormPersistence;

	@Reference
	private KaleoTimerInstanceTokenLocalService
		_kaleoTimerInstanceTokenLocalService;

	@Reference
	private Staging _staging;

	@Reference
	private UserLocalService _userLocalService;

}