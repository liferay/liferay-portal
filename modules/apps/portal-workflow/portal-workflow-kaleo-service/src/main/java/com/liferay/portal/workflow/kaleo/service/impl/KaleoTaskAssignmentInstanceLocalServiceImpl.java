/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignment;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignmentInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;
import com.liferay.portal.workflow.kaleo.service.base.KaleoTaskAssignmentInstanceLocalServiceBaseImpl;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoInstancePersistence;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Marcellus Tavares
 */
@Component(
	property = "model.class.name=com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignmentInstance",
	service = AopService.class
)
public class KaleoTaskAssignmentInstanceLocalServiceImpl
	extends KaleoTaskAssignmentInstanceLocalServiceBaseImpl {

	@Override
	public KaleoTaskAssignmentInstance addKaleoTaskAssignmentInstance(
			long groupId, KaleoTaskInstanceToken kaleoTaskInstanceToken,
			String assigneeClassName, long assigneeClassPK,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(
			serviceContext.getGuestOrUserId());
		Date date = new Date();

		long kaleoTaskAssignmentInstanceId = counterLocalService.increment();

		KaleoTaskAssignmentInstance kaleoTaskAssignmentInstance =
			kaleoTaskAssignmentInstancePersistence.create(
				kaleoTaskAssignmentInstanceId);

		kaleoTaskAssignmentInstance.setGroupId(groupId);
		kaleoTaskAssignmentInstance.setCompanyId(user.getCompanyId());
		kaleoTaskAssignmentInstance.setUserId(user.getUserId());
		kaleoTaskAssignmentInstance.setUserName(user.getFullName());
		kaleoTaskAssignmentInstance.setCreateDate(date);
		kaleoTaskAssignmentInstance.setModifiedDate(date);
		kaleoTaskAssignmentInstance.setKaleoDefinitionId(
			kaleoTaskInstanceToken.getKaleoDefinitionId());
		kaleoTaskAssignmentInstance.setKaleoDefinitionVersionId(
			kaleoTaskInstanceToken.getKaleoDefinitionVersionId());
		kaleoTaskAssignmentInstance.setKaleoInstanceId(
			kaleoTaskInstanceToken.getKaleoInstanceId());
		kaleoTaskAssignmentInstance.setKaleoInstanceTokenId(
			kaleoTaskInstanceToken.getKaleoInstanceTokenId());
		kaleoTaskAssignmentInstance.setKaleoTaskInstanceTokenId(
			kaleoTaskInstanceToken.getKaleoTaskInstanceTokenId());
		kaleoTaskAssignmentInstance.setKaleoTaskId(
			kaleoTaskInstanceToken.getKaleoTaskId());
		kaleoTaskAssignmentInstance.setKaleoTaskName(
			kaleoTaskInstanceToken.getKaleoTaskName());
		kaleoTaskAssignmentInstance.setAssigneeClassName(assigneeClassName);

		if ((assigneeClassPK == 0) &&
			assigneeClassName.equals(User.class.getName())) {

			KaleoInstance kaleoInstance =
				_kaleoInstancePersistence.findByPrimaryKey(
					kaleoTaskInstanceToken.getKaleoInstanceId());

			kaleoTaskAssignmentInstance.setAssigneeClassPK(
				kaleoInstance.getUserId());
		}
		else {
			kaleoTaskAssignmentInstance.setAssigneeClassPK(assigneeClassPK);
		}

		kaleoTaskAssignmentInstance.setCompleted(false);

		return kaleoTaskAssignmentInstancePersistence.update(
			kaleoTaskAssignmentInstance);
	}

	@Override
	public List<KaleoTaskAssignmentInstance> addTaskAssignmentInstances(
			KaleoTaskInstanceToken kaleoTaskInstanceToken,
			Collection<KaleoTaskAssignment> kaleoTaskAssignments,
			Map<String, Serializable> workflowContext,
			ServiceContext serviceContext)
		throws PortalException {

		List<KaleoTaskAssignmentInstance> kaleoTaskAssignmentInstances =
			new ArrayList<>(kaleoTaskAssignments.size());

		for (KaleoTaskAssignment kaleoTaskAssignment : kaleoTaskAssignments) {
			long groupId = kaleoTaskAssignment.getGroupId();

			if (groupId <= 0) {
				groupId = kaleoTaskInstanceToken.getGroupId();
			}

			KaleoTaskAssignmentInstance kaleoTaskAssignmentInstance =
				addKaleoTaskAssignmentInstance(
					groupId, kaleoTaskInstanceToken,
					kaleoTaskAssignment.getAssigneeClassName(),
					kaleoTaskAssignment.getAssigneeClassPK(), serviceContext);

			kaleoTaskAssignmentInstances.add(kaleoTaskAssignmentInstance);
		}

		return kaleoTaskAssignmentInstances;
	}

	@Override
	public KaleoTaskAssignmentInstance assignKaleoTaskAssignmentInstance(
			KaleoTaskInstanceToken kaleoTaskInstanceToken,
			String assigneeClassName, long assigneeClassPK,
			ServiceContext serviceContext)
		throws PortalException {

		deleteKaleoTaskAssignmentInstances(kaleoTaskInstanceToken);

		return addKaleoTaskAssignmentInstance(
			kaleoTaskInstanceToken.getGroupId(), kaleoTaskInstanceToken,
			assigneeClassName, assigneeClassPK, serviceContext);
	}

	@Override
	public List<KaleoTaskAssignmentInstance> assignKaleoTaskAssignmentInstances(
			KaleoTaskInstanceToken kaleoTaskInstanceToken,
			Collection<KaleoTaskAssignment> kaleoTaskAssignments,
			Map<String, Serializable> workflowContext,
			ServiceContext serviceContext)
		throws PortalException {

		deleteKaleoTaskAssignmentInstances(kaleoTaskInstanceToken);

		return addTaskAssignmentInstances(
			kaleoTaskInstanceToken, kaleoTaskAssignments, workflowContext,
			serviceContext);
	}

	@Override
	public KaleoTaskAssignmentInstance completeKaleoTaskInstanceToken(
			long kaleoTaskInstanceTokenId, ServiceContext serviceContext)
		throws PortalException {

		List<KaleoTaskAssignmentInstance> kaleoTaskAssignmentInstances =
			kaleoTaskAssignmentInstancePersistence.
				findByKaleoTaskInstanceTokenId(kaleoTaskInstanceTokenId);

		if (kaleoTaskAssignmentInstances.size() > 1) {
			throw new WorkflowException(
				"Cannot complete a task that is not assigned to an " +
					"individual user");
		}

		KaleoTaskAssignmentInstance kaleoTaskAssignmentInstance =
			kaleoTaskAssignmentInstances.get(0);

		kaleoTaskAssignmentInstance.setCompleted(true);
		kaleoTaskAssignmentInstance.setCompletionDate(new Date());

		return kaleoTaskAssignmentInstancePersistence.update(
			kaleoTaskAssignmentInstance);
	}

	@Override
	public void deleteCompanyKaleoTaskAssignmentInstances(long companyId) {
		kaleoTaskAssignmentInstancePersistence.removeByCompanyId(companyId);
	}

	@Override
	public void deleteKaleoDefinitionVersionKaleoTaskAssignmentInstances(
		long kaleoDefinitionId) {

		kaleoTaskAssignmentInstancePersistence.removeByKaleoDefinitionVersionId(
			kaleoDefinitionId);
	}

	@Override
	public void deleteKaleoInstanceKaleoTaskAssignmentInstances(
		long kaleoInstanceId) {

		kaleoTaskAssignmentInstancePersistence.removeByKaleoInstanceId(
			kaleoInstanceId);
	}

	@Override
	public void deleteKaleoTaskAssignmentInstances(
		KaleoTaskInstanceToken kaleoTaskInstanceToken) {

		List<KaleoTaskAssignmentInstance> kaleoTaskAssignmentInstances =
			kaleoTaskAssignmentInstancePersistence.
				findByKaleoTaskInstanceTokenId(
					kaleoTaskInstanceToken.getKaleoTaskInstanceTokenId());

		for (KaleoTaskAssignmentInstance kaleoTaskAssignmentInstance :
				kaleoTaskAssignmentInstances) {

			kaleoTaskAssignmentInstancePersistence.remove(
				kaleoTaskAssignmentInstance);
		}
	}

	@Override
	public KaleoTaskAssignmentInstance fetchFirstKaleoTaskAssignmentInstance(
		long kaleoTaskInstanceTokenId,
		OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator) {

		return kaleoTaskAssignmentInstancePersistence.
			fetchByKaleoTaskInstanceTokenId_First(
				kaleoTaskInstanceTokenId, orderByComparator);
	}

	@Override
	public List<KaleoTaskAssignmentInstance> getKaleoTaskAssignmentInstances(
		long kaleoTaskInstanceTokenId) {

		return kaleoTaskAssignmentInstancePersistence.
			findByKaleoTaskInstanceTokenId(kaleoTaskInstanceTokenId);
	}

	@Override
	public int getKaleoTaskAssignmentInstancesCount(
		long kaleoTaskInstanceTokenId) {

		return kaleoTaskAssignmentInstancePersistence.
			countByKaleoTaskInstanceTokenId(kaleoTaskInstanceTokenId);
	}

	@Reference
	private KaleoInstancePersistence _kaleoInstancePersistence;

	@Reference
	private UserLocalService _userLocalService;

}