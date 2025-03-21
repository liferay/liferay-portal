/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.instance.tracker.url.provider;

import com.liferay.osgi.util.ServiceTrackerFactory;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Feliphe Marinho
 */
public class WorkflowInstanceTrackerURLProviderUtil {

	public static String getURL(
			Object bean, HttpServletRequest httpServletRequest,
			Class<?> modelClass, boolean useDialog)
		throws Exception {

		WorkflowInstanceTrackerURLProvider workflowInstanceTrackerURLProvider =
			_serviceTracker.getService();

		return workflowInstanceTrackerURLProvider.getURL(
			bean, httpServletRequest, modelClass, useDialog);
	}

	private static final ServiceTracker
		<WorkflowInstanceTrackerURLProvider, WorkflowInstanceTrackerURLProvider>
			_serviceTracker = ServiceTrackerFactory.open(
				FrameworkUtil.getBundle(
					WorkflowInstanceTrackerURLProviderUtil.class),
				WorkflowInstanceTrackerURLProvider.class);

}