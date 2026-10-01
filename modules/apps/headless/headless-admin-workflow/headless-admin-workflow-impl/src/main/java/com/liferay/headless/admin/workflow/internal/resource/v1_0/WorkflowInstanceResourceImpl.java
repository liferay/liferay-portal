/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.internal.resource.v1_0;

import com.liferay.headless.admin.workflow.dto.v1_0.ChangeTransition;
import com.liferay.headless.admin.workflow.dto.v1_0.WorkflowInstance;
import com.liferay.headless.admin.workflow.dto.v1_0.WorkflowInstanceSubmit;
import com.liferay.headless.admin.workflow.internal.dto.v1_0.util.ObjectReviewedUtil;
import com.liferay.headless.admin.workflow.resource.v1_0.WorkflowInstanceResource;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManager;
import com.liferay.portal.kernel.workflow.WorkflowNode;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/workflow-instance.properties",
	scope = ServiceScope.PROTOTYPE, service = WorkflowInstanceResource.class
)
@CTAware
public class WorkflowInstanceResourceImpl
	extends BaseWorkflowInstanceResourceImpl {

	@Override
	public void deleteWorkflowInstance(Long workflowInstanceId)
		throws Exception {

		_workflowInstanceManager.deleteWorkflowInstance(
			contextCompany.getCompanyId(), workflowInstanceId);
	}

	@Override
	public WorkflowInstance getWorkflowInstance(Long workflowInstanceId)
		throws Exception {

		return _toWorkflowInstance(
			_workflowInstanceManager.getWorkflowInstance(workflowInstanceId));
	}

	@Override
	public Page<WorkflowInstance> getWorkflowInstancesPage(
			String assetClassName, Long assetPrimaryKey, Boolean completed,
			Pagination pagination)
		throws Exception {

		return Page.of(
			HashMapBuilder.put(
				"get",
				addAction(
					ActionKeys.VIEW, "getWorkflowInstancesPage",
					WorkflowConstants.RESOURCE_NAME, null)
			).build(),
			transform(
				_workflowInstanceManager.getWorkflowInstances(
					contextCompany.getCompanyId(), contextUser.getUserId(),
					assetClassName, assetPrimaryKey,
					GetterUtil.getBoolean(completed),
					pagination.getStartPosition(), pagination.getEndPosition(),
					null),
				this::_toWorkflowInstance),
			pagination,
			_workflowInstanceManager.getWorkflowInstanceCount(
				contextCompany.getCompanyId(), contextUser.getUserId(),
				assetClassName, assetPrimaryKey,
				GetterUtil.getBoolean(completed)));
	}

	@Override
	public WorkflowInstance patchWorkflowInstance(
			Long workflowInstanceId, WorkflowInstance workflowInstance)
		throws Exception {

		return _toWorkflowInstance(
			_workflowInstanceManager.updateContext(
				workflowInstanceId,
				_getWorkflowContext(
					workflowInstance.getContext(), workflowInstanceId)));
	}

	@Override
	public WorkflowInstance postWorkflowInstanceChangeTransition(
			Long workflowInstanceId, ChangeTransition changeTransition)
		throws Exception {

		return _toWorkflowInstance(
			_workflowInstanceManager.signalWorkflowInstance(
				contextCompany.getCompanyId(), contextUser.getUserId(),
				workflowInstanceId, changeTransition.getTransitionName(),
				null));
	}

	@Override
	public WorkflowInstance postWorkflowInstanceSubmit(
			WorkflowInstanceSubmit workflowInstanceSubmit)
		throws Exception {

		return _toWorkflowInstance(
			_workflowInstanceManager.startWorkflowInstance(
				contextCompany.getCompanyId(),
				GetterUtil.getLong(workflowInstanceSubmit.getSiteId()),
				contextUser.getUserId(),
				workflowInstanceSubmit.getWorkflowDefinitionName(),
				GetterUtil.getInteger(
					workflowInstanceSubmit.getWorkflowDefinitionVersion()),
				workflowInstanceSubmit.getTransitionName(),
				_toWorkflowContext(
					workflowInstanceSubmit.getContext(), false,
					GetterUtil.getLong(workflowInstanceSubmit.getSiteId()))));
	}

	private Map<String, Serializable> _getWorkflowContext(
			Map<String, ?> context, long workflowInstanceId)
		throws Exception {

		com.liferay.portal.kernel.workflow.WorkflowInstance workflowInstance =
			_workflowInstanceManager.getWorkflowInstance(
				contextCompany.getCompanyId(), workflowInstanceId);

		Map<String, Serializable> actualWorkflowContext =
			workflowInstance.getWorkflowContext();

		Map<String, Serializable> workflowContext = _toWorkflowContext(
			context, true, GetterUtil.getLong(workflowInstance.getGroupId()));

		for (Map.Entry<String, Serializable> entry :
				workflowContext.entrySet()) {

			actualWorkflowContext.putIfAbsent(entry.getKey(), entry.getValue());
		}

		return actualWorkflowContext;
	}

	private Map<String, Serializable> _toWorkflowContext(
			Map<String, ?> context, boolean readOnly, long siteId)
		throws Exception {

		Map<String, Serializable> workflowContext = new HashMap<>();

		for (Map.Entry<String, ?> entry : context.entrySet()) {
			if (entry.getValue() instanceof Serializable value) {
				workflowContext.put(entry.getKey(), value);
			}
		}

		if (readOnly) {
			workflowContext.remove(WorkflowConstants.CONTEXT_SERVICE_CONTEXT);
		}
		else if (!workflowContext.containsKey(
					WorkflowConstants.CONTEXT_SERVICE_CONTEXT)) {

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				contextHttpServletRequest);

			serviceContext.setScopeGroupId(siteId);

			workflowContext.put(
				WorkflowConstants.CONTEXT_SERVICE_CONTEXT, serviceContext);
		}

		return workflowContext;
	}

	private WorkflowInstance _toWorkflowInstance(
			com.liferay.portal.kernel.workflow.WorkflowInstance
				workflowInstance)
		throws Exception {

		return new WorkflowInstance() {
			{
				setActions(
					() -> HashMapBuilder.put(
						"changeTransition",
						addAction(
							ActionKeys.UPDATE,
							workflowInstance.getWorkflowInstanceId(),
							"postWorkflowInstanceChangeTransition",
							_kaleoInstanceModelResourcePermission)
					).put(
						"delete",
						addAction(
							ActionKeys.DELETE,
							workflowInstance.getWorkflowInstanceId(),
							"deleteWorkflowInstance",
							_kaleoInstanceModelResourcePermission)
					).build());
				setCompleted(workflowInstance::isComplete);
				setContext(
					() -> _toWorkflowContext(
						workflowInstance.getWorkflowContext(), true,
						workflowInstance.getGroupId()));
				setCurrentNodeNames(
					() -> transformToArray(
						workflowInstance.getCurrentWorkflowNodes(),
						WorkflowNode::getName, String.class));
				setDateCompletion(workflowInstance::getEndDate);
				setDateCreated(workflowInstance::getStartDate);
				setId(workflowInstance::getWorkflowInstanceId);
				setObjectReviewed(
					() -> ObjectReviewedUtil.toObjectReviewed(
						contextAcceptLanguage.getPreferredLocale(),
						workflowInstance.getWorkflowContext()));
				setWorkflowDefinitionName(
					workflowInstance::getWorkflowDefinitionName);
				setWorkflowDefinitionVersion(
					() -> String.valueOf(
						workflowInstance.getWorkflowDefinitionVersion()));
			}
		};
	}

	@Reference(
		target = "(model.class.name=com.liferay.portal.workflow.kaleo.model.KaleoInstance)"
	)
	private ModelResourcePermission<?> _kaleoInstanceModelResourcePermission;

	@Reference
	private WorkflowInstanceManager _workflowInstanceManager;

}