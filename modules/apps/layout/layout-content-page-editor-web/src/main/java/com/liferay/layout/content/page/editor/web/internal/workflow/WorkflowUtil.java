/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.workflow;

import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFunction;
import com.liferay.portal.kernel.workflow.WorkflowThreadLocal;

import jakarta.portlet.ActionRequest;

import java.util.function.Function;

/**
 * @author Alejandro Tardín
 */
public class WorkflowUtil {

	public static Function<String, ServiceContext> getServiceContextFunction(
		int workflowAction, ActionRequest actionRequest) {

		Function<String, ServiceContext> serviceContextFunction =
			new ServiceContextFunction(actionRequest);

		return serviceContextFunction.andThen(
			serviceContext -> {
				serviceContext.setWorkflowAction(workflowAction);

				return serviceContext;
			});
	}

	public static <E extends Exception> void withoutWorkflow(
			UnsafeRunnable<E> unsafeRunnable)
		throws E {

		boolean workflowEnabled = WorkflowThreadLocal.isEnabled();

		WorkflowThreadLocal.setEnabled(false);

		try {
			unsafeRunnable.run();
		}
		finally {
			WorkflowThreadLocal.setEnabled(workflowEnabled);
		}
	}

	public static <T, E extends Exception> T withoutWorkflow(
			UnsafeSupplier<T, E> unsafeSupplier)
		throws E {

		boolean workflowEnabled = WorkflowThreadLocal.isEnabled();

		WorkflowThreadLocal.setEnabled(false);

		try {
			return unsafeSupplier.get();
		}
		finally {
			WorkflowThreadLocal.setEnabled(workflowEnabled);
		}
	}

}