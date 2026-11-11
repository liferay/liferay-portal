/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime.node;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.workflow.kaleo.definition.ExecutionType;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.model.KaleoNode;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.action.KaleoActionExecutor;
import com.liferay.portal.workflow.kaleo.runtime.graph.PathElement;
import com.liferay.portal.workflow.kaleo.runtime.notification.KaleoNotificationSender;
import com.liferay.portal.workflow.kaleo.runtime.util.ExecutionContextHelper;
import com.liferay.portal.workflow.kaleo.service.KaleoInstanceTokenLocalServiceUtil;
import com.liferay.portal.workflow.kaleo.service.KaleoTimerInstanceTokenLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTimerLocalService;

import java.util.List;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
public abstract class BaseNodeExecutor implements NodeExecutor {

	@Override
	public boolean enter(
			KaleoNode currentKaleoNode, ExecutionContext executionContext)
		throws PortalException {

		KaleoInstanceToken kaleoInstanceToken =
			executionContext.getKaleoInstanceToken();

		if (kaleoInstanceToken.getCurrentKaleoNodeId() !=
				currentKaleoNode.getKaleoNodeId()) {

			executionContext.setKaleoInstanceToken(
				KaleoInstanceTokenLocalServiceUtil.updateKaleoInstanceToken(
					kaleoInstanceToken.getKaleoInstanceTokenId(),
					currentKaleoNode.getKaleoNodeId(),
					currentKaleoNode.getName()));
		}

		boolean performExecute = doEnter(currentKaleoNode, executionContext);

		kaleoActionExecutor.executeKaleoActions(
			KaleoNode.class.getName(), currentKaleoNode.getKaleoNodeId(),
			ExecutionType.ON_ENTRY, executionContext);

		kaleoNotificationSender.sendNotifications(
			KaleoNode.class.getName(), currentKaleoNode.getKaleoNodeId(),
			ExecutionType.ON_ENTRY, executionContext);

		return performExecute;
	}

	@Override
	public void execute(
			KaleoNode currentKaleoNode, ExecutionContext executionContext,
			List<PathElement> remainingPathElements)
		throws PortalException {

		if (executionContextHelper.isKaleoInstanceBlocked(executionContext)) {
			return;
		}

		doExecute(currentKaleoNode, executionContext, remainingPathElements);
	}

	@Override
	public void exit(
			KaleoNode currentKaleoNode, ExecutionContext executionContext,
			List<PathElement> remainingPathElements)
		throws PortalException {

		executionContextHelper.completeKaleoTimerInstances(executionContext);

		doExit(currentKaleoNode, executionContext, remainingPathElements);

		kaleoActionExecutor.executeKaleoActions(
			KaleoNode.class.getName(), currentKaleoNode.getKaleoNodeId(),
			ExecutionType.ON_EXIT, executionContext);

		kaleoNotificationSender.sendNotifications(
			KaleoNode.class.getName(), currentKaleoNode.getKaleoNodeId(),
			ExecutionType.ON_EXIT, executionContext);
	}

	protected abstract boolean doEnter(
			KaleoNode currentKaleoNode, ExecutionContext executionContext)
		throws PortalException;

	protected abstract void doExecute(
			KaleoNode currentKaleoNode, ExecutionContext executionContext,
			List<PathElement> remainingPathElements)
		throws PortalException;

	protected abstract void doExit(
			KaleoNode currentKaleoNode, ExecutionContext executionContext,
			List<PathElement> remainingPathElements)
		throws PortalException;

	@Reference
	protected ExecutionContextHelper executionContextHelper;

	@Reference
	protected KaleoActionExecutor kaleoActionExecutor;

	@Reference
	protected KaleoNotificationSender kaleoNotificationSender;

	@Reference
	protected KaleoTimerInstanceTokenLocalService
		kaleoTimerInstanceTokenLocalService;

	@Reference
	protected KaleoTimerLocalService kaleoTimerLocalService;

}