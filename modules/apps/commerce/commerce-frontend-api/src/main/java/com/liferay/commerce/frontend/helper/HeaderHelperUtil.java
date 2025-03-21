/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.frontend.helper;

import com.liferay.commerce.frontend.model.HeaderActionModel;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.workflow.WorkflowTask;

import jakarta.portlet.PortletURL;

import java.util.List;

import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Alec Sloan
 */
public class HeaderHelperUtil {

	public static WorkflowTask getReviewWorkflowTask(
			long companyId, long userId, long beanId, String className)
		throws PortalException {

		HeaderHelper headerHelper = _serviceTracker.getService();

		return headerHelper.getReviewWorkflowTask(
			companyId, userId, beanId, className);
	}

	public static List<HeaderActionModel>
			getWorkflowTransitionHeaderActionModels(
				long userId, long companyId, String className, long beanId,
				PortletURL transitionPortletURL)
		throws PortalException {

		HeaderHelper headerHelper = _serviceTracker.getService();

		return headerHelper.getWorkflowTransitionHeaderActionModels(
			userId, companyId, className, beanId, transitionPortletURL);
	}

	private static final ServiceTracker<?, HeaderHelper> _serviceTracker =
		ServiceTrackerFactory.open(
			FrameworkUtil.getBundle(HeaderHelperUtil.class),
			HeaderHelper.class);

}