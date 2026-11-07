/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime.internal.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;

import java.io.Serializable;

import java.util.Map;

/**
 * @author Marcellus Tavares
 */
public class NotificationMessageUtil {

	public static JSONObject createMessageJSONObject(
		ExecutionContext executionContext, String notificationMessage) {

		Map<String, Serializable> workflowContext =
			executionContext.getWorkflowContext();

		JSONObject jsonObject = JSONUtil.put(
			WorkflowConstants.CONTEXT_COMPANY_ID,
			String.valueOf(
				workflowContext.get(WorkflowConstants.CONTEXT_COMPANY_ID))
		).put(
			WorkflowConstants.CONTEXT_CT_COLLECTION_ID,
			String.valueOf(
				workflowContext.get(WorkflowConstants.CONTEXT_CT_COLLECTION_ID))
		).put(
			WorkflowConstants.CONTEXT_ENTRY_CLASS_NAME,
			(String)workflowContext.get(
				WorkflowConstants.CONTEXT_ENTRY_CLASS_NAME)
		).put(
			WorkflowConstants.CONTEXT_ENTRY_CLASS_PK,
			String.valueOf(
				workflowContext.get(WorkflowConstants.CONTEXT_ENTRY_CLASS_PK))
		).put(
			WorkflowConstants.CONTEXT_ENTRY_TYPE,
			(String)workflowContext.get(WorkflowConstants.CONTEXT_ENTRY_TYPE)
		).put(
			WorkflowConstants.CONTEXT_GROUP_ID,
			String.valueOf(
				workflowContext.get(WorkflowConstants.CONTEXT_GROUP_ID))
		).put(
			WorkflowConstants.CONTEXT_URL,
			String.valueOf(workflowContext.get(WorkflowConstants.CONTEXT_URL))
		);

		KaleoInstanceToken kaleoInstanceToken =
			executionContext.getKaleoInstanceToken();

		jsonObject.put(
			WorkflowConstants.CONTEXT_USER_ID,
			String.valueOf(_getUserId(executionContext, kaleoInstanceToken))
		).put(
			"notificationMessage", notificationMessage
		).put(
			"plid", workflowContext.get("plid")
		).put(
			"portletId", workflowContext.get("portletId")
		).put(
			"workflowInstanceId", kaleoInstanceToken.getKaleoInstanceId()
		);

		KaleoTaskInstanceToken kaleoTaskInstanceToken =
			executionContext.getKaleoTaskInstanceToken();

		if (kaleoTaskInstanceToken != null) {
			jsonObject.put(
				"workflowTaskId",
				kaleoTaskInstanceToken.getKaleoTaskInstanceTokenId());
		}

		return jsonObject;
	}

	private static long _getUserId(
		ExecutionContext executionContext,
		KaleoInstanceToken kaleoInstanceToken) {

		try {
			ServiceContext serviceContext =
				executionContext.getServiceContext();

			return serviceContext.getGuestOrUserId();
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get user from context, using userId from " +
						"kaleoInstanceToken instead",
					portalException);
			}

			return kaleoInstanceToken.getUserId();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		NotificationMessageUtil.class);

}