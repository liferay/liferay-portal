/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.agent.util;

import com.liferay.petra.concurrent.DefaultNoticeableFuture;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.workflow.WorkflowInstance;

import java.io.Serializable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Feliphe Marinho
 * @author João Victor Alves
 */
public class AgentUtil {

	public static void complete(Message message) {
		DefaultNoticeableFuture<Map<String, Serializable>>
			defaultNoticeableFuture = _defaultNoticeableFutures.remove(
				message.getLong("workflowInstanceId"));

		if (defaultNoticeableFuture != null) {
			defaultNoticeableFuture.set(
				(Map<String, Serializable>)message.get("workflowContext"));
		}
	}

	public static void completeExceptionally(Message message) {
		DefaultNoticeableFuture<Map<String, Serializable>>
			defaultNoticeableFuture = _defaultNoticeableFutures.remove(
				message.getLong("workflowInstanceId"));

		if (defaultNoticeableFuture != null) {
			defaultNoticeableFuture.set(
				HashMapBuilder.<String, Serializable>put(
					"exception", (Exception)message.get("exception")
				).build());
		}
	}

	public static String getOutput(WorkflowInstance workflowInstance)
		throws Exception {

		DefaultNoticeableFuture<Map<String, Serializable>>
			defaultNoticeableFuture = new DefaultNoticeableFuture<>();

		_defaultNoticeableFutures.put(
			workflowInstance.getWorkflowInstanceId(), defaultNoticeableFuture);

		Map<String, Serializable> workflowContext = defaultNoticeableFuture.get(
			1, TimeUnit.MINUTES);

		if (workflowContext.get("exception") instanceof Exception exception) {
			throw exception;
		}

		return MapUtil.getString(workflowContext, "output");
	}

	private static final ConcurrentMap
		<Long, DefaultNoticeableFuture<Map<String, Serializable>>>
			_defaultNoticeableFutures = new ConcurrentHashMap<>();

}