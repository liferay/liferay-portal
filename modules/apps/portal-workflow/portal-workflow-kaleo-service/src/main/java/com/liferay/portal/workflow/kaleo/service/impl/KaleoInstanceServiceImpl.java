/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.workflow.constants.WorkflowDefinitionConstants;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.model.KaleoNode;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.KaleoSignaler;
import com.liferay.portal.workflow.kaleo.service.KaleoLogLocalService;
import com.liferay.portal.workflow.kaleo.service.base.KaleoInstanceServiceBaseImpl;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoDefinitionPersistence;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoDefinitionVersionPersistence;

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

		if (!kaleoDefinition.isActive()) {
			throw new WorkflowException(
				StringBundler.concat(
					"Inactive workflow definition with name ",
					kaleoDefinitionName, " and version ",
					kaleoDefinitionVersion));
		}

		KaleoDefinitionVersion serviceBuilderKaleoDefinitionVersion =
			_kaleoDefinitionVersionPersistence.findByC_N_V(
				serviceContext.getCompanyId(), kaleoDefinitionName,
				_getVersion(kaleoDefinitionVersion));

		KaleoNode kaleoStartNode =
			serviceBuilderKaleoDefinitionVersion.getKaleoStartNode();

		if (Validator.isNotNull(transitionName)) {

			// Validate that the transition actually exists before moving
			// forward

			kaleoStartNode.getKaleoTransition(transitionName);
		}

		long scopeGroupId = serviceContext.getScopeGroupId();

		if (scopeGroupId != WorkflowConstants.DEFAULT_GROUP_ID) {
			Group group = _groupLocalService.getGroup(scopeGroupId);

			if (group.isLayout()) {
				group = _groupLocalService.getGroup(group.getParentGroupId());

				serviceContext.setScopeGroupId(group.getGroupId());
			}
		}

		KaleoInstance kaleoInstance =
			kaleoInstanceLocalService.addKaleoInstance(
				kaleoDefinition.getKaleoDefinitionId(),
				serviceBuilderKaleoDefinitionVersion.
					getKaleoDefinitionVersionId(),
				serviceBuilderKaleoDefinitionVersion.getName(),
				_getVersion(serviceBuilderKaleoDefinitionVersion.getVersion()),
				workflowContext, serviceContext);

		KaleoInstanceToken rootKaleoInstanceToken =
			kaleoInstance.getRootKaleoInstanceToken(
				workflowContext, serviceContext);

		rootKaleoInstanceToken.setCurrentKaleoNode(kaleoStartNode);

		_kaleoLogLocalService.addInstanceStartKaleoLog(
			rootKaleoInstanceToken, serviceContext);

		ExecutionContext executionContext = new ExecutionContext(
			rootKaleoInstanceToken, workflowContext, serviceContext);

		TransactionCallbackUtil.registerCommitCallback(
			() -> {
				try {
					_kaleoSignaler.signalEntry(
						transitionName, executionContext, waitForCompletion);
				}
				catch (Exception exception) {
					throw new WorkflowException(
						"Unable to start workflow", exception);
				}

				return null;
			});

		return kaleoInstance;
	}

	private String _getVersion(int version) {
		return version + StringPool.PERIOD + 0;
	}

	private int _getVersion(String version) {
		int[] versionParts = StringUtil.split(version, StringPool.PERIOD, 0);

		return versionParts[0];
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private KaleoDefinitionPersistence _kaleoDefinitionPersistence;

	@Reference
	private KaleoDefinitionVersionPersistence
		_kaleoDefinitionVersionPersistence;

	@Reference
	private KaleoLogLocalService _kaleoLogLocalService;

	@Reference
	private KaleoSignaler _kaleoSignaler;

	@Reference(
		target = "(resource.name=" + WorkflowConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}