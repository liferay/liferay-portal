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
 * @author Matija Petanjek
 */
@Component(
	property = "dispatch.task.executor.type=" + TestDispatchTaskExecutor.DISPATCH_TASK_EXECUTOR_TYPE_TEST,
	service = DispatchTaskExecutor.class
)
public class TestDispatchTaskExecutor extends BaseDispatchTaskExecutor {

	public static final String DISPATCH_TASK_EXECUTOR_TYPE_TEST = "test";

	public static final int SLEEP_MILLIS = 1500;

	public static final AtomicInteger executionCounter = new AtomicInteger(0);

	@Override
	public void doExecute(
		DispatchTrigger dispatchTrigger,
		DispatchTaskExecutorOutput dispatchTaskExecutorOutput) {

		try {
			Thread.sleep(SLEEP_MILLIS);

			executionCounter.incrementAndGet();
		}
		catch (InterruptedException interruptedException) {
			interruptedException.printStackTrace();
		}
	}

	@Override
	public String getName() {
		return DISPATCH_TASK_EXECUTOR_TYPE_TEST;
	}

}