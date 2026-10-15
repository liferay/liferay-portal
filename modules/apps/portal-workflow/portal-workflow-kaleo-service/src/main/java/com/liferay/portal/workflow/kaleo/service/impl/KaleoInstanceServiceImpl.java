/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.workflow.constants.WorkflowActionKeys;
import com.liferay.portal.workflow.constants.WorkflowDefinitionConstants;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;
import com.liferay.portal.workflow.kaleo.service.base.KaleoInstanceServiceBaseImpl;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoDefinitionPersistence;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = {
		"json.web.service.context.name=kaleo",
		"json.web.service.context.path=KaleoInstance"
	},
	service = AopService.class
)
public class KaleoInstanceServiceImpl extends KaleoInstanceServiceBaseImpl {

	@Override
	public KaleoInstance addKaleoInstance(
			String kaleoDefinitionName, Integer kaleoDefinitionVersion,
			String transitionName, Map<String, Serializable> workflowContext,
			ServiceContext serviceContext, boolean waitForCompletion)
		throws PortalException {

		KaleoDefinition kaleoDefinition = _kaleoDefinitionPersistence.findByC_N(
			serviceContext.getCompanyId(), kaleoDefinitionName);

		if (Objects.equals(
				kaleoDefinition.getScope(),
				WorkflowDefinitionConstants.SCOPE_AI)) {

			_portletResourcePermission.check(
				getPermissionChecker(), serviceContext.getScopeGroupId(),
				ActionKeys.ADD_INSTANCE);
		}
		else {
			_portletResourcePermission.check(
				getPermissionChecker(), serviceContext.getScopeGroupId(),
				WorkflowActionKeys.START_WORKFLOW_INSTANCE);
		}

		return kaleoInstanceLocalService.addKaleoInstance(
			kaleoDefinitionName, kaleoDefinitionVersion, transitionName,
			workflowContext, serviceContext, waitForCompletion);
	}

	@Override
	public KaleoInstance deleteKaleoInstance(long kaleoInstanceId)
		throws PortalException {

		_kaleoInstanceModelResourcePermission.check(
			getPermissionChecker(), kaleoInstanceId, ActionKeys.DELETE);

		return kaleoInstanceLocalService.deleteKaleoInstance(kaleoInstanceId);
	}

	@Override
	public KaleoInstance getKaleoInstance(long kaleoInstanceId)
		throws PortalException {

		KaleoInstance kaleoInstance = kaleoInstancePersistence.findByPrimaryKey(
			kaleoInstanceId);

		_kaleoInstanceModelResourcePermission.check(
			getPermissionChecker(), kaleoInstance, ActionKeys.VIEW);

		return kaleoInstance;
	}

	@Override
	public KaleoInstance updateKaleoInstance(
			long kaleoInstanceId, Map<String, Serializable> workflowContext)
		throws PortalException {

		_kaleoInstanceModelResourcePermission.check(
			getPermissionChecker(), kaleoInstanceId, ActionKeys.UPDATE);

		return kaleoInstanceLocalService.updateKaleoInstance(
			kaleoInstanceId, workflowContext);
	}

	@Reference
	private KaleoDefinitionPersistence _kaleoDefinitionPersistence;

	@Reference(
		target = "(model.class.name=com.liferay.portal.workflow.kaleo.model.KaleoInstance)"
	)
	private ModelResourcePermission<KaleoInstance>
		_kaleoInstanceModelResourcePermission;

	@Reference(
		target = "(resource.name=" + WorkflowConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}