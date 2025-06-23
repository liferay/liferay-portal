/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.executor.internal.messaging;

import com.liferay.dispatch.executor.BaseDispatchTaskExecutor;
import com.liferay.dispatch.executor.DispatchTaskExecutor;
import com.liferay.dispatch.executor.DispatchTaskExecutorOutput;
import com.liferay.dispatch.model.DispatchTrigger;

import java.util.concurrent.atomic.AtomicInteger;

import org.osgi.service.component.annotations.Component;

/**
 * @author Igor Beslic
 */
@Component(
	property = "dispatch.task.executor.type=" + SingleNodeClusterModeDispatchTaskExecutor.DISPATCH_TASK_EXECUTOR_TYPE_SINGLE_NODE,
	service = DispatchTaskExecutor.class
)
public class SingleNodeClusterModeDispatchTaskExecutor
	extends BaseDispatchTaskExecutor {

	public static final String DISPATCH_TASK_EXECUTOR_TYPE_SINGLE_NODE =
		"test-single-node";

	@Override
	public void doExecute(
		DispatchTrigger dispatchTrigger,
		DispatchTaskExecutorOutput dispatchTaskExecutorOutput) {

		executionCounter.incrementAndGet();
	}

	@Override
	public String getName() {
		return DISPATCH_TASK_EXECUTOR_TYPE_SINGLE_NODE;
	}

	@Override
	public boolean isClusterModeSingle() {
		return true;
	}

	protected static final AtomicInteger executionCounter = new AtomicInteger(
		0);

}