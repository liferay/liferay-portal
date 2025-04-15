/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.portlet.shared.task;

import com.liferay.portal.search.web.internal.portlet.shared.task.helper.PortletSharedRequestHelper;
import com.liferay.portal.search.web.portlet.shared.task.PortletSharedTask;
import com.liferay.portal.search.web.portlet.shared.task.PortletSharedTaskExecutor;

import jakarta.portlet.RenderRequest;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author André de Oliveira
 */
@Component(service = PortletSharedTaskExecutor.class)
public class PortletSharedTaskExecutorImpl
	implements PortletSharedTaskExecutor {

	@Override
	public <T> T executeOnlyOnce(
		PortletSharedTask<T> portletSharedTask, String attributeSuffix,
		RenderRequest renderRequest) {

		FutureTask<T> futureTask = null;

		AtomicBoolean oldTaskExists = new AtomicBoolean(true);

		String attributeName = "LIFERAY_SHARED_" + attributeSuffix;

		synchronized (renderRequest) {
			futureTask = portletSharedRequestHelper.getAttribute(
				attributeName, renderRequest);

			if (futureTask == null) {
				futureTask = new FutureTask<>(portletSharedTask::execute);

				portletSharedRequestHelper.setAttribute(
					attributeName, futureTask, renderRequest);

				oldTaskExists.set(false);
			}
		}

		if (!oldTaskExists.get()) {
			futureTask.run();
		}

		try {
			return futureTask.get();
		}
		catch (ExecutionException | InterruptedException exception) {
			throw new RuntimeException(exception);
		}
	}

	@Reference
	protected PortletSharedRequestHelper portletSharedRequestHelper;

}